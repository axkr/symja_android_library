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
class IntRules260 { 
  public static IAST RULES = List( 
IIntegrate(5201,Integrate(Times(ArcTan(Plus(c_DEFAULT,Times(Coth(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),d_DEFAULT))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),ArcTan(Plus(c,Times(d,Coth(Plus(a,Times(b,x)))))),Power(Times(f,Plus(m,C1)),CN1)),x),Negate(Dist(Times(CI,b,Subtract(Subtract(CI,c),d),Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Exp(Plus(Times(C2,a),Times(C2,b,x))),Power(Subtract(Plus(CI,Negate(c),d),Times(Subtract(Subtract(CI,c),d),Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x),x)),Dist(Times(CI,b,Plus(CI,c,d),Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Exp(Plus(Times(C2,a),Times(C2,b,x))),Power(Subtract(Subtract(Plus(CI,c),d),Times(Plus(CI,c,d),Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(m,C0),NeQ(Sqr(Subtract(c,d)),CN1)))),
IIntegrate(5202,Integrate(Times(ArcCot(Plus(c_DEFAULT,Times(Coth(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),d_DEFAULT))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),ArcCot(Plus(c,Times(d,Coth(Plus(a,Times(b,x)))))),Power(Times(f,Plus(m,C1)),CN1)),x),Dist(Times(CI,b,Subtract(Subtract(CI,c),d),Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Exp(Plus(Times(C2,a),Times(C2,b,x))),Power(Subtract(Plus(CI,Negate(c),d),Times(Subtract(Subtract(CI,c),d),Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x),x),Negate(Dist(Times(CI,b,Plus(CI,c,d),Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Exp(Plus(Times(C2,a),Times(C2,b,x))),Power(Subtract(Subtract(Plus(CI,c),d),Times(Plus(CI,c,d),Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x),x))),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(m,C0),NeQ(Sqr(Subtract(c,d)),CN1)))),
IIntegrate(5203,Integrate(ArcTan(u_),x_Symbol),
    Condition(Subtract(Simp(Times(x,ArcTan(u)),x),Integrate(SimplifyIntegrand(Times(x,D(u,x),Power(Plus(C1,Sqr(u)),CN1)),x),x)),InverseFunctionFreeQ(u,x))),
IIntegrate(5204,Integrate(ArcCot(u_),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcCot(u)),x),Integrate(SimplifyIntegrand(Times(x,D(u,x),Power(Plus(C1,Sqr(u)),CN1)),x),x)),InverseFunctionFreeQ(u,x))),
IIntegrate(5205,Integrate(Times(Plus(a_DEFAULT,Times(ArcTan(u_),b_DEFAULT)),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Plus(a,Times(b,ArcTan(u))),Power(Times(d,Plus(m,C1)),CN1)),x),Dist(Times(b,Power(Times(d,Plus(m,C1)),CN1)),Integrate(SimplifyIntegrand(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),D(u,x),Power(Plus(C1,Sqr(u)),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,m),x),NeQ(m,CN1),InverseFunctionFreeQ(u,x),Not(FunctionOfQ(Power(Plus(c,Times(d,x)),Plus(m,C1)),u,x)),FalseQ(PowerVariableExpn(u,Plus(m,C1),x))))),
IIntegrate(5206,Integrate(Times(Plus(a_DEFAULT,Times(ArcCot(u_),b_DEFAULT)),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Plus(a,Times(b,ArcCot(u))),Power(Times(d,Plus(m,C1)),CN1)),x),Dist(Times(b,Power(Times(d,Plus(m,C1)),CN1)),Integrate(SimplifyIntegrand(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),D(u,x),Power(Plus(C1,Sqr(u)),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,m),x),NeQ(m,CN1),InverseFunctionFreeQ(u,x),Not(FunctionOfQ(Power(Plus(c,Times(d,x)),Plus(m,C1)),u,x)),FalseQ(PowerVariableExpn(u,Plus(m,C1),x))))),
IIntegrate(5207,Integrate(Times(Plus(a_DEFAULT,Times(ArcTan(u_),b_DEFAULT)),v_),x_Symbol),
    Condition(With(list(Set(w,IntHide(v,x))),Condition(Subtract(Dist(Plus(a,Times(b,ArcTan(u))),w,x),Dist(b,Integrate(SimplifyIntegrand(Times(w,D(u,x),Power(Plus(C1,Sqr(u)),CN1)),x),x),x)),InverseFunctionFreeQ(w,x))),And(FreeQ(list(a,b),x),InverseFunctionFreeQ(u,x),Not(MatchQ(v,Condition(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x)),m_DEFAULT),FreeQ(list(c,d,m),x)))),FalseQ(FunctionOfLinear(Times(v,Plus(a,Times(b,ArcTan(u)))),x))))),
IIntegrate(5208,Integrate(Times(Plus(a_DEFAULT,Times(ArcCot(u_),b_DEFAULT)),v_),x_Symbol),
    Condition(With(list(Set(w,IntHide(v,x))),Condition(Plus(Dist(Plus(a,Times(b,ArcCot(u))),w,x),Dist(b,Integrate(SimplifyIntegrand(Times(w,D(u,x),Power(Plus(C1,Sqr(u)),CN1)),x),x),x)),InverseFunctionFreeQ(w,x))),And(FreeQ(list(a,b),x),InverseFunctionFreeQ(u,x),Not(MatchQ(v,Condition(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x)),m_DEFAULT),FreeQ(list(c,d,m),x)))),FalseQ(FunctionOfLinear(Times(v,Plus(a,Times(b,ArcCot(u)))),x))))),
IIntegrate(5209,Integrate(Times(ArcTan(v_),Log(w_),Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Subtract(Dist(Times(C1D2,CI),Integrate(Times(Log(Subtract(C1,Times(CI,v))),Log(w),Power(Plus(a,Times(b,x)),CN1)),x),x),Dist(Times(C1D2,CI),Integrate(Times(Log(Plus(C1,Times(CI,v))),Log(w),Power(Plus(a,Times(b,x)),CN1)),x),x)),And(FreeQ(list(a,b),x),LinearQ(v,x),LinearQ(w,x),EqQ(Simplify(D(Times(v,Power(Plus(a,Times(b,x)),CN1)),x)),C0),EqQ(Simplify(D(Times(w,Power(Plus(a,Times(b,x)),CN1)),x)),C0)))),
IIntegrate(5210,Integrate(Times(ArcTan(v_),Log(w_)),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcTan(v),Log(w)),x),Negate(Integrate(SimplifyIntegrand(Times(x,Log(w),D(v,x),Power(Plus(C1,Sqr(v)),CN1)),x),x)),Negate(Integrate(SimplifyIntegrand(Times(x,ArcTan(v),D(w,x),Power(w,CN1)),x),x))),And(InverseFunctionFreeQ(v,x),InverseFunctionFreeQ(w,x)))),
IIntegrate(5211,Integrate(Times(ArcCot(v_),Log(w_)),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcCot(v),Log(w)),x),Integrate(SimplifyIntegrand(Times(x,Log(w),D(v,x),Power(Plus(C1,Sqr(v)),CN1)),x),x),Negate(Integrate(SimplifyIntegrand(Times(x,ArcCot(v),D(w,x),Power(w,CN1)),x),x))),And(InverseFunctionFreeQ(v,x),InverseFunctionFreeQ(w,x)))),
IIntegrate(5212,Integrate(Times(ArcTan(v_),Log(w_),u_),x_Symbol),
    Condition(With(list(Set(z,IntHide(u,x))),Condition(Plus(Dist(Times(ArcTan(v),Log(w)),z,x),Negate(Integrate(SimplifyIntegrand(Times(z,Log(w),D(v,x),Power(Plus(C1,Sqr(v)),CN1)),x),x)),Negate(Integrate(SimplifyIntegrand(Times(z,ArcTan(v),D(w,x),Power(w,CN1)),x),x))),InverseFunctionFreeQ(z,x))),And(InverseFunctionFreeQ(v,x),InverseFunctionFreeQ(w,x)))),
IIntegrate(5213,Integrate(Times(ArcCot(v_),Log(w_),u_),x_Symbol),
    Condition(With(list(Set(z,IntHide(u,x))),Condition(Plus(Dist(Times(ArcCot(v),Log(w)),z,x),Integrate(SimplifyIntegrand(Times(z,Log(w),D(v,x),Power(Plus(C1,Sqr(v)),CN1)),x),x),Negate(Integrate(SimplifyIntegrand(Times(z,ArcCot(v),D(w,x),Power(w,CN1)),x),x))),InverseFunctionFreeQ(z,x))),And(InverseFunctionFreeQ(v,x),InverseFunctionFreeQ(w,x)))),
IIntegrate(5214,Integrate(ArcSec(Times(c_DEFAULT,x_)),x_Symbol),
    Condition(Subtract(Simp(Times(x,ArcSec(Times(c,x))),x),Dist(Power(c,CN1),Integrate(Power(Times(x,Sqrt(Subtract(C1,Power(Times(Sqr(c),Sqr(x)),CN1)))),CN1),x),x)),FreeQ(c,x))),
IIntegrate(5215,Integrate(ArcCsc(Times(c_DEFAULT,x_)),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcCsc(Times(c,x))),x),Dist(Power(c,CN1),Integrate(Power(Times(x,Sqrt(Subtract(C1,Power(Times(Sqr(c),Sqr(x)),CN1)))),CN1),x),x)),FreeQ(c,x))),
IIntegrate(5216,Integrate(Power(Plus(a_DEFAULT,Times(ArcSec(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),x_Symbol),
    Condition(Dist(Power(c,CN1),Subst(Integrate(Times(Power(Plus(a,Times(b,x)),n),Sec(x),Tan(x)),x),x,ArcSec(Times(c,x))),x),And(FreeQ(List(a,b,c,n),x),IGtQ(n,C0)))),
IIntegrate(5217,Integrate(Power(Plus(a_DEFAULT,Times(ArcCsc(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),x_Symbol),
    Condition(Negate(Dist(Power(c,CN1),Subst(Integrate(Times(Power(Plus(a,Times(b,x)),n),Csc(x),Cot(x)),x),x,ArcCsc(Times(c,x))),x)),And(FreeQ(List(a,b,c,n),x),IGtQ(n,C0)))),
IIntegrate(5218,Integrate(Times(Plus(a_DEFAULT,Times(ArcSec(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(x_,CN1)),x_Symbol),
    Condition(Negate(Subst(Integrate(Times(Plus(a,Times(b,ArcCos(Times(x,Power(c,CN1))))),Power(x,CN1)),x),x,Power(x,CN1))),FreeQ(list(a,b,c),x))),
IIntegrate(5219,Integrate(Times(Plus(a_DEFAULT,Times(ArcCsc(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(x_,CN1)),x_Symbol),
    Condition(Negate(Subst(Integrate(Times(Plus(a,Times(b,ArcSin(Times(x,Power(c,CN1))))),Power(x,CN1)),x),x,Power(x,CN1))),FreeQ(list(a,b,c),x))),
IIntegrate(5220,Integrate(Times(Plus(a_DEFAULT,Times(ArcSec(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(d,x),Plus(m,C1)),Plus(a,Times(b,ArcSec(Times(c,x)))),Power(Times(d,Plus(m,C1)),CN1)),x),Dist(Times(b,d,Power(Times(c,Plus(m,C1)),CN1)),Integrate(Times(Power(Times(d,x),Subtract(m,C1)),Power(Subtract(C1,Power(Times(Sqr(c),Sqr(x)),CN1)),CN1D2)),x),x)),And(FreeQ(List(a,b,c,d,m),x),NeQ(m,CN1))))
  );
}
