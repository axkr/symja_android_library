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
class IntRules357 { 
  public static IAST RULES = List( 
IIntegrate(7141,Integrate(PolyLog(n_,Times(a_DEFAULT,Power(Times(b_DEFAULT,Power(x_,p_DEFAULT)),q_DEFAULT))),x_Symbol),
    Condition(Subtract(Simp(Times(x,PolyLog(Plus(n,C1),Times(a,Power(Times(b,Power(x,p)),q))),Power(Times(p,q),CN1)),x),Simp(Star(Power(Times(p,q),CN1),Integrate(PolyLog(Plus(n,C1),Times(a,Power(Times(b,Power(x,p)),q))),x)),x)),And(FreeQ(List(a,b,p,q),x),LtQ(n,CN1)))),
IIntegrate(7142,Integrate(PolyLog(n_,Times(a_DEFAULT,Power(Times(b_DEFAULT,Power(x_,p_DEFAULT)),q_DEFAULT))),x_Symbol),
    Condition(Unintegrable(PolyLog(n,Times(a,Power(Times(b,Power(x,p)),q))),x),FreeQ(List(a,b,n,p,q),x))),
IIntegrate(7143,Integrate(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),CN1),PolyLog(n_,Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),p_DEFAULT)))),x_Symbol),
    Condition(Simp(Times(PolyLog(Plus(n,C1),Times(c,Power(Plus(a,Times(b,x)),p))),Power(Times(e,p),CN1)),x),And(FreeQ(List(a,b,c,d,e,n,p),x),EqQ(Times(b,d),Times(a,e))))),
IIntegrate(7144,Integrate(Times(Power(x_,CN1),PolyLog(n_,Times(a_DEFAULT,Power(Times(b_DEFAULT,Power(x_,p_DEFAULT)),q_DEFAULT)))),x_Symbol),
    Condition(Simp(Times(PolyLog(Plus(n,C1),Times(a,Power(Times(b,Power(x,p)),q))),Power(Times(p,q),CN1)),x),FreeQ(List(a,b,n,p,q),x))),
IIntegrate(7145,Integrate(Times(Power(Times(d_DEFAULT,x_),m_DEFAULT),PolyLog(n_,Times(a_DEFAULT,Power(Times(b_DEFAULT,Power(x_,p_DEFAULT)),q_DEFAULT)))),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(d,x),Plus(m,C1)),PolyLog(n,Times(a,Power(Times(b,Power(x,p)),q))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Star(Times(p,q,Power(Plus(m,C1),CN1)),Integrate(Times(Power(Times(d,x),m),PolyLog(Subtract(n,C1),Times(a,Power(Times(b,Power(x,p)),q)))),x)),x)),And(FreeQ(List(a,b,d,m,p,q),x),NeQ(m,CN1),GtQ(n,C0)))),
IIntegrate(7146,Integrate(Times(Power(Times(d_DEFAULT,x_),m_DEFAULT),PolyLog(n_,Times(a_DEFAULT,Power(Times(b_DEFAULT,Power(x_,p_DEFAULT)),q_DEFAULT)))),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(d,x),Plus(m,C1)),PolyLog(Plus(n,C1),Times(a,Power(Times(b,Power(x,p)),q))),Power(Times(d,p,q),CN1)),x),Simp(Star(Times(Plus(m,C1),Power(Times(p,q),CN1)),Integrate(Times(Power(Times(d,x),m),PolyLog(Plus(n,C1),Times(a,Power(Times(b,Power(x,p)),q)))),x)),x)),And(FreeQ(List(a,b,d,m,p,q),x),NeQ(m,CN1),LtQ(n,CN1)))),
IIntegrate(7147,Integrate(Times(Power(Times(d_DEFAULT,x_),m_DEFAULT),PolyLog(n_,Times(a_DEFAULT,Power(Times(b_DEFAULT,Power(x_,p_DEFAULT)),q_DEFAULT)))),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(d,x),m),PolyLog(n,Times(a,Power(Times(b,Power(x,p)),q)))),x),FreeQ(List(a,b,d,m,n,p,q),x))),
IIntegrate(7148,Integrate(Times(Power(Log(Times(c_DEFAULT,Power(x_,m_DEFAULT))),r_DEFAULT),Power(x_,CN1),PolyLog(n_,Times(a_DEFAULT,Power(Times(b_DEFAULT,Power(x_,p_DEFAULT)),q_DEFAULT)))),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Log(Times(c,Power(x,m))),r),PolyLog(Plus(n,C1),Times(a,Power(Times(b,Power(x,p)),q))),Power(Times(p,q),CN1)),x),Simp(Star(Times(m,r,Power(Times(p,q),CN1)),Integrate(Times(Power(Log(Times(c,Power(x,m))),Subtract(r,C1)),PolyLog(Plus(n,C1),Times(a,Power(Times(b,Power(x,p)),q))),Power(x,CN1)),x)),x)),And(FreeQ(List(a,b,c,m,n,q,r),x),GtQ(r,C0)))),
IIntegrate(7149,Integrate(PolyLog(n_,Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),p_DEFAULT))),x_Symbol),
    Condition(Plus(Simp(Times(x,PolyLog(n,Times(c,Power(Plus(a,Times(b,x)),p)))),x),Negate(Simp(Star(p,Integrate(PolyLog(Subtract(n,C1),Times(c,Power(Plus(a,Times(b,x)),p))),x)),x)),Simp(Star(Times(a,p),Integrate(Times(PolyLog(Subtract(n,C1),Times(c,Power(Plus(a,Times(b,x)),p))),Power(Plus(a,Times(b,x)),CN1)),x)),x)),And(FreeQ(List(a,b,c,p),x),GtQ(n,C0)))),
IIntegrate(7150,Integrate(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),CN1),PolyLog(C2,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),x_Symbol),
    Condition(Plus(Simp(Times(Log(Subtract(Subtract(C1,Times(a,c)),Times(b,c,x))),PolyLog(C2,Times(c,Plus(a,Times(b,x)))),Power(e,CN1)),x),Simp(Star(Times(b,Power(e,CN1)),Integrate(Times(Sqr(Log(Subtract(Subtract(C1,Times(a,c)),Times(b,c,x)))),Power(Plus(a,Times(b,x)),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e),x),EqQ(Plus(Times(c,Subtract(Times(b,d),Times(a,e))),e),C0)))),
IIntegrate(7151,Integrate(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),CN1),PolyLog(C2,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),x_Symbol),
    Condition(Plus(Simp(Times(Log(Plus(d,Times(e,x))),PolyLog(C2,Times(c,Plus(a,Times(b,x)))),Power(e,CN1)),x),Simp(Star(Times(b,Power(e,CN1)),Integrate(Times(Log(Plus(d,Times(e,x))),Log(Subtract(Subtract(C1,Times(a,c)),Times(b,c,x))),Power(Plus(a,Times(b,x)),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e),x),NeQ(Plus(Times(c,Subtract(Times(b,d),Times(a,e))),e),C0)))),
IIntegrate(7152,Integrate(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT),PolyLog(C2,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),PolyLog(C2,Times(c,Plus(a,Times(b,x)))),Power(Times(e,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,Power(Times(e,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Log(Subtract(Subtract(C1,Times(a,c)),Times(b,c,x))),Power(Plus(a,Times(b,x)),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,m),x),NeQ(m,CN1)))),
IIntegrate(7153,Integrate(Times(Power(x_,m_DEFAULT),PolyLog(n_,Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),p_DEFAULT)))),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Subtract(Power(a,Plus(m,C1)),Times(Power(b,Plus(m,C1)),Power(x,Plus(m,C1)))),PolyLog(n,Times(c,Power(Plus(a,Times(b,x)),p))),Power(Times(Plus(m,C1),Power(b,Plus(m,C1))),CN1)),x),Simp(Star(Times(p,Power(Times(Plus(m,C1),Power(b,m)),CN1)),Integrate(ExpandIntegrand(PolyLog(Subtract(n,C1),Times(c,Power(Plus(a,Times(b,x)),p))),Times(Subtract(Power(a,Plus(m,C1)),Times(Power(b,Plus(m,C1)),Power(x,Plus(m,C1)))),Power(Plus(a,Times(b,x)),CN1)),x),x)),x)),And(FreeQ(List(a,b,c,p),x),GtQ(n,C0),IntegerQ(m),NeQ(m,CN1)))),
IIntegrate(7154,Integrate(Times(Plus(g_DEFAULT,Times(Log(Times(f_DEFAULT,Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),n_DEFAULT))),h_DEFAULT)),PolyLog(C2,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),x_Symbol),
    Condition(Plus(Simp(Times(x,Plus(g,Times(h,Log(Times(f,Power(Plus(d,Times(e,x)),n))))),PolyLog(C2,Times(c,Plus(a,Times(b,x))))),x),Simp(Star(b,Integrate(Times(Plus(g,Times(h,Log(Times(f,Power(Plus(d,Times(e,x)),n))))),Log(Subtract(Subtract(C1,Times(a,c)),Times(b,c,x))),ExpandIntegrand(Times(x,Power(Plus(a,Times(b,x)),CN1)),x)),x)),x),Negate(Simp(Star(Times(e,h,n),Integrate(Times(PolyLog(C2,Times(c,Plus(a,Times(b,x)))),ExpandIntegrand(Times(x,Power(Plus(d,Times(e,x)),CN1)),x)),x)),x))),FreeQ(List(a,b,c,d,e,f,g,h,n),x))),
IIntegrate(7155,Integrate(Times(Log(Plus(C1,Times(e_DEFAULT,x_))),Power(x_,CN1),PolyLog(C2,Times(c_DEFAULT,x_))),x_Symbol),
    Condition(Simp(Times(C1D2,CN1,Sqr(PolyLog(C2,Times(c,x)))),x),And(FreeQ(list(c,e),x),EqQ(Plus(c,e),C0)))),
IIntegrate(7156,Integrate(Times(Plus(Times(Log(Plus(C1,Times(e_DEFAULT,x_))),h_DEFAULT),g_),Power(x_,CN1),PolyLog(C2,Times(c_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Simp(Star(g,Integrate(Times(PolyLog(C2,Times(c,x)),Power(x,CN1)),x)),x),Simp(Star(h,Integrate(Times(Log(Plus(C1,Times(e,x))),PolyLog(C2,Times(c,x)),Power(x,CN1)),x)),x)),And(FreeQ(List(c,e,g,h),x),EqQ(Plus(c,e),C0)))),
IIntegrate(7157,Integrate(Times(Plus(g_DEFAULT,Times(Log(Times(f_DEFAULT,Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),n_DEFAULT))),h_DEFAULT)),Power(x_,m_DEFAULT),PolyLog(C2,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),Plus(g,Times(h,Log(Times(f,Power(Plus(d,Times(e,x)),n))))),PolyLog(C2,Times(c,Plus(a,Times(b,x)))),Power(Plus(m,C1),CN1)),x),Simp(Star(Times(b,Power(Plus(m,C1),CN1)),Integrate(ExpandIntegrand(Times(Plus(g,Times(h,Log(Times(f,Power(Plus(d,Times(e,x)),n))))),Log(Subtract(Subtract(C1,Times(a,c)),Times(b,c,x)))),Times(Power(x,Plus(m,C1)),Power(Plus(a,Times(b,x)),CN1)),x),x)),x),Negate(Simp(Star(Times(e,h,n,Power(Plus(m,C1),CN1)),Integrate(ExpandIntegrand(PolyLog(C2,Times(c,Plus(a,Times(b,x)))),Times(Power(x,Plus(m,C1)),Power(Plus(d,Times(e,x)),CN1)),x),x)),x))),And(FreeQ(List(a,b,c,d,e,f,g,h,n),x),IntegerQ(m),NeQ(m,CN1)))),
IIntegrate(7158,Integrate(Times(Plus(g_DEFAULT,Times(Log(Times(f_DEFAULT,Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),n_DEFAULT))),h_DEFAULT)),$p("§px"),PolyLog(C2,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),x_Symbol),
    Condition(With(list(Set(u,IntHide($s("§px"),x))),Plus(Simp(Times(u,Plus(g,Times(h,Log(Times(f,Power(Plus(d,Times(e,x)),n))))),PolyLog(C2,Times(c,Plus(a,Times(b,x))))),x),Simp(Star(b,Integrate(ExpandIntegrand(Times(Plus(g,Times(h,Log(Times(f,Power(Plus(d,Times(e,x)),n))))),Log(Subtract(Subtract(C1,Times(a,c)),Times(b,c,x)))),Times(u,Power(Plus(a,Times(b,x)),CN1)),x),x)),x),Negate(Simp(Star(Times(e,h,n),Integrate(ExpandIntegrand(PolyLog(C2,Times(c,Plus(a,Times(b,x)))),Times(u,Power(Plus(d,Times(e,x)),CN1)),x),x)),x)))),And(FreeQ(List(a,b,c,d,e,f,g,h,n),x),PolyQ($s("§px"),x)))),
IIntegrate(7159,Integrate(Times(Plus(g_DEFAULT,Times(Log(Plus(C1,Times(e_DEFAULT,x_))),h_DEFAULT)),$p("§px"),Power(x_,m_),PolyLog(C2,Times(c_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Simp(Star(Coeff($s("§px"),x,Subtract(Negate(m),C1)),Integrate(Times(Plus(g,Times(h,Log(Plus(C1,Times(e,x))))),PolyLog(C2,Times(c,x)),Power(x,CN1)),x)),x),Integrate(Times(Power(x,m),Subtract($s("§px"),Times(Coeff($s("§px"),x,Subtract(Negate(m),C1)),Power(x,Subtract(Negate(m),C1)))),Plus(g,Times(h,Log(Plus(C1,Times(e,x))))),PolyLog(C2,Times(c,x))),x)),And(FreeQ(List(c,e,g,h),x),PolyQ($s("§px"),x),ILtQ(m,C0),EqQ(Plus(c,e),C0),NeQ(Coeff($s("§px"),x,Subtract(Negate(m),C1)),C0)))),
IIntegrate(7160,Integrate(Times(Plus(g_DEFAULT,Times(Log(Times(f_DEFAULT,Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),n_DEFAULT))),h_DEFAULT)),$p("§px"),Power(x_,m_DEFAULT),PolyLog(C2,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),x_Symbol),
    Condition(With(list(Set(u,IntHide(Times(Power(x,m),$s("§px")),x))),Plus(Simp(Times(u,Plus(g,Times(h,Log(Times(f,Power(Plus(d,Times(e,x)),n))))),PolyLog(C2,Times(c,Plus(a,Times(b,x))))),x),Simp(Star(b,Integrate(ExpandIntegrand(Times(Plus(g,Times(h,Log(Times(f,Power(Plus(d,Times(e,x)),n))))),Log(Subtract(Subtract(C1,Times(a,c)),Times(b,c,x)))),Times(u,Power(Plus(a,Times(b,x)),CN1)),x),x)),x),Negate(Simp(Star(Times(e,h,n),Integrate(ExpandIntegrand(PolyLog(C2,Times(c,Plus(a,Times(b,x)))),Times(u,Power(Plus(d,Times(e,x)),CN1)),x),x)),x)))),And(FreeQ(List(a,b,c,d,e,f,g,h,n),x),PolyQ($s("§px"),x),IntegerQ(m))))
  );
}
