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
class IntRules344 { 
  public static IAST RULES = List( 
IIntegrate(6881,Integrate(Power(Plus(a_DEFAULT,Times(ArcSech(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Dist(Power(d,CN1),Subst(Integrate(Power(Plus(a,Times(b,ArcSech(x))),p),x),x,Plus(c,Times(d,x))),x),x),And(FreeQ(List(a,b,c,d),x),IGtQ(p,C0)))),
IIntegrate(6882,Integrate(Power(Plus(a_DEFAULT,Times(ArcCsch(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Dist(Power(d,CN1),Subst(Integrate(Power(Plus(a,Times(b,ArcCsch(x))),p),x),x,Plus(c,Times(d,x))),x),x),And(FreeQ(List(a,b,c,d),x),IGtQ(p,C0)))),
IIntegrate(6883,Integrate(Power(Plus(a_DEFAULT,Times(ArcSech(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_),x_Symbol),
    Condition(Unintegrable(Power(Plus(a,Times(b,ArcSech(Plus(c,Times(d,x))))),p),x),And(FreeQ(List(a,b,c,d,p),x),Not(IGtQ(p,C0))))),
IIntegrate(6884,Integrate(Power(Plus(a_DEFAULT,Times(ArcCsch(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_),x_Symbol),
    Condition(Unintegrable(Power(Plus(a,Times(b,ArcCsch(Plus(c,Times(d,x))))),p),x),And(FreeQ(List(a,b,c,d,p),x),Not(IGtQ(p,C0))))),
IIntegrate(6885,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSech(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(d,CN1),Subst(Integrate(Times(Power(Times(f,x,Power(d,CN1)),m),Power(Plus(a,Times(b,ArcSech(x))),p)),x),x,Plus(c,Times(d,x))),x),x),And(FreeQ(List(a,b,c,d,e,f,m),x),EqQ(Subtract(Times(d,e),Times(c,f)),C0),IGtQ(p,C0)))),
IIntegrate(6886,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCsch(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(d,CN1),Subst(Integrate(Times(Power(Times(f,x,Power(d,CN1)),m),Power(Plus(a,Times(b,ArcCsch(x))),p)),x),x,Plus(c,Times(d,x))),x),x),And(FreeQ(List(a,b,c,d,e,f,m),x),EqQ(Subtract(Times(d,e),Times(c,f)),C0),IGtQ(p,C0)))),
IIntegrate(6887,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSech(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Negate(Power(Power(d,Plus(m,C1)),CN1)),Subst(Integrate(Times(Power(Plus(a,Times(b,x)),p),Sech(x),Tanh(x),Power(Plus(Times(d,e),Times(CN1,c,f),Times(f,Sech(x))),m)),x),x,ArcSech(Plus(c,Times(d,x)))),x),x),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(p,C0),IntegerQ(m)))),
IIntegrate(6888,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCsch(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Negate(Power(Power(d,Plus(m,C1)),CN1)),Subst(Integrate(Times(Power(Plus(a,Times(b,x)),p),Csch(x),Coth(x),Power(Plus(Times(d,e),Times(CN1,c,f),Times(f,Csch(x))),m)),x),x,ArcCsch(Plus(c,Times(d,x)))),x),x),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(p,C0),IntegerQ(m)))),
IIntegrate(6889,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSech(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(d,CN1),Subst(Integrate(Times(Power(Plus(Times(Subtract(Times(d,e),Times(c,f)),Power(d,CN1)),Times(f,x,Power(d,CN1))),m),Power(Plus(a,Times(b,ArcSech(x))),p)),x),x,Plus(c,Times(d,x))),x),x),And(FreeQ(List(a,b,c,d,e,f,m),x),IGtQ(p,C0)))),
IIntegrate(6890,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCsch(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(d,CN1),Subst(Integrate(Times(Power(Plus(Times(Subtract(Times(d,e),Times(c,f)),Power(d,CN1)),Times(f,x,Power(d,CN1))),m),Power(Plus(a,Times(b,ArcCsch(x))),p)),x),x,Plus(c,Times(d,x))),x),x),And(FreeQ(List(a,b,c,d,e,f,m),x),IGtQ(p,C0)))),
IIntegrate(6891,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSech(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(e,Times(f,x)),m),Power(Plus(a,Times(b,ArcSech(Plus(c,Times(d,x))))),p)),x),And(FreeQ(List(a,b,c,d,e,f,m,p),x),Not(IGtQ(p,C0))))),
IIntegrate(6892,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCsch(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(e,Times(f,x)),m),Power(Plus(a,Times(b,ArcCsch(Plus(c,Times(d,x))))),p)),x),And(FreeQ(List(a,b,c,d,e,f,m,p),x),Not(IGtQ(p,C0))))),
IIntegrate(6893,Integrate(Times(Power(ArcSech(Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))),CN1))),m_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Integrate(Times(u,Power(ArcCosh(Plus(Times(a,Power(c,CN1)),Times(b,Power(x,n),Power(c,CN1)))),m)),x),FreeQ(List(a,b,c,n,m),x))),
IIntegrate(6894,Integrate(Times(Power(ArcCsch(Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))),CN1))),m_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Integrate(Times(u,Power(ArcSinh(Plus(Times(a,Power(c,CN1)),Times(b,Power(x,n),Power(c,CN1)))),m)),x),FreeQ(List(a,b,c,n,m),x))),
IIntegrate(6895,Integrate(Exp(ArcSech(Times(a_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Simp(Times(x,Exp(ArcSech(Times(a,x)))),x),Simp(Times(Log(x),Power(a,CN1)),x),Simp(Dist(Power(a,CN1),Integrate(Times(Power(Times(x,Subtract(C1,Times(a,x))),CN1),Sqrt(Times(Subtract(C1,Times(a,x)),Power(Plus(C1,Times(a,x)),CN1)))),x),x),x)),FreeQ(a,x))),
IIntegrate(6896,Integrate(Exp(ArcSech(Times(a_DEFAULT,Power(x_,p_)))),x_Symbol),
    Condition(Plus(Simp(Times(x,Exp(ArcSech(Times(a,Power(x,p))))),x),Simp(Dist(Times(p,Power(a,CN1)),Integrate(Power(Power(x,p),CN1),x),x),x),Simp(Dist(Times(p,Sqrt(Plus(C1,Times(a,Power(x,p)))),Power(a,CN1),Sqrt(Power(Plus(C1,Times(a,Power(x,p))),CN1))),Integrate(Power(Times(Power(x,p),Sqrt(Plus(C1,Times(a,Power(x,p)))),Sqrt(Subtract(C1,Times(a,Power(x,p))))),CN1),x),x),x)),FreeQ(list(a,p),x))),
IIntegrate(6897,Integrate(Exp(ArcCsch(Times(a_DEFAULT,Power(x_,p_DEFAULT)))),x_Symbol),
    Condition(Plus(Simp(Dist(Power(a,CN1),Integrate(Power(Power(x,p),CN1),x),x),x),Integrate(Sqrt(Plus(C1,Power(Times(Sqr(a),Power(x,Times(C2,p))),CN1))),x)),FreeQ(list(a,p),x))),
IIntegrate(6898,Integrate(Exp(Times(ArcSech(u_),n_DEFAULT)),x_Symbol),
    Condition(Integrate(Power(Plus(Power(u,CN1),Sqrt(Times(Subtract(C1,u),Power(Plus(C1,u),CN1))),Times(Power(u,CN1),Sqrt(Times(Subtract(C1,u),Power(Plus(C1,u),CN1))))),n),x),IntegerQ(n))),
IIntegrate(6899,Integrate(Exp(Times(ArcCsch(u_),n_DEFAULT)),x_Symbol),
    Condition(Integrate(Power(Plus(Power(u,CN1),Sqrt(Plus(C1,Power(u,CN2)))),n),x),IntegerQ(n))),
IIntegrate(6900,Integrate(Times(Exp(ArcSech(Times(a_DEFAULT,Power(x_,p_DEFAULT)))),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Negate(Simp(Power(Times(a,p,Power(x,p)),CN1),x)),Simp(Dist(Times(Sqrt(Plus(C1,Times(a,Power(x,p)))),Power(a,CN1),Sqrt(Power(Plus(C1,Times(a,Power(x,p))),CN1))),Integrate(Times(Sqrt(Plus(C1,Times(a,Power(x,p)))),Sqrt(Subtract(C1,Times(a,Power(x,p)))),Power(Power(x,Plus(p,C1)),CN1)),x),x),x)),FreeQ(list(a,p),x)))
  );
}
