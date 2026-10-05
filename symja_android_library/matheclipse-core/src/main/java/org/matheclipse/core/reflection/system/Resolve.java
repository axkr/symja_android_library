package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.reduce.QuadraticQE;
import org.matheclipse.core.reduce.Formula;
import org.matheclipse.core.reduce.IntegerReduceEngine;
import org.matheclipse.core.reduce.Lowering;
import org.matheclipse.core.reduce.RationalQE;
import org.matheclipse.core.reduce.Variable;
import org.matheclipse.core.eval.util.SolveUtils;

/**
 * Resolve(expr) and Resolve(expr, domain) - eliminate the {@link S#ForAll} and {@link S#Exists}
 * quantifiers from <code>expr</code>.
 *
 * <p>
 * The quantifiers are decided by the following (incomplete) strategies:
 *
 * <ul>
 * <li>a single polynomial (in)equality is decided by the global infimum and supremum of the
 * polynomial over the reals; if the extrema depend on free parameters the resulting condition for
 * the parameters is returned (e.g. <code>Resolve(Exists(x, x^2 == c), Reals)</code> returns
 * <code>c&gt;=0</code>)</li>
 * <li>a polynomial equation over the {@link S#Complexes} is solvable if the polynomial isn't
 * constant</li>
 * <li>an existence claim is otherwise proven by a witness which is verified by substituting it into
 * the original condition</li>
 * <li>a univariate condition is delegated to {@link S#Reduce}; an empty solution set refutes the
 * existence claim</li>
 * </ul>
 *
 * <p>
 * If none of the strategies applies, the expression is returned unevaluated.
 */
public class Resolve extends AbstractFunctionOptionEvaluator {

  /** Sample values used by the witness search. */
  private static final IExpr[] SAMPLE_POINTS =
      new IExpr[] {F.C0, F.C1, F.CN1, F.C1D2, F.CN1D2, F.C2, F.CN2};

  /** Reduced set of sample values used by the witness search for many variables. */
  private static final IExpr[] SMALL_SAMPLE_POINTS = new IExpr[] {F.C0, F.C1, F.CN1};

  /** Maximum number of points tested by the witness search. */
  private static final int MAX_WITNESS_TESTS = 1000;

  /**
   * The global infimum or supremum of a function together with the information whether the extremum
   * is attained at a concrete point.
   */
  private static final class Extremum {
    /** The extremum value; may be {@link F#CInfinity} or {@link F#CNInfinity} */
    final IExpr value;

    /**
     * <code>true</code> if the extremum was verified to be attained at a concrete point of the
     * domain
     */
    final boolean attained;

    Extremum(IExpr value, boolean attained) {
      this.value = value;
      this.attained = attained;
    }
  }

  public Resolve() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options,
      final EvalEngine engine, IAST originalAST) {
    SolveOptions solveOptions = SolveOptions.of(SolveOptions.RESOLVE_KEYS, options);
    if (argSize > 0 && argSize < ast.argSize()) {
      ast = ast.copyUntil(argSize + 1);
    }
    long precision = SolveUtils.workingPrecision(ast, solveOptions.workingPrecision(), engine);
    if (precision == SolveUtils.INVALID_PRECISION) {
      return F.NIL;
    }

    ISymbol domain = null;
    if (ast.isAST2()) {
      IExpr arg2 = ast.arg2();
      if (arg2 == S.Integers) {
        // the integers are decided by Cooper elimination; the strategies below reason over a
        // continuum and would answer `Exists(x, 2*x == 1)` with True
        return IntegerReduceEngine.resolve(ast.arg1(), engine);
      }
      if (arg2 != S.Reals && arg2 != S.Complexes) {
        return F.NIL;
      }
      domain = (ISymbol) arg2;
    }
    IExpr result;
    try {
      result = resolve(ast.arg1(), domain, engine);
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return F.NIL;
    }
    if (result.isNIL() && domain == S.Reals) {
      // a condition on the free variables, by virtual substitution
      result = QuadraticQE.eliminate(ast.arg1(), engine);
    }
    if (result.isNIL()) {
      return F.NIL;
    }
    if (precision != SolveUtils.MACHINE_PRECISION_REQUESTED) {
      // the quantifier elimination itself is exact; the requested precision is applied to its
      // result
      result = engine.evaluate(F.N(result, F.ZZ(precision)));
    }
    return result;
  }

  /**
   * Eliminate all {@link S#ForAll} and {@link S#Exists} quantifiers from the given expression.
   *
   * @param expr the (partially quantified) expression
   * @param domain {@link S#Reals}, {@link S#Complexes} or <code>null</code> to determine the domain
   *        from the condition itself
   * @param engine the evaluation engine
   * @return the quantifier free expression or {@link F#NIL} if a quantifier couldn't be eliminated
   */
  public static IExpr resolve(IExpr expr, ISymbol domain, EvalEngine engine) {
    if (!hasQuantifier(expr)) {
      return expr;
    }
    if (domain == S.Reals || (domain == null && containsInequality(expr))) {
      IExpr linear = resolveLinear(expr, engine);
      if (linear.isPresent()) {
        return linear;
      }
      // Not linear: x^2 > m, or the product m*x of two of the variables. A statement without free
      // variables whose atoms are of degree 2 at most in each variable is decided by virtual
      // substitution.
      IExpr quadratic = QuadraticQE.decide(expr, engine);
      if (quadratic.isPresent()) {
        return quadratic;
      }
    }
    if (expr.isAST(S.Exists) || expr.isAST(S.ForAll)) {
      return resolveQuantifier((IAST) expr, domain, engine);
    }
    if (expr.isNot()) {
      IExpr negated = resolve(expr.first(), domain, engine);
      return negated.isPresent() ? engine.evaluate(F.Not(negated)) : F.NIL;
    }
    if (expr.isAST(S.Implies, 3)) {
      // a => b is !a || b
      return resolve(F.Or(F.Not(expr.first()), expr.second()), domain, engine);
    }
    if (expr.isAST(S.Equivalent, 3)) {
      IExpr a = expr.first();
      IExpr b = expr.second();
      return resolve(F.Or(F.And(a, b), F.And(F.Not(a), F.Not(b))), domain, engine);
    }
    if (expr.isAnd() || expr.isOr()) {
      IAST logic = (IAST) expr;
      IASTMutable result = logic.copy();
      for (int i = 1; i < logic.size(); i++) {
        IExpr arg = resolve(logic.get(i), domain, engine);
        if (arg.isNIL()) {
          return F.NIL;
        }
        result.set(i, arg);
      }
      return engine.evaluate(result);
    }
    return F.NIL;
  }

  /**
   * Eliminate a single {@link S#ForAll} or {@link S#Exists} quantifier. <code>ForAll</code> is
   * reduced to <code>Not(Exists(vars, Not(condition)))</code>.
   *
   * @param quant the quantifier AST (<code>ForAll</code> or <code>Exists</code>)
   * @param domain {@link S#Reals}, {@link S#Complexes} or <code>null</code> to determine the domain
   *        from the condition itself
   * @param engine the evaluation engine
   * @return the quantifier free expression or {@link F#NIL} if the quantifier couldn't be decided
   */
  public static IExpr resolveQuantifier(IAST quant, ISymbol domain, EvalEngine engine) {
    final boolean forAll = quant.isAST(S.ForAll);
    if (!quant.isAST2() && !quant.isAST3()) {
      return F.NIL;
    }
    IAST boundVars = quant.arg1().makeList();
    if (boundVars.argSize() < 1) {
      return F.NIL;
    }
    for (int i = 1; i < boundVars.size(); i++) {
      if (!boundVars.get(i).isSymbol()) {
        return F.NIL;
      }
    }

    IExpr condition;
    if (quant.isAST3()) {
      // ForAll(vars, cond, expr) => cond => expr ; Exists(vars, cond, expr) => cond && expr
      condition = forAll ? F.Implies(quant.arg2(), quant.arg3()) //
          : F.And(quant.arg2(), quant.arg3());
    } else {
      condition = quant.arg2();
    }
    condition = engine.evaluate(condition);

    // eliminate nested quantifiers first
    if (hasQuantifier(condition)) {
      condition = resolve(condition, domain, engine);
      if (condition.isNIL()) {
        return F.NIL;
      }
    }
    if (isFreeOfVariables(condition, boundVars)) {
      // quantifying an unrestricted, non empty domain doesn't change the condition
      return condition;
    }

    ISymbol quantifierDomain = domain;
    if (quantifierDomain == null) {
      // WMA convention: variables in inequalities are real, algebraic variables are complex
      quantifierDomain = containsInequality(condition) ? S.Reals : S.Complexes;
    }

    if (forAll) {
      IExpr negated = engine.evaluate(F.Not(condition));
      IExpr existence = exists(boundVars, negated, quantifierDomain, engine);
      if (existence.isPresent()) {
        return engine.evaluate(F.Not(existence));
      }
    } else {
      IExpr existence = exists(boundVars, condition, quantifierDomain, engine);
      if (existence.isPresent()) {
        return existence;
      }
    }
    return decideBySolutionSet(forAll, boundVars, condition, quantifierDomain, engine);
  }

  /**
   * Decide a quantifier from the solution set which {@link S#Reduce} computes for its condition. A
   * {@link S#ForAll} holds if the solution set covers the whole domain, an {@link S#Exists} holds if
   * the solution set names an attained value.
   *
   * <p>
   * This is the fallback for the quantifiers which the {@link #exists(IAST, IExpr, ISymbol,
   * EvalEngine)} analysis above cannot decide.
   *
   * @param forAll <code>true</code> for {@link S#ForAll}, <code>false</code> for {@link S#Exists}
   * @param boundVars the quantified variables
   * @param condition the quantified condition, with nested quantifiers already eliminated
   * @param domain the domain of the quantified variables
   * @param engine the evaluation engine
   * @return {@link S#True}, {@link S#False} or {@link F#NIL} if the quantifier stays undecided
   */
  private static IExpr decideBySolutionSet(boolean forAll, IAST boundVars, IExpr condition,
      ISymbol domain, EvalEngine engine) {
    IExpr reduced = engine.evaluate(F.Reduce(condition, boundVars, domain));
    if (reduced.isTrue()) {
      return S.True;
    }
    if (reduced.isFalse()) {
      return S.False;
    }
    if (forAll) {
      // holds for every value iff the solution set is the whole domain
      return isFullDomain(reduced, boundVars) ? S.True : F.NIL;
    }
    // Exists: the solution set has to name a value which the variables actually attain, and it
    // must not depend on the free parameters of the condition - a parametric solution set like
    // `x == -b/2 + Sqrt(b^2-4*c)/2` only names a real value for some of the parameter values
    if (!reduced.isFree(S.Reduce) || !isAttainedSolution(reduced)) {
      return F.NIL;
    }
    return isParameterFree(reduced, boundVars) ? S.True : F.NIL;
  }

  /**
   * Test whether a solution set only names the quantified variables (and the generated integer
   * constants of a periodic solution family), so that it describes a witness for every value of the
   * remaining free parameters.
   *
   * @param reduced the solution set which {@code Reduce} computed
   * @param boundVars the quantified variables
   */
  private static boolean isParameterFree(IExpr reduced, IAST boundVars) {
    IAST variables = new VariablesSet(reduced).getVarList();
    for (int i = 1; i < variables.size(); i++) {
      IExpr variable = variables.get(i);
      if (!boundVars.contains(variable) && !variable.isAST(S.C, 2)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Test whether a solution set names values which the variables actually attain. {@code Reduce}
   * also returns limit-like solutions, for example <code>E^x == 0</code> over the reals reduces to
   * <code>ConditionalExpression(x == -Infinity + I*2*Pi*C(1), C(1) ∈ Integers)</code>. Such a
   * solution set proves no existence.
   */
  private static boolean isAttainedSolution(IExpr reduced) {
    return reduced.isFree(S.ConditionalExpression) //
        && reduced.isFree(S.DirectedInfinity) //
        && reduced.isFree(S.Indeterminate);
  }

  /**
   * Test whether a solution set covers the whole domain of the given variables.
   */
  private static boolean isFullDomain(IExpr reduced, IAST vars) {
    if (reduced.isTrue()) {
      return true;
    }
    if (reduced.isAST(S.Element, 3)) {
      IExpr domain = reduced.second();
      return (domain == S.Reals || domain == S.Complexes) && vars.contains(reduced.first());
    }
    if (reduced.isAnd()) {
      IAST and = (IAST) reduced;
      for (int i = 1; i < and.size(); i++) {
        if (!isFullDomain(and.get(i), vars)) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  /**
   * Decide <code>Exists(vars, cond)</code> over the given domain.
   *
   * @return {@link S#True}, {@link S#False}, a condition for the remaining free parameters or
   *         {@link F#NIL} if the existence claim couldn't be decided
   */
  private static IExpr exists(IAST vars, IExpr condition, ISymbol domain, EvalEngine engine) {
    IExpr cond = engine.evaluate(condition);
    if (cond.isTrue() || cond.isFalse()) {
      return cond;
    }
    if (isFreeOfVariables(cond, vars)) {
      return cond;
    }
    if (domain == S.Complexes && !containsInequality(cond)) {
      return existsComplexes(vars, cond, engine);
    }
    return existsReals(vars, cond, engine);
  }

  /**
   * Decide <code>Exists(vars, cond)</code> over the {@link S#Complexes} for a condition which only
   * consists of equations.
   */
  private static IExpr existsComplexes(IAST vars, IExpr cond, EvalEngine engine) {
    if (cond.isOr()) {
      return existsOr((IAST) cond, vars, S.Complexes, engine);
    }
    int headID = cond.headID();
    if (headID == ID.Equal || headID == ID.Unequal) {
      IAST comparator = (IAST) cond;
      if (comparator.argSize() == 2) {
        IExpr f = engine.evaluate(F.Subtract(comparator.arg1(), comparator.arg2()));
        if (f.isPolynomial(vars) && !isFreeOfVariables(f, vars)) {
          // a non constant polynomial has a complex root and isn't identically zero
          return S.True;
        }
      }
    }
    return F.NIL;
  }

  /**
   * Decide <code>Exists(vars, cond)</code> over the {@link S#Reals} by applying the available
   * strategies one after the other.
   */
  private static IExpr existsReals(IAST vars, IExpr cond, EvalEngine engine) {
    // a single polynomial (in)equality is decided by the extrema of the polynomial; this is the
    // only strategy which can return a condition for the remaining free parameters
    IExpr atom = existsPolynomialAtom(vars, cond, engine);
    if (atom.isPresent()) {
      return atom;
    }
    if (cond.isOr()) {
      IExpr alternatives = existsOr((IAST) cond, vars, S.Reals, engine);
      if (alternatives.isPresent()) {
        return alternatives;
      }
    }
    if (existsSampledWitness(vars, cond, engine)) {
      return S.True;
    }
    if (vars.isList1()) {
      IExpr projected = existsByEquation(vars.arg1(), cond, engine);
      if (projected.isPresent()) {
        return projected;
      }
    }
    if (vars.isList1()) {
      IExpr reduced = existsByReduce(vars, cond, engine);
      if (reduced.isPresent()) {
        return reduced;
      }
    } else {
      IExpr sliced = existsBySlicing(vars, cond, engine);
      if (sliced.isPresent()) {
        return sliced;
      }
    }
    return F.NIL;
  }

  /** <code>Exists(vars, A || B) == Exists(vars, A) || Exists(vars, B)</code> */
  private static IExpr existsOr(IAST or, IAST vars, ISymbol domain, EvalEngine engine) {
    IASTAppendable result = F.Or();
    boolean complete = true;
    for (int i = 1; i < or.size(); i++) {
      IExpr alternative = exists(vars, or.get(i), domain, engine);
      if (alternative.isTrue()) {
        return S.True;
      }
      if (alternative.isNIL()) {
        complete = false;
      } else if (!alternative.isFalse()) {
        result.append(alternative);
      }
    }
    return complete ? engine.evaluate(result) : F.NIL;
  }

  /**
   * Decide <code>Exists(vars, f REL 0)</code> for a polynomial <code>f</code> and a relation
   * <code>REL</code> with the global infimum and supremum of <code>f</code> over the reals:
   *
   * <ul>
   * <li><code>f &lt; 0</code> is solvable iff <code>inf f &lt; 0</code></li>
   * <li><code>f &lt;= 0</code> is solvable iff <code>inf f &lt; 0</code> or the infimum is
   * <code>0</code> and attained</li>
   * <li><code>f == 0</code> is solvable iff <code>inf f &lt;= 0 &lt;= sup f</code>; the polynomial
   * is continuous on the connected domain, so it attains every value in between</li>
   * <li><code>f != 0</code> is solvable because a non constant polynomial isn't identically
   * zero</li>
   * </ul>
   */
  private static IExpr existsPolynomialAtom(IAST vars, IExpr cond, EvalEngine engine) {
    final int headID = cond.headID();
    switch (headID) {
      case ID.Less:
      case ID.LessEqual:
      case ID.Greater:
      case ID.GreaterEqual:
      case ID.Equal:
      case ID.Unequal:
        break;
      default:
        return F.NIL;
    }
    IAST comparator = (IAST) cond;
    if (comparator.argSize() != 2) {
      return F.NIL;
    }
    IExpr f = engine.evaluate(F.Subtract(comparator.arg1(), comparator.arg2()));
    if (!f.isPolynomial(vars) || isFreeOfVariables(f, vars)) {
      return F.NIL;
    }

    if (headID == ID.Equal && vars.isList1()) {
      // a linear or quadratic equation has an exact criterion for a real root
      IExpr rootCondition = realRootCondition(f, vars.arg1(), engine);
      if (rootCondition.isPresent()) {
        return rootCondition;
      }
    }

    switch (headID) {
      case ID.Unequal:
        // a non constant polynomial isn't identically zero
        return S.True;
      case ID.Less: // f < 0 <=> inf f < 0
        return strictCondition(extremum(f, vars, false, engine), S.Less, vars, engine);
      case ID.Greater: // f > 0 <=> sup f > 0
        return strictCondition(extremum(f, vars, true, engine), S.Greater, vars, engine);
      case ID.LessEqual: // f <= 0
        return boundaryCondition(extremum(f, vars, false, engine), true, vars, engine);
      case ID.GreaterEqual: // f >= 0
        return boundaryCondition(extremum(f, vars, true, engine), false, vars, engine);
      case ID.Equal: // f == 0 <=> inf f <= 0 <= sup f
        IExpr lower = boundaryCondition(extremum(f, vars, false, engine), true, vars, engine);
        if (lower.isNIL() || lower.isFalse()) {
          return lower;
        }
        IExpr upper = boundaryCondition(extremum(f, vars, true, engine), false, vars, engine);
        if (upper.isNIL()) {
          return F.NIL;
        }
        return engine.evaluate(F.And(lower, upper));
      default:
        return F.NIL;
    }
  }

  /**
   * The condition under which the polynomial <code>f</code> of degree <code>1</code> or
   * <code>2</code> has a real root in <code>variable</code>:
   *
   * <ul>
   * <li><code>a1*x+a0</code> has one iff <code>a1 != 0 || a0 == 0</code></li>
   * <li><code>a2*x^2+a1*x+a0</code> has one iff the discriminant <code>a1^2-4*a2*a0</code> is non
   * negative (or the equation degenerates to the linear case)</li>
   * </ul>
   *
   * @param f the polynomial
   * @param variable the (single) quantified variable
   * @param engine the evaluation engine
   * @return the condition on the coefficients of <code>f</code> or {@link F#NIL} if the degree
   *         isn't <code>1</code> or <code>2</code>
   */
  private static IExpr realRootCondition(IExpr f, IExpr variable, EvalEngine engine) {
    IExpr coefficients = S.CoefficientList.ofNIL(engine, f, variable);
    if (!coefficients.isList()) {
      return F.NIL;
    }
    IAST coefficientList = (IAST) coefficients;
    if (coefficientList.argSize() == 2) {
      IExpr a0 = coefficientList.arg1();
      IExpr a1 = coefficientList.arg2();
      return engine.evaluate(F.Or(F.Unequal(a1, F.C0), F.Equal(a0, F.C0)));
    }
    if (coefficientList.argSize() == 3) {
      IExpr a0 = coefficientList.arg1();
      IExpr a1 = coefficientList.arg2();
      IExpr a2 = coefficientList.arg3();
      IExpr discriminant = F.Subtract(F.Sqr(a1), F.Times(F.C4, a2, a0));
      IExpr quadraticCase = F.And(F.Unequal(a2, F.C0), F.GreaterEqual(discriminant, F.C0));
      if (engine.evalTrue(F.Unequal(a2, F.C0))) {
        return engine.evaluate(F.GreaterEqual(discriminant, F.C0));
      }
      IExpr linearCase =
          F.And(F.Equal(a2, F.C0), F.Or(F.Unequal(a1, F.C0), F.Equal(a0, F.C0)));
      return engine.evaluate(F.Or(quadraticCase, linearCase));
    }
    return F.NIL;
  }

  /**
   * Compare the extremum with <code>0</code>. The strict comparison is an exact criterion which
   * doesn't depend on the extremum being attained.
   */
  private static IExpr strictCondition(Extremum extremum, ISymbol relation, IAST vars,
      EvalEngine engine) {
    if (extremum == null) {
      return F.NIL;
    }
    return acceptCondition(engine.evaluate(F.binaryAST2(relation, extremum.value, F.C0)), vars);
  }

  /**
   * Test whether the closed condition <code>inf f &lt;= 0</code> (respectively
   * <code>sup f &gt;= 0</code>) holds. If the extremum equals <code>0</code> the condition only
   * holds if the extremum is attained, so an unverified attainment stays undecided.
   *
   * @param extremum the infimum or the supremum of the polynomial
   * @param lower <code>true</code> for the infimum, <code>false</code> for the supremum
   */
  private static IExpr boundaryCondition(Extremum extremum, boolean lower, IAST vars,
      EvalEngine engine) {
    if (extremum == null) {
      return F.NIL;
    }
    IExpr value = extremum.value;
    ISymbol strict = lower ? S.Less : S.Greater;
    ISymbol opposite = lower ? S.Greater : S.Less;
    if (engine.evaluate(F.binaryAST2(strict, value, F.C0)).isTrue()) {
      return S.True;
    }
    if (engine.evaluate(F.binaryAST2(opposite, value, F.C0)).isTrue()) {
      return S.False;
    }
    if (extremum.attained) {
      ISymbol nonStrict = lower ? S.LessEqual : S.GreaterEqual;
      return acceptCondition(engine.evaluate(F.binaryAST2(nonStrict, value, F.C0)), vars);
    }
    return F.NIL;
  }

  /**
   * Accept a decided comparison or a condition which only depends on the remaining free parameters.
   */
  private static IExpr acceptCondition(IExpr comparison, IAST vars) {
    if (comparison.isTrue() || comparison.isFalse()) {
      return comparison;
    }
    if ((comparison.isComparatorFunction() || comparison.isBooleanFunction())
        && isFreeOfVariables(comparison, vars)) {
      return comparison;
    }
    return F.NIL;
  }

  /**
   * Compute the global infimum or supremum of <code>f</code> over <code>Reals^n</code>.
   *
   * <p>
   * The multivariate optimizer is tried first. As a fallback the variables are eliminated one after
   * the other which is sound because <code>inf_{x,y} f == inf_y (inf_x f)</code>.
   *
   * @param f the (polynomial) objective function
   * @param vars the list of variables
   * @param isMax <code>true</code> for the supremum, <code>false</code> for the infimum
   * @param engine the evaluation engine
   * @return the extremum or <code>null</code> if it couldn't be determined
   */
  private static Extremum extremum(IExpr f, IAST vars, boolean isMax, EvalEngine engine) {
    if (engine.getOptimizeExpressionDepth() != 0) {
      // the optimizers call Solve/Reduce internally; avoid mutual recursion
      return null;
    }
    final ISymbol head = isMax ? S.Maximize : S.Minimize;
    final boolean quietMode = engine.isQuietMode();
    engine.setQuietMode(true);
    engine.incOptimizeExpressionDepth();
    try {
      if (!vars.isList1()) {
        Extremum global = toExtremum(Maximize.multivariateExtremum(head, f, vars, isMax, engine), f,
            vars, engine);
        if (global != null) {
          return global;
        }
      }

      IExpr current = f;
      IASTAppendable point = F.ListAlloc(vars.argSize());
      for (int i = 1; i < vars.size(); i++) {
        IExpr variable = vars.get(i);
        if (current.isFree(variable, true)) {
          continue;
        }
        // the evaluator simplifies the parametric Piecewise results of the static optimizer methods
        IExpr result = head.of(engine, current, variable);
        if (!result.isList2()) {
          return null;
        }
        IExpr value = ((IAST) result).first();
        if (!isValidExtremumValue(value, variable)) {
          return null;
        }
        IExpr rules = ((IAST) result).second();
        if (rules.isList()) {
          point.appendArgs((IAST) rules);
        }
        if (value.isInfinity() || value.isNegativeInfinity()) {
          // the objective is unbounded, so the extremum isn't attained at a concrete point
          return new Extremum(value, false);
        }
        current = engine.evaluate(value);
      }
      if (!isFreeOfVariables(current, vars)) {
        return null;
      }
      return new Extremum(current, isAttained(f, vars, point, current, engine));
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return null;
    } finally {
      engine.decOptimizeExpressionDepth();
      engine.setQuietMode(quietMode);
    }
  }

  /** Convert an optimizer result <code>{value, {v_i -&gt; p_i}}</code> into an {@link Extremum}. */
  private static Extremum toExtremum(IExpr result, IExpr f, IAST vars, EvalEngine engine) {
    if (!result.isList2()) {
      return null;
    }
    IExpr value = ((IAST) result).first();
    if (value.isInfinity() || value.isNegativeInfinity()) {
      return new Extremum(value, false);
    }
    if (!isValidExtremumValue(value, null) || !isFreeOfVariables(value, vars)) {
      return null;
    }
    IExpr rules = ((IAST) result).second();
    if (!rules.isList()) {
      return null;
    }
    return new Extremum(value, isAttained(f, vars, (IAST) rules, value, engine));
  }

  /**
   * Test whether the optimizer returned a usable extremum value.
   *
   * @param variable the variable which was eliminated or <code>null</code>
   */
  private static boolean isValidExtremumValue(IExpr value, IExpr variable) {
    if (value.isInfinity() || value.isNegativeInfinity()) {
      return true;
    }
    if (value.isIndeterminate() || value.isDirectedInfinity() || value.isAST(S.Piecewise)) {
      return false;
    }
    if (!value.isFree(S.Minimize, true) || !value.isFree(S.Maximize, true)) {
      return false;
    }
    return variable == null || value.isFree(variable, true);
  }

  /**
   * Verify that the extremum is attained by substituting the minimizer/maximizer into the original
   * objective function.
   */
  private static boolean isAttained(IExpr f, IAST vars, IAST point, IExpr value,
      EvalEngine engine) {
    if (point.argSize() == 0) {
      return false;
    }
    IExpr candidate = f;
    for (int i = 0; i <= vars.argSize(); i++) {
      candidate = engine.evaluate(F.subst(candidate, point));
      if (isFreeOfVariables(candidate, vars)) {
        break;
      }
    }
    if (!isFreeOfVariables(candidate, vars) || candidate.isIndeterminate()
        || candidate.isDirectedInfinity()) {
      return false;
    }
    return engine.evaluate(F.Equal(candidate, value)).isTrue();
  }

  /**
   * Search a witness for the existence claim on a small rational grid. The candidate is verified by
   * substituting it into the condition, so a positive result is a proof.
   */
  private static boolean existsSampledWitness(IAST vars, IExpr cond, EvalEngine engine) {
    final int numberOfVariables = vars.argSize();
    IExpr[] samples = SAMPLE_POINTS;
    if (combinations(samples.length, numberOfVariables) > MAX_WITNESS_TESTS) {
      samples = SMALL_SAMPLE_POINTS;
      if (combinations(samples.length, numberOfVariables) > MAX_WITNESS_TESTS) {
        samples = new IExpr[] {F.C0};
      }
    }
    return testWitness(vars, cond, samples, new IExpr[numberOfVariables], 0, engine);
  }

  private static long combinations(int base, int exponent) {
    long result = 1;
    for (int i = 0; i < exponent; i++) {
      result *= base;
      if (result > MAX_WITNESS_TESTS) {
        return result;
      }
    }
    return result;
  }

  private static boolean testWitness(IAST vars, IExpr cond, IExpr[] samples, IExpr[] point,
      int index, EvalEngine engine) {
    if (index >= point.length) {
      IASTAppendable rules = F.ListAlloc(point.length);
      for (int i = 0; i < point.length; i++) {
        rules.append(F.Rule(vars.get(i + 1), point[i]));
      }
      // quiet: a sample point may be a pole of the condition
      return engine.evalQuiet(F.subst(cond, rules)).isTrue();
    }
    for (int i = 0; i < samples.length; i++) {
      point[index] = samples[i];
      if (testWitness(vars, cond, samples, point, index + 1, engine)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Decide a univariate existence claim with {@link S#Reduce}. An empty solution set refutes the
   * claim; a non empty solution set is only accepted if a concrete witness can be derived from it
   * and verified against the original condition.
   */
  private static IExpr existsByReduce(IAST vars, IExpr cond, EvalEngine engine) {
    IExpr reduced;
    final boolean quietMode = engine.isQuietMode();
    engine.setQuietMode(true);
    try {
      reduced = engine.evaluate(F.Reduce(cond, vars, S.Reals));
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return F.NIL;
    } finally {
      engine.setQuietMode(quietMode);
    }
    if (reduced.isFalse() || reduced.isTrue()) {
      return reduced;
    }
    if (!reduced.isFree(S.Reduce, true) || hasQuantifier(reduced)) {
      return F.NIL;
    }
    // instantiate the integer constants C(1), C(2),... of a parametrized solution family
    IExpr instance = engine.evaluate(F.subst(reduced, constantRules(reduced)));
    return verifyBindings(instance, vars, cond, engine) ? S.True : F.NIL;
  }

  /** Collect rules which replace all generated constants <code>C(k)</code> by <code>0</code>. */
  private static IAST constantRules(IExpr expr) {
    Set<IExpr> constants = new LinkedHashSet<IExpr>();
    collectConstants(expr, constants);
    IASTAppendable rules = F.ListAlloc(constants.size());
    for (IExpr constant : constants) {
      rules.append(F.Rule(constant, F.C0));
    }
    return rules;
  }

  private static void collectConstants(IExpr expr, Set<IExpr> constants) {
    if (expr.isAST(S.C, 2)) {
      constants.add(expr);
      return;
    }
    if (expr.isAST()) {
      for (IExpr arg : (IAST) expr) {
        collectConstants(arg, constants);
      }
    }
  }

  /**
   * Extract a concrete point from a solution set description and verify it against the original
   * condition.
   */
  private static boolean verifyBindings(IExpr region, IAST vars, IExpr cond, EvalEngine engine) {
    if (region.isOr()) {
      IAST or = (IAST) region;
      for (int i = 1; i < or.size(); i++) {
        if (verifyBindings(or.get(i), vars, cond, engine)) {
          return true;
        }
      }
      return false;
    }
    IASTAppendable rules = F.ListAlloc(vars.argSize());
    if (!collectBindings(region, vars, rules) || rules.argSize() != vars.argSize()) {
      return false;
    }
    return engine.evaluate(F.subst(cond, rules)).isTrue();
  }

  private static boolean collectBindings(IExpr region, IAST vars, IASTAppendable rules) {
    if (region.isAnd()) {
      IAST and = (IAST) region;
      for (int i = 1; i < and.size(); i++) {
        if (!collectBindings(and.get(i), vars, rules)) {
          return false;
        }
      }
      return true;
    }
    if (region.isAST(S.Element, 3)) {
      // a domain assertion doesn't bind a value
      return true;
    }
    if (region.isEqual() && region.size() == 3) {
      IAST equation = (IAST) region;
      IExpr lhs = equation.arg1();
      IExpr rhs = equation.arg2();
      if (vars.contains(lhs) && isFreeOfVariables(rhs, vars) && isConcreteRealValue(rhs)) {
        rules.append(F.Rule(lhs, rhs));
        return true;
      }
    }
    return false;
  }

  /**
   * Test whether the expression is a concrete real value. A witness which still depends on free
   * parameters (like <code>Sqrt(c)</code>) doesn't prove the existence claim for every parameter
   * value, so it isn't accepted.
   */
  private static boolean isConcreteRealValue(IExpr expr) {
    return expr.isFree(x -> x.isSymbol() && !x.isBuiltInSymbol(), true) && expr.isRealResult();
  }

  /**
   * Substitute <code>0</code> for all but one variable and decide the remaining univariate
   * existence claim. A witness on a slice is a witness for the whole domain, so only a positive
   * result is accepted.
   */
  private static IExpr existsBySlicing(IAST vars, IExpr cond, EvalEngine engine) {
    for (int i = 1; i < vars.size(); i++) {
      IASTAppendable rules = F.ListAlloc(vars.argSize());
      for (int j = 1; j < vars.size(); j++) {
        if (i != j) {
          rules.append(F.Rule(vars.get(j), F.C0));
        }
      }
      IExpr sliced = engine.evaluate(F.subst(cond, rules));
      if (exists(F.list(vars.get(i)), sliced, S.Reals, engine).isTrue()) {
        return S.True;
      }
    }
    return F.NIL;
  }

  /**
   * Decide <code>Exists(y, eq &amp;&amp; rest)</code> with parameters by the real solutions of the
   * equation: <code>Exists(y, y&gt;0 &amp;&amp; y^2==x)</code> is <code>x&gt;0</code>. The equation is
   * solved with {@link S#Reduce}, every root is put into the other conditions, and the sign
   * conditions of a square root which this creates are written without it.
   *
   * @return {@link F#NIL} if the condition isn't of this form
   */
  private static IExpr existsByEquation(IExpr y, IExpr cond, EvalEngine engine) {
    if (!cond.isAnd() || !y.isSymbol()) {
      return F.NIL;
    }
    IAST and = (IAST) cond;
    int index = and.indexOf(atom -> atom.isEqual() && !atom.isFree(y)
        && engine.evaluate(F.PolynomialQ(F.Subtract(atom.first(), atom.second()), y)).isTrue());
    if (index < 0) {
      return F.NIL;
    }
    IExpr equation = and.get(index);
    IAST rest = and.removeAtCopy(index);
    IExpr solutions;
    final boolean quietMode = engine.isQuietMode();
    engine.setQuietMode(true);
    try {
      solutions = engine.evaluate(F.Reduce(equation, y, S.Reals));
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return F.NIL;
    } finally {
      engine.setQuietMode(quietMode);
    }
    // P && (y==e1 || y==e2 || ...)
    IASTAppendable conditions = F.And();
    IASTAppendable roots = F.ListAlloc();
    IAST parts = solutions.isAnd() ? (IAST) solutions : F.And(solutions);
    for (IExpr part : parts) {
      if (part.isFree(y)) {
        conditions.append(part);
      } else if (part.isOr()) {
        for (IExpr alternative : (IAST) part) {
          if (!addRoot(alternative, y, roots)) {
            return F.NIL;
          }
        }
      } else if (!addRoot(part, y, roots)) {
        return F.NIL;
      }
    }
    if (roots.isEmpty()) {
      return F.NIL;
    }
    IASTAppendable alternatives = F.Or();
    for (IExpr root : roots) {
      IExpr substituted = engine.evaluate(F.subst(rest.oneIdentity1(), y, root));
      IExpr withoutRoot = sqrtSignConditions(substituted, engine);
      if (withoutRoot.isNIL()) {
        return F.NIL;
      }
      IASTAppendable branch = conditions.copyAppendable();
      branch.append(withoutRoot);
      alternatives.append(branch);
    }
    // evaluated first: an unevaluated False would count as a variable
    IExpr combined = engine.evaluate(alternatives);
    IAST parameters = new VariablesSet(combined).getVarList();
    if (parameters.isEmpty()) {
      return combined;
    }
    IExpr reduced = engine.evalQuiet(F.Reduce(combined, parameters, S.Reals));
    return reduced.isFree(S.Reduce) && reduced.isFree(y) ? reduced : F.NIL;
  }

  private static boolean addRoot(IExpr equation, IExpr y, IASTAppendable roots) {
    if (equation.isEqual() && equation.first().equals(y) && equation.second().isFree(y)) {
      roots.append(equation.second());
      return true;
    }
    return false;
  }

  /**
   * Write the sign conditions <code>c*Sqrt(u) REL 0</code> of a condition without the square root,
   * for a nonnegative <code>u</code>: <code>Sqrt(u)&gt;0</code> is <code>u&gt;0</code>,
   * <code>Sqrt(u)&gt;=0</code> is <code>True</code>, <code>Sqrt(u)&lt;0</code> is <code>False</code>
   * and <code>Sqrt(u)&lt;=0</code> is <code>u==0</code>.
   *
   * @return {@link F#NIL} if a square root is left in another position
   */
  private static IExpr sqrtSignConditions(IExpr expr, EvalEngine engine) {
    if (expr.isAnd() || expr.isOr()) {
      IAST logic = (IAST) expr;
      IASTMutable result = logic.copy();
      for (int i = 1; i < logic.size(); i++) {
        IExpr arg = sqrtSignConditions(logic.get(i), engine);
        if (arg.isNIL()) {
          return F.NIL;
        }
        result.set(i, arg);
      }
      return engine.evaluate(result);
    }
    if (expr.isFree(x -> x.isPower() && x.exponent().isFraction(), true)) {
      return expr;
    }
    if (expr.isAST2() && expr.isFunctionID(ID.Less, ID.LessEqual, ID.Greater, ID.GreaterEqual)) {
      IExpr f = engine.evaluate(F.Subtract(expr.first(), expr.second()));
      IExpr c = F.C1;
      IExpr root = f;
      if (f.isTimes() && f.size() == 3 && f.first().isReal()) {
        c = f.first();
        root = f.second();
      }
      if (root.isSqrt() && root.base().isFree(x -> x.isPower() && x.exponent().isFraction(), true)
          && !c.isZero()) {
        IExpr u = root.base();
        IExpr head = expr.head();
        if (c.isNegative()) {
          // c*Sqrt(u) REL 0 is Sqrt(u) REL' 0
          head = head == S.Less ? S.Greater
              : head == S.LessEqual ? S.GreaterEqual : head == S.Greater ? S.Less : S.LessEqual;
        }
        if (head == S.Greater) {
          return F.Greater(u, F.C0);
        }
        if (head == S.GreaterEqual) {
          return S.True;
        }
        if (head == S.Less) {
          return S.False;
        }
        return F.Equal(u, F.C0);
      }
    }
    return F.NIL;
  }

  /** At most this many <code>Abs</code> terms are split into their two sign cases. */
  private static final int MAX_ABS_SPLITS = 6;

  /**
   * Decide a real sentence with linear atoms by quantifier elimination over an ordered field
   * ({@link RationalQE}). An <code>Abs(u)</code> is split into the cases <code>u&gt;=0</code> and
   * <code>u&lt;0</code> inside the scope of the quantifier whose body contains it, so that e.g. the
   * epsilon-delta sentence of a linear limit
   * <code>ForAll(eps, eps&gt;0, Exists(del, del&gt;0, ForAll(x, 0&lt;Abs(x-1)&lt;del, Abs(2*x-2)&lt;eps)))</code>
   * is decided.
   *
   * @return {@link F#NIL} if the sentence isn't linear
   */
  private static IExpr resolveLinear(IExpr expr, EvalEngine engine) {
    int[] budget = new int[] {MAX_ABS_SPLITS};
    IExpr split = withoutAbs(absComparisons(expr, engine), budget);
    if (split.isNIL()) {
      return F.NIL;
    }
    split = splitAbs(split, budget);
    if (split.isNIL() || !split.isFree(S.Element)) {
      return F.NIL;
    }
    Formula formula = Lowering.lower(split);
    if (formula == null || formula.containsDivisibility() || !RationalQE.hasOrdering(formula)) {
      return F.NIL;
    }
    List<Variable> targets = new ArrayList<Variable>(new TreeSet<Variable>(formula.freeVariables()));
    IExpr reduced = RationalQE.reduce(formula, targets, false);
    return reduced.isPresent() ? absorb(engine.evaluate(reduced)) : F.NIL;
  }

  /**
   * Drop the alternatives of a disjunction which another alternative absorbs:
   * <code>A || (A &amp;&amp; B)</code> is <code>A</code>.
   */
  private static IExpr absorb(IExpr expr) {
    if (!expr.isOr()) {
      return expr;
    }
    IAST or = (IAST) expr;
    List<Set<IExpr>> branches = new ArrayList<Set<IExpr>>(or.argSize());
    for (IExpr alternative : or) {
      Set<IExpr> atoms = new LinkedHashSet<IExpr>();
      if (alternative.isAnd()) {
        for (IExpr atom : (IAST) alternative) {
          atoms.add(atom);
        }
      } else {
        atoms.add(alternative);
      }
      branches.add(atoms);
    }
    IASTAppendable result = F.ast(S.Or, or.argSize());
    for (int i = 0; i < branches.size(); i++) {
      boolean absorbed = false;
      for (int j = 0; j < branches.size() && !absorbed; j++) {
        if (i != j && branches.get(i).containsAll(branches.get(j))
            // of two equal alternatives the first one is kept
            && (branches.get(i).size() > branches.get(j).size() || j < i)) {
          absorbed = true;
        }
      }
      if (!absorbed) {
        result.append(or.get(i + 1));
      }
    }
    return result.oneIdentity0();
  }

  /**
   * Rewrite a comparison of an <code>Abs(u)</code> with a term which is free of <code>Abs</code>
   * without splitting the whole formula into cases: <code>Abs(u)&lt;c</code> is
   * <code>-c&lt;u&lt;c</code> and <code>Abs(u)&gt;c</code> is <code>u&gt;c || u&lt;-c</code>, for
   * every real <code>c</code>. A chained <code>Inequality</code> is written as a conjunction first.
   */
  private static IExpr absComparisons(IExpr expr, EvalEngine engine) {
    if (expr.isAST(S.Inequality) && expr.argSize() >= 3 && (expr.argSize() & 1) == 1) {
      IAST chain = (IAST) expr;
      IASTAppendable and = F.ast(S.And, chain.argSize() / 2);
      for (int i = 1; i + 2 < chain.size(); i += 2) {
        and.append(F.binaryAST2(chain.get(i + 1), chain.get(i), chain.get(i + 2)));
      }
      return absComparisons(and, engine);
    }
    if (expr.isAST2() && expr.isFunctionID(ID.Less, ID.LessEqual, ID.Greater, ID.GreaterEqual)) {
      IExpr lhs = expr.first();
      IExpr rhs = expr.second();
      IExpr head = expr.head();
      if (!lhs.isAbs() && rhs.isAbs()) {
        // c < Abs(u) is Abs(u) > c
        IExpr swap = lhs;
        lhs = rhs;
        rhs = swap;
        head = head == S.Less ? S.Greater
            : head == S.LessEqual ? S.GreaterEqual : head == S.Greater ? S.Less : S.LessEqual;
      }
      if (lhs.isAbs() && lhs.isAST1() && rhs.isFree(S.Abs)) {
        IExpr u = lhs.first();
        IExpr minusC = F.Negate(rhs);
        if (head == S.Less || head == S.LessEqual) {
          return F.And(F.binaryAST2(head, minusC, u), F.binaryAST2(head, u, rhs));
        }
        IExpr below = head == S.Greater ? S.Less : S.LessEqual;
        return F.Or(F.binaryAST2(head, u, rhs), F.binaryAST2(below, u, minusC));
      }
      return expr;
    }
    if (expr.isAST() && (isConnective(expr.head()) || expr.isAST(S.ForAll)
        || expr.isAST(S.Exists))) {
      IAST ast = (IAST) expr;
      IASTMutable result = ast.copy();
      int start = ast.isAST(S.ForAll) || ast.isAST(S.Exists) ? 2 : 1;
      for (int i = start; i < ast.size(); i++) {
        result.set(i, absComparisons(ast.get(i), engine));
      }
      return result;
    }
    return expr;
  }

  /**
   * Split the <code>Abs</code> terms in the bodies of all quantifiers, innermost first, and write a
   * restricted quantifier with two arguments.
   */
  private static IExpr withoutAbs(IExpr expr, int[] budget) {
    if ((expr.isAST(S.ForAll) || expr.isAST(S.Exists)) && (expr.isAST2() || expr.isAST3())) {
      IAST quantifier = (IAST) expr;
      boolean forAll = quantifier.isAST(S.ForAll);
      IExpr body = quantifier.arg2();
      if (quantifier.isAST3()) {
        body = forAll ? F.Implies(quantifier.arg2(), quantifier.arg3())
            : F.And(quantifier.arg2(), quantifier.arg3());
      }
      IExpr inner = withoutAbs(body, budget);
      IExpr split = inner.isPresent() ? splitAbs(inner, budget) : F.NIL;
      return split.isPresent() ? F.binaryAST2(quantifier.head(), quantifier.arg1(), split) : F.NIL;
    }
    if (expr.isAST() && isConnective(expr.head())) {
      IAST ast = (IAST) expr;
      IASTMutable result = ast.copy();
      for (int i = 1; i < ast.size(); i++) {
        IExpr arg = withoutAbs(ast.get(i), budget);
        if (arg.isNIL()) {
          return F.NIL;
        }
        result.set(i, arg);
      }
      return result;
    }
    return expr;
  }

  private static boolean isConnective(IExpr head) {
    return head == S.And || head == S.Or || head == S.Not || head == S.Implies
        || head == S.Equivalent || head == S.Xor || head == S.Nand || head == S.Nor;
  }

  /**
   * <code>F(Abs(u))</code> is <code>u&gt;=0 &amp;&amp; F(u) || u&lt;0 &amp;&amp; F(-u)</code>, for the
   * <code>Abs</code> terms outside of a nested quantifier.
   *
   * @return {@link F#NIL} if there are more than {@link #MAX_ABS_SPLITS} of them
   */
  private static IExpr splitAbs(IExpr formula, int[] budget) {
    IExpr abs = findAbs(formula);
    if (abs.isNIL()) {
      return formula;
    }
    if (--budget[0] < 0) {
      return F.NIL;
    }
    IExpr u = abs.first();
    IExpr positive = splitAbs(F.And(F.GreaterEqual(u, F.C0), F.subst(formula, abs, u)), budget);
    if (positive.isNIL()) {
      return F.NIL;
    }
    IExpr negative = splitAbs(F.And(F.Less(u, F.C0), F.subst(formula, abs, F.Negate(u))), budget);
    return negative.isPresent() ? F.Or(positive, negative) : F.NIL;
  }

  /** The first <code>Abs(u)</code> outside of a nested quantifier, or {@link F#NIL}. */
  private static IExpr findAbs(IExpr expr) {
    if (expr.isAbs() && expr.isAST1()) {
      return expr;
    }
    if (expr.isAST(S.ForAll) || expr.isAST(S.Exists) || !expr.isAST()) {
      return F.NIL;
    }
    for (IExpr arg : (IAST) expr) {
      IExpr abs = findAbs(arg);
      if (abs.isPresent()) {
        return abs;
      }
    }
    return F.NIL;
  }

  /** Test whether the expression contains a {@link S#ForAll} or {@link S#Exists} quantifier. */
  private static boolean hasQuantifier(IExpr expr) {
    if (expr.isAST(S.Exists) || expr.isAST(S.ForAll)) {
      return true;
    }
    if (expr.isAST()) {
      for (IExpr arg : (IAST) expr) {
        if (hasQuantifier(arg)) {
          return true;
        }
      }
    }
    return false;
  }

  /** Test whether the expression contains an inequality which forces the variables to be real. */
  private static boolean containsInequality(IExpr expr) {
    if (expr.isFunctionID(ID.Less, ID.LessEqual, ID.Greater, ID.GreaterEqual, ID.Inequality)) {
      return true;
    }
    if (expr.isAST()) {
      for (IExpr arg : (IAST) expr) {
        if (containsInequality(arg)) {
          return true;
        }
      }
    }
    return false;
  }

  private static boolean isFreeOfVariables(IExpr expr, IAST vars) {
    return expr.isFree(x -> vars.contains(x), true);
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_1_2;
  }

  /** {@inheritDoc} */
  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public void setUp(ISymbol newSymbol) {
    setOptions(newSymbol, SolveOptions.RESOLVE_KEYS, SolveOptions.RESOLVE_DEFAULTS);
  }
}
