package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;
import org.matheclipse.core.interfaces.IAST;

/** 
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class IntRules316 { 
  public static IAST RULES = List( 
IIntegrate(6321,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSech(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Negate(Dist(Power(Power(d,Plus(m,C1)),CN1),Subst(Integrate(Times(Power(Plus(a,Times(b,x)),p),Sech(x),Tanh(x),Power(Plus(Times(d,e),Times(CN1,c,f),Times(f,Sech(x))),m)),x),x,ArcSech(Plus(c,Times(d,x)))),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(p,C0),IntegerQ(m)))),
IIntegrate(6322,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCsch(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Negate(Dist(Power(Power(d,Plus(m,C1)),CN1),Subst(Integrate(Times(Power(Plus(a,Times(b,x)),p),Csch(x),Coth(x),Power(Plus(Times(d,e),Times(CN1,c,f),Times(f,Csch(x))),m)),x),x,ArcCsch(Plus(c,Times(d,x)))),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(p,C0),IntegerQ(m)))),
IIntegrate(6323,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSech(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Dist(Power(d,CN1),Subst(Integrate(Times(Power(Plus(Times(Subtract(Times(d,e),Times(c,f)),Power(d,CN1)),Times(f,x,Power(d,CN1))),m),Power(Plus(a,Times(b,ArcSech(x))),p)),x),x,Plus(c,Times(d,x))),x),And(FreeQ(List(a,b,c,d,e,f,m),x),IGtQ(p,C0)))),
IIntegrate(6324,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCsch(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Dist(Power(d,CN1),Subst(Integrate(Times(Power(Plus(Times(Subtract(Times(d,e),Times(c,f)),Power(d,CN1)),Times(f,x,Power(d,CN1))),m),Power(Plus(a,Times(b,ArcCsch(x))),p)),x),x,Plus(c,Times(d,x))),x),And(FreeQ(List(a,b,c,d,e,f,m),x),IGtQ(p,C0)))),
IIntegrate(6325,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSech(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(e,Times(f,x)),m),Power(Plus(a,Times(b,ArcSech(Plus(c,Times(d,x))))),p)),x),And(FreeQ(List(a,b,c,d,e,f,m,p),x),Not(IGtQ(p,C0))))),
IIntegrate(6326,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCsch(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(e,Times(f,x)),m),Power(Plus(a,Times(b,ArcCsch(Plus(c,Times(d,x))))),p)),x),And(FreeQ(List(a,b,c,d,e,f,m,p),x),Not(IGtQ(p,C0))))),
IIntegrate(6327,Integrate(Times(Power(ArcSech(Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))),CN1))),m_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Integrate(Times(u,Power(ArcCosh(Plus(Times(a,Power(c,CN1)),Times(b,Power(x,n),Power(c,CN1)))),m)),x),FreeQ(List(a,b,c,n,m),x))),
IIntegrate(6328,Integrate(Times(Power(ArcCsch(Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))),CN1))),m_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Integrate(Times(u,Power(ArcSinh(Plus(Times(a,Power(c,CN1)),Times(b,Power(x,n),Power(c,CN1)))),m)),x),FreeQ(List(a,b,c,n,m),x))),
IIntegrate(6329,Integrate(Exp(ArcSech(Times(a_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Simp(Times(x,Exp(ArcSech(Times(a,x)))),x),Dist(Power(a,CN1),Integrate(Times(C1,Sqrt(Times(Subtract(C1,Times(a,x)),Power(Plus(C1,Times(a,x)),CN1))),Power(Times(x,Subtract(C1,Times(a,x))),CN1)),x),x),Simp(Times(Log(x),Power(a,CN1)),x)),FreeQ(a,x))),
IIntegrate(6330,Integrate(Exp(ArcSech(Times(a_DEFAULT,Power(x_,p_)))),x_Symbol),
    Condition(Plus(Simp(Times(x,Exp(ArcSech(Times(a,Power(x,p))))),x),Dist(Times(p,Power(a,CN1)),Integrate(Power(Power(x,p),CN1),x),x),Dist(Times(p,Sqrt(Plus(C1,Times(a,Power(x,p)))),Sqrt(Power(Plus(C1,Times(a,Power(x,p))),CN1)),Power(a,CN1)),Integrate(Power(Times(Power(x,p),Sqrt(Plus(C1,Times(a,Power(x,p)))),Sqrt(Subtract(C1,Times(a,Power(x,p))))),CN1),x),x)),FreeQ(List(a,p),x))),
IIntegrate(6331,Integrate(Exp(ArcCsch(Times(a_DEFAULT,Power(x_,p_DEFAULT)))),x_Symbol),
    Condition(Plus(Dist(Power(a,CN1),Integrate(Power(Power(x,p),CN1),x),x),Integrate(Sqrt(Plus(C1,Power(Times(Sqr(a),Power(x,Times(C2,p))),CN1))),x)),FreeQ(List(a,p),x))),
IIntegrate(6332,Integrate(Exp(Times(ArcSech(u_),n_DEFAULT)),x_Symbol),
    Condition(Integrate(Power(Plus(Power(u,CN1),Sqrt(Times(Subtract(C1,u),Power(Plus(C1,u),CN1))),Times(C1,Sqrt(Times(Subtract(C1,u),Power(Plus(C1,u),CN1))),Power(u,CN1))),n),x),IntegerQ(n))),
IIntegrate(6333,Integrate(Exp(Times(ArcCsch(u_),n_DEFAULT)),x_Symbol),
    Condition(Integrate(Power(Plus(Power(u,CN1),Sqrt(Plus(C1,Power(u,CN2)))),n),x),IntegerQ(n))),
IIntegrate(6334,Integrate(Times(Exp(ArcSech(Times(a_DEFAULT,Power(x_,p_DEFAULT)))),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Negate(Simp(Power(Times(a,p,Power(x,p)),CN1),x)),Dist(Times(Sqrt(Plus(C1,Times(a,Power(x,p)))),Sqrt(Power(Plus(C1,Times(a,Power(x,p))),CN1)),Power(a,CN1)),Integrate(Times(Sqrt(Plus(C1,Times(a,Power(x,p)))),Sqrt(Subtract(C1,Times(a,Power(x,p)))),Power(Power(x,Plus(p,C1)),CN1)),x),x)),FreeQ(List(a,p),x))),
IIntegrate(6335,Integrate(Times(Exp(ArcSech(Times(a_DEFAULT,Power(x_,p_DEFAULT)))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),Exp(ArcSech(Times(a,Power(x,p)))),Power(Plus(m,C1),CN1)),x),Dist(Times(p,Power(Times(a,Plus(m,C1)),CN1)),Integrate(Power(x,Subtract(m,p)),x),x),Dist(Times(p,Sqrt(Plus(C1,Times(a,Power(x,p)))),Sqrt(Power(Plus(C1,Times(a,Power(x,p))),CN1)),Power(Times(a,Plus(m,C1)),CN1)),Integrate(Times(Power(x,Subtract(m,p)),Power(Times(Sqrt(Plus(C1,Times(a,Power(x,p)))),Sqrt(Subtract(C1,Times(a,Power(x,p))))),CN1)),x),x)),And(FreeQ(List(a,m,p),x),NeQ(m,CN1)))),
IIntegrate(6336,Integrate(Times(Exp(ArcCsch(Times(a_DEFAULT,Power(x_,p_DEFAULT)))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Dist(Power(a,CN1),Integrate(Power(x,Subtract(m,p)),x),x),Integrate(Times(Power(x,m),Sqrt(Plus(C1,Power(Times(Sqr(a),Power(x,Times(C2,p))),CN1)))),x)),FreeQ(List(a,m,p),x))),
IIntegrate(6337,Integrate(Times(Exp(Times(ArcSech(u_),n_DEFAULT)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(x,m),Power(Plus(Power(u,CN1),Sqrt(Times(Subtract(C1,u),Power(Plus(C1,u),CN1))),Times(C1,Sqrt(Times(Subtract(C1,u),Power(Plus(C1,u),CN1))),Power(u,CN1))),n)),x),And(FreeQ(m,x),IntegerQ(n)))),
IIntegrate(6338,Integrate(Times(Exp(Times(ArcCsch(u_),n_DEFAULT)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(x,m),Power(Plus(Power(u,CN1),Sqrt(Plus(C1,Power(u,CN2)))),n)),x),And(FreeQ(m,x),IntegerQ(n)))),
IIntegrate(6339,Integrate(Times(Exp(ArcSech(Times(c_DEFAULT,x_))),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Plus(Dist(Power(Times(a,c),CN1),Integrate(Times(Sqrt(Power(Plus(C1,Times(c,x)),CN1)),Power(Times(x,Sqrt(Subtract(C1,Times(c,x)))),CN1)),x),x),Dist(Power(c,CN1),Integrate(Power(Times(x,Plus(a,Times(b,Sqr(x)))),CN1),x),x)),And(FreeQ(List(a,b,c),x),EqQ(Plus(b,Times(a,Sqr(c))),C0)))),
IIntegrate(6340,Integrate(Times(Exp(ArcCsch(Times(c_DEFAULT,x_))),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Plus(Dist(Power(Times(a,Sqr(c)),CN1),Integrate(Power(Times(Sqr(x),Sqrt(Plus(C1,Power(Times(Sqr(c),Sqr(x)),CN1)))),CN1),x),x),Dist(Power(c,CN1),Integrate(Power(Times(x,Plus(a,Times(b,Sqr(x)))),CN1),x),x)),And(FreeQ(List(a,b,c),x),EqQ(Subtract(b,Times(a,Sqr(c))),C0))))
  );
}
