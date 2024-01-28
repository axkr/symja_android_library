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
class IntRules212 { 
  public static IAST RULES = List( 
IIntegrate(4241,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cot(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(Times(e_,x_),m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(Power(e,IntPart(m)),Power(Times(e,x),FracPart(m)),Power(Power(x,FracPart(m)),CN1)),Integrate(Times(Power(x,m),Power(Plus(a,Times(b,Cot(Plus(c,Times(d,Power(x,n)))))),p)),x)),x),FreeQ(List(a,b,c,d,e,m,n,p),x))),
IIntegrate(4242,Integrate(Times(Power(Times(e_,x_),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Tan(u_))),p_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Times(e,x),m),Power(Plus(a,Times(b,Tan(ExpandToSum(u,x)))),p)),x),And(FreeQ(List(a,b,e,m,p),x),BinomialQ(u,x),Not(BinomialMatchQ(u,x))))),
IIntegrate(4243,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cot(u_),b_DEFAULT)),p_DEFAULT),Power(Times(e_,x_),m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Times(e,x),m),Power(Plus(a,Times(b,Cot(ExpandToSum(u,x)))),p)),x),And(FreeQ(List(a,b,e,m,p),x),BinomialQ(u,x),Not(BinomialMatchQ(u,x))))),
IIntegrate(4244,Integrate(Times(Power(x_,m_DEFAULT),Power(Sec(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT),Power(Tan(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),q_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,Negate(n),C1)),Power(Sec(Plus(a,Times(b,Power(x,n)))),p),Power(Times(b,n,p),CN1)),x),Simp(Star(Times(Plus(m,Negate(n),C1),Power(Times(b,n,p),CN1)),Integrate(Times(Power(x,Subtract(m,n)),Power(Sec(Plus(a,Times(b,Power(x,n)))),p)),x)),x)),And(FreeQ(list(a,b,p),x),IntegerQ(n),GeQ(m,n),EqQ(q,C1)))),
IIntegrate(4245,Integrate(Times(Power(Cot(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),q_DEFAULT),Power(Csc(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Power(x,Plus(m,Negate(n),C1)),Power(Csc(Plus(a,Times(b,Power(x,n)))),p),Power(Times(b,n,p),CN1)),x),Simp(Star(Times(Plus(m,Negate(n),C1),Power(Times(b,n,p),CN1)),Integrate(Times(Power(x,Subtract(m,n)),Power(Csc(Plus(a,Times(b,Power(x,n)))),p)),x)),x)),And(FreeQ(list(a,b,p),x),IntegerQ(n),GeQ(m,n),EqQ(q,C1)))),
IIntegrate(4246,Integrate(Power(Tan(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_DEFAULT),x_Symbol),
    Condition(Unintegrable(Power(Tan(Plus(a,Times(b,x),Times(c,Sqr(x)))),n),x),FreeQ(List(a,b,c,n),x))),
IIntegrate(4247,Integrate(Power(Cot(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_DEFAULT),x_Symbol),
    Condition(Unintegrable(Power(Cot(Plus(a,Times(b,x),Times(c,Sqr(x)))),n),x),FreeQ(List(a,b,c,n),x))),
IIntegrate(4248,Integrate(Times(Plus(d_,Times(e_DEFAULT,x_)),Tan(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Simp(Times(CN1,e,Log(Cos(Plus(a,Times(b,x),Times(c,Sqr(x))))),Power(Times(C2,c),CN1)),x),And(FreeQ(List(a,b,c,d,e),x),EqQ(Subtract(Times(C2,c,d),Times(b,e)),C0)))),
IIntegrate(4249,Integrate(Times(Cot(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),Plus(d_,Times(e_DEFAULT,x_))),x_Symbol),
    Condition(Simp(Times(e,Log(Sin(Plus(a,Times(b,x),Times(c,Sqr(x))))),Power(Times(C2,c),CN1)),x),And(FreeQ(List(a,b,c,d,e),x),EqQ(Subtract(Times(C2,c,d),Times(b,e)),C0)))),
IIntegrate(4250,Integrate(Times(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),Tan(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Simp(Times(CN1,e,Log(Cos(Plus(a,Times(b,x),Times(c,Sqr(x))))),Power(Times(C2,c),CN1)),x),Simp(Star(Times(Subtract(Times(C2,c,d),Times(b,e)),Power(Times(C2,c),CN1)),Integrate(Tan(Plus(a,Times(b,x),Times(c,Sqr(x)))),x)),x)),And(FreeQ(List(a,b,c,d,e),x),NeQ(Subtract(Times(C2,c,d),Times(b,e)),C0)))),
IIntegrate(4251,Integrate(Times(Cot(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),Plus(d_DEFAULT,Times(e_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Simp(Times(e,Log(Sin(Plus(a,Times(b,x),Times(c,Sqr(x))))),Power(Times(C2,c),CN1)),x),Simp(Star(Times(Subtract(Times(C2,c,d),Times(b,e)),Power(Times(C2,c),CN1)),Integrate(Cot(Plus(a,Times(b,x),Times(c,Sqr(x)))),x)),x)),And(FreeQ(List(a,b,c,d,e),x),NeQ(Subtract(Times(C2,c,d),Times(b,e)),C0)))),
IIntegrate(4252,Integrate(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT),Power(Tan(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(d,Times(e,x)),m),Power(Tan(Plus(a,Times(b,x),Times(c,Sqr(x)))),n)),x),FreeQ(List(a,b,c,d,e,m,n),x))),
IIntegrate(4253,Integrate(Times(Power(Cot(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(d,Times(e,x)),m),Power(Cot(Plus(a,Times(b,x),Times(c,Sqr(x)))),n)),x),FreeQ(List(a,b,c,d,e,m,n),x))),
IIntegrate(4254,Integrate(Power($($s("§csc"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),n_),x_Symbol),
    Condition(Simp(Star(Negate(Power(d,CN1)),Subst(Integrate(ExpandIntegrand(Power(Plus(C1,Sqr(x)),Subtract(Times(C1D2,n),C1)),x),x),x,Cot(Plus(c,Times(d,x))))),x),And(FreeQ(list(c,d),x),IGtQ(Times(C1D2,n),C0)))),
IIntegrate(4255,Integrate(Power(Times($($s("§csc"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT),n_),x_Symbol),
    Condition(Plus(Simp(Times(CN1,b,Cos(Plus(c,Times(d,x))),Power(Times(b,Csc(Plus(c,Times(d,x)))),Subtract(n,C1)),Power(Times(d,Subtract(n,C1)),CN1)),x),Simp(Star(Times(Sqr(b),Subtract(n,C2),Power(Subtract(n,C1),CN1)),Integrate(Power(Times(b,Csc(Plus(c,Times(d,x)))),Subtract(n,C2)),x)),x)),And(FreeQ(list(b,c,d),x),GtQ(n,C1),IntegerQ(Times(C2,n))))),
IIntegrate(4256,Integrate(Power(Times($($s("§csc"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT),n_),x_Symbol),
    Condition(Plus(Simp(Times(Cos(Plus(c,Times(d,x))),Power(Times(b,Csc(Plus(c,Times(d,x)))),Plus(n,C1)),Power(Times(b,d,n),CN1)),x),Simp(Star(Times(Plus(n,C1),Power(Times(Sqr(b),n),CN1)),Integrate(Power(Times(b,Csc(Plus(c,Times(d,x)))),Plus(n,C2)),x)),x)),And(FreeQ(list(b,c,d),x),LtQ(n,CN1),IntegerQ(Times(C2,n))))),
IIntegrate(4257,Integrate($($s("§csc"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),x_Symbol),
    Condition(Simp(Times(CN1,ArcTanh(Cos(Plus(c,Times(d,x)))),Power(d,CN1)),x),FreeQ(list(c,d),x))),
IIntegrate(4258,Integrate(Power(Times($($s("§csc"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT),n_),x_Symbol),
    Condition(Simp(Star(Times(Power(Times(b,Csc(Plus(c,Times(d,x)))),n),Power(Sin(Plus(c,Times(d,x))),n)),Integrate(Power(Power(Sin(Plus(c,Times(d,x))),n),CN1),x)),x),And(FreeQ(list(b,c,d),x),EqQ(Sqr(n),C1D4)))),
IIntegrate(4259,Integrate(Power(Times($($s("§csc"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT),n_),x_Symbol),
    Condition(Simp(Times(Power(Times(b,Csc(Plus(c,Times(d,x)))),Subtract(n,C1)),Star(Power(Times(Sin(Plus(c,Times(d,x))),Power(b,CN1)),Subtract(n,C1)),Integrate(Power(Power(Times(Sin(Plus(c,Times(d,x))),Power(b,CN1)),n),CN1),x))),x),And(FreeQ(List(b,c,d,n),x),Not(IntegerQ(n))))),
IIntegrate(4260,Integrate(Sqr(Plus(Times($($s("§csc"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT),a_)),x_Symbol),
    Condition(Plus(Simp(Times(Sqr(a),x),x),Simp(Star(Times(C2,a,b),Integrate(Csc(Plus(c,Times(d,x))),x)),x),Simp(Star(Sqr(b),Integrate(Sqr(Csc(Plus(c,Times(d,x)))),x)),x)),FreeQ(List(a,b,c,d),x)))
  );
}
