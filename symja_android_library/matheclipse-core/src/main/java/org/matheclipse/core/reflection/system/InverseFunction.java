package org.matheclipse.core.reflection.system;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 *
 *
 * <pre>
 * InverseFunction(head)
 * </pre>
 *
 * <blockquote>
 *
 * <p>returns the inverse function for the symbol <code>head</code>.
 *
 * </blockquote>
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * &gt;&gt; InverseFunction(Sin)
 * ArcSin
 * </pre>
 */
public class InverseFunction extends AbstractFunctionEvaluator {
  private static final Logger LOGGER = LogManager.getLogger();

  public InverseFunction() {}

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1();
    if (arg1.isFunction()) {
      IExpr f = arg1.first();
      if (f.isAST()) {
        ISymbol dummy = F.Dummy();
        IAST[] arr = Eliminate.eliminateSlot(F.Equal(f, dummy), F.Slot1, engine);
        if (arr != null) {
          return F.Function(F.subst(arr[1].second(), F.Rule(dummy, F.Slot1)));
        }
      }
    } else if (arg1.isBuiltInSymbol()) {
      if (arg1.equals(S.Abs)) {
        LOGGER.log(engine.getLogLevel(),
            "InverseFunction: using of inverse functions may omit some values.");
      }
      IExpr temp = getUnaryInverseFunction((ISymbol) arg1);
      if (temp != null) {
        return temp;
      }
    } else if (arg1.isASTSizeGE(S.Composition, 2) || arg1.isASTSizeGE(S.RightComposition, 2)) {
      IAST composition = (IAST) arg1;
      if (composition.forAll(x -> x.isSymbol())) {
        IASTAppendable result = F.ast(composition.head(), composition.size());
        for (int i = composition.size() - 1; i >= 1; i--) {
          result.append(F.InverseFunction(composition.get(i)));
        }
        return result;
      }
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_1;
  }

  /**
   * Get the inverse function symbol if possible.
   *
   * @param headSymbol the symbol which represents a function name (i.e. <code>Cos, Sin, ArcSin,...
   *     </code>)
   * @return <code>null</code> if there is no inverse function defined.
   */
  public static IExpr getUnaryInverseFunction(ISymbol headSymbol) {
    return F.UNARY_INVERSE_FUNCTIONS.get(headSymbol);
  }

  /**
   * Get a new constructed inverse function AST from the given <code>ast</code>, with empty
   * arguments (i.e. <code>inverseAST.size()==1)</code>.
   *
   * @param ast the AST which represents a function (i.e. <code>Cos(x), Sin(x), ArcSin(x),...</code>
   *     )
   * @param realAbs return inverse for <code>RealBas()</code> function
   * @return <code>null</code> if there is no inverse function defined.
   */
  public static IASTAppendable getUnaryInverseFunction(IAST ast, boolean realAbs) {
    IExpr expr = ast.head();
    if (expr.isSymbol()) {
      IExpr inverseSymbol = F.UNARY_INVERSE_FUNCTIONS.get(expr);
      if (inverseSymbol != null) {
        return F.ast(inverseSymbol);
      }
      if (realAbs) {
        if (ast.isAST(S.RealAbs, 2)) {
          return F.ast(F.Function(F.Times(F.CN1, F.Slot1)));
        }
      }
    }
    return F.NIL;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(ISymbol.NHOLDALL);
  }
}
