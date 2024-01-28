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
class IntRules298 { 
  public static IAST RULES = List( 
IIntegrate(5961,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sech(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(x,m),Power(Plus(a,Times(b,Sech(Plus(c,Times(d,Power(x,n)))))),p)),x),FreeQ(List(a,b,c,d,m,n,p),x))),
IIntegrate(5962,Integrate(Times(Power(Plus(a_DEFAULT,Times(Csch(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(x,m),Power(Plus(a,Times(b,Csch(Plus(c,Times(d,Power(x,n)))))),p)),x),FreeQ(List(a,b,c,d,m,n,p),x))),
IIntegrate(5963,Integrate(Times(Power(Times(e_,x_),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sech(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(Power(e,IntPart(m)),Power(Times(e,x),FracPart(m)),Power(Power(x,FracPart(m)),CN1)),Integrate(Times(Power(x,m),Power(Plus(a,Times(b,Sech(Plus(c,Times(d,Power(x,n)))))),p)),x)),x),FreeQ(List(a,b,c,d,e,m,n,p),x))),
IIntegrate(5964,Integrate(Times(Power(Plus(a_DEFAULT,Times(Csch(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(Times(e_,x_),m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(Power(e,IntPart(m)),Power(Times(e,x),FracPart(m)),Power(Power(x,FracPart(m)),CN1)),Integrate(Times(Power(x,m),Power(Plus(a,Times(b,Csch(Plus(c,Times(d,Power(x,n)))))),p)),x)),x),FreeQ(List(a,b,c,d,e,m,n,p),x))),
IIntegrate(5965,Integrate(Times(Power(Times(e_,x_),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sech(u_))),p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Times(e,x),m),Power(Plus(a,Times(b,Sech(ExpandToSum(u,x)))),p)),x),And(FreeQ(List(a,b,e,m,p),x),BinomialQ(u,x),Not(BinomialMatchQ(u,x))))),
IIntegrate(5966,Integrate(Times(Power(Plus(a_DEFAULT,Times(Csch(u_),b_DEFAULT)),p_DEFAULT),Power(Times(e_,x_),m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Times(e,x),m),Power(Plus(a,Times(b,Csch(ExpandToSum(u,x)))),p)),x),And(FreeQ(List(a,b,e,m,p),x),BinomialQ(u,x),Not(BinomialMatchQ(u,x))))),
IIntegrate(5967,Integrate(Times(Power(x_,m_DEFAULT),Power(Sech(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),p_),Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))))),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Power(x,Plus(m,Negate(n),C1)),Power(Sech(Plus(a,Times(b,Power(x,n)))),Subtract(p,C1)),Power(Times(b,n,Subtract(p,C1)),CN1)),x),Simp(Star(Times(Plus(m,Negate(n),C1),Power(Times(b,n,Subtract(p,C1)),CN1)),Integrate(Times(Power(x,Subtract(m,n)),Power(Sech(Plus(a,Times(b,Power(x,n)))),Subtract(p,C1))),x)),x)),And(FreeQ(list(a,b,p),x),IntegerQ(n),GeQ(Subtract(m,n),C0),NeQ(p,C1)))),
IIntegrate(5968,Integrate(Times(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),Power(Csch(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),p_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Power(x,Plus(m,Negate(n),C1)),Power(Csch(Plus(a,Times(b,Power(x,n)))),Subtract(p,C1)),Power(Times(b,n,Subtract(p,C1)),CN1)),x),Simp(Star(Times(Plus(m,Negate(n),C1),Power(Times(b,n,Subtract(p,C1)),CN1)),Integrate(Times(Power(x,Subtract(m,n)),Power(Csch(Plus(a,Times(b,Power(x,n)))),Subtract(p,C1))),x)),x)),And(FreeQ(list(a,b,p),x),IntegerQ(n),GeQ(Subtract(m,n),C0),NeQ(p,C1)))),
IIntegrate(5969,Integrate(Times(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),m),Power(Sinh(Plus(a,Times(b,x))),Plus(n,C1)),Power(Times(b,Plus(n,C1)),CN1)),x),Simp(Star(Times(d,m,Power(Times(b,Plus(n,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Subtract(m,C1)),Power(Sinh(Plus(a,Times(b,x))),Plus(n,C1))),x)),x)),And(FreeQ(List(a,b,c,d,n),x),IGtQ(m,C0),NeQ(n,CN1)))),
IIntegrate(5970,Integrate(Times(Power(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),m),Power(Cosh(Plus(a,Times(b,x))),Plus(n,C1)),Power(Times(b,Plus(n,C1)),CN1)),x),Simp(Star(Times(d,m,Power(Times(b,Plus(n,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Subtract(m,C1)),Power(Cosh(Plus(a,Times(b,x))),Plus(n,C1))),x)),x)),And(FreeQ(List(a,b,c,d,n),x),IGtQ(m,C0),NeQ(n,CN1)))),
IIntegrate(5971,Integrate(Times(Power(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Power(Plus(c,Times(d,x)),m),Times(Power(Sinh(Plus(a,Times(b,x))),n),Power(Cosh(Plus(a,Times(b,x))),p)),x),x),And(FreeQ(List(a,b,c,d,m),x),IGtQ(n,C0),IGtQ(p,C0)))),
IIntegrate(5972,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Tanh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT)),x_Symbol),
    Condition(Subtract(Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(Sinh(Plus(a,Times(b,x))),n),Power(Tanh(Plus(a,Times(b,x))),Subtract(p,C2))),x),Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(Sinh(Plus(a,Times(b,x))),Subtract(n,C2)),Power(Tanh(Plus(a,Times(b,x))),p)),x)),And(FreeQ(List(a,b,c,d,m),x),IGtQ(n,C0),IGtQ(p,C0)))),
IIntegrate(5973,Integrate(Times(Power(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Coth(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(Cosh(Plus(a,Times(b,x))),n),Power(Coth(Plus(a,Times(b,x))),Subtract(p,C2))),x),Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(Cosh(Plus(a,Times(b,x))),Subtract(n,C2)),Power(Coth(Plus(a,Times(b,x))),p)),x)),And(FreeQ(List(a,b,c,d,m),x),IGtQ(n,C0),IGtQ(p,C0)))),
IIntegrate(5974,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Sech(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Tanh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Power(Plus(c,Times(d,x)),m),Power(Sech(Plus(a,Times(b,x))),n),Power(Times(b,n),CN1)),x),Simp(Star(Times(d,m,Power(Times(b,n),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Subtract(m,C1)),Power(Sech(Plus(a,Times(b,x))),n)),x)),x)),And(FreeQ(List(a,b,c,d,n),x),EqQ(p,C1),GtQ(m,C0)))),
IIntegrate(5975,Integrate(Times(Power(Coth(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT),Power(Csch(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Power(Plus(c,Times(d,x)),m),Power(Csch(Plus(a,Times(b,x))),n),Power(Times(b,n),CN1)),x),Simp(Star(Times(d,m,Power(Times(b,n),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Subtract(m,C1)),Power(Csch(Plus(a,Times(b,x))),n)),x)),x)),And(FreeQ(List(a,b,c,d,n),x),EqQ(p,C1),GtQ(m,C0)))),
IIntegrate(5976,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Sqr(Sech(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Tanh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),m),Power(Tanh(Plus(a,Times(b,x))),Plus(n,C1)),Power(Times(b,Plus(n,C1)),CN1)),x),Simp(Star(Times(d,m,Power(Times(b,Plus(n,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Subtract(m,C1)),Power(Tanh(Plus(a,Times(b,x))),Plus(n,C1))),x)),x)),And(FreeQ(List(a,b,c,d,n),x),IGtQ(m,C0),NeQ(n,CN1)))),
IIntegrate(5977,Integrate(Times(Power(Coth(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Sqr(Csch(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Power(Plus(c,Times(d,x)),m),Power(Coth(Plus(a,Times(b,x))),Plus(n,C1)),Power(Times(b,Plus(n,C1)),CN1)),x),Simp(Star(Times(d,m,Power(Times(b,Plus(n,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Subtract(m,C1)),Power(Coth(Plus(a,Times(b,x))),Plus(n,C1))),x)),x)),And(FreeQ(List(a,b,c,d,n),x),IGtQ(m,C0),NeQ(n,CN1)))),
IIntegrate(5978,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Sech(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Tanh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_)),x_Symbol),
    Condition(Subtract(Integrate(Times(Power(Plus(c,Times(d,x)),m),Sech(Plus(a,Times(b,x))),Power(Tanh(Plus(a,Times(b,x))),Subtract(p,C2))),x),Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(Sech(Plus(a,Times(b,x))),C3),Power(Tanh(Plus(a,Times(b,x))),Subtract(p,C2))),x)),And(FreeQ(List(a,b,c,d,m),x),IGtQ(Times(C1D2,p),C0)))),
IIntegrate(5979,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Sech(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Tanh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_)),x_Symbol),
    Condition(Subtract(Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(Sech(Plus(a,Times(b,x))),n),Power(Tanh(Plus(a,Times(b,x))),Subtract(p,C2))),x),Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(Sech(Plus(a,Times(b,x))),Plus(n,C2)),Power(Tanh(Plus(a,Times(b,x))),Subtract(p,C2))),x)),And(FreeQ(List(a,b,c,d,m,n),x),IGtQ(Times(C1D2,p),C0)))),
IIntegrate(5980,Integrate(Times(Power(Coth(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_),Csch(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Integrate(Times(Power(Plus(c,Times(d,x)),m),Csch(Plus(a,Times(b,x))),Power(Coth(Plus(a,Times(b,x))),Subtract(p,C2))),x),Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(Csch(Plus(a,Times(b,x))),C3),Power(Coth(Plus(a,Times(b,x))),Subtract(p,C2))),x)),And(FreeQ(List(a,b,c,d,m),x),IGtQ(Times(C1D2,p),C0))))
  );
}
