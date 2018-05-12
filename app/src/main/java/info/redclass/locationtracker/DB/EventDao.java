package info.redclass.locationtracker.DB;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface EventDao {
    @Query("SELECT * FROM event_table")
    List<Event> getAll();

    @Query("SELECT * FROM event_table WHERE id IN (:eventIds)")
    List<Event> loadAllByIds(int[] eventIds);

    @Query("SELECT * FROM event_table ORDER BY Id DESC LIMIT 1")
    Event getOldest(); // This will be called when the phone has an internet connection.

    @Insert
    void insertAll(Event... events);

    @Delete
    void delete(Event event);
}
