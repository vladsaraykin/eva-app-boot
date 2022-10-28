package com.quatex.evaproxy.service;

import com.quatex.evaproxy.entity.PromoEntity;
import com.quatex.evaproxy.repository.PromoCodeRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PromoCodeService {

    private final PromoCodeRepository promoCodeRepository;

    public PromoCodeService(PromoCodeRepository promoCodeRepository) {
        this.promoCodeRepository = promoCodeRepository;
    }

    public List<PromoEntity> getAll() {
        return promoCodeRepository.getAll();
    }

    public PromoEntity getByCode(String code) {
        PromoEntity value = promoCodeRepository.getByCode(code);
        if (value == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return value;
    }

    public PromoEntity create(PromoEntity promoEntity) {
        if (StringUtils.isBlank(promoEntity.getCode())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Code not be null");
        }
        PromoEntity value = promoCodeRepository.getByCode(promoEntity.getCode());
        if (value != null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "This code has already exist");
        }
        promoCodeRepository.create(promoEntity);
        return promoEntity;
    }

    public PromoEntity update(PromoEntity promoEntity) {
        if (StringUtils.isBlank(promoEntity.getCode())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Code not be null");
        }
        PromoEntity value = promoCodeRepository.getByCode(promoEntity.getCode());
        if (value == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        promoCodeRepository.update(promoEntity);
        return promoEntity;
    }

    public void delete(String code) {
        promoCodeRepository.delete(code);
    }
}
