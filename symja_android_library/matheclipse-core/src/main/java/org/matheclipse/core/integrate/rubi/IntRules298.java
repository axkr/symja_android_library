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
class IntRules298 {
  public static IAST RULES =
      List(
          IIntegrate(5961,
              Integrate(Times(Plus(a_DEFAULT, Times(ArcCoth(Times(c_DEFAULT, x_)), b_DEFAULT)),
                  Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_)), x_Symbol),
              Condition(
                  Plus(Negate(
                      Simp(
                          Times(b, Power(Plus(d, Times(e, Sqr(x))), Plus(q, C1)),
                              Power(Times(C4, c, d, Sqr(Plus(q, C1))), CN1)),
                          x)),
                      Dist(Times(Plus(Times(C2, q), C3), Power(Times(C2, d, Plus(q, C1)), CN1)),
                          Integrate(
                              Times(Power(Plus(d, Times(e, Sqr(x))), Plus(q, C1)),
                                  Plus(a, Times(b, ArcCoth(Times(c, x))))),
                              x),
                          x),
                      Negate(
                          Simp(
                              Times(x, Power(Plus(d, Times(e, Sqr(x))), Plus(q, C1)),
                                  Plus(a, Times(b, ArcCoth(Times(c, x)))), Power(
                                      Times(C2, d, Plus(q, C1)), CN1)),
                              x))),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(Plus(Times(Sqr(c), d),
                      e), C0), LtQ(q,
                          CN1),
                      NeQ(q, QQ(-3L, 2L))))),
          IIntegrate(5962,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT, Times(ArcTanh(Times(c_DEFAULT, x_)),
                          b_DEFAULT)), p_),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), QQ(-3L, 2L))),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(b, p,
                                  Power(Plus(a, Times(b, ArcTanh(Times(c, x)))), Subtract(p,
                                      C1)),
                                  Power(Times(c, d, Sqrt(Plus(d, Times(e, Sqr(x))))), CN1)),
                              x)),
                      Dist(
                          Times(Sqr(b), p, Subtract(p,
                              C1)),
                          Integrate(
                              Times(Power(Plus(a, Times(b, ArcTanh(Times(c, x)))), Subtract(p, C2)),
                                  Power(Plus(d, Times(e, Sqr(x))), QQ(-3L, 2L))),
                              x),
                          x),
                      Simp(
                          Times(
                              x, Power(Plus(a, Times(b, ArcTanh(Times(c, x)))), p), Power(
                                  Times(d, Sqrt(Plus(d, Times(e, Sqr(x))))), CN1)),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(Plus(Times(Sqr(c), d), e), C0),
                      GtQ(p, C1)))),
          IIntegrate(5963,
              Integrate(Times(Power(Plus(a_DEFAULT,
                  Times(ArcCoth(Times(c_DEFAULT, x_)), b_DEFAULT)), p_), Power(
                      Plus(d_, Times(e_DEFAULT, Sqr(x_))), QQ(-3L, 2L))),
                  x_Symbol),
              Condition(
                  Plus(Negate(Simp(
                      Times(b, p, Power(Plus(a, Times(b, ArcCoth(Times(c, x)))), Subtract(p, C1)),
                          Power(Times(c, d, Sqrt(Plus(d, Times(e, Sqr(x))))), CN1)),
                      x)), Dist(
                          Times(Sqr(b), p, Subtract(p, C1)),
                          Integrate(
                              Times(Power(Plus(a, Times(b, ArcCoth(Times(c, x)))), Subtract(p, C2)),
                                  Power(Plus(d, Times(e, Sqr(x))), QQ(-3L, 2L))),
                              x),
                          x),
                      Simp(
                          Times(
                              x, Power(Plus(a,
                                  Times(b, ArcCoth(Times(c, x)))), p),
                              Power(Times(d, Sqrt(Plus(d, Times(e, Sqr(x))))), CN1)),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(Plus(Times(Sqr(c), d),
                      e), C0), GtQ(p,
                          C1)))),
          IIntegrate(5964,
              Integrate(Times(
                  Power(Plus(a_DEFAULT, Times(ArcTanh(Times(c_DEFAULT, x_)), b_DEFAULT)), p_),
                  Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_)), x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(
                                  b, p, Power(Plus(d, Times(e,
                                      Sqr(x))), Plus(q,
                                          C1)),
                                  Power(Plus(a, Times(b, ArcTanh(Times(c, x)))), Subtract(p,
                                      C1)),
                                  Power(Times(C4, c, d, Sqr(Plus(q, C1))), CN1)),
                              x)),
                      Dist(
                          Times(Plus(Times(C2, q), C3), Power(Times(C2, d, Plus(q, C1)),
                              CN1)),
                          Integrate(
                              Times(
                                  Power(Plus(d, Times(e, Sqr(x))), Plus(q,
                                      C1)),
                                  Power(Plus(a, Times(b, ArcTanh(Times(c, x)))), p)),
                              x),
                          x),
                      Dist(
                          Times(Sqr(b), p, Subtract(p, C1), Power(Times(C4, Sqr(Plus(q, C1))),
                              CN1)),
                          Integrate(
                              Times(Power(Plus(d, Times(e, Sqr(x))), q),
                                  Power(Plus(a, Times(b, ArcTanh(Times(c, x)))), Subtract(p, C2))),
                              x),
                          x),
                      Negate(
                          Simp(
                              Times(x, Power(Plus(d, Times(e, Sqr(x))), Plus(q, C1)),
                                  Power(Plus(a, Times(b, ArcTanh(Times(c, x)))),
                                      p),
                                  Power(Times(C2, d, Plus(q, C1)), CN1)),
                              x))),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(Plus(Times(Sqr(c), d),
                      e), C0), LtQ(q,
                          CN1),
                      GtQ(p, C1), NeQ(q, QQ(-3L, 2L))))),
          IIntegrate(5965,
              Integrate(Times(
                  Power(Plus(a_DEFAULT, Times(ArcCoth(Times(c_DEFAULT, x_)), b_DEFAULT)), p_),
                  Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_)), x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(
                                  b, p, Power(Plus(d, Times(e,
                                      Sqr(x))), Plus(q,
                                          C1)),
                                  Power(Plus(a, Times(b, ArcCoth(Times(c, x)))), Subtract(p,
                                      C1)),
                                  Power(Times(C4, c, d, Sqr(Plus(q, C1))), CN1)),
                              x)),
                      Dist(
                          Times(Plus(Times(C2,
                              q), C3), Power(Times(C2, d, Plus(q, C1)),
                                  CN1)),
                          Integrate(Times(Power(Plus(d, Times(e, Sqr(x))), Plus(q, C1)),
                              Power(Plus(a, Times(b, ArcCoth(Times(c, x)))), p)), x),
                          x),
                      Dist(
                          Times(Sqr(b), p, Subtract(p, C1), Power(Times(C4, Sqr(Plus(q, C1))),
                              CN1)),
                          Integrate(
                              Times(Power(Plus(d, Times(e, Sqr(x))), q),
                                  Power(Plus(a, Times(b, ArcCoth(Times(c, x)))), Subtract(p, C2))),
                              x),
                          x),
                      Negate(
                          Simp(
                              Times(x, Power(Plus(d, Times(e, Sqr(x))), Plus(q, C1)),
                                  Power(Plus(a, Times(b, ArcCoth(Times(c, x)))), p),
                                  Power(Times(C2, d, Plus(q, C1)), CN1)),
                              x))),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(Plus(Times(Sqr(c), d),
                      e), C0), LtQ(q,
                          CN1),
                      GtQ(p, C1), NeQ(q, QQ(-3L, 2L))))),
          IIntegrate(5966,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(ArcTanh(Times(c_DEFAULT, x_)), b_DEFAULT)), p_),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(Plus(d, Times(e, Sqr(x))), Plus(q, C1)),
                              Power(Plus(a, Times(b, ArcTanh(Times(c, x)))),
                                  Plus(p, C1)),
                              Power(Times(b, c, d, Plus(p, C1)), CN1)),
                          x),
                      Dist(
                          Times(C2, c, Plus(q, C1), Power(Times(b, Plus(p, C1)),
                              CN1)),
                          Integrate(
                              Times(
                                  x, Power(Plus(d, Times(e,
                                      Sqr(x))), q),
                                  Power(Plus(a, Times(b, ArcTanh(Times(c, x)))), Plus(p, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(Plus(Times(Sqr(c), d),
                      e), C0), LtQ(q,
                          CN1),
                      LtQ(p, CN1)))),
          IIntegrate(5967,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(ArcCoth(Times(c_DEFAULT, x_)), b_DEFAULT)), p_),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(Plus(d, Times(e, Sqr(x))), Plus(q, C1)),
                              Power(Plus(a, Times(b, ArcCoth(Times(c, x)))),
                                  Plus(p, C1)),
                              Power(Times(b, c, d, Plus(p, C1)), CN1)),
                          x),
                      Dist(
                          Times(C2, c, Plus(q, C1), Power(Times(b, Plus(p, C1)),
                              CN1)),
                          Integrate(
                              Times(x, Power(Plus(d, Times(e, Sqr(x))), q),
                                  Power(Plus(a, Times(b, ArcCoth(Times(c, x)))), Plus(p, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(Plus(Times(Sqr(c), d),
                      e), C0), LtQ(q,
                          CN1),
                      LtQ(p, CN1)))),
          IIntegrate(5968,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(ArcTanh(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          p_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(d, q), Power(c,
                          CN1)),
                      Subst(
                          Integrate(
                              Times(
                                  Power(Plus(a, Times(b, x)), p), Power(
                                      Power(Cosh(x), Times(C2, Plus(q, C1))), CN1)),
                              x),
                          x, ArcTanh(Times(c, x))),
                      x),
                  And(FreeQ(List(a, b, c, d, e, p), x), EqQ(Plus(Times(Sqr(c),
                      d), e), C0), ILtQ(Times(C2,
                          Plus(q, C1)), C0),
                      Or(IntegerQ(q), GtQ(d, C0))))),
          IIntegrate(5969,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcTanh(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          p_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(d, Plus(q, C1D2)), Sqrt(Subtract(C1, Times(Sqr(c), Sqr(x)))), Power(
                              Plus(d, Times(e, Sqr(x))), CN1D2)),
                      Integrate(
                          Times(
                              Power(Subtract(C1, Times(Sqr(c), Sqr(x))), q), Power(
                                  Plus(a, Times(b, ArcTanh(Times(c, x)))), p)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, p), x), EqQ(Plus(Times(Sqr(c), d), e), C0), ILtQ(
                      Times(C2, Plus(q, C1)), C0), Not(Or(IntegerQ(q), GtQ(d, C0)))))),
          IIntegrate(5970,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(ArcCoth(Times(c_DEFAULT, x_)), b_DEFAULT)), p_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_)),
                  x_Symbol),
              Condition(
                  Negate(
                      Dist(Times(Power(Negate(d), q), Power(c, CN1)),
                          Subst(
                              Integrate(
                                  Times(Power(Plus(a, Times(b, x)), p),
                                      Power(Power(Sinh(x), Times(C2, Plus(q, C1))), CN1)),
                                  x),
                              x, ArcCoth(Times(c, x))),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, p), x), EqQ(Plus(Times(Sqr(c), d), e), C0),
                      ILtQ(Times(C2, Plus(q, C1)), C0), IntegerQ(q)))),
          IIntegrate(5971,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(ArcCoth(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          p_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_)),
                  x_Symbol),
              Condition(
                  Negate(
                      Dist(Times(Power(Negate(d), Plus(q, C1D2)), x,
                          Sqrt(Times(Subtract(Times(Sqr(c), Sqr(x)), C1),
                              Power(Times(Sqr(c), Sqr(x)), CN1))),
                          Power(Plus(d, Times(e, Sqr(x))), CN1D2)),
                          Subst(
                              Integrate(
                                  Times(Power(Plus(a, Times(b, x)), p), Power(
                                      Power(Sinh(x), Times(C2, Plus(q, C1))), CN1)),
                                  x),
                              x, ArcCoth(Times(c, x))),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, p), x), EqQ(Plus(Times(Sqr(c), d), e), C0),
                      ILtQ(Times(C2, Plus(q, C1)), C0), Not(IntegerQ(q))))),
          IIntegrate(5972,
              Integrate(
                  Times(
                      ArcTanh(Times(c_DEFAULT,
                          x_)),
                      Power(Plus(d_DEFAULT, Times(e_DEFAULT, Sqr(x_))), CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(C1D2,
                          Integrate(
                              Times(Log(Plus(C1, Times(c, x))),
                                  Power(Plus(d, Times(e, Sqr(x))), CN1)),
                              x),
                          x),
                      Dist(C1D2,
                          Integrate(
                              Times(
                                  Log(Subtract(C1,
                                      Times(c, x))),
                                  Power(Plus(d, Times(e, Sqr(x))), CN1)),
                              x),
                          x)),
                  FreeQ(List(c, d, e), x))),
          IIntegrate(5973,
              Integrate(
                  Times(
                      ArcCoth(Times(c_DEFAULT,
                          x_)),
                      Power(Plus(d_DEFAULT, Times(e_DEFAULT, Sqr(x_))), CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(C1D2,
                          Integrate(
                              Times(Log(Plus(C1, Power(Times(c, x), CN1))),
                                  Power(Plus(d, Times(e, Sqr(x))), CN1)),
                              x),
                          x),
                      Dist(C1D2,
                          Integrate(
                              Times(Log(Subtract(C1, Power(Times(c, x), CN1))),
                                  Power(Plus(d, Times(e, Sqr(x))), CN1)),
                              x),
                          x)),
                  FreeQ(List(c, d, e), x))),
          IIntegrate(5974,
              Integrate(
                  Times(
                      Plus(Times(ArcTanh(Times(c_DEFAULT, x_)),
                          b_DEFAULT), a_),
                      Power(Plus(d_DEFAULT, Times(e_DEFAULT, Sqr(x_))), CN1)),
                  x_Symbol),
              Condition(
                  Plus(Dist(a, Integrate(Power(Plus(d, Times(e, Sqr(x))), CN1), x), x),
                      Dist(b,
                          Integrate(
                              Times(ArcTanh(Times(c, x)),
                                  Power(Plus(d, Times(e, Sqr(x))), CN1)),
                              x),
                          x)),
                  FreeQ(List(a, b, c, d, e), x))),
          IIntegrate(5975,
              Integrate(
                  Times(
                      Plus(Times(ArcCoth(Times(c_DEFAULT, x_)),
                          b_DEFAULT), a_),
                      Power(Plus(d_DEFAULT, Times(e_DEFAULT, Sqr(x_))), CN1)),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(a, Integrate(Power(Plus(d, Times(e, Sqr(x))), CN1),
                          x), x),
                      Dist(b,
                          Integrate(
                              Times(ArcCoth(Times(c, x)),
                                  Power(Plus(d, Times(e, Sqr(x))), CN1)),
                              x),
                          x)),
                  FreeQ(List(a, b, c, d, e), x))),
          IIntegrate(5976,
              Integrate(
                  Times(
                      Plus(a_DEFAULT, Times(ArcTanh(Times(c_DEFAULT, x_)),
                          b_DEFAULT)),
                      Power(Plus(d_DEFAULT, Times(e_DEFAULT, Sqr(x_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(List(Set(u, IntHide(Power(Plus(d, Times(e, Sqr(x))), q), x))),
                      Subtract(Dist(Plus(a, Times(b, ArcTanh(Times(c, x)))), u, x),
                          Dist(Times(b, c),
                              Integrate(Times(u, Power(Subtract(C1, Times(Sqr(c), Sqr(x))), CN1)),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e), x), Or(IntegerQ(q), ILtQ(Plus(q, C1D2), C0))))),
          IIntegrate(5977,
              Integrate(
                  Times(
                      Plus(a_DEFAULT, Times(ArcCoth(Times(c_DEFAULT, x_)),
                          b_DEFAULT)),
                      Power(Plus(d_DEFAULT, Times(e_DEFAULT, Sqr(x_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(u,
                          IntHide(Power(Plus(d, Times(e, Sqr(x))), q), x))),
                      Subtract(Dist(Plus(a, Times(b, ArcCoth(Times(c, x)))), u, x),
                          Dist(Times(b, c),
                              Integrate(Times(u, Power(Subtract(C1, Times(Sqr(c), Sqr(x))), CN1)),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e), x), Or(IntegerQ(q), ILtQ(Plus(q, C1D2), C0))))),
          IIntegrate(5978,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcTanh(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          p_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Power(Plus(a, Times(b, ArcTanh(Times(c, x)))),
                              p),
                          Power(Plus(d, Times(e, Sqr(x))), q), x),
                      x),
                  And(FreeQ(List(a, b, c, d, e), x), IntegerQ(q), IGtQ(p, C0)))),
          IIntegrate(5979,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCoth(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          p_DEFAULT),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Power(Plus(a,
                              Times(b, ArcCoth(Times(c, x)))), p),
                          Power(Plus(d, Times(e, Sqr(x))), q), x),
                      x),
                  And(FreeQ(List(a, b, c, d, e), x), IntegerQ(q), IGtQ(p, C0)))),
          IIntegrate(5980,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcTanh(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          p_DEFAULT),
                      Power(Times(f_DEFAULT,
                          x_), m_),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Times(Sqr(f), Power(e, CN1)),
                          Integrate(Times(Power(Times(f, x), Subtract(m, C2)),
                              Power(Plus(a, Times(b, ArcTanh(Times(c, x)))), p)), x),
                          x),
                      Dist(Times(d, Sqr(f), Power(e, CN1)),
                          Integrate(Times(Power(Times(f, x), Subtract(m, C2)),
                              Power(Plus(a, Times(b, ArcTanh(Times(c, x)))), p),
                              Power(Plus(d, Times(e, Sqr(x))), CN1)), x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), GtQ(p, C0), GtQ(m, C1)))));
}
