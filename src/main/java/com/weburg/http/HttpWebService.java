package com.weburg.http;

import java.util.List;

// Correlates to service class com.weburg.services.HttpWebService
public interface HttpWebService {
	Engine getEngines(int id);

	List<Engine> getEngines();

	int createEngines(Engine engine);

	int createOrReplaceEngines(Engine engine);

	void updateEngines(Engine engine);

	void deleteEngines(int id);

	void restartEngines(int id);

	String createPhotos(Photo photo);
}
