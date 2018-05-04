package info.redclass.locationtracker;

import android.arch.persistence.room.Room;
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

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
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

    protected String doInBackground(String... data) {
        try {
            URL url = new URL(data[0]);

            if (data.length == 1) { // Normal GET
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setConnectTimeout(5000);
                connection.setRequestMethod("GET");
                connection.connect();
                //int statusCode = connection.getResponseCode();
                String msg = connection.getResponseMessage();
                return msg;
            }else if (data.length == 2) // POST
            {
                String postdata = data[1]; //data to post

                OutputStream out = null;


                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                out = new BufferedOutputStream(urlConnection.getOutputStream());
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));

                writer.write(postdata);

                writer.flush();

                writer.close();

                out.close();

                urlConnection.connect();
                //String message = urlConnection.getResponseMessage();

                return "Photo uploaded.";
            }
        } catch (Exception e) {
            this.exception = e;

            return e.toString();

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
        //implements LocationAssistant.Listener
{

    public DB_AppDatabase mAppDatabase = Room.databaseBuilder(getApplicationContext(),
            DB_AppDatabase.class, "redclass_database").build();

    private String mCurrentShiftGuardCode = "";
    private Boolean bLocationAccuracyHasGoneBelow20Once = false;
    private PowerManager.WakeLock mOnPatrolWakeLock;
    //private LocationAssistant assistant;

    private static final String TAG = "LocationActivity";
    private static final long INTERVAL = 1000 * 1;
    private static final long FASTEST_INTERVAL = 200;
    Button btnFusedLocation;
    //TextView tvLocation;
    LocationRequest mLocationRequest;
    //GoogleApiClient mGoogleApiClient;
    //Location mCurrentLocation;
    //String mLastUpdateTime;
    //Date mLastUpdateTimeObj;

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //assistant.onPermissionsUpdated(requestCode, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //assistant.onActivityResult(requestCode, resultCode);

        if (requestCode == Constants.REQUESTCODE_GUARDSTARTSHIFT)
        {
            if (resultCode == RESULT_OK) {
                // Get the code that the guard entered.
                String guardName = data.getStringExtra("GUARDNAME");
                String guardCode = data.getStringExtra("GUARDCODE");
                mCurrentShiftGuardCode = guardCode;
                Toast.makeText(this, guardCode, Toast.LENGTH_LONG).show();

                Intent shiftStartedIntent = new Intent(this, ShiftHomeActivity.class);
                shiftStartedIntent.putExtra("GUARDNAME", guardName);
                shiftStartedIntent.putExtra("GUARDCODE", guardCode);
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
    }



    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop fired ..............");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //stopLocationUpdates();
        //assistant.stop();
    }

    @Override
    public void onResume() {
        super.onResume();
    }



    public void OnStartShiftButtonClicked(View view)
    {
        //if (mCurrentShiftGuardCode == "")
        {
            Intent loginIntent = new Intent(this, GuardStartShiftActivity.class);
            startActivityForResult(loginIntent, Constants.REQUESTCODE_GUARDSTARTSHIFT);
        }
    }



}
