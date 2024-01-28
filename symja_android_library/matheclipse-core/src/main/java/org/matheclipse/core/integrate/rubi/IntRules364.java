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
class IntRules364 { 
  public static IAST RULES = List( 
IIntegrate(7280,Integrate(u_,x_Symbol),
    With(list(Set($s("lst"),FunctionOfLinear(u,x))),Condition(Simp(Star(Power(Part($s("lst"),C3),CN1),Subst(Integrate(Part($s("lst"),C1),x),x,Plus(Part($s("lst"),C2),Times(Part($s("lst"),C3),x)))),x),Not(FalseQ($s("lst")))))),
IIntegrate(7281,Integrate(Times(u_,Power(x_,CN1)),x_Symbol),
    Condition(With(list(Set($s("lst"),PowerVariableExpn(u,C0,x))),Condition(Simp(Star(Power(Part($s("lst"),C2),CN1),Subst(Integrate(NormalizeIntegrand(Simplify(Times(Part($s("lst"),C1),Power(x,CN1))),x),x),x,Power(Times(Part($s("lst"),C3),x),Part($s("lst"),C2)))),x),And(Not(FalseQ($s("lst"))),NeQ(Part($s("lst"),C2),C0)))),And(NonsumQ(u),Not(RationalFunctionQ(u,x))))),
IIntegrate(7282,Integrate(Times(u_,Power(x_,m_DEFAULT)),x_Symbol),
    Condition(With(list(Set($s("lst"),PowerVariableExpn(u,Plus(m,C1),x))),Condition(Simp(Star(Power(Part($s("lst"),C2),CN1),Subst(Integrate(NormalizeIntegrand(Simplify(Times(Part($s("lst"),C1),Power(x,CN1))),x),x),x,Power(Times(Part($s("lst"),C3),x),Part($s("lst"),C2)))),x),And(Not(FalseQ($s("lst"))),NeQ(Part($s("lst"),C2),Plus(m,C1))))),And(IntegerQ(m),NeQ(m,CN1),NonsumQ(u),Or(GtQ(m,C0),Not(AlgebraicFunctionQ(u,x)))))),
IIntegrate(7283,Integrate(Times($p("§fx"),Power(x_,m_)),x_Symbol),
    Condition(With(list(Set(k,Denominator(m))),Simp(Star(k,Subst(Integrate(Times(Power(x,Subtract(Times(k,Plus(m,C1)),C1)),$($s("§substpower"),$s("§fx"),x,k)),x),x,Power(x,Power(k,CN1)))),x)),FractionQ(m))),
IIntegrate(7284,Integrate(u_,x_Symbol),
    Condition(With(list(Set($s("lst"),FunctionOfSquareRootOfQuadratic(u,x))),Condition(Simp(Star(C2,Subst(Integrate(Part($s("lst"),C1),x),x,Part($s("lst"),C2))),x),And(Not(FalseQ($s("lst"))),EqQ(Part($s("lst"),C3),C1)))),EulerIntegrandQ(u,x))),
IIntegrate(7285,Integrate(u_,x_Symbol),
    Condition(With(list(Set($s("lst"),FunctionOfSquareRootOfQuadratic(u,x))),Condition(Simp(Star(C2,Subst(Integrate(Part($s("lst"),C1),x),x,Part($s("lst"),C2))),x),And(Not(FalseQ($s("lst"))),EqQ(Part($s("lst"),C3),C2)))),EulerIntegrandQ(u,x))),
IIntegrate(7286,Integrate(u_,x_Symbol),
    Condition(With(list(Set($s("lst"),FunctionOfSquareRootOfQuadratic(u,x))),Condition(Simp(Star(C2,Subst(Integrate(Part($s("lst"),C1),x),x,Part($s("lst"),C2))),x),And(Not(FalseQ($s("lst"))),EqQ(Part($s("lst"),C3),C3)))),EulerIntegrandQ(u,x))),
IIntegrate(7287,Integrate(Power(Plus(a_,Times(b_DEFAULT,Sqr(v_))),CN1),x_Symbol),
    Condition(Plus(Simp(Star(Power(Times(C2,a),CN1),Integrate(Together(Power(Subtract(C1,Times(v,Power(Rt(Times(CN1,a,Power(b,CN1)),C2),CN1))),CN1)),x)),x),Simp(Star(Power(Times(C2,a),CN1),Integrate(Together(Power(Plus(C1,Times(v,Power(Rt(Times(CN1,a,Power(b,CN1)),C2),CN1))),CN1)),x)),x)),FreeQ(list(a,b),x))),
IIntegrate(7288,Integrate(Power(Plus(a_,Times(b_DEFAULT,Power(v_,n_))),CN1),x_Symbol),
    Condition(Simp(Star(Times(C2,Power(Times(a,n),CN1)),Sum(Integrate(Together(Power(Subtract(C1,Times(Sqr(v),Power(Times(Power(CN1,Times(C4,k,Power(n,CN1))),Rt(Times(CN1,a,Power(b,CN1)),Times(C1D2,n))),CN1))),CN1)),x),list(k,C1,Times(C1D2,n)))),x),And(FreeQ(list(a,b),x),IGtQ(Times(C1D2,n),C1)))),
IIntegrate(7289,Integrate(Power(Plus(a_,Times(b_DEFAULT,Power(v_,n_))),CN1),x_Symbol),
    Condition(Simp(Star(Power(Times(a,n),CN1),Sum(Integrate(Together(Power(Subtract(C1,Times(v,Power(Times(Power(CN1,Times(C2,k,Power(n,CN1))),Rt(Times(CN1,a,Power(b,CN1)),n)),CN1))),CN1)),x),list(k,C1,n))),x),And(FreeQ(list(a,b),x),IGtQ(Times(C1D2,Subtract(n,C1)),C0)))),
IIntegrate(7290,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Power(u_,n_DEFAULT))),CN1),v_),x_Symbol),
    Condition(Integrate(ReplaceAll(ExpandIntegrand(Times(PolynomialInSubst(v,u,x),Power(Plus(a,Times(b,Power(x,n))),CN1)),x),Rule(x,u)),x),And(FreeQ(list(a,b),x),IGtQ(n,C0),PolynomialInQ(v,u,x)))),
IIntegrate(7291,Integrate(u_,x_Symbol),
    With(list(Set(v,NormalizeIntegrand(u,x))),Condition(Integrate(v,x),UnsameQ(v,u)))),
IIntegrate(7292,Integrate(u_,x_Symbol),
    With(list(Set(v,ExpandIntegrand(u,x))),Condition(Integrate(v,x),SumQ(v)))),
IIntegrate(7293,Integrate(Times(u_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,m_DEFAULT))),p_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_DEFAULT))),q_DEFAULT)),x_Symbol),
    Condition(Simp(Star(Times(Power(Plus(a,Times(b,Power(x,m))),p),Power(Plus(c,Times(d,Power(x,n))),q),Power(Power(x,Times(m,p)),CN1)),Integrate(Times(u,Power(x,Times(m,p))),x)),x),And(FreeQ(List(a,b,c,d,m,n,p,q),x),EqQ(Plus(a,d),C0),EqQ(Plus(b,c),C0),EqQ(Plus(m,n),C0),EqQ(Plus(p,q),C0)))),
IIntegrate(7294,Integrate(Times(u_,Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(c_DEFAULT,Power(x_,$p("n2",true)))),p_)),x_Symbol),
    Condition(Simp(Star(Times(Sqrt(Plus(a,Times(b,Power(x,n)),Times(c,Power(x,Times(C2,n))))),Power(Times(Power(Times(C4,c),Subtract(p,C1D2)),Plus(b,Times(C2,c,Power(x,n)))),CN1)),Integrate(Times(u,Power(Plus(b,Times(C2,c,Power(x,n))),Times(C2,p))),x)),x),And(FreeQ(List(a,b,c,n,p),x),EqQ($s("n2"),Times(C2,n)),EqQ(Subtract(Sqr(b),Times(C4,a,c)),C0),IntegerQ(Subtract(p,C1D2))))),
IIntegrate(7295,Integrate(u_,x_Symbol),
    With(list(Set($s("lst"),SubstForFractionalPowerOfLinear(u,x))),Condition(Simp(Star(Times(Part($s("lst"),C2),Part($s("lst"),C4)),Subst(Integrate(Part($s("lst"),C1),x),x,Power(Part($s("lst"),C3),Power(Part($s("lst"),C2),CN1)))),x),Not(FalseQ($s("lst")))))),
IIntegrate(7296,Integrate(u_,list(x_Symbol,a_,b_)),
    With(list(Set($s("result"),Integrate(u,x))),Subtract(Limit($s("result"),Rule(x,b)),Limit($s("result"),Rule(x,a))))),
IIntegrate(7297,Integrate(list(u_),x_Symbol),
    Map(Function(Integrate(Slot1,x)),list(u))),
IIntegrate(7298,Integrate(Power(Surd(x_,$p(n, Integer)),p_DEFAULT),x_Symbol),
    Condition(Times(n,x,Power(Surd(x,n),p),Power(Plus(n,p),CN1)),And(FreeQ(p,x),GtQ(n,C0)))),
IIntegrate(7299,Integrate(Times(Power(x_,m_),Power(Surd(x_,$p(n, Integer)),p_DEFAULT)),x_Symbol),
    Condition(Times(Power(x,Plus(C1,m)),Power(Surd(x,n),p),Power(Plus(C1,m,Times(p,Power(n,CN1))),CN1)),And(FreeQ(list(m,p),x),GtQ(n,C0))))
  );
}
