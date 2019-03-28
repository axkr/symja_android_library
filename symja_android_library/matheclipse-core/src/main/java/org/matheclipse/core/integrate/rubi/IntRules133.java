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
public class IntRules133 { 

	public static void initialize() {
		Initializer.init();
	}

	private static class Initializer  {

		private static void init() {
IIntegrate(6651,Int(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT))))),-1)),x_Symbol),
    Condition(Plus(Simp(Times(c,Power(x,Plus(m,C1)),Power(Times(c,ProductLog(Times(a,Power(x,n)))),Plus(p,Negate(C1))),Power(Times(d,Plus(m,C1)),-1)),x),Negate(Dist(Times(c,Plus(m,Times(n,Plus(p,Negate(C1))),C1),Power(Plus(m,C1),-1)),Int(Times(Power(x,m),Power(Times(c,ProductLog(Times(a,Power(x,n)))),Plus(p,Negate(C1))),Power(Plus(d,Times(d,ProductLog(Times(a,Power(x,n))))),-1)),x),x))),And(FreeQ(List(a,c,d,m,n,p),x),NeQ(m,CN1),GtQ(Simplify(Plus(p,Times(Plus(m,C1),Power(n,-1)))),C1))));
IIntegrate(6652,Int(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT))))),-1)),x_Symbol),
    Condition(Plus(Simp(Times(Power(x,Plus(m,C1)),Power(Times(c,ProductLog(Times(a,Power(x,n)))),p),Power(Times(d,Plus(m,Times(n,p),C1)),-1)),x),Negate(Dist(Times(Plus(m,C1),Power(Times(c,Plus(m,Times(n,p),C1)),-1)),Int(Times(Power(x,m),Power(Times(c,ProductLog(Times(a,Power(x,n)))),Plus(p,C1)),Power(Plus(d,Times(d,ProductLog(Times(a,Power(x,n))))),-1)),x),x))),And(FreeQ(List(a,c,d,m,n,p),x),NeQ(m,CN1),LtQ(Simplify(Plus(p,Times(Plus(m,C1),Power(n,-1)))),C0))));
IIntegrate(6653,Int(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,ProductLog(Times(a_DEFAULT,x_))),p_DEFAULT),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Times(a_DEFAULT,x_)))),-1)),x_Symbol),
    Condition(Simp(Times(Power(x,m),Gamma(Plus(m,p,C1),Times(CN1,Plus(m,C1),ProductLog(Times(a,x)))),Power(Times(c,ProductLog(Times(a,x))),p),Power(Times(a,d,Plus(m,C1),Exp(Times(m,ProductLog(Times(a,x)))),Power(Times(CN1,Plus(m,C1),ProductLog(Times(a,x))),Plus(m,p))),-1)),x),And(FreeQ(List(a,c,d,m,p),x),NeQ(m,CN1))));
IIntegrate(6654,Int(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT))))),-1)),x_Symbol),
    Condition(Negate(Subst(Int(Times(Power(Times(c,ProductLog(Times(a,Power(Power(x,n),-1)))),p),Power(Times(Power(x,Plus(m,C2)),Plus(d,Times(d,ProductLog(Times(a,Power(Power(x,n),-1)))))),-1)),x),x,Power(x,-1))),And(FreeQ(List(a,c,d,p),x),NeQ(m,CN1),IntegerQ(m),LtQ(n,C0))));
IIntegrate(6655,Int(u_,x_Symbol),
    Condition(Subst(Int(SimplifyIntegrand(Times(Plus(x,C1),Exp(x),SubstFor(ProductLog(x),u,x)),x),x),x,ProductLog(x)),FunctionOfQ(ProductLog(x),u,x)));
IIntegrate(6656,Int($($(Derivative(n_),f_),x_),x_Symbol),
    Condition(Simp($($(Derivative(Plus(n,Negate(C1))),f),x),x),FreeQ(List(f,n),x)));
IIntegrate(6657,Int(Times(Power(Times(c_DEFAULT,Power(F_,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),p_DEFAULT),$($(Derivative(n_),f_),x_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Times(c,Power(FSymbol,Plus(a,Times(b,x)))),p),$($(Derivative(Plus(n,Negate(C1))),f),x)),x),Negate(Dist(Times(b,p,Log(FSymbol)),Int(Times(Power(Times(c,Power(FSymbol,Plus(a,Times(b,x)))),p),$($(Derivative(Plus(n,Negate(C1))),f),x)),x),x))),And(FreeQ(List(a,b,c,f,FSymbol,p),x),IGtQ(n,C0))));
IIntegrate(6658,Int(Times(Power(Times(c_DEFAULT,Power(F_,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),p_DEFAULT),$($(Derivative(n_),f_),x_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Times(c,Power(FSymbol,Plus(a,Times(b,x)))),p),$($(Derivative(n),f),x),Power(Times(b,p,Log(FSymbol)),-1)),x),Negate(Dist(Power(Times(b,p,Log(FSymbol)),-1),Int(Times(Power(Times(c,Power(FSymbol,Plus(a,Times(b,x)))),p),$($(Derivative(Plus(n,C1)),f),x)),x),x))),And(FreeQ(List(a,b,c,f,FSymbol,p),x),ILtQ(n,C0))));
IIntegrate(6659,Int(Times(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),$($(Derivative(n_),f_),x_)),x_Symbol),
    Condition(Plus(Simp(Times(Sin(Plus(a,Times(b,x))),$($(Derivative(Plus(n,Negate(C1))),f),x)),x),Negate(Dist(b,Int(Times(Cos(Plus(a,Times(b,x))),$($(Derivative(Plus(n,Negate(C1))),f),x)),x),x))),And(FreeQ(List(a,b,f),x),IGtQ(n,C0))));
IIntegrate(6660,Int(Times(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),$($(Derivative(n_),f_),x_)),x_Symbol),
    Condition(Plus(Simp(Times(Cos(Plus(a,Times(b,x))),$($(Derivative(Plus(n,Negate(C1))),f),x)),x),Dist(b,Int(Times(Sin(Plus(a,Times(b,x))),$($(Derivative(Plus(n,Negate(C1))),f),x)),x),x)),And(FreeQ(List(a,b,f),x),IGtQ(n,C0))));
IIntegrate(6661,Int(Times(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),$($(Derivative(n_),f_),x_)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(Cos(Plus(a,Times(b,x))),$($(Derivative(n),f),x),Power(b,-1)),x)),Dist(Power(b,-1),Int(Times(Cos(Plus(a,Times(b,x))),$($(Derivative(Plus(n,C1)),f),x)),x),x)),And(FreeQ(List(a,b,f),x),ILtQ(n,C0))));
IIntegrate(6662,Int(Times(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),$($(Derivative(n_),f_),x_)),x_Symbol),
    Condition(Plus(Simp(Times(Sin(Plus(a,Times(b,x))),$($(Derivative(n),f),x),Power(b,-1)),x),Negate(Dist(Power(b,-1),Int(Times(Sin(Plus(a,Times(b,x))),$($(Derivative(Plus(n,C1)),f),x)),x),x))),And(FreeQ(List(a,b,f),x),ILtQ(n,C0))));
IIntegrate(6663,Int(Times(u_,$($(Derivative(n_),f_),x_)),x_Symbol),
    Condition(Subst(Int(SimplifyIntegrand(SubstFor($($(Derivative(Plus(n,Negate(C1))),f),x),u,x),x),x),x,$($(Derivative(Plus(n,Negate(C1))),f),x)),And(FreeQ(List(f,n),x),FunctionOfQ($($(Derivative(Plus(n,Negate(C1))),f),x),u,x))));
IIntegrate(6664,Int(Times(u_,Plus(Times(a_DEFAULT,$(g_,x_),$($(Derivative(C1),f_),x_)),Times(a_DEFAULT,$(f_,x_),$($(Derivative(C1),g_),x_)))),x_Symbol),
    Condition(Dist(a,Subst(Int(SimplifyIntegrand(SubstFor(Times($(f,x),$(g,x)),u,x),x),x),x,Times($(f,x),$(g,x))),x),And(FreeQ(List(a,f,g),x),FunctionOfQ(Times($(f,x),$(g,x)),u,x))));
IIntegrate(6665,Int(Times(u_,Plus(Times(a_DEFAULT,$(g_,x_),$($(Derivative(m_),f_),x_)),Times(a_DEFAULT,$($(Derivative(C1),g_),x_),$($(Derivative($p("m1")),f_),x_)))),x_Symbol),
    Condition(Dist(a,Subst(Int(SimplifyIntegrand(SubstFor(Times($($(Derivative(Plus(m,Negate(C1))),f),x),$(g,x)),u,x),x),x),x,Times($($(Derivative(Plus(m,Negate(C1))),f),x),$(g,x))),x),And(FreeQ(List(a,f,g,m),x),EqQ($s("m1"),Plus(m,Negate(C1))),FunctionOfQ(Times($($(Derivative(Plus(m,Negate(C1))),f),x),$(g,x)),u,x))));
IIntegrate(6666,Int(Times(u_,Plus(Times(a_DEFAULT,$($(Derivative($p("m1")),f_),x_),$($(Derivative(n_),g_),x_)),Times(a_DEFAULT,$($(Derivative(m_),f_),x_),$($(Derivative($p("n1")),g_),x_)))),x_Symbol),
    Condition(Dist(a,Subst(Int(SimplifyIntegrand(SubstFor(Times($($(Derivative(Plus(m,Negate(C1))),f),x),$($(Derivative(Plus(n,Negate(C1))),g),x)),u,x),x),x),x,Times($($(Derivative(Plus(m,Negate(C1))),f),x),$($(Derivative(Plus(n,Negate(C1))),g),x))),x),And(FreeQ(List(a,f,g,m,n),x),EqQ($s("m1"),Plus(m,Negate(C1))),EqQ($s("n1"),Plus(n,Negate(C1))),FunctionOfQ(Times($($(Derivative(Plus(m,Negate(C1))),f),x),$($(Derivative(Plus(n,Negate(C1))),g),x)),u,x))));
IIntegrate(6667,Int(Times(u_,Power($(f_,x_),p_DEFAULT),Plus(Times(a_DEFAULT,$(g_,x_),$($(Derivative(C1),f_),x_)),Times(b_DEFAULT,$(f_,x_),$($(Derivative(C1),g_),x_)))),x_Symbol),
    Condition(Dist(b,Subst(Int(SimplifyIntegrand(SubstFor(Times(Power($(f,x),Plus(p,C1)),$(g,x)),u,x),x),x),x,Times(Power($(f,x),Plus(p,C1)),$(g,x))),x),And(FreeQ(List(a,b,f,g,p),x),EqQ(a,Times(b,Plus(p,C1))),FunctionOfQ(Times(Power($(f,x),Plus(p,C1)),$(g,x)),u,x))));
IIntegrate(6668,Int(Times(u_,Power($($(Derivative($p("m1")),f_),x_),p_DEFAULT),Plus(Times(a_DEFAULT,$(g_,x_),$($(Derivative(m_),f_),x_)),Times(b_DEFAULT,$($(Derivative(C1),g_),x_),$($(Derivative($p("m1")),f_),x_)))),x_Symbol),
    Condition(Dist(b,Subst(Int(SimplifyIntegrand(SubstFor(Times(Power($($(Derivative(Plus(m,Negate(C1))),f),x),Plus(p,C1)),$(g,x)),u,x),x),x),x,Times(Power($($(Derivative(Plus(m,Negate(C1))),f),x),Plus(p,C1)),$(g,x))),x),And(FreeQ(List(a,b,f,g,m,p),x),EqQ($s("m1"),Plus(m,Negate(C1))),EqQ(a,Times(b,Plus(p,C1))),FunctionOfQ(Times(Power($($(Derivative(Plus(m,Negate(C1))),f),x),Plus(p,C1)),$(g,x)),u,x))));
IIntegrate(6669,Int(Times(u_,Power($(g_,x_),q_DEFAULT),Plus(Times(a_DEFAULT,$(g_,x_),$($(Derivative(m_),f_),x_)),Times(b_DEFAULT,$($(Derivative(C1),g_),x_),$($(Derivative($p("m1")),f_),x_)))),x_Symbol),
    Condition(Dist(a,Subst(Int(SimplifyIntegrand(SubstFor(Times($($(Derivative(Plus(m,Negate(C1))),f),x),Power($(g,x),Plus(q,C1))),u,x),x),x),x,Times($($(Derivative(Plus(m,Negate(C1))),f),x),Power($(g,x),Plus(q,C1)))),x),And(FreeQ(List(a,b,f,g,m,q),x),EqQ($s("m1"),Plus(m,Negate(C1))),EqQ(Times(a,Plus(q,C1)),b),FunctionOfQ(Times($($(Derivative(Plus(m,Negate(C1))),f),x),Power($(g,x),Plus(q,C1))),u,x))));
IIntegrate(6670,Int(Times(u_,Power($($(Derivative($p("m1")),f_),x_),p_DEFAULT),Plus(Times(b_DEFAULT,$($(Derivative($p("m1")),f_),x_),$($(Derivative(n_),g_),x_)),Times(a_DEFAULT,$($(Derivative(m_),f_),x_),$($(Derivative($p("n1")),g_),x_)))),x_Symbol),
    Condition(Dist(b,Subst(Int(SimplifyIntegrand(SubstFor(Times(Power($($(Derivative(Plus(m,Negate(C1))),f),x),Plus(p,C1)),$($(Derivative(Plus(n,Negate(C1))),g),x)),u,x),x),x),x,Times(Power($($(Derivative(Plus(m,Negate(C1))),f),x),Plus(p,C1)),$($(Derivative(Plus(n,Negate(C1))),g),x))),x),And(FreeQ(List(a,b,f,g,m,n,p),x),EqQ($s("m1"),Plus(m,Negate(C1))),EqQ($s("n1"),Plus(n,Negate(C1))),EqQ(a,Times(b,Plus(p,C1))),FunctionOfQ(Times(Power($($(Derivative(Plus(m,Negate(C1))),f),x),Plus(p,C1)),$($(Derivative(Plus(n,Negate(C1))),g),x)),u,x))));
IIntegrate(6671,Int(Times(u_,Power($(f_,x_),p_DEFAULT),Power($(g_,x_),q_DEFAULT),Plus(Times(a_DEFAULT,$(g_,x_),$($(Derivative(C1),f_),x_)),Times(b_DEFAULT,$(f_,x_),$($(Derivative(C1),g_),x_)))),x_Symbol),
    Condition(Dist(Times(a,Power(Plus(p,C1),-1)),Subst(Int(SimplifyIntegrand(SubstFor(Times(Power($(f,x),Plus(p,C1)),Power($(g,x),Plus(q,C1))),u,x),x),x),x,Times(Power($(f,x),Plus(p,C1)),Power($(g,x),Plus(q,C1)))),x),And(FreeQ(List(a,b,f,g,p,q),x),EqQ(Times(a,Plus(q,C1)),Times(b,Plus(p,C1))),FunctionOfQ(Times(Power($(f,x),Plus(p,C1)),Power($(g,x),Plus(q,C1))),u,x))));
IIntegrate(6672,Int(Times(u_,Power($(g_,x_),q_DEFAULT),Power($($(Derivative($p("m1")),f_),x_),p_DEFAULT),Plus(Times(a_DEFAULT,$(g_,x_),$($(Derivative(m_),f_),x_)),Times(b_DEFAULT,$($(Derivative(C1),g_),x_),$($(Derivative($p("m1")),f_),x_)))),x_Symbol),
    Condition(Dist(Times(a,Power(Plus(p,C1),-1)),Subst(Int(SimplifyIntegrand(SubstFor(Times(Power($($(Derivative(Plus(m,Negate(C1))),f),x),Plus(p,C1)),Power($(g,x),Plus(q,C1))),u,x),x),x),x,Times(Power($($(Derivative(Plus(m,Negate(C1))),f),x),Plus(p,C1)),Power($(g,x),Plus(q,C1)))),x),And(FreeQ(List(a,b,f,g,m,p,q),x),EqQ($s("m1"),Plus(m,Negate(C1))),EqQ(Times(a,Plus(q,C1)),Times(b,Plus(p,C1))),FunctionOfQ(Times(Power($($(Derivative(Plus(m,Negate(C1))),f),x),Plus(p,C1)),Power($(g,x),Plus(q,C1))),u,x))));
IIntegrate(6673,Int(Times(u_,Power($($(Derivative($p("m1")),f_),x_),p_DEFAULT),Power($($(Derivative($p("n1")),g_),x_),q_DEFAULT),Plus(Times(b_DEFAULT,$($(Derivative($p("m1")),f_),x_),$($(Derivative(n_),g_),x_)),Times(a_DEFAULT,$($(Derivative(m_),f_),x_),$($(Derivative($p("n1")),g_),x_)))),x_Symbol),
    Condition(Dist(Times(a,Power(Plus(p,C1),-1)),Subst(Int(SimplifyIntegrand(SubstFor(Times(Power($($(Derivative(Plus(m,Negate(C1))),f),x),Plus(p,C1)),Power($($(Derivative(Plus(n,Negate(C1))),g),x),Plus(q,C1))),u,x),x),x),x,Times(Power($($(Derivative(Plus(m,Negate(C1))),f),x),Plus(p,C1)),Power($($(Derivative(Plus(n,Negate(C1))),g),x),Plus(q,C1)))),x),And(FreeQ(List(a,b,f,g,m,n,p,q),x),EqQ($s("m1"),Plus(m,Negate(C1))),EqQ($s("n1"),Plus(n,Negate(C1))),EqQ(Times(a,Plus(q,C1)),Times(b,Plus(p,C1))),FunctionOfQ(Times(Power($($(Derivative(Plus(m,Negate(C1))),f),x),Plus(p,C1)),Power($($(Derivative(Plus(n,Negate(C1))),g),x),Plus(q,C1))),u,x))));
IIntegrate(6674,Int(Times(Power($(g_,x_),-2),Plus(Times($(g_,x_),$($(Derivative(C1),f_),x_)),Times(CN1,$(f_,x_),$($(Derivative(C1),g_),x_)))),x_Symbol),
    Condition(Simp(Times($(f,x),Power($(g,x),-1)),x),FreeQ(List(f,g),x)));
IIntegrate(6675,Int(Times(Power($(f_,x_),-1),Power($(g_,x_),-1),Plus(Times($(g_,x_),$($(Derivative(C1),f_),x_)),Times(CN1,$(f_,x_),$($(Derivative(C1),g_),x_)))),x_Symbol),
    Condition(Simp(Log(Times($(f,x),Power($(g,x),-1))),x),FreeQ(List(f,g),x)));
IIntegrate(6676,Int(Times(u_,Power(Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),n_)),p_)),x_Symbol),
    Condition(Dist(Times(Power(c,IntPart(p)),Power(Times(c,Power(Plus(a,Times(b,x)),n)),FracPart(p)),Power(Power(Plus(a,Times(b,x)),Times(n,FracPart(p))),-1)),Int(Times(u,Power(Plus(a,Times(b,x)),Times(n,p))),x),x),And(FreeQ(List(a,b,c,n,p),x),Not(IntegerQ(p)),Not(MatchQ(u,Condition(Times(Power(x,$p("n1",true)),v_DEFAULT),EqQ(n,Plus($s("n1"),C1))))))));
IIntegrate(6677,Int(Times(u_DEFAULT,Power(Times(c_DEFAULT,Power(Times(d_,Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_)),q_)),x_Symbol),
    Condition(Dist(Times(Power(Times(c,Power(Times(d,Plus(a,Times(b,x))),p)),q),Power(Power(Plus(a,Times(b,x)),Times(p,q)),-1)),Int(Times(u,Power(Plus(a,Times(b,x)),Times(p,q))),x),x),And(FreeQ(List(a,b,c,d,p,q),x),Not(IntegerQ(p)),Not(IntegerQ(q)))));
IIntegrate(6678,Int(Times(u_DEFAULT,Power(Times(c_DEFAULT,Power(Times(d_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),n_)),p_)),q_)),x_Symbol),
    Condition(Dist(Times(Power(Times(c,Power(Times(d,Power(Plus(a,Times(b,x)),n)),p)),q),Power(Power(Plus(a,Times(b,x)),Times(n,p,q)),-1)),Int(Times(u,Power(Plus(a,Times(b,x)),Times(n,p,q))),x),x),And(FreeQ(List(a,b,c,d,n,p,q),x),Not(IntegerQ(p)),Not(IntegerQ(q)))));
IIntegrate(6679,Int(Times(Power(Plus(A_DEFAULT,Times(B_DEFAULT,x_),Times(C_DEFAULT,Sqr(x_))),-1),Power(Plus(a_DEFAULT,Times(b_DEFAULT,$(F_,Times(c_DEFAULT,Sqrt(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),CN1D2))))),n_DEFAULT)),x_Symbol),
    Condition(Dist(Times(C2,e,g,Power(Times(CSymbol,Plus(Times(e,f),Times(CN1,d,g))),-1)),Subst(Int(Times(Power(Plus(a,Times(b,F(Times(c,x)))),n),Power(x,-1)),x),x,Times(Sqrt(Plus(d,Times(e,x))),Power(Plus(f,Times(g,x)),CN1D2))),x),And(FreeQ(List(a,b,c,d,e,f,g,ASymbol,BSymbol,CSymbol,FSymbol),x),EqQ(Plus(Times(CSymbol,d,f),Times(CN1,ASymbol,e,g)),C0),EqQ(Plus(Times(BSymbol,e,g),Times(CN1,CSymbol,Plus(Times(e,f),Times(d,g)))),C0),IGtQ(n,C0))));
IIntegrate(6680,Int(Times(Power(Plus(A_DEFAULT,Times(C_DEFAULT,Sqr(x_))),-1),Power(Plus(a_DEFAULT,Times(b_DEFAULT,$(F_,Times(c_DEFAULT,Sqrt(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),CN1D2))))),n_DEFAULT)),x_Symbol),
    Condition(Dist(Times(C2,e,g,Power(Times(CSymbol,Plus(Times(e,f),Times(CN1,d,g))),-1)),Subst(Int(Times(Power(Plus(a,Times(b,F(Times(c,x)))),n),Power(x,-1)),x),x,Times(Sqrt(Plus(d,Times(e,x))),Power(Plus(f,Times(g,x)),CN1D2))),x),And(FreeQ(List(a,b,c,d,e,f,g,ASymbol,CSymbol,FSymbol),x),EqQ(Plus(Times(CSymbol,d,f),Times(CN1,ASymbol,e,g)),C0),EqQ(Plus(Times(e,f),Times(d,g)),C0),IGtQ(n,C0))));
IIntegrate(6681,Int(Times(Power(Plus(A_DEFAULT,Times(B_DEFAULT,x_),Times(C_DEFAULT,Sqr(x_))),-1),Power(Plus(a_DEFAULT,Times(b_DEFAULT,$(F_,Times(c_DEFAULT,Sqrt(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),CN1D2))))),n_)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(a,Times(b,F(Times(c,Sqrt(Plus(d,Times(e,x))),Power(Plus(f,Times(g,x)),CN1D2))))),n),Power(Plus(ASymbol,Times(BSymbol,x),Times(CSymbol,Sqr(x))),-1)),x),And(FreeQ(List(a,b,c,d,e,f,g,ASymbol,BSymbol,CSymbol,FSymbol,n),x),EqQ(Plus(Times(CSymbol,d,f),Times(CN1,ASymbol,e,g)),C0),EqQ(Plus(Times(BSymbol,e,g),Times(CN1,CSymbol,Plus(Times(e,f),Times(d,g)))),C0),Not(IGtQ(n,C0)))));
IIntegrate(6682,Int(Times(Power(Plus(A_,Times(C_DEFAULT,Sqr(x_))),-1),Power(Plus(a_DEFAULT,Times(b_DEFAULT,$(F_,Times(c_DEFAULT,Sqrt(Plus(d_DEFAULT,Times(e_DEFAULT,x_))),Power(Plus(f_DEFAULT,Times(g_DEFAULT,x_)),CN1D2))))),n_)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(a,Times(b,F(Times(c,Sqrt(Plus(d,Times(e,x))),Power(Plus(f,Times(g,x)),CN1D2))))),n),Power(Plus(ASymbol,Times(CSymbol,Sqr(x))),-1)),x),And(FreeQ(List(a,b,c,d,e,f,g,ASymbol,CSymbol,FSymbol,n),x),EqQ(Plus(Times(CSymbol,d,f),Times(CN1,ASymbol,e,g)),C0),EqQ(Plus(Times(e,f),Times(d,g)),C0),Not(IGtQ(n,C0)))));
IIntegrate(6683,Int(Times(u_,Power(y_,-1)),x_Symbol),
    With(List(Set(q,DerivativeDivides(y,u,x))),Condition(Simp(Times(q,Log(RemoveContent(y,x))),x),Not(FalseQ(q)))));
IIntegrate(6684,Int(Times(u_,Power(w_,-1),Power(y_,-1)),x_Symbol),
    With(List(Set(q,DerivativeDivides(Times(y,w),u,x))),Condition(Simp(Times(q,Log(RemoveContent(Times(y,w),x))),x),Not(FalseQ(q)))));
IIntegrate(6685,Int(Times(u_,Power(y_,m_DEFAULT)),x_Symbol),
    Condition(With(List(Set(q,DerivativeDivides(y,u,x))),Condition(Simp(Times(q,Power(y,Plus(m,C1)),Power(Plus(m,C1),-1)),x),Not(FalseQ(q)))),And(FreeQ(m,x),NeQ(m,CN1))));
IIntegrate(6686,Int(Times(u_,Power(y_,m_DEFAULT),Power(z_,n_DEFAULT)),x_Symbol),
    Condition(With(List(Set(q,DerivativeDivides(Times(y,z),Times(u,Power(z,Plus(n,Negate(m)))),x))),Condition(Simp(Times(q,Power(y,Plus(m,C1)),Power(z,Plus(m,C1)),Power(Plus(m,C1),-1)),x),Not(FalseQ(q)))),And(FreeQ(List(m,n),x),NeQ(m,CN1))));
IIntegrate(6687,Int(u_,x_Symbol),
    With(List(Set(v,SimplifyIntegrand(u,x))),Condition(Int(v,x),SimplerIntegrandQ(v,u,x))));
IIntegrate(6688,Int(Times(u_DEFAULT,Power(Plus(Times(e_DEFAULT,Sqrt(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))))),Times(f_DEFAULT,Sqrt(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_DEFAULT)))))),m_)),x_Symbol),
    Condition(Dist(Power(Plus(Times(a,Sqr(e)),Times(CN1,c,Sqr(f))),m),Int(ExpandIntegrand(Times(u,Power(Power(Plus(Times(e,Sqrt(Plus(a,Times(b,Power(x,n))))),Times(CN1,f,Sqrt(Plus(c,Times(d,Power(x,n)))))),m),-1)),x),x),x),And(FreeQ(List(a,b,c,d,e,f,n),x),ILtQ(m,C0),EqQ(Plus(Times(b,Sqr(e)),Times(CN1,d,Sqr(f))),C0))));
IIntegrate(6689,Int(Times(u_DEFAULT,Power(Plus(Times(e_DEFAULT,Sqrt(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))))),Times(f_DEFAULT,Sqrt(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_DEFAULT)))))),m_)),x_Symbol),
    Condition(Dist(Power(Plus(Times(b,Sqr(e)),Times(CN1,d,Sqr(f))),m),Int(ExpandIntegrand(Times(u,Power(x,Times(m,n)),Power(Power(Plus(Times(e,Sqrt(Plus(a,Times(b,Power(x,n))))),Times(CN1,f,Sqrt(Plus(c,Times(d,Power(x,n)))))),m),-1)),x),x),x),And(FreeQ(List(a,b,c,d,e,f,n),x),ILtQ(m,C0),EqQ(Plus(Times(a,Sqr(e)),Times(CN1,c,Sqr(f))),C0))));
IIntegrate(6690,Int(Times(Power(u_,m_DEFAULT),Power(Plus(Times(a_DEFAULT,Power(u_,n_)),v_),p_DEFAULT),w_),x_Symbol),
    Condition(Int(Times(Power(u,Plus(m,Times(n,p))),Power(Plus(a,Times(v,Power(Power(u,n),-1))),p),w),x),And(FreeQ(List(a,m,n),x),IntegerQ(p),Not(GtQ(n,C0)),Not(FreeQ(v,x)))));
IIntegrate(6691,Int(Times(u_,Power(Plus(c_DEFAULT,Times(d_DEFAULT,v_)),n_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,y_)),m_DEFAULT)),x_Symbol),
    Condition(With(List(Set(q,DerivativeDivides(y,u,x))),Condition(Dist(q,Subst(Int(Times(Power(Plus(a,Times(b,x)),m),Power(Plus(c,Times(d,x)),n)),x),x,y),x),Not(FalseQ(q)))),And(FreeQ(List(a,b,c,d,m,n),x),EqQ(v,y))));
IIntegrate(6692,Int(Times(u_,Power(Plus(c_DEFAULT,Times(d_DEFAULT,v_)),n_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,w_)),p_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,y_)),m_DEFAULT)),x_Symbol),
    Condition(With(List(Set(q,DerivativeDivides(y,u,x))),Condition(Dist(q,Subst(Int(Times(Power(Plus(a,Times(b,x)),m),Power(Plus(c,Times(d,x)),n),Power(Plus(e,Times(f,x)),p)),x),x,y),x),Not(FalseQ(q)))),And(FreeQ(List(a,b,c,d,e,f,m,n,p),x),EqQ(v,y),EqQ(w,y))));
IIntegrate(6693,Int(Times(u_,Power(Plus(c_DEFAULT,Times(d_DEFAULT,v_)),n_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,w_)),p_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,y_)),m_DEFAULT),Power(Plus(g_DEFAULT,Times(h_DEFAULT,z_)),q_DEFAULT)),x_Symbol),
    Condition(With(List(Set(r,DerivativeDivides(y,u,x))),Condition(Dist(r,Subst(Int(Times(Power(Plus(a,Times(b,x)),m),Power(Plus(c,Times(d,x)),n),Power(Plus(e,Times(f,x)),p),Power(Plus(g,Times(h,x)),q)),x),x,y),x),Not(FalseQ(r)))),And(FreeQ(List(a,b,c,d,e,f,g,h,m,n,p,q),x),EqQ(v,y),EqQ(w,y),EqQ(z,y))));
IIntegrate(6694,Int(Times(u_DEFAULT,Plus(a_,Times(b_DEFAULT,Power(y_,n_)))),x_Symbol),
    Condition(With(List(Set(q,DerivativeDivides(y,u,x))),Condition(Plus(Dist(a,Int(u,x),x),Dist(Times(b,q),Subst(Int(Power(x,n),x),x,y),x)),Not(FalseQ(q)))),FreeQ(List(a,b,n),x)));
IIntegrate(6695,Int(Times(u_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(y_,n_))),p_)),x_Symbol),
    Condition(With(List(Set(q,DerivativeDivides(y,u,x))),Condition(Dist(q,Subst(Int(Power(Plus(a,Times(b,Power(x,n))),p),x),x,y),x),Not(FalseQ(q)))),FreeQ(List(a,b,n,p),x)));
IIntegrate(6696,Int(Times(u_DEFAULT,Power(v_,m_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(y_,n_))),p_DEFAULT)),x_Symbol),
    Condition(Module(List(q,r),Condition(Dist(Times(q,r),Subst(Int(Times(Power(x,m),Power(Plus(a,Times(b,Power(x,n))),p)),x),x,y),x),And(Not(FalseQ(Set(r,Divides(Power(y,m),Power(v,m),x)))),Not(FalseQ(Set(q,DerivativeDivides(y,u,x))))))),FreeQ(List(a,b,m,n,p),x)));
IIntegrate(6697,Int(Times(u_DEFAULT,Power(Plus(a_DEFAULT,Times(c_DEFAULT,Power(v_,$p("n2",true))),Times(b_DEFAULT,Power(y_,n_))),p_)),x_Symbol),
    Condition(With(List(Set(q,DerivativeDivides(y,u,x))),Condition(Dist(q,Subst(Int(Power(Plus(a,Times(b,Power(x,n)),Times(c,Power(x,Times(C2,n)))),p),x),x,y),x),Not(FalseQ(q)))),And(FreeQ(List(a,b,c,n,p),x),EqQ($s("n2"),Times(C2,n)),EqQ(v,y))));
IIntegrate(6698,Int(Times(u_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(v_,n_)),Times(c_DEFAULT,Power(w_,$p("n2",true)))),p_DEFAULT),Plus(A_,Times(B_DEFAULT,Power(y_,n_)))),x_Symbol),
    Condition(With(List(Set(q,DerivativeDivides(y,u,x))),Condition(Dist(q,Subst(Int(Times(Plus(ASymbol,Times(BSymbol,Power(x,n))),Power(Plus(a,Times(b,Power(x,n)),Times(c,Power(x,Times(C2,n)))),p)),x),x,y),x),Not(FalseQ(q)))),And(FreeQ(List(a,b,c,ASymbol,BSymbol,n,p),x),EqQ($s("n2"),Times(C2,n)),EqQ(v,y),EqQ(w,y))));
IIntegrate(6699,Int(Times(u_DEFAULT,Power(Plus(a_DEFAULT,Times(c_DEFAULT,Power(w_,$p("n2",true)))),p_DEFAULT),Plus(A_,Times(B_DEFAULT,Power(y_,n_)))),x_Symbol),
    Condition(With(List(Set(q,DerivativeDivides(y,u,x))),Condition(Dist(q,Subst(Int(Times(Plus(ASymbol,Times(BSymbol,Power(x,n))),Power(Plus(a,Times(c,Power(x,Times(C2,n)))),p)),x),x,y),x),Not(FalseQ(q)))),And(FreeQ(List(a,c,ASymbol,BSymbol,n,p),x),EqQ($s("n2"),Times(C2,n)),EqQ(w,y))));
  }
}
}
