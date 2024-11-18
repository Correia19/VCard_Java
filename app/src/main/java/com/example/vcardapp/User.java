package com.example.vcardapp;

public class User {

    String token;
    String name;
    Integer phone_number;
    String email;
    String password;
    Integer pin;
    Double balance;
    Double piggy_balance;
    Boolean activeNotifications;
    Boolean activePiggyRoundup;
    Boolean deleted;

    public User() {
    }

    public User(String token, Integer phone_number, String password, Integer pin) {
        this.token = token;
        this.phone_number = phone_number;
        this.password = password;
        this.pin = pin;
        this.balance = 0.0; //mudar para zero e adicionar um charge v-card
        this.piggy_balance = 0.0;
        this.activeNotifications = true;
        this.activePiggyRoundup = false;
        this.deleted = false;
    }

    public User(String token, String name, Integer phone_number, String email, String password, Integer pin) {
        this.token = token;
        this.name = name;
        this.phone_number = phone_number;
        this.email = email;
        this.password = password;
        this.pin = pin;
        this.balance = 0.0; //mudar para zero e adicionar um charge v-card
        this.piggy_balance = 0.0;
        this.activeNotifications = true;
        this.activePiggyRoundup = false;
        this.deleted = false;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(Integer phone_number) {
        this.phone_number = phone_number;
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

    public Integer getPin() {
        return pin;
    }

    public void setPin(Integer pin) {
        this.pin = pin;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Double getPiggy_balance() {
        return piggy_balance;
    }

    public void setPiggy_balance(Double piggy_balance) {
        this.piggy_balance = piggy_balance;
    }

    public Boolean getActiveNotifications() {
        return activeNotifications;
    }

    public void setActiveNotifications(Boolean activeNotifications) {
        this.activeNotifications = activeNotifications;
    }

    public Boolean getActivePiggyRoundup() {
        return activePiggyRoundup;
    }

    public void setActivePiggyRoundup(Boolean activePiggyRoundup) {
        this.activePiggyRoundup = activePiggyRoundup;
    }
    public Boolean getDeleted() {
        return deleted;
    }
    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}
