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
class IntRules273 {
  public static IAST RULES =
      List(
          IIntegrate(5461,
              Integrate(
                  Times(Power(Csch(Plus(a_DEFAULT, Times(b_DEFAULT, x_))), n_DEFAULT),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT,
                          x_)), m_DEFAULT),
                      Power(Sech(Plus(a_DEFAULT, Times(b_DEFAULT, x_))), n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(C2, n),
                      Integrate(
                          Times(Power(Plus(c, Times(d, x)), m),
                              Power(Csch(Plus(Times(C2, a), Times(C2, b, x))), n)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d), x), RationalQ(m), IntegerQ(n)))),
          IIntegrate(
              5462, Integrate(Times(Power(Csch(Plus(a_DEFAULT, Times(b_DEFAULT, x_))), n_DEFAULT),
                  Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_DEFAULT),
                  Power(Sech(Plus(a_DEFAULT, Times(b_DEFAULT, x_))), p_DEFAULT)), x_Symbol),
              Condition(With(
                  List(Set(u,
                      IntHide(Times(Power(Csch(Plus(a, Times(b, x))), n),
                          Power(Sech(Plus(a, Times(b, x))), p)), x))),
                  Subtract(Dist(Power(Plus(c, Times(d, x)), m), u, x),
                      Dist(Times(d, m),
                          Integrate(Times(Power(Plus(c, Times(d, x)), Subtract(m, C1)), u), x),
                          x))),
                  And(FreeQ(List(a, b, c, d), x), IntegersQ(n, p), GtQ(m, C0), NeQ(n, p)))),
          IIntegrate(5463,
              Integrate(
                  Times(
                      Power(u_, m_DEFAULT), Power($(F_,
                          v_), n_DEFAULT),
                      Power($(G_, w_), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(Power(ExpandToSum(u, x), m), Power(F(ExpandToSum(v, x)), n),
                          Power(G(ExpandToSum(v, x)), p)),
                      x),
                  And(FreeQ(List(m, n, p), x), HyperbolicQ(FSymbol), HyperbolicQ(GSymbol), EqQ(v,
                      w), LinearQ(List(u, v, w), x),
                      Not(LinearMatchQ(List(u, v, w), x))))),
          IIntegrate(5464,
              Integrate(
                  Times(Cosh(Plus(c_DEFAULT, Times(d_DEFAULT, x_))),
                      Power(Plus(e_DEFAULT,
                          Times(f_DEFAULT, x_)), m_DEFAULT),
                      Power(Plus(a_, Times(b_DEFAULT, Sinh(Plus(c_DEFAULT, Times(d_DEFAULT, x_))))),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(Times(Power(Plus(e, Times(f, x)), m),
                          Power(Plus(a, Times(b, Sinh(Plus(c, Times(d, x))))),
                              Plus(n, C1)),
                          Power(Times(b, d, Plus(n, C1)), CN1)), x),
                      Dist(
                          Times(f, m, Power(Times(b, d, Plus(n, C1)),
                              CN1)),
                          Integrate(
                              Times(
                                  Power(Plus(e, Times(f, x)), Subtract(m,
                                      C1)),
                                  Power(Plus(a, Times(b,
                                      Sinh(Plus(c, Times(d, x))))), Plus(n,
                                          C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, n), x), IGtQ(m, C0), NeQ(n, CN1)))),
          IIntegrate(5465,
              Integrate(
                  Times(
                      Power(
                          Plus(Times(Cosh(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), b_DEFAULT),
                              a_),
                          n_DEFAULT),
                      Power(Plus(e_DEFAULT,
                          Times(f_DEFAULT, x_)), m_DEFAULT),
                      Sinh(Plus(c_DEFAULT, Times(d_DEFAULT, x_)))),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Power(Plus(e, Times(f, x)), m),
                              Power(Plus(a, Times(b, Cosh(Plus(c, Times(d, x))))),
                                  Plus(n, C1)),
                              Power(Times(b, d, Plus(n, C1)), CN1)),
                          x),
                      Dist(
                          Times(f, m, Power(Times(b, d, Plus(n, C1)),
                              CN1)),
                          Integrate(
                              Times(
                                  Power(Plus(e, Times(f,
                                      x)), Subtract(m,
                                          C1)),
                                  Power(Plus(a, Times(b, Cosh(Plus(c, Times(d, x))))),
                                      Plus(n, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, n), x), IGtQ(m, C0), NeQ(n, CN1)))),
          IIntegrate(5466,
              Integrate(
                  Times(Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT),
                      Sqr(Sech(
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_)))),
                      Power(
                          Plus(a_, Times(b_DEFAULT, Tanh(Plus(c_DEFAULT,
                              Times(d_DEFAULT, x_))))),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Power(Plus(e, Times(f, x)), m),
                              Power(Plus(a, Times(b, Tanh(Plus(c, Times(d, x))))), Plus(n, C1)),
                              Power(Times(b, d, Plus(n, C1)), CN1)),
                          x),
                      Dist(Times(f, m, Power(Times(b, d, Plus(n, C1)), CN1)),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), Subtract(m, C1)),
                                  Power(Plus(a, Times(b, Tanh(Plus(c, Times(d, x))))),
                                      Plus(n, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, n), x), IGtQ(m, C0), NeQ(n, CN1)))),
          IIntegrate(5467,
              Integrate(
                  Times(
                      Sqr(Csch(
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_)))),
                      Power(
                          Plus(Times(Coth(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), b_DEFAULT),
                              a_),
                          n_DEFAULT),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(
                                  Power(Plus(e,
                                      Times(f, x)), m),
                                  Power(Plus(a, Times(b, Coth(Plus(c, Times(d, x))))), Plus(n, C1)),
                                  Power(Times(b, d, Plus(n, C1)), CN1)),
                              x)),
                      Dist(
                          Times(f, m, Power(Times(b, d, Plus(n, C1)),
                              CN1)),
                          Integrate(
                              Times(
                                  Power(Plus(e, Times(f, x)), Subtract(m,
                                      C1)),
                                  Power(Plus(a, Times(b,
                                      Coth(Plus(c, Times(d, x))))), Plus(n,
                                          C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, n), x), IGtQ(m, C0), NeQ(n, CN1)))),
          IIntegrate(5468,
              Integrate(
                  Times(
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT,
                          x_)), m_DEFAULT),
                      Sech(Plus(c_DEFAULT,
                          Times(d_DEFAULT, x_))),
                      Power(Plus(a_, Times(b_DEFAULT,
                          Sech(Plus(c_DEFAULT, Times(d_DEFAULT, x_))))), n_DEFAULT),
                      Tanh(Plus(c_DEFAULT, Times(d_DEFAULT, x_)))),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(
                                  Power(Plus(e,
                                      Times(f, x)), m),
                                  Power(Plus(a, Times(b, Sech(Plus(c, Times(d, x))))), Plus(n, C1)),
                                  Power(Times(b, d, Plus(n, C1)), CN1)),
                              x)),
                      Dist(
                          Times(f, m, Power(Times(b, d, Plus(n, C1)),
                              CN1)),
                          Integrate(
                              Times(
                                  Power(Plus(e, Times(f, x)), Subtract(m,
                                      C1)),
                                  Power(Plus(a, Times(b,
                                      Sech(Plus(c, Times(d, x))))), Plus(n,
                                          C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, n), x), IGtQ(m, C0), NeQ(n, CN1)))),
          IIntegrate(5469,
              Integrate(
                  Times(
                      Coth(Plus(c_DEFAULT, Times(d_DEFAULT,
                          x_))),
                      Csch(Plus(c_DEFAULT,
                          Times(d_DEFAULT, x_))),
                      Power(Plus(Times(Csch(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), b_DEFAULT),
                          a_), n_DEFAULT),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(
                                  Power(Plus(e,
                                      Times(f, x)), m),
                                  Power(Plus(a, Times(b, Csch(Plus(c, Times(d, x))))), Plus(n, C1)),
                                  Power(Times(b, d, Plus(n, C1)), CN1)),
                              x)),
                      Dist(Times(f, m, Power(Times(b, d, Plus(n, C1)), CN1)),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), Subtract(m, C1)),
                                  Power(Plus(a, Times(b, Csch(Plus(c, Times(d, x))))),
                                      Plus(n, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, n), x), IGtQ(m, C0), NeQ(n, CN1)))),
          IIntegrate(5470,
              Integrate(
                  Times(Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT),
                      Power(Sinh(Plus(a_DEFAULT, Times(b_DEFAULT, x_))), p_DEFAULT), Power(Sinh(
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandTrigReduce(
                          Power(Plus(e,
                              Times(f, x)), m),
                          Times(Power(Sinh(Plus(a, Times(b, x))), p),
                              Power(Sinh(Plus(c, Times(d, x))), q)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f), x), IGtQ(p, C0), IGtQ(q, C0), IntegerQ(m)))),
          IIntegrate(5471,
              Integrate(
                  Times(Power(Cosh(Plus(a_DEFAULT, Times(b_DEFAULT, x_))), p_DEFAULT),
                      Power(Cosh(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), q_DEFAULT), Power(
                          Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandTrigReduce(Power(Plus(e, Times(f, x)), m),
                          Times(Power(Cosh(Plus(a, Times(b, x))), p),
                              Power(Cosh(Plus(c, Times(d, x))), q)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f), x), IGtQ(p, C0), IGtQ(q, C0), IntegerQ(m)))),
          IIntegrate(5472,
              Integrate(
                  Times(Power(Cosh(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), q_DEFAULT),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT),
                      Power(Sinh(Plus(a_DEFAULT, Times(b_DEFAULT, x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandTrigReduce(Power(Plus(e, Times(f, x)), m),
                          Times(
                              Power(Sinh(Plus(a, Times(b, x))),
                                  p),
                              Power(Cosh(Plus(c, Times(d, x))), q)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m), x), IGtQ(p, C0), IGtQ(q, C0)))),
          IIntegrate(5473,
              Integrate(
                  Times(Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT),
                      Power($(F_, Plus(a_DEFAULT, Times(b_DEFAULT, x_))), p_DEFAULT), Power($(G_,
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandTrigExpand(
                          Times(Power(Plus(e, Times(f, x)), m), Power(G(Plus(c, Times(d, x))), q)),
                          FSymbol, Plus(c, Times(d, x)), p, Times(b, Power(d, CN1)), x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m), x), MemberQ(List(Sinh, Cosh), FSymbol),
                      MemberQ(List(Sech, Csch), GSymbol), IGtQ(p, C0), IGtQ(q, C0),
                      EqQ(Subtract(Times(b, c), Times(a, d)), C0), IGtQ(Times(b, Power(d, CN1)),
                          C1)))),
          IIntegrate(5474,
              Integrate(
                  Times(
                      Power(F_, Times(c_DEFAULT,
                          Plus(a_DEFAULT, Times(b_DEFAULT, x_)))),
                      Sinh(Plus(d_DEFAULT, Times(e_DEFAULT, x_)))),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(Simp(
                          Times(b, c, Log(FSymbol), Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                              Sinh(Plus(d, Times(e, x))),
                              Power(Subtract(Sqr(e), Times(Sqr(b), Sqr(c), Sqr(Log(FSymbol)))),
                                  CN1)),
                          x)),
                      Simp(
                          Times(
                              e, Power(FSymbol, Times(c, Plus(a,
                                  Times(b, x)))),
                              Cosh(Plus(d,
                                  Times(e, x))),
                              Power(Subtract(Sqr(e), Times(Sqr(b), Sqr(c), Sqr(Log(FSymbol)))),
                                  CN1)),
                          x)),
                  And(FreeQ(List(FSymbol, a, b, c, d,
                      e), x), NeQ(Subtract(Sqr(e), Times(Sqr(b), Sqr(c), Sqr(Log(FSymbol)))),
                          C0)))),
          IIntegrate(5475,
              Integrate(
                  Times(
                      Cosh(Plus(d_DEFAULT, Times(e_DEFAULT,
                          x_))),
                      Power(F_, Times(c_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, x_))))),
                  x_Symbol),
              Condition(
                  Plus(Negate(Simp(
                      Times(b, c, Log(FSymbol), Power(FSymbol,
                          Times(c, Plus(a, Times(b, x)))), Cosh(Plus(d, Times(e, x))),
                          Power(Subtract(Sqr(e), Times(Sqr(b), Sqr(c), Sqr(Log(FSymbol)))), CN1)),
                      x)), Simp(
                          Times(e, Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                              Sinh(Plus(d,
                                  Times(e, x))),
                              Power(Subtract(Sqr(e), Times(Sqr(b), Sqr(c), Sqr(Log(FSymbol)))),
                                  CN1)),
                          x)),
                  And(FreeQ(List(FSymbol, a, b, c, d, e), x), NeQ(Subtract(Sqr(e),
                      Times(Sqr(b), Sqr(c), Sqr(Log(FSymbol)))), C0)))),
          IIntegrate(5476,
              Integrate(
                  Times(
                      Power(F_, Times(c_DEFAULT, Plus(a_DEFAULT,
                          Times(b_DEFAULT, x_)))),
                      Power(Sinh(Plus(d_DEFAULT, Times(e_DEFAULT, x_))), n_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(b, c, Log(FSymbol),
                                  Power(FSymbol, Times(c, Plus(a,
                                      Times(b, x)))),
                                  Power(Sinh(
                                      Plus(d, Times(e, x))), n),
                                  Power(
                                      Subtract(
                                          Times(Sqr(e), Sqr(n)), Times(Sqr(b), Sqr(c),
                                              Sqr(Log(FSymbol)))),
                                      CN1)),
                              x)),
                      Negate(Dist(
                          Times(
                              n, Subtract(n, C1), Sqr(e),
                              Power(Subtract(Times(Sqr(e), Sqr(n)),
                                  Times(Sqr(b), Sqr(c), Sqr(Log(FSymbol)))), CN1)),
                          Integrate(
                              Times(Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                                  Power(Sinh(Plus(d, Times(e, x))), Subtract(n, C2))),
                              x),
                          x)),
                      Simp(
                          Times(e, n, Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                              Cosh(Plus(d, Times(e, x))),
                              Power(Sinh(Plus(d, Times(e, x))), Subtract(n, C1)),
                              Power(Subtract(Times(Sqr(e), Sqr(n)),
                                  Times(Sqr(b), Sqr(c), Sqr(Log(FSymbol)))), CN1)),
                          x)),
                  And(FreeQ(List(FSymbol, a, b, c, d,
                      e), x), NeQ(
                          Subtract(Times(Sqr(e), Sqr(
                              n)), Times(Sqr(b), Sqr(c),
                                  Sqr(Log(FSymbol)))),
                          C0),
                      GtQ(n, C1)))),
          IIntegrate(5477,
              Integrate(
                  Times(
                      Power(Cosh(Plus(d_DEFAULT,
                          Times(e_DEFAULT, x_))), n_),
                      Power(F_, Times(c_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, x_))))),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(Simp(
                          Times(b, c, Log(FSymbol), Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                              Power(Cosh(Plus(d, Times(e, x))), n),
                              Power(Subtract(Times(Sqr(e), Sqr(n)),
                                  Times(Sqr(b), Sqr(c), Sqr(Log(FSymbol)))), CN1)),
                          x)),
                      Dist(
                          Times(n, Subtract(n, C1), Sqr(e),
                              Power(
                                  Subtract(
                                      Times(Sqr(e), Sqr(n)), Times(Sqr(b), Sqr(c),
                                          Sqr(Log(FSymbol)))),
                                  CN1)),
                          Integrate(
                              Times(Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                                  Power(Cosh(Plus(d, Times(e, x))), Subtract(n, C2))),
                              x),
                          x),
                      Simp(
                          Times(e, n, Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                              Sinh(Plus(d, Times(e, x))),
                              Power(Cosh(Plus(d, Times(e, x))), Subtract(n, C1)),
                              Power(
                                  Subtract(Times(Sqr(e), Sqr(n)),
                                      Times(Sqr(b), Sqr(c), Sqr(Log(FSymbol)))),
                                  CN1)),
                          x)),
                  And(FreeQ(List(FSymbol, a, b, c, d,
                      e), x), NeQ(
                          Subtract(Times(Sqr(e), Sqr(n)),
                              Times(Sqr(b), Sqr(c), Sqr(Log(FSymbol)))),
                          C0),
                      GtQ(n, C1)))),
          IIntegrate(5478,
              Integrate(Times(Power(F_, Times(c_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, x_)))),
                  Power(Sinh(Plus(d_DEFAULT, Times(e_DEFAULT, x_))), n_)), x_Symbol),
              Condition(
                  Plus(Negate(
                      Simp(Times(b, c, Log(FSymbol), Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                          Power(Sinh(Plus(d, Times(e, x))), Plus(n, C2)),
                          Power(Times(Sqr(e), Plus(n, C1), Plus(n, C2)), CN1)), x)),
                      Simp(
                          Times(Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                              Cosh(Plus(d, Times(e, x))),
                              Power(Sinh(Plus(d, Times(e, x))), Plus(n,
                                  C1)),
                              Power(Times(e, Plus(n, C1)), CN1)),
                          x)),
                  And(FreeQ(List(FSymbol, a, b, c, d, e, n), x),
                      EqQ(Subtract(Times(Sqr(e), Sqr(Plus(n, C2))),
                          Times(Sqr(b), Sqr(c), Sqr(Log(FSymbol)))), C0),
                      NeQ(n, CN1), NeQ(n, CN2)))),
          IIntegrate(5479,
              Integrate(
                  Times(Power(Cosh(Plus(d_DEFAULT, Times(e_DEFAULT, x_))), n_),
                      Power(F_, Times(c_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, x_))))),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(Times(b, c, Log(FSymbol), Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                          Power(Cosh(Plus(d, Times(e, x))), Plus(n, C2)), Power(
                              Times(Sqr(e), Plus(n, C1), Plus(n, C2)), CN1)),
                          x),
                      Simp(
                          Times(Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                              Sinh(Plus(d, Times(e, x))), Power(Cosh(Plus(d, Times(e, x))),
                                  Plus(n, C1)),
                              Power(Times(e, Plus(n, C1)), CN1)),
                          x)),
                  And(FreeQ(List(FSymbol, a, b, c, d, e,
                      n), x), EqQ(
                          Subtract(
                              Times(Sqr(e), Sqr(
                                  Plus(n, C2))),
                              Times(Sqr(b), Sqr(c), Sqr(Log(FSymbol)))),
                          C0),
                      NeQ(n, CN1), NeQ(n, CN2)))),
          IIntegrate(5480,
              Integrate(
                  Times(Power(F_, Times(c_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, x_)))),
                      Power(Sinh(Plus(d_DEFAULT, Times(e_DEFAULT, x_))), n_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(b, c, Log(FSymbol),
                                  Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                                  Power(Sinh(Plus(d, Times(e, x))), Plus(n,
                                      C2)),
                                  Power(Times(Sqr(e), Plus(n, C1), Plus(n, C2)), CN1)),
                              x)),
                      Negate(
                          Dist(
                              Times(
                                  Subtract(Times(Sqr(e), Sqr(Plus(n, C2))),
                                      Times(Sqr(b), Sqr(c), Sqr(Log(FSymbol)))),
                                  Power(Times(Sqr(e), Plus(n, C1), Plus(n, C2)), CN1)),
                              Integrate(Times(Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                                  Power(Sinh(Plus(d, Times(e, x))), Plus(n, C2))), x),
                              x)),
                      Simp(Times(Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                          Cosh(Plus(d, Times(e, x))),
                          Power(Sinh(Plus(d, Times(e, x))), Plus(n, C1)),
                          Power(Times(e, Plus(n, C1)), CN1)), x)),
                  And(FreeQ(List(FSymbol, a, b, c, d, e), x),
                      NeQ(Subtract(Times(Sqr(e), Sqr(Plus(n, C2))),
                          Times(Sqr(b), Sqr(c), Sqr(Log(FSymbol)))), C0),
                      LtQ(n, CN1), NeQ(n, CN2)))));
}
