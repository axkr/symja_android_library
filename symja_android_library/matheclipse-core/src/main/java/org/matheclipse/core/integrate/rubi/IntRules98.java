package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctions.*;
import org.matheclipse.core.interfaces.IAST;

/** 
 * IndefiniteIntegrationRules from the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi -
 * rule-based integrator</a>.
 *  
 */
public class IntRules98 { 
  public static IAST RULES = List( 
IIntegrate(2451,Int(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(x_,n_))),p_DEFAULT))),b_DEFAULT)),q_),x_Symbol),
    Condition(With(List(Set(k,Denominator(n))),Dist(k,Subst(Int(Times(Power(x,Subtract(k,C1)),Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,Power(x,Times(k,n)))),p))))),q)),x),x,Power(x,Power(k,CN1))),x)),And(FreeQ(List(a,b,c,d,e,p,q),x),FractionQ(n)))),
IIntegrate(2452,Int(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(x_,n_))),p_DEFAULT))),b_DEFAULT)),q_),x_Symbol),
    Condition(Unintegrable(Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,Power(x,n))),p))))),q),x),FreeQ(List(a,b,c,d,e,n,p,q),x))),
IIntegrate(2453,Int(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(v_,p_DEFAULT))),b_DEFAULT)),q_DEFAULT),x_Symbol),
    Condition(Int(Power(Plus(a,Times(b,Log(Times(c,Power(ExpandToSum(v,x),p))))),q),x),And(FreeQ(List(a,b,c,p,q),x),BinomialQ(v,x),Not(BinomialMatchQ(v,x))))),
IIntegrate(2454,Int(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(x_,n_))),p_DEFAULT))),b_DEFAULT)),q_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Dist(Power(n,CN1),Subst(Int(Times(Power(x,Subtract(Simplify(Times(Plus(m,C1),Power(n,CN1))),C1)),Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,x)),p))))),q)),x),x,Power(x,n)),x),And(FreeQ(List(a,b,c,d,e,m,n,p,q),x),IntegerQ(Simplify(Times(Plus(m,C1),Power(n,CN1)))),Or(GtQ(Times(Plus(m,C1),Power(n,CN1)),C0),IGtQ(q,C0)),Not(And(EqQ(q,C1),ILtQ(n,C0),IGtQ(m,C0)))))),
IIntegrate(2455,Int(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(x_,n_))),p_DEFAULT))),b_DEFAULT)),Power(Times(f_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(f,x),Plus(m,C1)),Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,Power(x,n))),p))))),Power(Times(f,Plus(m,C1)),CN1)),x),Dist(Times(b,e,n,p,Power(Times(f,Plus(m,C1)),CN1)),Int(Times(Power(x,Subtract(n,C1)),Power(Times(f,x),Plus(m,C1)),Power(Plus(d,Times(e,Power(x,n))),CN1)),x),x)),And(FreeQ(List(a,b,c,d,e,f,m,n,p),x),NeQ(m,CN1)))),
IIntegrate(2456,Int(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(x_,n_))),p_DEFAULT))),b_DEFAULT)),q_DEFAULT),Power(Times(f_,x_),m_)),x_Symbol),
    Condition(Dist(Times(Power(Times(f,x),m),Power(Power(x,m),CN1)),Int(Times(Power(x,m),Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,Power(x,n))),p))))),q)),x),x),And(FreeQ(List(a,b,c,d,e,f,m,n,p,q),x),IntegerQ(Simplify(Times(Plus(m,C1),Power(n,CN1)))),Or(GtQ(Times(Plus(m,C1),Power(n,CN1)),C0),IGtQ(q,C0))))),
IIntegrate(2457,Int(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(x_,n_))),p_DEFAULT))),b_DEFAULT)),q_),Power(Times(f_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(f,x),Plus(m,C1)),Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,Power(x,n))),p))))),q),Power(Times(f,Plus(m,C1)),CN1)),x),Dist(Times(b,e,n,p,q,Power(Times(Power(f,n),Plus(m,C1)),CN1)),Int(Times(Power(Times(f,x),Plus(m,n)),Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,Power(x,n))),p))))),Subtract(q,C1)),Power(Plus(d,Times(e,Power(x,n))),CN1)),x),x)),And(FreeQ(List(a,b,c,d,e,f,m,p),x),IGtQ(q,C1),IntegerQ(n),NeQ(m,CN1)))),
IIntegrate(2458,Int(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(x_,n_))),p_DEFAULT))),b_DEFAULT)),q_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(With(List(Set(k,Denominator(n))),Dist(k,Subst(Int(Times(Power(x,Subtract(Times(k,Plus(m,C1)),C1)),Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,Power(x,Times(k,n)))),p))))),q)),x),x,Power(x,Power(k,CN1))),x)),And(FreeQ(List(a,b,c,d,e,m,p,q),x),FractionQ(n)))),
IIntegrate(2459,Int(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(x_,n_))),p_DEFAULT))),b_DEFAULT)),q_DEFAULT),Power(Times(f_,x_),m_)),x_Symbol),
    Condition(Dist(Times(Power(Times(f,x),m),Power(Power(x,m),CN1)),Int(Times(Power(x,m),Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,Power(x,n))),p))))),q)),x),x),And(FreeQ(List(a,b,c,d,e,f,m,p,q),x),FractionQ(n)))),
IIntegrate(2460,Int(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(x_,n_))),p_DEFAULT))),b_DEFAULT)),q_DEFAULT),Power(Times(f_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(f,x),m),Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,Power(x,n))),p))))),q)),x),FreeQ(List(a,b,c,d,e,f,m,n,p,q),x))),
IIntegrate(2461,Int(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(v_,p_DEFAULT))),b_DEFAULT)),q_DEFAULT),Power(Times(f_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(Times(f,x),m),Power(Plus(a,Times(b,Log(Times(c,Power(ExpandToSum(v,x),p))))),q)),x),And(FreeQ(List(a,b,c,f,m,p,q),x),BinomialQ(v,x),Not(BinomialMatchQ(v,x))))),
IIntegrate(2462,Int(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(x_,n_))),p_DEFAULT))),b_DEFAULT)),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(Log(Plus(f,Times(g,x))),Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,Power(x,n))),p))))),Power(g,CN1)),x),Dist(Times(b,e,n,p,Power(g,CN1)),Int(Times(Power(x,Subtract(n,C1)),Log(Plus(f,Times(g,x))),Power(Plus(d,Times(e,Power(x,n))),CN1)),x),x)),And(FreeQ(List(a,b,c,d,e,f,g,n,p),x),RationalQ(n)))),
IIntegrate(2463,Int(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(x_,n_))),p_DEFAULT))),b_DEFAULT)),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),r_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(f,Times(g,x)),Plus(r,C1)),Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,Power(x,n))),p))))),Power(Times(g,Plus(r,C1)),CN1)),x),Dist(Times(b,e,n,p,Power(Times(g,Plus(r,C1)),CN1)),Int(Times(Power(x,Subtract(n,C1)),Power(Plus(f,Times(g,x)),Plus(r,C1)),Power(Plus(d,Times(e,Power(x,n))),CN1)),x),x)),And(FreeQ(List(a,b,c,d,e,f,g,n,p,r),x),Or(IGtQ(r,C0),RationalQ(n)),NeQ(r,CN1)))),
IIntegrate(2464,Int(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(x_,n_))),p_DEFAULT))),b_DEFAULT)),q_DEFAULT),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),r_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(f,Times(g,x)),r),Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,Power(x,n))),p))))),q)),x),FreeQ(List(a,b,c,d,e,f,g,n,p,q,r),x))),
IIntegrate(2465,Int(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(v_,p_DEFAULT))),b_DEFAULT)),q_DEFAULT),Power(u_,r_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(ExpandToSum(u,x),r),Power(Plus(a,Times(b,Log(Times(c,Power(ExpandToSum(v,x),p))))),q)),x),And(FreeQ(List(a,b,c,p,q,r),x),LinearQ(u,x),BinomialQ(v,x),Not(And(LinearMatchQ(u,x),BinomialMatchQ(v,x)))))),
IIntegrate(2466,Int(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(x_,n_))),p_DEFAULT))),b_DEFAULT)),q_DEFAULT),Power(x_,m_DEFAULT),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),r_DEFAULT)),x_Symbol),
    Condition(Int(ExpandIntegrand(Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,Power(x,n))),p))))),q),Times(Power(x,m),Power(Plus(f,Times(g,x)),r)),x),x),And(FreeQ(List(a,b,c,d,e,f,g,n,p,q),x),IntegerQ(m),IntegerQ(r)))),
IIntegrate(2467,Int(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(x_,n_DEFAULT))),p_DEFAULT))),b_DEFAULT)),q_DEFAULT),Power(Times(h_DEFAULT,x_),m_),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),r_DEFAULT)),x_Symbol),
    Condition(With(List(Set(k,Denominator(m))),Dist(Times(k,Power(h,CN1)),Subst(Int(Times(Power(x,Subtract(Times(k,Plus(m,C1)),C1)),Power(Plus(f,Times(g,Power(x,k),Power(h,CN1))),r),Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,Power(x,Times(k,n)),Power(Power(h,n),CN1))),p))))),q)),x),x,Power(Times(h,x),Power(k,CN1))),x)),And(FreeQ(List(a,b,c,d,e,f,g,h,p,r),x),FractionQ(m),IntegerQ(n),IntegerQ(r)))),
IIntegrate(2468,Int(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(x_,n_))),p_DEFAULT))),b_DEFAULT)),q_DEFAULT),Power(Times(h_DEFAULT,x_),m_DEFAULT),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),r_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(h,x),m),Power(Plus(f,Times(g,x)),r),Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,Power(x,n))),p))))),q)),x),FreeQ(List(a,b,c,d,e,f,g,h,m,n,p,q,r),x))),
IIntegrate(2469,Int(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(v_,p_DEFAULT))),b_DEFAULT)),q_DEFAULT),Power(u_,r_DEFAULT),Power(Times(h_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(Times(h,x),m),Power(ExpandToSum(u,x),r),Power(Plus(a,Times(b,Log(Times(c,Power(ExpandToSum(v,x),p))))),q)),x),And(FreeQ(List(a,b,c,h,m,p,q,r),x),LinearQ(u,x),BinomialQ(v,x),Not(And(LinearMatchQ(u,x),BinomialMatchQ(v,x)))))),
IIntegrate(2470,Int(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(x_,n_))),p_DEFAULT))),b_DEFAULT)),Power(Plus(f_,Times(g_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(With(List(Set(u,IntHide(Power(Plus(f,Times(g,Sqr(x))),CN1),x))),Subtract(Simp(Times(u,Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,Power(x,n))),p)))))),x),Dist(Times(b,e,n,p),Int(Times(u,Power(x,Subtract(n,C1)),Power(Plus(d,Times(e,Power(x,n))),CN1)),x),x))),And(FreeQ(List(a,b,c,d,e,f,g,n,p),x),IntegerQ(n)))),
IIntegrate(2471,Int(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(x_,n_))),p_DEFAULT))),b_DEFAULT)),q_DEFAULT),Power(Plus(f_,Times(g_DEFAULT,Power(x_,s_))),r_DEFAULT)),x_Symbol),
    Condition(With(List(Set(t,ExpandIntegrand(Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,Power(x,n))),p))))),q),Power(Plus(f,Times(g,Power(x,s))),r),x))),Condition(Int(t,x),SumQ(t))),And(FreeQ(List(a,b,c,d,e,f,g,n,p,q,r,s),x),IntegerQ(n),IGtQ(q,C0),IntegerQ(r),IntegerQ(s),Or(EqQ(q,C1),And(GtQ(r,C0),GtQ(s,C1)),And(LtQ(s,C0),LtQ(r,C0)))))),
IIntegrate(2472,Int(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(x_,n_))),p_DEFAULT))),b_DEFAULT)),q_DEFAULT),Power(Plus(f_,Times(g_DEFAULT,Power(x_,s_))),r_DEFAULT)),x_Symbol),
    Condition(With(List(Set(k,Denominator(n))),Condition(Dist(k,Subst(Int(Times(Power(x,Subtract(k,C1)),Power(Plus(f,Times(g,Power(x,Times(k,s)))),r),Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,Power(x,Times(k,n)))),p))))),q)),x),x,Power(x,Power(k,CN1))),x),IntegerQ(Times(k,s)))),And(FreeQ(List(a,b,c,d,e,f,g,n,p,q,r,s),x),FractionQ(n)))),
IIntegrate(2473,Int(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(x_,n_))),p_DEFAULT))),b_DEFAULT)),q_DEFAULT),Power(Plus(f_,Times(g_DEFAULT,Power(x_,s_))),r_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(f,Times(g,Power(x,s))),r),Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,Power(x,n))),p))))),q)),x),FreeQ(List(a,b,c,d,e,f,g,n,p,q,r,s),x))),
IIntegrate(2474,Int(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(v_,p_DEFAULT))),b_DEFAULT)),q_DEFAULT),Power(u_,r_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(ExpandToSum(u,x),r),Power(Plus(a,Times(b,Log(Times(c,Power(ExpandToSum(v,x),p))))),q)),x),And(FreeQ(List(a,b,c,p,q,r),x),BinomialQ(List(u,v),x),Not(BinomialMatchQ(List(u,v),x))))),
IIntegrate(2475,Int(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(x_,n_))),p_DEFAULT))),b_DEFAULT)),q_DEFAULT),Power(x_,m_DEFAULT),Power(Plus(f_,Times(g_DEFAULT,Power(x_,s_))),r_DEFAULT)),x_Symbol),
    Condition(Dist(Power(n,CN1),Subst(Int(Times(Power(x,Subtract(Simplify(Times(Plus(m,C1),Power(n,CN1))),C1)),Power(Plus(f,Times(g,Power(x,Times(s,Power(n,CN1))))),r),Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,x)),p))))),q)),x),x,Power(x,n)),x),And(FreeQ(List(a,b,c,d,e,f,g,m,n,p,q,r,s),x),IntegerQ(r),IntegerQ(Times(s,Power(n,CN1))),IntegerQ(Simplify(Times(Plus(m,C1),Power(n,CN1)))),Or(GtQ(Times(Plus(m,C1),Power(n,CN1)),C0),IGtQ(q,C0)))))
  );
}
