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
public class IntRules67 { 
  public static IAST RULES = List( 
ISetDelayed(Int($($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Times(ArcTanh(Sin(Plus(a,Times(b,x)))),Power(b,-1)),FreeQ(List(a,b),x))),
ISetDelayed(Int($($s("§csc"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Times(CN1,ArcTanh(Cos(Plus(a,Times(b,x)))),Power(b,-1)),FreeQ(List(a,b),x))),
ISetDelayed(Int(Sqr($($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Times(Tan(Plus(a,Times(b,x))),Power(b,-1)),FreeQ(List(a,b),x))),
ISetDelayed(Int(Sqr($($s("§csc"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Times(CN1,Cot(Plus(a,Times(b,x))),Power(b,-1)),FreeQ(List(a,b),x))),
ISetDelayed(Int(Power($($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_),x_Symbol),
    Condition(Times(Power(b,-1),Subst(Int(ExpandIntegrand(Power(Plus(C1,Sqr(x)),Plus(Times(C1D2,n),Negate(C1))),x),x),x,Tan(Plus(a,Times(b,x))))),And(And(And(FreeQ(List(a,b),x),RationalQ(n)),Greater(n,C1)),EvenQ(n)))),
ISetDelayed(Int(Power($($s("§csc"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_),x_Symbol),
    Condition(Times(CN1,Power(b,-1),Subst(Int(ExpandIntegrand(Power(Plus(C1,Sqr(x)),Plus(Times(C1D2,n),Negate(C1))),x),x),x,Cot(Plus(a,Times(b,x))))),And(And(And(FreeQ(List(a,b),x),RationalQ(n)),Greater(n,C1)),EvenQ(n)))),
ISetDelayed(Int(Power(Times(c_DEFAULT,$($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_),x_Symbol),
    Condition(Plus(Times(c,Sin(Plus(a,Times(b,x))),Power(Times(c,Sec(Plus(a,Times(b,x)))),Plus(n,Negate(C1))),Power(Times(b,Plus(n,Negate(C1))),-1)),Times(Sqr(c),Plus(n,Negate(C2)),Power(Plus(n,Negate(C1)),-1),Int(Power(Times(c,Sec(Plus(a,Times(b,x)))),Plus(n,Negate(C2))),x))),And(And(And(FreeQ(List(a,b,c),x),RationalQ(n)),Greater(n,C1)),Not(EvenQ(n))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,$($s("§csc"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_),x_Symbol),
    Condition(Plus(Times(CN1,c,Cos(Plus(a,Times(b,x))),Power(Times(c,Csc(Plus(a,Times(b,x)))),Plus(n,Negate(C1))),Power(Times(b,Plus(n,Negate(C1))),-1)),Times(Sqr(c),Plus(n,Negate(C2)),Power(Plus(n,Negate(C1)),-1),Int(Power(Times(c,Csc(Plus(a,Times(b,x)))),Plus(n,Negate(C2))),x))),And(And(And(FreeQ(List(a,b,c),x),RationalQ(n)),Greater(n,C1)),Not(EvenQ(n))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,$($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_),x_Symbol),
    Condition(Plus(Times(CN1,Sin(Plus(a,Times(b,x))),Power(Times(c,Sec(Plus(a,Times(b,x)))),Plus(n,C1)),Power(Times(b,c,n),-1)),Times(Plus(n,C1),Power(Times(Sqr(c),n),-1),Int(Power(Times(c,Sec(Plus(a,Times(b,x)))),Plus(n,C2)),x))),And(And(FreeQ(List(a,b,c),x),RationalQ(n)),Less(n,CN1)))),
ISetDelayed(Int(Power(Times(c_DEFAULT,$($s("§csc"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_),x_Symbol),
    Condition(Plus(Times(Cos(Plus(a,Times(b,x))),Power(Times(c,Csc(Plus(a,Times(b,x)))),Plus(n,C1)),Power(Times(b,c,n),-1)),Times(Plus(n,C1),Power(Times(Sqr(c),n),-1),Int(Power(Times(c,Csc(Plus(a,Times(b,x)))),Plus(n,C2)),x))),And(And(FreeQ(List(a,b,c),x),RationalQ(n)),Less(n,CN1)))),
ISetDelayed(Int(Power(Times(c_DEFAULT,$($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_),x_Symbol),
    Condition(Times(Power(Cos(Plus(a,Times(b,x))),n),Power(Times(c,Sec(Plus(a,Times(b,x)))),n),Int(Power(Power(Cos(Plus(a,Times(b,x))),n),-1),x)),And(FreeQ(List(a,b,c),x),ZeroQ(Plus(Sqr(n),Negate(C1D4)))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,$($s("§csc"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_),x_Symbol),
    Condition(Times(Power(Sin(Plus(a,Times(b,x))),n),Power(Times(c,Csc(Plus(a,Times(b,x)))),n),Int(Power(Power(Sin(Plus(a,Times(b,x))),n),-1),x)),And(FreeQ(List(a,b,c),x),ZeroQ(Plus(Sqr(n),Negate(C1D4)))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,$($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_),x_Symbol),
    Condition(Times(Power(c,-1),Power(Cos(Plus(a,Times(b,x))),Plus(n,C1)),Power(Times(c,Sec(Plus(a,Times(b,x)))),Plus(n,C1)),Int(Power(Power(Cos(Plus(a,Times(b,x))),n),-1),x)),And(And(And(FreeQ(List(a,b,c),x),Not(IntegerQ(Times(C2,n)))),RationalQ(n)),Less(n,C0)))),
ISetDelayed(Int(Power(Times(c_DEFAULT,$($s("§csc"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_),x_Symbol),
    Condition(Times(Power(c,-1),Power(Sin(Plus(a,Times(b,x))),Plus(n,C1)),Power(Times(c,Csc(Plus(a,Times(b,x)))),Plus(n,C1)),Int(Power(Power(Sin(Plus(a,Times(b,x))),n),-1),x)),And(And(And(FreeQ(List(a,b,c),x),Not(IntegerQ(Times(C2,n)))),RationalQ(n)),Less(n,C0)))),
ISetDelayed(Int(Power(Times(c_DEFAULT,$($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_),x_Symbol),
    Condition(Times(c,Power(Cos(Plus(a,Times(b,x))),Plus(n,Negate(C1))),Power(Times(c,Sec(Plus(a,Times(b,x)))),Plus(n,Negate(C1))),Int(Power(Power(Cos(Plus(a,Times(b,x))),n),-1),x)),And(And(FreeQ(List(a,b,c,n),x),Not(IntegerQ(Times(C2,n)))),Not(And(RationalQ(n),Less(n,C0)))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,$($s("§csc"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_),x_Symbol),
    Condition(Times(c,Power(Sin(Plus(a,Times(b,x))),Plus(n,Negate(C1))),Power(Times(c,Csc(Plus(a,Times(b,x)))),Plus(n,Negate(C1))),Int(Power(Power(Sin(Plus(a,Times(b,x))),n),-1),x)),And(And(FreeQ(List(a,b,c,n),x),Not(IntegerQ(Times(C2,n)))),Not(And(RationalQ(n),Less(n,C0))))))
  );
}
