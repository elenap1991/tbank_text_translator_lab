package com.pirogova.translator.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserTranslateRequestDto {

    private String sourceLang;

    private String targetLang;

    private String sourceText;
}
