package org.matheclipse.core.builtin;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class EntityFunctions {

  private static class Initializer {

    private static void init() {
      S.RGBColor.setEvaluator(new RGBColor());
    }
  }

  private static class RGBColor extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST1() && ast.arg1().isString()) {
        return hexStringToRGBColor(ast.arg1().toString());
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_INFINITY;
    }
  }

  /**
   * Convert a hexadecimal color string in one of the notations <code>#RGB</code>,
   * <code>#RRGGBB</code> or <code>#RRGGBBAA</code> into an {@link S#RGBColor} expression with the
   * channel values scaled from <code>0.0</code> to <code>1.0</code>.
   *
   * @param hexString the color string including the leading <code>#</code> character
   * @return {@link F#NIL} if <code>hexString</code> isn't one of the supported notations
   */
  private static IExpr hexStringToRGBColor(String hexString) {
    if (hexString.length() < 2 || hexString.charAt(0) != '#') {
      return F.NIL;
    }
    final String digits = hexString.substring(1);
    // #RGB, #RRGGBB and #RRGGBBAA
    final int digitsPerChannel;
    switch (digits.length()) {
      case 3:
        digitsPerChannel = 1;
        break;
      case 6:
      case 8:
        digitsPerChannel = 2;
        break;
      default:
        return F.NIL;
    }

    final int numberOfChannels = digits.length() / digitsPerChannel;
    IExpr[] channels = new IExpr[numberOfChannels];
    for (int i = 0; i < numberOfChannels; i++) {
      int value = 0;
      for (int j = 0; j < digitsPerChannel; j++) {
        int digit = Character.digit(digits.charAt(i * digitsPerChannel + j), 16);
        if (digit < 0) {
          return F.NIL;
        }
        value = value * 16 + digit;
      }
      if (digitsPerChannel == 1) {
        // in the short notation every digit is duplicated, i.e. #F00 is the same as #FF0000
        value = value * 16 + value;
      }
      channels[i] = F.num(value / 255.0);
    }

    return numberOfChannels == 3 //
        ? F.RGBColor(channels[0], channels[1], channels[2])
        : F.RGBColor(channels[0], channels[1], channels[2], channels[3]);
  }

  public static void initialize() {
    Initializer.init();
  }

  private EntityFunctions() {}
}
