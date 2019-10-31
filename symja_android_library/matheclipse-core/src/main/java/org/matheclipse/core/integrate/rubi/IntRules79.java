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
public class IntRules79 { 
  public static IAST RULES = List( 
IIntegrate(1976,Int(Times(Power(u_,m_DEFAULT),Power(v_,p_DEFAULT),Power(w_,q_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(ExpandToSum(u,x),m),Power(ExpandToSum(v,x),p),Power(ExpandToSum(w,x),q)),x),And(FreeQ(List(m,p,q),x),BinomialQ(List(u,v,w),x),EqQ(Subtract(BinomialDegree(u,x),BinomialDegree(v,x)),C0),EqQ(Subtract(BinomialDegree(u,x),BinomialDegree(w,x)),C0),Not(BinomialMatchQ(List(u,v,w),x))))),
IIntegrate(1977,Int(Times(Power(u_,p_DEFAULT),Power(v_,q_DEFAULT),Power(Times(g_DEFAULT,x_),m_DEFAULT),Power(z_,r_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(Times(g,x),m),Power(ExpandToSum(u,x),p),Power(ExpandToSum(v,x),q),Power(ExpandToSum(z,x),r)),x),And(FreeQ(List(g,m,p,q,r),x),BinomialQ(List(u,v,z),x),EqQ(Subtract(BinomialDegree(u,x),BinomialDegree(v,x)),C0),EqQ(Subtract(BinomialDegree(u,x),BinomialDegree(z,x)),C0),Not(BinomialMatchQ(List(u,v,z),x))))),
IIntegrate(1978,Int(Times($p("§pq"),Power(u_,p_DEFAULT),Power(Times(c_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(Times(c,x),m),$s("§pq"),Power(ExpandToSum(u,x),p)),x),And(FreeQ(List(c,m,p),x),PolyQ($s("§pq"),x),BinomialQ(u,x),Not(BinomialMatchQ(u,x))))),
IIntegrate(1979,Int(Power(u_,p_),x_Symbol),
    Condition(Int(Power(ExpandToSum(u,x),p),x),And(FreeQ(p,x),GeneralizedBinomialQ(u,x),Not(GeneralizedBinomialMatchQ(u,x))))),
IIntegrate(1980,Int(Times(Power(u_,p_DEFAULT),Power(Times(c_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(Times(c,x),m),Power(ExpandToSum(u,x),p)),x),And(FreeQ(List(c,m,p),x),GeneralizedBinomialQ(u,x),Not(GeneralizedBinomialMatchQ(u,x))))),
IIntegrate(1981,Int(Power(u_,p_),x_Symbol),
    Condition(Int(Power(ExpandToSum(u,x),p),x),And(FreeQ(p,x),QuadraticQ(u,x),Not(QuadraticMatchQ(u,x))))),
IIntegrate(1982,Int(Times(Power(u_,m_DEFAULT),Power(v_,p_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(ExpandToSum(u,x),m),Power(ExpandToSum(v,x),p)),x),And(FreeQ(List(m,p),x),LinearQ(u,x),QuadraticQ(v,x),Not(And(LinearMatchQ(u,x),QuadraticMatchQ(v,x)))))),
IIntegrate(1983,Int(Times(Power(u_,m_DEFAULT),Power(v_,n_DEFAULT),Power(w_,p_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(ExpandToSum(u,x),m),Power(ExpandToSum(v,x),n),Power(ExpandToSum(w,x),p)),x),And(FreeQ(List(m,n,p),x),LinearQ(List(u,v),x),QuadraticQ(w,x),Not(And(LinearMatchQ(List(u,v),x),QuadraticMatchQ(w,x)))))),
IIntegrate(1984,Int(Times(Power(u_,p_DEFAULT),Power(v_,q_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(ExpandToSum(u,x),p),Power(ExpandToSum(v,x),q)),x),And(FreeQ(List(p,q),x),QuadraticQ(List(u,v),x),Not(QuadraticMatchQ(List(u,v),x))))),
IIntegrate(1985,Int(Times(Power(u_,p_DEFAULT),Power(v_,q_DEFAULT),Power(z_,m_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(ExpandToSum(z,x),m),Power(ExpandToSum(u,x),p),Power(ExpandToSum(v,x),q)),x),And(FreeQ(List(m,p,q),x),LinearQ(z,x),QuadraticQ(List(u,v),x),Not(And(LinearMatchQ(z,x),QuadraticMatchQ(List(u,v),x)))))),
IIntegrate(1986,Int(Times($p("§pq"),Power(u_,p_DEFAULT)),x_Symbol),
    Condition(Int(Times($s("§pq"),Power(ExpandToSum(u,x),p)),x),And(FreeQ(p,x),PolyQ($s("§pq"),x),QuadraticQ(u,x),Not(QuadraticMatchQ(u,x))))),
IIntegrate(1987,Int(Times($p("§pq"),Power(u_,m_DEFAULT),Power(v_,p_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(ExpandToSum(u,x),m),$s("§pq"),Power(ExpandToSum(v,x),p)),x),And(FreeQ(List(m,p),x),PolyQ($s("§pq"),x),LinearQ(u,x),QuadraticQ(v,x),Not(And(LinearMatchQ(u,x),QuadraticMatchQ(v,x)))))),
IIntegrate(1988,Int(Power(u_,p_),x_Symbol),
    Condition(Int(Power(ExpandToSum(u,x),p),x),And(FreeQ(p,x),TrinomialQ(u,x),Not(TrinomialMatchQ(u,x))))),
IIntegrate(1989,Int(Times(Power(u_,p_DEFAULT),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(Times(d,x),m),Power(ExpandToSum(u,x),p)),x),And(FreeQ(List(d,m,p),x),TrinomialQ(u,x),Not(TrinomialMatchQ(u,x))))),
IIntegrate(1990,Int(Times(Power(u_,q_DEFAULT),Power(v_,p_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(ExpandToSum(u,x),q),Power(ExpandToSum(v,x),p)),x),And(FreeQ(List(p,q),x),BinomialQ(u,x),TrinomialQ(v,x),Not(And(BinomialMatchQ(u,x),TrinomialMatchQ(v,x)))))),
IIntegrate(1991,Int(Times(Power(u_,q_DEFAULT),Power(v_,p_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(ExpandToSum(u,x),q),Power(ExpandToSum(v,x),p)),x),And(FreeQ(List(p,q),x),BinomialQ(u,x),BinomialQ(v,x),Not(And(BinomialMatchQ(u,x),BinomialMatchQ(v,x)))))),
IIntegrate(1992,Int(Times(Power(u_,p_DEFAULT),Power(Times(f_DEFAULT,x_),m_DEFAULT),Power(z_,q_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(Times(f,x),m),Power(ExpandToSum(z,x),q),Power(ExpandToSum(u,x),p)),x),And(FreeQ(List(f,m,p,q),x),BinomialQ(z,x),TrinomialQ(u,x),Not(And(BinomialMatchQ(z,x),TrinomialMatchQ(u,x)))))),
IIntegrate(1993,Int(Times(Power(u_,p_DEFAULT),Power(Times(f_DEFAULT,x_),m_DEFAULT),Power(z_,q_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(Times(f,x),m),Power(ExpandToSum(z,x),q),Power(ExpandToSum(u,x),p)),x),And(FreeQ(List(f,m,p,q),x),BinomialQ(z,x),BinomialQ(u,x),Not(And(BinomialMatchQ(z,x),BinomialMatchQ(u,x)))))),
IIntegrate(1994,Int(Times($p("§pq"),Power(u_,p_DEFAULT)),x_Symbol),
    Condition(Int(Times($s("§pq"),Power(ExpandToSum(u,x),p)),x),And(FreeQ(p,x),PolyQ($s("§pq"),x),TrinomialQ(u,x),Not(TrinomialMatchQ(u,x))))),
IIntegrate(1995,Int(Times($p("§pq"),Power(u_,p_DEFAULT),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(Times(d,x),m),$s("§pq"),Power(ExpandToSum(u,x),p)),x),And(FreeQ(List(d,m,p),x),PolyQ($s("§pq"),x),TrinomialQ(u,x),Not(TrinomialMatchQ(u,x))))),
IIntegrate(1996,Int(Power(u_,p_),x_Symbol),
    Condition(Int(Power(ExpandToSum(u,x),p),x),And(FreeQ(p,x),GeneralizedTrinomialQ(u,x),Not(GeneralizedTrinomialMatchQ(u,x))))),
IIntegrate(1997,Int(Times(Power(u_,p_DEFAULT),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(Times(d,x),m),Power(ExpandToSum(u,x),p)),x),And(FreeQ(List(d,m,p),x),GeneralizedTrinomialQ(u,x),Not(GeneralizedTrinomialMatchQ(u,x))))),
IIntegrate(1998,Int(Times(Power(u_,p_DEFAULT),z_),x_Symbol),
    Condition(Int(Times(ExpandToSum(z,x),Power(ExpandToSum(u,x),p)),x),And(FreeQ(p,x),BinomialQ(z,x),GeneralizedTrinomialQ(u,x),EqQ(Subtract(BinomialDegree(z,x),GeneralizedTrinomialDegree(u,x)),C0),Not(And(BinomialMatchQ(z,x),GeneralizedTrinomialMatchQ(u,x)))))),
IIntegrate(1999,Int(Times(Power(u_,p_DEFAULT),Power(Times(f_DEFAULT,x_),m_DEFAULT),z_),x_Symbol),
    Condition(Int(Times(Power(Times(f,x),m),ExpandToSum(z,x),Power(ExpandToSum(u,x),p)),x),And(FreeQ(List(f,m,p),x),BinomialQ(z,x),GeneralizedTrinomialQ(u,x),EqQ(Subtract(BinomialDegree(z,x),GeneralizedTrinomialDegree(u,x)),C0),Not(And(BinomialMatchQ(z,x),GeneralizedTrinomialMatchQ(u,x)))))),
IIntegrate(2000,Int(Power(Plus(Times(a_DEFAULT,Power(x_,j_DEFAULT)),Times(b_DEFAULT,Power(x_,n_DEFAULT))),p_),x_Symbol),
    Condition(Simp(Times(Power(Plus(Times(a,Power(x,j)),Times(b,Power(x,n))),Plus(p,C1)),Power(Times(b,Subtract(n,j),Plus(p,C1),Power(x,Subtract(n,C1))),CN1)),x),And(FreeQ(List(a,b,j,n,p),x),Not(IntegerQ(p)),NeQ(n,j),EqQ(Plus(Times(j,p),Negate(n),j,C1),C0))))
  );
}
