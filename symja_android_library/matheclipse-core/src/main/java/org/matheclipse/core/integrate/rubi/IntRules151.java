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
class IntRules151 {
  public static IAST RULES = List(
      IIntegrate(3021,
          Integrate(
              Times(
                  Power(Plus(a_DEFAULT,
                      Times(b_DEFAULT, $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))), m_),
                  Plus(A_DEFAULT,
                      Times(B_DEFAULT, $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                      Times(C_DEFAULT, Sqr($($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))))),
              x_Symbol),
          Condition(
              Plus(
                  Negate(
                      Simp(
                          Times(
                              Plus(
                                  Times(ASymbol, Sqr(b)), Times(CN1, a, b, BSymbol), Times(Sqr(a),
                                      CSymbol)),
                              Cos(Plus(e, Times(f, x))),
                              Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), Plus(m,
                                  C1)),
                              Power(Times(b, f, Plus(m, C1), Subtract(Sqr(a), Sqr(b))), CN1)),
                          x)),
                  Dist(Power(Times(b, Plus(m, C1), Subtract(Sqr(a), Sqr(b))), CN1),
                      Integrate(
                          Times(Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), Plus(m, C1)),
                              Simp(
                                  Subtract(
                                      Times(b,
                                          Plus(Times(a, ASymbol), Times(CN1, b, BSymbol),
                                              Times(a, CSymbol)),
                                          Plus(m, C1)),
                                      Times(
                                          Plus(Times(ASymbol, Sqr(b)), Times(CN1, a, b, BSymbol),
                                              Times(Sqr(a), CSymbol),
                                              Times(b,
                                                  Plus(Times(ASymbol, b), Times(CN1, a, BSymbol),
                                                      Times(b, CSymbol)),
                                                  Plus(m, C1))),
                                          Sin(Plus(e, Times(f, x))))),
                                  x)),
                          x),
                      x)),
              And(FreeQ(List(a, b, e, f, ASymbol, BSymbol,
                  CSymbol), x), LtQ(m,
                      CN1),
                  NeQ(Subtract(Sqr(a), Sqr(b)), C0)))),
      IIntegrate(3022,
          Integrate(
              Times(
                  Power(
                      Plus(a_,
                          Times(b_DEFAULT, $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      m_),
                  Plus(A_DEFAULT,
                      Times(C_DEFAULT, Sqr($($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))))),
              x_Symbol),
          Condition(
              Plus(
                  Negate(
                      Simp(
                          Times(
                              Plus(Times(ASymbol, Sqr(b)), Times(Sqr(
                                  a), CSymbol)),
                              Cos(Plus(e, Times(f, x))),
                              Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), Plus(m,
                                  C1)),
                              Power(Times(b, f, Plus(m, C1), Subtract(Sqr(a), Sqr(b))), CN1)),
                          x)),
                  Dist(Power(Times(b, Plus(m, C1), Subtract(Sqr(a), Sqr(b))), CN1),
                      Integrate(
                          Times(Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), Plus(m, C1)),
                              Simp(Subtract(Times(a, b, Plus(ASymbol, CSymbol), Plus(m, C1)),
                                  Times(
                                      Plus(Times(ASymbol, Sqr(b)), Times(Sqr(a), CSymbol),
                                          Times(Sqr(b), Plus(ASymbol, CSymbol), Plus(m, C1))),
                                      Sin(Plus(e, Times(f, x))))),
                                  x)),
                          x),
                      x)),
              And(FreeQ(List(a, b, e, f, ASymbol,
                  CSymbol), x), LtQ(m,
                      CN1),
                  NeQ(Subtract(Sqr(a), Sqr(b)), C0)))),
      IIntegrate(3023,
          Integrate(
              Times(
                  Power(
                      Plus(
                          a_DEFAULT, Times(b_DEFAULT, $($s("§sin"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      m_DEFAULT),
                  Plus(A_DEFAULT,
                      Times(B_DEFAULT, $($s("§sin"),
                          Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                      Times(C_DEFAULT, Sqr($($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))))),
              x_Symbol),
          Condition(
              Plus(
                  Negate(
                      Simp(
                          Times(CSymbol, Cos(Plus(e, Times(f, x))),
                              Power(Plus(a, Times(b,
                                  Sin(Plus(e, Times(f, x))))), Plus(m,
                                      C1)),
                              Power(Times(b, f, Plus(m, C2)), CN1)),
                          x)),
                  Dist(Power(Times(b, Plus(m, C2)), CN1),
                      Integrate(Times(Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), m),
                          Simp(
                              Plus(Times(ASymbol, b, Plus(m, C2)), Times(b, CSymbol, Plus(m, C1)),
                                  Times(Subtract(Times(b, BSymbol, Plus(m, C2)), Times(a, CSymbol)),
                                      Sin(Plus(e, Times(f, x))))),
                              x)),
                          x),
                      x)),
              And(FreeQ(List(a, b, e, f, ASymbol, BSymbol, CSymbol, m), x), Not(LtQ(m, CN1))))),
      IIntegrate(3024,
          Integrate(
              Times(
                  Power(
                      Plus(
                          a_, Times(b_DEFAULT, $($s("§sin"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      m_DEFAULT),
                  Plus(
                      A_DEFAULT, Times(C_DEFAULT, Sqr($($s("§sin"),
                          Plus(e_DEFAULT, Times(f_DEFAULT, x_))))))),
              x_Symbol),
          Condition(
              Plus(
                  Negate(
                      Simp(
                          Times(CSymbol, Cos(Plus(e, Times(f, x))),
                              Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), Plus(m, C1)),
                              Power(Times(b, f, Plus(m, C2)), CN1)),
                          x)),
                  Dist(
                      Power(Times(b,
                          Plus(m, C2)), CN1),
                      Integrate(
                          Times(Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), m),
                              Simp(
                                  Subtract(
                                      Plus(Times(ASymbol, b, Plus(m, C2)), Times(b, CSymbol,
                                          Plus(m, C1))),
                                      Times(a, CSymbol, Sin(Plus(e, Times(f, x))))),
                                  x)),
                          x),
                      x)),
              And(FreeQ(List(a, b, e, f, ASymbol, CSymbol, m), x), Not(LtQ(m, CN1))))),
      IIntegrate(3025,
          Integrate(
              Times(
                  Power(
                      Times(
                          b_DEFAULT, Power($($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              p_)),
                      m_),
                  Plus(A_DEFAULT,
                      Times(B_DEFAULT, $($s("§sin"),
                          Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                      Times(C_DEFAULT, Sqr($($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))))),
              x_Symbol),
          Condition(
              Dist(
                  Times(
                      Power(Times(b, Power(Sin(Plus(e, Times(f, x))), p)), m), Power(
                          Power(Times(b, Sin(Plus(e, Times(f, x)))), Times(m, p)), CN1)),
                  Integrate(
                      Times(
                          Power(Times(b,
                              Sin(Plus(e, Times(f, x)))), Times(m, p)),
                          Plus(
                              ASymbol, Times(BSymbol, Sin(
                                  Plus(e, Times(f, x)))),
                              Times(CSymbol, Sqr(Sin(Plus(e, Times(f, x))))))),
                      x),
                  x),
              And(FreeQ(List(b, e, f, ASymbol, BSymbol, CSymbol, m, p), x), Not(IntegerQ(m))))),
      IIntegrate(3026,
          Integrate(
              Times(
                  Power(
                      Times(
                          Power($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              p_),
                          b_DEFAULT),
                      m_),
                  Plus(A_DEFAULT,
                      Times($($s("§cos"),
                          Plus(e_DEFAULT, Times(f_DEFAULT, x_))), B_DEFAULT),
                      Times(Sqr($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))), C_DEFAULT))),
              x_Symbol),
          Condition(
              Dist(
                  Times(
                      Power(Times(b, Power(Cos(Plus(e, Times(f, x))), p)), m), Power(
                          Power(Times(b, Cos(Plus(e, Times(f, x)))), Times(m, p)), CN1)),
                  Integrate(
                      Times(
                          Power(Times(b,
                              Cos(Plus(e, Times(f, x)))), Times(m, p)),
                          Plus(ASymbol, Times(BSymbol, Cos(Plus(e, Times(f, x)))),
                              Times(CSymbol, Sqr(Cos(Plus(e, Times(f, x))))))),
                      x),
                  x),
              And(FreeQ(List(b, e, f, ASymbol, BSymbol, CSymbol, m, p), x), Not(IntegerQ(m))))),
      IIntegrate(3027,
          Integrate(
              Times(
                  Power(
                      Times(
                          b_DEFAULT, Power($($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              p_)),
                      m_),
                  Plus(
                      A_DEFAULT, Times(C_DEFAULT, Sqr($($s("§sin"),
                          Plus(e_DEFAULT, Times(f_DEFAULT, x_))))))),
              x_Symbol),
          Condition(
              Dist(
                  Times(
                      Power(Times(b, Power(Sin(Plus(e, Times(f, x))),
                          p)), m),
                      Power(Power(Times(b, Sin(Plus(e, Times(f, x)))), Times(m, p)), CN1)),
                  Integrate(
                      Times(
                          Power(Times(b, Sin(Plus(e, Times(f, x)))), Times(m, p)), Plus(ASymbol,
                              Times(CSymbol, Sqr(Sin(Plus(e, Times(f, x))))))),
                      x),
                  x),
              And(FreeQ(List(b, e, f, ASymbol, CSymbol, m, p), x), Not(IntegerQ(m))))),
      IIntegrate(3028,
          Integrate(
              Times(
                  Power(
                      Times(
                          Power($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))),
                              p_),
                          b_DEFAULT),
                      m_),
                  Plus(
                      A_DEFAULT, Times(Sqr($($s("§cos"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          C_DEFAULT))),
              x_Symbol),
          Condition(
              Dist(
                  Times(
                      Power(Times(b, Power(Cos(Plus(e, Times(f, x))),
                          p)), m),
                      Power(Power(Times(b, Cos(Plus(e, Times(f, x)))), Times(m, p)), CN1)),
                  Integrate(
                      Times(
                          Power(Times(b, Cos(
                              Plus(e, Times(f, x)))), Times(m,
                                  p)),
                          Plus(ASymbol, Times(CSymbol, Sqr(Cos(Plus(e, Times(f, x))))))),
                      x),
                  x),
              And(FreeQ(List(b, e, f, ASymbol, CSymbol, m, p), x), Not(IntegerQ(m))))),
      IIntegrate(3029,
          Integrate(
              Times(
                  Power(
                      Plus(a_DEFAULT, Times(b_DEFAULT,
                          $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      m_DEFAULT),
                  Power(
                      Plus(
                          c_DEFAULT, Times(d_DEFAULT, $($s("§sin"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      n_DEFAULT),
                  Plus(A_DEFAULT,
                      Times(B_DEFAULT, $($s("§sin"),
                          Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                      Times(C_DEFAULT, Sqr($($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))))),
              x_Symbol),
          Condition(
              Dist(Power(b, CN2),
                  Integrate(
                      Times(Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), Plus(m, C1)),
                          Power(Plus(c,
                              Times(d, Sin(Plus(e, Times(f, x))))), n),
                          Plus(
                              Times(b, BSymbol), Times(CN1, a, CSymbol), Times(b, CSymbol,
                                  Sin(Plus(e, Times(f, x)))))),
                      x),
                  x),
              And(FreeQ(List(a, b, c, d, e, f, ASymbol, BSymbol, CSymbol, m, n), x),
                  NeQ(Subtract(Times(b, c),
                      Times(a, d)), C0),
                  EqQ(Plus(Times(ASymbol, Sqr(
                      b)), Times(CN1, a, b,
                          BSymbol),
                      Times(Sqr(a), CSymbol)), C0)))),
      IIntegrate(3030,
          Integrate(
              Times(
                  Power(
                      Plus(
                          a_DEFAULT, Times(b_DEFAULT, $($s("§sin"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      m_DEFAULT),
                  Power(
                      Plus(
                          c_DEFAULT, Times(d_DEFAULT, $($s("§sin"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      n_DEFAULT),
                  Plus(A_DEFAULT,
                      Times(C_DEFAULT, Sqr($($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))))),
              x_Symbol),
          Condition(
              Negate(Dist(Times(CSymbol, Power(b, CN2)),
                  Integrate(Times(Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), Plus(m, C1)),
                      Power(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))), n), Subtract(a,
                          Times(b, Sin(Plus(e, Times(f, x)))))),
                      x),
                  x)),
              And(FreeQ(List(a, b, c, d, e, f, ASymbol, CSymbol, m, n), x),
                  NeQ(Subtract(Times(b, c), Times(a, d)), C0), EqQ(
                      Plus(Times(ASymbol, Sqr(b)), Times(Sqr(a), CSymbol)), C0)))),
      IIntegrate(3031,
          Integrate(
              Times(
                  Power(
                      Plus(
                          a_DEFAULT, Times(b_DEFAULT,
                              $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      m_),
                  Plus(
                      c_DEFAULT, Times(d_DEFAULT,
                          $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                  Plus(A_DEFAULT,
                      Times(B_DEFAULT, $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                      Times(C_DEFAULT, Sqr($($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))))),
              x_Symbol),
          Condition(
              Subtract(
                  Negate(
                      Simp(Times(Subtract(Times(b, c), Times(a, d)),
                          Plus(Times(ASymbol,
                              Sqr(b)), Times(CN1, a, b, BSymbol), Times(Sqr(a), CSymbol)),
                          Cos(Plus(e, Times(f, x))),
                          Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), Plus(m,
                              C1)),
                          Power(Times(Sqr(b), f, Plus(m, C1), Subtract(Sqr(a), Sqr(b))), CN1)),
                          x)),
                  Dist(
                      Power(Times(Sqr(b), Plus(m, C1),
                          Subtract(Sqr(a), Sqr(b))), CN1),
                      Integrate(
                          Times(
                              Power(Plus(a, Times(b,
                                  Sin(Plus(e, Times(f, x))))), Plus(m,
                                      C1)),
                              Simp(
                                  Subtract(
                                      Plus(
                                          Times(b, Plus(m, C1),
                                              Subtract(
                                                  Times(
                                                      Subtract(Times(
                                                          b, BSymbol), Times(a, CSymbol)),
                                                      Subtract(Times(b, c), Times(a, d))),
                                                  Times(ASymbol, b,
                                                      Subtract(Times(a, c), Times(b, d))))),
                                          Times(
                                              Plus(
                                                  Times(b, BSymbol,
                                                      Subtract(Plus(Times(Sqr(a), d),
                                                          Times(Sqr(b), d, Plus(m, C1))),
                                                          Times(a, b, c, Plus(m, C2)))),
                                                  Times(Subtract(Times(b, c), Times(a, d)),
                                                      Plus(Times(ASymbol, Sqr(b), Plus(m, C2)),
                                                          Times(CSymbol,
                                                              Plus(Sqr(a),
                                                                  Times(Sqr(b), Plus(m, C1))))))),
                                              Sin(Plus(e, Times(f, x))))),
                                      Times(b, CSymbol, d, Plus(m, C1), Subtract(Sqr(a), Sqr(b)),
                                          Sqr(Sin(Plus(e, Times(f, x)))))),
                                  x)),
                          x),
                      x)),
              And(FreeQ(List(a, b, c, d, e, f, ASymbol, BSymbol, CSymbol), x),
                  NeQ(Subtract(Times(b, c), Times(a,
                      d)), C0),
                  NeQ(Subtract(Sqr(a), Sqr(b)), C0), LtQ(m, CN1)))),
      IIntegrate(3032,
          Integrate(
              Times(
                  Power(
                      Plus(
                          a_DEFAULT, Times(b_DEFAULT,
                              $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      m_),
                  Plus(
                      c_DEFAULT, Times(d_DEFAULT,
                          $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                  Plus(
                      A_DEFAULT, Times(C_DEFAULT, Sqr($($s("§sin"),
                          Plus(e_DEFAULT, Times(f_DEFAULT, x_))))))),
              x_Symbol),
          Condition(
              Plus(
                  Negate(
                      Simp(
                          Times(Subtract(Times(b, c), Times(a, d)),
                              Plus(Times(ASymbol, Sqr(b)), Times(Sqr(
                                  a), CSymbol)),
                              Cos(Plus(e, Times(f, x))),
                              Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), Plus(m,
                                  C1)),
                              Power(Times(Sqr(b), f, Plus(m, C1), Subtract(Sqr(a), Sqr(b))), CN1)),
                          x)),
                  Dist(
                      Power(Times(Sqr(b), Plus(m, C1),
                          Subtract(Sqr(a), Sqr(b))), CN1),
                      Integrate(
                          Times(
                              Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), Plus(m,
                                  C1)),
                              Simp(Plus(
                                  Times(b, Plus(m, C1),
                                      Plus(Times(a, CSymbol, Subtract(Times(b, c), Times(a, d))),
                                          Times(ASymbol, b, Subtract(Times(a, c), Times(b, d))))),
                                  Times(CN1, Subtract(Times(b, c), Times(a, d)),
                                      Plus(Times(ASymbol, Sqr(b), Plus(m, C2)), Times(CSymbol,
                                          Plus(Sqr(a), Times(Sqr(b), Plus(m, C1))))),
                                      Sin(Plus(e, Times(f, x)))),
                                  Times(
                                      b, CSymbol, d, Plus(m, C1), Subtract(Sqr(a), Sqr(b)), Sqr(
                                          Sin(Plus(e, Times(f, x)))))),
                                  x)),
                          x),
                      x)),
              And(FreeQ(List(a, b, c, d, e, f, ASymbol, CSymbol), x),
                  NeQ(Subtract(Times(b, c), Times(a, d)), C0), NeQ(Subtract(Sqr(a), Sqr(b)),
                      C0),
                  LtQ(m, CN1)))),
      IIntegrate(3033,
          Integrate(
              Times(
                  Power(
                      Plus(
                          a_DEFAULT, Times(b_DEFAULT, $($s("§sin"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      m_DEFAULT),
                  Plus(c_, Times(d_DEFAULT,
                      $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                  Plus(A_DEFAULT,
                      Times(B_DEFAULT, $($s("§sin"),
                          Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                      Times(C_DEFAULT, Sqr($($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))))),
              x_Symbol),
          Condition(
              Plus(
                  Negate(
                      Simp(
                          Times(
                              CSymbol, d, Cos(Plus(e, Times(f,
                                  x))),
                              Sin(Plus(e, Times(f, x))),
                              Power(Plus(a, Times(b,
                                  Sin(Plus(e, Times(f, x))))), Plus(m,
                                      C1)),
                              Power(Times(b, f, Plus(m, C3)), CN1)),
                          x)),
                  Dist(Power(Times(b, Plus(m, C3)), CN1),
                      Integrate(
                          Times(
                              Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), m), Simp(
                                  Subtract(
                                      Plus(
                                          Times(a, CSymbol, d), Times(ASymbol, b, c, Plus(m, C3)),
                                          Times(
                                              b,
                                              Plus(Times(BSymbol, c, Plus(m, C3)),
                                                  Times(d,
                                                      Plus(Times(CSymbol, Plus(m, C2)),
                                                          Times(ASymbol, Plus(m, C3))))),
                                              Sin(Plus(e, Times(f, x))))),
                                      Times(Subtract(Times(C2, a, CSymbol, d),
                                          Times(b, Plus(Times(c, CSymbol), Times(BSymbol, d)),
                                              Plus(m, C3))),
                                          Sqr(Sin(Plus(e, Times(f, x)))))),
                                  x)),
                          x),
                      x)),
              And(FreeQ(List(a, b, c, d, e, f, ASymbol, BSymbol, CSymbol, m), x),
                  NeQ(Subtract(Times(b, c),
                      Times(a, d)), C0),
                  NeQ(Subtract(Sqr(a), Sqr(b)), C0), Not(LtQ(m, CN1))))),
      IIntegrate(3034,
          Integrate(
              Times(
                  Power(
                      Plus(a_DEFAULT, Times(b_DEFAULT,
                          $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      m_DEFAULT),
                  Plus(c_, Times(d_DEFAULT,
                      $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                  Plus(
                      A_DEFAULT, Times(C_DEFAULT, Sqr($($s("§sin"),
                          Plus(e_DEFAULT, Times(f_DEFAULT, x_))))))),
              x_Symbol),
          Condition(
              Plus(
                  Negate(
                      Simp(
                          Times(CSymbol, d, Cos(Plus(e, Times(f, x))), Sin(Plus(e, Times(f, x))),
                              Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), Plus(m,
                                  C1)),
                              Power(Times(b, f, Plus(m, C3)), CN1)),
                          x)),
                  Dist(Power(Times(b, Plus(m, C3)), CN1),
                      Integrate(
                          Times(Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), m),
                              Simp(Subtract(
                                  Plus(Times(a, CSymbol, d), Times(ASymbol, b, c, Plus(m, C3)),
                                      Times(b, d,
                                          Plus(Times(CSymbol, Plus(m, C2)),
                                              Times(ASymbol, Plus(m, C3))),
                                          Sin(Plus(e, Times(f, x))))),
                                  Times(
                                      Subtract(Times(C2, a, CSymbol, d),
                                          Times(b, c, CSymbol, Plus(m, C3))),
                                      Sqr(Sin(Plus(e, Times(f, x)))))),
                                  x)),
                          x),
                      x)),
              And(FreeQ(List(a, b, c, d, e, f, ASymbol, CSymbol, m), x),
                  NeQ(Subtract(Times(b, c), Times(a,
                      d)), C0),
                  NeQ(Subtract(Sqr(a), Sqr(b)), C0), Not(LtQ(m, CN1))))),
      IIntegrate(3035,
          Integrate(
              Times(
                  Power(
                      Plus(a_, Times(b_DEFAULT,
                          $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      m_),
                  Power(
                      Plus(
                          c_DEFAULT, Times(d_DEFAULT, $($s("§sin"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      n_DEFAULT),
                  Plus(A_DEFAULT,
                      Times(B_DEFAULT, $($s("§sin"),
                          Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                      Times(C_DEFAULT, Sqr($($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))))),
              x_Symbol),
          Condition(
              Subtract(
                  Simp(
                      Times(Plus(Times(a, ASymbol), Times(CN1, b, BSymbol), Times(a, CSymbol)),
                          Cos(Plus(e, Times(f, x))),
                          Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), m),
                          Power(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))), Plus(n, C1)), Power(
                              Times(C2, b, c, f, Plus(Times(C2, m), C1)), CN1)),
                      x),
                  Dist(Power(Times(C2, b, c, d, Plus(Times(C2, m), C1)), CN1),
                      Integrate(
                          Times(Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), Plus(m, C1)),
                              Power(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))), n),
                              Simp(
                                  Plus(
                                      Times(ASymbol,
                                          Plus(
                                              Times(Sqr(c), Plus(m, C1)), Times(Sqr(d),
                                                  Plus(Times(C2, m), n, C2)))),
                                      Times(CN1, BSymbol, c, d, Subtract(Subtract(m, n), C1)),
                                      Times(CN1, CSymbol,
                                          Subtract(Times(Sqr(c), m), Times(Sqr(d), Plus(n, C1)))),
                                      Times(d,
                                          Subtract(
                                              Times(Plus(Times(ASymbol, c), Times(BSymbol, d)),
                                                  Plus(m, n, C2)),
                                              Times(c, CSymbol, Subtract(Times(C3, m), n))),
                                          Sin(Plus(e, Times(f, x))))),
                                  x)),
                          x),
                      x)),
              And(FreeQ(List(a, b, c, d, e, f, ASymbol, BSymbol, CSymbol, m, n), x),
                  EqQ(Plus(Times(b, c), Times(a,
                      d)), C0),
                  EqQ(Subtract(Sqr(a),
                      Sqr(b)), C0),
                  Or(LtQ(m, Negate(C1D2)),
                      And(EqQ(Plus(m, n, C2), C0), NeQ(Plus(Times(C2, m), C1), C0)))))),
      IIntegrate(3036,
          Integrate(
              Times(
                  Power(
                      Plus(
                          a_, Times(b_DEFAULT,
                              $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      m_),
                  Power(Plus(c_DEFAULT, Times(d_DEFAULT,
                      $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))), n_DEFAULT),
                  Plus(
                      A_DEFAULT, Times(C_DEFAULT, Sqr($($s("§sin"),
                          Plus(e_DEFAULT, Times(f_DEFAULT, x_))))))),
              x_Symbol),
          Condition(
              Subtract(
                  Simp(
                      Times(Plus(Times(a, ASymbol), Times(a, CSymbol)), Cos(Plus(e, Times(f, x))),
                          Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), m),
                          Power(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))), Plus(n, C1)), Power(
                              Times(C2, b, c, f, Plus(Times(C2, m), C1)), CN1)),
                      x),
                  Dist(Power(Times(C2, b, c, d, Plus(Times(C2, m), C1)), CN1),
                      Integrate(
                          Times(Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), Plus(m, C1)),
                              Power(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))), n),
                              Simp(Plus(
                                  Times(ASymbol,
                                      Plus(Times(Sqr(c), Plus(m, C1)),
                                          Times(Sqr(d), Plus(Times(C2, m), n, C2)))),
                                  Times(CN1, CSymbol,
                                      Subtract(Times(Sqr(c), m), Times(Sqr(d), Plus(n, C1)))),
                                  Times(d,
                                      Subtract(Times(ASymbol, c, Plus(m, n, C2)),
                                          Times(c, CSymbol, Subtract(Times(C3, m), n))),
                                      Sin(Plus(e, Times(f, x))))),
                                  x)),
                          x),
                      x)),
              And(FreeQ(List(a, b, c, d, e, f, ASymbol, CSymbol, m, n), x),
                  EqQ(Plus(Times(b, c), Times(a, d)), C0), EqQ(Subtract(Sqr(a),
                      Sqr(b)), C0),
                  Or(LtQ(m, Negate(C1D2)),
                      And(EqQ(Plus(m, n, C2), C0), NeQ(Plus(Times(C2, m), C1), C0)))))),
      IIntegrate(3037,
          Integrate(
              Times(
                  Power(
                      Plus(a_DEFAULT, Times(b_DEFAULT,
                          $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      m_DEFAULT),
                  Power(
                      Plus(
                          c_DEFAULT, Times(d_DEFAULT,
                              $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      CN1D2),
                  Plus(A_DEFAULT,
                      Times(B_DEFAULT, $($s("§sin"),
                          Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                      Times(C_DEFAULT, Sqr($($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))))),
              x_Symbol),
          Condition(
              Plus(
                  Simp(
                      Times(CN2, CSymbol, Cos(Plus(e, Times(f, x))),
                          Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), Plus(m, C1)),
                          Power(
                              Times(b, f, Plus(Times(C2, m), C3),
                                  Sqrt(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))))),
                              CN1)),
                      x),
                  Integrate(Times(Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), m),
                      Simp(Plus(ASymbol, CSymbol, Times(BSymbol, Sin(Plus(e, Times(f, x))))),
                          x),
                      Power(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))), CN1D2)), x)),
              And(FreeQ(List(a, b, c, d, e, f, ASymbol, BSymbol, CSymbol, m), x),
                  EqQ(Plus(Times(b, c), Times(a,
                      d)), C0),
                  EqQ(Subtract(Sqr(a), Sqr(b)), C0), Not(LtQ(m, Negate(C1D2)))))),
      IIntegrate(3038,
          Integrate(
              Times(
                  Power(
                      Plus(
                          a_DEFAULT, Times(b_DEFAULT, $($s("§sin"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      m_DEFAULT),
                  Power(
                      Plus(
                          c_DEFAULT, Times(d_DEFAULT,
                              $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      CN1D2),
                  Plus(
                      A_DEFAULT, Times(C_DEFAULT, Sqr($($s("§sin"),
                          Plus(e_DEFAULT, Times(f_DEFAULT, x_))))))),
              x_Symbol),
          Condition(
              Plus(
                  Simp(Times(CN2, CSymbol, Cos(Plus(e, Times(f, x))),
                      Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), Plus(m, C1)),
                      Power(Times(b, f, Plus(Times(C2, m), C3),
                          Sqrt(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))))), CN1)),
                      x),
                  Dist(Plus(ASymbol, CSymbol),
                      Integrate(
                          Times(
                              Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), m), Power(Plus(c,
                                  Times(d, Sin(Plus(e, Times(f, x))))), CN1D2)),
                          x),
                      x)),
              And(FreeQ(List(a, b, c, d, e, f, ASymbol, CSymbol, m), x),
                  EqQ(Plus(Times(b, c), Times(a, d)), C0), EqQ(Subtract(Sqr(a), Sqr(b)),
                      C0),
                  Not(LtQ(m, Negate(C1D2)))))),
      IIntegrate(3039,
          Integrate(
              Times(
                  Power(
                      Plus(a_, Times(b_DEFAULT,
                          $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      m_DEFAULT),
                  Power(
                      Plus(
                          c_DEFAULT, Times(d_DEFAULT, $($s("§sin"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      n_DEFAULT),
                  Plus(A_DEFAULT,
                      Times(B_DEFAULT, $($s("§sin"),
                          Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                      Times(C_DEFAULT, Sqr($($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))))),
              x_Symbol),
          Condition(
              Plus(
                  Negate(
                      Simp(
                          Times(
                              CSymbol, Cos(Plus(e, Times(f,
                                  x))),
                              Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), m),
                              Power(Plus(c, Times(d,
                                  Sin(Plus(e, Times(f, x))))), Plus(n,
                                      C1)),
                              Power(Times(d, f, Plus(m, n, C2)), CN1)),
                          x)),
                  Dist(Power(Times(b, d, Plus(m, n, C2)), CN1),
                      Integrate(Times(Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), m),
                          Power(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))), n),
                          Simp(
                              Plus(Times(ASymbol, b, d, Plus(m, n, C2)),
                                  Times(CSymbol, Plus(Times(a, c, m), Times(b, d, Plus(n, C1)))),
                                  Times(
                                      Subtract(Times(b, BSymbol, d, Plus(m, n, C2)),
                                          Times(b, c, CSymbol, Plus(Times(C2, m), C1))),
                                      Sin(Plus(e, Times(f, x))))),
                              x)),
                          x),
                      x)),
              And(FreeQ(List(a, b, c, d, e, f, ASymbol, BSymbol, CSymbol, m, n), x),
                  EqQ(Plus(Times(b, c), Times(a, d)), C0), EqQ(Subtract(Sqr(a), Sqr(b)),
                      C0),
                  Not(LtQ(m, Negate(C1D2))), NeQ(Plus(m, n, C2), C0)))),
      IIntegrate(3040,
          Integrate(
              Times(
                  Power(
                      Plus(
                          a_, Times(b_DEFAULT, $($s("§sin"),
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      m_DEFAULT),
                  Power(
                      Plus(c_DEFAULT,
                          Times(d_DEFAULT, $($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))),
                      n_DEFAULT),
                  Plus(A_DEFAULT,
                      Times(C_DEFAULT, Sqr($($s("§sin"), Plus(e_DEFAULT, Times(f_DEFAULT, x_))))))),
              x_Symbol),
          Condition(
              Plus(
                  Negate(
                      Simp(
                          Times(
                              CSymbol, Cos(Plus(e, Times(f,
                                  x))),
                              Power(Plus(a, Times(b, Sin(Plus(e, Times(f, x))))), m),
                              Power(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))), Plus(n,
                                  C1)),
                              Power(Times(d, f, Plus(m, n, C2)), CN1)),
                          x)),
                  Dist(
                      Power(Times(b, d,
                          Plus(m, n, C2)), CN1),
                      Integrate(
                          Times(
                              Power(Plus(a, Times(b,
                                  Sin(Plus(e, Times(f, x))))), m),
                              Power(Plus(c, Times(d, Sin(Plus(e, Times(f, x))))), n),
                              Simp(Subtract(
                                  Plus(Times(ASymbol, b, d, Plus(m, n, C2)),
                                      Times(CSymbol,
                                          Plus(Times(a, c, m), Times(b, d, Plus(n, C1))))),
                                  Times(b, c, CSymbol, Plus(Times(C2, m), C1),
                                      Sin(Plus(e, Times(f, x))))),
                                  x)),
                          x),
                      x)),
              And(FreeQ(List(a, b, c, d, e, f, ASymbol, CSymbol, m, n), x),
                  EqQ(Plus(Times(b, c), Times(a, d)), C0), EqQ(Subtract(Sqr(a), Sqr(b)), C0),
                  Not(LtQ(m, Negate(C1D2))), NeQ(Plus(m, n, C2), C0)))));
}
