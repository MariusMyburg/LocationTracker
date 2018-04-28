package info.redclass.locationtracker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;


class Constants
{
    static int REQUESTCODE_GUARDSTARTSHIFT = 1;
}

class SendLocationDataToServerTask extends AsyncTask<String, Void, String> {

    private Exception exception;

    protected String doInBackground(String... urls) {
        try {
            URL url = new URL(urls[0]);

            //URL url = new URL(urlLocation);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setConnectTimeout(5000);
            connection.setRequestMethod("GET");
            connection.connect();
            //int statusCode = connection.getResponseCode();
            String msg = connection.getResponseMessage();
            return msg;
        } catch (Exception e) {
            this.exception = e;

        } finally {
            //is.close();
        }

        return "GetError";
    }

    protected void onPostExecute() {
        // TODO: check this.exception
        // TODO: do something with the feed
    }
}


public class MainActivity extends AppCompatActivity
        implements LocationAssistant.Listener
{


    private String mCurrentShiftGuardCode = "";
    private Boolean bLocationAccuracyHasGoneBelow20Once = false;
    private PowerManager.WakeLock mOnPatrolWakeLock;
    private LocationAssistant assistant;

    private static final String TAG = "LocationActivity";
    private static final long INTERVAL = 1000 * 1;
    private static final long FASTEST_INTERVAL = 200;
    Button btnFusedLocation;
    TextView tvLocation;
    LocationRequest mLocationRequest;
    //GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    String mLastUpdateTime;
    Date mLastUpdateTimeObj;

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        assistant.onPermissionsUpdated(requestCode, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        assistant.onActivityResult(requestCode, resultCode);

        if (requestCode == Constants.REQUESTCODE_GUARDSTARTSHIFT)
        {
            if (resultCode == RESULT_OK) {
                // Get the code that the guard entered.
                String guardCode = data.getStringExtra("GUARDCODE");
                mCurrentShiftGuardCode = guardCode;
                Toast.makeText(this, guardCode, Toast.LENGTH_LONG).show();

                Intent shiftStartedIntent = new Intent(this, ShiftHomeActivity.class);
                shiftStartedIntent.putExtra("GuardCode", guardCode);
                startActivity(shiftStartedIntent);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        setContentView(R.layout.activity_main);
        tvLocation = (TextView) findViewById(R.id.tvLocation);

    //tvLocation.setKeepScreenOn(true);

    //this.startService()

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onStart() {
        super.onStart();

        //mGoogleApiClient.connect();

        /*if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
            Log.d(TAG, "Location update resumed .....................");
        }*/

        if (assistant == null)
        {
            assistant = new LocationAssistant(this, this, LocationAssistant.Accuracy.HIGH, 1000, false);
        }




    }



    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop fired ..............");
        //mGoogleApiClient.disconnect();
        //Log.d(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected());
    }



    private void updateUI() throws IOException {
        Log.d(TAG, "UI update initiated .............");

        if (null != mCurrentLocation) {

            String deviceID = Build.SERIAL;
            String accuracy = String.valueOf(mCurrentLocation.getAccuracy()).toString();
            String urlLocation = "http://redclass.info/DeviceData/SubmitDeviceLocationData/" + deviceID + "/" + String.valueOf(mCurrentLocation.getLatitude()) + "/" + String.valueOf(mCurrentLocation.getLongitude()) + "/" + String.valueOf(mCurrentLocation.getBearing()) + "/" + accuracy + "/" + "2018-01-01";

            if (bLocationAccuracyHasGoneBelow20Once == true || (mCurrentLocation.hasAccuracy() && mCurrentLocation.getAccuracy() <= 20)) {
                new SendLocationDataToServerTask().execute(urlLocation);


                if (tvLocation != null) {

                    String lat = String.valueOf(mCurrentLocation.getLatitude());
                    String lng = String.valueOf(mCurrentLocation.getLongitude());
                    tvLocation.setText("At Time: " + mLastUpdateTime + "\n" +
                            "Serial: " + deviceID + "\n" +
                            "Latitude: " + lat + "\n" +
                            "Longitude: " + lng + "\n" +
                            "Accuracy: " + mCurrentLocation.getAccuracy() + "\n" +
                            "Provider: " + mCurrentLocation.getProvider());
                }
            }else
            {
                tvLocation.setText("Not sending location due to insufficient accuracy.");
            }
        } else {
            Log.d(TAG, "location is null ...............");
        }

    }

    public void OnStartStopPatrolButtonClicked(View view)
    {
        if (((Button)view).getText().toString().equals("Start Patrol"))
        {
            startPatrol();
            ((Button)view).setText("End Patrol");
        }else
        {
            endPatrol();
            ((Button)view).setText("Start Patrol");
            tvLocation.setText("Not on patrol.");
        }
    }

    private void startPatronButtonClicked(View view)
    {
        startPatrol();
    }

    private void endPatronButtonClicked(View view)
    {
        endPatrol();
    }

    private void startPatrol()
    {
        PowerManager pm = (PowerManager)this.getSystemService(Context.POWER_SERVICE);
        mOnPatrolWakeLock = pm.newWakeLock(
                PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE,
                TAG);
        mOnPatrolWakeLock.acquire();

        assistant.start();

    }

    private void endPatrol()
    {
        assistant.stop();

        mOnPatrolWakeLock.release();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //stopLocationUpdates();
        assistant.stop();
    }

    /*protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, (com.google.android.gms.location.LocationListener) this);
        Log.d(TAG, "Location update stopped .......................");
    }*/

    @Override
    public void onResume() {
        super.onResume();
        /*if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
            Log.d(TAG, "Location update resumed .....................");
        }*/

        //Intent intent= new Intent(this, LocalLocationService.class);
        //bindService(intent, this, Context.BIND_AUTO_CREATE);

        //assistant.start();

        if (mCurrentShiftGuardCode == "")
        {
            Intent loginIntent = new Intent(this, GuardStartShiftActivity.class);
            startActivityForResult(loginIntent, Constants.REQUESTCODE_GUARDSTARTSHIFT);
        }
    }



    @Override
    public void onNewLocationAvailable(Location location) {
        mCurrentLocation = location;
        try {
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            mLastUpdateTimeObj = new Date();

            if (location.getAccuracy() <= 20)
            {
                bLocationAccuracyHasGoneBelow20Once = true;
            }

            updateUI();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMockLocationsDetected(View.OnClickListener fromView, DialogInterface.OnClickListener fromDialog) {

    }

    @Override
    public void onError(LocationAssistant.ErrorType type, String message) {

    }

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


}
