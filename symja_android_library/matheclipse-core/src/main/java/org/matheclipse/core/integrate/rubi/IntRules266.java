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
public class IntRules266 { 
  public static IAST RULES = List( 
IIntegrate(6651,Int(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT))))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(c,Power(x,Plus(m,C1)),Power(Times(c,ProductLog(Times(a,Power(x,n)))),Subtract(p,C1)),Power(Times(d,Plus(m,C1)),CN1)),x),Dist(Times(c,Plus(m,Times(n,Subtract(p,C1)),C1),Power(Plus(m,C1),CN1)),Int(Times(Power(x,m),Power(Times(c,ProductLog(Times(a,Power(x,n)))),Subtract(p,C1)),Power(Plus(d,Times(d,ProductLog(Times(a,Power(x,n))))),CN1)),x),x)),And(FreeQ(List(a,c,d,m,n,p),x),NeQ(m,CN1),GtQ(Simplify(Plus(p,Times(Plus(m,C1),Power(n,CN1)))),C1)))),
IIntegrate(6652,Int(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT))))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,C1)),Power(Times(c,ProductLog(Times(a,Power(x,n)))),p),Power(Times(d,Plus(m,Times(n,p),C1)),CN1)),x),Dist(Times(Plus(m,C1),Power(Times(c,Plus(m,Times(n,p),C1)),CN1)),Int(Times(Power(x,m),Power(Times(c,ProductLog(Times(a,Power(x,n)))),Plus(p,C1)),Power(Plus(d,Times(d,ProductLog(Times(a,Power(x,n))))),CN1)),x),x)),And(FreeQ(List(a,c,d,m,n,p),x),NeQ(m,CN1),LtQ(Simplify(Plus(p,Times(Plus(m,C1),Power(n,CN1)))),C0)))),
IIntegrate(6653,Int(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,ProductLog(Times(a_DEFAULT,x_))),p_DEFAULT),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Times(a_DEFAULT,x_)))),CN1)),x_Symbol),
    Condition(Simp(Times(Power(x,m),Gamma(Plus(m,p,C1),Times(CN1,Plus(m,C1),ProductLog(Times(a,x)))),Power(Times(c,ProductLog(Times(a,x))),p),Power(Times(a,d,Plus(m,C1),Exp(Times(m,ProductLog(Times(a,x)))),Power(Times(CN1,Plus(m,C1),ProductLog(Times(a,x))),Plus(m,p))),CN1)),x),And(FreeQ(List(a,c,d,m,p),x),NeQ(m,CN1)))),
IIntegrate(6654,Int(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT))))),CN1)),x_Symbol),
    Condition(Negate(Subst(Int(Times(Power(Times(c,ProductLog(Times(a,Power(Power(x,n),CN1)))),p),Power(Times(Power(x,Plus(m,C2)),Plus(d,Times(d,ProductLog(Times(a,Power(Power(x,n),CN1)))))),CN1)),x),x,Power(x,CN1))),And(FreeQ(List(a,c,d,p),x),NeQ(m,CN1),IntegerQ(m),LtQ(n,C0)))),
IIntegrate(6655,Int(u_,x_Symbol),
    Condition(Subst(Int(SimplifyIntegrand(Times(Plus(x,C1),Exp(x),SubstFor(ProductLog(x),u,x)),x),x),x,ProductLog(x)),FunctionOfQ(ProductLog(x),u,x))),
IIntegrate(6656,Int($($(Derivative(n_),f_),x_),x_Symbol),
    Condition(Simp($($(Derivative(Subtract(n,C1)),f),x),x),FreeQ(List(f,n),x))),
IIntegrate(6657,Int(Times(Power(Times(c_DEFAULT,Power(F_,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),p_DEFAULT),$($(Derivative(n_),f_),x_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(c,Power(FSymbol,Plus(a,Times(b,x)))),p),$($(Derivative(Subtract(n,C1)),f),x)),x),Dist(Times(b,p,Log(FSymbol)),Int(Times(Power(Times(c,Power(FSymbol,Plus(a,Times(b,x)))),p),$($(Derivative(Subtract(n,C1)),f),x)),x),x)),And(FreeQ(List(a,b,c,f,FSymbol,p),x),IGtQ(n,C0)))),
IIntegrate(6658,Int(Times(Power(Times(c_DEFAULT,Power(F_,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),p_DEFAULT),$($(Derivative(n_),f_),x_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(c,Power(FSymbol,Plus(a,Times(b,x)))),p),$($(Derivative(n),f),x),Power(Times(b,p,Log(FSymbol)),CN1)),x),Dist(Power(Times(b,p,Log(FSymbol)),CN1),Int(Times(Power(Times(c,Power(FSymbol,Plus(a,Times(b,x)))),p),$($(Derivative(Plus(n,C1)),f),x)),x),x)),And(FreeQ(List(a,b,c,f,FSymbol,p),x),ILtQ(n,C0)))),
IIntegrate(6659,Int(Times(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),$($(Derivative(n_),f_),x_)),x_Symbol),
    Condition(Subtract(Simp(Times(Sin(Plus(a,Times(b,x))),$($(Derivative(Subtract(n,C1)),f),x)),x),Dist(b,Int(Times(Cos(Plus(a,Times(b,x))),$($(Derivative(Subtract(n,C1)),f),x)),x),x)),And(FreeQ(List(a,b,f),x),IGtQ(n,C0)))),
IIntegrate(6660,Int(Times(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),$($(Derivative(n_),f_),x_)),x_Symbol),
    Condition(Plus(Simp(Times(Cos(Plus(a,Times(b,x))),$($(Derivative(Subtract(n,C1)),f),x)),x),Dist(b,Int(Times(Sin(Plus(a,Times(b,x))),$($(Derivative(Subtract(n,C1)),f),x)),x),x)),And(FreeQ(List(a,b,f),x),IGtQ(n,C0)))),
IIntegrate(6661,Int(Times(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),$($(Derivative(n_),f_),x_)),x_Symbol),
    Condition(Plus(Negate(Simp(Times(Cos(Plus(a,Times(b,x))),$($(Derivative(n),f),x),Power(b,CN1)),x)),Dist(Power(b,CN1),Int(Times(Cos(Plus(a,Times(b,x))),$($(Derivative(Plus(n,C1)),f),x)),x),x)),And(FreeQ(List(a,b,f),x),ILtQ(n,C0)))),
IIntegrate(6662,Int(Times(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),$($(Derivative(n_),f_),x_)),x_Symbol),
    Condition(Subtract(Simp(Times(Sin(Plus(a,Times(b,x))),$($(Derivative(n),f),x),Power(b,CN1)),x),Dist(Power(b,CN1),Int(Times(Sin(Plus(a,Times(b,x))),$($(Derivative(Plus(n,C1)),f),x)),x),x)),And(FreeQ(List(a,b,f),x),ILtQ(n,C0)))),
IIntegrate(6663,Int(Times(u_,$($(Derivative(n_),f_),x_)),x_Symbol),
    Condition(Subst(Int(SimplifyIntegrand(SubstFor($($(Derivative(Subtract(n,C1)),f),x),u,x),x),x),x,$($(Derivative(Subtract(n,C1)),f),x)),And(FreeQ(List(f,n),x),FunctionOfQ($($(Derivative(Subtract(n,C1)),f),x),u,x)))),
IIntegrate(6664,Int(Times(u_,Plus(Times(a_DEFAULT,$(g_,x_),$($(Derivative(C1),f_),x_)),Times(a_DEFAULT,$(f_,x_),$($(Derivative(C1),g_),x_)))),x_Symbol),
    Condition(Dist(a,Subst(Int(SimplifyIntegrand(SubstFor(Times($(f,x),$(g,x)),u,x),x),x),x,Times($(f,x),$(g,x))),x),And(FreeQ(List(a,f,g),x),FunctionOfQ(Times($(f,x),$(g,x)),u,x)))),
IIntegrate(6665,Int(Times(u_,Plus(Times(a_DEFAULT,$(g_,x_),$($(Derivative(m_),f_),x_)),Times(a_DEFAULT,$($(Derivative(C1),g_),x_),$($(Derivative($p("m1")),f_),x_)))),x_Symbol),
    Condition(Dist(a,Subst(Int(SimplifyIntegrand(SubstFor(Times($($(Derivative(Subtract(m,C1)),f),x),$(g,x)),u,x),x),x),x,Times($($(Derivative(Subtract(m,C1)),f),x),$(g,x))),x),And(FreeQ(List(a,f,g,m),x),EqQ($s("m1"),Subtract(m,C1)),FunctionOfQ(Times($($(Derivative(Subtract(m,C1)),f),x),$(g,x)),u,x)))),
IIntegrate(6666,Int(Times(u_,Plus(Times(a_DEFAULT,$($(Derivative($p("m1")),f_),x_),$($(Derivative(n_),g_),x_)),Times(a_DEFAULT,$($(Derivative(m_),f_),x_),$($(Derivative($p("n1")),g_),x_)))),x_Symbol),
    Condition(Dist(a,Subst(Int(SimplifyIntegrand(SubstFor(Times($($(Derivative(Subtract(m,C1)),f),x),$($(Derivative(Subtract(n,C1)),g),x)),u,x),x),x),x,Times($($(Derivative(Subtract(m,C1)),f),x),$($(Derivative(Subtract(n,C1)),g),x))),x),And(FreeQ(List(a,f,g,m,n),x),EqQ($s("m1"),Subtract(m,C1)),EqQ($s("n1"),Subtract(n,C1)),FunctionOfQ(Times($($(Derivative(Subtract(m,C1)),f),x),$($(Derivative(Subtract(n,C1)),g),x)),u,x)))),
IIntegrate(6667,Int(Times(u_,Power($(f_,x_),p_DEFAULT),Plus(Times(a_DEFAULT,$(g_,x_),$($(Derivative(C1),f_),x_)),Times(b_DEFAULT,$(f_,x_),$($(Derivative(C1),g_),x_)))),x_Symbol),
    Condition(Dist(b,Subst(Int(SimplifyIntegrand(SubstFor(Times(Power($(f,x),Plus(p,C1)),$(g,x)),u,x),x),x),x,Times(Power($(f,x),Plus(p,C1)),$(g,x))),x),And(FreeQ(List(a,b,f,g,p),x),EqQ(a,Times(b,Plus(p,C1))),FunctionOfQ(Times(Power($(f,x),Plus(p,C1)),$(g,x)),u,x)))),
IIntegrate(6668,Int(Times(u_,Power($($(Derivative($p("m1")),f_),x_),p_DEFAULT),Plus(Times(a_DEFAULT,$(g_,x_),$($(Derivative(m_),f_),x_)),Times(b_DEFAULT,$($(Derivative(C1),g_),x_),$($(Derivative($p("m1")),f_),x_)))),x_Symbol),
    Condition(Dist(b,Subst(Int(SimplifyIntegrand(SubstFor(Times(Power($($(Derivative(Subtract(m,C1)),f),x),Plus(p,C1)),$(g,x)),u,x),x),x),x,Times(Power($($(Derivative(Subtract(m,C1)),f),x),Plus(p,C1)),$(g,x))),x),And(FreeQ(List(a,b,f,g,m,p),x),EqQ($s("m1"),Subtract(m,C1)),EqQ(a,Times(b,Plus(p,C1))),FunctionOfQ(Times(Power($($(Derivative(Subtract(m,C1)),f),x),Plus(p,C1)),$(g,x)),u,x)))),
IIntegrate(6669,Int(Times(u_,Power($(g_,x_),q_DEFAULT),Plus(Times(a_DEFAULT,$(g_,x_),$($(Derivative(m_),f_),x_)),Times(b_DEFAULT,$($(Derivative(C1),g_),x_),$($(Derivative($p("m1")),f_),x_)))),x_Symbol),
    Condition(Dist(a,Subst(Int(SimplifyIntegrand(SubstFor(Times($($(Derivative(Subtract(m,C1)),f),x),Power($(g,x),Plus(q,C1))),u,x),x),x),x,Times($($(Derivative(Subtract(m,C1)),f),x),Power($(g,x),Plus(q,C1)))),x),And(FreeQ(List(a,b,f,g,m,q),x),EqQ($s("m1"),Subtract(m,C1)),EqQ(Times(a,Plus(q,C1)),b),FunctionOfQ(Times($($(Derivative(Subtract(m,C1)),f),x),Power($(g,x),Plus(q,C1))),u,x)))),
IIntegrate(6670,Int(Times(u_,Power($($(Derivative($p("m1")),f_),x_),p_DEFAULT),Plus(Times(b_DEFAULT,$($(Derivative($p("m1")),f_),x_),$($(Derivative(n_),g_),x_)),Times(a_DEFAULT,$($(Derivative(m_),f_),x_),$($(Derivative($p("n1")),g_),x_)))),x_Symbol),
    Condition(Dist(b,Subst(Int(SimplifyIntegrand(SubstFor(Times(Power($($(Derivative(Subtract(m,C1)),f),x),Plus(p,C1)),$($(Derivative(Subtract(n,C1)),g),x)),u,x),x),x),x,Times(Power($($(Derivative(Subtract(m,C1)),f),x),Plus(p,C1)),$($(Derivative(Subtract(n,C1)),g),x))),x),And(FreeQ(List(a,b,f,g,m,n,p),x),EqQ($s("m1"),Subtract(m,C1)),EqQ($s("n1"),Subtract(n,C1)),EqQ(a,Times(b,Plus(p,C1))),FunctionOfQ(Times(Power($($(Derivative(Subtract(m,C1)),f),x),Plus(p,C1)),$($(Derivative(Subtract(n,C1)),g),x)),u,x)))),
IIntegrate(6671,Int(Times(u_,Power($(f_,x_),p_DEFAULT),Power($(g_,x_),q_DEFAULT),Plus(Times(a_DEFAULT,$(g_,x_),$($(Derivative(C1),f_),x_)),Times(b_DEFAULT,$(f_,x_),$($(Derivative(C1),g_),x_)))),x_Symbol),
    Condition(Dist(Times(a,Power(Plus(p,C1),CN1)),Subst(Int(SimplifyIntegrand(SubstFor(Times(Power($(f,x),Plus(p,C1)),Power($(g,x),Plus(q,C1))),u,x),x),x),x,Times(Power($(f,x),Plus(p,C1)),Power($(g,x),Plus(q,C1)))),x),And(FreeQ(List(a,b,f,g,p,q),x),EqQ(Times(a,Plus(q,C1)),Times(b,Plus(p,C1))),FunctionOfQ(Times(Power($(f,x),Plus(p,C1)),Power($(g,x),Plus(q,C1))),u,x)))),
IIntegrate(6672,Int(Times(u_,Power($(g_,x_),q_DEFAULT),Power($($(Derivative($p("m1")),f_),x_),p_DEFAULT),Plus(Times(a_DEFAULT,$(g_,x_),$($(Derivative(m_),f_),x_)),Times(b_DEFAULT,$($(Derivative(C1),g_),x_),$($(Derivative($p("m1")),f_),x_)))),x_Symbol),
    Condition(Dist(Times(a,Power(Plus(p,C1),CN1)),Subst(Int(SimplifyIntegrand(SubstFor(Times(Power($($(Derivative(Subtract(m,C1)),f),x),Plus(p,C1)),Power($(g,x),Plus(q,C1))),u,x),x),x),x,Times(Power($($(Derivative(Subtract(m,C1)),f),x),Plus(p,C1)),Power($(g,x),Plus(q,C1)))),x),And(FreeQ(List(a,b,f,g,m,p,q),x),EqQ($s("m1"),Subtract(m,C1)),EqQ(Times(a,Plus(q,C1)),Times(b,Plus(p,C1))),FunctionOfQ(Times(Power($($(Derivative(Subtract(m,C1)),f),x),Plus(p,C1)),Power($(g,x),Plus(q,C1))),u,x)))),
IIntegrate(6673,Int(Times(u_,Power($($(Derivative($p("m1")),f_),x_),p_DEFAULT),Power($($(Derivative($p("n1")),g_),x_),q_DEFAULT),Plus(Times(b_DEFAULT,$($(Derivative($p("m1")),f_),x_),$($(Derivative(n_),g_),x_)),Times(a_DEFAULT,$($(Derivative(m_),f_),x_),$($(Derivative($p("n1")),g_),x_)))),x_Symbol),
    Condition(Dist(Times(a,Power(Plus(p,C1),CN1)),Subst(Int(SimplifyIntegrand(SubstFor(Times(Power($($(Derivative(Subtract(m,C1)),f),x),Plus(p,C1)),Power($($(Derivative(Subtract(n,C1)),g),x),Plus(q,C1))),u,x),x),x),x,Times(Power($($(Derivative(Subtract(m,C1)),f),x),Plus(p,C1)),Power($($(Derivative(Subtract(n,C1)),g),x),Plus(q,C1)))),x),And(FreeQ(List(a,b,f,g,m,n,p,q),x),EqQ($s("m1"),Subtract(m,C1)),EqQ($s("n1"),Subtract(n,C1)),EqQ(Times(a,Plus(q,C1)),Times(b,Plus(p,C1))),FunctionOfQ(Times(Power($($(Derivative(Subtract(m,C1)),f),x),Plus(p,C1)),Power($($(Derivative(Subtract(n,C1)),g),x),Plus(q,C1))),u,x)))),
IIntegrate(6674,Int(Times(Power($(g_,x_),CN2),Plus(Times($(g_,x_),$($(Derivative(C1),f_),x_)),Times(CN1,$(f_,x_),$($(Derivative(C1),g_),x_)))),x_Symbol),
    Condition(Simp(Times($(f,x),Power($(g,x),CN1)),x),FreeQ(List(f,g),x)))
  );
}
