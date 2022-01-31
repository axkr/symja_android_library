package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;

import org.matheclipse.core.interfaces.IAST;

/**
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class UtilityFunctions31 {
  public static IAST RULES = List(
      ISetDelayed(552,
          FixInertTrigFunction(Times(Power($($s("§csc"), v_), n_DEFAULT), u_DEFAULT, w_), x_),
          Condition(Times(Power($($s("§sin"), v), Negate(n)), FixInertTrigFunction(Times(u, w), x)),
              And(PowerOfInertTrigSumQ(w, $s("§sin"), x), IntegerQ(n)))),
      ISetDelayed(553,
          FixInertTrigFunction(Times(Power($($s("§sec"), v_), n_DEFAULT), u_DEFAULT, w_), x_),
          Condition(Times(Power($($s("§cos"), v), Negate(n)), FixInertTrigFunction(Times(u, w), x)),
              And(PowerOfInertTrigSumQ(w, $s("§sin"), x), IntegerQ(n)))),
      ISetDelayed(554,
          FixInertTrigFunction(Times(Power($($s("§csc"), v_), n_DEFAULT), u_DEFAULT, w_), x_),
          Condition(Times(Power($($s("§sin"), v), Negate(n)), FixInertTrigFunction(Times(u, w), x)),
              And(PowerOfInertTrigSumQ(w, $s("§cos"), x), IntegerQ(n)))),
      ISetDelayed(555,
          FixInertTrigFunction(Times(Power($($s("§cot"), v_), n_DEFAULT), u_DEFAULT, w_), x_),
          Condition(Times(Power($($s("§tan"), v), Negate(n)), FixInertTrigFunction(Times(u, w), x)),
              And(PowerOfInertTrigSumQ(w, $s("§tan"), x), IntegerQ(n)))),
      ISetDelayed(556,
          FixInertTrigFunction(Times(Power($($s("§cos"), v_), n_DEFAULT), u_DEFAULT, w_), x_),
          Condition(Times(Power($($s("§sec"), v), Negate(n)), FixInertTrigFunction(Times(u, w), x)),
              And(PowerOfInertTrigSumQ(w, $s("§tan"), x), IntegerQ(n)))),
      ISetDelayed(557, FixInertTrigFunction(Times(Power($($s("§cos"), v_), n_), u_DEFAULT, w_), x_),
          Condition(Times(Power($($s("§sec"), v), Negate(n)), FixInertTrigFunction(Times(u, w), x)),
              And(PowerOfInertTrigSumQ(w, $s("§tan"), x), IntegerQ(n)))),
      ISetDelayed(558,
          FixInertTrigFunction(Times(Power($($s("§csc"), v_), n_DEFAULT), u_DEFAULT, w_), x_),
          Condition(Times(Power($($s("§sin"), v), Negate(n)), FixInertTrigFunction(Times(u, w), x)),
              And(PowerOfInertTrigSumQ(w, $s("§tan"), x), IntegerQ(n)))),
      ISetDelayed(559,
          FixInertTrigFunction(Times(Power($($s("§tan"), v_), n_DEFAULT), u_DEFAULT, w_), x_),
          Condition(Times(Power($($s("§cot"), v), Negate(n)), FixInertTrigFunction(Times(u, w), x)),
              And(PowerOfInertTrigSumQ(w, $s("§cot"), x), IntegerQ(n)))),
      ISetDelayed(560,
          FixInertTrigFunction(Times(Power($($s("§sin"), v_), n_DEFAULT), u_DEFAULT, w_), x_),
          Condition(Times(Power($($s("§csc"), v), Negate(n)), FixInertTrigFunction(Times(u, w), x)),
              And(PowerOfInertTrigSumQ(w, $s("§cot"), x), IntegerQ(n)))),
      ISetDelayed(561,
          FixInertTrigFunction(Times(Power($($s("§sin"), v_), n_DEFAULT), u_DEFAULT, w_), x_),
          Condition(Times(Power($($s("§csc"), v), Negate(n)), FixInertTrigFunction(Times(u, w), x)),
              And(PowerOfInertTrigSumQ(w, $s("§cot"), x), IntegerQ(n)))),
      ISetDelayed(562,
          FixInertTrigFunction(Times(Power($($s("§sec"), v_), n_DEFAULT), u_DEFAULT, w_), x_),
          Condition(Times(Power($($s("§cos"), v), Negate(n)), FixInertTrigFunction(Times(u, w), x)),
              And(PowerOfInertTrigSumQ(w, $s("§cot"), x), IntegerQ(n)))),
      ISetDelayed(563,
          FixInertTrigFunction(Times(Power($($s("§cos"), v_), n_DEFAULT), u_DEFAULT, w_), x_),
          Condition(Times(Power($($s("§sec"), v), Negate(n)), FixInertTrigFunction(Times(u, w), x)),
              And(PowerOfInertTrigSumQ(w, $s("§sec"), x), IntegerQ(n)))),
      ISetDelayed(564,
          FixInertTrigFunction(Times(Power($($s("§cot"), v_), n_DEFAULT), u_DEFAULT, w_), x_),
          Condition(Times(Power($($s("§tan"), v), Negate(n)), FixInertTrigFunction(Times(u, w), x)),
              And(PowerOfInertTrigSumQ(w, $s("§sec"), x), IntegerQ(n)))),
      ISetDelayed(565,
          FixInertTrigFunction(Times(Power($($s("§csc"), v_), n_DEFAULT), u_DEFAULT, w_), x_),
          Condition(Times(Power($($s("§sin"), v), Negate(n)), FixInertTrigFunction(Times(u, w), x)),
              And(PowerOfInertTrigSumQ(w, $s("§sec"), x), IntegerQ(n)))),
      ISetDelayed(566,
          FixInertTrigFunction(Times(Power($($s("§sin"), v_), n_DEFAULT), u_DEFAULT, w_), x_),
          Condition(Times(Power($($s("§csc"), v), Negate(n)), FixInertTrigFunction(Times(u, w), x)),
              And(PowerOfInertTrigSumQ(w, $s("§csc"), x), IntegerQ(n)))),
      ISetDelayed(567,
          FixInertTrigFunction(Times(Power($($s("§tan"), v_), n_DEFAULT), u_DEFAULT,
              w_), x_),
          Condition(
              Times(Power($($s("§cot"),
                  v), Negate(n)), FixInertTrigFunction(Times(u, w),
                      x)),
              And(PowerOfInertTrigSumQ(w, $s("§csc"), x), IntegerQ(n)))),
      ISetDelayed(568,
          FixInertTrigFunction(Times(Power($($s("§sec"), v_), n_DEFAULT), u_DEFAULT, w_), x_),
          Condition(Times(Power($($s("§cos"), v), Negate(n)), FixInertTrigFunction(Times(u, w), x)),
              And(PowerOfInertTrigSumQ(w, $s("§csc"), x), IntegerQ(n)))),
      ISetDelayed(569, FixInertTrigFunction(Times(Power($($s("§tan"), v_), m_DEFAULT),
          Power(Plus(Times($($s("§sin"), v_), a_DEFAULT), Times($($s("§cos"), v_), b_DEFAULT)),
              n_DEFAULT),
          u_DEFAULT), x_),
          Condition(
              Times(Power($($s("§sin"), v), m), Power($($s("§cos"), v), Negate(m)),
                  FixInertTrigFunction(
                      Times(u,
                          Power(Plus(Times(a, $($s("§sin"), v)), Times(b, $($s("§cos"), v))), n)),
                      x)),
              And(FreeQ(List(a, b, n), x), IntegerQ(m)))),
      ISetDelayed(570,
          FixInertTrigFunction(
              Times(
                  Power($($s("§cot"),
                      v_), m_DEFAULT),
                  Power(
                      Plus(Times($($s("§sin"), v_), a_DEFAULT), Times($($s("§cos"), v_),
                          b_DEFAULT)),
                      n_DEFAULT),
                  u_DEFAULT),
              x_),
          Condition(
              Times(
                  Power($($s("§cos"), v), m), Power($($s("§sin"),
                      v), Negate(m)),
                  FixInertTrigFunction(
                      Times(
                          u, Power(Plus(Times(a, $($s("§sin"), v)), Times(b, $($s("§cos"), v))),
                              n)),
                      x)),
              And(FreeQ(List(a, b, n), x), IntegerQ(m)))),
      ISetDelayed(571,
          FixInertTrigFunction(Times(Power($($s("§sec"), v_), m_DEFAULT),
              Power(Plus(Times($($s("§sin"), v_), a_DEFAULT), Times($($s("§cos"), v_), b_DEFAULT)),
                  n_DEFAULT),
              u_DEFAULT), x_),
          Condition(Times(Power($($s("§cos"), v), Negate(m)), FixInertTrigFunction(
              Times(u, Power(Plus(Times(a, $($s("§sin"), v)), Times(b, $($s("§cos"), v))), n)), x)),
              And(FreeQ(List(a, b, n), x), IntegerQ(m)))));
}
