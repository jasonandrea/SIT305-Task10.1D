package com.example.foodrescueapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.foodrescueapp.data.DatabaseHelper;
import com.example.foodrescueapp.model.User;

import java.util.Arrays;

public class SignUpActivity extends AppCompatActivity {
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        db = new DatabaseHelper(this);
        EditText usernameEditText = findViewById(R.id.signupUsernameEditText);
        EditText emailEditText = findViewById(R.id.signupEmailEditText);
        EditText phoneEditText = findViewById(R.id.signupPhoneEditText);
        EditText addressEditText = findViewById(R.id.signupAddressEditText);
        EditText passwordEditText = findViewById(R.id.signupPasswordEditText);
        EditText confirmPasswordEditText = findViewById(R.id.signupCPasswordEditText);
        Button signupButton = findViewById(R.id.finaliseSignUpButton);

        // OnClickListener for sign up button
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Array containing all user info (username, email, etc...)
                String[] userInfo = new String[] {
                        usernameEditText.getText().toString(),
                        emailEditText.getText().toString(),
                        phoneEditText.getText().toString(),
                        addressEditText.getText().toString(),
                        passwordEditText.getText().toString(),
                        confirmPasswordEditText.getText().toString()
                };

                // Check whether there is a blank EditText
                if (Arrays.asList(userInfo).contains(""))  // If there is at least 1 blank input
                    Toast.makeText(SignUpActivity.this, "Please complete the form", Toast.LENGTH_SHORT).show();
                else {  // This branch runs when there is no blank input
                    // Check whether both password (password & confirm password) are equal
                    if (userInfo[4].equals(userInfo[5])) {  // If both passwords are equal
                        // Insert user with input details to the database
                        long newRowId = db.insertUser(new User(
                                userInfo[0],
                                userInfo[1],
                                userInfo[2],
                                userInfo[3],
                                userInfo[4]
                        ));

                        // Check whether the insertion was successful
                        if (newRowId > 0) {
                            Toast.makeText(SignUpActivity.this, "Registration success", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else Toast.makeText(SignUpActivity.this, "Error: Registration failed", Toast.LENGTH_SHORT).show();
                    }
                    else Toast.makeText(SignUpActivity.this, "Both passwords do not match", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}