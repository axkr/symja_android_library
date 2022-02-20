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
class IntRules223 { 
  public static IAST RULES = List( 
IIntegrate(4461,Integrate(Times(Power(Plus(Times(Cos(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),g_DEFAULT),f_),n_DEFAULT),Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Sin(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),m_DEFAULT)),x_Symbol),
    Condition(Dist(Power(f,n),Integrate(Times(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Power(Cot(Plus(Times(C1D2,d),Times(C1D2,e,x))),m)),x),x),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g),x),EqQ(Plus(f,g),C0),IntegersQ(m,n),EqQ(Plus(m,n),C0)))),
IIntegrate(4462,Integrate(Times(Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Plus(Times(Cos(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),i_DEFAULT),h_),Power(Plus(f_,Times(g_DEFAULT,Sin(Plus(d_DEFAULT,Times(e_DEFAULT,x_))))),CN1)),x_Symbol),
    Condition(Plus(Dist(Times(C2,i),Integrate(Times(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Cos(Plus(d,Times(e,x))),Power(Plus(f,Times(g,Sin(Plus(d,Times(e,x))))),CN1)),x),x),Integrate(Times(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Subtract(h,Times(i,Cos(Plus(d,Times(e,x))))),Power(Plus(f,Times(g,Sin(Plus(d,Times(e,x))))),CN1)),x)),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g,h,i),x),EqQ(Subtract(Sqr(f),Sqr(g)),C0),EqQ(Subtract(Sqr(h),Sqr(i)),C0),EqQ(Subtract(Times(g,h),Times(f,i)),C0)))),
IIntegrate(4463,Integrate(Times(Power(Plus(Times(Cos(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),g_DEFAULT),f_),CN1),Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Plus(h_,Times(i_DEFAULT,Sin(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))))),x_Symbol),
    Condition(Plus(Dist(Times(C2,i),Integrate(Times(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Sin(Plus(d,Times(e,x))),Power(Plus(f,Times(g,Cos(Plus(d,Times(e,x))))),CN1)),x),x),Integrate(Times(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Subtract(h,Times(i,Sin(Plus(d,Times(e,x))))),Power(Plus(f,Times(g,Cos(Plus(d,Times(e,x))))),CN1)),x)),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g,h,i),x),EqQ(Subtract(Sqr(f),Sqr(g)),C0),EqQ(Subtract(Sqr(h),Sqr(i)),C0),EqQ(Plus(Times(g,h),Times(f,i)),C0)))),
IIntegrate(4464,Integrate(Times(Power(F_,Times(c_DEFAULT,u_)),Power($(G_,v_),n_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(FSymbol,Times(c,ExpandToSum(u,x))),Power(G(ExpandToSum(v,x)),n)),x),And(FreeQ(list(FSymbol,c,n),x),TrigQ(GSymbol),LinearQ(list(u,v),x),Not(LinearMatchQ(list(u,v),x))))),
IIntegrate(4465,Integrate(Times(Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Times(f_DEFAULT,x_),m_DEFAULT),Power(Sin(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Module(list(Set(u,IntHide(Times(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Power(Sin(Plus(d,Times(e,x))),n)),x))),Subtract(Dist(Power(Times(f,x),m),u,x),Dist(Times(f,m),Integrate(Times(Power(Times(f,x),Subtract(m,C1)),u),x),x))),And(FreeQ(List(FSymbol,a,b,c,d,e,f),x),IGtQ(n,C0),GtQ(m,C0)))),
IIntegrate(4466,Integrate(Times(Power(Cos(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),n_DEFAULT),Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Times(f_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Module(list(Set(u,IntHide(Times(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Power(Cos(Plus(d,Times(e,x))),n)),x))),Subtract(Dist(Power(Times(f,x),m),u,x),Dist(Times(f,m),Integrate(Times(Power(Times(f,x),Subtract(m,C1)),u),x),x))),And(FreeQ(List(FSymbol,a,b,c,d,e,f),x),IGtQ(n,C0),GtQ(m,C0)))),
IIntegrate(4467,Integrate(Times(Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Times(f_DEFAULT,x_),m_),Sin(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Simp(Times(Power(Times(f,x),Plus(m,C1)),Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Sin(Plus(d,Times(e,x))),Power(Times(f,Plus(m,C1)),CN1)),x),Negate(Dist(Times(e,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Times(f,x),Plus(m,C1)),Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Cos(Plus(d,Times(e,x)))),x),x)),Negate(Dist(Times(b,c,Log(FSymbol),Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Times(f,x),Plus(m,C1)),Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Sin(Plus(d,Times(e,x)))),x),x))),And(FreeQ(List(FSymbol,a,b,c,d,e,f,m),x),Or(LtQ(m,CN1),SumSimplerQ(m,C1))))),
IIntegrate(4468,Integrate(Times(Cos(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Times(f_DEFAULT,x_),m_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Times(f,x),Plus(m,C1)),Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Cos(Plus(d,Times(e,x))),Power(Times(f,Plus(m,C1)),CN1)),x),Dist(Times(e,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Times(f,x),Plus(m,C1)),Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Sin(Plus(d,Times(e,x)))),x),x),Negate(Dist(Times(b,c,Log(FSymbol),Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Times(f,x),Plus(m,C1)),Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Cos(Plus(d,Times(e,x)))),x),x))),And(FreeQ(List(FSymbol,a,b,c,d,e,f,m),x),Or(LtQ(m,CN1),SumSimplerQ(m,C1))))),
IIntegrate(4469,Integrate(Times(Power(Cos(Plus(f_DEFAULT,Times(g_DEFAULT,x_))),n_DEFAULT),Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Sin(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),m_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Times(Power(Sin(Plus(d,Times(e,x))),m),Power(Cos(Plus(f,Times(g,x))),n)),x),x),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g),x),IGtQ(m,C0),IGtQ(n,C0)))),
IIntegrate(4470,Integrate(Times(Power(Cos(Plus(f_DEFAULT,Times(g_DEFAULT,x_))),n_DEFAULT),Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(x_,p_DEFAULT),Power(Sin(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),m_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigReduce(Times(Power(x,p),Power(FSymbol,Times(c,Plus(a,Times(b,x))))),Times(Power(Sin(Plus(d,Times(e,x))),m),Power(Cos(Plus(f,Times(g,x))),n)),x),x),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g),x),IGtQ(m,C0),IGtQ(n,C0),IGtQ(p,C0)))),
IIntegrate(4471,Integrate(Times(Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power($(G_,Plus(d_DEFAULT,Times(e_DEFAULT,x_))),m_DEFAULT),Power($($p("H"),Plus(d_DEFAULT,Times(e_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigToExp(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Times(Power(G(Plus(d,Times(e,x))),m),Power(H(Plus(d,Times(e,x))),n)),x),x),And(FreeQ(List(FSymbol,a,b,c,d,e),x),IGtQ(m,C0),IGtQ(n,C0),TrigQ(GSymbol),TrigQ($s("H"))))),
IIntegrate(4472,Integrate(Times(Power(F_,u_),Power(Sin(v_),n_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigToExp(Power(FSymbol,u),Power(Sin(v),n),x),x),And(FreeQ(FSymbol,x),Or(LinearQ(u,x),PolyQ(u,x,C2)),Or(LinearQ(v,x),PolyQ(v,x,C2)),IGtQ(n,C0)))),
IIntegrate(4473,Integrate(Times(Power(Cos(v_),n_DEFAULT),Power(F_,u_)),x_Symbol),
    Condition(Integrate(ExpandTrigToExp(Power(FSymbol,u),Power(Cos(v),n),x),x),And(FreeQ(FSymbol,x),Or(LinearQ(u,x),PolyQ(u,x,C2)),Or(LinearQ(v,x),PolyQ(v,x,C2)),IGtQ(n,C0)))),
IIntegrate(4474,Integrate(Times(Power(Cos(v_),n_DEFAULT),Power(F_,u_),Power(Sin(v_),m_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandTrigToExp(Power(FSymbol,u),Times(Power(Sin(v),m),Power(Cos(v),n)),x),x),And(FreeQ(FSymbol,x),Or(LinearQ(u,x),PolyQ(u,x,C2)),Or(LinearQ(v,x),PolyQ(v,x,C2)),IGtQ(m,C0),IGtQ(n,C0)))),
IIntegrate(4475,Integrate(Sin(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(x,Sin(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Plus(Times(Sqr(b),Sqr(d),Sqr(n)),C1),CN1)),x),Simp(Times(b,d,n,x,Cos(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Plus(Times(Sqr(b),Sqr(d),Sqr(n)),C1),CN1)),x)),And(FreeQ(List(a,b,c,d,n),x),NeQ(Plus(Times(Sqr(b),Sqr(d),Sqr(n)),C1),C0)))),
IIntegrate(4476,Integrate(Cos(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(x,Cos(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Plus(Times(Sqr(b),Sqr(d),Sqr(n)),C1),CN1)),x),Simp(Times(b,d,n,x,Sin(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Plus(Times(Sqr(b),Sqr(d),Sqr(n)),C1),CN1)),x)),And(FreeQ(List(a,b,c,d,n),x),NeQ(Plus(Times(Sqr(b),Sqr(d),Sqr(n)),C1),C0)))),
IIntegrate(4477,Integrate(Power(Sin(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),p_),x_Symbol),
    Condition(Plus(Simp(Times(x,Power(Sin(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),p),Power(Plus(Times(Sqr(b),Sqr(d),Sqr(n),Sqr(p)),C1),CN1)),x),Dist(Times(Sqr(b),Sqr(d),Sqr(n),p,Subtract(p,C1),Power(Plus(Times(Sqr(b),Sqr(d),Sqr(n),Sqr(p)),C1),CN1)),Integrate(Power(Sin(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Subtract(p,C2)),x),x),Negate(Simp(Times(b,d,n,p,x,Cos(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Sin(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Subtract(p,C1)),Power(Plus(Times(Sqr(b),Sqr(d),Sqr(n),Sqr(p)),C1),CN1)),x))),And(FreeQ(List(a,b,c,d,n),x),IGtQ(p,C1),NeQ(Plus(Times(Sqr(b),Sqr(d),Sqr(n),Sqr(p)),C1),C0)))),
IIntegrate(4478,Integrate(Power(Cos(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),p_),x_Symbol),
    Condition(Plus(Simp(Times(x,Power(Cos(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),p),Power(Plus(Times(Sqr(b),Sqr(d),Sqr(n),Sqr(p)),C1),CN1)),x),Dist(Times(Sqr(b),Sqr(d),Sqr(n),p,Subtract(p,C1),Power(Plus(Times(Sqr(b),Sqr(d),Sqr(n),Sqr(p)),C1),CN1)),Integrate(Power(Cos(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Subtract(p,C2)),x),x),Simp(Times(b,d,n,p,x,Power(Cos(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Subtract(p,C1)),Sin(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Plus(Times(Sqr(b),Sqr(d),Sqr(n),Sqr(p)),C1),CN1)),x)),And(FreeQ(List(a,b,c,d,n),x),IGtQ(p,C1),NeQ(Plus(Times(Sqr(b),Sqr(d),Sqr(n),Sqr(p)),C1),C0)))),
IIntegrate(4479,Integrate(Power(Sin(Times(Plus(a_DEFAULT,Times(Log(x_),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Dist(Power(Times(Power(C2,p),Power(b,p),Power(d,p),Power(p,p)),CN1),Integrate(ExpandIntegrand(Power(Subtract(Times(Exp(Times(a,b,Sqr(d),p)),Power(Power(x,Power(p,CN1)),CN1)),Times(Power(x,Power(p,CN1)),Power(Exp(Times(a,b,Sqr(d),p)),CN1))),p),x),x),x),And(FreeQ(list(a,b,d),x),IGtQ(p,C0),EqQ(Plus(Times(Sqr(b),Sqr(d),Sqr(p)),C1),C0)))),
IIntegrate(4480,Integrate(Power(Cos(Times(Plus(a_DEFAULT,Times(Log(x_),b_DEFAULT)),d_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Dist(Power(Power(C2,p),CN1),Integrate(ExpandIntegrand(Power(Plus(Times(Exp(Times(a,b,Sqr(d),p)),Power(Power(x,Power(p,CN1)),CN1)),Times(Power(x,Power(p,CN1)),Power(Exp(Times(a,b,Sqr(d),p)),CN1))),p),x),x),x),And(FreeQ(list(a,b,d),x),IGtQ(p,C0),EqQ(Plus(Times(Sqr(b),Sqr(d),Sqr(p)),C1),C0))))
  );
}
