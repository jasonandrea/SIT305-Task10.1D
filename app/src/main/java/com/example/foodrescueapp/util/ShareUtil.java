package com.example.foodrescueapp.util;

import com.example.foodrescueapp.model.Food;

public class ShareUtil {
    // Key for subject text when sharing
    public static final String SHARE_SUBJECT = "Free food available: ";

    // Key for body text when sharing
    public static final String SHARE_BODY_HEAD = "Hello there! did you know that there is a yummy free food you can claim?";
    public static final String SHARE_BODY_FOOD_NAME = "Food: ";
    public static final String SHARE_BODY_FOOD_DATE = "Available on: ";
    public static final String SHARE_BODY_FOOD_PICK_UP_TIMES = "Pick up times: ";
    public static final String SHARE_BODY_FOOD_QUANTITY = "Available quantity: ";
    public static final String SHARE_BODY_FOOD_LOCATION = "Location: ";
    public static final String SHARE_BODY_CLOSE = "Hurry up and get yours!";

    // Method for returning the share body text. Accepts food object as the food to be shared
    public static String getShareText(Food food) {
        String share_text =
                ShareUtil.SHARE_BODY_HEAD + "\n" +
                ShareUtil.SHARE_BODY_FOOD_NAME + food.getName() + "\n" +
                ShareUtil.SHARE_BODY_FOOD_DATE + food.getDate() + "\n" +
                ShareUtil.SHARE_BODY_FOOD_PICK_UP_TIMES + food.getPickUpTimes() + "\n" +
                ShareUtil.SHARE_BODY_FOOD_QUANTITY + food.getQuantity() + "\n" +
                ShareUtil.SHARE_BODY_FOOD_LOCATION + food.getLocation() + "\n" +
                ShareUtil.SHARE_BODY_CLOSE;

        return share_text;
    }
}
