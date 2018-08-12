package com.rollercoders.predoskysmsbombing;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class Settings extends AppCompatActivity {

    TextView seekbarValue;
    Button confirm;
    int progressChangedValue = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        seekbarValue = findViewById(R.id.seekbar_value);
        confirm = findViewById(R.id.confirm);

        getActionBar();

        Log.d("spamming_lag", String.valueOf(MainActivity.spammingLag));

        SeekBar spammingLag = findViewById(R.id.spamming_lag);
        spammingLag.setProgress(MainActivity.spammingLag-500);
        seekbarValue.setText(getResources().getString(R.string.seekbar_value, MainActivity.spammingLag));

        spammingLag.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress+500;
                seekbarValue.setText(getResources().getString(R.string.seekbar_value, progressChangedValue));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.spammingLag = progressChangedValue;
                Toast.makeText(Settings.this, R.string.settings_saved, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
