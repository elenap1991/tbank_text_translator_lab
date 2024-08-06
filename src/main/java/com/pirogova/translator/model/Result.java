package com.pirogova.translator.model;

import lombok.Data;

@Data
public class Result {
    private final int id;
    private final String ip;
    private final String inputText;
    private final String result;
}

