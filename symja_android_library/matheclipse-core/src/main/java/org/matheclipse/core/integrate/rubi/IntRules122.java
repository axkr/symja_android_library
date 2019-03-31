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
public class IntRules122 { 

	public static void initialize() {
		Initializer.init();
	}

	private static class Initializer  {

		private static void init() {
IIntegrate(6101,Int(Times(Power(Plus(a_DEFAULT,Times(ArcTanh(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Unintegrable(Times(u,Power(Plus(a,Times(b,ArcTanh(Times(c,Power(x,n))))),p)),x),And(FreeQ(List(a,b,c,n,p),x),Or(EqQ(u,C1),MatchQ(u,Condition(Power(Times(d_DEFAULT,x),m_DEFAULT),FreeQ(List(d,m),x)))))));
IIntegrate(6102,Int(Times(Power(Plus(a_DEFAULT,Times(ArcCoth(Times(c_DEFAULT,Power(x_,n_))),b_DEFAULT)),p_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Unintegrable(Times(u,Power(Plus(a,Times(b,ArcCoth(Times(c,Power(x,n))))),p)),x),And(FreeQ(List(a,b,c,n,p),x),Or(EqQ(u,C1),MatchQ(u,Condition(Power(Times(d_DEFAULT,x),m_DEFAULT),FreeQ(List(d,m),x)))))));
IIntegrate(6103,Int(Power(Plus(a_DEFAULT,Times(ArcTanh(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Dist(Power(d,CN1),Subst(Int(Power(Plus(a,Times(b,ArcTanh(x))),p),x),x,Plus(c,Times(d,x))),x),And(FreeQ(List(a,b,c,d),x),IGtQ(p,C0))));
IIntegrate(6104,Int(Power(Plus(a_DEFAULT,Times(ArcCoth(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Dist(Power(d,CN1),Subst(Int(Power(Plus(a,Times(b,ArcCoth(x))),p),x),x,Plus(c,Times(d,x))),x),And(FreeQ(List(a,b,c,d),x),IGtQ(p,C0))));
IIntegrate(6105,Int(Power(Plus(a_DEFAULT,Times(ArcTanh(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_),x_Symbol),
    Condition(Unintegrable(Power(Plus(a,Times(b,ArcTanh(Plus(c,Times(d,x))))),p),x),And(FreeQ(List(a,b,c,d,p),x),Not(IGtQ(p,C0)))));
IIntegrate(6106,Int(Power(Plus(a_DEFAULT,Times(ArcCoth(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_),x_Symbol),
    Condition(Unintegrable(Power(Plus(a,Times(b,ArcCoth(Plus(c,Times(d,x))))),p),x),And(FreeQ(List(a,b,c,d,p),x),Not(IGtQ(p,C0)))));
IIntegrate(6107,Int(Times(Power(Plus(a_DEFAULT,Times(ArcTanh(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Dist(Power(d,CN1),Subst(Int(Times(Power(Times(f,x,Power(d,CN1)),m),Power(Plus(a,Times(b,ArcTanh(x))),p)),x),x,Plus(c,Times(d,x))),x),And(FreeQ(List(a,b,c,d,e,f,m),x),EqQ(Subtract(Times(d,e),Times(c,f)),C0),IGtQ(p,C0))));
IIntegrate(6108,Int(Times(Power(Plus(a_DEFAULT,Times(ArcCoth(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Dist(Power(d,CN1),Subst(Int(Times(Power(Times(f,x,Power(d,CN1)),m),Power(Plus(a,Times(b,ArcCoth(x))),p)),x),x,Plus(c,Times(d,x))),x),And(FreeQ(List(a,b,c,d,e,f,m),x),EqQ(Subtract(Times(d,e),Times(c,f)),C0),IGtQ(p,C0))));
IIntegrate(6109,Int(Times(Power(Plus(a_DEFAULT,Times(ArcTanh(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Power(Plus(a,Times(b,ArcTanh(Plus(c,Times(d,x))))),p),Power(Times(f,Plus(m,C1)),CN1)),x),Dist(Times(b,d,p,Power(Times(f,Plus(m,C1)),CN1)),Int(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Power(Plus(a,Times(b,ArcTanh(Plus(c,Times(d,x))))),Subtract(p,C1)),Power(Subtract(C1,Sqr(Plus(c,Times(d,x)))),CN1)),x),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(p,C0),ILtQ(m,CN1))));
IIntegrate(6110,Int(Times(Power(Plus(a_DEFAULT,Times(ArcCoth(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Power(Plus(a,Times(b,ArcCoth(Plus(c,Times(d,x))))),p),Power(Times(f,Plus(m,C1)),CN1)),x),Dist(Times(b,d,p,Power(Times(f,Plus(m,C1)),CN1)),Int(Times(Power(Plus(e,Times(f,x)),Plus(m,C1)),Power(Plus(a,Times(b,ArcCoth(Plus(c,Times(d,x))))),Subtract(p,C1)),Power(Subtract(C1,Sqr(Plus(c,Times(d,x)))),CN1)),x),x)),And(FreeQ(List(a,b,c,d,e,f),x),IGtQ(p,C0),ILtQ(m,CN1))));
IIntegrate(6111,Int(Times(Power(Plus(a_DEFAULT,Times(ArcTanh(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Dist(Power(d,CN1),Subst(Int(Times(Power(Plus(Times(Subtract(Times(d,e),Times(c,f)),Power(d,CN1)),Times(f,x,Power(d,CN1))),m),Power(Plus(a,Times(b,ArcTanh(x))),p)),x),x,Plus(c,Times(d,x))),x),And(FreeQ(List(a,b,c,d,e,f,m),x),IGtQ(p,C0))));
IIntegrate(6112,Int(Times(Power(Plus(a_DEFAULT,Times(ArcCoth(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Dist(Power(d,CN1),Subst(Int(Times(Power(Plus(Times(Subtract(Times(d,e),Times(c,f)),Power(d,CN1)),Times(f,x,Power(d,CN1))),m),Power(Plus(a,Times(b,ArcCoth(x))),p)),x),x,Plus(c,Times(d,x))),x),And(FreeQ(List(a,b,c,d,e,f,m),x),IGtQ(p,C0))));
IIntegrate(6113,Int(Times(Power(Plus(a_DEFAULT,Times(ArcTanh(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(e,Times(f,x)),m),Power(Plus(a,Times(b,ArcTanh(Plus(c,Times(d,x))))),p)),x),And(FreeQ(List(a,b,c,d,e,f,m,p),x),Not(IGtQ(p,C0)))));
IIntegrate(6114,Int(Times(Power(Plus(a_DEFAULT,Times(ArcCoth(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(e,Times(f,x)),m),Power(Plus(a,Times(b,ArcCoth(Plus(c,Times(d,x))))),p)),x),And(FreeQ(List(a,b,c,d,e,f,m,p),x),Not(IGtQ(p,C0)))));
IIntegrate(6115,Int(Times(ArcTanh(Plus(c_,Times(d_DEFAULT,x_))),Power(Plus(e_,Times(f_DEFAULT,Power(x_,n_DEFAULT))),CN1)),x_Symbol),
    Condition(Subtract(Dist(C1D2,Int(Times(Log(Plus(C1,c,Times(d,x))),Power(Plus(e,Times(f,Power(x,n))),CN1)),x),x),Dist(C1D2,Int(Times(Log(Subtract(Subtract(C1,c),Times(d,x))),Power(Plus(e,Times(f,Power(x,n))),CN1)),x),x)),And(FreeQ(List(c,d,e,f),x),RationalQ(n))));
IIntegrate(6116,Int(Times(ArcCoth(Plus(c_,Times(d_DEFAULT,x_))),Power(Plus(e_,Times(f_DEFAULT,Power(x_,n_DEFAULT))),CN1)),x_Symbol),
    Condition(Subtract(Dist(C1D2,Int(Times(Log(Times(Plus(C1,c,Times(d,x)),Power(Plus(c,Times(d,x)),CN1))),Power(Plus(e,Times(f,Power(x,n))),CN1)),x),x),Dist(C1D2,Int(Times(Log(Times(Plus(CN1,c,Times(d,x)),Power(Plus(c,Times(d,x)),CN1))),Power(Plus(e,Times(f,Power(x,n))),CN1)),x),x)),And(FreeQ(List(c,d,e,f),x),RationalQ(n))));
IIntegrate(6117,Int(Times(ArcTanh(Plus(c_,Times(d_DEFAULT,x_))),Power(Plus(e_,Times(f_DEFAULT,Power(x_,n_))),CN1)),x_Symbol),
    Condition(Unintegrable(Times(ArcTanh(Plus(c,Times(d,x))),Power(Plus(e,Times(f,Power(x,n))),CN1)),x),And(FreeQ(List(c,d,e,f,n),x),Not(RationalQ(n)))));
IIntegrate(6118,Int(Times(ArcCoth(Plus(c_,Times(d_DEFAULT,x_))),Power(Plus(e_,Times(f_DEFAULT,Power(x_,n_))),CN1)),x_Symbol),
    Condition(Unintegrable(Times(ArcCoth(Plus(c,Times(d,x))),Power(Plus(e,Times(f,Power(x,n))),CN1)),x),And(FreeQ(List(c,d,e,f,n),x),Not(RationalQ(n)))));
IIntegrate(6119,Int(Times(Power(Plus(a_DEFAULT,Times(ArcTanh(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(A_DEFAULT,Times(B_DEFAULT,x_),Times(C_DEFAULT,Sqr(x_))),q_DEFAULT)),x_Symbol),
    Condition(Dist(Power(d,CN1),Subst(Int(Times(Power(Plus(Times(CN1,CSymbol,Power(d,CN2)),Times(CSymbol,Sqr(x),Power(d,CN2))),q),Power(Plus(a,Times(b,ArcTanh(x))),p)),x),x,Plus(c,Times(d,x))),x),And(FreeQ(List(a,b,c,d,ASymbol,BSymbol,CSymbol,p,q),x),EqQ(Plus(Times(BSymbol,Subtract(C1,Sqr(c))),Times(C2,ASymbol,c,d)),C0),EqQ(Subtract(Times(C2,c,CSymbol),Times(BSymbol,d)),C0))));
IIntegrate(6120,Int(Times(Power(Plus(a_DEFAULT,Times(ArcCoth(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(A_DEFAULT,Times(B_DEFAULT,x_),Times(C_DEFAULT,Sqr(x_))),q_DEFAULT)),x_Symbol),
    Condition(Dist(Power(d,CN1),Subst(Int(Times(Power(Plus(Times(CSymbol,Power(d,CN2)),Times(CSymbol,Sqr(x),Power(d,CN2))),q),Power(Plus(a,Times(b,ArcCoth(x))),p)),x),x,Plus(c,Times(d,x))),x),And(FreeQ(List(a,b,c,d,ASymbol,BSymbol,CSymbol,p,q),x),EqQ(Plus(Times(BSymbol,Subtract(C1,Sqr(c))),Times(C2,ASymbol,c,d)),C0),EqQ(Subtract(Times(C2,c,CSymbol),Times(BSymbol,d)),C0))));
IIntegrate(6121,Int(Times(Power(Plus(a_DEFAULT,Times(ArcTanh(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Power(Plus(A_DEFAULT,Times(B_DEFAULT,x_),Times(C_DEFAULT,Sqr(x_))),q_DEFAULT)),x_Symbol),
    Condition(Dist(Power(d,CN1),Subst(Int(Times(Power(Plus(Times(Subtract(Times(d,e),Times(c,f)),Power(d,CN1)),Times(f,x,Power(d,CN1))),m),Power(Plus(Times(CN1,CSymbol,Power(d,CN2)),Times(CSymbol,Sqr(x),Power(d,CN2))),q),Power(Plus(a,Times(b,ArcTanh(x))),p)),x),x,Plus(c,Times(d,x))),x),And(FreeQ(List(a,b,c,d,e,f,ASymbol,BSymbol,CSymbol,m,p,q),x),EqQ(Plus(Times(BSymbol,Subtract(C1,Sqr(c))),Times(C2,ASymbol,c,d)),C0),EqQ(Subtract(Times(C2,c,CSymbol),Times(BSymbol,d)),C0))));
IIntegrate(6122,Int(Times(Power(Plus(a_DEFAULT,Times(ArcCoth(Plus(c_,Times(d_DEFAULT,x_))),b_DEFAULT)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Power(Plus(A_DEFAULT,Times(B_DEFAULT,x_),Times(C_DEFAULT,Sqr(x_))),q_DEFAULT)),x_Symbol),
    Condition(Dist(Power(d,CN1),Subst(Int(Times(Power(Plus(Times(Subtract(Times(d,e),Times(c,f)),Power(d,CN1)),Times(f,x,Power(d,CN1))),m),Power(Plus(Times(CN1,CSymbol,Power(d,CN2)),Times(CSymbol,Sqr(x),Power(d,CN2))),q),Power(Plus(a,Times(b,ArcCoth(x))),p)),x),x,Plus(c,Times(d,x))),x),And(FreeQ(List(a,b,c,d,e,f,ASymbol,BSymbol,CSymbol,m,p,q),x),EqQ(Plus(Times(BSymbol,Subtract(C1,Sqr(c))),Times(C2,ASymbol,c,d)),C0),EqQ(Subtract(Times(C2,c,CSymbol),Times(BSymbol,d)),C0))));
IIntegrate(6123,Int(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(Plus(C1,Times(a,x)),Times(C1D2,Plus(n,C1))),Power(Times(Power(Subtract(C1,Times(a,x)),Times(C1D2,Subtract(n,C1))),Sqrt(Subtract(C1,Times(Sqr(a),Sqr(x))))),CN1)),x),And(FreeQ(a,x),IntegerQ(Times(C1D2,Subtract(n,C1))))));
IIntegrate(6124,Int(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(x,m),Power(Plus(C1,Times(a,x)),Times(C1D2,Plus(n,C1))),Power(Times(Power(Subtract(C1,Times(a,x)),Times(C1D2,Subtract(n,C1))),Sqrt(Subtract(C1,Times(Sqr(a),Sqr(x))))),CN1)),x),And(FreeQ(List(a,m),x),IntegerQ(Times(C1D2,Subtract(n,C1))))));
IIntegrate(6125,Int(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_)),x_Symbol),
    Condition(Int(Times(Power(Plus(C1,Times(a,x)),Times(C1D2,n)),Power(Power(Subtract(C1,Times(a,x)),Times(C1D2,n)),CN1)),x),And(FreeQ(List(a,n),x),Not(IntegerQ(Times(C1D2,Subtract(n,C1)))))));
IIntegrate(6126,Int(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(x,m),Power(Plus(C1,Times(a,x)),Times(C1D2,n)),Power(Power(Subtract(C1,Times(a,x)),Times(C1D2,n)),CN1)),x),And(FreeQ(List(a,m,n),x),Not(IntegerQ(Times(C1D2,Subtract(n,C1)))))));
IIntegrate(6127,Int(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,x_)),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(c,n),Int(Times(Power(Plus(c,Times(d,x)),Subtract(p,n)),Power(Subtract(C1,Times(Sqr(a),Sqr(x))),Times(C1D2,n))),x),x),And(FreeQ(List(a,c,d,p),x),EqQ(Plus(Times(a,c),d),C0),IntegerQ(Times(C1D2,Subtract(n,C1))),IntegerQ(Times(C2,p)))));
IIntegrate(6128,Int(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,x_)),p_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Dist(Power(c,n),Int(Times(Power(Plus(e,Times(f,x)),m),Power(Plus(c,Times(d,x)),Subtract(p,n)),Power(Subtract(C1,Times(Sqr(a),Sqr(x))),Times(C1D2,n))),x),x),And(FreeQ(List(a,c,d,e,f,m,p),x),EqQ(Plus(Times(a,c),d),C0),IntegerQ(Times(C1D2,Subtract(n,C1))),Or(IntegerQ(p),EqQ(p,Times(C1D2,n)),EqQ(Subtract(Subtract(p,Times(C1D2,n)),C1),C0)),IntegerQ(Times(C2,p)))));
IIntegrate(6129,Int(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,x_)),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(c,p),Int(Times(u,Power(Plus(C1,Times(d,x,Power(c,CN1))),p),Power(Plus(C1,Times(a,x)),Times(C1D2,n)),Power(Power(Subtract(C1,Times(a,x)),Times(C1D2,n)),CN1)),x),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Subtract(Times(Sqr(a),Sqr(c)),Sqr(d)),C0),Or(IntegerQ(p),GtQ(c,C0)))));
IIntegrate(6130,Int(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,x_)),p_DEFAULT)),x_Symbol),
    Condition(Int(Times(u,Power(Plus(c,Times(d,x)),p),Power(Plus(C1,Times(a,x)),Times(C1D2,n)),Power(Power(Subtract(C1,Times(a,x)),Times(C1D2,n)),CN1)),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Subtract(Times(Sqr(a),Sqr(c)),Sqr(d)),C0),Not(Or(IntegerQ(p),GtQ(c,C0))))));
IIntegrate(6131,Int(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN1))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(d,p),Int(Times(u,Power(Plus(C1,Times(c,x,Power(d,CN1))),p),Exp(Times(n,ArcTanh(Times(a,x)))),Power(Power(x,p),CN1)),x),x),And(FreeQ(List(a,c,d,n),x),EqQ(Subtract(Sqr(c),Times(Sqr(a),Sqr(d))),C0),IntegerQ(p))));
IIntegrate(6132,Int(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN1))),p_)),x_Symbol),
    Condition(Dist(Times(Power(CN1,Times(C1D2,n)),Power(c,p)),Int(Times(u,Power(Plus(C1,Times(d,Power(Times(c,x),CN1))),p),Power(Plus(C1,Power(Times(a,x),CN1)),Times(C1D2,n)),Power(Power(Subtract(C1,Power(Times(a,x),CN1)),Times(C1D2,n)),CN1)),x),x),And(FreeQ(List(a,c,d,p),x),EqQ(Subtract(Sqr(c),Times(Sqr(a),Sqr(d))),C0),Not(IntegerQ(p)),IntegerQ(Times(C1D2,n)),GtQ(c,C0))));
IIntegrate(6133,Int(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN1))),p_)),x_Symbol),
    Condition(Int(Times(u,Power(Plus(c,Times(d,Power(x,CN1))),p),Power(Plus(C1,Times(a,x)),Times(C1D2,n)),Power(Power(Subtract(C1,Times(a,x)),Times(C1D2,n)),CN1)),x),And(FreeQ(List(a,c,d,p),x),EqQ(Subtract(Sqr(c),Times(Sqr(a),Sqr(d))),C0),Not(IntegerQ(p)),IntegerQ(Times(C1D2,n)),Not(GtQ(c,C0)))));
IIntegrate(6134,Int(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_DEFAULT)),u_DEFAULT,Power(Plus(c_,Times(d_DEFAULT,Power(x_,CN1))),p_)),x_Symbol),
    Condition(Dist(Times(Power(x,p),Power(Plus(c,Times(d,Power(x,CN1))),p),Power(Power(Plus(C1,Times(c,x,Power(d,CN1))),p),CN1)),Int(Times(u,Power(Plus(C1,Times(c,x,Power(d,CN1))),p),Exp(Times(n,ArcTanh(Times(a,x)))),Power(Power(x,p),CN1)),x),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Subtract(Sqr(c),Times(Sqr(a),Sqr(d))),C0),Not(IntegerQ(p)))));
IIntegrate(6135,Int(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),QQ(-3L,2L))),x_Symbol),
    Condition(Simp(Times(Subtract(n,Times(a,x)),Exp(Times(n,ArcTanh(Times(a,x)))),Power(Times(a,c,Subtract(Sqr(n),C1),Sqrt(Plus(c,Times(d,Sqr(x))))),CN1)),x),And(FreeQ(List(a,c,d,n),x),EqQ(Plus(Times(Sqr(a),c),d),C0),Not(IntegerQ(n)))));
IIntegrate(6136,Int(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(n,Times(C2,a,Plus(p,C1),x)),Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1)),Exp(Times(n,ArcTanh(Times(a,x)))),Power(Times(a,c,Subtract(Sqr(n),Times(C4,Sqr(Plus(p,C1))))),CN1)),x),Dist(Times(C2,Plus(p,C1),Plus(Times(C2,p),C3),Power(Times(c,Subtract(Sqr(n),Times(C4,Sqr(Plus(p,C1))))),CN1)),Int(Times(Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1)),Exp(Times(n,ArcTanh(Times(a,x))))),x),x)),And(FreeQ(List(a,c,d,n),x),EqQ(Plus(Times(Sqr(a),c),d),C0),LtQ(p,CN1),Not(IntegerQ(n)),NeQ(Subtract(Sqr(n),Times(C4,Sqr(Plus(p,C1)))),C0),IntegerQ(Times(C2,p)))));
IIntegrate(6137,Int(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Simp(Times(Exp(Times(n,ArcTanh(Times(a,x)))),Power(Times(a,c,n),CN1)),x),And(FreeQ(List(a,c,d,n),x),EqQ(Plus(Times(Sqr(a),c),d),C0),Not(IntegerQ(Times(C1D2,n))))));
IIntegrate(6138,Int(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(c,p),Int(Times(Power(Subtract(C1,Times(Sqr(a),Sqr(x))),Subtract(p,Times(C1D2,n))),Power(Plus(C1,Times(a,x)),n)),x),x),And(FreeQ(List(a,c,d,p),x),EqQ(Plus(Times(Sqr(a),c),d),C0),IntegerQ(p),IGtQ(Times(C1D2,Plus(n,C1)),C0),Not(IntegerQ(Subtract(p,Times(C1D2,n)))))));
IIntegrate(6139,Int(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(c,p),Int(Times(Power(Subtract(C1,Times(Sqr(a),Sqr(x))),Plus(p,Times(C1D2,n))),Power(Power(Subtract(C1,Times(a,x)),n),CN1)),x),x),And(FreeQ(List(a,c,d,p),x),EqQ(Plus(Times(Sqr(a),c),d),C0),IntegerQ(p),ILtQ(Times(C1D2,Subtract(n,C1)),C0),Not(IntegerQ(Subtract(p,Times(C1D2,n)))))));
IIntegrate(6140,Int(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(c,p),Int(Times(Power(Subtract(C1,Times(a,x)),Subtract(p,Times(C1D2,n))),Power(Plus(C1,Times(a,x)),Plus(p,Times(C1D2,n)))),x),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Plus(Times(Sqr(a),c),d),C0),Or(IntegerQ(p),GtQ(c,C0)))));
IIntegrate(6141,Int(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(c,Times(C1D2,n)),Int(Times(Power(Plus(c,Times(d,Sqr(x))),Subtract(p,Times(C1D2,n))),Power(Plus(C1,Times(a,x)),n)),x),x),And(FreeQ(List(a,c,d,p),x),EqQ(Plus(Times(Sqr(a),c),d),C0),Not(Or(IntegerQ(p),GtQ(c,C0))),IGtQ(Times(C1D2,n),C0))));
IIntegrate(6142,Int(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(Power(c,Times(C1D2,n)),CN1),Int(Times(Power(Plus(c,Times(d,Sqr(x))),Plus(p,Times(C1D2,n))),Power(Power(Subtract(C1,Times(a,x)),n),CN1)),x),x),And(FreeQ(List(a,c,d,p),x),EqQ(Plus(Times(Sqr(a),c),d),C0),Not(Or(IntegerQ(p),GtQ(c,C0))),ILtQ(Times(C1D2,n),C0))));
IIntegrate(6143,Int(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Dist(Times(Power(c,IntPart(p)),Power(Plus(c,Times(d,Sqr(x))),FracPart(p)),Power(Power(Subtract(C1,Times(Sqr(a),Sqr(x))),FracPart(p)),CN1)),Int(Times(Power(Subtract(C1,Times(Sqr(a),Sqr(x))),p),Exp(Times(n,ArcTanh(Times(a,x))))),x),x),And(FreeQ(List(a,c,d,n,p),x),EqQ(Plus(Times(Sqr(a),c),d),C0),Not(Or(IntegerQ(p),GtQ(c,C0))))));
IIntegrate(6144,Int(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_)),x_,Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),QQ(-3L,2L))),x_Symbol),
    Condition(Simp(Times(Subtract(C1,Times(a,n,x)),Exp(Times(n,ArcTanh(Times(a,x)))),Power(Times(d,Subtract(Sqr(n),C1),Sqrt(Plus(c,Times(d,Sqr(x))))),CN1)),x),And(FreeQ(List(a,c,d,n),x),EqQ(Plus(Times(Sqr(a),c),d),C0),Not(IntegerQ(n)))));
IIntegrate(6145,Int(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_)),x_,Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1)),Exp(Times(n,ArcTanh(Times(a,x)))),Power(Times(C2,d,Plus(p,C1)),CN1)),x),Dist(Times(a,c,n,Power(Times(C2,d,Plus(p,C1)),CN1)),Int(Times(Power(Plus(c,Times(d,Sqr(x))),p),Exp(Times(n,ArcTanh(Times(a,x))))),x),x)),And(FreeQ(List(a,c,d,n),x),EqQ(Plus(Times(Sqr(a),c),d),C0),LtQ(p,CN1),Not(IntegerQ(n)),IntegerQ(Times(C2,p)))));
IIntegrate(6146,Int(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_)),Sqr(x_),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Times(Subtract(C1,Times(a,n,x)),Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1)),Exp(Times(n,ArcTanh(Times(a,x)))),Power(Times(a,d,n,Subtract(Sqr(n),C1)),CN1)),x),And(FreeQ(List(a,c,d,n),x),EqQ(Plus(Times(Sqr(a),c),d),C0),EqQ(Plus(Sqr(n),Times(C2,Plus(p,C1))),C0),Not(IntegerQ(n)))));
IIntegrate(6147,Int(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_)),Sqr(x_),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(Plus(n,Times(C2,Plus(p,C1),a,x)),Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1)),Exp(Times(n,ArcTanh(Times(a,x)))),Power(Times(a,d,Subtract(Sqr(n),Times(C4,Sqr(Plus(p,C1))))),CN1)),x)),Dist(Times(Plus(Sqr(n),Times(C2,Plus(p,C1))),Power(Times(d,Subtract(Sqr(n),Times(C4,Sqr(Plus(p,C1))))),CN1)),Int(Times(Power(Plus(c,Times(d,Sqr(x))),Plus(p,C1)),Exp(Times(n,ArcTanh(Times(a,x))))),x),x)),And(FreeQ(List(a,c,d,n),x),EqQ(Plus(Times(Sqr(a),c),d),C0),LtQ(p,CN1),Not(IntegerQ(n)),NeQ(Subtract(Sqr(n),Times(C4,Sqr(Plus(p,C1)))),C0),IntegerQ(Times(C2,p)))));
IIntegrate(6148,Int(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(x_,m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(c,p),Int(Times(Power(x,m),Power(Subtract(C1,Times(Sqr(a),Sqr(x))),Subtract(p,Times(C1D2,n))),Power(Plus(C1,Times(a,x)),n)),x),x),And(FreeQ(List(a,c,d,m,p),x),EqQ(Plus(Times(Sqr(a),c),d),C0),Or(IntegerQ(p),GtQ(c,C0)),IGtQ(Times(C1D2,Plus(n,C1)),C0),Not(IntegerQ(Subtract(p,Times(C1D2,n)))))));
IIntegrate(6149,Int(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_)),Power(x_,m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(c,p),Int(Times(Power(x,m),Power(Subtract(C1,Times(Sqr(a),Sqr(x))),Plus(p,Times(C1D2,n))),Power(Power(Subtract(C1,Times(a,x)),n),CN1)),x),x),And(FreeQ(List(a,c,d,m,p),x),EqQ(Plus(Times(Sqr(a),c),d),C0),Or(IntegerQ(p),GtQ(c,C0)),ILtQ(Times(C1D2,Subtract(n,C1)),C0),Not(IntegerQ(Subtract(p,Times(C1D2,n)))))));
IIntegrate(6150,Int(Times(Exp(Times(ArcTanh(Times(a_DEFAULT,x_)),n_DEFAULT)),Power(x_,m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Sqr(x_))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(c,p),Int(Times(Power(x,m),Power(Subtract(C1,Times(a,x)),Subtract(p,Times(C1D2,n))),Power(Plus(C1,Times(a,x)),Plus(p,Times(C1D2,n)))),x),x),And(FreeQ(List(a,c,d,m,n,p),x),EqQ(Plus(Times(Sqr(a),c),d),C0),Or(IntegerQ(p),GtQ(c,C0)))));
  }
}
}
