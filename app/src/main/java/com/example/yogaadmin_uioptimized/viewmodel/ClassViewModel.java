package com.example.yogaadmin_uioptimized.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModelProvider;

import com.example.yogaadmin_uioptimized.ClassItem;
import com.example.yogaadmin_uioptimized.data.ClassRepository;
import com.example.yogaadmin_uioptimized.data.ClassTypeRepository;
import com.example.yogaadmin_uioptimized.data.model.ClassEntity;
import com.example.yogaadmin_uioptimized.data.model.ClassTypeEntity;
import com.example.yogaadmin_uioptimized.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClassViewModel extends AndroidViewModel {
    private final ClassRepository repository;
    private final ClassTypeRepository classTypeRepository;
    private final FirebaseViewModel firebaseViewModel; // Add FirebaseViewModel
    private final Application application; // Add Application

    private final MutableLiveData<List<ClassItem>> allClassesMutable = new MutableLiveData<>();
    public final LiveData<List<ClassItem>> allClasses = allClassesMutable;
    private ExecutorService executorService = Executors.newFixedThreadPool(4);


    public ClassViewModel(Application application) {
        super(application);
        repository = new ClassRepository(application);
        classTypeRepository = new ClassTypeRepository(application);
        firebaseViewModel = new ViewModelProvider.AndroidViewModelFactory(application).create(FirebaseViewModel.class); // Initialize FirebaseViewModel
        this.application = application; // Initialize Application
        loadAllClasses();
    }

    private void loadAllClasses() {
        repository.getAllClasses().observeForever(classEntities -> {
            if (classEntities != null) {
                executorService.execute(() -> { // Perform conversion in background thread
                    List<ClassItem> classItems = convertToClassItemList(classEntities, classTypeRepository);
                    allClassesMutable.postValue(classItems); // Update LiveData with converted list
                });
            } else {
                allClassesMutable.postValue(Collections.emptyList());
            }
        });
    }

    public LiveData<List<ClassItem>> getAllClasses() {
        return allClasses;
    }

    public ClassEntity getClassById(int classId) {
        return repository.getClassById(classId);
    }

    public LiveData<ClassEntity> getClassByIdLiveData(int classId) {
        return repository.getClassByIdLiveData(classId);
    }

    public LiveData<ClassItem> getClassItemByIdLiveData(int classId) { // New method to fetch LiveData<ClassItem> - UPDATED for async ClassType fetch
        return Transformations.switchMap(repository.getClassByIdLiveData(classId), classEntity -> { // Outer switchMap - observe ClassEntity LiveData
            if (classEntity == null) {
                return new MutableLiveData<>(null); // Return LiveData<null> if classEntity is null
            }
            return Transformations.map(classTypeRepository.getClassTypeById(classEntity.classTypeId), classTypeEntity -> { // Inner map - observe ClassTypeEntity LiveData
                String classTypeName = "Unknown Type";
                if (classTypeEntity != null) {
                    classTypeName = classTypeEntity.name;
                }
                return new ClassItem( // Create ClassItem object
                        classEntity.classId,
                        Arrays.asList(classEntity.daysOfWeek.split(",")),
                        classEntity.time,
                        classEntity.capacity,
                        classEntity.duration,
                        classEntity.price,
                        classEntity.classTypeId,
                        classEntity.description,
                        classEntity.level,
                        classEntity.room,
                        classTypeName
                );
            });
        });
    }

    @SuppressLint("StaticFieldLeak")
    public void saveClass(
            List<String> daysOfWeek,
            String time,
            int capacity,
            int duration,
            double price,
            int classTypeId,
            String description,
            String level,
            String room
    ) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                String daysOfWeekString = String.join(",", daysOfWeek);

                ClassEntity newClass = new ClassEntity(
                        daysOfWeekString,
                        time,
                        capacity,
                        duration,
                        price,
                        classTypeId,
                        description,
                        level,
                        room
                );
                repository.insertClass(newClass);
                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
                // Trigger Firebase upload after local save
                if (NetworkUtils.isNetworkConnected(application)) { // Check network
                    firebaseViewModel.uploadClasses(); // Call Firebase upload
                    Log.d("ClassViewModel", "saveClass: Firebase upload triggered");
                } else {
                    Log.w("ClassViewModel", "saveClass: No network, Firebase upload skipped");
                    showNoNetworkToast(); // Show no network message
                }
                loadAllClasses(); // Refresh local LiveData
            }
        }.execute();
    }

    public void update(ClassEntity classEntity) {
        new UpdateAsyncTask(repository, this).execute(classEntity);
    }

    public void delete(ClassEntity classEntity) {
        new DeleteAsyncTask(repository, this).execute(classEntity);
    }

    private List<ClassItem> convertToClassItemList(List<ClassEntity> classEntities, ClassTypeRepository classTypeRepository) {
        List<ClassItem> classItems = new ArrayList<>();
        if (classEntities == null) {
            return classItems; // Return empty list if classEntities is null
        }
        for (ClassEntity entity : classEntities) {
            List<String> daysOfWeek = Arrays.asList(entity.daysOfWeek.split(","));
            String classTypeName = "Unknown Type";

            // Synchronously get ClassTypeEntity (This should now be safe as it's in background thread)
            ClassTypeEntity classTypeEntity = classTypeRepository.getClassTypeByIdSync(entity.classTypeId); // Use synchronous get method
            if (classTypeEntity != null) {
                classTypeName = classTypeEntity.name;
            }

            ClassItem classItem = new ClassItem(
                    entity.classId,
                    daysOfWeek,
                    entity.time,
                    entity.capacity,
                    entity.duration,
                    entity.price,
                    entity.classTypeId,
                    entity.description,
                    entity.level,
                    entity.room,
                    classTypeName
            );
            classItems.add(classItem);
        }
        return classItems;
    }


    @SuppressLint("StaticFieldLeak")
    private static class DeleteAsyncTask extends AsyncTask<ClassEntity, Void, List<ClassItem>> {
        private ClassRepository repository;
        private ClassViewModel classViewModel;

        DeleteAsyncTask(ClassRepository repository, ClassViewModel classViewModel) {
            this.repository = repository;
            this.classViewModel = classViewModel;
        }

        @Override
        protected List<ClassItem> doInBackground(ClassEntity... classEntities) {
            repository.delete(classEntities[0]);
            Log.d("ClassViewModel", "Deleted class: " + classEntities[0].classId);
            List<ClassEntity> updatedClassEntities = repository.getAllClassesList(); // Re-fetch entities
            return classViewModel.convertToClassItemList(updatedClassEntities, classViewModel.classTypeRepository); // Convert in background
        }

        @Override
        protected void onPostExecute(List<ClassItem> updatedClassItems) {
            super.onPostExecute(updatedClassItems);
            classViewModel.allClassesMutable.postValue(updatedClassItems); // Update LiveData on main thread

            // Trigger Firebase upload after local delete
            if (NetworkUtils.isNetworkConnected(classViewModel.application)) { // Check network
                classViewModel.firebaseViewModel.uploadClasses(); // Call Firebase upload
                Log.d("ClassViewModel", "delete: Firebase upload triggered");
            } else {
                Log.w("ClassViewModel", "delete: No network, Firebase upload skipped");
                classViewModel.showNoNetworkToast(); // Show no network message
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private static class UpdateAsyncTask extends AsyncTask<ClassEntity, Void, Void> {
        private ClassRepository repository;
        private ClassViewModel classViewModel;

        UpdateAsyncTask(ClassRepository repository, ClassViewModel classViewModel) {
            this.repository = repository;
            this.classViewModel = classViewModel;
        }

        @Override
        protected Void doInBackground(ClassEntity... classEntities) {
            repository.update(classEntities[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            // Trigger Firebase upload after local update
            if (NetworkUtils.isNetworkConnected(classViewModel.application)) { // Check network using viewmodel's application context
                classViewModel.firebaseViewModel.uploadClasses(); // Call Firebase upload using viewmodel's firebaseViewModel
                Log.d("ClassViewModel", "UpdateAsyncTask: Firebase upload triggered");
            } else {
                Log.w("ClassViewModel", "UpdateAsyncTask: No network, Firebase upload skipped");
                classViewModel.showNoNetworkToast(); // Use viewmodel's showNoNetworkToast
            }
            classViewModel.loadAllClasses(); // Refresh local LiveData using viewmodel's method
        }
    }

    private void showNoNetworkToast() { // Helper method to show no network toast
        Toast.makeText(getApplication(), "No network connection. Data not synced to cloud.", Toast.LENGTH_LONG).show();
    }
}