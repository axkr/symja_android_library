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
class IntRules304 { 
  public static IAST RULES = List( 
IIntegrate(6081,Integrate(Power(Tanh(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Dist(Times(x,Power(Times(n,Power(Times(c,Power(x,n)),Power(n,CN1))),CN1)),Subst(Integrate(Times(Power(x,Plus(Power(n,CN1),CN1)),Power(Tanh(Times(d,Plus(a,Times(b,Log(x))))),p)),x),x,Times(c,Power(x,n))),x),x),And(FreeQ(List(a,b,c,d,n,p),x),Or(NeQ(c,C1),NeQ(n,C1))))),
IIntegrate(6082,Integrate(Power(Coth(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Dist(Times(x,Power(Times(n,Power(Times(c,Power(x,n)),Power(n,CN1))),CN1)),Subst(Integrate(Times(Power(x,Plus(Power(n,CN1),CN1)),Power(Coth(Times(d,Plus(a,Times(b,Log(x))))),p)),x),x,Times(c,Power(x,n))),x),x),And(FreeQ(List(a,b,c,d,n,p),x),Or(NeQ(c,C1),NeQ(n,C1))))),
IIntegrate(6083,Integrate(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Tanh(Times(Plus(a_DEFAULT,Times(Log(x_),b_DEFAULT)),d_DEFAULT)),p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Times(e,x),m),Power(Plus(CN1,Times(Exp(Times(C2,a,d)),Power(x,Times(C2,b,d)))),p),Power(Power(Plus(C1,Times(Exp(Times(C2,a,d)),Power(x,Times(C2,b,d)))),p),CN1)),x),FreeQ(List(a,b,d,e,m,p),x))),
IIntegrate(6084,Integrate(Times(Power(Coth(Times(Plus(a_DEFAULT,Times(Log(x_),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Times(e,x),m),Power(Subtract(CN1,Times(Exp(Times(C2,a,d)),Power(x,Times(C2,b,d)))),p),Power(Power(Subtract(C1,Times(Exp(Times(C2,a,d)),Power(x,Times(C2,b,d)))),p),CN1)),x),FreeQ(List(a,b,d,e,m,p),x))),
IIntegrate(6085,Integrate(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Tanh(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Times(Power(Times(e,x),Plus(m,C1)),Power(Times(e,n,Power(Times(c,Power(x,n)),Times(Plus(m,C1),Power(n,CN1)))),CN1)),Subst(Integrate(Times(Power(x,Plus(Times(Plus(m,C1),Power(n,CN1)),CN1)),Power(Tanh(Times(d,Plus(a,Times(b,Log(x))))),p)),x),x,Times(c,Power(x,n))),x),x),And(FreeQ(List(a,b,c,d,e,m,n,p),x),Or(NeQ(c,C1),NeQ(n,C1))))),
IIntegrate(6086,Integrate(Times(Power(Coth(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Times(Power(Times(e,x),Plus(m,C1)),Power(Times(e,n,Power(Times(c,Power(x,n)),Times(Plus(m,C1),Power(n,CN1)))),CN1)),Subst(Integrate(Times(Power(x,Plus(Times(Plus(m,C1),Power(n,CN1)),CN1)),Power(Coth(Times(d,Plus(a,Times(b,Log(x))))),p)),x),x,Times(c,Power(x,n))),x),x),And(FreeQ(List(a,b,c,d,e,m,n,p),x),Or(NeQ(c,C1),NeQ(n,C1))))),
IIntegrate(6087,Integrate(Power(Sech(Times(Plus(a_DEFAULT,Times(Log(x_),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Dist(Times(Power(C2,p),Power(Exp(Times(a,d,p)),CN1)),Integrate(Power(Times(Power(x,Times(b,d,p)),Power(Plus(C1,Power(Times(Exp(Times(C2,a,d)),Power(x,Times(C2,b,d))),CN1)),p)),CN1),x),x),x),And(FreeQ(list(a,b,d),x),IntegerQ(p)))),
IIntegrate(6088,Integrate(Power(Csch(Times(Plus(a_DEFAULT,Times(Log(x_),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Dist(Times(Power(C2,p),Power(Exp(Times(a,d,p)),CN1)),Integrate(Power(Times(Power(x,Times(b,d,p)),Power(Subtract(C1,Power(Times(Exp(Times(C2,a,d)),Power(x,Times(C2,b,d))),CN1)),p)),CN1),x),x),x),And(FreeQ(list(a,b,d),x),IntegerQ(p)))),
IIntegrate(6089,Integrate(Power(Sech(Times(Plus(a_DEFAULT,Times(Log(x_),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Dist(Times(Power(Sech(Times(d,Plus(a,Times(b,Log(x))))),p),Power(Plus(C1,Power(Times(Exp(Times(C2,a,d)),Power(x,Times(C2,b,d))),CN1)),p),Power(Power(x,Times(CN1,b,d,p)),CN1)),Integrate(Power(Times(Power(x,Times(b,d,p)),Power(Plus(C1,Power(Times(Exp(Times(C2,a,d)),Power(x,Times(C2,b,d))),CN1)),p)),CN1),x),x),x),And(FreeQ(List(a,b,d,p),x),Not(IntegerQ(p))))),
IIntegrate(6090,Integrate(Power(Csch(Times(Plus(a_DEFAULT,Times(Log(x_),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Dist(Times(Power(Csch(Times(d,Plus(a,Times(b,Log(x))))),p),Power(Subtract(C1,Power(Times(Exp(Times(C2,a,d)),Power(x,Times(C2,b,d))),CN1)),p),Power(Power(x,Times(CN1,b,d,p)),CN1)),Integrate(Power(Times(Power(x,Times(b,d,p)),Power(Subtract(C1,Power(Times(Exp(Times(C2,a,d)),Power(x,Times(C2,b,d))),CN1)),p)),CN1),x),x),x),And(FreeQ(List(a,b,d,p),x),Not(IntegerQ(p))))),
IIntegrate(6091,Integrate(Power(Sech(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Dist(Times(x,Power(Times(n,Power(Times(c,Power(x,n)),Power(n,CN1))),CN1)),Subst(Integrate(Times(Power(x,Plus(Power(n,CN1),CN1)),Power(Sech(Times(d,Plus(a,Times(b,Log(x))))),p)),x),x,Times(c,Power(x,n))),x),x),And(FreeQ(List(a,b,c,d,n,p),x),Or(NeQ(c,C1),NeQ(n,C1))))),
IIntegrate(6092,Integrate(Power(Csch(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Dist(Times(x,Power(Times(n,Power(Times(c,Power(x,n)),Power(n,CN1))),CN1)),Subst(Integrate(Times(Power(x,Plus(Power(n,CN1),CN1)),Power(Csch(Times(d,Plus(a,Times(b,Log(x))))),p)),x),x,Times(c,Power(x,n))),x),x),And(FreeQ(List(a,b,c,d,n,p),x),Or(NeQ(c,C1),NeQ(n,C1))))),
IIntegrate(6093,Integrate(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Sech(Times(Plus(a_DEFAULT,Times(Log(x_),b_DEFAULT)),d_DEFAULT)),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Times(Power(C2,p),Power(Exp(Times(a,d,p)),CN1)),Integrate(Times(Power(Times(e,x),m),Power(Times(Power(x,Times(b,d,p)),Power(Plus(C1,Power(Times(Exp(Times(C2,a,d)),Power(x,Times(C2,b,d))),CN1)),p)),CN1)),x),x),x),And(FreeQ(List(a,b,d,e,m),x),IntegerQ(p)))),
IIntegrate(6094,Integrate(Times(Power(Csch(Times(Plus(a_DEFAULT,Times(Log(x_),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Times(Power(C2,p),Power(Exp(Times(a,d,p)),CN1)),Integrate(Times(Power(Times(e,x),m),Power(Times(Power(x,Times(b,d,p)),Power(Subtract(C1,Power(Times(Exp(Times(C2,a,d)),Power(x,Times(C2,b,d))),CN1)),p)),CN1)),x),x),x),And(FreeQ(List(a,b,d,e,m),x),IntegerQ(p)))),
IIntegrate(6095,Integrate(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Sech(Times(Plus(a_DEFAULT,Times(Log(x_),b_DEFAULT)),d_DEFAULT)),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Times(Power(Sech(Times(d,Plus(a,Times(b,Log(x))))),p),Power(Plus(C1,Power(Times(Exp(Times(C2,a,d)),Power(x,Times(C2,b,d))),CN1)),p),Power(Power(x,Times(CN1,b,d,p)),CN1)),Integrate(Times(Power(Times(e,x),m),Power(Times(Power(x,Times(b,d,p)),Power(Plus(C1,Power(Times(Exp(Times(C2,a,d)),Power(x,Times(C2,b,d))),CN1)),p)),CN1)),x),x),x),And(FreeQ(List(a,b,d,e,m,p),x),Not(IntegerQ(p))))),
IIntegrate(6096,Integrate(Times(Power(Csch(Times(Plus(a_DEFAULT,Times(Log(x_),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Times(Power(Csch(Times(d,Plus(a,Times(b,Log(x))))),p),Power(Subtract(C1,Power(Times(Exp(Times(C2,a,d)),Power(x,Times(C2,b,d))),CN1)),p),Power(Power(x,Times(CN1,b,d,p)),CN1)),Integrate(Times(Power(Times(e,x),m),Power(Times(Power(x,Times(b,d,p)),Power(Subtract(C1,Power(Times(Exp(Times(C2,a,d)),Power(x,Times(C2,b,d))),CN1)),p)),CN1)),x),x),x),And(FreeQ(List(a,b,d,e,m,p),x),Not(IntegerQ(p))))),
IIntegrate(6097,Integrate(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Sech(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Times(Power(Times(e,x),Plus(m,C1)),Power(Times(e,n,Power(Times(c,Power(x,n)),Times(Plus(m,C1),Power(n,CN1)))),CN1)),Subst(Integrate(Times(Power(x,Plus(Times(Plus(m,C1),Power(n,CN1)),CN1)),Power(Sech(Times(d,Plus(a,Times(b,Log(x))))),p)),x),x,Times(c,Power(x,n))),x),x),And(FreeQ(List(a,b,c,d,e,m,n,p),x),Or(NeQ(c,C1),NeQ(n,C1))))),
IIntegrate(6098,Integrate(Times(Power(Csch(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Times(Power(Times(e,x),Plus(m,C1)),Power(Times(e,n,Power(Times(c,Power(x,n)),Times(Plus(m,C1),Power(n,CN1)))),CN1)),Subst(Integrate(Times(Power(x,Plus(Times(Plus(m,C1),Power(n,CN1)),CN1)),Power(Csch(Times(d,Plus(a,Times(b,Log(x))))),p)),x),x,Times(c,Power(x,n))),x),x),And(FreeQ(List(a,b,c,d,e,m,n,p),x),Or(NeQ(c,C1),NeQ(n,C1))))),
IIntegrate(6099,Integrate(Times(Log(Times(b_DEFAULT,x_)),Sinh(Times(Log(Times(b_DEFAULT,x_)),a_DEFAULT,x_))),x_Symbol),
    Condition(Subtract(Simp(Times(Cosh(Times(a,x,Log(Times(b,x)))),Power(a,CN1)),x),Integrate(Sinh(Times(a,x,Log(Times(b,x)))),x)),FreeQ(list(a,b),x))),
IIntegrate(6100,Integrate(Times(Cosh(Times(Log(Times(b_DEFAULT,x_)),a_DEFAULT,x_)),Log(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Subtract(Simp(Times(Sinh(Times(a,x,Log(Times(b,x)))),Power(a,CN1)),x),Integrate(Cosh(Times(a,x,Log(Times(b,x)))),x)),FreeQ(list(a,b),x)))
  );
}
