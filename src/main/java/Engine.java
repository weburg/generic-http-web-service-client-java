import java.beans.BeanProperty;
import java.beans.JavaBean;
import java.io.Serializable;

@JavaBean(description = "Used as an example resource and something that can really move a project along")
public class Engine implements Serializable {
    public Engine() {}

    private static final long serialVersionUID = 1L;

    private int id = 0;
    private String name = "";
    private int cylinders = 0;
    private int throttleSetting = 0;

    @BeanProperty(description = "The unique id of the engine")
    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCylinders() {
        return this.cylinders;
    }

    public void setCylinders(int cylinders) {
        this.cylinders = cylinders;
    }

    public int getThrottleSetting() {
        return throttleSetting;
    }

    public void setThrottleSetting(int throttleSetting) {
        this.throttleSetting = throttleSetting;
    }
}