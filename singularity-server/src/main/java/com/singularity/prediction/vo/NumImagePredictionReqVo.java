package com.singularity.prediction.vo;

import java.math.BigDecimal;
import java.util.List;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class NumImagePredictionReqVo {
    // { "instances": [[ 28 * 28 ]] }
    List<List<BigDecimal>> instances;
}