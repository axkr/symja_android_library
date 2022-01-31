package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctions.*;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules238 {
  public static IAST RULES =
      List(
          IIntegrate(4761,
              Integrate(
                  Times(
                      Plus(a_DEFAULT, Times(ArcSin(
                          Times(c_DEFAULT, x_)), b_DEFAULT)),
                      Power(Plus(f_,
                          Times(g_DEFAULT, x_)), m_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(u,
                          IntHide(Times(Power(Plus(f, Times(g, x)), m),
                              Power(Plus(d, Times(e, Sqr(x))), p)), x))),
                      Subtract(Dist(Plus(a, Times(b, ArcSin(Times(c, x)))), u, x),
                          Dist(Times(b, c),
                              Integrate(Dist(Power(Subtract(C1, Times(Sqr(c), Sqr(x))), CN1D2), u,
                                  x), x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e, f, g), x), EqQ(Plus(Times(Sqr(c), d), e), C0),
                      IGtQ(m, C0), ILtQ(Plus(p,
                          C1D2), C0),
                      GtQ(d, C0), Or(LtQ(m, Subtract(Times(CN2, p), C1)), GtQ(m, C3))))),
          IIntegrate(4762,
              Integrate(
                  Times(
                      Plus(a_DEFAULT, Times(ArcCos(
                          Times(c_DEFAULT, x_)), b_DEFAULT)),
                      Power(Plus(f_,
                          Times(g_DEFAULT, x_)), m_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  With(List(Set(u,
                      IntHide(Times(Power(Plus(f, Times(g, x)), m),
                          Power(Plus(d, Times(e, Sqr(x))), p)), x))),
                      Plus(Dist(Plus(a, Times(b, ArcCos(Times(c, x)))), u, x),
                          Dist(Times(b, c),
                              Integrate(Dist(Power(Subtract(C1, Times(Sqr(c), Sqr(x))), CN1D2), u,
                                  x), x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e, f, g), x), EqQ(Plus(Times(Sqr(c), d), e), C0),
                      IGtQ(m, C0), ILtQ(Plus(p,
                          C1D2), C0),
                      GtQ(d, C0), Or(LtQ(m, Subtract(Times(CN2, p), C1)), GtQ(m, C3))))),
          IIntegrate(4763,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcSin(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          n_DEFAULT),
                      Power(Plus(f_, Times(g_DEFAULT, x_)), m_DEFAULT), Power(
                          Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Times(
                              Power(Plus(d, Times(e, Sqr(x))),
                                  p),
                              Power(Plus(a, Times(b, ArcSin(Times(c, x)))), n)),
                          Power(Plus(f, Times(g, x)), m), x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g), x), EqQ(Plus(Times(Sqr(c), d), e), C0),
                      IGtQ(m, C0), IntegerQ(Plus(p,
                          C1D2)),
                      GtQ(d, C0), IGtQ(n, C0), Or(
                          Equal(m, C1), Greater(p, C0), And(Equal(n, C1),
                              Greater(p, CN1)),
                          And(Equal(m, C2), Less(p, CN2)))))),
          IIntegrate(4764,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(ArcCos(Times(c_DEFAULT, x_)), b_DEFAULT)), n_DEFAULT),
                      Power(Plus(f_, Times(g_DEFAULT, x_)), m_DEFAULT), Power(
                          Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Times(
                              Power(Plus(d, Times(e, Sqr(x))),
                                  p),
                              Power(Plus(a, Times(b, ArcCos(Times(c, x)))), n)),
                          Power(Plus(f, Times(g, x)), m), x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g), x), EqQ(Plus(Times(Sqr(c), d), e), C0),
                      IGtQ(m, C0), IntegerQ(Plus(p, C1D2)), GtQ(d, C0), IGtQ(n, C0),
                      Or(Equal(m, C1), Greater(p, C0), And(Equal(n, C1), Greater(p, CN1)),
                          And(Equal(m, C2), Less(p, CN2)))))),
          IIntegrate(4765,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcSin(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          n_DEFAULT),
                      Power(Plus(f_,
                          Times(g_DEFAULT, x_)), m_),
                      Sqrt(Plus(d_, Times(e_DEFAULT, Sqr(x_))))),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Power(Plus(f, Times(g, x)), m), Plus(d, Times(e, Sqr(x))),
                              Power(Plus(a, Times(b, ArcSin(Times(c, x)))), Plus(n, C1)), Power(
                                  Times(b, c, Sqrt(d), Plus(n, C1)), CN1)),
                          x),
                      Dist(
                          Power(Times(b, c, Sqrt(d),
                              Plus(n, C1)), CN1),
                          Integrate(
                              Times(
                                  Plus(Times(d, g, m), Times(C2, e, f, x),
                                      Times(e, g, Plus(m, C2), Sqr(x))),
                                  Power(Plus(f, Times(g, x)), Subtract(m, C1)), Power(Plus(a,
                                      Times(b, ArcSin(Times(c, x)))), Plus(n, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g), x), EqQ(Plus(Times(Sqr(c), d),
                      e), C0), ILtQ(m,
                          C0),
                      GtQ(d, C0), IGtQ(n, C0)))),
          IIntegrate(4766,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCos(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          n_DEFAULT),
                      Power(Plus(f_,
                          Times(g_DEFAULT, x_)), m_),
                      Sqrt(Plus(d_, Times(e_DEFAULT, Sqr(x_))))),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(Power(Plus(f, Times(g, x)), m), Plus(d, Times(e, Sqr(x))),
                                  Power(Plus(a, Times(b, ArcCos(Times(c, x)))), Plus(n, C1)),
                                  Power(Times(b, c, Sqrt(d), Plus(n, C1)), CN1)),
                              x)),
                      Dist(
                          Power(Times(b, c, Sqrt(d),
                              Plus(n, C1)), CN1),
                          Integrate(
                              Times(
                                  Plus(
                                      Times(d, g, m), Times(C2, e, f, x),
                                      Times(e, g, Plus(m, C2), Sqr(x))),
                                  Power(Plus(f, Times(g, x)), Subtract(m,
                                      C1)),
                                  Power(Plus(a, Times(b, ArcCos(Times(c, x)))), Plus(n, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g), x), EqQ(Plus(Times(Sqr(c), d),
                      e), C0), ILtQ(m,
                          C0),
                      GtQ(d, C0), IGtQ(n, C0)))),
          IIntegrate(4767,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(ArcSin(Times(c_DEFAULT, x_)), b_DEFAULT)), n_DEFAULT),
                      Power(Plus(f_,
                          Times(g_DEFAULT, x_)), m_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Times(
                              Sqrt(Plus(d, Times(e, Sqr(x)))), Power(
                                  Plus(a, Times(b, ArcSin(Times(c, x)))), n)),
                          Times(
                              Power(Plus(f, Times(g, x)), m), Power(Plus(d, Times(e, Sqr(x))),
                                  Subtract(p, C1D2))),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g), x), EqQ(Plus(Times(Sqr(c),
                      d), e), C0), IntegerQ(m), IGtQ(Plus(p,
                          C1D2), C0),
                      GtQ(d, C0), IGtQ(n, C0)))),
          IIntegrate(4768,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT, Times(ArcCos(Times(c_DEFAULT, x_)),
                          b_DEFAULT)), n_DEFAULT),
                      Power(Plus(f_, Times(g_DEFAULT, x_)), m_DEFAULT), Power(
                          Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Times(
                              Sqrt(Plus(d, Times(e, Sqr(x)))), Power(
                                  Plus(a, Times(b, ArcCos(Times(c, x)))), n)),
                          Times(
                              Power(Plus(f, Times(g, x)), m), Power(Plus(d, Times(e, Sqr(x))),
                                  Subtract(p, C1D2))),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g), x), EqQ(Plus(Times(Sqr(c),
                      d), e), C0), IntegerQ(m), IGtQ(Plus(p,
                          C1D2), C0),
                      GtQ(d, C0), IGtQ(n, C0)))),
          IIntegrate(4769,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(ArcSin(Times(c_DEFAULT, x_)), b_DEFAULT)), n_DEFAULT),
                      Power(Plus(f_,
                          Times(g_DEFAULT, x_)), m_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Power(Plus(f, Times(g, x)), m),
                              Power(Plus(d, Times(e, Sqr(x))), Plus(p, C1D2)),
                              Power(Plus(a, Times(b, ArcSin(Times(c, x)))), Plus(n, C1)), Power(
                                  Times(b, c, Sqrt(d), Plus(n, C1)), CN1)),
                          x),
                      Dist(
                          Power(Times(b, c, Sqrt(d),
                              Plus(n, C1)), CN1),
                          Integrate(
                              ExpandIntegrand(
                                  Times(Power(Plus(f, Times(g, x)), Subtract(m, C1)), Power(
                                      Plus(a, Times(b, ArcSin(Times(c, x)))), Plus(n, C1))),
                                  Times(Plus(Times(d, g, m), Times(e, f, Plus(Times(C2, p),
                                      C1), x), Times(e, g, Plus(m, Times(C2, p), C1),
                                          Sqr(x))),
                                      Power(Plus(d, Times(e, Sqr(x))), Subtract(p, C1D2))),
                                  x),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g), x), EqQ(Plus(Times(Sqr(c),
                      d), e), C0), ILtQ(m, C0), IGtQ(Subtract(p,
                          C1D2), C0),
                      GtQ(d, C0), IGtQ(n, C0)))),
          IIntegrate(4770,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT, Times(ArcCos(Times(c_DEFAULT, x_)),
                          b_DEFAULT)), n_DEFAULT),
                      Power(Plus(f_, Times(g_DEFAULT, x_)), m_DEFAULT), Power(
                          Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(Plus(Negate(Simp(Times(
                  Power(Plus(f, Times(g, x)), m), Power(Plus(d, Times(e, Sqr(x))), Plus(p, C1D2)),
                  Power(Plus(a, Times(b, ArcCos(Times(c, x)))),
                      Plus(n, C1)),
                  Power(Times(b, c, Sqrt(d), Plus(n, C1)), CN1)),
                  x)), Dist(Power(Times(b, c, Sqrt(d), Plus(n, C1)), CN1),
                      Integrate(
                          ExpandIntegrand(
                              Times(Power(Plus(f, Times(g, x)), Subtract(m, C1)),
                                  Power(Plus(a, Times(b, ArcCos(Times(c, x)))), Plus(n, C1))),
                              Times(
                                  Plus(Times(d, g, m), Times(e, f, Plus(Times(C2, p), C1), x),
                                      Times(e, g, Plus(m, Times(C2, p), C1), Sqr(x))),
                                  Power(Plus(d, Times(e, Sqr(x))), Subtract(p, C1D2))),
                              x),
                          x),
                      x)),
                  And(FreeQ(List(a, b, c, d, e, f, g), x), EqQ(Plus(Times(Sqr(c),
                      d), e), C0), ILtQ(m, C0), IGtQ(Subtract(p,
                          C1D2), C0),
                      GtQ(d, C0), IGtQ(n, C0)))),
          IIntegrate(4771,
              Integrate(
                  Times(Power(Plus(a_DEFAULT, Times(ArcSin(Times(c_DEFAULT, x_)), b_DEFAULT)), n_),
                      Power(Plus(f_,
                          Times(g_DEFAULT, x_)), m_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Power(Plus(f, Times(g, x)), m),
                              Power(Plus(a, Times(b, ArcSin(Times(c, x)))), Plus(n, C1)), Power(
                                  Times(b, c, Sqrt(d), Plus(n, C1)), CN1)),
                          x),
                      Dist(
                          Times(g, m, Power(Times(b, c, Sqrt(d), Plus(n, C1)),
                              CN1)),
                          Integrate(
                              Times(
                                  Power(Plus(f, Times(g, x)), Subtract(m,
                                      C1)),
                                  Power(Plus(a, Times(b, ArcSin(Times(c, x)))), Plus(n, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g), x), EqQ(Plus(Times(Sqr(c), d),
                      e), C0), IGtQ(m,
                          C0),
                      GtQ(d, C0), LtQ(n, CN1)))),
          IIntegrate(4772,
              Integrate(
                  Times(Power(Plus(a_DEFAULT, Times(ArcCos(Times(c_DEFAULT, x_)), b_DEFAULT)), n_),
                      Power(
                          Plus(f_, Times(g_DEFAULT, x_)), m_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Plus(Negate(Simp(Times(Power(Plus(f, Times(g, x)), m),
                      Power(Plus(a, Times(b, ArcCos(Times(c, x)))), Plus(n, C1)),
                      Power(Times(b, c, Sqrt(d), Plus(n, C1)), CN1)), x)), Dist(
                          Times(g, m, Power(Times(b, c, Sqrt(d), Plus(n, C1)), CN1)),
                          Integrate(
                              Times(Power(Plus(f, Times(g, x)), Subtract(m, C1)),
                                  Power(Plus(a, Times(b, ArcCos(Times(c, x)))), Plus(n, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g), x), EqQ(Plus(Times(Sqr(c), d), e), C0),
                      IGtQ(m, C0), GtQ(d, C0), LtQ(n, CN1)))),
          IIntegrate(4773,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(ArcSin(Times(c_DEFAULT, x_)), b_DEFAULT)), n_DEFAULT),
                      Power(Plus(f_,
                          Times(g_DEFAULT, x_)), m_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Dist(
                      Power(Times(Power(c, Plus(m, C1)),
                          Sqrt(d)), CN1),
                      Subst(
                          Integrate(
                              Times(Power(Plus(a, Times(b, x)), n),
                                  Power(Plus(Times(c, f), Times(g, Sin(x))), m)),
                              x),
                          x, ArcSin(Times(c, x))),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g,
                      n), x), EqQ(Plus(Times(Sqr(c), d), e),
                          C0),
                      IntegerQ(m), GtQ(d, C0), Or(GtQ(m, C0), IGtQ(n, C0))))),
          IIntegrate(4774,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(ArcCos(Times(c_DEFAULT, x_)), b_DEFAULT)), n_DEFAULT),
                      Power(Plus(f_,
                          Times(g_DEFAULT, x_)), m_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Negate(
                      Dist(
                          Power(Times(Power(c, Plus(m, C1)),
                              Sqrt(d)), CN1),
                          Subst(
                              Integrate(
                                  Times(
                                      Power(Plus(a, Times(b, x)), n), Power(
                                          Plus(Times(c, f), Times(g, Cos(x))), m)),
                                  x),
                              x, ArcCos(Times(c, x))),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g, n), x), EqQ(Plus(Times(Sqr(c), d), e), C0),
                      IntegerQ(m), GtQ(d, C0), Or(GtQ(m, C0), IGtQ(n, C0))))),
          IIntegrate(4775,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(ArcSin(Times(c_DEFAULT, x_)), b_DEFAULT)), n_DEFAULT),
                      Power(Plus(f_,
                          Times(g_DEFAULT, x_)), m_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Times(
                              Power(Plus(a, Times(b, ArcSin(Times(c, x)))),
                                  n),
                              Power(Plus(d, Times(e, Sqr(x))), CN1D2)),
                          Times(
                              Power(Plus(f,
                                  Times(g, x)), m),
                              Power(Plus(d, Times(e, Sqr(x))), Plus(p, C1D2))),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g), x), EqQ(Plus(Times(Sqr(c),
                      d), e), C0), IntegerQ(m), ILtQ(Plus(p,
                          C1D2), C0),
                      GtQ(d, C0), IGtQ(n, C0)))),
          IIntegrate(4776,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(ArcCos(Times(c_DEFAULT, x_)), b_DEFAULT)), n_DEFAULT),
                      Power(Plus(f_,
                          Times(g_DEFAULT, x_)), m_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Times(
                              Power(Plus(a,
                                  Times(b, ArcCos(Times(c, x)))), n),
                              Power(Plus(d, Times(e, Sqr(x))), CN1D2)),
                          Times(Power(Plus(f, Times(g, x)), m),
                              Power(Plus(d, Times(e, Sqr(x))), Plus(p, C1D2))),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g), x), EqQ(Plus(Times(Sqr(c),
                      d), e), C0), IntegerQ(m), ILtQ(Plus(p,
                          C1D2), C0),
                      GtQ(d, C0), IGtQ(n, C0)))),
          IIntegrate(4777,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(ArcSin(Times(c_DEFAULT, x_)), b_DEFAULT)), n_DEFAULT),
                      Power(Plus(f_, Times(g_DEFAULT, x_)), m_DEFAULT), Power(
                          Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(d, IntPart(p)), Power(Plus(d, Times(e, Sqr(x))), FracPart(
                              p)),
                          Power(Power(Subtract(C1, Times(Sqr(c), Sqr(x))), FracPart(p)), CN1)),
                      Integrate(
                          Times(Power(Plus(f, Times(g, x)), m),
                              Power(Subtract(C1, Times(Sqr(c), Sqr(x))), p),
                              Power(Plus(a, Times(b, ArcSin(Times(c, x)))), n)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g, n), x), EqQ(Plus(Times(Sqr(c),
                      d), e), C0), IntegerQ(m), IntegerQ(Subtract(p,
                          C1D2)),
                      Not(GtQ(d, C0))))),
          IIntegrate(4778,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCos(
                              Times(c_DEFAULT, x_)), b_DEFAULT)),
                          n_DEFAULT),
                      Power(Plus(f_,
                          Times(g_DEFAULT, x_)), m_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(d, IntPart(p)), Power(Plus(d, Times(e, Sqr(x))), FracPart(
                              p)),
                          Power(Power(Subtract(C1, Times(Sqr(c), Sqr(x))), FracPart(p)), CN1)),
                      Integrate(
                          Times(
                              Power(Plus(f, Times(g,
                                  x)), m),
                              Power(Subtract(C1,
                                  Times(Sqr(c), Sqr(x))), p),
                              Power(Plus(a, Times(b, ArcCos(Times(c, x)))), n)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g, n), x), EqQ(Plus(Times(Sqr(c), d),
                      e), C0), IntegerQ(
                          m),
                      IntegerQ(Subtract(p, C1D2)), Not(GtQ(d, C0))))),
          IIntegrate(4779,
              Integrate(
                  Times(
                      Log(Times(h_DEFAULT,
                          Power(Plus(f_DEFAULT, Times(g_DEFAULT, x_)), m_DEFAULT))),
                      Power(
                          Plus(a_DEFAULT, Times(ArcSin(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          n_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Log(Times(h, Power(Plus(f, Times(g, x)), m))),
                              Power(Plus(a, Times(b,
                                  ArcSin(Times(c, x)))), Plus(n,
                                      C1)),
                              Power(Times(b, c, Sqrt(d), Plus(n, C1)), CN1)),
                          x),
                      Dist(
                          Times(g, m, Power(Times(b, c, Sqrt(d), Plus(n, C1)),
                              CN1)),
                          Integrate(
                              Times(
                                  Power(Plus(a, Times(b, ArcSin(Times(c, x)))),
                                      Plus(n, C1)),
                                  Power(Plus(f, Times(g, x)), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g, h, m), x), EqQ(Plus(Times(Sqr(c), d),
                      e), C0), GtQ(d,
                          C0),
                      IGtQ(n, C0)))),
          IIntegrate(4780,
              Integrate(
                  Times(
                      Log(Times(h_DEFAULT,
                          Power(Plus(f_DEFAULT, Times(g_DEFAULT, x_)), m_DEFAULT))),
                      Power(Plus(
                          a_DEFAULT, Times(ArcCos(Times(c_DEFAULT, x_)), b_DEFAULT)), n_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(
                                  Log(Times(h, Power(Plus(f,
                                      Times(g, x)), m))),
                                  Power(Plus(a, Times(b, ArcCos(Times(c, x)))), Plus(n,
                                      C1)),
                                  Power(Times(b, c, Sqrt(d), Plus(n, C1)), CN1)),
                              x)),
                      Dist(Times(g, m, Power(Times(b, c, Sqrt(d), Plus(n, C1)), CN1)),
                          Integrate(
                              Times(Power(Plus(a, Times(b, ArcCos(Times(c, x)))), Plus(n, C1)),
                                  Power(Plus(f, Times(g, x)), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g, h, m), x), EqQ(Plus(Times(Sqr(c), d), e), C0),
                      GtQ(d, C0), IGtQ(n, C0)))));
}
