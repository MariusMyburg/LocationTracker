package info.redclass.locationtracker;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {DB_Guard.class}, version = 1)
public abstract class DB_AppDatabase extends RoomDatabase {
    public abstract DB_GuardDao userDao();
}

