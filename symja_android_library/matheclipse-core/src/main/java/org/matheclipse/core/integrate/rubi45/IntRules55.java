package org.matheclipse.core.integrate.rubi45;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi45.UtilityFunctionCtors.*;
import static org.matheclipse.core.integrate.rubi45.UtilityFunctions.*;

import org.matheclipse.core.interfaces.IAST;

/** 
 * IndefiniteIntegrationRules from the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi -
 * rule-based integrator</a>.
 *  
 */
public class IntRules55 { 
  public static IAST RULES = List( 
ISetDelayed(Int($($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Times(CN1,Log(RemoveContent(Cos(Plus(a,Times(b,x))),x)),Power(b,-1)),FreeQ(List(a,b),x))),
ISetDelayed(Int($($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Times(Log(RemoveContent(Sin(Plus(a,Times(b,x))),x)),Power(b,-1)),FreeQ(List(a,b),x))),
ISetDelayed(Int(Power(Times(c_DEFAULT,$($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_),x_Symbol),
    Condition(Plus(Times(c,Power(Times(c,Tan(Plus(a,Times(b,x)))),Plus(n,Negate(C1))),Power(Times(b,Plus(n,Negate(C1))),-1)),Times(CN1,Sqr(c),Int(Power(Times(c,Tan(Plus(a,Times(b,x)))),Plus(n,Negate(C2))),x))),And(And(FreeQ(List(a,b,c),x),RationalQ(n)),Greater(n,C1)))),
ISetDelayed(Int(Power(Times(c_DEFAULT,$($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_),x_Symbol),
    Condition(Plus(Times(CN1,c,Power(Times(c,Cot(Plus(a,Times(b,x)))),Plus(n,Negate(C1))),Power(Times(b,Plus(n,Negate(C1))),-1)),Times(CN1,Sqr(c),Int(Power(Times(c,Cot(Plus(a,Times(b,x)))),Plus(n,Negate(C2))),x))),And(And(FreeQ(List(a,b,c),x),RationalQ(n)),Greater(n,C1)))),
ISetDelayed(Int(Power(Times(c_DEFAULT,$($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_),x_Symbol),
    Condition(Plus(Times(Power(Times(c,Tan(Plus(a,Times(b,x)))),Plus(n,C1)),Power(Times(b,c,Plus(n,C1)),-1)),Times(CN1,Power(c,-2),Int(Power(Times(c,Tan(Plus(a,Times(b,x)))),Plus(n,C2)),x))),And(And(FreeQ(List(a,b,c),x),RationalQ(n)),Less(n,CN1)))),
ISetDelayed(Int(Power(Times(c_DEFAULT,$($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_),x_Symbol),
    Condition(Plus(Times(CN1,Power(Times(c,Cot(Plus(a,Times(b,x)))),Plus(n,C1)),Power(Times(b,c,Plus(n,C1)),-1)),Times(CN1,Power(c,-2),Int(Power(Times(c,Cot(Plus(a,Times(b,x)))),Plus(n,C2)),x))),And(And(FreeQ(List(a,b,c),x),RationalQ(n)),Less(n,CN1)))),
ISetDelayed(Int(Sqrt(Times(c_,$($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),x_Symbol),
    Condition(Plus(Times(c,Power(Times(C2,CI),-1),Int(Times(Plus(C1,Times(CI,Tan(Plus(a,Times(b,x))))),Power(Times(c,Tan(Plus(a,Times(b,x)))),CN1D2)),x)),Times(CN1,c,Power(Times(C2,CI),-1),Int(Times(Plus(C1,Times(CN1,CI,Tan(Plus(a,Times(b,x))))),Power(Times(c,Tan(Plus(a,Times(b,x)))),CN1D2)),x))),And(FreeQ(List(a,b,c),x),MatchQ(c,Times(d_DEFAULT,Complex(m_,n_)))))),
ISetDelayed(Int(Sqrt(Times(c_DEFAULT,$($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),x_Symbol),
    Condition(Plus(Times(C1D2,c,Int(Times(Plus(C1,Tan(Plus(a,Times(b,x)))),Power(Times(c,Tan(Plus(a,Times(b,x)))),CN1D2)),x)),Times(CN1,C1D2,c,Int(Times(Plus(C1,Negate(Tan(Plus(a,Times(b,x))))),Power(Times(c,Tan(Plus(a,Times(b,x)))),CN1D2)),x))),FreeQ(List(a,b,c),x))),
ISetDelayed(Int(Sqrt(Times(c_,$($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),x_Symbol),
    Condition(Plus(Times(c,Power(Times(C2,CI),-1),Int(Times(Plus(C1,Times(CI,Cot(Plus(a,Times(b,x))))),Power(Times(c,Cot(Plus(a,Times(b,x)))),CN1D2)),x)),Times(CN1,c,Power(Times(C2,CI),-1),Int(Times(Plus(C1,Times(CN1,CI,Cot(Plus(a,Times(b,x))))),Power(Times(c,Cot(Plus(a,Times(b,x)))),CN1D2)),x))),And(FreeQ(List(a,b,c),x),MatchQ(c,Times(d_DEFAULT,Complex(m_,n_)))))),
ISetDelayed(Int(Sqrt(Times(c_DEFAULT,$($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))))),x_Symbol),
    Condition(Plus(Times(C1D2,c,Int(Times(Plus(C1,Cot(Plus(a,Times(b,x)))),Power(Times(c,Cot(Plus(a,Times(b,x)))),CN1D2)),x)),Times(CN1,C1D2,c,Int(Times(Plus(C1,Negate(Cot(Plus(a,Times(b,x))))),Power(Times(c,Cot(Plus(a,Times(b,x)))),CN1D2)),x))),FreeQ(List(a,b,c),x))),
ISetDelayed(Int(Power(Times(c_DEFAULT,$($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),CN1D2),x_Symbol),
    Condition(Plus(Times(C1D2,Int(Times(Plus(C1,Times(CI,Tan(Plus(a,Times(b,x))))),Power(Times(c,Tan(Plus(a,Times(b,x)))),CN1D2)),x)),Times(C1D2,Int(Times(Plus(C1,Times(CN1,CI,Tan(Plus(a,Times(b,x))))),Power(Times(c,Tan(Plus(a,Times(b,x)))),CN1D2)),x))),And(FreeQ(List(a,b,c),x),MatchQ(c,Times(d_DEFAULT,Complex(m_,n_)))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,$($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),CN1D2),x_Symbol),
    Condition(Plus(Times(C1D2,Int(Times(Plus(C1,Tan(Plus(a,Times(b,x)))),Power(Times(c,Tan(Plus(a,Times(b,x)))),CN1D2)),x)),Times(C1D2,Int(Times(Plus(C1,Negate(Tan(Plus(a,Times(b,x))))),Power(Times(c,Tan(Plus(a,Times(b,x)))),CN1D2)),x))),FreeQ(List(a,b,c),x))),
ISetDelayed(Int(Power(Times(c_DEFAULT,$($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),CN1D2),x_Symbol),
    Condition(Plus(Times(C1D2,Int(Times(Plus(C1,Times(CI,Cot(Plus(a,Times(b,x))))),Power(Times(c,Cot(Plus(a,Times(b,x)))),CN1D2)),x)),Times(C1D2,Int(Times(Plus(C1,Times(CN1,CI,Cot(Plus(a,Times(b,x))))),Power(Times(c,Cot(Plus(a,Times(b,x)))),CN1D2)),x))),And(FreeQ(List(a,b,c),x),MatchQ(c,Times(d_DEFAULT,Complex(m_,n_)))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,$($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),CN1D2),x_Symbol),
    Condition(Plus(Times(C1D2,Int(Times(Plus(C1,Cot(Plus(a,Times(b,x)))),Power(Times(c,Cot(Plus(a,Times(b,x)))),CN1D2)),x)),Times(C1D2,Int(Times(Plus(C1,Negate(Cot(Plus(a,Times(b,x))))),Power(Times(c,Cot(Plus(a,Times(b,x)))),CN1D2)),x))),FreeQ(List(a,b,c),x))),
ISetDelayed(Int(Power(Times(c_DEFAULT,$($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_),x_Symbol),
    Condition(Module(List(Set(k,Denominator(n))),Times(k,c,Power(b,-1),Subst(Int(Times(Power(x,Plus(Times(k,Plus(n,C1)),Negate(C1))),Power(Plus(Sqr(c),Power(x,Times(C2,k))),-1)),x),x,Power(Times(c,Tan(Plus(a,Times(b,x)))),Power(k,-1))))),And(And(FreeQ(List(a,b,c),x),RationalQ(n)),Less(Less(CN1,n),C1)))),
ISetDelayed(Int(Power(Times(c_DEFAULT,$($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_),x_Symbol),
    Condition(Module(List(Set(k,Denominator(n))),Times(CN1,k,c,Power(b,-1),Subst(Int(Times(Power(x,Plus(Times(k,Plus(n,C1)),Negate(C1))),Power(Plus(Sqr(c),Power(x,Times(C2,k))),-1)),x),x,Power(Times(c,Cot(Plus(a,Times(b,x)))),Power(k,-1))))),And(And(FreeQ(List(a,b,c),x),RationalQ(n)),Less(Less(CN1,n),C1)))),
ISetDelayed(Int(Power(Times(c_DEFAULT,$($s("§tan"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_),x_Symbol),
    Condition(Times(Power(Times(c,Tan(Plus(a,Times(b,x)))),Plus(n,C1)),Power(Times(b,c,Plus(n,C1)),-1),Hypergeometric2F1(C1,Times(C1D2,Plus(n,C1)),Times(C1D2,Plus(n,C3)),Negate(Sqr(Tan(Plus(a,Times(b,x))))))),And(FreeQ(List(a,b,c,n),x),Not(IntegerQ(n))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,$($s("§cot"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_),x_Symbol),
    Condition(Times(CN1,Power(Times(c,Cot(Plus(a,Times(b,x)))),Plus(n,C1)),Power(Times(b,c,Plus(n,C1)),-1),Hypergeometric2F1(C1,Times(C1D2,Plus(n,C1)),Times(C1D2,Plus(n,C3)),Negate(Sqr(Cot(Plus(a,Times(b,x))))))),And(FreeQ(List(a,b,c,n),x),Not(IntegerQ(n)))))
  );
}
