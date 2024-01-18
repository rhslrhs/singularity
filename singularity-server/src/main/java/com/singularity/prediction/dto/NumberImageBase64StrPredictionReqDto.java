package com.singularity.prediction.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class NumberImageBase64StrPredictionReqDto extends BasePredictionReqDto {
    private String base64Str;
}