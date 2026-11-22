package org.matheclipse.core.expression;

import java.util.HashMap;
import java.util.Map;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;

/**
 * The names of the constants in {@link F}, which <code>internalJavaString()</code> prints instead
 * of the constant's structure, for example <code>C3D2</code> instead of <code>QQ(3L,2L)</code> or
 * <code>C1PSqrt3</code> instead of <code>Plus(C1,CSqrt3)</code>.
 *
 * <p>
 * The generated rule classes (<code>reflection.system.rules</code>, Rubi <code>IntRules*</code>)
 * are printed with these names, so a constant added here must exist as a public field of
 * {@link F}. The tables are created lazily, after {@link F} has been initialized.
 */
final class JavaConstantNames {

  /** The tables, created on first use. */
  private static final class Holder {
    /** Integer value to name. */
    static final Map<Integer, String> INTEGERS = new HashMap<>();
    /** {@link #fractionKey(long, long)} to name. */
    static final Map<Long, String> FRACTIONS = new HashMap<>();
    static final Map<IExpr, String> COMPLEX = new HashMap<>();
    static final Map<IExpr, String> ASTS = new HashMap<>();

    static {
      integer(F.CN10, "CN10");
      integer(F.CN9, "CN9");
      integer(F.CN8, "CN8");
      integer(F.CN7, "CN7");
      integer(F.CN6, "CN6");
      integer(F.CN5, "CN5");
      integer(F.CN4, "CN4");
      integer(F.CN3, "CN3");
      integer(F.CN2, "CN2");
      integer(F.CN1, "CN1");
      integer(F.C0, "C0");
      integer(F.C1, "C1");
      integer(F.C2, "C2");
      integer(F.C3, "C3");
      integer(F.C4, "C4");
      integer(F.C5, "C5");
      integer(F.C6, "C6");
      integer(F.C7, "C7");
      integer(F.C8, "C8");
      integer(F.C9, "C9");
      integer(F.C10, "C10");
      integer(F.C12, "C12");
      integer(F.C15, "C15");
      integer(F.C16, "C16");
      integer(F.C18, "C18");
      integer(F.C20, "C20");
      integer(F.C27, "C27");
      integer(F.C35, "C35");
      integer(F.C100, "C100");
      integer(F.C1000, "C1000");

      fraction(F.C1D2, "C1D2");
      fraction(F.CN1D2, "CN1D2");
      fraction(F.C3D2, "C3D2");
      fraction(F.CN3D2, "CN3D2");
      fraction(F.C5D2, "C5D2");
      fraction(F.CN5D2, "CN5D2");
      fraction(F.C7D2, "C7D2");
      fraction(F.C1D3, "C1D3");
      fraction(F.CN1D3, "CN1D3");
      fraction(F.C2D3, "C2D3");
      fraction(F.CN2D3, "CN2D3");
      fraction(F.C1D4, "C1D4");
      fraction(F.CN1D4, "CN1D4");
      fraction(F.C3D4, "C3D4");
      fraction(F.CN3D4, "CN3D4");
      fraction(F.CN5D4, "CN5D4");
      fraction(F.C1D5, "C1D5");
      fraction(F.CN1D5, "CN1D5");
      fraction(F.C1D6, "C1D6");
      fraction(F.CN1D6, "CN1D6");
      fraction(F.C1D8, "C1D8");
      fraction(F.CN1D8, "CN1D8");
      fraction(F.C5D8, "C5D8");
      fraction(F.C1D12, "C1D12");

      complex(F.CI, "CI");
      complex(F.CNI, "CNI");
      complex(F.CID2, "CID2");
      complex(F.CNID2, "CNID2");
      complex(F.CID4, "CID4");
      complex(F.C2I, "C2I");

      ast(F.CIInfinity, "CIInfinity");
      ast(F.CNIInfinity, "CNIInfinity");
      ast(F.CNPi, "CNPi");
      ast(F.CN2Pi, "CN2Pi");
      ast(F.C2Pi, "C2Pi");
      ast(F.CNPiHalf, "CNPiHalf");
      ast(F.CPiHalf, "CPiHalf");
      ast(F.CNPiThird, "CNPiThird");
      ast(F.CPiThird, "CPiThird");
      ast(F.CNPiQuarter, "CNPiQuarter");
      ast(F.CPiQuarter, "CPiQuarter");
      ast(F.CPiFifth, "CPiFifth");
      ast(F.CPiSixth, "CPiSixth");
      ast(F.CPiEighth, "CPiEighth");
      ast(F.CPiTwelfth, "CPiTwelfth");
      ast(F.CIPi, "CIPi");
      ast(F.CIPiHalf, "CIPiHalf");
      ast(F.C1DPi, "C1DPi");
      ast(F.CPiSqr, "CPiSqr");
      ast(F.CSqrtPi, "CSqrtPi");
      ast(F.C1DSqrtPi, "C1DSqrtPi");
      ast(F.CSqrtPiHalf, "CSqrtPiHalf");
      ast(F.CSqrt2DPi, "CSqrt2DPi");
      ast(F.CSqrt2, "CSqrt2");
      ast(F.CSqrt3, "CSqrt3");
      ast(F.CSqrt5, "CSqrt5");
      ast(F.CSqrt6, "CSqrt6");
      ast(F.CSqrt7, "CSqrt7");
      ast(F.CSqrt10, "CSqrt10");
      ast(F.C1DSqrt2, "C1DSqrt2");
      ast(F.C1DSqrt3, "C1DSqrt3");
      ast(F.C1DSqrt5, "C1DSqrt5");
      ast(F.C1DSqrt6, "C1DSqrt6");
      ast(F.C1DSqrt7, "C1DSqrt7");
      ast(F.C1DSqrt10, "C1DSqrt10");
      ast(F.C1PSqrt3, "C1PSqrt3");
      ast(F.C1MSqrt3, "C1MSqrt3");
      ast(F.CN1PSqrt3, "CN1PSqrt3");
      ast(F.C1PSqrt5, "C1PSqrt5");
      ast(F.C1MSqrt5, "C1MSqrt5");
      ast(F.CN1PSqrt5, "CN1PSqrt5");
      ast(F.CN1MSqrt5, "CN1MSqrt5");
      ast(F.CCbrt2, "CCbrt2");
      ast(F.CCbrt4, "CCbrt4");
      ast(F.CLog2, "CLog2");
      ast(F.CDerivative1, "CDerivative1");
    }

    private static void integer(IInteger value, String name) {
      INTEGERS.put(value.toIntDefault(), name);
    }

    private static void fraction(IFraction value, String name) {
      FRACTIONS.put(fractionKey(value.toBigNumerator().longValueExact(),
          value.toBigDenominator().longValueExact()), name);
    }

    private static void complex(IComplex value, String name) {
      COMPLEX.put(value, name);
    }

    private static void ast(IAST value, String name) {
      ASTS.put(value, name);
    }
  }

  private JavaConstantNames() {}

  private static long fractionKey(long numerator, long denominator) {
    return (numerator << 32) ^ (denominator & 0xFFFFFFFFL);
  }

  /** All names of the table, mapped to the constant. Used by the tests. */
  static Map<String, IExpr> all() {
    Map<String, IExpr> result = new HashMap<>();
    Holder.INTEGERS.forEach((value, name) -> result.put(name, F.ZZ(value)));
    Holder.COMPLEX.forEach((value, name) -> result.put(name, value));
    Holder.ASTS.forEach((value, name) -> result.put(name, value));
    for (Map.Entry<Long, String> entry : Holder.FRACTIONS.entrySet()) {
      long key = entry.getKey();
      result.put(entry.getValue(), F.QQ(key >> 32, key & 0xFFFFFFFFL));
    }
    return result;
  }

  /**
   * The name of the integer constant in {@link F} with this value.
   *
   * @return <code>null</code> if there is none
   */
  static String integer(int value) {
    return Holder.INTEGERS.get(value);
  }

  /**
   * The name of the fraction constant in {@link F} with this normalized numerator and denominator.
   *
   * @return <code>null</code> if there is none
   */
  static String fraction(long numerator, long denominator) {
    if (numerator < Integer.MIN_VALUE || numerator > Integer.MAX_VALUE || denominator <= 0
        || denominator > Integer.MAX_VALUE) {
      return null;
    }
    return Holder.FRACTIONS.get(fractionKey(numerator, denominator));
  }

  /**
   * The name of the complex constant in {@link F} equal to <code>value</code>.
   *
   * @return <code>null</code> if there is none
   */
  static String complex(IComplex value) {
    return Holder.COMPLEX.get(value);
  }

  /**
   * The name of the constant expression in {@link F} structurally equal to <code>ast</code>.
   *
   * @return <code>null</code> if there is none
   */
  static String ast(IAST ast) {
    if (ast.size() > 3) {
      return null;
    }
    IExpr head = ast.head();
    if (head != S.Times && head != S.Plus && head != S.Power && head != S.Log
        && head != S.DirectedInfinity && head != S.Derivative) {
      return null;
    }
    return Holder.ASTS.get(ast);
  }
}
