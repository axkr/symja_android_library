package org.matheclipse.core.interfaces;

import java.util.Set;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.expression.F;
import com.google.common.collect.Sets;

public interface ISeriesBase extends Iterable<IExpr> {
  public IExpr args0();

  public IExpr args1();

  default IExpr function() {
    return args0();
  }

  /**
   * The interval on which the sequence is defined
   * 
   * @return
   */
  default IAST interval() {
    throw new UnsupportedOperationException("ISeriesBase#interval()");
  }

  /**
   * The starting point of the sequence. This point is included.
   * 
   * @return
   */
  default IExpr start() {
    return interval().arg1().first();
  }

  /**
   * The ending point of the sequence. This point is included.
   * 
   * @return
   */
  default IExpr stop() {
    return interval().arg1().second();
  }

  default IExpr length() {
    throw new UnsupportedOperationException("ISeriesBase#length()");
  }

  /**
   * Returns a list of variables that are bounded
   * 
   * @return
   */
  default IAST variables() {
    return F.CEmptyList;
  }

  /**
   * This method returns the symbols in the object, excluding those that take on a specific value
   * (i.e. the dummy symbols).
   * 
   * @return
   */
  default Set<IExpr> freeSymbols() {
    VariablesSet varSet = new VariablesSet(function());
    Set<IExpr> resid = Sets.difference(varSet.getVariablesSet(), variables().asSortedSet());
    return resid;
  }

  /**
   * Term at point <code>pt</code> of a series.
   * 
   * @param pt
   * @return
   */
  default IExpr term(IExpr pt) {
    // if pt < self.start or pt > self.stop:
    // throw new UnsupportedOperationException("Index %s out of bounds %s" % (pt, self.interval));
    return _eval_term(pt);
  }

  default IExpr _eval_term(IExpr pt) {
    throw new UnsupportedOperationException("The _evalterm method should be added"//
        + " to return series term so it is available" //
        + "when term calls it.");
  }

  /**
   * Returns the i'th point of a series If start point is negative infinity, point is returned from
   * the end. Assumes the first point to be indexed zero.
   * 
   * @param i
   * @return
   */
  default IExpr _ith_point(int i) {
    IExpr initial;
    int step;
    if (start().isNegativeInfinity()) {
      initial = stop();
      step = -1;
    } else {
      initial = start();
      step = 1;
    }
    return initial.plus(F.ZZ(i * step));
  }

}
