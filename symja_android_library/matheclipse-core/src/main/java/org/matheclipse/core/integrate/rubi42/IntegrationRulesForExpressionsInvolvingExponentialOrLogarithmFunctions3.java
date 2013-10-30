package org.matheclipse.core.integrate.rubi42;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi42.UtilityFunctionCtors.*;
import static org.matheclipse.core.integrate.rubi42.UtilityFunctions.*;

import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
/** 
 * IntegrationRules rules from the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi -
 * rule-based integrator</a>.
 *  
 */
public class IntegrationRulesForExpressionsInvolvingExponentialOrLogarithmFunctions3 { 
  public static IAST RULES = List( 
SetDelayed(Int(Times(Log($p(u)),Power(Plus(Times(Power($p(x),C2),$p(c,true)),$p(a)),CN1)),$p(x,SymbolHead)),
    Condition(Module(List(Set(w,Block(List(Set($s("ShowSteps"),False),Set($s("StepCounter"),Null)),Int(Power(Plus(a,Times(c,Power(x,C2))),CN1),x)))),Plus(Times(w,Log(u)),Times(CN1,Int(SimplifyIntegrand(Times(w,D(u,x),Power(u,CN1)),x),x)))),And(FreeQ(List(a,c),x),InverseFunctionFreeQ(u,x)))),
SetDelayed(Int(Times(Log($p(u)),Power($p(u),Times($p(a,true),$p(x)))),$p(x,SymbolHead)),
    Condition(Plus(Times(Power(u,Times(a,x)),Power(a,CN1)),Times(CN1,Int(SimplifyIntegrand(Times(x,Power(u,Plus(Times(a,x),Times(CN1,C1))),D(u,x)),x),x))),And(FreeQ(a,x),InverseFunctionFreeQ(u,x)))),
SetDelayed(Int(Times(Log($p(u)),$p(v)),$p(x,SymbolHead)),
    Condition(Module(List(Set(w,Block(List(Set($s("ShowSteps"),False),Set($s("StepCounter"),Null)),Int(v,x)))),Condition(Plus(Dist(Log(u),w,x),Times(CN1,Int(SimplifyIntegrand(Times(w,D(u,x),Power(u,CN1)),x),x))),InverseFunctionFreeQ(w,x))),InverseFunctionFreeQ(u,x))),
SetDelayed(Int(Times(Log($p(v)),Log($p(w))),$p(x,SymbolHead)),
    Condition(Plus(Times(x,Log(v),Log(w)),Times(CN1,Int(SimplifyIntegrand(Times(x,Log(w),D(v,x),Power(v,CN1)),x),x)),Times(CN1,Int(SimplifyIntegrand(Times(x,Log(v),D(w,x),Power(w,CN1)),x),x))),And(InverseFunctionFreeQ(v,x),InverseFunctionFreeQ(w,x)))),
SetDelayed(Int(Times(Log($p(v)),Log($p(w)),$p(u)),$p(x,SymbolHead)),
    Condition(Module(List(Set(z,Block(List(Set($s("ShowSteps"),False),Set($s("StepCounter"),Null)),Int(u,x)))),Condition(Plus(Dist(Times(Log(v),Log(w)),z,x),Times(CN1,Int(SimplifyIntegrand(Times(z,Log(w),D(v,x),Power(v,CN1)),x),x)),Times(CN1,Int(SimplifyIntegrand(Times(z,Log(v),D(w,x),Power(w,CN1)),x),x))),InverseFunctionFreeQ(z,x))),And(InverseFunctionFreeQ(v,x),InverseFunctionFreeQ(w,x)))),
SetDelayed(Int(Power(Log(Times(Power(Plus(Times(Power($p(x),CN1),$p(b,true)),$p(a)),$p(n,true)),$p(c,true))),$p(p)),$p(x,SymbolHead)),
    Condition(Plus(Times(Plus(b,Times(a,x)),Power(Log(Times(c,Power(Plus(a,Times(b,Power(x,CN1))),n))),p),Power(a,CN1)),Times(b,Power(a,CN1),n,p,Int(Times(Power(Log(Times(c,Power(Plus(a,Times(b,Power(x,CN1))),n))),Plus(p,Times(CN1,C1))),Power(x,CN1)),x))),And(FreeQ(List(a,b,c,n),x),PositiveIntegerQ(p)))),
SetDelayed(Int(Power(Log(Times(Power(Plus(Times(Power($p(x),C2),$p(c,true)),Times($p(b,true),$p(x)),$p(a,true)),$p(n,true)),$p(d,true))),$p(p,true)),$p(x,SymbolHead)),
    Condition(Int(Power(Log(Times(d,Power(Plus(b,Times(C2,c,x)),Times(C2,n)),Power(Times(Power(C4,n),Power(c,n)),CN1))),p),x),And(And(FreeQ(List(a,b,c,d,p),x),IntegerQ(n)),ZeroQ(Plus(Power(b,C2),Times(CN1,C4,a,c)))))),
SetDelayed(Int(Power(Log(Times(Power(Plus(Times(Power($p(x),C2),$p(c,true)),Times($p(b,true),$p(x)),$p(a,true)),$p(n,true)),$p(d,true))),C2),$p(x,SymbolHead)),
    Condition(Plus(Times(x,Power(Log(Times(d,Power(Plus(a,Times(b,x),Times(c,Power(x,C2))),n))),C2)),Times(CN1,C2,b,n,Int(Times(x,Log(Times(d,Power(Plus(a,Times(b,x),Times(c,Power(x,C2))),n))),Power(Plus(a,Times(b,x),Times(c,Power(x,C2))),CN1)),x)),Times(CN1,C4,c,n,Int(Times(Power(x,C2),Log(Times(d,Power(Plus(a,Times(b,x),Times(c,Power(x,C2))),n))),Power(Plus(a,Times(b,x),Times(c,Power(x,C2))),CN1)),x))),And(FreeQ(List(a,b,c,d,n),x),Not(And(IntegerQ(n),ZeroQ(Plus(Power(b,C2),Times(CN1,C4,a,c)))))))),
SetDelayed(Int(Power(Log(Times(Power(Times(Power($p(x),$p(n,true)),$p(b,true)),$p(p)),$p(a,true))),$p(q,true)),$p(x,SymbolHead)),
    Condition(Subst(Int(Power(Log(Power(x,Times(n,p))),q),x),Power(x,Times(n,p)),Times(a,Power(Times(b,Power(x,n)),p))),FreeQ(List(a,b,n,p,q),x))),
SetDelayed(Int(Times(Power(Log(Times(Power(Times(Power($p(x),$p(n,true)),$p(b,true)),$p(p)),$p(a,true))),$p(q,true)),Power($p(x),$p(m,true))),$p(x,SymbolHead)),
    Condition(Subst(Int(Times(Power(x,m),Power(Log(Power(x,Times(n,p))),q)),x),Power(x,Times(n,p)),Times(a,Power(Times(b,Power(x,n)),p))),And(And(FreeQ(List(a,b,m,n,p,q),x),NonzeroQ(Plus(m,C1))),Not(SameQ(Power(x,Times(n,p)),Times(a,Power(Times(b,Power(x,n)),p))))))),
SetDelayed(Int(Power(Log(Times(Power(Times(Power(Times(Power($p(x),$p(n,true)),$p(c,true)),$p(p)),$p(b,true)),$p(q)),$p(a,true))),$p(r,true)),$p(x,SymbolHead)),
    Condition(Subst(Int(Power(Log(Power(x,Times(n,p,q))),r),x),Power(x,Times(n,p,q)),Times(a,Power(Times(b,Power(Times(c,Power(x,n)),p)),q))),FreeQ(List(a,b,c,n,p,q,r),x))),
SetDelayed(Int(Times(Power(Log(Times(Power(Times(Power(Times(Power($p(x),$p(n,true)),$p(c,true)),$p(p)),$p(b,true)),$p(q)),$p(a,true))),$p(r,true)),Power($p(x),$p(m,true))),$p(x,SymbolHead)),
    Condition(Subst(Int(Times(Power(x,m),Power(Log(Power(x,Times(n,p,q))),r)),x),Power(x,Times(n,p,q)),Times(a,Power(Times(b,Power(Times(c,Power(x,n)),p)),q))),And(And(FreeQ(List(a,b,c,m,n,p,q,r),x),NonzeroQ(Plus(m,C1))),Not(SameQ(Power(x,Times(n,p,q)),Times(a,Power(Times(b,Power(Times(c,Power(x,n)),p)),q))))))),
SetDelayed(Int(Log(Times(Power(Log(Times(Power($p(x),$p(n,true)),$p(b,true))),$p(p,true)),$p(a,true))),$p(x,SymbolHead)),
    Condition(Plus(Times(x,Log(Times(a,Power(Log(Times(b,Power(x,n))),p)))),Times(CN1,n,p,Int(Power(Log(Times(b,Power(x,n))),CN1),x))),FreeQ(List(a,b,n,p),x))),
SetDelayed(Int(Times(Log(Times(Power(Log(Times(Power($p(x),$p(n,true)),$p(b,true))),$p(p,true)),$p(a,true))),Power($p(x),CN1)),$p(x,SymbolHead)),
    Condition(Times(Log(Times(b,Power(x,n))),Plus(Times(CN1,p),Log(Times(a,Power(Log(Times(b,Power(x,n))),p)))),Power(n,CN1)),FreeQ(List(a,b,n,p),x))),
SetDelayed(Int(Times(Log(Times(Power(Log(Times(Power($p(x),$p(n,true)),$p(b,true))),$p(p,true)),$p(a,true))),Power($p(x),$p(m,true))),$p(x,SymbolHead)),
    Condition(Plus(Times(Power(x,Plus(m,C1)),Log(Times(a,Power(Log(Times(b,Power(x,n))),p))),Power(Plus(m,C1),CN1)),Times(CN1,n,p,Power(Plus(m,C1),CN1),Int(Times(Power(x,m),Power(Log(Times(b,Power(x,n))),CN1)),x))),And(FreeQ(List(a,b,m,n,p),x),NonzeroQ(Plus(m,C1))))),
SetDelayed(Int(Times(Plus(Times(Log(Plus(Times($p(d,true),$p(x)),$p(c,true))),$p("B",true)),$p("A",true)),Power(Plus(Times(Log(Plus(Times($p(d,true),$p(x)),$p(c,true))),$p(b,true)),$p(a)),CN1D2)),$p(x,SymbolHead)),
    Condition(Plus(Times(Plus(Times(b,$s("A")),Times(CN1,a,$s("B"))),Power(b,CN1),Int(Power(Sqrt(Plus(a,Times(b,Log(Plus(c,Times(d,x)))))),CN1),x)),Times($s("B"),Power(b,CN1),Int(Sqrt(Plus(a,Times(b,Log(Plus(c,Times(d,x)))))),x))),And(FreeQ(List(a,b,c,d,$s("A"),$s("B")),x),NonzeroQ(Plus(Times(b,$s("A")),Times(CN1,a,$s("B"))))))),
SetDelayed(Int(Power($p(f),Times(Log($p(u)),$p(a,true))),$p(x,SymbolHead)),
    Condition(Int(Power(u,Times(a,Log(f))),x),FreeQ(List(a,f),x))),
SetDelayed(Int($p(u),$p(x,SymbolHead)),
    Condition(Module(List(Set($s("lst"),FunctionOfLog(Cancel(Times(x,u)),x))),Condition(Times(Power(Part($s("lst"),C3),CN1),Subst(Int(Part($s("lst"),C1),x),x,Log(Part($s("lst"),C2)))),Not(FalseQ($s("lst"))))),NonsumQ(u))),
SetDelayed(Int(Times(Log(Gamma($p(v))),$p(u,true)),$p(x,SymbolHead)),
    Plus(Times(Plus(Log(Gamma(v)),Times(CN1,LogGamma(v))),Int(u,x)),Int(Times(u,LogGamma(v)),x))),
SetDelayed(Int(Times(Power(Plus(Times($p(a,true),$p(w)),Times(Power(Log($p(v)),$p(n,true)),$p(b,true),$p(w))),$p(p,true)),$p(u,true)),$p(x,SymbolHead)),
    Condition(Int(Times(u,Power(w,p),Power(Plus(a,Times(b,Power(Log(v),n))),p)),x),And(FreeQ(List(a,b,n),x),IntegerQ(p))))
  );
}
