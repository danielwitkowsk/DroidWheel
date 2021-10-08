package org.idk.droid_wheel.representation;


public class Quaternion extends Vector4f {


    private MatrixF4x4 matrix;


    private boolean dirty = false;

    private Vector4f tmpVector = new Vector4f();
    private Quaternion tmpQuaternion;


    public Quaternion() {
        super();
        matrix = new MatrixF4x4();
        loadIdentityQuat();
    }


    public void normalise() {
        this.dirty = true;
        float mag = (float) Math.sqrt(points[3] * points[3] + points[0] * points[0] + points[1] * points[1] + points[2]
                * points[2]);
        points[3] = points[3] / mag;
        points[0] = points[0] / mag;
        points[1] = points[1] / mag;
        points[2] = points[2] / mag;
    }

    @Override
    public void normalize() {
        normalise();
    }


    public void set(Quaternion quat) {
        this.dirty = true;
        copyVec4(quat);
    }


    public void multiplyByQuat(Quaternion input, Quaternion output) {

        if (input != output) {
            output.points[3] = (points[3] * input.points[3] - points[0] * input.points[0] - points[1] * input.points[1] - points[2]
                    * input.points[2]); //w = w1w2 - x1x2 - y1y2 - z1z2
            output.points[0] = (points[3] * input.points[0] + points[0] * input.points[3] + points[1] * input.points[2] - points[2]
                    * input.points[1]); //x = w1x2 + x1w2 + y1z2 - z1y2
            output.points[1] = (points[3] * input.points[1] + points[1] * input.points[3] + points[2] * input.points[0] - points[0]
                    * input.points[2]); //y = w1y2 + y1w2 + z1x2 - x1z2
            output.points[2] = (points[3] * input.points[2] + points[2] * input.points[3] + points[0] * input.points[1] - points[1]
                    * input.points[0]); //z = w1z2 + z1w2 + x1y2 - y1x2
        } else {
            tmpVector.points[0] = input.points[0];
            tmpVector.points[1] = input.points[1];
            tmpVector.points[2] = input.points[2];
            tmpVector.points[3] = input.points[3];

            output.points[3] = (points[3] * tmpVector.points[3] - points[0] * tmpVector.points[0] - points[1]
                    * tmpVector.points[1] - points[2] * tmpVector.points[2]); //w = w1w2 - x1x2 - y1y2 - z1z2
            output.points[0] = (points[3] * tmpVector.points[0] + points[0] * tmpVector.points[3] + points[1]
                    * tmpVector.points[2] - points[2] * tmpVector.points[1]); //x = w1x2 + x1w2 + y1z2 - z1y2
            output.points[1] = (points[3] * tmpVector.points[1] + points[1] * tmpVector.points[3] + points[2]
                    * tmpVector.points[0] - points[0] * tmpVector.points[2]); //y = w1y2 + y1w2 + z1x2 - x1z2
            output.points[2] = (points[3] * tmpVector.points[2] + points[2] * tmpVector.points[3] + points[0]
                    * tmpVector.points[1] - points[1] * tmpVector.points[0]); //z = w1z2 + z1w2 + x1y2 - y1x2
        }
    }

    public void multiplyByQuat(Quaternion input) {
        this.dirty = true;
        if(tmpQuaternion == null) tmpQuaternion = new Quaternion();
        tmpQuaternion.copyVec4(this);
        multiplyByQuat(input, tmpQuaternion);
        this.copyVec4(tmpQuaternion);
    }


    public void multiplyByScalar(float scalar) {
        this.dirty = true;
        multiplyByScalar(scalar);
    }


    public void addQuat(Quaternion input) {
        this.dirty = true;
        addQuat(input, this);
    }


    public void addQuat(Quaternion input, Quaternion output) {
        output.setX(getX() + input.getX());
        output.setY(getY() + input.getY());
        output.setZ(getZ() + input.getZ());
        output.setW(getW() + input.getW());
    }


    public void subQuat(Quaternion input) {
        this.dirty = true;
        subQuat(input, this);
    }


    public void subQuat(Quaternion input, Quaternion output) {
        output.setX(getX() - input.getX());
        output.setY(getY() - input.getY());
        output.setZ(getZ() - input.getZ());
        output.setW(getW() - input.getW());
    }


    private void convertQuatToMatrix() {
        float x = points[0];
        float y = points[1];
        float z = points[2];
        float w = points[3];

        matrix.setX0(1 - 2 * (y * y) - 2 * (z * z)); //1 - 2y2 - 2z2
        matrix.setX1(2 * (x * y) + 2 * (w * z)); // 2xy - 2wz
        matrix.setX2(2 * (x * z) - 2 * (w * y)); //2xz + 2wy
        matrix.setX3(0);
        matrix.setY0(2 * (x * y) - 2 * (w * z)); //2xy + 2wz
        matrix.setY1(1 - 2 * (x * x) - 2 * (z * z)); //1 - 2x2 - 2z2
        matrix.setY2(2 * (y * z) + 2 * (w * x)); // 2yz + 2wx
        matrix.setY3(0);
        matrix.setZ0(2 * (x * z) + 2 * (w * y)); //2xz + 2wy
        matrix.setZ1(2 * (y * z) - 2 * (w * x)); //2yz - 2wx
        matrix.setZ2(1 - 2 * (x * x) - 2 * (y * y)); //1 - 2x2 - 2y2
        matrix.setZ3(0);
        matrix.setW0(0);
        matrix.setW1(0);
        matrix.setW2(0);
        matrix.setW3(1);
    }


    public void toAxisAngle(Vector4f output) {
        if (getW() > 1) {
            normalise(); // if w>1 acos and sqrt will produce errors, this cant happen if quaternion is normalised
        }
        float angle = 2 * (float) Math.toDegrees(Math.acos(getW()));
        float x;
        float y;
        float z;

        float s = (float) Math.sqrt(1 - getW() * getW()); // assuming quaternion normalised then w is less than 1, so term always positive.
        if (s < 0.001) { // test to avoid divide by zero, s is always positive due to sqrt
            // if s close to zero then direction of axis not important
            x = points[0]; // if it is important that axis is normalised then replace with x=1; y=z=0;
            y = points[1];
            z = points[2];
        } else {
            x = points[0] / s; // normalise axis
            y = points[1] / s;
            z = points[2] / s;
        }

        output.points[0] = x;
        output.points[1] = y;
        output.points[2] = z;
        output.points[3] = angle;
    }


    public double[] toEulerAngles() {
        double[] ret = new double[3];

        ret[0] = Math.atan2(2 * points[1] * getW() - 2 * points[0] * points[2], 1 - 2 * (points[1] * points[1]) - 2
                * (points[2] * points[2])); // atan2(2*qy*qw-2*qx*qz , 1 - 2*qy2 - 2*qz2)
        ret[1] = Math.asin(2 * points[0] * points[1] + 2 * points[2] * getW()); // asin(2*qx*qy + 2*qz*qw) 
        ret[2] = Math.atan2(2 * points[0] * getW() - 2 * points[1] * points[2], 1 - 2 * (points[0] * points[0]) - 2
                * (points[2] * points[2])); // atan2(2*qx*qw-2*qy*qz , 1 - 2*qx2 - 2*qz2)

        return ret;
    }


    public void loadIdentityQuat() {
        this.dirty = true;
        setX(0);
        setY(0);
        setZ(0);
        setW(1);
    }

    @Override
    public String toString() {
        return "{X: " + getX() + ", Y:" + getY() + ", Z:" + getZ() + ", W:" + getW() + "}";
    }


    private void generateQuaternionFromMatrix() {

        float qx;
        float qy;
        float qz;
        float qw;

        float[] mat = matrix.getMatrix();
        int[] indices = null;

        if (this.matrix.size() == 16) {
            if (this.matrix.isColumnMajor()) {
                indices = MatrixF4x4.matIndCol16_3x3;
            } else {
                indices = MatrixF4x4.matIndRow16_3x3;
            }
        } else {
            if (this.matrix.isColumnMajor()) {
                indices = MatrixF4x4.matIndCol9_3x3;
            } else {
                indices = MatrixF4x4.matIndRow9_3x3;
            }
        }

        int m00 = indices[0];
        int m01 = indices[1];
        int m02 = indices[2];

        int m10 = indices[3];
        int m11 = indices[4];
        int m12 = indices[5];

        int m20 = indices[6];
        int m21 = indices[7];
        int m22 = indices[8];

        float tr = mat[m00] + mat[m11] + mat[m22];
        if (tr > 0) {
            float s = (float) Math.sqrt(tr + 1.0) * 2; // S=4*qw 
            qw = 0.25f * s;
            qx = (mat[m21] - mat[m12]) / s;
            qy = (mat[m02] - mat[m20]) / s;
            qz = (mat[m10] - mat[m01]) / s;
        } else if ((mat[m00] > mat[m11]) & (mat[m00] > mat[m22])) {
            float s = (float) Math.sqrt(1.0 + mat[m00] - mat[m11] - mat[m22]) * 2; // S=4*qx 
            qw = (mat[m21] - mat[m12]) / s;
            qx = 0.25f * s;
            qy = (mat[m01] + mat[m10]) / s;
            qz = (mat[m02] + mat[m20]) / s;
        } else if (mat[m11] > mat[m22]) {
            float s = (float) Math.sqrt(1.0 + mat[m11] - mat[m00] - mat[m22]) * 2; // S=4*qy
            qw = (mat[m02] - mat[m20]) / s;
            qx = (mat[m01] + mat[m10]) / s;
            qy = 0.25f * s;
            qz = (mat[m12] + mat[m21]) / s;
        } else {
            float s = (float) Math.sqrt(1.0 + mat[m22] - mat[m00] - mat[m11]) * 2; // S=4*qz
            qw = (mat[m10] - mat[m01]) / s;
            qx = (mat[m02] + mat[m20]) / s;
            qy = (mat[m12] + mat[m21]) / s;
            qz = 0.25f * s;
        }

        setX(qx);
        setY(qy);
        setZ(qz);
        setW(qw);
    }


    public void setColumnMajor(float[] matrix) {

        this.matrix.setMatrix(matrix);
        this.matrix.setColumnMajor(true);

        generateQuaternionFromMatrix();
    }


    public void setRowMajor(float[] matrix) {

        this.matrix.setMatrix(matrix);
        this.matrix.setColumnMajor(false);

        generateQuaternionFromMatrix();
    }


    public void setEulerAngle(float azimuth, float pitch, float roll) {

        double heading = Math.toRadians(roll);
        double attitude = Math.toRadians(pitch);
        double bank = Math.toRadians(azimuth);

        double c1 = Math.cos(heading / 2);
        double s1 = Math.sin(heading / 2);
        double c2 = Math.cos(attitude / 2);
        double s2 = Math.sin(attitude / 2);
        double c3 = Math.cos(bank / 2);
        double s3 = Math.sin(bank / 2);
        double c1c2 = c1 * c2;
        double s1s2 = s1 * s2;
        setW((float) (c1c2 * c3 - s1s2 * s3));
        setX((float) (c1c2 * s3 + s1s2 * c3));
        setY((float) (s1 * c2 * c3 + c1 * s2 * s3));
        setZ((float) (c1 * s2 * c3 - s1 * c2 * s3));

        dirty = true;
    }


    public void setAxisAngle(Vector3f vec, float rot) {
        double s = Math.sin(Math.toRadians(rot / 2));
        setX(vec.getX() * (float) s);
        setY(vec.getY() * (float) s);
        setZ(vec.getZ() * (float) s);
        setW((float) Math.cos(Math.toRadians(rot / 2)));

        dirty = true;
    }

    public void setAxisAngleRad(Vector3f vec, double rot) {
        double s = rot / 2;
        setX(vec.getX() * (float) s);
        setY(vec.getY() * (float) s);
        setZ(vec.getZ() * (float) s);
        setW((float) rot / 2);

        dirty = true;
    }


    public MatrixF4x4 getMatrix4x4() {
        //toMatrixColMajor();
        if (dirty) {
            convertQuatToMatrix();
            dirty = false;
        }
        return this.matrix;
    }

    public void copyFromVec3(Vector3f vec, float w) {
        copyFromV3f(vec, w);
    }


    public void slerp(Quaternion input, Quaternion output, float t) {
        // Calculate angle between them.
        //double cosHalftheta = this.dotProduct(input);
        Quaternion bufferQuat;
        float cosHalftheta = this.dotProduct(input);

        if (cosHalftheta < 0) {
            if(tmpQuaternion == null) tmpQuaternion = new Quaternion();
            bufferQuat = tmpQuaternion;
            cosHalftheta = -cosHalftheta;
            bufferQuat.points[0] = (-input.points[0]);
            bufferQuat.points[1] = (-input.points[1]);
            bufferQuat.points[2] = (-input.points[2]);
            bufferQuat.points[3] = (-input.points[3]);
        } else {
            bufferQuat = input;
        }

        // if qa=qb or qa=-qb then theta = 0 and we can return qa
        if (Math.abs(cosHalftheta) >= 1.0) {
            output.points[0] = (this.points[0]);
            output.points[1] = (this.points[1]);
            output.points[2] = (this.points[2]);
            output.points[3] = (this.points[3]);
        } else {
            double sinHalfTheta = Math.sqrt(1.0 - cosHalftheta * cosHalftheta);
            // if theta = 180 degrees then result is not fully defined
            // we could rotate around any axis normal to qa or qb
            //if(Math.abs(sinHalfTheta) < 0.001){
            //output.setW(this.getW() * 0.5f + input.getW() * 0.5f);
            //output.setX(this.getX() * 0.5f + input.getX() * 0.5f);
            //output.setY(this.getY() * 0.5f + input.getY() * 0.5f);
            //output.setZ(this.getZ() * 0.5f + input.getZ() * 0.5f);
            //  lerp(bufferQuat, output, t);
            //}
            //else{
            double halfTheta = Math.acos(cosHalftheta);

            double ratioA = Math.sin((1 - t) * halfTheta) / sinHalfTheta;
            double ratioB = Math.sin(t * halfTheta) / sinHalfTheta;

            //Calculate Quaternion
            output.points[3] = ((float) (points[3] * ratioA + bufferQuat.points[3] * ratioB));
            output.points[0] = ((float) (this.points[0] * ratioA + bufferQuat.points[0] * ratioB));
            output.points[1] = ((float) (this.points[1] * ratioA + bufferQuat.points[1] * ratioB));
            output.points[2] = ((float) (this.points[2] * ratioA + bufferQuat.points[2] * ratioB));

            //}
        }
    }

}
