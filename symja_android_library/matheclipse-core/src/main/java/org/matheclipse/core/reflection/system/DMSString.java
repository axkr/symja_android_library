package org.matheclipse.core.reflection.system;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IRational;

/**
 * <code>DMSString(angle)</code> - the string <code>d°m's"</code> of an angle in degrees, without
 * its sign. The angle can be a number, a list <code>{d, m, s}</code> (optionally followed by one of
 * the hemisphere letters <code>"N"</code>, <code>"S"</code>, <code>"E"</code>, <code>"W"</code>), a
 * string like <code>"30d15m50s"</code>, an angle quantity, or a latitude-longitude pair, which is
 * written with its hemisphere letters.
 *
 * <p>
 * <code>DMSString(angle, n)</code> gives the seconds with <code>n</code> decimals (default
 * <code>3</code>); <code>DMSString(angle, "XY")</code> appends <code>"X"</code> for a non-negative
 * angle and <code>"Y"</code> for a negative one; <code>DMSString(angle, {"XY", n})</code> does both.
 * Exact seconds which are an integer are written without decimals.
 */
public class DMSString extends AbstractFunctionEvaluator {

  @Override
  public IExpr evaluate(final IAST ast, EvalEngine engine) {
    int precision = 3;
    String hemisphere = null;
    if (ast.isAST2()) {
      IExpr spec = ast.arg2();
      if (spec.isString()) {
        hemisphere = spec.toString();
      } else if (spec.isList2() && spec.first().isString()) {
        hemisphere = spec.first().toString();
        precision = spec.second().toIntDefault();
      } else {
        precision = spec.toIntDefault();
      }
      if (precision < 0 || precision > 27 || (hemisphere != null && hemisphere.length() != 2)) {
        return F.NIL;
      }
    }
    IExpr arg1 = ast.arg1();
    if (arg1.isList2()) {
      // a latitude and a longitude
      IExpr latitude = angle(arg1.first(), engine);
      IExpr longitude = angle(arg1.second(), engine);
      if (latitude.isNIL() || longitude.isNIL()) {
        return F.NIL;
      }
      return F.stringx(withHemisphere(latitude, "NS", precision) + "  "
          + withHemisphere(longitude, "EW", precision));
    }
    IExpr value;
    if (arg1.isString()) {
      value = parse(arg1.toString());
    } else if (arg1.isList() && (arg1.argSize() == 3 || arg1.argSize() == 4)) {
      value = fromDMS((IAST) arg1, engine);
    } else {
      value = angle(arg1, engine);
    }
    if (value.isNIL()) {
      return F.NIL;
    }
    if (hemisphere != null) {
      return F.stringx(withHemisphere(value, hemisphere, precision));
    }
    return F.stringx(format(value, precision));
  }

  private static String withHemisphere(IExpr value, String letters, int precision) {
    char letter = isNegative(value) ? letters.charAt(1) : letters.charAt(0);
    return format(value, precision) + letter;
  }

  private static boolean isNegative(IExpr value) {
    return value.isNegative();
  }

  /**
   * The angle in degrees as an exact rational or a machine number, {@link F#NIL} if it is none.
   */
  private static IExpr angle(IExpr expr, EvalEngine engine) {
    if (expr.isRational()) {
      return expr;
    }
    if (expr.isQuantity() || expr.isAST(S.Quantity, 3)) {
      expr = engine.evaluate(F.unaryAST1(S.QuantityMagnitude,
          F.binaryAST2(S.UnitConvert, expr, F.stringx("AngularDegrees"))));
      if (expr.isRational()) {
        return expr;
      }
    }
    if (expr.isNumericFunction()) {
      double value = expr.evalfNaN();
      if (Double.isFinite(value)) {
        return F.num(value);
      }
    }
    return F.NIL;
  }

  /** <code>{d, m, s}</code> or <code>{d, m, s, hemisphere}</code> as an angle in degrees. */
  private static IExpr fromDMS(IAST list, EvalEngine engine) {
    IExpr d = angle(list.arg1(), engine);
    IExpr m = angle(list.arg2(), engine);
    IExpr s = angle(list.arg3(), engine);
    if (d.isNIL() || m.isNIL() || s.isNIL()) {
      return F.NIL;
    }
    IExpr value = engine.evaluate(F.Plus(d, F.Times(F.QQ(1, 60), m), F.Times(F.QQ(1, 3600), s)));
    if (list.argSize() == 4) {
      if (!list.arg4().isString()) {
        return F.NIL;
      }
      switch (list.arg4().toString()) {
        case "N":
        case "E":
          return engine.evaluate(F.Abs(value));
        case "S":
        case "W":
          return engine.evaluate(F.Negate(F.Abs(value)));
        default:
          return F.NIL;
      }
    }
    return value;
  }

  /**
   * Parse a string like <code>30°15'50.5"</code> or <code>30d15m50s</code> with an optional sign
   * and an optional hemisphere <code>N</code>, <code>S</code>, <code>E</code>, <code>W</code> (also
   * as a word); south and west are negative. The decimal numbers are read exactly.
   */
  private static IExpr parse(String str) {
    String rest = str.trim();
    boolean negative = false;
    if (rest.startsWith("-") || rest.startsWith("+")) {
      negative = rest.charAt(0) == '-';
      rest = rest.substring(1);
    }
    IRational[] parts = new IRational[] {F.C0, F.C0, F.C0};
    String[] marks = new String[] {"°d", "'′m", "\"″s"};
    int[] position = new int[] {0};
    for (int i = 0; i < 3; i++) {
      if (position[0] >= rest.length() || !Character.isDigit(rest.charAt(position[0]))) {
        if (i == 0) {
          return F.NIL;
        }
        break;
      }
      IRational number = number(rest, position);
      if (number == null || position[0] >= rest.length()
          || marks[i].indexOf(rest.charAt(position[0])) < 0) {
        return F.NIL;
      }
      position[0]++;
      parts[i] = number;
    }
    switch (rest.substring(position[0]).trim()) {
      case "":
      case "N":
      case "E":
      case "North":
      case "East":
        break;
      case "S":
      case "W":
      case "South":
      case "West":
        negative = !negative;
        break;
      default:
        return F.NIL;
    }
    IRational value =
        parts[0].add(parts[1].multiply(F.QQ(1, 60))).add(parts[2].multiply(F.QQ(1, 3600)));
    return negative ? value.negate() : value;
  }

  /** A decimal number <code>123</code> or <code>12.25</code> as an exact rational. */
  private static IRational number(String str, int[] position) {
    int start = position[0];
    int end = start;
    while (end < str.length() && (Character.isDigit(str.charAt(end)) || str.charAt(end) == '.')) {
      end++;
    }
    position[0] = end;
    try {
      BigDecimal decimal = new BigDecimal(str.substring(start, end));
      BigInteger denominator = BigInteger.TEN.pow(Math.max(decimal.scale(), 0));
      return F.QQ(F.ZZ(decimal.movePointRight(decimal.scale()).toBigIntegerExact()),
          F.ZZ(denominator));
    } catch (RuntimeException rex) {
      return null;
    }
  }

  /**
   * The absolute value of the angle in degrees as <code>d°m's"</code>, the seconds rounded half to
   * even to <code>precision</code> decimals.
   */
  private static String format(IExpr value, int precision) {
    BigInteger pow = BigInteger.TEN.pow(precision);
    BigInteger scaled;
    if (value.isRational()) {
      IRational abs = ((IRational) value).abs();
      IRational seconds = abs.multiply(F.ZZ(3600));
      if (seconds.isInteger()) {
        BigInteger total = seconds.toBigNumerator();
        return total.divide(BigInteger.valueOf(3600)) + "°"
            + total.mod(BigInteger.valueOf(3600)).divide(BigInteger.valueOf(60)) + "'"
            + total.mod(BigInteger.valueOf(60)) + "\"";
      }
      BigDecimal exact = new BigDecimal(seconds.toBigNumerator()).multiply(new BigDecimal(pow))
          .divide(new BigDecimal(seconds.toBigDenominator()), 0, RoundingMode.HALF_EVEN);
      scaled = exact.toBigInteger();
    } else {
      double x = Math.abs(value.evalfNaN());
      double degrees = Math.floor(x);
      BigDecimal fraction = new BigDecimal((x - degrees) * 3600.0).multiply(new BigDecimal(pow))
          .setScale(0, RoundingMode.HALF_EVEN);
      scaled = BigDecimal.valueOf(degrees).toBigInteger()
          .multiply(BigInteger.valueOf(3600).multiply(pow)).add(fraction.toBigInteger());
    }
    BigInteger degreeUnit = BigInteger.valueOf(3600).multiply(pow);
    BigInteger minuteUnit = BigInteger.valueOf(60).multiply(pow);
    BigInteger degrees = scaled.divide(degreeUnit);
    BigInteger minutes = scaled.mod(degreeUnit).divide(minuteUnit);
    BigInteger secondsScaled = scaled.mod(minuteUnit);
    StringBuilder buf = new StringBuilder();
    buf.append(degrees).append('°').append(minutes).append('\'');
    buf.append(secondsScaled.divide(pow)).append('.');
    if (precision > 0) {
      String fraction = secondsScaled.mod(pow).toString();
      for (int i = fraction.length(); i < precision; i++) {
        buf.append('0');
      }
      buf.append(fraction);
    }
    buf.append('"');
    return buf.toString();
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_2;
  }
}
