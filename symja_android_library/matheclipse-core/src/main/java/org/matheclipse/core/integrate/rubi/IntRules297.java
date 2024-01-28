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
class IntRules297 { 
  public static IAST RULES = List( 
IIntegrate(5941,Integrate(Times(Power(x_,m_DEFAULT),Power(Sech(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT),Power(Tanh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),q_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Power(x,Plus(m,Negate(n),C1)),Power(Sech(Plus(a,Times(b,Power(x,n)))),p),Power(Times(b,n,p),CN1)),x),Simp(Star(Times(Plus(m,Negate(n),C1),Power(Times(b,n,p),CN1)),Integrate(Times(Power(x,Subtract(m,n)),Power(Sech(Plus(a,Times(b,Power(x,n)))),p)),x)),x)),And(FreeQ(list(a,b,p),x),RationalQ(m),IntegerQ(n),GeQ(Subtract(m,n),C0),EqQ(q,C1)))),
IIntegrate(5942,Integrate(Times(Power(Coth(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),q_DEFAULT),Power(Csch(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Power(x,Plus(m,Negate(n),C1)),Power(Csch(Plus(a,Times(b,Power(x,n)))),p),Power(Times(b,n,p),CN1)),x),Simp(Star(Times(Plus(m,Negate(n),C1),Power(Times(b,n,p),CN1)),Integrate(Times(Power(x,Subtract(m,n)),Power(Csch(Plus(a,Times(b,Power(x,n)))),p)),x)),x)),And(FreeQ(list(a,b,p),x),RationalQ(m),IntegerQ(n),GeQ(Subtract(m,n),C0),EqQ(q,C1)))),
IIntegrate(5943,Integrate(Power(Tanh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_DEFAULT),x_Symbol),
    Condition(Simp(Integral(Power(Tanh(Plus(a,Times(b,x),Times(c,Sqr(x)))),n),x),x),FreeQ(List(a,b,c,n),x))),
IIntegrate(5944,Integrate(Power(Coth(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_DEFAULT),x_Symbol),
    Condition(Simp(Integral(Power(Coth(Plus(a,Times(b,x),Times(c,Sqr(x)))),n),x),x),FreeQ(List(a,b,c,n),x))),
IIntegrate(5945,Integrate(Times(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),Tanh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Simp(Times(e,Log(Cosh(Plus(a,Times(b,x),Times(c,Sqr(x))))),Power(Times(C2,c),CN1)),x),Simp(Star(Times(Subtract(Times(C2,c,d),Times(b,e)),Power(Times(C2,c),CN1)),Integrate(Tanh(Plus(a,Times(b,x),Times(c,Sqr(x)))),x)),x)),FreeQ(List(a,b,c,d,e),x))),
IIntegrate(5946,Integrate(Times(Coth(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),Plus(d_DEFAULT,Times(e_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Simp(Times(e,Log(Sinh(Plus(a,Times(b,x),Times(c,Sqr(x))))),Power(Times(C2,c),CN1)),x),Simp(Star(Times(Subtract(Times(C2,c,d),Times(b,e)),Power(Times(C2,c),CN1)),Integrate(Coth(Plus(a,Times(b,x),Times(c,Sqr(x)))),x)),x)),FreeQ(List(a,b,c,d,e),x))),
IIntegrate(5947,Integrate(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT),Power(Tanh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_DEFAULT)),x_Symbol),
    Condition(Simp(Integral(Times(Power(Plus(d,Times(e,x)),m),Power(Tanh(Plus(a,Times(b,x),Times(c,Sqr(x)))),n)),x),x),FreeQ(List(a,b,c,d,e,m,n),x))),
IIntegrate(5948,Integrate(Times(Power(Coth(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Integral(Times(Power(Plus(d,Times(e,x)),m),Power(Coth(Plus(a,Times(b,x),Times(c,Sqr(x)))),n)),x),x),FreeQ(List(a,b,c,d,e,m,n),x))),
IIntegrate(5949,Integrate(Times(Power(u_,m_DEFAULT),Power(Sech(v_),n_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(ExpandToSum(u,x),m),Power(Sech(ExpandToSum(v,x)),n)),x),And(FreeQ(list(m,n),x),LinearQ(list(u,v),x),Not(LinearMatchQ(list(u,v),x))))),
IIntegrate(5950,Integrate(Times(Power(Csch(v_),n_DEFAULT),Power(u_,m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(ExpandToSum(u,x),m),Power(Csch(ExpandToSum(v,x)),n)),x),And(FreeQ(list(m,n),x),LinearQ(list(u,v),x),Not(LinearMatchQ(list(u,v),x))))),
IIntegrate(5951,Integrate(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sech(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT),x_Symbol),
    Condition(Simp(Star(Power(n,CN1),Subst(Integrate(Times(Power(x,Subtract(Power(n,CN1),C1)),Power(Plus(a,Times(b,Sech(Plus(c,Times(d,x))))),p)),x),x,Power(x,n))),x),And(FreeQ(List(a,b,c,d,p),x),IGtQ(Power(n,CN1),C0),IntegerQ(p)))),
IIntegrate(5952,Integrate(Power(Plus(a_DEFAULT,Times(Csch(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Star(Power(n,CN1),Subst(Integrate(Times(Power(x,Subtract(Power(n,CN1),C1)),Power(Plus(a,Times(b,Csch(Plus(c,Times(d,x))))),p)),x),x,Power(x,n))),x),And(FreeQ(List(a,b,c,d,p),x),IGtQ(Power(n,CN1),C0),IntegerQ(p)))),
IIntegrate(5953,Integrate(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sech(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT),x_Symbol),
    Condition(Unintegrable(Power(Plus(a,Times(b,Sech(Plus(c,Times(d,Power(x,n)))))),p),x),FreeQ(List(a,b,c,d,n,p),x))),
IIntegrate(5954,Integrate(Power(Plus(a_DEFAULT,Times(Csch(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Unintegrable(Power(Plus(a,Times(b,Csch(Plus(c,Times(d,Power(x,n)))))),p),x),FreeQ(List(a,b,c,d,n,p),x))),
IIntegrate(5955,Integrate(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sech(Plus(c_DEFAULT,Times(d_DEFAULT,Power(u_,n_)))))),p_DEFAULT),x_Symbol),
    Condition(Simp(Star(Power(Coefficient(u,x,C1),CN1),Subst(Integrate(Power(Plus(a,Times(b,Sech(Plus(c,Times(d,Power(x,n)))))),p),x),x,u)),x),And(FreeQ(List(a,b,c,d,n,p),x),LinearQ(u,x),NeQ(u,x)))),
IIntegrate(5956,Integrate(Power(Plus(a_DEFAULT,Times(Csch(Plus(c_DEFAULT,Times(d_DEFAULT,Power(u_,n_)))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Star(Power(Coefficient(u,x,C1),CN1),Subst(Integrate(Power(Plus(a,Times(b,Csch(Plus(c,Times(d,Power(x,n)))))),p),x),x,u)),x),And(FreeQ(List(a,b,c,d,n,p),x),LinearQ(u,x),NeQ(u,x)))),
IIntegrate(5957,Integrate(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sech(u_))),p_DEFAULT),x_Symbol),
    Condition(Integrate(Power(Plus(a,Times(b,Sech(ExpandToSum(u,x)))),p),x),And(FreeQ(list(a,b,p),x),BinomialQ(u,x),Not(BinomialMatchQ(u,x))))),
IIntegrate(5958,Integrate(Power(Plus(a_DEFAULT,Times(Csch(u_),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Integrate(Power(Plus(a,Times(b,Csch(ExpandToSum(u,x)))),p),x),And(FreeQ(list(a,b,p),x),BinomialQ(u,x),Not(BinomialMatchQ(u,x))))),
IIntegrate(5959,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sech(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(n,CN1),Subst(Integrate(Times(Power(x,Subtract(Simplify(Times(Plus(m,C1),Power(n,CN1))),C1)),Power(Plus(a,Times(b,Sech(Plus(c,Times(d,x))))),p)),x),x,Power(x,n))),x),And(FreeQ(List(a,b,c,d,m,n,p),x),IGtQ(Simplify(Times(Plus(m,C1),Power(n,CN1))),C0),IntegerQ(p)))),
IIntegrate(5960,Integrate(Times(Power(Plus(a_DEFAULT,Times(Csch(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(n,CN1),Subst(Integrate(Times(Power(x,Subtract(Simplify(Times(Plus(m,C1),Power(n,CN1))),C1)),Power(Plus(a,Times(b,Csch(Plus(c,Times(d,x))))),p)),x),x,Power(x,n))),x),And(FreeQ(List(a,b,c,d,m,n,p),x),IGtQ(Simplify(Times(Plus(m,C1),Power(n,CN1))),C0),IntegerQ(p))))
  );
}
