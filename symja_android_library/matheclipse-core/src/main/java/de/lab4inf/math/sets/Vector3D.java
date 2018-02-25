/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2009,  Prof. Dr. Nikolaus Wulff
 * University of Applied Sciences, Muenster, Germany
 * Lab for Computer Sciences (Lab4Inf).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
*/

package de.lab4inf.math.sets;

import java.io.Serializable;

import de.lab4inf.math.L4MObject;

import static de.lab4inf.math.util.Accuracy.isSimilar;
import static java.lang.Math.PI;
import static java.lang.Math.acos;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
//import de.lab4inf.math.lapack.LinearAlgebra;

/**
 * A three dimensional vector in Euclidian space.
 *
 * @author nwulff
 * @version $Id: Vector3D.java,v 1.13 2014/06/01 16:25:22 nwulff Exp $
 * @since 02.04.2009
 */
public final class Vector3D extends L4MObject implements Cloneable,
        Serializable {
    /**
     * reference to the serialVersionUID attribute.
     */
    private static final long serialVersionUID = -831665881601879392L;
    private double x, y, z;

    /**
     * Default constructor.
     */
    public Vector3D() {
        this(0, 0, 0);
    }

    /**
     * Constructor with given initialization.
     *
     * @param xx the x value
     * @param yy the y value
     * @param zz the z value
     */
    public Vector3D(final double xx, final double yy, final double zz) {
        x = xx;
        y = yy;
        z = zz;
    }

    /**
     * Copy constructor.
     *
     * @param v the vector to copy
     */
    public Vector3D(final Vector3D v) {
        x = v.x;
        y = v.y;
        z = v.z;
    }

    /**
     * Addition of this vector with v.
     *
     * @param v the other vector
     * @return this + v
     */
    public Vector3D add(final Vector3D v) {
        return new Vector3D(x + v.x, y + v.y, z + v.z);
    }

    /**
     * Subtraction of v from this vector.
     *
     * @param v the other vector
     * @return this - v
     */
    public Vector3D minus(final Vector3D v) {
        return new Vector3D(x - v.x, y - v.y, z - v.z);
    }

    /**
     * Scalar product of this vector with v.
     *
     * @param v the other vector
     * @return this*v
     */
    public double multiply(final Vector3D v) {
        return x * v.x + y * v.y + z * v.z;
    }

    /**
     * Vector product of this vector with v, e.g
     * this x v is orthogonal to v and this.
     *
     * @param v the other vector
     * @return this x v
     */
    public Vector3D crossProduct(final Vector3D v) {
        double rx, ry, rz;
        rx = y * v.z - z * v.y;
        ry = z * v.x - x * v.z;
        rz = x * v.y - y * v.x;
        return new Vector3D(rx, ry, rz);
    }

    /**
     * Calculate the angle between this vector and v.
     *
     * @param v the other vector
     * @return the angle in radian
     */
    public double angle(final Vector3D v) {
        double cosA = 0;
        if (!isZero() && !v.isZero()) {
            cosA = min(1, max(-1, this.multiply(v) / (norm() * v.norm())));
        }
        return acos(cosA);
    }

    /**
     * Clone method of a 3D vector.
     *
     * @return the clone
     */
    @Override
    public Vector3D clone() {
        try {
            return (Vector3D) super.clone();
        } catch (CloneNotSupportedException e) {
            logger.error("no clone", e);
        }
        return this;
    }

    /**
     * Return the vector as a 3-dimensional array.
     *
     * @return 3D-array
     */
    public double[] asArray() {
        return new double[]{x, y, z};
    }

    /**
     * Calculate the Euclidian vector norm.
     *
     * @return ||this||
     */
    public double norm() {
        return sqrt(norm2());
    }

    /**
     * Calculate the square of the Euclidian vector norm.
     *
     * @return this*this
     */
    public double norm2() {
        return x * x + y * y + z * z;
    }

    /**
     * Calculate the transverse component  (R in cylindrical coordinate system).
     *
     * @return sqrt(x*x+y*y)
     */
    public double rho() {
        return sqrt(x * x + y * y);
    }

    /**
     * Calculate the polar angle phi = atan(y/x).
     *
     * @return phi in the range 0 to 2pi
     */
    public double phi() {
        double phi = 0;
        if (x * x + y * y > 0) {
            if (x != 0) {
                phi = atan2(y, x);
            } else {
                if (y > 0)
                    phi = PI / 2;
                else
                    phi = 3 * PI / 2;
            }
            if (phi < 0) {
                phi += 2 * PI;
            }
        }
        return phi;
    }

    /**
     * Calculate the azimuth angle theta = atan(r/z).
     *
     * @return theta in the range 0 to pi
     */
    public double theta() {
        double r, theta = 0;
        if (!isZero()) {
            r = sqrt(x * x + y * y);
            if (r != 0) {
                theta = atan2(r, z);
            } else {
                if (z > 0)
                    theta = 0;
                else
                    theta = PI;
            }
        }
        return theta;
    }

    /**
     * Rotate the vector in the zy-plane around the x axis.
     *
     * @param angle of rotation in radian
     * @return a rotated vector
     */
    public Vector3D rotateX(final double angle) {
        double zz, yy, s = sin(angle), c = cos(angle);
        yy = c * y - s * z;
        zz = s * y + c * z;
        return new Vector3D(x, yy, zz);
    }

    /**
     * Rotate the vector in the xz-plane around the y axis.
     *
     * @param angle of rotation in radian
     * @return a rotated vector
     */
    public Vector3D rotateY(final double angle) {
        double xx, zz, s = sin(angle), c = cos(angle);
        zz = c * z - s * x;
        xx = s * z + c * x;
        return new Vector3D(xx, y, zz);
    }

    /**
     * Rotate the vector in the xy-plane around the z axis.
     *
     * @param angle of rotation in radian
     * @return a rotated vector
     */
    public Vector3D rotateZ(final double angle) {
        double xx, yy, s = sin(angle), c = cos(angle);
        xx = c * x - s * y;
        yy = s * x + c * y;
        return new Vector3D(xx, yy, z);
    }

    /**
     * Make a generic transformation/rotation with the
     * given 3x3 matrix.
     *
     * @param m a 3x3 transformation matrix
     * @return a transformed vector
     */
    public Vector3D transform(final double[][] m) {
        int n = m.length;
        if (3 != n) {
            throw new IllegalArgumentException("matrix not 3x3: " + n);
        }
        double[] v = asArray();
        double[] w = new double[n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                w[i] += m[i][j] * v[j];
            }
        }
        return new Vector3D(w[0], w[1], w[3]);
    }

    /**
     * Scaling of this vector with s.
     *
     * @param s the scaling factor
     * @return this*s
     */
    public Vector3D multiply(final double s) {
        return new Vector3D(x * s, y * s, z * s);
    }

    /**
     * Signal if this vector is of zero length.
     *
     * @return boolean zero indication
     */
    public boolean isZero() {
        return x == 0 && y == 0 && z == 0;
    }

    /**
     * Get the x value.
     *
     * @return the x
     */
    public double getX() {
        return x;
    }

    /**
     * Set the x value.
     *
     * @param v the x to set
     */
    public void setX(final double v) {
        this.x = v;
    }

    /**
     * Get the y value.
     *
     * @return the y
     */
    public double getY() {
        return y;
    }

    /**
     * Set the y value.
     *
     * @param v the y to set
     */
    public void setY(final double v) {
        this.y = v;
    }

    /**
     * Get the value of the z attribute.
     *
     * @return the z
     */
    public double getZ() {
        return z;
    }

    /**
     * Set the z value.
     *
     * @param v the z to set
     */
    public void setZ(final double v) {
        this.z = v;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object o) {
        if (null == o) {
            return false;
        } else if (this == o) {
            return true;
        } else if (getClass().equals(o.getClass())) {
            Vector3D d = (Vector3D) o;
            return isSimilar(x, d.x) && isSimilar(y, d.y) && isSimilar(z, d.z);
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        long lx = Double.doubleToLongBits(x);
        long ly = Double.doubleToLongBits(y);
        long lz = Double.doubleToLongBits(z);
        int ix = (int) (lx ^ (lx >>> 32));
        int iy = (int) (ly ^ (ly >>> 32));
        int iz = (int) (lz ^ (lz >>> 32));
        return ix ^ iy ^ iz;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("<%+f|%+f|%+f>", x, y, z);
    }
}
 