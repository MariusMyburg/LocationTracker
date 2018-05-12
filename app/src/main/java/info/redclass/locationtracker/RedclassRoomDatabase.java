package info.redclass.locationtracker;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import info.redclass.locationtracker.DB.Guard;
import info.redclass.locationtracker.DB.GuardDao;

@Database(entities = {Guard.class}, version = 1)
public abstract class RedclassRoomDatabase extends RoomDatabase
{
    public abstract GuardDao guardDao();

    private static RedclassRoomDatabase INSTANCE;

    public static RedclassRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (RedclassRoomDatabase.class) {
                if (INSTANCE == null) {
                    // Create database here
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            RedclassRoomDatabase.class, "redclass_database")
                            .addCallback(sRoomDatabaseCallback)
                            .build();

                    //new PopulateDbAsync(INSTANCE).execute();
                }
            }
        }
        return INSTANCE;
    }


    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final GuardDao mDao;

        PopulateDbAsync(RedclassRoomDatabase db) {
            mDao = db.guardDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            //mDao.deleteAll();
            Guard word = new Guard();
            word.setName("Nigger");
            word.setGuardCode("test");
            word.setUid(1);
            mDao.insertAll(word);
            return null;
        }
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback(){

                @Override
                public void onOpen (@NonNull SupportSQLiteDatabase db){
                    super.onOpen(db);
                    //new PopulateDbAsync(INSTANCE).execute();
                }
            };
}

