package com.singularity.base.utils;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ImageUtils {

    private static final int NORMALIZATION_SCALE = 16;
    private static final BigDecimal BD_255 = new BigDecimal(255);

    public static Image resize(String base64Img, int width, int height) {
        if (base64Img == null || base64Img.isEmpty()) {
            throw new IllegalArgumentException("image str empty");
        }
        if (!base64Img.startsWith("data:image")) {
            throw new IllegalArgumentException("not base64image str");
        }
        if (!base64Img.contains(",")) {
            throw new IllegalArgumentException("not base64image str");
        }

        return resize(new ByteArrayInputStream(Base64.getDecoder().decode(base64Img.split(",")[1])), width, height);
    }
    public static Image resize(InputStream is, int width, int height) {
        BufferedImage newImage;
        try {
            BufferedImage originalImage = ImageIO.read(is);
            Image resizeImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            newImage = new BufferedImage(width, height, originalImage.getType());

            Graphics g = newImage.getGraphics();
            g.drawImage(resizeImage, 0, 0, null);
            g.dispose();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

//        File file = new File("/Users/jingon/tmp_resize/resize_" + UUID.randomUUID() + ".jpg");
//        try {
//            ImageIO.write(newImage, "png", file);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

        return newImage;
    }

    public static List<BigDecimal> normalization(Image image) {
        return normalization((BufferedImage) image);
    }
    public static List<BigDecimal> normalization(BufferedImage image) {
        if (image == null) {
            throw new IllegalArgumentException("image empty");
        }

        int width = image.getWidth(), height = image.getHeight();

        // proc: RGB to Grayscale
        int[][] imgPixArrs = new int[width][height];
        for (int row = 0; row < width; row++) {
            for (int col = 0; col < height; col++) {
                int rgb = image.getRGB(col, row);
                int a = (rgb >> 24) & 0xff;
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> NORMALIZATION_SCALE) & 0xff;
                int b = rgb & 0xff;
//                log.debug("### c[{},{}] {}/{}/{}/{}", row, col, a, r, b, g);
                int gray = (int)(0.299 * r + 0.587 * g + 0.114 * b);
                imgPixArrs[row][col] = gray;
//                log.debug("### g[{},{}] {}/{}/{}/{}", row, col, a, r, b, g);
            }
        }

        // proc: flat([int][int] -> [int])
        int[] imgPixArr = Arrays.stream(imgPixArrs).flatMapToInt(Arrays::stream).toArray();
//        log.debug("### imgPixArr: ({}) {}", imgPixArr.length, Arrays.toString(imgPixArr));

        List<BigDecimal> results = Arrays.stream(imgPixArr)
            .mapToObj(BigDecimal::new)
            // proc: 정규화 0~255 -> 0~1
            .map(g -> g.divide(BD_255, NORMALIZATION_SCALE, RoundingMode.HALF_DOWN))
            // proc: 색반전
            .map(BigDecimal.ONE::subtract)
            .toList();

//        log.debug("### results: {}", results);
        return results;
    }
}
