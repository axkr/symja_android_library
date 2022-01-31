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
class IntRules1 {
  public static IAST RULES =
      List(
          IIntegrate(21,
              Integrate(
                  Times(
                      u_DEFAULT, Power(Plus(a_,
                          Times(b_DEFAULT, v_)), m_DEFAULT),
                      Power(Plus(c_, Times(d_DEFAULT, v_)), n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Power(Times(b,
                          Power(d, CN1)), m),
                      Integrate(Times(u, Power(Plus(c, Times(d, v)), Plus(m, n))), x), x),
                  And(FreeQ(List(a, b, c, d, n), x), EqQ(Subtract(Times(b, c), Times(a, d)), C0),
                      IntegerQ(m), Or(
                          Not(IntegerQ(n)), SimplerQ(Plus(c, Times(d, x)),
                              Plus(a, Times(b, x))))))),
          IIntegrate(22,
              Integrate(
                  Times(
                      u_DEFAULT, Power(Plus(a_, Times(b_DEFAULT, v_)),
                          m_),
                      Power(Plus(c_, Times(d_DEFAULT, v_)), n_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Power(Times(b,
                          Power(d, CN1)), m),
                      Integrate(Times(u, Power(Plus(c, Times(d, v)), Plus(m, n))), x), x),
                  And(FreeQ(List(a, b, c, d, m, n), x), EqQ(Subtract(Times(b, c), Times(a, d)),
                      C0), GtQ(Times(b, Power(d, CN1)), C0),
                      Not(Or(IntegerQ(m), IntegerQ(n)))))),
          IIntegrate(23,
              Integrate(
                  Times(
                      u_DEFAULT, Power(Plus(a_, Times(b_DEFAULT, v_)),
                          m_),
                      Power(Plus(c_, Times(d_DEFAULT, v_)), n_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(Plus(a, Times(b, v)), m), Power(Power(Plus(c, Times(d, v)), m),
                              CN1)),
                      Integrate(Times(u, Power(Plus(c, Times(d, v)), Plus(m, n))), x), x),
                  And(FreeQ(List(a, b, c, d, m, n), x), EqQ(Subtract(Times(b, c), Times(a, d)), C0),
                      Not(Or(IntegerQ(m), IntegerQ(n), GtQ(Times(b, Power(d, CN1)), C0)))))),
          IIntegrate(24,
              Integrate(
                  Times(
                      u_DEFAULT, Power(Plus(a_, Times(b_DEFAULT, v_)), m_), Plus(A_DEFAULT,
                          Times(B_DEFAULT, v_), Times(C_DEFAULT, Sqr(v_)))),
                  x_Symbol),
              Condition(
                  Dist(Power(b, CN2),
                      Integrate(
                          Times(u, Power(Plus(a, Times(b, v)), Plus(m, C1)),
                              Simp(
                                  Plus(Times(b, BSymbol), Times(CN1, a, CSymbol),
                                      Times(b, CSymbol, v)),
                                  x)),
                          x),
                      x),
                  And(FreeQ(List(a, b, ASymbol, BSymbol, CSymbol), x),
                      EqQ(Plus(Times(ASymbol, Sqr(b)), Times(CN1, a, b, BSymbol),
                          Times(Sqr(a), CSymbol)), C0),
                      LeQ(m, CN1)))),
          IIntegrate(25,
              Integrate(
                  Times(u_DEFAULT,
                      Power(Plus(a_, Times(b_DEFAULT,
                          Power(x_, n_DEFAULT))), m_DEFAULT),
                      Power(Plus(c_, Times(d_DEFAULT, Power(x_, q_DEFAULT))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Power(Times(d,
                          Power(a, CN1)), p),
                      Integrate(
                          Times(
                              u, Power(Plus(a, Times(b, Power(x, n))), Plus(m,
                                  p)),
                              Power(Power(x, Times(n, p)), CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, m, n), x), EqQ(q, Negate(n)), IntegerQ(p),
                      EqQ(Subtract(Times(a, c),
                          Times(b, d)), C0),
                      Not(And(IntegerQ(m), NegQ(n)))))),
          IIntegrate(26,
              Integrate(Times(u_DEFAULT,
                  Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_DEFAULT))), m_DEFAULT), Power(
                      Plus(c_, Times(d_DEFAULT, Power(x_, j_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Power(Times(CN1, Sqr(b),
                          Power(d, CN1)), m),
                      Integrate(Times(u, Power(Power(Subtract(a, Times(b, Power(x, n))), m), CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, m, n, p), x), EqQ(j, Times(C2, n)), EqQ(p,
                      Negate(m)), EqQ(Plus(Times(Sqr(b), c), Times(Sqr(a), d)), C0), GtQ(a, C0),
                      LtQ(d, C0)))),
          IIntegrate(27,
              Integrate(
                  Times(u_DEFAULT, Power(Plus(a_, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))),
                      p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(u,
                          Cancel(
                              Times(
                                  Power(Plus(Times(C1D2, b), Times(c, x)),
                                      Times(C2, p)),
                                  Power(Power(c, p), CN1)))),
                      x),
                  And(FreeQ(List(a, b, c), x), EqQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      IntegerQ(p)))),
          IIntegrate(28,
              Integrate(
                  Times(u_DEFAULT,
                      Power(Plus(a_, Times(c_DEFAULT, Power(x_, $p("n2", true))),
                          Times(b_DEFAULT, Power(x_, n_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(Power(c, p), CN1),
                      Integrate(
                          Times(u,
                              Power(Plus(Times(C1D2, b), Times(c, Power(x, n))), Times(C2, p))),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, n), x), EqQ($s("n2"),
                      Times(C2, n)), EqQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      IntegerQ(p)))),
          IIntegrate(29, Integrate(Power(x_,
              CN1), x_Symbol), Simp(Log(x),
                  x)),
          IIntegrate(
              30, Integrate(Power(x_,
                  m_DEFAULT), x_Symbol),
              Condition(
                  Simp(Times(Power(x, Plus(m, C1)),
                      Power(Plus(m, C1), CN1)), x),
                  And(FreeQ(m, x), NeQ(m, CN1)))),
          IIntegrate(
              31, Integrate(Power(Plus(a_, Times(b_DEFAULT, x_)),
                  CN1), x_Symbol),
              Condition(
                  Simp(Times(Log(RemoveContent(Plus(a, Times(b, x)), x)),
                      Power(b, CN1)), x),
                  FreeQ(List(a, b), x))),
          IIntegrate(
              32, Integrate(Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)),
                  m_), x_Symbol),
              Condition(
                  Simp(
                      Times(
                          Power(Plus(a, Times(b, x)), Plus(m, C1)), Power(Times(b, Plus(m, C1)),
                              CN1)),
                      x),
                  And(FreeQ(List(a, b, m), x), NeQ(m, CN1)))),
          IIntegrate(33, Integrate(Power(Plus(a_DEFAULT, Times(b_DEFAULT, u_)), m_), x_Symbol),
              Condition(
                  Dist(Power(Coefficient(u, x, C1), CN1),
                      Subst(Integrate(Power(Plus(a, Times(b, x)), m), x), x, u), x),
                  And(FreeQ(List(a, b, m), x), LinearQ(u, x), NeQ(u, x)))),
          IIntegrate(34,
              Integrate(
                  Times(
                      Power(Plus(a_, Times(b_DEFAULT, x_)), m_DEFAULT), Plus(c_,
                          Times(d_DEFAULT, x_))),
                  x_Symbol),
              Condition(
                  Simp(Times(d, x, Power(Plus(a, Times(b, x)), Plus(m, C1)),
                      Power(Times(b, Plus(m, C2)), CN1)), x),
                  And(FreeQ(List(a, b, c, d, m), x),
                      EqQ(Subtract(Times(a, d), Times(b, c, Plus(m, C2))), C0)))),
          IIntegrate(35,
              Integrate(
                  Times(
                      Power(Plus(a_, Times(b_DEFAULT, x_)),
                          CN1),
                      Power(Plus(c_, Times(d_DEFAULT, x_)), CN1)),
                  x_Symbol),
              Condition(
                  Integrate(Power(Plus(Times(a, c), Times(b, d, Sqr(x))),
                      CN1), x),
                  And(FreeQ(List(a, b, c, d), x), EqQ(Plus(Times(b, c), Times(a, d)), C0)))),
          IIntegrate(36,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)), CN1), Power(
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_)), CN1)),
                  x_Symbol),
              Condition(Subtract(
                  Dist(Times(b, Power(Subtract(Times(b, c), Times(a, d)), CN1)),
                      Integrate(Power(Plus(a, Times(b, x)), CN1), x), x),
                  Dist(Times(d, Power(Subtract(Times(b, c), Times(a, d)), CN1)),
                      Integrate(Power(Plus(c, Times(d, x)), CN1), x), x)),
                  And(FreeQ(List(a, b, c, d), x), NeQ(Subtract(Times(b, c), Times(a, d)), C0)))),
          IIntegrate(37,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT,
                          Times(b_DEFAULT, x_)), m_DEFAULT),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), n_)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(Power(Plus(a, Times(b, x)), Plus(m, C1)),
                          Power(Plus(c, Times(d, x)), Plus(n,
                              C1)),
                          Power(Times(Subtract(Times(b, c), Times(a, d)), Plus(m, C1)), CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, m, n), x), NeQ(Subtract(Times(b, c), Times(a, d)), C0),
                      EqQ(Plus(m, n, C2), C0), NeQ(m, CN1)))),
          IIntegrate(38,
              Integrate(
                  Times(Power(Plus(a_, Times(b_DEFAULT, x_)), m_),
                      Power(Plus(c_, Times(d_DEFAULT, x_)), m_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(
                              x, Power(Plus(a, Times(b, x)), m), Power(Plus(c, Times(d, x)),
                                  m),
                              Power(Plus(Times(C2, m), C1), CN1)),
                          x),
                      Dist(
                          Times(C2, a, c, m, Power(Plus(Times(C2, m), C1),
                              CN1)),
                          Integrate(Times(Power(Plus(a, Times(b, x)), Subtract(m, C1)),
                              Power(Plus(c, Times(d, x)), Subtract(m, C1))), x),
                          x)),
                  And(FreeQ(List(a, b, c, d), x), EqQ(Plus(Times(b, c), Times(a, d)), C0),
                      IGtQ(Plus(m, C1D2), C0)))),
          IIntegrate(39,
              Integrate(
                  Times(
                      Power(Plus(a_, Times(b_DEFAULT, x_)), QQ(-3L,
                          2L)),
                      Power(Plus(c_, Times(d_DEFAULT, x_)), QQ(-3L, 2L))),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(x,
                          Power(
                              Times(a, c, Sqrt(Plus(a, Times(b, x))),
                                  Sqrt(Plus(c, Times(d, x)))),
                              CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d), x), EqQ(Plus(Times(b, c), Times(a, d)), C0)))),
          IIntegrate(40,
              Integrate(Times(Power(Plus(a_, Times(b_DEFAULT, x_)), m_),
                  Power(Plus(c_, Times(d_DEFAULT, x_)), m_)), x_Symbol),
              Condition(
                  Plus(
                      Negate(Simp(Times(x, Power(Plus(a, Times(b, x)), Plus(m, C1)),
                          Power(Plus(c, Times(d, x)), Plus(m, C1)),
                          Power(Times(C2, a, c, Plus(m, C1)), CN1)), x)),
                      Dist(Times(Plus(Times(C2, m), C3), Power(Times(C2, a, c, Plus(m, C1)), CN1)),
                          Integrate(Times(Power(Plus(a, Times(b, x)), Plus(m, C1)),
                              Power(Plus(c, Times(d, x)), Plus(m, C1))), x),
                          x)),
                  And(FreeQ(List(a, b, c, d), x), EqQ(Plus(Times(b, c), Times(a, d)), C0),
                      ILtQ(Plus(m, QQ(3L, 2L)), C0)))));
}
