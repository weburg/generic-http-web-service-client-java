package example;

import java.io.Serializable;

public class Engine implements Serializable {
    public Engine() {}

    private static final long serialVersionUID = 1L;

    private int id = 0;
    private String name = "";
    private int cylinders = 0;
    private int throttleSetting = 0;

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