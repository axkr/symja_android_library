package org.matheclipse.core.expression;

import static org.matheclipse.core.expression.NumberUtil.hasIntValue;
import java.math.BigInteger;
import java.util.NoSuchElementException;
import java.util.function.DoubleFunction;
import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.FixedPrecisionApfloatHelper;
import org.hipparchus.exception.MathRuntimeException;
import org.hipparchus.fraction.BigFraction;
import org.hipparchus.util.ArithmeticUtils;
import org.hipparchus.util.FastMath;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.combinatoric.BinomialCache;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ASTElementLimitExceeded;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.exception.BigIntegerLimitExceeded;
import org.matheclipse.core.eval.exception.IterationLimitExceeded;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.IVisitor;
import org.matheclipse.core.visit.IVisitorBoolean;
import org.matheclipse.core.visit.IVisitorInt;
import org.matheclipse.core.visit.IVisitorLong;
import org.matheclipse.core.visit.RationalizeNumericsVisitor;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntRBTreeMap;

/**
 * Abstract base class for FractionSym and BigFractionSym
 *
 * @see FractionSym
 * @see BigFractionSym
 */
public abstract class AbstractFractionSym implements IFraction {

  private static final long serialVersionUID = -8743141041586314213L;

  static final int LOW_NUMER = -32;
  static final int HIGH_NUMER = 32;
  static final int LOW_DENOM = 2;
  static final int HIGH_DENOM = 32;

  static final FractionSym[][] CACHE = new FractionSym[][] {
      {new FractionSym(-16, 1), new FractionSym(-32, 3), new FractionSym(-8, 1),
          new FractionSym(-32, 5), new FractionSym(-16, 3), new FractionSym(-32, 7),
          new FractionSym(-4, 1), new FractionSym(-32, 9), new FractionSym(-16, 5),
          new FractionSym(-32, 11), new FractionSym(-8, 3), new FractionSym(-32, 13),
          new FractionSym(-16, 7), new FractionSym(-32, 15), new FractionSym(-2, 1),
          new FractionSym(-32, 17), new FractionSym(-16, 9), new FractionSym(-32, 19),
          new FractionSym(-8, 5), new FractionSym(-32, 21), new FractionSym(-16, 11),
          new FractionSym(-32, 23), new FractionSym(-4, 3), new FractionSym(-32, 25),
          new FractionSym(-16, 13), new FractionSym(-32, 27), new FractionSym(-8, 7),
          new FractionSym(-32, 29), new FractionSym(-16, 15), new FractionSym(-32, 31),
          new FractionSym(-1, 1)}, //
      {new FractionSym(-31, 2), new FractionSym(-31, 3), new FractionSym(-31, 4),
          new FractionSym(-31, 5), new FractionSym(-31, 6), new FractionSym(-31, 7),
          new FractionSym(-31, 8), new FractionSym(-31, 9), new FractionSym(-31, 10),
          new FractionSym(-31, 11), new FractionSym(-31, 12), new FractionSym(-31, 13),
          new FractionSym(-31, 14), new FractionSym(-31, 15), new FractionSym(-31, 16),
          new FractionSym(-31, 17), new FractionSym(-31, 18), new FractionSym(-31, 19),
          new FractionSym(-31, 20), new FractionSym(-31, 21), new FractionSym(-31, 22),
          new FractionSym(-31, 23), new FractionSym(-31, 24), new FractionSym(-31, 25),
          new FractionSym(-31, 26), new FractionSym(-31, 27), new FractionSym(-31, 28),
          new FractionSym(-31, 29), new FractionSym(-31, 30), new FractionSym(-1, 1),
          new FractionSym(-31, 32)}, //
      {new FractionSym(-15, 1), new FractionSym(-10, 1), new FractionSym(-15, 2),
          new FractionSym(-6, 1), new FractionSym(-5, 1), new FractionSym(-30, 7),
          new FractionSym(-15, 4), new FractionSym(-10, 3), new FractionSym(-3, 1),
          new FractionSym(-30, 11), new FractionSym(-5, 2), new FractionSym(-30, 13),
          new FractionSym(-15, 7), new FractionSym(-2, 1), new FractionSym(-15, 8),
          new FractionSym(-30, 17), new FractionSym(-5, 3), new FractionSym(-30, 19),
          new FractionSym(-3, 2), new FractionSym(-10, 7), new FractionSym(-15, 11),
          new FractionSym(-30, 23), new FractionSym(-5, 4), new FractionSym(-6, 5),
          new FractionSym(-15, 13), new FractionSym(-10, 9), new FractionSym(-15, 14),
          new FractionSym(-30, 29), new FractionSym(-1, 1), new FractionSym(-30, 31),
          new FractionSym(-15, 16)}, //
      {new FractionSym(-29, 2), new FractionSym(-29, 3), new FractionSym(-29, 4),
          new FractionSym(-29, 5), new FractionSym(-29, 6), new FractionSym(-29, 7),
          new FractionSym(-29, 8), new FractionSym(-29, 9), new FractionSym(-29, 10),
          new FractionSym(-29, 11), new FractionSym(-29, 12), new FractionSym(-29, 13),
          new FractionSym(-29, 14), new FractionSym(-29, 15), new FractionSym(-29, 16),
          new FractionSym(-29, 17), new FractionSym(-29, 18), new FractionSym(-29, 19),
          new FractionSym(-29, 20), new FractionSym(-29, 21), new FractionSym(-29, 22),
          new FractionSym(-29, 23), new FractionSym(-29, 24), new FractionSym(-29, 25),
          new FractionSym(-29, 26), new FractionSym(-29, 27), new FractionSym(-29, 28),
          new FractionSym(-1, 1), new FractionSym(-29, 30), new FractionSym(-29, 31),
          new FractionSym(-29, 32)}, //
      {new FractionSym(-14, 1), new FractionSym(-28, 3), new FractionSym(-7, 1),
          new FractionSym(-28, 5), new FractionSym(-14, 3), new FractionSym(-4, 1),
          new FractionSym(-7, 2), new FractionSym(-28, 9), new FractionSym(-14, 5),
          new FractionSym(-28, 11), new FractionSym(-7, 3), new FractionSym(-28, 13),
          new FractionSym(-2, 1), new FractionSym(-28, 15), new FractionSym(-7, 4),
          new FractionSym(-28, 17), new FractionSym(-14, 9), new FractionSym(-28, 19),
          new FractionSym(-7, 5), new FractionSym(-4, 3), new FractionSym(-14, 11),
          new FractionSym(-28, 23), new FractionSym(-7, 6), new FractionSym(-28, 25),
          new FractionSym(-14, 13), new FractionSym(-28, 27), new FractionSym(-1, 1),
          new FractionSym(-28, 29), new FractionSym(-14, 15), new FractionSym(-28, 31),
          new FractionSym(-7, 8)}, //
      {new FractionSym(-27, 2), new FractionSym(-9, 1), new FractionSym(-27, 4),
          new FractionSym(-27, 5), new FractionSym(-9, 2), new FractionSym(-27, 7),
          new FractionSym(-27, 8), new FractionSym(-3, 1), new FractionSym(-27, 10),
          new FractionSym(-27, 11), new FractionSym(-9, 4), new FractionSym(-27, 13),
          new FractionSym(-27, 14), new FractionSym(-9, 5), new FractionSym(-27, 16),
          new FractionSym(-27, 17), new FractionSym(-3, 2), new FractionSym(-27, 19),
          new FractionSym(-27, 20), new FractionSym(-9, 7), new FractionSym(-27, 22),
          new FractionSym(-27, 23), new FractionSym(-9, 8), new FractionSym(-27, 25),
          new FractionSym(-27, 26), new FractionSym(-1, 1), new FractionSym(-27, 28),
          new FractionSym(-27, 29), new FractionSym(-9, 10), new FractionSym(-27, 31),
          new FractionSym(-27, 32)}, //
      {new FractionSym(-13, 1), new FractionSym(-26, 3), new FractionSym(-13, 2),
          new FractionSym(-26, 5), new FractionSym(-13, 3), new FractionSym(-26, 7),
          new FractionSym(-13, 4), new FractionSym(-26, 9), new FractionSym(-13, 5),
          new FractionSym(-26, 11), new FractionSym(-13, 6), new FractionSym(-2, 1),
          new FractionSym(-13, 7), new FractionSym(-26, 15), new FractionSym(-13, 8),
          new FractionSym(-26, 17), new FractionSym(-13, 9), new FractionSym(-26, 19),
          new FractionSym(-13, 10), new FractionSym(-26, 21), new FractionSym(-13, 11),
          new FractionSym(-26, 23), new FractionSym(-13, 12), new FractionSym(-26, 25),
          new FractionSym(-1, 1), new FractionSym(-26, 27), new FractionSym(-13, 14),
          new FractionSym(-26, 29), new FractionSym(-13, 15), new FractionSym(-26, 31),
          new FractionSym(-13, 16)}, //
      {new FractionSym(-25, 2), new FractionSym(-25, 3), new FractionSym(-25, 4),
          new FractionSym(-5, 1), new FractionSym(-25, 6), new FractionSym(-25, 7),
          new FractionSym(-25, 8), new FractionSym(-25, 9), new FractionSym(-5, 2),
          new FractionSym(-25, 11), new FractionSym(-25, 12), new FractionSym(-25, 13),
          new FractionSym(-25, 14), new FractionSym(-5, 3), new FractionSym(-25, 16),
          new FractionSym(-25, 17), new FractionSym(-25, 18), new FractionSym(-25, 19),
          new FractionSym(-5, 4), new FractionSym(-25, 21), new FractionSym(-25, 22),
          new FractionSym(-25, 23), new FractionSym(-25, 24), new FractionSym(-1, 1),
          new FractionSym(-25, 26), new FractionSym(-25, 27), new FractionSym(-25, 28),
          new FractionSym(-25, 29), new FractionSym(-5, 6), new FractionSym(-25, 31),
          new FractionSym(-25, 32)}, //
      {new FractionSym(-12, 1), new FractionSym(-8, 1), new FractionSym(-6, 1),
          new FractionSym(-24, 5), new FractionSym(-4, 1), new FractionSym(-24, 7),
          new FractionSym(-3, 1), new FractionSym(-8, 3), new FractionSym(-12, 5),
          new FractionSym(-24, 11), new FractionSym(-2, 1), new FractionSym(-24, 13),
          new FractionSym(-12, 7), new FractionSym(-8, 5), new FractionSym(-3, 2),
          new FractionSym(-24, 17), new FractionSym(-4, 3), new FractionSym(-24, 19),
          new FractionSym(-6, 5), new FractionSym(-8, 7), new FractionSym(-12, 11),
          new FractionSym(-24, 23), new FractionSym(-1, 1), new FractionSym(-24, 25),
          new FractionSym(-12, 13), new FractionSym(-8, 9), new FractionSym(-6, 7),
          new FractionSym(-24, 29), new FractionSym(-4, 5), new FractionSym(-24, 31),
          new FractionSym(-3, 4)}, //
      {new FractionSym(-23, 2), new FractionSym(-23, 3), new FractionSym(-23, 4),
          new FractionSym(-23, 5), new FractionSym(-23, 6), new FractionSym(-23, 7),
          new FractionSym(-23, 8), new FractionSym(-23, 9), new FractionSym(-23, 10),
          new FractionSym(-23, 11), new FractionSym(-23, 12), new FractionSym(-23, 13),
          new FractionSym(-23, 14), new FractionSym(-23, 15), new FractionSym(-23, 16),
          new FractionSym(-23, 17), new FractionSym(-23, 18), new FractionSym(-23, 19),
          new FractionSym(-23, 20), new FractionSym(-23, 21), new FractionSym(-23, 22),
          new FractionSym(-1, 1), new FractionSym(-23, 24), new FractionSym(-23, 25),
          new FractionSym(-23, 26), new FractionSym(-23, 27), new FractionSym(-23, 28),
          new FractionSym(-23, 29), new FractionSym(-23, 30), new FractionSym(-23, 31),
          new FractionSym(-23, 32)}, //
      {new FractionSym(-11, 1), new FractionSym(-22, 3), new FractionSym(-11, 2),
          new FractionSym(-22, 5), new FractionSym(-11, 3), new FractionSym(-22, 7),
          new FractionSym(-11, 4), new FractionSym(-22, 9), new FractionSym(-11, 5),
          new FractionSym(-2, 1), new FractionSym(-11, 6), new FractionSym(-22, 13),
          new FractionSym(-11, 7), new FractionSym(-22, 15), new FractionSym(-11, 8),
          new FractionSym(-22, 17), new FractionSym(-11, 9), new FractionSym(-22, 19),
          new FractionSym(-11, 10), new FractionSym(-22, 21), new FractionSym(-1, 1),
          new FractionSym(-22, 23), new FractionSym(-11, 12), new FractionSym(-22, 25),
          new FractionSym(-11, 13), new FractionSym(-22, 27), new FractionSym(-11, 14),
          new FractionSym(-22, 29), new FractionSym(-11, 15), new FractionSym(-22, 31),
          new FractionSym(-11, 16)}, //
      {new FractionSym(-21, 2), new FractionSym(-7, 1), new FractionSym(-21, 4),
          new FractionSym(-21, 5), new FractionSym(-7, 2), new FractionSym(-3, 1),
          new FractionSym(-21, 8), new FractionSym(-7, 3), new FractionSym(-21, 10),
          new FractionSym(-21, 11), new FractionSym(-7, 4), new FractionSym(-21, 13),
          new FractionSym(-3, 2), new FractionSym(-7, 5), new FractionSym(-21, 16),
          new FractionSym(-21, 17), new FractionSym(-7, 6), new FractionSym(-21, 19),
          new FractionSym(-21, 20), new FractionSym(-1, 1), new FractionSym(-21, 22),
          new FractionSym(-21, 23), new FractionSym(-7, 8), new FractionSym(-21, 25),
          new FractionSym(-21, 26), new FractionSym(-7, 9), new FractionSym(-3, 4),
          new FractionSym(-21, 29), new FractionSym(-7, 10), new FractionSym(-21, 31),
          new FractionSym(-21, 32)}, //
      {new FractionSym(-10, 1), new FractionSym(-20, 3), new FractionSym(-5, 1),
          new FractionSym(-4, 1), new FractionSym(-10, 3), new FractionSym(-20, 7),
          new FractionSym(-5, 2), new FractionSym(-20, 9), new FractionSym(-2, 1),
          new FractionSym(-20, 11), new FractionSym(-5, 3), new FractionSym(-20, 13),
          new FractionSym(-10, 7), new FractionSym(-4, 3), new FractionSym(-5, 4),
          new FractionSym(-20, 17), new FractionSym(-10, 9), new FractionSym(-20, 19),
          new FractionSym(-1, 1), new FractionSym(-20, 21), new FractionSym(-10, 11),
          new FractionSym(-20, 23), new FractionSym(-5, 6), new FractionSym(-4, 5),
          new FractionSym(-10, 13), new FractionSym(-20, 27), new FractionSym(-5, 7),
          new FractionSym(-20, 29), new FractionSym(-2, 3), new FractionSym(-20, 31),
          new FractionSym(-5, 8)}, //
      {new FractionSym(-19, 2), new FractionSym(-19, 3), new FractionSym(-19, 4),
          new FractionSym(-19, 5), new FractionSym(-19, 6), new FractionSym(-19, 7),
          new FractionSym(-19, 8), new FractionSym(-19, 9), new FractionSym(-19, 10),
          new FractionSym(-19, 11), new FractionSym(-19, 12), new FractionSym(-19, 13),
          new FractionSym(-19, 14), new FractionSym(-19, 15), new FractionSym(-19, 16),
          new FractionSym(-19, 17), new FractionSym(-19, 18), new FractionSym(-1, 1),
          new FractionSym(-19, 20), new FractionSym(-19, 21), new FractionSym(-19, 22),
          new FractionSym(-19, 23), new FractionSym(-19, 24), new FractionSym(-19, 25),
          new FractionSym(-19, 26), new FractionSym(-19, 27), new FractionSym(-19, 28),
          new FractionSym(-19, 29), new FractionSym(-19, 30), new FractionSym(-19, 31),
          new FractionSym(-19, 32)}, //
      {new FractionSym(-9, 1), new FractionSym(-6, 1), new FractionSym(-9, 2),
          new FractionSym(-18, 5), new FractionSym(-3, 1), new FractionSym(-18, 7),
          new FractionSym(-9, 4), new FractionSym(-2, 1), new FractionSym(-9, 5),
          new FractionSym(-18, 11), new FractionSym(-3, 2), new FractionSym(-18, 13),
          new FractionSym(-9, 7), new FractionSym(-6, 5), new FractionSym(-9, 8),
          new FractionSym(-18, 17), new FractionSym(-1, 1), new FractionSym(-18, 19),
          new FractionSym(-9, 10), new FractionSym(-6, 7), new FractionSym(-9, 11),
          new FractionSym(-18, 23), new FractionSym(-3, 4), new FractionSym(-18, 25),
          new FractionSym(-9, 13), new FractionSym(-2, 3), new FractionSym(-9, 14),
          new FractionSym(-18, 29), new FractionSym(-3, 5), new FractionSym(-18, 31),
          new FractionSym(-9, 16)}, //
      {new FractionSym(-17, 2), new FractionSym(-17, 3), new FractionSym(-17, 4),
          new FractionSym(-17, 5), new FractionSym(-17, 6), new FractionSym(-17, 7),
          new FractionSym(-17, 8), new FractionSym(-17, 9), new FractionSym(-17, 10),
          new FractionSym(-17, 11), new FractionSym(-17, 12), new FractionSym(-17, 13),
          new FractionSym(-17, 14), new FractionSym(-17, 15), new FractionSym(-17, 16),
          new FractionSym(-1, 1), new FractionSym(-17, 18), new FractionSym(-17, 19),
          new FractionSym(-17, 20), new FractionSym(-17, 21), new FractionSym(-17, 22),
          new FractionSym(-17, 23), new FractionSym(-17, 24), new FractionSym(-17, 25),
          new FractionSym(-17, 26), new FractionSym(-17, 27), new FractionSym(-17, 28),
          new FractionSym(-17, 29), new FractionSym(-17, 30), new FractionSym(-17, 31),
          new FractionSym(-17, 32)}, //
      {new FractionSym(-8, 1), new FractionSym(-16, 3), new FractionSym(-4, 1),
          new FractionSym(-16, 5), new FractionSym(-8, 3), new FractionSym(-16, 7),
          new FractionSym(-2, 1), new FractionSym(-16, 9), new FractionSym(-8, 5),
          new FractionSym(-16, 11), new FractionSym(-4, 3), new FractionSym(-16, 13),
          new FractionSym(-8, 7), new FractionSym(-16, 15), new FractionSym(-1, 1),
          new FractionSym(-16, 17), new FractionSym(-8, 9), new FractionSym(-16, 19),
          new FractionSym(-4, 5), new FractionSym(-16, 21), new FractionSym(-8, 11),
          new FractionSym(-16, 23), new FractionSym(-2, 3), new FractionSym(-16, 25),
          new FractionSym(-8, 13), new FractionSym(-16, 27), new FractionSym(-4, 7),
          new FractionSym(-16, 29), new FractionSym(-8, 15), new FractionSym(-16, 31),
          new FractionSym(-1, 2)}, //
      {new FractionSym(-15, 2), new FractionSym(-5, 1), new FractionSym(-15, 4),
          new FractionSym(-3, 1), new FractionSym(-5, 2), new FractionSym(-15, 7),
          new FractionSym(-15, 8), new FractionSym(-5, 3), new FractionSym(-3, 2),
          new FractionSym(-15, 11), new FractionSym(-5, 4), new FractionSym(-15, 13),
          new FractionSym(-15, 14), new FractionSym(-1, 1), new FractionSym(-15, 16),
          new FractionSym(-15, 17), new FractionSym(-5, 6), new FractionSym(-15, 19),
          new FractionSym(-3, 4), new FractionSym(-5, 7), new FractionSym(-15, 22),
          new FractionSym(-15, 23), new FractionSym(-5, 8), new FractionSym(-3, 5),
          new FractionSym(-15, 26), new FractionSym(-5, 9), new FractionSym(-15, 28),
          new FractionSym(-15, 29), new FractionSym(-1, 2), new FractionSym(-15, 31),
          new FractionSym(-15, 32)}, //
      {new FractionSym(-7, 1), new FractionSym(-14, 3), new FractionSym(-7, 2),
          new FractionSym(-14, 5), new FractionSym(-7, 3), new FractionSym(-2, 1),
          new FractionSym(-7, 4), new FractionSym(-14, 9), new FractionSym(-7, 5),
          new FractionSym(-14, 11), new FractionSym(-7, 6), new FractionSym(-14, 13),
          new FractionSym(-1, 1), new FractionSym(-14, 15), new FractionSym(-7, 8),
          new FractionSym(-14, 17), new FractionSym(-7, 9), new FractionSym(-14, 19),
          new FractionSym(-7, 10), new FractionSym(-2, 3), new FractionSym(-7, 11),
          new FractionSym(-14, 23), new FractionSym(-7, 12), new FractionSym(-14, 25),
          new FractionSym(-7, 13), new FractionSym(-14, 27), new FractionSym(-1, 2),
          new FractionSym(-14, 29), new FractionSym(-7, 15), new FractionSym(-14, 31),
          new FractionSym(-7, 16)}, //
      {new FractionSym(-13, 2), new FractionSym(-13, 3), new FractionSym(-13, 4),
          new FractionSym(-13, 5), new FractionSym(-13, 6), new FractionSym(-13, 7),
          new FractionSym(-13, 8), new FractionSym(-13, 9), new FractionSym(-13, 10),
          new FractionSym(-13, 11), new FractionSym(-13, 12), new FractionSym(-1, 1),
          new FractionSym(-13, 14), new FractionSym(-13, 15), new FractionSym(-13, 16),
          new FractionSym(-13, 17), new FractionSym(-13, 18), new FractionSym(-13, 19),
          new FractionSym(-13, 20), new FractionSym(-13, 21), new FractionSym(-13, 22),
          new FractionSym(-13, 23), new FractionSym(-13, 24), new FractionSym(-13, 25),
          new FractionSym(-1, 2), new FractionSym(-13, 27), new FractionSym(-13, 28),
          new FractionSym(-13, 29), new FractionSym(-13, 30), new FractionSym(-13, 31),
          new FractionSym(-13, 32)}, //
      {new FractionSym(-6, 1), new FractionSym(-4, 1), new FractionSym(-3, 1),
          new FractionSym(-12, 5), new FractionSym(-2, 1), new FractionSym(-12, 7),
          new FractionSym(-3, 2), new FractionSym(-4, 3), new FractionSym(-6, 5),
          new FractionSym(-12, 11), new FractionSym(-1, 1), new FractionSym(-12, 13),
          new FractionSym(-6, 7), new FractionSym(-4, 5), new FractionSym(-3, 4),
          new FractionSym(-12, 17), new FractionSym(-2, 3), new FractionSym(-12, 19),
          new FractionSym(-3, 5), new FractionSym(-4, 7), new FractionSym(-6, 11),
          new FractionSym(-12, 23), new FractionSym(-1, 2), new FractionSym(-12, 25),
          new FractionSym(-6, 13), new FractionSym(-4, 9), new FractionSym(-3, 7),
          new FractionSym(-12, 29), new FractionSym(-2, 5), new FractionSym(-12, 31),
          new FractionSym(-3, 8)}, //
      {new FractionSym(-11, 2), new FractionSym(-11, 3), new FractionSym(-11, 4),
          new FractionSym(-11, 5), new FractionSym(-11, 6), new FractionSym(-11, 7),
          new FractionSym(-11, 8), new FractionSym(-11, 9), new FractionSym(-11, 10),
          new FractionSym(-1, 1), new FractionSym(-11, 12), new FractionSym(-11, 13),
          new FractionSym(-11, 14), new FractionSym(-11, 15), new FractionSym(-11, 16),
          new FractionSym(-11, 17), new FractionSym(-11, 18), new FractionSym(-11, 19),
          new FractionSym(-11, 20), new FractionSym(-11, 21), new FractionSym(-1, 2),
          new FractionSym(-11, 23), new FractionSym(-11, 24), new FractionSym(-11, 25),
          new FractionSym(-11, 26), new FractionSym(-11, 27), new FractionSym(-11, 28),
          new FractionSym(-11, 29), new FractionSym(-11, 30), new FractionSym(-11, 31),
          new FractionSym(-11, 32)}, //
      {new FractionSym(-5, 1), new FractionSym(-10, 3), new FractionSym(-5, 2),
          new FractionSym(-2, 1), new FractionSym(-5, 3), new FractionSym(-10, 7),
          new FractionSym(-5, 4), new FractionSym(-10, 9), new FractionSym(-1, 1),
          new FractionSym(-10, 11), new FractionSym(-5, 6), new FractionSym(-10, 13),
          new FractionSym(-5, 7), new FractionSym(-2, 3), new FractionSym(-5, 8),
          new FractionSym(-10, 17), new FractionSym(-5, 9), new FractionSym(-10, 19),
          new FractionSym(-1, 2), new FractionSym(-10, 21), new FractionSym(-5, 11),
          new FractionSym(-10, 23), new FractionSym(-5, 12), new FractionSym(-2, 5),
          new FractionSym(-5, 13), new FractionSym(-10, 27), new FractionSym(-5, 14),
          new FractionSym(-10, 29), new FractionSym(-1, 3), new FractionSym(-10, 31),
          new FractionSym(-5, 16)}, //
      {new FractionSym(-9, 2), new FractionSym(-3, 1), new FractionSym(-9, 4),
          new FractionSym(-9, 5), new FractionSym(-3, 2), new FractionSym(-9, 7),
          new FractionSym(-9, 8), new FractionSym(-1, 1), new FractionSym(-9, 10),
          new FractionSym(-9, 11), new FractionSym(-3, 4), new FractionSym(-9, 13),
          new FractionSym(-9, 14), new FractionSym(-3, 5), new FractionSym(-9, 16),
          new FractionSym(-9, 17), new FractionSym(-1, 2), new FractionSym(-9, 19),
          new FractionSym(-9, 20), new FractionSym(-3, 7), new FractionSym(-9, 22),
          new FractionSym(-9, 23), new FractionSym(-3, 8), new FractionSym(-9, 25),
          new FractionSym(-9, 26), new FractionSym(-1, 3), new FractionSym(-9, 28),
          new FractionSym(-9, 29), new FractionSym(-3, 10), new FractionSym(-9, 31),
          new FractionSym(-9, 32)}, //
      {new FractionSym(-4, 1), new FractionSym(-8, 3), new FractionSym(-2, 1),
          new FractionSym(-8, 5), new FractionSym(-4, 3), new FractionSym(-8, 7),
          new FractionSym(-1, 1), new FractionSym(-8, 9), new FractionSym(-4, 5),
          new FractionSym(-8, 11), new FractionSym(-2, 3), new FractionSym(-8, 13),
          new FractionSym(-4, 7), new FractionSym(-8, 15), new FractionSym(-1, 2),
          new FractionSym(-8, 17), new FractionSym(-4, 9), new FractionSym(-8, 19),
          new FractionSym(-2, 5), new FractionSym(-8, 21), new FractionSym(-4, 11),
          new FractionSym(-8, 23), new FractionSym(-1, 3), new FractionSym(-8, 25),
          new FractionSym(-4, 13), new FractionSym(-8, 27), new FractionSym(-2, 7),
          new FractionSym(-8, 29), new FractionSym(-4, 15), new FractionSym(-8, 31),
          new FractionSym(-1, 4)}, //
      {new FractionSym(-7, 2), new FractionSym(-7, 3), new FractionSym(-7, 4),
          new FractionSym(-7, 5), new FractionSym(-7, 6), new FractionSym(-1, 1),
          new FractionSym(-7, 8), new FractionSym(-7, 9), new FractionSym(-7, 10),
          new FractionSym(-7, 11), new FractionSym(-7, 12), new FractionSym(-7, 13),
          new FractionSym(-1, 2), new FractionSym(-7, 15), new FractionSym(-7, 16),
          new FractionSym(-7, 17), new FractionSym(-7, 18), new FractionSym(-7, 19),
          new FractionSym(-7, 20), new FractionSym(-1, 3), new FractionSym(-7, 22),
          new FractionSym(-7, 23), new FractionSym(-7, 24), new FractionSym(-7, 25),
          new FractionSym(-7, 26), new FractionSym(-7, 27), new FractionSym(-1, 4),
          new FractionSym(-7, 29), new FractionSym(-7, 30), new FractionSym(-7, 31),
          new FractionSym(-7, 32)}, //
      {new FractionSym(-3, 1), new FractionSym(-2, 1), new FractionSym(-3, 2),
          new FractionSym(-6, 5), new FractionSym(-1, 1), new FractionSym(-6, 7),
          new FractionSym(-3, 4), new FractionSym(-2, 3), new FractionSym(-3, 5),
          new FractionSym(-6, 11), new FractionSym(-1, 2), new FractionSym(-6, 13),
          new FractionSym(-3, 7), new FractionSym(-2, 5), new FractionSym(-3, 8),
          new FractionSym(-6, 17), new FractionSym(-1, 3), new FractionSym(-6, 19),
          new FractionSym(-3, 10), new FractionSym(-2, 7), new FractionSym(-3, 11),
          new FractionSym(-6, 23), new FractionSym(-1, 4), new FractionSym(-6, 25),
          new FractionSym(-3, 13), new FractionSym(-2, 9), new FractionSym(-3, 14),
          new FractionSym(-6, 29), new FractionSym(-1, 5), new FractionSym(-6, 31),
          new FractionSym(-3, 16)}, //
      {new FractionSym(-5, 2), new FractionSym(-5, 3), new FractionSym(-5, 4),
          new FractionSym(-1, 1), new FractionSym(-5, 6), new FractionSym(-5, 7),
          new FractionSym(-5, 8), new FractionSym(-5, 9), new FractionSym(-1, 2),
          new FractionSym(-5, 11), new FractionSym(-5, 12), new FractionSym(-5, 13),
          new FractionSym(-5, 14), new FractionSym(-1, 3), new FractionSym(-5, 16),
          new FractionSym(-5, 17), new FractionSym(-5, 18), new FractionSym(-5, 19),
          new FractionSym(-1, 4), new FractionSym(-5, 21), new FractionSym(-5, 22),
          new FractionSym(-5, 23), new FractionSym(-5, 24), new FractionSym(-1, 5),
          new FractionSym(-5, 26), new FractionSym(-5, 27), new FractionSym(-5, 28),
          new FractionSym(-5, 29), new FractionSym(-1, 6), new FractionSym(-5, 31),
          new FractionSym(-5, 32)}, //
      {new FractionSym(-2, 1), new FractionSym(-4, 3), new FractionSym(-1, 1),
          new FractionSym(-4, 5), new FractionSym(-2, 3), new FractionSym(-4, 7),
          new FractionSym(-1, 2), new FractionSym(-4, 9), new FractionSym(-2, 5),
          new FractionSym(-4, 11), new FractionSym(-1, 3), new FractionSym(-4, 13),
          new FractionSym(-2, 7), new FractionSym(-4, 15), new FractionSym(-1, 4),
          new FractionSym(-4, 17), new FractionSym(-2, 9), new FractionSym(-4, 19),
          new FractionSym(-1, 5), new FractionSym(-4, 21), new FractionSym(-2, 11),
          new FractionSym(-4, 23), new FractionSym(-1, 6), new FractionSym(-4, 25),
          new FractionSym(-2, 13), new FractionSym(-4, 27), new FractionSym(-1, 7),
          new FractionSym(-4, 29), new FractionSym(-2, 15), new FractionSym(-4, 31),
          new FractionSym(-1, 8)}, //
      {new FractionSym(-3, 2), new FractionSym(-1, 1), new FractionSym(-3, 4),
          new FractionSym(-3, 5), new FractionSym(-1, 2), new FractionSym(-3, 7),
          new FractionSym(-3, 8), new FractionSym(-1, 3), new FractionSym(-3, 10),
          new FractionSym(-3, 11), new FractionSym(-1, 4), new FractionSym(-3, 13),
          new FractionSym(-3, 14), new FractionSym(-1, 5), new FractionSym(-3, 16),
          new FractionSym(-3, 17), new FractionSym(-1, 6), new FractionSym(-3, 19),
          new FractionSym(-3, 20), new FractionSym(-1, 7), new FractionSym(-3, 22),
          new FractionSym(-3, 23), new FractionSym(-1, 8), new FractionSym(-3, 25),
          new FractionSym(-3, 26), new FractionSym(-1, 9), new FractionSym(-3, 28),
          new FractionSym(-3, 29), new FractionSym(-1, 10), new FractionSym(-3, 31),
          new FractionSym(-3, 32)}, //
      {new FractionSym(-1, 1), new FractionSym(-2, 3), new FractionSym(-1, 2),
          new FractionSym(-2, 5), new FractionSym(-1, 3), new FractionSym(-2, 7),
          new FractionSym(-1, 4), new FractionSym(-2, 9), new FractionSym(-1, 5),
          new FractionSym(-2, 11), new FractionSym(-1, 6), new FractionSym(-2, 13),
          new FractionSym(-1, 7), new FractionSym(-2, 15), new FractionSym(-1, 8),
          new FractionSym(-2, 17), new FractionSym(-1, 9), new FractionSym(-2, 19),
          new FractionSym(-1, 10), new FractionSym(-2, 21), new FractionSym(-1, 11),
          new FractionSym(-2, 23), new FractionSym(-1, 12), new FractionSym(-2, 25),
          new FractionSym(-1, 13), new FractionSym(-2, 27), new FractionSym(-1, 14),
          new FractionSym(-2, 29), new FractionSym(-1, 15), new FractionSym(-2, 31),
          new FractionSym(-1, 16)}, //
      {new FractionSym(-1, 2), new FractionSym(-1, 3), new FractionSym(-1, 4),
          new FractionSym(-1, 5), new FractionSym(-1, 6), new FractionSym(-1, 7),
          new FractionSym(-1, 8), new FractionSym(-1, 9), new FractionSym(-1, 10),
          new FractionSym(-1, 11), new FractionSym(-1, 12), new FractionSym(-1, 13),
          new FractionSym(-1, 14), new FractionSym(-1, 15), new FractionSym(-1, 16),
          new FractionSym(-1, 17), new FractionSym(-1, 18), new FractionSym(-1, 19),
          new FractionSym(-1, 20), new FractionSym(-1, 21), new FractionSym(-1, 22),
          new FractionSym(-1, 23), new FractionSym(-1, 24), new FractionSym(-1, 25),
          new FractionSym(-1, 26), new FractionSym(-1, 27), new FractionSym(-1, 28),
          new FractionSym(-1, 29), new FractionSym(-1, 30), new FractionSym(-1, 31),
          new FractionSym(-1, 32)}, //
      {new FractionSym(0, 1), new FractionSym(0, 1), new FractionSym(0, 1), new FractionSym(0, 1),
          new FractionSym(0, 1), new FractionSym(0, 1), new FractionSym(0, 1),
          new FractionSym(0, 1), new FractionSym(0, 1), new FractionSym(0, 1),
          new FractionSym(0, 1), new FractionSym(0, 1), new FractionSym(0, 1),
          new FractionSym(0, 1), new FractionSym(0, 1), new FractionSym(0, 1),
          new FractionSym(0, 1), new FractionSym(0, 1), new FractionSym(0, 1),
          new FractionSym(0, 1), new FractionSym(0, 1), new FractionSym(0, 1),
          new FractionSym(0, 1), new FractionSym(0, 1), new FractionSym(0, 1),
          new FractionSym(0, 1), new FractionSym(0, 1), new FractionSym(0, 1),
          new FractionSym(0, 1), new FractionSym(0, 1), new FractionSym(0, 1)}, //
      {new FractionSym(1, 2), new FractionSym(1, 3), new FractionSym(1, 4), new FractionSym(1, 5),
          new FractionSym(1, 6), new FractionSym(1, 7), new FractionSym(1, 8),
          new FractionSym(1, 9), new FractionSym(1, 10), new FractionSym(1, 11),
          new FractionSym(1, 12), new FractionSym(1, 13), new FractionSym(1, 14),
          new FractionSym(1, 15), new FractionSym(1, 16), new FractionSym(1, 17),
          new FractionSym(1, 18), new FractionSym(1, 19), new FractionSym(1, 20),
          new FractionSym(1, 21), new FractionSym(1, 22), new FractionSym(1, 23),
          new FractionSym(1, 24), new FractionSym(1, 25), new FractionSym(1, 26),
          new FractionSym(1, 27), new FractionSym(1, 28), new FractionSym(1, 29),
          new FractionSym(1, 30), new FractionSym(1, 31), new FractionSym(1, 32)}, //
      {new FractionSym(1, 1), new FractionSym(2, 3), new FractionSym(1, 2), new FractionSym(2, 5),
          new FractionSym(1, 3), new FractionSym(2, 7), new FractionSym(1, 4),
          new FractionSym(2, 9), new FractionSym(1, 5), new FractionSym(2, 11),
          new FractionSym(1, 6), new FractionSym(2, 13), new FractionSym(1, 7),
          new FractionSym(2, 15), new FractionSym(1, 8), new FractionSym(2, 17),
          new FractionSym(1, 9), new FractionSym(2, 19), new FractionSym(1, 10),
          new FractionSym(2, 21), new FractionSym(1, 11), new FractionSym(2, 23),
          new FractionSym(1, 12), new FractionSym(2, 25), new FractionSym(1, 13),
          new FractionSym(2, 27), new FractionSym(1, 14), new FractionSym(2, 29),
          new FractionSym(1, 15), new FractionSym(2, 31), new FractionSym(1, 16)}, //
      {new FractionSym(3, 2), new FractionSym(1, 1), new FractionSym(3, 4), new FractionSym(3, 5),
          new FractionSym(1, 2), new FractionSym(3, 7), new FractionSym(3, 8),
          new FractionSym(1, 3), new FractionSym(3, 10), new FractionSym(3, 11),
          new FractionSym(1, 4), new FractionSym(3, 13), new FractionSym(3, 14),
          new FractionSym(1, 5), new FractionSym(3, 16), new FractionSym(3, 17),
          new FractionSym(1, 6), new FractionSym(3, 19), new FractionSym(3, 20),
          new FractionSym(1, 7), new FractionSym(3, 22), new FractionSym(3, 23),
          new FractionSym(1, 8), new FractionSym(3, 25), new FractionSym(3, 26),
          new FractionSym(1, 9), new FractionSym(3, 28), new FractionSym(3, 29),
          new FractionSym(1, 10), new FractionSym(3, 31), new FractionSym(3, 32)}, //
      {new FractionSym(2, 1), new FractionSym(4, 3), new FractionSym(1, 1), new FractionSym(4, 5),
          new FractionSym(2, 3), new FractionSym(4, 7), new FractionSym(1, 2),
          new FractionSym(4, 9), new FractionSym(2, 5), new FractionSym(4, 11),
          new FractionSym(1, 3), new FractionSym(4, 13), new FractionSym(2, 7),
          new FractionSym(4, 15), new FractionSym(1, 4), new FractionSym(4, 17),
          new FractionSym(2, 9), new FractionSym(4, 19), new FractionSym(1, 5),
          new FractionSym(4, 21), new FractionSym(2, 11), new FractionSym(4, 23),
          new FractionSym(1, 6), new FractionSym(4, 25), new FractionSym(2, 13),
          new FractionSym(4, 27), new FractionSym(1, 7), new FractionSym(4, 29),
          new FractionSym(2, 15), new FractionSym(4, 31), new FractionSym(1, 8)}, //
      {new FractionSym(5, 2), new FractionSym(5, 3), new FractionSym(5, 4), new FractionSym(1, 1),
          new FractionSym(5, 6), new FractionSym(5, 7), new FractionSym(5, 8),
          new FractionSym(5, 9), new FractionSym(1, 2), new FractionSym(5, 11),
          new FractionSym(5, 12), new FractionSym(5, 13), new FractionSym(5, 14),
          new FractionSym(1, 3), new FractionSym(5, 16), new FractionSym(5, 17),
          new FractionSym(5, 18), new FractionSym(5, 19), new FractionSym(1, 4),
          new FractionSym(5, 21), new FractionSym(5, 22), new FractionSym(5, 23),
          new FractionSym(5, 24), new FractionSym(1, 5), new FractionSym(5, 26),
          new FractionSym(5, 27), new FractionSym(5, 28), new FractionSym(5, 29),
          new FractionSym(1, 6), new FractionSym(5, 31), new FractionSym(5, 32)}, //
      {new FractionSym(3, 1), new FractionSym(2, 1), new FractionSym(3, 2), new FractionSym(6, 5),
          new FractionSym(1, 1), new FractionSym(6, 7), new FractionSym(3, 4),
          new FractionSym(2, 3), new FractionSym(3, 5), new FractionSym(6, 11),
          new FractionSym(1, 2), new FractionSym(6, 13), new FractionSym(3, 7),
          new FractionSym(2, 5), new FractionSym(3, 8), new FractionSym(6, 17),
          new FractionSym(1, 3), new FractionSym(6, 19), new FractionSym(3, 10),
          new FractionSym(2, 7), new FractionSym(3, 11), new FractionSym(6, 23),
          new FractionSym(1, 4), new FractionSym(6, 25), new FractionSym(3, 13),
          new FractionSym(2, 9), new FractionSym(3, 14), new FractionSym(6, 29),
          new FractionSym(1, 5), new FractionSym(6, 31), new FractionSym(3, 16)}, //
      {new FractionSym(7, 2), new FractionSym(7, 3), new FractionSym(7, 4), new FractionSym(7, 5),
          new FractionSym(7, 6), new FractionSym(1, 1), new FractionSym(7, 8),
          new FractionSym(7, 9), new FractionSym(7, 10), new FractionSym(7, 11),
          new FractionSym(7, 12), new FractionSym(7, 13), new FractionSym(1, 2),
          new FractionSym(7, 15), new FractionSym(7, 16), new FractionSym(7, 17),
          new FractionSym(7, 18), new FractionSym(7, 19), new FractionSym(7, 20),
          new FractionSym(1, 3), new FractionSym(7, 22), new FractionSym(7, 23),
          new FractionSym(7, 24), new FractionSym(7, 25), new FractionSym(7, 26),
          new FractionSym(7, 27), new FractionSym(1, 4), new FractionSym(7, 29),
          new FractionSym(7, 30), new FractionSym(7, 31), new FractionSym(7, 32)}, //
      {new FractionSym(4, 1), new FractionSym(8, 3), new FractionSym(2, 1), new FractionSym(8, 5),
          new FractionSym(4, 3), new FractionSym(8, 7), new FractionSym(1, 1),
          new FractionSym(8, 9), new FractionSym(4, 5), new FractionSym(8, 11),
          new FractionSym(2, 3), new FractionSym(8, 13), new FractionSym(4, 7),
          new FractionSym(8, 15), new FractionSym(1, 2), new FractionSym(8, 17),
          new FractionSym(4, 9), new FractionSym(8, 19), new FractionSym(2, 5),
          new FractionSym(8, 21), new FractionSym(4, 11), new FractionSym(8, 23),
          new FractionSym(1, 3), new FractionSym(8, 25), new FractionSym(4, 13),
          new FractionSym(8, 27), new FractionSym(2, 7), new FractionSym(8, 29),
          new FractionSym(4, 15), new FractionSym(8, 31), new FractionSym(1, 4)}, //
      {new FractionSym(9, 2), new FractionSym(3, 1), new FractionSym(9, 4), new FractionSym(9, 5),
          new FractionSym(3, 2), new FractionSym(9, 7), new FractionSym(9, 8),
          new FractionSym(1, 1), new FractionSym(9, 10), new FractionSym(9, 11),
          new FractionSym(3, 4), new FractionSym(9, 13), new FractionSym(9, 14),
          new FractionSym(3, 5), new FractionSym(9, 16), new FractionSym(9, 17),
          new FractionSym(1, 2), new FractionSym(9, 19), new FractionSym(9, 20),
          new FractionSym(3, 7), new FractionSym(9, 22), new FractionSym(9, 23),
          new FractionSym(3, 8), new FractionSym(9, 25), new FractionSym(9, 26),
          new FractionSym(1, 3), new FractionSym(9, 28), new FractionSym(9, 29),
          new FractionSym(3, 10), new FractionSym(9, 31), new FractionSym(9, 32)}, //
      {new FractionSym(5, 1), new FractionSym(10, 3), new FractionSym(5, 2), new FractionSym(2, 1),
          new FractionSym(5, 3), new FractionSym(10, 7), new FractionSym(5, 4),
          new FractionSym(10, 9), new FractionSym(1, 1), new FractionSym(10, 11),
          new FractionSym(5, 6), new FractionSym(10, 13), new FractionSym(5, 7),
          new FractionSym(2, 3), new FractionSym(5, 8), new FractionSym(10, 17),
          new FractionSym(5, 9), new FractionSym(10, 19), new FractionSym(1, 2),
          new FractionSym(10, 21), new FractionSym(5, 11), new FractionSym(10, 23),
          new FractionSym(5, 12), new FractionSym(2, 5), new FractionSym(5, 13),
          new FractionSym(10, 27), new FractionSym(5, 14), new FractionSym(10, 29),
          new FractionSym(1, 3), new FractionSym(10, 31), new FractionSym(5, 16)}, //
      {new FractionSym(11, 2), new FractionSym(11, 3), new FractionSym(11, 4),
          new FractionSym(11, 5), new FractionSym(11, 6), new FractionSym(11, 7),
          new FractionSym(11, 8), new FractionSym(11, 9), new FractionSym(11, 10),
          new FractionSym(1, 1), new FractionSym(11, 12), new FractionSym(11, 13),
          new FractionSym(11, 14), new FractionSym(11, 15), new FractionSym(11, 16),
          new FractionSym(11, 17), new FractionSym(11, 18), new FractionSym(11, 19),
          new FractionSym(11, 20), new FractionSym(11, 21), new FractionSym(1, 2),
          new FractionSym(11, 23), new FractionSym(11, 24), new FractionSym(11, 25),
          new FractionSym(11, 26), new FractionSym(11, 27), new FractionSym(11, 28),
          new FractionSym(11, 29), new FractionSym(11, 30), new FractionSym(11, 31),
          new FractionSym(11, 32)}, //
      {new FractionSym(6, 1), new FractionSym(4, 1), new FractionSym(3, 1), new FractionSym(12, 5),
          new FractionSym(2, 1), new FractionSym(12, 7), new FractionSym(3, 2),
          new FractionSym(4, 3), new FractionSym(6, 5), new FractionSym(12, 11),
          new FractionSym(1, 1), new FractionSym(12, 13), new FractionSym(6, 7),
          new FractionSym(4, 5), new FractionSym(3, 4), new FractionSym(12, 17),
          new FractionSym(2, 3), new FractionSym(12, 19), new FractionSym(3, 5),
          new FractionSym(4, 7), new FractionSym(6, 11), new FractionSym(12, 23),
          new FractionSym(1, 2), new FractionSym(12, 25), new FractionSym(6, 13),
          new FractionSym(4, 9), new FractionSym(3, 7), new FractionSym(12, 29),
          new FractionSym(2, 5), new FractionSym(12, 31), new FractionSym(3, 8)}, //
      {new FractionSym(13, 2), new FractionSym(13, 3), new FractionSym(13, 4),
          new FractionSym(13, 5), new FractionSym(13, 6), new FractionSym(13, 7),
          new FractionSym(13, 8), new FractionSym(13, 9), new FractionSym(13, 10),
          new FractionSym(13, 11), new FractionSym(13, 12), new FractionSym(1, 1),
          new FractionSym(13, 14), new FractionSym(13, 15), new FractionSym(13, 16),
          new FractionSym(13, 17), new FractionSym(13, 18), new FractionSym(13, 19),
          new FractionSym(13, 20), new FractionSym(13, 21), new FractionSym(13, 22),
          new FractionSym(13, 23), new FractionSym(13, 24), new FractionSym(13, 25),
          new FractionSym(1, 2), new FractionSym(13, 27), new FractionSym(13, 28),
          new FractionSym(13, 29), new FractionSym(13, 30), new FractionSym(13, 31),
          new FractionSym(13, 32)}, //
      {new FractionSym(7, 1), new FractionSym(14, 3), new FractionSym(7, 2), new FractionSym(14, 5),
          new FractionSym(7, 3), new FractionSym(2, 1), new FractionSym(7, 4),
          new FractionSym(14, 9), new FractionSym(7, 5), new FractionSym(14, 11),
          new FractionSym(7, 6), new FractionSym(14, 13), new FractionSym(1, 1),
          new FractionSym(14, 15), new FractionSym(7, 8), new FractionSym(14, 17),
          new FractionSym(7, 9), new FractionSym(14, 19), new FractionSym(7, 10),
          new FractionSym(2, 3), new FractionSym(7, 11), new FractionSym(14, 23),
          new FractionSym(7, 12), new FractionSym(14, 25), new FractionSym(7, 13),
          new FractionSym(14, 27), new FractionSym(1, 2), new FractionSym(14, 29),
          new FractionSym(7, 15), new FractionSym(14, 31), new FractionSym(7, 16)}, //
      {new FractionSym(15, 2), new FractionSym(5, 1), new FractionSym(15, 4), new FractionSym(3, 1),
          new FractionSym(5, 2), new FractionSym(15, 7), new FractionSym(15, 8),
          new FractionSym(5, 3), new FractionSym(3, 2), new FractionSym(15, 11),
          new FractionSym(5, 4), new FractionSym(15, 13), new FractionSym(15, 14),
          new FractionSym(1, 1), new FractionSym(15, 16), new FractionSym(15, 17),
          new FractionSym(5, 6), new FractionSym(15, 19), new FractionSym(3, 4),
          new FractionSym(5, 7), new FractionSym(15, 22), new FractionSym(15, 23),
          new FractionSym(5, 8), new FractionSym(3, 5), new FractionSym(15, 26),
          new FractionSym(5, 9), new FractionSym(15, 28), new FractionSym(15, 29),
          new FractionSym(1, 2), new FractionSym(15, 31), new FractionSym(15, 32)}, //
      {new FractionSym(8, 1), new FractionSym(16, 3), new FractionSym(4, 1), new FractionSym(16, 5),
          new FractionSym(8, 3), new FractionSym(16, 7), new FractionSym(2, 1),
          new FractionSym(16, 9), new FractionSym(8, 5), new FractionSym(16, 11),
          new FractionSym(4, 3), new FractionSym(16, 13), new FractionSym(8, 7),
          new FractionSym(16, 15), new FractionSym(1, 1), new FractionSym(16, 17),
          new FractionSym(8, 9), new FractionSym(16, 19), new FractionSym(4, 5),
          new FractionSym(16, 21), new FractionSym(8, 11), new FractionSym(16, 23),
          new FractionSym(2, 3), new FractionSym(16, 25), new FractionSym(8, 13),
          new FractionSym(16, 27), new FractionSym(4, 7), new FractionSym(16, 29),
          new FractionSym(8, 15), new FractionSym(16, 31), new FractionSym(1, 2)}, //
      {new FractionSym(17, 2), new FractionSym(17, 3), new FractionSym(17, 4),
          new FractionSym(17, 5), new FractionSym(17, 6), new FractionSym(17, 7),
          new FractionSym(17, 8), new FractionSym(17, 9), new FractionSym(17, 10),
          new FractionSym(17, 11), new FractionSym(17, 12), new FractionSym(17, 13),
          new FractionSym(17, 14), new FractionSym(17, 15), new FractionSym(17, 16),
          new FractionSym(1, 1), new FractionSym(17, 18), new FractionSym(17, 19),
          new FractionSym(17, 20), new FractionSym(17, 21), new FractionSym(17, 22),
          new FractionSym(17, 23), new FractionSym(17, 24), new FractionSym(17, 25),
          new FractionSym(17, 26), new FractionSym(17, 27), new FractionSym(17, 28),
          new FractionSym(17, 29), new FractionSym(17, 30), new FractionSym(17, 31),
          new FractionSym(17, 32)}, //
      {new FractionSym(9, 1), new FractionSym(6, 1), new FractionSym(9, 2), new FractionSym(18, 5),
          new FractionSym(3, 1), new FractionSym(18, 7), new FractionSym(9, 4),
          new FractionSym(2, 1), new FractionSym(9, 5), new FractionSym(18, 11),
          new FractionSym(3, 2), new FractionSym(18, 13), new FractionSym(9, 7),
          new FractionSym(6, 5), new FractionSym(9, 8), new FractionSym(18, 17),
          new FractionSym(1, 1), new FractionSym(18, 19), new FractionSym(9, 10),
          new FractionSym(6, 7), new FractionSym(9, 11), new FractionSym(18, 23),
          new FractionSym(3, 4), new FractionSym(18, 25), new FractionSym(9, 13),
          new FractionSym(2, 3), new FractionSym(9, 14), new FractionSym(18, 29),
          new FractionSym(3, 5), new FractionSym(18, 31), new FractionSym(9, 16)}, //
      {new FractionSym(19, 2), new FractionSym(19, 3), new FractionSym(19, 4),
          new FractionSym(19, 5), new FractionSym(19, 6), new FractionSym(19, 7),
          new FractionSym(19, 8), new FractionSym(19, 9), new FractionSym(19, 10),
          new FractionSym(19, 11), new FractionSym(19, 12), new FractionSym(19, 13),
          new FractionSym(19, 14), new FractionSym(19, 15), new FractionSym(19, 16),
          new FractionSym(19, 17), new FractionSym(19, 18), new FractionSym(1, 1),
          new FractionSym(19, 20), new FractionSym(19, 21), new FractionSym(19, 22),
          new FractionSym(19, 23), new FractionSym(19, 24), new FractionSym(19, 25),
          new FractionSym(19, 26), new FractionSym(19, 27), new FractionSym(19, 28),
          new FractionSym(19, 29), new FractionSym(19, 30), new FractionSym(19, 31),
          new FractionSym(19, 32)}, //
      {new FractionSym(10, 1), new FractionSym(20, 3), new FractionSym(5, 1), new FractionSym(4, 1),
          new FractionSym(10, 3), new FractionSym(20, 7), new FractionSym(5, 2),
          new FractionSym(20, 9), new FractionSym(2, 1), new FractionSym(20, 11),
          new FractionSym(5, 3), new FractionSym(20, 13), new FractionSym(10, 7),
          new FractionSym(4, 3), new FractionSym(5, 4), new FractionSym(20, 17),
          new FractionSym(10, 9), new FractionSym(20, 19), new FractionSym(1, 1),
          new FractionSym(20, 21), new FractionSym(10, 11), new FractionSym(20, 23),
          new FractionSym(5, 6), new FractionSym(4, 5), new FractionSym(10, 13),
          new FractionSym(20, 27), new FractionSym(5, 7), new FractionSym(20, 29),
          new FractionSym(2, 3), new FractionSym(20, 31), new FractionSym(5, 8)}, //
      {new FractionSym(21, 2), new FractionSym(7, 1), new FractionSym(21, 4),
          new FractionSym(21, 5), new FractionSym(7, 2), new FractionSym(3, 1),
          new FractionSym(21, 8), new FractionSym(7, 3), new FractionSym(21, 10),
          new FractionSym(21, 11), new FractionSym(7, 4), new FractionSym(21, 13),
          new FractionSym(3, 2), new FractionSym(7, 5), new FractionSym(21, 16),
          new FractionSym(21, 17), new FractionSym(7, 6), new FractionSym(21, 19),
          new FractionSym(21, 20), new FractionSym(1, 1), new FractionSym(21, 22),
          new FractionSym(21, 23), new FractionSym(7, 8), new FractionSym(21, 25),
          new FractionSym(21, 26), new FractionSym(7, 9), new FractionSym(3, 4),
          new FractionSym(21, 29), new FractionSym(7, 10), new FractionSym(21, 31),
          new FractionSym(21, 32)}, //
      {new FractionSym(11, 1), new FractionSym(22, 3), new FractionSym(11, 2),
          new FractionSym(22, 5), new FractionSym(11, 3), new FractionSym(22, 7),
          new FractionSym(11, 4), new FractionSym(22, 9), new FractionSym(11, 5),
          new FractionSym(2, 1), new FractionSym(11, 6), new FractionSym(22, 13),
          new FractionSym(11, 7), new FractionSym(22, 15), new FractionSym(11, 8),
          new FractionSym(22, 17), new FractionSym(11, 9), new FractionSym(22, 19),
          new FractionSym(11, 10), new FractionSym(22, 21), new FractionSym(1, 1),
          new FractionSym(22, 23), new FractionSym(11, 12), new FractionSym(22, 25),
          new FractionSym(11, 13), new FractionSym(22, 27), new FractionSym(11, 14),
          new FractionSym(22, 29), new FractionSym(11, 15), new FractionSym(22, 31),
          new FractionSym(11, 16)}, //
      {new FractionSym(23, 2), new FractionSym(23, 3), new FractionSym(23, 4),
          new FractionSym(23, 5), new FractionSym(23, 6), new FractionSym(23, 7),
          new FractionSym(23, 8), new FractionSym(23, 9), new FractionSym(23, 10),
          new FractionSym(23, 11), new FractionSym(23, 12), new FractionSym(23, 13),
          new FractionSym(23, 14), new FractionSym(23, 15), new FractionSym(23, 16),
          new FractionSym(23, 17), new FractionSym(23, 18), new FractionSym(23, 19),
          new FractionSym(23, 20), new FractionSym(23, 21), new FractionSym(23, 22),
          new FractionSym(1, 1), new FractionSym(23, 24), new FractionSym(23, 25),
          new FractionSym(23, 26), new FractionSym(23, 27), new FractionSym(23, 28),
          new FractionSym(23, 29), new FractionSym(23, 30), new FractionSym(23, 31),
          new FractionSym(23, 32)}, //
      {new FractionSym(12, 1), new FractionSym(8, 1), new FractionSym(6, 1), new FractionSym(24, 5),
          new FractionSym(4, 1), new FractionSym(24, 7), new FractionSym(3, 1),
          new FractionSym(8, 3), new FractionSym(12, 5), new FractionSym(24, 11),
          new FractionSym(2, 1), new FractionSym(24, 13), new FractionSym(12, 7),
          new FractionSym(8, 5), new FractionSym(3, 2), new FractionSym(24, 17),
          new FractionSym(4, 3), new FractionSym(24, 19), new FractionSym(6, 5),
          new FractionSym(8, 7), new FractionSym(12, 11), new FractionSym(24, 23),
          new FractionSym(1, 1), new FractionSym(24, 25), new FractionSym(12, 13),
          new FractionSym(8, 9), new FractionSym(6, 7), new FractionSym(24, 29),
          new FractionSym(4, 5), new FractionSym(24, 31), new FractionSym(3, 4)}, //
      {new FractionSym(25, 2), new FractionSym(25, 3), new FractionSym(25, 4),
          new FractionSym(5, 1), new FractionSym(25, 6), new FractionSym(25, 7),
          new FractionSym(25, 8), new FractionSym(25, 9), new FractionSym(5, 2),
          new FractionSym(25, 11), new FractionSym(25, 12), new FractionSym(25, 13),
          new FractionSym(25, 14), new FractionSym(5, 3), new FractionSym(25, 16),
          new FractionSym(25, 17), new FractionSym(25, 18), new FractionSym(25, 19),
          new FractionSym(5, 4), new FractionSym(25, 21), new FractionSym(25, 22),
          new FractionSym(25, 23), new FractionSym(25, 24), new FractionSym(1, 1),
          new FractionSym(25, 26), new FractionSym(25, 27), new FractionSym(25, 28),
          new FractionSym(25, 29), new FractionSym(5, 6), new FractionSym(25, 31),
          new FractionSym(25, 32)}, //
      {new FractionSym(13, 1), new FractionSym(26, 3), new FractionSym(13, 2),
          new FractionSym(26, 5), new FractionSym(13, 3), new FractionSym(26, 7),
          new FractionSym(13, 4), new FractionSym(26, 9), new FractionSym(13, 5),
          new FractionSym(26, 11), new FractionSym(13, 6), new FractionSym(2, 1),
          new FractionSym(13, 7), new FractionSym(26, 15), new FractionSym(13, 8),
          new FractionSym(26, 17), new FractionSym(13, 9), new FractionSym(26, 19),
          new FractionSym(13, 10), new FractionSym(26, 21), new FractionSym(13, 11),
          new FractionSym(26, 23), new FractionSym(13, 12), new FractionSym(26, 25),
          new FractionSym(1, 1), new FractionSym(26, 27), new FractionSym(13, 14),
          new FractionSym(26, 29), new FractionSym(13, 15), new FractionSym(26, 31),
          new FractionSym(13, 16)}, //
      {new FractionSym(27, 2), new FractionSym(9, 1), new FractionSym(27, 4),
          new FractionSym(27, 5), new FractionSym(9, 2), new FractionSym(27, 7),
          new FractionSym(27, 8), new FractionSym(3, 1), new FractionSym(27, 10),
          new FractionSym(27, 11), new FractionSym(9, 4), new FractionSym(27, 13),
          new FractionSym(27, 14), new FractionSym(9, 5), new FractionSym(27, 16),
          new FractionSym(27, 17), new FractionSym(3, 2), new FractionSym(27, 19),
          new FractionSym(27, 20), new FractionSym(9, 7), new FractionSym(27, 22),
          new FractionSym(27, 23), new FractionSym(9, 8), new FractionSym(27, 25),
          new FractionSym(27, 26), new FractionSym(1, 1), new FractionSym(27, 28),
          new FractionSym(27, 29), new FractionSym(9, 10), new FractionSym(27, 31),
          new FractionSym(27, 32)}, //
      {new FractionSym(14, 1), new FractionSym(28, 3), new FractionSym(7, 1),
          new FractionSym(28, 5), new FractionSym(14, 3), new FractionSym(4, 1),
          new FractionSym(7, 2), new FractionSym(28, 9), new FractionSym(14, 5),
          new FractionSym(28, 11), new FractionSym(7, 3), new FractionSym(28, 13),
          new FractionSym(2, 1), new FractionSym(28, 15), new FractionSym(7, 4),
          new FractionSym(28, 17), new FractionSym(14, 9), new FractionSym(28, 19),
          new FractionSym(7, 5), new FractionSym(4, 3), new FractionSym(14, 11),
          new FractionSym(28, 23), new FractionSym(7, 6), new FractionSym(28, 25),
          new FractionSym(14, 13), new FractionSym(28, 27), new FractionSym(1, 1),
          new FractionSym(28, 29), new FractionSym(14, 15), new FractionSym(28, 31),
          new FractionSym(7, 8)}, //
      {new FractionSym(29, 2), new FractionSym(29, 3), new FractionSym(29, 4),
          new FractionSym(29, 5), new FractionSym(29, 6), new FractionSym(29, 7),
          new FractionSym(29, 8), new FractionSym(29, 9), new FractionSym(29, 10),
          new FractionSym(29, 11), new FractionSym(29, 12), new FractionSym(29, 13),
          new FractionSym(29, 14), new FractionSym(29, 15), new FractionSym(29, 16),
          new FractionSym(29, 17), new FractionSym(29, 18), new FractionSym(29, 19),
          new FractionSym(29, 20), new FractionSym(29, 21), new FractionSym(29, 22),
          new FractionSym(29, 23), new FractionSym(29, 24), new FractionSym(29, 25),
          new FractionSym(29, 26), new FractionSym(29, 27), new FractionSym(29, 28),
          new FractionSym(1, 1), new FractionSym(29, 30), new FractionSym(29, 31),
          new FractionSym(29, 32)}, //
      {new FractionSym(15, 1), new FractionSym(10, 1), new FractionSym(15, 2),
          new FractionSym(6, 1), new FractionSym(5, 1), new FractionSym(30, 7),
          new FractionSym(15, 4), new FractionSym(10, 3), new FractionSym(3, 1),
          new FractionSym(30, 11), new FractionSym(5, 2), new FractionSym(30, 13),
          new FractionSym(15, 7), new FractionSym(2, 1), new FractionSym(15, 8),
          new FractionSym(30, 17), new FractionSym(5, 3), new FractionSym(30, 19),
          new FractionSym(3, 2), new FractionSym(10, 7), new FractionSym(15, 11),
          new FractionSym(30, 23), new FractionSym(5, 4), new FractionSym(6, 5),
          new FractionSym(15, 13), new FractionSym(10, 9), new FractionSym(15, 14),
          new FractionSym(30, 29), new FractionSym(1, 1), new FractionSym(30, 31),
          new FractionSym(15, 16)}, //
      {new FractionSym(31, 2), new FractionSym(31, 3), new FractionSym(31, 4),
          new FractionSym(31, 5), new FractionSym(31, 6), new FractionSym(31, 7),
          new FractionSym(31, 8), new FractionSym(31, 9), new FractionSym(31, 10),
          new FractionSym(31, 11), new FractionSym(31, 12), new FractionSym(31, 13),
          new FractionSym(31, 14), new FractionSym(31, 15), new FractionSym(31, 16),
          new FractionSym(31, 17), new FractionSym(31, 18), new FractionSym(31, 19),
          new FractionSym(31, 20), new FractionSym(31, 21), new FractionSym(31, 22),
          new FractionSym(31, 23), new FractionSym(31, 24), new FractionSym(31, 25),
          new FractionSym(31, 26), new FractionSym(31, 27), new FractionSym(31, 28),
          new FractionSym(31, 29), new FractionSym(31, 30), new FractionSym(1, 1),
          new FractionSym(31, 32)}, //
      {new FractionSym(16, 1), new FractionSym(32, 3), new FractionSym(8, 1),
          new FractionSym(32, 5), new FractionSym(16, 3), new FractionSym(32, 7),
          new FractionSym(4, 1), new FractionSym(32, 9), new FractionSym(16, 5),
          new FractionSym(32, 11), new FractionSym(8, 3), new FractionSym(32, 13),
          new FractionSym(16, 7), new FractionSym(32, 15), new FractionSym(2, 1),
          new FractionSym(32, 17), new FractionSym(16, 9), new FractionSym(32, 19),
          new FractionSym(8, 5), new FractionSym(32, 21), new FractionSym(16, 11),
          new FractionSym(32, 23), new FractionSym(4, 3), new FractionSym(32, 25),
          new FractionSym(16, 13), new FractionSym(32, 27), new FractionSym(8, 7),
          new FractionSym(32, 29), new FractionSym(16, 15), new FractionSym(32, 31),
          new FractionSym(1, 1)} //
  };

  // static {
  // printCacheSource();
  // }

  /**
   * Compute the Bernoulli number of the first kind with the <a href=
   * "https://oeis.org/wiki/User:Peter_Luschny/ComputationAndAsymptoticsOfBernoulliNumbers#Seidel">Seidel
   * algorithm</a>
   * 
   * @param n
   * @return throws ArithmeticException if n is a negative int number
   */
  // public static IRational bernoulliNumber(int n) {
  // if (n == 0) {
  // return F.C1;
  // } else if (n == 1) {
  // return F.CN1D2;
  // } else if (n < 0) {
  // throw new ArithmeticException("BernoulliB(n): n is not a positive int number");
  // } else if (n % 2 != 0) {
  // // http://fungrim.org/entry/a98234/
  // return F.C0;
  // }
  // if (n > Config.MAX_AST_SIZE) {
  // throw new ASTElementLimitExceeded(n);
  // }
  //
  // synchronized (BernoulliCache.CACHE) {
  // if (n < BernoulliCache.CACHE.size() && BernoulliCache.CACHE.get(n) != null) {
  // return BernoulliCache.CACHE.get(n);
  // }
  //
  // int oldSize = BernoulliCache.CACHE.size();
  // if (n >= oldSize) {
  // BernoulliCache.CACHE.addAll(Collections.nCopies(n - oldSize + 1, null));
  // }
  //
  // if (n == 0) {
  // BernoulliCache.CACHE.set(0, F.C1);
  // return F.C1;
  // }
  // if (n == 1) {
  // BernoulliCache.CACHE.set(1, F.CN1D2);
  // return F.CN1D2;
  // }
  // if (n % 2 != 0) {
  // BernoulliCache.CACHE.set(n, F.C0);
  // return F.C0;
  // }
  //
  // // Seidel's algorithm implementation
  // int m = n / 2;
  // IRational[] d = new IRational[m + 3];
  // IRational[] r = new IRational[m + 3];
  // for (int i = 0; i < d.length; i++) {
  // d[i] = F.C0;
  // }
  // d[1] = F.C1;
  // r[0] = F.C1;
  //
  // long iterationLimit = EvalEngine.get().getIterationLimit();
  // if (iterationLimit > 0 && iterationLimit < Integer.MAX_VALUE / 2) {
  // iterationLimit *= 10L;
  // iterationLimit += n;
  // if (iterationLimit > 0L && iterationLimit <= n) {
  // IterationLimitExceeded.throwIt(iterationLimit, F.BernoulliB(F.ZZ(n)));
  // }
  // }
  // int iterationCounter = 0;
  //
  // boolean b = true;
  // int h = 1;
  // IInteger p = F.C1;
  // IInteger s = F.CN2;
  //
  // IInteger q = F.C0;
  // for (int i = 0; i < n; i++) {
  // iterationCounter += h;
  // if (iterationLimit > 0L && iterationLimit <= iterationCounter) {
  // IterationLimitExceeded.throwIt(iterationCounter, F.BernoulliB(F.ZZ(n)));
  // }
  //
  // if (b) {
  // h++;
  // p = p.multiply(4);
  // s = s.negate();
  // q = s.multiply(p.subtract(F.C1));
  // for (int k = h; k > 0; k--) {
  // d[k] = d[k].add(d[k + 1]);
  // }
  // } else {
  // for (int k = 1; k < h; k++) {
  // d[k] = d[k].add(d[k - 1]);
  // }
  // r[h] = d[h - 1].divideBy(q);
  //
  // }
  // b = !b;
  // }
  // for (int i = oldSize; i <= n; i++) {
  // if (i % 2 != 0) {
  // BernoulliCache.CACHE.set(i, F.C0);
  // } else {
  // BernoulliCache.CACHE.set(i, r[i / 2 + 1]);
  // }
  // }
  // return BernoulliCache.CACHE.get(n);
  // }
  // }


  /**
   * Compute the Bernoulli number of the first kind.
   *
   * @param n
   * @return throws ArithmeticException if n is a negative int number
   */
  public static IRational bernoulliNumber(int n) {
    if (n == 0) {
      return F.C1;
    } else if (n == 1) {
      return F.CN1D2;
    } else if (n < 0) {
      throw new ArithmeticException("BernoulliB(n): n is not a positive int number");
    } else if (n % 2 != 0) {
      // http://fungrim.org/entry/a98234/
      return F.C0;
    }
    if (n > Config.MAX_AST_SIZE) {
      throw new ASTElementLimitExceeded(n);
    }
    IFraction[] bernoulli = new IFraction[n + 1];
    bernoulli[0] = FractionSym.ONE;
    bernoulli[1] = AbstractFractionSym.valueOf(-1L, 2L);

    long iterationLimit = EvalEngine.get().getIterationLimit();
    if (iterationLimit > 0 && iterationLimit < Integer.MAX_VALUE / 2) {
      iterationLimit *= 10L;
      iterationLimit += n;
      if (iterationLimit > 0L && iterationLimit <= n) {
        IterationLimitExceeded.throwIt(iterationLimit, F.BernoulliB(F.ZZ(n)));
      }
    }

    if (n <= BinomialCache.MAX_N) {
      iterationLimit = -1;
    }
    int iterationCounter = 0;
    for (int k = 2; k <= n; k++) {
      bernoulli[k] = FractionSym.ZERO;
      iterationCounter += k;
      if (iterationLimit > 0 && iterationLimit <= iterationCounter) {
        IterationLimitExceeded.throwIt(iterationCounter, F.BernoulliB(F.ZZ(n)));
      }
      for (int i = 0; i < k; i++) {
        if (!bernoulli[i].isZero()) {
          int nb = k + 1;
          int mb = nb - i;
          IFraction bin = AbstractFractionSym.valueOf(AbstractIntegerSym.binomial(nb, mb));
          bernoulli[k] = bernoulli[k].sub(bin.mul(bernoulli[i]));
        }
      }
      bernoulli[k] = bernoulli[k].div(AbstractFractionSym.valueOf(k + 1));

    }
    return bernoulli[n].normalize();
  }

  /**
   * Compute the Bernoulli number of the first kind.
   *
   * <p>
   * See <a href="http://en.wikipedia.org/wiki/Bernoulli_number">Wikipedia - Bernoulli number</a>.
   * <br>
   * For better performing implementations see
   * <a href= "http://oeis.org/wiki/User:Peter_Luschny/ComputationAndAsymptoticsOfBernoulliNumbers"
   * >ComputationAndAsymptoticsOfBernoulliNumbers</a>
   *
   * @param n
   * @return throws ArithmeticException if n is not an non-negative Java int number
   */
  public static IRational bernoulliNumber(final IInteger n) {
    int bn = n.toIntDefault(-1);
    if (bn >= 0) {
      return bernoulliNumber(bn);
    }
    throw new ArithmeticException("BernoulliB(n): n is not a positive int number");
  }

  /**
   * Returns the last element of the series of convergent-steps to approximate the given value.
   * 
   * @param value value to approximate
   * @param maxConvergents maximum number of convergents to examine
   * @param limit
   * @return the fraction of last element of the series of convergents and a boolean if that element
   *         satisfies the specified convergent test
   */
  private static IFraction convergeFraction(double value, int maxConvergents, double limit) {
    return rationalize(value, v -> {
      org.hipparchus.util.Pair<BigFraction, Boolean> convergent = BigFraction.convergent(v,
          maxConvergents, (p, q) -> FastMath.abs(p * q - v * q * q) <= limit);
      return convergent.getSecond().booleanValue() ? convergent.getFirst() : null;
    });
  }

  private static IFraction fractionOf(BigInteger numerator, BigInteger denominator) {
    if (denominator.signum() == 0) {
      throw getDivisionTroughZeroException(F.ZZ(numerator)); // Infinite expression `1` encountered.
    }
    if (hasIntValue(denominator) && hasIntValue(numerator)) {
      return valueOf(numerator.intValue(), denominator.intValue());
    }
    return null;
  }

  public static BigInteger gcd(BigInteger i1, BigInteger i2) {
    if (i1.equals(BigInteger.ONE) || i2.equals(BigInteger.ONE)) {
      return BigInteger.ONE;
    } else if (hasIntValue(i1) && hasIntValue(i2)) {
      return BigInteger.valueOf(AbstractIntegerSym.gcd(i1.intValue(), i2.intValue()));
    } else {
      return i1.gcd(i2);
    }
  }

  /**
   * Returns a new instance of {@link ArgumentTypeException} with message shortcut <code>infy</code>
   * - <code>Infinite expression `1` encountered</code>
   * 
   * @param num
   * @return
   */
  private static ArgumentTypeException getDivisionTroughZeroException(IInteger num) {
    // Infinite expression `1` encountered.
    String str = Errors.getMessage("infy", F.list(F.Rational(num, F.C0)), EvalEngine.get());
    return new ArgumentTypeException(str);
  }

  private static IExpr getInfiniteOrInteger(double value) {
    if (Double.isNaN(value)) {
      return F.NIL;
    } else if (value == Double.POSITIVE_INFINITY) {
      return F.CInfinity;
    } else if (value == Double.NEGATIVE_INFINITY) {
      return F.CNInfinity;
    }
    long integerValue = (long) value;
    if (value == integerValue) { // also catches value == 0
      return F.ZZ(integerValue); // take shortcut
    }
    return null;
  }

  private static void printCacheSource() {
    System.out.println("static final FractionSym[][] CACHE = new FractionSym[][] {");
    FractionSym[][] CACHE =
        new FractionSym[(HIGH_NUMER - LOW_NUMER) + 1][(HIGH_DENOM - LOW_DENOM) + 1];
    int i = LOW_NUMER;
    for (int k = 0; k < CACHE.length; k++) {
      int j = LOW_DENOM;
      System.out.print("{");
      for (int l = 0; l < CACHE[0].length; l++) {
        int gcd = ArithmeticUtils.gcd(i, j);
        if (gcd == 1) {
          CACHE[k][l] = new FractionSym(i, j);
          System.out.print("new FractionSym(" + i + "," + j + ")");
        } else {
          CACHE[k][l] = new FractionSym(i / gcd, j / gcd);
          System.out.print("new FractionSym(" + (i / gcd) + "," + (j / gcd) + ")");
        }
        j++;
        if (l < CACHE[0].length - 1) {
          System.out.print(",");
        }

      }

      if (k < CACHE.length - 1) {
        System.out.print("}, // \n");
      } else {
        System.out.print("} // \n");
      }
      i++;
    }
    System.out.println("};");
  }

  private static IFraction rationalize(double value, DoubleFunction<BigFraction> f) {
    try {
      BigFraction fraction = f.apply(value < 0 ? -value : value);
      if (fraction != null) {// && fraction.getNumerator().signum()!=0) {
        return valueOf(value < 0 ? fraction.negate() : fraction);
      }
    } catch (MathRuntimeException e) { // assume no solution
    }
    return null;
  }

  public static IExpr rationalize(IExpr arg1) {
    return rationalize(arg1, Config.DOUBLE_EPSILON, true);
  }

  /**
   * Rationalize only pure numeric numbers in <code>expr</code>.
   *
   * @param expr
   * @return {@link F#NIL} if no expression was transformed
   */
  public static IExpr rationalize(IExpr expr, boolean useConvergenceMethod) {
    return AbstractFractionSym.rationalize(expr, Config.DOUBLE_EPSILON, useConvergenceMethod);
  }

  /**
   * Rationalize only pure numeric numbers in expression <code>expr</code>.
   *
   * @param expr
   * @param epsilon
   * @param useConvergenceMethod
   * @return {@link F#NIL} if no expression was transformed
   */
  public static IExpr rationalize(IExpr expr, double epsilon, boolean useConvergenceMethod) {
    RationalizeNumericsVisitor rationalizeVisitor =
        new RationalizeNumericsVisitor(epsilon, useConvergenceMethod);
    return expr.accept(rationalizeVisitor);
  }

  public static IFraction valueOf(BigFraction fraction) {
    IFraction f = fractionOf(fraction.getNumerator(), fraction.getDenominator());
    return f != null ? f : new BigFractionSym(fraction);
  }

  public static IFraction valueOf(BigInteger numerator) {
    return valueOf(numerator, BigInteger.ONE);
  }

  /**
   * Construct a rational from two BigIntegers. Use this method to create a rational number if the
   * numerator or denominator may be to big to fit in an Java int. This method normalizes the
   * rational number.
   *
   * @param numerator Numerator
   * @param denominator Denominator
   * @return
   */
  public static IFraction valueOf(BigInteger numerator, BigInteger denominator) {
    IFraction f = fractionOf(numerator, denominator);
    return f != null ? f : new BigFractionSym(numerator, denominator);
  }

  public static IFraction valueOf(IInteger numerator) {
    if (numerator instanceof IntegerSym) {
      return valueOf(((IntegerSym) numerator).fIntValue);
    }
    return valueOf(numerator.toBigNumerator());
  }

  public static IFraction valueOf(IInteger numerator, IInteger denominator) {
    if (numerator instanceof IntegerSym && denominator instanceof IntegerSym) {
      return valueOf(((IntegerSym) numerator).fIntValue, ((IntegerSym) denominator).fIntValue);
    }
    return valueOf(numerator.toBigNumerator(), denominator.toBigNumerator());
  }

  /**
   * Construct a rational from two longs. Use this method to create a rational number. This method
   * normalizes the rational number and may return a previously created one. This method does not
   * work if called with value Long.MIN_VALUE.
   *
   * @param numerator Numerator.
   * @return
   */
  public static IFraction valueOf(long numerator) {
    if (numerator == 0) {
      return FractionSym.ZERO;
    }
    if (numerator == 1) {
      return FractionSym.ONE;
    }
    if (numerator == -1) {
      return FractionSym.MONE;
    }

    if (Integer.MIN_VALUE < numerator && numerator <= Integer.MAX_VALUE) {
      return new FractionSym((int) numerator, 1);
    }
    return new BigFractionSym(BigInteger.valueOf(numerator), BigInteger.ONE);
  }

  /**
   * Construct a rational from two longs. Use this method to create a rational number. This method
   * normalizes the rational number and may return a previously created one. This method does not
   * work if called with value Long.MIN_VALUE.
   *
   * @param numerator Numerator.
   * @param denominator Denominator.
   * @return
   */
  public static IFraction valueOf(long numerator, long denominator) {
    if (numerator > Long.MIN_VALUE && denominator > Long.MIN_VALUE) {
      if (denominator == 0) {
        throw getDivisionTroughZeroException(F.ZZ(numerator));
      } else if (numerator == 0) {
        return FractionSym.ZERO;
      } else if (numerator == denominator) {
        return FractionSym.ONE;
      }
      if (denominator < 0) {
        numerator = -numerator;
        denominator = -denominator;
      }
      if (numerator >= FractionSym.LOW_NUMER && numerator <= FractionSym.HIGH_NUMER //
          && denominator >= FractionSym.LOW_DENOM && denominator <= FractionSym.HIGH_DENOM) {
        return FractionSym.CACHE[((int) numerator) + (-FractionSym.LOW_NUMER)][((int) denominator)
            + (-FractionSym.LOW_DENOM)];
      }
      if (numerator != 1 && denominator != 1) {
        long gcd = Math.abs(ArithmeticUtils.gcd(numerator, denominator));
        if (gcd != 1L) {
          if (Config.TRACE_BASIC_ARITHMETIC && EvalEngine.get().isTraceMode()) {
            if (EvalEngine.get().isTraceMode()) {
              IAST divide = F.Rational(F.ZZ(numerator), F.ZZ(denominator));
              EvalEngine.get().addTraceStep(divide, divide,
                  F.List(S.Rational, F.$str("FractionCancelGCD"), divide, F.ZZ(gcd)));
            }
          }
          numerator /= gcd;
          denominator /= gcd;
        }
      }

      if (denominator == 1) {
        if (numerator == 1) {
          return FractionSym.ONE;
        }
        if (numerator == -1) {
          return FractionSym.MONE;
        }
        if (numerator == 0) {
          return FractionSym.ZERO;
        }
      }

      if (Integer.MIN_VALUE < numerator && numerator <= Integer.MAX_VALUE
          && denominator <= Integer.MAX_VALUE) {
        return new FractionSym((int) numerator, (int) denominator);
      }
    } else {
      if (denominator == 0) {
        throw getDivisionTroughZeroException(F.ZZ(numerator));
      } else if (numerator == 0) {
        return FractionSym.ZERO;
      } else if (numerator == denominator) {
        return FractionSym.ONE;
      }
    }
    return new BigFractionSym(BigInteger.valueOf(numerator), BigInteger.valueOf(denominator));
  }

  /**
   * Returns the last element of the series of convergent-steps to approximate the given value.
   * 
   * @param value value to approximate
   */
  public static IFraction valueOfConvergent(double value) {
    IFraction fraction = convergeFraction(value, 20, 0.5E-4);
    if (fraction == null) {
      throw new NoSuchElementException("No converging fraction found for value " + value);
    }
    return fraction;
  }

  /**
   * Rationalize the given double value with <code>epsilon</code> maximum error allowed.
   *
   * @param value the double value to convert to a fraction.
   * @param epsilon maximum error allowed. The resulting fraction is within epsilon of value, in
   *        absolute terms.
   * @return
   */
  public static IFraction valueOfEpsilon(double value, double epsilon) {
    IFraction fraction = rationalize(value, v -> new BigFraction(v, epsilon, 200));
    return fraction != null ? fraction : valueOf(new BigFraction(value));
  }

  /**
   * Rationalizes the given double value exactly.
   * <p>
   * This methods returns an {@link IExpr} that, when being evaluated to a double value (using
   * {@link IExpr#evalf()}), results to the exact same value (per bit) as the given one.
   * </p>
   * <p>
   * Although it is not possible to express all real numbers as a fraction of two integers, it is
   * possible for all finite floating-point numbers to be expressed as fraction with exact same
   * value, because floating-point numbers are finite in their representation and therefore cannot
   * express all real numbers exactly. But this allows the exact representation as a fraction.<br>
   * Nevertheless this may lead to unexpected results. For example the value {@code 0.7} is
   * rationalized to {@code 3152519739159347/4503599627370496} and not the expected {@code 7/10}.
   * </p>
   * There is no guarantee made about the specific type of the returned expression. Not all possible
   * values of a double, especially small ones, can be expressed as {@link IFraction} in such way
   * that it evaluates to the same double value. In such cases the value is expressed by
   * {@code  mantissa * 2 ^ power}.
   * 
   * @param value the double value to convert
   * @return an IExpr that evaluates to the exact same double value
   */
  public static IExpr valueOfExact(double value) {
    IExpr ii = getInfiniteOrInteger(value);
    if (ii != null) {
      return ii;
    }
    BigFraction exactFraction = new BigFraction(value); // computes exact fraction representation
    int denominatorExponent = exactFraction.getDenominator().bitLength() - 1;
    if (denominatorExponent <= Double.MAX_EXPONENT) {
      return valueOf(exactFraction);
    }
    // The fractions denominator cannot be expressed as double value and would lead to an infinite
    // denominator double-value this has the consequence that the fraction will become return zero
    // when evaluated to double. Instead express the value as mantissa2 * 2 ^ exp2

    int exp2 = Math.getExponent(value);
    double mantissa2 = value * Math.pow(2, -exp2);

    IExpr mantissaFraction = getInfiniteOrInteger(mantissa2); // try shortcut
    if (mantissaFraction == null) {
      mantissaFraction = valueOf(new BigFraction(mantissa2));
    }
    return F.Times(mantissaFraction, F.Power(F.C2, F.ZZ(exp2)));
  }

  /**
   * Rationalizes the given double value exactly while tending to return results that are closer to
   * what a human would expect and are therefore considered 'nicer'.
   * <p>
   * This method has the same constraints for the returned value like {@link #valueOfExact(double)},
   * which often does return less 'nice' results, but tends to run longer.
   * </p>
   * 
   * @param value the double value to convert
   * @return an IExpr that evaluates to the exact same double value
   * @see #valueOfExact(double)
   * @see #valueOfConvergent(double)
   */
  public static IExpr valueOfExactNice(double value) {
    IExpr ii = getInfiniteOrInteger(value);
    if (ii != null) {
      return ii;
    }
    if (FastMath.abs(Math.getExponent(value)) < 100) {
      // For values in the order of small powers of ten and only a few decimals (e.g. 3.75, 0.01,
      // 124.6) the convergence approach usually achieves results that are closer to what a human
      // would compute and are therefore considered 'nicer'. To honor this we try a few convergence
      // iterations and take that result if it is exact.
      // The number of maxIterations is a tuning parameter and reflects how bad we want nicer
      // results. Since valueOfExact() is faster the fewer iterations are attempted before giving up
      // the faster this method is in average.
      IFraction fraction = convergeFraction(value, 5, 0.0);
      if (fraction != null && value == fraction.doubleValue()) {
        return fraction;
      }
    }
    return valueOfExact(value);
  }

  /** {@inheritDoc} */
  @Override
  public IExpr accept(IVisitor visitor) {
    return visitor.visit(this);
  }

  /** {@inheritDoc} */
  @Override
  public boolean accept(IVisitorBoolean visitor) {
    return visitor.visit(this);
  }

  /** {@inheritDoc} */
  @Override
  public int accept(IVisitorInt visitor) {
    return visitor.visit(this);
  }

  /** {@inheritDoc} */
  @Override
  public long accept(IVisitorLong visitor) {
    return visitor.visit(this);
  }

  /** {@inheritDoc} */
  @Override
  public IRational add(IRational that) {
    if (that instanceof IFraction) {
      return add((IFraction) that);
    }
    if (that instanceof IntegerSym) {
      return add(AbstractFractionSym.valueOf(((IntegerSym) that).fIntValue));
    }
    return add(AbstractFractionSym.valueOf(((BigIntegerSym) that).fBigIntValue));
  }

  /** {@inheritDoc} */
  @Override
  public IReal add(IReal that) {
    if (that instanceof IRational) {
      return add((IRational) that);
    }
    return Num.valueOf(doubleValue() + that.doubleValue());
  }

  /**
   * Returns <code>this+(fac1*fac2)</code>.
   *
   * @param fac1 the first factor
   * @param fac2 the second factor
   * @return <code>this+(fac1*fac2)</code>
   */
  public IFraction addmul(IFraction fac1, IFraction fac2) {
    return add(fac1.mul(fac2));
  }

  @Override
  public ApcomplexNum apcomplexNumValue() {
    return ApcomplexNum.valueOf(apcomplexValue());
  }

  @Override
  public Apcomplex apcomplexValue() {
    FixedPrecisionApfloatHelper h = EvalEngine.getApfloat();
    Apfloat real = h.divide(new Apfloat(toBigNumerator(), h.precision()),
        new Apfloat(toBigDenominator(), h.precision()));
    return new Apcomplex(real);
  }

  @Override
  public ApfloatNum apfloatNumValue() {
    return ApfloatNum.valueOf(toBigNumerator(), toBigDenominator());
  }

  @Override
  public Apfloat apfloatValue() {
    long precision = EvalEngine.getApfloat().precision();
    Apfloat n = new Apfloat(toBigNumerator(), precision);
    Apfloat d = new Apfloat(toBigDenominator(), precision);
    return n.divide(d);
  }

  @Override
  public Pair asNumerDenom() {
    return F.pair(numerator(), denominator());
  }

  @Override
  public void checkBitLength() {
    if (Integer.MAX_VALUE > Config.MAX_BIT_LENGTH) {
      long bitLength = (long) toBigNumerator().bitLength() + toBigDenominator().bitLength();
      if (bitLength > Config.MAX_BIT_LENGTH) {
        BigIntegerLimitExceeded.throwIt(bitLength);
      }
    }
  }

  @Override
  public int compareTo(IExpr expr) {
    if (expr.isNumber() && !expr.isReal()) {
      int c = this.compareTo(expr.re());
      if (c != 0) {
        return c;
      }
      IExpr im = expr.im();
      return im.isPositive() ? -1 : im.isNegative() ? 1 : IExpr.compareHierarchy(this, expr);
    }
    return IExpr.compareHierarchy(this, expr);
  }

  /** {@inheritDoc} */
  @Override
  public int complexSign() {
    return toBigNumerator().signum();
  }

  @Override
  public IExpr copy() {
    return this;
  }

  /**
   * Returns the denominator of this fraction.
   *
   * @return denominator
   */
  @Override
  public IInteger denominator() {
    return AbstractIntegerSym.valueOf(toBigDenominator());
  }

  /** {@inheritDoc} */
  @Override
  public IRational divideBy(IRational that) {
    if (that instanceof IFraction) {
      return this.div((IFraction) that);
    }
    if (that instanceof IntegerSym) {
      return this.div(AbstractFractionSym.valueOf(((IntegerSym) that).fIntValue));
    }
    return this.div(AbstractFractionSym.valueOf(((BigIntegerSym) that).fBigIntValue));
  }

  /** {@inheritDoc} */
  @Override
  public IReal divideBy(IReal that) {
    if (that instanceof IRational) {
      return this.divideBy((IRational) that);
    }
    return Num.valueOf(doubleValue() / that.doubleValue());
  }

  @Override
  public boolean equalsInt(int i) {
    return toBigNumerator().equals(BigInteger.valueOf(i))
        && toBigDenominator().equals(BigInteger.ONE);
  }

  /** {@inheritDoc} */
  @Override
  public IExpr evaluate(EvalEngine engine) {
    if (engine.isNumericMode()) {
      return numericNumber();
    }
    INumber cTemp = normalize();
    return (cTemp == this) ? F.NIL : cTemp;
  }

  @Override
  /** {@inheritDoc} */
  public IASTAppendable factorInteger() {
    IInteger num = numerator();
    IInteger den = denominator();
    IASTAppendable numResult = num.factorInteger();
    IASTAppendable denResult = den.factorInteger();
    // negate the exponents of the denominator part
    denResult.forEach(list -> ((IASTMutable) list).set(2, list.second().negate()));
    IASTAppendable result = F.ListAlloc(denResult.argSize() + numResult.argSize());
    // add the factors from the numerator part
    result.appendArgs(denResult);
    result.appendArgs(numResult);
    EvalAttributes.sort(result);
    return result;
  }

  @Override
  public IAST factorSmallPrimes(int rootNumerator, int rootDenominator) {
    IInteger b = numerator();
    boolean isNegative = false;
    if (complexSign() < 0) {
      b = b.negate();
      isNegative = true;
    }
    if (rootNumerator != 1) {
      b = b.powerRational(rootNumerator);
    }
    IInteger d = denominator();
    if (rootNumerator != 1) {
      d = d.powerRational(rootNumerator);
    }
    Int2IntMap bMap = new Int2IntRBTreeMap();
    BigInteger number = b.toBigNumerator();
    IAST bAST = AbstractIntegerSym.factorBigInteger(number, isNegative, rootNumerator,
        rootDenominator, bMap);
    Int2IntMap dMap = new Int2IntRBTreeMap();
    number = d.toBigNumerator();
    IAST dAST =
        AbstractIntegerSym.factorBigInteger(number, false, rootNumerator, rootDenominator, dMap);
    if (bAST.isPresent()) {
      if (dAST.isPresent()) {
        return F.Times(bAST, F.Power(dAST, F.CN1));
      }
      return F.Times(bAST, F.Power(denominator(), F.QQ(-rootNumerator, rootDenominator)));
    } else if (dAST.isPresent()) {
      return F.Times(F.Power(numerator(), F.QQ(rootNumerator, rootDenominator)),
          F.Power(dAST, F.CN1));
    }
    return F.NIL;
  }

  /** {@inheritDoc} */
  @Override
  public IRational gcd(IRational that) {
    if (that.isZero()) {
      return this;
    }
    if (this.isZero()) {
      return that;
    }
    BigInteger tdenom = this.toBigDenominator();
    BigInteger odenom = that.toBigDenominator();
    BigInteger gcddenom = tdenom.gcd(odenom);
    BigInteger denom = tdenom.divide(gcddenom).multiply(odenom);
    BigInteger num = toBigNumerator().gcd(that.toBigNumerator());
    return AbstractFractionSym.valueOf(num, denom);
  }

  @Override
  public IRational getImaginaryPart() {
    return F.C0;
  }

  @Override
  public IRational getRealPart() {
    return this;
  }

  @Override
  public ISymbol head() {
    return S.Rational;
  }

  @Override
  public int hierarchy() {
    return FRACTIONID;
  }

  @Override
  public IReal im() {
    return F.C0;
  }

  @Override
  public double imDoubleValue() {
    return 0.0;
  }

  /** {@inheritDoc} */
  @Override
  public IInteger integerPart() {
    return isNegative() ? ceilFraction() : floorFraction();
  }

  @Override
  public boolean isGT(IReal obj) {
    if (obj instanceof FractionSym) {
      return compareTo((obj)) > 0;
    }
    if (obj instanceof IInteger) {
      return compareTo(
          AbstractFractionSym.valueOf(((IInteger) obj).toBigNumerator(), BigInteger.ONE)) > 0;
    }
    return doubleValue() > obj.doubleValue();
  }

  /**
   * Check whether this rational represents an integral value (i.e. the denominator equals 1).
   *
   * @return <code>true</code> iff value is integral.
   */
  public abstract boolean isIntegral();

  @Override
  public boolean isLT(IReal obj) {
    if (obj instanceof FractionSym) {
      return compareTo((obj)) < 0;
    }
    if (obj instanceof IInteger) {
      return compareTo(
          AbstractFractionSym.valueOf(((IInteger) obj).toBigNumerator(), BigInteger.ONE)) < 0;
    }
    return doubleValue() < obj.doubleValue();
  }

  /** {@inheritDoc} */
  @Override
  public boolean isNumEqualRational(IRational value) throws ArithmeticException {
    return equals(value);
  }

  /** {@inheritDoc} */
  @Override
  public boolean isRationalValue(IRational value) {
    return equals(value);
  }

  @Override
  public long leafCount() {
    return 3;
  }

  @Override
  public long leafCountSimplify() {
    return 1 + numerator().leafCountSimplify() + denominator().leafCountSimplify();
  }

  /**
   * Return a new rational representing <code>this * other</code>.
   *
   * @param other Rational to multiply.
   * @return Product of <code>this</code> and <code>other</code>.
   */
  @Override
  public IFraction mul(IFraction other) {
    if (other.isOne()) {
      return this;
    }
    if (other.isZero()) {
      return other;
    }
    if (other.isMinusOne()) {
      return this.negate();
    }

    BigInteger newnum = toBigNumerator().multiply(other.toBigNumerator());
    BigInteger newdenom = toBigDenominator().multiply(other.toBigDenominator());
    return valueOf(newnum, newdenom);
  }

  /** {@inheritDoc} */
  @Override
  public IRational multiply(IRational that) {
    if (that instanceof IFraction) {
      return mul((IFraction) that);
    }
    if (that instanceof IntegerSym) {
      return mul(AbstractFractionSym.valueOf(((IntegerSym) that).fIntValue));
    }
    return mul(AbstractFractionSym.valueOf(((BigIntegerSym) that).fBigIntValue));
  }

  /** {@inheritDoc} */
  @Override
  public IReal multiply(IReal that) {
    if (that instanceof IRational) {
      return multiply((IRational) that);
    }
    return Num.valueOf(doubleValue() * that.doubleValue());
  }

  /**
   * Returns the numerator of this fraction.
   *
   * @return denominator
   */
  @Override
  public IInteger numerator() {
    return AbstractIntegerSym.valueOf(toBigNumerator());
  }

  @Override
  public INumber numericNumber() {
    return F.num(this);
  }

  @Override
  public Num numValue() {
    return Num.valueOf(doubleValue());
  }

  @Override
  public IReal opposite() {
    return this.negate();
  }

  /** {@inheritDoc} */
  @Override
  public IExpr plus(IExpr that) {
    if (that instanceof INumber) {
      return this.plus((INumber) that);
    }
    return IFraction.super.plus(that);
  }

  /** {@inheritDoc} */
  @Override
  public INumber plus(INumber that) {
    if (that.isZero()) {
      return this;
    }
    if (that instanceof IFraction) {
      return this.add((IFraction) that).normalize();
    }
    if (that instanceof IntegerSym) {
      return this.add(valueOf(((IntegerSym) that).fIntValue)).normalize();
    }
    if (that instanceof BigIntegerSym) {
      return this.add(valueOf(((BigIntegerSym) that).fBigIntValue)).normalize();
    }
    if (that instanceof ComplexSym) {
      return ((ComplexSym) that).add(ComplexSym.valueOf(this)).normalize();
    }
    if (that instanceof INum) {
      if (that instanceof ApfloatNum) {
        return apfloatNumValue().add(((ApfloatNum) that).apfloatNumValue());
      }
      return F.num(((Num) that).value + evalf());
    }
    if (that instanceof IComplexNum) {
      if (that instanceof ApcomplexNum) {
        return apcomplexNumValue().add(((ApcomplexNum) that).apcomplexNumValue());
      }
      return F.complexNum(evalfc().add(that.evalfc()));
    }
    throw new java.lang.ArithmeticException();
  }

  @Override
  public IExpr power(IExpr that) {
    if (that instanceof IInteger) {
      if (that.isZero()) {
        if (!this.isZero()) {
          return F.C1;
        }
        return IFraction.super.power(that);
      } else if (that.isOne()) {
        return this;
      } else if (that.isMinusOne()) {
        return inverse();
      }
      long n = that.toLongDefault();
      if (F.isPresent(n)) {
        return power(n);
      }
    }
    return IFraction.super.power(that);
  }

  /** {@inheritDoc} */
  @Override
  public final IFraction powerRational(long n) throws ArithmeticException {
    if (n == 0L) {
      if (!this.isZero()) {
        return FractionSym.ONE;
      }
      throw new ArithmeticException("Indeterminate: 0^0");
    } else if (n == 1L) {
      return this;
    } else if (n == -1L) {
      return inverse();
    }
    long exp = n;
    if (n < 0) {
      if (F.isNotPresent(n)) {
        throw new java.lang.ArithmeticException();
      }
      exp *= -1;
    }
    int b2pow = 0;

    while ((exp & 1) == 0) {
      b2pow++;
      exp >>= 1;
    }

    IFraction result = this;
    IFraction x = result;

    while ((exp >>= 1) > 0) {
      x = x.mul(x);
      if ((exp & 1) != 0) {
        result.checkBitLength();
        result = result.mul(x);
      }
    }

    while (b2pow-- > 0) {
      result.checkBitLength();
      result = result.mul(result);
    }
    if (n < 0) {
      return result.inverse();
    }
    return result;
  }

  @Override
  public IReal re() {
    return this;
  }

  @Override
  public double reDoubleValue() {
    return doubleValue();
  }

  @Override
  public IRational roundClosest(IReal multiple) {
    if (!multiple.isRational()) {
      multiple = F.fraction(multiple.doubleValue(), Config.DOUBLE_EPSILON);
    }
    IInteger ii = this.divideBy((IRational) multiple).roundExpr();
    return ii.multiply((IRational) multiple);
  }

  /**
   * Return a new rational representing <code>this - other</code>.
   *
   * @param other Rational to subtract.
   * @return Difference of <code>this</code> and <code>other</code>.
   */
  @Override
  public IFraction sub(IFraction other) {
    return add(other.negate());
  }

  /**
   * Returns <code>(this-s)/d</code>.
   *
   * @param s
   * @param d the denominator
   * @return <code>(this-s)/d</code>
   */
  public IFraction subdiv(IFraction s, FractionSym d) {
    return sub(s).div(d);
  }

  /** {@inheritDoc} */
  @Override
  public IRational subtract(IRational that) {
    if (isZero()) {
      return that.negate();
    }
    return this.add(that.negate());
  }

  /** {@inheritDoc} */
  @Override
  public IReal subtractFrom(IReal that) {
    if (that instanceof IRational) {
      return this.add((IRational) that.negate());
    }
    return Num.valueOf(doubleValue() - that.doubleValue());
  }

  /** {@inheritDoc} */
  @Override
  public IExpr times(IExpr that) {
    if (that instanceof INumber) {
      return this.times((INumber) that);
    }
    return IFraction.super.times(that);
  }

  /** {@inheritDoc} */
  @Override
  public INumber times(INumber that) {
    if (that.isOne()) {
      return this;
    }
    if (that.isMinusOne()) {
      return negate();
    }
    if (that.isZero()) {
      return F.C0;
    }
    if (that instanceof IFraction) {
      return this.mul((IFraction) that).normalize();
    }
    if (that instanceof IntegerSym) {
      return this.mul(valueOf(((IntegerSym) that).fIntValue)).normalize();
    }
    if (that instanceof BigIntegerSym) {
      return this.mul(valueOf(((BigIntegerSym) that).fBigIntValue)).normalize();
    }
    if (that instanceof ComplexSym) {
      return ((ComplexSym) that).multiply(ComplexSym.valueOf(this)).normalize();
    }
    if (that instanceof INum) {
      if (that instanceof ApfloatNum) {
        return apfloatNumValue().multiply(((ApfloatNum) that).apfloatNumValue());
      }
      return F.num(((Num) that).value * evalf());
    }
    if (that instanceof IComplexNum) {
      if (that instanceof ApcomplexNum) {
        return apcomplexNumValue().multiply(((ApcomplexNum) that).apcomplexNumValue());
      }
      return F.complexNum(evalfc().multiply(that.evalfc()));
    }
    throw new java.lang.ArithmeticException();
  }

  @Override
  public int toIntRoot() {
    return Config.INVALID_INT;
  }

  @Override
  public int toIntRoot(int defaultValue) {
    return defaultValue;
  }
}
