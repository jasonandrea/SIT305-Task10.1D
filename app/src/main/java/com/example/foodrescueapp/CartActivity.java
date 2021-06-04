package com.example.foodrescueapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodrescueapp.data.DatabaseHelper;
import com.example.foodrescueapp.model.Food;
import com.example.foodrescueapp.model.User;
import com.example.foodrescueapp.util.CartAdapter;
import com.example.foodrescueapp.util.FoodsAdapter;
import com.example.foodrescueapp.util.Keys;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    // Constants
    public static final int PAYPAL_REQUEST_CODE = 23415;
    private static final double PRICE_PER_FOOD = 9.99;

    DatabaseHelper db;
    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private Intent intent;
    private User user;
    private List<Food> foods;
    private String totalAmount;
    private TextView totalAmountTV;
    private Button payButton;

    // Declaring and initialising immediately PayPalConfiguration
    private static PayPalConfiguration ppConfig = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX).clientId(Keys.PAYPAL_CLIENT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        db = new DatabaseHelper(this);
        totalAmountTV = findViewById(R.id.totalPriceTextView);
        payButton = findViewById(R.id.payButton);

        // Get intent and get user object from the intent
        intent = getIntent();
        user = intent.getParcelableExtra(Keys.USER_KEY);

        // Get user's cart from database and store it to foods (list)
        foods = db.fetchCart(user.getCart());

        // Setting up foods recycler view
        cartRecyclerView = findViewById(R.id.cartRecyclerView);
        cartAdapter = new CartAdapter(foods, this);
        cartRecyclerView.setAdapter(cartAdapter);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Start the PayPal service
        Intent ppService = new Intent(this, PayPalService.class);
        ppService.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, ppConfig);
        startService(ppService);

        // Initialise totalAmount and then display it
        totalAmount = calculateBill();
        totalAmountTV.setText("$ " + totalAmount.toString());

        // Disable the pay button if the amount is 0.0
        if (totalAmount.equals("0.0")) {
            payButton.setEnabled(false);
            Toast.makeText(CartActivity.this, "You can't make a payment with an empty cart", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(CartActivity.this, PayPalService.class));
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
                        Intent finaliseIntent = new Intent(CartActivity.this, PayActivity.class);
                        finaliseIntent.putExtra(Keys.PAYPAL_PAYMENT_DETAILS_KEY, paymentDetails);
                        finaliseIntent.putExtra(Keys.PAYPAL_AMOUNT_KEY, totalAmount);
                        startActivity(finaliseIntent);
                    } catch (JSONException e) {
                        Toast.makeText(CartActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CartActivity.this, "Canceled", Toast.LENGTH_SHORT).show();
                }
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Toast.makeText(CartActivity.this, "Invalid payment", Toast.LENGTH_SHORT).show();
            }
        }
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
                Intent newHomeIntent = new Intent(CartActivity.this, HomeActivity.class);
                newHomeIntent.putExtra(Keys.USER_KEY, user);   // Pass user object to intent
                startActivity(newHomeIntent);   // Start new HomeActivity with same user passed to intent
                finish();                       // Finish current activity
                break;
            case R.id.accountOption: // "Account" option chosen
                Intent newAccountIntent = new Intent(CartActivity.this, AccountActivity.class);
                newAccountIntent.putExtra(Keys.USER_KEY, user);   // Pass user object to intent
                startActivity(newAccountIntent);    // Start new AccountActivity with same user passed to intent
                finish();                           // Finish current activity
                break;
            case R.id.myListOption: // "My list" option chosen
                Intent newMyListIntent = new Intent(CartActivity.this, MyListActivity.class);
                newMyListIntent.putExtra(Keys.USER_KEY, user);   // Pass user object to intent
                startActivity(newMyListIntent);  // Start new MyListActivity with same user passed to intent
                finish();                        // Finish current activity
                break;
            case R.id.cartOption: // "Cart" option chosen
                Intent newCartIntent = new Intent(CartActivity.this, CartActivity.class);
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
    // This is to bring the user back to HomeActivity when pressing back in this activity
    // Without this, the user will be brought back to LoginActivity when pressing back button
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent homeIntent = new Intent(CartActivity.this, HomeActivity.class);
        homeIntent.putExtra(Keys.USER_KEY, user);
        startActivity(homeIntent);
        finish();
    }

    // onClick method that will be called when clicking the pay now button (blue button)
    public void startPayment(View view) {
        PayPalPayment payment = new PayPalPayment(new BigDecimal(String.valueOf(totalAmount)), "AUD",
                "Payment to FOOD RESCUE", PayPalPayment.PAYMENT_INTENT_SALE);

        Intent payIntent = new Intent(CartActivity.this, PaymentActivity.class);
        payIntent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, ppConfig);
        payIntent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
        startActivityForResult(payIntent, PAYPAL_REQUEST_CODE);
    }

    // onClick method that will be called when clicking the clear cart button to clear the cart
    public void clearCart(View view) {
        // Set the user cart to "-1". Which means empty list (no food in cart)
        user.setCart("-1");

        // Update the new user info to the database
        if (db.updateUser(user)) {  // If successful, tell the user and go back to home screen
            Toast.makeText(CartActivity.this, "Cart cleared", Toast.LENGTH_SHORT).show();
            Intent homeIntent = new Intent(CartActivity.this, HomeActivity.class);
            homeIntent.putExtra(Keys.USER_KEY, user);
            startActivity(homeIntent);
            finish();
        }
        else Toast.makeText(CartActivity.this, "Failed to clear cart", Toast.LENGTH_SHORT).show();
    }

    public String calculateBill() {
        // Calculate the bill by multiplying $9.99 with the number of food
        double amount = PRICE_PER_FOOD * foods.size();

        // Make the calculated amount to string format and then return it
        return "" + amount;
    }
}