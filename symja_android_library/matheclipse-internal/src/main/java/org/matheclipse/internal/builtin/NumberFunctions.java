package org.matheclipse.internal.builtin;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IPair;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.internal.InternalFunction;

/** Number functions of the <code>Internal`</code> context. */
public class NumberFunctions {

  /** <code>Internal`Square(x)</code> - <code>x^2</code>; threads over lists. */
  private static final class Square extends InternalFunction {
    Square() {
      super(1, 1);
    }

    @Override
    protected IExpr evaluateArgs(IAST ast, EvalEngine engine) {
      return engine.evaluate(F.Sqr(ast.arg1()));
    }
  }

  /** <code>Internal`AbsSquare(z)</code> - <code>Abs(z)^2</code>; threads over lists. */
  private static final class AbsSquare extends InternalFunction {
    AbsSquare() {
      super(1, 1);
    }

    @Override
    protected IExpr evaluateArgs(IAST ast, EvalEngine engine) {
      return engine.evaluate(F.Sqr(F.Abs(ast.arg1())));
    }
  }

  /**
   * <code>Internal`StringToDouble("text")</code> - the machine real number written in
   * <code>text</code>, which may use the exponent marks <code>e</code> or <code>*^</code>, or
   * <code>$Failed</code>.
   */
  private static final class StringToDouble extends InternalFunction {
    StringToDouble() {
      super(1, 1);
    }

    @Override
    protected IExpr evaluateArgs(IAST ast, EvalEngine engine) {
      if (!ast.arg1().isString()) {
        return F.NIL;
      }
      String text = ast.arg1().toString().trim().replace("*^", "e");
      try {
        return F.num(Double.parseDouble(text));
      } catch (NumberFormatException e) {
        return S.$Failed;
      }
    }
  }

  /**
   * <code>Internal`PerfectPower(n)</code> - <code>{m, p}</code> with the largest exponent
   * <code>p</code> for which <code>n == m^p</code>; <code>{n, 1}</code> if <code>n</code> is no
   * perfect power.
   */
  private static final class PerfectPower extends InternalFunction {
    PerfectPower() {
      super(1, 1);
    }

    @Override
    protected IExpr evaluateArgs(IAST ast, EvalEngine engine) {
      if (!ast.arg1().isInteger()) {
        return F.NIL;
      }
      IInteger n = (IInteger) ast.arg1();
      IInteger magnitude = n.abs();
      if (magnitude.isOne() || magnitude.isZero()) {
        return F.list(n, F.C1);
      }
      boolean negative = n.isNegative();
      for (int p = (int) magnitude.bitLength(); p >= 2; p--) {
        if (negative && (p & 1) == 0) {
          // an even power is not negative
          continue;
        }
        IPair root = magnitude.nthRoot(p);
        if (root.second().isTrue()) {
          IInteger m = (IInteger) root.first();
          return F.list(negative ? m.negate() : m, F.ZZ(p));
        }
      }
      return F.list(n, F.C1);
    }
  }

  /**
   * <code>Internal`CompareNumeric(prec, a, b)</code> - <code>-1</code>, <code>0</code> or
   * <code>1</code> as <code>a</code> is less than, equal to or greater than <code>b</code>. Two
   * inexact numbers are equal if they agree to the smaller of their precisions less
   * <code>prec</code> decimal digits.
   */
  private static final class CompareNumeric extends InternalFunction {
    /** The decimal digits of a machine real number, <code>53*Log10(2)</code>. */
    private static final double MACHINE_DIGITS = 53 * Math.log10(2.0);

    CompareNumeric() {
      super(3, 3);
    }

    @Override
    protected IExpr evaluateArgs(IAST ast, EvalEngine engine) {
      IExpr a = ast.arg2();
      IExpr b = ast.arg3();
      if (!ast.arg1().isReal() || !a.isReal() || !b.isReal()) {
        return F.NIL;
      }
      IExpr difference = engine.evaluate(F.Subtract(a, b));
      if (!difference.isReal()) {
        return F.NIL;
      }
      int sign = difference.isZero() ? 0 : difference.isNegative() ? -1 : 1;
      if (sign == 0 || (a.isExactNumber() && b.isExactNumber())) {
        return F.ZZ(sign);
      }
      double digits =
          Math.min(precision(a, engine), precision(b, engine)) - ast.arg1().evalf();
      double magnitude = Math.max(Math.abs(a.evalf()), Math.abs(b.evalf()));
      double tolerance = magnitude * Math.pow(10.0, -digits);
      return F.ZZ(Math.abs(difference.evalf()) <= tolerance ? 0 : sign);
    }

    /** The precision of <code>x</code> in decimal digits; infinite for an exact number. */
    private static double precision(IExpr x, EvalEngine engine) {
      IExpr precision = engine.evaluate(F.unaryAST1(S.Precision, x));
      if (precision.isReal()) {
        return precision.evalf();
      }
      if (precision == S.MachinePrecision) {
        return MACHINE_DIGITS;
      }
      return Double.POSITIVE_INFINITY;
    }
  }

  /**
   * <code>Internal`BinomialPrimePowerDecomposition(n, m)</code> - the prime factorization of
   * <code>Binomial(n, m)</code> as <code>FactorInteger</code> gives it.
   */
  private static final class BinomialPrimePowerDecomposition extends InternalFunction {
    BinomialPrimePowerDecomposition() {
      super(2, 2);
    }

    @Override
    protected IExpr evaluateArgs(IAST ast, EvalEngine engine) {
      if (!ast.arg1().isInteger() || !ast.arg2().isInteger()) {
        return F.NIL;
      }
      IExpr binomial = engine.evaluate(F.Binomial(ast.arg1(), ast.arg2()));
      if (!binomial.isInteger() || binomial.isZero()) {
        return F.NIL;
      }
      return engine.evaluate(F.FactorInteger(binomial));
    }
  }

  /**
   * <code>Internal`DiracGammaMatrix(n)</code> - the Dirac matrix <code>gamma^n</code>,
   * <code>n = 0, 1, 2, 3</code>, in the Dirac basis for the metric
   * <code>{1, -1, -1, -1}</code>; <code>n = 5</code> gives <code>gamma^5</code>.
   *
   * <p>
   * Options: <code>"Basis" -&gt; "Chiral"</code> gives the matrices of the Weyl basis.
   * <code>"Metric" -&gt; {s0, s1, s2, s3}</code> scales each matrix by <code>I</code> where its
   * square has to be <code>si</code> rather than the sign of the standard metric.
   */
  private static final class DiracGammaMatrix extends InternalFunction {
    DiracGammaMatrix() {
      super(1, 3);
    }

    @Override
    protected IExpr evaluateArgs(IAST ast, EvalEngine engine) {
      int n = ast.arg1().toIntDefault();
      if (n < 0 || n > 5 || n == 4) {
        return F.NIL;
      }
      boolean chiral = false;
      IExpr metric = F.List(F.C1, F.CN1, F.CN1, F.CN1);
      for (int i = 2; i < ast.size(); i++) {
        IExpr option = ast.get(i);
        if (!option.isRuleAST() || !option.first().isString()) {
          return F.NIL;
        }
        String name = option.first().toString();
        if (name.equals("Basis")) {
          String basis = option.second().toString();
          if (basis.equals("Chiral") || basis.equals("Weyl")) {
            chiral = true;
          } else if (!basis.equals("Dirac")) {
            return F.NIL;
          }
        } else if (name.equals("Metric")) {
          metric = option.second();
          if (!metric.isList() || metric.argSize() != 4
              || !((IAST) metric).forAll(x -> x.isOne() || x.isMinusOne())) {
            return F.NIL;
          }
        } else {
          return F.NIL;
        }
      }
      IExpr[][] gamma = new IExpr[4][];
      for (int k = 0; k <= 3; k++) {
        gamma[k] = standard(k, chiral);
        // the square of the standard matrix is +1 for k == 0 and -1 otherwise
        int standardSign = k == 0 ? 1 : -1;
        if (metric.getAt(k + 1).toIntDefault() != standardSign) {
          for (int j = 0; j < 16; j++) {
            gamma[k][j] = engine.evaluate(F.Times(F.CI, gamma[k][j]));
          }
        }
      }
      if (n == 5) {
        IExpr product = F.Times(F.CI,
            F.Dot(matrix(gamma[0]), matrix(gamma[1]), matrix(gamma[2]), matrix(gamma[3])));
        return engine.evaluate(product);
      }
      return matrix(gamma[n]);
    }

    /** The 16 entries of the standard matrix <code>gamma^k</code>, row by row. */
    private static IExpr[] standard(int k, boolean chiral) {
      IExpr o = F.C0;
      IExpr l = F.C1;
      IExpr m = F.CN1;
      IExpr i = F.CI;
      IExpr j = F.CNI;
      switch (k) {
        case 0:
          return chiral ? new IExpr[] {o, o, l, o, o, o, o, l, l, o, o, o, o, l, o, o}
              : new IExpr[] {l, o, o, o, o, l, o, o, o, o, m, o, o, o, o, m};
        case 1:
          return new IExpr[] {o, o, o, l, o, o, l, o, o, m, o, o, m, o, o, o};
        case 2:
          return new IExpr[] {o, o, o, j, o, o, i, o, o, i, o, o, j, o, o, o};
        default:
          return new IExpr[] {o, o, l, o, o, o, o, m, m, o, o, o, o, l, o, o};
      }
    }

    private static IAST matrix(IExpr[] entries) {
      return F.List(F.List(entries[0], entries[1], entries[2], entries[3]),
          F.List(entries[4], entries[5], entries[6], entries[7]),
          F.List(entries[8], entries[9], entries[10], entries[11]),
          F.List(entries[12], entries[13], entries[14], entries[15]));
    }
  }

  static void init() {
    InternalFunctions.define("Square", ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION, new Square());
    InternalFunctions.define("AbsSquare", ISymbol.LISTABLE | ISymbol.NUMERICFUNCTION,
        new AbsSquare());
    InternalFunctions.define("StringToDouble", ISymbol.NOATTRIBUTE, new StringToDouble());
    InternalFunctions.define("PerfectPower", ISymbol.NOATTRIBUTE, new PerfectPower());
    InternalFunctions.define("CompareNumeric", ISymbol.NOATTRIBUTE, new CompareNumeric());
    InternalFunctions.define("BinomialPrimePowerDecomposition", ISymbol.NOATTRIBUTE,
        new BinomialPrimePowerDecomposition());
    InternalFunctions.define("DiracGammaMatrix", ISymbol.NOATTRIBUTE, new DiracGammaMatrix());
  }

  private NumberFunctions() {}
}
