package com.example.foodrescueapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.foodrescueapp.model.Food;
import com.example.foodrescueapp.util.Keys;

public class FoodDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_details);

        // Get intent and get food object from the intent
        Intent intent = getIntent();
        byte[] imageBlob = intent.getByteArrayExtra(Keys.FOOD_IMAGE_BLOB);
        String[] foodStringDetails = intent.getStringArrayExtra(Keys.FOOD_STRING_DETAILS);

        // Convert imageBlob to bitmap then finally create food object
        Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageBlob, 0, imageBlob.length);
        Food food = new Food(
                imageBitmap,
                foodStringDetails[0],
                foodStringDetails[1],
                foodStringDetails[2],
                foodStringDetails[3],
                foodStringDetails[4],
                foodStringDetails[5]
        );

        ImageView image = findViewById(R.id.detailsImageView);
        TextView title = findViewById(R.id.detailsNameTextView);
        TextView desc = findViewById(R.id.detailsDescTextView);
        TextView date = findViewById(R.id.detailsDateValueTextView);
        TextView pickUpTimes = findViewById(R.id.detailsPickUpTimesValueTextView);
        TextView quantity = findViewById(R.id.detailsQuantityValueTextView);
        TextView location = findViewById(R.id.detailsLocationValueTextView);

        // Set all food details according to the food object passed to here via intent
        image.setImageBitmap(food.getImage());
        title.setText(food.getName());
        desc.setText(food.getDesc());
        date.setText(food.getDate());
        pickUpTimes.setText(food.getPickUpTimes());
        quantity.setText(food.getQuantity());
        location.setText(food.getLocation());
    }

    // onClick method that will be called when clicking OK button
    public void goBack(View view) {
        // simply close the activity. nothing else
        finish();
    }
}