package org.example.finostra.Utils.UserDocuments;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;

public class UserDocsChecker {

    private static final int DEFAULT_DPI = 300;

    public static boolean isValidDocument(File imageFile) {
        if (imageFile == null || !imageFile.exists()) {
            System.out.println("Invalid file: The file is either null or doesn't exist.");
            return false;
        }

        try {
            ITesseract tesseract = new Tesseract();
            tesseract.setDatapath("F:\\Java\\Projects\\Finostra\\Finostra\\src\\main\\java\\com\\example\\finostra\\Utils\\UserDocuments");

            tesseract.setConfigs(Collections.singletonList("tessedit_dpi=" + DEFAULT_DPI));

            BufferedImage image = ImageIO.read(imageFile);

            BufferedImage resampledImage = resampleImage(image);
            applyDpiToImage(resampledImage, DEFAULT_DPI);

            String extractedText = tesseract.doOCR(resampledImage).trim();

            return extractedText.length() > 100;
        } catch (TesseractException | IOException e) {
            System.out.println("Error during OCR: " + e.getMessage());
            return false;
        }
    }

    private static BufferedImage resampleImage(BufferedImage image) {
        if (image == null) {
            return null;
        }

        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage resampledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resampledImage.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(image, 0, 0, width, height, null);

        g2d.dispose();

        return resampledImage;
    }

    private static void applyDpiToImage(BufferedImage image, int dpi) {
        try {
            Image scaledImage = image.getScaledInstance(image.getWidth() * dpi / 72, image.getHeight() * dpi / 72, Image.SCALE_DEFAULT);

            BufferedImage bufferedImage = new BufferedImage(scaledImage.getWidth(null), scaledImage.getHeight(null), BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = bufferedImage.createGraphics();
            g2d.drawImage(scaledImage, 0, 0, null);
            g2d.dispose();

            File tempFile = new File("temp_image.png");
            ImageIO.write(bufferedImage, "PNG", tempFile);

            tempFile.setReadable(true, false);
            tempFile.setWritable(true, false);
        } catch (IOException e) {
            System.out.println("Error applying DPI: " + e.getMessage());
        }
    }

    public static File convertMultipartToFile(MultipartFile multipartFile) throws IOException {
        File file = new File(multipartFile.getOriginalFilename());
        multipartFile.transferTo(file);
        return file;
    }
}
