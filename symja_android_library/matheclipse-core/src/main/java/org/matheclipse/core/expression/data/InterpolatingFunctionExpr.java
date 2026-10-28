package org.matheclipse.core.expression.data;

import org.hipparchus.analysis.interpolation.FieldHermiteInterpolator;
import org.hipparchus.ode.DenseOutputModel;
import org.hipparchus.stat.regression.SimpleRegression;
import org.hipparchus.stat.regression.UpdatingMultipleLinearRegression;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Represents an interpolating function stored as data within the expression system.
 * <p>
 * This class wraps various interpolation/regression models (for example
 * {@link FieldHermiteInterpolator}, {@link SimpleRegression}, or an {@link IAST} representing a
 * symbolic interpolator). It provides evaluation and normalization behavior appropriate for the
 * stored model and records the valid input domain via {@code min} and {@code max}.
 * </p>
 *
 * @param <T> the concrete model type stored in this expression
 */
public class InterpolatingFunctionExpr<T> extends DataExpr<T> {
  /**
   * Minimum domain value for which the interpolator is defined (inclusive).
   */
  double min;

  /**
   * Maximum domain value for which the interpolator is defined (inclusive).
   */
  double max;

  /** The degree of the interpolating polynomials. */
  int order = 3;

  /**
   * The domain <code>{min, max}</code> as the exact numbers of the data it was built from, or
   * {@link F#NIL} if the data had inexact or no such numbers.
   */
  IAST exactDomain = F.NIL;

  /**
   * Sets the domain to the exact numbers <code>min</code> and <code>max</code>, which the
   * <code>"Domain"</code> property gives instead of their machine values.
   *
   * @return this function
   */
  public InterpolatingFunctionExpr<T> withExactDomain(IExpr min, IExpr max) {
    this.exactDomain = F.list(min, max);
    return this;
  }

  /**
   * Whether <code>f(x)</code> with a symbolic argument stays unevaluated instead of writing out
   * the polynomial pieces.
   */
  boolean numericArgumentsOnly = false;

  /**
   * Leaves <code>f(x)</code> with a symbolic argument unevaluated, as for the solution of a
   * differential equation.
   *
   * @return this function
   */
  public InterpolatingFunctionExpr<T> withNumericArgumentsOnly() {
    this.numericArgumentsOnly = true;
    return this;
  }

  /**
   * Wrapper for an interpolating function represented as an {@link IAST}. This variant returns the
   * original stored AST via {@link #normal(boolean)}.
   */
  private static class ASTFunctionExpr extends InterpolatingFunctionExpr<IAST> {
    private static final long serialVersionUID = 7355009868203033788L;

    /**
     * Return the stored {@link IAST} representation for normalization.
     *
     * @param nilIfUnevaluated ignored for this implementation
     * @return the AST data representing the interpolator
     */
    @Override
    public IAST normal(boolean nilIfUnevaluated) {
      return toData();
    }

    /**
     * Construct an AST-backed interpolating function.
     *
     * @param interpolator the AST representing the interpolator
     * @param min the minimum domain value
     * @param max the maximum domain value
     */
    public ASTFunctionExpr(final IAST interpolator, double min, double max) {
      super(interpolator, min, max);
    }

    @Override
    public IExpr copy() {
      ASTFunctionExpr function = new ASTFunctionExpr(toData(), min, max);
      function.order = order;
      function.exactDomain = exactDomain;
      function.numericArgumentsOnly = numericArgumentsOnly;
      return function;
    }
  }

  /**
   * Wrapper for a Hermite interpolator model ({@link FieldHermiteInterpolator})
   */
  private static class HermiteFunctionExpr
      extends InterpolatingFunctionExpr<FieldHermiteInterpolator<IExpr>> {
    private static final long serialVersionUID = 5139557783149167160L;

    public HermiteFunctionExpr(final FieldHermiteInterpolator<IExpr> interpolator, double min,
        double max) {
      super(interpolator, min, max);
    }
  }

  /**
   * How the components of the solution of an ordinary differential equation lie in the state
   * vector of numbers the integrator works with, when they are not simply one real number each at
   * the index of the component: a component may be a vector of numbers, and a complex state holds
   * the imaginary parts of all its numbers after the real parts.
   */
  public static final class StateLayout implements java.io.Serializable {
    private static final long serialVersionUID = -2405913370123598470L;

    /**
     * Where the numbers of every component start, and one past the last one, or <code>null</code>
     * if every component is one number at its own index.
     */
    private final int[] start;

    /**
     * <code>true</code> for a component which is a vector of numbers, or <code>null</code> if none
     * is.
     */
    private final boolean[] vector;

    /** The number of real parts, <code>0</code> if the state is real. */
    private final int imaginaryOffset;

    /**
     * @param start where the numbers of every component start, and one past the last one, or
     *        <code>null</code> if every component is one number
     * @param vector <code>true</code> for a component which is a vector, or <code>null</code>
     * @param components the number of components
     * @param complex <code>true</code> if the numbers are complex
     */
    public StateLayout(int[] start, boolean[] vector, int components, boolean complex) {
      this.start = start;
      this.vector = vector;
      this.imaginaryOffset = complex ? (start == null ? components : start[components]) : 0;
    }

    /** <code>true</code> if the numbers are complex. */
    public boolean isComplex() {
      return imaginaryOffset > 0;
    }

    /** The length of the state vector of <code>components</code> components. */
    public int length(int components) {
      int real = start == null ? components : start[components];
      return isComplex() ? 2 * real : real;
    }

    private int start(int component) {
      return start == null ? component : start[component];
    }

    private boolean isVector(int component) {
      return vector != null && vector[component];
    }

    /** The value of component <code>component</code> of <code>state</code>. */
    public IExpr read(double[] state, int component) {
      final int first = start(component);
      if (isVector(component)) {
        final int width = start(component + 1) - first;
        IASTAppendable list = F.ListAlloc(width);
        for (int k = 0; k < width; k++) {
          list.append(number(state, first + k));
        }
        return list;
      }
      return number(state, first);
    }

    private IExpr number(double[] state, int index) {
      return isComplex() ? F.complexNum(state[index], state[imaginaryOffset + index])
          : F.num(state[index]);
    }

    /**
     * Store the numeric value <code>value</code> as component <code>component</code> of
     * <code>state</code>.
     *
     * @return <code>false</code> if it has not the shape of the component, or isn't numeric; the
     *         numbers of the component are then {@link Double#NaN}
     */
    public boolean write(IExpr value, double[] state, int component) {
      final int first = start(component);
      if (isVector(component)) {
        final int width = start(component + 1) - first;
        if (value.isSparseArray()) {
          value = value.normal(false);
        }
        boolean numeric = value.isList() && value.argSize() == width;
        for (int k = 0; k < width; k++) {
          numeric &= writeNumber(numeric ? value.getAt(k + 1) : F.NIL, state, first + k);
        }
        return numeric;
      }
      return writeNumber(value, state, first);
    }

    private boolean writeNumber(IExpr value, double[] state, int index) {
      double re = Double.NaN;
      double im = Double.NaN;
      if (value.isPresent()) {
        if (value.isReal()) {
          re = value.evalf();
          im = 0.0;
        } else if (isComplex() && value.isNumber()) {
          org.hipparchus.complex.Complex c = value.evalfc();
          re = c.getReal();
          im = c.getImaginary();
        } else if (!isComplex()) {
          re = value.evalfNaN();
        }
      }
      state[index] = re;
      if (isComplex()) {
        state[imaginaryOffset + index] = im;
      }
      return !Double.isNaN(re) && !Double.isNaN(im);
    }
  }

  /**
   * Wrapper for the solution of an ordinary differential equation, held as the continuous extension
   * the integrator built while it stepped.
   *
   * <p>
   * Sampling the integrator's own dense output is both more accurate and cheaper than laying the
   * solution on a grid and fitting it a second time: the fit would add its own error - for
   * <code>E^x</code> on a grid of <code>0.1</code> roughly <code>10^-5</code>, against the
   * <code>10^-12</code> the integrator was asked for - and would need one stored row per grid
   * point, where the model stores one interpolator per step it actually took.
   *
   * <p>
   * Initial conditions given at an interior point are integrated once in each direction, so up to
   * two models are held and the one covering the requested point is asked for the value. They
   * cannot be joined into one: {@link DenseOutputModel#append} rejects a model running the other
   * way with a propagation direction mismatch.
   */
  private static class ODEFunctionExpr extends InterpolatingFunctionExpr<DenseOutputModel[]> {
    private static final long serialVersionUID = 8127358036442621281L;

    /** The component of the state vector this function reads. */
    private final int component;

    /**
     * The last component of the block holding <code>y, y', ..., y^(n-1)</code> of the function this
     * one belongs to: the derivative of a component before it is the next component.
     */
    private final int blockEnd;

    /** <code>true</code> for the derivative of the state component, not its value. */
    private final boolean stateDerivative;

    /** The point the initial conditions were given at, separating the two directions. */
    private final double t0;

    /** The state at <code>t0</code>: the value on a domain of the single point <code>t0</code>. */
    private double[] initialState = null;

    /**
     * Where the components lie in the state, or <code>null</code> if each is one real number at
     * its own index.
     */
    private StateLayout layout = null;

    /**
     * @param forward the model covering <code>[t0, max]</code>, or <code>null</code>
     * @param backward the model covering <code>[min, t0]</code>, or <code>null</code>
     * @param t0 the point the initial conditions were given at
     * @param component the component of the state vector this function reads
     * @param min the minimum domain value
     * @param max the maximum domain value
     */
    public ODEFunctionExpr(final DenseOutputModel forward, final DenseOutputModel backward,
        double t0, int component, double min, double max) {
      this(forward, backward, t0, component, component, false, min, max);
    }

    private ODEFunctionExpr(final DenseOutputModel forward, final DenseOutputModel backward,
        double t0, int component, int blockEnd, boolean stateDerivative, double min,
        double max) {
      super(new DenseOutputModel[] {forward, backward}, min, max);
      this.t0 = t0;
      this.component = component;
      this.blockEnd = blockEnd;
      this.stateDerivative = stateDerivative;
    }

    /**
     * The <code>n</code>-th derivative: the next components of the block, and after the last one
     * the derivative the integrator computed for it.
     */
    @Override
    public IExpr derivative(int n) {
      int c = component;
      boolean derivativeOfState = stateDerivative;
      for (int i = 0; i < n; i++) {
        if (derivativeOfState) {
          // the second derivative of the last component isn't stored
          return F.NIL;
        }
        if (c < blockEnd) {
          c++;
        } else {
          derivativeOfState = true;
        }
      }
      DenseOutputModel[] models = toData();
      ODEFunctionExpr function = new ODEFunctionExpr(models[0], models[1], t0, c, blockEnd,
          derivativeOfState, min, max);
      function.initialState = initialState;
      function.layout = layout;
      return function;
    }

    /**
     * The value of the solution at <code>t</code>, taken from whichever direction covers it.
     *
     * @param t the point to sample at
     * @return the value of this function's state component
     */
    public IExpr valueAt(double t) {
      DenseOutputModel[] models = toData();
      DenseOutputModel model = (t < t0 && models[1] != null) ? models[1] : models[0];
      if (model == null) {
        // the requested range lies entirely on the other side of t0
        model = models[1];
      }
      if (model == null) {
        // nothing was integrated: the domain is t0 alone
        return stateDerivative ? F.NIL : read(initialState);
      }
      return read(stateDerivative ? model.getInterpolatedState(t).getPrimaryDerivative()
          : model.getInterpolatedState(t).getPrimaryState());
    }

    /** This function's component of the state vector <code>state</code>. */
    private IExpr read(double[] state) {
      return layout == null ? F.num(state[component]) : layout.read(state, component);
    }

    /** The same solution component of the same integration, or its same derivative. */
    @Override
    public boolean equals(final Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null || obj.getClass() != getClass()) {
        return false;
      }
      ODEFunctionExpr other = (ODEFunctionExpr) obj;
      DenseOutputModel[] models = toData();
      DenseOutputModel[] otherModels = other.toData();
      return models[0] == otherModels[0] && models[1] == otherModels[1]
          && component == other.component && stateDerivative == other.stateDerivative
          && t0 == other.t0 && min == other.min && max == other.max
          && initialState == other.initialState && layout == other.layout;
    }

    @Override
    public int hashCode() {
      DenseOutputModel[] models = toData();
      return java.util.Objects.hash(System.identityHashCode(models[0]),
          System.identityHashCode(models[1]), component, stateDerivative, t0);
    }

    @Override
    public IExpr copy() {
      DenseOutputModel[] models = toData();
      ODEFunctionExpr function = new ODEFunctionExpr(models[0], models[1], t0, component,
          blockEnd, stateDerivative, min, max);
      function.initialState = initialState;
      function.layout = layout;
      return function;
    }

    /**
     * The model is opaque, so it is shown as the domain it is defined on, followed by a placeholder
     * for the data.
     */
    @Override
    public String toString() {
      return "InterpolatingFunction({{" + min + "," + max + "}},<>)";
    }

    /**
     * The stored data is an array of integrator models, whose own <code>toString</code> is the bare
     * Java identity. Show the same opaque form the other outputs use instead of leaking it.
     */
    @Override
    public String fullFormString() {
      return toString();
    }

    @Override
    public String toHTML() {
      return toString();
    }
  }

  /**
   * A function of two variables known on a grid - what <code>ListInterpolation</code> of a matrix
   * and <code>NDSolve</code> of a partial differential equation hand back - interpolated by a
   * Hipparchus bicubic spline (<code>InterpolationOrder</code> 3, at least 5 points along each
   * axis) or bilinearly.
   */
  private static class GridFunctionExpr
      extends InterpolatingFunctionExpr<org.hipparchus.analysis.BivariateFunction> {
    private static final long serialVersionUID = -4418066101223771830L;

    /** The domain of the second variable; the first one's is {@link #min} .. {@link #max}. */
    private final double yMin;
    private final double yMax;

    /** The grid points along each axis, or <code>null</code> if they aren't known. */
    private double[] xs = null;
    private double[] ys = null;

    public GridFunctionExpr(final org.hipparchus.analysis.BivariateFunction function,
        double xMin, double xMax, double yMin, double yMax) {
      super(function, xMin, xMax);
      this.yMin = yMin;
      this.yMax = yMax;
    }

    /** The value at <code>(x, y)</code>, a point outside the grid taken from its edge. */
    public IExpr valueAt(double x, double y) {
      double cx = Math.max(min, Math.min(max, x));
      double cy = Math.max(yMin, Math.min(yMax, y));
      return F.num(toData().value(cx, cy));
    }

    @Override
    public IExpr copy() {
      GridFunctionExpr function = new GridFunctionExpr(toData(), min, max, yMin, yMax);
      function.xs = xs;
      function.ys = ys;
      return function;
    }

    @Override
    public String toString() {
      return "InterpolatingFunction({{" + min + "," + max + "},{" + yMin + "," + yMax + "}},<>)";
    }

    @Override
    public String fullFormString() {
      return toString();
    }

    @Override
    public String toHTML() {
      return toString();
    }
  }

  /**
   * A function of two variables from its values on a grid.
   *
   * @param xs the grid's first axis, strictly increasing
   * @param ys the grid's second axis, strictly increasing
   * @param values <code>values[i][j]</code> at <code>(xs[i], ys[j])</code>
   * @param order the interpolation order: 3 or more asks for a bicubic spline where the grid has
   *        the 5 points along each axis it needs, anything else is bilinear
   */
  public static InterpolatingFunctionExpr newGrid(double[] xs, double[] ys, double[][] values,
      int order) {
    org.hipparchus.analysis.BivariateFunction function;
    if (order >= 3 && xs.length >= 5 && ys.length >= 5) {
      function = new org.hipparchus.analysis.interpolation.PiecewiseBicubicSplineInterpolator()
          .interpolate(xs, ys, values);
    } else {
      function = new org.hipparchus.analysis.interpolation.BilinearInterpolator().interpolate(xs,
          ys, values);
    }
    GridFunctionExpr result =
        new GridFunctionExpr(function, xs[0], xs[xs.length - 1], ys[0], ys[ys.length - 1]);
    result.xs = xs;
    result.ys = ys;
    return result;
  }

  /**
   * A function of one variable known at sample points, interpolated by a Hipparchus Akima spline.
   * Only a numeric argument gives a value; a symbolic one leaves <code>f(x)</code> standing.
   */
  private static class SampledFunctionExpr
      extends InterpolatingFunctionExpr<org.hipparchus.analysis.UnivariateFunction> {
    private static final long serialVersionUID = 2470558337467213207L;

    public SampledFunctionExpr(final org.hipparchus.analysis.UnivariateFunction function,
        double min, double max) {
      super(function, min, max);
    }

    /** The value at <code>x</code>, a point outside the samples taken from their edge. */
    public IExpr valueAt(double x) {
      return F.num(toData().value(Math.max(min, Math.min(max, x))));
    }

    @Override
    public IExpr copy() {
      return new SampledFunctionExpr(toData(), min, max);
    }

    /** The spline is opaque: shown as its domain and a placeholder for the data. */
    @Override
    public String toString() {
      return "InterpolatingFunction({{" + min + "," + max + "}},<>)";
    }

    @Override
    public String fullFormString() {
      return toString();
    }

    @Override
    public String toHTML() {
      return toString();
    }
  }

  /**
   * A function of one variable from its values at sample points.
   *
   * @param xs the sample points, strictly increasing, at least 5 of them
   * @param ys the values at the sample points
   */
  public static InterpolatingFunctionExpr newSampled(double[] xs, double[] ys) {
    return new SampledFunctionExpr(
        new org.hipparchus.analysis.interpolation.AkimaSplineInterpolator().interpolate(xs, ys),
        xs[0], xs[xs.length - 1]);
  }

  /** */
  private static final long serialVersionUID = -3183236658957651705L;

  /**
   * Create a new instance wrapping a {@link FieldHermiteInterpolator}.
   *
   * @param interpolator the hermite interpolator model
   * @param min the minimum domain value
   * @param max the maximum domain value
   * @return a new {@link InterpolatingFunctionExpr} instance
   */
  public static InterpolatingFunctionExpr newInstance(
      final FieldHermiteInterpolator<IExpr> interpolator, double min, double max) {
    return new HermiteFunctionExpr(interpolator, min, max);
  }

  /**
   * Create a new instance wrapping an {@link IAST} interpolator.
   *
   * @param interpolator the AST representing the interpolator
   * @param min the minimum domain value
   * @param max the maximum domain value
   * @return a new {@link InterpolatingFunctionExpr} instance
   */
  public static InterpolatingFunctionExpr newInstance(final IAST interpolator, double min,
      double max) {
    return new ASTFunctionExpr(interpolator, min, max);
  }

  /**
   * Create a new instance wrapping an {@link IAST} interpolator of polynomials of degree
   * <code>order</code>.
   *
   * @param interpolator the AST representing the interpolator
   * @param min the minimum domain value
   * @param max the maximum domain value
   * @param order the degree of the interpolating polynomials
   * @return a new {@link InterpolatingFunctionExpr} instance
   */
  public static InterpolatingFunctionExpr newInstance(final IAST interpolator, double min,
      double max, int order) {
    ASTFunctionExpr function = new ASTFunctionExpr(interpolator, min, max);
    function.order = order;
    return function;
  }

  /**
   * Create a new instance wrapping an {@link UpdatingMultipleLinearRegression} model.
   *
   * @param value the regression model instance
   * @param min the minimum domain value
   * @param max the maximum domain value
   * @return a new {@link InterpolatingFunctionExpr} instance
   */
  public static InterpolatingFunctionExpr newInstance(final UpdatingMultipleLinearRegression value,
      double min, double max) {
    return new InterpolatingFunctionExpr(value, min, max);
  }

  /**
   * Create a new instance wrapping the continuous extension of an integrated ODE.
   *
   * @param forward the model covering <code>[t0, max]</code>, or <code>null</code> if the requested
   *        range does not extend past <code>t0</code>
   * @param backward the model covering <code>[min, t0]</code>, or <code>null</code> if the
   *        requested range does not extend below <code>t0</code>
   * @param t0 the point the initial conditions were given at
   * @param component the component of the state vector this function reads
   * @param min the minimum domain value
   * @param max the maximum domain value
   * @return a new {@link InterpolatingFunctionExpr} instance
   */
  public static InterpolatingFunctionExpr newInstance(final DenseOutputModel forward,
      final DenseOutputModel backward, double t0, int component, double min, double max) {
    return new ODEFunctionExpr(forward, backward, t0, component, min, max);
  }

  /**
   * Create a new instance for component <code>component</code> of the solution of an ODE, whose
   * derivatives are the components up to <code>blockEnd</code>.
   */
  public static InterpolatingFunctionExpr newInstance(final DenseOutputModel forward,
      final DenseOutputModel backward, double t0, int component, int blockEnd, double min,
      double max) {
    return new ODEFunctionExpr(forward, backward, t0, component, blockEnd, false, min, max);
  }

  /**
   * Create a new instance for component <code>component</code> of the solution of an ODE, whose
   * derivatives are the components up to <code>blockEnd</code>, and whose components lie in the
   * state as <code>layout</code> says.
   *
   * @param layout where the components lie in the state, or <code>null</code> if each is one real
   *        number at its own index
   */
  public static InterpolatingFunctionExpr newInstance(final DenseOutputModel forward,
      final DenseOutputModel backward, double t0, int component, int blockEnd, double min,
      double max, StateLayout layout) {
    ODEFunctionExpr function =
        new ODEFunctionExpr(forward, backward, t0, component, blockEnd, false, min, max);
    function.layout = layout;
    return function;
  }

  /**
   * The <code>n</code>-th derivative of this function, or {@link F#NIL} if it isn't known.
   *
   * @param n a positive order
   */
  public IExpr derivative(int n) {
    return F.NIL;
  }

  /**
   * The derivative of this function, of the order <code>orders[i]</code> in its
   * <code>i</code>-th variable, as an interpolating function of its own.
   *
   * @param orders the non-negative orders of differentiation, one for each variable
   * @return {@link F#NIL} if the derivative can't be built from the stored model
   */
  public IExpr derivative(int[] orders) {
    return orders.length == 1 && orders[0] > 0 ? derivative(orders[0]) : F.NIL;
  }

  /**
   * Create a new instance for the solution of an ODE on the single point <code>t0</code>, where
   * nothing is integrated and the function is the initial value.
   *
   * @param initialState the state vector at <code>t0</code>
   * @param t0 the point the initial conditions were given at
   * @param component the component of the state vector this function reads
   * @return a new {@link InterpolatingFunctionExpr} instance
   */
  public static InterpolatingFunctionExpr newInstance(final double[] initialState, double t0,
      int component) {
    return newInstance(initialState, t0, component, null);
  }

  /**
   * Create a new instance for the solution of an ODE on the single point <code>t0</code>, whose
   * components lie in the state as <code>layout</code> says.
   *
   * @param layout where the components lie in the state, or <code>null</code> if each is one real
   *        number at its own index
   */
  public static InterpolatingFunctionExpr newInstance(final double[] initialState, double t0,
      int component, StateLayout layout) {
    ODEFunctionExpr function = new ODEFunctionExpr(null, null, t0, component, t0, t0);
    function.initialState = initialState;
    function.layout = layout;
    return function;
  }

  /**
   * Protected constructor used by concrete subclasses and factory methods.
   *
   * @param function the underlying model to store as data
   * @param min the minimum domain value (inclusive)
   * @param max the maximum domain value (inclusive)
   */
  protected InterpolatingFunctionExpr(final T function, double min, double max) {
    super(S.InterpolatingFunction, function);
    this.min = min;
    this.max = max;
  }

  /**
   * Equality checks consider the wrapped model data for equivalence.
   *
   * @param obj the object to compare with
   * @return {@code true} if the underlying data is equal, {@code false} otherwise
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof ODEFunctionExpr || obj instanceof GridInterpolatingFunctionExpr) {
      // they compare more than the model, so they only equal their own kind
      return false;
    }
    if (obj instanceof InterpolatingFunctionExpr) {
      return fData.equals(((InterpolatingFunctionExpr) obj).fData);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return (fData == null) ? 461 : 461 + fData.hashCode();
  }

  /**
   * Returns the expression hierarchy id for interpolated functions.
   *
   * @return the hierarchy id
   */
  @Override
  public int hierarchy() {
    return INTERPOLATEDFUNCTONID;
  }

  /**
   * Evaluate an AST that has this interpolating function as head and a single argument.
   * <p>
   * Behavior:
   * <ul>
   * <li>If the head is an {@link InterpolatingFunctionExpr} and the AST has one argument, attempt
   * to evaluate the argument to a numeric value.</li>
   * <li>If numeric and outside {@code [min, max]} a warning message is emitted via
   * {@link Errors#printMessage} indicating extrapolation will be used.</li>
   * <li>If the head is a {@link HermiteFunctionExpr} and the stored model is a
   * {@link FieldHermiteInterpolator}, the interpolator is invoked and a numeric result (or list) is
   * returned.</li>
   * <li>If the head is an {@link ASTFunctionExpr} and the stored model is an {@link IAST}, the
   * interpolator AST is applied to the argument via {@link F#unaryAST1}.</li>
   * <li>For non-numeric/symbolic arguments or unsupported models {@link F#NIL} is returned.</li>
   * </ul>
   *
   * @param ast the AST to evaluate (expected with this interpolator as head)
   * @param engine the evaluation engine (used for error/warning messages)
   * @return the evaluated expression or {@link F#NIL} if not applicable
   */
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    if (ast.head() instanceof GridFunctionExpr && ast.isAST2()) {
      // f(x, y) of a function of two variables: numbers give its value, anything else stays
      double x = ast.arg1().evalfNaN();
      double y = ast.arg2().evalfNaN();
      if (Double.isNaN(x) || Double.isNaN(y)) {
        return F.NIL;
      }
      return ((GridFunctionExpr) ast.head()).valueAt(x, y);
    }
    if (ast.head() instanceof InterpolatingFunctionExpr && ast.isAST1()) {
      IExpr arg1 = ast.arg1();
      if (arg1.isString()) {
        InterpolatingFunctionExpr<?> function = (InterpolatingFunctionExpr<?>) ast.head();
        String property = arg1.toString();
        if (property.equals("Domain")) {
          if (function instanceof GridFunctionExpr) {
            // a function of two variables has a range for each
            GridFunctionExpr grid = (GridFunctionExpr) function;
            return F.list(F.list(F.num(grid.min), F.num(grid.max)),
                F.list(F.num(grid.yMin), F.num(grid.yMax)));
          }
          if (function.exactDomain.isPresent()) {
            return F.list(function.exactDomain);
          }
          return F.list(F.list(F.num(function.min), F.num(function.max)));
        }
        if (property.equals("Coordinates") && function instanceof GridFunctionExpr) {
          // the grid points along each axis
          GridFunctionExpr grid = (GridFunctionExpr) function;
          if (grid.xs != null && grid.ys != null) {
            return F.list(F.List(grid.xs), F.List(grid.ys));
          }
        }
        if (property.equals("InterpolationOrder") && function instanceof ASTFunctionExpr) {
          return F.list(F.ZZ(function.order));
        }
        return F.NIL;
      }
      if (arg1.isList() && ast.head() instanceof ODEFunctionExpr) {
        // the solution at each point of a list
        final IExpr head = ast.head();
        return F.mapList((IAST) arg1, (IExpr point) -> F.unaryAST1(head, point));
      }
      if (arg1.isComplex() || arg1.isComplexNumeric()) {
        return F.NIL;
      }
      boolean evaled = false;
      double value = arg1.evalfNaN();
      // a non-numeric value falls through for symbolic arguments
      if (!Double.isNaN(value)) {
        evaled = true;
        if (value < min || value > max) {
          // Input value `1` lies outside the range of data in the interpolating function.
          // Extrapolation will be used.
          Errors.printMessage(ast.topHead(), "dmval", F.list(F.list(arg1)), engine);
        }
      }
      final IExpr head = ast.head();
      if (head instanceof ODEFunctionExpr) {
        if (evaled) {
          return ((ODEFunctionExpr) head).valueAt(value);
        }
        // a symbolic argument leaves y(x) -> InterpolatingFunction(...)(x) standing, which is what
        // the caller asked for: the model cannot be written out as an expression
      } else if (head instanceof SampledFunctionExpr) {
        if (evaled) {
          return ((SampledFunctionExpr) head).valueAt(value);
        }
      } else if (head instanceof HermiteFunctionExpr) {
        if (evaled) {
          InterpolatingFunctionExpr<IAST> function = (InterpolatingFunctionExpr<IAST>) head;
          Object model = function.toData();
          if (model instanceof FieldHermiteInterpolator) {
            FieldHermiteInterpolator<IExpr> interpolator = (FieldHermiteInterpolator<IExpr>) model;
            IExpr[] arr = interpolator.value(arg1);
            if (arr.length == 1) {
              return arr[0];
            }
            return F.List(arr);
          }
        }
      } else if (head instanceof ASTFunctionExpr) {
        ASTFunctionExpr function = (ASTFunctionExpr) head;
        Object model = function.toData();
        if (model instanceof IAST && (evaled || !function.numericArgumentsOnly)) {
          IAST interpolator = (IAST) model;
          return F.unaryAST1(interpolator, arg1);
        }
      }
    }

    return F.NIL;
  }

  @Override
  public IExpr copy() {
    InterpolatingFunctionExpr<T> function = new InterpolatingFunctionExpr<T>(fData, min, max);
    function.exactDomain = exactDomain;
    return function;
  }

  @Override
  public IAST normal(boolean nilIfUnevaluated) {
    Object model = toData();
    if (model instanceof SimpleRegression) {
      SimpleRegression simpleModel = (SimpleRegression) model;
      return F.Plus(F.num(simpleModel.getIntercept()), F.Times(F.num(simpleModel.getSlope()), S.C));
    }
    return F.NIL;
  }
}
