package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctions.*;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
/** 
 * IndefiniteIntegrationRules rules from the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi -
 * rule-based integrator</a>.
 *  
 */
public class IndefiniteIntegrationRules16 { 
  public static IAST RULES = List( 
SetDelayed(Int(Times($p("u",true),Power(Times(Power(v_,$p("m",true)),Power(w_,$p("n",true))),p_)),$p("x",$s("Symbol"))),
    Condition(Module(List(Set(r,Simplify(Times(Power(Times(Power(v,m),Power(w,n)),p),Power(Times(Power(v,Times(m,p)),Power(w,Times(n,p))),CN1)))),$s("lst")),Condition(CompoundExpression(Set($s("lst"),SplitFreeFactors(Times(Power(v,Times(m,p)),Power(w,Times(n,p))),x)),Times(Times(r,Part($s("lst"),C1)),Int(Regularize(Times(u,Part($s("lst"),C2)),x),x))),NonzeroQ(Plus(r,Times(CN1,C1))))),And(And(FreeQ(p,x),Not(PowerQ(v))),Not(PowerQ(w))))),
SetDelayed(Int(Times(Times($p("u",true),Power(v_,m_)),Power(w_,n_)),$p("x",$s("Symbol"))),
    Condition(Module(List(Set(q,Cancel(Times(v,Power(w,CN1))))),Condition(Times(Times(Times(Power(v,m),Power(w,n)),Power(Power(q,m),CN1)),Int(Times(u,Power(q,m)),x)),PolynomialQ(q,x))),And(And(FractionQ(List(m,n)),Equal(Plus(m,n),C0)),PolynomialQ(List(v,w),x)))),
SetDelayed(Int(u_,$p("x",$s("Symbol"))),
    Module(List(Set($s("lst"),SubstForFractionalPowerOfLinear(u,x))),Condition(Dist(Times(Part($s("lst"),C2),Part($s("lst"),C4)),Subst(Int(Part($s("lst"),C1),x),x,Power(Part($s("lst"),C3),Times(C1,Power(Part($s("lst"),C2),CN1))))),NotFalseQ($s("lst"))))),
SetDelayed(Int(Times($p("u",true),Power(Plus(a_,Times($p("b",true),Power(v_,C2))),CN1)),$p("x",$s("Symbol"))),
    Condition(Plus(Dist(C1D2,Int(Times(u,Power(Plus(a,Times(Times(b,Rt(Times(Times(CN1,a),Power(b,CN1)),C2)),v)),CN1)),x)),Dist(C1D2,Int(Times(u,Power(Plus(a,Times(CN1,Times(Times(b,Rt(Times(Times(CN1,a),Power(b,CN1)),C2)),v))),CN1)),x))),FreeQ(List(a,b),x))),
SetDelayed(Int(Times($p("u",true),Power(Plus(a_,Times($p("b",true),Power(v_,C2))),m_)),$p("x",$s("Symbol"))),
    Condition(Dist(Power(a,m),Int(Times(Times(u,Power(Plus(C1,Times(Rt(Times(Times(CN1,b),Power(a,CN1)),C2),v)),m)),Power(Plus(C1,Times(CN1,Times(Rt(Times(Times(CN1,b),Power(a,CN1)),C2),v))),m)),x)),And(And(FreeQ(List(a,b),x),IntegerQ(m)),Or(Less(m,CN1),And(Equal(m,CN1),PositiveQ(Times(Times(CN1,b),Power(a,CN1)))))))),
SetDelayed(Int(Times(Times($p("u",true),Power(f_,Plus(a_,v_))),Power(g_,Plus(b_,w_))),$p("x",$s("Symbol"))),
    Condition(Dist(Times(Power(f,a),Power(g,b)),Int(Times(Times(u,Power(f,v)),Power(g,w)),x)),And(And(FreeQ(List(a,b,f,g),x),Not(MatchQ(v,Condition(Plus(c_,t_),FreeQ(c,x))))),Not(MatchQ(w,Condition(Plus(c_,t_),FreeQ(c,x))))))),
SetDelayed(Int(Times($p("u",true),Power(f_,Plus(a_,v_))),$p("x",$s("Symbol"))),
    Condition(Dist(Power(f,a),Int(Times(u,Power(f,v)),x)),And(FreeQ(List(a,f),x),Not(MatchQ(v,Condition(Plus(b_,w_),FreeQ(b,x))))))),
SetDelayed(Int(u_,$p("x",$s("Symbol"))),
    Condition(Dist(C2,Subst(Int(Regularize(Times(SubstForTrig(u,Times(C2,Times(x,Power(Plus(C1,Power(x,C2)),CN1))),Times(Plus(C1,Times(CN1,Power(x,C2))),Power(Plus(C1,Power(x,C2)),CN1)),x,x),Power(Plus(C1,Power(x,C2)),CN1)),x),x),x,Tan(Times(x,C1D2)))),FunctionOfTrigQ(u,x,x))),
SetDelayed(Int(u_,$p("x",$s("Symbol"))),
    Condition(Dist(C2,Subst(Int(Regularize(Times(SubstForHyperbolic(u,Times(C2,Times(x,Power(Plus(C1,Times(CN1,Power(x,C2))),CN1))),Times(Plus(C1,Power(x,C2)),Power(Plus(C1,Times(CN1,Power(x,C2))),CN1)),x,x),Power(Plus(C1,Times(CN1,Power(x,C2))),CN1)),x),x),x,Tanh(Times(x,C1D2)))),FunctionOfHyperbolicQ(u,x,x))),
SetDelayed(Int(u_,$p("x",$s("Symbol"))),
    Module(List(Set($s("lst"),FunctionOfSquareRootOfQuadratic(u,x))),Condition(Dist(C2,Subst(Int(Part($s("lst"),C1),x),x,Part($s("lst"),C2))),Not(FalseQ($s("lst")))))),
SetDelayed(Int(u_,$p("x",$s("Symbol"))),
    Module(List(Set($s("lst"),FunctionOfSquareRootOfQuadratic(u,x))),Condition(Dist(C2,Subst(Int(Part($s("lst"),C1),x),x,Part($s("lst"),C2))),Not(FalseQ($s("lst")))))),
SetDelayed(Int(u_,$p("x",$s("Symbol"))),
    Module(List(Set($s("lst"),FunctionOfSquareRootOfQuadratic(u,x))),Condition(Dist(C2,Subst(Int(Part($s("lst"),C1),x),x,Part($s("lst"),C2))),Not(FalseQ($s("lst")))))),
SetDelayed(Int(Times(u_,Power(Plus(C1,Times(CN1,Power(Plus($p("a",true),Times($p("b",true),x_)),C2))),$p("n",true))),$p("x",$s("Symbol"))),
    Condition(Module(List(Set($s("tmp"),InverseFunctionOfLinear(u,x))),Condition(Dist(Times(C1,Power(b,CN1)),Subst(Int(Regularize(Times(SubstForInverseFunction(u,$s("tmp"),x),Power(Cos(x),Plus(Times(C2,n),C1))),x),x),x,$s("tmp"))),And(NotFalseQ($s("tmp")),SameQ($s("tmp"),ArcSin(Plus(a,Times(b,x))))))),And(FreeQ(List(a,b),x),IntegerQ(Times(C2,n))))),
SetDelayed(Int(Times(u_,Power(Plus(C1,Times(CN1,Power(Plus($p("a",true),Times($p("b",true),x_)),C2))),$p("n",true))),$p("x",$s("Symbol"))),
    Condition(Module(List(Set($s("tmp"),InverseFunctionOfLinear(u,x))),Condition(Times(CN1,Dist(Times(C1,Power(b,CN1)),Subst(Int(Regularize(Times(SubstForInverseFunction(u,$s("tmp"),x),Power(Sin(x),Plus(Times(C2,n),C1))),x),x),x,$s("tmp")))),And(NotFalseQ($s("tmp")),SameQ($s("tmp"),ArcCos(Plus(a,Times(b,x))))))),And(FreeQ(List(a,b),x),IntegerQ(Times(C2,n))))),
SetDelayed(Int(Times(u_,Power(Plus(C1,Power(Plus($p("a",true),Times($p("b",true),x_)),C2)),$p("n",true))),$p("x",$s("Symbol"))),
    Condition(Module(List(Set($s("tmp"),InverseFunctionOfLinear(u,x))),Condition(Dist(Times(C1,Power(b,CN1)),Subst(Int(Regularize(Times(SubstForInverseFunction(u,$s("tmp"),x),Power(Cosh(x),Plus(Times(C2,n),C1))),x),x),x,$s("tmp"))),And(NotFalseQ($s("tmp")),SameQ($s("tmp"),ArcSinh(Plus(a,Times(b,x))))))),And(FreeQ(List(a,b),x),IntegerQ(Times(C2,n))))),
SetDelayed(Int(u_,$p("x",$s("Symbol"))),
    Condition(Module(List(Set($s("lst"),SubstForInverseFunctionOfLinear(u,x))),Condition(Dist(Times(C1,Power(Part($s("lst"),C3),CN1)),Subst(Int(Part($s("lst"),C1),x),x,Part($s("lst"),C2))),NotFalseQ($s("lst")))),Not(NotIntegrableQ(u,x)))),
SetDelayed(Int(u_,$p("x",$s("Symbol"))),
    Condition(Module(List(Set($s("lst"),SubstForInverseFunctionOfQuotientOfLinears(u,x))),Condition(Dist(Part($s("lst"),C3),Subst(Int(Part($s("lst"),C1),x),x,Part($s("lst"),C2))),NotFalseQ($s("lst")))),Not(NotIntegrableQ(u,x))))
  );
}
