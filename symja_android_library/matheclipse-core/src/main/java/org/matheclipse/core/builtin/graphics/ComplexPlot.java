package org.matheclipse.core.builtin.graphics;

import java.awt.Color;
import org.hipparchus.complex.Complex;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.GraphicsOptions;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Functions for generating Complex Plots with advanced domain coloring.
 * <p>
 * Example:
 * <code>ComplexPlot[(z^2 + 1) / (z^2 - 1), {z, -2 - 2 I, 2 + 2 I}, ColorFunction -> "CyclicLogAbsArg"]</code>
 */
public class ComplexPlot extends ListPlot {

  public ComplexPlot() {}

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    if (argSize < 2) {
      return F.NIL;
    }

    IExpr f = ast.arg1();

    IExpr rangeSpec = ast.arg2();
    ISymbol zVar = null;
    IExpr zMin = null;
    IExpr zMax = null;

    // Parse Range {z, n} or {z, min, max}
    if (rangeSpec.isList()) {
      IAST rangeList = (IAST) rangeSpec;
      if (rangeList.argSize() >= 2 && rangeList.arg1().isSymbol()) {
        zVar = (ISymbol) rangeList.arg1();
        if (rangeList.argSize() == 2) {
          // {z, n} -> -n-nI to n+nI
          IExpr n = rangeList.arg2();
          zMin = F.Times(F.CN1, F.Plus(n, F.Times(n, F.CI))); // -n - n I
          zMax = F.Plus(n, F.Times(n, F.CI)); // n + n I
        } else if (rangeList.argSize() >= 3) {
          // {z, min, max}
          zMin = rangeList.arg2();
          zMax = rangeList.arg3();
        }
      }
    }

    if (zVar == null || zMin == null || zMax == null) {
      // Range specification `1` is not of the form {x, xmin, xmax}.
      return Errors.printMessage(S.ComplexPlot, "pllim", F.list(rangeSpec), engine);
    }

    GraphicsOptions graphicsOptions = setGraphicsOptions(options, engine);

    // Handle PlotLegends -> Automatic for ComplexPlot (Phase Legend)
    if (graphicsOptions.plotLegends().isAutomatic()) {
      graphicsOptions.setPlotLegends(F.BarLegend(S.Hue, F.List(F.num(-Math.PI), F.num(Math.PI))));
    }

    // Default defaults
    int plotPoints = 100; // Higher default for raster-like quality
    String colorFunction = "Automatic";

    IExpr colorFunctionSpec =
        GraphicsOptions.optionValue(originalAST, S.ColorFunction, S.Automatic);
    boolean colorFunctionScaling =
        !GraphicsOptions.optionValue(originalAST, S.ColorFunctionScaling, S.True).isFalse();
    // the options array holds resolved values, not the rules the caller wrote,
    // so the option rules are read back off the original call
    for (IExpr opt : originalAST) {
      if (opt.isRuleAST()) {
        IExpr key = ((IAST) opt).arg1();
        IExpr val = ((IAST) opt).arg2();
        if (key.equals(S.PlotPoints)) {
          plotPoints = val.toIntDefault(100);
        } else if (key.equals(S.ColorFunction)) {
          colorFunction = val.toString().replace("\"", "");
        }
      }
    }
    // A name this plot knows selects one of its own shading schemes. Anything else -- a gradient
    // name, or a function -- colours the plot itself, and takes over from the schemes.
    boolean namedScheme = colorFunctionSpec == S.Automatic || isNamedScheme(colorFunction);

    double x0 = 0, y0 = 0, x1 = 0, y1 = 0;
    try {
      Complex cMin = zMin.evalfc();
      Complex cMax = zMax.evalfc();
      x0 = cMin.getRealPart();
      y0 = cMin.getImaginaryPart();
      x1 = cMax.getRealPart();
      y1 = cMax.getImaginaryPart();
    } catch (Exception e) {
      return F.NIL;
    }

    double dx = (x1 - x0) / plotPoints;
    double dy = (y1 - y0) / plotPoints;

    IASTAppendable primitives = F.ListAlloc();
    // the grid becomes a single raster rather than one rectangle per cell
    IExpr[][] cells = new IExpr[plotPoints][plotPoints];

    // Generate the colour grid
    for (int i = 0; i < plotPoints; i++) {
      double rMid = x0 + (i + 0.5) * dx;
      for (int j = 0; j < plotPoints; j++) {
        double iMid = y0 + (j + 0.5) * dy;
        IExpr zVal = F.complexNum(rMid, iMid);

        try {
          // Evaluate f[z]
          IExpr valExpr = f.replaceAll(F.List(F.Rule(zVar, zVal)));
          IExpr res = engine.evaluate(valExpr);

          IExpr rgb = F.NIL;
          if (namedScheme) {
            double[] hsb = complexToHSB(res, colorFunction);
            if (hsb != null) {
              Color c = Color.getHSBColor((float) hsb[0], (float) hsb[1], (float) hsb[2]);
              rgb = F.RGBColor(c.getRed() / 255.0, c.getGreen() / 255.0, c.getBlue() / 255.0);
            }
          } else {
            rgb = paintedColor(colorFunctionSpec, res, colorFunctionScaling, engine);
          }
          if (rgb.isPresent()) {
            // j counts upwards from the bottom, while the raster rows are given top first
            cells[plotPoints - 1 - j][i] = rgb;
          }
        } catch (RuntimeException rex) {
          return Errors.printMessage(S.ComplexPlot, rex);
        }
      }
    }
    primitives.append(GraphicsOptions.rasterTopFirst(cells, x0, y0, x1, y1));

    graphicsOptions.setBoundingBox(new double[] {x0, x1, y0, y1});

    // Ensure AspectRatio -> Automatic (1:1) is default for ComplexPlot unless overridden
    if (graphicsOptions.aspectRatio().equals(S.Automatic)) {
      graphicsOptions.setAspectRatio(F.C1);
    }

    return createGraphicsFunction(primitives, graphicsOptions, ast);
  }

  /** Shading schemes this plot draws itself, as opposed to colours a caller supplies. */
  private static boolean isNamedScheme(String name) {
    switch (name) {
      case "Automatic":
      case "None":
      case "GlobalAbs":
      case "MaxAbs":
      case "CyclicArg":
      case "CyclicLogAbs":
      case "CyclicLogAbsArg":
      case "CyclicReImLogAbs":
        return true;
      default:
        return false;
    }
  }

  /**
   * The colour a caller's own {@code ColorFunction} gives a value.
   *
   * <p>
   * A function is offered the value itself first. A gradient name takes a single number instead, so
   * the phase is used: scaled to 0..1 over a full turn, or left as the angle in radians when
   * {@code ColorFunctionScaling -> False}. A function that wanted a number rather than the value
   * gets the same phase.
   *
   * @return the colour, or {@link F#NIL} when nothing usable came back
   */
  private static IExpr paintedColor(IExpr spec, IExpr value, boolean scaling, EvalEngine engine) {
    if (!spec.isString()) {
      IExpr direct = evalColor(F.unaryAST1(spec, value), engine);
      if (direct.isPresent()) {
        return direct;
      }
    }
    double phase = phaseOf(value);
    if (Double.isNaN(phase)) {
      return F.NIL;
    }
    double argument = scaling ? (phase + Math.PI) / (2.0 * Math.PI) : phase;
    IExpr function = spec.isString() ? F.unaryAST1(S.ColorData, spec) : spec;
    return evalColor(F.unaryAST1(function, F.num(argument)), engine);
  }

  /** The phase of a complex value, or {@code NaN} when it has none. */
  private static double phaseOf(IExpr value) {
    if (!value.isNumber()) {
      return Double.NaN;
    }
    try {
      Complex c = value.evalfc();
      double re = c.getRealPart();
      double im = c.getImaginaryPart();
      if (!Double.isFinite(re) || !Double.isFinite(im)) {
        return Double.NaN;
      }
      return Math.atan2(im, re);
    } catch (RuntimeException rex) {
      return Double.NaN;
    }
  }

  private static IExpr evalColor(IExpr call, EvalEngine engine) {
    try {
      IExpr color = engine.evaluate(call);
      return GraphicsOptions.isColorExpr(color) ? color : F.NIL;
    } catch (RuntimeException rex) {
      return F.NIL;
    }
  }

  /**
   * Maps a complex value to HSB colors based on the selected ColorFunction strategy.
   * <p>
   * Instead of smooth Sin^2 waves, we use Pow(Sin, 0.1) to create broad bright areas and narrow
   * dark drops (grid lines).
   */
  private double[] complexToHSB(IExpr val, String mode) {
    if (val.isNumber()) {
      Complex c = null;
      try {
        c = val.evalfc();
      } catch (Exception e) {
        return null;
      }

      double re = c.getRealPart();
      double im = c.getImaginaryPart();

      // Singularities/Infinity -> White
      if (Double.isNaN(re) || Double.isNaN(im) || Double.isInfinite(re) || Double.isInfinite(im)) {
        return new double[] {0.0, 0.0, 1.0};
      }

      double abs = Math.hypot(re, im);
      double arg = Math.atan2(im, re); // -pi to pi

      // 1. Base Hue: Argument (Phase) -> 0..1
      double hue = (arg < 0 ? arg + 2 * Math.PI : arg) / (2.0 * Math.PI);

      double saturation = 1.0;
      double brightness = 1.0;

      // Log Magnitude
      double logAbs = Math.log(abs + 1e-20);

      // --- Shading Logic ---
      // "Sharp" logic: use power < 1.0 (e.g. 0.15) on the sine wave.
      // |Sin(x)| goes 0..1. |Sin(x)|^0.1 rises very fast to ~1.0, staying bright,
      // and dips sharply to 0 at x = n*Pi. This creates the "Grid line" effect.
      double sharpness = 0.15;

      if (mode.equalsIgnoreCase("None")) {
        // Pure Phase Plot
        return new double[] {hue, 1.0, 1.0};

      } else if (mode.equalsIgnoreCase("GlobalAbs") || mode.equalsIgnoreCase("MaxAbs")) {
        // Smooth gradient based on magnitude (no cycles)
        brightness = abs / (1.0 + abs);

      } else if (mode.equalsIgnoreCase("CyclicArg")) {
        // Radial Spokes only
        // Cycle 6 times (every 60 deg)
        double spoke = Math.pow(Math.abs(Math.sin(arg * 6.0)), sharpness);
        brightness = spoke;

      } else if (mode.equalsIgnoreCase("CyclicLogAbsArg")) {
        // ** TARGET LOOK **: Polar Grid (Rings + Spokes)
        // Rings: repeat every 2*Pi in log scale (natural log steps)
        double ring = Math.pow(Math.abs(Math.sin(logAbs * Math.PI)), sharpness);

        // Spokes: 6 or 12 spokes? Mma usually has 2*k segments. Let's use 6.
        double spoke = Math.pow(Math.abs(Math.sin(arg * 6.0)), sharpness);

        // Combine: Darken if either ring OR spoke is hit (Grid intersection is darkest)
        // Multiply brightnesses.
        brightness = ring * spoke;

        // Optional: slight saturation drop at lines for "ink" effect
        saturation = 0.8 + 0.2 * brightness;

      } else if (mode.equalsIgnoreCase("CyclicReImLogAbs")) {
        // Checkerboard + Rings
        double reGrid = Math.pow(Math.abs(Math.sin(re * 2.0)), sharpness);
        double imGrid = Math.pow(Math.abs(Math.sin(im * 2.0)), sharpness);
        double logRing = Math.pow(Math.abs(Math.sin(logAbs * 4.0)), sharpness);

        brightness = reGrid * imGrid * logRing;

      } else {
        // Default: "Automatic" / "CyclicLogAbs"
        // Just rings, sharp contours
        // Freq 2*Pi creates bands at e^0, e^1, e^2...
        double ring = Math.pow(Math.abs(Math.sin(logAbs * Math.PI)), sharpness);
        brightness = ring;
      }

      return new double[] {hue, saturation, brightness};
    }
    return null;
  }

  @Override
  public int status() {
    return ImplementationStatus.EXPERIMENTAL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_2_INFINITY;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    IExpr[] defaults = GraphicsOptions.listPlotDefaultOptionValues(false, false);

    defaults[GraphicsOptions.X_FRAME] = S.True;
    defaults[GraphicsOptions.X_AXES] = S.False;
    defaults[GraphicsOptions.X_ASPECTRATIO] = S.Automatic;

    GraphicsOptions.OptionSet optionSet = GraphicsOptions.densityExtras(
        new GraphicsOptions.OptionSet().add(GraphicsOptions.listPlotDefaultOptionKeys(), defaults));
    setOptions(newSymbol, optionSet.keys(), optionSet.values());
  }
}
