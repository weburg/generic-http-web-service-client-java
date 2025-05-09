import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

public class Photo implements Serializable {
    public Photo() {}

    private static final long serialVersionUID = 1L;

    private String name = "";
    private String caption = "";
    private transient File photoFile = new File("");

    public String getName() {
        return name;
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
        this.name = photoFile.getName();

        try {
            File captionFile = new File(this.photoFile + ".txt");

            if (getCaption().isEmpty() && captionFile.exists() && captionFile.isFile()) {
                setCaption(FileUtils.readFileToString(captionFile));
            }
        } catch (IOException e) {
            // If file didn't exist, then, let caption be what it was
        }
    }
}