package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Cosh;
import static org.matheclipse.core.expression.F.Coth;
import static org.matheclipse.core.expression.F.Csch;
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
import static org.matheclipse.core.expression.F.Sech;
import static org.matheclipse.core.expression.F.Sinh;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Tanh;
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
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.HyperbolicQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntegersQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Unintegrable;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules279 {
  public static IAST RULES =
      List(
          IIntegrate(5581,
              Integrate(
                  Times(
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT,
                          x_)), m_DEFAULT),
                      Power(Sinh(
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_))), p_DEFAULT),
                      Power(Plus(a_, Times(b_DEFAULT,
                          Sinh(Plus(c_DEFAULT, Times(d_DEFAULT, x_))))), CN1),
                      Power(Tanh(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Power(b, CN1),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), m),
                                  Power(Sinh(Plus(c, Times(d, x))), Subtract(p, C1)),
                                  Power(Tanh(Plus(c, Times(d, x))), n)),
                              x),
                          x),
                      Dist(
                          Times(a, Power(b,
                              CN1)),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), m),
                                  Power(Sinh(Plus(c, Times(d,
                                      x))), Subtract(p, C1)),
                                  Power(Tanh(Plus(c, Times(d, x))), n),
                                  Power(Plus(a, Times(b, Sinh(Plus(c, Times(d, x))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), IGtQ(m, C0), IGtQ(n, C0), IGtQ(p, C0)))),
          IIntegrate(5582,
              Integrate(
                  Times(
                      Power(Cosh(Plus(c_DEFAULT,
                          Times(d_DEFAULT, x_))), p_DEFAULT),
                      Power(Coth(
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_))), n_DEFAULT),
                      Power(
                          Plus(Times(Cosh(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), b_DEFAULT),
                              a_),
                          CN1),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Power(b, CN1),
                          Integrate(Times(Power(Plus(e, Times(f, x)), m),
                              Power(Cosh(Plus(c, Times(d, x))), Subtract(p, C1)),
                              Power(Coth(Plus(c, Times(d, x))), n)), x),
                          x),
                      Dist(Times(a, Power(b, CN1)),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), m),
                                  Power(Cosh(Plus(c, Times(d, x))), Subtract(p, C1)),
                                  Power(Coth(Plus(c, Times(d, x))), n),
                                  Power(Plus(a, Times(b, Cosh(Plus(c, Times(d, x))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), IGtQ(m, C0), IGtQ(n, C0), IGtQ(p, C0)))),
          IIntegrate(5583,
              Integrate(
                  Times(Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT),
                      Power(Sech(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), p_DEFAULT),
                      Power(
                          Plus(a_, Times(b_DEFAULT,
                              Sinh(Plus(c_DEFAULT, Times(d_DEFAULT, x_))))),
                          CN1),
                      Power(Tanh(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Power(b, CN1),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), m),
                                  Power(Sech(Plus(c, Times(d, x))), Plus(p, C1)),
                                  Power(Tanh(Plus(c, Times(d, x))), Subtract(n, C1))),
                              x),
                          x),
                      Dist(Times(a, Power(b, CN1)),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), m),
                                  Power(Sech(Plus(c, Times(d, x))), Plus(p, C1)),
                                  Power(Tanh(Plus(c, Times(d, x))), Subtract(n, C1)),
                                  Power(Plus(a, Times(b, Sinh(Plus(c, Times(d, x))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), IGtQ(m, C0), IGtQ(n, C0), IGtQ(p, C0)))),
          IIntegrate(5584,
              Integrate(
                  Times(Power(Coth(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), n_DEFAULT),
                      Power(Csch(
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_))), p_DEFAULT),
                      Power(
                          Plus(Times(Cosh(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), b_DEFAULT),
                              a_),
                          CN1),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Power(b, CN1),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), m),
                                  Power(Csch(Plus(c, Times(d, x))), Plus(p, C1)),
                                  Power(Coth(Plus(c, Times(d, x))), Subtract(n, C1))),
                              x),
                          x),
                      Dist(Times(a, Power(b, CN1)),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), m),
                                  Power(Csch(Plus(c, Times(d, x))), Plus(p, C1)),
                                  Power(Coth(Plus(c, Times(d, x))), Subtract(n,
                                      C1)),
                                  Power(Plus(a, Times(b, Cosh(Plus(c, Times(d, x))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), IGtQ(m, C0), IGtQ(n, C0), IGtQ(p, C0)))),
          IIntegrate(5585,
              Integrate(
                  Times(
                      Power(Cosh(Plus(c_DEFAULT, Times(d_DEFAULT,
                          x_))), p_DEFAULT),
                      Power(Coth(Plus(c_DEFAULT,
                          Times(d_DEFAULT, x_))), n_DEFAULT),
                      Power(Plus(e_DEFAULT,
                          Times(f_DEFAULT, x_)), m_DEFAULT),
                      Power(
                          Plus(a_, Times(b_DEFAULT, Sinh(
                              Plus(c_DEFAULT, Times(d_DEFAULT, x_))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Power(a, CN1),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), m),
                                  Power(Cosh(Plus(c, Times(d, x))), p),
                                  Power(Coth(Plus(c, Times(d, x))), n)),
                              x),
                          x),
                      Dist(
                          Times(b, Power(a,
                              CN1)),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), m),
                                  Power(Cosh(Plus(c, Times(d, x))), Plus(p, C1)),
                                  Power(Coth(Plus(c, Times(d, x))), Subtract(n, C1)),
                                  Power(Plus(a, Times(b, Sinh(Plus(c, Times(d, x))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), IGtQ(m, C0), IGtQ(n, C0), IGtQ(p, C0)))),
          IIntegrate(5586,
              Integrate(
                  Times(
                      Power(Plus(
                          Times(Cosh(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), b_DEFAULT), a_), CN1),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT),
                      Power(Sinh(Plus(c_DEFAULT,
                          Times(d_DEFAULT, x_))), p_DEFAULT),
                      Power(Tanh(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Power(a, CN1),
                          Integrate(Times(Power(Plus(e, Times(f, x)), m),
                              Power(Sinh(Plus(c, Times(d, x))),
                                  p),
                              Power(Tanh(Plus(c, Times(d, x))), n)), x),
                          x),
                      Dist(
                          Times(b, Power(a,
                              CN1)),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), m),
                                  Power(Sinh(Plus(c, Times(d, x))), Plus(p, C1)),
                                  Power(Tanh(Plus(c, Times(d, x))), Subtract(n, C1)), Power(Plus(a,
                                      Times(b, Cosh(Plus(c, Times(d, x))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), IGtQ(m, C0), IGtQ(n, C0), IGtQ(p, C0)))),
          IIntegrate(5587,
              Integrate(
                  Times(Power(Coth(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), n_DEFAULT),
                      Power(Csch(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), p_DEFAULT),
                      Power(Plus(e_DEFAULT,
                          Times(f_DEFAULT, x_)), m_DEFAULT),
                      Power(
                          Plus(a_, Times(b_DEFAULT, Sinh(
                              Plus(c_DEFAULT, Times(d_DEFAULT, x_))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Power(a, CN1),
                          Integrate(Times(Power(Plus(e, Times(f, x)), m),
                              Power(Csch(Plus(c, Times(d, x))),
                                  p),
                              Power(Coth(Plus(c, Times(d, x))), n)), x),
                          x),
                      Dist(
                          Times(b, Power(a,
                              CN1)),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), m),
                                  Power(Csch(Plus(c, Times(d, x))), Subtract(p, C1)),
                                  Power(Coth(Plus(c, Times(d, x))), n),
                                  Power(Plus(a, Times(b, Sinh(Plus(c, Times(d, x))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), IGtQ(m, C0), IGtQ(n, C0), IGtQ(p, C0)))),
          IIntegrate(5588,
              Integrate(
                  Times(
                      Power(Plus(
                          Times(Cosh(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), b_DEFAULT), a_), CN1),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT),
                      Power(Sech(Plus(c_DEFAULT,
                          Times(d_DEFAULT, x_))), p_DEFAULT),
                      Power(Tanh(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Power(a, CN1),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), m),
                                  Power(Sech(Plus(c, Times(d, x))), p),
                                  Power(Tanh(Plus(c, Times(d, x))), n)),
                              x),
                          x),
                      Dist(Times(b, Power(a, CN1)),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), m),
                                  Power(Sech(Plus(c, Times(d, x))), Subtract(p, C1)),
                                  Power(Tanh(Plus(c, Times(d, x))), n), Power(Plus(a,
                                      Times(b, Cosh(Plus(c, Times(d, x))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), IGtQ(m, C0), IGtQ(n, C0), IGtQ(p, C0)))),
          IIntegrate(5589,
              Integrate(
                  Times(
                      Power(Csch(Plus(c_DEFAULT, Times(d_DEFAULT,
                          x_))), n_DEFAULT),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT,
                          x_)), m_DEFAULT),
                      Power(Sech(
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_))), p_DEFAULT),
                      Power(Plus(a_, Times(b_DEFAULT, Sinh(Plus(c_DEFAULT, Times(d_DEFAULT, x_))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Power(a, CN1),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), m),
                                  Power(Sech(
                                      Plus(c, Times(d, x))), p),
                                  Power(Csch(Plus(c, Times(d, x))), n)),
                              x),
                          x),
                      Dist(Times(b, Power(a, CN1)), Integrate(Times(Power(Plus(e, Times(f, x)), m),
                          Power(Sech(Plus(c, Times(d, x))), p),
                          Power(Csch(Plus(c, Times(d, x))), Subtract(n, C1)), Power(
                              Plus(a, Times(b, Sinh(Plus(c, Times(d, x))))), CN1)),
                          x), x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), IGtQ(m, C0), IGtQ(n, C0), IGtQ(p, C0)))),
          IIntegrate(5590,
              Integrate(
                  Times(
                      Power(Csch(
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_))), p_DEFAULT),
                      Power(Plus(
                          Times(Cosh(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), b_DEFAULT), a_), CN1),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT,
                          x_)), m_DEFAULT),
                      Power(Sech(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Power(a, CN1),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), m),
                                  Power(Csch(Plus(c, Times(d, x))), p),
                                  Power(Sech(Plus(c, Times(d, x))), n)),
                              x),
                          x),
                      Dist(Times(b, Power(a, CN1)),
                          Integrate(Times(Power(Plus(e, Times(f, x)), m),
                              Power(Csch(Plus(c, Times(d, x))), p),
                              Power(Sech(Plus(c, Times(d, x))), Subtract(n, C1)), Power(Plus(a,
                                  Times(b, Cosh(Plus(c, Times(d, x))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), IGtQ(m, C0), IGtQ(n, C0), IGtQ(p, C0)))),
          IIntegrate(5591,
              Integrate(
                  Times(Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT),
                      Power(Plus(a_,
                          Times(b_DEFAULT, Sinh(Plus(c_DEFAULT, Times(d_DEFAULT, x_))))), CN1),
                      Power($(F_, Plus(c_DEFAULT,
                          Times(d_DEFAULT, x_))), n_DEFAULT),
                      Power($(G_, Plus(c_DEFAULT, Times(d_DEFAULT, x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Unintegrable(
                      Times(Power(Plus(e, Times(f, x)), m), Power(F(Plus(c, Times(d, x))), n),
                          Power(G(
                              Plus(c, Times(d, x))), p),
                          Power(Plus(a, Times(b, Sinh(Plus(c, Times(d, x))))), CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, n, p), x), HyperbolicQ(FSymbol),
                      HyperbolicQ(GSymbol)))),
          IIntegrate(5592,
              Integrate(
                  Times(
                      Power(Plus(
                          Times(Cosh(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), b_DEFAULT), a_), CN1),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT),
                      Power($(F_, Plus(c_DEFAULT,
                          Times(d_DEFAULT, x_))), n_DEFAULT),
                      Power($(G_, Plus(c_DEFAULT, Times(d_DEFAULT, x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Unintegrable(Times(Power(Plus(e, Times(f, x)), m),
                      Power(F(Plus(c, Times(d, x))), n), Power(G(Plus(c, Times(d, x))),
                          p),
                      Power(Plus(a, Times(b, Cosh(Plus(c, Times(d, x))))), CN1)), x),
                  And(FreeQ(List(a, b, c, d, e, f, m, n,
                      p), x), HyperbolicQ(
                          FSymbol),
                      HyperbolicQ(GSymbol)))),
          IIntegrate(5593,
              Integrate(
                  Times(Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT),
                      Power(
                          Plus(a_, Times(b_DEFAULT,
                              Sech(Plus(c_DEFAULT, Times(d_DEFAULT, x_))))),
                          CN1),
                      Power($(F_, Plus(c_DEFAULT, Times(d_DEFAULT, x_))), n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(Power(Plus(e, Times(f, x)), m), Cosh(Plus(c, Times(d, x))),
                          Power(F(Plus(c, Times(d, x))), n), Power(
                              Plus(b, Times(a, Cosh(Plus(c, Times(d, x))))), CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f), x), HyperbolicQ(FSymbol), IntegersQ(m, n)))),
          IIntegrate(5594,
              Integrate(Times(
                  Power(Plus(Times(Csch(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), b_DEFAULT), a_),
                      CN1),
                  Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT),
                  Power($(F_, Plus(c_DEFAULT, Times(d_DEFAULT, x_))), n_DEFAULT)), x_Symbol),
              Condition(Integrate(Times(Power(Plus(e, Times(f, x)), m), Sinh(Plus(c, Times(d, x))),
                  Power(F(Plus(c, Times(d, x))),
                      n),
                  Power(Plus(b, Times(a, Sinh(Plus(c, Times(d, x))))), CN1)), x),
                  And(FreeQ(List(a, b, c, d, e, f), x), HyperbolicQ(FSymbol), IntegersQ(m, n)))),
          IIntegrate(5595,
              Integrate(
                  Times(
                      Power(Plus(e_DEFAULT,
                          Times(f_DEFAULT, x_)), m_DEFAULT),
                      Power(Plus(a_,
                          Times(b_DEFAULT, Sech(Plus(c_DEFAULT, Times(d_DEFAULT, x_))))), CN1),
                      Power($(F_, Plus(c_DEFAULT,
                          Times(d_DEFAULT, x_))), n_DEFAULT),
                      Power($(G_, Plus(c_DEFAULT, Times(d_DEFAULT, x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(Times(Power(Plus(e, Times(f, x)), m), Cosh(Plus(c, Times(d, x))),
                      Power(F(Plus(c, Times(d, x))), n), Power(G(Plus(c, Times(d, x))),
                          p),
                      Power(Plus(b, Times(a, Cosh(Plus(c, Times(d, x))))), CN1)), x),
                  And(FreeQ(List(a, b, c, d, e,
                      f), x), HyperbolicQ(
                          FSymbol),
                      HyperbolicQ(GSymbol), IntegersQ(m, n, p)))),
          IIntegrate(5596,
              Integrate(
                  Times(
                      Power(
                          Plus(Times(Csch(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), b_DEFAULT), a_),
                          CN1),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT),
                      Power($(F_, Plus(c_DEFAULT,
                          Times(d_DEFAULT, x_))), n_DEFAULT),
                      Power($(G_, Plus(c_DEFAULT, Times(d_DEFAULT, x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(Power(Plus(e, Times(f, x)), m), Sinh(Plus(c, Times(d, x))),
                          Power(F(Plus(c, Times(d, x))), n), Power(G(
                              Plus(c, Times(d, x))), p),
                          Power(Plus(b, Times(a, Sinh(Plus(c, Times(d, x))))), CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, e,
                      f), x), HyperbolicQ(
                          FSymbol),
                      HyperbolicQ(GSymbol), IntegersQ(m, n, p)))),
          IIntegrate(5597,
              Integrate(
                  Times(
                      Power(Sinh(Plus(a_DEFAULT, Times(b_DEFAULT, x_))), p_DEFAULT), Power(Sinh(
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(Power(C2, Plus(p, q)), CN1),
                      Integrate(
                          ExpandIntegrand(
                              Power(Plus(Negate(Exp(Subtract(Negate(c), Times(d, x)))),
                                  Exp(Plus(c, Times(d, x)))), q),
                              Power(Plus(Negate(Exp(Subtract(Negate(a), Times(b, x)))),
                                  Exp(Plus(a, Times(b, x)))), p),
                              x),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, q), x), IGtQ(p, C0), Not(IntegerQ(q))))),
          IIntegrate(5598,
              Integrate(
                  Times(
                      Power(Cosh(Plus(a_DEFAULT, Times(b_DEFAULT, x_))), p_DEFAULT), Power(Cosh(
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Power(Power(C2,
                          Plus(p, q)), CN1),
                      Integrate(
                          ExpandIntegrand(
                              Power(
                                  Plus(Exp(Subtract(Negate(c), Times(d, x))),
                                      Exp(Plus(c, Times(d, x)))),
                                  q),
                              Power(
                                  Plus(
                                      Exp(Subtract(Negate(a), Times(b, x))), Exp(
                                          Plus(a, Times(b, x)))),
                                  p),
                              x),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, q), x), IGtQ(p, C0), Not(IntegerQ(q))))),
          IIntegrate(5599,
              Integrate(
                  Times(
                      Power(Cosh(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), q_DEFAULT), Power(Sinh(
                          Plus(a_DEFAULT, Times(b_DEFAULT, x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Power(Power(C2,
                          Plus(p, q)), CN1),
                      Integrate(
                          ExpandIntegrand(
                              Power(
                                  Plus(Exp(Subtract(Negate(c), Times(d, x))),
                                      Exp(Plus(c, Times(d, x)))),
                                  q),
                              Power(
                                  Plus(
                                      Negate(Exp(
                                          Subtract(Negate(a), Times(b, x)))),
                                      Exp(Plus(a, Times(b, x)))),
                                  p),
                              x),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, q), x), IGtQ(p, C0), Not(IntegerQ(q))))),
          IIntegrate(5600,
              Integrate(Times(Power(Cosh(Plus(a_DEFAULT, Times(b_DEFAULT, x_))), p_DEFAULT),
                  Power(Sinh(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), q_DEFAULT)), x_Symbol),
              Condition(
                  Dist(Power(Power(C2, Plus(p, q)), CN1), Integrate(ExpandIntegrand(
                      Power(Plus(Negate(Exp(Subtract(Negate(c), Times(d, x)))),
                          Exp(Plus(c, Times(d, x)))), q),
                      Power(Plus(Exp(Subtract(Negate(a), Times(b, x))), Exp(Plus(a, Times(b, x)))),
                          p),
                      x), x), x),
                  And(FreeQ(List(a, b, c, d, q), x), IGtQ(p, C0), Not(IntegerQ(q))))));
}
