package com.example.application.data.entity;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.server.StreamResource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Base64;

public class ImageUtils {

    public String saveImageToFile(File imageFile) {
        // Define the target directory to save the image
        String targetDirectory = "src/files/images/products/";

        // Create the target directory if it doesn't exist
        try {
            Files.createDirectories(Paths.get(targetDirectory));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Generate a unique file name for the image
        String imageFileName = generateUniqueFileName();

        // Create the target file path
        String targetFilePath = targetDirectory + imageFileName;

        // Save the image file to the target directory
        try {
            Files.copy(imageFile.toPath(), Paths.get(targetFilePath), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return imageFileName;
    }

    private static String generateUniqueFileName() {
        // Generate a unique file name based on your requirements
        // You can use a UUID or a timestamp-based approach, or any other method that suits your needs
        // Example:
        String uniqueFileName = "image_" + System.currentTimeMillis() + ".jpg";
        return uniqueFileName;
    }

    public Image renderImage(String Url)
    { String resourcePath = "src/files/images/products/" + Url;
        try {
            File imageFile = new File(resourcePath);
            FileInputStream fileInputStream = new FileInputStream(imageFile);
            StreamResource resource = new StreamResource(Url, () -> fileInputStream);
            Image image = new Image(resource, "Alt Text");
            image.setWidth("20%");
            return image;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return new Image();}

}

