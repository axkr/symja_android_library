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
class IntRules141 { 
  public static IAST RULES = List( 
IIntegrate(2821,Integrate(Times(Log(Times(d_DEFAULT,Plus(e_,Times(f_DEFAULT,Power(x_,m_DEFAULT))))),Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,PolyLog(C2,Times(CN1,d,f,Power(x,m))),Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),p),Power(m,CN1)),x),Simp(Star(Times(b,n,p,Power(m,CN1)),Integrate(Times(PolyLog(C2,Times(CN1,d,f,Power(x,m))),Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),Subtract(p,C1)),Power(x,CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,f,m,n),x),IGtQ(p,C0),EqQ(Times(d,e),C1)))),
IIntegrate(2822,Integrate(Times(Log(Times(d_DEFAULT,Power(Plus(e_,Times(f_DEFAULT,Power(x_,m_DEFAULT))),r_DEFAULT))),Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),Power(x_,CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(Log(Times(d,Power(Plus(e,Times(f,Power(x,m))),r))),Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),Plus(p,C1)),Power(Times(b,n,Plus(p,C1)),CN1)),x),Simp(Star(Times(f,m,r,Power(Times(b,n,Plus(p,C1)),CN1)),Integrate(Times(Power(x,Subtract(m,C1)),Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),Plus(p,C1)),Power(Plus(e,Times(f,Power(x,m))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,f,r,m,n),x),IGtQ(p,C0),NeQ(Times(d,e),C1)))),
IIntegrate(2823,Integrate(Times(Log(Times(d_DEFAULT,Power(Plus(e_,Times(f_DEFAULT,Power(x_,m_DEFAULT))),r_DEFAULT))),Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),Power(Times(g_DEFAULT,x_),q_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Times(Power(Times(g,x),q),Log(Times(d,Power(Plus(e,Times(f,Power(x,m))),r)))),x))),Subtract(Simp(Star(Plus(a,Times(b,Log(Times(c,Power(x,n))))),u),x),Simp(Star(Times(b,n),Integrate(Star(Power(x,CN1),u),x)),x))),And(FreeQ(List(a,b,c,d,e,f,g,r,m,n,q),x),Or(IntegerQ(Times(Plus(q,C1),Power(m,CN1))),And(RationalQ(m),RationalQ(q))),NeQ(q,CN1)))),
IIntegrate(2824,Integrate(Times(Log(Times(d_DEFAULT,Plus(e_,Times(f_DEFAULT,Power(x_,m_DEFAULT))))),Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),Power(Times(g_DEFAULT,x_),q_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Times(Power(Times(g,x),q),Log(Times(d,Plus(e,Times(f,Power(x,m)))))),x))),Subtract(Simp(Star(Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),p),u),x),Simp(Star(Times(b,n,p),Integrate(Star(Times(Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),Subtract(p,C1)),Power(x,CN1)),u),x)),x))),And(FreeQ(List(a,b,c,d,e,f,g,m,n,q),x),IGtQ(p,C0),RationalQ(m),RationalQ(q),NeQ(q,CN1),Or(EqQ(p,C1),And(FractionQ(m),IntegerQ(Times(Plus(q,C1),Power(m,CN1)))),And(IGtQ(q,C0),IntegerQ(Times(Plus(q,C1),Power(m,CN1))),EqQ(Times(d,e),C1)))))),
IIntegrate(2825,Integrate(Times(Log(Times(d_DEFAULT,Power(Plus(e_,Times(f_DEFAULT,Power(x_,m_DEFAULT))),r_DEFAULT))),Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),Power(Times(g_DEFAULT,x_),q_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Times(Power(Times(g,x),q),Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),p)),x))),Subtract(Simp(Star(Log(Times(d,Power(Plus(e,Times(f,Power(x,m))),r))),u),x),Simp(Star(Times(f,m,r),Integrate(Star(Times(Power(x,Subtract(m,C1)),Power(Plus(e,Times(f,Power(x,m))),CN1)),u),x)),x))),And(FreeQ(List(a,b,c,d,e,f,g,r,m,n,q),x),IGtQ(p,C0),RationalQ(m),RationalQ(q)))),
IIntegrate(2826,Integrate(Times(Log(Times(d_DEFAULT,Power(Plus(e_,Times(f_DEFAULT,Power(x_,m_DEFAULT))),r_DEFAULT))),Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),Power(Times(g_DEFAULT,x_),q_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(g,x),q),Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),p),Log(Times(d,Power(Plus(e,Times(f,Power(x,m))),r)))),x),FreeQ(List(a,b,c,d,e,f,g,r,m,n,p,q),x))),
IIntegrate(2827,Integrate(Times(Log(Times(d_DEFAULT,Power(u_,r_DEFAULT))),Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),Power(Times(g_DEFAULT,x_),q_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Times(g,x),q),Log(Times(d,Power(ExpandToSum(u,x),r))),Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),p)),x),And(FreeQ(List(a,b,c,d,g,r,n,p,q),x),BinomialQ(u,x),Not(BinomialMatchQ(u,x))))),
IIntegrate(2828,Integrate(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),PolyLog(k_,Times(e_DEFAULT,Power(x_,q_DEFAULT)))),x_Symbol),
    Condition(Plus(Simp(Times(CN1,b,n,x,PolyLog(k,Times(e,Power(x,q)))),x),Simp(Times(x,PolyLog(k,Times(e,Power(x,q))),Plus(a,Times(b,Log(Times(c,Power(x,n)))))),x),Negate(Simp(Star(q,Integrate(Times(PolyLog(Subtract(k,C1),Times(e,Power(x,q))),Plus(a,Times(b,Log(Times(c,Power(x,n)))))),x)),x)),Simp(Star(Times(b,n,q),Integrate(PolyLog(Subtract(k,C1),Times(e,Power(x,q))),x)),x)),And(FreeQ(List(a,b,c,e,n,q),x),IGtQ(k,C0)))),
IIntegrate(2829,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),PolyLog(k_,Times(e_DEFAULT,Power(x_,q_DEFAULT)))),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),p),PolyLog(k,Times(e,Power(x,q)))),x),FreeQ(List(a,b,c,e,n,p,q),x))),
IIntegrate(2830,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),Power(x_,CN1),PolyLog(k_,Times(e_DEFAULT,Power(x_,q_DEFAULT)))),x_Symbol),
    Condition(Subtract(Simp(Times(PolyLog(Plus(k,C1),Times(e,Power(x,q))),Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),p),Power(q,CN1)),x),Simp(Star(Times(b,n,p,Power(q,CN1)),Integrate(Times(PolyLog(Plus(k,C1),Times(e,Power(x,q))),Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),Subtract(p,C1)),Power(x,CN1)),x)),x)),And(FreeQ(List(a,b,c,e,k,n,q),x),GtQ(p,C0)))),
IIntegrate(2831,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),Power(x_,CN1),PolyLog(k_,Times(e_DEFAULT,Power(x_,q_DEFAULT)))),x_Symbol),
    Condition(Subtract(Simp(Times(PolyLog(k,Times(e,Power(x,q))),Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),Plus(p,C1)),Power(Times(b,n,Plus(p,C1)),CN1)),x),Simp(Star(Times(q,Power(Times(b,n,Plus(p,C1)),CN1)),Integrate(Times(PolyLog(Subtract(k,C1),Times(e,Power(x,q))),Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),Plus(p,C1)),Power(x,CN1)),x)),x)),And(FreeQ(List(a,b,c,e,k,n,q),x),LtQ(p,CN1)))),
IIntegrate(2832,Integrate(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),Power(Times(d_DEFAULT,x_),m_DEFAULT),PolyLog(k_,Times(e_DEFAULT,Power(x_,q_DEFAULT)))),x_Symbol),
    Condition(Plus(Simp(Times(CN1,b,n,Power(Times(d,x),Plus(m,C1)),PolyLog(k,Times(e,Power(x,q))),Power(Times(d,Sqr(Plus(m,C1))),CN1)),x),Simp(Times(Power(Times(d,x),Plus(m,C1)),PolyLog(k,Times(e,Power(x,q))),Plus(a,Times(b,Log(Times(c,Power(x,n))))),Power(Times(d,Plus(m,C1)),CN1)),x),Negate(Simp(Star(Times(q,Power(Plus(m,C1),CN1)),Integrate(Times(Power(Times(d,x),m),PolyLog(Subtract(k,C1),Times(e,Power(x,q))),Plus(a,Times(b,Log(Times(c,Power(x,n)))))),x)),x)),Simp(Star(Times(b,n,q,Power(Plus(m,C1),CN2)),Integrate(Times(Power(Times(d,x),m),PolyLog(Subtract(k,C1),Times(e,Power(x,q)))),x)),x)),And(FreeQ(List(a,b,c,d,e,m,n,q),x),IGtQ(k,C0)))),
IIntegrate(2833,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),Power(Times(d_DEFAULT,x_),m_DEFAULT),PolyLog(k_,Times(e_DEFAULT,Power(x_,q_DEFAULT)))),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(d,x),m),Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),p),PolyLog(k,Times(e,Power(x,q)))),x),FreeQ(List(a,b,c,d,e,m,n,p,q),x))),
IIntegrate(2834,Integrate(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),$p("§px",true),Power($(F_,Times(d_DEFAULT,Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Times($s("§px"),Power(F(Times(d,Plus(e,Times(f,x)))),m)),x))),Subtract(Simp(Star(Plus(a,Times(b,Log(Times(c,Power(x,n))))),u),x),Simp(Star(Times(b,n),Integrate(Star(Power(x,CN1),u),x)),x))),And(FreeQ(List(a,b,c,d,e,f,n),x),PolynomialQ($s("§px"),x),IGtQ(m,C0),MemberQ(List(ArcSin,ArcCos,ArcSinh,ArcCosh),FSymbol)))),
IIntegrate(2835,Integrate(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),$p("§px",true),$(F_,Times(d_DEFAULT,Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),x_Symbol),
    Condition(With(list(Set(u,IntHide(Times($s("§px"),F(Times(d,Plus(e,Times(f,x))))),x))),Subtract(Simp(Star(Plus(a,Times(b,Log(Times(c,Power(x,n))))),u),x),Simp(Star(Times(b,n),Integrate(Star(Power(x,CN1),u),x)),x))),And(FreeQ(List(a,b,c,d,e,f,n),x),PolynomialQ($s("§px"),x),MemberQ(List(ArcTan,ArcCot,ArcTanh,ArcCoth),FSymbol)))),
IIntegrate(2836,Integrate(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,x_)),n_DEFAULT))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Star(Power(e,CN1),Subst(Integrate(Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),p),x),x,Plus(d,Times(e,x)))),x),FreeQ(List(a,b,c,d,e,n,p),x))),
IIntegrate(2837,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,x_)),n_DEFAULT))),b_DEFAULT)),p_DEFAULT),Power(Plus(f_,Times(g_DEFAULT,x_)),q_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(e,CN1),Subst(Integrate(Times(Power(Times(f,x,Power(d,CN1)),q),Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),p)),x),x,Plus(d,Times(e,x)))),x),And(FreeQ(List(a,b,c,d,e,f,g,n,p,q),x),EqQ(Subtract(Times(e,f),Times(d,g)),C0)))),
IIntegrate(2838,Integrate(Times(Log(Times(c_DEFAULT,Plus(d_,Times(e_DEFAULT,Power(x_,n_DEFAULT))))),Power(x_,CN1)),x_Symbol),
    Condition(Simp(Times(CN1,PolyLog(C2,Times(CN1,c,e,Power(x,n))),Power(n,CN1)),x),And(FreeQ(List(c,d,e,n),x),EqQ(Times(c,d),C1)))),
IIntegrate(2839,Integrate(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Plus(d_,Times(e_DEFAULT,x_)))),b_DEFAULT)),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Simp(Times(Plus(a,Times(b,Log(Times(c,d)))),Log(x)),x),Simp(Star(b,Integrate(Times(Log(Plus(C1,Times(e,x,Power(d,CN1)))),Power(x,CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e),x),GtQ(Times(c,d),C0)))),
IIntegrate(2840,Integrate(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Plus(d_,Times(e_DEFAULT,x_)))),b_DEFAULT)),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Simp(Star(Power(g,CN1),Subst(Integrate(Times(Plus(a,Times(b,Log(Plus(C1,Times(c,e,x,Power(g,CN1)))))),Power(x,CN1)),x),x,Plus(f,Times(g,x)))),x),And(FreeQ(List(a,b,c,d,e,f,g),x),NeQ(Subtract(Times(e,f),Times(d,g)),C0),EqQ(Plus(g,Times(c,Subtract(Times(e,f),Times(d,g)))),C0))))
  );
}
