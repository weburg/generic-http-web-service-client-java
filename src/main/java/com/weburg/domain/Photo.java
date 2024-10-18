package com.weburg.domain;

import java.io.File;
import java.io.Serializable;

public class Photo implements Serializable {
    public Photo() {}

    private int id;
    private String caption = "";
    private File photoFile;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCaption() {
        return this.caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public File getPhotoFile() {
        return this.photoFile;
    }

    public void setPhotoFile(File photoFile) {
        this.photoFile = photoFile;
    }
}