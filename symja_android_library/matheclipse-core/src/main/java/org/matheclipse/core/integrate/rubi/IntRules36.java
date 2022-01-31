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
class IntRules36 {
  public static IAST RULES =
      List(
          IIntegrate(721,
              Integrate(
                  Times(
                      Power(Plus(d_, Times(e_DEFAULT, x_)), m_), Power(
                          Plus(a_, Times(c_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Negate(
                          Simp(
                              Times(Power(Plus(d, Times(e, x)), Plus(m, C1)),
                                  Plus(Times(CN2, a, e), Times(C2, c, d, x)),
                                  Power(Plus(a, Times(c, Sqr(x))), p),
                                  Power(Times(C2, Plus(m, C1),
                                      Plus(Times(c, Sqr(d)), Times(a, Sqr(e)))), CN1)),
                              x)),
                      Dist(
                          Times(C4, a, c, p,
                              Power(
                                  Times(C2, Plus(m, C1),
                                      Plus(Times(c, Sqr(d)), Times(a, Sqr(e)))),
                                  CN1)),
                          Integrate(
                              Times(
                                  Power(Plus(d, Times(e, x)), Plus(m, C2)), Power(
                                      Plus(a, Times(c, Sqr(x))), Subtract(p, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, c, d, e), x), NeQ(Plus(Times(c, Sqr(
                      d)), Times(a,
                          Sqr(e))),
                      C0), EqQ(Plus(m, Times(C2, p), C2), C0), GtQ(p, C0)))),
          IIntegrate(722,
              Integrate(
                  Times(
                      Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), m_),
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Power(Plus(d, Times(e, x)), Subtract(m, C1)), Plus(Times(d, b),
                              Times(CN1, C2, a, e),
                              Times(Subtract(Times(C2, c, d), Times(b, e)), x)),
                              Power(Plus(a, Times(b, x), Times(c, Sqr(x))), Plus(p, C1)),
                              Power(Times(Plus(p, C1), Subtract(Sqr(b), Times(C4, a, c))), CN1)),
                          x),
                      Dist(
                          Times(C2, Plus(Times(C2, p), C3),
                              Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a, Sqr(e))),
                              Power(Times(Plus(p, C1), Subtract(Sqr(b), Times(C4, a, c))), CN1)),
                          Integrate(
                              Times(Power(Plus(d, Times(e, x)), Subtract(m, C2)),
                                  Power(Plus(a, Times(b, x), Times(c, Sqr(x))), Plus(p, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      NeQ(Plus(Times(c, Sqr(
                          d)), Times(CN1, b, d,
                              e),
                          Times(a, Sqr(e))), C0),
                      NeQ(Subtract(Times(C2, c, d), Times(b, e)),
                          C0),
                      EqQ(Plus(m, Times(C2, p), C2), C0), LtQ(p, CN1)))),
          IIntegrate(723,
              Integrate(
                  Times(
                      Power(Plus(d_, Times(e_DEFAULT, x_)), m_), Power(
                          Plus(a_, Times(c_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(Plus(d, Times(e, x)), Subtract(m, C1)),
                              Subtract(Times(a, e), Times(c, d, x)),
                              Power(Plus(a, Times(c, Sqr(x))), Plus(p, C1)),
                              Power(Times(C2, a, c, Plus(p, C1)), CN1)),
                          x),
                      Dist(
                          Times(
                              Plus(Times(C2, p), C3), Plus(Times(c, Sqr(d)), Times(a, Sqr(e))),
                              Power(Times(C2, a, c, Plus(p, C1)), CN1)),
                          Integrate(
                              Times(Power(Plus(d, Times(e, x)), Subtract(m, C2)),
                                  Power(Plus(a, Times(c, Sqr(x))), Plus(p, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, c, d, e), x), NeQ(Plus(Times(c, Sqr(d)),
                      Times(a, Sqr(e))), C0), EqQ(Plus(m, Times(C2, p), C2),
                          C0),
                      LtQ(p, CN1)))),
          IIntegrate(724,
              Integrate(Times(
                  Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), CN1), Power(Plus(a_DEFAULT,
                      Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))),
                      CN1D2)),
                  x_Symbol),
              Condition(
                  Dist(CN2,
                      Subst(
                          Integrate(Power(Subtract(Plus(Times(C4, c, Sqr(d)),
                              Times(CN1, C4, b, d, e), Times(C4, a, Sqr(e))), Sqr(x)), CN1), x),
                          x,
                          Times(
                              Subtract(Subtract(Times(C2, a, e), Times(b, d)),
                                  Times(Subtract(Times(C2, c, d), Times(b, e)), x)),
                              Power(Plus(a, Times(b, x), Times(c, Sqr(x))), CN1D2))),
                      x),
                  And(FreeQ(List(a, b, c, d, e), x), NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      NeQ(Subtract(Times(C2, c, d), Times(b, e)), C0)))),
          IIntegrate(725,
              Integrate(Times(Power(Plus(d_, Times(e_DEFAULT, x_)), CN1),
                  Power(Plus(a_, Times(c_DEFAULT, Sqr(x_))), CN1D2)), x_Symbol),
              Condition(
                  Negate(
                      Subst(
                          Integrate(Power(
                              Subtract(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))), Sqr(x)), CN1), x),
                          x,
                          Times(Subtract(Times(a, e), Times(c, d, x)),
                              Power(Plus(a, Times(c, Sqr(x))), CN1D2)))),
                  FreeQ(List(a, c, d, e), x))),
          IIntegrate(726,
              Integrate(
                  Times(Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), m_),
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(Negate(
                  Simp(
                      Times(
                          Plus(
                              b, Negate(Rt(Subtract(Sqr(b),
                                  Times(C4, a, c)), C2)),
                              Times(C2, c, x)),
                          Power(Plus(d, Times(e, x)), Plus(m, C1)),
                          Power(Plus(a, Times(b, x),
                              Times(c, Sqr(x))), p),
                          Hypergeometric2F1(
                              Plus(m, C1), Negate(p), Plus(m, C2), Times(CN4, c,
                                  Rt(Subtract(Sqr(b), Times(C4, a,
                                      c)), C2),
                                  Plus(d, Times(e, x)),
                                  Power(
                                      Times(
                                          Subtract(Subtract(Times(C2, c, d), Times(b, e)),
                                              Times(e, Rt(Subtract(Sqr(b), Times(C4, a, c)), C2))),
                                          Plus(b, Negate(Rt(Subtract(Sqr(b), Times(C4, a, c)), C2)),
                                              Times(C2, c, x))),
                                      CN1))),
                          Power(
                              Times(Plus(m, C1),
                                  Plus(Times(C2, c,
                                      d), Times(CN1, b, e),
                                      Times(e, Rt(Subtract(Sqr(b), Times(C4, a, c)), C2))),
                                  Power(
                                      Times(
                                          Plus(Times(C2, c, d), Times(CN1, b,
                                              e),
                                              Times(e, Rt(Subtract(Sqr(b), Times(C4, a, c)), C2))),
                                          Plus(
                                              b, Rt(Subtract(Sqr(b), Times(C4, a, c)),
                                                  C2),
                                              Times(C2, c, x)),
                                          Power(Times(
                                              Subtract(Subtract(Times(C2, c, d), Times(b, e)),
                                                  Times(e,
                                                      Rt(Subtract(Sqr(b), Times(C4, a, c)), C2))),
                                              Plus(b,
                                                  Negate(Rt(Subtract(Sqr(b), Times(C4, a, c)), C2)),
                                                  Times(C2, c, x))),
                                              CN1)),
                                      p)),
                              CN1)),
                      x)),
                  And(FreeQ(List(a, b, c, d, e, m, p), x),
                      NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      NeQ(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a, Sqr(e))), C0),
                      NeQ(Subtract(Times(C2, c, d),
                          Times(b, e)), C0),
                      Not(IntegerQ(p)), EqQ(Plus(m, Times(C2, p), C2), C0)))),
          IIntegrate(727,
              Integrate(
                  Times(
                      Power(Plus(d_,
                          Times(e_DEFAULT, x_)), m_),
                      Power(Plus(a_, Times(c_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(Subtract(Rt(Times(CN1, a, c), C2), Times(c, x)),
                          Power(Plus(d, Times(e,
                              x)), Plus(m,
                                  C1)),
                          Power(Plus(a,
                              Times(c, Sqr(x))), p),
                          Hypergeometric2F1(Plus(m, C1), Negate(p), Plus(m, C2),
                              Times(C2, c, Rt(Times(CN1, a, c), C2), Plus(d, Times(e, x)),
                                  Power(
                                      Times(
                                          Subtract(Times(c, d), Times(e, Rt(Times(CN1, a, c),
                                              C2))),
                                          Subtract(Rt(Times(CN1, a, c), C2), Times(c, x))),
                                      CN1))),
                          Power(
                              Times(Plus(m, C1),
                                  Plus(Times(c, d), Times(e, Rt(Times(CN1, a, c), C2))),
                                  Power(Times(Plus(Times(c, d), Times(e, Rt(Times(CN1, a, c), C2))),
                                      Plus(Rt(Times(CN1, a, c), C2), Times(c, x)),
                                      Power(
                                          Times(
                                              Subtract(Times(c, d),
                                                  Times(e, Rt(Times(CN1, a, c), C2))),
                                              Plus(Negate(Rt(Times(CN1, a, c), C2)), Times(c, x))),
                                          CN1)),
                                      p)),
                              CN1)),
                      x),
                  And(FreeQ(List(a, c, d, e, m, p), x),
                      NeQ(Plus(Times(c, Sqr(d)), Times(a,
                          Sqr(e))), C0),
                      Not(IntegerQ(p)), EqQ(Plus(m, Times(C2, p), C2), C0)))),
          IIntegrate(728,
              Integrate(
                  Times(
                      Power(Plus(d_DEFAULT,
                          Times(e_DEFAULT, x_)), m_),
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(Plus(d, Times(e, x)), m), Plus(b, Times(C2, c, x)),
                              Power(Plus(a, Times(b, x), Times(c, Sqr(x))), Plus(p, C1)),
                              Power(Times(Plus(p, C1), Subtract(Sqr(b), Times(C4, a, c))), CN1)),
                          x),
                      Dist(
                          Times(
                              m, Subtract(Times(C2, c, d), Times(b, e)),
                              Power(Times(Plus(p, C1), Subtract(Sqr(b), Times(C4, a, c))), CN1)),
                          Integrate(
                              Times(
                                  Power(Plus(d, Times(e, x)), Subtract(m, C1)),
                                  Power(Plus(a, Times(b, x), Times(c, Sqr(x))), Plus(p, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, m, p), x),
                      NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      NeQ(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a, Sqr(e))), C0),
                      NeQ(Subtract(Times(C2, c, d), Times(b,
                          e)), C0),
                      EqQ(Plus(m, Times(C2, p), C3), C0), LtQ(p, CN1)))),
          IIntegrate(729,
              Integrate(
                  Times(Power(Plus(d_, Times(e_DEFAULT, x_)), m_),
                      Power(Plus(a_, Times(c_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Negate(
                          Simp(Times(Power(Plus(d, Times(e, x)), m), C2, c, x,
                              Power(Plus(a, Times(c, Sqr(x))), Plus(p, C1)), Power(
                                  Times(C4, a, c, Plus(p, C1)), CN1)),
                              x)),
                      Dist(Times(m, C2, c, d, Power(Times(C4, a, c, Plus(p, C1)), CN1)),
                          Integrate(
                              Times(Power(Plus(d, Times(e, x)), Subtract(m, C1)),
                                  Power(Plus(a, Times(c, Sqr(x))), Plus(p, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, c, d, e, m, p), x),
                      NeQ(Plus(Times(c, Sqr(
                          d)), Times(a,
                              Sqr(e))),
                          C0),
                      EqQ(Plus(m, Times(C2, p), C3), C0), LtQ(p, CN1)))),
          IIntegrate(730,
              Integrate(
                  Times(
                      Power(Plus(d_DEFAULT,
                          Times(e_DEFAULT, x_)), m_),
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(Times(e, Power(Plus(d, Times(e, x)), Plus(m, C1)),
                          Power(Plus(a, Times(b, x), Times(c, Sqr(x))), Plus(p, C1)),
                          Power(
                              Times(Plus(m, C1),
                                  Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a, Sqr(e)))),
                              CN1)),
                          x),
                      Dist(
                          Times(Subtract(Times(C2, c, d), Times(b, e)),
                              Power(Times(C2,
                                  Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a, Sqr(e)))),
                                  CN1)),
                          Integrate(
                              Times(Power(Plus(d, Times(e, x)), Plus(m, C1)),
                                  Power(Plus(a, Times(b, x), Times(c, Sqr(x))), p)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, m, p), x),
                      NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      NeQ(Plus(Times(c, Sqr(
                          d)), Times(CN1, b, d,
                              e),
                          Times(a, Sqr(e))), C0),
                      NeQ(Subtract(Times(C2, c, d),
                          Times(b, e)), C0),
                      EqQ(Plus(m, Times(C2, p), C3), C0)))),
          IIntegrate(731,
              Integrate(
                  Times(
                      Power(Plus(d_,
                          Times(e_DEFAULT, x_)), m_),
                      Power(Plus(a_, Times(c_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(e, Power(Plus(d, Times(e, x)), Plus(m, C1)),
                              Power(Plus(a, Times(c, Sqr(x))), Plus(p, C1)),
                              Power(Times(Plus(m, C1), Plus(Times(c, Sqr(d)), Times(a, Sqr(e)))),
                                  CN1)),
                          x),
                      Dist(Times(c, d, Power(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))), CN1)),
                          Integrate(
                              Times(Power(Plus(d, Times(e, x)), Plus(m, C1)),
                                  Power(Plus(a, Times(c, Sqr(x))), p)),
                              x),
                          x)),
                  And(FreeQ(List(a, c, d, e, m, p), x),
                      NeQ(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))), C0),
                      EqQ(Plus(m, Times(C2, p), C3), C0)))),
          IIntegrate(732,
              Integrate(
                  Times(
                      Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), m_),
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Power(Plus(d, Times(e, x)), Plus(m, C1)),
                              Power(Plus(a, Times(b, x), Times(c, Sqr(x))),
                                  p),
                              Power(Times(e, Plus(m, C1)), CN1)),
                          x),
                      Dist(
                          Times(p, Power(Times(e, Plus(m, C1)),
                              CN1)),
                          Integrate(
                              Times(Power(Plus(d, Times(e, x)), Plus(m, C1)),
                                  Plus(b, Times(C2, c, x)),
                                  Power(Plus(a, Times(b, x), Times(c, Sqr(x))), Subtract(p, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, m), x), NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      NeQ(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a, Sqr(e))), C0),
                      NeQ(Subtract(Times(C2, c, d), Times(b, e)), C0), GtQ(p, C0),
                      Or(IntegerQ(p), LtQ(m, CN1)), NeQ(m, CN1),
                      Not(ILtQ(Plus(m, Times(C2, p), C1),
                          C0)),
                      IntQuadraticQ(a, b, c, d, e, m, p, x)))),
          IIntegrate(733,
              Integrate(
                  Times(Power(Plus(d_, Times(e_DEFAULT, x_)), m_),
                      Power(Plus(a_, Times(c_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Power(Plus(d, Times(e, x)), Plus(m, C1)),
                              Power(Plus(a, Times(c, Sqr(x))), p), Power(Times(e, Plus(m, C1)),
                                  CN1)),
                          x),
                      Dist(Times(C2, c, p, Power(Times(e, Plus(m, C1)), CN1)),
                          Integrate(
                              Times(
                                  x, Power(Plus(d, Times(e, x)), Plus(m, C1)), Power(
                                      Plus(a, Times(c, Sqr(x))), Subtract(p, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, c, d, e, m), x),
                      NeQ(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))), C0), GtQ(p, C0),
                      Or(IntegerQ(p), LtQ(m, CN1)), NeQ(m, CN1),
                      Not(ILtQ(Plus(m, Times(C2, p), C1), C0)), IntQuadraticQ(a, C0, c, d, e, m, p,
                          x)))),
          IIntegrate(734,
              Integrate(
                  Times(
                      Power(Plus(d_DEFAULT,
                          Times(e_DEFAULT, x_)), m_),
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Power(Plus(d, Times(e, x)), Plus(m, C1)),
                              Power(Plus(a, Times(b, x), Times(c, Sqr(x))),
                                  p),
                              Power(Times(e, Plus(m, Times(C2, p), C1)), CN1)),
                          x),
                      Dist(Times(p, Power(Times(e, Plus(m, Times(C2, p), C1)), CN1)),
                          Integrate(
                              Times(Power(Plus(d, Times(e, x)), m),
                                  Simp(Plus(Times(b, d), Times(CN1, C2, a, e),
                                      Times(Subtract(Times(C2, c, d), Times(b, e)), x)), x),
                                  Power(Plus(a, Times(b, x), Times(c, Sqr(x))), Subtract(p, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, m), x), NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      NeQ(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a, Sqr(e))), C0),
                      NeQ(Subtract(Times(C2, c, d), Times(b, e)), C0), GtQ(p, C0),
                      NeQ(Plus(m, Times(C2, p), C1), C0), Or(Not(RationalQ(m)), LtQ(m, C1)),
                      Not(ILtQ(Plus(m, Times(C2, p)), C0)),
                      IntQuadraticQ(a, b, c, d, e, m, p, x)))),
          IIntegrate(735,
              Integrate(
                  Times(
                      Power(Plus(d_,
                          Times(e_DEFAULT, x_)), m_),
                      Power(Plus(a_, Times(c_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(Plus(d, Times(e, x)), Plus(m, C1)),
                              Power(Plus(a, Times(c, Sqr(x))),
                                  p),
                              Power(Times(e, Plus(m, Times(C2, p), C1)), CN1)),
                          x),
                      Dist(Times(C2, p, Power(Times(e, Plus(m, Times(C2, p), C1)), CN1)),
                          Integrate(
                              Times(Power(Plus(d, Times(e, x)), m),
                                  Simp(Subtract(Times(a, e), Times(c, d, x)), x),
                                  Power(Plus(a, Times(c, Sqr(x))), Subtract(p, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, c, d, e, m), x),
                      NeQ(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))), C0), GtQ(p, C0),
                      NeQ(Plus(m, Times(C2, p), C1), C0), Or(Not(RationalQ(m)), LtQ(m, C1)),
                      Not(ILtQ(Plus(m, Times(C2, p)), C0)),
                      IntQuadraticQ(a, C0, c, d, e, m, p, x)))),
          IIntegrate(736, Integrate(
              Times(Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), m_), Power(Plus(a_DEFAULT,
                  Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), p_)),
              x_Symbol),
              Condition(
                  Subtract(
                      Simp(Times(Power(Plus(d, Times(e, x)), m), Plus(b, Times(C2, c, x)),
                          Power(Plus(a, Times(b, x), Times(c, Sqr(x))), Plus(p, C1)),
                          Power(Times(Plus(p, C1), Subtract(Sqr(b), Times(C4, a, c))), CN1)), x),
                      Dist(Power(Times(Plus(p, C1), Subtract(Sqr(b), Times(C4, a, c))), CN1),
                          Integrate(Times(Power(Plus(d, Times(e, x)), Subtract(m, C1)),
                              Plus(Times(b, e, m), Times(C2, c, d, Plus(Times(C2, p), C3)),
                                  Times(C2, c, e, Plus(m, Times(C2, p), C3), x)),
                              Power(Plus(a, Times(b, x), Times(c, Sqr(x))), Plus(p, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      NeQ(Plus(Times(c, Sqr(
                          d)), Times(CN1, b, d,
                              e),
                          Times(a, Sqr(e))), C0),
                      NeQ(Subtract(Times(C2, c, d), Times(b, e)), C0), LtQ(p, CN1), GtQ(m, C0),
                      Or(LtQ(m, C1), And(ILtQ(Plus(m, Times(C2, p), C3), C0), NeQ(m, C2))),
                      IntQuadraticQ(a, b, c, d, e, m, p, x)))),
          IIntegrate(737,
              Integrate(
                  Times(
                      Power(Plus(d_,
                          Times(e_DEFAULT, x_)), m_),
                      Power(Plus(a_, Times(c_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(Simp(Times(x, Power(Plus(d, Times(e, x)), m),
                          Power(Plus(a, Times(c, Sqr(x))), Plus(p, C1)),
                          Power(Times(C2, a, Plus(p, C1)), CN1)), x)),
                      Dist(Power(Times(C2, a, Plus(p, C1)), CN1),
                          Integrate(Times(Power(Plus(d, Times(e, x)), Subtract(m, C1)),
                              Plus(Times(d, Plus(Times(C2, p), C3)),
                                  Times(e, Plus(m, Times(C2, p), C3), x)),
                              Power(Plus(a, Times(c, Sqr(x))), Plus(p, C1))), x),
                          x)),
                  And(FreeQ(List(a, c, d, e), x), NeQ(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))), C0),
                      LtQ(p, CN1), GtQ(m, C0),
                      Or(LtQ(m, C1), And(ILtQ(Plus(m, Times(C2, p), C3), C0), NeQ(m, C2))),
                      IntQuadraticQ(a, C0, c, d, e, m, p, x)))),
          IIntegrate(738,
              Integrate(
                  Times(
                      Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), m_), Power(Plus(a_DEFAULT,
                          Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))),
                          p_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(Times(Power(Plus(d, Times(e, x)), Subtract(m, C1)),
                          Plus(Times(d, b), Times(CN1, C2, a, e),
                              Times(Subtract(Times(C2, c, d), Times(b, e)), x)),
                          Power(Plus(a, Times(b, x), Times(c, Sqr(x))), Plus(p, C1)), Power(Times(
                              Plus(p, C1), Subtract(Sqr(b), Times(C4, a, c))), CN1)),
                          x),
                      Dist(
                          Power(Times(Plus(p, C1),
                              Subtract(Sqr(b), Times(C4, a, c))), CN1),
                          Integrate(
                              Times(
                                  Power(Plus(d, Times(e, x)), Subtract(m,
                                      C2)),
                                  Simp(Plus(
                                      Times(e,
                                          Plus(Times(C2, a, e, Subtract(m, C1)),
                                              Times(b, d, Plus(Times(C2, p), Negate(m), C4)))),
                                      Times(CN1, C2, c, Sqr(d), Plus(Times(C2, p), C3)),
                                      Times(e, Subtract(Times(b, e), Times(C2, d, c)),
                                          Plus(m, Times(C2, p), C2), x)),
                                      x),
                                  Power(Plus(a, Times(b, x), Times(c, Sqr(x))), Plus(p, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      NeQ(Plus(Times(c, Sqr(
                          d)), Times(CN1, b, d,
                              e),
                          Times(a, Sqr(e))), C0),
                      NeQ(Subtract(Times(C2, c, d), Times(b, e)), C0), LtQ(p, CN1), GtQ(m, C1),
                      IntQuadraticQ(a, b, c, d, e, m, p, x)))),
          IIntegrate(739,
              Integrate(
                  Times(
                      Power(Plus(d_,
                          Times(e_DEFAULT, x_)), m_),
                      Power(Plus(a_, Times(c_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(Plus(d, Times(e, x)), Subtract(m, C1)),
                              Subtract(Times(a, e), Times(c, d, x)),
                              Power(Plus(a, Times(c,
                                  Sqr(x))), Plus(p,
                                      C1)),
                              Power(Times(C2, a, c, Plus(p, C1)), CN1)),
                          x),
                      Dist(Power(Times(Plus(p, C1), CN2, a, c), CN1),
                          Integrate(Times(Power(Plus(d, Times(e, x)), Subtract(m, C2)),
                              Simp(Subtract(
                                  Subtract(Times(a, Sqr(e), Subtract(m, C1)),
                                      Times(c, Sqr(d), Plus(Times(C2, p), C3))),
                                  Times(d, c, e, Plus(m, Times(C2, p), C2), x)), x),
                              Power(Plus(a, Times(c, Sqr(x))), Plus(p, C1))), x),
                          x)),
                  And(FreeQ(List(a, c, d, e), x), NeQ(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))),
                      C0), LtQ(p, CN1), GtQ(m, C1),
                      IntQuadraticQ(a, C0, c, d, e, m, p, x)))),
          IIntegrate(740,
              Integrate(
                  Times(Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), m_),
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(Plus(d, Times(e, x)), Plus(m, C1)), Plus(Times(b, c, d),
                              Times(CN1, Sqr(b), e), Times(C2, a, c, e),
                              Times(c, Subtract(Times(C2, c, d), Times(b, e)), x)),
                              Power(Plus(a, Times(b, x), Times(c, Sqr(x))), Plus(p, C1)),
                              Power(Times(Plus(p, C1), Subtract(Sqr(b), Times(C4, a, c)),
                                  Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a, Sqr(e)))),
                                  CN1)),
                          x),
                      Dist(Power(Times(
                          Plus(p, C1), Subtract(Sqr(b), Times(C4, a, c)), Plus(Times(c, Sqr(d)),
                              Times(CN1, b, d, e), Times(a, Sqr(e)))),
                          CN1),
                          Integrate(
                              Times(
                                  Power(Plus(d, Times(e, x)), m),
                                  Simp(Subtract(
                                      Subtract(
                                          Subtract(
                                              Plus(
                                                  Times(b, c, d, e,
                                                      Plus(Times(C2, p), Negate(m), C2)),
                                                  Times(Sqr(b), Sqr(e), Plus(m, p, C2))),
                                              Times(C2, Sqr(c), Sqr(d), Plus(Times(C2, p), C3))),
                                          Times(C2, a, c, Sqr(e), Plus(m, Times(C2, p), C3))),
                                      Times(c, e, Subtract(Times(C2, c, d), Times(b, e)),
                                          Plus(m, Times(C2, p), C4), x)),
                                      x),
                                  Power(Plus(a, Times(b, x), Times(c, Sqr(x))), Plus(p, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, m), x), NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      NeQ(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a, Sqr(e))), C0),
                      NeQ(Subtract(Times(C2, c, d), Times(b, e)), C0), LtQ(p, CN1),
                      IntQuadraticQ(a, b, c, d, e, m, p, x)))));
}
