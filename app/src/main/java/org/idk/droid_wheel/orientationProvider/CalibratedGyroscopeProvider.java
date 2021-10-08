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
import org.idk.droid_wheel.MainActivity;
import org.idk.droid_wheel.representation.Quaternion;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
public class CalibratedGyroscopeProvider extends OrientationProvider {
    private static final float NS2S = 1.0f / 1000000000.0f;
    private final Quaternion deltaQuaternion = new Quaternion();
    private long timestamp;
    private static final double EPSILON = 0.1f;
    private double gyroscopeRotationVelocity = 0;
    private Quaternion correctedQuaternion = new Quaternion();
    public CalibratedGyroscopeProvider(SensorManager sensorManager) {
        super(sensorManager);
        sensorList.add(sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE));
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            if (timestamp != 0) {
                final float dT = (event.timestamp - timestamp) * NS2S;
                float axisX = event.values[0];
                float axisY = event.values[1];
                float axisZ = event.values[2];
                gyroscopeRotationVelocity = Math.sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ);
                if (gyroscopeRotationVelocity > EPSILON) {
                    axisX /= gyroscopeRotationVelocity;
                    axisY /= gyroscopeRotationVelocity;
                    axisZ /= gyroscopeRotationVelocity;
                }
                double thetaOverTwo = gyroscopeRotationVelocity * dT / 2.0f;
                double sinThetaOverTwo = Math.sin(thetaOverTwo);
                double cosThetaOverTwo = Math.cos(thetaOverTwo);
                deltaQuaternion.setX((float) (sinThetaOverTwo * axisX));
                deltaQuaternion.setY((float) (sinThetaOverTwo * axisY));
                deltaQuaternion.setZ((float) (sinThetaOverTwo * axisZ));
                deltaQuaternion.setW(-(float) cosThetaOverTwo);
                synchronized (synchronizationToken) {
                    deltaQuaternion.multiplyByQuat(currentOrientationQuaternion, currentOrientationQuaternion);
                }
                correctedQuaternion.set(currentOrientationQuaternion);
                correctedQuaternion.w(-correctedQuaternion.w());
                synchronized (synchronizationToken) {
                    SensorManager.getRotationMatrixFromVector(currentOrientationRotationMatrix.matrix,
                            correctedQuaternion.array());
                }
            }
            timestamp = event.timestamp;
            float[] values=new float[3];
            getEulerAngles(values);
            MainActivity.client.pre_send(values);
        }
    }
}
