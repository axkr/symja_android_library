package org.matheclipse.core.sympy.series;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import org.matheclipse.core.builtin.IOFunctions;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.BuiltInDummy;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IPair;
import org.matheclipse.core.interfaces.ISeqBase;
import org.matheclipse.core.interfaces.ISeriesBase;
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

  public static class FormalPowerSeries implements ISeriesBase {

    IExpr function;
    IExpr ak;
    IExpr k;
    IExpr dir;
    IAST resultTriple;
    ISeqBase ak_seq;
    ISeqBase fact_seq;
    ISeqBase bell_coeff_seq;
    ISeqBase sign_seq;

    // new FormalPowerSeries(f, x, x0, dir, triple);
    public FormalPowerSeries(IExpr f, IExpr x, IExpr x0, IExpr dir, IAST resultTriple) {
      this.function = f;
      this.k = x;
      this.ak = x0;
      this.dir = dir;
      this.resultTriple = resultTriple;
      // Sequences.SeqFormula ak = (Sequences.SeqFormula) resultTriple.arg1();
      ak_seq = Sequences.sequence(ak, F.List(k, F.C1, F.CInfinity));
      fact_seq = Sequences.sequence(F.Factorial(k), F.List(k, F.C1, F.CInfinity));
      bell_coeff_seq = ak_seq.mul(fact_seq);
      sign_seq = Sequences.sequence(F.List(-1, 1), F.List(k, F.C1, F.oo));
    }

    protected static final class FormalPowerSeriesIterator implements Iterator<IExpr> {

      private IExpr length;

      private int currentIndex;

      private ISeriesBase self;

      public FormalPowerSeriesIterator(ISeriesBase self, IExpr length) {
        this.length = length;
        this.currentIndex = 0;
        this.self = self;
      }

      // @Override
      // public void setLength(int n) {
      // this.length = n;
      // };

      @Override
      public boolean hasNext() {
        return length.greaterThan(currentIndex).isTrue();
      }

      @Override
      public IExpr next() {
        if (length.lessEqualThan(currentIndex).isTrue()) {
          throw new NoSuchElementException();
        }
        IExpr pt = self._ith_point(currentIndex++);
        return self.term(pt);
      }

    }

    @Override
    public FormalPowerSeriesIterator iterator() {
      return new FormalPowerSeriesIterator(this, length());
    }


    @Override
    public IExpr args0() {
      return function;
    }

    @Override
    public IExpr args1() {
      return k;
    }

    @Override
    public IExpr function() {
      return function;
    }

    public IExpr x() {
      return k;
    }

    public IExpr x0() {
      return ak;
    }

    public IExpr dir() {
      return dir;
    }

    public IExpr ak() {
      return resultTriple.arg1();
    }

    public IExpr xk() {
      return resultTriple.arg2();
    }

    public IExpr ind() {
      return resultTriple.arg3();
    }

    @Override
    public IAST interval() {
      return F.Interval(F.C0, F.CInfinity);
    }

    private IExpr _get_pow_x(IExpr term) {
      // """Returns the power of x in a term."""
      IPair asBaseExp = Expr.asIndependent(term, F.List(x())).second().asBaseExp();
      IExpr xterm = asBaseExp.first();
      IExpr pow_x = asBaseExp.second();
      if (!xterm.has(x())) {
        return F.C0;
      }
      return pow_x;
    }

    public IExpr polynomial() {
      return polynomial(6);
    }

    public IExpr polynomial(int n) {
      // """
      // Truncated series as polynomial.
      //
      // Explanation
      // ===========
      //
      // Returns series expansion of ``f`` upto order ``O(x**n)``
      // as a polynomial(without ``O`` term).
      // """
      IASTAppendable terms = F.PlusAlloc(n);
      Set<IExpr> sym = freeSymbols();// self.free_symbols;
      Iterator<IExpr> iter = iterator();
      int i = 0;
      while (iter.hasNext()) {
        IExpr t = iter.next();
        i++;
        IExpr xp = _get_pow_x(t);
        if (xp.has(sym)) {
          xp = xp.asCoeffAdd(sym).first();
        }
        if (xp.greater(F.ZZ(n)).isTrue()) {
          break;
        } else if (xp.isInteger() && i == n + 1) {
          break;
        } else if (!t.isZero()) {
          terms.append(t);
        }
      }
      return terms.oneIdentity0();
    }


    @Override
    public IExpr start() {
      return interval().arg1().first();
    }

    @Override
    public IExpr stop() {
      return interval().arg1().second();
    }

    @Override
    public IExpr length() {
      return F.CInfinity;
    }

    @Override
    public String toString() {
      IExpr polynomial = polynomial();
      return polynomial.toString();
    }

    @Override
    public IExpr _eval_term(IExpr pt) {
      IExpr term;
      try {
        IExpr pt_xk = Expr.coeff(xk(), pt);
        IExpr pt_ak = F.evalSimplify(Expr.coeff(ak(), pt)); // Simplify the coefficients
        term = pt_ak.times(pt_xk);
        // } catch( IndexError ie) {
      } catch (RuntimeException rex) {
        term = F.C0;
      }

      if (ind().isPresent()) {
        IExpr ind = F.C0;
        Set<IExpr> sym = freeSymbols(); // self.free_symbols
        IASTMutable plusArgs = Operations.makeArgs(S.Plus, ind);
        for (int i = 1; i < plusArgs.size(); i++) {
          IExpr t = plusArgs.get(i);
          IExpr pow_x = _get_pow_x(t);
          if (pow_x.has(sym)) {
            pow_x = pow_x.asCoeffAdd(sym).first();
          }
          if (pt.isZero() && pow_x.lessThan(1).isTrue()) {
            ind = ind.plus(t);
          } else if (pow_x.greaterEqualThan(pt).isTrue()
              && pow_x.lessThan(pt.plus(F.C1)).isTrue()) {
            ind = ind.plus(t);
          }
          term = term.plus(ind);
        }
      }
      return F.evalCollect(term, x());
    }
  }

  public static Object fps(IExpr f) {
    return fps(f, F.NIL, F.C0, F.C1, true, 4, true, false);
  }

  public static Object fps(IExpr f, IExpr x) {
    return fps(f, x, F.C0, F.C1, true, 4, true, false);
  }

  public static Object fps(IExpr f, IExpr x, char dir) {
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
  public static Object fps(IExpr f, IExpr x, INumber x0, IExpr dir, boolean hyper, int order,
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
    return new FormalPowerSeries(f, x, x0, dir, triple);
    // ASTSeriesData series = SeriesFunctions.seriesDataRecursive(f, x, x0, order,
    // EvalEngine.get());
    // if (series != null) {
    // return series;
    // }
    // return f;
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

    // # Break instances of Add
    // # this allows application of different
    // # algorithms on different terms increasing the
    // # range of admissible functions.
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

    // The symbolic term - symb, if present, is being separated from the function
    // Otherwise symb is being set to S.One

    // TODO
    return F.NIL;
  }

}
