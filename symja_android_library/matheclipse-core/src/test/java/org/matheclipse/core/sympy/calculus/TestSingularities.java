package org.matheclipse.core.sympy.calculus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

public class TestSingularities extends ExprEvaluatorTestCase {

  private IExpr parse(String str) {
    return evaluator.getEvalEngine().parse(str);
  }

  private String increasing(String f, String domain) {
    EvalEngine engine = evaluator.getEvalEngine();
    return Singularities.isIncreasing(parse(f), parse(domain), (ISymbol) parse("x"), engine)
        .toString();
  }

  private String strictlyIncreasing(String f, String domain) {
    EvalEngine engine = evaluator.getEvalEngine();
    return Singularities
        .isStrictlyIncreasing(parse(f), parse(domain), (ISymbol) parse("x"), engine).toString();
  }

  private String decreasing(String f, String domain) {
    EvalEngine engine = evaluator.getEvalEngine();
    return Singularities.isDecreasing(parse(f), parse(domain), (ISymbol) parse("x"), engine)
        .toString();
  }

  private String strictlyDecreasing(String f, String domain) {
    EvalEngine engine = evaluator.getEvalEngine();
    return Singularities
        .isStrictlyDecreasing(parse(f), parse(domain), (ISymbol) parse("x"), engine).toString();
  }

  private String monotonic(String f, String domain) {
    EvalEngine engine = evaluator.getEvalEngine();
    return Singularities.isMonotonic(parse(f), parse(domain), (ISymbol) parse("x"), engine)
        .toString();
  }

  private String convex(String f, String domain) {
    EvalEngine engine = evaluator.getEvalEngine();
    return Util.isConvex(parse(f), (ISymbol) parse("x"), parse(domain), engine).toString();
  }

  private String stationaryPoints(String f, String domain) {
    EvalEngine engine = evaluator.getEvalEngine();
    return Util.stationaryPoints(parse(f), (ISymbol) parse("x"), parse(domain), engine)
        .toString();
  }

  @Test
  public void testIsIncreasing() {
    // https://github.com/sympy/sympy/blob/master/sympy/calculus/tests/test_singularities.py
    // a = Symbol('a', negative=True)
    // assert is_increasing(x**3 - 3*x**2 + 4*x, S.Reals)
    assertEquals("True", increasing("x^3 - 3*x^2 + 4*x", "True"));
    // assert is_increasing(-x**2, Interval(-oo, 0))
    assertEquals("True", increasing("-x^2", "x<=0"));
    // assert not is_increasing(-x**2, Interval(0, oo))
    assertEquals("False", increasing("-x^2", "x>=0"));
    // assert not is_increasing(4*x**3 - 6*x**2 - 72*x + 30, Interval(-2, 3))
    assertEquals("False", increasing("4*x^3 - 6*x^2 - 72*x + 30", "-2<=x<=3"));
    // assert is_increasing(x**2 + y, Interval(1, oo), x)
    assertEquals("True", increasing("x^2 + y", "x>=1"));
    // assert is_increasing(-x**2*a, Interval(1, oo), x)
    // TODO Reduce(x>=1 && a<0 && a*x>0, x, Reals) isn't solved for x, therefore undecided
    assertEquals("NIL", increasing("-x^2*a", "x>=1 && a<0"));
    // assert is_increasing(1)
    assertEquals("True", increasing("1", "True"));
    assertEquals("True", increasing("E^x", "True"));
  }

  @Test
  public void testIsStrictlyIncreasing() {
    // assert is_strictly_increasing(4*x**3 - 6*x**2 - 72*x + 30, Interval.Ropen(-oo, -2))
    assertEquals("True", strictlyIncreasing("4*x^3 - 6*x^2 - 72*x + 30", "x< -2"));
    // assert is_strictly_increasing(4*x**3 - 6*x**2 - 72*x + 30, Interval.Lopen(3, oo))
    assertEquals("True", strictlyIncreasing("4*x^3 - 6*x^2 - 72*x + 30", "x>3"));
    // assert not is_strictly_increasing(4*x**3 - 6*x**2 - 72*x + 30, Interval.open(-2, 3))
    assertEquals("False", strictlyIncreasing("4*x^3 - 6*x^2 - 72*x + 30", "-2<x<3"));
    // assert not is_strictly_increasing(-x**2, Interval(0, oo))
    assertEquals("False", strictlyIncreasing("-x^2", "x>=0"));
    // assert not is_strictly_decreasing(1)
    assertEquals("False", strictlyDecreasing("1", "True"));
    // deviation from sympy: isolated stationary points are allowed
    assertEquals("True", strictlyIncreasing("x^3", "True"));
  }

  @Test
  public void testIsDecreasing() {
    // b = Symbol('b', positive=True)
    // assert is_decreasing(1/(x**2 - 3*x), Interval.open(Rational(3,2), 3))
    assertEquals("True", decreasing("1/(x^2 - 3*x)", "3/2<x<3"));
    // assert is_decreasing(1/(x**2 - 3*x), Interval.open(1.5, 3))
    assertEquals("True", decreasing("1/(x^2 - 3*x)", "1.5<x<3"));
    // assert is_decreasing(1/(x**2 - 3*x), Interval.Lopen(3, oo))
    assertEquals("True", decreasing("1/(x^2 - 3*x)", "x>3"));
    // assert not is_decreasing(1/(x**2 - 3*x), Interval.Ropen(-oo, Rational(3, 2)))
    assertEquals("False", decreasing("1/(x^2 - 3*x)", "x<3/2"));
    // assert not is_decreasing(-x**2, Interval(-oo, 0))
    assertEquals("False", decreasing("-x^2", "x<=0"));
    // assert not is_decreasing(-x**2*b, Interval(-oo, 0), x)
    // TODO Reduce(x<=0 && b>0 && b*x<0, x, Reals) isn't solved for x, therefore undecided
    assertEquals("NIL", decreasing("-x^2*b", "x<=0 && b>0"));
  }

  @Test
  public void testIsStrictlyDecreasing() {
    // assert is_strictly_decreasing(1/(x**2 - 3*x), Interval.Lopen(3, oo))
    assertEquals("True", strictlyDecreasing("1/(x^2 - 3*x)", "x>3"));
    // assert not is_strictly_decreasing(1/(x**2 - 3*x), Interval.Ropen(-oo, Rational(3, 2)))
    assertEquals("False", strictlyDecreasing("1/(x^2 - 3*x)", "x<3/2"));
    // assert not is_strictly_decreasing(-x**2, Interval(-oo, 0))
    assertEquals("False", strictlyDecreasing("-x^2", "x<=0"));
    // assert not is_strictly_decreasing(1)
    assertEquals("False", strictlyDecreasing("1", "True"));
    // assert is_strictly_decreasing(1/(x**2 - 3*x), Interval.open(Rational(3,2), 3))
    assertEquals("True", strictlyDecreasing("1/(x^2 - 3*x)", "3/2<x<3"));
    // assert is_strictly_decreasing(1/(x**2 - 3*x), Interval.open(1.5, 3))
    assertEquals("True", strictlyDecreasing("1/(x^2 - 3*x)", "1.5<x<3"));
  }

  @Test
  public void testIsMonotonic() {
    // assert is_monotonic(1/(x**2 - 3*x), Interval.open(Rational(3,2), 3))
    assertEquals("True", monotonic("1/(x^2 - 3*x)", "3/2<x<3"));
    // assert is_monotonic(1/(x**2 - 3*x), Interval.open(1.5, 3))
    assertEquals("True", monotonic("1/(x^2 - 3*x)", "1.5<x<3"));
    // assert is_monotonic(1/(x**2 - 3*x), Interval.Lopen(3, oo))
    assertEquals("True", monotonic("1/(x^2 - 3*x)", "x>3"));
    // assert is_monotonic(x**3 - 3*x**2 + 4*x, S.Reals)
    assertEquals("True", monotonic("x^3 - 3*x^2 + 4*x", "True"));
    // assert not is_monotonic(-x**2, S.Reals)
    assertEquals("False", monotonic("-x^2", "True"));
    // assert is_monotonic(x**2 + y + 1, Interval(1, 2), x)
    assertEquals("True", monotonic("x^2 + y + 1", "1<=x<=2"));
    // raises(NotImplementedError, lambda: is_monotonic(x**2 + y + 1))
    // isn't applicable: the Java API requires the variable
  }

  @Test
  public void testIssue23401() {
    // expr = (x + 1)/(-1.0e-3*x**2 + 0.1*x + 0.1)
    // assert is_increasing(expr, Interval(1,2), x)
    assertEquals("True", increasing("(x + 1)/(-1.0*10^-3*x^2 + 0.1*x + 0.1)", "1<=x<=2"));
  }

  @Test
  public void testMonotonicityWithInteriorSingularities() {
    // assert is_monotonic(tan(x), Interval(0, 5), x) is False
    assertEquals("False", monotonic("Tan(x)", "0<=x<=5"));
    // assert is_increasing(tan(x), Interval(0, 5), x) is False
    assertEquals("False", increasing("Tan(x)", "0<=x<=5"));
    // assert is_monotonic(1/x, Interval(-1, 1), x) is False
    assertEquals("False", monotonic("1/x", "-1<=x<=1"));
    // assert is_increasing(1/x, Interval(-1, 1), x) is False
    assertEquals("False", increasing("1/x", "-1<=x<=1"));
    // assert is_decreasing(1/x, Interval(-1, 1), x) is False
    assertEquals("False", decreasing("1/x", "-1<=x<=1"));
    // assert is_increasing(tan(x), Interval(0, 1), x) is True
    assertEquals("True", increasing("Tan(x)", "0<=x<=1"));
    // assert is_increasing(tan(x), Interval.open(-pi/2, pi/2), x) is True
    assertEquals("True", increasing("Tan(x)", "-Pi/2<x<Pi/2"));
    // assert is_decreasing(1/x, Interval.open(0, 1), x) is True
    assertEquals("True", decreasing("1/x", "0<x<1"));
  }

  @Test
  public void testIsConvex() {
    // https://github.com/sympy/sympy/blob/master/sympy/calculus/tests/test_util.py
    // assert is_convex(1/x, x, domain=Interval.open(0, oo)) == True
    assertEquals("True", convex("1/x", "x>0"));
    // assert is_convex(1/x, x, domain=Interval(-oo, 0)) == False
    assertEquals("False", convex("1/x", "x<=0"));
    // assert is_convex(x**2, x, domain=Interval(0, oo)) == True
    assertEquals("True", convex("x^2", "x>=0"));
    // assert is_convex(1/x**3, x, domain=Interval.Lopen(0, oo)) == True
    assertEquals("True", convex("1/x^3", "x>0"));
    // assert is_convex(-1/x**3, x, domain=Interval.Ropen(-oo, 0)) == True
    assertEquals("True", convex("-1/x^3", "x<0"));
    // assert is_convex(log(x) ,x) == False
    assertEquals("False", convex("Log(x)", "True"));
    // assert is_convex(cos(x) + cos(y), x) == False
    assertEquals("False", convex("Cos(x) + Cos(y)", "True"));
    // multivariate convexity (Hessian) isn't ported:
    // assert is_convex(x**2+y**2, x, y) == True
    // assert is_convex(8*x**2 - 2*y**2, x, y) == False

    // >>> is_convex(exp(x), x)
    assertEquals("True", convex("E^x", "True"));
    // >>> is_convex(x**3, x, domain = Interval(-1, oo))
    assertEquals("False", convex("x^3", "x>=-1"));
    // >>> is_convex(1/x**2, x, domain=Interval.open(0, oo))
    assertEquals("True", convex("1/x^2", "x>0"));
  }

  @Test
  public void testStationaryPoints() {
    // assert stationary_points(sin(x), x, Interval(-pi/2, pi/2)) == {-pi/2, pi/2}
    assertEquals("x==-Pi/2||x==Pi/2", stationaryPoints("Sin(x)", "-Pi/2<=x<=Pi/2"));
    // assert stationary_points(sin(x), x, Interval.Ropen(0, pi/4)) is S.EmptySet
    assertEquals("False", stationaryPoints("Sin(x)", "0<=x<Pi/4"));
    // assert stationary_points(tan(x), x) is S.EmptySet
    assertEquals("False", stationaryPoints("Tan(x)", "True"));
    // assert stationary_points(sin(x)*cos(x), x, Interval(0, pi)) == {pi/4, pi*Rational(3, 4)}
    assertEquals("x==Pi/4||x==3/4*Pi", stationaryPoints("Sin(x)*Cos(x)", "0<=x<=Pi"));
    // assert stationary_points(sec(x), x, Interval(0, pi)) == {0, pi}
    assertEquals("x==0||x==Pi", stationaryPoints("Sec(x)", "0<=x<=Pi"));
    // assert stationary_points((x+3)*(x-2), x) == FiniteSet(Rational(-1, 2))
    assertEquals("x==-1/2", stationaryPoints("(x+3)*(x-2)", "True"));
    // assert stationary_points((x + 3)/(x - 2), x, Interval(-5, 5)) is S.EmptySet
    assertEquals("False", stationaryPoints("(x + 3)/(x - 2)", "-5<=x<=5"));
    // assert stationary_points((x**2+3)/(x-2), x) == {2 - sqrt(7), 2 + sqrt(7)}
    assertEquals("x==2-Sqrt(7)||x==2+Sqrt(7)", stationaryPoints("(x^2+3)/(x-2)", "True"));
    // assert stationary_points((x**2+3)/(x-2), x, Interval(0, 5)) == {2 + sqrt(7)}
    assertEquals("x==2+Sqrt(7)", stationaryPoints("(x^2+3)/(x-2)", "0<=x<=5"));
    // assert stationary_points(x**4 + x**3 - 5*x**2, x, S.Reals) == FiniteSet(-2, 0, Rational(5, 4))
    assertEquals("x==-2||x==0||x==5/4", stationaryPoints("x^4 + x^3 - 5*x^2", "True"));
    // assert stationary_points(exp(x), x) is S.EmptySet
    assertEquals("False", stationaryPoints("E^x", "True"));
    // assert stationary_points(log(x) - x, x, S.Reals) == {1}
    assertEquals("x==1", stationaryPoints("Log(x) - x", "True"));
    // assert stationary_points(cos(x), x, Union(Interval(0, 5), Interval(-6, -3))) == {0, -pi, pi}
    assertEquals("x==-Pi||x==0||x==Pi", stationaryPoints("Cos(x)", "0<=x<=5 || -6<=x<=-3"));
    // assert stationary_points(x**3 - 3*x, x) == {-1, 1}
    assertEquals("x==-1||x==1", stationaryPoints("x^3 - 3*x", "True"));
    // >>> stationary_points(1/x, x, S.Reals)
    // EmptySet
    assertEquals("False", stationaryPoints("1/x", "True"));
  }
}
