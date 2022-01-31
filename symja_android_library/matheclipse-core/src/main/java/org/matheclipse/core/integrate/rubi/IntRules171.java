package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.CI;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Cos;
import static org.matheclipse.core.expression.F.Denominator;
import static org.matheclipse.core.expression.F.Exp;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.If;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Module;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Simplify;
import static org.matheclipse.core.expression.F.Sin;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.f_DEFAULT;
import static org.matheclipse.core.expression.F.g_DEFAULT;
import static org.matheclipse.core.expression.F.h_DEFAULT;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.p_;
import static org.matheclipse.core.expression.F.p_DEFAULT;
import static org.matheclipse.core.expression.F.u_;
import static org.matheclipse.core.expression.F.v_;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.d;
import static org.matheclipse.core.expression.S.e;
import static org.matheclipse.core.expression.S.f;
import static org.matheclipse.core.expression.S.g;
import static org.matheclipse.core.expression.S.h;
import static org.matheclipse.core.expression.S.k;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.v;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.BinomialMatchQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.BinomialQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandIntegrand;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandToSum;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandTrigReduce;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FracPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FractionQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LinearMatchQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LinearQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Subst;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Unintegrable;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules171 {
  public static IAST RULES =
      List(
          IIntegrate(3421,
              Integrate(
                  Times(
                      Power(Times(e_,
                          x_), m_),
                      Power(
                          Plus(a_DEFAULT,
                              Times(
                                  b_DEFAULT, Sin(Plus(c_DEFAULT, Times(d_DEFAULT,
                                      Power(x_, n_)))))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(e, IntPart(m)), Power(Times(e, x),
                              FracPart(m)),
                          Power(Power(x, FracPart(m)), CN1)),
                      Integrate(
                          Times(
                              Power(x, m), Power(Plus(a,
                                  Times(b, Sin(Plus(c, Times(d, Power(x, n)))))), p)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, m, n), x), IntegerQ(p), NeQ(m,
                      CN1), IGtQ(Simplify(Times(n, Power(Plus(m, C1), CN1))), C0),
                      Not(IntegerQ(n))))),
          IIntegrate(3422,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(
                                  Cos(Plus(c_DEFAULT, Times(d_DEFAULT,
                                      Power(x_, n_)))),
                                  b_DEFAULT)),
                          p_DEFAULT),
                      Power(Times(e_, x_), m_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(e, IntPart(m)), Power(Times(e, x),
                              FracPart(m)),
                          Power(Power(x, FracPart(m)), CN1)),
                      Integrate(Times(
                          Power(x, m), Power(Plus(a, Times(b, Cos(Plus(c, Times(d, Power(x, n)))))),
                              p)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, m, n), x), IntegerQ(p), NeQ(m,
                      CN1), IGtQ(Simplify(Times(n, Power(Plus(m, C1), CN1))), C0),
                      Not(IntegerQ(n))))),
          IIntegrate(3423,
              Integrate(
                  Times(
                      Power(Times(e_DEFAULT, x_), m_DEFAULT), Sin(
                          Plus(c_DEFAULT, Times(d_DEFAULT, Power(x_, n_))))),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Times(C1D2, CI),
                          Integrate(
                              Times(
                                  Power(Times(e, x), m), Exp(Subtract(Times(CN1, c, CI),
                                      Times(d, CI, Power(x, n))))),
                              x),
                          x),
                      Dist(Times(C1D2, CI),
                          Integrate(
                              Times(
                                  Power(Times(e, x), m), Exp(Plus(Times(c, CI),
                                      Times(d, CI, Power(x, n))))),
                              x),
                          x)),
                  FreeQ(List(c, d, e, m, n), x))),
          IIntegrate(3424,
              Integrate(
                  Times(Cos(Plus(c_DEFAULT, Times(d_DEFAULT, Power(x_, n_)))),
                      Power(Times(e_DEFAULT, x_), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(C1D2,
                          Integrate(
                              Times(
                                  Power(Times(e, x), m), Exp(Subtract(Times(CN1, c, CI),
                                      Times(d, CI, Power(x, n))))),
                              x),
                          x),
                      Dist(C1D2,
                          Integrate(
                              Times(
                                  Power(Times(e, x), m), Exp(Plus(Times(c, CI),
                                      Times(d, CI, Power(x, n))))),
                              x),
                          x)),
                  FreeQ(List(c, d, e, m, n), x))),
          IIntegrate(3425,
              Integrate(
                  Times(Power(Times(e_DEFAULT, x_), m_DEFAULT),
                      Power(
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT, Sin(Plus(c_DEFAULT,
                                  Times(d_DEFAULT, Power(x_, n_)))))),
                          p_)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandTrigReduce(
                          Power(Times(e, x),
                              m),
                          Power(Plus(a, Times(b, Sin(Plus(c, Times(d, Power(x, n)))))), p), x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, m, n), x), IGtQ(p, C0)))),
          IIntegrate(3426,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(Cos(Plus(c_DEFAULT, Times(d_DEFAULT, Power(x_, n_)))),
                                  b_DEFAULT)),
                          p_),
                      Power(Times(e_DEFAULT, x_), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandTrigReduce(
                          Power(Times(e,
                              x), m),
                          Power(Plus(a, Times(b, Cos(Plus(c, Times(d, Power(x, n)))))), p), x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, m, n), x), IGtQ(p, C0)))),
          IIntegrate(3427,
              Integrate(
                  Times(
                      Power(Times(e_DEFAULT,
                          x_), m_DEFAULT),
                      Power(
                          Plus(a_DEFAULT,
                              Times(
                                  b_DEFAULT, Sin(Plus(c_DEFAULT, Times(d_DEFAULT,
                                      Power(x_, n_)))))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Unintegrable(
                      Times(Power(Times(e, x), m), Power(
                          Plus(a, Times(b, Sin(Plus(c, Times(d, Power(x, n)))))), p)),
                      x),
                  FreeQ(List(a, b, c, d, e, m, n, p), x))),
          IIntegrate(3428,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(Cos(Plus(c_DEFAULT, Times(d_DEFAULT, Power(x_, n_)))),
                                  b_DEFAULT)),
                          p_DEFAULT),
                      Power(Times(e_DEFAULT, x_), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Unintegrable(Times(Power(Times(e, x), m),
                      Power(Plus(a, Times(b, Cos(Plus(c, Times(d, Power(x, n)))))), p)), x),
                  FreeQ(List(a, b, c, d, e, m, n, p), x))),
          IIntegrate(3429,
              Integrate(
                  Times(
                      Power(Times(e_, x_), m_DEFAULT),
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, Sin(u_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(Power(Times(e, x), m),
                          Power(Plus(a, Times(b, Sin(ExpandToSum(u, x)))), p)),
                      x),
                  And(FreeQ(List(a, b, e, m, p), x), BinomialQ(u, x), Not(BinomialMatchQ(u, x))))),
          IIntegrate(3430,
              Integrate(
                  Times(Power(Plus(a_DEFAULT, Times(Cos(u_), b_DEFAULT)), p_DEFAULT),
                      Power(Times(e_, x_), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(
                          Power(Times(e, x), m), Power(Plus(a, Times(b, Cos(ExpandToSum(u, x)))),
                              p)),
                      x),
                  And(FreeQ(List(a, b, e, m, p), x), BinomialQ(u, x), Not(BinomialMatchQ(u, x))))),
          IIntegrate(3431,
              Integrate(
                  Times(
                      Power(Plus(g_DEFAULT,
                          Times(h_DEFAULT, x_)), m_DEFAULT),
                      Power(
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT,
                                  Sin(Plus(c_DEFAULT,
                                      Times(
                                          d_DEFAULT, Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)),
                                              n_)))))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(Times(n, f), CN1),
                      Subst(Integrate(
                          ExpandIntegrand(Power(Plus(a, Times(b, Sin(Plus(c, Times(d, x))))), p),
                              Times(Power(x, Subtract(Power(n, CN1), C1)),
                                  Power(
                                      Plus(g, Times(CN1, e, h, Power(f, CN1)),
                                          Times(h, Power(x, Power(n, CN1)), Power(f, CN1))),
                                      m)),
                              x),
                          x), x, Power(Plus(e, Times(f, x)), n)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g, h,
                      m), x), IGtQ(p,
                          C0),
                      IntegerQ(Power(n, CN1))))),
          IIntegrate(3432,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(
                                  Cos(Plus(c_DEFAULT,
                                      Times(d_DEFAULT, Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)),
                                          n_)))),
                                  b_DEFAULT)),
                          p_DEFAULT),
                      Power(Plus(g_DEFAULT, Times(h_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(Times(n, f), CN1),
                      Subst(Integrate(
                          ExpandIntegrand(Power(Plus(a, Times(b, Cos(Plus(c, Times(d, x))))), p),
                              Times(Power(x, Subtract(Power(n, CN1), C1)),
                                  Power(Plus(g, Times(CN1, e, h, Power(f, CN1)),
                                      Times(h, Power(x, Power(n, CN1)), Power(f, CN1))), m)),
                              x),
                          x), x, Power(Plus(e, Times(f, x)), n)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g, h,
                      m), x), IGtQ(p,
                          C0),
                      IntegerQ(Power(n, CN1))))),
          IIntegrate(3433,
              Integrate(
                  Times(Power(Plus(g_DEFAULT, Times(h_DEFAULT, x_)), m_DEFAULT),
                      Power(
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT,
                                  Sin(Plus(c_DEFAULT,
                                      Times(d_DEFAULT, Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)),
                                          n_)))))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Module(
                      List(Set(k,
                          If(FractionQ(n), Denominator(n), C1))),
                      Dist(Times(k, Power(Power(f, Plus(m, C1)), CN1)),
                          Subst(
                              Integrate(
                                  ExpandIntegrand(
                                      Power(
                                          Plus(a,
                                              Times(b,
                                                  Sin(Plus(c, Times(d, Power(x, Times(k, n))))))),
                                          p),
                                      Times(Power(x, Subtract(k, C1)),
                                          Power(Plus(Times(f, g), Times(CN1, e, h),
                                              Times(h, Power(x, k))), m)),
                                      x),
                                  x),
                              x, Power(Plus(e, Times(f, x)), Power(k, CN1))),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g, h), x), IGtQ(p, C0), IGtQ(m, C0)))),
          IIntegrate(3434,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(
                                  Cos(Plus(
                                      c_DEFAULT,
                                      Times(
                                          d_DEFAULT,
                                          Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), n_)))),
                                  b_DEFAULT)),
                          p_DEFAULT),
                      Power(Plus(g_DEFAULT, Times(h_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Module(
                      List(Set(k,
                          If(FractionQ(n), Denominator(n), C1))),
                      Dist(
                          Times(k, Power(Power(f, Plus(m, C1)), CN1)), Subst(
                              Integrate(
                                  ExpandIntegrand(
                                      Power(
                                          Plus(a,
                                              Times(b,
                                                  Cos(Plus(c, Times(d, Power(x, Times(k, n))))))),
                                          p),
                                      Times(Power(x, Subtract(k, C1)),
                                          Power(Plus(Times(f, g), Times(CN1, e, h),
                                              Times(h, Power(x, k))), m)),
                                      x),
                                  x),
                              x, Power(Plus(e, Times(f, x)), Power(k, CN1))),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g, h), x), IGtQ(p, C0), IGtQ(m, C0)))),
          IIntegrate(3435,
              Integrate(
                  Times(
                      Power(Plus(g_DEFAULT,
                          Times(h_DEFAULT, x_)), m_DEFAULT),
                      Power(
                          Plus(a_DEFAULT,
                              Times(
                                  b_DEFAULT, Sin(
                                      Plus(c_DEFAULT,
                                          Times(
                                              d_DEFAULT, Power(Plus(e_DEFAULT,
                                                  Times(f_DEFAULT, x_)),
                                                  n_)))))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(f, CN1),
                      Subst(
                          Integrate(
                              Times(
                                  Power(Times(h, x, Power(f, CN1)), m), Power(Plus(a,
                                      Times(b, Sin(Plus(c, Times(d, Power(x, n)))))),
                                      p)),
                              x),
                          x, Plus(e, Times(f, x))),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g, h, m), x), IGtQ(p, C0),
                      EqQ(Subtract(Times(f, g), Times(e, h)), C0)))),
          IIntegrate(3436,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(
                                  Cos(Plus(c_DEFAULT,
                                      Times(
                                          d_DEFAULT, Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)),
                                              n_)))),
                                  b_DEFAULT)),
                          p_DEFAULT),
                      Power(Plus(g_DEFAULT, Times(h_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(f, CN1),
                      Subst(
                          Integrate(
                              Times(Power(Times(h, x, Power(f, CN1)), m),
                                  Power(Plus(a, Times(b, Cos(Plus(c, Times(d, Power(x, n)))))), p)),
                              x),
                          x, Plus(e, Times(f, x))),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g, h,
                      m), x), IGtQ(p,
                          C0),
                      EqQ(Subtract(Times(f, g), Times(e, h)), C0)))),
          IIntegrate(3437,
              Integrate(
                  Times(Power(Plus(g_DEFAULT, Times(h_DEFAULT, x_)), m_DEFAULT),
                      Power(
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT,
                                  Sin(Plus(c_DEFAULT,
                                      Times(d_DEFAULT, Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)),
                                          n_)))))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Unintegrable(
                      Times(
                          Power(Plus(g,
                              Times(h, x)), m),
                          Power(
                              Plus(a, Times(b, Sin(Plus(c,
                                  Times(d, Power(Plus(e, Times(f, x)), n)))))),
                              p)),
                      x),
                  FreeQ(List(a, b, c, d, e, f, g, h, m, n, p), x))),
          IIntegrate(3438,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(Cos(Plus(c_DEFAULT,
                                  Times(d_DEFAULT, Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)),
                                      n_)))),
                                  b_DEFAULT)),
                          p_DEFAULT),
                      Power(Plus(g_DEFAULT, Times(h_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Unintegrable(
                      Times(
                          Power(Plus(g,
                              Times(h, x)), m),
                          Power(
                              Plus(a,
                                  Times(b, Cos(Plus(c, Times(d, Power(Plus(e, Times(f, x)), n)))))),
                              p)),
                      x),
                  FreeQ(List(a, b, c, d, e, f, g, h, m, n, p), x))),
          IIntegrate(3439,
              Integrate(
                  Times(Power(v_, m_DEFAULT),
                      Power(
                          Plus(
                              a_DEFAULT,
                              Times(
                                  b_DEFAULT,
                                  Sin(Plus(c_DEFAULT, Times(d_DEFAULT, Power(u_, n_)))))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(
                          Power(ExpandToSum(v,
                              x), m),
                          Power(
                              Plus(a, Times(b, Sin(
                                  Plus(c, Times(d, Power(ExpandToSum(u, x), n)))))),
                              p)),
                      x),
                  And(FreeQ(List(a, b, c, d, m, n, p), x), LinearQ(u, x), LinearQ(v, x),
                      Not(And(LinearMatchQ(u, x), LinearMatchQ(v, x)))))),
          IIntegrate(3440,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(Cos(Plus(c_DEFAULT, Times(d_DEFAULT, Power(u_, n_)))),
                                  b_DEFAULT)),
                          p_DEFAULT),
                      Power(v_, m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(Times(Power(ExpandToSum(v, x), m),
                      Power(Plus(a, Times(b, Cos(Plus(c, Times(d, Power(ExpandToSum(u, x), n)))))),
                          p)),
                      x),
                  And(FreeQ(List(a, b, c, d, m, n, p), x), LinearQ(u, x), LinearQ(v, x),
                      Not(And(LinearMatchQ(u, x), LinearMatchQ(v, x)))))));
}
