import java.util.List;

// Correlates to service class on server
public interface HttpWebService {
    Sound getSounds(String name);

    List<Sound> getSounds();

    String createSounds(Sound sound);

    void playSounds(String name);

    Image getImages(String name);

    List<Image> getImages();

    String createImages(Image image);

    Engine getEngines(int id);

    List<Engine> getEngines();

    int createEngines(Engine engine);

    int createOrReplaceEngines(Engine engine);

    int updateEngines(Engine engine);

    void deleteEngines(int id);

    int restartEngines(int id);

    int stopEngines(int id);

    String raceTrucks(Truck truck1, Truck truck2);
}