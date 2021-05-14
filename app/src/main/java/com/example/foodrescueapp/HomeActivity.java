package com.example.foodrescueapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.foodrescueapp.data.DatabaseHelper;
import com.example.foodrescueapp.model.Food;
import com.example.foodrescueapp.model.User;
import com.example.foodrescueapp.util.FoodsAdapter;
import com.example.foodrescueapp.util.Keys;

import java.util.List;

public class HomeActivity extends AppCompatActivity implements FoodsAdapter.OnFoodListener {
    private RecyclerView foodRecyclerView;
    private FoodsAdapter foodsAdapter;
    private Intent intent;
    private User user;
    private List<Food> foods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        DatabaseHelper db = new DatabaseHelper(this);

        // Get all foods from database and store it to food (list)
        foods = db.fetchAllFood();

        // Get intent and get user id & food list from the intent
        intent = getIntent();
        user = intent.getParcelableExtra(Keys.USER_KEY);

        // Setting up foods recycler view
        foodRecyclerView = findViewById(R.id.homeFoodRecyclerView);
        foodsAdapter = new FoodsAdapter(foods, this, this);
        foodRecyclerView.setAdapter(foodsAdapter);
        foodRecyclerView.setLayoutManager(new LinearLayoutManager(this));
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
                intent = new Intent(HomeActivity.this, HomeActivity.class);
                intent.putExtra(Keys.USER_KEY, user);   // Pass user object to intent
                startActivity(intent);   // Start new HomeActivity with same user id passed to intent
                finish();                // Finish current activity
                break;
            case R.id.accountOption: // "Account" option chosen
                break;
            case R.id.myListOption: // "My list" option chosen
                intent = new Intent(HomeActivity.this, MyListActivity.class);
                intent.putExtra(Keys.USER_KEY, user);   // Pass user object to intent
                startActivity(intent);  // Start new HomeActivity with same user id passed to intent
                finish();               // Finish current activity
                break;
            default:
                // This branch should only run when something unexpected is happening
                throw new IllegalStateException("Unexpected value: " + item.getItemId());
        }
        return super.onOptionsItemSelected(item);
    }

    // onClick method that will be called on clicking homeAddFoodButton
    public void addFood(View view)
    {
        intent = new Intent(HomeActivity.this, AddFoodActivity.class);
        intent.putExtra(Keys.USER_KEY, user);
        startActivity(intent);  // Start new AddFoodActivity with the same user id passed to intent
    }

    // Method that is called on clicking an element of foods recycler view
    @Override
    public void onFoodClick(int position) {
        // TODO: Start AddFoodActivity maybe
    }
}