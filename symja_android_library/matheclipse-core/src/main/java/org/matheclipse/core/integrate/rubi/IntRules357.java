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
class IntRules357 { 
  public static IAST RULES = List( 
IIntegrate(7141,Integrate(Times(Power(Factorial(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),PolyGamma(C0,Plus(c_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Simp(Times(Power(Factorial(Plus(a,Times(b,x))),n),Power(Times(b,n),CN1)),x),And(FreeQ(List(a,b,c,n),x),EqQ(c,Plus(a,C1))))),
IIntegrate(7142,Integrate(Gamma(p_,Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(x,Gamma(p,Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),x),Simp(Dist(Times(b,d,n,Power(Exp(Times(a,d)),CN1)),Integrate(Times(Power(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))),Plus(p,CN1)),Power(Power(Times(c,Power(x,n)),Times(b,d)),CN1)),x),x),x)),FreeQ(List(a,b,c,d,n,p),x))),
IIntegrate(7143,Integrate(Times(Gamma(p_,Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),Power(x_,CN1)),x_Symbol),
    Condition(Simp(Dist(Power(n,CN1),Subst(Gamma(p,Times(d,Plus(a,Times(b,x)))),x,Log(Times(c,Power(x,n)))),x),x),FreeQ(List(a,b,c,d,n,p),x))),
IIntegrate(7144,Integrate(Times(Gamma(p_,Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Times(e,x),Plus(m,C1)),Gamma(p,Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Times(e,Plus(m,C1)),CN1)),x),Simp(Dist(Times(b,d,n,Power(Times(e,x),Times(b,d,n)),Power(Times(Plus(m,C1),Power(Times(c,Power(x,n)),Times(b,d))),CN1),Power(Exp(Times(a,d)),CN1)),Integrate(Times(Power(Times(e,x),Subtract(m,Times(b,d,n))),Power(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n)))))),Plus(p,CN1))),x),x),x)),And(FreeQ(List(a,b,c,d,e,m,n,p),x),NeQ(m,CN1)))),
IIntegrate(7145,Integrate(Gamma(p_,Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,x_)),n_DEFAULT))),b_DEFAULT)),f_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(e,CN1),Subst(Integrate(Gamma(p,Times(f,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),x),x,Plus(d,Times(e,x))),x),x),FreeQ(List(a,b,c,d,e,f,n,p),x))),
IIntegrate(7146,Integrate(Times(Gamma(p_,Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,x_)),n_DEFAULT))),b_DEFAULT)),f_DEFAULT)),Power(Plus(g_,Times(h_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(e,CN1),Subst(Integrate(Times(Power(Times(g,x,Power(d,CN1)),m),Gamma(p,Times(f,Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),x),x,Plus(d,Times(e,x))),x),x),And(FreeQ(List(a,b,c,d,e,f,g,h,m,n,p),x),EqQ(Subtract(Times(e,g),Times(d,h)),C0)))),
IIntegrate(7147,Integrate(Zeta(C2,Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Integrate(PolyGamma(C1,Plus(a,Times(b,x))),x),FreeQ(list(a,b),x))),
IIntegrate(7148,Integrate(Zeta(s_,Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Simp(Times(CN1,Zeta(Plus(s,CN1),Plus(a,Times(b,x))),Power(Times(b,Plus(s,CN1)),CN1)),x),And(FreeQ(list(a,b,s),x),NeQ(s,C1),NeQ(s,C2)))),
IIntegrate(7149,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Zeta(C2,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Integrate(Times(Power(Plus(c,Times(d,x)),m),PolyGamma(C1,Plus(a,Times(b,x)))),x),And(FreeQ(List(a,b,c,d),x),RationalQ(m)))),
IIntegrate(7150,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Zeta(s_,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Power(Plus(c,Times(d,x)),m),Zeta(Plus(s,CN1),Plus(a,Times(b,x))),Power(Times(b,Plus(s,CN1)),CN1)),x),Simp(Dist(Times(d,m,Power(Times(b,Plus(s,CN1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(m,CN1)),Zeta(Plus(s,CN1),Plus(a,Times(b,x)))),x),x),x)),And(FreeQ(List(a,b,c,d,s),x),NeQ(s,C1),NeQ(s,C2),GtQ(m,C0)))),
IIntegrate(7151,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Zeta(s_,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Zeta(s,Plus(a,Times(b,x))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Dist(Times(b,s,Power(Times(d,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Zeta(Plus(s,C1),Plus(a,Times(b,x)))),x),x),x)),And(FreeQ(List(a,b,c,d,s),x),NeQ(s,C1),NeQ(s,C2),LtQ(m,CN1)))),
IIntegrate(7152,Integrate(PolyLog(n_,Times(a_DEFAULT,Power(Times(b_DEFAULT,Power(x_,p_DEFAULT)),q_DEFAULT))),x_Symbol),
    Condition(Subtract(Simp(Times(x,PolyLog(n,Times(a,Power(Times(b,Power(x,p)),q)))),x),Simp(Dist(Times(p,q),Integrate(PolyLog(Plus(n,CN1),Times(a,Power(Times(b,Power(x,p)),q))),x),x),x)),And(FreeQ(List(a,b,p,q),x),GtQ(n,C0)))),
IIntegrate(7153,Integrate(PolyLog(n_,Times(a_DEFAULT,Power(Times(b_DEFAULT,Power(x_,p_DEFAULT)),q_DEFAULT))),x_Symbol),
    Condition(Subtract(Simp(Times(x,PolyLog(Plus(n,C1),Times(a,Power(Times(b,Power(x,p)),q))),Power(Times(p,q),CN1)),x),Simp(Dist(Power(Times(p,q),CN1),Integrate(PolyLog(Plus(n,C1),Times(a,Power(Times(b,Power(x,p)),q))),x),x),x)),And(FreeQ(List(a,b,p,q),x),LtQ(n,CN1)))),
IIntegrate(7154,Integrate(PolyLog(n_,Times(a_DEFAULT,Power(Times(b_DEFAULT,Power(x_,p_DEFAULT)),q_DEFAULT))),x_Symbol),
    Condition(Unintegrable(PolyLog(n,Times(a,Power(Times(b,Power(x,p)),q))),x),FreeQ(List(a,b,n,p,q),x))),
IIntegrate(7155,Integrate(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),CN1),PolyLog(n_,Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),p_DEFAULT)))),x_Symbol),
    Condition(Simp(Times(PolyLog(Plus(n,C1),Times(c,Power(Plus(a,Times(b,x)),p))),Power(Times(e,p),CN1)),x),And(FreeQ(List(a,b,c,d,e,n,p),x),EqQ(Times(b,d),Times(a,e))))),
IIntegrate(7156,Integrate(Times(Power(x_,CN1),PolyLog(n_,Times(a_DEFAULT,Power(Times(b_DEFAULT,Power(x_,p_DEFAULT)),q_DEFAULT)))),x_Symbol),
    Condition(Simp(Times(PolyLog(Plus(n,C1),Times(a,Power(Times(b,Power(x,p)),q))),Power(Times(p,q),CN1)),x),FreeQ(List(a,b,n,p,q),x))),
IIntegrate(7157,Integrate(Times(Power(Times(d_DEFAULT,x_),m_DEFAULT),PolyLog(n_,Times(a_DEFAULT,Power(Times(b_DEFAULT,Power(x_,p_DEFAULT)),q_DEFAULT)))),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(d,x),Plus(m,C1)),PolyLog(n,Times(a,Power(Times(b,Power(x,p)),q))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Dist(Times(p,q,Power(Plus(m,C1),CN1)),Integrate(Times(Power(Times(d,x),m),PolyLog(Plus(n,CN1),Times(a,Power(Times(b,Power(x,p)),q)))),x),x),x)),And(FreeQ(List(a,b,d,m,p,q),x),NeQ(m,CN1),GtQ(n,C0)))),
IIntegrate(7158,Integrate(Times(Power(Times(d_DEFAULT,x_),m_DEFAULT),PolyLog(n_,Times(a_DEFAULT,Power(Times(b_DEFAULT,Power(x_,p_DEFAULT)),q_DEFAULT)))),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(d,x),Plus(m,C1)),PolyLog(Plus(n,C1),Times(a,Power(Times(b,Power(x,p)),q))),Power(Times(d,p,q),CN1)),x),Simp(Dist(Times(Plus(m,C1),Power(Times(p,q),CN1)),Integrate(Times(Power(Times(d,x),m),PolyLog(Plus(n,C1),Times(a,Power(Times(b,Power(x,p)),q)))),x),x),x)),And(FreeQ(List(a,b,d,m,p,q),x),NeQ(m,CN1),LtQ(n,CN1)))),
IIntegrate(7159,Integrate(Times(Power(Times(d_DEFAULT,x_),m_DEFAULT),PolyLog(n_,Times(a_DEFAULT,Power(Times(b_DEFAULT,Power(x_,p_DEFAULT)),q_DEFAULT)))),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(d,x),m),PolyLog(n,Times(a,Power(Times(b,Power(x,p)),q)))),x),FreeQ(List(a,b,d,m,n,p,q),x))),
IIntegrate(7160,Integrate(Times(Power(Log(Times(c_DEFAULT,Power(x_,m_DEFAULT))),r_DEFAULT),Power(x_,CN1),PolyLog(n_,Times(a_DEFAULT,Power(Times(b_DEFAULT,Power(x_,p_DEFAULT)),q_DEFAULT)))),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Log(Times(c,Power(x,m))),r),PolyLog(Plus(n,C1),Times(a,Power(Times(b,Power(x,p)),q))),Power(Times(p,q),CN1)),x),Simp(Dist(Times(m,r,Power(Times(p,q),CN1)),Integrate(Times(Power(Log(Times(c,Power(x,m))),Plus(r,CN1)),PolyLog(Plus(n,C1),Times(a,Power(Times(b,Power(x,p)),q))),Power(x,CN1)),x),x),x)),And(FreeQ(List(a,b,c,m,n,q,r),x),GtQ(r,C0))))
  );
}
