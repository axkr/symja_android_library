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
class IntRules301 { 
  public static IAST RULES = List( 
IIntegrate(6021,Integrate(Times(Power(Plus(Times(Cosh(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),g_DEFAULT),f_),n_DEFAULT),Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),x_Symbol),
    Condition(Simp(Star(Times(Power(C2,n),Power(g,n)),Integrate(Times(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Power(Sinh(Plus(Times(C1D2,d),Times(e,C1D2,x))),Times(C2,n))),x)),x),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g),x),EqQ(Plus(f,g),C0),ILtQ(n,C0)))),
IIntegrate(6022,Integrate(Times(Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Plus(f_,Times(g_DEFAULT,Sinh(Plus(d_DEFAULT,Times(e_DEFAULT,x_))))),n_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(Power(Plus(f,Times(g,Sinh(Plus(d,Times(e,x))))),n),Power(Power(Cosh(Plus(Times(C1D2,d),Times(CN1,f,Pi,Power(Times(C4,g),CN1)),Times(e,C1D2,x))),Times(C2,n)),CN1)),Integrate(Times(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Power(Cosh(Plus(Times(C1D2,d),Times(CN1,f,Pi,Power(Times(C4,g),CN1)),Times(e,C1D2,x))),Times(C2,n))),x)),x),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g,n),x),EqQ(Plus(Sqr(f),Sqr(g)),C0),Not(IntegerQ(n))))),
IIntegrate(6023,Integrate(Times(Power(Plus(Times(Cosh(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),g_DEFAULT),f_),n_DEFAULT),Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),x_Symbol),
    Condition(Simp(Star(Times(Power(Plus(f,Times(g,Cosh(Plus(d,Times(e,x))))),n),Power(Power(Cosh(Plus(Times(C1D2,d),Times(e,C1D2,x))),Times(C2,n)),CN1)),Integrate(Times(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Power(Cosh(Plus(Times(C1D2,d),Times(e,C1D2,x))),Times(C2,n))),x)),x),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g,n),x),EqQ(Subtract(f,g),C0),Not(IntegerQ(n))))),
IIntegrate(6024,Integrate(Times(Power(Plus(Times(Cosh(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),g_DEFAULT),f_),n_DEFAULT),Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),x_Symbol),
    Condition(Simp(Star(Times(Power(Plus(f,Times(g,Cosh(Plus(d,Times(e,x))))),n),Power(Power(Sinh(Plus(Times(C1D2,d),Times(e,C1D2,x))),Times(C2,n)),CN1)),Integrate(Times(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Power(Sinh(Plus(Times(C1D2,d),Times(e,C1D2,x))),Times(C2,n))),x)),x),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g,n),x),EqQ(Plus(f,g),C0),Not(IntegerQ(n))))),
IIntegrate(6025,Integrate(Times(Power(Cosh(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),m_DEFAULT),Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Plus(f_,Times(g_DEFAULT,Sinh(Plus(d_DEFAULT,Times(e_DEFAULT,x_))))),n_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(g,n),Integrate(Times(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Power(Tanh(Subtract(Plus(Times(C1D2,d),Times(e,C1D2,x)),Times(f,Pi,Power(Times(C4,g),CN1)))),m)),x)),x),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g),x),EqQ(Plus(Sqr(f),Sqr(g)),C0),IntegersQ(m,n),EqQ(Plus(m,n),C0)))),
IIntegrate(6026,Integrate(Times(Power(Plus(Times(Cosh(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),g_DEFAULT),f_),n_DEFAULT),Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Sinh(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(g,n),Integrate(Times(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Power(Tanh(Plus(Times(C1D2,d),Times(e,C1D2,x))),m)),x)),x),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g),x),EqQ(Subtract(f,g),C0),IntegersQ(m,n),EqQ(Plus(m,n),C0)))),
IIntegrate(6027,Integrate(Times(Power(Plus(Times(Cosh(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),g_DEFAULT),f_),n_DEFAULT),Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Sinh(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(g,n),Integrate(Times(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Power(Coth(Plus(Times(C1D2,d),Times(e,C1D2,x))),m)),x)),x),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g),x),EqQ(Plus(f,g),C0),IntegersQ(m,n),EqQ(Plus(m,n),C0)))),
IIntegrate(6028,Integrate(Times(Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Plus(Times(Cosh(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),i_DEFAULT),h_),Power(Plus(f_,Times(g_DEFAULT,Sinh(Plus(d_DEFAULT,Times(e_DEFAULT,x_))))),CN1)),x_Symbol),
    Condition(Plus(Simp(Star(Times(C2,i),Integrate(Times(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Cosh(Plus(d,Times(e,x))),Power(Plus(f,Times(g,Sinh(Plus(d,Times(e,x))))),CN1)),x)),x),Integrate(Times(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Subtract(h,Times(i,Cosh(Plus(d,Times(e,x))))),Power(Plus(f,Times(g,Sinh(Plus(d,Times(e,x))))),CN1)),x)),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g,h,i),x),EqQ(Plus(Sqr(f),Sqr(g)),C0),EqQ(Subtract(Sqr(h),Sqr(i)),C0),EqQ(Subtract(Times(g,h),Times(f,i)),C0)))),
IIntegrate(6029,Integrate(Times(Power(Plus(Times(Cosh(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),g_DEFAULT),f_),CN1),Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Plus(h_,Times(i_DEFAULT,Sinh(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))))),x_Symbol),
    Condition(Plus(Simp(Star(Times(C2,i),Integrate(Times(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Sinh(Plus(d,Times(e,x))),Power(Plus(f,Times(g,Cosh(Plus(d,Times(e,x))))),CN1)),x)),x),Integrate(Times(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Subtract(h,Times(i,Sinh(Plus(d,Times(e,x))))),Power(Plus(f,Times(g,Cosh(Plus(d,Times(e,x))))),CN1)),x)),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g,h,i),x),EqQ(Subtract(Sqr(f),Sqr(g)),C0),EqQ(Plus(Sqr(h),Sqr(i)),C0),EqQ(Plus(Times(g,h),Times(f,i)),C0)))),
IIntegrate(6030,Integrate(Times(Power(F_,Times(c_DEFAULT,u_)),Power($(G_,v_),n_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(FSymbol,Times(c,ExpandToSum(u,x))),Power(G(ExpandToSum(v,x)),n)),x),And(FreeQ(list(FSymbol,c,n),x),HyperbolicQ(GSymbol),LinearQ(list(u,v),x),Not(LinearMatchQ(list(u,v),x))))),
IIntegrate(6031,Integrate(Times(Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Times(f_DEFAULT,x_),m_DEFAULT),Power(Sinh(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Module(list(Set(u,IntHide(Times(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Power(Sinh(Plus(d,Times(e,x))),n)),x))),Subtract(Simp(Star(Power(Times(f,x),m),u),x),Simp(Star(Times(f,m),Integrate(Times(Power(Times(f,x),Subtract(m,C1)),u),x)),x))),And(FreeQ(List(FSymbol,a,b,c,d,e,f),x),IGtQ(n,C0),GtQ(m,C0)))),
IIntegrate(6032,Integrate(Times(Power(Cosh(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),n_DEFAULT),Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Times(f_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Module(list(Set(u,IntHide(Times(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Power(Cosh(Plus(d,Times(e,x))),n)),x))),Subtract(Simp(Star(Power(Times(f,x),m),u),x),Simp(Star(Times(f,m),Integrate(Times(Power(Times(f,x),Subtract(m,C1)),u),x)),x))),And(FreeQ(List(FSymbol,a,b,c,d,e,f),x),IGtQ(n,C0),GtQ(m,C0)))),
IIntegrate(6033,Integrate(Times(Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Times(f_DEFAULT,x_),m_),Sinh(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Simp(Times(Power(Times(f,x),Plus(m,C1)),Power(Times(f,Plus(m,C1)),CN1),Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Sinh(Plus(d,Times(e,x)))),x),Negate(Simp(Star(Times(e,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Times(f,x),Plus(m,C1)),Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Cosh(Plus(d,Times(e,x)))),x)),x)),Negate(Simp(Star(Times(b,c,Log(FSymbol),Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Times(f,x),Plus(m,C1)),Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Sinh(Plus(d,Times(e,x)))),x)),x))),And(FreeQ(List(FSymbol,a,b,c,d,e,f,m),x),Or(LtQ(m,CN1),SumSimplerQ(m,C1))))),
IIntegrate(6034,Integrate(Times(Cosh(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Times(f_DEFAULT,x_),m_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Times(f,x),Plus(m,C1)),Power(Times(f,Plus(m,C1)),CN1),Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Cosh(Plus(d,Times(e,x)))),x),Negate(Simp(Star(Times(e,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Times(f,x),Plus(m,C1)),Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Sinh(Plus(d,Times(e,x)))),x)),x)),Negate(Simp(Star(Times(b,c,Log(FSymbol),Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Times(f,x),Plus(m,C1)),Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Cosh(Plus(d,Times(e,x)))),x)),x))),And(FreeQ(List(FSymbol,a,b,c,d,e,f,m),x),Or(LtQ(m,CN1),SumSimplerQ(m,C1))))),
IIntegrate(6035,Integrate(Times(Power(Cosh(Plus(f_DEFAULT,Times(g_DEFAULT,x_))),n_DEFAULT),Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Sinh(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),m_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Times(Power(Sinh(Plus(d,Times(e,x))),m),Power(Cosh(Plus(f,Times(g,x))),n)),x),x),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g),x),IGtQ(m,C0),IGtQ(n,C0)))),
IIntegrate(6036,Integrate(Times(Power(Cosh(Plus(f_DEFAULT,Times(g_DEFAULT,x_))),n_DEFAULT),Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(x_,p_DEFAULT),Power(Sinh(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),m_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Times(Power(x,p),Power(FSymbol,Times(c,Plus(a,Times(b,x))))),Times(Power(Sinh(Plus(d,Times(e,x))),m),Power(Cosh(Plus(f,Times(g,x))),n)),x),x),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g),x),IGtQ(m,C0),IGtQ(n,C0),IGtQ(p,C0)))),
IIntegrate(6037,Integrate(Times(Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power($(G_,Plus(d_DEFAULT,Times(e_DEFAULT,x_))),m_DEFAULT),Power($($p("H"),Plus(d_DEFAULT,Times(e_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigToExp(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Times(Power(G(Plus(d,Times(e,x))),m),Power(H(Plus(d,Times(e,x))),n)),x),x),And(FreeQ(List(FSymbol,a,b,c,d,e),x),IGtQ(m,C0),IGtQ(n,C0),HyperbolicQ(GSymbol),HyperbolicQ($s("H"))))),
IIntegrate(6038,Integrate(Times(Power(F_,u_),Power(Sinh(v_),n_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigToExp(Power(FSymbol,u),Power(Sinh(v),n),x),x),And(FreeQ(FSymbol,x),Or(LinearQ(u,x),PolyQ(u,x,C2)),Or(LinearQ(v,x),PolyQ(v,x,C2)),IGtQ(n,C0)))),
IIntegrate(6039,Integrate(Times(Power(Cosh(v_),n_DEFAULT),Power(F_,u_)),x_Symbol),
    Condition(Integrate(ExpandTrigToExp(Power(FSymbol,u),Power(Cosh(v),n),x),x),And(FreeQ(FSymbol,x),Or(LinearQ(u,x),PolyQ(u,x,C2)),Or(LinearQ(v,x),PolyQ(v,x,C2)),IGtQ(n,C0)))),
IIntegrate(6040,Integrate(Times(Power(Cosh(v_),n_DEFAULT),Power(F_,u_),Power(Sinh(v_),m_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigToExp(Power(FSymbol,u),Times(Power(Sinh(v),m),Power(Cosh(v),n)),x),x),And(FreeQ(FSymbol,x),Or(LinearQ(u,x),PolyQ(u,x,C2)),Or(LinearQ(v,x),PolyQ(v,x,C2)),IGtQ(m,C0),IGtQ(n,C0))))
  );
}
