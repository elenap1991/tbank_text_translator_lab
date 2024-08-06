package com.pirogova.translator.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pirogova.translator.dto.UserTranslateRequestDto;
import com.pirogova.translator.model.Result;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TranslatorService {

    /**
     * @param userTranslateRequestDto пользовательский DTO
     * @param ip
     * @return
     * @throws JsonProcessingException
     */
    String translateText(UserTranslateRequestDto userTranslateRequestDto, String ip)
            throws JsonProcessingException;

    /**
     * @return получить все записи из БД
     */
    List<Result> getAllFromDB();
}

