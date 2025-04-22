package com.example.yogaadmin_uioptimized.viewmodel;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.yogaadmin_uioptimized.data.ClassTypeRepository;
import com.example.yogaadmin_uioptimized.data.model.ClassTypeEntity;

import java.util.List;

public class ClassTypeViewModel extends AndroidViewModel {
    private ClassTypeRepository repository;
    private LiveData<List<ClassTypeEntity>> allClassTypes;

    public ClassTypeViewModel(@NonNull Application application) {
        super(application);
        repository = new ClassTypeRepository(application);
        allClassTypes = repository.getAllClassTypes();
    }

    public LiveData<List<ClassTypeEntity>> getAllClassTypes() {
        return allClassTypes;
    }

    public void insertClassType(ClassTypeEntity classTypeEntity) {
        new InsertClassTypeAsyncTask(repository).execute(classTypeEntity);
    }

    public void updateClassType(ClassTypeEntity classTypeEntity) {
        new UpdateClassTypeAsyncTask(repository).execute(classTypeEntity);
    }

    public void deleteClassType(ClassTypeEntity classTypeEntity) {
        new DeleteClassTypeAsyncTask(repository).execute(classTypeEntity);
    }

    public LiveData<ClassTypeEntity> getClassTypeById(int classTypeId) { // Modified to return LiveData
        return repository.getClassTypeById(classTypeId);
    }

    private static class InsertClassTypeAsyncTask extends AsyncTask<ClassTypeEntity, Void, Void> {
        private ClassTypeRepository repository;

        InsertClassTypeAsyncTask(ClassTypeRepository repository) {
            this.repository = repository;
        }

        @Override
        protected Void doInBackground(ClassTypeEntity... classTypeEntities) {
            repository.insertClassType(classTypeEntities[0]);
            return null;
        }
    }

    private static class UpdateClassTypeAsyncTask extends AsyncTask<ClassTypeEntity, Void, Void> {
        private ClassTypeRepository repository;

        UpdateClassTypeAsyncTask(ClassTypeRepository repository) {
            this.repository = repository;
        }

        @Override
        protected Void doInBackground(ClassTypeEntity... classTypeEntities) {
            repository.updateClassType(classTypeEntities[0]);
            return null;
        }
    }

    private static class DeleteClassTypeAsyncTask extends AsyncTask<ClassTypeEntity, Void, Void> {
        private ClassTypeRepository repository;

        DeleteClassTypeAsyncTask(ClassTypeRepository repository) {
            this.repository = repository;
        }

        @Override
        protected Void doInBackground(ClassTypeEntity... classTypeEntities) {
            repository.deleteClassType(classTypeEntities[0]);
            return null;
        }
    }
}