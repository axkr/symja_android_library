package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <code>ConstantRegionQ(reg)</code> - test whether <code>reg</code> is a region which does not
 * depend on any parameter.
 *
 * <p>
 * A region is constant when it is a region at all and none of the numbers which describe it is
 * symbolic - <code>Disk({0,0}, 2)</code> is constant, <code>Disk({0,0}, r)</code> is not. Only a
 * constant region can be tested for membership of a numeric point or measured to a number.
 */
public class ConstantRegionQ extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1();

    // Unwrap Region display wrapper if present
    if (arg1.isAST(S.Region, 1)) {
      arg1 = arg1.first();
    }
    if (!arg1.isAST() || RegionEmbeddingDimension.getEmbeddingDimension(arg1) < 0) {
      if (!isFormulaRegion(arg1)) {
        return S.False;
      }
    }
    return F.booleSymbol(isConstant((IAST) arg1));
  }

  /** A region which is described by a formula rather than by an explicit shape. */
  private static boolean isFormulaRegion(IExpr expr) {
    if (!expr.isAST()) {
      return false;
    }
    switch (((IAST) expr).headID()) {
      case ID.ImplicitRegion:
      case ID.ParametricRegion:
      case ID.EmptyRegion:
      case ID.FullRegion:
      case ID.RegionUnion:
      case ID.RegionIntersection:
      case ID.RegionDifference:
      case ID.RegionSymmetricDifference:
        return true;
      default:
        return false;
    }
  }

  /**
   * The region has no free parameter. The bound variables of the formula regions are not free, so
   * they are excluded from the test.
   */
  private static boolean isConstant(IAST reg) {
    switch (reg.headID()) {
      case ID.ImplicitRegion:
      case ID.ParametricRegion:
        // the variables in the second argument are bound by the region itself
        return reg.argSize() == 2 && boundVariablesCover(reg);
      default:
        return reg.isFree(x -> x.isSymbol() && !x.isBuiltInSymbol(), true);
    }
  }

  /** Every symbol of the formula is one of the variables which the region binds. */
  private static boolean boundVariablesCover(IAST reg) {
    IExpr variables = reg.arg2();
    if (!variables.isList()) {
      return false;
    }
    IAST variableList = (IAST) variables;
    return reg.arg1().isFree(x -> {
      if (!x.isSymbol() || x.isBuiltInSymbol()) {
        return false;
      }
      for (int i = 1; i <= variableList.argSize(); i++) {
        IExpr variable = variableList.get(i);
        if (variable.isList3()) {
          variable = ((IAST) variable).arg1();
        }
        if (variable.equals(x)) {
          return false;
        }
      }
      return true;
    }, true);
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }
}
