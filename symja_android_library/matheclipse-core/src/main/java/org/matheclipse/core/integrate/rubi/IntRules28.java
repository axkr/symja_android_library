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
import static org.matheclipse.core.expression.F.Denominator;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.GCD;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Simplify;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.Unequal;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.f_DEFAULT;
import static org.matheclipse.core.expression.F.g_;
import static org.matheclipse.core.expression.F.g_DEFAULT;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_;
import static org.matheclipse.core.expression.F.p_DEFAULT;
import static org.matheclipse.core.expression.F.q_;
import static org.matheclipse.core.expression.F.q_DEFAULT;
import static org.matheclipse.core.expression.F.r_;
import static org.matheclipse.core.expression.F.r_DEFAULT;
import static org.matheclipse.core.expression.F.u_;
import static org.matheclipse.core.expression.F.v_;
import static org.matheclipse.core.expression.F.w_;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.d;
import static org.matheclipse.core.expression.S.e;
import static org.matheclipse.core.expression.S.f;
import static org.matheclipse.core.expression.S.g;
import static org.matheclipse.core.expression.S.k;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.q;
import static org.matheclipse.core.expression.S.r;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.v;
import static org.matheclipse.core.expression.S.w;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandIntegrand;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FracPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FractionQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntegersQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LinearQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NegQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SimplerQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Subst;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules28 {
  public static IAST RULES =
      List(
          IIntegrate(561,
              Integrate(
                  Times(Power(Plus(a_DEFAULT, Times(b_DEFAULT, Power(u_, n_))), p_DEFAULT),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT,
                          Power(v_, n_))), q_DEFAULT),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, Power(w_, n_))), r_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Power(Coefficient(u, x,
                          C1), CN1),
                      Subst(
                          Integrate(Times(Power(Plus(a, Times(b, Power(x, n))), p),
                              Power(Plus(c, Times(d, Power(x, n))),
                                  q),
                              Power(Plus(e, Times(f, Power(x, n))), r)), x),
                          x, u),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, p, n, q, r), x), EqQ(u, v), EqQ(u, w),
                      LinearQ(u, x), NeQ(u, x)))),
          IIntegrate(562,
              Integrate(
                  Times(
                      Power(Plus(c_, Times(d_DEFAULT, Power(x_,
                          $p("mn", true)))), q_DEFAULT),
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT,
                          Power(x_, n_DEFAULT))), p_DEFAULT),
                      Power(Plus(e_, Times(f_DEFAULT, Power(x_, n_DEFAULT))), r_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(Power(Plus(a, Times(b, Power(x, n))), p),
                          Power(Plus(d, Times(c, Power(x, n))), q),
                          Power(Plus(e,
                              Times(f, Power(x, n))), r),
                          Power(Power(x, Times(n, q)), CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, n, p,
                      r), x), EqQ($s("mn"),
                          Negate(n)),
                      IntegerQ(q)))),
          IIntegrate(563,
              Integrate(
                  Times(Power(Plus(c_, Times(d_DEFAULT, Power(x_, $p("mn", true)))), q_DEFAULT),
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT,
                          Power(x_, n_DEFAULT))), p_DEFAULT),
                      Power(Plus(e_, Times(f_DEFAULT, Power(x_, n_DEFAULT))), r_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(Power(x, Times(n, Plus(p, r))),
                          Power(Plus(b, Times(a,
                              Power(Power(x, n), CN1))), p),
                          Power(Plus(c, Times(d, Power(Power(x, n), CN1))), q),
                          Power(Plus(f, Times(e, Power(Power(x, n), CN1))), r)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, n,
                      q), x), EqQ($s("mn"),
                          Negate(n)),
                      IntegerQ(p), IntegerQ(r)))),
          IIntegrate(564,
              Integrate(
                  Times(Power(Plus(c_, Times(d_DEFAULT, Power(x_, $p("mn", true)))), q_),
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT,
                          Power(x_, n_DEFAULT))), p_DEFAULT),
                      Power(Plus(e_, Times(f_DEFAULT, Power(x_, n_DEFAULT))), r_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(x, Times(n, FracPart(q))),
                          Power(Plus(c, Times(d, Power(Power(x, n), CN1))), FracPart(
                              q)),
                          Power(Power(Plus(d, Times(c, Power(x, n))), FracPart(q)), CN1)),
                      Integrate(
                          Times(Power(Plus(a, Times(b, Power(x, n))), p),
                              Power(Plus(d, Times(c, Power(x, n))), q),
                              Power(Plus(e, Times(f, Power(x, n))), r), Power(Power(x, Times(n, q)),
                                  CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, n, p, q, r), x), EqQ($s("mn"), Negate(n)),
                      Not(IntegerQ(q))))),
          IIntegrate(565,
              Integrate(
                  Times(
                      Power(
                          Plus($p("e1"), Times($p("f1", true), Power(x_, $p("n2", true)))),
                          r_DEFAULT),
                      Power(
                          Plus($p("e2"), Times($p("f2", true), Power(x_, $p("n2", true)))),
                          r_DEFAULT),
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), p_DEFAULT),
                      Power(Plus(c_, Times(d_DEFAULT, Power(x_, n_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(Power(Plus(a, Times(b, Power(x, n))), p),
                          Power(Plus(c, Times(d, Power(x, n))), q),
                          Power(
                              Plus(Times($s("e1"), $s("e2")),
                                  Times($s("f1"), $s("f2"), Power(x, n))),
                              r)),
                      x),
                  And(FreeQ(
                      List(a, b, c, d, $s("e1"), $s("f1"), $s("e2"), $s("f2"), n, p, q, r), x),
                      EqQ($s("n2"), Times(C1D2, n)),
                      EqQ(Plus(Times($s("e2"), $s("f1")),
                          Times($s("e1"), $s("f2"))), C0),
                      Or(IntegerQ(r), And(GtQ($s("e1"), C0), GtQ($s("e2"), C0)))))),
          IIntegrate(566,
              Integrate(
                  Times(
                      Power(
                          Plus($p("e1"),
                              Times($p("f1", true), Power(x_, $p("n2", true)))),
                          r_DEFAULT),
                      Power(Plus($p("e2"), Times($p("f2", true), Power(x_,
                          $p("n2", true)))), r_DEFAULT),
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), p_DEFAULT),
                      Power(Plus(c_, Times(d_DEFAULT, Power(x_, n_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(
                              Plus($s("e1"), Times($s("f1"),
                                  Power(x, Times(C1D2, n)))),
                              FracPart(r)),
                          Power(Plus($s("e2"), Times($s("f2"), Power(x, Times(C1D2, n)))),
                              FracPart(r)),
                          Power(
                              Power(Plus(Times($s("e1"), $s("e2")),
                                  Times($s("f1"), $s("f2"), Power(x, n))), FracPart(r)),
                              CN1)),
                      Integrate(Times(Power(Plus(a, Times(b, Power(x, n))), p),
                          Power(Plus(c, Times(d, Power(x, n))), q),
                          Power(Plus(Times($s("e1"), $s("e2")),
                              Times($s("f1"), $s("f2"), Power(x, n))), r)),
                          x),
                      x),
                  And(FreeQ(
                      List(a, b, c, d, $s("e1"), $s("f1"), $s("e2"), $s("f2"), n, p, q, r), x),
                      EqQ($s("n2"), Times(C1D2,
                          n)),
                      EqQ(Plus(Times($s("e2"), $s("f1")), Times($s("e1"), $s("f2"))), C0)))),
          IIntegrate(567,
              Integrate(
                  Times(Power(Times(g_DEFAULT, x_), m_DEFAULT),
                      Power(Times(b_DEFAULT, Power(x_, n_)), p_),
                      Power(Plus(c_, Times(d_DEFAULT,
                          Power(x_, n_))), q_DEFAULT),
                      Power(Plus(e_, Times(f_DEFAULT, Power(x_, n_))), r_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(g, m),
                          Power(
                              Times(n,
                                  Power(
                                      b, Subtract(Simplify(Times(Plus(m, C1), Power(n, CN1))),
                                          C1))),
                              CN1)),
                      Subst(
                          Integrate(
                              Times(Power(Times(b, x),
                                  Subtract(Plus(p, Simplify(Times(Plus(m, C1), Power(n, CN1)))),
                                      C1)),
                                  Power(Plus(c, Times(d, x)), q), Power(Plus(e, Times(f, x)), r)),
                              x),
                          x, Power(x, n)),
                      x),
                  And(FreeQ(List(b, c, d, e, f, g, m, n, p, q, r), x), Or(IntegerQ(m), GtQ(g, C0)),
                      IntegerQ(Simplify(Times(Plus(m, C1), Power(n, CN1))))))),
          IIntegrate(568,
              Integrate(
                  Times(Power(Times(g_DEFAULT, x_), m_DEFAULT),
                      Power(Times(b_DEFAULT, Power(x_, n_DEFAULT)), p_),
                      Power(Plus(c_, Times(d_DEFAULT, Power(x_, n_))), q_DEFAULT), Power(Plus(e_,
                          Times(f_DEFAULT, Power(x_, n_))), r_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(g, m), Power(b, IntPart(p)),
                          Power(Times(b,
                              Power(x, n)), FracPart(p)),
                          Power(Power(x, Times(n, FracPart(p))), CN1)),
                      Integrate(
                          Times(Power(x, Plus(m, Times(n, p))),
                              Power(Plus(c, Times(d, Power(x, n))),
                                  q),
                              Power(Plus(e, Times(f, Power(x, n))), r)),
                          x),
                      x),
                  And(FreeQ(List(b, c, d, e, f, g, m, n, p, q, r), x), Or(IntegerQ(m), GtQ(g,
                      C0)), Not(
                          IntegerQ(Simplify(Times(Plus(m, C1), Power(n, CN1)))))))),
          IIntegrate(569,
              Integrate(
                  Times(Power(Times(g_, x_), m_), Power(Times(b_DEFAULT, Power(x_, n_DEFAULT)), p_),
                      Power(Plus(c_, Times(d_DEFAULT, Power(x_, n_))), q_DEFAULT), Power(Plus(e_,
                          Times(f_DEFAULT, Power(x_, n_))), r_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(g, IntPart(m)), Power(Times(g, x),
                          FracPart(m)), Power(Power(x, FracPart(m)), CN1)),
                      Integrate(
                          Times(Power(x, m), Power(Times(b, Power(x, n)), p),
                              Power(Plus(c, Times(d, Power(x, n))), q),
                              Power(Plus(e, Times(f, Power(x, n))), r)),
                          x),
                      x),
                  And(FreeQ(List(b, c, d, e, f, g, m, n, p, q, r), x), Not(IntegerQ(m))))),
          IIntegrate(570,
              Integrate(
                  Times(Power(Times(g_DEFAULT, x_), m_DEFAULT),
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), p_DEFAULT),
                      Power(Plus(c_, Times(d_DEFAULT,
                          Power(x_, n_))), q_DEFAULT),
                      Power(Plus(e_, Times(f_DEFAULT, Power(x_, n_))), r_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Times(Power(Times(g, x), m), Power(Plus(a, Times(b, Power(x, n))), p),
                              Power(Plus(c, Times(d, Power(x, n))),
                                  q),
                              Power(Plus(e, Times(f, Power(x, n))), r)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g, m, n), x), IGtQ(p, CN2), IGtQ(q, C0), IGtQ(r,
                      C0)))),
          IIntegrate(571,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), p_DEFAULT),
                      Power(Plus(c_, Times(d_DEFAULT, Power(x_, n_))), q_DEFAULT), Power(Plus(e_,
                          Times(f_DEFAULT, Power(x_, n_))), r_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(n, CN1),
                      Subst(
                          Integrate(
                              Times(Power(Plus(a, Times(b, x)), p), Power(Plus(c, Times(d, x)), q),
                                  Power(Plus(e, Times(f, x)), r)),
                              x),
                          x, Power(x, n)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, n, p, q, r), x),
                      EqQ(Plus(m, Negate(n), C1), C0)))),
          IIntegrate(572,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), p_DEFAULT),
                      Power(Plus(c_, Times(d_DEFAULT, Power(x_, n_))), q_DEFAULT), Power(Plus(e_,
                          Times(f_DEFAULT, Power(x_, n_))), r_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(
                          Power(x, Plus(m, Times(n, Plus(p, q,
                              r)))),
                          Power(Plus(b, Times(a, Power(Power(x, n), CN1))), p),
                          Power(Plus(d, Times(c, Power(Power(x, n), CN1))), q),
                          Power(Plus(f, Times(e, Power(Power(x, n), CN1))), r)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, n), x), IntegersQ(p, q, r), NegQ(n)))),
          IIntegrate(573,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), p_DEFAULT),
                      Power(Plus(c_, Times(d_DEFAULT,
                          Power(x_, n_))), q_DEFAULT),
                      Power(Plus(e_, Times(f_DEFAULT, Power(x_, n_))), r_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(n, CN1),
                      Subst(
                          Integrate(
                              Times(
                                  Power(x,
                                      Subtract(Simplify(Times(Plus(m, C1), Power(n, CN1))), C1)),
                                  Power(Plus(a, Times(b, x)), p), Power(Plus(c, Times(d, x)),
                                      q),
                                  Power(Plus(e, Times(f, x)), r)),
                              x),
                          x, Power(x, n)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, n, p, q, r), x),
                      IntegerQ(Simplify(Times(Plus(m, C1), Power(n, CN1))))))),
          IIntegrate(574,
              Integrate(Times(Power(Times(g_, x_), m_DEFAULT),
                  Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), p_DEFAULT),
                  Power(Plus(c_, Times(d_DEFAULT, Power(x_, n_))), q_DEFAULT), Power(
                      Plus(e_, Times(f_DEFAULT, Power(x_, n_))), r_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(g, IntPart(m)), Power(Times(g, x),
                              FracPart(m)),
                          Power(Power(x, FracPart(m)), CN1)),
                      Integrate(Times(Power(x, m), Power(Plus(a, Times(b, Power(x, n))), p),
                          Power(Plus(c, Times(d, Power(x, n))), q),
                          Power(Plus(e, Times(f, Power(x, n))), r)), x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g, m, n, p, q,
                      r), x), IntegerQ(
                          Simplify(Times(Plus(m, C1), Power(n, CN1))))))),
          IIntegrate(575,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_,
                          n_))), p_DEFAULT),
                      Power(Plus(c_, Times(d_DEFAULT,
                          Power(x_, n_))), q_DEFAULT),
                      Power(Plus(e_, Times(f_DEFAULT, Power(x_, n_))), r_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(k,
                          GCD(Plus(m, C1), n))),
                      Condition(Dist(Power(k, CN1),
                          Subst(Integrate(
                              Times(Power(x, Subtract(Times(Plus(m, C1), Power(k, CN1)), C1)),
                                  Power(Plus(a, Times(b, Power(x, Times(n, Power(k, CN1))))), p),
                                  Power(Plus(c, Times(d, Power(x, Times(n, Power(k, CN1))))), q),
                                  Power(Plus(e, Times(f, Power(x, Times(n, Power(k, CN1))))), r)),
                              x), x, Power(x, k)),
                          x), Unequal(k, C1))),
                  And(FreeQ(List(a, b, c, d, e, f, p, q, r), x), IGtQ(n, C0), IntegerQ(m)))),
          IIntegrate(576,
              Integrate(
                  Times(Power(Times(g_DEFAULT, x_), m_),
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_,
                          n_))), p_),
                      Power(Plus(c_,
                          Times(d_DEFAULT, Power(x_, n_))), q_),
                      Power(Plus(e_, Times(f_DEFAULT, Power(x_, n_))), r_)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(k,
                          Denominator(m))),
                      Dist(
                          Times(k, Power(g,
                              CN1)),
                          Subst(
                              Integrate(
                                  Times(Power(x, Subtract(Times(k, Plus(m, C1)), C1)),
                                      Power(Plus(a,
                                          Times(b, Power(x, Times(k, n)), Power(Power(g, n), CN1))),
                                          p),
                                      Power(
                                          Plus(c,
                                              Times(d, Power(x, Times(k, n)),
                                                  Power(Power(g, n), CN1))),
                                          q),
                                      Power(
                                          Plus(e,
                                              Times(f, Power(x, Times(k, n)),
                                                  Power(Power(g, n), CN1))),
                                          r)),
                                  x),
                              x, Power(Times(g, x), Power(k, CN1))),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g, p, q, r), x), IGtQ(n, C0), FractionQ(m)))),
          IIntegrate(577,
              Integrate(
                  Times(Power(Times(g_DEFAULT, x_), m_DEFAULT),
                      Power(Plus(a_, Times(b_DEFAULT,
                          Power(x_, n_))), p_),
                      Power(Plus(c_,
                          Times(d_DEFAULT, Power(x_, n_))), q_DEFAULT),
                      Plus(e_, Times(f_DEFAULT, Power(x_, n_)))),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(Subtract(Times(b, e), Times(a, f)),
                                  Power(Times(g, x), Plus(m, C1)),
                                  Power(Plus(a, Times(b, Power(x, n))), Plus(p, C1)),
                                  Power(Plus(c,
                                      Times(d, Power(x, n))), q),
                                  Power(Times(a, b, g, n, Plus(p, C1)), CN1)),
                              x)),
                      Dist(
                          Power(Times(a, b, n,
                              Plus(p, C1)), CN1),
                          Integrate(
                              Times(Power(Times(g, x), m),
                                  Power(Plus(a, Times(b, Power(x, n))), Plus(p, C1)),
                                  Power(Plus(c, Times(d, Power(x, n))), Subtract(q,
                                      C1)),
                                  Simp(
                                      Plus(Times(c,
                                          Plus(Times(b, e, n, Plus(p, C1)),
                                              Times(Subtract(Times(b, e), Times(a, f)),
                                                  Plus(m, C1)))),
                                          Times(d,
                                              Plus(Times(b, e, n, Plus(p, C1)),
                                                  Times(Subtract(Times(b, e), Times(a, f)),
                                                      Plus(m, Times(n, q), C1))),
                                              Power(x, n))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g,
                      m), x), IGtQ(n, C0), LtQ(p, CN1), GtQ(q, C0), Not(
                          And(EqQ(q, C1),
                              SimplerQ(Subtract(Times(b, c), Times(a, d)),
                                  Subtract(Times(b, e), Times(a, f)))))))),
          IIntegrate(578,
              Integrate(
                  Times(Power(Times(g_DEFAULT, x_), m_DEFAULT),
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), p_),
                      Power(Plus(c_,
                          Times(d_DEFAULT, Power(x_, n_))), q_),
                      Plus(e_, Times(f_DEFAULT, Power(x_, n_)))),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Power(g, Subtract(n, C1)), Subtract(Times(b, e), Times(a, f)),
                              Power(Times(g, x), Plus(m, Negate(n), C1)),
                              Power(Plus(a, Times(b, Power(x,
                                  n))), Plus(p, C1)),
                              Power(Plus(c, Times(d, Power(x, n))), Plus(q, C1)),
                              Power(
                                  Times(b, n, Subtract(Times(b, c), Times(a, d)),
                                      Plus(p, C1)),
                                  CN1)),
                          x),
                      Dist(
                          Times(Power(g, n),
                              Power(
                                  Times(b, n, Subtract(Times(b, c), Times(a,
                                      d)), Plus(p,
                                          C1)),
                                  CN1)),
                          Integrate(Times(Power(Times(g, x), Subtract(m, n)),
                              Power(Plus(a, Times(b, Power(x, n))), Plus(p, C1)),
                              Power(Plus(c, Times(d, Power(x, n))), q),
                              Simp(Plus(
                                  Times(c, Subtract(Times(b, e), Times(a, f)),
                                      Plus(m, Negate(n), C1)),
                                  Times(Subtract(
                                      Times(d, Subtract(Times(b, e), Times(a, f)),
                                          Plus(m, Times(n, q), C1)),
                                      Times(b, n, Subtract(Times(c, f), Times(d, e)), Plus(p, C1))),
                                      Power(x, n))),
                                  x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g,
                      q), x), IGtQ(n,
                          C0),
                      LtQ(p, CN1), GtQ(Plus(m, Negate(n), C1), C0)))),
          IIntegrate(579,
              Integrate(
                  Times(Power(Times(g_DEFAULT, x_), m_DEFAULT),
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), p_),
                      Power(Plus(c_, Times(d_DEFAULT, Power(x_, n_))), q_),
                      Plus(e_, Times(f_DEFAULT, Power(x_, n_)))),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(Subtract(Times(b, e), Times(a, f)),
                                  Power(Times(g, x), Plus(m, C1)),
                                  Power(Plus(a, Times(b, Power(x, n))), Plus(p, C1)),
                                  Power(Plus(c, Times(d, Power(x, n))), Plus(q,
                                      C1)),
                                  Power(Times(
                                      a, g, n, Subtract(Times(b, c), Times(a, d)), Plus(p, C1)),
                                      CN1)),
                              x)),
                      Dist(Power(Times(a, n, Subtract(Times(b, c), Times(a, d)), Plus(p, C1)), CN1),
                          Integrate(
                              Times(Power(Times(g, x), m),
                                  Power(Plus(a, Times(b, Power(x, n))), Plus(p, C1)),
                                  Power(Plus(c, Times(d, Power(x, n))), q),
                                  Simp(Plus(
                                      Times(c, Subtract(Times(b, e), Times(a, f)), Plus(m, C1)),
                                      Times(e, n, Subtract(Times(b, c), Times(a, d)), Plus(p, C1)),
                                      Times(d, Subtract(Times(b, e), Times(a, f)),
                                          Plus(m, Times(n, Plus(p, q, C2)), C1), Power(x, n))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g, m, q), x), IGtQ(n, C0), LtQ(p, CN1)))),
          IIntegrate(580,
              Integrate(
                  Times(Power(Times(g_DEFAULT, x_), m_),
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), p_DEFAULT),
                      Power(Plus(c_, Times(d_DEFAULT, Power(x_, n_))), q_DEFAULT),
                      Plus(e_, Times(f_DEFAULT, Power(x_, n_)))),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(e, Power(Times(g, x), Plus(m, C1)),
                              Power(Plus(a, Times(b, Power(x, n))), Plus(p, C1)),
                              Power(Plus(c,
                                  Times(d, Power(x, n))), q),
                              Power(Times(a, g, Plus(m, C1)), CN1)),
                          x),
                      Dist(Power(Times(a, Power(g, n), Plus(m, C1)), CN1),
                          Integrate(Times(Power(Times(g, x), Plus(m, n)),
                              Power(Plus(a, Times(b, Power(x, n))), p),
                              Power(Plus(c, Times(d, Power(x, n))), Subtract(q, C1)),
                              Simp(Plus(Times(c, Subtract(Times(b, e), Times(a, f)), Plus(m, C1)),
                                  Times(e, n, Plus(Times(b, c, Plus(p, C1)), Times(a, d, q))),
                                  Times(d,
                                      Plus(Times(Subtract(Times(b, e), Times(a, f)), Plus(m, C1)),
                                          Times(b, e, n, Plus(p, q, C1))),
                                      Power(x, n))),
                                  x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g, p), x), IGtQ(n, C0), GtQ(q, C0), LtQ(m, CN1),
                      Not(And(EqQ(q, C1), SimplerQ(Plus(e, Times(f, Power(x, n))),
                          Plus(c, Times(d, Power(x, n))))))))));
}
