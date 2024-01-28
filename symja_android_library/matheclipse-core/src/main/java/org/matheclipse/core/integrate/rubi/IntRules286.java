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
class IntRules286 { 
  public static IAST RULES = List( 
IIntegrate(5721,Integrate(Times(ArcCot(Plus(c_DEFAULT,Times(Coth(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),d_DEFAULT))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),ArcCot(Plus(c,Times(d,Coth(Plus(a,Times(b,x)))))),Power(Times(f,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Power(Subtract(Subtract(c,d),Times(c,Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(m,C0),EqQ(Sqr(Subtract(c,d)),CN1)))),
IIntegrate(5722,Integrate(Times(ArcTan(Plus(c_DEFAULT,Times(d_DEFAULT,Tanh(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),ArcTan(Plus(c,Times(d,Tanh(Plus(a,Times(b,x)))))),Power(Times(f,Plus(m,C1)),CN1)),x),Simp(Star(Times(CI,b,Subtract(Subtract(CI,c),d),Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Exp(Plus(Times(C2,a),Times(C2,b,x))),Power(Plus(CI,Negate(c),d,Times(Subtract(Subtract(CI,c),d),Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x)),x),Negate(Simp(Star(Times(CI,b,Plus(CI,c,d),Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Exp(Plus(Times(C2,a),Times(C2,b,x))),Power(Plus(CI,c,Negate(d),Times(Plus(CI,c,d),Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x)),x))),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(m,C0),NeQ(Sqr(Subtract(c,d)),CN1)))),
IIntegrate(5723,Integrate(Times(ArcCot(Plus(c_DEFAULT,Times(d_DEFAULT,Tanh(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),ArcCot(Plus(c,Times(d,Tanh(Plus(a,Times(b,x)))))),Power(Times(f,Plus(m,C1)),CN1)),x),Negate(Simp(Star(Times(CI,b,Subtract(Subtract(CI,c),d),Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Exp(Plus(Times(C2,a),Times(C2,b,x))),Power(Plus(CI,Negate(c),d,Times(Subtract(Subtract(CI,c),d),Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x)),x)),Simp(Star(Times(CI,b,Plus(CI,c,d),Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Exp(Plus(Times(C2,a),Times(C2,b,x))),Power(Plus(CI,c,Negate(d),Times(Plus(CI,c,d),Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(m,C0),NeQ(Sqr(Subtract(c,d)),CN1)))),
IIntegrate(5724,Integrate(Times(ArcTan(Plus(c_DEFAULT,Times(Coth(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),d_DEFAULT))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),ArcTan(Plus(c,Times(d,Coth(Plus(a,Times(b,x)))))),Power(Times(f,Plus(m,C1)),CN1)),x),Negate(Simp(Star(Times(CI,b,Subtract(Subtract(CI,c),d),Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Exp(Plus(Times(C2,a),Times(C2,b,x))),Power(Subtract(Plus(CI,Negate(c),d),Times(Subtract(Subtract(CI,c),d),Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x)),x)),Simp(Star(Times(CI,b,Plus(CI,c,d),Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Exp(Plus(Times(C2,a),Times(C2,b,x))),Power(Subtract(Subtract(Plus(CI,c),d),Times(Plus(CI,c,d),Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(m,C0),NeQ(Sqr(Subtract(c,d)),CN1)))),
IIntegrate(5725,Integrate(Times(ArcCot(Plus(c_DEFAULT,Times(Coth(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),d_DEFAULT))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),ArcCot(Plus(c,Times(d,Coth(Plus(a,Times(b,x)))))),Power(Times(f,Plus(m,C1)),CN1)),x),Simp(Star(Times(CI,b,Subtract(Subtract(CI,c),d),Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Exp(Plus(Times(C2,a),Times(C2,b,x))),Power(Subtract(Plus(CI,Negate(c),d),Times(Subtract(Subtract(CI,c),d),Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x)),x),Negate(Simp(Star(Times(CI,b,Plus(CI,c,d),Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Exp(Plus(Times(C2,a),Times(C2,b,x))),Power(Subtract(Subtract(Plus(CI,c),d),Times(Plus(CI,c,d),Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x)),x))),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(m,C0),NeQ(Sqr(Subtract(c,d)),CN1)))),
IIntegrate(5726,Integrate(ArcTan(u_),x_Symbol),
    Condition(Subtract(Simp(Times(x,ArcTan(u)),x),Integrate(SimplifyIntegrand(Times(x,D(u,x),Power(Plus(C1,Sqr(u)),CN1)),x),x)),InverseFunctionFreeQ(u,x))),
IIntegrate(5727,Integrate(ArcCot(u_),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcCot(u)),x),Integrate(SimplifyIntegrand(Times(x,D(u,x),Power(Plus(C1,Sqr(u)),CN1)),x),x)),InverseFunctionFreeQ(u,x))),
IIntegrate(5728,Integrate(Times(Plus(a_DEFAULT,Times(ArcTan(u_),b_DEFAULT)),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Plus(a,Times(b,ArcTan(u))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,Power(Times(d,Plus(m,C1)),CN1)),Integrate(SimplifyIntegrand(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),D(u,x),Power(Plus(C1,Sqr(u)),CN1)),x),x)),x)),And(FreeQ(List(a,b,c,d,m),x),NeQ(m,CN1),InverseFunctionFreeQ(u,x),Not(FunctionOfQ(Power(Plus(c,Times(d,x)),Plus(m,C1)),u,x)),FalseQ(PowerVariableExpn(u,Plus(m,C1),x))))),
IIntegrate(5729,Integrate(Times(Plus(a_DEFAULT,Times(ArcCot(u_),b_DEFAULT)),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Plus(a,Times(b,ArcCot(u))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,Power(Times(d,Plus(m,C1)),CN1)),Integrate(SimplifyIntegrand(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),D(u,x),Power(Plus(C1,Sqr(u)),CN1)),x),x)),x)),And(FreeQ(List(a,b,c,d,m),x),NeQ(m,CN1),InverseFunctionFreeQ(u,x),Not(FunctionOfQ(Power(Plus(c,Times(d,x)),Plus(m,C1)),u,x)),FalseQ(PowerVariableExpn(u,Plus(m,C1),x))))),
IIntegrate(5730,Integrate(Times(Plus(a_DEFAULT,Times(ArcTan(u_),b_DEFAULT)),v_),x_Symbol),
    Condition(With(list(Set(w,IntHide(v,x))),Condition(Subtract(Simp(Star(Plus(a,Times(b,ArcTan(u))),w),x),Simp(Star(b,Integrate(SimplifyIntegrand(Times(w,D(u,x),Power(Plus(C1,Sqr(u)),CN1)),x),x)),x)),InverseFunctionFreeQ(w,x))),And(FreeQ(list(a,b),x),InverseFunctionFreeQ(u,x),Not(MatchQ(v,Condition(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x)),m_DEFAULT),FreeQ(list(c,d,m),x)))),FalseQ(FunctionOfLinear(Times(v,Plus(a,Times(b,ArcTan(u)))),x))))),
IIntegrate(5731,Integrate(Times(Plus(a_DEFAULT,Times(ArcCot(u_),b_DEFAULT)),v_),x_Symbol),
    Condition(With(list(Set(w,IntHide(v,x))),Condition(Plus(Simp(Times(Plus(a,Times(b,ArcCot(u))),w),x),Simp(Star(b,Integrate(SimplifyIntegrand(Times(w,D(u,x),Power(Plus(C1,Sqr(u)),CN1)),x),x)),x)),InverseFunctionFreeQ(w,x))),And(FreeQ(list(a,b),x),InverseFunctionFreeQ(u,x),Not(MatchQ(v,Condition(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x)),m_DEFAULT),FreeQ(list(c,d,m),x)))),FalseQ(FunctionOfLinear(Times(v,Plus(a,Times(b,ArcCot(u)))),x))))),
IIntegrate(5732,Integrate(Times(ArcTan(v_),Log(w_),Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Subtract(Simp(Star(Times(C1D2,CI),Integrate(Times(Log(Subtract(C1,Times(CI,v))),Log(w),Power(Plus(a,Times(b,x)),CN1)),x)),x),Simp(Star(Times(C1D2,CI),Integrate(Times(Log(Plus(C1,Times(CI,v))),Log(w),Power(Plus(a,Times(b,x)),CN1)),x)),x)),And(FreeQ(list(a,b),x),LinearQ(v,x),LinearQ(w,x),EqQ(Simplify(D(Times(v,Power(Plus(a,Times(b,x)),CN1)),x)),C0),EqQ(Simplify(D(Times(w,Power(Plus(a,Times(b,x)),CN1)),x)),C0)))),
IIntegrate(5733,Integrate(Times(ArcTan(v_),Log(w_)),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcTan(v),Log(w)),x),Negate(Integrate(SimplifyIntegrand(Times(x,Log(w),D(v,x),Power(Plus(C1,Sqr(v)),CN1)),x),x)),Negate(Integrate(SimplifyIntegrand(Times(x,ArcTan(v),D(w,x),Power(w,CN1)),x),x))),And(InverseFunctionFreeQ(v,x),InverseFunctionFreeQ(w,x)))),
IIntegrate(5734,Integrate(Times(ArcCot(v_),Log(w_)),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcCot(v),Log(w)),x),Integrate(SimplifyIntegrand(Times(x,Log(w),D(v,x),Power(Plus(C1,Sqr(v)),CN1)),x),x),Negate(Integrate(SimplifyIntegrand(Times(x,ArcCot(v),D(w,x),Power(w,CN1)),x),x))),And(InverseFunctionFreeQ(v,x),InverseFunctionFreeQ(w,x)))),
IIntegrate(5735,Integrate(Times(ArcTan(v_),Log(w_),u_),x_Symbol),
    Condition(With(list(Set(z,IntHide(u,x))),Condition(Plus(Simp(Star(Times(ArcTan(v),Log(w)),z),x),Negate(Integrate(SimplifyIntegrand(Times(z,Log(w),D(v,x),Power(Plus(C1,Sqr(v)),CN1)),x),x)),Negate(Integrate(SimplifyIntegrand(Times(z,ArcTan(v),D(w,x),Power(w,CN1)),x),x))),InverseFunctionFreeQ(z,x))),And(InverseFunctionFreeQ(v,x),InverseFunctionFreeQ(w,x)))),
IIntegrate(5736,Integrate(Times(ArcCot(v_),Log(w_),u_),x_Symbol),
    Condition(With(list(Set(z,IntHide(u,x))),Condition(Plus(Simp(Star(Times(ArcCot(v),Log(w)),z),x),Integrate(SimplifyIntegrand(Times(z,Log(w),D(v,x),Power(Plus(C1,Sqr(v)),CN1)),x),x),Negate(Integrate(SimplifyIntegrand(Times(z,ArcCot(v),D(w,x),Power(w,CN1)),x),x))),InverseFunctionFreeQ(z,x))),And(InverseFunctionFreeQ(v,x),InverseFunctionFreeQ(w,x)))),
IIntegrate(5737,Integrate(ArcSec(Times(c_DEFAULT,x_)),x_Symbol),
    Condition(Subtract(Simp(Times(x,ArcSec(Times(c,x))),x),Simp(Star(Power(c,CN1),Integrate(Power(Times(x,Sqrt(Subtract(C1,Power(Times(Sqr(c),Sqr(x)),CN1)))),CN1),x)),x)),FreeQ(c,x))),
IIntegrate(5738,Integrate(ArcCsc(Times(c_DEFAULT,x_)),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcCsc(Times(c,x))),x),Simp(Star(Power(c,CN1),Integrate(Power(Times(x,Sqrt(Subtract(C1,Power(Times(Sqr(c),Sqr(x)),CN1)))),CN1),x)),x)),FreeQ(c,x))),
IIntegrate(5739,Integrate(Power(Plus(a_DEFAULT,Times(ArcSec(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),x_Symbol),
    Condition(Simp(Star(Power(c,CN1),Subst(Integrate(Times(Power(Plus(a,Times(b,x)),n),Sec(x),Tan(x)),x),x,ArcSec(Times(c,x)))),x),And(FreeQ(List(a,b,c,n),x),IGtQ(n,C0)))),
IIntegrate(5740,Integrate(Power(Plus(a_DEFAULT,Times(ArcCsc(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),x_Symbol),
    Condition(Simp(Star(Negate(Power(c,CN1)),Subst(Integrate(Times(Power(Plus(a,Times(b,x)),n),Csc(x),Cot(x)),x),x,ArcCsc(Times(c,x)))),x),And(FreeQ(List(a,b,c,n),x),IGtQ(n,C0))))
  );
}
