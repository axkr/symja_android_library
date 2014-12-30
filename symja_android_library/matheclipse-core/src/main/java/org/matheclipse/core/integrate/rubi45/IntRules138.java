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
ISetDelayed(Int(Times(u_DEFAULT,Power(Plus(Times(a_DEFAULT,Power(x_,m_DEFAULT)),Times(b_DEFAULT,Sqrt(Times(c_DEFAULT,Power(x_,n_))))),CN1)),x_Symbol),
    Condition(Int(Times(u,Plus(Times(a,Power(x,m)),Times(CN1,b,Sqrt(Times(c,Power(x,n))))),Power(Plus(Times(Sqr(a),Power(x,Times(C2,m))),Times(CN1,Sqr(b),c,Power(x,n))),CN1)),x),FreeQ(List(a,b,c,m,n),x))),
ISetDelayed(Int(u_,x_Symbol),
    Module(List(Set($s("lst"),FunctionOfLinear(u,x))),Condition(Dist(Power(Part($s("lst"),C3),CN1),Subst(Int(Part($s("lst"),C1),x),x,Plus(Part($s("lst"),C2),Times(Part($s("lst"),C3),x))),x),Not(FalseQ($s("lst")))))),
ISetDelayed(Int(Times(u_,Power(x_,CN1)),x_Symbol),
    Condition(Module(List(Set($s("lst"),PowerVariableExpn(u,C0,x))),Condition(Times(Power(Part($s("lst"),C2),CN1),Subst(Int(NormalizeIntegrand(Simplify(Times(Part($s("lst"),C1),Power(x,CN1))),x),x),x,Power(Times(Part($s("lst"),C3),x),Part($s("lst"),C2)))),And(Not(FalseQ($s("lst"))),NonzeroQ(Part($s("lst"),C2))))),And(NonsumQ(u),Not(RationalFunctionQ(u,x))))),
ISetDelayed(Int(Times(u_,Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Module(List(Set($s("lst"),PowerVariableExpn(u,Plus(m,C1),x))),Condition(Times(Power(Part($s("lst"),C2),CN1),Subst(Int(NormalizeIntegrand(Simplify(Times(Part($s("lst"),C1),Power(x,CN1))),x),x),x,Power(Times(Part($s("lst"),C3),x),Part($s("lst"),C2)))),And(NotFalseQ($s("lst")),NonzeroQ(Plus(Part($s("lst"),C2),Times(CN1,m),Times(CN1,C1)))))),And(And(And(IntegerQ(m),Unequal(m,CN1)),NonsumQ(u)),Or(Greater(m,C0),Not(AlgebraicFunctionQ(u,x)))))),
ISetDelayed(Int(Times(u_,Power(x_,m_)),x_Symbol),
    Condition(Module(List(Set(k,Denominator(m))),Times(k,Subst(Int(Times(Power(x,Plus(Times(k,Plus(m,C1)),Times(CN1,C1))),ReplaceAll(u,Rule(x,Power(x,k)))),x),x,Power(x,Power(k,CN1))))),FractionQ(m))),
ISetDelayed(Int(u_,x_Symbol),
    Condition(Module(List(Set($s("lst"),FunctionOfSquareRootOfQuadratic(u,x))),Condition(Times(C2,Subst(Int(Part($s("lst"),C1),x),x,Part($s("lst"),C2))),Not(FalseQ($s("lst"))))),EulerIntegrandQ(u,x))),
ISetDelayed(Int(u_,x_Symbol),
    Condition(Module(List(Set($s("lst"),FunctionOfSquareRootOfQuadratic(u,x))),Condition(Times(C2,Subst(Int(Part($s("lst"),C1),x),x,Part($s("lst"),C2))),Not(FalseQ($s("lst"))))),EulerIntegrandQ(u,x))),
ISetDelayed(Int(u_,x_Symbol),
    Condition(Module(List(Set($s("lst"),FunctionOfSquareRootOfQuadratic(u,x))),Condition(Times(C2,Subst(Int(Part($s("lst"),C1),x),x,Part($s("lst"),C2))),Not(FalseQ($s("lst"))))),EulerIntegrandQ(u,x))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Sqr(v_))),CN1),x_Symbol),
    Condition(Plus(Times(Power(Times(C2,a),CN1),Int(Together(Power(Plus(C1,Times(CN1,v,Power(Rt(Times(CN1,a,Power(b,CN1)),C2),CN1))),CN1)),x)),Times(Power(Times(C2,a),CN1),Int(Together(Power(Plus(C1,Times(v,Power(Rt(Times(CN1,a,Power(b,CN1)),C2),CN1))),CN1)),x))),FreeQ(List(a,b),x))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Power(v_,n_))),CN1),x_Symbol),
    Condition(Dist(Times(C2,Power(Times(a,n),CN1)),Sum(Int(Together(Power(Plus(C1,Times(CN1,Sqr(v),Power(Times(Power(CN1,Times(C4,k,Power(n,CN1))),Rt(Times(CN1,a,Power(b,CN1)),Times(C1D2,n))),CN1))),CN1)),x),List(k,C1,Times(C1D2,n))),x),And(And(FreeQ(List(a,b),x),EvenQ(n)),Greater(n,C2)))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Power(v_,n_))),CN1),x_Symbol),
    Condition(Dist(Power(Times(a,n),CN1),Sum(Int(Together(Power(Plus(C1,Times(CN1,v,Power(Times(Power(CN1,Times(C2,k,Power(n,CN1))),Rt(Times(CN1,a,Power(b,CN1)),n)),CN1))),CN1)),x),List(k,C1,n)),x),And(And(FreeQ(List(a,b),x),OddQ(n)),Greater(n,C1)))),
ISetDelayed(Int(Times(v_,Power(Plus(a_,Times(b_DEFAULT,Power(u_,n_DEFAULT))),CN1)),x_Symbol),
    Condition(Int(ReplaceAll(ExpandIntegrand(Times(PolynomialInSubst(v,u,x),Power(Plus(a,Times(b,Power(x,n))),CN1)),x),Rule(x,u)),x),And(And(FreeQ(List(a,b),x),PositiveIntegerQ(n)),PolynomialInQ(v,u,x)))),
ISetDelayed(Int(u_,x_Symbol),
    Module(List(Set(v,NormalizeIntegrand(u,x))),Condition(Int(v,x),UnsameQ(v,u)))),
ISetDelayed(Int(u_,x_Symbol),
    Module(List(Set(v,ExpandIntegrand(u,x))),Condition(Int(v,x),SumQ(v)))),
ISetDelayed(Int(Times(u_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,m_DEFAULT))),p_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,Power(x_,n_DEFAULT))),q_DEFAULT)),x_Symbol),
    Condition(Times(Power(Plus(a,Times(b,Power(x,m))),p),Power(Plus(c,Times(d,Power(x,n))),q),Power(Power(x,Times(m,p)),CN1),Int(Times(u,Power(x,Times(m,p))),x)),And(And(And(And(FreeQ(List(a,b,c,d,m,n,p,q),x),ZeroQ(Plus(a,d))),ZeroQ(Plus(b,c))),ZeroQ(Plus(m,n))),ZeroQ(Plus(p,q))))),
ISetDelayed(Int(Times(u_,Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(c_DEFAULT,Power(x_,j_DEFAULT))),p_)),x_Symbol),
    Condition(Times(Sqrt(Plus(a,Times(b,Power(x,n)),Times(c,Power(x,Times(C2,n))))),Power(Times(Power(Times(C4,c),Plus(p,Times(CN1,C1D2))),Plus(b,Times(C2,c,Power(x,n)))),CN1),Int(Times(u,Power(Plus(b,Times(C2,c,Power(x,n))),Times(C2,p))),x)),And(And(And(FreeQ(List(a,b,c,n,p),x),ZeroQ(Plus(j,Times(CN1,C2,n)))),ZeroQ(Plus(Sqr(b),Times(CN1,C4,a,c)))),IntegerQ(Plus(p,Times(CN1,C1D2)))))),
ISetDelayed(Int(u_,x_Symbol),
    Module(List(Set($s("lst"),SubstForFractionalPowerOfLinear(u,x))),Condition(Times(Part($s("lst"),C2),Part($s("lst"),C4),Subst(Int(Part($s("lst"),C1),x),x,Power(Part($s("lst"),C3),Power(Part($s("lst"),C2),CN1)))),NotFalseQ($s("lst")))))
  );
}
