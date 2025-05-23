import com.weburg.ghowst.DescriptionInOut;
import jdk.jfr.Description;
import jdk.jfr.Name;

import java.util.List;

@Name("Generic HTTP Web Service")
@Description("An example service showing the power of GHoWSt")
public interface ExampleService {
    Sound getSounds(String name);

    List<Sound> getSounds();

    String createSounds(Sound sound);

    void playSounds(String name);

    Image getImages(String name);

    List<Image> getImages();

    String createImages(Image image);

    Video getVideos(String name);

    List<Video> getVideos();

    String createVideos(Video video);

    @Description("Gets a list of Engines based on the id")
    Engine getEngines(int id);

    List<Engine> getEngines();

    @Description("Create a new engine")
    @DescriptionInOut("The id of the created engine") int createEngines(@DescriptionInOut("The engine to create") Engine engine);

    int createOrReplaceEngines(Engine engine);

    int updateEngines(Engine engine);

    void deleteEngines(int id);

    int restartEngines(int id);

    int stopEngines(int id);

    String raceTrucks(Truck truck1, Truck truck2);
}