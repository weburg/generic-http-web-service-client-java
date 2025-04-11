import java.io.Serializable;

public class Truck implements Serializable {
    public Truck() {}

    private String name = "";
    private Engine engine = new Engine();

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Engine getEngine() {
        return this.engine;
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }
}
