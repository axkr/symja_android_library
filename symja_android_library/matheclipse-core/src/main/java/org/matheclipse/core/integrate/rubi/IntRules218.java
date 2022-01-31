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
class IntRules218 {
  public static IAST RULES =
      List(
          IIntegrate(4361,
              Integrate(
                  Times(u_, $(F_, Times(c_DEFAULT,
                      Plus(a_DEFAULT, Times(b_DEFAULT, x_))))),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(d,
                          FreeFactors(Cos(Times(c, Plus(a, Times(b, x)))), x))),
                      Condition(
                          Negate(
                              Dist(Power(Times(b, c), CN1),
                                  Subst(
                                      Integrate(
                                          SubstFor(Power(x, CN1),
                                              Times(Cos(Times(c, Plus(a, Times(b, x)))),
                                                  Power(d, CN1)),
                                              u, x),
                                          x),
                                      x, Times(Cos(Times(c, Plus(a, Times(b, x)))), Power(d, CN1))),
                                  x)),
                          FunctionOfQ(Times(Cos(Times(c, Plus(a, Times(b, x)))), Power(d, CN1)), u,
                              x))),
                  And(FreeQ(List(a, b, c), x), Or(EqQ(FSymbol, Tan), EqQ(FSymbol, $s("§tan")))))),
          IIntegrate(4362,
              Integrate(
                  Times(Coth(Times(c_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, x_)))),
                      u_),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(d,
                          FreeFactors(Sinh(Times(c, Plus(a, Times(b, x)))), x))),
                      Condition(
                          Dist(Power(Times(b, c), CN1),
                              Subst(
                                  Integrate(SubstFor(Power(x, CN1),
                                      Times(Sinh(Times(c, Plus(a, Times(b, x)))), Power(d, CN1)), u,
                                      x), x),
                                  x, Times(Sinh(Times(c, Plus(a, Times(b, x)))), Power(d, CN1))),
                              x),
                          FunctionOfQ(Times(Sinh(Times(c, Plus(a, Times(b, x)))), Power(d, CN1)), u,
                              x))),
                  FreeQ(List(a, b, c), x))),
          IIntegrate(4363,
              Integrate(
                  Times(u_, Tanh(Times(c_DEFAULT,
                      Plus(a_DEFAULT, Times(b_DEFAULT, x_))))),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(d,
                          FreeFactors(Cosh(Times(c, Plus(a, Times(b, x)))), x))),
                      Condition(
                          Dist(Power(Times(b, c), CN1),
                              Subst(
                                  Integrate(SubstFor(Power(x, CN1),
                                      Times(Cosh(Times(c, Plus(a, Times(b, x)))), Power(d, CN1)), u,
                                      x), x),
                                  x, Times(Cosh(Times(c, Plus(a, Times(b, x)))), Power(d, CN1))),
                              x),
                          FunctionOfQ(Times(Cosh(Times(c, Plus(a, Times(b, x)))), Power(d, CN1)), u,
                              x))),
                  FreeQ(List(a, b, c), x))),
          IIntegrate(4364, Integrate(
              Times(u_, Power($(F_, Times(c_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, x_)))), n_)),
              x_Symbol),
              Condition(
                  With(List(Set(d, FreeFactors(Sin(Times(c, Plus(a, Times(b, x)))), x))), Condition(
                      Dist(Times(d, Power(Times(b, c), CN1)), Subst(
                          Integrate(SubstFor(
                              Power(Subtract(C1, Times(Sqr(d), Sqr(x))),
                                  Times(C1D2, Subtract(n, C1))),
                              Times(Sin(Times(c, Plus(a, Times(b, x)))), Power(d, CN1)), u, x), x),
                          x, Times(Sin(Times(c, Plus(a, Times(b, x)))), Power(d, CN1))), x),
                      FunctionOfQ(Times(Sin(Times(c, Plus(a, Times(b, x)))), Power(d, CN1)), u,
                          x))),
                  And(FreeQ(List(a, b, c), x), IntegerQ(Times(C1D2,
                      Subtract(n, C1))), NonsumQ(
                          u),
                      Or(EqQ(FSymbol, Cos), EqQ(FSymbol, $s("§cos")))))),
          IIntegrate(4365,
              Integrate(
                  Times(u_,
                      Power($(F_, Times(c_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, x_)))), n_)),
                  x_Symbol),
              Condition(
                  With(List(Set(d, FreeFactors(Sin(Times(c, Plus(a, Times(b, x)))), x))),
                      Condition(
                          Dist(Times(d,
                              Power(Times(b, c), CN1)),
                              Subst(
                                  Integrate(SubstFor(
                                      Power(Subtract(C1, Times(Sqr(d), Sqr(x))),
                                          Times(C1D2, Subtract(Negate(n), C1))),
                                      Times(Sin(Times(c, Plus(a, Times(b, x)))), Power(d, CN1)), u,
                                      x), x),
                                  x, Times(Sin(Times(c, Plus(a, Times(b, x)))), Power(d, CN1))),
                              x),
                          FunctionOfQ(Times(Sin(Times(c, Plus(a, Times(b, x)))), Power(d, CN1)), u,
                              x))),
                  And(FreeQ(List(a, b, c), x), IntegerQ(Times(C1D2,
                      Subtract(n, C1))), NonsumQ(
                          u),
                      Or(EqQ(FSymbol, Sec), EqQ(FSymbol, $s("§sec")))))),
          IIntegrate(4366,
              Integrate(
                  Times(u_,
                      Power($(F_, Times(c_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, x_)))), n_)),
                  x_Symbol),
              Condition(
                  With(List(Set(d, FreeFactors(Cos(Times(c, Plus(a, Times(b, x)))), x))),
                      Condition(
                          Negate(Dist(Times(d, Power(Times(b, c), CN1)), Subst(Integrate(
                              SubstFor(
                                  Power(Subtract(C1, Times(Sqr(d), Sqr(x))),
                                      Times(C1D2, Subtract(n, C1))),
                                  Times(Cos(Times(c, Plus(a, Times(b, x)))), Power(d, CN1)), u, x),
                              x), x, Times(Cos(Times(c, Plus(a, Times(b, x)))), Power(d, CN1))),
                              x)),
                          FunctionOfQ(Times(Cos(Times(c,
                              Plus(a, Times(b, x)))), Power(d,
                                  CN1)),
                              u, x))),
                  And(FreeQ(List(a, b, c), x), IntegerQ(Times(C1D2,
                      Subtract(n, C1))), NonsumQ(
                          u),
                      Or(EqQ(FSymbol, Sin), EqQ(FSymbol, $s("§sin")))))),
          IIntegrate(4367,
              Integrate(
                  Times(u_,
                      Power($(F_, Times(c_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, x_)))), n_)),
                  x_Symbol),
              Condition(
                  With(List(Set(d, FreeFactors(Cos(Times(c, Plus(a, Times(b, x)))), x))),
                      Condition(
                          Negate(Dist(Times(d, Power(Times(b, c), CN1)), Subst(Integrate(
                              SubstFor(
                                  Power(Subtract(C1, Times(Sqr(d), Sqr(x))),
                                      Times(C1D2, Subtract(Negate(n), C1))),
                                  Times(Cos(Times(c, Plus(a, Times(b, x)))), Power(d, CN1)), u, x),
                              x), x, Times(Cos(Times(c, Plus(a, Times(b, x)))), Power(d, CN1))),
                              x)),
                          FunctionOfQ(Times(Cos(Times(c,
                              Plus(a, Times(b, x)))), Power(d,
                                  CN1)),
                              u, x))),
                  And(FreeQ(List(a, b, c), x), IntegerQ(Times(C1D2,
                      Subtract(n, C1))), NonsumQ(
                          u),
                      Or(EqQ(FSymbol, Csc), EqQ(FSymbol, $s("§csc")))))),
          IIntegrate(4368, Integrate(
              Times(Power(Cosh(Times(c_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, x_)))), n_), u_),
              x_Symbol),
              Condition(With(List(Set(d, FreeFactors(Sinh(Times(c, Plus(a, Times(b, x)))), x))),
                  Condition(
                      Dist(Times(d, Power(Times(b, c), CN1)),
                          Subst(Integrate(
                              SubstFor(
                                  Power(Plus(C1, Times(Sqr(d), Sqr(x))),
                                      Times(C1D2, Subtract(n, C1))),
                                  Times(Sinh(Times(c, Plus(a, Times(b, x)))), Power(d, CN1)), u, x),
                              x), x, Times(Sinh(Times(c, Plus(a, Times(b, x)))), Power(d, CN1))),
                          x),
                      FunctionOfQ(Times(Sinh(Times(c, Plus(a, Times(b, x)))), Power(d, CN1)), u,
                          x))),
                  And(FreeQ(List(a, b, c), x), IntegerQ(Times(C1D2,
                      Subtract(n, C1))), NonsumQ(
                          u)))),
          IIntegrate(4369,
              Integrate(
                  Times(u_,
                      Power(Sech(Times(c_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, x_)))), n_)),
                  x_Symbol),
              Condition(With(
                  List(Set(d, FreeFactors(Sinh(Times(c, Plus(a, Times(b, x)))), x))), Condition(
                      Dist(Times(d, Power(Times(b, c), CN1)), Subst(
                          Integrate(
                              SubstFor(
                                  Power(Plus(C1, Times(Sqr(d), Sqr(x))),
                                      Times(C1D2, Subtract(Negate(n), C1))),
                                  Times(Sinh(Times(c, Plus(a, Times(b, x)))), Power(d, CN1)), u, x),
                              x),
                          x, Times(Sinh(Times(c, Plus(a, Times(b, x)))), Power(d, CN1))), x),
                      FunctionOfQ(Times(Sinh(Times(c, Plus(a, Times(b, x)))), Power(d, CN1)), u,
                          x))),
                  And(FreeQ(List(a, b, c), x), IntegerQ(Times(C1D2, Subtract(n, C1))),
                      NonsumQ(u)))),
          IIntegrate(4370, Integrate(
              Times(u_, Power(Sinh(Times(c_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, x_)))), n_)),
              x_Symbol),
              Condition(
                  With(List(Set(d, FreeFactors(Cosh(Times(c, Plus(a, Times(b, x)))), x))),
                      Condition(
                          Dist(Times(d, Power(Times(b, c), CN1)), Subst(Integrate(
                              SubstFor(
                                  Power(Plus(CN1, Times(Sqr(d), Sqr(x))),
                                      Times(C1D2, Subtract(n, C1))),
                                  Times(Cosh(Times(c, Plus(a, Times(b, x)))), Power(d, CN1)), u, x),
                              x), x, Times(Cosh(Times(c, Plus(a, Times(b, x)))), Power(d, CN1))),
                              x),
                          FunctionOfQ(Times(Cosh(Times(c, Plus(a, Times(b, x)))), Power(d, CN1)), u,
                              x))),
                  And(FreeQ(List(a, b, c), x), IntegerQ(Times(C1D2,
                      Subtract(n, C1))), NonsumQ(
                          u)))),
          IIntegrate(4371,
              Integrate(
                  Times(Power(Csch(Times(c_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, x_)))), n_),
                      u_),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(d,
                          FreeFactors(Cosh(Times(c, Plus(a, Times(b, x)))), x))),
                      Condition(
                          Dist(Times(d, Power(Times(b, c), CN1)),
                              Subst(
                                  Integrate(SubstFor(
                                      Power(Plus(CN1, Times(Sqr(d), Sqr(x))),
                                          Times(C1D2, Subtract(Negate(n), C1))),
                                      Times(Cosh(Times(c, Plus(a, Times(b, x)))), Power(d, CN1)), u,
                                      x), x),
                                  x, Times(Cosh(Times(c, Plus(a, Times(b, x)))), Power(d, CN1))),
                              x),
                          FunctionOfQ(Times(Cosh(Times(c, Plus(a, Times(b, x)))), Power(d, CN1)), u,
                              x))),
                  And(FreeQ(List(a, b, c), x), IntegerQ(Times(C1D2,
                      Subtract(n, C1))), NonsumQ(
                          u)))),
          IIntegrate(4372,
              Integrate(
                  Times(
                      u_, Power($(F_, Times(c_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, x_)))),
                          n_)),
                  x_Symbol),
              Condition(
                  With(List(Set(d, FreeFactors(Sin(Times(c, Plus(a, Times(b, x)))), x))),
                      Condition(
                          Dist(Power(Times(b, c, Power(d, Subtract(n, C1))), CN1), Subst(Integrate(
                              SubstFor(
                                  Times(Power(Subtract(C1, Times(Sqr(d), Sqr(x))),
                                      Times(C1D2, Subtract(n, C1))), Power(Power(x, n), CN1)),
                                  Times(Sin(Times(c, Plus(a, Times(b, x)))), Power(d, CN1)), u, x),
                              x), x, Times(Sin(Times(c, Plus(a, Times(b, x)))), Power(d, CN1))), x),
                          FunctionOfQ(Times(Sin(Times(c, Plus(a, Times(b, x)))), Power(d, CN1)), u,
                              x))),
                  And(FreeQ(List(a, b, c), x), IntegerQ(Times(C1D2,
                      Subtract(n, C1))), NonsumQ(
                          u),
                      Or(EqQ(FSymbol, Cot), EqQ(FSymbol, $s("§cot")))))),
          IIntegrate(4373,
              Integrate(
                  Times(
                      u_, Power($(F_, Times(c_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, x_)))),
                          n_)),
                  x_Symbol),
              Condition(
                  With(List(Set(d, FreeFactors(Cos(Times(c, Plus(a, Times(b, x)))), x))),
                      Condition(
                          Negate(
                              Dist(Power(Times(b, c, Power(d, Subtract(n, C1))), CN1),
                                  Subst(Integrate(SubstFor(
                                      Times(
                                          Power(Subtract(C1, Times(Sqr(d), Sqr(x))),
                                              Times(C1D2, Subtract(n, C1))),
                                          Power(Power(x, n), CN1)),
                                      Times(Cos(Times(c, Plus(a, Times(b, x)))), Power(d, CN1)), u,
                                      x), x), x, Times(Cos(Times(c, Plus(a, Times(b, x)))),
                                          Power(d, CN1))),
                                  x)),
                          FunctionOfQ(Times(Cos(Times(c, Plus(a, Times(b, x)))), Power(d, CN1)), u,
                              x))),
                  And(FreeQ(List(a, b, c), x), IntegerQ(Times(C1D2,
                      Subtract(n, C1))), NonsumQ(
                          u),
                      Or(EqQ(FSymbol, Tan), EqQ(FSymbol, $s("§tan")))))),
          IIntegrate(4374,
              Integrate(
                  Times(Power(Coth(Times(c_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, x_)))), n_),
                      u_),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(d,
                          FreeFactors(Sinh(Times(c, Plus(a, Times(b, x)))), x))),
                      Condition(
                          Dist(Power(Times(b, c, Power(d, Subtract(n, C1))), CN1),
                              Subst(Integrate(
                                  SubstFor(
                                      Times(
                                          Power(Plus(C1, Times(Sqr(d), Sqr(x))),
                                              Times(C1D2, Subtract(n, C1))),
                                          Power(Power(x, n), CN1)),
                                      Times(Sinh(Times(c, Plus(a, Times(b, x)))), Power(d, CN1)), u,
                                      x),
                                  x), x,
                                  Times(Sinh(Times(c, Plus(a, Times(b, x)))), Power(d, CN1))),
                              x),
                          FunctionOfQ(Times(Sinh(Times(c, Plus(a, Times(b, x)))), Power(d, CN1)), u,
                              x))),
                  And(FreeQ(List(a, b, c), x), IntegerQ(Times(C1D2,
                      Subtract(n, C1))), NonsumQ(
                          u)))),
          IIntegrate(4375,
              Integrate(
                  Times(u_,
                      Power(Tanh(Times(c_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, x_)))), n_)),
                  x_Symbol),
              Condition(With(List(Set(d, FreeFactors(Cosh(Times(c, Plus(a, Times(b, x)))), x))),
                  Condition(
                      Dist(
                          Power(Times(b, c, Power(d, Subtract(n, C1))), CN1), Subst(
                              Integrate(
                                  SubstFor(
                                      Times(
                                          Power(Plus(CN1, Times(Sqr(d), Sqr(x))),
                                              Times(C1D2, Subtract(n, C1))),
                                          Power(Power(x, n), CN1)),
                                      Times(Cosh(Times(c, Plus(a, Times(b, x)))), Power(d, CN1)), u,
                                      x),
                                  x),
                              x, Times(Cosh(Times(c, Plus(a, Times(b, x)))), Power(d, CN1))),
                          x),
                      FunctionOfQ(Times(Cosh(Times(c, Plus(a, Times(b, x)))), Power(d, CN1)), u,
                          x))),
                  And(FreeQ(List(a, b, c), x), IntegerQ(Times(C1D2,
                      Subtract(n, C1))), NonsumQ(
                          u)))),
          IIntegrate(4376,
              Integrate(
                  Times(u_,
                      Plus(v_,
                          Times(d_DEFAULT,
                              Power(
                                  $(F_, Times(c_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, x_)))),
                                  n_DEFAULT)))),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(e,
                          FreeFactors(Sin(Times(c, Plus(a, Times(b, x)))), x))),
                      Condition(
                          Plus(Integrate(ActivateTrig(Times(u, v)), x),
                              Dist(d,
                                  Integrate(Times(ActivateTrig(u),
                                      Power(Cos(Times(c, Plus(a, Times(b, x)))), n)), x),
                                  x)),
                          FunctionOfQ(Times(Sin(Times(c,
                              Plus(a, Times(b, x)))), Power(e,
                                  CN1)),
                              u, x))),
                  And(FreeQ(List(a, b, c, d), x), Not(FreeQ(v, x)),
                      IntegerQ(Times(C1D2,
                          Subtract(n, C1))),
                      NonsumQ(u), Or(EqQ(FSymbol, Cos), EqQ(FSymbol, $s("§cos")))))),
          IIntegrate(4377,
              Integrate(
                  Times(u_,
                      Plus(v_,
                          Times(d_DEFAULT,
                              Power($(F_, Times(c_DEFAULT, Plus(a_DEFAULT, Times(b_DEFAULT, x_)))),
                                  n_DEFAULT)))),
                  x_Symbol),
              Condition(
                  With(List(Set(e, FreeFactors(Cos(Times(c, Plus(a, Times(b, x)))), x))),
                      Condition(Plus(Integrate(ActivateTrig(Times(u, v)), x),
                          Dist(d,
                              Integrate(Times(ActivateTrig(u),
                                  Power(Sin(Times(c, Plus(a, Times(b, x)))), n)), x),
                              x)),
                          FunctionOfQ(Times(Cos(Times(c,
                              Plus(a, Times(b, x)))), Power(e,
                                  CN1)),
                              u, x))),
                  And(FreeQ(List(a, b, c, d), x), Not(FreeQ(v, x)),
                      IntegerQ(Times(C1D2, Subtract(n, C1))), NonsumQ(u), Or(EqQ(FSymbol, Sin),
                          EqQ(FSymbol, $s("§sin")))))),
          IIntegrate(4378, Integrate(u_, x_Symbol),
              With(
                  List(Set(v,
                      FunctionOfTrig(u, x))),
                  Condition(
                      Simp(With(List(Set(d, FreeFactors(Sin(v), x))),
                          Dist(Times(d, Power(Coefficient(v, x, C1), CN1)),
                              Subst(
                                  Integrate(SubstFor(C1, Times(Sin(v), Power(d, CN1)),
                                      Times(u, Power(Cos(v), CN1)), x), x),
                                  x, Times(Sin(v), Power(d, CN1))),
                              x)),
                          x),
                      And(Not(FalseQ(v)),
                          FunctionOfQ(NonfreeFactors(Sin(v), x), Times(u, Power(Cos(v), CN1)),
                              x))))),
          IIntegrate(4379, Integrate(u_, x_Symbol),
              With(List(Set(v, FunctionOfTrig(u, x))),
                  Condition(
                      Simp(
                          With(List(Set(d, FreeFactors(Cos(v), x))),
                              Dist(Times(CN1, d, Power(Coefficient(v, x, C1), CN1)),
                                  Subst(
                                      Integrate(SubstFor(C1, Times(Cos(v), Power(d, CN1)),
                                          Times(u, Power(Sin(v), CN1)), x), x),
                                      x, Times(Cos(v), Power(d, CN1))),
                                  x)),
                          x),
                      And(Not(FalseQ(v)),
                          FunctionOfQ(NonfreeFactors(Cos(
                              v), x), Times(u,
                                  Power(Sin(v), CN1)),
                              x))))),
          IIntegrate(4380,
              Integrate(Times(u_DEFAULT,
                  Power(Plus(a_DEFAULT,
                      Times(Sqr($($s("§cos"), Plus(d_DEFAULT, Times(e_DEFAULT, x_)))), b_DEFAULT),
                      Times(c_DEFAULT, Sqr($($s("§sin"), Plus(d_DEFAULT, Times(e_DEFAULT, x_)))))),
                      p_DEFAULT)),
                  x_Symbol),
              Condition(Dist(Power(Plus(a, c), p), Integrate(ActivateTrig(u), x), x),
                  And(FreeQ(List(a, b, c, d, e, p), x), EqQ(Subtract(b, c), C0)))));
}
