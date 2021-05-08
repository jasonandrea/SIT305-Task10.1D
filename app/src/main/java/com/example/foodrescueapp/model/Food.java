package com.example.foodrescueapp.model;

public class Food {
    // Instance variables
    private int id;
    private String name, desc;

    // Constructor
    public Food(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.desc = description;
    }

    public Food(String name, String description) {
        this.name = name;
        this.desc = description;
    }

    // Getters
    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getDesc() {
        return this.desc;
    }
}
