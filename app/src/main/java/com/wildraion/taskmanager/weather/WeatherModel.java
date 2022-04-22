package com.wildraion.taskmanager.weather;

import com.wildraion.taskmanager.weather.model.City;
import com.wildraion.taskmanager.weather.model.List;

public class WeatherModel {

    private int cod;
    private int message;
    private int cnt;
    private java.util.List<List> list;
    private City city;

    public WeatherModel(int cod, int message, int cnt, java.util.List<List> list, City city) {
        this.cod = cod;
        this.message = message;
        this.cnt = cnt;
        this.list = list;
        this.city = city;
    }

    public int getCod() {
        return cod;
    }

    public void setCod(int cod) {
        this.cod = cod;
    }

    public int getMessage() {
        return message;
    }

    public void setMessage(int message) {
        this.message = message;
    }

    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }

    public java.util.List<List> getList() {
        return list;
    }

    public void setList(java.util.List<List> list) {
        this.list = list;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }
}
