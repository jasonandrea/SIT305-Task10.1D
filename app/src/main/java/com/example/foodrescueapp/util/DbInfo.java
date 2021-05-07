package com.example.foodrescueapp.util;

public class DbInfo {
    // Database info
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "food_rescue_db";
    public static final String USER_TABLE_NAME = "users";
    public static final String FOOD_TABLE_NAME = "foods";

    // Table fields info for "users" table
    public static final String USER_ID = "user_id";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";

    // Table fields info for "foods" table
    public static final String FOOD_ID = "food_id";
    public static final String FOOD_NAME = "name";
    public static final String FOOD_DESC = "description";
}
