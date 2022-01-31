package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Cosh;
import static org.matheclipse.core.expression.F.Denominator;
import static org.matheclipse.core.expression.F.Exp;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Simplify;
import static org.matheclipse.core.expression.F.Sinh;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.p_;
import static org.matheclipse.core.expression.F.p_DEFAULT;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.d;
import static org.matheclipse.core.expression.S.e;
import static org.matheclipse.core.expression.S.k;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandTrigReduce;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FracPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FractionQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntegersQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Subst;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules266 {
  public static IAST RULES =
      List(
          IIntegrate(5321,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(Cosh(Plus(c_DEFAULT, Times(d_DEFAULT, Power(x_, n_)))),
                                  b_DEFAULT)),
                          p_DEFAULT),
                      Power(x_, m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(n, CN1),
                      Subst(
                          Integrate(
                              Times(
                                  Power(x,
                                      Subtract(Simplify(Times(Plus(m, C1), Power(n, CN1))), C1)),
                                  Power(Plus(a, Times(b, Cosh(Plus(c, Times(d, x))))), p)),
                              x),
                          x, Power(x, n)),
                      x),
                  And(FreeQ(List(a, b, c, d, m, n, p), x),
                      IntegerQ(Simplify(Times(Plus(m, C1), Power(n, CN1)))), Or(EqQ(p, C1),
                          EqQ(m, Subtract(n,
                              C1)),
                          And(IntegerQ(p), GtQ(Simplify(Times(Plus(m, C1),
                              Power(n, CN1))), C0)))))),
          IIntegrate(5322,
              Integrate(
                  Times(
                      Power(Times(e_,
                          x_), m_),
                      Power(
                          Plus(a_DEFAULT,
                              Times(
                                  b_DEFAULT, Sinh(Plus(c_DEFAULT, Times(d_DEFAULT,
                                      Power(x_, n_)))))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(e, IntPart(m)), Power(Times(e, x),
                              FracPart(m)),
                          Power(Power(x, FracPart(m)), CN1)),
                      Integrate(
                          Times(Power(x, m),
                              Power(Plus(a, Times(b, Sinh(Plus(c, Times(d, Power(x, n)))))), p)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, m, n,
                      p), x), IntegerQ(
                          Simplify(Times(Plus(m, C1), Power(n, CN1))))))),
          IIntegrate(5323,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(
                                  Cosh(Plus(c_DEFAULT, Times(d_DEFAULT,
                                      Power(x_, n_)))),
                                  b_DEFAULT)),
                          p_DEFAULT),
                      Power(Times(e_, x_), m_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(e, IntPart(m)), Power(Times(e,
                              x), FracPart(m)),
                          Power(Power(x, FracPart(m)), CN1)),
                      Integrate(
                          Times(
                              Power(x, m), Power(Plus(a,
                                  Times(b, Cosh(Plus(c, Times(d, Power(x, n)))))), p)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, m, n,
                      p), x), IntegerQ(
                          Simplify(Times(Plus(m, C1), Power(n, CN1))))))),
          IIntegrate(5324,
              Integrate(Times(Power(Times(e_DEFAULT, x_), m_DEFAULT),
                  Sinh(Plus(c_DEFAULT, Times(d_DEFAULT, Power(x_, n_))))), x_Symbol),
              Condition(
                  Subtract(Simp(
                      Times(Power(e, Subtract(n, C1)), Power(Times(e, x), Plus(m, Negate(n), C1)),
                          Cosh(Plus(c, Times(d, Power(x, n)))), Power(Times(d, n), CN1)),
                      x),
                      Dist(Times(Power(e, n), Plus(m, Negate(n), C1), Power(Times(d, n), CN1)),
                          Integrate(
                              Times(Power(Times(e, x), Subtract(m, n)),
                                  Cosh(Plus(c, Times(d, Power(x, n))))),
                              x),
                          x)),
                  And(FreeQ(List(c, d, e), x), IGtQ(n, C0), LtQ(C0, n, Plus(m, C1))))),
          IIntegrate(5325,
              Integrate(Times(Cosh(Plus(c_DEFAULT, Times(d_DEFAULT, Power(x_, n_)))),
                  Power(Times(e_DEFAULT, x_), m_DEFAULT)), x_Symbol),
              Condition(
                  Subtract(Simp(
                      Times(Power(e, Subtract(n, C1)), Power(Times(e, x), Plus(m, Negate(n), C1)),
                          Sinh(Plus(c, Times(d, Power(x, n)))), Power(Times(d, n), CN1)),
                      x),
                      Dist(Times(Power(e, n), Plus(m, Negate(n), C1), Power(Times(d, n), CN1)),
                          Integrate(
                              Times(Power(Times(e, x), Subtract(m, n)),
                                  Sinh(Plus(c, Times(d, Power(x, n))))),
                              x),
                          x)),
                  And(FreeQ(List(c, d, e), x), IGtQ(n, C0), LtQ(C0, n, Plus(m, C1))))),
          IIntegrate(5326,
              Integrate(
                  Times(
                      Power(Times(e_DEFAULT, x_), m_), Sinh(
                          Plus(c_DEFAULT, Times(d_DEFAULT, Power(x_, n_))))),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Power(Times(e, x), Plus(m, C1)),
                              Sinh(Plus(c, Times(d, Power(x, n)))), Power(Times(e, Plus(m, C1)),
                                  CN1)),
                          x),
                      Dist(
                          Times(d, n, Power(Times(Power(e, n), Plus(m, C1)),
                              CN1)),
                          Integrate(
                              Times(
                                  Power(Times(e, x), Plus(m, n)), Cosh(
                                      Plus(c, Times(d, Power(x, n))))),
                              x),
                          x)),
                  And(FreeQ(List(c, d, e), x), IGtQ(n, C0), LtQ(m, CN1)))),
          IIntegrate(5327,
              Integrate(
                  Times(
                      Cosh(Plus(c_DEFAULT,
                          Times(d_DEFAULT, Power(x_, n_)))),
                      Power(Times(e_DEFAULT, x_), m_)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Power(Times(e, x), Plus(m, C1)),
                              Cosh(Plus(c, Times(d, Power(x, n)))), Power(Times(e, Plus(m, C1)),
                                  CN1)),
                          x),
                      Dist(Times(d, n, Power(Times(Power(e, n), Plus(m, C1)), CN1)),
                          Integrate(
                              Times(
                                  Power(Times(e, x), Plus(m, n)), Sinh(
                                      Plus(c, Times(d, Power(x, n))))),
                              x),
                          x)),
                  And(FreeQ(List(c, d, e), x), IGtQ(n, C0), LtQ(m, CN1)))),
          IIntegrate(5328,
              Integrate(
                  Times(
                      Power(Times(e_DEFAULT, x_), m_DEFAULT), Sinh(
                          Plus(c_DEFAULT, Times(d_DEFAULT, Power(x_, n_))))),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(C1D2,
                          Integrate(Times(Power(Times(e, x), m),
                              Exp(Plus(c, Times(d, Power(x, n))))), x),
                          x),
                      Dist(C1D2,
                          Integrate(
                              Times(Power(Times(e, x), m),
                                  Exp(Subtract(Negate(c), Times(d, Power(x, n))))),
                              x),
                          x)),
                  And(FreeQ(List(c, d, e, m), x), IGtQ(n, C0)))),
          IIntegrate(5329,
              Integrate(
                  Times(
                      Cosh(Plus(c_DEFAULT,
                          Times(d_DEFAULT, Power(x_, n_)))),
                      Power(Times(e_DEFAULT, x_), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Dist(C1D2,
                          Integrate(Times(Power(Times(e, x), m),
                              Exp(Plus(c, Times(d, Power(x, n))))), x),
                          x),
                      Dist(C1D2,
                          Integrate(
                              Times(
                                  Power(Times(e, x), m), Exp(
                                      Subtract(Negate(c), Times(d, Power(x, n))))),
                              x),
                          x)),
                  And(FreeQ(List(c, d, e, m), x), IGtQ(n, C0)))),
          IIntegrate(5330,
              Integrate(
                  Times(
                      Power(x_, m_DEFAULT), Power(Sinh(
                          Plus(a_DEFAULT, Times(b_DEFAULT, Power(x_, n_)))),
                          p_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(
                                  Power(Sinh(Plus(a, Times(b, Power(x, n)))), p),
                                  Power(Times(Subtract(n, C1), Power(x, Subtract(n, C1))), CN1)),
                              x)),
                      Dist(
                          Times(b, n, p, Power(Subtract(n, C1),
                              CN1)),
                          Integrate(
                              Times(
                                  Power(Sinh(Plus(a, Times(b, Power(x, n)))), Subtract(p,
                                      C1)),
                                  Cosh(Plus(a, Times(b, Power(x, n))))),
                              x),
                          x)),
                  And(FreeQ(List(a, b), x), IntegersQ(n, p), EqQ(Plus(m,
                      n), C0), GtQ(p,
                          C1),
                      NeQ(n, C1)))),
          IIntegrate(5331,
              Integrate(
                  Times(
                      Power(Cosh(
                          Plus(a_DEFAULT, Times(b_DEFAULT, Power(x_, n_)))), p_),
                      Power(x_, m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(Power(Cosh(Plus(a, Times(b, Power(x, n)))), p),
                                  Power(Times(Subtract(n, C1), Power(x, Subtract(n, C1))), CN1)),
                              x)),
                      Dist(Times(b, n, p, Power(Subtract(n, C1), CN1)),
                          Integrate(
                              Times(Power(Cosh(Plus(a, Times(b, Power(x, n)))), Subtract(p, C1)),
                                  Sinh(Plus(a, Times(b, Power(x, n))))),
                              x),
                          x)),
                  And(FreeQ(List(a, b), x), IntegersQ(n, p), EqQ(Plus(m,
                      n), C0), GtQ(p,
                          C1),
                      NeQ(n, C1)))),
          IIntegrate(5332,
              Integrate(
                  Times(
                      Power(x_, m_DEFAULT),
                      Power(Sinh(Plus(a_DEFAULT, Times(b_DEFAULT, Power(x_, n_)))), p_)),
                  x_Symbol),
              Condition(Plus(
                  Negate(Simp(Times(n, Power(Sinh(Plus(a, Times(b, Power(x, n)))), p),
                      Power(Times(Sqr(b), Sqr(n), Sqr(p)), CN1)), x)),
                  Negate(Dist(Times(Subtract(p, C1), Power(p, CN1)),
                      Integrate(Times(Power(x, m),
                          Power(Sinh(Plus(a, Times(b, Power(x, n)))), Subtract(p, C2))), x),
                      x)),
                  Simp(
                      Times(Power(x, n), Cosh(Plus(a, Times(b, Power(x, n)))),
                          Power(Sinh(Plus(a, Times(b, Power(x, n)))), Subtract(p, C1)),
                          Power(Times(b, n, p), CN1)),
                      x)),
                  And(FreeQ(List(a, b, m, n), x), EqQ(Plus(m, Times(CN1, C2, n),
                      C1), C0), GtQ(p,
                          C1)))),
          IIntegrate(5333,
              Integrate(
                  Times(
                      Power(Cosh(
                          Plus(a_DEFAULT, Times(b_DEFAULT, Power(x_, n_)))), p_),
                      Power(x_, m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(
                                  n, Power(Cosh(
                                      Plus(a, Times(b, Power(x, n)))), p),
                                  Power(Times(Sqr(b), Sqr(n), Sqr(p)), CN1)),
                              x)),
                      Dist(Times(Subtract(p, C1), Power(p, CN1)),
                          Integrate(
                              Times(
                                  Power(x, m), Power(Cosh(Plus(a, Times(b, Power(x, n)))),
                                      Subtract(p, C2))),
                              x),
                          x),
                      Simp(
                          Times(Power(x, n), Sinh(Plus(a, Times(b, Power(x, n)))),
                              Power(Cosh(Plus(a, Times(b, Power(x, n)))), Subtract(p,
                                  C1)),
                              Power(Times(b, n, p), CN1)),
                          x)),
                  And(FreeQ(List(a, b, m, n), x), EqQ(Plus(m, Times(CN1, C2, n),
                      C1), C0), GtQ(p,
                          C1)))),
          IIntegrate(5334,
              Integrate(
                  Times(
                      Power(x_, m_DEFAULT), Power(Sinh(
                          Plus(a_DEFAULT, Times(b_DEFAULT, Power(x_, n_)))),
                          p_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(Plus(m, Negate(n), C1),
                                  Power(x, Plus(m, Times(CN1, C2, n), C1)),
                                  Power(Sinh(Plus(a, Times(b, Power(x, n)))),
                                      p),
                                  Power(Times(Sqr(b), Sqr(n), Sqr(p)), CN1)),
                              x)),
                      Negate(
                          Dist(
                              Times(Subtract(p, C1), Power(p,
                                  CN1)),
                              Integrate(
                                  Times(Power(x, m), Power(Sinh(Plus(a, Times(b, Power(x, n)))),
                                      Subtract(p, C2))),
                                  x),
                              x)),
                      Dist(
                          Times(
                              Plus(m, Negate(n), C1), Plus(m, Times(CN1, C2, n),
                                  C1),
                              Power(Times(Sqr(b), Sqr(n), Sqr(p)), CN1)),
                          Integrate(
                              Times(Power(x, Subtract(m, Times(C2, n))),
                                  Power(Sinh(Plus(a, Times(b, Power(x, n)))), p)),
                              x),
                          x),
                      Simp(
                          Times(Power(x, Plus(m, Negate(n), C1)),
                              Cosh(Plus(a, Times(b, Power(x, n)))),
                              Power(Sinh(Plus(a, Times(b, Power(x, n)))), Subtract(p, C1)),
                              Power(Times(b, n, p), CN1)),
                          x)),
                  And(FreeQ(List(a,
                      b), x), IntegersQ(m,
                          n),
                      GtQ(p, C1), LtQ(C0, Times(C2, n), Plus(m, C1))))),
          IIntegrate(5335,
              Integrate(
                  Times(
                      Power(Cosh(Plus(a_DEFAULT, Times(b_DEFAULT, Power(x_, n_)))), p_), Power(x_,
                          m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(Plus(m, Negate(n), C1),
                                  Power(x, Plus(m, Times(CN1, C2, n), C1)),
                                  Power(Cosh(Plus(a, Times(b, Power(x, n)))), p),
                                  Power(Times(Sqr(b), Sqr(n), Sqr(p)), CN1)),
                              x)),
                      Dist(Times(Subtract(p, C1), Power(p, CN1)),
                          Integrate(Times(Power(x, m),
                              Power(Cosh(Plus(a, Times(b, Power(x, n)))), Subtract(p, C2))), x),
                          x),
                      Dist(
                          Times(
                              Plus(m, Negate(n), C1), Plus(m, Times(CN1, C2, n), C1),
                              Power(Times(Sqr(b), Sqr(n), Sqr(p)), CN1)),
                          Integrate(Times(Power(x, Subtract(m, Times(C2, n))),
                              Power(Cosh(Plus(a, Times(b, Power(x, n)))), p)), x),
                          x),
                      Simp(Times(Power(x, Plus(m, Negate(n), C1)),
                          Sinh(Plus(a, Times(b, Power(x, n)))),
                          Power(Cosh(Plus(a, Times(b, Power(x, n)))), Subtract(p, C1)),
                          Power(Times(b, n, p), CN1)), x)),
                  And(FreeQ(List(a,
                      b), x), IntegersQ(m, n), GtQ(p, C1), LtQ(C0, Times(C2, n),
                          Plus(m, C1))))),
          IIntegrate(5336,
              Integrate(
                  Times(Power(x_, m_DEFAULT), Power(Sinh(
                      Plus(a_DEFAULT, Times(b_DEFAULT, Power(x_, n_)))),
                      p_)),
                  x_Symbol),
              Condition(
                  Plus(Simp(Times(Power(x, Plus(m, C1)), Power(Sinh(Plus(a, Times(b, Power(x, n)))),
                      p), Power(Plus(m, C1), CN1)), x), Dist(Times(
                          Sqr(b), Sqr(n), p, Subtract(p,
                              C1),
                          Power(Times(Plus(m, C1), Plus(m, n, C1)), CN1)),
                          Integrate(Times(Power(x, Plus(m, Times(C2, n))),
                              Power(Sinh(Plus(a, Times(b, Power(x, n)))), Subtract(p, C2))), x),
                          x),
                      Dist(
                          Times(Sqr(b), Sqr(n), Sqr(p),
                              Power(Times(Plus(m, C1), Plus(m, n, C1)), CN1)),
                          Integrate(Times(Power(x, Plus(m, Times(C2, n))),
                              Power(Sinh(Plus(a, Times(b, Power(x, n)))), p)), x),
                          x),
                      Negate(Simp(Times(b, n, p, Power(x, Plus(m, n, C1)),
                          Cosh(Plus(a, Times(b, Power(x, n)))),
                          Power(Sinh(Plus(a, Times(b, Power(x, n)))), Subtract(p, C1)),
                          Power(Times(Plus(m, C1), Plus(m, n, C1)), CN1)), x))),
                  And(FreeQ(List(a, b), x), IntegersQ(m, n), GtQ(p, C1), LtQ(C0, Times(C2,
                      n), Subtract(C1, m)), NeQ(Plus(m, n, C1),
                          C0)))),
          IIntegrate(5337,
              Integrate(
                  Times(
                      Power(Cosh(
                          Plus(a_DEFAULT, Times(b_DEFAULT, Power(x_, n_)))), p_),
                      Power(x_, m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(x, Plus(m, C1)),
                              Power(Cosh(Plus(a, Times(b, Power(x, n)))), p), Power(Plus(m, C1),
                                  CN1)),
                          x),
                      Negate(
                          Dist(
                              Times(
                                  Sqr(b), Sqr(n), p, Subtract(p, C1), Power(
                                      Times(Plus(m, C1), Plus(m, n, C1)), CN1)),
                              Integrate(
                                  Times(
                                      Power(x, Plus(m,
                                          Times(C2, n))),
                                      Power(Cosh(Plus(a, Times(b, Power(x, n)))), Subtract(p, C2))),
                                  x),
                              x)),
                      Dist(
                          Times(Sqr(b), Sqr(n), Sqr(p),
                              Power(Times(Plus(m, C1), Plus(m, n, C1)), CN1)),
                          Integrate(
                              Times(Power(x, Plus(m, Times(C2, n))),
                                  Power(Cosh(Plus(a, Times(b, Power(x, n)))), p)),
                              x),
                          x),
                      Negate(
                          Simp(
                              Times(b, n, p, Power(x, Plus(m, n, C1)),
                                  Sinh(Plus(a, Times(b, Power(x, n)))),
                                  Power(Cosh(Plus(a, Times(b, Power(x, n)))), Subtract(p,
                                      C1)),
                                  Power(Times(Plus(m, C1), Plus(m, n, C1)), CN1)),
                              x))),
                  And(FreeQ(List(a, b), x), IntegersQ(m, n), GtQ(p, C1), LtQ(C0, Times(C2, n),
                      Subtract(C1, m)), NeQ(Plus(m, n, C1), C0)))),
          IIntegrate(5338,
              Integrate(
                  Times(Power(Times(e_DEFAULT, x_), m_),
                      Power(
                          Plus(a_DEFAULT,
                              Times(
                                  b_DEFAULT,
                                  Sinh(Plus(c_DEFAULT, Times(d_DEFAULT, Power(x_, n_)))))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(k,
                          Denominator(m))),
                      Dist(Times(k, Power(e, CN1)),
                          Subst(
                              Integrate(
                                  Times(Power(x, Subtract(Times(k, Plus(m, C1)), C1)),
                                      Power(
                                          Plus(a,
                                              Times(b,
                                                  Sinh(Plus(c,
                                                      Times(d, Power(x, Times(k, n)),
                                                          Power(Power(e, n), CN1)))))),
                                          p)),
                                  x),
                              x, Power(Times(e, x), Power(k, CN1))),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), IntegerQ(p), IGtQ(n, C0), FractionQ(m)))),
          IIntegrate(5339,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(
                                  Cosh(Plus(c_DEFAULT, Times(d_DEFAULT,
                                      Power(x_, n_)))),
                                  b_DEFAULT)),
                          p_DEFAULT),
                      Power(Times(e_DEFAULT, x_), m_)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(k,
                          Denominator(m))),
                      Dist(Times(k, Power(e, CN1)),
                          Subst(
                              Integrate(
                                  Times(Power(x, Subtract(Times(k, Plus(m, C1)), C1)),
                                      Power(
                                          Plus(a,
                                              Times(b,
                                                  Cosh(Plus(c,
                                                      Times(d, Power(x, Times(k, n)),
                                                          Power(Power(e, n), CN1)))))),
                                          p)),
                                  x),
                              x, Power(Times(e, x), Power(k, CN1))),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), IntegerQ(p), IGtQ(n, C0), FractionQ(m)))),
          IIntegrate(5340,
              Integrate(Times(Power(Times(e_DEFAULT, x_), m_DEFAULT),
                  Power(
                      Plus(a_DEFAULT,
                          Times(b_DEFAULT, Sinh(Plus(c_DEFAULT, Times(d_DEFAULT, Power(x_, n_)))))),
                      p_)),
                  x_Symbol),
              Condition(
                  Integrate(ExpandTrigReduce(Power(Times(e, x), m),
                      Power(Plus(a, Times(b, Sinh(Plus(c, Times(d, Power(x, n)))))), p), x), x),
                  And(FreeQ(List(a, b, c, d, e, m), x), IGtQ(p, C1), IGtQ(n, C0)))));
}
