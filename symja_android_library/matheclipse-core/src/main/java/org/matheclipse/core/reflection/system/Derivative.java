package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.List;
import org.apfloat.Apcomplex;
import org.apfloat.ApfloatRuntimeException;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.ApcomplexNum;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.InterpolatingFunctionExpr;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.numerics.functions.CarlsonArbitraryPrecision;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.core.patternmatching.PatternMatcherEquals;
import org.matheclipse.core.patternmatching.RulesData;
import org.matheclipse.external.apfloat.ApcomplexZetaMath;
import org.matheclipse.parser.client.ParserConfig;

/**
 *
 *
 * <pre>
 * Derivative(n)[f]
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * represents the <code>n</code>-th derivative of the function <code>f</code>.<br>
 *
 * </blockquote>
 *
 * <pre>
 * Derivative(n1, n2, n3,...)[f]
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * represents a multivariate derivative.
 *
 * </blockquote>
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * &gt;&gt; Derivative(1)[Sin]
 * Cos(#1)&amp;
 *
 * &gt;&gt; Derivative(3)[Sin]
 * -Cos(#1)&amp;
 *
 * &gt;&gt; Derivative(2)[# ^ 3&amp;]
 * 6*#1&amp;
 * </pre>
 *
 * <p>
 * <code>Derivative</code> can be entered using <code>'</code>:<br>
 *
 * <pre>
 * &gt;&gt; Sin'(x)
 * Cos(x)
 *
 * &gt;&gt; (# ^ 4&amp;)''
 * 12*#1^2&amp;
 *
 * &gt;&gt; f'(x) // FullForm
 * "Derivative(1)[f][x]"
 * </pre>
 *
 * <p>
 * The <code>0</code>th derivative of any expression is the expression itself:
 *
 * <pre>
 * &gt;&gt; Derivative(0,0,0)[a+b+c]
 * a+b+c
 * </pre>
 *
 * <p>
 * Unknown derivatives:<br>
 *
 * <pre>
 * &gt;&gt; Derivative(2, 1)[h]
 * Derivative(2,1)[h]
 *
 * &gt;&gt; Derivative(2, 0, 1, 0)[h(g)]
 * Derivative(2,0,1,0)[h(g)]
 * </pre>
 */
public class Derivative extends AbstractFunctionEvaluator {

  /**
   * The rules of this symbol as they were installed. <code>Derivative</code> isn't protected, so
   * <code>Sin' = Tan</code> changes its rules; <code>D</code> goes on with these ones, and they
   * bring back what <code>Sin' =.</code> or <code>Clear(Derivative)</code> took away.
   */
  private static volatile RulesData builtinRules = null;

  /** Was a definition made which isn't one for a function of the user? */
  private static volatile boolean builtinRulesChanged = false;

  /** Keep the built-in <code>rules</code> of this symbol, when they were just installed. */
  public static void rulesInstalled(RulesData rules) {
    builtinRules = rules.copy();
  }

  /** Note that a definition may hide the built-in rules from now on. */
  public static void setBuiltinRulesChanged() {
    builtinRulesChanged = true;
  }

  /**
   * <code>derivative</code> is <code>Derivative(n1, n2, ...)[f][x1, x2, ...]</code>. <code>D</code>
   * doesn't use definitions like <code>Sin' = Tan</code>, only <code>Sin'</code> itself does.
   *
   * @return <code>derivative</code> with the head a built-in rule gives for it, or
   *         <code>derivative</code> itself
   */
  public static IAST withBuiltinRule(IAST derivative) {
    final RulesData rules = builtinRules;
    if (!builtinRulesChanged || rules == null || !derivative.head().first().isBuiltInSymbol()) {
      return derivative;
    }
    IExpr function = rules.evalDownRule(derivative.head(), EvalEngine.get());
    return function.isPresent() ? derivative.setAtCopy(0, function) : derivative;
  }

  /**
   * <code>lhs =.</code> for the left side <code>lhs</code> of a built-in rule: such a rule stays, or
   * comes back if a definition replaced it.
   *
   * @return <code>false</code> if <code>lhs</code> isn't the left side of a built-in rule
   */
  public static boolean restoreBuiltinRule(IExpr lhs) {
    final RulesData rules = builtinRules;
    if (rules == null) {
      return false;
    }
    PatternMatcherEquals rule = rules.getEqualDownRules().get(lhs);
    if (rule != null) {
      S.Derivative.putDownRule(IPatternMatcher.SET, true, (IAST) lhs, rule.getRHS(), true);
      return true;
    }
    return rules.copy().removeRule(IPatternMatcher.SET, false, lhs);
  }

  /** <code>Clear(Derivative)</code> removes the definitions, not the built-in rules. */
  public static void restoreBuiltinRules() {
    final RulesData rules = builtinRules;
    if (rules != null) {
      S.Derivative.setRulesData(rules.copy());
      builtinRulesChanged = false;
    }
  }

  public Derivative() {}

  @Override
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    if (isDiracDeltaDerivativeAwayFromZero(ast)) {
      // like DiracDelta(x) itself, every derivative of it vanishes where x is a nonzero real
      return F.C0;
    }
    IAST[] derivativeAST = ast.isDerivative();
    if (derivativeAST == null) {
      return F.NIL;
    }
    IAST derivativeHead = derivativeAST[0];
    IAST functions = derivativeAST[1];
    boolean isZero = derivativeHead.forAll(n -> n.isZero());
    if (isZero && derivativeAST[2] == null) {
      // Derivative(0, 0, ...)[f] -> f
      return functions.size() > 1 ? functions.arg1() : F.NIL;
    }
    if (functions.size() != 2) {
      return F.NIL;
    }
    if (functions.arg1().isNumber()) {
      return F.Function(F.C0);
    }
    if (derivativeAST[2] != null && functions.arg1() == S.Zeta && derivativeHead.isAST1()
        && derivativeHead.arg1().isOne() && derivativeAST[2].isAST1()) {
      IExpr value = zetaDerivative(derivativeAST[2].arg1(), engine);
      if (value.isPresent()) {
        return value;
      }
    }
    for (int i = 1; i < derivativeHead.size(); i++) {
      if (isInvalidDerivativeOrder(derivativeHead.get(i))) {
        // like in Mathematica an explicit negative or non-integer order stays unevaluated
        return F.NIL;
      }
    }
    if (functions.arg1() instanceof InterpolatingFunctionExpr) {
      // an interpolating function or numerical solution which knows its derivatives
      IExpr derived = interpolatingFunctionDerivative(derivativeHead,
          (InterpolatingFunctionExpr<?>) functions.arg1());
      if (derived.isPresent()) {
        return derivativeAST[2] != null ? derivativeAST[2].setAtCopy(0, derived) : derived;
      }
    }
    if (derivativeAST[2] != null && functions.arg1().isAST(S.BSplineFunction)) {
      // a spline at real parameters
      IExpr derived = BSplineFunction.derivative((IAST) functions.arg1(), derivativeHead,
          derivativeAST[2], engine);
      if (derived.isPresent()) {
        return derived;
      }
    }
    IExpr extremum = maxMinDerivative(derivativeHead, functions.arg1(), derivativeAST[2]);
    if (extremum.isPresent()) {
      return extremum;
    }
    IExpr result = evaluateDIfPossible(derivativeHead, functions, derivativeAST[2], engine);
    if (result.isNIL() && derivativeAST[2] != null) {
      return numericDerivative(derivativeHead, functions.arg1(), derivativeAST[2], engine);
    }
    return result;
  }

  /** The largest total order of a derivative which is approximated by finite differences. */
  private static final int MAX_NUMERIC_ORDER = 4;

  /**
   * The most evaluations of the function for one finite difference derivative: the extrapolations
   * of a mixed partial derivative are nested, one for each variable.
   */
  private static final int MAX_NUMERIC_EVALUATIONS = 5000;

  /**
   * <code>Derivative(n1, n2, ...)[f][x1, x2, ...]</code> of a user defined function
   * <code>f</code> at a point of machine real numbers, if <code>f</code> has no symbolic
   * derivative - for example because it is only defined for numeric arguments. The derivative is
   * approximated by central differences, extrapolated to step size zero.
   *
   * @return {@link F#NIL} if <code>f</code> isn't a user defined symbol, the point isn't machine
   *         real or <code>f</code> doesn't give a machine real number near the point
   */
  private static IExpr numericDerivative(IAST orders, IExpr function, IAST arguments,
      EvalEngine engine) {
    if (!function.isSymbol() || function.isBuiltInSymbol()
        || orders.argSize() != arguments.argSize()) {
      return F.NIL;
    }
    final int k = arguments.argSize();
    final double[] point = new double[k];
    final int[] n = new int[k];
    int totalOrder = 0;
    for (int i = 0; i < k; i++) {
      IExpr arg = arguments.get(i + 1);
      if (!arg.isReal() || !arg.isInexactNumber()) {
        return F.NIL;
      }
      point[i] = arg.evalf();
      n[i] = orders.get(i + 1).toIntDefault();
      if (n[i] < 0) {
        return F.NIL;
      }
      totalOrder += n[i];
    }
    if (totalOrder == 0 || totalOrder > MAX_NUMERIC_ORDER) {
      return F.NIL;
    }
    double value =
        partialDerivative(function, point, n, 0, new int[] {MAX_NUMERIC_EVALUATIONS}, engine);
    if (Double.isNaN(value) || Double.isInfinite(value)) {
      return F.NIL;
    }
    return F.num(value);
  }

  /**
   * The derivative of the orders <code>n[i], n[i+1], ...</code> of <code>function</code> with
   * respect to the variables <code>i, i+1, ...</code> at <code>point</code>.
   *
   * @param budget the number of function evaluations which are left, decremented by each one
   * @return {@link Double#NaN} if the function doesn't give a machine real number or the budget is
   *         exhausted
   */
  private static double partialDerivative(IExpr function, double[] point, int[] n, int i,
      int[] budget, EvalEngine engine) {
    if (i == point.length) {
      if (--budget[0] < 0) {
        return Double.NaN;
      }
      engine.checkTimeLimit();
      IASTAppendable call = F.ast(function, point.length);
      for (double x : point) {
        call.append(F.num(x));
      }
      IExpr value = engine.evaluate(call);
      if (value.isReal()) {
        return value.evalf();
      }
      return Double.NaN;
    }
    if (n[i] == 0) {
      return partialDerivative(function, point, n, i + 1, budget, engine);
    }
    final double x0 = point[i];
    final int order = n[i];
    java.util.function.DoubleUnaryOperator centralDifference = h -> {
      // the central difference of order m with the points x0 + (m/2 - j)*h
      double sum = 0.0;
      double binomial = 1.0;
      for (int j = 0; j <= order; j++) {
        double[] shifted = point.clone();
        shifted[i] = x0 + (order / 2.0 - j) * h;
        double f = partialDerivative(function, shifted, n, i + 1, budget, engine);
        if (Double.isNaN(f)) {
          return Double.NaN;
        }
        sum += ((j & 1) == 0 ? binomial : -binomial) * f;
        binomial = binomial * (order - j) / (j + 1);
      }
      return sum / Math.pow(h, order);
    };
    // Ridders' extrapolation of the central differences to h -> 0; their error is a series in h^2
    final int ntab = 10;
    final double con = 1.4;
    final double con2 = con * con;
    final double safe = 2.0;
    double h = 0.1 * Math.max(1.0, Math.abs(x0)) * order;
    double[][] a = new double[ntab][ntab];
    a[0][0] = centralDifference.applyAsDouble(h);
    if (Double.isNaN(a[0][0])) {
      return Double.NaN;
    }
    double result = a[0][0];
    double err = Double.MAX_VALUE;
    for (int col = 1; col < ntab; col++) {
      h /= con;
      a[0][col] = centralDifference.applyAsDouble(h);
      if (Double.isNaN(a[0][col])) {
        return Double.NaN;
      }
      double fac = con2;
      for (int row = 1; row <= col; row++) {
        a[row][col] = (a[row - 1][col] * fac - a[row - 1][col - 1]) / (fac - 1.0);
        fac *= con2;
        double errt = Math.max(Math.abs(a[row][col] - a[row - 1][col]),
            Math.abs(a[row][col] - a[row - 1][col - 1]));
        if (errt <= err) {
          err = errt;
          result = a[row][col];
        }
      }
      if (Math.abs(a[col][col] - a[col - 1][col - 1]) >= safe * err) {
        break;
      }
    }
    return result;
  }

  /**
   * <code>Derivative(n1, n2, ...)[f]</code> of an interpolating function which can build its
   * derivative from its data.
   *
   * @return {@link F#NIL} unless all orders are non-negative machine integers and the function
   *         supports derivatives
   */
  private static IExpr interpolatingFunctionDerivative(IAST orders,
      InterpolatingFunctionExpr<?> function) {
    int[] n = new int[orders.argSize()];
    for (int i = 0; i < n.length; i++) {
      n[i] = orders.get(i + 1).toIntDefault(-1);
      if (n[i] < 0) {
        return F.NIL;
      }
    }
    return function.derivative(n);
  }

  /**
   * <code>Zeta'(s)</code> for a number: there is no other function which it could be written in.
   */
  private static IExpr zetaDerivative(IExpr s, EvalEngine engine) {
    if (!s.isNumber() && engine.isNumericMode() && s.isNumericFunction()) {
      s = engine.evalN(s);
    }
    if (!s.isNumber() || s.isOne() || (!s.isInexactNumber() && !engine.isNumericMode())) {
      return F.NIL;
    }
    final boolean arbitrary = s instanceof ApfloatNum || s instanceof ApcomplexNum
        || (!s.isInexactNumber() && engine.isArbitraryMode());
    final int digits =
        arbitrary ? (int) engine.getNumericPrecision() : (int) ParserConfig.MACHINE_PRECISION + 1;
    try {
      Apcomplex value = ApcomplexZetaMath.zetaDerivative(CarlsonArbitraryPrecision.apcomplex(s,
          digits + CarlsonArbitraryPrecision.GUARD_DIGITS));
      if (!arbitrary) {
        return s.isReal() ? F.num(value.real().doubleValue())
            : F.complexNum(value.real().doubleValue(), value.imag().doubleValue());
      }
      return CarlsonArbitraryPrecision.round(value, digits);
    } catch (ArithmeticException | ApfloatRuntimeException ex) {
      Errors.rethrowsInterruptException(ex);
      return F.NIL;
    }
  }

  /**
   * A partial derivative of <code>Max</code> or <code>Min</code>. Of the first order in argument
   * <code>k</code>: a <code>Piecewise</code> over the cases that argument <code>i</code> is the
   * extremum, the first one at a tie, which is <code>1</code> for <code>i == k</code> and
   * <code>0</code> otherwise; <code>Derivative(0,1)[Max][x,y]</code> is
   * <code>Piecewise({{0, x-y >= 0}}, 1)</code>. Adjacent cases of the same value are joined, the
   * last case is the default. Of a higher order <code>0</code>, since the function is one of its
   * arguments almost everywhere.
   */
  private static IExpr maxMinDerivative(IAST orders, IExpr function, IAST arguments) {
    if (function != S.Max && function != S.Min) {
      return F.NIL;
    }
    if (arguments == null) {
      // Derivative(1,1)[Max] is applied to its arguments only after it is evaluated itself
      int total = 0;
      for (int i = 1; i <= orders.argSize(); i++) {
        int n = orders.get(i).toIntDefault(-1);
        if (n < 0) {
          return F.NIL;
        }
        total += n;
      }
      return total > 1 ? F.Function(F.C0) : F.NIL;
    }
    if (arguments.argSize() != orders.argSize() || arguments.argSize() < 1
        || arguments.exists(x -> x.isList() || (x.isNumber() && !x.isReal()))) {
      // the maximum of a list, or of complex numbers, is not one of the arguments
      return F.NIL;
    }
    int position = -1;
    int total = 0;
    for (int i = 1; i <= orders.argSize(); i++) {
      IExpr order = orders.get(i);
      if (!order.isInteger() || order.isNegative()) {
        return F.NIL;
      }
      int n = order.toIntDefault();
      if (n > 0) {
        position = i;
        total += n;
      }
    }
    if (total == 0) {
      return F.NIL;
    }
    if (total > 1) {
      return F.C0;
    }
    final boolean max = function == S.Max;
    final int n = arguments.argSize();
    // the value and the condition of each case but the last, which is the default
    IASTAppendable cases = F.ListAlloc(n);
    IExpr previousValue = null;
    IASTAppendable previousCondition = null;
    for (int i = 1; i < n; i++) {
      IExpr xi = arguments.get(i);
      IASTAppendable condition = F.ast(S.And, n);
      for (int j = 1; j <= n; j++) {
        if (j < i) {
          // an earlier argument which would win a tie is not the extremum
          IExpr difference = F.Subtract(arguments.get(j), xi);
          condition.append(max ? F.Less(difference, F.C0) : F.Greater(difference, F.C0));
        } else if (j > i) {
          IExpr difference = F.Subtract(xi, arguments.get(j));
          condition.append(max ? F.GreaterEqual(difference, F.C0) : F.LessEqual(difference, F.C0));
        }
      }
      IExpr value = i == position ? F.C1 : F.C0;
      IExpr conjunction = condition.isAST1() ? condition.arg1() : condition;
      if (value.equals(previousValue)) {
        previousCondition.append(conjunction);
      } else {
        if (previousCondition != null) {
          cases.append(F.list(previousValue, orOf(previousCondition)));
        }
        previousValue = value;
        previousCondition = F.ast(S.Or);
        previousCondition.append(conjunction);
      }
    }
    IExpr defaultValue = n == position ? F.C1 : F.C0;
    if (previousCondition != null && !previousValue.equals(defaultValue)) {
      cases.append(F.list(previousValue, orOf(previousCondition)));
    }
    if (cases.isEmpty()) {
      return defaultValue;
    }
    return F.Piecewise(cases, defaultValue);
  }

  private static IExpr orOf(IASTAppendable or) {
    return or.isAST1() ? or.arg1() : or;
  }

  /**
   * Whether <code>ast</code> is <code>Derivative(n1, n2, ...)[DiracDelta][x1, x2, ...]</code> with
   * non-negative integer orders and one of the <code>xi</code> a nonzero real: the residual of a
   * step-forced equation, differentiated twice, holds <code>DiracDelta'(7/10 - Pi)</code>.
   */
  private static boolean isDiracDeltaDerivativeAwayFromZero(IAST ast) {
    IExpr head = ast.head();
    if (ast.argSize() < 1 || !head.isAST1() || head.first() != S.DiracDelta
        || !head.head().isAST(S.Derivative) || ((IAST) head.head()).argSize() != ast.argSize()) {
      return false;
    }
    IAST orders = (IAST) head.head();
    for (int i = 1; i <= orders.argSize(); i++) {
      if (!orders.get(i).isInteger() || orders.get(i).isNegative()) {
        return false;
      }
    }
    return ast.exists(x -> x.isNonZeroRealResult());
  }

  /**
   * Test if <code>n</code> cannot be used as the order of a derivative.
   *
   * <p>
   * Valid orders are non-negative integers and symbolic expressions which don't evaluate to an
   * explicit number (for example <code>n</code>, <code>-1+n</code> or <code>2*n</code>). This is
   * the same test which {@link D} uses for the <code>{variable, n}</code> specifier.
   *
   * @param n the order of the derivative
   * @return <code>true</code> if <code>n</code> is an explicit negative or non-integer number
   */
  private static boolean isInvalidDerivativeOrder(IExpr n) {
    return n.isNegativeResult() || (!n.isInteger() && n.isNumericFunction());
  }

  private static IExpr evaluateDIfPossible(IAST head, IAST headDerivative, IAST fullDerivative,
      EvalEngine engine) {
    if (fullDerivative == null) {
      IExpr named = namedFunctionDerivative(head, headDerivative.arg1(), engine);
      if (named.isPresent()) {
        return named;
      }
    }
    IASTAppendable newFunction = F.ast(headDerivative.arg1());
    IASTAppendable list = F.ListAlloc(headDerivative.size());
    IASTAppendable dExpr;
    for (int i = 1; i < head.size(); i++) {
      IExpr n = head.get(i);
      IExpr symbol = F.Slot(i);
      if (fullDerivative != null) {
        if (fullDerivative.size() != headDerivative.size()) {
          return F.NIL;
        }
        symbol = fullDerivative.get(i);
        if (!symbol.isVariable()) {
          return F.NIL;
        }
      }

      newFunction.append(symbol);

      if (n.isOne()) {
        list.append(symbol);
      } else {
        int ni = n.toIntDefault();
        if (ni < 0) {
          if (F.isNotPresent(ni)) {
            list.append(F.list(symbol, n));
          } else {
            return F.NIL;
          }
        } else if (ni > 0) {
          int iterationLimit = engine.getIterationLimit();
          if (iterationLimit > 0 && iterationLimit < ni) {
            // Iteration limit of `1` exceeded.
            return Errors.printMessage(S.Derivative, "itlim", F.list(F.ZZ(iterationLimit)), engine);
          }
          list.append(F.list(symbol, n));
        }
      }
    }
    boolean doEval = false;
    IExpr temp = newFunction;
    if (headDerivative.arg1().isBuiltInSymbol()) {
      IBuiltInSymbol builtin = (IBuiltInSymbol) headDerivative.arg1();
      if (builtin.hasNumericFunctionAttribute()) {
        if (head.isAST1()) {
          int n = head.first().toMachineInt();
          if (n > 0) {
            IExpr dResult = S.Derivative.evalDownRule(engine,
                (n == 1) ? headDerivative : headDerivative.setAtCopy(0, head.setAtCopy(1, F.C1)));
            if (dResult.isPresent()) {
              doEval = true;
            }
          }
        } else {
          IExpr dResult = S.Derivative.evalDownRule(engine, headDerivative);
          if (dResult.isPresent()) {
            doEval = true;
          } else if (builtin == S.Multinomial) {
            temp = multinomial(head);
            if (temp.isPresent()) {
              return temp;
            }
          }
        }
        int[] orders = headDerivative.head().toIntVector();
        if (orders != null) {
          temp = pureMultipleOrderDerivative(builtin, engine, orders);
          if (temp.isPresent()) {
            return temp;
          }
        }
      }
    } else {
      temp = engine.evaluateNIL(newFunction);
      if (temp.isPresent()) {
        doEval = true;
      }
    }
    if (doEval) {
      dExpr = F.ast(S.D, list.size() + 1);
      dExpr.append(temp);
      dExpr.appendArgs(list); // w.r.t these symbols
      return F.Function(engine.evaluate(dExpr));
    }
    return F.NIL;
  }

  /**
   * <code>Derivative(n1, n2, ...)[Function({x1, x2, ...}, body)]</code> keeps the named
   * parameters: <code>Derivative(1)[Function({q}, q^2)]</code> is <code>Function({q}, 2*q)</code>.
   *
   * @return {@link F#NIL} if <code>function</code> is no function with as many named parameters as
   *         there are orders, or an order is no non-negative integer
   */
  private static IExpr namedFunctionDerivative(IAST orders, IExpr function, EvalEngine engine) {
    if (!function.isFunction() || function.argSize() != 2) {
      return F.NIL;
    }
    IAST parameters = function.first().makeList();
    if (parameters.argSize() != orders.argSize() || !parameters.forAll(x -> x.isSymbol())) {
      return F.NIL;
    }
    // the parameters are local: they are differentiated as slots, so a global value of a
    // parameter's symbol doesn't replace it
    java.util.Map<IExpr, IExpr> toSlots = new java.util.HashMap<IExpr, IExpr>();
    java.util.Map<IExpr, IExpr> toParameters = new java.util.HashMap<IExpr, IExpr>();
    for (int i = 1; i < parameters.size(); i++) {
      toSlots.put(parameters.get(i), F.Slot(i));
      toParameters.put(F.Slot(i), parameters.get(i));
    }
    IASTAppendable d = F.ast(S.D, orders.argSize() + 1);
    d.append(F.subst(function.second(), toSlots));
    for (int i = 1; i < orders.size(); i++) {
      int n = orders.get(i).toIntDefault();
      if (n < 0) {
        return F.NIL;
      }
      if (n > 0) {
        d.append(F.list(F.Slot(i), F.ZZ(n)));
      }
    }
    if (d.argSize() == 1) {
      return function;
    }
    IExpr body = engine.evaluate(d);
    if (body.isFree(x -> x.isAST(S.D), true)) {
      return F.Function(function.first(), F.subst(body, toParameters));
    }
    return F.NIL;
  }

  /**
   * Generates a symbolic expression for the partial derivative of a given multi-argument function.
   *
   * @param function the built-in symbol of the Symja function
   * @param orders an array of integers representing the derivative orders for each argument.
   * @return the symbolic expression of the derivative as an {@link IAST} object.
   */
  public static IAST pureMultipleOrderDerivative(IBuiltInSymbol function, EvalEngine engine,
      int... orders) {
    if (isEndlessRecursion(orders)) {
      return F.NIL;
    }

    int numberOfSlots = orders.length;
    List<IAST> slots = new ArrayList<>(numberOfSlots);
    IASTAppendable functionExpr = F.ast(function, numberOfSlots);
    for (int i = 0; i < numberOfSlots; i++) {
      IAST slot = F.Slot(i + 1);
      slots.add(slot);
      functionExpr.append(slot);
    }

    // Dynamically create the base symbolic expression from the function name.
    IExpr exprToDifferentiate = functionExpr;
    for (int i = 0; i < numberOfSlots; i++) {
      IAST currentVar = slots.get(i);
      int order = orders[i];
      for (int j = 0; j < order; j++) {
        IExpr temp = engine.evaluateNIL(F.D(exprToDifferentiate, currentVar));
        if (temp.isPresent() && temp.topHead() != S.Derivative) {
          exprToDifferentiate = temp;
        } else {
          return F.NIL;
        }
      }
    }

    return F.Function(exprToDifferentiate);

  }

  /**
   * Check for the case <code>orders.length == 0</code> or the case
   * <code>Derivative(1,0,0,...,0)[f]</code> which could lead to endless recursion if <code>f</code>
   * is a built-in numeric function symbol
   * 
   * @param orders
   * @return
   */
  private static boolean isEndlessRecursion(int... orders) {
    if (orders.length == 0) {
      return true;
    }
    int countOne = 0;
    int countUnequalZero = 0;
    for (int i = 0; i < orders.length; i++) {
      if (orders[i] < 0) {
        return false;
      }
      if (orders[i] == 1) {
        countOne++;
      } else if (orders[i] != 0) {
        countUnequalZero++;
      }
    }
    return countUnequalZero == 0 && countOne == 1;
  }

  private static IExpr multinomial(IAST head) {
    final int argSize = head.argSize();
    IASTAppendable multinomial = F.ast(S.Multinomial, argSize);
    IASTAppendable harmonicPlus = F.ast(S.Plus, argSize);
    int countOne = 0;
    int harmonicIndex = -1;
    for (int i = 1; i <= argSize; i++) {
      final IAST slot = F.Slot(i);
      multinomial.append(slot);
      harmonicPlus.append(slot);
      if (head.get(i).isOne()) {
        harmonicIndex = i;
        countOne++;
      }
    }
    if (countOne == 1) {
      // (-HarmonicNumber(#[i])+HarmonicNumber(harmonicPlus))*multinomial&
      return F.Function(F.Times(
          F.Plus(F.Negate(F.HarmonicNumber(F.Slot(harmonicIndex))), F.HarmonicNumber(harmonicPlus)),
          multinomial));
    }
    return F.NIL;
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(Attribute.NHOLDALL);
    // not protected: f'[x_] := ... and Derivative[1, 0][f][x_, y_] := ... define derivatives. In a
    // Config.SHARED_JVM it counts as protected all the same, see BuiltInSymbol#hasProtectedAttribute
    newSymbol.clearAttributes(ISymbol.PROTECTED);
    super.setUp(newSymbol);
  }

  /**
   * Create <code>Derivative(n)[header][arg1]</code>
   *
   * @param n
   * @param header
   * @param arg1
   * @returnW
   */
  public static IAST createDerivative(final int n, final IExpr header, final IExpr arg1) {
    IAST deriv = F.Derivative(F.ZZ(n));
    IASTAppendable fDeriv = F.ast(deriv);
    fDeriv.append(header);
    IASTAppendable fDerivParam = F.ast(fDeriv);
    fDerivParam.append(arg1);
    return fDerivParam;
  }

  /**
   * Create <code>Derivative(n)[header]</code>
   *
   * @param n
   * @param header
   * @return
   */
  public static IAST createDerivative(final int n, final IExpr header) {
    IAST deriv = F.Derivative(F.ZZ(n));
    IASTAppendable fDeriv = F.ast(deriv);
    fDeriv.append(header);
    return fDeriv;
  }
}
