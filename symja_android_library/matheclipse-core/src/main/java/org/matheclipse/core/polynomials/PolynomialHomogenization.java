package org.matheclipse.core.polynomials;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ReduceVariableEqual;
import org.matheclipse.core.expression.AbstractIntegerSym;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Forward and backward substitutions of expressions for polynomials. See <a href=
 * "https://www.research.ed.ac.uk/portal/files/413486/Solving_Symbolic_Equations_%20with_PRESS.pdf">3.5
 * Homogenization</a>
 */
public class PolynomialHomogenization {

  /** Largest <code>k</code> for which <code>Tan(k*u)</code> is rewritten in <code>Tan(u)</code>. */
  private static final int MAX_TAN_MULTIPLE = 12;

  /**
   * Largest integer exponent of a sum, which is walked instead of substituted as a whole:
   * <code>(1+Sin(x))^2</code> gives <code>(1+t)^2</code>, but <code>(1+Sin(x))^1000</code> gives
   * <code>t^1000</code>.
   */
  private static final int MAX_WALKED_EXPONENT = 64;

  /** Largest integer summand split off an exponent: <code>3^(2+x) -> 9*3^x</code>. */
  private static final int MAX_SPLIT_EXPONENT = 1024;

  /**
   * Largest integer multiplier split off an exponent as the power of a kernel:
   * <code>E^(3*x) -> t^3</code>, but <code>10^(-1500/t)</code> stays a kernel of its own - as the
   * power <code>-1500</code> of <code>10^(1/t)</code> it made a degree 1500 polynomial which
   * Factor, called by Solve, never finished.
   */
  private static final int MAX_KERNEL_MULTIPLIER = 64;

  /**
   * Rewrite <code>Sech(u)^(2*n) -> (1 - Tanh(u)^2)^n</code> and
   * <code>Csch(u)^(2*n) -> (Coth(u)^2 - 1)^n</code>, but only for an argument <code>u</code> whose
   * <code>Tanh(u)</code> (respectively <code>Coth(u)</code>) occurs as well. A <code>Sech(u)</code>
   * on its own is already a good substitution kernel.
   */
  private static final class TanhSechTransform implements Function<IExpr, IExpr> {
    private final Set<IExpr> tanhArgs = new HashSet<>();
    private final Set<IExpr> cothArgs = new HashSet<>();

    TanhSechTransform(IExpr expr) {
      collectArgs(expr);
    }

    private void collectArgs(IExpr expr) {
      if (expr.isAST()) {
        if (expr.isAST(S.Tanh, 2)) {
          tanhArgs.add(expr.first());
        } else if (expr.isAST(S.Coth, 2)) {
          cothArgs.add(expr.first());
        }
        ((IAST) expr).forEach(this::collectArgs);
      }
    }

    boolean isApplicable() {
      return !tanhArgs.isEmpty() || !cothArgs.isEmpty();
    }

    @Override
    public IExpr apply(IExpr x) {
      if (x.isPower()) {
        IExpr base = x.base();
        int exponent = x.exponent().toIntDefault();
        if (F.isPresent(exponent) && exponent != 0 && (exponent % 2) == 0) {
          if (base.isAST(S.Sech, 2) && tanhArgs.contains(base.first())) {
            IExpr oneMinusTanh2 = F.Plus(F.C1, F.Negate(F.Power(F.Tanh(base.first()), F.C2)));
            return evenPower(oneMinusTanh2, exponent);
          }
          if (base.isAST(S.Csch, 2) && cothArgs.contains(base.first())) {
            IExpr coth2MinusOne = F.Plus(F.CN1, F.Power(F.Coth(base.first()), F.C2));
            return evenPower(coth2MinusOne, exponent);
          }
        }
      }
      return F.NIL;
    }
  }

  /**
   * Rewrite even powers of <code>Cos(u)</code> in <code>Sin(u)</code> (or the other way round), so
   * that a single trigonometric kernel per argument <code>u</code> remains.
   */
  private static final class CosSinTransform {
    private static final int SIN_ODD = 0;
    private static final int COS_ODD = 1;

    /** Number of odd powers of <code>Sin(u)</code> and <code>Cos(u)</code> per argument u */
    private final Map<IExpr, int[]> statsMap = new HashMap<>();

    private void determineStatsRecursive(IExpr expr) {
      if (!expr.isAST()) {
        return;
      }
      if (expr.isPower()) {
        int exponent = expr.exponent().toIntDefault();
        if (F.isPresent(exponent)) {
          IExpr base = expr.base();
          if (base.isCos() || base.isSin()) {
            addStatistics((IAST) base, exponent);
            // Skip the power base to prevent counting the same trig node twice.
            determineStatsRecursive(base.first());
            return;
          }
        }
      }
      if (expr.isCos() || expr.isSin()) {
        addStatistics((IAST) expr, 1);
        determineStatsRecursive(expr.first());
        return;
      }
      ((IAST) expr).forEach(this::determineStatsRecursive);
    }

    private void addStatistics(IAST trigFunction, int exponent) {
      if ((exponent % 2) != 0) {
        int[] stats = statsMap.computeIfAbsent(trigFunction.arg1(), k -> new int[2]);
        stats[trigFunction.isCos() ? COS_ODD : SIN_ODD]++;
      }
    }

    /**
     * Rewrite to <code>Sin</code>, unless only <code>Cos(u)</code> occurs with an odd power.
     */
    private boolean isRewriteToSin(IExpr arg) {
      int[] stats = statsMap.get(arg);
      return stats == null || stats[SIN_ODD] != 0 || stats[COS_ODD] == 0;
    }

    private IExpr rewriteEvenCosSinFunctions(IExpr x) {
      if (x.isPower()) {
        IExpr base = x.base();
        int exponent = x.exponent().toIntDefault();
        if (F.isPresent(exponent) && exponent != 0 && (exponent % 2) == 0) {
          if (base.isCos() && isRewriteToSin(base.first())) {
            IExpr oneMinusSin2 = F.Plus(F.C1, F.Negate(F.Power(F.Sin(base.first()), F.C2)));
            return evenPower(oneMinusSin2, exponent);
          }
          if (base.isSin() && !isRewriteToSin(base.first())) {
            IExpr oneMinusCos2 = F.Plus(F.C1, F.Negate(F.Power(F.Cos(base.first()), F.C2)));
            return evenPower(oneMinusCos2, exponent);
          }
        }
      }
      return F.NIL;
    }

    IExpr applyGlobal(IExpr x, EvalEngine engine) {
      IExpr trigExpand = S.TrigExpand.of(engine, x);
      determineStatsRecursive(trigExpand);
      return F.subst(trigExpand, this::rewriteEvenCosSinFunctions);
    }
  }

  /**
   * A power <code>base ^ (n * rest)</code> decomposed into the substitution kernel
   * <code>base ^ rest</code> and the integer power <code>n</code> of the kernel.
   */
  private static final class PowerKernel {
    final IExpr kernel;
    final int power;

    PowerKernel(IExpr kernel, int power) {
      this.kernel = kernel;
      this.power = power;
    }
  }

  /**
   * Variables ({@link ISymbol}s) which are substituted from the original polynomial (backward
   * substitution). Uses {@link LinkedHashMap} to preserve insertion order, which is important for
   * consistent variable naming in forward substitution (e.g. t1, t2, t3, ...). The order of
   * variable registration is determined by the depth-first traversal of the expression tree in
   * {@link #collect(IExpr, boolean)}.
   */
  private final Map<ISymbol, IExpr> substitutedVariables = new LinkedHashMap<ISymbol, IExpr>();

  /**
   * Expressions which are substituted with variables(ISymbol) from the original polynomial (forward
   * substitution).
   */
  private final Map<IExpr, ISymbol> substitutedExpr = new HashMap<IExpr, ISymbol>();

  /**
   * The least common multiple of all rational-exponent denominators a base expression occurs with.
   * If the base <code>x</code> appears as <code>x^(1/2)</code> and <code>x^(1/3)</code> the entry
   * for <code>x</code> is <code>6</code>. Filled by the first pass of
   * {@link #collect(IExpr, boolean)}, before any substitution symbol is created.
   */
  private final Map<IExpr, IInteger> baseDenominatorLCM = new HashMap<IExpr, IInteger>();

  /**
   * Maps a dummy symbol <code>t</code> to the integer <code>k > 1</code> of its substitution
   * <code>t = u^(1/k)</code>, which eliminates all fractional exponents of the original base
   * <code>u</code>. Symbols without an entry have <code>k = 1</code>.
   */
  private final Map<ISymbol, IInteger> symbolDenominatorLCM =
      new IdentityHashMap<ISymbol, IInteger>();

  /**
   * If <code>true</code> the forward substitution also rewrites trigonometric and hyperbolic
   * functions (<code>TrigExpand</code>, <code>Cos^2 = 1-Sin^2</code>,
   * <code>Sech^2 = 1-Tanh^2</code>, multiple angles of <code>Tan</code>), so that a single kernel
   * remains. If <code>false</code> these functions are independent substitution kernels.
   */
  private final boolean isFactorTrigOption;

  private final EvalEngine engine;

  /**
   * Forward and backward substitutions of expressions for polynomials. See <a href=
   * "https://www.research.ed.ac.uk/portal/files/413486/Solving_Symbolic_Equations_%20with_PRESS.pdf">3.5
   * Homogenization</a> (page 112)
   *
   * @param engine the evaluation engine
   */
  public PolynomialHomogenization(EvalEngine engine) {
    this(engine, true);
  }

  /**
   * @param engine the evaluation engine
   * @param isFactorTrigOption if <code>true</code> rewrite trigonometric and hyperbolic functions
   *        in {@link #replaceForward(IExpr)} and {@link #replaceForwardList(IAST)}
   */
  public PolynomialHomogenization(EvalEngine engine, boolean isFactorTrigOption) {
    this.engine = engine;
    this.isFactorTrigOption = isFactorTrigOption;
  }

  /**
   * Determine the least-common-multiple-factor associated with a substitution symbol.
   *
   * @param x
   */
  public IInteger getLCM(IExpr x) {
    IInteger i = symbolDenominatorLCM.get(x);
    return i == null ? F.C1 : i;
  }

  /**
   * Forward substitution - transforming the expression into a polynomial expression by introducing
   * substitution variables. After transforming the polynomial expression may be solvable by a
   * polynomial factorization.
   * <p>
   * Don't call this method for the equations of a system one after the other; the substitution
   * <code>t = u^(1/k)</code> is only known after all of them were analysed. Use
   * {@link #replaceForwardList(IAST)} instead.
   *
   * @param expression
   * @return the polynomial expression
   */
  public IExpr replaceForward(final IExpr expression) {
    IExpr expr = preTransform(expression);
    collect(expr, true);
    collect(expr, false);
    return replaceForwardRecursive(expr);
  }

  /**
   * Forward substitution of all expressions in the list with the same substitution variables. All
   * expressions are analysed before the first one is rewritten.
   *
   * @param listOfExpressions
   * @return the list of polynomial expressions
   * @see #replaceForward(IExpr)
   */
  public IAST replaceForwardList(final IAST listOfExpressions) {
    IExpr[] transformed = new IExpr[listOfExpressions.argSize()];
    for (int i = 0; i < transformed.length; i++) {
      transformed[i] = preTransform(listOfExpressions.get(i + 1));
    }
    return F.List(replaceForwardAll(transformed));
  }

  /**
   * Forward substitution - transforming the numerator and denominator expression into polynomial
   * expressions by introducing substitution variables. In contrast to
   * {@link #replaceForward(IExpr)} the expressions are substituted as they are.
   *
   * @param numerator
   * @param denominator
   * @return polynomial numerator at index '0'; and polynomial denominator at index '1'
   */
  public IExpr[] replaceForward(final IExpr numerator, final IExpr denominator) {
    return replaceForwardAll(numerator, denominator);
  }

  private IExpr[] replaceForwardAll(final IExpr... expressions) {
    for (int i = 0; i < expressions.length; i++) {
      collect(expressions[i], true);
    }
    for (int i = 0; i < expressions.length; i++) {
      collect(expressions[i], false);
    }
    IExpr[] result = new IExpr[expressions.length];
    for (int i = 0; i < expressions.length; i++) {
      result[i] = replaceForwardRecursive(expressions[i]);
    }
    return result;
  }

  /**
   * Rewrite the expression, so that as few as possible different substitution kernels remain.
   */
  private IExpr preTransform(final IExpr expression) {
    IExpr expr = unifySharedIntegerBases(expression);
    expr = F.subst(expr, PolynomialHomogenization::unifyIntegerPowers);
    // Normalize Log(v)/Log(c) -> Log(c,v) so Log-based homogenization works
    // even when Symja auto-evaluates Log(c,v) to Log(v)/Log(c) beforehand.
    expr = F.subst(expr, PolynomialHomogenization::normalizeLogBase);
    if (!isFactorTrigOption) {
      return expr;
    }
    if (!expr.isFree(x -> x.isCos() || x.isSin(), false)) {
      expr = new CosSinTransform().applyGlobal(expr, engine);
      return F.evalExpandAll(expr, engine);
    }
    if (!expr.isFree(x -> x.isTan(), false)) {
      expr = tanMultipleAngle(expr);
    }
    if (!expr.isFree(x -> x.isAST(S.Sech, 2) || x.isAST(S.Csch, 2), false)) {
      TanhSechTransform transform = new TanhSechTransform(expr);
      if (transform.isApplicable()) {
        IExpr temp = F.subst(expr, transform);
        if (temp != expr) {
          expr = F.evalExpandAll(temp, engine);
        }
      }
    }
    return expr;
  }

  /**
   * <code>base ^ (exponent/2)</code> for an even exponent.
   */
  private static IExpr evenPower(IExpr base, int exponent) {
    return exponent == 2 ? base : F.Power(base, F.ZZ(exponent / 2));
  }

  /**
   * If <code>Tan(u)</code> occurs with different integer multiples of the same <code>u</code>
   * rewrite <code>Tan(k*u)</code> as the rational function
   * <code>Im((1+I*t)^k) / Re((1+I*t)^k)</code> of <code>t = Tan(u)</code>.
   */
  private IExpr tanMultipleAngle(IExpr expr) {
    Map<IExpr, Set<Integer>> multiples = new HashMap<>();
    collectTanMultiples(expr, multiples);
    boolean applicable = false;
    for (Set<Integer> set : multiples.values()) {
      if (set.size() > 1) {
        applicable = true;
        break;
      }
    }
    if (!applicable) {
      return expr;
    }
    IExpr temp = F.subst(expr, x -> {
      if (x.isTan()) {
        IExpr arg = x.first();
        int k = tanMultiple(arg);
        if (k > 1 && k <= MAX_TAN_MULTIPLE) {
          IExpr u = arg.rest().oneIdentity1();
          if (multiples.get(u).size() > 1) {
            return tanOfMultiple(k, F.Tan(u));
          }
        }
      }
      return F.NIL;
    });
    return temp == expr ? expr : engine.evaluate(F.Together(temp));
  }

  private static void collectTanMultiples(IExpr expr, Map<IExpr, Set<Integer>> multiples) {
    if (expr.isAST()) {
      if (expr.isTan()) {
        IExpr arg = expr.first();
        int k = tanMultiple(arg);
        IExpr u = k > 1 ? arg.rest().oneIdentity1() : arg;
        multiples.computeIfAbsent(u, key -> new HashSet<>()).add(k);
      }
      ((IAST) expr).forEach(x -> collectTanMultiples(x, multiples));
    }
  }

  /**
   * @return <code>k > 1</code> if <code>arg</code> has the form <code>k*u</code>; <code>1</code>
   *         otherwise
   */
  private static int tanMultiple(IExpr arg) {
    if (arg.isTimes() && arg.first().isInteger()) {
      int k = arg.first().toIntDefault();
      if (F.isPresent(k) && k > 1) {
        return k;
      }
    }
    return 1;
  }

  private static IExpr tanOfMultiple(int k, IExpr t) {
    IASTAppendable numerator = F.PlusAlloc(k / 2 + 1);
    IASTAppendable denominator = F.PlusAlloc(k / 2 + 1);
    for (int j = 0; j <= k; j++) {
      IInteger coefficient = AbstractIntegerSym.binomial(k, j);
      if (((j / 2) % 2) != 0) {
        coefficient = coefficient.negate();
      }
      IExpr term = j == 0 ? coefficient : F.Times(coefficient, F.Power(t, F.ZZ(j)));
      if ((j % 2) == 0) {
        denominator.append(term);
      } else {
        numerator.append(term);
      }
    }
    return F.Times(numerator, F.Power(denominator, F.CN1));
  }

  /**
   * If the integer bases of two powers are powers of the same integer, rewrite them to this common
   * base: <code>4^x - 2^x</code> gives <code>2^(2*x) - 2^x</code>.
   */
  private static IExpr unifySharedIntegerBases(IExpr expression) {
    Map<IInteger, Set<IInteger>> rootToBases = new HashMap<>();
    Map<IInteger, long[]> baseToRoot = new HashMap<>();
    collectIntegerBases(expression, rootToBases, baseToRoot);
    if (baseToRoot.isEmpty()) {
      return expression;
    }
    return F.subst(expression, x -> {
      if (x.isPower() && x.base().isInteger()) {
        long[] rootAndPower = baseToRoot.get(x.base());
        if (rootAndPower != null && rootAndPower[1] > 1
            && rootToBases.get(F.ZZ(rootAndPower[0])).size() > 1) {
          IExpr exponent = S.Expand.of(F.Times(F.ZZ(rootAndPower[1]), x.exponent()));
          return F.Power(F.ZZ(rootAndPower[0]), exponent);
        }
      }
      return F.NIL;
    });
  }

  private static void collectIntegerBases(IExpr expr, Map<IInteger, Set<IInteger>> rootToBases,
      Map<IInteger, long[]> baseToRoot) {
    if (expr.isAST()) {
      if (expr.isPower() && expr.base().isInteger() && !expr.exponent().isNumber()) {
        IInteger base = (IInteger) expr.base();
        long value = base.toLongDefault();
        if (F.isPresent(value) && value > 1 && !baseToRoot.containsKey(base)) {
          long[] rootAndPower = perfectPower(value);
          baseToRoot.put(base, rootAndPower);
          rootToBases.computeIfAbsent(F.ZZ(rootAndPower[0]), k -> new HashSet<>()).add(base);
        }
      }
      ((IAST) expr).forEach(x -> collectIntegerBases(x, rootToBases, baseToRoot));
    }
  }

  /**
   * @return <code>{r, k}</code> with <code>value == r^k</code> and the largest possible
   *         <code>k</code>
   */
  private static long[] perfectPower(long value) {
    for (int k = 62; k > 1; k--) {
      long estimate = Math.round(Math.pow(value, 1.0 / k));
      for (long root = Math.max(2, estimate - 1); root <= estimate + 1; root++) {
        if (F.ZZ(root).powerRational(k).equals(F.ZZ(value))) {
          return new long[] {root, k};
        }
      }
    }
    return new long[] {value, 1};
  }

  /**
   * Unify powers in two steps like <code>3^(2+2*x)</code> to <code>3^2 * 3^(2*x)</code>. Merge
   * powers like <code>2^(2*x)*3^(2*x)</code> to <code>6^(2*x)</code>.
   *
   * @param x
   * @return {@link F#NIL} if no rewrite applied
   */
  private static IExpr unifyIntegerPowers(IExpr x) {
    if (x.isTimes()) {
      IAST timesAST = (IAST) x;
      boolean evaled = false;
      // first step
      IASTAppendable times = F.TimesAlloc(timesAST.argSize());
      for (int i = 1; i <= timesAST.argSize(); i++) {
        IExpr arg = timesAST.get(i);
        if (arg.isPower() && arg.base().isInteger()) {
          IInteger base = (IInteger) arg.base();
          if (base.isPositive()) {
            IExpr exp = arg.exponent();
            if (exp.isPlus() && exp.first().isInteger()) {
              int n = exp.first().toIntDefault();
              if (n > 0 && n <= MAX_SPLIT_EXPONENT) {
                evaled = true;
                IExpr rest = exp.rest().oneIdentity1();
                times.append(base.powerRational(n));
                times.append(F.Power(base, rest));
                continue;
              }
            }
          }
        }
        times.append(arg);
      }

      // second step
      Map<IExpr, IInteger> exponentMap = new TreeMap<IExpr, IInteger>();
      IASTAppendable timesMapped = F.TimesAlloc(times.argSize());
      for (int i = 1; i <= times.argSize(); i++) {
        IExpr arg = times.get(i);
        if (arg.isPower() && arg.base().isInteger() && arg.base().isPositive()) {
          IExpr exponent = arg.exponent();
          IInteger value = exponentMap.get(exponent);
          if (value != null) {
            evaled = true;
            value = value.multiply((IInteger) arg.base());
          } else {
            value = (IInteger) arg.base();
          }
          exponentMap.put(exponent, value);
        } else {
          timesMapped.append(arg);
        }
      }
      if (evaled) {
        for (Map.Entry<IExpr, IInteger> entry : exponentMap.entrySet()) {
          timesMapped.append(F.Power(entry.getValue(), entry.getKey()));
        }
        timesMapped.sortInplace();
        return timesMapped.oneIdentity1();
      }
    }
    return F.NIL;
  }

  /**
   * Decompose <code>base ^ timesExponent</code> into a kernel and an integer power of the kernel:
   * <ul>
   * <li><code>E^(3*x)</code> gives the kernel <code>E^x</code> and the power <code>3</code></li>
   * <li><code>E^(2*I*x)</code> gives the kernel <code>E^(I*x)</code> and the power
   * <code>2</code></li>
   * <li>a negative multiplier is only split off a numeric base: <code>2^(-3*x)</code> gives
   * <code>2^x</code> and <code>-3</code>; <code>E^(-I*x)</code> gives <code>E^(I*x)</code> and
   * <code>-1</code></li>
   * </ul>
   * Both phases of the forward substitution use this method, so that they always agree on the
   * kernel.
   *
   * @return <code>null</code> if the power has to be substituted as a whole
   */
  private static PowerKernel splitTimesExponent(final IExpr base, final IAST timesExponent) {
    IExpr first = timesExponent.first();
    if (first.isComplex() && ((IComplex) first).reRational().isZero()) {
      int n = ((IComplex) first).imRational().toIntDefault();
      if (F.isPresent(n) && Math.abs(n) <= MAX_KERNEL_MULTIPLIER
          && (n > 0 || (n < 0 && base.isNumericFunction()))) {
        return new PowerKernel(base.power(timesExponent.setAtCopy(1, F.CI)), n);
      }
      return null;
    }
    int n = first.toIntDefault();
    if (F.isPresent(n) && Math.abs(n) <= MAX_KERNEL_MULTIPLIER
        && (n > 0 || (n < 0 && base.isNumericFunction()))) {
      return new PowerKernel(base.power(timesExponent.rest().oneIdentity1()), n);
    }
    return null;
  }

  /**
   * Split <code>base ^ (r + rest)</code> with a rational number <code>r</code> into
   * <code>base^r</code> at index 0 and <code>base^rest</code> at index 1. Both phases of the
   * forward substitution use this method, so that they always agree on the two factors.
   */
  private static IExpr[] splitPlusExponent(final IExpr base, final IAST plusExponent) {
    return new IExpr[] {//
        S.Power.of(base, plusExponent.first()),
        S.Power.of(base, plusExponent.rest().oneIdentity0())};
  }

  /**
   * The analysis phase of the forward substitution. It registers every substitutable
   * sub-expression in {@link #substitutedExpr} / {@link #substitutedVariables}.
   * <p>
   * Homogenization (PRESS 3.5) replaces a non-polynomial sub-expression <code>u</code> with a fresh
   * dummy variable <code>t</code>. If <code>u</code> appears under fractional exponents - e.g.
   * <code>u^(1/2)</code> and <code>u^(1/3)</code> - the substitution <code>t = u^(1/k)</code>, with
   * <code>k</code> the least common multiple of all denominators, turns every occurrence into an
   * integer power of <code>t</code>. Therefore the expressions are walked twice: the first pass
   * (<code>fractionalOnly</code>) only determines <code>k</code> for every base, the second pass
   * creates the substitution symbols.
   * <ul>
   * <li><b>Plus / Times</b> - recurse into every argument.</li>
   * <li><b>Power with a rational exponent</b> - the base is a kernel. A sum under an integer
   * exponent is no kernel, unless it also occurs under a fractional exponent:
   * <code>(1+x)^2</code> is walked, but <code>(1+x)^2+Sqrt(1+x)</code> gives
   * <code>t = Sqrt(1+x)</code>.</li>
   * <li><b>Power with a Times exponent</b> - see {@link #splitTimesExponent(IExpr, IAST)}.</li>
   * <li><b>Power with a Plus exponent <code>r + g(x)</code></b> (rational <code>r</code>) - see
   * {@link #splitPlusExponent(IExpr, IAST)}.</li>
   * <li><b>Any other AST or Symbol</b> - a kernel.</li>
   * </ul>
   *
   * @param expression the node of the expression tree to analyse
   * @param fractionalOnly the first pass
   */
  private void collect(final IExpr expression, final boolean fractionalOnly) {
    if (expression.isAST()) {
      final IAST ast = (IAST) expression;
      if (ast.isPlus() || ast.isTimes()) {
        for (int i = 1; i <= ast.argSize(); i++) {
          collect(ast.get(i), fractionalOnly);
        }
        return;
      }
      if (ast.isPower()) {
        final IExpr base = ast.base();
        final IExpr exp = ast.exponent();
        if (exp.isReal()) {
          IRational rat = ((IReal) exp).rationalFactor();
          if (rat == null) {
            register(ast, fractionalOnly);
          } else if (!rat.isInteger()) {
            if (fractionalOnly && (base.isAST() || base.isSymbol())) {
              baseDenominatorLCM.merge(base, rat.denominator(), IInteger::lcm);
            }
            register(base, fractionalOnly);
          } else if (isWalkedSum(base, rat) && !baseDenominatorLCM.containsKey(base)
              && !substitutedExpr.containsKey(base)) {
            collect(base, fractionalOnly);
          } else {
            register(base, fractionalOnly);
          }
          return;
        }
        if (exp.isTimes()) {
          PowerKernel kernel = splitTimesExponent(base, (IAST) exp);
          register(kernel == null ? ast : kernel.kernel, fractionalOnly);
          return;
        }
        if (exp.isPlus() && exp.first().isRational()) {
          // base^(2 + n*x) -> base^2 * base^(n*x)
          // base^(1/2 + n*x) -> base^(1/2) * base^(n*x)
          IExpr[] parts = splitPlusExponent(base, (IAST) exp);
          collect(parts[0], fractionalOnly);
          collect(parts[1], fractionalOnly);
          return;
        }
      }
      register(expression, fractionalOnly);
      return;
    }
    if (expression.isSymbol()) {
      register(expression, fractionalOnly);
    }
  }

  private static boolean isWalkedSum(final IExpr base, final IRational integerExponent) {
    if (base.isPlus()) {
      int n = integerExponent.toIntDefault();
      return F.isPresent(n) && Math.abs(n) <= MAX_WALKED_EXPONENT;
    }
    return false;
  }

  /**
   * Create the substitution symbol for the kernel in the second pass of
   * {@link #collect(IExpr, boolean)}.
   */
  private void register(final IExpr kernel, final boolean fractionalOnly) {
    if (fractionalOnly || !(kernel.isAST() || kernel.isSymbol())) {
      return;
    }
    ISymbol symbol = substitutedExpr.get(kernel);
    if (symbol == null) {
      symbol = F.Dummy(EvalEngine.uniqueName("jas$"));
      substitutedVariables.put(symbol, kernel);
      substitutedExpr.put(kernel, symbol);
    }
    IInteger lcm = baseDenominatorLCM.get(kernel);
    if (lcm != null && !lcm.isOne()) {
      symbolDenominatorLCM.put(symbol, lcm);
    }
  }

  /**
   * The rewrite phase of the forward substitution. It walks the expression exactly like
   * {@link #collect(IExpr, boolean)}.
   *
   * @param expression
   * @return
   */
  private IExpr replaceForwardRecursive(final IExpr expression) {
    if (expression.isAST()) {
      final IAST ast = (IAST) expression;
      if (ast.isPlus() || ast.isTimes()) {
        IASTAppendable newAST = F.ast(ast.head(), ast.argSize());
        for (int i = 1; i <= ast.argSize(); i++) {
          newAST.append(replaceForwardRecursive(ast.get(i)));
        }
        return newAST;
      }
      if (ast.isPower()) {
        final IExpr power = replaceExpression(ast);
        if (power.isPresent()) {
          return power;
        }
        final IExpr base = ast.base();
        final IExpr exp = ast.exponent();
        if (exp.isReal()) {
          IExpr temp = replacePower(base, (IReal) exp);
          if (temp.isPresent()) {
            return temp;
          }
          if (exp.isInteger() && isWalkedSum(base, (IInteger) exp)) {
            return F.Power(replaceForwardRecursive(base), exp);
          }
          return ast;
        }
        if (exp.isTimes()) {
          PowerKernel kernel = splitTimesExponent(base, (IAST) exp);
          if (kernel != null) {
            IExpr temp = replaceExpression(kernel.kernel);
            if (temp.isPresent()) {
              return kernel.power == 1 ? temp : F.Power(temp, F.ZZ(kernel.power));
            }
          }
          return ast;
        }
        if (exp.isPlus() && exp.first().isRational()) {
          IExpr[] parts = splitPlusExponent(base, (IAST) exp);
          return F.Times(replaceForwardRecursive(parts[0]), replaceForwardRecursive(parts[1]));
        }
        return ast;
      }
      return replaceExpression(expression).orElse(expression);
    }
    if (expression.isSymbol()) {
      return replaceExpression(expression).orElse(expression);
    }
    return expression;
  }

  private IExpr replaceExpression(final IExpr exprPoly) {
    ISymbol symbol = substitutedExpr.get(exprPoly);
    if (symbol != null) {
      IInteger lcm = getLCM(symbol);
      if (lcm.isOne()) {
        return symbol;
      }
      return F.Power(symbol, lcm);
    }
    return F.NIL;
  }

  private IExpr replacePower(final IExpr exprPoly, IReal exp) {
    ISymbol symbol = substitutedExpr.get(exprPoly);
    if (symbol != null) {
      IRational rat = exp.rationalFactor();
      if (rat != null) {
        IInteger lcm = getLCM(symbol);
        if (lcm.mod(rat.denominator()).isZero()) {
          IInteger exponent = rat.numerator().multiply(lcm.div(rat.denominator()));
          return exponent.isOne() ? symbol : F.Power(symbol, exponent);
        }
      }
    }
    return F.NIL;
  }

  /**
   * Backward substitution - transforming the expression back by replacing the introduced
   * substitution variables.
   *
   * @param expression
   * @see #replaceForward(IExpr)
   */
  public IExpr replaceBackward(final IExpr expression) {
    return F.subst(expression, x -> {
      if (x.isSymbol()) {
        IExpr t = substitutedVariables.get(x);
        if (t != null) {
          IInteger denominatorLCM = getLCM(x);
          if (denominatorLCM.isOne()) {
            return t;
          }
          return F.Power(t, F.fraction(F.C1, denominatorLCM));
        }
      }
      return F.NIL;
    });
  }

  /**
   * Backward substitution of a single value. The substitution symbol stands for
   * <code>u^(1/k)</code>, where <code>k</code> is the denominator LCM; solve
   * <code>u == resultValue^k</code> for the single variable in <code>u</code>.
   *
   * @param symbol a substitution symbol
   * @param resultValue the value of the substitution symbol
   * @return {@link F#NIL} if <code>u</code> isn't a variable or a unary function of a variable
   */
  public IExpr replaceDenominatorBackwardLCM(final ISymbol symbol, IExpr resultValue) {
    final IExpr t = substitutedVariables.get(symbol);
    if (t != null) {
      final IInteger denominatorLCM = getLCM(symbol);
      final IExpr value =
          denominatorLCM.isOne() ? resultValue : F.Power(resultValue, denominatorLCM);
      if (t.isSymbol()) {
        return value;
      }
      final VariablesSet varSet = new VariablesSet(t);
      if (varSet.size() == 1) {
        IExpr solveVar = varSet.firstVariable();
        if (t.isAST1() && t.head().isSymbol() && t.first().equals(solveVar)) {
          return ReduceVariableEqual.reduce(F.Equal(t, value), solveVar, false);
        }
      }
    }
    return F.NIL;
  }

  /**
   * Variables (ISymbols) which are substituted from the original polynomial (backward substitution)
   * in the order of their creation.
   */
  public Map<ISymbol, IExpr> substitutedVariables() {
    return Collections.unmodifiableMap(substitutedVariables);
  }

  public Set<ISymbol> substitutedVariablesSet() {
    return substitutedVariables.keySet();
  }

  public int size() {
    return substitutedVariables.size();
  }

  /**
   * Return a list of rules containing the backward substitutions, of the dummy variables
   *
   */
  public IASTAppendable listOfBackwardSubstitutions() {
    IASTAppendable list = F.ListAlloc(size());
    for (Map.Entry<ISymbol, IExpr> entry : substitutedVariables.entrySet()) {
      ISymbol key = entry.getKey();
      IInteger denominatorLCM = getLCM(key);
      if (denominatorLCM.isOne()) {
        list.append(F.Rule(key, entry.getValue()));
      } else {
        list.append(F.Rule(key, F.Power(entry.getValue(), F.fraction(F.C1, denominatorLCM))));
      }
    }
    return list;
  }

  /**
   * Normalize change-of-base log forms within a Times expression:
   * <ul>
   * <li>{@code Log(v) * Log(c)^(-1)  ->  Log(c, v)} (i.e. log_c(v))</li>
   * <li>{@code Log(c) * Log(v)^(-1)  ->  Log(c, v)^(-1)} (i.e. 1/log_c(v))</li>
   * </ul>
   * where {@code c} is a numeric constant and {@code v} depends on the variables. All matching
   * pairs are rewritten in a single pass.
   *
   * @param expr a node from the expression tree
   * @return the rewritten node, or {@link F#NIL} if no rewrite applied
   */
  private static IExpr normalizeLogBase(IExpr expr) {
    if (!expr.isTimes()) {
      return F.NIL;
    }
    final IAST timesAST = (IAST) expr;

    // Collect 1-based indices for each category
    List<Integer> varLogIdxs = new ArrayList<>(); // Log(v), v non-numeric
    List<Integer> constLogInvIdxs = new ArrayList<>(); // Log(c)^(-1), c numeric
    List<Integer> constLogIdxs = new ArrayList<>(); // Log(c), c numeric
    List<Integer> varLogInvIdxs = new ArrayList<>(); // Log(v)^(-1), v non-numeric

    for (int i = 1; i <= timesAST.argSize(); i++) {
      IExpr arg = timesAST.get(i);
      if (arg.isAST(S.Log, 2)) {
        if (arg.first().isNumericFunction()) {
          constLogIdxs.add(i);
        } else {
          varLogIdxs.add(i);
        }
      } else if (arg.isPower() && arg.exponent().isMinusOne() && arg.base().isAST(S.Log, 2)) {
        if (arg.base().first().isNumericFunction()) {
          constLogInvIdxs.add(i);
        } else {
          varLogInvIdxs.add(i);
        }
      }
    }

    // How many pairs of each kind can be formed
    int pairCount1 = Math.min(varLogIdxs.size(), constLogInvIdxs.size()); // -> Log(c, v)
    int pairCount2 = Math.min(constLogIdxs.size(), varLogInvIdxs.size()); // -> Log(c, v)^(-1)

    if (pairCount1 == 0 && pairCount2 == 0) {
      return F.NIL;
    }

    // Mark all consumed positions
    Set<Integer> consumed = new HashSet<>();
    for (int k = 0; k < pairCount1; k++) {
      consumed.add(varLogIdxs.get(k));
      consumed.add(constLogInvIdxs.get(k));
    }
    for (int k = 0; k < pairCount2; k++) {
      consumed.add(constLogIdxs.get(k));
      consumed.add(varLogInvIdxs.get(k));
    }

    // Build new Times: unpaired factors first, then rewritten log pairs
    int newSize = timesAST.argSize() - consumed.size() + pairCount1 + pairCount2;
    IASTAppendable newTimes = F.TimesAlloc(newSize);

    for (int i = 1; i <= timesAST.argSize(); i++) {
      if (!consumed.contains(i)) {
        newTimes.append(timesAST.get(i));
      }
    }

    // Log(v) * Log(c)^(-1) -> Log(c, v)
    for (int k = 0; k < pairCount1; k++) {
      IExpr varExpr = timesAST.get(varLogIdxs.get(k)).first();
      IExpr constExpr = timesAST.get(constLogInvIdxs.get(k)).base().first();
      newTimes.append(F.Log(constExpr, varExpr));
    }

    // Log(c) * Log(v)^(-1) -> Log(c, v)^(-1)
    for (int k = 0; k < pairCount2; k++) {
      IExpr constExpr = timesAST.get(constLogIdxs.get(k)).first();
      IExpr varExpr = timesAST.get(varLogInvIdxs.get(k)).base().first();
      newTimes.append(F.Power(F.Log(constExpr, varExpr), F.CN1));
    }

    return newTimes.oneIdentity1();
  }

}
