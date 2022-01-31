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
class IntRules52 {
  public static IAST RULES =
      List(
          IIntegrate(1041,
              Integrate(
                  Times(
                      Plus(g_DEFAULT, Times(h_DEFAULT,
                          x_)),
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_),
                          Times(c_DEFAULT, Sqr(x_))), CN1D3),
                      Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_), Times(f_DEFAULT, Sqr(x_))), CN1)),
                  x_Symbol),
              Condition(
                  With(List(Set(q, Times(CN1, c, Power(Subtract(Sqr(b), Times(C4, a, c)), CN1)))),
                      Dist(
                          Times(
                              Power(Times(q,
                                  Plus(a, Times(b, x), Times(c, Sqr(x)))), C1D3),
                              Power(Plus(a, Times(b, x), Times(c, Sqr(x))), CN1D3)),
                          Integrate(Times(Plus(g, Times(h, x)), Power(Times(
                              Power(Plus(Times(q, a), Times(b, q, x), Times(c, q, Sqr(x))), C1D3),
                              Plus(d, Times(e, x), Times(f, Sqr(x)))), CN1)), x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g, h), x),
                      EqQ(Subtract(Times(c, e), Times(b, f)), C0),
                      EqQ(Subtract(Times(Sqr(c), d), Times(f, Subtract(Sqr(b), Times(C3, a, c)))),
                          C0),
                      EqQ(Plus(
                          Times(Sqr(c), Sqr(g)), Times(CN1, b, c, g, h),
                          Times(CN1, C2, Sqr(b), Sqr(h)), Times(C9, a, c, Sqr(h))), C0),
                      Not(GtQ(Subtract(Times(C4, a), Times(Sqr(b), Power(c, CN1))), C0))))),
          IIntegrate(1042,
              Integrate(
                  Times(Plus(g_DEFAULT, Times(h_DEFAULT, x_)),
                      Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), p_),
                      Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_), Times(f_DEFAULT, Sqr(x_))), q_)),
                  x_Symbol),
              Condition(
                  Unintegrable(Times(Plus(g, Times(h, x)),
                      Power(Plus(a, Times(b, x), Times(c, Sqr(x))),
                          p),
                      Power(Plus(d, Times(e, x), Times(f, Sqr(x))), q)), x),
                  FreeQ(List(a, b, c, d, e, f, g, h, p, q), x))),
          IIntegrate(1043,
              Integrate(
                  Times(Plus(g_DEFAULT, Times(h_DEFAULT, x_)),
                      Power(Plus(a_DEFAULT, Times(c_DEFAULT, Sqr(x_))), p_),
                      Power(Plus(d_DEFAULT, Times(e_DEFAULT, x_), Times(f_DEFAULT, Sqr(x_))), q_)),
                  x_Symbol),
              Condition(
                  Unintegrable(
                      Times(Plus(g, Times(h, x)), Power(Plus(a, Times(c, Sqr(x))), p),
                          Power(Plus(d, Times(e, x), Times(f, Sqr(x))), q)),
                      x),
                  FreeQ(List(a, c, d, e, f, g, h, p, q), x))),
          IIntegrate(1044,
              Integrate(
                  Times(Power(Plus(g_DEFAULT, Times(h_DEFAULT, u_)), m_DEFAULT),
                      Power(
                          Plus(a_DEFAULT, Times(b_DEFAULT, u_),
                              Times(c_DEFAULT, Sqr(u_))),
                          p_DEFAULT),
                      Power(Plus(d_DEFAULT, Times(e_DEFAULT, u_), Times(f_DEFAULT, Sqr(u_))),
                          q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(Coefficient(u, x, C1), CN1),
                      Subst(
                          Integrate(
                              Times(Power(Plus(g, Times(h, x)), m),
                                  Power(Plus(a, Times(b, x), Times(c, Sqr(x))), p),
                                  Power(Plus(d, Times(e, x), Times(f, Sqr(x))), q)),
                              x),
                          x, u),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g, h, m, p, q), x), LinearQ(u, x), NeQ(u, x)))),
          IIntegrate(1045,
              Integrate(
                  Times(Power(Plus(g_DEFAULT, Times(h_DEFAULT, u_)), m_DEFAULT),
                      Power(Plus(a_DEFAULT, Times(c_DEFAULT, Sqr(u_))), p_DEFAULT),
                      Power(Plus(d_DEFAULT, Times(e_DEFAULT, u_), Times(f_DEFAULT, Sqr(u_))),
                          q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(Coefficient(u, x, C1), CN1),
                      Subst(Integrate(
                          Times(Power(Plus(g, Times(h, x)), m), Power(Plus(a, Times(c, Sqr(x))), p),
                              Power(Plus(d, Times(e, x), Times(f, Sqr(x))), q)),
                          x), x, u),
                      x),
                  And(FreeQ(List(a, c, d, e, f, g, h, m, p, q), x), LinearQ(u, x), NeQ(u, x)))),
          IIntegrate(1046,
              Integrate(
                  Times(Power(Plus(a_, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), p_DEFAULT),
                      Plus(A_DEFAULT, Times(B_DEFAULT, x_),
                          Times(C_DEFAULT, Sqr(x_))),
                      Power(Plus(d_, Times(e_DEFAULT, x_), Times(f_DEFAULT, Sqr(x_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(Times(c, Power(f, CN1)), p),
                      Integrate(
                          Times(Power(Plus(d, Times(e, x), Times(f, Sqr(x))), Plus(p, q)),
                              Plus(ASymbol, Times(BSymbol, x), Times(CSymbol, Sqr(x)))),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, ASymbol, BSymbol, CSymbol, p, q), x),
                      EqQ(Subtract(Times(c, d), Times(a, f)), C0),
                      EqQ(Subtract(Times(b, d), Times(a, e)), C0),
                      Or(IntegerQ(p), GtQ(Times(c, Power(f, CN1)),
                          C0)),
                      Or(Not(IntegerQ(q)),
                          LessEqual(LeafCount(Plus(d, Times(e, x), Times(f, Sqr(x)))),
                              LeafCount(Plus(a, Times(b, x), Times(c, Sqr(x))))))))),
          IIntegrate(1047,
              Integrate(Times(
                  Power(Plus(a_, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), p_DEFAULT),
                  Plus(A_DEFAULT, Times(C_DEFAULT, Sqr(x_))), Power(Plus(d_, Times(e_DEFAULT, x_),
                      Times(f_DEFAULT, Sqr(x_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Power(Times(c,
                          Power(f, CN1)), p),
                      Integrate(
                          Times(
                              Power(Plus(d, Times(e, x), Times(f, Sqr(x))),
                                  Plus(p, q)),
                              Plus(ASymbol, Times(CSymbol, Sqr(x)))),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, ASymbol, CSymbol, p, q), x),
                      EqQ(Subtract(Times(c, d), Times(a, f)), C0),
                      EqQ(Subtract(Times(b, d), Times(a, e)), C0),
                      Or(IntegerQ(p), GtQ(Times(c, Power(f, CN1)), C0)),
                      Or(Not(IntegerQ(q)),
                          LessEqual(LeafCount(Plus(d, Times(e, x), Times(f, Sqr(x)))),
                              LeafCount(Plus(a, Times(b, x), Times(c, Sqr(x))))))))),
          IIntegrate(1048,
              Integrate(
                  Times(Power(Plus(a_, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), p_DEFAULT),
                      Plus(A_DEFAULT, Times(B_DEFAULT, x_), Times(C_DEFAULT,
                          Sqr(x_))),
                      Power(Plus(d_, Times(e_DEFAULT, x_), Times(f_DEFAULT, Sqr(x_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(a, IntPart(p)),
                          Power(Plus(a, Times(b, x), Times(c, Sqr(x))), FracPart(p)),
                          Power(
                              Times(Power(d, IntPart(p)),
                                  Power(Plus(d, Times(e, x), Times(f, Sqr(x))), FracPart(p))),
                              CN1)),
                      Integrate(
                          Times(Power(Plus(d, Times(e, x), Times(f, Sqr(x))), Plus(p, q)), Plus(
                              ASymbol, Times(BSymbol, x), Times(CSymbol, Sqr(x)))),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, ASymbol, BSymbol, CSymbol, p, q), x),
                      EqQ(Subtract(Times(c, d), Times(a, f)), C0),
                      EqQ(Subtract(Times(b, d), Times(a, e)), C0), Not(IntegerQ(p)), Not(IntegerQ(
                          q)),
                      Not(GtQ(Times(c, Power(f, CN1)), C0))))),
          IIntegrate(1049,
              Integrate(
                  Times(Power(Plus(a_, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), p_DEFAULT),
                      Plus(A_DEFAULT, Times(C_DEFAULT, Sqr(x_))), Power(Plus(d_,
                          Times(e_DEFAULT, x_), Times(f_DEFAULT, Sqr(x_))),
                          q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(a, IntPart(p)),
                          Power(Plus(a, Times(b, x), Times(c, Sqr(x))), FracPart(p)),
                          Power(
                              Times(Power(d, IntPart(p)),
                                  Power(Plus(d, Times(e, x), Times(f, Sqr(x))), FracPart(p))),
                              CN1)),
                      Integrate(
                          Times(
                              Power(Plus(d, Times(e, x), Times(f, Sqr(x))),
                                  Plus(p, q)),
                              Plus(ASymbol, Times(CSymbol, Sqr(x)))),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, ASymbol, CSymbol, p, q), x),
                      EqQ(Subtract(Times(c, d), Times(a, f)), C0),
                      EqQ(Subtract(Times(b, d), Times(a, e)), C0), Not(IntegerQ(p)), Not(
                          IntegerQ(q)),
                      Not(GtQ(Times(c, Power(f, CN1)), C0))))),
          IIntegrate(1050, Integrate(Times(
              Power(Plus(a_, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), p_DEFAULT),
              Plus(A_DEFAULT, Times(B_DEFAULT, x_), Times(C_DEFAULT, Sqr(x_))), Power(Plus(
                  d_DEFAULT, Times(e_DEFAULT, x_), Times(f_DEFAULT, Sqr(x_))),
                  q_DEFAULT)),
              x_Symbol),
              Condition(
                  Dist(Times(Power(Plus(a, Times(b, x), Times(c, Sqr(x))), FracPart(p)),
                      Power(
                          Times(Power(Times(C4, c), IntPart(p)),
                              Power(Plus(b, Times(C2, c, x)), Times(C2, FracPart(p)))),
                          CN1)),
                      Integrate(
                          Times(Power(Plus(b, Times(C2, c, x)), Times(C2, p)),
                              Power(Plus(d, Times(e, x), Times(f, Sqr(x))), q), Plus(ASymbol,
                                  Times(BSymbol, x), Times(CSymbol, Sqr(x)))),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, ASymbol, BSymbol, CSymbol, p,
                      q), x), EqQ(Subtract(Sqr(b), Times(C4, a, c)),
                          C0)))),
          IIntegrate(1051,
              Integrate(
                  Times(Power(Plus(a_, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), p_DEFAULT),
                      Plus(A_DEFAULT, Times(C_DEFAULT, Sqr(x_))),
                      Power(
                          Plus(d_DEFAULT, Times(e_DEFAULT, x_),
                              Times(f_DEFAULT, Sqr(x_))),
                          q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Plus(a, Times(b, x), Times(c, Sqr(x))), FracPart(p)),
                          Power(
                              Times(Power(Times(C4, c), IntPart(p)),
                                  Power(Plus(b, Times(C2, c, x)), Times(C2, FracPart(p)))),
                              CN1)),
                      Integrate(
                          Times(Power(Plus(b, Times(C2, c, x)), Times(C2, p)),
                              Power(Plus(d, Times(e, x), Times(f, Sqr(x))), q), Plus(ASymbol,
                                  Times(CSymbol, Sqr(x)))),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, ASymbol, CSymbol, p,
                      q), x), EqQ(Subtract(Sqr(b), Times(C4, a, c)),
                          C0)))),
          IIntegrate(1052,
              Integrate(
                  Times(Power(Plus(a_, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), p_DEFAULT),
                      Plus(A_DEFAULT, Times(B_DEFAULT, x_), Times(C_DEFAULT, Sqr(x_))), Power(Plus(
                          d_DEFAULT, Times(f_DEFAULT, Sqr(x_))), q_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Plus(a, Times(b, x), Times(c, Sqr(x))), FracPart(p)),
                          Power(Times(Power(Times(C4, c), IntPart(p)),
                              Power(Plus(b, Times(C2, c, x)), Times(C2, FracPart(p)))),
                              CN1)),
                      Integrate(Times(Power(Plus(b, Times(C2, c, x)), Times(C2, p)),
                          Power(Plus(d, Times(f, Sqr(x))), q),
                          Plus(ASymbol, Times(BSymbol, x), Times(CSymbol, Sqr(x)))), x),
                      x),
                  And(FreeQ(List(a, b, c, d, f, ASymbol, BSymbol, CSymbol, p,
                      q), x), EqQ(Subtract(Sqr(b), Times(C4, a, c)),
                          C0)))),
          IIntegrate(1053,
              Integrate(Times(
                  Power(Plus(a_, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), p_DEFAULT),
                  Plus(A_DEFAULT, Times(C_DEFAULT, Sqr(x_))),
                  Power(Plus(d_DEFAULT, Times(f_DEFAULT, Sqr(x_))), q_DEFAULT)), x_Symbol),
              Condition(Dist(
                  Times(
                      Power(Plus(a, Times(b, x), Times(c, Sqr(x))), FracPart(p)),
                      Power(Times(Power(Times(C4, c), IntPart(p)),
                          Power(Plus(b, Times(C2, c, x)), Times(C2, FracPart(p)))), CN1)),
                  Integrate(Times(Power(Plus(b, Times(C2, c, x)), Times(C2, p)),
                      Power(Plus(d, Times(f, Sqr(x))), q), Plus(ASymbol, Times(CSymbol, Sqr(x)))),
                      x),
                  x),
                  And(FreeQ(List(a, b, c, d, f, ASymbol, CSymbol, p,
                      q), x), EqQ(Subtract(Sqr(b), Times(C4, a, c)),
                          C0)))),
          IIntegrate(1054,
              Integrate(
                  Times(Power(Plus(a_, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), p_),
                      Plus(A_DEFAULT, Times(B_DEFAULT, x_), Times(C_DEFAULT, Sqr(x_))),
                      Power(Plus(d_, Times(e_DEFAULT, x_), Times(f_DEFAULT, Sqr(x_))), q_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(
                              Subtract(Plus(
                                  Times(ASymbol, b, c), Times(CN1, C2, a, BSymbol, c), Times(a, b,
                                      CSymbol)),
                                  Times(
                                      Subtract(
                                          Times(c,
                                              Subtract(Times(b, BSymbol), Times(C2, ASymbol, c))),
                                          Times(CSymbol, Subtract(Sqr(b), Times(C2, a, c)))),
                                      x)),
                              Power(Plus(a, Times(b, x), Times(c, Sqr(x))), Plus(p, C1)),
                              Power(Plus(d, Times(e, x), Times(f, Sqr(x))), q), Power(Times(c,
                                  Subtract(Sqr(b), Times(C4, a, c)), Plus(p, C1)), CN1)),
                          x),
                      Dist(Power(Times(c, Subtract(Sqr(b), Times(C4, a, c)), Plus(p, C1)), CN1),
                          Integrate(
                              Times(
                                  Power(Plus(a, Times(b, x), Times(c,
                                      Sqr(x))), Plus(p,
                                          C1)),
                                  Power(Plus(d, Times(e, x), Times(f, Sqr(x))), Subtract(q, C1)),
                                  Simp(Subtract(Plus(
                                      Times(e, q,
                                          Plus(
                                              Times(ASymbol, b, c), Times(CN1, C2, a, BSymbol,
                                                  c),
                                              Times(a, b, CSymbol))),
                                      Times(CN1, d,
                                          Plus(
                                              Times(c,
                                                  Subtract(Times(b,
                                                      BSymbol), Times(C2, ASymbol, c)),
                                                  Plus(Times(C2, p), C3)),
                                              Times(
                                                  CSymbol,
                                                  Subtract(Times(C2, a, c),
                                                      Times(Sqr(b), Plus(p, C2)))))),
                                      Times(Subtract(
                                          Times(C2, f, q, Plus(Times(ASymbol, b, c),
                                              Times(CN1, C2, a, BSymbol, c), Times(a, b, CSymbol))),
                                          Times(e,
                                              Plus(
                                                  Times(c,
                                                      Subtract(Times(b, BSymbol),
                                                          Times(C2, ASymbol, c)),
                                                      Plus(Times(C2, p), q, C3)),
                                                  Times(CSymbol,
                                                      Subtract(Times(C2, a, c, Plus(q, C1)),
                                                          Times(Sqr(b), Plus(p, q, C2))))))),
                                          x)),
                                      Times(f,
                                          Plus(
                                              Times(c,
                                                  Subtract(Times(b, BSymbol),
                                                      Times(C2, ASymbol, c)),
                                                  Plus(Times(C2, p), Times(C2, q), C3)),
                                              Times(CSymbol,
                                                  Subtract(Times(C2, a, c, Plus(Times(C2, q), C1)),
                                                      Times(Sqr(b), Plus(p, Times(C2, q), C2))))),
                                          Sqr(x))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, ASymbol, BSymbol, CSymbol), x),
                      NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      NeQ(Subtract(Sqr(e),
                          Times(C4, d, f)), C0),
                      LtQ(p, CN1), GtQ(q, C0), Not(IGtQ(q, C0))))),
          IIntegrate(1055,
              Integrate(
                  Times(Power(Plus(a_, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), p_),
                      Plus(A_DEFAULT, Times(C_DEFAULT, Sqr(
                          x_))),
                      Power(Plus(d_, Times(e_DEFAULT, x_), Times(f_DEFAULT, Sqr(x_))), q_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(
                              Plus(Times(ASymbol, b, c), Times(a, b, CSymbol),
                                  Times(Plus(
                                      Times(C2, ASymbol, Sqr(c)),
                                      Times(CSymbol, Subtract(Sqr(b), Times(C2, a, c)))), x)),
                              Power(Plus(a, Times(b, x), Times(c, Sqr(x))), Plus(p, C1)),
                              Power(Plus(d, Times(e, x),
                                  Times(f, Sqr(x))), q),
                              Power(Times(c, Subtract(Sqr(b), Times(C4, a, c)), Plus(p, C1)), CN1)),
                          x),
                      Dist(
                          Power(Times(c, Subtract(Sqr(b), Times(C4, a,
                              c)), Plus(p,
                                  C1)),
                              CN1),
                          Integrate(
                              Times(
                                  Power(Plus(a, Times(b, x), Times(c,
                                      Sqr(x))), Plus(p,
                                          C1)),
                                  Power(Plus(d, Times(e, x), Times(f, Sqr(x))), Subtract(q,
                                      C1)),
                                  Simp(
                                      Subtract(
                                          Plus(
                                              Times(ASymbol, c,
                                                  Plus(Times(C2, c, d, Plus(
                                                      Times(C2, p), C3)), Times(b, e,
                                                          q))),
                                              Times(CN1, CSymbol,
                                                  Subtract(
                                                      Subtract(Times(C2, a, c, d),
                                                          Times(Sqr(b), d, Plus(p, C2))),
                                                      Times(a, b, e, q))),
                                              Times(
                                                  Plus(
                                                      Times(CSymbol,
                                                          Plus(Times(C2, a, b, f, q),
                                                              Times(CN1, C2, a, c, e, Plus(q, C1)),
                                                              Times(Sqr(b), e, Plus(p, q, C2)))),
                                                      Times(C2, ASymbol, c,
                                                          Plus(Times(b, f, q),
                                                              Times(c, e,
                                                                  Plus(Times(C2, p), q, C3))))),
                                                  x)),
                                          Times(f, Plus(
                                              Times(CN2, ASymbol, Sqr(c),
                                                  Plus(Times(C2, p), Times(C2, q), C3)),
                                              Times(CSymbol,
                                                  Subtract(Times(C2, a, c, Plus(Times(C2, q), C1)),
                                                      Times(Sqr(b), Plus(p, Times(C2, q), C2))))),
                                              Sqr(x))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, ASymbol, CSymbol), x),
                      NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      NeQ(Subtract(Sqr(e), Times(C4, d, f)), C0), LtQ(p, CN1), GtQ(q,
                          C0),
                      Not(IGtQ(q, C0))))),
          IIntegrate(1056,
              Integrate(
                  Times(Power(Plus(a_, Times(c_DEFAULT, Sqr(x_))), p_),
                      Plus(A_DEFAULT, Times(B_DEFAULT, x_), Times(C_DEFAULT, Sqr(
                          x_))),
                      Power(Plus(d_, Times(e_DEFAULT, x_), Times(f_DEFAULT, Sqr(x_))), q_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(
                              Subtract(Times(a, BSymbol),
                                  Times(Subtract(Times(ASymbol, c), Times(a, CSymbol)), x)),
                              Power(Plus(a, Times(c,
                                  Sqr(x))), Plus(p,
                                      C1)),
                              Power(Plus(d, Times(e, x),
                                  Times(f, Sqr(x))), q),
                              Power(Times(C2, a, c, Plus(p, C1)), CN1)),
                          x),
                      Dist(Times(C2, Power(Times(CN4, a, c, Plus(p, C1)), CN1)),
                          Integrate(
                              Times(
                                  Power(Plus(a, Times(c,
                                      Sqr(x))), Plus(p,
                                          C1)),
                                  Power(Plus(
                                      d, Times(e, x), Times(f, Sqr(x))), Subtract(q, C1)),
                                  Simp(
                                      Subtract(
                                          Plus(Times(ASymbol, c, d, Plus(Times(C2, p), C3)),
                                              Times(CN1, a,
                                                  Plus(Times(CSymbol, d), Times(BSymbol, e, q))),
                                              Times(
                                                  Subtract(
                                                      Times(ASymbol, c, e,
                                                          Plus(Times(C2, p), q, C3)),
                                                      Times(a,
                                                          Plus(Times(C2, BSymbol, f, q),
                                                              Times(CSymbol, e, Plus(q, C1))))),
                                                  x)),
                                          Times(f,
                                              Subtract(Times(a, CSymbol, Plus(Times(C2, q), C1)),
                                                  Times(ASymbol, c,
                                                      Plus(Times(C2, p), Times(C2, q), C3))),
                                              Sqr(x))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, c, d, e, f, ASymbol, BSymbol, CSymbol), x),
                      NeQ(Subtract(Sqr(e),
                          Times(C4, d, f)), C0),
                      LtQ(p, CN1), GtQ(q, C0), Not(IGtQ(q, C0))))),
          IIntegrate(1057,
              Integrate(
                  Times(
                      Power(Plus(a_, Times(c_DEFAULT,
                          Sqr(x_))), p_),
                      Plus(A_DEFAULT, Times(C_DEFAULT, Sqr(
                          x_))),
                      Power(Plus(d_, Times(e_DEFAULT, x_), Times(f_DEFAULT, Sqr(x_))), q_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(Subtract(Times(ASymbol, c), Times(a, CSymbol)), x,
                                  Power(Plus(a, Times(c, Sqr(x))), Plus(p, C1)),
                                  Power(Plus(d, Times(e, x), Times(f, Sqr(x))),
                                      q),
                                  Power(Times(C2, a, c, Plus(p, C1)), CN1)),
                              x)),
                      Dist(
                          Times(C2, Power(Times(C4, a, c, Plus(p, C1)), CN1)),
                          Integrate(
                              Times(
                                  Power(Plus(a, Times(c, Sqr(
                                      x))), Plus(p,
                                          C1)),
                                  Power(Plus(d, Times(e, x), Times(f,
                                      Sqr(x))), Subtract(q,
                                          C1)),
                                  Simp(Subtract(
                                      Plus(Times(ASymbol, c, d, Plus(Times(C2, p), C3)),
                                          Times(CN1, a, CSymbol, d),
                                          Times(Subtract(
                                              Times(ASymbol, c, e, Plus(Times(C2, p), q, C3)),
                                              Times(a, CSymbol, e, Plus(q, C1))), x)),
                                      Times(f,
                                          Subtract(Times(a, CSymbol, Plus(Times(C2, q), C1)),
                                              Times(ASymbol, c,
                                                  Plus(Times(C2, p), Times(C2, q), C3))),
                                          Sqr(x))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, c, d, e, f, ASymbol, CSymbol), x),
                      NeQ(Subtract(Sqr(e), Times(C4, d, f)), C0), LtQ(p, CN1), GtQ(q,
                          C0),
                      Not(IGtQ(q, C0))))),
          IIntegrate(1058,
              Integrate(Times(Power(Plus(a_, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), p_),
                  Plus(A_DEFAULT, Times(B_DEFAULT, x_), Times(C_DEFAULT, Sqr(x_))),
                  Power(Plus(d_, Times(f_DEFAULT, Sqr(x_))), q_)), x_Symbol),
              Condition(
                  Subtract(
                      Simp(Times(
                          Subtract(
                              Plus(
                                  Times(ASymbol, b, c), Times(CN1, C2, a, BSymbol, c), Times(a, b,
                                      CSymbol)),
                              Times(Subtract(
                                  Times(c, Subtract(Times(b, BSymbol), Times(C2, ASymbol, c))),
                                  Times(CSymbol, Subtract(Sqr(b), Times(C2, a, c)))), x)),
                          Power(Plus(a, Times(b, x), Times(c, Sqr(x))), Plus(p, C1)),
                          Power(Plus(d, Times(f, Sqr(
                              x))), q),
                          Power(Times(c, Subtract(Sqr(b), Times(C4, a, c)), Plus(p, C1)), CN1)), x),
                      Dist(
                          Power(Times(c, Subtract(Sqr(b), Times(C4, a, c)),
                              Plus(p, C1)), CN1),
                          Integrate(
                              Times(
                                  Power(Plus(a, Times(b, x), Times(c, Sqr(
                                      x))), Plus(p, C1)),
                                  Power(Plus(d, Times(f,
                                      Sqr(x))), Subtract(q,
                                          C1)),
                                  Simp(Subtract(
                                      Plus(
                                          Times(
                                              CN1, d,
                                              Plus(
                                                  Times(c,
                                                      Subtract(Times(b, BSymbol),
                                                          Times(C2, ASymbol, c)),
                                                      Plus(Times(C2, p), C3)),
                                                  Times(CSymbol,
                                                      Subtract(Times(C2, a, c),
                                                          Times(Sqr(b), Plus(p, C2)))))),
                                          Times(C2, f, q, Plus(Times(ASymbol, b, c),
                                              Times(CN1, C2, a, BSymbol, c), Times(a, b, CSymbol)),
                                              x)),
                                      Times(f,
                                          Plus(
                                              Times(c, Subtract(Times(b, BSymbol),
                                                  Times(C2, ASymbol, c)),
                                                  Plus(Times(C2, p), Times(C2, q), C3)),
                                              Times(CSymbol,
                                                  Subtract(Times(C2, a, c, Plus(Times(C2, q), C1)),
                                                      Times(Sqr(b), Plus(p, Times(C2, q), C2))))),
                                          Sqr(x))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, f, ASymbol, BSymbol, CSymbol), x),
                      NeQ(Subtract(Sqr(b),
                          Times(C4, a, c)), C0),
                      LtQ(p, CN1), GtQ(q, C0), Not(IGtQ(q, C0))))),
          IIntegrate(1059,
              Integrate(
                  Times(Power(Plus(a_, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), p_),
                      Plus(A_DEFAULT, Times(C_DEFAULT,
                          Sqr(x_))),
                      Power(Plus(d_, Times(f_DEFAULT, Sqr(x_))), q_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(
                              Plus(Times(ASymbol, b, c), Times(a, b, CSymbol),
                                  Times(Plus(Times(C2, ASymbol, Sqr(c)),
                                      Times(CSymbol, Subtract(Sqr(b), Times(C2, a, c)))), x)),
                              Power(Plus(a, Times(b, x), Times(c, Sqr(x))), Plus(p, C1)),
                              Power(Plus(d, Times(f, Sqr(x))), q), Power(Times(c,
                                  Subtract(Sqr(b), Times(C4, a, c)), Plus(p, C1)), CN1)),
                          x),
                      Dist(Power(Times(c, Subtract(Sqr(b), Times(C4, a, c)), Plus(p, C1)), CN1),
                          Integrate(Times(
                              Power(Plus(a, Times(b, x), Times(c, Sqr(x))), Plus(p, C1)),
                              Power(Plus(d, Times(f, Sqr(x))), Subtract(q, C1)),
                              Simp(Subtract(
                                  Plus(Times(ASymbol, c, C2, c, d, Plus(Times(C2, p), C3)),
                                      Times(CN1, CSymbol,
                                          Subtract(Times(C2, a, c, d),
                                              Times(Sqr(b), d, Plus(p, C2)))),
                                      Times(
                                          Plus(Times(CSymbol, C2, a, b, f, q),
                                              Times(C2, ASymbol, c, b, f, q)),
                                          x)),
                                  Times(f,
                                      Plus(
                                          Times(CN2, ASymbol, Sqr(c),
                                              Plus(Times(C2, p), Times(C2, q), C3)),
                                          Times(CSymbol,
                                              Subtract(Times(C2, a, c, Plus(Times(C2, q), C1)),
                                                  Times(Sqr(b), Plus(p, Times(C2, q), C2))))),
                                      Sqr(x))),
                                  x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, f, ASymbol, CSymbol), x),
                      NeQ(Subtract(Sqr(b),
                          Times(C4, a, c)), C0),
                      LtQ(p, CN1), GtQ(q, C0), Not(IGtQ(q, C0))))),
          IIntegrate(1060,
              Integrate(
                  Times(Power(Plus(a_, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_))), p_),
                      Plus(A_DEFAULT, Times(B_DEFAULT, x_), Times(C_DEFAULT, Sqr(x_))),
                      Power(Plus(d_, Times(e_DEFAULT, x_), Times(f_DEFAULT, Sqr(x_))), q_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(Plus(a, Times(b, x), Times(c, Sqr(x))), Plus(p, C1)),
                              Power(Plus(d, Times(e, x), Times(f,
                                  Sqr(x))), Plus(q,
                                      C1)),
                              Plus(
                                  Times(Subtract(Times(ASymbol, c), Times(a, CSymbol)),
                                      Subtract(
                                          Times(C2, a, c, e), Times(b,
                                              Plus(Times(c, d), Times(a, f))))),
                                  Times(Subtract(Times(ASymbol, b), Times(a, BSymbol)),
                                      Subtract(Plus(Times(C2, Sqr(c), d), Times(Sqr(b), f)),
                                          Times(c, Plus(Times(b, e), Times(C2, a, f))))),
                                  Times(c, Plus(
                                      Times(ASymbol,
                                          Subtract(Plus(Times(C2, Sqr(c), d), Times(Sqr(b), f)),
                                              Times(c, Plus(Times(b, e), Times(C2, a, f))))),
                                      Times(CN1, BSymbol,
                                          Plus(Times(b, c, d), Times(CN1, C2, a, c, e),
                                              Times(a, b, f))),
                                      Times(CSymbol,
                                          Subtract(Subtract(Times(Sqr(b), d), Times(a, b, e)),
                                              Times(C2, a, Subtract(Times(c, d), Times(a, f)))))),
                                      x)),
                              Power(Times(Subtract(Sqr(b), Times(C4, a, c)),
                                  Subtract(Sqr(Subtract(Times(c, d), Times(a, f))),
                                      Times(Subtract(Times(b, d), Times(a, e)),
                                          Subtract(Times(c, e), Times(b, f)))),
                                  Plus(p, C1)), CN1)),
                          x),
                      Dist(
                          Power(
                              Times(Subtract(Sqr(b), Times(C4, a, c)),
                                  Subtract(Sqr(Subtract(Times(c, d), Times(a, f))),
                                      Times(Subtract(Times(b, d), Times(a, e)),
                                          Subtract(Times(c, e), Times(b, f)))),
                                  Plus(p, C1)),
                              CN1),
                          Integrate(
                              Times(Power(Plus(a, Times(b, x), Times(c, Sqr(x))), Plus(p, C1)),
                                  Power(Plus(d, Times(e, x), Times(f, Sqr(x))), q), Simp(
                                      Subtract(
                                          Subtract(
                                              Subtract(Plus(Times(
                                                  Subtract(Subtract(Times(b, BSymbol), Times(
                                                      C2, ASymbol, c)), Times(C2, a,
                                                          CSymbol)),
                                                  Subtract(Sqr(Subtract(Times(c, d), Times(a, f))),
                                                      Times(Subtract(Times(b, d), Times(a, e)),
                                                          Subtract(Times(c, e), Times(b, f)))),
                                                  Plus(p, C1)),
                                                  Times(
                                                      Plus(
                                                          Times(Sqr(b),
                                                              Plus(Times(CSymbol, d), Times(ASymbol,
                                                                  f))),
                                                          Times(CN1, b,
                                                              Plus(Times(BSymbol, c, d), Times(
                                                                  ASymbol, c, e),
                                                                  Times(a, CSymbol, e),
                                                                  Times(a, BSymbol, f))),
                                                          Times(C2, Subtract(
                                                              Times(ASymbol, c,
                                                                  Subtract(Times(c, d),
                                                                      Times(a, f))),
                                                              Times(a,
                                                                  Subtract(
                                                                      Subtract(Times(c, CSymbol, d),
                                                                          Times(BSymbol, c, e)),
                                                                      Times(a, CSymbol, f)))))),
                                                      Subtract(Times(a, f, Plus(p, C1)), Times(c, d,
                                                          Plus(p, C2))))),
                                                  Times(e, Plus(Times(Subtract(
                                                      Times(ASymbol, c), Times(a, CSymbol)),
                                                      Subtract(Times(C2, a, c, e),
                                                          Times(b,
                                                              Plus(Times(c, d), Times(a, f))))),
                                                      Times(
                                                          Subtract(Times(ASymbol, b), Times(a,
                                                              BSymbol)),
                                                          Subtract(
                                                              Plus(Times(C2, Sqr(c),
                                                                  d), Times(Sqr(b), f)),
                                                              Times(c,
                                                                  Plus(Times(b,
                                                                      e),
                                                                      Times(C2, a, f)))))),
                                                      Plus(p, q, C2))),
                                              Times(
                                                  Subtract(
                                                      Times(C2, f, Plus(Times(
                                                          Subtract(Times(ASymbol, c),
                                                              Times(a, CSymbol)),
                                                          Subtract(Times(C2, a, c, e),
                                                              Times(b, Plus(Times(c, d),
                                                                  Times(a, f))))),
                                                          Times(
                                                              Subtract(Times(ASymbol, b),
                                                                  Times(a, BSymbol)),
                                                              Subtract(
                                                                  Plus(Times(C2, Sqr(c), d),
                                                                      Times(Sqr(b), f)),
                                                                  Times(c,
                                                                      Plus(Times(b, e),
                                                                          Times(C2, a, f)))))),
                                                          Plus(p, q, C2)),
                                                      Times(
                                                          Plus(
                                                              Times(Sqr(b),
                                                                  Plus(Times(CSymbol, d),
                                                                      Times(ASymbol, f))),
                                                              Times(CN1, b, Plus(
                                                                  Times(BSymbol, c, d),
                                                                  Times(ASymbol, c, e),
                                                                  Times(a, CSymbol, e),
                                                                  Times(a, BSymbol, f))),
                                                              Times(C2, Subtract(
                                                                  Times(ASymbol, c,
                                                                      Subtract(Times(c, d),
                                                                          Times(a, f))),
                                                                  Times(a, Subtract(
                                                                      Subtract(Times(c, CSymbol, d),
                                                                          Times(BSymbol, c, e)),
                                                                      Times(a, CSymbol, f)))))),
                                                          Subtract(Times(b, f, Plus(p, C1)),
                                                              Times(c, e,
                                                                  Plus(Times(C2, p), q, C4))))),
                                                  x)),
                                          Times(c, f, Plus(
                                              Times(Sqr(b),
                                                  Plus(Times(CSymbol, d), Times(ASymbol, f))),
                                              Times(
                                                  CN1, b,
                                                  Plus(Times(BSymbol, c, d), Times(ASymbol, c, e),
                                                      Times(a, CSymbol, e), Times(a, BSymbol, f))),
                                              Times(C2,
                                                  Subtract(
                                                      Times(ASymbol, c,
                                                          Subtract(Times(c, d), Times(a, f))),
                                                      Times(a,
                                                          Subtract(
                                                              Subtract(Times(c, CSymbol, d),
                                                                  Times(BSymbol, c, e)),
                                                              Times(a, CSymbol, f)))))),
                                              Plus(Times(C2, p), Times(C2, q), C5), Sqr(x))),
                                      x)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, ASymbol, BSymbol, CSymbol, q), x),
                      NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      NeQ(Subtract(Sqr(e), Times(C4, d, f)), C0), LtQ(p, CN1),
                      NeQ(Subtract(Sqr(Subtract(Times(c, d), Times(a, f))),
                          Times(Subtract(Times(b, d), Times(a, e)),
                              Subtract(Times(c, e), Times(b, f)))),
                          C0),
                      Not(And(Not(IntegerQ(p)), ILtQ(q, CN1))), Not(IGtQ(q, C0))))));
}
