package com.example.foodrescueapp.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodrescueapp.R;
import com.example.foodrescueapp.model.Food;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    // Instance variables
    private List<Food> foods;
    private Context context;

    // Constructor
    public CartAdapter(List<Food> foods, Context context) {
        this.foods = foods;
        this.context = context;
    }

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(this.context).inflate(R.layout.cart, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, int position) {
        holder.foodTitle.setText(this.foods.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return this.foods.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // Instance variable
        private TextView foodTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            foodTitle = itemView.findViewById(R.id.cartFoodNameTextView);
        }
    }
}
