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
class IntRules359 { 
  public static IAST RULES = List( 
IIntegrate(7181,Integrate(Times(Power(Times(c_DEFAULT,ProductLog(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),CN1D2),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),CN1)),x_Symbol),
    Condition(Simp(Times(Rt(Times(CN1,Pi,c),C2),Erf(Times(Sqrt(Times(c,ProductLog(Plus(a,Times(b,x))))),Power(Rt(Negate(c),C2),CN1))),Power(Times(b,c,d),CN1)),x),And(FreeQ(List(a,b,c,d),x),NegQ(c)))),
IIntegrate(7182,Integrate(Times(Power(Times(c_DEFAULT,ProductLog(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),p_),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(a,Times(b,x)),Power(Times(c,ProductLog(Plus(a,Times(b,x)))),p),Power(Times(b,d,Plus(p,C1)),CN1)),x),Simp(Star(Power(Times(c,Plus(p,C1)),CN1),Integrate(Times(Power(Times(c,ProductLog(Plus(a,Times(b,x)))),Plus(p,C1)),Power(Plus(d,Times(d,ProductLog(Plus(a,Times(b,x))))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d),x),LtQ(p,CN1)))),
IIntegrate(7183,Integrate(Times(Power(Times(c_DEFAULT,ProductLog(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),p_DEFAULT),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),CN1)),x_Symbol),
    Condition(Simp(Times(Gamma(Plus(p,C1),Negate(ProductLog(Plus(a,Times(b,x))))),Power(Times(c,ProductLog(Plus(a,Times(b,x)))),p),Power(Times(b,d,Power(Negate(ProductLog(Plus(a,Times(b,x)))),p)),CN1)),x),FreeQ(List(a,b,c,d,p),x))),
IIntegrate(7184,Integrate(Times(Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Plus(a_,Times(b_DEFAULT,x_))))),CN1)),x_Symbol),
    Condition(Simp(Star(Power(Power(b,Plus(m,C1)),CN1),Subst(Integrate(ExpandIntegrand(Power(Plus(d,Times(d,ProductLog(x))),CN1),Power(Plus(Times(b,e),Times(CN1,a,f),Times(f,x)),m),x),x),x,Plus(a,Times(b,x)))),x),And(FreeQ(List(a,b,d,e,f),x),IGtQ(m,C0)))),
IIntegrate(7185,Integrate(Times(Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Power(Times(c_DEFAULT,ProductLog(Plus(a_,Times(b_DEFAULT,x_)))),p_DEFAULT),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Plus(a_,Times(b_DEFAULT,x_))))),CN1)),x_Symbol),
    Condition(Simp(Star(Power(Power(b,Plus(m,C1)),CN1),Subst(Integrate(ExpandIntegrand(Times(Power(Times(c,ProductLog(x)),p),Power(Plus(d,Times(d,ProductLog(x))),CN1)),Power(Plus(Times(b,e),Times(CN1,a,f),Times(f,x)),m),x),x),x,Plus(a,Times(b,x)))),x),And(FreeQ(List(a,b,c,d,e,f,p),x),IGtQ(m,C0)))),
IIntegrate(7186,Integrate(Power(Plus(d_,Times(d_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_))))),CN1),x_Symbol),
    Condition(Negate(Subst(Integrate(Power(Times(Sqr(x),Plus(d,Times(d,ProductLog(Times(a,Power(Power(x,n),CN1)))))),CN1),x),x,Power(x,CN1))),And(FreeQ(list(a,d),x),ILtQ(n,C0)))),
IIntegrate(7187,Integrate(Times(Power(Times(c_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT))))),CN1)),x_Symbol),
    Condition(Simp(Times(c,x,Power(Times(c,ProductLog(Times(a,Power(x,n)))),Subtract(p,C1)),Power(d,CN1)),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Times(n,Subtract(p,C1)),CN1)))),
IIntegrate(7188,Integrate(Times(Power(ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT))),p_DEFAULT),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT))))),CN1)),x_Symbol),
    Condition(Simp(Times(Power(a,p),ExpIntegralEi(Times(CN1,p,ProductLog(Times(a,Power(x,n))))),Power(Times(d,n),CN1)),x),And(FreeQ(list(a,d),x),IntegerQ(p),EqQ(Times(n,p),CN1)))),
IIntegrate(7189,Integrate(Times(Power(Times(c_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT)))),p_),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT))))),CN1)),x_Symbol),
    Condition(Simp(Times(Rt(Times(Pi,c,n),C2),Power(Times(d,n,Power(a,Power(n,CN1)),Power(c,Power(n,CN1))),CN1),Erfi(Times(Sqrt(Times(c,ProductLog(Times(a,Power(x,n))))),Power(Rt(Times(c,n),C2),CN1)))),x),And(FreeQ(list(a,c,d),x),IntegerQ(Power(n,CN1)),EqQ(p,Subtract(C1D2,Power(n,CN1))),PosQ(Times(c,n))))),
IIntegrate(7190,Integrate(Times(Power(Times(c_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT)))),p_),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT))))),CN1)),x_Symbol),
    Condition(Simp(Times(Rt(Times(CN1,Pi,c,n),C2),Power(Times(d,n,Power(a,Power(n,CN1)),Power(c,Power(n,CN1))),CN1),Erf(Times(Sqrt(Times(c,ProductLog(Times(a,Power(x,n))))),Power(Rt(Times(CN1,c,n),C2),CN1)))),x),And(FreeQ(list(a,c,d),x),IntegerQ(Power(n,CN1)),EqQ(p,Subtract(C1D2,Power(n,CN1))),NegQ(Times(c,n))))),
IIntegrate(7191,Integrate(Times(Power(Times(c_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT))))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(c,x,Power(Times(c,ProductLog(Times(a,Power(x,n)))),Subtract(p,C1)),Power(d,CN1)),x),Simp(Star(Times(c,Plus(Times(n,Subtract(p,C1)),C1)),Integrate(Times(Power(Times(c,ProductLog(Times(a,Power(x,n)))),Subtract(p,C1)),Power(Plus(d,Times(d,ProductLog(Times(a,Power(x,n))))),CN1)),x)),x)),And(FreeQ(list(a,c,d),x),GtQ(n,C0),GtQ(Plus(Times(n,Subtract(p,C1)),C1),C0)))),
IIntegrate(7192,Integrate(Times(Power(Times(c_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT))))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(x,Power(Times(c,ProductLog(Times(a,Power(x,n)))),p),Power(Times(d,Plus(Times(n,p),C1)),CN1)),x),Simp(Star(Power(Times(c,Plus(Times(n,p),C1)),CN1),Integrate(Times(Power(Times(c,ProductLog(Times(a,Power(x,n)))),Plus(p,C1)),Power(Plus(d,Times(d,ProductLog(Times(a,Power(x,n))))),CN1)),x)),x)),And(FreeQ(list(a,c,d),x),GtQ(n,C0),LtQ(Plus(Times(n,p),C1),C0)))),
IIntegrate(7193,Integrate(Times(Power(Times(c_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_)))),p_DEFAULT),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_))))),CN1)),x_Symbol),
    Condition(Negate(Subst(Integrate(Times(Power(Times(c,ProductLog(Times(a,Power(Power(x,n),CN1)))),p),Power(Times(Sqr(x),Plus(d,Times(d,ProductLog(Times(a,Power(Power(x,n),CN1)))))),CN1)),x),x,Power(x,CN1))),And(FreeQ(List(a,c,d,p),x),ILtQ(n,C0)))),
IIntegrate(7194,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Times(a_DEFAULT,x_)))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,C1)),Power(Times(d,Plus(m,C1),ProductLog(Times(a,x))),CN1)),x),Simp(Star(Times(m,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,m),Power(Times(ProductLog(Times(a,x)),Plus(d,Times(d,ProductLog(Times(a,x))))),CN1)),x)),x)),And(FreeQ(list(a,d),x),GtQ(m,C0)))),
IIntegrate(7195,Integrate(Times(Power(x_,CN1),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Times(a_DEFAULT,x_)))),CN1)),x_Symbol),
    Condition(Simp(Times(Log(ProductLog(Times(a,x))),Power(d,CN1)),x),FreeQ(list(a,d),x))),
IIntegrate(7196,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Times(a_DEFAULT,x_)))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,C1)),Power(Times(d,Plus(m,C1)),CN1)),x),Integrate(Times(Power(x,m),ProductLog(Times(a,x)),Power(Plus(d,Times(d,ProductLog(Times(a,x)))),CN1)),x)),And(FreeQ(list(a,d),x),LtQ(m,CN1)))),
IIntegrate(7197,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Times(a_DEFAULT,x_)))),CN1)),x_Symbol),
    Condition(Simp(Times(Power(x,m),Gamma(Plus(m,C1),Times(CN1,Plus(m,C1),ProductLog(Times(a,x)))),Power(Times(a,d,Plus(m,C1),Exp(Times(m,ProductLog(Times(a,x)))),Power(Times(CN1,Plus(m,C1),ProductLog(Times(a,x))),m)),CN1)),x),And(FreeQ(list(a,d,m),x),Not(IntegerQ(m))))),
IIntegrate(7198,Integrate(Times(Power(x_,CN1),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT))))),CN1)),x_Symbol),
    Condition(Simp(Times(Log(ProductLog(Times(a,Power(x,n)))),Power(Times(d,n),CN1)),x),FreeQ(list(a,d,n),x))),
IIntegrate(7199,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_))))),CN1)),x_Symbol),
    Condition(Negate(Subst(Integrate(Power(Times(Power(x,Plus(m,C2)),Plus(d,Times(d,ProductLog(Times(a,Power(Power(x,n),CN1)))))),CN1),x),x,Power(x,CN1))),And(FreeQ(list(a,d),x),IntegerQ(m),ILtQ(n,C0),NeQ(m,CN1)))),
IIntegrate(7200,Integrate(Times(Power(x_,CN1),Power(Times(c_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT))))),CN1)),x_Symbol),
    Condition(Simp(Times(Power(Times(c,ProductLog(Times(a,Power(x,n)))),p),Power(Times(d,n,p),CN1)),x),FreeQ(List(a,c,d,n,p),x)))
  );
}
