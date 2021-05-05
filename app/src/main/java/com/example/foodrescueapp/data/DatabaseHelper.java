package com.example.foodrescueapp.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.foodrescueapp.model.User;
import com.example.foodrescueapp.util.DbInfo;

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(@Nullable Context context) {
        super(context, DbInfo.DATABASE_NAME, null, DbInfo.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // String variable to store create table command
        String CREATE_USER_TABLE = "CREATE TABLE " + DbInfo.TABLE_NAME + "(" + DbInfo.USER_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT," + DbInfo.USERNAME + " TEXT," +
                DbInfo.PASSWORD + " TEXT)";

        // Execute the above command
        db.execSQL(CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // String variable to store drop table command
        String DROP_USER_TABLE = "DROP TABLE " + DbInfo.TABLE_NAME;

        // Execute the above command to drop the table
        db.execSQL(DROP_USER_TABLE);

        // Create a new table
        onCreate(db);
    }

    // Method to add a new record to the user table (new user)
    public long insertUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Put username and password to ContentValues
        values.put(DbInfo.USERNAME, user.getUsername());
        values.put(DbInfo.PASSWORD, user.getPassword());

        // Insert new record with values above then close SQLiteDatabase
        long newRowId = db.insert(DbInfo.TABLE_NAME, null, values);
        db.close();

        // Return the new row id
        return newRowId;
    }

    // Method to fetch an user record from the table
    public boolean fetchUser(User user) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(DbInfo.TABLE_NAME, new String[]{ DbInfo.USER_ID },
                DbInfo.USERNAME + "=? AND " + DbInfo.PASSWORD + "=?",
                new String[]{ user.getUsername(), user.getPassword() }, null, null, null);
        int numberOfRows = cursor.getCount();   // Get the number of rows returned by the query above
        db.close();                             // Close SQLiteDatabase to prevent memory leak
        cursor.close();                         // Close cursor object to prevent memory leak

        // If a record is found, return true
        return numberOfRows > 0;
    }
}
