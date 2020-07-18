/*
 * Project: Lab4Math
 *
 * Copyright (c) 2008-2009,  Prof. Dr. Nikolaus Wulff
 * University of Applied Sciences, Muenster, Germany
 * Lab for computer sciences (Lab4Inf).
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
import de.lab4inf.math.util.Accuracy;

import static java.lang.Math.PI;
import static java.lang.Math.atan2;
import static java.lang.Math.log;
import static java.lang.Math.sqrt;

/**
 * A four dimensional vector in Minkowski space, i.e. the non Euklidian space of
 * special relativity, with the metric tensor trace (1,-1,-1,-1).
 * <p>
 * The components are internaly coded as (ct,x,y,z) in the space-time frame,
 * or equivalent as energy-momentum vector (E,P)=(E,px,py,pz). This has
 * to be noticed if the vector components are read via the asArray method.
 * <p>
 * In all formulae dimensionless units of c=1 are assumed.
 *
 * @author nwulff
 * @version $Id: LVector4D.java,v 1.18 2014/11/18 21:03:21 nwulff Exp $
 * @since 03.04.2009
 */
public final class LVector4D extends L4MObject implements Cloneable, Serializable {
    /**
     * reference to the serialVersionUID attribute.
     */
    private static final long serialVersionUID = -8523269306363604837L;
    private final Vector3D v;
    private boolean cartesianToString = true;
    private boolean anglesInDegree = true;
    private double t;

    /**
     * Default constructor.
     */
    public LVector4D() {
        v = new Vector3D();
    }

    /**
     * Constructor with given initialization.
     *
     * @param t the time or energy component
     * @param x the 1.st space or momentum component
     * @param y the 2.nd space or momentum component
     * @param z the 3.rd space or momentum component
     */
    public LVector4D(final double t, final double x, final double y, final double z) {
        this.t = t;
        v = new Vector3D(x, y, z);
    }

    /**
     * Constructor with given energy and momentum (time, space-vector).
     *
     * @param e the energy or time component
     * @param p the momentum or space vector component
     */
    public LVector4D(final double e, final Vector3D p) {
        t = e;
        v = p.clone();
    }

    /**
     * Copy constructor.
     *
     * @param rhs the vector to copy
     */
    public LVector4D(final LVector4D rhs) {
        t = rhs.t;
        v = rhs.v.clone();
    }

    /**
     * Return the given radian angle in degrees.
     *
     * @param radian the angle
     * @return degrees of angle
     */
    private static double degree(final double radian) {
        return radian * 180 / Math.PI;
    }

    /**
     * Clone method of a lorentz vector.
     *
     * @return the clone
     */
    @Override
    public LVector4D clone() {
        try {
            return (LVector4D) super.clone();
        } catch (final CloneNotSupportedException e) {
            logger.error("no clone", e);
        }
        return this;
    }

    /**
     * Return the vector as a 4-dimensional array with ordering (t,x,y,z)
     * or (e,px,py,pz).
     *
     * @return 4D-array
     */
    public double[] asArray() {
        return new double[]{t, getX(), getY(), getZ()};
    }

    /**
     * Addition of this vector with v.
     *
     * @param rhs the other vector
     * @return this + rhs
     */
    public LVector4D add(final LVector4D rhs) {
        return new LVector4D(t + rhs.t, v.add(rhs.v));
    }

    /**
     * Subtraction of v from this vector.
     *
     * @param rhs the other vector
     * @return this - rhs
     */
    public LVector4D minus(final LVector4D rhs) {
        return new LVector4D(t - rhs.t, v.minus(rhs.v));
    }

    /**
     * Product of this vector with v using the Minkowski metric.
     *
     * @param rhs the other vector
     * @return this*rhs
     */
    public double multiply(final LVector4D rhs) {
        double r = t * rhs.t;
        r -= this.v.multiply(rhs.v);
        return r;
    }

    /**
     * Scaling of this vector with a scalar.
     *
     * @param s the scaling factor
     * @return this*s
     */
    public LVector4D multiply(final double s) {
        return new LVector4D(t * s, v.multiply(s));
    }

    /**
     * Calculate the angle between this vector and v
     * using the momentum/space-like component.
     *
     * @param rhs the other vector
     * @return the angle in radian
     */
    public double angle(final LVector4D rhs) {
        return v.angle(rhs.v);
    }

    /**
     * Get the invariant mass of the energy momentum vector.
     * It E*E - p*p<0 then the negative root is returned!
     *
     * @return the invariant mass
     */
    public double mass() {
        final double m2 = mass2();
        if (m2 < 0) {
            return -sqrt(-m2);
        }
        return sqrt(m2);
    }

    /**
     * Get the squared invariant mass of the energy momentum vector.
     *
     * @return m2 = ||p||**2
     */
    public double mass2() {
        return t * t - v.norm2();
    }

    /**
     * Beta = |p|/E, which is the ratio of v/c.
     *
     * @return beta
     */
    public double beta() {
        if (getE() > 0) {
            return v.norm() / getE();
        }
        return 0;
    }

    /**
     * Gamma=1/sqrt(1-b*b).
     *
     * @return gamma
     */
    public double gamma() {
        final double b = beta();
        if (b > 0) {
            return 1.0 / sqrt(1 - b * b);
        }
        return 1;
    }

    /**
     * Rho = |p| the momentum value beta*E.
     *
     * @return |p|
     */
    public double rho() {
        return v.norm();
    }

    /**
     * Get access to the three dimensional momentum (or space) component.
     *
     * @return the (not cloned) momentum
     */
    public Vector3D getP() {
        return v;
    }

    /**
     * Get the transversal momentum.
     *
     * @return pt = sqrt(px*px+py*py)
     */
    public double pt() {
        return v.rho();
    }

    /**
     * Get the longitudinal momentum.
     *
     * @return pz
     */
    public double pl() {
        return v.getZ();
    }

    /**
     * Signal if this vector is of zero length.
     *
     * @return boolean zero indication
     */
    public boolean isZero() {
        return t == 0 && v.isZero();
    }

    /**
     * Get the x value.
     *
     * @return the x
     */
    public double getX() {
        return v.getX();
    }

    /**
     * Set the x value.
     *
     * @param x the value
     */
    public void setX(final double x) {
        v.setX(x);
    }

    /**
     * Get the y value.
     *
     * @return the y
     */
    public double getY() {
        return v.getY();
    }

    /**
     * Set the y value.
     *
     * @param y the value to set
     */
    public void setY(final double y) {
        v.setY(y);
    }

    /**
     * Get the value of the z attribute.
     *
     * @return the z
     */
    public double getZ() {
        return v.getZ();
    }

    /**
     * Set the z value.
     *
     * @param z the value to set
     */
    public void setZ(final double z) {
        v.setZ(z);
    }

    /**
     * Get the value of the t attribute.
     *
     * @return the t
     */
    public double getT() {
        return t;
    }

    /**
     * Set the t value.
     *
     * @param t the value to set
     */
    public void setT(final double t) {
        this.t = t;
    }

    /**
     * Get the px component of the transversal impuls.
     *
     * @return the px
     */
    public double getPX() {
        return getX();
    }

    /**
     * Set the px component of the transversal impuls.
     *
     * @param px the value to set
     */
    public void setPX(final double px) {
        setX(px);
    }

    /**
     * Get the py component of the transversal impuls.
     *
     * @return the py
     */
    public double getPY() {
        return getY();
    }

    /**
     * Set the py component of the transversal impuls.
     *
     * @param py the value to set
     */
    public void setPY(final double py) {
        setY(py);
    }

    /**
     * Get the longitudinal impuls.
     *
     * @return the pz
     */
    public double getPZ() {
        return getZ();
    }

    /**
     * Set the pz longitudinal impuls value.
     *
     * @param pz the value to set
     */
    public void setPZ(final double pz) {
        setZ(pz);
    }

    /**
     * Get the energy componment.
     *
     * @return the E
     */
    public double getE() {
        return t;
    }

    /**
     * Set the energy component.
     *
     * @param e the valueto set
     */
    public void setE(final double e) {
        this.t = e;
    }

    /**
     * The phi angle of the transversal component.
     * <b>Note:</b></br>
     * The polar angle is calculated different as for
     * the 3-dimensional vector. The 3D has an polar
     * angle from 0 to 2pi, whereas the 4D vector
     * uses -pi to pi. This helps to check the pt
     * balance within HEP experiments
     * </b>.
     *
     * @return phi in the range -pi to pi
     */
    public double phi() {
        double phi = 0;
        if (v.norm2() > 0) {
            if (getX() != 0) {
                phi = atan2(getY(), getX());
            } else {
                if (getY() > 0)
                    phi = PI / 2;
                else
                    phi = -PI / 2;
            }
        }
        return phi;
    }

    /**
     * Calculate the angle theta = atan(r/z).
     *
     * @return theta in the range 0 to pi
     */
    public double theta() {
        double r, theta = 0;
        if (!isZero()) {
            r = v.rho();
            if (r != 0) {
                theta = atan2(r, getZ());
            } else {
                if (getZ() > 0)
                    theta = 0;
                else
                    theta = PI;
            }
        }
        return theta;
    }

    /**
     * Returns the rapidity, i.e. 0.5*ln((E+pz)/(E-pz)).
     *
     * @return the rapidity
     */
    public double rapidity() {
        final double eta = (getE() + getPZ()) / (getE() - getPZ());
        return 0.5 * log(eta);
    }

    /**
     * Return the pseudo rapidity, i.e. -ln(theta/2).
     *
     * @return the pseudo rapidity
     */
    public double pseudoRapidity() {
        return -log(theta() / 2);
    }

    /**
     * Perform a Lorentz boost of this vector.
     *
     * @param px the x transversal momentum
     * @param py the y transversal momentum
     * @param pz the longitudinal momentum
     */
    public void boost(final double px, final double py, final double pz) {
        double gamma2 = 0;
        final double p2 = px * px + py * py + pz * pz;
        final double gamma = 1.0 / sqrt(1.0 - p2);
        final double pp = px * getPX() + py * getPY() + pz * getPZ();
        if (p2 > 0) {
            gamma2 = (gamma - 1.0) / p2;
        }

        setPX(getPX() + gamma2 * pp * px + gamma * px * t);
        setPY(getPY() + gamma2 * pp * py + gamma * py * t);
        setPZ(getPZ() + gamma2 * pp * pz + gamma * pz * t);
        t = gamma * (t + pp);
    }

    /**
     * Perform a Lorentz boost of this vector.
     *
     * @param p the momentum to boost with
     */
    public void boost(final Vector3D p) {
        boost(p.getX(), p.getY(), p.getZ());
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object o) {
        if (null == o) {
            return false;
        } else if (this == o) {
            return true;
        } else if (getClass().equals(o.getClass())) {
            final LVector4D d = (LVector4D) o;
            return Accuracy.isSimilar(t, d.t) && v.equals(d.v);
        }
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final long lt = Double.doubleToLongBits(t);
        final int it = (int) (lt ^ (lt >>> 32));
        return it ^ v.hashCode();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        final String fmtCart = "<%.3f|%+.3f|%+.3f|%+.3f>";
        final String fmtAngle = "<%.3f|%.3f|%+.3f|%+.3f>";
        String s;
        if (cartesianToString) {
            s = String.format(fmtCart, t, getX(), getY(), getZ());
        } else {
            if (anglesInDegree) {
                s = String.format(fmtAngle, t, getP().norm(), degree(phi()), degree(theta()));
            } else {
                s = String.format(fmtAngle, t, getP().norm(), phi(), theta());
            }
        }
        return s;
    }

    /**
     * Get the cartesianToString value.
     *
     * @return the cartesianToString
     */
    public boolean isCartesianToString() {
        return cartesianToString;
    }

    /**
     * Set the cartesianToString indicator to true for cartesian coordinates
     * otherwise polar coordinates will be used for the 3D space part.
     *
     * @param cartesianToString set to true for cartesian coordinates
     */
    public void setCartesianToString(final boolean cartesianToString) {
        this.cartesianToString = cartesianToString;
    }

    /**
     * Get the anglesInDegree value.
     *
     * @return the anglesInDegree
     */
    public boolean isAnglesInDegree() {
        return anglesInDegree;
    }

    /**
     * Set the anglesInDegree value.
     *
     * @param anglesInDegree the anglesInDegree to set
     */
    public void setAnglesInDegree(final boolean anglesInDegree) {
        this.anglesInDegree = anglesInDegree;
    }
}
 