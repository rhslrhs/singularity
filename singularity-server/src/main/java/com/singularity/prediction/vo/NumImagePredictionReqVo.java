package com.singularity.prediction.vo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class NumImagePredictionReqVo {
    // { "instances": [[ 28 * 28 ], [ 28 * 28 ], [ 28 * 28 ], [ 28 * 28 ], ...] }
    List<List<BigDecimal>> instances = new ArrayList<>();
}