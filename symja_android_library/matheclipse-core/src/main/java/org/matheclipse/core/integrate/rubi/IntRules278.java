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
class IntRules278 { 
  public static IAST RULES = List( 
IIntegrate(5561,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Unintegrable(Times(u,Power(Plus(a,Times(b,ArcCot(Times(c,x)))),p)),x),And(FreeQ(List(a,b,c,p),x),Or(EqQ(u,C1),MatchQ(u,Condition(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x)),q_DEFAULT),FreeQ(list(d,e,q),x))),MatchQ(u,Condition(Times(Power(Times(f_DEFAULT,x),m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x)),q_DEFAULT)),FreeQ(List(d,e,f,m,q),x))),MatchQ(u,Condition(Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x))),q_DEFAULT),FreeQ(list(d,e,q),x))),MatchQ(u,Condition(Times(Power(Times(f_DEFAULT,x),m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x))),q_DEFAULT)),FreeQ(List(d,e,f,m,q),x))))))),
IIntegrate(5562,Integrate(Power(Plus(a_DEFAULT,Times(ArcTan(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Star(Power(d,CN1),Subst(Integrate(Power(Plus(a,Times(b,ArcTan(x))),p),x),x,Plus(c,Times(d,x)))),x),And(FreeQ(List(a,b,c,d),x),IGtQ(p,C0)))),
IIntegrate(5563,Integrate(Power(Plus(a_DEFAULT,Times(ArcCot(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Star(Power(d,CN1),Subst(Integrate(Power(Plus(a,Times(b,ArcCot(x))),p),x),x,Plus(c,Times(d,x)))),x),And(FreeQ(List(a,b,c,d),x),IGtQ(p,C0)))),
IIntegrate(5564,Integrate(Power(Plus(a_DEFAULT,Times(ArcTan(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_),x_Symbol),
    Condition(Unintegrable(Power(Plus(a,Times(b,ArcTan(Plus(c,Times(d,x))))),p),x),And(FreeQ(List(a,b,c,d,p),x),Not(IGtQ(p,C0))))),
IIntegrate(5565,Integrate(Power(Plus(a_DEFAULT,Times(ArcCot(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_),x_Symbol),
    Condition(Unintegrable(Power(Plus(a,Times(b,ArcCot(Plus(c,Times(d,x))))),p),x),And(FreeQ(List(a,b,c,d,p),x),Not(IGtQ(p,C0))))),
IIntegrate(5566,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(d,CN1),Subst(Integrate(Times(Power(Times(f,x,Power(d,CN1)),m),Power(Plus(a,Times(b,ArcTan(x))),p)),x),x,Plus(c,Times(d,x)))),x),And(FreeQ(List(a,b,c,d,e,f,m),x),EqQ(Subtract(Times(d,e),Times(c,f)),C0),IGtQ(p,C0)))),
IIntegrate(5567,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(d,CN1),Subst(Integrate(Times(Power(Times(f,x,Power(d,CN1)),m),Power(Plus(a,Times(b,ArcCot(x))),p)),x),x,Plus(c,Times(d,x)))),x),And(FreeQ(List(a,b,c,d,e,f,m),x),EqQ(Subtract(Times(d,e),Times(c,f)),C0),IGtQ(p,C0)))),
IIntegrate(5568,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Power(Plus(a,Times(b,ArcTan(Plus(c,Times(d,x))))),p),Power(Times(f,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,d,p,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Power(Plus(a,Times(b,ArcTan(Plus(c,Times(d,x))))),Subtract(p,C1)),Power(Plus(C1,Sqr(Plus(c,Times(d,x)))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(p,C0),ILtQ(m,CN1)))),
IIntegrate(5569,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Power(Plus(a,Times(b,ArcCot(Plus(c,Times(d,x))))),p),Power(Times(f,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,d,p,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Power(Plus(a,Times(b,ArcCot(Plus(c,Times(d,x))))),Subtract(p,C1)),Power(Plus(C1,Sqr(Plus(c,Times(d,x)))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(p,C0),ILtQ(m,CN1)))),
IIntegrate(5570,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(d,CN1),Subst(Integrate(Times(Power(Plus(Times(Subtract(Times(d,e),Times(c,f)),Power(d,CN1)),Times(f,x,Power(d,CN1))),m),Power(Plus(a,Times(b,ArcTan(x))),p)),x),x,Plus(c,Times(d,x)))),x),And(FreeQ(List(a,b,c,d,e,f,m,p),x),IGtQ(p,C0)))),
IIntegrate(5571,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(d,CN1),Subst(Integrate(Times(Power(Plus(Times(Subtract(Times(d,e),Times(c,f)),Power(d,CN1)),Times(f,x,Power(d,CN1))),m),Power(Plus(a,Times(b,ArcCot(x))),p)),x),x,Plus(c,Times(d,x)))),x),And(FreeQ(List(a,b,c,d,e,f,m,p),x),IGtQ(p,C0)))),
IIntegrate(5572,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(e,Times(f,x)),m),Power(Plus(a,Times(b,ArcTan(Plus(c,Times(d,x))))),p)),x),And(FreeQ(List(a,b,c,d,e,f,m,p),x),Not(IGtQ(p,C0))))),
IIntegrate(5573,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(e,Times(f,x)),m),Power(Plus(a,Times(b,ArcCot(Plus(c,Times(d,x))))),p)),x),And(FreeQ(List(a,b,c,d,e,f,m,p),x),Not(IGtQ(p,C0))))),
IIntegrate(5574,Integrate(Times(ArcTan(Plus(a_,Times(b_DEFAULT,x_))),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_DEFAULT))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Star(Times(C1D2,CI),Integrate(Times(Log(Subtract(Subtract(C1,Times(CI,a)),Times(CI,b,x))),Power(Plus(c,Times(d,Power(x,n))),CN1)),x)),x),Simp(Star(Times(C1D2,CI),Integrate(Times(Log(Plus(C1,Times(CI,a),Times(CI,b,x))),Power(Plus(c,Times(d,Power(x,n))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d),x),RationalQ(n)))),
IIntegrate(5575,Integrate(Times(ArcCot(Plus(a_,Times(b_DEFAULT,x_))),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_DEFAULT))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Star(Times(C1D2,CI),Integrate(Times(Log(Times(Plus(CNI,a,Times(b,x)),Power(Plus(a,Times(b,x)),CN1))),Power(Plus(c,Times(d,Power(x,n))),CN1)),x)),x),Simp(Star(Times(C1D2,CI),Integrate(Times(Log(Times(Plus(CI,a,Times(b,x)),Power(Plus(a,Times(b,x)),CN1))),Power(Plus(c,Times(d,Power(x,n))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d),x),RationalQ(n)))),
IIntegrate(5576,Integrate(Times(ArcTan(Plus(a_,Times(b_DEFAULT,x_))),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_))),CN1)),x_Symbol),
    Condition(Unintegrable(Times(ArcTan(Plus(a,Times(b,x))),Power(Plus(c,Times(d,Power(x,n))),CN1)),x),And(FreeQ(List(a,b,c,d,n),x),Not(RationalQ(n))))),
IIntegrate(5577,Integrate(Times(ArcCot(Plus(a_,Times(b_DEFAULT,x_))),Power(Plus(c_,Times(d_DEFAULT,Power(x_,n_))),CN1)),x_Symbol),
    Condition(Unintegrable(Times(ArcCot(Plus(a,Times(b,x))),Power(Plus(c,Times(d,Power(x,n))),CN1)),x),And(FreeQ(List(a,b,c,d,n),x),Not(RationalQ(n))))),
IIntegrate(5578,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(A_DEFAULT,Times(B_DEFAULT,x_),Times(C_DEFAULT,Sqr(x_))),q_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(d,CN1),Subst(Integrate(Times(Power(Plus(Times(CSymbol,Power(d,CN2)),Times(CSymbol,Power(d,CN2),Sqr(x))),q),Power(Plus(a,Times(b,ArcTan(x))),p)),x),x,Plus(c,Times(d,x)))),x),And(FreeQ(List(a,b,c,d,ASymbol,BSymbol,CSymbol,p,q),x),EqQ(Subtract(Times(BSymbol,Plus(C1,Sqr(c))),Times(C2,ASymbol,c,d)),C0),EqQ(Subtract(Times(C2,c,CSymbol),Times(BSymbol,d)),C0)))),
IIntegrate(5579,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCot(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(A_DEFAULT,Times(B_DEFAULT,x_),Times(C_DEFAULT,Sqr(x_))),q_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(d,CN1),Subst(Integrate(Times(Power(Plus(Times(CSymbol,Power(d,CN2)),Times(CSymbol,Power(d,CN2),Sqr(x))),q),Power(Plus(a,Times(b,ArcCot(x))),p)),x),x,Plus(c,Times(d,x)))),x),And(FreeQ(List(a,b,c,d,ASymbol,BSymbol,CSymbol,p,q),x),EqQ(Subtract(Times(BSymbol,Plus(C1,Sqr(c))),Times(C2,ASymbol,c,d)),C0),EqQ(Subtract(Times(C2,c,CSymbol),Times(BSymbol,d)),C0)))),
IIntegrate(5580,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Power(Plus(A_DEFAULT,Times(B_DEFAULT,x_),Times(C_DEFAULT,Sqr(x_))),q_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(d,CN1),Subst(Integrate(Times(Power(Plus(Times(Subtract(Times(d,e),Times(c,f)),Power(d,CN1)),Times(f,x,Power(d,CN1))),m),Power(Plus(Times(CSymbol,Power(d,CN2)),Times(CSymbol,Power(d,CN2),Sqr(x))),q),Power(Plus(a,Times(b,ArcTan(x))),p)),x),x,Plus(c,Times(d,x)))),x),And(FreeQ(List(a,b,c,d,e,f,ASymbol,BSymbol,CSymbol,m,p,q),x),EqQ(Subtract(Times(BSymbol,Plus(C1,Sqr(c))),Times(C2,ASymbol,c,d)),C0),EqQ(Subtract(Times(C2,c,CSymbol),Times(BSymbol,d)),C0))))
  );
}
