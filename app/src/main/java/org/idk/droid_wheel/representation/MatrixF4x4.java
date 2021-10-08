package org.idk.droid_wheel.representation;

import android.util.Log;


public class MatrixF4x4 {

    public static final int[] matIndCol9_3x3 = { 0, 1, 2, 3, 4, 5, 6, 7, 8 };
    public static final int[] matIndCol16_3x3 = { 0, 1, 2, 4, 5, 6, 8, 9, 10 };
    public static final int[] matIndRow9_3x3 = { 0, 3, 6, 1, 4, 7, 3, 5, 8 };
    public static final int[] matIndRow16_3x3 = { 0, 4, 8, 1, 5, 9, 2, 6, 10 };

    public static final int[] matIndCol16_4x4 = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };
    public static final int[] matIndRow16_4x4 = { 0, 4, 8, 12, 1, 5, 9, 13, 2, 6, 10, 14, 3, 7, 11, 15 };

    private boolean colMaj = true;


    public float[] matrix;


    public MatrixF4x4() {
        // The matrix is defined as float[column][row]
        this.matrix = new float[16];
        Matrix.setIdentityM(this.matrix, 0);
    }


    public float[] getMatrix() {
        return this.matrix;
    }

    public int size() {
        return matrix.length;
    }


    public void setMatrix(float[] matrix) {
        if (matrix.length == 16 || matrix.length == 9)
            this.matrix = matrix;
        else {
            throw new IllegalArgumentException("Matrix set is invalid, size is " + matrix.length + " expected 9 or 16");
        }
    }

    public void set(MatrixF4x4 source) {
        System.arraycopy(source.matrix, 0, matrix, 0, matrix.length);
    }


    public void setColumnMajor(boolean colMajor) {
        this.colMaj = colMajor;
    }


    public boolean isColumnMajor() {
        return colMaj;
    }


    public void multiplyVector4fByMatrix(Vector4f vector) {

        if (matrix.length == 16) {
            float x = 0;
            float y = 0;
            float z = 0;
            float w = 0;

            float[] vectorArray = vector.array();

            if (colMaj) {
                for (int i = 0; i < 4; i++) {

                    int k = i * 4;

                    x += this.matrix[k + 0] * vectorArray[i];
                    y += this.matrix[k + 1] * vectorArray[i];
                    z += this.matrix[k + 2] * vectorArray[i];
                    w += this.matrix[k + 3] * vectorArray[i];
                }
            } else {
                for (int i = 0; i < 4; i++) {

                    x += this.matrix[0 + i] * vectorArray[i];
                    y += this.matrix[4 + i] * vectorArray[i];
                    z += this.matrix[8 + i] * vectorArray[i];
                    w += this.matrix[12 + i] * vectorArray[i];
                }
            }

            vector.setX(x);
            vector.setY(y);
            vector.setZ(z);
            vector.setW(w);
        } else
            Log.e("matrix", "Matrix is invalid, is " + matrix.length + " long, this equation expects a 16 value matrix");
    }


    public void multiplyVector3fByMatrix(Vector3f vector) {

        if (matrix.length == 9) {
            float x = 0;
            float y = 0;
            float z = 0;

            float[] vectorArray = vector.toArray();

            if (!colMaj) {
                for (int i = 0; i < 3; i++) {

                    int k = i * 3;

                    x += this.matrix[k + 0] * vectorArray[i];
                    y += this.matrix[k + 1] * vectorArray[i];
                    z += this.matrix[k + 2] * vectorArray[i];
                }
            } else {
                for (int i = 0; i < 3; i++) {

                    x += this.matrix[0 + i] * vectorArray[i];
                    y += this.matrix[3 + i] * vectorArray[i];
                    z += this.matrix[6 + i] * vectorArray[i];
                }
            }

            vector.setX(x);
            vector.setY(y);
            vector.setZ(z);
        } else
            Log.e("matrix", "Matrix is invalid, is " + matrix.length
                    + " long, this function expects the internal matrix to be of size 9");
    }


    public void multiplyMatrix4x4ByMatrix(MatrixF4x4 matrixf) {

        // TODO implement Strassen Algorithm in place of this slower naive one.
        float[] bufferMatrix = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        float[] matrix = matrixf.getMatrix();



        multiplyMatrix(matrix, 0, bufferMatrix, 0);
        matrixf.setMatrix(bufferMatrix);
    }

    public void multiplyMatrix(float[] input, int inputOffset, float[] output, int outputOffset) {
        float[] bufferMatrix = output;
        float[] matrix = input;

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {

                int k = i * 4;
                bufferMatrix[outputOffset + 0 + j] += this.matrix[k + j] * matrix[inputOffset + 0 * 4 + i];
                bufferMatrix[outputOffset + 1 * 4 + j] += this.matrix[k + j] * matrix[inputOffset + 1 * 4 + i];
                bufferMatrix[outputOffset + 2 * 4 + j] += this.matrix[k + j] * matrix[inputOffset + 2 * 4 + i];
                bufferMatrix[outputOffset + 3 * 4 + j] += this.matrix[k + j] * matrix[inputOffset + 3 * 4 + i];
            }
        }
    }


    public void transpose() {
        if (this.matrix.length == 16) {
            float[] newMatrix = new float[16];
            for (int i = 0; i < 4; i++) {

                int k = i * 4;

                newMatrix[k] = matrix[i];
                newMatrix[k + 1] = matrix[4 + i];
                newMatrix[k + 2] = matrix[8 + i];
                newMatrix[k + 3] = matrix[12 + i];
            }
            matrix = newMatrix;

        } else {
            float[] newMatrix = new float[9];
            for (int i = 0; i < 3; i++) {

                int k = i * 3;

                newMatrix[k] = matrix[i];
                newMatrix[k + 1] = matrix[3 + i];
                newMatrix[k + 2] = matrix[6 + i];
            }
            matrix = newMatrix;
        }

    }

    public void setX0(float value) {

        if (matrix.length == 16) {
            if (colMaj)
                matrix[matIndCol16_3x3[0]] = value;
            else
                matrix[matIndRow16_3x3[0]] = value;
        } else {
            if (colMaj)
                matrix[matIndCol9_3x3[0]] = value;
            else
                matrix[matIndRow9_3x3[0]] = value;
        }
    }

    public void setX1(float value) {

        if (matrix.length == 16) {
            if (colMaj)
                matrix[matIndCol16_3x3[1]] = value;
            else
                matrix[matIndRow16_3x3[1]] = value;
        } else {
            if (colMaj)
                matrix[matIndCol9_3x3[1]] = value;
            else
                matrix[matIndRow9_3x3[1]] = value;
        }
    }

    public void setX2(float value) {

        if (matrix.length == 16) {
            if (colMaj)
                matrix[matIndCol16_3x3[2]] = value;
            else
                matrix[matIndRow16_3x3[2]] = value;
        } else {
            if (colMaj)
                matrix[matIndCol9_3x3[2]] = value;
            else
                matrix[matIndRow9_3x3[2]] = value;
        }
    }

    public void setY0(float value) {

        if (matrix.length == 16) {
            if (colMaj)
                matrix[matIndCol16_3x3[3]] = value;
            else
                matrix[matIndRow16_3x3[3]] = value;
        } else {
            if (colMaj)
                matrix[matIndCol9_3x3[3]] = value;
            else
                matrix[matIndRow9_3x3[3]] = value;
        }
    }

    public void setY1(float value) {

        if (matrix.length == 16) {
            if (colMaj)
                matrix[matIndCol16_3x3[4]] = value;
            else
                matrix[matIndRow16_3x3[4]] = value;
        } else {
            if (colMaj)
                matrix[matIndCol9_3x3[4]] = value;
            else
                matrix[matIndRow9_3x3[4]] = value;
        }
    }

    public void setY2(float value) {

        if (matrix.length == 16) {
            if (colMaj)
                matrix[matIndCol16_3x3[5]] = value;
            else
                matrix[matIndRow16_3x3[5]] = value;
        } else {
            if (colMaj)
                matrix[matIndCol9_3x3[5]] = value;
            else
                matrix[matIndRow9_3x3[5]] = value;
        }
    }

    public void setZ0(float value) {

        if (matrix.length == 16) {
            if (colMaj)
                matrix[matIndCol16_3x3[6]] = value;
            else
                matrix[matIndRow16_3x3[6]] = value;
        } else {
            if (colMaj)
                matrix[matIndCol9_3x3[6]] = value;
            else
                matrix[matIndRow9_3x3[6]] = value;
        }
    }

    public void setZ1(float value) {

        if (matrix.length == 16) {
            if (colMaj)
                matrix[matIndCol16_3x3[7]] = value;
            else
                matrix[matIndRow16_3x3[7]] = value;
        } else {
            if (colMaj)
                matrix[matIndCol9_3x3[7]] = value;
            else
                matrix[matIndRow9_3x3[7]] = value;
        }
    }

    public void setZ2(float value) {

        if (matrix.length == 16) {
            if (colMaj)
                matrix[matIndCol16_3x3[8]] = value;
            else
                matrix[matIndRow16_3x3[8]] = value;
        } else {
            if (colMaj)
                matrix[matIndCol9_3x3[8]] = value;
            else
                matrix[matIndRow9_3x3[8]] = value;
        }
    }

    public void setX3(float value) {

        if (matrix.length == 16) {
            if (colMaj)
                matrix[matIndCol16_4x4[3]] = value;
            else
                matrix[matIndRow16_4x4[3]] = value;
        }else
            throw new IllegalStateException("length of matrix should be 16");
    }

    public void setY3(float value) {

        if (matrix.length == 16) {
            if (colMaj)
                matrix[matIndCol16_4x4[7]] = value;
            else
                matrix[matIndRow16_4x4[7]] = value;
        }else
            throw new IllegalStateException("length of matrix should be 16");
    }

    public void setZ3(float value) {

        if (matrix.length == 16) {
            if (colMaj)
                matrix[matIndCol16_4x4[11]] = value;
            else
                matrix[matIndRow16_4x4[11]] = value;
        }else
            throw new IllegalStateException("length of matrix should be 16");
    }

    public void setW0(float value) {

        if (matrix.length == 16) {
            if (colMaj)
                matrix[matIndCol16_4x4[12]] = value;
            else
                matrix[matIndRow16_4x4[12]] = value;
        }else
            throw new IllegalStateException("length of matrix should be 16");
    }

    public void setW1(float value) {

        if (matrix.length == 16) {
            if (colMaj)
                matrix[matIndCol16_4x4[13]] = value;
            else
                matrix[matIndRow16_4x4[13]] = value;
        }else
            throw new IllegalStateException("length of matrix should be 16");
    }

    public void setW2(float value) {

        if (matrix.length == 16) {
            if (colMaj)
                matrix[matIndCol16_4x4[14]] = value;
            else
                matrix[matIndRow16_4x4[14]] = value;
        }else
            throw new IllegalStateException("length of matrix should be 16");
    }

    public void setW3(float value) {

        if (matrix.length == 16) {
            if (colMaj)
                matrix[matIndCol16_4x4[15]] = value;
            else
                matrix[matIndRow16_4x4[15]] = value;
        }else
            throw new IllegalStateException("length of matrix should be 16");
    }

}
