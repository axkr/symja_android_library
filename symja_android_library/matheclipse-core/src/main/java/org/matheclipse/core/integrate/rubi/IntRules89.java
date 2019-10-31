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
public class IntRules89 { 
  public static IAST RULES = List( 
IIntegrate(2226,Int(Times(Power(F_,Plus(a_DEFAULT,Times(b_DEFAULT,Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),n_)))),u_),x_Symbol),
    Condition(Int(ExpandLinearProduct(Power(FSymbol,Plus(a,Times(b,Power(Plus(c,Times(d,x)),n)))),u,c,d,x),x),And(FreeQ(List(FSymbol,a,b,c,d,n),x),PolynomialQ(u,x)))),
IIntegrate(2227,Int(Times(u_DEFAULT,Power(F_,Plus(a_DEFAULT,Times(b_DEFAULT,v_)))),x_Symbol),
    Condition(Int(Times(u,Power(FSymbol,Plus(a,Times(b,NormalizePowerOfLinear(v,x))))),x),And(FreeQ(List(FSymbol,a,b),x),PolynomialQ(u,x),PowerOfLinearQ(v,x),Not(PowerOfLinearMatchQ(v,x))))),
IIntegrate(2228,Int(Times(Power(F_,Plus(a_DEFAULT,Times(b_DEFAULT,Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),CN1)))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),CN1),Power(Plus(g_DEFAULT,Times(h_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Negate(Dist(Times(d,Power(Times(f,Subtract(Times(d,g),Times(c,h))),CN1)),Subst(Int(Times(Power(FSymbol,Plus(a,Times(CN1,b,h,Power(Subtract(Times(d,g),Times(c,h)),CN1)),Times(d,b,x,Power(Subtract(Times(d,g),Times(c,h)),CN1)))),Power(x,CN1)),x),x,Times(Plus(g,Times(h,x)),Power(Plus(c,Times(d,x)),CN1))),x)),And(FreeQ(List(FSymbol,a,b,c,d,e,f),x),EqQ(Subtract(Times(d,e),Times(c,f)),C0)))),
IIntegrate(2229,Int(Times(Power(F_,Plus(e_DEFAULT,Times(f_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),CN1)))),Power(Plus(g_DEFAULT,Times(h_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Dist(Power(FSymbol,Plus(e,Times(f,b,Power(d,CN1)))),Int(Power(Plus(g,Times(h,x)),m),x),x),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g,h,m),x),EqQ(Subtract(Times(b,c),Times(a,d)),C0)))),
IIntegrate(2230,Int(Times(Power(F_,Plus(e_DEFAULT,Times(f_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),CN1)))),Power(Plus(g_DEFAULT,Times(h_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(Plus(g,Times(h,x)),m),Power(FSymbol,Subtract(Times(Plus(Times(d,e),Times(b,f)),Power(d,CN1)),Times(f,Subtract(Times(b,c),Times(a,d)),Power(Times(d,Plus(c,Times(d,x))),CN1))))),x),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g,h,m),x),NeQ(Subtract(Times(b,c),Times(a,d)),C0),EqQ(Subtract(Times(d,g),Times(c,h)),C0)))),
IIntegrate(2231,Int(Times(Power(F_,Plus(e_DEFAULT,Times(f_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),CN1)))),Power(Plus(g_DEFAULT,Times(h_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Subtract(Dist(Times(d,Power(h,CN1)),Int(Times(Power(FSymbol,Plus(e,Times(f,Plus(a,Times(b,x)),Power(Plus(c,Times(d,x)),CN1)))),Power(Plus(c,Times(d,x)),CN1)),x),x),Dist(Times(Subtract(Times(d,g),Times(c,h)),Power(h,CN1)),Int(Times(Power(FSymbol,Plus(e,Times(f,Plus(a,Times(b,x)),Power(Plus(c,Times(d,x)),CN1)))),Power(Times(Plus(c,Times(d,x)),Plus(g,Times(h,x))),CN1)),x),x)),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g,h),x),NeQ(Subtract(Times(b,c),Times(a,d)),C0),NeQ(Subtract(Times(d,g),Times(c,h)),C0)))),
IIntegrate(2232,Int(Times(Power(F_,Plus(e_DEFAULT,Times(f_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),CN1)))),Power(Plus(g_DEFAULT,Times(h_DEFAULT,x_)),m_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(g,Times(h,x)),Plus(m,C1)),Power(FSymbol,Plus(e,Times(f,Plus(a,Times(b,x)),Power(Plus(c,Times(d,x)),CN1)))),Power(Times(h,Plus(m,C1)),CN1)),x),Dist(Times(f,Subtract(Times(b,c),Times(a,d)),Log(FSymbol),Power(Times(h,Plus(m,C1)),CN1)),Int(Times(Power(Plus(g,Times(h,x)),Plus(m,C1)),Power(FSymbol,Plus(e,Times(f,Plus(a,Times(b,x)),Power(Plus(c,Times(d,x)),CN1)))),Power(Plus(c,Times(d,x)),CN2)),x),x)),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g,h),x),NeQ(Subtract(Times(b,c),Times(a,d)),C0),NeQ(Subtract(Times(d,g),Times(c,h)),C0),ILtQ(m,CN1)))),
IIntegrate(2233,Int(Times(Power(F_,Plus(e_DEFAULT,Times(f_DEFAULT,Plus(a_DEFAULT,Times(b_DEFAULT,x_)),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),CN1)))),Power(Plus(g_DEFAULT,Times(h_DEFAULT,x_)),CN1),Power(Plus(i_DEFAULT,Times(j_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Negate(Dist(Times(d,Power(Times(h,Subtract(Times(d,i),Times(c,j))),CN1)),Subst(Int(Times(Power(FSymbol,Subtract(Plus(e,Times(f,Subtract(Times(b,i),Times(a,j)),Power(Subtract(Times(d,i),Times(c,j)),CN1))),Times(Subtract(Times(b,c),Times(a,d)),f,x,Power(Subtract(Times(d,i),Times(c,j)),CN1)))),Power(x,CN1)),x),x,Times(Plus(i,Times(j,x)),Power(Plus(c,Times(d,x)),CN1))),x)),And(FreeQ(List(FSymbol,a,b,c,d,e,f,g,h),x),EqQ(Subtract(Times(d,g),Times(c,h)),C0)))),
IIntegrate(2234,Int(Power(F_,Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),x_Symbol),
    Condition(Dist(Power(FSymbol,Subtract(a,Times(Sqr(b),Power(Times(C4,c),CN1)))),Int(Power(FSymbol,Times(Sqr(Plus(b,Times(C2,c,x))),Power(Times(C4,c),CN1))),x),x),FreeQ(List(FSymbol,a,b,c),x))),
IIntegrate(2235,Int(Power(F_,v_),x_Symbol),
    Condition(Int(Power(FSymbol,ExpandToSum(v,x)),x),And(FreeQ(FSymbol,x),QuadraticQ(v,x),Not(QuadraticMatchQ(v,x))))),
IIntegrate(2236,Int(Times(Power(F_,Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),Plus(d_DEFAULT,Times(e_DEFAULT,x_))),x_Symbol),
    Condition(Simp(Times(e,Power(FSymbol,Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c,Log(FSymbol)),CN1)),x),And(FreeQ(List(FSymbol,a,b,c,d,e),x),EqQ(Subtract(Times(b,e),Times(C2,c,d)),C0)))),
IIntegrate(2237,Int(Times(Power(F_,Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_)),x_Symbol),
    Condition(Subtract(Simp(Times(e,Power(Plus(d,Times(e,x)),Subtract(m,C1)),Power(FSymbol,Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c,Log(FSymbol)),CN1)),x),Dist(Times(Subtract(m,C1),Sqr(e),Power(Times(C2,c,Log(FSymbol)),CN1)),Int(Times(Power(Plus(d,Times(e,x)),Subtract(m,C2)),Power(FSymbol,Plus(a,Times(b,x),Times(c,Sqr(x))))),x),x)),And(FreeQ(List(FSymbol,a,b,c,d,e),x),EqQ(Subtract(Times(b,e),Times(C2,c,d)),C0),GtQ(m,C1)))),
IIntegrate(2238,Int(Times(Power(F_,Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Simp(Times(C1,Power(FSymbol,Subtract(a,Times(Sqr(b),Power(Times(C4,c),CN1)))),ExpIntegralEi(Times(Sqr(Plus(b,Times(C2,c,x))),Log(FSymbol),Power(Times(C4,c),CN1))),Power(Times(C2,e),CN1)),x),And(FreeQ(List(FSymbol,a,b,c,d,e),x),EqQ(Subtract(Times(b,e),Times(C2,c,d)),C0)))),
IIntegrate(2239,Int(Times(Power(F_,Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Power(FSymbol,Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(e,Plus(m,C1)),CN1)),x),Dist(Times(C2,c,Log(FSymbol),Power(Times(Sqr(e),Plus(m,C1)),CN1)),Int(Times(Power(Plus(d,Times(e,x)),Plus(m,C2)),Power(FSymbol,Plus(a,Times(b,x),Times(c,Sqr(x))))),x),x)),And(FreeQ(List(FSymbol,a,b,c,d,e),x),EqQ(Subtract(Times(b,e),Times(C2,c,d)),C0),LtQ(m,CN1)))),
IIntegrate(2240,Int(Times(Power(F_,Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),Plus(d_DEFAULT,Times(e_DEFAULT,x_))),x_Symbol),
    Condition(Subtract(Simp(Times(e,Power(FSymbol,Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c,Log(FSymbol)),CN1)),x),Dist(Times(Subtract(Times(b,e),Times(C2,c,d)),Power(Times(C2,c),CN1)),Int(Power(FSymbol,Plus(a,Times(b,x),Times(c,Sqr(x)))),x),x)),And(FreeQ(List(FSymbol,a,b,c,d,e),x),NeQ(Subtract(Times(b,e),Times(C2,c,d)),C0)))),
IIntegrate(2241,Int(Times(Power(F_,Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_)),x_Symbol),
    Condition(Plus(Simp(Times(e,Power(Plus(d,Times(e,x)),Subtract(m,C1)),Power(FSymbol,Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c,Log(FSymbol)),CN1)),x),Negate(Dist(Times(Subtract(Times(b,e),Times(C2,c,d)),Power(Times(C2,c),CN1)),Int(Times(Power(Plus(d,Times(e,x)),Subtract(m,C1)),Power(FSymbol,Plus(a,Times(b,x),Times(c,Sqr(x))))),x),x)),Negate(Dist(Times(Subtract(m,C1),Sqr(e),Power(Times(C2,c,Log(FSymbol)),CN1)),Int(Times(Power(Plus(d,Times(e,x)),Subtract(m,C2)),Power(FSymbol,Plus(a,Times(b,x),Times(c,Sqr(x))))),x),x))),And(FreeQ(List(FSymbol,a,b,c,d,e),x),NeQ(Subtract(Times(b,e),Times(C2,c,d)),C0),GtQ(m,C1)))),
IIntegrate(2242,Int(Times(Power(F_,Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Power(FSymbol,Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(e,Plus(m,C1)),CN1)),x),Negate(Dist(Times(C2,c,Log(FSymbol),Power(Times(Sqr(e),Plus(m,C1)),CN1)),Int(Times(Power(Plus(d,Times(e,x)),Plus(m,C2)),Power(FSymbol,Plus(a,Times(b,x),Times(c,Sqr(x))))),x),x)),Negate(Dist(Times(Subtract(Times(b,e),Times(C2,c,d)),Log(FSymbol),Power(Times(Sqr(e),Plus(m,C1)),CN1)),Int(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Power(FSymbol,Plus(a,Times(b,x),Times(c,Sqr(x))))),x),x))),And(FreeQ(List(FSymbol,a,b,c,d,e),x),NeQ(Subtract(Times(b,e),Times(C2,c,d)),C0),LtQ(m,CN1)))),
IIntegrate(2243,Int(Times(Power(F_,Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(FSymbol,Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Plus(d,Times(e,x)),m)),x),FreeQ(List(FSymbol,a,b,c,d,e,m),x))),
IIntegrate(2244,Int(Times(Power(F_,v_),Power(u_,m_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(ExpandToSum(u,x),m),Power(FSymbol,ExpandToSum(v,x))),x),And(FreeQ(List(FSymbol,m),x),LinearQ(u,x),QuadraticQ(v,x),Not(And(LinearMatchQ(u,x),QuadraticMatchQ(v,x)))))),
IIntegrate(2245,Int(Times(Power(F_,Times(e_DEFAULT,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(F_,v_))),p_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(With(List(Set(u,IntHide(Times(Power(FSymbol,Times(e,Plus(c,Times(d,x)))),Power(Plus(a,Times(b,Power(FSymbol,v))),p)),x))),Subtract(Dist(Power(x,m),u,x),Dist(m,Int(Times(Power(x,Subtract(m,C1)),u),x),x))),And(FreeQ(List(FSymbol,a,b,c,d,e),x),EqQ(v,Times(C2,e,Plus(c,Times(d,x)))),GtQ(m,C0),ILtQ(p,C0)))),
IIntegrate(2246,Int(Times(Power(Power(F_,Times(e_DEFAULT,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),n_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(Power(F_,Times(e_DEFAULT,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),n_DEFAULT))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(Times(d,e,n,Log(FSymbol)),CN1),Subst(Int(Power(Plus(a,Times(b,x)),p),x),x,Power(Power(FSymbol,Times(e,Plus(c,Times(d,x)))),n)),x),FreeQ(List(FSymbol,a,b,c,d,e,n,p),x))),
IIntegrate(2247,Int(Times(Power(Plus(a_,Times(b_DEFAULT,Power(Power(F_,Times(e_DEFAULT,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),n_DEFAULT))),p_DEFAULT),Power(Power(G_,Times(h_DEFAULT,Plus(f_DEFAULT,Times(g_DEFAULT,x_)))),m_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(Power(GSymbol,Times(h,Plus(f,Times(g,x)))),m),Power(Power(Power(FSymbol,Times(e,Plus(c,Times(d,x)))),n),CN1)),Int(Times(Power(Power(FSymbol,Times(e,Plus(c,Times(d,x)))),n),Power(Plus(a,Times(b,Power(Power(FSymbol,Times(e,Plus(c,Times(d,x)))),n))),p)),x),x),And(FreeQ(List(FSymbol,GSymbol,a,b,c,d,e,f,g,h,m,n,p),x),EqQ(Times(d,e,n,Log(FSymbol)),Times(g,h,m,Log(GSymbol)))))),
IIntegrate(2248,Int(Times(Power(Plus(a_,Times(b_DEFAULT,Power(F_,Times(e_DEFAULT,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),p_DEFAULT),Power(G_,Times(h_DEFAULT,Plus(f_DEFAULT,Times(g_DEFAULT,x_))))),x_Symbol),
    Condition(With(List(Set(m,FullSimplify(Times(g,h,Log(GSymbol),Power(Times(d,e,Log(FSymbol)),CN1))))),Condition(Dist(Times(Denominator(m),Power(GSymbol,Subtract(Times(f,h),Times(c,g,h,Power(d,CN1)))),Power(Times(d,e,Log(FSymbol)),CN1)),Subst(Int(Times(Power(x,Subtract(Numerator(m),C1)),Power(Plus(a,Times(b,Power(x,Denominator(m)))),p)),x),x,Power(FSymbol,Times(e,Plus(c,Times(d,x)),Power(Denominator(m),CN1)))),x),Or(LeQ(m,CN1),GeQ(m,C1)))),FreeQ(List(FSymbol,GSymbol,a,b,c,d,e,f,g,h,p),x))),
IIntegrate(2249,Int(Times(Power(Plus(a_,Times(b_DEFAULT,Power(F_,Times(e_DEFAULT,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),p_DEFAULT),Power(G_,Times(h_DEFAULT,Plus(f_DEFAULT,Times(g_DEFAULT,x_))))),x_Symbol),
    Condition(With(List(Set(m,FullSimplify(Times(d,e,Log(FSymbol),Power(Times(g,h,Log(GSymbol)),CN1))))),Condition(Dist(Times(Denominator(m),Power(Times(g,h,Log(GSymbol)),CN1)),Subst(Int(Times(Power(x,Subtract(Denominator(m),C1)),Power(Plus(a,Times(b,Power(FSymbol,Subtract(Times(c,e),Times(d,e,f,Power(g,CN1)))),Power(x,Numerator(m)))),p)),x),x,Power(GSymbol,Times(h,Plus(f,Times(g,x)),Power(Denominator(m),CN1)))),x),Or(LtQ(m,CN1),GtQ(m,C1)))),FreeQ(List(FSymbol,GSymbol,a,b,c,d,e,f,g,h,p),x))),
IIntegrate(2250,Int(Times(Power(Plus(a_,Times(b_DEFAULT,Power(F_,Times(e_DEFAULT,Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),p_DEFAULT),Power(G_,Times(h_DEFAULT,Plus(f_DEFAULT,Times(g_DEFAULT,x_))))),x_Symbol),
    Condition(Int(Expand(Times(Power(GSymbol,Times(h,Plus(f,Times(g,x)))),Power(Plus(a,Times(b,Power(FSymbol,Times(e,Plus(c,Times(d,x)))))),p)),x),x),And(FreeQ(List(FSymbol,GSymbol,a,b,c,d,e,f,g,h),x),IGtQ(p,C0))))
  );
}
