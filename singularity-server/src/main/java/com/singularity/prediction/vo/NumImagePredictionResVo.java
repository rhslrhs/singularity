package com.singularity.prediction.vo;

import com.singularity.base.utils.JsonUtils;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class NumImagePredictionResVo {
    List<List<BigDecimal>> predictions;

    List<ValIdxPredictionVo> results = new ArrayList<>();
    List<ValIdxPredictionVo> sortedResults = new ArrayList<>();
    private ValIdxPredictionVo maxValIdxPrediction;

    public void setPredictions(List<List<BigDecimal>> predictions) {
        this.predictions = predictions;

        if (!predictions.isEmpty()) {
            AtomicInteger ai = new AtomicInteger(0);
            predictions.get(0).forEach(val -> {
                ValIdxPredictionVo e = new ValIdxPredictionVo(ai.getAndIncrement(), val);
                results.add(e);
                sortedResults.add(e);
            });

            sortedResults.sort(Comparator.comparing(ValIdxPredictionVo::getValue).reversed());
            maxValIdxPrediction = sortedResults.get(0);
        }
    }

    public ValIdxPredictionVo getResultMax() {
        return results.stream()
            .max(Comparator.comparing(ValIdxPredictionVo::getValue))
            .orElseThrow(() -> new RuntimeException("empty"));
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter
    @ToString
    public static class ValIdxPredictionVo {
        private int idx;
        private BigDecimal value;
    }

    public <T> T toDto(Class<T> dtoClass) {
//        return JsonUtils.OM.convertValue(this, dtoClass);
        return JsonUtils.parse(JsonUtils.stringify(this), dtoClass);
    }
}