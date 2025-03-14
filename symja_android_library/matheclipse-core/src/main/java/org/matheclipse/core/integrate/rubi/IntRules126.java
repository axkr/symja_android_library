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
class IntRules126 { 
  public static IAST RULES = List( 
IIntegrate(2521,Integrate(Times(Plus(a_DEFAULT,Times(Log(Times(Power(Log(Times(d_DEFAULT,Power(x_,n_DEFAULT))),p_DEFAULT),c_DEFAULT)),b_DEFAULT)),Power(x_,CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(Log(Times(d,Power(x,n))),Plus(a,Times(b,Log(Times(c,Power(Log(Times(d,Power(x,n))),p))))),Power(n,CN1)),x),Simp(Times(b,p,Log(x)),x)),FreeQ(List(a,b,c,d,n,p),x))),
IIntegrate(2522,Integrate(Times(Plus(a_DEFAULT,Times(Log(Times(Power(Log(Times(d_DEFAULT,Power(x_,n_DEFAULT))),p_DEFAULT),c_DEFAULT)),b_DEFAULT)),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(e,x),Plus(m,C1)),Plus(a,Times(b,Log(Times(c,Power(Log(Times(d,Power(x,n))),p))))),Power(Times(e,Plus(m,C1)),CN1)),x),Dist(Times(b,n,p,Power(Plus(m,C1),CN1)),Integrate(Times(Power(Times(e,x),m),Power(Log(Times(d,Power(x,n))),CN1)),x),x)),And(FreeQ(List(a,b,c,d,e,m,n,p),x),NeQ(m,CN1)))),
IIntegrate(2523,Integrate(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power($p("§rfx"),p_DEFAULT))),b_DEFAULT)),n_DEFAULT),x_Symbol),
    Condition(Subtract(Simp(Times(x,Power(Plus(a,Times(b,Log(Times(c,Power($s("§rfx"),p))))),n)),x),Dist(Times(b,n,p),Integrate(SimplifyIntegrand(Times(x,Power(Plus(a,Times(b,Log(Times(c,Power($s("§rfx"),p))))),Subtract(n,C1)),D($s("§rfx"),x),Power($s("§rfx"),CN1)),x),x),x)),And(FreeQ(List(a,b,c,p),x),RationalFunctionQ($s("§rfx"),x),IGtQ(n,C0)))),
IIntegrate(2524,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power($p("§rfx"),p_DEFAULT))),b_DEFAULT)),n_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(Log(Plus(d,Times(e,x))),Power(Plus(a,Times(b,Log(Times(c,Power($s("§rfx"),p))))),n),Power(e,CN1)),x),Dist(Times(b,n,p,Power(e,CN1)),Integrate(Times(Log(Plus(d,Times(e,x))),Power(Plus(a,Times(b,Log(Times(c,Power($s("§rfx"),p))))),Subtract(n,C1)),D($s("§rfx"),x),Power($s("§rfx"),CN1)),x),x)),And(FreeQ(List(a,b,c,d,e,p),x),RationalFunctionQ($s("§rfx"),x),IGtQ(n,C0)))),
IIntegrate(2525,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power($p("§rfx"),p_DEFAULT))),b_DEFAULT)),n_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Power(Plus(a,Times(b,Log(Times(c,Power($s("§rfx"),p))))),n),Power(Times(e,Plus(m,C1)),CN1)),x),Dist(Times(b,n,p,Power(Times(e,Plus(m,C1)),CN1)),Integrate(SimplifyIntegrand(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Power(Plus(a,Times(b,Log(Times(c,Power($s("§rfx"),p))))),Subtract(n,C1)),D($s("§rfx"),x),Power($s("§rfx"),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e,m,p),x),RationalFunctionQ($s("§rfx"),x),IGtQ(n,C0),Or(EqQ(n,C1),IntegerQ(m)),NeQ(m,CN1)))),
IIntegrate(2526,Integrate(Times(Log(Times(c_DEFAULT,Power($p("§rfx"),n_DEFAULT))),Power(Plus(d_,Times(e_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Power(Plus(d,Times(e,Sqr(x))),CN1),x))),Subtract(Simp(Times(u,Log(Times(c,Power($s("§rfx"),n)))),x),Dist(n,Integrate(SimplifyIntegrand(Times(u,D($s("§rfx"),x),Power($s("§rfx"),CN1)),x),x),x))),And(FreeQ(List(c,d,e,n),x),RationalFunctionQ($s("§rfx"),x),Not(PolynomialQ($s("§rfx"),x))))),
IIntegrate(2527,Integrate(Times(Log(Times(c_DEFAULT,Power($p("§px"),n_DEFAULT))),Power($p("§qx"),CN1)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Power($s("§qx"),CN1),x))),Subtract(Simp(Times(u,Log(Times(c,Power($s("§px"),n)))),x),Dist(n,Integrate(SimplifyIntegrand(Times(u,D($s("§px"),x),Power($s("§px"),CN1)),x),x),x))),And(FreeQ(list(c,n),x),QuadraticQ(list($s("§qx"),$s("§px")),x),EqQ(D(Times($s("§px"),Power($s("§qx"),CN1)),x),C0)))),
IIntegrate(2528,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power($p("§rfx"),p_DEFAULT))),b_DEFAULT)),n_DEFAULT),$p("§rgx")),x_Symbol),
    Condition(With(list(Set(u,ExpandIntegrand(Power(Plus(a,Times(b,Log(Times(c,Power($s("§rfx"),p))))),n),$s("§rgx"),x))),Condition(Integrate(u,x),SumQ(u))),And(FreeQ(List(a,b,c,p),x),RationalFunctionQ($s("§rfx"),x),RationalFunctionQ($s("§rgx"),x),IGtQ(n,C0)))),
IIntegrate(2529,Integrate(Times(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power($p("§rfx"),p_DEFAULT))),b_DEFAULT)),n_DEFAULT),$p("§rgx")),x_Symbol),
    Condition(With(list(Set(u,ExpandIntegrand(Times($s("§rgx"),Power(Plus(a,Times(b,Log(Times(c,Power($s("§rfx"),p))))),n)),x))),Condition(Integrate(u,x),SumQ(u))),And(FreeQ(List(a,b,c,p),x),RationalFunctionQ($s("§rfx"),x),RationalFunctionQ($s("§rgx"),x),IGtQ(n,C0)))),
IIntegrate(2530,Integrate(Times(Plus(a_DEFAULT,Times(Log(u_),b_DEFAULT)),$p("§rfx")),x_Symbol),
    Condition(With(list(Set($s("lst"),SubstForFractionalPowerOfLinear(Times($s("§rfx"),Plus(a,Times(b,Log(u)))),x))),Condition(Dist(Times(Part($s("lst"),C2),Part($s("lst"),C4)),Subst(Integrate(Part($s("lst"),C1),x),x,Power(Part($s("lst"),C3),Power(Part($s("lst"),C2),CN1))),x),Not(FalseQ($s("lst"))))),And(FreeQ(list(a,b),x),RationalFunctionQ($s("§rfx"),x)))),
IIntegrate(2531,Integrate(Times(Log(Plus(C1,Times(e_DEFAULT,Power(Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_DEFAULT)))),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(Power(Plus(f,Times(g,x)),m),PolyLog(C2,Times(CN1,e,Power(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),n))),Power(Times(b,c,n,Log(FSymbol)),CN1)),x)),Dist(Times(g,m,Power(Times(b,c,n,Log(FSymbol)),CN1)),Integrate(Times(Power(Plus(f,Times(g,x)),Subtract(m,C1)),PolyLog(C2,Times(CN1,e,Power(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),n)))),x),x)),And(FreeQ(List(FSymbol,a,b,c,e,f,g,n),x),GtQ(m,C0)))),
IIntegrate(2532,Integrate(Times(Log(Plus(d_,Times(e_DEFAULT,Power(Power(F_,Times(c_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_DEFAULT)))),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(f,Times(g,x)),Plus(m,C1)),Log(Plus(d,Times(e,Power(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),n)))),Power(Times(g,Plus(m,C1)),CN1)),x),Integrate(Times(Power(Plus(f,Times(g,x)),m),Log(Plus(C1,Times(e,Power(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),n),Power(d,CN1))))),x),Negate(Simp(Times(Power(Plus(f,Times(g,x)),Plus(m,C1)),Log(Plus(C1,Times(e,Power(Power(FSymbol,Times(c,Plus(a,Times(b,x)))),n),Power(d,CN1)))),Power(Times(g,Plus(m,C1)),CN1)),x))),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g,n),x),GtQ(m,C0),NeQ(d,C1)))),
IIntegrate(2533,Integrate(Log(Plus(d_DEFAULT,Times(e_DEFAULT,x_),Times(f_DEFAULT,Sqrt(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))))),x_Symbol),
    Condition(Plus(Simp(Times(x,Log(Plus(d,Times(e,x),Times(f,Sqrt(Plus(a,Times(b,x),Times(c,Sqr(x)))))))),x),Dist(Times(C1D2,Sqr(f),Subtract(Sqr(b),Times(C4,a,c))),Integrate(Times(x,Power(Subtract(Times(Subtract(Times(C2,d,e),Times(b,Sqr(f))),Plus(a,Times(b,x),Times(c,Sqr(x)))),Times(f,Plus(Times(b,d),Times(CN1,C2,a,e),Times(Subtract(Times(C2,c,d),Times(b,e)),x)),Sqrt(Plus(a,Times(b,x),Times(c,Sqr(x)))))),CN1)),x),x)),And(FreeQ(List(a,b,c,d,e,f),x),EqQ(Subtract(Sqr(e),Times(c,Sqr(f))),C0)))),
IIntegrate(2534,Integrate(Log(Plus(d_DEFAULT,Times(e_DEFAULT,x_),Times(f_DEFAULT,Sqrt(Plus(a_DEFAULT,Times(c_DEFAULT,Sqr(x_))))))),x_Symbol),
    Condition(Subtract(Simp(Times(x,Log(Plus(d,Times(e,x),Times(f,Sqrt(Plus(a,Times(c,Sqr(x)))))))),x),Dist(Times(a,c,Sqr(f)),Integrate(Times(x,Power(Plus(Times(d,e,Plus(a,Times(c,Sqr(x)))),Times(f,Subtract(Times(a,e),Times(c,d,x)),Sqrt(Plus(a,Times(c,Sqr(x)))))),CN1)),x),x)),And(FreeQ(List(a,c,d,e,f),x),EqQ(Subtract(Sqr(e),Times(c,Sqr(f))),C0)))),
IIntegrate(2535,Integrate(Times(Log(Plus(d_DEFAULT,Times(e_DEFAULT,x_),Times(f_DEFAULT,Sqrt(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))))),Power(Times(g_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Times(g,x),Plus(m,C1)),Log(Plus(d,Times(e,x),Times(f,Sqrt(Plus(a,Times(b,x),Times(c,Sqr(x))))))),Power(Times(g,Plus(m,C1)),CN1)),x),Dist(Times(Sqr(f),Subtract(Sqr(b),Times(C4,a,c)),Power(Times(C2,g,Plus(m,C1)),CN1)),Integrate(Times(Power(Times(g,x),Plus(m,C1)),Power(Subtract(Times(Subtract(Times(C2,d,e),Times(b,Sqr(f))),Plus(a,Times(b,x),Times(c,Sqr(x)))),Times(f,Plus(Times(b,d),Times(CN1,C2,a,e),Times(Subtract(Times(C2,c,d),Times(b,e)),x)),Sqrt(Plus(a,Times(b,x),Times(c,Sqr(x)))))),CN1)),x),x)),And(FreeQ(List(a,b,c,d,e,f,g,m),x),EqQ(Subtract(Sqr(e),Times(c,Sqr(f))),C0),NeQ(m,CN1),IntegerQ(Times(C2,m))))),
IIntegrate(2536,Integrate(Times(Log(Plus(d_DEFAULT,Times(e_DEFAULT,x_),Times(f_DEFAULT,Sqrt(Plus(a_DEFAULT,Times(c_DEFAULT,Sqr(x_))))))),Power(Times(g_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(g,x),Plus(m,C1)),Log(Plus(d,Times(e,x),Times(f,Sqrt(Plus(a,Times(c,Sqr(x))))))),Power(Times(g,Plus(m,C1)),CN1)),x),Dist(Times(a,c,Sqr(f),Power(Times(g,Plus(m,C1)),CN1)),Integrate(Times(Power(Times(g,x),Plus(m,C1)),Power(Plus(Times(d,e,Plus(a,Times(c,Sqr(x)))),Times(f,Subtract(Times(a,e),Times(c,d,x)),Sqrt(Plus(a,Times(c,Sqr(x)))))),CN1)),x),x)),And(FreeQ(List(a,c,d,e,f,g,m),x),EqQ(Subtract(Sqr(e),Times(c,Sqr(f))),C0),NeQ(m,CN1),IntegerQ(Times(C2,m))))),
IIntegrate(2537,Integrate(Times(Log(Plus(d_DEFAULT,Times(f_DEFAULT,Sqrt(u_)),Times(e_DEFAULT,x_))),v_DEFAULT),x_Symbol),
    Condition(Integrate(Times(v,Log(Plus(d,Times(e,x),Times(f,Sqrt(ExpandToSum(u,x)))))),x),And(FreeQ(list(d,e,f),x),QuadraticQ(u,x),Not(QuadraticMatchQ(u,x)),Or(EqQ(v,C1),MatchQ(v,Condition(Power(Times(g_DEFAULT,x),m_DEFAULT),FreeQ(list(g,m),x))))))),
IIntegrate(2538,Integrate(Times(Power(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),r_DEFAULT),Power(x_,CN1),Power(Plus(Times(Power(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),q_),b_DEFAULT),Times(a_DEFAULT,Power(x_,m_DEFAULT))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(Log(Plus(Times(a,Power(x,m)),Times(b,Power(Log(Times(c,Power(x,n))),q)))),Power(Times(b,n,q),CN1)),x),Dist(Times(a,m,Power(Times(b,n,q),CN1)),Integrate(Times(Power(x,Subtract(m,C1)),Power(Plus(Times(a,Power(x,m)),Times(b,Power(Log(Times(c,Power(x,n))),q))),CN1)),x),x)),And(FreeQ(List(a,b,c,m,n,q,r),x),EqQ(r,Subtract(q,C1))))),
IIntegrate(2539,Integrate(Times(Power(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),r_DEFAULT),Power(x_,CN1),Power(Plus(Times(Power(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),q_),b_DEFAULT),Times(a_DEFAULT,Power(x_,m_DEFAULT))),p_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Times(Power(Log(Times(c,Power(x,n))),r),Power(x,CN1)),Power(Plus(Times(a,Power(x,m)),Times(b,Power(Log(Times(c,Power(x,n))),q))),p),x),x),And(FreeQ(List(a,b,c,m,n,p,q,r),x),EqQ(r,Subtract(q,C1)),IGtQ(p,C0)))),
IIntegrate(2540,Integrate(Times(Power(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),r_DEFAULT),Power(x_,CN1),Power(Plus(Times(Power(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),q_),b_DEFAULT),Times(a_DEFAULT,Power(x_,m_DEFAULT))),p_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(Times(a,Power(x,m)),Times(b,Power(Log(Times(c,Power(x,n))),q))),Plus(p,C1)),Power(Times(b,n,q,Plus(p,C1)),CN1)),x),Dist(Times(a,m,Power(Times(b,n,q),CN1)),Integrate(Times(Power(x,Subtract(m,C1)),Power(Plus(Times(a,Power(x,m)),Times(b,Power(Log(Times(c,Power(x,n))),q))),p)),x),x)),And(FreeQ(List(a,b,c,m,n,p,q,r),x),EqQ(r,Subtract(q,C1)),NeQ(p,CN1))))
  );
}
