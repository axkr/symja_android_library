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
class IntRules104 {
  public static IAST RULES =
      List(
          IIntegrate(2081,
              Integrate(
                  Times(
                      Power($p(
                          "§p3"), p_DEFAULT),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(a, Coeff($s("§p3"), x, C0)), Set(b, Coeff($s(
                              "§p3"), x, C1)),
                          Set(c, Coeff($s("§p3"), x, C2)), Set(d, Coeff($s("§p3"), x, C3))),
                      Condition(
                          Subst(Integrate(Times(
                              Power(
                                  Plus(Times(Subtract(Times(C3, d, e), Times(c, f)),
                                      Power(Times(C3, d), CN1)), Times(f, x)),
                                  m),
                              Power(
                                  Simp(
                                      Plus(
                                          Times(
                                              Plus(Times(C2, Power(c, C3)), Times(CN1, C9, b, c, d),
                                                  Times(ZZ(27L), a, Sqr(d))),
                                              Power(Times(ZZ(27L), Sqr(d)), CN1)),
                                          Times(CN1, Subtract(Sqr(c), Times(C3, b, d)), x,
                                              Power(Times(C3, d), CN1)),
                                          Times(d, Power(x, C3))),
                                      x),
                                  p)),
                              x), x, Plus(x, Times(c, Power(Times(C3, d), CN1)))),
                          NeQ(c, C0))),
                  And(FreeQ(List(e, f, m, p), x), PolyQ($s("§p3"), x, C3)))),
          IIntegrate(2082,
              Integrate(
                  Times(x_,
                      Power(
                          Plus(a_, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_)),
                              Times(e_DEFAULT, Power(x_, C4))),
                          CN1D2)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set($s("§px"),
                              Plus(
                                  Times(
                                      QQ(1L, 320L), C1,
                                      Plus(
                                          Times(ZZ(33L), Sqr(b), c), Times(C6, a, Sqr(c)),
                                          Times(ZZ(40L), Sqr(a), e))),
                                  Times(CN1, QQ(1L, 5L), ZZ(22L), a, c, e, Sqr(x)),
                                  Times(QQ(1L, 15L), ZZ(22L), b, c, e, Power(x,
                                      C3)),
                                  Times(
                                      C1D4, C1, e, Plus(Times(C5, Sqr(c)), Times(C4, a,
                                          e)),
                                      Power(x, C4)),
                                  Times(C1D3, C4, b, Sqr(e), Power(x, C5)),
                                  Times(C2, c, Sqr(e), Power(x, C6)),
                                  Times(Power(e, C3), Power(x, C8))))),
                      Simp(Times(C1,
                          Log(Plus($s("§px"),
                              Times(Dist(Power(Times(C8, Rt(e, C2), x), CN1), D($s("§px"), x), x),
                                  Sqrt(Plus(a, Times(b, x), Times(c, Sqr(x)),
                                      Times(e, Power(x, C4))))))),
                          Power(Times(C8, Rt(e, C2)), CN1)), x)),
                  And(FreeQ(List(a, b, c, e), x),
                      EqQ(Plus(Times(ZZ(71L), Sqr(c)), Times(ZZ(100L), a, e)), C0),
                      EqQ(Subtract(Times(ZZ(1152L), Power(c, C3)), Times(ZZ(125L), Sqr(b), e)),
                          C0)))),
          IIntegrate(2083, Integrate(Times(Plus(A_, Times(B_DEFAULT, x_)),
              Power(Plus(a_, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_)),
                  Times(d_DEFAULT, Power(x_, C3)), Times(e_DEFAULT, Power(x_, C4))), CN1D2)),
              x_Symbol),
              Condition(Dist(BSymbol, Subst(
                  Integrate(Times(x, Power(Plus(
                      Times(
                          Plus(Times(CN3, Power(d, C4)), Times(ZZ(16L), c, Sqr(d), e),
                              Times(CN1, ZZ(64L), b, d, Sqr(e)), Times(ZZ(256L), a, Power(e, C3))),
                          Power(Times(ZZ(256L), Power(e, C3)), CN1)),
                      Times(Plus(Power(d, C3), Times(CN1, C4, c, d, e), Times(C8, b, Sqr(e))), x,
                          Power(Times(C8, Sqr(e)), CN1)),
                      Times(CN1, Subtract(Times(C3, Sqr(d)), Times(C8, c, e)), Sqr(x),
                          Power(Times(C8, e), CN1)),
                      Times(e, Power(x, C4))), CN1D2)), x),
                  x, Plus(Times(d, Power(Times(C4, e), CN1)), x)), x), And(
                      FreeQ(List(a, b, c, d, e, ASymbol, BSymbol), x),
                      EqQ(Subtract(Times(BSymbol, d),
                          Times(C4, ASymbol, e)), C0),
                      EqQ(Plus(Times(d,
                          Subtract(
                              Subtract(Times(ZZ(141L), Power(d, C3)), Times(ZZ(752L), c, d, e)),
                              Times(ZZ(400L), b, Sqr(e)))),
                          Times(ZZ(16L), Sqr(e),
                              Plus(Times(ZZ(71L), Sqr(c)), Times(ZZ(100L), a, e)))),
                          C0),
                      EqQ(Plus(
                          Times(ZZ(144L), Power(Subtract(Times(C3, Sqr(d)), Times(C8, c, e)),
                              C3)),
                          Times(
                              ZZ(125L), Sqr(
                                  Plus(Power(d, C3), Times(CN1, C4, c, d, e),
                                      Times(C8, b, Sqr(e)))))),
                          C0)))),
          IIntegrate(2084,
              Integrate(
                  Times(Power(Plus(d_, Times(e_DEFAULT, x_), Times(d_DEFAULT, Sqr(x_))), CN1),
                      Plus(f_, Times(g_DEFAULT, Sqr(x_))),
                      Power(
                          Plus(a_, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_)),
                              Times(b_DEFAULT, Power(x_, C3)), Times(a_DEFAULT, Power(x_, C4))),
                          CN1D2)),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(a, f,
                          ArcTan(
                              Times(Plus(Times(a, b),
                                  Times(Subtract(Plus(Times(C4, Sqr(a)), Sqr(b)), Times(C2, a, c)),
                                      x),
                                  Times(a, b, Sqr(x))),
                                  Power(Times(C2, Rt(Times(Sqr(a), Subtract(Times(C2, a), c)), C2),
                                      Sqrt(Plus(
                                          a, Times(b, x), Times(c, Sqr(x)), Times(b, Power(x, C3)),
                                          Times(a, Power(x, C4))))),
                                      CN1))),
                          Power(Times(d, Rt(Times(Sqr(a), Subtract(Times(C2, a), c)), C2)), CN1)),
                      x),
                  And(FreeQ(List(a, b, c, d, e, f, g), x),
                      EqQ(Subtract(Times(b, d),
                          Times(a, e)), C0),
                      EqQ(Plus(f, g), C0), PosQ(Times(Sqr(a), Subtract(Times(C2, a), c)))))),
          IIntegrate(2085,
              Integrate(Times(Power(Plus(d_, Times(e_DEFAULT, x_), Times(d_DEFAULT, Sqr(x_))), CN1),
                  Plus(f_, Times(g_DEFAULT, Sqr(x_))),
                  Power(
                      Plus(a_, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_)),
                          Times(b_DEFAULT, Power(x_, C3)), Times(a_DEFAULT, Power(x_, C4))),
                      CN1D2)),
                  x_Symbol),
              Condition(
                  Negate(
                      Simp(
                          Times(a, f,
                              ArcTanh(Times(
                                  Plus(Times(a, b),
                                      Times(Subtract(Plus(Times(C4, Sqr(a)), Sqr(b)),
                                          Times(C2, a, c)), x),
                                      Times(a, b, Sqr(x))),
                                  Power(
                                      Times(C2,
                                          Rt(Times(CN1, Sqr(a), Subtract(Times(C2, a), c)), C2),
                                          Sqrt(Plus(a, Times(b, x), Times(c, Sqr(x)),
                                              Times(b, Power(x, C3)), Times(a, Power(x, C4))))),
                                      CN1))),
                              Power(Times(d, Rt(Times(CN1, Sqr(a), Subtract(Times(C2, a), c)), C2)),
                                  CN1)),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f, g), x),
                      EqQ(Subtract(Times(b, d), Times(a, e)), C0), EqQ(Plus(f, g),
                          C0),
                      NegQ(Times(Sqr(a), Subtract(Times(C2, a), c)))))),
          IIntegrate(2086,
              Integrate(
                  Times($p("§p3"),
                      Power(
                          Plus(
                              a_, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(
                                  x_)),
                              Times(d_DEFAULT, Power(x_, C3)), Times(e_DEFAULT, Power(x_, C4))),
                          CN1)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(q, Sqrt(Subtract(Plus(Times(C8, Sqr(a)), Sqr(b)), Times(C4, a, c)))),
                          Set(ASymbol, Coeff($s("§p3"), x, C0)),
                          Set(BSymbol, Coeff($s(
                              "§p3"), x, C1)),
                          Set(CSymbol, Coeff($s("§p3"), x, C2)), Set(D, Coeff($s("§p3"), x, C3))),
                      Subtract(
                          Dist(Power(q, CN1),
                              Integrate(
                                  Times(Plus(Times(b, ASymbol), Times(CN1, C2, a, BSymbol),
                                      Times(C2, a, D), Times(ASymbol, q),
                                      Times(Plus(Times(C2, a, ASymbol), Times(CN1, C2, a, CSymbol),
                                          Times(b, D), Times(D, q)), x)),
                                      Power(
                                          Plus(
                                              Times(C2, a), Times(Plus(b, q), x), Times(C2, a,
                                                  Sqr(x))),
                                          CN1)),
                                  x),
                              x),
                          Dist(Power(q, CN1), Integrate(Times(
                              Plus(Times(b, ASymbol), Times(CN1, C2, a, BSymbol), Times(C2, a, D),
                                  Times(CN1, ASymbol, q),
                                  Times(
                                      Subtract(Plus(Times(C2, a, ASymbol),
                                          Times(CN1, C2, a, CSymbol), Times(b, D)), Times(D, q)),
                                      x)),
                              Power(Plus(Times(C2, a), Times(Subtract(b, q), x),
                                  Times(C2, a, Sqr(x))), CN1)),
                              x), x))),
                  And(FreeQ(List(a, b, c), x), PolyQ($s("§p3"), x, C3), EqQ(a, e), EqQ(b, d)))),
          IIntegrate(2087,
              Integrate(
                  Times($p("§p3"), Power(x_, m_DEFAULT),
                      Power(
                          Plus(
                              a_, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(
                                  x_)),
                              Times(d_DEFAULT, Power(x_, C3)), Times(e_DEFAULT, Power(x_, C4))),
                          CN1)),
                  x_Symbol),
              Condition(With(List(
                  Set(q, Sqrt(Subtract(Plus(Times(C8, Sqr(a)), Sqr(b)), Times(C4, a, c)))),
                  Set(ASymbol, Coeff($s("§p3"), x, C0)), Set(BSymbol, Coeff($s("§p3"), x, C1)),
                  Set(CSymbol, Coeff($s("§p3"), x, C2)), Set(D, Coeff($s("§p3"), x, C3))),
                  Subtract(Dist(Power(q, CN1), Integrate(
                      Times(Power(x, m), Plus(Times(b, ASymbol), Times(CN1, C2, a, BSymbol),
                          Times(C2, a, D), Times(ASymbol, q),
                          Times(Plus(Times(C2, a, ASymbol), Times(CN1, C2, a, CSymbol), Times(b, D),
                              Times(D, q)), x)),
                          Power(Plus(Times(C2, a), Times(Plus(b, q), x), Times(C2, a, Sqr(x))),
                              CN1)),
                      x), x),
                      Dist(Power(q, CN1), Integrate(Times(Power(x, m),
                          Plus(
                              Times(b, ASymbol), Times(CN1, C2, a, BSymbol), Times(C2, a, D),
                              Times(CN1, ASymbol, q),
                              Times(Subtract(Plus(Times(C2, a, ASymbol), Times(CN1, C2, a, CSymbol),
                                  Times(b, D)), Times(D, q)), x)),
                          Power(Plus(Times(C2, a), Times(Subtract(b, q), x), Times(C2, a, Sqr(x))),
                              CN1)),
                          x), x))),
                  And(FreeQ(List(a, b, c, m), x), PolyQ($s("§p3"), x, C3), EqQ(a, e), EqQ(b, d)))),
          IIntegrate(2088,
              Integrate(Times(Plus(A_DEFAULT, Times(B_DEFAULT, x_), Times(C_DEFAULT, Sqr(x_))),
                  Power(
                      Plus(a_, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_)),
                          Times(d_DEFAULT, Power(x_, C3)), Times(e_DEFAULT, Power(x_, C4))),
                      CN1)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(q, Rt(Times(CSymbol,
                          Plus(Times(C2, e, Subtract(Times(BSymbol, d), Times(C4, ASymbol, e))),
                              Times(CSymbol, Subtract(Sqr(d), Times(C4, c, e))))),
                          C2))),
                      Plus(
                          Simp(
                              Times(CN2, Sqr(CSymbol),
                                  ArcTanh(Times(Plus(
                                      Times(CSymbol, d), Times(CN1, BSymbol, e),
                                      Times(C2, CSymbol, e, x)), Power(q, CN1))),
                                  Power(q, CN1)),
                              x),
                          Simp(Times(C2, Sqr(CSymbol), ArcTanh(Times(CSymbol,
                              Plus(Times(C4, BSymbol, c, CSymbol), Times(CN1, C3, Sqr(BSymbol), d),
                                  Times(CN1, C4, ASymbol, CSymbol, d),
                                  Times(ZZ(12L), ASymbol, BSymbol, e),
                                  Times(C4, CSymbol,
                                      Plus(Times(C2, c, CSymbol), Times(CN1, BSymbol, d),
                                          Times(C2, ASymbol, e)),
                                      x),
                                  Times(C4, CSymbol,
                                      Subtract(Times(C2, CSymbol, d), Times(BSymbol, e)), Sqr(x)),
                                  Times(C8, Sqr(CSymbol), e, Power(x, C3))),
                              Power(Times(q, Subtract(Sqr(BSymbol), Times(C4, ASymbol, CSymbol))),
                                  CN1))),
                              Power(q, CN1)), x))),
                  And(FreeQ(List(a, b, c, d, e, ASymbol, BSymbol, CSymbol), x),
                      EqQ(Subtract(Plus(Times(Sqr(BSymbol), d), Times(C2, CSymbol,
                          Plus(Times(b, CSymbol), Times(ASymbol, d)))), Times(C2, BSymbol,
                              Plus(Times(c, CSymbol), Times(C2, ASymbol, e)))),
                          C0),
                      EqQ(Plus(Times(C2, Sqr(BSymbol), c, CSymbol),
                          Times(CN1, C8, a, Power(CSymbol, C3)), Times(CN1, Power(BSymbol, C3), d),
                          Times(CN1, C4, ASymbol, BSymbol, CSymbol, d), Times(C4, ASymbol,
                              Plus(Sqr(BSymbol), Times(C2, ASymbol, CSymbol)), e)),
                          C0),
                      PosQ(Times(CSymbol,
                          Plus(Times(C2, e, Subtract(Times(BSymbol, d), Times(C4, ASymbol, e))),
                              Times(CSymbol, Subtract(Sqr(d), Times(C4, c, e))))))))),
          IIntegrate(2089,
              Integrate(
                  Times(Plus(A_DEFAULT, Times(C_DEFAULT, Sqr(x_))),
                      Power(
                          Plus(
                              a_, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_)),
                              Times(d_DEFAULT, Power(x_, C3)), Times(e_DEFAULT, Power(x_, C4))),
                          CN1)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(q,
                              Rt(Times(CSymbol,
                                  Plus(
                                      Times(CN8, ASymbol, Sqr(e)), Times(CSymbol,
                                          Subtract(Sqr(d), Times(C4, c, e))))),
                                  C2))),
                      Plus(
                          Simp(
                              Times(CN2, Sqr(CSymbol),
                                  ArcTanh(Times(CSymbol, Plus(d, Times(C2, e,
                                      x)), Power(q,
                                          CN1))),
                                  Power(q, CN1)),
                              x),
                          Simp(
                              Times(C2, Sqr(CSymbol),
                                  ArcTanh(
                                      Times(CSymbol,
                                          Subtract(
                                              Subtract(
                                                  Subtract(Times(ASymbol, d),
                                                      Times(C2,
                                                          Plus(Times(c, CSymbol),
                                                              Times(ASymbol, e)),
                                                          x)),
                                                  Times(C2, CSymbol, d, Sqr(x))),
                                              Times(C2, CSymbol, e, Power(x, C3))),
                                          Power(Times(ASymbol, q), CN1))),
                                  Power(q, CN1)),
                              x))),
                  And(FreeQ(List(a, b, c, d, e, ASymbol, CSymbol), x),
                      EqQ(Plus(Times(b, CSymbol), Times(ASymbol, d)), C0),
                      EqQ(Subtract(Times(a, Sqr(CSymbol)), Times(Sqr(ASymbol), e)), C0),
                      PosQ(Times(CSymbol,
                          Plus(Times(CN8, ASymbol, Sqr(e)),
                              Times(CSymbol, Subtract(Sqr(d), Times(C4, c, e))))))))),
          IIntegrate(2090,
              Integrate(
                  Times(Plus(A_DEFAULT, Times(B_DEFAULT, x_), Times(C_DEFAULT, Sqr(x_))),
                      Power(
                          Plus(a_, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_)),
                              Times(d_DEFAULT, Power(x_, C3)), Times(e_DEFAULT, Power(x_, C4))),
                          CN1)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(q, Rt(Times(CN1, CSymbol,
                          Plus(Times(C2, e, Subtract(Times(BSymbol, d), Times(C4, ASymbol, e))),
                              Times(CSymbol, Subtract(Sqr(d), Times(C4, c, e))))),
                          C2))),
                      Subtract(
                          Simp(
                              Times(C2, Sqr(CSymbol),
                                  ArcTan(Times(Plus(
                                      Times(CSymbol, d), Times(CN1, BSymbol, e),
                                      Times(C2, CSymbol, e, x)), Power(q, CN1))),
                                  Power(q, CN1)),
                              x),
                          Simp(Times(C2, Sqr(CSymbol), ArcTan(Times(CSymbol,
                              Plus(Times(C4, BSymbol, c, CSymbol), Times(CN1, C3, Sqr(BSymbol), d),
                                  Times(CN1, C4, ASymbol, CSymbol, d),
                                  Times(ZZ(12L), ASymbol, BSymbol, e),
                                  Times(C4, CSymbol,
                                      Plus(Times(C2, c, CSymbol), Times(CN1, BSymbol, d),
                                          Times(C2, ASymbol, e)),
                                      x),
                                  Times(C4, CSymbol,
                                      Subtract(Times(C2, CSymbol, d), Times(BSymbol, e)), Sqr(x)),
                                  Times(C8, Sqr(CSymbol), e, Power(x, C3))),
                              Power(Times(q, Subtract(Sqr(BSymbol), Times(C4, ASymbol, CSymbol))),
                                  CN1))),
                              Power(q, CN1)), x))),
                  And(FreeQ(List(a, b, c, d, e, ASymbol, BSymbol, CSymbol), x), EqQ(Subtract(
                      Plus(Times(Sqr(BSymbol), d),
                          Times(C2, CSymbol, Plus(Times(b, CSymbol), Times(ASymbol, d)))),
                      Times(C2, BSymbol, Plus(Times(c, CSymbol), Times(C2, ASymbol, e)))), C0), EqQ(
                          Plus(Times(C2, Sqr(BSymbol), c, CSymbol),
                              Times(CN1, C8, a, Power(CSymbol, C3)),
                              Times(CN1, Power(BSymbol, C3), d),
                              Times(CN1, C4, ASymbol, BSymbol, CSymbol, d),
                              Times(C4, ASymbol, Plus(Sqr(BSymbol), Times(C2, ASymbol, CSymbol)),
                                  e)),
                          C0),
                      NegQ(
                          Times(CSymbol,
                              Plus(
                                  Times(C2, e, Subtract(Times(BSymbol, d),
                                      Times(C4, ASymbol, e))),
                                  Times(CSymbol, Subtract(Sqr(d), Times(C4, c, e))))))))),
          IIntegrate(2091,
              Integrate(
                  Times(
                      Plus(A_DEFAULT, Times(C_DEFAULT,
                          Sqr(x_))),
                      Power(Plus(a_, Times(b_DEFAULT, x_), Times(c_DEFAULT, Sqr(x_)),
                          Times(d_DEFAULT, Power(x_, C3)), Times(e_DEFAULT, Power(x_, C4))), CN1)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(
                          q,
                          Rt(Times(
                              CN1, CSymbol,
                              Plus(
                                  Times(CN8, ASymbol, Sqr(e)),
                                  Times(CSymbol, Subtract(Sqr(d), Times(C4, c, e))))),
                              C2))),
                      Subtract(
                          Simp(Times(C2, Sqr(CSymbol),
                              ArcTan(Times(Plus(Times(CSymbol, d), Times(C2, CSymbol, e, x)),
                                  Power(q, CN1))),
                              Power(q, CN1)), x),
                          Simp(
                              Times(C2, Sqr(CSymbol),
                                  ArcTan(
                                      Times(CN1, CSymbol,
                                          Plus(Times(CN1, ASymbol, d),
                                              Times(C2, Plus(Times(c, CSymbol), Times(ASymbol, e)),
                                                  x),
                                              Times(C2, CSymbol, d, Sqr(x)),
                                              Times(C2, CSymbol, e, Power(x, C3))),
                                          Power(Times(ASymbol, q), CN1))),
                                  Power(q, CN1)),
                              x))),
                  And(FreeQ(List(a, b, c, d, e, ASymbol, CSymbol), x),
                      EqQ(Plus(Times(b, CSymbol), Times(ASymbol, d)), C0),
                      EqQ(Subtract(Times(a, Sqr(CSymbol)), Times(Sqr(ASymbol), e)), C0),
                      NegQ(Times(CSymbol,
                          Plus(Times(CN8, ASymbol, Sqr(e)),
                              Times(CSymbol, Subtract(Sqr(d), Times(C4, c, e))))))))),
          IIntegrate(
              2092, Integrate(Times(Power($p(
                  "§p4"), p_), $p(
                      "§px")),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(a, Coeff($s("§p4"), x, C0)), Set(b, Coeff($s("§p4"), x, C1)),
                          Set(c, Coeff($s(
                              "§p4"), x, C2)),
                          Set(d, Coeff($s("§p4"), x, C3)), Set(e, Coeff($s("§p4"), x, C4))),
                      Condition(
                          Dist(Power(Power(a, Times(C3, p)), CN1), Integrate(ExpandIntegrand(
                              Times($s("§px"),
                                  Power(
                                      Times(Power(Subtract(a, Times(b, x)), p),
                                          Power(Power(Subtract(Power(a, C5),
                                              Times(Power(b, C5), Power(x, C5))), p), CN1)),
                                      CN1)),
                              x), x), x),
                          And(NeQ(a, C0), EqQ(c, Times(Sqr(b), Power(a, CN1))),
                              EqQ(d, Times(Power(b, C3), Power(a, CN2))),
                              EqQ(e, Times(Power(b, C4), Power(a, CN3)))))),
                  And(FreeQ(p, x), PolyQ($s("§p4"), x, C4), PolyQ($s("§px"), x), ILtQ(p, C0)))),
          IIntegrate(2093,
              Integrate(
                  Times(Plus(A_, Times(B_DEFAULT, Power(x_, n_))),
                      Power(
                          Plus(
                              a_, Times(b_DEFAULT, Sqr(x_)), Times(c_DEFAULT, Power(x_, n_)), Times(
                                  d_DEFAULT, Power(x_, $p("n2")))),
                          CN1)),
                  x_Symbol),
              Condition(Dist(Times(Sqr(ASymbol), Subtract(n, C1)),
                  Subst(Integrate(
                      Power(Plus(a, Times(Sqr(ASymbol), b, Sqr(Subtract(n, C1)), Sqr(x))), CN1), x),
                      x,
                      Times(x,
                          Power(Subtract(Times(ASymbol, Subtract(n, C1)),
                              Times(BSymbol, Power(x, n))), CN1))),
                  x),
                  And(FreeQ(List(a, b, c, d, ASymbol, BSymbol, n), x), EqQ($s("n2"), Times(C2, n)),
                      NeQ(n, C2), EqQ(
                          Subtract(Times(a, Sqr(
                              BSymbol)), Times(Sqr(ASymbol), d,
                                  Sqr(Subtract(n, C1)))),
                          C0),
                      EqQ(Plus(Times(BSymbol, c), Times(C2, ASymbol, d, Subtract(n, C1))), C0)))),
          IIntegrate(2094,
              Integrate(
                  Times(
                      Power(x_, m_DEFAULT), Plus(A_, Times(B_DEFAULT,
                          Power(x_, n_DEFAULT))),
                      Power(
                          Plus(a_, Times(b_DEFAULT, Power(x_, k_DEFAULT)),
                              Times(c_DEFAULT, Power(x_,
                                  n_DEFAULT)),
                              Times(d_DEFAULT, Power(x_, $p("n2")))),
                          CN1)),
                  x_Symbol),
              Condition(Dist(Times(Sqr(ASymbol), Plus(m, Negate(n), C1), Power(Plus(m, C1), CN1)),
                  Subst(
                      Integrate(Power(
                          Plus(a, Times(Sqr(ASymbol), b, Sqr(Plus(m, Negate(n), C1)), Sqr(x))),
                          CN1), x),
                      x,
                      Times(Power(x, Plus(m, C1)),
                          Power(Plus(Times(ASymbol, Plus(m, Negate(n), C1)),
                              Times(BSymbol, Plus(m, C1), Power(x, n))), CN1))),
                  x),
                  And(FreeQ(List(a, b, c, d, ASymbol, BSymbol, m, n), x),
                      EqQ($s("n2"), Times(C2, n)), EqQ(k, Times(C2, Plus(m, C1))), EqQ(
                          Subtract(Times(a, Sqr(BSymbol), Sqr(Plus(m, C1))),
                              Times(Sqr(ASymbol), d, Sqr(Plus(m, Negate(n), C1)))),
                          C0),
                      EqQ(Subtract(
                          Times(BSymbol, c, Plus(m, C1)), Times(C2, ASymbol, d,
                              Plus(m, Negate(n), C1))),
                          C0)))),
          IIntegrate(2095,
              Integrate(
                  Times(
                      Plus(a_, Times(b_DEFAULT, Sqr(
                          x_)), Times(c_DEFAULT,
                              Power(x_, C4))),
                      Power(
                          Plus(
                              d_, Times(e_DEFAULT, Sqr(x_)), Times(f_DEFAULT, Power(x_,
                                  C4)),
                              Times(g_DEFAULT, Power(x_, C6))),
                          CN1)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(q,
                              Rt(Times(
                                  Plus(
                                      Times(CN1, a, c, Sqr(f)), Times(ZZ(12L), Sqr(a), Sqr(
                                          g)),
                                      Times(f, Subtract(Times(C3, Sqr(c), d), Times(C2, a, b, g)))),
                                  Power(Times(c, g, Subtract(Times(C3, c, d), Times(a, f))), CN1)),
                                  C2)),
                          Set(r, Rt(
                              Times(
                                  Subtract(
                                      Plus(Times(a, c, Sqr(f)),
                                          Times(C4, g, Plus(Times(b, c, d), Times(Sqr(a), g)))),
                                      Times(f, Plus(Times(C3, Sqr(c), d), Times(C2, a, b, g)))),
                                  Power(Times(c, g, Subtract(Times(C3, c, d), Times(a, f))), CN1)),
                              C2))),
                      Plus(
                          Simp(
                              Times(
                                  c, ArcTan(Times(Plus(r, Times(C2, x)), Power(q, CN1))), Power(
                                      Times(g, q), CN1)),
                              x),
                          Negate(
                              Simp(Times(c, ArcTan(Times(Subtract(r, Times(C2, x)), Power(q, CN1))),
                                  Power(Times(g, q), CN1)), x)),
                          Negate(Simp(
                              Times(c, ArcTan(Times(Subtract(Times(C3, c, d), Times(a, f)), x,
                                  Plus(Times(b, Sqr(c), d, f), Times(CN1, a, Sqr(b), f, g),
                                      Times(CN1, C2, Sqr(a), c, f, g), Times(C6, Sqr(a), b, Sqr(g)),
                                      Times(c,
                                          Plus(Times(C3, Sqr(c), d, f), Times(CN1, a, c, Sqr(f)),
                                              Times(CN1, b, c, d, g), Times(C2, Sqr(a), Sqr(g))),
                                          Sqr(x)),
                                      Times(Sqr(c), g, Subtract(Times(C3, c, d), Times(a, f)),
                                          Power(x, C4))),
                                  Power(Times(g, q, Subtract(Times(b, c, d), Times(C2, Sqr(a), g)),
                                      Plus(Times(b, c, d), Times(CN1, a, b, f),
                                          Times(C4, Sqr(a), g))),
                                      CN1))),
                                  Power(Times(g, q), CN1)),
                              x)))),
                  And(FreeQ(List(a, b, c, d, e, f, g), x),
                      EqQ(Subtract(
                          Plus(Times(C9, Power(c, C3), Sqr(d)),
                              Times(CN1, c, Plus(Sqr(b), Times(C6, a, c)), d, f), Times(Sqr(a), c,
                                  Sqr(f)),
                              Times(C2, a, b, Plus(Times(C3, c, d), Times(a, f)), g)),
                          Times(ZZ(12L), Power(a, C3), Sqr(g))), C0),
                      EqQ(Subtract(Plus(Times(C3, Power(c, C4), Sqr(d), e),
                          Times(CN1, C3, Sqr(a), Sqr(c), d, f, g),
                          Times(Power(a, C3), c, Sqr(f), g),
                          Times(C2, Power(a, C3), Sqr(g), Subtract(Times(b, f), Times(C6, a, g)))),
                          Times(Power(c, C3), d,
                              Subtract(Plus(Times(C2, b, d, f), Times(a, e, f)),
                                  Times(ZZ(12L), a, d, g)))),
                          C0),
                      NeQ(Subtract(Times(C3, c, d), Times(a,
                          f)), C0),
                      NeQ(Subtract(Times(b, c, d), Times(C2, Sqr(a),
                          g)), C0),
                      NeQ(Plus(Times(b, c, d), Times(CN1, a, b, f),
                          Times(C4, Sqr(a), g)), C0),
                      PosQ(
                          Times(
                              Plus(
                                  Times(CN1, a, c, Sqr(f)), Times(ZZ(12L), Sqr(a), Sqr(g)), Times(f,
                                      Subtract(Times(C3, Sqr(c), d), Times(C2, a, b, g)))),
                              Power(Times(c, g, Subtract(Times(C3, c, d), Times(a, f))), CN1)))))),
          IIntegrate(2096,
              Integrate(
                  Times(
                      Plus(a_, Times(c_DEFAULT,
                          Power(x_, C4))),
                      Power(
                          Plus(
                              d_, Times(e_DEFAULT, Sqr(x_)), Times(f_DEFAULT, Power(x_, C4)), Times(
                                  g_DEFAULT, Power(x_, C6))),
                          CN1)),
                  x_Symbol),
              Condition(
                  With(List(
                      Set(q, Rt(
                          Times(
                              Plus(Times(CN1, a, c, Sqr(f)), Times(ZZ(12L), Sqr(a), Sqr(g)),
                                  Times(C3, f, Sqr(c), d)),
                              Power(Times(c, g, Subtract(Times(C3, c, d), Times(a, f))), CN1)),
                          C2)),
                      Set(r, Rt(
                          Times(
                              Subtract(Plus(Times(a, c, Sqr(f)), Times(C4, Sqr(a), Sqr(g))),
                                  Times(C3, Sqr(c), d, f)),
                              Power(Times(c, g, Subtract(Times(C3, c, d), Times(a, f))), CN1)),
                          C2))),
                      Plus(
                          Simp(
                              Times(
                                  c, ArcTan(Times(Plus(r, Times(C2, x)), Power(q, CN1))), Power(
                                      Times(g, q), CN1)),
                              x),
                          Negate(
                              Simp(Times(c, ArcTan(Times(Subtract(r, Times(C2, x)), Power(q, CN1))),
                                  Power(Times(g, q), CN1)), x)),
                          Negate(Simp(Times(c,
                              ArcTan(Times(c, Subtract(Times(C3, c, d), Times(a, f)), x, Subtract(
                                  Subtract(Times(C2, Sqr(a), f, g),
                                      Times(Plus(Times(C3, Sqr(c), d, f), Times(CN1, a, c, Sqr(f)),
                                          Times(C2, Sqr(a), Sqr(g))), Sqr(x))),
                                  Times(c, Subtract(Times(C3, c, d), Times(a, f)), g,
                                      Power(x, C4))),
                                  Power(Times(C8, Power(a, C4), Power(g, C3), q), CN1))),
                              Power(Times(g, q), CN1)), x)))),
                  And(FreeQ(List(a, c, d, e, f, g), x),
                      EqQ(Subtract(
                          Plus(Times(C9, Power(c, C3), Sqr(d)), Times(CN1, C6, a, Sqr(c), d, f),
                              Times(Sqr(a), c, Sqr(f))),
                          Times(ZZ(12L), Power(a, C3), Sqr(g))), C0),
                      EqQ(Subtract(
                          Subtract(
                              Plus(Times(C3, Power(c, C4), Sqr(d), e),
                                  Times(CN1, C3, Sqr(a), Sqr(c), d, f, g),
                                  Times(Power(a, C3), c, Sqr(f), g)),
                              Times(ZZ(12L), Power(a, C4), Power(g, C3))),
                          Times(a, Power(c, C3), d, Subtract(Times(e, f), Times(ZZ(12L), d, g)))),
                          C0),
                      NeQ(Subtract(Times(C3, c, d),
                          Times(a, f)), C0),
                      PosQ(
                          Times(
                              Plus(Times(CN1, a, c, Sqr(f)), Times(ZZ(12L), Sqr(a),
                                  Sqr(g)), Times(C3, Sqr(c), d, f)),
                              Power(Times(c, g, Subtract(Times(C3, c, d), Times(a, f))), CN1)))))),
          IIntegrate(
              2097, Integrate(Times(Power($p("§q6"), p_),
                  u_), x_Symbol),
              Condition(
                  With(
                      List(Set(a, Coeff($s("§q6"), x, C0)), Set(b, Coeff($s("§q6"), x, C2)),
                          Set(c, Coeff($s(
                              "§q6"), x, C3)),
                          Set(d, Coeff($s("§q6"), x, C4)), Set(e, Coeff($s("§q6"), x, C6))),
                      Condition(
                          Dist(Power(Times(Power(C3, Times(C3, p)), Power(a, Times(C2, p))), CN1),
                              Integrate(
                                  ExpandIntegrand(
                                      Times(u,
                                          Power(Plus(Times(C3, a),
                                              Times(C3, Sqr(Rt(a, C3)), Rt(c, C3), x),
                                              Times(b, Sqr(x))), p),
                                          Power(Plus(Times(C3, a),
                                              Times(CN1, C3, Power(CN1, C1D3), Sqr(Rt(a, C3)),
                                                  Rt(c, C3), x),
                                              Times(b, Sqr(x))), p),
                                          Power(Plus(Times(C3, a),
                                              Times(C3, Power(CN1, QQ(2L, 3L)), Sqr(Rt(a, C3)),
                                                  Rt(c, C3), x),
                                              Times(b, Sqr(x))), p)),
                                      x),
                                  x),
                              x),
                          And(EqQ(Subtract(Sqr(b), Times(C3, a, d)), C0),
                              EqQ(Subtract(Power(b, C3), Times(ZZ(27L), Sqr(a), e)), C0)))),
                  And(ILtQ(p, C0), PolyQ($s("§q6"), x, C6), EqQ(Coeff($s(
                      "§q6"), x, C1), C0), EqQ(Coeff($s("§q6"), x,
                          C5), C0),
                      RationalFunctionQ(u, x)))),
          IIntegrate(
              2098, Integrate(Times($p("§pm"),
                  Power($p("§qn"), CN1)), x_Symbol),
              Condition(
                  With(
                      List(Set(m, Expon($s("§pm"),
                          x)), Set(n,
                              Expon($s("§qn"), x))),
                      Condition(
                          Plus(
                              Simp(
                                  Times(Coeff($s("§pm"), x, m), Log($s("§qn")),
                                      Power(Times(n, Coeff($s("§qn"), x, n)), CN1)),
                                  x),
                              Dist(
                                  Simplify(
                                      Subtract($s("§pm"), Times(Coeff($s("§pm"), x, m),
                                          D($s(
                                              "§qn"), x),
                                          Power(Times(n, Coeff($s("§qn"), x, n)), CN1)))),
                                  Integrate(Power($s("§qn"), CN1), x), x)),
                          And(EqQ(m, Subtract(n, C1)),
                              EqQ(D(
                                  Simplify(
                                      Subtract($s("§pm"), Times(Coeff($s("§pm"), x, m),
                                          D($s("§qn"), x), Power(Times(n, Coeff($s("§qn"), x, n)),
                                              CN1)))),
                                  x), C0)))),
                  And(PolyQ($s("§pm"), x), PolyQ($s("§qn"), x)))),
          IIntegrate(
              2099, Integrate(Times($p("§pm"),
                  Power($p("§qn"), p_)), x_Symbol),
              Condition(
                  With(
                      List(Set(m, Expon($s("§pm"),
                          x)), Set(n,
                              Expon($s("§qn"), x))),
                      Condition(
                          Plus(Simp(
                              Times(Coeff($s("§pm"), x, m), Power($s("§qn"), Plus(p, C1)), Power(
                                  Times(n, Plus(p, C1), Coeff($s("§qn"), x, n)), CN1)),
                              x),
                              Dist(
                                  Simplify(
                                      Subtract($s("§pm"), Times(Coeff($s("§pm"), x, m),
                                          D($s(
                                              "§qn"), x),
                                          Power(Times(n, Coeff($s("§qn"), x, n)), CN1)))),
                                  Integrate(Power($s("§qn"), p), x), x)),
                          And(EqQ(m, Subtract(n, C1)),
                              EqQ(D(
                                  Simplify(
                                      Subtract($s("§pm"), Times(Coeff($s("§pm"), x, m),
                                          D($s("§qn"), x), Power(Times(n, Coeff($s("§qn"), x, n)),
                                              CN1)))),
                                  x), C0)))),
                  And(FreeQ(p, x), PolyQ($s("§pm"), x), PolyQ($s("§qn"), x), NeQ(p, CN1)))),
          IIntegrate(
              2100, Integrate(Times($p("§pm"),
                  Power($p("§qn"), CN1)), x_Symbol),
              Condition(
                  With(List(Set(m, Expon($s("§pm"), x)), Set(n, Expon($s("§qn"), x))),
                      Condition(Plus(
                          Simp(Times(Coeff($s("§pm"), x, m), Log($s("§qn")),
                              Power(Times(n, Coeff($s("§qn"), x, n)), CN1)), x),
                          Dist(Power(Times(n, Coeff($s("§qn"), x, n)), CN1),
                              Integrate(Times(
                                  ExpandToSum(Subtract(Times(n, Coeff($s("§qn"), x, n), $s("§pm")),
                                      Times(Coeff($s("§pm"), x, m), D($s("§qn"), x))), x),
                                  Power($s("§qn"), CN1)), x),
                              x)),
                          EqQ(m, Subtract(n, C1)))),
                  And(PolyQ($s("§pm"), x), PolyQ($s("§qn"), x)))));
}
