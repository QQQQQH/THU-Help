package com.thu.thuhelp.utils;

public class Deal {
    public String address;
    public double bonus;
    public String description;
    public String title;
    public String phone;
    public String name;
    public String startTime;
    public String endTime;

    public Deal(String address, double bonus, String description, String title, String phone, String name, String startTime, String endTime) {
        this.address = address;
        this.bonus = bonus;
        this.description = description;
        this.title = title;
        this.phone = phone;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
