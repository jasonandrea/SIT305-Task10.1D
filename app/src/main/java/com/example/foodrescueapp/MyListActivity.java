package com.example.foodrescueapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.foodrescueapp.util.Keys;

public class MyListActivity extends AppCompatActivity {
    private Intent intent;
    private int logged_in_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_list);

        // Get intent and get user id from the intent
        intent = getIntent();
        logged_in_user_id = intent.getIntExtra(Keys.USER_ID_KEY, -1);
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
                intent = new Intent(MyListActivity.this, HomeActivity.class);
                intent.putExtra(Keys.USER_ID_KEY, logged_in_user_id);    // Put user id to intent
                startActivity(intent);  // Start new HomeActivity with same user id passed to intent
                finish();               // Finish current activity
                break;
            case R.id.accountOption: // "Account" option chosen
                break;
            case R.id.myListOption: // "My list" option chosen
                intent = new Intent(MyListActivity.this, MyListActivity.class);
                intent.putExtra(Keys.USER_ID_KEY, logged_in_user_id);   // Put user id to intent
                startActivity(intent);  // Start new HomeActivity with same user id passed to intent
                finish();               // Finish current activity
                break;
            default:
                // This branch should only run when something unexpected is happening
                throw new IllegalStateException("Unexpected value: " + item.getItemId());
        }
        return super.onOptionsItemSelected(item);
    }
}