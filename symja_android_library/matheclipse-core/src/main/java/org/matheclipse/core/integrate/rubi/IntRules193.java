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
public class IntRules193 { 
  public static IAST RULES = List( 
IIntegrate(4826,Int(Power(Plus(a_DEFAULT,Times(ArcCos(Plus(C1,Times(d_DEFAULT,Sqr(x_)))),b_DEFAULT)),CN2),x_Symbol),
    Condition(Plus(Simp(Times(Sqrt(Subtract(Times(CN2,d,Sqr(x)),Times(Sqr(d),Power(x,C4)))),Power(Times(C2,b,d,x,Plus(a,Times(b,ArcCos(Plus(C1,Times(d,Sqr(x))))))),CN1)),x),Simp(Times(x,Sin(Times(a,Power(Times(C2,b),CN1))),CosIntegral(Times(Plus(a,Times(b,ArcCos(Plus(C1,Times(d,Sqr(x)))))),Power(Times(C2,b),CN1))),Power(Times(C2,CSqrt2,Sqr(b),Sqrt(Times(CN1,d,Sqr(x)))),CN1)),x),Negate(Simp(Times(x,Cos(Times(a,Power(Times(C2,b),CN1))),SinIntegral(Times(Plus(a,Times(b,ArcCos(Plus(C1,Times(d,Sqr(x)))))),Power(Times(C2,b),CN1))),Power(Times(C2,CSqrt2,Sqr(b),Sqrt(Times(CN1,d,Sqr(x)))),CN1)),x))),FreeQ(List(a,b,d),x))),
IIntegrate(4827,Int(Power(Plus(a_DEFAULT,Times(ArcCos(Plus(CN1,Times(d_DEFAULT,Sqr(x_)))),b_DEFAULT)),CN2),x_Symbol),
    Condition(Plus(Simp(Times(Sqrt(Subtract(Times(C2,d,Sqr(x)),Times(Sqr(d),Power(x,C4)))),Power(Times(C2,b,d,x,Plus(a,Times(b,ArcCos(Plus(CN1,Times(d,Sqr(x))))))),CN1)),x),Negate(Simp(Times(x,Cos(Times(a,Power(Times(C2,b),CN1))),CosIntegral(Times(Plus(a,Times(b,ArcCos(Plus(CN1,Times(d,Sqr(x)))))),Power(Times(C2,b),CN1))),Power(Times(C2,CSqrt2,Sqr(b),Sqrt(Times(d,Sqr(x)))),CN1)),x)),Negate(Simp(Times(x,Sin(Times(a,Power(Times(C2,b),CN1))),SinIntegral(Times(Plus(a,Times(b,ArcCos(Plus(CN1,Times(d,Sqr(x)))))),Power(Times(C2,b),CN1))),Power(Times(C2,CSqrt2,Sqr(b),Sqrt(Times(d,Sqr(x)))),CN1)),x))),FreeQ(List(a,b,d),x))),
IIntegrate(4828,Int(Power(Plus(a_DEFAULT,Times(ArcSin(Plus(c_,Times(d_DEFAULT,Sqr(x_)))),b_DEFAULT)),n_),x_Symbol),
    Condition(Plus(Simp(Times(x,Power(Plus(a,Times(b,ArcSin(Plus(c,Times(d,Sqr(x)))))),Plus(n,C2)),Power(Times(C4,Sqr(b),Plus(n,C1),Plus(n,C2)),CN1)),x),Negate(Dist(Power(Times(C4,Sqr(b),Plus(n,C1),Plus(n,C2)),CN1),Int(Power(Plus(a,Times(b,ArcSin(Plus(c,Times(d,Sqr(x)))))),Plus(n,C2)),x),x)),Simp(Times(Sqrt(Subtract(Times(CN2,c,d,Sqr(x)),Times(Sqr(d),Power(x,C4)))),Power(Plus(a,Times(b,ArcSin(Plus(c,Times(d,Sqr(x)))))),Plus(n,C1)),Power(Times(C2,b,d,Plus(n,C1),x),CN1)),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Sqr(c),C1),LtQ(n,CN1),NeQ(n,CN2)))),
IIntegrate(4829,Int(Power(Plus(a_DEFAULT,Times(ArcCos(Plus(c_,Times(d_DEFAULT,Sqr(x_)))),b_DEFAULT)),n_),x_Symbol),
    Condition(Plus(Simp(Times(x,Power(Plus(a,Times(b,ArcCos(Plus(c,Times(d,Sqr(x)))))),Plus(n,C2)),Power(Times(C4,Sqr(b),Plus(n,C1),Plus(n,C2)),CN1)),x),Negate(Dist(Power(Times(C4,Sqr(b),Plus(n,C1),Plus(n,C2)),CN1),Int(Power(Plus(a,Times(b,ArcCos(Plus(c,Times(d,Sqr(x)))))),Plus(n,C2)),x),x)),Negate(Simp(Times(Sqrt(Subtract(Times(CN2,c,d,Sqr(x)),Times(Sqr(d),Power(x,C4)))),Power(Plus(a,Times(b,ArcCos(Plus(c,Times(d,Sqr(x)))))),Plus(n,C1)),Power(Times(C2,b,d,Plus(n,C1),x),CN1)),x))),And(FreeQ(List(a,b,c,d),x),EqQ(Sqr(c),C1),LtQ(n,CN1),NeQ(n,CN2)))),
IIntegrate(4830,Int(Times(Power(ArcSin(Times(a_DEFAULT,Power(x_,p_))),n_DEFAULT),Power(x_,CN1)),x_Symbol),
    Condition(Dist(Power(p,CN1),Subst(Int(Times(Power(x,n),Cot(x)),x),x,ArcSin(Times(a,Power(x,p)))),x),And(FreeQ(List(a,p),x),IGtQ(n,C0)))),
IIntegrate(4831,Int(Times(Power(ArcCos(Times(a_DEFAULT,Power(x_,p_))),n_DEFAULT),Power(x_,CN1)),x_Symbol),
    Condition(Negate(Dist(Power(p,CN1),Subst(Int(Times(Power(x,n),Tan(x)),x),x,ArcCos(Times(a,Power(x,p)))),x)),And(FreeQ(List(a,p),x),IGtQ(n,C0)))),
IIntegrate(4832,Int(Times(Power(ArcSin(Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))),CN1))),m_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Int(Times(u,Power(ArcCsc(Plus(Times(a,Power(c,CN1)),Times(b,Power(x,n),Power(c,CN1)))),m)),x),FreeQ(List(a,b,c,n,m),x))),
IIntegrate(4833,Int(Times(Power(ArcCos(Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))),CN1))),m_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Int(Times(u,Power(ArcSec(Plus(Times(a,Power(c,CN1)),Times(b,Power(x,n),Power(c,CN1)))),m)),x),FreeQ(List(a,b,c,n,m),x))),
IIntegrate(4834,Int(Times(Power(ArcSin(Sqrt(Plus(C1,Times(b_DEFAULT,Sqr(x_))))),n_DEFAULT),Power(Plus(C1,Times(b_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Dist(Times(Sqrt(Times(CN1,b,Sqr(x))),Power(Times(b,x),CN1)),Subst(Int(Times(Power(ArcSin(x),n),Power(Subtract(C1,Sqr(x)),CN1D2)),x),x,Sqrt(Plus(C1,Times(b,Sqr(x))))),x),FreeQ(List(b,n),x))),
IIntegrate(4835,Int(Times(Power(ArcCos(Sqrt(Plus(C1,Times(b_DEFAULT,Sqr(x_))))),n_DEFAULT),Power(Plus(C1,Times(b_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Dist(Times(Sqrt(Times(CN1,b,Sqr(x))),Power(Times(b,x),CN1)),Subst(Int(Times(Power(ArcCos(x),n),Power(Subtract(C1,Sqr(x)),CN1D2)),x),x,Sqrt(Plus(C1,Times(b,Sqr(x))))),x),FreeQ(List(b,n),x))),
IIntegrate(4836,Int(Times(u_DEFAULT,Power(f_,Times(Power(ArcSin(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),c_DEFAULT))),x_Symbol),
    Condition(Dist(Power(b,CN1),Subst(Int(Times(ReplaceAll(u,Rule(x,Plus(Times(CN1,a,Power(b,CN1)),Times(Sin(x),Power(b,CN1))))),Power(f,Times(c,Power(x,n))),Cos(x)),x),x,ArcSin(Plus(a,Times(b,x)))),x),And(FreeQ(List(a,b,c,f),x),IGtQ(n,C0)))),
IIntegrate(4837,Int(Times(u_DEFAULT,Power(f_,Times(Power(ArcCos(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),c_DEFAULT))),x_Symbol),
    Condition(Negate(Dist(Power(b,CN1),Subst(Int(Times(ReplaceAll(u,Rule(x,Plus(Times(CN1,a,Power(b,CN1)),Times(Cos(x),Power(b,CN1))))),Power(f,Times(c,Power(x,n))),Sin(x)),x),x,ArcCos(Plus(a,Times(b,x)))),x)),And(FreeQ(List(a,b,c,f),x),IGtQ(n,C0)))),
IIntegrate(4838,Int(ArcSin(Plus(Times(a_DEFAULT,Sqr(x_)),Times(b_DEFAULT,Sqrt(Plus(c_,Times(d_DEFAULT,Sqr(x_))))))),x_Symbol),
    Condition(Subtract(Simp(Times(x,ArcSin(Plus(Times(a,Sqr(x)),Times(b,Sqrt(Plus(c,Times(d,Sqr(x)))))))),x),Dist(Times(x,Sqrt(Plus(Times(Sqr(b),d),Times(Sqr(a),Sqr(x)),Times(C2,a,b,Sqrt(Plus(c,Times(d,Sqr(x))))))),Power(Times(CN1,Sqr(x),Plus(Times(Sqr(b),d),Times(Sqr(a),Sqr(x)),Times(C2,a,b,Sqrt(Plus(c,Times(d,Sqr(x))))))),CN1D2)),Int(Times(x,Plus(Times(b,d),Times(C2,a,Sqrt(Plus(c,Times(d,Sqr(x)))))),Power(Times(Sqrt(Plus(c,Times(d,Sqr(x)))),Sqrt(Plus(Times(Sqr(b),d),Times(Sqr(a),Sqr(x)),Times(C2,a,b,Sqrt(Plus(c,Times(d,Sqr(x)))))))),CN1)),x),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Times(Sqr(b),c),C1)))),
IIntegrate(4839,Int(ArcCos(Plus(Times(a_DEFAULT,Sqr(x_)),Times(b_DEFAULT,Sqrt(Plus(c_,Times(d_DEFAULT,Sqr(x_))))))),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcCos(Plus(Times(a,Sqr(x)),Times(b,Sqrt(Plus(c,Times(d,Sqr(x)))))))),x),Dist(Times(x,Sqrt(Plus(Times(Sqr(b),d),Times(Sqr(a),Sqr(x)),Times(C2,a,b,Sqrt(Plus(c,Times(d,Sqr(x))))))),Power(Times(CN1,Sqr(x),Plus(Times(Sqr(b),d),Times(Sqr(a),Sqr(x)),Times(C2,a,b,Sqrt(Plus(c,Times(d,Sqr(x))))))),CN1D2)),Int(Times(x,Plus(Times(b,d),Times(C2,a,Sqrt(Plus(c,Times(d,Sqr(x)))))),Power(Times(Sqrt(Plus(c,Times(d,Sqr(x)))),Sqrt(Plus(Times(Sqr(b),d),Times(Sqr(a),Sqr(x)),Times(C2,a,b,Sqrt(Plus(c,Times(d,Sqr(x)))))))),CN1)),x),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Times(Sqr(b),c),C1)))),
IIntegrate(4840,Int(ArcSin(u_),x_Symbol),
    Condition(Subtract(Simp(Times(x,ArcSin(u)),x),Int(SimplifyIntegrand(Times(x,D(u,x),Power(Subtract(C1,Sqr(u)),CN1D2)),x),x)),And(InverseFunctionFreeQ(u,x),Not(FunctionOfExponentialQ(u,x))))),
IIntegrate(4841,Int(ArcCos(u_),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcCos(u)),x),Int(SimplifyIntegrand(Times(x,D(u,x),Power(Subtract(C1,Sqr(u)),CN1D2)),x),x)),And(InverseFunctionFreeQ(u,x),Not(FunctionOfExponentialQ(u,x))))),
IIntegrate(4842,Int(Times(Plus(a_DEFAULT,Times(ArcSin(u_),b_DEFAULT)),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Plus(a,Times(b,ArcSin(u))),Power(Times(d,Plus(m,C1)),CN1)),x),Dist(Times(b,Power(Times(d,Plus(m,C1)),CN1)),Int(SimplifyIntegrand(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),D(u,x),Power(Subtract(C1,Sqr(u)),CN1D2)),x),x),x)),And(FreeQ(List(a,b,c,d,m),x),NeQ(m,CN1),InverseFunctionFreeQ(u,x),Not(FunctionOfQ(Power(Plus(c,Times(d,x)),Plus(m,C1)),u,x)),Not(FunctionOfExponentialQ(u,x))))),
IIntegrate(4843,Int(Times(Plus(a_DEFAULT,Times(ArcCos(u_),b_DEFAULT)),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Plus(a,Times(b,ArcCos(u))),Power(Times(d,Plus(m,C1)),CN1)),x),Dist(Times(b,Power(Times(d,Plus(m,C1)),CN1)),Int(SimplifyIntegrand(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),D(u,x),Power(Subtract(C1,Sqr(u)),CN1D2)),x),x),x)),And(FreeQ(List(a,b,c,d,m),x),NeQ(m,CN1),InverseFunctionFreeQ(u,x),Not(FunctionOfQ(Power(Plus(c,Times(d,x)),Plus(m,C1)),u,x)),Not(FunctionOfExponentialQ(u,x))))),
IIntegrate(4844,Int(Times(Plus(a_DEFAULT,Times(ArcSin(u_),b_DEFAULT)),v_),x_Symbol),
    Condition(With(List(Set(w,IntHide(v,x))),Condition(Subtract(Dist(Plus(a,Times(b,ArcSin(u))),w,x),Dist(b,Int(SimplifyIntegrand(Times(w,D(u,x),Power(Subtract(C1,Sqr(u)),CN1D2)),x),x),x)),InverseFunctionFreeQ(w,x))),And(FreeQ(List(a,b),x),InverseFunctionFreeQ(u,x),Not(MatchQ(v,Condition(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x)),m_DEFAULT),FreeQ(List(c,d,m),x))))))),
IIntegrate(4845,Int(Times(Plus(a_DEFAULT,Times(ArcCos(u_),b_DEFAULT)),v_),x_Symbol),
    Condition(With(List(Set(w,IntHide(v,x))),Condition(Plus(Dist(Plus(a,Times(b,ArcCos(u))),w,x),Dist(b,Int(SimplifyIntegrand(Times(w,D(u,x),Power(Subtract(C1,Sqr(u)),CN1D2)),x),x),x)),InverseFunctionFreeQ(w,x))),And(FreeQ(List(a,b),x),InverseFunctionFreeQ(u,x),Not(MatchQ(v,Condition(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x)),m_DEFAULT),FreeQ(List(c,d,m),x))))))),
IIntegrate(4846,Int(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Subtract(Simp(Times(x,Power(Plus(a,Times(b,ArcTan(Times(c,x)))),p)),x),Dist(Times(b,c,p),Int(Times(x,Power(Plus(a,Times(b,ArcTan(Times(c,x)))),Subtract(p,C1)),Power(Plus(C1,Times(Sqr(c),Sqr(x))),CN1)),x),x)),And(FreeQ(List(a,b,c),x),IGtQ(p,C0)))),
IIntegrate(4847,Int(Power(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Plus(Simp(Times(x,Power(Plus(a,Times(b,ArcCot(Times(c,x)))),p)),x),Dist(Times(b,c,p),Int(Times(x,Power(Plus(a,Times(b,ArcCot(Times(c,x)))),Subtract(p,C1)),Power(Plus(C1,Times(Sqr(c),Sqr(x))),CN1)),x),x)),And(FreeQ(List(a,b,c),x),IGtQ(p,C0)))),
IIntegrate(4848,Int(Times(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Simp(Times(a,Log(x)),x),Dist(Times(C1D2,CI,b),Int(Times(Log(Subtract(C1,Times(CI,c,x))),Power(x,CN1)),x),x),Negate(Dist(Times(C1D2,CI,b),Int(Times(Log(Plus(C1,Times(CI,c,x))),Power(x,CN1)),x),x))),FreeQ(List(a,b,c),x))),
IIntegrate(4849,Int(Times(Plus(a_DEFAULT,Times(ArcCot(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Simp(Times(a,Log(x)),x),Dist(Times(C1D2,CI,b),Int(Times(Log(Subtract(C1,Times(CI,Power(Times(c,x),CN1)))),Power(x,CN1)),x),x),Negate(Dist(Times(C1D2,CI,b),Int(Times(Log(Plus(C1,Times(CI,Power(Times(c,x),CN1)))),Power(x,CN1)),x),x))),FreeQ(List(a,b,c),x))),
IIntegrate(4850,Int(Times(Power(Plus(a_DEFAULT,Times(ArcTan(Times(c_DEFAULT,x_)),b_DEFAULT)),p_),Power(x_,CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(C2,Power(Plus(a,Times(b,ArcTan(Times(c,x)))),p),ArcTanh(Subtract(C1,Times(C2,Power(Plus(C1,Times(CI,c,x)),CN1))))),x),Dist(Times(C2,b,c,p),Int(Times(Power(Plus(a,Times(b,ArcTan(Times(c,x)))),Subtract(p,C1)),ArcTanh(Subtract(C1,Times(C2,Power(Plus(C1,Times(CI,c,x)),CN1)))),Power(Plus(C1,Times(Sqr(c),Sqr(x))),CN1)),x),x)),And(FreeQ(List(a,b,c),x),IGtQ(p,C1))))
  );
}
