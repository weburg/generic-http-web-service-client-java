package example;

import java.io.File;
import java.io.Serializable;

public class Sound implements Serializable {
    public Sound() {}

    private static final long serialVersionUID = 1L;

    private String name = "";
    private String caption = "";
    private transient File soundFile = new File("");

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

    public File getSoundFile() {
        return this.soundFile;
    }

    public void setSoundFile(File soundFile) {
        this.soundFile = soundFile;
    }
}