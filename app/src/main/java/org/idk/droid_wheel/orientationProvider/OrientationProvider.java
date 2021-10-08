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
import java.util.ArrayList;
import java.util.List;
import org.idk.droid_wheel.representation.MatrixF4x4;
import org.idk.droid_wheel.representation.Quaternion;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
public abstract class OrientationProvider implements SensorEventListener {
    protected final Object synchronizationToken = new Object();
    protected List<Sensor> sensorList = new ArrayList<Sensor>();
    protected final MatrixF4x4 currentOrientationRotationMatrix;
    protected final Quaternion currentOrientationQuaternion;
    protected SensorManager sensorManager;
    public OrientationProvider(SensorManager sensorManager) {
        this.sensorManager = sensorManager;
        currentOrientationRotationMatrix = new MatrixF4x4();
        currentOrientationQuaternion = new Quaternion();
    }
    public void start() {
        for (Sensor sensor : sensorList) {
            sensorManager.registerListener(this, sensor,
                    SensorManager.SENSOR_DELAY_GAME);
        }
    }
    public void stop() {
        for (Sensor sensor : sensorList) {
            sensorManager.unregisterListener(this, sensor);
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
    public void getRotationMatrix(MatrixF4x4 matrix) {
        synchronized (synchronizationToken) {
            matrix.set(currentOrientationRotationMatrix);
        }
    }
    public void getQuaternion(Quaternion quaternion) {
        synchronized (synchronizationToken) {
            quaternion.set(currentOrientationQuaternion);
        }
    }
    public void getEulerAngles(float angles[]) {
        synchronized (synchronizationToken) {
            SensorManager.getOrientation(currentOrientationRotationMatrix.matrix, angles);
        }
    }
}
