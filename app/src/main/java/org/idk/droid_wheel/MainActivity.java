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
import org.idk.droid_wheel.representation.MatrixF4x4;
import android.view.MenuItem;
import android.widget.PopupMenu;

public class MainActivity extends Activity {

    SensorManager mSensorManager;
    public static float[] test=new float[3];
    public static double offset=0;
    public static double degree;
    public static UDPWorker client = new UDPWorker();
    public MatrixF4x4 cos;
    private OrientationProvider current_provider;
    public static ImageView img;
    public static TextView testingtext;
    protected void onCreate(Bundle savedInstanceState) {
        getActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        final Button less10 = (Button)this.findViewById(R.id.less10);
        final Button less1 = (Button)this.findViewById(R.id.less1);
        final Button more10 = (Button)this.findViewById(R.id.more10);
        final Button more1 = (Button)this.findViewById(R.id.more1);

        final Button test_button = (Button)this.findViewById(R.id.test_button);
        final Button submit = (Button)this.findViewById(R.id.submit);
        final LinearLayout one = (LinearLayout)this.findViewById(R.id.startLayout);
        final LinearLayout two = (LinearLayout)this.findViewById(R.id.secondLayout);
        final EditText ip = (EditText)this.findViewById(R.id.ipaddr);
        final EditText port = (EditText)this.findViewById(R.id.port);
        final TextView curr_sens = (TextView)this.findViewById(R.id.curr_sens);
        img = (ImageView) this.findViewById(R.id.wheel);
        one.setVisibility(View.VISIBLE);
        two.setVisibility(View.INVISIBLE);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ipaddr;
                String portstr;
                two.setVisibility(View.VISIBLE);
                one.setVisibility(View.INVISIBLE);
                ipaddr = ip.getText().toString();
                Log.d("k",String.valueOf(ipaddr));
                portstr = port.getText().toString();
                client.setHost(ipaddr);
                client.setPort(Integer.parseInt(portstr));
                current_provider=new ImprovedOrientationSensor2Provider(mSensorManager);
                current_provider.start();
                curr_sens.setText("Improved Orientation 2");
            }
        });

        test_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(MainActivity.this, test_button);
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
