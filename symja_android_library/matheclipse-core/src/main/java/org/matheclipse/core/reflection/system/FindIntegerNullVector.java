package org.matheclipse.core.reflection.system;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <code>FindIntegerNullVector({x1, x2, ..., xn})</code> - an integer vector <code>{c1, ..., cn}</code>
 * with <code>c1*x1 + ... + cn*xn == 0</code>.
 *
 * <p>
 * The real numbers, scaled by <code>10^d</code> and rounded, are appended to the rows of the unit
 * matrix; a short vector of the reduced lattice starts with the integer relation. For exact numbers
 * the relation is verified exactly; for machine numbers it has to hold to the precision of the
 * numbers.
 */
public class FindIntegerNullVector extends AbstractFunctionEvaluator {

  /** The digits of the scale factor for exact numbers. */
  private static final int EXACT_DIGITS = 40;

  /** The digits of the scale factor for machine numbers. */
  private static final int MACHINE_DIGITS = 12;

  /** The largest number of elements. */
  private static final int MAX_LENGTH = 30;

  public FindIntegerNullVector() {}

  @Override
  public IExpr evaluate(final IAST ast, final EvalEngine engine) {
    IExpr arg1 = ast.arg1();
    if (!arg1.isList() || arg1.argSize() < 2 || arg1.argSize() > MAX_LENGTH) {
      return F.NIL;
    }
    IAST list = (IAST) arg1;
    if (!list.forAll(x -> x.isRealResult() || x.isReal())) {
      return F.NIL;
    }
    boolean exact = list.forAll(x -> !x.isInexactNumber() && x.isFree(y -> y.isInexactNumber(), true));
    if (!exact && !list.forAll(x -> x.isReal())) {
      return F.NIL;
    }
    int digits = exact ? EXACT_DIGITS : MACHINE_DIGITS;
    IExpr values = exact ? engine.evaluate(F.N(list, F.ZZ(digits + 20))) : list;
    if (!values.isList()) {
      return F.NIL;
    }
    final int n = list.argSize();
    IASTAppendable matrix = F.ListAlloc(n);
    IExpr scale = F.Power(F.C10, F.ZZ(digits));
    for (int i = 1; i <= n; i++) {
      IASTAppendable row = F.ListAlloc(n + 1);
      for (int j = 1; j <= n; j++) {
        row.append(i == j ? F.C1 : F.C0);
      }
      IExpr scaled = engine.evaluate(F.Round(F.Times(scale, ((IAST) values).get(i))));
      if (!scaled.isInteger()) {
        return F.NIL;
      }
      row.append(scaled);
      matrix.append(row);
    }
    IExpr reduced = engine.evaluate(F.unaryAST1(S.LatticeReduce, matrix));
    if (!reduced.isList()) {
      return F.NIL;
    }
    for (IExpr row : (IAST) reduced) {
      if (!row.isList() || row.argSize() != n + 1) {
        return F.NIL;
      }
      IAST relation = ((IAST) row).removeAtClone(n + 1);
      if (relation.forAll(x -> x.isZero())) {
        continue;
      }
      IExpr sum = F.Dot(relation, list);
      boolean holds = exact ? engine.evalTrue(F.PossibleZeroQ(sum))
          : engine.evalTrue(F.Less(F.Abs(sum),
              F.Times(F.num(1.0e-8), F.Max(F.Map(S.Abs, list)), F.ZZ(n))));
      if (holds) {
        // the first nonzero element is positive
        IExpr firstNonZero = relation.get(relation.indexOf(x -> !x.isZero()));
        return firstNonZero.isNegative() ? engine.evaluate(F.Negate(relation)) : relation;
      }
      engine.checkTimeLimit();
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_2;
  }
}
