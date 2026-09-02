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
class IntRules211 { 
  public static IAST RULES = List( 
IIntegrate(4221,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),CN1),Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),CN1)),x_Symbol),
    Condition(Plus(Simp(Times(Log(Plus(c,Times(d,x))),Power(Times(C2,a,d),CN1)),x),Simp(Dist(Power(Times(C2,a),CN1),Integrate(Times(Cos(Plus(Times(C2,e),Times(C2,f,x))),Power(Plus(c,Times(d,x)),CN1)),x),x),x),Simp(Dist(Power(Times(C2,b),CN1),Integrate(Times(Sin(Plus(Times(C2,e),Times(C2,f,x))),Power(Plus(c,Times(d,x)),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e,f),x),EqQ(Plus(Sqr(a),Sqr(b)),C0)))),
IIntegrate(4222,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_),Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),CN1)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Power(Times(C2,a,d,Plus(m,C1)),CN1)),x),Simp(Dist(Power(Times(C2,a),CN1),Integrate(Times(Power(Plus(c,Times(d,x)),m),Exp(Times(C2,a,Power(b,CN1),Plus(e,Times(f,x))))),x),x),x)),And(FreeQ(List(a,b,c,d,e,f,m),x),EqQ(Plus(Sqr(a),Sqr(b)),C0),Not(IntegerQ(m))))),
IIntegrate(4223,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_),Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),n_)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Power(Plus(c,Times(d,x)),m),Power(Plus(Power(Times(C2,a),CN1),Times(Cos(Plus(Times(C2,e),Times(C2,f,x))),Power(Times(C2,a),CN1)),Times(Sin(Plus(Times(C2,e),Times(C2,f,x))),Power(Times(C2,b),CN1))),Negate(n)),x),x),And(FreeQ(List(a,b,c,d,e,f),x),EqQ(Plus(Sqr(a),Sqr(b)),C0),ILtQ(m,C0),ILtQ(n,C0)))),
IIntegrate(4224,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_),Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),n_)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Power(Plus(c,Times(d,x)),m),Power(Plus(Power(Times(C2,a),CN1),Times(Exp(Times(C2,a,Power(b,CN1),Plus(e,Times(f,x)))),Power(Times(C2,a),CN1))),Negate(n)),x),x),And(FreeQ(List(a,b,c,d,e,f,m),x),EqQ(Plus(Sqr(a),Sqr(b)),C0),ILtQ(n,C0)))),
IIntegrate(4225,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),n_)),x_Symbol),
    Condition(With(list(Set(u,IntHide(Power(Plus(a,Times(b,Tan(Plus(e,Times(f,x))))),n),x))),Subtract(Simp(Dist(Power(Plus(c,Times(d,x)),m),u,x),x),Simp(Dist(Times(d,m),Integrate(Dist(Power(Plus(c,Times(d,x)),Plus(m,CN1)),u,x),x),x),x))),And(FreeQ(List(a,b,c,d,e,f),x),EqQ(Plus(Sqr(a),Sqr(b)),C0),ILtQ(n,CN1),GtQ(m,C0)))),
IIntegrate(4226,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(Pi,k_DEFAULT),Times(f_DEFAULT,x_))))),CN1)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Power(Times(d,Plus(m,C1),Plus(a,Times(CI,b))),CN1)),x),Simp(Dist(Times(C2,CI,b),Integrate(Times(Power(Plus(c,Times(d,x)),m),Exp(Times(C2,CI,k,Pi)),Exp(Simp(Times(C2,CI,Plus(e,Times(f,x))),x)),Power(Plus(Sqr(Plus(a,Times(CI,b))),Times(Plus(Sqr(a),Sqr(b)),Exp(Times(C2,CI,k,Pi)),Exp(Simp(Times(C2,CI,Plus(e,Times(f,x))),x)))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e,f),x),IntegerQ(Times(C4,k)),NeQ(Plus(Sqr(a),Sqr(b)),C0),IGtQ(m,C0)))),
IIntegrate(4227,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),CN1)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Power(Times(d,Plus(m,C1),Plus(a,Times(CI,b))),CN1)),x),Simp(Dist(Times(C2,CI,b),Integrate(Times(Power(Plus(c,Times(d,x)),m),Exp(Simp(Times(C2,CI,Plus(e,Times(f,x))),x)),Power(Plus(Sqr(Plus(a,Times(CI,b))),Times(Plus(Sqr(a),Sqr(b)),Exp(Simp(Times(C2,CI,Plus(e,Times(f,x))),x)))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,e,f),x),NeQ(Plus(Sqr(a),Sqr(b)),C0),IGtQ(m,C0)))),
IIntegrate(4228,Integrate(Times(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),CN2)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Sqr(Plus(c,Times(d,x))),Power(Times(C2,d,Plus(Sqr(a),Sqr(b))),CN1)),x),Simp(Dist(Power(Times(f,Plus(Sqr(a),Sqr(b))),CN1),Integrate(Times(Plus(Times(b,d),Times(C2,a,c,f),Times(C2,a,d,f,x)),Power(Plus(a,Times(b,Tan(Plus(e,Times(f,x))))),CN1)),x),x),x),Negate(Simp(Times(b,Plus(c,Times(d,x)),Power(Times(f,Plus(Sqr(a),Sqr(b)),Plus(a,Times(b,Tan(Plus(e,Times(f,x)))))),CN1)),x))),And(FreeQ(List(a,b,c,d,e,f),x),NeQ(Plus(Sqr(a),Sqr(b)),C0)))),
IIntegrate(4229,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),n_)),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Power(Plus(c,Times(d,x)),m),Power(Subtract(Power(Subtract(a,Times(CI,b)),CN1),Times(C2,CI,b,Power(Plus(Sqr(a),Sqr(b),Times(Sqr(Subtract(a,Times(CI,b))),Exp(Times(C2,CI,Plus(e,Times(f,x)))))),CN1))),Negate(n)),x),x),And(FreeQ(List(a,b,c,d,e,f),x),NeQ(Plus(Sqr(a),Sqr(b)),C0),ILtQ(n,C0),IGtQ(m,C0)))),
IIntegrate(4230,Integrate(Times(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),Sqrt(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))))),x_Symbol),
    Condition(Plus(Simp(Times(CN1,CSqrt2,b,Plus(c,Times(d,x)),ArcTanh(Times(Sqrt(Plus(a,Times(b,Tan(Plus(e,Times(f,x)))))),Power(Times(CSqrt2,Rt(a,C2)),CN1))),Power(Times(Rt(a,C2),f),CN1)),x),Simp(Dist(Times(CSqrt2,b,d,Power(Times(Rt(a,C2),f),CN1)),Integrate(ArcTanh(Times(Sqrt(Plus(a,Times(b,Tan(Plus(e,Times(f,x)))))),Power(Times(CSqrt2,Rt(a,C2)),CN1))),x),x),x)),And(FreeQ(List(a,b,c,d,e,f),x),EqQ(Plus(Sqr(a),Sqr(b)),C0)))),
IIntegrate(4231,Integrate(Times(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),Sqrt(Plus(a_DEFAULT,Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))))),x_Symbol),
    Condition(Plus(Simp(Times(CNI,Rt(Subtract(a,Times(CI,b)),C2),Plus(c,Times(d,x)),Power(f,CN1),ArcTanh(Times(Sqrt(Plus(a,Times(b,Tan(Plus(e,Times(f,x)))))),Power(Rt(Subtract(a,Times(CI,b)),C2),CN1)))),x),Simp(Times(CI,Rt(Plus(a,Times(CI,b)),C2),Plus(c,Times(d,x)),Power(f,CN1),ArcTanh(Times(Sqrt(Plus(a,Times(b,Tan(Plus(e,Times(f,x)))))),Power(Rt(Plus(a,Times(CI,b)),C2),CN1)))),x),Simp(Dist(Times(CI,d,Rt(Subtract(a,Times(CI,b)),C2),Power(f,CN1)),Integrate(ArcTanh(Times(Sqrt(Plus(a,Times(b,Tan(Plus(e,Times(f,x)))))),Power(Rt(Subtract(a,Times(CI,b)),C2),CN1))),x),x),x),Negate(Simp(Dist(Times(CI,d,Rt(Plus(a,Times(CI,b)),C2),Power(f,CN1)),Integrate(ArcTanh(Times(Sqrt(Plus(a,Times(b,Tan(Plus(e,Times(f,x)))))),Power(Rt(Plus(a,Times(CI,b)),C2),CN1))),x),x),x))),And(FreeQ(List(a,b,c,d,e,f),x),NeQ(Plus(Sqr(a),Sqr(b)),C0)))),
IIntegrate(4232,Integrate(Times(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),CN1D2)),x_Symbol),
    Condition(Plus(Simp(Dist(Power(Times(C2,a),CN1),Integrate(Times(Plus(c,Times(d,x)),Sqrt(Plus(a,Times(b,Tan(Plus(e,Times(f,x))))))),x),x),x),Simp(Dist(Times(C1D2,a),Integrate(Times(Plus(c,Times(d,x)),Sqr(Sec(Plus(e,Times(f,x)))),Power(Plus(a,Times(b,Tan(Plus(e,Times(f,x))))),QQ(-3L,2L))),x),x),x)),And(FreeQ(List(a,b,c,d,e,f),x),EqQ(Plus(Sqr(a),Sqr(b)),C0)))),
IIntegrate(4233,Integrate(Times(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),Power(Plus(a_DEFAULT,Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),CN1D2)),x_Symbol),
    Condition(Plus(Simp(Times(CNI,Plus(c,Times(d,x)),Power(Times(f,Rt(Subtract(a,Times(CI,b)),C2)),CN1),ArcTanh(Times(Sqrt(Plus(a,Times(b,Tan(Plus(e,Times(f,x)))))),Power(Rt(Subtract(a,Times(CI,b)),C2),CN1)))),x),Simp(Times(CI,Plus(c,Times(d,x)),Power(Times(f,Rt(Plus(a,Times(CI,b)),C2)),CN1),ArcTanh(Times(Sqrt(Plus(a,Times(b,Tan(Plus(e,Times(f,x)))))),Power(Rt(Plus(a,Times(CI,b)),C2),CN1)))),x),Simp(Dist(Times(CI,d,Power(Times(f,Rt(Subtract(a,Times(CI,b)),C2)),CN1)),Integrate(ArcTanh(Times(Sqrt(Plus(a,Times(b,Tan(Plus(e,Times(f,x)))))),Power(Rt(Subtract(a,Times(CI,b)),C2),CN1))),x),x),x),Negate(Simp(Dist(Times(CI,d,Power(Times(f,Rt(Plus(a,Times(CI,b)),C2)),CN1)),Integrate(ArcTanh(Times(Sqrt(Plus(a,Times(b,Tan(Plus(e,Times(f,x)))))),Power(Rt(Plus(a,Times(CI,b)),C2),CN1))),x),x),x))),And(FreeQ(List(a,b,c,d,e,f),x),NeQ(Plus(Sqr(a),Sqr(b)),C0)))),
IIntegrate(4234,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power($($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Simp(If(MatchQ(f,Times($p("f1",true),Complex(C0,j_))),If(MatchQ(e,Plus($p("e1",true),CPiHalf)),Times(Power(CI,n),Unintegrable(Times(Power(Plus(c,Times(d,x)),m),Power(Coth(Subtract(Times(CNI,Subtract(e,CPiHalf)),Times(CI,f,x))),n)),x)),Times(Power(CI,n),Unintegrable(Times(Power(Plus(c,Times(d,x)),m),Power(Tanh(Subtract(Times(CNI,e),Times(CI,f,x))),n)),x))),If(MatchQ(e,Plus($p("e1",true),CPiHalf)),Times(Power(-1,n),Unintegrable(Times(Power(Plus(c,Times(d,x)),m),Power(Cot(Plus(e,Times(CN1,C1D2,Pi),Times(f,x))),n)),x)),Unintegrable(Times(Power(Plus(c,Times(d,x)),m),Power(Tan(Plus(e,Times(f,x))),n)),x))),x),And(FreeQ(List(c,d,e,f,m,n),x),IntegerQ(n)))),
IIntegrate(4235,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,$($s("§tan"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))),n_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(c,Times(d,x)),m),Power(Plus(a,Times(b,Tan(Plus(e,Times(f,x))))),n)),x),FreeQ(List(a,b,c,d,e,f,m,n),x))),
IIntegrate(4236,Integrate(Times(Power(u_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Tan(v_))),n_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(ExpandToSum(u,x),m),Power(Plus(a,Times(b,Tan(ExpandToSum(v,x)))),n)),x),And(FreeQ(List(a,b,m,n),x),LinearQ(list(u,v),x),Not(LinearMatchQ(list(u,v),x))))),
IIntegrate(4237,Integrate(Times(Power(Plus(a_DEFAULT,Times(Cot(v_),b_DEFAULT)),n_DEFAULT),Power(u_,m_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(ExpandToSum(u,x),m),Power(Plus(a,Times(b,Cot(ExpandToSum(v,x)))),n)),x),And(FreeQ(List(a,b,m,n),x),LinearQ(list(u,v),x),Not(LinearMatchQ(list(u,v),x))))),
IIntegrate(4238,Integrate(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Tan(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT),x_Symbol),
    Condition(Simp(Dist(Power(n,CN1),Subst(Integrate(Times(Power(x,Plus(Power(n,CN1),CN1)),Power(Plus(a,Times(b,Tan(Plus(c,Times(d,x))))),p)),x),x,Power(x,n)),x),x),And(FreeQ(List(a,b,c,d,p),x),IGtQ(Power(n,CN1),C0),IntegerQ(p)))),
IIntegrate(4239,Integrate(Power(Plus(a_DEFAULT,Times(Cot(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Simp(Dist(Power(n,CN1),Subst(Integrate(Times(Power(x,Plus(Power(n,CN1),CN1)),Power(Plus(a,Times(b,Cot(Plus(c,Times(d,x))))),p)),x),x,Power(x,n)),x),x),And(FreeQ(List(a,b,c,d,p),x),IGtQ(Power(n,CN1),C0),IntegerQ(p)))),
IIntegrate(4240,Integrate(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Tan(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT),x_Symbol),
    Condition(Unintegrable(Power(Plus(a,Times(b,Tan(Plus(c,Times(d,Power(x,n)))))),p),x),FreeQ(List(a,b,c,d,n,p),x)))
  );
}
