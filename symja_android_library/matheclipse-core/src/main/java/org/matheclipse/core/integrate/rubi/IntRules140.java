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
class IntRules140 { 
  public static IAST RULES = List( 
IIntegrate(2801,Integrate(Times(Plus(Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT),a_),Power(x_,m_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Power(x_,r_DEFAULT))),q_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Times(Power(x,m),Power(Plus(d,Times(e,Power(x,r))),q)),x))),Subtract(Simp(Star(Plus(a,Times(b,Log(Times(c,Power(x,n))))),u),x),Simp(Star(Times(b,n),Integrate(SimplifyIntegrand(Times(u,Power(x,CN1)),x),x)),x))),And(FreeQ(List(a,b,c,d,e,n,r),x),IGtQ(q,C0),IntegerQ(m),Not(And(EqQ(q,C1),EqQ(m,CN1)))))),
IIntegrate(2802,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),Power(x_,CN1),Power(Plus(d_,Times(e_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Subtract(Simp(Star(Power(d,CN1),Integrate(Times(Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),p),Power(x,CN1)),x)),x),Simp(Star(Times(e,Power(d,CN1)),Integrate(Times(Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),p),Power(Plus(d,Times(e,x)),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,n),x),IGtQ(p,C0)))),
IIntegrate(2803,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),$p("§polyx")),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Times($s("§polyx"),Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),p)),x),x),And(FreeQ(List(a,b,c,n,p),x),PolynomialQ($s("§polyx"),x)))),
IIntegrate(2804,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),$p("§rfx")),x_Symbol),
    Condition(With(list(Set(u,ExpandIntegrand(Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),p),$s("§rfx"),x))),Condition(Integrate(u,x),SumQ(u))),And(FreeQ(List(a,b,c,n),x),RationalFunctionQ($s("§rfx"),x),IGtQ(p,C0)))),
IIntegrate(2805,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),$p("§rfx")),x_Symbol),
    Condition(With(list(Set(u,ExpandIntegrand(Times($s("§rfx"),Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),p)),x))),Condition(Integrate(u,x),SumQ(u))),And(FreeQ(List(a,b,c,n),x),RationalFunctionQ($s("§rfx"),x),IGtQ(p,C0)))),
IIntegrate(2806,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),$p("§afx")),x_Symbol),
    Condition(Unintegrable(Times($s("§afx"),Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),p)),x),And(FreeQ(List(a,b,c,n,p),x),AlgebraicFunctionQ($s("§afx"),x,True)))),
IIntegrate(2807,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),Power(Plus(Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),e_DEFAULT),d_),q_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Times(Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),p),Power(Plus(d,Times(e,Log(Times(c,Power(x,n))))),q)),x),x),And(FreeQ(List(a,b,c,d,e,n),x),IntegerQ(p),IntegerQ(q)))),
IIntegrate(2808,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),Plus(d_DEFAULT,Times(Log(Times(f_DEFAULT,Power(x_,r_DEFAULT))),e_DEFAULT))),x_Symbol),
    Condition(With(list(Set(u,IntHide(Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),p),x))),Subtract(Simp(Star(Plus(d,Times(e,Log(Times(f,Power(x,r))))),u),x),Simp(Star(Times(e,r),Integrate(SimplifyIntegrand(Times(u,Power(x,CN1)),x),x)),x))),FreeQ(List(a,b,c,d,e,f,n,p,r),x))),
IIntegrate(2809,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),Power(Plus(d_DEFAULT,Times(Log(Times(f_DEFAULT,Power(x_,r_DEFAULT))),e_DEFAULT)),q_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(x,Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),p),Power(Plus(d,Times(e,Log(Times(f,Power(x,r))))),q)),x),Negate(Simp(Star(Times(b,n,p),Integrate(Times(Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),Subtract(p,C1)),Power(Plus(d,Times(e,Log(Times(f,Power(x,r))))),q)),x)),x)),Negate(Simp(Star(Times(e,q,r),Integrate(Times(Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),p),Power(Plus(d,Times(e,Log(Times(f,Power(x,r))))),Subtract(q,C1))),x)),x))),And(FreeQ(List(a,b,c,d,e,f,n,r),x),IGtQ(p,C0),IGtQ(q,C0)))),
IIntegrate(2810,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),Power(Plus(d_DEFAULT,Times(Log(Times(f_DEFAULT,Power(x_,r_DEFAULT))),e_DEFAULT)),q_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),p),Power(Plus(d,Times(e,Log(Times(f,Power(x,r))))),q)),x),FreeQ(List(a,b,c,d,e,f,n,p,q,r),x))),
IIntegrate(2811,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(v_),b_DEFAULT)),p_DEFAULT),Power(Plus(c_DEFAULT,Times(Log(v_),d_DEFAULT)),q_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(Coeff(v,x,C1),CN1),Subst(Integrate(Times(Power(Plus(a,Times(b,Log(x))),p),Power(Plus(c,Times(d,Log(x))),q)),x),x,v)),x),And(FreeQ(List(a,b,c,d,p,q),x),LinearQ(v,x),NeQ(Coeff(v,x,C0),C0)))),
IIntegrate(2812,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),Power(Plus(d_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),e_DEFAULT)),q_DEFAULT),Power(x_,CN1)),x_Symbol),
    Condition(Simp(Star(Power(n,CN1),Subst(Integrate(Times(Power(Plus(a,Times(b,x)),p),Power(Plus(d,Times(e,x)),q)),x),x,Log(Times(c,Power(x,n))))),x),FreeQ(List(a,b,c,d,e,n,p,q),x))),
IIntegrate(2813,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),Plus(d_DEFAULT,Times(Log(Times(f_DEFAULT,Power(x_,r_DEFAULT))),e_DEFAULT)),Power(Times(g_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Times(Power(Times(g,x),m),Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),p)),x))),Subtract(Simp(Star(Plus(d,Times(e,Log(Times(f,Power(x,r))))),u),x),Simp(Star(Times(e,r),Integrate(SimplifyIntegrand(Times(u,Power(x,CN1)),x),x)),x))),And(FreeQ(List(a,b,c,d,e,f,g,m,n,p,r),x),Not(And(EqQ(p,C1),EqQ(a,C0),NeQ(d,C0)))))),
IIntegrate(2814,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),Power(Plus(d_DEFAULT,Times(Log(Times(f_DEFAULT,Power(x_,r_DEFAULT))),e_DEFAULT)),q_DEFAULT),Power(Times(g_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Times(g,x),Plus(m,C1)),Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),p),Power(Plus(d,Times(e,Log(Times(f,Power(x,r))))),q),Power(Times(g,Plus(m,C1)),CN1)),x),Negate(Simp(Star(Times(b,n,p,Power(Plus(m,C1),CN1)),Integrate(Times(Power(Times(g,x),m),Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),Subtract(p,C1)),Power(Plus(d,Times(e,Log(Times(f,Power(x,r))))),q)),x)),x)),Negate(Simp(Star(Times(e,q,r,Power(Plus(m,C1),CN1)),Integrate(Times(Power(Times(g,x),m),Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),p),Power(Plus(d,Times(e,Log(Times(f,Power(x,r))))),Subtract(q,C1))),x)),x))),And(FreeQ(List(a,b,c,d,e,f,g,m,n,r),x),IGtQ(p,C0),IGtQ(q,C0),NeQ(m,CN1)))),
IIntegrate(2815,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),Power(Plus(d_DEFAULT,Times(Log(Times(f_DEFAULT,Power(x_,r_DEFAULT))),e_DEFAULT)),q_DEFAULT),Power(Times(g_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(g,x),m),Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),p),Power(Plus(d,Times(e,Log(Times(f,Power(x,r))))),q)),x),FreeQ(List(a,b,c,d,e,f,g,m,n,p,q,r),x))),
IIntegrate(2816,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(v_),b_DEFAULT)),p_DEFAULT),Power(Plus(c_DEFAULT,Times(Log(v_),d_DEFAULT)),q_DEFAULT),Power(u_,m_DEFAULT)),x_Symbol),
    Condition(With(List(Set(e,Coeff(u,x,C0)),Set(f,Coeff(u,x,C1)),Set(g,Coeff(v,x,C0)),Set(h,Coeff(v,x,C1))),Condition(Simp(Star(Power(h,CN1),Subst(Integrate(Times(Power(Times(f,x,Power(h,CN1)),m),Power(Plus(a,Times(b,Log(x))),p),Power(Plus(c,Times(d,Log(x))),q)),x),x,v)),x),And(EqQ(Subtract(Times(f,g),Times(e,h)),C0),NeQ(g,C0)))),And(FreeQ(List(a,b,c,d,m,p,q),x),LinearQ(list(u,v),x)))),
IIntegrate(2817,Integrate(Times(Log(Times(d_DEFAULT,Power(Plus(e_,Times(f_DEFAULT,Power(x_,m_DEFAULT))),r_DEFAULT))),Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Log(Times(d,Power(Plus(e,Times(f,Power(x,m))),r))),x))),Subtract(Simp(Star(Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),p),u),x),Simp(Star(Times(b,n,p),Integrate(Star(Times(Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),Subtract(p,C1)),Power(x,CN1)),u),x)),x))),And(FreeQ(List(a,b,c,d,e,f,r,m,n),x),IGtQ(p,C0),RationalQ(m),Or(EqQ(p,C1),And(FractionQ(m),IntegerQ(Power(m,CN1))),And(EqQ(r,C1),EqQ(m,C1),EqQ(Times(d,e),C1)))))),
IIntegrate(2818,Integrate(Times(Log(Times(d_DEFAULT,Power(Plus(e_,Times(f_DEFAULT,Power(x_,m_DEFAULT))),r_DEFAULT))),Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),p),x))),Subtract(Simp(Star(Log(Times(d,Power(Plus(e,Times(f,Power(x,m))),r))),u),x),Simp(Star(Times(f,m,r),Integrate(Star(Times(Power(x,Subtract(m,C1)),Power(Plus(e,Times(f,Power(x,m))),CN1)),u),x)),x))),And(FreeQ(List(a,b,c,d,e,f,r,m,n),x),IGtQ(p,C0),IntegerQ(m)))),
IIntegrate(2819,Integrate(Times(Log(Times(d_DEFAULT,Power(Plus(e_,Times(f_DEFAULT,Power(x_,m_DEFAULT))),r_DEFAULT))),Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),p),Log(Times(d,Power(Plus(e,Times(f,Power(x,m))),r)))),x),FreeQ(List(a,b,c,d,e,f,r,m,n,p),x))),
IIntegrate(2820,Integrate(Times(Log(Times(d_DEFAULT,Power(u_,r_DEFAULT))),Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Log(Times(d,Power(ExpandToSum(u,x),r))),Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),p)),x),And(FreeQ(List(a,b,c,d,r,n,p),x),BinomialQ(u,x),Not(BinomialMatchQ(u,x)))))
  );
}
