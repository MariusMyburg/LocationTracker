package info.redclass.locationtracker;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.concurrent.ExecutionException;

public class GuardStartShiftActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guard_start_shift);
    }


    public void onStartShiftButtonClicked(View view)
    {
        String deviceID = Build.SERIAL;
        String guardCode = "1234";
        String urlLocation = "http://redclass.info/ShiftData/SubmitShiftStartData/" + deviceID + "/" + guardCode;

        try {
            String response = new SendLocationDataToServerTask().execute(urlLocation).get();
            if (response == "Success")
            {
                Intent intent = new Intent();
                EditText guardCodeText = findViewById(R.id.guardCodeText);
                intent.putExtra("GUARDCODE", guardCodeText.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }else
            {
                Intent intent = new Intent();
                EditText guardCodeText = findViewById(R.id.guardCodeText);
                intent.putExtra("GUARDCODE", guardCodeText.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


    }
}
