import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
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
        this.name = soundFile.getName();

        try {
            File captionFile = new File(soundFile.getAbsolutePath() + ".txt");

            if ((getCaption() == null || getCaption().isEmpty()) && captionFile.exists() && captionFile.isFile()) {
                setCaption(FileUtils.readFileToString(captionFile));
            }
        } catch (IOException e) {
            // If file didn't exist, then, let caption be what it was
        }
    }
}