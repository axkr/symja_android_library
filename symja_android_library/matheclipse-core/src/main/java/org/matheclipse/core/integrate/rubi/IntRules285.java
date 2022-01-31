package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$p;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.ArcCosh;
import static org.matheclipse.core.expression.F.ArcSinh;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN1D2;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Cosh;
import static org.matheclipse.core.expression.F.Coth;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.Greater;
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
import static org.matheclipse.core.expression.F.Sinh;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Sqrt;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Tanh;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.f_;
import static org.matheclipse.core.expression.F.g_DEFAULT;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_;
import static org.matheclipse.core.expression.F.p_DEFAULT;
import static org.matheclipse.core.expression.F.q_;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.d;
import static org.matheclipse.core.expression.S.e;
import static org.matheclipse.core.expression.S.f;
import static org.matheclipse.core.expression.S.g;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.q;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandIntegrand;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FracPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.HalfIntegerQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ILtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntHide;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntPart;
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
class IntRules285 {
  public static IAST RULES =
      List(
          IIntegrate(5701,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(ArcCosh(Times(c_DEFAULT, x_)), b_DEFAULT)), n_DEFAULT),
                      Power(Plus($p("d1"), Times($p("e1", true), x_)), p_DEFAULT), Power(Plus(
                          $p("d2"), Times($p("e2", true), x_)), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Times(CN1, $s("d1"), $s("d2")), p), Power(c,
                          CN1)),
                      Subst(
                          Integrate(
                              Times(
                                  Power(Plus(a, Times(b, x)), n), Power(Sinh(x),
                                      Plus(Times(C2, p), C1))),
                              x),
                          x, ArcCosh(Times(c, x))),
                      x),
                  And(FreeQ(List(a, b, c, $s("d1"), $s("e1"), $s("d2"), $s("e2"), n), x),
                      EqQ($s("e1"), Times(c, $s("d1"))), EqQ($s("e2"), Times(CN1, c,
                          $s("d2"))),
                      IGtQ(Plus(p, C1D2), C0), And(GtQ($s("d1"), C0), LtQ($s("d2"), C0))))),
          IIntegrate(5702,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcSinh(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          n_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(d, Subtract(p, C1D2)), Sqrt(Plus(d,
                              Times(e, Sqr(x)))),
                          Power(Plus(C1, Times(Sqr(c), Sqr(x))), CN1D2)),
                      Integrate(
                          Times(Power(Plus(C1, Times(Sqr(c), Sqr(x))), p),
                              Power(Plus(a, Times(b, ArcSinh(Times(c, x)))), n)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, n), x), EqQ(e, Times(Sqr(
                      c), d)), IGtQ(Times(C2,
                          p), C0),
                      Not(Or(IntegerQ(p), GtQ(d, C0)))))),
          IIntegrate(5703,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCosh(
                              Times(c_DEFAULT, x_)), b_DEFAULT)),
                          n_DEFAULT),
                      Power(Plus($p("d1"), Times($p("e1", true),
                          x_)), p_DEFAULT),
                      Power(Plus($p("d2"), Times($p("e2", true), x_)), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Times(CN1, $s("d1"), $s("d2")), Subtract(p, C1D2)),
                          Sqrt(Plus($s("d1"), Times($s("e1"), x))),
                          Sqrt(Plus($s("d2"), Times($s("e2"), x))),
                          Power(Times(Sqrt(Plus(C1, Times(c, x))), Sqrt(Plus(CN1, Times(c, x)))),
                              CN1)),
                      Integrate(
                          Times(Power(Plus(C1, Times(c, x)), p), Power(Plus(CN1, Times(c, x)), p),
                              Power(Plus(a, Times(b, ArcCosh(Times(c, x)))), n)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, $s("d1"), $s("e1"), $s("d2"), $s("e2"), n), x),
                      EqQ($s("e1"), Times(c, $s("d1"))), EqQ($s("e2"), Times(CN1, c, $s(
                          "d2"))),
                      IGtQ(Times(C2, p), C0), Not(And(GtQ($s("d1"), C0), LtQ($s("d2"), C0)))))),
          IIntegrate(5704,
              Integrate(
                  Times(
                      Plus(a_DEFAULT, Times(ArcSinh(Times(c_DEFAULT, x_)),
                          b_DEFAULT)),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(u,
                          IntHide(Power(Plus(d, Times(e, Sqr(x))), p), x))),
                      Subtract(
                          Dist(Plus(a,
                              Times(b, ArcSinh(Times(c, x)))), u, x),
                          Dist(Times(b, c),
                              Integrate(
                                  SimplifyIntegrand(
                                      Times(u, Power(Plus(C1, Times(Sqr(c), Sqr(x))), CN1D2)), x),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e), x), NeQ(e, Times(Sqr(c), d)),
                      Or(IGtQ(p, C0), ILtQ(Plus(p, C1D2), C0))))),
          IIntegrate(5705,
              Integrate(
                  Times(
                      Plus(a_DEFAULT, Times(ArcCosh(Times(c_DEFAULT, x_)),
                          b_DEFAULT)),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(u,
                          IntHide(Power(Plus(d, Times(e, Sqr(x))), p), x))),
                      Subtract(Dist(Plus(a, Times(b, ArcCosh(Times(c, x)))), u, x),
                          Dist(Times(b, c),
                              Integrate(SimplifyIntegrand(Times(u,
                                  Power(Times(Sqrt(Plus(C1, Times(c, x))),
                                      Sqrt(Plus(CN1, Times(c, x)))), CN1)),
                                  x), x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e), x), NeQ(Plus(Times(Sqr(c), d),
                      e), C0), Or(IGtQ(p, C0),
                          ILtQ(Plus(p, C1D2), C0))))),
          IIntegrate(5706,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcSinh(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          n_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(Power(Plus(a, Times(b, ArcSinh(Times(c, x)))), n),
                          Power(Plus(d, Times(e, Sqr(x))), p), x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, n), x), NeQ(e, Times(Sqr(c),
                      d)), IntegerQ(
                          p),
                      Or(Greater(p, C0), IGtQ(n, C0))))),
          IIntegrate(5707,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCosh(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          n_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Power(Plus(a, Times(b, ArcCosh(Times(c, x)))),
                              n),
                          Power(Plus(d, Times(e, Sqr(x))), p), x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, n), x), NeQ(Plus(Times(Sqr(c), d), e), C0),
                      IntegerQ(p), Or(Greater(p, C0), IGtQ(n, C0))))),
          IIntegrate(5708,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT, Times(ArcSinh(Times(c_DEFAULT, x_)),
                          b_DEFAULT)), n_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Unintegrable(
                      Times(
                          Power(Plus(d,
                              Times(e, Sqr(x))), p),
                          Power(Plus(a, Times(b, ArcSinh(Times(c, x)))), n)),
                      x),
                  FreeQ(List(a, b, c, d, e, n, p), x))),
          IIntegrate(5709,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT, Times(ArcCosh(Times(c_DEFAULT, x_)),
                          b_DEFAULT)), n_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Unintegrable(
                      Times(
                          Power(Plus(d, Times(e, Sqr(x))),
                              p),
                          Power(Plus(a, Times(b, ArcCosh(Times(c, x)))), n)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, n, p), x), IntegerQ(p)))),
          IIntegrate(5710,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(ArcCosh(Times(c_DEFAULT, x_)), b_DEFAULT)), n_DEFAULT),
                      Power(Plus($p("d1"), Times($p("e1", true),
                          x_)), p_DEFAULT),
                      Power(Plus($p("d2"), Times($p("e2", true), x_)), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Unintegrable(
                      Times(
                          Power(Plus($s("d1"), Times($s("e1"),
                              x)), p),
                          Power(Plus($s("d2"), Times($s("e2"), x)), p), Power(
                              Plus(a, Times(b, ArcCosh(Times(c, x)))), n)),
                      x),
                  FreeQ(List(a, b, c, $s("d1"), $s("e1"), $s("d2"), $s("e2"), n, p), x))),
          IIntegrate(5711,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(ArcSinh(Times(c_DEFAULT, x_)), b_DEFAULT)), n_DEFAULT),
                      Power(Plus(d_,
                          Times(e_DEFAULT, x_)), p_),
                      Power(Plus(f_, Times(g_DEFAULT, x_)), q_)),
                  x_Symbol),
              Condition(
                  Dist(Power(Times(CN1, Sqr(d), g, Power(e, CN1)), q),
                      Integrate(Times(Power(Plus(d, Times(e, x)), Subtract(p, q)),
                          Power(Plus(C1, Times(Sqr(c), Sqr(x))),
                              q),
                          Power(Plus(a, Times(b, ArcSinh(Times(c, x)))), n)), x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g, n), x),
                      EqQ(Plus(Times(e, f), Times(d, g)), C0),
                      EqQ(Plus(Times(Sqr(c), Sqr(d)),
                          Sqr(e)), C0),
                      HalfIntegerQ(p, q), GeQ(Subtract(p,
                          q), C0),
                      GtQ(d, C0), LtQ(Times(g, Power(e, CN1)), C0)))),
          IIntegrate(5712,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcSinh(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          n_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, x_)),
                          p_),
                      Power(Plus(f_, Times(g_DEFAULT, x_)), q_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(Plus(d, Times(e, x)), q), Power(Plus(f, Times(g, x)),
                              q),
                          Power(Power(Plus(C1, Times(Sqr(c), Sqr(x))), q), CN1)),
                      Integrate(
                          Times(Power(Plus(d, Times(e, x)), Subtract(p, q)),
                              Power(Plus(C1,
                                  Times(Sqr(c), Sqr(x))), q),
                              Power(Plus(a, Times(b, ArcSinh(Times(c, x)))), n)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g, n), x),
                      EqQ(Plus(Times(e, f), Times(d,
                          g)), C0),
                      EqQ(Plus(Times(Sqr(c), Sqr(
                          d)), Sqr(
                              e)),
                          C0),
                      HalfIntegerQ(p, q), GeQ(Subtract(p, q), C0)))),
          IIntegrate(5713,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(ArcCosh(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          n_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Negate(d), IntPart(p)),
                          Power(Plus(d, Times(e, Sqr(x))), FracPart(p)),
                          Power(
                              Times(Power(Plus(C1, Times(c, x)), FracPart(p)),
                                  Power(Plus(CN1, Times(c, x)), FracPart(p))),
                              CN1)),
                      Integrate(
                          Times(
                              Power(Plus(C1, Times(c,
                                  x)), p),
                              Power(Plus(CN1, Times(c, x)), p),
                              Power(Plus(a, Times(b, ArcCosh(Times(c, x)))), n)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, n, p), x), EqQ(Plus(Times(Sqr(c), d), e), C0),
                      Not(IntegerQ(p))))),
          IIntegrate(5714,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcSinh(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          n_DEFAULT),
                      x_, Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1)),
                  x_Symbol),
              Condition(
                  Dist(Power(e, CN1),
                      Subst(Integrate(Times(Power(Plus(a, Times(b, x)), n), Tanh(x)), x), x,
                          ArcSinh(Times(c, x))),
                      x),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(e, Times(Sqr(c), d)), IGtQ(n, C0)))),
          IIntegrate(5715,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(ArcCosh(Times(c_DEFAULT, x_)), b_DEFAULT)), n_DEFAULT),
                      x_, Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1)),
                  x_Symbol),
              Condition(
                  Dist(Power(e, CN1),
                      Subst(
                          Integrate(Times(Power(Plus(a, Times(b, x)), n), Coth(x)),
                              x),
                          x, ArcCosh(Times(c, x))),
                      x),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(Plus(Times(Sqr(c), d),
                      e), C0), IGtQ(n,
                          C0)))),
          IIntegrate(5716,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCosh(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          n_DEFAULT),
                      x_, Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Power(Plus(d, Times(e, Sqr(x))), Plus(p, C1)),
                              Power(Plus(a, Times(b, ArcCosh(Times(c, x)))),
                                  n),
                              Power(Times(C2, e, Plus(p, C1)), CN1)),
                          x),
                      Dist(Times(b, n, Power(Negate(d), p), Power(Times(C2, c, Plus(p, C1)), CN1)),
                          Integrate(Times(Power(Plus(C1, Times(c, x)), Plus(p, C1D2)),
                              Power(Plus(CN1, Times(c, x)), Plus(p, C1D2)),
                              Power(Plus(a, Times(b, ArcCosh(Times(c, x)))), Subtract(n, C1))), x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, p), x), EqQ(Plus(Times(Sqr(c), d), e), C0),
                      GtQ(n, C0), NeQ(p, CN1), IntegerQ(p)))),
          IIntegrate(5717,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcSinh(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          n_DEFAULT),
                      x_, Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Power(Plus(d, Times(e, Sqr(x))), Plus(p, C1)),
                              Power(Plus(a,
                                  Times(b, ArcSinh(Times(c, x)))), n),
                              Power(Times(C2, e, Plus(p, C1)), CN1)),
                          x),
                      Dist(
                          Times(b, n, Power(d, IntPart(p)),
                              Power(Plus(d, Times(e, Sqr(x))), FracPart(p)),
                              Power(
                                  Times(
                                      C2, c, Plus(p, C1), Power(Plus(C1, Times(Sqr(c), Sqr(x))),
                                          FracPart(p))),
                                  CN1)),
                          Integrate(
                              Times(
                                  Power(Plus(C1, Times(Sqr(c), Sqr(x))), Plus(p, C1D2)), Power(Plus(
                                      a, Times(b, ArcSinh(Times(c, x)))),
                                      Subtract(n, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, p), x), EqQ(e, Times(Sqr(c),
                      d)), GtQ(n,
                          C0),
                      NeQ(p, CN1)))),
          IIntegrate(5718,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCosh(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          n_DEFAULT),
                      x_, Power(Plus($p("d1"), Times($p("e1", true),
                          x_)), p_DEFAULT),
                      Power(Plus($p("d2"), Times($p("e2", true), x_)), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Power(Plus($s("d1"), Times($s("e1"), x)), Plus(p, C1)),
                              Power(Plus($s("d2"), Times($s("e2"), x)), Plus(p, C1)),
                              Power(Plus(a,
                                  Times(b, ArcCosh(Times(c, x)))), n),
                              Power(Times(C2, $s("e1"), $s("e2"), Plus(p, C1)), CN1)),
                          x),
                      Dist(
                          Times(b, n, Power(Times(CN1, $s("d1"), $s("d2")), IntPart(p)),
                              Power(Plus($s("d1"), Times($s("e1"), x)), FracPart(p)),
                              Power(Plus($s("d2"), Times($s("e2"), x)), FracPart(p)),
                              Power(Times(C2, c, Plus(p, C1),
                                  Power(Plus(C1, Times(c, x)), FracPart(p)),
                                  Power(Plus(CN1, Times(c, x)), FracPart(p))), CN1)),
                          Integrate(
                              Times(
                                  Power(Plus(CN1, Times(Sqr(c), Sqr(x))), Plus(p,
                                      C1D2)),
                                  Power(Plus(a, Times(b, ArcCosh(Times(c, x)))), Subtract(n, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, $s("d1"), $s("e1"), $s("d2"), $s("e2"), p), x),
                      EqQ(Subtract($s("e1"), Times(c,
                          $s("d1"))), C0),
                      EqQ(Plus($s("e2"), Times(c, $s("d2"))), C0), GtQ(n, C0), NeQ(p,
                          CN1),
                      IntegerQ(Plus(p, C1D2))))),
          IIntegrate(5719,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCosh(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          n_DEFAULT),
                      x_, Power(Plus($p("d1"), Times($p("e1", true),
                          x_)), p_DEFAULT),
                      Power(Plus($p("d2"), Times($p("e2", true), x_)), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Power(Plus($s("d1"), Times($s("e1"), x)), Plus(p, C1)),
                              Power(Plus($s("d2"), Times($s("e2"), x)), Plus(p, C1)),
                              Power(Plus(a, Times(b, ArcCosh(Times(c, x)))), n), Power(
                                  Times(C2, $s("e1"), $s("e2"), Plus(p, C1)), CN1)),
                          x),
                      Dist(Times(b, n, Power(Times(CN1, $s("d1"), $s("d2")), IntPart(p)),
                          Power(Plus($s("d1"), Times($s("e1"), x)), FracPart(p)),
                          Power(Plus($s("d2"), Times($s("e2"), x)), FracPart(p)),
                          Power(Times(C2, c, Plus(p, C1), Power(Plus(C1, Times(c, x)), FracPart(p)),
                              Power(Plus(CN1, Times(c, x)), FracPart(p))), CN1)),
                          Integrate(
                              Times(Power(Plus(C1, Times(c, x)), Plus(p, C1D2)),
                                  Power(Plus(CN1, Times(c, x)), Plus(p, C1D2)), Power(Plus(a,
                                      Times(b, ArcCosh(Times(c, x)))),
                                      Subtract(n, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, $s("d1"), $s("e1"), $s("d2"), $s("e2"), p), x),
                      EqQ(Subtract($s("e1"), Times(c,
                          $s("d1"))), C0),
                      EqQ(Plus($s("e2"), Times(c, $s("d2"))), C0), GtQ(n, C0), NeQ(p, CN1)))),
          IIntegrate(5720, Integrate(
              Times(Power(Plus(a_DEFAULT, Times(ArcSinh(Times(c_DEFAULT, x_)), b_DEFAULT)),
                  n_DEFAULT), Power(x_, CN1), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1)),
              x_Symbol),
              Condition(
                  Dist(Power(d, CN1),
                      Subst(Integrate(Times(Power(Plus(a, Times(b, x)), n),
                          Power(Times(Cosh(x), Sinh(x)), CN1)), x), x, ArcSinh(Times(c, x))),
                      x),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(e, Times(Sqr(c), d)), IGtQ(n, C0)))));
}
