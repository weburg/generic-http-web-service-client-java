package example;

import com.weburg.ghowst.GenericHttpWebServiceClient;
import com.weburg.ghowst.HttpWebServiceException;

import java.io.File;
import java.util.List;

public class RunExampleGenericHttpWebServiceClient {
    public static void main(String[] args) {
        ExampleService httpWebService = (ExampleService) GenericHttpWebServiceClient
                .newInstance("http://localhost:8081/generichttpws", ExampleService.class);

        /*** example.Image ***/

        // Create
        Image image = new Image();
        image.setCaption("Some Java K");
        image.setImageFile(new File("JAVA.JPG"));
        httpWebService.createImages(image);

        /*** example.Engine ***/

        Engine engine;

        // Create
        engine = new Engine();
        engine.setName("JavaEngine");
        engine.setCylinders(44);
        engine.setThrottleSetting(49);
        int engineId1 = httpWebService.createEngines(engine);

        // CreateOrReplace (which will create)
        engine = new Engine();
        engine.setId(-1);
        engine.setName("JavaEngineCreatedNotReplaced");
        engine.setCylinders(45);
        engine.setThrottleSetting(50);
        httpWebService.createOrReplaceEngines(engine);

        // Prepare for CreateOrReplace
        engine = new Engine();
        engine.setName("JavaEngine2");
        engine.setCylinders(44);
        engine.setThrottleSetting(49);
        int engineId2 = httpWebService.createEngines(engine);

        // CreateOrReplace (which will replace)
        engine = new Engine();
        engine.setId(engineId2);
        engine.setName("JavaEngine2Replacement");
        engine.setCylinders(56);
        engine.setThrottleSetting(59);
        httpWebService.createOrReplaceEngines(engine);

        // Prepare for Update
        engine = new Engine();
        engine.setName("JavaEngine3");
        engine.setCylinders(44);
        engine.setThrottleSetting(49);
        int engineId3 = httpWebService.createEngines(engine);

        // Update
        engine = new Engine();
        engine.setId(engineId3);
        engine.setName("JavaEngine3Updated");
        httpWebService.updateEngines(engine);

        // Get
        engine = httpWebService.getEngines(engineId1);
        System.out.println("example.Engine returned: " + engine.getName());

        // Get all
        List<Engine> engines = httpWebService.getEngines();
        System.out.println("Engines returned: " + engines.size());

        // Prepare for delete
        engine = new Engine();
        engine.setName("JavaEngine4ToDelete");
        engine.setCylinders(89);
        engine.setThrottleSetting(70);
        int engineId4 = httpWebService.createEngines(engine);

        // Delete
        httpWebService.deleteEngines(engineId4);

        // Custom verb
        httpWebService.restartEngines(engineId2);

        // Repeat, complex objects with different names
        Truck truck1 = new Truck();
        truck1.setName("Ram");
        truck1.setEngineId(engineId1);
        Truck truck2 = new Truck();
        truck2.setName("Ford");
        truck2.setEngineId(engineId2);
        String truckResult = httpWebService.raceTrucks(truck1, truck2);

        System.out.println("Race result: " + truckResult);

        // Induce a not found error and catch it
        try {
            engine = httpWebService.getEngines(-2);
            System.out.println("example.Engine returned: " + engine.getName());
        } catch (HttpWebServiceException e) {
            // TODO eventually this will be a subclass e.g. ResourceNotFoundException
            System.out.println("Status: " + e.getHttpStatus() + " Message: " + e.getMessage());
        }

        // Induce a service error and catch it
        try {
            ExampleService httpWebServiceWrong = (ExampleService) GenericHttpWebServiceClient
                    .newInstance("http://nohost:8081/generichttpws", ExampleService.class);
            httpWebServiceWrong.getEngines(-2);
        } catch (HttpWebServiceException e) {
            System.out.println("Status: " + e.getHttpStatus() + " Message: " + e.getMessage());
        }
    }
}