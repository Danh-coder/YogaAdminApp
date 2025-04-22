package com.example.yogaadmin_uioptimized.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.yogaadmin_uioptimized.ClassInstanceItem;
import com.example.yogaadmin_uioptimized.ClassItem;
import com.example.yogaadmin_uioptimized.R;

import java.util.List;

public class ClassInstanceDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_instance_detail);

        // Get ClassInstanceItem and ClassItem from Intent extras
        ClassInstanceItem classInstanceItem = getIntent().getParcelableExtra("CLASS_INSTANCE_ITEM"); // Will implement Parcelable in next step
        ClassItem classItem = getIntent().getParcelableExtra("CLASS_ITEM"); // Will implement Parcelable in next step

        if (classInstanceItem != null && classItem != null) {
            populateUI(classInstanceItem, classItem);
        } else {
            // Handle error - data not passed correctly
            Toast.makeText(this, "Error: Could not load class instance details", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @SuppressLint("SetTextI18n")
    private void populateUI(ClassInstanceItem classInstanceItem, ClassItem classItem) {
        // Instance Details TextViews
        TextView tvDetailInstanceClassDescription = findViewById(R.id.tvDetailInstanceClassDescription);
        TextView tvDetailInstanceDate = findViewById(R.id.tvDetailInstanceDate);
        TextView tvDetailInstanceTime = findViewById(R.id.tvDetailInstanceTime);
        TextView tvDetailInstanceTeacherName = findViewById(R.id.tvDetailInstanceTeacherName);
        TextView tvDetailInstanceComments = findViewById(R.id.tvDetailInstanceComments);

        // Class Details TextViews
        TextView tvDetailClassDescription = findViewById(R.id.tvDetailClassDescription);
        TextView tvDetailClassDaysOfWeek = findViewById(R.id.tvDetailClassDaysOfWeek);
        TextView tvDetailClassTime = findViewById(R.id.tvDetailClassTime);
        TextView tvDetailClassCapacity = findViewById(R.id.tvDetailClassCapacity);
        TextView tvDetailClassDuration = findViewById(R.id.tvDetailClassDuration);
        TextView tvDetailClassPrice = findViewById(R.id.tvDetailClassPrice);
        TextView tvDetailClassTypeName = findViewById(R.id.tvDetailClassTypeName);
        TextView tvDetailClassLevel = findViewById(R.id.tvDetailClassLevel);
        TextView tvDetailClassRoom = findViewById(R.id.tvDetailClassRoom);

        // Set Instance Details
        tvDetailInstanceClassDescription.setText(classItem.description);
        tvDetailInstanceDate.setText("Date: " + classInstanceItem.date);
        tvDetailInstanceTime.setText("Time: " + classInstanceItem.getClassTime()); // Use getClassTime()
        tvDetailInstanceTeacherName.setText("Teacher Name: " + classInstanceItem.teacherName);
        tvDetailInstanceComments.setText("Additional Comments: " + (!classInstanceItem.additionalComments.isEmpty() ? classInstanceItem.additionalComments : "None"));

        // Set Class Details
        tvDetailClassDescription.setText("Description: " + classItem.description);
        tvDetailClassDaysOfWeek.setText("Days of Week: " + String.join(", ", classItem.daysOfWeek));
        tvDetailClassTime.setText("Class Time: " + classItem.time);
        tvDetailClassCapacity.setText("Capacity: " + classItem.capacity);
        tvDetailClassDuration.setText("Duration: " + classItem.duration + " minutes");
        tvDetailClassPrice.setText("Price: $" + classItem.price);
        tvDetailClassTypeName.setText("Class Type: " + /* classItem.classTypeId */ classItem.classTypeName); // Use classTypeName
        tvDetailClassLevel.setText("Level: " + classItem.level);
        tvDetailClassRoom.setText("Room: " + classItem.room);
    }
}