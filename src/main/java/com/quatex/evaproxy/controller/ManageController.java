package com.quatex.evaproxy.controller;

import com.quatex.evaproxy.Store;
import com.quatex.evaproxy.service.KeitaroService;
import com.quatex.evaproxy.service.ManageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua_parser.Client;
import ua_parser.Parser;

import javax.servlet.http.HttpServletRequest;
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
    public ResponseEntity<String> changeEnabledStatus(@RequestParam(required = false) boolean newVersion,
                                                      @RequestParam("enabled") Integer enabled) {
        manageService.updateEnabled(newVersion, enabled);
        return ResponseEntity.ok("Enabled status set");
    }

    @PutMapping("link")
    public ResponseEntity<String> changeLinkStatus(@RequestParam(required = false) boolean newVersion,
                                                   @RequestParam("link") String link) {
        manageService.updateLink(newVersion, link);
        return ResponseEntity.ok("success");
    }

    @GetMapping("link")
    public ResponseEntity<String> getLink(@RequestParam(defaultValue = "1") Integer version) {
        return ResponseEntity.ok(manageService.getLink(version));
    }

    @PutMapping("linkPay")
    public ResponseEntity<String> changeLinkPay(@RequestParam("link") String link) {
        return ResponseEntity.ok(manageService.updateLinkCryptoPay(link));
    }

    @GetMapping("linkPay")
    public ResponseEntity<String> getLinkPay() {
        return ResponseEntity.ok(manageService.getLinkCryptoPay());
    }

    @GetMapping("enabled")
    public ResponseEntity<Integer> enabled(@RequestParam(defaultValue = "1") Integer version) {
        return ResponseEntity.ok(manageService.getEnabled(version));
    }

    @GetMapping("status")
    public Integer defineDevice(@RequestParam(defaultValue = "1") Integer version,
                                @RequestParam(required = false, defaultValue = "iphone") String model,
                                HttpServletRequest request) {
        String remoteAddr = request.getHeader("X-Forwarded-For");
        if (remoteAddr == null || remoteAddr.equals("")) {
            log.info("X-Forwarded-For is empty");
            remoteAddr = request.getRemoteAddr();
        }

        if (remoteAddr == null) {
            return 0;
        }

        String uaString = request.getHeader("user-agent");
        Client parse = uaParser.parse(uaString);
        String name = parse.os.family;
        String systemVersion = parse.os.major + "." + parse.os.minor;
        String actualModel = parse.device.family;

        log.info("User-agent: Ip: {} ;" +
                " Name: {} ;" +
                " systemVersion: {} ;" +
                " model: {} ;" +
                " actualModel: {} ;", remoteAddr, name, systemVersion, model, actualModel);

        int status = keitaroService.getStatus(remoteAddr, name, systemVersion, model);

        if (status == 1) {
            Integer enabled = manageService.getEnabled(version);
            if (0 == enabled) {
                return 0;
            }
        }
        return status;
    }

    @GetMapping("store")
    public ResponseEntity<Map<String, Object>> getStore() {
        return ResponseEntity.ok(this.store.getStore());
    }

    @PutMapping("changeVersion")
    public ResponseEntity<String> changeVersion(@RequestParam Integer version) {
        manageService.updateVersion(version);
        return ResponseEntity.ok("success");
    }
}
