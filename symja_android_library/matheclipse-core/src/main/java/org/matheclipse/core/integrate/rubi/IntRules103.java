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
class IntRules103 {
  public static IAST RULES = List(
      IIntegrate(
          2061, Integrate(Power(P_,
              p_), x_Symbol),
          Condition(
              Integrate(ExpandToSum(Power(PSymbol, p), x),
                  x),
              And(PolyQ(PSymbol, x), IGtQ(p, C0)))),
      IIntegrate(2062, Integrate(Power(P_, p_), x_Symbol),
          Condition(Integrate(ExpandIntegrand(Power(PSymbol, p), x), x), And(PolyQ(PSymbol, x),
              IntegerQ(p), QuadraticProductQ(Factor(PSymbol), x)))),
      IIntegrate(2063,
          Integrate(
              Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(d_DEFAULT, Power(x_, C3))),
                  p_),
              x_Symbol),
          Condition(
              Dist(
                  Power(Times(Power(C3, Times(C3, p)),
                      Power(a, Times(C2, p))), CN1),
                  Integrate(
                      Times(
                          Power(Subtract(Times(C3, a),
                              Times(b, x)), p),
                          Power(Plus(Times(C3, a), Times(C2, b, x)), Times(C2, p))),
                      x),
                  x),
              And(FreeQ(List(a, b, d),
                  x), EqQ(Plus(Times(C4, Power(b, C3)), Times(ZZ(27L), Sqr(a), d)), C0),
                  IntegerQ(p)))),
      IIntegrate(2064,
          Integrate(
              Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(d_DEFAULT, Power(x_, C3))),
                  p_),
              x_Symbol),
          Condition(
              Dist(
                  Times(Power(Plus(a, Times(b, x), Times(d, Power(x, C3))), p),
                      Power(
                          Times(Power(Subtract(Times(C3, a), Times(b, x)), p),
                              Power(Plus(Times(C3, a), Times(C2, b, x)), Times(C2, p))),
                          CN1)),
                  Integrate(
                      Times(
                          Power(Subtract(Times(C3, a), Times(b, x)), p), Power(
                              Plus(Times(C3, a), Times(C2, b, x)), Times(C2, p))),
                      x),
                  x),
              And(FreeQ(List(a, b, d, p), x),
                  EqQ(Plus(Times(C4, Power(b, C3)), Times(ZZ(27L), Sqr(a), d)),
                      C0),
                  Not(IntegerQ(p))))),
      IIntegrate(2065,
          Integrate(
              Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(d_DEFAULT, Power(x_, C3))),
                  p_),
              x_Symbol),
          Condition(
              With(
                  List(
                      Set(r,
                          Rt(Plus(
                              Times(CN9, a, Sqr(
                                  d)),
                              Times(CSqrt3, d,
                                  Sqrt(
                                      Plus(
                                          Times(C4, Power(b, C3), d), Times(ZZ(27L), Sqr(a),
                                              Sqr(d)))))),
                              C3))),
                  Dist(
                      Power(Power(d,
                          Times(C2, p)), CN1),
                      Integrate(
                          Times(
                              Power(
                                  Simp(
                                      Plus(Times(Power(ZZ(18L), C1D3), b, d, Power(Times(C3, r),
                                          CN1)), Times(CN1, r,
                                              Power(ZZ(18L), CN1D3)),
                                          Times(d, x)),
                                      x),
                                  p),
                              Power(
                                  Simp(
                                      Plus(Times(C1D3, b, d),
                                          Times(
                                              Power(ZZ(
                                                  12L), C1D3),
                                              Sqr(b), Sqr(d), Power(Times(C3, Sqr(r)), CN1)),
                                          Times(Sqr(r),
                                              Power(Times(C3, Power(ZZ(12L), C1D3)), CN1)),
                                          Times(CN1, d,
                                              Subtract(
                                                  Times(Power(C2, C1D3), b, d,
                                                      Power(Times(Power(C3, C1D3), r), CN1)),
                                                  Times(r, Power(ZZ(18L), CN1D3))),
                                              x),
                                          Times(Sqr(d), Sqr(x))),
                                      x),
                                  p)),
                          x),
                      x)),
              And(FreeQ(List(a, b, d),
                  x), NeQ(Plus(Times(C4, Power(b, C3)), Times(ZZ(27L), Sqr(a), d)), C0),
                  IntegerQ(p)))),
      IIntegrate(2066,
          Integrate(
              Power(Plus(a_DEFAULT, Times(b_DEFAULT, x_), Times(d_DEFAULT, Power(x_, C3))),
                  p_),
              x_Symbol),
          Condition(
              With(
                  List(
                      Set(r,
                          Rt(Plus(Times(CN9, a, Sqr(d)),
                              Times(CSqrt3, d, Sqrt(Plus(Times(C4, Power(b, C3), d),
                                  Times(ZZ(27L), Sqr(a), Sqr(d)))))),
                              C3))),
                  Dist(Times(Power(Plus(a, Times(b, x), Times(d, Power(x, C3))), p),
                      Power(Times(
                          Power(
                              Simp(
                                  Plus(Times(Power(ZZ(18L), C1D3), b, d,
                                      Power(Times(C3, r), CN1)),
                                      Times(CN1, r, Power(ZZ(18L), CN1D3)), Times(d, x)),
                                  x),
                              p),
                          Power(Simp(Plus(Times(C1D3, b, d),
                              Times(Power(ZZ(12L), C1D3), Sqr(b), Sqr(d),
                                  Power(Times(C3, Sqr(r)), CN1)),
                              Times(Sqr(r), Power(Times(C3, Power(ZZ(12L), C1D3)), CN1)),
                              Times(
                                  CN1, d,
                                  Subtract(
                                      Times(
                                          Power(C2, C1D3), b, d,
                                          Power(Times(Power(C3, C1D3), r), CN1)),
                                      Times(r, Power(ZZ(18L), CN1D3))),
                                  x),
                              Times(Sqr(d), Sqr(x))), x), p)),
                          CN1)),
                      Integrate(
                          Times(
                              Power(
                                  Simp(
                                      Plus(Times(Power(ZZ(18L),
                                          C1D3), b, d, Power(Times(C3, r), CN1)), Times(CN1, r,
                                              Power(ZZ(18L), CN1D3)),
                                          Times(d, x)),
                                      x),
                                  p),
                              Power(
                                  Simp(
                                      Plus(Times(C1D3, b, d),
                                          Times(
                                              Power(ZZ(
                                                  12L), C1D3),
                                              Sqr(b), Sqr(d), Power(Times(C3, Sqr(r)), CN1)),
                                          Times(Sqr(r),
                                              Power(Times(C3, Power(ZZ(12L), C1D3)), CN1)),
                                          Times(CN1, d,
                                              Subtract(
                                                  Times(Power(C2, C1D3), b, d,
                                                      Power(Times(Power(C3, C1D3), r), CN1)),
                                                  Times(r, Power(ZZ(18L), CN1D3))),
                                              x),
                                          Times(Sqr(d), Sqr(x))),
                                      x),
                                  p)),
                          x),
                      x)),
              And(FreeQ(List(a, b, d, p), x),
                  NeQ(Plus(Times(C4, Power(b, C3)),
                      Times(ZZ(27L), Sqr(a), d)), C0),
                  Not(IntegerQ(p))))),
      IIntegrate(
          2067, Integrate(Power($p("§p3"),
              p_), x_Symbol),
          Condition(
              With(
                  List(
                      Set(a, Coeff($s("§p3"), x, C0)), Set(b, Coeff($s("§p3"), x, C1)), Set(c,
                          Coeff($s("§p3"), x, C2)),
                      Set(d, Coeff($s("§p3"), x, C3))),
                  Condition(
                      Subst(
                          Integrate(
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
                                  p),
                              x),
                          x, Plus(x, Times(c, Power(Times(C3, d), CN1)))),
                      NeQ(c, C0))),
              And(FreeQ(p, x), PolyQ($s("§p3"), x, C3)))),
      IIntegrate(
          2068, Integrate(Power($p("§p4"),
              p_), x_Symbol),
          Condition(
              With(
                  List(Set(a, Coeff($s("§p4"), x, C0)), Set(b, Coeff($s("§p4"), x, C1)),
                      Set(c, Coeff($s("§p4"), x, C2)), Set(d, Coeff($s("§p4"), x,
                          C3)),
                      Set(e, Coeff($s("§p4"), x, C4))),
                  Condition(
                      Dist(Power(Power(a, Times(C3, p)), CN1),
                          Integrate(
                              ExpandIntegrand(
                                  Power(
                                      Times(Power(Subtract(a, Times(b, x)), p),
                                          Power(Power(Subtract(Power(a, C5),
                                              Times(Power(b, C5), Power(x, C5))), p), CN1)),
                                      CN1),
                                  x),
                              x),
                          x),
                      And(NeQ(a, C0), EqQ(c, Times(Sqr(b), Power(a, CN1))),
                          EqQ(d, Times(Power(b, C3), Power(a, CN2))),
                          EqQ(e, Times(Power(b, C4), Power(a, CN3)))))),
              And(FreeQ(p, x), PolyQ($s("§p4"), x, C4), ILtQ(p, C0)))),
      IIntegrate(
          2069, Integrate(Power($p("§p4"),
              p_), x_Symbol),
          Condition(
              With(
                  List(Set(a, Coeff($s("§p4"), x, C0)), Set(b, Coeff($s("§p4"), x, C1)),
                      Set(c, Coeff($s(
                          "§p4"), x, C2)),
                      Set(d, Coeff($s("§p4"), x, C3)), Set(e, Coeff($s("§p4"), x, C4))),
                  Condition(
                      Dist(Times(ZZ(-16L), Sqr(a)),
                          Subst(
                              Integrate(Times(C1,
                                  Power(Times(a,
                                      Plus(Times(CN3, Power(b, C4)), Times(ZZ(16L), a, Sqr(b), c),
                                          Times(CN1, ZZ(64L), Sqr(a), b, d),
                                          Times(ZZ(256L), Power(a, C3), e),
                                          Times(CN1, ZZ(32L), Sqr(a),
                                              Subtract(Times(C3, Sqr(b)), Times(C8, a, c)), Sqr(x)),
                                          Times(ZZ(256L), Power(a, C4), Power(x, C4))),
                                      Power(Subtract(b, Times(C4, a, x)), CN4)), p),
                                  Power(Subtract(b, Times(C4, a, x)), CN2)), x),
                              x, Plus(Times(b, Power(Times(C4, a), CN1)), Power(x, CN1))),
                          x),
                      And(NeQ(a, C0), NeQ(b, C0),
                          EqQ(Plus(Power(b, C3), Times(CN1, C4, a, b, c), Times(C8, Sqr(a), d)),
                              C0)))),
              And(FreeQ(p, x), PolyQ($s("§p4"), x, C4), IntegerQ(Times(C2, p)), Not(IGtQ(p, C0))))),
      IIntegrate(2070, Integrate(Power($p("§q6"), p_), x_Symbol),
          Condition(
              With(
                  List(Set(a, Coeff($s("§q6"), x, C0)), Set(b, Coeff($s("§q6"), x, C2)),
                      Set(c, Coeff($s("§q6"), x, C3)), Set(d, Coeff($s("§q6"), x, C4)),
                      Set(e, Coeff($s("§q6"), x, C6))),
                  Condition(
                      Dist(Power(Times(Power(C3, Times(C3, p)), Power(a, Times(C2, p))), CN1),
                          Integrate(
                              ExpandIntegrand(Times(
                                  Power(Plus(Times(C3, a), Times(C3, Sqr(Rt(a, C3)), Rt(c, C3), x),
                                      Times(b, Sqr(x))), p),
                                  Power(Plus(Times(C3, a),
                                      Times(CN1, C3, Power(CN1, C1D3), Sqr(Rt(a, C3)), Rt(c, C3),
                                          x),
                                      Times(b, Sqr(x))), p),
                                  Power(Plus(Times(C3, a),
                                      Times(C3, Power(CN1, QQ(2L, 3L)), Sqr(Rt(a, C3)), Rt(c, C3),
                                          x),
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
      IIntegrate(2071,
          Integrate(Times(Sqrt(v_),
              Power(Plus(d_, Times(e_DEFAULT, Power(x_, C4))), CN1)), x_Symbol),
          Condition(With(
              List(Set(a, Coeff(v, x, C0)), Set(b, Coeff(v, x, C2)), Set(c, Coeff(v, x, C4))),
              Condition(Dist(Times(a, Power(d, CN1)),
                  Subst(
                      Integrate(Power(Plus(C1, Times(CN1, C2, b, Sqr(x)),
                          Times(Subtract(Sqr(b), Times(C4, a, c)), Power(x, C4))), CN1), x),
                      x, Times(x, Power(v, CN1D2))),
                  x), And(EqQ(Plus(Times(c, d), Times(a, e)), C0), PosQ(Times(a, c))))),
              And(FreeQ(List(d, e), x), PolyQ(v, Sqr(x), C2)))),
      IIntegrate(2072,
          Integrate(
              Times(
                  Sqrt(Plus(a_, Times(b_DEFAULT, Sqr(
                      x_)), Times(c_DEFAULT,
                          Power(x_, C4)))),
                  Power(Plus(d_, Times(e_DEFAULT, Power(x_, C4))), CN1)),
              x_Symbol),
          Condition(
              With(
                  List(Set(q,
                      Sqrt(Subtract(Sqr(b), Times(C4, a, c))))),
                  Plus(
                      Negate(
                          Simp(
                              Times(a, Sqrt(Plus(b, q)),
                                  ArcTan(
                                      Times(Sqrt(Plus(b, q)), x,
                                          Plus(b, Negate(q), Times(C2, c, Sqr(x))),
                                          Power(
                                              Times(C2, CSqrt2, Rt(Times(CN1, a, c), C2), Sqrt(Plus(
                                                  a, Times(b, Sqr(x)), Times(c, Power(x, C4))))),
                                              CN1))),
                                  Power(Times(C2, CSqrt2, Rt(Times(CN1, a, c), C2), d), CN1)),
                              x)),
                      Simp(
                          Times(a, Sqrt(Plus(Negate(b), q)),
                              ArcTanh(Times(Sqrt(Plus(Negate(b), q)), x,
                                  Plus(b, q, Times(C2, c, Sqr(x))),
                                  Power(
                                      Times(C2, CSqrt2, Rt(Times(CN1, a, c), C2),
                                          Sqrt(Plus(a, Times(b, Sqr(x)), Times(c, Power(x, C4))))),
                                      CN1))),
                              Power(Times(C2, CSqrt2, Rt(Times(CN1, a, c), C2), d), CN1)),
                          x))),
              And(FreeQ(List(a, b, c, d,
                  e), x), EqQ(Plus(Times(c, d), Times(a, e)),
                      C0),
                  NegQ(Times(a, c))))),
      IIntegrate(2073, Integrate(Times(Power(P_, p_), Power(Q_, q_DEFAULT)), x_Symbol),
          Condition(
              With(List(Set($s("§pp"), Factor(ReplaceAll(PSymbol, Rule(x, Sqrt(x)))))),
                  Condition(Integrate(ExpandIntegrand(
                      Times(Power(ReplaceAll($s("§pp"), Rule(x, Sqr(x))), p), Power(QSymbol, q)),
                      x), x), Not(
                          SumQ(NonfreeFactors($s("§pp"), x))))),
              And(FreeQ(q, x), PolyQ(PSymbol, Sqr(x)), PolyQ(QSymbol, x), ILtQ(p, C0)))),
      IIntegrate(
          2074, Integrate(Times(Power(P_, p_),
              Power(Q_, q_DEFAULT)), x_Symbol),
          Condition(
              With(List(Set($s("§pp"), Factor(PSymbol))),
                  Condition(
                      Integrate(ExpandIntegrand(Times(Power($s("§pp"), p), Power(QSymbol, q)), x),
                          x),
                      Not(SumQ(NonfreeFactors($s("§pp"), x))))),
              And(FreeQ(q, x), PolyQ(PSymbol, x), PolyQ(QSymbol, x), IntegerQ(p),
                  NeQ(PSymbol, x)))),
      IIntegrate(
          2075, Integrate(Times(Power(P_, p_),
              $p("§qm")), x_Symbol),
          Condition(
              With(List(Set($s("§pp"), Factor(PSymbol))),
                  Condition(
                      Integrate(ExpandIntegrand(Times(Power($s("§pp"), p), $s("§qm")), x),
                          x),
                      QuadraticProductQ($s("§pp"), x))),
              And(PolyQ($s("§qm"), x), PolyQ(PSymbol, x), ILtQ(p, C0)))),
      IIntegrate(2076,
          Integrate(
              Times(Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT),
                  Power(Plus(a_, Times(b_DEFAULT, x_), Times(d_DEFAULT, Power(x_, C3))),
                      p_DEFAULT)),
              x_Symbol),
          Condition(
              Dist(
                  Power(Times(Power(C3, Times(C3, p)),
                      Power(a, Times(C2, p))), CN1),
                  Integrate(Times(Power(Plus(e, Times(f, x)), m),
                      Power(Subtract(Times(C3, a), Times(b, x)), p), Power(
                          Plus(Times(C3, a), Times(C2, b, x)), Times(C2, p))),
                      x),
                  x),
              And(FreeQ(List(a, b, d, e, f,
                  m), x), EqQ(Plus(Times(C4, Power(b, C3)), Times(ZZ(27L), Sqr(a), d)),
                      C0),
                  IntegerQ(p)))),
      IIntegrate(2077,
          Integrate(
              Times(Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT),
                  Power(Plus(a_, Times(b_DEFAULT, x_), Times(d_DEFAULT, Power(x_, C3))), p_)),
              x_Symbol),
          Condition(
              Dist(
                  Times(Power(Plus(a, Times(b, x), Times(d, Power(x, C3))), p),
                      Power(Times(Power(Subtract(Times(C3, a), Times(b, x)), p),
                          Power(Plus(Times(C3, a), Times(C2, b, x)), Times(C2, p))), CN1)),
                  Integrate(Times(Power(Plus(e, Times(f, x)), m),
                      Power(Subtract(Times(C3, a), Times(b, x)), p),
                      Power(Plus(Times(C3, a), Times(C2, b, x)), Times(C2, p))), x),
                  x),
              And(FreeQ(List(a, b, d, e, f, m, p), x),
                  EqQ(Plus(Times(C4, Power(b, C3)), Times(ZZ(27L), Sqr(a), d)),
                      C0),
                  Not(IntegerQ(p))))),
      IIntegrate(2078,
          Integrate(
              Times(Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT),
                  Power(Plus(a_, Times(b_DEFAULT, x_), Times(d_DEFAULT, Power(x_, C3))),
                      p_DEFAULT)),
              x_Symbol),
          Condition(
              Integrate(
                  ExpandIntegrand(
                      Times(Power(Plus(e, Times(f, x)), m),
                          Power(Plus(a, Times(b, x), Times(d, Power(x, C3))), p)),
                      x),
                  x),
              And(FreeQ(List(a, b, d, e, f, m), x), NeQ(Plus(Times(C4, Power(b,
                  C3)), Times(ZZ(27L), Sqr(a),
                      d)),
                  C0), IGtQ(p, C0)))),
      IIntegrate(2079,
          Integrate(
              Times(
                  Power(Plus(e_DEFAULT, Times(f_DEFAULT,
                      x_)), m_DEFAULT),
                  Power(Plus(a_, Times(b_DEFAULT, x_), Times(d_DEFAULT, Power(x_, C3))), p_)),
              x_Symbol),
          Condition(
              With(
                  List(
                      Set(r,
                          Rt(Plus(
                              Times(CN9, a, Sqr(
                                  d)),
                              Times(CSqrt3, d,
                                  Sqrt(
                                      Plus(
                                          Times(C4, Power(b, C3), d), Times(ZZ(27L), Sqr(a),
                                              Sqr(d)))))),
                              C3))),
                  Dist(
                      Power(Power(d,
                          Times(C2, p)), CN1),
                      Integrate(
                          Times(Power(Plus(e, Times(f, x)), m),
                              Power(
                                  Simp(
                                      Plus(Times(Power(ZZ(18L), C1D3), b, d, Power(
                                          Times(C3, r), CN1)), Times(CN1, r,
                                              Power(ZZ(18L), CN1D3)),
                                          Times(d, x)),
                                      x),
                                  p),
                              Power(
                                  Simp(
                                      Plus(Times(C1D3, b, d),
                                          Times(
                                              Power(ZZ(
                                                  12L), C1D3),
                                              Sqr(b), Sqr(d), Power(Times(C3, Sqr(r)), CN1)),
                                          Times(Sqr(r),
                                              Power(Times(C3, Power(ZZ(12L), C1D3)), CN1)),
                                          Times(CN1, d,
                                              Subtract(
                                                  Times(Power(C2, C1D3), b, d,
                                                      Power(Times(Power(C3, C1D3), r), CN1)),
                                                  Times(r, Power(ZZ(18L), CN1D3))),
                                              x),
                                          Times(Sqr(d), Sqr(x))),
                                      x),
                                  p)),
                          x),
                      x)),
              And(FreeQ(List(a, b, d, e, f, m), x), NeQ(Plus(Times(C4, Power(b, C3)),
                  Times(ZZ(27L), Sqr(a), d)), C0), ILtQ(p,
                      C0)))),
      IIntegrate(2080,
          Integrate(
              Times(Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT),
                  Power(Plus(a_, Times(b_DEFAULT, x_), Times(d_DEFAULT, Power(x_, C3))), p_)),
              x_Symbol),
          Condition(
              With(
                  List(
                      Set(r,
                          Rt(Plus(
                              Times(CN9, a, Sqr(
                                  d)),
                              Times(CSqrt3, d,
                                  Sqrt(
                                      Plus(
                                          Times(C4, Power(b, C3), d), Times(ZZ(27L), Sqr(a),
                                              Sqr(d)))))),
                              C3))),
                  Dist(
                      Times(
                          Power(Plus(a, Times(b, x),
                              Times(d, Power(x, C3))), p),
                          Power(
                              Times(
                                  Power(
                                      Simp(
                                          Plus(
                                              Times(Power(ZZ(18L), C1D3), b, d, Power(Times(C3,
                                                  r), CN1)),
                                              Times(CN1, r, Power(ZZ(18L), CN1D3)), Times(d, x)),
                                          x),
                                      p),
                                  Power(
                                      Simp(
                                          Plus(Times(C1D3, b, d),
                                              Times(
                                                  Power(ZZ(
                                                      12L), C1D3),
                                                  Sqr(b), Sqr(d), Power(Times(C3, Sqr(r)), CN1)),
                                              Times(Sqr(r), Power(
                                                  Times(C3, Power(ZZ(12L), C1D3)), CN1)),
                                              Times(CN1, d,
                                                  Subtract(
                                                      Times(Power(C2, C1D3), b, d,
                                                          Power(Times(Power(C3, C1D3), r), CN1)),
                                                      Times(r, Power(ZZ(18L), CN1D3))),
                                                  x),
                                              Times(Sqr(d), Sqr(x))),
                                          x),
                                      p)),
                              CN1)),
                      Integrate(
                          Times(Power(Plus(e, Times(f, x)), m),
                              Power(
                                  Simp(
                                      Plus(
                                          Times(Power(ZZ(18L), C1D3), b, d,
                                              Power(Times(C3, r), CN1)),
                                          Times(CN1, r, Power(ZZ(18L), CN1D3)), Times(d, x)),
                                      x),
                                  p),
                              Power(
                                  Simp(
                                      Plus(Times(C1D3, b, d),
                                          Times(
                                              Power(ZZ(
                                                  12L), C1D3),
                                              Sqr(b), Sqr(d), Power(Times(C3, Sqr(r)), CN1)),
                                          Times(Sqr(r),
                                              Power(Times(C3, Power(ZZ(12L), C1D3)), CN1)),
                                          Times(CN1, d,
                                              Subtract(
                                                  Times(Power(C2, C1D3), b, d,
                                                      Power(Times(Power(C3, C1D3), r), CN1)),
                                                  Times(r, Power(ZZ(18L), CN1D3))),
                                              x),
                                          Times(Sqr(d), Sqr(x))),
                                      x),
                                  p)),
                          x),
                      x)),
              And(FreeQ(List(a, b, d, e, f, m, p), x),
                  NeQ(Plus(Times(C4, Power(b, C3)), Times(ZZ(27L), Sqr(a), d)), C0),
                  Not(IntegerQ(p))))));
}
