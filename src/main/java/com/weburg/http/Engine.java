package com.weburg.http;

import java.io.Serializable;

public class Engine implements Serializable {
    public Engine() {}

    private int id;
    private String name = "Pentastar";
    private int cylinders = 6;
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