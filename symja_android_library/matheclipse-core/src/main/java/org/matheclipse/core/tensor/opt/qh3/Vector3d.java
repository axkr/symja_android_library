// code adapted from https://github.com/datahaki/tensor

/**
 * Copyright John E. Lloyd, 2004. All rights reserved. Permission to use, copy, modify and
 * redistribute is granted, provided that this copyright notice is retained and the author is given
 * credit whenever appropriate.
 *
 * This software is distributed "as is", without any warranty, including any implied warranty of
 * merchantability or fitness for a particular use. The author assumes no responsibility for, and
 * shall not be liable for, any special, indirect, or consequential damages, or any damages
 * whatsoever, arising out of or in connection with the use of this software.
 */
package org.matheclipse.core.tensor.opt.qh3;

import java.util.Random;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.tensor.nrm.Vector2Norm;
import org.matheclipse.core.tensor.nrm.Vector2NormSquared;

/**
 * A three-element vector. This class is actually a reduced version of the Vector3d class contained
 * in the author's matlib package (which was partly inspired by javax.vecmath). Only a mininal
 * number of methods which are relevant to convex hull generation are supplied here.
 *
 * @author John E. Lloyd, Fall 2004
 */
public class Vector3d {
  /** Precision of a double. */
  static private final double DOUBLE_PREC = 2.2204460492503131e-16;
  /** First element */
  public IExpr x;
  /** Second element */
  public IExpr y;
  /** Third element */
  public IExpr z;

  /** Creates a 3-vector and initializes its elements to 0. */
  public Vector3d() {}

  /**
   * Creates a 3-vector by copying an existing one.
   *
   * @param v vector to be copied
   */
  public Vector3d(Vector3d v) {
    set(v);
  }

  /**
   * Creates a 3-vector with the supplied element values.
   *
   * @param x first element
   * @param y second element
   * @param z third element
   */
  public Vector3d(IExpr x, IExpr y, IExpr z) {
    set(x, y, z);
  }

  public Vector3d(double x, double y, double z) {
    this(F.num(x), F.num(y), F.num(z));
  }

  /**
   * Gets a single element of this vector. Elements 0, 1, and 2 correspond to x, y, and z.
   *
   * @param i element index
   * @return element value throws ArrayIndexOutOfBoundsException if i is not in the range 0 to 2.
   */
  public IExpr get(int i) {
    return toTensor().get(i + 1);
  }

  /**
   * Sets a single element of this vector. Elements 0, 1, and 2 correspond to x, y, and z.
   *
   * @param i element index
   * @param value element value
   * @return element value throws ArrayIndexOutOfBoundsException if i is not in the range 0 to 2.
   */
  public void set(int i, ISignedNumber value) {
    switch (i) {
      case 0: {
        x = value;
        break;
      }
      case 1: {
        y = value;
        break;
      }
      case 2: {
        z = value;
        break;
      }
      default: {
        throw new ArrayIndexOutOfBoundsException(i);
      }
    }
  }

  /**
   * Sets the values of this vector to those of v1.
   *
   * @param v1 vector whose values are copied
   */
  public void set(Vector3d v1) {
    set(v1.toTensor());
  }

  /**
   * Adds vector v1 to v2 and places the result in this vector.
   *
   * @param v1 left-hand vector
   * @param v2 right-hand vector
   */
  public void add(Vector3d v1, Vector3d v2) {
    set((IAST) v1.toTensor().add(v2.toTensor()));
  }

  /**
   * Adds this vector to v1 and places the result in this vector.
   *
   * @param v1 right-hand vector
   */
  public void add(Vector3d v1) {
    set((IAST) toTensor().add(v1.toTensor()));
  }

  /**
   * Subtracts vector v1 from v2 and places the result in this vector.
   *
   * @param v1 left-hand vector
   * @param v2 right-hand vector
   */
  public void sub(Vector3d v1, Vector3d v2) {
    set((IAST) v1.toTensor().subtract(v2.toTensor()));
  }

  /**
   * Subtracts v1 from this vector and places the result in this vector.
   *
   * @param v1 right-hand vector
   */
  public void sub(Vector3d v1) {
    set((IAST) toTensor().subtract(v1.toTensor()));
  }

  /**
   * Scales the elements of this vector by <code>s</code>.
   *
   * @param s scaling factor
   */
  public void scale(IExpr s) {
    set((IAST) toTensor().multiply(s));
  }

  /**
   * Scales the elements of vector v1 by <code>s</code> and places the results in this vector.
   *
   * @param s scaling factor
   * @param v1 vector to be scaled
   */
  public void scale(IExpr s, Vector3d v1) {
    set(v1.toTensor().map(x -> x.times(s)));
  }

  /**
   * Returns the 2 norm of this vector. This is the square root of the sum of the squares of the
   * elements.
   *
   * @return vector 2 norm
   */
  public IExpr norm() {
    return Vector2Norm.of(toTensor());
  }

  /**
   * Returns the square of the 2 norm of this vector. This is the sum of the squares of the
   * elements.
   *
   * @return square of the 2 norm
   */
  public IExpr normSquared() {
    return Vector2NormSquared.of(toTensor());
  }

  /**
   * Returns the Euclidean distance between this vector and vector v.
   *
   * @return distance between this vector and v
   */
  public IExpr distance(Vector3d v) {
    return Vector2Norm.between(toTensor(), v.toTensor());
  }

  /**
   * Returns the squared of the Euclidean distance between this vector and vector v.
   *
   * @return squared distance between this vector and v
   */
  public IExpr distanceSquared(Vector3d v) {
    return Vector2NormSquared.between(toTensor(), v.toTensor());
  }

  /**
   * Returns the dot product of this vector and v1.
   *
   * @param v1 right-hand vector
   * @return dot product
   */
  public IExpr dot(Vector3d v1) {
    return S.Dot.of(toTensor(), v1.toTensor()); // toTensor().dot(v1.toTensor());
  }

  /** Normalizes this vector in place. */
  public void normalize() {
    IExpr scalar = Vector2NormSquared.of(toTensor());
    double lenSqr = scalar.evalDouble();
    double err = lenSqr - 1;
    if (err > (2 * DOUBLE_PREC) || err < -(2 * DOUBLE_PREC)) {
      double len = Math.sqrt(lenSqr);
      set((IAST) toTensor().divide(F.num(len)));
    }
  }

  /** Sets the elements of this vector to zero. */
  public void setZero() {
    set(F.List(F.C0, F.C0, F.C0));
  }

  /**
   * Sets the elements of this vector to the prescribed values.
   * 
   * @param x value for first element
   * @param y value for second element
   * @param z value for third element
   */
  public void set(IExpr x, IExpr y, IExpr z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  /**
   * Computes the cross product of v1 and v2 and places the result in this vector.
   *
   * @param v1 left-hand vector
   * @param v2 right-hand vector
   */
  public void cross(Vector3d v1, Vector3d v2) {
    set((IAST) S.Cross.of(EvalEngine.get(), v1.toTensor(), v2.toTensor()));
  }

  void set(IAST tensor) {
    set(tensor.arg1(), tensor.arg2(), tensor.arg3());
  }

  /**
   * Sets the elements of this vector to uniformly distributed random values in a specified range,
   * using a supplied random number generator.
   *
   * @param lower lower random value (inclusive)
   * @param upper upper random value (exclusive)
   * @param generator random number generator
   */
  protected void setRandom(ISignedNumber lower, ISignedNumber upper, Random generator) {
    EvalEngine engine = EvalEngine.get();
    IExpr distribution = S.UniformDistribution.of(engine, lower, upper);
    set((IAST) S.RandomVariate.of(engine, distribution, 3));
  }

  /**
   * Returns a string representation of this vector, consisting of the x, y, and z coordinates.
   *
   * @return string representation
   */
  @Override
  public String toString() {
    return x + " " + y + " " + z;
  }

  public IAST toTensor() {
    return F.List(x, y, z);
  }
}
