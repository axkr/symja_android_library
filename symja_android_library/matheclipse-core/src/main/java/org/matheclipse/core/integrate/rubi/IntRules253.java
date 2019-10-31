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
public class IntRules253 { 
  public static IAST RULES = List( 
IIntegrate(6326,Int(Times(Power(Plus(a_DEFAULT,Times(ArcCsch(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(e,Times(f,x)),m),Power(Plus(a,Times(b,ArcCsch(Plus(c,Times(d,x))))),p)),x),And(FreeQ(List(a,b,c,d,e,f,m,p),x),Not(IGtQ(p,C0))))),
IIntegrate(6327,Int(Times(Power(ArcSech(Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))),CN1))),m_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Int(Times(u,Power(ArcCosh(Plus(Times(a,Power(c,CN1)),Times(b,Power(x,n),Power(c,CN1)))),m)),x),FreeQ(List(a,b,c,n,m),x))),
IIntegrate(6328,Int(Times(Power(ArcCsch(Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))),CN1))),m_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Int(Times(u,Power(ArcSinh(Plus(Times(a,Power(c,CN1)),Times(b,Power(x,n),Power(c,CN1)))),m)),x),FreeQ(List(a,b,c,n,m),x))),
IIntegrate(6329,Int(Exp(ArcSech(Times(a_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Simp(Times(x,Exp(ArcSech(Times(a,x)))),x),Dist(Power(a,CN1),Int(Times(C1,Sqrt(Times(Subtract(C1,Times(a,x)),Power(Plus(C1,Times(a,x)),CN1))),Power(Times(x,Subtract(C1,Times(a,x))),CN1)),x),x),Simp(Times(Log(x),Power(a,CN1)),x)),FreeQ(a,x))),
IIntegrate(6330,Int(Exp(ArcSech(Times(a_DEFAULT,Power(x_,p_)))),x_Symbol),
    Condition(Plus(Simp(Times(x,Exp(ArcSech(Times(a,Power(x,p))))),x),Dist(Times(p,Power(a,CN1)),Int(Power(Power(x,p),CN1),x),x),Dist(Times(p,Sqrt(Plus(C1,Times(a,Power(x,p)))),Sqrt(Power(Plus(C1,Times(a,Power(x,p))),CN1)),Power(a,CN1)),Int(Power(Times(Power(x,p),Sqrt(Plus(C1,Times(a,Power(x,p)))),Sqrt(Subtract(C1,Times(a,Power(x,p))))),CN1),x),x)),FreeQ(List(a,p),x))),
IIntegrate(6331,Int(Exp(ArcCsch(Times(a_DEFAULT,Power(x_,p_DEFAULT)))),x_Symbol),
    Condition(Plus(Dist(Power(a,CN1),Int(Power(Power(x,p),CN1),x),x),Int(Sqrt(Plus(C1,Power(Times(Sqr(a),Power(x,Times(C2,p))),CN1))),x)),FreeQ(List(a,p),x))),
IIntegrate(6332,Int(Exp(Times(ArcSech(u_),n_DEFAULT)),x_Symbol),
    Condition(Int(Power(Plus(Power(u,CN1),Sqrt(Times(Subtract(C1,u),Power(Plus(C1,u),CN1))),Times(C1,Sqrt(Times(Subtract(C1,u),Power(Plus(C1,u),CN1))),Power(u,CN1))),n),x),IntegerQ(n))),
IIntegrate(6333,Int(Exp(Times(ArcCsch(u_),n_DEFAULT)),x_Symbol),
    Condition(Int(Power(Plus(Power(u,CN1),Sqrt(Plus(C1,Power(u,CN2)))),n),x),IntegerQ(n))),
IIntegrate(6334,Int(Times(Exp(ArcSech(Times(a_DEFAULT,Power(x_,p_DEFAULT)))),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Negate(Simp(Power(Times(a,p,Power(x,p)),CN1),x)),Dist(Times(Sqrt(Plus(C1,Times(a,Power(x,p)))),Sqrt(Power(Plus(C1,Times(a,Power(x,p))),CN1)),Power(a,CN1)),Int(Times(Sqrt(Plus(C1,Times(a,Power(x,p)))),Sqrt(Subtract(C1,Times(a,Power(x,p)))),Power(Power(x,Plus(p,C1)),CN1)),x),x)),FreeQ(List(a,p),x))),
IIntegrate(6335,Int(Times(Exp(ArcSech(Times(a_DEFAULT,Power(x_,p_DEFAULT)))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),Exp(ArcSech(Times(a,Power(x,p)))),Power(Plus(m,C1),CN1)),x),Dist(Times(p,Power(Times(a,Plus(m,C1)),CN1)),Int(Power(x,Subtract(m,p)),x),x),Dist(Times(p,Sqrt(Plus(C1,Times(a,Power(x,p)))),Sqrt(Power(Plus(C1,Times(a,Power(x,p))),CN1)),Power(Times(a,Plus(m,C1)),CN1)),Int(Times(Power(x,Subtract(m,p)),Power(Times(Sqrt(Plus(C1,Times(a,Power(x,p)))),Sqrt(Subtract(C1,Times(a,Power(x,p))))),CN1)),x),x)),And(FreeQ(List(a,m,p),x),NeQ(m,CN1)))),
IIntegrate(6336,Int(Times(Exp(ArcCsch(Times(a_DEFAULT,Power(x_,p_DEFAULT)))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Dist(Power(a,CN1),Int(Power(x,Subtract(m,p)),x),x),Int(Times(Power(x,m),Sqrt(Plus(C1,Power(Times(Sqr(a),Power(x,Times(C2,p))),CN1)))),x)),FreeQ(List(a,m,p),x))),
IIntegrate(6337,Int(Times(Exp(Times(ArcSech(u_),n_DEFAULT)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(x,m),Power(Plus(Power(u,CN1),Sqrt(Times(Subtract(C1,u),Power(Plus(C1,u),CN1))),Times(C1,Sqrt(Times(Subtract(C1,u),Power(Plus(C1,u),CN1))),Power(u,CN1))),n)),x),And(FreeQ(m,x),IntegerQ(n)))),
IIntegrate(6338,Int(Times(Exp(Times(ArcCsch(u_),n_DEFAULT)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(x,m),Power(Plus(Power(u,CN1),Sqrt(Plus(C1,Power(u,CN2)))),n)),x),And(FreeQ(m,x),IntegerQ(n)))),
IIntegrate(6339,Int(Times(Exp(ArcSech(Times(c_DEFAULT,x_))),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Plus(Dist(Power(Times(a,c),CN1),Int(Times(Sqrt(Power(Plus(C1,Times(c,x)),CN1)),Power(Times(x,Sqrt(Subtract(C1,Times(c,x)))),CN1)),x),x),Dist(Power(c,CN1),Int(Power(Times(x,Plus(a,Times(b,Sqr(x)))),CN1),x),x)),And(FreeQ(List(a,b,c),x),EqQ(Plus(b,Times(a,Sqr(c))),C0)))),
IIntegrate(6340,Int(Times(Exp(ArcCsch(Times(c_DEFAULT,x_))),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Plus(Dist(Power(Times(a,Sqr(c)),CN1),Int(Power(Times(Sqr(x),Sqrt(Plus(C1,Power(Times(Sqr(c),Sqr(x)),CN1)))),CN1),x),x),Dist(Power(c,CN1),Int(Power(Times(x,Plus(a,Times(b,Sqr(x)))),CN1),x),x)),And(FreeQ(List(a,b,c),x),EqQ(Subtract(b,Times(a,Sqr(c))),C0)))),
IIntegrate(6341,Int(Times(Exp(ArcSech(Times(c_DEFAULT,x_))),Power(Times(d_DEFAULT,x_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Plus(Dist(Times(d,Power(Times(a,c),CN1)),Int(Times(Power(Times(d,x),Subtract(m,C1)),Sqrt(Power(Plus(C1,Times(c,x)),CN1)),Power(Subtract(C1,Times(c,x)),CN1D2)),x),x),Dist(Times(d,Power(c,CN1)),Int(Times(Power(Times(d,x),Subtract(m,C1)),Power(Plus(a,Times(b,Sqr(x))),CN1)),x),x)),And(FreeQ(List(a,b,c,d,m),x),EqQ(Plus(b,Times(a,Sqr(c))),C0)))),
IIntegrate(6342,Int(Times(Exp(ArcCsch(Times(c_DEFAULT,x_))),Power(Times(d_DEFAULT,x_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Plus(Dist(Times(Sqr(d),Power(Times(a,Sqr(c)),CN1)),Int(Times(Power(Times(d,x),Subtract(m,C2)),Power(Plus(C1,Power(Times(Sqr(c),Sqr(x)),CN1)),CN1D2)),x),x),Dist(Times(d,Power(c,CN1)),Int(Times(Power(Times(d,x),Subtract(m,C1)),Power(Plus(a,Times(b,Sqr(x))),CN1)),x),x)),And(FreeQ(List(a,b,c,d,m),x),EqQ(Subtract(b,Times(a,Sqr(c))),C0)))),
IIntegrate(6343,Int(ArcSech(u_),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcSech(u)),x),Dist(Times(Sqrt(Subtract(C1,Sqr(u))),Power(Times(u,Sqrt(Plus(CN1,Power(u,CN1))),Sqrt(Plus(C1,Power(u,CN1)))),CN1)),Int(SimplifyIntegrand(Times(x,D(u,x),Power(Times(u,Sqrt(Subtract(C1,Sqr(u)))),CN1)),x),x),x)),And(InverseFunctionFreeQ(u,x),Not(FunctionOfExponentialQ(u,x))))),
IIntegrate(6344,Int(ArcCsch(u_),x_Symbol),
    Condition(Subtract(Simp(Times(x,ArcCsch(u)),x),Dist(Times(u,Power(Negate(Sqr(u)),CN1D2)),Int(SimplifyIntegrand(Times(x,D(u,x),Power(Times(u,Sqrt(Subtract(CN1,Sqr(u)))),CN1)),x),x),x)),And(InverseFunctionFreeQ(u,x),Not(FunctionOfExponentialQ(u,x))))),
IIntegrate(6345,Int(Times(Plus(a_DEFAULT,Times(ArcSech(u_),b_DEFAULT)),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Plus(a,Times(b,ArcSech(u))),Power(Times(d,Plus(m,C1)),CN1)),x),Dist(Times(b,Sqrt(Subtract(C1,Sqr(u))),Power(Times(d,Plus(m,C1),u,Sqrt(Plus(CN1,Power(u,CN1))),Sqrt(Plus(C1,Power(u,CN1)))),CN1)),Int(SimplifyIntegrand(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),D(u,x),Power(Times(u,Sqrt(Subtract(C1,Sqr(u)))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,m),x),NeQ(m,CN1),InverseFunctionFreeQ(u,x),Not(FunctionOfQ(Power(Plus(c,Times(d,x)),Plus(m,C1)),u,x)),Not(FunctionOfExponentialQ(u,x))))),
IIntegrate(6346,Int(Times(Plus(a_DEFAULT,Times(ArcCsch(u_),b_DEFAULT)),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Plus(a,Times(b,ArcCsch(u))),Power(Times(d,Plus(m,C1)),CN1)),x),Dist(Times(b,u,Power(Times(d,Plus(m,C1),Sqrt(Negate(Sqr(u)))),CN1)),Int(SimplifyIntegrand(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),D(u,x),Power(Times(u,Sqrt(Subtract(CN1,Sqr(u)))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,m),x),NeQ(m,CN1),InverseFunctionFreeQ(u,x),Not(FunctionOfQ(Power(Plus(c,Times(d,x)),Plus(m,C1)),u,x)),Not(FunctionOfExponentialQ(u,x))))),
IIntegrate(6347,Int(Times(Plus(a_DEFAULT,Times(ArcSech(u_),b_DEFAULT)),v_),x_Symbol),
    Condition(With(List(Set(w,IntHide(v,x))),Condition(Plus(Dist(Plus(a,Times(b,ArcSech(u))),w,x),Dist(Times(b,Sqrt(Subtract(C1,Sqr(u))),Power(Times(u,Sqrt(Plus(CN1,Power(u,CN1))),Sqrt(Plus(C1,Power(u,CN1)))),CN1)),Int(SimplifyIntegrand(Times(w,D(u,x),Power(Times(u,Sqrt(Subtract(C1,Sqr(u)))),CN1)),x),x),x)),InverseFunctionFreeQ(w,x))),And(FreeQ(List(a,b),x),InverseFunctionFreeQ(u,x),Not(MatchQ(v,Condition(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x)),m_DEFAULT),FreeQ(List(c,d,m),x))))))),
IIntegrate(6348,Int(Times(Plus(a_DEFAULT,Times(ArcCsch(u_),b_DEFAULT)),v_),x_Symbol),
    Condition(With(List(Set(w,IntHide(v,x))),Condition(Subtract(Dist(Plus(a,Times(b,ArcCsch(u))),w,x),Dist(Times(b,u,Power(Negate(Sqr(u)),CN1D2)),Int(SimplifyIntegrand(Times(w,D(u,x),Power(Times(u,Sqrt(Subtract(CN1,Sqr(u)))),CN1)),x),x),x)),InverseFunctionFreeQ(w,x))),And(FreeQ(List(a,b),x),InverseFunctionFreeQ(u,x),Not(MatchQ(v,Condition(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x)),m_DEFAULT),FreeQ(List(c,d,m),x))))))),
IIntegrate(6349,Int(Erf(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Simp(Times(Plus(a,Times(b,x)),Erf(Plus(a,Times(b,x))),Power(b,CN1)),x),Simp(Power(Times(b,Sqrt(Pi),Exp(Sqr(Plus(a,Times(b,x))))),CN1),x)),FreeQ(List(a,b),x))),
IIntegrate(6350,Int(Erfc(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(a,Times(b,x)),Erfc(Plus(a,Times(b,x))),Power(b,CN1)),x),Simp(Power(Times(b,Sqrt(Pi),Exp(Sqr(Plus(a,Times(b,x))))),CN1),x)),FreeQ(List(a,b),x)))
  );
}
