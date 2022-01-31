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
class IntRules255 {
  public static IAST RULES =
      List(
          IIntegrate(5101,
              Integrate(
                  Times(Exp(Times(ArcCot(Times(a_DEFAULT, x_)), n_)),
                      Power(x_, m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Negate(
                      Subst(
                          Integrate(
                              Times(
                                  Power(
                                      Subtract(C1, Times(CI, x, Power(a, CN1))),
                                      Times(C1D2, Plus(Times(CI, n), C1))),
                                  Power(Times(Power(x, Plus(m, C2)),
                                      Power(Plus(C1, Times(CI, x, Power(a, CN1))),
                                          Times(C1D2, Subtract(Times(CI, n), C1))),
                                      Sqrt(Plus(C1, Times(Sqr(x), Power(a, CN2))))), CN1)),
                              x),
                          x, Power(x, CN1))),
                  And(FreeQ(a, x), IntegerQ(Times(C1D2,
                      Subtract(Times(CI, n), C1))), IntegerQ(
                          m)))),
          IIntegrate(
              5102, Integrate(Exp(
                  Times(ArcCot(Times(a_DEFAULT, x_)), n_DEFAULT)), x_Symbol),
              Condition(
                  Negate(
                      Subst(
                          Integrate(Times(
                              Power(Subtract(C1, Times(CI, x, Power(a, CN1))), Times(C1D2, CI, n)),
                              Power(
                                  Times(Sqr(x),
                                      Power(Plus(C1, Times(CI, x, Power(a, CN1))),
                                          Times(C1D2, CI, n))),
                                  CN1)),
                              x),
                          x, Power(x, CN1))),
                  And(FreeQ(List(a, n), x), Not(IntegerQ(Times(CI, n)))))),
          IIntegrate(5103,
              Integrate(
                  Times(Exp(Times(ArcCot(Times(a_DEFAULT, x_)), n_DEFAULT)),
                      Power(x_, m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Negate(
                      Subst(
                          Integrate(
                              Times(
                                  Power(Subtract(C1, Times(CI, x, Power(a, CN1))), Times(C1D2, n)),
                                  Power(Times(Power(x, Plus(m, C2)),
                                      Power(Plus(C1, Times(CI, x, Power(a, CN1))), Times(C1D2, n))),
                                      CN1)),
                              x),
                          x, Power(x, CN1))),
                  And(FreeQ(List(a, n), x), Not(IntegerQ(Times(CI, n))), IntegerQ(m)))),
          IIntegrate(5104,
              Integrate(Times(Exp(Times(ArcCot(Times(a_DEFAULT, x_)), n_)), Power(x_, m_)),
                  x_Symbol),
              Condition(Negate(Dist(Times(Power(x, m), Power(Power(x, CN1), m)), Subst(
                  Integrate(
                      Times(
                          Power(Subtract(C1, Times(CI, x, Power(a, CN1))),
                              Times(C1D2, Plus(Times(CI, n), C1))),
                          Power(
                              Times(Power(x, Plus(m, C2)),
                                  Power(Plus(C1, Times(CI, x, Power(a, CN1))), Times(C1D2,
                                      Subtract(Times(CI, n), C1))),
                                  Sqrt(Plus(C1, Times(Sqr(x), Power(a, CN2))))),
                              CN1)),
                      x),
                  x, Power(x, CN1)), x)), And(
                      FreeQ(List(a, m), x), IntegerQ(Times(C1D2,
                          Subtract(Times(CI, n), C1))),
                      Not(IntegerQ(m))))),
          IIntegrate(5105,
              Integrate(
                  Times(Exp(Times(ArcCot(Times(a_DEFAULT, x_)), n_DEFAULT)),
                      Power(x_, m_)),
                  x_Symbol),
              Condition(
                  Negate(
                      Subst(
                          Integrate(
                              Times(
                                  Power(Subtract(C1, Times(CI, x, Power(a, CN1))), Times(C1D2, n)),
                                  Power(
                                      Times(Power(x, Plus(m, C2)),
                                          Power(Plus(C1, Times(CI, x, Power(a, CN1))),
                                              Times(C1D2, n))),
                                      CN1)),
                              x),
                          x, Power(x, CN1))),
                  And(FreeQ(List(a, m,
                      n), x), Not(
                          IntegerQ(Times(C1D2, CI, n))),
                      Not(IntegerQ(m))))),
          IIntegrate(5106,
              Integrate(
                  Times(
                      Exp(Times(ArcCot(Times(a_DEFAULT, x_)), n_DEFAULT)), u_DEFAULT, Power(
                          Plus(c_, Times(d_DEFAULT, x_)), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(d, p),
                      Integrate(
                          Times(u, Power(x, p),
                              Power(Plus(C1, Times(c, Power(Times(d, x), CN1))),
                                  p),
                              Exp(Times(n, ArcCot(Times(a, x))))),
                          x),
                      x),
                  And(FreeQ(List(a, c, d, n), x), EqQ(Plus(Times(Sqr(a), Sqr(
                      c)), Sqr(
                          d)),
                      C0), Not(IntegerQ(Times(C1D2, CI, n))), IntegerQ(p)))),
          IIntegrate(5107,
              Integrate(
                  Times(
                      Exp(Times(ArcCot(Times(a_DEFAULT, x_)),
                          n_DEFAULT)),
                      u_DEFAULT, Power(Plus(c_, Times(d_DEFAULT, x_)), p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(Plus(c, Times(d, x)), p),
                          Power(
                              Times(Power(x, p),
                                  Power(Plus(C1, Times(c, Power(Times(d, x), CN1))), p)),
                              CN1)),
                      Integrate(
                          Times(u, Power(x, p),
                              Power(Plus(C1, Times(c, Power(Times(d, x), CN1))),
                                  p),
                              Exp(Times(n, ArcCot(Times(a, x))))),
                          x),
                      x),
                  And(FreeQ(List(a, c, d, n, p), x), EqQ(Plus(Times(Sqr(a),
                      Sqr(c)), Sqr(d)), C0), Not(IntegerQ(
                          Times(C1D2, CI, n))),
                      Not(IntegerQ(p))))),
          IIntegrate(5108,
              Integrate(
                  Times(
                      Exp(Times(ArcCot(Times(a_DEFAULT, x_)),
                          n_DEFAULT)),
                      Power(Plus(c_, Times(d_DEFAULT, Power(x_, CN1))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Negate(
                      Dist(Power(c, p),
                          Subst(Integrate(Times(Power(Plus(C1, Times(d, x, Power(c, CN1))), p),
                              Power(Subtract(C1, Times(CI, x, Power(a, CN1))), Times(C1D2, CI, n)),
                              Power(Times(Sqr(x),
                                  Power(Plus(C1, Times(CI, x, Power(a, CN1))), Times(C1D2, CI, n))),
                                  CN1)),
                              x), x, Power(x, CN1)),
                          x)),
                  And(FreeQ(List(a, c, d, n, p), x), EqQ(Plus(Sqr(c),
                      Times(Sqr(a), Sqr(d))), C0), Not(
                          IntegerQ(Times(C1D2, CI, n))),
                      Or(IntegerQ(p), GtQ(c, C0))))),
          IIntegrate(5109,
              Integrate(
                  Times(Exp(Times(ArcCot(Times(a_DEFAULT, x_)), n_DEFAULT)),
                      Power(Plus(c_,
                          Times(d_DEFAULT, Power(x_, CN1))), p_DEFAULT),
                      Power(x_, m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Negate(
                      Dist(Power(c, p),
                          Subst(Integrate(Times(Power(Plus(C1, Times(d, x, Power(c, CN1))), p),
                              Power(Subtract(C1, Times(CI, x, Power(a, CN1))), Times(C1D2, CI, n)),
                              Power(Times(Power(x, Plus(m, C2)),
                                  Power(Plus(C1, Times(CI, x, Power(a, CN1))), Times(C1D2, CI, n))),
                                  CN1)),
                              x), x, Power(x, CN1)),
                          x)),
                  And(FreeQ(List(a, c, d, m, n, p), x),
                      EqQ(Plus(Sqr(c), Times(Sqr(a), Sqr(
                          d))), C0),
                      Not(IntegerQ(Times(C1D2, CI, n))), Or(IntegerQ(p),
                          GtQ(c, C0)),
                      IntegerQ(m)))),
          IIntegrate(5110,
              Integrate(
                  Times(
                      Exp(Times(ArcCot(Times(a_DEFAULT,
                          x_)), n_DEFAULT)),
                      Power(Plus(c_, Times(d_DEFAULT, Power(x_, CN1))), p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(Plus(c, Times(d, Power(x, CN1))), p), Power(Power(
                              Plus(C1, Times(d, Power(Times(c, x), CN1))), p), CN1)),
                      Integrate(
                          Times(Power(Plus(C1, Times(d, Power(Times(c, x), CN1))), p),
                              Exp(Times(n, ArcCot(Times(a, x))))),
                          x),
                      x),
                  And(FreeQ(List(a, c, d, n, p), x), EqQ(Plus(Sqr(c), Times(Sqr(a), Sqr(d))),
                      C0), Not(IntegerQ(Times(C1D2, CI, n))),
                      Not(Or(IntegerQ(p), GtQ(c, C0)))))),
          IIntegrate(5111,
              Integrate(
                  Times(
                      Exp(Times(ArcCot(
                          Times(a_DEFAULT, x_)), n_DEFAULT)),
                      Power(Plus(c_, Times(d_DEFAULT, Power(x_, CN1))), p_DEFAULT), Power(x_, m_)),
                  x_Symbol),
              Condition(
                  Negate(
                      Dist(Times(Power(c, p), Power(x, m), Power(Power(x, CN1), m)),
                          Subst(Integrate(Times(Power(Plus(C1, Times(d, x, Power(c, CN1))), p),
                              Power(Subtract(C1, Times(CI, x, Power(a, CN1))), Times(C1D2, CI, n)),
                              Power(Times(Power(x, Plus(m, C2)),
                                  Power(Plus(C1, Times(CI, x, Power(a, CN1))), Times(C1D2, CI, n))),
                                  CN1)),
                              x), x, Power(x, CN1)),
                          x)),
                  And(FreeQ(List(a, c, d, m, n, p), x),
                      EqQ(Plus(Sqr(c), Times(Sqr(a), Sqr(d))), C0),
                      Not(IntegerQ(Times(C1D2, CI,
                          n))),
                      Or(IntegerQ(p), GtQ(c, C0)), Not(IntegerQ(m))))),
          IIntegrate(5112,
              Integrate(
                  Times(
                      Exp(Times(ArcCot(Times(a_DEFAULT, x_)), n_DEFAULT)), u_DEFAULT, Power(
                          Plus(c_, Times(d_DEFAULT, Power(x_, CN1))), p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(Plus(c, Times(d,
                              Power(x, CN1))), p),
                          Power(Power(Plus(C1, Times(d, Power(Times(c, x), CN1))), p), CN1)),
                      Integrate(
                          Times(
                              u, Power(Plus(C1, Times(d, Power(Times(c, x), CN1))),
                                  p),
                              Exp(Times(n, ArcCot(Times(a, x))))),
                          x),
                      x),
                  And(FreeQ(List(a, c, d, n, p), x), EqQ(Plus(Sqr(c), Times(Sqr(a), Sqr(d))),
                      C0), Not(IntegerQ(Times(C1D2, CI, n))),
                      Not(Or(IntegerQ(p), GtQ(c, C0)))))),
          IIntegrate(5113,
              Integrate(
                  Times(
                      Exp(Times(ArcCot(Times(a_DEFAULT, x_)),
                          n_DEFAULT)),
                      Power(Plus(c_, Times(d_DEFAULT, Sqr(x_))), CN1)),
                  x_Symbol),
              Condition(
                  Negate(
                      Simp(
                          Times(Exp(Times(n, ArcCot(Times(a, x)))),
                              Power(Times(a, c, n), CN1)),
                          x)),
                  And(FreeQ(List(a, c, d, n), x), EqQ(d, Times(Sqr(a), c))))),
          IIntegrate(5114,
              Integrate(
                  Times(
                      Exp(Times(ArcCot(Times(a_DEFAULT, x_)), n_DEFAULT)), Power(
                          Plus(c_, Times(d_DEFAULT, Sqr(x_))), QQ(-3L, 2L))),
                  x_Symbol),
              Condition(
                  Negate(
                      Simp(
                          Times(
                              Subtract(n, Times(a, x)), Exp(Times(n,
                                  ArcCot(Times(a, x)))),
                              Power(
                                  Times(a, c, Plus(Sqr(
                                      n), C1), Sqrt(
                                          Plus(c, Times(d, Sqr(x))))),
                                  CN1)),
                          x)),
                  And(FreeQ(List(a, c, d, n), x), EqQ(d, Times(Sqr(a), c)),
                      Not(IntegerQ(Times(C1D2, Subtract(Times(CI, n), C1))))))),
          IIntegrate(5115,
              Integrate(
                  Times(
                      Exp(Times(ArcCot(Times(a_DEFAULT, x_)),
                          n_DEFAULT)),
                      Power(Plus(c_, Times(d_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(Plus(Negate(
                  Simp(
                      Times(Plus(n, Times(C2, a, Plus(p, C1), x)), Power(
                          Plus(c, Times(d, Sqr(x))), Plus(p, C1)),
                          Exp(Times(n, ArcCot(Times(a, x)))),
                          Power(Times(a, c, Plus(Sqr(n), Times(C4, Sqr(Plus(p, C1))))), CN1)),
                      x)),
                  Dist(
                      Times(
                          C2, Plus(p, C1), Plus(Times(C2, p), C3), Power(
                              Times(c, Plus(Sqr(n), Times(C4, Sqr(Plus(p, C1))))), CN1)),
                      Integrate(
                          Times(Power(Plus(c, Times(d, Sqr(x))), Plus(p, C1)),
                              Exp(Times(n, ArcCot(Times(a, x))))),
                          x),
                      x)),
                  And(FreeQ(List(a, c, d, n), x), EqQ(d, Times(Sqr(a), c)), LtQ(p, CN1),
                      NeQ(p, QQ(-3L, 2L)), NeQ(Plus(Sqr(n), Times(C4, Sqr(Plus(p, C1)))), C0),
                      Not(And(IntegerQ(p),
                          IntegerQ(Times(C1D2, CI, n)))),
                      Not(And(Not(IntegerQ(
                          p)), IntegerQ(
                              Times(C1D2, Subtract(Times(CI, n), C1)))))))),
          IIntegrate(5116,
              Integrate(
                  Times(
                      Exp(Times(ArcCot(Times(a_DEFAULT, x_)), n_DEFAULT)), x_, Power(
                          Plus(c_, Times(d_DEFAULT, Sqr(x_))), QQ(-3L, 2L))),
                  x_Symbol),
              Condition(
                  Negate(
                      Simp(
                          Times(
                              Plus(C1, Times(a, n, x)), Exp(Times(n,
                                  ArcCot(Times(a, x)))),
                              Power(
                                  Times(Sqr(a), c, Plus(Sqr(
                                      n), C1), Sqrt(
                                          Plus(c, Times(d, Sqr(x))))),
                                  CN1)),
                          x)),
                  And(FreeQ(List(a, c, d, n), x), EqQ(d, Times(Sqr(a), c)), Not(IntegerQ(Times(C1D2,
                      Subtract(Times(CI, n), C1))))))),
          IIntegrate(5117,
              Integrate(
                  Times(
                      Exp(Times(ArcCot(Times(a_DEFAULT, x_)),
                          n_DEFAULT)),
                      x_, Power(Plus(c_, Times(d_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(Plus(
                  Simp(
                      Times(Subtract(Times(C2, Plus(p, C1)), Times(a, n, x)),
                          Power(Plus(c, Times(d, Sqr(x))), Plus(p, C1)),
                          Exp(Times(n, ArcCot(Times(a, x)))),
                          Power(Times(Sqr(a), c, Plus(Sqr(n), Times(C4, Sqr(Plus(p, C1))))), CN1)),
                      x),
                  Dist(
                      Times(n, Plus(Times(C2, p), C3),
                          Power(Times(a, c, Plus(Sqr(n), Times(C4, Sqr(Plus(p, C1))))), CN1)),
                      Integrate(Times(Power(Plus(c, Times(d, Sqr(x))), Plus(p, C1)),
                          Exp(Times(n, ArcCot(Times(a, x))))), x),
                      x)),
                  And(FreeQ(List(a, c, d, n), x), EqQ(d, Times(Sqr(a), c)), LeQ(p, CN1),
                      NeQ(p, QQ(-3L, 2L)), NeQ(Plus(Sqr(n), Times(C4, Sqr(Plus(p, C1)))), C0),
                      Not(And(IntegerQ(p), IntegerQ(Times(C1D2, CI, n)))),
                      Not(And(Not(IntegerQ(p)),
                          IntegerQ(Times(C1D2, Subtract(Times(CI, n), C1)))))))),
          IIntegrate(5118,
              Integrate(
                  Times(
                      Exp(Times(ArcCot(Times(a_DEFAULT, x_)),
                          n_DEFAULT)),
                      Sqr(x_), Power(Plus(c_, Times(d_DEFAULT, Sqr(x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(Plus(n, Times(C2, Plus(p, C1), a, x)),
                          Power(Plus(c, Times(d, Sqr(x))), Plus(p, C1)),
                          Exp(Times(n,
                              ArcCot(Times(a, x)))),
                          Power(Times(Power(a, C3), c, Sqr(n), Plus(Sqr(n), C1)), CN1)),
                      x),
                  And(FreeQ(List(a, c, d, n), x), EqQ(d, Times(Sqr(a), c)),
                      EqQ(Subtract(Sqr(n),
                          Times(C2, Plus(p, C1))), C0),
                      NeQ(Plus(Sqr(n), C1), C0)))),
          IIntegrate(5119,
              Integrate(
                  Times(
                      Exp(Times(ArcCot(Times(a_DEFAULT, x_)),
                          n_DEFAULT)),
                      Sqr(x_), Power(Plus(c_, Times(d_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(Times(Plus(n, Times(C2, Plus(p, C1), a, x)),
                          Power(Plus(c, Times(d, Sqr(x))), Plus(p, C1)),
                          Exp(Times(n, ArcCot(Times(a, x)))),
                          Power(Times(Power(a, C3), c, Plus(Sqr(n), Times(C4, Sqr(Plus(p, C1))))),
                              CN1)),
                          x),
                      Dist(
                          Times(Subtract(Sqr(n), Times(C2, Plus(p, C1))),
                              Power(Times(Sqr(a), c, Plus(Sqr(n), Times(C4, Sqr(Plus(p, C1))))),
                                  CN1)),
                          Integrate(Times(Power(Plus(c, Times(d, Sqr(x))), Plus(p, C1)),
                              Exp(Times(n, ArcCot(Times(a, x))))), x),
                          x)),
                  And(FreeQ(List(a, c, d, n), x), EqQ(d, Times(Sqr(a), c)), LeQ(p, CN1),
                      NeQ(Subtract(Sqr(n), Times(C2, Plus(p, C1))), C0),
                      NeQ(Plus(Sqr(n), Times(C4,
                          Sqr(Plus(p, C1)))), C0),
                      Not(And(IntegerQ(p),
                          IntegerQ(Times(C1D2, CI, n)))),
                      Not(And(Not(IntegerQ(p)),
                          IntegerQ(Times(C1D2, Subtract(Times(CI, n), C1)))))))),
          IIntegrate(5120,
              Integrate(Times(
                  Exp(Times(ArcCot(Times(a_DEFAULT, x_)), n_DEFAULT)), Power(x_, m_DEFAULT),
                  Power(Plus(c_, Times(d_DEFAULT, Sqr(x_))), p_)), x_Symbol),
              Condition(
                  Negate(Dist(Times(Power(c, p), Power(Power(a, Plus(m, C1)), CN1)), Subst(
                      Integrate(
                          Times(Exp(Times(n, x)), Power(Cot(x), Plus(m, Times(C2, Plus(p, C1)))),
                              Power(Power(Cos(x), Times(C2, Plus(p, C1))), CN1)),
                          x),
                      x, ArcCot(Times(a, x))), x)),
                  And(FreeQ(List(a, c, d, n), x), EqQ(d, Times(Sqr(a), c)), IntegerQ(m),
                      LeQ(C3, m, Times(CN2, Plus(p, C1))), IntegerQ(p)))));
}
