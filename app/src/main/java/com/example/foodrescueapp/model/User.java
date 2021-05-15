package com.example.foodrescueapp.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;

public class User implements Parcelable {
    // Instance variables
    private int userId;
    private String username, password, email, phone, address, foodList;

    // Constructors
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(String username, String email, String phone, String address, String password) {
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.password = password;
        this.foodList = "-1";   // -1 means empty list
    }

    public User(int id, String username, String email, String phone, String address, String password, String foodList) {
        this.userId = id;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.password = password;
        this.foodList = foodList;
    }

    // Methods
    public boolean insertFoodToList(int foodId) {
        if (this.foodList.equals("-1")) {
            this.foodList = String.valueOf(foodId);
            return true;
        }
        else {
            String[] arrayString= this.foodList.split(",");
            if (!Arrays.asList(arrayString).contains(String.valueOf(foodId))) {
                this.foodList = this.foodList + "," + foodId;
                return true;
            }
            else return false;
        }
    }

    // Getters
    public int getUserId() {
        return this.userId;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPhone() {
        return this.phone;
    }

    public String getAddress() {
        return this.address;
    }

    public String getFoodList() {
        return this.foodList;
    }

    // Parcelable stuff
    @Override
    public int describeContents() {
        return 0;
    }

    protected User(Parcel in) {
        userId = in.readInt();
        username = in.readString();
        password = in.readString();
        email = in.readString();
        phone = in.readString();
        address = in.readString();
        foodList = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(userId);
        dest.writeString(username);
        dest.writeString(password);
        dest.writeString(email);
        dest.writeString(phone);
        dest.writeString(address);
        dest.writeString(foodList);
    }
}
