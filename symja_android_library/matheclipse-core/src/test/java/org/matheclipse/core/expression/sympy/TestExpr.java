package org.matheclipse.core.expression.sympy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.Test;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.ISymbol;

public class TestExpr {
  @Test
  public void test_as_coeff_add() {
    ISymbol x = F.x;
    ISymbol y = F.y;
    assertEquals(F.C2.asCoeffAdd().toString(), "{2,{}}");
    assertEquals(F.num(3.0).asCoeffAdd().toString(), "{0,{3.0}}");
    assertEquals(F.num(-3.0).asCoeffAdd().toString(), "{0,{-3.0}}");
    assertEquals(x.asCoeffAdd().toString(), "{0,{x}}");
    assertEquals(x.subtract(F.C1).asCoeffAdd().toString(), "{-1,{x}}");
    assertEquals(x.plus(F.C1).asCoeffAdd().toString(), "{1,{x}}");
    assertEquals(x.plus(F.C2).asCoeffAdd().toString(), "{2,{x}}");
    assertEquals(x.plus(y).asCoeffAdd(y).toString(), "{x,{y}}");
    assertEquals(F.C3.times(x).asCoeffAdd(y).toString(), "{3*x,{}}");
    IAST e2 = F.Power(F.Plus(x, y), F.C2);
    assertEquals(e2.asCoeffAdd(y).toString(), "{0,{(x+y)^2}}");

    // assert S(2).as_coeff_add() == (2, ())
    // assert S(3.0).as_coeff_add() == (0, (S(3.0),))
    // assert S(-3.0).as_coeff_add() == (0, (S(-3.0),))
    // assert x.as_coeff_add() == (0, (x,))
    // assert (x - 1).as_coeff_add() == (-1, (x,))
    // assert (x + 1).as_coeff_add() == (1, (x,))
    // assert (x + 2).as_coeff_add() == (2, (x,))
    // assert (x + y).as_coeff_add(y) == (x, (y,))
    // assert (3*x).as_coeff_add(y) == (3*x, ())
    // # don't do expansion
    // e = (x + y)**2
    // assert e.as_coeff_add(y) == (0, (e,))
  }


  @Test
  public void test_as_coeff_mul() {
    // https://github.com/sympy/sympy/blob/d01454493251159beaa925c32eaa9909f2f5f299/sympy/core/tests/test_expr.py#L1246
    ISymbol x = F.x;
    ISymbol y = F.y;
    assertEquals(F.C2.asCoeffmul(null, true).toString(), "{2,{}}");
    assertEquals(F.num(3.0).asCoeffmul(null, true).toString(), "{1,{3.0}}");
    assertEquals(F.num(-3.0).asCoeffmul(null, true).toString(), "{-1,{3.0}}");
    assertEquals(F.num(-3.0).asCoeffmul(null, false).toString(), "{-3.0,{}}");
    assertEquals(x.asCoeffmul().toString(), "{1,{x}}");
    assertEquals(x.negate().asCoeffmul().toString(), "{-1,{x}}");
    assertEquals(F.C2.times(x).asCoeffmul().toString(), "{2,{x}}");
    assertEquals(x.times(y).asCoeffmul(y).toString(), "{x,{y}}");
    assertEquals(F.C3.plus(x).asCoeffmul().toString(), "{1,{3+x}}");
    assertEquals(F.C3.plus(x).asCoeffmul(y).toString(), "{3+x,{}}");

    IAST e1 = F.Exp(F.Plus(x, y));
    assertEquals(e1.asCoeffmul(y, false).toString(), "{1,{E^(x+y)}}");
    IAST e2 = F.Power(F.C2, F.Plus(x, y));
    assertEquals(e2.asCoeffmul(y, false).toString(), "{1,{2^(x+y)}}");
    assertEquals(F.num(1.1).multiply(x).asCoeffmul(null, false).toString(), "{1.1,{x}}");
    assertEquals(F.num(1.1).multiply(x).asCoeffmul(null, true).toString(), "{1,{1.1,x}}");
    assertEquals(F.CNInfinity.multiply(x).asCoeffmul(null, true).toString(), "{-1,{x,Infinity}}");

    // assert S(2).as_coeff_mul() == (2, ())
    // assert S(3.0).as_coeff_mul() == (1, (S(3.0),))
    // assert S(-3.0).as_coeff_mul() == (-1, (S(3.0),))
    // assert S(-3.0).as_coeff_mul(rational=False) == (-S(3.0), ())
    // assert x.as_coeff_mul() == (1, (x,))
    // assert (-x).as_coeff_mul() == (-1, (x,))
    // assert (2*x).as_coeff_mul() == (2, (x,))
    // assert (x*y).as_coeff_mul(y) == (x, (y,))
    // assert (3 + x).as_coeff_mul() == (1, (3 + x,))
    // assert (3 + x).as_coeff_mul(y) == (3 + x, ())
    // # don't do expansion
    // e = exp(x + y)
    // assert e.as_coeff_mul(y) == (1, (e,))
    // e = 2**(x + y)
    // assert e.as_coeff_mul(y) == (1, (e,))
    // assert (1.1*x).as_coeff_mul(rational=False) == (1.1, (x,))
    // assert (1.1*x).as_coeff_mul() == (1, (1.1, x))
    // assert (-oo*x).as_coeff_mul(rational=True) == (-1, (oo, x))
  }

  @Test
  public void test_as_coeff_Mul() {
    // non sympy test
    assertEquals(F.Times(F.C1D3, S.Pi).asCoeffMul().toString(), "{1/3,Pi}");

    // https://github.com/sympy/sympy/blob/8f90e7f894b09a3edc54c44af601b838b15aa41b/sympy/core/tests/test_expr.py#L1658
    ISymbol x = F.x;
    ISymbol y = F.y;
    assertEquals(F.C3.asCoeffMul().toString(), "{3,1}");
    assertEquals(F.C3D4.asCoeffMul().toString(), "{3/4,1}");
    assertEquals(F.num(5.0).asCoeffMul().toString(), "{5.0,1}");

    assertEquals(F.Times(F.C3, x).asCoeffMul().toString(), "{3,x}");
    assertEquals(F.Times(F.C3D4, x).asCoeffMul().toString(), "{3/4,x}");
    assertEquals(F.Times(F.num(5.0), x).asCoeffMul().toString(), "{5.0,x}");

    assertEquals(F.Times(F.C3, x, y).asCoeffMul().toString(), "{3,x*y}");
    assertEquals(F.Times(F.C3D4, x, y).asCoeffMul().toString(), "{3/4,x*y}");
    assertEquals(F.Times(F.num(5.0), x, y).asCoeffMul().toString(), "{5.0,x*y}");

    assertEquals(x.asCoeffMul().toString(), "{1,x}");
    assertEquals(F.Times(x, y).asCoeffMul().toString(), "{1,{x,y}}");
    assertEquals(F.Times(F.CNInfinity, x).asCoeffMul().toString(), "{-1,x*Infinity}");
  }

  @Test
  public void test_as_coeff_exponent() {
    // https://github.com/sympy/sympy/blob/7158ec42de7d8b02ad8809fdbb87daa0da4ca121/sympy/core/tests/test_expr.py#L1268
    ISymbol x = F.x;
    ISymbol y = F.y;
    assertEquals(F.Times(F.C3, F.Power(x, F.C4)).asCoeffExponent(x).toString(), //
        "{3,4}");
    assertEquals(F.Times(F.C1, F.Power(x, F.C0)).asCoeffExponent(x).toString(), //
        "{1,0}");
    assertEquals(F.Times(F.C0, F.Power(x, F.C0)).asCoeffExponent(x).toString(), //
        "{0,0}");
    assertEquals(
        F.Divide(F.Times(x, F.Log(F.C2)), F.Plus(F.Times(F.C2, x, F.C3), F.Times(S.Pi, x)))
            .asCoeffExponent(x).toString(), //
        "{Log(2)/(6+Pi),0}");
  }

  @Test
  public void test_as_powers_dict() {
    ISymbol x = F.x;
    ISymbol y = F.y;
    ISymbol z = F.z;
    assertEquals(x.asPowersDict().toString(), "{x=1}");
    // x^y * z
    assertEquals(F.Times(F.Power(x, y), z).asPowersDict().toString(), "{x=y, z=1}");
    assertEquals(F.Times(F.C2, F.C2).asPowersDict().toString(), "{2=2}");
    assertEquals(F.Times(x, y).asPowersDict().get(z).toString(), "0");
  }
}
