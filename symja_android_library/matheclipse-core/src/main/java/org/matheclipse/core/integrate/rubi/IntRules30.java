package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$p;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C3;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN1D2;
import static org.matheclipse.core.expression.F.CN2;
import static org.matheclipse.core.expression.F.CN4;
import static org.matheclipse.core.expression.F.Coefficient;
import static org.matheclipse.core.expression.F.Condition;
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
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.QQ;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Simplify;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Sqrt;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_;
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
import static org.matheclipse.core.expression.F.r_DEFAULT;
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
import static org.matheclipse.core.expression.S.g;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.q;
import static org.matheclipse.core.expression.S.r;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.v;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandIntegrand;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FracPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LinearPairQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PerfectSquareQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PosQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RationalQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RemoveContent;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Rt;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Subst;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Unintegrable;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules30 {
  public static IAST RULES =
      List(
          IIntegrate(601,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(Plus(c_, Times(d_DEFAULT, Power(x_, $p("mn", true)))), q_),
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT,
                          Power(x_, n_DEFAULT))), p_DEFAULT),
                      Power(Plus(e_, Times(f_DEFAULT, Power(x_, n_DEFAULT))), r_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(x, Times(n, FracPart(q))),
                          Power(Plus(c, Times(d,
                              Power(Power(x, n), CN1))), FracPart(
                                  q)),
                          Power(Power(Plus(d, Times(c, Power(x, n))), FracPart(q)), CN1)),
                      Integrate(
                          Times(Power(x, Subtract(m, Times(n, q))),
                              Power(Plus(a, Times(b, Power(x, n))), p),
                              Power(Plus(d, Times(c, Power(x, n))),
                                  q),
                              Power(Plus(e, Times(f, Power(x, n))), r)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, n, p, q,
                      r), x), EqQ($s("mn"),
                          Negate(n)),
                      Not(IntegerQ(q))))),
          IIntegrate(602,
              Integrate(
                  Times(Power(Times(g_, x_), m_),
                      Power(Plus(c_, Times(d_DEFAULT, Power(x_, $p("mn", true)))), q_DEFAULT),
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_DEFAULT))), p_DEFAULT),
                      Power(Plus(e_, Times(f_DEFAULT, Power(x_, n_DEFAULT))), r_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(g, IntPart(m)), Power(Times(g,
                              x), FracPart(m)),
                          Power(Power(x, FracPart(m)), CN1)),
                      Integrate(
                          Times(Power(x, m), Power(Plus(a, Times(b, Power(x, n))), p),
                              Power(Plus(c, Times(d, Power(Power(x, n), CN1))), q),
                              Power(Plus(e, Times(f, Power(x, n))), r)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g, m, n, p, q,
                      r), x), EqQ($s("mn"),
                          Negate(n))))),
          IIntegrate(603,
              Integrate(
                  Times(Power(Times(g_DEFAULT, x_), m_DEFAULT),
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), p_DEFAULT),
                      Power(Plus(c_, Times(d_DEFAULT, Power(x_, n_))), q_DEFAULT), Power(Plus(e_,
                          Times(f_DEFAULT, Power(x_, n_))), r_DEFAULT)),
                  x_Symbol),
              Condition(
                  Unintegrable(
                      Times(Power(Times(g, x), m), Power(Plus(a, Times(b, Power(x, n))), p),
                          Power(Plus(c, Times(d, Power(x, n))),
                              q),
                          Power(Plus(e, Times(f, Power(x, n))), r)),
                      x),
                  FreeQ(List(a, b, c, d, e, f, g, m, n, p, q, r), x))),
          IIntegrate(604,
              Integrate(Times(Power(u_, m_DEFAULT),
                  Power(Plus(a_DEFAULT, Times(b_DEFAULT, Power(v_, n_))), p_DEFAULT),
                  Power(Plus(c_DEFAULT, Times(d_DEFAULT,
                      Power(v_, n_))), q_DEFAULT),
                  Power(Plus(e_, Times(f_DEFAULT, Power(v_, n_))), r_DEFAULT)), x_Symbol),
              Condition(Dist(
                  Times(Power(u, m), Power(Times(Coefficient(v, x, C1), Power(v, m)), CN1)),
                  Subst(Integrate(Times(Power(x, m), Power(Plus(a, Times(b, Power(x, n))), p),
                      Power(Plus(c, Times(d, Power(x, n))),
                          q),
                      Power(Plus(e, Times(f, Power(x, n))), r)), x), x,
                      v),
                  x), And(FreeQ(List(a, b, c, d, e, f, m, n, p, q, r), x), LinearPairQ(u, v, x)))),
          IIntegrate(605,
              Integrate(
                  Times(Power(Times(g_DEFAULT, x_), m_DEFAULT),
                      Power(Plus($p("e1"), Times($p("f1", true), Power(x_, $p("n2", true)))),
                          r_DEFAULT),
                      Power(Plus($p("e2"),
                          Times($p("f2", true), Power(x_, $p("n2", true)))), r_DEFAULT),
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), p_DEFAULT), Power(Plus(c_,
                          Times(d_DEFAULT, Power(x_, n_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(Power(Times(g, x), m), Power(Plus(a, Times(b, Power(x, n))), p),
                          Power(Plus(c, Times(d, Power(x, n))), q),
                          Power(
                              Plus(Times($s("e1"), $s("e2")),
                                  Times($s("f1"), $s("f2"), Power(x, n))),
                              r)),
                      x),
                  And(FreeQ(
                      List(a, b, c, d, $s("e1"), $s("f1"), $s("e2"), $s("f2"), g, m, n, p, q, r),
                      x), EqQ($s("n2"), Times(C1D2, n)),
                      EqQ(Plus(Times($s("e2"), $s("f1")), Times($s("e1"), $s("f2"))), C0), Or(
                          IntegerQ(r), And(GtQ($s("e1"), C0), GtQ($s("e2"), C0)))))),
          IIntegrate(606,
              Integrate(
                  Times(
                      Power(Times(g_DEFAULT,
                          x_), m_DEFAULT),
                      Power(
                          Plus($p("e1"),
                              Times($p("f1", true), Power(x_, $p("n2", true)))),
                          r_DEFAULT),
                      Power(Plus($p("e2"), Times($p("f2", true), Power(x_, $p("n2", true)))),
                          r_DEFAULT),
                      Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_))), p_DEFAULT),
                      Power(Plus(c_, Times(d_DEFAULT, Power(x_, n_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(Plus($s("e1"), Times($s("f1"), Power(x, Times(C1D2, n)))),
                              FracPart(r)),
                          Power(
                              Plus($s("e2"), Times($s("f2"), Power(x, Times(C1D2, n)))),
                              FracPart(r)),
                          Power(Power(Plus(Times($s("e1"), $s("e2")),
                              Times($s("f1"), $s("f2"), Power(x, n))), FracPart(r)), CN1)),
                      Integrate(
                          Times(Power(Times(g, x), m), Power(Plus(a, Times(b, Power(x, n))), p),
                              Power(Plus(c, Times(d, Power(x, n))), q),
                              Power(Plus(Times($s("e1"), $s("e2")),
                                  Times($s("f1"), $s("f2"), Power(x, n))), r)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, $s("e1"), $s("f1"), $s("e2"), $s("f2"), g, m, n, p, q,
                      r), x), EqQ($s("n2"), Times(C1D2, n)),
                      EqQ(Plus(Times($s("e2"), $s("f1")), Times($s("e1"), $s("f2"))), C0)))),
          IIntegrate(607,
              Integrate(Power(Plus(a_, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))),
                  p_), x_Symbol),
              Condition(
                  Simp(Times(C2, Power(Plus(a, Times(b, x), Times(c, Sqr(x))), Plus(p, C1)),
                      Power(Times(Plus(Times(C2, p), C1), Plus(b, Times(C2, c, x))), CN1)), x),
                  And(FreeQ(List(a, b, c, p), x), EqQ(Subtract(Sqr(b),
                      Times(C4, a, c)), C0), LtQ(p,
                          CN1)))),
          IIntegrate(608,
              Integrate(
                  Power(Plus(a_, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))),
                      CN1D2),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Plus(Times(C1D2, b), Times(c, x)), Power(
                          Plus(a, Times(b, x), Times(c, Sqr(x))), CN1D2)),
                      Integrate(Power(Plus(Times(C1D2, b), Times(c, x)), CN1), x), x),
                  And(FreeQ(List(a, b, c), x), EqQ(Subtract(Sqr(b), Times(C4, a, c)), C0)))),
          IIntegrate(609,
              Integrate(Power(Plus(a_, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))),
                  p_), x_Symbol),
              Condition(
                  Simp(
                      Times(Plus(b, Times(C2, c, x)),
                          Power(Plus(a, Times(b, x),
                              Times(c, Sqr(x))), p),
                          Power(Times(C2, c, Plus(Times(C2, p), C1)), CN1)),
                      x),
                  And(FreeQ(List(a, b, c, p), x), EqQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      NeQ(p, Negate(C1D2))))),
          IIntegrate(610,
              Integrate(Power(Plus(a_, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), p_),
                  x_Symbol),
              Condition(
                  With(List(Set(q, Rt(Subtract(Sqr(b), Times(C4, a, c)), C2))),
                      Dist(Power(Power(c, p), CN1), Integrate(
                          Times(
                              Power(Simp(Plus(Times(C1D2, b), Times(CN1, C1D2, q), Times(c, x)), x),
                                  p),
                              Power(Simp(Plus(Times(C1D2, b), Times(C1D2, q), Times(c, x)), x), p)),
                          x), x)),
                  And(FreeQ(List(a, b, c), x), NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      IGtQ(p, C0), PerfectSquareQ(Subtract(Sqr(b), Times(C4, a, c)))))),
          IIntegrate(611,
              Integrate(
                  Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))),
                      p_),
                  x_Symbol),
              Condition(
                  Integrate(ExpandIntegrand(Power(Plus(a, Times(b, x), Times(c, Sqr(x))), p),
                      x), x),
                  And(FreeQ(List(a, b, c), x), NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      IGtQ(p, C0), Or(EqQ(a, C0),
                          Not(PerfectSquareQ(Subtract(Sqr(b), Times(C4, a, c)))))))),
          IIntegrate(612,
              Integrate(
                  Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))),
                      p_),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Plus(b, Times(C2, c, x)),
                              Power(Plus(a, Times(b, x), Times(c, Sqr(x))),
                                  p),
                              Power(Times(C2, c, Plus(Times(C2, p), C1)), CN1)),
                          x),
                      Dist(
                          Times(
                              p, Subtract(Sqr(b), Times(C4, a,
                                  c)),
                              Power(Times(C2, c, Plus(Times(C2, p), C1)), CN1)),
                          Integrate(Power(Plus(a, Times(b, x), Times(c, Sqr(x))), Subtract(p, C1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c), x), NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      GtQ(p, C0), IntegerQ(Times(C4, p))))),
          IIntegrate(613,
              Integrate(
                  Power(
                      Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT,
                          Sqr(x_))),
                      QQ(-3L, 2L)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(CN2, Plus(b, Times(C2, c, x)),
                          Power(Times(Subtract(Sqr(b), Times(C4, a, c)),
                              Sqrt(Plus(a, Times(b, x), Times(c, Sqr(x))))), CN1)),
                      x),
                  And(FreeQ(List(a, b, c), x), NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0)))),
          IIntegrate(614,
              Integrate(
                  Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))),
                      p_),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Plus(b, Times(C2, c, x)),
                              Power(Plus(a, Times(b, x), Times(c, Sqr(x))), Plus(p, C1)),
                              Power(Times(Plus(p, C1), Subtract(Sqr(b), Times(C4, a, c))), CN1)),
                          x),
                      Dist(
                          Times(C2, c, Plus(Times(C2, p), C3),
                              Power(Times(Plus(p, C1), Subtract(Sqr(b), Times(C4, a, c))), CN1)),
                          Integrate(Power(Plus(a, Times(b, x), Times(c, Sqr(x))), Plus(p, C1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c), x), NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      LtQ(p, CN1), NeQ(p, QQ(-3L, 2L)), IntegerQ(Times(C4, p))))),
          IIntegrate(615,
              Integrate(Power(Plus(Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))),
                  CN1), x_Symbol),
              Condition(
                  Subtract(
                      Simp(Times(Log(x),
                          Power(b, CN1)), x),
                      Simp(Times(Log(RemoveContent(Plus(b, Times(c, x)), x)), Power(b, CN1)), x)),
                  FreeQ(List(b, c), x))),
          IIntegrate(616,
              Integrate(
                  Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))),
                      CN1),
                  x_Symbol),
              Condition(With(List(Set(q, Rt(Subtract(Sqr(b), Times(C4, a, c)), C2))),
                  Subtract(
                      Dist(Times(c, Power(q, CN1)),
                          Integrate(
                              Power(Simp(Plus(Times(C1D2, b), Times(CN1, C1D2, q), Times(c, x)), x),
                                  CN1),
                              x),
                          x),
                      Dist(Times(c, Power(q, CN1)),
                          Integrate(
                              Power(Simp(Plus(Times(C1D2, b), Times(C1D2, q), Times(c, x)), x),
                                  CN1),
                              x),
                          x))),
                  And(FreeQ(List(a, b, c), x), NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      PosQ(Subtract(Sqr(b), Times(C4, a, c))), PerfectSquareQ(
                          Subtract(Sqr(b), Times(C4, a, c)))))),
          IIntegrate(617,
              Integrate(Power(Plus(a_, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), CN1),
                  x_Symbol),
              Condition(
                  With(List(Set(q, Subtract(C1, Times(C4, Simplify(Times(a, c, Power(b, CN2))))))),
                      Condition(
                          Dist(Times(CN2, Power(b, CN1)),
                              Subst(Integrate(Power(Subtract(q, Sqr(x)), CN1), x), x,
                                  Plus(C1, Times(C2, c, x, Power(b, CN1)))),
                              x),
                          And(RationalQ(q),
                              Or(EqQ(Sqr(q), C1),
                                  Not(RationalQ(Subtract(Sqr(b), Times(C4, a, c)))))))),
                  And(FreeQ(List(a, b, c), x), NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0)))),
          IIntegrate(618,
              Integrate(
                  Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), CN1),
                  x_Symbol),
              Condition(
                  Dist(CN2,
                      Subst(Integrate(
                          Power(Simp(Subtract(Subtract(Sqr(b), Times(C4, a, c)), Sqr(x)), x), CN1),
                          x), x, Plus(b, Times(C2, c, x))),
                      x),
                  And(FreeQ(List(a, b, c), x), NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0)))),
          IIntegrate(619,
              Integrate(
                  Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))),
                      p_),
                  x_Symbol),
              Condition(
                  Dist(
                      Power(
                          Times(C2, c,
                              Power(Times(CN4, c, Power(Subtract(Sqr(b), Times(C4, a, c)), CN1)),
                                  p)),
                          CN1),
                      Subst(
                          Integrate(
                              Power(
                                  Simp(
                                      Subtract(C1,
                                          Times(Sqr(x),
                                              Power(Subtract(Sqr(b), Times(C4, a, c)), CN1))),
                                      x),
                                  p),
                              x),
                          x, Plus(b, Times(C2, c, x))),
                      x),
                  And(FreeQ(List(a, b, c, p), x),
                      GtQ(Subtract(Times(C4, a), Times(Sqr(b), Power(c, CN1))), C0)))),
          IIntegrate(620,
              Integrate(Power(Plus(Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), CN1D2),
                  x_Symbol),
              Condition(
                  Dist(C2,
                      Subst(Integrate(Power(Subtract(C1, Times(c, Sqr(x))), CN1), x), x,
                          Times(x, Power(Plus(Times(b, x), Times(c, Sqr(x))), CN1D2))),
                      x),
                  FreeQ(List(b, c), x))));
}
