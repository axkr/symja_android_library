package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;

import org.matheclipse.core.interfaces.IAST;

/**
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 * 
 */
class UtilityFunctions29 {
  public static IAST RULES =
      List(
          ISetDelayed(513, ActivateTrig(u_),
              ReplaceAll(u,
                  List(
                      Rule($s("§sin"), Sin), Rule($s("§cos"), Cos), Rule($s("§tan"), Tan), Rule($s(
                          "§cot"), Cot),
                      Rule($s("§sec"), Sec), Rule($s("§csc"), Csc)))),
          ISetDelayed(514,
              DeactivateTrig(
                  Times(Power(
                      Plus(a_DEFAULT, Times($($p("§trig"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                          b_DEFAULT)),
                      n_DEFAULT),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_DEFAULT)),
                  x_),
              Condition(
                  Times(Power(Plus(c, Times(d, x)), m),
                      Power(
                          Plus(a,
                              Times(b, DeactivateTrig($($s("§trig"), Plus(e, Times(f, x))), x))),
                          n)),
                  And(FreeQ(List(a, b, c, d, e, f, m,
                      n), x), Or(TrigQ($s("§trig")),
                          HyperbolicQ($s("§trig")))))),
          ISetDelayed(515, DeactivateTrig(u_, x_),
              UnifyInertTrigFunction(FixInertTrigFunction(DeactivateTrigAux(u, x), x), x)),
          ISetDelayed(516, DeactivateTrigAux(u_, x_),
              If(AtomQ(u), u,
                  If(And(TrigQ(u), LinearQ(Part(u, C1), x)),
                      With(List(Set(v, ExpandToSum(Part(u, C1), x))), Switch(Head(u), Sin,
                          ReduceInertTrig($s("§sin"), v), Cos, ReduceInertTrig($s("§cos"), v), Tan,
                          ReduceInertTrig($s("§tan"), v), Cot, ReduceInertTrig($s("§cot"), v), Sec,
                          ReduceInertTrig($s("§sec"), v), Csc, ReduceInertTrig($s("§csc"), v))),
                      If(And(HyperbolicQ(u), LinearQ(Part(u, C1), x)),
                          With(List(Set(v, ExpandToSum(Times(CI, Part(u, C1)), x))),
                              Switch(Head(u), Sinh, Times(CN1, CI, ReduceInertTrig($s("§sin"), v)),
                                  Cosh, ReduceInertTrig($s("§cos"), v), Tanh,
                                  Times(CN1, CI, ReduceInertTrig($s("§tan"), v)), Coth,
                                  Times(CI, ReduceInertTrig($s("§cot"), v)), Sech,
                                  ReduceInertTrig($s("§sec"), v), Csch,
                                  Times(CI, ReduceInertTrig($s("§csc"), v)))),
                          Map(Function(DeactivateTrigAux(Slot1, x)), u))))),
          ISetDelayed(517, FixInertTrigFunction(Times(a_, u_), x_),
              Condition(Times(a, FixInertTrigFunction(u, x)), FreeQ(a, x))),
          ISetDelayed(518,
              FixInertTrigFunction(Times(u_DEFAULT, Power(Times(a_, Plus(b_, v_)), n_)), x_),
              Condition(FixInertTrigFunction(Times(u, Power(Plus(Times(a, b), Times(a, v)), n)), x),
                  And(FreeQ(List(a, b, n), x), Not(FreeQ(v, x))))),
          ISetDelayed(519,
              FixInertTrigFunction(Times(Power($($s("§csc"), v_), m_DEFAULT),
                  Power(Times($($s("§sin"), w_), c_DEFAULT), n_DEFAULT)), x_),
              Condition(
                  Times(Power($($s("§sin"), v), Negate(m)), Power(Times(c, $($s("§sin"), w)), n)),
                  And(FreeQ(List(c, n), x), IntegerQ(m)))),
          ISetDelayed(520,
              FixInertTrigFunction(Times(Power($($s("§sec"), v_), m_DEFAULT),
                  Power(Times($($s("§cos"), w_), c_DEFAULT), n_DEFAULT)), x_),
              Condition(
                  Times(Power($($s("§cos"), v), Negate(m)), Power(Times(c, $($s("§cos"), w)), n)),
                  And(FreeQ(List(c, n), x), IntegerQ(m)))),
          ISetDelayed(521,
              FixInertTrigFunction(Times(Power($($s("§cot"), v_), m_DEFAULT),
                  Power(Times($($s("§tan"), w_), c_DEFAULT), n_DEFAULT)), x_),
              Condition(
                  Times(Power($($s("§tan"), v), Negate(m)), Power(Times(c, $($s("§tan"), w)), n)),
                  And(FreeQ(List(c, n), x), IntegerQ(m)))),
          ISetDelayed(522,
              FixInertTrigFunction(Times(Power($($s("§tan"), v_), m_DEFAULT),
                  Power(Times($($s("§cot"), w_), c_DEFAULT), n_DEFAULT)), x_),
              Condition(
                  Times(Power($($s("§cot"), v), Negate(m)), Power(Times(c, $($s("§cot"), w)), n)),
                  And(FreeQ(List(c, n), x), IntegerQ(m)))),
          ISetDelayed(523,
              FixInertTrigFunction(Times(Power($($s("§cos"), v_), m_DEFAULT),
                  Power(Times($($s("§sec"), w_), c_DEFAULT), n_DEFAULT)), x_),
              Condition(
                  Times(Power($($s("§sec"), v), Negate(m)), Power(Times(c, $($s("§sec"), w)), n)),
                  And(FreeQ(List(c, n), x), IntegerQ(m)))),
          ISetDelayed(524,
              FixInertTrigFunction(Times(Power($($s("§sin"), v_), m_DEFAULT),
                  Power(Times($($s("§csc"), w_), c_DEFAULT), n_DEFAULT)), x_),
              Condition(
                  Times(Power($($s("§csc"), v), Negate(m)), Power(Times(c, $($s("§csc"), w)), n)),
                  And(FreeQ(List(c, n), x), IntegerQ(m)))),
          ISetDelayed(525,
              FixInertTrigFunction(Times(Power($($s("§sec"), v_), m_DEFAULT),
                  Power(Times($($s("§sin"), w_), c_DEFAULT), n_DEFAULT)), x_),
              Condition(
                  Times(Power($($s("§cos"), v), Negate(m)), Power(Times(c, $($s("§sin"), w)), n)),
                  And(FreeQ(List(c, n), x), IntegerQ(m)))),
          ISetDelayed(526,
              FixInertTrigFunction(Times(Power($($s("§csc"), v_), m_DEFAULT),
                  Power(Times($($s("§cos"), w_), c_DEFAULT), n_DEFAULT)), x_),
              Condition(
                  Times(Power($($s("§sin"), v), Negate(m)), Power(Times(c, $($s("§cos"), w)), n)),
                  And(FreeQ(List(c, n), x), IntegerQ(m)))),
          ISetDelayed(527,
              FixInertTrigFunction(Times(Power($($s("§cos"), v_), m_DEFAULT),
                  Power(Times($($s("§tan"), w_), c_DEFAULT), n_DEFAULT)), x_),
              Condition(
                  Times(Power($($s("§sec"), v), Negate(m)), Power(Times(c, $($s("§tan"), w)), n)),
                  And(FreeQ(List(c, n), x), IntegerQ(m)))),
          ISetDelayed(528,
              FixInertTrigFunction(Times(Power($($s("§sin"), v_), m_DEFAULT),
                  Power(Times($($s("§cot"), w_), c_DEFAULT), n_DEFAULT)), x_),
              Condition(
                  Times(Power($($s("§csc"), v), Negate(m)), Power(Times(c, $($s("§cot"), w)), n)),
                  And(FreeQ(List(c, n), x), IntegerQ(m)))),
          ISetDelayed(529,
              FixInertTrigFunction(Times(Power($($s("§sin"), v_), m_DEFAULT),
                  Power(Times($($s("§sec"), w_), c_DEFAULT), n_DEFAULT)), x_),
              Condition(
                  Times(Power($($s("§csc"), v), Negate(m)), Power(Times(c, $($s("§sec"), w)), n)),
                  And(FreeQ(List(c, n), x), IntegerQ(m)))),
          ISetDelayed(530,
              FixInertTrigFunction(Times(Power($($s("§cos"), v_), m_DEFAULT),
                  Power(Times($($s("§csc"), w_), c_DEFAULT), n_DEFAULT)), x_),
              Condition(
                  Times(Power($($s("§sec"), v), Negate(m)), Power(Times(c, $($s("§csc"), w)), n)),
                  And(FreeQ(List(c, n), x), IntegerQ(m)))),
          ISetDelayed(531,
              FixInertTrigFunction(Times(Power($($s("§cot"), v_), m_DEFAULT),
                  Power(Times($($s("§sin"), w_), c_DEFAULT), n_DEFAULT)), x_),
              Condition(
                  Times(Power($($s("§tan"), v), Negate(m)), Power(Times(c, $($s("§sin"), w)), n)),
                  And(FreeQ(List(c, n), x), IntegerQ(m)))));
}
