package com.singularity.prediction.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Builder
@Getter
@ToString
public class NumImagePredictionReqDto {
    private List<MultipartFile> files;
}