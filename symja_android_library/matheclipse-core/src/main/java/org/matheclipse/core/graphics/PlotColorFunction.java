package org.matheclipse.core.graphics;

import java.util.function.DoubleFunction;
import java.util.function.DoubleUnaryOperator;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.svg.ColorUtil;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * The {@code ColorFunction} of a plot: which colour a sample gets.
 *
 * <p>
 * Which numbers a colour function is handed depends on what is being drawn, and the Wolfram
 * Language fixes that per family: a curve is coloured from {@code x, y}, a surface from
 * {@code x, y, z}, a density plot from the value alone. {@link Family} is that table. A caller
 * names its family, says what range each of those arguments spans, and then asks for the colour of
 * one sample at a time.
 *
 * <p>
 * A colour scheme named as a string is not a function of the whole tuple but of one argument of it
 * - the second for a curve, the third for a surface - which is why {@code ColorFunction ->
 * "Rainbow"} follows a curve's height rather than sweeping left to right. {@link Family} carries
 * that slot too.
 *
 * <p>
 * Construction decides whether there is anything to do at all: {@link Builder#build()} answers
 * {@code null} for {@code Automatic}, for {@code None}, for a name no gradient goes by, and for a
 * function that never produces a colour. A caller that gets {@code null} paints the way it always
 * did, and pays nothing per sample. A caller that gets an instance can rely on {@link #color} to
 * answer with a colour every time.
 */
public final class PlotColorFunction {

  /**
   * What a plot hands its colour function, and which of those a colour scheme name is applied to.
   *
   * <p>
   * These are the same tuples {@code RegionFunction} receives on the same plots, so one parameter
   * list serves both options.
   */
  public enum Family {
    /** {@code Plot}, the {@code Log} plots, {@code ListPlot} and friends: {@code x, y}. */
    CURVE_2D(2, 2),
    /** {@code ParametricPlot}: {@code x, y, u}. */
    PARAMETRIC_2D(3, 2),
    /** {@code ParametricPlot} over two parameters: {@code x, y, u, v}. */
    PARAMETRIC_2D_UV(4, 2),
    /** {@code PolarPlot}, {@code ListPolarPlot}: {@code x, y, theta, r}. */
    POLAR_2D(4, 2),
    /** {@code ContourPlot}, {@code DensityPlot} and their list forms: the value. */
    FIELD_2D(1, 1),
    /** {@code ArrayPlot}, {@code MatrixPlot}, {@code ReliefImage}, {@code Raster}: the cell. */
    ARRAY(1, 1),
    /** {@code Histogram}, the charts, {@code WordCloud}: the height or the datum. */
    CHART(1, 1),
    /** {@code Plot3D}, {@code ListPlot3D} and friends: {@code x, y, z}. */
    SURFACE_3D(3, 3),
    /** {@code ParametricPlot3D} over one parameter: {@code x, y, z, u}. */
    PARAMETRIC_3D(4, 3),
    /** {@code ParametricPlot3D} over two: {@code x, y, z, u, v}. */
    PARAMETRIC_3D_UV(5, 3),
    /** {@code SphericalPlot3D}: {@code x, y, z, theta, phi, r}. */
    SPHERICAL_3D(6, 3),
    /** {@code RevolutionPlot3D}: {@code x, y, z, t, theta, r}. */
    REVOLUTION_3D(6, 3),
    /** {@code ContourPlot3D}: {@code x, y, z, f}. */
    CONTOUR_3D(4, 4);

    /** How many arguments the function is given. */
    public final int arity;

    /** Which of them, counting from one, a colour scheme name is applied to. */
    public final int gradientSlot;

    Family(int arity, int gradientSlot) {
      this.arity = arity;
      this.gradientSlot = gradientSlot;
    }
  }

  /** Where the colour is going, which decides what may be made of a directive. */
  public enum Sink {
    /**
     * A 2D primitive list, which can carry a whole directive: a thickness beside a colour, an
     * opacity in front of one.
     */
    DIRECTIVE,
    /** A raster cell or a {@code VertexColors} entry, which is one colour and nothing else. */
    FLAT
  }

  /** How finely a coordinate is rounded before it is used as a cache key. */
  private static final double CACHE_GRID = 512.0;

  /** Beyond this many distinct samples the cache stops growing, rather than the heap. */
  private static final int CACHE_LIMIT = 4096;

  private final Family family;
  private final IExpr function;
  private final boolean gradient;
  private final boolean scaled;
  private final Sink sink;
  private final double[] lo;
  private final double[] hi;
  private final DoubleUnaryOperator[] scalers;
  private final DoubleFunction<IExpr> fallback;
  private final ISymbol plotSymbol;
  private final EvalEngine engine;
  private final java.util.HashMap<Long, IExpr> cache = new java.util.HashMap<>();

  /** Set once the first sample fails, so a bad function costs one message and not one per cell. */
  private boolean reported = false;

  private PlotColorFunction(Builder builder) {
    this.family = builder.family;
    this.function = builder.function;
    this.gradient = builder.gradient;
    this.scaled = builder.scaled;
    this.sink = builder.sink;
    this.lo = builder.lo;
    this.hi = builder.hi;
    this.scalers = builder.scalers;
    this.fallback = builder.fallback;
    this.plotSymbol = builder.plotSymbol;
    this.engine = builder.engine;
  }

  /**
   * Begin reading the {@code ColorFunction} of a plot.
   *
   * @param colorFunction the option value; {@code Automatic}, {@code None} and {@code null} all
   *        mean the plot colours itself
   * @param colorFunctionScaling the {@code ColorFunctionScaling} option value
   * @param plotSymbol the plot a complaint is reported against
   */
  public static Builder of(Family family, IExpr colorFunction, IExpr colorFunctionScaling,
      ISymbol plotSymbol, EvalEngine engine) {
    return new Builder(family, colorFunction, colorFunctionScaling, plotSymbol, engine);
  }

  /** Collects what the family cannot know: the ranges, the default colouring, the destination. */
  public static final class Builder {
    private final Family family;
    private final IExpr spec;
    private final boolean scaled;
    private final ISymbol plotSymbol;
    private final EvalEngine engine;
    private final double[] lo;
    private final double[] hi;
    private final DoubleUnaryOperator[] scalers;
    private IExpr function = F.NIL;
    private boolean gradient = false;
    private Sink sink = Sink.DIRECTIVE;
    private DoubleFunction<IExpr> fallback = t -> F.NIL;

    private Builder(Family family, IExpr colorFunction, IExpr colorFunctionScaling,
        ISymbol plotSymbol, EvalEngine engine) {
      this.family = family;
      this.spec = colorFunction;
      this.scaled = colorFunctionScaling == null || !colorFunctionScaling.isFalse();
      this.plotSymbol = plotSymbol;
      this.engine = engine;
      this.lo = new double[family.arity + 1];
      this.hi = new double[family.arity + 1];
      this.scalers = new DoubleUnaryOperator[family.arity + 1];
      java.util.Arrays.fill(lo, Double.NaN);
      java.util.Arrays.fill(hi, Double.NaN);
    }

    /**
     * The range argument {@code slot} spans, counting from one. A slot left unset is passed through
     * as it came, which is what an already normalised quantity wants.
     */
    public Builder range(int slot, double low, double high) {
      if (slot >= 1 && slot <= family.arity) {
        lo[slot] = low;
        hi[slot] = high;
      }
      return this;
    }

    /** The ranges of the leading slots, as {@code lo1, hi1, lo2, hi2, ...}. */
    public Builder ranges(double... loHiPairs) {
      for (int i = 0; i + 1 < loHiPairs.length; i += 2) {
        range(i / 2 + 1, loHiPairs[i], loHiPairs[i + 1]);
      }
      return this;
    }

    /**
     * A slot whose scaling is not a straight line - a rank among the samples, say - given as the
     * map onto {@code 0..1}. Only consulted when the arguments are scaled at all.
     */
    public Builder scaler(int slot, DoubleUnaryOperator toUnit) {
      if (slot >= 1 && slot <= family.arity) {
        scalers[slot] = toUnit;
      }
      return this;
    }

    /** The colour to use for a sample the caller's function could not answer for. */
    public Builder fallback(DoubleFunction<IExpr> ofGradientSlot) {
      this.fallback = ofGradientSlot == null ? t -> F.NIL : ofGradientSlot;
      return this;
    }

    /** The one colour to use for a sample the caller's function could not answer for. */
    public Builder fallback(IExpr constant) {
      this.fallback = t -> constant;
      return this;
    }

    public Builder sink(Sink destination) {
      this.sink = destination;
      return this;
    }

    /**
     * @return the colouring, or {@code null} when there is none to apply and the caller should
     *         paint the way it always did
     */
    public PlotColorFunction build() {
      if (spec == null || !spec.isPresent() || spec.isAutomatic() || spec.isNone()) {
        return null;
      }
      if (spec.isString()) {
        IExpr resolved = resolveGradient(spec);
        if (resolved == null) {
          // `1` is not a known color gradient for `2`; using the default coloring.
          Errors.printMessage(plotSymbol, "cfname", F.list(spec, plotSymbol), engine);
          return null;
        }
        function = resolved;
        gradient = true;
      } else if (spec.isAST(S.ColorDataFunction)) {
        // a ColorDataFunction only ever answers to one argument, so it is a gradient however it
        // was spelled
        function = spec;
        gradient = true;
      } else {
        function = spec;
      }
      PlotColorFunction colorFunction = new PlotColorFunction(this);
      if (!colorFunction.answers()) {
        // `1` did not evaluate to a color; using the default coloring.
        Errors.printMessage(plotSymbol, "cfeval", F.list(plotSymbol), engine);
        return null;
      }
      return colorFunction;
    }

    /** @return {@code ColorData(name)} when the name is one, {@code null} otherwise */
    private IExpr resolveGradient(IExpr name) {
      try {
        IExpr resolved = engine.evaluate(F.ColorData(name));
        // an unknown name leaves ColorData unevaluated, which is how it is recognised
        return resolved.isAST() && !resolved.isAST(S.ColorData) ? resolved : null;
      } catch (RuntimeException rex) {
        return null;
      }
    }
  }

  /**
   * Whether the function answers with a colour anywhere at all.
   *
   * <p>
   * Sampled at both ends and the middle of the range the gradient slot spans, rather than at one
   * point: a function such as {@code Hue(1/#2)} has nothing to say at one end and everything to say
   * elsewhere, and a single probe at the wrong end would throw the whole plot away.
   */
  private boolean answers() {
    double[] probe = new double[family.arity];
    for (int i = 0; i < probe.length; i++) {
      probe[i] = midpoint(i + 1);
    }
    int slot = family.gradientSlot - 1;
    for (double at : new double[] {low(family.gradientSlot), midpoint(family.gradientSlot),
        high(family.gradientSlot)}) {
      probe[slot] = at;
      if (evaluate(probe).isPresent()) {
        return true;
      }
    }
    return false;
  }

  private double low(int slot) {
    return Double.isNaN(lo[slot]) ? 0.0 : lo[slot];
  }

  private double high(int slot) {
    return Double.isNaN(hi[slot]) ? 1.0 : hi[slot];
  }

  private double midpoint(int slot) {
    return (low(slot) + high(slot)) / 2.0;
  }

  /**
   * The colour of one sample.
   *
   * @param coordinates the family's tuple, in the plot's own coordinates; scaling happens here
   * @return a colour or a directive, never {@link F#NIL}
   */
  public IExpr color(double... coordinates) {
    IExpr color = cached(coordinates);
    if (color.isPresent()) {
      return color;
    }
    if (!reported) {
      reported = true;
      // `1` did not evaluate to a color; using the default coloring.
      Errors.printMessage(plotSymbol, "cfeval", F.list(plotSymbol), engine);
    }
    IExpr defaulted =
        fallback.apply(scale(family.gradientSlot, value(coordinates, family.gradientSlot)));
    return defaulted.isPresent() ? defaulted : F.RGBColor(F.C0, F.C0, F.C0);
  }

  private IExpr cached(double[] coordinates) {
    long key = 0L;
    for (int i = 0; i < family.arity; i++) {
      key = key * 1000003L + Math.round(scale(i + 1, value(coordinates, i + 1)) * CACHE_GRID);
    }
    IExpr hit = cache.get(key);
    if (hit != null) {
      return hit;
    }
    IExpr color = evaluate(coordinates);
    if (cache.size() < CACHE_LIMIT) {
      cache.put(key, color);
    }
    return color;
  }

  private static double value(double[] coordinates, int slot) {
    return slot <= coordinates.length ? coordinates[slot - 1] : 0.0;
  }

  /**
   * Apply the function to one tuple.
   *
   * <p>
   * A gradient is a function of its one slot; anything else is given the whole tuple. There is no
   * probing for how many arguments the caller's function wants: a {@code Function} binds the
   * parameters it names and drops the rest, so a function of fewer arguments than the family
   * supplies works as it stands, and one that names more is a genuine mistake worth reporting.
   *
   * @return the colour, or {@link F#NIL} when the call produced none
   */
  private IExpr evaluate(double[] coordinates) {
    IAST call;
    if (gradient) {
      // a colour scheme is defined on 0..1 and has nothing to say outside it, so an argument that
      // was not scaled - or a value beyond the range the caller declared - takes the nearest end
      // of the scheme rather than costing the sample its colour
      double at = scale(family.gradientSlot, value(coordinates, family.gradientSlot));
      call = F.unaryAST1(function, F.num(at < 0.0 ? 0.0 : at > 1.0 ? 1.0 : at));
    } else {
      IExpr[] arguments = new IExpr[family.arity];
      for (int i = 0; i < family.arity; i++) {
        arguments[i] = F.num(scale(i + 1, value(coordinates, i + 1)));
      }
      call = F.ast(arguments, function);
    }
    try {
      IExpr result = engine.evaluate(call);
      if (!GraphicsOptions.isColorExpr(result)) {
        return F.NIL;
      }
      if (result.isAST(S.Glow) || result.isAST(S.Specularity)) {
        return dropped(result);
      }
      return sink == Sink.FLAT ? flatten(result, coordinates) : result;
    } catch (RuntimeException rex) {
      return F.NIL;
    }
  }

  /** A lighting directive cannot be varied per sample by either renderer. */
  private IExpr dropped(IExpr directive) {
    if (!reported) {
      reported = true;
      // `1` cannot be applied to one sample by `2` and was ignored.
      Errors.printMessage(plotSymbol, "cfdrop", F.list(directive.head(), plotSymbol), engine);
    }
    return F.NIL;
  }

  /**
   * Reduce a directive to the single colour a raster cell or a vertex can hold.
   *
   * <p>
   * Only a directive needs it. A colour that stands on its own - a {@code GrayLevel}, a
   * {@code Hue}, a named one - is already something a cell can hold, and rewriting it as an
   * {@code RGBColor} would only make the picture harder to read back.
   */
  private IExpr flatten(IExpr result, double[] coordinates) {
    if (ColorUtil.parse(result) != null) {
      return result;
    }
    IExpr under =
        fallback.apply(scale(family.gradientSlot, value(coordinates, family.gradientSlot)));
    return GraphicsOptions.toColorExpr(result, under);
  }

  /** Map one argument onto {@code 0..1} over the range its slot spans. */
  private double scale(int slot, double raw) {
    if (!scaled) {
      return raw;
    }
    if (scalers[slot] != null) {
      return scalers[slot].applyAsDouble(raw);
    }
    double low = lo[slot];
    double high = hi[slot];
    if (Double.isNaN(low) || Double.isNaN(high)) {
      // a slot whose range nobody declared is already in the units the function wants
      return raw;
    }
    if (!(high > low)) {
      // everything sampled the same value, so there is no position within the range to report
      return 0.5;
    }
    double t = (raw - low) / (high - low);
    return t < 0.0 ? 0.0 : t > 1.0 ? 1.0 : t;
  }

  /** Whether the arguments are mapped onto {@code 0..1} before the function sees them. */
  public boolean isScaled() {
    return scaled;
  }

  /**
   * Whether this is a colour scheme applied to one argument rather than a function of the tuple.
   */
  public boolean isGradient() {
    return gradient;
  }

  public Family family() {
    return family;
  }

  /**
   * The function a legend colour bar should sample over {@code 0..1}.
   *
   * <p>
   * Only a gradient has one: it is already a function of a single number running that range, and it
   * is the same number the bar is labelled with. A colour function of a whole tuple has no such
   * axis, so a legend beside it has nothing honest to draw.
   *
   * @return the function, or {@link F#NIL}
   */
  public IExpr legendFunction() {
    return gradient ? function : F.NIL;
  }
}
