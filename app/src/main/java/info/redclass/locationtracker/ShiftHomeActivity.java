package info.redclass.locationtracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ShiftHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shift_home);
    }

    @Override
    protected void onResume() {
        super.onResume();

        String guardCode = getIntent().getStringExtra("GuardCode");
        TextView txtOnShift = findViewById(R.id.txtOnShift);
        txtOnShift.setText("Guard on Shift: " + guardCode);
    }
}
