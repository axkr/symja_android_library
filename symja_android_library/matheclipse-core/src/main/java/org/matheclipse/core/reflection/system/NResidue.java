package org.matheclipse.core.reflection.system;

import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.hipparchus.complex.Complex;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.numerics.integral.ContourResidue;
import org.matheclipse.parser.client.ParserConfig;

/**
 * <pre>
 * <code>NResidue(expr, {z, z0})</code>
 * </pre>
 *
 * <blockquote>
 * <p>
 * numerically finds the residue of <code>expr</code> near the point <code>z = z0</code>, i.e. the
 * coefficient of <code>(z-z0)^(-1)</code> in the Laurent expansion of <code>expr</code>.
 * </p>
 * </blockquote>
 *
 * <p>
 * The residue is computed by integrating <code>expr</code> around a small circle in the complex
 * plane, so - unlike the symbolic {@link org.matheclipse.core.expression.S#Residue} - it needs no
 * series expansion and also works for essential singularities like <code>Exp(1/z)</code>.
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * <code>&gt;&gt; NResidue(1/x, {x, 0}) // Chop
 * 1.0
 *
 * &gt;&gt; NResidue(Sin(1/(10*x)), {x, 0}) // Chop
 * 0.1
 *
 * &gt;&gt; NResidue(Exp(1/x), {x, 0}, Radius -&gt; 1) // Chop
 * 1.0
 * </code>
 * </pre>
 *
 * <h3>Related terms</h3>
 * <p>
 * <a href="Residue.md">Residue</a>, <a href="NIntegrate.md">NIntegrate</a>, <a href="ND.md">ND</a>,
 * <a href="Chop.md">Chop</a>
 * </p>
 */
public class NResidue extends AbstractFunctionOptionEvaluator {

  /** Number of contour sample count doublings if {@link S#MaxRecursion} doesn't say otherwise. */
  private static final int DEFAULT_MAX_RECURSION = 10;

  /** Guard digits the automatic {@link S#PrecisionGoal} stays below the working precision. */
  private static final double GOAL_GUARD_DIGITS = 2.0;

  public NResidue() {
    // default ctor
  }

  @Override
  public IExpr evaluate(final IAST ast, final int argSize, final IExpr[] options,
      final EvalEngine engine, IAST originalAST) {
    if (argSize != 2) {
      return F.NIL;
    }
    final IExpr function = ast.arg1();
    if (function.isList()) {
      // NResidue is deliberately not ISymbol.LISTABLE: generic threading would split the {z, z0}
      // specification into two meaningless calls, so the first argument is threaded by hand.
      return engine.evaluate(((IAST) function).mapThread(ast, 1));
    }
    if (!ast.arg2().isList2()) {
      return F.NIL;
    }
    final IAST specification = (IAST) ast.arg2();
    final IExpr variable = specification.arg1();
    if (!variable.isVariable()) {
      // `1` is not a valid variable.
      return Errors.printMessage(S.NResidue, "ivar", F.List(variable), engine);
    }
    final IExpr center = specification.arg2();

    final long precision = workingPrecision(options[1], engine);
    final double workingDigits =
        (precision > 0) ? precision : ParserConfig.MACHINE_PRECISION_DOUBLE;
    final double precisionGoal = Math.max(1.0,
        Math.min(workingDigits, goal(S.PrecisionGoal, options[2], workingDigits, engine)));
    final double accuracyGoal = goal(S.AccuracyGoal, options[3], workingDigits, engine);
    final int maxRecursion = maxRecursion(options[4], engine);
    checkMethod(options[5], engine);

    final boolean automaticRadius = options[0].isAutomatic();
    final IExpr radius = automaticRadius ? F.NIL : radius(options[0], engine);

    if (precision > 0) {
      return apfloatResidue(ast, function, variable, center, radius, automaticRadius, precision,
          precisionGoal, accuracyGoal, maxRecursion, engine);
    }
    return machineResidue(ast, function, variable, center, radius, automaticRadius, precisionGoal,
        accuracyGoal, maxRecursion, engine);
  }

  /**
   * Computes the residue in machine precision.
   *
   * @param radius the contour radius, {@link F#NIL} if <code>automaticRadius</code> is set
   */
  private static IExpr machineResidue(IAST ast, IExpr function, IExpr variable, IExpr center,
      IExpr radius, boolean automaticRadius, double precisionGoal, double accuracyGoal,
      int maxRecursion, EvalEngine engine) {
    final Complex z0 = center.evalfcNaN();
    if (z0 == null || z0.isNaN() || z0.isInfinite()) {
      return nonNumeric(ast, function, center, engine);
    }
    final ContourResidue.Sampler<Complex> sampler = z -> {
      try {
        final Complex value = F.subst(function, variable, F.complexNum(z)).evalfcNaN();
        return (value == null || value.isNaN() || value.isInfinite()) ? null : value;
      } catch (RuntimeException rex) {
        return null;
      }
    };

    final ContourResidue.Result<Complex> result;
    // the integrand is sampled at points it may well be singular at - those samples are simply
    // dropped, the user doesn't need to hear about each of them
    final boolean quietMode = engine.isQuietMode();
    try {
      engine.setQuietMode(true);
      if (automaticRadius) {
        result =
            ContourResidue.autoRadius(MachineOps::new, sampler, z0, precisionGoal, maxRecursion);
      } else {
        final double r = radius.evalfNaN();
        result = ContourResidue.fixedRadius(new MachineOps(r), sampler, z0, precisionGoal,
            maxRecursion);
      }
    } finally {
      engine.setQuietMode(quietMode);
    }

    if (result.status == ContourResidue.Status.NON_NUMERIC) {
      return nonNumeric(ast, function, center, engine);
    }
    report(result, result.value.norm(), accuracyGoal, function, center, engine);
    return (result.value.getImaginary() == 0.0) ? F.num(result.value.getReal())
        : F.complexNum(result.value);
  }

  /**
   * Computes the residue with <code>precision</code> decimal digits of arbitrary precision
   * arithmetic.
   *
   * @param radius the contour radius, {@link F#NIL} if <code>automaticRadius</code> is set
   */
  private static IExpr apfloatResidue(IAST ast, IExpr function, IExpr variable, IExpr center,
      IExpr radius, boolean automaticRadius, long precision, double precisionGoal,
      double accuracyGoal, int maxRecursion, EvalEngine engine) {
    final ContourResidue.Sampler<Apcomplex> sampler = z -> {
      try {
        return apcomplexValue(F.subst(function, variable, F.complexNum(z)), engine);
      } catch (RuntimeException rex) {
        return null;
      }
    };

    final ContourResidue.Result<Apcomplex> result;
    final boolean oldNumericMode = engine.isNumericMode();
    final long oldPrecision = engine.getNumericPrecision();
    final int oldSignificantFigures = engine.getSignificantFigures();
    final boolean quietMode = engine.isQuietMode();
    try {
      // The whole contour is sampled in one arbitrary precision numeric mode. Evaluating each
      // sample as N(sample, precision) instead would re-enter the evaluator per point and run into
      // its iteration limit.
      engine.setNumericMode(true, precision, oldSignificantFigures);

      final Apcomplex z0 = apcomplexValue(center, engine);
      if (z0 == null) {
        return nonNumeric(ast, function, center, engine);
      }
      final Apcomplex r = automaticRadius ? null : apcomplexValue(radius, engine);
      if (!automaticRadius && r == null) {
        return nonNumeric(ast, function, center, engine);
      }

      // the integrand is sampled at points it may well be singular at - those samples are simply
      // dropped, the user doesn't need to hear about each of them
      engine.setQuietMode(true);
      if (automaticRadius) {
        result = ContourResidue.autoRadius(
            radiusValue -> new ApfloatOps(new Apfloat(radiusValue, precision), precision), sampler,
            z0, precisionGoal, maxRecursion);
      } else {
        result = ContourResidue.fixedRadius(new ApfloatOps(r.real(), precision), sampler, z0,
            precisionGoal, maxRecursion);
      }
    } finally {
      engine.setQuietMode(quietMode);
      engine.setNumericMode(oldNumericMode, oldPrecision, oldSignificantFigures);
    }

    if (result.status == ContourResidue.Status.NON_NUMERIC) {
      return nonNumeric(ast, function, center, engine);
    }
    report(result, ApcomplexMath.abs(result.value).doubleValue(), accuracyGoal, function, center,
        engine);
    return (result.value.imag().signum() == 0) ? F.num(result.value.real())
        : F.complexNum(result.value);
  }

  /**
   * Prints the message that goes with a non-{@link ContourResidue.Status#OK} result, if any.
   *
   * @param magnitude the absolute value of the computed residue
   * @param accuracyGoal the absolute error target in decimal digits;
   *        {@link Double#POSITIVE_INFINITY} disables it
   */
  private static void report(ContourResidue.Result<?> result, double magnitude, double accuracyGoal,
      IExpr function, IExpr center, EvalEngine engine) {
    switch (result.status) {
      case NO_CONVERGENCE:
        // A residue that is smaller than the absolute AccuracyGoal is indistinguishable from zero
        // anyway, so the relative goal it missed doesn't tell the user anything.
        if (magnitude > Math.pow(10.0, -accuracyGoal)) {
          // NResidue failed to converge to the requested accuracy ...
          Errors.printMessage(S.NResidue, "nrescnv",
              F.List(F.num(result.radius), center, F.ZZ(result.evaluations)), engine);
        }
        break;
      case BRANCH_CUT:
        // The integrand `1` is not analytic on the contour ...
        Errors.printMessage(S.NResidue, "nresbc", F.List(function, F.num(result.radius), center),
            engine);
        break;
      default:
        break;
    }
  }

  private static IExpr nonNumeric(IAST ast, IExpr function, IExpr center, EvalEngine engine) {
    // The integrand `1` could not be evaluated to a number on the contour around `2`.
    Errors.printMessage(S.NResidue, "nresnum", F.List(function, center), engine);
    return F.NIL;
  }

  /**
   * Numerically evaluates <code>expr</code> in the arbitrary precision numeric mode the engine is
   * currently set to.
   *
   * @return <code>null</code> if <code>expr</code> has no numerical value
   */
  private static Apcomplex apcomplexValue(IExpr expr, EvalEngine engine) {
    final IExpr value = engine.evalNumericFunction(expr, false);
    return value.isNumber() ? ((INumber) value).apcomplexValue() : null;
  }

  /**
   * @return the value of the {@link S#Radius} option, or the default <code>1/100</code> if it isn't
   *         a positive number
   */
  private static IExpr radius(IExpr option, EvalEngine engine) {
    final double value = option.evalfNaN();
    if (value > 0.0 && Double.isFinite(value)) {
      return option;
    }
    // Value of option `1` -> `2` is not valid; the default value is used.
    Errors.printMessage(S.NResidue, "nresopt", F.List(S.Radius, option), engine);
    return F.QQ(1, 100);
  }

  /**
   * @return the number of decimal digits of arbitrary precision arithmetic to compute with, or
   *         <code>-1</code> to compute in machine precision
   */
  private static long workingPrecision(IExpr option, EvalEngine engine) {
    if (option.isAutomatic() || option == S.MachinePrecision) {
      return -1;
    }
    final int digits = option.toIntDefault();
    if (digits <= 0) {
      // Value of option `1` -> `2` is not valid; the default value is used.
      Errors.printMessage(S.NResidue, "nresopt", F.List(S.WorkingPrecision, option), engine);
      return -1;
    }
    if (digits <= ParserConfig.MACHINE_PRECISION) {
      return -1;
    }
    return Math.min(digits, Config.MAX_PRECISION_APFLOAT);
  }

  /**
   * Resolves an {@link S#AccuracyGoal} or {@link S#PrecisionGoal} option to a number of decimal
   * digits. <code>Automatic</code> and <code>MachinePrecision</code> track the working precision,
   * <code>Infinity</code> switches the goal off.
   */
  private static double goal(IBuiltInSymbol name, IExpr option, double workingDigits,
      EvalEngine engine) {
    if (option.isAutomatic() || option == S.MachinePrecision) {
      return workingDigits - GOAL_GUARD_DIGITS;
    }
    if (option.isInfinity()) {
      return Double.POSITIVE_INFINITY;
    }
    final double digits = option.evalfNaN();
    if (Double.isNaN(digits)) {
      // Value of option `1` -> `2` is not valid; the default value is used.
      Errors.printMessage(S.NResidue, "nresopt", F.List(name, option), engine);
      return workingDigits - GOAL_GUARD_DIGITS;
    }
    return digits;
  }

  private static int maxRecursion(IExpr option, EvalEngine engine) {
    if (option.isAutomatic()) {
      return DEFAULT_MAX_RECURSION;
    }
    final int recursion = option.toIntDefault();
    if (recursion < 0) {
      // Value of option `1` -> `2` is not valid; the default value is used.
      Errors.printMessage(S.NResidue, "nresopt", F.List(S.MaxRecursion, option), engine);
      return DEFAULT_MAX_RECURSION;
    }
    return recursion;
  }

  /**
   * The periodic trapezoidal rule is the only method that makes sense on a circular contour, so
   * anything else is reported and ignored.
   */
  private static void checkMethod(IExpr option, EvalEngine engine) {
    // an unknown symbol reaches this as its (lower cased) name, a string as its content
    if (option.isAutomatic() || option.toString().equalsIgnoreCase("Trapezoidal")) {
      return;
    }
    // Value of option `1` -> `2` is not valid; the default value is used.
    Errors.printMessage(S.NResidue, "nresopt", F.List(S.Method, option), engine);
  }

  /** Machine precision contour arithmetic. */
  private static final class MachineOps implements ContourResidue.Ops<Complex> {

    private final double radius;

    private MachineOps(final double radius) {
      this.radius = radius;
    }

    @Override
    public Complex add(Complex a, Complex b) {
      return a.add(b);
    }

    @Override
    public Complex subtract(Complex a, Complex b) {
      return a.subtract(b);
    }

    @Override
    public Complex multiply(Complex a, Complex b) {
      return a.multiply(b);
    }

    @Override
    public Complex divide(Complex a, Complex b) {
      return a.divide(b);
    }

    @Override
    public Complex node(Complex z0, int k, int n) {
      return z0.add(phase(k, n).multiply(radius));
    }

    @Override
    public Complex phase(int k, int n) {
      final double theta = 2.0 * Math.PI * k / n;
      return new Complex(Math.cos(theta), Math.sin(theta));
    }

    @Override
    public Complex weight(Complex sum, int n) {
      return sum.multiply(radius / n);
    }

    @Override
    public double abs(Complex a) {
      return a.norm();
    }

    @Override
    public boolean isFinite(Complex a) {
      return !a.isNaN() && !a.isInfinite();
    }

    @Override
    public double radius() {
      return radius;
    }
  }

  /** Arbitrary precision contour arithmetic. */
  private static final class ApfloatOps implements ContourResidue.Ops<Apcomplex> {

    private final Apfloat radius;
    private final long precision;
    private final Apfloat twoPi;
    private final double radiusValue;

    private ApfloatOps(final Apfloat radius, final long precision) {
      this.radius = radius;
      this.precision = precision;
      this.twoPi = ApfloatMath.pi(precision).multiply(new Apfloat(2, precision));
      this.radiusValue = radius.doubleValue();
    }

    @Override
    public Apcomplex add(Apcomplex a, Apcomplex b) {
      return a.add(b);
    }

    @Override
    public Apcomplex subtract(Apcomplex a, Apcomplex b) {
      return a.subtract(b);
    }

    @Override
    public Apcomplex multiply(Apcomplex a, Apcomplex b) {
      return a.multiply(b);
    }

    @Override
    public Apcomplex divide(Apcomplex a, Apcomplex b) {
      return a.divide(b);
    }

    @Override
    public Apcomplex node(Apcomplex z0, int k, int n) {
      return z0.add(phase(k, n).multiply(new Apcomplex(radius)));
    }

    @Override
    public Apcomplex phase(int k, int n) {
      if (k == 0) {
        return new Apcomplex(new Apfloat(1, precision));
      }
      final Apfloat theta =
          twoPi.multiply(new Apfloat(k, precision)).divide(new Apfloat(n, precision));
      return new Apcomplex(ApfloatMath.cos(theta), ApfloatMath.sin(theta));
    }

    @Override
    public Apcomplex weight(Apcomplex sum, int n) {
      return sum.multiply(new Apcomplex(radius.divide(new Apfloat(n, precision))));
    }

    @Override
    public double abs(Apcomplex a) {
      return ApcomplexMath.abs(a).doubleValue();
    }

    @Override
    public boolean isFinite(Apcomplex a) {
      // apfloat has no representation for NaN or Infinity - a value that got this far is finite
      return true;
    }

    @Override
    public double radius() {
      return radiusValue;
    }
  }

  private static IBuiltInSymbol[] defaultOptionKeys() {
    return new IBuiltInSymbol[] {//
        S.Radius, S.WorkingPrecision, S.PrecisionGoal, S.AccuracyGoal, S.MaxRecursion, S.Method};
  }

  private static IExpr[] defaultOptionValues() {
    return new IExpr[] {//
        F.QQ(1, 100), S.MachinePrecision, S.Automatic, S.MachinePrecision,
        F.ZZ(DEFAULT_MAX_RECURSION), S.Automatic};
  }

  @Override
  public int status() {
    return ImplementationStatus.PARTIAL_SUPPORT;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_2_INFINITY;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(ISymbol.HOLDFIRST);
    setOptions(newSymbol, defaultOptionKeys(), defaultOptionValues());
  }
}
