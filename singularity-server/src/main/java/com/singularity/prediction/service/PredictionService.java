package com.singularity.prediction.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.singularity.prediction.dto.NumImagePredictionResDto;
import com.singularity.prediction.vo.NumImagePredictionReqVo;
import com.singularity.prediction.vo.NumImagePredictionResVo;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;

@Service
@Slf4j
public class PredictionService {

    private static final TypeReference<NumImagePredictionReqVo> TR = new TypeReference<>() {};

    @Value("${ml.predict.url_v1}")
    private String mlPredictUrl;

    public NumImagePredictionResDto predict(NumImagePredictionReqVo reqVo) {
        RequestBodyUriSpec uriSpec = WebClient.builder()
            .baseUrl(mlPredictUrl)
            .build()
            .post();
        NumImagePredictionResVo resVo = addHeaders(uriSpec, new HashMap<>())
            .body(BodyInserters.fromValue(reqVo))
            .retrieve()
            .bodyToMono(NumImagePredictionResVo.class)
            .block();

        assert resVo != null;
        log.debug("### result-max: {}", resVo.getResultMax());

        return resVo.toDto(NumImagePredictionResDto.class);
    }

    private static RequestBodyUriSpec addHeaders(RequestBodyUriSpec uriSpec, Map<String, String> headers) {
        for (String header : headers.keySet()) {
            uriSpec.header(header, headers.get(header));
        }
        return uriSpec;
    }

}
