package org.idk.droid_wheel;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends Activity {
    public static UDPWorker client = new UDPWorker();

    protected void onCreate(Bundle savedInstanceState) {

        //getActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText ip = (EditText)this.findViewById(R.id.ipaddr);
        final EditText port = (EditText)this.findViewById(R.id.port);
        final Button submit = (Button)this.findViewById(R.id.submit);

        /*boolean isFilePresent = isFilePresent(this, "storage.txt");
        if(isFilePresent) {
            String contentString = read(this, "storage.txt");
            ip.setText(contentString);
        } else {
            boolean isFileCreated = create(this, "storage.txt", "{}");
            if(isFileCreated) {

            } else {

            }
        }
        */
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

    private String read(Context context, String fileName) {
        try {
            FileInputStream fis = context.openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (FileNotFoundException fileNotFound) {
            return null;
        } catch (IOException ioException) {
            return null;
        }
    }

    private boolean create(Context context, String fileName, String txtString){
        String FILENAME = "storage.txt";
        try {
            FileOutputStream fos = context.openFileOutput(fileName,Context.MODE_PRIVATE);
            if (txtString != null) {
                fos.write(txtString.getBytes());
            }
            fos.close();
            return true;
        } catch (FileNotFoundException fileNotFound) {
            return false;
        } catch (IOException ioException) {
            return false;
        }

    }

    public boolean isFilePresent(Context context, String fileName) {
        String path = context.getFilesDir().getAbsolutePath() + "/" + fileName;
        File file = new File(path);
        return file.exists();
    }

}
