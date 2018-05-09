package info.redclass.locationtracker;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {DB_Guard.class}, version = 1)
public abstract class RedclassRoomDatabase extends RoomDatabase
{
    public abstract DB_GuardDao guardDao();

    private static RedclassRoomDatabase INSTANCE;

    public static RedclassRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (RedclassRoomDatabase.class) {
                if (INSTANCE == null) {
                    // Create database here
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            RedclassRoomDatabase.class, "redclass_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}

