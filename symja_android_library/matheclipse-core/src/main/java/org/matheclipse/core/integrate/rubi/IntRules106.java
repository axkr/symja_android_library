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
public class IntRules106 { 

	public static void initialize() {
		Initializer.init();
	}

	private static class Initializer  {

		private static void init() {
IIntegrate(5301,Int(Power(Plus(a_DEFAULT,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_),x_Symbol),
    Condition(Int(ExpandTrigReduce(Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,Power(x,n)))))),p),x),x),And(FreeQ(List(a,b,c,d),x),IGtQ(n,C1),IGtQ(p,C1))));
IIntegrate(5302,Int(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT),x_Symbol),
    Condition(Negate(Subst(Int(Times(Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,Power(Power(x,n),CN1)))))),p),Power(x,CN2)),x),x,Power(x,CN1))),And(FreeQ(List(a,b,c,d),x),ILtQ(n,C0),IntegerQ(p))));
IIntegrate(5303,Int(Power(Plus(a_DEFAULT,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Negate(Subst(Int(Times(Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,Power(Power(x,n),CN1)))))),p),Power(x,CN2)),x),x,Power(x,CN1))),And(FreeQ(List(a,b,c,d),x),ILtQ(n,C0),IntegerQ(p))));
IIntegrate(5304,Int(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT),x_Symbol),
    Condition(Module(List(Set(k,Denominator(n))),Dist(k,Subst(Int(Times(Power(x,Subtract(k,C1)),Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,Power(x,Times(k,n))))))),p)),x),x,Power(x,Power(k,CN1))),x)),And(FreeQ(List(a,b,c,d),x),FractionQ(n),IntegerQ(p))));
IIntegrate(5305,Int(Power(Plus(a_DEFAULT,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Module(List(Set(k,Denominator(n))),Dist(k,Subst(Int(Times(Power(x,Subtract(k,C1)),Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,Power(x,Times(k,n))))))),p)),x),x,Power(x,Power(k,CN1))),x)),And(FreeQ(List(a,b,c,d),x),FractionQ(n),IntegerQ(p))));
IIntegrate(5306,Int(Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),x_Symbol),
    Condition(Subtract(Dist(C1D2,Int(Exp(Plus(c,Times(d,Power(x,n)))),x),x),Dist(C1D2,Int(Exp(Subtract(Negate(c),Times(d,Power(x,n)))),x),x)),FreeQ(List(c,d,n),x)));
IIntegrate(5307,Int(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),x_Symbol),
    Condition(Plus(Dist(C1D2,Int(Exp(Plus(c,Times(d,Power(x,n)))),x),x),Dist(C1D2,Int(Exp(Subtract(Negate(c),Times(d,Power(x,n)))),x),x)),FreeQ(List(c,d,n),x)));
IIntegrate(5308,Int(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_),x_Symbol),
    Condition(Int(ExpandTrigReduce(Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,Power(x,n)))))),p),x),x),And(FreeQ(List(a,b,c,d,n),x),IGtQ(p,C0))));
IIntegrate(5309,Int(Power(Plus(a_DEFAULT,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_),x_Symbol),
    Condition(Int(ExpandTrigReduce(Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,Power(x,n)))))),p),x),x),And(FreeQ(List(a,b,c,d,n),x),IGtQ(p,C0))));
IIntegrate(5310,Int(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(u_,n_)))))),p_DEFAULT),x_Symbol),
    Condition(Dist(Power(Coefficient(u,x,C1),CN1),Subst(Int(Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,Power(x,n)))))),p),x),x,u),x),And(FreeQ(List(a,b,c,d,n),x),IntegerQ(p),LinearQ(u,x),NeQ(u,x))));
IIntegrate(5311,Int(Power(Plus(a_DEFAULT,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(u_,n_)))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Dist(Power(Coefficient(u,x,C1),CN1),Subst(Int(Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,Power(x,n)))))),p),x),x,u),x),And(FreeQ(List(a,b,c,d,n),x),IntegerQ(p),LinearQ(u,x),NeQ(u,x))));
IIntegrate(5312,Int(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(u_,n_)))))),p_),x_Symbol),
    Condition(Unintegrable(Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,Power(u,n)))))),p),x),And(FreeQ(List(a,b,c,d,n,p),x),LinearQ(u,x))));
IIntegrate(5313,Int(Power(Plus(a_DEFAULT,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(u_,n_)))),b_DEFAULT)),p_),x_Symbol),
    Condition(Unintegrable(Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,Power(u,n)))))),p),x),And(FreeQ(List(a,b,c,d,n,p),x),LinearQ(u,x))));
IIntegrate(5314,Int(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(u_))),p_DEFAULT),x_Symbol),
    Condition(Int(Power(Plus(a,Times(b,Sinh(ExpandToSum(u,x)))),p),x),And(FreeQ(List(a,b,p),x),BinomialQ(u,x),Not(BinomialMatchQ(u,x)))));
IIntegrate(5315,Int(Power(Plus(a_DEFAULT,Times(Cosh(u_),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Int(Power(Plus(a,Times(b,Cosh(ExpandToSum(u,x)))),p),x),And(FreeQ(List(a,b,p),x),BinomialQ(u,x),Not(BinomialMatchQ(u,x)))));
IIntegrate(5316,Int(Times(Power(x_,CN1),Sinh(Times(d_DEFAULT,Power(x_,n_)))),x_Symbol),
    Condition(Simp(Times(SinhIntegral(Times(d,Power(x,n))),Power(n,CN1)),x),FreeQ(List(d,n),x)));
IIntegrate(5317,Int(Times(Cosh(Times(d_DEFAULT,Power(x_,n_))),Power(x_,CN1)),x_Symbol),
    Condition(Simp(Times(CoshIntegral(Times(d,Power(x,n))),Power(n,CN1)),x),FreeQ(List(d,n),x)));
IIntegrate(5318,Int(Times(Power(x_,CN1),Sinh(Plus(c_,Times(d_DEFAULT,Power(x_,n_))))),x_Symbol),
    Condition(Plus(Dist(Sinh(c),Int(Times(Cosh(Times(d,Power(x,n))),Power(x,CN1)),x),x),Dist(Cosh(c),Int(Times(Sinh(Times(d,Power(x,n))),Power(x,CN1)),x),x)),FreeQ(List(c,d,n),x)));
IIntegrate(5319,Int(Times(Cosh(Plus(c_,Times(d_DEFAULT,Power(x_,n_)))),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Dist(Cosh(c),Int(Times(Cosh(Times(d,Power(x,n))),Power(x,CN1)),x),x),Dist(Sinh(c),Int(Times(Sinh(Times(d,Power(x,n))),Power(x,CN1)),x),x)),FreeQ(List(c,d,n),x)));
IIntegrate(5320,Int(Times(Power(x_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(n,CN1),Subst(Int(Times(Power(x,Subtract(Simplify(Times(Plus(m,C1),Power(n,CN1))),C1)),Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,x))))),p)),x),x,Power(x,n)),x),And(FreeQ(List(a,b,c,d,m,n,p),x),IntegerQ(Simplify(Times(Plus(m,C1),Power(n,CN1)))),Or(EqQ(p,C1),EqQ(m,Subtract(n,C1)),And(IntegerQ(p),GtQ(Simplify(Times(Plus(m,C1),Power(n,CN1))),C0))))));
IIntegrate(5321,Int(Times(Power(Plus(a_DEFAULT,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Dist(Power(n,CN1),Subst(Int(Times(Power(x,Subtract(Simplify(Times(Plus(m,C1),Power(n,CN1))),C1)),Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,x))))),p)),x),x,Power(x,n)),x),And(FreeQ(List(a,b,c,d,m,n,p),x),IntegerQ(Simplify(Times(Plus(m,C1),Power(n,CN1)))),Or(EqQ(p,C1),EqQ(m,Subtract(n,C1)),And(IntegerQ(p),GtQ(Simplify(Times(Plus(m,C1),Power(n,CN1))),C0))))));
IIntegrate(5322,Int(Times(Power(Times(e_,x_),m_),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(e,IntPart(m)),Power(Times(e,x),FracPart(m)),Power(Power(x,FracPart(m)),CN1)),Int(Times(Power(x,m),Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,Power(x,n)))))),p)),x),x),And(FreeQ(List(a,b,c,d,e,m,n,p),x),IntegerQ(Simplify(Times(Plus(m,C1),Power(n,CN1)))))));
IIntegrate(5323,Int(Times(Power(Plus(a_DEFAULT,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(Times(e_,x_),m_)),x_Symbol),
    Condition(Dist(Times(Power(e,IntPart(m)),Power(Times(e,x),FracPart(m)),Power(Power(x,FracPart(m)),CN1)),Int(Times(Power(x,m),Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,Power(x,n)))))),p)),x),x),And(FreeQ(List(a,b,c,d,e,m,n,p),x),IntegerQ(Simplify(Times(Plus(m,C1),Power(n,CN1)))))));
IIntegrate(5324,Int(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_))))),x_Symbol),
    Condition(Subtract(Simp(Times(Power(e,Subtract(n,C1)),Power(Times(e,x),Plus(m,Negate(n),C1)),Cosh(Plus(c,Times(d,Power(x,n)))),Power(Times(d,n),CN1)),x),Dist(Times(Power(e,n),Plus(m,Negate(n),C1),Power(Times(d,n),CN1)),Int(Times(Power(Times(e,x),Subtract(m,n)),Cosh(Plus(c,Times(d,Power(x,n))))),x),x)),And(FreeQ(List(c,d,e),x),IGtQ(n,C0),LtQ(C0,n,Plus(m,C1)))));
IIntegrate(5325,Int(Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(e,Subtract(n,C1)),Power(Times(e,x),Plus(m,Negate(n),C1)),Sinh(Plus(c,Times(d,Power(x,n)))),Power(Times(d,n),CN1)),x),Dist(Times(Power(e,n),Plus(m,Negate(n),C1),Power(Times(d,n),CN1)),Int(Times(Power(Times(e,x),Subtract(m,n)),Sinh(Plus(c,Times(d,Power(x,n))))),x),x)),And(FreeQ(List(c,d,e),x),IGtQ(n,C0),LtQ(C0,n,Plus(m,C1)))));
IIntegrate(5326,Int(Times(Power(Times(e_DEFAULT,x_),m_),Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_))))),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(e,x),Plus(m,C1)),Sinh(Plus(c,Times(d,Power(x,n)))),Power(Times(e,Plus(m,C1)),CN1)),x),Dist(Times(d,n,Power(Times(Power(e,n),Plus(m,C1)),CN1)),Int(Times(Power(Times(e,x),Plus(m,n)),Cosh(Plus(c,Times(d,Power(x,n))))),x),x)),And(FreeQ(List(c,d,e),x),IGtQ(n,C0),LtQ(m,CN1))));
IIntegrate(5327,Int(Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),Power(Times(e_DEFAULT,x_),m_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(e,x),Plus(m,C1)),Cosh(Plus(c,Times(d,Power(x,n)))),Power(Times(e,Plus(m,C1)),CN1)),x),Dist(Times(d,n,Power(Times(Power(e,n),Plus(m,C1)),CN1)),Int(Times(Power(Times(e,x),Plus(m,n)),Sinh(Plus(c,Times(d,Power(x,n))))),x),x)),And(FreeQ(List(c,d,e),x),IGtQ(n,C0),LtQ(m,CN1))));
IIntegrate(5328,Int(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_))))),x_Symbol),
    Condition(Subtract(Dist(C1D2,Int(Times(Power(Times(e,x),m),Exp(Plus(c,Times(d,Power(x,n))))),x),x),Dist(C1D2,Int(Times(Power(Times(e,x),m),Exp(Subtract(Negate(c),Times(d,Power(x,n))))),x),x)),And(FreeQ(List(c,d,e,m),x),IGtQ(n,C0))));
IIntegrate(5329,Int(Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Plus(Dist(C1D2,Int(Times(Power(Times(e,x),m),Exp(Plus(c,Times(d,Power(x,n))))),x),x),Dist(C1D2,Int(Times(Power(Times(e,x),m),Exp(Subtract(Negate(c),Times(d,Power(x,n))))),x),x)),And(FreeQ(List(c,d,e,m),x),IGtQ(n,C0))));
IIntegrate(5330,Int(Times(Power(x_,m_DEFAULT),Power(Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(Power(Sinh(Plus(a,Times(b,Power(x,n)))),p),Power(Times(Subtract(n,C1),Power(x,Subtract(n,C1))),CN1)),x)),Dist(Times(b,n,p,Power(Subtract(n,C1),CN1)),Int(Times(Power(Sinh(Plus(a,Times(b,Power(x,n)))),Subtract(p,C1)),Cosh(Plus(a,Times(b,Power(x,n))))),x),x)),And(FreeQ(List(a,b),x),IntegersQ(n,p),EqQ(Plus(m,n),C0),GtQ(p,C1),NeQ(n,C1))));
IIntegrate(5331,Int(Times(Power(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(Power(Cosh(Plus(a,Times(b,Power(x,n)))),p),Power(Times(Subtract(n,C1),Power(x,Subtract(n,C1))),CN1)),x)),Dist(Times(b,n,p,Power(Subtract(n,C1),CN1)),Int(Times(Power(Cosh(Plus(a,Times(b,Power(x,n)))),Subtract(p,C1)),Sinh(Plus(a,Times(b,Power(x,n))))),x),x)),And(FreeQ(List(a,b),x),IntegersQ(n,p),EqQ(Plus(m,n),C0),GtQ(p,C1),NeQ(n,C1))));
IIntegrate(5332,Int(Times(Power(x_,m_DEFAULT),Power(Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(n,Power(Sinh(Plus(a,Times(b,Power(x,n)))),p),Power(Times(Sqr(b),Sqr(n),Sqr(p)),CN1)),x)),Negate(Dist(Times(Subtract(p,C1),Power(p,CN1)),Int(Times(Power(x,m),Power(Sinh(Plus(a,Times(b,Power(x,n)))),Subtract(p,C2))),x),x)),Simp(Times(Power(x,n),Cosh(Plus(a,Times(b,Power(x,n)))),Power(Sinh(Plus(a,Times(b,Power(x,n)))),Subtract(p,C1)),Power(Times(b,n,p),CN1)),x)),And(FreeQ(List(a,b,m,n),x),EqQ(Plus(m,Times(CN1,C2,n),C1)),GtQ(p,C1))));
IIntegrate(5333,Int(Times(Power(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(n,Power(Cosh(Plus(a,Times(b,Power(x,n)))),p),Power(Times(Sqr(b),Sqr(n),Sqr(p)),CN1)),x)),Dist(Times(Subtract(p,C1),Power(p,CN1)),Int(Times(Power(x,m),Power(Cosh(Plus(a,Times(b,Power(x,n)))),Subtract(p,C2))),x),x),Simp(Times(Power(x,n),Sinh(Plus(a,Times(b,Power(x,n)))),Power(Cosh(Plus(a,Times(b,Power(x,n)))),Subtract(p,C1)),Power(Times(b,n,p),CN1)),x)),And(FreeQ(List(a,b,m,n),x),EqQ(Plus(m,Times(CN1,C2,n),C1)),GtQ(p,C1))));
IIntegrate(5334,Int(Times(Power(x_,m_DEFAULT),Power(Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(Plus(m,Negate(n),C1),Power(x,Plus(m,Times(CN1,C2,n),C1)),Power(Sinh(Plus(a,Times(b,Power(x,n)))),p),Power(Times(Sqr(b),Sqr(n),Sqr(p)),CN1)),x)),Negate(Dist(Times(Subtract(p,C1),Power(p,CN1)),Int(Times(Power(x,m),Power(Sinh(Plus(a,Times(b,Power(x,n)))),Subtract(p,C2))),x),x)),Dist(Times(Plus(m,Negate(n),C1),Plus(m,Times(CN1,C2,n),C1),Power(Times(Sqr(b),Sqr(n),Sqr(p)),CN1)),Int(Times(Power(x,Subtract(m,Times(C2,n))),Power(Sinh(Plus(a,Times(b,Power(x,n)))),p)),x),x),Simp(Times(Power(x,Plus(m,Negate(n),C1)),Cosh(Plus(a,Times(b,Power(x,n)))),Power(Sinh(Plus(a,Times(b,Power(x,n)))),Subtract(p,C1)),Power(Times(b,n,p),CN1)),x)),And(FreeQ(List(a,b),x),IntegersQ(m,n),GtQ(p,C1),LtQ(C0,Times(C2,n),Plus(m,C1)))));
IIntegrate(5335,Int(Times(Power(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(Plus(m,Negate(n),C1),Power(x,Plus(m,Times(CN1,C2,n),C1)),Power(Cosh(Plus(a,Times(b,Power(x,n)))),p),Power(Times(Sqr(b),Sqr(n),Sqr(p)),CN1)),x)),Dist(Times(Subtract(p,C1),Power(p,CN1)),Int(Times(Power(x,m),Power(Cosh(Plus(a,Times(b,Power(x,n)))),Subtract(p,C2))),x),x),Dist(Times(Plus(m,Negate(n),C1),Plus(m,Times(CN1,C2,n),C1),Power(Times(Sqr(b),Sqr(n),Sqr(p)),CN1)),Int(Times(Power(x,Subtract(m,Times(C2,n))),Power(Cosh(Plus(a,Times(b,Power(x,n)))),p)),x),x),Simp(Times(Power(x,Plus(m,Negate(n),C1)),Sinh(Plus(a,Times(b,Power(x,n)))),Power(Cosh(Plus(a,Times(b,Power(x,n)))),Subtract(p,C1)),Power(Times(b,n,p),CN1)),x)),And(FreeQ(List(a,b),x),IntegersQ(m,n),GtQ(p,C1),LtQ(C0,Times(C2,n),Plus(m,C1)))));
IIntegrate(5336,Int(Times(Power(x_,m_DEFAULT),Power(Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),Power(Sinh(Plus(a,Times(b,Power(x,n)))),p),Power(Plus(m,C1),CN1)),x),Dist(Times(Sqr(b),Sqr(n),p,Subtract(p,C1),Power(Times(Plus(m,C1),Plus(m,n,C1)),CN1)),Int(Times(Power(x,Plus(m,Times(C2,n))),Power(Sinh(Plus(a,Times(b,Power(x,n)))),Subtract(p,C2))),x),x),Dist(Times(Sqr(b),Sqr(n),Sqr(p),Power(Times(Plus(m,C1),Plus(m,n,C1)),CN1)),Int(Times(Power(x,Plus(m,Times(C2,n))),Power(Sinh(Plus(a,Times(b,Power(x,n)))),p)),x),x),Negate(Simp(Times(b,n,p,Power(x,Plus(m,n,C1)),Cosh(Plus(a,Times(b,Power(x,n)))),Power(Sinh(Plus(a,Times(b,Power(x,n)))),Subtract(p,C1)),Power(Times(Plus(m,C1),Plus(m,n,C1)),CN1)),x))),And(FreeQ(List(a,b),x),IntegersQ(m,n),GtQ(p,C1),LtQ(C0,Times(C2,n),Subtract(C1,m)),NeQ(Plus(m,n,C1),C0))));
IIntegrate(5337,Int(Times(Power(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),Power(Cosh(Plus(a,Times(b,Power(x,n)))),p),Power(Plus(m,C1),CN1)),x),Negate(Dist(Times(Sqr(b),Sqr(n),p,Subtract(p,C1),Power(Times(Plus(m,C1),Plus(m,n,C1)),CN1)),Int(Times(Power(x,Plus(m,Times(C2,n))),Power(Cosh(Plus(a,Times(b,Power(x,n)))),Subtract(p,C2))),x),x)),Dist(Times(Sqr(b),Sqr(n),Sqr(p),Power(Times(Plus(m,C1),Plus(m,n,C1)),CN1)),Int(Times(Power(x,Plus(m,Times(C2,n))),Power(Cosh(Plus(a,Times(b,Power(x,n)))),p)),x),x),Negate(Simp(Times(b,n,p,Power(x,Plus(m,n,C1)),Sinh(Plus(a,Times(b,Power(x,n)))),Power(Cosh(Plus(a,Times(b,Power(x,n)))),Subtract(p,C1)),Power(Times(Plus(m,C1),Plus(m,n,C1)),CN1)),x))),And(FreeQ(List(a,b),x),IntegersQ(m,n),GtQ(p,C1),LtQ(C0,Times(C2,n),Subtract(C1,m)),NeQ(Plus(m,n,C1),C0))));
IIntegrate(5338,Int(Times(Power(Times(e_DEFAULT,x_),m_),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(With(List(Set(k,Denominator(m))),Dist(Times(k,Power(e,CN1)),Subst(Int(Times(Power(x,Subtract(Times(k,Plus(m,C1)),C1)),Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,Power(x,Times(k,n)),Power(Power(e,n),CN1)))))),p)),x),x,Power(Times(e,x),Power(k,CN1))),x)),And(FreeQ(List(a,b,c,d,e),x),IntegerQ(p),IGtQ(n,C0),FractionQ(m))));
IIntegrate(5339,Int(Times(Power(Plus(a_DEFAULT,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(Times(e_DEFAULT,x_),m_)),x_Symbol),
    Condition(With(List(Set(k,Denominator(m))),Dist(Times(k,Power(e,CN1)),Subst(Int(Times(Power(x,Subtract(Times(k,Plus(m,C1)),C1)),Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,Power(x,Times(k,n)),Power(Power(e,n),CN1)))))),p)),x),x,Power(Times(e,x),Power(k,CN1))),x)),And(FreeQ(List(a,b,c,d,e),x),IntegerQ(p),IGtQ(n,C0),FractionQ(m))));
IIntegrate(5340,Int(Times(Power(Times(e_DEFAULT,x_),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_)),x_Symbol),
    Condition(Int(ExpandTrigReduce(Power(Times(e,x),m),Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,Power(x,n)))))),p),x),x),And(FreeQ(List(a,b,c,d,e,m),x),IGtQ(p,C1),IGtQ(n,C0))));
IIntegrate(5341,Int(Times(Power(Plus(a_DEFAULT,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_),Power(Times(e_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Int(ExpandTrigReduce(Power(Times(e,x),m),Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,Power(x,n)))))),p),x),x),And(FreeQ(List(a,b,c,d,e,m),x),IGtQ(p,C1),IGtQ(n,C0))));
IIntegrate(5342,Int(Times(Power(x_,m_DEFAULT),Power(Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,n),Cosh(Plus(a,Times(b,Power(x,n)))),Power(Sinh(Plus(a,Times(b,Power(x,n)))),Plus(p,C1)),Power(Times(b,n,Plus(p,C1)),CN1)),x),Negate(Dist(Times(Plus(p,C2),Power(Plus(p,C1),CN1)),Int(Times(Power(x,m),Power(Sinh(Plus(a,Times(b,Power(x,n)))),Plus(p,C2))),x),x)),Negate(Simp(Times(n,Power(Sinh(Plus(a,Times(b,Power(x,n)))),Plus(p,C2)),Power(Times(Sqr(b),Sqr(n),Plus(p,C1),Plus(p,C2)),CN1)),x))),And(FreeQ(List(a,b,m,n),x),EqQ(Plus(m,Times(CN1,C2,n),C1),C0),LtQ(p,CN1),NeQ(p,CN2))));
IIntegrate(5343,Int(Times(Power(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(Power(x,n),Sinh(Plus(a,Times(b,Power(x,n)))),Power(Cosh(Plus(a,Times(b,Power(x,n)))),Plus(p,C1)),Power(Times(b,n,Plus(p,C1)),CN1)),x)),Dist(Times(Plus(p,C2),Power(Plus(p,C1),CN1)),Int(Times(Power(x,m),Power(Cosh(Plus(a,Times(b,Power(x,n)))),Plus(p,C2))),x),x),Simp(Times(n,Power(Cosh(Plus(a,Times(b,Power(x,n)))),Plus(p,C2)),Power(Times(Sqr(b),Sqr(n),Plus(p,C1),Plus(p,C2)),CN1)),x)),And(FreeQ(List(a,b,m,n),x),EqQ(Plus(m,Times(CN1,C2,n),C1),C0),LtQ(p,CN1),NeQ(p,CN2))));
IIntegrate(5344,Int(Times(Power(x_,m_DEFAULT),Power(Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,Negate(n),C1)),Cosh(Plus(a,Times(b,Power(x,n)))),Power(Sinh(Plus(a,Times(b,Power(x,n)))),Plus(p,C1)),Power(Times(b,n,Plus(p,C1)),CN1)),x),Negate(Dist(Times(Plus(p,C2),Power(Plus(p,C1),CN1)),Int(Times(Power(x,m),Power(Sinh(Plus(a,Times(b,Power(x,n)))),Plus(p,C2))),x),x)),Dist(Times(Plus(m,Negate(n),C1),Plus(m,Times(CN1,C2,n),C1),Power(Times(Sqr(b),Sqr(n),Plus(p,C1),Plus(p,C2)),CN1)),Int(Times(Power(x,Subtract(m,Times(C2,n))),Power(Sinh(Plus(a,Times(b,Power(x,n)))),Plus(p,C2))),x),x),Negate(Simp(Times(Plus(m,Negate(n),C1),Power(x,Plus(m,Times(CN1,C2,n),C1)),Power(Sinh(Plus(a,Times(b,Power(x,n)))),Plus(p,C2)),Power(Times(Sqr(b),Sqr(n),Plus(p,C1),Plus(p,C2)),CN1)),x))),And(FreeQ(List(a,b),x),IntegersQ(m,n),LtQ(p,CN1),NeQ(p,CN2),LtQ(C0,Times(C2,n),Plus(m,C1)))));
IIntegrate(5345,Int(Times(Power(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(Power(x,Plus(m,Negate(n),C1)),Sinh(Plus(a,Times(b,Power(x,n)))),Power(Cosh(Plus(a,Times(b,Power(x,n)))),Plus(p,C1)),Power(Times(b,n,Plus(p,C1)),CN1)),x)),Dist(Times(Plus(p,C2),Power(Plus(p,C1),CN1)),Int(Times(Power(x,m),Power(Cosh(Plus(a,Times(b,Power(x,n)))),Plus(p,C2))),x),x),Negate(Dist(Times(Plus(m,Negate(n),C1),Plus(m,Times(CN1,C2,n),C1),Power(Times(Sqr(b),Sqr(n),Plus(p,C1),Plus(p,C2)),CN1)),Int(Times(Power(x,Subtract(m,Times(C2,n))),Power(Cosh(Plus(a,Times(b,Power(x,n)))),Plus(p,C2))),x),x)),Simp(Times(Plus(m,Negate(n),C1),Power(x,Plus(m,Times(CN1,C2,n),C1)),Power(Cosh(Plus(a,Times(b,Power(x,n)))),Plus(p,C2)),Power(Times(Sqr(b),Sqr(n),Plus(p,C1),Plus(p,C2)),CN1)),x)),And(FreeQ(List(a,b),x),IntegersQ(m,n),LtQ(p,CN1),NeQ(p,CN2),LtQ(C0,Times(C2,n),Plus(m,C1)))));
IIntegrate(5346,Int(Times(Power(x_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Negate(Subst(Int(Times(Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,Power(Power(x,n),CN1)))))),p),Power(Power(x,Plus(m,C2)),CN1)),x),x,Power(x,CN1))),And(FreeQ(List(a,b,c,d),x),IntegerQ(p),ILtQ(n,C0),IntegerQ(m))));
IIntegrate(5347,Int(Times(Power(Plus(a_DEFAULT,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Negate(Subst(Int(Times(Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,Power(Power(x,n),CN1)))))),p),Power(Power(x,Plus(m,C2)),CN1)),x),x,Power(x,CN1))),And(FreeQ(List(a,b,c,d),x),IntegerQ(p),ILtQ(n,C0),IntegerQ(m))));
IIntegrate(5348,Int(Times(Power(Times(e_DEFAULT,x_),m_),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(With(List(Set(k,Denominator(m))),Negate(Dist(Times(k,Power(e,CN1)),Subst(Int(Times(Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,Power(Times(Power(e,n),Power(x,Times(k,n))),CN1)))))),p),Power(Power(x,Plus(Times(k,Plus(m,C1)),C1)),CN1)),x),x,Power(Power(Times(e,x),Power(k,CN1)),CN1)),x))),And(FreeQ(List(a,b,c,d,e),x),IntegerQ(p),ILtQ(n,C0),FractionQ(m))));
IIntegrate(5349,Int(Times(Power(Plus(a_DEFAULT,Times(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(Times(e_DEFAULT,x_),m_)),x_Symbol),
    Condition(With(List(Set(k,Denominator(m))),Negate(Dist(Times(k,Power(e,CN1)),Subst(Int(Times(Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,Power(Times(Power(e,n),Power(x,Times(k,n))),CN1)))))),p),Power(Power(x,Plus(Times(k,Plus(m,C1)),C1)),CN1)),x),x,Power(Power(Times(e,x),Power(k,CN1)),CN1)),x))),And(FreeQ(List(a,b,c,d,e),x),IntegerQ(p),ILtQ(n,C0),FractionQ(m))));
IIntegrate(5350,Int(Times(Power(Times(e_DEFAULT,x_),m_),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Negate(Dist(Times(Power(Times(e,x),m),Power(Power(x,CN1),m)),Subst(Int(Times(Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,Power(Power(x,n),CN1)))))),p),Power(Power(x,Plus(m,C2)),CN1)),x),x,Power(x,CN1)),x)),And(FreeQ(List(a,b,c,d,e,m),x),IntegerQ(p),ILtQ(n,C0),Not(RationalQ(m)))));
  }
}
}
