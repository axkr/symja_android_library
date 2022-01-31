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
class IntRules216 {
  public static IAST RULES =
      List(
          IIntegrate(4321,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cos"), Plus(a_DEFAULT, Times(b_DEFAULT, x_))),
                              f_DEFAULT),
                          n_),
                      Power(
                          Times(e_DEFAULT,
                              $($s("§sin"), Plus(a_DEFAULT, Times(b_DEFAULT, x_)))),
                          m_),
                      Power(Times(g_DEFAULT, $($s("§sin"), Plus(c_DEFAULT, Times(d_DEFAULT, x_)))),
                          p_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(
                                  e, Power(Times(e, Sin(Plus(a,
                                      Times(b, x)))), Subtract(m, C1)),
                                  Power(Times(f, Cos(Plus(a, Times(b, x)))), Plus(n, C1)),
                                  Power(Times(g,
                                      Sin(Plus(c, Times(d, x)))), p),
                                  Power(Times(b, f, Plus(n, p, C1)), CN1)),
                              x)),
                      Dist(
                          Times(Sqr(e), Subtract(Plus(m, p),
                              C1), Power(Times(Sqr(f), Plus(n, p, C1)), CN1)),
                          Integrate(
                              Times(Power(Times(e, Sin(Plus(a, Times(b, x)))), Subtract(m, C2)),
                                  Power(Times(f, Cos(Plus(a, Times(b, x)))), Plus(n,
                                      C2)),
                                  Power(Times(g, Sin(Plus(c, Times(d, x)))), p)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g, p), x),
                      EqQ(Subtract(Times(b, c), Times(a, d)), C0), EqQ(Times(d, Power(b, CN1)), C2),
                      Not(IntegerQ(p)), GtQ(m, C1), LtQ(n, CN1), NeQ(Plus(n, p,
                          C1), C0),
                      IntegersQ(Times(C2, m), Times(C2, n), Times(C2, p))))),
          IIntegrate(4322,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cos"), Plus(a_DEFAULT, Times(b_DEFAULT, x_))),
                              e_DEFAULT),
                          m_),
                      Power(
                          Times(f_DEFAULT, $($s("§sin"),
                              Plus(a_DEFAULT, Times(b_DEFAULT, x_)))),
                          n_DEFAULT),
                      Power(
                          Times(g_DEFAULT, $($s("§sin"),
                              Plus(c_DEFAULT, Times(d_DEFAULT, x_)))),
                          p_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(e, Power(Times(e, Cos(Plus(a, Times(b, x)))), Subtract(m, C1)),
                              Power(Times(f, Sin(
                                  Plus(a, Times(b, x)))), Plus(n,
                                      C1)),
                              Power(Times(g, Sin(Plus(c, Times(d, x)))),
                                  p),
                              Power(Times(b, f, Plus(m, n, Times(C2, p))), CN1)),
                          x),
                      Dist(
                          Times(
                              Sqr(e), Subtract(Plus(m,
                                  p), C1),
                              Power(Plus(m, n, Times(C2, p)), CN1)),
                          Integrate(
                              Times(Power(Times(e, Cos(Plus(a, Times(b, x)))), Subtract(m, C2)),
                                  Power(Times(f,
                                      Sin(Plus(a, Times(b, x)))), n),
                                  Power(Times(g, Sin(Plus(c, Times(d, x)))), p)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g, n, p), x),
                      EqQ(Subtract(Times(b, c), Times(a, d)), C0), EqQ(Times(d, Power(b, CN1)), C2),
                      Not(IntegerQ(p)), GtQ(m, C1), NeQ(Plus(m, n, Times(C2,
                          p)), C0),
                      IntegersQ(Times(C2, m), Times(C2, n), Times(C2, p))))),
          IIntegrate(4323,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cos"), Plus(a_DEFAULT, Times(b_DEFAULT, x_))),
                              f_DEFAULT),
                          n_DEFAULT),
                      Power(
                          Times(e_DEFAULT,
                              $($s("§sin"), Plus(a_DEFAULT, Times(b_DEFAULT, x_)))),
                          m_),
                      Power(
                          Times(g_DEFAULT, $($s("§sin"),
                              Plus(c_DEFAULT, Times(d_DEFAULT, x_)))),
                          p_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(e, Power(Times(e, Sin(Plus(a, Times(b, x)))), Subtract(m, C1)),
                                  Power(Times(f, Cos(Plus(a, Times(b, x)))), Plus(n, C1)),
                                  Power(Times(g,
                                      Sin(Plus(c, Times(d, x)))), p),
                                  Power(Times(b, f, Plus(m, n, Times(C2, p))), CN1)),
                              x)),
                      Dist(
                          Times(Sqr(e), Subtract(Plus(m, p), C1),
                              Power(Plus(m, n, Times(C2, p)), CN1)),
                          Integrate(
                              Times(Power(Times(e, Sin(Plus(a, Times(b, x)))), Subtract(m, C2)),
                                  Power(Times(f, Cos(Plus(a, Times(b, x)))), n),
                                  Power(Times(g, Sin(Plus(c, Times(d, x)))), p)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g, n, p), x),
                      EqQ(Subtract(Times(b, c), Times(a, d)), C0), EqQ(Times(d, Power(b, CN1)), C2),
                      Not(IntegerQ(p)), GtQ(m, C1), NeQ(Plus(m, n,
                          Times(C2, p)), C0),
                      IntegersQ(Times(C2, m), Times(C2, n), Times(C2, p))))),
          IIntegrate(4324,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cos"), Plus(a_DEFAULT, Times(b_DEFAULT, x_))),
                              e_DEFAULT),
                          m_),
                      Power(
                          Times(f_DEFAULT, $($s("§sin"),
                              Plus(a_DEFAULT, Times(b_DEFAULT, x_)))),
                          n_DEFAULT),
                      Power(
                          Times(g_DEFAULT, $($s("§sin"),
                              Plus(c_DEFAULT, Times(d_DEFAULT, x_)))),
                          p_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(f, Power(Times(e, Cos(Plus(a, Times(b, x)))), Plus(m, C1)),
                                  Power(Times(f, Sin(Plus(a,
                                      Times(b, x)))), Subtract(n, C1)),
                                  Power(Times(g, Sin(Plus(c, Times(d, x)))), p),
                                  Power(Times(b, e, Plus(m, n, Times(C2, p))), CN1)),
                              x)),
                      Dist(
                          Times(
                              C2, f, g, Subtract(Plus(n, p),
                                  C1),
                              Power(Times(e, Plus(m, n, Times(C2, p))), CN1)),
                          Integrate(
                              Times(Power(Times(e, Cos(Plus(a, Times(b, x)))), Plus(m, C1)),
                                  Power(Times(f, Sin(Plus(a, Times(b, x)))), Subtract(n, C1)),
                                  Power(Times(g, Sin(Plus(c, Times(d, x)))), Subtract(p, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g), x),
                      EqQ(Subtract(Times(b, c), Times(a, d)), C0), EqQ(Times(d, Power(b, CN1)), C2),
                      Not(IntegerQ(p)), LtQ(m, CN1), GtQ(n, C0), GtQ(p, C0),
                      NeQ(Plus(m, n, Times(C2, p)), C0), IntegersQ(Times(C2, m), Times(C2, n),
                          Times(C2, p))))),
          IIntegrate(4325,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cos"), Plus(a_DEFAULT, Times(b_DEFAULT, x_))),
                              f_DEFAULT),
                          n_DEFAULT),
                      Power(
                          Times(e_DEFAULT,
                              $($s("§sin"), Plus(a_DEFAULT, Times(b_DEFAULT, x_)))),
                          m_),
                      Power(
                          Times(g_DEFAULT, $($s("§sin"),
                              Plus(c_DEFAULT, Times(d_DEFAULT, x_)))),
                          p_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(
                              f, Power(Times(e, Sin(Plus(a, Times(b,
                                  x)))), Plus(m, C1)),
                              Power(Times(f, Cos(Plus(a, Times(b, x)))), Subtract(n, C1)),
                              Power(Times(g, Sin(Plus(c, Times(d, x)))), p),
                              Power(Times(b, e, Plus(m, n, Times(C2, p))), CN1)),
                          x),
                      Dist(
                          Times(
                              C2, f, g, Subtract(Plus(n, p),
                                  C1),
                              Power(Times(e, Plus(m, n, Times(C2, p))), CN1)),
                          Integrate(
                              Times(Power(Times(e, Sin(Plus(a, Times(b, x)))), Plus(m, C1)),
                                  Power(Times(f, Cos(Plus(a, Times(b, x)))), Subtract(n,
                                      C1)),
                                  Power(Times(g, Sin(Plus(c, Times(d, x)))), Subtract(p, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g), x),
                      EqQ(Subtract(Times(b, c), Times(a, d)), C0), EqQ(Times(d, Power(b, CN1)), C2),
                      Not(IntegerQ(p)), LtQ(m, CN1), GtQ(n, C0), GtQ(p, C0),
                      NeQ(Plus(m, n,
                          Times(C2, p)), C0),
                      IntegersQ(Times(C2, m), Times(C2, n), Times(C2, p))))),
          IIntegrate(4326,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cos"), Plus(a_DEFAULT, Times(b_DEFAULT, x_))),
                              e_DEFAULT),
                          m_),
                      Power(
                          Times(f_DEFAULT, $($s("§sin"),
                              Plus(a_DEFAULT, Times(b_DEFAULT, x_)))),
                          n_DEFAULT),
                      Power(Times(g_DEFAULT, $($s("§sin"), Plus(c_DEFAULT, Times(d_DEFAULT, x_)))),
                          p_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(Power(Times(e, Cos(Plus(a, Times(b, x)))), Plus(m, C1)),
                                  Power(Times(f, Sin(
                                      Plus(a, Times(b, x)))), Plus(n, C1)),
                                  Power(Times(g, Sin(Plus(c, Times(d, x)))), p),
                                  Power(Times(b, e, f, Plus(m, p, C1)), CN1)),
                              x)),
                      Dist(
                          Times(
                              f, Plus(m, n, Times(C2, p),
                                  C2),
                              Power(Times(C2, e, g, Plus(m, p, C1)), CN1)),
                          Integrate(
                              Times(Power(Times(e, Cos(Plus(a, Times(b, x)))), Plus(m, C1)),
                                  Power(Times(f, Sin(Plus(a, Times(b, x)))), Subtract(n,
                                      C1)),
                                  Power(Times(g, Sin(Plus(c, Times(d, x)))), Plus(p, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g), x),
                      EqQ(Subtract(Times(b, c), Times(a, d)), C0), EqQ(Times(d, Power(b, CN1)), C2),
                      Not(IntegerQ(p)), LtQ(m, CN1), GtQ(n, C0), LtQ(p, CN1),
                      NeQ(Plus(m, n, Times(C2, p), C2), C0), NeQ(Plus(m, p, C1), C0),
                      IntegersQ(Times(C2, m), Times(C2, n), Times(C2, p))))),
          IIntegrate(4327,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cos"), Plus(a_DEFAULT, Times(b_DEFAULT, x_))),
                              f_DEFAULT),
                          n_DEFAULT),
                      Power(
                          Times(e_DEFAULT,
                              $($s("§sin"), Plus(a_DEFAULT, Times(b_DEFAULT, x_)))),
                          m_),
                      Power(Times(g_DEFAULT, $($s("§sin"), Plus(c_DEFAULT, Times(d_DEFAULT, x_)))),
                          p_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(
                              Power(Times(e, Sin(Plus(a,
                                  Times(b, x)))), Plus(m, C1)),
                              Power(Times(f, Cos(Plus(a, Times(b, x)))), Plus(n, C1)),
                              Power(Times(g, Sin(Plus(c, Times(d, x)))), p),
                              Power(Times(b, e, f, Plus(m, p, C1)), CN1)),
                          x),
                      Dist(Times(f, Plus(m, n, Times(C2, p), C2),
                          Power(Times(C2, e, g, Plus(m, p, C1)), CN1)),
                          Integrate(
                              Times(Power(Times(e, Sin(Plus(a, Times(b, x)))), Plus(m, C1)),
                                  Power(Times(f, Cos(Plus(a, Times(b, x)))), Subtract(n,
                                      C1)),
                                  Power(Times(g, Sin(Plus(c, Times(d, x)))), Plus(p, C1))),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g), x),
                      EqQ(Subtract(Times(b, c), Times(a, d)), C0), EqQ(Times(d, Power(b, CN1)), C2),
                      Not(IntegerQ(p)), LtQ(m, CN1), GtQ(n, C0), LtQ(p, CN1),
                      NeQ(Plus(m, n, Times(C2, p), C2), C0), NeQ(Plus(m, p,
                          C1), C0),
                      IntegersQ(Times(C2, m), Times(C2, n), Times(C2, p))))),
          IIntegrate(4328,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cos"), Plus(a_DEFAULT, Times(b_DEFAULT, x_))),
                              e_DEFAULT),
                          m_),
                      Power(Times(f_DEFAULT,
                          $($s("§sin"), Plus(a_DEFAULT, Times(b_DEFAULT, x_)))), n_DEFAULT),
                      Power(
                          Times(g_DEFAULT, $($s("§sin"),
                              Plus(c_DEFAULT, Times(d_DEFAULT, x_)))),
                          p_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(
                                  Power(Times(e, Cos(Plus(a,
                                      Times(b, x)))), Plus(m, C1)),
                                  Power(Times(f, Sin(Plus(a, Times(b, x)))), Plus(n, C1)),
                                  Power(Times(g, Sin(Plus(c, Times(d, x)))), p),
                                  Power(Times(b, e, f, Plus(m, p, C1)), CN1)),
                              x)),
                      Dist(
                          Times(Plus(m, n, Times(C2, p), C2),
                              Power(Times(Sqr(e), Plus(m, p, C1)), CN1)),
                          Integrate(Times(Power(Times(e, Cos(Plus(a, Times(b, x)))), Plus(m, C2)),
                              Power(Times(f, Sin(Plus(a, Times(b, x)))), n),
                              Power(Times(g, Sin(Plus(c, Times(d, x)))), p)), x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g, n, p), x),
                      EqQ(Subtract(Times(b, c), Times(a, d)), C0), EqQ(Times(d, Power(b, CN1)), C2),
                      Not(IntegerQ(p)), LtQ(m, CN1), NeQ(Plus(m, n, Times(C2,
                          p), C2), C0),
                      NeQ(Plus(m, p, C1), C0), IntegersQ(Times(C2, m), Times(C2, n),
                          Times(C2, p))))),
          IIntegrate(4329,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cos"), Plus(a_DEFAULT, Times(b_DEFAULT, x_))),
                              f_DEFAULT),
                          n_DEFAULT),
                      Power(
                          Times(e_DEFAULT,
                              $($s("§sin"), Plus(a_DEFAULT, Times(b_DEFAULT, x_)))),
                          m_),
                      Power(Times(g_DEFAULT, $($s("§sin"), Plus(c_DEFAULT, Times(d_DEFAULT, x_)))),
                          p_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(Times(e, Sin(Plus(a, Times(b, x)))), Plus(m, C1)),
                              Power(Times(f, Cos(
                                  Plus(a, Times(b, x)))), Plus(n,
                                      C1)),
                              Power(Times(g, Sin(Plus(c, Times(d, x)))), p),
                              Power(Times(b, e, f, Plus(m, p, C1)), CN1)),
                          x),
                      Dist(
                          Times(Plus(m, n, Times(C2, p), C2), Power(Times(Sqr(e), Plus(m, p, C1)),
                              CN1)),
                          Integrate(
                              Times(Power(Times(e, Sin(Plus(a, Times(b, x)))), Plus(m, C2)),
                                  Power(Times(f,
                                      Cos(Plus(a, Times(b, x)))), n),
                                  Power(Times(g, Sin(Plus(c, Times(d, x)))), p)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g, n, p), x),
                      EqQ(Subtract(Times(b, c), Times(a, d)), C0), EqQ(Times(d, Power(b, CN1)), C2),
                      Not(IntegerQ(p)), LtQ(m, CN1), NeQ(Plus(m, n, Times(C2, p), C2), C0),
                      NeQ(Plus(m, p,
                          C1), C0),
                      IntegersQ(Times(C2, m), Times(C2, n), Times(C2, p))))),
          IIntegrate(4330,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cos"), Plus(a_DEFAULT, Times(b_DEFAULT, x_))),
                              e_DEFAULT),
                          m_DEFAULT),
                      Power(
                          Times(f_DEFAULT, $($s("§sin"),
                              Plus(a_DEFAULT, Times(b_DEFAULT, x_)))),
                          n_DEFAULT),
                      Power(
                          Times(g_DEFAULT, $($s("§sin"),
                              Plus(c_DEFAULT, Times(d_DEFAULT, x_)))),
                          p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Times(g, Sin(Plus(c, Times(d, x)))), p),
                          Power(Times(Power(Times(e, Cos(Plus(a, Times(b, x)))), p),
                              Power(Times(f, Sin(Plus(a, Times(b, x)))), p)), CN1)),
                      Integrate(
                          Times(Power(Times(e, Cos(Plus(a, Times(b, x)))), Plus(m, p)),
                              Power(Times(f, Sin(Plus(a, Times(b, x)))), Plus(n, p))),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g, m, n, p), x),
                      EqQ(Subtract(Times(b, c), Times(a, d)), C0), EqQ(Times(d, Power(b, CN1)),
                          C2),
                      Not(IntegerQ(p))))),
          IIntegrate(4331,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§cos"), Plus(a_DEFAULT, Times(b_DEFAULT, x_))),
                              e_DEFAULT),
                          m_DEFAULT),
                      $($s("§sin"), Plus(c_DEFAULT, Times(d_DEFAULT, x_)))),
                  x_Symbol),
              Condition(
                  Negate(
                      Simp(
                          Times(Plus(m, C2),
                              Power(Times(e, Cos(Plus(a, Times(b, x)))), Plus(m, C1)),
                              Cos(Times(Plus(m, C1),
                                  Plus(a, Times(b, x)))),
                              Power(Times(d, e, Plus(m, C1)), CN1)),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, m), x), EqQ(Subtract(Times(b, c), Times(a, d)), C0),
                      EqQ(Times(d, Power(b, CN1)), Abs(Plus(m, C2)))))),
          IIntegrate(4332,
              Integrate(
                  Power(
                      Times(a_DEFAULT, Power($(F_, Plus(c_DEFAULT, Times(d_DEFAULT, x_))),
                          p_)),
                      n_),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(v,
                          ActivateTrig(F(Plus(c, Times(d, x)))))),
                      Dist(Times(Power(a, IntPart(n)),
                          Power(Times(v, Power(NonfreeFactors(v, x), CN1)), Times(p, IntPart(n))),
                          Power(Times(a, Power(v, p)), FracPart(n)), Power(
                              Power(NonfreeFactors(v, x), Times(p, FracPart(n))), CN1)),
                          Integrate(Power(NonfreeFactors(v, x), Times(n, p)), x), x)),
                  And(FreeQ(List(a, c, d, n, p), x), InertTrigQ(FSymbol), Not(IntegerQ(n)),
                      IntegerQ(p)))),
          IIntegrate(4333,
              Integrate(
                  Power(
                      Times(a_DEFAULT,
                          Power(Times(b_DEFAULT, $(F_, Plus(c_DEFAULT, Times(d_DEFAULT, x_)))),
                              p_)),
                      n_DEFAULT),
                  x_Symbol),
              Condition(
                  With(List(Set(v, ActivateTrig(F(Plus(c, Times(d, x)))))),
                      Dist(
                          Times(Power(a, IntPart(n)),
                              Power(Times(a, Power(Times(b, v), p)), FracPart(n)), Power(
                                  Power(Times(b, v), Times(p, FracPart(n))), CN1)),
                          Integrate(Power(Times(b, v), Times(n, p)), x), x)),
                  And(FreeQ(List(a, b, c, d, n,
                      p), x), InertTrigQ(
                          FSymbol),
                      Not(IntegerQ(n)), Not(IntegerQ(p))))),
          IIntegrate(4334,
              Integrate(
                  Times(u_, $(F_, Times(c_DEFAULT,
                      Plus(a_DEFAULT, Times(b_DEFAULT, x_))))),
                  x_Symbol),
              Condition(
                  With(List(Set(d, FreeFactors(Sin(Times(c, Plus(a, Times(b, x)))), x))), Condition(
                      Dist(Times(d, Power(Times(b, c), CN1)),
                          Subst(Integrate(
                              SubstFor(C1,
                                  Times(Sin(Times(c, Plus(a, Times(b, x)))), Power(d, CN1)), u, x),
                              x), x, Times(Sin(Times(c, Plus(a, Times(b, x)))), Power(d, CN1))),
                          x),
                      FunctionOfQ(Times(Sin(Times(c, Plus(a, Times(b, x)))), Power(d, CN1)), u, x,
                          True))),
                  And(FreeQ(List(a, b, c), x), Or(EqQ(FSymbol, Cos), EqQ(FSymbol, $s("§cos")))))),
          IIntegrate(4335,
              Integrate(Times(u_, $(F_,
                  Times(c_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, x_))))), x_Symbol),
              Condition(
                  With(
                      List(Set(d,
                          FreeFactors(Cos(Times(c, Plus(a, Times(b, x)))), x))),
                      Condition(
                          Negate(
                              Dist(Times(d, Power(Times(b, c), CN1)),
                                  Subst(
                                      Integrate(
                                          SubstFor(C1,
                                              Times(Cos(Times(c, Plus(a, Times(b, x)))),
                                                  Power(d, CN1)),
                                              u, x),
                                          x),
                                      x, Times(Cos(Times(c, Plus(a, Times(b, x)))), Power(d, CN1))),
                                  x)),
                          FunctionOfQ(Times(Cos(Times(c, Plus(a, Times(b, x)))), Power(d, CN1)), u,
                              x, True))),
                  And(FreeQ(List(a, b, c), x), Or(EqQ(FSymbol, Sin), EqQ(FSymbol, $s("§sin")))))),
          IIntegrate(4336,
              Integrate(
                  Times(Cosh(Times(c_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, x_)))),
                      u_),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(d,
                          FreeFactors(Sinh(Times(c, Plus(a, Times(b, x)))), x))),
                      Condition(
                          Dist(Times(d, Power(Times(b, c), CN1)),
                              Subst(
                                  Integrate(SubstFor(C1,
                                      Times(Sinh(Times(c, Plus(a, Times(b, x)))), Power(d, CN1)), u,
                                      x), x),
                                  x, Times(Sinh(Times(c, Plus(a, Times(b, x)))), Power(d, CN1))),
                              x),
                          FunctionOfQ(Times(Sinh(Times(c, Plus(a, Times(b, x)))), Power(d, CN1)), u,
                              x, True))),
                  FreeQ(List(a, b, c), x))),
          IIntegrate(4337,
              Integrate(
                  Times(u_, Sinh(Times(c_DEFAULT,
                      Plus(a_DEFAULT, Times(b_DEFAULT, x_))))),
                  x_Symbol),
              Condition(
                  With(List(Set(d, FreeFactors(Cosh(Times(c, Plus(a, Times(b, x)))), x))),
                      Condition(
                          Dist(Times(d, Power(Times(b, c), CN1)),
                              Subst(Integrate(SubstFor(
                                  C1, Times(Cosh(Times(c, Plus(a, Times(b, x)))), Power(d, CN1)), u,
                                  x), x), x, Times(Cosh(Times(c, Plus(a, Times(b, x)))),
                                      Power(d, CN1))),
                              x),
                          FunctionOfQ(
                              Times(Cosh(Times(c, Plus(a, Times(b, x)))),
                                  Power(d, CN1)),
                              u, x, True))),
                  FreeQ(List(a, b, c), x))),
          IIntegrate(4338,
              Integrate(
                  Times(u_, $(F_, Times(c_DEFAULT,
                      Plus(a_DEFAULT, Times(b_DEFAULT, x_))))),
                  x_Symbol),
              Condition(
                  With(List(Set(d, FreeFactors(Sin(Times(c, Plus(a, Times(b, x)))), x))), Condition(
                      Dist(Power(Times(b, c), CN1),
                          Subst(Integrate(
                              SubstFor(
                                  Power(x, CN1),
                                  Times(Sin(Times(c, Plus(a, Times(b, x)))), Power(d, CN1)), u, x),
                              x), x, Times(Sin(Times(c, Plus(a, Times(b, x)))), Power(d, CN1))),
                          x),
                      FunctionOfQ(Times(Sin(Times(c, Plus(a, Times(b, x)))), Power(d, CN1)), u, x,
                          True))),
                  And(FreeQ(List(a, b, c), x), Or(EqQ(FSymbol, Cot), EqQ(FSymbol, $s("§cot")))))),
          IIntegrate(4339,
              Integrate(
                  Times(u_, $(F_, Times(c_DEFAULT,
                      Plus(a_DEFAULT, Times(b_DEFAULT, x_))))),
                  x_Symbol),
              Condition(
                  With(List(Set(d, FreeFactors(Cos(Times(c, Plus(a, Times(b, x)))), x))),
                      Condition(
                          Negate(
                              Dist(
                                  Power(Times(b, c), CN1),
                                  Subst(
                                      Integrate(SubstFor(Power(x, CN1),
                                          Times(Cos(Times(c, Plus(a, Times(b, x)))), Power(d, CN1)),
                                          u, x), x),
                                      x, Times(Cos(Times(c, Plus(a, Times(b, x)))), Power(d, CN1))),
                                  x)),
                          FunctionOfQ(Times(Cos(Times(c, Plus(a, Times(b, x)))), Power(d, CN1)), u,
                              x, True))),
                  And(FreeQ(List(a, b, c), x), Or(EqQ(FSymbol, Tan), EqQ(FSymbol, $s("§tan")))))),
          IIntegrate(4340,
              Integrate(Times(Coth(Times(c_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, x_)))),
                  u_), x_Symbol),
              Condition(
                  With(List(Set(d, FreeFactors(Sinh(Times(c, Plus(a, Times(b, x)))), x))),
                      Condition(
                          Dist(
                              Power(Times(b, c), CN1),
                              Subst(
                                  Integrate(SubstFor(Power(x, CN1),
                                      Times(Sinh(Times(c, Plus(a, Times(b, x)))), Power(d, CN1)), u,
                                      x), x),
                                  x, Times(Sinh(Times(c, Plus(a, Times(b, x)))), Power(d, CN1))),
                              x),
                          FunctionOfQ(Times(Sinh(Times(c, Plus(a, Times(b, x)))), Power(d, CN1)), u,
                              x, True))),
                  FreeQ(List(a, b, c), x))));
}
