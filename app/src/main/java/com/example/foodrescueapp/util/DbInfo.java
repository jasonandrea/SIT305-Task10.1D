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
    public static final String USER_EMAIL = "email";
    public static final String USER_PHONE = "phone";
    public static final String USER_ADDRESS = "address";
    public static final String USER_FOOD_LIST = "food_list";
    public static final String USER_CART = "saved_cart";

    // Table fields info for "foods" table
    public static final String FOOD_ID = "food_id";
    public static final String FOOD_IMAGE = "image";
    public static final String FOOD_NAME = "name";
    public static final String FOOD_DESC = "description";
    public static final String FOOD_DATE = "date";
    public static final String FOOD_PICK_UP_TIMES = "pick_up_times";
    public static final String FOOD_QUANTITY = "quantity";
    public static final String FOOD_LOCATION = "location";
    public static final String FOOD_LAT = "food_latitude";
    public static final String FOOD_LNG = "food_longitude";
}
