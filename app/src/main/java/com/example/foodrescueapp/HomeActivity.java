package com.example.foodrescueapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.accounts.Account;
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

import java.io.ByteArrayOutputStream;
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

        // Get intent and get user object from the intent
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
                Intent newHomeIntent = new Intent(HomeActivity.this, HomeActivity.class);
                newHomeIntent.putExtra(Keys.USER_KEY, user);   // Pass user object to intent
                startActivity(newHomeIntent);   // Start new HomeActivity with same user passed to intent
                finish();                       // Finish current activity
                break;
            case R.id.accountOption: // "Account" option chosen
                Intent newAccountIntent = new Intent(HomeActivity.this, AccountActivity.class);
                newAccountIntent.putExtra(Keys.USER_KEY, user);   // Pass user object to intent
                startActivity(newAccountIntent);    // Start new HomeActivity with same user passed to intent
                finish();                           // Finish current activity
                break;
            case R.id.myListOption: // "My list" option chosen
                Intent newMyListIntent = new Intent(HomeActivity.this, MyListActivity.class);
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

    // onClick method that will be called on clicking homeAddFoodButton
    public void addFood(View view) {
        intent = new Intent(HomeActivity.this, AddFoodActivity.class);
        intent.putExtra(Keys.USER_KEY, user);
        startActivity(intent);  // Start new AddFoodActivity with the same user id passed to intent
    }

    // Method that is called on clicking an element of foods recycler view
    @Override
    public void onFoodClick(int position) {
        Intent newIntent = new Intent(HomeActivity.this, FoodDetailsActivity.class);

        // Food object has bitmap field. Passing a bitmap to a parcelable will cause JAVA BINDER FAILURE
        byte[] imageBlob = foods.get(position).getImageBlob();
        String[] foodDetails = new String[] {
            foods.get(position).getName(),
            foods.get(position).getDesc(),
            foods.get(position).getDate(),
            foods.get(position).getPickUpTimes(),
            foods.get(position).getQuantity(),
            foods.get(position).getLocation()
        };

        // Pass image blob and other details to intent
        newIntent.putExtra(Keys.FOOD_IMAGE_BLOB, imageBlob);
        newIntent.putExtra(Keys.FOOD_STRING_DETAILS, foodDetails);

        // Start the activity
        startActivity(newIntent);
    }
}