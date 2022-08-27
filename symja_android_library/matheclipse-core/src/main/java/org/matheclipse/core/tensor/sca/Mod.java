// code adapted from https://github.com/datahaki/tensor
package org.matheclipse.core.tensor.sca;

import java.util.Objects;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.tensor.api.ScalarUnaryOperator;

/**
 * our implementation is <em>not</em> consistent with Mathematica for negative, and complex n.
 * 
 */
// TODO TENSOR NUM implement https://en.wikipedia.org/wiki/Modular_arithmetic
public class Mod implements ScalarUnaryOperator {
  /** @param n
   * @return remainder on division by n */
  public static Mod function(IExpr n) {
    return function(n, n.zero());
  }

  /** @param n
   * @return remainder on division by n */
  public static Mod function(Number n) {
    return function(F.num(n.doubleValue()));
  }

  /** @param n
   * @param d offset
   * @return remainder on division by n with offset d */
  public static Mod function(IExpr n, IExpr d) {
    if (n.isZero()) {
      throw new IllegalArgumentException("");
      // throw new Throw(n);
    }
    return new Mod(n, Objects.requireNonNull(d));
  }

  /** @param n
   * @param d offset
   * @return remainder on division by n with offset d */
  public static Mod function(Number n, Number d) {
    return function(F.num(n.doubleValue()), F.num(d.doubleValue()));
  }

  // ---
  private final IExpr n;
  private final IExpr d;

  private Mod(IExpr n, IExpr d) {
    this.n = n;
    this.d = d;
  }

  @Override
  public IExpr apply(IExpr scalar) {
    IExpr loops = scalar.subtract(d).divide(n).floor();
    return scalar.subtract(n.multiply(loops));
    // return scalar.subtract(FiniteScalarQ.require(loops).multiply(n));
  }

  @SuppressWarnings("unchecked")
  public <T extends IAST> T of(T tensor) {
    return (T) tensor.map(this);
  }

  @Override // from Object
  public String toString() {
    return "Mod";
    // return MathematicaFormat.concise("Mod", n, d);
  }
}
