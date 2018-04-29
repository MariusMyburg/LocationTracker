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
        String guardCode = guardCodeText.getText().toString();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        String formattedDate = df.format(c.getTime());

        String urlLocation = "http://redclass.info/ShiftData/SubmitShiftStartData/" + deviceID + "/" + guardCode  + "/?StartTime=" + formattedDate;
        urlLocation = urlLocation.replace(" ", "%20");

        try {
            String response = new SendLocationDataToServerTask().execute(urlLocation).get();

            Toast.makeText(this, response, Toast.LENGTH_LONG).show();

            if (response == "Success")
            {
                Intent intent = new Intent();

                intent.putExtra("GUARDCODE", guardCode);
                setResult(RESULT_OK, intent);
                finish();
            }else // "There is no guard with such code."
            {
                Intent intent = new Intent();
                intent.putExtra("GUARDCODE", "");
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


    }
}
