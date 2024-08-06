package com.pirogova.translator.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pirogova.translator.dto.UserTranslateRequestDto;
import com.pirogova.translator.model.Result;
import com.pirogova.translator.service.TranslatorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/translator")
public class TranslationController {

    TranslatorService translatorService;

    public TranslationController(TranslatorService translatorService) {
        this.translatorService = translatorService;
    }

    @PostMapping("/translate")
    public String translateText(@RequestBody UserTranslateRequestDto userTranslateRequestDto,
            HttpServletRequest httpServletRequest) throws JsonProcessingException {
        return translatorService
                .translateText(userTranslateRequestDto,
                        httpServletRequest.getRemoteAddr());
    }

    @GetMapping("/getAllFromDB")
    public List<Result> getAllFromDB() {
        return translatorService
                .getAllFromDB();
    }

}

