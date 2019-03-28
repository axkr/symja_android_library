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
public class IntRules126 { 

	public static void initialize() {
		Initializer.init();
	}

	private static class Initializer  {

		private static void init() {
IIntegrate(6301,Int(Times(Plus(a_DEFAULT,Times(ArcSech(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Times(f_DEFAULT,x_),m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(With(List(Set(u,IntHide(Times(Power(Times(f,x),m),Power(Plus(d,Times(e,Sqr(x))),p)),x))),Plus(Dist(Plus(a,Times(b,ArcSech(Times(c,x)))),u,x),Dist(Times(b,Sqrt(Plus(C1,Times(c,x))),Sqrt(Power(Plus(C1,Times(c,x)),-1))),Int(SimplifyIntegrand(Times(u,Power(Times(x,Sqrt(Plus(C1,Times(CN1,c,x))),Sqrt(Plus(C1,Times(c,x)))),-1)),x),x),x))),And(FreeQ(List(a,b,c,d,e,f,m,p),x),Or(And(IGtQ(p,C0),Not(And(ILtQ(Times(C1D2,Plus(m,Negate(C1))),C0),GtQ(Plus(m,Times(C2,p),C3),C0)))),And(IGtQ(Times(C1D2,Plus(m,C1)),C0),Not(And(ILtQ(p,C0),GtQ(Plus(m,Times(C2,p),C3),C0)))),And(ILtQ(Times(C1D2,Plus(m,Times(C2,p),C1)),C0),Not(ILtQ(Times(C1D2,Plus(m,Negate(C1))),C0)))))));
IIntegrate(6302,Int(Times(Plus(a_DEFAULT,Times(ArcCsch(Times(c_DEFAULT,x_)),b_DEFAULT)),Power(Times(f_DEFAULT,x_),m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(With(List(Set(u,IntHide(Times(Power(Times(f,x),m),Power(Plus(d,Times(e,Sqr(x))),p)),x))),Plus(Dist(Plus(a,Times(b,ArcCsch(Times(c,x)))),u,x),Negate(Dist(Times(b,c,x,Power(Times(CN1,Sqr(c),Sqr(x)),CN1D2)),Int(SimplifyIntegrand(Times(u,Power(Times(x,Sqrt(Plus(CN1,Times(CN1,Sqr(c),Sqr(x))))),-1)),x),x),x)))),And(FreeQ(List(a,b,c,d,e,f,m,p),x),Or(And(IGtQ(p,C0),Not(And(ILtQ(Times(C1D2,Plus(m,Negate(C1))),C0),GtQ(Plus(m,Times(C2,p),C3),C0)))),And(IGtQ(Times(C1D2,Plus(m,C1)),C0),Not(And(ILtQ(p,C0),GtQ(Plus(m,Times(C2,p),C3),C0)))),And(ILtQ(Times(C1D2,Plus(m,Times(C2,p),C1)),C0),Not(ILtQ(Times(C1D2,Plus(m,Negate(C1))),C0)))))));
IIntegrate(6303,Int(Times(Power(Plus(a_DEFAULT,Times(ArcSech(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(x_,m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Negate(Subst(Int(Times(Power(Plus(e,Times(d,Sqr(x))),p),Power(Plus(a,Times(b,ArcCosh(Times(x,Power(c,-1))))),n),Power(Power(x,Plus(m,Times(C2,Plus(p,C1)))),-1)),x),x,Power(x,-1))),And(FreeQ(List(a,b,c,d,e,n),x),IGtQ(n,C0),IntegersQ(m,p))));
IIntegrate(6304,Int(Times(Power(Plus(a_DEFAULT,Times(ArcCsch(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(x_,m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Negate(Subst(Int(Times(Power(Plus(e,Times(d,Sqr(x))),p),Power(Plus(a,Times(b,ArcSinh(Times(x,Power(c,-1))))),n),Power(Power(x,Plus(m,Times(C2,Plus(p,C1)))),-1)),x),x,Power(x,-1))),And(FreeQ(List(a,b,c,d,e,n),x),IGtQ(n,C0),IntegersQ(m,p))));
IIntegrate(6305,Int(Times(Power(Plus(a_DEFAULT,Times(ArcSech(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(x_,m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Negate(Dist(Times(Sqrt(Sqr(x)),Power(x,-1)),Subst(Int(Times(Power(Plus(e,Times(d,Sqr(x))),p),Power(Plus(a,Times(b,ArcCosh(Times(x,Power(c,-1))))),n),Power(Power(x,Plus(m,Times(C2,Plus(p,C1)))),-1)),x),x,Power(x,-1)),x)),And(FreeQ(List(a,b,c,d,e,n),x),IGtQ(n,C0),EqQ(Plus(Times(Sqr(c),d),e),C0),IntegerQ(m),IntegerQ(Plus(p,C1D2)),GtQ(e,C0),LtQ(d,C0))));
IIntegrate(6306,Int(Times(Power(Plus(a_DEFAULT,Times(ArcCsch(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(x_,m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Negate(Dist(Times(Sqrt(Sqr(x)),Power(x,-1)),Subst(Int(Times(Power(Plus(e,Times(d,Sqr(x))),p),Power(Plus(a,Times(b,ArcSinh(Times(x,Power(c,-1))))),n),Power(Power(x,Plus(m,Times(C2,Plus(p,C1)))),-1)),x),x,Power(x,-1)),x)),And(FreeQ(List(a,b,c,d,e,n),x),IGtQ(n,C0),EqQ(Plus(e,Times(CN1,Sqr(c),d)),C0),IntegerQ(m),IntegerQ(Plus(p,C1D2)),GtQ(e,C0),LtQ(d,C0))));
IIntegrate(6307,Int(Times(Power(Plus(a_DEFAULT,Times(ArcSech(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(x_,m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Negate(Dist(Times(Sqrt(Plus(d,Times(e,Sqr(x)))),Power(Times(x,Sqrt(Plus(e,Times(d,Power(x,-2))))),-1)),Subst(Int(Times(Power(Plus(e,Times(d,Sqr(x))),p),Power(Plus(a,Times(b,ArcCosh(Times(x,Power(c,-1))))),n),Power(Power(x,Plus(m,Times(C2,Plus(p,C1)))),-1)),x),x,Power(x,-1)),x)),And(FreeQ(List(a,b,c,d,e,n),x),IGtQ(n,C0),EqQ(Plus(Times(Sqr(c),d),e),C0),IntegerQ(m),IntegerQ(Plus(p,C1D2)),Not(And(GtQ(e,C0),LtQ(d,C0))))));
IIntegrate(6308,Int(Times(Power(Plus(a_DEFAULT,Times(ArcCsch(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),Power(x_,m_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Negate(Dist(Times(Sqrt(Plus(d,Times(e,Sqr(x)))),Power(Times(x,Sqrt(Plus(e,Times(d,Power(x,-2))))),-1)),Subst(Int(Times(Power(Plus(e,Times(d,Sqr(x))),p),Power(Plus(a,Times(b,ArcSinh(Times(x,Power(c,-1))))),n),Power(Power(x,Plus(m,Times(C2,Plus(p,C1)))),-1)),x),x,Power(x,-1)),x)),And(FreeQ(List(a,b,c,d,e,n),x),IGtQ(n,C0),EqQ(Plus(e,Times(CN1,Sqr(c),d)),C0),IntegerQ(m),IntegerQ(Plus(p,C1D2)),Not(And(GtQ(e,C0),LtQ(d,C0))))));
IIntegrate(6309,Int(Times(Plus(a_DEFAULT,Times(ArcSech(Times(c_DEFAULT,x_)),b_DEFAULT)),u_),x_Symbol),
    Condition(With(List(Set(v,IntHide(u,x))),Condition(Plus(Dist(Plus(a,Times(b,ArcSech(Times(c,x)))),v,x),Dist(Times(b,Sqrt(Plus(C1,Times(CN1,Sqr(c),Sqr(x)))),Power(Times(c,x,Sqrt(Plus(CN1,Power(Times(c,x),-1))),Sqrt(Plus(C1,Power(Times(c,x),-1)))),-1)),Int(SimplifyIntegrand(Times(v,Power(Times(x,Sqrt(Plus(C1,Times(CN1,Sqr(c),Sqr(x))))),-1)),x),x),x)),InverseFunctionFreeQ(v,x))),FreeQ(List(a,b,c),x)));
IIntegrate(6310,Int(Times(Plus(a_DEFAULT,Times(ArcCsch(Times(c_DEFAULT,x_)),b_DEFAULT)),u_),x_Symbol),
    Condition(With(List(Set(v,IntHide(u,x))),Condition(Plus(Dist(Plus(a,Times(b,ArcCsch(Times(c,x)))),v,x),Dist(Times(b,Power(c,-1)),Int(SimplifyIntegrand(Times(v,Power(Times(Sqr(x),Sqrt(Plus(C1,Power(Times(Sqr(c),Sqr(x)),-1)))),-1)),x),x),x)),InverseFunctionFreeQ(v,x))),FreeQ(List(a,b,c),x)));
IIntegrate(6311,Int(Times(Power(Plus(a_DEFAULT,Times(ArcSech(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Unintegrable(Times(u,Power(Plus(a,Times(b,ArcSech(Times(c,x)))),n)),x),FreeQ(List(a,b,c,n),x)));
IIntegrate(6312,Int(Times(Power(Plus(a_DEFAULT,Times(ArcCsch(Times(c_DEFAULT,x_)),b_DEFAULT)),n_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Unintegrable(Times(u,Power(Plus(a,Times(b,ArcCsch(Times(c,x)))),n)),x),FreeQ(List(a,b,c,n),x)));
IIntegrate(6313,Int(ArcSech(Plus(c_,Times(d_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Simp(Times(Plus(c,Times(d,x)),ArcSech(Plus(c,Times(d,x))),Power(d,-1)),x),Int(Times(Sqrt(Times(Plus(C1,Negate(c),Times(CN1,d,x)),Power(Plus(C1,c,Times(d,x)),-1))),Power(Plus(C1,Negate(c),Times(CN1,d,x)),-1)),x)),FreeQ(List(c,d),x)));
IIntegrate(6314,Int(ArcCsch(Plus(c_,Times(d_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Simp(Times(Plus(c,Times(d,x)),ArcCsch(Plus(c,Times(d,x))),Power(d,-1)),x),Int(Power(Times(Plus(c,Times(d,x)),Sqrt(Plus(C1,Power(Plus(c,Times(d,x)),-2)))),-1),x)),FreeQ(List(c,d),x)));
IIntegrate(6315,Int(Power(Plus(a_DEFAULT,Times(ArcSech(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Dist(Power(d,-1),Subst(Int(Power(Plus(a,Times(b,ArcSech(x))),p),x),x,Plus(c,Times(d,x))),x),And(FreeQ(List(a,b,c,d),x),IGtQ(p,C0))));
IIntegrate(6316,Int(Power(Plus(a_DEFAULT,Times(ArcCsch(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Dist(Power(d,-1),Subst(Int(Power(Plus(a,Times(b,ArcCsch(x))),p),x),x,Plus(c,Times(d,x))),x),And(FreeQ(List(a,b,c,d),x),IGtQ(p,C0))));
IIntegrate(6317,Int(Power(Plus(a_DEFAULT,Times(ArcSech(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_),x_Symbol),
    Condition(Unintegrable(Power(Plus(a,Times(b,ArcSech(Plus(c,Times(d,x))))),p),x),And(FreeQ(List(a,b,c,d,p),x),Not(IGtQ(p,C0)))));
IIntegrate(6318,Int(Power(Plus(a_DEFAULT,Times(ArcCsch(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_),x_Symbol),
    Condition(Unintegrable(Power(Plus(a,Times(b,ArcCsch(Plus(c,Times(d,x))))),p),x),And(FreeQ(List(a,b,c,d,p),x),Not(IGtQ(p,C0)))));
IIntegrate(6319,Int(Times(Power(Plus(a_DEFAULT,Times(ArcSech(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Dist(Power(d,-1),Subst(Int(Times(Power(Times(f,x,Power(d,-1)),m),Power(Plus(a,Times(b,ArcSech(x))),p)),x),x,Plus(c,Times(d,x))),x),And(FreeQ(List(a,b,c,d,e,f,m),x),EqQ(Plus(Times(d,e),Times(CN1,c,f)),C0),IGtQ(p,C0))));
IIntegrate(6320,Int(Times(Power(Plus(a_DEFAULT,Times(ArcCsch(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Dist(Power(d,-1),Subst(Int(Times(Power(Times(f,x,Power(d,-1)),m),Power(Plus(a,Times(b,ArcCsch(x))),p)),x),x,Plus(c,Times(d,x))),x),And(FreeQ(List(a,b,c,d,e,f,m),x),EqQ(Plus(Times(d,e),Times(CN1,c,f)),C0),IGtQ(p,C0))));
IIntegrate(6321,Int(Times(Power(Plus(a_DEFAULT,Times(ArcSech(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Negate(Dist(Power(Power(d,Plus(m,C1)),-1),Subst(Int(Times(Power(Plus(a,Times(b,x)),p),Sech(x),Tanh(x),Power(Plus(Times(d,e),Times(CN1,c,f),Times(f,Sech(x))),m)),x),x,ArcSech(Plus(c,Times(d,x)))),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(p,C0),IntegerQ(m))));
IIntegrate(6322,Int(Times(Power(Plus(a_DEFAULT,Times(ArcCsch(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Negate(Dist(Power(Power(d,Plus(m,C1)),-1),Subst(Int(Times(Power(Plus(a,Times(b,x)),p),Csch(x),Coth(x),Power(Plus(Times(d,e),Times(CN1,c,f),Times(f,Csch(x))),m)),x),x,ArcCsch(Plus(c,Times(d,x)))),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(p,C0),IntegerQ(m))));
IIntegrate(6323,Int(Times(Power(Plus(a_DEFAULT,Times(ArcSech(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Dist(Power(d,-1),Subst(Int(Times(Power(Plus(Times(Plus(Times(d,e),Times(CN1,c,f)),Power(d,-1)),Times(f,x,Power(d,-1))),m),Power(Plus(a,Times(b,ArcSech(x))),p)),x),x,Plus(c,Times(d,x))),x),And(FreeQ(List(a,b,c,d,e,f,m),x),IGtQ(p,C0))));
IIntegrate(6324,Int(Times(Power(Plus(a_DEFAULT,Times(ArcCsch(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Dist(Power(d,-1),Subst(Int(Times(Power(Plus(Times(Plus(Times(d,e),Times(CN1,c,f)),Power(d,-1)),Times(f,x,Power(d,-1))),m),Power(Plus(a,Times(b,ArcCsch(x))),p)),x),x,Plus(c,Times(d,x))),x),And(FreeQ(List(a,b,c,d,e,f,m),x),IGtQ(p,C0))));
IIntegrate(6325,Int(Times(Power(Plus(a_DEFAULT,Times(ArcSech(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(e,Times(f,x)),m),Power(Plus(a,Times(b,ArcSech(Plus(c,Times(d,x))))),p)),x),And(FreeQ(List(a,b,c,d,e,f,m,p),x),Not(IGtQ(p,C0)))));
IIntegrate(6326,Int(Times(Power(Plus(a_DEFAULT,Times(ArcCsch(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(e,Times(f,x)),m),Power(Plus(a,Times(b,ArcCsch(Plus(c,Times(d,x))))),p)),x),And(FreeQ(List(a,b,c,d,e,f,m,p),x),Not(IGtQ(p,C0)))));
IIntegrate(6327,Int(Times(Power(ArcSech(Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))),-1))),m_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Int(Times(u,Power(ArcCosh(Plus(Times(a,Power(c,-1)),Times(b,Power(x,n),Power(c,-1)))),m)),x),FreeQ(List(a,b,c,n,m),x)));
IIntegrate(6328,Int(Times(Power(ArcCsch(Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))),-1))),m_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Int(Times(u,Power(ArcSinh(Plus(Times(a,Power(c,-1)),Times(b,Power(x,n),Power(c,-1)))),m)),x),FreeQ(List(a,b,c,n,m),x)));
IIntegrate(6329,Int(Exp(ArcSech(Times(a_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Simp(Times(x,Exp(ArcSech(Times(a,x)))),x),Dist(Power(a,-1),Int(Times(C1,Sqrt(Times(Plus(C1,Times(CN1,a,x)),Power(Plus(C1,Times(a,x)),-1))),Power(Times(x,Plus(C1,Times(CN1,a,x))),-1)),x),x),Simp(Times(Log(x),Power(a,-1)),x)),FreeQ(a,x)));
IIntegrate(6330,Int(Exp(ArcSech(Times(a_DEFAULT,Power(x_,p_)))),x_Symbol),
    Condition(Plus(Simp(Times(x,Exp(ArcSech(Times(a,Power(x,p))))),x),Dist(Times(p,Power(a,-1)),Int(Power(Power(x,p),-1),x),x),Dist(Times(p,Sqrt(Plus(C1,Times(a,Power(x,p)))),Sqrt(Power(Plus(C1,Times(a,Power(x,p))),-1)),Power(a,-1)),Int(Power(Times(Power(x,p),Sqrt(Plus(C1,Times(a,Power(x,p)))),Sqrt(Plus(C1,Times(CN1,a,Power(x,p))))),-1),x),x)),FreeQ(List(a,p),x)));
IIntegrate(6331,Int(Exp(ArcCsch(Times(a_DEFAULT,Power(x_,p_DEFAULT)))),x_Symbol),
    Condition(Plus(Dist(Power(a,-1),Int(Power(Power(x,p),-1),x),x),Int(Sqrt(Plus(C1,Power(Times(Sqr(a),Power(x,Times(C2,p))),-1))),x)),FreeQ(List(a,p),x)));
IIntegrate(6332,Int(Exp(Times(ArcSech(u_),n_DEFAULT)),x_Symbol),
    Condition(Int(Power(Plus(Power(u,-1),Sqrt(Times(Plus(C1,Negate(u)),Power(Plus(C1,u),-1))),Times(C1,Sqrt(Times(Plus(C1,Negate(u)),Power(Plus(C1,u),-1))),Power(u,-1))),n),x),IntegerQ(n)));
IIntegrate(6333,Int(Exp(Times(ArcCsch(u_),n_DEFAULT)),x_Symbol),
    Condition(Int(Power(Plus(Power(u,-1),Sqrt(Plus(C1,Power(u,-2)))),n),x),IntegerQ(n)));
IIntegrate(6334,Int(Times(Exp(ArcSech(Times(a_DEFAULT,Power(x_,p_DEFAULT)))),Power(x_,-1)),x_Symbol),
    Condition(Plus(Negate(Simp(Power(Times(a,p,Power(x,p)),-1),x)),Dist(Times(Sqrt(Plus(C1,Times(a,Power(x,p)))),Sqrt(Power(Plus(C1,Times(a,Power(x,p))),-1)),Power(a,-1)),Int(Times(Sqrt(Plus(C1,Times(a,Power(x,p)))),Sqrt(Plus(C1,Times(CN1,a,Power(x,p)))),Power(Power(x,Plus(p,C1)),-1)),x),x)),FreeQ(List(a,p),x)));
IIntegrate(6335,Int(Times(Exp(ArcSech(Times(a_DEFAULT,Power(x_,p_DEFAULT)))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),Exp(ArcSech(Times(a,Power(x,p)))),Power(Plus(m,C1),-1)),x),Dist(Times(p,Power(Times(a,Plus(m,C1)),-1)),Int(Power(x,Plus(m,Negate(p))),x),x),Dist(Times(p,Sqrt(Plus(C1,Times(a,Power(x,p)))),Sqrt(Power(Plus(C1,Times(a,Power(x,p))),-1)),Power(Times(a,Plus(m,C1)),-1)),Int(Times(Power(x,Plus(m,Negate(p))),Power(Times(Sqrt(Plus(C1,Times(a,Power(x,p)))),Sqrt(Plus(C1,Times(CN1,a,Power(x,p))))),-1)),x),x)),And(FreeQ(List(a,m,p),x),NeQ(m,CN1))));
IIntegrate(6336,Int(Times(Exp(ArcCsch(Times(a_DEFAULT,Power(x_,p_DEFAULT)))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Dist(Power(a,-1),Int(Power(x,Plus(m,Negate(p))),x),x),Int(Times(Power(x,m),Sqrt(Plus(C1,Power(Times(Sqr(a),Power(x,Times(C2,p))),-1)))),x)),FreeQ(List(a,m,p),x)));
IIntegrate(6337,Int(Times(Exp(Times(ArcSech(u_),n_DEFAULT)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(x,m),Power(Plus(Power(u,-1),Sqrt(Times(Plus(C1,Negate(u)),Power(Plus(C1,u),-1))),Times(C1,Sqrt(Times(Plus(C1,Negate(u)),Power(Plus(C1,u),-1))),Power(u,-1))),n)),x),And(FreeQ(m,x),IntegerQ(n))));
IIntegrate(6338,Int(Times(Exp(Times(ArcCsch(u_),n_DEFAULT)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(x,m),Power(Plus(Power(u,-1),Sqrt(Plus(C1,Power(u,-2)))),n)),x),And(FreeQ(m,x),IntegerQ(n))));
IIntegrate(6339,Int(Times(Exp(ArcSech(Times(c_DEFAULT,x_))),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),-1)),x_Symbol),
    Condition(Plus(Dist(Power(Times(a,c),-1),Int(Times(Sqrt(Power(Plus(C1,Times(c,x)),-1)),Power(Times(x,Sqrt(Plus(C1,Times(CN1,c,x)))),-1)),x),x),Dist(Power(c,-1),Int(Power(Times(x,Plus(a,Times(b,Sqr(x)))),-1),x),x)),And(FreeQ(List(a,b,c),x),EqQ(Plus(b,Times(a,Sqr(c))),C0))));
IIntegrate(6340,Int(Times(Exp(ArcCsch(Times(c_DEFAULT,x_))),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),-1)),x_Symbol),
    Condition(Plus(Dist(Power(Times(a,Sqr(c)),-1),Int(Power(Times(Sqr(x),Sqrt(Plus(C1,Power(Times(Sqr(c),Sqr(x)),-1)))),-1),x),x),Dist(Power(c,-1),Int(Power(Times(x,Plus(a,Times(b,Sqr(x)))),-1),x),x)),And(FreeQ(List(a,b,c),x),EqQ(Plus(b,Times(CN1,a,Sqr(c))),C0))));
IIntegrate(6341,Int(Times(Exp(ArcSech(Times(c_DEFAULT,x_))),Power(Times(d_DEFAULT,x_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),-1)),x_Symbol),
    Condition(Plus(Dist(Times(d,Power(Times(a,c),-1)),Int(Times(Power(Times(d,x),Plus(m,Negate(C1))),Sqrt(Power(Plus(C1,Times(c,x)),-1)),Power(Plus(C1,Times(CN1,c,x)),CN1D2)),x),x),Dist(Times(d,Power(c,-1)),Int(Times(Power(Times(d,x),Plus(m,Negate(C1))),Power(Plus(a,Times(b,Sqr(x))),-1)),x),x)),And(FreeQ(List(a,b,c,d,m),x),EqQ(Plus(b,Times(a,Sqr(c))),C0))));
IIntegrate(6342,Int(Times(Exp(ArcCsch(Times(c_DEFAULT,x_))),Power(Times(d_DEFAULT,x_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),-1)),x_Symbol),
    Condition(Plus(Dist(Times(Sqr(d),Power(Times(a,Sqr(c)),-1)),Int(Times(Power(Times(d,x),Plus(m,Negate(C2))),Power(Plus(C1,Power(Times(Sqr(c),Sqr(x)),-1)),CN1D2)),x),x),Dist(Times(d,Power(c,-1)),Int(Times(Power(Times(d,x),Plus(m,Negate(C1))),Power(Plus(a,Times(b,Sqr(x))),-1)),x),x)),And(FreeQ(List(a,b,c,d,m),x),EqQ(Plus(b,Times(CN1,a,Sqr(c))),C0))));
IIntegrate(6343,Int(ArcSech(u_),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcSech(u)),x),Dist(Times(Sqrt(Plus(C1,Negate(Sqr(u)))),Power(Times(u,Sqrt(Plus(CN1,Power(u,-1))),Sqrt(Plus(C1,Power(u,-1)))),-1)),Int(SimplifyIntegrand(Times(x,D(u,x),Power(Times(u,Sqrt(Plus(C1,Negate(Sqr(u))))),-1)),x),x),x)),And(InverseFunctionFreeQ(u,x),Not(FunctionOfExponentialQ(u,x)))));
IIntegrate(6344,Int(ArcCsch(u_),x_Symbol),
    Condition(Plus(Simp(Times(x,ArcCsch(u)),x),Negate(Dist(Times(u,Power(Negate(Sqr(u)),CN1D2)),Int(SimplifyIntegrand(Times(x,D(u,x),Power(Times(u,Sqrt(Plus(CN1,Negate(Sqr(u))))),-1)),x),x),x))),And(InverseFunctionFreeQ(u,x),Not(FunctionOfExponentialQ(u,x)))));
IIntegrate(6345,Int(Times(Plus(a_DEFAULT,Times(ArcSech(u_),b_DEFAULT)),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Plus(a,Times(b,ArcSech(u))),Power(Times(d,Plus(m,C1)),-1)),x),Dist(Times(b,Sqrt(Plus(C1,Negate(Sqr(u)))),Power(Times(d,Plus(m,C1),u,Sqrt(Plus(CN1,Power(u,-1))),Sqrt(Plus(C1,Power(u,-1)))),-1)),Int(SimplifyIntegrand(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),D(u,x),Power(Times(u,Sqrt(Plus(C1,Negate(Sqr(u))))),-1)),x),x),x)),And(FreeQ(List(a,b,c,d,m),x),NeQ(m,CN1),InverseFunctionFreeQ(u,x),Not(FunctionOfQ(Power(Plus(c,Times(d,x)),Plus(m,C1)),u,x)),Not(FunctionOfExponentialQ(u,x)))));
IIntegrate(6346,Int(Times(Plus(a_DEFAULT,Times(ArcCsch(u_),b_DEFAULT)),Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Plus(a,Times(b,ArcCsch(u))),Power(Times(d,Plus(m,C1)),-1)),x),Negate(Dist(Times(b,u,Power(Times(d,Plus(m,C1),Sqrt(Negate(Sqr(u)))),-1)),Int(SimplifyIntegrand(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),D(u,x),Power(Times(u,Sqrt(Plus(CN1,Negate(Sqr(u))))),-1)),x),x),x))),And(FreeQ(List(a,b,c,d,m),x),NeQ(m,CN1),InverseFunctionFreeQ(u,x),Not(FunctionOfQ(Power(Plus(c,Times(d,x)),Plus(m,C1)),u,x)),Not(FunctionOfExponentialQ(u,x)))));
IIntegrate(6347,Int(Times(Plus(a_DEFAULT,Times(ArcSech(u_),b_DEFAULT)),v_),x_Symbol),
    Condition(With(List(Set(w,IntHide(v,x))),Condition(Plus(Dist(Plus(a,Times(b,ArcSech(u))),w,x),Dist(Times(b,Sqrt(Plus(C1,Negate(Sqr(u)))),Power(Times(u,Sqrt(Plus(CN1,Power(u,-1))),Sqrt(Plus(C1,Power(u,-1)))),-1)),Int(SimplifyIntegrand(Times(w,D(u,x),Power(Times(u,Sqrt(Plus(C1,Negate(Sqr(u))))),-1)),x),x),x)),InverseFunctionFreeQ(w,x))),And(FreeQ(List(a,b),x),InverseFunctionFreeQ(u,x),Not(MatchQ(v,Condition(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x)),m_DEFAULT),FreeQ(List(c,d,m),x)))))));
IIntegrate(6348,Int(Times(Plus(a_DEFAULT,Times(ArcCsch(u_),b_DEFAULT)),v_),x_Symbol),
    Condition(With(List(Set(w,IntHide(v,x))),Condition(Plus(Dist(Plus(a,Times(b,ArcCsch(u))),w,x),Negate(Dist(Times(b,u,Power(Negate(Sqr(u)),CN1D2)),Int(SimplifyIntegrand(Times(w,D(u,x),Power(Times(u,Sqrt(Plus(CN1,Negate(Sqr(u))))),-1)),x),x),x))),InverseFunctionFreeQ(w,x))),And(FreeQ(List(a,b),x),InverseFunctionFreeQ(u,x),Not(MatchQ(v,Condition(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x)),m_DEFAULT),FreeQ(List(c,d,m),x)))))));
IIntegrate(6349,Int(Erf(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Simp(Times(Plus(a,Times(b,x)),Erf(Plus(a,Times(b,x))),Power(b,-1)),x),Simp(Power(Times(b,Sqrt(Pi),Exp(Sqr(Plus(a,Times(b,x))))),-1),x)),FreeQ(List(a,b),x)));
IIntegrate(6350,Int(Erfc(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Simp(Times(Plus(a,Times(b,x)),Erfc(Plus(a,Times(b,x))),Power(b,-1)),x),Negate(Simp(Power(Times(b,Sqrt(Pi),Exp(Sqr(Plus(a,Times(b,x))))),-1),x))),FreeQ(List(a,b),x)));
  }
}
}
