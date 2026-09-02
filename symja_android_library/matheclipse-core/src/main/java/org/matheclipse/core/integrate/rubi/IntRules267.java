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
class IntRules267 { 
  public static IAST RULES = List( 
IIntegrate(5341,Integrate(Times(Power(ArcSin(Times(a_DEFAULT,Power(x_,p_))),n_DEFAULT),Power(x_,CN1)),x_Symbol),
    Condition(Simp(Dist(Power(p,CN1),Subst(Integrate(Times(Power(x,n),Cot(x)),x),x,ArcSin(Times(a,Power(x,p)))),x),x),And(FreeQ(list(a,p),x),IGtQ(n,C0)))),
IIntegrate(5342,Integrate(Times(Power(ArcCos(Times(a_DEFAULT,Power(x_,p_))),n_DEFAULT),Power(x_,CN1)),x_Symbol),
    Condition(Simp(Dist(Negate(Power(p,CN1)),Subst(Integrate(Times(Power(x,n),Tan(x)),x),x,ArcCos(Times(a,Power(x,p)))),x),x),And(FreeQ(list(a,p),x),IGtQ(n,C0)))),
IIntegrate(5343,Integrate(Times(Power(ArcSin(Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))),CN1))),m_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Integrate(Times(u,Power(ArcCsc(Plus(Times(a,Power(c,CN1)),Times(b,Power(x,n),Power(c,CN1)))),m)),x),FreeQ(List(a,b,c,n,m),x))),
IIntegrate(5344,Integrate(Times(Power(ArcCos(Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))),CN1))),m_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Integrate(Times(u,Power(ArcSec(Plus(Times(a,Power(c,CN1)),Times(b,Power(x,n),Power(c,CN1)))),m)),x),FreeQ(List(a,b,c,n,m),x))),
IIntegrate(5345,Integrate(Times(Power(ArcSin(Sqrt(Plus(C1,Times(b_DEFAULT,Sqr(x_))))),n_DEFAULT),Power(Plus(C1,Times(b_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Simp(Dist(Times(Sqrt(Times(CN1,b,Sqr(x))),Power(Times(b,x),CN1)),Subst(Integrate(Times(Power(ArcSin(x),n),Power(Subtract(C1,Sqr(x)),CN1D2)),x),x,Sqrt(Plus(C1,Times(b,Sqr(x))))),x),x),FreeQ(list(b,n),x))),
IIntegrate(5346,Integrate(Times(Power(ArcCos(Sqrt(Plus(C1,Times(b_DEFAULT,Sqr(x_))))),n_DEFAULT),Power(Plus(C1,Times(b_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Simp(Dist(Times(Sqrt(Times(CN1,b,Sqr(x))),Power(Times(b,x),CN1)),Subst(Integrate(Times(Power(ArcCos(x),n),Power(Subtract(C1,Sqr(x)),CN1D2)),x),x,Sqrt(Plus(C1,Times(b,Sqr(x))))),x),x),FreeQ(list(b,n),x))),
IIntegrate(5347,Integrate(Times(u_DEFAULT,Power(f_,Times(Power(ArcSin(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),c_DEFAULT))),x_Symbol),
    Condition(Simp(Dist(Power(b,CN1),Subst(Integrate(Times(ReplaceAll(u,Rule(x,Plus(Times(CN1,a,Power(b,CN1)),Times(Sin(x),Power(b,CN1))))),Power(f,Times(c,Power(x,n))),Cos(x)),x),x,ArcSin(Plus(a,Times(b,x)))),x),x),And(FreeQ(List(a,b,c,f),x),IGtQ(n,C0)))),
IIntegrate(5348,Integrate(Times(u_DEFAULT,Power(f_,Times(Power(ArcCos(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),c_DEFAULT))),x_Symbol),
    Condition(Simp(Dist(Negate(Power(b,CN1)),Subst(Integrate(Times(ReplaceAll(u,Rule(x,Plus(Times(CN1,a,Power(b,CN1)),Times(Cos(x),Power(b,CN1))))),Power(f,Times(c,Power(x,n))),Sin(x)),x),x,ArcCos(Plus(a,Times(b,x)))),x),x),And(FreeQ(List(a,b,c,f),x),IGtQ(n,C0)))),
IIntegrate(5349,Integrate(ArcSin(Plus(Times(a_DEFAULT,Sqr(x_)),Times(b_DEFAULT,Sqrt(Plus(c_,Times(d_DEFAULT,Sqr(x_))))))),x_Symbol),
    Condition(Subtract(Simp(Times(x,ArcSin(Plus(Times(a,Sqr(x)),Times(b,Sqrt(Plus(c,Times(d,Sqr(x)))))))),x),Simp(Dist(Times(x,Sqrt(Plus(Times(Sqr(b),d),Times(Sqr(a),Sqr(x)),Times(C2,a,b,Sqrt(Plus(c,Times(d,Sqr(x))))))),Power(Times(CN1,Sqr(x),Plus(Times(Sqr(b),d),Times(Sqr(a),Sqr(x)),Times(C2,a,b,Sqrt(Plus(c,Times(d,Sqr(x))))))),CN1D2)),Integrate(Times(x,Plus(Times(b,d),Times(C2,a,Sqrt(Plus(c,Times(d,Sqr(x)))))),Power(Times(Sqrt(Plus(c,Times(d,Sqr(x)))),Sqrt(Plus(Times(Sqr(b),d),Times(Sqr(a),Sqr(x)),Times(C2,a,b,Sqrt(Plus(c,Times(d,Sqr(x)))))))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Times(Sqr(b),c),C1)))),
IIntegrate(5350,Integrate(ArcCos(Plus(Times(a_DEFAULT,Sqr(x_)),Times(b_DEFAULT,Sqrt(Plus(c_,Times(d_DEFAULT,Sqr(x_))))))),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcCos(Plus(Times(a,Sqr(x)),Times(b,Sqrt(Plus(c,Times(d,Sqr(x)))))))),x),Simp(Dist(Times(x,Sqrt(Plus(Times(Sqr(b),d),Times(Sqr(a),Sqr(x)),Times(C2,a,b,Sqrt(Plus(c,Times(d,Sqr(x))))))),Power(Times(CN1,Sqr(x),Plus(Times(Sqr(b),d),Times(Sqr(a),Sqr(x)),Times(C2,a,b,Sqrt(Plus(c,Times(d,Sqr(x))))))),CN1D2)),Integrate(Times(x,Plus(Times(b,d),Times(C2,a,Sqrt(Plus(c,Times(d,Sqr(x)))))),Power(Times(Sqrt(Plus(c,Times(d,Sqr(x)))),Sqrt(Plus(Times(Sqr(b),d),Times(Sqr(a),Sqr(x)),Times(C2,a,b,Sqrt(Plus(c,Times(d,Sqr(x)))))))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Times(Sqr(b),c),C1)))),
IIntegrate(5351,Integrate(ArcSin(u_),x_Symbol),
    Condition(Subtract(Simp(Times(x,ArcSin(u)),x),Integrate(SimplifyIntegrand(Times(x,D(u,x),Power(Subtract(C1,Sqr(u)),CN1D2)),x),x)),And(InverseFunctionFreeQ(u,x),Not(FunctionOfExponentialQ(u,x))))),
IIntegrate(5352,Integrate(ArcCos(u_),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcCos(u)),x),Integrate(SimplifyIntegrand(Times(x,D(u,x),Power(Subtract(C1,Sqr(u)),CN1D2)),x),x)),And(InverseFunctionFreeQ(u,x),Not(FunctionOfExponentialQ(u,x))))),
IIntegrate(5353,Integrate(Times(Plus(a_DEFAULT,Times(ArcSin(u_),b_DEFAULT)),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Plus(a,Times(b,ArcSin(u))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Dist(Times(b,Power(Times(d,Plus(m,C1)),CN1)),Integrate(SimplifyIntegrand(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),D(u,x),Power(Subtract(C1,Sqr(u)),CN1D2)),x),x),x),x)),And(FreeQ(List(a,b,c,d,m),x),NeQ(m,CN1),InverseFunctionFreeQ(u,x),Not(FunctionOfQ(Power(Plus(c,Times(d,x)),Plus(m,C1)),u,x)),Not(FunctionOfExponentialQ(u,x))))),
IIntegrate(5354,Integrate(Times(Plus(a_DEFAULT,Times(ArcCos(u_),b_DEFAULT)),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Plus(a,Times(b,ArcCos(u))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Dist(Times(b,Power(Times(d,Plus(m,C1)),CN1)),Integrate(SimplifyIntegrand(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),D(u,x),Power(Subtract(C1,Sqr(u)),CN1D2)),x),x),x),x)),And(FreeQ(List(a,b,c,d,m),x),NeQ(m,CN1),InverseFunctionFreeQ(u,x),Not(FunctionOfQ(Power(Plus(c,Times(d,x)),Plus(m,C1)),u,x)),Not(FunctionOfExponentialQ(u,x))))),
IIntegrate(5355,Integrate(Times(Plus(a_DEFAULT,Times(ArcSin(u_),b_DEFAULT)),v_),x_Symbol),
    Condition(With(list(Set(w,IntHide(v,x))),Condition(Subtract(Simp(Dist(Plus(a,Times(b,ArcSin(u))),w,x),x),Simp(Dist(b,Integrate(SimplifyIntegrand(Times(w,D(u,x),Power(Subtract(C1,Sqr(u)),CN1D2)),x),x),x),x)),InverseFunctionFreeQ(w,x))),And(FreeQ(list(a,b),x),InverseFunctionFreeQ(u,x),Not(MatchQ(v,Condition(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x)),m_DEFAULT),FreeQ(list(c,d,m),x))))))),
IIntegrate(5356,Integrate(Times(Plus(a_DEFAULT,Times(ArcCos(u_),b_DEFAULT)),v_),x_Symbol),
    Condition(With(list(Set(w,IntHide(v,x))),Condition(Plus(Simp(Dist(Plus(a,Times(b,ArcCos(u))),w,x),x),Simp(Dist(b,Integrate(SimplifyIntegrand(Times(w,D(u,x),Power(Subtract(C1,Sqr(u)),CN1D2)),x),x),x),x)),InverseFunctionFreeQ(w,x))),And(FreeQ(list(a,b),x),InverseFunctionFreeQ(u,x),Not(MatchQ(v,Condition(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x)),m_DEFAULT),FreeQ(list(c,d,m),x))))))),
IIntegrate(5357,Integrate(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Subtract(Simp(Times(x,Power(Plus(a,Times(b,ArcTan(Times(c,Power(x,n))))),p)),x),Simp(Dist(Times(b,c,n,p),Integrate(Times(Power(x,n),Power(Plus(a,Times(b,ArcTan(Times(c,Power(x,n))))),Plus(p,CN1)),Power(Plus(C1,Times(Sqr(c),Power(x,Times(C2,n)))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,n),x),IGtQ(p,C0),Or(EqQ(n,C1),EqQ(p,C1))))),
IIntegrate(5358,Integrate(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Plus(Simp(Times(x,Power(Plus(a,Times(b,ArcCot(Times(c,Power(x,n))))),p)),x),Simp(Dist(Times(b,c,n,p),Integrate(Times(Power(x,n),Power(Plus(a,Times(b,ArcCot(Times(c,Power(x,n))))),Plus(p,CN1)),Power(Plus(C1,Times(Sqr(c),Power(x,Times(C2,n)))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,n),x),IGtQ(p,C0),Or(EqQ(n,C1),EqQ(p,C1))))),
IIntegrate(5359,Integrate(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Power(Subtract(Plus(a,Times(C1D2,CI,b,Log(Subtract(C1,Times(CI,c,Power(x,n)))))),Times(C1D2,CI,b,Log(Plus(C1,Times(CI,c,Power(x,n)))))),p),x),x),And(FreeQ(list(a,b,c),x),IGtQ(p,C1),IGtQ(n,C0)))),
IIntegrate(5360,Integrate(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_),x_Symbol),
    Condition(Integrate(ExpandIntegrand(Power(Subtract(Plus(a,Times(C1D2,CI,b,Log(Subtract(C1,Times(CI,Power(Times(Power(x,n),c),CN1)))))),Times(C1D2,CI,b,Log(Plus(C1,Times(CI,Power(Times(Power(x,n),c),CN1)))))),p),x),x),And(FreeQ(list(a,b,c),x),IGtQ(p,C1),IGtQ(n,C0))))
  );
}
