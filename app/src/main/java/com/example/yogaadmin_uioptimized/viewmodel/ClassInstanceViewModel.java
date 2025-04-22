package com.example.yogaadmin_uioptimized.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

import com.example.yogaadmin_uioptimized.ClassInstanceItem;
import com.example.yogaadmin_uioptimized.ClassItem;
import com.example.yogaadmin_uioptimized.Converters;
import com.example.yogaadmin_uioptimized.DateGroupedClassInstance;
import com.example.yogaadmin_uioptimized.data.ClassInstanceRepository;
import com.example.yogaadmin_uioptimized.data.ClassRepository;
import com.example.yogaadmin_uioptimized.data.TeacherRepository;
import com.example.yogaadmin_uioptimized.data.model.ClassEntity;
import com.example.yogaadmin_uioptimized.data.model.ClassInstanceEntity;
import com.example.yogaadmin_uioptimized.data.model.TeacherEntity;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ClassInstanceViewModel extends AndroidViewModel {
    private ClassInstanceRepository classInstanceRepository;
    private ClassRepository classRepository;
    private TeacherRepository teacherRepository;
    private MutableLiveData<List<ClassInstanceItem>> searchResults = new MutableLiveData<>();
    private MutableLiveData<List<ClassInstanceItem>> allClassInstanceItemsMutable = new MutableLiveData<>();
    private LiveData<List<ClassInstanceItem>> allClassInstanceItems = allClassInstanceItemsMutable;
    private MediatorLiveData<List<DateGroupedClassInstance>> dateGroupedClassInstances = new MediatorLiveData<>();
    private ExecutorService executorService = Executors.newFixedThreadPool(4);
    private MediatorLiveData<List<ClassInstanceItem>> allClassInstanceItemsMediator = new MediatorLiveData<>();
    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // --- Filter Criteria LiveData ---
    private MutableLiveData<String> searchTextFilter = new MutableLiveData<>("");
    private MutableLiveData<List<String>> selectedDaysOfWeekFilterLiveData = new MutableLiveData<>(Collections.emptyList());
    private MutableLiveData<String> filterDateStringLiveData = new MutableLiveData<>("");


    public ClassInstanceViewModel(Application application) {
        super(application);
        classInstanceRepository = new ClassInstanceRepository(application);
        classRepository = new ClassRepository(application);
        teacherRepository = new TeacherRepository(application);

        // Initialize MediatorLiveData to observe repository LiveData
        allClassInstanceItemsMediator.addSource(classInstanceRepository.getAllClassInstanceItems(), classInstanceItemsFromRepo -> {
            Log.d("ClassInstanceViewModel", "allClassInstanceItemsMediator - classInstanceRepository.getAllClassInstanceItems() source changed");
            if (classInstanceItemsFromRepo != null) {
                allClassInstanceItemsMutable.postValue(classInstanceItemsFromRepo);
            }
        });

        allClassInstanceItemsMediator.addSource(allClassInstanceItemsMutable, updatedList -> {
            Log.d("ClassInstanceViewModel", "allClassInstanceItemsMediator source changed");
            if (updatedList != null) {
                recomputeGroupedClassInstances(updatedList);
            }
        });


        // --- Transformations to create filtered and grouped LiveData ---
        dateGroupedClassInstances.addSource(allClassInstanceItems, classInstanceItems -> {
            Log.d("ClassInstanceViewModel", "dateGroupedClassInstances - allClassInstanceItems source changed");

            if (classInstanceItems == null) {
                dateGroupedClassInstances.setValue(Collections.emptyList());
                return;
            }

            executorService.execute(() -> {
                List<DateGroupedClassInstance> result = groupClassInstancesByDate(applyFiltersToItems(classInstanceItems));
                dateGroupedClassInstances.postValue(result); // Post result to LiveData safely
            });
        });

        // Observe filter criteria LiveData - Add these new addSource blocks:
        dateGroupedClassInstances.addSource(searchTextFilter, searchText -> {
            Log.d("ClassInstanceViewModel", "dateGroupedClassInstances - searchTextFilter source changed: " + searchText);
            applyFiltersAndGroupData();
        });

        dateGroupedClassInstances.addSource(selectedDaysOfWeekFilterLiveData, selectedDaysOfWeek -> {
            Log.d("ClassInstanceViewModel", "dateGroupedClassInstances - selectedDaysOfWeekFilterLiveData source changed: " + selectedDaysOfWeek);
            applyFiltersAndGroupData();
        });

        dateGroupedClassInstances.addSource(filterDateStringLiveData, filterDate -> {
            Log.d("ClassInstanceViewModel", "dateGroupedClassInstances - filterDateStringLiveData source changed: " + filterDate);
            applyFiltersAndGroupData();
        });

        allClassInstanceItems = allClassInstanceItemsMediator;

        loadInitialClassInstanceItems();
    }

    private void recomputeGroupedClassInstances(List<ClassInstanceItem> updatedList) {
        executorService.execute(() -> {
            List<DateGroupedClassInstance> groupedData = groupClassInstancesByDate(updatedList);
            dateGroupedClassInstances.postValue(groupedData);
        });
    }

    private void loadInitialClassInstanceItems() {
        Log.d("ClassInstanceViewModel", "loadInitialClassInstanceItems: Method STARTING");
        Log.d("ClassInstanceViewModel", "loadInitialClassInstanceItems: Calling classInstanceRepository.getAllClassInstanceItems()");

        LiveData<List<ClassInstanceItem>> repoLiveData = classInstanceRepository.getAllClassInstanceItems();

        Observer<List<ClassInstanceItem>> observer = new Observer<List<ClassInstanceItem>>() {
            @Override
            public void onChanged(List<ClassInstanceItem> initialClassInstanceItems) {
                Log.d("ClassInstanceViewModel", "loadInitialClassInstanceItems: Observer TRIGGERED");
                Log.d("ClassInstanceViewModel", "loadInitialClassInstanceItems: initialClassInstanceItems from Repo: " + initialClassInstanceItems);
                if (initialClassInstanceItems != null) {
                    allClassInstanceItemsMutable.postValue(initialClassInstanceItems);
                }
//                repoLiveData.removeObserver(this);
            }
        };

        repoLiveData.observeForever(observer);
    }


    public LiveData<List<DateGroupedClassInstance>> getDateGroupedClassInstances() {
        Log.d("ClassInstanceViewModel", "getDateGroupedClassInstances: Method called");
        loadInitialClassInstanceItems();

        // Check if LiveData has been initialized with a value
        if (dateGroupedClassInstances.getValue() == null) {
            Log.d("ClassInstanceViewModel", "getDateGroupedClassInstances: No data available yet");
            return dateGroupedClassInstances;
        }

        // Debugging logs
        for (DateGroupedClassInstance item : dateGroupedClassInstances.getValue()) {
            for (ClassInstanceItem classInstanceItem : item.getItems()) {
                Log.d("ClassInstanceViewModel", "ClassInstanceItem: " + classInstanceItem.getAdditionalComments());
            }
        }

        Log.d("ClassInstanceViewModel", "getDateGroupedClassInstances: Calling loadInitialClassInstanceItems()");
        loadInitialClassInstanceItems();
        Log.d("ClassInstanceViewModel", "getDateGroupedClassInstances: Returning LiveData");
        return dateGroupedClassInstances;
    }

    public List<ClassInstanceItem> applyFiltersToItems(List<ClassInstanceItem> classInstanceItems) {
        Log.d("ClassInstanceViewModel", "applyFiltersToItems: Method called");
        List<ClassInstanceItem> filteredList = new ArrayList<>(classInstanceItems);

        // Apply Teacher Name Search
        String searchText = searchTextFilter.getValue();
        if (searchText != null && !searchText.isEmpty()) {
            filteredList.removeIf(item -> !item.getTeacherName().toLowerCase().contains(searchText.toLowerCase()));
        }

        // Apply Day of Week Filter
        List<String> selectedDaysOfWeekFilter = selectedDaysOfWeekFilterLiveData.getValue();
        if (selectedDaysOfWeekFilter != null && !selectedDaysOfWeekFilter.isEmpty()) {
            filteredList.removeIf(item -> {
                Calendar instanceDate = Calendar.getInstance();
                try {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                    LocalDate instanceDateO = LocalDate.parse(item.getDate(), formatter);
                    DayOfWeek dayOfWeek = instanceDateO.getDayOfWeek();
                    String dayOfWeekString = dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault());


                    boolean dayMatch = false;
                    for (String filterDay : selectedDaysOfWeekFilter) {
                        if (dayOfWeekString.equalsIgnoreCase(filterDay)) {
                            dayMatch = true;
                            break;
                        }
                    }
                    return !dayMatch;
                } catch (Exception e) {
                    return true;
                }
            });
        }

        // Apply Date Filter
        String filterDate = filterDateStringLiveData.getValue();
        if (filterDate != null && !filterDate.isEmpty()) {
            filteredList.removeIf(item -> !item.getDate().equals(filterDate));
        }

        return filteredList;
    }

    public void applyFiltersAndGroupData() {
        executorService.execute(() -> {
            List<ClassInstanceItem> allItems = allClassInstanceItemsMutable.getValue();
            if (allItems != null) {
                List<DateGroupedClassInstance> filteredGroupedList = groupClassInstancesByDate(applyFiltersToItems(allItems));
                dateGroupedClassInstances.postValue(filteredGroupedList);
            } else {
                dateGroupedClassInstances.postValue(Collections.emptyList());
            }
        });
    }

    public List<DateGroupedClassInstance> groupClassInstancesByDate(List<ClassInstanceItem> classInstanceItems) {
        Log.d("ClassInstanceViewModel", "groupClassInstancesByDate: Method called");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        HashMap<String, DateGroupedClassInstance> groupedMap = new HashMap<>();
        DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("EEEE", Locale.getDefault());

        for (ClassInstanceItem item : classInstanceItems) {
            String dateStr = item.getDate();
            LocalDate date = LocalDate.parse(dateStr, formatter);
            String dayOfWeek = date.format(displayFormatter);

            ClassEntity classEntity = classRepository.getClassById(item.classId); // Fetch ClassEntity synchronously to get time
            String classTime = "N/A"; // Default time if ClassEntity not found
            if (classEntity != null) {
                classTime = classEntity.time; // Get class time from ClassEntity
            }

            if (!groupedMap.containsKey(dateStr)) {
                groupedMap.put(dateStr, new DateGroupedClassInstance(dateStr, dayOfWeek, new ArrayList<>()));
            }
            groupedMap.get(dateStr).getItems().add(new ClassInstanceItem( // Update to create ClassInstanceItem with classTime
                    item.instanceId,
                    item.classId,
                    item.getDate(),
                    item.teacherId,
                    item.additionalComments,
                    item.classDescription,
                    item.teacherName,
                    classTime // Pass classTime to ClassInstanceItem constructor
            ));
        }

        List<DateGroupedClassInstance> groupedList = new ArrayList<>(groupedMap.values());
        Collections.sort(groupedList, (group1, group2) -> group1.getDate().compareTo(group2.getDate()));
        return groupedList;
    }


    public LiveData<List<ClassInstanceItem>> getAllClassInstancesItems() {
        return allClassInstanceItems;
    }

    public LiveData<List<ClassInstanceEntity>> getClassInstancesByClassId(int classId) {
        return classInstanceRepository.getClassInstancesByClassId(classId);
    }

    public LiveData<ClassInstanceEntity> getClassInstanceById(int instanceId) {
        return classInstanceRepository.getClassInstanceById(instanceId);
    }

    // --- Methods to update filter criteria ---
    public void setSearchTextFilter(String text) {
        searchTextFilter.setValue(text);
    }

    public void setSelectedDaysOfWeekFilter(List<String> daysOfWeek) {
        selectedDaysOfWeekFilterLiveData.setValue(daysOfWeek);
    }

    public void setFilterDateString(String dateString) {
        filterDateStringLiveData.setValue(dateString);
    }

    @SuppressLint("StaticFieldLeak")
    public void generateClassInstances(int classId, String startDateString, String endDateString, ClassEntity classEntity, int teacherId) { // Updated parameters
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                LocalDate startDate = null;
                LocalDate endDate = null;
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                startDate = LocalDate.parse(startDateString, formatter); // Parse start and end dates from strings
                endDate = LocalDate.parse(endDateString, formatter);

                List<String> daysOfWeek = Converters.fromString(classEntity.daysOfWeek);
                Log.d("ClassInstanceViewModel", "daysOfWeek: " + daysOfWeek);
                for (String day : daysOfWeek) {
                    Log.d("ClassInstanceViewModel", "daysOfWeek - day: '" + day + "'");
                }

                List<ClassInstanceEntity> newInstances = new ArrayList<>();

                for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                    DayOfWeek dayOfWeek = date.getDayOfWeek();
                    String dayOfWeekString = dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault());
                    Log.d("ClassInstanceViewModel", "Day of week - String: " + dayOfWeekString);
                    Log.d("ClassInstanceViewModel", "daysOfWeek.contains(dayOfWeekString): " + daysOfWeek.contains(dayOfWeekString));

                    if (daysOfWeek.contains(dayOfWeekString)) {
                        Log.d("ClassInstanceViewModel", "Days of week - Class: " + daysOfWeek);
                        Log.d("ClassInstanceViewModel", "Day of week - String: " + dayOfWeekString);
                        Log.d("ClassInstanceViewModel", "Day of week - Original: " + date);
                        String formattedDate = date.format(formatter);
                        ClassInstanceEntity newInstance = new ClassInstanceEntity(classId, formattedDate, teacherId, "");
                        newInstances.add(newInstance);
                    }
                }

                classInstanceRepository.insertClassInstances(newInstances);
                return null;
            }
        }.execute();
    }

    public List<ClassInstanceItem> getAllClassInstanceItemsList() { // New method - synchronous list fetch
        List<ClassInstanceEntity> classInstanceEntities = classInstanceRepository.getAllClassInstancesList();
        List<ClassInstanceItem> items = new ArrayList<>();
        if (classInstanceEntities != null) {
            for (ClassInstanceEntity entity : classInstanceEntities) {
                ClassEntity classEntity = classRepository.getClassById(entity.classId);
                TeacherEntity teacherEntity = teacherRepository.getTeacherById(entity.teacherId);
                String classTime = "N/A";
                if (classEntity != null) {
                    classTime = classEntity.time;
                }
                ClassInstanceItem item = new ClassInstanceItem(
                        entity.instanceId,
                        entity.classId,
                        entity.date,
                        entity.teacherId,
                        entity.additionalComments,
                        classEntity != null ? classEntity.description : "No Description",
                        teacherEntity != null ? teacherEntity.name : "No Teacher",
                        classTime
                );
                items.add(item);
            }
        }
        return items;
    }

    public void insert(ClassInstanceEntity classInstanceEntity) {
        new InsertClassInstanceAsyncTask(classInstanceRepository).execute(classInstanceEntity);
    }

    public void update(ClassInstanceEntity classInstanceEntity) {
        new UpdateClassInstanceAsyncTask(classInstanceRepository, this).execute(classInstanceEntity);
    }

    public void delete(ClassInstanceEntity classInstanceEntity) {
        new DeleteClassInstanceAsyncTask(classInstanceRepository).execute(classInstanceEntity);
    }


    @SuppressLint("StaticFieldLeak")
    private static class InsertClassInstanceAsyncTask extends AsyncTask<ClassInstanceEntity, Void, Void> {
        private ClassInstanceRepository classInstanceRepository;

        public InsertClassInstanceAsyncTask(ClassInstanceRepository classInstanceRepository) {
            this.classInstanceRepository = classInstanceRepository;
        }

        @Override
        protected Void doInBackground(ClassInstanceEntity... classInstanceEntities) {
            classInstanceRepository.insert(classInstanceEntities[0]);
            return null;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private static class UpdateClassInstanceAsyncTask extends AsyncTask<ClassInstanceEntity, Void, List<ClassInstanceItem>> { // Change Void to List<ClassInstanceItem>
        private ClassInstanceRepository classInstanceRepository;
        private ClassInstanceViewModel classInstanceViewModel; // Add ClassInstanceViewModel reference


        public UpdateClassInstanceAsyncTask(ClassInstanceRepository classInstanceRepository, ClassInstanceViewModel classInstanceViewModel) { // Take ClassInstanceViewModel in constructor
            this.classInstanceRepository = classInstanceRepository;
            this.classInstanceViewModel = classInstanceViewModel; // Initialize viewmodel
        }

        @Override
        protected List<ClassInstanceItem> doInBackground(ClassInstanceEntity... classInstanceEntities) {
            classInstanceRepository.update(classInstanceEntities[0]);
            Log.d("ClassInstanceViewModel", "UpdateClassInstanceAsyncTask: doInBackground - Class instance updated in DB"); // Log in doInBackground
            return classInstanceViewModel.getAllClassInstanceItemsList(); // Re-fetch all items after update
        }

        @Override
        protected void onPostExecute(List<ClassInstanceItem> updatedClassInstanceItems) { // Change Void to List<ClassInstanceItem>
            Log.d("ClassInstanceViewModel", "UpdateClassInstanceAsyncTask: onPostExecute - Updating LiveData"); // Log in onPostExecute
            Log.d("ClassInstanceViewModel", "UpdateClassInstanceAsyncTask: onPostExecute - updatedClassInstanceItems: " + updatedClassInstanceItems); // Log in onPostExecute)
            if (classInstanceViewModel.allClassInstanceItems instanceof MutableLiveData) {
                ((MutableLiveData<List<ClassInstanceItem>>) classInstanceViewModel.allClassInstanceItems).postValue(updatedClassInstanceItems); // Update LiveData with re-fetched list
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private static class DeleteClassInstanceAsyncTask extends AsyncTask<ClassInstanceEntity, Void, Void> {
        private ClassInstanceRepository classInstanceRepository;

        public DeleteClassInstanceAsyncTask(ClassInstanceRepository classInstanceRepository) {
            this.classInstanceRepository = classInstanceRepository;
        }

        @Override
        protected Void doInBackground(ClassInstanceEntity... classInstanceEntities) {
            Log.d("DeleteClassInstanceAsyncTask", "Deleting class instance: " + classInstanceEntities[0]);
            classInstanceRepository.delete(classInstanceEntities[0]);
            Log.d("DeleteClassInstanceAsyncTask", "Deleted class instance: " + classInstanceEntities[0]);
            return null;
        }
    }
}
