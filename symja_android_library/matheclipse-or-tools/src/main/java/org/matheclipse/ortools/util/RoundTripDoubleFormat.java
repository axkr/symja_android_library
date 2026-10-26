// Copyright 2010-2025 Google LLC
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
// Modified for the Symja matheclipse-or-tools module: ported from ortools/util/fp_roundtrip_conv.h
// C++ to Java (the std::to_chars() formatting), see NOTICE in the module root.

package org.matheclipse.ortools.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Converts a <code>double</code> to a string from which the same <code>double</code> is parsed
 * back, the port of <code>operations_research::RoundTripDoubleFormat</code>.
 *
 * <p>
 * The format is the one of <code>std::to_chars(double)</code>: the shortest digits which round
 * trip, written in fixed notation (<code>0.1</code>, <code>100</code>) or in exponent notation
 * with a sign and at least two exponent digits (<code>1e+30</code>, <code>1.5e-07</code>),
 * whichever is shorter; a tie goes to the fixed notation. Infinities are <code>inf</code> and
 * <code>-inf</code>, NaN is <code>nan</code>, and the negative zero is <code>-0</code>.
 */
public final class RoundTripDoubleFormat {

  private RoundTripDoubleFormat() {}

  /** Returns the shortest round trip representation of <code>value</code>. */
  public static String toString(double value) {
    if (Double.isNaN(value)) {
      return "nan";
    }
    if (Double.isInfinite(value)) {
      return value > 0 ? "inf" : "-inf";
    }
    if (value == 0.0) {
      return (Double.doubleToRawLongBits(value) < 0) ? "-0" : "0";
    }
    final String sign = value < 0 ? "-" : "";
    final double abs = Math.abs(value);
    // Double.toString gives digits which round trip, but at least two of them, and before Java 19
    // sometimes more than needed: the nearest decimal with fewer digits may round trip as well
    BigDecimal decimal = new BigDecimal(Double.toString(abs)).stripTrailingZeros();
    for (int precision = 1; precision < decimal.precision(); ++precision) {
      final BigDecimal shorter = decimal.round(new MathContext(precision, RoundingMode.HALF_EVEN));
      if (Double.parseDouble(shorter.toString()) == abs) {
        decimal = shorter.stripTrailingZeros();
        break;
      }
    }
    final String digits = decimal.unscaledValue().toString();
    final int numDigits = digits.length();
    // the decimal exponent of the first digit
    final int exponent = numDigits - 1 - decimal.scale();

    final StringBuilder scientific = new StringBuilder(sign).append(digits.charAt(0));
    if (numDigits > 1) {
      scientific.append('.').append(digits, 1, numDigits);
    }
    scientific.append('e').append(exponent < 0 ? '-' : '+');
    final int absExponent = Math.abs(exponent);
    if (absExponent < 10) {
      scientific.append('0');
    }
    scientific.append(absExponent);

    final StringBuilder fixed = new StringBuilder(sign);
    if (exponent >= numDigits - 1) {
      fixed.append(digits);
      for (int i = numDigits - 1; i < exponent; ++i) {
        fixed.append('0');
      }
    } else if (exponent >= 0) {
      fixed.append(digits, 0, exponent + 1).append('.').append(digits, exponent + 1, numDigits);
    } else {
      fixed.append("0.");
      for (int i = -1; i > exponent; --i) {
        fixed.append('0');
      }
      fixed.append(digits);
    }
    return fixed.length() <= scientific.length() ? fixed.toString() : scientific.toString();
  }

  /**
   * Parses a number printed by {@link #toString(double)}.
   *
   * @throws NumberFormatException if <code>str</code> is not a number or has extra characters
   */
  public static double parse(String str) {
    switch (str) {
      case "inf":
      case "infinity":
        return Double.POSITIVE_INFINITY;
      case "-inf":
      case "-infinity":
        return Double.NEGATIVE_INFINITY;
      case "nan":
      case "-nan":
        return Double.NaN;
      default:
        break;
    }
    // Double.parseDouble also accepts surrounding white space, a d/f suffix and hexadecimal input
    for (int i = 0; i < str.length(); ++i) {
      final char c = str.charAt(i);
      if (!(c >= '0' && c <= '9') && c != '-' && c != '+' && c != '.' && c != 'e' && c != 'E') {
        throw new NumberFormatException("not a number: \"" + str + "\"");
      }
    }
    return Double.parseDouble(str);
  }
}
