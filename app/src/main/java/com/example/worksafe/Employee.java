package com.example.worksafe;

public class Employee {

    String name;
    String role;
    String status; // SAFE / WARNING / DANGER

    public Employee(String name, String role, String status) {
        this.name = name;
        this.role = role;
        this.status = status;
    }
}