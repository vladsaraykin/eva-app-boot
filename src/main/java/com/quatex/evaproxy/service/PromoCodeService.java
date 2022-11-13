package com.quatex.evaproxy.service;

import com.quatex.evaproxy.entity.PromoEntity;
import com.quatex.evaproxy.repository.PromoCodeRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class PromoCodeService {

    private final PromoCodeRepository promoCodeRepository;

    public PromoCodeService(PromoCodeRepository promoCodeRepository) {
        this.promoCodeRepository = promoCodeRepository;
    }

    public Flux<PromoEntity> getAll() {
        return promoCodeRepository.getAll();
    }

    public Mono<PromoEntity> getByCode(String code) {
        return promoCodeRepository.getByCode(code)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    public Mono<PromoEntity> create(PromoEntity promoEntity) {
        if (StringUtils.isBlank(promoEntity.getCode())) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Code not be null"));
        }
        return promoCodeRepository.getByCode(promoEntity.getCode())
                .handle((code, sink) -> {
                    if (code != null) {
                        sink.error(new ResponseStatusException(HttpStatus.CONFLICT, "This code has already exist"));
                    }
                    promoCodeRepository.create(promoEntity);
                    sink.next(promoEntity);
        });
    }

    public Mono<PromoEntity> update(PromoEntity promoEntity) {
        if (StringUtils.isBlank(promoEntity.getCode())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Code not be null");
        }
        return promoCodeRepository.getByCode(promoEntity.getCode())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .map(code -> {
                    promoCodeRepository.update(promoEntity);
                    return promoEntity;
                });
    }

    public void delete(String code) {
        promoCodeRepository.delete(code);
    }
}
