package com.example.foodrescueapp.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodrescueapp.R;
import com.example.foodrescueapp.model.Food;

import java.util.List;

public class FoodsAdapter extends RecyclerView.Adapter<FoodsAdapter.ViewHolder>{
    // Instance variables
    private List<Food> foods;
    private Context context;
    private OnFoodListener onFoodList;

    // Constructor
    public FoodsAdapter(List<Food> foods, Context context, OnFoodListener listener) {
        this.foods = foods;
        this.context = context;
        this.onFoodList = listener;
    }

    // Methods
    @NonNull
    @Override
    public FoodsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(this.context).inflate(R.layout.food, parent, false);
        return new ViewHolder(itemView, onFoodList);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodsAdapter.ViewHolder holder, int position) {
        holder.thumbnail.setImageBitmap(this.foods.get(position).getImage());
        holder.foodTitle.setText(this.foods.get(position).getName());
        holder.foodDesc.setText(this.foods.get(position).getDesc());
    }

    // Method to return the item count of this.foods (List<Food>)
    @Override
    public int getItemCount() {
        return this.foods.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Instance variables
        private ImageView thumbnail;
        private ImageButton shareButton;
        private TextView foodTitle, foodDesc;
        private OnFoodListener listener;

        // Constructor
        public ViewHolder(@NonNull View itemView, OnFoodListener listener) {
            super(itemView);
            thumbnail = (ImageView)itemView.findViewById(R.id.foodImageView);
            foodTitle = itemView.findViewById(R.id.foodTitleTextView);
            foodDesc = itemView.findViewById(R.id.foodDescTextView);
            shareButton = itemView.findViewById(R.id.shareIcon);
            this.listener = listener;

            itemView.setOnClickListener(this);

            // onClickListener just for the share icon
            shareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onShareClick(getAdapterPosition());
                }
            });
        }

        // onClick method to detect clicks
        @Override
        public void onClick(View view) {
            listener.onFoodClick(getAdapterPosition());
        }
    }

    public interface OnFoodListener {
        // Use this method to send the position of the item when clicked
        void onFoodClick(int position);

        // Use this method to send the position of the item when clicking share button
        void onShareClick(int position);
    }
}
