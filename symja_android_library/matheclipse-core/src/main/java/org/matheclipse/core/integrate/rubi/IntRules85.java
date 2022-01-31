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
class IntRules85 {
  public static IAST RULES =
      List(
          IIntegrate(1701,
              Integrate(
                  Times(Plus(A_, Times(B_DEFAULT, Sqr(x_))),
                      Power(Plus(d_, Times(e_DEFAULT,
                          Sqr(x_))), CN1),
                      Power(Plus(a_, Times(c_DEFAULT, Power(x_, C4))), CN1D2)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(
                          Times(
                              Plus(Times(BSymbol, d), Times(ASymbol, e)), Power(Times(C2, d, e),
                                  CN1)),
                          Integrate(Power(Plus(a, Times(c, Power(x, C4))), CN1D2), x), x),
                      Dist(
                          Times(Subtract(Times(BSymbol, d),
                              Times(ASymbol, e)), Power(Times(C2, d, e), CN1)),
                          Integrate(
                              Times(Subtract(d, Times(e, Sqr(x))),
                                  Power(
                                      Times(Plus(d, Times(e, Sqr(x))),
                                          Sqrt(Plus(a, Times(c, Power(x, C4))))),
                                      CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, c, d, e, ASymbol, BSymbol), x),
                      NeQ(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))), C0),
                      EqQ(Subtract(Times(c, Sqr(d)), Times(a, Sqr(e))), C0),
                      NeQ(Plus(Times(BSymbol, d), Times(ASymbol, e)), C0)))),
          IIntegrate(1702,
              Integrate(
                  Times(
                      Plus(A_, Times(B_DEFAULT, Sqr(
                          x_))),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1),
                      Power(Plus(a_, Times(b_DEFAULT, Sqr(x_)), Times(c_DEFAULT, Power(x_, C4))),
                          CN1D2)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Sqrt(Plus(ASymbol, Times(BSymbol, Sqr(x)))),
                          Sqrt(Plus(
                              Times(a, Power(ASymbol, CN1)),
                              Times(c, Sqr(x), Power(BSymbol, CN1)))),
                          Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))), CN1D2)),
                      Integrate(Times(Sqrt(Plus(ASymbol, Times(BSymbol, Sqr(x)))),
                          Power(Times(Plus(d, Times(e, Sqr(x))),
                              Sqrt(Plus(Times(a, Power(ASymbol, CN1)),
                                  Times(c, Sqr(x), Power(BSymbol, CN1))))),
                              CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, ASymbol, BSymbol), x),
                      NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      NeQ(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a, Sqr(e))), C0),
                      EqQ(Plus(Times(c, Sqr(ASymbol)), Times(CN1, b, ASymbol, BSymbol),
                          Times(a, Sqr(BSymbol))), C0)))),
          IIntegrate(1703,
              Integrate(
                  Times(Plus(A_, Times(B_DEFAULT, Sqr(x_))),
                      Power(Plus(d_, Times(e_DEFAULT,
                          Sqr(x_))), CN1),
                      Power(Plus(a_, Times(c_DEFAULT, Power(x_, C4))), CN1D2)),
                  x_Symbol),
              Condition(
                  Dist(Times(Sqrt(Plus(ASymbol, Times(BSymbol, Sqr(x)))),
                      Sqrt(Plus(Times(a, Power(ASymbol, CN1)),
                          Times(c, Sqr(x), Power(BSymbol, CN1)))),
                      Power(Plus(a, Times(c, Power(x, C4))), CN1D2)),
                      Integrate(
                          Times(Sqrt(Plus(ASymbol, Times(BSymbol, Sqr(x)))),
                              Power(
                                  Times(Plus(d, Times(e, Sqr(x))),
                                      Sqrt(Plus(Times(a, Power(ASymbol, CN1)),
                                          Times(c, Sqr(x), Power(BSymbol, CN1))))),
                                  CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, c, d, e, ASymbol, BSymbol), x),
                      NeQ(Plus(Times(c, Sqr(d)), Times(a,
                          Sqr(e))), C0),
                      EqQ(Plus(Times(c, Sqr(ASymbol)), Times(a, Sqr(BSymbol))), C0)))),
          IIntegrate(1704,
              Integrate(
                  Times(
                      Plus(A_, Times(B_DEFAULT, Sqr(
                          x_))),
                      Power(Plus(d_,
                          Times(e_DEFAULT, Sqr(x_))), CN1),
                      Power(
                          Plus(a_, Times(b_DEFAULT, Sqr(x_)),
                              Times(c_DEFAULT, Power(x_, C4))),
                          CN1D2)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(q,
                          Sqrt(Subtract(Sqr(b), Times(C4, a, c))))),
                      Condition(
                          Subtract(
                              Dist(
                                  Times(
                                      Subtract(Times(C2, a, BSymbol), Times(ASymbol,
                                          Plus(b, q))),
                                      Power(Subtract(Times(C2, a, e), Times(d, Plus(b, q))), CN1)),
                                  Integrate(
                                      Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))),
                                          CN1D2),
                                      x),
                                  x),
                              Dist(
                                  Times(
                                      Subtract(Times(BSymbol, d), Times(ASymbol, e)),
                                      Power(Subtract(Times(C2, a, e), Times(d, Plus(b, q))), CN1)),
                                  Integrate(Times(Plus(Times(C2, a), Times(Plus(b, q), Sqr(x))),
                                      Power(Times(Plus(d, Times(e, Sqr(x))),
                                          Sqrt(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))))),
                                          CN1)),
                                      x),
                                  x)),
                          RationalQ(q))),
                  And(FreeQ(List(a, b, c, d, e, ASymbol, BSymbol), x),
                      GtQ(Subtract(Sqr(b), Times(C4, a,
                          c)), C0),
                      NeQ(Plus(Times(c, Sqr(
                          d)), Times(CN1, b, d,
                              e),
                          Times(a, Sqr(e))), C0),
                      NeQ(Plus(
                          Times(c, Sqr(ASymbol)), Times(CN1, b, ASymbol, BSymbol), Times(a,
                              Sqr(BSymbol))),
                          C0)))),
          IIntegrate(1705,
              Integrate(
                  Times(Plus(A_, Times(B_DEFAULT, Sqr(x_))),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1), Power(
                          Plus(a_, Times(c_DEFAULT, Power(x_, C4))), CN1D2)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(q,
                          Sqrt(Times(CN1, a, c)))),
                      Condition(
                          Subtract(Dist(Times(Subtract(Times(a, BSymbol), Times(ASymbol, q)), Power(
                              Subtract(Times(a, e), Times(d, q)), CN1)), Integrate(
                                  Power(Plus(a, Times(c, Power(x, C4))), CN1D2), x),
                              x),
                              Dist(
                                  Times(
                                      Subtract(Times(BSymbol, d), Times(ASymbol,
                                          e)),
                                      Power(Subtract(Times(a, e), Times(d, q)), CN1)),
                                  Integrate(Times(Plus(a, Times(q, Sqr(x))),
                                      Power(Times(Plus(d, Times(e, Sqr(x))),
                                          Sqrt(Plus(a, Times(c, Power(x, C4))))), CN1)),
                                      x),
                                  x)),
                          RationalQ(q))),
                  And(FreeQ(List(a, c, d, e, ASymbol, BSymbol), x), GtQ(Times(CN1, a, c), C0),
                      EqQ(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))), C0), NeQ(
                          Plus(Times(c, Sqr(ASymbol)), Times(a, Sqr(BSymbol))), C0)))),
          IIntegrate(1706,
              Integrate(
                  Times(
                      Plus(A_, Times(B_DEFAULT, Sqr(
                          x_))),
                      Power(Plus(d_,
                          Times(e_DEFAULT, Sqr(x_))), CN1),
                      Power(
                          Plus(a_, Times(b_DEFAULT, Sqr(
                              x_)), Times(c_DEFAULT,
                                  Power(x_, C4))),
                          CN1D2)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(q,
                          Rt(Times(BSymbol, Power(ASymbol, CN1)), C2))),
                      Plus(
                          Negate(
                              Simp(
                                  Times(Subtract(Times(BSymbol, d), Times(ASymbol, e)),
                                      ArcTan(Times(
                                          Rt(Plus(Negate(b), Times(c, d, Power(e, CN1)),
                                              Times(a, e, Power(d, CN1))), C2),
                                          x,
                                          Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))),
                                              CN1D2))),
                                      Power(Times(C2, d, e,
                                          Rt(Plus(Negate(b), Times(c, d, Power(e, CN1)),
                                              Times(a, e, Power(d, CN1))), C2)),
                                          CN1)),
                                  x)),
                          Simp(
                              Times(Plus(Times(BSymbol, d), Times(ASymbol, e)),
                                  Plus(ASymbol, Times(BSymbol,
                                      Sqr(x))),
                                  Sqrt(Times(Sqr(ASymbol),
                                      Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))),
                                      Power(Times(a, Sqr(Plus(ASymbol, Times(BSymbol, Sqr(x))))),
                                          CN1))),
                                  EllipticPi(
                                      Cancel(Times(CN1,
                                          Sqr(Subtract(Times(BSymbol, d), Times(ASymbol, e))),
                                          Power(Times(C4, d, e, ASymbol, BSymbol), CN1))),
                                      Times(C2, ArcTan(Times(q, x))),
                                      Subtract(C1D2,
                                          Times(b, ASymbol, Power(Times(C4, a, BSymbol), CN1)))),
                                  Power(
                                      Times(C4, d, e, ASymbol, q,
                                          Sqrt(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))))),
                                      CN1)),
                              x))),
                  And(FreeQ(List(a, b, c, d, e, ASymbol, BSymbol), x),
                      NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      NeQ(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a, Sqr(e))), C0),
                      NeQ(Subtract(Times(c, Sqr(
                          d)), Times(a,
                              Sqr(e))),
                          C0),
                      PosQ(Times(c, Power(a,
                          CN1))),
                      EqQ(Subtract(Times(c, Sqr(ASymbol)), Times(a, Sqr(BSymbol))), C0)))),
          IIntegrate(1707,
              Integrate(
                  Times(Plus(A_, Times(B_DEFAULT, Sqr(x_))),
                      Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1), Power(
                          Plus(a_, Times(c_DEFAULT, Power(x_, C4))), CN1D2)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(q,
                          Rt(Times(BSymbol, Power(ASymbol, CN1)), C2))),
                      Plus(
                          Negate(Simp(Times(Subtract(Times(BSymbol, d), Times(ASymbol, e)),
                              ArcTan(Times(
                                  Rt(Plus(Times(c, d, Power(e, CN1)), Times(a, e, Power(d, CN1))),
                                      C2),
                                  x, Power(Plus(a, Times(c, Power(x, C4))), CN1D2))),
                              Power(
                                  Times(
                                      C2, d, e, Rt(
                                          Plus(Times(c, d, Power(e, CN1)),
                                              Times(a, e, Power(d, CN1))),
                                          C2)),
                                  CN1)),
                              x)),
                          Simp(Times(Plus(Times(BSymbol, d), Times(ASymbol, e)),
                              Plus(ASymbol, Times(BSymbol, Sqr(x))),
                              Sqrt(Times(
                                  Sqr(ASymbol), Plus(a, Times(c, Power(x, C4))),
                                  Power(Times(a, Sqr(Plus(ASymbol, Times(BSymbol, Sqr(x))))),
                                      CN1))),
                              EllipticPi(
                                  Cancel(Times(CN1,
                                      Sqr(Subtract(Times(BSymbol, d), Times(ASymbol, e))),
                                      Power(Times(C4, d, e, ASymbol, BSymbol), CN1))),
                                  Times(C2, ArcTan(Times(q, x))), C1D2),
                              Power(Times(C4, d, e, ASymbol, q,
                                  Sqrt(Plus(a, Times(c, Power(x, C4))))), CN1)),
                              x))),
                  And(FreeQ(List(a, c, d, e, ASymbol, BSymbol), x),
                      NeQ(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))), C0),
                      NeQ(Subtract(Times(c, Sqr(
                          d)), Times(a,
                              Sqr(e))),
                          C0),
                      PosQ(Times(c, Power(a,
                          CN1))),
                      EqQ(Subtract(Times(c, Sqr(ASymbol)), Times(a, Sqr(BSymbol))), C0)))),
          IIntegrate(1708,
              Integrate(
                  Times(
                      Plus(A_DEFAULT, Times(B_DEFAULT, Sqr(
                          x_))),
                      Power(Plus(d_, Times(e_DEFAULT,
                          Sqr(x_))), CN1),
                      Power(Plus(a_, Times(b_DEFAULT, Sqr(x_)), Times(c_DEFAULT, Power(x_, C4))),
                          CN1D2)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(q,
                          Rt(Times(c, Power(a, CN1)), C2))),
                      Plus(Dist(
                          Times(Subtract(Times(ASymbol, Plus(Times(c, d), Times(a, e, q))), Times(a,
                              BSymbol, Plus(e, Times(d, q)))), Power(
                                  Subtract(Times(c, Sqr(d)), Times(a, Sqr(e))), CN1)),
                          Integrate(Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))), CN1D2),
                              x),
                          x),
                          Dist(
                              Times(a, Subtract(Times(BSymbol, d), Times(ASymbol, e)),
                                  Plus(e, Times(d, q)),
                                  Power(Subtract(Times(c, Sqr(d)), Times(a, Sqr(e))), CN1)),
                              Integrate(
                                  Times(Plus(C1, Times(q, Sqr(x))),
                                      Power(Times(Plus(d, Times(e, Sqr(x))),
                                          Sqrt(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))))),
                                          CN1)),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e, ASymbol, BSymbol), x),
                      NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      NeQ(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a, Sqr(e))), C0),
                      NeQ(Subtract(Times(c, Sqr(d)), Times(a, Sqr(e))), C0),
                      PosQ(Times(c, Power(a, CN1))), NeQ(Subtract(Times(c, Sqr(ASymbol)),
                          Times(a, Sqr(BSymbol))), C0)))),
          IIntegrate(1709,
              Integrate(
                  Times(Plus(A_DEFAULT, Times(B_DEFAULT, Sqr(x_))),
                      Power(Plus(d_, Times(e_DEFAULT,
                          Sqr(x_))), CN1),
                      Power(Plus(a_, Times(c_DEFAULT, Power(x_, C4))), CN1D2)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(q,
                          Rt(Times(c, Power(a, CN1)), C2))),
                      Plus(
                          Dist(
                              Times(
                                  Subtract(
                                      Times(ASymbol, Plus(Times(c, d), Times(a, e,
                                          q))),
                                      Times(a, BSymbol, Plus(e, Times(d, q)))),
                                  Power(Subtract(Times(c, Sqr(d)), Times(a, Sqr(e))), CN1)),
                              Integrate(Power(Plus(a, Times(c, Power(x, C4))), CN1D2), x), x),
                          Dist(
                              Times(a, Subtract(Times(BSymbol, d), Times(ASymbol, e)),
                                  Plus(e, Times(d, q)),
                                  Power(Subtract(Times(c, Sqr(d)), Times(a, Sqr(e))), CN1)),
                              Integrate(Times(Plus(C1, Times(q, Sqr(x))),
                                  Power(Times(Plus(d, Times(e, Sqr(x))),
                                      Sqrt(Plus(a, Times(c, Power(x, C4))))), CN1)),
                                  x),
                              x))),
                  And(FreeQ(List(a, c, d, e, ASymbol, BSymbol), x),
                      NeQ(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))), C0),
                      NeQ(Subtract(Times(c, Sqr(
                          d)), Times(a,
                              Sqr(e))),
                          C0),
                      PosQ(Times(c, Power(a,
                          CN1))),
                      NeQ(Subtract(Times(c, Sqr(ASymbol)), Times(a, Sqr(BSymbol))), C0)))),
          IIntegrate(1710,
              Integrate(
                  Times(Plus(A_DEFAULT, Times(B_DEFAULT, Sqr(x_))),
                      Power(Plus(d_,
                          Times(e_DEFAULT, Sqr(x_))), CN1),
                      Power(
                          Plus(a_, Times(b_DEFAULT, Sqr(x_)),
                              Times(c_DEFAULT, Power(x_, C4))),
                          CN1D2)),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(
                          Times(BSymbol, Power(e,
                              CN1)),
                          Integrate(Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))), CN1D2),
                              x),
                          x),
                      Dist(Times(Subtract(Times(e, ASymbol), Times(d, BSymbol)), Power(e, CN1)),
                          Integrate(
                              Power(
                                  Times(Plus(d, Times(e, Sqr(x))),
                                      Sqrt(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))))),
                                  CN1),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, ASymbol, BSymbol), x),
                      NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      NeQ(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a, Sqr(e))), C0),
                      NeQ(Subtract(Times(c, Sqr(d)),
                          Times(a, Sqr(e))), C0),
                      NegQ(Times(c, Power(a, CN1)))))),
          IIntegrate(1711,
              Integrate(
                  Times(Plus(A_DEFAULT, Times(B_DEFAULT, Sqr(x_))),
                      Power(Plus(d_, Times(e_DEFAULT,
                          Sqr(x_))), CN1),
                      Power(Plus(a_, Times(c_DEFAULT, Power(x_, C4))), CN1D2)),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(Times(BSymbol, Power(e, CN1)), Integrate(
                          Power(Plus(a, Times(c, Power(x, C4))), CN1D2), x), x),
                      Dist(Times(Subtract(Times(e, ASymbol), Times(d, BSymbol)), Power(e, CN1)),
                          Integrate(
                              Power(Times(Plus(d, Times(e, Sqr(x))),
                                  Sqrt(Plus(a, Times(c, Power(x, C4))))), CN1),
                              x),
                          x)),
                  And(FreeQ(List(a, c, d, e, ASymbol, BSymbol), x),
                      NeQ(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))), C0),
                      NeQ(Subtract(Times(c, Sqr(d)),
                          Times(a, Sqr(e))), C0),
                      NegQ(Times(c, Power(a, CN1)))))),
          IIntegrate(1712,
              Integrate(
                  Times(
                      $p("§p4x"), Power(Plus(d_,
                          Times(e_DEFAULT, Sqr(x_))), CN1),
                      Power(
                          Plus(a_, Times(b_DEFAULT, Sqr(x_)),
                              Times(c_DEFAULT, Power(x_, C4))),
                          CN1D2)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(ASymbol, Coeff($s(
                              "§p4x"), x, C0)),
                          Set(BSymbol, Coeff($s("§p4x"), x,
                              C2)),
                          Set(CSymbol, Coeff($s("§p4x"), x, C4))),
                      Plus(
                          Negate(
                              Dist(
                                  Times(CSymbol, Power(e,
                                      CN2)),
                                  Integrate(
                                      Times(Subtract(d, Times(e, Sqr(x))),
                                          Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))),
                                              CN1D2)),
                                      x),
                                  x)),
                          Dist(Power(e, CN2),
                              Integrate(Times(
                                  Plus(Times(CSymbol, Sqr(d)), Times(ASymbol, Sqr(e)),
                                      Times(BSymbol, Sqr(e), Sqr(x))),
                                  Power(
                                      Times(Plus(d, Times(e, Sqr(x))),
                                          Sqrt(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))))),
                                      CN1)),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e), x), PolyQ($s("§p4x"), Sqr(x), C2),
                      NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      NeQ(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e),
                          Times(a, Sqr(e))), C0),
                      EqQ(Subtract(Times(c, Sqr(d)), Times(a, Sqr(e))), C0)))),
          IIntegrate(1713,
              Integrate(
                  Times(
                      $p("§p4x"), Power(Plus(d_, Times(e_DEFAULT,
                          Sqr(x_))), CN1),
                      Power(Plus(a_, Times(c_DEFAULT, Power(x_, C4))), CN1D2)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(ASymbol, Coeff($s(
                              "§p4x"), x, C0)),
                          Set(BSymbol, Coeff($s("§p4x"), x,
                              C2)),
                          Set(CSymbol, Coeff($s("§p4x"), x, C4))),
                      Plus(
                          Negate(
                              Dist(
                                  Times(CSymbol, Power(e,
                                      CN2)),
                                  Integrate(
                                      Times(
                                          Subtract(d, Times(e, Sqr(x))), Power(Plus(a,
                                              Times(c, Power(x, C4))), CN1D2)),
                                      x),
                                  x)),
                          Dist(Power(e, CN2),
                              Integrate(
                                  Times(
                                      Plus(Times(CSymbol, Sqr(d)), Times(ASymbol, Sqr(e)),
                                          Times(BSymbol, Sqr(e), Sqr(x))),
                                      Power(
                                          Times(Plus(d, Times(e, Sqr(x))),
                                              Sqrt(Plus(a, Times(c, Power(x, C4))))),
                                          CN1)),
                                  x),
                              x))),
                  And(FreeQ(List(a, c, d, e), x), PolyQ($s("§p4x"), Sqr(x), C2),
                      NeQ(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))),
                          C0),
                      EqQ(Subtract(Times(c, Sqr(d)), Times(a, Sqr(e))), C0)))),
          IIntegrate(1714,
              Integrate(
                  Times($p("§p4x"), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1),
                      Power(
                          Plus(a_, Times(b_DEFAULT, Sqr(x_)),
                              Times(c_DEFAULT, Power(x_, C4))),
                          CN1D2)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(q, Rt(Times(c, Power(a, CN1)), C2)),
                          Set(ASymbol, Coeff($s(
                              "§p4x"), x, C0)),
                          Set(BSymbol, Coeff($s("§p4x"), x,
                              C2)),
                          Set(CSymbol, Coeff($s("§p4x"), x, C4))),
                      Plus(
                          Negate(
                              Dist(
                                  Times(CSymbol, Power(Times(e, q),
                                      CN1)),
                                  Integrate(
                                      Times(Subtract(C1, Times(q, Sqr(x))),
                                          Power(
                                              Plus(a, Times(b, Sqr(x)),
                                                  Times(c, Power(x, C4))),
                                              CN1D2)),
                                      x),
                                  x)),
                          Dist(
                              Power(Times(c,
                                  e), CN1),
                              Integrate(
                                  Times(
                                      Plus(Times(ASymbol, c, e), Times(a, CSymbol, d, q),
                                          Times(
                                              Subtract(Times(BSymbol, c, e),
                                                  Times(CSymbol,
                                                      Subtract(Times(c, d), Times(a, e, q)))),
                                              Sqr(x))),
                                      Power(Times(Plus(d, Times(e, Sqr(x))),
                                          Sqrt(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))))),
                                          CN1)),
                                  x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e), x), PolyQ($s("§p4x"), Sqr(x), C2),
                      NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      NeQ(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a, Sqr(e))), C0),
                      NeQ(Subtract(Times(c, Sqr(d)), Times(a,
                          Sqr(e))), C0),
                      PosQ(Times(c, Power(a, CN1))), Not(GtQ(Subtract(Sqr(b), Times(C4, a,
                          c)), C0))))),
          IIntegrate(1715,
              Integrate(Times($p("§p4x"), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1),
                  Power(Plus(a_, Times(c_DEFAULT, Power(x_, C4))), CN1D2)), x_Symbol),
              Condition(
                  With(
                      List(Set(q, Rt(Times(c, Power(a, CN1)), C2)),
                          Set(ASymbol, Coeff($s(
                              "§p4x"), x, C0)),
                          Set(BSymbol, Coeff($s("§p4x"), x,
                              C2)),
                          Set(CSymbol, Coeff($s("§p4x"), x, C4))),
                      Plus(
                          Negate(
                              Dist(
                                  Times(CSymbol, Power(Times(e, q),
                                      CN1)),
                                  Integrate(
                                      Times(
                                          Subtract(C1, Times(q,
                                              Sqr(x))),
                                          Power(Plus(a, Times(c, Power(x, C4))), CN1D2)),
                                      x),
                                  x)),
                          Dist(Power(Times(c, e), CN1),
                              Integrate(
                                  Times(
                                      Plus(Times(ASymbol, c, e), Times(a, CSymbol, d, q),
                                          Times(
                                              Subtract(Times(BSymbol, c, e),
                                                  Times(CSymbol,
                                                      Subtract(Times(c, d), Times(a, e, q)))),
                                              Sqr(x))),
                                      Power(Times(Plus(d, Times(e, Sqr(x))),
                                          Sqrt(Plus(a, Times(c, Power(x, C4))))), CN1)),
                                  x),
                              x))),
                  And(FreeQ(List(a, c, d, e), x), PolyQ($s("§p4x"), Sqr(x), C2),
                      NeQ(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))), C0),
                      NeQ(Subtract(Times(c, Sqr(d)),
                          Times(a, Sqr(e))), C0),
                      PosQ(Times(c, Power(a, CN1)))))),
          IIntegrate(1716,
              Integrate(
                  Times(
                      $p("§p4x"), Power(Plus(d_,
                          Times(e_DEFAULT, Sqr(x_))), CN1),
                      Power(
                          Plus(a_, Times(b_DEFAULT, Sqr(
                              x_)), Times(c_DEFAULT,
                                  Power(x_, C4))),
                          CN1D2)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(ASymbol, Coeff($s("§p4x"), x, C0)),
                          Set(BSymbol, Coeff($s("§p4x"), x, C2)), Set(CSymbol,
                              Coeff($s("§p4x"), x, C4))),
                      Plus(
                          Negate(
                              Dist(Power(e, CN2),
                                  Integrate(
                                      Times(
                                          Subtract(Subtract(Times(CSymbol, d), Times(BSymbol, e)),
                                              Times(CSymbol, e, Sqr(x))),
                                          Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))),
                                              CN1D2)),
                                      x),
                                  x)),
                          Dist(
                              Times(Plus(Times(CSymbol, Sqr(d)), Times(CN1, BSymbol, d, e),
                                  Times(ASymbol, Sqr(e))), Power(e, CN2)),
                              Integrate(Power(
                                  Times(Plus(d, Times(e, Sqr(x))),
                                      Sqrt(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))))),
                                  CN1), x),
                              x))),
                  And(FreeQ(List(a, b, c, d, e), x), PolyQ($s("§p4x"), Sqr(x), C2),
                      NeQ(Subtract(Sqr(b), Times(C4, a,
                          c)), C0),
                      NeQ(Plus(Times(c, Sqr(
                          d)), Times(CN1, b, d,
                              e),
                          Times(a, Sqr(e))),
                          C0),
                      NeQ(Subtract(Times(c, Sqr(d)), Times(a, Sqr(e))), C0)))),
          IIntegrate(1717,
              Integrate(Times($p("§p4x"), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1),
                  Power(Plus(a_, Times(c_DEFAULT, Power(x_, C4))), CN1D2)), x_Symbol),
              Condition(
                  With(
                      List(Set(ASymbol, Coeff($s("§p4x"), x, C0)),
                          Set(BSymbol, Coeff($s("§p4x"), x, C2)), Set(CSymbol,
                              Coeff($s("§p4x"), x, C4))),
                      Plus(
                          Negate(
                              Dist(Power(e, CN2),
                                  Integrate(Times(Subtract(
                                      Subtract(Times(CSymbol, d), Times(BSymbol, e)), Times(CSymbol,
                                          e, Sqr(x))),
                                      Power(Plus(a, Times(c, Power(x, C4))), CN1D2)), x),
                                  x)),
                          Dist(
                              Times(Plus(Times(CSymbol, Sqr(d)), Times(CN1, BSymbol, d, e),
                                  Times(ASymbol, Sqr(e))), Power(e, CN2)),
                              Integrate(Power(Times(Plus(d, Times(e, Sqr(x))),
                                  Sqrt(Plus(a, Times(c, Power(x, C4))))), CN1), x),
                              x))),
                  And(FreeQ(List(a, c, d, e), x), PolyQ($s("§p4x"), Sqr(x), C2),
                      NeQ(Plus(Times(c, Sqr(d)),
                          Times(a, Sqr(e))), C0),
                      NeQ(Subtract(Times(c, Sqr(d)), Times(a, Sqr(e))), C0)))),
          IIntegrate(1718,
              Integrate(Times($p("§px"), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), CN1),
                  Power(Plus(a_, Times(b_DEFAULT, Sqr(x_)), Times(c_DEFAULT, Power(x_, C4))),
                      CN1D2)),
                  x_Symbol),
              Condition(
                  With(List(Set(q, Expon($s("§px"), x))),
                      Condition(
                          Plus(Simp(Times(Coeff($s("§px"), x, q), Power(x, Subtract(q, C5)),
                              Sqrt(Plus(a, Times(b, Sqr(x)),
                                  Times(c, Power(x, C4)))),
                              Power(Times(c, e, Subtract(q, C3)), CN1)), x), Dist(
                                  Power(Times(c, e,
                                      Subtract(q, C3)), CN1),
                                  Integrate(Times(
                                      Subtract(Times(c, e, Subtract(q, C3), $s("§px")),
                                          Times(Coeff($s("§px"), x, q), Power(x, Subtract(q, C6)),
                                              Plus(d, Times(e, Sqr(x))),
                                              Plus(Times(a, Subtract(q, C5)),
                                                  Times(b, Subtract(q, C4), Sqr(x)),
                                                  Times(c, Subtract(q, C3), Power(x, C4))))),
                                      Power(Times(Plus(d, Times(e, Sqr(x))),
                                          Sqrt(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))))),
                                          CN1)),
                                      x),
                                  x)),
                          GtQ(q, C4))),
                  And(FreeQ(List(a, b, c, d, e), x), PolyQ($s("§px"), x),
                      NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0), NeQ(Plus(Times(c, Sqr(d)),
                          Times(CN1, b, d, e), Times(a, Sqr(e))), C0)))),
          IIntegrate(1719,
              Integrate(
                  Times(
                      $p("§px"), Power(Plus(d_, Times(e_DEFAULT,
                          Sqr(x_))), CN1),
                      Power(Plus(a_, Times(c_DEFAULT, Power(x_, C4))), CN1D2)),
                  x_Symbol),
              Condition(
                  With(List(Set(q, Expon($s("§px"), x))),
                      Condition(
                          Plus(
                              Simp(Times(Coeff($s("§px"), x, q), Power(x, Subtract(q, C5)),
                                  Sqrt(Plus(a, Times(c, Power(x, C4)))), Power(
                                      Times(c, e, Subtract(q, C3)), CN1)),
                                  x),
                              Dist(
                                  Power(Times(c, e,
                                      Subtract(q, C3)), CN1),
                                  Integrate(Times(
                                      Subtract(Times(c, e, Subtract(q, C3), $s("§px")),
                                          Times(Coeff($s("§px"), x, q), Power(x, Subtract(q, C6)),
                                              Plus(d, Times(e, Sqr(x))),
                                              Plus(Times(a, Subtract(q, C5)),
                                                  Times(c, Subtract(q, C3), Power(x, C4))))),
                                      Power(Times(Plus(d, Times(e, Sqr(x))),
                                          Sqrt(Plus(a, Times(c, Power(x, C4))))), CN1)),
                                      x),
                                  x)),
                          GtQ(q, C4))),
                  And(FreeQ(List(a, c, d,
                      e), x), PolyQ($s("§px"),
                          x),
                      NeQ(Plus(Times(c, Sqr(d)), Times(a, Sqr(e))), C0)))),
          IIntegrate(1720,
              Integrate(
                  Times(
                      $p("§px"), Power(Plus(d_, Times(e_DEFAULT, Sqr(x_))), q_DEFAULT),
                      Power(
                          Plus(a_, Times(b_DEFAULT, Sqr(x_)), Times(c_DEFAULT, Power(x_, C4))),
                          p_)),
                  x_Symbol),
              Condition(
                  Integrate(ExpandIntegrand(
                      Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))), CN1D2),
                      Times($s("§px"), Power(Plus(d, Times(e, Sqr(x))), q),
                          Power(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))), Plus(p, C1D2))),
                      x), x),
                  And(FreeQ(List(a, b, c, d, e), x), PolyQ($s("§px"), Sqr(x)),
                      NeQ(Subtract(Sqr(b), Times(C4, a, c)), C0),
                      NeQ(Plus(Times(c, Sqr(d)), Times(CN1, b, d, e), Times(a, Sqr(e))), C0),
                      IntegerQ(Plus(p, C1D2)), IntegerQ(q)))));
}
