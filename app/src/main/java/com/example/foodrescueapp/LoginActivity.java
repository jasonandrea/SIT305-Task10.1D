package com.example.foodrescueapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.foodrescueapp.data.DatabaseHelper;
import com.example.foodrescueapp.model.User;
import com.example.foodrescueapp.util.Keys;

public class LoginActivity extends AppCompatActivity {
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = new DatabaseHelper(this);
        EditText usernameEditText = findViewById(R.id.loginUsernameEditText);
        EditText passwordEditText = findViewById(R.id.loginPasswordEditText);
        Button loginButton = findViewById(R.id.loginButton);
        Button signupButton = findViewById(R.id.signupButton);

        // OnClickListener for the login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                User user = new User(username, password);

                if (username.equals("") || password.equals(""))
                    Toast.makeText(LoginActivity.this, "Please complete the form", Toast.LENGTH_SHORT).show();
                else {
                    if (db.checkLoginDetails(user)) {
                        Toast.makeText(LoginActivity.this, "logged in successfully", Toast.LENGTH_SHORT).show();

                        // Start next activity (HomeActivity)
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        intent.putExtra(Keys.USER_KEY, db.getUser(username, password));
                        startActivity(intent);
                    } else
                        Toast.makeText(LoginActivity.this, "Incorrect username/password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // OnClickListener for the sign up button
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    // Method that will be called when pressing the back button on the phone
    // This is to make sure than pressing back in the login screen will close the app
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent quitIntent = new Intent(Intent.ACTION_MAIN);
        quitIntent.addCategory(Intent.CATEGORY_HOME);
        quitIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(quitIntent);
    }
}