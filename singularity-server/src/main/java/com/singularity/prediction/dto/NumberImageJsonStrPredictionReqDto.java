package com.singularity.prediction.dto;

import com.singularity.base.dto.BaseReqDto;
import com.singularity.base.utils.JsonUtils;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@Getter
@ToString
public class NumberImageJsonStrPredictionReqDto extends BasePredictionReqDto {

    // { "instances": [[ 28 * 28 ], [ 28 * 28 ], [ 28 * 28 ], [ 28 * 28 ], ...] }
    @NotNull
    private final List<List<BigDecimal>> instances;
}