package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctions.*;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 * 
 */
class IntRules261 {
  public static IAST RULES =
      List(
          IIntegrate(5221,
              Integrate(
                  Times(
                      Plus(a_DEFAULT, Times(ArcCsc(Times(c_DEFAULT, x_)), b_DEFAULT)), Power(
                          Times(d_DEFAULT, x_), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(Times(d, x), Plus(m, C1)),
                              Plus(a, Times(b, ArcCsc(Times(c, x)))), Power(Times(d, Plus(m, C1)),
                                  CN1)),
                          x),
                      Dist(Times(b, d, Power(Times(c, Plus(m, C1)), CN1)),
                          Integrate(
                              Times(Power(Times(d, x), Subtract(m, C1)),
                                  Power(Subtract(C1, Power(Times(Sqr(c), Sqr(x)), CN1)), CN1D2)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, m), x), NeQ(m, CN1)))),
          IIntegrate(5222,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT, Times(ArcSec(Times(c_DEFAULT, x_)), b_DEFAULT)), n_),
                      Power(x_, m_DEFAULT)),
                  x_Symbol),
              Condition(Dist(Power(Power(c, Plus(m, C1)), CN1),
                  Subst(Integrate(
                      Times(Power(Plus(a, Times(b, x)), n), Power(Sec(x), Plus(m, C1)), Tan(x)), x),
                      x, ArcSec(Times(c, x))),
                  x),
                  And(FreeQ(List(a, b, c), x), IntegerQ(n), IntegerQ(m),
                      Or(GtQ(n, C0), LtQ(m, CN1))))),
          IIntegrate(5223,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT, Times(ArcCsc(Times(c_DEFAULT, x_)), b_DEFAULT)), n_),
                      Power(x_, m_DEFAULT)),
                  x_Symbol),
              Condition(Negate(Dist(Power(Power(c, Plus(m, C1)), CN1),
                  Subst(Integrate(
                      Times(Power(Plus(a, Times(b, x)), n), Power(Csc(x), Plus(m, C1)), Cot(x)), x),
                      x, ArcCsc(Times(c, x))),
                  x)), And(
                      FreeQ(List(a, b,
                          c), x),
                      IntegerQ(n), IntegerQ(m), Or(GtQ(n, C0), LtQ(m, CN1))))),
          IIntegrate(5224,
              Integrate(
                  Times(Plus(a_DEFAULT, Times(ArcSec(Times(c_DEFAULT, x_)), b_DEFAULT)),
                      Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), CN1)),
                  x_Symbol),
              Condition(Plus(
                  Simp(
                      Times(
                          Plus(a, Times(b, ArcSec(Times(c, x)))), Log(
                              Plus(C1,
                                  Times(Subtract(e, Sqrt(Plus(Times(CN1, Sqr(c), Sqr(d)), Sqr(e)))),
                                      Exp(Times(CI, ArcSec(Times(c, x)))),
                                      Power(Times(c, d), CN1)))),
                          Power(e, CN1)),
                      x),
                  Negate(
                      Dist(
                          Times(b, Power(Times(c, e),
                              CN1)),
                          Integrate(
                              Times(
                                  Log(Plus(C1,
                                      Times(
                                          Subtract(e, Sqrt(
                                              Plus(Times(CN1, Sqr(c), Sqr(d)), Sqr(e)))),
                                          Exp(Times(CI, ArcSec(Times(c, x)))),
                                          Power(Times(c, d), CN1)))),
                                  Power(
                                      Times(Sqr(x),
                                          Sqrt(Subtract(C1, Power(Times(Sqr(c), Sqr(x)), CN1)))),
                                      CN1)),
                              x),
                          x)),
                  Negate(Dist(Times(b, Power(Times(c, e), CN1)), Integrate(Times(
                      Log(Plus(C1,
                          Times(Plus(e, Sqrt(Plus(Times(CN1, Sqr(c), Sqr(d)), Sqr(e)))),
                              Exp(Times(CI, ArcSec(Times(c, x)))), Power(Times(c, d), CN1)))),
                      Power(Times(Sqr(x), Sqrt(Subtract(C1, Power(Times(Sqr(c), Sqr(x)), CN1)))),
                          CN1)),
                      x), x)),
                  Dist(
                      Times(b, Power(Times(c, e),
                          CN1)),
                      Integrate(
                          Times(Log(Plus(C1, Exp(Times(C2, CI, ArcSec(Times(c, x)))))),
                              Power(
                                  Times(
                                      Sqr(x), Sqrt(Subtract(C1,
                                          Power(Times(Sqr(c), Sqr(x)), CN1)))),
                                  CN1)),
                          x),
                      x),
                  Simp(Times(Plus(a, Times(b, ArcSec(Times(c, x)))),
                      Log(Plus(C1,
                          Times(Plus(e, Sqrt(Plus(Times(CN1, Sqr(c), Sqr(d)), Sqr(e)))),
                              Exp(Times(CI, ArcSec(Times(c, x)))), Power(Times(c, d), CN1)))),
                      Power(e, CN1)), x),
                  Negate(Simp(
                      Times(Plus(a, Times(b, ArcSec(Times(c, x)))),
                          Log(Plus(C1, Exp(Times(C2, CI, ArcSec(Times(c, x)))))), Power(e, CN1)),
                      x))),
                  FreeQ(List(a, b, c, d, e), x))),
          IIntegrate(5225,
              Integrate(
                  Times(
                      Plus(a_DEFAULT, Times(ArcCsc(Times(c_DEFAULT, x_)), b_DEFAULT)), Power(
                          Plus(d_DEFAULT, Times(e_DEFAULT, x_)), CN1)),
                  x_Symbol),
              Condition(Plus(
                  Simp(
                      Times(
                          Plus(a, Times(b,
                              ArcCsc(Times(c, x)))),
                          Log(Subtract(
                              C1, Times(CI,
                                  Subtract(e, Sqrt(Plus(Times(CN1, Sqr(c), Sqr(d)),
                                      Sqr(e)))),
                                  Exp(Times(CI, ArcCsc(Times(c, x)))), Power(Times(c, d), CN1)))),
                          Power(e, CN1)),
                      x),
                  Dist(Times(b, Power(Times(c, e), CN1)),
                      Integrate(
                          Times(
                              Log(Subtract(C1,
                                  Times(CI,
                                      Subtract(e, Sqrt(Plus(Times(CN1, Sqr(c), Sqr(d)), Sqr(e)))),
                                      Exp(Times(CI, ArcCsc(Times(c, x)))),
                                      Power(Times(c, d), CN1)))),
                              Power(
                                  Times(Sqr(x),
                                      Sqrt(Subtract(C1, Power(Times(Sqr(c), Sqr(x)), CN1)))),
                                  CN1)),
                          x),
                      x),
                  Dist(Times(b, Power(Times(c, e), CN1)),
                      Integrate(
                          Times(
                              Log(Subtract(C1,
                                  Times(CI, Plus(e, Sqrt(Plus(Times(CN1, Sqr(c), Sqr(d)), Sqr(e)))),
                                      Exp(Times(CI, ArcCsc(Times(c, x)))), Power(Times(c, d),
                                          CN1)))),
                              Power(
                                  Times(Sqr(x),
                                      Sqrt(Subtract(C1, Power(Times(Sqr(c), Sqr(x)), CN1)))),
                                  CN1)),
                          x),
                      x),
                  Negate(
                      Dist(
                          Times(b, Power(Times(c, e),
                              CN1)),
                          Integrate(Times(
                              Log(Subtract(C1, Exp(Times(C2, CI, ArcCsc(Times(c, x)))))),
                              Power(Times(Sqr(x),
                                  Sqrt(Subtract(C1, Power(Times(Sqr(c), Sqr(x)), CN1)))),
                                  CN1)),
                              x),
                          x)),
                  Simp(
                      Times(Plus(a, Times(b, ArcCsc(Times(c, x)))),
                          Log(Subtract(C1,
                              Times(CI, Plus(e, Sqrt(Plus(Times(CN1, Sqr(c), Sqr(d)), Sqr(e)))),
                                  Exp(Times(CI, ArcCsc(Times(c, x)))), Power(Times(c, d), CN1)))),
                          Power(e, CN1)),
                      x),
                  Negate(
                      Simp(
                          Times(Plus(a, Times(b, ArcCsc(Times(c, x)))),
                              Log(Subtract(C1,
                                  Exp(Times(C2, CI, ArcCsc(Times(c, x)))))),
                              Power(e, CN1)),
                          x))),
                  FreeQ(List(a, b, c, d, e), x))),
          IIntegrate(5226,
              Integrate(
                  Times(
                      Plus(a_DEFAULT, Times(ArcSec(Times(c_DEFAULT, x_)),
                          b_DEFAULT)),
                      Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Power(Plus(d, Times(e, x)), Plus(m, C1)),
                              Plus(a, Times(b, ArcSec(Times(c, x)))), Power(Times(e, Plus(m, C1)),
                                  CN1)),
                          x),
                      Dist(
                          Times(b, Power(Times(c, e, Plus(m, C1)),
                              CN1)),
                          Integrate(
                              Times(
                                  Power(Plus(d, Times(e, x)), Plus(m,
                                      C1)),
                                  Power(
                                      Times(
                                          Sqr(x), Sqrt(Subtract(C1, Power(Times(Sqr(c), Sqr(x)),
                                              CN1)))),
                                      CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, m), x), NeQ(m, CN1)))),
          IIntegrate(5227,
              Integrate(
                  Times(
                      Plus(a_DEFAULT, Times(ArcCsc(Times(c_DEFAULT, x_)), b_DEFAULT)), Power(
                          Plus(d_DEFAULT, Times(e_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(
                              Power(Plus(d, Times(e,
                                  x)), Plus(m,
                                      C1)),
                              Plus(a, Times(b,
                                  ArcCsc(Times(c, x)))),
                              Power(Times(e, Plus(m, C1)), CN1)),
                          x),
                      Dist(
                          Times(b, Power(Times(c, e, Plus(m, C1)),
                              CN1)),
                          Integrate(
                              Times(
                                  Power(Plus(d, Times(e, x)), Plus(m,
                                      C1)),
                                  Power(
                                      Times(
                                          Sqr(x),
                                          Sqrt(Subtract(C1, Power(Times(Sqr(c), Sqr(x)), CN1)))),
                                      CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, m), x), NeQ(m, CN1)))),
          IIntegrate(5228,
              Integrate(
                  Times(
                      Plus(a_DEFAULT, Times(ArcSec(Times(c_DEFAULT, x_)),
                          b_DEFAULT)),
                      Power(Plus(d_DEFAULT, Times(e_DEFAULT, Sqr(x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(u,
                          IntHide(Power(Plus(d, Times(e, Sqr(x))), p), x))),
                      Subtract(
                          Dist(Plus(a,
                              Times(b, ArcSec(Times(c, x)))), u, x),
                          Dist(Times(b, c, x, Power(Times(Sqr(c), Sqr(x)), CN1D2)),
                              Integrate(
                                  SimplifyIntegrand(
                                      Times(u,
                                          Power(Times(x, Sqrt(Subtract(Times(Sqr(c), Sqr(x)), C1))),
                                              CN1)),
                                      x),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e), x), Or(IGtQ(p, C0), ILtQ(Plus(p, C1D2), C0))))),
          IIntegrate(5229,
              Integrate(
                  Times(
                      Plus(a_DEFAULT, Times(ArcCsc(Times(c_DEFAULT, x_)),
                          b_DEFAULT)),
                      Power(Plus(d_DEFAULT, Times(e_DEFAULT, Sqr(x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(u,
                          IntHide(Power(Plus(d, Times(e, Sqr(x))), p), x))),
                      Plus(
                          Dist(Plus(a,
                              Times(b, ArcCsc(Times(c, x)))), u, x),
                          Dist(Times(b, c, x, Power(Times(Sqr(c), Sqr(x)), CN1D2)),
                              Integrate(
                                  SimplifyIntegrand(
                                      Times(u,
                                          Power(Times(x, Sqrt(Subtract(Times(Sqr(c), Sqr(x)), C1))),
                                              CN1)),
                                      x),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e), x), Or(IGtQ(p, C0), ILtQ(Plus(p, C1D2), C0))))),
          IIntegrate(5230,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcSec(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          n_DEFAULT),
                      Power(Plus(d_DEFAULT, Times(e_DEFAULT, Sqr(x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Negate(
                      Subst(
                          Integrate(
                              Times(Power(Plus(e, Times(d, Sqr(x))), p),
                                  Power(Plus(a,
                                      Times(b, ArcCos(Times(x, Power(c, CN1))))), n),
                                  Power(Power(x, Times(C2, Plus(p, C1))), CN1)),
                              x),
                          x, Power(x, CN1))),
                  And(FreeQ(List(a, b, c, d, e, n), x), IGtQ(n, C0), IntegerQ(p)))),
          IIntegrate(5231,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCsc(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          n_DEFAULT),
                      Power(Plus(d_DEFAULT, Times(e_DEFAULT, Sqr(x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Negate(
                      Subst(
                          Integrate(
                              Times(Power(Plus(e, Times(d, Sqr(x))), p),
                                  Power(Plus(a,
                                      Times(b, ArcSin(Times(x, Power(c, CN1))))), n),
                                  Power(Power(x, Times(C2, Plus(p, C1))), CN1)),
                              x),
                          x, Power(x, CN1))),
                  And(FreeQ(List(a, b, c, d, e, n), x), IGtQ(n, C0), IntegerQ(p)))),
          IIntegrate(5232,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcSec(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          n_DEFAULT),
                      Power(Plus(d_DEFAULT, Times(e_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Negate(
                      Dist(
                          Times(Sqrt(Sqr(
                              x)), Power(x,
                                  CN1)),
                          Subst(
                              Integrate(
                                  Times(Power(Plus(e, Times(d, Sqr(x))), p),
                                      Power(Plus(a, Times(b, ArcCos(Times(x, Power(c, CN1))))), n),
                                      Power(Power(x, Times(C2, Plus(p, C1))), CN1)),
                                  x),
                              x, Power(x, CN1)),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, n), x), IGtQ(n, C0),
                      EqQ(Plus(Times(Sqr(c), d), e), C0), IntegerQ(Plus(p,
                          C1D2)),
                      GtQ(e, C0), LtQ(d, C0)))),
          IIntegrate(5233,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCsc(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          n_DEFAULT),
                      Power(Plus(d_DEFAULT, Times(e_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Negate(
                      Dist(
                          Times(Sqrt(Sqr(
                              x)), Power(x,
                                  CN1)),
                          Subst(
                              Integrate(
                                  Times(Power(Plus(e, Times(d, Sqr(x))), p),
                                      Power(Plus(a, Times(b, ArcSin(Times(x, Power(c, CN1))))), n),
                                      Power(Power(x, Times(C2, Plus(p, C1))), CN1)),
                                  x),
                              x, Power(x, CN1)),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, n), x), IGtQ(n, C0),
                      EqQ(Plus(Times(Sqr(c),
                          d), e), C0),
                      IntegerQ(Plus(p, C1D2)), GtQ(e, C0), LtQ(d, C0)))),
          IIntegrate(5234,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcSec(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          n_DEFAULT),
                      Power(Plus(d_DEFAULT, Times(e_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Negate(
                      Dist(
                          Times(
                              Sqrt(Plus(d, Times(e, Sqr(x)))), Power(
                                  Times(x, Sqrt(Plus(e, Times(d, Power(x, CN2))))), CN1)),
                          Subst(
                              Integrate(
                                  Times(Power(Plus(e, Times(d, Sqr(x))), p),
                                      Power(Plus(a,
                                          Times(b, ArcCos(Times(x, Power(c, CN1))))), n),
                                      Power(Power(x, Times(C2, Plus(p, C1))), CN1)),
                                  x),
                              x, Power(x, CN1)),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, n), x), IGtQ(n, C0),
                      EqQ(Plus(Times(Sqr(c),
                          d), e), C0),
                      IntegerQ(Plus(p, C1D2)), Not(And(GtQ(e, C0), LtQ(d, C0)))))),
          IIntegrate(5235,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcCsc(Times(c_DEFAULT, x_)),
                              b_DEFAULT)),
                          n_DEFAULT),
                      Power(Plus(d_DEFAULT, Times(e_DEFAULT, Sqr(x_))), p_)),
                  x_Symbol),
              Condition(
                  Negate(
                      Dist(
                          Times(
                              Sqrt(Plus(d, Times(e,
                                  Sqr(x)))),
                              Power(Times(x, Sqrt(Plus(e, Times(d, Power(x, CN2))))), CN1)),
                          Subst(
                              Integrate(
                                  Times(Power(Plus(e, Times(d, Sqr(x))), p),
                                      Power(Plus(a,
                                          Times(b, ArcSin(Times(x, Power(c, CN1))))), n),
                                      Power(Power(x, Times(C2, Plus(p, C1))), CN1)),
                                  x),
                              x, Power(x, CN1)),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, n), x), IGtQ(n, C0),
                      EqQ(Plus(Times(Sqr(c), d), e), C0), IntegerQ(Plus(p,
                          C1D2)),
                      Not(And(GtQ(e, C0), LtQ(d, C0)))))),
          IIntegrate(5236,
              Integrate(
                  Times(
                      Plus(a_DEFAULT, Times(ArcSec(Times(c_DEFAULT, x_)),
                          b_DEFAULT)),
                      x_, Power(Plus(d_DEFAULT, Times(e_DEFAULT, Sqr(x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Power(Plus(d, Times(e, Sqr(x))), Plus(p, C1)),
                              Plus(a, Times(b, ArcSec(Times(c, x)))), Power(
                                  Times(C2, e, Plus(p, C1)), CN1)),
                          x),
                      Dist(
                          Times(
                              b, c, x, Power(Times(C2, e, Plus(p, C1), Sqrt(Times(Sqr(c), Sqr(x)))),
                                  CN1)),
                          Integrate(
                              Times(
                                  Power(Plus(d, Times(e,
                                      Sqr(x))), Plus(p,
                                          C1)),
                                  Power(Times(x, Sqrt(Subtract(Times(Sqr(c), Sqr(x)), C1))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, p), x), NeQ(p, CN1)))),
          IIntegrate(5237,
              Integrate(
                  Times(
                      Plus(a_DEFAULT, Times(ArcCsc(Times(c_DEFAULT, x_)),
                          b_DEFAULT)),
                      x_, Power(Plus(d_DEFAULT, Times(e_DEFAULT, Sqr(x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(Plus(d, Times(e, Sqr(x))), Plus(p, C1)),
                              Plus(a, Times(b, ArcCsc(Times(c, x)))), Power(
                                  Times(C2, e, Plus(p, C1)), CN1)),
                          x),
                      Dist(Times(
                          b, c, x, Power(Times(C2, e, Plus(p, C1), Sqrt(Times(Sqr(c), Sqr(x)))),
                              CN1)),
                          Integrate(
                              Times(Power(Plus(d, Times(e, Sqr(x))), Plus(p, C1)), Power(Times(x,
                                  Sqrt(Subtract(Times(Sqr(c), Sqr(x)), C1))),
                                  CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, p), x), NeQ(p, CN1)))),
          IIntegrate(5238,
              Integrate(
                  Times(Plus(a_DEFAULT, Times(ArcSec(Times(c_DEFAULT, x_)), b_DEFAULT)),
                      Power(Times(f_DEFAULT, x_), m_DEFAULT),
                      Power(Plus(d_DEFAULT, Times(e_DEFAULT, Sqr(x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(u,
                              IntHide(
                                  Times(Power(Times(f, x), m), Power(Plus(d, Times(e, Sqr(x))), p)),
                                  x))),
                      Subtract(
                          Dist(Plus(a,
                              Times(b, ArcSec(Times(c, x)))), u, x),
                          Dist(Times(b, c, x, Power(Times(Sqr(c), Sqr(x)), CN1D2)),
                              Integrate(SimplifyIntegrand(Times(u,
                                  Power(Times(x, Sqrt(Subtract(Times(Sqr(c), Sqr(x)), C1))), CN1)),
                                  x), x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e, f, m, p), x), Or(
                      And(IGtQ(p, C0),
                          Not(And(ILtQ(Times(C1D2, Subtract(m, C1)), C0),
                              GtQ(Plus(m, Times(C2, p), C3), C0)))),
                      And(IGtQ(Times(C1D2, Plus(m, C1)), C0),
                          Not(And(ILtQ(p, C0), GtQ(Plus(m, Times(C2, p), C3), C0)))),
                      And(ILtQ(Times(C1D2, Plus(m, Times(C2, p), C1)), C0),
                          Not(ILtQ(Times(C1D2, Subtract(m, C1)), C0))))))),
          IIntegrate(5239,
              Integrate(
                  Times(Plus(a_DEFAULT, Times(ArcCsc(Times(c_DEFAULT, x_)), b_DEFAULT)),
                      Power(Times(f_DEFAULT, x_), m_DEFAULT), Power(Plus(d_DEFAULT,
                          Times(e_DEFAULT, Sqr(x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(u,
                              IntHide(
                                  Times(Power(Times(f, x), m), Power(Plus(d, Times(e, Sqr(x))), p)),
                                  x))),
                      Plus(
                          Dist(Plus(a,
                              Times(b, ArcCsc(Times(c, x)))), u, x),
                          Dist(
                              Times(b, c, x, Power(Times(Sqr(c), Sqr(x)), CN1D2)),
                              Integrate(
                                  SimplifyIntegrand(Times(u,
                                      Power(Times(x, Sqrt(Subtract(Times(Sqr(c), Sqr(x)), C1))),
                                          CN1)),
                                      x),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e, f, m,
                      p), x), Or(
                          And(IGtQ(p, C0),
                              Not(And(ILtQ(Times(C1D2, Subtract(m, C1)), C0),
                                  GtQ(Plus(m, Times(C2, p), C3), C0)))),
                          And(IGtQ(Times(C1D2,
                              Plus(m, C1)), C0), Not(
                                  And(ILtQ(p, C0), GtQ(Plus(m, Times(C2, p), C3), C0)))),
                          And(ILtQ(Times(C1D2, Plus(m, Times(C2, p), C1)), C0),
                              Not(ILtQ(Times(C1D2, Subtract(m, C1)), C0))))))),
          IIntegrate(5240,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(ArcSec(Times(c_DEFAULT, x_)), b_DEFAULT)),
                          n_DEFAULT),
                      Power(x_, m_DEFAULT),
                      Power(Plus(d_DEFAULT, Times(e_DEFAULT, Sqr(x_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Negate(Subst(
                      Integrate(Times(Power(Plus(e, Times(d, Sqr(x))), p),
                          Power(Plus(a, Times(b, ArcCos(Times(x, Power(c, CN1))))), n),
                          Power(Power(x, Plus(m, Times(C2, Plus(p, C1)))), CN1)), x),
                      x, Power(x, CN1))),
                  And(FreeQ(List(a, b, c, d, e, n), x), IGtQ(n, C0), IntegerQ(m), IntegerQ(p)))));
}
