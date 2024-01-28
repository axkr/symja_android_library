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
class IntRules234 { 
  public static IAST RULES = List( 
IIntegrate(4681,Integrate(Times(Power(Plus(a_DEFAULT,Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT)),n_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(c,Times(d,x)),m),Power(Plus(a,Times(b,Csc(Plus(e,Times(f,x))))),n)),x),FreeQ(List(a,b,c,d,e,f,m,n),x))),
IIntegrate(4682,Integrate(Times(Power(u_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sec(v_))),n_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(ExpandToSum(u,x),m),Power(Plus(a,Times(b,Sec(ExpandToSum(v,x)))),n)),x),And(FreeQ(List(a,b,m,n),x),LinearQ(list(u,v),x),Not(LinearMatchQ(list(u,v),x))))),
IIntegrate(4683,Integrate(Times(Power(Plus(a_DEFAULT,Times(Csc(v_),b_DEFAULT)),n_DEFAULT),Power(u_,m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(ExpandToSum(u,x),m),Power(Plus(a,Times(b,Csc(ExpandToSum(v,x)))),n)),x),And(FreeQ(List(a,b,m,n),x),LinearQ(list(u,v),x),Not(LinearMatchQ(list(u,v),x))))),
IIntegrate(4684,Integrate(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sec(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT),x_Symbol),
    Condition(Simp(Star(Power(n,CN1),Subst(Integrate(Times(Power(x,Subtract(Power(n,CN1),C1)),Power(Plus(a,Times(b,Sec(Plus(c,Times(d,x))))),p)),x),x,Power(x,n))),x),And(FreeQ(List(a,b,c,d,p),x),IGtQ(Power(n,CN1),C0),IntegerQ(p)))),
IIntegrate(4685,Integrate(Power(Plus(a_DEFAULT,Times(Csc(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Star(Power(n,CN1),Subst(Integrate(Times(Power(x,Subtract(Power(n,CN1),C1)),Power(Plus(a,Times(b,Csc(Plus(c,Times(d,x))))),p)),x),x,Power(x,n))),x),And(FreeQ(List(a,b,c,d,p),x),IGtQ(Power(n,CN1),C0),IntegerQ(p)))),
IIntegrate(4686,Integrate(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sec(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT),x_Symbol),
    Condition(Unintegrable(Power(Plus(a,Times(b,Sec(Plus(c,Times(d,Power(x,n)))))),p),x),FreeQ(List(a,b,c,d,n,p),x))),
IIntegrate(4687,Integrate(Power(Plus(a_DEFAULT,Times(Csc(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Unintegrable(Power(Plus(a,Times(b,Csc(Plus(c,Times(d,Power(x,n)))))),p),x),FreeQ(List(a,b,c,d,n,p),x))),
IIntegrate(4688,Integrate(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sec(Plus(c_DEFAULT,Times(d_DEFAULT,Power(u_,n_)))))),p_DEFAULT),x_Symbol),
    Condition(Simp(Star(Power(Coefficient(u,x,C1),CN1),Subst(Integrate(Power(Plus(a,Times(b,Sec(Plus(c,Times(d,Power(x,n)))))),p),x),x,u)),x),And(FreeQ(List(a,b,c,d,n,p),x),LinearQ(u,x),NeQ(u,x)))),
IIntegrate(4689,Integrate(Power(Plus(a_DEFAULT,Times(Csc(Plus(c_DEFAULT,Times(d_DEFAULT,Power(u_,n_)))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Star(Power(Coefficient(u,x,C1),CN1),Subst(Integrate(Power(Plus(a,Times(b,Csc(Plus(c,Times(d,Power(x,n)))))),p),x),x,u)),x),And(FreeQ(List(a,b,c,d,n,p),x),LinearQ(u,x),NeQ(u,x)))),
IIntegrate(4690,Integrate(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sec(u_))),p_DEFAULT),x_Symbol),
    Condition(Integrate(Power(Plus(a,Times(b,Sec(ExpandToSum(u,x)))),p),x),And(FreeQ(list(a,b,p),x),BinomialQ(u,x),Not(BinomialMatchQ(u,x))))),
IIntegrate(4691,Integrate(Power(Plus(a_DEFAULT,Times(Csc(u_),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Integrate(Power(Plus(a,Times(b,Csc(ExpandToSum(u,x)))),p),x),And(FreeQ(list(a,b,p),x),BinomialQ(u,x),Not(BinomialMatchQ(u,x))))),
IIntegrate(4692,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sec(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(n,CN1),Subst(Integrate(Times(Power(x,Subtract(Simplify(Times(Plus(m,C1),Power(n,CN1))),C1)),Power(Plus(a,Times(b,Sec(Plus(c,Times(d,x))))),p)),x),x,Power(x,n))),x),And(FreeQ(List(a,b,c,d,m,n,p),x),IGtQ(Simplify(Times(Plus(m,C1),Power(n,CN1))),C0),IntegerQ(p)))),
IIntegrate(4693,Integrate(Times(Power(Plus(a_DEFAULT,Times(Csc(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(n,CN1),Subst(Integrate(Times(Power(x,Subtract(Simplify(Times(Plus(m,C1),Power(n,CN1))),C1)),Power(Plus(a,Times(b,Csc(Plus(c,Times(d,x))))),p)),x),x,Power(x,n))),x),And(FreeQ(List(a,b,c,d,m,n,p),x),IGtQ(Simplify(Times(Plus(m,C1),Power(n,CN1))),C0),IntegerQ(p)))),
IIntegrate(4694,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sec(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(x,m),Power(Plus(a,Times(b,Sec(Plus(c,Times(d,Power(x,n)))))),p)),x),FreeQ(List(a,b,c,d,m,n,p),x))),
IIntegrate(4695,Integrate(Times(Power(Plus(a_DEFAULT,Times(Csc(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(x,m),Power(Plus(a,Times(b,Csc(Plus(c,Times(d,Power(x,n)))))),p)),x),FreeQ(List(a,b,c,d,m,n,p),x))),
IIntegrate(4696,Integrate(Times(Power(Times(e_,x_),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sec(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(Power(e,IntPart(m)),Power(Times(e,x),FracPart(m)),Power(Power(x,FracPart(m)),CN1)),Integrate(Times(Power(x,m),Power(Plus(a,Times(b,Sec(Plus(c,Times(d,Power(x,n)))))),p)),x)),x),FreeQ(List(a,b,c,d,e,m,n,p),x))),
IIntegrate(4697,Integrate(Times(Power(Plus(a_DEFAULT,Times(Csc(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(Times(e_,x_),m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(Power(e,IntPart(m)),Power(Times(e,x),FracPart(m)),Power(Power(x,FracPart(m)),CN1)),Integrate(Times(Power(x,m),Power(Plus(a,Times(b,Csc(Plus(c,Times(d,Power(x,n)))))),p)),x)),x),FreeQ(List(a,b,c,d,e,m,n,p),x))),
IIntegrate(4698,Integrate(Times(Power(Times(e_,x_),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sec(u_))),p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Times(e,x),m),Power(Plus(a,Times(b,Sec(ExpandToSum(u,x)))),p)),x),And(FreeQ(List(a,b,e,m,p),x),BinomialQ(u,x),Not(BinomialMatchQ(u,x))))),
IIntegrate(4699,Integrate(Times(Power(Plus(a_DEFAULT,Times(Csc(u_),b_DEFAULT)),p_DEFAULT),Power(Times(e_,x_),m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Times(e,x),m),Power(Plus(a,Times(b,Csc(ExpandToSum(u,x)))),p)),x),And(FreeQ(List(a,b,e,m,p),x),BinomialQ(u,x),Not(BinomialMatchQ(u,x))))),
IIntegrate(4700,Integrate(Times(Power(x_,m_DEFAULT),Power(Sec(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),p_),Sin(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))))),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,Negate(n),C1)),Power(Sec(Plus(a,Times(b,Power(x,n)))),Subtract(p,C1)),Power(Times(b,n,Subtract(p,C1)),CN1)),x),Simp(Star(Times(Plus(m,Negate(n),C1),Power(Times(b,n,Subtract(p,C1)),CN1)),Integrate(Times(Power(x,Subtract(m,n)),Power(Sec(Plus(a,Times(b,Power(x,n)))),Subtract(p,C1))),x)),x)),And(FreeQ(list(a,b,p),x),IntegerQ(n),GeQ(Subtract(m,n),C0),NeQ(p,C1))))
  );
}
