package com.example.yogaadmin_uioptimized.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView; // Import AutoCompleteTextView
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.yogaadmin_uioptimized.R;
import com.example.yogaadmin_uioptimized.data.TeacherRepository;
import com.example.yogaadmin_uioptimized.data.model.TeacherClassTypeCrossRef;
import com.example.yogaadmin_uioptimized.data.model.TeacherEntity;
import com.example.yogaadmin_uioptimized.viewmodel.ClassTypeViewModel; // Add ClassTypeViewModel import
import com.example.yogaadmin_uioptimized.viewmodel.TeacherViewModel;

import java.util.ArrayList;
import java.util.List;

public class AddTeacherActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextBasicInfo;
    private AutoCompleteTextView editClassTypeId; // Changed to AutoCompleteTextView
    private Button buttonSave;
    private TeacherViewModel teacherViewModel;
    private ClassTypeViewModel classTypeViewModel; // Add ClassTypeViewModel

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_teacher);

        editTextName = findViewById(R.id.editTeacherName);
        editTextBasicInfo = findViewById(R.id.editTeacherBasicInfo);
        editClassTypeId = findViewById(R.id.editClassTypeId); // Get reference to AutoCompleteTextView
        buttonSave = findViewById(R.id.btnAddTeacher);

        teacherViewModel = new ViewModelProvider(this).get(TeacherViewModel.class);
        classTypeViewModel = new ViewModelProvider(this).get(ClassTypeViewModel.class); // Initialize ClassTypeViewModel

        // Populate Class Type dropdown
        classTypeViewModel.getAllClassTypes().observe(this, classTypeEntities -> {
            List<String> classTypeNames = new ArrayList<>();
            List<Integer> classTypeIds = new ArrayList<>();
            for (com.example.yogaadmin_uioptimized.data.model.ClassTypeEntity entity : classTypeEntities) {
                classTypeNames.add(entity.name);
                classTypeIds.add(entity.classTypeId);
            }
            ArrayAdapter<String> classTypeAdapter = new ArrayAdapter<>(this, R.layout.dropdown_menu_item, classTypeNames);
            editClassTypeId.setAdapter(classTypeAdapter);
            // You can store classTypeIds for later use if needed to get the ID based on selected name
            editClassTypeId.setOnItemClickListener((parent, view, position, id) -> {
                // You can retrieve the selected ClassTypeId if needed using classTypeIds.get(position)
            });
        });


        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editTextName.getText().toString().trim();
                String basicInfo = editTextBasicInfo.getText().toString().trim();
                String classTypeName = editClassTypeId.getText().toString().trim(); // Get Class Type Name from dropdown

                if (name.isEmpty() || basicInfo.isEmpty() || classTypeName.isEmpty()) {
                    Toast.makeText(AddTeacherActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                int classTypeId = 0; // Retrieve classTypeId from selected ClassTypeName
                // Retrieve ClassTypeId based on selected ClassTypeName
                for (com.example.yogaadmin_uioptimized.data.model.ClassTypeEntity entity : classTypeViewModel.getAllClassTypes().getValue()) { // Iterate through observed ClassTypeEntities
                    if (entity.name.equals(classTypeName)) {
                        classTypeId = entity.classTypeId;
                        break;
                    }
                }
                if (classTypeId == 0) {
                    Toast.makeText(AddTeacherActivity.this, "Invalid Class Type selected", Toast.LENGTH_SHORT).show();
                    return;
                }


                TeacherEntity teacher = new TeacherEntity(name, basicInfo);
                int finalClassTypeId = classTypeId;
                teacherViewModel.insert(teacher, new TeacherRepository.InsertTeacherCallback() {
                    @Override
                    public void onTeacherInserted(long teacherId) {
                        // This code will be executed after the teacher is inserted
                        // Now we can safely insert the TeacherClassTypeCrossRef
                        TeacherClassTypeCrossRef crossRef = new TeacherClassTypeCrossRef((int) teacherId, finalClassTypeId);
                        teacherViewModel.insertTeacherClassTypeCrossRef(crossRef);

                        // Show a success message and finish the activity
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(AddTeacherActivity.this, "Teacher added", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    }
                });
            }
        });
    }
}