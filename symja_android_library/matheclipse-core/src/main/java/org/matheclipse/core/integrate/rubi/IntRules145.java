package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctions.*;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IAST;
import com.google.common.base.Supplier;

/** 
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class IntRules145 { 
  public static IAST RULES = List( 
IIntegrate(2901,Integrate(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(x_,n_))),p_DEFAULT))),b_DEFAULT)),q_),x_Symbol),
    Condition(With(list(Set(k,Denominator(n))),Simp(Dist(k,Subst(Integrate(Times(Power(x,Plus(k,CN1)),Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,Power(x,Times(k,n)))),p))))),q)),x),x,Power(x,Power(k,CN1))),x),x)),And(FreeQ(List(a,b,c,d,e,p,q),x),FractionQ(n)))),
IIntegrate(2902,Integrate(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(x_,n_))),p_DEFAULT))),b_DEFAULT)),q_),x_Symbol),
    Condition(Unintegrable(Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,Power(x,n))),p))))),q),x),FreeQ(List(a,b,c,d,e,n,p,q),x))),
IIntegrate(2903,Integrate(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(v_,p_DEFAULT))),b_DEFAULT)),q_DEFAULT),x_Symbol),
    Condition(Integrate(Power(Plus(a,Times(b,Log(Times(c,Power(ExpandToSum(v,x),p))))),q),x),And(FreeQ(List(a,b,c,p,q),x),BinomialQ(v,x),Not(BinomialMatchQ(v,x))))),
IIntegrate(2904,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(x_,n_))),p_DEFAULT))),b_DEFAULT)),q_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(n,CN1),Subst(Integrate(Times(Power(x,Plus(Simplify(Times(Plus(m,C1),Power(n,CN1))),CN1)),Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,x)),p))))),q)),x),x,Power(x,n)),x),x),And(FreeQ(List(a,b,c,d,e,m,n,p,q),x),IntegerQ(Simplify(Times(Plus(m,C1),Power(n,CN1)))),Or(GtQ(Times(Plus(m,C1),Power(n,CN1)),C0),IGtQ(q,C0)),Not(And(EqQ(q,C1),ILtQ(n,C0),IGtQ(m,C0)))))),
IIntegrate(2905,Integrate(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(x_,n_))),p_DEFAULT))),b_DEFAULT)),Power(Times(f_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(f,x),Plus(m,C1)),Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,Power(x,n))),p))))),Power(Times(f,Plus(m,C1)),CN1)),x),Simp(Dist(Times(b,e,n,p,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(x,Plus(n,CN1)),Power(Times(f,x),Plus(m,C1)),Power(Plus(d,Times(e,Power(x,n))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e,f,m,n,p),x),NeQ(m,CN1)))),
IIntegrate(2906,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(x_,n_))),p_DEFAULT))),b_DEFAULT)),q_DEFAULT),Power(Times(f_,x_),m_)),x_Symbol),
    Condition(Simp(Dist(Times(Power(Times(f,x),m),Power(Power(x,m),CN1)),Integrate(Times(Power(x,m),Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,Power(x,n))),p))))),q)),x),x),x),And(FreeQ(List(a,b,c,d,e,f,m,n,p,q),x),IntegerQ(Simplify(Times(Plus(m,C1),Power(n,CN1)))),Or(GtQ(Times(Plus(m,C1),Power(n,CN1)),C0),IGtQ(q,C0))))),
IIntegrate(2907,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(x_,n_))),p_DEFAULT))),b_DEFAULT)),q_),Power(Times(f_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(f,x),Plus(m,C1)),Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,Power(x,n))),p))))),q),Power(Times(f,Plus(m,C1)),CN1)),x),Simp(Dist(Times(b,e,n,p,q,Power(Times(Power(f,n),Plus(m,C1)),CN1)),Integrate(Times(Power(Times(f,x),Plus(m,n)),Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,Power(x,n))),p))))),Plus(q,CN1)),Power(Plus(d,Times(e,Power(x,n))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e,f,m,p),x),IGtQ(q,C1),IntegerQ(n),NeQ(m,CN1)))),
IIntegrate(2908,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(x_,n_))),p_DEFAULT))),b_DEFAULT)),q_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(k,Denominator(n))),Simp(Dist(k,Subst(Integrate(Times(Power(x,Plus(Times(k,Plus(m,C1)),CN1)),Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,Power(x,Times(k,n)))),p))))),q)),x),x,Power(x,Power(k,CN1))),x),x)),And(FreeQ(List(a,b,c,d,e,m,p,q),x),FractionQ(n)))),
IIntegrate(2909,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(x_,n_))),p_DEFAULT))),b_DEFAULT)),q_DEFAULT),Power(Times(f_,x_),m_)),x_Symbol),
    Condition(Simp(Dist(Times(Power(Times(f,x),m),Power(Power(x,m),CN1)),Integrate(Times(Power(x,m),Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,Power(x,n))),p))))),q)),x),x),x),And(FreeQ(List(a,b,c,d,e,f,m,p,q),x),FractionQ(n)))),
IIntegrate(2910,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(x_,n_))),p_DEFAULT))),b_DEFAULT)),q_DEFAULT),Power(Times(f_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(f,x),m),Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,Power(x,n))),p))))),q)),x),FreeQ(List(a,b,c,d,e,f,m,n,p,q),x))),
IIntegrate(2911,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(v_,p_DEFAULT))),b_DEFAULT)),q_DEFAULT),Power(Times(f_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Times(f,x),m),Power(Plus(a,Times(b,Log(Times(c,Power(ExpandToSum(v,x),p))))),q)),x),And(FreeQ(List(a,b,c,f,m,p,q),x),BinomialQ(v,x),Not(BinomialMatchQ(v,x))))),
IIntegrate(2912,Integrate(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(x_,n_))),p_DEFAULT))),b_DEFAULT)),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(Log(Plus(f,Times(g,x))),Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,Power(x,n))),p))))),Power(g,CN1)),x),Simp(Dist(Times(b,e,n,p,Power(g,CN1)),Integrate(Times(Power(x,Plus(n,CN1)),Log(Plus(f,Times(g,x))),Power(Plus(d,Times(e,Power(x,n))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e,f,g,n,p),x),RationalQ(n)))),
IIntegrate(2913,Integrate(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(x_,n_))),p_DEFAULT))),b_DEFAULT)),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),r_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(f,Times(g,x)),Plus(r,C1)),Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,Power(x,n))),p))))),Power(Times(g,Plus(r,C1)),CN1)),x),Simp(Dist(Times(b,e,n,p,Power(Times(g,Plus(r,C1)),CN1)),Integrate(Times(Power(x,Plus(n,CN1)),Power(Plus(f,Times(g,x)),Plus(r,C1)),Power(Plus(d,Times(e,Power(x,n))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e,f,g,n,p,r),x),Or(IGtQ(r,C0),RationalQ(n)),NeQ(r,CN1)))),
IIntegrate(2914,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(x_,n_))),p_DEFAULT))),b_DEFAULT)),q_DEFAULT),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),r_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(f,Times(g,x)),r),Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,Power(x,n))),p))))),q)),x),FreeQ(List(a,b,c,d,e,f,g,n,p,q,r),x))),
IIntegrate(2915,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(v_,p_DEFAULT))),b_DEFAULT)),q_DEFAULT),Power(u_,r_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(ExpandToSum(u,x),r),Power(Plus(a,Times(b,Log(Times(c,Power(ExpandToSum(v,x),p))))),q)),x),And(FreeQ(List(a,b,c,p,q,r),x),LinearQ(u,x),BinomialQ(v,x),Not(And(LinearMatchQ(u,x),BinomialMatchQ(v,x)))))),
IIntegrate(2916,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(x_,n_))),p_DEFAULT))),b_DEFAULT)),q_DEFAULT),Power(x_,m_DEFAULT),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),r_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,Power(x,n))),p))))),q),Times(Power(x,m),Power(Plus(f,Times(g,x)),r)),x),x),And(FreeQ(List(a,b,c,d,e,f,g,n,p,q),x),IntegerQ(m),IntegerQ(r)))),
IIntegrate(2917,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(x_,n_DEFAULT))),p_DEFAULT))),b_DEFAULT)),q_DEFAULT),Power(Times(h_DEFAULT,x_),m_),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),r_DEFAULT)),x_Symbol),
    Condition(With(list(Set(k,Denominator(m))),Simp(Dist(Times(k,Power(h,CN1)),Subst(Integrate(Times(Power(x,Plus(Times(k,Plus(m,C1)),CN1)),Power(Plus(f,Times(g,Power(x,k),Power(h,CN1))),r),Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,Power(x,Times(k,n)),Power(Power(h,n),CN1))),p))))),q)),x),x,Power(Times(h,x),Power(k,CN1))),x),x)),And(FreeQ(List(a,b,c,d,e,f,g,h,p,r),x),FractionQ(m),IntegerQ(n),IntegerQ(r)))),
IIntegrate(2918,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(x_,n_))),p_DEFAULT))),b_DEFAULT)),q_DEFAULT),Power(Times(h_DEFAULT,x_),m_DEFAULT),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),r_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(h,x),m),Power(Plus(f,Times(g,x)),r),Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,Power(x,n))),p))))),q)),x),FreeQ(List(a,b,c,d,e,f,g,h,m,n,p,q,r),x))),
IIntegrate(2919,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(v_,p_DEFAULT))),b_DEFAULT)),q_DEFAULT),Power(u_,r_DEFAULT),Power(Times(h_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Times(h,x),m),Power(ExpandToSum(u,x),r),Power(Plus(a,Times(b,Log(Times(c,Power(ExpandToSum(v,x),p))))),q)),x),And(FreeQ(List(a,b,c,h,m,p,q,r),x),LinearQ(u,x),BinomialQ(v,x),Not(And(LinearMatchQ(u,x),BinomialMatchQ(v,x)))))),
IIntegrate(2920,Integrate(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(x_,n_))),p_DEFAULT))),b_DEFAULT)),Power(Plus(f_,Times(g_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Power(Plus(f,Times(g,Sqr(x))),CN1),x))),Subtract(Simp(Times(u,Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,Power(x,n))),p)))))),x),Simp(Dist(Times(b,e,n,p),Integrate(Times(u,Power(x,Plus(n,CN1)),Power(Plus(d,Times(e,Power(x,n))),CN1)),x),x),x))),And(FreeQ(List(a,b,c,d,e,f,g,n,p),x),IntegerQ(n))))
  );
}
