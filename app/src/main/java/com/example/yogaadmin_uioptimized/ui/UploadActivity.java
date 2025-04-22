package com.example.yogaadmin_uioptimized.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.yogaadmin_uioptimized.R;
import com.example.yogaadmin_uioptimized.utils.NetworkUtils; // Import NetworkUtils
import com.example.yogaadmin_uioptimized.viewmodel.ClassViewModel;
import com.example.yogaadmin_uioptimized.viewmodel.FirebaseViewModel;

import java.util.ArrayList;
import java.util.List;

public class UploadActivity extends AppCompatActivity {

    private FirebaseViewModel firebaseViewModel;
    private ClassViewModel classViewModel;
    private Button btnUploadClasses, btnUploadClassTypes, btnUploadTeachers, btnUploadAllData, btnUploadClassInstances; // Buttons for different upload actions

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        firebaseViewModel = new ViewModelProvider(this).get(FirebaseViewModel.class);
        classViewModel = new ViewModelProvider(this).get(ClassViewModel.class);

        btnUploadClasses = findViewById(R.id.btnUploadClasses); // Find individual upload buttons
        btnUploadClassTypes = findViewById(R.id.btnUploadClassTypes);
        btnUploadTeachers = findViewById(R.id.btnUploadTeachers);
        btnUploadAllData = findViewById(R.id.btnUploadAllData); // Find "Upload All Data" button
        btnUploadClassInstances = findViewById(R.id.btnUploadClassInstances); // Find "Upload Class Instances" button

        btnUploadClasses.setOnClickListener(v -> { // OnClickListener for Upload Classes
            if (NetworkUtils.isNetworkConnected(this)) { // Check network connectivity
                firebaseViewModel.uploadClasses();
                Toast.makeText(this, "Classes upload started", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            }
        });

        btnUploadClassTypes.setOnClickListener(v -> { // OnClickListener for Upload Class Types
            if (NetworkUtils.isNetworkConnected(this)) { // Check network connectivity
                firebaseViewModel.uploadClassTypes();
                Toast.makeText(this, "Class types upload started", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            }
        });

        btnUploadTeachers.setOnClickListener(v -> { // OnClickListener for Upload Teachers
            if (NetworkUtils.isNetworkConnected(this)) { // Check network connectivity
                firebaseViewModel.uploadTeachers();
                Toast.makeText(this, "Teachers upload started", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            }
        });

        btnUploadClassInstances.setOnClickListener(v -> { // OnClickListener for Upload Class Instances
            if (NetworkUtils.isNetworkConnected(this)) { // Check network connectivity
                firebaseViewModel.uploadClassInstances();
                Toast.makeText(this, "Class instances upload started", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            }
        });

        btnUploadAllData.setOnClickListener(v -> { // OnClickListener for Upload All Data
            if (NetworkUtils.isNetworkConnected(this)) { // Check network connectivity
                firebaseViewModel.uploadAllData();
                Toast.makeText(this, "All data upload started", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }
}