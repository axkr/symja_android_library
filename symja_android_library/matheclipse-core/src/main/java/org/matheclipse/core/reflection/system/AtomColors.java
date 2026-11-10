package org.matheclipse.core.reflection.system;

import java.util.Locale;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * The named color scheme <code>"Atoms"</code> of {@link ColorData}: a color for each chemical
 * element, asked for by its symbol (in any case) or its atomic number.
 *
 * <p>
 * Besides the 118 elements the scheme knows the 7 provisional names <code>"Uub" ... "Uuo"</code>
 * of the elements 112 to 118, which are reached by the atomic numbers 119 to 125. The range, the
 * color list and the periodic table image cover the 118 elements only.
 */
final class AtomColors {

  /** The name of the scheme. */
  static final String NAME = "Atoms";

  /** The number of elements of the range, the color list and the image. */
  private static final int ELEMENTS = 118;

  /**
   * <code>symbol, r, g, b</code> of the elements by atomic number, then the provisional names. A
   * channel written without a decimal point is an exact integer.
   */
  private static final String[] SYMBOL_COLORS = {
      "H,0.65,0.7,0.7", "He,0.836713,1.,1.", "Li,0.799435,0.543572,0.997559",
      "Be,0.770565,0.964309,0.0442359", "B,1.,0.709804,0.709804", "C,0.4,0.4,0.4",
      "N,0.291989,0.437977,0.888609", "O,0.800498,0.201504,0.192061",
      "F,0.578462,0.85539,0.408855", "Ne,0.677263,0.928423,0.955287",
      "Na,0.658708,0.492173,0.842842", "Mg,0.628274,0.850553,0.0782731",
      "Al,0.8913,0.631904,0.627399", "Si,0.941176,0.784314,0.627451", "P,1.,0.501961,0",
      "S,0.90443,0.97015,0.13504", "Cl,0.412698,0.932689,0.166398",
      "Ar,0.546138,0.844244,0.892092", "K,0.534026,0.420729,0.705621",
      "Ca,0.480072,0.744591,0.0955222", "Sc,0.901961,0.901961,0.901961",
      "Ti,0.74902,0.760784,0.780392", "V,0.65098,0.65098,0.670588", "Cr,0.541176,0.6,0.780392",
      "Mn,0.611765,0.478431,0.780392", "Fe,0.878431,0.4,0.2", "Co,0.941176,0.564706,0.627451",
      "Ni,0.313725,0.815686,0.313725", "Cu,0.784314,0.501961,0.2", "Zn,0.490196,0.501961,0.690196",
      "Ga,0.800757,0.542666,0.533513", "Ge,0.60508,0.632465,0.576489",
      "As,0.741176,0.501961,0.890196", "Se,0.917248,0.657833,0.0706628",
      "Br,0.58847,0.22163,0.16064", "Kr,0.426019,0.747462,0.810413",
      "Rb,0.425391,0.329242,0.585895", "Sr,0.325959,0.646423,0.095983", "Y,0.531014,1.,1.",
      "Zr,0.458599,0.917466,0.918573", "Nb,0.385036,0.834854,0.841681",
      "Mo,0.310325,0.752163,0.769323", "Tc,0.234466,0.669394,0.701499",
      "Ru,0.157459,0.586546,0.638209", "Rh,0.0793033,0.50362,0.579453", "Pd,0.,0.420615,0.525231",
      "Ag,0.752941,0.752941,0.752941", "Cd,1.,0.85098,0.560784", "In,0.728371,0.440594,0.422196",
      "Sn,0.39799,0.491477,0.495586", "Sb,0.619608,0.388235,0.709804",
      "Te,0.816706,0.451332,0.0100947", "I,0.580392,0,0.580392", "Xe,0.316906,0.638078,0.710252",
      "Cs,0.332803,0.217712,0.483666", "Ba,0.165935,0.55605,0.0796556",
      "La,0.928084,0.716075,0.329427", "Ce,0.894824,0.731424,0.325131",
      "Pr,0.86523,0.707999,0.315261", "Nd,0.837836,0.662974,0.301635",
      "Pm,0.811992,0.607859,0.285626", "Sm,0.787563,0.549894,0.268279",
      "Eu,0.764628,0.493261,0.250405", "Gd,0.743177,0.440115,0.23269",
      "Tb,0.72281,0.39143,0.215783", "Dy,0.702434,0.347663,0.200392",
      "Ho,0.679962,0.309234,0.187368", "Er,0.652012,0.276823,0.17779",
      "Tm,0.613603,0.251489,0.173042", "Yb,0.557855,0.234598,0.17489",
      "Lu,0.475685,0.227573,0.18555", "Hf,0.781537,0.717388,0.716579",
      "Ta,0.734443,0.544489,0.683471", "W,0.681179,0.360409,0.63675",
      "Re,0.605181,0.367584,0.556343", "Os,0.521806,0.382125,0.469204",
      "Ir,0.445624,0.373159,0.399069", "Pt,0.815686,0.815686,0.878431", "Au,1.,0.819608,0.137255",
      "Hg,0.721569,0.721569,0.815686", "Tl,0.65098,0.329412,0.301961",
      "Pb,0.341176,0.34902,0.380392", "Bi,0.619608,0.309804,0.709804", "Po,0.670588,0.360784,0",
      "At,0.458824,0.309804,0.270588", "Rn,0.218799,0.516091,0.591608",
      "Fr,0.25626,0.0861372,0.398932", "Ra,0.,0.473472,0.04654", "Ac,0.322042,0.71693,0.988479",
      "Th,0.3608,0.67166,0.943003", "Pa,0.397469,0.628,0.898853", "U,0.43205,0.58595,0.856029",
      "Np,0.464542,0.54551,0.814532", "Pu,0.494945,0.506679,0.774361",
      "Am,0.52326,0.469458,0.735517", "Cm,0.549486,0.433847,0.697999",
      "Bk,0.573624,0.399845,0.661808", "Cf,0.595673,0.367454,0.626942",
      "Es,0.615633,0.336672,0.593404", "Fm,0.633505,0.307499,0.561191",
      "Md,0.649288,0.279937,0.530305", "No,0.662982,0.253984,0.500746",
      "Lr,0.674588,0.22964,0.472513", "Rf,0.684106,0.206907,0.445606",
      "Db,0.691534,0.185783,0.420025", "Sg,0.696874,0.166269,0.395772",
      "Bh,0.700126,0.148365,0.372844", "Hs,0.701289,0.13207,0.351243",
      "Mt,0.700363,0.117385,0.330968", "Ds,0.697348,0.10431,0.31202",
      "Rg,0.692245,0.0928444,0.294398", "Cn,0.685054,0.0829886,0.278102",
      "Nh,0.675773,0.0747426,0.263133", "Fl,0.664405,0.0681063,0.249491",
      "Mc,0.650947,0.0630797,0.237174", "Lv,0.635401,0.0596628,0.226184",
      "Ts,0.635401,0.056628,0.226184", "Og,0.635401,0.0528,0.226184",
      "Uub,0.685054,0.0829886,0.278102", "Uut,0.675773,0.0747426,0.263133",
      "Uuq,0.664405,0.0681063,0.249491", "Uup,0.650947,0.0630797,0.237174",
      "Uuh,0.635401,0.0596628,0.226184", "Uus,0.635401,0.056628,0.226184",
      "Uuo,0.635401,0.0528,0.226184",
  };

  /** The first atomic number of the periods 1 to 7, followed by the end of the 7th period. */
  private static final int[] PERIOD_START = {1, 3, 11, 19, 37, 55, 87, 119};

  /** The number of columns and rows of the periodic table image. */
  private static final int COLUMNS = 18;

  private static final int ROWS = 10;

  /**
   * The rows of the f-block elements lie this many rows below their period, one empty row apart
   * from the main table.
   */
  private static final int F_BLOCK_ROW_OFFSET = 3;

  private static final class Holder {
    static final String[] SYMBOLS = symbols();
    static final IAST[] COLORS = colors();
  }

  private AtomColors() {}

  private static String[] symbols() {
    String[] symbols = new String[SYMBOL_COLORS.length];
    for (int i = 0; i < SYMBOL_COLORS.length; i++) {
      symbols[i] = SYMBOL_COLORS[i].substring(0, SYMBOL_COLORS[i].indexOf(','));
    }
    return symbols;
  }

  private static IAST[] colors() {
    IAST[] colors = new IAST[SYMBOL_COLORS.length];
    for (int i = 0; i < SYMBOL_COLORS.length; i++) {
      String[] parts = SYMBOL_COLORS[i].split(",");
      colors[i] = F.RGBColor(channel(parts[1]), channel(parts[2]), channel(parts[3]));
    }
    return colors;
  }

  private static IExpr channel(String value) {
    return value.indexOf('.') < 0 ? F.ZZ(Integer.parseInt(value)) : F.num(Double.parseDouble(value));
  }

  /**
   * The index into {@link #SYMBOL_COLORS} of an atomic number <code>1 ... 125</code> or a symbol in
   * any case.
   *
   * @return <code>-1</code> if <code>element</code> is neither
   */
  private static int indexOf(IExpr element) {
    if (element.isInteger()) {
      int z = element.toIntDefault();
      return z >= 1 && z <= SYMBOL_COLORS.length ? z - 1 : -1;
    }
    if (element.isString()) {
      String symbol = element.toString();
      for (int i = 0; i < Holder.SYMBOLS.length; i++) {
        if (Holder.SYMBOLS[i].equalsIgnoreCase(symbol)) {
          return i;
        }
      }
    }
    return -1;
  }

  /**
   * The color of an element given by its atomic number or its symbol.
   *
   * @return {@link F#NIL} if <code>element</code> names no element of the scheme
   */
  static IExpr color(IExpr element) {
    int index = indexOf(element);
    return index < 0 ? F.NIL : Holder.COLORS[index];
  }

  /**
   * <code>ColorDataFunction("Atoms", "Named", {symbols, lowerCaseSymbols}, function)</code>, where
   * the function replaces a lower case symbol by its color.
   */
  static IAST colorDataFunction() {
    int n = SYMBOL_COLORS.length;
    IASTAppendable symbols = F.ListAlloc(n);
    IASTAppendable lowerCase = F.ListAlloc(n);
    IASTAppendable rules = F.ListAlloc(n);
    for (int i = 0; i < n; i++) {
      String lower = Holder.SYMBOLS[i].toLowerCase(Locale.US);
      symbols.append(F.stringx(Holder.SYMBOLS[i]));
      lowerCase.append(F.stringx(lower));
      rules.append(F.Rule(F.stringx(lower), Holder.COLORS[i]));
    }
    IExpr function = F.Function(F.Replace(F.Slot1, F.unaryAST1(S.Dispatch, rules)));
    return F.quaternary(S.ColorDataFunction, F.stringx(NAME), F.stringx("Named"),
        F.list(symbols, lowerCase), function);
  }

  /**
   * <code>ColorData("Atoms", argument)</code>: the color of an element, or one of the properties
   * <code>"ColorFunction", "ColorList", "Image", "Range"</code>.
   *
   * @return {@link F#NIL} if <code>argument</code> is neither an element nor such a property
   */
  static IExpr property(IExpr argument) {
    if (argument.isString("ColorFunction")) {
      return colorDataFunction();
    }
    if (argument.isString("ColorList")) {
      return F.mapRange(0, ELEMENTS, i -> Holder.COLORS[i]);
    }
    if (argument.isString("Range")) {
      return F.list(F.mapRange(0, ELEMENTS, i -> F.stringx(Holder.SYMBOLS[i])));
    }
    if (argument.isString("Image")) {
      return image();
    }
    return color(argument);
  }

  /**
   * The row and column of an element in the periodic table image: the main table fills the rows 1
   * to 7, the f-block of the periods 6 and 7 the rows 9 and 10 from column 3 on.
   *
   * @param z the atomic number <code>1 ... 118</code>
   * @return <code>{row, column}</code> counted from 1
   */
  static int[] tablePosition(int z) {
    int period = 1;
    while (z >= PERIOD_START[period]) {
      period++;
    }
    int offset = z - PERIOD_START[period - 1];
    int length = PERIOD_START[period] - PERIOD_START[period - 1];
    if (offset < Math.min(2, length - 1)) {
      // the s-block on the left; helium is placed above the noble gases
      return new int[] {period, offset + 1};
    }
    if (length == 32 && offset < 17) {
      // the 15 lanthanides or actinides
      return new int[] {period + F_BLOCK_ROW_OFFSET, offset + 1};
    }
    // the remaining elements of a period are aligned on the right
    return new int[] {period, COLUMNS - (length - 1 - offset)};
  }

  /**
   * The gray level of an element's label: dark on light colors, light on dark colors, measured by
   * the luminance <code>0.299 r + 0.587 g + 0.114 b</code>.
   */
  private static double labelGrayLevel(IAST color) {
    double luminance = 0.299 * color.arg1().evalf() + 0.587 * color.arg2().evalf()
        + 0.114 * color.arg3().evalf();
    return luminance > 0.5 ? 0.2 : 0.85;
  }

  /**
   * The periodic table of the elements in their colors: a <code>Graphics</code> of a 10 x 18 grid
   * of cells <code>{color, Rectangle(...), Inset(symbol, {column, y}, ...)}</code>, with
   * <code>{}</code> for an empty cell. The y coordinate of row <code>r</code> is
   * <code>11 - r</code>.
   */
  static IAST image() {
    IExpr[][] cells = new IExpr[ROWS][COLUMNS];
    for (int z = 1; z <= ELEMENTS; z++) {
      int[] position = tablePosition(z);
      cells[position[0] - 1][position[1] - 1] = cell(z, position[0], position[1]);
    }
    IASTAppendable rows = F.ListAlloc(ROWS);
    for (int r = 0; r < ROWS; r++) {
      IASTAppendable row = F.ListAlloc(COLUMNS);
      for (int c = 0; c < COLUMNS; c++) {
        row.append(cells[r][c] == null ? F.CEmptyList : cells[r][c]);
      }
      rows.append(row);
    }
    // the options are given as one list
    return F.binaryAST2(S.Graphics, F.list(F.EdgeForm(F.Opacity(0.1)), rows),
        F.list(F.Rule(S.ContentSelectable, S.False), F.Rule(S.PlotRangePadding, S.None)));
  }

  private static IAST cell(int z, int row, int column) {
    IAST color = Holder.COLORS[z - 1];
    int y = ROWS + 1 - row;
    // the corners column -+ 0.45, y -+ 0.45 as the nearest machine reals of these decimals
    IAST rectangle = F.Rectangle(
        F.list(F.num((100 * column - 45) / 100.0), F.num((100 * y - 45) / 100.0)),
        F.list(F.num((100 * column + 45) / 100.0), F.num((100 * y + 45) / 100.0)));
    IASTAppendable label = F.ast(S.Style, 5);
    label.append(F.stringx(Holder.SYMBOLS[z - 1]));
    label.append(F.Rule(S.FontSize, F.Scaled(0.022)));
    label.append(S.Bold);
    label.append(F.Rule(S.FontFamily, F.stringx("Helvetica")));
    label.append(F.GrayLevel(labelGrayLevel(color)));
    IAST inset = F.ternaryAST3(S.Inset, label, F.list(F.ZZ(column), F.ZZ(y)),
        F.Rule(S.Alignment, F.list(S.Center, S.Center)));
    return F.list(color, rectangle, inset);
  }
}
