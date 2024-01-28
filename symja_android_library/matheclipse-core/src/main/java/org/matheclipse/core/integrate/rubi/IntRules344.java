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
class IntRules344 { 
  public static IAST RULES = List( 
IIntegrate(6881,Integrate(Times(Power(ArcSech(Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))),CN1))),m_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Integrate(Times(u,Power(ArcCosh(Plus(Times(a,Power(c,CN1)),Times(b,Power(x,n),Power(c,CN1)))),m)),x),FreeQ(List(a,b,c,n,m),x))),
IIntegrate(6882,Integrate(Times(Power(ArcCsch(Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))),CN1))),m_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Integrate(Times(u,Power(ArcSinh(Plus(Times(a,Power(c,CN1)),Times(b,Power(x,n),Power(c,CN1)))),m)),x),FreeQ(List(a,b,c,n,m),x))),
IIntegrate(6883,Integrate(Exp(ArcSech(Times(a_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Simp(Times(x,Exp(ArcSech(Times(a,x)))),x),Simp(Times(Log(x),Power(a,CN1)),x),Simp(Star(Power(a,CN1),Integrate(Times(Power(Times(x,Subtract(C1,Times(a,x))),CN1),Sqrt(Times(Subtract(C1,Times(a,x)),Power(Plus(C1,Times(a,x)),CN1)))),x)),x)),FreeQ(a,x))),
IIntegrate(6884,Integrate(Exp(ArcSech(Times(a_DEFAULT,Power(x_,p_)))),x_Symbol),
    Condition(Plus(Simp(Times(x,Exp(ArcSech(Times(a,Power(x,p))))),x),Simp(Star(Times(p,Power(a,CN1)),Integrate(Power(Power(x,p),CN1),x)),x),Simp(Star(Times(p,Sqrt(Plus(C1,Times(a,Power(x,p)))),Power(a,CN1),Sqrt(Power(Plus(C1,Times(a,Power(x,p))),CN1))),Integrate(Power(Times(Power(x,p),Sqrt(Plus(C1,Times(a,Power(x,p)))),Sqrt(Subtract(C1,Times(a,Power(x,p))))),CN1),x)),x)),FreeQ(list(a,p),x))),
IIntegrate(6885,Integrate(Exp(ArcCsch(Times(a_DEFAULT,Power(x_,p_DEFAULT)))),x_Symbol),
    Condition(Plus(Simp(Star(Power(a,CN1),Integrate(Power(Power(x,p),CN1),x)),x),Integrate(Sqrt(Plus(C1,Power(Times(Sqr(a),Power(x,Times(C2,p))),CN1))),x)),FreeQ(list(a,p),x))),
IIntegrate(6886,Integrate(Exp(Times(ArcSech(u_),n_DEFAULT)),x_Symbol),
    Condition(Integrate(Power(Plus(Power(u,CN1),Sqrt(Times(Subtract(C1,u),Power(Plus(C1,u),CN1))),Times(Power(u,CN1),Sqrt(Times(Subtract(C1,u),Power(Plus(C1,u),CN1))))),n),x),IntegerQ(n))),
IIntegrate(6887,Integrate(Exp(Times(ArcCsch(u_),n_DEFAULT)),x_Symbol),
    Condition(Integrate(Power(Plus(Power(u,CN1),Sqrt(Plus(C1,Power(u,CN2)))),n),x),IntegerQ(n))),
IIntegrate(6888,Integrate(Times(Exp(ArcSech(Times(a_DEFAULT,Power(x_,p_DEFAULT)))),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Negate(Simp(Power(Times(a,p,Power(x,p)),CN1),x)),Simp(Star(Times(Sqrt(Plus(C1,Times(a,Power(x,p)))),Power(a,CN1),Sqrt(Power(Plus(C1,Times(a,Power(x,p))),CN1))),Integrate(Times(Sqrt(Plus(C1,Times(a,Power(x,p)))),Sqrt(Subtract(C1,Times(a,Power(x,p)))),Power(Power(x,Plus(p,C1)),CN1)),x)),x)),FreeQ(list(a,p),x))),
IIntegrate(6889,Integrate(Times(Exp(ArcSech(Times(a_DEFAULT,Power(x_,p_DEFAULT)))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),Exp(ArcSech(Times(a,Power(x,p)))),Power(Plus(m,C1),CN1)),x),Simp(Star(Times(p,Power(Times(a,Plus(m,C1)),CN1)),Integrate(Power(x,Subtract(m,p)),x)),x),Simp(Star(Times(p,Sqrt(Plus(C1,Times(a,Power(x,p)))),Power(Times(a,Plus(m,C1)),CN1),Sqrt(Power(Plus(C1,Times(a,Power(x,p))),CN1))),Integrate(Times(Power(x,Subtract(m,p)),Power(Times(Sqrt(Plus(C1,Times(a,Power(x,p)))),Sqrt(Subtract(C1,Times(a,Power(x,p))))),CN1)),x)),x)),And(FreeQ(list(a,m,p),x),NeQ(m,CN1)))),
IIntegrate(6890,Integrate(Times(Exp(ArcCsch(Times(a_DEFAULT,Power(x_,p_DEFAULT)))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Star(Power(a,CN1),Integrate(Power(x,Subtract(m,p)),x)),x),Integrate(Times(Power(x,m),Sqrt(Plus(C1,Power(Times(Sqr(a),Power(x,Times(C2,p))),CN1)))),x)),FreeQ(list(a,m,p),x))),
IIntegrate(6891,Integrate(Times(Exp(Times(ArcSech(u_),n_DEFAULT)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(x,m),Power(Plus(Power(u,CN1),Sqrt(Times(Subtract(C1,u),Power(Plus(C1,u),CN1))),Times(Power(u,CN1),Sqrt(Times(Subtract(C1,u),Power(Plus(C1,u),CN1))))),n)),x),And(FreeQ(m,x),IntegerQ(n)))),
IIntegrate(6892,Integrate(Times(Exp(Times(ArcCsch(u_),n_DEFAULT)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(x,m),Power(Plus(Power(u,CN1),Sqrt(Plus(C1,Power(u,CN2)))),n)),x),And(FreeQ(m,x),IntegerQ(n)))),
IIntegrate(6893,Integrate(Times(Exp(ArcSech(Times(c_DEFAULT,x_))),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Plus(Simp(Star(Power(Times(a,c),CN1),Integrate(Times(Sqrt(Power(Plus(C1,Times(c,x)),CN1)),Power(Times(x,Sqrt(Subtract(C1,Times(c,x)))),CN1)),x)),x),Simp(Star(Power(c,CN1),Integrate(Power(Times(x,Plus(a,Times(b,Sqr(x)))),CN1),x)),x)),And(FreeQ(list(a,b,c),x),EqQ(Plus(b,Times(a,Sqr(c))),C0)))),
IIntegrate(6894,Integrate(Times(Exp(ArcCsch(Times(c_DEFAULT,x_))),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Plus(Simp(Star(Power(Times(a,Sqr(c)),CN1),Integrate(Power(Times(Sqr(x),Sqrt(Plus(C1,Power(Times(Sqr(c),Sqr(x)),CN1)))),CN1),x)),x),Simp(Star(Power(c,CN1),Integrate(Power(Times(x,Plus(a,Times(b,Sqr(x)))),CN1),x)),x)),And(FreeQ(list(a,b,c),x),EqQ(Subtract(b,Times(a,Sqr(c))),C0)))),
IIntegrate(6895,Integrate(Times(Exp(ArcSech(Times(c_DEFAULT,x_))),Power(Times(d_DEFAULT,x_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Plus(Simp(Star(Times(d,Power(Times(a,c),CN1)),Integrate(Times(Power(Times(d,x),Subtract(m,C1)),Sqrt(Power(Plus(C1,Times(c,x)),CN1)),Power(Subtract(C1,Times(c,x)),CN1D2)),x)),x),Simp(Star(Times(d,Power(c,CN1)),Integrate(Times(Power(Times(d,x),Subtract(m,C1)),Power(Plus(a,Times(b,Sqr(x))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,m),x),EqQ(Plus(b,Times(a,Sqr(c))),C0)))),
IIntegrate(6896,Integrate(Times(Exp(ArcCsch(Times(c_DEFAULT,x_))),Power(Times(d_DEFAULT,x_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Plus(Simp(Star(Times(Sqr(d),Power(Times(a,Sqr(c)),CN1)),Integrate(Times(Power(Times(d,x),Subtract(m,C2)),Power(Plus(C1,Power(Times(Sqr(c),Sqr(x)),CN1)),CN1D2)),x)),x),Simp(Star(Times(d,Power(c,CN1)),Integrate(Times(Power(Times(d,x),Subtract(m,C1)),Power(Plus(a,Times(b,Sqr(x))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,m),x),EqQ(Subtract(b,Times(a,Sqr(c))),C0)))),
IIntegrate(6897,Integrate(ArcSech(u_),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcSech(u)),x),Simp(Star(Times(Sqrt(Subtract(C1,Sqr(u))),Power(Times(u,Sqrt(Plus(CN1,Power(u,CN1))),Sqrt(Plus(C1,Power(u,CN1)))),CN1)),Integrate(SimplifyIntegrand(Times(x,D(u,x),Power(Times(u,Sqrt(Subtract(C1,Sqr(u)))),CN1)),x),x)),x)),And(InverseFunctionFreeQ(u,x),Not(FunctionOfExponentialQ(u,x))))),
IIntegrate(6898,Integrate(ArcCsch(u_),x_Symbol),
    Condition(Subtract(Simp(Times(x,ArcCsch(u)),x),Simp(Star(Times(u,Power(Negate(Sqr(u)),CN1D2)),Integrate(SimplifyIntegrand(Times(x,D(u,x),Power(Times(u,Sqrt(Subtract(CN1,Sqr(u)))),CN1)),x),x)),x)),And(InverseFunctionFreeQ(u,x),Not(FunctionOfExponentialQ(u,x))))),
IIntegrate(6899,Integrate(Times(Plus(a_DEFAULT,Times(ArcSech(u_),b_DEFAULT)),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Plus(a,Times(b,ArcSech(u))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,Sqrt(Subtract(C1,Sqr(u))),Power(Times(d,Plus(m,C1),u,Sqrt(Plus(CN1,Power(u,CN1))),Sqrt(Plus(C1,Power(u,CN1)))),CN1)),Integrate(SimplifyIntegrand(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),D(u,x),Power(Times(u,Sqrt(Subtract(C1,Sqr(u)))),CN1)),x),x)),x)),And(FreeQ(List(a,b,c,d,m),x),NeQ(m,CN1),InverseFunctionFreeQ(u,x),Not(FunctionOfQ(Power(Plus(c,Times(d,x)),Plus(m,C1)),u,x)),Not(FunctionOfExponentialQ(u,x))))),
IIntegrate(6900,Integrate(Times(Plus(a_DEFAULT,Times(ArcCsch(u_),b_DEFAULT)),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Plus(a,Times(b,ArcCsch(u))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,u,Power(Times(d,Plus(m,C1),Sqrt(Negate(Sqr(u)))),CN1)),Integrate(SimplifyIntegrand(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),D(u,x),Power(Times(u,Sqrt(Subtract(CN1,Sqr(u)))),CN1)),x),x)),x)),And(FreeQ(List(a,b,c,d,m),x),NeQ(m,CN1),InverseFunctionFreeQ(u,x),Not(FunctionOfQ(Power(Plus(c,Times(d,x)),Plus(m,C1)),u,x)),Not(FunctionOfExponentialQ(u,x)))))
  );
}
