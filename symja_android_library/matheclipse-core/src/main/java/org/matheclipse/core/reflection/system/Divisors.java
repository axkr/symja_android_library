package org.matheclipse.core.reflection.system;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionOptionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IBigNumber;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.numbertheory.GaussianInteger;

/**
 * <pre>
 * Divisors(n)
 * </pre>
 *
 * <blockquote>
 *
 * <p>
 * gives a list of the integers that divide <code>n</code>.
 * </p>
 *
 * </blockquote>
 *
 * <h3>Examples</h3>
 *
 * <pre>
 * >> Divisors(12)
 * {1, 2, 3, 4, 6, 12}
 *
 * >> Divisors(6 + 4*I)
 * {1, 1+I, 1+5*I, 2, 3+2*I, 6+4*I}
 * </pre>
 */
public class Divisors extends AbstractFunctionOptionEvaluator {

  @Override
  public IExpr evaluate(IAST ast, int argSize, IExpr[] options, EvalEngine engine,
      IAST originalAST) {
    IExpr arg1 = ast.arg1();
    boolean gaussianIntegers = options[0].isTrue();

    if (arg1.isInteger() && !arg1.isZero()) {
      IInteger i = (IInteger) arg1;
      if (gaussianIntegers) {
        return gaussianDivisors(i, i.toBigNumerator(), BigInteger.ZERO, engine);
      }
      if (i.isNegative()) {
        i = i.negate();
      }
      return i.divisors();
    } else if (arg1.isComplex()) {
      IComplex c = (IComplex) arg1;
      IRational re = c.re();
      IRational im = c.im();
      if (re.isInteger() && im.isInteger()) {
        return gaussianDivisors(c, ((IInteger) re).toBigNumerator(),
            ((IInteger) im).toBigNumerator(), engine);
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
    newSymbol.setAttributes(ISymbol.LISTABLE);
    setOptions(newSymbol, new IBuiltInSymbol[] {S.GaussianIntegers}, new IExpr[] {S.False});
  }

  /**
   * Generates all divisors for a given Gaussian Integer.
   */
  private static IAST gaussianDivisors(IBigNumber z, BigInteger re, BigInteger im,
      EvalEngine engine) {
    IAST factorList = GaussianInteger.factorize(z, re, im);
    List<IExpr> primes = new ArrayList<>();
    List<Integer> exponents = new ArrayList<>();

    for (int i = 1; i <= factorList.argSize(); i++) {
      IAST f_e = (IAST) factorList.get(i);
      IExpr prime = f_e.arg1();
      int exp = f_e.arg2().toIntDefault();

      // Ignore units
      if (prime.equals(F.C1) || prime.equals(F.CN1) || prime.equals(F.CI) || prime.equals(F.CNI)) {
        continue;
      }
      primes.add(prime);
      exponents.add(exp);
    }

    IASTAppendable divisors = F.ListAlloc();
    generateDivisors(0, F.C1, primes, exponents, divisors, engine);

    return (IAST) engine.evaluate(F.Sort(divisors));
  }

  /**
   * Recursively computes power permutations of all extracted prime factors.
   */
  private static void generateDivisors(int index, IExpr currentProduct, List<IExpr> primes,
      List<Integer> exponents, IASTAppendable divisors, EvalEngine engine) {
    if (index == primes.size()) {
      divisors.append(normalizeGaussian(currentProduct));
      return;
    }

    IExpr prime = primes.get(index);
    int maxExp = exponents.get(index);
    IExpr term = F.C1;

    for (int i = 0; i <= maxExp; i++) {
      IExpr nextProduct = engine.evaluate(F.Times(currentProduct, term));
      generateDivisors(index + 1, nextProduct, primes, exponents, divisors, engine);
      term = engine.evaluate(F.Times(term, prime));
    }
  }

  /**
   * Normalizes the Gaussian divisor so it aligns on the positive real axis (Quadrant 0).
   */
  private static IExpr normalizeGaussian(IExpr expr) {
    IInteger re = F.C0;
    IInteger im = F.C0;
    if (expr.isInteger()) {
      re = (IInteger) expr;
    } else if (expr.isComplex()) {
      IComplex c = (IComplex) expr;
      re = (IInteger) c.re();
      im = (IInteger) c.im();
    } else {
      return expr;
    }
    GaussianInteger gi = new GaussianInteger(re, im);
    GaussianInteger norm = gi.normalize();

    if (norm.im().isZero()) {
      return norm.re();
    }
    return F.CC(norm.re(), norm.im());
  }
}
