package org.matheclipse.core.sympy.series;

import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.builtin.SeriesFunctions;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.ASTSeriesData;
import org.matheclipse.core.expression.BuiltInDummy;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISeqBase;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.sympy.core.Expr;
import org.matheclipse.core.sympy.core.Operations;

public class Formal {
  public final static IBuiltInSymbol COEFF = new BuiltInDummy("$coeff", new Coeff());

  public static IAST Coeff(IExpr p, IExpr x, IExpr n) {
    return F.ternaryAST3(COEFF, p, x, n);
  }

  private static class Coeff extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr p = ast.arg1();
      IExpr x = ast.arg2();
      int n = ast.toIntDefault();
      if (n < 0) {
        // Non-negative machine-sized integer expected at position `2` in `1`.
        return IOFunctions.printMessage(COEFF, "intnm", F.List(F.C3, ast), engine);
      }
      if (p.isPolynomial(x) && n >= 0) {
        return Expr.coeff(p, x, n, false, true);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      // newSymbol.setAttributes(ISymbol.LISTABLE);
    }
  }

  public static IExpr fps(IExpr f) {
    return fps(f, F.NIL, F.C0, F.C1, true, 4, true, false);
  }

  public static IExpr fps(IExpr f, IExpr x) {
    return fps(f, x, F.C0, F.C1, true, 4, true, false);
  }

  public static IExpr fps(IExpr f, IExpr x, char dir) {
    if (dir == '-') {
      return fps(f, x, F.C0, F.CN1, true, 4, true, false);
    } else if (dir == '+') {
      return fps(f, x, F.C0, F.C1, true, 4, true, false);
    } else {
      throw new IllegalArgumentException("Dir must be '+' or '-'");
    }
  }

  /**
   * Generates Formal Power Series of ``f``
   * 
   * @return
   */
  public static IExpr fps(IExpr f, IExpr x, INumber x0, IExpr dir, boolean hyper, int order,
      boolean rational, boolean full) {
    if (x.isNIL()) {
      // determine vars
      VariablesSet varSet = new VariablesSet(f);
      if (varSet.size() == 0) {
        return f;
      }
      if (varSet.size() != 1) {
        throw new UnsupportedOperationException("Multivariate power series not supported");
      }
      x = varSet.getArrayList().get(0);
    }

    IAST triple = compute_fps(f, x, x0, dir, hyper, order, rational, full);
    if (triple.isNIL()) {
      return f;
    }
    ASTSeriesData series = SeriesFunctions.seriesDataRecursive(f, x, x0, order, EvalEngine.get());
    if (series != null) {
      return series;
    }
    return f;
  }

  private static IAST compute_fps(IExpr f, IExpr x, INumber x0, IExpr dir, boolean hyper, int order,
      boolean rational, boolean full) {
    if (!f.has(x)) {
      return F.NIL;
    }
    if (dir.isOne()) {
      dir = F.C1;
    } else if (dir.isMinusOne()) {
      dir = F.CN1;
    } else {
      throw new IllegalArgumentException("Dir must be '1' or '-1'");
    }
    return _compute_fps(f, x, x0, dir, hyper, order, rational, full);
  }

  /**
   * Recursive wrapper to compute_fps
   * 
   * @param f
   * @param x
   * @param x0
   * @param dir
   * @param hyper
   * @param order
   * @param rational
   * @param full
   */
  private static IAST _compute_fps(IExpr f, IExpr x, INumber x0, IExpr dir, boolean hyper,
      int order, boolean rational, boolean full) {
    if (x0.isInfinity() || x0.isNegativeInfinity()) {
      if (x0.isInfinity()) {
        dir = F.C1;
      } else {
        dir = F.CN1;
      }
      IExpr xInverse = x.inverse();
      IExpr temp = f.subs(x, xInverse);
      IAST result = _compute_fps(temp, x, F.C0, dir, hyper, order, rational, full);
      if (result.isNIL()) {
        return F.NIL;
      }
      return F.List(//
          result.arg1(), //
          result.arg2().subs(x, xInverse), //
          result.arg3().subs(x, xInverse));

    } else if (!x0.isZero() || dir.isMinusOne()) {
      IExpr rep;
      IExpr rep2;
      IExpr rep2b;
      if (dir.isMinusOne()) {
        rep = x.negate().plus(x0);
        rep2 = x.negate();
        rep2b = x0;
      } else {
        rep = x.plus(x0);
        rep2 = x;
        rep2b = x0.negate();
      }
      IExpr temp = f.subs(x, rep);
      IAST result = _compute_fps(temp, x, F.C0, F.C1, hyper, order, rational, full);
      if (result.isNIL()) {
        return F.NIL;
      }
      IExpr rep2PlusRep2b = rep2.plus(rep2b);
      return F.List(//
          result.arg1(), //
          result.arg2().subs(x, rep2PlusRep2b), //
          result.arg3().subs(x, rep2PlusRep2b));
    }
    if (f.isPolynomial(x)) {
      IExpr k = F.Dummy('k');
      IAST coeff = Coeff(f, x, k);
      ISeqBase ak = Sequences.sequence(coeff, F.List(k, F.C1, F.oo));
      ISeqBase xk = Sequences.sequence(F.Power(x0, k), F.List(k, F.C0, F.oo));
      IExpr ind = Expr.coeff(f, x, 0);
      return F.List(ak.asAST(), xk.asAST(), ind);
    }

    if (f.isPlus()) {
      boolean result = false;
      ISeqBase ak = Sequences.sequence(F.C0, F.List(F.C0, F.oo));
      IExpr ind = F.C0;
      IExpr xk = F.NIL;
      IASTMutable plusAST = Operations.makeArgs(S.Plus, f);
      for (int i = 1; i < plusAST.size(); i++) {
        IExpr t = plusAST.get(i);
        IAST res = _compute_fps(t, x, F.C0, F.C1, hyper, order, rational, full);
        if (res.isPresent()) {
          if (!result) {
            result = true;
            xk = res.arg2();
          }
          IExpr s = ak.start();
          IExpr f2 = ((ISeqBase) res).start();
          if (f2.greater(s).isTrue()) {
            result = true;
            xk = res.arg2();
          } else {
            // TODO
            // IExpr save= Add(*[z[0]*z[1] for z in zip(seq[0:(f - s)], xk[s:f])]);
          }
        }
      }
      return F.List(ak.asAST(), xk, ind);
    }
    return F.NIL;
  }



}
