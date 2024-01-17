package com.singularity.prediction.service;

import com.singularity.base.utils.ImageUtils;
import com.singularity.prediction.dto.NumberImageBase64StrPredictionReqDto;
import com.singularity.prediction.dto.NumberImageFilesPredictionReqDto;
import com.singularity.prediction.dto.NumberImageJsonStrPredictionReqDto;
import com.singularity.prediction.dto.NumberImagePredictionResDto;
import com.singularity.prediction.vo.NumberImagePredictionReqVo;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
public class NumberImagePredictionService extends BasePredictionService {

    private static final int IMAGE_MODEL_CALC_SIZE = 28;
    @Value("${tf-serv.predict.number_image.url-v1}")
    private String tfServingUrl;

    public NumberImagePredictionResDto predict(NumberImageJsonStrPredictionReqDto reqDto) {
        NumberImagePredictionReqVo reqVo = reqDto.toVo(NumberImagePredictionReqVo.class);
        return predict(reqVo);
    }

    public NumberImagePredictionResDto predict(NumberImageFilesPredictionReqDto reqDto) {
        List<MultipartFile> files = reqDto.getFiles();

        final List<List<BigDecimal>> instances = new ArrayList<>();
        files.forEach(f -> {
            try {
                instances.add(ImageUtils.normalization(ImageUtils.resize(f.getInputStream(), IMAGE_MODEL_CALC_SIZE, IMAGE_MODEL_CALC_SIZE)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        NumberImagePredictionReqVo reqVo = new NumberImagePredictionReqVo(instances);
        return predict(reqVo);
    }

    public NumberImagePredictionResDto predict(NumberImageBase64StrPredictionReqDto reqDto) {
        List<BigDecimal> singleInstance = ImageUtils.normalization(ImageUtils.resize(reqDto.getBase64Str(), IMAGE_MODEL_CALC_SIZE, IMAGE_MODEL_CALC_SIZE));

        NumberImagePredictionReqVo reqVo = new NumberImagePredictionReqVo(Collections.singletonList(singleInstance));
        return predict(reqVo);
    }

    private NumberImagePredictionResDto predict(NumberImagePredictionReqVo reqVo) {
        List<String> processedImgs = reqVo.getInstances().stream()
            .map(i -> ImageUtils.toBase64Str(i, IMAGE_MODEL_CALC_SIZE, IMAGE_MODEL_CALC_SIZE))
            .collect(Collectors.toList());

        NumberImagePredictionResDto predict = predict(tfServingUrl, reqVo, NumberImagePredictionResDto.class);
        predict.setProcessedImgs(processedImgs);
        return predict;
    }
}
