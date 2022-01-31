package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Cosh;
import static org.matheclipse.core.expression.F.Csch;
import static org.matheclipse.core.expression.F.Exp;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Sech;
import static org.matheclipse.core.expression.F.Sinh;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.f_DEFAULT;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_DEFAULT;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.d;
import static org.matheclipse.core.expression.S.e;
import static org.matheclipse.core.expression.S.f;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Subst;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules277 {
  public static IAST RULES =
      List(
          IIntegrate(5541,
              Integrate(
                  Power(
                      Sech(Times(Plus(a_DEFAULT, Times(Log(x_), b_DEFAULT)),
                          d_DEFAULT)),
                      p_DEFAULT),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(C2, p), Power(Exp(Times(a, d, p)),
                          CN1)),
                      Integrate(
                          Power(
                              Times(
                                  Power(x, Times(b, d, p)), Power(
                                      Plus(C1,
                                          Power(
                                              Times(Exp(Times(C2, a, d)),
                                                  Power(x, Times(C2, b, d))),
                                              CN1)),
                                      p)),
                              CN1),
                          x),
                      x),
                  And(FreeQ(List(a, b, d), x), IntegerQ(p)))),
          IIntegrate(5542,
              Integrate(
                  Power(
                      Csch(Times(Plus(a_DEFAULT, Times(Log(x_), b_DEFAULT)),
                          d_DEFAULT)),
                      p_DEFAULT),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(C2, p), Power(Exp(Times(a, d, p)),
                          CN1)),
                      Integrate(
                          Power(
                              Times(
                                  Power(x, Times(b, d,
                                      p)),
                                  Power(Subtract(C1,
                                      Power(Times(Exp(Times(C2, a, d)), Power(x, Times(C2, b, d))),
                                          CN1)),
                                      p)),
                              CN1),
                          x),
                      x),
                  And(FreeQ(List(a, b, d), x), IntegerQ(p)))),
          IIntegrate(5543,
              Integrate(
                  Power(Sech(Times(Plus(a_DEFAULT, Times(Log(x_), b_DEFAULT)), d_DEFAULT)),
                      p_DEFAULT),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(Sech(
                              Times(d, Plus(a, Times(b, Log(x))))), p),
                          Power(
                              Plus(C1,
                                  Power(Times(Exp(Times(C2, a,
                                      d)), Power(x,
                                          Times(C2, b, d))),
                                      CN1)),
                              p),
                          Power(Power(x, Times(CN1, b, d, p)), CN1)),
                      Integrate(
                          Power(
                              Times(
                                  Power(x, Times(b, d,
                                      p)),
                                  Power(
                                      Plus(C1,
                                          Power(
                                              Times(Exp(Times(C2, a, d)), Power(x,
                                                  Times(C2, b, d))),
                                              CN1)),
                                      p)),
                              CN1),
                          x),
                      x),
                  And(FreeQ(List(a, b, d, p), x), Not(IntegerQ(p))))),
          IIntegrate(5544,
              Integrate(
                  Power(
                      Csch(Times(Plus(a_DEFAULT, Times(Log(x_), b_DEFAULT)),
                          d_DEFAULT)),
                      p_DEFAULT),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Csch(Times(d, Plus(a, Times(b, Log(x))))), p),
                          Power(Subtract(
                              C1, Power(Times(Exp(Times(C2, a, d)), Power(x, Times(C2, b, d))),
                                  CN1)),
                              p),
                          Power(Power(x, Times(CN1, b, d, p)), CN1)),
                      Integrate(
                          Power(
                              Times(Power(x, Times(b, d, p)),
                                  Power(Subtract(C1,
                                      Power(Times(Exp(Times(C2, a, d)), Power(x, Times(C2, b, d))),
                                          CN1)),
                                      p)),
                              CN1),
                          x),
                      x),
                  And(FreeQ(List(a, b, d, p), x), Not(IntegerQ(p))))),
          IIntegrate(5545,
              Integrate(
                  Power(Sech(Times(
                      Plus(a_DEFAULT,
                          Times(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))), b_DEFAULT)),
                      d_DEFAULT)), p_DEFAULT),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(x, Power(Times(n, Power(Times(c, Power(x, n)), Power(n, CN1))),
                          CN1)),
                      Subst(
                          Integrate(Times(Power(x, Subtract(Power(n, CN1), C1)),
                              Power(Sech(Times(d, Plus(a, Times(b, Log(x))))), p)), x),
                          x, Times(c, Power(x, n))),
                      x),
                  And(FreeQ(List(a, b, c, d, n, p), x), Or(NeQ(c, C1), NeQ(n, C1))))),
          IIntegrate(5546,
              Integrate(
                  Power(
                      Csch(
                          Times(
                              Plus(
                                  a_DEFAULT, Times(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))),
                                      b_DEFAULT)),
                              d_DEFAULT)),
                      p_DEFAULT),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(x, Power(Times(n, Power(Times(c, Power(x, n)), Power(n, CN1))),
                          CN1)),
                      Subst(
                          Integrate(
                              Times(
                                  Power(x, Subtract(Power(n, CN1),
                                      C1)),
                                  Power(Csch(Times(d, Plus(a, Times(b, Log(x))))), p)),
                              x),
                          x, Times(c, Power(x, n))),
                      x),
                  And(FreeQ(List(a, b, c, d, n, p), x), Or(NeQ(c, C1), NeQ(n, C1))))),
          IIntegrate(5547,
              Integrate(
                  Times(
                      Power(Times(e_DEFAULT,
                          x_), m_DEFAULT),
                      Power(
                          Sech(Times(Plus(a_DEFAULT, Times(Log(x_), b_DEFAULT)),
                              d_DEFAULT)),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(C2, p), Power(Exp(Times(a, d, p)),
                          CN1)),
                      Integrate(
                          Times(
                              Power(Times(e,
                                  x), m),
                              Power(
                                  Times(
                                      Power(x, Times(b, d,
                                          p)),
                                      Power(
                                          Plus(C1,
                                              Power(
                                                  Times(Exp(Times(C2, a, d)),
                                                      Power(x, Times(C2, b, d))),
                                                  CN1)),
                                          p)),
                                  CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, b, d, e, m), x), IntegerQ(p)))),
          IIntegrate(5548,
              Integrate(
                  Times(
                      Power(
                          Csch(Times(Plus(a_DEFAULT, Times(Log(x_), b_DEFAULT)),
                              d_DEFAULT)),
                          p_DEFAULT),
                      Power(Times(e_DEFAULT, x_), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(C2, p), Power(Exp(Times(a, d, p)),
                          CN1)),
                      Integrate(
                          Times(
                              Power(Times(e,
                                  x), m),
                              Power(
                                  Times(Power(x, Times(b, d, p)),
                                      Power(Subtract(C1,
                                          Power(
                                              Times(Exp(Times(C2, a, d)),
                                                  Power(x, Times(C2, b, d))),
                                              CN1)),
                                          p)),
                                  CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, b, d, e, m), x), IntegerQ(p)))),
          IIntegrate(5549,
              Integrate(
                  Times(Power(Times(e_DEFAULT, x_), m_DEFAULT),
                      Power(Sech(Times(Plus(a_DEFAULT, Times(Log(x_), b_DEFAULT)), d_DEFAULT)),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Sech(Times(d, Plus(a, Times(b, Log(x))))), p),
                          Power(
                              Plus(C1,
                                  Power(Times(Exp(Times(C2, a, d)), Power(x, Times(C2, b, d))),
                                      CN1)),
                              p),
                          Power(Power(x, Times(CN1, b, d, p)), CN1)),
                      Integrate(
                          Times(
                              Power(Times(e,
                                  x), m),
                              Power(
                                  Times(Power(x, Times(b, d, p)),
                                      Power(
                                          Plus(C1,
                                              Power(
                                                  Times(Exp(Times(C2, a, d)),
                                                      Power(x, Times(C2, b, d))),
                                                  CN1)),
                                          p)),
                                  CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, b, d, e, m, p), x), Not(IntegerQ(p))))),
          IIntegrate(5550,
              Integrate(
                  Times(
                      Power(
                          Csch(Times(Plus(a_DEFAULT, Times(Log(x_), b_DEFAULT)),
                              d_DEFAULT)),
                          p_DEFAULT),
                      Power(Times(e_DEFAULT, x_), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(Power(Csch(Times(d, Plus(a, Times(b, Log(x))))), p),
                          Power(
                              Subtract(C1,
                                  Power(Times(Exp(Times(C2, a, d)), Power(x, Times(C2, b, d))),
                                      CN1)),
                              p),
                          Power(Power(x, Times(CN1, b, d, p)), CN1)),
                      Integrate(
                          Times(
                              Power(Times(e,
                                  x), m),
                              Power(Times(Power(x, Times(b, d, p)),
                                  Power(Subtract(C1,
                                      Power(Times(Exp(Times(C2, a, d)), Power(x, Times(C2, b, d))),
                                          CN1)),
                                      p)),
                                  CN1)),
                          x),
                      x),
                  And(FreeQ(List(a, b, d, e, m, p), x), Not(IntegerQ(p))))),
          IIntegrate(5551,
              Integrate(
                  Times(
                      Power(Times(e_DEFAULT,
                          x_), m_DEFAULT),
                      Power(
                          Sech(Times(
                              Plus(a_DEFAULT,
                                  Times(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))), b_DEFAULT)),
                              d_DEFAULT)),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Times(Power(Times(e, x), Plus(m, C1)),
                      Power(
                          Times(e, n,
                              Power(Times(c, Power(x, n)), Times(Plus(m, C1), Power(n, CN1)))),
                          CN1)),
                      Subst(
                          Integrate(
                              Times(Power(x, Subtract(Times(Plus(m, C1), Power(n, CN1)), C1)),
                                  Power(Sech(Times(d, Plus(a, Times(b, Log(x))))), p)),
                              x),
                          x, Times(c, Power(x, n))),
                      x),
                  And(FreeQ(List(a, b, c, d, e, m, n, p), x), Or(NeQ(c, C1), NeQ(n, C1))))),
          IIntegrate(5552,
              Integrate(
                  Times(
                      Power(
                          Csch(
                              Times(
                                  Plus(a_DEFAULT,
                                      Times(Log(Times(c_DEFAULT, Power(x_, n_DEFAULT))),
                                          b_DEFAULT)),
                                  d_DEFAULT)),
                          p_DEFAULT),
                      Power(Times(e_DEFAULT, x_), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(Times(e, x), Plus(m,
                              C1)),
                          Power(
                              Times(
                                  e, n, Power(Times(c, Power(x, n)),
                                      Times(Plus(m, C1), Power(n, CN1)))),
                              CN1)),
                      Subst(
                          Integrate(
                              Times(Power(x, Subtract(Times(Plus(m, C1), Power(n, CN1)), C1)),
                                  Power(Csch(Times(d, Plus(a, Times(b, Log(x))))), p)),
                              x),
                          x, Times(c, Power(x, n))),
                      x),
                  And(FreeQ(List(a, b, c, d, e, m, n, p), x), Or(NeQ(c, C1), NeQ(n, C1))))),
          IIntegrate(5553,
              Integrate(
                  Times(Log(Times(b_DEFAULT, x_)),
                      Sinh(Times(Log(Times(b_DEFAULT, x_)), a_DEFAULT, x_))),
                  x_Symbol),
              Condition(
                  Subtract(Simp(Times(Cosh(Times(a, x, Log(Times(b, x)))), Power(a, CN1)),
                      x), Integrate(Sinh(Times(a, x, Log(Times(b, x)))), x)),
                  FreeQ(List(a, b), x))),
          IIntegrate(5554,
              Integrate(
                  Times(
                      Cosh(Times(Log(Times(b_DEFAULT, x_)), a_DEFAULT,
                          x_)),
                      Log(Times(b_DEFAULT, x_))),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(Times(Sinh(Times(a, x, Log(Times(b, x)))),
                          Power(a, CN1)), x),
                      Integrate(Cosh(Times(a, x, Log(Times(b, x)))), x)),
                  FreeQ(List(a, b), x))),
          IIntegrate(5555,
              Integrate(
                  Times(
                      Log(Times(b_DEFAULT, x_)), Power(x_, m_DEFAULT), Sinh(Times(Log(
                          Times(b_DEFAULT, x_)), a_DEFAULT,
                          Power(x_, n_DEFAULT)))),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(
                              Cosh(Times(a, Power(x, n),
                                  Log(Times(b, x)))),
                              Power(Times(a, n), CN1)),
                          x),
                      Dist(Power(n, CN1),
                          Integrate(Times(Power(x, m), Sinh(
                              Times(a, Power(x, n), Log(Times(b, x))))), x),
                          x)),
                  And(FreeQ(List(a, b, m, n), x), EqQ(m, Subtract(n, C1))))),
          IIntegrate(5556,
              Integrate(
                  Times(
                      Cosh(Times(Log(Times(b_DEFAULT, x_)), a_DEFAULT,
                          Power(x_, n_DEFAULT))),
                      Log(Times(b_DEFAULT, x_)), Power(x_, m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(
                              Sinh(Times(a, Power(x, n),
                                  Log(Times(b, x)))),
                              Power(Times(a, n), CN1)),
                          x),
                      Dist(Power(n, CN1),
                          Integrate(
                              Times(Power(x, m), Cosh(Times(a, Power(x, n), Log(Times(b, x))))), x),
                          x)),
                  And(FreeQ(List(a, b, m, n), x), EqQ(m, Subtract(n, C1))))),
          IIntegrate(5557,
              Integrate(
                  Times(
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT,
                          x_)), m_DEFAULT),
                      Power(Sinh(
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_))), n_DEFAULT),
                      Power(
                          Plus(a_, Times(b_DEFAULT, Sinh(
                              Plus(c_DEFAULT, Times(d_DEFAULT, x_))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Subtract(Dist(Power(b, CN1),
                      Integrate(
                          Times(Power(Plus(e, Times(f, x)), m),
                              Power(Sinh(Plus(c, Times(d, x))), Subtract(n, C1))),
                          x),
                      x),
                      Dist(Times(a, Power(b, CN1)),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), m),
                                  Power(Sinh(Plus(c, Times(d, x))), Subtract(n, C1)), Power(Plus(a,
                                      Times(b, Sinh(Plus(c, Times(d, x))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), IGtQ(m, C0), IGtQ(n, C0)))),
          IIntegrate(5558,
              Integrate(
                  Times(
                      Power(Cosh(
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_))), n_DEFAULT),
                      Power(
                          Plus(Times(Cosh(Plus(c_DEFAULT, Times(d_DEFAULT, x_))), b_DEFAULT),
                              a_),
                          CN1),
                      Power(Plus(e_DEFAULT, Times(f_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Power(b, CN1),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), m),
                                  Power(Cosh(Plus(c, Times(d, x))), Subtract(n, C1))),
                              x),
                          x),
                      Dist(
                          Times(a, Power(b,
                              CN1)),
                          Integrate(
                              Times(Power(Plus(e, Times(f, x)), m),
                                  Power(Cosh(Plus(c,
                                      Times(d, x))), Subtract(n,
                                          C1)),
                                  Power(Plus(a, Times(b, Cosh(Plus(c, Times(d, x))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), IGtQ(m, C0), IGtQ(n, C0)))),
          IIntegrate(5559,
              Integrate(
                  Times(
                      Cosh(Plus(c_DEFAULT, Times(d_DEFAULT,
                          x_))),
                      Power(Plus(e_DEFAULT,
                          Times(f_DEFAULT, x_)), m_DEFAULT),
                      Power(
                          Plus(a_, Times(b_DEFAULT, Sinh(
                              Plus(c_DEFAULT, Times(d_DEFAULT, x_))))),
                          CN1)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(Power(Plus(e, Times(f, x)), Plus(m, C1)),
                                  Power(Times(b, f, Plus(m, C1)), CN1)),
                              x)),
                      Dist(C2,
                          Integrate(
                              Times(
                                  Power(Plus(e, Times(f, x)), m), Exp(Plus(c, Times(d,
                                      x))),
                                  Power(Plus(a, Times(b, Exp(Plus(c, Times(d, x))))), CN1)),
                              x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), IGtQ(m, C0), EqQ(Plus(Sqr(a), Sqr(b)),
                      C0)))),
          IIntegrate(5560,
              Integrate(
                  Times(
                      Power(
                          Plus(Times(Cosh(Plus(c_DEFAULT, Times(d_DEFAULT, x_))),
                              b_DEFAULT), a_),
                          CN1),
                      Power(Plus(e_DEFAULT,
                          Times(f_DEFAULT, x_)), m_DEFAULT),
                      Sinh(Plus(c_DEFAULT, Times(d_DEFAULT, x_)))),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(Simp(Times(Power(Plus(e, Times(f, x)), Plus(m, C1)),
                          Power(Times(b, f, Plus(m, C1)), CN1)), x)),
                      Dist(C2,
                          Integrate(Times(Power(Plus(e, Times(f, x)), m), Exp(Plus(c, Times(d, x))),
                              Power(Plus(a, Times(b, Exp(Plus(c, Times(d, x))))), CN1)), x),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, f), x), IGtQ(m, C0),
                      EqQ(Subtract(Sqr(a), Sqr(b)), C0)))));
}
