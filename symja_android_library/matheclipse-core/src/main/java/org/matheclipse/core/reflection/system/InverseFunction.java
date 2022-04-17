package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.Collections;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
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
 * <p>
 * returns the inverse function for the symbol <code>head</code>.
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
        LOGGER.log(engine.getLogLevel(),
            "InverseFunction: using of inverse functions may omit some values.");
      }
      if (ast.isAST1()) {
        IExpr temp = getUnaryInverseFunction((ISymbol) arg1);
        if (temp != null) {
          return temp;
        }
      } else if (ast.isAST3()) {
        int argSize = ast.arg3().toIntDefault();
        if (argSize == 2) {
          int argPosition = ast.arg2().toIntDefault();
          if (argPosition == 1 || argPosition == 2) {
            IExpr temp = getBinaryInverseFunction((ISymbol) arg1, argPosition, argSize);
            if (temp != null) {
              return temp;
            }
          }
        }
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
    return IFunctionEvaluator.ARGS_1_3;
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

  public static IExpr getBinaryInverseFunction(ISymbol headSymbol, int argPosition, int argSize) {
    int id = headSymbol.ordinal();
    if (id > 0) {
      switch (id) {
        case ID.ArcTan:
          if (argPosition == 1 && argSize == 2) {
            // I*(1+Cos(2*#)+I*Sin(2*#))*#2)/(-1+Cos(2*#)+I*Sin(2*#)
            IExpr arg = F.Times(F.C2, F.Slot1);
            IExpr numer =
                F.Times(F.CI, F.Plus(F.C1, F.Cos(arg), F.Times(F.CI, F.Sin(arg)), F.Slot2));
            IExpr denom = F.Plus(F.CN1, F.Cos(arg), F.Times(F.CI, F.Sin(arg)));
            return F.Function(F.Times(numer, F.Power(denom, F.CN1)));
          } else if (argPosition == 2 && argSize == 2) {
            // -((I*(-1 +Cos(2*#2)+I*Sin(2*#2))*#)/(1+Cos(2*#2)+I*Sin(2*#2)))
            IExpr arg = F.Times(F.C2, F.Slot2);
            IExpr numer =
                F.Times(F.CNI, F.Plus(F.CN1, F.Cos(arg), F.Times(F.CI, F.Sin(arg))), F.Slot1);
            IExpr denom = F.Plus(F.C1, F.Cos(arg), F.Times(F.CI, F.Sin(arg)));
            return F.Function(F.Times(numer, F.Power(denom, F.CN1)));
          }
          break;
        case ID.InverseJacobiCD:
          if (argPosition == 1 && argSize == 2) {
            return S.JacobiCD;
          }
          break;
        case ID.JacobiCD:
          if (argPosition == 1 && argSize == 2) {
            return S.InverseJacobiCD;
          }
          break;
        case ID.InverseJacobiCN:
          if (argPosition == 1 && argSize == 2) {
            return S.JacobiCN;
          }
          break;
        case ID.JacobiCN:
          if (argPosition == 1 && argSize == 2) {
            return S.InverseJacobiCN;
          }
          break;
        case ID.InverseJacobiDN:
          if (argPosition == 1 && argSize == 2) {
            return S.JacobiDN;
          }
          break;
        case ID.JacobiDN:
          if (argPosition == 1 && argSize == 2) {
            return S.InverseJacobiDN;
          }
          break;

        case ID.InverseJacobiSD:
          if (argPosition == 1 && argSize == 2) {
            return S.JacobiSD;
          }
          break;
        case ID.JacobiSD:
          if (argPosition == 1 && argSize == 2) {
            return S.InverseJacobiSD;
          }
          break;
        case ID.InverseJacobiSN:
          if (argPosition == 1 && argSize == 2) {
            return S.JacobiSN;
          }
          break;
        case ID.JacobiSN:
          if (argPosition == 1 && argSize == 2) {
            return S.InverseJacobiSN;
          }
          break;
        case ID.InverseJacobiSC:
          if (argPosition == 1 && argSize == 2) {
            return S.JacobiSC;
          }
          break;
        case ID.JacobiSC:
          if (argPosition == 1 && argSize == 2) {
            return S.InverseJacobiSC;
          }
          break;
      }
    }
    return null;
  }

  /**
   * Get a new constructed inverse function AST from the given <code>ast</code>, with empty
   * arguments (i.e. <code>inverseAST.size()==1)</code>.
   *
   * @param ast the AST which represents a function (i.e. <code>Cos(x), Sin(x), ArcSin(x),...</code>
   *        )
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
   *         rule which is used for variable elimination in index <code>[1]</code>.
   */
  public static IAST[] eliminateSlotInverseFunction(IExpr xFunction, ISymbol dummy,
      EvalEngine engine) {
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
