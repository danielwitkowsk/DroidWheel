package org.idk.droid_wheel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import org.idk.droid_wheel.orientationProvider.AccelerometerCompassProvider;
import org.idk.droid_wheel.orientationProvider.GravityCompassProvider;
import org.idk.droid_wheel.orientationProvider.ImprovedOrientationSensor1Provider;
import org.idk.droid_wheel.orientationProvider.RotationVectorProvider;
import org.idk.droid_wheel.orientationProvider.CalibratedGyroscopeProvider;
import org.idk.droid_wheel.orientationProvider.OrientationProvider;
import org.idk.droid_wheel.orientationProvider.ImprovedOrientationSensor2Provider;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class postStart extends AppCompatActivity {
    SensorManager mSensorManager;
    public static double offset=0;
    private OrientationProvider current_provider;
    public static ImageView img;
    private TextView curr_sens;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wheel_main);
        getSupportActionBar().hide();
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Button less10 = (Button)this.findViewById(R.id.less10);
        Button less1 = (Button)this.findViewById(R.id.less1);
        Button more10 = (Button)this.findViewById(R.id.more10);
        Button more1 = (Button)this.findViewById(R.id.more1);
        curr_sens = (TextView)this.findViewById(R.id.curr_sens);
        FloatingActionButton open_settings = (FloatingActionButton) this.findViewById(R.id.open_settings);
        img = (ImageView) this.findViewById(R.id.wheel);
        current_provider=new ImprovedOrientationSensor2Provider(mSensorManager);
        current_provider.start();
        curr_sens.setText("Improved Orientation 2");

        open_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(postStart.this,Settings_activity.class));
            }
        });

        more10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                offset=offset+10;
            }
        });
        more1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                offset=offset+1;
            }
        });
        less1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                offset=offset-1;
            }
        });
        less10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                offset=offset-10;
            }
        });


    }
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private int LoadPreferences(String key){
        SharedPreferences sharedPreferences = getSharedPreferences("SHARED_SETTINGS",MODE_PRIVATE);
        int result = sharedPreferences.getInt(key,0);
        return result;
    }

    @Override
    public void onResume() {
        super.onResume();
        current_provider.stop();
        switch (LoadPreferences("current_sensor")) {
            case 1:
                current_provider= new AccelerometerCompassProvider(mSensorManager);
                curr_sens.setText("Accelerometer and Compass");
                break;
            case 2:
                current_provider= new GravityCompassProvider(mSensorManager);
                curr_sens.setText("Gravity and Compass");
                break;
            case 3:
                current_provider= new ImprovedOrientationSensor1Provider(mSensorManager);
                curr_sens.setText("Improved Orientation 1");
                break;
            case 4:
                current_provider= new RotationVectorProvider(mSensorManager);
                curr_sens.setText("Rotation Vector");
                break;
            case 5:
                current_provider= new CalibratedGyroscopeProvider(mSensorManager);
                curr_sens.setText("Calibrated Gyroscope");
                break;
            case 6:
                current_provider= new ImprovedOrientationSensor2Provider(mSensorManager);
                curr_sens.setText("Improved Orientation 2");
                break;
        }
        current_provider.start();
    }

}
