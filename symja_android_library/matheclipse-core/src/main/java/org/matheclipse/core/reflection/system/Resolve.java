package org.matheclipse.core.reflection.system;

import java.util.LinkedHashSet;
import java.util.Set;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
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
public class Resolve extends AbstractEvaluator {

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
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    ISymbol domain = null;
    if (ast.isAST2()) {
      IExpr arg2 = ast.arg2();
      if (arg2 != S.Reals && arg2 != S.Complexes) {
        return F.NIL;
      }
      domain = (ISymbol) arg2;
    }
    try {
      return resolve(ast.arg1(), domain, engine);
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
      return F.NIL;
    }
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
    if (expr.isAST(S.Exists) || expr.isAST(S.ForAll)) {
      return resolveQuantifier((IAST) expr, domain, engine);
    }
    if (expr.isNot()) {
      IExpr negated = resolve(expr.first(), domain, engine);
      return negated.isPresent() ? engine.evaluate(F.Not(negated)) : F.NIL;
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
      return existence.isPresent() ? engine.evaluate(F.Not(existence)) : F.NIL;
    }
    return exists(boundVars, condition, quantifierDomain, engine);
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
      return engine.evaluate(F.subst(cond, rules)).isTrue();
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
    //
  }
}
