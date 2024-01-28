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
class IntRules250 { 
  public static IAST RULES = List( 
IIntegrate(5001,Integrate(Times(Cos(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),Power(Times(Plus(e_DEFAULT,Times(Log(Times(g_DEFAULT,Power(x_,m_DEFAULT))),f_DEFAULT)),h_DEFAULT),q_DEFAULT),Power(Times(i_DEFAULT,x_),r_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Star(Times(Power(Times(i,x),r),Power(Times(Power(Times(c,Power(x,n)),Times(CI,b,d)),C2,Power(x,Subtract(r,Times(CI,b,d,n)))),CN1),Power(Exp(Times(CI,a,d)),CN1)),Integrate(Times(Power(x,Subtract(r,Times(CI,b,d,n))),Power(Times(h,Plus(e,Times(f,Log(Times(g,Power(x,m)))))),q)),x)),x),Simp(Star(Times(Exp(Times(CI,a,d)),Power(Times(i,x),r),Power(Times(c,Power(x,n)),Times(CI,b,d)),Power(Times(C2,Power(x,Plus(r,Times(CI,b,d,n)))),CN1)),Integrate(Times(Power(x,Plus(r,Times(CI,b,d,n))),Power(Times(h,Plus(e,Times(f,Log(Times(g,Power(x,m)))))),q)),x)),x)),FreeQ(List(a,b,c,d,e,f,g,h,i,m,n,q,r),x))),
IIntegrate(5002,Integrate(Power(Tan(Times(Plus(a_DEFAULT,Times(Log(x_),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Integrate(Power(Times(Subtract(CI,Times(CI,Exp(Times(C2,CI,a,d)),Power(x,Times(C2,CI,b,d)))),Power(Plus(C1,Times(Exp(Times(C2,CI,a,d)),Power(x,Times(C2,CI,b,d)))),CN1)),p),x),FreeQ(List(a,b,d,p),x))),
IIntegrate(5003,Integrate(Power(Cot(Times(Plus(a_DEFAULT,Times(Log(x_),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Integrate(Power(Times(Subtract(CNI,Times(CI,Exp(Times(C2,CI,a,d)),Power(x,Times(C2,CI,b,d)))),Power(Subtract(C1,Times(Exp(Times(C2,CI,a,d)),Power(x,Times(C2,CI,b,d)))),CN1)),p),x),FreeQ(List(a,b,d,p),x))),
IIntegrate(5004,Integrate(Power(Tan(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Star(Times(x,Power(Times(n,Power(Times(c,Power(x,n)),Power(n,CN1))),CN1)),Subst(Integrate(Times(Power(x,Subtract(Power(n,CN1),C1)),Power(Tan(Times(d,Plus(a,Times(b,Log(x))))),p)),x),x,Times(c,Power(x,n)))),x),And(FreeQ(List(a,b,c,d,n,p),x),Or(NeQ(c,C1),NeQ(n,C1))))),
IIntegrate(5005,Integrate(Power(Cot(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Star(Times(x,Power(Times(n,Power(Times(c,Power(x,n)),Power(n,CN1))),CN1)),Subst(Integrate(Times(Power(x,Subtract(Power(n,CN1),C1)),Power(Cot(Times(d,Plus(a,Times(b,Log(x))))),p)),x),x,Times(c,Power(x,n)))),x),And(FreeQ(List(a,b,c,d,n,p),x),Or(NeQ(c,C1),NeQ(n,C1))))),
IIntegrate(5006,Integrate(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Tan(Times(Plus(a_DEFAULT,Times(Log(x_),b_DEFAULT)),d_DEFAULT)),p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Times(e,x),m),Power(Times(Subtract(CI,Times(CI,Exp(Times(C2,CI,a,d)),Power(x,Times(C2,CI,b,d)))),Power(Plus(C1,Times(Exp(Times(C2,CI,a,d)),Power(x,Times(C2,CI,b,d)))),CN1)),p)),x),FreeQ(List(a,b,d,e,m,p),x))),
IIntegrate(5007,Integrate(Times(Power(Cot(Times(Plus(a_DEFAULT,Times(Log(x_),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Times(e,x),m),Power(Times(Subtract(CNI,Times(CI,Exp(Times(C2,CI,a,d)),Power(x,Times(C2,CI,b,d)))),Power(Subtract(C1,Times(Exp(Times(C2,CI,a,d)),Power(x,Times(C2,CI,b,d)))),CN1)),p)),x),FreeQ(List(a,b,d,e,m,p),x))),
IIntegrate(5008,Integrate(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Tan(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(Power(Times(e,x),Plus(m,C1)),Power(Times(e,n,Power(Times(c,Power(x,n)),Times(Plus(m,C1),Power(n,CN1)))),CN1)),Subst(Integrate(Times(Power(x,Subtract(Times(Plus(m,C1),Power(n,CN1)),C1)),Power(Tan(Times(d,Plus(a,Times(b,Log(x))))),p)),x),x,Times(c,Power(x,n)))),x),And(FreeQ(List(a,b,c,d,e,m,n,p),x),Or(NeQ(c,C1),NeQ(n,C1))))),
IIntegrate(5009,Integrate(Times(Power(Cot(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(Power(Times(e,x),Plus(m,C1)),Power(Times(e,n,Power(Times(c,Power(x,n)),Times(Plus(m,C1),Power(n,CN1)))),CN1)),Subst(Integrate(Times(Power(x,Subtract(Times(Plus(m,C1),Power(n,CN1)),C1)),Power(Cot(Times(d,Plus(a,Times(b,Log(x))))),p)),x),x,Times(c,Power(x,n)))),x),And(FreeQ(List(a,b,c,d,e,m,n,p),x),Or(NeQ(c,C1),NeQ(n,C1))))),
IIntegrate(5010,Integrate(Power(Sec(Times(Plus(a_DEFAULT,Times(Log(x_),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Star(Times(Power(C2,p),Exp(Times(CI,a,d,p))),Integrate(Times(Power(x,Times(CI,b,d,p)),Power(Power(Plus(C1,Times(Exp(Times(C2,CI,a,d)),Power(x,Times(C2,CI,b,d)))),p),CN1)),x)),x),And(FreeQ(list(a,b,d),x),IntegerQ(p)))),
IIntegrate(5011,Integrate(Power(Csc(Times(Plus(a_DEFAULT,Times(Log(x_),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Star(Times(Power(Times(CN2,CI),p),Exp(Times(CI,a,d,p))),Integrate(Times(Power(x,Times(CI,b,d,p)),Power(Power(Subtract(C1,Times(Exp(Times(C2,CI,a,d)),Power(x,Times(C2,CI,b,d)))),p),CN1)),x)),x),And(FreeQ(list(a,b,d),x),IntegerQ(p)))),
IIntegrate(5012,Integrate(Power(Sec(Times(Plus(a_DEFAULT,Times(Log(x_),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Star(Times(Power(Sec(Times(d,Plus(a,Times(b,Log(x))))),p),Power(Plus(C1,Times(Exp(Times(C2,CI,a,d)),Power(x,Times(C2,CI,b,d)))),p),Power(Power(x,Times(CI,b,d,p)),CN1)),Integrate(Times(Power(x,Times(CI,b,d,p)),Power(Power(Plus(C1,Times(Exp(Times(C2,CI,a,d)),Power(x,Times(C2,CI,b,d)))),p),CN1)),x)),x),And(FreeQ(List(a,b,d,p),x),Not(IntegerQ(p))))),
IIntegrate(5013,Integrate(Power(Csc(Times(Plus(a_DEFAULT,Times(Log(x_),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Star(Times(Power(Csc(Times(d,Plus(a,Times(b,Log(x))))),p),Power(Subtract(C1,Times(Exp(Times(C2,CI,a,d)),Power(x,Times(C2,CI,b,d)))),p),Power(Power(x,Times(CI,b,d,p)),CN1)),Integrate(Times(Power(x,Times(CI,b,d,p)),Power(Power(Subtract(C1,Times(Exp(Times(C2,CI,a,d)),Power(x,Times(C2,CI,b,d)))),p),CN1)),x)),x),And(FreeQ(List(a,b,d,p),x),Not(IntegerQ(p))))),
IIntegrate(5014,Integrate(Power(Sec(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Star(Times(x,Power(Times(n,Power(Times(c,Power(x,n)),Power(n,CN1))),CN1)),Subst(Integrate(Times(Power(x,Subtract(Power(n,CN1),C1)),Power(Sec(Times(d,Plus(a,Times(b,Log(x))))),p)),x),x,Times(c,Power(x,n)))),x),And(FreeQ(List(a,b,c,d,n,p),x),Or(NeQ(c,C1),NeQ(n,C1))))),
IIntegrate(5015,Integrate(Power(Csc(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Star(Times(x,Power(Times(n,Power(Times(c,Power(x,n)),Power(n,CN1))),CN1)),Subst(Integrate(Times(Power(x,Subtract(Power(n,CN1),C1)),Power(Csc(Times(d,Plus(a,Times(b,Log(x))))),p)),x),x,Times(c,Power(x,n)))),x),And(FreeQ(List(a,b,c,d,n,p),x),Or(NeQ(c,C1),NeQ(n,C1))))),
IIntegrate(5016,Integrate(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Sec(Times(Plus(a_DEFAULT,Times(Log(x_),b_DEFAULT)),d_DEFAULT)),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(Power(C2,p),Exp(Times(CI,a,d,p))),Integrate(Times(Power(Times(e,x),m),Power(x,Times(CI,b,d,p)),Power(Power(Plus(C1,Times(Exp(Times(C2,CI,a,d)),Power(x,Times(C2,CI,b,d)))),p),CN1)),x)),x),And(FreeQ(List(a,b,d,e,m),x),IntegerQ(p)))),
IIntegrate(5017,Integrate(Times(Power(Csc(Times(Plus(a_DEFAULT,Times(Log(x_),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(Power(Times(CN2,CI),p),Exp(Times(CI,a,d,p))),Integrate(Times(Power(Times(e,x),m),Power(x,Times(CI,b,d,p)),Power(Power(Subtract(C1,Times(Exp(Times(C2,CI,a,d)),Power(x,Times(C2,CI,b,d)))),p),CN1)),x)),x),And(FreeQ(List(a,b,d,e,m),x),IntegerQ(p)))),
IIntegrate(5018,Integrate(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Sec(Times(Plus(a_DEFAULT,Times(Log(x_),b_DEFAULT)),d_DEFAULT)),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(Power(Sec(Times(d,Plus(a,Times(b,Log(x))))),p),Power(Plus(C1,Times(Exp(Times(C2,CI,a,d)),Power(x,Times(C2,CI,b,d)))),p),Power(Power(x,Times(CI,b,d,p)),CN1)),Integrate(Times(Power(Times(e,x),m),Power(x,Times(CI,b,d,p)),Power(Power(Plus(C1,Times(Exp(Times(C2,CI,a,d)),Power(x,Times(C2,CI,b,d)))),p),CN1)),x)),x),And(FreeQ(List(a,b,d,e,m,p),x),Not(IntegerQ(p))))),
IIntegrate(5019,Integrate(Times(Power(Csc(Times(Plus(a_DEFAULT,Times(Log(x_),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(Power(Csc(Times(d,Plus(a,Times(b,Log(x))))),p),Power(Subtract(C1,Times(Exp(Times(C2,CI,a,d)),Power(x,Times(C2,CI,b,d)))),p),Power(Power(x,Times(CI,b,d,p)),CN1)),Integrate(Times(Power(Times(e,x),m),Power(x,Times(CI,b,d,p)),Power(Power(Subtract(C1,Times(Exp(Times(C2,CI,a,d)),Power(x,Times(C2,CI,b,d)))),p),CN1)),x)),x),And(FreeQ(List(a,b,d,e,m,p),x),Not(IntegerQ(p))))),
IIntegrate(5020,Integrate(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Sec(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(Power(Times(e,x),Plus(m,C1)),Power(Times(e,n,Power(Times(c,Power(x,n)),Times(Plus(m,C1),Power(n,CN1)))),CN1)),Subst(Integrate(Times(Power(x,Subtract(Times(Plus(m,C1),Power(n,CN1)),C1)),Power(Sec(Times(d,Plus(a,Times(b,Log(x))))),p)),x),x,Times(c,Power(x,n)))),x),And(FreeQ(List(a,b,c,d,e,m,n,p),x),Or(NeQ(c,C1),NeQ(n,C1)))))
  );
}
