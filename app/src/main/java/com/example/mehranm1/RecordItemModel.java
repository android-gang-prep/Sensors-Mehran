package com.example.mehranm1;

public class RecordItemModel {
    private float speed;
    private float temp;
    private double lat;
    private double lng;
    private int steps;
    private String img;

    public RecordItemModel(float speed, float temp, double lat, double lng, int steps, String img) {
        this.speed = speed;
        this.temp = temp;
        this.lat = lat;
        this.lng = lng;
        this.steps = steps;
        this.img = img;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getTemp() {
        return temp;
    }

    public void setTemp(float temp) {
        this.temp = temp;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }


    public double getLoc() {
        return lat * lng;
    }
}
