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
class IntRules297 { 
  public static IAST RULES = List( 
IIntegrate(5941,Integrate(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Tanh(u_))),p_DEFAULT),x_Symbol),
    Condition(Integrate(Power(Plus(a,Times(b,Tanh(ExpandToSum(u,x)))),p),x),And(FreeQ(list(a,b,p),x),BinomialQ(u,x),Not(BinomialMatchQ(u,x))))),
IIntegrate(5942,Integrate(Power(Plus(a_DEFAULT,Times(Coth(u_),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Integrate(Power(Plus(a,Times(b,Coth(ExpandToSum(u,x)))),p),x),And(FreeQ(list(a,b,p),x),BinomialQ(u,x),Not(BinomialMatchQ(u,x))))),
IIntegrate(5943,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Tanh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(n,CN1),Subst(Integrate(Times(Power(x,Plus(Simplify(Times(Plus(m,C1),Power(n,CN1))),CN1)),Power(Plus(a,Times(b,Tanh(Plus(c,Times(d,x))))),p)),x),x,Power(x,n)),x),x),And(FreeQ(List(a,b,c,d,m,n,p),x),IGtQ(Simplify(Times(Plus(m,C1),Power(n,CN1))),C0),IntegerQ(p)))),
IIntegrate(5944,Integrate(Times(Power(Plus(a_DEFAULT,Times(Coth(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(n,CN1),Subst(Integrate(Times(Power(x,Plus(Simplify(Times(Plus(m,C1),Power(n,CN1))),CN1)),Power(Plus(a,Times(b,Coth(Plus(c,Times(d,x))))),p)),x),x,Power(x,n)),x),x),And(FreeQ(List(a,b,c,d,m,n,p),x),IGtQ(Simplify(Times(Plus(m,C1),Power(n,CN1))),C0),IntegerQ(p)))),
IIntegrate(5945,Integrate(Times(Power(x_,m_DEFAULT),Sqr(Tanh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Power(x,Plus(m,Negate(n),C1)),Tanh(Plus(c,Times(d,Power(x,n)))),Power(Times(d,n),CN1)),x),Integrate(Power(x,m),x),Simp(Times(Plus(m,Negate(n),C1),Power(Times(d,n),CN1),Integrate(Times(Power(x,Subtract(m,n)),Tanh(Plus(c,Times(d,Power(x,n))))),x)),x)),FreeQ(List(c,d,m,n),x))),
IIntegrate(5946,Integrate(Times(Sqr(Coth(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_))))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Power(x,Plus(m,Negate(n),C1)),Coth(Plus(c,Times(d,Power(x,n)))),Power(Times(d,n),CN1)),x),Integrate(Power(x,m),x),Simp(Times(Plus(m,Negate(n),C1),Power(Times(d,n),CN1),Integrate(Times(Power(x,Subtract(m,n)),Coth(Plus(c,Times(d,Power(x,n))))),x)),x)),FreeQ(List(c,d,m,n),x))),
IIntegrate(5947,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Tanh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Integral(Times(Power(x,m),Power(Plus(a,Times(b,Tanh(Plus(c,Times(d,Power(x,n)))))),p)),x),x),FreeQ(List(a,b,c,d,m,n,p),x))),
IIntegrate(5948,Integrate(Times(Power(Plus(a_DEFAULT,Times(Coth(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Simp(Integral(Times(Power(x,m),Power(Plus(a,Times(b,Coth(Plus(c,Times(d,Power(x,n)))))),p)),x),x),FreeQ(List(a,b,c,d,m,n,p),x))),
IIntegrate(5949,Integrate(Times(Power(Times(e_,x_),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Tanh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Times(Power(e,IntPart(m)),Power(Times(e,x),FracPart(m)),Power(Power(x,FracPart(m)),CN1)),Integrate(Times(Power(x,m),Power(Plus(a,Times(b,Tanh(Plus(c,Times(d,Power(x,n)))))),p)),x),x),x),FreeQ(List(a,b,c,d,e,m,n,p),x))),
IIntegrate(5950,Integrate(Times(Power(Plus(a_DEFAULT,Times(Coth(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(Times(e_,x_),m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Times(Power(e,IntPart(m)),Power(Times(e,x),FracPart(m)),Power(Power(x,FracPart(m)),CN1)),Integrate(Times(Power(x,m),Power(Plus(a,Times(b,Coth(Plus(c,Times(d,Power(x,n)))))),p)),x),x),x),FreeQ(List(a,b,c,d,e,m,n,p),x))),
IIntegrate(5951,Integrate(Times(Power(Times(e_,x_),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Tanh(u_))),p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Times(e,x),m),Power(Plus(a,Times(b,Tanh(ExpandToSum(u,x)))),p)),x),And(FreeQ(List(a,b,e,m,p),x),BinomialQ(u,x),Not(BinomialMatchQ(u,x))))),
IIntegrate(5952,Integrate(Times(Power(Plus(a_DEFAULT,Times(Coth(u_),b_DEFAULT)),p_DEFAULT),Power(Times(e_,x_),m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Times(e,x),m),Power(Plus(a,Times(b,Coth(ExpandToSum(u,x)))),p)),x),And(FreeQ(List(a,b,e,m,p),x),BinomialQ(u,x),Not(BinomialMatchQ(u,x))))),
IIntegrate(5953,Integrate(Times(Power(x_,m_DEFAULT),Power(Sech(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT),Power(Tanh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),q_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Power(x,Plus(m,Negate(n),C1)),Power(Sech(Plus(a,Times(b,Power(x,n)))),p),Power(Times(b,n,p),CN1)),x),Simp(Dist(Times(Plus(m,Negate(n),C1),Power(Times(b,n,p),CN1)),Integrate(Times(Power(x,Subtract(m,n)),Power(Sech(Plus(a,Times(b,Power(x,n)))),p)),x),x),x)),And(FreeQ(list(a,b,p),x),RationalQ(m),IntegerQ(n),GeQ(Subtract(m,n),C0),EqQ(q,C1)))),
IIntegrate(5954,Integrate(Times(Power(Coth(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),q_DEFAULT),Power(Csch(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Power(x,Plus(m,Negate(n),C1)),Power(Csch(Plus(a,Times(b,Power(x,n)))),p),Power(Times(b,n,p),CN1)),x),Simp(Dist(Times(Plus(m,Negate(n),C1),Power(Times(b,n,p),CN1)),Integrate(Times(Power(x,Subtract(m,n)),Power(Csch(Plus(a,Times(b,Power(x,n)))),p)),x),x),x)),And(FreeQ(list(a,b,p),x),RationalQ(m),IntegerQ(n),GeQ(Subtract(m,n),C0),EqQ(q,C1)))),
IIntegrate(5955,Integrate(Power(Tanh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_DEFAULT),x_Symbol),
    Condition(Simp(Integral(Power(Tanh(Plus(a,Times(b,x),Times(c,Sqr(x)))),n),x),x),FreeQ(List(a,b,c,n),x))),
IIntegrate(5956,Integrate(Power(Coth(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_DEFAULT),x_Symbol),
    Condition(Simp(Integral(Power(Coth(Plus(a,Times(b,x),Times(c,Sqr(x)))),n),x),x),FreeQ(List(a,b,c,n),x))),
IIntegrate(5957,Integrate(Times(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),Tanh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Simp(Times(e,Log(Cosh(Plus(a,Times(b,x),Times(c,Sqr(x))))),Power(Times(C2,c),CN1)),x),Simp(Dist(Times(Subtract(Times(C2,c,d),Times(b,e)),Power(Times(C2,c),CN1)),Integrate(Tanh(Plus(a,Times(b,x),Times(c,Sqr(x)))),x),x),x)),FreeQ(List(a,b,c,d,e),x))),
IIntegrate(5958,Integrate(Times(Coth(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),Plus(d_DEFAULT,Times(e_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Simp(Times(e,Log(Sinh(Plus(a,Times(b,x),Times(c,Sqr(x))))),Power(Times(C2,c),CN1)),x),Simp(Dist(Times(Subtract(Times(C2,c,d),Times(b,e)),Power(Times(C2,c),CN1)),Integrate(Coth(Plus(a,Times(b,x),Times(c,Sqr(x)))),x),x),x)),FreeQ(List(a,b,c,d,e),x))),
IIntegrate(5959,Integrate(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT),Power(Tanh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_DEFAULT)),x_Symbol),
    Condition(Simp(Integral(Times(Power(Plus(d,Times(e,x)),m),Power(Tanh(Plus(a,Times(b,x),Times(c,Sqr(x)))),n)),x),x),FreeQ(List(a,b,c,d,e,m,n),x))),
IIntegrate(5960,Integrate(Times(Power(Coth(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Integral(Times(Power(Plus(d,Times(e,x)),m),Power(Coth(Plus(a,Times(b,x),Times(c,Sqr(x)))),n)),x),x),FreeQ(List(a,b,c,d,e,m,n),x)))
  );
}
