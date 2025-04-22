package com.example.yogaadmin_uioptimized.data.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "class_instances",
        foreignKeys = {
                @ForeignKey(entity = ClassEntity.class,
                        parentColumns = "classId",
                        childColumns = "classId",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = TeacherEntity.class,
                        parentColumns = "teacherId",
                        childColumns = "teacherId",
                        onDelete = ForeignKey.SET_NULL)
        })
public class ClassInstanceEntity {
    @PrimaryKey(autoGenerate = true)
    public int instanceId;

    public int classId;
    public String date;
    public int teacherId;
    public String additionalComments;

    public ClassInstanceEntity(int classId, String date, int teacherId, String additionalComments) {
        this.classId = classId;
        this.date = date;
        this.teacherId = teacherId;
        this.additionalComments = additionalComments;
    }

//    public static class Converters {
//        private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//
//        @TypeConverter
//        public static LocalDate fromString(String value) {
//            return value == null ? null : LocalDate.parse(value, formatter);
//        }
//
//        @TypeConverter
//        public static String dateToString(LocalDate date) {
//            return date == null ? null : date.format(formatter);
//        }
//    }
}
