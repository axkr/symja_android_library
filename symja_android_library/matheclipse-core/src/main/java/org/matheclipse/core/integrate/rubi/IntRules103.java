package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$p;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN2;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.f_DEFAULT;
import static org.matheclipse.core.expression.F.g_DEFAULT;
import static org.matheclipse.core.expression.F.list;
import static org.matheclipse.core.expression.F.m_;
import static org.matheclipse.core.expression.F.m_DEFAULT;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.n_DEFAULT;
import static org.matheclipse.core.expression.F.p_;
import static org.matheclipse.core.expression.F.p_DEFAULT;
import static org.matheclipse.core.expression.F.q_;
import static org.matheclipse.core.expression.F.q_DEFAULT;
import static org.matheclipse.core.expression.F.r_DEFAULT;
import static org.matheclipse.core.expression.F.s_;
import static org.matheclipse.core.expression.F.u_;
import static org.matheclipse.core.expression.F.u_DEFAULT;
import static org.matheclipse.core.expression.F.v_;
import static org.matheclipse.core.expression.F.w_;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.F.z_;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.d;
import static org.matheclipse.core.expression.S.e;
import static org.matheclipse.core.expression.S.f;
import static org.matheclipse.core.expression.S.g;
import static org.matheclipse.core.expression.S.m;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.q;
import static org.matheclipse.core.expression.S.r;
import static org.matheclipse.core.expression.S.s;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.v;
import static org.matheclipse.core.expression.S.w;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.expression.S.z;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.BinomialDegree;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.BinomialMatchQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.BinomialQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Coeff;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandToSum;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Expon;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GeneralizedBinomialMatchQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GeneralizedBinomialQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PolyQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.QuadraticMatchQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.QuadraticQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Rt;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Star;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Subst;
import org.matheclipse.core.interfaces.IAST;

/** 
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class IntRules103 { 
  public static IAST RULES = List( 
IIntegrate(2061,Integrate(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Times(c_DEFAULT,Power(x_,CN1)),n_))),p_),Power(Times(d_DEFAULT,x_),m_)),x_Symbol),
    Condition(Simp(Star(Times(CN1,c,Power(Times(d,x),m),Power(Times(c,Power(x,CN1)),m)),Subst(Integrate(Times(Power(Plus(a,Times(b,Power(x,n))),p),Power(Power(x,Plus(m,C2)),CN1)),x),x,Times(c,Power(x,CN1)))),x),And(FreeQ(List(a,b,c,d,m,n,p),x),Not(IntegerQ(m))))),
IIntegrate(2062,Integrate(Power(Plus(a_DEFAULT,Times(c_DEFAULT,Power(Times(d_DEFAULT,Power(x_,CN1)),$p("n2",true))),Times(b_DEFAULT,Power(Times(d_DEFAULT,Power(x_,CN1)),n_))),p_),x_Symbol),
    Condition(Simp(Star(Negate(d),Subst(Integrate(Times(Power(Plus(a,Times(b,Power(x,n)),Times(c,Power(x,Times(C2,n)))),p),Power(x,CN2)),x),x,Times(d,Power(x,CN1)))),x),And(FreeQ(List(a,b,c,d,n,p),x),EqQ($s("n2"),Times(C2,n))))),
IIntegrate(2063,Integrate(Times(Power(Plus(a_,Times(c_DEFAULT,Power(Times(d_DEFAULT,Power(x_,CN1)),$p("n2",true))),Times(b_DEFAULT,Power(Times(d_DEFAULT,Power(x_,CN1)),n_))),p_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Negate(Power(d,Plus(m,C1))),Subst(Integrate(Times(Power(Plus(a,Times(b,Power(x,n)),Times(c,Power(x,Times(C2,n)))),p),Power(Power(x,Plus(m,C2)),CN1)),x),x,Times(d,Power(x,CN1)))),x),And(FreeQ(List(a,b,c,d,n,p),x),EqQ($s("n2"),Times(C2,n)),IntegerQ(m)))),
IIntegrate(2064,Integrate(Times(Power(Plus(a_,Times(c_DEFAULT,Power(Times(d_DEFAULT,Power(x_,CN1)),$p("n2",true))),Times(b_DEFAULT,Power(Times(d_DEFAULT,Power(x_,CN1)),n_))),p_),Power(Times(e_DEFAULT,x_),m_)),x_Symbol),
    Condition(Simp(Star(Times(CN1,d,Power(Times(e,x),m),Power(Times(d,Power(x,CN1)),m)),Subst(Integrate(Times(Power(Plus(a,Times(b,Power(x,n)),Times(c,Power(x,Times(C2,n)))),p),Power(Power(x,Plus(m,C2)),CN1)),x),x,Times(d,Power(x,CN1)))),x),And(FreeQ(List(a,b,c,d,e,m,n,p),x),EqQ($s("n2"),Times(C2,n)),Not(IntegerQ(m))))),
IIntegrate(2065,Integrate(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Times(d_DEFAULT,Power(x_,CN1)),n_)),Times(c_DEFAULT,Power(x_,$p("n2",true)))),p_),x_Symbol),
    Condition(Simp(Star(Negate(d),Subst(Integrate(Times(Power(Plus(a,Times(b,Power(x,n)),Times(c,Power(Power(d,Times(C2,n)),CN1),Power(x,Times(C2,n)))),p),Power(x,CN2)),x),x,Times(d,Power(x,CN1)))),x),And(FreeQ(List(a,b,c,d,n,p),x),EqQ($s("n2"),Times(CN2,n)),IntegerQ(Times(C2,n))))),
IIntegrate(2066,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(Times(d_DEFAULT,Power(x_,CN1)),n_)),Times(c_DEFAULT,Power(x_,$p("n2",true)))),p_)),x_Symbol),
    Condition(Simp(Star(Negate(Power(d,Plus(m,C1))),Subst(Integrate(Times(Power(Plus(a,Times(b,Power(x,n)),Times(c,Power(Power(d,Times(C2,n)),CN1),Power(x,Times(C2,n)))),p),Power(Power(x,Plus(m,C2)),CN1)),x),x,Times(d,Power(x,CN1)))),x),And(FreeQ(List(a,b,c,d,n,p),x),EqQ($s("n2"),Times(CN2,n)),IntegerQ(Times(C2,n)),IntegerQ(m)))),
IIntegrate(2067,Integrate(Times(Power(Times(e_DEFAULT,x_),m_),Power(Plus(a_,Times(b_DEFAULT,Power(Times(d_DEFAULT,Power(x_,CN1)),n_)),Times(c_DEFAULT,Power(x_,$p("n2",true)))),p_)),x_Symbol),
    Condition(Simp(Star(Times(CN1,d,Power(Times(e,x),m),Power(Times(d,Power(x,CN1)),m)),Subst(Integrate(Times(Power(Plus(a,Times(b,Power(x,n)),Times(c,Power(Power(d,Times(C2,n)),CN1),Power(x,Times(C2,n)))),p),Power(Power(x,Plus(m,C2)),CN1)),x),x,Times(d,Power(x,CN1)))),x),And(FreeQ(List(a,b,c,d,e,n,p),x),EqQ($s("n2"),Times(CN2,n)),Not(IntegerQ(m)),IntegerQ(Times(C2,n))))),
IIntegrate(2068,Integrate(Times(u_DEFAULT,Power(Times(e_DEFAULT,Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_DEFAULT))),r_DEFAULT)),p_),Power(Times(f_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_DEFAULT))),s_)),q_)),x_Symbol),
    Condition(Simp(Star(Times(Power(Times(e,Power(Plus(a,Times(b,Power(x,n))),r)),p),Power(Times(f,Power(Plus(c,Times(d,Power(x,n))),s)),q),Power(Times(Power(Plus(a,Times(b,Power(x,n))),Times(p,r)),Power(Plus(c,Times(d,Power(x,n))),Times(q,s))),CN1)),Integrate(Times(u,Power(Plus(a,Times(b,Power(x,n))),Times(p,r)),Power(Plus(c,Times(d,Power(x,n))),Times(q,s))),x)),x),FreeQ(List(a,b,c,d,e,f,n,p,q,r,s),x))),
//IIntegrate(2069,Integrate(Times(u_DEFAULT,$p("§px")),x_Symbol),
      // Condition(With(list(Set(a,Rt(Coeff($s("§px"),Sqr(x),C0),Expon($s("§px"),Sqr(x)))),Set(b,Rt(Coeff($s("§px"),Sqr(x),Expon($s("§px"),Sqr(x))),Expon($s("§px"),Sqr(x))))),Condition(Integrate(Times(u,Power(Plus(a,Times(b,Sqr(x))),Expon($s("§px"),Sqr(x)))),x),EqQ($s("§px"),Power(Plus(a,Times(b,Sqr(x))),Expon($s("§px"),Sqr(x)))))),And(PolyQ($s("§px"),Sqr(x)),GtQ(Expon($s("§px"),Sqr(x)),C1),NeQ(Coeff($s("§px"),Sqr(x),C0),C0),Not(MatchQ($s("§px"),Condition(Times(a_DEFAULT,Power(v_,Expon($s("§px"),Sqr(x)))),And(FreeQ(a,x),BinomialQ(v,x,C2)))))))),
      IIntegrate(2070, Integrate(Times(u_DEFAULT, Power($p("§px"), p_)), x_Symbol),
          Condition(With(list(Set(a, Rt(Coeff($s("§px"), Sqr(x), C0), Expon($s("§px"), Sqr(x)))),
              Set(b,
                  Rt(Coeff($s("§px"), Sqr(x), Expon($s("§px"), Sqr(x))),
                      Expon($s("§px"), Sqr(x))))),
              Condition(
                  Integrate(
                      Times(u, Power(Plus(a, Times(b, Sqr(x))),
                          Times(Expon($s("§px"), Sqr(x)), p))),
                      x),
                  EqQ($s("§px"), Power(Plus(a, Times(b, Sqr(x))), Expon($s("§px"), Sqr(x)))))),
              And(IntegerQ(p), PolyQ($s("§px"), Sqr(x)), GtQ(Expon($s("§px"),
                  Sqr(x)), C1), NeQ(Coeff($s("§px"), Sqr(x), C0),
                      C0)))),
      IIntegrate(2071, Integrate(Times(u_DEFAULT, Power($p("§px"), p_)), x_Symbol),
          Condition(
              With(
                  list(Set(a, Rt(Coeff($s("§px"), Sqr(x), C0), Expon($s("§px"), Sqr(x)))),
                      Set(b,
                          Rt(Coeff($s("§px"), Sqr(x), Expon($s("§px"), Sqr(x))),
                              Expon($s("§px"), Sqr(x))))),
                  Condition(
                      Simp(Star(
                          Times(
                              Power(Power(Plus(a, Times(b, Sqr(x))), Expon($s("§px"), Sqr(x))), p),
                              Power(Power(Plus(a, Times(b, Sqr(x))),
                                  Times(Expon($s("§px"), Sqr(x)), p)), CN1)),
                          Integrate(Times(u,
                              Power(Plus(a, Times(b, Sqr(x))), Times(Expon($s("§px"), Sqr(x)), p))),
                              x)),
                          x),
                      EqQ($s("§px"), Power(Plus(a, Times(b, Sqr(x))), Expon($s("§px"), Sqr(x)))))),
              And(Not(IntegerQ(p)), PolyQ($s("§px"), Sqr(x)), GtQ(Expon($s("§px"), Sqr(x)), C1),
                  NeQ(Coeff($s("§px"), Sqr(x), C0), C0)))),
IIntegrate(2072,Integrate(Power(u_,p_),x_Symbol),
    Condition(Integrate(Power(ExpandToSum(u,x),p),x),And(FreeQ(p,x),BinomialQ(u,x),Not(BinomialMatchQ(u,x))))),
IIntegrate(2073,Integrate(Times(Power(u_,p_DEFAULT),Power(Times(c_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Times(c,x),m),Power(ExpandToSum(u,x),p)),x),And(FreeQ(list(c,m,p),x),BinomialQ(u,x),Not(BinomialMatchQ(u,x))))),
IIntegrate(2074,Integrate(Times(Power(u_,p_DEFAULT),Power(v_,q_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(ExpandToSum(u,x),p),Power(ExpandToSum(v,x),q)),x),And(FreeQ(list(p,q),x),BinomialQ(list(u,v),x),EqQ(Subtract(BinomialDegree(u,x),BinomialDegree(v,x)),C0),Not(BinomialMatchQ(list(u,v),x))))),
IIntegrate(2075,Integrate(Times(Power(u_,p_DEFAULT),Power(v_,q_DEFAULT),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Times(e,x),m),Power(ExpandToSum(u,x),p),Power(ExpandToSum(v,x),q)),x),And(FreeQ(List(e,m,p,q),x),BinomialQ(list(u,v),x),EqQ(Subtract(BinomialDegree(u,x),BinomialDegree(v,x)),C0),Not(BinomialMatchQ(list(u,v),x))))),
IIntegrate(2076,Integrate(Times(Power(u_,m_DEFAULT),Power(v_,p_DEFAULT),Power(w_,q_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(ExpandToSum(u,x),m),Power(ExpandToSum(v,x),p),Power(ExpandToSum(w,x),q)),x),And(FreeQ(list(m,p,q),x),BinomialQ(list(u,v,w),x),EqQ(Subtract(BinomialDegree(u,x),BinomialDegree(v,x)),C0),EqQ(Subtract(BinomialDegree(u,x),BinomialDegree(w,x)),C0),Not(BinomialMatchQ(list(u,v,w),x))))),
IIntegrate(2077,Integrate(Times(Power(u_,p_DEFAULT),Power(v_,q_DEFAULT),Power(Times(g_DEFAULT,x_),m_DEFAULT),Power(z_,r_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Times(g,x),m),Power(ExpandToSum(u,x),p),Power(ExpandToSum(v,x),q),Power(ExpandToSum(z,x),r)),x),And(FreeQ(List(g,m,p,q,r),x),BinomialQ(list(u,v,z),x),EqQ(Subtract(BinomialDegree(u,x),BinomialDegree(v,x)),C0),EqQ(Subtract(BinomialDegree(u,x),BinomialDegree(z,x)),C0),Not(BinomialMatchQ(list(u,v,z),x))))),
IIntegrate(2078,Integrate(Power(u_,p_),x_Symbol),
    Condition(Integrate(Power(ExpandToSum(u,x),p),x),And(FreeQ(p,x),GeneralizedBinomialQ(u,x),Not(GeneralizedBinomialMatchQ(u,x))))),
IIntegrate(2079,Integrate(Times(Power(u_,p_DEFAULT),Power(Times(c_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Times(c,x),m),Power(ExpandToSum(u,x),p)),x),And(FreeQ(list(c,m,p),x),GeneralizedBinomialQ(u,x),Not(GeneralizedBinomialMatchQ(u,x))))),
IIntegrate(2080,Integrate(Power(u_,p_),x_Symbol),
    Condition(Integrate(Power(ExpandToSum(u,x),p),x),And(FreeQ(p,x),QuadraticQ(u,x),Not(QuadraticMatchQ(u,x)))))
  );
}
