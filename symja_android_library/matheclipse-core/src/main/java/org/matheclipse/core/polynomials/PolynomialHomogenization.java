package org.matheclipse.core.polynomials;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ReduceVariableEqual;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
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
  class TanhSechTransform implements Function<IExpr, IExpr> {
    @Override
    public IExpr apply(IExpr x) {
      // Rewrite Sech(u)^(2*n) -> (1 - Tanh(u)^2)^n
      // Rewrite Csch(u)^(2*n) -> (Coth(u)^2 - 1)^n
      if (x.isPower()) {
        IExpr base = x.base();
        IExpr exp = x.exponent();
        if (base.isAST(S.Sech, 2)) {
          int exponent = exp.toIntDefault();
          if (exponent > 0 && (exponent % 2) == 0) {
            IExpr arg = base.first();
            IExpr oneMinusTanh2 = F.Plus(F.C1, F.Negate(F.Power(F.Tanh(arg), F.C2)));
            if (exponent == 2) {
              return oneMinusTanh2;
            }
            return F.Power(oneMinusTanh2, F.ZZ(exponent / 2));
          }
        } else if (base.isAST(S.Csch, 2)) {
          int exponent = exp.toIntDefault();
          if (exponent > 0 && (exponent % 2) == 0) {
            IExpr arg = base.first();
            IExpr coth2MinusOne = F.Plus(F.Power(F.Coth(arg), F.C2), F.CN1);
            if (exponent == 2) {
              return coth2MinusOne;
            }
            return F.Power(coth2MinusOne, F.ZZ(exponent / 2));
          }
        }
      }
      return F.NIL;
    }
  }

  class CosSinTransform {
    private static final int DECISION = 0;
    private static final int SIN_ODD = 1;
    private static final int SIN_EVEN = 2;
    private static final int COS_ODD = 3;
    private static final int COS_EVEN = 4;

    private final Map<IExpr, int[]> statsMap;

    public CosSinTransform() {
      statsMap = new HashMap<>();
    }

    private int[] getStats(IExpr arg) {
      return statsMap.computeIfAbsent(arg, k -> new int[5]);
    }

    /**
     * If decision is 1 rewrite to Sin expression. If decision is -1 rewrite to Cos expression.
     */
    private void decideTransform() {
      statsMap.forEach((x, a) -> {
        a[DECISION] = 1;
        if (a[SIN_ODD] == 0) {
          if (a[COS_ODD] != 0) {
            a[DECISION] = -1;
          }
        }
      });
    }

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
            // Skip power base recursion to prevent duplicate odd/even counting
            // over the same trig node. Recurse directly into arguments.
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

      IAST ast = (IAST) expr;
      for (int i = 1; i <= ast.argSize(); i++) {
        determineStatsRecursive(ast.get(i));
      }
    }

    /**
     * Add statistics entry for base ^ exponent * @param base
     * 
     * @param exponent
     */
    private void addStatistics(IAST base, int exponent) {
      if (base.isCos()) {
        int[] stats = getStats(base.arg1());
        if ((exponent % 2) == 0) {
          stats[COS_EVEN]++;
        } else {
          stats[COS_ODD]++;
        }
      } else if (base.isSin()) {
        int[] stats = getStats(base.arg1());
        if ((exponent % 2) == 0) {
          stats[SIN_EVEN]++;
        } else {
          stats[SIN_ODD]++;
        }
      }
    }

    private IExpr rewriteEvenCosSinFunctions(IExpr x) {
      if (x.isPowerInteger() && x.base().isAST1()) {
        IAST baseAST = (IAST) x.base();
        int[] array = statsMap.get(baseAST.arg1());
        if (array != null) {
          boolean rewriteToSin = array[DECISION] > 0;
          int exponent = x.exponent().toIntDefault();
          if (F.isPresent(exponent) && (exponent % 2) == 0) {
            if (rewriteToSin && baseAST.isCos()) {
              IExpr cosArg = baseAST.arg1();
              IExpr oneMinusSin2 = F.Plus(F.C1, F.Negate(F.Power(F.Sin(cosArg), F.C2)));
              if (exponent > 2) {
                return F.Power(oneMinusSin2, F.ZZ(exponent / 2));
              }
              return oneMinusSin2;
            }
            if (!rewriteToSin && baseAST.isSin()) {
              IExpr sinArg = baseAST.arg1();
              IExpr oneMinusCos2 = F.Plus(F.C1, F.Negate(F.Power(F.Cos(sinArg), F.C2)));
              if (exponent > 2) {
                return F.Power(oneMinusCos2, F.ZZ(exponent / 2));
              }
              return oneMinusCos2;
            }
          }
        }
      }
      return F.NIL;
    }

    public IExpr applyGlobal(IExpr x) {
      IExpr trigExpand = F.TrigExpand.of(x);
      if (trigExpand.isNIL()) {
        trigExpand = x;
      }
      determineStatsRecursive(trigExpand);
      decideTransform();
      return F.subst(trigExpand, f -> rewriteEvenCosSinFunctions(f));
    }
  }

  /**
   * Variables ({@link ISymbol}s) which are substituted from the original polynomial (backward
   * substitution). Uses {@link LinkedHashMap} to preserve insertion order, which is important for
   * consistent variable naming in forward substitution (e.g. t1, t2, t3, ...). The order of
   * variable registration is determined by the depth-first traversal of the expression tree in
   * {@link #collectSubstitutionLCMs(IExpr)}, which ensures that sub-expressions closer to the root
   * of the tree get registered first and thus receive "earlier" substitution variables (e.g. t1).
   * 
   */
  private java.util.Map<ISymbol, IExpr> substitutedVariables = new LinkedHashMap<ISymbol, IExpr>();

  /**
   * Intermediate accumulator used during the analysis phase of forward substitution.
   *
   * <p>
   * Maps each dummy substitution symbol (introduced by {@link #replaceExpressionLCM}) to an
   * appendable {@code LCM[d1, d2, ...]} AST that collects every rational-exponent denominator
   * encountered for that symbol's base expression during {@link #collectSubstitutionLCMs(IExpr)}.
   *
   * <p>
   * For example, if the base {@code x} appears as both {@code x^(1/2)} and {@code x^(1/3)} in the
   * expression, the dummy symbol for {@code x} accumulates {@code LCM[2, 3]} here. Once collection
   * is complete, {@link #replaceForward(IExpr)} evaluates each pending AST, obtains the integer
   * result (here {@code 6}), and stores it in {@link #symbolDenominatorLCM}. After that point this
   * map is no longer used.
   *
   * <p>
   * Lazily initialized — {@code null} until the first fractional exponent is encountered. Uses an
   * {@link IdentityHashMap} because the keys are unique dummy {@link ISymbol} instances compared by
   * identity.
   *
   * @see #symbolDenominatorLCM
   * @see #collectSubstitutionLCMs(IExpr)
   * @see #replaceExpressionLCM(IExpr, IInteger)
   */
  private java.util.Map<ISymbol, IASTAppendable> symbolLCMAccumulators = null;

  /**
   * Final, evaluated LCM of rational-exponent denominators for each dummy substitution symbol.
   *
   * <p>
   * Maps each dummy symbol {@code t} to the integer {@code k} such that the substitution
   * {@code t = u^(1/k)} eliminates all fractional exponents on the original base {@code u}. Only
   * symbols whose LCM is strictly greater than {@code 1} are stored; for all others
   * {@link #getLCM(IExpr)} falls back to returning {@link F#C1}.
   *
   * <p>
   * This map is populated by {@link #replaceForward(IExpr)} immediately after
   * {@link #collectSubstitutionLCMs(IExpr)} finishes, by evaluating every pending
   * {@code LCM[d1, d2, ...]} AST stored in {@link #symbolLCMAccumulators}. It is then consumed by:
   * <ul>
   * <li>{@link #replaceForwardRecursive(IExpr)} — via {@link #replacePower} and
   * {@link #replaceExpression}, to raise the substitution variable to the correct integer power
   * ({@code rat * k}).</li>
   * <li>{@link #replaceBackward(IExpr)} — to invert the substitution by restoring
   * {@code u = t^(1/k)}.</li>
   * <li>{@link #listOfBackwardSubstitutions()} — to emit the backward-substitution rules.</li>
   * </ul>
   *
   * <p>
   * Lazily initialized — {@code null} until at least one fractional exponent with denominator
   * {@literal >} 1 is found. Uses an {@link IdentityHashMap} because the keys are unique dummy
   * {@link ISymbol} instances compared by identity.
   *
   * @see #symbolLCMAccumulators
   * @see #getLCM(IExpr)
   * @see #replaceForward(IExpr)
   */
  private java.util.Map<ISymbol, IInteger> symbolDenominatorLCM = null;

  /**
   * If <code>true</code> - the forward substitution phase should also factor trigonometric
   * functions to increase the chance of successful polynomial factorization.
   */
  private boolean isFactorTrigOption = false;

  /**
   * Expressions which are substituted with variables(ISymbol) from the original polynomial (forward
   * substitution).
   */
  private java.util.HashMap<IExpr, ISymbol> substitutedExpr = new HashMap<IExpr, ISymbol>();

  final private EvalEngine engine;

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


  public PolynomialHomogenization(EvalEngine engine, boolean isFactorTrigOption) {
    this.engine = engine;
    this.isFactorTrigOption = isFactorTrigOption;
  }

  /**
   * Lazy initialization for map symbol -> list-of-least-common-multiple-factors.
   *
   * @return
   */
  private Map<ISymbol, IASTAppendable> getSymbol2IntegerAST() {
    if (symbolLCMAccumulators == null) {
      symbolLCMAccumulators = new IdentityHashMap<ISymbol, IASTAppendable>();
    }
    return symbolLCMAccumulators;
  }

  /**
   * Lazy initialization for map symbol -> least-common-multiple-factors.
   *
   * @return
   */
  private Map<ISymbol, IInteger> getSymbol2LCM() {
    if (symbolDenominatorLCM == null) {
      symbolDenominatorLCM = new IdentityHashMap<ISymbol, IInteger>();
    }
    return symbolDenominatorLCM;
  }

  /**
   * Determine the least-common-multiple-factor associated with a symbol.
   *
   * @param x
   */
  public IInteger getLCM(IExpr x) {
    if (symbolDenominatorLCM == null) {
      return F.C1;
    }
    IInteger i = symbolDenominatorLCM.get(x);
    if (i == null) {
      return F.C1;
    }
    return i;
  }

  /**
   * Forward substitution - transforming the expression into a polynomial expression by introducing
   * substitution variables. After transforming the polynomial expression may be solvable by a
   * polynomial factorization.
   *
   * @param expression
   * @return the polynomial expression
   */
  public IExpr replaceForward(final IExpr expression) {
    IExpr expr;
    if (expression.isFree(x -> x.isTrigFunction(), false)) {
      expr = F.subst(expression, PolynomialHomogenization::unifyIntegerPowers);
      // Normalize Log(v)/Log(c) -> Log(c,v) so Log-based homogenization works
      // even when Symja auto-evaluates Log(c,v) to Log(v)/Log(c) beforehand.
      expr = F.subst(expr, this::normalizeLogBase);
    } else {
      if (!expression.isFree(x -> x.isCos() || x.isSin(), false)) {
        CosSinTransform transform = new CosSinTransform();
        expr = transform.applyGlobal(expression);
        expr = F.evalExpandAll(expr, engine);
      } else if (!expression.isFree(x -> x.isAST(S.Sech, 2) || x.isAST(S.Csch, 2), false)) {
        expr = F.subst(expression, new TanhSechTransform());
        expr = F.evalExpandAll(expr, engine);
      } else {
        expr = expression;
      }
    }
    collectSubstitutionLCMs(expr);
    if (symbolLCMAccumulators != null) {
      for (Map.Entry<ISymbol, IASTAppendable> entry : symbolLCMAccumulators.entrySet()) {
        IASTAppendable denominatorLCMAST = entry.getValue();
        IInteger denominatorLCM = F.C1;
        if (denominatorLCMAST.isAST0()) {
        } else if (denominatorLCMAST.isAST1()) {
          denominatorLCM = (IInteger) denominatorLCMAST.arg1();
        } else {
          denominatorLCM = (IInteger) engine.evaluate(denominatorLCMAST);
        }
        if (!denominatorLCM.isOne()) {
          getSymbol2LCM().put(entry.getKey(), denominatorLCM);
        }
      }
    }
    return replaceForwardRecursive(expr);
  }

  /**
   * Unify powers in two steps like 3^(2+2*x) to 3^2 * 3^(2*x). Merge powers like 2^(2*x)*3^(2*x) to
   * 6^(2*x) * @param x
   * 
   * @return
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

            if (exp.isPlus() && exp.first().isInteger() && exp.first().isPositive()) {
              evaled = true;
              IExpr rest = exp.rest().oneIdentity1();
              times.append(base.pow(exp.first()));
              times.append(F.Power(base, rest));
              continue;
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
          IExpr key = entry.getKey();
          IInteger val = entry.getValue();
          timesMapped.append(F.Power(val, key));
        }
        timesMapped.sortInplace();
        return timesMapped.oneIdentity1();
      }
    }
    return F.NIL;
  }

  /**
   * Forward substitution - transforming the numerator and denominator expression into polynomial
   * expressions by introducing substitution variables. After transforming the polynomial expression
   * may be solvable by a polynomial factorization.
   *
   * @param numerator
   * @param denominator
   * @return polynomial numerator at index '0'; and polynomial denominator at index '1'
   */
  public IExpr[] replaceForward(final IExpr numerator, final IExpr denominator) {
    collectSubstitutionLCMs(numerator);
    collectSubstitutionLCMs(denominator);
    if (symbolLCMAccumulators != null) {
      for (Map.Entry<ISymbol, IASTAppendable> entry : symbolLCMAccumulators.entrySet()) {
        IASTAppendable denominatorLCMAST = entry.getValue();
        IInteger denominatorLCM = F.C1;
        if (denominatorLCMAST.isAST0()) {
        } else if (denominatorLCMAST.isAST1()) {
          denominatorLCM = (IInteger) denominatorLCMAST.arg1();
        } else {
          denominatorLCM = (IInteger) engine.evaluate(denominatorLCMAST);
        }
        if (!denominatorLCM.isOne()) {
          getSymbol2LCM().put(entry.getKey(), denominatorLCM);
        }
      }
    }

    IExpr[] result = new IExpr[2];
    result[0] = replaceForwardRecursive(numerator);
    result[1] = replaceForwardRecursive(denominator);
    return result;
  }

  /**
   * Recursively walks {@code expression} and registers every substitutable sub-expression in
   * {@link #substitutedExpr} / {@link #substitutedVariables}, accumulating the
   * <em>least-common-multiple of all rational-exponent denominators</em> for each base in
   * {@link #symbolLCMAccumulators}.
   *
   * <h3>Purpose</h3> Homogenization (PRESS §3.5) replaces a non-polynomial sub-expression {@code u}
   * with a fresh dummy variable {@code t}, so that the equation becomes a polynomial in {@code t}.
   * When {@code u} appears under fractional exponents — e.g. both {@code u^(1/2)} and
   * {@code u^(3/2)} in the same equation — the substitution {@code t = u^(1/k)} (where {@code k} is
   * the LCM of all denominators) turns every occurrence into an integer power of {@code t}, making
   * the result a true polynomial.
   *
   * <p>
   * This method is the <em>analysis phase</em>: it determines the required {@code k} for every base
   * before {@link #replaceForwardRecursive(IExpr)} performs the actual rewrite.
   *
   * <h3>Traversal rules</h3>
   * <ul>
   * <li><b>Plus / Times</b> — recurse into every argument; the LCM is the LCM of all constituent
   * bases.</li>
   * <li><b>Power with a real (rational) exponent</b> — extract the denominator {@code d} of the
   * rational part; register the base with {@code lcm = d}. For an integer exponent {@code d = 1},
   * so the base is registered without changing the running LCM.</li>
   * <li><b>Power with a Times exponent {@code n*f(x)}</b> — delegated to
   * {@link #determineTimes(IAST, IExpr, IAST)}, which handles pure-imaginary leading coefficients
   * and positive integer multipliers.</li>
   * <li><b>Power with a Plus exponent {@code r + g(x)}} (rational {@code r})</b> — split as
   * {@code base^r * base^g(x)} and recurse into both halves independently. This ensures the
   * denominator of {@code r} contributes to the LCM even when the exponent is not purely
   * rational.</li>
   * <li><b>Any other AST or Symbol</b> — treated as an atomic substitutable unit; registered with
   * {@code lcm = 1} (no fractional contribution).</li>
   * </ul>
   *
   * <h3>Side effects</h3>
   * <ul>
   * <li>New dummy symbols are added to {@link #substitutedExpr} and {@link #substitutedVariables}
   * (via {@link #replaceExpressionLCM}).</li>
   * <li>For each base whose LCM denominator is {@literal >} 1, a pending {@code LCM(...)} AST is
   * accumulated in {@link #symbolLCMAccumulators}. The caller ({@link #replaceForward(IExpr)})
   * evaluates that AST and stores the final integer in {@link #symbolDenominatorLCM} before
   * invoking {@link #replaceForwardRecursive(IExpr)}.</li>
   * </ul>
   *
   * <h3>Example</h3> Given {@code x^(1/2) + x^(3/2)}:
   * <ol>
   * <li>Visit {@code x^(1/2)}: base = {@code x}, denominator = 2 → register {@code x} with LCM
   * contribution 2.</li>
   * <li>Visit {@code x^(3/2)}: base = {@code x}, denominator = 2 → add another LCM contribution 2.
   * Final LCM = {@code LCM(2,2) = 2}.</li>
   * <li>{@link #replaceForwardRecursive} then introduces {@code t = x^(1/2)}, rewriting the
   * expression as {@code t + t^3}.</li>
   * </ol>
   *
   * @param expression the node of the expression tree to analyse; must not be {@code null}
   */
  private void collectSubstitutionLCMs(final IExpr expression) {
    if (expression instanceof IAST) {
      final IAST ast = (IAST) expression;
      if (ast.isPlus() || ast.isTimes()) {
        for (int i = 1; i <= ast.argSize(); i++) {
          collectSubstitutionLCMs(ast.get(i));
        }
        return;
      } else if (ast.isPower()) {
        IExpr exp = ast.exponent();
        IExpr base = ast.base();
        if (exp.isReal()) {
          IInteger lcm = F.C1;
          IRational rat = ((IReal) exp).rationalFactor();
          if (rat == null) {
            return;
          }
          if (!rat.isInteger()) {
            IInteger denominator = rat.denominator();
            if (denominator.isNegative()) {
              denominator = denominator.negate();
            }
            lcm = denominator;
          }
          replaceExpressionLCM(base, lcm);
          return;
        }
        if (exp.isTimes()) {
          determineTimes(ast, base, (IAST) exp);
          return;
        } else if (exp.isPlus()) {
          IAST plusAST = (IAST) exp;
          // Handle both integer and rational leading coefficients, e.g.:
          // base^(2 + n*x) -> base^2 * base^(n*x)
          // base^(1/2 + n*x) -> base^(1/2) * base^(n*x)
          // The rational part recurses into the exp.isReal() branch above,
          // which correctly extracts the denominator for the LCM.
          if (plusAST.first().isRational()) {
            collectSubstitutionLCMs(S.Power.of(base, plusAST.first()));
            collectSubstitutionLCMs(S.Power.of(base, plusAST.rest().oneIdentity0()));
            return;
          }
        }
        replaceExpressionLCM(ast, F.C1);
        return;
      }
      replaceExpressionLCM(expression, F.C1);
      return;
    }
    if (expression instanceof ISymbol) {
      replaceExpressionLCM(expression, F.C1);
    }
  }



  /**
   * Analyses a {@code Power} node whose exponent is a {@code Times} expression and registers the
   * appropriate substitutable base unit in the LCM accumulation maps.
   *
   * <p>
   * This method is called exclusively from {@link #collectSubstitutionLCMs(IExpr)} when it
   * encounters {@code base ^ timesExponent} and {@code timesExponent} is a product (i.e.
   * {@code timesExponent = first * rest}). The goal is to identify the <em>kernel</em>
   * sub-expression — the part that will become the substitution variable's base — and register it
   * via {@link #replaceExpressionLCM(IExpr, IInteger)} with {@code lcm = 1}, because a
   * {@code Times} exponent never introduces a fractional denominator by itself.
   *
   * <h3>Case 1 — Pure-imaginary leading factor: {@code base ^ (I*n * rest)}</h3>
   * <p>
   * When the first factor of {@code timesExponent} is a complex number with zero real part (e.g.
   * {@code 2*I}), the imaginary part is extracted as a rational and converted to an integer
   * {@code n}:
   * <ul>
   * <li>If {@code n} is not representable as a Java {@code int}, or is not positive, the whole
   * power {@code ast} is treated as an opaque atomic unit (registered as-is).</li>
   * <li>If {@code n > 0}, the leading factor is replaced by {@code I} alone, yielding
   * {@code restExponent = I * rest}. The kernel {@code base ^ restExponent} is registered. The
   * integer {@code n} will later become the outer integer exponent of the substitution variable in
   * {@link #replaceTimes(IAST, IExpr, IExpr)}.</li>
   * </ul>
   *
   * <h3>Case 2 — Positive-integer leading factor: {@code base ^ (n * rest)}</h3>
   * <p>
   * When the first factor is a plain positive integer {@code n}, or when {@code base} is a numeric
   * function (making the sign irrelevant):
   * <ul>
   * <li>If {@code n} is not representable as a Java {@code int}, the whole power {@code ast} is
   * registered as an opaque atom.</li>
   * <li>If {@code n > 0} (or {@code base.isNumericFunction()}), the remaining factors
   * {@code rest = timesExponent.rest()} form the variable part of the exponent, and the kernel
   * {@code base ^ rest} is registered. The integer {@code n} will later become the outer integer
   * exponent of the substitution variable.</li>
   * <li>Otherwise (non-positive, non-numeric) the whole power {@code ast} is registered as an
   * atom.</li>
   * </ul>
   *
   * <h3>Example — integer multiplier</h3>
   * 
   * <pre>
   *   expression : E ^ (3 * x)
   *   base       : E
   *   timesExponent : Times[3, x]   first=3, rest=x
   *   -> kernel  : E^x  registered with lcm=1
   *   -> replaceForwardRecursive will later produce  t^3  where  t = E^x
   * </pre>
   *
   * <h3>Example — pure-imaginary multiplier</h3>
   * 
   * <pre>
   *   expression : E ^ (2*I * x)
   *   base       : E
   *   timesExponent : Times[2*I, x]   first=2*I (imPart=2), rest=I*x
   *   -> kernel  : E^(I*x)  registered with lcm=1
   *   -> replaceForwardRecursive will later produce  t^2  where  t = E^(I*x)
   * </pre>
   *
   * @param ast the full {@code Power[base, timesExponent]} node, used as a fallback atom when no
   *        structured decomposition is possible
   * @param base the base of the power expression ({@code ast.base()})
   * @param timesExponent the exponent, which must satisfy {@code timesExponent.isTimes() == true}
   *        ({@code ast.exponent()} cast to {@link IAST})
   */
  private void determineTimes(final IAST ast, final IExpr base, IAST timesExponent) {
    IExpr first = timesExponent.first();
    if (first.isComplex() && ((IComplex) first).reRational().isZero()) {
      IRational pureImPart = ((IComplex) first).imRational();
      int exponent = pureImPart.toIntDefault();
      if (F.isNotPresent(exponent)) {
        replaceExpressionLCM(ast, F.C1);
        return;
      } else if (exponent > 0) {
        IASTMutable restExponent = timesExponent.setAtCopy(1, F.CI);
        replaceExpressionLCM(base.power(restExponent), F.C1);
        return;
      }
      replaceExpressionLCM(ast, F.C1);
      return;
    }
    int exponent = first.toIntDefault();
    if (F.isNotPresent(exponent)) {
      replaceExpressionLCM(ast, F.C1);
      return;
    } else if (exponent > 0 || base.isNumericFunction()) {
      IExpr rest = timesExponent.rest().oneIdentity1();
      replaceExpressionLCM(base.power(rest), F.C1);
      return;
    }
    replaceExpressionLCM(ast, F.C1);
  }

  /**
   * Forward substitution - transforming the expression into a polynomial expression by introducing
   * substitution variables. After transforming the polynomial expression may be solvable by a
   * polynomial factorization.
   *
   * @param expression
   * @return
   * @throws ArithmeticException
   * @throws ClassCastException
   */
  private IExpr replaceForwardRecursive(final IExpr expression)
      throws ArithmeticException, ClassCastException {
    if (expression instanceof IAST) {
      final IAST ast = (IAST) expression;
      if (ast.isPlus() || ast.isTimes()) {
        IASTAppendable newAST = F.ast(ast.head(), ast.argSize() + 1);
        IExpr temp = replaceForwardRecursive(ast.arg1());
        if (temp.isNIL()) {
          return F.NIL;
        }
        newAST.append(temp);
        for (int i = 2; i <= ast.argSize(); i++) {
          temp = replaceForwardRecursive(ast.get(i));
          if (temp.isNIL()) {
            return F.NIL;
          }
          newAST.append(temp);
        }
        return newAST;
      } else if (ast.isPower()) {
        final IExpr power = replaceExpression(ast);
        if (power.isPresent()) {
          return power;
        }
        final IExpr b = ast.base();
        IExpr exp = ast.exponent();
        if (exp.isReal()) {
          IExpr base = replacePower(b, (IReal) exp);
          if (base.isPresent()) {
            return base;
          }
        }
        IExpr base = b;

        if (exp.isTimes()) {
          return replaceTimes(ast, base, exp);
        } else if (exp.isPlus()) {
          IAST plusAST = (IAST) exp;
          if (plusAST.first().isRational()) { // <-- same change here
            IExpr coefficient = S.Power.of(base, plusAST.first());
            IExpr a1 = replaceForwardRecursive(coefficient);
            if (a1.isNIL()) {
              return F.NIL;
            }
            IExpr a2 = replaceForwardRecursive(F.Power(base, plusAST.rest().oneIdentity0()));
            if (a2.isNIL()) {
              return F.NIL;
            }
            return F.Times(a1, a2);
          }
        }

        return ast;
      }
      return replaceExpression(expression);
    }
    if (expression.isSymbol()) {
      return replaceExpression(expression).orElse(expression);
    }
    return expression;
  }

  private IExpr replaceTimes(final IAST ast, final IExpr base, IExpr exp) {
    IExpr first = exp.first();
    if (first.isComplex() && ((IComplex) first).reRational().isZero()) {
      IRational imPart = ((IComplex) first).imRational();
      int exponent = imPart.toIntDefault();
      if (F.isNotPresent(exponent)) {
        return replaceExpression(ast).orElse(ast);
      } else if (exponent > 0) {
        IASTMutable restExponent = ((IAST) exp).setAtCopy(1, F.CI);
        return F.Power(replaceExpression(base.power(restExponent)), exponent);
      }
      return replaceExpression(ast);
    }
    int exponent = first.toIntDefault();
    if (F.isNotPresent(exponent)) {
      return replaceExpression(ast);
    }
    if (exponent > 0 || base.isNumericFunction()) {
      final IExpr rest = exp.rest().oneIdentity1();
      return F.Power(replaceExpression(base.power(rest)), exponent);
    }
    return replaceExpression(ast).orElse(ast);
  }

  private IExpr replaceExpressionLCM(final IExpr exprPoly, IInteger lcm) {
    if (exprPoly.isAST() || exprPoly.isSymbol()) {
      ISymbol newSymbol = substitutedExpr.get(exprPoly);
      if (newSymbol != null) {
        if (!lcm.isOne()) {
          IASTAppendable ast = getSymbol2IntegerAST().get(newSymbol);
          if (ast == null) {
            IASTAppendable list = F.ast(S.LCM);
            list.append(lcm);
            getSymbol2IntegerAST().put(newSymbol, list);
          } else {
            ast.append(lcm);
          }
        }
        return newSymbol;
      }
      newSymbol = F.Dummy(EvalEngine.uniqueName("jas$"));
      substitutedVariables.put(newSymbol, exprPoly);
      substitutedExpr.put(exprPoly, newSymbol);

      if (!lcm.isOne()) {
        IASTAppendable list = F.ast(S.LCM);
        list.append(lcm);
        getSymbol2IntegerAST().put(newSymbol, list);
      }

      return newSymbol;
    }
    return exprPoly;
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
      IInteger lcm = getLCM(symbol);
      if (lcm.isOne()) {
        lcm = F.C1;
      }
      if (lcm.isOne() && exp.isInteger()) {
        return F.Power(symbol, exp);
      }

      IRational rat = exp.rationalFactor();
      if (rat != null) {
        IInteger intExp = rat.multiply(lcm).numerator();
        int exponent = intExp.toIntDefault();
        if (F.isPresent(exponent)) {
          if (exponent == 1) {
            return symbol;
          }
          return F.Power(symbol, exponent);
        }
      }
      return F.Power(symbol, F.Times(lcm, exp));
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
    IExpr temp = F.subst(expression, x -> {
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
    return temp; // engine.evaluate(temp);
  }

  /**
   * Backward substitution - transforming the symbol back by reading the denominator LCM and
   * returning factor ^ denominatorLCM * @param symbol this symbol must be replaced by another
   * symbol and associated denominator LCM
   * 
   * @param resultValue
   */
  public IExpr replaceDenominatorBackwardLCM(final ISymbol symbol, IExpr resultValue) {
    final IExpr t = substitutedVariables.get(symbol);
    if (t != null) {
      final IInteger denominatorLCM = getLCM(symbol);
      if (t.isSymbol()) {
        if (denominatorLCM.isOne()) {
          return resultValue;
        }
        return F.Power(resultValue, denominatorLCM);
      } else {
        final VariablesSet varSet = new VariablesSet(t);
        if (varSet.size() == 1) {
          IExpr solveVar = varSet.firstVariable();
          if (t.isAST1() && t.head().isSymbol() && t.first().equals(solveVar)) {
            final IExpr reduced =
                ReduceVariableEqual.reduce(F.Equal(t, resultValue), solveVar, false);
            if (reduced.isPresent()) {
              if (denominatorLCM.isOne()) {
                return reduced;
              }
              return F.Power(reduced, denominatorLCM);
            }
          }
        }
      }
    }
    return F.NIL;

  }

  /**
   * Variables (ISymbols) which are substituted from the original polynomial (backward substitution)
   * returned in a IdentityHashMap.
   */
  public java.util.Map<ISymbol, IExpr> substitutedVariables() {
    return substitutedVariables;
  }

  public java.util.Set<ISymbol> substitutedVariablesSet() {
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
    Map<ISymbol, IExpr> map = substitutedVariables();
    IASTAppendable list = F.ListAlloc(size());
    for (Map.Entry<ISymbol, IExpr> x : map.entrySet()) {
      IExpr value = x.getValue();
      if (value != null) {
        IExpr key = x.getKey();
        IInteger denominatorLCM = getLCM(key);
        if (denominatorLCM.isOne()) {
          list.append(F.Rule(key, value));
        } else {
          list.append(F.Rule(key, F.Power(value, F.fraction(F.C1, denominatorLCM))));
        }
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
  private IExpr normalizeLogBase(IExpr expr) {
    if (!expr.isTimes()) {
      return F.NIL;
    }
    final IAST timesAST = (IAST) expr;

    // Collect 1-based indices for each category
    java.util.List<Integer> varLogIdxs = new java.util.ArrayList<>(); // Log(v), v non-numeric
    java.util.List<Integer> constLogInvIdxs = new java.util.ArrayList<>(); // Log(c)^(-1), c numeric
    java.util.List<Integer> constLogIdxs = new java.util.ArrayList<>(); // Log(c), c numeric
    java.util.List<Integer> varLogInvIdxs = new java.util.ArrayList<>(); // Log(v)^(-1), v
                                                                         // non-numeric

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
    java.util.Set<Integer> consumed = new java.util.HashSet<>();
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
