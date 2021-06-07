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
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class AddFoodActivity extends AppCompatActivity {
    public static final int IMAGE_GALLERY_REQUEST = 432;
    public static final int AC_REQUEST = 847;

    private DatabaseHelper db;
    private ImageView imagePreview;
    private EditText name, desc, pickUpTimes, quantity, location;
    private CalendarView date;
    private Bitmap image;
    private User user;
    private Intent intent;
    private double lat, lng;

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
        // This will run if everything is running as expected/successfully
        if (resultCode == RESULT_OK) {
            // Branch for IMAGE_GALLERY_REQUEST code
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

            // Branch for AC_REQUEST code
            if (requestCode == AC_REQUEST) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                location.setText(place.getName());          // Set locationEditText text to the name of the location
                lat = place.getLatLng().latitude;           // Set lat to the latitude of the location
                lng = place.getLatLng().longitude;          // Set lng to the longitude of the location
            }
        }
        else if (resultCode == AutocompleteActivity.RESULT_ERROR) { // This will run if there is an error
            // Show error message (toast) if something goes wrong
            Status status = Autocomplete.getStatusFromIntent(data);
            Toast.makeText(AddFoodActivity.this, status.getStatusMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Method that will be called when pressing the back button on the phone
    // This is to bring the user back to NoteListActivity when pressing back in ModifyNoteActivity
    // Without this, the user will be brought back to MainActivity when pressing back button
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent homeIntent = new Intent(AddFoodActivity.this, HomeActivity.class);
        homeIntent.putExtra(Keys.USER_KEY, user);
        startActivity(homeIntent);
        finish();
    }

    // OnClick method that will be called when clicking the location edittext. New intent will be started
    public void openPlacesAutocomplete(View view) {
        // Initialise the Places SDK & create a new Places client instance
        Places.initialize(getApplicationContext(), getString(R.string.google_maps_key));
        PlacesClient pc = Places.createClient(this);

        // Start the auto complete intent
        Intent acIntent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY,
                Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))
                .setCountries(Arrays.asList("AU"))
                .build(this);
        startActivityForResult(acIntent, AC_REQUEST);
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
                    foodStrInfo[5],
                    lat,    // This is a global variable, the value is from AutoComplete places
                    lng     // Same as latitude, this one is for longitude
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