// code by jph
package org.matheclipse.core.tensor.chq;

import java.util.Objects;
import org.hipparchus.linear.SingularValueDecomposition;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.tensor.qty.IQuantity;


/**
 * predicate to test if scalar is encoded in exact precision. result is determined by implementation
 * of {@link InexactScalarMarker}.
 * 
 * <p>
 * Examples:
 * 
 * <pre>
 * ExactScalarQ.of(RationalScalar.of(2, 3)) == true
 * ExactScalarQ.of(ComplexScalar.of(3, 4)) == true
 * ExactScalarQ.of(GaussScalar.of(4, 7)) == true
 * ExactScalarQ.of(Quantity.of(3, "m")) == true
 * 
 * ExactScalarQ.of(DoubleScalar.of(3.14)) == false
 * ExactScalarQ.of(DoubleScalar.POSITIVE_INFINITY) == false
 * ExactScalarQ.of(DoubleScalar.INDETERMINATE) == false
 * ExactScalarQ.of(DecimalScalar.of("3.14")) == false
 * ExactScalarQ.of(Quantity.of(2.71, "kg*s")) == false
 * </pre>
 * 
 * <p>
 * The predicate is used to select the appropriate algorithm. For instance, the nullspace for a
 * matrix with all exact scalars is computed using {@link RowReduce}, otherwise
 * {@link SingularValueDecomposition}.
 * 
 * <p>
 * Identical to Mathematica::Exact"Number"Q except for input of type {@link Quantity}.
 * 
 * @see InexactScalarMarker
 * @see IntegerQ
 * @see FiniteScalarQ
 */
public enum ExactScalarQ {
  ;
  /**
   * @param scalar
   * @return true, if scalar is instance of {@link InexactScalarMarker} which evaluates to true
   */
  public static boolean of(IExpr scalar) {
    if (!(scalar instanceof INumber) && !(scalar instanceof IQuantity)) {
      return false;
    }
    if (scalar instanceof IRational) {
      return true;
    }
    if (scalar instanceof IComplex) {
      return true;
    }
    if (scalar instanceof IQuantity) {
      IQuantity q = ((IQuantity) scalar);
      return of(q.value());
    }
    Objects.requireNonNull(scalar);
    return true;
  }

  /**
   * @param scalar
   * @return given scalar
   * @throws Exception if given scalar is not an integer in exact precision
   */
  public static IExpr require(IExpr scalar) {
    if (scalar.isExactNumber()) {
      return scalar;
    }
    throw new IllegalArgumentException("Scalar requires to be exact: " + scalar.toString());
    // throw new Throw(scalar);
  }

  /**
   * @param tensor
   * @return true if any scalar entry in given tensor satisfies the predicate {@link #of(IExpr)}
   */
  public static boolean any(IAST tensor) { 
    return tensor.forAllLeaves(x -> x.isExactNumber(), 1);
//    return tensor.flatten(-1).map(IExpr.class::cast).anyMatch(ExactScalarQ::of);
  }
}
