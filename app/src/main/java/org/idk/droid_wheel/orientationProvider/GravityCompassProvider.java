package org.idk.droid_wheel.orientationProvider;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

import org.idk.droid_wheel.MainActivity;

import java.nio.charset.StandardCharsets;

/**
 * The orientation provider that delivers the current orientation from the {@link Sensor#TYPE_GRAVITY
 * Gravity} and {@link Sensor#TYPE_MAGNETIC_FIELD Compass}.
 * 
 * @author Alexander Pacha
 * 
 */
public class GravityCompassProvider extends OrientationProvider {

    /**
     * Compass values
     */
    final private float[] magnitudeValues = new float[3];

    /**
     * Gravity values
     */
    final private float[] gravityValues = new float[3];

    /**
     * Inclination values
     */
    float[] inclinationValues = new float[16];

    /**
     * Initialises a new GravityCompassProvider
     * 
     * @param sensorManager The android sensor manager
     */
    public GravityCompassProvider(SensorManager sensorManager) {
        super(sensorManager);

        //Add the compass and the gravity sensor
        sensorList.add(sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY));
        sensorList.add(sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD));
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // we received a sensor event. it is a good practice to check
        // that we received the proper event
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magnitudeValues, 0, magnitudeValues.length);
        } else if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
            System.arraycopy(event.values, 0, gravityValues, 0, gravityValues.length);
        }

        if (magnitudeValues != null && gravityValues != null) {
            // Fuse gravity-sensor (virtual sensor) with compass
            SensorManager.getRotationMatrix(currentOrientationRotationMatrix.matrix, inclinationValues, gravityValues, magnitudeValues);
            // Transform rotation matrix to quaternion
            currentOrientationQuaternion.setRowMajor(currentOrientationRotationMatrix.matrix);
        }
        getEulerAngles(MainActivity.test);
        MainActivity.degree=(MainActivity.test[0]+Math.PI)*(360/(2*Math.PI));
        MainActivity.degree = (MainActivity.degree + MainActivity.offset)%360;
        if (MainActivity.degree < 0) MainActivity.degree = 360 +MainActivity.degree;
        MainActivity.img.setRotation(-(float)MainActivity.degree);
        byte[] mess = (String.valueOf((int)MainActivity.degree)).getBytes(StandardCharsets.UTF_8);
        MainActivity.client.send(mess);
    }
}
