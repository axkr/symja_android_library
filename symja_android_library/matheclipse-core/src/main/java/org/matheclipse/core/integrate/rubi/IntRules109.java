package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Coefficient;
import static org.matheclipse.core.expression.F.CompoundExpression;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.F_;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.G_;
import static org.matheclipse.core.expression.F.Gamma;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.If;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.Module;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Part;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.PolynomialQ;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.SameQ;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.f_DEFAULT;
import static org.matheclipse.core.expression.F.g_DEFAULT;
import static org.matheclipse.core.expression.F.h_DEFAULT;
import static org.matheclipse.core.expression.F.i_DEFAULT;
import static org.matheclipse.core.expression.F.j_DEFAULT;
import static org.matheclipse.core.expression.F.k_DEFAULT;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_;
import static org.matheclipse.core.expression.F.p_DEFAULT;
import static org.matheclipse.core.expression.F.q_DEFAULT;
import static org.matheclipse.core.expression.F.u_;
import static org.matheclipse.core.expression.F.v_;
import static org.matheclipse.core.expression.F.w_;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.FSymbol;
import static org.matheclipse.core.expression.S.False;
import static org.matheclipse.core.expression.S.GSymbol;
import static org.matheclipse.core.expression.S.True;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.d;
import static org.matheclipse.core.expression.S.e;
import static org.matheclipse.core.expression.S.f;
import static org.matheclipse.core.expression.S.g;
import static org.matheclipse.core.expression.S.h;
import static org.matheclipse.core.expression.S.i;
import static org.matheclipse.core.expression.S.j;
import static org.matheclipse.core.expression.S.k;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.q;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.v;
import static org.matheclipse.core.expression.S.w;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.expression.S.z;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Dist;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandIntegrand;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandToSum;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FracPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ILtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntHide;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntPart;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LinearMatchQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LinearQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NormalizePowerOfLinear;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PowerOfLinearMatchQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PowerOfLinearQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PowerQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Unintegrable;
import org.matheclipse.core.interfaces.IAST;

/**
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi - rule-based
 * integrator</a>.
 *
 */
class IntRules109 {
  public static IAST RULES =
      List(
          IIntegrate(2181,
              Integrate(
                  Times(
                      Power(F_, Times(g_DEFAULT,
                          Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_)),
                  x_Symbol),
              Condition(
                  Negate(
                      Simp(
                          Times(Power(FSymbol, Times(g, Subtract(e, Times(c, f, Power(d, CN1))))),
                              Power(Plus(c, Times(d, x)), FracPart(m)),
                              Gamma(Plus(m, C1),
                                  Times(CN1, f, g, Log(FSymbol), Power(d, CN1),
                                      Plus(c, Times(d, x)))),
                              Power(
                                  Times(d,
                                      Power(Times(CN1, f, g, Log(FSymbol), Power(d, CN1)),
                                          Plus(IntPart(m), C1)),
                                      Power(Times(CN1, f, g, Log(FSymbol), Plus(c, Times(d, x)),
                                          Power(d, CN1)), FracPart(m))),
                                  CN1)),
                          x)),
                  And(FreeQ(List(FSymbol, c, d, e, f, g, m), x), Not(IntegerQ(m))))),
          IIntegrate(2182,
              Integrate(
                  Times(Power(Times(b_DEFAULT, Power(F_,
                      Times(g_DEFAULT, Plus(e_DEFAULT, Times(f_DEFAULT, x_))))), n_), Power(
                          Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(Times(b, Power(FSymbol, Times(g, Plus(e, Times(f, x))))), n), Power(
                              Power(FSymbol, Times(g, n, Plus(e, Times(f, x)))), CN1)),
                      Integrate(
                          Times(
                              Power(Plus(c, Times(d, x)), m), Power(FSymbol,
                                  Times(g, n, Plus(e, Times(f, x))))),
                          x),
                      x),
                  FreeQ(List(FSymbol, b, c, d, e, f, g, m, n), x))),
          IIntegrate(2183,
              Integrate(
                  Times(
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  Power(
                                      Power(
                                          F_,
                                          Times(g_DEFAULT, Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                                      n_DEFAULT))),
                          p_DEFAULT),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(Power(Plus(c, Times(d, x)), m),
                          Power(
                              Plus(a,
                                  Times(b, Power(Power(FSymbol, Times(g, Plus(e, Times(f, x)))),
                                      n))),
                              p),
                          x),
                      x),
                  And(FreeQ(List(FSymbol, a, b, c, d, e, f, g, m, n), x), IGtQ(p, C0)))),
          IIntegrate(2184,
              Integrate(
                  Times(
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  Power(
                                      Power(F_,
                                          Times(g_DEFAULT, Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                                      n_DEFAULT))),
                          CN1),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(
                              Power(Plus(c, Times(d, x)), Plus(m,
                                  C1)),
                              Power(Times(a, d, Plus(m, C1)), CN1)),
                          x),
                      Dist(Times(b, Power(a, CN1)),
                          Integrate(
                              Times(Power(Plus(c, Times(d, x)), m),
                                  Power(Power(FSymbol, Times(g, Plus(e, Times(f, x)))), n),
                                  Power(Plus(a,
                                      Times(b,
                                          Power(Power(FSymbol, Times(g, Plus(e, Times(f, x)))),
                                              n))),
                                      CN1)),
                              x),
                          x)),
                  And(FreeQ(List(FSymbol, a, b, c, d, e, f, g, n), x), IGtQ(m, C0)))),
          IIntegrate(2185,
              Integrate(
                  Times(
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  Power(
                                      Power(F_,
                                          Times(g_DEFAULT, Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                                      n_DEFAULT))),
                          p_),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Dist(Power(a, CN1),
                          Integrate(
                              Times(Power(Plus(c, Times(d, x)), m),
                                  Power(
                                      Plus(a,
                                          Times(
                                              b,
                                              Power(
                                                  Power(FSymbol, Times(g, Plus(e, Times(f, x)))),
                                                  n))),
                                      Plus(p, C1))),
                              x),
                          x),
                      Dist(Times(b, Power(a, CN1)), Integrate(Times(Power(Plus(c, Times(d, x)), m),
                          Power(Power(FSymbol, Times(g, Plus(e, Times(f, x)))), n),
                          Power(
                              Plus(a,
                                  Times(b,
                                      Power(Power(FSymbol, Times(g, Plus(e, Times(f, x)))), n))),
                              p)),
                          x), x)),
                  And(FreeQ(List(FSymbol, a, b, c, d, e, f, g, n), x), ILtQ(p, C0), IGtQ(m, C0)))),
          IIntegrate(2186,
              Integrate(
                  Times(
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  Power(
                                      Power(F_,
                                          Times(g_DEFAULT, Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                                      n_DEFAULT))),
                          p_),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  With(
                      List(
                          Set(u,
                              IntHide(
                                  Power(Plus(a,
                                      Times(b,
                                          Power(Power(FSymbol, Times(g, Plus(e, Times(f, x)))),
                                              n))),
                                      p),
                                  x))),
                      Subtract(
                          Dist(Power(Plus(c, Times(d, x)),
                              m), u, x),
                          Dist(Times(d, m),
                              Integrate(Times(Power(Plus(c, Times(d, x)), Subtract(m, C1)), u),
                                  x),
                              x))),
                  And(FreeQ(List(FSymbol, a, b, c, d, e, f, g, n), x), IGtQ(m, C0), LtQ(p, CN1)))),
          IIntegrate(2187,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT, Times(b_DEFAULT, Power(Power(F_, Times(g_DEFAULT, v_)),
                              n_DEFAULT))),
                          p_DEFAULT),
                      Power(u_, m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Integrate(
                      Times(
                          Power(NormalizePowerOfLinear(u,
                              x), m),
                          Power(
                              Plus(
                                  a, Times(b, Power(Power(FSymbol, Times(g, ExpandToSum(v, x))),
                                      n))),
                              p)),
                      x),
                  And(FreeQ(List(FSymbol, a, b, g, n, p), x), LinearQ(v, x), PowerOfLinearQ(u,
                      x), Not(And(LinearMatchQ(v, x), PowerOfLinearMatchQ(u, x))),
                      IntegerQ(m)))),
          IIntegrate(2188,
              Integrate(
                  Times(
                      Power(
                          Plus(
                              a_DEFAULT, Times(b_DEFAULT, Power(Power(F_, Times(g_DEFAULT, v_)),
                                  n_DEFAULT))),
                          p_DEFAULT),
                      Power(u_, m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Module(
                      List(Set($s("uu"),
                          NormalizePowerOfLinear(u, x)), z),
                      Simp(
                          CompoundExpression(
                              Set(z,
                                  If(And(PowerQ($s("uu")), FreeQ(Part($s("uu"), C2), x)),
                                      Power(Part($s("uu"), C1), Times(m,
                                          Part($s("uu"), C2))),
                                      Power($s("uu"), m))),
                              Times(
                                  Power($s("uu"), m), Integrate(
                                      Times(z,
                                          Power(Plus(a,
                                              Times(b,
                                                  Power(Power(FSymbol, Times(g, ExpandToSum(v, x))),
                                                      n))),
                                              p)),
                                      x),
                                  Power(z, CN1))),
                          x)),
                  And(FreeQ(List(FSymbol, a, b, g, m, n, p), x), LinearQ(v, x),
                      PowerOfLinearQ(u, x), Not(And(LinearMatchQ(v, x),
                          PowerOfLinearMatchQ(u, x))),
                      Not(IntegerQ(m))))),
          IIntegrate(2189,
              Integrate(
                  Times(
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  Power(
                                      Power(
                                          F_, Times(g_DEFAULT, Plus(e_DEFAULT, Times(f_DEFAULT,
                                              x_)))),
                                      n_DEFAULT))),
                          p_DEFAULT),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Unintegrable(
                      Times(
                          Power(
                              Plus(a,
                                  Times(b, Power(Power(FSymbol, Times(g, Plus(e, Times(f, x)))),
                                      n))),
                              p),
                          Power(Plus(c, Times(d, x)), m)),
                      x),
                  FreeQ(List(a, b, c, d, e, f, g, m, n, p), x))),
          IIntegrate(2190,
              Integrate(
                  Times(
                      Power(
                          Power(F_, Times(g_DEFAULT, Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          n_DEFAULT),
                      Power(
                          Plus(a_,
                              Times(b_DEFAULT,
                                  Power(
                                      Power(F_,
                                          Times(g_DEFAULT, Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                                      n_DEFAULT))),
                          CN1),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(
                              Power(Plus(c,
                                  Times(d, x)), m),
                              Log(Plus(
                                  C1, Times(b,
                                      Power(Power(FSymbol, Times(g, Plus(e, Times(f, x)))),
                                          n),
                                      Power(a, CN1)))),
                              Power(Times(b, f, g, n, Log(FSymbol)), CN1)),
                          x),
                      Dist(
                          Times(d, m, Power(Times(b, f, g, n, Log(FSymbol)), CN1)), Integrate(
                              Times(Power(Plus(c, Times(d, x)), Subtract(m, C1)),
                                  Log(Plus(C1,
                                      Times(b,
                                          Power(Power(FSymbol, Times(g, Plus(e, Times(f, x)))), n),
                                          Power(a, CN1))))),
                              x),
                          x)),
                  And(FreeQ(List(FSymbol, a, b, c, d, e, f, g, n), x), IGtQ(m, C0)))),
          IIntegrate(2191,
              Integrate(
                  Times(
                      Power(
                          Power(F_, Times(g_DEFAULT,
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          n_DEFAULT),
                      Power(
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT,
                                  Power(
                                      Power(F_,
                                          Times(g_DEFAULT, Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                                      n_DEFAULT))),
                          p_DEFAULT),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Subtract(
                      Simp(
                          Times(Power(Plus(c, Times(d, x)), m),
                              Power(
                                  Plus(a,
                                      Times(b,
                                          Power(Power(FSymbol, Times(g, Plus(e, Times(f, x)))),
                                              n))),
                                  Plus(p, C1)),
                              Power(Times(b, f, g, n, Plus(p, C1), Log(FSymbol)), CN1)),
                          x),
                      Dist(Times(d, m, Power(Times(b, f, g, n, Plus(p, C1), Log(FSymbol)), CN1)),
                          Integrate(Times(Power(Plus(c, Times(d, x)), Subtract(m, C1)), Power(
                              Plus(a,
                                  Times(b,
                                      Power(Power(FSymbol, Times(g, Plus(e, Times(f, x)))), n))),
                              Plus(p, C1))), x),
                          x)),
                  And(FreeQ(List(FSymbol, a, b, c, d, e, f, g, m, n, p), x), NeQ(p, CN1)))),
          IIntegrate(2192,
              Integrate(
                  Times(
                      Power(
                          Power(F_, Times(g_DEFAULT,
                              Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                          n_DEFAULT),
                      Power(
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT,
                                  Power(
                                      Power(F_,
                                          Times(g_DEFAULT, Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                                      n_DEFAULT))),
                          p_DEFAULT),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Unintegrable(
                      Times(Power(Power(FSymbol, Times(g, Plus(e, Times(f, x)))), n),
                          Power(
                              Plus(a,
                                  Times(b, Power(Power(FSymbol, Times(g, Plus(e, Times(f, x)))),
                                      n))),
                              p),
                          Power(Plus(c, Times(d, x)), m)),
                      x),
                  FreeQ(List(FSymbol, a, b, c, d, e, f, g, m, n, p), x))),
          IIntegrate(2193,
              Integrate(
                  Times(
                      Power(
                          Plus(a_DEFAULT,
                              Times(b_DEFAULT,
                                  Power(
                                      Power(F_,
                                          Times(g_DEFAULT, Plus(e_DEFAULT, Times(f_DEFAULT, x_)))),
                                      n_DEFAULT))),
                          p_DEFAULT),
                      Power(
                          Times(
                              k_DEFAULT, Power(G_, Times(j_DEFAULT,
                                  Plus(h_DEFAULT, Times(i_DEFAULT, x_))))),
                          q_DEFAULT),
                      Power(Plus(c_DEFAULT, Times(d_DEFAULT, x_)), m_DEFAULT)),
                  x_Symbol),
              Condition(
                  Dist(
                      Times(
                          Power(Times(k, Power(GSymbol,
                              Times(j, Plus(h, Times(i, x))))), q),
                          Power(Power(Power(FSymbol, Times(g, Plus(e, Times(f, x)))), n), CN1)),
                      Integrate(Times(Power(Plus(c, Times(d, x)), m),
                          Power(Power(FSymbol, Times(g, Plus(e, Times(f, x)))), n),
                          Power(
                              Plus(a,
                                  Times(b, Power(Power(FSymbol, Times(g, Plus(e, Times(f, x)))),
                                      n))),
                              p)),
                          x),
                      x),
                  And(FreeQ(List(FSymbol, a, b, c, d, e, f, g, h, i, j, k, m, n, p, q), x),
                      EqQ(Subtract(Times(f, g, n, Log(FSymbol)),
                          Times(i, j, q, Log(GSymbol))), C0),
                      NeQ(Subtract(
                          Power(Times(k, Power(GSymbol, Times(j, Plus(h, Times(i, x))))), q),
                          Power(Power(FSymbol, Times(g, Plus(e, Times(f, x)))), n)),
                          C0)))),
          IIntegrate(2194,
              Integrate(
                  Power(
                      Power(F_, Times(c_DEFAULT,
                          Plus(a_DEFAULT, Times(b_DEFAULT, x_)))),
                      n_DEFAULT),
                  x_Symbol),
              Condition(
                  Simp(
                      Times(
                          Power(Power(FSymbol, Times(c, Plus(a, Times(b, x)))),
                              n),
                          Power(Times(b, c, n, Log(FSymbol)), CN1)),
                      x),
                  FreeQ(List(FSymbol, a, b, c, n), x))),
          IIntegrate(
              2195, Integrate(Times(Power(F_, Times(c_DEFAULT, v_)),
                  u_), x_Symbol),
              Condition(
                  Integrate(ExpandIntegrand(Times(u, Power(FSymbol, Times(c, ExpandToSum(v, x)))),
                      x), x),
                  And(FreeQ(List(FSymbol, c), x), PolynomialQ(u, x), LinearQ(v, x),
                      SameQ(False, True)))),
          IIntegrate(
              2196, Integrate(Times(Power(F_, Times(c_DEFAULT, v_)),
                  u_), x_Symbol),
              Condition(
                  Integrate(ExpandIntegrand(Power(FSymbol, Times(c, ExpandToSum(v, x))), u,
                      x), x),
                  And(FreeQ(List(FSymbol, c), x), PolynomialQ(u, x), LinearQ(v, x),
                      Not(SameQ(False, True))))),
          IIntegrate(2197,
              Integrate(Times(Power(F_, Times(c_DEFAULT,
                  v_)), Power(u_,
                      m_DEFAULT),
                  w_), x_Symbol),
              Condition(
                  With(
                      List(Set(b, Coefficient(v, x, C1)), Set(d, Coefficient(u, x, C0)),
                          Set(e, Coefficient(u, x, C1)), Set(f, Coefficient(w, x, C0)),
                          Set(g, Coefficient(w, x, C1))),
                      Condition(
                          Simp(Times(g, Power(u, Plus(m, C1)), Power(FSymbol, Times(c, v)),
                              Power(Times(b, c, e, Log(FSymbol)), CN1)), x),
                          EqQ(Subtract(Times(e, g, Plus(m, C1)),
                              Times(b, c, Subtract(Times(e, f), Times(d, g)), Log(FSymbol))), C0))),
                  And(FreeQ(List(FSymbol, c, m), x), LinearQ(List(u, v, w), x)))),
          IIntegrate(2198,
              Integrate(Times(Power(F_, Times(c_DEFAULT, v_)), Power(u_, m_DEFAULT),
                  w_), x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Times(w, Power(NormalizePowerOfLinear(u, x), m),
                              Power(FSymbol, Times(c, ExpandToSum(v, x)))),
                          x),
                      x),
                  And(FreeQ(List(FSymbol, c), x), PolynomialQ(w, x), LinearQ(v, x),
                      PowerOfLinearQ(u, x), IntegerQ(m), SameQ(False, True)))),
          IIntegrate(2199,
              Integrate(Times(Power(F_, Times(c_DEFAULT,
                  v_)), Power(u_,
                      m_DEFAULT),
                  w_), x_Symbol),
              Condition(
                  Integrate(
                      ExpandIntegrand(
                          Power(FSymbol, Times(c, ExpandToSum(v, x))), Times(w,
                              Power(NormalizePowerOfLinear(u, x), m)),
                          x),
                      x),
                  And(FreeQ(List(FSymbol,
                      c), x), PolynomialQ(w, x), LinearQ(v, x), PowerOfLinearQ(u,
                          x),
                      IntegerQ(m), Not(SameQ(False, True))))),
          IIntegrate(2200,
              Integrate(Times(Power(F_, Times(c_DEFAULT, v_)), Power(u_, m_DEFAULT), w_), x_Symbol),
              Condition(
                  Module(
                      List(Set($s("uu"),
                          NormalizePowerOfLinear(u, x)), z),
                      Simp(CompoundExpression(
                          Set(z, If(And(PowerQ($s("uu")), FreeQ(Part($s("uu"), C2), x)),
                              Power(Part($s("uu"), C1), Times(m, Part($s("uu"), C2))),
                              Power($s("uu"), m))),
                          Times(Power($s("uu"), m),
                              Integrate(
                                  ExpandIntegrand(
                                      Times(w, z, Power(FSymbol, Times(c, ExpandToSum(v, x)))), x),
                                  x),
                              Power(z, CN1))),
                          x)),
                  And(FreeQ(List(FSymbol, c, m), x), PolynomialQ(w, x), LinearQ(v, x),
                      PowerOfLinearQ(u, x), Not(IntegerQ(m))))));
}
