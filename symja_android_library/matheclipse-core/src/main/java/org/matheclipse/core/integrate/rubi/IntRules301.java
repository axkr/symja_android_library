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
class IntRules301 {
  public static IAST RULES =
      List(
          IIntegrate(6021,
              Integrate(
                  Times(Power(Plus(a_DEFAULT,
                      Times(ArcCoth(Times(c_DEFAULT, x_)), b_DEFAULT)), p_), Power(x_, CN1), Power(
                          Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Negate(
                      Dist(
                          Times(
                              c, x, Sqrt(Subtract(C1,
                                  Power(Times(Sqr(c), Sqr(x)), CN1))),
                              Power(Plus(d, Times(e, Sqr(x))), CN1D2)),
                          Subst(Integrate(Times(Power(Plus(a, Times(b, x)), p), Sech(x)), x), x,
                              ArcCoth(Times(c, x))),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(Plus(Times(Sqr(c), d), e), C0),
                      IGtQ(p, C0), GtQ(d, C0)))),
          IIntegrate(6022,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcTanh(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          p_DEFAULT),
                      Power(x_, CN1), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Sqrt(Subtract(C1,
                              Times(Sqr(c), Sqr(x)))),
                          Power(Plus(d, Times(e, Sqr(x))), CN1D2)),
                      Integrate(
                          Times(
                              Power(Plus(a, Times(b, ArcTanh(Times(c, x)))), p), Power(Times(x,
                                  Sqrt(Subtract(C1, Times(Sqr(c), Sqr(x))))), CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(Plus(Times(Sqr(c), d),
                      e), C0), IGtQ(p,
                          C0),
                      Not(GtQ(d, C0))))),
          IIntegrate(6023,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT, Times(ArcCoth(Times(c_DEFAULT, x_)),
                          b_DEFAULT)), p_DEFAULT),
                      Power(x_, CN1), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Sqrt(Subtract(C1, Times(Sqr(c), Sqr(x)))), Power(
                              Plus(d, Times(e, Sqr(x))), CN1D2)),
                      Integrate(
                          Times(
                              Power(Plus(a, Times(b,
                                  ArcCoth(Times(c, x)))), p),
                              Power(Times(x, Sqrt(Subtract(C1, Times(Sqr(c), Sqr(x))))), CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(Plus(Times(Sqr(c), d), e), C0),
                      IGtQ(p, C0), Not(GtQ(d, C0))))),
          IIntegrate(6024,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT, Times(ArcTanh(Times(c_DEFAULT, x_)),
                          b_DEFAULT)), p_DEFAULT),
                      Power(x_, CN2), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(Times(Sqrt(Plus(d, Times(e, Sqr(x)))),
                              Power(Plus(a, Times(b, ArcTanh(Times(c, x)))), p), Power(Times(d, x),
                                  CN1)),
                              x)),
                      Dist(Times(b, c, p),
                          Integrate(
                              Times(
                                  Power(Plus(a, Times(b, ArcTanh(Times(c, x)))), Subtract(p,
                                      C1)),
                                  Power(Times(x, Sqrt(Plus(d, Times(e, Sqr(x))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(Plus(Times(Sqr(c), d), e), C0),
                      GtQ(p, C0)))),
          IIntegrate(6025,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCoth(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          p_DEFAULT),
                      Power(x_, CN2), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(Times(Sqrt(Plus(d, Times(e, Sqr(x)))),
                              Power(Plus(a, Times(b, ArcCoth(Times(c, x)))), p), Power(Times(d, x),
                                  CN1)),
                              x)),
                      Dist(Times(b, c, p),
                          Integrate(Times(Power(Plus(a, Times(b, ArcCoth(Times(c, x)))), Subtract(p,
                              C1)), Power(Times(x, Sqrt(Plus(d, Times(e, Sqr(x))))),
                                  CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(Plus(Times(Sqr(c), d), e), C0),
                      GtQ(p, C0)))),
          IIntegrate(6026,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcTanh(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          p_DEFAULT),
                      Power(Times(f_DEFAULT,
                          x_), m_),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1D2)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(
                              Power(Times(f, x), Plus(m, C1)), Sqrt(Plus(d, Times(e,
                                  Sqr(x)))),
                              Power(Plus(a,
                                  Times(b, ArcTanh(Times(c, x)))), p),
                              Power(Times(d, f, Plus(m, C1)), CN1)),
                          x),
                      Negate(
                          Dist(
                              Times(b, c, p, Power(Times(f, Plus(m, C1)),
                                  CN1)),
                              Integrate(
                                  Times(
                                      Power(Times(f, x), Plus(m,
                                          C1)),
                                      Power(Plus(a, Times(b, ArcTanh(Times(c, x)))), Subtract(p,
                                          C1)),
                                      Power(Plus(d, Times(e, Sqr(x))), CN1D2)),
                                  x),
                              x)),
                      Dist(Times(Sqr(c), Plus(m, C2), Power(Times(Sqr(f), Plus(m, C1)), CN1)),
                          Integrate(Times(Power(Times(f, x), Plus(m, C2)),
                              Power(Plus(a, Times(b, ArcTanh(Times(c, x)))), p),
                              Power(Plus(d, Times(e, Sqr(x))), CN1D2)), x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), EqQ(Plus(Times(Sqr(c), d),
                      e), C0), GtQ(p,
                          C0),
                      LtQ(m, CN1), NeQ(m, CN2)))),
          IIntegrate(6027,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCoth(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          p_DEFAULT),
                      Power(Times(f_DEFAULT, x_), m_), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))),
                          CN1D2)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(Times(f, x), Plus(m, C1)), Sqrt(Plus(d, Times(e, Sqr(x)))),
                              Power(Plus(a,
                                  Times(b, ArcCoth(Times(c, x)))), p),
                              Power(Times(d, f, Plus(m, C1)), CN1)),
                          x),
                      Negate(
                          Dist(
                              Times(b, c, p, Power(Times(f, Plus(m, C1)),
                                  CN1)),
                              Integrate(
                                  Times(
                                      Power(Times(f, x), Plus(m,
                                          C1)),
                                      Power(Plus(a, Times(b, ArcCoth(Times(c, x)))), Subtract(p,
                                          C1)),
                                      Power(Plus(d, Times(e, Sqr(x))), CN1D2)),
                                  x),
                              x)),
                      Dist(
                          Times(Sqr(c), Plus(m, C2), Power(Times(Sqr(f), Plus(m, C1)),
                              CN1)),
                          Integrate(Times(Power(Times(f, x), Plus(m, C2)),
                              Power(Plus(a, Times(b, ArcCoth(Times(c, x)))),
                                  p),
                              Power(Plus(d, Times(e, Sqr(x))), CN1D2)), x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), EqQ(Plus(Times(Sqr(c), d), e), C0),
                      GtQ(p, C0), LtQ(m, CN1), NeQ(m, CN2)))),
          IIntegrate(6028,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcTanh(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          p_DEFAULT),
                      Power(x_, m_), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Power(e, CN1),
                          Integrate(
                              Times(Power(x, Subtract(m, C2)),
                                  Power(Plus(d, Times(e, Sqr(x))), Plus(q, C1)),
                                  Power(Plus(a, Times(b, ArcTanh(Times(c, x)))), p)),
                              x),
                          x),
                      Dist(
                          Times(d, Power(e,
                              CN1)),
                          Integrate(Times(Power(x, Subtract(m, C2)),
                              Power(Plus(d,
                                  Times(e, Sqr(x))), q),
                              Power(Plus(a, Times(b, ArcTanh(Times(c, x)))), p)), x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(Plus(Times(Sqr(c), d),
                      e), C0), IntegersQ(p,
                          Times(C2, q)),
                      LtQ(q, CN1), IGtQ(m, C1), NeQ(p, CN1)))),
          IIntegrate(6029,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCoth(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          p_DEFAULT),
                      Power(x_, m_), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Power(e, CN1),
                          Integrate(Times(Power(x, Subtract(m, C2)),
                              Power(Plus(d, Times(e, Sqr(x))), Plus(q, C1)),
                              Power(Plus(a, Times(b, ArcCoth(Times(c, x)))), p)), x),
                          x),
                      Dist(Times(d, Power(e, CN1)),
                          Integrate(
                              Times(Power(x, Subtract(m, C2)), Power(Plus(d, Times(e, Sqr(x))), q),
                                  Power(Plus(a, Times(b, ArcCoth(Times(c, x)))), p)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(
                      Plus(Times(Sqr(c), d), e), C0), IntegersQ(p, Times(C2, q)), LtQ(q, CN1),
                      IGtQ(m, C1), NeQ(p, CN1)))),
          IIntegrate(6030,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcTanh(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          p_DEFAULT),
                      Power(x_, m_), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Power(d, CN1),
                          Integrate(
                              Times(Power(x, m), Power(Plus(d, Times(e, Sqr(x))), Plus(q, C1)),
                                  Power(Plus(a, Times(b, ArcTanh(Times(c, x)))), p)),
                              x),
                          x),
                      Dist(Times(e, Power(d, CN1)),
                          Integrate(
                              Times(Power(x, Plus(m, C2)), Power(Plus(d, Times(e, Sqr(x))), q),
                                  Power(Plus(a, Times(b, ArcTanh(Times(c, x)))), p)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(Plus(Times(Sqr(c), d), e), C0), IntegersQ(
                      p, Times(C2, q)), LtQ(q, CN1), ILtQ(m, C0), NeQ(p, CN1)))),
          IIntegrate(6031,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCoth(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          p_DEFAULT),
                      Power(x_, m_), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Power(d, CN1),
                          Integrate(
                              Times(Power(x, m), Power(Plus(d, Times(e, Sqr(x))), Plus(q, C1)),
                                  Power(Plus(a, Times(b, ArcCoth(Times(c, x)))), p)),
                              x),
                          x),
                      Dist(Times(e, Power(d, CN1)),
                          Integrate(
                              Times(
                                  Power(x, Plus(m, C2)), Power(Plus(d, Times(e,
                                      Sqr(x))), q),
                                  Power(Plus(a, Times(b, ArcCoth(Times(c, x)))), p)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(Plus(Times(Sqr(c), d), e), C0), IntegersQ(
                      p, Times(C2, q)), LtQ(q, CN1), ILtQ(m, C0), NeQ(p, CN1)))),
          IIntegrate(6032,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT, Times(ArcTanh(Times(c_DEFAULT, x_)),
                          b_DEFAULT)), p_DEFAULT),
                      Power(x_, m_DEFAULT), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(Times(Power(x, m), Power(Plus(d, Times(e, Sqr(x))), Plus(q, C1)),
                          Power(Plus(a, Times(b, ArcTanh(Times(c, x)))),
                              Plus(p, C1)),
                          Power(Times(b, c, d, Plus(p, C1)), CN1)), x),
                      Dist(
                          Times(c, Plus(m, Times(C2,
                              q), C2), Power(Times(b, Plus(p, C1)),
                                  CN1)),
                          Integrate(
                              Times(
                                  Power(x, Plus(m, C1)), Power(Plus(d, Times(e, Sqr(
                                      x))), q),
                                  Power(Plus(a, Times(b, ArcTanh(Times(c, x)))), Plus(p, C1))),
                              x),
                          x),
                      Negate(
                          Dist(
                              Times(m, Power(Times(b, c, Plus(p, C1)),
                                  CN1)),
                              Integrate(
                                  Times(Power(x, Subtract(m, C1)),
                                      Power(Plus(d, Times(e, Sqr(x))), q),
                                      Power(Plus(a, Times(b, ArcTanh(Times(c, x)))), Plus(p, C1))),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c, d,
                      e), x), EqQ(Plus(Times(Sqr(c), d), e), C0), IntegerQ(m), LtQ(q, CN1),
                      LtQ(p, CN1), NeQ(Plus(m, Times(C2, q), C2), C0)))),
          IIntegrate(6033,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCoth(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          p_DEFAULT),
                      Power(x_, m_DEFAULT), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(Times(Power(x, m), Power(Plus(d, Times(e, Sqr(x))), Plus(q, C1)),
                          Power(Plus(a, Times(b, ArcCoth(Times(c, x)))),
                              Plus(p, C1)),
                          Power(Times(b, c, d, Plus(p, C1)), CN1)), x),
                      Dist(
                          Times(c, Plus(m, Times(C2,
                              q), C2), Power(Times(b, Plus(p, C1)),
                                  CN1)),
                          Integrate(
                              Times(
                                  Power(x, Plus(m, C1)), Power(Plus(d, Times(e, Sqr(
                                      x))), q),
                                  Power(Plus(a, Times(b, ArcCoth(Times(c, x)))), Plus(p, C1))),
                              x),
                          x),
                      Negate(Dist(Times(m, Power(Times(b, c, Plus(p, C1)), CN1)),
                          Integrate(
                              Times(Power(x, Subtract(m, C1)), Power(Plus(d, Times(e, Sqr(x))), q),
                                  Power(Plus(a, Times(b, ArcCoth(Times(c, x)))), Plus(p, C1))),
                              x),
                          x))),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ(Plus(Times(Sqr(c), d), e),
                      C0), IntegerQ(m), LtQ(q, CN1), LtQ(p, CN1),
                      NeQ(Plus(m, Times(C2, q), C2), C0)))),
          IIntegrate(6034,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcTanh(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          p_DEFAULT),
                      Power(x_, m_DEFAULT), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_)),
                  x_Symbol),
              Condition(
                  Dist(Times(Power(d, q), Power(Power(c, Plus(m, C1)), CN1)),
                      Subst(
                          Integrate(
                              Times(
                                  Power(Plus(a, Times(b, x)), p), Power(Sinh(x), m), Power(Power(
                                      Cosh(x), Plus(m, Times(C2, Plus(q, C1)))),
                                      CN1)),
                              x),
                          x, ArcTanh(Times(c, x))),
                      x),
                  And(FreeQ(List(a, b, c, d, e, p), x), EqQ(Plus(Times(Sqr(c), d), e), C0),
                      IGtQ(m, C0), ILtQ(Plus(m, Times(C2, q),
                          C1), C0),
                      Or(IntegerQ(q), GtQ(d, C0))))),
          IIntegrate(6035,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcTanh(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          p_DEFAULT),
                      Power(x_, m_DEFAULT), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(d, Plus(q, C1D2)), Sqrt(Subtract(C1,
                              Times(Sqr(c), Sqr(x)))),
                          Power(Plus(d, Times(e, Sqr(x))), CN1D2)),
                      Integrate(
                          Times(
                              Power(x, m), Power(Subtract(C1,
                                  Times(Sqr(c), Sqr(x))), q),
                              Power(Plus(a, Times(b, ArcTanh(Times(c, x)))), p)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, p), x), EqQ(Plus(Times(Sqr(c), d), e), C0),
                      IGtQ(m, C0), ILtQ(Plus(m, Times(C2, q),
                          C1), C0),
                      Not(Or(IntegerQ(q), GtQ(d, C0)))))),
          IIntegrate(6036,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT, Times(ArcCoth(Times(c_DEFAULT, x_)),
                          b_DEFAULT)), p_DEFAULT),
                      Power(x_, m_DEFAULT), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_)),
                  x_Symbol),
              Condition(
                  Negate(
                      Dist(
                          Times(Power(Negate(d), q), Power(Power(c, Plus(m, C1)),
                              CN1)),
                          Subst(
                              Integrate(
                                  Times(Power(Plus(a, Times(b, x)), p), Power(Cosh(x), m),
                                      Power(Power(Sinh(x), Plus(m, Times(C2, Plus(q, C1)))), CN1)),
                                  x),
                              x, ArcCoth(Times(c, x))),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, p), x), EqQ(Plus(Times(Sqr(c), d),
                      e), C0), IGtQ(m,
                          C0),
                      ILtQ(Plus(m, Times(C2, q), C1), C0), IntegerQ(q)))),
          IIntegrate(6037,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCoth(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          p_DEFAULT),
                      Power(x_, m_DEFAULT), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_)),
                  x_Symbol),
              Condition(
                  Negate(
                      Dist(
                          Times(
                              Power(Negate(d), Plus(q,
                                  C1D2)),
                              x,
                              Sqrt(
                                  Times(
                                      Subtract(Times(Sqr(c),
                                          Sqr(x)), C1),
                                      Power(Times(Sqr(c), Sqr(x)), CN1))),
                              Power(Times(Power(c, m), Sqrt(Plus(d, Times(e, Sqr(x))))), CN1)),
                          Subst(
                              Integrate(
                                  Times(Power(Plus(a, Times(b, x)), p), Power(Cosh(x), m),
                                      Power(Power(Sinh(x), Plus(m, Times(C2, Plus(q, C1)))), CN1)),
                                  x),
                              x, ArcCoth(Times(c, x))),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, p), x), EqQ(Plus(Times(Sqr(c), d),
                      e), C0), IGtQ(m,
                          C0),
                      ILtQ(Plus(m, Times(C2, q), C1), C0), Not(IntegerQ(q))))),
          IIntegrate(6038,
              Integrate(
                  Times(Plus(a_DEFAULT, Times(ArcTanh(Times(c_DEFAULT, x_)), b_DEFAULT)), x_, Power(
                      Plus(d_DEFAULT, Times(e_DEFAULT, Sqr(x_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Power(Plus(d, Times(e, Sqr(x))), Plus(q, C1)),
                              Plus(a, Times(b,
                                  ArcTanh(Times(c, x)))),
                              Power(Times(C2, e, Plus(q, C1)), CN1)),
                          x),
                      Dist(Times(b, c, Power(Times(C2, e, Plus(q, C1)), CN1)),
                          Integrate(
                              Times(Power(Plus(d, Times(e, Sqr(x))), Plus(q, C1)),
                                  Power(Subtract(C1, Times(Sqr(c), Sqr(x))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, q), x), NeQ(q, CN1)))),
          IIntegrate(6039,
              Integrate(
                  Times(Plus(a_DEFAULT, Times(ArcCoth(Times(c_DEFAULT, x_)), b_DEFAULT)), x_, Power(
                      Plus(d_DEFAULT, Times(e_DEFAULT, Sqr(x_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Power(Plus(d, Times(e, Sqr(x))), Plus(q, C1)),
                              Plus(a, Times(b,
                                  ArcCoth(Times(c, x)))),
                              Power(Times(C2, e, Plus(q, C1)), CN1)),
                          x),
                      Dist(
                          Times(b, c, Power(Times(C2, e, Plus(q, C1)),
                              CN1)),
                          Integrate(
                              Times(
                                  Power(Plus(d, Times(e, Sqr(x))), Plus(q,
                                      C1)),
                                  Power(Subtract(C1, Times(Sqr(c), Sqr(x))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, q), x), NeQ(q, CN1)))),
          IIntegrate(6040,
              Integrate(
                  Times(Plus(a_DEFAULT, Times(ArcTanh(Times(c_DEFAULT, x_)), b_DEFAULT)),
                      Power(Times(f_DEFAULT, x_), m_DEFAULT),
                      Power(Plus(d_DEFAULT, Times(e_DEFAULT, Sqr(x_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(u,
                          IntHide(Times(Power(Times(f, x), m), Power(Plus(d, Times(e, Sqr(x))), q)),
                              x))),
                      Subtract(Dist(Plus(a, Times(b, ArcTanh(Times(c, x)))), u, x), Dist(
                          Times(b, c),
                          Integrate(SimplifyIntegrand(
                              Times(u, Power(Subtract(C1, Times(Sqr(c), Sqr(x))), CN1)), x), x),
                          x))),
                  And(FreeQ(List(a, b, c, d, e, f, m, q), x), Or(
                      And(IGtQ(q, C0),
                          Not(And(ILtQ(Times(C1D2, Subtract(m, C1)), C0),
                              GtQ(Plus(m, Times(C2, q), C3), C0)))),
                      And(IGtQ(Times(C1D2, Plus(m, C1)), C0),
                          Not(And(ILtQ(q, C0), GtQ(Plus(m, Times(C2, q), C3), C0)))),
                      And(ILtQ(Times(C1D2, Plus(m, Times(C2, q), C1)), C0),
                          Not(ILtQ(Times(C1D2, Subtract(m, C1)), C0))))))));
}
