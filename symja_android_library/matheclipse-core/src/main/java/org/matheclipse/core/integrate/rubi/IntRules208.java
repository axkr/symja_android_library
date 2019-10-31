package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctions.*;
import org.matheclipse.core.interfaces.IAST;

/** 
 * IndefiniteIntegrationRules from the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi -
 * rule-based integrator</a>.
 *  
 */
public class IntRules208 { 
  public static IAST RULES = List( 
IIntegrate(5201,Int(Times(ArcTan(Plus(c_DEFAULT,Times(Coth(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),d_DEFAULT))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),ArcTan(Plus(c,Times(d,Coth(Plus(a,Times(b,x)))))),Power(Times(f,Plus(m,C1)),CN1)),x),Negate(Dist(Times(CI,b,Subtract(Subtract(CI,c),d),Power(Times(f,Plus(m,C1)),CN1)),Int(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Exp(Plus(Times(C2,a),Times(C2,b,x))),Power(Subtract(Plus(CI,Negate(c),d),Times(Subtract(Subtract(CI,c),d),Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x),x)),Dist(Times(CI,b,Plus(CI,c,d),Power(Times(f,Plus(m,C1)),CN1)),Int(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Exp(Plus(Times(C2,a),Times(C2,b,x))),Power(Subtract(Subtract(Plus(CI,c),d),Times(Plus(CI,c,d),Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(m,C0),NeQ(Sqr(Subtract(c,d)),CN1)))),
IIntegrate(5202,Int(Times(ArcCot(Plus(c_DEFAULT,Times(Coth(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),d_DEFAULT))),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),ArcCot(Plus(c,Times(d,Coth(Plus(a,Times(b,x)))))),Power(Times(f,Plus(m,C1)),CN1)),x),Dist(Times(CI,b,Subtract(Subtract(CI,c),d),Power(Times(f,Plus(m,C1)),CN1)),Int(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Exp(Plus(Times(C2,a),Times(C2,b,x))),Power(Subtract(Plus(CI,Negate(c),d),Times(Subtract(Subtract(CI,c),d),Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x),x),Negate(Dist(Times(CI,b,Plus(CI,c,d),Power(Times(f,Plus(m,C1)),CN1)),Int(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Exp(Plus(Times(C2,a),Times(C2,b,x))),Power(Subtract(Subtract(Plus(CI,c),d),Times(Plus(CI,c,d),Exp(Plus(Times(C2,a),Times(C2,b,x))))),CN1)),x),x))),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(m,C0),NeQ(Sqr(Subtract(c,d)),CN1)))),
IIntegrate(5203,Int(ArcTan(u_),x_Symbol),
    Condition(Subtract(Simp(Times(x,ArcTan(u)),x),Int(SimplifyIntegrand(Times(x,D(u,x),Power(Plus(C1,Sqr(u)),CN1)),x),x)),InverseFunctionFreeQ(u,x))),
IIntegrate(5204,Int(ArcCot(u_),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcCot(u)),x),Int(SimplifyIntegrand(Times(x,D(u,x),Power(Plus(C1,Sqr(u)),CN1)),x),x)),InverseFunctionFreeQ(u,x))),
IIntegrate(5205,Int(Times(Plus(a_DEFAULT,Times(ArcTan(u_),b_DEFAULT)),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Plus(a,Times(b,ArcTan(u))),Power(Times(d,Plus(m,C1)),CN1)),x),Dist(Times(b,Power(Times(d,Plus(m,C1)),CN1)),Int(SimplifyIntegrand(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),D(u,x),Power(Plus(C1,Sqr(u)),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,m),x),NeQ(m,CN1),InverseFunctionFreeQ(u,x),Not(FunctionOfQ(Power(Plus(c,Times(d,x)),Plus(m,C1)),u,x)),FalseQ(PowerVariableExpn(u,Plus(m,C1),x))))),
IIntegrate(5206,Int(Times(Plus(a_DEFAULT,Times(ArcCot(u_),b_DEFAULT)),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Plus(a,Times(b,ArcCot(u))),Power(Times(d,Plus(m,C1)),CN1)),x),Dist(Times(b,Power(Times(d,Plus(m,C1)),CN1)),Int(SimplifyIntegrand(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),D(u,x),Power(Plus(C1,Sqr(u)),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,m),x),NeQ(m,CN1),InverseFunctionFreeQ(u,x),Not(FunctionOfQ(Power(Plus(c,Times(d,x)),Plus(m,C1)),u,x)),FalseQ(PowerVariableExpn(u,Plus(m,C1),x))))),
IIntegrate(5207,Int(Times(Plus(a_DEFAULT,Times(ArcTan(u_),b_DEFAULT)),v_),x_Symbol),
    Condition(With(List(Set(w,IntHide(v,x))),Condition(Subtract(Dist(Plus(a,Times(b,ArcTan(u))),w,x),Dist(b,Int(SimplifyIntegrand(Times(w,D(u,x),Power(Plus(C1,Sqr(u)),CN1)),x),x),x)),InverseFunctionFreeQ(w,x))),And(FreeQ(List(a,b),x),InverseFunctionFreeQ(u,x),Not(MatchQ(v,Condition(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x)),m_DEFAULT),FreeQ(List(c,d,m),x)))),FalseQ(FunctionOfLinear(Times(v,Plus(a,Times(b,ArcTan(u)))),x))))),
IIntegrate(5208,Int(Times(Plus(a_DEFAULT,Times(ArcCot(u_),b_DEFAULT)),v_),x_Symbol),
    Condition(With(List(Set(w,IntHide(v,x))),Condition(Plus(Dist(Plus(a,Times(b,ArcCot(u))),w,x),Dist(b,Int(SimplifyIntegrand(Times(w,D(u,x),Power(Plus(C1,Sqr(u)),CN1)),x),x),x)),InverseFunctionFreeQ(w,x))),And(FreeQ(List(a,b),x),InverseFunctionFreeQ(u,x),Not(MatchQ(v,Condition(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x)),m_DEFAULT),FreeQ(List(c,d,m),x)))),FalseQ(FunctionOfLinear(Times(v,Plus(a,Times(b,ArcCot(u)))),x))))),
IIntegrate(5209,Int(Times(ArcTan(v_),Log(w_),Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Subtract(Dist(Times(C1D2,CI),Int(Times(Log(Subtract(C1,Times(CI,v))),Log(w),Power(Plus(a,Times(b,x)),CN1)),x),x),Dist(Times(C1D2,CI),Int(Times(Log(Plus(C1,Times(CI,v))),Log(w),Power(Plus(a,Times(b,x)),CN1)),x),x)),And(FreeQ(List(a,b),x),LinearQ(v,x),LinearQ(w,x),EqQ(Simplify(D(Times(v,Power(Plus(a,Times(b,x)),CN1)),x)),C0),EqQ(Simplify(D(Times(w,Power(Plus(a,Times(b,x)),CN1)),x)),C0)))),
IIntegrate(5210,Int(Times(ArcTan(v_),Log(w_)),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcTan(v),Log(w)),x),Negate(Int(SimplifyIntegrand(Times(x,Log(w),D(v,x),Power(Plus(C1,Sqr(v)),CN1)),x),x)),Negate(Int(SimplifyIntegrand(Times(x,ArcTan(v),D(w,x),Power(w,CN1)),x),x))),And(InverseFunctionFreeQ(v,x),InverseFunctionFreeQ(w,x)))),
IIntegrate(5211,Int(Times(ArcCot(v_),Log(w_)),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcCot(v),Log(w)),x),Int(SimplifyIntegrand(Times(x,Log(w),D(v,x),Power(Plus(C1,Sqr(v)),CN1)),x),x),Negate(Int(SimplifyIntegrand(Times(x,ArcCot(v),D(w,x),Power(w,CN1)),x),x))),And(InverseFunctionFreeQ(v,x),InverseFunctionFreeQ(w,x)))),
IIntegrate(5212,Int(Times(ArcTan(v_),Log(w_),u_),x_Symbol),
    Condition(With(List(Set(z,IntHide(u,x))),Condition(Plus(Dist(Times(ArcTan(v),Log(w)),z,x),Negate(Int(SimplifyIntegrand(Times(z,Log(w),D(v,x),Power(Plus(C1,Sqr(v)),CN1)),x),x)),Negate(Int(SimplifyIntegrand(Times(z,ArcTan(v),D(w,x),Power(w,CN1)),x),x))),InverseFunctionFreeQ(z,x))),And(InverseFunctionFreeQ(v,x),InverseFunctionFreeQ(w,x)))),
IIntegrate(5213,Int(Times(ArcCot(v_),Log(w_),u_),x_Symbol),
    Condition(With(List(Set(z,IntHide(u,x))),Condition(Plus(Dist(Times(ArcCot(v),Log(w)),z,x),Int(SimplifyIntegrand(Times(z,Log(w),D(v,x),Power(Plus(C1,Sqr(v)),CN1)),x),x),Negate(Int(SimplifyIntegrand(Times(z,ArcCot(v),D(w,x),Power(w,CN1)),x),x))),InverseFunctionFreeQ(z,x))),And(InverseFunctionFreeQ(v,x),InverseFunctionFreeQ(w,x)))),
IIntegrate(5214,Int(ArcSec(Times(c_DEFAULT,x_)),x_Symbol),
    Condition(Subtract(Simp(Times(x,ArcSec(Times(c,x))),x),Dist(Power(c,CN1),Int(Power(Times(x,Sqrt(Subtract(C1,Power(Times(Sqr(c),Sqr(x)),CN1)))),CN1),x),x)),FreeQ(c,x))),
IIntegrate(5215,Int(ArcCsc(Times(c_DEFAULT,x_)),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcCsc(Times(c,x))),x),Dist(Power(c,CN1),Int(Power(Times(x,Sqrt(Subtract(C1,Power(Times(Sqr(c),Sqr(x)),CN1)))),CN1),x),x)),FreeQ(c,x))),
IIntegrate(5216,Int(Power(Plus(a_DEFAULT,Times(ArcSec(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),x_Symbol),
    Condition(Dist(Power(c,CN1),Subst(Int(Times(Power(Plus(a,Times(b,x)),n),Sec(x),Tan(x)),x),x,ArcSec(Times(c,x))),x),And(FreeQ(List(a,b,c,n),x),IGtQ(n,C0)))),
IIntegrate(5217,Int(Power(Plus(a_DEFAULT,Times(ArcCsc(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),x_Symbol),
    Condition(Negate(Dist(Power(c,CN1),Subst(Int(Times(Power(Plus(a,Times(b,x)),n),Csc(x),Cot(x)),x),x,ArcCsc(Times(c,x))),x)),And(FreeQ(List(a,b,c,n),x),IGtQ(n,C0)))),
IIntegrate(5218,Int(Times(Plus(a_DEFAULT,Times(ArcSec(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(x_,CN1)),x_Symbol),
    Condition(Negate(Subst(Int(Times(Plus(a,Times(b,ArcCos(Times(x,Power(c,CN1))))),Power(x,CN1)),x),x,Power(x,CN1))),FreeQ(List(a,b,c),x))),
IIntegrate(5219,Int(Times(Plus(a_DEFAULT,Times(ArcCsc(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(x_,CN1)),x_Symbol),
    Condition(Negate(Subst(Int(Times(Plus(a,Times(b,ArcSin(Times(x,Power(c,CN1))))),Power(x,CN1)),x),x,Power(x,CN1))),FreeQ(List(a,b,c),x))),
IIntegrate(5220,Int(Times(Plus(a_DEFAULT,Times(ArcSec(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(d,x),Plus(m,C1)),Plus(a,Times(b,ArcSec(Times(c,x)))),Power(Times(d,Plus(m,C1)),CN1)),x),Dist(Times(b,d,Power(Times(c,Plus(m,C1)),CN1)),Int(Times(Power(Times(d,x),Subtract(m,C1)),Power(Subtract(C1,Power(Times(Sqr(c),Sqr(x)),CN1)),CN1D2)),x),x)),And(FreeQ(List(a,b,c,d,m),x),NeQ(m,CN1)))),
IIntegrate(5221,Int(Times(Plus(a_DEFAULT,Times(ArcCsc(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Times(d,x),Plus(m,C1)),Plus(a,Times(b,ArcCsc(Times(c,x)))),Power(Times(d,Plus(m,C1)),CN1)),x),Dist(Times(b,d,Power(Times(c,Plus(m,C1)),CN1)),Int(Times(Power(Times(d,x),Subtract(m,C1)),Power(Subtract(C1,Power(Times(Sqr(c),Sqr(x)),CN1)),CN1D2)),x),x)),And(FreeQ(List(a,b,c,d,m),x),NeQ(m,CN1)))),
IIntegrate(5222,Int(Times(Power(Plus(a_DEFAULT,Times(ArcSec(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Dist(Power(Power(c,Plus(m,C1)),CN1),Subst(Int(Times(Power(Plus(a,Times(b,x)),n),Power(Sec(x),Plus(m,C1)),Tan(x)),x),x,ArcSec(Times(c,x))),x),And(FreeQ(List(a,b,c),x),IntegerQ(n),IntegerQ(m),Or(GtQ(n,C0),LtQ(m,CN1))))),
IIntegrate(5223,Int(Times(Power(Plus(a_DEFAULT,Times(ArcCsc(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Negate(Dist(Power(Power(c,Plus(m,C1)),CN1),Subst(Int(Times(Power(Plus(a,Times(b,x)),n),Power(Csc(x),Plus(m,C1)),Cot(x)),x),x,ArcCsc(Times(c,x))),x)),And(FreeQ(List(a,b,c),x),IntegerQ(n),IntegerQ(m),Or(GtQ(n,C0),LtQ(m,CN1))))),
IIntegrate(5224,Int(Times(Plus(a_DEFAULT,Times(ArcSec(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Plus(Simp(Times(Plus(a,Times(b,ArcSec(Times(c,x)))),Log(Plus(C1,Times(Subtract(e,Sqrt(Plus(Times(CN1,Sqr(c),Sqr(d)),Sqr(e)))),Exp(Times(CI,ArcSec(Times(c,x)))),Power(Times(c,d),CN1)))),Power(e,CN1)),x),Negate(Dist(Times(b,Power(Times(c,e),CN1)),Int(Times(Log(Plus(C1,Times(Subtract(e,Sqrt(Plus(Times(CN1,Sqr(c),Sqr(d)),Sqr(e)))),Exp(Times(CI,ArcSec(Times(c,x)))),Power(Times(c,d),CN1)))),Power(Times(Sqr(x),Sqrt(Subtract(C1,Power(Times(Sqr(c),Sqr(x)),CN1)))),CN1)),x),x)),Negate(Dist(Times(b,Power(Times(c,e),CN1)),Int(Times(Log(Plus(C1,Times(Plus(e,Sqrt(Plus(Times(CN1,Sqr(c),Sqr(d)),Sqr(e)))),Exp(Times(CI,ArcSec(Times(c,x)))),Power(Times(c,d),CN1)))),Power(Times(Sqr(x),Sqrt(Subtract(C1,Power(Times(Sqr(c),Sqr(x)),CN1)))),CN1)),x),x)),Dist(Times(b,Power(Times(c,e),CN1)),Int(Times(Log(Plus(C1,Exp(Times(C2,CI,ArcSec(Times(c,x)))))),Power(Times(Sqr(x),Sqrt(Subtract(C1,Power(Times(Sqr(c),Sqr(x)),CN1)))),CN1)),x),x),Simp(Times(Plus(a,Times(b,ArcSec(Times(c,x)))),Log(Plus(C1,Times(Plus(e,Sqrt(Plus(Times(CN1,Sqr(c),Sqr(d)),Sqr(e)))),Exp(Times(CI,ArcSec(Times(c,x)))),Power(Times(c,d),CN1)))),Power(e,CN1)),x),Negate(Simp(Times(Plus(a,Times(b,ArcSec(Times(c,x)))),Log(Plus(C1,Exp(Times(C2,CI,ArcSec(Times(c,x)))))),Power(e,CN1)),x))),FreeQ(List(a,b,c,d,e),x))),
IIntegrate(5225,Int(Times(Plus(a_DEFAULT,Times(ArcCsc(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Plus(Simp(Times(Plus(a,Times(b,ArcCsc(Times(c,x)))),Log(Subtract(C1,Times(CI,Subtract(e,Sqrt(Plus(Times(CN1,Sqr(c),Sqr(d)),Sqr(e)))),Exp(Times(CI,ArcCsc(Times(c,x)))),Power(Times(c,d),CN1)))),Power(e,CN1)),x),Dist(Times(b,Power(Times(c,e),CN1)),Int(Times(Log(Subtract(C1,Times(CI,Subtract(e,Sqrt(Plus(Times(CN1,Sqr(c),Sqr(d)),Sqr(e)))),Exp(Times(CI,ArcCsc(Times(c,x)))),Power(Times(c,d),CN1)))),Power(Times(Sqr(x),Sqrt(Subtract(C1,Power(Times(Sqr(c),Sqr(x)),CN1)))),CN1)),x),x),Dist(Times(b,Power(Times(c,e),CN1)),Int(Times(Log(Subtract(C1,Times(CI,Plus(e,Sqrt(Plus(Times(CN1,Sqr(c),Sqr(d)),Sqr(e)))),Exp(Times(CI,ArcCsc(Times(c,x)))),Power(Times(c,d),CN1)))),Power(Times(Sqr(x),Sqrt(Subtract(C1,Power(Times(Sqr(c),Sqr(x)),CN1)))),CN1)),x),x),Negate(Dist(Times(b,Power(Times(c,e),CN1)),Int(Times(Log(Subtract(C1,Exp(Times(C2,CI,ArcCsc(Times(c,x)))))),Power(Times(Sqr(x),Sqrt(Subtract(C1,Power(Times(Sqr(c),Sqr(x)),CN1)))),CN1)),x),x)),Simp(Times(Plus(a,Times(b,ArcCsc(Times(c,x)))),Log(Subtract(C1,Times(CI,Plus(e,Sqrt(Plus(Times(CN1,Sqr(c),Sqr(d)),Sqr(e)))),Exp(Times(CI,ArcCsc(Times(c,x)))),Power(Times(c,d),CN1)))),Power(e,CN1)),x),Negate(Simp(Times(Plus(a,Times(b,ArcCsc(Times(c,x)))),Log(Subtract(C1,Exp(Times(C2,CI,ArcCsc(Times(c,x)))))),Power(e,CN1)),x))),FreeQ(List(a,b,c,d,e),x)))
  );
}
