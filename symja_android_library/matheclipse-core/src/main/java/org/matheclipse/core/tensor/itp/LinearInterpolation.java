// code adapted from https://github.com/datahaki/tensor
package org.matheclipse.core.tensor.itp;

import java.io.Serializable;
import java.util.Objects;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.tensor.sca.Clip;

/**
 * Multi-linear interpolation
 * 
 * <p>
 * valid input for a respective dimension d are in the closed interval
 * <code>[0, Dimensions.of(tensor).get(d) - 1]</code>
 * 
 * <p>
 * Remark: for scalar inverse linear interpolation use {@link Clip#rescale(IExpr)}
 */
public class LinearInterpolation extends AbstractInterpolation implements Serializable {

  /**
   * @param tensor
   * @return
   * @throws Exception if tensor == null
   */
  public static Interpolation of(IAST tensor) {
    return new LinearInterpolation(tensor);
  }

  /**
   * @param clip
   * @return interpolation for evaluation over the unit interval with values ranging linearly
   *         between given clip.min and clip.max
   */
  public static Interpolation of(Clip clip) {
    return of(F.List(clip.min(), clip.max()));
  }

  // ---
  private final IAST tensor;

  private LinearInterpolation(IAST tensor) {
    this.tensor = Objects.requireNonNull(tensor);
  }

  @Override // from Interpolation
  public IAST get(IAST index) {
    throw new UnsupportedOperationException();
    // if (index.isEmpty()) {
    // return tensor.copy();
    // }
    // EvalEngine engine = EvalEngine.get();
    // IAST floor = (IAST) engine.evaluate(F.Floor(index));
    // IAST above = (IAST) engine.evaluate(F.Ceiling(index));
    // IAST width = (IAST) above.subtract(floor).plus(F.C1);
    // List<Integer> fromIndex = Convert.toIntegerList(floor);
    // List<Integer> dimensions = Convert.toIntegerList(width);
    // IAST block = tensor.block(fromIndex, dimensions);
    // IAST weights = (IAST) engine.evaluate(F.Transpose( F.List( //
    // above.subtract(index), //
    // index.subtract(floor))));
    //
    // for (int i = 1; i < weights.size(); i++) {
    // IAST weight = (IAST) weights.get(i);
    // block = block.argSize() == 1 //
    // ? (IAST) block.get(1)
    // : (IAST) engine.evaluate(F.Dot(weight, block));
    // }
    // return block;
  }

  @Override // from Interpolation
  public IAST at(IExpr index) {
    IExpr floor = index.floor();
    IExpr remain = index.subtract(floor);
    int below = floor.toIntDefault();
    if (remain.isZero()) {
      // TODO start: fix this hack
      if (below + 1 == tensor.size()) {
        return (IAST) tensor.get(below);
      }
      // end: fix this hack

      return (IAST) tensor.get(below + 1);
    }
    EvalEngine engine = EvalEngine.get();
    return (IAST) engine.evaluate(//
        F.Dot(//
            F.List(remain.one().subtract(remain), remain), //
            tensor.slice(below + 1, below + 3)));
  }
}
