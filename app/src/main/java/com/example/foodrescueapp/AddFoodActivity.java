package com.example.foodrescueapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.foodrescueapp.data.DatabaseHelper;
import com.example.foodrescueapp.model.Food;
import com.example.foodrescueapp.model.User;
import com.example.foodrescueapp.util.Keys;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class AddFoodActivity extends AppCompatActivity {
    public static final int IMAGE_GALLERY_REQUEST = 14;
    private DatabaseHelper db;
    private ImageView imagePreview;
    private EditText name, desc, pickUpTimes, quantity, location;
    private CalendarView date;
    private Bitmap image;
    private User user;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);
        db = new DatabaseHelper(this);

        // Get intent and get user object from the intent
        intent = getIntent();
        user = intent.getParcelableExtra(Keys.USER_KEY);

        imagePreview = (ImageView)findViewById(R.id.previewImageView);
        name = findViewById(R.id.newFoodTitleEditText);
        desc = findViewById(R.id.newFoodDescEditText);
        pickUpTimes = findViewById(R.id.newFoodPickUpTimesEditText);
        quantity = findViewById(R.id.newFoodQuantityEditText);
        location = findViewById(R.id.newFoodLocationEditText);
        date = findViewById(R.id.newFoodCalendarView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            // This will run if everything is running as expected/successfully
            if (requestCode == IMAGE_GALLERY_REQUEST){
                Uri imageUri = data.getData();  // The address of the image
                InputStream inputStream;

                try {
                    inputStream = getContentResolver().openInputStream(imageUri);
                    image = BitmapFactory.decodeStream(inputStream);

                    // Change the ImageView to the bitmap
                    imagePreview.setImageBitmap(image);
                }
                catch (FileNotFoundException e) {
                    Toast.makeText(AddFoodActivity.this, "File is not found", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // Method that is called when clicking add image text. Gallery will open and the user
    // will be able to pick an image. Chosen image will be displayed in the activity
    public void getImage(View view) {
        // Implicit intent
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK);
        File pictureDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String path = pictureDir.getPath();
        Uri data = Uri.parse(path);
        
        // Set the data and type
        // This method will accept all image type (png, jpeg, bmp, ..)
        pickPhotoIntent.setDataAndType(data, "image/*");

        // Start the intent and then return back to this activity once taking photo is done
        startActivityForResult(pickPhotoIntent, IMAGE_GALLERY_REQUEST);
    }

    // onClick method for saveFoodButton. Get all values from EditTexts, image from the user
    // and then store it in the database. Blank EditText(s) will not be accepted (will show error toast)
    public void saveNewFood(View view) {
        String[] foodStrInfo;   // String array to store all info except for image bitmap
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy"); // defining the date format in string

        // Get all values from EditTexts and store each values to foodStrInfo array
        foodStrInfo = new String[] {
                name.getText().toString(),
                desc.getText().toString(),
                dateFormat.format(new Date(date.getDate())),
                pickUpTimes.getText().toString(),
                quantity.getText().toString(),
                location.getText().toString()
        };

        // When there is no image, calling getHeight() should throw an error. getHeight() is to check
        // if there is an image selected.
        try {
            image.getHeight();
        }
        catch (Exception e) {
            Toast.makeText(AddFoodActivity.this, "Please add an image of the food", Toast.LENGTH_SHORT).show();
            return;
        }

        if (Arrays.asList(foodStrInfo).contains("")) // Check whether there is a blank EditText
            Toast.makeText(AddFoodActivity.this, "Please complete the form", Toast.LENGTH_SHORT).show();
        else {
            // This will run if the form is complete (no blank EditText and no empty image)
            // Insert new food with input details to the database
            long newRowId = db.insertFood(new Food(
                    image,
                    foodStrInfo[0],
                    foodStrInfo[1],
                    foodStrInfo[2],
                    foodStrInfo[3],
                    foodStrInfo[4],
                    foodStrInfo[5]
            ), user);

            // Check whether the insertion was successful
            if (newRowId > 0) {
                Toast.makeText(AddFoodActivity.this, "Food added", Toast.LENGTH_SHORT).show();
                Intent newIntent = new Intent(AddFoodActivity.this, HomeActivity.class);
                newIntent.putExtra(Keys.USER_KEY, db.getUser(user.getUserId()));
                startActivity(newIntent);
                finish();
            }
            else Toast.makeText(AddFoodActivity.this, "Error: Failed to add food", Toast.LENGTH_SHORT).show();
        }
    }
}