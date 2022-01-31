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
class IntRules209 {
  public static IAST RULES =
      List(
          IIntegrate(4181,
              Integrate(
                  Times(
                      $($s("§csc"), Plus(e_DEFAULT, Times(Pi, k_DEFAULT),
                          Times(f_DEFAULT, x_))),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(
                              CN2, Power(Plus(c,
                                  Times(d, x)), m),
                              ArcTanh(
                                  Times(Exp(Times(CI, k, Pi)), Exp(Times(CI, Plus(e, Times(f,
                                      x)))))),
                              Power(f, CN1)),
                          x),
                      Negate(
                          Dist(Times(d, m, Power(f, CN1)),
                              Integrate(
                                  Times(Power(Plus(c, Times(d, x)), Subtract(m, C1)),
                                      Log(Subtract(C1,
                                          Times(Exp(Times(CI, k, Pi)),
                                              Exp(Times(CI, Plus(e, Times(f, x)))))))),
                                  x),
                              x)),
                      Dist(
                          Times(d, m, Power(f,
                              CN1)),
                          Integrate(
                              Times(
                                  Power(Plus(c, Times(d, x)), Subtract(m, C1)), Log(
                                      Plus(C1,
                                          Times(Exp(Times(CI, k, Pi)), Exp(
                                              Times(CI, Plus(e, Times(f, x)))))))),
                              x),
                          x)),
                  And(FreeQ(List(c, d, e, f), x), IntegerQ(Times(C2, k)), IGtQ(m, C0)))),
          IIntegrate(4182,
              Integrate(
                  Times($($s("§csc"), Plus(e_DEFAULT,
                      Times(Complex(C0, $p("fz")), f_DEFAULT, x_))), Power(
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(CN2, Power(Plus(c, Times(d, x)), m),
                              ArcTanh(Exp(
                                  Plus(Times(CN1, CI, e), Times(f, $s("fz"), x)))),
                              Power(Times(f, $s("fz"), CI), CN1)),
                          x),
                      Negate(
                          Dist(Times(d, m, Power(Times(f, $s("fz"), CI), CN1)),
                              Integrate(
                                  Times(Power(Plus(c, Times(d, x)), Subtract(m, C1)),
                                      Log(Subtract(C1,
                                          Exp(Plus(Times(CN1, CI, e), Times(f, $s("fz"), x)))))),
                                  x),
                              x)),
                      Dist(Times(d, m, Power(Times(f, $s("fz"), CI), CN1)),
                          Integrate(Times(Power(Plus(c, Times(d, x)), Subtract(m, C1)),
                              Log(Plus(C1, Exp(Plus(Times(CN1, CI, e), Times(f, $s("fz"), x)))))),
                              x),
                          x)),
                  And(FreeQ(List(c, d, e, f, $s("fz")), x), IGtQ(m, C0)))),
          IIntegrate(4183,
              Integrate(
                  Times(
                      $($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(
                              CN2, Power(Plus(c, Times(d, x)), m), ArcTanh(Exp(
                                  Times(CI, Plus(e, Times(f, x))))),
                              Power(f, CN1)),
                          x),
                      Negate(
                          Dist(
                              Times(d, m, Power(f,
                                  CN1)),
                              Integrate(
                                  Times(Power(Plus(c, Times(d, x)), Subtract(m, C1)),
                                      Log(Subtract(C1, Exp(Times(CI, Plus(e, Times(f, x))))))),
                                  x),
                              x)),
                      Dist(Times(d, m, Power(f, CN1)),
                          Integrate(
                              Times(
                                  Power(Plus(c, Times(d, x)), Subtract(m, C1)),
                                  Log(Plus(C1, Exp(Times(CI, Plus(e, Times(f, x))))))),
                              x),
                          x)),
                  And(FreeQ(List(c, d, e, f), x), IGtQ(m, C0)))),
          IIntegrate(4184,
              Integrate(
                  Times(
                      Sqr($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))), Power(
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(Power(Plus(c, Times(d, x)), m), Cot(Plus(e, Times(f, x))),
                                  Power(f, CN1)),
                              x)),
                      Dist(
                          Times(d, m, Power(f,
                              CN1)),
                          Integrate(
                              Times(
                                  Power(Plus(c, Times(d, x)), Subtract(m,
                                      C1)),
                                  Cot(Plus(e, Times(f, x)))),
                              x),
                          x)),
                  And(FreeQ(List(c, d, e, f), x), GtQ(m, C0)))),
          IIntegrate(4185,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              b_DEFAULT),
                          n_),
                      Plus(c_DEFAULT, Times(d_DEFAULT, x_))),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(Times(Sqr(b), Plus(c, Times(d, x)), Cot(Plus(e, Times(f, x))),
                              Power(Times(b, Csc(Plus(e, Times(f, x)))), Subtract(n, C2)), Power(
                                  Times(f, Subtract(n, C1)), CN1)),
                              x)),
                      Dist(
                          Times(Sqr(b), Subtract(n, C2), Power(Subtract(n, C1),
                              CN1)),
                          Integrate(
                              Times(
                                  Plus(c, Times(d, x)), Power(Times(b, Csc(Plus(e, Times(f, x)))),
                                      Subtract(n, C2))),
                              x),
                          x),
                      Negate(
                          Simp(
                              Times(Sqr(b), d,
                                  Power(Times(b, Csc(Plus(e, Times(f, x)))), Subtract(n, C2)),
                                  Power(Times(Sqr(f), Subtract(n, C1), Subtract(n, C2)), CN1)),
                              x))),
                  And(FreeQ(List(b, c, d, e, f), x), GtQ(n, C1), NeQ(n, C2)))),
          IIntegrate(4186,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              b_DEFAULT),
                          n_),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(Simp(
                          Times(Sqr(b), Power(Plus(c, Times(d, x)), m), Cot(Plus(e, Times(f, x))),
                              Power(Times(b, Csc(Plus(e, Times(f, x)))), Subtract(n, C2)), Power(
                                  Times(f, Subtract(n, C1)), CN1)),
                          x)),
                      Dist(
                          Times(
                              Sqr(b), Sqr(d), m, Subtract(m, C1), Power(
                                  Times(Sqr(f), Subtract(n, C1), Subtract(n, C2)), CN1)),
                          Integrate(
                              Times(
                                  Power(Plus(c, Times(d,
                                      x)), Subtract(m,
                                          C2)),
                                  Power(Times(b, Csc(Plus(e, Times(f, x)))), Subtract(n, C2))),
                              x),
                          x),
                      Dist(
                          Times(Sqr(b), Subtract(n, C2), Power(Subtract(n, C1),
                              CN1)),
                          Integrate(
                              Times(Power(Plus(c, Times(d, x)), m),
                                  Power(Times(b, Csc(Plus(e, Times(f, x)))), Subtract(n, C2))),
                              x),
                          x),
                      Negate(
                          Simp(
                              Times(Sqr(b), d, m, Power(Plus(c, Times(d, x)), Subtract(m, C1)),
                                  Power(Times(b, Csc(Plus(e, Times(f, x)))), Subtract(n,
                                      C2)),
                                  Power(Times(Sqr(f), Subtract(n, C1), Subtract(n, C2)), CN1)),
                              x))),
                  And(FreeQ(List(b, c, d, e, f), x), GtQ(n, C1), NeQ(n, C2), GtQ(m, C1)))),
          IIntegrate(4187,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              b_DEFAULT),
                          n_),
                      Plus(c_DEFAULT, Times(d_DEFAULT, x_))),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(
                              d, Power(Times(b,
                                  Csc(Plus(e, Times(f, x)))), n),
                              Power(Times(Sqr(f), Sqr(n)), CN1)),
                          x),
                      Dist(Times(Plus(n, C1), Power(Times(Sqr(b), n), CN1)),
                          Integrate(
                              Times(
                                  Plus(c, Times(d, x)), Power(Times(b, Csc(Plus(e, Times(f, x)))),
                                      Plus(n, C2))),
                              x),
                          x),
                      Simp(
                          Times(Plus(c, Times(d, x)), Cos(Plus(e, Times(f, x))),
                              Power(Times(b, Csc(Plus(e, Times(f, x)))),
                                  Plus(n, C1)),
                              Power(Times(b, f, n), CN1)),
                          x)),
                  And(FreeQ(List(b, c, d, e, f), x), LtQ(n, CN1)))),
          IIntegrate(4188,
              Integrate(Times(Power(Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                  b_DEFAULT), n_), Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_)), x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(d, m, Power(Plus(c, Times(d, x)), Subtract(m, C1)),
                              Power(Times(b,
                                  Csc(Plus(e, Times(f, x)))), n),
                              Power(Times(Sqr(f), Sqr(n)), CN1)),
                          x),
                      Dist(
                          Times(Plus(n, C1), Power(Times(Sqr(b), n),
                              CN1)),
                          Integrate(
                              Times(
                                  Power(Plus(c,
                                      Times(d, x)), m),
                                  Power(Times(b, Csc(Plus(e, Times(f, x)))), Plus(n, C2))),
                              x),
                          x),
                      Negate(Dist(
                          Times(Sqr(d), m, Subtract(m, C1), Power(Times(Sqr(f), Sqr(n)), CN1)),
                          Integrate(Times(Power(Plus(c, Times(d, x)), Subtract(m, C2)),
                              Power(Times(b, Csc(Plus(e, Times(f, x)))), n)), x),
                          x)),
                      Simp(
                          Times(Power(Plus(c, Times(d, x)), m), Cos(Plus(e, Times(f, x))),
                              Power(Times(b, Csc(Plus(e, Times(f, x)))),
                                  Plus(n, C1)),
                              Power(Times(b, f, n), CN1)),
                          x)),
                  And(FreeQ(List(b, c, d, e, f), x), LtQ(n, CN1), GtQ(m, C1)))),
          IIntegrate(4189,
              Integrate(
                  Times(
                      Power(Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                          b_DEFAULT), n_),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(Times(b,
                              Sin(Plus(e, Times(f, x)))), n),
                          Power(Times(b, Csc(Plus(e, Times(f, x)))), n)),
                      Integrate(
                          Times(
                              Power(Plus(c, Times(d,
                                  x)), m),
                              Power(Power(Times(b, Sin(Plus(e, Times(f, x)))), n), CN1)),
                          x),
                      x),
                  And(FreeQ(List(b, c, d, e, f, m, n), x), Not(IntegerQ(n))))),
          IIntegrate(4190,
              Integrate(
                  Times(Power(
                      Plus(Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), b_DEFAULT),
                          a_),
                      n_DEFAULT), Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Power(Plus(c,
                              Times(d, x)), m),
                          Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), n), x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, m), x), IGtQ(m, C0), IGtQ(n, C0)))),
          IIntegrate(4191,
              Integrate(
                  Times(
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_),
                          n_DEFAULT),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(Power(Plus(c, Times(d, x)), m),
                          Power(
                              Times(Power(Sin(Plus(e, Times(f, x))), n),
                                  Power(Power(Plus(b, Times(a, Sin(Plus(e, Times(f, x))))), n),
                                      CN1)),
                              CN1),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f), x), ILtQ(n, C0), IGtQ(m, C0)))),
          IIntegrate(4192,
              Integrate(Times(
                  Power($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), n_DEFAULT),
                  Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_DEFAULT)), x_Symbol),
              Condition(
                  Simp(
                      If(MatchQ(f, Times($p("f1", true), Complex(C0, j_))),
                          If(MatchQ(e, Plus($p("e1", true), CPiHalf)),
                              Unintegrable(Times(Power(Plus(c, Times(d, x)), m),
                                  Power(
                                      Sech(Plus(Times(CI, Subtract(e, CPiHalf)),
                                          Times(CI, f, x))),
                                      n)),
                                  x),
                              Times(Power(CNI, n),
                                  Unintegrable(
                                      Times(Power(Plus(c, Times(d, x)), m),
                                          Power(Csch(Subtract(Times(CN1, CI, e), Times(CI, f, x))),
                                              n)),
                                      x))),
                          If(MatchQ(e, Plus($p("e1", true), CPiHalf)),
                              Unintegrable(Times(Power(Plus(c, Times(d, x)), m),
                                  Power(Sec(Plus(e, Times(CN1, C1D2, Pi), Times(f, x))), n)), x),
                              Unintegrable(
                                  Times(
                                      Power(Plus(c, Times(d, x)), m), Power(
                                          Csc(Plus(e, Times(f, x))), n)),
                                  x))),
                      x),
                  And(FreeQ(List(c, d, e, f, m, n), x), IntegerQ(n)))),
          IIntegrate(4193,
              Integrate(
                  Times(Power(
                      Plus(a_DEFAULT, Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                          b_DEFAULT)),
                      n_DEFAULT), Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Unintegrable(Times(Power(Plus(c, Times(d, x)), m),
                      Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), n)), x),
                  FreeQ(List(a, b, c, d, e, f, m, n), x))),
          IIntegrate(4194,
              Integrate(
                  Times(
                      Power(u_, m_DEFAULT),
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, Sec(v_))), n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(Times(Power(ExpandToSum(u, x), m),
                      Power(Plus(a, Times(b, Sec(ExpandToSum(v, x)))), n)), x),
                  And(FreeQ(List(a, b, m, n), x), LinearQ(List(u, v), x),
                      Not(LinearMatchQ(List(u, v), x))))),
          IIntegrate(4195,
              Integrate(
                  Times(
                      Power(Plus(a_DEFAULT, Times(Csc(v_), b_DEFAULT)), n_DEFAULT), Power(u_,
                          m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(
                          Power(ExpandToSum(u,
                              x), m),
                          Power(Plus(a, Times(b, Csc(ExpandToSum(v, x)))), n)),
                      x),
                  And(FreeQ(List(a, b, m,
                      n), x), LinearQ(List(u, v),
                          x),
                      Not(LinearMatchQ(List(u, v), x))))),
          IIntegrate(4196,
              Integrate(
                  Power(
                      Plus(
                          a_DEFAULT, Times(b_DEFAULT, Sec(Plus(c_DEFAULT,
                              Times(d_DEFAULT, Power(x_, n_)))))),
                      p_DEFAULT),
                  x_Symbol),
              Condition(
                  Dist(Power(n, CN1),
                      Subst(
                          Integrate(
                              Times(
                                  Power(x, Subtract(Power(n, CN1),
                                      C1)),
                                  Power(Plus(a, Times(b, Sec(Plus(c, Times(d, x))))), p)),
                              x),
                          x, Power(x, n)),
                      x),
                  And(FreeQ(List(a, b, c, d, p), x), IGtQ(Power(n, CN1), C0), IntegerQ(p)))),
          IIntegrate(4197,
              Integrate(
                  Power(
                      Plus(a_DEFAULT,
                          Times(Csc(Plus(c_DEFAULT, Times(d_DEFAULT, Power(x_, n_)))), b_DEFAULT)),
                      p_DEFAULT),
                  x_Symbol),
              Condition(
                  Dist(Power(n, CN1),
                      Subst(
                          Integrate(Times(Power(x, Subtract(Power(n, CN1), C1)),
                              Power(Plus(a, Times(b, Csc(Plus(c, Times(d, x))))), p)), x),
                          x, Power(x, n)),
                      x),
                  And(FreeQ(List(a, b, c, d, p), x), IGtQ(Power(n, CN1), C0), IntegerQ(p)))),
          IIntegrate(4198,
              Integrate(
                  Power(
                      Plus(a_DEFAULT,
                          Times(b_DEFAULT, Sec(Plus(c_DEFAULT, Times(d_DEFAULT, Power(x_, n_)))))),
                      p_DEFAULT),
                  x_Symbol),
              Condition(
                  Unintegrable(Power(Plus(a, Times(b, Sec(Plus(c, Times(d, Power(x, n)))))), p),
                      x),
                  FreeQ(List(a, b, c, d, n, p), x))),
          IIntegrate(4199,
              Integrate(
                  Power(
                      Plus(
                          a_DEFAULT, Times(Csc(Plus(c_DEFAULT, Times(d_DEFAULT, Power(x_, n_)))),
                              b_DEFAULT)),
                      p_DEFAULT),
                  x_Symbol),
              Condition(
                  Unintegrable(Power(Plus(a, Times(b, Csc(Plus(c, Times(d, Power(x, n)))))),
                      p), x),
                  FreeQ(List(a, b, c, d, n, p), x))),
          IIntegrate(4200, Integrate(Power(Plus(a_DEFAULT,
              Times(b_DEFAULT, Sec(Plus(c_DEFAULT, Times(d_DEFAULT, Power(u_, n_)))))), p_DEFAULT),
              x_Symbol),
              Condition(
                  Dist(Power(Coefficient(u, x, C1), CN1),
                      Subst(
                          Integrate(
                              Power(Plus(a, Times(b, Sec(Plus(c, Times(d, Power(x, n)))))), p), x),
                          x, u),
                      x),
                  And(FreeQ(List(a, b, c, d, n, p), x), LinearQ(u, x), NeQ(u, x)))));
}
