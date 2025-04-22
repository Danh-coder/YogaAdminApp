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
import com.example.yogaadmin_uioptimized.viewmodel.ClassTypeViewModel;
import com.example.yogaadmin_uioptimized.viewmodel.ClassViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddClassActivity extends AppCompatActivity {

    private ClassViewModel classViewModel;
    private ClassTypeViewModel classTypeViewModel;

    private ChipGroup chipGroupDaysOfWeek;
    private TextView textTimeDisplay;
    private TextInputEditText editCapacity, editDuration, editPrice, editDescription;
    private AutoCompleteTextView autoCompleteClassType, autoCompleteLevel, autoCompleteRoom;
    private MaterialButton btnSaveClass;

    private int selectedHour = -1;
    private int selectedMinute = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_class);

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
        btnSaveClass = findViewById(R.id.btnSaveClass);

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


        btnSaveClass.setOnClickListener(v -> saveClass());
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


    private void saveClass() {
        List<String> daysOfWeek = getSelectedDaysOfWeek();
        String time = textTimeDisplay.getText().toString();
        String capacityStr = editCapacity.getText().toString().trim();
        String durationStr = editDuration.getText().toString().trim();
        String priceStr = editPrice.getText().toString().trim();
        String classTypeName = autoCompleteClassType.getText().toString();
        String description = editDescription.getText().toString().trim();
        String level = autoCompleteLevel.getText().toString();
        String room = autoCompleteRoom.getText().toString();

        // Input Validation
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

        int capacity, duration;
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

        // Retrieve ClassTypeId based on selected ClassTypeName and save class within the observer
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
            classViewModel.saveClass(daysOfWeek, time, capacity, duration, price, classTypeId, description, level, room);
            Toast.makeText(this, "Class saved", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private List<String> getSelectedDaysOfWeek() {
        List<String> selectedDays = new ArrayList<>();
        for (int i = 0; i < chipGroupDaysOfWeek.getChildCount(); i++) {
            Chip chip = (Chip) chipGroupDaysOfWeek.getChildAt(i);
            if (chip.isChecked()) {
                selectedDays.add(chip.getText().toString());
            }
        }
        return convertDaysOfWeekToFullString(selectedDays);
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