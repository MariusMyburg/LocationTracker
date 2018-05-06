package info.redclass.locationtracker;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

public class ShiftHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shift_home);
    }

    @Override
    protected void onResume() {
        super.onResume();

        String guardName = getIntent().getStringExtra("GUARDNAME");
        TextView txtOnShift = findViewById(R.id.txtOnShift);
        txtOnShift.setText("Guard on Shift: " + guardName);

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        String formattedDate = df.format(c.getTime());
        TextView txtShiftStartDateTime = findViewById(R.id.txtShiftStartDateTime);
        txtShiftStartDateTime.setText("Shift Started: " + formattedDate);
    }

    public void OnStartPatrolButtonClicked(View view)
    {
        startPatrol();
    }

    public void OnEndShiftButtonClicked(View view)
    {
        String deviceID = Build.SERIAL;
        String guardCode = getIntent().getStringExtra("GUARDCODE");
        String lat = String.valueOf(0);//mCurrentLocation.getLatitude());
        String lng = String.valueOf(0);//mCurrentLocation.getLongitude());
        String accuracy = String.valueOf(0);//mCurrentLocation.getAccuracy());
        String eventType = "SHIFTEND";

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        String formattedDate = df.format(c.getTime());


        String urlLocation = "http://www.redclass.info/Event/SubmitEventData/" + formattedDate + "/" + deviceID + "/" + guardCode + "/" + lng + "/" + lat + "/" + accuracy + "/" + eventType;

        urlLocation = urlLocation.replace(" ", "%20");
        urlLocation = urlLocation.replace(":", "!");
        urlLocation = urlLocation.replace("http!", "http:");

        String response = null;
        try {
            response = new SendLocationDataToServerTask().execute(urlLocation).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        Toast.makeText(this, response, Toast.LENGTH_LONG).show();
        finish();
    }

    private void startPatronButtonClicked(View view)
    {
        startPatrol();
    }

    private void endPatronButtonClicked(View view)
    {
        //endPatrol();
    }

    private void startPatrol()
    {
        Intent shiftOnPatrolIntent = new Intent(this, ShiftOnPatrolActivity.class);
        startActivity(shiftOnPatrolIntent);
    }


}
