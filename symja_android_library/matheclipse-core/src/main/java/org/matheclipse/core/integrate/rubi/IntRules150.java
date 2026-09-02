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
class IntRules150 { 
  public static IAST RULES = List( 
IIntegrate(3001,Integrate(Times(Power(Log(Times(e_DEFAULT,Power(Times(f_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),p_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),q_DEFAULT)),r_DEFAULT))),s_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),m_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),n_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(a,Times(b,x)),Plus(m,C1)),Power(Plus(c,Times(d,x)),Plus(n,C1)),Power(Log(Times(e,Power(Times(f,Power(Plus(a,Times(b,x)),p),Power(Plus(c,Times(d,x)),q)),r))),s),Power(Times(Plus(m,C1),Subtract(Times(b,c),Times(a,d))),CN1)),x),Dist(Times(p,r,s,Subtract(Times(b,c),Times(a,d)),Power(Times(Plus(m,C1),Subtract(Times(b,c),Times(a,d))),CN1)),Integrate(Times(Power(Plus(a,Times(b,x)),m),Power(Plus(c,Times(d,x)),n),Power(Log(Times(e,Power(Times(f,Power(Plus(a,Times(b,x)),p),Power(Plus(c,Times(d,x)),q)),r))),Plus(s,CN1))),x),x)),And(FreeQ(List(a,b,c,d,e,f,m,n,p,q,r,s),x),NeQ(Subtract(Times(b,c),Times(a,d)),C0),EqQ(Plus(p,q),C0),EqQ(Plus(m,n,C2),C0),NeQ(m,CN1),IGtQ(s,C0)))),
IIntegrate(3002,Integrate(Times(Power(Log(Times(e_DEFAULT,Power(Times(f_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),p_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),q_DEFAULT)),r_DEFAULT))),CN1),Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),m_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),n_DEFAULT)),x_Symbol),
    Condition(Simp(Times(Power(Plus(a,Times(b,x)),Plus(m,C1)),Power(Plus(c,Times(d,x)),Plus(n,C1)),ExpIntegralEi(Times(Plus(m,C1),Log(Times(e,Power(Times(f,Power(Plus(a,Times(b,x)),p),Power(Plus(c,Times(d,x)),q)),r))),Power(Times(p,r),CN1))),Power(Times(p,r,Subtract(Times(b,c),Times(a,d)),Power(Times(e,Power(Times(f,Power(Plus(a,Times(b,x)),p),Power(Plus(c,Times(d,x)),q)),r)),Times(Plus(m,C1),Power(Times(p,r),CN1)))),CN1)),x),And(FreeQ(List(a,b,c,d,e,f,m,n,p,q,r),x),NeQ(Subtract(Times(b,c),Times(a,d)),C0),EqQ(Plus(p,q),C0),EqQ(Plus(m,n,C2),C0),NeQ(m,CN1)))),
IIntegrate(3003,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Sqrt(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),CN1D2))),b_DEFAULT)),n_DEFAULT),Power(Plus(A_DEFAULT,Times(B_DEFAULT,x_),Times(C_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Simp(Dist(Times(C2,e,g,Power(Times(CSymbol,Subtract(Times(e,f),Times(d,g))),CN1)),Subst(Integrate(Times(Power(Plus(a,Times(b,Log(Times(c,x)))),n),Power(x,CN1)),x),x,Times(Sqrt(Plus(d,Times(e,x))),Power(Plus(f,Times(g,x)),CN1D2))),x),x),And(FreeQ(List(a,b,c,d,e,f,g,ASymbol,BSymbol,CSymbol,n),x),EqQ(Subtract(Times(CSymbol,d,f),Times(ASymbol,e,g)),C0),EqQ(Subtract(Times(BSymbol,e,g),Times(CSymbol,Plus(Times(e,f),Times(d,g)))),C0)))),
IIntegrate(3004,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Sqrt(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),CN1D2))),b_DEFAULT)),n_DEFAULT),Power(Plus(A_DEFAULT,Times(C_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Simp(Dist(Times(g,Power(Times(CSymbol,f),CN1)),Subst(Integrate(Times(Power(Plus(a,Times(b,Log(Times(c,x)))),n),Power(x,CN1)),x),x,Times(Sqrt(Plus(d,Times(e,x))),Power(Plus(f,Times(g,x)),CN1D2))),x),x),And(FreeQ(List(a,b,c,d,e,f,g,ASymbol,CSymbol,n),x),EqQ(Subtract(Times(CSymbol,d,f),Times(ASymbol,e,g)),C0),EqQ(Plus(Times(e,f),Times(d,g)),C0)))),
IIntegrate(3005,Integrate(Times(Log(Times(e_DEFAULT,Power(Times(f_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),p_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),q_DEFAULT)),r_DEFAULT))),$p("§rfx",true)),x_Symbol),
    Condition(Plus(Simp(Dist(Times(p,r),Integrate(Times($s("§rfx"),Log(Plus(a,Times(b,x)))),x),x),x),Simp(Dist(Times(q,r),Integrate(Times($s("§rfx"),Log(Plus(c,Times(d,x)))),x),x),x),Negate(Simp(Dist(Subtract(Plus(Times(p,r,Log(Plus(a,Times(b,x)))),Times(q,r,Log(Plus(c,Times(d,x))))),Log(Times(e,Power(Times(f,Power(Plus(a,Times(b,x)),p),Power(Plus(c,Times(d,x)),q)),r)))),Integrate($s("§rfx"),x),x),x))),And(FreeQ(List(a,b,c,d,e,f,p,q,r),x),RationalFunctionQ($s("§rfx"),x),NeQ(Subtract(Times(b,c),Times(a,d)),C0),Not(MatchQ($s("§rfx"),Condition(Times(u_DEFAULT,Power(Plus(a,Times(b,x)),m_DEFAULT),Power(Plus(c,Times(d,x)),n_DEFAULT)),IntegersQ(m,n))))))),
IIntegrate(3006,Integrate(Times(Power(Log(Times(e_DEFAULT,Power(Times(f_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),p_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),q_DEFAULT)),r_DEFAULT))),s_DEFAULT),$p("§rfx")),x_Symbol),
    Condition(With(list(Set(u,ExpandIntegrand(Power(Log(Times(e,Power(Times(f,Power(Plus(a,Times(b,x)),p),Power(Plus(c,Times(d,x)),q)),r))),s),$s("§rfx"),x))),Condition(Integrate(u,x),SumQ(u))),And(FreeQ(List(a,b,c,d,e,f,p,q,r,s),x),RationalFunctionQ($s("§rfx"),x),IGtQ(s,C0)))),
IIntegrate(3007,Integrate(Times(Power(Log(Times(e_DEFAULT,Power(Times(f_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),p_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),q_DEFAULT)),r_DEFAULT))),s_DEFAULT),$p("§rfx")),x_Symbol),
    Condition(Unintegrable(Times($s("§rfx"),Power(Log(Times(e,Power(Times(f,Power(Plus(a,Times(b,x)),p),Power(Plus(c,Times(d,x)),q)),r))),s)),x),And(FreeQ(List(a,b,c,d,e,f,p,q,r,s),x),RationalFunctionQ($s("§rfx"),x)))),
IIntegrate(3008,Integrate(Times(Power(Log(Times(e_DEFAULT,Power(Times(f_DEFAULT,Power(v_,p_DEFAULT),Power(w_,q_DEFAULT)),r_DEFAULT))),s_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Integrate(Times(u,Power(Log(Times(e,Power(Times(f,Power(ExpandToSum(v,x),p),Power(ExpandToSum(w,x),q)),r))),s)),x),And(FreeQ(List(e,f,p,q,r,s),x),LinearQ(list(v,w),x),Not(LinearMatchQ(list(v,w),x)),AlgebraicFunctionQ(u,x)))),
IIntegrate(3009,Integrate(Times(Power(Log(Times(e_DEFAULT,Power(Times(f_DEFAULT,Plus(g_,Times(v_DEFAULT,Power(w_,CN1)))),r_DEFAULT))),s_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Integrate(Times(u,Power(Log(Times(e,Power(Times(f,ExpandToSum(Plus(v,Times(g,w)),x),Power(ExpandToSum(w,x),CN1)),r))),s)),x),And(FreeQ(List(e,f,g,r,s),x),LinearQ(w,x),Or(FreeQ(v,x),LinearQ(v,x)),AlgebraicFunctionQ(u,x)))),
IIntegrate(3010,Integrate(Times(Log(v_),u_),x_Symbol),
    With(list(Set(w,DerivativeDivides(v,Times(u,Subtract(C1,v)),x))),Condition(Simp(Times(w,PolyLog(C2,Subtract(C1,v))),x),Not(FalseQ(w))))),
IIntegrate(3011,Integrate(Times(Log(v_),Plus(a_DEFAULT,Times(Log(u_),b_DEFAULT)),w_),x_Symbol),
    Condition(With(list(Set(z,DerivativeDivides(v,Times(w,Subtract(C1,v)),x))),Condition(Subtract(Simp(Times(z,Plus(a,Times(b,Log(u))),PolyLog(C2,Subtract(C1,v))),x),Simp(Dist(b,Integrate(SimplifyIntegrand(Times(z,PolyLog(C2,Subtract(C1,v)),D(u,x),Power(u,CN1)),x),x),x),x)),Not(FalseQ(z)))),And(FreeQ(list(a,b),x),InverseFunctionFreeQ(u,x)))),
IIntegrate(3012,Integrate(Log(Times(Power(Log(Times(d_DEFAULT,Power(x_,n_DEFAULT))),p_DEFAULT),c_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(x,Log(Times(c,Power(Log(Times(d,Power(x,n))),p)))),x),Simp(Dist(Times(n,p),Integrate(Power(Log(Times(d,Power(x,n))),CN1),x),x),x)),FreeQ(List(c,d,n,p),x))),
IIntegrate(3013,Integrate(Times(Plus(a_DEFAULT,Times(Log(Times(Power(Log(Times(d_DEFAULT,Power(x_,n_DEFAULT))),p_DEFAULT),c_DEFAULT)),b_DEFAULT)),Power(x_,CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(Log(Times(d,Power(x,n))),Plus(a,Times(b,Log(Times(c,Power(Log(Times(d,Power(x,n))),p))))),Power(n,CN1)),x),Simp(Times(b,p,Log(x)),x)),FreeQ(List(a,b,c,d,n,p),x))),
IIntegrate(3014,Integrate(Times(Plus(a_DEFAULT,Times(Log(Times(Power(Log(Times(d_DEFAULT,Power(x_,n_DEFAULT))),p_DEFAULT),c_DEFAULT)),b_DEFAULT)),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(e,x),Plus(m,C1)),Plus(a,Times(b,Log(Times(c,Power(Log(Times(d,Power(x,n))),p))))),Power(Times(e,Plus(m,C1)),CN1)),x),Simp(Dist(Times(b,n,p,Power(Plus(m,C1),CN1)),Integrate(Times(Power(Times(e,x),m),Power(Log(Times(d,Power(x,n))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e,m,n,p),x),NeQ(m,CN1)))),
IIntegrate(3015,Integrate(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power($p("§rfx"),p_DEFAULT))),b_DEFAULT)),n_DEFAULT),x_Symbol),
    Condition(Subtract(Simp(Times(x,Power(Plus(a,Times(b,Log(Times(c,Power($s("§rfx"),p))))),n)),x),Simp(Dist(Times(b,n,p),Integrate(SimplifyIntegrand(Times(x,Power(Plus(a,Times(b,Log(Times(c,Power($s("§rfx"),p))))),Plus(n,CN1)),D($s("§rfx"),x),Power($s("§rfx"),CN1)),x),x),x),x)),And(FreeQ(List(a,b,c,p),x),RationalFunctionQ($s("§rfx"),x),IGtQ(n,C0)))),
IIntegrate(3016,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power($p("§rfx"),p_DEFAULT))),b_DEFAULT)),n_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(Log(Plus(d,Times(e,x))),Power(Plus(a,Times(b,Log(Times(c,Power($s("§rfx"),p))))),n),Power(e,CN1)),x),Simp(Dist(Times(b,n,p,Power(e,CN1)),Integrate(Times(Log(Plus(d,Times(e,x))),Power(Plus(a,Times(b,Log(Times(c,Power($s("§rfx"),p))))),Plus(n,CN1)),D($s("§rfx"),x),Power($s("§rfx"),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e,p),x),RationalFunctionQ($s("§rfx"),x),IGtQ(n,C0)))),
IIntegrate(3017,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power($p("§rfx"),p_DEFAULT))),b_DEFAULT)),n_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Power(Plus(a,Times(b,Log(Times(c,Power($s("§rfx"),p))))),n),Power(Times(e,Plus(m,C1)),CN1)),x),Simp(Dist(Times(b,n,p,Power(Times(e,Plus(m,C1)),CN1)),Integrate(SimplifyIntegrand(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Power(Plus(a,Times(b,Log(Times(c,Power($s("§rfx"),p))))),Plus(n,CN1)),D($s("§rfx"),x),Power($s("§rfx"),CN1)),x),x),x),x)),And(FreeQ(List(a,b,c,d,e,m,p),x),RationalFunctionQ($s("§rfx"),x),IGtQ(n,C0),Or(EqQ(n,C1),IntegerQ(m)),NeQ(m,CN1)))),
IIntegrate(3018,Integrate(Times(Log(Times(c_DEFAULT,Power($p("§rfx"),n_DEFAULT))),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Power(Plus(d,Times(e,Sqr(x))),CN1),x))),Subtract(Simp(Times(u,Log(Times(c,Power($s("§rfx"),n)))),x),Simp(Dist(n,Integrate(SimplifyIntegrand(Times(u,D($s("§rfx"),x),Power($s("§rfx"),CN1)),x),x),x),x))),And(FreeQ(List(c,d,e,n),x),RationalFunctionQ($s("§rfx"),x),Not(PolynomialQ($s("§rfx"),x))))),
IIntegrate(3019,Integrate(Times(Log(Times(c_DEFAULT,Power($p("§px"),n_DEFAULT))),Power($p("§qx"),CN1)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Power($s("§qx"),CN1),x))),Subtract(Simp(Times(u,Log(Times(c,Power($s("§px"),n)))),x),Simp(Dist(n,Integrate(SimplifyIntegrand(Times(u,D($s("§px"),x),Power($s("§px"),CN1)),x),x),x),x))),And(FreeQ(list(c,n),x),QuadraticQ(list($s("§qx"),$s("§px")),x),EqQ(D(Times($s("§px"),Power($s("§qx"),CN1)),x),C0)))),
IIntegrate(3020,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power($p("§rfx"),p_DEFAULT))),b_DEFAULT)),n_DEFAULT),$p("§rgx")),x_Symbol),
    Condition(With(list(Set(u,ExpandIntegrand(Power(Plus(a,Times(b,Log(Times(c,Power($s("§rfx"),p))))),n),$s("§rgx"),x))),Condition(Integrate(u,x),SumQ(u))),And(FreeQ(List(a,b,c,p),x),RationalFunctionQ($s("§rfx"),x),RationalFunctionQ($s("§rgx"),x),IGtQ(n,C0))))
  );
}
