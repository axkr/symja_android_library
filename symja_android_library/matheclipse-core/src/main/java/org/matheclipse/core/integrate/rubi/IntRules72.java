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
import static org.matheclipse.core.expression.F.C4;
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
import static org.matheclipse.core.expression.F.g_DEFAULT;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_;
import static org.matheclipse.core.expression.F.p_DEFAULT;
import static org.matheclipse.core.expression.F.q_;
import static org.matheclipse.core.expression.F.q_DEFAULT;
import static org.matheclipse.core.expression.F.r_DEFAULT;
import static org.matheclipse.core.expression.F.u_;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.ASymbol;
import static org.matheclipse.core.expression.S.BSymbol;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.d;
import static org.matheclipse.core.expression.S.e;
import static org.matheclipse.core.expression.S.f;
import static org.matheclipse.core.expression.S.g;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.q;
import static org.matheclipse.core.expression.S.r;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FracPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LinearQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PosQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Subst;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules72 {
  public static IAST RULES =
      List(
          IIntegrate(1441,
              Integrate(
                  Times(
                      Power(Plus(d_,
                          Times(e_DEFAULT, Power(u_, n_))), q_DEFAULT),
                      Power(
                          Plus(
                              a_, Times(b_DEFAULT, Power(u_, n_)), Times(c_DEFAULT,
                                  Power(u_, $p("n2")))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Power(Coefficient(u, x,
                          C1), CN1),
                      Subst(
                          Integrate(
                              Times(Power(Plus(d, Times(e, Power(x, n))), q),
                                  Power(
                                      Plus(a, Times(b, Power(x, n)),
                                          Times(c, Power(x, Times(C2, n)))),
                                      p)),
                              x),
                          x, u),
                      x),
                  And(FreeQ(List(a, b, c, d, e, n, p, q), x), EqQ($s("n2"), Times(C2,
                      n)), LinearQ(u,
                          x),
                      NeQ(u, x)))),
          IIntegrate(1442,
              Integrate(
                  Times(
                      Power(Plus(d_, Times(e_DEFAULT, Power(u_, n_))), q_DEFAULT), Power(Plus(a_,
                          Times(c_DEFAULT, Power(u_, $p("n2")))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(Coefficient(u, x, C1), CN1),
                      Subst(
                          Integrate(
                              Times(Power(Plus(d, Times(e, Power(x, n))), q),
                                  Power(Plus(a, Times(c, Power(x, Times(C2, n)))), p)),
                              x),
                          x, u),
                      x),
                  And(FreeQ(List(a, c, d, e, n, p, q), x), EqQ($s("n2"), Times(C2,
                      n)), LinearQ(u,
                          x),
                      NeQ(u, x)))),
          IIntegrate(1443,
              Integrate(
                  Times(Power(Plus(d_, Times(e_DEFAULT, Power(x_, $p("mn", true)))), q_DEFAULT),
                      Power(
                          Plus(
                              a_DEFAULT, Times(b_DEFAULT, Power(x_, n_DEFAULT)), Times(c_DEFAULT,
                                  Power(x_, $p("n2", true)))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(Power(Plus(e, Times(d, Power(x, n))), q),
                          Power(Plus(a, Times(b, Power(x, n)),
                              Times(c, Power(x, Times(C2, n)))), p),
                          Power(Power(x, Times(n, q)), CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, n, p), x), EqQ($s("n2"),
                      Times(C2, n)), EqQ($s("mn"), Negate(n)), IntegerQ(q),
                      Or(PosQ(n), Not(IntegerQ(p)))))),
          IIntegrate(1444,
              Integrate(
                  Times(Power(Plus(d_, Times(e_DEFAULT,
                      Power(x_, $p("mn", true)))), q_DEFAULT), Power(Plus(a_,
                          Times(c_DEFAULT, Power(x_, $p("n2", true)))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(Power(x, Times($s("mn"), q)),
                          Power(Plus(e,
                              Times(d, Power(Power(x, $s("mn")), CN1))), q),
                          Power(Plus(a, Times(c, Power(x, $s("n2")))), p)),
                      x),
                  And(FreeQ(List(a, c, d, e, $s("mn"), p), x), EqQ($s("n2"), Times(CN2,
                      $s("mn"))), IntegerQ(
                          q),
                      Or(PosQ($s("n2")), Not(IntegerQ(p)))))),
          IIntegrate(1445,
              Integrate(
                  Times(
                      Power(
                          Plus(
                              a_DEFAULT, Times(b_DEFAULT, Power(x_, $p("mn", true))),
                              Times(c_DEFAULT, Power(x_, $p("mn2", true)))),
                          p_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Power(x_, n_DEFAULT))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(
                          Power(Plus(d,
                              Times(e, Power(x, n))), q),
                          Power(Plus(c, Times(b, Power(x, n)), Times(a, Power(x, Times(C2, n)))),
                              p),
                          Power(Power(x, Times(C2, n, p)), CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, n, q), x), EqQ($s("mn"), Negate(
                      n)), EqQ($s("mn2"),
                          Times(C2, $s("mn"))),
                      IntegerQ(p)))),
          IIntegrate(1446,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT, Times(c_DEFAULT,
                          Power(x_, $p("mn2", true)))), p_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Power(x_, n_DEFAULT))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(Power(Plus(d, Times(e, Power(x, n))), q),
                          Power(Plus(c, Times(a, Power(x, Times(C2, n)))),
                              p),
                          Power(Power(x, Times(C2, n, p)), CN1)),
                      x),
                  And(FreeQ(List(a, c, d, e, n,
                      q), x), EqQ($s("mn2"),
                          Times(CN2, n)),
                      IntegerQ(p)))),
          IIntegrate(1447,
              Integrate(
                  Times(Power(Plus(d_, Times(e_DEFAULT, Power(x_, $p("mn", true)))), q_),
                      Power(
                          Plus(
                              a_DEFAULT, Times(b_DEFAULT, Power(x_, n_DEFAULT)), Times(c_DEFAULT,
                                  Power(x_, $p("n2", true)))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(e, IntPart(q)), Power(x, Times(n, FracPart(q))),
                          Power(Plus(d, Times(e, Power(Power(x, n), CN1))), FracPart(q)),
                          Power(Power(Plus(C1, Times(d, Power(x, n), Power(e, CN1))), FracPart(q)),
                              CN1)),
                      Integrate(Times(Power(Plus(C1, Times(d, Power(x, n), Power(e, CN1))), q),
                          Power(Plus(a, Times(b, Power(x, n)), Times(c, Power(x, Times(C2, n)))),
                              p),
                          Power(Power(x, Times(n, q)), CN1)), x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, n, p, q), x), EqQ($s("n2"), Times(C2,
                      n)), EqQ($s("mn"),
                          Negate(n)),
                      Not(IntegerQ(p)), Not(IntegerQ(q)), PosQ(n)))),
          IIntegrate(1448,
              Integrate(
                  Times(
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
                          Times(Power(x, Times($s("mn"), q)),
                              Power(Plus(C1, Times(d, Power(Times(Power(x, $s("mn")), e), CN1))),
                                  q),
                              Power(Plus(a, Times(c, Power(x, $s("n2")))), p)),
                          x),
                      x),
                  And(FreeQ(List(a, c, d, e, $s("mn"), p, q), x),
                      EqQ($s("n2"), Times(CN2, $s("mn"))), Not(IntegerQ(p)), Not(IntegerQ(q)), PosQ(
                          $s("n2"))))),
          IIntegrate(1449,
              Integrate(
                  Times(
                      Power(
                          Plus(
                              a_DEFAULT, Times(b_DEFAULT, Power(x_,
                                  $p("mn", true))),
                              Times(c_DEFAULT, Power(x_, $p("mn2", true)))),
                          p_),
                      Power(Plus(d_, Times(e_DEFAULT, Power(x_, n_DEFAULT))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(x, Times(C2, n,
                              FracPart(p))),
                          Power(Plus(a, Times(b, Power(Power(x, n), CN1)),
                              Times(c, Power(Power(x, Times(C2, n)), CN1))), FracPart(p)),
                          Power(Power(
                              Plus(c, Times(b, Power(x, n)), Times(a, Power(x, Times(C2, n)))),
                              FracPart(p)), CN1)),
                      Integrate(
                          Times(Power(Plus(d, Times(e, Power(x, n))), q),
                              Power(
                                  Plus(c, Times(b, Power(x, n)),
                                      Times(a, Power(x, Times(C2, n)))),
                                  p),
                              Power(Power(x, Times(C2, n, p)), CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, n, p, q), x), EqQ($s("mn"), Negate(n)),
                      EqQ($s("mn2"), Times(C2,
                          $s("mn"))),
                      Not(IntegerQ(p)), Not(IntegerQ(q)), PosQ(n)))),
          IIntegrate(1450,
              Integrate(
                  Times(
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
                          Times(Power(Plus(d, Times(e, Power(x, n))), q),
                              Power(Plus(c, Times(a, Power(x, Times(C2, n)))),
                                  p),
                              Power(Power(x, Times(C2, n, p)), CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, c, d, e, n, p, q), x), EqQ($s("mn2"), Times(CN2, n)),
                      Not(IntegerQ(p)), Not(IntegerQ(q)), PosQ(n)))),
          IIntegrate(1451,
              Integrate(
                  Times(
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
                          Power(Plus(d,
                              Times(e, Power(x, n))), q),
                          Power(Plus(b, Times(a, Power(x, n)),
                              Times(c, Power(x, Times(C2, n)))), p),
                          Power(Power(x, Times(n, p)), CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, n, q), x), EqQ($s("mn"), Negate(n)), IntegerQ(p)))),
          IIntegrate(1452,
              Integrate(
                  Times(
                      Power(
                          Plus(
                              a_, Times(c_DEFAULT, Power(x_,
                                  n_DEFAULT)),
                              Times(b_DEFAULT, Power(x_, $p("mn")))),
                          p_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Power(x_, n_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(x, Times(n,
                              FracPart(p))),
                          Power(
                              Plus(a, Times(b, Power(Power(x, n),
                                  CN1)), Times(c, Power(x, n))),
                              FracPart(p)),
                          Power(
                              Power(
                                  Plus(b, Times(a, Power(x, n)), Times(c, Power(x, Times(C2, n)))),
                                  FracPart(p)),
                              CN1)),
                      Integrate(Times(Power(Plus(d, Times(e, Power(x, n))), q),
                          Power(Plus(b, Times(a, Power(x, n)), Times(c, Power(x, Times(C2, n)))),
                              p),
                          Power(Power(x, Times(n, p)), CN1)), x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, n, p,
                      q), x), EqQ($s("mn"),
                          Negate(n)),
                      Not(IntegerQ(p))))),
          IIntegrate(1453,
              Integrate(
                  Times(
                      Power(Plus(d_, Times(e_DEFAULT,
                          Power(x_, n_))), q_DEFAULT),
                      Power(Plus(f_,
                          Times(g_DEFAULT, Power(x_, n_))), r_DEFAULT),
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_)),
                          Times(c_DEFAULT, Power(x_, $p("n2")))), p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(Plus(a, Times(b, Power(x, n)),
                              Times(c, Power(x, Times(C2, n)))), FracPart(p)),
                          Power(
                              Times(Power(Times(C4, c), IntPart(p)),
                                  Power(Plus(b, Times(C2, c, Power(x, n))),
                                      Times(C2, FracPart(p)))),
                              CN1)),
                      Integrate(
                          Times(Power(Plus(d, Times(e, Power(x, n))), q),
                              Power(Plus(f, Times(g, Power(x, n))), r), Power(
                                  Plus(b, Times(C2, c, Power(x, n))), Times(C2, p))),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g, n, p, q, r), x), EqQ($s("n2"), Times(C2, n)),
                      EqQ(Subtract(Sqr(b), Times(C4, a, c)), C0), Not(IntegerQ(p))))),
          IIntegrate(1454,
              Integrate(
                  Times(Power(Plus(d_, Times(e_DEFAULT, Power(x_, n_))), q_DEFAULT),
                      Power(Plus(f_, Times(g_DEFAULT, Power(x_, n_))), r_DEFAULT),
                      Power(
                          Plus(a_, Times(b_DEFAULT, Power(x_, n_)),
                              Times(c_DEFAULT, Power(x_, $p("n2")))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(Power(Plus(d, Times(e, Power(x, n))), Plus(p, q)),
                          Power(Plus(f,
                              Times(g, Power(x, n))), r),
                          Power(
                              Plus(Times(a, Power(d, CN1)),
                                  Times(c, Power(x, n), Power(e, CN1))),
                              p)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g, n, q, r), x), EqQ($s("n2"), Times(C2, n)),
                      NeQ(Subtract(Sqr(b), Times(C4, a,
                          c)), C0),
                      EqQ(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a, Sqr(e))),
                          C0),
                      IntegerQ(p)))),
          IIntegrate(1455,
              Integrate(
                  Times(Power(Plus(d_, Times(e_DEFAULT, Power(x_, n_))), q_DEFAULT),
                      Power(Plus(f_, Times(g_DEFAULT, Power(x_, n_))), r_DEFAULT), Power(Plus(a_,
                          Times(c_DEFAULT, Power(x_, $p("n2")))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(Power(Plus(d, Times(e, Power(x, n))), Plus(p, q)),
                          Power(Plus(f, Times(g, Power(x, n))), r),
                          Power(
                              Plus(Times(a, Power(d, CN1)),
                                  Times(c, Power(x, n), Power(e, CN1))),
                              p)),
                      x),
                  And(FreeQ(List(a, c, d, e, f, g, n, q, r), x), EqQ($s("n2"), Times(C2,
                      n)), EqQ(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))),
                          C0),
                      IntegerQ(p)))),
          IIntegrate(1456,
              Integrate(
                  Times(
                      Power(Plus(d_, Times(e_DEFAULT,
                          Power(x_, n_))), q_DEFAULT),
                      Power(Plus(f_,
                          Times(g_DEFAULT, Power(x_, n_))), r_DEFAULT),
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_)),
                          Times(c_DEFAULT, Power(x_, $p("n2")))), p_)),
                  x_Symbol),
              Condition(
                  Dist(Times(
                      Power(Plus(a, Times(b, Power(x, n)), Times(c, Power(x, Times(C2, n)))),
                          FracPart(p)),
                      Power(Times(Power(Plus(d, Times(e, Power(x, n))), FracPart(p)),
                          Power(Plus(Times(a, Power(d, CN1)), Times(c, Power(x, n), Power(e, CN1))),
                              FracPart(p))),
                          CN1)),
                      Integrate(
                          Times(Power(Plus(d, Times(e, Power(x, n))), Plus(p, q)),
                              Power(Plus(f, Times(g, Power(x, n))), r),
                              Power(
                                  Plus(Times(a, Power(d, CN1)),
                                      Times(c, Power(x, n), Power(e, CN1))),
                                  p)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g, n, p, q, r), x), EqQ($s("n2"), Times(C2, n)),
                      NeQ(Subtract(Sqr(b), Times(C4, a,
                          c)), C0),
                      EqQ(Plus(Times(c, Sqr(
                          d)), Times(CN1, b, d,
                              e),
                          Times(a, Sqr(e))), C0),
                      Not(IntegerQ(p))))),
          IIntegrate(1457,
              Integrate(
                  Times(Power(Plus(d_, Times(e_DEFAULT, Power(x_, n_))), q_DEFAULT),
                      Power(Plus(f_, Times(g_DEFAULT,
                          Power(x_, n_))), r_DEFAULT),
                      Power(Plus(a_, Times(c_DEFAULT, Power(x_, $p("n2")))), p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Plus(a, Times(c, Power(x, Times(C2, n)))), FracPart(p)),
                          Power(
                              Times(Power(Plus(d, Times(e, Power(x, n))), FracPart(p)),
                                  Power(
                                      Plus(Times(a, Power(d, CN1)),
                                          Times(c, Power(x, n), Power(e, CN1))),
                                      FracPart(p))),
                              CN1)),
                      Integrate(
                          Times(Power(Plus(d, Times(e, Power(x, n))), Plus(p, q)),
                              Power(Plus(f,
                                  Times(g, Power(x, n))), r),
                              Power(
                                  Plus(Times(a, Power(d, CN1)),
                                      Times(c, Power(x, n), Power(e, CN1))),
                                  p)),
                          x),
                      x),
                  And(FreeQ(List(a, c, d, e, f, g, n, p, q, r), x), EqQ($s("n2"),
                      Times(C2, n)), EqQ(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))), C0),
                      Not(IntegerQ(p))))),
          IIntegrate(1458, Integrate(Times(
              Power(Plus($p("d1"), Times($p("e1", true), Power(x_, $p("non2", true)))), q_DEFAULT),
              Power(Plus($p("d2"), Times($p("e2", true), Power(x_, $p("non2", true)))), q_DEFAULT),
              Power(
                  Plus(a_DEFAULT, Times(b_DEFAULT, Power(x_, n_)),
                      Times(c_DEFAULT, Power(x_, $p("n2")))),
                  p_DEFAULT)),
              x_Symbol),
              Condition(
                  Integrate(
                      Times(Power(Plus(Times($s("d1"), $s("d2")),
                          Times($s("e1"), $s("e2"), Power(x, n))), q),
                          Power(Plus(a, Times(b, Power(x, n)), Times(c, Power(x, Times(C2, n)))),
                              p)),
                      x),
                  And(FreeQ(List(a, b, c, $s("d1"), $s("e1"), $s("d2"), $s("e2"), n, p, q), x),
                      EqQ($s("n2"), Times(C2, n)), EqQ($s("non2"), Times(C1D2, n)),
                      EqQ(Plus(Times($s("d2"), $s("e1")), Times($s("d1"), $s("e2"))), C0), Or(
                          IntegerQ(q), And(GtQ($s("d1"), C0), GtQ($s("d2"), C0)))))),
          IIntegrate(1459,
              Integrate(
                  Times(
                      Power(
                          Plus($p("d1"), Times($p("e1", true),
                              Power(x_, $p("non2", true)))),
                          q_DEFAULT),
                      Power(
                          Plus($p("d2"), Times($p("e2", true),
                              Power(x_, $p("non2", true)))),
                          q_DEFAULT),
                      Power(
                          Plus(
                              a_DEFAULT, Times(b_DEFAULT, Power(x_,
                                  n_)),
                              Times(c_DEFAULT, Power(x_, $p("n2")))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(
                              Plus($s("d1"), Times($s("e1"),
                                  Power(x, Times(C1D2, n)))),
                              FracPart(q)),
                          Power(Plus($s("d2"),
                              Times($s("e2"), Power(x, Times(C1D2, n)))), FracPart(q)),
                          Power(Power(
                              Plus(Times($s("d1"), $s("d2")),
                                  Times($s("e1"), $s("e2"), Power(x, n))),
                              FracPart(q)), CN1)),
                      Integrate(
                          Times(Power(Plus(Times($s("d1"), $s("d2")),
                              Times($s("e1"), $s("e2"), Power(x, n))), q), Power(
                                  Plus(a, Times(b, Power(x, n)), Times(c, Power(x, Times(C2, n)))),
                                  p)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, $s("d1"), $s("e1"), $s("d2"), $s("e2"), n, p, q), x),
                      EqQ($s("n2"), Times(C2, n)), EqQ($s("non2"), Times(C1D2,
                          n)),
                      EqQ(Plus(Times($s("d2"), $s("e1")), Times($s("d1"), $s("e2"))), C0)))),
          IIntegrate(1460,
              Integrate(
                  Times(
                      Plus(A_, Times(B_DEFAULT, Power(x_,
                          m_DEFAULT))),
                      Power(Plus(d_,
                          Times(e_DEFAULT, Power(x_, n_))), q_DEFAULT),
                      Power(
                          Plus(
                              a_, Times(b_DEFAULT, Power(x_, n_)), Times(c_DEFAULT,
                                  Power(x_, $p("n2")))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(ASymbol, Integrate(
                          Times(Power(Plus(d, Times(e, Power(x, n))), q), Power(
                              Plus(a, Times(b, Power(x, n)), Times(c, Power(x, Times(C2, n)))), p)),
                          x), x),
                      Dist(BSymbol, Integrate(
                          Times(Power(x, m), Power(Plus(d, Times(e, Power(x, n))), q), Power(
                              Plus(a, Times(b, Power(x, n)), Times(c, Power(x, Times(C2, n)))), p)),
                          x), x)),
                  And(FreeQ(List(a, b, c, d, e, ASymbol, BSymbol, m, n, p, q), x),
                      EqQ($s("n2"), Times(C2, n)), EqQ(Plus(m, Negate(n), C1), C0)))));
}
