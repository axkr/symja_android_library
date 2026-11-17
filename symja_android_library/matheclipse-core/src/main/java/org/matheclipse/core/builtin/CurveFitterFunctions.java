package org.matheclipse.core.builtin;

import org.matheclipse.core.annotation.AllowNIL;
import java.util.Arrays;
import java.util.List;
import org.hipparchus.analysis.ParametricUnivariateFunction;
import org.hipparchus.exception.MathIllegalArgumentException;
import org.hipparchus.fitting.AbstractCurveFitter;
import org.hipparchus.fitting.SimpleCurveFitter;
import org.hipparchus.fitting.WeightedObservedPoint;
import org.hipparchus.fitting.WeightedObservedPoints;
import org.hipparchus.linear.Array2DRowRealMatrix;
import org.hipparchus.linear.ArrayRealVector;
import org.hipparchus.linear.DecompositionSolver;
import org.hipparchus.linear.MatrixUtils;
import org.hipparchus.linear.QRDecomposition;
import org.hipparchus.linear.RealMatrix;
import org.hipparchus.linear.RealVector;
import org.hipparchus.linear.SingularValueDecomposition;
import org.hipparchus.optim.nonlinear.vector.leastsquares.LeastSquaresBuilder;
import org.hipparchus.optim.nonlinear.vector.leastsquares.LeastSquaresProblem;
import org.hipparchus.optim.nonlinear.vector.leastsquares.LevenbergMarquardtOptimizer;
import org.hipparchus.optim.nonlinear.vector.leastsquares.MultivariateJacobianFunction;
import org.hipparchus.util.Pair;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.FittedModelExpr;
import org.matheclipse.core.expression.data.GeneralizedFittedModelExpr;
import org.matheclipse.core.expression.data.NonlinearFittedModelExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMatcher;

public class CurveFitterFunctions {

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.FindFit.setEvaluator(new FindFit());
      S.Fit.setEvaluator(new Fit());
      S.GeneralizedLinearModelFit.setEvaluator(new GeneralizedLinearModelFit());
      S.LinearModelFit.setEvaluator(new LinearModelFit());
      S.LogitModelFit.setEvaluator(new BinomialModelFit("LogitLink"));
      S.ProbitModelFit.setEvaluator(new BinomialModelFit("ProbitLink"));
      S.DiscreteIndicator.setEvaluator(new DiscreteIndicator());
      S.NonlinearModelFit.setEvaluator(new NonlinearModelFit());
    }
  }

  /**
   * The constraints <code>cons</code> of a model <code>{form, cons}</code> on its parameters.
   *
   * <p>
   * A bound of a single parameter, <code>q &lt; 1</code> or <code>0 &lt;= a &lt;= 2</code>, is
   * kept by fitting an unbounded parameter <code>s</code> instead, which the bounds are a smooth
   * function of: <code>q == 2-Sqrt(1+s^2)</code> takes every value up to <code>1</code>. Every
   * other inequality, like <code>a + b &lt; 1</code>, adds the residual
   * <code>weight*Max(0, a+b-1)</code> to the fit, with a weight which grows until the result
   * keeps the inequality; what is left outside is moved back to the boundary on the line to the
   * start. Equations and other conditions are not supported.
   */
  static final class ParameterConstraints {
    /** The halvings of a step which leaves the region of the constraints. */
    private static final int MAX_STEP_HALVINGS = 40;

    private final IAST parameters;

    /** The lower bounds, <code>-Infinity</code> if a parameter has none. */
    private final double[] lower;

    /** The upper bounds, <code>Infinity</code> if a parameter has none. */
    private final double[] upper;

    /** The constraints which are no bounds of a single parameter. */
    private final IASTAppendable others;

    private ParameterConstraints(IAST parameters) {
      this.parameters = parameters;
      final int n = parameters.argSize();
      this.lower = new double[n];
      this.upper = new double[n];
      Arrays.fill(lower, Double.NEGATIVE_INFINITY);
      Arrays.fill(upper, Double.POSITIVE_INFINITY);
      this.others = F.ListAlloc();
    }

    /**
     * The constraints <code>cons</code> on the <code>parameters</code>.
     *
     * @return <code>null</code> if a constraint isn't a relation of the parameters or the bounds
     *         leave no value of a parameter
     */
    static ParameterConstraints of(IExpr constraints, IAST parameters, EvalEngine engine) {
      ParameterConstraints result = new ParameterConstraints(parameters);
      if (!result.add(constraints, engine)) {
        return null;
      }
      for (int i = 0; i < result.lower.length; i++) {
        if (!(result.lower[i] < result.upper[i])) {
          return null;
        }
      }
      return result;
    }

    private boolean add(IExpr constraint, EvalEngine engine) {
      if (constraint.isTrue()) {
        return true;
      }
      if (constraint.isAnd() || constraint.isList()) {
        for (IExpr arg : (IAST) constraint) {
          if (!add(arg, engine)) {
            return false;
          }
        }
        return true;
      }
      if (constraint.isAST(S.Inequality) && constraint.argSize() >= 3
          && constraint.argSize() % 2 == 1) {
        IAST inequality = (IAST) constraint;
        for (int i = 1; i + 2 <= inequality.argSize(); i += 2) {
          IExpr relation = inequality.get(i + 1);
          if (!(relation.isSymbol() && addRelation((ISymbol) relation, inequality.get(i),
              inequality.get(i + 2), engine))) {
            return false;
          }
        }
        return true;
      }
      if ((constraint.isAST(S.Less) || constraint.isAST(S.LessEqual) || constraint.isAST(S.Greater)
          || constraint.isAST(S.GreaterEqual)) && constraint.argSize() >= 2) {
        IAST relation = (IAST) constraint;
        for (int i = 1; i < relation.argSize(); i++) {
          if (!addRelation((ISymbol) relation.head(), relation.get(i), relation.get(i + 1),
              engine)) {
            return false;
          }
        }
        return true;
      }
      // an equation or another condition is not supported
      return false;
    }

    /** <code>lhs relation rhs</code>, a bound if one side is a parameter and the other a number. */
    private boolean addRelation(ISymbol relation, IExpr lhs, IExpr rhs, EvalEngine engine) {
      final boolean less = relation == S.Less || relation == S.LessEqual;
      final boolean greater = relation == S.Greater || relation == S.GreaterEqual;
      if (!less && !greater) {
        return false;
      }
      int index = parameters.indexOf(lhs);
      IExpr bound = rhs;
      boolean isUpper = less;
      if (index < 0) {
        index = parameters.indexOf(rhs);
        bound = lhs;
        isUpper = greater;
      }
      if (index > 0 && bound.isRealResult()) {
        double value = engine.evalDouble(bound);
        if (!Double.isNaN(value)) {
          if (isUpper) {
            upper[index - 1] = Math.min(upper[index - 1], value);
          } else {
            lower[index - 1] = Math.max(lower[index - 1], value);
          }
          return true;
        }
      }
      return addOther(less ? F.Subtract(lhs, rhs) : F.Subtract(rhs, lhs));
    }

    /**
     * A constraint <code>excess &lt;= 0</code>, which is evaluated at the points of the fit.
     *
     * @return <code>false</code> if <code>excess</code> depends on more than the parameters
     */
    private boolean addOther(IExpr excess) {
      if (!excess.isFree(x -> x.isSymbol() && !x.isBuiltInSymbol() && !x.isConstantAttribute()
          && !parameters.contains(x), true)) {
        return false;
      }
      others.append(excess);
      return true;
    }

    /** The value of parameter <code>i</code> for the unbounded value <code>s</code>. */
    private double external(int i, double s) {
      final double lo = lower[i];
      final double hi = upper[i];
      if (lo == Double.NEGATIVE_INFINITY) {
        return hi == Double.POSITIVE_INFINITY ? s : hi + 1.0 - Math.sqrt(s * s + 1.0);
      }
      if (hi == Double.POSITIVE_INFINITY) {
        return lo - 1.0 + Math.sqrt(s * s + 1.0);
      }
      return lo + 0.5 * (hi - lo) * (Math.sin(s) + 1.0);
    }

    /** The derivative of {@link #external(int, double)} by <code>s</code>. */
    private double derivative(int i, double s) {
      final double lo = lower[i];
      final double hi = upper[i];
      if (lo == Double.NEGATIVE_INFINITY) {
        return hi == Double.POSITIVE_INFINITY ? 1.0 : -s / Math.sqrt(s * s + 1.0);
      }
      if (hi == Double.POSITIVE_INFINITY) {
        return s / Math.sqrt(s * s + 1.0);
      }
      return 0.5 * (hi - lo) * Math.cos(s);
    }

    /**
     * The unbounded value of parameter <code>i</code> with the value <code>p</code>. A value on or
     * beyond a bound is moved inside first, where the parameter can still move both ways.
     */
    private double internal(int i, double p) {
      final double lo = lower[i];
      final double hi = upper[i];
      if (lo == Double.NEGATIVE_INFINITY && hi == Double.POSITIVE_INFINITY) {
        return p;
      }
      if (lo == Double.NEGATIVE_INFINITY || hi == Double.POSITIVE_INFINITY) {
        final double bound = lo == Double.NEGATIVE_INFINITY ? hi : lo;
        double distance = Math.abs(p - bound);
        if (!(lo == Double.NEGATIVE_INFINITY ? p < hi : p > lo)) {
          distance = 0.1 * Math.max(1.0, Math.abs(bound));
        }
        // the inverse of distance == Sqrt(1+s^2)-1
        return Math.sqrt(distance * (distance + 2.0));
      }
      double t = 2.0 * (p - lo) / (hi - lo) - 1.0;
      if (!(t > -1.0 && t < 1.0)) {
        t = t > 0.0 ? 0.9 : -0.9;
      }
      return Math.asin(t);
    }

    double[] toExternal(double[] s) {
      final double[] p = new double[s.length];
      for (int i = 0; i < s.length; i++) {
        p[i] = external(i, s[i]);
      }
      return p;
    }

    double[] toInternal(double[] p) {
      final double[] s = new double[p.length];
      for (int i = 0; i < p.length; i++) {
        s[i] = internal(i, p[i]);
      }
      return s;
    }

    /** The derivatives of the parameters by their unbounded values. */
    double[] derivatives(double[] s) {
      final double[] d = new double[s.length];
      for (int i = 0; i < s.length; i++) {
        d[i] = derivative(i, s[i]);
      }
      return d;
    }

    /** Whether the constraints which are no bounds hold for the parameter values <code>p</code>. */
    boolean holds(double[] p, EvalEngine engine) {
      for (double excess : excesses(p, engine)) {
        if (!(excess <= 0.0)) {
          return false;
        }
      }
      return true;
    }

    /**
     * The values <code>g(p)</code> of the constraints <code>g &lt;= 0</code> which are no bounds,
     * <code>NaN</code> for a value which isn't a real number.
     */
    double[] excesses(double[] p, EvalEngine engine) {
      final double[] result = new double[others.argSize()];
      if (result.length == 0) {
        return result;
      }
      IASTAppendable rules = F.ListAlloc(p.length);
      for (int i = 0; i < p.length; i++) {
        rules.append(F.Rule(parameters.get(i + 1), F.num(p[i])));
      }
      for (int k = 0; k < result.length; k++) {
        result[k] = F.subst(others.get(k + 1), rules).evalfNaN();
      }
      return result;
    }

    /** The number of constraints which are no bounds. */
    int othersSize() {
      return others.argSize();
    }

    /**
     * The bound which each parameter of <code>p</code> has reached, <code>NaN</code> for a
     * parameter which is inside its bounds. The smooth function of the unbounded value has the
     * derivative 0 at a bound, where the fit only creeps towards it.
     */
    double[] reachedBounds(double[] p) {
      final double[] result = new double[p.length];
      for (int i = 0; i < p.length; i++) {
        result[i] = Double.NaN;
        for (double bound : new double[] {lower[i], upper[i]}) {
          if (Double.isFinite(bound)
              && Math.abs(p[i] - bound) <= BOUND_REACHED * Math.max(1.0, Math.abs(bound))) {
            result[i] = bound;
          }
        }
      }
      return result;
    }

    /** The relative distance to a bound, which counts as reaching it. */
    private static final double BOUND_REACHED = 1.0e-5;

    /**
     * The point <code>s</code> of unbounded values, or the furthest point towards it from
     * <code>valid</code> where the constraints hold.
     */
    double[] restrict(double[] s, double[] valid, EvalEngine engine) {
      if (holds(toExternal(s), engine)) {
        return s;
      }
      double lo = 0.0;
      double hi = 1.0;
      final double[] point = new double[s.length];
      for (int k = 0; k < MAX_STEP_HALVINGS; k++) {
        final double mid = 0.5 * (lo + hi);
        for (int i = 0; i < s.length; i++) {
          point[i] = valid[i] + mid * (s[i] - valid[i]);
        }
        if (holds(toExternal(point), engine)) {
          lo = mid;
        } else {
          hi = mid;
        }
      }
      for (int i = 0; i < s.length; i++) {
        point[i] = valid[i] + lo * (s[i] - valid[i]);
      }
      return point;
    }
  }

  /**
   *
   *
   * <pre>
   * FindFit(list - of - data - points, function, parameters, variable)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * solve a least squares problem using the Levenberg-Marquardt algorithm.
   *
   * </blockquote>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Levenberg%E2%80%93Marquardt_algorithm">Wikipedia -
   * Levenberg–Marquardt algorithm</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; FindFit({{15.2,8.9},{31.1,9.9},{38.6,10.3},{52.2,10.7},{75.4,11.4}}, a*Log(b*x), {a, b}, x)
   * {a-&gt;1.54503,b-&gt;20.28258}
   *
   * &gt;&gt; FindFit({{1,1},{2,4},{3,9},{4,16}}, a+b*x+c*x^2, {a, b, c}, x)
   * {a-&gt;0.0,b-&gt;0.0,c-&gt;1.0}
   * </pre>
   *
   * <p>
   * The default initial guess in the following example for the parameters <code>{a,w,f}</code> is
   * <code>{1.0, 1.0, 1.0}</code>. These initial values give a bad result:
   *
   * <pre>
   * &gt;&gt; FindFit(Table({t, 3*Sin(3*t + 1)}, {t, -3, 3, 0.1}), a* Sin(w*t + f), {a,w,f}, t)
   * {a-&gt;0.6688,w-&gt;1.49588,f-&gt;3.74845}
   * </pre>
   *
   * <p>
   * The initial guess <code>{2.0, 1.0, 1.0}</code> gives a much better result:
   *
   * <pre>
   * &gt;&gt; FindFit(Table({t, 3*Sin(3*t + 1)}, {t, -3, 3, 0.1}), a* Sin(w*t + f), {{a, 2}, {w,1}, {f,1}}, t)
   * {a-&gt;3.0,w-&gt;3.0,f-&gt;1.0}
   * </pre>
   *
   * <p>
   * You can omit <code>1.0</code> in the parameter list because it's the default value:
   *
   * <pre>
   * &gt;&gt; FindFit(Table({t, 3*Sin(3*t + 1)}, {t, -3, 3, 0.1}), a* Sin(w*t + f), {{a, 2}, w, f}, t)
   * {a-&gt;3.0,w-&gt;3.0,f-&gt;1.0}
   * </pre>
   */
  private static class FindFit extends AbstractFunctionOptionEvaluator {

    private static class FindFitParametricFunction implements ParametricUnivariateFunction {
      final EvalEngine engine;
      final IExpr function;
      IAST gradientList;
      IASTAppendable listOfRules;

      public FindFitParametricFunction(final IExpr function, final IAST gradientList,
          final IAST listOfSymbols, final IExpr x, final EvalEngine engine) {
        this.function = function;
        this.engine = engine;
        this.gradientList = gradientList;
        this.listOfRules = F.ListAlloc(gradientList.size());
        this.listOfRules.append(F.Rule(x, S.Null));
        listOfSymbols.forEach(arg -> this.listOfRules.append(F.Rule(arg, S.Null)));
      }

      private void createSubstitutionRules(double t, double... parameters) {
        IASTMutable substitutionRules = (IASTMutable) this.listOfRules.arg1();
        substitutionRules.set(2, F.num(t));
        for (int i = 2; i < listOfRules.size(); i++) {
          substitutionRules = (IASTMutable) this.listOfRules.get(i);
          substitutionRules.set(2, F.num(parameters[i - 2]));
        }
      }

      @Override
      public double[] gradient(double t, double... parameters) {
        createSubstitutionRules(t, parameters);
        final double[] gradient = new double[parameters.length];
        boolean numeric = true;
        for (int i = 0; i < parameters.length; i++) {
          gradient[i] = F.subst(gradientList.get(i + 1), listOfRules).evalfNaN();
          numeric &= Double.isFinite(gradient[i]);
        }
        if (!numeric) {
          // a model which is only defined for numeric arguments, f(a_?NumericQ)(t_?NumericQ),
          // has no derivative which can be evaluated: use central differences instead
          for (int i = 0; i < parameters.length; i++) {
            if (!Double.isFinite(gradient[i])) {
              final double[] shifted = parameters.clone();
              final double h = differenceStep(parameters[i]);
              shifted[i] = parameters[i] + h;
              final double upper = value(t, shifted);
              shifted[i] = parameters[i] - h;
              final double lower = value(t, shifted);
              gradient[i] = (upper - lower) / (2.0 * h);
            }
          }
          createSubstitutionRules(t, parameters);
        }
        return gradient;
      }

      /** {@inheritDoc} */
      @Override
      public double value(final double t, final double... parameters)
          throws MathIllegalArgumentException {
        createSubstitutionRules(t, parameters);
        return engine.evalDouble(F.subst(function, listOfRules));
      }
    }

    /**
     * The width of the central difference for a parameter with the value <code>p</code>: the cube
     * root of the machine epsilon, relative to the size of <code>p</code>.
     */
    static double differenceStep(double p) {
      return 6.0e-6 * Math.max(1.0, Math.abs(p));
    }

    /**
     * Evaluate the data to double values and add it to the observed points.
     *
     * @param data
     * @param obs
     * @return
     */
    protected static boolean addWeightedObservedPoints(IAST data, WeightedObservedPoints obs) {
      int[] isMatrix = data.isMatrix();
      if (isMatrix != null && isMatrix[1] == 2) {
        final double[][] elements = data.toDoubleMatrix(true);
        if (elements == null) {
          return false;
        }
        for (int i = 0; i < elements.length; i++) {
          obs.add(1.0, elements[i][0], elements[i][1]);
        }
      } else {
        int rowSize = data.isVector();
        if (rowSize < 0) {
          return false;
        }
        final double[] elements = data.toDoubleVector();
        if (elements == null) {
          return false;
        }
        for (int i = 0; i < elements.length; i++) {
          obs.add(1.0, i + 1, elements[i]);
        }
      }
      return true;
    }

    @Override
    public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
        IAST originalAST) {
      return numericEval(ast, argSize, options, engine, originalAST);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_4_4;
    }

    /**
     * Determine the initial guess. Default is <code>[1.0, 1.0, 1.0,...]</code>
     *
     * @param listOfSymbolsOrPairs
     * @param initialGuess
     * @return <code>F.NIL</code> if the list of symbols couldn't be determined
     */
    protected static IAST initialGuess(IAST listOfSymbolsOrPairs, double[] initialGuess) {
      return F.mapList(listOfSymbolsOrPairs, (temp, i) -> {
        if (temp.isVariable()) {
          initialGuess[i - 1] = 1.0;
          return temp;
        } else if ((temp.isList2() || temp.isList3()) && temp.first().isSymbol()) {
          // {a, a0} or {a, a0, a1}: the search starts at a0
          final IReal realNumber = temp.second().evalReal();
          if (realNumber != null) {
            initialGuess[i - 1] = realNumber.doubleValue();
          } else {
            return null;
          }
          return temp.first();
        }
        return F.NIL;
      });
    }

    /**
     * Replace the parameters which are no symbols, like <code>s(1)</code>, by unique symbols in the
     * parameter specification <code>a</code> or <code>{a, a0}</code>.
     *
     * @param listOfSymbolsOrPairs the parameter specification
     * @param renamed the rules <code>s(1) -> $1</code> are appended to this list
     * @return the parameter specification with the replaced parameters
     */
    protected static IAST renameParameters(IAST listOfSymbolsOrPairs, IASTAppendable renamed) {
      return F.mapList(listOfSymbolsOrPairs, (IExpr spec) -> {
        IExpr parameter = spec.isList() && spec.argSize() > 0 ? spec.first() : spec;
        if (!parameter.isAST() || parameter.isList()) {
          return spec;
        }
        ISymbol dummy = F.Dummy();
        renamed.append(F.Rule(parameter, dummy));
        return spec.isList() ? ((IAST) spec).setAtCopy(1, dummy) : dummy;
      });
    }

    /**
     * The model <code>{form}</code> is the model <code>form</code> without constraints.
     */
    protected static IExpr modelForm(IExpr model) {
      return model.isList() && model.argSize() >= 1 ? model.first() : model;
    }

    /**
     * The constraints <code>cons</code> of the model <code>{form, cons}</code>.
     *
     * @return {@link F#NIL} if the model has no constraints
     */
    protected static @AllowNIL IExpr modelConstraints(IExpr model) {
      if (!model.isList() || model.argSize() < 2) {
        return F.NIL;
      }
      return model.argSize() == 2 ? model.second() : ((IAST) model).rest().setAtCopy(0, S.And);
    }

    /**
     * Fit the model with the constraints on its parameters.
     *
     * @return <code>null</code> if the constraints aren't relations of the parameters, the data
     *         are no rows of numbers, or the constraints don't hold at the start
     */
    static double[] fitConstrained(IAST data, IExpr function, IExpr constraints, IAST symbols,
        IAST variables, double[] initialGuess, EvalEngine engine) {
      IASTAppendable equations = F.ListAlloc();
      IASTAppendable relations = F.ListAlloc();
      collectConstraints(constraints, equations, relations);
      if (equations.argSize() > 0) {
        return fitWithEquations(data, function, equations, relations, symbols, variables,
            initialGuess, engine);
      }
      ParameterConstraints parameterConstraints =
          ParameterConstraints.of(constraints, symbols, engine);
      if (parameterConstraints == null) {
        return null;
      }
      IAST rows = Fit.buildDataMatrix(data, variables.argSize());
      if (rows.isNIL()) {
        return null;
      }
      double[] values = fitMultivariate(rows, function, symbols, variables, initialGuess,
          parameterConstraints, engine);
      if (values == null) {
        return null;
      }
      // the parameters which reached a bound are set to it, and the others fitted once more
      final double[] reached = parameterConstraints.reachedBounds(values);
      IASTAppendable fixed = F.ListAlloc();
      IASTAppendable free = F.ListAlloc();
      int[] freeIndex = new int[values.length];
      for (int i = 0; i < values.length; i++) {
        if (Double.isNaN(reached[i])) {
          freeIndex[free.argSize()] = i;
          free.append(symbols.get(i + 1));
        } else {
          fixed.append(F.Rule(symbols.get(i + 1), F.num(reached[i])));
        }
      }
      if (fixed.argSize() == 0) {
        return values;
      }
      double[] polished = values.clone();
      for (int i = 0; i < values.length; i++) {
        if (!Double.isNaN(reached[i])) {
          polished[i] = reached[i];
        }
      }
      if (free.argSize() > 0) {
        ParameterConstraints freeConstraints =
            ParameterConstraints.of(F.subst(constraints, fixed), free, engine);
        if (freeConstraints == null) {
          return values;
        }
        double[] freeGuess = new double[free.argSize()];
        for (int k = 0; k < freeGuess.length; k++) {
          freeGuess[k] = values[freeIndex[k]];
        }
        double[] freeValues = fitMultivariate(rows, F.subst(function, fixed), free, variables,
            freeGuess, freeConstraints, engine);
        if (freeValues == null) {
          return values;
        }
        for (int k = 0; k < freeValues.length; k++) {
          polished[freeIndex[k]] = freeValues[k];
        }
      }
      return parameterConstraints.holds(polished, engine) ? polished : values;
    }

    /** Split the constraints into the equations and the other relations. */
    private static void collectConstraints(IExpr constraints, IASTAppendable equations,
        IASTAppendable relations) {
      if (constraints.isAnd() || constraints.isList()) {
        for (IExpr arg : (IAST) constraints) {
          collectConstraints(arg, equations, relations);
        }
      } else if (constraints.isEqual()) {
        equations.append(constraints);
      } else if (!constraints.isTrue()) {
        relations.append(constraints);
      }
    }

    /**
     * Fit the model with equations between its parameters: the equations are solved for some of
     * the parameters, which are replaced in the model and the other constraints, the remaining
     * parameters are fitted, and the eliminated ones follow from them.
     *
     * @return <code>null</code> if the equations aren't solved for parameters or the fit fails
     */
    private static double[] fitWithEquations(IAST data, IExpr function, IAST equations,
        IAST relations, IAST symbols, IAST variables, double[] initialGuess,
        EvalEngine engine) {
      IExpr solutions = engine.evalQuiet(F.Solve(equations, symbols));
      if (!solutions.isListOfLists() || solutions.argSize() < 1) {
        return null;
      }
      IAST rules = (IAST) solutions.first();
      IASTAppendable free = F.ListAlloc();
      IASTAppendable freeGuess = F.ListAlloc();
      for (int i = 1; i < symbols.size(); i++) {
        final IExpr symbol = symbols.get(i);
        if (!rules.exists(rule -> rule.isRuleAST() && rule.first().equals(symbol))) {
          free.append(symbol);
          freeGuess.append(F.num(initialGuess[i - 1]));
        }
      }
      if (!rules.forAll(rule -> rule.isRuleAST() && rule.second().isFree(
          x -> symbols.contains(x) && !free.contains(x), true))) {
        return null;
      }
      double[] freeValues = new double[0];
      if (free.argSize() > 0) {
        IExpr freeFunction = engine.evaluate(F.subst(function, rules));
        double[] guess = freeGuess.toDoubleVector();
        if (relations.argSize() > 0) {
          IExpr freeConstraints = engine.evaluate(F.subst(relations.setAtCopy(0, S.And), rules));
          freeValues = fitConstrained(data, freeFunction, freeConstraints, free, variables, guess,
              engine);
        } else {
          IAST rows = Fit.buildDataMatrix(data, variables.argSize());
          freeValues = rows.isNIL() ? null
              : fitMultivariate(rows, freeFunction, free, variables, guess, engine);
        }
        if (freeValues == null) {
          return null;
        }
      }
      IASTAppendable freeRules = F.ListAlloc(free.argSize());
      for (int k = 0; k < freeValues.length; k++) {
        freeRules.append(F.Rule(free.get(k + 1), F.num(freeValues[k])));
      }
      double[] values = new double[symbols.argSize()];
      for (int i = 1; i < symbols.size(); i++) {
        final IExpr symbol = symbols.get(i);
        IExpr value = symbol;
        for (IExpr rule : rules) {
          if (rule.first().equals(symbol)) {
            value = rule.second();
            break;
          }
        }
        IExpr number = engine.evalN(F.subst(value, freeRules));
        if (!number.isReal()) {
          return null;
        }
        values[i - 1] = number.evalf();
      }
      return values;
    }

    /**
     * Create the initial guess <code>[1.0, 1.0, 1.0,...]</code>
     *
     * @param length the length of the initial array
     * @param value the value every element of the array should be initialized
     */
    protected static double[] initialGuess(final int length, final double value) {
      double[] initialGuess = new double[length];
      for (int i = 0; i < initialGuess.length; i++) {
        initialGuess[i] = value;
      }
      return initialGuess;
    }

    /**
     * Fit <code>function</code> of <code>x</code> to the data by Levenberg-Marquardt, starting from
     * <code>initialGuess</code>.
     *
     * <p>
     * Shared by <code>FindFit</code>, which only wants the parameters, and
     * <code>NonlinearModelFit</code>, which also wants the data points the fit was made to - those
     * are added to <code>pointsOut</code> when it is given.
     *
     * @return the fitted parameter values, in the order of <code>symbols</code>, or
     *         <code>null</code> when the function has no gradient or the data are not numeric
     */
    static double[] fit(IAST data, IExpr function, IAST symbols, IExpr x, double[] initialGuess,
        java.util.List<org.hipparchus.fitting.WeightedObservedPoint> pointsOut,
        EvalEngine engine) {
      IExpr fitFunction = F.substAbs(function);
      IExpr gradientList = S.Grad.of(engine, fitFunction, symbols);
      if (!gradientList.isList()) {
        return null;
      }
      AbstractCurveFitter fitter = SimpleCurveFitter.create(new FindFitParametricFunction(
          fitFunction, (IAST) gradientList, symbols, x, engine), initialGuess);
      WeightedObservedPoints obs = new WeightedObservedPoints();
      if (!addWeightedObservedPoints(data, obs)) {
        return null;
      }
      if (pointsOut != null) {
        pointsOut.addAll(obs.toList());
      }
      return fitter.fit(obs.toList());
    }

    /**
     * A model which is linear in the parameters and whose parameters are not determined by the
     * data - there are fewer points than parameters, or the basis functions depend on each other
     * at the points - has the parameters of the least squares solution with the smallest norm in
     * the coordinates of the basis functions scaled to unit length, whatever the start values.
     *
     * @return <code>null</code> if the model is not linear in the parameters, the data are not
     *         numeric or the parameters are determined by the data
     */
    private static double[] minimumNormLinearFit(IAST data, IExpr model, IAST symbols,
        IAST variables, EvalEngine engine) {
      IASTAppendable basis = F.ListAlloc(symbols.argSize());
      for (IExpr parameter : symbols) {
        IExpr derivative = engine.evaluate(F.D(model, parameter));
        if (!derivative.isFree(x -> symbols.contains(x), false)) {
          return null;
        }
        basis.append(derivative);
      }
      IAST dataMatrix = Fit.buildDataMatrix(data, variables.argSize());
      if (dataMatrix.isNIL()) {
        return null;
      }
      final double[][] points = dataMatrix.toDoubleMatrix(false);
      if (points == null) {
        return null;
      }
      final int n = variables.argSize();
      IExpr offset = engine.evaluate(F.subst(model, F.mapList(symbols, p -> F.Rule(p, F.C0))));
      double[] response = new double[points.length];
      try {
        for (int i = 0; i < points.length; i++) {
          response[i] = points[i][n] - engine.evalDouble(engine.evaluate(
              F.subst(offset, Fit.pointRules(variables, (IAST) dataMatrix.get(i + 1)))));
        }
        return Fit.scaledLeastSquares(Fit.designMatrix(dataMatrix, basis, variables, engine),
            response, true);
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        // a basis function without a numeric value: the iteration reports it
        return null;
      }
    }

    /**
     * The model variables <code>x</code> or <code>{x, y, ...}</code> as a list.
     *
     * @return {@link F#NIL} if <code>arg</code> is no variable and no list of variables
     */
    protected static IAST fitVariables(IExpr arg) {
      if (arg.isVariable()) {
        return F.list(arg);
      }
      if (arg.isList() && arg.argSize() > 0 && ((IAST) arg).forAll(x -> x.isVariable())) {
        return (IAST) arg;
      }
      return F.NIL;
    }

    /**
     * Fit <code>function</code> of several <code>variables</code> to the rows <code>{x, y, ...,
     * f}</code> of the data by Levenberg-Marquardt, starting from <code>initialGuess</code>.
     *
     * @return the fitted parameter values, in the order of <code>symbols</code>, or
     *         <code>null</code> when the function has no gradient or the data are not numeric rows
     *         with one more element than there are variables
     */
    static double[] fitMultivariate(IAST data, IExpr function, IAST symbols,
        IAST variables, double[] initialGuess, EvalEngine engine) {
      return fitMultivariate(data, function, symbols, variables, initialGuess, null, engine);
    }

    /**
     * See {@link #fitMultivariate(IAST, IExpr, IAST, IAST, double[], EvalEngine)}.
     *
     * @param constraints the constraints on the parameters, or <code>null</code>
     * @return <code>null</code> also if the constraints don't hold at the start or the result
     */
    static double[] fitMultivariate(IAST data, IExpr function, IAST symbols,
        IAST variables, double[] initialGuess, ParameterConstraints constraints,
        EvalEngine engine) {
      final int n = variables.argSize();
      int[] dim = data.isMatrix();
      if (dim == null || dim[1] != n + 1) {
        return null;
      }
      final double[][] rows = data.toDoubleMatrix(true);
      if (rows == null) {
        return null;
      }
      final IExpr fitFunction = F.substAbs(function);
      IExpr gradientList = S.Grad.of(engine, fitFunction, symbols);
      if (!gradientList.isList()) {
        return null;
      }
      final IAST gradient = (IAST) gradientList;
      final int parameters = symbols.argSize();
      // the rules variable -> value and parameter -> value, whose values are set for each row
      final IASTAppendable rules = F.ListAlloc(n + parameters);
      variables.forEach(v -> rules.append(F.Rule(v, S.Null)));
      symbols.forEach(p -> rules.append(F.Rule(p, S.Null)));
      // the constraints g <= 0 which are no bounds add the residuals weight*Max(0, g)
      final int penalties = constraints == null ? 0 : constraints.othersSize();
      final double[] target = new double[rows.length + penalties];
      double scale = 0.0;
      for (int i = 0; i < rows.length; i++) {
        target[i] = rows[i][n];
        scale = Math.max(scale, Math.abs(target[i]));
      }
      final double[] weight = {Math.max(1.0, scale)};
      MultivariateJacobianFunction model = unbounded -> {
        // the parameters of the model, and their derivatives by the values which are fitted
        final double[] point;
        final double[] chain;
        if (constraints == null) {
          point = unbounded.toArray();
          chain = null;
        } else {
          point = constraints.toExternal(unbounded.toArray());
          chain = constraints.derivatives(unbounded.toArray());
        }
        for (int j = 0; j < parameters; j++) {
          ((IASTMutable) rules.get(n + j + 1)).set(2, F.num(point[j]));
        }
        double[] values = new double[rows.length + penalties];
        double[][] jacobian = new double[rows.length + penalties][parameters];
        if (penalties > 0) {
          penaltyResiduals(constraints, unbounded.toArray(), weight[0], values, jacobian,
              rows.length, engine);
        }
        for (int i = 0; i < rows.length; i++) {
          for (int k = 0; k < n; k++) {
            ((IASTMutable) rules.get(k + 1)).set(2, F.num(rows[i][k]));
          }
          values[i] = engine.evalDouble(F.subst(fitFunction, rules));
          for (int j = 0; j < parameters; j++) {
            jacobian[i][j] = F.subst(gradient.get(j + 1), rules).evalfNaN();
          }
          for (int j = 0; j < parameters; j++) {
            if (!Double.isFinite(jacobian[i][j])) {
              // no derivative which can be evaluated: a central difference
              final IASTMutable rule = (IASTMutable) rules.get(n + j + 1);
              final double p = point[j];
              final double h = differenceStep(p);
              rule.set(2, F.num(p + h));
              final double upper = engine.evalDouble(F.subst(fitFunction, rules));
              rule.set(2, F.num(p - h));
              final double lower = engine.evalDouble(F.subst(fitFunction, rules));
              rule.set(2, F.num(p));
              jacobian[i][j] = (upper - lower) / (2.0 * h);
            }
            if (chain != null) {
              jacobian[i][j] *= chain[j];
            }
          }
        }
        engine.checkTimeLimit();
        return new Pair<>(new ArrayRealVector(values, false),
            new Array2DRowRealMatrix(jacobian, false));
      };
      final double[] start =
          constraints == null ? initialGuess : constraints.toInternal(initialGuess);
      double[] result = start;
      // with constraints which are no bounds the weight of their residuals grows from round to
      // round, each round starting where the last one ended
      final int rounds = penalties == 0 ? 1 : PENALTY_ROUNDS;
      for (int round = 0; round < rounds; round++) {
        LeastSquaresProblem problem = new LeastSquaresBuilder() //
            .maxEvaluations(Integer.MAX_VALUE) //
            .maxIterations(Integer.MAX_VALUE) //
            .start(result) //
            .target(target) //
            .model(model) //
            .build();
        result = new LevenbergMarquardtOptimizer().optimize(problem).getPoint().toArray();
        if (penalties > 0 && constraints.holds(constraints.toExternal(result), engine)) {
          break;
        }
        weight[0] *= PENALTY_GROWTH;
      }
      if (constraints == null) {
        return result;
      }
      if (penalties > 0 && !constraints.holds(constraints.toExternal(result), engine)) {
        // what the weights leave outside of the region: the point of the boundary between the
        // start and the result
        if (!constraints.holds(constraints.toExternal(start), engine)) {
          return null;
        }
        result = constraints.restrict(result, start, engine);
      }
      result = constraints.toExternal(result);
      return constraints.holds(result, engine) ? result : null;
    }

    /** The rounds of a fit with constraints which are no bounds. */
    private static final int PENALTY_ROUNDS = 6;

    /** The factor of the weight of the constraint residuals from one round to the next. */
    private static final double PENALTY_GROWTH = 100.0;

    /**
     * The residuals <code>weight*Max(0, g)</code> of the constraints <code>g &lt;= 0</code> which
     * are no bounds, and their derivatives by the unbounded parameters, by central differences.
     *
     * @param offset the row of the first residual
     */
    private static void penaltyResiduals(ParameterConstraints constraints, double[] unbounded,
        double weight, double[] values, double[][] jacobian, int offset, EvalEngine engine) {
      final double[] excesses = constraints.excesses(constraints.toExternal(unbounded), engine);
      for (int k = 0; k < excesses.length; k++) {
        values[offset + k] = excesses[k] > 0.0 ? weight * excesses[k] : 0.0;
      }
      final double[] shifted = unbounded.clone();
      for (int j = 0; j < unbounded.length; j++) {
        final double h = differenceStep(unbounded[j]);
        shifted[j] = unbounded[j] + h;
        final double[] upper = constraints.excesses(constraints.toExternal(shifted), engine);
        shifted[j] = unbounded[j] - h;
        final double[] lower = constraints.excesses(constraints.toExternal(shifted), engine);
        shifted[j] = unbounded[j];
        for (int k = 0; k < excesses.length; k++) {
          jacobian[offset + k][j] =
              excesses[k] > 0.0 ? weight * (upper[k] - lower[k]) / (2.0 * h) : 0.0;
        }
      }
    }

    public @AllowNIL IExpr numericEval(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
        IAST originalAST) {
      IExpr workingPrecision = options[0];
      final IAST variables = fitVariables(ast.arg4());
      if (ast.arg1().isList() && ast.arg3().isList() && variables.isPresent()) {
        IAST data = (IAST) ast.arg1();
        IExpr function = modelForm(ast.arg2());
        IExpr constraints = modelConstraints(ast.arg2());
        IASTAppendable renamed = F.ListAlloc();
        IAST listOfSymbols = renameParameters((IAST) ast.arg3(), renamed);
        if (renamed.argSize() > 0) {
          function = F.subst(function, renamed);
          constraints = F.subst(constraints, renamed);
        }

        double[] initialGuess = new double[listOfSymbols.argSize()];
        listOfSymbols = initialGuess(listOfSymbols, initialGuess);
        if (listOfSymbols.isPresent()) {
          if (data.isEmpty()) {
            if (ast.isAST(S.FindFit)) {
              // no data: every parameter fits as 0
              final IAST zeros = F.mapList(listOfSymbols, (IExpr symbol) -> F.Rule(symbol, F.C0));
              return renamed.argSize() > 0 ? F.subst(zeros, F.mapList(renamed,
                  (IExpr rule) -> F.Rule(rule.second(), rule.first()))) : zeros;
            }
            // a model of no data stays unevaluated
            return F.NIL;
          }
          try {
            final IAST symbols = listOfSymbols;
            final IExpr model = function;
            final double[] values;
            if (constraints.isPresent()) {
              values = fitConstrained(data, model, constraints, symbols, variables,
                  initialGuess, engine);
            } else {
              double[] linear = workingPrecision == S.Automatic
                  ? minimumNormLinearFit(data, model, symbols, variables, engine)
                  : null;
              values = linear != null ? linear
                  : variables.argSize() == 1
                      ? fit(data, model, symbols, variables.arg1(), initialGuess, null, engine)
                      : fitMultivariate(data, model, symbols, variables, initialGuess, engine);
            }
            if (values != null) {
              IAST rules =
                  F.mapList(symbols, (symbol, i) -> F.Rule(symbol, F.num(values[i - 1])));
              // the dummy symbols back to the parameters they replaced
              return renamed.argSize() > 0 ? F.subst(rules, F.mapList(renamed,
                  (IExpr rule) -> F.Rule(rule.second(), rule.first()))) : rules;
            }
          } catch (ValidateException ve) {
            return Errors.printMessage(ast.topHead(), ve, engine);
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            return Errors.printMessage(S.FindFit, rex, engine);
          }
        }
      }
      return F.NIL;
    }

    /**
     * The parameters and the variables are local: <code>x = 0.3; FindFit(data, a*x, {a}, x)</code>
     * fits <code>a*x</code> and not <code>0.3*a</code>.
     */
    @Override
    public boolean localIterators() {
      return true;
    }

    /** The parameters <code>a</code> or <code>{a, a0}</code> and the variables. */
    @Override
    protected IAST localVariables(IAST held) {
      IASTAppendable variables = F.ListAlloc();
      if (held.argSize() >= 4) {
        for (IExpr parameter : held.arg3().makeList()) {
          appendLocalVariable(parameter.isList() && parameter.argSize() > 0 ? parameter.first()
              : parameter, variables);
        }
        for (IExpr variable : held.arg4().makeList()) {
          appendLocalVariable(variable, variables);
        }
      }
      return variables;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      setOptions(newSymbol, new IBuiltInSymbol[] {S.WorkingPrecision}, new IExpr[] {S.Automatic});
    }
  }

  /**
   *
   *
   * <pre>
   * Fit(list - of - data - points, functions, variable)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * solve a least squares problem using the Levenberg-Marquardt algorithm.
   *
   * </blockquote>
   *
   * <pre>
   * Fit({m, v})
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * find a fit (coefficient) vector <code>a</code> that minimizes <code>Norm(m.a - v)</code> for
   * the design matrix <code>m</code> and the response vector <code>v</code> and return the best fit
   * expression.
   *
   * </blockquote>
   *
   * <p>
   * The option <code>WorkingPrecision</code> controls the arithmetic used to solve the least
   * squares problem:
   *
   * <ul>
   * <li><code>WorkingPrecision-&gt;Automatic</code> uses machine (double) precision (default)
   * <li><code>WorkingPrecision-&gt;Infinity</code> uses exact/symbolic arithmetic
   * <li><code>WorkingPrecision-&gt;n</code> uses <code>n</code>-digit <code>Apfloat</code>
   * arbitrary precision arithmetic
   * </ul>
   *
   * <p>
   * See:<br>
   *
   * <ul>
   * <li><a href="https://en.wikipedia.org/wiki/Levenberg%E2%80%93Marquardt_algorithm">Wikipedia -
   * Levenberg–Marquardt algorithm</a>
   * </ul>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Fit({{1,1},{2,4},{3,9},{4,16}},2,x)
   * x^2.0
   * </pre>
   */
  private static class Fit extends FindFit {

    /** {@link S#WorkingPrecision} sentinel: use machine (double) precision. */
    private static final int DOUBLE_PRECISION = -2;

    /** {@link S#WorkingPrecision} sentinel: use exact/symbolic arithmetic. */
    private static final int EXACT_PRECISION = -1;

    @Override
    public @AllowNIL IExpr numericEval(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
        IAST originalAST) {
      IExpr workingPrecision = options[0];
      int precision = workingPrecisionValue(workingPrecision);

      // Fit({m, v}) signature
      if (argSize == 1) {
        IExpr arg1 = ast.arg1();
        if (arg1.isList2()) {
          IExpr m = arg1.first();
          IExpr v = arg1.second();
          IExpr model = FittedModelExpr.linearModelFit(m, v);
          try {
            if (model instanceof FittedModelExpr) {
              // return the best fit (coefficient) vector
              return F.List(((FittedModelExpr) model).toData().estimateRegressionParameters());
            }
            return model;
          } catch (MathIllegalArgumentException miae) {
            return Errors.printMessage(S.Fit, miae);
            // if (miae.getSpecifier().equals(LocalizedCoreFormats.SINGULAR_MATRIX)) {
            // }
          }
        }
        return F.NIL;
      }

      final IAST variables = argSize >= 3 ? fitVariables(ast.arg3()) : F.NIL;
      if (variables.isPresent() && ast.arg1().isList()) {
        IAST termList = ast.arg2().makeList();
        IAST data = (IAST) ast.arg1();
        if (data.isEmpty()) {
          // no data: the fit and its properties are 0
          return F.C0;
        }
        final IAST coefficients = fitCoefficients(data, termList, variables, precision, engine);
        if (coefficients.isNIL()) {
          return F.NIL;
        }
        final IExpr bestFit = engine.evaluate(F.mapRange(S.Plus, 1, termList.size(), j -> {
          final IExpr coefficient = coefficients.get(j);
          // a machine zero drops its basis function; an exact zero vanishes in the sum anyway and
          // an arbitrary precision zero keeps its precision
          return coefficient.isZero() && coefficient.isMachineNumber() ? F.NIL
              : F.Times(coefficient, termList.get(j));
        }));
        if (argSize == 4) {
          final IExpr property = ast.arg4();
          if (property.isList()) {
            return ((IAST) property).map(p -> fitProperty(p, data, termList, variables,
                coefficients, bestFit, engine));
          }
          return fitProperty(property, data, termList, variables, coefficients, bestFit, engine);
        }
        return bestFit;
      }
      return F.NIL;
    }

    /**
     * The coefficients of the basis functions in the least squares fit.
     *
     * @param data the list of data points (a matrix of <code>{x, ..., f}</code> rows or a vector of
     *        f-values)
     * @param termList the basis functions in the given <code>variables</code>
     * @param variables the model variables
     * @param precision {@link #DOUBLE_PRECISION}, {@link #EXACT_PRECISION} or the number of
     *        <code>Apfloat</code> digits
     * @return a list with one coefficient for each basis function or {@link F#NIL}
     */
    private static IAST fitCoefficients(IAST data, IAST termList, IAST variables, int precision,
        EvalEngine engine) {
      // exact (Infinity) or Apfloat (n digits) arbitrary precision linear least squares
      if (precision != DOUBLE_PRECISION) {
        return symbolicFit(data, termList, variables, precision, engine);
      }
      IAST dataMatrix = buildDataMatrix(data, variables.argSize());
      final double[][] points = dataMatrix.isPresent() ? dataMatrix.toDoubleMatrix(false) : null;
      if (points != null) {
        try {
          final double[] coefficients =
              linearLeastSquares(dataMatrix, points, termList, variables, engine);
          return F.mapRange(0, coefficients.length, i -> F.num(coefficients[i]));
        } catch (RuntimeException rex) {
          Errors.rethrowsInterruptException(rex);
          return Errors.printMessage(S.Fit, rex, engine);
        }
      }
      if (dataMatrix.isPresent() && dataMatrix.forAll(row -> isResponse(row.last()))) {
        return symbolicResponseFit(dataMatrix, termList, variables, engine);
      }
      return F.NIL;
    }

    /**
     * A property of the fit: <code>"BestFit"</code>, <code>"BestFitParameters"</code>,
     * <code>"BasisFunctions"</code>, <code>"FitResiduals"</code> or <code>"Function"</code>.
     *
     * @return <code>Missing("NotApplicable")</code> for an unknown property
     */
    private static @AllowNIL IExpr fitProperty(IExpr property, IAST data, IAST termList, IAST variables,
        IAST coefficients, IExpr bestFit, EvalEngine engine) {
      if (property.isString()) {
        switch (property.toString()) {
          case "BestFit":
            return bestFit;
          case "BestFitParameters":
            return coefficients;
          case "BasisFunctions":
            return termList;
          case "FitResiduals":
            final IAST dataMatrix = buildDataMatrix(data, variables.argSize());
            if (dataMatrix.isPresent()) {
              // the value of the fit minus the data value at each point
              return engine.evaluate(F.mapRange(1, dataMatrix.size(), i -> {
                final IAST row = (IAST) dataMatrix.get(i);
                return F.Subtract(F.subst(bestFit, pointRules(variables, row)), row.last());
              }));
            }
            return F.NIL;
          case "Function":
            // the variables become the slots #1, #2, ... in their order
            return F.Function(F.subst(bestFit,
                F.mapRange(1, variables.size(), k -> F.Rule(variables.get(k), F.Slot(k)))));
          default:
        }
      }
      // `1` is not a valid property.
      Errors.printMessage(S.Fit, "noprop", F.list(property), engine);
      return F.Missing("NotApplicable");
    }

    /** Whether a value of the data can be a term of a linear combination of the responses. */
    private static boolean isResponse(IExpr value) {
      return !(value.isList() || value.isRuleAST() || value.isString());
    }

    /**
     * The least squares fit of data whose responses are not all numbers. Each coefficient is a
     * linear combination of the responses: with the columns of the design matrix scaled to unit
     * length and <code>P</code> the pseudo-inverse of the scaled matrix, the coefficient of the
     * basis function <code>j</code> is <code>(P[j,1] y1 + P[j,2] y2 + ...)</code> over the length of
     * the column <code>j</code>.
     *
     * @param dataMatrix the data as <code>{x, y, ..., f}</code> rows with numeric coordinates
     * @return the coefficients of the basis functions or {@link F#NIL} if a coordinate or a basis
     *         function value is not a number
     */
    private static IAST symbolicResponseFit(IAST dataMatrix, IAST termList, IAST variables,
        EvalEngine engine) {
      final int rows = dataMatrix.argSize();
      final int columns = termList.argSize();
      final double[][] design;
      try {
        design = designMatrix(dataMatrix, termList, variables, engine);
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        return F.NIL;
      }
      final double[] length = new double[columns];
      for (int j = 0; j < columns; j++) {
        for (int i = 0; i < rows; i++) {
          length[j] += design[i][j] * design[i][j];
        }
        length[j] = Math.sqrt(length[j]);
      }
      for (int i = 0; i < rows; i++) {
        for (int j = 0; j < columns; j++) {
          if (length[j] > 0.0) {
            design[i][j] /= length[j];
          }
        }
      }
      final double[][] inverse = LinearAlgebra
          .machinePseudoInverse(MatrixUtils.createRealMatrix(design)).getData();
      IASTAppendable coefficients = F.ListAlloc(columns);
      for (int j = 0; j < columns; j++) {
        if (length[j] > 0.0) {
          final double[] row = inverse[j];
          IExpr combination = F.mapRange(S.Plus, 0, rows,
              k -> F.Times(F.num(row[k]), ((IAST) dataMatrix.get(k + 1)).last()));
          coefficients.append(engine.evaluate(F.Times(F.num(1.0 / length[j]), combination)));
        } else {
          coefficients.append(F.CD0);
        }
      }
      return coefficients;
    }

    /**
     * A column of the design matrix depends on the columns before it, if what is left of its
     * unit length after the reduction by them is not larger than this.
     */
    private static final double DEPENDENT_COLUMN = 1.0e-11;

    /**
     * The coefficients of the basis functions which minimize the sum of the squared residuals.
     * There is one coefficient for each function, and no constant which is not in the basis.
     *
     * <p>
     * The problem is linear, see {@link #scaledLeastSquares(double[][], double[], boolean)}.
     *
     * @param dataMatrix the data as <code>{x, y, ..., f}</code> rows; the basis functions are
     *        evaluated at these <code>x, y, ...</code>, so that <code>Sin(2*x)</code> is zero at
     *        <code>Pi</code>
     * @param points the same data as machine numbers
     * @param termList the basis functions in the given <code>variables</code>
     * @param variables the model variables
     * @param engine the evaluation engine
     */
    private static double[] linearLeastSquares(IAST dataMatrix, double[][] points,
        IAST termList, IAST variables, EvalEngine engine) {
      final int n = variables.argSize();
      final double[] response = new double[points.length];
      for (int i = 0; i < points.length; i++) {
        response[i] = points[i][n];
      }
      return scaledLeastSquares(designMatrix(dataMatrix, termList, variables, engine), response,
          false);
    }

    /**
     * The values of the basis functions at the points of the data.
     *
     * @param dataMatrix the data as <code>{x, y, ..., f}</code> rows
     */
    private static double[][] designMatrix(IAST dataMatrix, IAST termList, IAST variables,
        EvalEngine engine) {
      final int rows = dataMatrix.argSize();
      final int columns = termList.argSize();
      final double[][] design = new double[rows][columns];
      for (int i = 0; i < rows; i++) {
        final IAST rules = pointRules(variables, (IAST) dataMatrix.get(i + 1));
        for (int j = 0; j < columns; j++) {
          // the exact value first: Sin(2*x) is 0 at Pi, and not the sine of 6.283185307179586
          design[i][j] = engine.evalDouble(engine.evaluate(F.subst(termList.get(j + 1), rules)));
        }
      }
      return design;
    }

    /**
     * The coefficients which minimize the sum of the squared residuals of
     * <code>design.c == response</code>.
     *
     * <p>
     * The columns of the design matrix are scaled to unit length, so that <code>1</code> and
     * <code>x^5</code> count alike, and the matrix is solved by its QR decomposition. If the
     * columns depend on each other, or if there are more columns than rows, the singular values of
     * the scaled matrix give the solution with the smallest coefficients in the scaled coordinates.
     * A column which is zero at every point gets the coefficient 0. An iteration from a first guess
     * ran away with such functions.
     *
     * @param minimumNormOnly if <code>true</code> only a problem without a unique solution is
     *        solved
     * @return <code>null</code> if <code>minimumNormOnly</code> is set and the problem has a unique
     *         solution
     */
    private static double[] scaledLeastSquares(double[][] design, double[] response,
        boolean minimumNormOnly) {
      final int rows = design.length;
      final int columns = rows == 0 ? 0 : design[0].length;
      // the columns which are not zero, and their lengths
      final double[] length = new double[columns];
      final int[] column = new int[columns];
      int size = 0;
      for (int j = 0; j < columns; j++) {
        double sum = 0.0;
        for (int i = 0; i < rows; i++) {
          sum = Math.hypot(sum, design[i][j]);
        }
        length[j] = sum;
        if (sum > 0.0) {
          column[size++] = j;
        }
      }
      final double[] coefficients = new double[columns];
      if (size == 0) {
        return coefficients;
      }
      final double[][] scaled = new double[rows][size];
      for (int i = 0; i < rows; i++) {
        for (int k = 0; k < size; k++) {
          scaled[i][k] = design[i][column[k]] / length[column[k]];
        }
      }
      final RealVector vector = new ArrayRealVector(response, false);
      if (rows >= size) {
        final DecompositionSolver solver =
            new QRDecomposition(MatrixUtils.createRealMatrix(scaled), DEPENDENT_COLUMN)
                .getSolver();
        if (solver.isNonSingular()) {
          if (minimumNormOnly && size == columns) {
            return null;
          }
          final double[] solution = solver.solve(vector).toArray();
          for (int k = 0; k < size; k++) {
            coefficients[column[k]] = solution[k] / length[column[k]];
          }
          return coefficients;
        }
      }
      final RealMatrix matrix = MatrixUtils.createRealMatrix(scaled);
      final DecompositionSolver svd = new SingularValueDecomposition(matrix).getSolver();
      RealVector solution = svd.solve(vector);
      for (int refinement = 0; refinement < 2; refinement++) {
        // an ill-conditioned matrix amplifies the rounding errors of the singular vectors: the
        // solution of the residual corrects them, and keeps the solution the one of smallest norm
        solution = solution.add(svd.solve(vector.subtract(matrix.operate(solution))));
      }
      for (int k = 0; k < size; k++) {
        coefficients[column[k]] = solution.getEntry(k) / length[column[k]];
      }
      return coefficients;
    }

    /** The rules <code>{x -> x1, y -> y1, ...}</code> for the data row <code>{x1, y1, ...}</code>. */
    private static IAST pointRules(IAST variables, IAST row) {
      return F.mapRange(1, variables.size(), k -> F.Rule(variables.get(k), row.get(k)));
    }

    /**
     * Solve the linear least squares problem with exact (<code>Infinity</code>) or
     * <code>Apfloat</code> (<code>precision</code> digits) arithmetic.
     *
     * <p>
     * The coefficients are computed by solving the normal equations <code>(X^T.X).a == X^T.y</code>
     * with {@link S#LinearSolve}. Solving the normal equations directly keeps exact rational (or
     * <code>Apfloat</code>) entries and avoids the square roots introduced by a QR decomposition,
     * so e.g. an exact fit reduces to a simplified expression.
     *
     * @param data the list of data points (a matrix of <code>{x, ..., f}</code> rows or a vector of
     *        f-values)
     * @param termList the basis functions in the given <code>variables</code>
     * @param variables the model variables
     * @param precision {@link #EXACT_PRECISION} for exact arithmetic or the number of
     *        <code>Apfloat</code> digits
     * @param engine the evaluation engine
     * @return the coefficients of the basis functions or {@link F#NIL}
     */
    private static IAST symbolicFit(IAST data, IAST termList, IAST variables, int precision,
        EvalEngine engine) {
      try {
        IAST dataMatrix = buildDataMatrix(data, variables.argSize());
        if (dataMatrix.isPresent()) {
          final int numberOfPoints = dataMatrix.argSize();
          final int numberOfTerms = termList.argSize();

          // build the design matrix X and the response vector y.
          // The basis functions are evaluated with the exact x-data first (so e.g. integer powers
          // stay exact) and only the resulting numeric entries are converted to the requested
          // working precision. This avoids raising an Apfloat base to a symbolic exponent.
          IASTAppendable designMatrix = F.ListAlloc(numberOfPoints);
          IASTAppendable responseVector = F.ListAlloc(numberOfPoints);
          for (int i = 1; i <= numberOfPoints; i++) {
            IAST row = (IAST) dataMatrix.get(i);
            IAST rules = pointRules(variables, row);
            responseVector.append(toPrecision(row.last(), precision, engine));
            IASTAppendable designRow = F.ListAlloc(numberOfTerms);
            for (int j = 1; j <= numberOfTerms; j++) {
              IExpr basisValue = engine.evaluate(F.subst(termList.get(j), rules));
              designRow.append(toPrecision(basisValue, precision, engine));
            }
            designMatrix.append(designRow);
          }

          // solve the normal equations (X^T.X).a == X^T.y
          IExpr transposed = S.Transpose.of(engine, designMatrix);
          IExpr xtx = S.Dot.of(engine, transposed, designMatrix);
          IExpr xty = S.Dot.of(engine, transposed, responseVector);
          IExpr coefficients = S.LinearSolve.of(engine, xtx, xty);
          if (coefficients.isList() && coefficients.argSize() == numberOfTerms) {
            return (IAST) coefficients;
          }
        }
      } catch (ValidateException ve) {
        Errors.printMessage(S.Fit, ve, engine);
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        return Errors.printMessage(S.Fit, rex, engine);
      }
      return F.NIL;
    }

    /**
     * Build a matrix of exact <code>{x, ..., f}</code> rows from the data.
     *
     * @param data the list of data points (a matrix of <code>{x, ..., f}</code> rows or, for one
     *        variable, a vector of f-values)
     * @param numberOfVariables the number of the model variables
     * @return the matrix as a nested list or {@link F#NIL} if the data couldn't be interpreted
     */
    private static IAST buildDataMatrix(IAST data, int numberOfVariables) {
      int[] isMatrix = data.isMatrix();
      if (isMatrix != null && isMatrix[1] == numberOfVariables + 1) {
        IASTAppendable result = F.ListAlloc(data.size());
        for (int i = 1; i < data.size(); i++) {
          result.append(data.get(i));
        }
        return result;
      }
      if (numberOfVariables != 1) {
        return F.NIL;
      }
      int rowSize = data.isVector();
      if (rowSize < 0) {
        return F.NIL;
      }
      IASTAppendable result = F.ListAlloc(data.size());
      for (int i = 1; i < data.size(); i++) {
        result.append(F.List(F.ZZ(i), data.get(i)));
      }
      return result;
    }

    /**
     * Convert a value to the requested working precision.
     *
     * @param value the value to convert
     * @param precision {@link #EXACT_PRECISION} for exact arithmetic or the number of
     *        <code>Apfloat</code> digits
     * @param engine the evaluation engine
     * @return the converted value
     */
    private static IExpr toPrecision(IExpr value, int precision, EvalEngine engine) {
      if (precision > 0) {
        return S.N.of(engine, value, F.ZZ(precision));
      }
      return engine.evaluate(value);
    }

    /**
     * Determine the requested working precision.
     *
     * @param workingPrecision the value of the {@link S#WorkingPrecision} option
     * @return {@link #DOUBLE_PRECISION} for {@link S#Automatic} (or invalid values),
     *         {@link #EXACT_PRECISION} for {@link S#Infinity}, otherwise the requested number of
     *         <code>Apfloat</code> digits
     */
    private static int workingPrecisionValue(IExpr workingPrecision) {
      if (workingPrecision == S.Automatic) {
        return DOUBLE_PRECISION;
      }
      if (workingPrecision.isInfinity()) {
        return EXACT_PRECISION;
      }
      int n = workingPrecision.toIntDefault();
      if (n > 0) {
        return n;
      }
      return DOUBLE_PRECISION;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_4;
    }

    /** The variables of a linear fit are not localized. */
    @Override
    public boolean localIterators() {
      return false;
    }
  }

  /**
   * Unable to construct a numeric design matrix. Nominal variables may need to be specified, or
   * non-numeric entries for numeric variables may need to be replaced.
   */
  private static final String DESMAT_MESSAGE =
      "Unable to construct a numeric design matrix. Nominal variables may need to be specified, "
          + "or non-numeric entries for numeric variables may need to be replaced.";

  private static final String NOTDATA_MESSAGE =
      "The first argument is not a vector, a matrix or a list containing a design matrix and "
          + "response vector.";

  /**
   * Whether an independent variable of a model can be the given expression: a symbol, or an
   * expression like <code>Subscript(x, 1)</code>, but not a number, a string or a list.
   */
  private static boolean isModelVariable(IExpr variable) {
    return !(variable.isNumericFunction(true) || variable.isList() || variable.isString());
  }

  /**
   * The data of a linear model as machine rows <code>{x1, ..., xk, y}</code>. A vector, or a matrix
   * of one-element rows, are responses at the positions <code>1, 2, ...</code>. A data point with a
   * <code>Missing(...)</code> entry is left out.
   *
   * @param data the first argument of the fit
   * @param variables the independent variables
   * @param head the function which prints the messages
   * @param responseTag the message for a response which is not a number while the coordinates
   *        are, or <code>null</code> for the message <code>desmat</code>
   * @return <code>null</code> after a message if the data cannot be used
   */
  private static double[][] modelData(IExpr data, IAST variables, ISymbol head,
      String responseTag, EvalEngine engine) {
    if (!variables.forAll(CurveFitterFunctions::isModelVariable)) {
      // `1` is not a valid variable.
      Errors.printMessage(head, "ivar", F.list(variables), engine);
      return null;
    }
    if (!data.isList() || data.argSize() == 0 || !((IAST) data).forAll(x -> x.isList())
        && ((IAST) data).exists(x -> x.isList())) {
      // The first argument is not a vector, a matrix or a list containing a design matrix and
      // response vector.
      Errors.printMessage(head, "notdata", F.CEmptyList, engine);
      return null;
    }
    IAST list = (IAST) data;
    IASTAppendable rows = F.ListAlloc(list.argSize());
    int coordinates = 1;
    if (list.arg1().isList()) {
      final int length = list.arg1().argSize();
      if (!list.forAll(row -> row.argSize() == length) || length == 0) {
        Errors.printMessage(head, "notdata", F.CEmptyList, engine);
        return null;
      }
      if (length > 1) {
        coordinates = length - 1;
        rows.appendArgs(list);
      } else {
        list.forEach((row, i) -> rows.append(F.List(F.ZZ(i), row.first())));
      }
    } else {
      list.forEach((y, i) -> rows.append(F.List(F.ZZ(i), y)));
    }
    if (coordinates != variables.argSize()) {
      // The number of coordinates (`1`) is not equal to the number of variables (`2`).
      Errors.printMessage(head, "fitc", F.List(F.ZZ(coordinates), F.ZZ(variables.argSize())),
          engine);
      return null;
    }
    java.util.List<double[]> points = new java.util.ArrayList<double[]>();
    for (int i = 1; i < rows.size(); i++) {
      IAST row = (IAST) rows.get(i);
      if (row.exists(x -> x.head() == S.Missing || x == S.Missing)) {
        continue;
      }
      double[] point = row.toDoubleVector();
      if (point == null) {
        String tag = responseTag != null && row.removeAtCopy(row.argSize()).toDoubleVector() != null
            ? responseTag
            : "desmat";
        Errors.printMessage(head, tag, F.CEmptyList, engine);
        return null;
      }
      points.add(point);
    }
    if (points.isEmpty()) {
      Errors.printMessage(head, "desmat", F.CEmptyList, engine);
      return null;
    }
    return points.toArray(new double[points.size()][]);
  }

  /**
   * The basis functions of a model: a constant basis function is added unless the list has one
   * already, or <code>IncludeConstantBasis -> False</code> asks for a fit through the origin.
   */
  private static IAST modelBasis(IExpr functions, IExpr includeConstantBasis) {
    IAST basisFunctions = functions.makeList();
    if (!includeConstantBasis.isFalse() && !basisFunctions.exists(f -> f.isOne())) {
      IASTAppendable temp = F.ListAlloc(basisFunctions.size());
      temp.append(F.C1);
      temp.appendArgs(basisFunctions);
      basisFunctions = temp;
    }
    return basisFunctions;
  }

  /**
   * Data given as rules <code>{x1 -> y1, ...}</code> or <code>{x1, ...} -> {y1, ...}</code> as the
   * list of points <code>{{x1, y1}, ...}</code>; a point <code>x</code> may be a vector.
   *
   * @return the data unchanged if it has neither form
   */
  private static @AllowNIL IExpr ruleData(IExpr data) {
    IAST rules = F.NIL;
    if (data.isRuleAST() && data.first().isList() && data.second().isList()
        && data.first().argSize() == data.second().argSize()) {
      IAST x = (IAST) data.first();
      IAST y = (IAST) data.second();
      rules = F.mapRange(1, x.size(), i -> F.Rule(x.get(i), y.get(i)));
    } else if (data.isList() && data.argSize() > 0 && ((IAST) data).forAll(r -> r.isRuleAST())) {
      rules = (IAST) data;
    }
    if (rules.isNIL()) {
      return data;
    }
    return rules.map(r -> {
      IExpr x = r.first();
      if (x.isList()) {
        return ((IAST) x).appendClone(r.second());
      }
      return F.list(x, r.second());
    }, 1);
  }

  private static final class LinearModelFit extends AbstractFunctionOptionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, final int argSize, final IExpr[] options,
        final EvalEngine engine, final IAST originalAST) {
      IExpr arg1 = ast.arg1();
      // the call still holds the option; argSize counts the positional arguments alone
      if (argSize == 1) {
        if (arg1.isList2()) {
          IExpr m = arg1.first();
          IExpr v = arg1.second();
          return FittedModelExpr.linearModelFit(m, v);
        }
        return F.NIL;
      }
      if (argSize == 3) {
        IAST variables = ast.arg3().makeList();
        arg1 = ruleData(arg1);
        double[][] data = modelData(arg1, variables, S.LinearModelFit, null, engine);
        if (data == null) {
          return F.NIL;
        }
        double[] weights = null;
        if (!isUnitWeights(options[1], data.length)) {
          weights = positiveWeights(options[1], data.length);
          if (weights == null) {
            // weights which are not a list of positive numbers for the data
            return F.NIL;
          }
        }
        return FittedModelExpr.linearModelFit(data, weights, modelBasis(ast.arg2(), options[0]),
            variables, engine);
      }
      // The first argument is not a vector or matrix or a list containing a design matrix and
      // response vector.
      return Errors.printMessage(S.LinearModelFit, "notdata", F.CEmptyList, engine);
    }


    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      putModelMessages(newSymbol);
      setOptions(newSymbol, new IBuiltInSymbol[] {S.IncludeConstantBasis, S.Weights},
          new IExpr[] {S.True, S.Automatic});
    }

    /**
     * Test if <code>weights</code> is <code>Automatic</code> or a list of <code>n</code> equal
     * positive numbers.
     */
    private static boolean isUnitWeights(IExpr weights, int n) {
      if (weights == S.Automatic) {
        return true;
      }
      if (!weights.isList() || weights.argSize() != n || n == 0) {
        return false;
      }
      IExpr first = weights.first();
      return first.isReal() && first.isPositiveResult()
          && ((IAST) weights).forAll(w -> w.equals(first));
    }

    /**
     * The weights as machine numbers, or <code>null</code> if <code>weights</code> is not a list of
     * <code>n</code> positive real numbers.
     */
    private static double[] positiveWeights(IExpr weights, int n) {
      if (!weights.isList() || weights.argSize() != n) {
        return null;
      }
      double[] result = weights.toDoubleVector();
      if (result == null) {
        return null;
      }
      for (double w : result) {
        if (!(w > 0.0) || Double.isInfinite(w)) {
          return null;
        }
      }
      return result;
    }
  }

  /**
   * Fit a generalized linear model.
   *
   * @param ast the call <code>head(data, functions, variables, ...)</code>
   * @param family the exponential family of the response
   * @param link the link function
   * @param includeConstantBasis the value of the option {@link S#IncludeConstantBasis}
   * @return the model or {@link F#NIL}
   */
  private static @AllowNIL IExpr generalizedLinearModelFit(IAST ast, String family, String link,
      IExpr includeConstantBasis, EvalEngine engine) {
    final ISymbol head = (ISymbol) ast.topHead();
    IAST variables = ast.arg3().makeList();
    double[][] data = modelData(ast.arg1(), variables, head,
        family.equals("Binomial") ? "binrsp" : null, engine);
    if (data == null) {
      return F.NIL;
    }
    IAST basisFunctions = modelBasis(ast.arg2(), includeConstantBasis);
    final int k = variables.argSize();
    double[] response = new double[data.length];
    for (int i = 0; i < data.length; i++) {
      response[i] = data[i][k];
      if (!GeneralizedFittedModelExpr.isResponse(family, response[i])) {
        if (family.equals("Binomial")) {
          // The response values are expected to be between 0 and 1 for binomial models.
          return Errors.printMessage(head, "binrsp", F.CEmptyList, engine);
        }
        return F.NIL;
      }
    }
    double[][] design = FittedModelExpr.designMatrixNumeric(data, basisFunctions, variables, engine);
    if (design == null) {
      return Errors.printMessage(head, "desmat", F.CEmptyList, engine);
    }
    if (design[0].length == 0) {
      return F.NIL;
    }
    GeneralizedFittedModelExpr model = GeneralizedFittedModelExpr.fit(new GeneralizedFittedModelExpr.Fit(
        family, link, basisFunctions, variables, design, response));
    return model == null ? F.NIL : model;
  }

  /** The messages of the linear model fits. */
  private static void putModelMessages(ISymbol symbol) {
    symbol.putMessage(IPatternMatcher.SET, "desmat", F.stringx(DESMAT_MESSAGE));
    symbol.putMessage(IPatternMatcher.SET, "notdata", F.stringx(NOTDATA_MESSAGE));
    symbol.putMessage(IPatternMatcher.SET, "binrsp",
        F.stringx("The response values are expected to be between 0 and 1 for binomial models."));
  }

  /**
   * <code>GeneralizedLinearModelFit(data, functions, variables)</code> - a generalized linear model
   * of a response from an exponential family, with the canonical link function of the family.
   */
  private static final class GeneralizedLinearModelFit extends AbstractFunctionOptionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, final int argSize, final IExpr[] options,
        final EvalEngine engine, final IAST originalAST) {
      if (argSize != 3) {
        return F.NIL;
      }
      IExpr family = options[0];
      if (family.isAutomatic()) {
        family = F.stringx("Gaussian");
      }
      if (!family.isString()
          || Arrays.binarySearch(GeneralizedFittedModelExpr.FAMILIES, family.toString()) < 0) {
        return F.NIL;
      }
      String name = family.toString();
      return generalizedLinearModelFit(ast, name, GeneralizedFittedModelExpr.canonicalLink(name),
          options[1], engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_INFINITY;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      putModelMessages(newSymbol);
      setOptions(newSymbol,
          new IBuiltInSymbol[] {S.ExponentialFamily, S.IncludeConstantBasis},
          new IExpr[] {F.stringx("Gaussian"), S.True});
    }
  }

  /**
   * <code>LogitModelFit(data, functions, variables)</code> and
   * <code>ProbitModelFit(data, functions, variables)</code> - the binomial generalized linear model
   * of probabilities with the logit or the probit link function.
   */
  private static final class BinomialModelFit extends AbstractFunctionOptionEvaluator {
    private final String link;

    BinomialModelFit(String link) {
      this.link = link;
    }

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, final int argSize, final IExpr[] options,
        final EvalEngine engine, final IAST originalAST) {
      if (argSize != 3) {
        return F.NIL;
      }
      return generalizedLinearModelFit(ast, "Binomial", link, options[0], engine);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_INFINITY;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      putModelMessages(newSymbol);
      setOptions(newSymbol, new IBuiltInSymbol[] {S.IncludeConstantBasis}, new IExpr[] {S.True});
    }
  }

  /**
   * <code>DiscreteIndicator(x, c, {c1, c2, ...})</code> - <code>1</code> if the level
   * <code>x</code> is <code>c</code> and <code>0</code> for another level; both must be levels.
   */
  private static final class DiscreteIndicator extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg3().isList()) {
        IAST levels = (IAST) ast.arg3();
        if (levels.contains(ast.arg1()) && levels.contains(ast.arg2())) {
          return ast.arg1().equals(ast.arg2()) ? F.C1 : F.C0;
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_3;
    }
  }

  /**
   * <code>NonlinearModelFit(data, model, parameters, x)</code> - fit a model that need not be linear
   * in its parameters, and return a <code>FittedModel</code> that can be asked about the fit.
   *
   * <p>
   * The fit is the one <code>FindFit</code> makes, Levenberg-Marquardt from a start of
   * <code>1.0</code> for each parameter or the value given as <code>{a, a0}</code>. The model has
   * one independent variable <code>x</code> or several <code>{x1, x2, ...}</code>, whose values
   * are the leading elements of the data rows <code>{x1, x2, ..., y}</code>.
   */
  private static final class NonlinearModelFit extends AbstractEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ruleData(ast.arg1());
      IAST variables = FindFit.fitVariables(ast.arg4());
      if (!arg1.isList() || !ast.arg3().isList() || variables.isNIL()) {
        return F.NIL;
      }
      IAST data = (IAST) arg1;
      IExpr function = FindFit.modelForm(ast.arg2());
      IExpr constraints = FindFit.modelConstraints(ast.arg2());
      IASTAppendable renamed = F.ListAlloc();
      IAST spec = FindFit.renameParameters((IAST) ast.arg3(), renamed);
      if (renamed.argSize() > 0) {
        function = F.subst(function, renamed);
        constraints = F.subst(constraints, renamed);
      }
      double[] initialGuess = new double[spec.argSize()];
      IAST symbols = FindFit.initialGuess(spec, initialGuess);
      if (symbols == null || !symbols.isPresent()) {
        return F.NIL;
      }
      if (constraints.isPresent()) {
        // the constrained fit is made to the rows {x1, ..., xn, y}
        data = Fit.buildDataMatrix(data, variables.argSize());
        if (data.isNIL()) {
          return F.NIL;
        }
      }
      try {
        double[] values;
        double[][] xs;
        double[] ys;
        if (constraints.isNIL() && variables.argSize() == 1) {
          java.util.List<org.hipparchus.fitting.WeightedObservedPoint> points =
              new java.util.ArrayList<>();
          values = FindFit.fit(data, function, symbols, variables.arg1(), initialGuess, points,
              engine);
          xs = new double[points.size()][];
          ys = new double[points.size()];
          for (int i = 0; i < xs.length; i++) {
            xs[i] = new double[] {points.get(i).getX()};
            ys[i] = points.get(i).getY();
          }
        } else {
          // the rows {x1, ..., xn, y}
          values = constraints.isPresent()
              ? FindFit.fitConstrained(data, function, constraints, symbols, variables,
                  initialGuess, engine)
              : FindFit.fitMultivariate(data, function, symbols, variables, initialGuess, engine);
          final int n = variables.argSize();
          double[][] rows = values == null ? null : data.toDoubleMatrix(true);
          if (rows == null) {
            return F.NIL;
          }
          xs = new double[rows.length][];
          ys = new double[rows.length];
          for (int i = 0; i < rows.length; i++) {
            xs[i] = java.util.Arrays.copyOf(rows[i], n);
            ys[i] = rows[i][n];
          }
        }
        if (values == null) {
          return F.NIL;
        }
        if (renamed.argSize() > 0) {
          // the model keeps the parameters as they were given
          IAST inverse = F.mapList(renamed, (IExpr rule) -> F.Rule(rule.second(), rule.first()));
          function = F.subst(function, inverse);
          symbols = (IAST) F.subst(symbols, inverse);
        }
        return new NonlinearFittedModelExpr(
            new NonlinearFittedModelExpr.Fit(function, symbols, variables, values, xs, ys));
      } catch (ValidateException ve) {
        return Errors.printMessage(ast.topHead(), ve, engine);
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        return Errors.printMessage(S.NonlinearModelFit, rex, engine);
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_4_4;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private CurveFitterFunctions() {}
}
