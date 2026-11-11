package org.matheclipse.core.reflection.system;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.graphics.ColorDataGradients;
import org.matheclipse.core.graphics.ColorDataLists;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

public class ColorData extends AbstractFunctionEvaluator {

  public ColorData() {}

  private IExpr createColorDataFunction(String name, String type, IExpr range) {
    // Return ColorDataFunction[name, type, range]
    IExpr sym = F.symbol("ColorDataFunction");
    return F.function(sym, F.stringx(name), F.stringx(type), range);
  }

  @Override
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    if (ast.size() < 2) {
      // ColorData[]: return list of properties
      return F.List(F.stringx("Gradients"), F.stringx("Indexed"));
    }

    IExpr arg1 = ast.arg1();
    IExpr colorDataFunction = F.NIL;

    if (arg1.isString()) {
      String name = arg1.toString();
      if (name.startsWith("\"") && name.endsWith("\"")) {
        name = name.substring(1, name.length() - 1);
      }

      if (name.equalsIgnoreCase("Gradients")) {
        return getAllGradients();
      } else if (name.equalsIgnoreCase("Indexed")) {
        return getAllIndexed();
      } else if (name.equalsIgnoreCase("Named") && ast.isAST1()) {
        return F.List(F.stringx("Atoms"), F.stringx("Crayola"), F.stringx("GeologicAges"),
            F.stringx("HTML"), F.stringx("Legacy"), F.stringx("WebSafe"));
      } else if (name.equalsIgnoreCase("Physical") && ast.isAST1()) {
        return F.List(F.stringx("BlackBodySpectrum"), F.stringx("HypsometricTints"),
            F.stringx("VisibleSpectrum"));
      } else if (name.equalsIgnoreCase("WebSafe")) {
        // the colors named by their hex codes
        if (ast.isAST2()) {
          return ast.arg2().isString("ColorRules") ? NamedColorSchemes.webSafeRules()
              : webSafe(ast.arg2());
        }
        return createColorDataFunction("WebSafe", "Named", F.CEmptyList);
      } else if (name.equalsIgnoreCase(AtomColors.NAME)) {
        if (ast.isAST1()) {
          return AtomColors.colorDataFunction();
        }
        return ast.isAST2() ? AtomColors.property(ast.arg2()) : F.NIL;
      }

      IAST blendArgument = themeGradient(name);
      if (blendArgument != null) {
        return themeGradientProperty(ast, name, blendArgument, engine);
      }

      if (ast.isAST2()) {
        IExpr named = NamedColorSchemes.of(name, ast.arg2());
        if (named.isPresent()) {
          return named;
        }
      }

      // Try to find scheme
      colorDataFunction = findScheme(name);
    } else if (arg1.isList2() && arg1.first().isString() && arg1.second().isString()
        && arg1.second().toString().equals("Reverse")) {
      // {"Rainbow", "Reverse"}: the gradient run backwards over its range
      IExpr gradient = findScheme(arg1.first().toString());
      if (gradient.isPresent() && ((IAST) gradient).arg2().toString().equals("Gradients")) {
        IExpr range = ((IAST) gradient).arg3();
        colorDataFunction =
            ((IAST) gradient).setAtCopy(3, F.list(range.second(), range.first()));
      }
    } else if (arg1.isList2() && arg1.first().isString() && arg1.second().isList2()) {
      // {"Rainbow", {min, max}}: the gradient spread over the range from min to max; it runs
      // backwards if max < min
      IExpr range = arg1.second();
      double min = range.first().evalfNaN();
      double max = range.second().evalfNaN();
      if (min != max && !Double.isNaN(min) && !Double.isNaN(max)) {
        IExpr gradient = findScheme(arg1.first().toString());
        if (gradient.isPresent() && ((IAST) gradient).arg2().toString().equals("Gradients")) {
          colorDataFunction = ((IAST) gradient).setAtCopy(3, range);
        }
      }
    } else if (arg1.isInteger()) {
      int n = arg1.toIntDefault(-1);
      if (n >= 0) {
        String name = String.format(Locale.US, "_%03d", n);
        colorDataFunction = findIndexedScheme(name);
      }
    }

    if (colorDataFunction.isPresent()) {
      if (ast.isAST2() && ast.arg2().isReal()) {
        // ColorData(scheme, value) is the color the scheme's function gives for the value
        return engine.evaluate(F.unaryAST1(colorDataFunction, ast.arg2()));
      }
      if (ast.isAST2() && ast.arg2().toString().equals("ColorList")
          && ((IAST) colorDataFunction).arg2().toString().equals("Indexed")) {
        int length = colorListLength(((IAST) colorDataFunction).arg1().toString());
        if (length > 0) {
          final IExpr function = colorDataFunction;
          return F.mapRange(1, length + 1,
              i -> engine.evaluate(F.unaryAST1(function, F.ZZ(i))));
        }
      }
      if (ast.isAST2()) {
        String prop = ast.arg2().toString();
        if (prop.equals("Name")) {
          IAST function = (IAST) colorDataFunction;
          if (function.arg2().toString().equals("Gradients")) {
            return F.stringx(gradientTitle(function.arg1().toString()));
          }
          if (ast.arg1().isString()) {
            // the name as the scheme is called
            return ast.arg1();
          }
          if (colorDataFunction.argSize() >= 1) {
            return ((IAST) colorDataFunction).arg1();
          }
        }
        if (prop.equals("Range")) {
          if (colorDataFunction.argSize() >= 3) {
            return ((IAST) colorDataFunction).arg3();
          }
          // if (type.equals("Gradients")) {
          // return F.List(F.C0, F.C1);
          // }
          // if (type.equals("Indexed")) {
          // try {
          // ColorDataLists listDetails = ColorDataLists.valueOf(name);
          // return F.List(F.C1, F.ZZ(listDetails.length()));
          // } catch (Exception e) {
          // }
          // }
        }
        return F.NIL;
      }
      // if (ast.size() == 2) {
      return colorDataFunction;
      // } else if (ast.size() == 3) {
      // // specific param
      // IExpr param = ast.arg2();
      // return F.unary(colorDataFunction, param);
      // }
    }

    return F.NIL;
  }

  /** The name of the default gradient of density plots in the 2014 plot theme. */
  private static final String WL14_DENSITY = "WL14DefaultDensityGradient";

  /** The colors <code>WL14DefaultDensityGradient</code> blends, from dark blue to light yellow. */
  private static final IAST WL14_DENSITY_COLORS = F.List(//
      F.ternaryAST3(S.LABColor, F.num(0.2374577454874495), F.num(0.4502326479674301),
          F.num(-0.29344758919909575)), //
      F.ternaryAST3(S.LABColor, F.num(0.48288891610929785), F.num(0.7019509501984089),
          F.num(0.1585008503354124)), //
      F.ternaryAST3(S.LABColor, F.num(0.7226680127694958), F.num(0.3783642915277907),
          F.num(0.4813038070805067)), //
      F.ternaryAST3(S.LABColor, F.num(0.9735241229755421), F.num(-0.05249018370661607),
          F.num(0.31994142972937545)));

  /**
   * The colors a theme gradient blends evenly over <code>{0, 1}</code>.
   *
   * @return <code>null</code> if <code>name</code> is no theme gradient
   */
  public static IAST themeGradient(String name) {
    return WL14_DENSITY.equals(name) ? WL14_DENSITY_COLORS : null;
  }

  /** The description <code>ColorData(name, "Name")</code> gives for a theme gradient. */
  private static String themeGradientTitle(String name) {
    return WL14_DENSITY.equals(name) ? "WL14 default density gradient" : name;
  }

  /**
   * <code>ColorData(name, ...)</code> of a theme gradient: the gradient function, the color at a
   * value, or one of the properties <code>"BlendArgument", "Name", "Range"</code>.
   */
  private static IExpr themeGradientProperty(IAST ast, String name, IAST blendArgument,
      EvalEngine engine) {
    if (ast.isAST1()) {
      return F.quaternary(S.ColorDataFunction, F.stringx(name), F.stringx("ThemeGradients"),
          F.list(F.C0, F.C1), F.Function(F.binaryAST2(S.Blend, F.stringx(name), F.Slot1)));
    }
    if (!ast.isAST2()) {
      return F.NIL;
    }
    IExpr arg2 = ast.arg2();
    if (arg2.isReal()) {
      return engine.evaluate(F.binaryAST2(S.Blend, blendArgument, arg2));
    }
    if (arg2.isString("BlendArgument")) {
      return blendArgument;
    }
    if (arg2.isString("Name")) {
      return F.stringx(themeGradientTitle(name));
    }
    if (arg2.isString("Range")) {
      return F.list(F.C0, F.C1);
    }
    return F.NIL;
  }

  /**
   * The range of the values a gradient is spread over: wavelengths in nanometers for the visible
   * spectrum, temperatures in kelvin for the black body spectrum, <code>0 ... 1</code> otherwise.
   */
  private static IExpr gradientRange(ColorDataGradients g) {
    if (g == ColorDataGradients.VISIBLE_SPECTRUM) {
      return F.List(F.ZZ(380), F.ZZ(750));
    }
    if (g == ColorDataGradients.BLACK_BODY_SPECTRUM) {
      return F.List(F.ZZ(1000), F.ZZ(10000));
    }
    return F.List(F.C0, F.C1);
  }

  /**
   * The color a web safe hex code such as <code>"#99CC00"</code> names.
   *
   * @return {@link F#NIL} if <code>code</code> is no such code
   */
  public static IExpr webSafe(IExpr code) {
    if (!code.isString()) {
      return F.NIL;
    }
    String hex = code.toString();
    if (hex.startsWith("#")) {
      hex = hex.substring(1);
    }
    if (hex.length() != 6) {
      return F.NIL;
    }
    IASTAppendable rgb = F.ast(org.matheclipse.core.expression.S.RGBColor, 3);
    for (int i = 0; i < 3; i++) {
      int value;
      try {
        value = Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
      } catch (NumberFormatException nfe) {
        return F.NIL;
      }
      if (value % 0x33 != 0) {
        // the web safe components are 00, 33, 66, 99, CC and FF
        return F.NIL;
      }
      rgb.append(F.num(value / 255.0));
    }
    return rgb;
  }

  /**
   * The number of colors <code>ColorData(scheme, "ColorList")</code> lists for an indexed scheme.
   *
   * @return <code>-1</code> if <code>name</code> names no indexed scheme
   */
  private static int colorListLength(String name) {
    try {
      return ColorDataLists.valueOf(name).colorListLength();
    } catch (IllegalArgumentException unknownScheme) {
      return -1;
    }
  }

  private IExpr findIndexedScheme(String name) {
    try {
      return createColorDataFunction(name, "Indexed", ColorDataLists.valueOf(name).range());
    } catch (Exception e) {
      return F.NIL;
    }
  }

  private IExpr findScheme(String name) {
    // Try Gradient
    ColorDataGradients g;
    try {
      // Try direct match first
      g = ColorDataGradients.valueOf(name.toUpperCase(Locale.US));
    } catch (IllegalArgumentException e) {
      // Attempt to match insensitive and ignoring underscores
      g = gradientNamed(name);
    }
    if (g != null) {
      return createColorDataFunction(g.name(), "Gradients", gradientRange(g));
    }

    // Try Indexed by name (integer string)
    try {
      int n = Integer.parseInt(name);
      String idxName = String.format(Locale.US, "_%03d", n);
      return findIndexedScheme(idxName);
    } catch (NumberFormatException e) {
    }

    return F.NIL;
  }

  /**
   * The gradient a name asks for, ignoring case, spaces and underscores.
   *
   * <p>
   * A trailing <code>Colors</code> is dropped when nothing matches with it, because several schemes
   * are named both ways - <code>AvocadoColors</code> and <code>Avocado</code> are the same scheme.
   *
   * @return the gradient, or <code>null</code> when no scheme goes by that name
   */
  public static ColorDataGradients gradientNamed(String name) {
    String search = name.replace(" ", "").replace("_", "").toUpperCase(Locale.US);
    ColorDataGradients found = gradientMatching(search);
    if (found == null && search.endsWith("COLORS")) {
      found = gradientMatching(search.substring(0, search.length() - "COLORS".length()));
    }
    if (found == null && search.endsWith("TONES")) {
      // BrownCyanTones is BROWN_CYAN
      found = gradientMatching(search.substring(0, search.length() - "TONES".length()));
    }
    return found;
  }

  /**
   * The gradients <code>ColorData("Gradients")</code> lists, by the names they are known by. The
   * schemes that are not in this collection (<code>"Jet"</code>, <code>"Parula"</code>, ...) can
   * still be asked for by name.
   */
  private static final String[] GRADIENT_NAMES = {"AlpineColors", "Aquamarine", "ArmyColors",
      "AtlanticColors", "AuroraColors", "AvocadoColors", "BeachColors", "BlueGreenYellow",
      "BrassTones", "BrightBands", "BrownCyanTones", "CandyColors", "CherryTones", "CMYKColors",
      "CoffeeTones", "DarkBands", "DarkRainbow", "DarkTerrain", "DeepSeaColors", "FallColors",
      "FruitPunchColors", "FuchsiaTones", "GrayTones", "GrayYellowTones", "GreenBrownTerrain",
      "GreenPinkTones", "IslandColors", "LakeColors", "LightTemperatureMap", "LightTerrain",
      "MintColors", "NeonColors", "Pastel", "PearlColors", "PigeonTones", "PlumColors", "Rainbow",
      "RedBlueTones", "RedGreenSplit", "RoseColors", "RustTones", "SandyTerrain", "SiennaTones",
      "SolarColors", "SouthwestColors", "StarryNightColors", "SunsetColors", "TemperatureMap",
      "ThermometerColors", "ValentineTones", "WatermelonColors"};

  /** The first name in {@link #GRADIENT_NAMES} of each gradient, built on first use. */
  private static final class KnownNames {
    static final Map<ColorDataGradients, String> NAMES = new EnumMap<>(ColorDataGradients.class);
    static {
      for (String candidate : GRADIENT_NAMES) {
        ColorDataGradients g = gradientNamed(candidate);
        if (g != null) {
          NAMES.putIfAbsent(g, candidate);
        }
      }
    }
  }

  /** Names whose title joins two colours with a hyphen, followed by their titles. */
  private static final String[] HYPHENATED_TITLES = {"BlueGreenYellow", "blue-green-yellow",
      "BrownCyanTones", "brown-cyan tones", "GrayYellowTones", "gray-yellow tones",
      "GreenBrownTerrain", "green-brown terrain", "GreenPinkTones", "green-pink tones",
      "RedBlueTones", "red-blue tones", "RedGreenSplit", "red-green split"};

  /**
   * The description <code>ColorData(name, "Name")</code> gives for a gradient: its name in lower
   * case words, such as <code>"alpine colors"</code> for <code>"AlpineColors"</code> or
   * <code>"Alpine"</code>.
   *
   * @param name a name of the gradient
   */
  public static String gradientTitle(String name) {
    ColorDataGradients g = gradientNamed(name);
    String known = g != null ? KnownNames.NAMES.get(g) : null;
    if (known == null) {
      // a scheme outside the collection is described by the name of its table
      known = g != null ? toCamelCase(g.name()) : name;
    }
    for (int i = 0; i < HYPHENATED_TITLES.length; i += 2) {
      if (HYPHENATED_TITLES[i].equals(known)) {
        return HYPHENATED_TITLES[i + 1];
      }
    }
    if (known.startsWith("CMYK")) {
      return "CMYK" + camelCaseWords(known.substring(4));
    }
    return camelCaseWords(known).trim();
  }

  /** <code>"StarryNightColors"</code> as <code>" starry night colors"</code>. */
  private static String camelCaseWords(String camelCase) {
    StringBuilder buf = new StringBuilder();
    for (char c : camelCase.toCharArray()) {
      if (Character.isUpperCase(c)) {
        buf.append(' ').append(Character.toLowerCase(c));
      } else {
        buf.append(c);
      }
    }
    return buf.toString();
  }

  private static ColorDataGradients gradientMatching(String search) {
    if (search.isEmpty()) {
      return null;
    }
    for (ColorDataGradients g : ColorDataGradients.values()) {
      if (g.name().replace("_", "").equals(search)) {
        return g;
      }
    }
    return null;
  }

  private static IExpr getAllGradients() {
    IASTAppendable list = F.ListAlloc(GRADIENT_NAMES.length);
    for (String name : GRADIENT_NAMES) {
      list.append(F.stringx(name));
    }
    return list;
  }

  private IExpr getAllIndexed() {
    IASTAppendable list = F.ListAlloc(ColorDataLists.values().length);
    for (ColorDataLists g : ColorDataLists.values()) {
      try {
        int n = Integer.parseInt(g.name().substring(1));
        list.append(F.ZZ(n));
      } catch (Exception e) {
      }
    }
    return list;
  }

  private static String toCamelCase(String s) {
    if (s == null || s.isEmpty())
      return s;
    StringBuilder sb = new StringBuilder();
    boolean upper = true;
    for (char c : s.toCharArray()) {
      if (c == '_') {
        upper = true;
      } else {
        if (upper) {
          sb.append(Character.toUpperCase(c));
          upper = false;
        } else {
          sb.append(Character.toLowerCase(c));
        }
      }
    }
    return sb.toString();
  }

}
