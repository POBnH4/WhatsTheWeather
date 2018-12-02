package com.example.peterboncheff.coursework;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.Query;
import android.support.annotation.NonNull;

import java.util.List;
import java.util.Date;
import java.util.List;

@Entity
public class Module {
    @NonNull
    @PrimaryKey(autoGenerate = true)

    private int uid;

    private String reference;

    @ColumnInfo(name = "SCQF_Credits")
    private int scqfCredits;

    private Date createdOn;

    /** Room does not support the ability to store Lists directly, nor the ability to convert to/from Lists.
     *  It supports converting and storing POJO's(Plain old java object). In this case the solution is simple.
     *  Instead of storing a List<String> forecasts I want to store the forecasts in a class */
    public class Forecasts{

        private List<Double> temperatureForecasts;

        private List<String> windSpeedForecasts;

        private List<String> windDirectionForecasts;

        private List<String> airPressureForecasts;

        private List<String> humidityForecasts;

        private List<String> hoursForecast;

        public List<Double> getTemperatureForecasts() {
            return temperatureForecasts;
        }

        public void setTemperatureForecasts(List<Double> temperatureForecasts) {
            this.temperatureForecasts = temperatureForecasts;
        }

        public List<String> getWindSpeedForecasts() {
            return windSpeedForecasts;
        }

        public void setWindSpeedForecasts(List<String> windSpeedForecasts) {
            this.windSpeedForecasts = windSpeedForecasts;
        }

        public List<String> getWindDirectionForecasts() {
            return windDirectionForecasts;
        }

        public void setWindDirectionForecasts(List<String> windDirectionForecasts) {
            this.windDirectionForecasts = windDirectionForecasts;
        }

        public List<String> getAirPressureForecasts() {
            return airPressureForecasts;
        }

        public void setAirPressureForecasts(List<String> airPressureForecasts) {
            this.airPressureForecasts = airPressureForecasts;
        }

        public List<String> getHumidityForecasts() {
            return humidityForecasts;
        }

        public void setHumidityForecasts(List<String> humidityForecasts) {
            this.humidityForecasts = humidityForecasts;
        }

        public List<String> getHoursForecast() {
            return hoursForecast;
        }

        public void setHoursForecast(List<String> hoursForecast) {
            this.hoursForecast = hoursForecast;
        }

    }


    @NonNull
    public int getUid() {
        return uid;
    }

    public void setUid(@NonNull int uid) {
        this.uid = uid;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public int getScqfCredits() {
        return scqfCredits;
    }

    public void setScqfCredits(int scqfCredits) {
        this.scqfCredits = scqfCredits;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }


}
