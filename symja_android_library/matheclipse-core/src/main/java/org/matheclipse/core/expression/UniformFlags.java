package org.matheclipse.core.expression;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Type flags for uniform {@link IAST} expressions.
 */
public final class UniformFlags {
  /**
   * The type flags of the arguments were not determined yet. They are computed on demand the first
   * time {@link IAST#isUniform()}, {@link IAST#isUniform(int)} or {@link IAST#isUniformAny(int)} is
   * called.
   * <p>
   * <b>Note</b>: this value must never be used in a bitwise &quot;and&quot;-operation with the
   * flags of an argument, because all bits are set.
   */
  public static final int UNKNOWN = -1;

  // Basic-Bits
  public static final int NONE = 0;
  public static final int ATOM = 1 << 0;
  public static final int NUMBER = 1 << 1;
  /**
   * Small integer numbers {@link IntegerSym}
   */
  public static final int INT = 1 << 2;
  /**
   * Small integer numbers {@link IntegerSym} or {@link BigIntegerSym}
   */
  public static final int INTEGER = 1 << 3;
  public static final int FRACTION = 1 << 4;
  /**
   * Double numbers {@link Num}
   */
  public static final int DOUBLE = 1 << 5;
  /**
   * floating-point numbers {@link Num} or {@link ApfloatNum}
   */
  public static final int REAL = 1 << 6;
  /**
   * Double complex numbers {@link ComplexNum}
   */
  public static final int DOUBLECOMPLEX = 1 << 7;
  /**
   * Double complex numbers {@link ComplexNum}, {@link ApcomplexNum} or symbolic {@link ComplexSym}
   */
  public static final int COMPLEX = 1 << 8;
  public static final int SYMBOL = 1 << 9;
  public static final int STRING = 1 << 10;

  /**
   * Bit mask of all defined type flags. It's the neutral element of the bitwise
   * &quot;and&quot;-operation which accumulates the flags of the arguments.
   */
  public static final int ALL = (1 << 11) - 1;

  /**
   * Get the type mask of the arguments which can never be equal to the given <code>expr</code>.
   * <p>
   * A mask is only derived for the expressions which
   * {@link org.matheclipse.core.generic.Predicates#toFreeQ(IExpr)} compares with
   * {@link IExpr#equals(Object)} or with object identity, because only for those the argument type
   * alone decides that no match is possible. For every other expression (especially pattern
   * objects, which may match any type) {@link #NONE} is returned.
   *
   * @param expr the searched expression
   * @return the type mask which can be used in {@link IAST#isUniformAny(int)} or {@link #NONE} if
   *         no argument type can be excluded
   */
  public static int unequalMask(IExpr expr) {
    if (expr.isSymbol()) {
      // a number or a string is never identical to a symbol
      return NUMBER | STRING;
    }
    if (expr.isNumber()) {
      // a symbol or a string is never equal to a number
      return SYMBOL | STRING;
    }
    if (expr.isString()) {
      // a number or a symbol is never equal to a string
      return NUMBER | SYMBOL;
    }
    return NONE;
  }

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
