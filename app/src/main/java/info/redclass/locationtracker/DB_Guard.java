package info.redclass.locationtracker;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "guard_table")
public class DB_Guard {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")

    private int uid;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "guardcode")
    private String guardCode;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGuardCode() {
        return guardCode;
    }

    public void setGuardCode(String guardCode) {
        this.guardCode = guardCode;
    }
}
