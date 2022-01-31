package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$p;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.A_;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.B_DEFAULT;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C3;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.C5;
import static org.matheclipse.core.expression.F.C6;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.D;
import static org.matheclipse.core.expression.F.Denominator;
import static org.matheclipse.core.expression.F.Equal;
import static org.matheclipse.core.expression.F.Floor;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.MatchQ;
import static org.matheclipse.core.expression.F.Module;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Numerator;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.PolynomialQuotient;
import static org.matheclipse.core.expression.F.PolynomialRemainder;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.QQ;
import static org.matheclipse.core.expression.F.ReplaceAll;
import static org.matheclipse.core.expression.F.Rule;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Simplify;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Sqrt;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Sum;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_;
import static org.matheclipse.core.expression.F.f_DEFAULT;
import static org.matheclipse.core.expression.F.g_DEFAULT;
import static org.matheclipse.core.expression.F.h_DEFAULT;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_;
import static org.matheclipse.core.expression.F.p_DEFAULT;
import static org.matheclipse.core.expression.F.u_;
import static org.matheclipse.core.expression.F.u_DEFAULT;
import static org.matheclipse.core.expression.F.v_;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.ASymbol;
import static org.matheclipse.core.expression.S.BSymbol;
import static org.matheclipse.core.expression.S.QSymbol;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.d;
import static org.matheclipse.core.expression.S.e;
import static org.matheclipse.core.expression.S.f;
import static org.matheclipse.core.expression.S.g;
import static org.matheclipse.core.expression.S.h;
import static org.matheclipse.core.expression.S.i;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.q;
import static org.matheclipse.core.expression.S.r;
import static org.matheclipse.core.expression.S.s;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.v;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Coeff;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandIntegrand;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandToSum;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Expon;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FracPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FractionQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LinearPairQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LinearQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PolyQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PosQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Rt;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Subst;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SubstFor;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules92 {
  public static IAST RULES =
      List(
          IIntegrate(1841,
              Integrate(
                  Times(
                      $p("§pq"), Power(Times(c_, x_), m_), Power(
                          Plus(a_, Times(b_DEFAULT, Power(x_, n_))), p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(c, IntPart(m)), Power(Times(c, x),
                              FracPart(m)),
                          Power(Power(x, FracPart(m)), CN1)),
                      Integrate(
                          Times(Power(x, m), $s("§pq"),
                              Power(Plus(a, Times(b, Power(x, n))), p)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, m, p), x), PolyQ($s("§pq"), x), FractionQ(n)))),
          IIntegrate(1842,
              Integrate(
                  Times(
                      $p("§pq"), Power(x_, m_DEFAULT), Power(
                          Plus(a_, Times(b_DEFAULT, Power(x_, n_))), p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Power(Plus(m,
                          C1), CN1),
                      Subst(
                          Integrate(
                              Times(
                                  ReplaceAll(SubstFor(Power(x, n), $s("§pq"), x),
                                      Rule(x,
                                          Power(x, Simplify(Times(n, Power(Plus(m, C1), CN1)))))),
                                  Power(
                                      Plus(a,
                                          Times(b,
                                              Power(x,
                                                  Simplify(Times(n, Power(Plus(m, C1), CN1)))))),
                                      p)),
                              x),
                          x, Power(x, Plus(m, C1))),
                      x),
                  And(FreeQ(List(a, b, m, n, p), x), PolyQ($s("§pq"), Power(x, n)),
                      IntegerQ(Simplify(Times(n, Power(Plus(m, C1), CN1)))), Not(IntegerQ(n))))),
          IIntegrate(1843,
              Integrate(Times($p("§pq"), Power(Times(c_, x_), m_),
                  Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), p_)), x_Symbol),
              Condition(Dist(
                  Times(Power(c, IntPart(m)), Power(Times(c, x), FracPart(m)),
                      Power(Power(x, FracPart(m)), CN1)),
                  Integrate(Times(Power(x, m), $s("§pq"), Power(Plus(a, Times(b, Power(x, n))), p)),
                      x),
                  x),
                  And(FreeQ(List(a, b, c, m, n, p), x), PolyQ($s("§pq"), Power(x, n)), IntegerQ(
                      Simplify(Times(n, Power(Plus(m, C1), CN1)))), Not(IntegerQ(n))))),
          IIntegrate(1844,
              Integrate(
                  Times($p("§pq"), Power(Times(c_DEFAULT, x_), m_DEFAULT), Power(
                      Plus(a_, Times(b_DEFAULT, Power(x_, n_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Times(
                              Power(Times(c, x), m), $s("§pq"), Power(
                                  Plus(a, Times(b, Power(x, n))), p)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, m, n, p), x), Or(PolyQ($s(
                      "§pq"), x), PolyQ($s("§pq"),
                          Power(x, n))),
                      Not(IGtQ(m, C0))))),
          IIntegrate(1845,
              Integrate(
                  Times(
                      $p("§pq"), Power(u_, m_DEFAULT), Power(Plus(a_,
                          Times(b_DEFAULT, Power(v_, n_DEFAULT))), p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(u, m), Power(Times(Coeff(v, x, C1), Power(v, m)),
                          CN1)),
                      Subst(
                          Integrate(
                              Times(
                                  Power(x, m), SubstFor(v, $s(
                                      "§pq"), x),
                                  Power(Plus(a, Times(b, Power(x, n))), p)),
                              x),
                          x, v),
                      x),
                  And(FreeQ(List(a, b, m, n,
                      p), x), LinearPairQ(u, v,
                          x),
                      PolyQ($s("§pq"), Power(v, n))))),
          IIntegrate(1846,
              Integrate(
                  Times(
                      $p("§pq"), Power(Times(c_DEFAULT,
                          x_), m_DEFAULT),
                      Power(Plus($p("a1"), Times($p("b1", true), Power(x_, n_DEFAULT))), p_DEFAULT),
                      Power(Plus($p("a2"), Times($p("b2", true), Power(x_, n_DEFAULT))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(Power(Times(c, x), m), $s("§pq"),
                          Power(
                              Plus(Times($s("a1"), $s("a2")),
                                  Times($s("b1"), $s("b2"), Power(x, Times(C2, n)))),
                              p)),
                      x),
                  And(FreeQ(List($s("a1"), $s("b1"), $s("a2"), $s("b2"), c, m, n, p), x),
                      PolyQ($s("§pq"), x),
                      EqQ(Plus(Times($s("a2"), $s(
                          "b1")), Times($s("a1"),
                              $s("b2"))),
                          C0),
                      Or(IntegerQ(p), And(GtQ($s("a1"), C0), GtQ($s("a2"), C0)))))),
          IIntegrate(1847,
              Integrate(
                  Times($p("§pq"), Power(Times(c_DEFAULT, x_), m_DEFAULT),
                      Power(Plus($p("a1"),
                          Times($p("b1", true), Power(x_, n_DEFAULT))), p_DEFAULT),
                      Power(Plus($p("a2"), Times($p("b2", true), Power(x_, n_DEFAULT))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Plus($s("a1"), Times($s("b1"), Power(x, n))), FracPart(p)),
                          Power(Plus($s("a2"),
                              Times($s("b2"), Power(x, n))), FracPart(p)),
                          Power(Power(Plus(Times($s("a1"), $s("a2")),
                              Times($s("b1"), $s("b2"), Power(x, Times(C2, n)))), FracPart(p)),
                              CN1)),
                      Integrate(
                          Times(Power(Times(c, x), m), $s("§pq"),
                              Power(
                                  Plus(Times($s("a1"), $s("a2")),
                                      Times($s("b1"), $s("b2"), Power(x, Times(C2, n)))),
                                  p)),
                          x),
                      x),
                  And(FreeQ(List($s("a1"), $s("b1"), $s("a2"), $s("b2"), c, m, n, p), x),
                      PolyQ($s("§pq"), x),
                      EqQ(Plus(Times($s("a2"), $s("b1")), Times($s("a1"), $s("b2"))), C0),
                      Not(And(EqQ(n, C1), LinearQ($s("§pq"), x)))))),
          IIntegrate(1848,
              Integrate(
                  Times(Power(Times(h_DEFAULT, x_), m_DEFAULT),
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_DEFAULT))), p_DEFAULT),
                      Power(Plus(c_, Times(d_DEFAULT, Power(x_, n_DEFAULT))), p_DEFAULT), Plus(e_,
                          Times(f_DEFAULT, Power(x_, n_DEFAULT)), Times(g_DEFAULT,
                              Power(x_, $p("n2", true))))),
                  x_Symbol),
              Condition(
                  Simp(Times(e, Power(Times(h, x), Plus(m, C1)),
                      Power(Plus(a, Times(b, Power(x, n))), Plus(p, C1)),
                      Power(Plus(c, Times(d, Power(x, n))),
                          Plus(p, C1)),
                      Power(Times(a, c, h, Plus(m, C1)), CN1)), x),
                  And(FreeQ(List(a, b, c, d, e, f, g, h, m, n, p), x), EqQ($s("n2"), Times(C2, n)),
                      EqQ(Subtract(Times(a, c, f, Plus(m, C1)),
                          Times(e, Plus(Times(b, c), Times(a, d)),
                              Plus(m, Times(n, Plus(p, C1)), C1))),
                          C0),
                      EqQ(Subtract(Times(a, c, g, Plus(m, C1)),
                          Times(b, d, e, Plus(m, Times(C2, n, Plus(p, C1)), C1))),
                          C0),
                      NeQ(m, CN1)))),
          IIntegrate(1849,
              Integrate(
                  Times(Power(Times(h_DEFAULT, x_), m_DEFAULT),
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_DEFAULT))), p_DEFAULT),
                      Power(Plus(c_,
                          Times(d_DEFAULT, Power(x_, n_DEFAULT))), p_DEFAULT),
                      Plus(e_, Times(g_DEFAULT, Power(x_, $p("n2", true))))),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(e, Power(Times(h, x), Plus(m, C1)),
                          Power(Plus(a, Times(b, Power(x, n))), Plus(p, C1)),
                          Power(Plus(c, Times(d, Power(x, n))), Plus(p,
                              C1)),
                          Power(Times(a, c, h, Plus(m, C1)), CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, g, h, m, n, p), x), EqQ($s("n2"), Times(C2, n)),
                      EqQ(Plus(m, Times(n, Plus(p, C1)),
                          C1), C0),
                      EqQ(Subtract(Times(a, c, g, Plus(m, C1)),
                          Times(b, d, e, Plus(m, Times(C2, n, Plus(p, C1)), C1))), C0),
                      NeQ(m, CN1)))),
          IIntegrate(1850, Integrate(
              Times($p("§pq"), Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_DEFAULT))), p_DEFAULT)),
              x_Symbol),
              Condition(
                  Integrate(ExpandIntegrand(
                      Times($s("§pq"), Power(Plus(a, Times(b, Power(x, n))), p)), x), x),
                  And(FreeQ(List(a, b, n), x), PolyQ($s("§pq"), x), Or(IGtQ(p, C0), EqQ(n, C1))))),
          IIntegrate(1851, Integrate(
              Times($p("§pq"), Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_DEFAULT))), p_)),
              x_Symbol),
              Condition(
                  Integrate(Times(x, PolynomialQuotient($s("§pq"), x, x),
                      Power(Plus(a, Times(b, Power(x, n))), p)), x),
                  And(FreeQ(List(a, b, n, p), x), PolyQ($s("§pq"), x),
                      EqQ(Coeff($s("§pq"), x, C0), C0),
                      Not(MatchQ($s("§pq"),
                          Condition(Times(Power(x, m_DEFAULT), u_DEFAULT), IntegerQ(m))))))),
          IIntegrate(1852,
              Integrate(
                  Times($p("§pq"),
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_DEFAULT))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(PolynomialQuotient($s("§pq"), Plus(a, Times(b, Power(x, n))), x),
                          Power(Plus(a, Times(b, Power(x, n))), Plus(p, C1))),
                      x),
                  And(FreeQ(List(a, b, p), x), PolyQ($s("§pq"), x), IGtQ(n, C0),
                      GeQ(Expon($s("§pq"),
                          x), n),
                      EqQ(PolynomialRemainder($s("§pq"), Plus(a, Times(b, Power(x, n))), x), C0)))),
          IIntegrate(1853,
              Integrate(Times($p("§pq"), Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_DEFAULT))),
                  p_)), x_Symbol),
              Condition(
                  Module(
                      List(Set(q,
                          Expon($s("§pq"), x)), i),
                      Plus(Simp(Times(Power(Plus(a, Times(b, Power(x, n))), p),
                          Sum(Times(Coeff($s("§pq"), x, i), Power(x, Plus(i, C1)),
                              Power(Plus(Times(n, p), i, C1), CN1)), List(i, C0, q))),
                          x),
                          Dist(Times(a, n, p),
                              Integrate(
                                  Times(Power(Plus(a, Times(b, Power(x, n))), Subtract(p, C1)),
                                      Sum(Times(Coeff($s("§pq"), x, i), Power(x, i),
                                          Power(Plus(Times(n, p), i, C1), CN1)),
                                          List(i, C0, q))),
                                  x),
                              x))),
                  And(FreeQ(List(a, b), x), PolyQ($s("§pq"), x), IGtQ(Times(C1D2,
                      Subtract(n, C1)), C0), GtQ(p,
                          C0)))),
          IIntegrate(1854,
              Integrate(Times($p("§pq"),
                  Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), p_)), x_Symbol),
              Condition(
                  Module(
                      List(Set(q,
                          Expon($s("§pq"), x)), i),
                      Condition(
                          Plus(
                              Simp(
                                  Times(
                                      Subtract(
                                          Times(a, Coeff($s("§pq"), x,
                                              q)),
                                          Times(b, x,
                                              ExpandToSum(
                                                  Subtract($s("§pq"), Times(Coeff($s("§pq"), x, q),
                                                      Power(x, q))),
                                                  x))),
                                      Power(Plus(a, Times(b, Power(x, n))), Plus(p, C1)),
                                      Power(Times(a, b, n, Plus(p, C1)), CN1)),
                                  x),
                              Dist(
                                  Power(Times(a, n,
                                      Plus(p, C1)), CN1),
                                  Integrate(Times(
                                      Sum(Times(Plus(Times(n, Plus(p, C1)), i, C1),
                                          Coeff($s("§pq"), x, i), Power(x, i)),
                                          List(i, C0, Subtract(q, C1))),
                                      Power(Plus(a, Times(b, Power(x, n))), Plus(p, C1))), x),
                                  x)),
                          Equal(q, Subtract(n, C1)))),
                  And(FreeQ(List(a, b), x), PolyQ($s("§pq"), x), IGtQ(n, C0), LtQ(p, CN1)))),
          IIntegrate(1855,
              Integrate(
                  Times($p("§pq"), Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_DEFAULT))),
                      p_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(Simp(
                          Times(x, $s("§pq"), Power(Plus(a, Times(b, Power(x, n))), Plus(p, C1)),
                              Power(Times(a, n, Plus(p, C1)), CN1)),
                          x)),
                      Dist(Power(Times(a, n, Plus(p, C1)), CN1),
                          Integrate(
                              Times(
                                  ExpandToSum(Plus(Times(n, Plus(p, C1), $s("§pq")),
                                      D(Times(x, $s("§pq")), x)), x),
                                  Power(Plus(a, Times(b, Power(x, n))), Plus(p, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b), x), PolyQ($s(
                      "§pq"), x), IGtQ(n, C0), LtQ(p,
                          CN1),
                      LtQ(Expon($s("§pq"), x), Subtract(n, C1))))),
          IIntegrate(1856,
              Integrate(
                  Times($p("§p4"), Power(Plus(a_, Times(b_DEFAULT,
                      Power(x_, C4))), QQ(-3L,
                          2L))),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(d, Coeff($s("§p4"), x, C0)), Set(e, Coeff($s(
                              "§p4"), x, C1)),
                          Set(f, Coeff($s("§p4"), x, C3)), Set(g, Coeff($s("§p4"), x, C4))),
                      Condition(
                          Negate(
                              Simp(
                                  Times(
                                      Subtract(
                                          Plus(Times(a, f), Times(C2, a, g,
                                              x)),
                                          Times(b, e, Sqr(x))),
                                      Power(Times(C2, a, b, Sqrt(Plus(a, Times(b, Power(x, C4))))),
                                          CN1)),
                                  x)),
                          EqQ(Plus(Times(b, d), Times(a, g)), C0))),
                  And(FreeQ(List(a,
                      b), x), PolyQ($s("§p4"), x,
                          C4),
                      EqQ(Coeff($s("§p4"), x, C2), C0)))),
          IIntegrate(1857,
              Integrate(
                  Times($p("§p6"), Power(Plus(a_, Times(b_DEFAULT,
                      Power(x_, C4))), QQ(-3L,
                          2L))),
                  x_Symbol),
              Condition(With(
                  List(Set(d, Coeff($s("§p6"), x, C0)), Set(e, Coeff($s("§p6"), x, C2)),
                      Set(f, Coeff($s("§p6"), x, C3)), Set(g, Coeff($s("§p6"), x, C4)),
                      Set(h, Coeff($s("§p6"), x, C6))),
                  Condition(
                      Negate(Simp(Times(
                          Subtract(Subtract(Times(a, f), Times(C2, b, d, x)),
                              Times(C2, a, h, Power(x, C3))),
                          Power(Times(C2, a, b, Sqrt(Plus(a, Times(b, Power(x, C4))))), CN1)), x)),
                      And(EqQ(Subtract(Times(b, e), Times(C3, a, h)), C0),
                          EqQ(Plus(Times(b, d), Times(a, g)), C0)))),
                  And(FreeQ(List(a, b), x), PolyQ($s("§p6"), x, C6), EqQ(Coeff($s("§p6"), x,
                      C1), C0), EqQ(Coeff($s("§p6"), x, C5),
                          C0)))),
          IIntegrate(1858, Integrate(
              Times($p("§pq"), Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_DEFAULT))), p_)),
              x_Symbol),
              Condition(With(List(Set(q, Expon($s("§pq"), x))), Condition(
                  Module(
                      List(Set(QSymbol, PolynomialQuotient(
                          Times(
                              Power(b, Plus(Floor(Times(Subtract(q, C1), Power(n, CN1))), C1)), $s(
                                  "§pq")),
                          Plus(a, Times(b, Power(x, n))), x)), Set(
                              $s("R"),
                              PolynomialRemainder(
                                  Times(Power(b, Plus(Floor(Times(Subtract(q, C1), Power(n, CN1))),
                                      C1)), $s("§pq")),
                                  Plus(a, Times(b, Power(x, n))), x))),
                      Subtract(
                          Dist(
                              Power(
                                  Times(a, n, Plus(p, C1),
                                      Power(b, Plus(Floor(Times(Subtract(q, C1), Power(n, CN1))),
                                          C1))),
                                  CN1),
                              Integrate(Times(Power(Plus(a, Times(b, Power(x, n))), Plus(p, C1)),
                                  ExpandToSum(
                                      Plus(Times(a, n, Plus(p, C1), QSymbol),
                                          Times(n, Plus(p, C1), $s("R")), D(Times(x, $s("R")), x)),
                                      x)),
                                  x),
                              x),
                          Simp(Times(x, $s("R"), Power(Plus(a, Times(b, Power(x, n))), Plus(p, C1)),
                              Power(
                                  Times(a, n, Plus(p, C1),
                                      Power(b,
                                          Plus(Floor(Times(Subtract(q, C1), Power(n, CN1))), C1))),
                                  CN1)),
                              x))),
                  GeQ(q, n))), And(FreeQ(List(a, b), x), PolyQ($s("§pq"), x), IGtQ(n, C0),
                      LtQ(p, CN1)))),
          IIntegrate(1859,
              Integrate(
                  Times(
                      Plus(A_, Times(B_DEFAULT,
                          x_)),
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_, C3))), CN1)),
                  x_Symbol),
              Condition(
                  Dist(Times(Power(BSymbol, C3), Power(b, CN1)),
                      Integrate(
                          Power(
                              Plus(Sqr(ASymbol), Times(CN1, ASymbol, BSymbol, x),
                                  Times(Sqr(BSymbol), Sqr(x))),
                              CN1),
                          x),
                      x),
                  And(FreeQ(List(a, b, ASymbol, BSymbol), x),
                      EqQ(Subtract(Times(a, Power(BSymbol, C3)), Times(b, Power(ASymbol, C3))),
                          C0)))),
          IIntegrate(1860,
              Integrate(
                  Times(
                      Plus(A_, Times(B_DEFAULT, x_)),
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_, C3))), CN1)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(r, Numerator(
                              Rt(Times(a, Power(b, CN1)), C3))),
                          Set(s, Denominator(Rt(Times(a, Power(b, CN1)), C3)))),
                      Plus(
                          Negate(
                              Dist(
                                  Times(
                                      r, Subtract(Times(BSymbol, r), Times(ASymbol,
                                          s)),
                                      Power(Times(C3, a, s), CN1)),
                                  Integrate(Power(Plus(r, Times(s, x)), CN1), x), x)),
                          Dist(Times(r, Power(Times(C3, a, s), CN1)), Integrate(Times(
                              Plus(Times(r, Plus(Times(BSymbol, r), Times(C2, ASymbol, s))),
                                  Times(s, Subtract(Times(BSymbol, r), Times(ASymbol, s)), x)),
                              Power(Plus(Sqr(r), Times(CN1, r, s, x), Times(Sqr(s), Sqr(x))), CN1)),
                              x), x))),
                  And(FreeQ(List(a, b, ASymbol, BSymbol), x),
                      NeQ(Subtract(Times(a, Power(BSymbol, C3)), Times(b, Power(ASymbol, C3))), C0),
                      PosQ(Times(a, Power(b, CN1)))))));
}
