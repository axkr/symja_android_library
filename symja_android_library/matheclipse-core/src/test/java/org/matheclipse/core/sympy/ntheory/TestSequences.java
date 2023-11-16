package org.matheclipse.core.sympy.ntheory;

import org.junit.Test;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.sympy.series.Sequences;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

import static org.junit.Assert.assertEquals;

public class TestSequences extends ExprEvaluatorTestCase {

  @Test
  public void test_find_linear_recurrence001() {
    // sequence(n**2).find_linear_recurrence(10)== [3, -3, 1]
    assertEquals(Sequences.sequence(F.Power(F.n, 2)).find_linear_recurrence(10).toString(), //
        "{3,-3,1}");

    // assert sequence((0, 1, 1, 2, 3, 5, 8, 13, 21, 34, 55), \
    // (n, 0, 10)).find_linear_recurrence(11) == [1, 1]
    assertEquals(
        Sequences.sequence(F.List(0, 1, 1, 2, 3, 5, 8, 13, 21, 34, 55), F.List(F.n, F.C0, F.C10))
            .find_linear_recurrence(10).toString(), //
        "{1,1}");

    // assert sequence((1, 2, 4, 7, 28, 128, 582, 2745, 13021, 61699, 292521, \
    // 1387138), (n, 0, 11)).find_linear_recurrence(12) == [5, -2, 6, -11]
    assertEquals(
        Sequences.sequence(F.List(1, 2, 4, 7, 28, 128, 582, 2745, 13021, 61699, 292521, 1387138), //
            F.List(F.n, F.C0, F.ZZ(11))).find_linear_recurrence(12).toString(), //
        "{5,-2,6,-11}");

    // TODO
    // assert sequence(x*n**3+y*n, (n, 0, oo)).find_linear_recurrence(10) \
    // == [4, -6, 4, -1]
    // assertEquals(
    // Sequences.sequence(F.Plus(F.Times(F.x, F.Power(F.n, 3)), F.Times(F.y, F.n)),
    // F.List(F.n, F.C0, F.CInfinity)).find_linear_recurrence(10).toString(), //
    // "{4,-6,4,1}");

    // assert sequence(x**n, (n,0,20)).find_linear_recurrence(21) == [x]
    assertEquals(
        Sequences.sequence(F.Power(F.x, F.n), F.List(F.n, F.C0, F.ZZ(20)))
            .find_linear_recurrence(21).toString(), //
        "{x}");
    // assert sequence((1,2,3)).find_linear_recurrence(10, 5) == [0, 0, 1]
    assertEquals(
        Sequences.sequence(F.List(1, 2, 3)).find_linear_recurrence(10, F.C5, F.NIL).toString(), //
        "{0,0,1}");

    // TODO Simplify problem?
    // assert sequence(((1 + sqrt(5))/2)**n + (-(1 + sqrt(5))/2)**(-n)).find_linear_recurrence(10)
    // == [1, 1]
    // assertEquals(
    // Sequences.sequence(F.Plus(F.Power(F.Times(F.C1D2, F.Plus(F.C1, F.Sqrt(F.C5))), F.n), //
    // F.Power(F.Times(F.CN1D2, F.Plus(F.C1, F.Sqrt(F.C5))), F.Negate(F.n))))
    // .find_linear_recurrence(10).toString(), //
    // " ");


    // assert sequence(x*((1 + sqrt(5))/2)**n + y*(-(1 + sqrt(5))/2)**(-n), \
    // (n,0,oo)).find_linear_recurrence(10) == [1, 1]
    // assert sequence((1,2,3,4,6),(n, 0, 4)).find_linear_recurrence(5) == []
    assertEquals(
        Sequences.sequence(F.List(1, 2, 3, 4, 6), F.List(F.n, F.C0, F.C4)).find_linear_recurrence(5)
            .toString(), //
        "{}");
  }

  @Test
  public void test_find_linear_recurrence002() {
    // assert sequence((2,3,4,5,6,79),(n, 0, 5)).find_linear_recurrence(6,gfvar=x) \
    // == ([], None)
    assertEquals(
        Sequences.sequence(F.List(2, 3, 4, 5, 6, 79), F.List(F.n, F.C0, F.C5))
            .find_linear_recurrence(6, F.NIL, F.x).toString(), //
        "{{},None}");

    // assert sequence((2,3,4,5,8,30),(n, 0, 5)).find_linear_recurrence(6,gfvar=x) \
    // == ([Rational(19, 2), -20, Rational(27, 2)], (-31*x**2 + 32*x - 4)/(27*x**3 - 40*x**2 + 19*x
    // -2))
    assertEquals(
        Sequences.sequence(F.List(2, 3, 4, 5, 8, 30), F.List(F.n, F.C0, F.C5))
            .find_linear_recurrence(6, F.NIL, F.x).toString(), //
        "{{19/2,-20,27/2},(4-32*x+31*x^2)/(2-19*x+40*x^2-27*x^3)}");

    // assert sequence(fibonacci(n)).find_linear_recurrence(30,gfvar=x) \
    // == ([1, 1], -x/(x**2 + x - 1))
    assertEquals(
        Sequences.sequence(F.Fibonacci(F.n), F.List(F.n, F.C0, F.C5))
            .find_linear_recurrence(6, F.NIL, F.x).toString(), //
        "{{1,1},-x/(-1+x+x^2)}");

    // TODO tribonacci not defined in symja
    // assert sequence(tribonacci(n)).find_linear_recurrence(30,gfvar=x) \
    // == ([1, 1, 1], -x/(x**3 + x**2 + x - 1))
    // assertEquals(
    // Sequences.sequence(F.Fibonacci(F.n), F.List(F.n, F.C0, F.C5))
    // .find_linear_recurrence(6, F.NIL, F.x).toString(), //
    // "{{1,1},-x/(-1+x+x^2)}");
  }
}
