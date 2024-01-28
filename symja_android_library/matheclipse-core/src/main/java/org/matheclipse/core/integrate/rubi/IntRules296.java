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
class IntRules296 { 
  public static IAST RULES = List( 
IIntegrate(5921,Integrate(Times(Power(u_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Tanh(v_))),n_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(ExpandToSum(u,x),m),Power(Plus(a,Times(b,Tanh(ExpandToSum(v,x)))),n)),x),And(FreeQ(List(a,b,m,n),x),LinearQ(list(u,v),x),Not(LinearMatchQ(list(u,v),x))))),
IIntegrate(5922,Integrate(Times(Power(Plus(a_DEFAULT,Times(Coth(v_),b_DEFAULT)),n_DEFAULT),Power(u_,m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(ExpandToSum(u,x),m),Power(Plus(a,Times(b,Coth(ExpandToSum(v,x)))),n)),x),And(FreeQ(List(a,b,m,n),x),LinearQ(list(u,v),x),Not(LinearMatchQ(list(u,v),x))))),
IIntegrate(5923,Integrate(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Tanh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT),x_Symbol),
    Condition(Simp(Star(Power(n,CN1),Subst(Integrate(Times(Power(x,Subtract(Power(n,CN1),C1)),Power(Plus(a,Times(b,Tanh(Plus(c,Times(d,x))))),p)),x),x,Power(x,n))),x),And(FreeQ(List(a,b,c,d,p),x),IGtQ(Power(n,CN1),C0),IntegerQ(p)))),
IIntegrate(5924,Integrate(Power(Plus(a_DEFAULT,Times(Coth(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Star(Power(n,CN1),Subst(Integrate(Times(Power(x,Subtract(Power(n,CN1),C1)),Power(Plus(a,Times(b,Coth(Plus(c,Times(d,x))))),p)),x),x,Power(x,n))),x),And(FreeQ(List(a,b,c,d,p),x),IGtQ(Power(n,CN1),C0),IntegerQ(p)))),
IIntegrate(5925,Integrate(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Tanh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT),x_Symbol),
    Condition(Simp(Integral(Power(Plus(a,Times(b,Tanh(Plus(c,Times(d,Power(x,n)))))),p),x),x),FreeQ(List(a,b,c,d,n,p),x))),
IIntegrate(5926,Integrate(Power(Plus(a_DEFAULT,Times(Coth(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Integral(Power(Plus(a,Times(b,Coth(Plus(c,Times(d,Power(x,n)))))),p),x),x),FreeQ(List(a,b,c,d,n,p),x))),
IIntegrate(5927,Integrate(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Tanh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(u_,n_)))))),p_DEFAULT),x_Symbol),
    Condition(Simp(Star(Power(Coefficient(u,x,C1),CN1),Subst(Integrate(Power(Plus(a,Times(b,Tanh(Plus(c,Times(d,Power(x,n)))))),p),x),x,u)),x),And(FreeQ(List(a,b,c,d,n,p),x),LinearQ(u,x),NeQ(u,x)))),
IIntegrate(5928,Integrate(Power(Plus(a_DEFAULT,Times(Coth(Plus(c_DEFAULT,Times(d_DEFAULT,Power(u_,n_)))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Star(Power(Coefficient(u,x,C1),CN1),Subst(Integrate(Power(Plus(a,Times(b,Coth(Plus(c,Times(d,Power(x,n)))))),p),x),x,u)),x),And(FreeQ(List(a,b,c,d,n,p),x),LinearQ(u,x),NeQ(u,x)))),
IIntegrate(5929,Integrate(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Tanh(u_))),p_DEFAULT),x_Symbol),
    Condition(Integrate(Power(Plus(a,Times(b,Tanh(ExpandToSum(u,x)))),p),x),And(FreeQ(list(a,b,p),x),BinomialQ(u,x),Not(BinomialMatchQ(u,x))))),
IIntegrate(5930,Integrate(Power(Plus(a_DEFAULT,Times(Coth(u_),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Integrate(Power(Plus(a,Times(b,Coth(ExpandToSum(u,x)))),p),x),And(FreeQ(list(a,b,p),x),BinomialQ(u,x),Not(BinomialMatchQ(u,x))))),
IIntegrate(5931,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Tanh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(n,CN1),Subst(Integrate(Times(Power(x,Subtract(Simplify(Times(Plus(m,C1),Power(n,CN1))),C1)),Power(Plus(a,Times(b,Tanh(Plus(c,Times(d,x))))),p)),x),x,Power(x,n))),x),And(FreeQ(List(a,b,c,d,m,n,p),x),IGtQ(Simplify(Times(Plus(m,C1),Power(n,CN1))),C0),IntegerQ(p)))),
IIntegrate(5932,Integrate(Times(Power(Plus(a_DEFAULT,Times(Coth(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(n,CN1),Subst(Integrate(Times(Power(x,Subtract(Simplify(Times(Plus(m,C1),Power(n,CN1))),C1)),Power(Plus(a,Times(b,Coth(Plus(c,Times(d,x))))),p)),x),x,Power(x,n))),x),And(FreeQ(List(a,b,c,d,m,n,p),x),IGtQ(Simplify(Times(Plus(m,C1),Power(n,CN1))),C0),IntegerQ(p)))),
IIntegrate(5933,Integrate(Times(Power(x_,m_DEFAULT),Sqr(Tanh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Power(x,Plus(m,Negate(n),C1)),Tanh(Plus(c,Times(d,Power(x,n)))),Power(Times(d,n),CN1)),x),Integrate(Power(x,m),x),Simp(Times(Plus(m,Negate(n),C1),Power(Times(d,n),CN1),Integrate(Times(Power(x,Subtract(m,n)),Tanh(Plus(c,Times(d,Power(x,n))))),x)),x)),FreeQ(List(c,d,m,n),x))),
IIntegrate(5934,Integrate(Times(Sqr(Coth(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_))))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Power(x,Plus(m,Negate(n),C1)),Coth(Plus(c,Times(d,Power(x,n)))),Power(Times(d,n),CN1)),x),Integrate(Power(x,m),x),Simp(Times(Plus(m,Negate(n),C1),Power(Times(d,n),CN1),Integrate(Times(Power(x,Subtract(m,n)),Coth(Plus(c,Times(d,Power(x,n))))),x)),x)),FreeQ(List(c,d,m,n),x))),
IIntegrate(5935,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Tanh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Integral(Times(Power(x,m),Power(Plus(a,Times(b,Tanh(Plus(c,Times(d,Power(x,n)))))),p)),x),x),FreeQ(List(a,b,c,d,m,n,p),x))),
IIntegrate(5936,Integrate(Times(Power(Plus(a_DEFAULT,Times(Coth(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Simp(Integral(Times(Power(x,m),Power(Plus(a,Times(b,Coth(Plus(c,Times(d,Power(x,n)))))),p)),x),x),FreeQ(List(a,b,c,d,m,n,p),x))),
IIntegrate(5937,Integrate(Times(Power(Times(e_,x_),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Tanh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(Power(e,IntPart(m)),Power(Times(e,x),FracPart(m)),Power(Power(x,FracPart(m)),CN1)),Integrate(Times(Power(x,m),Power(Plus(a,Times(b,Tanh(Plus(c,Times(d,Power(x,n)))))),p)),x)),x),FreeQ(List(a,b,c,d,e,m,n,p),x))),
IIntegrate(5938,Integrate(Times(Power(Plus(a_DEFAULT,Times(Coth(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(Times(e_,x_),m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(Power(e,IntPart(m)),Power(Times(e,x),FracPart(m)),Power(Power(x,FracPart(m)),CN1)),Integrate(Times(Power(x,m),Power(Plus(a,Times(b,Coth(Plus(c,Times(d,Power(x,n)))))),p)),x)),x),FreeQ(List(a,b,c,d,e,m,n,p),x))),
IIntegrate(5939,Integrate(Times(Power(Times(e_,x_),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Tanh(u_))),p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Times(e,x),m),Power(Plus(a,Times(b,Tanh(ExpandToSum(u,x)))),p)),x),And(FreeQ(List(a,b,e,m,p),x),BinomialQ(u,x),Not(BinomialMatchQ(u,x))))),
IIntegrate(5940,Integrate(Times(Power(Plus(a_DEFAULT,Times(Coth(u_),b_DEFAULT)),p_DEFAULT),Power(Times(e_,x_),m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Times(e,x),m),Power(Plus(a,Times(b,Coth(ExpandToSum(u,x)))),p)),x),And(FreeQ(List(a,b,e,m,p),x),BinomialQ(u,x),Not(BinomialMatchQ(u,x)))))
  );
}
