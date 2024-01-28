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
class IntRules13 { 
  public static IAST RULES = List( 
IIntegrate(261,Integrate(Times(Sqrt(Times(c_,x_)),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Simp(Star(Times(Sqrt(Times(c,x)),Power(x,CN1D2)),Integrate(Times(Sqrt(x),Power(Plus(a,Times(b,Sqr(x))),CN1D2)),x)),x),And(FreeQ(list(a,b,c),x),GtQ(Times(CN1,b,Power(a,CN1)),C0)))),
IIntegrate(262,Integrate(Times(Power(Times(c_DEFAULT,x_),m_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Subtract(Simp(Times(c,Power(Times(c,x),Subtract(m,C1)),Power(Plus(a,Times(b,Sqr(x))),Plus(p,C1)),Power(Times(b,Plus(m,Times(C2,p),C1)),CN1)),x),Simp(Star(Times(a,Sqr(c),Subtract(m,C1),Power(Times(b,Plus(m,Times(C2,p),C1)),CN1)),Integrate(Times(Power(Times(c,x),Subtract(m,C2)),Power(Plus(a,Times(b,Sqr(x))),p)),x)),x)),And(FreeQ(List(a,b,c,p),x),GtQ(m,Subtract(C2,C1)),NeQ(Plus(m,Times(C2,p),C1),C0),IntBinomialQ(a,b,c,C2,m,p,x)))),
IIntegrate(263,Integrate(Times(Power(Times(c_DEFAULT,x_),m_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Subtract(Simp(Times(c,Power(Times(c,x),Subtract(m,C1)),Power(Plus(a,Times(b,Sqr(x))),Plus(p,C1)),Power(Times(b,Plus(m,Times(C2,p),C1)),CN1)),x),Simp(Star(Times(a,Sqr(c),Subtract(m,C1),Power(Times(b,Plus(m,Times(C2,p),C1)),CN1)),Integrate(Times(Power(Times(c,x),Subtract(m,C2)),Power(Plus(a,Times(b,Sqr(x))),p)),x)),x)),And(FreeQ(List(a,b,c,m,p),x),SumSimplerQ(m,CN2),NeQ(Plus(m,Times(C2,p),C1),C0),ILtQ(Simplify(Plus(Times(C1D2,Plus(m,C1)),p)),C0)))),
IIntegrate(264,Integrate(Times(Power(Times(c_DEFAULT,x_),m_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(c,x),Plus(m,C1)),Power(Plus(a,Times(b,Sqr(x))),Plus(p,C1)),Power(Times(a,c,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,Plus(m,Times(C2,p),C3),Power(Times(a,Sqr(c),Plus(m,C1)),CN1)),Integrate(Times(Power(Times(c,x),Plus(m,C2)),Power(Plus(a,Times(b,Sqr(x))),p)),x)),x)),And(FreeQ(List(a,b,c,p),x),LtQ(m,CN1),IntBinomialQ(a,b,c,C2,m,p,x)))),
IIntegrate(265,Integrate(Times(Power(Times(c_DEFAULT,x_),m_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Times(c,x),Plus(m,C1)),Power(Plus(a,Times(b,Sqr(x))),Plus(p,C1)),Power(Times(a,c,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,Plus(m,Times(C2,p),C3),Power(Times(a,Sqr(c),Plus(m,C1)),CN1)),Integrate(Times(Power(Times(c,x),Plus(m,C2)),Power(Plus(a,Times(b,Sqr(x))),p)),x)),x)),And(FreeQ(List(a,b,c,m,p),x),SumSimplerQ(m,C2),ILtQ(Simplify(Plus(Times(C1D2,Plus(m,C1)),p)),C0)))),
IIntegrate(266,Integrate(Times(Power(Times(c_DEFAULT,x_),m_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(With(list(Set(k,Denominator(m))),Simp(Star(Times(k,Power(c,CN1)),Subst(Integrate(Times(Power(x,Subtract(Times(k,Plus(m,C1)),C1)),Power(Plus(a,Times(b,Power(x,Times(C2,k)),Power(c,CN2))),p)),x),x,Power(Times(c,x),Power(k,CN1)))),x)),And(FreeQ(List(a,b,c,p),x),FractionQ(m),IntBinomialQ(a,b,c,C2,m,p,x)))),
IIntegrate(267,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Simp(Star(Power(a,Plus(p,Times(C1D2,Plus(m,C1)))),Subst(Integrate(Times(Power(x,m),Power(Power(Subtract(C1,Times(b,Sqr(x))),Plus(p,Times(C1D2,Plus(m,C1)),C1)),CN1)),x),x,Times(x,Power(Plus(a,Times(b,Sqr(x))),CN1D2)))),x),And(FreeQ(list(a,b),x),LtQ(CN1,p,C0),NeQ(p,Negate(C1D2)),IntegersQ(m,Plus(p,Times(C1D2,Plus(m,C1))))))),
IIntegrate(268,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Simp(Star(Times(Power(Times(a,Power(Plus(a,Times(b,Sqr(x))),CN1)),Plus(p,Times(C1D2,Plus(m,C1)))),Power(Plus(a,Times(b,Sqr(x))),Plus(p,Times(C1D2,Plus(m,C1))))),Subst(Integrate(Times(Power(x,m),Power(Power(Subtract(C1,Times(b,Sqr(x))),Plus(p,Times(C1D2,Plus(m,C1)),C1)),CN1)),x),x,Times(x,Power(Plus(a,Times(b,Sqr(x))),CN1D2)))),x),And(FreeQ(list(a,b),x),LtQ(CN1,p,C0),NeQ(p,Negate(C1D2)),IntegerQ(m),LtQ(Denominator(Plus(p,Times(C1D2,Plus(m,C1)))),Denominator(p))))),
IIntegrate(269,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,C1)),Power(Plus(a,Times(b,Sqr(x))),p),Power(Plus(m,C1),CN1)),x),Simp(Star(Times(C2,b,p,Power(Plus(m,C1),CN1)),Integrate(Times(Power(x,Plus(m,C2)),Power(Plus(a,Times(b,Sqr(x))),Subtract(p,C1))),x)),x)),And(FreeQ(list(a,b,m),x),EqQ(Plus(Times(C1D2,Plus(m,C1)),p),C0),GtQ(p,C0)))),
IIntegrate(270,Integrate(Times(Power(Times(c_,x_),m_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Simp(Star(Times(Power(c,IntPart(m)),Power(Times(c,x),FracPart(m)),Power(Power(x,FracPart(m)),CN1)),Integrate(Times(Power(x,m),Power(Plus(a,Times(b,Sqr(x))),p)),x)),x),And(FreeQ(List(a,b,c,m),x),EqQ(Plus(Times(C1D2,Plus(m,C1)),p),C0),GtQ(p,C0)))),
IIntegrate(271,Integrate(Times(Power(Times(c_DEFAULT,x_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(Power(Times(c,x),Plus(m,C1)),Power(Plus(a,Times(b,Sqr(x))),p),Power(Times(c,Plus(m,Times(C2,p),C1)),CN1)),x),Simp(Star(Times(C2,a,p,Power(Plus(m,Times(C2,p),C1),CN1)),Integrate(Times(Power(Times(c,x),m),Power(Plus(a,Times(b,Sqr(x))),Subtract(p,C1))),x)),x)),And(FreeQ(List(a,b,c,m),x),IntegerQ(Plus(p,Simplify(Times(C1D2,Plus(m,C1))))),GtQ(p,C0),NeQ(Plus(m,Times(C2,p),C1),C0)))),
IIntegrate(272,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(With(list(Set(k,Denominator(p))),Simp(Star(Times(k,C1D2,Power(a,Plus(p,Simplify(Times(C1D2,Plus(m,C1)))))),Subst(Integrate(Times(Power(x,Subtract(Times(k,Simplify(Times(C1D2,Plus(m,C1)))),C1)),Power(Power(Subtract(C1,Times(b,Power(x,k))),Plus(p,Simplify(Times(C1D2,Plus(m,C1))),C1)),CN1)),x),x,Times(Power(x,Times(C2,Power(k,CN1))),Power(Power(Plus(a,Times(b,Sqr(x))),Power(k,CN1)),CN1)))),x)),And(FreeQ(list(a,b,m),x),IntegerQ(Plus(p,Simplify(Times(C1D2,Plus(m,C1))))),LtQ(CN1,p,C0)))),
IIntegrate(273,Integrate(Times(Power(Times(c_,x_),m_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Simp(Star(Times(Power(c,IntPart(m)),Power(Times(c,x),FracPart(m)),Power(Power(x,FracPart(m)),CN1)),Integrate(Times(Power(x,m),Power(Plus(a,Times(b,Sqr(x))),p)),x)),x),And(FreeQ(List(a,b,c,m),x),IntegerQ(Plus(p,Simplify(Times(C1D2,Plus(m,C1))))),LtQ(CN1,p,C0)))),
IIntegrate(274,Integrate(Times(Power(Times(c_DEFAULT,x_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Power(Times(c,x),Plus(m,C1)),Power(Plus(a,Times(b,Sqr(x))),Plus(p,C1)),Power(Times(a,c,C2,Plus(p,C1)),CN1)),x),Simp(Star(Times(Plus(m,Times(C2,Plus(p,C1)),C1),Power(Times(a,C2,Plus(p,C1)),CN1)),Integrate(Times(Power(Times(c,x),m),Power(Plus(a,Times(b,Sqr(x))),Plus(p,C1))),x)),x)),And(FreeQ(List(a,b,c,m),x),IntegerQ(Plus(p,Simplify(Times(C1D2,Plus(m,C1))))),LtQ(p,CN1)))),
IIntegrate(275,Integrate(Times(Power(x_,m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Subtract(m,C1)),Power(Times(b,Subtract(m,C1)),CN1)),x),Simp(Star(Times(a,Power(b,CN1)),Integrate(Times(Power(x,Subtract(m,C2)),Power(Plus(a,Times(b,Sqr(x))),CN1)),x)),x)),And(FreeQ(list(a,b,m),x),FractionQ(Times(C1D2,Plus(m,C1))),SumSimplerQ(m,CN2)))),
IIntegrate(276,Integrate(Times(Power(x_,m_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(Power(x,Plus(m,C1)),Power(Times(a,Plus(m,C1)),CN1)),x),Simp(Star(Times(b,Power(a,CN1)),Integrate(Times(Power(x,Simplify(Plus(m,C2))),Power(Plus(a,Times(b,Sqr(x))),CN1)),x)),x)),And(FreeQ(list(a,b,m),x),FractionQ(Times(C1D2,Plus(m,C1))),SumSimplerQ(m,C2)))),
IIntegrate(277,Integrate(Times(Power(Times(c_,x_),m_),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Simp(Star(Times(Power(c,IntPart(m)),Power(Times(c,x),FracPart(m)),Power(Power(x,FracPart(m)),CN1)),Integrate(Times(Power(x,m),Power(Plus(a,Times(b,Sqr(x))),CN1)),x)),x),And(FreeQ(List(a,b,c,m),x),FractionQ(Times(C1D2,Plus(m,C1))),Or(SumSimplerQ(m,C2),SumSimplerQ(m,CN2))))),
IIntegrate(278,Integrate(Times(Power(Times(c_DEFAULT,x_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Simp(Times(Power(a,p),Power(Times(c,x),Plus(m,C1)),Power(Times(c,Plus(m,C1)),CN1),Hypergeometric2F1(Negate(p),Times(C1D2,Plus(m,C1)),Plus(Times(C1D2,Plus(m,C1)),C1),Times(CN1,b,Sqr(x),Power(a,CN1)))),x),And(FreeQ(List(a,b,c,m,p),x),Not(IGtQ(p,C0)),Or(ILtQ(p,C0),GtQ(a,C0))))),
IIntegrate(279,Integrate(Times(Power(Times(c_DEFAULT,x_),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),p_)),x_Symbol),
    Condition(Simp(Star(Times(Power(a,IntPart(p)),Power(Plus(a,Times(b,Sqr(x))),FracPart(p)),Power(Power(Plus(C1,Times(b,Sqr(x),Power(a,CN1))),FracPart(p)),CN1)),Integrate(Times(Power(Times(c,x),m),Power(Plus(C1,Times(b,Sqr(x),Power(a,CN1))),p)),x)),x),And(FreeQ(List(a,b,c,m,p),x),Not(IGtQ(p,C0)),Not(Or(ILtQ(p,C0),GtQ(a,C0)))))),
IIntegrate(280,Integrate(Times(u_DEFAULT,Power(Times(b_DEFAULT,Power(x_,n_)),p_),Power(Times(d_DEFAULT,Power(x_,n_)),q_)),x_Symbol),
    Condition(Simp(Star(Times(Power(b,IntPart(p)),Power(d,IntPart(q)),Power(Times(b,Power(x,n)),FracPart(p)),Power(Times(d,Power(x,n)),FracPart(q)),Power(Power(x,Times(n,Plus(FracPart(p),FracPart(q)))),CN1)),Integrate(Times(u,Power(x,Times(n,Plus(p,q)))),x)),x),FreeQ(List(b,d,n,p,q),x)))
  );
}
