package org.matheclipse.core.sympy.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.matheclipse.core.sympy.core.Expr.argsCnc;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

public class TestExpr extends ExprEvaluatorTestCase {

  @Test
  public void test_as_coeff_add() {
    ISymbol x = F.x;
    ISymbol y = F.y;
    assertEquals(F.C2.asCoeffAdd().toString(), "{2,Plus()}");
    assertEquals(F.num(3.0).asCoeffAdd().toString(), "{0,Plus(3.0)}");
    assertEquals(F.num(-3.0).asCoeffAdd().toString(), "{0,Plus(-3.0)}");
    assertEquals(x.asCoeffAdd().toString(), "{0,Plus(x)}");
    assertEquals(x.subtract(F.C1).asCoeffAdd().toString(), "{-1,Plus(x)}");
    assertEquals(x.plus(F.C1).asCoeffAdd().toString(), "{1,Plus(x)}");
    assertEquals(x.plus(F.C2).asCoeffAdd().toString(), "{2,Plus(x)}");
    assertEquals(x.plus(y).asCoeffAdd(y).toString(), "{x,Plus(y)}");
    assertEquals(F.C3.times(x).asCoeffAdd(y).toString(), "{3*x,Plus()}");
    IExpr e2 = F.Power(F.Plus(x, y), F.C2);
    assertEquals(e2.asCoeffAdd(y).toString(), "{0,Plus((x+y)^2)}");

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
    IExpr e2 = F.Power(F.C2, F.Plus(x, y));
    assertEquals(e2.asCoeffmul(y, false).toString(), "{1,{2^(x+y)}}");
    assertEquals(F.num(1.1).multiply(x).asCoeffmul(null, false).toString(), "{1.1,{x}}");
    assertEquals(F.num(1.1).multiply(x).asCoeffmul(null, true).toString(), "{1,{1.1,x}}");
    assertEquals(F.CNInfinity.multiply(x).asCoeffmul(null, true).toString(), "{-1,{Infinity,x}}");

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
    assertEquals(F.Times(x, y).asCoeffMul().toString(), "{1,x*y}");
    assertEquals(F.Times(F.CNInfinity, x).asCoeffMul().toString(), "{-1,Infinity*x}");
  }

  @Test
  public void test_as_independent() {

    ISymbol x = F.x;
    ISymbol y = F.y;
    // assert (-2*tan(4*x)).as_independent(x) == ( )
    assertEquals(
        Expr.asIndependent(F.Times(F.CN2, F.Tan(F.Times(F.C4, x)), x), F.List(x)).toString(), //
        "{-2,x*Tan(4*x)}");

    // // assert S.Zero.as_independent(x, as_Add=True) == (0, 0)
    assertEquals(Expr.asIndependent(F.C0, F.List(x), Map.of("as_Add", S.True)).toString(), //
        "{0,0}");
    // assert S.Zero.as_independent(x, as_Add=False) == (0, 0)
    assertEquals(Expr.asIndependent(F.C0, F.List(x), Map.of("as_Add", S.False)).toString(), //
        "{0,0}");
    // assert (2*x*sin(x) + y + x).as_independent(x) == (y, x + 2*x*sin(x))
    assertEquals(Expr.asIndependent(F.Plus(F.Times(2, x, F.Sin(x)), x, y), F.List(x)).toString(), //
        "{y,x+2*x*Sin(x)}");

    // assert (2*x*sin(x) + y + x).as_independent(y) == (x + 2*x*sin(x), y)
    assertEquals(Expr.asIndependent(F.Plus(F.Times(2, x, F.Sin(x)), x, y), F.List(y)).toString(), //
        "{x+2*x*Sin(x),y}");

    // assert (2*x*sin(x) + y + x).as_independent(x, y) == (0, y + x + 2*x*sin(x))
    assertEquals(Expr.asIndependent(F.Plus(F.Times(2, x, F.Sin(x)), x, y), F.List(x, y)).toString(), //
        "{0,x+y+2*x*Sin(x)}");
    // assert (x*sin(x)*cos(y)).as_independent(x) == (cos(y), x*sin(x))
    assertEquals(Expr.asIndependent(F.Times(x, F.Cos(y), F.Sin(x)), F.List(x)).toString(),
        //
        "{Cos(y),x*Sin(x)}");
    // assert (x*sin(x)*cos(y)).as_independent(y) == (x*sin(x), cos(y))
    assertEquals(Expr.asIndependent(F.Times(x, F.Cos(y), F.Sin(x)), F.List(y)).toString(),
        //
        "{x*Sin(x),Cos(y)}");

    // assert (x*sin(x)*cos(y)).as_independent(x, y) == (1, x*sin(x)*cos(y))
    assertEquals(Expr.asIndependent(F.Times(x, F.Cos(y), F.Sin(x)), F.List(x, y)).toString(), //
        "{1,x*Cos(y)*Sin(x)}");
    // assert (sin(x)).as_independent(x) == (1, sin(x))
    assertEquals(Expr.asIndependent(F.Sin(x), F.List(x)).toString(), //
        "{1,Sin(x)}");

    // assert (sin(x)).as_independent(y) == (sin(x), 1)
    assertEquals(Expr.asIndependent(F.Sin(x), F.List(y)).toString(), //
        "{Sin(x),1}");

    // assert (2*sin(x)).as_independent(x) == (2, sin(x))
    assertEquals(Expr.asIndependent(F.Times(2, F.Sin(x)), F.List(x)).toString(), //
        "{2,Sin(x)}");
    // assert (2*sin(x)).as_independent(y) == (2*sin(x), 1)
    assertEquals(Expr.asIndependent(F.Times(2, F.Sin(x)), F.List(y)).toString(), //
        "{2*Sin(x),1}");

    // # issue 4903 = 1766b
    // n1, n2, n3 = symbols('n1 n2 n3', commutative=False)
    // assert (n1 + n1*n2).as_independent(n2) == (n1, n1*n2)
    // assert (n2*n1 + n1*n2).as_independent(n2) == (0, n1*n2 + n2*n1)
    // assert (n1*n2*n1).as_independent(n2) == (n1, n2*n1)
    // assert (n1*n2*n1).as_independent(n1) == (1, n1*n2*n1)
    //
    // assert (3*x).as_independent(x, as_Add=True) == (0, 3*x)
    assertEquals(Expr.asIndependent(F.Times(3, x), F.List(x), Map.of("as_Add", S.True)).toString(), //
        "{0,3*x}");
    // assert (3*x).as_independent(x, as_Add=False) == (3, x)
    assertEquals(Expr.asIndependent(F.Times(3, x), F.List(x), Map.of("as_Add", S.False)).toString(), //
        "{3,x}");
    // assert (3 + x).as_independent(x, as_Add=True) == (3, x)
    assertEquals(Expr.asIndependent(F.Plus(3, x), F.List(x), Map.of("as_Add", S.True)).toString(), //
        "{3,x}");
    // // assert (3 + x).as_independent(x, as_Add=False) == (1, 3 + x)
    assertEquals(Expr.asIndependent(F.Plus(3, x), F.List(x), Map.of("as_Add", S.False)).toString(), //
        "{1,3+x}");

    // # issue 5479
    // assert (3*x).as_independent(Symbol) == (3, x)
    //
    // # issue 5648
    // assert (n1*x*y).as_independent(x) == (n1*y, x)
    // assert ((x + n1)*(x - y)).as_independent(x) == (1, (x + n1)*(x - y))
    // assert ((x + n1)*(x - y)).as_independent(y) == (x + n1, x - y)
    // assert (DiracDelta(x - n1)*DiracDelta(x - y)).as_independent(x) \
    // == (1, DiracDelta(x - n1)*DiracDelta(x - y))
    // assert (x*y*n1*n2*n3).as_independent(n2) == (x*y*n1, n2*n3)
    // assert (x*y*n1*n2*n3).as_independent(n1) == (x*y, n1*n2*n3)
    // assert (x*y*n1*n2*n3).as_independent(n3) == (x*y*n1*n2, n3)
    // assert (DiracDelta(x - n1)*DiracDelta(y - n1)*DiracDelta(x - n2)).as_independent(y) == \
    // (DiracDelta(x - n1)*DiracDelta(x - n2), DiracDelta(y - n1))
    //
    // # issue 5784
    // assert (x + Integral(x, (x, 1, 2))).as_independent(x, strict=True) == \
    // (Integral(x, (x, 1, 2)), x)
    //
    // eq = Add(x, -x, 2, -3, evaluate=False)
    // assert eq.as_independent(x) == (-1, Add(x, -x, evaluate=False))
    // eq = Mul(x, 1/x, 2, -3, evaluate=False)
    // assert eq.as_independent(x) == (-6, Mul(x, 1/x, evaluate=False))
    //
    // assert (x*y).as_independent(z, as_Add=True) == (x*y, 0)
  }

  @Test
  public void test_as_coeff_exponent() {
    // https://github.com/sympy/sympy/blob/7158ec42de7d8b02ad8809fdbb87daa0da4ca121/sympy/core/tests/test_expr.py#L1268
    ISymbol x = F.x;
    ISymbol y = F.y;
    assertEquals(x.asCoeffExponent(x).toString(), //
        "{1,1}");
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
  public void test_extractions() {
    // https://github.com/sympy/sympy/blob/7158ec42de7d8b02ad8809fdbb87daa0da4ca121/sympy/core/tests/test_expr.py#L1268
    ISymbol x = F.x;
    ISymbol y = F.y;
    assertEquals(Expr.extractAdditively(F.Plus(F.C3, x), F.Plus(F.CN3, y)).isPresent(), //
        false);
    // for base in (2, S.Exp1):
    // assert Pow(base**x, 3, evaluate=False
    // ).extract_multiplicatively(base**x) == base**(2*x)
    // assert (base**(5*x)).extract_multiplicatively(
    // base**(3*x)) == base**(2*x)
    // assert ((x*y)**3).extract_multiplicatively(x**2 * y) == x*y**2
    // assert ((x*y)**3).extract_multiplicatively(x**4 * y) is None
    // assert (2*x).extract_multiplicatively(2) == x
    // assert (2*x).extract_multiplicatively(3) is None
    // assert (2*x).extract_multiplicatively(-1) is None
    // assert (S.Half*x).extract_multiplicatively(3) == x/6
    // assert (sqrt(x)).extract_multiplicatively(x) is None
    // assert (sqrt(x)).extract_multiplicatively(1/x) is None
    // assert x.extract_multiplicatively(-x) is None
    // assert (-2 - 4*I).extract_multiplicatively(-2) == 1 + 2*I
    // assert (-2 - 4*I).extract_multiplicatively(3) is None
    // assert (-2*x - 4*y - 8).extract_multiplicatively(-2) == x + 2*y + 4
    // assert (-2*x*y - 4*x**2*y).extract_multiplicatively(-2*y) == 2*x**2 + x
    // assert (2*x*y + 4*x**2*y).extract_multiplicatively(2*y) == 2*x**2 + x
    // assert (-4*y**2*x).extract_multiplicatively(-3*y) is None
    // assert (2*x).extract_multiplicatively(1) == 2*x
    // assert (-oo).extract_multiplicatively(5) is -oo
    // assert (oo).extract_multiplicatively(5) is oo
    //
    // assert ((x*y)**3).extract_additively(1) is None
    assertEquals(Expr.extractAdditively(F.Power(F.Times(x, y), F.C3), F.C1).isPresent(), //
        false);
    // assert (x + 1).extract_additively(x) == 1
    assertEquals(Expr.extractAdditively(F.Plus(F.C1, x), x).toString(), //
        "1");

    // assert (x + 1).extract_additively(2*x) is None
    assertEquals(Expr.extractAdditively(F.Plus(F.C1, x), F.Times(2, x)).isPresent(), //
        false);

    // assert (x + 1).extract_additively(-x) is None
    // assert (-x + 1).extract_additively(2*x) is None
    // assert (2*x + 3).extract_additively(x) == x + 3
    assertEquals(Expr.extractAdditively(F.Plus(F.C3, F.Times(2, x)), x).toString(), //
        "3+x");
    // assert (2*x + 3).extract_additively(2) == 2*x + 1
    // assert (2*x + 3).extract_additively(3) == 2*x
    // assert (2*x + 3).extract_additively(-2) is None
    // assert (2*x + 3).extract_additively(3*x) is None
    // assert (2*x + 3).extract_additively(2*x) == 3
    // assert x.extract_additively(0) == x
    // assert S(2).extract_additively(x) is None
    // assert S(2.).extract_additively(2) is S.Zero
    // assert S(2*x + 3).extract_additively(x + 1) == x + 2
    // assert S(2*x + 3).extract_additively(y + 1) is None
    // assert S(2*x - 3).extract_additively(x + 1) is None
    // assert S(2*x - 3).extract_additively(y + z) is None
    // assert ((a + 1)*x*4 + y).extract_additively(x).expand() == \
    // 4*a*x + 3*x + y
    // assert ((a + 1)*x*4 + 3*y).extract_additively(x + 2*y).expand() == \
    // 4*a*x + 3*x + y
    // assert (y*(x + 1)).extract_additively(x + 1) is None
    // assert ((y + 1)*(x + 1) + 3).extract_additively(x + 1) == \
    // y*(x + 1) + 3
    // assert ((x + y)*(x + 1) + x + y + 3).extract_additively(x + y) == \
    // x*(x + y) + 3
    // assert (x + y + 2*((x + y)*(x + 1)) + 3).extract_additively((x + y)*(x + 1)) == \
    // x + y + (x + 1)*(x + y) + 3
    // assert ((y + 1)*(x + 2*y + 1) + 3).extract_additively(y + 1) == \
    // (x + 2*y)*(y + 1) + 3
    // assert (-x - x*I).extract_additively(-x) == -I*x
    // # extraction does not leave artificats, now
    // assert (4*x*(y + 1) + y).extract_additively(x) == x*(4*y + 3) + y
    //
    // n = Symbol("n", integer=True)
    // assert (Integer(-3)).could_extract_minus_sign() is True
    // assert (-n*x + x).could_extract_minus_sign() != \
    // (n*x - x).could_extract_minus_sign()
    // assert (x - y).could_extract_minus_sign() != \
    // (-x + y).could_extract_minus_sign()
    // assert (1 - x - y).could_extract_minus_sign() is True
    // assert (1 - x + y).could_extract_minus_sign() is False
    // assert ((-x - x*y)/y).could_extract_minus_sign() is False
    // assert ((x + x*y)/(-y)).could_extract_minus_sign() is True
    // assert ((x + x*y)/y).could_extract_minus_sign() is False
    // assert ((-x - y)/(x + y)).could_extract_minus_sign() is False
    //
    // class sign_invariant(Function, Expr):
    // nargs = 1
    // def __neg__(self):
    // return self
    // foo = sign_invariant(x)
    // assert foo == -foo
    // assert foo.could_extract_minus_sign() is False
    // assert (x - y).could_extract_minus_sign() is False
    // assert (-x + y).could_extract_minus_sign() is True
    // assert (x - 1).could_extract_minus_sign() is False
    // assert (1 - x).could_extract_minus_sign() is True
    // assert (sqrt(2) - 1).could_extract_minus_sign() is True
    // assert (1 - sqrt(2)).could_extract_minus_sign() is False
    // # check that result is canonical
    // eq = (3*x + 15*y).extract_multiplicatively(3)
    // assert eq.args == eq.func(*eq.args).args

  }

  @Test
  public void test_leadterm() {
    // https://github.com/sympy/sympy/blob/7158ec42de7d8b02ad8809fdbb87daa0da4ca121/sympy/core/tests/test_expr.py#L1268
    ISymbol x = F.x;
    ISymbol y = F.y;

    // assert (3 + 2*x**(log(3)/log(2) - 1)).leadterm(x) == (3, 0)
    // assertEquals(
    // F.Plus(F.C3,
    // F.Times(F.C2, F.Power(F.x, F.Subtract(F.Divide(F.Log(3), F.Log(2)), F.C1))))
    // .leadTerm(x).toString(), //
    // "{2,Log(3)/Log(2)}");

    // assert (1/x**2 + 1 + x + x**2).leadterm(x)[1] == -2
    // assert (1/x + 1 + x + x**2).leadterm(x)[1] == -1
    // assert (x**2 + 1/x).leadterm(x)[1] == -1
    // assert (1 + x**2).leadterm(x)[1] == 0
    // assert (x + 1).leadterm(x)[1] == 0
    // assertEquals(F.Plus(x, F.C1).leadTerm(x).second().toString(), //
    // "0");
    // // assert (x + x**2).leadterm(x)[1] == 1
    // assertEquals(F.Plus(x, F.Power(x, 2)).leadTerm(x).second().toString(), //
    // "1");
    // // // assert (x**2).leadterm(x)[1] == 2
    assertEquals(F.Power(x, 2).leadTerm(x).second().toString(), //
        "2");
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
    assertEquals(F.Times(x, y).asPowersDict().getValue(z).toString(), "0");
  }


  @Test
  public void test_args_cnc() {
    // https://github.com/sympy/sympy/blob/8f90e7f894b09a3edc54c44af601b838b15aa41b/sympy/core/tests/test_expr.py#L1561
    ISymbol a = F.a;
    ISymbol b = F.b;
    ISymbol x = F.x;
    ISymbol y = F.y;
    assertEquals(argsCnc(F.CEmptyList).toString(), "{{},{}}");


    // assert (x + a).args_cnc() == [[a + x], []]
    assertEquals(argsCnc(F.Plus(a, x)).toString(), "{{a+x},{}}");
    // assert (x * a).args_cnc() == [[a, x], []]
    assertEquals(argsCnc(F.Times(a, x)).toString(), "{{a,x},{}}");

    assertEquals(argsCnc(F.Times(x, y, F.NonCommutativeMultiply(a, b))).toString(), //
        "{{x,y},{a,b}}");

    assertEquals(argsCnc(F.Times(F.CNInfinity, x)).toString(), "{{-1,Infinity,x},{}}");

    assertEquals(argsCnc(F.Times(F.CN1, x)).toString(), "{{-1,x},{}}");
    assertEquals(argsCnc(F.Times(F.num(-2.0), x)).toString(), "{{-1,2.0,x},{}}");
    assertEquals(argsCnc(F.Times(F.CNI, x)).toString(), "{{-1,I,x},{}}");
    // A = symbols('A', commutative=False)
    // assert (x + A).args_cnc() == \
    // [[], [x + A]]
    // assert (x + a).args_cnc() == \
    // [[a + x], []]
    // assert (x*a).args_cnc() == \
    // [[a, x], []]
    // assert (x*y*A*(A + 1)).args_cnc(cset=True) == \
    // [{x, y}, [A, 1 + A]]
    // assert Mul(x, x, evaluate=False).args_cnc(cset=True, warn=False) == \
    // [{x}, []]
    assertEquals(((IAST) argsCnc(F.Times(x, x)).first()).asSortedSet().toString(), "[x]");
    assertEquals(argsCnc(F.Times(x, x)).second().toString(), "{}");

    // assert Mul(x, x**2, evaluate=False).args_cnc(cset=True, warn=False) == \
    // [{x, x**2}, []]
    assertEquals(((IAST) argsCnc(F.Times(x, F.Power(x, 2))).first()).asSortedSet().toString(), //
        "[x, x^2]");
    assertEquals(argsCnc(F.Times(x, F.Power(x, 2))).second().toString(), //
        "{}");
    // raises(ValueError, lambda: Mul(x, x, evaluate=False).args_cnc(cset=True))
    // assert Mul(x, y, x, evaluate=False).args_cnc() == \
    // [[x, y, x], []]
    // # always split -1 from leading number
    // assert (-1.*x).args_cnc() == [[-1, 1.0, x], []]
    assertEquals(argsCnc(F.Times(F.CND1, x)).toString(), //
        "{{-1,1.0,x},{}}");
  }


  @Test
  public void test_coeff() {
    ISymbol x = F.x;
    ISymbol y = F.y;
    ISymbol z = F.z;
    // assert (x + 1).coeff(x + 1) == 1
    assertEquals(Expr.coeff(F.Plus(F.C1, x), F.Plus(F.C1, x)).toString(), //
        "1");

    // assert (3*x).coeff(0) == 0
    assertEquals(Expr.coeff(F.Times(F.C3, x), F.C0).toString(), //
        "0");

    // assert (z*(1 + x)*x**2).coeff(1 + x) == z*x**2
    assertEquals(Expr.coeff(F.Times(F.Plus(F.C1, x), F.Power(x, 2), z), F.Plus(F.C1, x)).toString(), //
        "x^2*z");

    // assert (1 + 2*x*x**(1 + x)).coeff(x*x**(1 + x)) == 2
    assertEquals(
        Expr.coeff(F.Plus(F.C1, F.Times(2, F.Power(x, F.Plus(F.C2, x)))),
            F.Power(x, F.Plus(F.C2, x))).toString(), //
        "2");
    // assert (1 + 2*x**(y + z)).coeff(x**(y + z)) == 2
    assertEquals(
        Expr.coeff(F.Plus(F.C1, F.Times(2, F.Power(x, F.Plus(y, z)))), F.Power(x, F.Plus(y, z)))
            .toString(), //
        "2");
    // assert (3 + 2*x + 4*x**2).coeff(1) == 0
    assertEquals(
        Expr.coeff(F.Plus(F.C3, F.Times(F.C2, x), F.Times(F.C4, F.Power(x, 2))), F.C1).toString(), //
        "0");
    // assert (3 + 2*x + 4*x**2).coeff(-1) == 0
    assertEquals(
        Expr.coeff(F.Plus(F.C3, F.Times(F.C2, x), F.Times(F.C4, F.Power(x, 2))), F.CN1).toString(), //
        "0");
    // assert (3 + 2*x + 4*x**2).coeff(x) == 2
    assertEquals(
        Expr.coeff(F.Plus(F.C3, F.Times(F.C2, x), F.Times(F.C4, F.Power(x, 2))), x).toString(), //
        "2");
    // assert (3 + 2*x + 4*x**2).coeff(x**2) == 4
    assertEquals(
        Expr.coeff(F.Plus(F.C3, F.Times(F.C2, x), F.Times(F.C4, F.Power(x, 2))), F.Power(x, 2))
            .toString(), //
        "4");
    // assert (3 + 2*x + 4*x**2).coeff(x**3) == 0
    assertEquals(
        Expr.coeff(F.Plus(F.C3, F.Times(F.C2, x), F.Times(F.C4, F.Power(x, 2))), F.Power(x, 3))
            .toString(), //
        "0");

    // assert (-x/8 + x*y).coeff(x) == Rational(-1, 8) + y
    assertEquals(Expr.coeff(F.Plus(F.Times(F.QQ(-1, 8), x), F.Times(x, y)), x).toString(), //
        "(-1)*1/8*y");
    // assert (-x/8 + x*y).coeff(-x) == S.One/8
    assertEquals(
        Expr.coeff(F.Plus(F.Times(F.QQ(-1, 8), x), F.Times(x, y)), F.Times(F.CN1, x)).toString(), //
        "1/8");
    // assert (4*x).coeff(2*x) == 0
    assertEquals(Expr.coeff(F.Times(4, x), F.Times(2, x)).toString(), //
        "0");
    // assert (2*x).coeff(2*x) == 1
    assertEquals(Expr.coeff(F.Times(2, x), F.Times(2, x)).toString(), //
        "1");
    // assert (-oo*x).coeff(x*oo) == -1
    assertEquals(Expr.coeff(F.Times(F.CNInfinity, x), F.Times(F.CInfinity, x)).toString(), //
        "-1");
    // assert (10*x).coeff(x, 0) == 0
    // assert (10*x).coeff(10*x, 0) == 0
    //
    // n1, n2 = symbols('n1 n2', commutative=False)
    // assert (n1*n2).coeff(n1) == 1
    // assert (n1*n2).coeff(n2) == n1
    // assert (n1*n2 + x*n1).coeff(n1) == 1 # 1*n1*(n2+x)
    // assert (n2*n1 + x*n1).coeff(n1) == n2 + x
    // assert (n2*n1 + x*n1**2).coeff(n1) == n2
    // assert (n1**x).coeff(n1) == 0
    // assert (n1*n2 + n2*n1).coeff(n1) == 0
    // assert (2*(n1 + n2)*n2).coeff(n1 + n2, right=1) == n2
    // assert (2*(n1 + n2)*n2).coeff(n1 + n2, right=0) == 2
    //
    // assert (2*f(x) + 3*f(x).diff(x)).coeff(f(x)) == 2
    //
    // expr = z*(x + y)**2
    // expr2 = z*(x + y)**2 + z*(2*x + 2*y)**2
    // assert expr.coeff(z) == (x + y)**2
    // assert expr.coeff(x + y) == 0
    // assert expr2.coeff(z) == (x + y)**2 + (2*x + 2*y)**2
    //
    // assert (x + y + 3*z).coeff(1) == x + y
    assertEquals(Expr.coeff(F.Plus(x, y, F.Times(F.C3, z)), F.C1).toString(), //
        "x+y");
    // assert (-x + 2*y).coeff(-1) == x
    assertEquals(Expr.coeff(F.Plus(F.Negate(x), F.Times(F.C2, y)), F.CN1).toString(), //
        "x");
    // assert (x - 2*y).coeff(-1) == 2*y
    assertEquals(Expr.coeff(F.Plus(x, F.Times(F.CN2, y)), F.CN1).toString(), //
        "2*y");
    // assert (3 + 2*x + 4*x**2).coeff(1) == 0
    assertEquals(
        Expr.coeff(F.Plus(F.C3, F.Times(F.C2, x), F.Times(F.C4, F.Power(x, 2))), F.C1).toString(), //
        "0");
    // assert (-x - 2*y).coeff(2) == -y
    assertEquals(Expr.coeff(F.Plus(F.Negate(x), F.Times(F.CN2, y)), F.C2).toString(), //
        "-y");
    // assert (x + sqrt(2)*x).coeff(sqrt(2)) == x
    assertEquals(Expr.coeff(F.Plus(x, F.Times(F.Sqrt(2), x)), F.Sqrt(2)).toString(), //
        "x");
    // assert (3 + 2*x + 4*x**2).coeff(x) == 2
    // assert (3 + 2*x + 4*x**2).coeff(x**2) == 4
    // assert (3 + 2*x + 4*x**2).coeff(x**3) == 0
    // assert (z*(x + y)**2).coeff((x + y)**2) == z
    // assert (z*(x + y)**2).coeff(x + y) == 0
    // assert (2 + 2*x + (x + 1)*y).coeff(x + 1) == y
    //
    // assert (x + 2*y + 3).coeff(1) == x
    assertEquals(Expr.coeff(F.Plus(F.C3, x, F.Times(2, y)), F.C1).toString(), //
        "x");
    // assert (x + 2*y + 3).coeff(x, 0) == 2*y + 3
    assertEquals(Expr.coeff(F.Plus(F.C3, x, F.Times(2, y)), x, 0, false, true).toString(), //
        "3+2*y");
    // assert (x**2 + 2*y + 3*x).coeff(x**2, 0) == 2*y + 3*x
    assertEquals(
        Expr.coeff(F.Plus(F.Times(F.C3, x), F.Power(x, 2), F.Times(2, y)), F.Power(x, 2), 0, false,
            true).toString(), //
        "3*x+2*y");
    // assert x.coeff(0, 0) == 0
    assertEquals(Expr.coeff(x, F.C0, 0, false, true).toString(), //
        "0");
    // assert x.coeff(x, 0) == 0
    assertEquals(Expr.coeff(x, x, 0, false, true).toString(), //
        "0");
    // n, m, o, l = symbols('n m o l', commutative=False)
    // assert n.coeff(n) == 1
    // assert y.coeff(n) == 0
    // assert (3*n).coeff(n) == 3
    // assert (2 + n).coeff(x*m) == 0
    // assert (2*x*n*m).coeff(x) == 2*n*m
    // assert (2 + n).coeff(x*m*n + y) == 0
    // assert (2*x*n*m).coeff(3*n) == 0
    // assert (n*m + m*n*m).coeff(n) == 1 + m
    // assert (n*m + m*n*m).coeff(n, right=True) == m # = (1 + m)*n*m
    // assert (n*m + m*n).coeff(n) == 0
    // assert (n*m + o*m*n).coeff(m*n) == o
    // assert (n*m + o*m*n).coeff(m*n, right=True) == 1
    // assert (n*m + n*m*n).coeff(n*m, right=True) == 1 + n # = n*m*(n + 1)
    //
    // assert (x*y).coeff(z, 0) == x*y
    assertEquals(Expr.coeff(F.Times(x, y), z, 0, false, true).toString(), //
        "x*y");
    // assert (x*n + y*n + z*m).coeff(n) == x + y
    // assert (n*m + n*o + o*l).coeff(n, right=True) == m + o
    // assert (x*n*m*n + y*n*m*o + z*l).coeff(m, right=True) == x*n + y*o
    // assert (x*n*m*n + x*n*m*o + z*l).coeff(m, right=True) == n + o
    // assert (x*n*m*n + x*n*m*o + z*l).coeff(m) == x*n
  }
}
