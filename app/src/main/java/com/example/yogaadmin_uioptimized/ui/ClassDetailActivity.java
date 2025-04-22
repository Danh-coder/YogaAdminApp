package com.example.yogaadmin_uioptimized.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yogaadmin_uioptimized.R;

public class ClassDetailActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_detail);

        // Get data from intent
        int classId = getIntent().getIntExtra("classId", -1);
        String daysOfWeek = getIntent().getStringExtra("daysOfWeek");
        String time = getIntent().getStringExtra("time");
        int capacity = getIntent().getIntExtra("capacity", 0);
        int duration = getIntent().getIntExtra("duration", 0);
        double price = getIntent().getDoubleExtra("price", 0.0);
        int classTypeId = getIntent().getIntExtra("classTypeId", 0);
        String description = getIntent().getStringExtra("description");
        String level = getIntent().getStringExtra("level");
        String room = getIntent().getStringExtra("room");

        // Set data to TextViews
        TextView tvClassId = findViewById(R.id.tvClassId);
        TextView tvDaysOfWeek = findViewById(R.id.tvDaysOfWeek);
        TextView tvTime = findViewById(R.id.tvTime);
        TextView tvCapacity = findViewById(R.id.tvCapacity);
        TextView tvDuration = findViewById(R.id.tvDuration);
        TextView tvPrice = findViewById(R.id.tvPrice);
        TextView tvClassTypeId = findViewById(R.id.tvClassTypeId);
        TextView tvDescription = findViewById(R.id.tvDescription);
        TextView tvLevel = findViewById(R.id.tvLevel);
        TextView tvRoom = findViewById(R.id.tvRoom);

        tvClassId.setText("Class ID: " + classId);
        tvDaysOfWeek.setText("Days of Week: " + daysOfWeek);
        tvTime.setText("Time: " + time);
        tvCapacity.setText("Capacity: " + capacity);
        tvDuration.setText("Duration: " + duration);
        tvPrice.setText("Price: " + price);
        tvClassTypeId.setText("Class Type ID: " + classTypeId);
        tvDescription.setText("Description: " + description);
        tvLevel.setText("Level: " + level);
        tvRoom.setText("Room: " + room);
    }
}