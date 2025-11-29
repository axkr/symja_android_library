package org.matheclipse.core.expression;

import org.matheclipse.core.interfaces.IAST;

/**
 * Type flags for uniform {@link IAST} expressions.
 */
public final class UniformFlags {
  // Basic-Bits
  public static final int NONE = 0;
  public static final int ATOM = 1 << 0;
  public static final int NUMBER = 1 << 1;
  public static final int INTEGER = 1 << 2;
  public static final int FRACTION = 1 << 3;
  public static final int REAL = 1 << 4;
  public static final int COMPLEX = 1 << 5;
  public static final int SYMBOL = 1 << 6;
  public static final int STRING = 1 << 7;

  /**
   * Get the type mask for a &quot;head test&quot; in a pattern.
   * 
   * @param patternHead the &quot;head test&quot; of a pattern (especially pattern sequence)
   * @return the type mask which can be used in {@link IAST#isUniform(int)}
   */
  public static int uniformMask(BuiltInSymbol patternHead) {
    switch (patternHead.ordinal()) {
      case ID.Integer:
        // The pattern _Integer stands for a integer number
        return INTEGER;
      case ID.Rational:
        // The pattern _Rational stands for a fractional number not for a integer number
        return FRACTION;
      case ID.Real:
        // The pattern _Real stands for real floating-point numbers
        return REAL;
      case ID.Complex:
        // The pattern _Complex stands for complex exact and complex floating-point numbers
        return COMPLEX;
      case ID.String:
        // The pattern _String stands for any number
        return STRING;
      default:
        return NONE;
    }
  }
}
