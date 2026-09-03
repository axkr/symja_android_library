package org.matheclipse.core.graphics;

import java.awt.Color;
import java.util.Arrays;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.svg.ColorUtil;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * The domain colouring of a complex plot: which colour the value {@code f} of the plotted function
 * gets at the sample point {@code z}.
 *
 * <p>
 * A colour is made of two independent parts, the way the Wolfram Language describes them. The
 * <b>base colour</b> comes from {@code Arg(f)} running round the colour wheel - red on the positive
 * real axis, counterclockwise - or from a colour function the caller supplied. The <b>shading</b>
 * then lightens or darkens that colour to bring out a feature: how large {@code Abs(f)} is, or
 * which band of {@code Log(Abs(f))} the point falls in. {@code ColorFunction -> {cfunc, sfunc}}
 * names the two separately.
 *
 * <p>
 * The shading is a single signed factor. Negative blends the base colour towards black, positive
 * towards white, and only the extremes -1 and +1 reach them. That is the point of doing it this
 * way: a cyclic scheme stays inside a band, so a level set of {@code Abs(f)} shows up as a change
 * of brightness rather than as a black curve drawn across the picture.
 *
 * <p>
 * Colouring runs in two passes because several of the schemes, and
 * {@code ColorFunctionScaling -> True}, need to know the distribution of the values before any of
 * them can be coloured: {@link #observe} every sample, then {@link #prepare}, then {@link #color}.
 */
public final class ComplexColoring {

  // --------------------------------------------------------------- tunable constants
  //
  // The Wolfram Language documents which quantity each scheme cycles over but never the period,
  // the count, or the amplitude, so every constant here was chosen by eye against the reference
  // pictures rather than derived. They are the knobs to turn when the look needs adjusting.

  /** One dark to light cycle of {@code Log(Abs(f))} per factor of two. */
  static final double LOG_ABS_PERIOD = Math.log(2.0);

  /** Dark to light cycles of {@code Arg(f)} around the full turn. */
  static final int ARG_CYCLES = 12;

  /** One dark to light cycle of {@code Re(f)} and of {@code Im(f)} per unit. */
  static final double RE_IM_PERIOD = 1.0;

  /** The darkest point of a dark cycle: brightness never drops below {@code 1 + CYCLE_DARK}. */
  static final double CYCLE_DARK = -0.30;

  /** The lightest point of a light cycle, as a fraction of the way to white. */
  static final double CYCLE_LIGHT = 0.5;

  /** Softness of the {@code Automatic} shading, in units of {@code Log(Abs(f))}. Larger is gentler. */
  static final double AUTOMATIC_SOFTNESS = 3.0;

  /** The quantile of {@code Abs(f)} above which {@code "LocalMaxAbs"} starts to lighten. */
  static final double LOCAL_MAX_QUANTILE = 0.75;

  /** How far {@code "QuantileAbs"} reaches towards black and white at the ends of the data. */
  static final double QUANTILE_RANGE = 0.8;

  /** The {@code Abs(f)} below which {@code "ShiftedCyclicLogAbs"} leaves the hue alone. */
  static final double SHIFT_THRESHOLD = 1.0;

  /** The saturation of the hue the base colour uses when the caller named no colour function. */
  static final double BASE_SATURATION = 1.0;

  /** {@code Log(Abs(f))} at {@code Abs(f) == 0}, so that the cyclic schemes stay defined there. */
  private static final double MIN_LOG_ABS = Math.log(Double.MIN_NORMAL);

  /** The shading schemes, under the names the Wolfram Language gives them. */
  public enum Shading {
    /** Gentle shading by {@code Abs(f)}: zeros dark, poles light. */
    AUTOMATIC("Automatic"),
    /** No shading at all, so the picture shows the phase and nothing else. */
    NONE("None"),
    /** Lightens only where {@code Abs(f)} is large. */
    MAX_ABS("MaxAbs"),
    /** Lightens the upper quantile of the {@code Abs(f)} that was actually sampled. */
    LOCAL_MAX_ABS("LocalMaxAbs"),
    /** Dark to light across the whole range of {@code Abs(f)}: zeros black, poles white. */
    GLOBAL_ABS("GlobalAbs"),
    /** Dark to light by the rank of {@code Abs(f)} among the samples. */
    QUANTILE_ABS("QuantileAbs"),
    /** Cyclic dark to light bands of {@code Log(Abs(f))}. */
    CYCLIC_LOG_ABS("CyclicLogAbs"),
    /** Cyclic dark to light sectors of {@code Arg(f)}. */
    CYCLIC_ARG("CyclicArg"),
    /** Both of the above, giving a polar grid. */
    CYCLIC_LOG_ABS_ARG("CyclicLogAbsArg"),
    /** Dark cycles of {@code Re(f)} and {@code Im(f)}, light cycles of {@code Log(Abs(f))}. */
    CYCLIC_RE_IM_LOG_ABS("CyclicReImLogAbs"),
    /** Cyclic bands of {@code Log(Abs(f))}, but only outside {@code Abs(f) <= 1}. */
    SHIFTED_CYCLIC_LOG_ABS("ShiftedCyclicLogAbs");

    private final String wolframName;

    Shading(String wolframName) {
      this.wolframName = wolframName;
    }

    /** The name the Wolfram Language uses for this scheme. */
    public String wolframName() {
      return wolframName;
    }

    /**
     * @return the scheme of that name, or {@code null} when the name does not belong to one; the
     *         comparison ignores case
     */
    public static Shading byName(String name) {
      if (name != null) {
        for (Shading shading : values()) {
          if (shading.wolframName.equalsIgnoreCase(name)) {
            return shading;
          }
        }
      }
      return null;
    }
  }

  /** The colour a value gets before it is shaded. */
  private interface BaseColor {
    /** @return the red, green and blue components in 0..1 */
    float[] rgb(double re, double im, double fre, double fim);
  }

  private final Shading shading;
  private final BaseColor base;
  private final boolean scaled;

  /** The sampled rectangle, {@code {x0, x1, y0, y1}}, which scales {@code Re(z)} and {@code Im(z)}. */
  private final double[] plotBox;

  /** Every finite {@code Abs(f)} that was observed; sorted by {@link #prepare}. */
  private double[] absSamples = new double[256];
  private int absCount = 0;

  private double minAbs = Double.POSITIVE_INFINITY;
  private double maxAbs = Double.NEGATIVE_INFINITY;
  private double minRe = Double.POSITIVE_INFINITY;
  private double maxRe = Double.NEGATIVE_INFINITY;
  private double minIm = Double.POSITIVE_INFINITY;
  private double maxIm = Double.NEGATIVE_INFINITY;
  private double maxAbsZ = 0.0;
  private boolean prepared = false;

  private ComplexColoring(Shading shading, IExpr baseSpec, boolean scaled, double[] plotBox,
      EvalEngine engine) {
    this.shading = shading;
    this.scaled = scaled;
    this.plotBox = plotBox;
    this.base = baseSpec == null ? null : new FunctionColor(baseSpec, scaled, engine);
  }

  /**
   * Read the {@code ColorFunction} and {@code ColorFunctionScaling} of a complex plot.
   *
   * <p>
   * A name this class knows selects a shading scheme over the default hue. A name
   * {@code ColorData} knows is a gradient, and colours the plot by phase with no shading, which is
   * what a caller writing {@code ColorFunction -> "Rainbow"} is asking for. A function colours the
   * plot itself and is shaded automatically, the way the Wolfram Language treats a bare function.
   * A name that is neither falls back to {@code Automatic} and says so once, rather than drawing
   * nothing.
   *
   * @param plotBox the sampled rectangle {@code {x0, x1, y0, y1}}
   * @param plotSymbol the plot an unknown name is reported against
   * @return never {@code null}
   */
  public static ComplexColoring of(IExpr colorFunction, IExpr colorFunctionScaling,
      double[] plotBox, ISymbol plotSymbol, EvalEngine engine) {
    boolean scaled = colorFunctionScaling == null || !colorFunctionScaling.isFalse();
    IExpr colorSpec = colorFunction;
    IExpr shadingSpec = null;
    if (colorFunction != null && colorFunction.isList() && ((IAST) colorFunction).argSize() == 2) {
      // ColorFunction -> {cfunc, sfunc} names the base colour and the shading separately
      colorSpec = ((IAST) colorFunction).arg1();
      shadingSpec = ((IAST) colorFunction).arg2();
    }

    // the shading is read first: it is what reports an unknown name, so that the base colour can
    // fall back to the default hue without saying anything twice
    Shading shading = readShading(shadingSpec, colorSpec, plotSymbol, engine);
    return new ComplexColoring(shading, readBase(colorSpec, engine), scaled, plotBox, engine);
  }

  /**
   * Whether the value asks for one of this class's own schemes rather than for a gradient or a
   * function of its own.
   */
  public static boolean isScheme(IExpr colorFunction) {
    if (colorFunction == null || colorFunction.isAutomatic() || colorFunction.isNone()) {
      return true;
    }
    if (colorFunction.isList() && ((IAST) colorFunction).argSize() == 2) {
      return true;
    }
    return colorFunction.isString()
        && Shading.byName(colorFunction.toString()) != null;
  }

  /** The shading half of the option, given the base colour half for the bare forms. */
  private static Shading readShading(IExpr shadingSpec, IExpr colorSpec, ISymbol plotSymbol,
      EvalEngine engine) {
    if (shadingSpec != null) {
      // the sfunc of an explicit {cfunc, sfunc} pair
      if (shadingSpec.isNone()) {
        return Shading.NONE;
      }
      if (shadingSpec.isAutomatic()) {
        return Shading.AUTOMATIC;
      }
      if (shadingSpec.isString()) {
        Shading named = Shading.byName(shadingSpec.toString());
        return named != null ? named : unknownShading(shadingSpec, plotSymbol, engine);
      }
      return Shading.AUTOMATIC;
    }
    if (colorSpec == null || colorSpec.isAutomatic()) {
      return Shading.AUTOMATIC;
    }
    if (colorSpec.isNone()) {
      return Shading.NONE;
    }
    if (colorSpec.isString()) {
      Shading named = Shading.byName(colorSpec.toString());
      if (named != null) {
        return named;
      }
      // a gradient name colours by phase and asks for no shading of its own
      return gradientFunction(colorSpec, engine) != null ? Shading.NONE
          : unknownShading(colorSpec, plotSymbol, engine);
    }
    // a bare function is a base colour, and the Wolfram Language shades it automatically
    return Shading.AUTOMATIC;
  }

  private static Shading unknownShading(IExpr spec, ISymbol plotSymbol, EvalEngine engine) {
    // `1` is not a known shading scheme for `2`; using Automatic.
    Errors.printMessage(plotSymbol, "cfshade", F.list(spec, plotSymbol), engine);
    return Shading.AUTOMATIC;
  }

  /**
   * The base colour half of the option, or {@code null} for the default hue.
   *
   * <p>
   * A string is either a gradient - which becomes the function that paints the plot - or a shading
   * scheme or a typo, both of which keep the hue. An unknown name has already been reported by
   * {@link #readShading}, so nothing is said about it here.
   */
  private static IExpr readBase(IExpr colorSpec, EvalEngine engine) {
    if (colorSpec == null || colorSpec.isAutomatic() || colorSpec.isNone()) {
      return null;
    }
    if (colorSpec.isString()) {
      return Shading.byName(colorSpec.toString()) != null ? null
          : gradientFunction(colorSpec, engine);
    }
    return colorSpec;
  }

  /**
   * @return {@code ColorData(name)} when the name is a known gradient, {@code null} otherwise; an
   *         unknown name leaves {@code ColorData} unevaluated, which is how it is recognised
   */
  private static IExpr gradientFunction(IExpr name, EvalEngine engine) {
    try {
      IExpr gradient = engine.evaluate(F.ColorData(name));
      return gradient.isAST() && !gradient.isAST(S.ColorData) ? gradient : null;
    } catch (RuntimeException rex) {
      return null;
    }
  }

  // --------------------------------------------------------------- sampling

  /**
   * Record one sample, so that the scaling and the schemes that need the distribution of the
   * values have something to work from. A pole - a value that is not finite - is counted as a
   * sample but contributes no magnitude.
   */
  public void observe(double re, double im, double fre, double fim) {
    maxAbsZ = Math.max(maxAbsZ, Math.hypot(re, im));
    if (!Double.isFinite(fre) || !Double.isFinite(fim)) {
      return;
    }
    double abs = Math.hypot(fre, fim);
    if (!Double.isFinite(abs)) {
      return;
    }
    minRe = Math.min(minRe, fre);
    maxRe = Math.max(maxRe, fre);
    minIm = Math.min(minIm, fim);
    maxIm = Math.max(maxIm, fim);
    minAbs = Math.min(minAbs, abs);
    maxAbs = Math.max(maxAbs, abs);
    if (absCount == absSamples.length) {
      absSamples = Arrays.copyOf(absSamples, absCount * 2);
    }
    absSamples[absCount++] = abs;
    prepared = false;
  }

  /** Close the sampling pass. Calling it twice costs nothing. */
  public void prepare() {
    if (!prepared) {
      Arrays.sort(absSamples, 0, absCount);
      prepared = true;
    }
  }

  // --------------------------------------------------------------- colouring

  /**
   * The colour of one sample.
   *
   * @return an {@code RGBColor}; a pole is white, and nothing is ever {@link F#NIL}, so a caller
   *         that decided to paint a cell always gets a colour for it
   */
  public IExpr color(double re, double im, double fre, double fim) {
    prepare();
    if (!Double.isFinite(fre) || !Double.isFinite(fim)) {
      // a pole takes the light end of every scheme
      return F.RGBColor(F.C1, F.C1, F.C1);
    }
    float[] rgb = base == null ? null : base.rgb(re, im, fre, fim);
    if (rgb == null) {
      rgb = hueColor(fre, fim);
    }
    return blend(rgb, shade(fre, fim));
  }

  /**
   * The shading factor of one value, from -1 (black) to +1 (white).
   *
   * <p>
   * Every cyclic scheme is bounded away from both ends, which is what keeps a level set of
   * {@code Abs(f)} from being drawn as a black curve.
   */
  double shade(double fre, double fim) {
    prepare();
    double abs = Math.hypot(fre, fim);
    double logAbs = abs > 0.0 ? Math.log(abs) : MIN_LOG_ABS;
    switch (shading) {
      case NONE:
        return 0.0;
      case GLOBAL_ABS:
        return Math.tanh(logAbs / 2.0);
      case MAX_ABS:
        return Math.max(0.0, Math.tanh(logAbs / 2.0));
      case LOCAL_MAX_ABS: {
        double q = quantile(abs);
        return clamp01((q - LOCAL_MAX_QUANTILE) / (1.0 - LOCAL_MAX_QUANTILE));
      }
      case QUANTILE_ABS:
        return QUANTILE_RANGE * (2.0 * quantile(abs) - 1.0);
      case CYCLIC_LOG_ABS:
        return cycleDark(logAbs / LOG_ABS_PERIOD);
      case CYCLIC_ARG:
        return cycleDark(ARG_CYCLES * argOf(fre, fim) / (2.0 * Math.PI));
      case CYCLIC_LOG_ABS_ARG: {
        double rings = 1.0 + cycleDark(logAbs / LOG_ABS_PERIOD);
        double spokes = 1.0 + cycleDark(ARG_CYCLES * argOf(fre, fim) / (2.0 * Math.PI));
        return rings * spokes - 1.0;
      }
      case CYCLIC_RE_IM_LOG_ABS: {
        double reGrid = 1.0 + cycleDark(fre / RE_IM_PERIOD);
        double imGrid = 1.0 + cycleDark(fim / RE_IM_PERIOD);
        double dark = reGrid * imGrid - 1.0;
        double light = cycleLight(logAbs / LOG_ABS_PERIOD);
        // the light rings sit on top of the dark grid, so both stay visible
        return dark + (1.0 - Math.abs(dark)) * light;
      }
      case SHIFTED_CYCLIC_LOG_ABS:
        return abs <= SHIFT_THRESHOLD ? 0.0
            : cycleDark((logAbs - Math.log(SHIFT_THRESHOLD)) / LOG_ABS_PERIOD);
      case AUTOMATIC:
      default:
        return Math.tanh(logAbs / AUTOMATIC_SOFTNESS);
    }
  }

  /** The hue of the phase: red on the positive real axis, running counterclockwise. */
  private static float[] hueColor(double fre, double fim) {
    double hue = argOf(fre, fim) / (2.0 * Math.PI);
    int packed = Color.HSBtoRGB((float) hue, (float) BASE_SATURATION, 1.0f);
    return new float[] {((packed >> 16) & 0xFF) / 255.0f, ((packed >> 8) & 0xFF) / 255.0f,
        (packed & 0xFF) / 255.0f};
  }

  /** Towards black below zero, towards white above it; only the extremes reach either. */
  private static IExpr blend(float[] rgb, double s) {
    double factor = s < 0.0 ? 1.0 + s : 1.0;
    double toWhite = s > 0.0 ? s : 0.0;
    double r = rgb[0] * factor;
    double g = rgb[1] * factor;
    double b = rgb[2] * factor;
    r += (1.0 - r) * toWhite;
    g += (1.0 - g) * toWhite;
    b += (1.0 - b) * toWhite;
    return F.RGBColor(clamp01(r), clamp01(g), clamp01(b));
  }

  /** {@code Arg} mapped to {@code 0..2*Pi}, so the colour wheel is continuous. */
  private static double argOf(double re, double im) {
    double arg = Math.atan2(im, re);
    return arg < 0.0 ? arg + 2.0 * Math.PI : arg;
  }

  private static double saw(double t) {
    return t - Math.floor(t);
  }

  /** Darkest at the start of a band, climbing back to the unshaded colour at its end. */
  private static double cycleDark(double t) {
    return CYCLE_DARK * (1.0 - saw(t));
  }

  /** Unshaded at the start of a band, lightest at its end. */
  private static double cycleLight(double t) {
    return CYCLE_LIGHT * saw(t);
  }

  /** Where this magnitude falls among the magnitudes that were sampled, from 0 to 1. */
  private double quantile(double abs) {
    if (absCount <= 1) {
      return 0.5;
    }
    int index = Arrays.binarySearch(absSamples, 0, absCount, abs);
    if (index < 0) {
      index = -index - 1;
    }
    return clamp01(index / (double) (absCount - 1));
  }

  private static double clamp01(double value) {
    return value < 0.0 ? 0.0 : value > 1.0 ? 1.0 : value;
  }

  /** Scale a value into 0..1 over a range the sampling found, or leave it alone. */
  private double scale(double value, double lo, double hi) {
    if (!scaled) {
      return value;
    }
    if (!(hi > lo)) {
      return 0.5;
    }
    return clamp01((value - lo) / (hi - lo));
  }

  /**
   * A colour function the caller supplied.
   *
   * <p>
   * The Wolfram Language hands such a function eight arguments - the real part, imaginary part,
   * magnitude and phase of the sample point, then the same four of the value - so that a colour can
   * be built out of whichever of them the picture is about. A gradient takes one argument instead,
   * the phase, which is the older and much more common spelling. Which of the two a function wants
   * is settled by trying and remembered, exactly as the surface plots do it.
   */
  private final class FunctionColor implements BaseColor {
    private final IExpr function;
    private final boolean scaling;
    private final EvalEngine engine;
    /** 0 until the first successful call says how many arguments this function takes. */
    private int arity = 0;

    FunctionColor(IExpr function, boolean scaling, EvalEngine engine) {
      this.function = function;
      this.scaling = scaling;
      this.engine = engine;
    }

    @Override
    public float[] rgb(double re, double im, double fre, double fim) {
      if (arity != 1) {
        float[] full = tryApply(F.ast(new IExpr[] {F.num(scale(re, plotBox[0], plotBox[1])),
            F.num(scale(im, plotBox[2], plotBox[3])),
            F.num(scale(Math.hypot(re, im), 0.0, maxAbsZ)), F.num(scaledArg(im, re)),
            F.num(scale(fre, minRe, maxRe)), F.num(scale(fim, minIm, maxIm)),
            F.num(scale(Math.hypot(fre, fim), minAbs, maxAbs)), F.num(scaledArg(fim, fre))},
            function));
        if (full != null) {
          arity = 8;
          return full;
        }
        if (arity == 8) {
          return null;
        }
      }
      float[] phase = tryApply(F.unaryAST1(function, F.num(scaledArg(fim, fre))));
      if (phase != null) {
        arity = 1;
      }
      return phase;
    }

    /**
     * The phase as a colour function sees it.
     *
     * <p>
     * Scaling maps {@code Arg} from its own range of {@code -Pi..Pi} onto {@code 0..1}, so the
     * scaled phase starts half a turn away from the wheel the default hue uses. That is what makes
     * {@code Hue(#8 + 0.5)} the way to write the default colouring out by hand, and it is the
     * convention a gradient has always been fed here.
     */
    private double scaledArg(double y, double x) {
      double arg = Math.atan2(y, x);
      return scaling ? (arg + Math.PI) / (2.0 * Math.PI) : arg;
    }

    /** @return the colour the call produced, or {@code null} when it produced none */
    private float[] tryApply(IExpr call) {
      try {
        IExpr result = engine.evaluate(call);
        if (!GraphicsOptions.isColorExpr(result)) {
          return null;
        }
        Color color = ColorUtil.parse(result);
        if (color == null) {
          return null;
        }
        return new float[] {color.getRed() / 255.0f, color.getGreen() / 255.0f,
            color.getBlue() / 255.0f};
      } catch (RuntimeException rex) {
        return null;
      }
    }
  }
}
