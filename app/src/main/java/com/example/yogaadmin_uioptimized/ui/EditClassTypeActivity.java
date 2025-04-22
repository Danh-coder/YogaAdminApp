package com.example.yogaadmin_uioptimized.ui;

import android.content.Intent;
import android.os.AsyncTask; // Remove AsyncTask import
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.yogaadmin_uioptimized.R;
import com.example.yogaadmin_uioptimized.data.model.ClassTypeEntity;
import com.example.yogaadmin_uioptimized.viewmodel.ClassTypeViewModel;

public class EditClassTypeActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextDescription;
    private Button buttonSave;
    private Button buttonDelete;
    private ClassTypeViewModel classTypeViewModel;
    private ClassTypeEntity classType; // No longer needed as member variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_class_type);

        editTextName = findViewById(R.id.editTextName);
        editTextDescription = findViewById(R.id.editTextDescription);
        buttonSave = findViewById(R.id.buttonSave);
        buttonDelete = findViewById(R.id.buttonDelete);

        classTypeViewModel = new ViewModelProvider(this).get(ClassTypeViewModel.class);

        Intent intent = getIntent();
        int classTypeId = intent.getIntExtra("classTypeId", -1);
        if (classTypeId == -1) {
            Toast.makeText(this, "Invalid class type ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Remove GetClassTypeAsyncTask and replace with LiveData observation
        classTypeViewModel.getClassTypeById(classTypeId).observe(this, classTypeEntity -> {
            if (classTypeEntity != null) {
                // Class type found, populate UI
                classType = classTypeEntity; // Keep this line to use classType in button clicks
                editTextName.setText(classTypeEntity.name);
                editTextDescription.setText(classTypeEntity.description);
            } else {
                // Class type not found
                Toast.makeText(EditClassTypeActivity.this, "Class type not found", Toast.LENGTH_SHORT).show();
                finish();
            }
        });


        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString().trim();
                String description = editTextDescription.getText().toString().trim();

                if (name.isEmpty() || description.isEmpty()) {
                    Toast.makeText(EditClassTypeActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (classType != null) { // Check if classType is not null before updating
                    classType.name = name;
                    classType.description = description;
                    classTypeViewModel.updateClassType(classType);

                    Toast.makeText(EditClassTypeActivity.this, "Class type updated", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditClassTypeActivity.this, "Error: Class type not loaded", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (classType != null) { // Check if classType is not null before deleting
                    classTypeViewModel.deleteClassType(classType);
                    Toast.makeText(EditClassTypeActivity.this, "Class type deleted", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(EditClassTypeActivity.this, "Error: Class type not loaded", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}