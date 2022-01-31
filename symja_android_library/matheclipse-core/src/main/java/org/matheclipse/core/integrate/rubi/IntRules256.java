package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.ArcCot;
import static org.matheclipse.core.expression.F.ArcCoth;
import static org.matheclipse.core.expression.F.ArcTan;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.CI;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN2;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Exp;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.Unequal;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_;
import static org.matheclipse.core.expression.F.p_DEFAULT;
import static org.matheclipse.core.expression.F.u_DEFAULT;
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
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ILtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntegersQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RationalQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Subst;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules256 {
  public static IAST RULES =
      List(
          IIntegrate(5121,
              Integrate(
                  Times(
                      Exp(Times(ArcCot(Times(a_DEFAULT, x_)), n_DEFAULT)), u_DEFAULT, Power(
                          Plus(c_, Times(d_DEFAULT, Sqr(x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(d, p),
                      Integrate(
                          Times(u, Power(x, Times(C2, p)),
                              Power(Plus(C1, Power(Times(Sqr(a), Sqr(x)), CN1)),
                                  p),
                              Exp(Times(n, ArcCot(Times(a, x))))),
                          x),
                      x),
                  And(FreeQ(List(a, c, d, n), x), EqQ(d, Times(Sqr(
                      a), c)), Not(IntegerQ(
                          Times(C1D2, CI, n))),
                      IntegerQ(p)))),
          IIntegrate(5122,
              Integrate(
                  Times(
                      Exp(Times(ArcCot(Times(a_DEFAULT, x_)), n_DEFAULT)), u_DEFAULT, Power(
                          Plus(c_, Times(d_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Plus(c, Times(d, Sqr(x))), p),
                          Power(
                              Times(
                                  Power(x, Times(C2, p)),
                                  Power(Plus(C1, Power(Times(Sqr(a), Sqr(x)), CN1)), p)),
                              CN1)),
                      Integrate(Times(u, Power(x, Times(C2, p)),
                          Power(Plus(C1, Power(Times(Sqr(a), Sqr(x)), CN1)), p),
                          Exp(Times(n, ArcCot(Times(a, x))))), x),
                      x),
                  And(FreeQ(List(a, c, d, n, p), x), EqQ(d, Times(Sqr(a),
                      c)), Not(
                          IntegerQ(Times(C1D2, CI, n))),
                      Not(IntegerQ(p))))),
          IIntegrate(5123,
              Integrate(
                  Times(
                      Exp(Times(ArcCot(Times(a_DEFAULT, x_)),
                          n_DEFAULT)),
                      u_DEFAULT, Power(Plus(c_, Times(d_DEFAULT, Power(x_, CN2))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Times(Power(c, p), Power(Power(Times(CI, a), Times(C2, p)), CN1)),
                      Integrate(
                          Times(u,
                              Power(Plus(CN1, Times(CI, a, x)), Subtract(p, Times(C1D2, CI, n))),
                              Power(Plus(C1, Times(CI, a, x)), Plus(p, Times(C1D2, CI, n))), Power(
                                  Power(x, Times(C2, p)), CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, c, d, n, p), x), EqQ(c, Times(Sqr(a), d)),
                      Not(IntegerQ(Times(C1D2, CI, n))), Or(IntegerQ(p), GtQ(c,
                          C0)),
                      IntegersQ(Times(C2, p), Plus(p, Times(C1D2, CI, n)))))),
          IIntegrate(5124,
              Integrate(
                  Times(Exp(Times(ArcCot(Times(a_DEFAULT, x_)), n_DEFAULT)), Power(
                      Plus(c_, Times(d_DEFAULT, Power(x_, CN2))), p_DEFAULT)),
                  x_Symbol),
              Condition(Negate(Dist(Power(c, p),
                  Subst(Integrate(Times(
                      Power(Subtract(C1, Times(CI, x, Power(a, CN1))), Plus(p, Times(C1D2, CI, n))),
                      Power(Plus(C1, Times(CI, x, Power(a, CN1))), Subtract(p, Times(C1D2, CI, n))),
                      Power(x, CN2)), x), x, Power(x, CN1)),
                  x)), And(
                      FreeQ(List(a, c, d, n, p), x), EqQ(c, Times(Sqr(a), d)),
                      Not(IntegerQ(Times(C1D2, CI, n))), Or(IntegerQ(p), GtQ(c,
                          C0)),
                      Not(And(IntegerQ(Times(C2, p)), IntegerQ(Plus(p, Times(C1D2, CI, n)))))))),
          IIntegrate(5125,
              Integrate(Times(Exp(Times(ArcCot(Times(a_DEFAULT, x_)), n_DEFAULT)),
                  Power(Plus(c_, Times(d_DEFAULT, Power(x_, CN2))), p_DEFAULT), Power(x_,
                      m_DEFAULT)),
                  x_Symbol),
              Condition(Negate(Dist(Power(c, p),
                  Subst(Integrate(Times(
                      Power(Subtract(C1, Times(CI, x, Power(a, CN1))), Plus(p, Times(C1D2, CI, n))),
                      Power(Plus(C1, Times(CI, x, Power(a, CN1))), Subtract(p, Times(C1D2, CI, n))),
                      Power(Power(x, Plus(m, C2)), CN1)), x), x, Power(x, CN1)),
                  x)), And(FreeQ(List(a, c, d, n, p), x), EqQ(c, Times(Sqr(a), d)),
                      Not(IntegerQ(Times(C1D2, CI, n))), Or(IntegerQ(p), GtQ(c, C0)),
                      Not(And(IntegerQ(Times(C2, p)),
                          IntegerQ(Plus(p, Times(C1D2, CI, n))))),
                      IntegerQ(m)))),
          IIntegrate(5126,
              Integrate(
                  Times(
                      Exp(Times(ArcCot(
                          Times(a_DEFAULT, x_)), n_DEFAULT)),
                      Power(Plus(c_, Times(d_DEFAULT, Power(x_, CN2))), p_DEFAULT), Power(x_, m_)),
                  x_Symbol),
              Condition(Negate(Dist(Times(Power(c, p), Power(x, m), Power(Power(x, CN1), m)),
                  Subst(Integrate(Times(
                      Power(Subtract(C1, Times(CI, x, Power(a, CN1))), Plus(p, Times(C1D2, CI, n))),
                      Power(Plus(C1, Times(CI, x, Power(a, CN1))), Subtract(p, Times(C1D2, CI, n))),
                      Power(Power(x, Plus(m, C2)), CN1)), x), x, Power(x, CN1)),
                  x)), And(
                      FreeQ(List(a, c, d, m, n, p), x), EqQ(c, Times(Sqr(a), d)),
                      Not(IntegerQ(Times(C1D2, CI,
                          n))),
                      Or(IntegerQ(p), GtQ(c, C0)),
                      Not(And(IntegerQ(Times(C2,
                          p)), IntegerQ(
                              Plus(p, Times(C1D2, CI, n))))),
                      Not(IntegerQ(m))))),
          IIntegrate(5127,
              Integrate(
                  Times(
                      Exp(Times(ArcCot(Times(a_DEFAULT, x_)), n_DEFAULT)), u_DEFAULT, Power(
                          Plus(c_, Times(d_DEFAULT, Power(x_, CN2))), p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(Plus(c, Times(d,
                              Power(x, CN2))), p),
                          Power(Power(Plus(C1, Power(Times(Sqr(a), Sqr(x)), CN1)), p), CN1)),
                      Integrate(
                          Times(
                              u, Power(Plus(C1, Power(Times(Sqr(a), Sqr(x)), CN1)),
                                  p),
                              Exp(Times(n, ArcCot(Times(a, x))))),
                          x),
                      x),
                  And(FreeQ(List(a, c, d, n, p), x), EqQ(c, Times(Sqr(
                      a), d)), Not(IntegerQ(
                          Times(C1D2, CI, n))),
                      Not(Or(IntegerQ(p), GtQ(c, C0)))))),
          IIntegrate(5128,
              Integrate(
                  Times(
                      Exp(Times(ArcCot(Times(c_DEFAULT, Plus(a_, Times(b_DEFAULT, x_)))),
                          n_)),
                      u_DEFAULT),
                  x_Symbol),
              Condition(
                  Dist(
                      Power(CN1, Times(C1D2, CI,
                          n)),
                      Integrate(
                          Times(u, Power(Exp(Times(n, ArcTan(Times(c, Plus(a, Times(b, x)))))),
                              CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c), x), IntegerQ(Times(C1D2, CI, n))))),
          IIntegrate(5129,
              Integrate(
                  Exp(Times(ArcCot(Times(c_DEFAULT, Plus(a_, Times(b_DEFAULT, x_)))),
                      n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Times(CI, c, Plus(a, Times(b, x))), Times(C1D2, CI, n)),
                          Power(Plus(C1, Power(Times(CI, c, Plus(a, Times(b, x))), CN1)),
                              Times(C1D2, CI, n)),
                          Power(
                              Power(Plus(C1, Times(CI, a, c), Times(CI, b, c, x)),
                                  Times(C1D2, CI, n)),
                              CN1)),
                      Integrate(
                          Times(
                              Power(
                                  Plus(C1, Times(CI, a, c), Times(CI, b, c, x)),
                                  Times(C1D2, CI, n)),
                              Power(
                                  Power(
                                      Plus(CN1, Times(CI, a, c), Times(CI, b, c, x)), Times(C1D2,
                                          CI, n)),
                                  CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, n), x), Not(IntegerQ(Times(C1D2, CI, n)))))),
          IIntegrate(5130,
              Integrate(
                  Times(
                      Exp(Times(ArcCoth(Times(c_DEFAULT, Plus(a_, Times(b_DEFAULT, x_)))), n_)),
                      Power(x_, m_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(C4,
                          Power(
                              Times(Power(CI, m), n, Power(b, Plus(m,
                                  C1)), Power(c,
                                      Plus(m, C1))),
                              CN1)),
                      Subst(
                          Integrate(
                              Times(Power(x, Times(C2, Power(Times(CI, n), CN1))),
                                  Power(
                                      Plus(C1, Times(CI, a, c),
                                          Times(Subtract(C1, Times(CI, a, c)), Power(x,
                                              Times(C2, Power(Times(CI, n), CN1))))),
                                      m),
                                  Power(
                                      Power(Plus(CN1, Power(x,
                                          Times(C2, Power(Times(CI, n), CN1)))), Plus(m,
                                              C2)),
                                      CN1)),
                              x),
                          x,
                          Times(
                              Power(
                                  Plus(C1, Power(Times(CI, c, Plus(a, Times(b, x))),
                                      CN1)),
                                  Times(C1D2, CI, n)),
                              Power(
                                  Power(
                                      Subtract(C1, Power(Times(CI, c, Plus(a, Times(b, x))),
                                          CN1)),
                                      Times(C1D2, CI, n)),
                                  CN1))),
                      x),
                  And(FreeQ(List(a, b, c), x), ILtQ(m, C0), LtQ(CN1, Times(CI, n), C1)))),
          IIntegrate(5131,
              Integrate(Times(
                  Exp(Times(ArcCoth(Times(c_DEFAULT, Plus(a_, Times(b_DEFAULT, x_)))), n_DEFAULT)),
                  Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), m_DEFAULT)), x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Times(CI, c, Plus(a, Times(b, x))), Times(C1D2, CI, n)),
                          Power(Plus(C1, Power(Times(CI, c, Plus(a, Times(b, x))), CN1)),
                              Times(C1D2, CI, n)),
                          Power(
                              Power(Plus(C1, Times(CI, a, c), Times(CI, b, c, x)),
                                  Times(C1D2, CI, n)),
                              CN1)),
                      Integrate(
                          Times(Power(Plus(d, Times(e, x)), m),
                              Power(Plus(C1, Times(CI, a, c), Times(CI, b, c, x)), Times(C1D2, CI,
                                  n)),
                              Power(
                                  Power(
                                      Plus(CN1, Times(CI, a, c), Times(CI, b, c, x)), Times(C1D2,
                                          CI, n)),
                                  CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, m, n), x), Not(IntegerQ(Times(C1D2, CI, n)))))),
          IIntegrate(5132,
              Integrate(
                  Times(Exp(Times(ArcCot(Plus(a_, Times(b_DEFAULT, x_))), n_DEFAULT)), u_DEFAULT,
                      Power(Plus(c_, Times(d_DEFAULT, x_), Times(e_DEFAULT, Sqr(x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(Times(c, Power(Plus(C1, Sqr(a)), CN1)), p), Power(
                              Times(Plus(Times(CI, a), Times(CI, b, x)),
                                  Power(Plus(C1, Times(CI, a), Times(CI, b, x)), CN1)),
                              Times(C1D2, CI, n)),
                          Power(Times(Plus(C1, Times(CI, a), Times(CI, b, x)),
                              Power(Plus(Times(CI, a), Times(CI, b, x)), CN1)), Times(C1D2, CI, n)),
                          Power(Subtract(Subtract(C1, Times(CI, a)), Times(CI, b, x)),
                              Times(C1D2, CI, n)),
                          Power(Power(Plus(CN1, Times(CI, a), Times(CI, b, x)), Times(C1D2, CI, n)),
                              CN1)),
                      Integrate(
                          Times(u,
                              Power(Subtract(Subtract(C1, Times(CI, a)), Times(CI, b, x)),
                                  Subtract(p, Times(C1D2, CI, n))),
                              Power(Plus(C1, Times(CI, a), Times(CI, b, x)),
                                  Plus(p, Times(C1D2, CI, n)))),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, n, p), x), Not(IntegerQ(Times(C1D2, CI, n))),
                      EqQ(Subtract(Times(b, d), Times(C2, a, e)), C0),
                      EqQ(Subtract(Times(Sqr(b), c), Times(e, Plus(C1, Sqr(a)))), C0), Or(IntegerQ(
                          p), GtQ(Times(c, Power(Plus(C1, Sqr(a)), CN1)), C0))))),
          IIntegrate(5133,
              Integrate(
                  Times(
                      Exp(Times(ArcCot(Plus(a_, Times(b_DEFAULT, x_))),
                          n_DEFAULT)),
                      u_DEFAULT,
                      Power(Plus(c_, Times(d_DEFAULT, x_), Times(e_DEFAULT, Sqr(x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Times(Power(Plus(c, Times(d, x), Times(e, Sqr(x))), p),
                      Power(Power(Plus(C1, Sqr(a), Times(C2, a, b, x), Times(Sqr(b), Sqr(x))), p),
                          CN1)),
                      Integrate(
                          Times(u,
                              Power(Plus(C1, Sqr(a), Times(C2, a, b, x), Times(Sqr(b), Sqr(x))), p),
                              Exp(Times(n, ArcCot(Times(a, x))))),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, n, p), x), Not(IntegerQ(Times(C1D2, CI, n))),
                      EqQ(Subtract(Times(b, d), Times(C2, a,
                          e)), C0),
                      EqQ(Subtract(Times(Sqr(
                          b), c), Times(e,
                              Plus(C1, Sqr(a)))),
                          C0),
                      Not(Or(IntegerQ(p), GtQ(Times(c, Power(Plus(C1, Sqr(a)), CN1)), C0)))))),
          IIntegrate(5134,
              Integrate(
                  Times(
                      Exp(Times(
                          ArcCot(Times(c_DEFAULT, Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_)),
                              CN1))),
                          n_DEFAULT)),
                      u_DEFAULT),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(
                          u, Exp(
                              Times(n,
                                  ArcTan(Plus(Times(a, Power(c, CN1)),
                                      Times(b, x, Power(c, CN1))))))),
                      x),
                  FreeQ(List(a, b, c, n), x))),
          IIntegrate(
              5135, Integrate(ArcTan(
                  Plus(a_, Times(b_DEFAULT, Power(x_, n_)))), x_Symbol),
              Condition(
                  Subtract(
                      Simp(Times(x,
                          ArcTan(Plus(a, Times(b, Power(x, n))))), x),
                      Dist(Times(b, n),
                          Integrate(
                              Times(Power(x, n),
                                  Power(
                                      Plus(C1, Sqr(a), Times(C2, a, b, Power(x, n)),
                                          Times(Sqr(b), Power(x, Times(C2, n)))),
                                      CN1)),
                              x),
                          x)),
                  FreeQ(List(a, b, n), x))),
          IIntegrate(
              5136, Integrate(ArcCot(
                  Plus(a_, Times(b_DEFAULT, Power(x_, n_)))), x_Symbol),
              Condition(
                  Plus(
                      Simp(Times(x,
                          ArcCot(Plus(a, Times(b, Power(x, n))))), x),
                      Dist(Times(b, n),
                          Integrate(
                              Times(Power(x, n),
                                  Power(
                                      Plus(C1, Sqr(a), Times(C2, a, b, Power(x, n)),
                                          Times(Sqr(b), Power(x, Times(C2, n)))),
                                      CN1)),
                              x),
                          x)),
                  FreeQ(List(a, b, n), x))),
          IIntegrate(5137,
              Integrate(
                  Times(ArcTan(Plus(a_DEFAULT,
                      Times(b_DEFAULT, Power(x_, n_)))), Power(x_,
                          CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Times(C1D2, CI),
                          Integrate(Times(
                              Log(Subtract(Subtract(C1, Times(CI, a)), Times(CI, b, Power(x, n)))),
                              Power(x, CN1)), x),
                          x),
                      Dist(Times(C1D2, CI),
                          Integrate(
                              Times(Log(Plus(C1, Times(CI, a), Times(CI, b, Power(x, n)))),
                                  Power(x, CN1)),
                              x),
                          x)),
                  FreeQ(List(a, b, n), x))),
          IIntegrate(5138,
              Integrate(
                  Times(ArcCot(Plus(a_DEFAULT,
                      Times(b_DEFAULT, Power(x_, n_)))), Power(x_,
                          CN1)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Times(C1D2, CI),
                          Integrate(
                              Times(
                                  Log(Subtract(C1, Times(CI,
                                      Power(Plus(a, Times(b, Power(x, n))), CN1)))),
                                  Power(x, CN1)),
                              x),
                          x),
                      Dist(Times(C1D2, CI),
                          Integrate(
                              Times(
                                  Log(Plus(C1, Times(CI,
                                      Power(Plus(a, Times(b, Power(x, n))), CN1)))),
                                  Power(x, CN1)),
                              x),
                          x)),
                  FreeQ(List(a, b, n), x))),
          IIntegrate(5139,
              Integrate(
                  Times(ArcTan(Plus(a_,
                      Times(b_DEFAULT, Power(x_, n_)))), Power(x_,
                          m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(
                              Power(x, Plus(m, C1)), ArcTan(Plus(a, Times(b, Power(x, n)))), Power(
                                  Plus(m, C1), CN1)),
                          x),
                      Dist(
                          Times(b, n, Power(Plus(m, C1),
                              CN1)),
                          Integrate(
                              Times(
                                  Power(x, Plus(m, n)), Power(Plus(C1, Sqr(a),
                                      Times(C2, a, b, Power(x, n)), Times(Sqr(b),
                                          Power(x, Times(C2, n)))),
                                      CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b), x), RationalQ(m, n), Unequal(Plus(m,
                      C1), C0), Unequal(Plus(m, C1),
                          n)))),
          IIntegrate(5140,
              Integrate(
                  Times(ArcCot(Plus(a_, Times(b_DEFAULT, Power(x_, n_)))), Power(x_, m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(Times(Power(x, Plus(m, C1)), ArcCot(Plus(a, Times(b, Power(x, n)))),
                          Power(Plus(m, C1), CN1)), x),
                      Dist(Times(b, n, Power(Plus(m, C1), CN1)),
                          Integrate(Times(Power(x, Plus(m, n)),
                              Power(Plus(C1, Sqr(a), Times(C2, a, b, Power(x, n)),
                                  Times(Sqr(b), Power(x, Times(C2, n)))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b), x), RationalQ(m, n), Unequal(Plus(m, C1), C0),
                      Unequal(Plus(m, C1), n)))));
}
