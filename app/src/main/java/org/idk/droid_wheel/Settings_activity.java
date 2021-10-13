package org.idk.droid_wheel;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class Settings_activity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_window);
        getSupportActionBar().hide();
        Button toggle_darkmode_btn = (Button)this.findViewById(R.id.toggle_darkmode_btn);
        Button choose_sensor_btn = (Button)this.findViewById(R.id.choose_sensor_btn);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(0.85*width),(int)(0.65*height));
        toggle_darkmode_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                int current_theme = LoadDarkMode();
                if (current_theme==1) {
                    editor.putString("night_mode", "off");
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                else if (current_theme==0) {
                    editor.putString("night_mode", "on");
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
                editor.commit();
                recreate();
            }
        });
        choose_sensor_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(Settings_activity.this);
                builderSingle.setTitle("Select Sensor");

                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Settings_activity.this, android.R.layout.select_dialog_singlechoice);
                arrayAdapter.add("Accelerometer and Compass");
                arrayAdapter.add("Gravity and Compass");
                arrayAdapter.add("Improved Orientation 1");
                arrayAdapter.add("Rotation Vector");
                arrayAdapter.add("Calibrated Gyroscope");
                arrayAdapter.add("Improved Orientation 2");
                builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = arrayAdapter.getItem(which);
                        SavePreferences("current_sensor", which+1);
                        Toast.makeText(Settings_activity.this, "Exit settings to apply",Toast.LENGTH_LONG).show();
                    }
                });
                builderSingle.show();
            }
        });
    }
    private void SavePreferences(String key, int value) {
        SharedPreferences sharedPreferences = getSharedPreferences("SHARED_SETTINGS",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    private int LoadDarkMode() {
        SharedPreferences sharedPreferences = getSharedPreferences("SHARED_SETTINGS",MODE_PRIVATE);
        String loaded_str = sharedPreferences.getString("night_mode", "");
        if(loaded_str.equals("on")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            return 1;
        }
        else if (loaded_str.equals("off")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            return 0;
        }
        return 0;
    }
}
