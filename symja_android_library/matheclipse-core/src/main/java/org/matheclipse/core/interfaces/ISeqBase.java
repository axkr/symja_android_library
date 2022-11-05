package org.matheclipse.core.interfaces;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.sympy.series.Sequences;

public interface ISeqBase extends Iterable<IExpr> {
  public IExpr args0();

  public IAST args1();

  public IASTMutable asAST();

  default IExpr gen() {
    throw new UnsupportedOperationException("SeqBase#gen()");
  }

  /**
   * The interval on which the sequence is defined
   * 
   * @return
   */
  default IExpr interval() {
    throw new UnsupportedOperationException("SeqBase#interval()");
  }

  /**
   * The starting point of the sequence. This point is included.
   * 
   * @return
   */
  default IExpr start() {
    return args1().arg2();
  }

  /**
   * The ending point of the sequence. This point is included.
   * 
   * @return
   */
  default IExpr stop() {
    return args1().arg3();
  }

  /**
   * Returns a list of variables that are bounded
   * 
   * @return
   */
  default IAST variables() {
    return F.List(args1().arg1());
  }

  default IExpr _eval_coeff(IExpr pt) {
    throw new UnsupportedOperationException("The _eval_coeff method should be added"//
        + " to return coefficient so it is available" //
        + "when coeff calls it.");
  }

  /**
   * Returns the term-wise multiplication of <code>this</code> and <code>other</code>'.
   * 
   * @param other
   * @return
   */
  default ISeqBase mul(ISeqBase other) {
    return Sequences.SeqMul((Sequences.SeqBase) this, (Sequences.SeqBase) other);
  }

  default IExpr _ith_point(int i) {
    // Returns the i'th point of a sequence.
    //
    // Explanation
    // ===========
    //
    // If start point is negative infinity, point is returned from the end.
    // Assumes the first point to be indexed zero.
    //
    // Examples
    // =========
    //
    // >>> from sympy import oo
    // >>> from sympy.series.sequences import SeqPer
    //
    // bounded
    //
    // >>> SeqPer((1, 2, 3), (-10, 10))._ith_point(0)
    // -10
    // >>> SeqPer((1, 2, 3), (-10, 10))._ith_point(5)
    // -5
    //
    // End is at infinity
    //
    // >>> SeqPer((1, 2, 3), (0, oo))._ith_point(5)
    // 5
    //
    // Starts at negative infinity
    //
    // >>> SeqPer((1, 2, 3), (-oo, 0))._ith_point(5)
    // -5
    IExpr initial;
    int step;
    if (start().isNegativeInfinity()) {
      initial = stop();
    } else {
      initial = start();
    }
    if (start().isNegativeInfinity()) {
      step = -1;
    } else {
      step = 1;
    }
    return initial.plus(F.ZZ(i * step));
  }

  /**
   * Finds the shortest linear recurrence that satisfies the first n terms of sequence of order &le;
   * <code>n/2</code> if possible.
   * 
   * @param n
   * @return
   */
  default IAST find_linear_recurrence(int n) {
    return find_linear_recurrence(n, F.NIL, F.NIL);
  }

  /**
   * Finds the shortest linear recurrence that satisfies the first n terms of sequence of order &le;
   * <code>n/2</code> if possible. If <code>d</code> is specified, find shortest linear recurrence
   * of order &le; min(d, n/2) if possible. Returns list of coefficients
   * <code>{b(1), b(2), ...}</code> corresponding to the recurrence relation
   * <code>x(n) = b(1)*x(n-1) + b(2)*x(n-2) + ...</code> Returns <code>{}2</code> if no recurrence
   * is found. If <code>gfvar</code> is specified, also returns ordinary generating function as a
   * function of <code>gfvar</code>.
   * 
   * @param n
   * @param d
   * @param gfvar
   * @return
   */
  public IAST find_linear_recurrence(int n, IExpr d, IExpr gfvar);
}
