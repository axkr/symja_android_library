package org.matheclipse.core.graphics;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Compares the {@code Graphics} expression each plot produces against the values Mathematica
 * reports for the same input.
 * 
 */
public class PlotParityTest {

  private static ExprEvaluator evaluator;

  @BeforeAll
  public static void setUpEngine() {
    Locale.setDefault(Locale.US);
    Config.SERVER_MODE = false;
    // Other test classes lower these shared limits; a density style plot legitimately builds a
    // large expression (one rectangle per cell), so the ceiling has to be restored here rather
    // than inherited from whichever test ran last.
    Config.MAX_AST_SIZE = Integer.MAX_VALUE;
    Config.MAX_MATRIX_DIMENSION_SIZE = Integer.MAX_VALUE;
    Config.FILESYSTEM_ENABLED = false;
    try {
      F.await();
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException(ie);
    }
    EvalEngine engine = new EvalEngine(true);
    EvalEngine.set(engine);
    engine.init();
    engine.setIterationLimit(10000);
    evaluator = new ExprEvaluator(engine, false, (short) 100);
    // Other tests in the suite leave values assigned to common one letter symbols, and a plot
    // whose variable is already bound does not evaluate to a graphic. Clearing them keeps these
    // tests independent of the order the suite happens to run in.
    evaluator.eval("ClearAll(a,b,c,i,j,k,n,r,s,t,u,v,w,x,y,z)");
  }

  /** One reference case: the input, and what Mathematica reports for it. */
  private static final class Reference {
    final String input;
    final String firstColor;
    final String thickness;
    final String clipping;

    Reference(String input, String firstColor, String thickness, String clipping) {
      this.input = input;
      this.firstColor = firstColor;
      this.thickness = thickness;
      this.clipping = clipping;
    }
  }

  /**
   * The reference expectations, one per canonical example. {@code null} means the reference does
   * not report that value, so it is not compared.
   */
  private static final Map<String, Reference> REFERENCES = new LinkedHashMap<>();

  static {
    // function and list plots: the shared default curve style
    String blue = "0.24,0.6,0.8";
    String[][] curvePlots = { //
        {"Plot", "Plot[Sin[x], {x, 0, 2*Pi}]", "True"}, //
        {"LogPlot", "LogPlot[Exp[x], {x, 0, 10}]", "True"}, //
        {"LogLogPlot", "LogLogPlot[x^2, {x, 1, 100}]", "True"}, //
        {"LogLinearPlot", "LogLinearPlot[x, {x, 1, 100}]", "True"}, //
        {"ParametricPlot", "ParametricPlot[{Sin[u], Cos[u]}, {u, 0, 2*Pi}]", "True"}, //
        {"PolarPlot", "PolarPlot[1 + Cos[t], {t, 0, 2*Pi}]", "True"}, //
        {"DiscretePlot", "DiscretePlot[n^2, {n, 1, 20}]", "True"}, //
        {"ListPlot", "ListPlot[Table[Prime[n], {n, 25}]]", "True"}, //
        {"ListLinePlot", "ListLinePlot[{1, 1, 2, 3, 5, 8, 13, 21}]", "True"}, //
        {"ListLogPlot", "ListLogPlot[Table[2^n, {n, 20}]]", "True"}, //
        {"ListLogLogPlot", "ListLogLogPlot[Table[n^3, {n, 20}]]", "True"}, //
        {"ListLogLinearPlot", "ListLogLinearPlot[Table[{n, Log[n]}, {n, 1, 100}]]", "True"}, //
        {"ListPolarPlot", "ListPolarPlot[Table[{t, t}, {t, 0, 4*Pi, 0.1}]]", "False"}, //
        {"ListStepPlot", "ListStepPlot[{1, 3, 2, 5, 4}]", "True"}, //
    };
    for (String[] c : curvePlots) {
      REFERENCES.put(c[0], new Reference(c[1], blue, "2", c[2]));
    }

    // charts use their own colour cycle, which starts at amber
    String amber = "1.0,0.835,0.25";
    REFERENCES.put("BarChart", new Reference("BarChart[{1, 2, 3, 4}]", amber, null, "False"));
    REFERENCES.put("BoxWhiskerChart",
        new Reference("BoxWhiskerChart[Table[Mod[n^2, 17], {n, 100}]]", amber, null, "False"));
    REFERENCES.put("PieChart", new Reference("PieChart[{1, 2, 3, 4}]", null, null, "False"));
    REFERENCES.put("Histogram",
        new Reference("Histogram[Table[Mod[n^2, 17], {n, 200}]]", null, null, "False"));

    // the remaining families: only the frame shape is compared
    REFERENCES.put("ContourPlot",
        new Reference("ContourPlot[Sin[x]*Sin[y], {x, -2, 2}, {y, -2, 2}]", null, null, "True"));
    REFERENCES.put("DensityPlot",
        new Reference("DensityPlot[Sin[x]*Sin[y], {x, -2, 2}, {y, -2, 2}]", null, null, "True"));
    REFERENCES.put("ComplexPlot", new Reference(
        "ComplexPlot[(z^2 + 1)/(z^2 - 1), {z, -2 - 2*I, 2 + 2*I}]", null, null, "True"));
    REFERENCES.put("ListContourPlot",
        new Reference("ListContourPlot[Table[Sin[x]*Sin[y], {x, -2, 2, 0.2}, {y, -2, 2, 0.2}]]",
            null, null, "True"));
    REFERENCES.put("ListDensityPlot",
        new Reference("ListDensityPlot[Table[Sin[x]*Sin[y], {x, -2, 2, 0.4}, {y, -2, 2, 0.4}]]",
            null, null, "True"));
    REFERENCES.put("MatrixPlot",
        new Reference("MatrixPlot[Table[1/(i + j - 1), {i, 10}, {j, 10}]]", null, null, "False"));
    REFERENCES.put("ArrayPlot",
        new Reference("ArrayPlot[Table[Mod[i + j, 2], {i, 10}, {j, 10}]]", null, null, "False"));
    REFERENCES.put("NumberLinePlot",
        new Reference("NumberLinePlot[Prime[Range[10]]]", null, null, "False"));
  }

  private static String find(String haystack, String regex) {
    Matcher m = Pattern.compile(regex).matcher(haystack);
    return m.find() ? m.group(1) : null;
  }

  /** The default curve colour must be the one the reference rendering uses. */
  @Test
  public void testDefaultStyleColorMatchesReference() {
    List<String> problems = new ArrayList<>();
    for (Map.Entry<String, Reference> entry : REFERENCES.entrySet()) {
      Reference reference = entry.getValue();
      if (reference.firstColor == null) {
        continue;
      }
      IExpr result = evaluator.eval(reference.input);
      String actual = find(result.toString(), "RGBColor\\(([^\\)]{0,40})\\)");
      if (actual == null || !actual.startsWith(reference.firstColor)) {
        problems.add(entry.getKey() + ": expected " + reference.firstColor + " got " + actual);
      }
    }
    assertTrue(problems.isEmpty(),
        () -> "default colours differ from the reference:\n  " + String.join("\n  ", problems));
  }

  /**
   * Curves must carry a stroke width. Without one they draw a single pixel wide, which is much
   * thinner than the reference.
   */
  @Test
  public void testCurvesCarryAbsoluteThickness() {
    List<String> problems = new ArrayList<>();
    for (Map.Entry<String, Reference> entry : REFERENCES.entrySet()) {
      Reference reference = entry.getValue();
      if (reference.thickness == null) {
        continue;
      }
      IExpr result = evaluator.eval(reference.input);
      String actual = find(result.toString(), "AbsoluteThickness\\(([^\\)]*)\\)");
      if (actual == null || Double.parseDouble(actual) != Double.parseDouble(reference.thickness)) {
        problems.add(entry.getKey() + ": expected AbsoluteThickness " + reference.thickness
            + " got " + actual);
      }
    }
    assertTrue(problems.isEmpty(),
        () -> "curve thickness differs from the reference:\n  " + String.join("\n  ", problems));
  }

  /** {@code PlotRangeClipping} decides whether a curve may spill past the axes. */
  @Test
  public void testPlotRangeClippingMatchesReference() {
    List<String> problems = new ArrayList<>();
    for (Map.Entry<String, Reference> entry : REFERENCES.entrySet()) {
      Reference reference = entry.getValue();
      if (reference.clipping == null) {
        continue;
      }
      IExpr result = evaluator.eval(reference.input);
      String actual = find(result.toString(), "PlotRangeClipping->([^,\\)]+)");
      if (actual == null || !actual.trim().equals(reference.clipping)) {
        problems.add(entry.getKey() + ": expected " + reference.clipping + " got " + actual);
      }
    }
    assertTrue(problems.isEmpty(),
        () -> "PlotRangeClipping differs from the reference:\n  " + String.join("\n  ", problems));
  }

  /** Every canonical example must still produce a graphic. */
  @Test
  public void testAllReferenceCasesRender() {
    List<String> problems = new ArrayList<>();
    for (Map.Entry<String, Reference> entry : REFERENCES.entrySet()) {
      try {
        IExpr result = evaluator.eval(entry.getValue().input);
        if (!result.isGraphicsObject()) {
          String shown = result.toString();
          problems.add(entry.getKey() + ": did not evaluate to a Graphics object, got "
              + (shown.length() > 160 ? shown.substring(0, 160) : shown));
        }
      } catch (RuntimeException | StackOverflowError ex) {
        problems.add(entry.getKey() + ": " + ex.getClass().getSimpleName());
      }
    }
    assertTrue(problems.isEmpty(), () -> String.join("\n  ", problems));
  }
}
