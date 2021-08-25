package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;
import org.matheclipse.core.interfaces.IAST;

/** 
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class IntRules186 { 
  public static IAST RULES = List( 
IIntegrate(3721,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),n_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(c,Times(d,x)),m),Power(Times(b,Tan(Plus(e,Times(f,x)))),Plus(n,C1)),Power(Times(b,f,Plus(n,C1)),CN1)),x),Negate(Dist(Times(d,m,Power(Times(b,f,Plus(n,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Subtract(m,C1)),Power(Times(b,Tan(Plus(e,Times(f,x)))),Plus(n,C1))),x),x)),Negate(Dist(Power(b,CN2),Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(Times(b,Tan(Plus(e,Times(f,x)))),Plus(n,C2))),x),x))),And(FreeQ(List(b,c,d,e,f),x),LtQ(n,CN1),GtQ(m,C0)))),
IIntegrate(3722,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),n_DEFAULT)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Power(Plus(c,Times(d,x)),m),Power(Plus(a,Times(b,Tan(Plus(e,Times(f,x))))),n),x),x),And(FreeQ(List(a,b,c,d,e,f,m),x),IGtQ(m,C0),IGtQ(n,C0)))),
IIntegrate(3723,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),CN1)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Power(Times(C2,a,d,Plus(m,C1)),CN1)),x),Dist(Times(a,d,m,Power(Times(C2,b,f),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Subtract(m,C1)),Power(Plus(a,Times(b,Tan(Plus(e,Times(f,x))))),CN1)),x),x),Negate(Simp(Times(a,Power(Plus(c,Times(d,x)),m),Power(Times(C2,b,f,Plus(a,Times(b,Tan(Plus(e,Times(f,x)))))),CN1)),x))),And(FreeQ(List(a,b,c,d,e,f),x),EqQ(Plus(Sqr(a),Sqr(b)),C0),GtQ(m,C0)))),
IIntegrate(3724,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),CN2),Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),CN1)),x_Symbol),
    Condition(Plus(Negate(Simp(Power(Times(d,Plus(c,Times(d,x)),Plus(a,Times(b,Tan(Plus(e,Times(f,x)))))),CN1),x)),Negate(Dist(Times(f,Power(Times(a,d),CN1)),Integrate(Times(Sin(Plus(Times(C2,e),Times(C2,f,x))),Power(Plus(c,Times(d,x)),CN1)),x),x)),Dist(Times(f,Power(Times(b,d),CN1)),Integrate(Times(Cos(Plus(Times(C2,e),Times(C2,f,x))),Power(Plus(c,Times(d,x)),CN1)),x),x)),And(FreeQ(List(a,b,c,d,e,f),x),EqQ(Plus(Sqr(a),Sqr(b)),C0)))),
IIntegrate(3725,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_),Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),CN1)),x_Symbol),
    Condition(Plus(Simp(Times(f,Power(Plus(c,Times(d,x)),Plus(m,C2)),Power(Times(b,Sqr(d),Plus(m,C1),Plus(m,C2)),CN1)),x),Dist(Times(C2,b,f,Power(Times(a,d,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Power(Plus(a,Times(b,Tan(Plus(e,Times(f,x))))),CN1)),x),x),Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Power(Times(d,Plus(m,C1),Plus(a,Times(b,Tan(Plus(e,Times(f,x)))))),CN1)),x)),And(FreeQ(List(a,b,c,d,e,f),x),EqQ(Plus(Sqr(a),Sqr(b)),C0),LtQ(m,CN1),NeQ(m,CN2)))),
IIntegrate(3726,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),CN1),Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),CN1)),x_Symbol),
    Condition(Plus(Simp(Times(Log(Plus(c,Times(d,x))),Power(Times(C2,a,d),CN1)),x),Dist(Power(Times(C2,a),CN1),Integrate(Times(Cos(Plus(Times(C2,e),Times(C2,f,x))),Power(Plus(c,Times(d,x)),CN1)),x),x),Dist(Power(Times(C2,b),CN1),Integrate(Times(Sin(Plus(Times(C2,e),Times(C2,f,x))),Power(Plus(c,Times(d,x)),CN1)),x),x)),And(FreeQ(List(a,b,c,d,e,f),x),EqQ(Plus(Sqr(a),Sqr(b)),C0)))),
IIntegrate(3727,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_),Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),CN1)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Power(Times(C2,a,d,Plus(m,C1)),CN1)),x),Dist(Power(Times(C2,a),CN1),Integrate(Times(Power(Plus(c,Times(d,x)),m),Exp(Times(C2,a,Plus(e,Times(f,x)),Power(b,CN1)))),x),x)),And(FreeQ(List(a,b,c,d,e,f,m),x),EqQ(Plus(Sqr(a),Sqr(b)),C0),Not(IntegerQ(m))))),
IIntegrate(3728,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_),Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),n_)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Power(Plus(c,Times(d,x)),m),Power(Plus(Power(Times(C2,a),CN1),Times(Cos(Plus(Times(C2,e),Times(C2,f,x))),Power(Times(C2,a),CN1)),Times(Sin(Plus(Times(C2,e),Times(C2,f,x))),Power(Times(C2,b),CN1))),Negate(n)),x),x),And(FreeQ(List(a,b,c,d,e,f),x),EqQ(Plus(Sqr(a),Sqr(b)),C0),ILtQ(m,C0),ILtQ(n,C0)))),
IIntegrate(3729,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_),Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),n_)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Power(Plus(c,Times(d,x)),m),Power(Plus(Power(Times(C2,a),CN1),Times(Exp(Times(C2,a,Plus(e,Times(f,x)),Power(b,CN1))),Power(Times(C2,a),CN1))),Negate(n)),x),x),And(FreeQ(List(a,b,c,d,e,f,m),x),EqQ(Plus(Sqr(a),Sqr(b)),C0),ILtQ(n,C0)))),
IIntegrate(3730,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),n_)),x_Symbol),
    Condition(With(List(Set(u,IntHide(Power(Plus(a,Times(b,Tan(Plus(e,Times(f,x))))),n),x))),Subtract(Dist(Power(Plus(c,Times(d,x)),m),u,x),Dist(Times(d,m),Integrate(Dist(Power(Plus(c,Times(d,x)),Subtract(m,C1)),u,x),x),x))),And(FreeQ(List(a,b,c,d,e,f),x),EqQ(Plus(Sqr(a),Sqr(b)),C0),ILtQ(n,CN1),GtQ(m,C0)))),
IIntegrate(3731,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(Pi,k_DEFAULT),Times(f_DEFAULT,x_))))),CN1)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Power(Times(d,Plus(m,C1),Plus(a,Times(CI,b))),CN1)),x),Dist(Times(C2,CI,b),Integrate(Times(Power(Plus(c,Times(d,x)),m),Exp(Times(C2,CI,k,Pi)),Exp(Simp(Times(C2,CI,Plus(e,Times(f,x))),x)),Power(Plus(Sqr(Plus(a,Times(CI,b))),Times(Plus(Sqr(a),Sqr(b)),Exp(Times(C2,CI,k,Pi)),Exp(Simp(Times(C2,CI,Plus(e,Times(f,x))),x)))),CN1)),x),x)),And(FreeQ(List(a,b,c,d,e,f),x),IntegerQ(Times(C4,k)),NeQ(Plus(Sqr(a),Sqr(b)),C0),IGtQ(m,C0)))),
IIntegrate(3732,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),CN1)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Power(Times(d,Plus(m,C1),Plus(a,Times(CI,b))),CN1)),x),Dist(Times(C2,CI,b),Integrate(Times(Power(Plus(c,Times(d,x)),m),Exp(Simp(Times(C2,CI,Plus(e,Times(f,x))),x)),Power(Plus(Sqr(Plus(a,Times(CI,b))),Times(Plus(Sqr(a),Sqr(b)),Exp(Simp(Times(C2,CI,Plus(e,Times(f,x))),x)))),CN1)),x),x)),And(FreeQ(List(a,b,c,d,e,f),x),NeQ(Plus(Sqr(a),Sqr(b)),C0),IGtQ(m,C0)))),
IIntegrate(3733,Integrate(Times(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),CN2)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(Sqr(Plus(c,Times(d,x))),Power(Times(C2,d,Plus(Sqr(a),Sqr(b))),CN1)),x)),Dist(Power(Times(f,Plus(Sqr(a),Sqr(b))),CN1),Integrate(Times(Plus(Times(b,d),Times(C2,a,c,f),Times(C2,a,d,f,x)),Power(Plus(a,Times(b,Tan(Plus(e,Times(f,x))))),CN1)),x),x),Negate(Simp(Times(b,Plus(c,Times(d,x)),Power(Times(f,Plus(Sqr(a),Sqr(b)),Plus(a,Times(b,Tan(Plus(e,Times(f,x)))))),CN1)),x))),And(FreeQ(List(a,b,c,d,e,f),x),NeQ(Plus(Sqr(a),Sqr(b)),C0)))),
IIntegrate(3734,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),n_)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Power(Plus(c,Times(d,x)),m),Power(Subtract(Power(Subtract(a,Times(CI,b)),CN1),Times(C2,CI,b,Power(Plus(Sqr(a),Sqr(b),Times(Sqr(Subtract(a,Times(CI,b))),Exp(Times(C2,CI,Plus(e,Times(f,x)))))),CN1))),Negate(n)),x),x),And(FreeQ(List(a,b,c,d,e,f),x),NeQ(Plus(Sqr(a),Sqr(b)),C0),ILtQ(n,C0),IGtQ(m,C0)))),
IIntegrate(3735,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power($($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Simp(If(MatchQ(f,Times($p("f1",true),Complex(C0,j_))),If(MatchQ(e,Plus($p("e1",true),CPiHalf)),Times(Power(CI,n),Unintegrable(Times(Power(Plus(c,Times(d,x)),m),Power(Coth(Subtract(Times(CN1,CI,Subtract(e,CPiHalf)),Times(CI,f,x))),n)),x)),Times(Power(CI,n),Unintegrable(Times(Power(Plus(c,Times(d,x)),m),Power(Tanh(Subtract(Times(CN1,CI,e),Times(CI,f,x))),n)),x))),If(MatchQ(e,Plus($p("e1",true),CPiHalf)),Times(Power(CN1,n),Unintegrable(Times(Power(Plus(c,Times(d,x)),m),Power(Cot(Plus(e,Times(CN1,C1D2,Pi),Times(f,x))),n)),x)),Unintegrable(Times(Power(Plus(c,Times(d,x)),m),Power(Tan(Plus(e,Times(f,x))),n)),x))),x),And(FreeQ(List(c,d,e,f,m,n),x),IntegerQ(n)))),
IIntegrate(3736,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),n_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(c,Times(d,x)),m),Power(Plus(a,Times(b,Tan(Plus(e,Times(f,x))))),n)),x),FreeQ(List(a,b,c,d,e,f,m,n),x))),
IIntegrate(3737,Integrate(Times(Power(u_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Tan(v_))),n_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(ExpandToSum(u,x),m),Power(Plus(a,Times(b,Tan(ExpandToSum(v,x)))),n)),x),And(FreeQ(List(a,b,m,n),x),LinearQ(List(u,v),x),Not(LinearMatchQ(List(u,v),x))))),
IIntegrate(3738,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cot(v_),b_DEFAULT)),n_DEFAULT),Power(u_,m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(ExpandToSum(u,x),m),Power(Plus(a,Times(b,Cot(ExpandToSum(v,x)))),n)),x),And(FreeQ(List(a,b,m,n),x),LinearQ(List(u,v),x),Not(LinearMatchQ(List(u,v),x))))),
IIntegrate(3739,Integrate(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Tan(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT),x_Symbol),
    Condition(Dist(Power(n,CN1),Subst(Integrate(Times(Power(x,Subtract(Power(n,CN1),C1)),Power(Plus(a,Times(b,Tan(Plus(c,Times(d,x))))),p)),x),x,Power(x,n)),x),And(FreeQ(List(a,b,c,d,p),x),IGtQ(Power(n,CN1),C0),IntegerQ(p)))),
IIntegrate(3740,Integrate(Power(Plus(a_DEFAULT,Times(Cot(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Dist(Power(n,CN1),Subst(Integrate(Times(Power(x,Subtract(Power(n,CN1),C1)),Power(Plus(a,Times(b,Cot(Plus(c,Times(d,x))))),p)),x),x,Power(x,n)),x),And(FreeQ(List(a,b,c,d,p),x),IGtQ(Power(n,CN1),C0),IntegerQ(p))))
  );
}
