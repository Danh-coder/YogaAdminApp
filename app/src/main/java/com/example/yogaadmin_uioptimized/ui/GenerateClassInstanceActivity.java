package com.example.yogaadmin_uioptimized.ui;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.yogaadmin_uioptimized.ClassItem;
import com.example.yogaadmin_uioptimized.R;
import com.example.yogaadmin_uioptimized.data.model.ClassEntity;
import com.example.yogaadmin_uioptimized.data.model.TeacherEntity;
import com.example.yogaadmin_uioptimized.viewmodel.ClassInstanceViewModel;
import com.example.yogaadmin_uioptimized.viewmodel.ClassViewModel;
import com.example.yogaadmin_uioptimized.viewmodel.TeacherViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class GenerateClassInstanceActivity extends AppCompatActivity {

    private ClassInstanceViewModel classInstanceViewModel;
    private ClassViewModel classViewModel;
    private TeacherViewModel teacherViewModel; // Add TeacherViewModel

    private AutoCompleteTextView autoCompleteClassDescription, autoCompleteTeacherName;
    private TextView textStartDateDisplay, textEndDateDisplay;
    private Button btnGenerate;

    private Calendar startDateCalendar = Calendar.getInstance(); // Calendar for start date
    private Calendar endDateCalendar = Calendar.getInstance(); // Calendar for end date
    private String startDateString = "";
    private String endDateString = "";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_class_instance);

        classInstanceViewModel = new ViewModelProvider(this).get(ClassInstanceViewModel.class);
        classViewModel = new ViewModelProvider(this).get(ClassViewModel.class);
        teacherViewModel = new ViewModelProvider(this).get(TeacherViewModel.class); // Initialize TeacherViewModel

        autoCompleteClassDescription = findViewById(R.id.autoCompleteClassDescription);
        autoCompleteTeacherName = findViewById(R.id.autoCompleteTeacherName);
        textStartDateDisplay = findViewById(R.id.textStartDateDisplay);
        textEndDateDisplay = findViewById(R.id.textEndDateDisplay);
        btnGenerate = findViewById(R.id.btnGenerate);

        // Populate Class Description dropdown
        classViewModel.getAllClasses().observe(this, classItems -> {
            List<String> classDescriptions = new ArrayList<>();
            List<Integer> classIds = new ArrayList<>();
            for (ClassItem classItem : classItems) {
                classDescriptions.add(classItem.description);
                classIds.add(classItem.classId);
            }
            ArrayAdapter<String> classAdapter = new ArrayAdapter<>(this, R.layout.dropdown_menu_item, classDescriptions);
            autoCompleteClassDescription.setAdapter(classAdapter);
            // You can store classIds if you need to map description to ID later
        });

        // Populate Teacher Name dropdown
        teacherViewModel.getAllTeachers().observe(this, teacherEntities -> {
            List<String> teacherNames = new ArrayList<>();
            List<Integer> teacherIds = new ArrayList<>();
            for (TeacherEntity teacherEntity : teacherEntities) {
                teacherNames.add(teacherEntity.name);
                teacherIds.add(teacherEntity.teacherId);
            }
            ArrayAdapter<String> teacherAdapter = new ArrayAdapter<>(this, R.layout.dropdown_menu_item, teacherNames);
            autoCompleteTeacherName.setAdapter(teacherAdapter);
            // You can store teacherIds if you need to map name to ID later
        });

        // Set click listeners for Date TextViews
        textStartDateDisplay.setOnClickListener(v -> showStartDatePickerDialog());
        textEndDateDisplay.setOnClickListener(v -> showEndDatePickerDialog());

        btnGenerate.setOnClickListener(v -> generateClassInstances());
    }

    private void showStartDatePickerDialog() {
        int year = startDateCalendar.get(Calendar.YEAR);
        int month = startDateCalendar.get(Calendar.MONTH);
        int day = startDateCalendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, yearSelected, monthOfYear, dayOfMonth) -> {
                    startDateCalendar.set(Calendar.YEAR, yearSelected);
                    startDateCalendar.set(Calendar.MONTH, monthOfYear);
                    startDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateStartDateDisplay();
                }, year, month, day);
        datePickerDialog.show();
    }

    private void showEndDatePickerDialog() {
        int year = endDateCalendar.get(Calendar.YEAR);
        int month = endDateCalendar.get(Calendar.MONTH);
        int day = endDateCalendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, yearSelected, monthOfYear, dayOfMonth) -> {
                    endDateCalendar.set(Calendar.YEAR, yearSelected);
                    endDateCalendar.set(Calendar.MONTH, monthOfYear);
                    endDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateEndDateDisplay();
                }, year, month, day);
        datePickerDialog.show();
    }

    private void updateStartDateDisplay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        startDateString = dateFormat.format(startDateCalendar.getTime());
        textStartDateDisplay.setText(startDateString);
    }

    private void updateEndDateDisplay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        endDateString = dateFormat.format(endDateCalendar.getTime());
        textEndDateDisplay.setText(endDateString);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void generateClassInstances() {
        String classDescription = autoCompleteClassDescription.getText().toString();
        String teacherName = autoCompleteTeacherName.getText().toString();

        if (classDescription.isEmpty() || teacherName.isEmpty() || startDateString.isEmpty() || endDateString.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Retrieve classId from selected Class Description
        classViewModel.getAllClasses().observe(this, classItems -> {
            Integer selectedClassId = null; // Use Integer to handle null cases properly

            for (ClassItem classItem : classItems) {
                if (classItem.description.equals(classDescription)) {
                    selectedClassId = classItem.classId;
                    break;
                }
            }

            if (selectedClassId == null) {
                Toast.makeText(this, "Invalid Class Description selected", Toast.LENGTH_SHORT).show();
                return;
            }

            // Retrieve teacherId from selected Teacher Name
            Integer finalClassId = selectedClassId; // Make classId final for use inside the inner observer
            teacherViewModel.getAllTeachers().observe(this, teacherEntities -> {
                Integer selectedTeacherId = null; // Use Integer instead of int to differentiate between 0 and null

                for (TeacherEntity teacherEntity : teacherEntities) {
                    if (teacherEntity.name.equals(teacherName)) {
                        selectedTeacherId = teacherEntity.teacherId;
                        break;
                    }
                }

                if (selectedTeacherId == null) {
                    Toast.makeText(this, "Invalid Teacher Name selected", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Fetch ClassEntity to generate class instances
                Integer finalTeacherId = selectedTeacherId; // Make teacherId final for use inside the next observer
                classViewModel.getClassByIdLiveData(finalClassId).observe(this, fetchedClassEntity -> {
                    if (fetchedClassEntity != null) {
                        classInstanceViewModel.generateClassInstances(finalClassId, startDateString, endDateString, fetchedClassEntity, finalTeacherId);
                        Toast.makeText(GenerateClassInstanceActivity.this, "Class instances generated", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(GenerateClassInstanceActivity.this, "Error: Class details not found", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });

    }
}