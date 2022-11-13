package com.quatex.evaproxy.controller;

import com.quatex.evaproxy.Store;
import com.quatex.evaproxy.service.KeitaroService;
import com.quatex.evaproxy.service.ManageService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ua_parser.Client;
import ua_parser.Parser;

import java.util.Map;

@RestController
public class ManageController {
    Logger log = LoggerFactory.getLogger(ManageController.class);

    private final Parser uaParser = new Parser();
    private final KeitaroService keitaroService;
    private final ManageService manageService;
    private final Store<String, Object> store;

    public ManageController(KeitaroService keitaroService,
                            Store<String, Object> store,
                            ManageService manageService) {
        this.keitaroService = keitaroService;
        this.store = store;
        this.manageService = manageService;
    }


    @PutMapping("enabled")
    public Mono<String> changeEnabledStatus(@RequestParam(required = false) boolean newVersion,
                                                      @RequestParam("enabled") Integer enabled) {
        manageService.updateEnabled(newVersion, enabled);
        return Mono.just("Enabled status set");
    }

    @PutMapping("link")
    public Mono<String> changeLinkStatus(@RequestParam(required = false) boolean newVersion,
                                                   @RequestParam("link") String link) {
        manageService.updateLink(newVersion, link);
        return Mono.just("success");
    }

    @GetMapping("link")
    public Mono<String> getLink(@RequestParam(defaultValue = "1") Integer version) {
        return manageService.getLink(version);
    }

    @PutMapping("linkPay")
    public Mono<String> changeLinkPay(@RequestParam("link") String link) {
        return Mono.just(manageService.updateLinkCryptoPay(link));
    }

    @GetMapping("linkPay")
    public Mono<String> getLinkPay() {
        return manageService.getLinkCryptoPay();
    }

    @GetMapping("enabled")
    public Mono<Integer> enabled(@RequestParam(defaultValue = "1") Integer version) {
        return manageService.getEnabled(version);
    }

    @GetMapping("status")
    public Mono<Integer> defineDevice(@RequestParam(defaultValue = "1") Integer version,
                                      @RequestParam(required = false, defaultValue = "iphone") String model,
                                      ServerHttpRequest request) {
        String remoteAddr = request.getHeaders().getFirst("X-Forwarded-For");
        if (StringUtils.isBlank(remoteAddr)) {
            log.info("X-Forwarded-For is empty");
            remoteAddr = request.getRemoteAddress().getAddress().toString();
        }

        if (remoteAddr == null) {
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
                        }
                    } else {
                        sink.next(keitaroResponse);
                    }
                });
    }

    @GetMapping("store")
    public Mono<Map<String, Object>> getStore() {
        return store.getStore();
    }

    @PutMapping("changeVersion")
    public Mono<String> changeVersion(@RequestParam Integer version) {
        return manageService.updateVersion(version).map(v -> "Success update version: " + version);
    }
}
