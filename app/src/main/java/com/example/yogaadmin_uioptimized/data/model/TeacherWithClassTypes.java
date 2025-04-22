package com.example.yogaadmin_uioptimized.data.model;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

public class TeacherWithClassTypes {
    @Embedded
    public TeacherEntity teacher;

    @Relation(
            parentColumn = "teacherId",
            entityColumn = "classTypeId",
            associateBy = @Junction(TeacherClassTypeCrossRef.class)
    )
    public List<ClassTypeEntity> classTypes;
}