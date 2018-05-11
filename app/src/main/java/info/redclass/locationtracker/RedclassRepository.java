package info.redclass.locationtracker;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;


public class RedclassRepository {

    private DB_GuardDao mWordDao;
    private List<DB_Guard> mAllWords;

    RedclassRepository(Application application) {
        RedclassRoomDatabase db = RedclassRoomDatabase.getDatabase(application);
        mWordDao = db.guardDao();
        mAllWords = mWordDao.getAll();
    }

    List<DB_Guard> getAllGuards() {
        return mAllWords;
    }


    public void insert (DB_Guard word) {
        new insertAsyncTask(mWordDao).execute(word);
    }

    private static class insertAsyncTask extends AsyncTask<DB_Guard, Void, Void> {

        private DB_GuardDao mAsyncTaskDao;

        insertAsyncTask(DB_GuardDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final DB_Guard... params) {
            mAsyncTaskDao.insertAll(params[0]);
            return null;
        }
    }
}
