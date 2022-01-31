package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN2;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Cosh;
import static org.matheclipse.core.expression.F.Coth;
import static org.matheclipse.core.expression.F.Csch;
import static org.matheclipse.core.expression.F.Exp;
import static org.matheclipse.core.expression.F.F_;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Sech;
import static org.matheclipse.core.expression.F.Sinh;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Tanh;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.f_DEFAULT;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_DEFAULT;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.FSymbol;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.d;
import static org.matheclipse.core.expression.S.e;
import static org.matheclipse.core.expression.S.f;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.F;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.HyperbolicQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Rt;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Unintegrable;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules278 {
  public static IAST RULES =
      List(
          IIntegrate(5561,
              Integrate(
                  Times(
                      Cosh(Plus(c_DEFAULT, Times(d_DEFAULT,
                          x_))),
                      Power(Plus(e_DEFAULT,
                          Times(f_DEFAULT, x_)), m_DEFAULT),
                      Power(
                          Plus(a_, Times(b_DEFAULT, Sinh(
                              Plus(c_DEFAULT, Times(d_DEFAULT, x_))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(
                                  Power(Plus(e, Times(f, x)), Plus(m, C1)), Power(
                                      Times(b, f, Plus(m, C1)), CN1)),
                              x)),
                      Integrate(Times(Power(Plus(e, Times(f, x)), m), Exp(Plus(c, Times(d, x))),
                          Power(Plus(a, Negate(Rt(Plus(Sqr(a), Sqr(b)), C2)),
                              Times(b, Exp(Plus(c, Times(d, x))))), CN1)),
                          x),
                      Integrate(
                          Times(
                              Power(Plus(e, Times(f, x)), m), Exp(Plus(c,
                                  Times(d, x))),
                              Power(
                                  Plus(
                                      a, Rt(Plus(Sqr(a), Sqr(b)), C2), Times(b,
                                          Exp(Plus(c, Times(d, x))))),
                                  CN1)),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), IGtQ(m, C0),
                      NeQ(Plus(Sqr(a), Sqr(b)), C0)))),
          IIntegrate(5562,
              Integrate(
                  Times(
                      Power(
                          Plus(Times(Cosh(Plus(c_DEFAULT, Times(d_DEFAULT, x_))),
                              b_DEFAULT), a_),
                          CN1),
                      Power(Plus(e_DEFAULT,
                          Times(f_DEFAULT, x_)), m_DEFAULT),
                      Sinh(Plus(c_DEFAULT, Times(d_DEFAULT, x_)))),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(
                                  Power(Plus(e, Times(f, x)), Plus(m,
                                      C1)),
                                  Power(Times(b, f, Plus(m, C1)), CN1)),
                              x)),
                      Integrate(
                          Times(Power(Plus(e, Times(f, x)), m), Exp(Plus(c, Times(d, x))),
                              Power(
                                  Plus(a, Negate(Rt(Subtract(Sqr(a), Sqr(b)), C2)),
                                      Times(b, Exp(Plus(c, Times(d, x))))),
                                  CN1)),
                          x),
                      Integrate(
                          Times(Power(Plus(e, Times(f, x)), m), Exp(Plus(c, Times(d, x))),
                              Power(Plus(a, Rt(Subtract(Sqr(a), Sqr(b)), C2),
                                  Times(b, Exp(Plus(c, Times(d, x))))), CN1)),
                          x)),
                  And(FreeQ(List(a, b, c, d, e,
                      f), x), IGtQ(m,
                          C0),
                      NeQ(Subtract(Sqr(a), Sqr(b)), C0)))),
          IIntegrate(5563,
              Integrate(
                  Times(Power(Cosh(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), n_),
                      Power(Plus(e_DEFAULT,
                          Times(f_DEFAULT, x_)), m_DEFAULT),
                      Power(Plus(a_, Times(b_DEFAULT, Sinh(Plus(c_DEFAULT, Times(d_DEFAULT, x_))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(Power(a, CN1),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), m),
                                  Power(Cosh(Plus(c, Times(d, x))), Subtract(n, C2))),
                              x),
                          x),
                      Dist(Power(b, CN1),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), m),
                                  Power(Cosh(Plus(c,
                                      Times(d, x))), Subtract(n,
                                          C2)),
                                  Sinh(Plus(c, Times(d, x)))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f,
                      m), x), IGtQ(n,
                          C1),
                      EqQ(Plus(Sqr(a), Sqr(b)), C0)))),
          IIntegrate(5564,
              Integrate(
                  Times(
                      Power(Plus(Times(Cosh(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), b_DEFAULT), a_),
                          CN1),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT), Power(
                          Sinh(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), n_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Dist(Power(a, CN1),
                              Integrate(
                                  Times(Power(Plus(e, Times(f, x)), m),
                                      Power(Sinh(Plus(c, Times(d, x))), Subtract(n, C2))),
                                  x),
                              x)),
                      Dist(Power(b, CN1),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), m),
                                  Power(Sinh(Plus(c, Times(d, x))), Subtract(n,
                                      C2)),
                                  Cosh(Plus(c, Times(d, x)))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, m), x), IGtQ(n, C1),
                      EqQ(Subtract(Sqr(a), Sqr(b)), C0)))),
          IIntegrate(5565,
              Integrate(
                  Times(Power(Cosh(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), n_),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT),
                      Power(Plus(a_, Times(b_DEFAULT, Sinh(Plus(c_DEFAULT, Times(d_DEFAULT, x_))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Dist(Times(a, Power(b, CN2)),
                              Integrate(
                                  Times(
                                      Power(Plus(e, Times(f, x)), m), Power(Cosh(
                                          Plus(c, Times(d, x))), Subtract(n, C2))),
                                  x),
                              x)),
                      Dist(Power(b, CN1),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), m),
                                  Power(Cosh(Plus(c, Times(d, x))), Subtract(n,
                                      C2)),
                                  Sinh(Plus(c, Times(d, x)))),
                              x),
                          x),
                      Dist(
                          Times(Plus(Sqr(a), Sqr(
                              b)), Power(b,
                                  CN2)),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), m),
                                  Power(Cosh(Plus(c,
                                      Times(d, x))), Subtract(n,
                                          C2)),
                                  Power(Plus(a, Times(b, Sinh(Plus(c, Times(d, x))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), IGtQ(n, C1), NeQ(Plus(Sqr(a),
                      Sqr(b)), C0), IGtQ(m,
                          C0)))),
          IIntegrate(5566,
              Integrate(
                  Times(
                      Power(
                          Plus(Times(Cosh(Plus(c_DEFAULT, Times(d_DEFAULT,
                              x_))), b_DEFAULT), a_),
                          CN1),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT,
                          x_)), m_DEFAULT),
                      Power(Sinh(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), n_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Dist(
                              Times(a, Power(b,
                                  CN2)),
                              Integrate(
                                  Times(Power(Plus(e, Times(f, x)), m), Power(
                                      Sinh(Plus(c, Times(d, x))), Subtract(n, C2))),
                                  x),
                              x)),
                      Dist(Power(b, CN1),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), m),
                                  Power(Sinh(Plus(c,
                                      Times(d, x))), Subtract(n,
                                          C2)),
                                  Cosh(Plus(c, Times(d, x)))),
                              x),
                          x),
                      Dist(
                          Times(Subtract(Sqr(a), Sqr(
                              b)), Power(b,
                                  CN2)),
                          Integrate(Times(Power(Plus(e, Times(f, x)), m),
                              Power(Sinh(Plus(c, Times(d, x))), Subtract(n, C2)),
                              Power(Plus(a, Times(b, Cosh(Plus(c, Times(d, x))))), CN1)), x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), IGtQ(n, C1), NeQ(Subtract(Sqr(a),
                      Sqr(b)), C0), IGtQ(m,
                          C0)))),
          IIntegrate(5567,
              Integrate(
                  Times(
                      Power(Plus(e_DEFAULT,
                          Times(f_DEFAULT, x_)), m_DEFAULT),
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
                              Times(
                                  Power(Plus(e, Times(f, x)), m), Sech(Plus(c, Times(d,
                                      x))),
                                  Power(Tanh(Plus(c, Times(d, x))), Subtract(n, C1))),
                              x),
                          x),
                      Dist(Times(a, Power(b, CN1)),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), m), Sech(Plus(c, Times(d, x))),
                                  Power(Tanh(Plus(c, Times(d, x))), Subtract(n, C1)), Power(Plus(a,
                                      Times(b, Sinh(Plus(c, Times(d, x))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), IGtQ(m, C0), IGtQ(n, C0)))),
          IIntegrate(5568,
              Integrate(
                  Times(
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
                              Csch(Plus(c, Times(d, x))), Power(Coth(Plus(c, Times(d, x))),
                                  Subtract(n, C1))),
                              x),
                          x),
                      Dist(
                          Times(a, Power(b,
                              CN1)),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), m), Csch(Plus(c, Times(d, x))),
                                  Power(Coth(Plus(c,
                                      Times(d, x))), Subtract(n,
                                          C1)),
                                  Power(Plus(a, Times(b, Cosh(Plus(c, Times(d, x))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), IGtQ(m, C0), IGtQ(n, C0)))),
          IIntegrate(5569,
              Integrate(
                  Times(
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
                              Times(
                                  Power(Plus(e, Times(f, x)), m), Power(Coth(Plus(c, Times(d, x))),
                                      n)),
                              x),
                          x),
                      Dist(
                          Times(b, Power(a,
                              CN1)),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), m), Cosh(Plus(c, Times(d, x))),
                                  Power(Coth(Plus(c, Times(d, x))), Subtract(n,
                                      C1)),
                                  Power(Plus(a, Times(b, Sinh(Plus(c, Times(d, x))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), IGtQ(m, C0), IGtQ(n, C0)))),
          IIntegrate(5570,
              Integrate(
                  Times(
                      Power(Plus(
                          Times(Cosh(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), b_DEFAULT), a_), CN1),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT,
                          x_)), m_DEFAULT),
                      Power(Tanh(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Power(a, CN1),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), m),
                                  Power(Tanh(Plus(c, Times(d, x))), n)),
                              x),
                          x),
                      Dist(
                          Times(b, Power(a,
                              CN1)),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), m), Sinh(Plus(c, Times(d, x))),
                                  Power(Tanh(Plus(c, Times(d, x))), Subtract(n, C1)), Power(Plus(a,
                                      Times(b, Cosh(Plus(c, Times(d, x))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), IGtQ(m, C0), IGtQ(n, C0)))),
          IIntegrate(5571,
              Integrate(
                  Times(Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT),
                      Power(Sech(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), n_DEFAULT),
                      Power(Plus(a_, Times(b_DEFAULT, Sinh(Plus(c_DEFAULT, Times(d_DEFAULT, x_))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(Power(a, CN1),
                          Integrate(Times(Power(Plus(e, Times(f, x)), m),
                              Power(Sech(Plus(c, Times(d, x))), Plus(n, C2))), x),
                          x),
                      Dist(Power(b, CN1),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), m),
                                  Power(Sech(Plus(c, Times(d, x))), Plus(n, C1)),
                                  Tanh(Plus(c, Times(d, x)))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f,
                      n), x), IGtQ(m,
                          C0),
                      EqQ(Plus(Sqr(a), Sqr(b)), C0)))),
          IIntegrate(5572,
              Integrate(
                  Times(
                      Power(Csch(
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_))), n_DEFAULT),
                      Power(Plus(Times(Cosh(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), b_DEFAULT),
                          a_), CN1),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Dist(Power(a, CN1),
                              Integrate(
                                  Times(Power(Plus(e, Times(f, x)), m),
                                      Power(Csch(Plus(c, Times(d, x))), Plus(n, C2))),
                                  x),
                              x)),
                      Dist(Power(b, CN1),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), m),
                                  Power(Csch(Plus(c, Times(d, x))),
                                      Plus(n, C1)),
                                  Coth(Plus(c, Times(d, x)))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f,
                      n), x), IGtQ(m,
                          C0),
                      EqQ(Subtract(Sqr(a), Sqr(b)), C0)))),
          IIntegrate(5573,
              Integrate(
                  Times(
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT,
                          x_)), m_DEFAULT),
                      Power(Sech(
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_))), n_DEFAULT),
                      Power(
                          Plus(a_, Times(b_DEFAULT, Sinh(
                              Plus(c_DEFAULT, Times(d_DEFAULT, x_))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(Times(Sqr(b), Power(Plus(Sqr(a), Sqr(b)), CN1)),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), m),
                                  Power(Sech(Plus(c, Times(d, x))), Subtract(n, C2)), Power(Plus(a,
                                      Times(b, Sinh(Plus(c, Times(d, x))))), CN1)),
                              x),
                          x),
                      Dist(
                          Power(Plus(Sqr(a),
                              Sqr(b)), CN1),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), m),
                                  Power(Sech(Plus(c, Times(d, x))), n), Subtract(a,
                                      Times(b, Sinh(Plus(c, Times(d, x)))))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), IGtQ(m, C0), NeQ(Plus(Sqr(a),
                      Sqr(b)), C0), IGtQ(n,
                          C0)))),
          IIntegrate(5574,
              Integrate(
                  Times(
                      Power(Csch(
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_))), n_DEFAULT),
                      Power(
                          Plus(Times(Cosh(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), b_DEFAULT),
                              a_),
                          CN1),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(Times(Sqr(b), Power(Subtract(Sqr(a), Sqr(b)), CN1)),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), m),
                                  Power(Csch(Plus(c, Times(d, x))), Subtract(n, C2)),
                                  Power(Plus(a, Times(b, Cosh(Plus(c, Times(d, x))))), CN1)),
                              x),
                          x),
                      Dist(
                          Power(Subtract(Sqr(a),
                              Sqr(b)), CN1),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), m),
                                  Power(Csch(Plus(c, Times(d, x))), n),
                                  Subtract(a, Times(b, Cosh(Plus(c, Times(d, x)))))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), IGtQ(m, C0), NeQ(Subtract(Sqr(a),
                      Sqr(b)), C0), IGtQ(n,
                          C0)))),
          IIntegrate(5575,
              Integrate(
                  Times(
                      Power(Csch(Plus(c_DEFAULT,
                          Times(d_DEFAULT, x_))), n_DEFAULT),
                      Power(Plus(e_DEFAULT,
                          Times(f_DEFAULT, x_)), m_DEFAULT),
                      Power(Plus(a_, Times(b_DEFAULT, Sinh(Plus(c_DEFAULT, Times(d_DEFAULT, x_))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Power(a, CN1),
                          Integrate(
                              Times(
                                  Power(Plus(e, Times(f, x)), m), Power(Csch(Plus(c, Times(d, x))),
                                      n)),
                              x),
                          x),
                      Dist(
                          Times(b, Power(a,
                              CN1)),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), m),
                                  Power(Csch(Plus(c, Times(d, x))), Subtract(n,
                                      C1)),
                                  Power(Plus(a, Times(b, Sinh(Plus(c, Times(d, x))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), IGtQ(m, C0), IGtQ(n, C0)))),
          IIntegrate(5576,
              Integrate(
                  Times(
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
                                  Power(Sech(Plus(c, Times(d, x))), n)),
                              x),
                          x),
                      Dist(Times(b, Power(a, CN1)),
                          Integrate(Times(Power(Plus(e, Times(f, x)), m),
                              Power(Sech(Plus(c, Times(d, x))), Subtract(n, C1)),
                              Power(Plus(a, Times(b, Cosh(Plus(c, Times(d, x))))), CN1)), x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), IGtQ(m, C0), IGtQ(n, C0)))),
          IIntegrate(5577,
              Integrate(
                  Times(
                      Power(Plus(e_DEFAULT,
                          Times(f_DEFAULT, x_)), m_DEFAULT),
                      Power(
                          Plus(a_, Times(b_DEFAULT,
                              Sinh(Plus(c_DEFAULT, Times(d_DEFAULT, x_))))),
                          CN1),
                      Power($(F_, Plus(c_DEFAULT, Times(d_DEFAULT, x_))), n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Unintegrable(
                      Times(
                          Power(Plus(e, Times(f, x)), m), Power(F(
                              Plus(c, Times(d, x))), n),
                          Power(Plus(a, Times(b, Sinh(Plus(c, Times(d, x))))), CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, n), x), HyperbolicQ(FSymbol)))),
          IIntegrate(5578,
              Integrate(
                  Times(
                      Power(Plus(
                          Times(Cosh(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), b_DEFAULT), a_), CN1),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT,
                          x_)), m_DEFAULT),
                      Power($(F_, Plus(c_DEFAULT, Times(d_DEFAULT, x_))), n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Unintegrable(
                      Times(
                          Power(Plus(e, Times(f, x)), m), Power(F(
                              Plus(c, Times(d, x))), n),
                          Power(Plus(a, Times(b, Cosh(Plus(c, Times(d, x))))), CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m, n), x), HyperbolicQ(FSymbol)))),
          IIntegrate(5579,
              Integrate(
                  Times(Power(Cosh(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), p_DEFAULT),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT,
                          x_)), m_DEFAULT),
                      Power(Sinh(
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_))), n_DEFAULT),
                      Power(
                          Plus(a_, Times(b_DEFAULT, Sinh(
                              Plus(c_DEFAULT, Times(d_DEFAULT, x_))))),
                          CN1)),
                  x_Symbol),
              Condition(Subtract(Dist(Power(b, CN1), Integrate(Times(Power(Plus(e, Times(f, x)), m),
                  Power(Cosh(Plus(c, Times(d, x))), p), Power(Sinh(Plus(c, Times(d, x))),
                      Subtract(n, C1))),
                  x), x),
                  Dist(Times(a, Power(b, CN1)),
                      Integrate(Times(Power(Plus(e, Times(f, x)), m),
                          Power(Cosh(Plus(c, Times(d, x))), p),
                          Power(Sinh(Plus(c, Times(d, x))), Subtract(n, C1)), Power(
                              Plus(a, Times(b, Sinh(Plus(c, Times(d, x))))), CN1)),
                          x),
                      x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), IGtQ(m, C0), IGtQ(n, C0), IGtQ(p, C0)))),
          IIntegrate(5580,
              Integrate(
                  Times(
                      Power(Cosh(
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_))), n_DEFAULT),
                      Power(
                          Plus(Times(Cosh(Plus(c_DEFAULT, Times(d_DEFAULT,
                              x_))), b_DEFAULT), a_),
                          CN1),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT,
                          x_)), m_DEFAULT),
                      Power(Sinh(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Power(b, CN1),
                          Integrate(Times(Power(Plus(e, Times(f, x)), m),
                              Power(Sinh(Plus(c, Times(d, x))), p),
                              Power(Cosh(Plus(c, Times(d, x))), Subtract(n, C1))), x),
                          x),
                      Dist(Times(a, Power(b, CN1)),
                          Integrate(Times(Power(Plus(e, Times(f, x)), m),
                              Power(Sinh(Plus(c, Times(d, x))), p),
                              Power(Cosh(Plus(c, Times(d, x))), Subtract(n, C1)),
                              Power(Plus(a, Times(b, Cosh(Plus(c, Times(d, x))))), CN1)), x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), IGtQ(m, C0), IGtQ(n, C0), IGtQ(p, C0)))));
}
