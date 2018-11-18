package com.example.peterboncheff.coursework;

public class Location {

    private String nameOfLocation, lastUpdated;
    private double currentTemperature;

    public Location(String nameOfLocation, String lastUpdated, double currentTemperature) {
        this.nameOfLocation = nameOfLocation;
        this.lastUpdated = lastUpdated;
        this.currentTemperature = currentTemperature;
    }

    public String getNameOfLocation() {
        return nameOfLocation;
    }

    public void setNameOfLocation(String nameOfLocation) {
        this.nameOfLocation = nameOfLocation;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public double getCurrentTemperature() {
        return currentTemperature;
    }

    public void setCurrentTemperature(int currentTemperature) {
        this.currentTemperature = currentTemperature;
    }
}
