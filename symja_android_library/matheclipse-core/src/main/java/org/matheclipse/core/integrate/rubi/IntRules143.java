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
class IntRules143 { 
  public static IAST RULES = List( 
IIntegrate(2861,Integrate(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,x_)),n_DEFAULT))),b_DEFAULT)),Power(x_,m_DEFAULT),Power(Plus(f_,Times(g_DEFAULT,Power(x_,r_DEFAULT))),q_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Times(Power(x,m),Power(Plus(f,Times(g,Power(x,r))),q)),x))),Condition(Subtract(Simp(Star(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,x)),n))))),u),x),Simp(Star(Times(b,e,n),Integrate(SimplifyIntegrand(Times(u,Power(Plus(d,Times(e,x)),CN1)),x),x)),x)),InverseFunctionFreeQ(u,x))),And(FreeQ(List(a,b,c,d,e,f,g,m,n,q,r),x),IntegerQ(m),IntegerQ(q),IntegerQ(r)))),
IIntegrate(2862,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,x_)),n_DEFAULT))),b_DEFAULT)),p_DEFAULT),Power(x_,m_DEFAULT),Power(Plus(f_DEFAULT,Times(g_DEFAULT,Power(x_,r_))),q_DEFAULT)),x_Symbol),
    Condition(With(list(Set(k,Denominator(r))),Simp(Star(k,Subst(Integrate(Times(Power(x,Subtract(Times(k,Plus(m,C1)),C1)),Power(Plus(f,Times(g,Power(x,Times(k,r)))),q),Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,Power(x,k))),n))))),p)),x),x,Power(x,Power(k,CN1)))),x)),And(FreeQ(List(a,b,c,d,e,f,g,n,p,q),x),FractionQ(r),IGtQ(p,C0),IntegerQ(m)))),
IIntegrate(2863,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,x_)),n_DEFAULT))),b_DEFAULT)),p_DEFAULT),Power(Times(h_DEFAULT,x_),m_DEFAULT),Power(Plus(f_,Times(g_DEFAULT,Power(x_,r_DEFAULT))),q_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,x)),n))))),p),Times(Power(Times(h,x),m),Power(Plus(f,Times(g,Power(x,r))),q)),x),x),And(FreeQ(List(a,b,c,d,e,f,g,h,m,n,p,q,r),x),IntegerQ(m),IntegerQ(q)))),
IIntegrate(2864,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,x_)),n_DEFAULT))),b_DEFAULT)),p_DEFAULT),$p("§polyx")),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Times($s("§polyx"),Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,x)),n))))),p)),x),x),And(FreeQ(List(a,b,c,d,e,n,p),x),PolynomialQ($s("§polyx"),x)))),
IIntegrate(2865,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,x_)),n_DEFAULT))),b_DEFAULT)),p_DEFAULT),$p("§rfx")),x_Symbol),
    Condition(With(list(Set(u,ExpandIntegrand(Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,x)),n))))),p),$s("§rfx"),x))),Condition(Integrate(u,x),SumQ(u))),And(FreeQ(List(a,b,c,d,e,n),x),RationalFunctionQ($s("§rfx"),x),IntegerQ(p)))),
IIntegrate(2866,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,x_)),n_DEFAULT))),b_DEFAULT)),p_DEFAULT),$p("§rfx")),x_Symbol),
    Condition(With(list(Set(u,ExpandIntegrand(Times($s("§rfx"),Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,x)),n))))),p)),x))),Condition(Integrate(u,x),SumQ(u))),And(FreeQ(List(a,b,c,d,e,n),x),RationalFunctionQ($s("§rfx"),x),IntegerQ(p)))),
IIntegrate(2867,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,x_)),n_DEFAULT))),b_DEFAULT)),p_DEFAULT),$p("§afx")),x_Symbol),
    Condition(Unintegrable(Times($s("§afx"),Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,x)),n))))),p)),x),And(FreeQ(List(a,b,c,d,e,n,p),x),AlgebraicFunctionQ($s("§afx"),x,True)))),
IIntegrate(2868,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(v_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),Power(u_,q_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(ExpandToSum(u,x),q),Power(Plus(a,Times(b,Log(Times(c,Power(ExpandToSum(v,x),n))))),p)),x),And(FreeQ(List(a,b,c,n,p,q),x),BinomialQ(u,x),LinearQ(v,x),Not(And(BinomialMatchQ(u,x),LinearMatchQ(v,x)))))),
IIntegrate(2869,Integrate(Times(Log(Times(f_DEFAULT,Power(x_,m_DEFAULT))),Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,x_)),n_DEFAULT))),b_DEFAULT))),x_Symbol),
    Condition(Plus(Simp(Times(CN1,x,Subtract(m,Log(Times(f,Power(x,m)))),Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,x)),n)))))),x),Negate(Simp(Star(Times(b,e,n),Integrate(Times(x,Log(Times(f,Power(x,m))),Power(Plus(d,Times(e,x)),CN1)),x)),x)),Simp(Star(Times(b,e,m,n),Integrate(Times(x,Power(Plus(d,Times(e,x)),CN1)),x)),x)),FreeQ(List(a,b,c,d,e,f,m,n),x))),
IIntegrate(2870,Integrate(Times(Log(Times(f_DEFAULT,Power(x_,m_DEFAULT))),Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,x_)),n_DEFAULT))),b_DEFAULT)),p_)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,x)),n))))),p),x))),Subtract(Simp(Star(Log(Times(f,Power(x,m))),u),x),Simp(Star(m,Integrate(Star(Power(x,CN1),u),x)),x))),And(FreeQ(List(a,b,c,d,e,f,m,n),x),IGtQ(p,C1)))),
IIntegrate(2871,Integrate(Times(Log(Times(f_DEFAULT,Power(x_,m_DEFAULT))),Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,x_)),n_DEFAULT))),b_DEFAULT)),p_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Log(Times(f,Power(x,m))),Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,x)),n))))),p)),x),FreeQ(List(a,b,c,d,e,f,m,n,p),x))),
IIntegrate(2872,Integrate(Times(Log(Times(f_DEFAULT,Power(x_,m_DEFAULT))),Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,x_)),n_DEFAULT))),b_DEFAULT)),Power(x_,CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(Sqr(Log(Times(f,Power(x,m)))),Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,x)),n))))),Power(Times(C2,m),CN1)),x),Simp(Star(Times(b,e,n,Power(Times(C2,m),CN1)),Integrate(Times(Sqr(Log(Times(f,Power(x,m)))),Power(Plus(d,Times(e,x)),CN1)),x)),x)),FreeQ(List(a,b,c,d,e,f,m,n),x))),
IIntegrate(2873,Integrate(Times(Log(Times(f_DEFAULT,Power(x_,m_DEFAULT))),Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,x_)),n_DEFAULT))),b_DEFAULT)),Power(Times(g_DEFAULT,x_),q_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Power(Times(g,Plus(q,C1)),CN1),Subtract(Times(m,Power(Times(g,x),Plus(q,C1)),Power(Plus(q,C1),CN1)),Times(Power(Times(g,x),Plus(q,C1)),Log(Times(f,Power(x,m))))),Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,x)),n)))))),x),Negate(Simp(Star(Times(b,e,n,Power(Times(g,Plus(q,C1)),CN1)),Integrate(Times(Power(Times(g,x),Plus(q,C1)),Log(Times(f,Power(x,m))),Power(Plus(d,Times(e,x)),CN1)),x)),x)),Simp(Star(Times(b,e,m,n,Power(Times(g,Sqr(Plus(q,C1))),CN1)),Integrate(Times(Power(Times(g,x),Plus(q,C1)),Power(Plus(d,Times(e,x)),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,f,g,m,n,q),x),NeQ(q,CN1)))),
IIntegrate(2874,Integrate(Times(Log(Times(f_DEFAULT,Power(x_,m_DEFAULT))),Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,x_)),n_DEFAULT))),b_DEFAULT)),p_DEFAULT),Power(x_,CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(Sqr(Log(Times(f,Power(x,m)))),Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,x)),n))))),p),Power(Times(C2,m),CN1)),x),Simp(Star(Times(b,e,n,p,Power(Times(C2,m),CN1)),Integrate(Times(Sqr(Log(Times(f,Power(x,m)))),Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,x)),n))))),Subtract(p,C1)),Power(Plus(d,Times(e,x)),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,f,m,n),x),IGtQ(p,C0)))),
IIntegrate(2875,Integrate(Times(Log(Times(f_DEFAULT,Power(x_,m_DEFAULT))),Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,x_)),n_DEFAULT))),b_DEFAULT)),p_),Power(Times(g_DEFAULT,x_),q_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Times(Power(Times(g,x),q),Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,x)),n))))),p)),x))),Subtract(Simp(Star(Log(Times(f,Power(x,m))),u),x),Simp(Star(m,Integrate(Star(Power(x,CN1),u),x)),x))),And(FreeQ(List(a,b,c,d,e,f,g,m,n,q),x),IGtQ(p,C1),IGtQ(q,C0)))),
IIntegrate(2876,Integrate(Times(Log(Times(f_DEFAULT,Power(x_,m_DEFAULT))),Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,x_)),n_DEFAULT))),b_DEFAULT)),p_DEFAULT),Power(Times(g_DEFAULT,x_),q_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(g,x),q),Log(Times(f,Power(x,m))),Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,x)),n))))),p)),x),FreeQ(List(a,b,c,d,e,f,g,m,n,p,q),x))),
IIntegrate(2877,Integrate(Times(Log(Times(f_DEFAULT,Power(Plus(g_DEFAULT,Times(h_DEFAULT,x_)),m_DEFAULT))),Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,x_)),n_DEFAULT))),b_DEFAULT)),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(e,CN1),Subst(Integrate(Times(Log(Times(f,Power(Times(g,x,Power(d,CN1)),m))),Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),p)),x),x,Plus(d,Times(e,x)))),x),And(FreeQ(List(a,b,c,d,e,f,g,h,m,n,p),x),EqQ(Subtract(Times(e,f),Times(d,g)),C0)))),
IIntegrate(2878,Integrate(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,x_)),n_DEFAULT))),b_DEFAULT)),Plus(f_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,x_)),n_DEFAULT))),g_DEFAULT))),x_Symbol),
    Condition(Subtract(Simp(Times(x,Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,x)),n))))),Plus(f,Times(g,Log(Times(c,Power(Plus(d,Times(e,x)),n)))))),x),Simp(Star(Times(e,n),Integrate(Times(x,Plus(Times(b,f),Times(a,g),Times(C2,b,g,Log(Times(c,Power(Plus(d,Times(e,x)),n))))),Power(Plus(d,Times(e,x)),CN1)),x)),x)),FreeQ(List(a,b,c,d,e,f,g,n),x))),
IIntegrate(2879,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,x_)),n_DEFAULT))),b_DEFAULT)),p_DEFAULT),Plus(f_DEFAULT,Times(Log(Times(h_DEFAULT,Power(Plus(i_DEFAULT,Times(j_DEFAULT,x_)),m_DEFAULT))),g_DEFAULT))),x_Symbol),
    Condition(Plus(Simp(Times(x,Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,x)),n))))),p),Plus(f,Times(g,Log(Times(h,Power(Plus(i,Times(j,x)),m)))))),x),Negate(Simp(Star(Times(g,j,m),Integrate(Times(x,Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,x)),n))))),p),Power(Plus(i,Times(j,x)),CN1)),x)),x)),Negate(Simp(Star(Times(b,e,n,p),Integrate(Times(x,Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,x)),n))))),Subtract(p,C1)),Plus(f,Times(g,Log(Times(h,Power(Plus(i,Times(j,x)),m))))),Power(Plus(d,Times(e,x)),CN1)),x)),x))),And(FreeQ(List(a,b,c,d,e,f,g,h,i,j,m,n),x),IGtQ(p,C0)))),
IIntegrate(2880,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,x_)),n_DEFAULT))),b_DEFAULT)),p_DEFAULT),Power(Plus(f_DEFAULT,Times(Log(Times(h_DEFAULT,Power(Plus(i_DEFAULT,Times(j_DEFAULT,x_)),m_DEFAULT))),g_DEFAULT)),q_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,x)),n))))),p),Power(Plus(f,Times(g,Log(Times(h,Power(Plus(i,Times(j,x)),m))))),q)),x),FreeQ(List(a,b,c,d,e,f,g,h,i,j,m,n,p),x)))
  );
}
