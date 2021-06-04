package com.example.foodrescueapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodrescueapp.data.DatabaseHelper;
import com.example.foodrescueapp.model.Food;
import com.example.foodrescueapp.model.User;
import com.example.foodrescueapp.util.Keys;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;

public class FoodDetailsActivity extends AppCompatActivity {
    // Constants
    public static final int PAYPAL_REQUEST_CODE = 23415;
    private static final double PRICE_PER_FOOD = 9.99;

    private Intent intent;
    private User user;
    private Food food;

    // Declaring and initialising immediately PayPalConfiguration
    private static PayPalConfiguration ppConfig = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX).clientId(Keys.PAYPAL_CLIENT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_details);

        // Get intent and get food object from the intent
        intent = getIntent();
        user = intent.getParcelableExtra(Keys.USER_KEY);
        byte[] imageBlob = intent.getByteArrayExtra(Keys.FOOD_IMAGE_BLOB);
        String[] foodStringDetails = intent.getStringArrayExtra(Keys.FOOD_STRING_DETAILS);

        // Convert imageBlob to bitmap then finally create food object
        Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageBlob, 0, imageBlob.length);
        food = new Food(
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

        // Start the PayPal service
        Intent ppService = new Intent(this, PayPalService.class);
        ppService.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, ppConfig);
        startService(ppService);
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(FoodDetailsActivity.this, PayPalService.class));
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYPAL_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                if (confirmation != null) {
                    try {
                        // Get the payment details information from JSON file and store it in a string variable
                        String paymentDetails = confirmation.toJSONObject().toString(4);

                        // Create an intent that will start a new activity and show the payment details
                        Intent finaliseIntent = new Intent(FoodDetailsActivity.this, PayActivity.class);
                        finaliseIntent.putExtra(Keys.PAYPAL_PAYMENT_DETAILS_KEY, paymentDetails);
                        finaliseIntent.putExtra(Keys.PAYPAL_AMOUNT_KEY, PRICE_PER_FOOD);
                        startActivity(finaliseIntent);
                    } catch (JSONException e) {
                        Toast.makeText(FoodDetailsActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(FoodDetailsActivity.this, "Canceled", Toast.LENGTH_SHORT).show();
                }
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Toast.makeText(FoodDetailsActivity.this, "Invalid payment", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // onClick method that will be called when clicking add to cart button
    public void addToCart(View view) {
        DatabaseHelper db = new DatabaseHelper(this);

        // Update the user's cart by adding the food to the cart
        int foodId = intent.getIntExtra(Keys.FOOD_ID_KEY, -9);
        long rowsAffected = db.insertToCart(user, foodId);

        // Alert the user whether the addition to cart was successful
        if (rowsAffected > 0) {
            Toast.makeText(FoodDetailsActivity.this, "Added to cart", Toast.LENGTH_SHORT).show();
            finish();
        }
        else Toast.makeText(FoodDetailsActivity.this, "Failed to add food to cart", Toast.LENGTH_SHORT).show();
    }

    // onClick method that will be called when clicking buy now button
    public void buyNow(View view) {
        PayPalPayment payment = new PayPalPayment(new BigDecimal(String.valueOf(PRICE_PER_FOOD)), "AUD",
                "Payment to FOOD RESCUE", PayPalPayment.PAYMENT_INTENT_SALE);

        // Start payment process by starting a new payment activity, charging 9.99 as it is only one food
        Intent payIntent = new Intent(FoodDetailsActivity.this, PaymentActivity.class);
        payIntent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, ppConfig);
        payIntent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
        startActivityForResult(payIntent, PAYPAL_REQUEST_CODE); // Start the activity
    }
}