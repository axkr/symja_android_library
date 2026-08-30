package org.matheclipse.chem.convert;

import java.util.HashMap;
import java.util.Map;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * CPK element colours and atom radii for the molecule plots.
 *
 * <p>
 * This is the one lookup table the module has to own. Radii come from <code>ElementData</code> in
 * <code>matheclipse-core</code>, which already publishes <code>"CovalentRadius"</code> and
 * <code>"VanDerWaalsRadius"</code>, so only the colours are tabulated here — pulling in a CDK
 * renderer module just for a colour map is not worth the dependency.
 */
public class ChemColors {

  private static final Map<String, int[]> CPK = new HashMap<String, int[]>();

  /** Covalent radii in angstrom, used for ball-and-stick sizing. */
  private static final Map<String, Double> COVALENT_RADIUS = new HashMap<String, Double>();

  /** Van der Waals radii in angstrom, used for space-filling sizing. */
  private static final Map<String, Double> VDW_RADIUS = new HashMap<String, Double>();

  /**
   * The colours a 3D ball-and-stick model uses, where they are known to differ from CPK.
   *
   * <p>
   * These are the values <code>MoleculePlot3D</code> emits - a desaturated CPK, with a grey
   * hydrogen rather than a white one, which reads far better against a light background. Only the
   * four elements whose reference output is on hand are tabulated; everything else keeps its CPK
   * colour.
   */
  private static final Map<String, double[]> MODEL_COLOR = new HashMap<String, double[]>();

  /**
   * Ball radii to match, for the same four elements. They sit within a hundredth of a fifth of the
   * van der Waals radius, which is what every other element falls back to.
   */
  private static final Map<String, Double> MODEL_RADIUS = new HashMap<String, Double>();

  /**
   * The colours a 2D structure diagram uses. The same desaturated CPK as the 3D model except for
   * hydrogen, which a diagram draws as a mid grey rather than the near-white that only works
   * against the shading of a sphere.
   */
  private static final Map<String, double[]> DIAGRAM_COLOR = new HashMap<String, double[]>();

  /**
   * The colour cycle a highlight is picked from. These are the values the reference implementation
   * uses; past the third, the shared chart cycle takes over.
   */
  private static final double[][] HIGHLIGHT_COLOR = new double[][] { //
      {0.985248, 0.676238, 0.0398315}, //
      {0.21099, 0.531208, 0.953188}, //
      {0.519913, 0.338384, 0.950217}};

  static {
    CPK.put("H", new int[] {255, 255, 255});
    CPK.put("C", new int[] {80, 80, 80});
    CPK.put("N", new int[] {48, 80, 248});
    CPK.put("O", new int[] {255, 13, 13});
    CPK.put("F", new int[] {144, 224, 80});
    CPK.put("Cl", new int[] {31, 240, 31});
    CPK.put("Br", new int[] {166, 41, 41});
    CPK.put("I", new int[] {148, 0, 148});
    CPK.put("He", new int[] {217, 255, 255});
    CPK.put("Ne", new int[] {179, 227, 245});
    CPK.put("Ar", new int[] {128, 209, 227});
    CPK.put("Xe", new int[] {66, 158, 176});
    CPK.put("Kr", new int[] {92, 184, 209});
    CPK.put("P", new int[] {255, 128, 0});
    CPK.put("S", new int[] {255, 255, 48});
    CPK.put("B", new int[] {255, 181, 181});
    CPK.put("Li", new int[] {204, 128, 255});
    CPK.put("Na", new int[] {171, 92, 242});
    CPK.put("K", new int[] {143, 64, 212});
    CPK.put("Mg", new int[] {138, 255, 0});
    CPK.put("Ca", new int[] {61, 255, 0});
    CPK.put("Ti", new int[] {191, 194, 199});
    CPK.put("Fe", new int[] {224, 102, 51});
    CPK.put("Zn", new int[] {125, 128, 176});
    CPK.put("Si", new int[] {240, 200, 160});

    COVALENT_RADIUS.put("H", Double.valueOf(0.31));
    COVALENT_RADIUS.put("C", Double.valueOf(0.76));
    COVALENT_RADIUS.put("N", Double.valueOf(0.71));
    COVALENT_RADIUS.put("O", Double.valueOf(0.66));
    COVALENT_RADIUS.put("F", Double.valueOf(0.57));
    COVALENT_RADIUS.put("P", Double.valueOf(1.07));
    COVALENT_RADIUS.put("S", Double.valueOf(1.05));
    COVALENT_RADIUS.put("Cl", Double.valueOf(1.02));
    COVALENT_RADIUS.put("Br", Double.valueOf(1.20));
    COVALENT_RADIUS.put("I", Double.valueOf(1.39));

    VDW_RADIUS.put("H", Double.valueOf(1.20));
    VDW_RADIUS.put("C", Double.valueOf(1.70));
    VDW_RADIUS.put("N", Double.valueOf(1.55));
    VDW_RADIUS.put("O", Double.valueOf(1.52));
    VDW_RADIUS.put("F", Double.valueOf(1.47));
    VDW_RADIUS.put("P", Double.valueOf(1.80));
    VDW_RADIUS.put("S", Double.valueOf(1.80));
    VDW_RADIUS.put("Cl", Double.valueOf(1.75));
    VDW_RADIUS.put("Br", Double.valueOf(1.85));
    VDW_RADIUS.put("I", Double.valueOf(1.98));

    MODEL_COLOR.put("C", new double[] {0.4, 0.4, 0.4});
    MODEL_COLOR.put("H", new double[] {0.65, 0.7, 0.7});
    MODEL_COLOR.put("N", new double[] {0.291989, 0.437977, 0.888609});
    MODEL_COLOR.put("O", new double[] {0.800498, 0.201504, 0.192061});

    MODEL_RADIUS.put("C", Double.valueOf(0.34));
    MODEL_RADIUS.put("H", Double.valueOf(0.24));
    MODEL_RADIUS.put("N", Double.valueOf(0.32));
    MODEL_RADIUS.put("O", Double.valueOf(0.31));

    DIAGRAM_COLOR.put("C", new double[] {0.4, 0.4, 0.4});
    DIAGRAM_COLOR.put("H", new double[] {0.433333, 0.466667, 0.466667});
    DIAGRAM_COLOR.put("N", new double[] {0.291989, 0.437977, 0.888609});
    DIAGRAM_COLOR.put("O", new double[] {0.800498, 0.201504, 0.192061});
  }

  /** The CPK colour of an element as an <code>RGBColor</code>; pink for anything untabulated. */
  public static IAST colorOf(String element) {
    int[] rgb = CPK.get(element);
    if (rgb == null) {
      rgb = new int[] {255, 105, 180};
    }
    return F.ternaryAST3(S.RGBColor, //
        F.num(rgb[0] / 255.0), F.num(rgb[1] / 255.0), F.num(rgb[2] / 255.0));
  }

  /**
   * The colour of an element in a 3D model, which is not quite its CPK colour.
   *
   * @see #MODEL_COLOR
   */
  public static IAST ballColorOf(String element) {
    double[] rgb = MODEL_COLOR.get(element);
    if (rgb == null) {
      return colorOf(element);
    }
    return F.ternaryAST3(S.RGBColor, F.num(rgb[0]), F.num(rgb[1]), F.num(rgb[2]));
  }

  /** The colour of an element in a 2D structure diagram. @see #DIAGRAM_COLOR */
  public static IAST diagramColorOf(String element) {
    double[] rgb = DIAGRAM_COLOR.get(element);
    if (rgb == null) {
      return colorOf(element);
    }
    return F.ternaryAST3(S.RGBColor, F.num(rgb[0]), F.num(rgb[1]), F.num(rgb[2]));
  }

  /** The colour of the <code>index</code>th highlight of a plot. @see #HIGHLIGHT_COLOR */
  public static IAST highlightColorOf(int index) {
    if (index < 0) {
      index = 0;
    }
    if (index < HIGHLIGHT_COLOR.length) {
      double[] rgb = HIGHLIGHT_COLOR[index];
      return F.ternaryAST3(S.RGBColor, F.num(rgb[0]), F.num(rgb[1]), F.num(rgb[2]));
    }
    return org.matheclipse.core.graphics.GraphicsOptions
        .chartStyleColorExpr(index - HIGHLIGHT_COLOR.length);
  }

  /**
   * The radius a ball-and-stick model draws an atom at: a fifth of its van der Waals radius.
   */
  public static double ballRadius(String element) {
    Double radius = MODEL_RADIUS.get(element);
    return radius == null ? vanDerWaalsRadius(element) / 5.0 : radius.doubleValue();
  }

  /** Covalent radius in angstrom; 0.7 for anything untabulated. */
  public static double covalentRadius(String element) {
    Double radius = COVALENT_RADIUS.get(element);
    return radius == null ? 0.7 : radius.doubleValue();
  }

  /** Van der Waals radius in angstrom; 1.7 for anything untabulated. */
  public static double vanDerWaalsRadius(String element) {
    Double radius = VDW_RADIUS.get(element);
    return radius == null ? 1.7 : radius.doubleValue();
  }

  /**
   * Unused today; kept so the plot code can be pointed at core's data instead of the tables here.
   */
  static IExpr elementDataRadius(String element, String property) {
    return F.binaryAST2(S.ElementData, F.stringx(element), F.stringx(property));
  }

  private ChemColors() {}
}
