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
class IntRules135 { 
  public static IAST RULES = List( 
IIntegrate(2701,Integrate(Times(Power(F_,Times(g_DEFAULT,Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),n_DEFAULT))),Power(u_,m_DEFAULT),Power(Plus(a_,Times(c_,Sqr(x_))),CN1)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Power(FSymbol,Times(g,Power(Plus(d,Times(e,x)),n))),Times(Power(u,m),Power(Plus(a,Times(c,Sqr(x))),CN1)),x),x),And(FreeQ(List(FSymbol,a,c,d,e,g,n),x),PolynomialQ(u,x),IntegerQ(m)))),
IIntegrate(2702,Integrate(Power(F_,Times(Power(x_,CN2),Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,C4))))),x_Symbol),
    Condition(Subtract(Simp(Times(CSqrtPi,Exp(Times(C2,Sqrt(Times(CN1,a,Log(FSymbol))),Sqrt(Times(CN1,b,Log(FSymbol))))),Erf(Times(Plus(Sqrt(Times(CN1,a,Log(FSymbol))),Times(Sqrt(Times(CN1,b,Log(FSymbol))),Sqr(x))),Power(x,CN1))),Power(Times(C4,Sqrt(Times(CN1,b,Log(FSymbol)))),CN1)),x),Simp(Times(CSqrtPi,Exp(Times(CN2,Sqrt(Times(CN1,a,Log(FSymbol))),Sqrt(Times(CN1,b,Log(FSymbol))))),Erf(Times(Subtract(Sqrt(Times(CN1,a,Log(FSymbol))),Times(Sqrt(Times(CN1,b,Log(FSymbol))),Sqr(x))),Power(x,CN1))),Power(Times(C4,Sqrt(Times(CN1,b,Log(FSymbol)))),CN1)),x)),FreeQ(list(FSymbol,a,b),x))),
IIntegrate(2703,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(Exp(x_),Power(x_,m_DEFAULT)),n_)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Power(Plus(Exp(x),Power(x,m)),Plus(n,C1)),Power(Plus(n,C1),CN1)),x),Integrate(Power(Plus(Exp(x),Power(x,m)),Plus(n,C1)),x),Simp(Star(m,Integrate(Times(Power(x,Subtract(m,C1)),Power(Plus(Exp(x),Power(x,m)),n)),x)),x)),And(RationalQ(m,n),GtQ(m,C0),LtQ(n,C0),NeQ(n,CN1)))),
IIntegrate(2704,Integrate(Times(u_DEFAULT,Power(F_,Times(a_DEFAULT,Plus(Times(Log(z_),b_DEFAULT),v_DEFAULT)))),x_Symbol),
    Condition(Integrate(Times(u,Power(FSymbol,Times(a,v)),Power(z,Times(a,b,Log(FSymbol)))),x),FreeQ(list(FSymbol,a,b),x))),
IIntegrate(2705,Integrate(Power(F_,Times(Plus(a_DEFAULT,Times(Sqr(Log(Times(c_DEFAULT,Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),n_DEFAULT)))),b_DEFAULT)),f_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(Plus(d,Times(e,x)),Power(Times(e,n,Power(Times(c,Power(Plus(d,Times(e,x)),n)),Power(n,CN1))),CN1)),Subst(Integrate(Exp(Plus(Times(a,f,Log(FSymbol)),Times(x,Power(n,CN1)),Times(b,f,Log(FSymbol),Sqr(x)))),x),x,Log(Times(c,Power(Plus(d,Times(e,x)),n))))),x),FreeQ(List(FSymbol,a,b,c,d,e,f,n),x))),
IIntegrate(2706,Integrate(Times(Power(F_,Times(Plus(a_DEFAULT,Times(Sqr(Log(Times(c_DEFAULT,Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),n_DEFAULT)))),b_DEFAULT)),f_DEFAULT)),Power(Plus(g_DEFAULT,Times(h_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(Power(Plus(g,Times(h,x)),Plus(m,C1)),Power(Times(h,n,Power(Times(c,Power(Plus(d,Times(e,x)),n)),Times(Plus(m,C1),Power(n,CN1)))),CN1)),Subst(Integrate(Exp(Plus(Times(a,f,Log(FSymbol)),Times(Plus(m,C1),x,Power(n,CN1)),Times(b,f,Log(FSymbol),Sqr(x)))),x),x,Log(Times(c,Power(Plus(d,Times(e,x)),n))))),x),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g,h,m,n),x),EqQ(Subtract(Times(e,g),Times(d,h)),C0)))),
IIntegrate(2707,Integrate(Times(Power(F_,Times(Plus(a_DEFAULT,Times(Sqr(Log(Times(c_DEFAULT,Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),n_DEFAULT)))),b_DEFAULT)),f_DEFAULT)),Power(Plus(g_DEFAULT,Times(h_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(Power(e,Plus(m,C1)),CN1),Subst(Integrate(ExpandIntegrand(Power(FSymbol,Times(f,Plus(a,Times(b,Sqr(Log(Times(c,Power(x,n)))))))),Power(Plus(Times(e,g),Times(CN1,d,h),Times(h,x)),m),x),x),x,Plus(d,Times(e,x)))),x),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g,h,n),x),IGtQ(m,C0)))),
IIntegrate(2708,Integrate(Times(Power(F_,Times(Plus(a_DEFAULT,Times(Sqr(Log(Times(c_DEFAULT,Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),n_DEFAULT)))),b_DEFAULT)),f_DEFAULT)),Power(Plus(g_DEFAULT,Times(h_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(FSymbol,Times(f,Plus(a,Times(b,Sqr(Log(Times(c,Power(Plus(d,Times(e,x)),n)))))))),Power(Plus(g,Times(h,x)),m)),x),FreeQ(List(FSymbol,a,b,c,d,e,f,g,h,m,n),x))),
IIntegrate(2709,Integrate(Power(F_,Times(Sqr(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),n_DEFAULT))),b_DEFAULT))),f_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(c,Times(C2,a,b,f,Log(FSymbol))),Integrate(Times(Power(Plus(d,Times(e,x)),Times(C2,a,b,f,n,Log(FSymbol))),Power(FSymbol,Plus(Times(Sqr(a),f),Times(Sqr(b),f,Sqr(Log(Times(c,Power(Plus(d,Times(e,x)),n)))))))),x)),x),And(FreeQ(List(FSymbol,a,b,c,d,e,f,n),x),IntegerQ(Times(C2,a,b,f,Log(FSymbol)))))),
IIntegrate(2710,Integrate(Power(F_,Times(Sqr(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),n_DEFAULT))),b_DEFAULT))),f_DEFAULT)),x_Symbol),
    Condition(Simp(Times(Power(Times(c,Power(Plus(d,Times(e,x)),n)),Times(C2,a,b,f,Log(FSymbol))),Power(Power(Plus(d,Times(e,x)),Times(C2,a,b,f,n,Log(FSymbol))),CN1),Integrate(Times(Power(Plus(d,Times(e,x)),Times(C2,a,b,f,n,Log(FSymbol))),Power(FSymbol,Plus(Times(Sqr(a),f),Times(Sqr(b),f,Sqr(Log(Times(c,Power(Plus(d,Times(e,x)),n)))))))),x)),x),And(FreeQ(List(FSymbol,a,b,c,d,e,f,n),x),Not(IntegerQ(Times(C2,a,b,f,Log(FSymbol))))))),
IIntegrate(2711,Integrate(Times(Power(F_,Times(Sqr(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),n_DEFAULT))),b_DEFAULT))),f_DEFAULT)),Power(Plus(g_DEFAULT,Times(h_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(Power(h,m),Power(c,Times(C2,a,b,f,Log(FSymbol))),Power(Power(e,m),CN1)),Integrate(Times(Power(Plus(d,Times(e,x)),Plus(m,Times(C2,a,b,f,n,Log(FSymbol)))),Power(FSymbol,Plus(Times(Sqr(a),f),Times(Sqr(b),f,Sqr(Log(Times(c,Power(Plus(d,Times(e,x)),n)))))))),x)),x),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g,h,m,n),x),EqQ(Subtract(Times(e,g),Times(d,h)),C0),IntegerQ(Times(C2,a,b,f,Log(FSymbol))),Or(IntegerQ(m),EqQ(h,e))))),
IIntegrate(2712,Integrate(Times(Power(F_,Times(Sqr(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),n_DEFAULT))),b_DEFAULT))),f_DEFAULT)),Power(Plus(g_DEFAULT,Times(h_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Times(Power(Plus(g,Times(h,x)),m),Power(Times(c,Power(Plus(d,Times(e,x)),n)),Times(C2,a,b,f,Log(FSymbol))),Power(Power(Plus(d,Times(e,x)),Plus(m,Times(C2,a,b,f,n,Log(FSymbol)))),CN1),Integrate(Times(Power(Plus(d,Times(e,x)),Plus(m,Times(C2,a,b,f,n,Log(FSymbol)))),Power(FSymbol,Plus(Times(Sqr(a),f),Times(Sqr(b),f,Sqr(Log(Times(c,Power(Plus(d,Times(e,x)),n)))))))),x)),x),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g,h,m,n),x),EqQ(Subtract(Times(e,g),Times(d,h)),C0)))),
IIntegrate(2713,Integrate(Times(Power(F_,Times(Sqr(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),n_DEFAULT))),b_DEFAULT))),f_DEFAULT)),Power(Plus(g_DEFAULT,Times(h_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(Power(e,Plus(m,C1)),CN1),Subst(Integrate(ExpandIntegrand(Power(FSymbol,Times(f,Sqr(Plus(a,Times(b,Log(Times(c,Power(x,n)))))))),Power(Plus(Times(e,g),Times(CN1,d,h),Times(h,x)),m),x),x),x,Plus(d,Times(e,x)))),x),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g,h,n),x),IGtQ(m,C0)))),
IIntegrate(2714,Integrate(Times(Power(F_,Times(Sqr(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),n_DEFAULT))),b_DEFAULT))),f_DEFAULT)),Power(Plus(g_DEFAULT,Times(h_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(FSymbol,Times(f,Sqr(Plus(a,Times(b,Log(Times(c,Power(Plus(d,Times(e,x)),n)))))))),Power(Plus(g,Times(h,x)),m)),x),FreeQ(List(FSymbol,a,b,c,d,e,f,g,h,m,n),x))),
IIntegrate(2715,Integrate(Log(Plus(a_,Times(b_DEFAULT,Power(Power(F_,Times(e_DEFAULT,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),n_DEFAULT)))),x_Symbol),
    Condition(Simp(Star(Power(Times(d,e,n,Log(FSymbol)),CN1),Subst(Integrate(Times(Log(Plus(a,Times(b,x))),Power(x,CN1)),x),x,Power(Power(FSymbol,Times(e,Plus(c,Times(d,x)))),n))),x),And(FreeQ(List(FSymbol,a,b,c,d,e,n),x),GtQ(a,C0)))),
IIntegrate(2716,Integrate(Log(Plus(a_,Times(b_DEFAULT,Power(Power(F_,Times(e_DEFAULT,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),n_DEFAULT)))),x_Symbol),
    Condition(Subtract(Simp(Times(x,Log(Plus(a,Times(b,Power(Power(FSymbol,Times(e,Plus(c,Times(d,x)))),n))))),x),Simp(Star(Times(b,d,e,n,Log(FSymbol)),Integrate(Times(x,Power(Power(FSymbol,Times(e,Plus(c,Times(d,x)))),n),Power(Plus(a,Times(b,Power(Power(FSymbol,Times(e,Plus(c,Times(d,x)))),n))),CN1)),x)),x)),And(FreeQ(List(FSymbol,a,b,c,d,e,n),x),Not(GtQ(a,C0))))),
IIntegrate(2717,Integrate(Times(u_DEFAULT,Power(Times(a_DEFAULT,Power(F_,v_)),n_)),x_Symbol),
    Condition(Simp(Star(Times(Power(Times(a,Power(FSymbol,v)),n),Power(Power(FSymbol,Times(n,v)),CN1)),Integrate(Times(u,Power(FSymbol,Times(n,v))),x)),x),And(FreeQ(list(FSymbol,a,n),x),Not(IntegerQ(n))))),
IIntegrate(2718,Integrate(Power(F_,Times(d_DEFAULT,Power(Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),n_)),m_))),x_Symbol),
    Condition(Simp(Times(Plus(a,Times(b,x)),Power(FSymbol,Times(d,Power(Times(c,Power(Plus(a,Times(b,x)),n)),m))),Power(Times(b,d,Power(Times(c,Power(Plus(a,Times(b,x)),n)),m),Log(FSymbol)),CN1)),x),And(FreeQ(List(FSymbol,a,b,c,d,m,n),x),EqQ(Times(m,n),C1)))),
IIntegrate(2719,Integrate(Power(F_,Times(d_DEFAULT,Power(Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),n_)),m_))),x_Symbol),
    Condition(Simp(Times(CN1,Plus(a,Times(b,x)),Gamma(Power(Times(m,n),CN1),Times(CN1,d,Power(Times(c,Power(Plus(a,Times(b,x)),n)),m),Log(FSymbol))),Power(Times(b,m,n,Power(Times(CN1,d,Power(Times(c,Power(Plus(a,Times(b,x)),n)),m),Log(FSymbol)),Power(Times(m,n),CN1))),CN1)),x),FreeQ(List(FSymbol,a,b,c,d,m,n),x))),
IIntegrate(2720,Integrate(u_,x_Symbol),
    Condition(With(list(Set(v,FunctionOfExponential(u,x))),Simp(Star(Times(v,Power(D(v,x),CN1)),Subst(Integrate(Times(FunctionOfExponentialFunction(u,x),Power(x,CN1)),x),x,v)),x)),And(FunctionOfExponentialQ(u,x),Not(MatchQ(u,Condition(Times(w_,Power(Times(a_DEFAULT,Power(v_,n_)),m_)),And(FreeQ(list(a,m,n),x),IntegerQ(Times(m,n)))))),Not(MatchQ(u,Condition(Times(Exp(Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x)))),$(F_,v_)),And(FreeQ(list(a,b,c),x),InverseFunctionQ(F(x)))))))))
  );
}
