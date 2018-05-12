package info.redclass.locationtracker;

import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.IBinder;
import android.view.View;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

public class LocalLocationService extends Service implements LocationAssistant.Listener {
    private final IBinder mBinder = new MyBinder();
    private LocationAssistant assistant;
    private Location mCurrentLocation;


    @Override
    public void onNeedLocationPermission() {

    }

    @Override
    public void onExplainLocationPermission() {

    }

    @Override
    public void onLocationPermissionPermanentlyDeclined(View.OnClickListener fromView, DialogInterface.OnClickListener fromDialog) {

    }

    @Override
    public void onNeedLocationSettingsChange() {

    }

    @Override
    public void onFallBackToSystemSettings(View.OnClickListener fromView, DialogInterface.OnClickListener fromDialog) {

    }

    @Override
    public void onNewLocationAvailable(Location location) {
        mCurrentLocation = location;

        if (mCurrentLocation.hasAccuracy() && mCurrentLocation.getAccuracy() <= 20) {
            String deviceID = "1";
            String accuracy = String.valueOf(mCurrentLocation.getAccuracy()).toString();
            //String accuracy = "2";
            String urlLocation = "http://redclass.info/DeviceData/SubmitDeviceLocationData/" + deviceID + "/" + String.valueOf(mCurrentLocation.getLatitude()) + "/" + String.valueOf(mCurrentLocation.getLongitude()) + "/" + String.valueOf(mCurrentLocation.getBearing()) + "/" + accuracy + "/" + "2018-01-01";

            new SendLocationDataToServerImmediatelyTask().execute(urlLocation);
            //mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            //mLastUpdateTimeObj = new Date();
            //updateUI();
        }
    }

    @Override
    public void onMockLocationsDetected(View.OnClickListener fromView, DialogInterface.OnClickListener fromDialog) {

    }

    @Override
    public void onError(LocationAssistant.ErrorType type, String message) {

    }

    public class MyBinder extends Binder {
        LocalLocationService getService() {
            return LocalLocationService.this;
        }
    }


    public LocalLocationService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (assistant == null)
        {
            assistant = new LocationAssistant(this, this, LocationAssistant.Accuracy.HIGH, 1000, false);
            assistant.start();
        }
        return mBinder;
    }
}
