package example;

import java.io.File;
import java.io.Serializable;

public class Video implements Serializable {
    public Video() {}

    private static final long serialVersionUID = 1L;

    private String name = "";
    private String caption = "";
    private transient File videoFile = new File("");

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

    public File getVideoFile() {
        return this.videoFile;
    }

    public void setVideoFile(File videoFile) {
        this.videoFile = videoFile;
    }
}