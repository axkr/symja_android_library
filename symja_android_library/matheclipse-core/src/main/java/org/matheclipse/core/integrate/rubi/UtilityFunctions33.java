package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.A_DEFAULT;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.B_DEFAULT;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CPiHalf;
import static org.matheclipse.core.expression.F.C_DEFAULT;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.ISetDelayed;
import static org.matheclipse.core.expression.F.If;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Less;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.f_DEFAULT;
import static org.matheclipse.core.expression.F.g_DEFAULT;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_;
import static org.matheclipse.core.expression.F.p_DEFAULT;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.S.ASymbol;
import static org.matheclipse.core.expression.S.BSymbol;
import static org.matheclipse.core.expression.S.CSymbol;
import static org.matheclipse.core.expression.S.Pi;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.d;
import static org.matheclipse.core.expression.S.e;
import static org.matheclipse.core.expression.S.f;
import static org.matheclipse.core.expression.S.g;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.UnifyInertTrigFunction;
import org.matheclipse.core.interfaces.IAST;

/**
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class UtilityFunctions33 {
  public static IAST RULES = List(
      ISetDelayed(591,
          UnifyInertTrigFunction(
              Times(
                  Power(
                      Plus(
                          a_DEFAULT, Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              b_DEFAULT)),
                      m_DEFAULT),
                  Power(
                      Plus(
                          c_DEFAULT, Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              d_DEFAULT)),
                      n_DEFAULT)),
              x_),
          Condition(
              Times(
                  Power(Plus(a, Times(b,
                      $($s("§sin"), Plus(e, CPiHalf, Times(f, x))))), m),
                  Power(Plus(c, Times(d, $($s("§sin"), Plus(e, CPiHalf, Times(f, x))))), n)),
              FreeQ(List(a, b, c, d, e, f, m, n), x))),
      ISetDelayed(592,
          UnifyInertTrigFunction(Times(
              Power(
                  Plus(a_DEFAULT,
                      Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), b_DEFAULT)),
                  m_DEFAULT),
              Power(Plus(c_DEFAULT,
                  Times($($s("§sec"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), d_DEFAULT)),
                  n_DEFAULT)),
              x_),
          Condition(
              Times(
                  Power(Plus(a, Times(b, $($s("§sin"), Plus(e, CPiHalf, Times(f, x))))), m), Power(
                      Plus(c, Times(d, $($s("§csc"), Plus(e, CPiHalf, Times(f, x))))), n)),
              FreeQ(List(a, b, c, d, e, f, m, n), x))),
      ISetDelayed(593,
          UnifyInertTrigFunction(
              Times(
                  Power(Plus(a_DEFAULT, Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                      b_DEFAULT)), m_DEFAULT),
                  Power(
                      Plus(c_DEFAULT, Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                          d_DEFAULT)),
                      n_DEFAULT),
                  Power(
                      Times($($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                          g_DEFAULT),
                      p_DEFAULT)),
              x_),
          Condition(
              If(And(IntegerQ(Times(C2,
                  p)), Less(p,
                      C0),
                  IntegerQ(Times(C2, n))),
                  Times(
                      Power(Times(g,
                          $($s("§cos"), Plus(e, Times(CN1, C1D2, Pi), Times(f, x)))), p),
                      Power(
                          Subtract(
                              a,
                              Times(b, $($s("§sin"), Plus(e, Times(CN1, C1D2, Pi), Times(f, x))))),
                          m),
                      Power(
                          Subtract(
                              c,
                              Times(d, $($s("§sin"), Plus(e, Times(CN1, C1D2, Pi), Times(f, x))))),
                          n)),
                  Times(Power(Times(CN1, g, $($s("§cos"), Plus(e, CPiHalf, Times(f, x)))), p),
                      Power(Plus(a, Times(b,
                          $($s("§sin"), Plus(e, CPiHalf, Times(f, x))))), m),
                      Power(Plus(c, Times(d, $($s("§sin"), Plus(e, CPiHalf, Times(f, x))))), n))),
              FreeQ(List(a, b, c, d, e, f, g, m, n, p), x))),
      ISetDelayed(594,
          UnifyInertTrigFunction(
              Times(
                  Power(
                      Plus(a_DEFAULT, Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                          b_DEFAULT)),
                      m_DEFAULT),
                  Power(
                      Plus(c_DEFAULT, Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                          d_DEFAULT)),
                      n_DEFAULT),
                  Power(Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), g_DEFAULT),
                      p_DEFAULT)),
              x_),
          Condition(
              Times(
                  Power(Times(g,
                      $($s("§sec"), Plus(e, Times(CN1, C1D2, Pi), Times(f, x)))), p),
                  Power(
                      Subtract(a, Times(b,
                          $($s("§sin"), Plus(e, Times(CN1, C1D2, Pi), Times(f, x))))),
                      m),
                  Power(
                      Subtract(c, Times(d,
                          $($s("§sin"), Plus(e, Times(CN1, C1D2, Pi), Times(f, x))))),
                      n)),
              FreeQ(List(a, b, c, d, e, f, g, m, n, p), x))),
      ISetDelayed(595,
          UnifyInertTrigFunction(
              Times(
                  Power(
                      Plus(a_DEFAULT, Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                          b_DEFAULT)),
                      m_DEFAULT),
                  Power(
                      Plus(
                          c_DEFAULT, Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              d_DEFAULT)),
                      n_DEFAULT),
                  Power(
                      Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                          g_DEFAULT),
                      p_DEFAULT)),
              x_),
          Condition(
              Times(
                  Power(Times(g, $($s("§sin"), Plus(e, CPiHalf,
                      Times(f, x)))), p),
                  Power(Plus(a, Times(b,
                      $($s("§sin"), Plus(e, CPiHalf, Times(f, x))))), m),
                  Power(Plus(c, Times(d, $($s("§sin"), Plus(e, CPiHalf, Times(f, x))))), n)),
              FreeQ(List(a, b, c, d, e, f, g, m, n, p), x))),
      ISetDelayed(596,
          UnifyInertTrigFunction(
              Times(
                  Power(
                      Plus(
                          a_DEFAULT, Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              b_DEFAULT)),
                      m_DEFAULT),
                  Power(Plus(c_DEFAULT, Times($($s("§sec"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                      d_DEFAULT)), n_DEFAULT),
                  Power(
                      Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                          g_DEFAULT),
                      p_DEFAULT)),
              x_),
          Condition(
              Times(Power(Times(g, $($s("§sin"), Plus(e, CPiHalf, Times(f, x)))), p),
                  Power(Plus(a, Times(b,
                      $($s("§sin"), Plus(e, CPiHalf, Times(f, x))))), m),
                  Power(Plus(c, Times(d, $($s("§csc"), Plus(e, CPiHalf, Times(f, x))))), n)),
              FreeQ(List(a, b, c, d, e, f, g, m, n, p), x))),
      ISetDelayed(597,
          UnifyInertTrigFunction(
              Times(
                  Power(
                      Plus(a_DEFAULT, Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                          b_DEFAULT)),
                      m_DEFAULT),
                  Power(
                      Plus(
                          c_DEFAULT, Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              d_DEFAULT)),
                      n_DEFAULT),
                  Power(
                      Times($($s("§sec"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                          g_DEFAULT),
                      p_DEFAULT)),
              x_),
          Condition(
              Times(Power(Times(g, $($s("§csc"), Plus(e, CPiHalf, Times(f, x)))), p),
                  Power(Plus(a, Times(b,
                      $($s("§sin"), Plus(e, CPiHalf, Times(f, x))))), m),
                  Power(Plus(c, Times(d, $($s("§sin"), Plus(e, CPiHalf, Times(f, x))))), n)),
              FreeQ(List(a, b, c, d, e, f, g, m, n, p), x))),
      ISetDelayed(598,
          UnifyInertTrigFunction(
              Times(
                  Power(
                      Plus(a_DEFAULT, Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                          b_DEFAULT)),
                      m_DEFAULT),
                  Power(Plus(c_DEFAULT, Times($($s("§sec"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                      d_DEFAULT)), n_DEFAULT),
                  Power(
                      Times($($s("§sec"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                          g_DEFAULT),
                      p_DEFAULT)),
              x_),
          Condition(
              Times(Power(Times(g, $($s("§csc"), Plus(e, CPiHalf, Times(f, x)))), p),
                  Power(Plus(a, Times(b, $($s("§sin"), Plus(e, CPiHalf, Times(f, x))))), m), Power(
                      Plus(c, Times(d, $($s("§csc"), Plus(e, CPiHalf, Times(f, x))))), n)),
              FreeQ(List(a, b, c, d, e, f, g, m, n, p), x))),
      ISetDelayed(599,
          UnifyInertTrigFunction(
              Times(
                  Power(
                      Plus(
                          a_DEFAULT, Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              b_DEFAULT)),
                      m_DEFAULT),
                  Plus(
                      A_DEFAULT, Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                          B_DEFAULT)),
                  Power(
                      Plus(
                          c_DEFAULT, Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              d_DEFAULT)),
                      n_DEFAULT)),
              x_),
          Condition(
              Times(
                  Power(Plus(a, Times(b,
                      $($s("§sin"), Plus(e, CPiHalf, Times(f, x))))), m),
                  Power(Plus(c,
                      Times(d, $($s("§sin"), Plus(e, CPiHalf, Times(f, x))))), n),
                  Plus(ASymbol, Times(BSymbol, $($s("§sin"), Plus(e, CPiHalf, Times(f, x)))))),
              FreeQ(List(a, b, c, d, e, f, ASymbol, BSymbol, m, n), x))),
      ISetDelayed(600,
          UnifyInertTrigFunction(
              Times(
                  Power(
                      Plus(
                          a_DEFAULT, Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              b_DEFAULT)),
                      m_DEFAULT),
                  Plus(A_DEFAULT,
                      Times($($s("§cos"),
                          Plus(e_DEFAULT, Times(f_DEFAULT, x_))), B_DEFAULT),
                      Times(Sqr($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))), C_DEFAULT))),
              x_),
          Condition(
              Times(Power(Plus(a, Times(b, $($s("§sin"), Plus(e, CPiHalf, Times(f, x))))), m),
                  Plus(
                      ASymbol, Times(BSymbol, $($s("§sin"),
                          Plus(e, CPiHalf, Times(f, x)))),
                      Times(CSymbol, Sqr($($s("§sin"), Plus(e, CPiHalf, Times(f, x))))))),
              FreeQ(List(a, b, c, e, f, ASymbol, BSymbol, CSymbol, m), x))),
      ISetDelayed(601,
          UnifyInertTrigFunction(
              Times(
                  Power(
                      Plus(
                          a_DEFAULT, Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              b_DEFAULT)),
                      m_DEFAULT),
                  Plus(
                      A_DEFAULT, Times(Sqr($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          C_DEFAULT))),
              x_),
          Condition(
              Times(
                  Power(Plus(a,
                      Times(b, $($s("§sin"), Plus(e, CPiHalf, Times(f, x))))), m),
                  Plus(ASymbol, Times(CSymbol, Sqr($($s("§sin"), Plus(e, CPiHalf, Times(f, x))))))),
              FreeQ(List(a, b, c, e, f, ASymbol, CSymbol, m), x))),
      ISetDelayed(602,
          UnifyInertTrigFunction(
              Times(
                  Power(
                      Plus(
                          a_DEFAULT, Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              b_DEFAULT)),
                      m_DEFAULT),
                  Plus(A_DEFAULT,
                      Times($($s("§cos"),
                          Plus(e_DEFAULT, Times(f_DEFAULT, x_))), B_DEFAULT),
                      Times(Sqr($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))), C_DEFAULT)),
                  Power(
                      Plus(c_DEFAULT,
                          Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), d_DEFAULT)),
                      n_DEFAULT)),
              x_),
          Condition(
              Times(
                  Power(Plus(a, Times(b,
                      $($s("§sin"), Plus(e, CPiHalf, Times(f, x))))), m),
                  Power(Plus(c, Times(d, $($s("§sin"), Plus(e, CPiHalf, Times(f, x))))), n),
                  Plus(
                      ASymbol, Times(BSymbol, $($s("§sin"),
                          Plus(e, CPiHalf, Times(f, x)))),
                      Times(CSymbol, Sqr($($s("§sin"), Plus(e, CPiHalf, Times(f, x))))))),
              FreeQ(List(a, b, c, d, e, f, ASymbol, BSymbol, CSymbol, m, n), x))),
      ISetDelayed(603,
          UnifyInertTrigFunction(
              Times(
                  Power(Plus(a_DEFAULT, Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                      b_DEFAULT)), m_DEFAULT),
                  Plus(
                      A_DEFAULT, Times(Sqr($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          C_DEFAULT)),
                  Power(
                      Plus(c_DEFAULT,
                          Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), d_DEFAULT)),
                      n_DEFAULT)),
              x_),
          Condition(
              Times(Power(Plus(a, Times(b, $($s("§sin"), Plus(e, CPiHalf, Times(f, x))))), m),
                  Power(Plus(c,
                      Times(d, $($s("§sin"), Plus(e, CPiHalf, Times(f, x))))), n),
                  Plus(ASymbol, Times(CSymbol, Sqr($($s("§sin"), Plus(e, CPiHalf, Times(f, x))))))),
              FreeQ(List(a, b, c, d, e, f, ASymbol, CSymbol, m, n), x))),
      ISetDelayed(604,
          UnifyInertTrigFunction(
              Power(Plus(a_DEFAULT,
                  Times(b_DEFAULT,
                      Power(Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), c_DEFAULT),
                          n_))),
                  p_),
              x_),
          Condition(
              Power(
                  Plus(
                      a, Times(b, Power(Times(c, $($s("§sin"), Plus(e, CPiHalf, Times(f, x)))),
                          n))),
                  p),
              And(FreeQ(List(a, b, e, f, n, p), x), Not(And(EqQ(a, C0), IntegerQ(p)))))),
      ISetDelayed(605,
          UnifyInertTrigFunction(
              Times(
                  Power(
                      Plus(a_DEFAULT,
                          Times(b_DEFAULT,
                              Power(Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  c_DEFAULT), n_))),
                      p_DEFAULT),
                  Power(Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), d_DEFAULT),
                      m_DEFAULT)),
              x_),
          Condition(
              Times(
                  Power(Times(d,
                      $($s("§sin"), Plus(e, CPiHalf, Times(f, x)))), m),
                  Power(
                      Plus(a,
                          Times(b, Power(Times(c, $($s("§sin"), Plus(e, CPiHalf, Times(f, x)))),
                              n))),
                      p)),
              And(FreeQ(List(a, b, c, d, e, f, m, n, p), x), Not(And(EqQ(a, C0), IntegerQ(p)))))),
      ISetDelayed(606,
          UnifyInertTrigFunction(
              Times(
                  Power(Plus(a_DEFAULT,
                      Times(b_DEFAULT,
                          Power(Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              c_DEFAULT), n_))),
                      p_DEFAULT),
                  Power(
                      Times($($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                          d_DEFAULT),
                      m_DEFAULT)),
              x_),
          Condition(
              Times(Power(Times(CN1, d, $($s("§cos"), Plus(e, CPiHalf, Times(f, x)))), m),
                  Power(
                      Plus(a,
                          Times(b, Power(Times(c, $($s("§sin"), Plus(e, CPiHalf, Times(f, x)))),
                              n))),
                      p)),
              And(FreeQ(List(a, b, c, d, e, f, m, n, p), x), Not(And(EqQ(a, C0), IntegerQ(p)))))),
      ISetDelayed(607,
          UnifyInertTrigFunction(
              Times(Power(Plus(a_DEFAULT,
                  Times(b_DEFAULT,
                      Power(Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), c_DEFAULT),
                          n_))),
                  p_DEFAULT),
                  Power(
                      Times($($s("§cot"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                          d_DEFAULT),
                      m_DEFAULT)),
              x_),
          Condition(
              Times(
                  Power(Times(CN1, d,
                      $($s("§tan"), Plus(e, CPiHalf, Times(f, x)))), m),
                  Power(
                      Plus(a,
                          Times(b, Power(Times(c, $($s("§sin"), Plus(e, CPiHalf, Times(f, x)))),
                              n))),
                      p)),
              And(FreeQ(List(a, b, c, d, e, f, m, n, p), x), Not(And(EqQ(a, C0), IntegerQ(p)))))),
      ISetDelayed(608,
          UnifyInertTrigFunction(
              Times(
                  Power(
                      Plus(a_DEFAULT,
                          Times(b_DEFAULT,
                              Power(Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  c_DEFAULT), n_))),
                      p_DEFAULT),
                  Power(
                      Times($($s("§tan"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                          d_DEFAULT),
                      m_DEFAULT)),
              x_),
          Condition(
              Times(
                  Power(Times(CN1, d,
                      $($s("§cot"), Plus(e, CPiHalf, Times(f, x)))), m),
                  Power(
                      Plus(a,
                          Times(b, Power(Times(c, $($s("§sin"), Plus(e, CPiHalf, Times(f, x)))),
                              n))),
                      p)),
              And(FreeQ(List(a, b, c, d, e, f, m, n, p), x), Not(And(EqQ(a, C0), IntegerQ(p)))))),
      ISetDelayed(609,
          UnifyInertTrigFunction(
              Times(
                  Power(
                      Plus(a_DEFAULT,
                          Times(b_DEFAULT,
                              Power(Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  c_DEFAULT), n_))),
                      p_DEFAULT),
                  Power(
                      Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                          d_DEFAULT),
                      m_DEFAULT)),
              x_),
          Condition(
              Times(
                  Power(Times(CN1, d,
                      $($s("§sec"), Plus(e, CPiHalf, Times(f, x)))), m),
                  Power(
                      Plus(a,
                          Times(b, Power(Times(c, $($s("§sin"), Plus(e, CPiHalf, Times(f, x)))),
                              n))),
                      p)),
              And(FreeQ(List(a, b, c, d, e, f, m, n, p), x), Not(And(EqQ(a, C0), IntegerQ(p)))))),
      ISetDelayed(610,
          UnifyInertTrigFunction(
              Times(
                  Power(
                      Plus(a_DEFAULT,
                          Times(b_DEFAULT,
                              Power(Times($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  c_DEFAULT), n_))),
                      p_DEFAULT),
                  Power(Times($($s("§sec"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), d_DEFAULT),
                      m_DEFAULT)),
              x_),
          Condition(
              Times(Power(Times(d, $($s("§csc"), Plus(e, CPiHalf, Times(f, x)))), m),
                  Power(
                      Plus(a,
                          Times(b,
                              Power(Times(c, $($s("§sin"), Plus(e, CPiHalf, Times(f, x)))), n))),
                      p)),
              And(FreeQ(List(a, b, c, d, e, f, m, n, p), x), Not(And(EqQ(a, C0), IntegerQ(p)))))));
}
