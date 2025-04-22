package com.example.yogaadmin_uioptimized.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;
import androidx.room.Upsert;

import com.example.yogaadmin_uioptimized.data.model.TeacherClassTypeCrossRef;
import com.example.yogaadmin_uioptimized.data.model.TeacherEntity;
import com.example.yogaadmin_uioptimized.data.model.TeacherWithClassTypes;

import java.util.List;

@Dao
public interface TeacherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(TeacherEntity teacher);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertTeacherClassTypeCrossRef(TeacherClassTypeCrossRef crossRef);

    @Upsert
    void upsertTeacherClassTypeCrossRef(TeacherClassTypeCrossRef crossRef);

    @Update
    void updateTeacher(TeacherEntity teacher);

    @Delete
    void deleteTeacher(TeacherEntity teacher);

    @Transaction
    @Query("SELECT * FROM teachers")
    LiveData<List<TeacherWithClassTypes>> getTeachersWithClassTypes();

    @Transaction
    @Query("SELECT * FROM teachers")
    List<TeacherWithClassTypes> getTeachersWithClassTypesList();

    @Query("SELECT * FROM teachers")
    LiveData<List<TeacherEntity>> getAllTeachers();

    @Query("SELECT * FROM teachers")
    List<TeacherEntity> getAllTeachersList();

    @Query("SELECT * FROM teachers WHERE teacherId = :teacherId")
    TeacherEntity getTeacherById(int teacherId);

    @Query("SELECT * FROM teachers WHERE teacherId = :teacherId")
    LiveData<TeacherEntity> getTeacherByIdLiveData(int teacherId);

    @Query("DELETE FROM TeacherClassTypeCrossRef WHERE teacherId = :teacherId")
    void deleteTeacherClassTypeCrossRefByTeacherId(int teacherId);

    @Query("SELECT * FROM TeacherClassTypeCrossRef WHERE teacherId = :teacherId")
    TeacherClassTypeCrossRef getTeacherClassTypeCrossRefByTeacherId(int teacherId);

    @Query("DELETE FROM TeacherClassTypeCrossRef WHERE teacherId = :teacherId AND classTypeId = :classTypeId")
    void deleteTeacherClassTypeCrossRefByTeacherIdAndClassTypeId(int teacherId, int classTypeId);
}