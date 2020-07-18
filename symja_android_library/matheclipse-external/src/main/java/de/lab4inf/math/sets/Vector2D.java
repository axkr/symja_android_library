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
import static java.lang.Math.sin;

/**
 * A two dimensional vector in Euclidian space.
 *
 * @author nwulff
 * @version $Id: Vector2D.java,v 1.12 2014/06/01 16:25:22 nwulff Exp $
 * @since 02.04.2009
 */
public final class Vector2D extends L4MObject implements Cloneable,
        Serializable {
    /**
     * reference to the serialVersionUID attribute.
     */
    private static final long serialVersionUID = -8715421747262583847L;
    private double x, y;

    /**
     * Default constructor.
     */
    public Vector2D() {
        this(0, 0);
    }

    /**
     * Constructor with given initialization.
     *
     * @param xx the x value
     * @param yy the y value
     */
    public Vector2D(final double xx, final double yy) {
        x = xx;
        y = yy;
    }

    /**
     * Copy constructor.
     *
     * @param v the vector to copy
     */
    public Vector2D(final Vector2D v) {
        x = v.x;
        y = v.y;
    }

    /**
     * Return the vector as a 2-dimensional array.
     *
     * @return 2D-array
     */
    public double[] asArray() {
        return new double[]{x, y};
    }

    /**
     * Addition of this vector with v.
     *
     * @param v the other vector
     * @return this + v
     */
    public Vector2D add(final Vector2D v) {
        return new Vector2D(x + v.x, y + v.y);
    }

    /**
     * Subtraction of v from this vector.
     *
     * @param v the other vector
     * @return this - v
     */
    public Vector2D minus(final Vector2D v) {
        return new Vector2D(x - v.x, y - v.y);
    }

    /**
     * Scalar product of this vector with v.
     *
     * @param v the other vector
     * @return this*v
     */
    public double multiply(final Vector2D v) {
        return x * v.x + y * v.y;
    }

    /**
     * Calculate the Euclidian vector norm.
     *
     * @return ||this||
     */
    public double norm() {
        return Math.sqrt(norm2());
    }

    /**
     * Calculate the square of the Euclidian vector norm.
     *
     * @return this*this;
     */
    public double norm2() {
        return x * x + y * y;
    }

    /**
     * Calculate the angle phi = atan(y/x).
     *
     * @return phi in the range 0 to 2pi
     */
    public double phi() {
        double phi = 0;
        if (!isZero()) {
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
     * Calculate the angle between this vector and v.
     *
     * @param v the other vector
     * @return the angle in radian
     */
    public double angle(final Vector2D v) {
        double cosA = 0;
        if (!isZero() && !v.isZero()) {
            cosA = this.multiply(v) / (norm() * v.norm());
        }
        return acos(cosA);
    }

    /**
     * Clone method of a 2D vector.
     *
     * @return the clone
     */
    @Override
    public Vector2D clone() {
        try {
            return (Vector2D) super.clone();
        } catch (CloneNotSupportedException e) {
            logger.error("no clone", e);
        }
        return this;
    }

    /**
     * Scaling of this vector with a scalar.
     *
     * @param s the scaling factor
     * @return this*s
     */
    public Vector2D multiply(final double s) {
        return new Vector2D(x * s, y * s);
    }

    /**
     * Rotate the vector in the xy-plane with angle phi.
     *
     * @param phi rotation angle
     * @return a rotated vector
     */
    public Vector2D rotate(final double phi) {
        double xx, yy, s = sin(phi), c = cos(phi);
        xx = c * x - s * y;
        yy = s * x + c * y;
        return new Vector2D(xx, yy);
    }

    /**
     * Signal if this vector is of zero length.
     *
     * @return boolean zero indication
     */
    public boolean isZero() {
        return x == 0 && y == 0;
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
            Vector2D d = (Vector2D) o;
            return isSimilar(x, d.x) && isSimilar(y, d.y);
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
        int ix = (int) (lx ^ (lx >>> 32));
        int iy = (int) (ly ^ (ly >>> 32));
        return ix ^ iy;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.format("<%+f|%+f>", x, y);
    }
}
 