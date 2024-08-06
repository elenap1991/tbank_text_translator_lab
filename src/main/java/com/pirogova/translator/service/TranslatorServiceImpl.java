package com.pirogova.translator.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pirogova.translator.config.AppConfig;
import com.pirogova.translator.dto.UserTranslateRequestDto;
import com.pirogova.translator.dto.external.ApiBadRequestDto;
import com.pirogova.translator.dto.external.ApiRequestDto;
import com.pirogova.translator.dto.external.ApiResponseDto;
import com.pirogova.translator.exception.RestTemplateErrorHandler;
import com.pirogova.translator.model.Result;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class TranslatorServiceImpl implements TranslatorService {

    private final JdbcTemplate jdbcTemplate;
    private final AppConfig appConfig;
    private final RestTemplateErrorHandler errorHandler;
    private static final String URL = "https://translate.api.cloud.yandex.net/translate/v2/translate";
    private static final String APIKey = "Api-Key %s";
    private static final String INSERT_INTO_PATTERN = "INSERT INTO requests ('ip', 'input', 'result') VALUES ('%s', '%s', '%s')";
    private static final ObjectMapper mapper = new ObjectMapper();

    public TranslatorServiceImpl(JdbcTemplate jdbcTemplate, AppConfig appConfig,
            RestTemplateErrorHandler errorHandler) {
        this.jdbcTemplate = jdbcTemplate;
        this.appConfig = appConfig;
        this.errorHandler = errorHandler;
    }

    @Override
    @Transactional
    public String translateText(UserTranslateRequestDto userTranslateRequestDto, String ip)
            throws JsonProcessingException {

        StringBuilder result = new StringBuilder();

        ExecutorService executorService = Executors.newFixedThreadPool(appConfig.getMaxThreadCount());
        ConcurrentHashMap<Integer, String> resultMap = new ConcurrentHashMap<>();
        String status = "";
        String[] textArr = userTranslateRequestDto.getSourceText().split("\\s");
        List<Future<ResponseEntity<String>>> futures = new ArrayList<Future<ResponseEntity<String>>>();
        for (int i = 0; i < textArr.length; i++) {
            int finalI = i;

            Callable<ResponseEntity<String>> task = () ->

                    getResponseFromTranslationAPI(userTranslateRequestDto.getSourceLang(),
                            userTranslateRequestDto.getTargetLang(),
                            textArr[finalI]);
            futures.add(executorService.submit(task));
        }
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(appConfig.getTimeoutMilliseconds(), TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
                return "408 request timeouted!";
            }
        } catch (InterruptedException e) {
            System.out.println(String.format("unxpected termination"));
            executorService.shutdownNow();
            return "500 internal server error";
        }

        for (int i = 0; i < textArr.length; i++) {
            Future<ResponseEntity<String>> stringFuture = futures.get(i);
            ResponseEntity<String> response;
            try {
                response = stringFuture.get();
            } catch (ExecutionException | InterruptedException ex) {
                Thread.currentThread().interrupt();
                return "Ошибка доступа к ресурсу перевода";
            }

            if (response.getStatusCodeValue() == 200) {
                ApiResponseDto responseDto = mapper.readValue(response.getBody(), ApiResponseDto.class);
                status = String.valueOf(response.getStatusCodeValue());
                resultMap.put(i, responseDto.getTranslations().get(0).getText());
            } else {
                result.append(response.getStatusCode());
                if (response.getBody() != null) {
                    ApiBadRequestDto apiBadRequestDto = mapper.readValue(response.getBody(), ApiBadRequestDto.class);
                    result.append(" ").append(apiBadRequestDto.getMessage());
                }
                jdbcTemplate
                        .execute(String.format(INSERT_INTO_PATTERN, ip, userTranslateRequestDto.getSourceText(),
                                result));
                return result.toString();
            }
        }

        String resultMapAsString = resultMap.keySet().stream()
                .map(resultMap::get)
                .collect(Collectors.joining(" "));

        result.append(status).append(" ").append(resultMapAsString);

        jdbcTemplate.execute(String.format(INSERT_INTO_PATTERN, ip, userTranslateRequestDto.getSourceText(), result.toString()));

        return result.toString();
    }

    @Override
    public List<Result> getAllFromDB() {
        return jdbcTemplate.query("SELECT * FROM requests",
                (resultSet, rowNum) -> new Result(resultSet.getInt("id"), resultSet.getString("ip"), resultSet.getString("input"),
                        resultSet.getString("result")));
    }

    private ResponseEntity<String> getResponseFromTranslationAPI(String sourceLang, String targetLang, String originalText) {
        ApiRequestDto request = new ApiRequestDto(originalText, sourceLang, targetLang);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", String.format(APIKey, appConfig.getApiKey()));
        HttpEntity<ApiRequestDto> httpRequest = new HttpEntity(request, headers);

        RestTemplate restTemplate = new RestTemplateBuilder().errorHandler(errorHandler).build();
        return restTemplate.exchange(URL, HttpMethod.POST, httpRequest, String.class);
    }
}


