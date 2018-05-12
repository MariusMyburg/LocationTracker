package info.redclass.locationtracker.DB;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;


@Entity(tableName = "event_table")
public class Event {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    private int uid;

    @ColumnInfo(name = "datetime")
    private String datetime;

    @ColumnInfo(name = "guardcode")
    private String guardCode;

    @ColumnInfo(name = "latitude")
    private double latitude;

    @ColumnInfo(name = "longitude")
    private double longitude;

    @ColumnInfo(name = "accuracy")
    private double accuracy;

    @ColumnInfo(name = "eventtype")
    private String eventtype;

    @ColumnInfo(name = "photo")
    private String photo;


    public int getUid() {
        return uid;
    }
    public String getDatetime() {
        return datetime;
    }
    public String getGuardCode() {
        return guardCode;
    }
    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public double getAccuracy() {
        return accuracy;
    }
    public String getEventtype() {
        return eventtype;
    }
    public String getPhoto() {
        return photo;
    }

    public void setUid(int uid) { this.uid = uid; }
    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
    public void setGuardCode(String guardCode) {
        this.guardCode = guardCode;
    }

    public void setLatitude(double latitude) { this.latitude = latitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public void setAccuracy(double accuracy) { this.accuracy = accuracy; }
    public void setEventtype(String eventtype) { this.eventtype = eventtype; }
    public void setPhoto(String photo) { this.photo = photo; }

    // Roses are Red
    // Niggers are Black,
    // I'm a Cool Nerd
    // I Like to Hack
}