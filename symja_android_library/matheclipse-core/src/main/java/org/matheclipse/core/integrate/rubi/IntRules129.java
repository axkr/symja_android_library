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
import static org.matheclipse.core.expression.F.Csc;
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
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Sin;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Sqrt;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Tan;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.f_DEFAULT;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.e;
import static org.matheclipse.core.expression.S.f;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FracPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FreeFactors;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntegersQ;
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
class IntRules129 {
  public static IAST RULES =
      List(
          IIntegrate(2581,
              Integrate(
                  Times(
                      Power(
                          Times(b_DEFAULT,
                              $($s("§sec"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          n_),
                      Power(
                          Times(a_DEFAULT, $($s("§sin"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          m_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(
                              Power(Times(a, Sin(Plus(e,
                                  Times(f, x)))), Plus(m, C1)),
                              Power(Times(b, Sec(Plus(e, Times(f, x)))),
                                  Plus(n, C1)),
                              Power(Times(a, b, f, Plus(m, C1)), CN1)),
                          x),
                      Dist(Times(Plus(n, C1), Power(Times(Sqr(a), Sqr(b), Plus(m, C1)), CN1)),
                          Integrate(
                              Times(Power(Times(a, Sin(Plus(e, Times(f, x)))), Plus(m, C2)),
                                  Power(Times(b, Sec(Plus(e, Times(f, x)))), Plus(n, C2))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, e, f), x), LtQ(n, CN1), LtQ(m, CN1),
                      IntegersQ(Times(C2, m), Times(C2, n))))),
          IIntegrate(2582,
              Integrate(
                  Times(
                      Power(
                          Times(b_DEFAULT,
                              $($s("§sec"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          n_),
                      Power(
                          Times(a_DEFAULT, $($s("§sin"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          m_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Power(Times(a, Sin(Plus(e, Times(f, x)))), Plus(m, C1)),
                              Power(Times(b, Sec(Plus(e, Times(f, x)))), Plus(n, C1)), Power(
                                  Times(a, b, f, Subtract(m, n)), CN1)),
                          x),
                      Dist(Times(Plus(n, C1), Power(Times(Sqr(b), Subtract(m, n)), CN1)),
                          Integrate(
                              Times(Power(Times(a, Sin(Plus(e, Times(f, x)))), m),
                                  Power(Times(b, Sec(Plus(e, Times(f, x)))), Plus(n, C2))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, e, f, m), x), LtQ(n, CN1), NeQ(Subtract(m,
                      n), C0), IntegersQ(Times(C2, m),
                          Times(C2, n))))),
          IIntegrate(2583,
              Integrate(
                  Times(
                      Power(
                          Times(b_DEFAULT,
                              $($s("§sec"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          n_),
                      Power(Times(a_DEFAULT, $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          m_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(a, b,
                                  Power(Times(a, Sin(Plus(e, Times(f, x)))), Subtract(m, C1)),
                                  Power(Times(b, Sec(
                                      Plus(e, Times(f, x)))), Subtract(n,
                                          C1)),
                                  Power(Times(f, Subtract(m, n)), CN1)),
                              x)),
                      Dist(Times(Sqr(a), Subtract(m, C1), Power(Subtract(m, n), CN1)),
                          Integrate(
                              Times(Power(Times(a, Sin(Plus(e, Times(f, x)))), Subtract(m, C2)),
                                  Power(Times(b, Sec(Plus(e, Times(f, x)))), n)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, e, f, n), x), GtQ(m, C1), NeQ(Subtract(m,
                      n), C0), IntegersQ(Times(C2, m),
                          Times(C2, n))))),
          IIntegrate(2584,
              Integrate(
                  Times(
                      Power(
                          Times(b_DEFAULT,
                              $($s("§sec"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          n_),
                      Power(
                          Times(a_DEFAULT, $($s("§sin"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          m_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(b, Power(Times(a, Sin(Plus(e, Times(f, x)))), Plus(m, C1)),
                              Power(Times(b, Sec(
                                  Plus(e, Times(f, x)))), Subtract(n,
                                      C1)),
                              Power(Times(a, f, Plus(m, C1)), CN1)),
                          x),
                      Dist(Times(Plus(m, Negate(n), C2), Power(Times(Sqr(a), Plus(m, C1)), CN1)),
                          Integrate(
                              Times(
                                  Power(Times(a, Sin(Plus(e, Times(f, x)))), Plus(m,
                                      C2)),
                                  Power(Times(b, Sec(Plus(e, Times(f, x)))), n)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, e, f,
                      n), x), LtQ(m,
                          CN1),
                      IntegersQ(Times(C2, m), Times(C2, n))))),
          IIntegrate(2585,
              Integrate(
                  Times(
                      Power(
                          Times(b_DEFAULT,
                              $($s("§sec"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          n_),
                      Power(
                          Times(a_DEFAULT, $($s("§sin"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          m_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(Times(b, Cos(Plus(e, Times(f, x)))),
                              n),
                          Power(Times(b, Sec(Plus(e, Times(f, x)))), n)),
                      Integrate(
                          Times(
                              Power(Times(a, Sin(Plus(e, Times(f, x)))), m), Power(
                                  Power(Times(b, Cos(Plus(e, Times(f, x)))), n), CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, b, e, f, m, n), x), IntegerQ(Subtract(m, C1D2)),
                      IntegerQ(Subtract(n, C1D2))))),
          IIntegrate(2586,
              Integrate(
                  Times(
                      Power(
                          Times(b_DEFAULT,
                              $($s("§sec"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          n_),
                      Power(
                          Times(a_DEFAULT, $($s("§sin"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          m_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          C1, Power(Times(b, Cos(Plus(e, Times(f, x)))), Plus(n,
                              C1)),
                          Power(Times(b, Sec(Plus(e, Times(f, x)))), Plus(n, C1)), Power(b, CN2)),
                      Integrate(
                          Times(
                              Power(Times(a, Sin(Plus(e, Times(f, x)))), m), Power(
                                  Power(Times(b, Cos(Plus(e, Times(f, x)))), n), CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, b, e, f, m, n), x), Not(IntegerQ(m)), Not(IntegerQ(n)),
                      LtQ(n, C1)))),
          IIntegrate(2587,
              Integrate(
                  Times(
                      Power(
                          Times(b_DEFAULT,
                              $($s("§sec"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          n_),
                      Power(
                          Times(a_DEFAULT, $($s("§sin"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          m_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Sqr(b), Power(Times(b, Cos(Plus(e, Times(f, x)))), Subtract(n,
                              C1)),
                          Power(Times(b, Sec(Plus(e, Times(f, x)))), Subtract(n, C1))),
                      Integrate(
                          Times(
                              Power(Times(a, Sin(Plus(e, Times(f, x)))), m), Power(
                                  Power(Times(b, Cos(Plus(e, Times(f, x)))), n), CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, b, e, f, m, n), x), Not(IntegerQ(m)), Not(IntegerQ(n))))),
          IIntegrate(2588,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              b_DEFAULT),
                          n_),
                      Power(Times(a_DEFAULT, $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          m_DEFAULT)),
                  x_Symbol),
              Condition(Dist(Times(Power(Times(a, b), IntPart(n)),
                  Power(Times(a, Sin(Plus(e, Times(f, x)))), FracPart(n)), Power(
                      Times(b, Csc(Plus(e, Times(f, x)))), FracPart(n))),
                  Integrate(Power(Times(a, Sin(Plus(e, Times(f, x)))), Subtract(m, n)), x), x),
                  And(FreeQ(List(a, b, e, f, m, n), x), Not(IntegerQ(m)), Not(IntegerQ(n))))),
          IIntegrate(2589,
              Integrate(
                  Times(
                      Power(
                          Times(a_DEFAULT,
                              $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          m_),
                      Power(
                          Times(b_DEFAULT, $($s("§tan"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          n_)),
                  x_Symbol),
              Condition(
                  Negate(
                      Simp(
                          Times(b, Power(Times(a, Sin(Plus(e, Times(f, x)))), m),
                              Power(Times(b, Tan(Plus(e, Times(f, x)))),
                                  Subtract(n, C1)),
                              Power(Times(f, m), CN1)),
                          x)),
                  And(FreeQ(List(a, b, e, f, m, n), x), EqQ(Subtract(Plus(m, n), C1), C0)))),
          IIntegrate(2590,
              Integrate(
                  Times(
                      Power($($s("§sin"), Plus(e_DEFAULT,
                          Times(f_DEFAULT, x_))), m_DEFAULT),
                      Power($($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Negate(
                      Dist(Power(f, CN1),
                          Subst(
                              Integrate(
                                  Times(
                                      Power(Subtract(C1, Sqr(x)), Times(C1D2,
                                          Subtract(Plus(m, n), C1))),
                                      Power(Power(x, n), CN1)),
                                  x),
                              x, Cos(Plus(e, Times(f, x)))),
                          x)),
                  And(FreeQ(List(e,
                      f), x), IntegersQ(m, n,
                          Times(C1D2, Subtract(Plus(m, n), C1)))))),
          IIntegrate(2591,
              Integrate(
                  Times(
                      Power($($s("§sin"),
                          Plus(e_DEFAULT, Times(f_DEFAULT, x_))), m_),
                      Power(
                          Times(b_DEFAULT, $($s("§tan"), Plus(e_DEFAULT,
                              Times(f_DEFAULT, x_)))),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set($s("ff"),
                          FreeFactors(Tan(Plus(e, Times(f, x))), x))),
                      Dist(
                          Times(b, $s("ff"), Power(f,
                              CN1)),
                          Subst(
                              Integrate(
                                  Times(Power(Times($s("ff"), x), Plus(m, n)),
                                      Power(Power(Plus(Sqr(b), Times(Sqr($s("ff")), Sqr(x))),
                                          Plus(Times(C1D2, m), C1)), CN1)),
                                  x),
                              x, Times(b, Tan(Plus(e, Times(f, x))), Power($s("ff"), CN1))),
                          x)),
                  And(FreeQ(List(b, e, f, n), x), IntegerQ(Times(C1D2, m))))),
          IIntegrate(2592,
              Integrate(
                  Times(
                      Power(
                          Times(a_DEFAULT, $($s("§sin"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          m_DEFAULT),
                      Power($($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), n_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(List(Set($s("ff"), FreeFactors(Sin(Plus(e, Times(f, x))), x))),
                      Dist(Times($s("ff"), Power(f, CN1)),
                          Subst(Integrate(Times(Power(Times($s("ff"), x), Plus(m, n)),
                              Power(Power(Subtract(Sqr(a), Times(Sqr($s("ff")), Sqr(x))),
                                  Times(C1D2, Plus(n, C1))), CN1)),
                              x), x, Times(a, Sin(Plus(e, Times(f, x))), Power($s("ff"), CN1))),
                          x)),
                  And(FreeQ(List(a, e, f, m), x), IntegerQ(Times(C1D2, Plus(n, C1)))))),
          IIntegrate(2593,
              Integrate(
                  Times(
                      Power(
                          Times(a_DEFAULT, $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          m_),
                      Power(
                          Times(b_DEFAULT, $($s("§tan"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          n_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(
                              b, Power(Times(a, Sin(Plus(e,
                                  Times(f, x)))), Plus(m, C2)),
                              Power(Times(b, Tan(Plus(e, Times(f, x)))), Subtract(n, C1)),
                              Power(Times(Sqr(a), f, Subtract(n, C1)), CN1)),
                          x),
                      Dist(Times(Sqr(b), Plus(m, C2), Power(Times(Sqr(a), Subtract(n, C1)), CN1)),
                          Integrate(
                              Times(Power(Times(a, Sin(Plus(e, Times(f, x)))), Plus(m, C2)),
                                  Power(Times(b, Tan(Plus(e, Times(f, x)))), Subtract(n, C2))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, e, f), x), GtQ(n, C1),
                      Or(LtQ(m, CN1), And(EqQ(m, CN1),
                          EqQ(n, QQ(3L, 2L)))),
                      IntegersQ(Times(C2, m), Times(C2, n))))),
          IIntegrate(2594,
              Integrate(
                  Times(
                      Power(
                          Times(a_DEFAULT, $($s("§sin"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          m_DEFAULT),
                      Power(
                          Times(b_DEFAULT, $($s("§tan"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          n_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(
                              b, Power(Times(a, Sin(Plus(e,
                                  Times(f, x)))), m),
                              Power(Times(b, Tan(
                                  Plus(e, Times(f, x)))), Subtract(n,
                                      C1)),
                              Power(Times(f, Subtract(n, C1)), CN1)),
                          x),
                      Dist(Times(Sqr(b), Subtract(Plus(m, n), C1), Power(Subtract(n, C1), CN1)),
                          Integrate(
                              Times(Power(Times(a, Sin(Plus(e, Times(f, x)))), m),
                                  Power(Times(b, Tan(Plus(e, Times(f, x)))), Subtract(n, C2))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, e, f, m), x), GtQ(n, C1),
                      IntegersQ(Times(C2, m), Times(C2, n)), Not(And(GtQ(m, C1),
                          Not(IntegerQ(Times(C1D2, Subtract(m, C1))))))))),
          IIntegrate(2595,
              Integrate(
                  Times(
                      Sqrt(Times(a_DEFAULT,
                          $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      Power(
                          Times(b_DEFAULT, $($s("§tan"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          QQ(-3L, 2L))),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(
                              C2, Sqrt(Times(a, Sin(Plus(e, Times(f, x))))), Power(Times(b, f,
                                  Sqrt(Times(b, Tan(Plus(e, Times(f, x)))))), CN1)),
                          x),
                      Dist(
                          Times(Sqr(a), Power(b,
                              CN2)),
                          Integrate(
                              Times(
                                  Sqrt(Times(b, Tan(Plus(e, Times(f, x))))), Power(Times(a,
                                      Sin(Plus(e, Times(f, x)))), QQ(-3L, 2L))),
                              x),
                          x)),
                  FreeQ(List(a, b, e, f), x))),
          IIntegrate(2596,
              Integrate(
                  Times(
                      Power(
                          Times(a_DEFAULT,
                              $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          m_),
                      Power(
                          Times(b_DEFAULT, $($s("§tan"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          n_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(
                              Power(Times(a, Sin(Plus(e,
                                  Times(f, x)))), m),
                              Power(Times(b, Tan(Plus(e, Times(f, x)))),
                                  Plus(n, C1)),
                              Power(Times(b, f, m), CN1)),
                          x),
                      Dist(Times(Sqr(a), Plus(n, C1), Power(Times(Sqr(b), m), CN1)),
                          Integrate(
                              Times(
                                  Power(Times(a, Sin(Plus(e, Times(f, x)))), Subtract(m,
                                      C2)),
                                  Power(Times(b, Tan(Plus(e, Times(f, x)))), Plus(n, C2))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, e, f), x), LtQ(n, CN1), GtQ(m, C1),
                      IntegersQ(Times(C2, m), Times(C2, n))))),
          IIntegrate(2597,
              Integrate(
                  Times(
                      Power(
                          Times(a_DEFAULT, $($s("§sin"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          m_DEFAULT),
                      Power(
                          Times(b_DEFAULT, $($s("§tan"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          n_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Power(Times(a, Sin(Plus(e, Times(f, x)))), m),
                              Power(Times(b, Tan(Plus(e, Times(f, x)))),
                                  Plus(n, C1)),
                              Power(Times(b, f, Plus(m, n, C1)), CN1)),
                          x),
                      Dist(
                          Times(Plus(n, C1), Power(Times(Sqr(b), Plus(m, n, C1)),
                              CN1)),
                          Integrate(
                              Times(
                                  Power(Times(a, Sin(Plus(e, Times(f, x)))), m), Power(Times(b, Tan(
                                      Plus(e, Times(f, x)))), Plus(n,
                                          C2))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, e, f, m), x), LtQ(n, CN1), NeQ(Plus(m, n, C1), C0),
                      IntegersQ(Times(C2, m), Times(C2,
                          n)),
                      Not(And(EqQ(n, QQ(-3L, 2L)), EqQ(m, C1)))))),
          IIntegrate(2598,
              Integrate(
                  Times(
                      Power(
                          Times(a_DEFAULT, $($s("§sin"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          m_DEFAULT),
                      Power(
                          Times(b_DEFAULT, $($s("§tan"), Plus(e_DEFAULT,
                              Times(f_DEFAULT, x_)))),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(
                                  b, Power(Times(a, Sin(Plus(e,
                                      Times(f, x)))), m),
                                  Power(Times(b, Tan(
                                      Plus(e, Times(f, x)))), Subtract(n,
                                          C1)),
                                  Power(Times(f, m), CN1)),
                              x)),
                      Dist(Times(Sqr(a), Subtract(Plus(m, n), C1), Power(m, CN1)),
                          Integrate(
                              Times(Power(Times(a, Sin(Plus(e, Times(f, x)))), Subtract(m, C2)),
                                  Power(Times(b, Tan(Plus(e, Times(f, x)))), n)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, e, f, n), x), Or(GtQ(m, C1), And(EqQ(m, C1), EqQ(n, C1D2))),
                      IntegersQ(Times(C2, m), Times(C2, n))))),
          IIntegrate(2599,
              Integrate(
                  Times(
                      Power(
                          Times(a_DEFAULT, $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          m_),
                      Power(
                          Times(b_DEFAULT, $($s("§tan"), Plus(e_DEFAULT,
                              Times(f_DEFAULT, x_)))),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(
                              b, Power(Times(a, Sin(Plus(e,
                                  Times(f, x)))), Plus(m, C2)),
                              Power(Times(b, Tan(Plus(e, Times(f, x)))), Subtract(n, C1)), Power(
                                  Times(Sqr(a), f, Plus(m, n, C1)), CN1)),
                          x),
                      Dist(Times(Plus(m, C2), Power(Times(Sqr(a), Plus(m, n, C1)), CN1)),
                          Integrate(
                              Times(
                                  Power(Times(a, Sin(Plus(e, Times(f, x)))), Plus(m,
                                      C2)),
                                  Power(Times(b, Tan(Plus(e, Times(f, x)))), n)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, e, f, n), x), LtQ(m, CN1), NeQ(Plus(m, n,
                      C1), C0), IntegersQ(Times(C2, m),
                          Times(C2, n))))),
          IIntegrate(2600,
              Integrate(Times(
                  Power(Times(a_DEFAULT, $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))), m_),
                  Power($($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), n_)), x_Symbol),
              Condition(
                  Dist(Power(Power(a, n), CN1),
                      Integrate(Times(Power(Times(a, Sin(Plus(e, Times(f, x)))), Plus(m, n)),
                          Power(Power(Cos(Plus(e, Times(f, x))), n), CN1)), x),
                      x),
                  And(FreeQ(List(a, e, f, m), x), IntegerQ(n), Not(IntegerQ(m))))));
}
