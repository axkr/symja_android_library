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
public class IntRules82 { 
  public static IAST RULES = List( 
ISetDelayed(Int(Power(Times(c_DEFAULT,Sqr($($s("§sin"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Plus(Times(CN1,Cot(Plus(a,Times(b,x))),Power(Times(c,Sqr(Sin(Plus(a,Times(b,x))))),n),Power(Times(C2,b,n),CN1)),Times(c,Plus(Times(C2,n),Times(CN1,C1)),Power(Times(C2,n),CN1),Int(Power(Times(c,Sqr(Sin(Plus(a,Times(b,x))))),Plus(n,Times(CN1,C1))),x))),And(And(FreeQ(List(a,b,c),x),RationalQ(n)),Greater(n,C1)))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Sqr($($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Plus(Times(Tan(Plus(a,Times(b,x))),Power(Times(c,Sqr(Cos(Plus(a,Times(b,x))))),n),Power(Times(C2,b,n),CN1)),Times(c,Plus(Times(C2,n),Times(CN1,C1)),Power(Times(C2,n),CN1),Int(Power(Times(c,Sqr(Cos(Plus(a,Times(b,x))))),Plus(n,Times(CN1,C1))),x))),And(And(FreeQ(List(a,b,c),x),RationalQ(n)),Greater(n,C1)))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Sqr($($s("§sin"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Plus(Times(Cot(Plus(a,Times(b,x))),Power(Times(c,Sqr(Sin(Plus(a,Times(b,x))))),Plus(n,C1)),Power(Times(b,c,Plus(Times(C2,n),C1)),CN1)),Times(C2,Plus(n,C1),Power(Times(c,Plus(Times(C2,n),C1)),CN1),Int(Power(Times(c,Sqr(Sin(Plus(a,Times(b,x))))),Plus(n,C1)),x))),And(And(FreeQ(List(a,b,c),x),RationalQ(n)),Less(n,CN1)))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Sqr($($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Plus(Times(CN1,Tan(Plus(a,Times(b,x))),Power(Times(c,Sqr(Cos(Plus(a,Times(b,x))))),Plus(n,C1)),Power(Times(b,c,Plus(Times(C2,n),C1)),CN1)),Times(C2,Plus(n,C1),Power(Times(c,Plus(Times(C2,n),C1)),CN1),Int(Power(Times(c,Sqr(Cos(Plus(a,Times(b,x))))),Plus(n,C1)),x))),And(And(FreeQ(List(a,b,c),x),RationalQ(n)),Less(n,CN1)))),
ISetDelayed(Int(Power(Sqr($($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_),x_Symbol),
    Condition(Times(Power(b,CN1),Subst(Int(Power(Plus(C1,Sqr(x)),Plus(n,Times(CN1,C1))),x),x,Tan(Plus(a,Times(b,x))))),FreeQ(List(a,b,n),x))),
ISetDelayed(Int(Power(Sqr($($s("§csc"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_),x_Symbol),
    Condition(Times(CN1,Power(b,CN1),Subst(Int(Power(Plus(C1,Sqr(x)),Plus(n,Times(CN1,C1))),x),x,Cot(Plus(a,Times(b,x))))),FreeQ(List(a,b,n),x))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Power(Times($p(d,true),$($p("F"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),m_)),n_),x_Symbol),
    Condition(Module(List(Set(u,ActivateTrig(F(Plus(a,Times(b,x)))))),Times(Power(c,Plus(n,Times(CN1,C1D2))),Power(Times(d,FreeFactors(u,x)),Times(m,Plus(n,Times(CN1,C1D2)))),Sqrt(Times(c,Power(Times(d,u),m))),Power(Power(NonfreeFactors(u,x),Times(C1D2,m)),CN1),Int(Power(NonfreeFactors(u,x),Times(m,n)),x))),And(And(FreeQ(List(a,b,c,d,m),x),InertTrigQ($s("F"))),PositiveIntegerQ(Plus(n,C1D2))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Power(Times($p(d,true),$($p("F"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),m_)),n_),x_Symbol),
    Condition(Module(List(Set(u,ActivateTrig(F(Plus(a,Times(b,x)))))),Times(Power(c,Plus(n,C1D2)),Power(Times(d,FreeFactors(u,x)),Times(m,Plus(n,C1D2))),Power(NonfreeFactors(u,x),Times(C1D2,m)),Power(Times(c,Power(Times(d,u),m)),CN1D2),Int(Power(NonfreeFactors(u,x),Times(m,n)),x))),And(And(FreeQ(List(a,b,c,d,m),x),InertTrigQ($s("F"))),NegativeIntegerQ(Plus(n,Times(CN1,C1D2)))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Power(Times($p(d,true),$($p("F"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),m_)),n_),x_Symbol),
    Condition(Module(List(Set(u,ActivateTrig(F(Plus(a,Times(b,x)))))),Times(Power(Times(c,Power(Times(d,u),m)),n),Power(Power(NonfreeFactors(u,x),Times(m,n)),CN1),Int(Power(NonfreeFactors(u,x),Times(m,n)),x))),And(And(FreeQ(List(a,b,c,d,m,n),x),InertTrigQ($s("F"))),Not(IntegerQ(Plus(n,C1D2))))))
  );
}
