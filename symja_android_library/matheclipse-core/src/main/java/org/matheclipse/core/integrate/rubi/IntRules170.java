package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN2;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Cos;
import static org.matheclipse.core.expression.F.Denominator;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Module;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Simplify;
import static org.matheclipse.core.expression.F.Sin;
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
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ILtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RationalQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Subst;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules170 {
  public static IAST RULES =
      List(
          IIntegrate(3401,
              Integrate(
                  Times(
                      Power(Times(e_DEFAULT,
                          x_), m_),
                      Power(
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT,
                                  Sin(Plus(c_DEFAULT, Times(d_DEFAULT, Power(x_, n_)))))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(k,
                          Denominator(m))),
                      Dist(
                          Times(k, Power(e,
                              CN1)),
                          Subst(
                              Integrate(
                                  Times(Power(x, Subtract(Times(k, Plus(m, C1)), C1)),
                                      Power(
                                          Plus(a,
                                              Times(b,
                                                  Sin(Plus(c,
                                                      Times(d, Power(x, Times(k, n)),
                                                          Power(Power(e, n), CN1)))))),
                                          p)),
                                  x),
                              x, Power(Times(e, x), Power(k, CN1))),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), IntegerQ(p), IGtQ(n, C0), FractionQ(m)))),
          IIntegrate(3402,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(
                                  Cos(Plus(c_DEFAULT, Times(d_DEFAULT,
                                      Power(x_, n_)))),
                                  b_DEFAULT)),
                          p_DEFAULT),
                      Power(Times(e_DEFAULT, x_), m_)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(k,
                          Denominator(m))),
                      Dist(
                          Times(k, Power(
                              e, CN1)),
                          Subst(Integrate(Times(Power(x, Subtract(Times(k, Plus(m, C1)), C1)),
                              Power(Plus(a, Times(b,
                                  Cos(Plus(c,
                                      Times(d, Power(x, Times(k, n)), Power(Power(e, n), CN1)))))),
                                  p)),
                              x), x, Power(Times(e, x), Power(k, CN1))),
                          x)),
                  And(FreeQ(List(a, b, c, d, e), x), IntegerQ(p), IGtQ(n, C0), FractionQ(m)))),
          IIntegrate(3403,
              Integrate(
                  Times(Power(Times(e_DEFAULT, x_), m_DEFAULT),
                      Power(Plus(a_DEFAULT,
                          Times(b_DEFAULT, Sin(Plus(c_DEFAULT, Times(d_DEFAULT, Power(x_, n_)))))),
                          p_)),
                  x_Symbol),
              Condition(
                  Integrate(ExpandTrigReduce(Power(Times(e, x), m),
                      Power(Plus(a, Times(b, Sin(Plus(c, Times(d, Power(x, n)))))), p), x), x),
                  And(FreeQ(List(a, b, c, d, e, m), x), IGtQ(p, C1), IGtQ(n, C0)))),
          IIntegrate(3404,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(Cos(Plus(c_DEFAULT, Times(d_DEFAULT, Power(x_, n_)))),
                                  b_DEFAULT)),
                          p_),
                      Power(Times(e_DEFAULT, x_), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandTrigReduce(
                          Power(Times(e, x),
                              m),
                          Power(Plus(a, Times(b, Cos(Plus(c, Times(d, Power(x, n)))))), p), x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, m), x), IGtQ(p, C1), IGtQ(n, C0)))),
          IIntegrate(3405,
              Integrate(
                  Times(
                      Power(x_, m_DEFAULT),
                      Power(Sin(Plus(a_DEFAULT, Times(b_DEFAULT, Power(x_, n_)))), p_)),
                  x_Symbol),
              Condition(
                  Plus(
                      Simp(
                          Times(Power(x, n), Cos(Plus(a, Times(b, Power(x, n)))),
                              Power(Sin(Plus(a, Times(b, Power(x, n)))), Plus(p,
                                  C1)),
                              Power(Times(b, n, Plus(p, C1)), CN1)),
                          x),
                      Dist(Times(Plus(p, C2), Power(Plus(p, C1), CN1)), Integrate(Times(Power(x, m),
                          Power(Sin(Plus(a, Times(b, Power(x, n)))), Plus(p, C2))), x), x),
                      Negate(
                          Simp(
                              Times(n, Power(Sin(Plus(a, Times(b, Power(x, n)))), Plus(p, C2)),
                                  Power(Times(Sqr(b), Sqr(n), Plus(p, C1), Plus(p, C2)), CN1)),
                              x))),
                  And(FreeQ(List(a, b, m, n), x), EqQ(Plus(m, Times(CN1, C2, n),
                      C1), C0), LtQ(p,
                          CN1),
                      NeQ(p, CN2)))),
          IIntegrate(3406,
              Integrate(
                  Times(
                      Power(Cos(
                          Plus(a_DEFAULT, Times(b_DEFAULT, Power(x_, n_)))), p_),
                      Power(x_, m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(Power(x, n), Sin(Plus(a, Times(b, Power(x, n)))),
                                  Power(Cos(Plus(a, Times(b, Power(x, n)))), Plus(p, C1)), Power(
                                      Times(b, n, Plus(p, C1)), CN1)),
                              x)),
                      Dist(Times(Plus(p, C2), Power(Plus(p, C1), CN1)),
                          Integrate(
                              Times(
                                  Power(x, m), Power(Cos(Plus(a, Times(b, Power(x, n)))),
                                      Plus(p, C2))),
                              x),
                          x),
                      Negate(
                          Simp(
                              Times(n, Power(Cos(Plus(a, Times(b, Power(x, n)))), Plus(p, C2)),
                                  Power(Times(Sqr(b), Sqr(n), Plus(p, C1), Plus(p, C2)), CN1)),
                              x))),
                  And(FreeQ(List(a, b, m, n), x), EqQ(Plus(m, Times(CN1, C2, n), C1), C0),
                      LtQ(p, CN1), NeQ(p, CN2)))),
          IIntegrate(3407,
              Integrate(Times(
                  Power(x_, m_DEFAULT),
                  Power(Sin(Plus(a_DEFAULT, Times(b_DEFAULT, Power(x_, n_)))), p_)), x_Symbol),
              Condition(
                  Plus(Simp(
                      Times(Power(x, Plus(m, Negate(n), C1)), Cos(Plus(a, Times(b, Power(x, n)))),
                          Power(Sin(Plus(a, Times(b, Power(x, n)))),
                              Plus(p, C1)),
                          Power(Times(b, n, Plus(p, C1)), CN1)),
                      x),
                      Dist(Times(Plus(p, C2), Power(Plus(p, C1), CN1)),
                          Integrate(
                              Times(
                                  Power(x, m), Power(Sin(Plus(a, Times(b, Power(x, n)))),
                                      Plus(p, C2))),
                              x),
                          x),
                      Dist(
                          Times(Plus(m, Negate(n), C1), Plus(m, Times(CN1, C2, n), C1),
                              Power(Times(Sqr(b), Sqr(n), Plus(p, C1), Plus(p, C2)), CN1)),
                          Integrate(
                              Times(Power(x, Subtract(m, Times(C2, n))),
                                  Power(Sin(Plus(a, Times(b, Power(x, n)))), Plus(p, C2))),
                              x),
                          x),
                      Negate(Simp(
                          Times(Plus(m, Negate(n), C1), Power(x, Plus(m, Times(CN1, C2, n), C1)),
                              Power(Sin(Plus(a, Times(b, Power(x, n)))), Plus(p,
                                  C2)),
                              Power(Times(Sqr(b), Sqr(n), Plus(p, C1), Plus(p, C2)), CN1)),
                          x))),
                  And(FreeQ(List(a, b), x), LtQ(p, CN1), NeQ(p, CN2), IGtQ(n, C0), IGtQ(m, Subtract(
                      Times(C2, n), C1))))),
          IIntegrate(3408,
              Integrate(
                  Times(
                      Power(Cos(Plus(a_DEFAULT, Times(b_DEFAULT, Power(x_, n_)))), p_),
                      Power(x_, m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Plus(
                      Negate(
                          Simp(
                              Times(Power(x, Plus(m, Negate(n), C1)),
                                  Sin(Plus(a, Times(b, Power(x, n)))),
                                  Power(Cos(Plus(a, Times(b, Power(x, n)))), Plus(p, C1)), Power(
                                      Times(b, n, Plus(p, C1)), CN1)),
                              x)),
                      Dist(
                          Times(Plus(p, C2), Power(Plus(p, C1),
                              CN1)),
                          Integrate(
                              Times(
                                  Power(x, m), Power(Cos(Plus(a, Times(b, Power(x, n)))),
                                      Plus(p, C2))),
                              x),
                          x),
                      Dist(
                          Times(
                              Plus(m, Negate(n), C1), Plus(m, Times(CN1, C2,
                                  n), C1),
                              Power(Times(Sqr(b), Sqr(n), Plus(p, C1), Plus(p, C2)), CN1)),
                          Integrate(
                              Times(Power(x, Subtract(m, Times(C2, n))),
                                  Power(Cos(Plus(a, Times(b, Power(x, n)))), Plus(p, C2))),
                              x),
                          x),
                      Negate(Simp(
                          Times(Plus(m, Negate(n), C1), Power(x, Plus(m, Times(CN1, C2, n), C1)),
                              Power(Cos(Plus(a, Times(b, Power(x, n)))), Plus(p,
                                  C2)),
                              Power(Times(Sqr(b), Sqr(n), Plus(p, C1), Plus(p, C2)), CN1)),
                          x))),
                  And(FreeQ(List(a,
                      b), x), LtQ(p, CN1), NeQ(p,
                          CN2),
                      IGtQ(n, C0), IGtQ(m, Subtract(Times(C2, n), C1))))),
          IIntegrate(3409,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(
                          Plus(a_DEFAULT,
                              Times(
                                  b_DEFAULT,
                                  Sin(Plus(c_DEFAULT, Times(d_DEFAULT, Power(x_, n_)))))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Negate(
                      Subst(
                          Integrate(
                              Times(
                                  Power(
                                      Plus(a,
                                          Times(b,
                                              Sin(Plus(c, Times(d, Power(Power(x, n), CN1)))))),
                                      p),
                                  Power(Power(x, Plus(m, C2)), CN1)),
                              x),
                          x, Power(x, CN1))),
                  And(FreeQ(List(a, b, c,
                      d), x), IGtQ(p,
                          C0),
                      ILtQ(n, C0), IntegerQ(m), EqQ(n, CN2)))),
          IIntegrate(3410,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(Cos(Plus(c_DEFAULT, Times(d_DEFAULT, Power(x_, n_)))),
                                  b_DEFAULT)),
                          p_DEFAULT),
                      Power(x_, m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Negate(
                      Subst(
                          Integrate(
                              Times(
                                  Power(
                                      Plus(a,
                                          Times(b,
                                              Cos(Plus(c, Times(d, Power(Power(x, n), CN1)))))),
                                      p),
                                  Power(Power(x, Plus(m, C2)), CN1)),
                              x),
                          x, Power(x, CN1))),
                  And(FreeQ(List(a, b, c,
                      d), x), IGtQ(p,
                          C0),
                      ILtQ(n, C0), IntegerQ(m), EqQ(n, CN2)))),
          IIntegrate(3411,
              Integrate(
                  Times(
                      Power(Times(e_DEFAULT,
                          x_), m_),
                      Power(
                          Plus(a_DEFAULT,
                              Times(
                                  b_DEFAULT, Sin(Plus(c_DEFAULT, Times(d_DEFAULT,
                                      Power(x_, n_)))))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(k,
                          Denominator(m))),
                      Negate(
                          Dist(
                              Times(k, Power(e,
                                  CN1)),
                              Subst(
                                  Integrate(Times(
                                      Power(Plus(a,
                                          Times(b,
                                              Sin(Plus(c, Times(d,
                                                  Power(Times(Power(e, n), Power(x, Times(k, n))),
                                                      CN1)))))),
                                          p),
                                      Power(Power(x, Plus(Times(k, Plus(m, C1)), C1)), CN1)), x),
                                  x, Power(Power(Times(e, x), Power(k, CN1)), CN1)),
                              x))),
                  And(FreeQ(List(a, b, c, d, e), x), IGtQ(p, C0), ILtQ(n, C0), FractionQ(m)))),
          IIntegrate(3412,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(Cos(Plus(c_DEFAULT, Times(d_DEFAULT, Power(x_, n_)))),
                                  b_DEFAULT)),
                          p_DEFAULT),
                      Power(Times(e_DEFAULT, x_), m_)),
                  x_Symbol),
              Condition(
                  With(
                      List(Set(k,
                          Denominator(m))),
                      Negate(
                          Dist(
                              Times(k, Power(e,
                                  CN1)),
                              Subst(
                                  Integrate(
                                      Times(
                                          Power(Plus(a, Times(b,
                                              Cos(Plus(c, Times(d,
                                                  Power(Times(Power(e, n), Power(x, Times(k, n))),
                                                      CN1)))))),
                                              p),
                                          Power(Power(x, Plus(Times(k, Plus(m, C1)), C1)), CN1)),
                                      x),
                                  x, Power(Power(Times(e, x), Power(k, CN1)), CN1)),
                              x))),
                  And(FreeQ(List(a, b, c, d, e), x), IGtQ(p, C0), ILtQ(n, C0), FractionQ(m)))),
          IIntegrate(3413,
              Integrate(
                  Times(
                      Power(Times(e_DEFAULT,
                          x_), m_),
                      Power(
                          Plus(a_DEFAULT,
                              Times(
                                  b_DEFAULT, Sin(Plus(c_DEFAULT, Times(d_DEFAULT,
                                      Power(x_, n_)))))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Negate(
                      Dist(
                          Times(Power(Times(e,
                              x), m), Power(Power(x, CN1),
                                  m)),
                          Subst(
                              Integrate(Times(
                                  Power(
                                      Plus(a,
                                          Times(b,
                                              Sin(Plus(c, Times(d, Power(Power(x, n), CN1)))))),
                                      p),
                                  Power(Power(x, Plus(m, C2)), CN1)), x),
                              x, Power(x, CN1)),
                          x)),
                  And(FreeQ(List(a, b, c, d, e, m), x), IGtQ(p, C0), ILtQ(n, C0),
                      Not(RationalQ(m))))),
          IIntegrate(3414,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(Cos(Plus(c_DEFAULT, Times(d_DEFAULT, Power(x_, n_)))),
                                  b_DEFAULT)),
                          p_DEFAULT),
                      Power(Times(e_DEFAULT, x_), m_)),
                  x_Symbol),
              Condition(
                  Negate(
                      Dist(
                          Times(Power(Times(e,
                              x), m), Power(
                                  Power(x, CN1), m)),
                          Subst(
                              Integrate(
                                  Times(
                                      Power(
                                          Plus(a,
                                              Times(b,
                                                  Cos(Plus(c, Times(d, Power(Power(x, n), CN1)))))),
                                          p),
                                      Power(Power(x, Plus(m, C2)), CN1)),
                                  x),
                              x, Power(x, CN1)),
                          x)),
                  And(FreeQ(List(a, b, c, d, e,
                      m), x), IGtQ(p,
                          C0),
                      ILtQ(n, C0), Not(RationalQ(m))))),
          IIntegrate(3415,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT, Sin(Plus(c_DEFAULT, Times(d_DEFAULT,
                                  Power(x_, n_)))))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Module(
                      List(Set(k,
                          Denominator(n))),
                      Dist(k,
                          Subst(
                              Integrate(
                                  Times(Power(x, Subtract(Times(k, Plus(m, C1)), C1)),
                                      Power(
                                          Plus(a,
                                              Times(b,
                                                  Sin(Plus(c, Times(d, Power(x, Times(k, n))))))),
                                          p)),
                                  x),
                              x, Power(x, Power(k, CN1))),
                          x)),
                  And(FreeQ(List(a, b, c, d, m), x), IntegerQ(p), FractionQ(n)))),
          IIntegrate(3416,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(
                                  Cos(Plus(c_DEFAULT, Times(d_DEFAULT,
                                      Power(x_, n_)))),
                                  b_DEFAULT)),
                          p_DEFAULT),
                      Power(x_, m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Module(
                      List(Set(k,
                          Denominator(n))),
                      Dist(k,
                          Subst(
                              Integrate(
                                  Times(Power(x, Subtract(Times(k, Plus(m, C1)), C1)),
                                      Power(
                                          Plus(a,
                                              Times(b,
                                                  Cos(Plus(c, Times(d, Power(x, Times(k, n))))))),
                                          p)),
                                  x),
                              x, Power(x, Power(k, CN1))),
                          x)),
                  And(FreeQ(List(a, b, c, d, m), x), IntegerQ(p), FractionQ(n)))),
          IIntegrate(3417,
              Integrate(
                  Times(
                      Power(Times(e_,
                          x_), m_),
                      Power(
                          Plus(a_DEFAULT,
                              Times(
                                  b_DEFAULT, Sin(Plus(c_DEFAULT, Times(d_DEFAULT,
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
                          Times(
                              Power(x, m), Power(Plus(a,
                                  Times(b, Sin(Plus(c, Times(d, Power(x, n)))))), p)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, m), x), IntegerQ(p), FractionQ(n)))),
          IIntegrate(3418,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(Cos(Plus(c_DEFAULT, Times(d_DEFAULT, Power(x_, n_)))),
                                  b_DEFAULT)),
                          p_DEFAULT),
                      Power(Times(e_, x_), m_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(e, IntPart(m)), Power(Times(e, x),
                              FracPart(m)),
                          Power(Power(x, FracPart(m)), CN1)),
                      Integrate(Times(
                          Power(x, m), Power(Plus(a, Times(b, Cos(Plus(c, Times(d, Power(x, n)))))),
                              p)),
                          x),
                      x),
                  And(FreeQ(List(a, b, c, d, e, m), x), IntegerQ(p), FractionQ(n)))),
          IIntegrate(3419,
              Integrate(
                  Times(Power(x_, m_DEFAULT),
                      Power(
                          Plus(a_DEFAULT,
                              Times(
                                  b_DEFAULT, Sin(Plus(c_DEFAULT, Times(d_DEFAULT,
                                      Power(x_, n_)))))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(Power(Plus(m, C1), CN1),
                      Subst(
                          Integrate(
                              Power(
                                  Plus(a,
                                      Times(b,
                                          Sin(Plus(c, Times(d,
                                              Power(x,
                                                  Simplify(Times(n, Power(Plus(m, C1), CN1))))))))),
                                  p),
                              x),
                          x, Power(x, Plus(m, C1))),
                      x),
                  And(FreeQ(List(a, b, c, d, m, n), x), IntegerQ(p), NeQ(m, CN1), IGtQ(Simplify(
                      Times(n, Power(Plus(m, C1), CN1))), C0), Not(
                          IntegerQ(n))))),
          IIntegrate(3420,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(Cos(Plus(c_DEFAULT, Times(d_DEFAULT, Power(x_, n_)))),
                                  b_DEFAULT)),
                          p_DEFAULT),
                      Power(x_, m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Power(Plus(m,
                          C1), CN1),
                      Subst(
                          Integrate(
                              Power(
                                  Plus(a,
                                      Times(b,
                                          Cos(Plus(c, Times(d,
                                              Power(x,
                                                  Simplify(Times(n, Power(Plus(m, C1), CN1))))))))),
                                  p),
                              x),
                          x, Power(x, Plus(m, C1))),
                      x),
                  And(FreeQ(List(a, b, c, d, m, n), x), IntegerQ(p), NeQ(m, CN1),
                      IGtQ(Simplify(Times(n, Power(Plus(m, C1), CN1))), C0), Not(IntegerQ(n))))));
}
