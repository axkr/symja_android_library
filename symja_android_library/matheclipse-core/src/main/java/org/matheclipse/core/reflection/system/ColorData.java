package org.matheclipse.core.reflection.system;

import java.util.Locale;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.tensor.img.ColorDataGradients;
import org.matheclipse.core.tensor.img.ColorDataLists;

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
      } else if (name.equalsIgnoreCase("WebSafe")) {
        // the colors named by their hex codes
        if (ast.isAST2()) {
          return webSafe(ast.arg2());
        }
        return createColorDataFunction("WebSafe", "Named", F.CEmptyList);
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
        IExpr range = ((IAST) colorDataFunction).arg3();
        int length = range.isList2() ? range.second().toIntDefault() : -1;
        if (length > 0) {
          final IExpr function = colorDataFunction;
          return F.mapRange(1, length + 1,
              i -> engine.evaluate(F.unaryAST1(function, F.ZZ(i))));
        }
      }
      if (ast.isAST2()) {
        String prop = ast.arg2().toString();
        if (prop.equals("Name")) {
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
          // return F.List(F.C1, F.ZZ(listDetails.cyclic().length()));
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

  private IExpr findIndexedScheme(String name) {
    try {
      ColorDataLists l = ColorDataLists.valueOf(name);
      int len = l.cyclic().length();
      return createColorDataFunction(name, "Indexed", F.List(F.C1, F.ZZ(len)));
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
    return found;
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

  private IExpr getAllGradients() {
    IASTAppendable list = F.ListAlloc(ColorDataGradients.values().length);
    for (ColorDataGradients g : ColorDataGradients.values()) {
      list.append(F.stringx(toCamelCase(g.name())));
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

  private String toCamelCase(String s) {
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
