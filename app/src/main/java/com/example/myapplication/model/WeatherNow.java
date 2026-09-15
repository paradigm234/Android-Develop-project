package com.example.myapplication.model;

/**
 * 实时天气，对应数据库 t_weather_now 表。
 */
public class WeatherNow {

    private final String city;
    private final int temp;
    private final String humidity;
    private final String wind;
    private final String air;

    public WeatherNow(String city, int temp, String humidity, String wind, String air) {
        this.city = city;
        this.temp = temp;
        this.humidity = humidity;
        this.wind = wind;
        this.air = air;
    }

    public String getCity() {
        return city;
    }

    public int getTemp() {
        return temp;
    }

    public String getHumidity() {
        return humidity;
    }

    public String getWind() {
        return wind;
    }

    public String getAir() {
        return air;
    }
}
