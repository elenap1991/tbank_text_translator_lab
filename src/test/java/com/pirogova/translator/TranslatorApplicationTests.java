package com.pirogova.translator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.pirogova.translator.controller.TranslationController;
import com.pirogova.translator.dto.UserTranslateRequestDto;
import com.pirogova.translator.service.TranslatorService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TranslatorApplicationTests {

    @Autowired
    private TranslationController controller;

    @Autowired TranslatorService translatorService;

    @Test
    void contextLoads() throws Exception {
        assertThat(controller).isNotNull();
        assertThat(translatorService).isNotNull();
    }

    @Test
    void TranslationControllerSuccessTest_1() throws JsonProcessingException {
        UserTranslateRequestDto userTranslateRequestDto = new UserTranslateRequestDto("ru", "en", "яблоко");
        String ip = "127.0.0.1";
        String expected = "200 apple";
        String actual = translatorService.translateText(userTranslateRequestDto, ip);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void TranslationControllerSuccessTest_2() throws JsonProcessingException {
        UserTranslateRequestDto userTranslateRequestDto = new UserTranslateRequestDto("ru", "en",
                "Напиток безалкогольный тонизирующий энергетический газированный.Пастеризованный");
        String ip = "127.0.0.1";
        String expected = "200 Drink non- alcoholic tonic energy carbonated.Pasteurized";
        String actual = translatorService.translateText(userTranslateRequestDto, ip);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void TranslationControllerErrorTest_1() throws JsonProcessingException {
        UserTranslateRequestDto userTranslateRequestDto = new UserTranslateRequestDto("ru", "zz",
                "Напиток безалкогольный тонизирующий энергетический газированный.Пастеризованный");
        String ip = "127.0.0.1";
        String expected = "400 BAD_REQUEST unsupported target_language_code: zz";
        String actual = translatorService.translateText(userTranslateRequestDto, ip);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void TranslationControllerErrorTest_2() throws JsonProcessingException {
        UserTranslateRequestDto userTranslateRequestDto = new UserTranslateRequestDto("zz", "en",
                "Напиток безалкогольный тонизирующий энергетический газированный.Пастеризованный");
        String ip = "127.0.0.1";
        String expected = "400 BAD_REQUEST unsupported source_language_code: zz";
        String actual = translatorService.translateText(userTranslateRequestDto, ip);
        Assertions.assertEquals(expected, actual);
    }

}
