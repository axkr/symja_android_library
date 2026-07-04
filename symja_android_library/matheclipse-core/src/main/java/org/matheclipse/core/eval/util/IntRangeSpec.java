package org.matheclipse.core.eval.util;

import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class IntRangeSpec {
  final int min;
  final int max;
  final int step;

  public int minimum() {
    return min;
  }

  public int maximum() {
    return max;
  }

  public int step() {
    return step;
  }

  public IntRangeSpec(int min, int max) {
    this.min = min;
    this.max = max;
    this.step = 1;
  }

  public IntRangeSpec(int min, int max, int step) {
    this.min = min;
    this.max = max;
    this.step = step;
  }

  public static IntRangeSpec createNonNegative() {
    return new IntRangeSpec(0, Integer.MAX_VALUE, 1);
  }

  public static IntRangeSpec createNonNegative(IAST ast, int position) {
    IntRangeSpec range = null;
    if (ast.size() <= position) {
      range = createNonNegative();
    } else if (ast.size() > position) {
      range = createNonNegative(ast.get(position));
    }
    return range;
  }

  public static IntRangeSpec createNonNegative(IExpr specification) {
    int min = 0;
    int max = Integer.MAX_VALUE;
    int step = 1;

    if (specification.equals(S.All) || specification.isInfinity()) {
      // all from 0 to Integer.MAX_VALUE
    } else if (specification.isInteger()) {
      max = specification.toIntDefault(-1);
      if (max < 0)
        return null;
    } else if (specification.isList1()) {
      min = specification.first().toIntDefault(-1);
      if (min < 0)
        return null;
      max = min;
    } else if (specification.isList2() || specification.isList3()) {
      IAST list = (IAST) specification;
      min = list.first().toIntDefault(-1);
      if (min < 0)
        return null;

      int parsedStep = 1;
      if (specification.isList3()) {
        parsedStep = list.arg3().toIntDefault(0);
        if (parsedStep == 0)
          return null; // Step cannot be mathematically 0
      }
      step = parsedStep;

      IExpr arg2 = list.second();
      if (arg2.isInfinity() || arg2.equals(S.All)) {
        max = step > 0 ? Integer.MAX_VALUE : 0;
      } else {
        max = arg2.toIntDefault(-1);
        if (max < 0)
          return null;
      }
    } else {
      return null;
    }
    return new IntRangeSpec(min, max, step);
  }

  public boolean isIncluded(int value) {
    if (step > 0) {
      if (min <= value && value <= max) {
        if (step == 1)
          return true;
        return (value - min) % step == 0;
      }
    } else if (step < 0) {
      if (min >= value && value >= max) {
        if (step == -1)
          return true;
        return (value - min) % step == 0;
      }
    }
    return false;
  }
}