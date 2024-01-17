package com.singularity.prediction.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Builder
@Getter
@ToString
public class NumberImageFilesPredictionReqDto extends BasePredictionReqDto {
    private List<MultipartFile> files;
}