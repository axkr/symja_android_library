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
class IntRules294 { 
  public static IAST RULES = List( 
IIntegrate(5881,Integrate(Times(Power(Times(e_,x_),m_),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(Power(e,IntPart(m)),Power(Times(e,x),FracPart(m)),Power(Power(x,FracPart(m)),CN1)),Integrate(Times(Power(x,m),Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,Power(x,n)))))),p)),x)),x),And(FreeQ(List(a,b,c,d,e,m,n),x),IntegerQ(p),NeQ(m,CN1),IGtQ(Simplify(Times(n,Power(Plus(m,C1),CN1))),C0),Not(IntegerQ(n))))),
IIntegrate(5882,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(Times(e_,x_),m_)),x_Symbol),
    Condition(Simp(Star(Times(Power(e,IntPart(m)),Power(Times(e,x),FracPart(m)),Power(Power(x,FracPart(m)),CN1)),Integrate(Times(Power(x,m),Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,Power(x,n)))))),p)),x)),x),And(FreeQ(List(a,b,c,d,e,m,n),x),IntegerQ(p),NeQ(m,CN1),IGtQ(Simplify(Times(n,Power(Plus(m,C1),CN1))),C0),Not(IntegerQ(n))))),
IIntegrate(5883,Integrate(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_))))),x_Symbol),
    Condition(Subtract(Simp(Star(C1D2,Integrate(Times(Power(Times(e,x),m),Exp(Plus(c,Times(d,Power(x,n))))),x)),x),Simp(Star(C1D2,Integrate(Times(Power(Times(e,x),m),Exp(Subtract(Negate(c),Times(d,Power(x,n))))),x)),x)),FreeQ(List(c,d,e,m,n),x))),
IIntegrate(5884,Integrate(Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Star(C1D2,Integrate(Times(Power(Times(e,x),m),Exp(Plus(c,Times(d,Power(x,n))))),x)),x),Simp(Star(C1D2,Integrate(Times(Power(Times(e,x),m),Exp(Subtract(Negate(c),Times(d,Power(x,n))))),x)),x)),FreeQ(List(c,d,e,m,n),x))),
IIntegrate(5885,Integrate(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_)),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Power(Times(e,x),m),Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,Power(x,n)))))),p),x),x),And(FreeQ(List(a,b,c,d,e,m,n),x),IGtQ(p,C0)))),
IIntegrate(5886,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Power(Times(e,x),m),Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,Power(x,n)))))),p),x),x),And(FreeQ(List(a,b,c,d,e,m,n),x),IGtQ(p,C0)))),
IIntegrate(5887,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(u_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(Power(Coefficient(u,x,C1),Plus(m,C1)),CN1),Subst(Integrate(Times(Power(Subtract(x,Coefficient(u,x,C0)),m),Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,Power(x,n)))))),p)),x),x,u)),x),And(FreeQ(List(a,b,c,d,n,p),x),LinearQ(u,x),NeQ(u,x),IntegerQ(m)))),
IIntegrate(5888,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(u_,n_)))),b_DEFAULT)),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(Power(Coefficient(u,x,C1),Plus(m,C1)),CN1),Subst(Integrate(Times(Power(Subtract(x,Coefficient(u,x,C0)),m),Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,Power(x,n)))))),p)),x),x,u)),x),And(FreeQ(List(a,b,c,d,n,p),x),LinearQ(u,x),NeQ(u,x),IntegerQ(m)))),
IIntegrate(5889,Integrate(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(u_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(e,x),m),Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,Power(u,n)))))),p)),x),And(FreeQ(List(a,b,c,d,e,m,n,p),x),LinearQ(u,x)))),
IIntegrate(5890,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(u_,n_)))),b_DEFAULT)),p_DEFAULT),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(e,x),m),Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,Power(u,n)))))),p)),x),And(FreeQ(List(a,b,c,d,e,m,n,p),x),LinearQ(u,x)))),
IIntegrate(5891,Integrate(Times(Power(Times(e_,x_),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(u_))),p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Times(e,x),m),Power(Plus(a,Times(b,Sinh(ExpandToSum(u,x)))),p)),x),And(FreeQ(List(a,b,e,m,p),x),BinomialQ(u,x),Not(BinomialMatchQ(u,x))))),
IIntegrate(5892,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cosh(u_),b_DEFAULT)),p_DEFAULT),Power(Times(e_,x_),m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Times(e,x),m),Power(Plus(a,Times(b,Cosh(ExpandToSum(u,x)))),p)),x),And(FreeQ(List(a,b,e,m,p),x),BinomialQ(u,x),Not(BinomialMatchQ(u,x))))),
IIntegrate(5893,Integrate(Times(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),Power(x_,m_DEFAULT),Power(Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Times(Power(Sinh(Plus(a,Times(b,Power(x,n)))),Plus(p,C1)),Power(Times(b,n,Plus(p,C1)),CN1)),x),And(FreeQ(List(a,b,m,n,p),x),EqQ(m,Subtract(n,C1)),NeQ(p,CN1)))),
IIntegrate(5894,Integrate(Times(Power(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_DEFAULT),Power(x_,m_DEFAULT),Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))))),x_Symbol),
    Condition(Simp(Times(Power(Cosh(Plus(a,Times(b,Power(x,n)))),Plus(p,C1)),Power(Times(b,n,Plus(p,C1)),CN1)),x),And(FreeQ(List(a,b,m,n,p),x),EqQ(m,Subtract(n,C1)),NeQ(p,CN1)))),
IIntegrate(5895,Integrate(Times(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),Power(x_,m_DEFAULT),Power(Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,Negate(n),C1)),Power(Sinh(Plus(a,Times(b,Power(x,n)))),Plus(p,C1)),Power(Times(b,n,Plus(p,C1)),CN1)),x),Simp(Star(Times(Plus(m,Negate(n),C1),Power(Times(b,n,Plus(p,C1)),CN1)),Integrate(Times(Power(x,Subtract(m,n)),Power(Sinh(Plus(a,Times(b,Power(x,n)))),Plus(p,C1))),x)),x)),And(FreeQ(list(a,b,p),x),LtQ(C0,n,Plus(m,C1)),NeQ(p,CN1)))),
IIntegrate(5896,Integrate(Times(Power(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT),Power(x_,m_DEFAULT),Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))))),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,Negate(n),C1)),Power(Cosh(Plus(a,Times(b,Power(x,n)))),Plus(p,C1)),Power(Times(b,n,Plus(p,C1)),CN1)),x),Simp(Star(Times(Plus(m,Negate(n),C1),Power(Times(b,n,Plus(p,C1)),CN1)),Integrate(Times(Power(x,Subtract(m,n)),Power(Cosh(Plus(a,Times(b,Power(x,n)))),Plus(p,C1))),x)),x)),And(FreeQ(list(a,b,p),x),LtQ(C0,n,Plus(m,C1)),NeQ(p,CN1)))),
IIntegrate(5897,Integrate(Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),x_Symbol),
    Condition(Subtract(Simp(Star(C1D2,Integrate(Exp(Plus(a,Times(b,x),Times(c,Sqr(x)))),x)),x),Simp(Star(C1D2,Integrate(Exp(Subtract(Subtract(Negate(a),Times(b,x)),Times(c,Sqr(x)))),x)),x)),FreeQ(list(a,b,c),x))),
IIntegrate(5898,Integrate(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),x_Symbol),
    Condition(Plus(Simp(Star(C1D2,Integrate(Exp(Plus(a,Times(b,x),Times(c,Sqr(x)))),x)),x),Simp(Star(C1D2,Integrate(Exp(Subtract(Subtract(Negate(a),Times(b,x)),Times(c,Sqr(x)))),x)),x)),FreeQ(list(a,b,c),x))),
IIntegrate(5899,Integrate(Power(Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Power(Sinh(Plus(a,Times(b,x),Times(c,Sqr(x)))),n),x),x),And(FreeQ(list(a,b,c),x),IGtQ(n,C1)))),
IIntegrate(5900,Integrate(Power(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Power(Cosh(Plus(a,Times(b,x),Times(c,Sqr(x)))),n),x),x),And(FreeQ(list(a,b,c),x),IGtQ(n,C1))))
  );
}
