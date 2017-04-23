package com.fangyini.yiniweather.model;

import java.io.Serializable;

public class WeatherForecast implements Serializable {

    private long dateTime;
    private float temperatureMin;
    private float temperatureMax;
    private String icon;
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description.substring(0, 1).toUpperCase() + description.substring(1);
    }

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }


    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }


    public float getTemperatureMax() {
        return temperatureMax;
    }

    public void setTemperatureMax(float temperatureMax) {
        this.temperatureMax = (float) (temperatureMax - 273.15);
    }

    public float getTemperatureMin() {
        return temperatureMin;
    }

    public void setTemperatureMin(float temperatureMin) {
        this.temperatureMin = (float) (temperatureMin - 273.15);
    }
}
