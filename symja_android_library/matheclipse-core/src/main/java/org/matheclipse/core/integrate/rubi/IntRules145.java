package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.Denominator;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Log;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Simplify;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.f_;
import static org.matheclipse.core.expression.F.f_DEFAULT;
import static org.matheclipse.core.expression.F.g_DEFAULT;
import static org.matheclipse.core.expression.F.h_DEFAULT;
import static org.matheclipse.core.expression.F.list;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_DEFAULT;
import static org.matheclipse.core.expression.F.q_;
import static org.matheclipse.core.expression.F.q_DEFAULT;
import static org.matheclipse.core.expression.F.r_DEFAULT;
import static org.matheclipse.core.expression.F.u_;
import static org.matheclipse.core.expression.F.v_;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.d;
import static org.matheclipse.core.expression.S.e;
import static org.matheclipse.core.expression.S.f;
import static org.matheclipse.core.expression.S.g;
import static org.matheclipse.core.expression.S.h;
import static org.matheclipse.core.expression.S.k;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.q;
import static org.matheclipse.core.expression.S.r;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.v;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.BinomialMatchQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.BinomialQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandIntegrand;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandToSum;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.FractionQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ILtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntHide;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LinearMatchQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LinearQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.RationalQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Star;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Subst;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Unintegrable;
import org.matheclipse.core.interfaces.IAST;

/** 
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class IntRules145 { 
  public static IAST RULES = List( 
IIntegrate(2901,Integrate(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(x_,n_))),p_DEFAULT))),b_DEFAULT)),q_),x_Symbol),
    Condition(With(list(Set(k,Denominator(n))),Simp(Star(k,Subst(Integrate(Times(Power(x,Subtract(k,C1)),Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,Power(x,Times(k,n)))),p))))),q)),x),x,Power(x,Power(k,CN1)))),x)),And(FreeQ(List(a,b,c,d,e,p,q),x),FractionQ(n)))),
IIntegrate(2902,Integrate(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(x_,n_))),p_DEFAULT))),b_DEFAULT)),q_),x_Symbol),
    Condition(Unintegrable(Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,Power(x,n))),p))))),q),x),FreeQ(List(a,b,c,d,e,n,p,q),x))),
IIntegrate(2903,Integrate(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(v_,p_DEFAULT))),b_DEFAULT)),q_DEFAULT),x_Symbol),
    Condition(Integrate(Power(Plus(a,Times(b,Log(Times(c,Power(ExpandToSum(v,x),p))))),q),x),And(FreeQ(List(a,b,c,p,q),x),BinomialQ(v,x),Not(BinomialMatchQ(v,x))))),
IIntegrate(2904,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(x_,n_))),p_DEFAULT))),b_DEFAULT)),q_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(n,CN1),Subst(Integrate(Times(Power(x,Subtract(Simplify(Times(Plus(m,C1),Power(n,CN1))),C1)),Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,x)),p))))),q)),x),x,Power(x,n))),x),And(FreeQ(List(a,b,c,d,e,m,n,p,q),x),IntegerQ(Simplify(Times(Plus(m,C1),Power(n,CN1)))),Or(GtQ(Times(Plus(m,C1),Power(n,CN1)),C0),IGtQ(q,C0)),Not(And(EqQ(q,C1),ILtQ(n,C0),IGtQ(m,C0)))))),
IIntegrate(2905,Integrate(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(x_,n_))),p_DEFAULT))),b_DEFAULT)),Power(Times(f_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(f,x),Plus(m,C1)),Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,Power(x,n))),p))))),Power(Times(f,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,e,n,p,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(x,Subtract(n,C1)),Power(Times(f,x),Plus(m,C1)),Power(Plus(d,Times(e,Power(x,n))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,f,m,n,p),x),NeQ(m,CN1)))),
      IIntegrate(2906,
          Integrate(
              Times(
                  Power(
                      Plus(a_DEFAULT,
                          Times(
                              Log(Times(c_DEFAULT, Power(Plus(d_, Times(e_DEFAULT, Power(x_, n_))),
                                  p_DEFAULT))),
                              b_DEFAULT)),
                      q_DEFAULT),
                  Power(Times(f_, x_), m_)),
              x_Symbol),
          Condition(
              Simp(
                  Star(
                      Times(Power(Times(f,
                          x), m), Power(Power(x, m),
                              CN1)),
                      Integrate(Times(Power(x, m),
                          Power(
                              Plus(a,
                                  Times(b,
                                      Log(Times(c, Power(Plus(d, Times(e, Power(x, n))), p))))),
                              q)),
                          x)),
                  x),
              And(FreeQ(List(a, b, c, d, e, f, m, n, p, q), x),
                  IntegerQ(Simplify(Times(Plus(m, C1), Power(n, CN1)))),
                  Or(GtQ(Times(Plus(m, C1), Power(n, CN1)), C0), IGtQ(q, C0))))),
IIntegrate(2907,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(x_,n_))),p_DEFAULT))),b_DEFAULT)),q_),Power(Times(f_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(f,x),Plus(m,C1)),Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,Power(x,n))),p))))),q),Power(Times(f,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,e,n,p,q,Power(Times(Power(f,n),Plus(m,C1)),CN1)),Integrate(Times(Power(Times(f,x),Plus(m,n)),Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,Power(x,n))),p))))),Subtract(q,C1)),Power(Plus(d,Times(e,Power(x,n))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,f,m,p),x),IGtQ(q,C1),IntegerQ(n),NeQ(m,CN1)))),
IIntegrate(2908,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(x_,n_))),p_DEFAULT))),b_DEFAULT)),q_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(k,Denominator(n))),Simp(Star(k,Subst(Integrate(Times(Power(x,Subtract(Times(k,Plus(m,C1)),C1)),Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,Power(x,Times(k,n)))),p))))),q)),x),x,Power(x,Power(k,CN1)))),x)),And(FreeQ(List(a,b,c,d,e,m,p,q),x),FractionQ(n)))),
IIntegrate(2909,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(x_,n_))),p_DEFAULT))),b_DEFAULT)),q_DEFAULT),Power(Times(f_,x_),m_)),x_Symbol),
    Condition(Simp(Star(Times(Power(Times(f,x),m),Power(Power(x,m),CN1)),Integrate(Times(Power(x,m),Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,Power(x,n))),p))))),q)),x)),x),And(FreeQ(List(a,b,c,d,e,f,m,p,q),x),FractionQ(n)))),
IIntegrate(2910,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(x_,n_))),p_DEFAULT))),b_DEFAULT)),q_DEFAULT),Power(Times(f_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(f,x),m),Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,Power(x,n))),p))))),q)),x),FreeQ(List(a,b,c,d,e,f,m,n,p,q),x))),
IIntegrate(2911,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(v_,p_DEFAULT))),b_DEFAULT)),q_DEFAULT),Power(Times(f_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Times(f,x),m),Power(Plus(a,Times(b,Log(Times(c,Power(ExpandToSum(v,x),p))))),q)),x),And(FreeQ(List(a,b,c,f,m,p,q),x),BinomialQ(v,x),Not(BinomialMatchQ(v,x))))),
IIntegrate(2912,Integrate(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(x_,n_))),p_DEFAULT))),b_DEFAULT)),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(Log(Plus(f,Times(g,x))),Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,Power(x,n))),p))))),Power(g,CN1)),x),Simp(Star(Times(b,e,n,p,Power(g,CN1)),Integrate(Times(Power(x,Subtract(n,C1)),Log(Plus(f,Times(g,x))),Power(Plus(d,Times(e,Power(x,n))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,f,g,n,p),x),RationalQ(n)))),
IIntegrate(2913,Integrate(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(x_,n_))),p_DEFAULT))),b_DEFAULT)),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),r_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(f,Times(g,x)),Plus(r,C1)),Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,Power(x,n))),p))))),Power(Times(g,Plus(r,C1)),CN1)),x),Simp(Star(Times(b,e,n,p,Power(Times(g,Plus(r,C1)),CN1)),Integrate(Times(Power(x,Subtract(n,C1)),Power(Plus(f,Times(g,x)),Plus(r,C1)),Power(Plus(d,Times(e,Power(x,n))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,f,g,n,p,r),x),Or(IGtQ(r,C0),RationalQ(n)),NeQ(r,CN1)))),
IIntegrate(2914,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(x_,n_))),p_DEFAULT))),b_DEFAULT)),q_DEFAULT),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),r_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(f,Times(g,x)),r),Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,Power(x,n))),p))))),q)),x),FreeQ(List(a,b,c,d,e,f,g,n,p,q,r),x))),
IIntegrate(2915,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(v_,p_DEFAULT))),b_DEFAULT)),q_DEFAULT),Power(u_,r_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(ExpandToSum(u,x),r),Power(Plus(a,Times(b,Log(Times(c,Power(ExpandToSum(v,x),p))))),q)),x),And(FreeQ(List(a,b,c,p,q,r),x),LinearQ(u,x),BinomialQ(v,x),Not(And(LinearMatchQ(u,x),BinomialMatchQ(v,x)))))),
IIntegrate(2916,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(x_,n_))),p_DEFAULT))),b_DEFAULT)),q_DEFAULT),Power(x_,m_DEFAULT),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),r_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,Power(x,n))),p))))),q),Times(Power(x,m),Power(Plus(f,Times(g,x)),r)),x),x),And(FreeQ(List(a,b,c,d,e,f,g,n,p,q),x),IntegerQ(m),IntegerQ(r)))),
IIntegrate(2917,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(x_,n_DEFAULT))),p_DEFAULT))),b_DEFAULT)),q_DEFAULT),Power(Times(h_DEFAULT,x_),m_),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),r_DEFAULT)),x_Symbol),
    Condition(With(list(Set(k,Denominator(m))),Simp(Star(Times(k,Power(h,CN1)),Subst(Integrate(Times(Power(x,Subtract(Times(k,Plus(m,C1)),C1)),Power(Plus(f,Times(g,Power(x,k),Power(h,CN1))),r),Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,Power(x,Times(k,n)),Power(Power(h,n),CN1))),p))))),q)),x),x,Power(Times(h,x),Power(k,CN1)))),x)),And(FreeQ(List(a,b,c,d,e,f,g,h,p,r),x),FractionQ(m),IntegerQ(n),IntegerQ(r)))),
IIntegrate(2918,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(x_,n_))),p_DEFAULT))),b_DEFAULT)),q_DEFAULT),Power(Times(h_DEFAULT,x_),m_DEFAULT),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),r_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(h,x),m),Power(Plus(f,Times(g,x)),r),Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,Power(x,n))),p))))),q)),x),FreeQ(List(a,b,c,d,e,f,g,h,m,n,p,q,r),x))),
IIntegrate(2919,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(v_,p_DEFAULT))),b_DEFAULT)),q_DEFAULT),Power(u_,r_DEFAULT),Power(Times(h_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Times(h,x),m),Power(ExpandToSum(u,x),r),Power(Plus(a,Times(b,Log(Times(c,Power(ExpandToSum(v,x),p))))),q)),x),And(FreeQ(List(a,b,c,h,m,p,q,r),x),LinearQ(u,x),BinomialQ(v,x),Not(And(LinearMatchQ(u,x),BinomialMatchQ(v,x)))))),
IIntegrate(2920,Integrate(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(x_,n_))),p_DEFAULT))),b_DEFAULT)),Power(Plus(f_,Times(g_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Power(Plus(f,Times(g,Sqr(x))),CN1),x))),Subtract(Simp(Times(u,Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,Power(x,n))),p)))))),x),Simp(Star(Times(b,e,n,p),Integrate(Times(u,Power(x,Subtract(n,C1)),Power(Plus(d,Times(e,Power(x,n))),CN1)),x)),x))),And(FreeQ(List(a,b,c,d,e,f,g,n,p),x),IntegerQ(n))))
  );
}
