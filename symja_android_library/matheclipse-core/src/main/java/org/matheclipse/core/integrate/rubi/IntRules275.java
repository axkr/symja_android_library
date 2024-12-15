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
class IntRules275 { 
  public static IAST RULES = List( 
IIntegrate(5501,Integrate(Times(Power(Plus(Times(Cosh(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),g_DEFAULT),f_),n_DEFAULT),Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Sinh(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),m_DEFAULT)),x_Symbol),
    Condition(Dist(Power(g,n),Integrate(Times(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Power(Coth(Plus(Times(C1D2,d),Times(C1D2,e,x))),m)),x),x),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g),x),EqQ(Plus(f,g),C0),IntegersQ(m,n),EqQ(Plus(m,n),C0)))),
IIntegrate(5502,Integrate(Times(Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Plus(Times(Cosh(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),i_DEFAULT),h_),Power(Plus(f_,Times(g_DEFAULT,Sinh(Plus(d_DEFAULT,Times(e_DEFAULT,x_))))),CN1)),x_Symbol),
    Condition(Plus(Dist(Times(C2,i),Integrate(Times(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Cosh(Plus(d,Times(e,x))),Power(Plus(f,Times(g,Sinh(Plus(d,Times(e,x))))),CN1)),x),x),Integrate(Times(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Subtract(h,Times(i,Cosh(Plus(d,Times(e,x))))),Power(Plus(f,Times(g,Sinh(Plus(d,Times(e,x))))),CN1)),x)),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g,h,i),x),EqQ(Plus(Sqr(f),Sqr(g)),C0),EqQ(Subtract(Sqr(h),Sqr(i)),C0),EqQ(Subtract(Times(g,h),Times(f,i)),C0)))),
IIntegrate(5503,Integrate(Times(Power(Plus(Times(Cosh(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),g_DEFAULT),f_),CN1),Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Plus(h_,Times(i_DEFAULT,Sinh(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))))),x_Symbol),
    Condition(Plus(Dist(Times(C2,i),Integrate(Times(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Sinh(Plus(d,Times(e,x))),Power(Plus(f,Times(g,Cosh(Plus(d,Times(e,x))))),CN1)),x),x),Integrate(Times(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Subtract(h,Times(i,Sinh(Plus(d,Times(e,x))))),Power(Plus(f,Times(g,Cosh(Plus(d,Times(e,x))))),CN1)),x)),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g,h,i),x),EqQ(Subtract(Sqr(f),Sqr(g)),C0),EqQ(Plus(Sqr(h),Sqr(i)),C0),EqQ(Plus(Times(g,h),Times(f,i)),C0)))),
IIntegrate(5504,Integrate(Times(Power(F_,Times(c_DEFAULT,u_)),Power($(G_,v_),n_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(FSymbol,Times(c,ExpandToSum(u,x))),Power(G(ExpandToSum(v,x)),n)),x),And(FreeQ(list(FSymbol,c,n),x),HyperbolicQ(GSymbol),LinearQ(list(u,v),x),Not(LinearMatchQ(list(u,v),x))))),
IIntegrate(5505,Integrate(Times(Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Times(f_DEFAULT,x_),m_DEFAULT),Power(Sinh(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Module(list(Set(u,IntHide(Times(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Power(Sinh(Plus(d,Times(e,x))),n)),x))),Subtract(Dist(Power(Times(f,x),m),u,x),Dist(Times(f,m),Integrate(Times(Power(Times(f,x),Subtract(m,C1)),u),x),x))),And(FreeQ(List(FSymbol,a,b,c,d,e,f),x),IGtQ(n,C0),GtQ(m,C0)))),
IIntegrate(5506,Integrate(Times(Power(Cosh(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),n_DEFAULT),Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Times(f_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Module(list(Set(u,IntHide(Times(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Power(Cosh(Plus(d,Times(e,x))),n)),x))),Subtract(Dist(Power(Times(f,x),m),u,x),Dist(Times(f,m),Integrate(Times(Power(Times(f,x),Subtract(m,C1)),u),x),x))),And(FreeQ(List(FSymbol,a,b,c,d,e,f),x),IGtQ(n,C0),GtQ(m,C0)))),
IIntegrate(5507,Integrate(Times(Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Times(f_DEFAULT,x_),m_),Sinh(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Simp(Times(Power(Times(f,x),Plus(m,C1)),Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Sinh(Plus(d,Times(e,x))),Power(Times(f,Plus(m,C1)),CN1)),x),Negate(Dist(Times(e,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Times(f,x),Plus(m,C1)),Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Cosh(Plus(d,Times(e,x)))),x),x)),Negate(Dist(Times(b,c,Log(FSymbol),Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Times(f,x),Plus(m,C1)),Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Sinh(Plus(d,Times(e,x)))),x),x))),And(FreeQ(List(FSymbol,a,b,c,d,e,f,m),x),Or(LtQ(m,CN1),SumSimplerQ(m,C1))))),
IIntegrate(5508,Integrate(Times(Cosh(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Times(f_DEFAULT,x_),m_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Times(f,x),Plus(m,C1)),Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Cosh(Plus(d,Times(e,x))),Power(Times(f,Plus(m,C1)),CN1)),x),Negate(Dist(Times(e,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Times(f,x),Plus(m,C1)),Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Sinh(Plus(d,Times(e,x)))),x),x)),Negate(Dist(Times(b,c,Log(FSymbol),Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Times(f,x),Plus(m,C1)),Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Cosh(Plus(d,Times(e,x)))),x),x))),And(FreeQ(List(FSymbol,a,b,c,d,e,f,m),x),Or(LtQ(m,CN1),SumSimplerQ(m,C1))))),
IIntegrate(5509,Integrate(Times(Power(Cosh(Plus(f_DEFAULT,Times(g_DEFAULT,x_))),n_DEFAULT),Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Sinh(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),m_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Times(Power(Sinh(Plus(d,Times(e,x))),m),Power(Cosh(Plus(f,Times(g,x))),n)),x),x),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g),x),IGtQ(m,C0),IGtQ(n,C0)))),
IIntegrate(5510,Integrate(Times(Power(Cosh(Plus(f_DEFAULT,Times(g_DEFAULT,x_))),n_DEFAULT),Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(x_,p_DEFAULT),Power(Sinh(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),m_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Times(Power(x,p),Power(FSymbol,Times(c,Plus(a,Times(b,x))))),Times(Power(Sinh(Plus(d,Times(e,x))),m),Power(Cosh(Plus(f,Times(g,x))),n)),x),x),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g),x),IGtQ(m,C0),IGtQ(n,C0),IGtQ(p,C0)))),
IIntegrate(5511,Integrate(Times(Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power($(G_,Plus(d_DEFAULT,Times(e_DEFAULT,x_))),m_DEFAULT),Power($($p("H"),Plus(d_DEFAULT,Times(e_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigToExp(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Times(Power(G(Plus(d,Times(e,x))),m),Power(H(Plus(d,Times(e,x))),n)),x),x),And(FreeQ(List(FSymbol,a,b,c,d,e),x),IGtQ(m,C0),IGtQ(n,C0),HyperbolicQ(GSymbol),HyperbolicQ($s("H"))))),
IIntegrate(5512,Integrate(Times(Power(F_,u_),Power(Sinh(v_),n_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigToExp(Power(FSymbol,u),Power(Sinh(v),n),x),x),And(FreeQ(FSymbol,x),Or(LinearQ(u,x),PolyQ(u,x,C2)),Or(LinearQ(v,x),PolyQ(v,x,C2)),IGtQ(n,C0)))),
IIntegrate(5513,Integrate(Times(Power(Cosh(v_),n_DEFAULT),Power(F_,u_)),x_Symbol),
    Condition(Integrate(ExpandTrigToExp(Power(FSymbol,u),Power(Cosh(v),n),x),x),And(FreeQ(FSymbol,x),Or(LinearQ(u,x),PolyQ(u,x,C2)),Or(LinearQ(v,x),PolyQ(v,x,C2)),IGtQ(n,C0)))),
IIntegrate(5514,Integrate(Times(Power(Cosh(v_),n_DEFAULT),Power(F_,u_),Power(Sinh(v_),m_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigToExp(Power(FSymbol,u),Times(Power(Sinh(v),m),Power(Cosh(v),n)),x),x),And(FreeQ(FSymbol,x),Or(LinearQ(u,x),PolyQ(u,x,C2)),Or(LinearQ(v,x),PolyQ(v,x,C2)),IGtQ(m,C0),IGtQ(n,C0)))),
IIntegrate(5515,Integrate(Power(Sinh(Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Integrate(Power(Subtract(Times(C1D2,Power(Times(c,Power(x,n)),b)),Power(Times(C2,Power(Times(c,Power(x,n)),b)),CN1)),p),x),And(FreeQ(c,x),RationalQ(b,n,p)))),
IIntegrate(5516,Integrate(Power(Cosh(Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Integrate(Power(Plus(Times(C1D2,Power(Times(c,Power(x,n)),b)),Power(Times(C2,Power(Times(c,Power(x,n)),b)),CN1)),p),x),And(FreeQ(c,x),RationalQ(b,n,p)))),
IIntegrate(5517,Integrate(Sinh(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(x,Sinh(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Subtract(Times(Sqr(b),Sqr(d),Sqr(n)),C1),CN1)),x)),Simp(Times(b,d,n,x,Cosh(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Subtract(Times(Sqr(b),Sqr(d),Sqr(n)),C1),CN1)),x)),And(FreeQ(List(a,b,c,d,n),x),NeQ(Subtract(Times(Sqr(b),Sqr(d),Sqr(n)),C1),C0)))),
IIntegrate(5518,Integrate(Cosh(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(x,Cosh(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Subtract(Times(Sqr(b),Sqr(d),Sqr(n)),C1),CN1)),x)),Simp(Times(b,d,n,x,Sinh(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Subtract(Times(Sqr(b),Sqr(d),Sqr(n)),C1),CN1)),x)),And(FreeQ(List(a,b,c,d,n),x),NeQ(Subtract(Times(Sqr(b),Sqr(d),Sqr(n)),C1),C0)))),
IIntegrate(5519,Integrate(Power(Sinh(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),p_),x_Symbol),
    Condition(Plus(Negate(Simp(Times(x,Power(Sinh(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),p),Power(Subtract(Times(Sqr(b),Sqr(d),Sqr(n),Sqr(p)),C1),CN1)),x)),Negate(Dist(Times(Sqr(b),Sqr(d),Sqr(n),p,Subtract(p,C1),Power(Subtract(Times(Sqr(b),Sqr(d),Sqr(n),Sqr(p)),C1),CN1)),Integrate(Power(Sinh(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Subtract(p,C2)),x),x)),Simp(Times(b,d,n,p,x,Cosh(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Sinh(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Subtract(p,C1)),Power(Subtract(Times(Sqr(b),Sqr(d),Sqr(n),Sqr(p)),C1),CN1)),x)),And(FreeQ(List(a,b,c,d,n),x),IGtQ(p,C1),NeQ(Subtract(Times(Sqr(b),Sqr(d),Sqr(n),Sqr(p)),C1),C0)))),
IIntegrate(5520,Integrate(Power(Cosh(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),p_),x_Symbol),
    Condition(Plus(Negate(Simp(Times(x,Power(Cosh(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),p),Power(Subtract(Times(Sqr(b),Sqr(d),Sqr(n),Sqr(p)),C1),CN1)),x)),Dist(Times(Sqr(b),Sqr(d),Sqr(n),p,Subtract(p,C1),Power(Subtract(Times(Sqr(b),Sqr(d),Sqr(n),Sqr(p)),C1),CN1)),Integrate(Power(Cosh(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Subtract(p,C2)),x),x),Simp(Times(b,d,n,p,x,Power(Cosh(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Subtract(p,C1)),Sinh(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Subtract(Times(Sqr(b),Sqr(d),Sqr(n),Sqr(p)),C1),CN1)),x)),And(FreeQ(List(a,b,c,d,n),x),IGtQ(p,C1),NeQ(Subtract(Times(Sqr(b),Sqr(d),Sqr(n),Sqr(p)),C1),C0))))
  );
}
