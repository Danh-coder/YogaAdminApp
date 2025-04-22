package com.example.yogaadmin_uioptimized.ui;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yogaadmin_uioptimized.ClassInstanceItem;
import com.example.yogaadmin_uioptimized.R;
import com.example.yogaadmin_uioptimized.DateGroupedClassInstance;
import com.example.yogaadmin_uioptimized.viewmodel.ClassInstanceViewModel;
import com.example.yogaadmin_uioptimized.viewmodel.ClassViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ClassSpecificInstanceListActivity extends AppCompatActivity {

    private ClassInstanceViewModel classInstanceViewModel;
    private ClassViewModel classViewModel; // Add ClassViewModel
    private RecyclerView classInstanceRecyclerView;
    private ClassInstanceAdapter classInstanceAdapter;
    private int classId; // Class ID to filter instances

    private TextInputEditText searchEditTextTeacherName;
    private ChipGroup chipGroupFilterDaysOfWeek;
    private TextView textFilterDateDisplay;

    private Calendar filterDateCalendar = Calendar.getInstance();
    private String filterDateString = "";
    private String searchText = "";
    private List<String> selectedDaysOfWeekFilter = new ArrayList<>();


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_specific_instance_list);

        classInstanceRecyclerView = findViewById(R.id.classInstanceRecyclerView);
        searchEditTextTeacherName = findViewById(R.id.searchEditTextTeacherName);
        chipGroupFilterDaysOfWeek = findViewById(R.id.chipGroupFilterDaysOfWeek);
        textFilterDateDisplay = findViewById(R.id.textFilterDateDisplay);

        classInstanceRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        classInstanceAdapter = new ClassInstanceAdapter(new ArrayList<>(), classInstanceViewModel, classViewModel, this); // Initialize adapter with ClassViewModel
        classInstanceRecyclerView.setAdapter(classInstanceAdapter);


        classInstanceViewModel = new ViewModelProvider(this).get(ClassInstanceViewModel.class);
        classViewModel = new ViewModelProvider(this).get(ClassViewModel.class); // Initialize ClassViewModel

        // Get classId from intent
        classId = getIntent().getIntExtra("CLASS_ID", -1);
        if (classId == -1) {
            Toast.makeText(this, "Class ID not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        observeGroupedClassInstances(); // Observe grouped class instances

        searchEditTextTeacherName.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                searchText = searchEditTextTeacherName.getText().toString().trim();
                applyFilters();
                return true;
            }
            return false;
        });

        chipGroupFilterDaysOfWeek.setOnCheckedStateChangeListener((group, checkedIds) -> {
            selectedDaysOfWeekFilter = getSelectedDaysOfWeekFilter();
            applyFilters();
        });

        textFilterDateDisplay.setOnClickListener(v -> {
            if (!filterDateString.isEmpty()) {
                // Clear Date Filter if a date is already selected
                filterDateString = ""; // Reset filterDateString
                textFilterDateDisplay.setText("No date selected"); // Update display text
                textFilterDateDisplay.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_calendar, 0);
            } else {
                // Show DatePickerDialog if no date is selected yet
                showDatePickerDialog();
            }
            applyFilters(); // Apply filters when date filter changes - Just call applyFilters to update criteria
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void observeGroupedClassInstances() {
        classInstanceViewModel.getDateGroupedClassInstances().observe(this, dateGroupedClassInstances -> {
            Log.d("ClassSpecificInstanceListActivity", "dateGroupedClassInstances observed: " + dateGroupedClassInstances);
            if (dateGroupedClassInstances != null) {
                List<DateGroupedClassInstance> filteredList = filterByClassId(dateGroupedClassInstances); // Filter by classId
                classInstanceAdapter.setClassInstanceItems(filteredList);
            }
        });
    }

    private List<DateGroupedClassInstance> filterByClassId(List<DateGroupedClassInstance> dateGroupedClassInstances) {
        List<DateGroupedClassInstance> filteredByClassId = new ArrayList<>();
        for (DateGroupedClassInstance groupedInstance : dateGroupedClassInstances) {
            List<ClassInstanceItem> filteredItems = new ArrayList<>();
            for (ClassInstanceItem item : groupedInstance.getItems()) {
                if (item.getClassId() == classId) { // Filter by classId
                    filteredItems.add(item);
                }
            }
            if (!filteredItems.isEmpty()) {
                filteredByClassId.add(new DateGroupedClassInstance(groupedInstance.getDate(), groupedInstance.getDayOfWeek(), filteredItems));
            }
        }
        return filteredByClassId;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void applyFilters() {
        // Apply search and filters - reuse logic from ClassInstancesFragment or adapt as needed
        Log.d("ClassInstancesFragment", "applyFilters: Method called");
        // No longer observing LiveData here - just updating filter criteria
        classInstanceViewModel.setSearchTextFilter(searchText); // Update search text filter in ViewModel
        classInstanceViewModel.setSelectedDaysOfWeekFilter(selectedDaysOfWeekFilter); // Update day of week filter
        classInstanceViewModel.setFilterDateString(filterDateString); // Update date filter
        // The Transformation in ViewModel will now automatically re-apply filters and update dateGroupedClassInstances LiveData
        classInstanceViewModel.applyFiltersAndGroupData(); // Explicitly refresh grouped data
    }

    private List<String> getSelectedDaysOfWeekFilter() {
        List<String> selectedDays = new ArrayList<>();
        for (int i = 0; i < chipGroupFilterDaysOfWeek.getChildCount(); i++) {
            Chip chip = (Chip) chipGroupFilterDaysOfWeek.getChildAt(i);
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showDatePickerDialog() {
        int year = filterDateCalendar.get(Calendar.YEAR);
        int month = filterDateCalendar.get(Calendar.MONTH);
        int day = filterDateCalendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, yearSelected, monthOfYear, dayOfMonth) -> {
                    filterDateCalendar.set(Calendar.YEAR, yearSelected);
                    filterDateCalendar.set(Calendar.MONTH, monthOfYear);
                    filterDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateDisplay();
                    applyFilters();
                },
                year, month, day);
        datePickerDialog.show();
    }

    private void updateDateDisplay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        filterDateString = dateFormat.format(filterDateCalendar.getTime());

        SimpleDateFormat displayDateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()); // Format for display
        String displayDate = displayDateFormat.format(filterDateCalendar.getTime());
        textFilterDateDisplay.setText(displayDate);


        if (!filterDateString.isEmpty()) {
            textFilterDateDisplay.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_clear, 0);
        } else {
            textFilterDateDisplay.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_calendar, 0);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        super.onResume();
        observeGroupedClassInstances();
    }
}