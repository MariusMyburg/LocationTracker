package info.redclass.locationtracker;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

import info.redclass.locationtracker.DB.Event;
import info.redclass.locationtracker.DB.EventDao;
import info.redclass.locationtracker.DB.Guard;
import info.redclass.locationtracker.DB.GuardDao;


public class RedclassRepository {

    private GuardDao mGuardDao;
    private EventDao mEventDao;

    private List<Event> mAllEvents;

    public GuardDao getGuardDao() { return mGuardDao; }
    public EventDao getEventDao() { return mEventDao; }


    RedclassRepository(Application application) {
        RedclassRoomDatabase db = RedclassRoomDatabase.getDatabase(application);
        mGuardDao = db.guardDao();
        mEventDao = db.eventDao();
        mAllEvents = mEventDao.getAll();
    }



    public void insert (Guard guard) {
        new insertGuardAsyncTask(mGuardDao).execute(guard);
    }
    public void insert (Event event) {
        new insertEventAsyncTask(mEventDao).execute(event);
    }

    private static class insertGuardAsyncTask extends AsyncTask<Guard, Void, Void> {

        private GuardDao mAsyncTaskDao;

        insertGuardAsyncTask(GuardDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Guard... params) {
            mAsyncTaskDao.insertAll(params[0]);
            return null;
        }
    }

    private static class insertEventAsyncTask extends AsyncTask<Event, Void, Void> {

        private EventDao mAsyncTaskDao;

        insertEventAsyncTask(EventDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Event... params) {
            mAsyncTaskDao.insertAll(params[0]);
            return null;
        }
    }
}
