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
public class IntRules84 { 

	public static void initialize() {
		Initializer.init();
	}

	private static class Initializer  {

		private static void init() {
IIntegrate(4201,Int(Power(Plus(a_DEFAULT,Times(Csc(Plus(c_DEFAULT,Times(d_DEFAULT,Power(u_,n_)))),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Dist(Power(Coefficient(u,x,C1),CN1),Subst(Int(Power(Plus(a,Times(b,Csc(Plus(c,Times(d,Power(x,n)))))),p),x),x,u),x),And(FreeQ(List(a,b,c,d,n,p),x),LinearQ(u,x),NeQ(u,x))));
IIntegrate(4202,Int(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sec(u_))),p_DEFAULT),x_Symbol),
    Condition(Int(Power(Plus(a,Times(b,Sec(ExpandToSum(u,x)))),p),x),And(FreeQ(List(a,b,p),x),BinomialQ(u,x),Not(BinomialMatchQ(u,x)))));
IIntegrate(4203,Int(Power(Plus(a_DEFAULT,Times(Csc(u_),b_DEFAULT)),p_DEFAULT),x_Symbol),
    Condition(Int(Power(Plus(a,Times(b,Csc(ExpandToSum(u,x)))),p),x),And(FreeQ(List(a,b,p),x),BinomialQ(u,x),Not(BinomialMatchQ(u,x)))));
IIntegrate(4204,Int(Times(Power(x_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sec(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Power(n,CN1),Subst(Int(Times(Power(x,Subtract(Simplify(Times(Plus(m,C1),Power(n,CN1))),C1)),Power(Plus(a,Times(b,Sec(Plus(c,Times(d,x))))),p)),x),x,Power(x,n)),x),And(FreeQ(List(a,b,c,d,m,n,p),x),IGtQ(Simplify(Times(Plus(m,C1),Power(n,CN1))),C0),IntegerQ(p))));
IIntegrate(4205,Int(Times(Power(Plus(a_DEFAULT,Times(Csc(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Dist(Power(n,CN1),Subst(Int(Times(Power(x,Subtract(Simplify(Times(Plus(m,C1),Power(n,CN1))),C1)),Power(Plus(a,Times(b,Csc(Plus(c,Times(d,x))))),p)),x),x,Power(x,n)),x),And(FreeQ(List(a,b,c,d,m,n,p),x),IGtQ(Simplify(Times(Plus(m,C1),Power(n,CN1))),C0),IntegerQ(p))));
IIntegrate(4206,Int(Times(Power(x_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sec(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(x,m),Power(Plus(a,Times(b,Sec(Plus(c,Times(d,Power(x,n)))))),p)),x),FreeQ(List(a,b,c,d,m,n,p),x)));
IIntegrate(4207,Int(Times(Power(Plus(a_DEFAULT,Times(Csc(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(x,m),Power(Plus(a,Times(b,Csc(Plus(c,Times(d,Power(x,n)))))),p)),x),FreeQ(List(a,b,c,d,m,n,p),x)));
IIntegrate(4208,Int(Times(Power(Times(e_,x_),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sec(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))))),p_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(e,IntPart(m)),Power(Times(e,x),FracPart(m)),Power(Power(x,FracPart(m)),CN1)),Int(Times(Power(x,m),Power(Plus(a,Times(b,Sec(Plus(c,Times(d,Power(x,n)))))),p)),x),x),FreeQ(List(a,b,c,d,e,m,n,p),x)));
IIntegrate(4209,Int(Times(Power(Plus(a_DEFAULT,Times(Csc(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_)))),b_DEFAULT)),p_DEFAULT),Power(Times(e_,x_),m_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(e,IntPart(m)),Power(Times(e,x),FracPart(m)),Power(Power(x,FracPart(m)),CN1)),Int(Times(Power(x,m),Power(Plus(a,Times(b,Csc(Plus(c,Times(d,Power(x,n)))))),p)),x),x),FreeQ(List(a,b,c,d,e,m,n,p),x)));
IIntegrate(4210,Int(Times(Power(Times(e_,x_),m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sec(u_))),p_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(Times(e,x),m),Power(Plus(a,Times(b,Sec(ExpandToSum(u,x)))),p)),x),And(FreeQ(List(a,b,e,m,p),x),BinomialQ(u,x),Not(BinomialMatchQ(u,x)))));
IIntegrate(4211,Int(Times(Power(Plus(a_DEFAULT,Times(Csc(u_),b_DEFAULT)),p_DEFAULT),Power(Times(e_,x_),m_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(Times(e,x),m),Power(Plus(a,Times(b,Csc(ExpandToSum(u,x)))),p)),x),And(FreeQ(List(a,b,e,m,p),x),BinomialQ(u,x),Not(BinomialMatchQ(u,x)))));
IIntegrate(4212,Int(Times(Power(x_,m_DEFAULT),Power(Sec(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),p_),Sin(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))))),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,Negate(n),C1)),Power(Sec(Plus(a,Times(b,Power(x,n)))),Subtract(p,C1)),Power(Times(b,n,Subtract(p,C1)),CN1)),x),Dist(Times(Plus(m,Negate(n),C1),Power(Times(b,n,Subtract(p,C1)),CN1)),Int(Times(Power(x,Subtract(m,n)),Power(Sec(Plus(a,Times(b,Power(x,n)))),Subtract(p,C1))),x),x)),And(FreeQ(List(a,b,p),x),IntegerQ(n),GeQ(Subtract(m,n),C0),NeQ(p,C1))));
IIntegrate(4213,Int(Times(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),Power(Csc(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),p_),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(Power(x,Plus(m,Negate(n),C1)),Power(Csc(Plus(a,Times(b,Power(x,n)))),Subtract(p,C1)),Power(Times(b,n,Subtract(p,C1)),CN1)),x)),Dist(Times(Plus(m,Negate(n),C1),Power(Times(b,n,Subtract(p,C1)),CN1)),Int(Times(Power(x,Subtract(m,n)),Power(Csc(Plus(a,Times(b,Power(x,n)))),Subtract(p,C1))),x),x)),And(FreeQ(List(a,b,p),x),IntegerQ(n),GeQ(Subtract(m,n),C0),NeQ(p,C1))));
IIntegrate(4214,Int(Times(u_,Power(Times(d_DEFAULT,$($s("§sin"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_DEFAULT),Power(Times(c_DEFAULT,$($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),m_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(Times(c,Tan(Plus(a,Times(b,x)))),m),Power(Times(d,Cos(Plus(a,Times(b,x)))),m),Power(Power(Times(d,Sin(Plus(a,Times(b,x)))),m),CN1)),Int(Times(ActivateTrig(u),Power(Times(d,Sin(Plus(a,Times(b,x)))),Plus(m,n)),Power(Power(Times(d,Cos(Plus(a,Times(b,x)))),m),CN1)),x),x),And(FreeQ(List(a,b,c,d,m,n),x),KnownSineIntegrandQ(u,x),Not(IntegerQ(m)))));
IIntegrate(4215,Int(Times(Power(Times($($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),d_DEFAULT),n_DEFAULT),u_,Power(Times(c_DEFAULT,$($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),m_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(Times(c,Tan(Plus(a,Times(b,x)))),m),Power(Times(d,Cos(Plus(a,Times(b,x)))),m),Power(Power(Times(d,Sin(Plus(a,Times(b,x)))),m),CN1)),Int(Times(ActivateTrig(u),Power(Times(d,Sin(Plus(a,Times(b,x)))),m),Power(Power(Times(d,Cos(Plus(a,Times(b,x)))),Subtract(m,n)),CN1)),x),x),And(FreeQ(List(a,b,c,d,m,n),x),KnownSineIntegrandQ(u,x),Not(IntegerQ(m)))));
IIntegrate(4216,Int(Times(Power(Times($($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),c_DEFAULT),m_DEFAULT),u_,Power(Times(d_DEFAULT,$($s("§sin"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(Times(c,Cot(Plus(a,Times(b,x)))),m),Power(Times(d,Sin(Plus(a,Times(b,x)))),m),Power(Power(Times(d,Cos(Plus(a,Times(b,x)))),m),CN1)),Int(Times(ActivateTrig(u),Power(Times(d,Cos(Plus(a,Times(b,x)))),m),Power(Power(Times(d,Sin(Plus(a,Times(b,x)))),Subtract(m,n)),CN1)),x),x),And(FreeQ(List(a,b,c,d,m,n),x),KnownSineIntegrandQ(u,x),Not(IntegerQ(m)))));
IIntegrate(4217,Int(Times(Power(Times($($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),c_DEFAULT),m_DEFAULT),Power(Times($($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),d_DEFAULT),n_DEFAULT),u_),x_Symbol),
    Condition(Dist(Times(Power(Times(c,Cot(Plus(a,Times(b,x)))),m),Power(Times(d,Sin(Plus(a,Times(b,x)))),m),Power(Power(Times(d,Cos(Plus(a,Times(b,x)))),m),CN1)),Int(Times(ActivateTrig(u),Power(Times(d,Cos(Plus(a,Times(b,x)))),Plus(m,n)),Power(Power(Times(d,Sin(Plus(a,Times(b,x)))),m),CN1)),x),x),And(FreeQ(List(a,b,c,d,m,n),x),KnownSineIntegrandQ(u,x),Not(IntegerQ(m)))));
IIntegrate(4218,Int(Times(Power(Times($($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),d_DEFAULT),n_DEFAULT),u_,Power(Times(c_DEFAULT,$($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),m_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(Times(c,Csc(Plus(a,Times(b,x)))),m),Power(Times(d,Sin(Plus(a,Times(b,x)))),m)),Int(Times(ActivateTrig(u),Power(Times(d,Sin(Plus(a,Times(b,x)))),Subtract(n,m))),x),x),And(FreeQ(List(a,b,c,d,m,n),x),KnownSineIntegrandQ(u,x))));
IIntegrate(4219,Int(Times(u_,Power(Times(c_DEFAULT,$($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),m_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(Times(c,Tan(Plus(a,Times(b,x)))),m),Power(Times(c,Cos(Plus(a,Times(b,x)))),m),Power(Power(Times(c,Sin(Plus(a,Times(b,x)))),m),CN1)),Int(Times(ActivateTrig(u),Power(Times(c,Sin(Plus(a,Times(b,x)))),m),Power(Power(Times(c,Cos(Plus(a,Times(b,x)))),m),CN1)),x),x),And(FreeQ(List(a,b,c,m),x),Not(IntegerQ(m)),KnownSineIntegrandQ(u,x))));
IIntegrate(4220,Int(Times(Power(Times($($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),c_DEFAULT),m_DEFAULT),u_),x_Symbol),
    Condition(Dist(Times(Power(Times(c,Cot(Plus(a,Times(b,x)))),m),Power(Times(c,Sin(Plus(a,Times(b,x)))),m),Power(Power(Times(c,Cos(Plus(a,Times(b,x)))),m),CN1)),Int(Times(ActivateTrig(u),Power(Times(c,Cos(Plus(a,Times(b,x)))),m),Power(Power(Times(c,Sin(Plus(a,Times(b,x)))),m),CN1)),x),x),And(FreeQ(List(a,b,c,m),x),Not(IntegerQ(m)),KnownSineIntegrandQ(u,x))));
IIntegrate(4221,Int(Times(u_,Power(Times(c_DEFAULT,$($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),m_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(Times(c,Sec(Plus(a,Times(b,x)))),m),Power(Times(c,Cos(Plus(a,Times(b,x)))),m)),Int(Times(ActivateTrig(u),Power(Power(Times(c,Cos(Plus(a,Times(b,x)))),m),CN1)),x),x),And(FreeQ(List(a,b,c,m),x),Not(IntegerQ(m)),KnownSineIntegrandQ(u,x))));
IIntegrate(4222,Int(Times(Power(Times($($s("§csc"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),c_DEFAULT),m_DEFAULT),u_),x_Symbol),
    Condition(Dist(Times(Power(Times(c,Csc(Plus(a,Times(b,x)))),m),Power(Times(c,Sin(Plus(a,Times(b,x)))),m)),Int(Times(ActivateTrig(u),Power(Power(Times(c,Sin(Plus(a,Times(b,x)))),m),CN1)),x),x),And(FreeQ(List(a,b,c,m),x),Not(IntegerQ(m)),KnownSineIntegrandQ(u,x))));
IIntegrate(4223,Int(Times(Plus(Times($($s("§csc"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),B_DEFAULT),A_),u_,Power(Times(c_DEFAULT,$($s("§sin"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition(Dist(c,Int(Times(ActivateTrig(u),Power(Times(c,Sin(Plus(a,Times(b,x)))),Subtract(n,C1)),Plus(BSymbol,Times(ASymbol,Sin(Plus(a,Times(b,x)))))),x),x),And(FreeQ(List(a,b,c,ASymbol,BSymbol,n),x),KnownSineIntegrandQ(u,x))));
IIntegrate(4224,Int(Times(Power(Times($($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),c_DEFAULT),n_DEFAULT),u_,Plus(A_,Times(B_DEFAULT,$($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),x_Symbol),
    Condition(Dist(c,Int(Times(ActivateTrig(u),Power(Times(c,Cos(Plus(a,Times(b,x)))),Subtract(n,C1)),Plus(BSymbol,Times(ASymbol,Cos(Plus(a,Times(b,x)))))),x),x),And(FreeQ(List(a,b,c,ASymbol,BSymbol,n),x),KnownSineIntegrandQ(u,x))));
IIntegrate(4225,Int(Times(Plus(Times($($s("§csc"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),B_DEFAULT),A_),u_),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Plus(BSymbol,Times(ASymbol,Sin(Plus(a,Times(b,x))))),Power(Sin(Plus(a,Times(b,x))),CN1)),x),And(FreeQ(List(a,b,ASymbol,BSymbol),x),KnownSineIntegrandQ(u,x))));
IIntegrate(4226,Int(Times(u_,Plus(A_,Times(B_DEFAULT,$($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Plus(BSymbol,Times(ASymbol,Cos(Plus(a,Times(b,x))))),Power(Cos(Plus(a,Times(b,x))),CN1)),x),And(FreeQ(List(a,b,ASymbol,BSymbol),x),KnownSineIntegrandQ(u,x))));
IIntegrate(4227,Int(Times(Plus(A_DEFAULT,Times($($s("§csc"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),B_DEFAULT),Times(Sqr($($s("§csc"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),C_DEFAULT)),u_DEFAULT,Power(Times(c_DEFAULT,$($s("§sin"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition(Dist(Sqr(c),Int(Times(ActivateTrig(u),Power(Times(c,Sin(Plus(a,Times(b,x)))),Subtract(n,C2)),Plus(CSymbol,Times(BSymbol,Sin(Plus(a,Times(b,x)))),Times(ASymbol,Sqr(Sin(Plus(a,Times(b,x))))))),x),x),And(FreeQ(List(a,b,c,ASymbol,BSymbol,CSymbol,n),x),KnownSineIntegrandQ(u,x))));
IIntegrate(4228,Int(Times(Power(Times($($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),c_DEFAULT),n_DEFAULT),u_DEFAULT,Plus(A_DEFAULT,Times(B_DEFAULT,$($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Times(C_DEFAULT,Sqr($($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))))))),x_Symbol),
    Condition(Dist(Sqr(c),Int(Times(ActivateTrig(u),Power(Times(c,Cos(Plus(a,Times(b,x)))),Subtract(n,C2)),Plus(CSymbol,Times(BSymbol,Cos(Plus(a,Times(b,x)))),Times(ASymbol,Sqr(Cos(Plus(a,Times(b,x))))))),x),x),And(FreeQ(List(a,b,c,ASymbol,BSymbol,CSymbol,n),x),KnownSineIntegrandQ(u,x))));
IIntegrate(4229,Int(Times(u_DEFAULT,Plus(Times(Sqr($($s("§csc"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),C_DEFAULT),A_),Power(Times(c_DEFAULT,$($s("§sin"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition(Dist(Sqr(c),Int(Times(ActivateTrig(u),Power(Times(c,Sin(Plus(a,Times(b,x)))),Subtract(n,C2)),Plus(CSymbol,Times(ASymbol,Sqr(Sin(Plus(a,Times(b,x))))))),x),x),And(FreeQ(List(a,b,c,ASymbol,CSymbol,n),x),KnownSineIntegrandQ(u,x))));
IIntegrate(4230,Int(Times(Power(Times($($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),c_DEFAULT),n_DEFAULT),u_DEFAULT,Plus(A_,Times(C_DEFAULT,Sqr($($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))))))),x_Symbol),
    Condition(Dist(Sqr(c),Int(Times(ActivateTrig(u),Power(Times(c,Cos(Plus(a,Times(b,x)))),Subtract(n,C2)),Plus(CSymbol,Times(ASymbol,Sqr(Cos(Plus(a,Times(b,x))))))),x),x),And(FreeQ(List(a,b,c,ASymbol,CSymbol,n),x),KnownSineIntegrandQ(u,x))));
IIntegrate(4231,Int(Times(Plus(A_DEFAULT,Times($($s("§csc"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),B_DEFAULT),Times(Sqr($($s("§csc"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),C_DEFAULT)),u_),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Plus(CSymbol,Times(BSymbol,Sin(Plus(a,Times(b,x)))),Times(ASymbol,Sqr(Sin(Plus(a,Times(b,x)))))),Power(Sin(Plus(a,Times(b,x))),CN2)),x),And(FreeQ(List(a,b,ASymbol,BSymbol,CSymbol),x),KnownSineIntegrandQ(u,x))));
IIntegrate(4232,Int(Times(u_,Plus(A_DEFAULT,Times(B_DEFAULT,$($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Times(C_DEFAULT,Sqr($($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))))))),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Plus(CSymbol,Times(BSymbol,Cos(Plus(a,Times(b,x)))),Times(ASymbol,Sqr(Cos(Plus(a,Times(b,x)))))),Power(Cos(Plus(a,Times(b,x))),CN2)),x),And(FreeQ(List(a,b,ASymbol,BSymbol,CSymbol),x),KnownSineIntegrandQ(u,x))));
IIntegrate(4233,Int(Times(Plus(Times(Sqr($($s("§csc"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),C_DEFAULT),A_),u_),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Plus(CSymbol,Times(ASymbol,Sqr(Sin(Plus(a,Times(b,x)))))),Power(Sin(Plus(a,Times(b,x))),CN2)),x),And(FreeQ(List(a,b,ASymbol,CSymbol),x),KnownSineIntegrandQ(u,x))));
IIntegrate(4234,Int(Times(u_,Plus(A_,Times(C_DEFAULT,Sqr($($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))))))),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Plus(CSymbol,Times(ASymbol,Sqr(Cos(Plus(a,Times(b,x)))))),Power(Cos(Plus(a,Times(b,x))),CN2)),x),And(FreeQ(List(a,b,ASymbol,CSymbol),x),KnownSineIntegrandQ(u,x))));
IIntegrate(4235,Int(Times(u_,Plus(A_DEFAULT,Times($($s("§csc"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),C_DEFAULT),Times(B_DEFAULT,$($s("§sin"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Plus(CSymbol,Times(ASymbol,Sin(Plus(a,Times(b,x)))),Times(BSymbol,Sqr(Sin(Plus(a,Times(b,x)))))),Power(Sin(Plus(a,Times(b,x))),CN1)),x),FreeQ(List(a,b,ASymbol,BSymbol,CSymbol),x)));
IIntegrate(4236,Int(Times(u_,Plus(A_DEFAULT,Times($($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),B_DEFAULT),Times(C_DEFAULT,$($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Plus(CSymbol,Times(ASymbol,Cos(Plus(a,Times(b,x)))),Times(BSymbol,Sqr(Cos(Plus(a,Times(b,x)))))),Power(Cos(Plus(a,Times(b,x))),CN1)),x),FreeQ(List(a,b,ASymbol,BSymbol,CSymbol),x)));
IIntegrate(4237,Int(Times(u_,Plus(Times(A_DEFAULT,Power($($s("§sin"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT)),Times(B_DEFAULT,Power($($s("§sin"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),$p("n1"))),Times(C_DEFAULT,Power($($s("§sin"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),$p("n2"))))),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Power(Sin(Plus(a,Times(b,x))),n),Plus(ASymbol,Times(BSymbol,Sin(Plus(a,Times(b,x)))),Times(CSymbol,Sqr(Sin(Plus(a,Times(b,x))))))),x),And(FreeQ(List(a,b,ASymbol,BSymbol,CSymbol,n),x),EqQ($s("n1"),Plus(n,C1)),EqQ($s("n2"),Plus(n,C2)))));
IIntegrate(4238,Int(Times(Plus(Times(Power($($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),A_DEFAULT),Times(Power($($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),$p("n1")),B_DEFAULT),Times(Power($($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),$p("n2")),C_DEFAULT)),u_),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Power(Cos(Plus(a,Times(b,x))),n),Plus(ASymbol,Times(BSymbol,Cos(Plus(a,Times(b,x)))),Times(CSymbol,Sqr(Cos(Plus(a,Times(b,x))))))),x),And(FreeQ(List(a,b,ASymbol,BSymbol,CSymbol,n),x),EqQ($s("n1"),Plus(n,C1)),EqQ($s("n2"),Plus(n,C2)))));
IIntegrate(4239,Int(Times(Power(Times($($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),c_DEFAULT),m_DEFAULT),u_,Power(Times(d_DEFAULT,$($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(Times(c,Cot(Plus(a,Times(b,x)))),m),Power(Times(d,Tan(Plus(a,Times(b,x)))),m)),Int(Times(ActivateTrig(u),Power(Times(d,Tan(Plus(a,Times(b,x)))),Subtract(n,m))),x),x),And(FreeQ(List(a,b,c,d,m,n),x),KnownTangentIntegrandQ(u,x))));
IIntegrate(4240,Int(Times(Power(Times($($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),d_DEFAULT),n_DEFAULT),u_,Power(Times(c_DEFAULT,$($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),m_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(Times(c,Tan(Plus(a,Times(b,x)))),m),Power(Times(d,Cos(Plus(a,Times(b,x)))),m),Power(Power(Times(d,Sin(Plus(a,Times(b,x)))),m),CN1)),Int(Times(ActivateTrig(u),Power(Times(d,Sin(Plus(a,Times(b,x)))),m),Power(Power(Times(d,Cos(Plus(a,Times(b,x)))),Subtract(m,n)),CN1)),x),x),And(FreeQ(List(a,b,c,d,m,n),x),KnownCotangentIntegrandQ(u,x))));
IIntegrate(4241,Int(Times(Power(Times($($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),c_DEFAULT),m_DEFAULT),u_),x_Symbol),
    Condition(Dist(Times(Power(Times(c,Cot(Plus(a,Times(b,x)))),m),Power(Times(c,Tan(Plus(a,Times(b,x)))),m)),Int(Times(ActivateTrig(u),Power(Power(Times(c,Tan(Plus(a,Times(b,x)))),m),CN1)),x),x),And(FreeQ(List(a,b,c,m),x),Not(IntegerQ(m)),KnownTangentIntegrandQ(u,x))));
IIntegrate(4242,Int(Times(u_,Power(Times(c_DEFAULT,$($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),m_DEFAULT)),x_Symbol),
    Condition(Dist(Times(Power(Times(c,Cot(Plus(a,Times(b,x)))),m),Power(Times(c,Tan(Plus(a,Times(b,x)))),m)),Int(Times(ActivateTrig(u),Power(Power(Times(c,Cot(Plus(a,Times(b,x)))),m),CN1)),x),x),And(FreeQ(List(a,b,c,m),x),Not(IntegerQ(m)),KnownCotangentIntegrandQ(u,x))));
IIntegrate(4243,Int(Times(Plus(Times($($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),B_DEFAULT),A_),u_,Power(Times(c_DEFAULT,$($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition(Dist(c,Int(Times(ActivateTrig(u),Power(Times(c,Tan(Plus(a,Times(b,x)))),Subtract(n,C1)),Plus(BSymbol,Times(ASymbol,Tan(Plus(a,Times(b,x)))))),x),x),And(FreeQ(List(a,b,c,ASymbol,BSymbol,n),x),KnownTangentIntegrandQ(u,x))));
IIntegrate(4244,Int(Times(Power(Times($($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),c_DEFAULT),n_DEFAULT),u_,Plus(A_,Times(B_DEFAULT,$($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),x_Symbol),
    Condition(Dist(c,Int(Times(ActivateTrig(u),Power(Times(c,Cot(Plus(a,Times(b,x)))),Subtract(n,C1)),Plus(BSymbol,Times(ASymbol,Cot(Plus(a,Times(b,x)))))),x),x),And(FreeQ(List(a,b,c,ASymbol,BSymbol,n),x),KnownCotangentIntegrandQ(u,x))));
IIntegrate(4245,Int(Times(Plus(Times($($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),B_DEFAULT),A_),u_),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Plus(BSymbol,Times(ASymbol,Tan(Plus(a,Times(b,x))))),Power(Tan(Plus(a,Times(b,x))),CN1)),x),And(FreeQ(List(a,b,ASymbol,BSymbol),x),KnownTangentIntegrandQ(u,x))));
IIntegrate(4246,Int(Times(u_,Plus(A_,Times(B_DEFAULT,$($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))))),x_Symbol),
    Condition(Int(Times(ActivateTrig(u),Plus(BSymbol,Times(ASymbol,Cot(Plus(a,Times(b,x))))),Power(Cot(Plus(a,Times(b,x))),CN1)),x),And(FreeQ(List(a,b,ASymbol,BSymbol),x),KnownCotangentIntegrandQ(u,x))));
IIntegrate(4247,Int(Times(Plus(A_DEFAULT,Times($($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),B_DEFAULT),Times(Sqr($($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),C_DEFAULT)),u_DEFAULT,Power(Times(c_DEFAULT,$($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition(Dist(Sqr(c),Int(Times(ActivateTrig(u),Power(Times(c,Tan(Plus(a,Times(b,x)))),Subtract(n,C2)),Plus(CSymbol,Times(BSymbol,Tan(Plus(a,Times(b,x)))),Times(ASymbol,Sqr(Tan(Plus(a,Times(b,x))))))),x),x),And(FreeQ(List(a,b,c,ASymbol,BSymbol,CSymbol,n),x),KnownTangentIntegrandQ(u,x))));
IIntegrate(4248,Int(Times(Power(Times($($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),c_DEFAULT),n_DEFAULT),u_DEFAULT,Plus(A_DEFAULT,Times(B_DEFAULT,$($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),Times(C_DEFAULT,Sqr($($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))))))),x_Symbol),
    Condition(Dist(Sqr(c),Int(Times(ActivateTrig(u),Power(Times(c,Cot(Plus(a,Times(b,x)))),Subtract(n,C2)),Plus(CSymbol,Times(BSymbol,Cot(Plus(a,Times(b,x)))),Times(ASymbol,Sqr(Cot(Plus(a,Times(b,x))))))),x),x),And(FreeQ(List(a,b,c,ASymbol,BSymbol,CSymbol,n),x),KnownCotangentIntegrandQ(u,x))));
IIntegrate(4249,Int(Times(u_DEFAULT,Plus(Times(Sqr($($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),C_DEFAULT),A_),Power(Times(c_DEFAULT,$($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_DEFAULT)),x_Symbol),
    Condition(Dist(Sqr(c),Int(Times(ActivateTrig(u),Power(Times(c,Tan(Plus(a,Times(b,x)))),Subtract(n,C2)),Plus(CSymbol,Times(ASymbol,Sqr(Tan(Plus(a,Times(b,x))))))),x),x),And(FreeQ(List(a,b,c,ASymbol,CSymbol,n),x),KnownTangentIntegrandQ(u,x))));
IIntegrate(4250,Int(Times(Power(Times($($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),c_DEFAULT),n_DEFAULT),u_DEFAULT,Plus(A_,Times(C_DEFAULT,Sqr($($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))))))),x_Symbol),
    Condition(Dist(Sqr(c),Int(Times(ActivateTrig(u),Power(Times(c,Cot(Plus(a,Times(b,x)))),Subtract(n,C2)),Plus(CSymbol,Times(ASymbol,Sqr(Cot(Plus(a,Times(b,x))))))),x),x),And(FreeQ(List(a,b,c,ASymbol,CSymbol,n),x),KnownCotangentIntegrandQ(u,x))));
  }
}
}
