package com.singularity.prediction.vo;

import java.math.BigDecimal;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@RequiredArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
@Getter
@Setter
@ToString
public class NumberImagePredictionReqVo extends BasePredictionReqVo {
    // { "instances": [[ 28 * 28 ], [ 28 * 28 ], [ 28 * 28 ], [ 28 * 28 ], ...] }
    private final List<List<BigDecimal>> instances;
}