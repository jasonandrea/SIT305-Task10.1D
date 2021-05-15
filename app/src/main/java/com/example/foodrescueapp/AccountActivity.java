package com.example.foodrescueapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.foodrescueapp.data.DatabaseHelper;
import com.example.foodrescueapp.model.User;
import com.example.foodrescueapp.util.Keys;

import java.util.Arrays;

public class AccountActivity extends AppCompatActivity {
    private DatabaseHelper db;
    private Intent intent;
    private User user;
    private EditText username, email, phone, address, newPassword, oldPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        db = new DatabaseHelper(this);

        // Get intent and get user object from the intent
        intent = getIntent();
        user = intent.getParcelableExtra(Keys.USER_KEY);

        username = findViewById(R.id.newUsernameEditText);
        email = findViewById(R.id.newEmailEditText);
        phone = findViewById(R.id.newPhoneEditText);
        address = findViewById(R.id.newAddressEditText);
        newPassword = findViewById(R.id.newPasswordEditText);
        oldPassword = findViewById(R.id.oldPasswordEditText);

        // Fill all details (except password) to all EditTexts based on current user details
        username.setText(user.getUsername());
        email.setText(user.getEmail());
        phone.setText(user.getPhone());
        address.setText(user.getAddress());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_options, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Do actions based on the option chosen by the user
        switch (item.getItemId()) {
            case R.id.homeOption: // "Home" option chosen
                Intent newHomeIntent = new Intent(AccountActivity.this, HomeActivity.class);
                newHomeIntent.putExtra(Keys.USER_KEY, user);   // Pass user object to intent
                startActivity(newHomeIntent);   // Start new HomeActivity with same user passed to intent
                finish();                       // Finish current activity
                break;
            case R.id.accountOption: // "Account" option chosen
                Intent newAccountIntent = new Intent(AccountActivity.this, AccountActivity.class);
                newAccountIntent.putExtra(Keys.USER_KEY, user);   // Pass user object to intent
                startActivity(newAccountIntent);    // Start new HomeActivity with same user passed to intent
                finish();                           // Finish current activity
                break;
            case R.id.myListOption: // "My list" option chosen
                Intent newMyListIntent = new Intent(AccountActivity.this, MyListActivity.class);
                newMyListIntent.putExtra(Keys.USER_KEY, user);   // Pass user object to intent
                startActivity(newMyListIntent);  // Start new HomeActivity with same user passed to intent
                finish();                       // Finish current activity
                break;
            default:
                // This branch should only run when something unexpected is happening
                throw new IllegalStateException("Unexpected value: " + item.getItemId());
        }
        return super.onOptionsItemSelected(item);
    }

    // onClick method that will be called on clicking saveDetailsButton to change user details
    public void saveNewDetails(View view) {
        // Variable that stores the id of current user
        int id = user.getUserId();

        // Array containing all user info (username, email, etc...) without id because id is integer
        String[] userInfo = new String[] {
                username.getText().toString(),
                email.getText().toString(),
                phone.getText().toString(),
                address.getText().toString(),
                user.getPassword(),             // Password is not going to be changed
                user.getFoodList()              // FoodList is not going to be changed
        };

        // Check whether there is a blank EditText
        if (Arrays.asList(userInfo).contains(""))  // If there is at least 1 blank input
            Toast.makeText(AccountActivity.this, "Please complete all account details", Toast.LENGTH_SHORT).show();
        else {
            // Create new User object that has new details. This will be passed to parameter later
            User newUser = new User(
                    id,             // User id
                    userInfo[0],    // Username
                    userInfo[1],    // Email
                    userInfo[2],    // Phone
                    userInfo[3],    // Address
                    userInfo[4],    // Password
                    userInfo[5]     // Food list
            );

            // Check whether the update was successful
            if (db.updateUser(newUser)) {
                Toast.makeText(AccountActivity.this, "Account updated", Toast.LENGTH_SHORT).show();
                finish();   // Close current activity
            }
            else Toast.makeText(AccountActivity.this, "Error: failed to update account", Toast.LENGTH_SHORT).show();
        }
    }

    // onClick method that will be called on clicking changePasswordButton to change user password
    public void changePassword(View view) {
        // Variable that stores the id of current user
        int id = user.getUserId();

        // Array containing all user info (username, email, etc...) without id because id is integer
        String[] userInfo = new String[] {
                user.getUsername(),                 // Everything except password is not going to change
                user.getEmail(),
                user.getPhone(),
                user.getAddress(),
                newPassword.getText().toString(),   // Only password is going to change based on user input
                user.getFoodList()
        };

        // Check if new password is blank (userInfo[4] is new password)
        if (userInfo[4].equals(""))
            Toast.makeText(AccountActivity.this, "Please enter the new password", Toast.LENGTH_SHORT).show();
        // Check if the old password matches with the password stored in the user table
        else if (!oldPassword.getText().toString().equals(user.getPassword()))
            Toast.makeText(AccountActivity.this, "Incorrect current/old password", Toast.LENGTH_SHORT).show();
        // If the code made it here, that means new password is not blank and old password is correct
        else {
            // Create new User object that has new details. This will be passed to parameter later
            User newUser = new User(
                    id,             // User id
                    userInfo[0],    // Username
                    userInfo[1],    // Email
                    userInfo[2],    // Phone
                    userInfo[3],    // Address
                    userInfo[4],    // Password
                    userInfo[5]     // Food list
            );

            // Check whether the update was successful
            if (db.updateUser(newUser)) {
                Toast.makeText(AccountActivity.this, "Password changed", Toast.LENGTH_SHORT).show();
                finish();   // Close current activity
            }
            else Toast.makeText(AccountActivity.this, "Error: failed to change password", Toast.LENGTH_SHORT).show();
        }
    }
}