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
class IntRules356 { 
  public static IAST RULES = List( 
IIntegrate(7121,Integrate(LogGamma(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Simp(Times(PolyGamma(CN2,Plus(a,Times(b,x))),Power(b,CN1)),x),FreeQ(list(a,b),x))),
IIntegrate(7122,Integrate(Times(LogGamma(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),m),PolyGamma(CN2,Plus(a,Times(b,x))),Power(b,CN1)),x),Simp(Star(Times(d,m,Power(b,CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Subtract(m,C1)),PolyGamma(CN2,Plus(a,Times(b,x)))),x)),x)),And(FreeQ(List(a,b,c,d),x),IGtQ(m,C0)))),
IIntegrate(7123,Integrate(Times(LogGamma(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(c,Times(d,x)),m),LogGamma(Plus(a,Times(b,x)))),x),FreeQ(List(a,b,c,d,m),x))),
IIntegrate(7124,Integrate(PolyGamma(n_,Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Simp(Times(PolyGamma(Subtract(n,C1),Plus(a,Times(b,x))),Power(b,CN1)),x),FreeQ(list(a,b,n),x))),
IIntegrate(7125,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),PolyGamma(n_,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),m),PolyGamma(Subtract(n,C1),Plus(a,Times(b,x))),Power(b,CN1)),x),Simp(Star(Times(d,m,Power(b,CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Subtract(m,C1)),PolyGamma(Subtract(n,C1),Plus(a,Times(b,x)))),x)),x)),And(FreeQ(List(a,b,c,d,n),x),GtQ(m,C0)))),
IIntegrate(7126,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),PolyGamma(n_,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),PolyGamma(n,Plus(a,Times(b,x))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,Power(Times(d,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),PolyGamma(Plus(n,C1),Plus(a,Times(b,x)))),x)),x)),And(FreeQ(List(a,b,c,d,n),x),LtQ(m,CN1)))),
IIntegrate(7127,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),PolyGamma(n_,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(c,Times(d,x)),m),PolyGamma(n,Plus(a,Times(b,x)))),x),FreeQ(List(a,b,c,d,m,n),x))),
IIntegrate(7128,Integrate(Times(Power(Gamma(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),PolyGamma(C0,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Simp(Times(Power(Gamma(Plus(a,Times(b,x))),n),Power(Times(b,n),CN1)),x),FreeQ(list(a,b,n),x))),
IIntegrate(7129,Integrate(Times(Power(Factorial(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),PolyGamma(C0,Plus(c_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Simp(Times(Power(Factorial(Plus(a,Times(b,x))),n),Power(Times(b,n),CN1)),x),And(FreeQ(List(a,b,c,n),x),EqQ(c,Plus(a,C1))))),
IIntegrate(7130,Integrate(Gamma(p_,Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(x,Gamma(p,Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),x),Simp(Star(Times(b,d,n,Power(Exp(Times(a,d)),CN1)),Integrate(Times(Power(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))),Subtract(p,C1)),Power(Power(Times(c,Power(x,n)),Times(b,d)),CN1)),x)),x)),FreeQ(List(a,b,c,d,n,p),x))),
IIntegrate(7131,Integrate(Times(Gamma(p_,Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),Power(x_,CN1)),x_Symbol),
    Condition(Simp(Star(Power(n,CN1),Subst(Gamma(p,Times(d,Plus(a,Times(b,x)))),x,Log(Times(c,Power(x,n))))),x),FreeQ(List(a,b,c,d,n,p),x))),
IIntegrate(7132,Integrate(Times(Gamma(p_,Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Times(e,x),Plus(m,C1)),Gamma(p,Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Times(e,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,d,n,Power(Times(e,x),Times(b,d,n)),Power(Times(Plus(m,C1),Power(Times(c,Power(x,n)),Times(b,d))),CN1),Power(Exp(Times(a,d)),CN1)),Integrate(Times(Power(Times(e,x),Subtract(m,Times(b,d,n))),Power(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))),Subtract(p,C1))),x)),x)),And(FreeQ(List(a,b,c,d,e,m,n,p),x),NeQ(m,CN1)))),
IIntegrate(7133,Integrate(Gamma(p_,Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,x_)),n_DEFAULT))),b_DEFAULT)),f_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(e,CN1),Subst(Integrate(Gamma(p,Times(f,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),x),x,Plus(d,Times(e,x)))),x),FreeQ(List(a,b,c,d,e,f,n,p),x))),
IIntegrate(7134,Integrate(Times(Gamma(p_,Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,x_)),n_DEFAULT))),b_DEFAULT)),f_DEFAULT)),Power(Plus(g_,Times(h_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(e,CN1),Subst(Integrate(Times(Power(Times(g,x,Power(d,CN1)),m),Gamma(p,Times(f,Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),x),x,Plus(d,Times(e,x)))),x),And(FreeQ(List(a,b,c,d,e,f,g,h,m,n,p),x),EqQ(Subtract(Times(e,g),Times(d,h)),C0)))),
IIntegrate(7135,Integrate(Zeta(C2,Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Integrate(PolyGamma(C1,Plus(a,Times(b,x))),x),FreeQ(list(a,b),x))),
IIntegrate(7136,Integrate(Zeta(s_,Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Simp(Times(CN1,Zeta(Subtract(s,C1),Plus(a,Times(b,x))),Power(Times(b,Subtract(s,C1)),CN1)),x),And(FreeQ(list(a,b,s),x),NeQ(s,C1),NeQ(s,C2)))),
IIntegrate(7137,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Zeta(C2,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Integrate(Times(Power(Plus(c,Times(d,x)),m),PolyGamma(C1,Plus(a,Times(b,x)))),x),And(FreeQ(List(a,b,c,d),x),RationalQ(m)))),
IIntegrate(7138,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Zeta(s_,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Power(Plus(c,Times(d,x)),m),Zeta(Subtract(s,C1),Plus(a,Times(b,x))),Power(Times(b,Subtract(s,C1)),CN1)),x),Simp(Star(Times(d,m,Power(Times(b,Subtract(s,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Subtract(m,C1)),Zeta(Subtract(s,C1),Plus(a,Times(b,x)))),x)),x)),And(FreeQ(List(a,b,c,d,s),x),NeQ(s,C1),NeQ(s,C2),GtQ(m,C0)))),
IIntegrate(7139,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Zeta(s_,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Zeta(s,Plus(a,Times(b,x))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,s,Power(Times(d,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Zeta(Plus(s,C1),Plus(a,Times(b,x)))),x)),x)),And(FreeQ(List(a,b,c,d,s),x),NeQ(s,C1),NeQ(s,C2),LtQ(m,CN1)))),
IIntegrate(7140,Integrate(PolyLog(n_,Times(a_DEFAULT,Power(Times(b_DEFAULT,Power(x_,p_DEFAULT)),q_DEFAULT))),x_Symbol),
    Condition(Subtract(Simp(Times(x,PolyLog(n,Times(a,Power(Times(b,Power(x,p)),q)))),x),Simp(Star(Times(p,q),Integrate(PolyLog(Subtract(n,C1),Times(a,Power(Times(b,Power(x,p)),q))),x)),x)),And(FreeQ(List(a,b,p,q),x),GtQ(n,C0))))
  );
}
