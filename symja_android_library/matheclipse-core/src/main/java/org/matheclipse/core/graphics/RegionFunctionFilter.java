package org.matheclipse.core.graphics;

import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;

/**
 * The {@code RegionFunction} of a plot: the predicate that decides which of the sampled points are
 * part of the picture.
 *
 * <p>
 * A point belongs to the region when the predicate applied to it yields {@code True}. Anything else
 * - {@code False}, an unevaluated expression, a predicate that threw - leaves the point out, and a
 * point that is left out is treated exactly the way a value that is not a number is: the surface
 * gets a hole, the curve is broken, the raster cell stays transparent. That is what makes
 * {@code BoundaryStyle} outline the edge of a region without having to know a region was asked for.
 *
 * <p>
 * How many coordinates a point is described by depends on the plot: {@code Plot} passes
 * {@code (x, y)}, {@code ContourPlot} passes {@code (x, y, f)}, {@code SphericalPlot3D} passes
 * {@code (x, y, z, theta, phi, r)}. Passing more than the predicate declares is deliberate and
 * safe: {@code Function} binds the parameters it names and discards the rest, and {@code Slot} only
 * looks up the positions the body mentions. So a caller hands over the whole tuple its family
 * documents and a predicate written as {@code Function({x, y}, y > 0)} still works.
 */
public final class RegionFunctionFilter {

  private final IExpr predicate;
  private final EvalEngine engine;

  private RegionFunctionFilter(IExpr predicate, EvalEngine engine) {
    this.predicate = predicate;
    this.engine = engine;
  }

  /**
   * The filter an option value asks for, or {@code null} when it asks for no filtering at all.
   *
   * <p>
   * Returning {@code null} rather than a filter that always says yes is what keeps the default
   * free: a plot that was never given a region does not evaluate a predicate once per sample.
   *
   * @param option the {@code RegionFunction} option value
   * @param engine the evaluation engine the predicate is applied in
   */
  public static RegionFunctionFilter of(IExpr option, EvalEngine engine) {
    if (option == null || !option.isPresent() || option == S.Automatic || option.isAutomatic()
        || option.isNone() || option.isTrue()) {
      return null;
    }
    // (True&) is the default, so a call that writes the default
    // out in full costs nothing either
    if (option.isAST(S.Function, 2) && option.first().isTrue()) {
      return null;
    }
    if (option.isAST(S.Function, 3) && option.second().isTrue()) {
      return null;
    }
    return new RegionFunctionFilter(option, engine);
  }

  /**
   * Whether the point belongs to the region.
   *
   * @param coordinates the point, in the order the calling plot's family documents
   */
  public boolean accepts(double... coordinates) {
    IExpr[] args = new IExpr[coordinates.length];
    for (int i = 0; i < coordinates.length; i++) {
      if (!Double.isFinite(coordinates[i])) {
        // a coordinate that is not a number is not in any region; it is already a hole
        return false;
      }
      args[i] = F.num(coordinates[i]);
    }
    return apply(args);
  }

  /**
   * Whether the point belongs to the region, for the plots that describe a point with something
   * other than real coordinates.
   *
   * <p>
   * {@code ComplexPlot} and {@code ComplexPlot3D} hand over the complex sample point and the
   * complex value the function took there, rather than a tuple of reals, so that a predicate can be
   * written as {@code Function({z, f}, Abs(z) < 2)} or {@code Function({z, f}, 1 < Abs(f) < 2)}.
   *
   * @param arguments the point, in the order the calling plot's family documents
   */
  public boolean accepts(IExpr... arguments) {
    for (IExpr argument : arguments) {
      if (argument == null || !argument.isNumber()) {
        // a point the function has no number at is already a hole; it is in no region
        return false;
      }
    }
    return apply(arguments);
  }

  private boolean apply(IExpr[] arguments) {
    try {
      return engine.evaluate(F.ast(arguments, predicate)).isTrue();
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      // a predicate that throws at a pole leaves that point out rather than failing the plot
      return false;
    }
  }
}
