package com.example.worksafe;

public class AlertModel {

    public String type;
    public String message;
    public String employee;
    public long timestamp;

    public AlertModel() {
        // required for Firebase
    }

    public AlertModel(String type, String message, String employee, long timestamp) {
        this.type = type;
        this.message = message;
        this.employee = employee;
        this.timestamp = timestamp;
    }
}