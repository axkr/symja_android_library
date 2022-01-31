package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.ArcTanh;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.C5;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN1D2;
import static org.matheclipse.core.expression.F.CN2;
import static org.matheclipse.core.expression.F.CN4;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Cos;
import static org.matheclipse.core.expression.F.Csc;
import static org.matheclipse.core.expression.F.Expand;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.QQ;
import static org.matheclipse.core.expression.F.Sec;
import static org.matheclipse.core.expression.F.Simplify;
import static org.matheclipse.core.expression.F.Sin;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Sqrt;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Tan;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.f_DEFAULT;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_DEFAULT;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.d;
import static org.matheclipse.core.expression.S.e;
import static org.matheclipse.core.expression.S.f;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FracPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ILtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntegersQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RationalQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Subst;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules175 {
  public static IAST RULES =
      List(
          IIntegrate(3501,
              Integrate(
                  Times(
                      Power(
                          Times(d_DEFAULT, $($s("§sec"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          m_DEFAULT),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(
                              Sqr(d), Power(Times(d, Sec(
                                  Plus(e, Times(f, x)))), Subtract(m,
                                      C2)),
                              Power(Plus(a, Times(b, Tan(Plus(e, Times(f, x))))), Plus(n, C1)),
                              Power(Times(b, f, Subtract(Plus(m, n), C1)), CN1)),
                          x),
                      Dist(
                          Times(Sqr(d), Subtract(m, C2), Power(Times(a, Subtract(Plus(m, n), C1)),
                              CN1)),
                          Integrate(
                              Times(
                                  Power(Times(d, Sec(Plus(e, Times(f, x)))), Subtract(m,
                                      C2)),
                                  Power(Plus(a, Times(b, Tan(Plus(e, Times(f, x))))), Plus(n, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, d, e, f), x), EqQ(Plus(Sqr(a), Sqr(b)), C0), LtQ(n, C0),
                      GtQ(m, C1), Not(ILtQ(Plus(m, n), C0)), NeQ(Subtract(Plus(m, n), C1), C0),
                      IntegersQ(Times(C2, m), Times(C2, n))))),
          IIntegrate(3502,
              Integrate(
                  Times(
                      Power(
                          Times(d_DEFAULT, $($s("§sec"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          m_DEFAULT),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§tan"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(a, Power(Times(d, Sec(Plus(e, Times(f, x)))), m),
                              Power(Plus(a, Times(b, Tan(Plus(e, Times(f, x))))),
                                  n),
                              Power(Times(b, f, Plus(m, Times(C2, n))), CN1)),
                          x),
                      Dist(Times(Simplify(Plus(m, n)), Power(Times(a, Plus(m, Times(C2, n))), CN1)),
                          Integrate(
                              Times(Power(Times(d, Sec(Plus(e, Times(f, x)))), m),
                                  Power(Plus(a, Times(b, Tan(Plus(e, Times(f, x))))), Plus(n, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, d, e, f, m), x), EqQ(Plus(Sqr(a), Sqr(
                      b)), C0), LtQ(n, C0), NeQ(Plus(m,
                          Times(C2, n)), C0),
                      IntegersQ(Times(C2, m), Times(C2, n))))),
          IIntegrate(3503,
              Integrate(
                  Times(
                      Power(
                          Times(d_DEFAULT, $($s("§sec"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          m_DEFAULT),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§tan"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(
                              b, Power(Times(d,
                                  Sec(Plus(e, Times(f, x)))), m),
                              Power(Plus(a, Times(b,
                                  Tan(Plus(e, Times(f, x))))), Subtract(n,
                                      C1)),
                              Power(Times(f, Simplify(Subtract(Plus(m, n), C1))), CN1)),
                          x),
                      Dist(
                          Times(
                              a, Subtract(Plus(m, Times(C2, n)),
                                  C2),
                              Power(Simplify(Subtract(Plus(m, n), C1)), CN1)),
                          Integrate(
                              Times(Power(Times(d, Sec(Plus(e, Times(f, x)))), m),
                                  Power(
                                      Plus(a, Times(b, Tan(Plus(e, Times(f, x))))), Subtract(n,
                                          C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, d, e, f, m, n), x), EqQ(Plus(Sqr(a), Sqr(
                      b)), C0), IGtQ(Simplify(
                          Subtract(Plus(m, n), C1)), C0),
                      RationalQ(n)))),
          IIntegrate(3504,
              Integrate(
                  Times(
                      Power(
                          Times(d_DEFAULT, $($s("§sec"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          m_DEFAULT),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§tan"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(a, Power(Times(d, Sec(Plus(e, Times(f, x)))), m),
                              Power(Plus(a,
                                  Times(b, Tan(Plus(e, Times(f, x))))), n),
                              Power(Times(b, f, Plus(m, Times(C2, n))), CN1)),
                          x),
                      Dist(
                          Times(Simplify(Plus(m,
                              n)), Power(Times(a, Plus(m, Times(C2, n))),
                                  CN1)),
                          Integrate(
                              Times(Power(Times(d, Sec(Plus(e, Times(f, x)))), m),
                                  Power(Plus(a, Times(b, Tan(Plus(e, Times(f, x))))), Plus(n, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, d, e, f, m, n), x), EqQ(Plus(Sqr(a), Sqr(
                      b)), C0), ILtQ(Simplify(
                          Plus(m, n)), C0),
                      NeQ(Plus(m, Times(C2, n)), C0)))),
          IIntegrate(3505,
              Integrate(
                  Times(
                      Power(
                          Times(d_DEFAULT, $($s("§sec"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          m_DEFAULT),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§tan"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_))))),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Times(Power(Times(d, Sec(Plus(e, Times(f, x)))), m),
                      Power(
                          Times(Power(Plus(a, Times(b, Tan(Plus(e, Times(f, x))))), Times(C1D2, m)),
                              Power(Subtract(a, Times(b, Tan(Plus(e, Times(f, x))))),
                                  Times(C1D2, m))),
                          CN1)),
                      Integrate(
                          Times(
                              Power(
                                  Plus(a, Times(b, Tan(Plus(e, Times(f, x))))), Plus(Times(C1D2, m),
                                      n)),
                              Power(
                                  Subtract(a, Times(b, Tan(Plus(e, Times(f, x))))), Times(C1D2,
                                      m))),
                          x),
                      x),
                  And(FreeQ(List(a, b, d, e, f, m, n), x), EqQ(Plus(Sqr(a), Sqr(b)), C0)))),
          IIntegrate(3506,
              Integrate(
                  Times(Power($($s("§sec"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), m_),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Power(Times(b,
                          f), CN1),
                      Subst(
                          Integrate(Times(Power(Plus(a, x), n),
                              Power(Plus(C1, Times(Sqr(x), Power(b, CN2))),
                                  Subtract(Times(C1D2, m), C1))),
                              x),
                          x, Times(b, Tan(Plus(e, Times(f, x))))),
                      x),
                  And(FreeQ(List(a, b, e, f,
                      n), x), NeQ(Plus(Sqr(a), Sqr(b)),
                          C0),
                      IntegerQ(Times(C1D2, m))))),
          IIntegrate(3507,
              Integrate(
                  Times(
                      Power($($s("§sec"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), CN1),
                      Sqr(Plus(a_, Times(b_DEFAULT, $($s("§tan"),
                          Plus(e_DEFAULT, Times(f_DEFAULT, x_))))))),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(Times(Sqr(b), ArcTanh(Sin(Plus(e, Times(f, x)))), Power(f,
                          CN1)), x),
                      Negate(Simp(Times(C2, a, b, Cos(Plus(e, Times(f, x))), Power(f, CN1)),
                          x)),
                      Simp(
                          Times(Subtract(Sqr(a), Sqr(b)), Sin(Plus(e, Times(f, x))), Power(f, CN1)),
                          x)),
                  And(FreeQ(List(a, b, e, f), x), NeQ(Plus(Sqr(a), Sqr(b)), C0)))),
          IIntegrate(3508,
              Integrate(
                  Times(
                      Power(
                          Times(d_DEFAULT, $($s("§sec"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          m_DEFAULT),
                      Sqr(Plus(
                          a_, Times(b_DEFAULT, $($s("§tan"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_))))))),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(b, Power(Times(d, Sec(Plus(e, Times(f, x)))), m),
                              Plus(a, Times(b, Tan(Plus(e, Times(f, x))))), Power(
                                  Times(f, Plus(m, C1)), CN1)),
                          x),
                      Dist(
                          Power(Plus(m,
                              C1), CN1),
                          Integrate(
                              Times(
                                  Power(Times(d, Sec(
                                      Plus(e, Times(f, x)))), m),
                                  Plus(
                                      Times(Sqr(a), Plus(m, C1)), Negate(Sqr(
                                          b)),
                                      Times(a, b, Plus(m, C2), Tan(Plus(e, Times(f, x)))))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, d, e, f, m), x), NeQ(Plus(Sqr(a),
                      Sqr(b)), C0), NeQ(m,
                          CN1)))),
          IIntegrate(3509,
              Integrate(
                  Times($($s("§sec"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Negate(
                      Dist(Power(f, CN1),
                          Subst(Integrate(Power(Subtract(Plus(Sqr(a), Sqr(b)), Sqr(x)), CN1), x), x,
                              Times(
                                  Subtract(b, Times(a, Tan(Plus(e, Times(f, x))))), Power(
                                      Sec(Plus(e, Times(f, x))), CN1))),
                          x)),
                  And(FreeQ(List(a, b, e, f), x), NeQ(Plus(Sqr(a), Sqr(b)), C0)))),
          IIntegrate(3510,
              Integrate(
                  Times(
                      Power(
                          Times(d_DEFAULT,
                              $($s("§sec"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          m_),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§tan"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Dist(Times(Sqr(d), Power(b, CN2)),
                              Integrate(
                                  Times(
                                      Power(Times(d, Sec(Plus(e, Times(f, x)))), Subtract(m,
                                          C2)),
                                      Subtract(a, Times(b, Tan(Plus(e, Times(f, x)))))),
                                  x),
                              x)),
                      Dist(
                          Times(Sqr(d), Plus(Sqr(a), Sqr(b)), Power(b,
                              CN2)),
                          Integrate(
                              Times(
                                  Power(Times(d, Sec(Plus(e, Times(f, x)))), Subtract(m,
                                      C2)),
                                  Power(Plus(a, Times(b, Tan(Plus(e, Times(f, x))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, d, e, f), x), NeQ(Plus(Sqr(a), Sqr(b)), C0), IGtQ(m, C1)))),
          IIntegrate(3511,
              Integrate(
                  Times(
                      Power(
                          Times(d_DEFAULT,
                              $($s("§sec"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          m_),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§tan"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(
                          Power(Plus(Sqr(a),
                              Sqr(b)), CN1),
                          Integrate(
                              Times(
                                  Power(Times(d, Sec(Plus(e, Times(f, x)))), m), Subtract(a,
                                      Times(b, Tan(Plus(e, Times(f, x)))))),
                              x),
                          x),
                      Dist(
                          Times(Sqr(b), Power(Times(Sqr(d), Plus(Sqr(a), Sqr(b))),
                              CN1)),
                          Integrate(
                              Times(
                                  Power(Times(d, Sec(
                                      Plus(e, Times(f, x)))), Plus(m,
                                          C2)),
                                  Power(Plus(a, Times(b, Tan(Plus(e, Times(f, x))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, d, e, f), x), NeQ(Plus(Sqr(a), Sqr(b)), C0), ILtQ(m, C0)))),
          IIntegrate(3512,
              Integrate(
                  Times(
                      Power(
                          Times(d_DEFAULT, $($s("§sec"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          m_DEFAULT),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(d, Times(C2, IntPart(Times(C1D2, m)))),
                          Power(
                              Times(d, Sec(Plus(e, Times(f, x)))), Times(C2,
                                  FracPart(Times(C1D2, m)))),
                          Power(
                              Times(b, f,
                                  Power(Sqr(Sec(Plus(e, Times(f, x)))), FracPart(Times(C1D2, m)))),
                              CN1)),
                      Subst(
                          Integrate(
                              Times(Power(Plus(a, x), n),
                                  Power(
                                      Plus(C1, Times(Sqr(x), Power(b, CN2))), Subtract(
                                          Times(C1D2, m), C1))),
                              x),
                          x, Times(b, Tan(Plus(e, Times(f, x))))),
                      x),
                  And(FreeQ(List(a, b, d, e, f, m,
                      n), x), NeQ(Plus(Sqr(a), Sqr(b)),
                          C0),
                      Not(IntegerQ(Times(C1D2, m)))))),
          IIntegrate(3513,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), d_DEFAULT),
                          CN1D2),
                      Sqrt(
                          Plus(a_,
                              Times(b_DEFAULT, $($s("§tan"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_))))))),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(CN4, b, Power(f,
                          CN1)),
                      Subst(
                          Integrate(
                              Times(Sqr(x), Power(Plus(Times(Sqr(a), Sqr(d)), Power(x, C4)),
                                  CN1)),
                              x),
                          x,
                          Times(
                              Sqrt(Times(d,
                                  Cos(Plus(e, Times(f, x))))),
                              Sqrt(Plus(a, Times(b, Tan(Plus(e, Times(f, x)))))))),
                      x),
                  And(FreeQ(List(a, b, d, e, f), x), EqQ(Plus(Sqr(a), Sqr(b)), C0)))),
          IIntegrate(3514,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cos"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_))), d_DEFAULT),
                          QQ(-3L, 2L)),
                      Power(
                          Plus(a_, Times(b_DEFAULT, $($s("§tan"), Plus(
                              e_DEFAULT, Times(f_DEFAULT, x_))))),
                          CN1D2)),
                  x_Symbol),
              Condition(
                  Dist(
                      Power(Times(d, Cos(Plus(e, Times(f, x))),
                          Sqrt(Subtract(a, Times(b, Tan(Plus(e, Times(f, x)))))), Sqrt(
                              Plus(a, Times(b, Tan(Plus(e, Times(f, x))))))),
                          CN1),
                      Integrate(
                          Times(
                              Sqrt(Subtract(a, Times(b, Tan(Plus(e, Times(f, x)))))), Power(Times(d,
                                  Cos(Plus(e, Times(f, x)))),
                                  CN1D2)),
                          x),
                      x),
                  And(FreeQ(List(a, b, d, e, f), x), EqQ(Plus(Sqr(a), Sqr(b)), C0)))),
          IIntegrate(3515,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              d_DEFAULT),
                          m_),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(Times(d, Cos(Plus(e, Times(f, x)))),
                              m),
                          Power(Times(d, Sec(Plus(e, Times(f, x)))), m)),
                      Integrate(
                          Times(
                              Power(Plus(a, Times(b, Tan(Plus(e, Times(f, x))))), n), Power(Power(
                                  Times(d, Sec(Plus(e, Times(f, x)))), m), CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, b, d, e, f, m, n), x), Not(IntegerQ(m))))),
          IIntegrate(3516,
              Integrate(
                  Times(
                      Power($($s("§sin"),
                          Plus(e_DEFAULT, Times(f_DEFAULT, x_))), m_),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(b, Power(f,
                          CN1)),
                      Subst(
                          Integrate(
                              Times(Power(x, m), Power(Plus(a, x), n),
                                  Power(Power(Plus(Sqr(b), Sqr(x)), Plus(Times(C1D2, m), C1)),
                                      CN1)),
                              x),
                          x, Times(b, Tan(Plus(e, Times(f, x))))),
                      x),
                  And(FreeQ(List(a, b, e, f, n), x), IntegerQ(Times(C1D2, m))))),
          IIntegrate(3517,
              Integrate(
                  Times(Power($($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), m_DEFAULT),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Expand(Times(Power(Sin(Plus(e, Times(f, x))), m),
                          Power(Plus(a, Times(b, Tan(Plus(e, Times(f, x))))), n)), x),
                      x),
                  And(FreeQ(List(a, b, e, f), x), IntegerQ(Times(C1D2, Subtract(m, C1))),
                      IGtQ(n, C0)))),
          IIntegrate(3518,
              Integrate(
                  Times(Power($($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), m_DEFAULT),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(
                          Power(Sin(
                              Plus(e, Times(f, x))), m),
                          Power(
                              Plus(
                                  Times(a, Cos(Plus(e, Times(f, x)))), Times(b,
                                      Sin(Plus(e, Times(f, x))))),
                              n),
                          Power(Power(Cos(Plus(e, Times(f, x))), n), CN1)),
                      x),
                  And(FreeQ(List(a, b, e, f), x), IntegerQ(Times(C1D2, Subtract(m, C1))),
                      ILtQ(n, C0),
                      Or(And(LtQ(m, C5), GtQ(n, CN4)), And(EqQ(m, C5), EqQ(n, CN1)))))),
          IIntegrate(3519,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              d_DEFAULT),
                          m_),
                      Power(
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT, $($s("§tan"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_))))),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(Times(d, Csc(Plus(e, Times(f, x)))), FracPart(
                              m)),
                          Power(Times(Sin(Plus(e, Times(f, x))), Power(d, CN1)), FracPart(m))),
                      Integrate(
                          Times(
                              Power(Plus(a,
                                  Times(b, Tan(Plus(e, Times(f, x))))), n),
                              Power(Power(Times(Sin(Plus(e, Times(f, x))), Power(d, CN1)), m),
                                  CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, b, d, e, f, m, n), x), Not(IntegerQ(m))))),
          IIntegrate(3520,
              Integrate(
                  Times(Power($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), m_DEFAULT),
                      Power($($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), p_DEFAULT),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  $($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(Times(Power(Cos(Plus(e, Times(f, x))), Subtract(m, n)),
                      Power(Sin(Plus(e, Times(f, x))), p),
                      Power(Plus(Times(a, Cos(Plus(e, Times(f, x)))),
                          Times(b, Sin(Plus(e, Times(f, x))))), n)),
                      x),
                  And(FreeQ(List(a, b, e, f, m, p), x), IntegerQ(n)))));
}
