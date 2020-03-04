package com.cmput301w20t10.uberapp.activities;

public class History {
    private String date;
    private String price;
    private String driver;

    History(String date, String price, String driver){
        this.date = date;
        this.price = price;
        this.driver = driver;
    }

    String getdate(){

        return this.date;
    }

    void setdate(String new_date){
        this.date = new_date;

    }
    void setPrice(String new_price){
        this.price = new_price;

    }
    String getPrice() {
        return this.price;
    }
    void setDriver(String new_driver){
        this.driver = new_driver;

    }
    String getDriver() {
        return this.driver;
    }
}

