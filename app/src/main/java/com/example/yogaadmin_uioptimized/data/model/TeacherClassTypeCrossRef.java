package com.example.yogaadmin_uioptimized.data.model;

import androidx.room.Entity;

@Entity(primaryKeys = {"teacherId", "classTypeId"})
public class TeacherClassTypeCrossRef {
    public int teacherId;
    public int classTypeId;

    public TeacherClassTypeCrossRef(int teacherId, int classTypeId) {
        this.teacherId = teacherId;
        this.classTypeId = classTypeId;
    }
}