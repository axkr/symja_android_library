package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.CI;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Cos;
import static org.matheclipse.core.expression.F.CosIntegral;
import static org.matheclipse.core.expression.F.Denominator;
import static org.matheclipse.core.expression.F.Exp;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Module;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Simplify;
import static org.matheclipse.core.expression.F.Sin;
import static org.matheclipse.core.expression.F.SinIntegral;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.f_DEFAULT;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.p_;
import static org.matheclipse.core.expression.F.p_DEFAULT;
import static org.matheclipse.core.expression.F.u_;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.d;
import static org.matheclipse.core.expression.S.e;
import static org.matheclipse.core.expression.S.f;
import static org.matheclipse.core.expression.S.k;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.BinomialMatchQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.BinomialQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandToSum;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandTrigReduce;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FractionQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LinearMatchQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LinearQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Subst;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Unintegrable;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules168 {
  public static IAST RULES =
      List(
          IIntegrate(3361,
              Integrate(
                  Power(
                      Plus(a_DEFAULT,
                          Times(
                              b_DEFAULT, Sin(
                                  Plus(c_DEFAULT,
                                      Times(
                                          d_DEFAULT, Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)),
                                              n_)))))),
                      p_DEFAULT),
                  x_Symbol),
              Condition(
                  Dist(Power(Times(n, f), CN1),
                      Subst(
                          Integrate(
                              Times(Power(x, Subtract(Power(n, CN1), C1)), Power(
                                  Plus(a, Times(b, Sin(Plus(c, Times(d, x))))), p)),
                              x),
                          x, Power(Plus(e, Times(f, x)), n)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f), x), IGtQ(p, C0), IntegerQ(Power(n, CN1))))),
          IIntegrate(3362,
              Integrate(
                  Power(
                      Plus(a_DEFAULT,
                          Times(
                              Cos(Plus(c_DEFAULT,
                                  Times(d_DEFAULT, Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)),
                                      n_)))),
                              b_DEFAULT)),
                      p_DEFAULT),
                  x_Symbol),
              Condition(
                  Dist(
                      Power(Times(n,
                          f), CN1),
                      Subst(
                          Integrate(
                              Times(
                                  Power(x, Subtract(Power(n, CN1), C1)), Power(Plus(a,
                                      Times(b, Cos(Plus(c, Times(d, x))))), p)),
                              x),
                          x, Power(Plus(e, Times(f, x)), n)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f), x), IGtQ(p, C0), IntegerQ(Power(n, CN1))))),
          IIntegrate(3363,
              Integrate(
                  Power(
                      Plus(a_DEFAULT,
                          Times(
                              b_DEFAULT, Sin(
                                  Plus(c_DEFAULT,
                                      Times(
                                          d_DEFAULT, Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)),
                                              n_)))))),
                      p_DEFAULT),
                  x_Symbol),
              Condition(
                  Module(
                      List(Set(k,
                          Denominator(n))),
                      Dist(Times(k, Power(f, CN1)),
                          Subst(
                              Integrate(
                                  Times(Power(x, Subtract(k, C1)),
                                      Power(
                                          Plus(a,
                                              Times(b,
                                                  Sin(Plus(c, Times(d, Power(x, Times(k, n))))))),
                                          p)),
                                  x),
                              x, Power(Plus(e, Times(f, x)), Power(k, CN1))),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), IGtQ(p, C0), FractionQ(n)))),
          IIntegrate(3364,
              Integrate(
                  Power(
                      Plus(a_DEFAULT,
                          Times(
                              Cos(Plus(c_DEFAULT,
                                  Times(
                                      d_DEFAULT, Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)),
                                          n_)))),
                              b_DEFAULT)),
                      p_DEFAULT),
                  x_Symbol),
              Condition(
                  Module(
                      List(Set(k,
                          Denominator(n))),
                      Dist(Times(k, Power(f, CN1)),
                          Subst(
                              Integrate(
                                  Times(Power(x, Subtract(k, C1)),
                                      Power(
                                          Plus(a,
                                              Times(b,
                                                  Cos(Plus(c, Times(d, Power(x, Times(k, n))))))),
                                          p)),
                                  x),
                              x, Power(Plus(e, Times(f, x)), Power(k, CN1))),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), IGtQ(p, C0), FractionQ(n)))),
          IIntegrate(3365,
              Integrate(
                  Sin(Plus(
                      c_DEFAULT, Times(d_DEFAULT,
                          Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), n_)))),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Times(C1D2, CI),
                          Integrate(
                              Exp(Subtract(Times(CN1, c, CI),
                                  Times(d, CI, Power(Plus(e, Times(f, x)), n)))),
                              x),
                          x),
                      Dist(Times(C1D2, CI),
                          Integrate(
                              Exp(Plus(Times(c, CI),
                                  Times(d, CI, Power(Plus(e, Times(f, x)), n)))),
                              x),
                          x)),
                  FreeQ(List(c, d, e, f, n), x))),
          IIntegrate(3366,
              Integrate(
                  Cos(Plus(
                      c_DEFAULT, Times(d_DEFAULT,
                          Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), n_)))),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(C1D2,
                          Integrate(
                              Exp(Subtract(Times(CN1, c, CI),
                                  Times(d, CI, Power(Plus(e, Times(f, x)), n)))),
                              x),
                          x),
                      Dist(C1D2,
                          Integrate(
                              Exp(Plus(Times(c, CI),
                                  Times(d, CI, Power(Plus(e, Times(f, x)), n)))),
                              x),
                          x)),
                  FreeQ(List(c, d, e, f, n), x))),
          IIntegrate(3367,
              Integrate(
                  Power(
                      Plus(a_DEFAULT,
                          Times(
                              b_DEFAULT, Sin(
                                  Plus(c_DEFAULT,
                                      Times(
                                          d_DEFAULT, Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)),
                                              n_)))))),
                      p_),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandTrigReduce(
                          Power(
                              Plus(a,
                                  Times(b, Sin(Plus(c, Times(d, Power(Plus(e, Times(f, x)), n)))))),
                              p),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, n), x), IGtQ(p, C1)))),
          IIntegrate(3368,
              Integrate(
                  Power(
                      Plus(a_DEFAULT,
                          Times(
                              Cos(Plus(c_DEFAULT,
                                  Times(d_DEFAULT, Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)),
                                      n_)))),
                              b_DEFAULT)),
                      p_),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandTrigReduce(
                          Power(
                              Plus(
                                  a, Times(b, Cos(Plus(c, Times(d, Power(Plus(e, Times(f, x)),
                                      n)))))),
                              p),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, n), x), IGtQ(p, C1)))),
          IIntegrate(3369,
              Integrate(
                  Power(
                      Plus(a_DEFAULT,
                          Times(b_DEFAULT,
                              Sin(Plus(c_DEFAULT,
                                  Times(d_DEFAULT, Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)),
                                      n_)))))),
                      p_),
                  x_Symbol),
              Condition(
                  Unintegrable(
                      Power(
                          Plus(a, Times(b, Sin(
                              Plus(c, Times(d, Power(Plus(e, Times(f, x)), n)))))),
                          p),
                      x),
                  FreeQ(List(a, b, c, d, e, f, n, p), x))),
          IIntegrate(3370,
              Integrate(
                  Power(
                      Plus(a_DEFAULT,
                          Times(
                              Cos(Plus(
                                  c_DEFAULT,
                                  Times(
                                      d_DEFAULT,
                                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), n_)))),
                              b_DEFAULT)),
                      p_),
                  x_Symbol),
              Condition(
                  Unintegrable(
                      Power(
                          Plus(a, Times(b, Cos(Plus(c, Times(d, Power(Plus(e, Times(f, x)), n)))))),
                          p),
                      x),
                  FreeQ(List(a, b, c, d, e, f, n, p), x))),
          IIntegrate(3371,
              Integrate(
                  Power(
                      Plus(
                          a_DEFAULT, Times(b_DEFAULT, Sin(Plus(c_DEFAULT,
                              Times(d_DEFAULT, Power(u_, n_)))))),
                      p_DEFAULT),
                  x_Symbol),
              Condition(
                  Integrate(
                      Power(Plus(a, Times(b, Sin(Plus(c, Times(d, Power(ExpandToSum(u, x), n)))))),
                          p),
                      x),
                  And(FreeQ(List(a, b, c, d, n, p), x), LinearQ(u, x), Not(LinearMatchQ(u, x))))),
          IIntegrate(3372,
              Integrate(
                  Power(
                      Plus(
                          a_DEFAULT,
                          Times(Cos(Plus(c_DEFAULT, Times(d_DEFAULT, Power(u_, n_)))), b_DEFAULT)),
                      p_DEFAULT),
                  x_Symbol),
              Condition(
                  Integrate(
                      Power(Plus(a, Times(b, Cos(Plus(c, Times(d, Power(ExpandToSum(u, x), n)))))),
                          p),
                      x),
                  And(FreeQ(List(a, b, c, d, n, p), x), LinearQ(u, x), Not(LinearMatchQ(u, x))))),
          IIntegrate(3373,
              Integrate(Power(Plus(a_DEFAULT, Times(b_DEFAULT, Sin(u_))), p_DEFAULT), x_Symbol),
              Condition(
                  Integrate(Power(Plus(a, Times(b, Sin(ExpandToSum(u, x)))), p), x), And(FreeQ(
                      List(a, b, p), x), BinomialQ(u, x), Not(BinomialMatchQ(u, x))))),
          IIntegrate(3374,
              Integrate(Power(Plus(a_DEFAULT, Times(Cos(u_), b_DEFAULT)), p_DEFAULT), x_Symbol),
              Condition(
                  Integrate(Power(Plus(a, Times(b, Cos(ExpandToSum(u, x)))), p), x), And(FreeQ(
                      List(a, b, p), x), BinomialQ(u, x), Not(BinomialMatchQ(u, x))))),
          IIntegrate(3375,
              Integrate(Times(Power(x_, CN1),
                  Sin(Times(d_DEFAULT, Power(x_, n_)))), x_Symbol),
              Condition(
                  Simp(Times(SinIntegral(Times(d, Power(x, n))),
                      Power(n, CN1)), x),
                  FreeQ(List(d, n), x))),
          IIntegrate(3376,
              Integrate(Times(Cos(Times(d_DEFAULT, Power(x_, n_))),
                  Power(x_, CN1)), x_Symbol),
              Condition(
                  Simp(Times(CosIntegral(Times(d, Power(x, n))),
                      Power(n, CN1)), x),
                  FreeQ(List(d, n), x))),
          IIntegrate(3377,
              Integrate(Times(Power(x_, CN1),
                  Sin(Plus(c_, Times(d_DEFAULT, Power(x_, n_))))), x_Symbol),
              Condition(
                  Plus(
                      Dist(
                          Sin(c), Integrate(Times(Cos(Times(d, Power(x, n))), Power(x, CN1)),
                              x),
                          x),
                      Dist(
                          Cos(c), Integrate(Times(Sin(Times(d, Power(x, n))), Power(x, CN1)),
                              x),
                          x)),
                  FreeQ(List(c, d, n), x))),
          IIntegrate(3378,
              Integrate(Times(Cos(Plus(c_,
                  Times(d_DEFAULT, Power(x_, n_)))), Power(x_,
                      CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(
                          Cos(c), Integrate(Times(Cos(Times(d,
                              Power(x, n))), Power(x,
                                  CN1)),
                              x),
                          x),
                      Dist(
                          Sin(c), Integrate(Times(Sin(Times(d,
                              Power(x, n))), Power(x,
                                  CN1)),
                              x),
                          x)),
                  FreeQ(List(c, d, n), x))),
          IIntegrate(3379,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT,
                                  Sin(Plus(c_DEFAULT, Times(d_DEFAULT, Power(x_, n_)))))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(n, CN1),
                      Subst(
                          Integrate(
                              Times(
                                  Power(x,
                                      Subtract(Simplify(Times(Plus(m, C1), Power(n, CN1))), C1)),
                                  Power(Plus(a, Times(b, Sin(Plus(c, Times(d, x))))), p)),
                              x),
                          x, Power(x, n)),
                      x),
                  And(FreeQ(List(a, b, c, d, m, n, p), x),
                      IntegerQ(Simplify(
                          Times(Plus(m, C1), Power(n, CN1)))),
                      Or(EqQ(p, C1), EqQ(m, Subtract(n, C1)),
                          And(IntegerQ(p),
                              GtQ(Simplify(Times(Plus(m, C1), Power(n, CN1))), C0)))))),
          IIntegrate(3380,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(
                                  Cos(Plus(c_DEFAULT, Times(d_DEFAULT,
                                      Power(x_, n_)))),
                                  b_DEFAULT)),
                          p_DEFAULT),
                      Power(x_, m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(n, CN1), Subst(
                      Integrate(
                          Times(Power(x, Subtract(Simplify(Times(Plus(m, C1), Power(n, CN1))), C1)),
                              Power(Plus(a, Times(b, Cos(Plus(c, Times(d, x))))), p)),
                          x),
                      x, Power(x, n)), x),
                  And(FreeQ(List(a, b, c, d, m, n, p), x),
                      IntegerQ(Simplify(Times(Plus(m, C1), Power(n, CN1)))),
                      Or(EqQ(p, C1), EqQ(m, Subtract(n, C1)), And(IntegerQ(p),
                          GtQ(Simplify(Times(Plus(m, C1), Power(n, CN1))), C0)))))));
}
