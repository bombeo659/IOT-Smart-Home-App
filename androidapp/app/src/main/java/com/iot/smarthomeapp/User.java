package com.iot.smarthomeapp;

public class User {

    public String fullName, phoneNumber, email, password;

    public User(){
        this.fullName = "";
        this.phoneNumber = "";
        this.email = "";
        this.password = "";
    }

    public User(String fullName, String phoneNumber, String email, String password){
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = password;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
