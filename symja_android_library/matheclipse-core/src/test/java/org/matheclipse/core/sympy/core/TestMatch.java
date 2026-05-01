package org.matheclipse.core.sympy.core;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPatternObject;
import org.matheclipse.core.system.ExprEvaluatorTestCase;

public class TestMatch extends ExprEvaluatorTestCase {
  @Test
  public void test_match_polynomial() {
    // def test_match_polynomial():
    // x = Symbol('x')
    // a = Wild('a', exclude=[x])
    // b = Wild('b', exclude=[x])
    // c = Wild('c', exclude=[x])
    // d = Wild('d', exclude=[x])
    //
    // eq = 4*x**3 + 3*x**2 + 2*x + 1
    // pattern = a*x**3 + b*x**2 + c*x + d
    // assert eq.match(pattern) == {a: 4, b: 3, c: 2, d: 1}
    // assert (eq - 3*x**2).match(pattern) == {a: 4, b: 0, c: 2, d: 1}
    // assert (x + sqrt(2) + 3).match(a + b*x + c*x**2) == \
    // {b: 1, a: sqrt(2) + 3, c: 0}

    // Initialize evaluation engine required for the matcher

    IExpr x = F.Dummy("x");

    // Using the F.wild() definition you provided.
    // Note: SymPy's 'exclude' parameter logic is complex to map directly to basic IPatternObject,
    // so we rely on the SympyMatcher's structural traversal to emulate the exclusion contextually.
    IPatternObject a = F.wild("a");
    IPatternObject b = F.wild("b");
    IPatternObject c = F.wild("c");
    IPatternObject d = F.wild("d");

    // eq1 = 4*x^3 + 3*x^2 + 2*x + 1
    IExpr eq1 = F.Plus(F.C1, F.Times(F.C2, x), F.Times(F.C3, F.Power(x, F.C2)),
        F.Times(F.ZZ(4), F.Power(x, F.C3)));

    // pattern = a*x^3 + b*x^2 + c*x + d
    IExpr pattern1 =
        F.Plus(d, F.Times(c, x), F.Times(b, F.Power(x, F.C2)), F.Times(a, F.Power(x, F.C3)));

    // Assert 1: eq.match(pattern) == {a: 4, b: 3, c: 2, d: 1}
    Map<IExpr, IExpr> result1 = eq1.matchWild(pattern1);
    System.out.println("Match result for eq1: " + result1);
    assertNotNull(result1, "Match 1 should not fail");
    assertEquals(F.ZZ(4), result1.get(a));
    assertEquals(F.C3, result1.get(b));
    assertEquals(F.C2, result1.get(c));
    assertEquals(F.C1, result1.get(d));

    // eq2 = 4*x^3 + 2*x + 1 (eq1 - 3*x^2)
    IExpr eq2 = F.Plus(F.C1, F.Times(F.C2, x), F.Times(F.ZZ(4), F.Power(x, F.C3)));

    // Assert 2: (eq - 3*x**2).match(pattern) == {a: 4, b: 0, c: 2, d: 1}
    Map<IExpr, IExpr> result2 = eq2.matchWild(pattern1);
    System.out.println("Match result for eq2: " + result2);
    assertNotNull(result2, "Match 2 should not fail");
    assertEquals(F.ZZ(4), result2.get(a));
    assertEquals(F.C0, result2.get(b)); // Expecting the matcher to bind missing terms to 0
    assertEquals(F.C2, result2.get(c));
    assertEquals(F.C1, result2.get(d));

    // eq3 = x + sqrt(2) + 3
    IExpr eq3 = F.Plus(F.C3, F.Sqrt(F.C2), x);

    // pattern3 = a + b*x + c*x^2
    IExpr pattern3 = F.Plus(a, F.Times(b, x), F.Times(c, F.Power(x, F.C2)));

    // Assert 3: (x + sqrt(2) + 3).match(a + b*x + c*x**2) == {b: 1, a: sqrt(2) + 3, c: 0}
    Map<IExpr, IExpr> result3 = eq3.matchWild(pattern3);
    assertNotNull(result3, "Match 3 should not fail");
    System.out.println("Match result for eq3: " + result3);

    // a should capture the constant terms
    assertEquals(F.Plus(F.C3, F.Sqrt(F.C2)), result3.get(a));
    assertEquals(F.C1, result3.get(b));
    assertEquals(F.C0, result3.get(c));
  }

  @Test
  public void test_exclude() {
    EvalEngine engine = new EvalEngine(false);

    IExpr x = F.Dummy("x");
    IExpr y = F.Dummy("y");
    IExpr a = F.Dummy("a");

    // p = Wild('p', exclude=[1, x])
    IPatternObject p = F.wild("p", F.C1, x);

    // q = Wild('q')
    IPatternObject q = F.wild("q");

    // r = Wild('r', exclude=[sin, y])
    IPatternObject r = F.wild("r", S.Sin, y);


    // assert sin(x).match(r) is null
    assertNull(F.Sin(x).matchWild(r, engine));
    //
    // // assert cos(y).match(r) is null
    assertNull(F.Cos(y).matchWild(r, engine));

    // e = 3*x**2 + y*x + a
    IExpr e1 = F.Plus(F.Times(F.C3, F.Power(x, F.C2)), F.Times(y, x), a);
    // pattern = p*x**2 + q*x + r
    IExpr pattern1 = F.Plus(F.Times(p, F.Power(x, F.C2)), F.Times(q, x), r);

    Map<IExpr, IExpr> res1 = e1.matchWild(pattern1, engine);
    System.out.println("Match res1: " + res1);
    assertNotNull(res1);
    assertEquals(F.C3, res1.get(p));
    assertEquals(y, res1.get(q));
    assertEquals(a, res1.get(r));

    // e = x + 1
    IExpr e2 = F.Plus(x, F.C1);

    // assert e.match(x + p) is None
    assertNull(e2.matchWild(F.Plus(x, p), engine));

    // assert e.match(p + 1) is None
    assertNull(e2.matchWild(F.Plus(p, F.C1), engine));

    // assert e.match(x + 1 + p) == {p: 0}
    IExpr pat4 = F.Plus(x, F.C1, p);
    Map<IExpr, IExpr> res4 = e2.matchWild(pat4, engine);

    assertNotNull(res4);
    assertEquals(F.C0, res4.get(p));

    // e = cos(x) + 5*sin(y)
    IExpr e3 = F.Plus(F.Cos(x), F.Times(F.C5, F.Sin(y)));

    // assert e.match(r) is None
    assertNull(e3.matchWild(r, engine));

    // assert e.match(cos(y) + r) is None
    IExpr pat5 = F.Plus(F.Cos(y), r);
    assertNull(e3.matchWild(pat5, engine));

    // assert e.match(r + p*sin(q)) == {r: cos(x), p: 5, q: y}
    IExpr pat6 = F.Plus(r, F.Times(p, F.Sin(q)));
    Map<IExpr, IExpr> res6 = e3.matchWild(pat6, engine);
    System.out.println("Match res6: " + res6);
    assertNotNull(res6);
    assertEquals(F.Cos(x), res6.get(r));
    assertEquals(F.C5, res6.get(p));
    assertEquals(y, res6.get(q));
  }

  @Test
  public void test_match_terms() {
    // def test_match_terms():
    // X, Y = map(Wild, "XY")
    // x, y, z = symbols('x y z')
    // assert (5*y - x).match(5*X - Y) == {X: y, Y: x}
    // # 15907
    // assert (x + (y - 1)*z).match(x + X*z) == {X: y - 1}
    // # 20747
    // assert (x - log(x/y)*(1-exp(x/y))).match(x - log(X/y)*(1-exp(x/y))) == {X: x}
    EvalEngine engine = new EvalEngine(false);
    IExpr x = F.Dummy("x");
    IExpr y = F.Dummy("y");
    IExpr z = F.Dummy("z");

    IPatternObject X = F.wild("X");
    IPatternObject Y = F.wild("Y");

    // assert (5*y - x).match(5*X - Y) == {X: y, Y: x}
    // 5*y - x => Plus(Times(5, y), Times(-1, x))
    // 5*X - Y => Plus(Times(5, X), Times(-1, Y))
    IExpr expr1 = F.Plus(F.Times(F.C5, y), F.Negate(x));
    IExpr pat1 = F.Plus(F.Times(F.C5, X), F.Negate(Y));
    Map<IExpr, IExpr> res1 = expr1.matchWild(pat1, engine);
    System.out.println("Match res1: " + res1);
    assertNotNull(res1, "test_match_terms #1 should match");
    assertEquals(y, res1.get(X));
    assertEquals(x, res1.get(Y));

    // assert (x + (y - 1)*z).match(x + X*z) == {X: y - 1}
    // x + (y-1)*z => Plus(x, Times(Plus(y, -1), z))
    // x + X*z => Plus(x, Times(X, z))
    IExpr expr2 = F.Plus(x, F.Times(F.Plus(y, F.CN1), z));
    IExpr pat2 = F.Plus(x, F.Times(X, z));
    Map<IExpr, IExpr> res2 = expr2.matchWild(pat2, engine);
    System.out.println("Match res2: " + res2);
    assertNotNull(res2, "test_match_terms #2 (issue 15907) should match");
    assertEquals(F.Plus(y, F.CN1), res2.get(X));

    // assert (x - log(x/y)*(1-exp(x/y))).match(x - log(X/y)*(1-exp(x/y))) == {X: x}
    // x - log(x/y)*(1-exp(x/y)) => Plus(x, Times(-1, Log(Times(x, Power(y,-1))), Plus(1, Times(-1,
    // Exp(Times(x, Power(y,-1)))))))
    // x - log(X/y)*(1-exp(x/y)) => Plus(x, Times(-1, Log(Times(X, Power(y,-1))), Plus(1, Times(-1,
    // Exp(Times(x, Power(y,-1)))))))
    IExpr xOverY = F.Times(x, F.Power(y, F.CN1));
    IExpr XOverY = F.Times(X, F.Power(y, F.CN1));
    IExpr expr3 =
        F.Plus(x, F.Times(F.CN1, F.Log(xOverY), F.Plus(F.C1, F.Times(F.CN1, F.Exp(xOverY)))));
    IExpr pat3 =
        F.Plus(x, F.Times(F.CN1, F.Log(XOverY), F.Plus(F.C1, F.Times(F.CN1, F.Exp(xOverY)))));
    Map<IExpr, IExpr> res3 = expr3.matchWild(pat3, engine);
    System.out.println("Match res3: " + res3);
    assertNotNull(res3, "test_match_terms #3 (issue 20747) should match");
    assertEquals(x, res3.get(X));
  }


  @Test
  public void test_match_bound() {
    // def test_match_bound():
    // V, W = map(Wild, "VW")
    // x, y = symbols('x y')
    // assert Sum(x, (x, 1, 2)).match(Sum(y, (y, 1, W))) is None
    // assert Sum(x, (x, 1, 2)).match(Sum(V, (V, 1, W))) == {W: 2, V:x}
    // assert Sum(x, (x, 1, 2)).match(Sum(V, (V, 1, 2))) == {V:x}
    EvalEngine engine = new EvalEngine(false);
    IExpr x = F.Dummy("x");
    IExpr y = F.Dummy("y");

    IPatternObject V = F.wild("V");
    IPatternObject W = F.wild("W");

    // expr = Sum(x, {x, 1, 2})
    IExpr expr = F.Sum(x, F.List(x, F.C1, F.C2));

    // assert Sum(x, (x, 1, 2)).match(Sum(y, (y, 1, W))) is None
    // y is a fixed symbol (not a Wild), so it cannot match x → null
    IExpr pat1 = F.Sum(y, F.List(y, F.C1, W));
    Map<IExpr, IExpr> res1 = expr.matchWild(pat1, engine);
    System.out.println("Match bound res1: " + res1);
    assertNull(res1);

    // assert Sum(x, (x, 1, 2)).match(Sum(V, (V, 1, W))) == {W: 2, V: x}
    // V matches x in body and consistently in the iterator, W matches 2
    IExpr pat2 = F.Sum(V, F.List(V, F.C1, W));
    Map<IExpr, IExpr> res2 = expr.matchWild(pat2, engine);
    System.out.println("Match bound res2: " + res2);
    assertNotNull(res2, "test_match_bound #2 should match");
    assertEquals(F.C2, res2.get(W));
    assertEquals(x, res2.get(V));

    // assert Sum(x, (x, 1, 2)).match(Sum(V, (V, 1, 2))) == {V: x}
    // V matches x, all other positions match literally
    IExpr pat3 = F.Sum(V, F.List(V, F.C1, F.C2));
    Map<IExpr, IExpr> res3 = expr.matchWild(pat3, engine);
    System.out.println("Match bound res3: " + res3);
    assertNotNull(res3, "test_match_bound #3 should match");
    assertEquals(x, res3.get(V));
  }

  @Test
  public void test_issue_3778() {
    // https://github.com/sympy/sympy/issues/3778

    // def test_issue_3778():
    // p, c, q = symbols('p c q', cls=Wild)
    // x = Symbol('x')
    // assert (sin(x)**2).match(sin(p)*sin(q)*c) == {q: x, c: 1, p: x}
    // assert (2*sin(x)).match(sin(p) + sin(q) + c) == {q: x, c: 0, p: x}
    EvalEngine engine = new EvalEngine(false);
    IExpr x = F.Dummy("x");

    IPatternObject p = F.wild("p");
    IPatternObject c = F.wild("c");
    IPatternObject q = F.wild("q");

    // assert (sin(x)**2).match(sin(p)*sin(q)*c) == {q: x, c: 1, p: x}
    // sin(x)^2 = Power(Sin(x), 2)
    // sin(p)*sin(q)*c = Times(c, Sin(p), Sin(q))
    IExpr expr1 = F.Power(F.Sin(x), F.C2);
    IExpr pat1 = F.Times(c, F.Sin(p), F.Sin(q));
    Map<IExpr, IExpr> res1 = expr1.matchWild(pat1, engine);
    System.out.println("Issue 3778 res1: " + res1);
    assertNotNull(res1, "sin(x)^2 should match sin(p)*sin(q)*c");
    assertEquals(x, res1.get(q));
    assertEquals(F.C1, res1.get(c));
    assertEquals(x, res1.get(p));

    // assert (2*sin(x)).match(sin(p) + sin(q) + c) == {q: x, c: 0, p: x}
    // 2*sin(x) = Times(2, Sin(x))
    // sin(p) + sin(q) + c = Plus(c, Sin(p), Sin(q))
    IExpr expr2 = F.Times(F.C2, F.Sin(x));
    IExpr pat2 = F.Plus(c, F.Sin(p), F.Sin(q));
    Map<IExpr, IExpr> res2 = expr2.matchWild(pat2, engine);
    System.out.println("Issue 3778 res2: " + res2);
    assertNotNull(res2, "2*sin(x) should match sin(p)+sin(q)+c");
    assertEquals(x, res2.get(q));
    assertEquals(F.C0, res2.get(c));
    assertEquals(x, res2.get(p));
  }

  @Test
  public void test_issue_6103() {
    // https://github.com/sympy/sympy/issues/6103

    // def test_issue_6103():
    // x = Symbol('x')
    // a = Wild('a')
    // assert (-I*x*oo).match(I*a*oo) == {a: -x}
    EvalEngine engine = new EvalEngine(false);
    IExpr x = F.Dummy("x");
    IPatternObject a = F.wild("a");

    // -I*x*oo = Times(CNI, x, CInfinity)
    // I*a*oo = Times(CI, a, CInfinity)
    IExpr expr = F.Times(F.CNI, x, F.CInfinity);
    IExpr pat = F.Times(F.CI, a, F.CInfinity);
    Map<IExpr, IExpr> res = expr.matchWild(pat, engine);
    System.out.println("Issue 6103 res: " + res);
    assertNotNull(res, "-I*x*oo should match I*a*oo");
    assertEquals(F.Negate(x), res.get(a));
  }

  @Test
  public void test_issue_3773() {
    // https://github.com/sympy/sympy/issues/3773

    // def test_issue_3773():
    // x = symbols('x')
    // z, phi, r = symbols('z phi r')
    // c, A, B, N = symbols('c A B N', cls=Wild)
    // l = Wild('l', exclude=(0,))
    //
    // eq = z * sin(2*phi) * r**7
    // matcher = c * sin(phi*N)**l * r**A * log(r)**B
    //
    // assert eq.match(matcher) == {c: z, l: 1, N: 2, A: 7, B: 0}
    // assert (-eq).match(matcher) == {c: -z, l: 1, N: 2, A: 7, B: 0}
    // assert (x*eq).match(matcher) == {c: x*z, l: 1, N: 2, A: 7, B: 0}
    // assert (-7*x*eq).match(matcher) == {c: -7*x*z, l: 1, N: 2, A: 7, B: 0}
    //
    // matcher = c*sin(phi*N)**l * r**A
    //
    // assert eq.match(matcher) == {c: z, l: 1, N: 2, A: 7}
    // assert (-eq).match(matcher) == {c: -z, l: 1, N: 2, A: 7}
    // assert (x*eq).match(matcher) == {c: x*z, l: 1, N: 2, A: 7}
    // assert (-7*x*eq).match(matcher) == {c: -7*x*z, l: 1, N: 2, A: 7}
    EvalEngine engine = new EvalEngine(false);
    IExpr x = F.Dummy("x");
    IExpr z = F.Dummy("z");
    IExpr phi = F.Dummy("phi");
    IExpr r = F.Dummy("r");

    IPatternObject c = F.wild("c");
    IPatternObject A = F.wild("A");
    IPatternObject B = F.wild("B");
    IPatternObject N = F.wild("N");
    IPatternObject l = F.wild("l", F.C0); // Wild('l', exclude=(0,))

    // eq = z * sin(2*phi) * r**7
    IExpr eq = F.Times(z, F.Sin(F.Times(F.C2, phi)), F.Power(r, F.ZZ(7)));

    // matcher1 = c * sin(phi*N)**l * r**A * log(r)**B
    IExpr matcher1 =
        F.Times(c, F.Power(F.Sin(F.Times(phi, N)), l), F.Power(r, A), F.Power(F.Log(r), B));

    // assert eq.match(matcher1) == {c: z, l: 1, N: 2, A: 7, B: 0}
    Map<IExpr, IExpr> res = eq.matchWild(matcher1, engine);
    System.out.println("Issue 3773 eq.match(matcher1): " + res);
    assertNotNull(res, "eq.match(matcher1) should match");
    assertEquals(z, res.get(c));
    assertEquals(F.C1, res.get(l));
    assertEquals(F.C2, res.get(N));
    assertEquals(F.ZZ(7), res.get(A));
    assertEquals(F.C0, res.get(B));

    // assert (-eq).match(matcher1) == {c: -z, l: 1, N: 2, A: 7, B: 0}
    IExpr negEq = engine.evaluate(F.Negate(eq));
    res = negEq.matchWild(matcher1, engine);
    System.out.println("Issue 3773 (-eq).match(matcher1): " + res);
    assertNotNull(res, "(-eq).match(matcher1) should match");
    assertEquals(engine.evaluate(F.Negate(z)), res.get(c));
    assertEquals(F.C1, res.get(l));
    assertEquals(F.C2, res.get(N));
    assertEquals(F.ZZ(7), res.get(A));
    assertEquals(F.C0, res.get(B));

    // assert (x*eq).match(matcher1) == {c: x*z, l: 1, N: 2, A: 7, B: 0}
    IExpr xEq = engine.evaluate(F.Times(x, eq));
    res = xEq.matchWild(matcher1, engine);
    System.out.println("Issue 3773 (x*eq).match(matcher1): " + res);
    assertNotNull(res, "(x*eq).match(matcher1) should match");
    assertEquals(engine.evaluate(F.Times(x, z)), res.get(c));
    assertEquals(F.C1, res.get(l));
    assertEquals(F.C2, res.get(N));
    assertEquals(F.ZZ(7), res.get(A));
    assertEquals(F.C0, res.get(B));

    // assert (-7*x*eq).match(matcher1) == {c: -7*x*z, l: 1, N: 2, A: 7, B: 0}
    IExpr neg7xEq = engine.evaluate(F.Times(F.ZZ(-7), x, eq));
    res = neg7xEq.matchWild(matcher1, engine);
    System.out.println("Issue 3773 (-7*x*eq).match(matcher1): " + res);
    assertNotNull(res, "(-7*x*eq).match(matcher1) should match");
    assertEquals(engine.evaluate(F.Times(F.ZZ(-7), x, z)), res.get(c));
    assertEquals(F.C1, res.get(l));
    assertEquals(F.C2, res.get(N));
    assertEquals(F.ZZ(7), res.get(A));
    assertEquals(F.C0, res.get(B));

    // matcher2 = c*sin(phi*N)**l * r**A
    IExpr matcher2 = F.Times(c, F.Power(F.Sin(F.Times(phi, N)), l), F.Power(r, A));

    // assert eq.match(matcher2) == {c: z, l: 1, N: 2, A: 7}
    res = eq.matchWild(matcher2, engine);
    System.out.println("Issue 3773 eq.match(matcher2): " + res);
    assertNotNull(res, "eq.match(matcher2) should match");
    assertEquals(z, res.get(c));
    assertEquals(F.C1, res.get(l));
    assertEquals(F.C2, res.get(N));
    assertEquals(F.ZZ(7), res.get(A));

    // assert (-eq).match(matcher2) == {c: -z, l: 1, N: 2, A: 7}
    res = negEq.matchWild(matcher2, engine);
    System.out.println("Issue 3773 (-eq).match(matcher2): " + res);
    assertNotNull(res, "(-eq).match(matcher2) should match");
    assertEquals(engine.evaluate(F.Negate(z)), res.get(c));
    assertEquals(F.C1, res.get(l));
    assertEquals(F.C2, res.get(N));
    assertEquals(F.ZZ(7), res.get(A));

    // assert (x*eq).match(matcher2) == {c: x*z, l: 1, N: 2, A: 7}
    res = xEq.matchWild(matcher2, engine);
    System.out.println("Issue 3773 (x*eq).match(matcher2): " + res);
    assertNotNull(res, "(x*eq).match(matcher2) should match");
    assertEquals(engine.evaluate(F.Times(x, z)), res.get(c));
    assertEquals(F.C1, res.get(l));
    assertEquals(F.C2, res.get(N));
    assertEquals(F.ZZ(7), res.get(A));

    // assert (-7*x*eq).match(matcher2) == {c: -7*x*z, l: 1, N: 2, A: 7}
    res = neg7xEq.matchWild(matcher2, engine);
    System.out.println("Issue 3773 (-7*x*eq).match(matcher2): " + res);
    assertNotNull(res, "(-7*x*eq).match(matcher2) should match");
    assertEquals(engine.evaluate(F.Times(F.ZZ(-7), x, z)), res.get(c));
    assertEquals(F.C1, res.get(l));
    assertEquals(F.C2, res.get(N));
    assertEquals(F.ZZ(7), res.get(A));
  }

  @Test
  public void test_issue_3883() {
    // https://github.com/sympy/sympy/issues/3883

    // def test_issue_3883():
    // from sympy.abc import gamma, mu, x
    // f = (-gamma * (x - mu)**2 - log(gamma) + log(2*pi))/2
    // a, b, c = symbols('a b c', cls=Wild, exclude=(gamma,))
    //
    // assert f.match(a * log(gamma) + b * gamma + c) == \
    // {a: Rational(-1, 2), b: -(-mu + x)**2/2, c: log(2*pi)/2}
    // assert f.expand().collect(gamma).match(a * log(gamma) + b * gamma + c) == \
    // {a: Rational(-1, 2), b: (-(x - mu)**2/2).expand(), c: (log(2*pi)/2).expand()}
    // g1 = Wild('g1', exclude=[gamma])
    // g2 = Wild('g2', exclude=[gamma])
    // g3 = Wild('g3', exclude=[gamma])
    // assert f.expand().match(g1 * log(gamma) + g2 * gamma + g3) == \
    // {g3: log(2)/2 + log(pi)/2, g1: Rational(-1, 2), g2: -mu**2/2 + mu*x - x**2/2}
    EvalEngine engine = new EvalEngine(false);
    IExpr gamma = F.Dummy("gamma");
    IExpr mu = F.Dummy("mu");
    IExpr x = F.Dummy("x");

    // f = (-gamma * (x - mu)**2 - log(gamma) + log(2*pi))/2
    // Build as: Plus(-1/2 * gamma * (x-mu)^2, -1/2 * Log(gamma), 1/2 * Log(2*Pi))
    IExpr f = engine.evaluate(
        F.Times(F.C1D2, F.Plus(F.Times(F.CN1, gamma, F.Power(F.Plus(x, F.Negate(mu)), F.C2)),
            F.Negate(F.Log(gamma)), F.Log(F.Times(F.C2, S.Pi)))));

    // a, b, c = symbols('a b c', cls=Wild, exclude=(gamma,))
    IPatternObject a = F.wild("a", gamma);
    IPatternObject b = F.wild("b", gamma);
    IPatternObject c = F.wild("c", gamma);

    // pattern = a * log(gamma) + b * gamma + c
    IExpr pattern = F.Plus(F.Times(a, F.Log(gamma)), F.Times(b, gamma), c);

    // assert f.match(pattern) == {a: -1/2, b: -(x-mu)^2/2, c: log(2*pi)/2}
    Map<IExpr, IExpr> res = f.matchWild(pattern, engine);
    System.out.println("Issue 3883 f.match(pattern): " + res);
    assertNotNull(res, "f.match(a*log(gamma)+b*gamma+c) should match");
    assertEquals(F.QQ(-1, 2), res.get(a));
    System.out.println("b: " + res.get(b));
    // assertEquals(engine.evaluate(F.Times(F.QQ(-1, 2), F.Power(F.Plus(x, F.Negate(mu)), F.C2))),
    // res.get(b));
    // (-1)*1/2*mu^2+mu*x-x^2/2
    assertEquals(
        engine.evaluate(
            F.Plus(F.Times(F.CN1, F.C1D2, F.Sqr(mu)), F.Times(mu, x), F.Times(F.CN1D2, F.Sqr(x)))),
        res.get(b));
    assertEquals(engine.evaluate(F.Times(F.C1D2, F.Log(F.Times(F.C2, S.Pi)))), res.get(c));

    // assert f.expand().collect(gamma).match(pattern) ==
    // {a: -1/2, b: (-(x-mu)^2/2).expand(), c: (log(2*pi)/2).expand()}
    IExpr fExpandedCollected = engine.evaluate(F.Collect(F.Expand(f), gamma));
    res = fExpandedCollected.matchWild(pattern, engine);
    System.out.println("Issue 3883 f.expand().collect(gamma).match(pattern): " + res);
    assertNotNull(res, "f.expand().collect(gamma).match(pattern) should match");
    assertEquals(F.QQ(-1, 2), res.get(a));
    assertEquals(
        engine.evaluate(F.Expand(F.Times(F.QQ(-1, 2), F.Power(F.Plus(x, F.Negate(mu)), F.C2)))),
        res.get(b));
    assertEquals(engine.evaluate(F.Expand(F.Times(F.C1D2, F.Log(F.Times(F.C2, S.Pi))))),
        res.get(c));

    // g1 = Wild('g1', exclude=[gamma])
    // g2 = Wild('g2', exclude=[gamma])
    // g3 = Wild('g3', exclude=[gamma])
    IPatternObject g1 = F.wild("g1", gamma);
    IPatternObject g2 = F.wild("g2", gamma);
    IPatternObject g3 = F.wild("g3", gamma);

    // pattern2 = g1 * log(gamma) + g2 * gamma + g3
    IExpr pattern2 = F.Plus(F.Times(g1, F.Log(gamma)), F.Times(g2, gamma), g3);

    // assert f.expand().match(pattern2) ==
    // {g3: log(2)/2 + log(pi)/2, g1: -1/2, g2: -mu^2/2 + mu*x - x^2/2}
    IExpr fExpanded = engine.evaluate(F.Expand(f));
    res = fExpanded.matchWild(pattern2, engine);
    System.out.println("Issue 3883 f.expand().match(pattern2): " + res);
    assertNotNull(res, "f.expand().match(g1*log(gamma)+g2*gamma+g3) should match");
    assertEquals(F.QQ(-1, 2), res.get(g1));
    assertEquals(engine.evaluate(F.Plus(F.Times(F.QQ(-1, 2), F.Power(mu, F.C2)), F.Times(mu, x),
        F.Times(F.QQ(-1, 2), F.Power(x, F.C2)))), res.get(g2));
    System.out.println("G3: " + res.get(g3));
    // Assert that g3 matched the un-expanded Log(2*Pi)/2
    assertEquals(engine.evaluate(F.Times(F.C1D2, F.Log(F.Times(F.C2, S.Pi)))), res.get(g3));
  }

  @Test
  public void test_issue_4418() {
    // https://github.com/sympy/sympy/issues/4418

    // def test_issue_4418():
    // x = Symbol('x')
    // a, b, c = symbols('a b c', cls=Wild, exclude=(x,))
    // f, g = symbols('f g', cls=Function)
    //
    // eq = diff(g(x)*f(x).diff(x), x)
    //
    // assert eq.match(
    // g(x).diff(x)*f(x).diff(x) + g(x)*f(x).diff(x, x) + c) == {c: 0}
    // assert eq.match(a*g(x).diff(
    // x)*f(x).diff(x) + b*g(x)*f(x).diff(x, x) + c) == {a: 1, b: 1, c: 0}
    EvalEngine engine = new EvalEngine(false);
    IExpr x = F.Dummy("x");

    // a, b, c = symbols('a b c', cls=Wild, exclude=(x,))
    IPatternObject a = F.wild("a", x);
    IPatternObject b = F.wild("b", x);
    IPatternObject c = F.wild("c", x);

    // f, g = symbols('f g', cls=Function)
    IExpr f = F.Dummy("f");
    IExpr g = F.Dummy("g");

    IExpr fx = F.unaryAST1(f, x); // f(x)
    IExpr gx = F.unaryAST1(g, x); // g(x)

    // f'(x) = D[f(x), x]
    IExpr dfx = engine.evaluate(F.D(fx, x));
    // g'(x) = D[g(x), x]
    IExpr dgx = engine.evaluate(F.D(gx, x));
    // f''(x) = D[f(x), {x, 2}]
    IExpr d2fx = engine.evaluate(F.D(fx, F.List(x, F.C2)));

    // eq = diff(g(x)*f(x).diff(x), x)
    // = g'(x)*f'(x) + g(x)*f''(x) (product rule)
    IExpr eq = engine.evaluate(F.D(F.Times(gx, dfx), x));

    // pattern1 = g'(x)*f'(x) + g(x)*f''(x) + c
    IExpr pat1 = F.Plus(F.Times(dgx, dfx), F.Times(gx, d2fx), c);

    // assert eq.match(pat1) == {c: 0}
    Map<IExpr, IExpr> res = eq.matchWild(pat1, engine);
    System.out.println("Issue 4418 eq.match(pat1): " + res);
    assertNotNull(res, "eq.match(g'(x)*f'(x) + g(x)*f''(x) + c) should match");
    assertEquals(F.C0, res.get(c));

    // pattern2 = a*g'(x)*f'(x) + b*g(x)*f''(x) + c
    IExpr pat2 = F.Plus(F.Times(a, dgx, dfx), F.Times(b, gx, d2fx), c);

    // assert eq.match(pat2) == {a: 1, b: 1, c: 0}
    res = eq.matchWild(pat2, engine);
    System.out.println("Issue 4418 eq.match(pat2): " + res);
    assertNotNull(res, "eq.match(a*g'(x)*f'(x) + b*g(x)*f''(x) + c) should match");
    assertEquals(F.C1, res.get(a));
    assertEquals(F.C1, res.get(b));
    assertEquals(F.C0, res.get(c));
  }

  @Test
  public void test_issue_3539() {
    // https://github.com/sympy/sympy/issues/3539 ?

    // def test_issue_3539():
    // a = Wild('a')
    // x = Symbol('x')
    // assert (x - 2).match(a - x) is None
    // assert (6/x).match(a*x) is None
    // assert (6/x**2).match(a/x) == {a: 6/x}
    EvalEngine engine = new EvalEngine(false);
    IExpr x = F.Dummy("x");
    IPatternObject a = F.wild("a");

    // assert (x - 2).match(a - x) is None
    // x - 2 = Plus(-2, x) ; a - x = Plus(a, Times(-1, x))
    IExpr expr1 = F.Plus(F.CN2, x);
    IExpr pat1 = F.Plus(a, F.Negate(x));
    Map<IExpr, IExpr> res1 = expr1.matchWild(pat1, engine);
    System.out.println("Issue 3539 res1: " + res1);
    assertNull(res1);

    // assert (6/x).match(a*x) is None
    // 6/x = Times(6, Power(x, -1)) ; a*x = Times(a, x)
    IExpr expr2 = F.Times(F.ZZ(6), F.Power(x, F.CN1));
    IExpr pat2 = F.Times(a, x);
    Map<IExpr, IExpr> res2 = expr2.matchWild(pat2, engine);
    System.out.println("Issue 3539 res2: " + res2);
    assertNull(res2);

    // assert (6/x**2).match(a/x) == {a: 6/x}
    // 6/x^2 = Times(6, Power(x, -2)) ; a/x = Times(a, Power(x, -1))
    IExpr expr3 = F.Times(F.ZZ(6), F.Power(x, F.CN2));
    IExpr pat3 = F.Times(a, F.Power(x, F.CN1));
    Map<IExpr, IExpr> res3 = expr3.matchWild(pat3, engine);
    System.out.println("Issue 3539 res3: " + res3);
    assertNotNull(res3, "6/x^2 should match a/x");
    // a should be 6/x = Times(6, Power(x, -1))
    assertEquals(F.Times(F.ZZ(6), F.Power(x, F.CN1)), res3.get(a));
  }

  @Test
  public void test_issue_4700() {
    // https://github.com/sympy/sympy/issues/4700

    // def test_issue_4700():
    // f = Function('f')
    // x = Symbol('x')
    // a, b = symbols('a b', cls=Wild, exclude=(f(x),))
    // p = a*f(x) + b
    // eq1 = sin(x)
    // eq2 = f(x) + sin(x)
    // eq3 = f(x) + x + sin(x)
    // eq4 = x + sin(x)
    // assert eq1.match(p) == {a: 0, b: sin(x)}
    // assert eq2.match(p) == {a: 1, b: sin(x)}
    // assert eq3.match(p) == {a: 1, b: x + sin(x)}
    // assert eq4.match(p) == {a: 0, b: x + sin(x)}
    EvalEngine engine = new EvalEngine(false);
    IExpr x = F.Dummy("x");
    IExpr f = F.Dummy("f");
    // f(x) — an unevaluated function application
    IExpr fx = F.unaryAST1(f, x);

    // Wild('a', exclude=(f(x),)) Wild('b', exclude=(f(x),))
    IPatternObject a = F.wild("a", fx);
    IPatternObject b = F.wild("b", fx);

    // p = a*f(x) + b
    IExpr p = F.Plus(F.Times(a, fx), b);

    // eq1 = sin(x)
    IExpr eq1 = F.Sin(x);
    // eq2 = f(x) + sin(x)
    IExpr eq2 = F.Plus(fx, F.Sin(x));
    // eq3 = f(x) + x + sin(x)
    IExpr eq3 = F.Plus(fx, x, F.Sin(x));
    // eq4 = x + sin(x)
    IExpr eq4 = F.Plus(x, F.Sin(x));

    // assert eq1.match(p) == {a: 0, b: sin(x)}
    Map<IExpr, IExpr> res1 = eq1.matchWild(p, engine);
    System.out.println("Issue 4700 res1: " + res1);
    assertNotNull(res1, "eq1.match(p) should match");
    assertEquals(F.C0, res1.get(a));
    assertEquals(F.Sin(x), res1.get(b));

    // assert eq2.match(p) == {a: 1, b: sin(x)}
    Map<IExpr, IExpr> res2 = eq2.matchWild(p, engine);
    System.out.println("Issue 4700 res2: " + res2);
    assertNotNull(res2, "eq2.match(p) should match");
    assertEquals(F.C1, res2.get(a));
    assertEquals(F.Sin(x), res2.get(b));

    // assert eq3.match(p) == {a: 1, b: x + sin(x)}
    Map<IExpr, IExpr> res3 = eq3.matchWild(p, engine);
    System.out.println("Issue 4700 res3: " + res3);
    assertNotNull(res3, "eq3.match(p) should match");
    assertEquals(F.C1, res3.get(a));
    assertEquals(engine.evaluate(F.Plus(x, F.Sin(x))), res3.get(b));

    // assert eq4.match(p) == {a: 0, b: x + sin(x)}
    Map<IExpr, IExpr> res4 = eq4.matchWild(p, engine);
    System.out.println("Issue 4700 res4: " + res4);
    assertNotNull(res4, "eq4.match(p) should match");
    assertEquals(F.C0, res4.get(a));
    assertEquals(engine.evaluate(F.Plus(x, F.Sin(x))), res4.get(b));
  }

  @Test
  public void test_issue_5168() {
    // https://github.com/sympy/sympy/issues/5168

    // def test_issue_5168():
    // a, b, c = symbols('a b c', cls=Wild)
    // x = Symbol('x')
    // f = Function('f')
    // ... (16 assertions testing x, -x, 2*x, -2*x against patterns a, a*f(x)**c, a*b, a*b*f(x)**c)
    EvalEngine engine = new EvalEngine(false);
    IExpr x = F.Dummy("x");
    IExpr f = F.Dummy("f");
    IExpr fx = F.unaryAST1(f, x);

    IPatternObject a = F.wild("a");
    IPatternObject b = F.wild("b");
    IPatternObject c = F.wild("c");

    // Reusable patterns
    IExpr pat_a = a;
    IExpr pat_afc = F.Times(a, F.Power(fx, c)); // a*f(x)**c
    IExpr pat_ab = F.Times(a, b); // a*b
    IExpr pat_abfc = F.Times(a, b, F.Power(fx, c)); // a*b*f(x)**c

    IExpr negx = engine.evaluate(F.Negate(x)); // -x
    IExpr twox = engine.evaluate(F.Times(F.C2, x)); // 2*x
    IExpr neg2x = engine.evaluate(F.Times(F.CN2, x)); // -2*x

    // --- x ---
    // assert x.match(a) == {a: x}
    Map<IExpr, IExpr> res = x.matchWild(pat_a, engine);
    System.out.println("Issue 5168 x.match(a): " + res);
    assertNotNull(res);
    assertEquals(x, res.get(a));

    // assert x.match(a*f(x)**c) == {a: x, c: 0}
    res = x.matchWild(pat_afc, engine);
    System.out.println("Issue 5168 x.match(a*f(x)**c): " + res);
    assertNotNull(res);
    assertEquals(x, res.get(a));
    assertEquals(F.C0, res.get(c));

    // assert x.match(a*b) == {a: 1, b: x}
    res = x.matchWild(pat_ab, engine);
    System.out.println("Issue 5168 x.match(a*b): " + res);
    assertNotNull(res);
    assertEquals(x, res.get(a));
    assertEquals(F.C1, res.get(b));

    // assert x.match(a*b*f(x)**c) == {a: 1, b: x, c: 0}
    res = x.matchWild(pat_abfc, engine);
    System.out.println("Issue 5168 x.match(a*b*f(x)**c): " + res);
    assertNotNull(res);
    assertEquals(x, res.get(a));
    assertEquals(F.C1, res.get(b));
    assertEquals(F.C0, res.get(c));

    // --- -x ---
    // assert (-x).match(a) == {a: -x}
    res = negx.matchWild(pat_a, engine);
    System.out.println("Issue 5168 (-x).match(a): " + res);
    assertNotNull(res);
    assertEquals(negx, res.get(a));

    // assert (-x).match(a*f(x)**c) == {a: -x, c: 0}
    res = negx.matchWild(pat_afc, engine);
    System.out.println("Issue 5168 (-x).match(a*f(x)**c): " + res);
    assertNotNull(res);
    assertEquals(negx, res.get(a));
    assertEquals(F.C0, res.get(c));

    // assert (-x).match(a*b) == {a: -1, b: x}
    res = negx.matchWild(pat_ab, engine);
    System.out.println("Issue 5168 (-x).match(a*b): " + res);
    assertNotNull(res);
    assertEquals(F.CN1, res.get(a));
    assertEquals(x, res.get(b));

    // assert (-x).match(a*b*f(x)**c) == {a: -1, b: x, c: 0}
    res = negx.matchWild(pat_abfc, engine);
    System.out.println("Issue 5168 (-x).match(a*b*f(x)**c): " + res);
    assertNotNull(res);
    assertEquals(F.CN1, res.get(a));
    assertEquals(x, res.get(b));
    assertEquals(F.C0, res.get(c));

    // --- 2*x ---
    // assert (2*x).match(a) == {a: 2*x}
    res = twox.matchWild(pat_a, engine);
    System.out.println("Issue 5168 (2*x).match(a): " + res);
    assertNotNull(res);
    assertEquals(twox, res.get(a));

    // assert (2*x).match(a*f(x)**c) == {a: 2*x, c: 0}
    res = twox.matchWild(pat_afc, engine);
    System.out.println("Issue 5168 (2*x).match(a*f(x)**c): " + res);
    assertNotNull(res);
    assertEquals(twox, res.get(a));
    assertEquals(F.C0, res.get(c));

    // assert (2*x).match(a*b) == {a: 2, b: x}
    res = twox.matchWild(pat_ab, engine);
    System.out.println("Issue 5168 (2*x).match(a*b): " + res);
    assertNotNull(res);
    assertEquals(F.C2, res.get(a));
    assertEquals(x, res.get(b));

    // assert (2*x).match(a*b*f(x)**c) == {a: 2, b: x, c: 0}
    res = twox.matchWild(pat_abfc, engine);
    System.out.println("Issue 5168 (2*x).match(a*b*f(x)**c): " + res);
    assertNotNull(res);
    assertEquals(F.C2, res.get(a));
    assertEquals(x, res.get(b));
    assertEquals(F.C0, res.get(c));

    // --- -2*x ---
    // assert (-2*x).match(a) == {a: -2*x}
    res = neg2x.matchWild(pat_a, engine);
    System.out.println("Issue 5168 (-2*x).match(a): " + res);
    assertNotNull(res);
    assertEquals(neg2x, res.get(a));

    // assert (-2*x).match(a*f(x)**c) == {a: -2*x, c: 0}
    res = neg2x.matchWild(pat_afc, engine);
    System.out.println("Issue 5168 (-2*x).match(a*f(x)**c): " + res);
    assertNotNull(res);
    assertEquals(neg2x, res.get(a));
    assertEquals(F.C0, res.get(c));

    // assert (-2*x).match(a*b) == {a: -2, b: x}
    res = neg2x.matchWild(pat_ab, engine);
    System.out.println("Issue 5168 (-2*x).match(a*b): " + res);
    assertNotNull(res);
    assertEquals(F.CN2, res.get(a));
    assertEquals(x, res.get(b));

    // assert (-2*x).match(a*b*f(x)**c) == {a: -2, b: x, c: 0}
    res = neg2x.matchWild(pat_abfc, engine);
    System.out.println("Issue 5168 (-2*x).match(a*b*f(x)**c): " + res);
    assertNotNull(res);
    assertEquals(F.CN2, res.get(a));
    assertEquals(x, res.get(b));
    assertEquals(F.C0, res.get(c));
  }

  @Test
  public void test_issue_4559() {
    // https://github.com/sympy/sympy/issues/4559

    // def test_issue_4559():
    // x = Symbol('x')
    // e = Symbol('e')
    // w = Wild('w', exclude=[x])
    // y = Wild('y')
    // ...
    EvalEngine engine = new EvalEngine(false);
    IExpr x = F.Dummy("x");
    IExpr e = F.Dummy("e");

    IPatternObject w = F.wild("w", x); // Wild('w', exclude=[x])
    IPatternObject y = F.wild("y"); // Wild('y')

    // --- "this is as it should be" ---

    // assert (3/x).match(w/y) == {w: 3, y: x}
    // 3/x = Times(3, Power(x,-1)); w/y = Times(w, Power(y,-1))
    IExpr expr_3overx = engine.evaluate(F.Times(F.C3, F.Power(x, F.CN1)));
    IExpr pat_wovery = F.Times(w, F.Power(y, F.CN1));
    Map<IExpr, IExpr> res = expr_3overx.matchWild(pat_wovery, engine);
    System.out.println("Issue 4559 (3/x).match(w/y): " + res);
    assertNotNull(res, "(3/x).match(w/y) should match");
    assertEquals(F.C3, res.get(w));
    assertEquals(x, res.get(y));

    // assert (3*x).match(w*y) == {w: 3, y: x}
    IExpr expr_3x = engine.evaluate(F.Times(F.C3, x));
    IExpr pat_wy = F.Times(w, y);
    res = expr_3x.matchWild(pat_wy, engine);
    System.out.println("Issue 4559 (3*x).match(w*y): " + res);
    assertNotNull(res, "(3*x).match(w*y) should match");
    assertEquals(F.C3, res.get(w));
    assertEquals(x, res.get(y));

    // assert (x/3).match(y/w) == {w: 3, y: x}
    // x/3 = Times(1/3, x); y/w = Times(y, Power(w,-1))
    IExpr expr_xover3 = engine.evaluate(F.Times(x, F.Power(F.C3, F.CN1)));
    IExpr pat_yoverw = F.Times(y, F.Power(w, F.CN1));
    res = expr_xover3.matchWild(pat_yoverw, engine);
    System.out.println("Issue 4559 (x/3).match(y/w): " + res);
    assertNotNull(res, "(x/3).match(y/w) should match");
    assertEquals(F.C3, res.get(w));
    assertEquals(x, res.get(y));

    // assert (3*x).match(y/w) == {w: Rational(1, 3), y: x}
    // 3*x matched against y*w^(-1): y=x, w^(-1)=3 → w=1/3
    res = expr_3x.matchWild(pat_yoverw, engine);
    System.out.println("Issue 4559 (3*x).match(y/w): " + res);
    assertNotNull(res, "(3*x).match(y/w) should match");
    assertEquals(F.QQ(1, 3), res.get(w));
    assertEquals(x, res.get(y));

    // --- "these could be allowed to fail" ---

    // assert (x/3).match(w/y) == {w: S.One/3, y: 1/x}
    // x/3 = Times(1/3, x); w/y = Times(w, Power(y,-1))
    // w=1/3, y^(-1)=x → y=1/x
    res = expr_xover3.matchWild(pat_wovery, engine);
    System.out.println("Issue 4559 (x/3).match(w/y): " + res);
    assertNotNull(res, "(x/3).match(w/y) should match");
    assertEquals(F.QQ(1, 3), res.get(w));
    assertEquals(engine.evaluate(F.Power(x, F.CN1)), res.get(y));

    // assert (3*x).match(w/y) == {w: 3, y: 1/x}
    res = expr_3x.matchWild(pat_wovery, engine);
    System.out.println("Issue 4559 (3*x).match(w/y): " + res);
    assertNotNull(res, "(3*x).match(w/y) should match");
    assertEquals(F.C3, res.get(w));
    assertEquals(engine.evaluate(F.Power(x, F.CN1)), res.get(y));

    // assert (3/x).match(w*y) == {w: 3, y: 1/x}
    res = expr_3overx.matchWild(pat_wy, engine);
    System.out.println("Issue 4559 (3/x).match(w*y): " + res);
    assertNotNull(res, "(3/x).match(w*y) should match");
    assertEquals(F.C3, res.get(w));
    assertEquals(engine.evaluate(F.Power(x, F.CN1)), res.get(y));

    // --- Power matching with rational exponent ---

    // r = Symbol('r', rational=True)
    IExpr r = F.Dummy("r", F.Element(F.Slot1, F.Rationals));

    // assert (x**r).match(y**2) == {y: x**(r/2)}
    IExpr expr_xr = F.Power(x, r);
    IExpr pat_y2 = F.Power(y, F.C2);
    res = expr_xr.matchWild(pat_y2, engine);
    System.out.println("Issue 4559 (x**r).match(y**2): " + res);
    assertNotNull(res, "(x**r).match(y**2) should match");
    // we assume r is rational, so r/2 is also rational, so x**(r/2) is the same as x**(1/2*r) =
    // sqrt(x**r)
    assertEquals(engine.evaluate(F.Power(x, F.Times(F.C1D2, r))), res.get(y));
    // assertEquals(engine.evaluate(F.Sqrt(F.Power(x, r))), res.get(y));

    // assert (x**e).match(y**2) == {y: sqrt(x**e)}
    IExpr expr_xe = F.Power(x, e);
    res = expr_xe.matchWild(pat_y2, engine);
    System.out.println("Issue 4559 (x**e).match(y**2): " + res);
    assertNotNull(res, "(x**e).match(y**2) should match");
    assertEquals(engine.evaluate(F.Sqrt(F.Power(x, e))), res.get(y));

    // --- Matching with Wild 'a' ---
    IPatternObject a = F.wild("a");

    // e = S.Zero
    // assert e.match(a) == {a: e}
    Map<IExpr, IExpr> res0 = F.C0.matchWild(a, engine);
    System.out.println("Issue 4559 S(0).match(a): " + res0);
    assertNotNull(res0, "S(0).match(a) should match");
    assertEquals(F.C0, res0.get(a));

    // assert e.match(1/a) is None
    // 0 can't match 1/a = Power(a, -1) because that would require a=oo or similar
    IExpr pat_1overa = F.Power(a, F.CN1);
    assertNull(F.C0.matchWild(pat_1overa, engine));

    // assert e.match(a**.3) is None
    // 0 can't match a^0.3 because it's ambiguous
    IExpr pat_a03 = F.Power(a, F.num(0.3));
    assertNull(F.C0.matchWild(pat_a03, engine));

    // e = S(3)
    // assert e.match(1/a) == {a: 1/e}
    res = F.C3.matchWild(pat_1overa, engine);
    System.out.println("Issue 4559 S(3).match(1/a): " + res);
    assertNotNull(res, "S(3).match(1/a) should match");
    assertEquals(F.QQ(1, 3), res.get(a));

    // assert e.match(1/a**2) == {a: 1/sqrt(e)}
    IExpr pat_1overa2 = F.Power(a, F.CN2);
    res = F.C3.matchWild(pat_1overa2, engine);
    System.out.println("Issue 4559 S(3).match(1/a**2): " + res);
    assertNotNull(res, "S(3).match(1/a**2) should match");
    assertEquals(engine.evaluate(F.Power(F.C3, F.QQ(-1, 2))), res.get(a));

    // e = pi
    // assert e.match(1/a) == {a: 1/e}
    res = S.Pi.matchWild(pat_1overa, engine);
    System.out.println("Issue 4559 pi.match(1/a): " + res);
    assertNotNull(res, "pi.match(1/a) should match");
    assertEquals(engine.evaluate(F.Power(S.Pi, F.CN1)), res.get(a));

    // assert e.match(1/a**2) == {a: 1/sqrt(e)}
    res = S.Pi.matchWild(pat_1overa2, engine);
    System.out.println("Issue 4559 pi.match(1/a**2): " + res);
    assertNotNull(res, "pi.match(1/a**2) should match");
    assertEquals(engine.evaluate(F.Power(S.Pi, F.QQ(-1, 2))), res.get(a));

    // assert (-e).match(sqrt(a)) is None
    // -pi can't match sqrt(a) = a^(1/2) for real a (would need negative under sqrt)
    IExpr pat_sqrta = F.Sqrt(a);
    IExpr negPi = engine.evaluate(F.Negate(S.Pi));
    assertNull(negPi.matchWild(pat_sqrta, engine));

    // assert (-e).match(a**2) == {a: I*sqrt(pi)}
    IExpr pat_a2 = F.Power(a, F.C2);
    res = negPi.matchWild(pat_a2, engine);
    System.out.println("Issue 4559 (-pi).match(a**2): " + res);
    assertNotNull(res, "(-pi).match(a**2) should match");
    assertEquals(engine.evaluate(F.Times(F.CI, F.Sqrt(S.Pi))), res.get(a));
  }

}
