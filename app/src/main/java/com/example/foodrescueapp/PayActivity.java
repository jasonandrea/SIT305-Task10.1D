package com.example.foodrescueapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.foodrescueapp.util.Keys;

import org.json.JSONException;
import org.json.JSONObject;

public class PayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);

        Intent intent = getIntent();
        TextView transactionId = findViewById(R.id.atv);
        TextView transactionState = findViewById(R.id.btv);
        TextView transactionAmount = findViewById(R.id.ctv);

        try {
            JSONObject json = new JSONObject(intent.getStringExtra(Keys.PAYPAL_PAYMENT_DETAILS_KEY));
            JSONObject json2 = json.getJSONObject(Keys.PAYPAL_RESPONSE_KEY);
            String amount = intent.getStringExtra(Keys.PAYPAL_AMOUNT_KEY);

            // Set all text views to display the transaction details
            transactionId.setText(json2.getString("id"));
            transactionState.setText(json2.getString("state"));
            transactionAmount.setText(json2.getString(String.format("$%s", amount)));
        }
        catch (JSONException e) {
            Toast.makeText(PayActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
        }
    }
}