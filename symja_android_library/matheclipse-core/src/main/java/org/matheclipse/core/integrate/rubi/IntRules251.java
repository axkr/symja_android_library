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
public class IntRules251 { 
  public static IAST RULES = List( 
IIntegrate(5021,Int(Times(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),Plus(d_DEFAULT,Times(Log(Plus(f_DEFAULT,Times(g_DEFAULT,Sqr(x_)))),e_DEFAULT)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(With(List(Set(u,IntHide(Times(Power(x,m),Plus(a,Times(b,ArcTan(Times(c,x))))),x))),Subtract(Dist(Plus(d,Times(e,Log(Plus(f,Times(g,Sqr(x)))))),u,x),Dist(Times(C2,e,g),Int(ExpandIntegrand(Times(x,u,Power(Plus(f,Times(g,Sqr(x))),CN1)),x),x),x))),And(FreeQ(List(a,b,c,d,e,f,g),x),IntegerQ(m),NeQ(m,CN1)))),
IIntegrate(5022,Int(Times(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),Plus(d_DEFAULT,Times(Log(Plus(f_DEFAULT,Times(g_DEFAULT,Sqr(x_)))),e_DEFAULT)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(With(List(Set(u,IntHide(Times(Power(x,m),Plus(a,Times(b,ArcCot(Times(c,x))))),x))),Subtract(Dist(Plus(d,Times(e,Log(Plus(f,Times(g,Sqr(x)))))),u,x),Dist(Times(C2,e,g),Int(ExpandIntegrand(Times(x,u,Power(Plus(f,Times(g,Sqr(x))),CN1)),x),x),x))),And(FreeQ(List(a,b,c,d,e,f,g),x),IntegerQ(m),NeQ(m,CN1)))),
IIntegrate(5023,Int(Times(Sqr(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT))),Plus(d_DEFAULT,Times(Log(Plus(f_,Times(g_DEFAULT,Sqr(x_)))),e_DEFAULT)),x_),x_Symbol),
    Condition(Plus(Simp(Times(Plus(f,Times(g,Sqr(x))),Plus(d,Times(e,Log(Plus(f,Times(g,Sqr(x)))))),Sqr(Plus(a,Times(b,ArcTan(Times(c,x))))),Power(Times(C2,g),CN1)),x),Negate(Dist(Times(b,Power(c,CN1)),Int(Times(Plus(d,Times(e,Log(Plus(f,Times(g,Sqr(x)))))),Plus(a,Times(b,ArcTan(Times(c,x))))),x),x)),Dist(Times(b,c,e),Int(Times(Sqr(x),Plus(a,Times(b,ArcTan(Times(c,x)))),Power(Plus(C1,Times(Sqr(c),Sqr(x))),CN1)),x),x),Negate(Simp(Times(C1D2,e,Sqr(x),Sqr(Plus(a,Times(b,ArcTan(Times(c,x)))))),x))),And(FreeQ(List(a,b,c,d,e,f,g),x),EqQ(g,Times(Sqr(c),f))))),
IIntegrate(5024,Int(Times(Sqr(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT))),Plus(d_DEFAULT,Times(Log(Plus(f_,Times(g_DEFAULT,Sqr(x_)))),e_DEFAULT)),x_),x_Symbol),
    Condition(Plus(Simp(Times(Plus(f,Times(g,Sqr(x))),Plus(d,Times(e,Log(Plus(f,Times(g,Sqr(x)))))),Sqr(Plus(a,Times(b,ArcCot(Times(c,x))))),Power(Times(C2,g),CN1)),x),Dist(Times(b,Power(c,CN1)),Int(Times(Plus(d,Times(e,Log(Plus(f,Times(g,Sqr(x)))))),Plus(a,Times(b,ArcCot(Times(c,x))))),x),x),Negate(Dist(Times(b,c,e),Int(Times(Sqr(x),Plus(a,Times(b,ArcCot(Times(c,x)))),Power(Plus(C1,Times(Sqr(c),Sqr(x))),CN1)),x),x)),Negate(Simp(Times(C1D2,e,Sqr(x),Sqr(Plus(a,Times(b,ArcCot(Times(c,x)))))),x))),And(FreeQ(List(a,b,c,d,e,f,g),x),EqQ(g,Times(Sqr(c),f))))),
IIntegrate(5025,Int(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Unintegrable(Times(u,Power(Plus(a,Times(b,ArcTan(Times(c,x)))),p)),x),And(FreeQ(List(a,b,c,p),x),Or(EqQ(u,C1),MatchQ(u,Condition(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x)),q_DEFAULT),FreeQ(List(d,e,q),x))),MatchQ(u,Condition(Times(Power(Times(f_DEFAULT,x),m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x)),q_DEFAULT)),FreeQ(List(d,e,f,m,q),x))),MatchQ(u,Condition(Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x))),q_DEFAULT),FreeQ(List(d,e,q),x))),MatchQ(u,Condition(Times(Power(Times(f_DEFAULT,x),m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x))),q_DEFAULT)),FreeQ(List(d,e,f,m,q),x))))))),
IIntegrate(5026,Int(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Unintegrable(Times(u,Power(Plus(a,Times(b,ArcCot(Times(c,x)))),p)),x),And(FreeQ(List(a,b,c,p),x),Or(EqQ(u,C1),MatchQ(u,Condition(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x)),q_DEFAULT),FreeQ(List(d,e,q),x))),MatchQ(u,Condition(Times(Power(Times(f_DEFAULT,x),m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x)),q_DEFAULT)),FreeQ(List(d,e,f,m,q),x))),MatchQ(u,Condition(Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x))),q_DEFAULT),FreeQ(List(d,e,q),x))),MatchQ(u,Condition(Times(Power(Times(f_DEFAULT,x),m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x))),q_DEFAULT)),FreeQ(List(d,e,f,m,q),x))))))),
IIntegrate(5027,Int(ArcTan(Times(c_DEFAULT,Power(x_,n_))),x_Symbol),
    Condition(Subtract(Simp(Times(x,ArcTan(Times(c,Power(x,n)))),x),Dist(Times(c,n),Int(Times(Power(x,n),Power(Plus(C1,Times(Sqr(c),Power(x,Times(C2,n)))),CN1)),x),x)),FreeQ(List(c,n),x))),
IIntegrate(5028,Int(ArcCot(Times(c_DEFAULT,Power(x_,n_))),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcCot(Times(c,Power(x,n)))),x),Dist(Times(c,n),Int(Times(Power(x,n),Power(Plus(C1,Times(Sqr(c),Power(x,Times(C2,n)))),CN1)),x),x)),FreeQ(List(c,n),x))),
IIntegrate(5029,Int(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Int(ExpandIntegrand(Power(Subtract(Plus(a,Times(C1D2,CI,b,Log(Subtract(C1,Times(CI,c,Power(x,n)))))),Times(C1D2,CI,b,Log(Plus(C1,Times(CI,c,Power(x,n)))))),p),x),x),And(FreeQ(List(a,b,c,n),x),IGtQ(p,C0),IntegerQ(n)))),
IIntegrate(5030,Int(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Int(ExpandIntegrand(Power(Subtract(Plus(a,Times(C1D2,CI,b,Log(Subtract(C1,Times(CI,Power(Times(Power(x,n),c),CN1)))))),Times(C1D2,CI,b,Log(Plus(C1,Times(CI,Power(Times(Power(x,n),c),CN1)))))),p),x),x),And(FreeQ(List(a,b,c,n),x),IGtQ(p,C0),IntegerQ(n)))),
IIntegrate(5031,Int(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_DEFAULT),Power(x_,CN1)),x_Symbol),
    Condition(Dist(Power(n,CN1),Subst(Int(Times(Power(Plus(a,Times(b,ArcTan(Times(c,x)))),p),Power(x,CN1)),x),x,Power(x,n)),x),And(FreeQ(List(a,b,c,n),x),IGtQ(p,C0)))),
IIntegrate(5032,Int(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_DEFAULT),Power(x_,CN1)),x_Symbol),
    Condition(Dist(Power(n,CN1),Subst(Int(Times(Power(Plus(a,Times(b,ArcCot(Times(c,x)))),p),Power(x,CN1)),x),x,Power(x,n)),x),And(FreeQ(List(a,b,c,n),x),IGtQ(p,C0)))),
IIntegrate(5033,Int(Times(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(d,x),Plus(m,C1)),Plus(a,Times(b,ArcTan(Times(c,Power(x,n))))),Power(Times(d,Plus(m,C1)),CN1)),x),Dist(Times(b,c,n,Power(Times(d,Plus(m,C1)),CN1)),Int(Times(Power(x,Subtract(n,C1)),Power(Times(d,x),Plus(m,C1)),Power(Plus(C1,Times(Sqr(c),Power(x,Times(C2,n)))),CN1)),x),x)),And(FreeQ(List(a,b,c,d,m,n),x),NeQ(m,CN1)))),
IIntegrate(5034,Int(Times(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Times(d,x),Plus(m,C1)),Plus(a,Times(b,ArcCot(Times(c,Power(x,n))))),Power(Times(d,Plus(m,C1)),CN1)),x),Dist(Times(b,c,n,Power(Times(d,Plus(m,C1)),CN1)),Int(Times(Power(x,Subtract(n,C1)),Power(Times(d,x),Plus(m,C1)),Power(Plus(C1,Times(Sqr(c),Power(x,Times(C2,n)))),CN1)),x),x)),And(FreeQ(List(a,b,c,d,m,n),x),NeQ(m,CN1)))),
IIntegrate(5035,Int(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Int(ExpandIntegrand(Times(Power(Times(d,x),m),Power(Subtract(Plus(a,Times(C1D2,CI,b,Log(Subtract(C1,Times(CI,c,Power(x,n)))))),Times(C1D2,CI,b,Log(Plus(C1,Times(CI,c,Power(x,n)))))),p)),x),x),And(FreeQ(List(a,b,c,d,m,n),x),IGtQ(p,C0),IntegerQ(m),IntegerQ(n)))),
IIntegrate(5036,Int(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Int(ExpandIntegrand(Times(Power(Times(d,x),m),Power(Subtract(Plus(a,Times(C1D2,CI,b,Log(Subtract(C1,Times(CI,Power(Times(Power(x,n),c),CN1)))))),Times(C1D2,CI,b,Log(Plus(C1,Times(CI,Power(Times(Power(x,n),c),CN1)))))),p)),x),x),And(FreeQ(List(a,b,c,d,m,n),x),IGtQ(p,C0),IntegerQ(m),IntegerQ(n)))),
IIntegrate(5037,Int(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Unintegrable(Times(u,Power(Plus(a,Times(b,ArcTan(Times(c,Power(x,n))))),p)),x),And(FreeQ(List(a,b,c,n,p),x),Or(EqQ(u,C1),MatchQ(u,Condition(Power(Times(d_DEFAULT,x),m_DEFAULT),FreeQ(List(d,m),x))))))),
IIntegrate(5038,Int(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Unintegrable(Times(u,Power(Plus(a,Times(b,ArcCot(Times(c,Power(x,n))))),p)),x),And(FreeQ(List(a,b,c,n,p),x),Or(EqQ(u,C1),MatchQ(u,Condition(Power(Times(d_DEFAULT,x),m_DEFAULT),FreeQ(List(d,m),x))))))),
IIntegrate(5039,Int(Power(Plus(a_DEFAULT,Times(ArcTan(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Dist(Power(d,CN1),Subst(Int(Power(Plus(a,Times(b,ArcTan(x))),p),x),x,Plus(c,Times(d,x))),x),And(FreeQ(List(a,b,c,d),x),IGtQ(p,C0)))),
IIntegrate(5040,Int(Power(Plus(a_DEFAULT,Times(ArcCot(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Dist(Power(d,CN1),Subst(Int(Power(Plus(a,Times(b,ArcCot(x))),p),x),x,Plus(c,Times(d,x))),x),And(FreeQ(List(a,b,c,d),x),IGtQ(p,C0))))
  );
}
