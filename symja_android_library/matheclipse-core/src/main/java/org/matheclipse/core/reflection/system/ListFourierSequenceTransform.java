package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <code>ListFourierSequenceTransform(list, w, n0)</code> is the discrete-time Fourier transform of
 * a finite sequence whose first element has the index <code>n0</code>, by default
 * <code>0</code>: the sum of <code>list[[j]]*E^(-I*b*(n0+j-1)*w)</code> with the factor
 * <code>(Abs(b)/(2*Pi))^((1-a)/2)</code> for <code>FourierParameters-&gt;{a,b}</code>.
 */
public class ListFourierSequenceTransform extends AbstractFunctionOptionEvaluator {

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] option, final EvalEngine engine,
      IAST originalAST) {
    if (argSize < 2 || ast.arg1().isVector() < 0) {
      return F.NIL;
    }
    int start = 0;
    if (argSize == 3) {
      start = ast.arg3().toIntDefault();
      if (F.isNotPresent(start)) {
        return F.NIL;
      }
    }
    IExpr[] ab = FourierSeries.fourierParametersAB(option[0]);
    if (ab == null) {
      return F.NIL;
    }
    IAST list = (IAST) ast.arg1().normal(false);
    IASTAppendable sum = F.PlusAlloc(list.argSize());
    for (int j = 1; j <= list.argSize(); j++) {
      sum.append(F.Times(list.get(j),
          F.Exp(F.Times(F.CNI, ab[1], F.ZZ((long) start + j - 1), ast.arg2()))));
    }
    return F.Times(
        F.Power(F.Times(F.Abs(ab[1]), F.Power(F.C2Pi, F.CN1)),
            F.Times(F.C1D2, F.Subtract(F.C1, ab[0]))),
        sum);
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_2_3;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    setOptions(newSymbol, new IBuiltInSymbol[] {S.FourierParameters}, new IExpr[] {S.Automatic});
  }
}
