package com.raisedsoftware.model;

import javafx.scene.image.Image;

public class ImageViewModel {
    private Image image;
    private String id;
    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
