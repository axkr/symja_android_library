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
class IntRules131 { 
  public static IAST RULES = List( 
IIntegrate(2621,Integrate(Times(Power(Power(F_,Times(g_DEFAULT,Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Power(F_,Times(g_DEFAULT,Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_DEFAULT))),p_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),m),Power(Plus(a,Times(b,Power(Power(FSymbol,Times(g,Plus(e,Times(f,x)))),n))),Plus(p,C1)),Power(Times(b,f,g,n,Plus(p,C1),Log(FSymbol)),CN1)),x),Simp(Star(Times(d,m,Power(Times(b,f,g,n,Plus(p,C1),Log(FSymbol)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Subtract(m,C1)),Power(Plus(a,Times(b,Power(Power(FSymbol,Times(g,Plus(e,Times(f,x)))),n))),Plus(p,C1))),x)),x)),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g,m,n,p),x),NeQ(p,CN1)))),
IIntegrate(2622,Integrate(Times(Power(Power(F_,Times(g_DEFAULT,Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Power(F_,Times(g_DEFAULT,Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_DEFAULT))),p_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Power(FSymbol,Times(g,Plus(e,Times(f,x)))),n),Power(Plus(a,Times(b,Power(Power(FSymbol,Times(g,Plus(e,Times(f,x)))),n))),p),Power(Plus(c,Times(d,x)),m)),x),FreeQ(List(FSymbol,a,b,c,d,e,f,g,m,n,p),x))),
IIntegrate(2623,Integrate(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Power(F_,Times(g_DEFAULT,Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_DEFAULT))),p_DEFAULT),Power(Times(k_DEFAULT,Power(G_,Times(j_DEFAULT,Plus(h_DEFAULT,Times(i_DEFAULT,x_))))),q_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(Power(Times(k,Power(GSymbol,Times(j,Plus(h,Times(i,x))))),q),Power(Power(Power(FSymbol,Times(g,Plus(e,Times(f,x)))),n),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(Power(FSymbol,Times(g,Plus(e,Times(f,x)))),n),Power(Plus(a,Times(b,Power(Power(FSymbol,Times(g,Plus(e,Times(f,x)))),n))),p)),x)),x),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g,h,i,j,k,m,n,p,q),x),EqQ(Subtract(Times(f,g,n,Log(FSymbol)),Times(i,j,q,Log(GSymbol))),C0),NeQ(Subtract(Power(Times(k,Power(GSymbol,Times(j,Plus(h,Times(i,x))))),q),Power(Power(FSymbol,Times(g,Plus(e,Times(f,x)))),n)),C0)))),
IIntegrate(2624,Integrate(Power(Power(F_,v_),n_DEFAULT),x_Symbol),
    Condition(Simp(Times(Power(Power(FSymbol,v),n),Power(Times(n,Log(FSymbol),D(v,x)),CN1)),x),And(FreeQ(list(FSymbol,n),x),LinearQ(v,x)))),
IIntegrate(2625,Integrate(Times(Power(F_,v_),$p("§px")),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Times($s("§px"),Power(FSymbol,v)),x),x),And(FreeQ(FSymbol,x),PolynomialQ($s("§px"),x),LinearQ(v,x),TrueQ(False)))),
IIntegrate(2626,Integrate(Times(Power(F_,v_),$p("§px")),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Power(FSymbol,v),$s("§px"),x),x),And(FreeQ(FSymbol,x),PolynomialQ($s("§px"),x),LinearQ(v,x),Not(TrueQ(False))))),
IIntegrate(2627,Integrate(Times(Power(F_,v_),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT),Plus(f_DEFAULT,Times(g_DEFAULT,x_))),x_Symbol),
    Condition(Simp(Times(g,Power(Plus(d,Times(e,x)),Plus(m,C1)),Power(FSymbol,v),Power(Times(D(v,x),e,Log(FSymbol)),CN1)),x),And(FreeQ(List(FSymbol,d,e,f,g,m),x),LinearQ(v,x),EqQ(Subtract(Times(e,g,Plus(m,C1)),Times(D(v,x),Subtract(Times(e,f),Times(d,g)),Log(FSymbol))),C0)))),
IIntegrate(2628,Integrate(Times(Power(F_,v_),$p("§px"),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Times($s("§px"),Power(Plus(d,Times(e,x)),m),Power(FSymbol,v)),x),x),And(FreeQ(List(FSymbol,d,e,m),x),PolynomialQ($s("§px"),x),LinearQ(v,x),TrueQ(False)))),
IIntegrate(2629,Integrate(Times(Power(F_,v_),$p("§px"),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Power(FSymbol,v),Times($s("§px"),Power(Plus(d,Times(e,x)),m)),x),x),And(FreeQ(List(FSymbol,d,e,m),x),PolynomialQ($s("§px"),x),LinearQ(v,x),Not(TrueQ(False))))),
IIntegrate(2630,Integrate(Times(Power(Log(Times(d_DEFAULT,x_)),n_DEFAULT),Power(F_,v_),Plus(e_,Times(Log(Times(d_DEFAULT,x_)),h_DEFAULT,Plus(f_DEFAULT,Times(g_DEFAULT,x_))))),x_Symbol),
    Condition(Simp(Times(e,x,Power(FSymbol,v),Power(Log(Times(d,x)),Plus(n,C1)),Power(Plus(n,C1),CN1)),x),And(FreeQ(List(FSymbol,d,e,f,g,h,n),x),LinearQ(v,x),EqQ(e,Times(f,h,Plus(n,C1))),EqQ(Times(g,h,Plus(n,C1)),Times(D(v,x),e,Log(FSymbol))),NeQ(n,CN1)))),
IIntegrate(2631,Integrate(Times(Power(Log(Times(d_DEFAULT,x_)),n_DEFAULT),Power(F_,v_),Power(x_,m_DEFAULT),Plus(e_,Times(Log(Times(d_DEFAULT,x_)),h_DEFAULT,Plus(f_DEFAULT,Times(g_DEFAULT,x_))))),x_Symbol),
    Condition(Simp(Times(e,Power(x,Plus(m,C1)),Power(FSymbol,v),Power(Log(Times(d,x)),Plus(n,C1)),Power(Plus(n,C1),CN1)),x),And(FreeQ(List(FSymbol,d,e,f,g,h,m,n),x),LinearQ(v,x),EqQ(Times(e,Plus(m,C1)),Times(f,h,Plus(n,C1))),EqQ(Times(g,h,Plus(n,C1)),Times(D(v,x),e,Log(FSymbol))),NeQ(n,CN1)))),
IIntegrate(2632,Integrate(Power(F_,Plus(a_DEFAULT,Times(b_DEFAULT,Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),x_Symbol),
    Condition(Simp(Times(Power(FSymbol,Plus(a,Times(b,Plus(c,Times(d,x))))),Power(Times(b,d,Log(FSymbol)),CN1)),x),FreeQ(List(FSymbol,a,b,c,d),x))),
IIntegrate(2633,Integrate(Power(F_,Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),x_Symbol),
    Condition(Simp(Times(Power(FSymbol,a),CSqrtPi,Erfi(Times(Plus(c,Times(d,x)),Rt(Times(b,Log(FSymbol)),C2))),Power(Times(C2,d,Rt(Times(b,Log(FSymbol)),C2)),CN1)),x),And(FreeQ(List(FSymbol,a,b,c,d),x),PosQ(b)))),
IIntegrate(2634,Integrate(Power(F_,Plus(a_DEFAULT,Times(b_DEFAULT,Sqr(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),x_Symbol),
    Condition(Simp(Times(Power(FSymbol,a),CSqrtPi,Erf(Times(Plus(c,Times(d,x)),Rt(Times(CN1,b,Log(FSymbol)),C2))),Power(Times(C2,d,Rt(Times(CN1,b,Log(FSymbol)),C2)),CN1)),x),And(FreeQ(List(FSymbol,a,b,c,d),x),NegQ(b)))),
IIntegrate(2635,Integrate(Power(F_,Plus(a_DEFAULT,Times(b_DEFAULT,Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),n_)))),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(c,Times(d,x)),Power(FSymbol,Plus(a,Times(b,Power(Plus(c,Times(d,x)),n)))),Power(d,CN1)),x),Simp(Star(Times(b,n,Log(FSymbol)),Integrate(Times(Power(Plus(c,Times(d,x)),n),Power(FSymbol,Plus(a,Times(b,Power(Plus(c,Times(d,x)),n))))),x)),x)),And(FreeQ(List(FSymbol,a,b,c,d),x),IntegerQ(Times(C2,Power(n,CN1))),ILtQ(n,C0)))),
IIntegrate(2636,Integrate(Power(F_,Plus(a_DEFAULT,Times(b_DEFAULT,Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),n_)))),x_Symbol),
    Condition(With(list(Set(k,Denominator(n))),Simp(Star(Times(k,Power(d,CN1)),Subst(Integrate(Times(Power(x,Subtract(k,C1)),Power(FSymbol,Plus(a,Times(b,Power(x,Times(k,n)))))),x),x,Power(Plus(c,Times(d,x)),Power(k,CN1)))),x)),And(FreeQ(List(FSymbol,a,b,c,d),x),IntegerQ(Times(C2,Power(n,CN1))),Not(IntegerQ(n))))),
IIntegrate(2637,Integrate(Power(F_,Plus(a_DEFAULT,Times(b_DEFAULT,Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),n_)))),x_Symbol),
    Condition(Simp(Times(CN1,Power(FSymbol,a),Plus(c,Times(d,x)),Gamma(Power(n,CN1),Times(CN1,b,Power(Plus(c,Times(d,x)),n),Log(FSymbol))),Power(Times(d,n,Power(Times(CN1,b,Power(Plus(c,Times(d,x)),n),Log(FSymbol)),Power(n,CN1))),CN1)),x),And(FreeQ(List(FSymbol,a,b,c,d,n),x),Not(IntegerQ(Times(C2,Power(n,CN1))))))),
IIntegrate(2638,Integrate(Times(Power(F_,Plus(a_DEFAULT,Times(b_DEFAULT,Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),n_)))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Times(Power(Plus(e,Times(f,x)),n),Power(FSymbol,Plus(a,Times(b,Power(Plus(c,Times(d,x)),n)))),Power(Times(b,f,n,Power(Plus(c,Times(d,x)),n),Log(FSymbol)),CN1)),x),And(FreeQ(List(FSymbol,a,b,c,d,e,f,n),x),EqQ(m,Subtract(n,C1)),EqQ(Subtract(Times(d,e),Times(c,f)),C0)))),
IIntegrate(2639,Integrate(Times(Power(F_,Plus(a_DEFAULT,Times(b_DEFAULT,Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),n_)))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Simp(Times(Power(FSymbol,a),ExpIntegralEi(Times(b,Power(Plus(c,Times(d,x)),n),Log(FSymbol))),Power(Times(f,n),CN1)),x),And(FreeQ(List(FSymbol,a,b,c,d,e,f,n),x),EqQ(Subtract(Times(d,e),Times(c,f)),C0)))),
IIntegrate(2640,Integrate(Times(Power(F_,Plus(a_DEFAULT,Times(b_DEFAULT,Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),n_)))),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(Times(d,Plus(m,C1)),CN1),Subst(Integrate(Power(FSymbol,Plus(a,Times(b,Sqr(x)))),x),x,Power(Plus(c,Times(d,x)),Plus(m,C1)))),x),And(FreeQ(List(FSymbol,a,b,c,d,m,n),x),EqQ(n,Times(C2,Plus(m,C1))))))
  );
}
