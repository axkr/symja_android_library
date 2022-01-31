package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C3;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN2;
import static org.matheclipse.core.expression.F.Cancel;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Cosh;
import static org.matheclipse.core.expression.F.Coth;
import static org.matheclipse.core.expression.F.Exp;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Part;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.PolynomialQ;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Sech;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Sinh;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Tanh;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_DEFAULT;
import static org.matheclipse.core.expression.F.q_DEFAULT;
import static org.matheclipse.core.expression.F.u_;
import static org.matheclipse.core.expression.F.u_DEFAULT;
import static org.matheclipse.core.expression.F.v_;
import static org.matheclipse.core.expression.F.w_;
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
import static org.matheclipse.core.expression.S.q;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.v;
import static org.matheclipse.core.expression.S.w;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.BinomialQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandTrigReduce;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IndependentQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.QuotientOfLinearsParts;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.QuotientOfLinearsQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Subst;
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
