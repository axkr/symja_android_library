package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctions.*;
import org.matheclipse.core.interfaces.IAST;

/** 
 * IndefiniteIntegrationRules from the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi -
 * rule-based integrator</a>.
 *  
 */
public class IntRules178 { 
  public static IAST RULES = List( 
IIntegrate(4451,Int(Times(Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Sec(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Simp(Times(Power(C2,n),Exp(Times(CI,n,Plus(d,Times(e,x)))),Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Hypergeometric2F1(n,Subtract(Times(C1D2,n),Times(CI,b,c,Log(FSymbol),Power(Times(C2,e),CN1))),Subtract(Plus(C1,Times(C1D2,n)),Times(CI,b,c,Log(FSymbol),Power(Times(C2,e),CN1))),Negate(Exp(Times(C2,CI,Plus(d,Times(e,x)))))),Power(Plus(Times(CI,e,n),Times(b,c,Log(FSymbol))),CN1)),x),And(FreeQ(List(FSymbol,a,b,c,d,e),x),IntegerQ(n)))),
IIntegrate(4452,Int(Times(Power(Csc(Plus(d_DEFAULT,Times(Pi,k_DEFAULT),Times(e_DEFAULT,x_))),n_DEFAULT),Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),x_Symbol),
    Condition(Simp(Times(Power(Times(CN2,CI),n),Exp(Times(CI,k,n,Pi)),Exp(Times(CI,n,Plus(d,Times(e,x)))),Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Power(Plus(Times(CI,e,n),Times(b,c,Log(FSymbol))),CN1),Hypergeometric2F1(n,Subtract(Times(C1D2,n),Times(CI,b,c,Log(FSymbol),Power(Times(C2,e),CN1))),Subtract(Plus(C1,Times(C1D2,n)),Times(CI,b,c,Log(FSymbol),Power(Times(C2,e),CN1))),Times(Exp(Times(C2,CI,k,Pi)),Exp(Times(C2,CI,Plus(d,Times(e,x))))))),x),And(FreeQ(List(FSymbol,a,b,c,d,e),x),IntegerQ(Times(C4,k)),IntegerQ(n)))),
IIntegrate(4453,Int(Times(Power(Csc(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),n_DEFAULT),Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),x_Symbol),
    Condition(Simp(Times(Power(Times(CN2,CI),n),Exp(Times(CI,n,Plus(d,Times(e,x)))),Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Power(Plus(Times(CI,e,n),Times(b,c,Log(FSymbol))),CN1),Hypergeometric2F1(n,Subtract(Times(C1D2,n),Times(CI,b,c,Log(FSymbol),Power(Times(C2,e),CN1))),Subtract(Plus(C1,Times(C1D2,n)),Times(CI,b,c,Log(FSymbol),Power(Times(C2,e),CN1))),Exp(Times(C2,CI,Plus(d,Times(e,x)))))),x),And(FreeQ(List(FSymbol,a,b,c,d,e),x),IntegerQ(n)))),
IIntegrate(4454,Int(Times(Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Sec(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(Plus(C1,Exp(Times(C2,CI,Plus(d,Times(e,x))))),n),Power(Sec(Plus(d,Times(e,x))),n),Power(Exp(Times(CI,n,Plus(d,Times(e,x)))),CN1)),Int(SimplifyIntegrand(Times(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Exp(Times(CI,n,Plus(d,Times(e,x)))),Power(Power(Plus(C1,Exp(Times(C2,CI,Plus(d,Times(e,x))))),n),CN1)),x),x),x),And(FreeQ(List(FSymbol,a,b,c,d,e),x),Not(IntegerQ(n))))),
IIntegrate(4455,Int(Times(Power(Csc(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),n_DEFAULT),Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),x_Symbol),
    Condition(Dist(Times(Power(Subtract(C1,Exp(Times(CN2,CI,Plus(d,Times(e,x))))),n),Power(Csc(Plus(d,Times(e,x))),n),Power(Exp(Times(CN1,CI,n,Plus(d,Times(e,x)))),CN1)),Int(SimplifyIntegrand(Times(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Power(Times(Exp(Times(CI,n,Plus(d,Times(e,x)))),Power(Subtract(C1,Exp(Times(CN2,CI,Plus(d,Times(e,x))))),n)),CN1)),x),x),x),And(FreeQ(List(FSymbol,a,b,c,d,e),x),Not(IntegerQ(n))))),
IIntegrate(4456,Int(Times(Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Plus(f_,Times(g_DEFAULT,Sin(Plus(d_DEFAULT,Times(e_DEFAULT,x_))))),n_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(C2,n),Power(f,n)),Int(Times(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Power(Cos(Subtract(Plus(Times(C1D2,d),Times(C1D2,e,x)),Times(f,Pi,Power(Times(C4,g),CN1)))),Times(C2,n))),x),x),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g),x),EqQ(Subtract(Sqr(f),Sqr(g)),C0),ILtQ(n,C0)))),
IIntegrate(4457,Int(Times(Power(Plus(Times(Cos(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),g_DEFAULT),f_),n_DEFAULT),Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),x_Symbol),
    Condition(Dist(Times(Power(C2,n),Power(f,n)),Int(Times(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Power(Cos(Plus(Times(C1D2,d),Times(C1D2,e,x))),Times(C2,n))),x),x),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g),x),EqQ(Subtract(f,g),C0),ILtQ(n,C0)))),
IIntegrate(4458,Int(Times(Power(Plus(Times(Cos(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),g_DEFAULT),f_),n_DEFAULT),Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),x_Symbol),
    Condition(Dist(Times(Power(C2,n),Power(f,n)),Int(Times(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Power(Sin(Plus(Times(C1D2,d),Times(C1D2,e,x))),Times(C2,n))),x),x),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g),x),EqQ(Plus(f,g),C0),ILtQ(n,C0)))),
IIntegrate(4459,Int(Times(Power(Cos(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),m_DEFAULT),Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Plus(f_,Times(g_DEFAULT,Sin(Plus(d_DEFAULT,Times(e_DEFAULT,x_))))),n_DEFAULT)),x_Symbol),
    Condition(Dist(Power(g,n),Int(Times(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Power(Tan(Subtract(Subtract(Times(f,Pi,Power(Times(C4,g),CN1)),Times(C1D2,d)),Times(C1D2,e,x))),m)),x),x),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g),x),EqQ(Subtract(Sqr(f),Sqr(g)),C0),IntegersQ(m,n),EqQ(Plus(m,n),C0)))),
IIntegrate(4460,Int(Times(Power(Plus(Times(Cos(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),g_DEFAULT),f_),n_DEFAULT),Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Sin(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),m_DEFAULT)),x_Symbol),
    Condition(Dist(Power(f,n),Int(Times(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Power(Tan(Plus(Times(C1D2,d),Times(C1D2,e,x))),m)),x),x),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g),x),EqQ(Subtract(f,g),C0),IntegersQ(m,n),EqQ(Plus(m,n),C0)))),
IIntegrate(4461,Int(Times(Power(Plus(Times(Cos(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),g_DEFAULT),f_),n_DEFAULT),Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Sin(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),m_DEFAULT)),x_Symbol),
    Condition(Dist(Power(f,n),Int(Times(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Power(Cot(Plus(Times(C1D2,d),Times(C1D2,e,x))),m)),x),x),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g),x),EqQ(Plus(f,g),C0),IntegersQ(m,n),EqQ(Plus(m,n),C0)))),
IIntegrate(4462,Int(Times(Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Plus(Times(Cos(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),i_DEFAULT),h_),Power(Plus(f_,Times(g_DEFAULT,Sin(Plus(d_DEFAULT,Times(e_DEFAULT,x_))))),CN1)),x_Symbol),
    Condition(Plus(Dist(Times(C2,i),Int(Times(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Cos(Plus(d,Times(e,x))),Power(Plus(f,Times(g,Sin(Plus(d,Times(e,x))))),CN1)),x),x),Int(Times(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Subtract(h,Times(i,Cos(Plus(d,Times(e,x))))),Power(Plus(f,Times(g,Sin(Plus(d,Times(e,x))))),CN1)),x)),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g,h,i),x),EqQ(Subtract(Sqr(f),Sqr(g)),C0),EqQ(Subtract(Sqr(h),Sqr(i)),C0),EqQ(Subtract(Times(g,h),Times(f,i)),C0)))),
IIntegrate(4463,Int(Times(Power(Plus(Times(Cos(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),g_DEFAULT),f_),CN1),Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Plus(h_,Times(i_DEFAULT,Sin(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))))),x_Symbol),
    Condition(Plus(Dist(Times(C2,i),Int(Times(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Sin(Plus(d,Times(e,x))),Power(Plus(f,Times(g,Cos(Plus(d,Times(e,x))))),CN1)),x),x),Int(Times(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Subtract(h,Times(i,Sin(Plus(d,Times(e,x))))),Power(Plus(f,Times(g,Cos(Plus(d,Times(e,x))))),CN1)),x)),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g,h,i),x),EqQ(Subtract(Sqr(f),Sqr(g)),C0),EqQ(Subtract(Sqr(h),Sqr(i)),C0),EqQ(Plus(Times(g,h),Times(f,i)),C0)))),
IIntegrate(4464,Int(Times(Power(F_,Times(c_DEFAULT,u_)),Power($(G_,v_),n_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(FSymbol,Times(c,ExpandToSum(u,x))),Power(G(ExpandToSum(v,x)),n)),x),And(FreeQ(List(FSymbol,c,n),x),TrigQ(GSymbol),LinearQ(List(u,v),x),Not(LinearMatchQ(List(u,v),x))))),
IIntegrate(4465,Int(Times(Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Times(f_DEFAULT,x_),m_DEFAULT),Power(Sin(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Module(List(Set(u,IntHide(Times(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Power(Sin(Plus(d,Times(e,x))),n)),x))),Subtract(Dist(Power(Times(f,x),m),u,x),Dist(Times(f,m),Int(Times(Power(Times(f,x),Subtract(m,C1)),u),x),x))),And(FreeQ(List(FSymbol,a,b,c,d,e,f),x),IGtQ(n,C0),GtQ(m,C0)))),
IIntegrate(4466,Int(Times(Power(Cos(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),n_DEFAULT),Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Times(f_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Module(List(Set(u,IntHide(Times(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Power(Cos(Plus(d,Times(e,x))),n)),x))),Subtract(Dist(Power(Times(f,x),m),u,x),Dist(Times(f,m),Int(Times(Power(Times(f,x),Subtract(m,C1)),u),x),x))),And(FreeQ(List(FSymbol,a,b,c,d,e,f),x),IGtQ(n,C0),GtQ(m,C0)))),
IIntegrate(4467,Int(Times(Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Times(f_DEFAULT,x_),m_),Sin(Plus(d_DEFAULT,Times(e_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Simp(Times(Power(Times(f,x),Plus(m,C1)),Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Sin(Plus(d,Times(e,x))),Power(Times(f,Plus(m,C1)),CN1)),x),Negate(Dist(Times(e,Power(Times(f,Plus(m,C1)),CN1)),Int(Times(Power(Times(f,x),Plus(m,C1)),Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Cos(Plus(d,Times(e,x)))),x),x)),Negate(Dist(Times(b,c,Log(FSymbol),Power(Times(f,Plus(m,C1)),CN1)),Int(Times(Power(Times(f,x),Plus(m,C1)),Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Sin(Plus(d,Times(e,x)))),x),x))),And(FreeQ(List(FSymbol,a,b,c,d,e,f,m),x),Or(LtQ(m,CN1),SumSimplerQ(m,C1))))),
IIntegrate(4468,Int(Times(Cos(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Times(f_DEFAULT,x_),m_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Times(f,x),Plus(m,C1)),Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Cos(Plus(d,Times(e,x))),Power(Times(f,Plus(m,C1)),CN1)),x),Dist(Times(e,Power(Times(f,Plus(m,C1)),CN1)),Int(Times(Power(Times(f,x),Plus(m,C1)),Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Sin(Plus(d,Times(e,x)))),x),x),Negate(Dist(Times(b,c,Log(FSymbol),Power(Times(f,Plus(m,C1)),CN1)),Int(Times(Power(Times(f,x),Plus(m,C1)),Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Cos(Plus(d,Times(e,x)))),x),x))),And(FreeQ(List(FSymbol,a,b,c,d,e,f,m),x),Or(LtQ(m,CN1),SumSimplerQ(m,C1))))),
IIntegrate(4469,Int(Times(Power(Cos(Plus(f_DEFAULT,Times(g_DEFAULT,x_))),n_DEFAULT),Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(Sin(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),m_DEFAULT)),x_Symbol),
    Condition(Int(ExpandTrigReduce(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Times(Power(Sin(Plus(d,Times(e,x))),m),Power(Cos(Plus(f,Times(g,x))),n)),x),x),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g),x),IGtQ(m,C0),IGtQ(n,C0)))),
IIntegrate(4470,Int(Times(Power(Cos(Plus(f_DEFAULT,Times(g_DEFAULT,x_))),n_DEFAULT),Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power(x_,p_DEFAULT),Power(Sin(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),m_DEFAULT)),x_Symbol),
    Condition(Int(ExpandTrigReduce(Times(Power(x,p),Power(FSymbol,Times(c,Plus(a,Times(b,x))))),Times(Power(Sin(Plus(d,Times(e,x))),m),Power(Cos(Plus(f,Times(g,x))),n)),x),x),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g),x),IGtQ(m,C0),IGtQ(n,C0),IGtQ(p,C0)))),
IIntegrate(4471,Int(Times(Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Power($(G_,Plus(d_DEFAULT,Times(e_DEFAULT,x_))),m_DEFAULT),Power($($p("H"),Plus(d_DEFAULT,Times(e_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Int(ExpandTrigToExp(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),Times(Power(G(Plus(d,Times(e,x))),m),Power(H(Plus(d,Times(e,x))),n)),x),x),And(FreeQ(List(FSymbol,a,b,c,d,e),x),IGtQ(m,C0),IGtQ(n,C0),TrigQ(GSymbol),TrigQ($s("H"))))),
IIntegrate(4472,Int(Times(Power(F_,u_),Power(Sin(v_),n_DEFAULT)),x_Symbol),
    Condition(Int(ExpandTrigToExp(Power(FSymbol,u),Power(Sin(v),n),x),x),And(FreeQ(FSymbol,x),Or(LinearQ(u,x),PolyQ(u,x,C2)),Or(LinearQ(v,x),PolyQ(v,x,C2)),IGtQ(n,C0)))),
IIntegrate(4473,Int(Times(Power(Cos(v_),n_DEFAULT),Power(F_,u_)),x_Symbol),
    Condition(Int(ExpandTrigToExp(Power(FSymbol,u),Power(Cos(v),n),x),x),And(FreeQ(FSymbol,x),Or(LinearQ(u,x),PolyQ(u,x,C2)),Or(LinearQ(v,x),PolyQ(v,x,C2)),IGtQ(n,C0)))),
IIntegrate(4474,Int(Times(Power(Cos(v_),n_DEFAULT),Power(F_,u_),Power(Sin(v_),m_DEFAULT)),x_Symbol),
    Condition(Int(ExpandTrigToExp(Power(FSymbol,u),Times(Power(Sin(v),m),Power(Cos(v),n)),x),x),And(FreeQ(FSymbol,x),Or(LinearQ(u,x),PolyQ(u,x,C2)),Or(LinearQ(v,x),PolyQ(v,x,C2)),IGtQ(m,C0),IGtQ(n,C0)))),
IIntegrate(4475,Int(Sin(Times(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),d_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(x,Sin(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Plus(Times(Sqr(b),Sqr(d),Sqr(n)),C1),CN1)),x),Simp(Times(b,d,n,x,Cos(Times(d,Plus(a,Times(b,Log(Times(c,Power(x,n))))))),Power(Plus(Times(Sqr(b),Sqr(d),Sqr(n)),C1),CN1)),x)),And(FreeQ(List(a,b,c,d,n),x),NeQ(Plus(Times(Sqr(b),Sqr(d),Sqr(n)),C1),C0))))
  );
}
