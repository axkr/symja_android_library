package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$p;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.ArcCosh;
import static org.matheclipse.core.expression.F.ArcSinh;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN1D2;
import static org.matheclipse.core.expression.F.CN2;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Cosh;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Sinh;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Sqrt;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_DEFAULT;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.d;
import static org.matheclipse.core.expression.S.e;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandTrigReduce;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntHide;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SimplifyIntegrand;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Subst;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Unintegrable;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules283 {
  public static IAST RULES =
      List(
          IIntegrate(5661,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(ArcSinh(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          n_DEFAULT),
                      Power(Times(d_DEFAULT, x_), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Power(Times(d, x), Plus(m, C1)),
                              Power(Plus(a, Times(b, ArcSinh(Times(c, x)))),
                                  n),
                              Power(Times(d, Plus(m, C1)), CN1)),
                          x),
                      Dist(Times(b, c, n, Power(Times(d, Plus(m, C1)), CN1)),
                          Integrate(
                              Times(Power(Times(d, x), Plus(m, C1)),
                                  Power(Plus(a, Times(b, ArcSinh(Times(c, x)))), Subtract(n,
                                      C1)),
                                  Power(Plus(C1, Times(Sqr(c), Sqr(x))), CN1D2)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, m), x), IGtQ(n, C0), NeQ(m, CN1)))),
          IIntegrate(5662,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(ArcCosh(Times(c_DEFAULT, x_)), b_DEFAULT)), n_DEFAULT),
                      Power(Times(d_DEFAULT, x_), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Power(Times(d, x), Plus(m, C1)),
                              Power(Plus(a, Times(b, ArcCosh(Times(c, x)))),
                                  n),
                              Power(Times(d, Plus(m, C1)), CN1)),
                          x),
                      Dist(Times(b, c, n, Power(Times(d, Plus(m, C1)), CN1)),
                          Integrate(
                              Times(Power(Times(d, x), Plus(m, C1)),
                                  Power(Plus(a, Times(b, ArcCosh(Times(c, x)))), Subtract(n, C1)),
                                  Power(Times(Sqrt(Plus(CN1, Times(c, x))),
                                      Sqrt(Plus(C1, Times(c, x)))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, m), x), IGtQ(n, C0), NeQ(m, CN1)))),
          IIntegrate(5663,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(ArcSinh(Times(c_DEFAULT, x_)), b_DEFAULT)), n_),
                      Power(x_, m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Power(x, Plus(m, C1)),
                              Power(Plus(a,
                                  Times(b, ArcSinh(Times(c, x)))), n),
                              Power(Plus(m, C1), CN1)),
                          x),
                      Dist(Times(b, c, n, Power(Plus(m, C1), CN1)),
                          Integrate(Times(Power(x, Plus(m, C1)),
                              Power(Plus(a, Times(b, ArcSinh(Times(c, x)))), Subtract(n, C1)),
                              Power(Plus(C1, Times(Sqr(c), Sqr(x))), CN1D2)), x),
                          x)),
                  And(FreeQ(List(a, b, c), x), IGtQ(m, C0), GtQ(n, C0)))),
          IIntegrate(5664,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(ArcCosh(Times(c_DEFAULT, x_)), b_DEFAULT)), n_),
                      Power(x_, m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Power(x, Plus(m, C1)),
                              Power(Plus(a,
                                  Times(b, ArcCosh(Times(c, x)))), n),
                              Power(Plus(m, C1), CN1)),
                          x),
                      Dist(
                          Times(b, c, n, Power(Plus(m, C1),
                              CN1)),
                          Integrate(
                              Times(Power(x, Plus(m, C1)),
                                  Power(Plus(a, Times(b, ArcCosh(Times(c, x)))), Subtract(n, C1)),
                                  Power(Times(Sqrt(Plus(CN1, Times(c, x))),
                                      Sqrt(Plus(C1, Times(c, x)))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c), x), IGtQ(m, C0), GtQ(n, C0)))),
          IIntegrate(5665,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(ArcSinh(Times(c_DEFAULT, x_)), b_DEFAULT)), n_),
                      Power(x_, m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Power(x, m), Sqrt(Plus(C1, Times(Sqr(c), Sqr(x)))),
                              Power(Plus(a, Times(b,
                                  ArcSinh(Times(c, x)))), Plus(n,
                                      C1)),
                              Power(Times(b, c, Plus(n, C1)), CN1)),
                          x),
                      Dist(
                          Power(Times(b, Power(c, Plus(m, C1)),
                              Plus(n, C1)), CN1),
                          Subst(Integrate(ExpandTrigReduce(Power(Plus(a, Times(b, x)), Plus(n, C1)),
                              Times(Power(Sinh(x), Subtract(m, C1)),
                                  Plus(m, Times(Plus(m, C1), Sqr(Sinh(x))))),
                              x), x), x, ArcSinh(
                                  Times(c, x))),
                          x)),
                  And(FreeQ(List(a, b, c), x), IGtQ(m, C0), GeQ(n, CN2), LtQ(n, CN1)))),
          IIntegrate(5666,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(ArcCosh(Times(c_DEFAULT, x_)), b_DEFAULT)), n_),
                      Power(x_, m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(x, m), Sqrt(Plus(CN1, Times(c, x))),
                              Sqrt(Plus(C1, Times(c, x))),
                              Power(Plus(a, Times(b, ArcCosh(Times(c, x)))),
                                  Plus(n, C1)),
                              Power(Times(b, c, Plus(n, C1)), CN1)),
                          x),
                      Dist(
                          Power(Times(b, Power(c, Plus(m, C1)), Plus(n, C1)), CN1), Subst(
                              Integrate(
                                  ExpandTrigReduce(Times(Power(Plus(a, Times(b, x)), Plus(n, C1)),
                                      Power(Cosh(x), Subtract(m, C1)),
                                      Subtract(m, Times(Plus(m, C1), Sqr(Cosh(x))))), x),
                                  x),
                              x, ArcCosh(Times(c, x))),
                          x)),
                  And(FreeQ(List(a, b, c), x), IGtQ(m, C0), GeQ(n, CN2), LtQ(n, CN1)))),
          IIntegrate(5667,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(ArcSinh(Times(c_DEFAULT, x_)), b_DEFAULT)), n_),
                      Power(x_, m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(x, m), Sqrt(Plus(C1, Times(Sqr(c), Sqr(x)))),
                              Power(Plus(a, Times(b, ArcSinh(Times(c, x)))), Plus(n, C1)),
                              Power(Times(b, c, Plus(n, C1)), CN1)),
                          x),
                      Negate(
                          Dist(
                              Times(c, Plus(m, C1), Power(Times(b, Plus(n, C1)),
                                  CN1)),
                              Integrate(
                                  Times(Power(x, Plus(m, C1)),
                                      Power(Plus(a, Times(b, ArcSinh(Times(c, x)))), Plus(n,
                                          C1)),
                                      Power(Plus(C1, Times(Sqr(c), Sqr(x))), CN1D2)),
                                  x),
                              x)),
                      Negate(
                          Dist(Times(m, Power(Times(b, c, Plus(n, C1)), CN1)),
                              Integrate(
                                  Times(Power(x, Subtract(m, C1)),
                                      Power(Plus(a, Times(b, ArcSinh(Times(c, x)))), Plus(n,
                                          C1)),
                                      Power(Plus(C1, Times(Sqr(c), Sqr(x))), CN1D2)),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c), x), IGtQ(m, C0), LtQ(n, CN2)))),
          IIntegrate(5668,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(ArcCosh(Times(c_DEFAULT, x_)), b_DEFAULT)), n_),
                      Power(x_, m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(x, m), Sqrt(Plus(CN1, Times(c, x))),
                              Sqrt(Plus(C1, Times(c, x))),
                              Power(Plus(a, Times(b,
                                  ArcCosh(Times(c, x)))), Plus(n,
                                      C1)),
                              Power(Times(b, c, Plus(n, C1)), CN1)),
                          x),
                      Negate(
                          Dist(
                              Times(c, Plus(m, C1), Power(Times(b, Plus(n, C1)),
                                  CN1)),
                              Integrate(Times(Power(x, Plus(m, C1)),
                                  Power(Plus(a, Times(b, ArcCosh(Times(c, x)))), Plus(n, C1)),
                                  Power(Times(Sqrt(Plus(CN1, Times(c, x))),
                                      Sqrt(Plus(C1, Times(c, x)))), CN1)),
                                  x),
                              x)),
                      Dist(
                          Times(m, Power(Times(b, c, Plus(n, C1)),
                              CN1)),
                          Integrate(
                              Times(Power(x, Subtract(m, C1)),
                                  Power(Plus(a, Times(b, ArcCosh(Times(c, x)))), Plus(n,
                                      C1)),
                                  Power(Times(Sqrt(Plus(CN1, Times(c, x))),
                                      Sqrt(Plus(C1, Times(c, x)))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c), x), IGtQ(m, C0), LtQ(n, CN2)))),
          IIntegrate(5669,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(ArcSinh(Times(c_DEFAULT, x_)), b_DEFAULT)), n_),
                      Power(x_, m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(Power(c, Plus(m, C1)), CN1),
                      Subst(
                          Integrate(Times(Power(Plus(a, Times(b, x)), n), Power(Sinh(x), m),
                              Cosh(x)), x),
                          x, ArcSinh(Times(c, x))),
                      x),
                  And(FreeQ(List(a, b, c, n), x), IGtQ(m, C0)))),
          IIntegrate(5670,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(ArcCosh(Times(c_DEFAULT, x_)), b_DEFAULT)), n_),
                      Power(x_, m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Power(Power(c,
                          Plus(m, C1)), CN1),
                      Subst(
                          Integrate(
                              Times(Power(Plus(a, Times(b, x)), n), Power(Cosh(x), m), Sinh(x)), x),
                          x, ArcCosh(Times(c, x))),
                      x),
                  And(FreeQ(List(a, b, c, n), x), IGtQ(m, C0)))),
          IIntegrate(5671,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(ArcSinh(Times(c_DEFAULT, x_)), b_DEFAULT)), n_DEFAULT),
                      Power(Times(d_DEFAULT, x_), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Unintegrable(Times(Power(Times(d, x), m),
                      Power(Plus(a, Times(b, ArcSinh(Times(c, x)))), n)), x),
                  FreeQ(List(a, b, c, d, m, n), x))),
          IIntegrate(5672,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(ArcCosh(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          n_DEFAULT),
                      Power(Times(d_DEFAULT, x_), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Unintegrable(
                      Times(Power(Times(d,
                          x), m), Power(Plus(a, Times(b, ArcCosh(Times(c, x)))),
                              n)),
                      x),
                  FreeQ(List(a, b, c, d, m, n), x))),
          IIntegrate(5673,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(ArcSinh(Times(c_DEFAULT, x_)), b_DEFAULT)), CN1),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Simp(Times(Log(Plus(a, Times(b, ArcSinh(Times(c, x))))),
                      Power(Times(b, c, Sqrt(d)), CN1)), x),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(e, Times(Sqr(c), d)), GtQ(d, C0)))),
          IIntegrate(5674,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT, Times(ArcCosh(Times(c_DEFAULT, x_)), b_DEFAULT)), CN1),
                      Power(Plus($p("d1"), Times($p("e1", true),
                          x_)), CN1D2),
                      Power(Plus($p("d2"), Times($p("e2", true), x_)), CN1D2)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(
                          Log(Plus(a,
                              Times(b, ArcCosh(Times(c, x))))),
                          Power(Times(b, c, Sqrt(Times(CN1, $s("d1"), $s("d2")))), CN1)),
                      x),
                  And(FreeQ(List(a, b, c, $s("d1"), $s("e1"), $s("d2"), $s("e2")), x),
                      EqQ($s("e1"), Times(c, $s("d1"))), EqQ($s("e2"), Times(CN1, c,
                          $s("d2"))),
                      GtQ($s("d1"), C0), LtQ($s("d2"), C0)))),
          IIntegrate(5675,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcSinh(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          n_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(
                          Power(Plus(a, Times(b, ArcSinh(Times(c, x)))),
                              Plus(n, C1)),
                          Power(Times(b, c, Sqrt(d), Plus(n, C1)), CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, n), x), EqQ(e, Times(Sqr(c),
                      d)), GtQ(d,
                          C0),
                      NeQ(n, CN1)))),
          IIntegrate(5676,
              Integrate(Times(
                  Power(Plus(a_DEFAULT, Times(ArcCosh(Times(c_DEFAULT, x_)), b_DEFAULT)),
                      n_DEFAULT),
                  Power(Plus($p("d1"), Times($p("e1", true), x_)), CN1D2),
                  Power(Plus($p("d2"), Times($p("e2", true), x_)), CN1D2)), x_Symbol),
              Condition(
                  Simp(Times(
                      Power(Plus(a, Times(b, ArcCosh(Times(c, x)))), Plus(n, C1)), Power(Times(b, c,
                          Sqrt(Times(CN1, $s("d1"), $s("d2"))), Plus(n, C1)), CN1)),
                      x),
                  And(FreeQ(List(a, b, c, $s("d1"), $s("e1"), $s("d2"), $s("e2"), n), x),
                      EqQ($s("e1"), Times(c, $s("d1"))), EqQ($s("e2"), Times(CN1, c,
                          $s("d2"))),
                      GtQ($s("d1"), C0), LtQ($s("d2"), C0), NeQ(n, CN1)))),
          IIntegrate(5677,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcSinh(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          n_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Sqrt(Plus(C1,
                              Times(Sqr(c), Sqr(x)))),
                          Power(Plus(d, Times(e, Sqr(x))), CN1D2)),
                      Integrate(
                          Times(Power(Plus(a, Times(b, ArcSinh(Times(c, x)))), n),
                              Power(Plus(C1, Times(Sqr(c), Sqr(x))), CN1D2)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e,
                      n), x), EqQ(e,
                          Times(Sqr(c), d)),
                      Not(GtQ(d, C0))))),
          IIntegrate(5678,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCosh(
                              Times(c_DEFAULT, x_)), b_DEFAULT)),
                          n_DEFAULT),
                      Power(Plus($p("d1"), Times($p("e1", true),
                          x_)), CN1D2),
                      Power(Plus($p("d2"), Times($p("e2", true), x_)), CN1D2)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Sqrt(Plus(C1, Times(c, x))), Sqrt(Plus(CN1, Times(c, x))),
                          Power(
                              Times(Sqrt(Plus($s("d1"), Times($s("e1"), x))),
                                  Sqrt(Plus($s("d2"), Times($s("e2"), x)))),
                              CN1)),
                      Integrate(
                          Times(
                              Power(Plus(a,
                                  Times(b, ArcCosh(Times(c, x)))), n),
                              Power(
                                  Times(Sqrt(Plus(C1, Times(c, x))),
                                      Sqrt(Plus(CN1, Times(c, x)))),
                                  CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, $s("d1"), $s("e1"), $s("d2"), $s("e2"), n), x),
                      EqQ($s("e1"), Times(c, $s("d1"))), EqQ($s("e2"), Times(CN1, c,
                          $s("d2"))),
                      Not(And(GtQ($s("d1"), C0), LtQ($s("d2"), C0)))))),
          IIntegrate(5679,
              Integrate(Times(Plus(a_DEFAULT, Times(ArcSinh(Times(c_DEFAULT, x_)), b_DEFAULT)),
                  Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_DEFAULT)), x_Symbol),
              Condition(
                  With(List(Set(u, IntHide(Power(Plus(d, Times(e, Sqr(x))), p), x))),
                      Subtract(Dist(Plus(a, Times(b, ArcSinh(Times(c, x)))), u, x),
                          Dist(Times(b, c),
                              Integrate(
                                  SimplifyIntegrand(
                                      Times(u, Power(Plus(C1, Times(Sqr(c), Sqr(x))), CN1D2)), x),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(e, Times(Sqr(c), d)), IGtQ(p, C0)))),
          IIntegrate(5680,
              Integrate(
                  Times(
                      Plus(a_DEFAULT, Times(ArcCosh(Times(c_DEFAULT, x_)),
                          b_DEFAULT)),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(List(Set(u, IntHide(Power(Plus(d, Times(e, Sqr(x))), p), x))), Subtract(
                      Dist(Plus(a, Times(b, ArcCosh(Times(c, x)))), u, x),
                      Dist(Times(b, c), Integrate(SimplifyIntegrand(Times(u,
                          Power(Times(Sqrt(Plus(C1, Times(c, x))), Sqrt(Plus(CN1, Times(c, x)))),
                              CN1)),
                          x), x), x))),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(Plus(Times(Sqr(c), d), e), C0),
                      IGtQ(p, C0)))));
}
