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
SetDelayed(Int($p(u),$p(x,SymbolHead)),
    Module(List(Set($s("lst"),SubstForFractionalPowerOfLinear(u,x))),Condition(Dist(Times(Part($s("lst"),C2),Part($s("lst"),C4)),Subst(Int(Part($s("lst"),C1),x),x,Power(Part($s("lst"),C3),Power(Part($s("lst"),C2),CN1)))),NotFalseQ($s("lst"))))),
SetDelayed(Int(Times(Power(Plus(Times(Power($p(v),C2),$p(b,true)),$p(a)),CN1),$p(u,true)),$p(x,SymbolHead)),
    Condition(Plus(Dist(C1D2,Int(Times(u,Power(Plus(a,Times(Times(b,Rt(Times(Times(CN1,a),Power(b,CN1)),C2)),v)),CN1)),x)),Dist(C1D2,Int(Times(u,Power(Plus(a,Times(CN1,Times(Times(b,Rt(Times(Times(CN1,a),Power(b,CN1)),C2)),v))),CN1)),x))),FreeQ(List(a,b),x))),
SetDelayed(Int(Times(Power(Plus(Times(Power($p(v),C2),$p(b,true)),$p(a)),$p(m)),$p(u,true)),$p(x,SymbolHead)),
    Condition(Dist(Power(a,m),Int(Times(Times(u,Power(Plus(C1,Times(Rt(Times(Times(CN1,b),Power(a,CN1)),C2),v)),m)),Power(Plus(C1,Times(CN1,Times(Rt(Times(Times(CN1,b),Power(a,CN1)),C2),v))),m)),x)),And(And(FreeQ(List(a,b),x),IntIntegerQ(m)),Or(Less(m,CN1),And(Equal(m,CN1),PositiveQ(Times(Times(CN1,b),Power(a,CN1)))))))),
SetDelayed(Int(Times(Power($p(f),Plus($p(a),$p(v))),Power($p(g),Plus($p(b),$p(w))),$p(u,true)),$p(x,SymbolHead)),
    Condition(Dist(Times(Power(f,a),Power(g,b)),Int(Times(Times(u,Power(f,v)),Power(g,w)),x)),And(And(FreeQ(List(a,b,f,g),x),Not(MatchQ(v,Condition(Plus($p(c),$p(t)),FreeQ(c,x))))),Not(MatchQ(w,Condition(Plus($p(c),$p(t)),FreeQ(c,x))))))),
SetDelayed(Int(Times(Power($p(f),Plus($p(a),$p(v))),$p(u,true)),$p(x,SymbolHead)),
    Condition(Dist(Power(f,a),Int(Times(u,Power(f,v)),x)),And(FreeQ(List(a,f),x),Not(MatchQ(v,Condition(Plus($p(b),$p(w)),FreeQ(b,x))))))),
SetDelayed(Int($p(u),$p(x,SymbolHead)),
    Condition(Dist(C2,Subst(Int(Regularize(Times(SubstForTrig(u,Times(C2,Times(x,Power(Plus(C1,Power(x,C2)),CN1))),Times(Plus(C1,Times(CN1,Power(x,C2))),Power(Plus(C1,Power(x,C2)),CN1)),x,x),Power(Plus(C1,Power(x,C2)),CN1)),x),x),x,Tan(Times(C1D2,x)))),FunctionOfTrigQ(u,x,x))),
SetDelayed(Int($p(u),$p(x,SymbolHead)),
    Condition(Dist(C2,Subst(Int(Regularize(Times(SubstForHyperbolic(u,Times(C2,Times(x,Power(Plus(C1,Times(CN1,Power(x,C2))),CN1))),Times(Plus(C1,Power(x,C2)),Power(Plus(C1,Times(CN1,Power(x,C2))),CN1)),x,x),Power(Plus(C1,Times(CN1,Power(x,C2))),CN1)),x),x),x,Tanh(Times(C1D2,x)))),FunctionOfHyperbolicQ(u,x,x))),
SetDelayed(Int($p(u),$p(x,SymbolHead)),
    Module(List(Set($s("lst"),FunctionOfSquareRootOfQuadratic(u,x))),Condition(Dist(C2,Subst(Int(Part($s("lst"),C1),x),x,Part($s("lst"),C2))),Not(FalseQ($s("lst")))))),
SetDelayed(Int($p(u),$p(x,SymbolHead)),
    Module(List(Set($s("lst"),FunctionOfSquareRootOfQuadratic(u,x))),Condition(Dist(C2,Subst(Int(Part($s("lst"),C1),x),x,Part($s("lst"),C2))),Not(FalseQ($s("lst")))))),
SetDelayed(Int($p(u),$p(x,SymbolHead)),
    Module(List(Set($s("lst"),FunctionOfSquareRootOfQuadratic(u,x))),Condition(Dist(C2,Subst(Int(Part($s("lst"),C1),x),x,Part($s("lst"),C2))),Not(FalseQ($s("lst")))))),
SetDelayed(Int(Times(Power(Plus(C1,Times(CN1,Power(Plus(Times($p(b,true),$p(x)),$p(a,true)),C2))),$p(n,true)),$p(u)),$p(x,SymbolHead)),
    Condition(Module(List(Set($s("tmp"),InverseFunctionOfLinear(u,x))),Condition(Dist(Power(b,CN1),Subst(Int(Regularize(Times(SubstForInverseFunction(u,$s("tmp"),x),Power(Cos(x),Plus(Times(C2,n),C1))),x),x),x,$s("tmp"))),And(NotFalseQ($s("tmp")),SameQ($s("tmp"),ArcSin(Plus(a,Times(b,x))))))),And(FreeQ(List(a,b),x),IntIntegerQ(Times(C2,n))))),
SetDelayed(Int(Times(Power(Plus(C1,Times(CN1,Power(Plus(Times($p(b,true),$p(x)),$p(a,true)),C2))),$p(n,true)),$p(u)),$p(x,SymbolHead)),
    Condition(Module(List(Set($s("tmp"),InverseFunctionOfLinear(u,x))),Condition(Times(CN1,Dist(Power(b,CN1),Subst(Int(Regularize(Times(SubstForInverseFunction(u,$s("tmp"),x),Power(Sin(x),Plus(Times(C2,n),C1))),x),x),x,$s("tmp")))),And(NotFalseQ($s("tmp")),SameQ($s("tmp"),ArcCos(Plus(a,Times(b,x))))))),And(FreeQ(List(a,b),x),IntIntegerQ(Times(C2,n))))),
SetDelayed(Int(Times(Power(Plus(C1,Power(Plus(Times($p(b,true),$p(x)),$p(a,true)),C2)),$p(n,true)),$p(u)),$p(x,SymbolHead)),
    Condition(Module(List(Set($s("tmp"),InverseFunctionOfLinear(u,x))),Condition(Dist(Power(b,CN1),Subst(Int(Regularize(Times(SubstForInverseFunction(u,$s("tmp"),x),Power(Cosh(x),Plus(Times(C2,n),C1))),x),x),x,$s("tmp"))),And(NotFalseQ($s("tmp")),SameQ($s("tmp"),ArcSinh(Plus(a,Times(b,x))))))),And(FreeQ(List(a,b),x),IntIntegerQ(Times(C2,n))))),
SetDelayed(Int($p(u),$p(x,SymbolHead)),
    Condition(Module(List(Set($s("lst"),SubstForInverseFunctionOfLinear(u,x))),Condition(Dist(Power(Part($s("lst"),C3),CN1),Subst(Int(Part($s("lst"),C1),x),x,Part($s("lst"),C2))),NotFalseQ($s("lst")))),Not(NotIntegrableQ(u,x)))),
SetDelayed(Int($p(u),$p(x,SymbolHead)),
    Condition(Module(List(Set($s("lst"),SubstForInverseFunctionOfQuotientOfLinears(u,x))),Condition(Dist(Part($s("lst"),C3),Subst(Int(Part($s("lst"),C1),x),x,Part($s("lst"),C2))),NotFalseQ($s("lst")))),Not(NotIntegrableQ(u,x))))
  );
}
