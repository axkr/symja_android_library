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
class IntRules99 { 
  public static IAST RULES = List( 
IIntegrate(1981,Integrate(Power(u_,p_),x_Symbol),
    Condition(Integrate(Power(ExpandToSum(u,x),p),x),And(FreeQ(p,x),QuadraticQ(u,x),Not(QuadraticMatchQ(u,x))))),
IIntegrate(1982,Integrate(Times(Power(u_,m_DEFAULT),Power(v_,p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(ExpandToSum(u,x),m),Power(ExpandToSum(v,x),p)),x),And(FreeQ(list(m,p),x),LinearQ(u,x),QuadraticQ(v,x),Not(And(LinearMatchQ(u,x),QuadraticMatchQ(v,x)))))),
IIntegrate(1983,Integrate(Times(Power(u_,m_DEFAULT),Power(v_,n_DEFAULT),Power(w_,p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(ExpandToSum(u,x),m),Power(ExpandToSum(v,x),n),Power(ExpandToSum(w,x),p)),x),And(FreeQ(list(m,n,p),x),LinearQ(list(u,v),x),QuadraticQ(w,x),Not(And(LinearMatchQ(list(u,v),x),QuadraticMatchQ(w,x)))))),
IIntegrate(1984,Integrate(Times(Power(u_,p_DEFAULT),Power(v_,q_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(ExpandToSum(u,x),p),Power(ExpandToSum(v,x),q)),x),And(FreeQ(list(p,q),x),QuadraticQ(list(u,v),x),Not(QuadraticMatchQ(list(u,v),x))))),
IIntegrate(1985,Integrate(Times(Power(u_,p_DEFAULT),Power(v_,q_DEFAULT),Power(z_,m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(ExpandToSum(z,x),m),Power(ExpandToSum(u,x),p),Power(ExpandToSum(v,x),q)),x),And(FreeQ(list(m,p,q),x),LinearQ(z,x),QuadraticQ(list(u,v),x),Not(And(LinearMatchQ(z,x),QuadraticMatchQ(list(u,v),x)))))),
IIntegrate(1986,Integrate(Times($p("§pq"),Power(u_,p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times($s("§pq"),Power(ExpandToSum(u,x),p)),x),And(FreeQ(p,x),PolyQ($s("§pq"),x),QuadraticQ(u,x),Not(QuadraticMatchQ(u,x))))),
IIntegrate(1987,Integrate(Times($p("§pq"),Power(u_,m_DEFAULT),Power(v_,p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(ExpandToSum(u,x),m),$s("§pq"),Power(ExpandToSum(v,x),p)),x),And(FreeQ(list(m,p),x),PolyQ($s("§pq"),x),LinearQ(u,x),QuadraticQ(v,x),Not(And(LinearMatchQ(u,x),QuadraticMatchQ(v,x)))))),
IIntegrate(1988,Integrate(Power(u_,p_),x_Symbol),
    Condition(Integrate(Power(ExpandToSum(u,x),p),x),And(FreeQ(p,x),TrinomialQ(u,x),Not(TrinomialMatchQ(u,x))))),
IIntegrate(1989,Integrate(Times(Power(u_,p_DEFAULT),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Times(d,x),m),Power(ExpandToSum(u,x),p)),x),And(FreeQ(list(d,m,p),x),TrinomialQ(u,x),Not(TrinomialMatchQ(u,x))))),
IIntegrate(1990,Integrate(Times(Power(u_,q_DEFAULT),Power(v_,p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(ExpandToSum(u,x),q),Power(ExpandToSum(v,x),p)),x),And(FreeQ(list(p,q),x),BinomialQ(u,x),TrinomialQ(v,x),Not(And(BinomialMatchQ(u,x),TrinomialMatchQ(v,x)))))),
IIntegrate(1991,Integrate(Times(Power(u_,q_DEFAULT),Power(v_,p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(ExpandToSum(u,x),q),Power(ExpandToSum(v,x),p)),x),And(FreeQ(list(p,q),x),BinomialQ(u,x),BinomialQ(v,x),Not(And(BinomialMatchQ(u,x),BinomialMatchQ(v,x)))))),
IIntegrate(1992,Integrate(Times(Power(u_,p_DEFAULT),Power(Times(f_DEFAULT,x_),m_DEFAULT),Power(z_,q_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Times(f,x),m),Power(ExpandToSum(z,x),q),Power(ExpandToSum(u,x),p)),x),And(FreeQ(List(f,m,p,q),x),BinomialQ(z,x),TrinomialQ(u,x),Not(And(BinomialMatchQ(z,x),TrinomialMatchQ(u,x)))))),
IIntegrate(1993,Integrate(Times(Power(u_,p_DEFAULT),Power(Times(f_DEFAULT,x_),m_DEFAULT),Power(z_,q_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Times(f,x),m),Power(ExpandToSum(z,x),q),Power(ExpandToSum(u,x),p)),x),And(FreeQ(List(f,m,p,q),x),BinomialQ(z,x),BinomialQ(u,x),Not(And(BinomialMatchQ(z,x),BinomialMatchQ(u,x)))))),
IIntegrate(1994,Integrate(Times($p("§pq"),Power(u_,p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times($s("§pq"),Power(ExpandToSum(u,x),p)),x),And(FreeQ(p,x),PolyQ($s("§pq"),x),TrinomialQ(u,x),Not(TrinomialMatchQ(u,x))))),
IIntegrate(1995,Integrate(Times($p("§pq"),Power(u_,p_DEFAULT),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Times(d,x),m),$s("§pq"),Power(ExpandToSum(u,x),p)),x),And(FreeQ(list(d,m,p),x),PolyQ($s("§pq"),x),TrinomialQ(u,x),Not(TrinomialMatchQ(u,x))))),
IIntegrate(1996,Integrate(Power(u_,p_),x_Symbol),
    Condition(Integrate(Power(ExpandToSum(u,x),p),x),And(FreeQ(p,x),GeneralizedTrinomialQ(u,x),Not(GeneralizedTrinomialMatchQ(u,x))))),
IIntegrate(1997,Integrate(Times(Power(u_,p_DEFAULT),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Times(d,x),m),Power(ExpandToSum(u,x),p)),x),And(FreeQ(list(d,m,p),x),GeneralizedTrinomialQ(u,x),Not(GeneralizedTrinomialMatchQ(u,x))))),
IIntegrate(1998,Integrate(Times(Power(u_,p_DEFAULT),z_),x_Symbol),
    Condition(Integrate(Times(ExpandToSum(z,x),Power(ExpandToSum(u,x),p)),x),And(FreeQ(p,x),BinomialQ(z,x),GeneralizedTrinomialQ(u,x),EqQ(Subtract(BinomialDegree(z,x),GeneralizedTrinomialDegree(u,x)),C0),Not(And(BinomialMatchQ(z,x),GeneralizedTrinomialMatchQ(u,x)))))),
IIntegrate(1999,Integrate(Times(Power(u_,p_DEFAULT),Power(Times(f_DEFAULT,x_),m_DEFAULT),z_),x_Symbol),
    Condition(Integrate(Times(Power(Times(f,x),m),ExpandToSum(z,x),Power(ExpandToSum(u,x),p)),x),And(FreeQ(list(f,m,p),x),BinomialQ(z,x),GeneralizedTrinomialQ(u,x),EqQ(Subtract(BinomialDegree(z,x),GeneralizedTrinomialDegree(u,x)),C0),Not(And(BinomialMatchQ(z,x),GeneralizedTrinomialMatchQ(u,x)))))),
IIntegrate(2000,Integrate(Power(Plus(Times(a_DEFAULT,Power(x_,j_DEFAULT)),Times(b_DEFAULT,Power(x_,n_DEFAULT))),p_),x_Symbol),
    Condition(Simp(Times(Power(Plus(Times(a,Power(x,j)),Times(b,Power(x,n))),Plus(p,C1)),Power(Times(b,Subtract(n,j),Plus(p,C1),Power(x,Subtract(n,C1))),CN1)),x),And(FreeQ(List(a,b,j,n,p),x),Not(IntegerQ(p)),NeQ(n,j),EqQ(Plus(Times(j,p),Negate(n),j,C1),C0))))
  );
}
