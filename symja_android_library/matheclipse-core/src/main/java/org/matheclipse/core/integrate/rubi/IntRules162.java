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
class IntRules162 {
  public static IAST RULES =
      List(
          IIntegrate(3241,
              Integrate(
                  Times(Power($($s("§cos"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))), m_),
                      Power(
                          Plus(
                              Times(Power($($s("§cos"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))), q_),
                                  c_DEFAULT),
                              a_,
                              Times(b_DEFAULT,
                                  Power($($s("§sin"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))), p_))),
                          n_)),
                  x_Symbol),
              Condition(
                  Module(List(Set(f, FreeFactors(Tan(Plus(d, Times(e, x))), x))),
                      Dist(
                          Times(f, Power(
                              e, CN1)),
                          Subst(Integrate(Times(Power(ExpandToSum(
                              Plus(c,
                                  Times(b,
                                      Power(Plus(C1, Times(Sqr(f), Sqr(x))),
                                          Subtract(Times(C1D2, q), Times(C1D2, p)))),
                                  Times(a, Power(Plus(C1, Times(Sqr(f), Sqr(x))), Times(C1D2, q)))),
                              x), n), Power(
                                  Power(Plus(C1, Times(Sqr(f), Sqr(x))),
                                      Plus(Times(C1D2, m), Times(C1D2, n, q), C1)),
                                  CN1)),
                              x), x, Times(Tan(Plus(d, Times(e, x))), Power(f, CN1))),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), IntegerQ(Times(C1D2, m)),
                      IntegerQ(Times(C1D2, p)), IntegerQ(Times(C1D2,
                          q)),
                      IntegerQ(n), GtQ(p, C0), LeQ(p, q)))),
          IIntegrate(3242,
              Integrate(
                  Times(Power($($s("§sin"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))), m_),
                      Power(
                          Plus(
                              Times(Power($($s("§cos"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))), p_),
                                  b_DEFAULT),
                              a_,
                              Times(
                                  c_DEFAULT,
                                  Power($($s("§sin"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))), q_))),
                          n_)),
                  x_Symbol),
              Condition(Module(List(Set(f, FreeFactors(Cot(Plus(d, Times(e, x))), x))),
                  Negate(Dist(Times(f, Power(e, CN1)),
                      Subst(
                          Integrate(
                              Times(
                                  Power(
                                      ExpandToSum(Plus(
                                          Times(a,
                                              Power(Plus(C1, Times(Sqr(f), Sqr(x))),
                                                  Times(C1D2, p))),
                                          Times(b, Power(f, p), Power(x, p)),
                                          Times(c,
                                              Power(Plus(C1, Times(Sqr(f), Sqr(x))),
                                                  Subtract(Times(C1D2, p), Times(C1D2, q))))),
                                          x),
                                      n),
                                  Power(Power(Plus(C1, Times(Sqr(f), Sqr(x))),
                                      Plus(Times(C1D2, m), Times(C1D2, n, p), C1)), CN1)),
                              x),
                          x, Times(Cot(Plus(d, Times(e, x))), Power(f, CN1))),
                      x))),
                  And(FreeQ(List(a, b, c, d, e), x), IntegerQ(Times(C1D2, m)),
                      IntegerQ(Times(C1D2, p)), IntegerQ(Times(C1D2, q)), IntegerQ(n), LtQ(C0, q,
                          p)))),
          IIntegrate(3243,
              Integrate(
                  Times(
                      Power($($s("§cos"),
                          Plus(d_DEFAULT, Times(e_DEFAULT, x_))), m_),
                      Power(
                          Plus(
                              Times(Power($($s("§cos"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))),
                                  q_), c_DEFAULT),
                              a_,
                              Times(
                                  b_DEFAULT, Power($($s("§sin"),
                                      Plus(d_DEFAULT, Times(e_DEFAULT, x_))),
                                      p_))),
                          n_)),
                  x_Symbol),
              Condition(
                  Module(List(Set(f, FreeFactors(Tan(Plus(d, Times(e, x))), x))),
                      Dist(Times(f, Power(e, CN1)),
                          Subst(
                              Integrate(
                                  Times(
                                      Power(ExpandToSum(Plus(
                                          Times(a,
                                              Power(Plus(C1, Times(Sqr(f), Sqr(x))),
                                                  Times(C1D2, p))),
                                          Times(b, Power(f, p), Power(x, p)),
                                          Times(c,
                                              Power(Plus(C1, Times(Sqr(f), Sqr(x))),
                                                  Subtract(Times(C1D2, p), Times(C1D2, q))))),
                                          x), n),
                                      Power(Power(Plus(C1, Times(Sqr(f), Sqr(x))),
                                          Plus(Times(C1D2, m), Times(C1D2, n, p), C1)), CN1)),
                                  x),
                              x, Times(Tan(Plus(d, Times(e, x))), Power(f, CN1))),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), IntegerQ(Times(C1D2, m)),
                      IntegerQ(Times(C1D2, p)), IntegerQ(Times(C1D2,
                          q)),
                      IntegerQ(n), LtQ(C0, q, p)))),
          IIntegrate(3244,
              Integrate(
                  Power(
                      Plus(a_DEFAULT,
                          Times(b_DEFAULT,
                              Power($($s("§sin"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))),
                                  n_DEFAULT)),
                          Times(c_DEFAULT,
                              Power($($s("§sin"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))),
                                  $p("n2", true)))),
                      p_DEFAULT),
                  x_Symbol),
              Condition(
                  Dist(
                      Power(Times(Power(C4, p),
                          Power(c, p)), CN1),
                      Integrate(
                          Power(
                              Plus(b, Times(C2, c,
                                  Power(Sin(Plus(d, Times(e, x))), n))),
                              Times(C2, p)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, n), x), EqQ($s("n2"), Times(C2,
                      n)), EqQ(Subtract(Sqr(b), Times(C4, a, c)),
                          C0),
                      IntegerQ(p)))),
          IIntegrate(3245,
              Integrate(
                  Power(Plus(a_DEFAULT,
                      Times(Power($($s("§cos"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))),
                          n_DEFAULT), b_DEFAULT),
                      Times(
                          Power($($s("§cos"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))),
                              $p("n2", true)),
                          c_DEFAULT)),
                      p_DEFAULT),
                  x_Symbol),
              Condition(
                  Dist(
                      Power(Times(Power(C4, p),
                          Power(c, p)), CN1),
                      Integrate(
                          Power(
                              Plus(b, Times(C2, c,
                                  Power(Cos(Plus(d, Times(e, x))), n))),
                              Times(C2, p)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, n), x), EqQ($s("n2"), Times(C2,
                      n)), EqQ(Subtract(Sqr(b), Times(C4, a, c)),
                          C0),
                      IntegerQ(p)))),
          IIntegrate(3246,
              Integrate(
                  Power(Plus(a_DEFAULT,
                      Times(
                          b_DEFAULT, Power($($s("§sin"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))),
                              n_DEFAULT)),
                      Times(c_DEFAULT,
                          Power(
                              $($s("§sin"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))), $p("n2",
                                  true)))),
                      p_),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(
                              Plus(
                                  a, Times(b, Power(Sin(Plus(d, Times(e, x))), n)), Times(c,
                                      Power(Sin(Plus(d, Times(e, x))), Times(C2, n)))),
                              p),
                          Power(
                              Power(
                                  Plus(b, Times(C2, c, Power(Sin(Plus(d, Times(e, x))), n))), Times(
                                      C2, p)),
                              CN1)),
                      Integrate(
                          Times(u,
                              Power(
                                  Plus(b, Times(C2, c, Power(Sin(Plus(d, Times(e, x))), n))), Times(
                                      C2, p))),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, n, p), x), EqQ($s("n2"), Times(C2,
                      n)), EqQ(Subtract(Sqr(b), Times(C4, a, c)),
                          C0),
                      Not(IntegerQ(p))))),
          IIntegrate(3247,
              Integrate(
                  Power(
                      Plus(
                          a_DEFAULT,
                          Times(
                              Power($($s("§cos"),
                                  Plus(d_DEFAULT, Times(e_DEFAULT, x_))),
                                  n_DEFAULT),
                              b_DEFAULT),
                          Times(Power($($s("§cos"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))),
                              $p("n2", true)), c_DEFAULT)),
                      p_),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(
                              Plus(a, Times(b, Power(Cos(Plus(d, Times(e, x))), n)), Times(c,
                                  Power(Cos(Plus(d, Times(e, x))), Times(C2, n)))),
                              p),
                          Power(
                              Power(Plus(b, Times(C2, c, Power(Cos(Plus(d, Times(e, x))), n))),
                                  Times(C2, p)),
                              CN1)),
                      Integrate(
                          Times(u,
                              Power(
                                  Plus(b, Times(C2, c, Power(Cos(Plus(d, Times(e, x))), n))), Times(
                                      C2, p))),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, n, p), x), EqQ($s("n2"), Times(C2,
                      n)), EqQ(Subtract(Sqr(b), Times(C4, a, c)),
                          C0),
                      Not(IntegerQ(p))))),
          IIntegrate(3248,
              Integrate(
                  Power(
                      Plus(a_DEFAULT,
                          Times(b_DEFAULT,
                              Power($($s("§sin"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))),
                                  n_DEFAULT)),
                          Times(c_DEFAULT,
                              Power(
                                  $($s("§sin"), Plus(d_DEFAULT,
                                      Times(e_DEFAULT, x_))),
                                  $p("n2", true)))),
                      CN1),
                  x_Symbol),
              Condition(
                  Module(
                      List(Set(q,
                          Rt(Subtract(Sqr(b), Times(C4, a, c)), C2))),
                      Subtract(
                          Dist(Times(C2, c, Power(q, CN1)),
                              Integrate(
                                  Power(
                                      Plus(b, Negate(q),
                                          Times(C2, c, Power(Sin(Plus(d, Times(e, x))), n))),
                                      CN1),
                                  x),
                              x),
                          Dist(
                              Times(C2, c, Power(q,
                                  CN1)),
                              Integrate(
                                  Power(
                                      Plus(b, q, Times(C2, c, Power(Sin(Plus(d, Times(e, x))),
                                          n))),
                                      CN1),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e, n), x), EqQ($s("n2"), Times(C2,
                      n)), NeQ(Subtract(Sqr(b), Times(C4, a, c)),
                          C0)))),
          IIntegrate(3249,
              Integrate(
                  Power(
                      Plus(
                          a_DEFAULT,
                          Times(
                              Power($($s("§cos"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))),
                                  n_DEFAULT),
                              b_DEFAULT),
                          Times(
                              Power(
                                  $($s("§cos"), Plus(d_DEFAULT,
                                      Times(e_DEFAULT, x_))),
                                  $p("n2", true)),
                              c_DEFAULT)),
                      CN1),
                  x_Symbol),
              Condition(
                  Module(
                      List(Set(q, Rt(Subtract(Sqr(b), Times(C4, a, c)), C2))), Subtract(
                          Dist(Times(C2, c, Power(q, CN1)),
                              Integrate(
                                  Power(Plus(b, Negate(q),
                                      Times(C2, c, Power(Cos(Plus(d, Times(e, x))), n))), CN1),
                                  x),
                              x),
                          Dist(Times(C2, c, Power(q, CN1)),
                              Integrate(Power(
                                  Plus(b, q, Times(C2, c, Power(Cos(Plus(d, Times(e, x))), n))),
                                  CN1), x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e, n), x), EqQ($s("n2"), Times(C2,
                      n)), NeQ(Subtract(Sqr(b), Times(C4, a, c)),
                          C0)))),
          IIntegrate(3250,
              Integrate(
                  Times(Power($($s("§sin"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))), m_DEFAULT),
                      Power(
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT,
                                  Power($($s("§sin"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))),
                                      n_DEFAULT)),
                              Times(c_DEFAULT,
                                  Power(
                                      $($s("§sin"), Plus(d_DEFAULT,
                                          Times(e_DEFAULT, x_))),
                                      $p("n2", true)))),
                          p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Power(Times(Power(C4, p),
                          Power(c, p)), CN1),
                      Integrate(
                          Times(Power(Sin(Plus(d, Times(e, x))), m),
                              Power(
                                  Plus(b, Times(C2, c,
                                      Power(Sin(Plus(d, Times(e, x))), n))),
                                  Times(C2, p))),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, m, n), x), EqQ($s("n2"), Times(C2,
                      n)), EqQ(Subtract(Sqr(b), Times(C4, a, c)),
                          C0),
                      IntegerQ(p)))),
          IIntegrate(3251,
              Integrate(
                  Times(
                      Power($($s("§cos"),
                          Plus(d_DEFAULT, Times(e_DEFAULT, x_))), m_DEFAULT),
                      Power(
                          Plus(a_DEFAULT,
                              Times(
                                  Power($($s("§cos"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))),
                                      n_DEFAULT),
                                  b_DEFAULT),
                              Times(
                                  Power(
                                      $($s("§cos"), Plus(d_DEFAULT,
                                          Times(e_DEFAULT, x_))),
                                      $p("n2", true)),
                                  c_DEFAULT)),
                          p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Power(Times(Power(C4, p),
                          Power(c, p)), CN1),
                      Integrate(
                          Times(Power(Cos(Plus(d, Times(e, x))), m),
                              Power(
                                  Plus(b, Times(C2, c,
                                      Power(Cos(Plus(d, Times(e, x))), n))),
                                  Times(C2, p))),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, m, n), x), EqQ($s("n2"), Times(C2, n)),
                      EqQ(Subtract(Sqr(b), Times(C4, a, c)), C0), IntegerQ(p)))),
          IIntegrate(3252,
              Integrate(
                  Times(
                      Power($($s("§sin"),
                          Plus(d_DEFAULT, Times(e_DEFAULT, x_))), m_DEFAULT),
                      Power(
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT,
                                  Power(
                                      $($s("§sin"), Plus(d_DEFAULT,
                                          Times(e_DEFAULT, x_))),
                                      n_DEFAULT)),
                              Times(c_DEFAULT, Power(
                                  $($s("§sin"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))),
                                  $p("n2", true)))),
                          p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(
                              Plus(
                                  a, Times(b, Power(Sin(
                                      Plus(d, Times(e, x))), n)),
                                  Times(c, Power(Sin(Plus(d, Times(e, x))), Times(C2, n)))),
                              p),
                          Power(
                              Power(Plus(b, Times(C2, c, Power(Sin(Plus(d, Times(e, x))), n))),
                                  Times(C2, p)),
                              CN1)),
                      Integrate(Times(Power(Sin(Plus(d, Times(e, x))), m),
                          Power(Plus(b, Times(C2, c, Power(Sin(Plus(d, Times(e, x))), n))),
                              Times(C2, p))),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, m, n, p), x), EqQ($s("n2"), Times(C2,
                      n)), EqQ(Subtract(Sqr(b), Times(C4, a, c)),
                          C0),
                      Not(IntegerQ(p))))),
          IIntegrate(3253,
              Integrate(
                  Times(
                      Power($($s("§cos"),
                          Plus(d_DEFAULT, Times(e_DEFAULT, x_))), m_DEFAULT),
                      Power(Plus(a_DEFAULT,
                          Times(Power($($s("§cos"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))),
                              n_DEFAULT), b_DEFAULT),
                          Times(
                              Power(
                                  $($s("§cos"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))), $p("n2",
                                      true)),
                              c_DEFAULT)),
                          p_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(
                              Plus(
                                  a, Times(b, Power(Cos(
                                      Plus(d, Times(e, x))), n)),
                                  Times(c, Power(Cos(Plus(d, Times(e, x))), Times(C2, n)))),
                              p),
                          Power(
                              Power(Plus(b, Times(C2, c, Power(Cos(Plus(d, Times(e, x))), n))),
                                  Times(C2, p)),
                              CN1)),
                      Integrate(
                          Times(Power(Cos(Plus(d, Times(e, x))), m),
                              Power(
                                  Plus(b, Times(C2, c, Power(Cos(Plus(d, Times(e, x))), n))), Times(
                                      C2, p))),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, m, n, p), x), EqQ($s("n2"), Times(C2,
                      n)), EqQ(Subtract(Sqr(b), Times(C4, a, c)),
                          C0),
                      Not(IntegerQ(p))))),
          IIntegrate(3254,
              Integrate(
                  Times(
                      Power($($s("§sin"),
                          Plus(d_DEFAULT, Times(e_DEFAULT, x_))), m_),
                      Power(
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT, Power(
                                  $($s("§sin"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))), n_)),
                              Times(c_DEFAULT,
                                  Power(
                                      $($s("§sin"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))), $p(
                                          "n2")))),
                          p_)),
                  x_Symbol),
              Condition(
                  Module(
                      List(Set(f,
                          FreeFactors(Cot(Plus(d, Times(e, x))), x))),
                      Negate(Dist(Times(f, Power(e, CN1)), Subst(
                          Integrate(Times(
                              Power(ExpandToSum(
                                  Plus(c, Times(b, Power(Plus(C1, Sqr(x)), Times(C1D2, n))),
                                      Times(a, Power(Plus(C1, Sqr(x)), n))),
                                  x), p),
                              Power(Power(Plus(C1, Times(Sqr(f), Sqr(x))),
                                  Plus(Times(C1D2, m), Times(n, p), C1)), CN1)),
                              x),
                          x, Times(Cot(Plus(d, Times(e, x))), Power(f, CN1))), x))),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ($s("n2"), Times(C2, n)),
                      IntegerQ(Times(C1D2, m)), NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      IntegerQ(Times(C1D2, n)), IntegerQ(p)))),
          IIntegrate(3255,
              Integrate(
                  Times(Power($($s("§cos"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))), m_DEFAULT),
                      Power(
                          Plus(a_DEFAULT,
                              Times(Power($($s("§cos"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))), n_),
                                  b_DEFAULT),
                              Times(Power(
                                  $($s("§cos"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))), $p("n2")),
                                  c_DEFAULT)),
                          p_)),
                  x_Symbol),
              Condition(
                  Module(List(Set(f, FreeFactors(Tan(Plus(d, Times(e, x))), x))),
                      Dist(Times(f, Power(e, CN1)),
                          Subst(
                              Integrate(
                                  Times(
                                      Power(ExpandToSum(
                                          Plus(c, Times(b, Power(Plus(C1, Sqr(x)), Times(C1D2, n))),
                                              Times(a, Power(Plus(C1, Sqr(x)), n))),
                                          x), p),
                                      Power(Power(Plus(C1, Times(Sqr(f), Sqr(x))),
                                          Plus(Times(C1D2, m), Times(n, p), C1)), CN1)),
                                  x),
                              x, Times(Tan(Plus(d, Times(e, x))), Power(f, CN1))),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ($s("n2"), Times(C2, n)),
                      IntegerQ(Times(C1D2, m)), NeQ(Subtract(Sqr(b), Times(C4, a,
                          c)), C0),
                      IntegerQ(Times(C1D2, n)), IntegerQ(p)))),
          IIntegrate(3256,
              Integrate(
                  Times(
                      Power($($s("§sin"),
                          Plus(d_DEFAULT, Times(e_DEFAULT, x_))), m_DEFAULT),
                      Power(
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT,
                                  Power($($s("§sin"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))),
                                      n_DEFAULT)),
                              Times(c_DEFAULT,
                                  Power(
                                      $($s("§sin"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))), $p("n2",
                                          true)))),
                          p_)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandTrig(
                          Times(
                              Power($($s("§sin"),
                                  Plus(d, Times(e, x))), m),
                              Power(
                                  Plus(a, Times(b, Power($($s("§sin"), Plus(d, Times(e, x))), n)),
                                      Times(c,
                                          Power($($s("§sin"), Plus(d, Times(e, x))),
                                              Times(C2, n)))),
                                  p)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ($s("n2"), Times(C2, n)), NeQ(Subtract(
                      Sqr(b), Times(C4, a, c)), C0), IntegersQ(m, n,
                          p)))),
          IIntegrate(3257,
              Integrate(
                  Times(
                      Power($($s("§cos"),
                          Plus(d_DEFAULT, Times(e_DEFAULT, x_))), m_DEFAULT),
                      Power(
                          Plus(a_DEFAULT,
                              Times(
                                  Power(
                                      $($s("§cos"), Plus(d_DEFAULT,
                                          Times(e_DEFAULT, x_))),
                                      n_DEFAULT),
                                  b_DEFAULT),
                              Times(
                                  Power(
                                      $($s("§cos"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))), $p("n2",
                                          true)),
                                  c_DEFAULT)),
                          p_)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandTrig(
                          Times(Power($($s("§cos"), Plus(d, Times(e, x))), m),
                              Power(Plus(a, Times(b, Power($($s("§cos"), Plus(d, Times(e, x))), n)),
                                  Times(c, Power($($s("§cos"), Plus(d, Times(e, x))),
                                      Times(C2, n)))),
                                  p)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e), x), EqQ($s("n2"), Times(C2, n)),
                      NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0), IntegersQ(m, n, p)))),
          IIntegrate(3258,
              Integrate(
                  Times(Power($($s("§cos"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))), m_DEFAULT),
                      Power(
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT,
                                  Power(
                                      Times(f_DEFAULT,
                                          $($s("§sin"), Plus(d_DEFAULT, Times(e_DEFAULT, x_)))),
                                      n_DEFAULT)),
                              Times(c_DEFAULT,
                                  Power(Times(f_DEFAULT, $($s("§sin"),
                                      Plus(d_DEFAULT, Times(e_DEFAULT, x_)))), $p("n2",
                                          true)))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Module(
                      List(Set(
                          g, FreeFactors(Sin(Plus(d, Times(e, x))), x))),
                      Dist(Times(g, Power(e, CN1)),
                          Subst(
                              Integrate(Times(
                                  Power(Subtract(C1, Times(Sqr(g), Sqr(x))),
                                      Times(C1D2, Subtract(m, C1))),
                                  Power(Plus(a, Times(b, Power(Times(f, g, x), n)),
                                      Times(c, Power(Times(f, g, x), Times(C2, n)))), p)),
                                  x),
                              x, Times(Sin(Plus(d, Times(e, x))), Power(g, CN1))),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, n,
                      p), x), EqQ($s("n2"),
                          Times(C2, n)),
                      IntegerQ(Times(C1D2, Subtract(m, C1)))))),
          IIntegrate(3259,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT,
                                  Power(
                                      Times($($s("§cos"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))),
                                          f_DEFAULT),
                                      n_DEFAULT)),
                              Times(c_DEFAULT,
                                  Power(
                                      Times(
                                          $($s("§cos"), Plus(d_DEFAULT,
                                              Times(e_DEFAULT, x_))),
                                          f_DEFAULT),
                                      $p("n2", true)))),
                          p_DEFAULT),
                      Power($($s("§sin"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Module(
                      List(Set(g,
                          FreeFactors(Cos(Plus(d, Times(e, x))), x))),
                      Negate(
                          Dist(
                              Times(g, Power(e, CN1)), Subst(
                                  Integrate(Times(
                                      Power(
                                          Subtract(C1, Times(Sqr(g), Sqr(x))),
                                          Times(C1D2, Subtract(m, C1))),
                                      Power(Plus(a, Times(b, Power(Times(f, g, x), n)),
                                          Times(c, Power(Times(f, g, x), Times(C2, n)))), p)),
                                      x),
                                  x, Times(Cos(Plus(d, Times(e, x))), Power(g, CN1))),
                              x))),
                  And(FreeQ(List(a, b, c, d, e, f, n,
                      p), x), EqQ($s("n2"),
                          Times(C2, n)),
                      IntegerQ(Times(C1D2, Subtract(m, C1)))))),
          IIntegrate(3260,
              Integrate(
                  Times(Power($($s("§cos"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))), m_),
                      Power(
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT,
                                  Power($($s("§sin"), Plus(d_DEFAULT, Times(e_DEFAULT, x_))),
                                      n_DEFAULT)),
                              Times(c_DEFAULT,
                                  Power(
                                      $($s("§sin"), Plus(d_DEFAULT,
                                          Times(e_DEFAULT, x_))),
                                      $p("n2", true)))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(Times(Power(C4, p), Power(c, p)), CN1),
                      Integrate(Times(Power(Cos(Plus(d, Times(e, x))), m),
                          Power(Plus(b, Times(C2, c, Power(Sin(Plus(d, Times(e, x))), n))),
                              Times(C2, p))),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, m, n), x), EqQ($s("n2"), Times(C2, n)),
                      Not(IntegerQ(Times(C1D2, Subtract(m, C1)))),
                      EqQ(Subtract(Sqr(b), Times(C4, a, c)), C0), IntegerQ(p)))));
}
