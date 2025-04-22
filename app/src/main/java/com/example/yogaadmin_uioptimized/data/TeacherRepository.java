package com.example.yogaadmin_uioptimized.data;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.yogaadmin_uioptimized.data.model.TeacherClassTypeCrossRef;
import com.example.yogaadmin_uioptimized.data.model.TeacherEntity;
import com.example.yogaadmin_uioptimized.data.model.TeacherWithClassTypes;

import java.util.List;

public class TeacherRepository {
    private TeacherDao teacherDao;
    private LiveData<List<TeacherWithClassTypes>> allTeachersWithClassTypes;
    private LiveData<List<TeacherEntity>> allTeachers;

    public TeacherRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        teacherDao = db.teacherDao();
        allTeachersWithClassTypes = teacherDao.getTeachersWithClassTypes();
        allTeachers = teacherDao.getAllTeachers();
    }

    public LiveData<List<TeacherWithClassTypes>> getAllTeachersWithClassTypes() {
        return allTeachersWithClassTypes;
    }

    public LiveData<List<TeacherEntity>> getAllTeachers() {
        return allTeachers;
    }

    public List<TeacherEntity> getAllTeachersList() {
        return teacherDao.getAllTeachersList();
    }

    public TeacherEntity getTeacherById(int teacherId) {
        return teacherDao.getTeacherById(teacherId);
    }

    public LiveData<TeacherEntity> getTeacherByIdLiveData(int teacherId) {
        return teacherDao.getTeacherByIdLiveData(teacherId);
    }

    public void getTeacherClassTypeCrossRefByTeacherId(int teacherId, GetTeacherClassTypeCrossRefCallback callback) {
        new GetTeacherClassTypeCrossRefAsyncTask(teacherDao, callback).execute(teacherId);
    }

    public void insert(TeacherEntity teacher, InsertTeacherCallback callback) {
        new InsertTeacherAsyncTask(teacherDao, callback).execute(teacher);
    }

    public void insertTeacherClassTypeCrossRef(TeacherClassTypeCrossRef crossRef) {
        new InsertTeacherClassTypeCrossRefAsyncTask(teacherDao).execute(crossRef);
    }
    public void upsertTeacherClassTypeCrossRef(TeacherClassTypeCrossRef crossRef) {
        new UpsertTeacherClassTypeCrossRefAsyncTask(teacherDao).execute(crossRef);
    }

    public void updateTeacher(TeacherEntity teacher, UpdateTeacherCallback callback) {
        new UpdateTeacherAsyncTask(teacherDao, callback).execute(teacher);
    }

    public void deleteTeacher(TeacherEntity teacher) {
        new DeleteTeacherAsyncTask(teacherDao).execute(teacher);
    }

    public void deleteTeacherClassTypeCrossRefByTeacherId(int teacherId) {
        new DeleteTeacherClassTypeCrossRefAsyncTask(teacherDao).execute(teacherId);
    }
    public void deleteTeacherClassTypeCrossRefByTeacherIdAndClassTypeId(int teacherId, int classTypeId) {
        new DeleteTeacherClassTypeCrossRefByTeacherIdAndClassTypeIdAsyncTask(teacherDao).execute(teacherId, classTypeId);
    }

    public LiveData<List<TeacherWithClassTypes>> getTeachersWithClassTypes() {
        return teacherDao.getTeachersWithClassTypes();
    }

    public List<TeacherWithClassTypes> getTeachersWithClassTypesList() {
        return teacherDao.getTeachersWithClassTypesList();
    }

    private static class InsertTeacherAsyncTask extends AsyncTask<TeacherEntity, Void, Long> {
        private TeacherDao teacherDao;
        private InsertTeacherCallback callback;

        InsertTeacherAsyncTask(TeacherDao dao, InsertTeacherCallback callback) {
            teacherDao = dao;
            this.callback = callback;
        }

        @Override
        protected Long doInBackground(final TeacherEntity... params) {
            long teacherId = teacherDao.insert(params[0]);
            return teacherId;
        }

        @Override
        protected void onPostExecute(Long teacherId) {
            if (callback != null) {
                callback.onTeacherInserted(teacherId);
            }
        }
    }

    private static class InsertTeacherClassTypeCrossRefAsyncTask extends AsyncTask<TeacherClassTypeCrossRef, Void, Void> {
        private TeacherDao teacherDao;

        InsertTeacherClassTypeCrossRefAsyncTask(TeacherDao dao) {
            teacherDao = dao;
        }

        @Override
        protected Void doInBackground(final TeacherClassTypeCrossRef... params) {
            teacherDao.insertTeacherClassTypeCrossRef(params[0]);
            return null;
        }
    }
    private static class UpsertTeacherClassTypeCrossRefAsyncTask extends AsyncTask<TeacherClassTypeCrossRef, Void, Void> {
        private TeacherDao teacherDao;

        UpsertTeacherClassTypeCrossRefAsyncTask(TeacherDao dao) {
            teacherDao = dao;
        }

        @Override
        protected Void doInBackground(final TeacherClassTypeCrossRef... params) {
            teacherDao.upsertTeacherClassTypeCrossRef(params[0]);
            return null;
        }
    }

    private static class UpdateTeacherAsyncTask extends AsyncTask<TeacherEntity, Void, Long> {
        private TeacherDao teacherDao;
        private UpdateTeacherCallback callback;

        UpdateTeacherAsyncTask(TeacherDao dao, UpdateTeacherCallback callback) {
            teacherDao = dao;
            this.callback = callback;
        }

        @Override
        protected Long doInBackground(final TeacherEntity... params) {
            teacherDao.updateTeacher(params[0]);
            return (long) params[0].teacherId;
        }

        @Override
        protected void onPostExecute(Long teacherId) {
            if (callback != null) {
                callback.onTeacherUpdated(teacherId);
            }
        }
    }

    private static class DeleteTeacherAsyncTask extends AsyncTask<TeacherEntity, Void, Void> {
        private TeacherDao teacherDao;

        DeleteTeacherAsyncTask(TeacherDao dao) {
            teacherDao = dao;
        }

        @Override
        protected Void doInBackground(final TeacherEntity... params) {
            teacherDao.deleteTeacher(params[0]);
            return null;
        }
    }

    private static class DeleteTeacherClassTypeCrossRefAsyncTask extends AsyncTask<Integer, Void, Void> {
        private TeacherDao teacherDao;

        DeleteTeacherClassTypeCrossRefAsyncTask(TeacherDao dao) {
            teacherDao = dao;
        }

        @Override
        protected Void doInBackground(Integer... teacherIds) {
            teacherDao.deleteTeacherClassTypeCrossRefByTeacherId(teacherIds[0]);
            return null;
        }
    }
    private static class DeleteTeacherClassTypeCrossRefByTeacherIdAndClassTypeIdAsyncTask extends AsyncTask<Integer, Void, Void> {
        private TeacherDao teacherDao;

        DeleteTeacherClassTypeCrossRefByTeacherIdAndClassTypeIdAsyncTask(TeacherDao dao) {
            teacherDao = dao;
        }

        @Override
        protected Void doInBackground(Integer... params) {
            teacherDao.deleteTeacherClassTypeCrossRefByTeacherIdAndClassTypeId(params[0], params[1]);
            return null;
        }
    }
    private static class GetTeacherClassTypeCrossRefAsyncTask extends AsyncTask<Integer, Void, TeacherClassTypeCrossRef> {
        private TeacherDao teacherDao;
        private GetTeacherClassTypeCrossRefCallback callback;

        GetTeacherClassTypeCrossRefAsyncTask(TeacherDao dao, GetTeacherClassTypeCrossRefCallback callback) {
            teacherDao = dao;
            this.callback = callback;
        }

        @Override
        protected TeacherClassTypeCrossRef doInBackground(Integer... teacherIds) {
            return teacherDao.getTeacherClassTypeCrossRefByTeacherId(teacherIds[0]);
        }

        @Override
        protected void onPostExecute(TeacherClassTypeCrossRef crossRef) {
            if (callback != null) {
                callback.onTeacherClassTypeCrossRefLoaded(crossRef);
            }
        }
    }

    public interface InsertTeacherCallback {
        void onTeacherInserted(long teacherId);
    }
    public interface UpdateTeacherCallback {
        void onTeacherUpdated(long teacherId);
    }
    public interface GetTeacherClassTypeCrossRefCallback {
        void onTeacherClassTypeCrossRefLoaded(TeacherClassTypeCrossRef crossRef);
    }
}