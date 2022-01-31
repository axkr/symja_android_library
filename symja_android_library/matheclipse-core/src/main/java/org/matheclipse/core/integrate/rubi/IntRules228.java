package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C3;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN2;
import static org.matheclipse.core.expression.F.Cancel;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Cos;
import static org.matheclipse.core.expression.F.Cot;
import static org.matheclipse.core.expression.F.Csc;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Module;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Part;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.PolynomialQ;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Sec;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Sin;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Tan;
import static org.matheclipse.core.expression.F.Times;
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
class IntRules228 {
  public static IAST RULES =
      List(
          IIntegrate(4561,
              Integrate(
                  Power(Sin(Times(a_DEFAULT, Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), CN1))),
                      n_DEFAULT),
                  x_Symbol),
              Condition(
                  Negate(
                      Dist(Power(d, CN1),
                          Subst(Integrate(Times(Power(Sin(Times(a, x)), n), Power(x, CN2)),
                              x), x, Power(Plus(c, Times(d, x)), CN1)),
                          x)),
                  And(FreeQ(List(a, c, d), x), IGtQ(n, C0)))),
          IIntegrate(4562,
              Integrate(
                  Power(Cos(Times(a_DEFAULT, Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), CN1))),
                      n_DEFAULT),
                  x_Symbol),
              Condition(
                  Negate(Dist(Power(d, CN1),
                      Subst(Integrate(Times(Power(Cos(Times(a, x)), n), Power(x, CN2)), x), x,
                          Power(Plus(c, Times(d, x)), CN1)),
                      x)),
                  And(FreeQ(List(a, c, d), x), IGtQ(n, C0)))),
          IIntegrate(4563,
              Integrate(
                  Power(
                      Sin(Times(e_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, x_)),
                          Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), CN1))),
                      n_DEFAULT),
                  x_Symbol),
              Condition(Negate(Dist(Power(d, CN1),
                  Subst(Integrate(Times(
                      Power(Sin(Subtract(Times(b, e, Power(d, CN1)),
                          Times(e, Subtract(Times(b, c), Times(a, d)), x, Power(d, CN1)))), n),
                      Power(x, CN2)), x), x, Power(Plus(c, Times(d, x)), CN1)),
                  x)), And(
                      FreeQ(List(a, b, c, d), x), IGtQ(n,
                          C0),
                      NeQ(Subtract(Times(b, c), Times(a, d)), C0)))),
          IIntegrate(4564,
              Integrate(
                  Power(
                      Cos(Times(
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
                                      Power(
                                          Cos(Subtract(Times(b, e, Power(d, CN1)),
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
              4565, Integrate(Power(Sin(u_),
                  n_DEFAULT), x_Symbol),
              Condition(
                  Module(List(Set($s("lst"), QuotientOfLinearsParts(u, x))),
                      Integrate(
                          Power(
                              Sin(Times(Plus(Part($s("lst"), C1), Times(Part($s("lst"), C2), x)),
                                  Power(Plus(Part($s("lst"), C3), Times(Part($s("lst"), C4), x)),
                                      CN1))),
                              n),
                          x)),
                  And(IGtQ(n, C0), QuotientOfLinearsQ(u, x)))),
          IIntegrate(4566, Integrate(Power(Cos(u_), n_DEFAULT), x_Symbol),
              Condition(Module(List(Set($s("lst"), QuotientOfLinearsParts(u, x))),
                  Integrate(Power(
                      Cos(Times(Plus(Part($s("lst"), C1), Times(Part($s("lst"), C2), x)),
                          Power(Plus(Part($s("lst"), C3), Times(Part($s("lst"), C4), x)), CN1))),
                      n), x)),
                  And(IGtQ(n, C0), QuotientOfLinearsQ(u, x)))),
          IIntegrate(4567,
              Integrate(Times(u_DEFAULT, Power(Sin(v_), p_DEFAULT),
                  Power(Sin(w_), q_DEFAULT)), x_Symbol),
              Condition(Integrate(Times(u, Power(Sin(v), Plus(p, q))), x), EqQ(w, v))),
          IIntegrate(4568,
              Integrate(Times(Power(Cos(v_), p_DEFAULT), Power(Cos(w_), q_DEFAULT),
                  u_DEFAULT), x_Symbol),
              Condition(Integrate(Times(u, Power(Cos(v), Plus(p, q))), x), EqQ(w, v))),
          IIntegrate(4569,
              Integrate(Times(Power(Sin(v_), p_DEFAULT), Power(Sin(w_), q_DEFAULT)), x_Symbol),
              Condition(
                  Integrate(ExpandTrigReduce(Times(Power(Sin(v), p), Power(Sin(w), q)),
                      x), x),
                  And(Or(
                      And(PolynomialQ(v, x), PolynomialQ(w, x)), And(
                          BinomialQ(List(v, w), x), IndependentQ(Cancel(Times(v, Power(w, CN1))),
                              x))),
                      IGtQ(p, C0), IGtQ(q, C0)))),
          IIntegrate(4570,
              Integrate(Times(Power(Cos(v_), p_DEFAULT), Power(Cos(w_), q_DEFAULT)), x_Symbol),
              Condition(
                  Integrate(ExpandTrigReduce(Times(Power(Cos(v), p), Power(Cos(w), q)), x), x),
                  And(Or(And(PolynomialQ(v, x), PolynomialQ(w, x)),
                      And(BinomialQ(List(v, w), x),
                          IndependentQ(Cancel(Times(v, Power(w, CN1))), x))),
                      IGtQ(p, C0), IGtQ(q, C0)))),
          IIntegrate(4571,
              Integrate(
                  Times(Power(x_, m_DEFAULT), Power(Sin(v_), p_DEFAULT),
                      Power(Sin(w_), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandTrigReduce(Power(x, m), Times(Power(Sin(v), p), Power(Sin(w), q)),
                          x),
                      x),
                  And(IGtQ(m, C0), IGtQ(p, C0), IGtQ(q, C0),
                      Or(And(PolynomialQ(v, x), PolynomialQ(w, x)),
                          And(BinomialQ(List(v, w), x),
                              IndependentQ(Cancel(Times(v, Power(w, CN1))), x)))))),
          IIntegrate(4572, Integrate(
              Times(Power(Cos(v_), p_DEFAULT), Power(Cos(w_), q_DEFAULT), Power(x_, m_DEFAULT)),
              x_Symbol),
              Condition(Integrate(
                  ExpandTrigReduce(Power(x, m), Times(Power(Cos(v), p), Power(Cos(w), q)), x), x),
                  And(IGtQ(m, C0), IGtQ(p, C0), IGtQ(q, C0),
                      Or(And(PolynomialQ(v, x), PolynomialQ(w, x)),
                          And(BinomialQ(List(v, w), x),
                              IndependentQ(Cancel(Times(v, Power(w, CN1))), x)))))),
          IIntegrate(4573,
              Integrate(Times(Power(Cos(w_), p_DEFAULT), u_DEFAULT, Power(Sin(v_), p_DEFAULT)),
                  x_Symbol),
              Condition(Dist(Power(Power(C2, p), CN1),
                  Integrate(Times(u, Power(Sin(Times(C2, v)), p)), x), x),
                  And(EqQ(w, v), IntegerQ(p)))),
          IIntegrate(4574,
              Integrate(Times(Power(Cos(w_), q_DEFAULT),
                  Power(Sin(v_), p_DEFAULT)), x_Symbol),
              Condition(
                  Integrate(ExpandTrigReduce(Times(Power(Sin(v), p), Power(Cos(w), q)), x), x),
                  And(IGtQ(p, C0), IGtQ(q, C0),
                      Or(And(PolynomialQ(v, x), PolynomialQ(w, x)),
                          And(BinomialQ(List(v, w), x),
                              IndependentQ(Cancel(Times(v, Power(w, CN1))), x)))))),
          IIntegrate(4575,
              Integrate(
                  Times(Power(Cos(
                      w_), q_DEFAULT), Power(x_,
                          m_DEFAULT),
                      Power(Sin(v_), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandTrigReduce(Power(x, m), Times(Power(Sin(v), p), Power(Cos(w), q)),
                          x),
                      x),
                  And(IGtQ(m, C0), IGtQ(p, C0), IGtQ(q, C0),
                      Or(And(PolynomialQ(v, x), PolynomialQ(w, x)),
                          And(BinomialQ(List(v, w), x),
                              IndependentQ(Cancel(Times(v, Power(w, CN1))), x)))))),
          IIntegrate(
              4576, Integrate(Times(Sin(v_),
                  Power(Tan(w_), n_DEFAULT)), x_Symbol),
              Condition(Plus(Negate(Integrate(Times(Cos(v), Power(Tan(w), Subtract(n, C1))), x)),
                  Dist(Cos(Subtract(v, w)),
                      Integrate(Times(Sec(w), Power(Tan(w), Subtract(n, C1))), x), x)),
                  And(GtQ(n, C0), FreeQ(Subtract(v, w), x), NeQ(w, v)))),
          IIntegrate(4577, Integrate(Times(Cos(v_), Power(Cot(w_), n_DEFAULT)), x_Symbol),
              Condition(Plus(Negate(Integrate(Times(Sin(v), Power(Cot(w), Subtract(n, C1))), x)),
                  Dist(Cos(Subtract(v, w)),
                      Integrate(Times(Csc(w), Power(Cot(w), Subtract(n, C1))), x), x)),
                  And(GtQ(n, C0), FreeQ(Subtract(v, w), x), NeQ(w, v)))),
          IIntegrate(4578, Integrate(Times(Power(Cot(w_), n_DEFAULT), Sin(v_)), x_Symbol),
              Condition(
                  Plus(Integrate(Times(Cos(v), Power(Cot(w), Subtract(n, C1))), x),
                      Dist(Sin(Subtract(v, w)),
                          Integrate(Times(Csc(w), Power(Cot(w), Subtract(n, C1))), x), x)),
                  And(GtQ(n, C0), FreeQ(Subtract(v, w), x), NeQ(w, v)))),
          IIntegrate(
              4579, Integrate(Times(Cos(v_),
                  Power(Tan(w_), n_DEFAULT)), x_Symbol),
              Condition(Subtract(Integrate(Times(Sin(v), Power(Tan(w), Subtract(n, C1))), x),
                  Dist(Sin(Subtract(v, w)),
                      Integrate(Times(Sec(w), Power(Tan(w), Subtract(n, C1))), x), x)),
                  And(GtQ(n, C0), FreeQ(Subtract(v, w), x), NeQ(w, v)))),
          IIntegrate(4580, Integrate(Times(Power(Sec(w_), n_DEFAULT), Sin(v_)), x_Symbol),
              Condition(
                  Plus(
                      Dist(Cos(Subtract(v, w)),
                          Integrate(Times(Tan(w), Power(Sec(w), Subtract(n, C1))), x), x),
                      Dist(Sin(Subtract(v, w)), Integrate(Power(Sec(w), Subtract(n, C1)), x), x)),
                  And(GtQ(n, C0), FreeQ(Subtract(v, w), x), NeQ(w, v)))));
}
