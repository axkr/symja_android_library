package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$p;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN2;
import static org.matheclipse.core.expression.F.Coefficient;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.f_;
import static org.matheclipse.core.expression.F.f_DEFAULT;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_;
import static org.matheclipse.core.expression.F.p_DEFAULT;
import static org.matheclipse.core.expression.F.q_;
import static org.matheclipse.core.expression.F.q_DEFAULT;
import static org.matheclipse.core.expression.F.u_;
import static org.matheclipse.core.expression.F.v_;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.d;
import static org.matheclipse.core.expression.S.e;
import static org.matheclipse.core.expression.S.f;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.q;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.v;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandIntegrand;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FracPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ILtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LinearPairQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PosQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Subst;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Unintegrable;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules78 {
  public static IAST RULES =
      List(
          IIntegrate(1561,
              Integrate(Times(Power(Times(f_DEFAULT, x_), m_DEFAULT),
                  Power(Plus(a_, Times(c_DEFAULT, Power(x_, $p("n2", true)))), p_DEFAULT), Power(
                      Plus(d_, Times(e_DEFAULT, Power(x_, n_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Times(Power(Times(f, x), m), Power(Plus(d, Times(e, Power(x, n))), q),
                              Power(Plus(a, Times(c, Power(x, Times(C2, n)))), p)),
                          x),
                      x),
                  And(FreeQ(List(a, c, d, e, f, m, n, p, q), x), EqQ($s("n2"), Times(C2, n)),
                      Or(IGtQ(p, C0), IGtQ(q, C0))))),
          IIntegrate(1562,
              Integrate(
                  Times(Power(Times(f_DEFAULT, x_), m_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT,
                          Power(x_, n_))), q_),
                      Power(Plus(a_, Times(c_DEFAULT, Power(x_, $p("n2")))), p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Times(f,
                          x), m), Power(Power(x, m),
                              CN1)),
                      Integrate(
                          ExpandIntegrand(
                              Times(Power(x, m),
                                  Power(Plus(a, Times(c, Power(x, Times(C2, n)))), p)),
                              Power(
                                  Subtract(
                                      Times(d,
                                          Power(Subtract(Sqr(d),
                                              Times(Sqr(e), Power(x, Times(C2, n)))), CN1)),
                                      Times(e, Power(x, n),
                                          Power(Subtract(Sqr(d),
                                              Times(Sqr(e), Power(x, Times(C2, n)))), CN1))),
                                  Negate(q)),
                              x),
                          x),
                      x),
                  And(FreeQ(List(a, c, d, e, f, m, n,
                      p), x), EqQ($s("n2"),
                          Times(C2, n)),
                      Not(IntegerQ(p)), ILtQ(q, C0)))),
          IIntegrate(1563,
              Integrate(Times(Power(Times(f_DEFAULT, x_), m_DEFAULT),
                  Power(Plus(a_, Times(c_DEFAULT, Power(x_, $p("n2", true))), Times(b_DEFAULT,
                      Power(x_, n_))), p_DEFAULT),
                  Power(Plus(d_, Times(e_DEFAULT, Power(x_, n_))), q_DEFAULT)), x_Symbol),
              Condition(
                  Unintegrable(
                      Times(
                          Power(Times(f, x), m), Power(Plus(d,
                              Times(e, Power(x, n))), q),
                          Power(Plus(a, Times(b, Power(x, n)), Times(c, Power(x, Times(C2, n)))),
                              p)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, n, p, q), x), EqQ($s("n2"), Times(C2, n))))),
          IIntegrate(1564,
              Integrate(
                  Times(Power(Times(f_DEFAULT, x_), m_DEFAULT),
                      Power(Plus(a_, Times(c_DEFAULT,
                          Power(x_, $p("n2", true)))), p_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Power(x_, n_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Unintegrable(Times(Power(Times(f, x), m),
                      Power(Plus(d, Times(e, Power(x, n))),
                          q),
                      Power(Plus(a, Times(c, Power(x, Times(C2, n)))), p)), x),
                  And(FreeQ(List(a, c, d, e, f, m, n, p, q), x), EqQ($s("n2"), Times(C2, n))))),
          IIntegrate(1565,
              Integrate(
                  Times(Power(u_, m_DEFAULT),
                      Power(
                          Plus(
                              a_, Times(c_DEFAULT, Power(v_, $p("n2", true))), Times(b_DEFAULT,
                                  Power(v_, n_))),
                          p_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Power(v_, n_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(u, m), Power(Times(Coefficient(v, x, C1), Power(v, m)),
                          CN1)),
                      Subst(
                          Integrate(Times(Power(x, m), Power(Plus(d, Times(e, Power(x, n))), q),
                              Power(
                                  Plus(a, Times(b, Power(x, n)), Times(c, Power(x, Times(C2, n)))),
                                  p)),
                              x),
                          x, v),
                      x),
                  And(FreeQ(List(a, b, c, d, e, m, n, p, q), x), EqQ($s("n2"), Times(C2,
                      n)), LinearPairQ(u, v,
                          x),
                      NeQ(v, x)))),
          IIntegrate(1566,
              Integrate(Times(Power(u_, m_DEFAULT),
                  Power(Plus(a_, Times(c_DEFAULT, Power(v_, $p("n2", true)))), p_DEFAULT), Power(
                      Plus(d_, Times(e_DEFAULT, Power(v_, n_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Times(Power(u, m), Power(Times(Coefficient(v, x, C1), Power(v, m)), CN1)),
                      Subst(
                          Integrate(
                              Times(
                                  Power(x, m), Power(Plus(d, Times(e, Power(x, n))), q), Power(Plus(
                                      a, Times(c, Power(x, Times(C2, n)))), p)),
                              x),
                          x, v),
                      x),
                  And(FreeQ(List(a, c, d, e, m, n, p), x), EqQ($s("n2"), Times(C2, n)),
                      LinearPairQ(u, v, x), NeQ(v, x)))),
          IIntegrate(1567,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Power(x_, $p("mn", true)))), q_DEFAULT),
                      Power(
                          Plus(
                              a_DEFAULT, Times(b_DEFAULT, Power(x_, n_DEFAULT)), Times(c_DEFAULT,
                                  Power(x_, $p("n2", true)))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(
                          Power(x, Subtract(m, Times(n,
                              q))),
                          Power(Plus(e,
                              Times(d, Power(x, n))), q),
                          Power(Plus(a, Times(b, Power(x, n)), Times(c, Power(x, Times(C2, n)))),
                              p)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, m, n, p), x), EqQ($s("n2"), Times(C2,
                      n)), EqQ($s("mn"),
                          Negate(n)),
                      IntegerQ(q), Or(PosQ(n), Not(IntegerQ(p)))))),
          IIntegrate(1568,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT,
                          Power(x_, $p("mn", true)))), q_DEFAULT),
                      Power(Plus(a_, Times(c_DEFAULT, Power(x_, $p("n2", true)))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(Power(x, Plus(m, Times($s("mn"), q))),
                          Power(Plus(e, Times(d, Power(Power(x, $s("mn")), CN1))),
                              q),
                          Power(Plus(a, Times(c, Power(x, $s("n2")))), p)),
                      x),
                  And(FreeQ(List(a, c, d, e, m, $s("mn"), p), x),
                      EqQ($s("n2"), Times(CN2,
                          $s("mn"))),
                      IntegerQ(q), Or(PosQ($s("n2")), Not(IntegerQ(p)))))),
          IIntegrate(1569,
              Integrate(
                  Times(Power(x_, m_DEFAULT), Power(Plus(a_DEFAULT,
                      Times(b_DEFAULT, Power(x_, $p("mn", true))), Times(c_DEFAULT,
                          Power(x_, $p("mn2", true)))),
                      p_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Power(x_, n_DEFAULT))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(
                          Power(x, Subtract(m, Times(C2, n,
                              p))),
                          Power(Plus(d,
                              Times(e, Power(x, n))), q),
                          Power(Plus(c, Times(b, Power(x, n)), Times(a, Power(x, Times(C2, n)))),
                              p)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, m, n, q), x), EqQ($s("mn"), Negate(
                      n)), EqQ($s("mn2"),
                          Times(C2, $s("mn"))),
                      IntegerQ(p)))),
          IIntegrate(1570,
              Integrate(
                  Times(
                      Power(x_, m_DEFAULT),
                      Power(Plus(a_DEFAULT, Times(c_DEFAULT,
                          Power(x_, $p("mn2", true)))), p_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Power(x_, n_DEFAULT))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(Power(x, Subtract(m, Times(C2, n, p))),
                          Power(Plus(d, Times(e, Power(x, n))),
                              q),
                          Power(Plus(c, Times(a, Power(x, Times(C2, n)))), p)),
                      x),
                  And(FreeQ(List(a, c, d, e, m, n,
                      q), x), EqQ($s("mn2"),
                          Times(CN2, n)),
                      IntegerQ(p)))),
          IIntegrate(1571,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Power(x_, $p("mn", true)))), q_),
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, Power(x_, n_DEFAULT)),
                          Times(c_DEFAULT, Power(x_, $p("n2", true)))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(e, IntPart(q)), Power(x, Times(n, FracPart(q))),
                          Power(Plus(d, Times(e, Power(Power(x, n), CN1))), FracPart(q)),
                          Power(Power(Plus(C1, Times(d, Power(x, n), Power(e, CN1))), FracPart(q)),
                              CN1)),
                      Integrate(Times(Power(x, Subtract(m, Times(n, q))),
                          Power(Plus(C1, Times(d, Power(x, n), Power(e, CN1))), q),
                          Power(Plus(a, Times(b, Power(x, n)), Times(c, Power(x, Times(C2, n)))),
                              p)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, m, n, p, q), x), EqQ($s("n2"),
                      Times(C2, n)), EqQ($s("mn"), Negate(n)), Not(IntegerQ(p)), Not(IntegerQ(q)),
                      PosQ(n)))),
          IIntegrate(1572,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT,
                          Power(x_, $p("mn", true)))), q_),
                      Power(Plus(a_, Times(c_DEFAULT, Power(x_, $p("n2", true)))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(e, IntPart(q)),
                          Power(Plus(d, Times(e, Power(x, $s("mn")))), FracPart(q)),
                          Power(Times(Power(x, Times($s("mn"), FracPart(q))),
                              Power(Plus(C1, Times(d, Power(Times(Power(x, $s("mn")), e), CN1))),
                                  FracPart(q))),
                              CN1)),
                      Integrate(
                          Times(Power(x, Plus(m, Times($s("mn"), q))),
                              Power(Plus(C1, Times(d, Power(Times(Power(x, $s("mn")), e), CN1))),
                                  q),
                              Power(Plus(a, Times(c, Power(x, $s("n2")))), p)),
                          x),
                      x),
                  And(FreeQ(List(a, c, d, e, m, $s("mn"), p, q), x),
                      EqQ($s("n2"), Times(CN2, $s(
                          "mn"))),
                      Not(IntegerQ(p)), Not(IntegerQ(q)), PosQ($s("n2"))))),
          IIntegrate(1573,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(
                          Plus(
                              a_DEFAULT, Times(b_DEFAULT, Power(x_, $p("mn", true))), Times(
                                  c_DEFAULT, Power(x_, $p("mn2", true)))),
                          p_),
                      Power(Plus(d_, Times(e_DEFAULT, Power(x_, n_DEFAULT))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(x, Times(C2, n,
                              FracPart(p))),
                          Power(
                              Plus(
                                  a, Times(b, Power(Power(x, n), CN1)), Times(c,
                                      Power(Power(x, Times(C2, n)), CN1))),
                              FracPart(p)),
                          Power(Power(
                              Plus(c, Times(b, Power(x, n)), Times(a, Power(x, Times(C2, n)))),
                              FracPart(p)), CN1)),
                      Integrate(
                          Times(Power(x, Subtract(m, Times(C2, n, p))),
                              Power(Plus(d,
                                  Times(e, Power(x, n))), q),
                              Power(
                                  Plus(c, Times(b, Power(x, n)), Times(a, Power(x, Times(C2, n)))),
                                  p)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, m, n, p, q), x), EqQ($s("mn"), Negate(n)),
                      EqQ($s("mn2"), Times(C2, $s(
                          "mn"))),
                      Not(IntegerQ(p)), Not(IntegerQ(q)), PosQ(n)))),
          IIntegrate(1574,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(Plus(a_DEFAULT, Times(c_DEFAULT,
                          Power(x_, $p("mn2", true)))), p_),
                      Power(Plus(d_, Times(e_DEFAULT, Power(x_, n_DEFAULT))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(x, Times(C2, n, FracPart(p))),
                          Power(Plus(a, Times(c, Power(Power(x, Times(C2, n)), CN1))), FracPart(p)),
                          Power(Power(Plus(c, Times(a, Power(x, Times(C2, n)))), FracPart(p)),
                              CN1)),
                      Integrate(
                          Times(Power(x, Subtract(m, Times(C2, n, p))),
                              Power(Plus(d,
                                  Times(e, Power(x, n))), q),
                              Power(Plus(c, Times(a, Power(x, Times(C2, n)))), p)),
                          x),
                      x),
                  And(FreeQ(List(a, c, d, e, m, n, p, q), x), EqQ($s("mn2"), Times(CN2,
                      n)), Not(
                          IntegerQ(p)),
                      Not(IntegerQ(q)), PosQ(n)))),
          IIntegrate(1575,
              Integrate(
                  Times(Power(Times(f_, x_), m_),
                      Power(Plus(d_, Times(e_DEFAULT, Power(x_, $p("mn", true)))), q_DEFAULT),
                      Power(
                          Plus(a_DEFAULT, Times(b_DEFAULT, Power(x_, n_DEFAULT)),
                              Times(c_DEFAULT, Power(x_, $p("n2", true)))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(f, IntPart(m)), Power(Times(f, x),
                              FracPart(m)),
                          Power(Power(x, FracPart(m)), CN1)),
                      Integrate(Times(Power(x, m), Power(Plus(d, Times(e, Power(x, $s("mn")))), q),
                          Power(Plus(a, Times(b, Power(x, n)), Times(c, Power(x, Times(C2, n)))),
                              p)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, n, p, q), x), EqQ($s("n2"), Times(C2,
                      n)), EqQ($s("mn"),
                          Negate(n))))),
          IIntegrate(1576,
              Integrate(
                  Times(Power(Times(f_, x_), m_),
                      Power(Plus(d_, Times(e_DEFAULT, Power(x_, $p("mn", true)))), q_DEFAULT),
                      Power(Plus(a_, Times(c_DEFAULT, Power(x_, $p("n2", true)))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(f, IntPart(m)), Power(Times(f, x),
                              FracPart(m)),
                          Power(Power(x, FracPart(m)), CN1)),
                      Integrate(
                          Times(
                              Power(x, m), Power(Plus(d, Times(e, Power(x, $s("mn")))),
                                  q),
                              Power(Plus(a, Times(c, Power(x, $s("n2")))), p)),
                          x),
                      x),
                  And(FreeQ(List(a, c, d, e, f, m, $s("mn"), p, q), x),
                      EqQ($s("n2"), Times(CN2, $s("mn")))))),
          IIntegrate(1577,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(
                          Plus(
                              a_, Times(c_DEFAULT, Power(x_, n_DEFAULT)), Times(b_DEFAULT,
                                  Power(x_, $p("mn")))),
                          p_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Power(x_, n_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(
                          Power(x, Subtract(m, Times(n,
                              p))),
                          Power(Plus(d,
                              Times(e, Power(x, n))), q),
                          Power(Plus(b, Times(a, Power(x, n)), Times(c, Power(x, Times(C2, n)))),
                              p)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, m, n, q), x), EqQ($s("mn"), Negate(n)),
                      IntegerQ(p)))),
          IIntegrate(1578,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(
                          Plus(
                              a_, Times(c_DEFAULT, Power(x_, n_DEFAULT)), Times(b_DEFAULT,
                                  Power(x_, $p("mn")))),
                          p_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Power(x_, n_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(x, Times(n, FracPart(
                              p))),
                          Power(Plus(a, Times(b, Power(Power(x, n), CN1)), Times(c, Power(x, n))),
                              FracPart(p)),
                          Power(
                              Power(
                                  Plus(b, Times(a, Power(x, n)), Times(c, Power(x, Times(C2, n)))),
                                  FracPart(p)),
                              CN1)),
                      Integrate(Times(Power(x, Subtract(m, Times(n, p))),
                          Power(Plus(d, Times(e, Power(x, n))), q),
                          Power(Plus(b, Times(a, Power(x, n)), Times(c, Power(x, Times(C2, n)))),
                              p)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, m, n, p,
                      q), x), EqQ($s("mn"),
                          Negate(n)),
                      Not(IntegerQ(p))))),
          IIntegrate(1579,
              Integrate(
                  Times(
                      Power(Times(f_, x_), m_DEFAULT), Power(Plus(a_,
                          Times(c_DEFAULT, Power(x_, n_DEFAULT)), Times(b_DEFAULT,
                              Power(x_, $p("mn")))),
                          p_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Power(x_, n_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(f, IntPart(m)), Power(Times(f, x),
                              FracPart(m)),
                          Power(Power(x, FracPart(m)), CN1)),
                      Integrate(
                          Times(
                              Power(x, m), Power(Plus(d,
                                  Times(e, Power(x, n))), q),
                              Power(
                                  Plus(a, Times(b, Power(Power(x, n), CN1)), Times(c, Power(x, n))),
                                  p)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, n, p, q), x), EqQ($s("mn"), Negate(n))))),
          IIntegrate(1580, Integrate(Times(Power(Times(f_DEFAULT, x_), m_DEFAULT),
              Power(Plus($p("d1"), Times($p("e1", true), Power(x_, $p("non2", true)))), q_DEFAULT),
              Power(Plus($p("d2"), Times($p("e2", true), Power(x_, $p("non2", true)))), q_DEFAULT),
              Power(Plus(a_DEFAULT, Times(b_DEFAULT, Power(x_, n_)),
                  Times(c_DEFAULT, Power(x_, $p("n2")))), p_DEFAULT)),
              x_Symbol),
              Condition(Integrate(
                  Times(Power(Times(f, x), m),
                      Power(Plus(Times($s("d1"), $s("d2")), Times($s("e1"), $s("e2"), Power(x, n))),
                          q),
                      Power(Plus(a, Times(b, Power(x, n)), Times(c, Power(x, Times(C2, n)))), p)),
                  x),
                  And(FreeQ(List(a, b, c, $s("d1"), $s("e1"), $s("d2"), $s("e2"), f, n, p, q), x),
                      EqQ($s("n2"), Times(C2, n)), EqQ($s("non2"), Times(C1D2, n)),
                      EqQ(Plus(Times($s("d2"), $s("e1")), Times($s("d1"), $s("e2"))), C0),
                      Or(IntegerQ(q), And(GtQ($s("d1"), C0), GtQ($s("d2"), C0)))))));
}
