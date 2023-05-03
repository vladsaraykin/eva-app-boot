package com.quatex.evaproxy.controller;

import com.quatex.evaproxy.entity.UserDeviceEntity;
import com.quatex.evaproxy.repository.UserDeviceRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;


@AllArgsConstructor
@RestController
@RequestMapping("userdevice")
public class UserDeviceController {

    private final UserDeviceRepository userDeviceRepository;

    @Operation(summary = "Store user device data")
    @PostMapping("/storeUserData")
    public Mono<UserDeviceEntity> storeUserDeviceData(@RequestParam("device_id") String deviceId,
                                           @RequestParam(value = "onesignalId") String onesignalId) {
        return userDeviceRepository.save(new UserDeviceEntity(deviceId, onesignalId));
    }

    @Operation(summary = "Get user device data")
    @GetMapping("/{deviceId}")
    public Mono<UserDeviceEntity> getUserDeviceData(@PathVariable("deviceId") String deviceId) {
        return userDeviceRepository.findById(deviceId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "DeviceId not found")));
    }
}
