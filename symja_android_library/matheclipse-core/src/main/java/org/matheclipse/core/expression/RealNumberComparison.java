package org.matheclipse.core.expression;

import org.matheclipse.core.interfaces.IReal;

/** Orders real values through their exact comparison representation, independent of storage type. */
final class RealNumberComparison {
  private RealNumberComparison() {}

  static boolean isGreater(IReal left, IReal right) {
    return !left.isNaN() && !right.isNaN() && compare(left, right) > 0;
  }

  static boolean isLess(IReal left, IReal right) {
    return !left.isNaN() && !right.isNaN() && compare(left, right) < 0;
  }

  static int compare(IReal left, IReal right) {
    // Total sorting order: -Infinity < finite values < +Infinity < NaN.
    // The predicates above keep NaN unordered instead of using that sorting convention.
    if (left.isNaN() || right.isNaN()) {
      return Boolean.compare(left.isNaN(), right.isNaN());
    }
    if (left.isInfinite() || right.isInfinite()) {
      return Integer.compare(left.isInfinite() ? left.complexSign() : 0,
          right.isInfinite() ? right.complexSign() : 0);
    }
    // Exact values make both signed zeros equal without changing either stored representation.
    // Apfloat.compareTo already dispatches to Aprational when an exact fraction is involved.
    return left.exactComparisonValue().compareTo(right.exactComparisonValue());
  }
}
