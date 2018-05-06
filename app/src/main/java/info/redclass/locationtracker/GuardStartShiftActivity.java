package info.redclass.locationtracker;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

public class GuardStartShiftActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guard_start_shift);
    }


    public void onStartShiftButtonClicked(View view)
    {
        EditText guardCodeText = findViewById(R.id.guardCodeText);

        String deviceID = Build.SERIAL;
        String guardCode = guardCodeText.getText().toString(); //getIntent().getStringExtra("GUARDCODE");
        String lat = String.valueOf(0);//mCurrentLocation.getLatitude());
        String lng = String.valueOf(0);//mCurrentLocation.getLongitude());
        String accuracy = String.valueOf(0);//mCurrentLocation.getAccuracy());
        String eventType = "SHIFTSTART";

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        String formattedDate = df.format(c.getTime());


        String urlLocation = "http://www.redclass.info/Event/SubmitEventData/" + formattedDate + "/" + deviceID + "/" + guardCode + "/" + lng + "/" + lat + "/" + accuracy + "/" + eventType;
        urlLocation = urlLocation.replace(" ", "%20");
        urlLocation = urlLocation.replace(":", "!");
        urlLocation = urlLocation.replace("http!", "http:");

        try {
            String response = new SendLocationDataToServerTask().execute(urlLocation).get();

            Toast.makeText(this, response, Toast.LENGTH_LONG).show();

            if (response.startsWith("Guard Name: ")) // Guard Name: Klingon
            {
                Intent intent = new Intent();
                String guardName = response.substring(12);
                intent.putExtra("GUARDCODE", guardCode);
                intent.putExtra("GUARDNAME", guardName);
                setResult(RESULT_OK, intent);
                finish();
            }else if (response.equals("There is no guard with such code.")) // "There is no guard with such code."
            {
                Toast.makeText(this, response, Toast.LENGTH_LONG).show();
            }else if (response.equals("A shift has already been started, you cannot start a new one."))
            {
                //Toast.makeText(this, response + "\n" + "TODO end the shift?", Toast.LENGTH_LONG).show();

                //String deviceID = Build.SERIAL;
                //String guardCode = getIntent().getStringExtra("GUARDNAME");

                df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                //Calendar c = Calendar.getInstance();
                formattedDate = df.format(c.getTime());

                eventType = "SHIFTEND";
                urlLocation = "http://www.redclass.info/Event/SubmitEventData/" + formattedDate + "/" + deviceID + "/" + guardCode + "/" + lng + "/" + lat + "/" + accuracy + "/" + eventType;
                urlLocation = urlLocation.replace(" ", "%20");
                urlLocation = urlLocation.replace(":", "!");
                urlLocation = urlLocation.replace("http!", "http:");

                try {
                    response = new SendLocationDataToServerTask().execute(urlLocation).get();

                    onStartShiftButtonClicked(view);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


    }
}
