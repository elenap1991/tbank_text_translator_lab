package com.pirogova.translator.dto.external;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ApiRequestDto {
    private String texts;
    private String sourceLanguageCode;
    private String targetLanguageCode;
}

