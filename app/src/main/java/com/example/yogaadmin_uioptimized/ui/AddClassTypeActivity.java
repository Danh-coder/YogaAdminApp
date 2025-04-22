package com.example.yogaadmin_uioptimized.ui;

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

public class AddClassTypeActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextDescription;
    private Button buttonSave;
    private ClassTypeViewModel classTypeViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class_type);

        editTextName = findViewById(R.id.editTextName);
        editTextDescription = findViewById(R.id.editTextDescription);
        buttonSave = findViewById(R.id.buttonSave);

        classTypeViewModel = new ViewModelProvider(this).get(ClassTypeViewModel.class);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString().trim();
                String description = editTextDescription.getText().toString().trim();

                if (name.isEmpty() || description.isEmpty()) {
                    Toast.makeText(AddClassTypeActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                ClassTypeEntity classTypeEntity = new ClassTypeEntity(name, description);
                classTypeViewModel.insertClassType(classTypeEntity);

                Toast.makeText(AddClassTypeActivity.this, "Class type saved", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}