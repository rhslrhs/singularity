package com.singularity.prediction.service;

import com.singularity.prediction.dto.BasePredictionResDto;
import com.singularity.prediction.vo.BasePredictionReqVo;
import com.singularity.prediction.vo.NumberImagePredictionResVo;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;

@Slf4j
public class BasePredictionService {

    public <ResDto extends BasePredictionResDto, ReqVo extends BasePredictionReqVo> ResDto predict(
        String tfServingUrl, ReqVo reqVo, Class<ResDto> resDtoClass
    ) {
        RequestBodyUriSpec uriSpec = WebClient.builder()
            .baseUrl(tfServingUrl)
            .build()
            .post();
        NumberImagePredictionResVo resVo = addHeaders(uriSpec, new HashMap<>())
            .body(BodyInserters.fromValue(reqVo))
            .retrieve()
            .bodyToMono(NumberImagePredictionResVo.class)
            .block();

        assert resVo != null;
        log.debug("### result-max: {}", resVo.getResultMax());

        return resVo.toDto(resDtoClass);
    }

    private static RequestBodyUriSpec addHeaders(RequestBodyUriSpec uriSpec, Map<String, String> headers) {
        for (String header : headers.keySet()) {
            uriSpec.header(header, headers.get(header));
        }
        return uriSpec;
    }
}
