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
    // assert is_increasing(x**3 - 3*x**2 + 4*x, S.Reals)
    assertEquals("True", increasing("x^3 - 3*x^2 + 4*x", "True"));
    // assert is_increasing(-x**2, Interval(-oo, 0))
    assertEquals("True", increasing("-x^2", "x<=0"));
    // assert not is_increasing(-x**2, Interval(0, oo))
    assertEquals("False", increasing("-x^2", "x>=0"));
    // assert not is_increasing(4*x**3 - 6*x**2 - 72*x + 30, Interval(-2, 3))
    assertEquals("False", increasing("4*x^3 - 6*x^2 - 72*x + 30", "-2<=x<=3"));
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
    assertEquals("False", strictlyIncreasing("1", "True"));
    // deviation from sympy: isolated stationary points are allowed
    assertEquals("True", strictlyIncreasing("x^3", "True"));
  }

  @Test
  public void testIsDecreasing() {
    // assert is_decreasing(1/(x**2 - 3*x), Interval.open(Rational(3,2), 3))
    assertEquals("True", decreasing("1/(x^2 - 3*x)", "3/2<x<3"));
    // assert is_decreasing(1/(x**2 - 3*x), Interval.Lopen(3, oo))
    assertEquals("True", decreasing("1/(x^2 - 3*x)", "x>3"));
    // assert not is_decreasing(1/(x**2 - 3*x), Interval.Ropen(-oo, Rational(3, 2)))
    assertEquals("False", decreasing("1/(x^2 - 3*x)", "x<3/2"));
    // assert not is_decreasing(-x**2, Interval(-oo, 0))
    assertEquals("False", decreasing("-x^2", "x<=0"));
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
  }

  @Test
  public void testIsMonotonic() {
    // assert is_monotonic(1/(x**2 - 3*x), Interval.open(Rational(3,2), 3))
    assertEquals("True", monotonic("1/(x^2 - 3*x)", "3/2<x<3"));
    // assert is_monotonic(1/(x**2 - 3*x), Interval.Lopen(3, oo))
    assertEquals("True", monotonic("1/(x^2 - 3*x)", "x>3"));
    // assert is_monotonic(x**3 - 3*x**2 + 4*x, S.Reals)
    assertEquals("True", monotonic("x^3 - 3*x^2 + 4*x", "True"));
    // assert not is_monotonic(-x**2, S.Reals)
    assertEquals("False", monotonic("-x^2", "True"));
    // assert is_monotonic(x**2 + y + 1, Interval(1, 2), x)
    assertEquals("True", monotonic("x^2 + 1", "1<=x<=2"));
  }

  @Test
  public void testIsConvex() {
    // https://github.com/sympy/sympy/blob/master/sympy/calculus/tests/test_util.py
    // assert is_convex(1/x**2, x, domain=Interval.open(0, oo)) == True
    assertEquals("True", convex("1/x^2", "x>0"));
    // assert is_convex(1/x, x, domain=Interval(-oo, 0)) == False
    assertEquals("False", convex("1/x", "x<0"));
    // assert is_convex(x**2, x, domain=Interval(0, oo)) == True
    assertEquals("True", convex("x^2", "x>=0"));
    // assert is_convex(1/x, x, domain=Interval.open(0, oo)) == True
    assertEquals("True", convex("1/x", "x>0"));
    // >>> is_convex(exp(x), x)
    assertEquals("True", convex("E^x", "True"));
    // >>> is_convex(x**3, x, domain = Interval(-1, oo))
    assertEquals("False", convex("x^3", "x>=-1"));
  }

  @Test
  public void testStationaryPoints() {
    // assert stationary_points(x**3 - 3*x, x) == {-1, 1}
    assertEquals("x==-1||x==1", stationaryPoints("x^3 - 3*x", "True"));
    // >>> stationary_points(1/x, x, S.Reals)
    // EmptySet
    assertEquals("False", stationaryPoints("1/x", "True"));
    // assert stationary_points(sin(x), x, Interval(-pi/2, pi/2)) == {-pi/2, pi/2}
    assertEquals("x==-Pi/2||x==Pi/2", stationaryPoints("Sin(x)", "-Pi/2<=x<=Pi/2"));
  }
}
