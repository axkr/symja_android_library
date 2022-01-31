package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN2;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Cos;
import static org.matheclipse.core.expression.F.Cot;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.Hypergeometric2F1;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Sin;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Tan;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.d;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandTrig;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ILtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Subst;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules154 {
  public static IAST RULES =
      List(
          IIntegrate(3081,
              Integrate(
                  Times(
                      Power($($s("§sin"),
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_))), m_DEFAULT),
                      Power(
                          Plus(
                              Times($($s("§cos"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))),
                                  a_DEFAULT),
                              Times(b_DEFAULT,
                                  $($s("§sin"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(a,
                              Power(
                                  Plus(
                                      Times(a, Cos(
                                          Plus(c, Times(d, x)))),
                                      Times(b, Sin(Plus(c, Times(d, x))))),
                                  n),
                              Power(Times(C2, b, d, n, Power(Sin(Plus(c, Times(d, x))), n)), CN1)),
                          x),
                      Dist(
                          Power(Times(C2,
                              b), CN1),
                          Integrate(
                              Times(
                                  Power(Plus(Times(a, Cos(Plus(c, Times(d, x)))), Times(b,
                                      Sin(Plus(c, Times(d, x))))), Plus(n,
                                          C1)),
                                  Power(Power(Sin(Plus(c, Times(d, x))), Plus(n, C1)), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d), x), EqQ(Plus(m, n), C0), EqQ(Plus(Sqr(a),
                      Sqr(b)), C0), LtQ(n,
                          C0)))),
          IIntegrate(3082,
              Integrate(
                  Times(
                      Power($($s("§cos"),
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_))), m_DEFAULT),
                      Power(
                          Plus(
                              Times($($s("§cos"),
                                  Plus(c_DEFAULT, Times(d_DEFAULT, x_))), a_DEFAULT),
                              Times(b_DEFAULT, $($s("§sin"), Plus(c_DEFAULT,
                                  Times(d_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(b,
                                  Power(
                                      Plus(
                                          Times(a, Cos(
                                              Plus(c, Times(d, x)))),
                                          Times(b, Sin(Plus(c, Times(d, x))))),
                                      n),
                                  Power(Times(C2, a, d, n, Power(Cos(Plus(c, Times(d, x))), n)),
                                      CN1)),
                              x)),
                      Dist(Power(Times(C2, a), CN1),
                          Integrate(
                              Times(
                                  Power(Plus(Times(a, Cos(Plus(c, Times(d, x)))),
                                      Times(b, Sin(Plus(c, Times(d, x))))), Plus(n, C1)),
                                  Power(Power(Cos(Plus(c, Times(d, x))), Plus(n, C1)), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d), x), EqQ(Plus(m, n), C0), EqQ(Plus(Sqr(a),
                      Sqr(b)), C0), LtQ(n,
                          C0)))),
          IIntegrate(3083,
              Integrate(
                  Times(
                      Power($($s("§sin"),
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_))), m_DEFAULT),
                      Power(
                          Plus(
                              Times($($s("§cos"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))),
                                  a_DEFAULT),
                              Times(b_DEFAULT,
                                  $($s("§sin"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(a,
                          Power(
                              Plus(
                                  Times(a, Cos(Plus(c, Times(d, x)))), Times(b,
                                      Sin(Plus(c, Times(d, x))))),
                              n),
                          Hypergeometric2F1(C1, n, Plus(n, C1),
                              Times(
                                  Plus(b, Times(a,
                                      Cot(Plus(c, Times(d, x))))),
                                  Power(Times(C2, b), CN1))),
                          Power(Times(C2, b, d, n, Power(Sin(Plus(c, Times(d, x))), n)), CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, n), x), EqQ(Plus(m, n), C0),
                      EqQ(Plus(Sqr(a), Sqr(b)), C0), Not(IntegerQ(n))))),
          IIntegrate(3084,
              Integrate(
                  Times(
                      Power($($s("§cos"),
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_))), m_DEFAULT),
                      Power(
                          Plus(
                              Times($($s("§cos"),
                                  Plus(c_DEFAULT, Times(d_DEFAULT, x_))), a_DEFAULT),
                              Times(b_DEFAULT,
                                  $($s("§sin"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Negate(Simp(Times(b,
                      Power(Plus(Times(a, Cos(Plus(c, Times(d, x)))),
                          Times(b, Sin(Plus(c, Times(d, x))))), n),
                      Hypergeometric2F1(C1, n, Plus(n, C1),
                          Times(Plus(a, Times(b, Tan(Plus(c, Times(d, x))))), Power(Times(C2, a),
                              CN1))),
                      Power(Times(C2, a, d, n, Power(Cos(Plus(c, Times(d, x))), n)), CN1)),
                      x)),
                  And(FreeQ(List(a, b, c, d, n), x), EqQ(Plus(m,
                      n), C0), EqQ(Plus(Sqr(a), Sqr(b)),
                          C0),
                      Not(IntegerQ(n))))),
          IIntegrate(3085,
              Integrate(
                  Times(
                      Power($($s("§sin"),
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_))), m_),
                      Power(
                          Plus(
                              Times($($s("§cos"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))),
                                  a_DEFAULT),
                              Times(b_DEFAULT, $($s("§sin"), Plus(c_DEFAULT,
                                  Times(d_DEFAULT, x_))))),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(Power(Plus(b, Times(a, Cot(Plus(c, Times(d, x))))),
                      n), x),
                  And(FreeQ(List(a, b, c,
                      d), x), EqQ(Plus(m, n),
                          C0),
                      IntegerQ(n), NeQ(Plus(Sqr(a), Sqr(b)), C0)))),
          IIntegrate(3086,
              Integrate(
                  Times(Power($($s("§cos"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))), m_),
                      Power(
                          Plus(
                              Times($($s("§cos"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))),
                                  a_DEFAULT),
                              Times(b_DEFAULT, $($s("§sin"), Plus(c_DEFAULT,
                                  Times(d_DEFAULT, x_))))),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(Power(Plus(a, Times(b, Tan(Plus(c, Times(d, x))))),
                      n), x),
                  And(FreeQ(List(a, b, c, d), x), EqQ(Plus(m, n), C0), IntegerQ(n),
                      NeQ(Plus(Sqr(a), Sqr(b)), C0)))),
          IIntegrate(3087,
              Integrate(
                  Times(
                      Power($($s("§sin"),
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_))), m_DEFAULT),
                      Power(Plus(
                          Times($($s("§cos"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))), a_DEFAULT),
                          Times(b_DEFAULT, $($s("§sin"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Dist(Power(d, CN1),
                      Subst(
                          Integrate(
                              Times(
                                  Power(x, m), Power(Plus(a,
                                      Times(b, x)), n),
                                  Power(Power(Plus(C1, Sqr(x)), Times(C1D2, Plus(m, n, C2))), CN1)),
                              x),
                          x, Tan(Plus(c, Times(d, x)))),
                      x),
                  And(FreeQ(List(a, b, c, d), x), IntegerQ(n), IntegerQ(Times(C1D2,
                      Plus(m, n))), NeQ(n,
                          CN1),
                      Not(And(GtQ(n, C0), GtQ(m, C1)))))),
          IIntegrate(3088,
              Integrate(
                  Times(
                      Power($($s("§cos"),
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_))), m_DEFAULT),
                      Power(
                          Plus(
                              Times($($s("§cos"),
                                  Plus(c_DEFAULT, Times(d_DEFAULT, x_))), a_DEFAULT),
                              Times(b_DEFAULT,
                                  $($s("§sin"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Negate(
                      Dist(Power(d, CN1),
                          Subst(
                              Integrate(
                                  Times(Power(x, m), Power(Plus(b, Times(a, x)), n),
                                      Power(Power(Plus(C1, Sqr(x)), Times(C1D2, Plus(m, n, C2))),
                                          CN1)),
                                  x),
                              x, Cot(Plus(c, Times(d, x)))),
                          x)),
                  And(FreeQ(List(a, b, c, d), x), IntegerQ(n), IntegerQ(Times(C1D2,
                      Plus(m, n))), NeQ(n,
                          CN1),
                      Not(And(GtQ(n, C0), GtQ(m, C1)))))),
          IIntegrate(3089,
              Integrate(
                  Times(
                      Power($($s("§sin"),
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_))), m_DEFAULT),
                      Power(
                          Plus(
                              Times($($s("§cos"),
                                  Plus(c_DEFAULT, Times(d_DEFAULT, x_))), a_DEFAULT),
                              Times(b_DEFAULT,
                                  $($s("§sin"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))))),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandTrig(
                          Times(Power($($s("§sin"), Plus(c, Times(d, x))), m),
                              Power(
                                  Plus(
                                      Times(a, $($s("§cos"), Plus(c, Times(d, x)))), Times(b,
                                          $($s("§sin"), Plus(c, Times(d, x))))),
                                  n)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d), x), IntegerQ(m), IGtQ(n, C0)))),
          IIntegrate(3090,
              Integrate(
                  Times(
                      Power($($s("§cos"),
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_))), m_DEFAULT),
                      Power(Plus(
                          Times($($s("§cos"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))), a_DEFAULT),
                          Times(b_DEFAULT, $($s("§sin"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))))),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandTrig(
                          Times(Power($($s("§cos"), Plus(c, Times(d, x))), m),
                              Power(
                                  Plus(Times(a, $($s("§cos"), Plus(c, Times(d, x)))),
                                      Times(b, $($s("§sin"), Plus(c, Times(d, x))))),
                                  n)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d), x), IntegerQ(m), IGtQ(n, C0)))),
          IIntegrate(3091,
              Integrate(
                  Times(
                      Power($($s("§sin"),
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_))), m_DEFAULT),
                      Power(
                          Plus(
                              Times($($s("§cos"),
                                  Plus(c_DEFAULT, Times(d_DEFAULT, x_))), a_DEFAULT),
                              Times(b_DEFAULT, $($s("§sin"), Plus(c_DEFAULT,
                                  Times(d_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(a, n), Power(b,
                          n)),
                      Integrate(
                          Times(Power(Sin(Plus(c, Times(d, x))), m),
                              Power(
                                  Power(
                                      Plus(Times(b, Cos(Plus(c, Times(d, x)))),
                                          Times(a, Sin(Plus(c, Times(d, x))))),
                                      n),
                                  CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, m), x), EqQ(Plus(Sqr(a), Sqr(b)), C0), ILtQ(n, C0)))),
          IIntegrate(3092,
              Integrate(
                  Times(
                      Power($($s("§cos"),
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_))), m_DEFAULT),
                      Power(
                          Plus(
                              Times($($s("§cos"),
                                  Plus(c_DEFAULT, Times(d_DEFAULT, x_))), a_DEFAULT),
                              Times(b_DEFAULT,
                                  $($s("§sin"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Dist(Times(Power(a, n), Power(b, n)),
                      Integrate(
                          Times(Power(Cos(Plus(c, Times(d, x))), m),
                              Power(Power(Plus(Times(b, Cos(Plus(c, Times(d, x)))),
                                  Times(a, Sin(Plus(c, Times(d, x))))), n), CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, m), x), EqQ(Plus(Sqr(a), Sqr(b)), C0), ILtQ(n, C0)))),
          IIntegrate(3093,
              Integrate(
                  Times(
                      Power($($s("§sin"),
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_))), CN1),
                      Power(
                          Plus(
                              Times($($s("§cos"),
                                  Plus(c_DEFAULT, Times(d_DEFAULT, x_))), a_DEFAULT),
                              Times(b_DEFAULT,
                                  $($s("§sin"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(
                                  Power(
                                      Plus(
                                          Times(a, Cos(
                                              Plus(c, Times(d, x)))),
                                          Times(b, Sin(Plus(c, Times(d, x))))),
                                      Plus(n, C1)),
                                  Power(Times(a, d, Plus(n, C1)), CN1)),
                              x)),
                      Dist(Power(a, CN2),
                          Integrate(
                              Times(
                                  Power(
                                      Plus(
                                          Times(a, Cos(Plus(c, Times(d, x)))), Times(b,
                                              Sin(Plus(c, Times(d, x))))),
                                      Plus(n, C2)),
                                  Power(Sin(Plus(c, Times(d, x))), CN1)),
                              x),
                          x),
                      Negate(Dist(Times(b, Power(a, CN2)), Integrate(Power(
                          Plus(Times(a, Cos(Plus(c, Times(d, x)))),
                              Times(b, Sin(Plus(c, Times(d, x))))),
                          Plus(n, C1)), x), x))),
                  And(FreeQ(List(a, b, c, d), x), NeQ(Plus(Sqr(a), Sqr(b)), C0), LtQ(n, CN1)))),
          IIntegrate(3094,
              Integrate(
                  Times(Power($($s("§cos"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))), CN1),
                      Power(
                          Plus(
                              Times($($s("§cos"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))),
                                  a_DEFAULT),
                              Times(b_DEFAULT,
                                  $($s("§sin"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(
                              Power(
                                  Plus(
                                      Times(a, Cos(
                                          Plus(c, Times(d, x)))),
                                      Times(b, Sin(Plus(c, Times(d, x))))),
                                  Plus(n, C1)),
                              Power(Times(b, d, Plus(n, C1)), CN1)),
                          x),
                      Dist(Power(b, CN2),
                          Integrate(
                              Times(
                                  Power(
                                      Plus(
                                          Times(a, Cos(Plus(c, Times(d, x)))), Times(b,
                                              Sin(Plus(c, Times(d, x))))),
                                      Plus(n, C2)),
                                  Power(Cos(Plus(c, Times(d, x))), CN1)),
                              x),
                          x),
                      Negate(
                          Dist(Times(a, Power(b, CN2)),
                              Integrate(
                                  Power(
                                      Plus(Times(a, Cos(Plus(c, Times(d, x)))),
                                          Times(b, Sin(Plus(c, Times(d, x))))),
                                      Plus(n, C1)),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c, d), x), NeQ(Plus(Sqr(a), Sqr(b)), C0), LtQ(n, CN1)))),
          IIntegrate(3095,
              Integrate(
                  Times(
                      Power($($s("§sin"),
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_))), m_),
                      Power(
                          Plus(
                              Times($($s("§cos"),
                                  Plus(c_DEFAULT, Times(d_DEFAULT, x_))), a_DEFAULT),
                              Times(b_DEFAULT, $($s("§sin"), Plus(c_DEFAULT,
                                  Times(d_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Dist(
                              Plus(Sqr(a), Sqr(
                                  b)),
                              Integrate(
                                  Times(
                                      Power(Sin(Plus(c,
                                          Times(d, x))), Plus(m,
                                              C2)),
                                      Power(
                                          Plus(Times(a, Cos(Plus(c, Times(d, x)))), Times(b,
                                              Sin(Plus(c, Times(d, x))))),
                                          Subtract(n, C2))),
                                  x),
                              x)),
                      Dist(Sqr(a),
                          Integrate(
                              Times(
                                  Power(Sin(
                                      Plus(c, Times(d, x))), m),
                                  Power(
                                      Plus(
                                          Times(a, Cos(Plus(c, Times(d, x)))), Times(b,
                                              Sin(Plus(c, Times(d, x))))),
                                      Subtract(n, C2))),
                              x),
                          x),
                      Dist(Times(C2, b),
                          Integrate(Times(Power(Sin(Plus(c, Times(d, x))), Plus(m, C1)),
                              Power(Plus(Times(a, Cos(Plus(c, Times(d, x)))),
                                  Times(b, Sin(Plus(c, Times(d, x))))),
                                  Subtract(n, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d), x), NeQ(Plus(Sqr(a), Sqr(b)), C0), GtQ(n, C1),
                      LtQ(m, CN1)))),
          IIntegrate(3096,
              Integrate(
                  Times(
                      Power($($s("§cos"),
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_))), m_),
                      Power(
                          Plus(
                              Times($($s("§cos"),
                                  Plus(c_DEFAULT, Times(d_DEFAULT, x_))), a_DEFAULT),
                              Times(b_DEFAULT, $($s("§sin"), Plus(c_DEFAULT,
                                  Times(d_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Dist(
                              Plus(Sqr(a), Sqr(
                                  b)),
                              Integrate(
                                  Times(
                                      Power(Cos(Plus(c, Times(d, x))), Plus(m,
                                          C2)),
                                      Power(
                                          Plus(
                                              Times(a, Cos(Plus(c, Times(d, x)))), Times(b,
                                                  Sin(Plus(c, Times(d, x))))),
                                          Subtract(n, C2))),
                                  x),
                              x)),
                      Dist(Times(C2, a),
                          Integrate(
                              Times(
                                  Power(Cos(Plus(c,
                                      Times(d, x))), Plus(m,
                                          C1)),
                                  Power(
                                      Plus(
                                          Times(a, Cos(Plus(c, Times(d, x)))), Times(b,
                                              Sin(Plus(c, Times(d, x))))),
                                      Subtract(n, C1))),
                              x),
                          x),
                      Dist(Sqr(b),
                          Integrate(
                              Times(Power(Cos(Plus(c, Times(d, x))), m),
                                  Power(Plus(Times(a, Cos(Plus(c, Times(d, x)))),
                                      Times(b, Sin(Plus(c, Times(d, x))))),
                                      Subtract(n, C2))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d), x), NeQ(Plus(Sqr(a),
                      Sqr(b)), C0), GtQ(n,
                          C1),
                      LtQ(m, CN1)))),
          IIntegrate(3097,
              Integrate(
                  Times(
                      $($s("§sin"), Plus(c_DEFAULT,
                          Times(d_DEFAULT, x_))),
                      Power(
                          Plus(
                              Times($($s("§cos"),
                                  Plus(c_DEFAULT, Times(d_DEFAULT, x_))), a_DEFAULT),
                              Times(b_DEFAULT, $($s("§sin"), Plus(c_DEFAULT,
                                  Times(d_DEFAULT, x_))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(Times(b, x,
                          Power(Plus(Sqr(a), Sqr(b)), CN1)), x),
                      Dist(Times(a, Power(Plus(Sqr(a), Sqr(b)), CN1)),
                          Integrate(
                              Times(
                                  Subtract(Times(b, Cos(Plus(c, Times(d, x)))),
                                      Times(a, Sin(Plus(c, Times(d, x))))),
                                  Power(Plus(Times(a, Cos(Plus(c, Times(d, x)))),
                                      Times(b, Sin(Plus(c, Times(d, x))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d), x), NeQ(Plus(Sqr(a), Sqr(b)), C0)))),
          IIntegrate(3098,
              Integrate(
                  Times(
                      $($s("§cos"), Plus(c_DEFAULT,
                          Times(d_DEFAULT, x_))),
                      Power(
                          Plus(
                              Times($($s("§cos"),
                                  Plus(c_DEFAULT, Times(d_DEFAULT, x_))), a_DEFAULT),
                              Times(b_DEFAULT, $($s("§sin"), Plus(c_DEFAULT,
                                  Times(d_DEFAULT, x_))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Plus(Simp(Times(a, x, Power(Plus(Sqr(a), Sqr(b)), CN1)), x),
                      Dist(Times(b, Power(Plus(Sqr(a), Sqr(b)), CN1)),
                          Integrate(Times(
                              Subtract(Times(b, Cos(Plus(c, Times(d, x)))),
                                  Times(a, Sin(Plus(c, Times(d, x))))),
                              Power(Plus(Times(a, Cos(Plus(c, Times(d, x)))),
                                  Times(b, Sin(Plus(c, Times(d, x))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d), x), NeQ(Plus(Sqr(a), Sqr(b)), C0)))),
          IIntegrate(3099,
              Integrate(
                  Times(Power($($s("§sin"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))), m_),
                      Power(
                          Plus(
                              Times($($s("§cos"), Plus(c_DEFAULT,
                                  Times(d_DEFAULT, x_))), a_DEFAULT),
                              Times(b_DEFAULT, $($s("§sin"), Plus(c_DEFAULT,
                                  Times(d_DEFAULT, x_))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(
                                  a, Power(Sin(Plus(c,
                                      Times(d, x))), Subtract(m,
                                          C1)),
                                  Power(Times(d, Plus(Sqr(a), Sqr(b)), Subtract(m, C1)), CN1)),
                              x)),
                      Dist(Times(Sqr(a), Power(Plus(Sqr(a), Sqr(b)), CN1)),
                          Integrate(Times(Power(Sin(Plus(c, Times(d, x))), Subtract(m, C2)),
                              Power(Plus(Times(a, Cos(Plus(c, Times(d, x)))),
                                  Times(b, Sin(Plus(c, Times(d, x))))), CN1)),
                              x),
                          x),
                      Dist(
                          Times(b, Power(Plus(Sqr(a), Sqr(b)), CN1)), Integrate(
                              Power(Sin(Plus(c, Times(d, x))), Subtract(m, C1)), x),
                          x)),
                  And(FreeQ(List(a, b, c, d), x), NeQ(Plus(Sqr(a), Sqr(b)), C0), GtQ(m, C1)))),
          IIntegrate(3100,
              Integrate(
                  Times(
                      Power($($s("§cos"),
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_))), m_),
                      Power(Plus(
                          Times($($s("§cos"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))), a_DEFAULT),
                          Times(b_DEFAULT, $($s("§sin"), Plus(c_DEFAULT, Times(d_DEFAULT, x_))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(
                              b, Power(Cos(Plus(c, Times(d, x))), Subtract(m,
                                  C1)),
                              Power(Times(d, Plus(Sqr(a), Sqr(b)), Subtract(m, C1)), CN1)),
                          x),
                      Dist(Times(a, Power(Plus(Sqr(a), Sqr(b)), CN1)),
                          Integrate(Power(Cos(Plus(c, Times(d, x))), Subtract(m, C1)), x), x),
                      Dist(Times(Sqr(b), Power(Plus(Sqr(a), Sqr(b)), CN1)),
                          Integrate(Times(Power(Cos(Plus(c, Times(d, x))), Subtract(m, C2)),
                              Power(Plus(Times(a, Cos(Plus(c, Times(d, x)))),
                                  Times(b, Sin(Plus(c, Times(d, x))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d), x), NeQ(Plus(Sqr(a), Sqr(b)), C0), GtQ(m, C1)))));
}
