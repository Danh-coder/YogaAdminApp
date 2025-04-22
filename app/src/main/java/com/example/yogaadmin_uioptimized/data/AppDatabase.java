package com.example.yogaadmin_uioptimized.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.yogaadmin_uioptimized.data.model.ClassEntity;
import com.example.yogaadmin_uioptimized.data.model.ClassInstanceEntity;
import com.example.yogaadmin_uioptimized.data.model.ClassTypeEntity;
import com.example.yogaadmin_uioptimized.data.model.TeacherClassTypeCrossRef;
import com.example.yogaadmin_uioptimized.data.model.TeacherEntity;

@Database(entities = {ClassEntity.class, ClassInstanceEntity.class, TeacherEntity.class, ClassTypeEntity.class, TeacherClassTypeCrossRef.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ClassDao classDao();
    public abstract ClassInstanceDao classInstanceDao();
    public abstract TeacherDao teacherDao();
    public abstract ClassTypeDao classTypeDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "yoga_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}