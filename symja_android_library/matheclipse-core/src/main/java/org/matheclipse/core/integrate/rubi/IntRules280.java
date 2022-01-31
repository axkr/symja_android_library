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
class IntRules280 {
  public static IAST RULES =
      List(
          IIntegrate(5601,
              Integrate(
                  Times(
                      Sinh(Plus(a_DEFAULT,
                          Times(b_DEFAULT, x_))),
                      Tanh(Plus(c_DEFAULT, Times(d_DEFAULT, x_)))),
                  x_Symbol),
              Condition(
                  Integrate(
                      Subtract(
                          Plus(Negate(Power(Times(Exp(Plus(a, Times(b, x))), C2), CN1)),
                              Times(C1D2, Exp(Plus(a, Times(b, x)))),
                              Power(
                                  Times(Exp(Plus(a, Times(b, x))),
                                      Plus(C1, Exp(Times(C2, Plus(c, Times(d, x)))))),
                                  CN1)),
                          Times(
                              Exp(Plus(a, Times(b, x))), Power(
                                  Plus(C1, Exp(Times(C2, Plus(c, Times(d, x))))), CN1))),
                      x),
                  And(FreeQ(List(a, b, c, d), x), NeQ(Subtract(Sqr(b), Sqr(d)), C0)))),
          IIntegrate(5602,
              Integrate(
                  Times(
                      Cosh(Plus(a_DEFAULT,
                          Times(b_DEFAULT, x_))),
                      Coth(Plus(c_DEFAULT, Times(d_DEFAULT, x_)))),
                  x_Symbol),
              Condition(
                  Integrate(
                      Subtract(
                          Subtract(
                              Plus(
                                  Power(Times(Exp(Plus(a, Times(b, x))), C2), CN1),
                                  Times(C1D2, Exp(Plus(a, Times(b, x))))),
                              Power(
                                  Times(
                                      Exp(Plus(a, Times(b, x))),
                                      Subtract(C1, Exp(Times(C2, Plus(c, Times(d, x)))))),
                                  CN1)),
                          Times(
                              Exp(Plus(a, Times(b,
                                  x))),
                              Power(Subtract(C1, Exp(Times(C2, Plus(c, Times(d, x))))), CN1))),
                      x),
                  And(FreeQ(List(a, b, c, d), x), NeQ(Subtract(Sqr(b), Sqr(d)), C0)))),
          IIntegrate(5603,
              Integrate(
                  Times(
                      Coth(Plus(c_DEFAULT,
                          Times(d_DEFAULT, x_))),
                      Sinh(Plus(a_DEFAULT, Times(b_DEFAULT, x_)))),
                  x_Symbol),
              Condition(
                  Integrate(
                      Subtract(
                          Plus(Negate(Power(Times(Exp(Plus(a, Times(b, x))), C2), CN1)),
                              Times(C1D2, Exp(Plus(a, Times(b, x)))),
                              Power(Times(Exp(Plus(a, Times(b, x))),
                                  Subtract(C1, Exp(Times(C2, Plus(c, Times(d, x)))))), CN1)),
                          Times(
                              Exp(Plus(a, Times(b, x))), Power(Subtract(C1,
                                  Exp(Times(C2, Plus(c, Times(d, x))))), CN1))),
                      x),
                  And(FreeQ(List(a, b, c, d), x), NeQ(Subtract(Sqr(b), Sqr(d)), C0)))),
          IIntegrate(5604,
              Integrate(
                  Times(
                      Cosh(Plus(a_DEFAULT,
                          Times(b_DEFAULT, x_))),
                      Tanh(Plus(c_DEFAULT, Times(d_DEFAULT, x_)))),
                  x_Symbol),
              Condition(
                  Integrate(
                      Subtract(
                          Subtract(
                              Plus(
                                  Power(Times(Exp(Plus(a, Times(b, x))), C2), CN1), Times(C1D2,
                                      Exp(Plus(a, Times(b, x))))),
                              Power(
                                  Times(Exp(Plus(a, Times(b, x))),
                                      Plus(C1, Exp(Times(C2, Plus(c, Times(d, x)))))),
                                  CN1)),
                          Times(
                              Exp(Plus(a, Times(b, x))), Power(
                                  Plus(C1, Exp(Times(C2, Plus(c, Times(d, x))))), CN1))),
                      x),
                  And(FreeQ(List(a, b, c, d), x), NeQ(Subtract(Sqr(b), Sqr(d)), C0)))),
          IIntegrate(5605,
              Integrate(
                  Power(Sinh(Times(a_DEFAULT, Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), CN1))),
                      n_DEFAULT),
                  x_Symbol),
              Condition(
                  Negate(Dist(Power(d, CN1),
                      Subst(Integrate(Times(Power(Sinh(Times(a, x)), n), Power(x, CN2)), x), x,
                          Power(Plus(c, Times(d, x)), CN1)),
                      x)),
                  And(FreeQ(List(a, c, d), x), IGtQ(n, C0)))),
          IIntegrate(5606,
              Integrate(
                  Power(
                      Cosh(Times(a_DEFAULT, Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), CN1))),
                      n_DEFAULT),
                  x_Symbol),
              Condition(Negate(Dist(Power(d, CN1),
                  Subst(Integrate(Times(Power(Cosh(Times(a, x)), n), Power(x, CN2)), x), x,
                      Power(Plus(c, Times(d, x)), CN1)),
                  x)), And(FreeQ(List(a, c, d), x),
                      IGtQ(n, C0)))),
          IIntegrate(5607,
              Integrate(
                  Power(
                      Sinh(
                          Times(
                              e_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT,
                                  x_)),
                              Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), CN1))),
                      n_DEFAULT),
                  x_Symbol),
              Condition(
                  Negate(
                      Dist(Power(d, CN1),
                          Subst(
                              Integrate(
                                  Times(Power(Sinh(Subtract(Times(b, e, Power(d, CN1)),
                                      Times(e, Subtract(Times(b, c), Times(a, d)), x,
                                          Power(d, CN1)))),
                                      n), Power(x, CN2)),
                                  x),
                              x, Power(Plus(c, Times(d, x)), CN1)),
                          x)),
                  And(FreeQ(List(a, b, c,
                      d), x), IGtQ(n,
                          C0),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0)))),
          IIntegrate(5608,
              Integrate(
                  Power(
                      Cosh(
                          Times(
                              e_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT,
                                  x_)),
                              Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), CN1))),
                      n_DEFAULT),
                  x_Symbol),
              Condition(
                  Negate(
                      Dist(Power(d, CN1),
                          Subst(
                              Integrate(
                                  Times(
                                      Power(Cosh(Subtract(Times(b, e, Power(d, CN1)),
                                          Times(e, Subtract(Times(b, c), Times(a, d)), x,
                                              Power(d, CN1)))),
                                          n),
                                      Power(x, CN2)),
                                  x),
                              x, Power(Plus(c, Times(d, x)), CN1)),
                          x)),
                  And(FreeQ(List(a, b, c, d), x), IGtQ(n, C0),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0)))),
          IIntegrate(
              5609, Integrate(Power(Sinh(u_),
                  n_DEFAULT), x_Symbol),
              Condition(
                  With(
                      List(Set($s("lst"),
                          QuotientOfLinearsParts(u, x))),
                      Integrate(
                          Power(
                              Sinh(
                                  Times(
                                      Plus(Part($s("lst"), C1), Times(Part($s("lst"), C2),
                                          x)),
                                      Power(
                                          Plus(Part($s("lst"), C3), Times(Part($s("lst"), C4), x)),
                                          CN1))),
                              n),
                          x)),
                  And(IGtQ(n, C0), QuotientOfLinearsQ(u, x)))),
          IIntegrate(5610, Integrate(Power(Cosh(u_), n_DEFAULT), x_Symbol),
              Condition(With(List(Set($s("lst"), QuotientOfLinearsParts(u, x))),
                  Integrate(Power(
                      Cosh(Times(Plus(Part($s("lst"), C1), Times(Part($s("lst"), C2), x)),
                          Power(Plus(Part($s("lst"), C3), Times(Part($s("lst"), C4), x)), CN1))),
                      n), x)),
                  And(IGtQ(n, C0), QuotientOfLinearsQ(u, x)))),
          IIntegrate(5611,
              Integrate(Times(u_DEFAULT, Power(Sinh(v_), p_DEFAULT),
                  Power(Sinh(w_), q_DEFAULT)), x_Symbol),
              Condition(Integrate(Times(u, Power(Sinh(v), Plus(p, q))), x), EqQ(w, v))),
          IIntegrate(5612,
              Integrate(Times(Power(Cosh(v_), p_DEFAULT), Power(Cosh(w_), q_DEFAULT),
                  u_DEFAULT), x_Symbol),
              Condition(Integrate(Times(u, Power(Cosh(v), Plus(p, q))), x), EqQ(w, v))),
          IIntegrate(5613,
              Integrate(Times(Power(Sinh(v_), p_DEFAULT),
                  Power(Sinh(w_), q_DEFAULT)), x_Symbol),
              Condition(
                  Integrate(ExpandTrigReduce(Times(Power(Sinh(v), p), Power(Sinh(w), q)),
                      x), x),
                  And(IGtQ(p, C0), IGtQ(q, C0),
                      Or(And(PolynomialQ(v, x), PolynomialQ(w, x)), And(BinomialQ(List(v, w), x),
                          IndependentQ(Cancel(Times(v, Power(w, CN1))), x)))))),
          IIntegrate(5614,
              Integrate(Times(Power(Cosh(v_), p_DEFAULT), Power(Cosh(w_), q_DEFAULT)), x_Symbol),
              Condition(
                  Integrate(ExpandTrigReduce(Times(Power(Cosh(v), p), Power(Cosh(w), q)), x), x),
                  And(IGtQ(p, C0), IGtQ(q, C0),
                      Or(And(PolynomialQ(v, x), PolynomialQ(w, x)),
                          And(BinomialQ(List(v, w), x),
                              IndependentQ(Cancel(Times(v, Power(w, CN1))), x)))))),
          IIntegrate(5615,
              Integrate(
                  Times(Power(x_, m_DEFAULT), Power(Sinh(v_), p_DEFAULT),
                      Power(Sinh(w_), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandTrigReduce(Power(x, m), Times(Power(Sinh(v), p), Power(Sinh(w), q)),
                          x),
                      x),
                  And(IGtQ(m, C0), IGtQ(p, C0), IGtQ(q, C0),
                      Or(And(PolynomialQ(v, x), PolynomialQ(w, x)),
                          And(BinomialQ(List(v, w), x),
                              IndependentQ(Cancel(Times(v, Power(w, CN1))), x)))))),
          IIntegrate(5616, Integrate(
              Times(Power(Cosh(v_), p_DEFAULT), Power(Cosh(w_), q_DEFAULT), Power(x_, m_DEFAULT)),
              x_Symbol),
              Condition(Integrate(
                  ExpandTrigReduce(Power(x, m), Times(Power(Cosh(v), p), Power(Cosh(w), q)), x), x),
                  And(IGtQ(m, C0), IGtQ(p, C0), IGtQ(q, C0),
                      Or(And(PolynomialQ(v, x), PolynomialQ(w, x)),
                          And(BinomialQ(List(v, w), x),
                              IndependentQ(Cancel(Times(v, Power(w, CN1))), x)))))),
          IIntegrate(5617,
              Integrate(Times(Power(Cosh(w_), p_DEFAULT), u_DEFAULT, Power(Sinh(v_), p_DEFAULT)),
                  x_Symbol),
              Condition(Dist(Power(Power(C2, p), CN1),
                  Integrate(Times(u, Power(Sinh(Times(C2, v)), p)), x), x),
                  And(EqQ(w, v), IntegerQ(p)))),
          IIntegrate(5618,
              Integrate(Times(Power(Cosh(w_), q_DEFAULT), Power(Sinh(v_), p_DEFAULT)), x_Symbol),
              Condition(
                  Integrate(ExpandTrigReduce(Times(Power(Sinh(v), p), Power(Cosh(w), q)), x), x),
                  And(IGtQ(p, C0), IGtQ(q, C0),
                      Or(And(PolynomialQ(v, x), PolynomialQ(w, x)),
                          And(BinomialQ(List(v, w), x),
                              IndependentQ(Cancel(Times(v, Power(w, CN1))), x)))))),
          IIntegrate(5619,
              Integrate(
                  Times(Power(Cosh(
                      w_), q_DEFAULT), Power(x_,
                          m_DEFAULT),
                      Power(Sinh(v_), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(ExpandTrigReduce(Power(x, m),
                      Times(Power(Sinh(v), p), Power(Cosh(w), q)), x), x),
                  And(IGtQ(m, C0), IGtQ(p, C0), IGtQ(q, C0),
                      Or(And(PolynomialQ(v, x), PolynomialQ(w, x)),
                          And(BinomialQ(List(v, w), x),
                              IndependentQ(Cancel(Times(v, Power(w, CN1))), x)))))),
          IIntegrate(5620, Integrate(Times(Sinh(v_), Power(Tanh(w_), n_DEFAULT)), x_Symbol),
              Condition(
                  Subtract(Integrate(Times(Cosh(v), Power(Tanh(w), Subtract(n, C1))), x),
                      Dist(Cosh(Subtract(v, w)),
                          Integrate(Times(Sech(w), Power(Tanh(w), Subtract(n, C1))), x), x)),
                  And(GtQ(n, C0), NeQ(w, v), FreeQ(Subtract(v, w), x)))));
}
