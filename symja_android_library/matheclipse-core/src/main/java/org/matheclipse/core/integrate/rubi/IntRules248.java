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
class IntRules248 { 
  public static IAST RULES = List( 
IIntegrate(4961,Integrate(Times(Power(Plus(Times(Cos(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),g_DEFAULT),f_),n_DEFAULT),Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),x_Symbol),
    Condition(Simp(Star(Times(Power(Plus(f,Times(g,Cos(Plus(d,Times(e,x))))),n),Power(Power(Sin(Plus(Times(C1D2,d),Times(e,C1D2,x))),Times(C2,n)),CN1)),Integrate(Times(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Power(Sin(Plus(Times(C1D2,d),Times(e,C1D2,x))),Times(C2,n))),x)),x),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g,n),x),EqQ(Plus(f,g),C0),Not(IntegerQ(n))))),
IIntegrate(4962,Integrate(Times(Power(Cos(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),m_DEFAULT),Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Plus(f_,Times(g_DEFAULT,Sin(Plus(d_DEFAULT,Times(e_DEFAULT,x_))))),n_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(g,n),Integrate(Times(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Power(Tan(Subtract(Subtract(Times(f,Pi,Power(Times(C4,g),CN1)),Times(C1D2,d)),Times(e,C1D2,x))),m)),x)),x),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g),x),EqQ(Subtract(Sqr(f),Sqr(g)),C0),IntegersQ(m,n),EqQ(Plus(m,n),C0)))),
IIntegrate(4963,Integrate(Times(Power(Plus(Times(Cos(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),g_DEFAULT),f_),n_DEFAULT),Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Sin(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(f,n),Integrate(Times(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Power(Tan(Plus(Times(C1D2,d),Times(e,C1D2,x))),m)),x)),x),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g),x),EqQ(Subtract(f,g),C0),IntegersQ(m,n),EqQ(Plus(m,n),C0)))),
IIntegrate(4964,Integrate(Times(Power(Plus(Times(Cos(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),g_DEFAULT),f_),n_DEFAULT),Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Sin(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(f,n),Integrate(Times(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Power(Cot(Plus(Times(C1D2,d),Times(e,C1D2,x))),m)),x)),x),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g),x),EqQ(Plus(f,g),C0),IntegersQ(m,n),EqQ(Plus(m,n),C0)))),
IIntegrate(4965,Integrate(Times(Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Plus(Times(Cos(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),i_DEFAULT),h_),Power(Plus(f_,Times(g_DEFAULT,Sin(Plus(d_DEFAULT,Times(e_DEFAULT,x_))))),CN1)),x_Symbol),
    Condition(Plus(Simp(Star(Times(C2,i),Integrate(Times(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Cos(Plus(d,Times(e,x))),Power(Plus(f,Times(g,Sin(Plus(d,Times(e,x))))),CN1)),x)),x),Integrate(Times(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Subtract(h,Times(i,Cos(Plus(d,Times(e,x))))),Power(Plus(f,Times(g,Sin(Plus(d,Times(e,x))))),CN1)),x)),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g,h,i),x),EqQ(Subtract(Sqr(f),Sqr(g)),C0),EqQ(Subtract(Sqr(h),Sqr(i)),C0),EqQ(Subtract(Times(g,h),Times(f,i)),C0)))),
IIntegrate(4966,Integrate(Times(Power(Plus(Times(Cos(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),g_DEFAULT),f_),CN1),Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Plus(h_,Times(i_DEFAULT,Sin(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))))),x_Symbol),
    Condition(Plus(Simp(Star(Times(C2,i),Integrate(Times(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Sin(Plus(d,Times(e,x))),Power(Plus(f,Times(g,Cos(Plus(d,Times(e,x))))),CN1)),x)),x),Integrate(Times(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Subtract(h,Times(i,Sin(Plus(d,Times(e,x))))),Power(Plus(f,Times(g,Cos(Plus(d,Times(e,x))))),CN1)),x)),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g,h,i),x),EqQ(Subtract(Sqr(f),Sqr(g)),C0),EqQ(Subtract(Sqr(h),Sqr(i)),C0),EqQ(Plus(Times(g,h),Times(f,i)),C0)))),
IIntegrate(4967,Integrate(Times(Power(F_,Times(c_DEFAULT,u_)),Power($(G_,v_),n_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(FSymbol,Times(c,ExpandToSum(u,x))),Power(G(ExpandToSum(v,x)),n)),x),And(FreeQ(list(FSymbol,c,n),x),TrigQ(GSymbol),LinearQ(list(u,v),x),Not(LinearMatchQ(list(u,v),x))))),
IIntegrate(4968,Integrate(Times(Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Times(f_DEFAULT,x_),m_DEFAULT),Power(Sin(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Module(list(Set(u,IntHide(Times(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Power(Sin(Plus(d,Times(e,x))),n)),x))),Subtract(Simp(Star(Power(Times(f,x),m),u),x),Simp(Star(Times(f,m),Integrate(Times(Power(Times(f,x),Subtract(m,C1)),u),x)),x))),And(FreeQ(List(FSymbol,a,b,c,d,e,f),x),IGtQ(n,C0),GtQ(m,C0)))),
IIntegrate(4969,Integrate(Times(Power(Cos(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),n_DEFAULT),Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Times(f_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Module(list(Set(u,IntHide(Times(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Power(Cos(Plus(d,Times(e,x))),n)),x))),Subtract(Simp(Star(Power(Times(f,x),m),u),x),Simp(Star(Times(f,m),Integrate(Times(Power(Times(f,x),Subtract(m,C1)),u),x)),x))),And(FreeQ(List(FSymbol,a,b,c,d,e,f),x),IGtQ(n,C0),GtQ(m,C0)))),
IIntegrate(4970,Integrate(Times(Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Times(f_DEFAULT,x_),m_),Sin(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Simp(Times(Power(Times(f,x),Plus(m,C1)),Power(Times(f,Plus(m,C1)),CN1),Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Sin(Plus(d,Times(e,x)))),x),Negate(Simp(Star(Times(e,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Times(f,x),Plus(m,C1)),Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Cos(Plus(d,Times(e,x)))),x)),x)),Negate(Simp(Star(Times(b,c,Log(FSymbol),Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Times(f,x),Plus(m,C1)),Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Sin(Plus(d,Times(e,x)))),x)),x))),And(FreeQ(List(FSymbol,a,b,c,d,e,f,m),x),Or(LtQ(m,CN1),SumSimplerQ(m,C1))))),
IIntegrate(4971,Integrate(Times(Cos(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Times(f_DEFAULT,x_),m_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Times(f,x),Plus(m,C1)),Power(Times(f,Plus(m,C1)),CN1),Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Cos(Plus(d,Times(e,x)))),x),Simp(Star(Times(e,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Times(f,x),Plus(m,C1)),Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Sin(Plus(d,Times(e,x)))),x)),x),Negate(Simp(Star(Times(b,c,Log(FSymbol),Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Times(f,x),Plus(m,C1)),Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Cos(Plus(d,Times(e,x)))),x)),x))),And(FreeQ(List(FSymbol,a,b,c,d,e,f,m),x),Or(LtQ(m,CN1),SumSimplerQ(m,C1))))),
IIntegrate(4972,Integrate(Times(Power(Cos(Plus(f_DEFAULT,Times(g_DEFAULT,x_))),n_DEFAULT),Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Sin(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),m_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Times(Power(Sin(Plus(d,Times(e,x))),m),Power(Cos(Plus(f,Times(g,x))),n)),x),x),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g),x),IGtQ(m,C0),IGtQ(n,C0)))),
IIntegrate(4973,Integrate(Times(Power(Cos(Plus(f_DEFAULT,Times(g_DEFAULT,x_))),n_DEFAULT),Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(x_,p_DEFAULT),Power(Sin(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),m_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Times(Power(x,p),Power(FSymbol,Times(c,Plus(a,Times(b,x))))),Times(Power(Sin(Plus(d,Times(e,x))),m),Power(Cos(Plus(f,Times(g,x))),n)),x),x),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g),x),IGtQ(m,C0),IGtQ(n,C0),IGtQ(p,C0)))),
IIntegrate(4974,Integrate(Times(Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power($(G_,Plus(d_DEFAULT,Times(e_DEFAULT,x_))),m_DEFAULT),Power($($p("H"),Plus(d_DEFAULT,Times(e_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigToExp(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Times(Power(G(Plus(d,Times(e,x))),m),Power(H(Plus(d,Times(e,x))),n)),x),x),And(FreeQ(List(FSymbol,a,b,c,d,e),x),IGtQ(m,C0),IGtQ(n,C0),TrigQ(GSymbol),TrigQ($s("H"))))),
IIntegrate(4975,Integrate(Times(Power(F_,u_),Power(Sin(v_),n_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigToExp(Power(FSymbol,u),Power(Sin(v),n),x),x),And(FreeQ(FSymbol,x),Or(LinearQ(u,x),PolyQ(u,x,C2)),Or(LinearQ(v,x),PolyQ(v,x,C2)),IGtQ(n,C0)))),
IIntegrate(4976,Integrate(Times(Power(Cos(v_),n_DEFAULT),Power(F_,u_)),x_Symbol),
    Condition(Integrate(ExpandTrigToExp(Power(FSymbol,u),Power(Cos(v),n),x),x),And(FreeQ(FSymbol,x),Or(LinearQ(u,x),PolyQ(u,x,C2)),Or(LinearQ(v,x),PolyQ(v,x,C2)),IGtQ(n,C0)))),
IIntegrate(4977,Integrate(Times(Power(Cos(v_),n_DEFAULT),Power(F_,u_),Power(Sin(v_),m_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigToExp(Power(FSymbol,u),Times(Power(Sin(v),m),Power(Cos(v),n)),x),x),And(FreeQ(FSymbol,x),Or(LinearQ(u,x),PolyQ(u,x,C2)),Or(LinearQ(v,x),PolyQ(v,x,C2)),IGtQ(m,C0),IGtQ(n,C0)))),
IIntegrate(4978,Integrate(Sin(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(x,Sin(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Plus(Times(Sqr(b),Sqr(d),Sqr(n)),C1),CN1)),x),Simp(Times(b,d,n,x,Cos(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Plus(Times(Sqr(b),Sqr(d),Sqr(n)),C1),CN1)),x)),And(FreeQ(List(a,b,c,d,n),x),NeQ(Plus(Times(Sqr(b),Sqr(d),Sqr(n)),C1),C0)))),
IIntegrate(4979,Integrate(Cos(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(x,Cos(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Plus(Times(Sqr(b),Sqr(d),Sqr(n)),C1),CN1)),x),Simp(Times(b,d,n,x,Sin(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Plus(Times(Sqr(b),Sqr(d),Sqr(n)),C1),CN1)),x)),And(FreeQ(List(a,b,c,d,n),x),NeQ(Plus(Times(Sqr(b),Sqr(d),Sqr(n)),C1),C0)))),
IIntegrate(4980,Integrate(Power(Sin(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),p_),x_Symbol),
    Condition(Plus(Simp(Times(x,Power(Sin(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),p),Power(Plus(Times(Sqr(b),Sqr(d),Sqr(n),Sqr(p)),C1),CN1)),x),Negate(Simp(Times(b,d,n,p,x,Cos(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Sin(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Subtract(p,C1)),Power(Plus(Times(Sqr(b),Sqr(d),Sqr(n),Sqr(p)),C1),CN1)),x)),Simp(Star(Times(Sqr(b),Sqr(d),Sqr(n),p,Subtract(p,C1),Power(Plus(Times(Sqr(b),Sqr(d),Sqr(n),Sqr(p)),C1),CN1)),Integrate(Power(Sin(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Subtract(p,C2)),x)),x)),And(FreeQ(List(a,b,c,d,n),x),IGtQ(p,C1),NeQ(Plus(Times(Sqr(b),Sqr(d),Sqr(n),Sqr(p)),C1),C0))))
  );
}
