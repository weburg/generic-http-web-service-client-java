# Generic HTTP Web Service Client in Java (GHoWSt)

## A client written to talk to the Generic HTTP Web Service Server

### Design goals

- Use local language semantics to talk to the server without requiring a 
  compiled client or intermediate language. The only thing required is a Java
  interface for the service, domain object classes, the ghowst classes, and 3rd
  party dependencies from the pom.xml.
- Every call, using a method name convention to map to HTTP methods, gets
  translated to HTTP requests. Responses are parsed from JSON and mapped back to
  local objects.

### Example code

```java
import com.weburg.ghowst.GenericHttpWebServiceClient;
import example.Engine;
import example.ExampleService;

public class RunExampleGenericHttpWebServiceClient {
    public static void main(String[] args) {
        ExampleService httpWebService = (ExampleService) GenericHttpWebServiceClient
                .newInstance("http://localhost:8081/generichttpws", ExampleService.class);
    
        // Create
        Engine engine = new Engine();
        engine.setName("JavaEngine");
        engine.setCylinders(44);
        engine.setThrottleSetting(49);
        int engineId1 = httpWebService.createEngines(engine);
    }
}
```

### Running the example

First, ensure the server is running. Refer to other grouped GHoWSt projects to
get and run the server. Ensure Java JDK 11 or better and Maven 3 or better are
installed. 

If using the CLI, ensure you are in the project directory. Run:

`mvn compile exec:java`

If using an IDE, you should only need to run the below file which it should
compile and run for you:

`src/main/java/RunExampleGenericHttpWebServiceClient.java`

The example runs several calls to create, update, replace, read, delete, and do
a custom action on resources.

### Running the tests

To run unit tests only:

`mvn test`

To run unit and integration tests:

`mvn verify`