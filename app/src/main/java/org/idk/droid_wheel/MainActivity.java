package org.idk.droid_wheel;
import android.content.Intent;
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

public class MainActivity extends Activity {
    public static UDPWorker client = new UDPWorker();

    protected void onCreate(Bundle savedInstanceState) {
        //getActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText ip = (EditText)this.findViewById(R.id.ipaddr);
        final EditText port = (EditText)this.findViewById(R.id.port);
        final Button submit = (Button)this.findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ipaddr;
                String portstr;
                ipaddr = ip.getText().toString();
                Log.d("k",String.valueOf(ipaddr));
                portstr = port.getText().toString();
                client.setHost(ipaddr);
                client.setPort(Integer.parseInt(portstr));
                Intent myIntent = new Intent(MainActivity.this, postStart.class);
                startActivity(myIntent);
            }
        });


    }



}
