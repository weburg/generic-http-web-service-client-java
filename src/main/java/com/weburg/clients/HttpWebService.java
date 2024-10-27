package com.weburg.clients;

import com.weburg.domain.Engine;
import com.weburg.domain.Photo;

import java.util.List;

// Correlates to service class on server
public interface HttpWebService {
    Engine getEngines(int id);

    List<Engine> getEngines();

    int createEngines(Engine engine);

    int createOrReplaceEngines(Engine engine);

    void updateEngines(Engine engine);

    void deleteEngines(int id);

    void restartEngines(int id);

    void stopEngines(int id);

    Photo getPhotos(String name);

    List<Photo> getPhotos();

    String createPhotos(Photo photo);
}
