package com.example.yogaadmin_uioptimized.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.yogaadmin_uioptimized.ClassItem;
import com.example.yogaadmin_uioptimized.data.ClassRepository;
import com.example.yogaadmin_uioptimized.data.model.ClassEntity;
import com.example.yogaadmin_uioptimized.data.model.TeacherClassTypeCrossRef;
import com.example.yogaadmin_uioptimized.data.model.TeacherEntity;
import com.example.yogaadmin_uioptimized.data.model.TeacherWithClassTypes;
import com.example.yogaadmin_uioptimized.data.TeacherRepository;

import java.util.List;

public class TeacherViewModel extends AndroidViewModel {
    private TeacherRepository repository;
    private LiveData<List<TeacherWithClassTypes>> allTeachersWithClassTypes;
    private LiveData<List<TeacherEntity>> allTeachers;

    public TeacherViewModel(Application application) {
        super(application);
        repository = new TeacherRepository(application);
        allTeachersWithClassTypes = repository.getAllTeachersWithClassTypes();
        allTeachers = repository.getAllTeachers();
    }

    public LiveData<List<TeacherWithClassTypes>> getAllTeachersWithClassTypes() {
        return allTeachersWithClassTypes;
    }

    public LiveData<List<TeacherEntity>> getAllTeachers() {
        return allTeachers;
    }

    public TeacherEntity getTeacherById(int teacherId) {
        return repository.getTeacherById(teacherId);
    }

    public LiveData<TeacherEntity> getTeacherByIdLiveData(int teacherId) { // New method - returns LiveData<TeacherEntity> - Add this method
        return repository.getTeacherByIdLiveData(teacherId);
    }

    public void getTeacherClassTypeCrossRefByTeacherId(int teacherId, TeacherRepository.GetTeacherClassTypeCrossRefCallback callback) {
        repository.getTeacherClassTypeCrossRefByTeacherId(teacherId, callback);
    }

    public void insert(TeacherEntity teacher, TeacherRepository.InsertTeacherCallback callback) {
        repository.insert(teacher, callback);
    }

    public void insertTeacherClassTypeCrossRef(TeacherClassTypeCrossRef crossRef) {
        repository.insertTeacherClassTypeCrossRef(crossRef);
    }
    public void upsertTeacherClassTypeCrossRef(TeacherClassTypeCrossRef crossRef) {
        repository.upsertTeacherClassTypeCrossRef(crossRef);
    }

    public void updateTeacher(TeacherEntity teacher, TeacherRepository.UpdateTeacherCallback callback) {
        repository.updateTeacher(teacher, callback);
    }

//    public void deleteTeacher(TeacherEntity teacher) {
//        repository.deleteTeacher(teacher);
//    }

    public void deleteTeacher(TeacherEntity teacher) {
        new TeacherViewModel.DeleteAsyncTask(repository, this).execute(teacher);
    }

    public void deleteTeacherClassTypeCrossRefByTeacherId(int teacherId) {
        repository.deleteTeacherClassTypeCrossRefByTeacherId(teacherId);
    }
    public void deleteTeacherClassTypeCrossRefByTeacherIdAndClassTypeId(int teacherId, int classTypeId) {
        repository.deleteTeacherClassTypeCrossRefByTeacherIdAndClassTypeId(teacherId, classTypeId);
    }

    @SuppressLint("StaticFieldLeak")
    private static class DeleteAsyncTask extends AsyncTask<TeacherEntity, Void, List<TeacherEntity>> {
        private TeacherRepository repository;
        private TeacherViewModel teacherViewModel;

        DeleteAsyncTask(TeacherRepository repository, TeacherViewModel teacherViewModel) {
            this.repository = repository;
            this.teacherViewModel = teacherViewModel;
        }

        @Override
        protected List<TeacherEntity> doInBackground(TeacherEntity... teacherEntities) {
            repository.deleteTeacher(teacherEntities[0]);
            Log.d("TeacherViewModel", "Deleted teacher: " + teacherEntities[0].name);
            // Re-fetch entities
            return repository.getAllTeachers().getValue();
        }

//        @Override
//        protected void onPostExecute(List<ClassItem> updatedClassItems) {
//            TeacherViewModel.allClassesMutable.postValue(updatedClassItems); // Update LiveData on main thread
//        }
    }
}