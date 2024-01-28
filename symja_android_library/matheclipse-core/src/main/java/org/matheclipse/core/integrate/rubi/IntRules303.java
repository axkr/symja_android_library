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
class IntRules303 { 
  public static IAST RULES = List( 
IIntegrate(6061,Integrate(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Sinh(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(Power(Times(e,x),Plus(m,C1)),Power(Times(e,n,Power(Times(c,Power(x,n)),Times(Plus(m,C1),Power(n,CN1)))),CN1)),Subst(Integrate(Times(Power(x,Subtract(Times(Plus(m,C1),Power(n,CN1)),C1)),Power(Sinh(Times(d,Plus(a,Times(b,Log(x))))),p)),x),x,Times(c,Power(x,n)))),x),And(FreeQ(List(a,b,c,d,e,m,n,p),x),Or(NeQ(c,C1),NeQ(n,C1))))),
IIntegrate(6062,Integrate(Times(Power(Cosh(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(Power(Times(e,x),Plus(m,C1)),Power(Times(e,n,Power(Times(c,Power(x,n)),Times(Plus(m,C1),Power(n,CN1)))),CN1)),Subst(Integrate(Times(Power(x,Subtract(Times(Plus(m,C1),Power(n,CN1)),C1)),Power(Cosh(Times(d,Plus(a,Times(b,Log(x))))),p)),x),x,Times(c,Power(x,n)))),x),And(FreeQ(List(a,b,c,d,e,m,n,p),x),Or(NeQ(c,C1),NeQ(n,C1))))),
IIntegrate(6063,Integrate(Times(Power(Times(Plus(e_DEFAULT,Times(Log(Times(g_DEFAULT,Power(x_,m_DEFAULT))),f_DEFAULT)),h_DEFAULT),q_DEFAULT),Sinh(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT))),x_Symbol),
    Condition(Plus(Simp(Star(Times(CN1,Exp(Times(CN1,a,d)),Power(Times(Power(Times(c,Power(x,n)),Times(b,d)),C2,Power(Power(x,Times(b,d,n)),CN1)),CN1)),Integrate(Times(Power(Times(h,Plus(e,Times(f,Log(Times(g,Power(x,m)))))),q),Power(Power(x,Times(b,d,n)),CN1)),x)),x),Simp(Star(Times(Exp(Times(a,d)),Power(Times(c,Power(x,n)),Times(b,d)),Power(Times(C2,Power(x,Times(b,d,n))),CN1)),Integrate(Times(Power(x,Times(b,d,n)),Power(Times(h,Plus(e,Times(f,Log(Times(g,Power(x,m)))))),q)),x)),x)),FreeQ(List(a,b,c,d,e,f,g,h,m,n,q),x))),
IIntegrate(6064,Integrate(Times(Cosh(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),Power(Times(Plus(e_DEFAULT,Times(Log(Times(g_DEFAULT,Power(x_,m_DEFAULT))),f_DEFAULT)),h_DEFAULT),q_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Star(Times(Power(Times(Power(Times(c,Power(x,n)),Times(b,d)),C2,Power(Power(x,Times(b,d,n)),CN1)),CN1),Power(Exp(Times(a,d)),CN1)),Integrate(Times(Power(Times(h,Plus(e,Times(f,Log(Times(g,Power(x,m)))))),q),Power(Power(x,Times(b,d,n)),CN1)),x)),x),Simp(Star(Times(Exp(Times(a,d)),Power(Times(c,Power(x,n)),Times(b,d)),Power(Times(C2,Power(x,Times(b,d,n))),CN1)),Integrate(Times(Power(x,Times(b,d,n)),Power(Times(h,Plus(e,Times(f,Log(Times(g,Power(x,m)))))),q)),x)),x)),FreeQ(List(a,b,c,d,e,f,g,h,m,n,q),x))),
IIntegrate(6065,Integrate(Times(Power(Times(Plus(e_DEFAULT,Times(Log(Times(g_DEFAULT,Power(x_,m_DEFAULT))),f_DEFAULT)),h_DEFAULT),q_DEFAULT),Power(Times(i_DEFAULT,x_),r_DEFAULT),Sinh(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT))),x_Symbol),
    Condition(Plus(Simp(Star(Times(CN1,Exp(Times(CN1,a,d)),Power(Times(i,x),r),Power(Times(Power(Times(c,Power(x,n)),Times(b,d)),C2,Power(x,Subtract(r,Times(b,d,n)))),CN1)),Integrate(Times(Power(x,Subtract(r,Times(b,d,n))),Power(Times(h,Plus(e,Times(f,Log(Times(g,Power(x,m)))))),q)),x)),x),Simp(Star(Times(Exp(Times(a,d)),Power(Times(i,x),r),Power(Times(c,Power(x,n)),Times(b,d)),Power(Times(C2,Power(x,Plus(r,Times(b,d,n)))),CN1)),Integrate(Times(Power(x,Plus(r,Times(b,d,n))),Power(Times(h,Plus(e,Times(f,Log(Times(g,Power(x,m)))))),q)),x)),x)),FreeQ(List(a,b,c,d,e,f,g,h,i,m,n,q,r),x))),
IIntegrate(6066,Integrate(Times(Cosh(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),Power(Times(Plus(e_DEFAULT,Times(Log(Times(g_DEFAULT,Power(x_,m_DEFAULT))),f_DEFAULT)),h_DEFAULT),q_DEFAULT),Power(Times(i_DEFAULT,x_),r_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Star(Times(Power(Times(i,x),r),Power(Times(Power(Times(c,Power(x,n)),Times(b,d)),C2,Power(x,Subtract(r,Times(b,d,n)))),CN1),Power(Exp(Times(a,d)),CN1)),Integrate(Times(Power(x,Subtract(r,Times(b,d,n))),Power(Times(h,Plus(e,Times(f,Log(Times(g,Power(x,m)))))),q)),x)),x),Simp(Star(Times(Exp(Times(a,d)),Power(Times(i,x),r),Power(Times(c,Power(x,n)),Times(b,d)),Power(Times(C2,Power(x,Plus(r,Times(b,d,n)))),CN1)),Integrate(Times(Power(x,Plus(r,Times(b,d,n))),Power(Times(h,Plus(e,Times(f,Log(Times(g,Power(x,m)))))),q)),x)),x)),FreeQ(List(a,b,c,d,e,f,g,h,i,m,n,q,r),x))),
IIntegrate(6067,Integrate(Power(Tanh(Times(Plus(a_DEFAULT,Times(Log(x_),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Integrate(Times(Power(Plus(CN1,Times(Exp(Times(C2,a,d)),Power(x,Times(C2,b,d)))),p),Power(Power(Plus(C1,Times(Exp(Times(C2,a,d)),Power(x,Times(C2,b,d)))),p),CN1)),x),FreeQ(List(a,b,d,p),x))),
IIntegrate(6068,Integrate(Power(Coth(Times(Plus(a_DEFAULT,Times(Log(x_),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Integrate(Times(Power(Subtract(CN1,Times(Exp(Times(C2,a,d)),Power(x,Times(C2,b,d)))),p),Power(Power(Subtract(C1,Times(Exp(Times(C2,a,d)),Power(x,Times(C2,b,d)))),p),CN1)),x),FreeQ(List(a,b,d,p),x))),
IIntegrate(6069,Integrate(Power(Tanh(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Star(Times(x,Power(Times(n,Power(Times(c,Power(x,n)),Power(n,CN1))),CN1)),Subst(Integrate(Times(Power(x,Subtract(Power(n,CN1),C1)),Power(Tanh(Times(d,Plus(a,Times(b,Log(x))))),p)),x),x,Times(c,Power(x,n)))),x),And(FreeQ(List(a,b,c,d,n,p),x),Or(NeQ(c,C1),NeQ(n,C1))))),
IIntegrate(6070,Integrate(Power(Coth(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Star(Times(x,Power(Times(n,Power(Times(c,Power(x,n)),Power(n,CN1))),CN1)),Subst(Integrate(Times(Power(x,Subtract(Power(n,CN1),C1)),Power(Coth(Times(d,Plus(a,Times(b,Log(x))))),p)),x),x,Times(c,Power(x,n)))),x),And(FreeQ(List(a,b,c,d,n,p),x),Or(NeQ(c,C1),NeQ(n,C1))))),
IIntegrate(6071,Integrate(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Tanh(Times(Plus(a_DEFAULT,Times(Log(x_),b_DEFAULT)),d_DEFAULT)),p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Times(e,x),m),Power(Plus(CN1,Times(Exp(Times(C2,a,d)),Power(x,Times(C2,b,d)))),p),Power(Power(Plus(C1,Times(Exp(Times(C2,a,d)),Power(x,Times(C2,b,d)))),p),CN1)),x),FreeQ(List(a,b,d,e,m,p),x))),
IIntegrate(6072,Integrate(Times(Power(Coth(Times(Plus(a_DEFAULT,Times(Log(x_),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Times(e,x),m),Power(Subtract(CN1,Times(Exp(Times(C2,a,d)),Power(x,Times(C2,b,d)))),p),Power(Power(Subtract(C1,Times(Exp(Times(C2,a,d)),Power(x,Times(C2,b,d)))),p),CN1)),x),FreeQ(List(a,b,d,e,m,p),x))),
IIntegrate(6073,Integrate(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Tanh(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(Power(Times(e,x),Plus(m,C1)),Power(Times(e,n,Power(Times(c,Power(x,n)),Times(Plus(m,C1),Power(n,CN1)))),CN1)),Subst(Integrate(Times(Power(x,Subtract(Times(Plus(m,C1),Power(n,CN1)),C1)),Power(Tanh(Times(d,Plus(a,Times(b,Log(x))))),p)),x),x,Times(c,Power(x,n)))),x),And(FreeQ(List(a,b,c,d,e,m,n,p),x),Or(NeQ(c,C1),NeQ(n,C1))))),
IIntegrate(6074,Integrate(Times(Power(Coth(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(Power(Times(e,x),Plus(m,C1)),Power(Times(e,n,Power(Times(c,Power(x,n)),Times(Plus(m,C1),Power(n,CN1)))),CN1)),Subst(Integrate(Times(Power(x,Subtract(Times(Plus(m,C1),Power(n,CN1)),C1)),Power(Coth(Times(d,Plus(a,Times(b,Log(x))))),p)),x),x,Times(c,Power(x,n)))),x),And(FreeQ(List(a,b,c,d,e,m,n,p),x),Or(NeQ(c,C1),NeQ(n,C1))))),
IIntegrate(6075,Integrate(Power(Sech(Times(Plus(a_DEFAULT,Times(Log(x_),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Star(Times(Power(C2,p),Power(Exp(Times(a,d,p)),CN1)),Integrate(Power(Times(Power(x,Times(b,d,p)),Power(Plus(C1,Power(Times(Exp(Times(C2,a,d)),Power(x,Times(C2,b,d))),CN1)),p)),CN1),x)),x),And(FreeQ(list(a,b,d),x),IntegerQ(p)))),
IIntegrate(6076,Integrate(Power(Csch(Times(Plus(a_DEFAULT,Times(Log(x_),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Star(Times(Power(C2,p),Power(Exp(Times(a,d,p)),CN1)),Integrate(Power(Times(Power(x,Times(b,d,p)),Power(Subtract(C1,Power(Times(Exp(Times(C2,a,d)),Power(x,Times(C2,b,d))),CN1)),p)),CN1),x)),x),And(FreeQ(list(a,b,d),x),IntegerQ(p)))),
IIntegrate(6077,Integrate(Power(Sech(Times(Plus(a_DEFAULT,Times(Log(x_),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Star(Times(Power(Sech(Times(d,Plus(a,Times(b,Log(x))))),p),Power(Plus(C1,Power(Times(Exp(Times(C2,a,d)),Power(x,Times(C2,b,d))),CN1)),p),Power(Power(x,Times(CN1,b,d,p)),CN1)),Integrate(Power(Times(Power(x,Times(b,d,p)),Power(Plus(C1,Power(Times(Exp(Times(C2,a,d)),Power(x,Times(C2,b,d))),CN1)),p)),CN1),x)),x),And(FreeQ(List(a,b,d,p),x),Not(IntegerQ(p))))),
IIntegrate(6078,Integrate(Power(Csch(Times(Plus(a_DEFAULT,Times(Log(x_),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Star(Times(Power(Csch(Times(d,Plus(a,Times(b,Log(x))))),p),Power(Subtract(C1,Power(Times(Exp(Times(C2,a,d)),Power(x,Times(C2,b,d))),CN1)),p),Power(Power(x,Times(CN1,b,d,p)),CN1)),Integrate(Power(Times(Power(x,Times(b,d,p)),Power(Subtract(C1,Power(Times(Exp(Times(C2,a,d)),Power(x,Times(C2,b,d))),CN1)),p)),CN1),x)),x),And(FreeQ(List(a,b,d,p),x),Not(IntegerQ(p))))),
IIntegrate(6079,Integrate(Power(Sech(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Star(Times(x,Power(Times(n,Power(Times(c,Power(x,n)),Power(n,CN1))),CN1)),Subst(Integrate(Times(Power(x,Subtract(Power(n,CN1),C1)),Power(Sech(Times(d,Plus(a,Times(b,Log(x))))),p)),x),x,Times(c,Power(x,n)))),x),And(FreeQ(List(a,b,c,d,n,p),x),Or(NeQ(c,C1),NeQ(n,C1))))),
IIntegrate(6080,Integrate(Power(Csch(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Star(Times(x,Power(Times(n,Power(Times(c,Power(x,n)),Power(n,CN1))),CN1)),Subst(Integrate(Times(Power(x,Subtract(Power(n,CN1),C1)),Power(Csch(Times(d,Plus(a,Times(b,Log(x))))),p)),x),x,Times(c,Power(x,n)))),x),And(FreeQ(List(a,b,c,d,n,p),x),Or(NeQ(c,C1),NeQ(n,C1)))))
  );
}
