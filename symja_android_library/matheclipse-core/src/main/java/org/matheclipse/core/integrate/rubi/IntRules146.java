package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctions.*;
import org.matheclipse.core.interfaces.IAST;

/** 
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class IntRules146 { 
  public static IAST RULES = List( 
IIntegrate(2921,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(x_,n_))),p_DEFAULT))),b_DEFAULT)),q_DEFAULT),Power(Plus(f_,Times(g_DEFAULT,Power(x_,s_))),r_DEFAULT)),x_Symbol),
    Condition(With(list(Set(t,ExpandIntegrand(Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,Power(x,n))),p))))),q),Power(Plus(f,Times(g,Power(x,s))),r),x))),Condition(Integrate(t,x),SumQ(t))),And(FreeQ(List(a,b,c,d,e,f,g,n,p,q,r,s),x),IntegerQ(n),IGtQ(q,C0),IntegerQ(r),IntegerQ(s),Or(EqQ(q,C1),And(GtQ(r,C0),GtQ(s,C1)),And(LtQ(s,C0),LtQ(r,C0)))))),
IIntegrate(2922,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(x_,n_))),p_DEFAULT))),b_DEFAULT)),q_DEFAULT),Power(Plus(f_,Times(g_DEFAULT,Power(x_,s_))),r_DEFAULT)),x_Symbol),
    Condition(With(list(Set(k,Denominator(n))),Condition(Simp(Star(k,Subst(Integrate(Times(Power(x,Subtract(k,C1)),Power(Plus(f,Times(g,Power(x,Times(k,s)))),r),Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,Power(x,Times(k,n)))),p))))),q)),x),x,Power(x,Power(k,CN1)))),x),IntegerQ(Times(k,s)))),And(FreeQ(List(a,b,c,d,e,f,g,n,p,q,r,s),x),FractionQ(n)))),
IIntegrate(2923,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(x_,n_))),p_DEFAULT))),b_DEFAULT)),q_DEFAULT),Power(Plus(f_,Times(g_DEFAULT,Power(x_,s_))),r_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(f,Times(g,Power(x,s))),r),Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,Power(x,n))),p))))),q)),x),FreeQ(List(a,b,c,d,e,f,g,n,p,q,r,s),x))),
IIntegrate(2924,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(v_,p_DEFAULT))),b_DEFAULT)),q_DEFAULT),Power(u_,r_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(ExpandToSum(u,x),r),Power(Plus(a,Times(b,Log(Times(c,Power(ExpandToSum(v,x),p))))),q)),x),And(FreeQ(List(a,b,c,p,q,r),x),BinomialQ(list(u,v),x),Not(BinomialMatchQ(list(u,v),x))))),
IIntegrate(2925,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(x_,n_))),p_DEFAULT))),b_DEFAULT)),q_DEFAULT),Power(x_,m_DEFAULT),Power(Plus(f_,Times(g_DEFAULT,Power(x_,s_))),r_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(n,CN1),Subst(Integrate(Times(Power(x,Subtract(Simplify(Times(Plus(m,C1),Power(n,CN1))),C1)),Power(Plus(f,Times(g,Power(x,Times(s,Power(n,CN1))))),r),Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,x)),p))))),q)),x),x,Power(x,n))),x),And(FreeQ(List(a,b,c,d,e,f,g,m,n,p,q,r,s),x),IntegerQ(r),IntegerQ(Times(s,Power(n,CN1))),IntegerQ(Simplify(Times(Plus(m,C1),Power(n,CN1)))),Or(GtQ(Times(Plus(m,C1),Power(n,CN1)),C0),IGtQ(q,C0))))),
IIntegrate(2926,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(x_,n_))),p_DEFAULT))),b_DEFAULT)),q_DEFAULT),Power(x_,m_DEFAULT),Power(Plus(f_,Times(g_DEFAULT,Power(x_,s_))),r_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,Power(x,n))),p))))),q),Times(Power(x,m),Power(Plus(f,Times(g,Power(x,s))),r)),x),x),And(FreeQ(List(a,b,c,d,e,f,g,m,n,p,q,r,s),x),IGtQ(q,C0),IntegerQ(m),IntegerQ(r),IntegerQ(s)))),
IIntegrate(2927,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(x_,n_))),p_DEFAULT))),b_DEFAULT)),q_DEFAULT),Power(x_,m_DEFAULT),Power(Plus(f_,Times(g_DEFAULT,Power(x_,s_))),r_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(n,CN1),Subst(Integrate(Times(Power(x,Subtract(Plus(m,Power(n,CN1)),C1)),Power(Plus(f,Times(g,Power(x,Times(s,Power(n,CN1))))),r),Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,x)),p))))),q)),x),x,Power(x,n))),x),And(FreeQ(List(a,b,c,d,e,f,g,m,n,p,q,r,s),x),FractionQ(n),IntegerQ(Power(n,CN1)),IntegerQ(Times(s,Power(n,CN1)))))),
IIntegrate(2928,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(x_,n_DEFAULT))),p_DEFAULT))),b_DEFAULT)),q_DEFAULT),Power(Times(h_DEFAULT,x_),m_),Power(Plus(f_DEFAULT,Times(g_DEFAULT,Power(x_,s_DEFAULT))),r_DEFAULT)),x_Symbol),
    Condition(With(list(Set(k,Denominator(m))),Simp(Star(Times(k,Power(h,CN1)),Subst(Integrate(Times(Power(x,Subtract(Times(k,Plus(m,C1)),C1)),Power(Plus(f,Times(g,Power(x,Times(k,s)),Power(Power(h,s),CN1))),r),Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,Power(x,Times(k,n)),Power(Power(h,n),CN1))),p))))),q)),x),x,Power(Times(h,x),Power(k,CN1)))),x)),And(FreeQ(List(a,b,c,d,e,f,g,h,p,r),x),FractionQ(m),IntegerQ(n),IntegerQ(s)))),
IIntegrate(2929,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(x_,n_))),p_DEFAULT))),b_DEFAULT)),q_DEFAULT),Power(Times(h_DEFAULT,x_),m_DEFAULT),Power(Plus(f_,Times(g_DEFAULT,Power(x_,s_))),r_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(h,x),m),Power(Plus(f,Times(g,Power(x,s))),r),Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,Power(x,n))),p))))),q)),x),FreeQ(List(a,b,c,d,e,f,g,h,m,n,p,q,r,s),x))),
IIntegrate(2930,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(v_,p_DEFAULT))),b_DEFAULT)),q_DEFAULT),Power(u_,r_DEFAULT),Power(Times(h_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Times(h,x),m),Power(ExpandToSum(u,x),r),Power(Plus(a,Times(b,Log(Times(c,Power(ExpandToSum(v,x),p))))),q)),x),And(FreeQ(List(a,b,c,h,m,p,q,r),x),BinomialQ(list(u,v),x),Not(BinomialMatchQ(list(u,v),x))))),
IIntegrate(2931,Integrate(Times(Power(Log(Times(f_DEFAULT,Power(x_,q_DEFAULT))),m_DEFAULT),Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(x_,n_))),p_DEFAULT))),b_DEFAULT)),Power(x_,CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Log(Times(f,Power(x,q))),Plus(m,C1)),Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,Power(x,n))),p))))),Power(Times(q,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,e,n,p,Power(Times(q,Plus(m,C1)),CN1)),Integrate(Times(Power(x,Subtract(n,C1)),Power(Log(Times(f,Power(x,q))),Plus(m,C1)),Power(Plus(d,Times(e,Power(x,n))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,f,m,n,p,q),x),NeQ(m,CN1)))),
IIntegrate(2932,Integrate(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(x_,n_))),p_DEFAULT))),b_DEFAULT)),Power($(F_,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Power(F(Times(f,x)),m),x))),Subtract(Simp(Star(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,Power(x,n))),p))))),u),x),Simp(Star(Times(b,e,n,p),Integrate(SimplifyIntegrand(Times(u,Power(x,Subtract(n,C1)),Power(Plus(d,Times(e,Power(x,n))),CN1)),x),x)),x))),And(FreeQ(List(a,b,c,d,e,f,p),x),MemberQ(List(ArcSin,ArcCos,ArcSinh,ArcCosh),FSymbol),IGtQ(m,C0),IGtQ(n,C1)))),
IIntegrate(2933,Integrate(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),n_))),p_DEFAULT))),b_DEFAULT)),q_DEFAULT),x_Symbol),
    Condition(Simp(Star(Power(g,CN1),Subst(Integrate(Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,Power(x,n))),p))))),q),x),x,Plus(f,Times(g,x)))),x),And(FreeQ(List(a,b,c,d,e,f,g,n,p),x),IGtQ(q,C0),Or(EqQ(q,C1),IntegerQ(n))))),
IIntegrate(2934,Integrate(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),n_))),p_DEFAULT))),b_DEFAULT)),q_DEFAULT),x_Symbol),
    Condition(Unintegrable(Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,Power(Plus(f,Times(g,x)),n))),p))))),q),x),FreeQ(List(a,b,c,d,e,f,g,n,p,q),x))),
IIntegrate(2935,Integrate(Power(Plus(A_DEFAULT,Times(Log(Times(e_DEFAULT,Power(Times(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),CN1)),n_DEFAULT))),B_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(a,Times(b,x)),Power(Plus(ASymbol,Times(BSymbol,Log(Times(e,Power(Times(Plus(a,Times(b,x)),Power(Plus(c,Times(d,x)),CN1)),n))))),p),Power(b,CN1)),x),Simp(Star(Times(BSymbol,n,p,Subtract(Times(b,c),Times(a,d)),Power(b,CN1)),Integrate(Times(Power(Plus(ASymbol,Times(BSymbol,Log(Times(e,Power(Times(Plus(a,Times(b,x)),Power(Plus(c,Times(d,x)),CN1)),n))))),Subtract(p,C1)),Power(Plus(c,Times(d,x)),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,ASymbol,BSymbol,n),x),NeQ(Subtract(Times(b,c),Times(a,d)),C0),IGtQ(p,C0)))),
IIntegrate(2936,Integrate(Power(Plus(A_DEFAULT,Times(Log(Times(e_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),n_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),$p("mn")))),B_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(a,Times(b,x)),Power(Plus(ASymbol,Times(BSymbol,Log(Times(e,Power(Plus(a,Times(b,x)),n),Power(Power(Plus(c,Times(d,x)),n),CN1))))),p),Power(b,CN1)),x),Simp(Star(Times(BSymbol,n,p,Subtract(Times(b,c),Times(a,d)),Power(b,CN1)),Integrate(Times(Power(Plus(ASymbol,Times(BSymbol,Log(Times(e,Power(Plus(a,Times(b,x)),n),Power(Power(Plus(c,Times(d,x)),n),CN1))))),Subtract(p,C1)),Power(Plus(c,Times(d,x)),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,ASymbol,BSymbol,n),x),EqQ(Plus(n,$s("mn")),C0),NeQ(Subtract(Times(b,c),Times(a,d)),C0),IGtQ(p,C0)))),
IIntegrate(2937,Integrate(Power(Plus(A_DEFAULT,Times(Log(Times(e_DEFAULT,Power(Times(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),CN1)),n_DEFAULT))),B_DEFAULT)),p_),x_Symbol),
    Condition(Unintegrable(Power(Plus(ASymbol,Times(BSymbol,Log(Times(e,Power(Times(Plus(a,Times(b,x)),Power(Plus(c,Times(d,x)),CN1)),n))))),p),x),FreeQ(List(a,b,c,d,e,ASymbol,BSymbol,n,p),x))),
IIntegrate(2938,Integrate(Power(Plus(A_DEFAULT,Times(Log(Times(e_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),n_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),$p("mn")))),B_DEFAULT)),p_),x_Symbol),
    Condition(Unintegrable(Power(Plus(ASymbol,Times(BSymbol,Log(Times(e,Power(Plus(a,Times(b,x)),n),Power(Power(Plus(c,Times(d,x)),n),CN1))))),p),x),And(FreeQ(List(a,b,c,d,e,ASymbol,BSymbol,n,p),x),EqQ(Plus(n,$s("mn")),C0)))),
IIntegrate(2939,Integrate(Power(Plus(A_DEFAULT,Times(Log(Times(e_DEFAULT,Power(Times(u_,Power(v_,CN1)),n_DEFAULT))),B_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Integrate(Power(Plus(ASymbol,Times(BSymbol,Log(Times(e,Power(Times(ExpandToSum(u,x),Power(ExpandToSum(v,x),CN1)),n))))),p),x),And(FreeQ(List(e,ASymbol,BSymbol,n,p),x),LinearQ(list(u,v),x),Not(LinearMatchQ(list(u,v),x))))),
IIntegrate(2940,Integrate(Power(Plus(A_DEFAULT,Times(Log(Times(e_DEFAULT,Power(u_,n_DEFAULT),Power(v_,$p("mn")))),B_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Integrate(Power(Plus(ASymbol,Times(BSymbol,Log(Times(e,Power(ExpandToSum(u,x),n),Power(Power(ExpandToSum(v,x),n),CN1))))),p),x),And(FreeQ(List(e,ASymbol,BSymbol,n,p),x),EqQ(Plus(n,$s("mn")),C0),IGtQ(n,C0),LinearQ(list(u,v),x),Not(LinearMatchQ(list(u,v),x)))))
  );
}
