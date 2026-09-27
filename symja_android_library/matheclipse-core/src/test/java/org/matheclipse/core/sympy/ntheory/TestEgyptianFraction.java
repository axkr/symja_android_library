package org.matheclipse.core.sympy.ntheory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IRational;
import org.matheclipse.core.sympy.exception.ValueError;
import org.matheclipse.core.sympy.ntheory.EgyptianFraction.Algorithm;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

public class TestEgyptianFraction extends ExprEvaluatorTestCase {

  private static void checkEquality(IRational r, Algorithm algorithm) {
    // r == Add(*[Rational(1, i) for i in egyptian_fraction(r, alg)])
    IAST list = EgyptianFraction.egyptianFraction(r, algorithm);
    IExpr sum = F.C0;
    for (int i = 1; i < list.size(); i++) {
      sum = sum.plus(list.get(i).inverse());
    }
    assertEquals(r.toString(), sum.toString());
  }

  @Test
  public void testEgyptianFraction() {
    // https://github.com/sympy/sympy/blob/master/sympy/ntheory/tests/test_egyptian_fraction.py
    for (Algorithm algorithm : Algorithm.values()) {
      checkEquality(F.QQ(3, 7), algorithm);
      checkEquality(F.QQ(5, 6), algorithm);
      checkEquality(F.QQ(5, 121), algorithm);
      checkEquality(F.C1, algorithm);
      if (algorithm == Algorithm.GREEDY || algorithm == Algorithm.GOLOMB) {
        // the remainders of these values have large numerators; the length of a Graham Jewett
        // expansion is 2^numerator - 1
        checkEquality(F.C3, algorithm);
        checkEquality(F.QQ(11, 5), algorithm);
        checkEquality(F.QQ(8, 3), algorithm);
        checkEquality(F.QQ(355, 113), algorithm);
      }
    }

    // assert egyptian_fraction(Rational(3, 7)) == [3, 11, 231]
    assertEquals(EgyptianFraction.egyptianFraction(F.QQ(3, 7)).toString(), //
        "{3,11,231}");
    // assert egyptian_fraction((3, 7), "Graham Jewett") == [7, 8, 9, 56, 57, 72, 3192]
    assertEquals(EgyptianFraction.egyptianFraction(F.QQ(3, 7), Algorithm.GRAHAM_JEWETT).toString(), //
        "{7,8,9,56,57,72,3192}");
    // assert egyptian_fraction((3, 7), "Takenouchi") == [4, 7, 28]
    assertEquals(EgyptianFraction.egyptianFraction(F.QQ(3, 7), Algorithm.TAKENOUCHI).toString(), //
        "{4,7,28}");
    // assert egyptian_fraction((3, 7), "Golomb") == [3, 15, 35]
    assertEquals(EgyptianFraction.egyptianFraction(F.QQ(3, 7), Algorithm.GOLOMB).toString(), //
        "{3,15,35}");
    // assert egyptian_fraction((11, 5), "Golomb") == [1, 2, 3, 4, 9, 234, 1118, 2580]
    assertEquals(EgyptianFraction.egyptianFraction(F.QQ(11, 5), Algorithm.GOLOMB).toString(), //
        "{1,2,3,4,9,234,1118,2580}");

    // assert egyptian_fraction(Rational(4, 17)) == [5, 29, 1233, 3039345]
    assertEquals(EgyptianFraction.egyptianFraction(F.QQ(4, 17)).toString(), //
        "{5,29,1233,3039345}");
    // assert egyptian_fraction(Rational(7, 13), "Greedy") == [2, 26]
    assertEquals(EgyptianFraction.egyptianFraction(F.QQ(7, 13), Algorithm.GREEDY).toString(), //
        "{2,26}");
    // assert egyptian_fraction(Rational(5, 6), "Golomb") == [2, 6, 12, 20, 30]
    assertEquals(EgyptianFraction.egyptianFraction(F.QQ(5, 6), Algorithm.GOLOMB).toString(), //
        "{2,6,12,20,30}");
    // assert egyptian_fraction(Rational(5, 121), "Golomb") == [25, 1225, 3577, 7081, 11737]
    assertEquals(EgyptianFraction.egyptianFraction(F.QQ(5, 121), Algorithm.GOLOMB).toString(), //
        "{25,1225,3577,7081,11737}");

    // common cases that all methods agree on
    for (Algorithm algorithm : Algorithm.values()) {
      assertEquals(EgyptianFraction.egyptianFraction(F.C2, algorithm).toString(), //
          "{1,2,3,6}");
      assertEquals(EgyptianFraction.egyptianFraction(F.QQ(4, 3), algorithm).toString(), //
          "{1,3}");
    }

    // raises(ValueError, lambda: egyptian_fraction(Rational(-4, 9)))
    assertThrows(ValueError.class, () -> EgyptianFraction.egyptianFraction(F.QQ(-4, 9)));
  }
}
