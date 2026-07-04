package org.matheclipse.core.reflection.system;

import java.math.BigInteger;
import java.util.SortedMap;
import java.util.function.IntUnaryOperator;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ImplementationStatus;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.numbertheory.GaussianInteger;

public class PrimeNu extends AbstractFunctionOptionEvaluator implements IntUnaryOperator {
  @Override
  public int applyAsInt(int arg) {
    if (arg == 0 || arg == 1 || arg == -1) {
      return 0;
    }
    long absArg = Math.abs((long) arg);
    return Config.PRIME_FACTORS.factorInteger(java.math.BigInteger.valueOf(absArg)).size();
  }

  @Override
  public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
      IAST originalAST) {
    IExpr arg1 = ast.arg1();

    if (arg1.isList()) {
      return arg1.mapThread(ast, 1);
    }

    if (arg1.isZero()) {
      return F.NIL;
    }

    if (arg1.isOne() || arg1.isMinusOne()) {
      return F.C0;
    }

    // Parse GaussianIntegers option
    boolean useGaussian = options[0].isTrue() || arg1.isComplex();

    if (useGaussian) {
      BigInteger re = BigInteger.ONE;
      BigInteger im = BigInteger.ZERO;

      IAST factors = F.NIL;

      if (arg1.isInteger()) {
        re = ((IInteger) arg1).toBigNumerator();
        factors = GaussianInteger.factorize((IInteger) arg1, re, im);
      } else if (arg1.isComplex()) {
        IComplex c = (IComplex) arg1;
        IRational real = c.getRealPart();
        IRational imaginary = c.getImaginaryPart();
        if (real.isInteger() && imaginary.isInteger()) {
          re = ((IInteger) real).toBigNumerator();
          im = ((IInteger) imaginary).toBigNumerator();
          factors = GaussianInteger.factorize(c, re, im);
        } else {
          return F.NIL;
        }
      } else {
        return F.NIL;
      }

      int primeCount = 0;
      int factorSize = factors.argSize();

      for (int i = 1; i <= factorSize; i++) {
        IAST factorList = (IAST) factors.get(i);
        IExpr primeOrUnit = factorList.arg1();

        // Skip Gaussian units: 1, -1, I, -I
        if (primeOrUnit.equals(F.C1) || primeOrUnit.equals(F.CN1) || primeOrUnit.equals(F.CI)
            || primeOrUnit.equals(F.CNI)) {
          continue;
        }
        primeCount++;
      }
      return F.ZZ(primeCount);
    }

    // Standard prime factorization
    if (arg1.isInteger()) {
      if (arg1.isNegative()) {
        arg1 = arg1.negate();
      }

      SortedMap<BigInteger, Integer> map =
          Config.PRIME_FACTORS.factorInteger(((IInteger) arg1).toBigNumerator());

      return F.ZZ(map.size());
    } else {
      IExpr negExpr = AbstractFunctionEvaluator.getNormalizedNegativeExpression(arg1);
      if (negExpr.isPresent()) {
        return F.unaryAST1(S.PrimeNu, negExpr);
      }
    }

    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_1;
  }

  @Override
  public int status() {
    return ImplementationStatus.FULL_SUPPORT;
  }

  @Override
  public void setUp(final ISymbol newSymbol) {
    newSymbol.setAttributes(ISymbol.LISTABLE);
    setOptions(newSymbol, //
        new IBuiltInSymbol[] {S.GaussianIntegers}, //
        new IExpr[] {S.False});
  }
}
