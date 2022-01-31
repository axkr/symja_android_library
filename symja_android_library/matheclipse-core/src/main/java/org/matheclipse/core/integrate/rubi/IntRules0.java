package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$p;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN1D2;
import static org.matheclipse.core.expression.F.Complex;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.Identity;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.MatchQ;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Simplify;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Sqrt;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.j_DEFAULT;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_;
import static org.matheclipse.core.expression.F.p_DEFAULT;
import static org.matheclipse.core.expression.F.u_;
import static org.matheclipse.core.expression.F.u_DEFAULT;
import static org.matheclipse.core.expression.F.v_;
import static org.matheclipse.core.expression.F.w_DEFAULT;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.j;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.v;
import static org.matheclipse.core.expression.S.w;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandIntegrand;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FracPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ILtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntSum;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.InverseFunctionQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LinearQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PolyQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RationalQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.SumQ;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules0 {
  public static IAST RULES =
      List(
          IIntegrate(1,
              Integrate(
                  Times(u_DEFAULT, Power(Plus(a_, Times(b_DEFAULT, Power(x_, n_DEFAULT))),
                      p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(Times(u,
                      Power(Times(b, Power(x, n)), p)), x),
                  And(FreeQ(List(a, b, n, p), x), EqQ(a, C0)))),
          IIntegrate(2,
              Integrate(
                  Times(
                      u_DEFAULT, Power(Plus(a_DEFAULT, Times(b_DEFAULT, Power(x_, n_DEFAULT))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(Times(u, Power(a, p)), x), And(FreeQ(List(a, b, n, p), x),
                      EqQ(b, C0)))),
          IIntegrate(3,
              Integrate(
                  Times(u_DEFAULT,
                      Power(
                          Plus(
                              a_, Times(c_DEFAULT, Power(x_, j_DEFAULT)),
                              Times(b_DEFAULT, Power(x_, n_DEFAULT))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(
                          u, Power(Plus(Times(b, Power(x, n)), Times(c, Power(x, Times(C2, n)))),
                              p)),
                      x),
                  And(FreeQ(List(a, b, c, n, p), x), EqQ(j, Times(C2, n)), EqQ(a, C0)))),
          IIntegrate(4,
              Integrate(
                  Times(u_DEFAULT,
                      Power(
                          Plus(
                              a_DEFAULT, Times(c_DEFAULT, Power(x_, j_DEFAULT)),
                              Times(b_DEFAULT, Power(x_, n_DEFAULT))),
                          p_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(Times(u, Power(Plus(a, Times(c, Power(x, Times(C2, n)))),
                      p)), x),
                  And(FreeQ(List(a, b, c, n, p), x), EqQ(j, Times(C2, n)), EqQ(b, C0)))),
          IIntegrate(5,
              Integrate(Times(u_DEFAULT,
                  Power(Plus(a_DEFAULT, Times(c_DEFAULT, Power(x_, j_DEFAULT)),
                      Times(b_DEFAULT, Power(x_, n_DEFAULT))), p_DEFAULT)),
                  x_Symbol),
              Condition(Integrate(Times(u, Power(Plus(a, Times(b, Power(x, n))), p)), x),
                  And(FreeQ(List(a, b, c, n, p), x), EqQ(j, Times(C2, n)), EqQ(c, C0)))),
          IIntegrate(6,
              Integrate(Times(u_DEFAULT,
                  Power(Plus(w_DEFAULT, Times(a_DEFAULT, v_), Times(b_DEFAULT, v_)), p_DEFAULT)),
                  x_Symbol),
              Condition(Integrate(Times(u, Power(Plus(Times(Plus(a, b), v), w), p)), x),
                  And(FreeQ(List(a, b), x), Not(FreeQ(v, x))))),
          IIntegrate(7, Integrate(Times(u_DEFAULT, Power($p("§px"), p_)), x_Symbol),
              Condition(Integrate(Times(u, Power($s("§px"), Simplify(p))), x), And(
                  PolyQ($s("§px"), x), Not(RationalQ(p)), FreeQ(p, x), RationalQ(Simplify(p))))),
          IIntegrate(8, Integrate(a_, x_Symbol), Condition(Simp(Times(a, x), x),
              FreeQ(a, x))),
          IIntegrate(9, Integrate(Times(a_, Plus(b_, Times(c_DEFAULT, x_))), x_Symbol),
              Condition(
                  Simp(Times(a, Sqr(Plus(b, Times(c, x))), Power(Times(C2, c), CN1)),
                      x),
                  FreeQ(List(a, b, c), x))),
          IIntegrate(10, Integrate(Negate(
              u_), x_Symbol), Dist(Identity(CN1), Integrate(u, x),
                  x)),
          IIntegrate(
              11, Integrate(Times(Complex(C0, a_),
                  u_), x_Symbol),
              Condition(
                  Dist(Complex(Identity(C0), a), Integrate(u,
                      x), x),
                  And(FreeQ(a, x), EqQ(Sqr(a), C1)))),
          IIntegrate(
              12, Integrate(Times(a_,
                  u_), x_Symbol),
              Condition(
                  Dist(a, Integrate(u,
                      x), x),
                  And(FreeQ(a, x), Not(MatchQ(u, Condition(Times(b_, v_), FreeQ(b, x))))))),
          IIntegrate(13, Integrate(u_, x_Symbol), Condition(Simp(IntSum(u,
              x), x), SumQ(
                  u))),
          IIntegrate(
              14, Integrate(Times(u_,
                  Power(Times(c_DEFAULT, x_), m_DEFAULT)), x_Symbol),
              Condition(Integrate(ExpandIntegrand(Times(Power(Times(c, x), m), u), x), x),
                  And(FreeQ(List(c, m), x), SumQ(u), Not(LinearQ(u, x)),
                      Not(MatchQ(u,
                          Condition(Plus(a_, Times(b_DEFAULT, v_)),
                              And(FreeQ(List(a, b), x), InverseFunctionQ(v)))))))),
          IIntegrate(15,
              Integrate(Times(u_DEFAULT, Power(Times(a_DEFAULT, Power(x_, n_)), m_)), x_Symbol),
              Condition(
                  Dist(
                      Times(Power(a, IntPart(m)), Power(Times(a, Power(x, n)), FracPart(m)),
                          Power(Power(x, Times(n, FracPart(m))), CN1)),
                      Integrate(Times(u, Power(x, Times(m, n))), x), x),
                  And(FreeQ(List(a, m, n), x), Not(IntegerQ(m))))),
          IIntegrate(16,
              Integrate(Times(u_DEFAULT, Power(v_, m_DEFAULT),
                  Power(Times(b_, v_), n_)), x_Symbol),
              Condition(
                  Dist(
                      Power(Power(b, m), CN1), Integrate(Times(u, Power(Times(b, v), Plus(m, n))),
                          x),
                      x),
                  And(FreeQ(List(b, n), x), IntegerQ(m)))),
          IIntegrate(17,
              Integrate(
                  Times(u_DEFAULT, Power(Times(a_DEFAULT, v_), m_),
                      Power(Times(b_DEFAULT, v_), n_)),
                  x_Symbol),
              Condition(Dist(Times(Power(a, Plus(m, C1D2)), Power(b, Subtract(n, C1D2)),
                  Sqrt(Times(b, v)), Power(Times(a, v), CN1D2)),
                  Integrate(Times(u, Power(v, Plus(m, n))), x), x),
                  And(FreeQ(List(a, b, m), x), Not(IntegerQ(m)), IGtQ(Plus(n, C1D2), C0),
                      IntegerQ(Plus(m, n))))),
          IIntegrate(18,
              Integrate(Times(u_DEFAULT, Power(Times(a_DEFAULT, v_), m_),
                  Power(Times(b_DEFAULT, v_), n_)), x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(a, Subtract(m, C1D2)), Power(b, Plus(n, C1D2)), Sqrt(Times(a, v)),
                          Power(Times(b, v), CN1D2)),
                      Integrate(Times(u, Power(v, Plus(m, n))), x), x),
                  And(FreeQ(List(a, b, m), x), Not(IntegerQ(m)), ILtQ(Subtract(n, C1D2), C0),
                      IntegerQ(Plus(m, n))))),
          IIntegrate(19,
              Integrate(
                  Times(u_DEFAULT, Power(Times(a_DEFAULT,
                      v_), m_), Power(Times(b_DEFAULT, v_),
                          n_)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(a, Plus(m, n)), Power(Times(b, v), n), Power(Power(Times(a, v), n),
                              CN1)),
                      Integrate(Times(u, Power(v, Plus(m, n))), x), x),
                  And(FreeQ(List(a, b, m, n), x), Not(IntegerQ(m)), Not(IntegerQ(n)),
                      IntegerQ(Plus(m, n))))),
          IIntegrate(20,
              Integrate(Times(u_DEFAULT, Power(Times(a_DEFAULT, v_), m_),
                  Power(Times(b_DEFAULT, v_), n_)), x_Symbol),
              Condition(
                  Dist(
                      Times(Power(b, IntPart(n)), Power(Times(b, v), FracPart(n)),
                          Power(Times(Power(a, IntPart(n)), Power(Times(a, v), FracPart(n))), CN1)),
                      Integrate(Times(u, Power(Times(a, v), Plus(m, n))), x), x),
                  And(FreeQ(List(a, b, m, n), x), Not(IntegerQ(m)), Not(IntegerQ(n)),
                      Not(IntegerQ(Plus(m, n)))))));
}
