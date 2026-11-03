package org.matheclipse.core.reduce;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IRational;

/**
 * The integer solutions of a conjunction with a polynomial equation which factors into factors
 * linear in one variable each, when the box of the variables has too many points to list them.
 *
 * <p>
 * Such an equation says that one of the factors is <code>0</code>: <code>u*(v-3) == 0</code> is
 * <code>u == 0</code> or <code>v == 3</code>. Every root gives one branch, in which the other
 * relations must be linear. The branch is then a linear system: a branch which determines all
 * variables is a point, a branch with one degree of freedom is the range of a parameter which the
 * relations bound on both sides, or the list of its points if the range is short, as in
 * <code>C(1)&isin;Integers &amp;&amp; -9&lt;=C(1)&lt;=9 &amp;&amp; u==0 &amp;&amp; v==C(1)</code>.
 * The branches of the same equation may overlap, because each describes its whole factor.
 *
 * <p>
 * All or nothing: a branch of any other shape, among them a branch with two degrees of freedom or
 * an unbounded parameter, declines the whole condition, because a part of the branches is a wrong
 * answer.
 */
public final class FactoredEquationBox {

  /** The number of branches which may be examined. */
  private static final int MAX_BRANCHES = 64;

  /** The largest exponent in a sum of an equation which is factored. */
  private static final int MAX_DEGREE = 32;

  /** A range of at most this many values is written as the list of its points. */
  private static final BigInteger MAX_LISTED_VALUES = BigInteger.TEN;

  private final IAST vars;

  private final EvalEngine engine;

  /** The reduced branches, in order and without repetitions. */
  private final Set<IExpr> branches = new LinkedHashSet<IExpr>();

  /** The number of examined branches. */
  private int branchCount = 0;

  private FactoredEquationBox(IAST vars, EvalEngine engine) {
    this.vars = vars;
    this.engine = engine;
  }

  /**
   * Reduce a conjunction over the integers.
   *
   * @param condition a relation or a conjunction of relations
   * @param vars the variables of the reduction
   * @param engine the evaluation engine
   * @return the disjunction of the branches, or {@link F#NIL} if the condition has no factored
   *         equation or a branch isn't decided
   */
  public static IExpr reduce(IExpr condition, IAST vars, EvalEngine engine) {
    List<IExpr> conjuncts = new ArrayList<IExpr>();
    for (IExpr conjunct : condition.isAnd() ? (IAST) condition : F.And(condition)) {
      conjuncts.add(conjunct);
    }
    FactoredEquationBox box = new FactoredEquationBox(vars, engine);
    try {
      if (!box.split(conjuncts, Collections.<IExpr>emptyList())) {
        return F.NIL;
      }
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return F.NIL;
    }
    if (box.branches.isEmpty()) {
      return S.False;
    }
    IASTAppendable disjuncts = F.ast(S.Or, box.branches.size());
    box.branches.forEach(disjuncts::append);
    return engine.evaluate(disjuncts.oneIdentity0());
  }

  /**
   * Split the first equation which isn't linear, and go on with the other relations in every
   * branch.
   *
   * @param conjuncts the relations
   * @param fixed the equations of the roots which were substituted into the relations
   * @return <code>false</code> if a branch isn't decided
   */
  private boolean split(List<IExpr> conjuncts, List<IExpr> fixed) {
    engine.checkTimeLimit();
    for (int i = 0; i < conjuncts.size(); i++) {
      if (Lowering.lower(conjuncts.get(i)) != null) {
        continue;
      }
      List<IAST> roots = roots(conjuncts.get(i));
      if (roots == null) {
        return false;
      }
      List<IExpr> others = new ArrayList<IExpr>(conjuncts);
      others.remove(i);
      for (IAST root : roots) {
        if (++branchCount > MAX_BRANCHES) {
          return false;
        }
        List<IExpr> substituted = substitute(others, root);
        if (substituted != null) {
          List<IExpr> extended = new ArrayList<IExpr>(fixed);
          extended.add(root);
          if (!split(substituted, extended)) {
            return false;
          }
        }
      }
      return true;
    }
    if (fixed.isEmpty()) {
      return false;
    }
    List<IExpr> relations = new ArrayList<IExpr>(fixed);
    relations.addAll(conjuncts);
    return addBranch(F.And(relations.toArray(new IExpr[0])));
  }

  /**
   * The integer roots <code>x == r</code> of the factors of an equation, in the canonical order of
   * the factors.
   *
   * @return <code>null</code> if a factor isn't linear in one of the variables
   */
  private List<IAST> roots(IExpr equation) {
    if (!equation.isEqual()) {
      return null;
    }
    IExpr polynomial = engine.evaluate(F.Subtract(equation.first(), equation.second()));
    if (!polynomial.isPolynomial(vars) || !isSmallDegree(polynomial)) {
      return null;
    }
    IExpr factorList = engine.evalQuiet(F.FactorList(polynomial));
    if (!factorList.isList() || factorList.argSize() < 2) {
      return null;
    }
    List<IExpr> factors = new ArrayList<IExpr>();
    for (int i = 2; i < factorList.size(); i++) {
      IExpr factor = factorList.getAt(i);
      if (!factor.isList2() || !factor.second().isPositive() || !factor.second().isInteger()) {
        return null;
      }
      factors.add(factor.first());
    }
    Collections.sort(factors);
    List<IAST> roots = new ArrayList<IAST>();
    for (IExpr factor : factors) {
      Formula root = Lowering.lower(F.Equal(factor, F.C0));
      if (root == null || root.kind() != Formula.Kind.ATOM) {
        return null;
      }
      AffineTerm term = root.atom().term();
      if (term.variables().size() != 1) {
        return null;
      }
      Variable variable = term.variables().iterator().next();
      if (!vars.contains(variable.symbol())) {
        return null;
      }
      IRational value = term.constant().negate().divideBy(term.coefficient(variable));
      if (value.denominator().isOne()) {
        roots.add(F.Equal(variable.symbol(), value.numerator()));
      }
    }
    return roots;
  }

  /** Whether no sum in the polynomial has an exponent too large to factor it in a short time. */
  private static boolean isSmallDegree(IExpr polynomial) {
    return !polynomial.has(sum -> sum.isPlus()
        && sum.has(x -> x.isPower() && x.exponent().toIntDefault() > MAX_DEGREE, true), true);
  }

  /**
   * The relations at the value of a root, without those which became true.
   *
   * @return <code>null</code> if a relation became false
   */
  private List<IExpr> substitute(List<IExpr> relations, IAST root) {
    List<IExpr> substituted = new ArrayList<IExpr>(relations.size());
    for (IExpr relation : relations) {
      IExpr value = engine.evalQuiet(F.subst(relation, root.first(), root.second()));
      if (value.isFalse()) {
        return null;
      }
      if (!value.isTrue()) {
        substituted.add(value);
      }
    }
    return substituted;
  }

  /**
   * Add the solutions of a linear conjunction which has equations.
   *
   * @return <code>false</code> if the conjunction has no form of a point or a bounded range
   */
  private boolean addBranch(IExpr condition) {
    Lowering.LinearRequest request = Lowering.request(condition, vars, IntegerDomain.INTEGERS);
    if (request == null) {
      return false;
    }
    Formula normalized = IntegerNormalizer.normalize(request.formula(), true);
    if (normalized == null) {
      return false;
    }
    if (normalized.isFalse()) {
      return true;
    }
    List<Atom> atoms = IntegerReduceEngine.conjunctionAtoms(normalized);
    if (atoms == null || !request.targets().containsAll(normalized.freeVariables())) {
      return false;
    }
    List<AffineTerm> equations = new ArrayList<AffineTerm>();
    List<Formula> residualAtoms = new ArrayList<Formula>();
    IntegerReduceEngine.splitEquations(atoms, equations, residualAtoms);
    LatticeSolver.Solution solution = LatticeSolver.solve(equations, request.targets());
    if (solution == null) {
      return true;
    }
    if (solution.parameterCount() > 1) {
      return false;
    }
    Variable parameter = Variable.free(F.Dummy("C$1"));
    List<Variable> parameters = Collections.singletonList(parameter);
    Formula residual = Formula.and(residualAtoms);
    for (int index = 0; index < request.targets().size(); index++) {
      residual = residual.substitute(request.targets().get(index),
          solution.parameterCount() == 0 ? AffineTerm.integer(solution.offset()[index])
              : solution.substitution(index, parameters));
    }
    residual = IntegerNormalizer.normalize(residual, true);
    if (residual == null) {
      return false;
    }
    if (residual.isFalse()) {
      return true;
    }
    if (solution.parameterCount() == 0) {
      return residual.isTrue() && addSolution(solution, F.NIL);
    }
    BigInteger[] range = range(residual, parameter);
    if (range == null) {
      return false;
    }
    if (range[0].compareTo(range[1]) > 0) {
      return true;
    }
    if (range[1].subtract(range[0]).compareTo(MAX_LISTED_VALUES) < 0) {
      for (BigInteger value = range[0]; value.compareTo(range[1]) <= 0; //
          value = value.add(BigInteger.ONE)) {
        addSolution(solution.fixParameter(value), F.NIL);
      }
      return true;
    }
    return addSolution(solution,
        F.ternaryAST3(S.LessEqual, F.ZZ(range[0]), F.C(1), F.ZZ(range[1])));
  }

  private boolean addSolution(LatticeSolver.Solution solution, IExpr condition) {
    branches.add(engine.evaluate(Emitter.latticeReduceForm(solution,
        Collections.<Variable>emptyList(), IntegerDomain.INTEGERS, condition)));
    return true;
  }

  /**
   * The smallest and the largest integer which the conjunction of relations in one variable
   * leaves.
   *
   * @return <code>null</code> if the formula has another form or the values are unbounded
   */
  private static BigInteger[] range(Formula formula, Variable variable) {
    List<Atom> atoms = IntegerReduceEngine.conjunctionAtoms(formula);
    if (atoms == null) {
      return null;
    }
    BigInteger low = null;
    BigInteger high = null;
    for (Atom atom : atoms) {
      IRational coefficient = atom.term().coefficient(variable);
      if (!atom.isRelation() || atom.relation() == Relation.NOT_EQUAL || coefficient.isZero()) {
        return null;
      }
      IRational bound = atom.term().constant().negate().divideBy(coefficient);
      BigInteger floor = bound.floor().toBigNumerator();
      BigInteger ceiling = bound.ceil().toBigNumerator();
      Relation relation =
          coefficient.isNegative() ? atom.relation().reversed() : atom.relation();
      if (relation != Relation.LESS && relation != Relation.LESS_EQUAL) {
        BigInteger lower = relation == Relation.GREATER ? floor.add(BigInteger.ONE) : ceiling;
        low = low == null ? lower : low.max(lower);
      }
      if (relation != Relation.GREATER && relation != Relation.GREATER_EQUAL) {
        BigInteger upper = relation == Relation.LESS ? ceiling.subtract(BigInteger.ONE) : floor;
        high = high == null ? upper : high.min(upper);
      }
    }
    return low == null || high == null ? null : new BigInteger[] {low, high};
  }
}
