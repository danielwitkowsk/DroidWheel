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
import android.util.Log;
public class ImprovedOrientationSensor1Provider extends OrientationProvider {
    private static final float NS2S = 1.0f / 1000000000.0f;
    private final Quaternion deltaQuaternion = new Quaternion();
    private Quaternion quaternionGyroscope = new Quaternion();
    private Quaternion quaternionRotationVector = new Quaternion();
    private long timestamp;
    private static final double EPSILON = 0.1f;
    private double gyroscopeRotationVelocity = 0;
    private boolean positionInitialised = false;
    private int panicCounter;
    private static final float DIRECT_INTERPOLATION_WEIGHT = 0.005f;
    private static final float OUTLIER_THRESHOLD = 0.85f;
    private static final float OUTLIER_PANIC_THRESHOLD = 0.65f;
    private static final int PANIC_THRESHOLD = 60;
    final private float[] temporaryQuaternion = new float[4];
    final private Quaternion correctedQuaternion = new Quaternion();
    final private Quaternion interpolatedQuaternion = new Quaternion();
    public ImprovedOrientationSensor1Provider(SensorManager sensorManager) {
        super(sensorManager);
        sensorList.add(sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE));
        sensorList.add(sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR));
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getQuaternionFromVector(temporaryQuaternion, event.values);
            quaternionRotationVector.setXYZW(temporaryQuaternion[1], temporaryQuaternion[2], temporaryQuaternion[3], -temporaryQuaternion[0]);
            if (!positionInitialised) {
                quaternionGyroscope.set(quaternionRotationVector);
                positionInitialised = true;
            }
        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
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
                deltaQuaternion.multiplyByQuat(quaternionGyroscope, quaternionGyroscope);
                float dotProd = quaternionGyroscope.dotProduct(quaternionRotationVector);
                if (Math.abs(dotProd) < OUTLIER_THRESHOLD) {
                    if (Math.abs(dotProd) < OUTLIER_PANIC_THRESHOLD) {
                        panicCounter++;
                    }
                    setOrientationQuaternionAndMatrix(quaternionGyroscope);
                } else {
                    quaternionGyroscope.slerp(quaternionRotationVector, interpolatedQuaternion, DIRECT_INTERPOLATION_WEIGHT);
                    setOrientationQuaternionAndMatrix(interpolatedQuaternion);
                    quaternionGyroscope.copyVec4(interpolatedQuaternion);
                    panicCounter = 0;
                }
                if (panicCounter > PANIC_THRESHOLD) {
                    Log.d("Rotation Vector",
                            "Panic counter is bigger than threshold; this indicates a Gyroscope failure. Panic reset is imminent.");
                    if (gyroscopeRotationVelocity < 3) {
                        Log.d("Rotation Vector",
                                "Performing Panic-reset. Resetting orientation to rotation-vector value.");
                        setOrientationQuaternionAndMatrix(quaternionRotationVector);
                        quaternionGyroscope.copyVec4(quaternionRotationVector);
                        panicCounter = 0;
                    } else {
                        Log.d("Rotation Vector",
                                String.format(
                                        "Panic reset delayed due to ongoing motion (user is still shaking the device). Gyroscope Velocity: %.2f > 3",
                                        gyroscopeRotationVelocity));
                    }
                }
            }
            timestamp = event.timestamp;
            float[] values=new float[3];
            getEulerAngles(values);
            MainActivity.client.pre_send(values);
        }
    }
    private void setOrientationQuaternionAndMatrix(Quaternion quaternion) {
        correctedQuaternion.set(quaternion);
        correctedQuaternion.w(-correctedQuaternion.w());
        synchronized (synchronizationToken) {
            currentOrientationQuaternion.copyVec4(quaternion);
            SensorManager.getRotationMatrixFromVector(currentOrientationRotationMatrix.matrix, correctedQuaternion.array());
        }
    }
}
