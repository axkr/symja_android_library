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
class IntRules212 { 
  public static IAST RULES = List( 
IIntegrate(4241,Integrate(Power(Plus(a_DEFAULT,Times(Cot(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Unintegrable(Power(Plus(a,Times(b,Cot(Plus(c,Times(d,Power(x,n)))))),p),x),FreeQ(List(a,b,c,d,n,p),x))),
IIntegrate(4242,Integrate(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Tan(Plus(c_DEFAULT,Times(d_DEFAULT,Power(u_,n_)))))),p_DEFAULT),x_Symbol),
    Condition(Simp(Dist(Power(Coefficient(u,x,C1),CN1),Subst(Integrate(Power(Plus(a,Times(b,Tan(Plus(c,Times(d,Power(x,n)))))),p),x),x,u),x),x),And(FreeQ(List(a,b,c,d,n,p),x),LinearQ(u,x),NeQ(u,x)))),
IIntegrate(4243,Integrate(Power(Plus(a_DEFAULT,Times(Cot(Plus(c_DEFAULT,Times(d_DEFAULT,Power(u_,n_)))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Dist(Power(Coefficient(u,x,C1),CN1),Subst(Integrate(Power(Plus(a,Times(b,Cot(Plus(c,Times(d,Power(x,n)))))),p),x),x,u),x),x),And(FreeQ(List(a,b,c,d,n,p),x),LinearQ(u,x),NeQ(u,x)))),
IIntegrate(4244,Integrate(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Tan(u_))),p_DEFAULT),x_Symbol),
    Condition(Integrate(Power(Plus(a,Times(b,Tan(ExpandToSum(u,x)))),p),x),And(FreeQ(list(a,b,p),x),BinomialQ(u,x),Not(BinomialMatchQ(u,x))))),
IIntegrate(4245,Integrate(Power(Plus(a_DEFAULT,Times(Cot(u_),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Integrate(Power(Plus(a,Times(b,Cot(ExpandToSum(u,x)))),p),x),And(FreeQ(list(a,b,p),x),BinomialQ(u,x),Not(BinomialMatchQ(u,x))))),
IIntegrate(4246,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Tan(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(n,CN1),Subst(Integrate(Times(Power(x,Plus(Simplify(Times(Plus(m,C1),Power(n,CN1))),CN1)),Power(Plus(a,Times(b,Tan(Plus(c,Times(d,x))))),p)),x),x,Power(x,n)),x),x),And(FreeQ(List(a,b,c,d,m,n,p),x),IGtQ(Simplify(Times(Plus(m,C1),Power(n,CN1))),C0),IntegerQ(p)))),
IIntegrate(4247,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cot(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(n,CN1),Subst(Integrate(Times(Power(x,Plus(Simplify(Times(Plus(m,C1),Power(n,CN1))),CN1)),Power(Plus(a,Times(b,Cot(Plus(c,Times(d,x))))),p)),x),x,Power(x,n)),x),x),And(FreeQ(List(a,b,c,d,m,n,p),x),IGtQ(Simplify(Times(Plus(m,C1),Power(n,CN1))),C0),IntegerQ(p)))),
IIntegrate(4248,Integrate(Times(Power(x_,m_DEFAULT),Sqr(Tan(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,Negate(n),C1)),Tan(Plus(c,Times(d,Power(x,n)))),Power(Times(d,n),CN1)),x),Negate(Integrate(Power(x,m),x)),Negate(Simp(Dist(Times(Plus(m,Negate(n),C1),Power(Times(d,n),CN1)),Integrate(Times(Power(x,Subtract(m,n)),Tan(Plus(c,Times(d,Power(x,n))))),x),x),x))),FreeQ(List(c,d,m,n),x))),
IIntegrate(4249,Integrate(Times(Sqr(Cot(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_))))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Power(x,Plus(m,Negate(n),C1)),Cot(Plus(c,Times(d,Power(x,n)))),Power(Times(d,n),CN1)),x),Negate(Integrate(Power(x,m),x)),Simp(Dist(Times(Plus(m,Negate(n),C1),Power(Times(d,n),CN1)),Integrate(Times(Power(x,Subtract(m,n)),Cot(Plus(c,Times(d,Power(x,n))))),x),x),x)),FreeQ(List(c,d,m,n),x))),
IIntegrate(4250,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Tan(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(x,m),Power(Plus(a,Times(b,Tan(Plus(c,Times(d,Power(x,n)))))),p)),x),FreeQ(List(a,b,c,d,m,n,p),x))),
IIntegrate(4251,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cot(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(x,m),Power(Plus(a,Times(b,Cot(Plus(c,Times(d,Power(x,n)))))),p)),x),FreeQ(List(a,b,c,d,m,n,p),x))),
IIntegrate(4252,Integrate(Times(Power(Times(e_,x_),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Tan(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Times(Power(e,IntPart(m)),Power(Times(e,x),FracPart(m)),Power(Power(x,FracPart(m)),CN1)),Integrate(Times(Power(x,m),Power(Plus(a,Times(b,Tan(Plus(c,Times(d,Power(x,n)))))),p)),x),x),x),FreeQ(List(a,b,c,d,e,m,n,p),x))),
IIntegrate(4253,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cot(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(Times(e_,x_),m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Times(Power(e,IntPart(m)),Power(Times(e,x),FracPart(m)),Power(Power(x,FracPart(m)),CN1)),Integrate(Times(Power(x,m),Power(Plus(a,Times(b,Cot(Plus(c,Times(d,Power(x,n)))))),p)),x),x),x),FreeQ(List(a,b,c,d,e,m,n,p),x))),
IIntegrate(4254,Integrate(Times(Power(Times(e_,x_),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Tan(u_))),p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Times(e,x),m),Power(Plus(a,Times(b,Tan(ExpandToSum(u,x)))),p)),x),And(FreeQ(List(a,b,e,m,p),x),BinomialQ(u,x),Not(BinomialMatchQ(u,x))))),
IIntegrate(4255,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cot(u_),b_DEFAULT)),p_DEFAULT),Power(Times(e_,x_),m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Times(e,x),m),Power(Plus(a,Times(b,Cot(ExpandToSum(u,x)))),p)),x),And(FreeQ(List(a,b,e,m,p),x),BinomialQ(u,x),Not(BinomialMatchQ(u,x))))),
IIntegrate(4256,Integrate(Times(Power(x_,m_DEFAULT),Power(Sec(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT),Power(Tan(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),q_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,Negate(n),C1)),Power(Sec(Plus(a,Times(b,Power(x,n)))),p),Power(Times(b,n,p),CN1)),x),Simp(Dist(Times(Plus(m,Negate(n),C1),Power(Times(b,n,p),CN1)),Integrate(Times(Power(x,Subtract(m,n)),Power(Sec(Plus(a,Times(b,Power(x,n)))),p)),x),x),x)),And(FreeQ(list(a,b,p),x),IntegerQ(n),GeQ(m,n),EqQ(q,C1)))),
IIntegrate(4257,Integrate(Times(Power(Cot(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),q_DEFAULT),Power(Csc(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Power(x,Plus(m,Negate(n),C1)),Power(Csc(Plus(a,Times(b,Power(x,n)))),p),Power(Times(b,n,p),CN1)),x),Simp(Dist(Times(Plus(m,Negate(n),C1),Power(Times(b,n,p),CN1)),Integrate(Times(Power(x,Subtract(m,n)),Power(Csc(Plus(a,Times(b,Power(x,n)))),p)),x),x),x)),And(FreeQ(list(a,b,p),x),IntegerQ(n),GeQ(m,n),EqQ(q,C1)))),
IIntegrate(4258,Integrate(Power(Tan(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_DEFAULT),x_Symbol),
    Condition(Unintegrable(Power(Tan(Plus(a,Times(b,x),Times(c,Sqr(x)))),n),x),FreeQ(List(a,b,c,n),x))),
IIntegrate(4259,Integrate(Power(Cot(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_DEFAULT),x_Symbol),
    Condition(Unintegrable(Power(Cot(Plus(a,Times(b,x),Times(c,Sqr(x)))),n),x),FreeQ(List(a,b,c,n),x))),
IIntegrate(4260,Integrate(Times(Plus(d_,Times(e_DEFAULT,x_)),Tan(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Simp(Times(CN1,e,Log(Cos(Plus(a,Times(b,x),Times(c,Sqr(x))))),Power(Times(C2,c),CN1)),x),And(FreeQ(List(a,b,c,d,e),x),EqQ(Subtract(Times(C2,c,d),Times(b,e)),C0))))
  );
}
