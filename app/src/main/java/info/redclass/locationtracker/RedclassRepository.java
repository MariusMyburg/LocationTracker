package info.redclass.locationtracker;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

import info.redclass.locationtracker.DB.Guard;
import info.redclass.locationtracker.DB.GuardDao;


public class RedclassRepository {

    private GuardDao mWordDao;
    private List<Guard> mAllWords;

    RedclassRepository(Application application) {
        RedclassRoomDatabase db = RedclassRoomDatabase.getDatabase(application);
        mWordDao = db.guardDao();
        mAllWords = mWordDao.getAll();
    }

    List<Guard> getAllGuards() {
        return mAllWords;
    }


    public void insert (Guard word) {
        new insertAsyncTask(mWordDao).execute(word);
    }

    private static class insertAsyncTask extends AsyncTask<Guard, Void, Void> {

        private GuardDao mAsyncTaskDao;

        insertAsyncTask(GuardDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Guard... params) {
            mAsyncTaskDao.insertAll(params[0]);
            return null;
        }
    }
}
