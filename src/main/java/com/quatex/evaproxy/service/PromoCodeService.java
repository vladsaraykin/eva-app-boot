package com.quatex.evaproxy.service;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.quatex.evaproxy.entity.PromoCodeEntity;
import com.quatex.evaproxy.repository.PromoCodeRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class PromoCodeService {

    private final PromoCodeRepository promoCodeRepository;

    public PromoCodeService(PromoCodeRepository promoCodeRepository) {
        this.promoCodeRepository = promoCodeRepository;
    }

    public Flux<PromoCodeEntity> getAll() {
        return promoCodeRepository.findAll();
    }

    public Mono<PromoCodeEntity> getByCode(String code) {
        return promoCodeRepository.findByCode(code)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    public Mono<PromoCodeEntity> create(PromoCodeEntity promoCodeEntity) {
        if (StringUtils.isBlank(promoCodeEntity.getCode())) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Code not be null"));
        }
        promoCodeEntity.setId(Uuids.timeBased());
        return promoCodeRepository.findByCode(promoCodeEntity.getCode())
                .<PromoCodeEntity>handle((exist, sink) -> {
                    if (exist != null) {
                        sink.error(new ResponseStatusException(HttpStatus.CONFLICT, "This code has already exist"));
                    }
                })
                .switchIfEmpty(promoCodeRepository.save(promoCodeEntity));
    }

    public Mono<PromoCodeEntity> update(PromoCodeEntity promoCodeEntity) {
        if (StringUtils.isBlank(promoCodeEntity.getCode())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Code not be null");
        }
        return promoCodeRepository.findByCode(promoCodeEntity.getCode())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(code -> promoCodeRepository.save(promoCodeEntity));
    }

    public Mono<Void> delete(UUID uuid) {
        return promoCodeRepository.deleteById(uuid);
    }
}
