package com.example.vcardapp;

public class Login implements java.io.Serializable {

    private Integer number;
    private String password;
    private Integer pin;


    public Login(Integer number, String password, Integer pin) {
        this.number = number;
        this.password = password;
        this.pin = pin;
    }
    public Login() {
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getPin() {
        return pin;
    }

    public void setPin(Integer pin) {
        this.pin = pin;
    }
}
