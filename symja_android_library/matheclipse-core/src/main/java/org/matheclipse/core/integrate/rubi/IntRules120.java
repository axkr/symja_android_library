package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$p;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN1D2;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Denominator;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.PolynomialQ;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Sqrt;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.f_;
import static org.matheclipse.core.expression.F.f_DEFAULT;
import static org.matheclipse.core.expression.F.g_DEFAULT;
import static org.matheclipse.core.expression.F.h_DEFAULT;
import static org.matheclipse.core.expression.F.i_DEFAULT;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_;
import static org.matheclipse.core.expression.F.p_DEFAULT;
import static org.matheclipse.core.expression.F.q_DEFAULT;
import static org.matheclipse.core.expression.F.r_;
import static org.matheclipse.core.expression.F.r_DEFAULT;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.True;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.d;
import static org.matheclipse.core.expression.S.e;
import static org.matheclipse.core.expression.S.f;
import static org.matheclipse.core.expression.S.g;
import static org.matheclipse.core.expression.S.h;
import static org.matheclipse.core.expression.S.i;
import static org.matheclipse.core.expression.S.k;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.q;
import static org.matheclipse.core.expression.S.r;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.AlgebraicFunctionQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandIntegrand;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FractionQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntHide;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.InverseFunctionFreeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RationalFunctionQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SimplifyIntegrand;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Subst;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SumQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Unintegrable;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules120 {
  public static IAST RULES =
      List(
          IIntegrate(2401,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(
                                  Log(Times(c_DEFAULT, Power(Plus(d_, Times(e_DEFAULT, x_)),
                                      n_DEFAULT))),
                                  b_DEFAULT)),
                          p_),
                      Power(Plus(f_DEFAULT, Times(g_DEFAULT, x_)), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Times(
                              Power(Plus(f,
                                  Times(g, x)), q),
                              Power(
                                  Plus(a, Times(b, Log(
                                      Times(c, Power(Plus(d, Times(e, x)), n))))),
                                  p)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g, n, p), x),
                      NeQ(Subtract(Times(e, f), Times(d, g)), C0), IGtQ(q, C0)))),
          IIntegrate(2402,
              Integrate(
                  Times(
                      Log(Times(c_DEFAULT,
                          Power(Plus(d_, Times(e_DEFAULT, x_)), CN1))),
                      Power(Plus(f_, Times(g_DEFAULT, Sqr(x_))), CN1)),
                  x_Symbol),
              Condition(
                  Negate(
                      Dist(
                          Times(e, Power(g,
                              CN1)),
                          Subst(
                              Integrate(
                                  Times(Log(Times(C2, d, x)),
                                      Power(Subtract(C1, Times(C2, d, x)), CN1)),
                                  x),
                              x, Power(Plus(d, Times(e, x)), CN1)),
                          x)),
                  And(FreeQ(List(c, d, e, f, g), x), EqQ(c, Times(C2,
                      d)), EqQ(Plus(Times(Sqr(e), f), Times(Sqr(d), g)),
                          C0)))),
          IIntegrate(2403,
              Integrate(
                  Times(
                      Plus(a_DEFAULT,
                          Times(
                              Log(Times(c_DEFAULT, Power(Plus(d_, Times(e_DEFAULT, x_)),
                                  CN1))),
                              b_DEFAULT)),
                      Power(Plus(f_, Times(g_DEFAULT, Sqr(x_))), CN1)),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(
                          Plus(a, Times(b,
                              Log(Times(c, Power(Times(C2, d), CN1))))),
                          Integrate(Power(Plus(f, Times(g, Sqr(x))), CN1), x), x),
                      Dist(b,
                          Integrate(
                              Times(
                                  Log(Times(C2, d,
                                      Power(Plus(d, Times(e, x)), CN1))),
                                  Power(Plus(f, Times(g, Sqr(x))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g), x),
                      EqQ(Plus(Times(Sqr(
                          e), f), Times(Sqr(d),
                              g)),
                          C0),
                      GtQ(Times(c, Power(Times(C2, d), CN1)), C0)))),
          IIntegrate(2404,
              Integrate(
                  Times(
                      Plus(a_DEFAULT,
                          Times(
                              Log(Times(c_DEFAULT, Power(Plus(d_, Times(e_DEFAULT, x_)),
                                  n_DEFAULT))),
                              b_DEFAULT)),
                      Power(Plus(f_, Times(g_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(u,
                          IntHide(Power(Plus(f, Times(g, Sqr(x))), CN1D2), x))),
                      Subtract(
                          Simp(Times(u, Plus(a,
                              Times(b, Log(Times(c, Power(Plus(d, Times(e, x)), n)))))), x),
                          Dist(Times(b, e, n), Integrate(
                              SimplifyIntegrand(Times(u, Power(Plus(d, Times(e, x)), CN1)), x), x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e, f, g, n), x), GtQ(f, C0)))),
          IIntegrate(2405,
              Integrate(
                  Times(
                      Plus(a_DEFAULT,
                          Times(
                              Log(Times(c_DEFAULT, Power(Plus(d_, Times(e_DEFAULT, x_)),
                                  n_DEFAULT))),
                              b_DEFAULT)),
                      Power(Plus($p("f1"), Times($p("g1", true),
                          x_)), CN1D2),
                      Power(Plus($p("f2"), Times($p("g2", true), x_)), CN1D2)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(u,
                              IntHide(
                                  Power(
                                      Plus(Times($s("f1"), $s("f2")), Times($s("g1"), $s("g2"),
                                          Sqr(x))),
                                      CN1D2),
                                  x))),
                      Subtract(
                          Simp(
                              Times(u,
                                  Plus(a, Times(b, Log(Times(c, Power(Plus(d, Times(e, x)), n)))))),
                              x),
                          Dist(Times(b, e, n),
                              Integrate(
                                  SimplifyIntegrand(Times(u, Power(Plus(d, Times(e, x)), CN1)), x),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e, $s("f1"), $s("g1"), $s("f2"), $s("g2"), n), x),
                      EqQ(Plus(Times($s("f2"), $s("g1")), Times($s("f1"),
                          $s("g2"))), C0),
                      GtQ($s("f1"), C0), GtQ($s("f2"), C0)))),
          IIntegrate(2406,
              Integrate(
                  Times(
                      Plus(a_DEFAULT,
                          Times(
                              Log(Times(c_DEFAULT, Power(Plus(d_, Times(e_DEFAULT, x_)),
                                  n_DEFAULT))),
                              b_DEFAULT)),
                      Power(Plus(f_, Times(g_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Sqrt(Plus(C1, Times(g, Sqr(x), Power(f, CN1)))), Power(
                              Plus(f, Times(g, Sqr(x))), CN1D2)),
                      Integrate(
                          Times(
                              Plus(a, Times(b, Log(
                                  Times(c, Power(Plus(d, Times(e, x)), n))))),
                              Power(Plus(C1, Times(g, Sqr(x), Power(f, CN1))), CN1D2)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g, n), x), Not(GtQ(f, C0))))),
          IIntegrate(2407,
              Integrate(
                  Times(
                      Plus(a_DEFAULT,
                          Times(Log(Times(c_DEFAULT,
                              Power(Plus(d_, Times(e_DEFAULT, x_)), n_DEFAULT))), b_DEFAULT)),
                      Power(Plus($p("f1"), Times($p("g1", true), x_)), CN1D2), Power(
                          Plus($p("f2"), Times($p("g2", true), x_)), CN1D2)),
                  x_Symbol),
              Condition(Dist(
                  Times(
                      Sqrt(Plus(C1,
                          Times($s("g1"), $s("g2"), Sqr(x),
                              Power(Times($s("f1"), $s("f2")), CN1)))),
                      Power(Times(Sqrt(Plus($s("f1"), Times($s("g1"), x))),
                          Sqrt(Plus($s("f2"), Times($s("g2"), x)))), CN1)),
                  Integrate(
                      Times(Plus(a, Times(b, Log(Times(c, Power(Plus(d, Times(e, x)), n))))),
                          Power(Plus(C1,
                              Times($s("g1"), $s("g2"), Sqr(x),
                                  Power(Times($s("f1"), $s("f2")), CN1))),
                              CN1D2)),
                      x),
                  x),
                  And(FreeQ(List(a, b, c, d, e, $s("f1"), $s("g1"), $s("f2"), $s("g2"),
                      n), x), EqQ(Plus(Times($s("f2"), $s("g1")), Times($s("f1"), $s("g2"))),
                          C0)))),
          IIntegrate(2408,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(
                                  Log(Times(c_DEFAULT,
                                      Power(Plus(d_, Times(e_DEFAULT, x_)), n_DEFAULT))),
                                  b_DEFAULT)),
                          p_DEFAULT),
                      Power(Plus(f_DEFAULT, Times(g_DEFAULT, Power(x_, r_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(k,
                          Denominator(r))),
                      Dist(k,
                          Subst(
                              Integrate(
                                  Times(Power(x, Subtract(k, C1)),
                                      Power(Plus(f, Times(g, Power(x, Times(k, r)))), q),
                                      Power(
                                          Plus(a,
                                              Times(b,
                                                  Log(Times(c,
                                                      Power(Plus(d, Times(e, Power(x, k))), n))))),
                                          p)),
                                  x),
                              x, Power(x, Power(k, CN1))),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g, n, p, q), x), FractionQ(r), IGtQ(p, C0)))),
          IIntegrate(2409,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(
                              Log(Times(c_DEFAULT,
                                  Power(Plus(d_, Times(e_DEFAULT, x_)), n_DEFAULT))),
                              b_DEFAULT)),
                          p_DEFAULT),
                      Power(Plus(f_, Times(g_DEFAULT, Power(x_, r_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(ExpandIntegrand(
                      Power(Plus(a, Times(b, Log(Times(c, Power(Plus(d, Times(e, x)), n))))),
                          p),
                      Power(Plus(f, Times(g, Power(x, r))), q), x), x),
                  And(FreeQ(List(a, b, c, d, e, f, g, n, r), x), IGtQ(p, C0), IntegerQ(q),
                      Or(GtQ(q, C0), And(IntegerQ(r), NeQ(r, C1)))))),
          IIntegrate(2410,
              Integrate(
                  Times(Log(Times(c_DEFAULT,
                      Plus(d_, Times(e_DEFAULT, x_)))), Power(x_, m_DEFAULT), Power(
                          Plus(f_, Times(g_DEFAULT, x_)), CN1)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Log(Times(c,
                              Plus(d, Times(e, x)))),
                          Times(Power(x, m), Power(Plus(f, Times(g, x)), CN1)), x),
                      x),
                  And(FreeQ(List(c, d, e, f, g), x), EqQ(Subtract(Times(e, f), Times(d,
                      g)), C0), EqQ(Times(c,
                          d), C1),
                      IntegerQ(m)))),
          IIntegrate(2411,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(Log(
                              Times(c_DEFAULT, Power(Plus(d_, Times(e_DEFAULT, x_)), n_DEFAULT))),
                              b_DEFAULT)),
                          p_DEFAULT),
                      Power(Plus(f_DEFAULT, Times(g_DEFAULT, x_)), q_DEFAULT), Power(
                          Plus(h_DEFAULT, Times(i_DEFAULT, x_)), r_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(e, CN1),
                      Subst(
                          Integrate(Times(Power(Times(g, x, Power(e, CN1)), q),
                              Power(Plus(Times(Subtract(Times(e, h), Times(d, i)), Power(e, CN1)),
                                  Times(i, x, Power(e, CN1))), r),
                              Power(Plus(a, Times(b, Log(Times(c, Power(x, n))))), p)), x),
                          x, Plus(d, Times(e, x))),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g, h, i, n, p, q, r), x),
                      EqQ(Subtract(Times(e, f),
                          Times(d, g)), C0),
                      Or(IGtQ(p, C0), IGtQ(r, C0)), IntegerQ(Times(C2, r))))),
          IIntegrate(2412,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(
                                  Log(Times(c_DEFAULT,
                                      Power(Plus(d_, Times(e_DEFAULT, x_)), n_DEFAULT))),
                                  b_DEFAULT)),
                          p_DEFAULT),
                      Power(Plus(f_, Times(g_DEFAULT, Power(x_, CN1))), q_DEFAULT), Power(x_,
                          m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(
                          Power(Plus(g,
                              Times(f, x)), q),
                          Power(Plus(a, Times(b, Log(Times(c, Power(Plus(d, Times(e, x)), n))))),
                              p)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g, m, n, p, q), x), EqQ(m, q), IntegerQ(q)))),
          IIntegrate(2413,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(
                                  Log(Times(c_DEFAULT,
                                      Power(Plus(d_, Times(e_DEFAULT, x_)), n_DEFAULT))),
                                  b_DEFAULT)),
                          p_DEFAULT),
                      Power(x_, m_DEFAULT), Power(Plus(f_DEFAULT,
                          Times(g_DEFAULT, Power(x_, r_DEFAULT))),
                          q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(
                              Power(Plus(f, Times(g,
                                  Power(x, r))), Plus(q,
                                      C1)),
                              Power(Plus(a, Times(b,
                                  Log(Times(c, Power(Plus(d, Times(e, x)), n))))), p),
                              Power(Times(g, r, Plus(q, C1)), CN1)),
                          x),
                      Dist(
                          Times(b, e, n, p, Power(Times(g, r, Plus(q, C1)),
                              CN1)),
                          Integrate(
                              Times(Power(Plus(f, Times(g, Power(x, r))), Plus(q, C1)),
                                  Power(Plus(a, Times(b,
                                      Log(Times(c, Power(Plus(d, Times(e, x)), n))))), Subtract(p,
                                          C1)),
                                  Power(Plus(d, Times(e, x)), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g, m, n, q, r), x), EqQ(m, Subtract(r,
                      C1)), NeQ(q,
                          CN1),
                      IGtQ(p, C0)))),
          IIntegrate(2414,
              Integrate(
                  Times(
                      Plus(a_DEFAULT,
                          Times(
                              Log(Times(c_DEFAULT, Power(Plus(d_, Times(e_DEFAULT, x_)),
                                  n_DEFAULT))),
                              b_DEFAULT)),
                      Power(x_, m_DEFAULT),
                      Power(Plus(f_, Times(g_DEFAULT, Power(x_, r_DEFAULT))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(u,
                              IntHide(
                                  Times(Power(x, m),
                                      Power(Plus(f, Times(g, Power(x, r))), q)),
                                  x))),
                      Condition(
                          Subtract(
                              Dist(
                                  Plus(a, Times(b,
                                      Log(Times(c, Power(Plus(d, Times(e, x)), n))))),
                                  u, x),
                              Dist(Times(b, e, n),
                                  Integrate(
                                      SimplifyIntegrand(Times(u, Power(Plus(d, Times(e, x)), CN1)),
                                          x),
                                      x),
                                  x)),
                          InverseFunctionFreeQ(u, x))),
                  And(FreeQ(List(a, b, c, d, e, f, g, m, n, q, r), x), IntegerQ(m), IntegerQ(q),
                      IntegerQ(r)))),
          IIntegrate(2415,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(
                                  Log(Times(c_DEFAULT,
                                      Power(Plus(d_, Times(e_DEFAULT, x_)), n_DEFAULT))),
                                  b_DEFAULT)),
                          p_DEFAULT),
                      Power(x_, m_DEFAULT), Power(Plus(f_DEFAULT, Times(g_DEFAULT, Power(x_, r_))),
                          q_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(k,
                          Denominator(r))),
                      Dist(k,
                          Subst(
                              Integrate(
                                  Times(Power(x, Subtract(Times(k, Plus(m, C1)), C1)),
                                      Power(Plus(f, Times(g, Power(x, Times(k, r)))), q),
                                      Power(
                                          Plus(a,
                                              Times(b,
                                                  Log(Times(c,
                                                      Power(Plus(d, Times(e, Power(x, k))), n))))),
                                          p)),
                                  x),
                              x, Power(x, Power(k, CN1))),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g, n, p,
                      q), x), FractionQ(
                          r),
                      IGtQ(p, C0), IntegerQ(m)))),
          IIntegrate(2416,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(
                                  Log(Times(c_DEFAULT, Power(Plus(d_, Times(e_DEFAULT, x_)),
                                      n_DEFAULT))),
                                  b_DEFAULT)),
                          p_DEFAULT),
                      Power(Times(h_DEFAULT,
                          x_), m_DEFAULT),
                      Power(Plus(f_, Times(g_DEFAULT, Power(x_, r_DEFAULT))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Power(Plus(a, Times(b, Log(Times(c, Power(Plus(d, Times(e, x)), n))))),
                              p),
                          Times(Power(Times(h,
                              x), m), Power(Plus(f, Times(g, Power(x, r))),
                                  q)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g, h, m, n, p, q, r), x), IntegerQ(m),
                      IntegerQ(q)))),
          IIntegrate(2417,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(
                                  Log(Times(c_DEFAULT,
                                      Power(Plus(d_, Times(e_DEFAULT, x_)), n_DEFAULT))),
                                  b_DEFAULT)),
                          p_DEFAULT),
                      $p("§polyx")),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Times($s("§polyx"),
                              Power(
                                  Plus(a, Times(b, Log(
                                      Times(c, Power(Plus(d, Times(e, x)), n))))),
                                  p)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, n, p), x), PolynomialQ($s("§polyx"), x)))),
          IIntegrate(2418,
              Integrate(
                  Times(Power(Plus(a_DEFAULT,
                      Times(Log(Times(c_DEFAULT, Power(Plus(d_, Times(e_DEFAULT, x_)), n_DEFAULT))),
                          b_DEFAULT)),
                      p_DEFAULT), $p("§rfx")),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(u,
                              ExpandIntegrand(
                                  Power(
                                      Plus(
                                          a,
                                          Times(b, Log(Times(c, Power(Plus(d, Times(e, x)), n))))),
                                      p),
                                  $s("§rfx"), x))),
                      Condition(Integrate(u, x), SumQ(u))),
                  And(FreeQ(List(a, b, c, d, e, n), x), RationalFunctionQ($s("§rfx"), x),
                      IntegerQ(p)))),
          IIntegrate(2419,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(
                                  Log(Times(c_DEFAULT, Power(Plus(d_, Times(e_DEFAULT, x_)),
                                      n_DEFAULT))),
                                  b_DEFAULT)),
                          p_DEFAULT),
                      $p("§rfx")),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(u,
                              ExpandIntegrand(
                                  Times($s("§rfx"),
                                      Power(
                                          Plus(a,
                                              Times(b,
                                                  Log(Times(c, Power(Plus(d, Times(e, x)), n))))),
                                          p)),
                                  x))),
                      Condition(Integrate(u, x), SumQ(u))),
                  And(FreeQ(List(a, b, c, d, e,
                      n), x), RationalFunctionQ($s("§rfx"),
                          x),
                      IntegerQ(p)))),
          IIntegrate(2420,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(
                                  Log(Times(c_DEFAULT,
                                      Power(Plus(d_, Times(e_DEFAULT, x_)), n_DEFAULT))),
                                  b_DEFAULT)),
                          p_DEFAULT),
                      $p("§afx")),
                  x_Symbol),
              Condition(
                  Unintegrable(Times($s("§afx"),
                      Power(Plus(a, Times(b, Log(Times(c, Power(Plus(d, Times(e, x)), n))))), p)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, n, p), x),
                      AlgebraicFunctionQ($s("§afx"), x, True)))));
}
