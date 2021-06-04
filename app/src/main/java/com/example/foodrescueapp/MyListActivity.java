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
import com.example.foodrescueapp.util.ShareUtil;

import java.util.Arrays;
import java.util.List;

public class MyListActivity extends AppCompatActivity implements FoodsAdapter.OnFoodListener {
    private RecyclerView foodRecyclerView;
    private FoodsAdapter foodsAdapter;
    private Intent intent;
    private User user;
    private List<Food> foods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_list);
        DatabaseHelper db = new DatabaseHelper(this);

        // Get intent and get user object from the intent
        intent = getIntent();
        user = intent.getParcelableExtra(Keys.USER_KEY);

        // Get user's list of food from database and store it to foods (list)
        foods = db.fetchUserFood(user.getFoodList());

        // Setting up foods recycler view
        foodRecyclerView = findViewById(R.id.myListRecyclerView);
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
                Intent newHomeIntent = new Intent(MyListActivity.this, HomeActivity.class);
                newHomeIntent.putExtra(Keys.USER_KEY, user);   // Pass user object to intent
                startActivity(newHomeIntent);   // Start new HomeActivity with same user passed to intent
                finish();                       // Finish current activity
                break;
            case R.id.accountOption: // "Account" option chosen
                Intent newAccountIntent = new Intent(MyListActivity.this, AccountActivity.class);
                newAccountIntent.putExtra(Keys.USER_KEY, user);   // Pass user object to intent
                startActivity(newAccountIntent);    // Start new AccountActivity with same user passed to intent
                finish();                           // Finish current activity
                break;
            case R.id.myListOption: // "My list" option chosen
                Intent newMyListIntent = new Intent(MyListActivity.this, MyListActivity.class);
                newMyListIntent.putExtra(Keys.USER_KEY, user);   // Pass user object to intent
                startActivity(newMyListIntent);  // Start new MyListActivity with same user passed to intent
                finish();                        // Finish current activity
                break;
            case R.id.cartOption: // "Cart" option chosen
                Intent newCartIntent = new Intent(MyListActivity.this, CartActivity.class);
                newCartIntent.putExtra(Keys.USER_KEY, user);    // Pass user object to intent
                startActivity(newCartIntent);   // Start new CartActivity with same user passed to intent
                finish();                       // Finish current activity
                break;
            default:
                // This branch should only run when something unexpected is happening
                throw new IllegalStateException("Unexpected value: " + item.getItemId());
        }
        return super.onOptionsItemSelected(item);
    }

    // Method that will be called when pressing the back button on the phone
    // This is to bring the user back to HomeActivity when pressing back in MyListActivity
    // Without this, the user will be brought back to LoginActivity when pressing back button
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent homeIntent = new Intent(MyListActivity.this, HomeActivity.class);
        homeIntent.putExtra(Keys.USER_KEY, user);
        startActivity(homeIntent);
        finish();
    }

    // onClick method that will be called on clicking myListAddFoodButton
    public void addFood(View view) {
        intent = new Intent(MyListActivity.this, AddFoodActivity.class);
        intent.putExtra(Keys.USER_KEY, user);   // Pass user object to intent
        startActivity(intent);  // Start new AddFoodActivity with the same user id passed to intent
    }

    // Method that is called on clicking an element of foods recycler view
    @Override
    public void onFoodClick(int position) {
        Intent newIntent = new Intent(MyListActivity.this, FoodDetailsActivity.class);

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
        newIntent.putExtra(Keys.FOOD_ID_KEY, foods.get(position).getId());
        newIntent.putExtra(Keys.FOOD_IMAGE_BLOB, imageBlob);
        newIntent.putExtra(Keys.FOOD_STRING_DETAILS, foodDetails);
        newIntent.putExtra(Keys.USER_KEY, user);

        // Start the activity
        startActivity(newIntent);
    }

    public void onShareClick(int position) {
        String SHARE_SUBJECT = ShareUtil.SHARE_SUBJECT + foods.get(position).getName();
        String SHARE_TEXT = ShareUtil.getShareText(foods.get(position));

        // Create new share intent
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, SHARE_SUBJECT);
        shareIntent.putExtra(Intent.EXTRA_TEXT, SHARE_TEXT);

        // Start the share intent
        startActivity(Intent.createChooser(shareIntent, "Share this food"));

        // Add food to user's list, if the food is not there yet
        String newUserFoodList = user.getFoodList();

        // Append added food id to user's food list
        if (newUserFoodList.equals("-1")) newUserFoodList = String.valueOf(foods.get(position).getId());
        else {
            newUserFoodList = newUserFoodList + "," + foods.get(position).getId();
            String[] arrayFoodList = newUserFoodList.split(",");
            if (!Arrays.asList(arrayFoodList).contains(foods.get(position).getId()))
                user.insertFoodToList(foods.get(position).getId());
        }
    }
}