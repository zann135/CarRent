package com.example.carrent.model;

public class Car {
    private String id;
    private String name;
    private String price;
    private String image;
    private String year;
    private String passenger;
    private String transmission;

    public Car() {
    }

    public Car(String name, String price, String image, String year, String passenger, String transmission) {
        this.name = name;
        this.price = price;
        this.image = image;
        this.year = year;
        this.passenger = passenger;
        this.transmission = transmission;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getPassenger() {
        return passenger;
    }

    public void setPassenger(String passenger) {
        this.passenger = passenger;
    }

    public String getTransmission() {
        return transmission;
    }

    public void setTransmission(String transmission) {
        this.transmission = transmission;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
