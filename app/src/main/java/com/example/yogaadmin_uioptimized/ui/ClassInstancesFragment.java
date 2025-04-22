package com.example.yogaadmin_uioptimized.ui;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
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

@RequiresApi(api = Build.VERSION_CODES.O)
public class ClassInstancesFragment extends Fragment {

    private ClassInstanceViewModel classInstanceViewModel;
    private ClassViewModel classViewModel;
    private RecyclerView classInstancesRecyclerView;
    private ClassInstanceAdapter classInstanceAdapter;
    private FloatingActionButton fabAddClassInstance;

    private TextInputEditText searchEditTextTeacherName;
    private ChipGroup chipGroupFilterDaysOfWeek;
    private TextView textFilterDateDisplay;

    private Calendar filterDateCalendar = Calendar.getInstance(); // Calendar for date filter
    private String filterDateString = ""; // Store current search text
    private String searchText = ""; // Store current search text
    private List<String> selectedDaysOfWeekFilter = new ArrayList<>(); // Store selected days of week filter

    public ClassInstancesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_class_instances, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        classInstancesRecyclerView = view.findViewById(R.id.classInstancesRecyclerView);
        fabAddClassInstance = view.findViewById(R.id.fabAddClassInstance);
        searchEditTextTeacherName = view.findViewById(R.id.searchEditTextTeacherName);
        chipGroupFilterDaysOfWeek = view.findViewById(R.id.chipGroupFilterDaysOfWeek);
        textFilterDateDisplay = view.findViewById(R.id.textFilterDateDisplay);

        classInstanceViewModel = new ViewModelProvider(this).get(ClassInstanceViewModel.class);
        classViewModel = new ViewModelProvider(this).get(ClassViewModel.class);

        classInstancesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        classInstanceAdapter = new ClassInstanceAdapter(new ArrayList<>(), classInstanceViewModel, classViewModel, getContext());
        classInstancesRecyclerView.setAdapter(classInstanceAdapter);

        observeGroupedClassInstances(); // Observe grouped class instances - observe ONCE in onViewCreated/onResume

        fabAddClassInstance.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), GenerateClassInstanceActivity.class); // You might want to use a dedicated "AddClassInstanceActivity" if you create one later
            startActivity(intent);
        });

        // Set listeners for Search, Day of Week Filter, Date Filter - Implement logic in next steps
        searchEditTextTeacherName.addTextChangedListener(new TextWatcher() { // Add TextWatcher for dynamic search
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // No action needed before text changes
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                searchText = charSequence.toString().trim(); // Update searchText
                applyFilters(); // Apply filters immediately on text change - Just call applyFilters to update criteria
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // No action needed after text changes
            }
        });


        chipGroupFilterDaysOfWeek.setOnCheckedStateChangeListener((group, checkedIds) -> {
            selectedDaysOfWeekFilter = getSelectedDaysOfWeekFilter(); // Update selected days filter
            applyFilters(); // Apply filters when day of week selection changes - Just call applyFilters to update criteria
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

    private void observeGroupedClassInstances() { // Observe grouped class instances - Observe ONCE in onViewCreated/onResume
        classInstanceViewModel.getDateGroupedClassInstances().observe(getViewLifecycleOwner(), dateGroupedClassInstances -> {
            Log.d("ClassInstancesFragment", "dateGroupedClassInstances observed: " + dateGroupedClassInstances);
            for (DateGroupedClassInstance groupedItem : dateGroupedClassInstances) {
                for (ClassInstanceItem item : groupedItem.getItems()) {
                    Log.d("ClassInstancesFragment", "ClassInstanceItem: " + item.getAdditionalComments());
                }
            }
            if (dateGroupedClassInstances != null) {
                classInstanceAdapter.setClassInstanceItems(dateGroupedClassInstances); // Update adapter with grouped data
            }
        });
    }


    private void applyFilters() { // Modified - now just updates filter criteria
        Log.d("ClassInstancesFragment", "applyFilters: Method called");
        // No longer observing LiveData here - just updating filter criteria
        classInstanceViewModel.setSearchTextFilter(searchText); // Update search text filter in ViewModel
        classInstanceViewModel.setSelectedDaysOfWeekFilter(selectedDaysOfWeekFilter); // Update day of week filter
        classInstanceViewModel.setFilterDateString(filterDateString); // Update date filter
        // The Transformation in ViewModel will now automatically re-apply filters and update dateGroupedClassInstances LiveData
        classInstanceViewModel.applyFiltersAndGroupData(); // Explicitly refresh grouped data
    }

    private List<DateGroupedClassInstance> groupClassInstancesByDate(List<ClassInstanceItem> classInstanceItems) { // Group class instances by date - moved to ViewModel
        // Moved to ClassInstanceViewModel - groupClassInstancesByDate method is now in ViewModel
        return classInstanceViewModel.groupClassInstancesByDate(classInstanceItems);
    }


    private void showDatePickerDialog() {
        int year = filterDateCalendar.get(Calendar.YEAR);
        int month = filterDateCalendar.get(Calendar.MONTH);
        int day = filterDateCalendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, yearSelected, monthOfYear, dayOfMonth) -> {
                    filterDateCalendar.set(Calendar.YEAR, yearSelected);
                    filterDateCalendar.set(Calendar.MONTH, monthOfYear);
                    filterDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateDisplay();
                    applyFilters(); // Apply filters when date is selected - Just call applyFilters to update criteria
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


    @Override
    public void onResume() {
        super.onResume();
        Log.d("ClassInstancesFragment", "onResume: Method called");
        if (classInstanceViewModel != null && classInstanceAdapter != null) {
            observeGroupedClassInstances();
        }
    }
}