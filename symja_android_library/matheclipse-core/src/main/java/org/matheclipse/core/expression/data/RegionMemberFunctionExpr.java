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
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * The membership test of a region as a reusable function object. It wraps the {@link IAST} of the
 * region so that <code>RegionMember(region)(point)</code> gives the same answer as
 * <code>RegionMember(region, point)</code> without re-parsing the region for every point.
 */
public class RegionMemberFunctionExpr extends DataExpr<IAST> implements Externalizable {

  private static final long serialVersionUID = 1L;

  public RegionMemberFunctionExpr() {
    super(S.RegionMemberFunction, null);
  }

  protected RegionMemberFunctionExpr(IAST region) {
    super(S.RegionMemberFunction, region);
  }

  public static RegionMemberFunctionExpr newInstance(IAST region) {
    return new RegionMemberFunctionExpr(region);
  }

  @Override
  public IExpr copy() {
    return new RegionMemberFunctionExpr(fData);
  }

  /**
   * Evaluate the applied point query <code>RegionMemberFunction(region)(point)</code>. A list of
   * points is tested point by point, which is the batch use case the function object exists for.
   */
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    if (ast.isAST1()) {
      IExpr point = ast.arg1();
      if (point.isListOfLists()) {
        IAST points = (IAST) point;
        IASTAppendable result = F.ListAlloc(points.argSize());
        for (int i = 1; i <= points.argSize(); i++) {
          result.append(F.RegionMember(fData, points.get(i)));
        }
        return engine.evaluate(result);
      }
      if (point.isList()) {
        return engine.evaluate(F.RegionMember(fData, point));
      }
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
    return F.RegionMemberFunction(fData);
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
