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
class IntRules287 { 
  public static IAST RULES = List( 
IIntegrate(5741,Integrate(Times(Plus(a_DEFAULT,Times(ArcCot(u_),b_DEFAULT)),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Plus(a,Times(b,ArcCot(u))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Dist(Times(b,Power(Times(d,Plus(m,C1)),CN1)),Integrate(SimplifyIntegrand(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),D(u,x),Power(Plus(C1,Sqr(u)),CN1)),x),x),x),x)),And(FreeQ(List(a,b,c,d,m),x),NeQ(m,CN1),InverseFunctionFreeQ(u,x),Not(FunctionOfQ(Power(Plus(c,Times(d,x)),Plus(m,C1)),u,x)),FalseQ(PowerVariableExpn(u,Plus(m,C1),x))))),
IIntegrate(5742,Integrate(Times(Plus(a_DEFAULT,Times(ArcTan(u_),b_DEFAULT)),v_),x_Symbol),
    Condition(With(list(Set(w,IntHide(v,x))),Condition(Subtract(Simp(Dist(Plus(a,Times(b,ArcTan(u))),w,x),x),Simp(Dist(b,Integrate(SimplifyIntegrand(Times(w,D(u,x),Power(Plus(C1,Sqr(u)),CN1)),x),x),x),x)),InverseFunctionFreeQ(w,x))),And(FreeQ(list(a,b),x),InverseFunctionFreeQ(u,x),Not(MatchQ(v,Condition(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x)),m_DEFAULT),FreeQ(list(c,d,m),x)))),FalseQ(FunctionOfLinear(Times(v,Plus(a,Times(b,ArcTan(u)))),x))))),
IIntegrate(5743,Integrate(Times(Plus(a_DEFAULT,Times(ArcCot(u_),b_DEFAULT)),v_),x_Symbol),
    Condition(With(list(Set(w,IntHide(v,x))),Condition(Plus(Simp(Times(Plus(a,Times(b,ArcCot(u))),w),x),Simp(Dist(b,Integrate(SimplifyIntegrand(Times(w,D(u,x),Power(Plus(C1,Sqr(u)),CN1)),x),x),x),x)),InverseFunctionFreeQ(w,x))),And(FreeQ(list(a,b),x),InverseFunctionFreeQ(u,x),Not(MatchQ(v,Condition(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x)),m_DEFAULT),FreeQ(list(c,d,m),x)))),FalseQ(FunctionOfLinear(Times(v,Plus(a,Times(b,ArcCot(u)))),x))))),
IIntegrate(5744,Integrate(Times(ArcTan(v_),Log(w_),Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Subtract(Simp(Dist(CC(0L,1L,1L,2L),Integrate(Times(Log(Subtract(C1,Times(CI,v))),Log(w),Power(Plus(a,Times(b,x)),CN1)),x),x),x),Simp(Dist(CC(0L,1L,1L,2L),Integrate(Times(Log(Plus(C1,Times(CI,v))),Log(w),Power(Plus(a,Times(b,x)),CN1)),x),x),x)),And(FreeQ(list(a,b),x),LinearQ(v,x),LinearQ(w,x),EqQ(Simplify(D(Times(v,Power(Plus(a,Times(b,x)),CN1)),x)),C0),EqQ(Simplify(D(Times(w,Power(Plus(a,Times(b,x)),CN1)),x)),C0)))),
IIntegrate(5745,Integrate(Times(ArcTan(v_),Log(w_)),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcTan(v),Log(w)),x),Negate(Integrate(SimplifyIntegrand(Times(x,Log(w),D(v,x),Power(Plus(C1,Sqr(v)),CN1)),x),x)),Negate(Integrate(SimplifyIntegrand(Times(x,ArcTan(v),D(w,x),Power(w,CN1)),x),x))),And(InverseFunctionFreeQ(v,x),InverseFunctionFreeQ(w,x)))),
IIntegrate(5746,Integrate(Times(ArcCot(v_),Log(w_)),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcCot(v),Log(w)),x),Integrate(SimplifyIntegrand(Times(x,Log(w),D(v,x),Power(Plus(C1,Sqr(v)),CN1)),x),x),Negate(Integrate(SimplifyIntegrand(Times(x,ArcCot(v),D(w,x),Power(w,CN1)),x),x))),And(InverseFunctionFreeQ(v,x),InverseFunctionFreeQ(w,x)))),
IIntegrate(5747,Integrate(Times(ArcTan(v_),Log(w_),u_),x_Symbol),
    Condition(With(list(Set(z,IntHide(u,x))),Condition(Plus(Simp(Dist(Times(ArcTan(v),Log(w)),z,x),x),Negate(Integrate(SimplifyIntegrand(Times(z,Log(w),D(v,x),Power(Plus(C1,Sqr(v)),CN1)),x),x)),Negate(Integrate(SimplifyIntegrand(Times(z,ArcTan(v),D(w,x),Power(w,CN1)),x),x))),InverseFunctionFreeQ(z,x))),And(InverseFunctionFreeQ(v,x),InverseFunctionFreeQ(w,x)))),
IIntegrate(5748,Integrate(Times(ArcCot(v_),Log(w_),u_),x_Symbol),
    Condition(With(list(Set(z,IntHide(u,x))),Condition(Plus(Simp(Dist(Times(ArcCot(v),Log(w)),z,x),x),Integrate(SimplifyIntegrand(Times(z,Log(w),D(v,x),Power(Plus(C1,Sqr(v)),CN1)),x),x),Negate(Integrate(SimplifyIntegrand(Times(z,ArcCot(v),D(w,x),Power(w,CN1)),x),x))),InverseFunctionFreeQ(z,x))),And(InverseFunctionFreeQ(v,x),InverseFunctionFreeQ(w,x)))),
IIntegrate(5749,Integrate(ArcSec(Times(c_DEFAULT,x_)),x_Symbol),
    Condition(Subtract(Simp(Times(x,ArcSec(Times(c,x))),x),Simp(Dist(Power(c,CN1),Integrate(Power(Times(x,Sqrt(Subtract(C1,Power(Times(Sqr(c),Sqr(x)),CN1)))),CN1),x),x),x)),FreeQ(c,x))),
IIntegrate(5750,Integrate(ArcCsc(Times(c_DEFAULT,x_)),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcCsc(Times(c,x))),x),Simp(Dist(Power(c,CN1),Integrate(Power(Times(x,Sqrt(Subtract(C1,Power(Times(Sqr(c),Sqr(x)),CN1)))),CN1),x),x),x)),FreeQ(c,x))),
IIntegrate(5751,Integrate(Power(Plus(a_DEFAULT,Times(ArcSec(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),x_Symbol),
    Condition(Simp(Dist(Power(c,CN1),Subst(Integrate(Times(Power(Plus(a,Times(b,x)),n),Sec(x),Tan(x)),x),x,ArcSec(Times(c,x))),x),x),And(FreeQ(List(a,b,c,n),x),IGtQ(n,C0)))),
IIntegrate(5752,Integrate(Power(Plus(a_DEFAULT,Times(ArcCsc(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),x_Symbol),
    Condition(Simp(Dist(Negate(Power(c,CN1)),Subst(Integrate(Times(Power(Plus(a,Times(b,x)),n),Csc(x),Cot(x)),x),x,ArcCsc(Times(c,x))),x),x),And(FreeQ(List(a,b,c,n),x),IGtQ(n,C0)))),
IIntegrate(5753,Integrate(Times(Plus(a_DEFAULT,Times(ArcSec(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(x_,CN1)),x_Symbol),
    Condition(Negate(Subst(Integrate(Times(Plus(a,Times(b,ArcCos(Times(x,Power(c,CN1))))),Power(x,CN1)),x),x,Power(x,CN1))),FreeQ(list(a,b,c),x))),
IIntegrate(5754,Integrate(Times(Plus(a_DEFAULT,Times(ArcCsc(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(x_,CN1)),x_Symbol),
    Condition(Negate(Subst(Integrate(Times(Plus(a,Times(b,ArcSin(Times(x,Power(c,CN1))))),Power(x,CN1)),x),x,Power(x,CN1))),FreeQ(list(a,b,c),x))),
IIntegrate(5755,Integrate(Times(Plus(a_DEFAULT,Times(ArcSec(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(d,x),Plus(m,C1)),Plus(a,Times(b,ArcSec(Times(c,x)))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Dist(Times(b,d,Power(Times(c,Plus(m,C1)),CN1)),Integrate(Times(Power(Times(d,x),Plus(m,CN1)),Power(Subtract(C1,Power(Times(Sqr(c),Sqr(x)),CN1)),CN1D2)),x),x),x)),And(FreeQ(List(a,b,c,d,m),x),NeQ(m,CN1)))),
IIntegrate(5756,Integrate(Times(Plus(a_DEFAULT,Times(ArcCsc(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Times(d_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Times(d,x),Plus(m,C1)),Plus(a,Times(b,ArcCsc(Times(c,x)))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Dist(Times(b,d,Power(Times(c,Plus(m,C1)),CN1)),Integrate(Times(Power(Times(d,x),Plus(m,CN1)),Power(Subtract(C1,Power(Times(Sqr(c),Sqr(x)),CN1)),CN1D2)),x),x),x)),And(FreeQ(List(a,b,c,d,m),x),NeQ(m,CN1)))),
IIntegrate(5757,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcSec(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(Power(c,Plus(m,C1)),CN1),Subst(Integrate(Times(Power(Plus(a,Times(b,x)),n),Power(Sec(x),Plus(m,C1)),Tan(x)),x),x,ArcSec(Times(c,x))),x),x),And(FreeQ(list(a,b,c),x),IntegerQ(n),IntegerQ(m),Or(GtQ(n,C0),LtQ(m,CN1))))),
IIntegrate(5758,Integrate(Times(Power(Plus(a_DEFAULT,Times(ArcCsc(Times(c_DEFAULT,x_)),b_DEFAULT)),n_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Negate(Power(Power(c,Plus(m,C1)),CN1)),Subst(Integrate(Times(Power(Plus(a,Times(b,x)),n),Power(Csc(x),Plus(m,C1)),Cot(x)),x),x,ArcCsc(Times(c,x))),x),x),And(FreeQ(list(a,b,c),x),IntegerQ(n),IntegerQ(m),Or(GtQ(n,C0),LtQ(m,CN1))))),
IIntegrate(5759,Integrate(Times(Plus(a_DEFAULT,Times(ArcSec(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Plus(Simp(Times(Plus(a,Times(b,ArcSec(Times(c,x)))),Log(Plus(C1,Times(Subtract(e,Sqrt(Plus(Times(CN1,Sqr(c),Sqr(d)),Sqr(e)))),Exp(Times(CI,ArcSec(Times(c,x)))),Power(Times(c,d),CN1)))),Power(e,CN1)),x),Simp(Times(Plus(a,Times(b,ArcSec(Times(c,x)))),Log(Plus(C1,Times(Plus(e,Sqrt(Plus(Times(CN1,Sqr(c),Sqr(d)),Sqr(e)))),Exp(Times(CI,ArcSec(Times(c,x)))),Power(Times(c,d),CN1)))),Power(e,CN1)),x),Negate(Simp(Times(Plus(a,Times(b,ArcSec(Times(c,x)))),Log(Plus(C1,Exp(Times(C2,CI,ArcSec(Times(c,x)))))),Power(e,CN1)),x)),Negate(Simp(Dist(Times(b,Power(Times(c,e),CN1)),Integrate(Times(Log(Plus(C1,Times(Subtract(e,Sqrt(Plus(Times(CN1,Sqr(c),Sqr(d)),Sqr(e)))),Exp(Times(CI,ArcSec(Times(c,x)))),Power(Times(c,d),CN1)))),Power(Times(Sqr(x),Sqrt(Subtract(C1,Power(Times(Sqr(c),Sqr(x)),CN1)))),CN1)),x),x),x)),Negate(Simp(Dist(Times(b,Power(Times(c,e),CN1)),Integrate(Times(Log(Plus(C1,Times(Plus(e,Sqrt(Plus(Times(CN1,Sqr(c),Sqr(d)),Sqr(e)))),Exp(Times(CI,ArcSec(Times(c,x)))),Power(Times(c,d),CN1)))),Power(Times(Sqr(x),Sqrt(Subtract(C1,Power(Times(Sqr(c),Sqr(x)),CN1)))),CN1)),x),x),x)),Simp(Dist(Times(b,Power(Times(c,e),CN1)),Integrate(Times(Log(Plus(C1,Exp(Times(C2,CI,ArcSec(Times(c,x)))))),Power(Times(Sqr(x),Sqrt(Subtract(C1,Power(Times(Sqr(c),Sqr(x)),CN1)))),CN1)),x),x),x)),FreeQ(List(a,b,c,d,e),x))),
IIntegrate(5760,Integrate(Times(Plus(a_DEFAULT,Times(ArcCsc(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(Plus(Simp(Times(Plus(a,Times(b,ArcCsc(Times(c,x)))),Log(Subtract(C1,Times(CI,Subtract(e,Sqrt(Plus(Times(CN1,Sqr(c),Sqr(d)),Sqr(e)))),Exp(Times(CI,ArcCsc(Times(c,x)))),Power(Times(c,d),CN1)))),Power(e,CN1)),x),Simp(Times(Plus(a,Times(b,ArcCsc(Times(c,x)))),Log(Subtract(C1,Times(CI,Plus(e,Sqrt(Plus(Times(CN1,Sqr(c),Sqr(d)),Sqr(e)))),Exp(Times(CI,ArcCsc(Times(c,x)))),Power(Times(c,d),CN1)))),Power(e,CN1)),x),Negate(Simp(Times(Plus(a,Times(b,ArcCsc(Times(c,x)))),Log(Subtract(C1,Exp(Times(C2,CI,ArcCsc(Times(c,x)))))),Power(e,CN1)),x)),Simp(Dist(Times(b,Power(Times(c,e),CN1)),Integrate(Times(Log(Subtract(C1,Times(CI,Subtract(e,Sqrt(Plus(Times(CN1,Sqr(c),Sqr(d)),Sqr(e)))),Exp(Times(CI,ArcCsc(Times(c,x)))),Power(Times(c,d),CN1)))),Power(Times(Sqr(x),Sqrt(Subtract(C1,Power(Times(Sqr(c),Sqr(x)),CN1)))),CN1)),x),x),x),Simp(Dist(Times(b,Power(Times(c,e),CN1)),Integrate(Times(Log(Subtract(C1,Times(CI,Plus(e,Sqrt(Plus(Times(CN1,Sqr(c),Sqr(d)),Sqr(e)))),Exp(Times(CI,ArcCsc(Times(c,x)))),Power(Times(c,d),CN1)))),Power(Times(Sqr(x),Sqrt(Subtract(C1,Power(Times(Sqr(c),Sqr(x)),CN1)))),CN1)),x),x),x),Negate(Simp(Dist(Times(b,Power(Times(c,e),CN1)),Integrate(Times(Log(Subtract(C1,Exp(Times(C2,CI,ArcCsc(Times(c,x)))))),Power(Times(Sqr(x),Sqrt(Subtract(C1,Power(Times(Sqr(c),Sqr(x)),CN1)))),CN1)),x),x),x))),FreeQ(List(a,b,c,d,e),x)))
  );
}
