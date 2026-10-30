package org.matheclipse.core.eval.util;

import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.exception.ASTElementLimitExceeded;
import org.matheclipse.core.eval.exception.BigIntegerLimitExceeded;
import org.matheclipse.core.expression.ApcomplexNum;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.ComplexNum;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.parser.client.ParserConfig;

/**
 * Collects the evaluated terms of a sum or the factors of a product, which are built one after
 * the other from an iterator. Numbers are folded into one number on the fly, everything else is
 * appended to a <code>Plus</code> or <code>Times</code> expression which isn't evaluated.
 *
 * <ul>
 * <li>exact rational numbers are combined pairwise, like a balanced binary tree, so that the
 * operands of an addition or multiplication have about the same size: the sum of the first
 * <code>n</code> reciprocals or the product of the first <code>n</code> integers isn't built from
 * <code>n</code> operations with a growing operand;
 * <li>machine real numbers of a sum are added with compensated summation;
 * <li>an exact <code>0</code> of a product absorbs all finite numbers, the factors after it are
 * still collected;
 * <li>machine real and complex numbers of a product are multiplied with a separate binary
 * exponent, so the
 * product doesn't overflow or underflow on its way, and a result beyond the double range becomes
 * an arbitrary precision number of machine precision;
 * <li>all other numbers (arbitrary precision numbers, exact complex numbers, infinite or NaN machine
 * numbers) are combined in order with {@link INumber#plus(INumber)} or
 * {@link INumber#times(INumber)}.
 * </ul>
 */
public abstract class NumberAccumulator {

  /**
   * A sum accumulator.
   *
   * @param terms the empty <code>Plus</code> which collects the terms which aren't numbers
   * @param limited if <code>true</code> the number of leaves of the collected terms and the bit
   *        length of the exact integer part are limited to <code>Config.MAX_AST_SIZE / 2</code> and
   *        <code>Config.MAX_BIT_LENGTH / 100</code>
   */
  public static NumberAccumulator sum(IASTAppendable terms, boolean limited) {
    return new Sum(terms, limited);
  }

  /**
   * A product accumulator.
   *
   * @param factors the empty <code>Times</code> which collects the factors which aren't numbers
   * @param limited if <code>true</code> the number of leaves of the collected factors and the bit
   *        length of the exact integer part are limited to <code>Config.MAX_AST_SIZE / 2</code> and
   *        <code>Config.MAX_BIT_LENGTH / 100</code>
   */
  public static NumberAccumulator product(IASTAppendable factors, boolean limited) {
    return new Product(factors, limited);
  }

  private static final class Sum extends NumberAccumulator {
    private CompensatedSum real;
    private CompensatedSum imaginary;

    Sum(IASTAppendable terms, boolean limited) {
      super(terms, limited);
    }

    @Override
    protected IRational combineExact(IRational a, IRational b) {
      return a.add(b);
    }

    @Override
    protected INumber combine(INumber a, INumber b) {
      return a.plus(b);
    }

    @Override
    protected boolean addMachine(INumber number) {
      if (number instanceof Num) {
        final double value = ((Num) number).doubleValue();
        if (Double.isFinite(value)) {
          if (real == null) {
            real = new CompensatedSum();
          }
          real.add(value);
          return true;
        }
      } else if (number instanceof ComplexNum) {
        final double re = ((ComplexNum) number).getRealPart();
        final double im = ((ComplexNum) number).getImaginaryPart();
        if (Double.isFinite(re) && Double.isFinite(im)) {
          if (real == null) {
            real = new CompensatedSum();
          }
          if (imaginary == null) {
            imaginary = new CompensatedSum();
          }
          real.add(re);
          imaginary.add(im);
          return true;
        }
      }
      return false;
    }

    @Override
    protected INumber machineValue() {
      if (imaginary != null) {
        return ComplexNum.valueOf(real.value(), imaginary.value());
      }
      return real == null ? null : Num.valueOf(real.value());
    }

    @Override
    protected boolean isExactNeutral(IRational exact) {
      return exact.isZero();
    }

    @Override
    protected IExpr neutral() {
      return F.C0;
    }
  }

  private static final class Product extends NumberAccumulator {
    /** <code>true</code> if a machine number was multiplied in. */
    private boolean hasMachine;
    /** <code>true</code> if a machine complex number was multiplied in. */
    private boolean hasComplex;
    /** The machine product is <code>(re + I*im) * 2^exponent</code>. */
    private double re = 1.0;
    private double im = 0.0;
    private long exponent;

    Product(IASTAppendable factors, boolean limited) {
      super(factors, limited);
    }

    @Override
    protected IRational combineExact(IRational a, IRational b) {
      return a.multiply(b);
    }

    @Override
    protected INumber combine(INumber a, INumber b) {
      return a.times(b);
    }

    @Override
    protected boolean addMachine(INumber number) {
      double a;
      double b;
      if (number instanceof Num) {
        a = ((Num) number).doubleValue();
        b = 0.0;
      } else if (number instanceof ComplexNum) {
        a = ((ComplexNum) number).getRealPart();
        b = ((ComplexNum) number).getImaginaryPart();
      } else {
        return false;
      }
      if (!Double.isFinite(a) || !Double.isFinite(b)) {
        return false;
      }
      hasMachine = true;
      hasComplex |= number instanceof ComplexNum;
      if ((a == 0.0 && b == 0.0) || (re == 0.0 && im == 0.0)) {
        final double r = re * a - im * b;
        im = re * b + im * a;
        re = r;
        return true;
      }
      // the factor and the mantissa both lie in [1, 2) in magnitude after scaling, so their
      // product can't overflow or underflow
      final int factorExponent = Math.getExponent(Math.max(Math.abs(a), Math.abs(b)));
      a = Math.scalb(a, -factorExponent);
      b = Math.scalb(b, -factorExponent);
      final double r = re * a - im * b;
      final double i = re * b + im * a;
      exponent += factorExponent;
      if (r == 0.0 && i == 0.0) {
        re = r;
        im = i;
        return true;
      }
      final int mantissaExponent = Math.getExponent(Math.max(Math.abs(r), Math.abs(i)));
      re = Math.scalb(r, -mantissaExponent);
      im = Math.scalb(i, -mantissaExponent);
      exponent += mantissaExponent;
      return true;
    }

    @Override
    protected INumber machineValue() {
      if (!hasMachine) {
        return null;
      }
      if (re == 0.0 && im == 0.0) {
        return hasComplex ? ComplexNum.valueOf(re, im) : Num.valueOf(re);
      }
      if (exponent > Double.MIN_EXPONENT - 53 && exponent <= Double.MAX_EXPONENT) {
        final double r = Math.scalb(re, (int) exponent);
        final double i = Math.scalb(im, (int) exponent);
        final double larger = Math.max(Math.abs(r), Math.abs(i));
        if (Double.isFinite(larger) && larger != 0.0) {
          return hasComplex ? ComplexNum.valueOf(r, i) : Num.valueOf(r);
        }
      }
      // beyond the double range
      final long precision = ParserConfig.MACHINE_PRECISION;
      final Apfloat scale = ApfloatMath.pow(new Apfloat(2, precision), exponent);
      final Apfloat r = new Apfloat(re, precision).multiply(scale);
      if (!hasComplex) {
        return ApfloatNum.machineOrPromoted(r);
      }
      return ApcomplexNum.valueOf(r, new Apfloat(im, precision).multiply(scale));
    }

    @Override
    protected boolean isExactNeutral(IRational exact) {
      return exact.isOne();
    }

    @Override
    protected boolean isAbsorbing(IRational exact) {
      return exact.isZero();
    }

    @Override
    protected IExpr neutral() {
      return F.C1;
    }
  }

  /**
   * The exact rational numbers combined so far, as a binary counter: <code>exact[i]</code> is the
   * combination of <code>2^i</code> numbers or <code>null</code>.
   */
  private IRational[] exact = new IRational[8];

  /** The numbers which are neither exact rational numbers nor finite machine numbers. */
  private INumber other;

  /** The collected terms or factors which aren't numbers. */
  private final IASTAppendable expressions;

  private final boolean limited;

  /** <code>true</code> if the exact <code>0</code> of a product was multiplied in. */
  private boolean absorbed;

  private long numberOfLeaves;

  private NumberAccumulator(IASTAppendable expressions, boolean limited) {
    this.expressions = expressions;
    this.limited = limited;
  }

  /**
   * Add the evaluated term of a sum or multiply the evaluated factor of a product.
   *
   * @param expr the evaluated term or factor
   */
  public void add(IExpr expr) {
    if (expr.isNumber()) {
      final INumber number = (INumber) expr;
      if (number instanceof IRational) {
        addExact((IRational) number);
      } else if (!addMachine(number)) {
        other = other == null ? number : combine(other, number);
      }
      return;
    }
    if (limited) {
      numberOfLeaves += expr.leafCount() + 1;
      if (numberOfLeaves >= Config.MAX_AST_SIZE / 2) {
        ASTElementLimitExceeded.throwIt(numberOfLeaves);
      }
    }
    expressions.append(expr);
  }

  private void addExact(IRational number) {
    if (absorbed) {
      return;
    }
    if (isAbsorbing(number)) {
      // the exact part stays 0; the factors after it are still collected, a 1/0 among them makes
      // the product Indeterminate
      absorbed = true;
      exact = new IRational[] {number};
      return;
    }
    IRational carry = number;
    int i = 0;
    while (exact[i] != null) {
      carry = combineExact(exact[i], carry);
      checkBitLength(carry);
      exact[i++] = null;
      if (i == exact.length) {
        IRational[] grown = new IRational[2 * exact.length];
        System.arraycopy(exact, 0, grown, 0, exact.length);
        exact = grown;
      }
    }
    exact[i] = carry;
  }

  private void checkBitLength(IRational number) {
    if (limited) {
      checkLimitedBitLength(number);
    } else {
      number.checkBitLength();
    }
  }

  /**
   * Throws {@link BigIntegerLimitExceeded} if <code>number</code> is an integer with more than
   * <code>Config.MAX_BIT_LENGTH / 100</code> bits, the limit of the limited accumulators.
   */
  public static void checkLimitedBitLength(INumber number) {
    if (number instanceof IInteger
        && ((IInteger) number).bitLength() > Config.MAX_BIT_LENGTH / 100) {
      BigIntegerLimitExceeded.throwIt(Config.MAX_BIT_LENGTH / 100);
    }
  }

  /**
   * The folded numbers or <code>null</code> if no number was added.
   */
  public INumber number() {
    IRational exactValue = null;
    for (IRational value : exact) {
      if (value != null) {
        exactValue = exactValue == null ? value : combineExact(value, exactValue);
        checkBitLength(exactValue);
      }
    }
    INumber result = exactValue;
    if (absorbed) {
      // an exact 0 absorbs all finite numbers: 0*1.5 is 0; the finite machine numbers aren't in
      // other, only an infinite or NaN one is
      return other instanceof Num ? combine(result, other) : result;
    }
    if (other != null) {
      result = result == null ? other : combine(result, other);
    }
    final INumber machine = machineValue();
    if (machine != null) {
      result = result == null ? machine : combine(result, machine);
    }
    return result;
  }

  /**
   * The folded number followed by the collected expressions, as a <code>Plus</code> or
   * <code>Times</code> which isn't evaluated. An exact <code>0</code> of a sum or an exact
   * <code>1</code> of a product is left out, and a single element is returned as it is.
   */
  public IExpr result() {
    final INumber number = number();
    if (number == null || (number instanceof IRational && isExactNeutral((IRational) number))) {
      if (expressions.isAST0()) {
        return number == null ? neutral() : number;
      }
      return expressions.oneIdentity0();
    }
    if (expressions.isAST0()) {
      return number;
    }
    expressions.append(1, number);
    return expressions;
  }

  /** <code>true</code> if no expression which isn't a number was collected. */
  public boolean isNumeric() {
    return expressions.isAST0();
  }

  protected abstract IRational combineExact(IRational a, IRational b);

  protected abstract INumber combine(INumber a, INumber b);

  /**
   * Fold a machine number into the machine part.
   *
   * @return <code>false</code> if <code>number</code> isn't a finite machine number which this
   *         accumulator handles itself
   */
  protected abstract boolean addMachine(INumber number);

  /** The machine part or <code>null</code> if no machine number was folded into it. */
  protected abstract INumber machineValue();

  protected abstract boolean isExactNeutral(IRational exact);

  /** <code>true</code> if <code>exact</code> absorbs all other exact numbers. */
  protected boolean isAbsorbing(IRational exact) {
    return false;
  }

  protected abstract IExpr neutral();
}
