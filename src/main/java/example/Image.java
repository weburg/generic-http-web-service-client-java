package example;

import java.io.File;
import java.io.Serializable;

public class Image implements Serializable {
    public Image() {}

    private static final long serialVersionUID = 1L;

    private String name = "";
    private String caption = "";
    private transient File imageFile = new File("");

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCaption() {
        return this.caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public File getImageFile() {
        return this.imageFile;
    }

    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;
    }
}