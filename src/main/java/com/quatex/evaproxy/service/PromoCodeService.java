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

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PromoCodeService {

    private final PromoCodeRepository promoCodeRepository;
    private final ConcurrentHashMap<String, PromoCodeEntity> cache = new ConcurrentHashMap<>();
    public PromoCodeService(PromoCodeRepository promoCodeRepository) {
        this.promoCodeRepository = promoCodeRepository;
    }

    public Flux<PromoCodeEntity> getAll() {
        return Flux.fromIterable(cache.values())
                .switchIfEmpty(
                        promoCodeRepository.findAll().doOnNext(e -> cache.put(e.getCode(), e))
                );
    }

    public Mono<PromoCodeEntity> getByCode(String code) {
        PromoCodeEntity promoCodeEntity = cache.get(code);
        if (promoCodeEntity == null) {
            return promoCodeRepository.findByCode(code)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .doOnSuccess(e -> cache.put(e.getCode(), e));
        }
        return Mono.just(promoCodeEntity);
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
                .switchIfEmpty(promoCodeRepository.save(promoCodeEntity))
                .doOnSuccess(e -> cache.put(e.getCode(), e));
    }

    public Mono<PromoCodeEntity> update(PromoCodeEntity promoCodeEntity) {
        if (StringUtils.isBlank(promoCodeEntity.getCode())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Code not be null");
        }
        return promoCodeRepository.findByCode(promoCodeEntity.getCode())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(code -> promoCodeRepository.save(promoCodeEntity))
                .doOnSuccess(e -> cache.put(e.getCode(), e));
    }

    public Mono<Void> delete(UUID uuid) {
        return promoCodeRepository.findById(uuid)
                .doOnSuccess(e -> cache.remove(e.getCode()))
                .flatMap(e -> promoCodeRepository.deleteById(uuid));
    }
}
