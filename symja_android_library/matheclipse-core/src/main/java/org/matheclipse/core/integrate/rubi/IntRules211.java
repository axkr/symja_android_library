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
class IntRules211 { 
  public static IAST RULES = List( 
IIntegrate(4221,Integrate(Times(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),Power(Plus(a_DEFAULT,Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),CN1D2)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,CI,Plus(c,Times(d,x)),Power(Times(f,Rt(Subtract(a,Times(CI,b)),C2)),CN1),ArcTanh(Times(Sqrt(Plus(a,Times(b,Tan(Plus(e,Times(f,x)))))),Power(Rt(Subtract(a,Times(CI,b)),C2),CN1)))),x),Simp(Times(CI,Plus(c,Times(d,x)),Power(Times(f,Rt(Plus(a,Times(CI,b)),C2)),CN1),ArcTanh(Times(Sqrt(Plus(a,Times(b,Tan(Plus(e,Times(f,x)))))),Power(Rt(Plus(a,Times(CI,b)),C2),CN1)))),x),Simp(Star(Times(CI,d,Power(Times(f,Rt(Subtract(a,Times(CI,b)),C2)),CN1)),Integrate(ArcTanh(Times(Sqrt(Plus(a,Times(b,Tan(Plus(e,Times(f,x)))))),Power(Rt(Subtract(a,Times(CI,b)),C2),CN1))),x)),x),Negate(Simp(Star(Times(CI,d,Power(Times(f,Rt(Plus(a,Times(CI,b)),C2)),CN1)),Integrate(ArcTanh(Times(Sqrt(Plus(a,Times(b,Tan(Plus(e,Times(f,x)))))),Power(Rt(Plus(a,Times(CI,b)),C2),CN1))),x)),x))),And(FreeQ(List(a,b,c,d,e,f),x),NeQ(Plus(Sqr(a),Sqr(b)),C0)))),
IIntegrate(4222,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power($($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Simp(If(MatchQ(f,Times($p("f1",true),Complex(C0,j_))),If(MatchQ(e,Plus($p("e1",true),CPiHalf)),Times(Power(CI,n),Unintegrable(Times(Power(Plus(c,Times(d,x)),m),Power(Coth(Subtract(Times(CN1,CI,Subtract(e,CPiHalf)),Times(CI,f,x))),n)),x)),Times(Power(CI,n),Unintegrable(Times(Power(Plus(c,Times(d,x)),m),Power(Tanh(Subtract(Times(CN1,CI,e),Times(CI,f,x))),n)),x))),If(MatchQ(e,Plus($p("e1",true),CPiHalf)),Times(Power(CN1,n),Unintegrable(Times(Power(Plus(c,Times(d,x)),m),Power(Cot(Plus(e,Times(CN1,C1D2,Pi),Times(f,x))),n)),x)),Unintegrable(Times(Power(Plus(c,Times(d,x)),m),Power(Tan(Plus(e,Times(f,x))),n)),x))),x),And(FreeQ(List(c,d,e,f,m,n),x),IntegerQ(n)))),
IIntegrate(4223,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),n_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(c,Times(d,x)),m),Power(Plus(a,Times(b,Tan(Plus(e,Times(f,x))))),n)),x),FreeQ(List(a,b,c,d,e,f,m,n),x))),
IIntegrate(4224,Integrate(Times(Power(u_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Tan(v_))),n_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(ExpandToSum(u,x),m),Power(Plus(a,Times(b,Tan(ExpandToSum(v,x)))),n)),x),And(FreeQ(List(a,b,m,n),x),LinearQ(list(u,v),x),Not(LinearMatchQ(list(u,v),x))))),
IIntegrate(4225,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cot(v_),b_DEFAULT)),n_DEFAULT),Power(u_,m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(ExpandToSum(u,x),m),Power(Plus(a,Times(b,Cot(ExpandToSum(v,x)))),n)),x),And(FreeQ(List(a,b,m,n),x),LinearQ(list(u,v),x),Not(LinearMatchQ(list(u,v),x))))),
IIntegrate(4226,Integrate(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Tan(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT),x_Symbol),
    Condition(Simp(Star(Power(n,CN1),Subst(Integrate(Times(Power(x,Subtract(Power(n,CN1),C1)),Power(Plus(a,Times(b,Tan(Plus(c,Times(d,x))))),p)),x),x,Power(x,n))),x),And(FreeQ(List(a,b,c,d,p),x),IGtQ(Power(n,CN1),C0),IntegerQ(p)))),
IIntegrate(4227,Integrate(Power(Plus(a_DEFAULT,Times(Cot(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Star(Power(n,CN1),Subst(Integrate(Times(Power(x,Subtract(Power(n,CN1),C1)),Power(Plus(a,Times(b,Cot(Plus(c,Times(d,x))))),p)),x),x,Power(x,n))),x),And(FreeQ(List(a,b,c,d,p),x),IGtQ(Power(n,CN1),C0),IntegerQ(p)))),
IIntegrate(4228,Integrate(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Tan(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT),x_Symbol),
    Condition(Unintegrable(Power(Plus(a,Times(b,Tan(Plus(c,Times(d,Power(x,n)))))),p),x),FreeQ(List(a,b,c,d,n,p),x))),
IIntegrate(4229,Integrate(Power(Plus(a_DEFAULT,Times(Cot(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Unintegrable(Power(Plus(a,Times(b,Cot(Plus(c,Times(d,Power(x,n)))))),p),x),FreeQ(List(a,b,c,d,n,p),x))),
IIntegrate(4230,Integrate(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Tan(Plus(c_DEFAULT,Times(d_DEFAULT,Power(u_,n_)))))),p_DEFAULT),x_Symbol),
    Condition(Simp(Star(Power(Coefficient(u,x,C1),CN1),Subst(Integrate(Power(Plus(a,Times(b,Tan(Plus(c,Times(d,Power(x,n)))))),p),x),x,u)),x),And(FreeQ(List(a,b,c,d,n,p),x),LinearQ(u,x),NeQ(u,x)))),
IIntegrate(4231,Integrate(Power(Plus(a_DEFAULT,Times(Cot(Plus(c_DEFAULT,Times(d_DEFAULT,Power(u_,n_)))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Star(Power(Coefficient(u,x,C1),CN1),Subst(Integrate(Power(Plus(a,Times(b,Cot(Plus(c,Times(d,Power(x,n)))))),p),x),x,u)),x),And(FreeQ(List(a,b,c,d,n,p),x),LinearQ(u,x),NeQ(u,x)))),
IIntegrate(4232,Integrate(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Tan(u_))),p_DEFAULT),x_Symbol),
    Condition(Integrate(Power(Plus(a,Times(b,Tan(ExpandToSum(u,x)))),p),x),And(FreeQ(list(a,b,p),x),BinomialQ(u,x),Not(BinomialMatchQ(u,x))))),
IIntegrate(4233,Integrate(Power(Plus(a_DEFAULT,Times(Cot(u_),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Integrate(Power(Plus(a,Times(b,Cot(ExpandToSum(u,x)))),p),x),And(FreeQ(list(a,b,p),x),BinomialQ(u,x),Not(BinomialMatchQ(u,x))))),
IIntegrate(4234,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Tan(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(n,CN1),Subst(Integrate(Times(Power(x,Subtract(Simplify(Times(Plus(m,C1),Power(n,CN1))),C1)),Power(Plus(a,Times(b,Tan(Plus(c,Times(d,x))))),p)),x),x,Power(x,n))),x),And(FreeQ(List(a,b,c,d,m,n,p),x),IGtQ(Simplify(Times(Plus(m,C1),Power(n,CN1))),C0),IntegerQ(p)))),
IIntegrate(4235,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cot(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Power(n,CN1),Subst(Integrate(Times(Power(x,Subtract(Simplify(Times(Plus(m,C1),Power(n,CN1))),C1)),Power(Plus(a,Times(b,Cot(Plus(c,Times(d,x))))),p)),x),x,Power(x,n))),x),And(FreeQ(List(a,b,c,d,m,n,p),x),IGtQ(Simplify(Times(Plus(m,C1),Power(n,CN1))),C0),IntegerQ(p)))),
IIntegrate(4236,Integrate(Times(Power(x_,m_DEFAULT),Sqr(Tan(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,Negate(n),C1)),Tan(Plus(c,Times(d,Power(x,n)))),Power(Times(d,n),CN1)),x),Negate(Integrate(Power(x,m),x)),Negate(Simp(Star(Times(Plus(m,Negate(n),C1),Power(Times(d,n),CN1)),Integrate(Times(Power(x,Subtract(m,n)),Tan(Plus(c,Times(d,Power(x,n))))),x)),x))),FreeQ(List(c,d,m,n),x))),
IIntegrate(4237,Integrate(Times(Sqr(Cot(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_))))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Power(x,Plus(m,Negate(n),C1)),Cot(Plus(c,Times(d,Power(x,n)))),Power(Times(d,n),CN1)),x),Negate(Integrate(Power(x,m),x)),Simp(Star(Times(Plus(m,Negate(n),C1),Power(Times(d,n),CN1)),Integrate(Times(Power(x,Subtract(m,n)),Cot(Plus(c,Times(d,Power(x,n))))),x)),x)),FreeQ(List(c,d,m,n),x))),
IIntegrate(4238,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Tan(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(x,m),Power(Plus(a,Times(b,Tan(Plus(c,Times(d,Power(x,n)))))),p)),x),FreeQ(List(a,b,c,d,m,n,p),x))),
IIntegrate(4239,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cot(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(x,m),Power(Plus(a,Times(b,Cot(Plus(c,Times(d,Power(x,n)))))),p)),x),FreeQ(List(a,b,c,d,m,n,p),x))),
IIntegrate(4240,Integrate(Times(Power(Times(e_,x_),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Tan(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(Power(e,IntPart(m)),Power(Times(e,x),FracPart(m)),Power(Power(x,FracPart(m)),CN1)),Integrate(Times(Power(x,m),Power(Plus(a,Times(b,Tan(Plus(c,Times(d,Power(x,n)))))),p)),x)),x),FreeQ(List(a,b,c,d,e,m,n,p),x)))
  );
}
