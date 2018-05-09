package info.redclass.locationtracker;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface DB_GuardDao {
    @Query("SELECT * FROM guard_table")
    LiveData<List<DB_Guard>> getAll();

    @Query("SELECT * FROM guard_table WHERE id IN (:guardIds)")
    LiveData<List<DB_Guard>> loadAllByIds(int[] guardIds);

    @Query("SELECT * FROM guard_table WHERE name LIKE :name LIMIT 1")
    DB_Guard findByName(String name);

    @Insert
    void insertAll(DB_Guard... users);

    @Delete
    void delete(DB_Guard user);
}


