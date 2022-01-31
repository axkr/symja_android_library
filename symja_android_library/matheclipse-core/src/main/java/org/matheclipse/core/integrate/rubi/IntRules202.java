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
class IntRules202 {
  public static IAST RULES =
      List(
          IIntegrate(4041,
              Integrate(
                  Times(
                      Plus(A_DEFAULT,
                          Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), B_DEFAULT),
                          Times(Sqr($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                              C_DEFAULT)),
                      Power(
                          Plus(Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              b_DEFAULT), a_),
                          m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(b, CN2),
                      Integrate(
                          Times(Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), Plus(m, C1)),
                              Simp(
                                  Plus(Times(b, BSymbol), Times(CN1, a, CSymbol),
                                      Times(b, CSymbol, Csc(Plus(e, Times(f, x))))),
                                  x)),
                          x),
                      x),
                  And(FreeQ(List(a, b, e, f, ASymbol, BSymbol, CSymbol,
                      m), x), EqQ(
                          Plus(
                              Times(ASymbol, Sqr(
                                  b)),
                              Times(CN1, a, b, BSymbol), Times(Sqr(a), CSymbol)),
                          C0)))),
          IIntegrate(4042,
              Integrate(
                  Times(
                      Plus(A_DEFAULT,
                          Times(
                              Sqr($($s("§csc"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                              C_DEFAULT)),
                      Power(
                          Plus(Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              b_DEFAULT), a_),
                          m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Times(CSymbol, Power(b, CN2)),
                      Integrate(
                          Times(Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), Plus(m, C1)),
                              Simp(Plus(Negate(a), Times(b, Csc(Plus(e, Times(f, x))))), x)),
                          x),
                      x),
                  And(FreeQ(List(a, b, e, f, ASymbol, CSymbol, m), x),
                      EqQ(Plus(Times(ASymbol, Sqr(b)), Times(Sqr(a), CSymbol)), C0)))),
          IIntegrate(4043,
              Integrate(
                  Times(
                      Power(Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                          b_DEFAULT), m_DEFAULT),
                      Plus(
                          Times(Sqr($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                              C_DEFAULT),
                          A_)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(
                          ASymbol, Cot(Plus(e, Times(f,
                              x))),
                          Power(Times(b, Csc(Plus(e, Times(f, x)))), m), Power(Times(f, m), CN1)),
                      x),
                  And(FreeQ(List(b, e, f, ASymbol, CSymbol,
                      m), x), EqQ(Plus(Times(CSymbol, m), Times(ASymbol, Plus(m, C1))),
                          C0)))),
          IIntegrate(4044,
              Integrate(
                  Times(Power($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), m_DEFAULT),
                      Plus(
                          Times(Sqr($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                              C_DEFAULT),
                          A_)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(
                          Plus(CSymbol, Times(ASymbol,
                              Sqr(Sin(Plus(e, Times(f, x)))))),
                          Power(Power(Sin(Plus(e, Times(f, x))), Plus(m, C2)), CN1)),
                      x),
                  And(FreeQ(List(e, f, ASymbol, CSymbol), x),
                      NeQ(Plus(Times(CSymbol, m),
                          Times(ASymbol, Plus(m, C1))), C0),
                      ILtQ(Times(C1D2, Plus(m, C1)), C0)))),
          IIntegrate(4045,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              b_DEFAULT),
                          m_DEFAULT),
                      Plus(Times(Sqr($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          C_DEFAULT), A_)),
                  x_Symbol),
              Condition(Plus(
                  Simp(
                      Times(
                          ASymbol, Cot(Plus(e, Times(f, x))),
                          Power(Times(b, Csc(Plus(e, Times(f, x)))), m), Power(Times(f, m), CN1)),
                      x),
                  Dist(
                      Times(
                          Plus(Times(CSymbol, m), Times(ASymbol, Plus(m,
                              C1))),
                          Power(Times(Sqr(b), m), CN1)),
                      Integrate(Power(Times(b, Csc(Plus(e, Times(f, x)))), Plus(m, C2)), x), x)),
                  And(FreeQ(List(b, e, f, ASymbol, CSymbol), x), NeQ(Plus(Times(CSymbol, m),
                      Times(ASymbol, Plus(m, C1))), C0), LeQ(m,
                          CN1)))),
          IIntegrate(4046,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              b_DEFAULT),
                          m_DEFAULT),
                      Plus(
                          Times(Sqr($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                              C_DEFAULT),
                          A_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(Times(CSymbol, Cot(Plus(e, Times(f, x))),
                              Power(Times(b, Csc(Plus(e, Times(f, x)))), m),
                              Power(Times(f, Plus(m, C1)), CN1)), x)),
                      Dist(
                          Times(
                              Plus(Times(CSymbol, m),
                                  Times(ASymbol, Plus(m, C1))),
                              Power(Plus(m, C1), CN1)),
                          Integrate(Power(Times(b, Csc(Plus(e, Times(f, x)))), m), x), x)),
                  And(FreeQ(List(b, e, f, ASymbol, CSymbol, m), x),
                      NeQ(Plus(Times(CSymbol, m),
                          Times(ASymbol, Plus(m, C1))), C0),
                      Not(LeQ(m, CN1))))),
          IIntegrate(4047,
              Integrate(
                  Times(
                      Power(
                          Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              b_DEFAULT),
                          m_DEFAULT),
                      Plus(A_DEFAULT,
                          Times($($s("§csc"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_))), B_DEFAULT),
                          Times(
                              Sqr($($s("§csc"), Plus(e_DEFAULT,
                                  Times(f_DEFAULT, x_)))),
                              C_DEFAULT))),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(
                          Times(BSymbol, Power(b, CN1)), Integrate(Power(Times(b, Csc(
                              Plus(e, Times(f, x)))), Plus(m,
                                  C1)),
                              x),
                          x),
                      Integrate(
                          Times(
                              Power(Times(b, Csc(Plus(e, Times(f, x)))), m), Plus(ASymbol,
                                  Times(CSymbol, Sqr(Csc(Plus(e, Times(f, x))))))),
                          x)),
                  FreeQ(List(b, e, f, ASymbol, BSymbol, CSymbol, m), x))),
          IIntegrate(4048,
              Integrate(
                  Times(
                      Plus(A_DEFAULT,
                          Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), B_DEFAULT),
                          Times(Sqr($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                              C_DEFAULT)),
                      Plus(
                          Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              b_DEFAULT),
                          a_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(
                                  b, CSymbol, Csc(Plus(e, Times(f, x))), Cot(Plus(e,
                                      Times(f, x))),
                                  Power(Times(C2, f), CN1)),
                              x)),
                      Dist(C1D2,
                          Integrate(
                              Simp(
                                  Plus(Times(C2, ASymbol, a),
                                      Times(
                                          Plus(Times(C2, BSymbol, a),
                                              Times(b, Plus(Times(C2, ASymbol), CSymbol))),
                                          Csc(Plus(e, Times(f, x)))),
                                      Times(C2, Plus(Times(a, CSymbol), Times(BSymbol, b)),
                                          Sqr(Csc(Plus(e, Times(f, x)))))),
                                  x),
                              x),
                          x)),
                  FreeQ(List(a, b, e, f, ASymbol, BSymbol, CSymbol), x))),
          IIntegrate(4049,
              Integrate(
                  Times(
                      Plus(A_DEFAULT,
                          Times(Sqr($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                              C_DEFAULT)),
                      Plus(
                          Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              b_DEFAULT),
                          a_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(
                                  b, CSymbol, Csc(Plus(e, Times(f, x))), Cot(
                                      Plus(e, Times(f, x))),
                                  Power(Times(C2, f), CN1)),
                              x)),
                      Dist(C1D2,
                          Integrate(
                              Simp(Plus(Times(C2, ASymbol, a),
                                  Times(b, Plus(Times(C2, ASymbol),
                                      CSymbol), Csc(Plus(e, Times(f, x)))),
                                  Times(C2, a, CSymbol, Sqr(Csc(Plus(e, Times(f, x)))))),
                                  x),
                              x),
                          x)),
                  FreeQ(List(a, b, e, f, ASymbol, CSymbol), x))),
          IIntegrate(4050,
              Integrate(
                  Times(
                      Plus(A_DEFAULT,
                          Times($($s("§csc"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_))), B_DEFAULT),
                          Times(
                              Sqr($($s("§csc"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                              C_DEFAULT)),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_),
                          CN1)),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(
                          Times(CSymbol, Power(b,
                              CN1)),
                          Integrate(Csc(Plus(e, Times(f, x))), x), x),
                      Dist(Power(b, CN1),
                          Integrate(
                              Times(
                                  Plus(Times(ASymbol, b),
                                      Times(Subtract(Times(b, BSymbol), Times(a, CSymbol)), Csc(
                                          Plus(e, Times(f, x))))),
                                  Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), CN1)),
                              x),
                          x)),
                  FreeQ(List(a, b, e, f, ASymbol, BSymbol, CSymbol), x))),
          IIntegrate(4051,
              Integrate(
                  Times(
                      Plus(A_DEFAULT,
                          Times(
                              Sqr($($s("§csc"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                              C_DEFAULT)),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_),
                          CN1)),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(
                          Times(CSymbol, Power(b,
                              CN1)),
                          Integrate(Csc(Plus(e, Times(f, x))), x), x),
                      Dist(Power(b, CN1),
                          Integrate(
                              Times(
                                  Subtract(
                                      Times(ASymbol, b),
                                      Times(a, CSymbol, Csc(Plus(e, Times(f, x))))),
                                  Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), CN1)),
                              x),
                          x)),
                  FreeQ(List(a, b, e, f, ASymbol, CSymbol), x))),
          IIntegrate(4052,
              Integrate(
                  Times(
                      Plus(A_DEFAULT,
                          Times($($s("§csc"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_))), B_DEFAULT),
                          Times(
                              Sqr($($s("§csc"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                              C_DEFAULT)),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_),
                          m_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(
                                  Plus(
                                      Times(a, ASymbol), Times(CN1, b, BSymbol), Times(a, CSymbol)),
                                  Cot(Plus(e, Times(f,
                                      x))),
                                  Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), m), Power(
                                      Times(a, f, Plus(Times(C2, m), C1)), CN1)),
                              x)),
                      Dist(Power(Times(a, b, Plus(Times(C2, m), C1)), CN1),
                          Integrate(
                              Times(
                                  Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), Plus(m, C1)),
                                  Simp(Plus(Times(ASymbol, b, Plus(Times(C2, m), C1)),
                                      Times(
                                          Subtract(Times(b, BSymbol, Plus(m, C1)),
                                              Times(a,
                                                  Subtract(Times(ASymbol, Plus(m, C1)),
                                                      Times(CSymbol, m)))),
                                          Csc(Plus(e, Times(f, x))))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, e, f, ASymbol, BSymbol, CSymbol), x),
                      EqQ(Subtract(Sqr(a), Sqr(b)), C0), LtQ(m, Negate(C1D2))))),
          IIntegrate(4053,
              Integrate(
                  Times(
                      Plus(A_DEFAULT,
                          Times(
                              Sqr($($s("§csc"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                              C_DEFAULT)),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_),
                          m_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(a, Plus(ASymbol, CSymbol), Cot(Plus(e, Times(f, x))),
                                  Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), m),
                                  Power(Times(a, f, Plus(Times(C2, m), C1)), CN1)),
                              x)),
                      Dist(Power(Times(a, b, Plus(Times(C2, m), C1)), CN1),
                          Integrate(
                              Times(
                                  Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), Plus(m, C1)),
                                  Simp(
                                      Subtract(Times(ASymbol, b, Plus(Times(C2, m), C1)),
                                          Times(a,
                                              Subtract(Times(ASymbol, Plus(m, C1)),
                                                  Times(CSymbol, m)),
                                              Csc(Plus(e, Times(f, x))))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, e, f, ASymbol, CSymbol), x), EqQ(Subtract(Sqr(a),
                      Sqr(b)), C0), LtQ(m,
                          Negate(C1D2))))),
          IIntegrate(4054,
              Integrate(
                  Times(
                      Plus(A_DEFAULT,
                          Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT,
                              x_))), B_DEFAULT),
                          Times(
                              Sqr($($s("§csc"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                              C_DEFAULT)),
                      Power(
                          Plus(Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              b_DEFAULT), a_),
                          m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(CSymbol, Cot(Plus(e, Times(f, x))),
                                  Power(Plus(a, Times(b,
                                      Csc(Plus(e, Times(f, x))))), m),
                                  Power(Times(f, Plus(m, C1)), CN1)),
                              x)),
                      Dist(
                          Power(Times(b,
                              Plus(m, C1)), CN1),
                          Integrate(Times(Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), m),
                              Simp(Plus(Times(ASymbol, b, Plus(m, C1)),
                                  Times(Plus(Times(a, CSymbol, m), Times(b, BSymbol, Plus(m, C1))),
                                      Csc(Plus(e, Times(f, x))))),
                                  x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, e, f, ASymbol, BSymbol, CSymbol, m), x),
                      EqQ(Subtract(Sqr(a), Sqr(b)), C0), Not(LtQ(m, Negate(C1D2)))))),
          IIntegrate(4055,
              Integrate(
                  Times(
                      Plus(A_DEFAULT,
                          Times(
                              Sqr($($s("§csc"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                              C_DEFAULT)),
                      Power(
                          Plus(Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              b_DEFAULT), a_),
                          m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(CSymbol, Cot(Plus(e, Times(f, x))),
                                  Power(Plus(a,
                                      Times(b, Csc(Plus(e, Times(f, x))))), m),
                                  Power(Times(f, Plus(m, C1)), CN1)),
                              x)),
                      Dist(
                          Power(Times(b,
                              Plus(m, C1)), CN1),
                          Integrate(
                              Times(
                                  Power(Plus(a,
                                      Times(b, Csc(Plus(e, Times(f, x))))), m),
                                  Simp(
                                      Plus(
                                          Times(ASymbol, b, Plus(m, C1)), Times(a, CSymbol, m,
                                              Csc(Plus(e, Times(f, x))))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, e, f, ASymbol, CSymbol, m), x),
                      EqQ(Subtract(Sqr(a), Sqr(b)), C0), Not(LtQ(m, Negate(C1D2)))))),
          IIntegrate(4056,
              Integrate(
                  Times(
                      Plus(A_DEFAULT,
                          Times($($s("§csc"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_))), B_DEFAULT),
                          Times(
                              Sqr($($s("§csc"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                              C_DEFAULT)),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_),
                          m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(
                                  CSymbol, Cot(Plus(e, Times(f,
                                      x))),
                                  Power(Plus(a,
                                      Times(b, Csc(Plus(e, Times(f, x))))), m),
                                  Power(Times(f, Plus(m, C1)), CN1)),
                              x)),
                      Dist(
                          Power(Plus(m,
                              C1), CN1),
                          Integrate(
                              Times(
                                  Power(
                                      Plus(a, Times(b,
                                          Csc(Plus(e, Times(f, x))))),
                                      Subtract(m, C1)),
                                  Simp(Plus(Times(a, ASymbol, Plus(m, C1)), Times(
                                      Plus(Times(Plus(Times(ASymbol, b), Times(a, BSymbol)),
                                          Plus(m, C1)), Times(b, CSymbol, m)),
                                      Csc(Plus(e, Times(f, x)))),
                                      Times(
                                          Plus(Times(b, BSymbol, Plus(m, C1)),
                                              Times(a, CSymbol, m)),
                                          Sqr(Csc(Plus(e, Times(f, x)))))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, e, f, ASymbol, BSymbol, CSymbol), x), NeQ(Subtract(Sqr(a),
                      Sqr(b)), C0), IGtQ(Times(C2, m),
                          C0)))),
          IIntegrate(4057,
              Integrate(
                  Times(
                      Plus(A_DEFAULT,
                          Times(
                              Sqr($($s("§csc"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                              C_DEFAULT)),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_),
                          m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(CSymbol, Cot(Plus(e, Times(f, x))),
                                  Power(Plus(a,
                                      Times(b, Csc(Plus(e, Times(f, x))))), m),
                                  Power(Times(f, Plus(m, C1)), CN1)),
                              x)),
                      Dist(Power(Plus(m, C1), CN1),
                          Integrate(Times(
                              Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), Subtract(m, C1)),
                              Simp(
                                  Plus(
                                      Times(a, ASymbol, Plus(m, C1)),
                                      Times(Plus(Times(ASymbol, b, Plus(m, C1)),
                                          Times(b, CSymbol, m)),
                                          Csc(Plus(e, Times(f, x)))),
                                      Times(a, CSymbol, m, Sqr(Csc(Plus(e, Times(f, x)))))),
                                  x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, e, f, ASymbol, CSymbol), x), NeQ(Subtract(Sqr(a),
                      Sqr(b)), C0), IGtQ(Times(C2, m),
                          C0)))),
          IIntegrate(4058,
              Integrate(
                  Times(
                      Plus(A_DEFAULT,
                          Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), B_DEFAULT),
                          Times(Sqr($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                              C_DEFAULT)),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_),
                          CN1D2)),
                  x_Symbol),
              Condition(
                  Plus(
                      Integrate(
                          Times(
                              Plus(ASymbol,
                                  Times(Subtract(BSymbol, CSymbol), Csc(Plus(e, Times(f, x))))),
                              Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), CN1D2)),
                          x),
                      Dist(CSymbol,
                          Integrate(Times(Csc(Plus(e, Times(f, x))),
                              Plus(C1, Csc(Plus(e, Times(f, x)))), Power(Plus(a,
                                  Times(b, Csc(Plus(e, Times(f, x))))), CN1D2)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, e, f, ASymbol, BSymbol, CSymbol), x),
                      NeQ(Subtract(Sqr(a), Sqr(b)), C0)))),
          IIntegrate(4059,
              Integrate(
                  Times(
                      Plus(A_DEFAULT,
                          Times(
                              Sqr($($s("§csc"),
                                  Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                              C_DEFAULT)),
                      Power(
                          Plus(
                              Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                                  b_DEFAULT),
                              a_),
                          CN1D2)),
                  x_Symbol),
              Condition(
                  Plus(
                      Integrate(
                          Times(
                              Subtract(ASymbol, Times(CSymbol, Csc(Plus(e, Times(f, x))))), Power(
                                  Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), CN1D2)),
                          x),
                      Dist(CSymbol,
                          Integrate(Times(Csc(Plus(e, Times(f, x))),
                              Plus(C1, Csc(Plus(e, Times(f, x)))), Power(Plus(a,
                                  Times(b, Csc(Plus(e, Times(f, x))))), CN1D2)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, e, f, ASymbol, CSymbol), x),
                      NeQ(Subtract(Sqr(a), Sqr(b)), C0)))),
          IIntegrate(4060,
              Integrate(
                  Times(
                      Plus(A_DEFAULT, Times(
                          $($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))), B_DEFAULT),
                          Times(Sqr($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                              C_DEFAULT)),
                      Power(Plus(Times($($s("§csc"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                          b_DEFAULT), a_), m_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(
                              Plus(
                                  Times(ASymbol, Sqr(b)), Times(CN1, a, b, BSymbol), Times(Sqr(a),
                                      CSymbol)),
                              Cot(Plus(e, Times(f, x))),
                              Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), Plus(m,
                                  C1)),
                              Power(Times(a, f, Plus(m, C1), Subtract(Sqr(a), Sqr(b))), CN1)),
                          x),
                      Dist(Power(Times(a, Plus(m, C1), Subtract(Sqr(a), Sqr(b))), CN1),
                          Integrate(
                              Times(
                                  Power(Plus(a, Times(b, Csc(Plus(e, Times(f, x))))), Plus(m, C1)),
                                  Simp(Plus(Times(ASymbol, Subtract(Sqr(a), Sqr(b)), Plus(m, C1)),
                                      Times(CN1, a,
                                          Plus(Times(ASymbol, b), Times(CN1, a, BSymbol),
                                              Times(b, CSymbol)),
                                          Plus(m, C1), Csc(Plus(e, Times(f, x)))),
                                      Times(
                                          Plus(Times(ASymbol, Sqr(b)), Times(CN1, a, b, BSymbol),
                                              Times(Sqr(a), CSymbol)),
                                          Plus(m, C2), Sqr(Csc(Plus(e, Times(f, x)))))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, e, f, ASymbol, BSymbol, CSymbol), x),
                      NeQ(Subtract(Sqr(a), Sqr(b)), C0), LtQ(m, CN1)))));
}
