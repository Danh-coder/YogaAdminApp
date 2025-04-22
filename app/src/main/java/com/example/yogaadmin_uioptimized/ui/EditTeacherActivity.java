package com.example.yogaadmin_uioptimized.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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

public class EditTeacherActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextBasicInfo;
    private AutoCompleteTextView editClassTypeId; // Changed to AutoCompleteTextView
    private Button buttonSave;
    private TeacherViewModel teacherViewModel;
    private ClassTypeViewModel classTypeViewModel; // Add ClassTypeViewModel
    private TeacherEntity teacher;
    private int teacherId;
    private TeacherClassTypeCrossRef crossRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_teacher); // Use activity_edit_teacher layout

        editTextName = findViewById(R.id.editTeacherName); // Use IDs from activity_add_teacher layout
        editTextBasicInfo = findViewById(R.id.editTeacherBasicInfo);
        editClassTypeId = findViewById(R.id.editClassTypeId); // Get reference to AutoCompleteTextView
        buttonSave = findViewById(R.id.btnAddTeacher); // Use IDs from activity_add_teacher layout

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


        Intent intent = getIntent();
        teacherId = intent.getIntExtra("teacherId", -1);
        if (teacherId == -1) {
            Toast.makeText(this, "Invalid teacher ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadTeacher(); // Load teacher data and pre-populate UI

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTeacher(); // Call updateTeacher method
            }
        });
    }

    private void loadTeacher() {
        teacherViewModel.getTeacherByIdLiveData(teacherId).observe(this, teacherEntity -> {
            teacher = teacherEntity;
            if (teacher == null) {
                Toast.makeText(EditTeacherActivity.this, "Teacher not found", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            editTextName.setText(teacher.name);
            editTextBasicInfo.setText(teacher.basicInfo);
            teacherViewModel.getTeacherClassTypeCrossRefByTeacherId(teacherId, new TeacherRepository.GetTeacherClassTypeCrossRefCallback() {
                @Override
                public void onTeacherClassTypeCrossRefLoaded(TeacherClassTypeCrossRef crossRefLoaded) {
                    crossRef = crossRefLoaded;
                    if (crossRef != null) {
                        // Set Class Type name in dropdown based on crossRef.classTypeId
                        classTypeViewModel.getAllClassTypes().observe(EditTeacherActivity.this, classTypeEntities -> {
                            for (com.example.yogaadmin_uioptimized.data.model.ClassTypeEntity entity : classTypeEntities) {
                                if (entity.classTypeId == crossRef.classTypeId) {
                                    editClassTypeId.setText(entity.name, false); // Set text in AutoCompleteTextView
                                    break;
                                }
                            }
                        });
                    } else {
                        editClassTypeId.setText(""); // Set empty if no class type is associated
                    }
                }
            });
        });
    }


    private void updateTeacher() {
        String name = editTextName.getText().toString().trim();
        String basicInfo = editTextBasicInfo.getText().toString().trim();
        String classTypeName = editClassTypeId.getText().toString().trim(); // Get Class Type Name from dropdown

        if (name.isEmpty() || basicInfo.isEmpty() || classTypeName.isEmpty()) {
            Toast.makeText(EditTeacherActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int classTypeId = 0; // Retrieve classTypeId from selected ClassTypeName
        // Retrieve classTypeId based on selected ClassTypeName
        for (com.example.yogaadmin_uioptimized.data.model.ClassTypeEntity entity : classTypeViewModel.getAllClassTypes().getValue()) { // Iterate through observed ClassTypeEntities
            if (entity.name.equals(classTypeName)) {
                classTypeId = entity.classTypeId;
                break;
            }
        }
        if (classTypeId == 0) {
            Toast.makeText(EditTeacherActivity.this, "Invalid Class Type selected", Toast.LENGTH_SHORT).show();
            return;
        }


        teacher.name = name;
        teacher.basicInfo = basicInfo;
        int finalClassTypeId = classTypeId;
        teacherViewModel.updateTeacher(teacher, new TeacherRepository.UpdateTeacherCallback() {
            @Override
            public void onTeacherUpdated(long teacherId) {
                if (crossRef != null) {
                    teacherViewModel.deleteTeacherClassTypeCrossRefByTeacherIdAndClassTypeId((int) teacherId, crossRef.classTypeId);
                }
                TeacherClassTypeCrossRef crossRef = new TeacherClassTypeCrossRef((int) teacherId, finalClassTypeId);
                teacherViewModel.upsertTeacherClassTypeCrossRef(crossRef);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(EditTeacherActivity.this, "Teacher updated", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        });
    }

    private void deleteTeacher() {
        // Delete the class type id
        teacherViewModel.deleteTeacherClassTypeCrossRefByTeacherId(teacherId);
        teacherViewModel.deleteTeacher(teacher);
        Toast.makeText(EditTeacherActivity.this, "Teacher deleted", Toast.LENGTH_SHORT).show();
        finish();
    }
}