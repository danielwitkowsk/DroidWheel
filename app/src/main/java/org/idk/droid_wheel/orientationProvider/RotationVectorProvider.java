package org.idk.droid_wheel.orientationProvider;
/*
Copyright (c) 2012-2021 Scott Chacon and others

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
"Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import org.idk.droid_wheel.MainActivity;
public class RotationVectorProvider extends OrientationProvider {
    final private float[] temporaryQuaternion = new float[4];
    public RotationVectorProvider(SensorManager sensorManager) {
        super(sensorManager);
        sensorList.add(sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR));
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(currentOrientationRotationMatrix.matrix, event.values);
            SensorManager.getQuaternionFromVector(temporaryQuaternion, event.values);
            currentOrientationQuaternion.setXYZW(temporaryQuaternion[1], temporaryQuaternion[2], temporaryQuaternion[3], -temporaryQuaternion[0]);
        }
        float[] values=new float[3];
        getEulerAngles(values);
        MainActivity.client.pre_send(values);
    }
}
