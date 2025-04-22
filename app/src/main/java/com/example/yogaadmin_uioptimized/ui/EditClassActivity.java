package com.example.yogaadmin_uioptimized.ui;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.yogaadmin_uioptimized.R;
import com.example.yogaadmin_uioptimized.data.model.ClassEntity;
import com.example.yogaadmin_uioptimized.viewmodel.ClassTypeViewModel;
import com.example.yogaadmin_uioptimized.viewmodel.ClassViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EditClassActivity extends AppCompatActivity {

    private ClassViewModel classViewModel;
    private ClassTypeViewModel classTypeViewModel; // Add ClassTypeViewModel

    private ChipGroup chipGroupDaysOfWeek;
    private TextView textTimeDisplay;
    private TextInputEditText editCapacity, editDuration, editPrice, editDescription;
    private AutoCompleteTextView autoCompleteClassType, autoCompleteLevel, autoCompleteRoom;
    private MaterialButton btnUpdateClass; // Changed from btnSaveClass to btnUpdateClass
    private int classId;

    private int selectedHour = -1;
    private int selectedMinute = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_class); // Make sure it's activity_edit_class

        classViewModel = new ViewModelProvider(this).get(ClassViewModel.class);
        classTypeViewModel = new ViewModelProvider(this).get(ClassTypeViewModel.class); // Initialize ClassTypeViewModel

        chipGroupDaysOfWeek = findViewById(R.id.chipGroupDaysOfWeek);
        textTimeDisplay = findViewById(R.id.textTimeDisplay);
        editCapacity = findViewById(R.id.editCapacity);
        editDuration = findViewById(R.id.editDuration);
        editPrice = findViewById(R.id.editPrice);
        autoCompleteClassType = findViewById(R.id.autoCompleteClassType);
        editDescription = findViewById(R.id.editDescription);
        autoCompleteLevel = findViewById(R.id.autoCompleteLevel);
        autoCompleteRoom = findViewById(R.id.autoCompleteRoom);
        btnUpdateClass = findViewById(R.id.btnSaveClass); // ID is still btnSaveClass in layout, but variable name changed
        btnUpdateClass.setText("Update Class"); // Change button text to "Update Class"

        // Set click listener for time selection TextView
        textTimeDisplay.setOnClickListener(v -> showTimePickerDialog());

        // Populate Level dropdown
        ArrayAdapter<String> levelAdapter = new ArrayAdapter<>(this, R.layout.dropdown_menu_item, getResources().getStringArray(R.array.class_levels));
        autoCompleteLevel.setAdapter(levelAdapter);

        // Populate Room dropdown
        ArrayAdapter<String> roomAdapter = new ArrayAdapter<>(this, R.layout.dropdown_menu_item, getResources().getStringArray(R.array.class_rooms));
        autoCompleteRoom.setAdapter(roomAdapter);

        // Populate Class Type dropdown from database
        classTypeViewModel.getAllClassTypes().observe(this, classTypeEntities -> {
            List<String> classTypeNames = new ArrayList<>();
            List<Integer> classTypeIds = new ArrayList<>();
            for (com.example.yogaadmin_uioptimized.data.model.ClassTypeEntity entity : classTypeEntities) {
                classTypeNames.add(entity.name);
                classTypeIds.add(entity.classTypeId);
            }
            ArrayAdapter<String> classTypeAdapter = new ArrayAdapter<>(this, R.layout.dropdown_menu_item, classTypeNames);
            autoCompleteClassType.setAdapter(classTypeAdapter);
            // Store ClassTypeIds for later use if needed to get the ID based on selected name
            autoCompleteClassType.setOnItemClickListener((parent, view, position, id) -> {
                // You can retrieve the selected ClassTypeId if needed using classTypeIds.get(position)
            });
        });


        btnUpdateClass.setOnClickListener(v -> updateClass()); // Changed to updateClass

        classId = getIntent().getIntExtra("classId", -1);
        if (classId == -1) {
            Toast.makeText(this, "Invalid class ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Observe LiveData for ClassEntity and pre-populate UI
        classViewModel.getClassByIdLiveData(classId).observe(this, classEntity -> {
            if (classEntity != null) {
                prePopulateUI(classEntity);
            }
        });
    }

    private void prePopulateUI(ClassEntity classEntity) {
        // Days of week chips
        List<String> daysOfWeekList = Arrays.asList(classEntity.daysOfWeek.split(","));
        for (String day : daysOfWeekList) {
            if (day.equals("Monday")) chipGroupDaysOfWeek.check(R.id.chipMonday);
            else if (day.equals("Tuesday")) chipGroupDaysOfWeek.check(R.id.chipTuesday);
            else if (day.equals("Wednesday")) chipGroupDaysOfWeek.check(R.id.chipWednesday);
            else if (day.equals("Thursday")) chipGroupDaysOfWeek.check(R.id.chipThursday);
            else if (day.equals("Friday")) chipGroupDaysOfWeek.check(R.id.chipFriday);
            else if (day.equals("Saturday")) chipGroupDaysOfWeek.check(R.id.chipSaturday);
            else if (day.equals("Sunday")) chipGroupDaysOfWeek.check(R.id.chipSunday);
        }

        // Time
        textTimeDisplay.setText(classEntity.time);
        String[] timeParts = classEntity.time.split(":"); // Assuming time is in "hh:mm a" format
        if (timeParts.length == 2) {
            try {
                selectedHour = Integer.parseInt(timeParts[0]);
                selectedMinute = Integer.parseInt(timeParts[1]);
                // You might need to adjust hour and minute parsing based on your time format if it includes AM/PM
            } catch (NumberFormatException e) {
                // Handle parsing error if time format is different
            }
        }


        editCapacity.setText(String.valueOf(classEntity.capacity));
        editDuration.setText(String.valueOf(classEntity.duration));
        editPrice.setText(String.valueOf(classEntity.price));
        editDescription.setText(classEntity.description);
        autoCompleteLevel.setText(classEntity.level, false);
        autoCompleteRoom.setText(classEntity.room, false);

        // Set Class Type - need to find the correct name from classTypeId
        classTypeViewModel.getAllClassTypes().observe(this, classTypeEntities -> {
            for (com.example.yogaadmin_uioptimized.data.model.ClassTypeEntity entity : classTypeEntities) {
                if (entity.classTypeId == classEntity.classTypeId) {
                    autoCompleteClassType.setText(entity.name, false); // Set text without filter
                    break;
                }
            }
        });
    }


    private void showTimePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minuteOfHour) -> {
                    selectedHour = hourOfDay;
                    selectedMinute = minuteOfHour;
                    updateTimeDisplay();
                }, hour, minute, true); // false for 12-hour format, true for 24-hour format
        timePickerDialog.show();
    }

    private void updateTimeDisplay() {
        if (selectedHour != -1 && selectedMinute != -1) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
            calendar.set(Calendar.MINUTE, selectedMinute);

            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault()); // Use "hh" for 12-hour format
            String formattedTime = timeFormat.format(calendar.getTime());
            textTimeDisplay.setText(formattedTime);
        } else {
            textTimeDisplay.setText("No time selected");
        }
    }


    private void updateClass() { // Changed from saveClass to updateClass
        List<String> daysOfWeek = getSelectedDaysOfWeek();
        String time = textTimeDisplay.getText().toString();
        String capacityStr = editCapacity.getText().toString().trim();
        String durationStr = editDuration.getText().toString().trim();
        String priceStr = editPrice.getText().toString().trim();
        String classTypeName = autoCompleteClassType.getText().toString();
        String description = editDescription.getText().toString().trim();
        String level = autoCompleteLevel.getText().toString();
        String room = autoCompleteRoom.getText().toString();

        // Input Validation (same as AddClassActivity)
        if (daysOfWeek.isEmpty()) {
            Toast.makeText(this, "Please select days of week", Toast.LENGTH_SHORT).show();
            return;
        }
        if (time.equals("No time selected")) {
            Toast.makeText(this, "Please select time", Toast.LENGTH_SHORT).show();
            return;
        }
        if (capacityStr.isEmpty() || durationStr.isEmpty() || priceStr.isEmpty() || classTypeName.isEmpty() || description.isEmpty() || level.isEmpty() || room.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int capacity, duration; // classTypeId will be retrieved or defaulted
        double price;
        try {
            capacity = Integer.parseInt(capacityStr);
            duration = Integer.parseInt(durationStr);
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number format", Toast.LENGTH_SHORT).show();
            return;
        }

        if (capacity <= 0 || duration <= 0 || price <= 0) {
            Toast.makeText(this, "Capacity, duration, and price must be greater than zero", Toast.LENGTH_SHORT).show();
            return;
        }

        // Retrieve ClassTypeId based on selected ClassTypeName and update class
        classTypeViewModel.getAllClassTypes().observe(this, classTypeEntities -> {
            int classTypeId = 0;
            for (com.example.yogaadmin_uioptimized.data.model.ClassTypeEntity entity : classTypeEntities) {
                if (entity.name.equals(classTypeName)) {
                    classTypeId = entity.classTypeId;
                    break; // Stop once found
                }
            }
            if (classTypeId == 0) {
                Toast.makeText(this, "Invalid Class Type selected", Toast.LENGTH_SHORT).show();
                return;
            }

            ClassEntity updatedClass = new ClassEntity(String.join(",", daysOfWeek), time, capacity, duration, price, classTypeId, description, level, room);
            updatedClass.classId = classId; // Set the classId for update
            classViewModel.update(updatedClass);

            Toast.makeText(this, "Class updated", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private List<String> getSelectedDaysOfWeek() {
        return getSelectedDaysOfWeekFullname();
    }
    private List<String> getSelectedDaysOfWeekAbbr() {
        List<String> selectedDays = new ArrayList<>();
        for (int i = 0; i < chipGroupDaysOfWeek.getChildCount(); i++) {
            Chip chip = (Chip) chipGroupDaysOfWeek.getChildAt(i);
            if (chip.isChecked()) {
                selectedDays.add(chip.getText().toString());
            }
        }
        return selectedDays;
    }
    private List<String> getSelectedDaysOfWeekFullname() {
        List<String> abbreviatedDays = getSelectedDaysOfWeekAbbr();
        return convertDaysOfWeekToFullString(abbreviatedDays);
    }


    private List<String> convertDaysOfWeekToFullString(List<String> abbreviatedDays) {
        List<String> fullDays = new ArrayList<>();
        for (String day : abbreviatedDays) {
            switch (day) {
                case "M": fullDays.add("Monday"); break;
                case "T": fullDays.add("Tuesday"); break;
                case "W": fullDays.add("Wednesday"); break;
                case "Th": fullDays.add("Thursday"); break;
                case "F": fullDays.add("Friday"); break;
                case "Sa": fullDays.add("Saturday"); break;
                case "Su": fullDays.add("Sunday"); break;
            }
        }
        return fullDays;
    }
}