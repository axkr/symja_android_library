package org.matheclipse.core.builtin;

import java.math.BigInteger;
import org.matheclipse.core.annotation.AllowNIL;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.Attribute;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Number theory functions on integers and rationals: <code>AlternatingFactorial</code>,
 * <code>AlternatingHarmonicNumber</code>, <code>Fibonorial</code>, <code>FareySequence</code>,
 * <code>PascalBinomial</code>, <code>MixedFractionParts</code>, <code>RealExponent</code>,
 * <code>RootOfUnityQ</code>, <code>ContinuedFractionK</code> and the inert
 * <code>MixedRadixQuantity</code>.
 */
public final class NumberSequenceFunctions {

  /** The largest argument of the functions which multiply or add that many terms. */
  private static final int MAX_TERMS = 100_000;

  private static class Initializer {

    private static void init() {
      S.AlternatingFactorial.setEvaluator(new AlternatingFactorial());
      S.AlternatingHarmonicNumber.setEvaluator(new AlternatingHarmonicNumber());
      S.Fibonorial.setEvaluator(new Fibonorial());
      S.FareySequence.setEvaluator(new FareySequence());
      S.PascalBinomial.setEvaluator(new PascalBinomial());
      S.MixedFractionParts.setEvaluator(new MixedFractionParts());
      S.RealExponent.setEvaluator(new RealExponent());
      S.RootOfUnityQ.setEvaluator(new RootOfUnityQ());
      S.ContinuedFractionK.setEvaluator(new ContinuedFractionK());
      S.MixedRadixQuantity.setEvaluator(new MixedRadixQuantity());
    }
  }

  /** <code>n</code> if <code>expr</code> is an integer in <code>0..MAX_TERMS</code>, else -1. */
  private static int termCount(IExpr expr) {
    int n = expr.toIntDefault();
    return n >= 0 && n <= MAX_TERMS ? n : -1;
  }

  /** <code>AlternatingFactorial(n)</code> - <code>n! - (n-1)! + (n-2)! - ... ± 1!</code>. */
  private static final class AlternatingFactorial extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.arg1().isInteger() && ast.arg1().isNegative()) {
        // the Gamma function terms of the closed form have poles at the negative integers
        return F.CComplexInfinity;
      }
      int n = termCount(ast.arg1());
      if (n < 0) {
        return F.NIL;
      }
      BigInteger factorial = BigInteger.ONE;
      BigInteger result = BigInteger.ZERO;
      for (int k = 1; k <= n; k++) {
        engine.checkTimeLimit();
        factorial = factorial.multiply(BigInteger.valueOf(k));
        result = factorial.subtract(result);
      }
      return F.ZZ(result);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
    }
  }

  /**
   * <code>AlternatingHarmonicNumber(n)</code>, <code>AlternatingHarmonicNumber(n, r)</code> -
   * <code>1 - 1/2^r + 1/3^r - ... ± 1/n^r</code>. For <code>n = Infinity</code> it is
   * <code>Log(2)</code> for <code>r = 1</code> and <code>(1 - 2^(1-r)) Zeta(r)</code> otherwise.
   */
  private static final class AlternatingHarmonicNumber extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      final IExpr r = ast.isAST2() ? ast.arg2() : F.C1;
      if (ast.arg1().isInfinity()) {
        if (r.isOne()) {
          return F.Log(F.C2);
        }
        if (r.isReal() && r.isPositiveResult()) {
          return F.Times(F.Subtract(F.C1, F.Power(F.C2, F.Subtract(F.C1, r))), F.Zeta(r));
        }
        return F.NIL;
      }
      int n = termCount(ast.arg1());
      if (n < 0 || !r.isNumber()) {
        return F.NIL;
      }
      IASTAppendable sum = F.PlusAlloc(n);
      for (int k = 1; k <= n; k++) {
        engine.checkTimeLimit();
        IExpr term = F.Power(F.ZZ(k), F.Negate(r));
        sum.append(k % 2 == 0 ? F.Negate(term) : term);
      }
      return n == 0 ? F.C0 : sum;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
    }
  }

  /** <code>Fibonorial(n)</code> - <code>Fibonacci(1) Fibonacci(2) ... Fibonacci(n)</code>. */
  private static final class Fibonorial extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      int n = termCount(ast.arg1());
      if (n < 0) {
        return F.NIL;
      }
      BigInteger previous = BigInteger.ZERO;
      BigInteger fibonacci = BigInteger.ONE;
      BigInteger product = BigInteger.ONE;
      for (int k = 1; k <= n; k++) {
        engine.checkTimeLimit();
        product = product.multiply(fibonacci);
        BigInteger next = fibonacci.add(previous);
        previous = fibonacci;
        fibonacci = next;
      }
      return F.ZZ(product);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
    }
  }

  /**
   * <code>FareySequence(n)</code> - the fractions between <code>0</code> and <code>1</code> with
   * denominators up to <code>n</code>, in increasing order. <code>FareySequence(n, k)</code> - the
   * <code>k</code>-th of them.
   */
  private static final class FareySequence extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      int n = ast.arg1().toIntDefault();
      if (n <= 0) {
        if (ast.arg1().isInteger()) {
          // Positive machine-sized integer expected at position `2` in `1`.
          return Errors.printMessage(S.FareySequence, "intpm", F.list(ast, F.C1), engine);
        }
        return F.NIL;
      }
      int rank = -1;
      if (ast.isAST2()) {
        rank = ast.arg2().toIntDefault();
        if (rank <= 0) {
          return F.NIL;
        }
      } else if ((long) n * n > 3L * MAX_TERMS) {
        // the sequence has about 3*n^2/Pi^2 terms
        return F.NIL;
      }
      // the neighbors a/b < c/d give the next term (k c - a)/(k d - b) with k = (n + b) div d
      long a = 0;
      long b = 1;
      long c = 1;
      long d = n;
      IASTAppendable terms = F.ListAlloc();
      terms.append(F.C0);
      int count = 1;
      if (rank == 1) {
        return F.C0;
      }
      while (c <= n) {
        engine.checkTimeLimit();
        long k = (n + b) / d;
        long c2 = k * c - a;
        long d2 = k * d - b;
        a = c;
        b = d;
        c = c2;
        d = d2;
        count++;
        IExpr term = F.QQ(a, b);
        if (rank > 0) {
          if (count == rank) {
            return term;
          }
        } else {
          terms.append(term);
        }
      }
      return rank > 0 ? F.NIL : terms;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }
  }

  /**
   * <code>PascalBinomial(n, k)</code> - the binomial coefficient continued as Pascal's triangle:
   * <code>0</code> for a negative integer <code>k</code>, otherwise <code>Binomial(n, k)</code>.
   */
  private static final class PascalBinomial extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr n = ast.arg1();
      IExpr k = ast.arg2();
      if (k.isInteger() && k.isNegative()) {
        // left of the triangle the value is the exact 0, also for an inexact n
        return F.C0;
      }
      IExpr binomial = engine.evaluate(F.Binomial(n, k));
      return binomial.isAST(S.Binomial) ? F.NIL : binomial;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE, Attribute.NUMERICFUNCTION);
    }
  }

  /**
   * <code>MixedFractionParts(x)</code> - <code>{IntegerPart(x), FractionalPart(x)}</code> of a
   * number.
   */
  private static final class MixedFractionParts extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr x = ast.arg1();
      if (!x.isNumber() && !x.isNumericFunction(true)) {
        return F.NIL;
      }
      return F.list(engine.evaluate(F.IntegerPart(x)), engine.evaluate(F.FractionalPart(x)));
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE);
    }
  }

  /**
   * <code>RealExponent(x)</code>, <code>RealExponent(x, b)</code> - <code>Log(b, Abs(x))</code>
   * as a machine number, with <code>b = 10</code> by default; <code>-Infinity</code> for
   * <code>0</code>.
   */
  private static final class RealExponent extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      double base = 10.0;
      if (ast.isAST2()) {
        if (!ast.arg2().isRealResult()) {
          return F.NIL;
        }
        base = ast.arg2().evalf();
        if (!(base > 1.0) || Double.isInfinite(base)) {
          return F.NIL;
        }
      }
      IExpr x = ast.arg1();
      if (x.isZero()) {
        return F.CNInfinity;
      }
      if (!x.isNumericFunction(true)) {
        return F.NIL;
      }
      IExpr magnitude = engine.evalN(F.Abs(x));
      if (!magnitude.isReal()) {
        return F.NIL;
      }
      double value = magnitude.evalf();
      if (value == 0.0) {
        return F.CNInfinity;
      }
      if (!(value > 0.0) || Double.isInfinite(value)) {
        return F.NIL;
      }
      double exponent = base == 10.0 ? Math.log10(value) : Math.log(value) / Math.log(base);
      return F.num(exponent);
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_2;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.LISTABLE);
    }
  }

  /**
   * <code>RootOfUnityQ(z)</code> - <code>True</code> if the exact number <code>z</code> is a root
   * of unity, i.e. if its minimal polynomial is a cyclotomic polynomial.
   */
  private static final class RootOfUnityQ extends AbstractFunctionEvaluator {

    @Override
    public IExpr evaluate(final IAST ast, EvalEngine engine) {
      IExpr z = ast.arg1();
      if (z.isOne() || z.isMinusOne()) {
        return S.True;
      }
      if (!z.isNumericFunction(true) || z.isInexactNumber() || z.has(x -> x.isInexactNumber(),
          true)) {
        return S.False;
      }
      // a root of unity has absolute value 1
      IExpr absolute = engine.evalN(F.Abs(z));
      if (!absolute.isReal() || Math.abs(absolute.evalf() - 1.0) > 1.0e-8) {
        return S.False;
      }
      ISymbol t = F.Dummy("t");
      IExpr minimal = engine.evaluate(F.binaryAST2(S.MinimalPolynomial, z, t));
      if (!minimal.isPolynomial(t)) {
        return S.False;
      }
      IExpr degreeExpr = engine.evaluate(F.Exponent(minimal, t));
      int degree = degreeExpr.toIntDefault();
      if (degree <= 0) {
        return S.False;
      }
      // EulerPhi(n) >= Sqrt(n/2), so n <= 2 degree^2
      long bound = 2L * degree * degree + 2;
      for (int n = 1; n <= bound; n++) {
        engine.checkTimeLimit();
        if (engine.evaluate(F.EulerPhi(F.ZZ(n))).toIntDefault() != degree) {
          continue;
        }
        IExpr difference =
            engine.evaluate(F.Expand(F.Subtract(minimal, F.binaryAST2(S.Cyclotomic, F.ZZ(n), t))));
        if (difference.isZero()) {
          return S.True;
        }
      }
      return S.False;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_1_1;
    }
  }

  /**
   * <code>ContinuedFractionK(g, {k, kmin, kmax})</code> -
   * <code>1/(g(kmin) + 1/(g(kmin+1) + ...))</code>;
   * <code>ContinuedFractionK(f, g, {k, kmin, kmax})</code> -
   * <code>f(kmin)/(g(kmin) + f(kmin+1)/(g(kmin+1) + ...))</code>. With finite integer bounds the
   * fraction is evaluated from the inside out; with constant positive <code>f</code> and
   * <code>g</code> and an infinite upper bound it is the positive fixed point of
   * <code>x = f/(g + x)</code>.
   */
  private static final class ContinuedFractionK extends AbstractCoreFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.argSize() < 2 || ast.argSize() > 3) {
        return engine.checkBuiltinArgsSize(ast, this);
      }
      final IExpr iterator = ast.last();
      if (!iterator.isList() || iterator.argSize() < 2 || iterator.argSize() > 3
          || !iterator.first().isSymbol()) {
        return F.NIL;
      }
      final ISymbol k = (ISymbol) iterator.first();
      final IExpr numerator = ast.isAST3() ? ast.arg1() : F.C1;
      final IExpr denominator = ast.isAST3() ? ast.arg2() : ast.arg1();
      IExpr kmin = iterator.argSize() == 3 ? engine.evaluate(iterator.second()) : F.C1;
      IExpr kmax = engine.evaluate(iterator.last());
      if (kmax.isInfinity()) {
        if (numerator.isFree(k) && denominator.isFree(k)) {
          IExpr f = engine.evaluate(numerator);
          IExpr g = engine.evaluate(denominator);
          if (f.isPositiveResult() && g.isPositiveResult()) {
            // x = f/(g + x), the positive root of x^2 + g x - f = 0
            return F.Times(F.C1D2,
                F.Plus(F.Negate(g), F.Sqrt(F.Plus(F.Sqr(g), F.Times(F.C4, f)))));
          }
        }
        return F.NIL;
      }
      int from = kmin.toIntDefault();
      int to = kmax.toIntDefault();
      if (from == Integer.MIN_VALUE || to == Integer.MIN_VALUE || (long) to - from > MAX_TERMS) {
        return F.NIL;
      }
      IExpr result = F.C0;
      for (int i = to; i >= from; i--) {
        engine.checkTimeLimit();
        IExpr f = engine.evaluate(F.subst(numerator, k, F.ZZ(i)));
        IExpr g = engine.evaluate(F.subst(denominator, k, F.ZZ(i)));
        result = engine.evaluate(F.Divide(f, F.Plus(g, result)));
      }
      return result;
    }

    @Override
    public int[] expectedArgSize(IAST ast) {
      return ARGS_2_3;
    }

    @Override
    public void setUp(final ISymbol newSymbol) {
      newSymbol.setAttributes(Attribute.HOLDALL);
    }
  }

  /**
   * <code>MixedRadixQuantity({d1, d2, ...}, {u1, u2, ...})</code> - a quantity written with
   * digits in several units, like <code>5</code> feet <code>3</code> inches. It has no evaluation
   * of its own.
   */
  private static final class MixedRadixQuantity extends AbstractFunctionEvaluator {

    @Override
    public @AllowNIL IExpr evaluate(final IAST ast, EvalEngine engine) {
      if (ast.isAST2() && ast.arg1().isList() && ast.arg2().isList()
          && ast.arg1().argSize() == ast.arg2().argSize() && ast.arg1().argSize() > 0) {
        // the quantity with the mixed unit
        return F.binaryAST2(S.Quantity, F.unaryAST1(S.MixedMagnitude, ast.arg1()),
            F.unaryAST1(S.MixedUnit, ast.arg2()));
      }
      return F.NIL;
    }
  }

  public static void initialize() {
    Initializer.init();
  }

  private NumberSequenceFunctions() {}
}
