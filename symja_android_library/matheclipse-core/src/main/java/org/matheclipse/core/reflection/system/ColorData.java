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
      }

      // Try to find scheme
      colorDataFunction = findScheme(name);
    } else if (arg1.isInteger()) {
      int n = arg1.toIntDefault(-1);
      if (n >= 0) {
        String name = String.format(Locale.US, "_%03d", n);
        colorDataFunction = findIndexedScheme(name);
      }
    }

    if (colorDataFunction.isPresent()) {
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
    try {
      // Try direct match first
      ColorDataGradients g = ColorDataGradients.valueOf(name.toUpperCase(Locale.US));
      return createColorDataFunction(g.name(), "Gradients", F.List(F.C0, F.C1));
    } catch (IllegalArgumentException e) {
      // Attempt to match insensitive and ignoring underscores
      String search = name.replace(" ", "").replace("_", "").toUpperCase(Locale.US);
      for (ColorDataGradients g : ColorDataGradients.values()) {
        String gName = g.name().replace("_", "");
        if (gName.equals(search)) {
          return createColorDataFunction(g.name(), "Gradients", F.List(F.C0, F.C1));
        }
      }
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
