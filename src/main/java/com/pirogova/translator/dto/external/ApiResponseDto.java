package com.pirogova.translator.dto.external;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApiResponseDto {
    public List<TranslationDto> translations;
}

