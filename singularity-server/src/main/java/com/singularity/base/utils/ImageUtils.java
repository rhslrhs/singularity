package com.singularity.base.utils;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    private static final String TRAIN_FILE_PATH = "/dev/train/org/";

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
        BufferedImage originalImage;
        try {
            originalImage = ImageIO.read(is);
            Image resizeImage = originalImage.getScaledInstance(width, height, Image.SCALE_REPLICATE);
            newImage = new BufferedImage(width, height, originalImage.getType());

            Graphics g = newImage.getGraphics();
            g.drawImage(resizeImage, 0, 0, null);
            g.dispose();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            String uuid = UUID.randomUUID().toString();
            String trainFilePath = TRAIN_FILE_PATH + LocalDate.now().format(DateTimeFormatter.ISO_DATE) + uuid;
            ImageIO.write(originalImage, "jpg", new FileOutputStream(trainFilePath + "-1.jpg"));
            ImageIO.write(newImage, "jpg", new FileOutputStream(trainFilePath + "-2.jpg"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

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
                int g = (rgb >> 8) & 0xff;
                int b = rgb & 0xff;
//                log.debug("### c[{},{}] {}/{}/{}/{}", row, col, a, r, g, b);
                int gray = (int)(0.299 * r + 0.587 * g + 0.114 * b);
                imgPixArrs[row][col] = gray;
//                log.debug("### g[{},{}] {}/{}/{}/{}", row, col, a, r, g, b);
            }
        }

        // proc: flat([int][int] -> [int])
        int[] imgPixArr = Arrays.stream(imgPixArrs).flatMapToInt(Arrays::stream).toArray();
//        log.debug("### imgPixArr: ({}) {}", imgPixArr.length, Arrays.toString(imgPixArr));

        List<BigDecimal> results = Arrays.stream(imgPixArr)
            .mapToObj(BigDecimal::new)
            // proc: 정규화 0~255 -> 0.0~1.0
            .map(g -> g.divide(BD_255, MathContext.DECIMAL64))
            // proc: 색반전
//            .map(BigDecimal.ONE::subtract)
            .toList();

//        log.debug("### results: {}", results);
        return results;
    }
    public static String toBase64Str(List<BigDecimal> pixelList, int width, int height) {
        // proc: 0.0~1.0 -> 0~255
        int[] pixelArr = pixelList.stream()
            .mapToInt(px -> px.multiply(BD_255, MathContext.DECIMAL64).setScale(0, RoundingMode.HALF_UP).intValue())
            .toArray();
        log.debug("## 0.0~1.0 -> 0~255: {}", Arrays.toString(pixelArr));
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_INDEXED);
        int idx = 0;
        for (int row = 0; row < width; row++) {
            for (int col = 0; col < height; col++) {
                image.setRGB(col, row, pixelArr[idx++]);
            }
        }
//        WritableRaster raster = (WritableRaster) image.getData();
//        raster.setPixels(0, 0, width, height, pixelArr);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", baos);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "data:image/jpg;base64," + Base64.getEncoder().encodeToString(baos.toByteArray());
//        try {
//            FileOutputStream fos = new FileOutputStream("c:/dev/preced_" + UUID.randomUUID() + ".jpg");
//            ImageIO.write(image, "jpg" ,fos);
//        } catch (Exception e) {
//            throw new RuntimeException();
//        }
//        return null;
    }

    public static void main(String[] args) {
        List<BigDecimal> list = Arrays.asList(new BigDecimal("0.0000"),new BigDecimal("0.0000"),new BigDecimal("0.0000"),new BigDecimal("1.0000"));
        String base64Str = toBase64Str(list, 2, 2);
        System.out.println(base64Str);
    }
}
