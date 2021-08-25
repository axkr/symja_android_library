package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;
import org.matheclipse.core.interfaces.IAST;

/** 
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class IntRules328 { 
  public static IAST RULES = List( 
IIntegrate(6561,Integrate(Times(Gamma(n_,Times(b_DEFAULT,x_)),Power(x_,CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(Gamma(n),Log(x)),x),Simp(Times(Power(Times(b,x),n),HypergeometricPFQ(List(n,n),List(Plus(C1,n),Plus(C1,n)),Times(CN1,b,x)),Power(n,CN2)),x)),And(FreeQ(List(b,n),x),Not(IntegerQ(n))))),
IIntegrate(6562,Integrate(Times(Gamma(n_,Times(b_DEFAULT,x_)),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(d,x),Plus(m,C1)),Gamma(n,Times(b,x)),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Times(Power(Times(d,x),m),Gamma(Plus(m,n,C1),Times(b,x)),Power(Times(b,Plus(m,C1),Power(Times(b,x),m)),CN1)),x)),And(FreeQ(List(b,d,m,n),x),NeQ(m,CN1)))),
IIntegrate(6563,Integrate(Times(Gamma(n_,Plus(a_,Times(b_DEFAULT,x_))),Power(Plus(c_,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Dist(Power(b,CN1),Subst(Integrate(Times(Power(Times(d,x,Power(b,CN1)),m),Gamma(n,x)),x),x,Plus(a,Times(b,x))),x),And(FreeQ(List(a,b,c,d,m,n),x),EqQ(Subtract(Times(b,c),Times(a,d)),C0)))),
IIntegrate(6564,Integrate(Times(Gamma(n_,Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Plus(Integrate(Times(Power(Plus(a,Times(b,x)),Subtract(n,C1)),Power(Times(Plus(c,Times(d,x)),Exp(Plus(a,Times(b,x)))),CN1)),x),Dist(Subtract(n,C1),Integrate(Times(Gamma(Subtract(n,C1),Plus(a,Times(b,x))),Power(Plus(c,Times(d,x)),CN1)),x),x)),And(FreeQ(List(a,b,c,d),x),IGtQ(n,C1)))),
IIntegrate(6565,Integrate(Times(Gamma(n_,Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Block(List(Set(False,True)),Plus(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Gamma(n,Plus(a,Times(b,x))),Power(Times(d,Plus(m,C1)),CN1)),x),Dist(Times(b,Power(Times(d,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Power(Plus(a,Times(b,x)),Subtract(n,C1)),Power(Exp(Plus(a,Times(b,x))),CN1)),x),x))),And(FreeQ(List(a,b,c,d,m,n),x),Or(IGtQ(m,C0),IGtQ(n,C0),IntegersQ(m,n)),NeQ(m,CN1)))),
IIntegrate(6566,Integrate(Times(Gamma(n_,Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(c,Times(d,x)),m),Gamma(n,Plus(a,Times(b,x)))),x),FreeQ(List(a,b,c,d,m,n),x))),
IIntegrate(6567,Integrate(LogGamma(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Simp(Times(PolyGamma(CN2,Plus(a,Times(b,x))),Power(b,CN1)),x),FreeQ(List(a,b),x))),
IIntegrate(6568,Integrate(Times(LogGamma(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),m),PolyGamma(CN2,Plus(a,Times(b,x))),Power(b,CN1)),x),Dist(Times(d,m,Power(b,CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Subtract(m,C1)),PolyGamma(CN2,Plus(a,Times(b,x)))),x),x)),And(FreeQ(List(a,b,c,d),x),IGtQ(m,C0)))),
IIntegrate(6569,Integrate(Times(LogGamma(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(c,Times(d,x)),m),LogGamma(Plus(a,Times(b,x)))),x),FreeQ(List(a,b,c,d,m),x))),
IIntegrate(6570,Integrate(PolyGamma(n_,Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Simp(Times(PolyGamma(Subtract(n,C1),Plus(a,Times(b,x))),Power(b,CN1)),x),FreeQ(List(a,b,n),x))),
IIntegrate(6571,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),PolyGamma(n_,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),m),PolyGamma(Subtract(n,C1),Plus(a,Times(b,x))),Power(b,CN1)),x),Dist(Times(d,m,Power(b,CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Subtract(m,C1)),PolyGamma(Subtract(n,C1),Plus(a,Times(b,x)))),x),x)),And(FreeQ(List(a,b,c,d,n),x),GtQ(m,C0)))),
IIntegrate(6572,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),PolyGamma(n_,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),PolyGamma(n,Plus(a,Times(b,x))),Power(Times(d,Plus(m,C1)),CN1)),x),Dist(Times(b,Power(Times(d,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),PolyGamma(Plus(n,C1),Plus(a,Times(b,x)))),x),x)),And(FreeQ(List(a,b,c,d,n),x),LtQ(m,CN1)))),
IIntegrate(6573,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),PolyGamma(n_,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(c,Times(d,x)),m),PolyGamma(n,Plus(a,Times(b,x)))),x),FreeQ(List(a,b,c,d,m,n),x))),
IIntegrate(6574,Integrate(Times(Power(Gamma(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),PolyGamma(C0,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Simp(Times(Power(Gamma(Plus(a,Times(b,x))),n),Power(Times(b,n),CN1)),x),FreeQ(List(a,b,n),x))),
IIntegrate(6575,Integrate(Times(Power(Factorial(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),PolyGamma(C0,Plus(c_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Simp(Times(Power(Factorial(Plus(a,Times(b,x))),n),Power(Times(b,n),CN1)),x),And(FreeQ(List(a,b,c,n),x),EqQ(c,Plus(a,C1))))),
IIntegrate(6576,Integrate(Gamma(p_,Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(x,Gamma(p,Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),x),Dist(Times(b,d,n,Power(Exp(Times(a,d)),CN1)),Integrate(Times(Power(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))),Subtract(p,C1)),Power(Power(Times(c,Power(x,n)),Times(b,d)),CN1)),x),x)),FreeQ(List(a,b,c,d,n,p),x))),
IIntegrate(6577,Integrate(Times(Gamma(p_,Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),Power(x_,CN1)),x_Symbol),
    Condition(Dist(Power(n,CN1),Subst(Gamma(p,Times(d,Plus(a,Times(b,x)))),x,Log(Times(c,Power(x,n)))),x),FreeQ(List(a,b,c,d,n,p),x))),
IIntegrate(6578,Integrate(Times(Gamma(p_,Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Times(e,x),Plus(m,C1)),Gamma(p,Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Times(e,Plus(m,C1)),CN1)),x),Dist(Times(b,d,n,Power(Times(e,x),Times(b,d,n)),Power(Times(Exp(Times(a,d)),Plus(m,C1),Power(Times(c,Power(x,n)),Times(b,d))),CN1)),Integrate(Times(Power(Times(e,x),Subtract(m,Times(b,d,n))),Power(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))),Subtract(p,C1))),x),x)),And(FreeQ(List(a,b,c,d,e,m,n,p),x),NeQ(m,CN1)))),
IIntegrate(6579,Integrate(Gamma(p_,Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,x_)),n_DEFAULT))),b_DEFAULT)),f_DEFAULT)),x_Symbol),
    Condition(Dist(Power(e,CN1),Subst(Integrate(Gamma(p,Times(f,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),x),x,Plus(d,Times(e,x))),x),FreeQ(List(a,b,c,d,e,f,n,p),x))),
IIntegrate(6580,Integrate(Times(Gamma(p_,Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,x_)),n_DEFAULT))),b_DEFAULT)),f_DEFAULT)),Power(Plus(g_,Times(h_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Dist(Power(e,CN1),Subst(Integrate(Times(Power(Times(g,x,Power(d,CN1)),m),Gamma(p,Times(f,Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),x),x,Plus(d,Times(e,x))),x),And(FreeQ(List(a,b,c,d,e,f,g,h,m,n,p),x),EqQ(Subtract(Times(e,g),Times(d,h)),C0))))
  );
}
