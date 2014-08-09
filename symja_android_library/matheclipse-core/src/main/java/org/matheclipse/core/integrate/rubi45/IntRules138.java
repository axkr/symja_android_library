package org.matheclipse.core.integrate.rubi45;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi45.UtilityFunctionCtors.*;
import static org.matheclipse.core.integrate.rubi45.UtilityFunctions.*;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/** 
 * IndefiniteIntegrationRules from the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi -
 * rule-based integrator</a>.
 *  
 */
public class IntRules138 { 
  public static IAST RULES = List( 
ISetDelayed(Int(Times(Power(Plus(Times(Power(x_,m_DEFAULT),a_DEFAULT),Times(Sqrt(Times(Power(x_,pn_),c_DEFAULT)),b_DEFAULT)),CN1),u_DEFAULT),x_Symbol),
    Condition(Int(Times(u,Plus(Times(a,Power(x,m)),Times(CN1,b,Sqrt(Times(c,Power(x,pn))))),Power(Plus(Times(Sqr(a),Power(x,Times(C2,m))),Times(CN1,Sqr(b),c,Power(x,pn))),CN1)),x),FreeQ(List(a,b,c,m,pn),x))),
ISetDelayed(Int(u_,x_Symbol),
    Module(List(Set($s("lst"),FunctionOfLinear(u,x))),Condition(Dist(Power(Part($s("lst"),C3),CN1),Subst(Int(Part($s("lst"),C1),x),x,Plus(Part($s("lst"),C2),Times(Part($s("lst"),C3),x))),x),Not(FalseQ($s("lst")))))),
ISetDelayed(Int(Times(Power(x_,CN1),u_),x_Symbol),
    Condition(Module(List(Set($s("lst"),PowerVariableExpn(u,C0,x))),Condition(Times(Power(Part($s("lst"),C2),CN1),Subst(Int(NormalizeIntegrand(Simplify(Times(Part($s("lst"),C1),Power(x,CN1))),x),x),x,Power(Times(Part($s("lst"),C3),x),Part($s("lst"),C2)))),And(Not(FalseQ($s("lst"))),NonzeroQ(Part($s("lst"),C2))))),And(NonsumQ(u),Not(RationalFunctionQ(u,x))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),u_),x_Symbol),
    Condition(Module(List(Set($s("lst"),PowerVariableExpn(u,Plus(m,C1),x))),Condition(Times(Power(Part($s("lst"),C2),CN1),Subst(Int(NormalizeIntegrand(Simplify(Times(Part($s("lst"),C1),Power(x,CN1))),x),x),x,Power(Times(Part($s("lst"),C3),x),Part($s("lst"),C2)))),And(NotFalseQ($s("lst")),NonzeroQ(Plus(Part($s("lst"),C2),Times(CN1,m),Times(CN1,C1)))))),And(And(And(IntegerQ(m),Unequal(m,CN1)),NonsumQ(u)),Or(Greater(m,C0),Not(AlgebraicFunctionQ(u,x)))))),
ISetDelayed(Int(Times(Power(x_,m_),u_),x_Symbol),
    Condition(Module(List(Set(k,Denominator(m))),Times(k,Subst(Int(Times(Power(x,Plus(Times(k,Plus(m,C1)),Times(CN1,C1))),ReplaceAll(u,Rule(x,Power(x,k)))),x),x,Power(x,Power(k,CN1))))),FractionQ(m))),
ISetDelayed(Int(u_,x_Symbol),
    Condition(Module(List(Set($s("lst"),FunctionOfSquareRootOfQuadratic(u,x))),Condition(Times(C2,Subst(Int(Part($s("lst"),C1),x),x,Part($s("lst"),C2))),Not(FalseQ($s("lst"))))),EulerIntegrandQ(u,x))),
ISetDelayed(Int(u_,x_Symbol),
    Condition(Module(List(Set($s("lst"),FunctionOfSquareRootOfQuadratic(u,x))),Condition(Times(C2,Subst(Int(Part($s("lst"),C1),x),x,Part($s("lst"),C2))),Not(FalseQ($s("lst"))))),EulerIntegrandQ(u,x))),
ISetDelayed(Int(u_,x_Symbol),
    Condition(Module(List(Set($s("lst"),FunctionOfSquareRootOfQuadratic(u,x))),Condition(Times(C2,Subst(Int(Part($s("lst"),C1),x),x,Part($s("lst"),C2))),Not(FalseQ($s("lst"))))),EulerIntegrandQ(u,x))),
ISetDelayed(Int(Power(Plus(Times(Sqr(v_),b_DEFAULT),a_),CN1),x_Symbol),
    Condition(Plus(Times(Power(Times(C2,a),CN1),Int(Together(Power(Plus(C1,Times(CN1,v,Power(Rt(Times(CN1,a,Power(b,CN1)),C2),CN1))),CN1)),x)),Times(Power(Times(C2,a),CN1),Int(Together(Power(Plus(C1,Times(v,Power(Rt(Times(CN1,a,Power(b,CN1)),C2),CN1))),CN1)),x))),FreeQ(List(a,b),x))),
ISetDelayed(Int(Power(Plus(Times(Power(v_,pn_),b_DEFAULT),a_),CN1),x_Symbol),
    Condition(Dist(Times(C2,Power(Times(a,pn),CN1)),Sum(Int(Together(Power(Plus(C1,Times(CN1,Sqr(v),Power(Times(Power(CN1,Times(C4,k,Power(pn,CN1))),Rt(Times(CN1,a,Power(b,CN1)),Times(C1D2,pn))),CN1))),CN1)),x),List(k,C1,Times(C1D2,pn))),x),And(And(FreeQ(List(a,b),x),EvenQ(pn)),Greater(pn,C2)))),
ISetDelayed(Int(Power(Plus(Times(Power(v_,pn_),b_DEFAULT),a_),CN1),x_Symbol),
    Condition(Dist(Power(Times(a,pn),CN1),Sum(Int(Together(Power(Plus(C1,Times(CN1,v,Power(Times(Power(CN1,Times(C2,k,Power(pn,CN1))),Rt(Times(CN1,a,Power(b,CN1)),pn)),CN1))),CN1)),x),List(k,C1,pn)),x),And(And(FreeQ(List(a,b),x),OddQ(pn)),Greater(pn,C1)))),
ISetDelayed(Int(Times(Power(Plus(Times(Power(u_,pn_DEFAULT),b_DEFAULT),a_),CN1),v_),x_Symbol),
    Condition(Int(ReplaceAll(ExpandIntegrand(Times(PolynomialInSubst(v,u,x),Power(Plus(a,Times(b,Power(x,pn))),CN1)),x),Rule(x,u)),x),And(And(FreeQ(List(a,b),x),PositiveIntegerQ(pn)),PolynomialInQ(v,u,x)))),
ISetDelayed(Int(u_,x_Symbol),
    Module(List(Set(v,NormalizeIntegrand(u,x))),Condition(Int(v,x),UnsameQ(v,u)))),
ISetDelayed(Int(u_,x_Symbol),
    Module(List(Set(v,ExpandIntegrand(u,x))),Condition(Int(v,x),SumQ(v)))),
ISetDelayed(Int(Times(Power(Plus(Times(Power(x_,m_DEFAULT),b_DEFAULT),a_DEFAULT),p_DEFAULT),Power(Plus(Times(Power(x_,pn_DEFAULT),pd_DEFAULT),c_DEFAULT),q_DEFAULT),u_DEFAULT),x_Symbol),
    Condition(Times(Power(Plus(a,Times(b,Power(x,m))),p),Power(Plus(c,Times(pd,Power(x,pn))),q),Power(Power(x,Times(m,p)),CN1),Int(Times(u,Power(x,Times(m,p))),x)),And(And(And(And(FreeQ(List(a,b,c,pd,m,pn,p,q),x),ZeroQ(Plus(a,pd))),ZeroQ(Plus(b,c))),ZeroQ(Plus(m,pn))),ZeroQ(Plus(p,q))))),
ISetDelayed(Int(Times(Power(Plus(Times(Power(x_,pn_DEFAULT),b_DEFAULT),Times(Power(x_,j_DEFAULT),c_DEFAULT),a_),p_),u_),x_Symbol),
    Condition(Times(Sqrt(Plus(a,Times(b,Power(x,pn)),Times(c,Power(x,Times(C2,pn))))),Power(Times(Power(Times(C4,c),Plus(p,Times(CN1,C1D2))),Plus(b,Times(C2,c,Power(x,pn)))),CN1),Int(Times(u,Power(Plus(b,Times(C2,c,Power(x,pn))),Times(C2,p))),x)),And(And(And(FreeQ(List(a,b,c,pn,p),x),ZeroQ(Plus(j,Times(CN1,C2,pn)))),ZeroQ(Plus(Sqr(b),Times(CN1,C4,a,c)))),IntegerQ(Plus(p,Times(CN1,C1D2)))))),
ISetDelayed(Int(u_,x_Symbol),
    Module(List(Set($s("lst"),SubstForFractionalPowerOfLinear(u,x))),Condition(Times(Part($s("lst"),C2),Part($s("lst"),C4),Subst(Int(Part($s("lst"),C1),x),x,Power(Part($s("lst"),C3),Power(Part($s("lst"),C2),CN1)))),NotFalseQ($s("lst")))))
  );
}
