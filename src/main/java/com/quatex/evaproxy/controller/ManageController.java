package com.quatex.evaproxy.controller;

import com.quatex.evaproxy.entity.ManagerSetting;
import com.quatex.evaproxy.service.KeitaroService;
import com.quatex.evaproxy.service.ManageService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ua_parser.Client;
import ua_parser.Parser;

import java.net.InetSocketAddress;
import java.util.Map;

@RestController
public class ManageController {
    Logger log = LoggerFactory.getLogger(ManageController.class);

    private final Parser uaParser = new Parser();
    private final KeitaroService keitaroService;
    private final ManageService manageService;

    public ManageController(KeitaroService keitaroService,
                            ManageService manageService) {
        this.keitaroService = keitaroService;
        this.manageService = manageService;
    }

    @GetMapping("settings")
    public Flux<ManagerSetting> getSettingsNew() {
        return manageService.getSetting();
    }

    @PostMapping("settings")
    public Mono<ManagerSetting> updateSettings(@RequestBody ManagerSetting settingEntity) {
        return manageService.update(settingEntity);
    }

    @GetMapping("link")
    public Mono<String> getLink(@RequestParam(defaultValue = "1") Integer version) {
        return manageService.getLink(version);
    }

    @GetMapping("enabled")
    public Mono<Integer> enabled(@RequestParam(defaultValue = "1") Integer version) {
        return manageService.getEnabled(version);
    }

    @PostMapping("createAlert")
    public Mono<String> createAlert(@RequestBody Map<String, Object> o) {
        return Mono.just("success");
    }

    @GetMapping("status")
    public Mono<Integer> defineDevice(@RequestParam(defaultValue = "1") Integer version,
                                      @RequestParam(required = false, defaultValue = "iphone") String model,
                                      ServerHttpRequest request) {
        String remoteAddr = parseIpClient(request);
        if (remoteAddr == null) {
            log.warn("Remote address is null {}", request.getId());
            return Mono.just(0);
        }

        String uaString = request.getHeaders().getFirst("user-agent");
        Client parse = uaParser.parse(uaString);
        String name = parse.os.family;
        String systemVersion = parse.os.major + "." + parse.os.minor;
        String actualModel = parse.device.family;

        log.info("User-agent: Ip: {} ;" +
                " Name: {} ;" +
                " systemVersion: {} ;" +
                " model: {} ;" +
                " actualModel: {} ;", remoteAddr, name, systemVersion, model, actualModel);

        return keitaroService.getStatus(remoteAddr, name, systemVersion, model)
                .zipWhen(res -> manageService.getEnabled(version))
                .handle((data, sink) -> {
                    final Integer keitaroResponse = data.getT1();
                    final Integer enabled = data.getT2();
                    if (keitaroResponse == 1) {
                        if (0 == enabled) {
                            sink.next(0);
                            return;
                        }
                    }
                    sink.next(keitaroResponse);
                });
    }

    private String parseIpClient(ServerHttpRequest request) {
        String remoteAddr = request.getHeaders().getFirst("X-Forwarded-For");
        if (StringUtils.isBlank(remoteAddr)) {
            log.info("X-Forwarded-For is empty");
            InetSocketAddress remoteAddress = request.getRemoteAddress();
            if (remoteAddress != null) {
                remoteAddr = remoteAddress.getAddress().getHostName();
            }
        }
        if (remoteAddr != null) {
            String[] split = remoteAddr.split(",");
            if (split.length > 1) {
                log.info("Ip client {}:. Take first ", remoteAddr);
                return split[0];
            }
        }
        return remoteAddr;
    }
}
