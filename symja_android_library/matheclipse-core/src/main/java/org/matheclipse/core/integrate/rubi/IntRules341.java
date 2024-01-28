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
class IntRules341 { 
  public static IAST RULES = List( 
IIntegrate(6821,Integrate(Times(ArcTanh(Plus(c_DEFAULT,Times(d_DEFAULT,Tan(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),ArcTanh(Plus(c,Times(d,Tan(Plus(a,Times(b,x)))))),Power(Times(f,Plus(m,C1)),CN1)),x),Negate(Simp(Star(Times(CI,b,Subtract(Plus(C1,c),Times(CI,d)),Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Exp(Plus(Times(C2,CI,a),Times(C2,CI,b,x))),Power(Plus(C1,c,Times(CI,d),Times(Subtract(Plus(C1,c),Times(CI,d)),Exp(Plus(Times(C2,CI,a),Times(C2,CI,b,x))))),CN1)),x)),x)),Simp(Star(Times(CI,b,Plus(C1,Negate(c),Times(CI,d)),Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Exp(Plus(Times(C2,CI,a),Times(C2,CI,b,x))),Power(Plus(C1,Negate(c),Times(CN1,CI,d),Times(Plus(C1,Negate(c),Times(CI,d)),Exp(Plus(Times(C2,CI,a),Times(C2,CI,b,x))))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(m,C0),NeQ(Sqr(Plus(c,Times(CI,d))),C1)))),
IIntegrate(6822,Integrate(Times(ArcCoth(Plus(c_DEFAULT,Times(d_DEFAULT,Tan(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),ArcCoth(Plus(c,Times(d,Tan(Plus(a,Times(b,x)))))),Power(Times(f,Plus(m,C1)),CN1)),x),Negate(Simp(Star(Times(CI,b,Subtract(Plus(C1,c),Times(CI,d)),Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Exp(Plus(Times(C2,CI,a),Times(C2,CI,b,x))),Power(Plus(C1,c,Times(CI,d),Times(Subtract(Plus(C1,c),Times(CI,d)),Exp(Plus(Times(C2,CI,a),Times(C2,CI,b,x))))),CN1)),x)),x)),Simp(Star(Times(CI,b,Plus(C1,Negate(c),Times(CI,d)),Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Exp(Plus(Times(C2,CI,a),Times(C2,CI,b,x))),Power(Plus(C1,Negate(c),Times(CN1,CI,d),Times(Plus(C1,Negate(c),Times(CI,d)),Exp(Plus(Times(C2,CI,a),Times(C2,CI,b,x))))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(m,C0),NeQ(Sqr(Plus(c,Times(CI,d))),C1)))),
IIntegrate(6823,Integrate(Times(ArcTanh(Plus(c_DEFAULT,Times(Cot(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),d_DEFAULT))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),ArcTanh(Plus(c,Times(d,Cot(Plus(a,Times(b,x)))))),Power(Times(f,Plus(m,C1)),CN1)),x),Negate(Simp(Star(Times(CI,b,Subtract(Subtract(C1,c),Times(CI,d)),Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Exp(Plus(Times(C2,CI,a),Times(C2,CI,b,x))),Power(Subtract(Plus(C1,Negate(c),Times(CI,d)),Times(Subtract(Subtract(C1,c),Times(CI,d)),Exp(Plus(Times(C2,CI,a),Times(C2,CI,b,x))))),CN1)),x)),x)),Simp(Star(Times(CI,b,Plus(C1,c,Times(CI,d)),Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Exp(Plus(Times(C2,CI,a),Times(C2,CI,b,x))),Power(Subtract(Subtract(Plus(C1,c),Times(CI,d)),Times(Plus(C1,c,Times(CI,d)),Exp(Plus(Times(C2,CI,a),Times(C2,CI,b,x))))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(m,C0),NeQ(Sqr(Subtract(c,Times(CI,d))),C1)))),
IIntegrate(6824,Integrate(Times(ArcCoth(Plus(c_DEFAULT,Times(Cot(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),d_DEFAULT))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),ArcCoth(Plus(c,Times(d,Cot(Plus(a,Times(b,x)))))),Power(Times(f,Plus(m,C1)),CN1)),x),Negate(Simp(Star(Times(CI,b,Subtract(Subtract(C1,c),Times(CI,d)),Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Exp(Plus(Times(C2,CI,a),Times(C2,CI,b,x))),Power(Subtract(Plus(C1,Negate(c),Times(CI,d)),Times(Subtract(Subtract(C1,c),Times(CI,d)),Exp(Plus(Times(C2,CI,a),Times(C2,CI,b,x))))),CN1)),x)),x)),Simp(Star(Times(CI,b,Plus(C1,c,Times(CI,d)),Power(Times(f,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Exp(Plus(Times(C2,CI,a),Times(C2,CI,b,x))),Power(Subtract(Subtract(Plus(C1,c),Times(CI,d)),Times(Plus(C1,c,Times(CI,d)),Exp(Plus(Times(C2,CI,a),Times(C2,CI,b,x))))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(m,C0),NeQ(Sqr(Subtract(c,Times(CI,d))),C1)))),
IIntegrate(6825,Integrate(ArcTanh(u_),x_Symbol),
    Condition(Subtract(Simp(Times(x,ArcTanh(u)),x),Integrate(SimplifyIntegrand(Times(x,D(u,x),Power(Subtract(C1,Sqr(u)),CN1)),x),x)),InverseFunctionFreeQ(u,x))),
IIntegrate(6826,Integrate(ArcCoth(u_),x_Symbol),
    Condition(Subtract(Simp(Times(x,ArcCoth(u)),x),Integrate(SimplifyIntegrand(Times(x,D(u,x),Power(Subtract(C1,Sqr(u)),CN1)),x),x)),InverseFunctionFreeQ(u,x))),
IIntegrate(6827,Integrate(Times(Plus(a_DEFAULT,Times(ArcTanh(u_),b_DEFAULT)),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Plus(a,Times(b,ArcTanh(u))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,Power(Times(d,Plus(m,C1)),CN1)),Integrate(SimplifyIntegrand(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),D(u,x),Power(Subtract(C1,Sqr(u)),CN1)),x),x)),x)),And(FreeQ(List(a,b,c,d,m),x),NeQ(m,CN1),InverseFunctionFreeQ(u,x),Not(FunctionOfQ(Power(Plus(c,Times(d,x)),Plus(m,C1)),u,x)),FalseQ(PowerVariableExpn(u,Plus(m,C1),x))))),
IIntegrate(6828,Integrate(Times(Plus(a_DEFAULT,Times(ArcCoth(u_),b_DEFAULT)),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Plus(a,Times(b,ArcCoth(u))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,Power(Times(d,Plus(m,C1)),CN1)),Integrate(SimplifyIntegrand(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),D(u,x),Power(Subtract(C1,Sqr(u)),CN1)),x),x)),x)),And(FreeQ(List(a,b,c,d,m),x),NeQ(m,CN1),InverseFunctionFreeQ(u,x),Not(FunctionOfQ(Power(Plus(c,Times(d,x)),Plus(m,C1)),u,x)),FalseQ(PowerVariableExpn(u,Plus(m,C1),x))))),
IIntegrate(6829,Integrate(Times(Plus(a_DEFAULT,Times(ArcTanh(u_),b_DEFAULT)),v_),x_Symbol),
    Condition(With(list(Set(w,IntHide(v,x))),Condition(Subtract(Simp(Star(Plus(a,Times(b,ArcTanh(u))),w),x),Simp(Star(b,Integrate(SimplifyIntegrand(Times(w,D(u,x),Power(Subtract(C1,Sqr(u)),CN1)),x),x)),x)),InverseFunctionFreeQ(w,x))),And(FreeQ(list(a,b),x),InverseFunctionFreeQ(u,x),Not(MatchQ(v,Condition(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x)),m_DEFAULT),FreeQ(list(c,d,m),x)))),FalseQ(FunctionOfLinear(Times(v,Plus(a,Times(b,ArcTanh(u)))),x))))),
IIntegrate(6830,Integrate(Times(Plus(a_DEFAULT,Times(ArcCoth(u_),b_DEFAULT)),v_),x_Symbol),
    Condition(With(list(Set(w,IntHide(v,x))),Condition(Subtract(Simp(Star(Plus(a,Times(b,ArcCoth(u))),w),x),Simp(Star(b,Integrate(SimplifyIntegrand(Times(w,D(u,x),Power(Subtract(C1,Sqr(u)),CN1)),x),x)),x)),InverseFunctionFreeQ(w,x))),And(FreeQ(list(a,b),x),InverseFunctionFreeQ(u,x),Not(MatchQ(v,Condition(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x)),m_DEFAULT),FreeQ(list(c,d,m),x)))),FalseQ(FunctionOfLinear(Times(v,Plus(a,Times(b,ArcCoth(u)))),x))))),
IIntegrate(6831,Integrate(ArcSech(Times(c_DEFAULT,x_)),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcSech(Times(c,x))),x),Simp(Star(Times(Sqrt(Plus(C1,Times(c,x))),Sqrt(Power(Plus(C1,Times(c,x)),CN1))),Integrate(Power(Subtract(C1,Times(Sqr(c),Sqr(x))),CN1D2),x)),x)),FreeQ(c,x))),
IIntegrate(6832,Integrate(ArcCsch(Times(c_DEFAULT,x_)),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcCsch(Times(c,x))),x),Simp(Star(Power(c,CN1),Integrate(Power(Times(x,Sqrt(Plus(C1,Power(Times(Sqr(c),Sqr(x)),CN1)))),CN1),x)),x)),FreeQ(c,x))),
IIntegrate(6833,Integrate(Power(Plus(a_DEFAULT,Times(ArcSech(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),x_Symbol),
    Condition(Simp(Star(Negate(Power(c,CN1)),Subst(Integrate(Times(Power(Plus(a,Times(b,x)),n),Sech(x),Tanh(x)),x),x,ArcSech(Times(c,x)))),x),And(FreeQ(List(a,b,c,n),x),IGtQ(n,C0)))),
IIntegrate(6834,Integrate(Power(Plus(a_DEFAULT,Times(ArcCsch(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),x_Symbol),
    Condition(Simp(Star(Negate(Power(c,CN1)),Subst(Integrate(Times(Power(Plus(a,Times(b,x)),n),Csch(x),Coth(x)),x),x,ArcCsch(Times(c,x)))),x),And(FreeQ(List(a,b,c,n),x),IGtQ(n,C0)))),
IIntegrate(6835,Integrate(Times(Plus(a_DEFAULT,Times(ArcSech(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(x_,CN1)),x_Symbol),
    Condition(Negate(Subst(Integrate(Times(Plus(a,Times(b,ArcCosh(Times(x,Power(c,CN1))))),Power(x,CN1)),x),x,Power(x,CN1))),FreeQ(list(a,b,c),x))),
IIntegrate(6836,Integrate(Times(Plus(a_DEFAULT,Times(ArcCsch(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(x_,CN1)),x_Symbol),
    Condition(Negate(Subst(Integrate(Times(Plus(a,Times(b,ArcSinh(Times(x,Power(c,CN1))))),Power(x,CN1)),x),x,Power(x,CN1))),FreeQ(list(a,b,c),x))),
IIntegrate(6837,Integrate(Times(Plus(a_DEFAULT,Times(ArcSech(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Times(d,x),Plus(m,C1)),Plus(a,Times(b,ArcSech(Times(c,x)))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,Sqrt(Plus(C1,Times(c,x))),Power(Plus(m,C1),CN1),Sqrt(Power(Plus(C1,Times(c,x)),CN1))),Integrate(Times(Power(Times(d,x),m),Power(Times(Sqrt(Subtract(C1,Times(c,x))),Sqrt(Plus(C1,Times(c,x)))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,m),x),NeQ(m,CN1)))),
IIntegrate(6838,Integrate(Times(Plus(a_DEFAULT,Times(ArcCsch(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Times(d,x),Plus(m,C1)),Plus(a,Times(b,ArcCsch(Times(c,x)))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,d,Power(Times(c,Plus(m,C1)),CN1)),Integrate(Times(Power(Times(d,x),Subtract(m,C1)),Power(Plus(C1,Power(Times(Sqr(c),Sqr(x)),CN1)),CN1D2)),x)),x)),And(FreeQ(List(a,b,c,d,m),x),NeQ(m,CN1)))),
IIntegrate(6839,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSech(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Negate(Power(Power(c,Plus(m,C1)),CN1)),Subst(Integrate(Times(Power(Plus(a,Times(b,x)),n),Power(Sech(x),Plus(m,C1)),Tanh(x)),x),x,ArcSech(Times(c,x)))),x),And(FreeQ(list(a,b,c),x),IntegerQ(n),IntegerQ(m),Or(GtQ(n,C0),LtQ(m,CN1))))),
IIntegrate(6840,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCsch(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Negate(Power(Power(c,Plus(m,C1)),CN1)),Subst(Integrate(Times(Power(Plus(a,Times(b,x)),n),Power(Csch(x),Plus(m,C1)),Coth(x)),x),x,ArcCsch(Times(c,x)))),x),And(FreeQ(list(a,b,c),x),IntegerQ(n),IntegerQ(m),Or(GtQ(n,C0),LtQ(m,CN1)))))
  );
}
