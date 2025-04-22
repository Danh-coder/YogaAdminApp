package com.example.yogaadmin_uioptimized.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.yogaadmin_uioptimized.data.model.ClassEntity;

import java.util.List;

@Dao
public interface ClassDao {
    @Insert
    void insert(ClassEntity classEntity);

    @Update
    void update(ClassEntity classEntity);

    @Query("DELETE FROM classes WHERE classId = :classId")
    void delete(int classId);

    @Query("SELECT * FROM classes")
    LiveData<List<ClassEntity>> getAllClasses();

    @Query("SELECT * FROM classes")
    List<ClassEntity> getAllClassesList();

    @Query("SELECT * FROM classes WHERE classId = :classId")
    ClassEntity getClassById(int classId);

    @Query("SELECT * FROM classes WHERE classId = :classId")
    LiveData<ClassEntity> getClassByIdLiveData(int classId);
}