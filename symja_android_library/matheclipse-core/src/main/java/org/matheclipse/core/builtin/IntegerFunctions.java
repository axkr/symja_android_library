package org.matheclipse.core.builtin;

import java.math.BigInteger;
import java.util.BitSet;
import java.util.function.Function;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apfloat.Apfloat;
import org.hipparchus.complex.Complex;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ASTElementLimitExceeded;
import org.matheclipse.core.eval.exception.ArgumentTypeException;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.INumeric;
import org.matheclipse.core.expression.ApfloatNum;
import org.matheclipse.core.expression.ComplexNum;
import org.matheclipse.core.expression.ComplexSym;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.IntervalSym;
import org.matheclipse.core.expression.Num;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.StringX;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INumber;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.IReal;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.tensor.qty.IQuantity;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

public class IntegerFunctions {
  private static final Logger LOGGER = LogManager.getLogger();

  /**
   * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation
   * in static initializer</a>
   */
  private static class Initializer {

    private static void init() {
      S.BitLength.setEvaluator(new BitLength());
      S.Ceiling.setEvaluator(new Ceiling());
      S.DigitCount.setEvaluator(new DigitCount());
      S.Floor.setEvaluator(new Floor());
      S.FractionalPart.setEvaluator(new FractionalPart());
      S.FromDigits.setEvaluator(new FromDigits());
      S.IntegerDigits.setEvaluator(new IntegerDigits());
      S.IntegerExponent.setEvaluator(new IntegerExponent());
      S.IntegerLength.setEvaluator(new IntegerLength());
      S.IntegerPart.setEvaluator(new IntegerPart());
      S.Mod.setEvaluator(new Mod());
      S.NumberDigit.setEvaluator(new NumberDigit());
      S.PowerMod.setEvaluator(new PowerMod());
      S.Quotient.setEvaluator(new Quotient());
      S.QuotientRemainder.setEvaluator(new QuotientRemainder());
      S.RealDigits.setEvaluator(new RealDigits());
      S.Round.setEvaluator(new Round());
    }
  }

  private static IAST integerDigits(IInteger n, IInteger base, int padLeftZeros) {
    IASTAppendable list = F.ListAlloc(16);
    if (n.isZero()) {
      list.append(F.C0);
    } else {
      if (base.equals(F.C2)) {
        BitSet bs = integerToBitSet(n);
        int last = 0;
        for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i + 1)) {
          if (i > 0) {
            for (int j = last; j < i; j++) {
              list.append(F.C0);
            }
          }
          last = i + 1;
          list.append(F.C1);
        }
      } else {
        while (n.isPositive()) {
          IInteger mod = n.mod(base);
          list.append(mod);
          n = n.subtract(mod).div(base);
        }
      }
    }

    if (padLeftZeros < list.argSize() && padLeftZeros > 0) {
      IASTAppendable result = F.ListAlloc(list.argSize());
      result = list.reverse(result);
      return result.copyFrom(list.size() - padLeftZeros);
    } else {
      int padSizeZeros = padLeftZeros - list.argSize();
      if (padSizeZeros < 0) {
        padSizeZeros = 0;
      }
      IASTAppendable result = F.ListAlloc(list.argSize() + padSizeZeros);
      for (int i = 0; i < padSizeZeros; i++) {
        result.append(F.C0);
      }
      return list.reverse(result);
    }
  }

  public static BitSet integerToBitSet(int n) {
    BigInteger bn = BigInteger.valueOf(n);
    BitSet bs = fromByteArray(bn.toByteArray());
    return bs;
  }

  public static BitSet integerToBitSet(IInteger n) {
    BigInteger bn = n.toBigNumerator();
    BitSet bs = fromByteArray(bn.toByteArray());
    return bs;
  }

  /**
   * Convert a {@link BigInteger#toByteArray()} byte array to a {@link BitSet}.
   * 
   * @param bytes a {@link BigInteger#toByteArray()} byte array
   * @return
   */
  private static BitSet fromByteArray(byte[] bytes) {
    BitSet bits = new BitSet();
    for (int i = 0; i < bytes.length * 8; i++) {
      if ((bytes[bytes.length - i / 8 - 1] & (1 << (i % 8))) > 0) {
        bits.set(i);
      }
    }
    return bits;
  }

  /**
   *
   *
   * <pre>
   * BitLength(x)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * gives the number of bits needed to represent the integer <code>x</code>. The sign of <code>x
   * </code> is ignored.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; BitLength(1023)
   * 10
   *
   * &gt;&gt; BitLength(100)
   * 7
   *
   * &gt;&gt; BitLength(-5)
   * 3
   *
   * &gt;&gt; BitLength(0)
   * 0
   * </pre>
   */
  private static class BitLength extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isInteger()) {
        IInteger iArg1 = (IInteger) ast.arg1();
        BigInteger big = iArg1.toBigNumerator();
        return F.ZZ(big.bitLength());
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }
  }

  /**
   *
   *
   * <pre>
   * Ceiling(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * gives the first integer greater than or equal <code>expr</code>.
   *
   * </blockquote>
   *
   * <pre>
   * Ceiling(expr, a)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * gives the first multiple of <code>a</code> greater than or equal to <code>expr</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Ceiling(1/3)
   * 1
   *
   * &gt;&gt; Ceiling(-1/3)
   * 0
   *
   * &gt;&gt; Ceiling(1.2)
   * 2
   *
   * &gt;&gt; Ceiling(3/2)
   * 2
   * </pre>
   *
   * <p>
   * For complex <code>expr</code>, take the ceiling of real and imaginary parts.<br>
   *
   * <pre>
   * &gt;&gt; Ceiling(1.3 + 0.7*I)
   * 2+I
   *
   * &gt;&gt; Ceiling(10.4, -1)
   * 10
   *
   * &gt;&gt; Ceiling(-10.4, -1)
   * -11
   * </pre>
   */
  private static final class Ceiling extends AbstractFunctionEvaluator implements INumeric {

    private static final class CeilingPlusFunction implements Function<IExpr, IExpr> {
      @Override
      public IExpr apply(IExpr expr) {
        if (expr.isInteger()) {
          return expr;
        }
        return F.NIL;
      }
    }

    @Override
    public double evalReal(final double[] stack, final int top, final int size) {
      if (size != 1) {
        throw new UnsupportedOperationException();
      }
      return Math.ceil(stack[top]);
    }

    /**
     * Returns the smallest (closest to negative infinity) <code>IReal</code> value that is not less
     * than <code>this</code> and is equal to a mathematical integer.
     *
     * <p>
     * See <a href="http://en.wikipedia.org/wiki/Floor_and_ceiling_functions">Wikipedia - Floor and
     * ceiling functions</a>
     */
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        if (ast.isAST2()) {
          return F.Times(F.Ceiling(F.Divide(ast.arg1(), ast.arg2())), ast.arg2());
        }
        IExpr arg1 = engine.evaluateNIL(ast.arg1());
        if (arg1.isPresent()) {
          return evalCeiling(arg1).orElseGet(() -> F.Ceiling(arg1));
        }
        return evalCeiling(ast.arg1());
      } catch (ArithmeticException ae) {
        // IReal#floor() or #ceil() may throw ArithmeticException
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    public IExpr evalCeiling(IExpr arg1) {
      if (arg1.isNumber()) {
        return ((INumber) arg1).ceilFraction();
      }
      INumber number = arg1.evalNumber();
      if (number != null) {
        return number.ceilFraction();
      }

      if (arg1.isIntegerResult()) {
        return arg1;
      }
      if (arg1.isDirectedInfinity() && arg1.argSize() == 1) {
        return arg1;
      }
      if (arg1.isComplexInfinity()) {
        return arg1;
      }

      if (arg1.isPlus()) {
        IASTAppendable[] splittedPlus = ((IAST) arg1).filterNIL(new CeilingPlusFunction());
        if (splittedPlus[0].size() > 1) {
          if (splittedPlus[1].size() > 1) {
            splittedPlus[0].append(F.Ceiling(splittedPlus[1].oneIdentity0()));
          }
          return splittedPlus[0];
        }
      }
      IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
      if (negExpr.isPresent()) {
        return F.Negate(F.Floor(negExpr));
      }
      if (arg1.isInterval()) {
        return IntervalSym.mapSymbol(S.Ceiling, (IAST) arg1);
      }
      if (arg1.isQuantity()) {
        return arg1.ceil();
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL | ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  private static class DigitCount extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr result = F.NIL;
      int radix = 10;
      if (ast.isAST1()) {
        result = S.IntegerDigits.of(engine, ast.arg1());
      } else if (ast.size() >= 3) {
        if (ast.isAST3() && ast.arg3().isList()) {
          return ast.arg3().mapThread(ast, 3);
        }

        radix = ast.arg2().toIntDefault();
        if (radix <= 0) {
          return F.NIL;
        }
        result = S.IntegerDigits.of(engine, ast.arg1(), ast.arg2());
      }
      if (result.isList()) {
        IAST list = (IAST) result;

        Object2IntOpenHashMap<IExpr> map = new Object2IntOpenHashMap<IExpr>();
        for (int i = 1; i < list.size(); i++) {
          map.addTo(list.get(i), 1);
        }
        if (ast.isAST3()) {
          int index = ast.arg3().toIntDefault();
          if (index > 0 && index < radix) {
            int count = map.getInt(F.ZZ(index));
            return F.ZZ(count);
          }
          return F.NIL;
        }

        if (Config.MAX_AST_SIZE < radix) {
          ASTElementLimitExceeded.throwIt(radix);
        }
        IExpr[] arr = new IExpr[radix];
        for (int i = 0; i < arr.length; i++) {
          arr[i] = F.C0;
        }
        for (Object2IntMap.Entry<IExpr> element : map.object2IntEntrySet()) {
          IExpr key = element.getKey();
          int k = key.toIntDefault();
          if (k == 0) {
            arr[radix - 1] = F.ZZ(element.getIntValue());
          } else if (k > 0 && k < radix) {
            arr[k - 1] = F.ZZ(element.getIntValue());
          } else {
            return F.NIL;
          }
        }
        return F.ast(arr, S.List);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }
  }

  /**
   *
   *
   * <pre>
   * IntegerDigits(n, base)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns a list of integer digits for <code>n</code> under <code>base</code>.
   *
   * </blockquote>
   *
   * <pre>
   * IntegerDigits(n, base, padLeft)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * pads the result list on the left with maximum <code>padLeft</code> zeros.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; IntegerDigits(123)
   * {1,2,3}
   *
   * &gt;&gt; IntegerDigits(-123)
   * {1,2,3}
   *
   * &gt;&gt; IntegerDigits(123, 2)
   * {1,1,1,1,0,1,1}
   *
   * &gt;&gt; IntegerDigits(123, 2, 10)
   * {0,0,0,1,1,1,1,0,1,1}
   *
   * &gt;&gt; IntegerDigits({123,456,789}, 2, 10)
   * {{0,0,0,1,1,1,1,0,1,1},{0,1,1,1,0,0,1,0,0,0},{1,1,0,0,0,1,0,1,0,1}}
   * </pre>
   */
  private static class IntegerDigits extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IInteger base = F.C10;
      int padLeftZeros = 0;
      if (ast.size() >= 3) {
        IExpr arg2 = ast.arg2();
        if (arg2.isInteger() && ((IInteger) arg2).compareInt(1) > 0) {
          base = (IInteger) arg2;
        } else {
          return F.NIL;
        }
      }
      if (ast.size() >= 4) {
        padLeftZeros = ast.arg3().toIntDefault();
        if (padLeftZeros < 0) {
          return F.NIL;
        }
      }
      IExpr arg1 = ast.arg1();
      if (arg1.isInteger()) {
        IInteger n = ((IInteger) arg1).abs();
        return integerDigits(n, base, padLeftZeros);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }
  }

  /**
   *
   *
   * <pre>
   * IntegerExponent(n, b)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * gives the highest exponent of <code>b</code> that divides <code>n</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; IntegerExponent(16, 2)
   * 4
   * &gt;&gt; IntegerExponent(-510000)
   * 4
   * &gt;&gt; IntegerExponent(10, b)
   * IntegerExponent(10, b)
   * </pre>
   */
  private static class IntegerExponent extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IInteger base = F.C10;
      if (ast.isAST2()) {
        IExpr arg2 = ast.arg2();
        if (arg2.isInteger() && ((IInteger) arg2).compareInt(1) > 0) {
          base = (IInteger) arg2;
        } else {
          return F.NIL;
        }
      }
      IExpr arg1 = ast.arg1();
      if (arg1.isInteger()) {
        return ((IInteger) arg1).exponent(base);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }
  }

  /**
   *
   *
   * <pre>
   * Floor(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * gives the smallest integer less than or equal <code>expr</code>.
   *
   * </blockquote>
   *
   * <pre>
   * Floor(expr, a)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * gives the smallest multiple of <code>a</code> less than or equal to <code>expr</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Floor(1/3)
   * 0
   *
   * &gt;&gt; Floor(-1/3)
   * -1
   *
   * &gt;&gt; Floor(10.4)
   * 10
   *
   * &gt;&gt; Floor(10/3)
   * 3
   *
   * &gt;&gt; Floor(10)
   * 10
   *
   * &gt;&gt; Floor(21, 2)
   * 20
   *
   * &gt;&gt; Floor(2.6, 0.5)
   * 2.5
   *
   * &gt;&gt; Floor(-10.4)
   * -11
   * </pre>
   *
   * <p>
   * For complex <code>expr</code>, take the floor of real an imaginary parts.<br>
   *
   * <pre>
   * &gt;&gt; Floor(1.5 + 2.7*I)
   * 1+I*2
   * </pre>
   *
   * <p>
   * For negative <code>a</code>, the smallest multiple of <code>a</code> greater than or equal to
   * <code>expr</code> is returned.<br>
   *
   * <pre>
   * &gt;&gt; Floor(10.4, -1)
   * 11
   *
   * &gt;&gt; Floor(-10.4, -1)
   * -10
   * </pre>
   */
  private static final class Floor extends AbstractFunctionEvaluator implements INumeric {

    private static final class FloorPlusFunction implements Function<IExpr, IExpr> {
      @Override
      public IExpr apply(IExpr expr) {
        if (expr.isInteger()) {
          return expr;
        }
        return F.NIL;
      }
    }

    @Override
    public double evalReal(final double[] stack, final int top, final int size) {
      if (size != 1) {
        throw new UnsupportedOperationException();
      }
      return Math.floor(stack[top]);
    }

    /**
     * Returns the largest (closest to positive infinity) <code>IReal</code> value that is not
     * greater than <code>this</code> and is equal to a mathematical integer.
     *
     * <p>
     * See <a href="http://en.wikipedia.org/wiki/Floor_and_ceiling_functions">Wikipedia - Floor and
     * ceiling functions</a>
     */
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        if (ast.isAST2()) {
          return F.Times(F.Floor(F.Divide(ast.arg1(), ast.arg2())), ast.arg2());
        }
        IExpr arg1 = engine.evaluateNIL(ast.arg1());
        if (arg1.isPresent()) {
          return evalFloor(arg1).orElseGet(() -> F.Floor(arg1));
        }
        return evalFloor(ast.arg1());
      } catch (ArithmeticException ae) {
        // IReal#floor() may throw ArithmeticException
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    public IExpr evalFloor(IExpr arg1) {
      if (arg1.isNumber()) {
        return ((INumber) arg1).floorFraction();
      }
      INumber number = arg1.evalNumber();
      if (number != null) {
        return number.floorFraction();
      }
      if (arg1.isIntegerResult()) {
        return arg1;
      }
      if (arg1.isDirectedInfinity() && arg1.argSize() == 1) {
        return arg1;
      }
      if (arg1.isComplexInfinity()) {
        return arg1;
      }
      if (arg1.isPlus()) {
        IASTAppendable[] splittedPlus = ((IAST) arg1).filterNIL(new FloorPlusFunction());
        if (splittedPlus[0].size() > 1) {
          if (splittedPlus[1].size() > 1) {
            splittedPlus[0].append(F.Floor(splittedPlus[1].oneIdentity0()));
          }
          return splittedPlus[0];
        }
      }
      IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
      if (negExpr.isPresent()) {
        return F.Negate(F.Ceiling(negExpr));
      }
      if (arg1.isInterval()) {
        return IntervalSym.mapSymbol(S.Floor, (IAST) arg1);
      }
      if (arg1.isQuantity()) {
        return arg1.floor();
      }
      return F.NIL;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.HOLDALL | ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  /**
   *
   *
   * <pre>
   * FractionalPart(number)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * get the fractional part of a <code>number</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; FractionalPart(1.5)
   * 0.5
   * </pre>
   */
  private static class FractionalPart extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      if (arg1.isNumber()) {
        // Note: fractional part for a complex number returns a new complex number with the
        // fractional parts of
        // the real and imaginary part.
        return ((INumber) arg1).fractionalPart();
      }
      if (arg1.isInfinity() || arg1.isComplexInfinity()) {
        return F.Interval(F.list(F.C0, F.C1));
      }
      if (arg1.isNegativeInfinity()) {
        return F.Interval(F.list(F.CN1, F.C0));
      }
      if (arg1.isDirectedInfinity(F.CI)) {
        return F.Times(F.CI, F.Interval(F.list(F.C0, F.C1)));
      }
      if (arg1.isDirectedInfinity(F.CNI)) {
        return F.Times(F.CNI, F.Interval(F.list(F.C0, F.C1)));
      }
      if (arg1.isIntegerResult()) {
        return F.C0;
      }
      if (arg1.isQuantity()) {
        IQuantity quantity = (IQuantity) arg1;
        IExpr fractionalPart = S.FractionalPart.of(quantity.value());
        return quantity.ofUnit(fractionalPart);
      }
      IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
      if (negExpr.isPresent()) {
        return F.Negate(F.FractionalPart(negExpr));
      }
      // if (arg1.isPlus() && arg1.first().isInteger()) {
      // }
      try {
        IReal realNumber = arg1.evalReal();
        if (realNumber != null) {
          if (realNumber.isRangeExclExcl(F.CN1, F.C1)) {
            // arg1 is in the interval ]-1, 1[
            return arg1;
          }
          IInteger intValue = realNumber.integerPart();
          return F.Subtract(arg1, intValue);
        } else {
          Complex complexNumber = arg1.evalfc();
          if (complexNumber != null) {
            double re = complexNumber.getReal();
            double im = complexNumber.getImaginary();
            if (re > -1.0 && re < 1.0 && im > -1.0 && im < 1.0) {
              // arg1 is in the interval ]-1, 1[
              return arg1;
            }
            INumber intValue = F.complexNum(complexNumber).integerPart();
            return F.Subtract(arg1, intValue);
          }
        }
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        LOGGER.debug("FractionalPart.evaluate() failed", rex);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.NHOLDALL | ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
    }
  }

  private static class FromDigits extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {
      IExpr base = F.C10;
      if (ast.size() >= 3) {
        base = ast.arg2();
      }
      IExpr arg1 = ast.arg1();
      if (arg1.isList()) {
        IAST list = (IAST) arg1;
        return fromDigits(list, base);
      }
      if (arg1.isString()) {
        StringX str = (StringX) arg1;
        int radix = base.toIntDefault(-1);
        if (radix > 0) {
          try {
            return F.ZZ(new BigInteger(str.toString(), radix));
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            //
          }
        }
        IASTAppendable digitsList = F.ListAlloc(str.length());
        for (int i = 0; i < str.length(); i++) {
          int digit = Integer.MIN_VALUE;
          char ch = str.charAt(i);
          if (ch >= '0' && ch <= '9') {
            digit = Character.digit(ch, radix);
          } else if ((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z')) {
            digit = Character.digit(ch, 36);
          } else {
            return F.NIL;
          }
          if (digit == Integer.MIN_VALUE) {
            return F.NIL;
          }
          digitsList.append(digit);
        }
        return fromDigits(digitsList, base);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    private IExpr fromDigits(IAST list, IExpr radix) {
      IASTAppendable result = F.PlusAlloc(list.size());
      int exp = 0;
      for (int i = list.size() - 1; i >= 1; i--) {
        result.append(list.get(i).abs().times(radix.power(exp++)));
      }
      return result;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {}
  }

  /**
   *
   *
   * <pre>
   * IntegerLength(x)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * gives the number of digits in the base-10 representation of <code>x</code>.
   *
   * </blockquote>
   *
   * <pre>
   * IntegerLength(x, b)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * gives the number of base-<code>b</code> digits in <code>x</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; IntegerLength(123456)
   * 6
   *
   * &gt;&gt; IntegerLength(10^10000)
   * 10001
   *
   * &gt;&gt; IntegerLength(-10^1000)
   * 1001
   * </pre>
   *
   * <p>
   * <code>IntegerLength</code> with base <code>2</code>:
   *
   * <pre>
   * &gt;&gt; IntegerLength(8, 2)
   * 4
   * </pre>
   *
   * <p>
   * Check that <code>IntegerLength</code> is correct for the first 100 powers of 10:
   *
   * <pre>
   * &gt;&gt; IntegerLength /@ (10 ^ Range(100)) == Range(2, 101)
   * True
   * </pre>
   *
   * <p>
   * The base must be greater than <code>1</code>:
   *
   * <pre>
   * &gt;&gt; IntegerLength(3, -2)
   * IntegerLength(3, -2)
   * </pre>
   *
   * <p>
   * '0' is a special case:
   *
   * <pre>
   * &gt;&gt; IntegerLength(0)
   * 0
   *
   * &gt;&gt; IntegerLength /@ (10 ^ Range(100) - 1) == Range(1, 100)
   * True
   * </pre>
   */
  private static class IntegerLength extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isInteger()) {
        IInteger radix = F.C10;
        if (ast.isAST2()) {
          if (ast.arg2().isInteger()) {
            radix = ((IInteger) ast.arg2());
          } else {
            return F.NIL;
          }
        }
        if (radix.isLT(F.C2)) {
          LOGGER.log(engine.getLogLevel(), "IntegerLength: The base must be greater than 1");
          return F.NIL;
        }
        IInteger iArg1 = (IInteger) ast.arg1();
        if (iArg1.isZero()) {
          return F.C1;
        }
        long l = iArg1.integerLength(radix);

        return F.ZZ(l);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }
  }

  /**
   *
   *
   * <pre>
   * IntegerPart(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * for real <code>expr</code> return the integer part of <code>expr</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; IntegerPart(2.4)
   * 2
   *
   * &gt;&gt; IntegerPart(-9/4)
   * -2
   * </pre>
   */
  private static class IntegerPart extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      try {
        IExpr arg1 = ast.arg1();
        if (arg1.isNumber()) {
          // Note: fractional part for a complex number returns a new complex number with the
          // fractional parts
          // of
          // the real and imaginary part.
          return ((INumber) arg1).integerPart();
        }
        if (arg1.isIntegerResult()) {
          return arg1;
        }
        if (arg1.isInfinity() || arg1.isNegativeInfinity() || arg1.isDirectedInfinity(F.CI)
            || arg1.isDirectedInfinity(F.CNI) || arg1.isAST(S.IntegerPart, 2)) {
          return arg1;
        }
        IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
        if (negExpr.isPresent()) {
          return F.Negate(F.IntegerPart(negExpr));
        }
        if (arg1.isInterval()) {
          return IntervalSym.mapSymbol(S.IntegerPart, (IAST) arg1);
        }
        if (arg1.isQuantity()) {
          IQuantity quantity = (IQuantity) arg1;
          IExpr fractionalPart = S.IntegerPart.of(quantity.value());
          return quantity.ofUnit(fractionalPart);
        }

        final IReal realNumber = arg1.evalReal();
        if (realNumber != null) {
          return realNumber.integerPart();
        } else {
          Complex complexNumber = arg1.evalfc();
          if (complexNumber != null) {
            return F.complexNum(complexNumber).integerPart();
          }
        }
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        // IReal#floor() or #ceil() may throw ArithmeticException
        LOGGER.debug("IntegerPart.evaluate() failed", rex);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.NHOLDALL | ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
    }
  }

  /**
   *
   *
   * <pre>
   * Mod(x, m)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * returns <code>x</code> modulo <code>m</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Mod(14, 6)
   * 2
   * &gt;&gt; Mod(-3, 4)
   * 1
   * &gt;&gt; Mod(-3, -4)
   * -3
   * </pre>
   *
   * <p>
   * The argument 0 should be nonzero
   *
   * <pre>
   * &gt;&gt; Mod(5, 0)
   * Mod(5, 0)
   * </pre>
   */
  private static class Mod extends AbstractFunctionEvaluator {

    /**
     * See: <a href="http://en.wikipedia.org/wiki/Modular_arithmetic">Wikipedia - Modular
     * arithmetic</a>
     */
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr m = ast.arg1();
      IExpr n = ast.arg2();
      if (n.isZero()) {
        // Indeterminate expression `1` encountered.
        Errors.printMessage(ast.topHead(), "indet", F.list(ast), engine);
        return S.Indeterminate;
      }
      if (ast.isAST3()) {
        // 3 args
        IExpr d = ast.arg3();
        if (m.isNumber() && n.isNumber() && d.isNumber()) {
          if (m.isInteger() && n.isInteger() && d.isInteger()) {
            IExpr subExpr = ((IReal) m.subtract(d).divide(n)).floorFraction();
            return m.plus(F.CN1.times(n).times(subExpr));
          }
          if (m.isComplex() || n.isComplex() || d.isComplex() || m.isComplexNumeric()
              || n.isComplexNumeric() || d.isComplexNumeric()) {
            // https://mathematica.stackexchange.com/a/114373/21734
            IExpr subExpr = engine.evaluate(F.Divide(F.Subtract(m, d), n));
            IExpr re = S.Round.of(subExpr.re());
            IExpr im = S.Round.of(subExpr.im());
            return F.Plus(m, F.Times(F.CN1, n, re), F.Times(F.CI, im));
          }
        }
        return F.NIL;
      }

      // 2 args
      if (m.isInteger() && n.isInteger()) {
        final IInteger i0 = (IInteger) m;
        final IInteger i1 = (IInteger) n;
        if (i1.isNegative()) {
          return i0.negate().mod(i1.negate()).negate();
        }
        return i0.mod(i1);
      }

      if (m.isReal() && n.isReal()) {
        return F.Subtract(m, F.Times(n, F.Floor(((IReal) m).divideBy((IReal) n))));
      }

      IExpr div = S.Divide.of(engine, m, n);
      if (div.isIndeterminate()) {
        return S.Indeterminate;
      }
      if (div.isNumericFunction(true) //
          || div.isDirectedInfinity() || div.isComplexInfinity()) {
        return F.Subtract(m, F.Times(n, F.Floor(div)));
      }

      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }

  private static class NumberDigit extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      int n = ast.arg2().toIntDefault();
      if (n != Integer.MIN_VALUE) {
        IExpr x = ast.arg1();
        int b = 10;
        if (ast.isAST3()) {
          b = ast.arg3().toIntDefault();
          if (b <= 1) {
            if (ast.arg3().isNumber()) {
              // Base `1` is not a real number greater than 1.
              Errors.printMessage(S.NumberDigit, "rbase", F.List(ast.arg3()), engine);
            }
            return F.NIL;
          }
        }
        IExpr realDigits = realDigits(x);
        if (realDigits.isList2()&&realDigits.isList()) {
          IAST resultList = (IAST) realDigits.first();
          return resultList.get(resultList.argSize() - n);
        }
      }
      return F.NIL;
    }

    @Override
    public int status() {
      return ImplementationStatus.EXPERIMENTAL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }
  }

  /**
   *
   *
   * <pre>
   * PowerMod(x, y, m)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * computes <code>x^y</code> modulo <code>m</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; PowerMod(2, 10000000, 3)
   * 1
   * &gt;&gt; PowerMod(3, -2, 10)
   * 9
   * </pre>
   *
   * <p>
   * 0 is not invertible modulo 2.
   *
   * <pre>
   * &gt;&gt; PowerMod(0, -1, 2)
   * PowerMod(0, -1, 2)
   * </pre>
   *
   * <p>
   * The argument 0 should be nonzero.
   *
   * <pre>
   * &gt;&gt; PowerMod(5, 2, 0)
   *  PowerMod(5, 2, 0)
   * </pre>
   */
  private static class PowerMod extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.exists(x -> !x.isInteger())) {
        return F.NIL;
      }
      final IInteger arg1 = (IInteger) ast.arg1();
      final IInteger arg2 = (IInteger) ast.arg2();
      final IInteger arg3 = (IInteger) ast.arg3();
      try {
        if (arg1.isZero() && arg2.isNegativeResult()) {
          // `1` is not invertible modulo `2`.
          return Errors.printMessage(ast.topHead(), "ninv", F.list(arg1, arg3), engine);
        }
        if (arg3.isZero()) {
          // The argument `1` should be nonzero.
          return Errors.printMessage(ast.topHead(), "divz", F.list(arg3, ast), engine);
        }
        if (arg2.isMinusOne()) {
          return arg1.modInverse(arg3);
        }
        return arg1.modPow(arg2, arg3);
      } catch (ArithmeticException ae) {
        LOGGER.log(engine.getLogLevel(), ast.topHead(), ae);
        return F.NIL;
      }
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_3_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
      super.setUp(newSymbol);
    }
  }

  /**
   *
   *
   * <pre>
   * Quotient(m, n)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * computes the integer quotient of <code>m</code> and <code>n</code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Quotient(23, 7)
   * 3
   * </pre>
   *
   * <p>
   * Infinite expression Quotient(13, 0) encountered.
   *
   * <pre>
   * &gt;&gt; Quotient(13, 0)
   * ComplexInfinity
   *
   * &gt;&gt; Quotient(-17, 7)
   * -3
   *
   * &gt;&gt; Quotient(-17, -4)
   * 4
   *
   * &gt;&gt; Quotient(19, -4)
   * -5
   * </pre>
   */
  private static class Quotient extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr z = engine.evaluate(ast.arg1());
      IExpr n = engine.evaluate(ast.arg2());
      if (n.isZero()) {
        LOGGER.log(engine.getLogLevel(), "Quotient: division by zero");
        return F.CComplexInfinity;
      }
      if (ast.isAST2()) {
        if (z.isInteger() && n.isInteger()) {
          return ((IInteger) z).quotient((IInteger) n);
        }
        if (z.isReal() && n.isReal()) {
          return ((IReal) z).divideBy((IReal) n).floorFraction();
        }
        if (z.isComplex() || n.isComplex()) {
          IComplex c1 = null;
          if (z.isComplex()) {
            c1 = (IComplex) z;
          } else if (z.isRational()) {
            c1 = F.complex((IRational) z);
          }
          if (c1 != null) {
            IComplex c2 = null;
            if (n.isComplex()) {
              c2 = (ComplexSym) n;
            } else if (n.isRational()) {
              c2 = F.complex((IRational) n);
            }
            if (c2 != null) {
              IComplex[] result = c1.quotientRemainder(c2);
              if (result != null) {
                return result[0];
              }
            }
          }
        }

        if (z.isNumericFunction(true) && n.isNumericFunction(true)) {
          try {
            double zDouble = Double.NaN;
            double nDouble = Double.NaN;
            try {
              zDouble = z.evalf();
              nDouble = n.evalf();
            } catch (RuntimeException ve) {
              Errors.rethrowsInterruptException(ve);
            }
            if (Double.isNaN(zDouble) || Double.isNaN(nDouble)) {
              Complex zComplex = z.evalfc();
              Complex nComplex = n.evalfc();
              Complex[] qr = ComplexNum.quotientRemainder(zComplex, nComplex);
              return F.complexNum(qr[0]).floorFraction();
            } else {
              return F.num(zDouble / nDouble).floorFraction();
            }
          } catch (ValidateException ve) {
            return Errors.printMessage(ast.topHead(), ve, engine);
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            LOGGER.log(engine.getLogLevel(), ast.topHead(), rex);
          }
        }
        return F.NIL;
      }
      if (ast.isAST3()) {
        IExpr d = engine.evaluate(ast.arg3());
        if (z.isInteger() && n.isInteger() && d.isInteger()) {
          // TODO implement for 3 args
        }
        return F.NIL;
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
    }
  }

  /**
   *
   *
   * <pre>
   * QuotientRemainder(m, n)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * computes a list of the quotient and remainder from division of <code>m</code> and <code>n
   * </code>.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; QuotientRemainder(23, 7)
   * {3,2}
   * </pre>
   *
   * <p>
   * Infinite expression QuotientRemainder(13, 0) encountered.
   *
   * <pre>
   * &gt;&gt; QuotientRemainder(13, 0)
   * QuotientRemainder(13, 0)
   *
   * &gt;&gt; QuotientRemainder(-17, 7)
   * {-3,4}
   *
   * &gt;&gt; QuotientRemainder(-17, -4)
   * {4,-1}
   *
   * &gt;&gt; QuotientRemainder(19, -4)
   * {-5,-1}
   * </pre>
   */
  private static class QuotientRemainder extends AbstractCoreFunctionEvaluator {

    @Override
    public IExpr evaluate(IAST ast, EvalEngine engine) {

      try {
        IExpr arg1 = engine.evaluate(ast.arg1());
        IExpr arg2 = engine.evaluate(ast.arg2());
        if (arg1.isInteger() && arg2.isInteger()) {
          final IInteger i0 = (IInteger) arg1;
          final IInteger i1 = (IInteger) arg2;
          if (i1.isZero()) {
            LOGGER.log(engine.getLogLevel(), "QuotientRemainder: division by zero");
            return F.NIL;
          }
          IASTMutable list = F.ListAlloc(S.Null, S.Null);

          list.set(1, i0.quotient(i1));
          if (i1.isNegative()) {
            list.set(2, i0.negate().mod(i1.negate()).negate());
            return list;
          }
          list.set(2, i0.mod(i1));
          return list;
        } else if (arg1.isComplex() || arg2.isComplex()) {
          if (arg1.isComplex() || arg2.isComplex()) {
            IComplex c1 = null;
            if (arg1.isComplex()) {
              c1 = (IComplex) arg1;
            } else if (arg1.isRational()) {
              c1 = F.complex((IRational) arg1);
            }
            if (c1 != null) {
              IComplex c2 = null;
              if (arg2.isComplex()) {
                c2 = (ComplexSym) arg2;
              } else if (arg2.isRational()) {
                c2 = F.complex((IRational) arg2);
              }
              if (c2 != null) {
                IComplex[] result = c1.quotientRemainder(c2);
                if (result != null) {
                  return F.list(result[0], result[1]);
                }
              }
            }
          }
        }

        if (arg1.isNumericFunction(true) && arg2.isNumericFunction(true)) {
          try {
            double zDouble = Double.NaN;
            double nDouble = Double.NaN;
            try {
              zDouble = arg1.evalf();
              nDouble = arg2.evalf();
            } catch (RuntimeException ve) {
              Errors.rethrowsInterruptException(ve);
            }
            if (Double.isNaN(zDouble) || Double.isNaN(nDouble)) {
              Complex zComplex = arg1.evalfc();
              Complex nComplex = arg2.evalfc();
              Complex[] qr = ComplexNum.quotientRemainder(zComplex, nComplex);
              return F.list(F.complexNum(qr[0]).floorFraction(), F.complexNum(qr[1]));
            } else {
              IInteger quotient = F.num(zDouble / nDouble).floorFraction();
              IExpr remainder = S.Plus.of(engine, arg1, F.Negate(F.Times(quotient, arg2)));
              return F.list(quotient, remainder);
            }
          } catch (ValidateException ve) {
            return Errors.printMessage(ast.topHead(), ve, engine);
          } catch (RuntimeException rex) {
            Errors.rethrowsInterruptException(rex);
            LOGGER.log(engine.getLogLevel(), ast.topHead(), rex);
          }
        }
      } catch (RuntimeException rex) {
        Errors.rethrowsInterruptException(rex);
        LOGGER.log(engine.getLogLevel(), "QuotientRemainder", rex);
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private IntegerFunctions() {}

  private static class RealDigits extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr arg1 = ast.arg1();
      try {
        IExpr realDigits = realDigits(arg1);
        if (realDigits.isPresent()) {
          return realDigits;
        }
      } catch (NumberFormatException | ArgumentTypeException atex) {
        LOGGER.log(engine.getLogLevel(), ast.topHead(), atex);
        return F.NIL;
      }

      if (arg1.isNumber()) {
        // The value `1` is not a real number.
        return Errors.printMessage(ast.topHead(), "realx", F.list(arg1), engine);
      }
      return F.NIL;
    }



    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE);
    }
  }

  /**
   *
   *
   * <pre>
   * Round(expr)
   * </pre>
   *
   * <blockquote>
   *
   * <p>
   * round a given <code>expr</code> to nearest integer.
   *
   * </blockquote>
   *
   * <h3>Examples</h3>
   *
   * <pre>
   * &gt;&gt; Round(-3.6)
   * -4
   * </pre>
   */
  private static class Round extends AbstractCoreFunctionEvaluator implements INumeric {

    @Override
    public double evalReal(final double[] stack, final int top, final int size) {
      if (size != 1) {
        throw new UnsupportedOperationException();
      }
      return Math.round(stack[top]);
    }

    /**
     * Round a given value to nearest integer.
     *
     * <p>
     * See <a href="http://en.wikipedia.org/wiki/Floor_and_ceiling_functions">Wikipedia - Floor and
     * ceiling functions</a>
     */
    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IASTMutable res = F.NIL;
      try {
        IExpr expr = ast.arg1();
        IExpr temp = engine.evaluateNIL(expr);
        if (temp.isPresent()) {
          expr = temp;
          res = ast.setAtCopy(1, temp);
        }
        if (expr.isList()) {
          return expr.mapThread(ast.setAtCopy(1, F.Slot1), 1);
        }
        if (ast.isAST2()) {
          // Round(expr, k)
          IExpr k = ast.arg2();
          temp = engine.evaluateNIL(k);
          if (temp.isPresent()) {
            if (temp.isNumericFunction() && (expr.isInfinity() || expr.isNegativeInfinity()
                || (expr.isDirectedInfinity() && expr.argSize() == 1)
                || expr.isComplexInfinity())) {
              return expr;
            }
            k = temp;
            if (res.isNIL()) {
              res = ast.setAtCopy(2, temp);
            } else {
              res.set(2, temp);
            }
          } else {
            if (k.isNumericFunction()) {
              if (expr.isInfinity() || expr.isNegativeInfinity()
                  || (expr.isDirectedInfinity() && expr.argSize() == 1)
                  || expr.isComplexInfinity()) {
                return expr;
              }
            } else {
              // Internal precision limit `1` reached while evaluating `2`.
              return Errors.printMessage(S.Round, "meprec", F.List(F.CEmptyString, k), engine);
            }
          }

          temp = round(engine, expr, k);
          if (temp.isPresent()) {
            return temp;
          }
        } else {
          // Round(expr)
          if (expr.isIntegerResult()) {
            return expr;
          }
          INumber number = expr.evalNumber();
          if (number != null) {
            return number.roundExpr();
          }
          if (expr.isInfinity() || expr.isNegativeInfinity() || expr.isDirectedInfinity()
              || expr.isComplexInfinity()) {
            return expr;
          }
          if (expr.isQuantity()) {
            return expr.roundExpr();
          }
          // if (expr.isPlus()) {
          // not used in WMA
          // IASTAppendable[] result = ((IAST) expr).filter(x -> x.isIntegerResult());
          // if (result[0].size() > 1) {
          // if (result[1].size() > 1) {
          // result[0].append(F.Round(result[1]));
          // }
          // return result[0];
          // }
          // }
          IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(expr);
          if (negExpr.isPresent()) {
            return F.Negate(F.Round(negExpr));
          }
          if (expr.isInterval()) {
            return IntervalSym.mapSymbol(S.Round, (IAST) expr);
          }
        }
      } catch (ArithmeticException ae) {
        // IReal#round() may throw ArithmeticException
      }
      return res;
    }

    private IExpr round(EvalEngine engine, IExpr expr, IExpr k) {
      IExpr n = S.Divide.ofNIL(engine, expr, k);
      if (n.isPresent()) {
        if (n.isRealResult() || n.isComplex() || n.isComplexNumeric()) {
          n = S.Round.of(engine, n);
          if (n.isPresent()) {
            return F.Times(n, k);
          }
          // Internal precision limit `1` reached while evaluating `2`.
          return Errors.printMessage(S.Round, "meprec", F.List(F.CEmptyString, k), engine);
        }
        if (n.isComplexInfinity() || n.isIndeterminate()) {
          return S.Indeterminate;
        }
      }
      return F.NIL;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION);
      super.setUp(newSymbol);
    }
  }


  public static IExpr realDigits(IExpr arg1) {
    if (arg1.isInteger()) {
      IInteger number = (IInteger) arg1;
      if (number.isNegative()) {
        number = number.abs();
      }
      IAST list = integerDigits(number, F.C10, 0);
      return F.list(list, F.ZZ(list.size() - 1));
    }
    IReal number = null;
    if (arg1.isReal()) {
      number = (IReal) arg1;
    } else {
      number = arg1.evalReal();
    }
    if (number != null) {
      if (number.isNegative()) {
        number = number.abs();
      }

      if (number instanceof ApfloatNum) {
        Apfloat apfloat = number.apfloatValue();
        String str = apfloat.toString();
        IASTAppendable list = F.ListAlloc(str.length() + 1);
        int numberOfLeftDigits = 0;
        for (int i = 0; i < str.length(); i++) {
          char ch = str.charAt(i);
          if (ch == '.') {
            numberOfLeftDigits = i;
            continue;
          }
          if (ch == 'e' || ch == 'E') {
            String exponentStr = str.substring(i + 1);
            int exponent = Integer.parseInt(exponentStr);

            numberOfLeftDigits += exponent;

            break;
          }
          list.append(ch);
        }

        return F.list(list, F.ZZ(numberOfLeftDigits));
      } else if (number instanceof Num) {
        String str = Double.toString(number.doubleValue());
        IASTAppendable list = F.ListAlloc(str.length() + 1);
        int numberOfLeftDigits = 0;
        for (int i = 0; i < str.length(); i++) {
          char ch = str.charAt(i);
          if (ch == '.') {
            numberOfLeftDigits = i;
            continue;
          }
          if (ch == 'e' || ch == 'E') {
            String exponentStr = str.substring(i + 1);
            int exponent = Integer.parseInt(exponentStr);
            numberOfLeftDigits += exponent;
            break;
          }
          list.append(ch);
        }

        return F.list(list, F.ZZ(numberOfLeftDigits));
      }
    }
    return F.NIL;
  }
}
