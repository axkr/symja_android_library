package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.Collections;
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
      IExpr xFunction = arg1.first();
      ISymbol dummy = F.Dummy();
      if (xFunction.isAST()) {
        IAST[] arr = InverseFunction.eliminateSlotInverseFunction(xFunction, dummy, engine);
        if (arr != null) {
          return F.Function(F.subst(arr[1].second(), F.Rule(dummy, F.Slot1)));
        }
      }
    } else if (arg1.isBuiltInSymbol()) {
      if (arg1.equals(S.Abs)) {
        LOGGER.log(
            engine.getLogLevel(),
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
    return F.getUnaryInverseFunction(headSymbol);
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
      IExpr inverseSymbol = F.getUnaryInverseFunction(expr);
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

  /**
   * @param xFunction the function which should be inverted
   * @param dummy a temporary variable
   * @return <code>null</code> if we can't eliminate an equation from the list for the given <code>
   *     variable</code> or the eliminated list of equations in index <code>[0]</code> and the last
   *     rule which is used for variable elimination in index <code>[1]</code>.
   */
  public static IAST[] eliminateSlotInverseFunction(
      IExpr xFunction, ISymbol dummy, EvalEngine engine) {
    final IAST equalAST = F.Equal(xFunction, dummy);
    Eliminate.VariableCounterVisitor exprAnalyzer;
    ArrayList<Eliminate.VariableCounterVisitor> analyzerList =
        new ArrayList<Eliminate.VariableCounterVisitor>();
    exprAnalyzer = new Eliminate.VariableCounterVisitor(equalAST, F.Slot1);
    equalAST.accept(exprAnalyzer);
    analyzerList.add(exprAnalyzer);
    Collections.sort(analyzerList);

    IAST[] slotEliminated = Eliminate.eliminateOneVariable(analyzerList, F.Slot1, false, engine);
    if (slotEliminated != null && slotEliminated[1].isList()) {
      // List results are not allowed in S.InverseFunction
      return null;
    }
    return slotEliminated;
  }
}
