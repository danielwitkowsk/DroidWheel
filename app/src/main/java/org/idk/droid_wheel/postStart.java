package org.idk.droid_wheel;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.idk.droid_wheel.orientationProvider.AccelerometerCompassProvider;
import org.idk.droid_wheel.orientationProvider.GravityCompassProvider;
import org.idk.droid_wheel.orientationProvider.ImprovedOrientationSensor1Provider;
import org.idk.droid_wheel.orientationProvider.RotationVectorProvider;
import org.idk.droid_wheel.orientationProvider.CalibratedGyroscopeProvider;
import org.idk.droid_wheel.orientationProvider.OrientationProvider;
import org.idk.droid_wheel.orientationProvider.ImprovedOrientationSensor2Provider;
import android.view.MenuItem;
import android.widget.PopupMenu;
import android.app.UiModeManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class postStart extends  MainActivity{
    SensorManager mSensorManager;
    public static double offset=0;
    private OrientationProvider current_provider;
    public static ImageView img;
    private UiModeManager uiModeManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wheel_main);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        uiModeManager = (UiModeManager) getSystemService(UI_MODE_SERVICE);
        final Button less10 = (Button)this.findViewById(R.id.less10);
        final Button less1 = (Button)this.findViewById(R.id.less1);
        final Button more10 = (Button)this.findViewById(R.id.more10);
        final Button more1 = (Button)this.findViewById(R.id.more1);
        final FloatingActionButton sensor_choice = (FloatingActionButton) this.findViewById(R.id.sensor_choice);
        final TextView curr_sens = (TextView)this.findViewById(R.id.curr_sens);
        img = (ImageView) this.findViewById(R.id.wheel);

        current_provider=new ImprovedOrientationSensor2Provider(mSensorManager);
        current_provider.start();
        curr_sens.setText("Improved Orientation 2");

        sensor_choice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(postStart.this, sensor_choice);
                popupMenu.getMenuInflater().inflate(R.menu.choose_sensor, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        current_provider.stop();
                        int id= menuItem.getItemId();
                        switch (id) {
                            case R.id.acp:
                                current_provider= new AccelerometerCompassProvider(mSensorManager);
                                break;
                            case R.id.gcp:
                                current_provider= new GravityCompassProvider(mSensorManager);
                                break;
                            case R.id.imp1:
                                current_provider= new ImprovedOrientationSensor1Provider(mSensorManager);
                                break;
                            case R.id.rvp:
                                current_provider= new RotationVectorProvider(mSensorManager);
                                break;
                            case R.id.cgp:
                                current_provider= new CalibratedGyroscopeProvider(mSensorManager);
                                break;
                            case R.id.imp2:
                                current_provider= new ImprovedOrientationSensor2Provider(mSensorManager);
                                break;
                        }
                        curr_sens.setText(menuItem.getTitle());
                        current_provider.start();
                        return true;
                    }
                });
                popupMenu.show();
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


}
