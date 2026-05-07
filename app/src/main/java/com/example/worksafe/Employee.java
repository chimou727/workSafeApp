package com.example.worksafe;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Employee {
    public String id;
    public String name;
    public String role;
    public String status;

    // Required for Firestore mapping
    public Employee() {}

    public Employee(String id, String name, String role, String status) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.status = status;
    }
}
