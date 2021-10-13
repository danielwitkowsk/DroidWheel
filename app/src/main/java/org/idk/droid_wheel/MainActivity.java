package org.idk.droid_wheel;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    public static UDPWorker client = new UDPWorker();

    protected void onCreate(Bundle savedInstanceState) {

        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ReadThemeMode();
        final EditText ip = (EditText)this.findViewById(R.id.ipaddr);
        final EditText port = (EditText)this.findViewById(R.id.port);
        final Button submit = (Button)this.findViewById(R.id.submit);
        LoadPreferences("saved_ip",ip);
        LoadPreferences("saved_port",port);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ipaddr;
                String portstr;
                ipaddr = ip.getText().toString();
                Log.d("k",String.valueOf(ipaddr));
                portstr = port.getText().toString();
                SavePreferences("saved_ip", ipaddr);
                SavePreferences("saved_port", portstr);
                client.setHost(ipaddr);
                client.setPort(Integer.parseInt(portstr));
                Intent myIntent = new Intent(MainActivity.this, postStart.class);
                startActivity(myIntent);
            }
        });
    }
    private void SavePreferences(String key, String value) {
        SharedPreferences sharedPreferences = getSharedPreferences("SHARED_SETTINGS",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }
    private void LoadPreferences(String key, EditText view){
        SharedPreferences sharedPreferences = getSharedPreferences("SHARED_SETTINGS",MODE_PRIVATE);
        String loaded_str = sharedPreferences.getString(key, "");
        view.setText(loaded_str);
    }

    private void ReadThemeMode() {
        int nightModeFlags =this.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                SavePreferences("night_mode","on");
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                SavePreferences("night_mode","off");
                break;
        }
    }
}
