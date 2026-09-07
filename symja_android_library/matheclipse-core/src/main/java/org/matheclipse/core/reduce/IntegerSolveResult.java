package org.matheclipse.core.reduce;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

/**
 * What the integer engine was able to prove about a condition.
 *
 * <p>
 * The distinction between {@link Kind#UNSUPPORTED} and {@link Kind#INFEASIBLE} is the point of
 * this type. A solver which cannot decide a problem and an empty solution set are not the same
 * answer, and reporting the first as the second is how a search with a bounded window reports that
 * a system has no solution when in truth it has one outside the window.
 */
public final class IntegerSolveResult {

  public enum Kind {
    /** The condition is not integer arithmetic this engine handles; try another method. */
    NOT_APPLICABLE,
    /** The condition is integer arithmetic, but was not decided; leave it unevaluated. */
    UNSUPPORTED,
    /** The condition was proved to have no solution. */
    INFEASIBLE,
    /** Every solution, enumerated. */
    FINITE,
    /** An infinite solution set, described by a lattice parametrization. */
    PARAMETRIC
  }

  private static final IntegerSolveResult NOT_APPLICABLE =
      new IntegerSolveResult(Kind.NOT_APPLICABLE, null, null, null, null);

  private static final IntegerSolveResult UNSUPPORTED =
      new IntegerSolveResult(Kind.UNSUPPORTED, null, null, null, null);

  private static final IntegerSolveResult INFEASIBLE =
      new IntegerSolveResult(Kind.INFEASIBLE, null, null, null, null);

  private final Kind kind;

  private final List<Variable> variables;

  private final List<BigInteger[]> solutions;

  private final LatticeSolver.Solution family;

  private final List<Variable> untouched;

  private IntegerSolveResult(Kind kind, List<Variable> variables, List<BigInteger[]> solutions,
      LatticeSolver.Solution family, List<Variable> untouched) {
    this.kind = kind;
    this.variables = variables;
    this.solutions = solutions;
    this.family = family;
    this.untouched = untouched;
  }

  public static IntegerSolveResult notApplicable() {
    return NOT_APPLICABLE;
  }

  public static IntegerSolveResult unsupported() {
    return UNSUPPORTED;
  }

  public static IntegerSolveResult infeasible() {
    return INFEASIBLE;
  }

  public static IntegerSolveResult finite(List<Variable> variables, List<BigInteger[]> solutions,
      List<Variable> untouched) {
    return solutions.isEmpty() ? INFEASIBLE
        : new IntegerSolveResult(Kind.FINITE, variables, solutions, null, untouched);
  }

  public static IntegerSolveResult parametric(LatticeSolver.Solution family,
      List<Variable> untouched) {
    return new IntegerSolveResult(Kind.PARAMETRIC, family.variables(), null, family, untouched);
  }

  public Kind kind() {
    return kind;
  }

  public boolean is(Kind expected) {
    return kind == expected;
  }

  /** The variables the tuples of {@link #solutions()} are indexed by. */
  public List<Variable> variables() {
    return variables == null ? Collections.<Variable>emptyList() : variables;
  }

  /** Every solution, in lexicographic order; only for {@link Kind#FINITE}. */
  public List<BigInteger[]> solutions() {
    return solutions == null ? Collections.<BigInteger[]>emptyList() : solutions;
  }

  /** The lattice parametrization; only for {@link Kind#PARAMETRIC}. */
  public LatticeSolver.Solution family() {
    return family;
  }

  /** The requested variables the condition does not constrain at all. */
  public List<Variable> untouched() {
    return untouched == null ? Collections.<Variable>emptyList() : untouched;
  }

  @Override
  public String toString() {
    return kind.toString();
  }
}
