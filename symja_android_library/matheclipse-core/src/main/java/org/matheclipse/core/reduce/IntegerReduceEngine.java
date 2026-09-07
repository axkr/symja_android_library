package org.matheclipse.core.reduce;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * The entry point the built-in functions use to decide a condition over a discrete domain.
 *
 * <p>
 * The engine is pure: it lowers an expression into the linear IR, decides it there, and emits the
 * answer. It never evaluates <code>Reduce</code> or <code>Solve</code>, which is what keeps the
 * integer paths of those two functions from calling each other.
 *
 * <p>
 * Every entry point returns {@link F#NIL}, or a {@link IntegerSolveResult.Kind#NOT_APPLICABLE}
 * result, when it cannot decide the input. That is a deliberate decline, not an answer: a caller
 * must leave the expression unevaluated rather than report an empty or truncated solution set.
 */
public final class IntegerReduceEngine {

  private IntegerReduceEngine() {}

  /**
   * Eliminate the quantifiers of a formula over the integers.
   *
   * @param expr the quantified formula
   * @param engine the evaluation engine, used only to evaluate the emitted expression
   * @return the quantifier free condition, or {@link F#NIL} if the formula is outside linear
   *         integer arithmetic
   */
  public static IExpr resolve(IExpr expr, EvalEngine engine) {
    Formula formula = Lowering.lower(expr);
    if (formula == null) {
      return F.NIL;
    }
    Formula result = Presburger.eliminateQuantifiers(formula);
    if (result == null) {
      return F.NIL;
    }
    return engine.evaluate(Emitter.formula(result));
  }

  /**
   * Reduce a condition over a discrete domain.
   *
   * @param condition the condition to reduce
   * @param variables the variables of the reduction
   * @param domainSymbol {@code Integers}, {@code Primes} or {@code Rationals}
   * @param engine the evaluation engine
   * @return the reduced condition, or {@link F#NIL} if no exact method applies
   */
  public static IExpr reduce(IExpr condition, IAST variables, ISymbol domainSymbol,
      EvalEngine engine) {
    IntegerDomain domain = IntegerDomain.of(domainSymbol);
    if (domain != IntegerDomain.INTEGERS) {
      // the prime and rational domains are not decided by the linear engine yet
      return F.NIL;
    }
    Lowering.LinearRequest request = Lowering.request(condition, variables, domain);
    if (request == null) {
      return F.NIL;
    }
    if (request.formula().containsQuantifier()) {
      Formula result = Presburger.eliminateQuantifiers(request.formula());
      if (result == null) {
        return F.NIL;
      }
      IExpr expression = Emitter.formula(result, request.targets());
      return engine.evaluate(Emitter.withDomainConditions(expression, request.targets(), domain));
    }
    IntegerSolveResult system = solveLinearSystem(request);
    switch (system.kind()) {
      case INFEASIBLE:
        return S.False;
      case PARAMETRIC:
        return engine.evaluate(
            Emitter.latticeReduceForm(system.family(), system.untouched(), domain));
      default:
        return F.NIL;
    }
  }

  /**
   * Solve a condition over the integers.
   *
   * @param condition the equations and inequalities
   * @param variables the variables to solve for
   * @param domainSymbol {@code Integers} or {@code Primes}
   * @return what could be proved about the solution set
   */
  public static IntegerSolveResult solve(IExpr condition, IAST variables, ISymbol domainSymbol) {
    IntegerDomain domain = IntegerDomain.of(domainSymbol);
    if (domain != IntegerDomain.INTEGERS) {
      return IntegerSolveResult.notApplicable();
    }
    Lowering.LinearRequest request = Lowering.request(condition, variables, domain);
    if (request == null || request.formula().containsQuantifier()) {
      return IntegerSolveResult.notApplicable();
    }
    return solveLinearSystem(request);
  }

  /**
   * Solve a system of linear equations over the integers.
   *
   * <p>
   * The solution set of such a system is a lattice coset, not a single point, and it is unbounded
   * whenever there are more unknowns than independent equations. Reporting a prefix of it, or
   * expressing one unknown as a fraction of the others, are both wrong: the answer is the
   * parametrization.
   */
  static IntegerSolveResult solveLinearSystem(Lowering.LinearRequest request) {
    Formula normalized = IntegerNormalizer.normalize(request.formula(), true);
    if (normalized == null) {
      return IntegerSolveResult.notApplicable();
    }
    if (normalized.isFalse()) {
      return IntegerSolveResult.infeasible();
    }
    List<Atom> atoms = conjunctionAtoms(normalized);
    if (atoms == null || atoms.isEmpty()) {
      return IntegerSolveResult.notApplicable();
    }
    List<AffineTerm> equations = new ArrayList<AffineTerm>(atoms.size());
    for (Atom atom : atoms) {
      if (!atom.isRelation() || atom.relation() != Relation.EQUAL) {
        return IntegerSolveResult.notApplicable();
      }
      equations.add(atom.term());
    }
    Set<Variable> free = normalized.freeVariables();
    if (!request.targets().containsAll(free)) {
      // a parameter in the system makes the solution set depend on its value
      return IntegerSolveResult.notApplicable();
    }
    List<Variable> present = new ArrayList<Variable>(request.targets().size());
    List<Variable> untouched = new ArrayList<Variable>();
    for (Variable target : request.targets()) {
      if (free.contains(target)) {
        present.add(target);
      } else {
        untouched.add(target);
      }
    }
    if (present.isEmpty()) {
      return IntegerSolveResult.notApplicable();
    }
    LatticeSolver.Solution solution = LatticeSolver.solve(equations, present);
    return solution == null ? IntegerSolveResult.infeasible()
        : IntegerSolveResult.parametric(solution, untouched);
  }

  /** The atoms of a conjunction, or <code>null</code> if the formula is not one. */
  private static List<Atom> conjunctionAtoms(Formula formula) {
    List<Atom> atoms = new ArrayList<Atom>();
    if (formula.kind() == Formula.Kind.ATOM) {
      atoms.add(formula.atom());
      return atoms;
    }
    if (formula.kind() != Formula.Kind.AND) {
      return null;
    }
    for (Formula child : formula.children()) {
      if (child.kind() != Formula.Kind.ATOM) {
        return null;
      }
      atoms.add(child.atom());
    }
    return atoms;
  }
}
