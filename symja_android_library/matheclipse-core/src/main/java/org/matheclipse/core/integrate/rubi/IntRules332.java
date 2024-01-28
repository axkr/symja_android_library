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
class IntRules332 { 
  public static IAST RULES = List( 
IIntegrate(6641,Integrate(Times(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,x_)),b_DEFAULT)),Plus(Times(Log(Plus(f_DEFAULT,Times(g_DEFAULT,Sqr(x_)))),e_DEFAULT),d_),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Simp(Star(d,Integrate(Times(Plus(a,Times(b,ArcTanh(Times(c,x)))),Power(x,CN1)),x)),x),Simp(Star(e,Integrate(Times(Log(Plus(f,Times(g,Sqr(x)))),Plus(a,Times(b,ArcTanh(Times(c,x)))),Power(x,CN1)),x)),x)),FreeQ(List(a,b,c,d,e,f,g),x))),
IIntegrate(6642,Integrate(Times(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,x_)),b_DEFAULT)),Plus(Times(Log(Plus(f_DEFAULT,Times(g_DEFAULT,Sqr(x_)))),e_DEFAULT),d_),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Simp(Star(d,Integrate(Times(Plus(a,Times(b,ArcCoth(Times(c,x)))),Power(x,CN1)),x)),x),Simp(Star(e,Integrate(Times(Log(Plus(f,Times(g,Sqr(x)))),Plus(a,Times(b,ArcCoth(Times(c,x)))),Power(x,CN1)),x)),x)),FreeQ(List(a,b,c,d,e,f,g),x))),
IIntegrate(6643,Integrate(Times(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,x_)),b_DEFAULT)),Plus(d_DEFAULT,Times(Log(Plus(f_DEFAULT,Times(g_DEFAULT,Sqr(x_)))),e_DEFAULT)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),Plus(d,Times(e,Log(Plus(f,Times(g,Sqr(x)))))),Plus(a,Times(b,ArcTanh(Times(c,x)))),Power(Plus(m,C1),CN1)),x),Negate(Simp(Star(Times(b,c,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,C1)),Plus(d,Times(e,Log(Plus(f,Times(g,Sqr(x)))))),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1)),x)),x)),Negate(Simp(Star(Times(C2,e,g,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,C2)),Plus(a,Times(b,ArcTanh(Times(c,x)))),Power(Plus(f,Times(g,Sqr(x))),CN1)),x)),x))),And(FreeQ(List(a,b,c,d,e,f,g),x),ILtQ(Times(C1D2,m),C0)))),
IIntegrate(6644,Integrate(Times(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,x_)),b_DEFAULT)),Plus(d_DEFAULT,Times(Log(Plus(f_DEFAULT,Times(g_DEFAULT,Sqr(x_)))),e_DEFAULT)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),Plus(d,Times(e,Log(Plus(f,Times(g,Sqr(x)))))),Plus(a,Times(b,ArcCoth(Times(c,x)))),Power(Plus(m,C1),CN1)),x),Negate(Simp(Star(Times(b,c,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,C1)),Plus(d,Times(e,Log(Plus(f,Times(g,Sqr(x)))))),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1)),x)),x)),Negate(Simp(Star(Times(C2,e,g,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,C2)),Plus(a,Times(b,ArcCoth(Times(c,x)))),Power(Plus(f,Times(g,Sqr(x))),CN1)),x)),x))),And(FreeQ(List(a,b,c,d,e,f,g),x),ILtQ(Times(C1D2,m),C0)))),
IIntegrate(6645,Integrate(Times(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,x_)),b_DEFAULT)),Plus(d_DEFAULT,Times(Log(Plus(f_DEFAULT,Times(g_DEFAULT,Sqr(x_)))),e_DEFAULT)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Times(Power(x,m),Plus(d,Times(e,Log(Plus(f,Times(g,Sqr(x))))))),x))),Subtract(Simp(Star(Plus(a,Times(b,ArcTanh(Times(c,x)))),u),x),Simp(Star(Times(b,c),Integrate(ExpandIntegrand(Times(u,Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1)),x),x)),x))),And(FreeQ(List(a,b,c,d,e,f,g),x),IGtQ(Times(C1D2,Plus(m,C1)),C0)))),
IIntegrate(6646,Integrate(Times(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,x_)),b_DEFAULT)),Plus(d_DEFAULT,Times(Log(Plus(f_DEFAULT,Times(g_DEFAULT,Sqr(x_)))),e_DEFAULT)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Times(Power(x,m),Plus(d,Times(e,Log(Plus(f,Times(g,Sqr(x))))))),x))),Subtract(Simp(Star(Plus(a,Times(b,ArcCoth(Times(c,x)))),u),x),Simp(Star(Times(b,c),Integrate(ExpandIntegrand(Times(u,Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1)),x),x)),x))),And(FreeQ(List(a,b,c,d,e,f,g),x),IGtQ(Times(C1D2,Plus(m,C1)),C0)))),
IIntegrate(6647,Integrate(Times(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,x_)),b_DEFAULT)),Plus(d_DEFAULT,Times(Log(Plus(f_DEFAULT,Times(g_DEFAULT,Sqr(x_)))),e_DEFAULT)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Times(Power(x,m),Plus(a,Times(b,ArcTanh(Times(c,x))))),x))),Subtract(Simp(Star(Plus(d,Times(e,Log(Plus(f,Times(g,Sqr(x)))))),u),x),Simp(Star(Times(C2,e,g),Integrate(ExpandIntegrand(Times(x,u,Power(Plus(f,Times(g,Sqr(x))),CN1)),x),x)),x))),And(FreeQ(List(a,b,c,d,e,f,g),x),IntegerQ(m),NeQ(m,CN1)))),
IIntegrate(6648,Integrate(Times(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,x_)),b_DEFAULT)),Plus(d_DEFAULT,Times(Log(Plus(f_DEFAULT,Times(g_DEFAULT,Sqr(x_)))),e_DEFAULT)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Times(Power(x,m),Plus(a,Times(b,ArcCoth(Times(c,x))))),x))),Subtract(Simp(Star(Plus(d,Times(e,Log(Plus(f,Times(g,Sqr(x)))))),u),x),Simp(Star(Times(C2,e,g),Integrate(ExpandIntegrand(Times(x,u,Power(Plus(f,Times(g,Sqr(x))),CN1)),x),x)),x))),And(FreeQ(List(a,b,c,d,e,f,g),x),IntegerQ(m),NeQ(m,CN1)))),
IIntegrate(6649,Integrate(Times(Sqr(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,x_)),b_DEFAULT))),Plus(d_DEFAULT,Times(Log(Plus(f_,Times(g_DEFAULT,Sqr(x_)))),e_DEFAULT)),x_),x_Symbol),
    Condition(Plus(Simp(Times(Plus(f,Times(g,Sqr(x))),Plus(d,Times(e,Log(Plus(f,Times(g,Sqr(x)))))),Sqr(Plus(a,Times(b,ArcTanh(Times(c,x))))),Power(Times(C2,g),CN1)),x),Negate(Simp(Times(e,Sqr(x),C1D2,Sqr(Plus(a,Times(b,ArcTanh(Times(c,x)))))),x)),Simp(Star(Times(b,Power(c,CN1)),Integrate(Times(Plus(d,Times(e,Log(Plus(f,Times(g,Sqr(x)))))),Plus(a,Times(b,ArcTanh(Times(c,x))))),x)),x),Simp(Star(Times(b,c,e),Integrate(Times(Sqr(x),Plus(a,Times(b,ArcTanh(Times(c,x)))),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,f,g),x),EqQ(Plus(Times(Sqr(c),f),g),C0)))),
IIntegrate(6650,Integrate(Times(Sqr(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,x_)),b_DEFAULT))),Plus(d_DEFAULT,Times(Log(Plus(f_,Times(g_DEFAULT,Sqr(x_)))),e_DEFAULT)),x_),x_Symbol),
    Condition(Plus(Simp(Times(Plus(f,Times(g,Sqr(x))),Plus(d,Times(e,Log(Plus(f,Times(g,Sqr(x)))))),Sqr(Plus(a,Times(b,ArcCoth(Times(c,x))))),Power(Times(C2,g),CN1)),x),Negate(Simp(Times(e,Sqr(x),C1D2,Sqr(Plus(a,Times(b,ArcCoth(Times(c,x)))))),x)),Simp(Star(Times(b,Power(c,CN1)),Integrate(Times(Plus(d,Times(e,Log(Plus(f,Times(g,Sqr(x)))))),Plus(a,Times(b,ArcCoth(Times(c,x))))),x)),x),Simp(Star(Times(b,c,e),Integrate(Times(Sqr(x),Plus(a,Times(b,ArcCoth(Times(c,x)))),Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,f,g),x),EqQ(Plus(Times(Sqr(c),f),g),C0)))),
IIntegrate(6651,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Unintegrable(Times(u,Power(Plus(a,Times(b,ArcTanh(Times(c,x)))),p)),x),And(FreeQ(List(a,b,c,p),x),Or(EqQ(u,C1),MatchQ(u,Condition(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x)),q_DEFAULT),FreeQ(list(d,e,q),x))),MatchQ(u,Condition(Times(Power(Times(f_DEFAULT,x),m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x)),q_DEFAULT)),FreeQ(List(d,e,f,m,q),x))),MatchQ(u,Condition(Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x))),q_DEFAULT),FreeQ(list(d,e,q),x))),MatchQ(u,Condition(Times(Power(Times(f_DEFAULT,x),m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x))),q_DEFAULT)),FreeQ(List(d,e,f,m,q),x))))))),
IIntegrate(6652,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Unintegrable(Times(u,Power(Plus(a,Times(b,ArcCoth(Times(c,x)))),p)),x),And(FreeQ(List(a,b,c,p),x),Or(EqQ(u,C1),MatchQ(u,Condition(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x)),q_DEFAULT),FreeQ(list(d,e,q),x))),MatchQ(u,Condition(Times(Power(Times(f_DEFAULT,x),m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x)),q_DEFAULT)),FreeQ(List(d,e,f,m,q),x))),MatchQ(u,Condition(Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x))),q_DEFAULT),FreeQ(list(d,e,q),x))),MatchQ(u,Condition(Times(Power(Times(f_DEFAULT,x),m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x))),q_DEFAULT)),FreeQ(List(d,e,f,m,q),x))))))),
IIntegrate(6653,Integrate(Power(Plus(a_DEFAULT,Times(ArcTanh(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Star(Power(d,CN1),Subst(Integrate(Power(Plus(a,Times(b,ArcTanh(x))),p),x),x,Plus(c,Times(d,x)))),x),And(FreeQ(List(a,b,c,d),x),IGtQ(p,C0)))),
IIntegrate(6654,Integrate(Power(Plus(a_DEFAULT,Times(ArcCoth(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Star(Power(d,CN1),Subst(Integrate(Power(Plus(a,Times(b,ArcCoth(x))),p),x),x,Plus(c,Times(d,x)))),x),And(FreeQ(List(a,b,c,d),x),IGtQ(p,C0)))),
IIntegrate(6655,Integrate(Power(Plus(a_DEFAULT,Times(ArcTanh(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_),x_Symbol),
    Condition(Unintegrable(Power(Plus(a,Times(b,ArcTanh(Plus(c,Times(d,x))))),p),x),And(FreeQ(List(a,b,c,d,p),x),Not(IGtQ(p,C0))))),
IIntegrate(6656,Integrate(Power(Plus(a_DEFAULT,Times(ArcCoth(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_),x_Symbol),
    Condition(Unintegrable(Power(Plus(a,Times(b,ArcCoth(Plus(c,Times(d,x))))),p),x),And(FreeQ(List(a,b,c,d,p),x),Not(IGtQ(p,C0))))),
IIntegrate(6657,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTanh(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(d,CN1),Subst(Integrate(Times(Power(Times(f,x,Power(d,CN1)),m),Power(Plus(a,Times(b,ArcTanh(x))),p)),x),x,Plus(c,Times(d,x)))),x),And(FreeQ(List(a,b,c,d,e,f,m),x),EqQ(Subtract(Times(d,e),Times(c,f)),C0),IGtQ(p,C0)))),
IIntegrate(6658,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCoth(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(d,CN1),Subst(Integrate(Times(Power(Times(f,x,Power(d,CN1)),m),Power(Plus(a,Times(b,ArcCoth(x))),p)),x),x,Plus(c,Times(d,x)))),x),And(FreeQ(List(a,b,c,d,e,f,m),x),EqQ(Subtract(Times(d,e),Times(c,f)),C0),IGtQ(p,C0)))),
IIntegrate(6659,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTanh(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Power(Plus(a,Times(b,ArcTanh(Plus(c,Times(d,x))))),p),Power(Times(f,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,d,p,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Power(Plus(a,Times(b,ArcTanh(Plus(c,Times(d,x))))),Subtract(p,C1)),Power(Subtract(C1,Sqr(Plus(c,Times(d,x)))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(p,C0),ILtQ(m,CN1)))),
IIntegrate(6660,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCoth(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Power(Plus(a,Times(b,ArcCoth(Plus(c,Times(d,x))))),p),Power(Times(f,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,d,p,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Power(Plus(a,Times(b,ArcCoth(Plus(c,Times(d,x))))),Subtract(p,C1)),Power(Subtract(C1,Sqr(Plus(c,Times(d,x)))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(p,C0),ILtQ(m,CN1))))
  );
}
