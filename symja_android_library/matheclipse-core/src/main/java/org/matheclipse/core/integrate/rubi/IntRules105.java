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
public class IntRules105 { 

	public static void initialize() {
		Initializer.init();
	}

	private static class Initializer  {

		private static void init() {
IIntegrate(5251,Int(ArcCsc(Plus(c_,Times(d_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Simp(Times(Plus(c,Times(d,x)),ArcCsc(Plus(c,Times(d,x))),Power(d,CN1)),x),Int(Power(Times(Plus(c,Times(d,x)),Sqrt(Subtract(C1,Power(Plus(c,Times(d,x)),CN2)))),CN1),x)),FreeQ(List(c,d),x)));
IIntegrate(5252,Int(Power(Plus(a_DEFAULT,Times(ArcSec(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Dist(Power(d,CN1),Subst(Int(Power(Plus(a,Times(b,ArcSec(x))),p),x),x,Plus(c,Times(d,x))),x),And(FreeQ(List(a,b,c,d),x),IGtQ(p,C0))));
IIntegrate(5253,Int(Power(Plus(a_DEFAULT,Times(ArcCsc(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Dist(Power(d,CN1),Subst(Int(Power(Plus(a,Times(b,ArcCsc(x))),p),x),x,Plus(c,Times(d,x))),x),And(FreeQ(List(a,b,c,d),x),IGtQ(p,C0))));
IIntegrate(5254,Int(Power(Plus(a_DEFAULT,Times(ArcSec(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_),x_Symbol),
    Condition(Unintegrable(Power(Plus(a,Times(b,ArcSec(Plus(c,Times(d,x))))),p),x),And(FreeQ(List(a,b,c,d,p),x),Not(IGtQ(p,C0)))));
IIntegrate(5255,Int(Power(Plus(a_DEFAULT,Times(ArcCsc(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_),x_Symbol),
    Condition(Unintegrable(Power(Plus(a,Times(b,ArcCsc(Plus(c,Times(d,x))))),p),x),And(FreeQ(List(a,b,c,d,p),x),Not(IGtQ(p,C0)))));
IIntegrate(5256,Int(Times(Power(Plus(a_DEFAULT,Times(ArcSec(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Dist(Power(d,CN1),Subst(Int(Times(Power(Times(f,x,Power(d,CN1)),m),Power(Plus(a,Times(b,ArcSec(x))),p)),x),x,Plus(c,Times(d,x))),x),And(FreeQ(List(a,b,c,d,e,f,m),x),EqQ(Subtract(Times(d,e),Times(c,f)),C0),IGtQ(p,C0))));
IIntegrate(5257,Int(Times(Power(Plus(a_DEFAULT,Times(ArcCsc(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Dist(Power(d,CN1),Subst(Int(Times(Power(Times(f,x,Power(d,CN1)),m),Power(Plus(a,Times(b,ArcCsc(x))),p)),x),x,Plus(c,Times(d,x))),x),And(FreeQ(List(a,b,c,d,e,f,m),x),EqQ(Subtract(Times(d,e),Times(c,f)),C0),IGtQ(p,C0))));
IIntegrate(5258,Int(Times(Power(Plus(a_DEFAULT,Times(ArcSec(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Dist(Power(Power(d,Plus(m,C1)),CN1),Subst(Int(Times(Power(Plus(a,Times(b,x)),p),Sec(x),Tan(x),Power(Plus(Times(d,e),Times(CN1,c,f),Times(f,Sec(x))),m)),x),x,ArcSec(Plus(c,Times(d,x)))),x),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(p,C0),IntegerQ(m))));
IIntegrate(5259,Int(Times(Power(Plus(a_DEFAULT,Times(ArcCsc(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Negate(Dist(Power(Power(d,Plus(m,C1)),CN1),Subst(Int(Times(Power(Plus(a,Times(b,x)),p),Csc(x),Cot(x),Power(Plus(Times(d,e),Times(CN1,c,f),Times(f,Csc(x))),m)),x),x,ArcCsc(Plus(c,Times(d,x)))),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(p,C0),IntegerQ(m))));
IIntegrate(5260,Int(Times(Power(Plus(a_DEFAULT,Times(ArcSec(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Dist(Power(d,CN1),Subst(Int(Times(Power(Plus(Times(Subtract(Times(d,e),Times(c,f)),Power(d,CN1)),Times(f,x,Power(d,CN1))),m),Power(Plus(a,Times(b,ArcSec(x))),p)),x),x,Plus(c,Times(d,x))),x),And(FreeQ(List(a,b,c,d,e,f,m),x),IGtQ(p,C0))));
IIntegrate(5261,Int(Times(Power(Plus(a_DEFAULT,Times(ArcCsc(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Dist(Power(d,CN1),Subst(Int(Times(Power(Plus(Times(Subtract(Times(d,e),Times(c,f)),Power(d,CN1)),Times(f,x,Power(d,CN1))),m),Power(Plus(a,Times(b,ArcCsc(x))),p)),x),x,Plus(c,Times(d,x))),x),And(FreeQ(List(a,b,c,d,e,f,m),x),IGtQ(p,C0))));
IIntegrate(5262,Int(Times(Power(Plus(a_DEFAULT,Times(ArcSec(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(e,Times(f,x)),m),Power(Plus(a,Times(b,ArcSec(Plus(c,Times(d,x))))),p)),x),And(FreeQ(List(a,b,c,d,e,f,m,p),x),Not(IGtQ(p,C0)))));
IIntegrate(5263,Int(Times(Power(Plus(a_DEFAULT,Times(ArcCsc(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(e,Times(f,x)),m),Power(Plus(a,Times(b,ArcCsc(Plus(c,Times(d,x))))),p)),x),And(FreeQ(List(a,b,c,d,e,f,m,p),x),Not(IGtQ(p,C0)))));
IIntegrate(5264,Int(Times(Power(ArcSec(Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))),CN1))),m_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Int(Times(u,Power(ArcCos(Plus(Times(a,Power(c,CN1)),Times(b,Power(x,n),Power(c,CN1)))),m)),x),FreeQ(List(a,b,c,n,m),x)));
IIntegrate(5265,Int(Times(Power(ArcCsc(Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))),CN1))),m_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Int(Times(u,Power(ArcSin(Plus(Times(a,Power(c,CN1)),Times(b,Power(x,n),Power(c,CN1)))),m)),x),FreeQ(List(a,b,c,n,m),x)));
IIntegrate(5266,Int(Times(u_DEFAULT,Power(f_,Times(Power(ArcSec(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),c_DEFAULT))),x_Symbol),
    Condition(Dist(Power(b,CN1),Subst(Int(Times(ReplaceAll(u,Rule(x,Plus(Times(CN1,a,Power(b,CN1)),Times(Sec(x),Power(b,CN1))))),Power(f,Times(c,Power(x,n))),Sec(x),Tan(x)),x),x,ArcSec(Plus(a,Times(b,x)))),x),And(FreeQ(List(a,b,c,f),x),IGtQ(n,C0))));
IIntegrate(5267,Int(Times(u_DEFAULT,Power(f_,Times(Power(ArcCsc(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),c_DEFAULT))),x_Symbol),
    Condition(Negate(Dist(Power(b,CN1),Subst(Int(Times(ReplaceAll(u,Rule(x,Plus(Times(CN1,a,Power(b,CN1)),Times(Csc(x),Power(b,CN1))))),Power(f,Times(c,Power(x,n))),Csc(x),Cot(x)),x),x,ArcCsc(Plus(a,Times(b,x)))),x)),And(FreeQ(List(a,b,c,f),x),IGtQ(n,C0))));
IIntegrate(5268,Int(ArcSec(u_),x_Symbol),
    Condition(Subtract(Simp(Times(x,ArcSec(u)),x),Dist(Times(u,Power(Sqr(u),CN1D2)),Int(SimplifyIntegrand(Times(x,D(u,x),Power(Times(u,Sqrt(Subtract(Sqr(u),C1))),CN1)),x),x),x)),And(InverseFunctionFreeQ(u,x),Not(FunctionOfExponentialQ(u,x)))));
IIntegrate(5269,Int(ArcCsc(u_),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcCsc(u)),x),Dist(Times(u,Power(Sqr(u),CN1D2)),Int(SimplifyIntegrand(Times(x,D(u,x),Power(Times(u,Sqrt(Subtract(Sqr(u),C1))),CN1)),x),x),x)),And(InverseFunctionFreeQ(u,x),Not(FunctionOfExponentialQ(u,x)))));
IIntegrate(5270,Int(Times(Plus(a_DEFAULT,Times(ArcSec(u_),b_DEFAULT)),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Plus(a,Times(b,ArcSec(u))),Power(Times(d,Plus(m,C1)),CN1)),x),Dist(Times(b,u,Power(Times(d,Plus(m,C1),Sqrt(Sqr(u))),CN1)),Int(SimplifyIntegrand(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),D(u,x),Power(Times(u,Sqrt(Subtract(Sqr(u),C1))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,m),x),NeQ(m,CN1),InverseFunctionFreeQ(u,x),Not(FunctionOfQ(Power(Plus(c,Times(d,x)),Plus(m,C1)),u,x)),Not(FunctionOfExponentialQ(u,x)))));
IIntegrate(5271,Int(Times(Plus(a_DEFAULT,Times(ArcCsc(u_),b_DEFAULT)),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Plus(a,Times(b,ArcCsc(u))),Power(Times(d,Plus(m,C1)),CN1)),x),Dist(Times(b,u,Power(Times(d,Plus(m,C1),Sqrt(Sqr(u))),CN1)),Int(SimplifyIntegrand(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),D(u,x),Power(Times(u,Sqrt(Subtract(Sqr(u),C1))),CN1)),x),x),x)),And(FreeQ(List(a,b,c,d,m),x),NeQ(m,CN1),InverseFunctionFreeQ(u,x),Not(FunctionOfQ(Power(Plus(c,Times(d,x)),Plus(m,C1)),u,x)),Not(FunctionOfExponentialQ(u,x)))));
IIntegrate(5272,Int(Times(Plus(a_DEFAULT,Times(ArcSec(u_),b_DEFAULT)),v_),x_Symbol),
    Condition(With(List(Set(w,IntHide(v,x))),Condition(Subtract(Dist(Plus(a,Times(b,ArcSec(u))),w,x),Dist(Times(b,u,Power(Sqr(u),CN1D2)),Int(SimplifyIntegrand(Times(w,D(u,x),Power(Times(u,Sqrt(Subtract(Sqr(u),C1))),CN1)),x),x),x)),InverseFunctionFreeQ(w,x))),And(FreeQ(List(a,b),x),InverseFunctionFreeQ(u,x),Not(MatchQ(v,Condition(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x)),m_DEFAULT),FreeQ(List(c,d,m),x)))))));
IIntegrate(5273,Int(Times(Plus(a_DEFAULT,Times(ArcCsc(u_),b_DEFAULT)),v_),x_Symbol),
    Condition(With(List(Set(w,IntHide(v,x))),Condition(Plus(Dist(Plus(a,Times(b,ArcCsc(u))),w,x),Dist(Times(b,u,Power(Sqr(u),CN1D2)),Int(SimplifyIntegrand(Times(w,D(u,x),Power(Times(u,Sqrt(Subtract(Sqr(u),C1))),CN1)),x),x),x)),InverseFunctionFreeQ(w,x))),And(FreeQ(List(a,b),x),InverseFunctionFreeQ(u,x),Not(MatchQ(v,Condition(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x)),m_DEFAULT),FreeQ(List(c,d,m),x)))))));
IIntegrate(5274,Int(Times(Power(u_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(v_))),n_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(ExpandToSum(u,x),m),Power(Plus(a,Times(b,Sinh(ExpandToSum(v,x)))),n)),x),And(FreeQ(List(a,b,m,n),x),LinearQ(List(u,v),x),Not(LinearMatchQ(List(u,v),x)))));
IIntegrate(5275,Int(Times(Power(Plus(a_DEFAULT,Times(Cosh(v_),b_DEFAULT)),n_DEFAULT),Power(u_,m_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(ExpandToSum(u,x),m),Power(Plus(a,Times(b,Cosh(ExpandToSum(v,x)))),n)),x),And(FreeQ(List(a,b,m,n),x),LinearQ(List(u,v),x),Not(LinearMatchQ(List(u,v),x)))));
IIntegrate(5276,Int(Times(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_DEFAULT),Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Int(ExpandIntegrand(Sinh(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Power(x,n))),p),x),x),And(FreeQ(List(a,b,c,d,n),x),IGtQ(p,C0))));
IIntegrate(5277,Int(Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_DEFAULT)),x_Symbol),
    Condition(Int(ExpandIntegrand(Cosh(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Power(x,n))),p),x),x),And(FreeQ(List(a,b,c,d,n),x),IGtQ(p,C0))));
IIntegrate(5278,Int(Times(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(Negate(n),C1)),Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Sinh(Plus(c,Times(d,x))),Power(Times(b,n,Plus(p,C1)),CN1)),x),Negate(Dist(Times(Plus(Negate(n),C1),Power(Times(b,n,Plus(p,C1)),CN1)),Int(Times(Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Sinh(Plus(c,Times(d,x))),Power(Power(x,n),CN1)),x),x)),Negate(Dist(Times(d,Power(Times(b,n,Plus(p,C1)),CN1)),Int(Times(Power(x,Plus(Negate(n),C1)),Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Cosh(Plus(c,Times(d,x)))),x),x))),And(FreeQ(List(a,b,c,d),x),IntegerQ(p),IGtQ(n,C0),LtQ(p,CN1),GtQ(n,C2))));
IIntegrate(5279,Int(Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(Negate(n),C1)),Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Cosh(Plus(c,Times(d,x))),Power(Times(b,n,Plus(p,C1)),CN1)),x),Negate(Dist(Times(Plus(Negate(n),C1),Power(Times(b,n,Plus(p,C1)),CN1)),Int(Times(Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Cosh(Plus(c,Times(d,x))),Power(Power(x,n),CN1)),x),x)),Negate(Dist(Times(d,Power(Times(b,n,Plus(p,C1)),CN1)),Int(Times(Power(x,Plus(Negate(n),C1)),Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Sinh(Plus(c,Times(d,x)))),x),x))),And(FreeQ(List(a,b,c,d),x),IntegerQ(p),IGtQ(n,C0),LtQ(p,CN1),GtQ(n,C2))));
IIntegrate(5280,Int(Times(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Int(ExpandIntegrand(Sinh(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Power(x,n))),p),x),x),And(FreeQ(List(a,b,c,d),x),ILtQ(p,C0),IGtQ(n,C0),Or(EqQ(n,C2),EqQ(p,CN1)))));
IIntegrate(5281,Int(Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_)),x_Symbol),
    Condition(Int(ExpandIntegrand(Cosh(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Power(x,n))),p),x),x),And(FreeQ(List(a,b,c,d),x),ILtQ(p,C0),IGtQ(n,C0),Or(EqQ(n,C2),EqQ(p,CN1)))));
IIntegrate(5282,Int(Times(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Int(Times(Power(x,Times(n,p)),Power(Plus(b,Times(a,Power(Power(x,n),CN1))),p),Sinh(Plus(c,Times(d,x)))),x),And(FreeQ(List(a,b,c,d),x),ILtQ(p,C0),ILtQ(n,C0))));
IIntegrate(5283,Int(Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_)),x_Symbol),
    Condition(Int(Times(Power(x,Times(n,p)),Power(Plus(b,Times(a,Power(Power(x,n),CN1))),p),Cosh(Plus(c,Times(d,x)))),x),And(FreeQ(List(a,b,c,d),x),ILtQ(p,C0),ILtQ(n,C0))));
IIntegrate(5284,Int(Times(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(a,Times(b,Power(x,n))),p),Sinh(Plus(c,Times(d,x)))),x),FreeQ(List(a,b,c,d,n,p),x)));
IIntegrate(5285,Int(Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(a,Times(b,Power(x,n))),p),Cosh(Plus(c,Times(d,x)))),x),FreeQ(List(a,b,c,d,n,p),x)));
IIntegrate(5286,Int(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_DEFAULT),Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Int(ExpandIntegrand(Sinh(Plus(c,Times(d,x))),Times(Power(Times(e,x),m),Power(Plus(a,Times(b,Power(x,n))),p)),x),x),And(FreeQ(List(a,b,c,d,e,m,n),x),IGtQ(p,C0))));
IIntegrate(5287,Int(Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_DEFAULT)),x_Symbol),
    Condition(Int(ExpandIntegrand(Cosh(Plus(c,Times(d,x))),Times(Power(Times(e,x),m),Power(Plus(a,Times(b,Power(x,n))),p)),x),x),And(FreeQ(List(a,b,c,d,e,m,n),x),IGtQ(p,C0))));
IIntegrate(5288,Int(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(Power(e,m),Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Sinh(Plus(c,Times(d,x))),Power(Times(b,n,Plus(p,C1)),CN1)),x),Dist(Times(d,Power(e,m),Power(Times(b,n,Plus(p,C1)),CN1)),Int(Times(Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Cosh(Plus(c,Times(d,x)))),x),x)),And(FreeQ(List(a,b,c,d,e,m,n),x),IntegerQ(p),EqQ(Plus(m,Negate(n),C1),C0),LtQ(p,CN1),Or(IntegerQ(n),GtQ(e,C0)))));
IIntegrate(5289,Int(Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(e,m),Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Cosh(Plus(c,Times(d,x))),Power(Times(b,n,Plus(p,C1)),CN1)),x),Dist(Times(d,Power(e,m),Power(Times(b,n,Plus(p,C1)),CN1)),Int(Times(Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Sinh(Plus(c,Times(d,x)))),x),x)),And(FreeQ(List(a,b,c,d,e,m,n),x),IntegerQ(p),EqQ(Plus(m,Negate(n),C1),C0),LtQ(p,CN1),Or(IntegerQ(n),GtQ(e,C0)))));
IIntegrate(5290,Int(Times(Power(x_,m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,Negate(n),C1)),Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Sinh(Plus(c,Times(d,x))),Power(Times(b,n,Plus(p,C1)),CN1)),x),Negate(Dist(Times(Plus(m,Negate(n),C1),Power(Times(b,n,Plus(p,C1)),CN1)),Int(Times(Power(x,Subtract(m,n)),Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Sinh(Plus(c,Times(d,x)))),x),x)),Negate(Dist(Times(d,Power(Times(b,n,Plus(p,C1)),CN1)),Int(Times(Power(x,Plus(m,Negate(n),C1)),Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Cosh(Plus(c,Times(d,x)))),x),x))),And(FreeQ(List(a,b,c,d),x),ILtQ(p,CN1),IGtQ(n,C0),RationalQ(m),Or(GtQ(Plus(m,Negate(n),C1),C0),GtQ(n,C2)))));
IIntegrate(5291,Int(Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Power(x_,m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,Negate(n),C1)),Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Cosh(Plus(c,Times(d,x))),Power(Times(b,n,Plus(p,C1)),CN1)),x),Negate(Dist(Times(Plus(m,Negate(n),C1),Power(Times(b,n,Plus(p,C1)),CN1)),Int(Times(Power(x,Subtract(m,n)),Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Cosh(Plus(c,Times(d,x)))),x),x)),Negate(Dist(Times(d,Power(Times(b,n,Plus(p,C1)),CN1)),Int(Times(Power(x,Plus(m,Negate(n),C1)),Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Sinh(Plus(c,Times(d,x)))),x),x))),And(FreeQ(List(a,b,c,d),x),ILtQ(p,CN1),IGtQ(n,C0),RationalQ(m),Or(GtQ(Plus(m,Negate(n),C1),C0),GtQ(n,C2)))));
IIntegrate(5292,Int(Times(Power(x_,m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Int(ExpandIntegrand(Sinh(Plus(c,Times(d,x))),Times(Power(x,m),Power(Plus(a,Times(b,Power(x,n))),p)),x),x),And(FreeQ(List(a,b,c,d),x),ILtQ(p,C0),IntegerQ(m),IGtQ(n,C0),Or(EqQ(n,C2),EqQ(p,CN1)))));
IIntegrate(5293,Int(Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Power(x_,m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_)),x_Symbol),
    Condition(Int(ExpandIntegrand(Cosh(Plus(c,Times(d,x))),Times(Power(x,m),Power(Plus(a,Times(b,Power(x,n))),p)),x),x),And(FreeQ(List(a,b,c,d),x),ILtQ(p,C0),IntegerQ(m),IGtQ(n,C0),Or(EqQ(n,C2),EqQ(p,CN1)))));
IIntegrate(5294,Int(Times(Power(x_,m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Int(Times(Power(x,Plus(m,Times(n,p))),Power(Plus(b,Times(a,Power(Power(x,n),CN1))),p),Sinh(Plus(c,Times(d,x)))),x),And(FreeQ(List(a,b,c,d,m),x),ILtQ(p,C0),ILtQ(n,C0))));
IIntegrate(5295,Int(Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Power(x_,m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_)),x_Symbol),
    Condition(Int(Times(Power(x,Plus(m,Times(n,p))),Power(Plus(b,Times(a,Power(Power(x,n),CN1))),p),Cosh(Plus(c,Times(d,x)))),x),And(FreeQ(List(a,b,c,d,m),x),ILtQ(p,C0),ILtQ(n,C0))));
IIntegrate(5296,Int(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_DEFAULT),Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(e,x),m),Power(Plus(a,Times(b,Power(x,n))),p),Sinh(Plus(c,Times(d,x)))),x),FreeQ(List(a,b,c,d,e,m,n,p),x)));
IIntegrate(5297,Int(Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Times(e,x),m),Power(Plus(a,Times(b,Power(x,n))),p),Cosh(Plus(c,Times(d,x)))),x),FreeQ(List(a,b,c,d,e,m,n,p),x)));
IIntegrate(5298,Int(Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),x_Symbol),
    Condition(Subtract(Dist(C1D2,Int(Exp(Plus(c,Times(d,Power(x,n)))),x),x),Dist(C1D2,Int(Exp(Subtract(Negate(c),Times(d,Power(x,n)))),x),x)),And(FreeQ(List(c,d),x),IGtQ(n,C1))));
IIntegrate(5299,Int(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),x_Symbol),
    Condition(Plus(Dist(C1D2,Int(Exp(Plus(c,Times(d,Power(x,n)))),x),x),Dist(C1D2,Int(Exp(Subtract(Negate(c),Times(d,Power(x,n)))),x),x)),And(FreeQ(List(c,d),x),IGtQ(n,C1))));
IIntegrate(5300,Int(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_),x_Symbol),
    Condition(Int(ExpandTrigReduce(Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,Power(x,n)))))),p),x),x),And(FreeQ(List(a,b,c,d),x),IGtQ(n,C1),IGtQ(p,C1))));
  }
}
}
