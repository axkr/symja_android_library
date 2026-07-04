package org.matheclipse.core.expression.data;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Represents a RegionNearestFunction expression stored as data inside the MathEclipse expression
 * tree. * It wraps an {@link IAST} sequence describing the geometric region and exposes an evaluate
 * method to process incoming coordinate queries.
 */
public class RegionNearestFunctionExpr extends DataExpr<IAST> implements Externalizable {

  private static final long serialVersionUID = 1L;

  public RegionNearestFunctionExpr() {
    super(S.RegionNearestFunction, null);
  }

  protected RegionNearestFunctionExpr(IAST region) {
    super(S.RegionNearestFunction, region);
    // Note: For future performance optimization (e.g., if fData is Point[{thousands of points}]),
    // you would initialize a KD-Tree or a Spatial Bounding Box hierarchy here.
  }

  public static RegionNearestFunctionExpr newInstance(IAST region) {
    return new RegionNearestFunctionExpr(region);
  }

  @Override
  public IExpr copy() {
    return new RegionNearestFunctionExpr(fData);
  }

  /**
   * Evaluates the applied point query: RegionNearestFunction(region)(point)
   */
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.isAST1()) {
      IExpr point = ast.arg1();
      if (point.isList()) {
        // Delegate the evaluation back to the standard RegionNearest evaluator logic.
        return engine.evaluate(F.RegionNearest(fData, point));
      }
    } else if (ast.argSize() > 1) {
      // RegionNearestFunction[reg][{x,y}, "Distance"] etc. can be supported here
    }
    return F.NIL;
  }

  @Override
  public int hierarchy() {
    return IExpr.DATAID;
  }

  @Override
  public IAST normal(boolean nilIfUnevaluated) {
    if (fData == null) {
      return F.NIL;
    }
    return F.RegionNearestFunction(fData);
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    this.fData = (IAST) in.readObject();
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeObject(this.fData);
  }

  @Override
  public String toString() {
    return fHead.toString() + "(" + fData.toString() + ")";
  }


}
