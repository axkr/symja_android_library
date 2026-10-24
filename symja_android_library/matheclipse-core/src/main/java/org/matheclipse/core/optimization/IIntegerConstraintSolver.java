package org.matheclipse.core.optimization;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Finds the integer solutions of a system of equations and inequations with a constraint solver.
 *
 * <p>
 * The implementation lives in the optional <code>matheclipse-or-tools</code> module (a CP-SAT
 * model), which <code>matheclipse-core</code> must not depend on, so the dependency is inverted
 * like {@link ILinearOptimizer}. <code>Solve</code> and <code>FindInstance</code> over the
 * <code>Integers</code> use it for the option <code>Method -&gt; "ORTools"</code> in place of the
 * choco-solver search of {@link org.matheclipse.core.convert.ChocoConvert}.
 */
public interface IIntegerConstraintSolver {

  /** The installed solver, or <code>null</code>. */
  IIntegerConstraintSolver[] INSTANCE = new IIntegerConstraintSolver[1];

  /** Install the solver. Called from <code>org.matheclipse.ortools.OrToolsInit</code>. */
  static void install(IIntegerConstraintSolver solver) {
    INSTANCE[0] = solver;
  }

  /** @return the installed solver, or <code>null</code> */
  static IIntegerConstraintSolver get() {
    return INSTANCE[0];
  }

  /** @return <code>true</code> if a solver is installed */
  static boolean isAvailable() {
    return INSTANCE[0] != null;
  }

  /**
   * Solve the system over the integers (or the primes).
   *
   * <p>
   * The contract is that of {@link org.matheclipse.core.convert.ChocoConvert#integerSolve}: the
   * variables which aren't bounded by the system are searched in the box
   * <code>[lowerBound, upperBound]</code>, so an empty result is only a proof of infeasibility if
   * every variable is bounded by the system itself.
   *
   * @param equationsAndInequations the list of relations <code>Equal, Unequal, Less, LessEqual,
   *        Greater, GreaterEqual</code> and the logical combinations <code>And, Or, Not, Xor,
   *        Implies</code> of them, with polynomial sides
   * @param variables the variables to solve for, in the order of the rules of a solution
   * @param userDefinedVariables the variables the user asked for
   * @param maximumNumberOfResults the maximum number of solutions; a negative number for all
   * @param domain <code>S.Integers</code> or <code>S.Primes</code>
   * @param lowerBound the default lower bound of a variable
   * @param upperBound the default upper bound of a variable
   * @param engine the evaluation engine
   * @return the list of solutions, each a list of rules; <code>F.CEmptyList</code> if there is
   *         none; <code>F.NIL</code> if the system can't be translated or the search was cut off
   */
  IAST solve(IAST equationsAndInequations, IAST variables, IAST userDefinedVariables,
      int maximumNumberOfResults, ISymbol domain, long lowerBound, long upperBound,
      EvalEngine engine);
}
