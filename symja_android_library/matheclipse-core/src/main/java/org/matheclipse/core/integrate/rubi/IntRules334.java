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
class IntRules334 {
  public static IAST RULES =
      List(
          IIntegrate(6680,
              Integrate(
                  Times(
                      Power(Plus(A_DEFAULT,
                          Times(C_DEFAULT, Sqr(x_))), CN1),
                      Power(
                          Plus(a_DEFAULT,
                              Times(
                                  b_DEFAULT, $(
                                      F_, Times(c_DEFAULT,
                                          Sqrt(Plus(d_DEFAULT, Times(e_DEFAULT,
                                              x_))),
                                          Power(Plus(f_DEFAULT, Times(g_DEFAULT, x_)), CN1D2))))),
                          n_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(C2, e, g, Power(Times(CSymbol, Subtract(Times(e, f), Times(d, g))),
                          CN1)),
                      Subst(
                          Integrate(Times(Power(Plus(a, Times(b, F(Times(c, x)))), n),
                              Power(x, CN1)), x),
                          x, Times(Sqrt(Plus(d, Times(e, x))), Power(Plus(f, Times(g, x)), CN1D2))),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g, ASymbol, CSymbol, FSymbol), x),
                      EqQ(Subtract(Times(CSymbol, d, f), Times(ASymbol, e, g)),
                          C0),
                      EqQ(Plus(Times(e, f), Times(d, g)), C0), IGtQ(n, C0)))),
          IIntegrate(6681,
              Integrate(
                  Times(
                      Power(Plus(A_DEFAULT, Times(B_DEFAULT, x_),
                          Times(C_DEFAULT, Sqr(x_))), CN1),
                      Power(
                          Plus(a_DEFAULT,
                              Times(
                                  b_DEFAULT, $(
                                      F_,
                                      Times(
                                          c_DEFAULT, Sqrt(Plus(d_DEFAULT, Times(e_DEFAULT, x_))),
                                          Power(Plus(f_DEFAULT, Times(g_DEFAULT, x_)), CN1D2))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Unintegrable(
                      Times(
                          Power(Plus(a,
                              Times(b,
                                  F(Times(c, Sqrt(Plus(d, Times(e, x))),
                                      Power(Plus(f, Times(g, x)), CN1D2))))),
                              n),
                          Power(Plus(ASymbol, Times(BSymbol, x), Times(CSymbol, Sqr(x))), CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g, ASymbol, BSymbol, CSymbol, FSymbol, n), x),
                      EqQ(Subtract(Times(CSymbol, d, f),
                          Times(ASymbol, e, g)), C0),
                      EqQ(Subtract(Times(BSymbol, e, g),
                          Times(CSymbol, Plus(Times(e, f), Times(d, g)))), C0),
                      Not(IGtQ(n, C0))))),
          IIntegrate(6682,
              Integrate(
                  Times(
                      Power(Plus(A_,
                          Times(C_DEFAULT, Sqr(x_))), CN1),
                      Power(
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT, $(F_,
                                  Times(c_DEFAULT, Sqrt(Plus(d_DEFAULT, Times(e_DEFAULT, x_))),
                                      Power(Plus(f_DEFAULT, Times(g_DEFAULT, x_)), CN1D2))))),
                          n_)),
                  x_Symbol),
              Condition(
                  Unintegrable(Times(
                      Power(Plus(a,
                          Times(b,
                              F(Times(c, Sqrt(Plus(d, Times(e, x))),
                                  Power(Plus(f, Times(g, x)), CN1D2))))),
                          n),
                      Power(Plus(ASymbol, Times(CSymbol, Sqr(x))), CN1)), x),
                  And(FreeQ(List(a, b, c, d, e, f, g, ASymbol, CSymbol, FSymbol, n), x),
                      EqQ(Subtract(Times(CSymbol, d, f), Times(ASymbol, e, g)), C0), EqQ(
                          Plus(Times(e, f), Times(d, g)), C0),
                      Not(IGtQ(n, C0))))),
          IIntegrate(6683, Integrate(Times(u_, Power(y_, CN1)), x_Symbol), With(
              List(Set(q, DerivativeDivides(y, u, x))),
              Condition(Simp(Times(q, Log(RemoveContent(y, x))), x), Not(FalseQ(q))))),
          IIntegrate(6684, Integrate(Times(u_, Power(w_, CN1), Power(y_, CN1)), x_Symbol),
              With(List(Set(q, DerivativeDivides(Times(y, w), u, x))),
                  Condition(Simp(Times(q, Log(RemoveContent(Times(y, w), x))), x),
                      Not(FalseQ(q))))),
          IIntegrate(6685, Integrate(Times(u_, Power(y_, m_DEFAULT)), x_Symbol),
              Condition(
                  With(List(Set(q, DerivativeDivides(y, u, x))),
                      Condition(Simp(Times(q, Power(y, Plus(m, C1)), Power(Plus(m, C1), CN1)), x),
                          Not(FalseQ(q)))),
                  And(FreeQ(m, x), NeQ(m, CN1)))),
          IIntegrate(6686,
              Integrate(Times(u_, Power(y_, m_DEFAULT),
                  Power(z_, n_DEFAULT)), x_Symbol),
              Condition(
                  With(
                      List(Set(q,
                          DerivativeDivides(Times(y, z), Times(u, Power(z, Subtract(n, m))), x))),
                      Condition(
                          Simp(Times(q, Power(y, Plus(m, C1)), Power(z, Plus(m, C1)),
                              Power(Plus(m, C1), CN1)), x),
                          Not(FalseQ(q)))),
                  And(FreeQ(List(m, n), x), NeQ(m, CN1)))),
          IIntegrate(6687, Integrate(u_, x_Symbol),
              With(List(Set(v, SimplifyIntegrand(u, x))),
                  Condition(Integrate(v, x), SimplerIntegrandQ(v, u, x)))),
          IIntegrate(6688,
              Integrate(
                  Times(u_DEFAULT,
                      Power(
                          Plus(
                              Times(
                                  e_DEFAULT,
                                  Sqrt(Plus(a_DEFAULT, Times(b_DEFAULT, Power(x_, n_DEFAULT))))),
                              Times(
                                  f_DEFAULT, Sqrt(Plus(c_DEFAULT, Times(d_DEFAULT,
                                      Power(x_, n_DEFAULT)))))),
                          m_)),
                  x_Symbol),
              Condition(
                  Dist(Power(Subtract(Times(a, Sqr(e)), Times(c, Sqr(f))), m),
                      Integrate(
                          ExpandIntegrand(
                              Times(u,
                                  Power(Power(
                                      Subtract(Times(e, Sqrt(Plus(a, Times(b, Power(x, n))))),
                                          Times(f, Sqrt(Plus(c, Times(d, Power(x, n)))))),
                                      m), CN1)),
                              x),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f,
                      n), x), ILtQ(m,
                          C0),
                      EqQ(Subtract(Times(b, Sqr(e)), Times(d, Sqr(f))), C0)))),
          IIntegrate(6689,
              Integrate(
                  Times(u_DEFAULT,
                      Power(
                          Plus(
                              Times(
                                  e_DEFAULT, Sqrt(Plus(a_DEFAULT,
                                      Times(b_DEFAULT, Power(x_, n_DEFAULT))))),
                              Times(
                                  f_DEFAULT, Sqrt(Plus(c_DEFAULT, Times(d_DEFAULT,
                                      Power(x_, n_DEFAULT)))))),
                          m_)),
                  x_Symbol),
              Condition(Dist(Power(Subtract(Times(b, Sqr(e)), Times(d, Sqr(f))), m), Integrate(
                  ExpandIntegrand(
                      Times(u, Power(x, Times(m, n)),
                          Power(Power(Subtract(Times(e, Sqrt(Plus(a, Times(b, Power(x, n))))),
                              Times(f, Sqrt(Plus(c, Times(d, Power(x, n)))))), m), CN1)),
                      x),
                  x), x), And(
                      FreeQ(List(a, b, c, d, e, f, n), x), ILtQ(m,
                          C0),
                      EqQ(Subtract(Times(a, Sqr(e)), Times(c, Sqr(f))), C0)))),
          IIntegrate(6690,
              Integrate(
                  Times(
                      Power(u_, m_DEFAULT), Power(Plus(Times(a_DEFAULT, Power(u_, n_)), v_),
                          p_DEFAULT),
                      w_),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(
                          Power(u, Plus(m,
                              Times(n, p))),
                          Power(Plus(a, Times(v, Power(Power(u, n), CN1))), p), w),
                      x),
                  And(FreeQ(List(a, m, n), x), IntegerQ(p), Not(GtQ(n, C0)), Not(FreeQ(v, x))))),
          IIntegrate(6691,
              Integrate(
                  Times(
                      u_, Power(Plus(c_DEFAULT, Times(d_DEFAULT, v_)), n_DEFAULT), Power(
                          Plus(a_DEFAULT, Times(b_DEFAULT, y_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(q,
                          DerivativeDivides(y, u, x))),
                      Condition(
                          Dist(q,
                              Subst(
                                  Integrate(Times(Power(Plus(a, Times(b, x)), m),
                                      Power(Plus(c, Times(d, x)), n)), x),
                                  x, y),
                              x),
                          Not(FalseQ(q)))),
                  And(FreeQ(List(a, b, c, d, m, n), x), EqQ(v, y)))),
          IIntegrate(6692,
              Integrate(
                  Times(u_, Power(Plus(c_DEFAULT, Times(d_DEFAULT, v_)), n_DEFAULT),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, w_)), p_DEFAULT),
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, y_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(List(Set(q, DerivativeDivides(y, u, x))),
                      Condition(
                          Dist(q,
                              Subst(Integrate(Times(Power(Plus(a, Times(b, x)), m),
                                  Power(Plus(c, Times(d, x)), n), Power(Plus(e, Times(f, x)), p)),
                                  x), x, y),
                              x),
                          Not(FalseQ(q)))),
                  And(FreeQ(List(a, b, c, d, e, f, m, n, p), x), EqQ(v, y), EqQ(w, y)))),
          IIntegrate(6693,
              Integrate(
                  Times(u_, Power(Plus(c_DEFAULT, Times(d_DEFAULT, v_)), n_DEFAULT),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, w_)), p_DEFAULT),
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, y_)), m_DEFAULT), Power(
                          Plus(g_DEFAULT, Times(h_DEFAULT, z_)), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(List(Set(r, DerivativeDivides(y, u, x))),
                      Condition(
                          Dist(r,
                              Subst(Integrate(Times(Power(Plus(a, Times(b, x)), m),
                                  Power(Plus(c, Times(d, x)), n), Power(Plus(e, Times(f, x)), p),
                                  Power(Plus(g, Times(h, x)), q)), x), x, y),
                              x),
                          Not(FalseQ(r)))),
                  And(FreeQ(List(a, b, c, d, e, f, g, h, m, n, p, q), x), EqQ(v, y), EqQ(w, y),
                      EqQ(z, y)))),
          IIntegrate(6694,
              Integrate(Times(u_DEFAULT,
                  Plus(a_, Times(b_DEFAULT, Power(y_, n_)))), x_Symbol),
              Condition(
                  With(
                      List(Set(q,
                          DerivativeDivides(y, u, x))),
                      Condition(
                          Plus(Dist(a, Integrate(u, x), x), Dist(Times(b, q),
                              Subst(Integrate(Power(x, n), x), x, y), x)),
                          Not(FalseQ(q)))),
                  FreeQ(List(a, b, n), x))),
          IIntegrate(6695,
              Integrate(
                  Times(u_DEFAULT, Power(Plus(a_DEFAULT, Times(b_DEFAULT, Power(y_, n_))),
                      p_)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(q,
                          DerivativeDivides(y, u, x))),
                      Condition(
                          Dist(q,
                              Subst(Integrate(Power(Plus(a, Times(b, Power(x, n))), p), x), x,
                                  y),
                              x),
                          Not(FalseQ(q)))),
                  FreeQ(List(a, b, n, p), x))),
          IIntegrate(6696,
              Integrate(
                  Times(
                      u_DEFAULT, Power(v_, m_DEFAULT),
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, Power(y_, n_))), p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Module(List(q, r),
                      Condition(Dist(Times(q, r),
                          Subst(
                              Integrate(
                                  Times(Power(x, m), Power(Plus(a, Times(b, Power(x, n))), p)), x),
                              x, y),
                          x),
                          And(Not(FalseQ(Set(r, Divides(Power(y, m), Power(v, m), x)))),
                              Not(FalseQ(Set(q, DerivativeDivides(y, u, x))))))),
                  FreeQ(List(a, b, m, n, p), x))),
          IIntegrate(6697,
              Integrate(
                  Times(u_DEFAULT,
                      Power(
                          Plus(
                              a_DEFAULT, Times(c_DEFAULT, Power(v_,
                                  $p("n2", true))),
                              Times(b_DEFAULT, Power(y_, n_))),
                          p_)),
                  x_Symbol),
              Condition(With(List(Set(q, DerivativeDivides(y, u, x))),
                  Condition(Dist(q,
                      Subst(Integrate(Power(
                          Plus(a, Times(b, Power(x, n)), Times(c, Power(x, Times(C2, n)))), p), x),
                          x, y),
                      x), Not(FalseQ(q)))),
                  And(FreeQ(List(a, b, c, n, p), x), EqQ($s("n2"), Times(C2, n)), EqQ(v, y)))),
          IIntegrate(6698,
              Integrate(
                  Times(u_DEFAULT,
                      Power(
                          Plus(
                              a_DEFAULT, Times(b_DEFAULT, Power(v_,
                                  n_)),
                              Times(c_DEFAULT, Power(w_, $p("n2", true)))),
                          p_DEFAULT),
                      Plus(A_, Times(B_DEFAULT, Power(y_, n_)))),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(q,
                          DerivativeDivides(y, u, x))),
                      Condition(
                          Dist(q,
                              Subst(
                                  Integrate(Times(Plus(ASymbol, Times(BSymbol, Power(x, n))),
                                      Power(Plus(a, Times(b, Power(x, n)),
                                          Times(c, Power(x, Times(C2, n)))), p)),
                                      x),
                                  x, y),
                              x),
                          Not(FalseQ(q)))),
                  And(FreeQ(List(a, b, c, ASymbol, BSymbol, n, p), x), EqQ($s("n2"), Times(C2,
                      n)), EqQ(v,
                          y),
                      EqQ(w, y)))),
          IIntegrate(6699,
              Integrate(
                  Times(u_DEFAULT,
                      Power(Plus(a_DEFAULT,
                          Times(c_DEFAULT, Power(w_, $p("n2", true)))), p_DEFAULT),
                      Plus(A_, Times(B_DEFAULT, Power(y_, n_)))),
                  x_Symbol),
              Condition(
                  With(List(Set(q, DerivativeDivides(y, u, x))), Condition(
                      Dist(q,
                          Subst(Integrate(Times(Plus(ASymbol, Times(BSymbol, Power(x, n))),
                              Power(Plus(a, Times(c, Power(x, Times(C2, n)))), p)), x), x, y),
                          x),
                      Not(FalseQ(q)))),
                  And(FreeQ(List(a, c, ASymbol, BSymbol, n, p), x), EqQ($s("n2"), Times(C2, n)),
                      EqQ(w, y)))));
}
