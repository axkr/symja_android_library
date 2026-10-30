package org.matheclipse.core.polynomials;

import java.util.List;
import org.matheclipse.core.eval.EvalEngine;
import edu.jas.gb.GroebnerBaseAbstract;
import edu.jas.gb.GroebnerBaseSeq;
import edu.jas.gb.ReductionSeq;
import edu.jas.poly.GenPolynomial;
import edu.jas.structure.RingElem;

/**
 * The reduction of the Buchberger loop, which tests the time limit of the evaluation before every
 * normal form: the loop itself runs in the library and tests nothing, so
 * <code>TimeConstrained(GroebnerBasis(...), 1)</code> would run to its end.
 */
public final class TimeLimitedReduction<C extends RingElem<C>> extends ReductionSeq<C> {
  private static final long serialVersionUID = 1L;

  private final transient EvalEngine engine;

  private TimeLimitedReduction(EvalEngine engine) {
    this.engine = engine;
  }

  /** A sequential Buchberger algorithm which stops at the time limit of the current evaluation. */
  public static <C extends RingElem<C>> GroebnerBaseAbstract<C> groebnerBase() {
    return new GroebnerBaseSeq<C>(new TimeLimitedReduction<C>(EvalEngine.get()));
  }

  @Override
  public GenPolynomial<C> normalform(List<GenPolynomial<C>> Pp, GenPolynomial<C> Ap) {
    engine.checkTimeLimit();
    return super.normalform(Pp, Ap);
  }
}
