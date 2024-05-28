package com.example.rc_chat.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LoadingScreenController {

    @FXML
    private ImageView gif;
    @FXML
    private Label gifLabel;

    private List<ImageView> images = new ArrayList<>();
    private List<String> texts = new ArrayList<>();

    public void initialize() {
        loadImagesFromFolder();
        loadTextsFromFile();

        Random rand = new Random();

        // Set random image to the gif ImageView
        gif.setImage(images.get(rand.nextInt(images.size())).getImage());

        // Set random text to the gifLabel
        gifLabel.setText(texts.get(rand.nextInt(texts.size())));
    }

    private void loadImagesFromFolder() {
        File folder = new File("src/main/resources/Images/LoadingScreenImages");
        if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (isImageFile(file)) {
                        Image image = new Image(file.toURI().toString());
                        ImageView imageView = new ImageView(image);
                        images.add(imageView);
                    }
                }
            }
        }
    }

    private boolean isImageFile(File file) {
        String[] imageExtensions = new String[]{"jpg", "jpeg", "png", "gif", "bmp"};
        String fileName = file.getName().toLowerCase();
        for (String ext : imageExtensions) {
            if (fileName.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    private void loadTextsFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/com/example/rc_chat/LoadingText.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                texts.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}




