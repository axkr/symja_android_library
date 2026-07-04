package org.matheclipse.core.reflection.system;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.eval.interfaces.IFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * <code>PeriodicTablePlot</code> implementation.
 */
public class PeriodicTablePlot extends AbstractFunctionOptionEvaluator {

  private static final String[] NAMES = {"", "Hydrogen", "Helium", "Lithium", "Beryllium", "Boron",
      "Carbon", "Nitrogen", "Oxygen", "Fluorine", "Neon", "Sodium", "Magnesium", "Aluminum",
      "Silicon", "Phosphorus", "Sulfur", "Chlorine", "Argon", "Potassium", "Calcium", "Scandium",
      "Titanium", "Vanadium", "Chromium", "Manganese", "Iron", "Cobalt", "Nickel", "Copper", "Zinc",
      "Gallium", "Germanium", "Arsenic", "Selenium", "Bromine", "Krypton", "Rubidium", "Strontium",
      "Yttrium", "Zirconium", "Niobium", "Molybdenum", "Technetium", "Ruthenium", "Rhodium",
      "Palladium", "Silver", "Cadmium", "Indium", "Tin", "Antimony", "Tellurium", "Iodine", "Xenon",
      "Cesium", "Barium", "Lanthanum", "Cerium", "Praseodymium", "Neodymium", "Promethium",
      "Samarium", "Europium", "Gadolinium", "Terbium", "Dysprosium", "Holmium", "Erbium", "Thulium",
      "Ytterbium", "Lutetium", "Hafnium", "Tantalum", "Tungsten", "Rhenium", "Osmium", "Iridium",
      "Platinum", "Gold", "Mercury", "Thallium", "Lead", "Bismuth", "Polonium", "Astatine", "Radon",
      "Francium", "Radium", "Actinium", "Thorium", "Protactinium", "Uranium", "Neptunium",
      "Plutonium", "Americium", "Curium", "Berkelium", "Californium", "Einsteinium", "Fermium",
      "Mendelevium", "Nobelium", "Lawrencium", "Rutherfordium", "Dubnium", "Seaborgium", "Bohrium",
      "Hassium", "Meitnerium", "Darmstadtium", "Roentgenium", "Copernicium", "Nihonium",
      "Flerovium", "Moscovium", "Livermorium", "Tennessine", "Oganesson"};

  private static final String[] SYMBOLS = {"", "H", "He", "Li", "Be", "B", "C", "N", "O", "F", "Ne",
      "Na", "Mg", "Al", "Si", "P", "S", "Cl", "Ar", "K", "Ca", "Sc", "Ti", "V", "Cr", "Mn", "Fe",
      "Co", "Ni", "Cu", "Zn", "Ga", "Ge", "As", "Se", "Br", "Kr", "Rb", "Sr", "Y", "Zr", "Nb", "Mo",
      "Tc", "Ru", "Rh", "Pd", "Ag", "Cd", "In", "Sn", "Sb", "Te", "I", "Xe", "Cs", "Ba", "La", "Ce",
      "Pr", "Nd", "Pm", "Sm", "Eu", "Gd", "Tb", "Dy", "Ho", "Er", "Tm", "Yb", "Lu", "Hf", "Ta", "W",
      "Re", "Os", "Ir", "Pt", "Au", "Hg", "Tl", "Pb", "Bi", "Po", "At", "Rn", "Fr", "Ra", "Ac",
      "Th", "Pa", "U", "Np", "Pu", "Am", "Cm", "Bk", "Cf", "Es", "Fm", "Md", "No", "Lr", "Rf", "Db",
      "Sg", "Bh", "Hs", "Mt", "Ds", "Rg", "Cn", "Nh", "Fl", "Mc", "Lv", "Ts", "Og"};

  private static final int[][] COORDS = new int[119][2];
  static {
    // Build coordinate grid. Y coordinates are inverted for standard Graphics cartesian mapping
    COORDS[1] = new int[] {1, 10};
    COORDS[2] = new int[] {18, 10};
    COORDS[3] = new int[] {1, 9};
    COORDS[4] = new int[] {2, 9};
    for (int i = 5; i <= 10; i++)
      COORDS[i] = new int[] {i + 8, 9};
    COORDS[11] = new int[] {1, 8};
    COORDS[12] = new int[] {2, 8};
    for (int i = 13; i <= 18; i++)
      COORDS[i] = new int[] {i, 8};
    for (int i = 19; i <= 36; i++)
      COORDS[i] = new int[] {i - 18, 7};
    for (int i = 37; i <= 54; i++)
      COORDS[i] = new int[] {i - 36, 6};
    COORDS[55] = new int[] {1, 5};
    COORDS[56] = new int[] {2, 5};
    for (int i = 72; i <= 86; i++)
      COORDS[i] = new int[] {i - 68, 5};
    COORDS[87] = new int[] {1, 4};
    COORDS[88] = new int[] {2, 4};
    for (int i = 104; i <= 118; i++)
      COORDS[i] = new int[] {i - 100, 4};
    // Placing La-Lu & Ac-Lr starting from Column 3 to 17
    for (int i = 57; i <= 71; i++)
      COORDS[i] = new int[] {i - 54, 2};
    for (int i = 89; i <= 103; i++)
      COORDS[i] = new int[] {i - 86, 1};
  }

  private static final IBuiltInSymbol[] OPTION_KEYS = {S.AlignmentPoint, S.AspectRatio, S.Axes,
      S.AxesLabel, S.AxesOrigin, S.AxesStyle, S.Background, S.BaselinePosition, S.BaseStyle,
      S.ColorFunction, S.ContentSelectable, S.CoordinatesToolOptions, S.Epilog, S.FormatType,
      S.Frame, S.FrameLabel, S.FrameStyle, S.FrameTicks, S.FrameTicksStyle, S.GridLines,
      S.GridLinesStyle, S.ImageMargins, S.ImagePadding, S.ImageSize, S.LabelStyle, S.Method,
      S.PlotLabel, S.PlotLegends, S.PlotRange, S.PlotRangeClipping, S.PlotRangePadding,
      S.PlotRegion, S.PreserveImageOptions, S.Prolog, S.RotateLabel, S.Ticks, S.TicksStyle};

  private static final IExpr[] OPTION_VALUES = {S.Center, S.Automatic, S.False, S.None, S.Automatic,
      F.List(), S.None, S.Automatic, F.List(), S.Automatic, S.Automatic, S.Automatic, F.List(),
      S.TraditionalForm, S.False, S.None, F.List(), S.Automatic, F.List(), S.None, F.List(),
      F.num(0.0), S.All, S.Automatic, F.List(), S.Automatic, S.None, S.Automatic, S.All, S.False,
      S.Automatic, S.Automatic, S.Automatic, F.List(), S.True, S.Automatic, F.List()};

  private static double getDouble(IExpr expr) {
    try {
      return expr.evalf();
    } catch (ArgumentTypeException ex) {
    }
    return 0.0;
  }

  private static IExpr getLighterColor(IExpr baseColor, double fraction) {
    if (baseColor.isAST(S.RGBColor)) {
      IAST rgb = (IAST) baseColor;
      if (rgb.argSize() >= 3) {
        try {
          double r = getDouble(rgb.arg1());
          double g = getDouble(rgb.arg2());
          double b = getDouble(rgb.arg3());
          double newR = r * (1.0 - fraction) + fraction;
          double newG = g * (1.0 - fraction) + fraction;
          double newB = b * (1.0 - fraction) + fraction;
          return F.RGBColor(F.num(newR), F.num(newG), F.num(newB));
        } catch (Exception e) {
          return baseColor;
        }
      }
    }
    return baseColor;
  }

  private static IExpr getColorFromIndex(int index) {
    int[][] palette = {//
        {94, 156, 211}, //
        {235, 125, 59}, //
        {165, 165, 165}, //
        {255, 192, 0}, //
        {68, 114, 196}, //
        {112, 173, 71}, //
        {37, 94, 145}, //
        {158, 72, 14}, //
        {99, 99, 99}, //
        {153, 115, 0}};
    int i = (index - 1) % palette.length;
    return rgb(palette[i][0], palette[i][1], palette[i][2]);
  }

  private static IExpr getDefaultColor(int z) {
    // Other Nonmetals
    if (z == 1 || z == 6 || z == 7 || z == 15)
      return rgb(146, 197, 222);
    // Alkali Metals
    if (z == 3 || z == 11 || z == 19 || z == 37 || z == 55 || z == 87)
      return rgb(166, 217, 141);
    // Alkaline Earth
    if (z == 4 || z == 12 || z == 20 || z == 38 || z == 56 || z == 88)
      return rgb(244, 165, 130);
    // Transition metals (extended bounds properly color Lu(71) and Lr(103) pink)
    if ((z >= 21 && z <= 30) || (z >= 39 && z <= 48) || (z >= 72 && z <= 80)
        || (z >= 104 && z <= 112) || z == 71 || z == 103)
      return rgb(216, 152, 211);
    // Post-transition metals (Yellow - Po(84) removed to Chalcogens)
    if (z == 13 || z == 31 || z == 49 || z == 50 || (z >= 81 && z <= 83) || (z >= 113 && z <= 116))
      return rgb(254, 224, 139);
    // Metalloids (Purple - Te(52) removed to Chalcogens)
    if (z == 5 || z == 14 || z == 32 || z == 33 || z == 51)
      return rgb(194, 165, 207);
    // Halogens (Dark Blue)
    if (z == 9 || z == 17 || z == 35 || z == 53 || z == 85 || z == 117)
      return rgb(67, 147, 195);
    // Noble Gases (Orange/Yellow)
    if (z == 2 || z == 10 || z == 18 || z == 36 || z == 54 || z == 86 || z == 118)
      return rgb(253, 174, 97);
    // Chalcogens (Brown) -> O, S, Se, Te, Po
    if (z == 8 || z == 16 || z == 34 || z == 52 || z == 84)
      return rgb(222, 165, 115);
    // Lanthanides
    if (z >= 57 && z <= 70)
      return rgb(184, 225, 134);
    // Actinides
    if (z >= 89 && z <= 102)
      return rgb(234, 130, 118);

    return rgb(220, 220, 220);
  }

  private static int nameToZ(String name) {
    for (int i = 1; i <= 118; i++) {
      if (NAMES[i].equalsIgnoreCase(name) || SYMBOLS[i].equalsIgnoreCase(name)) {
        return i;
      }
    }
    return 0;
  }

  private static IExpr rgb(int r, int g, int b) {
    return F.RGBColor(F.num(r / 255.0), F.num(g / 255.0), F.num(b / 255.0));
  }

  public PeriodicTablePlot() {}

  private IExpr createMarkerCell(int x, int y, String marker, IExpr textColor) {
    IASTAppendable cell = F.ListAlloc();
    cell.append(S.White); // Base canvas
    cell.append(F.Rectangle(F.List(F.num(x), F.num(y)), F.List(F.num(x + 0.9), F.num(y + 0.9))));
    cell.append(F.Text(F.Style(F.stringx(marker), F.Rule(S.FontSize, F.num(12)), textColor),
        F.List(F.num(x + 0.45), F.num(y + 0.45))));
    return F.List(cell);
  }

  @Override
  public IExpr evaluate(IAST ast, final int argSize, final IExpr[] options, final EvalEngine engine,
      IAST originalAST) {
    int mode = 0; // 0 = Default, 1 = Highlight, 2 = Property Mapping
    Set<Integer> highlightedElements = new LinkedHashSet<>();
    String property = null;

    // Parse arguments based on required signatures
    if (argSize >= 1) {
      IExpr arg1 = ast.arg1();
      if (arg1.isAST(S.EntityProperty, 3) || arg1.isAST(S.EntityProperty, 2)) {
        mode = 2;
        property = ((IAST) arg1).arg2().toString().replace("\"", "");
      } else if (arg1.isString() && !arg1.toString().replace("\"", "").equals("Element")) {
        String val = arg1.toString().replace("\"", "");
        if (nameToZ(val) > 0) {
          mode = 1;
          highlightedElements.add(nameToZ(val));
        } else {
          mode = 2;
          property = val;
        }
      } else {
        mode = 1;
        highlightedElements = parseElements(arg1, engine);
        if (highlightedElements.isEmpty()) {
          mode = 0; // Fallback to default if no valid elements found
        }
      }
    }

    IASTAppendable primitives = F.ListAlloc();
    Map<IExpr, IExpr> propertyToColor = new HashMap<>();
    int colorIndex = 1;

    for (int z = 1; z <= 118; z++) {
      int x = COORDS[z][0];
      int y = COORDS[z][1];

      IExpr baseColor = getDefaultColor(z);
      IExpr tooltipValue = F.NIL;

      // Map values for EntityProperty query
      if (mode == 2) {
        IExpr val = engine.evaluate(F.ElementData(F.ZZ(z), F.stringx(property)));
        tooltipValue = val;
        if (!propertyToColor.containsKey(val)) {
          propertyToColor.put(val, getColorFromIndex(colorIndex++));
        }
        baseColor = propertyToColor.get(val);
      }

      IExpr bgColor = baseColor;
      IExpr textColor = S.Black;

      if (mode == 1 && !highlightedElements.contains(z)) {
        bgColor = getLighterColor(baseColor, 0.85);
        textColor = F.GrayLevel(F.num(0.7));
      }

      IASTAppendable cell = F.ListAlloc();
      cell.append(bgColor);
      cell.append(F.Rectangle(F.List(F.num(x), F.num(y)), F.List(F.num(x + 0.9), F.num(y + 0.9))));

      cell.append(F.Text(F.Style(F.stringx(SYMBOLS[z]), F.Rule(S.FontSize, F.num(12)), textColor),
          F.List(F.num(x + 0.45), F.num(y + 0.55))));
      cell.append(F.Text(F.Style(F.ZZ(z), F.Rule(S.FontSize, F.num(8)), textColor),
          F.List(F.num(x + 0.45), F.num(y + 0.2))));

      IExpr cellExpr = F.List(cell);

      // Attach tooltips if a property query is utilized
      if (tooltipValue.isPresent()) {
        cellExpr = F.Tooltip(cellExpr, tooltipValue);
      }
      primitives.append(cellExpr);
    }

    // Append visual block markers in appropriate spaces and dim them along with non-selected
    // elements
    IExpr markerTextColor = (mode == 1) ? F.GrayLevel(F.num(0.7)) : S.Black;
    primitives.append(createMarkerCell(3, 5, "*", markerTextColor));
    primitives.append(createMarkerCell(3, 4, "**", markerTextColor));
    primitives.append(createMarkerCell(2, 2, "*", markerTextColor));
    primitives.append(createMarkerCell(2, 1, "**", markerTextColor));

    // Setup Legends for property mapping
    IExpr legendsOption = S.Automatic;
    IASTAppendable plotStyleOption = F.ListAlloc();
    if (mode == 2 && propertyToColor.size() > 0) {
      IASTAppendable legendLabels = F.ListAlloc();
      for (Map.Entry<IExpr, IExpr> entry : propertyToColor.entrySet()) {
        plotStyleOption.append(entry.getValue());
        legendLabels.append(entry.getKey());
      }
      legendsOption = legendLabels;
    }

    IASTAppendable result = F.Graphics(primitives);

    boolean hasPlotLegends = false;
    boolean hasPlotStyle = false;

    // Push user options overrides
    for (IExpr opt : options) {
      if (opt.isRuleAST()) {
        if (((IAST) opt).arg1().equals(S.PlotLegends)) {
          hasPlotLegends = true;
        }
        if (((IAST) opt).arg1().equals(S.PlotStyle)) {
          hasPlotStyle = true;
        }
        result.append(opt);
      }
    }

    // Bind inferred mapping rules
    if (mode == 2) {
      if (!hasPlotLegends)
        result.append(F.Rule(S.PlotLegends, legendsOption));
      if (!hasPlotStyle)
        result.append(F.Rule(S.PlotStyle, plotStyleOption));
    }

    return result;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return IFunctionEvaluator.ARGS_0_INFINITY;
  }

  private Set<Integer> parseElements(IExpr expr, EvalEngine engine) {
    Set<Integer> set = new LinkedHashSet<>();
    if (expr.isList()) {
      for (IExpr e : (IAST) expr) {
        set.addAll(parseElements(e, engine));
      }
    } else if (expr.isAST(S.Entity, 3) || expr.isAST(S.Entity, 2)) {
      IAST entity = (IAST) expr;
      if (entity.arg1().toString().replace("\"", "").equals("Element")) {
        String name = entity.arg2().toString().replace("\"", "");
        int z = nameToZ(name);
        if (z > 0)
          set.add(z);
      }
    } else if (expr.isString()) {
      String name = expr.toString().replace("\"", "");
      int z = nameToZ(name);
      if (z > 0)
        set.add(z);
    } else if (expr.isInteger()) {
      int z = expr.toIntDefault(0);
      if (z > 0 && z <= 118)
        set.add(z);
    } else if (expr.isAST(S.EntityClass, 3) || expr.isAST(S.EntityClass, 2)) {
      // Safely delegate EntityClass resolution back to the Engine Evaluator pipeline
      IExpr list = engine.evaluate(F.EntityList(expr));
      if (list.isList()) {
        set.addAll(parseElements(list, engine));
      }
    }
    return set;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    setOptions(newSymbol, OPTION_KEYS, OPTION_VALUES);
  }
}
