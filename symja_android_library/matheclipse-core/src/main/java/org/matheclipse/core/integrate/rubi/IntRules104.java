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
class IntRules104 { 
  public static IAST RULES = List( 
IIntegrate(2081,Integrate(Times(Power(u_,m_DEFAULT),Power(v_,p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(ExpandToSum(u,x),m),Power(ExpandToSum(v,x),p)),x),And(FreeQ(list(m,p),x),LinearQ(u,x),QuadraticQ(v,x),Not(And(LinearMatchQ(u,x),QuadraticMatchQ(v,x)))))),
IIntegrate(2082,Integrate(Times(Power(u_,m_DEFAULT),Power(v_,n_DEFAULT),Power(w_,p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(ExpandToSum(u,x),m),Power(ExpandToSum(v,x),n),Power(ExpandToSum(w,x),p)),x),And(FreeQ(list(m,n,p),x),LinearQ(list(u,v),x),QuadraticQ(w,x),Not(And(LinearMatchQ(list(u,v),x),QuadraticMatchQ(w,x)))))),
IIntegrate(2083,Integrate(Times(Power(u_,p_DEFAULT),Power(v_,q_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(ExpandToSum(u,x),p),Power(ExpandToSum(v,x),q)),x),And(FreeQ(list(p,q),x),QuadraticQ(list(u,v),x),Not(QuadraticMatchQ(list(u,v),x))))),
IIntegrate(2084,Integrate(Times(Power(u_,p_DEFAULT),Power(v_,q_DEFAULT),Power(z_,m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(ExpandToSum(z,x),m),Power(ExpandToSum(u,x),p),Power(ExpandToSum(v,x),q)),x),And(FreeQ(list(m,p,q),x),LinearQ(z,x),QuadraticQ(list(u,v),x),Not(And(LinearMatchQ(z,x),QuadraticMatchQ(list(u,v),x))),Not(MatchQ(Times(Power(z,m),Power(u,p),Power(v,q)),Condition(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x)),m),Sqr(Plus(f_DEFAULT,Times(g_DEFAULT,x))),Power(Plus(a_DEFAULT,Times(b_DEFAULT,x),Times(c_DEFAULT,Sqr(x))),t_DEFAULT)),FreeQ(List(a,b,c,d,e,f,g,t),x)))),Not(MatchQ(Times(Power(z,m),Power(u,p),Power(v,q)),Condition(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x)),m),Sqr(Plus(f_DEFAULT,Times(g_DEFAULT,x))),Power(Plus(a_DEFAULT,Times(c_DEFAULT,Sqr(x))),t_DEFAULT)),FreeQ(List(a,c,d,e,f,g,t),x))))))),
IIntegrate(2085,Integrate(Power(u_,p_),x_Symbol),
    Condition(Integrate(Power(ExpandToSum(u,x),p),x),And(FreeQ(p,x),TrinomialQ(u,x),Not(TrinomialMatchQ(u,x))))),
IIntegrate(2086,Integrate(Times(Power(u_,p_DEFAULT),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Times(d,x),m),Power(ExpandToSum(u,x),p)),x),And(FreeQ(list(d,m,p),x),TrinomialQ(u,x),Not(TrinomialMatchQ(u,x))))),
IIntegrate(2087,Integrate(Times(Power(u_,q_DEFAULT),Power(v_,p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(ExpandToSum(u,x),q),Power(ExpandToSum(v,x),p)),x),And(FreeQ(list(p,q),x),BinomialQ(u,x),TrinomialQ(v,x),Not(And(BinomialMatchQ(u,x),TrinomialMatchQ(v,x)))))),
IIntegrate(2088,Integrate(Times(Power(u_,q_DEFAULT),Power(v_,p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(ExpandToSum(u,x),q),Power(ExpandToSum(v,x),p)),x),And(FreeQ(list(p,q),x),BinomialQ(u,x),BinomialQ(v,x),Not(And(BinomialMatchQ(u,x),BinomialMatchQ(v,x)))))),
IIntegrate(2089,Integrate(Times(Power(u_,p_DEFAULT),Power(Times(f_DEFAULT,x_),m_DEFAULT),Power(z_,q_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Times(f,x),m),Power(ExpandToSum(z,x),q),Power(ExpandToSum(u,x),p)),x),And(FreeQ(List(f,m,p,q),x),BinomialQ(z,x),TrinomialQ(u,x),Not(And(BinomialMatchQ(z,x),TrinomialMatchQ(u,x)))))),
IIntegrate(2090,Integrate(Times(Power(u_,p_DEFAULT),Power(Times(f_DEFAULT,x_),m_DEFAULT),Power(z_,q_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Times(f,x),m),Power(ExpandToSum(z,x),q),Power(ExpandToSum(u,x),p)),x),And(FreeQ(List(f,m,p,q),x),BinomialQ(z,x),BinomialQ(u,x),Not(And(BinomialMatchQ(z,x),BinomialMatchQ(u,x)))))),
IIntegrate(2091,Integrate(Times($p("§px"),Power(u_,p_DEFAULT),Power(z_,q_DEFAULT)),x_Symbol),
    Condition(Integrate(Times($s("§px"),Power(ExpandToSum(z,x),q),Power(ExpandToSum(u,x),p)),x),And(FreeQ(list(p,q),x),PolyQ($s("§px"),x),BinomialQ(z,x),TrinomialQ(u,x),Not(And(BinomialMatchQ(z,x),TrinomialMatchQ(u,x)))))),
IIntegrate(2092,Integrate(Times($p("§px"),Power(u_,p_DEFAULT),Power(z_,q_DEFAULT)),x_Symbol),
    Condition(Integrate(Times($s("§px"),Power(ExpandToSum(z,x),q),Power(ExpandToSum(u,x),p)),x),And(FreeQ(list(p,q),x),BinomialQ(z,x),BinomialQ(u,x),Not(And(BinomialMatchQ(z,x),BinomialMatchQ(u,x)))))),
IIntegrate(2093,Integrate(Power(u_,p_),x_Symbol),
    Condition(Integrate(Power(ExpandToSum(u,x),p),x),And(FreeQ(p,x),GeneralizedTrinomialQ(u,x),Not(GeneralizedTrinomialMatchQ(u,x))))),
IIntegrate(2094,Integrate(Times(Power(u_,p_DEFAULT),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Times(d,x),m),Power(ExpandToSum(u,x),p)),x),And(FreeQ(list(d,m,p),x),GeneralizedTrinomialQ(u,x),Not(GeneralizedTrinomialMatchQ(u,x))))),
IIntegrate(2095,Integrate(Times(Power(u_,p_DEFAULT),z_),x_Symbol),
    Condition(Integrate(Times(ExpandToSum(z,x),Power(ExpandToSum(u,x),p)),x),And(FreeQ(p,x),BinomialQ(z,x),GeneralizedTrinomialQ(u,x),EqQ(Subtract(BinomialDegree(z,x),GeneralizedTrinomialDegree(u,x)),C0),Not(And(BinomialMatchQ(z,x),GeneralizedTrinomialMatchQ(u,x)))))),
IIntegrate(2096,Integrate(Times(Power(u_,p_DEFAULT),Power(Times(f_DEFAULT,x_),m_DEFAULT),z_),x_Symbol),
    Condition(Integrate(Times(Power(Times(f,x),m),ExpandToSum(z,x),Power(ExpandToSum(u,x),p)),x),And(FreeQ(list(f,m,p),x),BinomialQ(z,x),GeneralizedTrinomialQ(u,x),EqQ(Subtract(BinomialDegree(z,x),GeneralizedTrinomialDegree(u,x)),C0),Not(And(BinomialMatchQ(z,x),GeneralizedTrinomialMatchQ(u,x)))))),
IIntegrate(2097,Integrate(Times(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),Plus(A_DEFAULT,Times(B_DEFAULT,x_)),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),CN1D2),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),CN1D2),Power(Plus(g_DEFAULT,Times(h_DEFAULT,x_)),CN1D2)),x_Symbol),
    Condition(Plus(Simp(Times(C2,b,BSymbol,Sqrt(Plus(c,Times(d,x))),Sqrt(Plus(e,Times(f,x))),Sqrt(Plus(g,Times(h,x))),Power(Times(C3,d,f,h),CN1)),x),Simp(Star(Power(Times(C3,d,f,h),CN1),Integrate(Times(Power(Times(Sqrt(Plus(c,Times(d,x))),Sqrt(Plus(e,Times(f,x))),Sqrt(Plus(g,Times(h,x)))),CN1),Simp(Plus(Times(C3,a,ASymbol,d,f,h),Times(CN1,b,BSymbol,Plus(Times(d,e,g),Times(c,f,g),Times(c,e,h))),Times(Plus(Times(C3,ASymbol,b,d,f,h),Times(BSymbol,Subtract(Times(C3,a,d,f,h),Times(C2,b,Plus(Times(d,f,g),Times(d,e,h),Times(c,f,h)))))),x)),x)),x)),x)),FreeQ(List(a,b,c,d,e,f,g,h,ASymbol,BSymbol),x))),
IIntegrate(2098,Integrate(Times(Sqrt(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Plus(A_DEFAULT,Times(B_DEFAULT,x_)),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),CN1D2),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),CN1D2),Power(Plus(g_DEFAULT,Times(h_DEFAULT,x_)),CN1D2)),x_Symbol),
    Condition(Plus(Simp(Times(b,BSymbol,Sqrt(Plus(c,Times(d,x))),Sqrt(Plus(e,Times(f,x))),Sqrt(Plus(g,Times(h,x))),Power(Times(d,f,h,Sqrt(Plus(a,Times(b,x)))),CN1)),x),Negate(Simp(Star(Times(BSymbol,Subtract(Times(b,g),Times(a,h)),Power(Times(C2,f,h),CN1)),Integrate(Times(Sqrt(Plus(e,Times(f,x))),Power(Times(Sqrt(Plus(a,Times(b,x))),Sqrt(Plus(c,Times(d,x))),Sqrt(Plus(g,Times(h,x)))),CN1)),x)),x)),Simp(Star(Times(BSymbol,Subtract(Times(b,e),Times(a,f)),Subtract(Times(b,g),Times(a,h)),Power(Times(C2,d,f,h),CN1)),Integrate(Times(Sqrt(Plus(c,Times(d,x))),Power(Times(Power(Plus(a,Times(b,x)),QQ(3L,2L)),Sqrt(Plus(e,Times(f,x))),Sqrt(Plus(g,Times(h,x)))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,f,g,h,ASymbol,BSymbol),x),EqQ(Subtract(Times(C2,ASymbol,d,f),Times(BSymbol,Plus(Times(d,e),Times(c,f)))),C0)))),
IIntegrate(2099,Integrate(Times(Sqrt(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Plus(A_DEFAULT,Times(B_DEFAULT,x_)),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),CN1D2),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),CN1D2),Power(Plus(g_DEFAULT,Times(h_DEFAULT,x_)),CN1D2)),x_Symbol),
    Condition(Plus(Simp(Times(BSymbol,Sqrt(Plus(a,Times(b,x))),Sqrt(Plus(e,Times(f,x))),Sqrt(Plus(g,Times(h,x))),Power(Times(f,h,Sqrt(Plus(c,Times(d,x)))),CN1)),x),Negate(Simp(Star(Times(BSymbol,Subtract(Times(b,e),Times(a,f)),Subtract(Times(b,g),Times(a,h)),Power(Times(C2,b,f,h),CN1)),Integrate(Power(Times(Sqrt(Plus(a,Times(b,x))),Sqrt(Plus(c,Times(d,x))),Sqrt(Plus(e,Times(f,x))),Sqrt(Plus(g,Times(h,x)))),CN1),x)),x)),Simp(Star(Times(BSymbol,Subtract(Times(d,e),Times(c,f)),Subtract(Times(d,g),Times(c,h)),Power(Times(C2,d,f,h),CN1)),Integrate(Times(Sqrt(Plus(a,Times(b,x))),Power(Times(Power(Plus(c,Times(d,x)),QQ(3L,2L)),Sqrt(Plus(e,Times(f,x))),Sqrt(Plus(g,Times(h,x)))),CN1)),x)),x),Simp(Star(Times(Plus(Times(C2,ASymbol,b,d,f,h),Times(BSymbol,Subtract(Times(a,d,f,h),Times(b,Plus(Times(d,f,g),Times(d,e,h),Times(c,f,h)))))),Power(Times(C2,b,d,f,h),CN1)),Integrate(Times(Sqrt(Plus(a,Times(b,x))),Power(Times(Sqrt(Plus(c,Times(d,x))),Sqrt(Plus(e,Times(f,x))),Sqrt(Plus(g,Times(h,x)))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,f,g,h,ASymbol,BSymbol),x),NeQ(Subtract(Times(C2,ASymbol,d,f),Times(BSymbol,Plus(Times(d,e),Times(c,f)))),C0)))),
IIntegrate(2100,Integrate(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),m_),Plus(A_DEFAULT,Times(B_DEFAULT,x_)),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),CN1D2),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),CN1D2),Power(Plus(g_DEFAULT,Times(h_DEFAULT,x_)),CN1D2)),x_Symbol),
    Condition(Plus(Simp(Times(C2,b,BSymbol,Power(Plus(a,Times(b,x)),Subtract(m,C1)),Sqrt(Plus(c,Times(d,x))),Sqrt(Plus(e,Times(f,x))),Sqrt(Plus(g,Times(h,x))),Power(Times(d,f,h,Plus(Times(C2,m),C1)),CN1)),x),Simp(Star(Power(Times(d,f,h,Plus(Times(C2,m),C1)),CN1),Integrate(Times(Power(Plus(a,Times(b,x)),Subtract(m,C2)),Power(Times(Sqrt(Plus(c,Times(d,x))),Sqrt(Plus(e,Times(f,x))),Sqrt(Plus(g,Times(h,x)))),CN1),Simp(Plus(Times(CN1,b,BSymbol,Plus(Times(a,Plus(Times(d,e,g),Times(c,f,g),Times(c,e,h))),Times(C2,b,c,e,g,Subtract(m,C1)))),Times(Sqr(a),ASymbol,d,f,h,Plus(Times(C2,m),C1)),Times(Subtract(Times(C2,a,ASymbol,b,d,f,h,Plus(Times(C2,m),C1)),Times(BSymbol,Subtract(Plus(Times(C2,a,b,Plus(Times(d,f,g),Times(d,e,h),Times(c,f,h))),Times(Sqr(b),Plus(Times(d,e,g),Times(c,f,g),Times(c,e,h)),Subtract(Times(C2,m),C1))),Times(Sqr(a),d,f,h,Plus(Times(C2,m),C1))))),x),Times(b,Subtract(Times(ASymbol,b,d,f,h,Plus(Times(C2,m),C1)),Times(BSymbol,Subtract(Times(C2,b,Plus(Times(d,f,g),Times(d,e,h),Times(c,f,h)),m),Times(a,d,f,h,Subtract(Times(C4,m),C1))))),Sqr(x))),x)),x)),x)),And(FreeQ(List(a,b,c,d,e,f,g,h,ASymbol,BSymbol),x),IntegerQ(Times(C2,m)),GtQ(m,C1))))
  );
}
