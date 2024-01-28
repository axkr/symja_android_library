package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctions.*;
import org.matheclipse.core.interfaces.IAST;

/** 
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class IntRules360 { 
  public static IAST RULES = List( 
IIntegrate(7201,Integrate(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT))))),CN1)),x_Symbol),
    Condition(Simp(Times(c,Power(x,Plus(m,C1)),Power(Times(c,ProductLog(Times(a,Power(x,n)))),Subtract(p,C1)),Power(Times(d,Plus(m,C1)),CN1)),x),And(FreeQ(List(a,c,d,m,n,p),x),NeQ(m,CN1),EqQ(Plus(m,Times(n,Subtract(p,C1))),CN1)))),
IIntegrate(7202,Integrate(Times(Power(x_,m_DEFAULT),Power(ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT))),p_DEFAULT),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT))))),CN1)),x_Symbol),
    Condition(Simp(Times(Power(a,p),ExpIntegralEi(Times(CN1,p,ProductLog(Times(a,Power(x,n))))),Power(Times(d,n),CN1)),x),And(FreeQ(List(a,d,m,n),x),IntegerQ(p),EqQ(Plus(m,Times(n,p)),CN1)))),
IIntegrate(7203,Integrate(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT)))),p_),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT))))),CN1)),x_Symbol),
    Condition(Simp(Times(Power(a,Subtract(p,C1D2)),Power(c,Subtract(p,C1D2)),Rt(Times(Pi,c,Power(Subtract(p,C1D2),CN1)),C2),Erf(Times(Sqrt(Times(c,ProductLog(Times(a,Power(x,n))))),Power(Rt(Times(c,Power(Subtract(p,C1D2),CN1)),C2),CN1))),Power(Times(d,n),CN1)),x),And(FreeQ(List(a,c,d,m,n),x),NeQ(m,CN1),IntegerQ(Subtract(p,C1D2)),EqQ(Plus(m,Times(n,Subtract(p,C1D2))),CN1),PosQ(Times(c,Power(Subtract(p,C1D2),CN1)))))),
IIntegrate(7204,Integrate(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT)))),p_),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT))))),CN1)),x_Symbol),
    Condition(Simp(Times(Power(a,Subtract(p,C1D2)),Power(c,Subtract(p,C1D2)),Rt(Times(CN1,Pi,c,Power(Subtract(p,C1D2),CN1)),C2),Erfi(Times(Sqrt(Times(c,ProductLog(Times(a,Power(x,n))))),Power(Rt(Times(CN1,c,Power(Subtract(p,C1D2),CN1)),C2),CN1))),Power(Times(d,n),CN1)),x),And(FreeQ(List(a,c,d,m,n),x),NeQ(m,CN1),IntegerQ(Subtract(p,C1D2)),EqQ(Plus(m,Times(n,Subtract(p,C1D2))),CN1),NegQ(Times(c,Power(Subtract(p,C1D2),CN1)))))),
IIntegrate(7205,Integrate(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT))))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(c,Power(x,Plus(m,C1)),Power(Times(c,ProductLog(Times(a,Power(x,n)))),Subtract(p,C1)),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Star(Times(c,Plus(m,Times(n,Subtract(p,C1)),C1),Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,m),Power(Times(c,ProductLog(Times(a,Power(x,n)))),Subtract(p,C1)),Power(Plus(d,Times(d,ProductLog(Times(a,Power(x,n))))),CN1)),x)),x)),And(FreeQ(List(a,c,d,m,n,p),x),NeQ(m,CN1),GtQ(Simplify(Plus(p,Times(Plus(m,C1),Power(n,CN1)))),C1)))),
IIntegrate(7206,Integrate(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT))))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,C1)),Power(Times(c,ProductLog(Times(a,Power(x,n)))),p),Power(Times(d,Plus(m,Times(n,p),C1)),CN1)),x),Simp(Star(Times(Plus(m,C1),Power(Times(c,Plus(m,Times(n,p),C1)),CN1)),Integrate(Times(Power(x,m),Power(Times(c,ProductLog(Times(a,Power(x,n)))),Plus(p,C1)),Power(Plus(d,Times(d,ProductLog(Times(a,Power(x,n))))),CN1)),x)),x)),And(FreeQ(List(a,c,d,m,n,p),x),NeQ(m,CN1),LtQ(Simplify(Plus(p,Times(Plus(m,C1),Power(n,CN1)))),C0)))),
IIntegrate(7207,Integrate(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,ProductLog(Times(a_DEFAULT,x_))),p_DEFAULT),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Times(a_DEFAULT,x_)))),CN1)),x_Symbol),
    Condition(Simp(Times(Power(x,m),Gamma(Plus(m,p,C1),Times(CN1,Plus(m,C1),ProductLog(Times(a,x)))),Power(Times(c,ProductLog(Times(a,x))),p),Power(Times(a,d,Plus(m,C1),Exp(Times(m,ProductLog(Times(a,x)))),Power(Times(CN1,Plus(m,C1),ProductLog(Times(a,x))),Plus(m,p))),CN1)),x),And(FreeQ(List(a,c,d,m,p),x),NeQ(m,CN1)))),
IIntegrate(7208,Integrate(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT),Power(Plus(d_,Times(d_DEFAULT,ProductLog(Times(a_DEFAULT,Power(x_,n_DEFAULT))))),CN1)),x_Symbol),
    Condition(Negate(Subst(Integrate(Times(Power(Times(c,ProductLog(Times(a,Power(Power(x,n),CN1)))),p),Power(Times(Power(x,Plus(m,C2)),Plus(d,Times(d,ProductLog(Times(a,Power(Power(x,n),CN1)))))),CN1)),x),x,Power(x,CN1))),And(FreeQ(List(a,c,d,p),x),NeQ(m,CN1),IntegerQ(m),LtQ(n,C0)))),
IIntegrate(7209,Integrate(u_,x_Symbol),
    Condition(Subst(Integrate(SimplifyIntegrand(Times(Plus(x,C1),Exp(x),SubstFor(ProductLog(x),u,x)),x),x),x,ProductLog(x)),FunctionOfQ(ProductLog(x),u,x))),
IIntegrate(7210,Integrate($($(Derivative(n_),f_),x_),x_Symbol),
    Condition(Simp($($(Derivative(Subtract(n,C1)),f),x),x),FreeQ(list(f,n),x))),
IIntegrate(7211,Integrate(Times(Power(Times(c_DEFAULT,Power(F_,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),p_DEFAULT),$($(Derivative(n_),f_),x_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(c,Power(FSymbol,Plus(a,Times(b,x)))),p),$($(Derivative(Subtract(n,C1)),f),x)),x),Simp(Star(Times(b,p,Log(FSymbol)),Integrate(Times(Power(Times(c,Power(FSymbol,Plus(a,Times(b,x)))),p),$($(Derivative(Subtract(n,C1)),f),x)),x)),x)),And(FreeQ(List(a,b,c,f,FSymbol,p),x),IGtQ(n,C0)))),
IIntegrate(7212,Integrate(Times(Power(Times(c_DEFAULT,Power(F_,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),p_DEFAULT),$($(Derivative(n_),f_),x_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(c,Power(FSymbol,Plus(a,Times(b,x)))),p),$($(Derivative(n),f),x),Power(Times(b,p,Log(FSymbol)),CN1)),x),Simp(Star(Power(Times(b,p,Log(FSymbol)),CN1),Integrate(Times(Power(Times(c,Power(FSymbol,Plus(a,Times(b,x)))),p),$($(Derivative(Plus(n,C1)),f),x)),x)),x)),And(FreeQ(List(a,b,c,f,FSymbol,p),x),ILtQ(n,C0)))),
IIntegrate(7213,Integrate(Times(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),$($(Derivative(n_),f_),x_)),x_Symbol),
    Condition(Subtract(Simp(Times(Sin(Plus(a,Times(b,x))),$($(Derivative(Subtract(n,C1)),f),x)),x),Simp(Star(b,Integrate(Times(Cos(Plus(a,Times(b,x))),$($(Derivative(Subtract(n,C1)),f),x)),x)),x)),And(FreeQ(list(a,b,f),x),IGtQ(n,C0)))),
IIntegrate(7214,Integrate(Times(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),$($(Derivative(n_),f_),x_)),x_Symbol),
    Condition(Plus(Simp(Times(Cos(Plus(a,Times(b,x))),$($(Derivative(Subtract(n,C1)),f),x)),x),Simp(Star(b,Integrate(Times(Sin(Plus(a,Times(b,x))),$($(Derivative(Subtract(n,C1)),f),x)),x)),x)),And(FreeQ(list(a,b,f),x),IGtQ(n,C0)))),
IIntegrate(7215,Integrate(Times(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),$($(Derivative(n_),f_),x_)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Cos(Plus(a,Times(b,x))),$($(Derivative(n),f),x),Power(b,CN1)),x),Simp(Star(Power(b,CN1),Integrate(Times(Cos(Plus(a,Times(b,x))),$($(Derivative(Plus(n,C1)),f),x)),x)),x)),And(FreeQ(list(a,b,f),x),ILtQ(n,C0)))),
IIntegrate(7216,Integrate(Times(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),$($(Derivative(n_),f_),x_)),x_Symbol),
    Condition(Subtract(Simp(Times(Sin(Plus(a,Times(b,x))),$($(Derivative(n),f),x),Power(b,CN1)),x),Simp(Star(Power(b,CN1),Integrate(Times(Sin(Plus(a,Times(b,x))),$($(Derivative(Plus(n,C1)),f),x)),x)),x)),And(FreeQ(list(a,b,f),x),ILtQ(n,C0)))),
IIntegrate(7217,Integrate(Times(u_,$($(Derivative(n_),f_),x_)),x_Symbol),
    Condition(Subst(Integrate(SimplifyIntegrand(SubstFor($($(Derivative(Subtract(n,C1)),f),x),u,x),x),x),x,$($(Derivative(Subtract(n,C1)),f),x)),And(FreeQ(list(f,n),x),FunctionOfQ($($(Derivative(Subtract(n,C1)),f),x),u,x)))),
IIntegrate(7218,Integrate(Times(u_,Plus(Times(a_DEFAULT,$(g_,x_),$($(Derivative(C1),f_),x_)),Times(a_DEFAULT,$(f_,x_),$($(Derivative(C1),g_),x_)))),x_Symbol),
    Condition(Simp(Star(a,Subst(Integrate(SimplifyIntegrand(SubstFor(Times($(f,x),$(g,x)),u,x),x),x),x,Times($(f,x),$(g,x)))),x),And(FreeQ(list(a,f,g),x),FunctionOfQ(Times($(f,x),$(g,x)),u,x)))),
IIntegrate(7219,Integrate(Times(u_,Plus(Times(a_DEFAULT,$(g_,x_),$($(Derivative(m_),f_),x_)),Times(a_DEFAULT,$($(Derivative(C1),g_),x_),$($(Derivative($p("m1")),f_),x_)))),x_Symbol),
    Condition(Simp(Star(a,Subst(Integrate(SimplifyIntegrand(SubstFor(Times($($(Derivative(Subtract(m,C1)),f),x),$(g,x)),u,x),x),x),x,Times($($(Derivative(Subtract(m,C1)),f),x),$(g,x)))),x),And(FreeQ(List(a,f,g,m),x),EqQ($s("m1"),Subtract(m,C1)),FunctionOfQ(Times($($(Derivative(Subtract(m,C1)),f),x),$(g,x)),u,x)))),
IIntegrate(7220,Integrate(Times(u_,Plus(Times(a_DEFAULT,$($(Derivative($p("m1")),f_),x_),$($(Derivative(n_),g_),x_)),Times(a_DEFAULT,$($(Derivative(m_),f_),x_),$($(Derivative($p("n1")),g_),x_)))),x_Symbol),
    Condition(Simp(Star(a,Subst(Integrate(SimplifyIntegrand(SubstFor(Times($($(Derivative(Subtract(m,C1)),f),x),$($(Derivative(Subtract(n,C1)),g),x)),u,x),x),x),x,Times($($(Derivative(Subtract(m,C1)),f),x),$($(Derivative(Subtract(n,C1)),g),x)))),x),And(FreeQ(List(a,f,g,m,n),x),EqQ($s("m1"),Subtract(m,C1)),EqQ($s("n1"),Subtract(n,C1)),FunctionOfQ(Times($($(Derivative(Subtract(m,C1)),f),x),$($(Derivative(Subtract(n,C1)),g),x)),u,x))))
  );
}
