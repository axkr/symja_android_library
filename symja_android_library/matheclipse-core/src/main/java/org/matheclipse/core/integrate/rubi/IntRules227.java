package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CI;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Cos;
import static org.matheclipse.core.expression.F.Cot;
import static org.matheclipse.core.expression.F.Csc;
import static org.matheclipse.core.expression.F.Exp;
import static org.matheclipse.core.expression.F.F_;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.G_;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Sec;
import static org.matheclipse.core.expression.F.Sin;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Tan;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.f_DEFAULT;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_DEFAULT;
import static org.matheclipse.core.expression.F.q_DEFAULT;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.FSymbol;
import static org.matheclipse.core.expression.S.GSymbol;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.d;
import static org.matheclipse.core.expression.S.e;
import static org.matheclipse.core.expression.S.f;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.q;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandIntegrand;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.F;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.G;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntegersQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.TrigQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Unintegrable;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules227 {
  public static IAST RULES =
      List(
          IIntegrate(4541,
              Integrate(
                  Times(Power(Cos(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), p_DEFAULT),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT),
                      Power(
                          Plus(a_, Times(b_DEFAULT,
                              Sin(Plus(c_DEFAULT, Times(d_DEFAULT, x_))))),
                          CN1),
                      Power(Tan(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Power(b, CN1),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), m),
                                  Power(Cos(Plus(c, Times(d, x))), Subtract(p, C1)),
                                  Power(Tan(Plus(c, Times(d, x))), Subtract(n, C1))),
                              x),
                          x),
                      Dist(
                          Times(a, Power(b,
                              CN1)),
                          Integrate(Times(Power(Plus(e, Times(f, x)), m),
                              Power(Cos(Plus(c, Times(d, x))), Subtract(p, C1)),
                              Power(Tan(Plus(c, Times(d, x))), Subtract(n, C1)), Power(
                                  Plus(a, Times(b, Sin(Plus(c, Times(d, x))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), IGtQ(m, C0), IGtQ(n, C0), IGtQ(p, C0)))),
          IIntegrate(4542,
              Integrate(
                  Times(Power(Cot(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), n_DEFAULT),
                      Power(Plus(Times(Cos(Plus(c_DEFAULT, Times(d_DEFAULT,
                          x_))), b_DEFAULT), a_), CN1),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT), Power(Sin(
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(Dist(Power(b, CN1),
                      Integrate(Times(Power(Plus(e, Times(f, x)), m),
                          Power(Sin(Plus(c, Times(d, x))), Subtract(p, C1)),
                          Power(Cot(Plus(c, Times(d, x))), Subtract(n, C1))), x),
                      x),
                      Dist(Times(a, Power(b, CN1)),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), m),
                                  Power(Sin(Plus(c, Times(d, x))), Subtract(p, C1)),
                                  Power(Cot(Plus(c, Times(d, x))), Subtract(n, C1)),
                                  Power(Plus(a, Times(b, Cos(Plus(c, Times(d, x))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), IGtQ(m, C0), IGtQ(n, C0), IGtQ(p, C0)))),
          IIntegrate(4543,
              Integrate(
                  Times(
                      Power(Cos(Plus(c_DEFAULT, Times(d_DEFAULT,
                          x_))), p_DEFAULT),
                      Power(Cot(Plus(c_DEFAULT,
                          Times(d_DEFAULT, x_))), n_DEFAULT),
                      Power(Plus(e_DEFAULT,
                          Times(f_DEFAULT, x_)), m_DEFAULT),
                      Power(
                          Plus(a_, Times(b_DEFAULT, Sin(
                              Plus(c_DEFAULT, Times(d_DEFAULT, x_))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Power(a, CN1),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), m),
                                  Power(Cos(Plus(c, Times(d, x))), p),
                                  Power(Cot(Plus(c, Times(d, x))), n)),
                              x),
                          x),
                      Dist(
                          Times(b, Power(a,
                              CN1)),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), m),
                                  Power(Cos(Plus(c, Times(d, x))), Plus(p, C1)),
                                  Power(Cot(Plus(c,
                                      Times(d, x))), Subtract(n,
                                          C1)),
                                  Power(Plus(a, Times(b, Sin(Plus(c, Times(d, x))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), IGtQ(m, C0), IGtQ(n, C0), IGtQ(p, C0)))),
          IIntegrate(4544,
              Integrate(
                  Times(
                      Power(Plus(
                          Times(Cos(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), b_DEFAULT), a_), CN1),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT),
                      Power(Sin(Plus(c_DEFAULT, Times(d_DEFAULT,
                          x_))), p_DEFAULT),
                      Power(Tan(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Power(a, CN1),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), m),
                                  Power(Sin(Plus(c, Times(d, x))), p),
                                  Power(Tan(Plus(c, Times(d, x))), n)),
                              x),
                          x),
                      Dist(
                          Times(b, Power(a,
                              CN1)),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), m),
                                  Power(Sin(Plus(c, Times(d, x))), Plus(p, C1)),
                                  Power(Tan(Plus(c, Times(d, x))), Subtract(n, C1)),
                                  Power(Plus(a, Times(b, Cos(Plus(c, Times(d, x))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), IGtQ(m, C0), IGtQ(n, C0), IGtQ(p, C0)))),
          IIntegrate(4545,
              Integrate(
                  Times(
                      Power(Cos(Plus(c_DEFAULT, Times(d_DEFAULT,
                          x_))), p_DEFAULT),
                      Power(Csc(Plus(c_DEFAULT,
                          Times(d_DEFAULT, x_))), n_DEFAULT),
                      Power(Plus(e_DEFAULT,
                          Times(f_DEFAULT, x_)), m_DEFAULT),
                      Power(
                          Plus(a_, Times(b_DEFAULT, Sin(
                              Plus(c_DEFAULT, Times(d_DEFAULT, x_))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Power(a, CN1),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), m),
                                  Power(Cos(Plus(c, Times(d, x))), p),
                                  Power(Csc(Plus(c, Times(d, x))), n)),
                              x),
                          x),
                      Dist(
                          Times(b, Power(a,
                              CN1)),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), m),
                                  Power(Cos(Plus(c, Times(d, x))), p),
                                  Power(Csc(Plus(c, Times(d, x))), Subtract(n,
                                      C1)),
                                  Power(Plus(a, Times(b, Sin(Plus(c, Times(d, x))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), IGtQ(m, C0), IGtQ(n, C0), IGtQ(p, C0)))),
          IIntegrate(4546,
              Integrate(
                  Times(
                      Power(Plus(
                          Times(Cos(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), b_DEFAULT), a_), CN1),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT),
                      Power(Sec(Plus(c_DEFAULT,
                          Times(d_DEFAULT, x_))), n_DEFAULT),
                      Power(Sin(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Power(a, CN1),
                          Integrate(Times(Power(Plus(e, Times(f, x)), m),
                              Power(Sin(Plus(c, Times(d, x))), p), Power(Sec(Plus(c, Times(d, x))),
                                  n)),
                              x),
                          x),
                      Dist(Times(b, Power(a, CN1)), Integrate(
                          Times(Power(Plus(e, Times(f, x)), m), Power(Sin(Plus(c, Times(d, x))), p),
                              Power(Sec(Plus(c, Times(d, x))), Subtract(n, C1)),
                              Power(Plus(a, Times(b, Cos(Plus(c, Times(d, x))))), CN1)),
                          x), x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), IGtQ(m, C0), IGtQ(n, C0), IGtQ(p, C0)))),
          IIntegrate(4547,
              Integrate(
                  Times(Power(Cos(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), p_DEFAULT),
                      Power(Plus(e_DEFAULT,
                          Times(f_DEFAULT, x_)), m_DEFAULT),
                      Power(
                          Plus(a_, Times(b_DEFAULT,
                              Sin(Plus(c_DEFAULT, Times(d_DEFAULT, x_))))),
                          CN1),
                      Power($(F_, Plus(c_DEFAULT, Times(d_DEFAULT, x_))), n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Unintegrable(
                      Times(Power(Plus(e, Times(f, x)), m), Power(Cos(Plus(c, Times(d, x))), p),
                          Power(F(Plus(c, Times(d, x))), n), Power(Plus(a,
                              Times(b, Sin(Plus(c, Times(d, x))))), CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, n, p), x), TrigQ(FSymbol)))),
          IIntegrate(4548,
              Integrate(
                  Times(
                      Power(
                          Plus(Times(Cos(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), b_DEFAULT), a_),
                          CN1),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT),
                      Power(Sin(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), p_DEFAULT),
                      Power($(F_, Plus(c_DEFAULT, Times(d_DEFAULT, x_))), n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Unintegrable(
                      Times(Power(Plus(e, Times(f, x)), m), Power(F(Plus(c, Times(d, x))), n),
                          Power(Sin(Plus(c, Times(d, x))), p), Power(
                              Plus(a, Times(b, Cos(Plus(c, Times(d, x))))), CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, n), x), TrigQ(FSymbol)))),
          IIntegrate(4549,
              Integrate(
                  Times(Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT),
                      Power(
                          Plus(a_, Times(b_DEFAULT,
                              Sec(Plus(c_DEFAULT, Times(d_DEFAULT, x_))))),
                          CN1),
                      Power($(F_, Plus(c_DEFAULT, Times(d_DEFAULT, x_))), n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(Power(Plus(e, Times(f, x)), m), Cos(Plus(c, Times(d, x))),
                          Power(F(Plus(c, Times(d, x))), n), Power(
                              Plus(b, Times(a, Cos(Plus(c, Times(d, x))))), CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f), x), TrigQ(FSymbol), IntegersQ(m, n)))),
          IIntegrate(4550,
              Integrate(
                  Times(
                      Power(Plus(
                          Times(Csc(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), b_DEFAULT), a_), CN1),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT,
                          x_)), m_DEFAULT),
                      Power($(F_, Plus(c_DEFAULT, Times(d_DEFAULT, x_))), n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(Power(Plus(e, Times(f, x)), m), Sin(Plus(c, Times(d, x))),
                          Power(F(Plus(c, Times(d, x))), n), Power(
                              Plus(b, Times(a, Sin(Plus(c, Times(d, x))))), CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f), x), TrigQ(FSymbol), IntegersQ(m, n)))),
          IIntegrate(4551,
              Integrate(
                  Times(Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT),
                      Power(Plus(a_,
                          Times(b_DEFAULT, Sec(Plus(c_DEFAULT, Times(d_DEFAULT, x_))))), CN1),
                      Power($(F_, Plus(c_DEFAULT,
                          Times(d_DEFAULT, x_))), n_DEFAULT),
                      Power($(G_, Plus(c_DEFAULT, Times(d_DEFAULT, x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(Power(Plus(e, Times(f, x)), m), Cos(Plus(c, Times(d, x))),
                          Power(F(Plus(c,
                              Times(d, x))), n),
                          Power(G(Plus(c, Times(d, x))), p), Power(
                              Plus(b, Times(a, Cos(Plus(c, Times(d, x))))), CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f), x), TrigQ(FSymbol), TrigQ(GSymbol),
                      IntegersQ(m, n, p)))),
          IIntegrate(4552,
              Integrate(
                  Times(Power(
                      Plus(Times(Csc(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), b_DEFAULT), a_), CN1),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT),
                      Power($(F_, Plus(c_DEFAULT, Times(d_DEFAULT, x_))), n_DEFAULT), Power($(G_,
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(Integrate(Times(Power(Plus(e, Times(f, x)), m), Sin(Plus(c, Times(d, x))),
                  Power(F(Plus(c, Times(d, x))), n), Power(G(Plus(c, Times(d, x))),
                      p),
                  Power(Plus(b, Times(a, Sin(Plus(c, Times(d, x))))), CN1)), x), And(
                      FreeQ(List(a, b, c, d, e,
                          f), x),
                      TrigQ(FSymbol), TrigQ(GSymbol), IntegersQ(m, n, p)))),
          IIntegrate(4553,
              Integrate(
                  Times(
                      Power(Sin(Plus(a_DEFAULT,
                          Times(b_DEFAULT, x_))), p_DEFAULT),
                      Power(Sin(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(Power(C2, Plus(p, q)), CN1),
                      Integrate(ExpandIntegrand(
                          Power(
                              Subtract(Times(CI, Power(Exp(Times(CI, Plus(c, Times(d, x)))), CN1)),
                                  Times(CI, Exp(Times(CI, Plus(c, Times(d, x)))))),
                              q),
                          Power(
                              Subtract(Times(CI, Power(Exp(Times(CI, Plus(a, Times(b, x)))), CN1)),
                                  Times(CI, Exp(Times(CI, Plus(a, Times(b, x)))))),
                              p),
                          x), x),
                      x),
                  And(FreeQ(List(a, b, c, d, q), x), IGtQ(p, C0), Not(IntegerQ(q))))),
          IIntegrate(4554,
              Integrate(Times(Power(Cos(Plus(a_DEFAULT, Times(b_DEFAULT, x_))), p_DEFAULT),
                  Power(Cos(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), q_DEFAULT)), x_Symbol),
              Condition(
                  Dist(Power(Power(C2, Plus(p, q)), CN1),
                      Integrate(
                          ExpandIntegrand(
                              Power(Plus(Exp(Times(CN1, CI, Plus(c, Times(d, x)))),
                                  Exp(Times(CI, Plus(c, Times(d, x))))), q),
                              Power(Plus(Exp(Times(CN1, CI, Plus(a, Times(b, x)))),
                                  Exp(Times(CI, Plus(a, Times(b, x))))), p),
                              x),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, q), x), IGtQ(p, C0), Not(IntegerQ(q))))),
          IIntegrate(4555,
              Integrate(
                  Times(
                      Power(Cos(Plus(c_DEFAULT,
                          Times(d_DEFAULT, x_))), q_DEFAULT),
                      Power(Sin(Plus(a_DEFAULT, Times(b_DEFAULT, x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Power(Power(C2,
                          Plus(p, q)), CN1),
                      Integrate(
                          ExpandIntegrand(
                              Power(Plus(Exp(Times(CN1, CI, Plus(c, Times(d, x)))),
                                  Exp(Times(CI, Plus(c, Times(d, x))))), q),
                              Power(
                                  Subtract(Times(CI, Power(Exp(
                                      Times(CI, Plus(a, Times(b, x)))), CN1)), Times(CI, Exp(
                                          Times(CI, Plus(a, Times(b, x)))))),
                                  p),
                              x),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, q), x), IGtQ(p, C0), Not(IntegerQ(q))))),
          IIntegrate(4556,
              Integrate(
                  Times(
                      Power(Cos(Plus(a_DEFAULT, Times(b_DEFAULT, x_))), p_DEFAULT), Power(Sin(
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Power(Power(C2,
                          Plus(p, q)), CN1),
                      Integrate(
                          ExpandIntegrand(
                              Power(
                                  Subtract(
                                      Times(CI, Power(Exp(Times(CI, Plus(c, Times(d, x)))), CN1)),
                                      Times(CI, Exp(Times(CI, Plus(c, Times(d, x)))))),
                                  q),
                              Power(
                                  Plus(Exp(Times(CN1, CI, Plus(a, Times(b, x)))),
                                      Exp(Times(CI, Plus(a, Times(b, x))))),
                                  p),
                              x),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, q), x), IGtQ(p, C0), Not(IntegerQ(q))))),
          IIntegrate(4557,
              Integrate(
                  Times(
                      Sin(Plus(a_DEFAULT,
                          Times(b_DEFAULT, x_))),
                      Tan(Plus(c_DEFAULT, Times(d_DEFAULT, x_)))),
                  x_Symbol),
              Condition(
                  Integrate(
                      Plus(Power(Times(Exp(Times(CI, Plus(a, Times(b, x)))), C2), CN1),
                          Times(CN1, C1D2, Exp(
                              Times(CI, Plus(a, Times(b, x))))),
                          Negate(
                              Power(Times(Exp(Times(CI, Plus(a, Times(b, x)))),
                                  Plus(C1, Exp(Times(C2, CI, Plus(c, Times(d, x)))))), CN1)),
                          Times(
                              Exp(Times(CI, Plus(a, Times(b, x)))), Power(Plus(C1,
                                  Exp(Times(C2, CI, Plus(c, Times(d, x))))), CN1))),
                      x),
                  And(FreeQ(List(a, b, c, d), x), NeQ(Subtract(Sqr(b), Sqr(d)), C0)))),
          IIntegrate(4558,
              Integrate(
                  Times(
                      Cos(Plus(a_DEFAULT, Times(b_DEFAULT, x_))), Cot(
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_)))),
                  x_Symbol),
              Condition(
                  Integrate(Subtract(
                      Subtract(
                          Plus(
                              Times(CI,
                                  Power(Times(Exp(Times(CI, Plus(a, Times(b, x)))), C2), CN1)),
                              Times(C1D2, CI, Exp(Times(CI, Plus(a, Times(b, x)))))),
                          Times(CI,
                              Power(
                                  Times(Exp(Times(CI, Plus(a, Times(b, x)))),
                                      Subtract(C1, Exp(Times(C2, CI, Plus(c, Times(d, x)))))),
                                  CN1))),
                      Times(
                          CI, Exp(Times(CI, Plus(a, Times(b, x)))), Power(Subtract(C1,
                              Exp(Times(C2, CI, Plus(c, Times(d, x))))), CN1))),
                      x),
                  And(FreeQ(List(a, b, c, d), x), NeQ(Subtract(Sqr(b), Sqr(d)), C0)))),
          IIntegrate(4559,
              Integrate(
                  Times(
                      Cot(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), Sin(
                          Plus(a_DEFAULT, Times(b_DEFAULT, x_)))),
                  x_Symbol),
              Condition(
                  Integrate(Subtract(
                      Plus(Negate(Power(Times(Exp(Times(CI, Plus(a, Times(b, x)))), C2), CN1)),
                          Times(C1D2, Exp(Times(CI, Plus(a, Times(b, x))))),
                          Power(
                              Times(Exp(Times(CI, Plus(a, Times(b, x)))),
                                  Subtract(C1, Exp(Times(C2, CI, Plus(c, Times(d, x)))))),
                              CN1)),
                      Times(
                          Exp(Times(CI, Plus(a,
                              Times(b, x)))),
                          Power(Subtract(C1, Exp(Times(C2, CI, Plus(c, Times(d, x))))), CN1))),
                      x),
                  And(FreeQ(List(a, b, c, d), x), NeQ(Subtract(Sqr(b), Sqr(d)), C0)))),
          IIntegrate(4560,
              Integrate(
                  Times(
                      Cos(Plus(a_DEFAULT,
                          Times(b_DEFAULT, x_))),
                      Tan(Plus(c_DEFAULT, Times(d_DEFAULT, x_)))),
                  x_Symbol),
              Condition(
                  Integrate(Plus(
                      Times(CN1, CI, Power(Times(Exp(Times(CI, Plus(a, Times(b, x)))), C2), CN1)),
                      Times(CN1, C1D2, CI, Exp(Times(CI, Plus(a, Times(b, x))))),
                      Times(CI,
                          Power(Times(Exp(Times(CI, Plus(a, Times(b, x)))),
                              Plus(C1, Exp(Times(C2, CI, Plus(c, Times(d, x)))))), CN1)),
                      Times(CI, Exp(Times(CI, Plus(a, Times(b, x)))),
                          Power(Plus(C1, Exp(Times(C2, CI, Plus(c, Times(d, x))))), CN1))),
                      x),
                  And(FreeQ(List(a, b, c, d), x), NeQ(Subtract(Sqr(b), Sqr(d)), C0)))));
}
