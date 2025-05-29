package example;

import java.util.List;

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