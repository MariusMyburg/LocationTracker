package info.redclass.locationtracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class GuardStartShiftActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guard_start_shift);
    }


    public void onStartShiftButtonClicked(View view)
    {
        Intent intent = new Intent();
        intent.putExtra("GUARDCODE", "1234");
        setResult(RESULT_OK, intent);
        finish();
    }
}
