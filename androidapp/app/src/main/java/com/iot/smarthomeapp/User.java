package com.iot.smarthomeapp;

public class User {

    public String fullName, phoneNumber, email;

    public User(){
        this.fullName = "";
        this.phoneNumber = "";
        this.email = "";
    }

    public User(String fullName, String phoneNumber, String email){
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }
}
