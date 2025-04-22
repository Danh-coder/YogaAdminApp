package com.example.yogaadmin_uioptimized.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.yogaadmin_uioptimized.ClassItem;
import com.example.yogaadmin_uioptimized.R;
import com.example.yogaadmin_uioptimized.data.model.ClassEntity;
import com.example.yogaadmin_uioptimized.data.model.ClassInstanceEntity;
import com.example.yogaadmin_uioptimized.data.model.TeacherEntity;
import com.example.yogaadmin_uioptimized.viewmodel.ClassInstanceViewModel;
import com.example.yogaadmin_uioptimized.viewmodel.ClassViewModel;
import com.example.yogaadmin_uioptimized.viewmodel.TeacherViewModel;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EditClassInstanceActivity extends AppCompatActivity {

    public static final String EXTRA_INSTANCE_ID = "com.example.yogaadmin_full.EXTRA_INSTANCE_ID";

    private AutoCompleteTextView autoCompleteClassDescription;
    private TextView textDateDisplay;
    private AutoCompleteTextView autoCompleteTeacherName;
    private EditText editTextAdditionalComments;
    private Button buttonSave;

    private ClassInstanceViewModel classInstanceViewModel;
    private ClassViewModel classViewModel;
    private TeacherViewModel teacherViewModel; // Add TeacherViewModel
    private int instanceId;
    private Calendar dateCalendar = Calendar.getInstance();
    private String dateString = "";
    private int selectedClassId = 0;
    private int selectedTeacherId = 0;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_class_instance);

        autoCompleteClassDescription = findViewById(R.id.autoCompleteClassDescription);
        textDateDisplay = findViewById(R.id.textDateDisplay);
        autoCompleteTeacherName = findViewById(R.id.autoCompleteTeacherName);
        editTextAdditionalComments = findViewById(R.id.edit_text_additional_comments);
        buttonSave = findViewById(R.id.button_save);

        classInstanceViewModel = new ViewModelProvider(this).get(ClassInstanceViewModel.class);
        classViewModel = new ViewModelProvider(this).get(ClassViewModel.class);
        teacherViewModel = new ViewModelProvider(this).get(TeacherViewModel.class); // Initialize TeacherViewModel

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
            autoCompleteClassDescription.setOnItemClickListener((parent, view, position, id) -> {
                selectedClassId = classIds.get(position); // Get selected classId
            });
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
            autoCompleteTeacherName.setOnItemClickListener((parent, view, position, id) -> {
                selectedTeacherId = teacherIds.get(position); // Get selected teacherId
            });
        });

        // Set click listener for Date TextView
        textDateDisplay.setOnClickListener(v -> showDatePickerDialog());

        buttonSave.setOnClickListener(v -> saveClassInstance());

        Intent intent = getIntent();
        if (intent.hasExtra("instanceId")) {
            instanceId = intent.getIntExtra("instanceId", -1);
            if (instanceId != -1) {
                classInstanceViewModel.getClassInstanceById(instanceId).observe(this, classInstanceEntity -> {
                    if (classInstanceEntity != null) {
                        prePopulateUI(classInstanceEntity);
                    }
                });
            }
        }
    }

    private void prePopulateUI(ClassInstanceEntity classInstanceEntity) {
        editTextAdditionalComments.setText(classInstanceEntity.additionalComments);
        dateString = classInstanceEntity.date;
        textDateDisplay.setText(dateString);
        selectedClassId = classInstanceEntity.classId;
        selectedTeacherId = classInstanceEntity.teacherId;

        // Pre-select Class Description in dropdown
        classViewModel.getAllClasses().observe(this, classItems -> {
            for (ClassItem classItem : classItems) {
                if (classItem.classId == classInstanceEntity.classId) {
                    autoCompleteClassDescription.setText(classItem.description, false); // Set text without filter
                    break;
                }
            }
        });

        // Pre-select Teacher Name in dropdown
        teacherViewModel.getAllTeachers().observe(this, teacherEntities -> {
            for (TeacherEntity teacherEntity : teacherEntities) {
                if (teacherEntity.teacherId == classInstanceEntity.teacherId) {
                    autoCompleteTeacherName.setText(teacherEntity.name, false); // Set text without filter
                    break;
                }
            }
        });
    }


    private void showDatePickerDialog() {
        int year = dateCalendar.get(Calendar.YEAR);
        int month = dateCalendar.get(Calendar.MONTH);
        int day = dateCalendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, yearSelected, monthOfYear, dayOfMonth) -> {
                    dateCalendar.set(Calendar.YEAR, yearSelected);
                    dateCalendar.set(Calendar.MONTH, monthOfYear);
                    dateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateDisplay();
                }, year, month, day);
        datePickerDialog.show();
    }

    private void updateDateDisplay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        dateString = dateFormat.format(dateCalendar.getTime());
        textDateDisplay.setText(dateString);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void saveClassInstance() {
        String date = textDateDisplay.getText().toString();
        String additionalComments = editTextAdditionalComments.getText().toString();
        String classDescription = autoCompleteClassDescription.getText().toString();
        String teacherName = autoCompleteTeacherName.getText().toString();

        if (date.equals("Select Date") || classDescription.isEmpty() || teacherName.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Observe LiveData and ensure execution continues only when result is obtained
        isValidDateForClass(date, selectedClassId).observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isValid) {
                if (isValid == null) return; // Avoid null values

                if (isValid) {
                    // Proceed with saving class instance
                    ClassInstanceEntity classInstanceEntity = new ClassInstanceEntity(selectedClassId, date, selectedTeacherId, additionalComments);
                    classInstanceEntity.instanceId = instanceId;

                    classInstanceViewModel.update(classInstanceEntity);

                    Toast.makeText(EditClassInstanceActivity.this, "Class Instance updated", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    // Show error Toast if date is invalid
                    Toast.makeText(EditClassInstanceActivity.this, "Selected date does not match class days of week", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private LiveData<Boolean> isValidDateForClass(String dateStr, int classId) { // Modified to return LiveData<Boolean>
        MutableLiveData<Boolean> isValidLiveData = new MutableLiveData<>(); // MutableLiveData to hold validation result
        isValidLiveData.setValue(null); // Initialize to null

        classViewModel.getClassByIdLiveData(classId).observe(this, classEntity -> {
            boolean isValid = false; // Local isValid variable
            if (classEntity != null) {
                List<String> daysOfWeek = Arrays.asList(classEntity.daysOfWeek.split(","));
                if (!daysOfWeek.isEmpty()) {
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        LocalDate selectedDate = LocalDate.parse(dateStr, formatter);
                        DayOfWeek selectedDayOfWeek = selectedDate.getDayOfWeek();
                        String selectedDayOfWeekString = selectedDayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault());

                        Log.d("EditClassInstanceActivity", "Selected Day of Week: " + selectedDayOfWeekString);
                        for (String day : daysOfWeek) {
                            if (day.equalsIgnoreCase(selectedDayOfWeekString.trim())) {
                                isValid = true; // Set isValid to true if day matches
                                break;
                            }
                        }
                    } catch (Exception e) {
                        Log.e("EditClassInstanceActivity", "Date parsing error: ", e);
                    }
                }
            }
            isValidLiveData.postValue(isValid); // Post validation result to LiveData
        });

        return isValidLiveData; // Return LiveData<Boolean>
    }
}