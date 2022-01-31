package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.ISetDelayed;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_DEFAULT;
import static org.matheclipse.core.expression.F.u_;
import static org.matheclipse.core.expression.F.u_DEFAULT;
import static org.matheclipse.core.expression.F.v_;
import static org.matheclipse.core.expression.F.w_;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.v;
import static org.matheclipse.core.expression.S.w;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FixInertTrigFunction;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntegersQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PowerOfInertTrigSumQ;
import org.matheclipse.core.interfaces.IAST;

/**
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class UtilityFunctions30 {
  public static IAST RULES = List(
      ISetDelayed(532,
          FixInertTrigFunction(Times(Power($($s("§tan"), v_), m_DEFAULT),
              Power(Times($($s("§cos"), w_), c_DEFAULT), n_DEFAULT)), x_),
          Condition(Times(Power($($s("§cot"), v), Negate(m)), Power(Times(c, $($s("§cos"), w)), n)),
              And(FreeQ(List(c, n), x), IntegerQ(m)))),
      ISetDelayed(533,
          FixInertTrigFunction(Times(Power($($s("§csc"), v_), m_DEFAULT),
              Power(Times($($s("§tan"), w_), c_DEFAULT), n_DEFAULT)), x_),
          Condition(Times(Power($($s("§sin"), v), Negate(m)), Power(Times(c, $($s("§tan"), w)), n)),
              And(FreeQ(List(c, n), x), IntegerQ(m)))),
      ISetDelayed(534,
          FixInertTrigFunction(Times(Power($($s("§sec"), v_), m_DEFAULT),
              Power(Times($($s("§cot"), w_), c_DEFAULT), n_DEFAULT)), x_),
          Condition(Times(Power($($s("§cos"), v), Negate(m)), Power(Times(c, $($s("§cot"), w)), n)),
              And(FreeQ(List(c, n), x), IntegerQ(m)))),
      ISetDelayed(535,
          FixInertTrigFunction(
              Times(Power($($s("§cot"), v_), m_DEFAULT),
                  Power(Times($($s("§sec"), w_), c_DEFAULT), n_DEFAULT)),
              x_),
          Condition(Times(Power($($s("§tan"), v), Negate(m)), Power(Times(c, $($s("§sec"), w)), n)),
              And(FreeQ(List(c, n), x), IntegerQ(m)))),
      ISetDelayed(536,
          FixInertTrigFunction(
              Times(Power($($s("§tan"), v_), m_DEFAULT),
                  Power(Times($($s("§csc"), w_), c_DEFAULT), n_DEFAULT)),
              x_),
          Condition(
              Times(Power($($s("§cot"), v), Negate(m)), Power(Times(c, $($s("§csc"), w)),
                  n)),
              And(FreeQ(List(c, n), x), IntegerQ(m)))),
      ISetDelayed(537,
          FixInertTrigFunction(Times(Power($($s("§sec"), v_), m_DEFAULT),
              Power($($s("§sec"), w_), n_DEFAULT)), x_),
          Condition(
              Times(Power($($s("§cos"),
                  v), Negate(m)), Power($($s("§cos"), w),
                      Negate(n))),
              IntegersQ(m, n))),
      ISetDelayed(538,
          FixInertTrigFunction(Times(Power($($s("§csc"), v_), m_DEFAULT),
              Power($($s("§csc"), w_), n_DEFAULT)), x_),
          Condition(
              Times(Power($($s("§sin"),
                  v), Negate(m)), Power($($s("§sin"), w),
                      Negate(n))),
              IntegersQ(m, n))),
      ISetDelayed(539,
          FixInertTrigFunction(
              Times(
                  Power($($s("§tan"),
                      v_), m_DEFAULT),
                  Power(Plus(a_, Times($($s("§sin"), w_), b_DEFAULT)), n_DEFAULT), u_),
              x_),
          Condition(
              Times(
                  Power($($s("§sin"), v), m), Power(Power($($s("§cos"), v),
                      m), CN1),
                  FixInertTrigFunction(Times(u, Power(Plus(a, Times(b, $($s("§sin"), w))), n)), x)),
              And(FreeQ(List(a, b, n), x), IntegerQ(m)))),
      ISetDelayed(540,
          FixInertTrigFunction(
              Times(
                  Power($($s("§cot"),
                      v_), m_DEFAULT),
                  Power(Plus(a_, Times($($s("§sin"), w_), b_DEFAULT)), n_DEFAULT), u_),
              x_),
          Condition(
              Times(
                  Power($($s("§cos"), v), m), Power(Power($($s("§sin"), v),
                      m), CN1),
                  FixInertTrigFunction(Times(u, Power(Plus(a, Times(b, $($s("§sin"), w))), n)), x)),
              And(FreeQ(List(a, b, n), x), IntegerQ(m)))),
      ISetDelayed(541,
          FixInertTrigFunction(
              Times(
                  Power($($s("§tan"),
                      v_), m_DEFAULT),
                  Power(Plus(a_, Times($($s("§cos"), w_), b_DEFAULT)), n_DEFAULT), u_),
              x_),
          Condition(
              Times(Power($($s("§sin"), v), m), Power(Power($($s("§cos"), v),
                  m), CN1), FixInertTrigFunction(
                      Times(u, Power(Plus(a, Times(b, $($s("§cos"), w))), n)), x)),
              And(FreeQ(List(a, b, n), x), IntegerQ(m)))),
      ISetDelayed(542,
          FixInertTrigFunction(
              Times(
                  Power($($s("§cot"),
                      v_), m_DEFAULT),
                  Power(Plus(a_, Times($($s("§cos"), w_), b_DEFAULT)), n_DEFAULT), u_),
              x_),
          Condition(
              Times(Power($($s("§cos"), v), m), Power(Power($($s("§sin"), v),
                  m), CN1), FixInertTrigFunction(
                      Times(u, Power(Plus(a, Times(b, $($s("§cos"), w))), n)), x)),
              And(FreeQ(List(a, b, n), x), IntegerQ(m)))),
      ISetDelayed(543,
          FixInertTrigFunction(
              Times(
                  Power($($s("§cot"),
                      v_), m_DEFAULT),
                  Power(
                      Plus(
                          a_DEFAULT, Times(b_DEFAULT, Power(Times($($s("§sin"), w_), c_DEFAULT),
                              p_DEFAULT))),
                      n_DEFAULT)),
              x_),
          Condition(
              Times(
                  Power($($s("§tan"), v), Negate(m)), Power(
                      Plus(a, Times(b, Power(Times(c, $($s("§sin"), w)), p))), n)),
              And(FreeQ(List(a, b, c, n, p), x), IntegerQ(m)))),
      ISetDelayed(544,
          FixInertTrigFunction(
              Times(Power($($s("§tan"), v_), m_DEFAULT),
                  Power(
                      Plus(a_DEFAULT,
                          Times(b_DEFAULT, Power(Times($($s("§cos"), w_), c_DEFAULT), p_DEFAULT))),
                      n_DEFAULT)),
              x_),
          Condition(
              Times(Power($($s("§cot"), v), Negate(m)),
                  Power(Plus(a, Times(b, Power(Times(c, $($s("§cos"), w)), p))), n)),
              And(FreeQ(List(a, b, c, n, p), x), IntegerQ(m)))),
      ISetDelayed(545,
          FixInertTrigFunction(
              Times(Power(Times(Power($($s("§sin"), v_), n_DEFAULT), c_DEFAULT), p_DEFAULT),
                  u_DEFAULT, w_),
              x_),
          Condition(
              Times(Power(Times(c, Power($($s("§sin"), v), n)),
                  p), FixInertTrigFunction(Times(u, w), x)),
              And(FreeQ(List(c, p), x), PowerOfInertTrigSumQ(w, $s("§sin"), x)))),
      ISetDelayed(546,
          FixInertTrigFunction(
              Times(Power(Times(Power($($s("§cos"), v_), n_DEFAULT), c_DEFAULT), p_DEFAULT),
                  u_DEFAULT, w_),
              x_),
          Condition(
              Times(Power(Times(c, Power($($s("§cos"), v), n)), p),
                  FixInertTrigFunction(Times(u, w), x)),
              And(FreeQ(List(c, p), x), PowerOfInertTrigSumQ(w, $s("§cos"), x)))),
      ISetDelayed(547,
          FixInertTrigFunction(
              Times(Power(Times(Power($($s("§tan"), v_), n_DEFAULT), c_DEFAULT), p_DEFAULT),
                  u_DEFAULT, w_),
              x_),
          Condition(
              Times(Power(Times(c, Power($($s("§tan"), v), n)), p),
                  FixInertTrigFunction(Times(u, w), x)),
              And(FreeQ(List(c, p), x), PowerOfInertTrigSumQ(w, $s("§tan"), x)))),
      ISetDelayed(548,
          FixInertTrigFunction(
              Times(Power(Times(Power($($s("§cot"), v_), n_DEFAULT), c_DEFAULT), p_DEFAULT),
                  u_DEFAULT, w_),
              x_),
          Condition(
              Times(Power(Times(c, Power($($s("§cot"), v), n)),
                  p), FixInertTrigFunction(Times(u, w), x)),
              And(FreeQ(List(c, p), x), PowerOfInertTrigSumQ(w, $s("§cot"), x)))),
      ISetDelayed(549,
          FixInertTrigFunction(
              Times(Power(Times(Power($($s("§sec"), v_), n_DEFAULT), c_DEFAULT), p_DEFAULT),
                  u_DEFAULT, w_),
              x_),
          Condition(
              Times(Power(Times(c, Power($($s("§sec"), v), n)), p),
                  FixInertTrigFunction(Times(u, w), x)),
              And(FreeQ(List(c, p), x), PowerOfInertTrigSumQ(w, $s("§sec"), x)))),
      ISetDelayed(550,
          FixInertTrigFunction(
              Times(Power(Times(Power($($s("§csc"), v_), n_DEFAULT), c_DEFAULT), p_DEFAULT),
                  u_DEFAULT, w_),
              x_),
          Condition(
              Times(Power(Times(c, Power($($s("§csc"), v), n)), p),
                  FixInertTrigFunction(Times(u, w), x)),
              And(FreeQ(List(c, p), x), PowerOfInertTrigSumQ(w, $s("§csc"), x)))),
      ISetDelayed(551,
          FixInertTrigFunction(Times(Power($($s("§sec"), v_), n_DEFAULT), u_DEFAULT, w_), x_),
          Condition(Times(Power($($s("§cos"), v), Negate(n)), FixInertTrigFunction(Times(u, w), x)),
              And(PowerOfInertTrigSumQ(w, $s("§cos"), x), IntegerQ(n)))));
}
