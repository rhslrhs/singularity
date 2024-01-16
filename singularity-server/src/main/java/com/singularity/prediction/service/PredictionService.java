package com.singularity.prediction.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.singularity.base.utils.JsonUtils;
import com.singularity.prediction.dto.NumImagePredictionReqDto;
import com.singularity.prediction.dto.NumImagePredictionResDto;
import com.singularity.prediction.vo.NumImagePredictionReqVo;
import com.singularity.prediction.vo.NumImagePredictionResVo;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;

@Service
@Slf4j
public class PredictionService {

    private static final TypeReference<NumImagePredictionReqVo> TR = new TypeReference<>() {};
    private static final int MODEL_TARGET_XY = 28;


    @Value("${ml.predict.url_v1}")
    private String mlPredictUrl;

    public NumImagePredictionResDto predict(NumImagePredictionReqDto reqDto) {
        List<MultipartFile> files = reqDto.getFiles();
        NumImagePredictionReqVo reqVo = new NumImagePredictionReqVo();
        files.forEach(f -> {
            List<BigDecimal> res = null;
            try {
                res = imageNormalization(f.getInputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
           reqVo.getInstances().add(res);
        });

        return predict(reqVo);
    }
    public NumImagePredictionResDto predict(InputStream is) {
        NumImagePredictionReqVo reqVo = new NumImagePredictionReqVo();
        reqVo.getInstances().add(imageNormalization(is));
        return predict(reqVo);
    }


    private static List<BigDecimal> imageNormalization(InputStream is) {
        BufferedImage originalImage;
        BufferedImage newImage;
        try {
            originalImage = ImageIO.read(is);

            Image resizeImage = originalImage.getScaledInstance(MODEL_TARGET_XY, MODEL_TARGET_XY, Image.SCALE_SMOOTH);
            newImage = new BufferedImage(MODEL_TARGET_XY, MODEL_TARGET_XY, originalImage.getType());

            Graphics g = newImage.getGraphics();
            g.drawImage(resizeImage, 0, 0, null);
            g.dispose();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        File file = new File("/Users/jingon/tmp_resize/resize_" + UUID.randomUUID() + ".jpg");
        try {
            ImageIO.write(newImage, "png", file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        BufferedImage bi;
        int[] flatResult;
        try {
            bi = ImageIO.read(file);
            int[][] result = new int[MODEL_TARGET_XY][MODEL_TARGET_XY];
            for (int row = 0; row < MODEL_TARGET_XY; row++) {
                for (int col = 0; col < MODEL_TARGET_XY; col++) {
                    int rgb = bi.getRGB(col, row);
//                        result[row][col] = rgb;
                    int a = (rgb >> 24) & 0xff;
                    int r = (rgb >> 16) & 0xff;
                    int g = (rgb >> 8) & 0xff;
                    int b = rgb & 0xff;
//                        log.debug("### c[{},{}] {}/{}/{}/{}", row, col, a, r, b, g);
//                        r = g = b = (int)(0.299 * r + 0.587 * g + 0.114 * b);
                    int gray = (int)(0.299 * r + 0.587 * g + 0.114 * b);
                    result[row][col] = gray;
//                    log.debug("### g[{},{}] {}/{}/{}/{}", row, col, a, r, b, g);
                }
            }
            flatResult = Arrays.stream(result).flatMapToInt(Arrays::stream).toArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        log.debug("### flatResult: ({}) {}", flatResult.length, Arrays.toString(flatResult));

        BigDecimal bd255 = new BigDecimal(255);
        List<BigDecimal> res = Arrays.stream(flatResult)
            // 정규화
            .mapToObj(g -> new BigDecimal(g).divide(bd255, 8, RoundingMode.HALF_DOWN))
            // 색반전
            .map(BigDecimal.ONE::subtract)
            .toList();
//            Arrays.stream(flatResult).map()
        log.debug("### res: {}", res);
        return res;
    }

    public NumImagePredictionResDto predict(NumImagePredictionReqVo reqVo) {
        RequestBodyUriSpec uriSpec = WebClient.builder()
            .baseUrl(mlPredictUrl)
            .build()
            .post();
        NumImagePredictionResVo resVo = addHeaders(uriSpec, new HashMap<>())
            .body(BodyInserters.fromValue(reqVo))
            .retrieve()
            .bodyToMono(NumImagePredictionResVo.class)
            .block();

        assert resVo != null;
        log.debug("### result-max: {}", resVo.getResultMax());

        return resVo.toDto(NumImagePredictionResDto.class);
    }

    private static RequestBodyUriSpec addHeaders(RequestBodyUriSpec uriSpec, Map<String, String> headers) {
        for (String header : headers.keySet()) {
            uriSpec.header(header, headers.get(header));
        }
        return uriSpec;
    }
}
