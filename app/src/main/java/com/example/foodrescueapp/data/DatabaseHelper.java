package com.example.foodrescueapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.foodrescueapp.model.Food;
import com.example.foodrescueapp.model.User;
import com.example.foodrescueapp.util.DbInfo;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(@Nullable Context context) {
        super(context, DbInfo.DATABASE_NAME, null, DbInfo.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // String variables to store create table command
        String CREATE_USER_TABLE = "CREATE TABLE " + DbInfo.USER_TABLE_NAME + "(" +
                DbInfo.USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DbInfo.USERNAME + " TEXT," +
                DbInfo.PASSWORD + " TEXT," +
                DbInfo.USER_EMAIL + " TEXT," +
                DbInfo.USER_PHONE + " TEXT," +
                DbInfo.USER_ADDRESS + " TEXT," +
                DbInfo.USER_FOOD_LIST + " TEXT)";
        String CREATE_FOOD_TABLE = "CREATE TABLE " + DbInfo.FOOD_TABLE_NAME + "(" +
                DbInfo.FOOD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DbInfo.FOOD_IMAGE + " BLOB," +
                DbInfo.FOOD_NAME + " TEXT," +
                DbInfo.FOOD_DESC + " TEXT," +
                DbInfo.FOOD_DATE + " TEXT," +
                DbInfo.FOOD_PICK_UP_TIMES + " TEXT," +
                DbInfo.FOOD_QUANTITY + " TEXT," +
                DbInfo.FOOD_LOCATION + " TEXT)";

        // Execute the above commands
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_FOOD_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // String variable to store drop table command
        String DROP_TABLES = "DROP TABLE IF EXISTS " + DbInfo.DATABASE_NAME;

        // Execute the above command to drop the tables
        db.execSQL(DROP_TABLES);

        // Create a new tables
        onCreate(db);
    }

    // Method to add a new record to the user table (new user)
    public long insertUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Put username and password to ContentValues
        values.put(DbInfo.USERNAME, user.getUsername());
        values.put(DbInfo.PASSWORD, user.getPassword());
        values.put(DbInfo.USER_EMAIL, user.getEmail());
        values.put(DbInfo.USER_PHONE, user.getPhone());
        values.put(DbInfo.USER_ADDRESS, user.getAddress());
        values.put(DbInfo.USER_FOOD_LIST, user.getFoodList());

        // Insert new record with values above then close SQLiteDatabase
        long newRowId = db.insert(DbInfo.USER_TABLE_NAME, null, values);
        db.close();

        // Return the new row id
        return newRowId;
    }

    // Method to check login details. Return a true if details matches
    public boolean checkLoginDetails(User user) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(DbInfo.USER_TABLE_NAME, new String[]{ DbInfo.USER_ID },
                DbInfo.USERNAME + "=? AND " + DbInfo.PASSWORD + "=?",
                new String[]{ user.getUsername(), user.getPassword() }, null, null, null);
        int numberOfRows = cursor.getCount();   // Get the number of rows returned by the query above
        db.close();                             // Close SQLiteDatabase to prevent memory leak
        cursor.close();                         // Close cursor object to prevent memory leak

        // If a record is found, return true
        return numberOfRows > 0;
    }

    // Method to get an user object specified by id
    public User getUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + DbInfo.FOOD_TABLE_NAME + " WHERE " +
                DbInfo.USERNAME + "=" + username + " AND " + DbInfo.PASSWORD + "=" + password, null);

        int id = cursor.getInt(cursor.getColumnIndex(DbInfo.USER_ID));
        String name = cursor.getString(cursor.getColumnIndex(DbInfo.USERNAME));
        String email = cursor.getString(cursor.getColumnIndex(DbInfo.USER_EMAIL));
        String phone = cursor.getString(cursor.getColumnIndex(DbInfo.USER_PHONE));
        String address = cursor.getString(cursor.getColumnIndex(DbInfo.USER_ADDRESS));
        String pw = cursor.getString(cursor.getColumnIndex(DbInfo.PASSWORD));
        String foodList = cursor.getString(cursor.getColumnIndex(DbInfo.USER_FOOD_LIST));

        db.close();
        cursor.close();

        return new User(id, name, email, phone, address, pw, foodList);
    }

    // Method to add a new record to the food table (new food) and add to the user's list
    public long insertFood(Food food, User user){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues foodValues = new ContentValues();
        ContentValues userValues = new ContentValues();

        // Put food name and description to ContentValues
        foodValues.put(DbInfo.FOOD_IMAGE, food.getImageBlob());
        foodValues.put(DbInfo.FOOD_NAME, food.getName());
        foodValues.put(DbInfo.FOOD_DESC, food.getDesc());
        foodValues.put(DbInfo.FOOD_DATE, food.getDate());
        foodValues.put(DbInfo.FOOD_PICK_UP_TIMES, food.getPickUpTimes());
        foodValues.put(DbInfo.FOOD_QUANTITY, food.getQuantity());
        foodValues.put(DbInfo.FOOD_LOCATION, food.getLocation());

        // Update user's food list in the table
        userValues.put(DbInfo.USER_ID, user.getUserId());
        userValues.put(DbInfo.USERNAME, user.getUsername());
        userValues.put(DbInfo.PASSWORD, user.getPassword());
        userValues.put(DbInfo.USER_EMAIL, user.getEmail());
        userValues.put(DbInfo.USER_PHONE, user.getPhone());
        userValues.put(DbInfo.USER_ADDRESS, user.getAddress());
        userValues.put(DbInfo.USER_FOOD_LIST, user.getFoodList());

        // Insert new record with foodValues and update user record with userValues above then close SQLiteDatabase
        long newRowIdFood = db.insert(DbInfo.FOOD_TABLE_NAME, null, foodValues);
        db.update(DbInfo.USER_TABLE_NAME, userValues, DbInfo.USER_ID + "=?",
                new String[]{ String.valueOf(user.getUserId()) });
        db.close(); // To free up some memory

        // Return the new row id
        return newRowIdFood;
    }

    // Method to fetch all foods from the food table
    public List<Food> fetchAllFood() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Food> foods = new ArrayList<>();   // List to store all foods from the database

        // Query to select all row in the table
        String GET_FOODS_QUERY = "SELECT * FROM " + DbInfo.FOOD_TABLE_NAME;
        Cursor cursor = db.rawQuery(GET_FOODS_QUERY, null);

        // Get and add each row to the list
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                // Get food id, name and description from the table
                int id = cursor.getInt(cursor.getColumnIndex(DbInfo.FOOD_ID));
                byte[] imageBlob = cursor.getBlob(cursor.getColumnIndex(DbInfo.FOOD_IMAGE));
                String name = cursor.getString(cursor.getColumnIndex(DbInfo.FOOD_NAME));
                String desc = cursor.getString(cursor.getColumnIndex(DbInfo.FOOD_DESC));
                String date = cursor.getString(cursor.getColumnIndex(DbInfo.FOOD_DATE));
                String pickUpTimes = cursor.getString(cursor.getColumnIndex(DbInfo.FOOD_PICK_UP_TIMES));
                String quantity = cursor.getString(cursor.getColumnIndex(DbInfo.FOOD_QUANTITY));
                String location = cursor.getString(cursor.getColumnIndex(DbInfo.FOOD_LOCATION));

                // Convert image blob to Bitmap before creating new food object and adding it to list
                Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageBlob, 0, imageBlob.length);

                // Create new Food object and add it to the list
                foods.add(new Food(id, imageBitmap, name, desc, date, pickUpTimes, quantity, location));

                cursor.moveToNext();
            }
        }

        // Free up memory by closing both SQLiteDatabase and Cursor
        cursor.close(); db.close();

        // Return list of foods
        return foods;
    }

    // Method to fetch all foods stored in an user list
    public List<Food> fetchUserFood(String foodList) {
        SQLiteDatabase db = this.getReadableDatabase();
        List<Food> foods = new ArrayList<>();   // List to store foods
        String[] foodIdList = foodList.split(",");
        Cursor cursor = null;

        /*if (foodList != "-1") foodIdList = foodList.split(",");
        else foodIdList*/

        // Query to select all row in the table
        for (int i = 0; i < foodIdList.length; i++) {
            String GET_FOODS_QUERY = "SELECT * FROM " + DbInfo.FOOD_TABLE_NAME + " WHERE " +
                    DbInfo.FOOD_ID + "=" + foodIdList[i];
            cursor = db.rawQuery(GET_FOODS_QUERY, null);

            // Get and add each row to the list
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    // Get food id, name and description from the table
                    int id = cursor.getInt(cursor.getColumnIndex(DbInfo.FOOD_ID));
                    byte[] imageBlob = cursor.getBlob(cursor.getColumnIndex(DbInfo.FOOD_IMAGE));
                    String name = cursor.getString(cursor.getColumnIndex(DbInfo.FOOD_NAME));
                    String desc = cursor.getString(cursor.getColumnIndex(DbInfo.FOOD_DESC));
                    String date = cursor.getString(cursor.getColumnIndex(DbInfo.FOOD_DATE));
                    String pickUpTimes = cursor.getString(cursor.getColumnIndex(DbInfo.FOOD_PICK_UP_TIMES));
                    String quantity = cursor.getString(cursor.getColumnIndex(DbInfo.FOOD_QUANTITY));
                    String location = cursor.getString(cursor.getColumnIndex(DbInfo.FOOD_LOCATION));

                    // Convert image blob to Bitmap before creating new food object and adding it to list
                    Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageBlob, 0, imageBlob.length);

                    // Create new Food object and add it to the list
                    foods.add(new Food(id, imageBitmap, name, desc, date, pickUpTimes, quantity, location));

                    cursor.moveToNext();
                }
            }
        }

        // Free up memory by closing both SQLiteDatabase and Cursor
        cursor.close(); db.close();

        return foods;
    }
}
