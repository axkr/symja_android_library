package org.matheclipse.core.sympy.series;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.hipparchus.linear.FieldDecompositionSolver;
import org.hipparchus.linear.FieldLUDecomposition;
import org.hipparchus.linear.FieldMatrix;
import org.matheclipse.core.builtin.LinearAlgebra;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractMatrix1Expr;
import org.matheclipse.core.expression.BuiltInDummy;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISeqBase;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.sympy.exception.ValueError;

public class Sequences {
  interface ISeqIterator extends Iterator<IExpr> {
    public void setLength(int n);
  }

  /**
   * Represents term-wise multiplication of sequences.
   *
   */
  public static class SeqMul extends SeqBase {
    ISeqBase[] args;

    public SeqMul(ISeqBase... args) {
      // TODO clean up base class
      super(F.NIL);
      this.args = args;
    }

    protected static SeqBase __new__(boolean evaluate, SeqBase... args) {
      if (args.length == 0) {
        return new EmptySequence();
      }
      for (int i = 0; i < args.length - 1; i++) {
        SeqBase iSeq = args[i];
        for (int j = i + 1; j < args.length; j++) {
          SeqBase jSeq = args[j];
          IExpr interval =
              S.IntervalIntersection.ofNIL(EvalEngine.get(), iSeq.interval(), jSeq.interval());
          if (!interval.isInterval() || interval.isEmptyList()) {
            return new EmptySequence();
          }
        }
      }
      if (evaluate) {
        return SeqMul.reduce(args);
      }
      return new SeqMul(args);
    }

    private static SeqBase reduce(SeqBase... args) {
      // Simplify a :class:`SeqMul` using known rules.
      //
      // Explanation
      // ===========
      //
      // Iterates through all pairs and ask the constituent
      // sequences if they can simplify themselves with any other constituent.
      boolean new_args = true;
      while (new_args) {
        for (int i = 0; i < args.length - 1; i++) {
          new_args = false;
          SeqBase[] newArgs = null;
          SeqBase id1 = args[i];
          SeqBase s = args[i + 1];
          for (int j = 0; j < args.length - 1; j++) {
            SeqBase id2 = args[j];
            if (id1 == id2) {
              continue;
            }
            SeqBase t = args[j + 1];
            SeqBase new_seq = s._mul(t);
            // This returns null if s does not know how to multiply
            // with t. Returns the newly multiplied sequence otherwise
            if (new_seq != null) {
              int na = 0;
              newArgs = new SeqBase[args.length - 2];
              for (int k = 0; k < args.length; k++) {
                SeqBase seq = args[k];
                if (seq != s && seq != t) {
                  newArgs[na++] = seq;
                }
              }
              newArgs[na++] = new_seq;
              break;
            }

          }
          if (newArgs != null && newArgs.length > 0) {
            args = newArgs;
            break;
          }
        }
      }
      if (args.length == 1) {
        return args[0];
      } else {
        return SeqMul(args);
      }
    }
  }

  /**
   * Represents a periodic sequence.
   * 
   * The elements are repeated after a given period.
   * 
   * @param seq
   * @param limits
   * @return
   */
  public static SeqBase SeqPer(IExpr seq, IAST limits) {
    BuiltInDummy head = new BuiltInDummy("$seqper");
    IASTMutable ast = F.binaryAST2(head, seq, limits);

    SeqPer res = new SeqPer(ast);
    head.setEvaluator(res);
    return res;
  }

  /**
   * Represents term-wise multiplication of sequences.
   * 
   * @param args
   * @return
   */
  public static SeqBase SeqMul(SeqBase... args) {
    return SeqMul(false, args);
  }

  /**
   * Represents term-wise multiplication of sequences.
   * 
   * @param evaluate
   * @param args
   * @return
   */
  public static SeqBase SeqMul(boolean evaluate, SeqBase... args) {
    return SeqMul.__new__(evaluate, args);
  }

  public static ISeqBase SeqFormula(IExpr seq) {
    return SeqFormula(seq, F.NIL);
  }

  /**
   * Represents sequence based on a formula.
   * 
   * Elements are generated using a formula.
   * 
   * @param seq
   * @param limits
   * @return
   */
  public static SeqBase SeqFormula(IExpr seq, IAST limits) {
    BuiltInDummy head = new BuiltInDummy("$seqformula");
    IASTMutable ast = F.binaryAST2(head, seq, limits);
    SeqFormula res = new SeqFormula(ast);
    head.setEvaluator(res);
    return res;
  }

  public static class SeqBase extends AbstractFunctionEvaluator implements ISeqBase {
    protected IASTMutable seqAST;

    protected int length;
    protected IExpr start;
    protected IExpr stop;
    protected IAST variables;

    public SeqBase(IASTMutable seqAST) {
      this.seqAST = seqAST;
    }

    @Override
    public IASTMutable asAST() {
      return seqAST;
    }

    @Override
    public IExpr interval() {
      return F.Interval(args1().arg2(), args1().arg3());
    }

    public IExpr[] _intersect_interval(ISeqBase other) {
      // """Returns start and stop.
      //
      // Takes intersection over the two intervals.

      IExpr interval = S.IntervalIntersection.ofNIL(EvalEngine.get(), interval(), other.interval());
      if (interval.isInterval()) {
        return new IExpr[] {interval.first(), interval.second()};
      }
      return new IExpr[] {F.CNInfinity, F.CInfinity};
    }

    @Override
    public IExpr gen() {
      return seqAST.arg1();
    }

    @Override
    public IExpr args0() {
      return seqAST.arg1();
    }

    @Override
    public IAST args1() {
      return (IAST) seqAST.arg2();
    }

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    public ISeqBase __mul__(ISeqBase other) {
      if (other instanceof SeqBase) {
        return new SeqMul(this, other);
      }
      throw new IllegalArgumentException("cannot multiply sequence and " + other);
    }

    /**
     * <p>
     * Should only be used internally.
     * <p>
     * this._mul(other) returns a new, term-wise multiplied sequence if this knows how to multiply
     * with other, otherwise it returns <code>null</code>.
     * 
     * @param other
     * @return <code>null</code>
     */
    public SeqBase _mul(SeqBase other) {
      // Should only be used internally.
      //
      // Explanation
      // ===========
      //
      // self._mul(other) returns a new, term-wise multiplied sequence if self
      // knows how to multiply with other, otherwise it returns ``None``.
      //
      // ``other`` should only be a sequence object.
      //
      // Used within :class:`SeqMul` class.
      return null;
    }

    /**
     * Returns the coefficient at point pt.
     */
    public IExpr coeff(IExpr pt) {
      // if pt < self.start or pt > self.stop:
      //
      // raise IndexError("Index %s out of bounds %s" % (pt, self.interval))
      return _eval_coeff(pt);
    }

    @Override
    public IAST find_linear_recurrence(int n, IExpr d, IExpr gfvar) {
      // Finds the shortest linear recurrence that satisfies the first n
      // terms of sequence of order `\leq` ``n/2`` if possible.
      // If ``d`` is specified, find shortest linear recurrence of order
      // `\leq` min(d, n/2) if possible.
      // Returns list of coefficients ``[b(1), b(2), ...]`` corresponding to the
      // recurrence relation ``x(n) = b(1)*x(n-1) + b(2)*x(n-2) + ...``
      // Returns ``[]`` if no recurrence is found.
      // If gfvar is specified, also returns ordinary generating function as a
      // function of gfvar.
      //
      // Examples
      // ========
      //
      // >>> from sympy import sequence, sqrt, oo, lucas
      // >>> from sympy.abc import n, x, y
      // >>> sequence(n**2).find_linear_recurrence(10, 2)
      // []
      // >>> sequence(n**2).find_linear_recurrence(10)
      // [3, -3, 1]
      // >>> sequence(2**n).find_linear_recurrence(10)
      // [2]
      // >>> sequence(23*n**4+91*n**2).find_linear_recurrence(10)
      // [5, -10, 10, -5, 1]
      // >>> sequence(sqrt(5)*(((1 + sqrt(5))/2)**n - (-(1 +
      // sqrt(5))/2)**(-n))/5).find_linear_recurrence(10)
      // [1, 1]
      // >>> sequence(x+y*(-2)**(-n), (n, 0, oo)).find_linear_recurrence(30)
      // [1/2, 1/2]
      // >>> sequence(3*5**n + 12).find_linear_recurrence(20,gfvar=x)
      // ([6, -5], 3*(5 - 21*x)/((x - 1)*(5*x - 1)))
      // >>> sequence(lucas(n)).find_linear_recurrence(15,gfvar=x)
      // ([1, 1], (x - 2)/(x**2 + x - 1))
      // """

      // x = [simplify(expand(t)) for t in self[:n]]
      IASTAppendable x = F.ListAlloc(n);
      ISeqIterator iterator = iterator();
      iterator.setLength(n);
      while (iterator.hasNext()) {
        IExpr t = iterator.next();
        x.append(F.evalSimplify(t));
      }

      int r;
      int lx = x.argSize();
      int temp = lx / 2;
      if (d.isNIL()) {
        r = temp;
      } else {
        r = d.toIntDefault();
        if (r == Integer.MIN_VALUE) {
          throw new ArgumentTypeException("d must be a machine-size integer");
        }
        if (temp < r) {
          r = temp;
        }
      }
      IAST coeffs = F.CEmptyList;
      for (int l = 1; l < r + 1; l++) {
        int l2 = 2 * l;
        IASTAppendable mlist = F.ListAlloc();
        for (int k = 0; k < l; k++) {
          mlist.append(x.copyFrom(k + 1, k + l + 1));
        }
        FieldMatrix<IExpr> m = Convert.list2Matrix(mlist);
        if (m == null) {
          // mlist is no valid matrix
          return F.NIL;
        }
        IExpr mDet = LinearAlgebra.determinant(m);
        if (!mDet.isZero()) {

          FieldDecompositionSolver<IExpr> solver =
              new FieldLUDecomposition<IExpr>(m, AbstractMatrix1Expr.POSSIBLE_ZEROQ_TEST, false)
                  .getSolver();
          if (solver.isNonSingular()) {

            FieldMatrix<IExpr> m2 = Convert.list2Matrix(F.List(x.copyFrom(l + 1, l2 + 1)));
            if (m2 == null) {
              // not a valid matrix
              return F.NIL;
            }
            m2 = m2.transpose();
            FieldMatrix<IExpr> y = solver.solve(m2);
            if (lx == l2) {
              // coeffs = flatten(y[::-1])
              coeffs = EvalAttributes.flatten(Convert.matrix2List(y).reverse(F.NIL));
              break;
            }
            mlist = F.ListAlloc();
            for (int k = l; k < lx - l; k++) {
              mlist.append(x.copyFrom(k + 1, k + l + 1));
            }
            m = Convert.list2Matrix(mlist);
            if (m == null) {
              // not a valid matrix
              return F.NIL;
            }
            FieldMatrix<IExpr> mDoty = m.multiply(y);
            FieldMatrix<IExpr> m3 = Convert.list2Matrix(F.List(x.copyFrom(l2 + 1)));
            if (m3 == null) {
              // not a valid matrix
              return F.NIL;
            }
            m3 = m3.transpose();
            if (mDoty.equals(m3)) {
              coeffs = EvalAttributes.flatten(Convert.matrix2List(y).reverse(F.NIL));
              break;
            }
          }
        }
      }
      if (gfvar.isNIL()) {
        return coeffs;
      } else {
        int l = coeffs.argSize();
        if (l == 0) {
          return F.List(F.CEmptyList, S.None);
        } else {
          IExpr n1 = x.get(l).times(gfvar.pow(l - 1));
          IExpr d1 = F.C1.subtract(coeffs.get(l).times(gfvar.pow(l)));
          for (int i = 0; i < l - 1; i++) {
            n1 = n1.plus(x.get(i + 1).times(gfvar.pow(i)));
            for (int j = 0; j < l - i - 1; j++) {
              n1 = n1.subtract(coeffs.get(i + 1).times(x.get(j + 1)).times(gfvar.pow(i + j + 1)));
            }
            d1 = d1.subtract(coeffs.get(i + 1).times(gfvar.pow(i + 1)));
          }
          return F.List(coeffs, F.evalSimplify(F.Divide(n1, d1)));
        }
      }
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      // newSymbol.setAttributes(ISymbol.LISTABLE);
    }

    protected static final class SeqIterator implements ISeqIterator {

      private int length;

      private int currentIndex;

      private SeqBase self;

      public SeqIterator(SeqBase self, int length) {
        this.length = length;
        this.currentIndex = 0;
        this.self = self;
      }

      @Override
      public void setLength(int n) {
        this.length = n;
      };

      @Override
      public boolean hasNext() {
        return currentIndex < length;
      }

      @Override
      public IExpr next() {
        if (currentIndex == length) {
          throw new NoSuchElementException();
        }
        IExpr pt = self._ith_point(currentIndex++);
        return self.coeff(pt);
      }

    }

    @Override
    public ISeqIterator iterator() {
      return new SeqIterator(this, length);
    }
  }



  public static class EmptySequence extends SeqBase {
    // """Represents an empty sequence.
    //
    // The empty sequence is also available as a singleton as
    // ``S.EmptySequence``.
    //
    // Examples
    // ========
    //
    // >>> from sympy import EmptySequence, SeqPer
    // >>> from sympy.abc import x
    // >>> EmptySequence
    // EmptySequence
    // >>> SeqPer((1, 2), (x, 0, 10)) + EmptySequence
    // SeqPer((1, 2), (x, 0, 10))
    // >>> SeqPer((1, 2)) * EmptySequence
    // EmptySequence
    // >>> EmptySequence.coeff_mul(-1)
    // EmptySequence
    // """

    public EmptySequence() {
      super(F.NIL);
    }

    @Override
    public IAST interval() {
      return F.CEmptyList;
    }

    public int length() {
      return 0;
    }

    public ISeqBase coeff_mul(IExpr coeff) {
      // """See docstring of SeqBase.coeff_mul"""
      return this;
    }

    protected static final class EmptySeqIterator implements ISeqIterator {


      public EmptySeqIterator() {}

      @Override
      public void setLength(int n) {
        //
      };

      @Override
      public boolean hasNext() {
        return false;
      }

      @Override
      public IExpr next() {
        throw new NoSuchElementException();
      }

    }

    @Override
    public ISeqIterator iterator() {
      return new EmptySeqIterator();
    }
  }

  public final static class SeqFormula extends SeqBase {

    public SeqFormula(IASTMutable seqFormula) {
      super(seqFormula);
      IExpr formula = seqFormula.arg1();
      IAST limits = (IAST) seqFormula.arg2();
      IExpr x = F.NIL;
      start = F.NIL;
      stop = F.NIL;

      if (limits.isNIL()) {
        x = _find_x(formula);
        start = F.C0;
        stop = F.CInfinity;
      } else if (limits.isList3()) {
        x = limits.arg1();
        start = limits.arg2();
        stop = limits.arg3();
      } else if (limits.isList2()) {
        x = _find_x(formula);
        start = limits.arg1();
        stop = limits.arg2();
      }
      if (!x.isSymbol() || start.isNIL() || stop.isNIL()) {
        throw new ValueError("Invalid limits given: " + limits.toString());
      }
      if (start.isNegativeInfinity() && stop.isInfinity()) {
        throw new ValueError("Both the start and end value cannot be unbounded");
      }
      limits = F.List(x, start, stop);
      seqAST.set(2, limits);
    }

    private static IExpr _find_x(IExpr formula) {
      VariablesSet free = new VariablesSet(formula);
      if (free.size() == 1) {
        return free.getArrayList().get(0);
      } else if (free.size() == 0) {
        return F.Dummy('k');
      }
      throw new ValueError(" specify dummy variables for " + formula + ". If the formula contains"
          + " more than one free symbol, a dummy variable should be"
          + " supplied explicitly e.g., SeqFormula(m*n**2, (n, 0, 5))");
    }

    public IExpr formula() {
      return gen();
    }

    @Override
    public IExpr _eval_coeff(IExpr pt) {
      IExpr d = variables().first();
      return formula().subs(d, pt);
    }

    @Override
    public SeqBase _mul(SeqBase other) {
      if (other instanceof SeqFormula) {
        SeqFormula otherFormula = (SeqFormula) other;
        IExpr form1 = formula();
        IExpr v1 = variables.first();
        IExpr form2 = otherFormula.formula();
        IExpr v2 = otherFormula.variables.first();
        IExpr formula = form1.times(form2.subs(v2, v1));
        IExpr[] interval = _intersect_interval(other);
        start = interval[0];
        stop = interval[1];
        return SeqFormula(formula, F.List(v1, start, stop));
        // form1, v1 = self.formula, self.variables[0]
        // form2, v2 = other.formula, other.variables[0]
        // formula = form1 * form2.subs(v2, v1)
        // start, stop = self._intersect_interval(other)
        // return SeqFormula(formula, (v1, start, stop));
      }
      return null;
    }

    public ISeqBase expand(IExpr pt) {
      return SeqFormula(F.evalExpand(formula()), args1());
    }
  }

  /**
   * Represents a periodic sequence.
   * 
   * The elements are repeated after a given period.
   *
   */
  private static class SeqPer extends SeqBase {

    public SeqPer(IASTMutable seqPer) {
      super(seqPer);
      IExpr periodical = seqPer.arg1();
      IAST limits = (IAST) seqPer.arg2();
      IExpr x = F.NIL;
      start = F.NIL;
      stop = F.NIL;
      if (limits.isNIL()) {
        x = findX(periodical);
        start = F.C0;
        stop = F.CInfinity;
      } else if (limits.isList3()) {
        x = limits.arg1();
        start = limits.arg2();
        stop = limits.arg3();
      } else if (limits.isList2()) {
        x = findX(periodical);
        start = limits.arg1();
        stop = limits.arg2();
      }

      if (!x.isSymbol() || start.isNIL() || stop.isNIL()) {
        throw new ValueError("Invalid limits given: " + limits.toString());
      }
      if (start.isNegativeInfinity() && stop.isInfinity()) {
        throw new ValueError("Both the start and end value cannot be unbounded");
      }
      limits = F.List(x, start, stop);
      seqAST.set(2, limits);
    }

    private static IExpr findX(IExpr periodical) {
      VariablesSet set = new VariablesSet(periodical);
      if (set.size() == 1) {
        return set.getArrayList().get(0);
      } else {
        return F.Dummy('k');
      }
    }

    int period() {
      return gen().argSize();
    }

    IExpr periodical() {
      return gen();
    }

    @Override
    public IExpr _eval_coeff(IExpr pt) {
      final int idx;
      EvalEngine engine = EvalEngine.get();
      if (start().isNegativeInfinity()) {
        idx = engine.evaluate(stop().subtract(pt).mod(F.ZZ(period()))).toIntDefault();
      } else {
        idx = engine.evaluate(pt.subtract(start()).mod(F.ZZ(period()))).toIntDefault();
      }
      IExpr d = variables().first();
      return ((IAST) periodical()).get(idx + 1).subs(d, pt);
    }

  }

  public static ISeqBase sequence(IExpr seq) {
    return sequence(seq, F.NIL);
  }

  /**
   * Returns appropriate sequence object.
   * 
   * If <code>seq</code> is a list, returns {@link SeqPer} object otherwise returns
   * {@link SeqFormula} object.
   * 
   * @param seq
   * @param limits
   * @return
   */
  public static ISeqBase sequence(IExpr seq, IAST limits) {
    if (seq.isList()) {
      return SeqPer(seq, limits);
    }
    return SeqFormula(seq, limits);
  }
}
