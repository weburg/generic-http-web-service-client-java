import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
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
        this.name = videoFile.getName();

        try {
            File captionFile = new File(videoFile.getAbsolutePath() + ".txt");

            if ((getCaption() == null || getCaption().isEmpty()) && captionFile.exists() && captionFile.isFile()) {
                setCaption(FileUtils.readFileToString(captionFile));
            }
        } catch (IOException e) {
            // If file didn't exist, then, let caption be what it was
        }
    }
}