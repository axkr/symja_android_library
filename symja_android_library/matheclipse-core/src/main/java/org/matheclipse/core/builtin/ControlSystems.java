package org.matheclipse.core.builtin;

import org.hipparchus.complex.Complex;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.InterpolatingFunctionExpr;
import org.matheclipse.core.generic.UnaryNumerical;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Linear control systems: {@link S#TransferFunctionModel}, its poles, zeros and responses, the
 * conversions between continuous and sampled models and the frequency-response plots.
 *
 * <p>
 * A model is kept in the normal form <code>TransferFunctionModel({nums, den}, s, options...)</code>:
 * <code>nums</code> is the matrix of numerators (one row per output, one column per input) and
 * <code>den</code> either the denominator all elements share or a matrix of denominators. A time
 * delay <code>E^(-tau*s)</code> stays a factor of its numerator. Nothing is cancelled.
 */
public class ControlSystems {
  private static final int BILINEAR = 0;
  private static final int FORWARD = 1;
  private static final int BACKWARD = 2;

  private static final int BODE = 0;
  private static final int NYQUIST = 1;
  private static final int NICHOLS = 2;

  /** Output samples of a simulated response. */
  private static final int OUTPUT_INTERVALS = 1000;

  /** Frequencies sampled by a frequency-response plot. */
  private static final int FREQUENCY_POINTS = 400;

  private static class Initializer {
    private static void init() {
      S.TransferFunctionModel.setEvaluator(new TransferFunctionModel());
      S.TransferFunctionPoles.setEvaluator(new TransferFunctionRoots(true));
      S.TransferFunctionZeros.setEvaluator(new TransferFunctionRoots(false));
      S.OutputResponse.setEvaluator(new OutputResponse());
      S.ToDiscreteTimeModel.setEvaluator(new ConvertModel(true));
      S.ToContinuousTimeModel.setEvaluator(new ConvertModel(false));
      S.BodePlot.setEvaluator(new FrequencyResponsePlot(BODE));
      S.NyquistPlot.setEvaluator(new FrequencyResponsePlot(NYQUIST));
      S.NicholsPlot.setEvaluator(new FrequencyResponsePlot(NICHOLS));
    }
  }

  /** A transfer-function matrix: numerator, denominator and delay of every element. */
  private static final class Model {
    final IExpr[][] num;
    final IExpr[][] den;
    /** The delay <code>tau</code> of an element <code>E^(-tau*s)*num/den</code>. */
    final IExpr[][] delay;
    final IExpr var;
    /** The model expression; its arguments from the third on are the options. */
    final IAST template;

    Model(int rows, int cols, IExpr var, IAST template) {
      num = new IExpr[rows][cols];
      den = new IExpr[rows][cols];
      delay = new IExpr[rows][cols];
      this.var = var;
      this.template = template;
    }

    int rows() {
      return num.length;
    }

    int cols() {
      return num[0].length;
    }

    /** The model in normal form, or <code>null</code> if <code>expr</code> isn't one. */
    static Model of(IExpr expr, EvalEngine engine) {
      if (!expr.isAST(S.TransferFunctionModel) || expr.argSize() < 2
          || !isNormalForm(expr.first())) {
        return null;
      }
      IAST nums = (IAST) expr.first().first();
      IExpr dens = expr.first().second();
      int cols = nums.first().argSize();
      Model model = new Model(nums.argSize(), cols, expr.second(), (IAST) expr);
      for (int i = 0; i < model.rows(); i++) {
        for (int j = 0; j < cols; j++) {
          IExpr[] split = splitDelay(nums.getAST(i + 1).get(j + 1), model.var, engine);
          model.num[i][j] = split[1];
          model.delay[i][j] = engine.evaluate(F.Negate(split[0]));
          model.den[i][j] = dens.isList() ? ((IAST) dens).getAST(i + 1).get(j + 1) : dens;
        }
      }
      return model;
    }

    boolean hasDelay() {
      for (IExpr[] row : delay) {
        for (IExpr tau : row) {
          if (!tau.isZero()) {
            return true;
          }
        }
      }
      return false;
    }

    /** The value of the <code>SamplingPeriod</code> option, <code>None</code> if not given. */
    IExpr samplingPeriod() {
      for (int i = 3; i < template.size(); i++) {
        IExpr option = template.get(i);
        if (option.isRuleAST() && option.first().equals(S.SamplingPeriod)) {
          return option.second();
        }
      }
      return S.None;
    }

    /** The normal form, with the variable and options of <code>template</code>. */
    IExpr toExpr(EvalEngine engine) {
      IASTAppendable nums = F.ListAlloc(rows());
      IASTAppendable dens = F.ListAlloc(rows());
      boolean shared = true;
      for (int i = 0; i < rows(); i++) {
        IASTAppendable numRow = F.ListAlloc(cols());
        IASTAppendable denRow = F.ListAlloc(cols());
        for (int j = 0; j < cols(); j++) {
          numRow.append(engine.evaluate(F.Times(num[i][j], F.Exp(F.Times(F.CN1, delay[i][j], var)))));
          denRow.append(den[i][j]);
          shared &= den[i][j].equals(den[0][0]);
        }
        nums.append(numRow);
        dens.append(denRow);
      }
      IASTMutable result = template.setAtCopy(1, F.list(nums, shared ? den[0][0] : dens));
      result.set(2, var);
      return result;
    }
  }

  /**
   * <code>{nums, den}</code> with a matrix <code>nums</code> and a denominator which is a single
   * expression or a matrix of the same shape.
   */
  private static boolean isNormalForm(IExpr expr) {
    if (!expr.isList2() || !expr.first().isList()) {
      return false;
    }
    int[] dims = expr.first().isMatrix(false);
    if (dims == null || expr.first().first().first().isList()) {
      return false;
    }
    IExpr den = expr.second();
    if (!den.isList()) {
      return true;
    }
    int[] denDims = den.isMatrix(false);
    return denDims != null && denDims[0] == dims[0] && denDims[1] == dims[1];
  }

  /**
   * Split the factors <code>E^(c*s)</code> off <code>expr</code>.
   *
   * @return <code>{c, rest}</code>, the sum of the exponent coefficients and the other factors
   */
  private static IExpr[] splitDelay(IExpr expr, IExpr s, EvalEngine engine) {
    IExpr c = F.C0;
    IASTAppendable rest = F.TimesAlloc(4);
    for (IExpr factor : expr.isTimes() ? (IAST) expr : F.list(expr)) {
      IExpr coefficient = factor.isPower() && factor.base().equals(S.E)
          ? engine.evaluate(F.D(factor.exponent(), s))
          : F.C0;
      if (!coefficient.isZero() && coefficient.isFree(s) && engine
          .evaluate(F.Expand(F.Subtract(factor.exponent(), F.Times(coefficient, s)))).isZero()) {
        c = engine.evaluate(F.Plus(c, coefficient));
      } else {
        rest.append(factor);
      }
    }
    return new IExpr[] {c, rest.oneIdentity1()};
  }

  /**
   * Numerator, denominator and delay of one element given as an expression. A ratio of polynomials
   * keeps its own numerator and denominator; anything else is combined with
   * <code>Together</code>.
   *
   * @return <code>null</code> if the element isn't a rational function times a delay
   */
  private static IExpr[] elementParts(IExpr element, IExpr s, EvalEngine engine) {
    IExpr[] num = splitDelay(engine.evaluate(F.Numerator(element)), s, engine);
    IExpr[] den = splitDelay(engine.evaluate(F.Denominator(element)), s, engine);
    IExpr n = num[1];
    IExpr d = den[1];
    if (!n.isPolynomial(s) || !d.isPolynomial(s)) {
      IExpr together = engine.evaluate(F.Together(F.Divide(n, d)));
      n = engine.evaluate(F.Numerator(together));
      d = engine.evaluate(F.Denominator(together));
      if (!n.isPolynomial(s) || !d.isPolynomial(s)) {
        return null;
      }
    }
    IExpr tau = engine.evaluate(F.Subtract(den[0], num[0]));
    if (tau.isInexactNumber()) {
      // an inexact delay is written exactly; the rest of the element becomes inexact
      tau = engine.evaluate(F.Rationalize(tau));
      n = engine.evalN(n);
    }
    return new IExpr[] {n, d, tau};
  }

  /** A model from an expression or a matrix of expressions in <code>s</code>. */
  private static Model modelOf(IExpr expr, IExpr s, IAST template, EvalEngine engine) {
    IAST matrix = expr.isMatrix(false) != null ? (IAST) expr : F.list(F.list(expr));
    Model model = new Model(matrix.argSize(), matrix.first().argSize(), s, template);
    for (int i = 0; i < model.rows(); i++) {
      for (int j = 0; j < model.cols(); j++) {
        IExpr[] parts = elementParts(matrix.getAST(i + 1).get(j + 1), s, engine);
        if (parts == null) {
          return null;
        }
        model.num[i][j] = parts[0];
        model.den[i][j] = parts[1];
        model.delay[i][j] = parts[2];
      }
    }
    return model;
  }

  /**
   * <code>TransferFunctionModel(expr, s)</code>, <code>TransferFunctionModel({{...}}, s)</code> and
   * the normal form itself, with options such as <code>SamplingPeriod-&gt;T</code>.
   */
  private static final class TransferFunctionModel extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      if (!ast.arg2().isVariable() || isNormalForm(ast.arg1())) {
        return F.NIL;
      }
      Model model = modelOf(ast.arg1(), ast.arg2(), ast, engine);
      return model == null ? F.NIL : model.toExpr(engine);
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_INFINITY;
    }
  }

  /**
   * The roots of a polynomial in <code>s</code>, repeated by multiplicity, or <code>null</code>.
   */
  private static IAST roots(IExpr p, IExpr s, EvalEngine engine) {
    IASTAppendable result = F.ListAlloc();
    if (p.isFree(s)) {
      return result;
    }
    // square-free factors give the multiplicities; inexact coefficients are solved as they are
    IExpr factors = p.has(x -> x.isInexactNumber(), false) ? F.list(F.list(p, F.C1))
        : engine.evaluate(F.FactorSquareFreeList(p));
    if (!factors.isList()) {
      return null;
    }
    for (IExpr factor : (IAST) factors) {
      if (!factor.isList2() || factor.first().isFree(s)) {
        continue;
      }
      IExpr solutions = engine.evaluate(F.Solve(F.Equal(factor.first(), F.C0), s));
      if (!solutions.isListOfLists()) {
        return null;
      }
      for (int k = 0; k < factor.second().toIntDefault(1); k++) {
        for (IExpr rules : (IAST) solutions) {
          if (!rules.isList1() || !rules.first().isRuleAST()) {
            return null;
          }
          result.append(rules.first().second());
        }
      }
    }
    return result;
  }

  /** <code>TransferFunctionPoles(model)</code> and <code>TransferFunctionZeros(model)</code>. */
  private static final class TransferFunctionRoots extends AbstractFunctionEvaluator {
    private final boolean poles;

    TransferFunctionRoots(boolean poles) {
      this.poles = poles;
    }

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      Model model = Model.of(ast.arg1(), engine);
      if (model == null) {
        return F.NIL;
      }
      IASTAppendable result = F.ListAlloc(model.rows());
      for (int i = 0; i < model.rows(); i++) {
        IASTAppendable row = F.ListAlloc(model.cols());
        for (int j = 0; j < model.cols(); j++) {
          IAST roots = roots(poles ? model.den[i][j] : model.num[i][j], model.var, engine);
          if (roots == null) {
            return F.NIL;
          }
          row.append(roots);
        }
        result.append(row);
      }
      return result;
    }

    @Override
    public int status() {
      return ImplementationStatus.FULL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /**
   * <code>OutputResponse(model, u, t)</code> as a closed form and
   * <code>OutputResponse(model, u, {t, tmin, tmax})</code>, simulated as
   * <code>{InterpolatingFunction(...)(t), ...}</code> where no closed form is found. <code>u</code>
   * is one input or a list with one per model input; the system starts at rest.
   */
  private static final class OutputResponse extends AbstractFunctionEvaluator {
    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      Model model = Model.of(ast.arg1(), engine);
      if (model == null || !model.samplingPeriod().equals(S.None)) {
        return F.NIL;
      }
      IAST inputs = ast.arg2().isList() ? (IAST) ast.arg2() : F.list(ast.arg2());
      if (inputs.argSize() != model.cols()) {
        return F.NIL;
      }
      IExpr spec = ast.arg3();
      if (spec.isVariable()) {
        if (model.hasDelay()) {
          return Errors.printMessage(S.OutputResponse, "delaysm", F.CEmptyList, engine);
        }
        return symbolicResponse(model, inputs, spec, engine);
      }
      if ((spec.isList2() || spec.isList3()) && spec.first().isVariable()) {
        double t0 = spec.isList3() ? spec.second().evalfNaN() : 0.0;
        double t1 = spec.last().evalfNaN();
        if (!(t1 > t0)) {
          return F.NIL;
        }
        if (!model.hasDelay()) {
          IExpr exact = symbolicResponse(model, inputs, spec.first(), engine);
          if (exact.isPresent()) {
            return exact;
          }
        }
        return simulatedResponse(model, inputs, (ISymbol) spec.first(), t0, t1, engine);
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_3;
    }
  }

  /** The responses by the inverse Laplace transform, or <code>F.NIL</code>. */
  private static IExpr symbolicResponse(Model model, IAST inputs, IExpr t, EvalEngine engine) {
    IExpr s = model.var;
    IExpr[] transforms = new IExpr[model.cols()];
    for (int j = 0; j < transforms.length; j++) {
      transforms[j] = engine.evaluate(F.LaplaceTransform(inputs.get(j + 1), t, s));
      if (!transforms[j].isFree(S.LaplaceTransform)) {
        return F.NIL;
      }
    }
    // the response to an input switched on at 0 starts there
    IExpr start = inputs.has(S.DiracDelta) ? F.HeavisideTheta(t)
        : inputs.has(S.UnitStep) || inputs.has(S.HeavisideTheta) ? F.UnitStep(t) : F.C1;
    IASTAppendable result = F.ListAlloc(model.rows());
    for (int i = 0; i < model.rows(); i++) {
      IASTAppendable sum = F.PlusAlloc(model.cols());
      for (int j = 0; j < model.cols(); j++) {
        IExpr y = engine.evaluate(F.InverseLaplaceTransform(
            F.Times(model.num[i][j], F.Power(model.den[i][j], F.CN1), transforms[j]), s, t));
        if (!y.isFree(S.InverseLaplaceTransform)) {
          return F.NIL;
        }
        sum.append(y);
      }
      result.append(engine.evaluate(F.Times(sum.oneIdentity0(), start)));
    }
    return result;
  }

  /** Ascending numeric coefficients of a polynomial in <code>s</code>, or <code>null</code>. */
  private static double[] coefficients(IExpr p, IExpr s, EvalEngine engine) {
    IExpr list = engine.evaluate(F.CoefficientList(p, s));
    if (!list.isList() || list.argSize() == 0) {
      return list.isList() ? new double[] {0.0} : null;
    }
    double[] result = new double[list.argSize()];
    for (int k = 0; k < result.length; k++) {
      result[k] = list.get(k + 1).evalfNaN();
      if (!Double.isFinite(result[k])) {
        return null;
      }
    }
    return result;
  }

  /** The moduli of the roots of a polynomial with ascending coefficients. */
  private static double[] rootModuli(double[] p) {
    if (p.length < 2) {
      return new double[0];
    }
    Complex[] roots = RootsFunctions.allComplexRootsLaguerre(p);
    if (roots == null) {
      return new double[0];
    }
    double[] result = new double[roots.length];
    for (int k = 0; k < roots.length; k++) {
      result[k] = roots[k].norm();
    }
    return result;
  }

  /**
   * Simulate <code>b(s)/a(s)</code> (proper) with the classical Runge-Kutta method on its
   * controllable canonical realisation, from rest at <code>times[0]</code>.
   *
   * @param u the input
   * @param times the increasing times to give the output at
   * @param refine the number of steps between two of the <code>times</code>
   * @return the output at the <code>times</code>, or <code>null</code>
   */
  private static double[] simulate(double[] b, double[] a, UnaryNumerical u, double[] times,
      int refine) {
    int n = a.length - 1;
    double lead = a[n];
    double d = b.length > n ? b[n] / lead : 0.0;
    // y = d*u + c.x
    double[] c = new double[n];
    double[] an = new double[n];
    for (int k = 0; k < n; k++) {
      an[k] = a[k] / lead;
      c[k] = (k < b.length ? b[k] / lead : 0.0) - d * an[k];
    }
    double[] x = new double[n];
    double[] y = new double[times.length];
    for (int index = 0; index < times.length; index++) {
      double out = d == 0.0 ? 0.0 : d * u.value(times[index]);
      for (int k = 0; k < n; k++) {
        out += c[k] * x[k];
      }
      if (!Double.isFinite(out)) {
        return null;
      }
      y[index] = out;
      if (index + 1 == times.length || n == 0) {
        continue;
      }
      double h = (times[index + 1] - times[index]) / refine;
      // the input is sampled just inside each step, so that a jump at a step boundary takes effect
      // from that boundary on
      double inside = h * 1e-6;
      for (int step = 0; step < refine; step++) {
        double t = times[index] + step * h;
        double uStart = u.value(t + inside);
        double uMid = u.value(t + h / 2);
        double uEnd = u.value(t + h - inside);
        if (!Double.isFinite(uStart) || !Double.isFinite(uMid) || !Double.isFinite(uEnd)) {
          return null;
        }
        double[] k1 = derivative(x, an, uStart);
        double[] k2 = derivative(axpy(x, k1, h / 2), an, uMid);
        double[] k3 = derivative(axpy(x, k2, h / 2), an, uMid);
        double[] k4 = derivative(axpy(x, k3, h), an, uEnd);
        for (int k = 0; k < n; k++) {
          x[k] += h / 6 * (k1[k] + 2 * k2[k] + 2 * k3[k] + k4[k]);
        }
      }
    }
    return y;
  }

  /** The state derivative of the controllable canonical form. */
  private static double[] derivative(double[] x, double[] a, double u) {
    int n = x.length;
    double[] result = new double[n];
    double last = u;
    for (int k = 0; k < n; k++) {
      if (k < n - 1) {
        result[k] = x[k + 1];
      }
      last -= a[k] * x[k];
    }
    result[n - 1] = last;
    return result;
  }

  private static double[] axpy(double[] x, double[] dx, double h) {
    double[] result = new double[x.length];
    for (int k = 0; k < x.length; k++) {
      result[k] = x[k] + h * dx[k];
    }
    return result;
  }

  /** The simulated responses as <code>{InterpolatingFunction(...)(t), ...}</code>. */
  private static IExpr simulatedResponse(Model model, IAST inputs, ISymbol t, double t0, double t1,
      EvalEngine engine) {
    double[][] outputs = new double[model.rows()][OUTPUT_INTERVALS + 1];
    double dt = (t1 - t0) / OUTPUT_INTERVALS;
    for (int i = 0; i < model.rows(); i++) {
      for (int j = 0; j < model.cols(); j++) {
        double[] b = coefficients(model.num[i][j], model.var, engine);
        double[] a = coefficients(model.den[i][j], model.var, engine);
        double tau = model.delay[i][j].evalfNaN();
        if (b == null || a == null || a.length < b.length || a[a.length - 1] == 0.0
            || !Double.isFinite(tau)) {
          return F.NIL;
        }
        // a step small against the fastest pole
        double fastest = 0.0;
        for (double modulus : rootModuli(a)) {
          fastest = Math.max(fastest, modulus);
        }
        int refine = (int) Math.min(200, Math.max(1, Math.ceil(dt * fastest / 0.02)));
        // a delay shifts the response: the delay-free response is simulated at the shifted times
        int first = 0;
        while (first <= OUTPUT_INTERVALS && first * dt - tau < dt * 1e-9) {
          first++;
        }
        double[] shifted = new double[OUTPUT_INTERVALS + 2 - first];
        shifted[0] = t0;
        for (int k = first; k <= OUTPUT_INTERVALS; k++) {
          shifted[k - first + 1] = t0 + k * dt - tau;
        }
        double[] y = simulate(b, a, new UnaryNumerical(inputs.get(j + 1), t, Double.NaN, engine),
            shifted, refine);
        if (y == null) {
          return F.NIL;
        }
        for (int k = first; k <= OUTPUT_INTERVALS; k++) {
          outputs[i][k] += y[k - first + 1];
        }
        if (first > 0 && Math.abs((first - 1) * dt - tau) < dt * 1e-9) {
          // the output time the delay ends at
          outputs[i][first - 1] += y[0];
        }
      }
    }
    double[] times = new double[OUTPUT_INTERVALS + 1];
    for (int k = 0; k < times.length; k++) {
      times[k] = t0 + k * dt;
    }
    IASTAppendable result = F.ListAlloc(model.rows());
    for (double[] output : outputs) {
      result.append(F.unaryAST1(InterpolatingFunctionExpr.newSampled(times, output), t));
    }
    return result;
  }

  /** The conversion method of the <code>Method</code> option, <code>-1</code> if unknown. */
  private static int method(IAST ast, int from) {
    for (int i = from; i < ast.size(); i++) {
      IExpr option = ast.get(i);
      if (option.isRuleAST() && option.first().equals(S.Method)) {
        String name = option.second().toString();
        return name.equals("BilinearTransform") ? BILINEAR
            : name.equals("ForwardRectangularRule") ? FORWARD
                : name.equals("BackwardRectangularRule") ? BACKWARD : -1;
      }
    }
    return BILINEAR;
  }

  /**
   * <code>ToDiscreteTimeModel(model, T, z)</code> and <code>ToContinuousTimeModel(model, s)</code>
   * by the bilinear transform (the default) or a rectangular rule. Each element is written as a
   * ratio of polynomials in the new variable by the substitution <code>old = a/b</code>, clearing
   * the power of <code>b</code> from numerator and denominator alike.
   */
  private static final class ConvertModel extends AbstractFunctionEvaluator {
    private final boolean toDiscrete;

    ConvertModel(boolean toDiscrete) {
      this.toDiscrete = toDiscrete;
    }

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      Model model = Model.of(ast.arg1(), engine);
      if (model == null || model.hasDelay()) {
        return F.NIL;
      }
      IExpr period = toDiscrete ? (ast.argSize() >= 2 ? ast.arg2() : F.NIL) : model.samplingPeriod();
      if (!period.isPresent() || period.isRuleAST() || period.equals(S.None)
          || toDiscrete != model.samplingPeriod().equals(S.None)) {
        return F.NIL;
      }
      int varPosition = toDiscrete ? 3 : 2;
      IExpr var = ast.size() > varPosition && !ast.get(varPosition).isRuleAST()
          ? ast.get(varPosition)
          : toDiscrete ? S.z : S.s;
      int method = method(ast, varPosition);
      if (!var.isVariable() || method < 0) {
        return F.NIL;
      }
      IExpr a;
      IExpr b;
      IExpr one = F.C1;
      if (toDiscrete) {
        // s = a/b
        IExpr zm1 = F.Plus(F.CN1, var);
        a = method == BILINEAR ? F.Times(F.C2, zm1)
            : method == FORWARD ? F.Divide(zm1, period) : zm1;
        b = method == BILINEAR ? F.Times(period, F.Plus(one, var))
            : method == FORWARD ? one : F.Times(period, var);
      } else {
        // z = a/b
        IExpr sT = F.Times(period, var);
        a = method == BILINEAR ? F.Plus(F.C2, sT) : method == FORWARD ? F.Plus(one, sT) : one;
        b = method == BILINEAR ? F.Subtract(F.C2, sT)
            : method == FORWARD ? one : F.Subtract(one, sT);
      }
      IASTAppendable template = F.ast(S.TransferFunctionModel);
      template.append(F.CEmptyList);
      template.append(var);
      for (int i = 3; i < model.template.size(); i++) {
        IExpr option = model.template.get(i);
        if (!option.isRuleAST() || !option.first().equals(S.SamplingPeriod)) {
          template.append(option);
        }
      }
      template.append(F.Rule(S.SamplingPeriod, toDiscrete ? period : S.None));
      Model result = new Model(model.rows(), model.cols(), var, template);
      for (int i = 0; i < model.rows(); i++) {
        for (int j = 0; j < model.cols(); j++) {
          int n = Math.max(degree(model.num[i][j], model.var, engine),
              degree(model.den[i][j], model.var, engine));
          result.num[i][j] = homogenize(model.num[i][j], model.var, a, b, n, period, engine);
          result.den[i][j] = homogenize(model.den[i][j], model.var, a, b, n, period, engine);
          result.delay[i][j] = F.C0;
          if (!result.num[i][j].isPresent() || !result.den[i][j].isPresent()) {
            return F.NIL;
          }
        }
      }
      return result.toExpr(engine);
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return toDiscrete ? ARGS_2_INFINITY : ARGS_1_INFINITY;
    }
  }

  private static int degree(IExpr p, IExpr x, EvalEngine engine) {
    return engine.evaluate(F.Exponent(p, x)).toIntDefault(0);
  }

  /**
   * <code>Sum(c_k * a^k * b^(n-k))</code> for the coefficients <code>c_k</code> of <code>p</code>
   * in <code>x</code>: the polynomial <code>p(a/b)</code> cleared of its denominator
   * <code>b^n</code>. With an inexact sampling <code>period</code> a non-constant result is
   * inexact too.
   */
  private static IExpr homogenize(IExpr p, IExpr x, IExpr a, IExpr b, int n, IExpr period,
      EvalEngine engine) {
    IExpr list = engine.evaluate(F.CoefficientList(p, x));
    if (!list.isList()) {
      return F.NIL;
    }
    IASTAppendable sum = F.PlusAlloc(list.argSize());
    for (int k = 0; k < list.argSize(); k++) {
      sum.append(F.Times(list.get(k + 1), F.Power(a, F.ZZ(k)), F.Power(b, F.ZZ(n - k))));
    }
    IExpr result = engine.evaluate(F.Expand(sum.oneIdentity0()));
    return period.isInexactNumber() && !result.isNumber() ? engine.evalN(result) : result;
  }

  /** One element as numbers for its frequency response. */
  private static final class NumericElement {
    final double[] num;
    final double[] den;
    final double tau;
    /** The sampling period of a discrete model, 0 for a continuous one. */
    final double period;

    NumericElement(double[] num, double[] den, double tau, double period) {
      this.num = num;
      this.den = den;
      this.tau = tau;
      this.period = period;
    }

    /** The response <code>G(I*w)</code>, or <code>G(E^(I*w*T))</code> for a sampled model. */
    Complex at(double omega) {
      Complex x = period > 0 ? new Complex(0, omega * period).exp() : new Complex(0, omega);
      Complex delay = new Complex(0, -omega * tau).exp();
      return horner(num, x).divide(horner(den, x)).multiply(delay);
    }

    private static Complex horner(double[] p, Complex x) {
      Complex result = Complex.ZERO;
      for (int k = p.length - 1; k >= 0; k--) {
        result = result.multiply(x).add(p[k]);
      }
      return result;
    }
  }

  /**
   * <code>BodePlot(model)</code> (magnitude in decibels over phase in degrees against a logarithmic
   * frequency axis, as a two-row <code>Grid</code>), <code>NyquistPlot(model)</code> (the frequency
   * response in the complex plane with its mirror image) and <code>NicholsPlot(model)</code>
   * (magnitude in decibels against phase in degrees). The model may be a list of models or an
   * expression followed by its variable; a frequency range <code>{wmin, wmax}</code> may follow.
   */
  private static final class FrequencyResponsePlot extends AbstractFunctionEvaluator {
    private final int kind;

    FrequencyResponsePlot(int kind) {
      this.kind = kind;
    }

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IAST models = ast.arg1().isList() ? (IAST) ast.arg1() : F.list(ast.arg1());
      int next = 2;
      if (!models.forAll(m -> m.isAST(S.TransferFunctionModel))) {
        if (ast.argSize() < 2 || !ast.arg2().isVariable()) {
          return F.NIL;
        }
        models = F.list(engine.evaluate(F.binaryAST2(S.TransferFunctionModel, ast.arg1(), ast.arg2())));
        next = 3;
      }
      java.util.List<NumericElement> elements = new java.util.ArrayList<>();
      for (IExpr expr : models) {
        if (!addElements(expr, elements, engine)) {
          return F.NIL;
        }
      }
      double[] range = automaticRange(elements);
      if (ast.size() > next && ast.get(next).isList2()) {
        range = new double[] {ast.get(next).first().evalfNaN(), ast.get(next).second().evalfNaN()};
        next++;
        if (!(range[0] > 0 && range[1] > range[0])) {
          return F.NIL;
        }
      }
      IASTAppendable options = F.ListAlloc();
      for (int i = next; i < ast.size(); i++) {
        if (ast.get(i).isRuleAST()) {
          options.append(ast.get(i));
        }
      }
      return plot(elements, range, options, engine);
    }

    private IExpr plot(java.util.List<NumericElement> elements, double[] range, IAST options,
        EvalEngine engine) {
      IASTAppendable magnitudes = F.ListAlloc();
      IASTAppendable phases = F.ListAlloc();
      IASTAppendable curves = F.ListAlloc();
      for (NumericElement element : elements) {
        IASTAppendable magnitude = F.ListAlloc(FREQUENCY_POINTS);
        IASTAppendable phase = F.ListAlloc(FREQUENCY_POINTS);
        IASTAppendable curve = F.ListAlloc(FREQUENCY_POINTS);
        IASTAppendable mirror = F.ListAlloc(FREQUENCY_POINTS);
        double previous = Double.NaN;
        for (int k = 0; k < FREQUENCY_POINTS; k++) {
          double omega =
              range[0] * Math.pow(range[1] / range[0], k / (double) (FREQUENCY_POINTS - 1));
          Complex g = element.at(omega);
          double db = 20 * Math.log10(g.norm());
          double arg = g.getArgument();
          if (!Double.isFinite(db) || !Double.isFinite(arg)) {
            continue;
          }
          // a continuous phase: without jumps, starting in [-180, 180)
          arg = Double.isNaN(previous) ? (arg >= Math.PI ? arg - 2 * Math.PI : arg)
              : arg + 2 * Math.PI * Math.round((previous - arg) / (2 * Math.PI));
          previous = arg;
          double degrees = Math.toDegrees(arg);
          magnitude.append(F.list(F.num(omega), F.num(db)));
          phase.append(F.list(F.num(omega), F.num(degrees)));
          curve.append(kind == NYQUIST ? F.list(F.num(g.getReal()), F.num(g.getImaginary()))
              : F.list(F.num(degrees), F.num(db)));
          mirror.append(F.list(F.num(g.getReal()), F.num(-g.getImaginary())));
        }
        magnitudes.append(magnitude);
        phases.append(phase);
        curves.append(curve);
        if (kind == NYQUIST) {
          curves.append(mirror);
        }
      }
      if (kind == BODE) {
        return F.unaryAST1(S.Grid, F.list(F.list(listPlot(S.ListLogLinearPlot, magnitudes, options, engine)),
            F.list(listPlot(S.ListLogLinearPlot, phases, options, engine))));
      }
      return listPlot(S.ListLinePlot, curves, options, engine);
    }

    private static IExpr listPlot(ISymbol head, IAST data, IAST options, EvalEngine engine) {
      IASTAppendable plot = F.ast(head);
      plot.append(data);
      plot.append(F.Rule(S.Joined, S.True));
      plot.appendArgs(options);
      return engine.evaluate(plot);
    }

    @Override
    public int status() {
      return ImplementationStatus.PARTIAL_SUPPORT;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }
  }

  /** Add the numeric elements of a model; <code>false</code> if it has a non-numeric one. */
  private static boolean addElements(IExpr expr, java.util.List<NumericElement> elements,
      EvalEngine engine) {
    Model model = Model.of(expr, engine);
    if (model == null) {
      return false;
    }
    IExpr periodExpr = model.samplingPeriod();
    double period = periodExpr.equals(S.None) ? 0.0 : periodExpr.evalfNaN();
    for (int i = 0; i < model.rows(); i++) {
      for (int j = 0; j < model.cols(); j++) {
        double[] num = coefficients(model.num[i][j], model.var, engine);
        double[] den = coefficients(model.den[i][j], model.var, engine);
        double tau = model.delay[i][j].evalfNaN();
        if (num == null || den == null || !Double.isFinite(tau) || !(period >= 0)) {
          return false;
        }
        elements.add(new NumericElement(num, den, tau, period));
      }
    }
    return true;
  }

  /**
   * A decade and a third either side of the corner frequencies (the moduli of the poles and zeros),
   * from <code>0.001</code> when one lies at the origin, <code>{0.1, 10}</code> without any; a
   * sampled model up to its Nyquist frequency <code>Pi/T</code>.
   */
  private static double[] automaticRange(java.util.List<NumericElement> elements) {
    double low = Double.POSITIVE_INFINITY;
    double high = 0.0;
    boolean origin = false;
    double nyquist = Double.POSITIVE_INFINITY;
    for (NumericElement element : elements) {
      if (element.period > 0) {
        nyquist = Math.min(nyquist, Math.PI / element.period);
        continue;
      }
      for (double[] p : new double[][] {element.num, element.den}) {
        for (double modulus : rootModuli(p)) {
          if (modulus < 1e-10) {
            origin = true;
          } else {
            low = Math.min(low, modulus / 20);
            high = Math.max(high, modulus * 20);
          }
        }
      }
    }
    if (high == 0.0) {
      low = 0.1;
      high = 10.0;
    }
    if (origin) {
      low = Math.min(low, 0.001);
    }
    if (nyquist < Double.POSITIVE_INFINITY) {
      high = nyquist;
      low = Math.min(low, nyquist / 1000);
    }
    return new double[] {low, high};
  }

  public static void initialize() {
    Initializer.init();
  }

  private ControlSystems() {}
}
