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
public class IntegrationRulesForMiscellaneousExpressions13 { 
  public static IAST RULES = List( 
SetDelayed(Int(Power($p(u),$p(m,true)),$p(x,SymbolHead)),
    Condition(Module(List(Set(c,Simplify(D(u,x)))),Times(Power(c,CN1),Subst(Int(Power(x,m),x),x,u))),And(FreeQ(m,x),PiecewiseLinearQ(u,x)))),
SetDelayed(Int(Times(Power($p(u),CN1),$p(v)),$p(x,SymbolHead)),
    Condition(Module(List(Set(a,Simplify(D(u,x))),Set(b,Simplify(D(v,x)))),Condition(Plus(Times(b,x,Power(a,CN1)),Times(CN1,Plus(Times(b,u),Times(CN1,a,v)),Power(a,CN1),Int(Power(u,CN1),x))),NonzeroQ(Plus(Times(b,u),Times(CN1,a,v))))),PiecewiseLinearQ(u,v,x))),
SetDelayed(Int(Times(Power($p(u),CN1),Power($p(v),$p(n))),$p(x,SymbolHead)),
    Condition(Module(List(Set(a,Simplify(D(u,x))),Set(b,Simplify(D(v,x)))),Condition(Plus(Times(Power(v,n),Power(Times(a,n),CN1)),Times(CN1,Plus(Times(b,u),Times(CN1,a,v)),Power(a,CN1),Int(Times(Power(v,Plus(n,Times(CN1,C1))),Power(u,CN1)),x))),NonzeroQ(Plus(Times(b,u),Times(CN1,a,v))))),And(And(And(PiecewiseLinearQ(u,v,x),RationalQ(n)),Greater(n,C0)),Unequal(n,C1)))),
SetDelayed(Int(Times(Power($p(u),CN1),Power($p(v),CN1)),$p(x,SymbolHead)),
    Condition(Module(List(Set(a,Simplify(D(u,x))),Set(b,Simplify(D(v,x)))),Condition(Plus(Times(b,Power(Plus(Times(b,u),Times(CN1,a,v)),CN1),Int(Power(v,CN1),x)),Times(CN1,a,Power(Plus(Times(b,u),Times(CN1,a,v)),CN1),Int(Power(u,CN1),x))),NonzeroQ(Plus(Times(b,u),Times(CN1,a,v))))),PiecewiseLinearQ(u,v,x))),
SetDelayed(Int(Times(Power($p(u),CN1),Power($p(v),CN1D2)),$p(x,SymbolHead)),
    Condition(Module(List(Set(a,Simplify(D(u,x))),Set(b,Simplify(D(v,x)))),Condition(Times(C2,ArcTan(Times(Sqrt(v),Power(Rt(Times(Plus(Times(b,u),Times(CN1,a,v)),Power(a,CN1)),C2),CN1))),Power(Times(a,Rt(Times(Plus(Times(b,u),Times(CN1,a,v)),Power(a,CN1)),C2)),CN1)),And(NonzeroQ(Plus(Times(b,u),Times(CN1,a,v))),PosQ(Times(Plus(Times(b,u),Times(CN1,a,v)),Power(a,CN1)))))),PiecewiseLinearQ(u,v,x))),
SetDelayed(Int(Times(Power($p(u),CN1),Power($p(v),CN1D2)),$p(x,SymbolHead)),
    Condition(Module(List(Set(a,Simplify(D(u,x))),Set(b,Simplify(D(v,x)))),Condition(Times(CN2,ArcTanh(Times(Sqrt(v),Power(Rt(Times(CN1,Plus(Times(b,u),Times(CN1,a,v)),Power(a,CN1)),C2),CN1))),Power(Times(a,Rt(Times(CN1,Plus(Times(b,u),Times(CN1,a,v)),Power(a,CN1)),C2)),CN1)),And(NonzeroQ(Plus(Times(b,u),Times(CN1,a,v))),NegQ(Times(Plus(Times(b,u),Times(CN1,a,v)),Power(a,CN1)))))),PiecewiseLinearQ(u,v,x))),
SetDelayed(Int(Times(Power($p(u),CN1),Power($p(v),$p(n))),$p(x,SymbolHead)),
    Condition(Module(List(Set(a,Simplify(D(u,x))),Set(b,Simplify(D(v,x)))),Condition(Plus(Times(Power(v,Plus(n,C1)),Power(Times(Plus(n,C1),Plus(Times(b,u),Times(CN1,a,v))),CN1)),Times(CN1,a,Plus(n,C1),Power(Times(Plus(n,C1),Plus(Times(b,u),Times(CN1,a,v))),CN1),Int(Times(Power(v,Plus(n,C1)),Power(u,CN1)),x))),NonzeroQ(Plus(Times(b,u),Times(CN1,a,v))))),And(And(PiecewiseLinearQ(u,v,x),RationalQ(n)),Less(n,CN1)))),
SetDelayed(Int(Times(Power($p(u),CN1),Power($p(v),$p(n))),$p(x,SymbolHead)),
    Condition(Module(List(Set(a,Simplify(D(u,x))),Set(b,Simplify(D(v,x)))),Condition(Times(Power(v,Plus(n,C1)),Power(Times(Plus(n,C1),Plus(Times(b,u),Times(CN1,a,v))),CN1),Hypergeometric2F1(C1,Plus(n,C1),Plus(n,C2),Times(CN1,a,v,Power(Plus(Times(b,u),Times(CN1,a,v)),CN1)))),NonzeroQ(Plus(Times(b,u),Times(CN1,a,v))))),And(PiecewiseLinearQ(u,v,x),Not(IntegerQ(n))))),
SetDelayed(Int(Times(Power($p(u),CN1D2),Power($p(v),CN1D2)),$p(x,SymbolHead)),
    Condition(Module(List(Set(a,Simplify(D(u,x))),Set(b,Simplify(D(v,x)))),Condition(Times(C2,Power(Rt(Times(a,b),C2),CN1),ArcTanh(Times(Rt(Times(a,b),C2),Sqrt(u),Power(Times(a,Sqrt(v)),CN1)))),And(NonzeroQ(Plus(Times(b,u),Times(CN1,a,v))),PosQ(Times(a,b))))),PiecewiseLinearQ(u,v,x))),
SetDelayed(Int(Times(Power($p(u),CN1D2),Power($p(v),CN1D2)),$p(x,SymbolHead)),
    Condition(Module(List(Set(a,Simplify(D(u,x))),Set(b,Simplify(D(v,x)))),Condition(Times(C2,Power(Rt(Times(CN1,a,b),C2),CN1),ArcTan(Times(Rt(Times(CN1,a,b),C2),Sqrt(u),Power(Times(a,Sqrt(v)),CN1)))),And(NonzeroQ(Plus(Times(b,u),Times(CN1,a,v))),NegQ(Times(a,b))))),PiecewiseLinearQ(u,v,x))),
SetDelayed(Int(Times(Power($p(u),$p(m)),Power($p(v),$p(n))),$p(x,SymbolHead)),
    Condition(Module(List(Set(a,Simplify(D(u,x))),Set(b,Simplify(D(v,x)))),Condition(Times(CN1,Power(u,Plus(m,C1)),Power(v,Plus(n,C1)),Power(Times(Plus(m,C1),Plus(Times(b,u),Times(CN1,a,v))),CN1)),NonzeroQ(Plus(Times(b,u),Times(CN1,a,v))))),And(And(And(FreeQ(List(m,n),x),PiecewiseLinearQ(u,v,x)),ZeroQ(Plus(m,n,C2))),NonzeroQ(Plus(m,C1))))),
SetDelayed(Int(Times(Power($p(u),$p(m)),Power($p(v),$p(n,true))),$p(x,SymbolHead)),
    Condition(Module(List(Set(a,Simplify(D(u,x))),Set(b,Simplify(D(v,x)))),Condition(Plus(Times(Power(u,Plus(m,C1)),Power(v,n),Power(Times(a,Plus(m,C1)),CN1)),Times(CN1,b,n,Power(Times(a,Plus(m,C1)),CN1),Int(Times(Power(u,Plus(m,C1)),Power(v,Plus(n,Times(CN1,C1)))),x))),NonzeroQ(Plus(Times(b,u),Times(CN1,a,v))))),And(And(And(FreeQ(List(m,n),x),PiecewiseLinearQ(u,v,x)),NonzeroQ(Plus(m,C1))),Or(Or(Or(And(And(And(RationalQ(m,n),Less(m,CN1)),Greater(n,C0)),Not(And(And(IntegerQ(Plus(m,n)),Less(Plus(m,n,C2),C0)),Or(FractionQ(m),GreaterEqual(Plus(Times(C2,n),m,C1),C0))))),And(PositiveIntegerQ(n,m),LessEqual(n,m))),And(PositiveIntegerQ(n),Not(IntegerQ(m)))),And(NegativeIntegerQ(m),Not(IntegerQ(n))))))),
SetDelayed(Int(Times(Power($p(u),$p(m)),Power($p(v),$p(n,true))),$p(x,SymbolHead)),
    Condition(Module(List(Set(a,Simplify(D(u,x))),Set(b,Simplify(D(v,x)))),Condition(Plus(Times(Power(u,Plus(m,C1)),Power(v,n),Power(Times(a,Plus(m,n,C1)),CN1)),Times(CN1,n,Plus(Times(b,u),Times(CN1,a,v)),Power(Times(a,Plus(m,n,C1)),CN1),Int(Times(Power(u,m),Power(v,Plus(n,Times(CN1,C1)))),x))),NonzeroQ(Plus(Times(b,u),Times(CN1,a,v))))),And(And(And(And(And(And(PiecewiseLinearQ(u,v,x),NonzeroQ(Plus(m,n,C2))),RationalQ(n)),Greater(n,C0)),NonzeroQ(Plus(m,n,C1))),Not(And(PositiveIntegerQ(m),Or(Not(IntegerQ(n)),Less(Less(C0,m),n))))),Not(And(IntegerQ(Plus(m,n)),Less(Plus(m,n,C2),C0)))))),
SetDelayed(Int(Times(Power($p(u),$p(m)),Power($p(v),$p(n))),$p(x,SymbolHead)),
    Condition(Module(List(Set(a,Simplify(D(u,x))),Set(b,Simplify(D(v,x)))),Condition(Plus(Times(Power(u,Plus(m,C1)),Power(v,n),Power(Times(a,Plus(m,n,C1)),CN1)),Times(CN1,n,Plus(Times(b,u),Times(CN1,a,v)),Power(Times(a,Plus(m,n,C1)),CN1),Int(Times(Power(u,m),Power(v,Simplify(Plus(n,Times(CN1,C1))))),x))),NonzeroQ(Plus(Times(b,u),Times(CN1,a,v))))),And(And(And(PiecewiseLinearQ(u,v,x),NonzeroQ(Plus(m,n,C1))),Not(RationalQ(n))),SumSimplerQ(n,CN1)))),
SetDelayed(Int(Times(Power($p(u),$p(m)),Power($p(v),$p(n))),$p(x,SymbolHead)),
    Condition(Module(List(Set(a,Simplify(D(u,x))),Set(b,Simplify(D(v,x)))),Condition(Plus(Times(CN1,Power(u,Plus(m,C1)),Power(v,Plus(n,C1)),Power(Times(Plus(m,C1),Plus(Times(b,u),Times(CN1,a,v))),CN1)),Times(b,Plus(m,n,C2),Power(Times(Plus(m,C1),Plus(Times(b,u),Times(CN1,a,v))),CN1),Int(Times(Power(u,Plus(m,C1)),Power(v,n)),x))),NonzeroQ(Plus(Times(b,u),Times(CN1,a,v))))),And(And(And(PiecewiseLinearQ(u,v,x),NonzeroQ(Plus(m,n,C2))),RationalQ(m)),Less(m,CN1)))),
SetDelayed(Int(Times(Power($p(u),$p(m)),Power($p(v),$p(n))),$p(x,SymbolHead)),
    Condition(Module(List(Set(a,Simplify(D(u,x))),Set(b,Simplify(D(v,x)))),Condition(Plus(Times(CN1,Power(u,Plus(m,C1)),Power(v,Plus(n,C1)),Power(Times(Plus(m,C1),Plus(Times(b,u),Times(CN1,a,v))),CN1)),Times(b,Plus(m,n,C2),Power(Times(Plus(m,C1),Plus(Times(b,u),Times(CN1,a,v))),CN1),Int(Times(Power(u,Simplify(Plus(m,C1))),Power(v,n)),x))),NonzeroQ(Plus(Times(b,u),Times(CN1,a,v))))),And(And(PiecewiseLinearQ(u,v,x),Not(RationalQ(m))),SumSimplerQ(m,C1)))),
SetDelayed(Int(Times(Power($p(u),$p(m)),Power($p(v),$p(n))),$p(x,SymbolHead)),
    Condition(Module(List(Set(a,Simplify(D(u,x))),Set(b,Simplify(D(v,x)))),Condition(Times(Power(u,m),Power(v,Plus(n,C1)),Power(Times(b,Plus(n,C1),Power(Times(b,u,Power(Plus(Times(b,u),Times(CN1,a,v)),CN1)),m)),CN1),Hypergeometric2F1(Times(CN1,m),Plus(n,C1),Plus(n,C2),Times(CN1,a,v,Power(Plus(Times(b,u),Times(CN1,a,v)),CN1)))),NonzeroQ(Plus(Times(b,u),Times(CN1,a,v))))),And(And(PiecewiseLinearQ(u,v,x),Not(IntegerQ(m))),Not(IntegerQ(n))))),
SetDelayed(Int(Times(Log(Plus(Times($p(b,true),$p(x)),$p(a,true))),Power($p(u),$p(n,true))),$p(x,SymbolHead)),
    Condition(Module(List(Set(c,Simplify(D(u,x)))),Plus(Times(Power(u,n),Plus(a,Times(b,x)),Log(Plus(a,Times(b,x))),Power(b,CN1)),Times(CN1,Int(Power(u,n),x)),Times(CN1,c,n,Power(b,CN1),Int(Times(Power(u,Plus(n,Times(CN1,C1))),Plus(a,Times(b,x)),Log(Plus(a,Times(b,x)))),x)))),And(And(And(And(FreeQ(List(a,b),x),PiecewiseLinearQ(u,x)),Not(LinearQ(u,x))),RationalQ(n)),Greater(n,C0)))),
SetDelayed(Int(Times(Log(Plus(Times($p(b,true),$p(x)),$p(a,true))),Power(Plus(Times($p(b,true),$p(x)),$p(a,true)),CN1),Power($p(u),$p(n,true))),$p(x,SymbolHead)),
    Condition(Module(List(Set(c,Simplify(D(u,x)))),Plus(Times(Power(u,n),Power(Log(Plus(a,Times(b,x))),C2),Power(Times(C2,b),CN1)),Times(CN1,c,n,Power(Times(C2,b),CN1),Int(Times(Power(u,Plus(n,Times(CN1,C1))),Power(Log(Plus(a,Times(b,x))),C2)),x)))),And(And(And(FreeQ(List(a,b),x),PiecewiseLinearQ(u,x)),RationalQ(n)),Greater(n,C0)))),
SetDelayed(Int(Times(Log(Plus(Times($p(b,true),$p(x)),$p(a,true))),Power(Plus(Times($p(b,true),$p(x)),$p(a,true)),$p(m,true)),Power($p(u),$p(n,true))),$p(x,SymbolHead)),
    Condition(Module(List(Set(c,Simplify(D(u,x)))),Plus(Times(Power(u,n),Power(Plus(a,Times(b,x)),Plus(m,C1)),Log(Plus(a,Times(b,x))),Power(Times(b,Plus(m,C1)),CN1)),Times(CN1,Power(Plus(m,C1),CN1),Int(Times(Power(u,n),Power(Plus(a,Times(b,x)),m)),x)),Times(CN1,c,n,Power(Times(b,Plus(m,C1)),CN1),Int(Times(Power(u,Plus(n,Times(CN1,C1))),Power(Plus(a,Times(b,x)),Plus(m,C1)),Log(Plus(a,Times(b,x)))),x)))),And(And(And(And(And(FreeQ(List(a,b,m),x),PiecewiseLinearQ(u,x)),Not(LinearQ(u,x))),RationalQ(n)),Greater(n,C0)),NonzeroQ(Plus(m,C1)))))
  );
}
