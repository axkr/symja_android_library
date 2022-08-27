// code adapted from https://github.com/datahaki/tensor
package org.matheclipse.core.tensor.itp;

import java.util.Objects;
import java.util.function.Function;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.tensor.ext.Integers;

/**
 * DeBoor denotes the function that is defined by control points over a sequence of knots.
 * 
 */
public class DeBoor implements Function<IExpr, IExpr> {
  final BinaryAverage binaryAverage;
  final int degree;
  final IAST knots;
  final IAST control;

  /**
   * DeBoor denotes the function that is defined by control points over a sequence of knots.
   * 
   * @param binaryAverage
   * @param degree
   * @param knots
   * @param control
   */
  public DeBoor(BinaryAverage binaryAverage, int degree, IAST knots, IAST control) {
    this.binaryAverage = binaryAverage;
    this.degree = degree;
    this.knots = knots;
    this.control = control;
  }

  /**
   * @param binaryAverage non-null
   * @param knots vector of length degree * 2
   * @param control points of length degree + 1
   * @return
   * @throws IllegalArgumentException if given knots is not a vector, or degree cannot be
   *         established
   */
  public static DeBoor of(BinaryAverage binaryAverage, IAST knots, IAST control) {
    int length = knots.argSize();
    int degree = length / 2;
    Integers.requireEquals(control.argSize(), degree + 1);
    if (Integers.isEven(length)) {
      if (knots.isVector() <= 0) {
        throw new IllegalArgumentException("DeBoor: knots is not a vector");
      }
      return new DeBoor(Objects.requireNonNull(binaryAverage), degree, knots, // VectorQ.require(knots),
          control);
    }
    throw new IllegalArgumentException("DeBoor: even length expected");
    // throw new Throw(knots, control);
  }

  @Override
  public IExpr apply(IExpr x) {
    // d is modified over the course of the algorithm
    IExpr[] d = control.toArray(1);// .toArray(IAST[]::new);
    EvalEngine engine = EvalEngine.get();
    for (int r = 1; r < degree + 1; ++r) {

      for (int j = degree; j >= r; --j) {
        // knots max index = degree - 1
        IExpr kj1 = knots.get(j);
        // knots max index = degree + degree - 1
        IExpr den = engine.evaluate(knots.get(j + degree - r + 1).subtract(kj1));
        d[j] = den.isZero() //
            ? d[j - 1]
            : binaryAverage.split(d[j - 1], d[j], x.subtract(kj1).divide(den)); // control max index
                                                                                // = degree - 1
      }

    }
    return d[degree]; // control max index = degree
  }
}
