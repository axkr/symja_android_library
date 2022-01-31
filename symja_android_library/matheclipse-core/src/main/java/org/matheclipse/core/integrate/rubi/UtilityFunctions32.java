package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;

import org.matheclipse.core.interfaces.IAST;

/**
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 * 
 */
class UtilityFunctions32 {
  public static IAST RULES = List(
      ISetDelayed(572,
          FixInertTrigFunction(Times(
              Power($($s("§csc"), v_), m_DEFAULT),
              Power(Plus(Times($($s("§sin"), v_), a_DEFAULT), Times($($s("§cos"), v_), b_DEFAULT)),
                  n_DEFAULT),
              u_DEFAULT), x_),
          Condition(Times(Power($($s("§sin"), v), Negate(m)), FixInertTrigFunction(
              Times(u, Power(Plus(Times(a, $($s("§sin"), v)), Times(b, $($s("§cos"), v))), n)), x)),
              And(FreeQ(List(a, b, n), x), IntegerQ(m)))),
      ISetDelayed(573,
          FixInertTrigFunction(
              Times(Power($(f_, v_), m_DEFAULT),
                  Plus(A_DEFAULT, Times($(g_, v_), B_DEFAULT), Times(Sqr($(g_, v_)), C_DEFAULT))),
              x_),
          Condition(
              Times(Power($(g, v), Negate(m)),
                  Plus(ASymbol, Times(BSymbol, $(g, v)), Times(CSymbol, Sqr($(g, v))))),
              And(FreeQ(List(ASymbol, BSymbol, CSymbol), x), IntegerQ(m),
                  Or(InertReciprocalQ(f, g), InertReciprocalQ(g, f))))),
      ISetDelayed(574,
          FixInertTrigFunction(
              Times(Power($(f_, v_), m_DEFAULT), Plus(A_DEFAULT, Times(Sqr($(g_, v_)), C_DEFAULT))),
              x_),
          Condition(Times(Power($(g, v), Negate(m)), Plus(ASymbol, Times(CSymbol, Sqr($(g, v))))),
              And(FreeQ(List(ASymbol, CSymbol), x), IntegerQ(m),
                  Or(InertReciprocalQ(f, g), InertReciprocalQ(g, f))))),
      ISetDelayed(575,
          FixInertTrigFunction(
              Times(Power($(f_, v_), m_DEFAULT),
                  Power(Plus(a_DEFAULT, Times($(g_, v_), b_DEFAULT)), n_DEFAULT),
                  Plus(A_DEFAULT, Times($(g_, v_), B_DEFAULT), Times(Sqr($(g_, v_)), C_DEFAULT))),
              x_),
          Condition(
              Times(Power($(g, v), Negate(m)),
                  Plus(ASymbol, Times(BSymbol, $(g, v)), Times(CSymbol, Sqr($(g, v)))),
                  Power(Plus(a, Times(b, $(g, v))), n)),
              And(FreeQ(List(a, b, ASymbol, BSymbol, CSymbol, n), x), IntegerQ(m),
                  Or(InertReciprocalQ(f, g), InertReciprocalQ(g, f))))),
      ISetDelayed(576,
          FixInertTrigFunction(
              Times(Power($(f_, v_), m_DEFAULT),
                  Power(Plus(a_DEFAULT, Times($(g_, v_), b_DEFAULT)), n_DEFAULT), Plus(A_DEFAULT,
                      Times(Sqr($(g_, v_)), C_DEFAULT))),
              x_),
          Condition(Times(Power($(g, v), Negate(m)), Plus(ASymbol,
              Times(CSymbol, Sqr($(g, v)))), Power(Plus(a, Times(b, $(g, v))), n)), And(
                  FreeQ(List(a, b, ASymbol, CSymbol, n), x), IntegerQ(m), Or(InertReciprocalQ(f, g),
                      InertReciprocalQ(g, f))))),
      ISetDelayed(577, FixInertTrigFunction(u_,
          x_), u),
      ISetDelayed(
          578, PowerOfInertTrigSumQ(u_, $p(
              "func"), x_),
          Or(MatchQ(u,
              Condition(
                  Power(
                      Plus(a_DEFAULT,
                          Times(b_DEFAULT, Power(Times(c_DEFAULT, $($s("func"), w_)), n_DEFAULT))),
                      p_DEFAULT),
                  And(FreeQ(List(a, b, c, n, p), x),
                      Not(And(EqQ(a, C0), Or(IntegerQ(p), EqQ(n, C1))))))),
              MatchQ(u,
                  Condition(Power(
                      Plus(a_DEFAULT,
                          Times(b_DEFAULT, Power(Times(d_DEFAULT, $($s("func"), w_)), p_DEFAULT)),
                          Times(c_DEFAULT, Power(Times(d_DEFAULT, $($s("func"), w_)), q_DEFAULT))),
                      n_DEFAULT), FreeQ(List(a, b, c, d, n, p, q), x))))),
      ISetDelayed(579,
          ReduceInertTrig($p("func"),
              Plus(Times(m_DEFAULT, Plus(Times(Pi, n_DEFAULT), u_DEFAULT)), v_DEFAULT)),
          Condition(ReduceInertTrig($s("func"), Times(m, n), Plus(Times(m, u), v)),
              RationalQ(m, n))),
      ISetDelayed(580,
          ReduceInertTrig($p("func"),
              Plus(Times(Complex(C0, $p("mz")), m_DEFAULT,
                  Plus(Times(Pi, Complex(C0, $p("nz")), n_DEFAULT), u_DEFAULT)), v_DEFAULT)),
          Condition(
              ReduceInertTrig(
                  $s("func"), Times(CN1, m, $s("mz"), n, $s("nz")), Plus(Times(m, $s("mz"), CI, u),
                      v)),
              RationalQ(m, $s("mz"), n, $s("nz")))),
      ISetDelayed(581, ReduceInertTrig($p("func"), u_), $($s("func"), u)),
      ISetDelayed(582, ReduceInertTrig($p("func"), m_, u_), Condition(If(Less(m, C0), If(
          GreaterEqual(m, CN1D4), $($s("func"), Plus(Times(m, Pi), u)),
          Switch($s("func"), $s("§sin"), Negate(ReduceInertTrig($s("§sin"), Negate(m), Negate(u))),
              $s("§cos"), ReduceInertTrig($s("§cos"), Negate(m), Negate(u)), $s("§tan"),
              Negate(ReduceInertTrig($s("§tan"), Negate(m), Negate(u))), $s("§cot"),
              Negate(ReduceInertTrig($s("§cot"), Negate(m), Negate(u))), $s("§sec"),
              ReduceInertTrig($s("§sec"), Negate(m), Negate(u)), $s("§csc"),
              Negate(ReduceInertTrig($s("§csc"), Negate(m), Negate(u))))),
          If(GreaterEqual(m, C2), ReduceInertTrig($s("func"), Mod(m, C2), u),
              If(GreaterEqual(m, C1),
                  Switch($s("func"), $s("§sin"),
                      Negate(ReduceInertTrig($s("§sin"), Subtract(m, C1), u)), $s("§cos"),
                      Negate(ReduceInertTrig($s("§cos"), Subtract(m, C1), u)), $s("§tan"),
                      ReduceInertTrig($s("§tan"), Subtract(m, C1), u), $s("§cot"),
                      ReduceInertTrig($s("§cot"), Subtract(m, C1), u), $s("§sec"),
                      Negate(ReduceInertTrig($s("§sec"), Subtract(m, C1), u)), $s("§csc"),
                      Negate(ReduceInertTrig($s("§csc"), Subtract(m, C1), u))),
                  If(GreaterEqual(m, C1D2),
                      Switch($s("func"), $s("§sin"),
                          ReduceInertTrig($s("§cos"), Subtract(m, C1D2), u), $s("§cos"),
                          Negate(ReduceInertTrig($s("§sin"), Subtract(m, C1D2), u)), $s("§tan"),
                          Negate(ReduceInertTrig($s("§cot"), Subtract(m, C1D2), u)), $s("§cot"),
                          Negate(ReduceInertTrig($s("§tan"), Subtract(m, C1D2), u)), $s("§sec"),
                          Negate(ReduceInertTrig($s("§csc"), Subtract(m, C1D2), u)), $s(
                              "§csc"),
                          ReduceInertTrig($s("§sec"), Subtract(m, C1D2), u)),
                      $($s("func"), Plus(Times(m, Pi), u)))))),
          RationalQ(m))),
      ISetDelayed(
          583, UnifyInertTrigFunction(Times(a_,
              u_), x_),
          Condition(Times(a, UnifyInertTrigFunction(u, x)), FreeQ(a, x))),
      ISetDelayed(584,
          UnifyInertTrigFunction(
              Times(
                  Power(
                      Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                          a_DEFAULT),
                      m_DEFAULT),
                  Power(
                      Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                          b_DEFAULT),
                      n_DEFAULT)),
              x_),
          Condition(
              Times(Power(Times(a, $($s("§sin"), Plus(e, CPiHalf, Times(f, x)))), m),
                  Power(Times(CN1, b, $($s("§sec"), Plus(e, CPiHalf, Times(f, x)))), n)),
              FreeQ(List(a, b, e, f, m, n), x))),
      ISetDelayed(585,
          UnifyInertTrigFunction(Times(
              Power(Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), a_DEFAULT),
                  m_DEFAULT),
              Power(Times($($s("§sec"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), b_DEFAULT),
                  n_DEFAULT)),
              x_),
          Condition(
              Times(
                  Power(Times(a, $($s("§sin"), Plus(e, CPiHalf, Times(f, x)))), m),
                  Power(Times(b, $($s("§csc"), Plus(e, CPiHalf, Times(f, x)))), n)),
              FreeQ(List(a, b, e, f, m, n), x))),
      ISetDelayed(586,
          UnifyInertTrigFunction(
              Power(
                  Plus(
                      a_DEFAULT, Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                          b_DEFAULT)),
                  n_DEFAULT),
              x_),
          Condition(
              Power(Plus(a,
                  Times(b, $($s("§sin"), Plus(e, CPiHalf, Times(f, x))))), n),
              FreeQ(List(a, b, e, f, n), x))),
      ISetDelayed(587,
          UnifyInertTrigFunction(
              Times(
                  Power(
                      Plus(
                          a_, Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              b_DEFAULT)),
                      m_DEFAULT),
                  Power(Times($($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), g_DEFAULT),
                      p_DEFAULT)),
              x_),
          Condition(
              Times(
                  Power(Times(g,
                      $($s("§cos"), Plus(e, Times(CN1, C1D2, Pi), Times(f, x)))), p),
                  Power(
                      Subtract(a,
                          Times(b, $($s("§sin"), Plus(e, Times(CN1, C1D2, Pi), Times(f, x))))),
                      m)),
              FreeQ(List(a, b, e, f, g, m, p), x))),
      ISetDelayed(588,
          UnifyInertTrigFunction(
              Times(
                  Power(
                      Plus(
                          a_, Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              b_DEFAULT)),
                      m_DEFAULT),
                  Power(
                      Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                          g_DEFAULT),
                      p_DEFAULT)),
              x_),
          Condition(
              Times(
                  Power(Times(g,
                      $($s("§sec"), Plus(e, Times(CN1, C1D2, Pi), Times(f, x)))), p),
                  Power(
                      Subtract(a,
                          Times(b, $($s("§sin"), Plus(e, Times(CN1, C1D2, Pi), Times(f, x))))),
                      m)),
              FreeQ(List(a, b, e, f, g, m, p), x))),
      ISetDelayed(589,
          UnifyInertTrigFunction(
              Times(
                  Power(
                      Plus(
                          a_, Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              b_DEFAULT)),
                      m_DEFAULT),
                  Power(
                      Times($($s("§cot"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                          g_DEFAULT),
                      p_DEFAULT)),
              x_),
          Condition(
              If(True,
                  Times(
                      Power(
                          Times(CN1, g,
                              $($s("§tan"), Plus(e, Times(CN1, C1D2, Pi), Times(f, x)))),
                          p),
                      Power(
                          Subtract(a, Times(b, $($s("§sin"),
                              Plus(e, Times(CN1, C1D2, Pi), Times(f, x))))),
                          m)),
                  Times(
                      Power(Times(CN1, g, $($s("§tan"),
                          Plus(e, CPiHalf, Times(f, x)))), p),
                      Power(Plus(a, Times(b, $($s("§sin"), Plus(e, CPiHalf, Times(f, x))))), m))),
              FreeQ(List(a, b, e, f, g, m, p), x))),
      ISetDelayed(590, UnifyInertTrigFunction(Times(
          Power(Plus(a_, Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), b_DEFAULT)),
              m_DEFAULT),
          Power(Times($($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), g_DEFAULT), p_DEFAULT)),
          x_),
          Condition(
              Times(Power(Times(CN1, g, $($s("§cot"), Plus(e, CPiHalf, Times(f, x)))), p),
                  Power(Plus(a, Times(b, $($s("§sin"), Plus(e, CPiHalf, Times(f, x))))), m)),
              FreeQ(List(a, b, e, f, g, m, p), x))));
}
