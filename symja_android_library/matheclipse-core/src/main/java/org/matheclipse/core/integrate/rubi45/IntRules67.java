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
public class IntRules67 { 
  public static IAST RULES = List( 
ISetDelayed(Int($($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Times(ArcTanh(Sin(Plus(a,Times(b,x)))),Power(b,CN1)),FreeQ(List(a,b),x))),
ISetDelayed(Int($($s("§csc"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Times(CN1,ArcTanh(Cos(Plus(a,Times(b,x)))),Power(b,CN1)),FreeQ(List(a,b),x))),
ISetDelayed(Int(Sqr($($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Times(Tan(Plus(a,Times(b,x))),Power(b,CN1)),FreeQ(List(a,b),x))),
ISetDelayed(Int(Sqr($($s("§csc"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Times(CN1,Cot(Plus(a,Times(b,x))),Power(b,CN1)),FreeQ(List(a,b),x))),
ISetDelayed(Int(Power($($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_),x_Symbol),
    Condition(Times(Power(b,CN1),Subst(Int(ExpandIntegrand(Power(Plus(C1,Sqr(x)),Plus(Times(C1D2,n),Times(CN1,C1))),x),x),x,Tan(Plus(a,Times(b,x))))),And(And(And(FreeQ(List(a,b),x),RationalQ(n)),Greater(n,C1)),EvenQ(n)))),
ISetDelayed(Int(Power($($s("§csc"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_),x_Symbol),
    Condition(Times(CN1,Power(b,CN1),Subst(Int(ExpandIntegrand(Power(Plus(C1,Sqr(x)),Plus(Times(C1D2,n),Times(CN1,C1))),x),x),x,Cot(Plus(a,Times(b,x))))),And(And(And(FreeQ(List(a,b),x),RationalQ(n)),Greater(n,C1)),EvenQ(n)))),
ISetDelayed(Int(Power(Times(c_DEFAULT,$($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_),x_Symbol),
    Condition(Plus(Times(c,Sin(Plus(a,Times(b,x))),Power(Times(c,Sec(Plus(a,Times(b,x)))),Plus(n,Times(CN1,C1))),Power(Times(b,Plus(n,Times(CN1,C1))),CN1)),Times(Sqr(c),Plus(n,Times(CN1,C2)),Power(Plus(n,Times(CN1,C1)),CN1),Int(Power(Times(c,Sec(Plus(a,Times(b,x)))),Plus(n,Times(CN1,C2))),x))),And(And(And(FreeQ(List(a,b,c),x),RationalQ(n)),Greater(n,C1)),Not(EvenQ(n))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,$($s("§csc"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_),x_Symbol),
    Condition(Plus(Times(CN1,c,Cos(Plus(a,Times(b,x))),Power(Times(c,Csc(Plus(a,Times(b,x)))),Plus(n,Times(CN1,C1))),Power(Times(b,Plus(n,Times(CN1,C1))),CN1)),Times(Sqr(c),Plus(n,Times(CN1,C2)),Power(Plus(n,Times(CN1,C1)),CN1),Int(Power(Times(c,Csc(Plus(a,Times(b,x)))),Plus(n,Times(CN1,C2))),x))),And(And(And(FreeQ(List(a,b,c),x),RationalQ(n)),Greater(n,C1)),Not(EvenQ(n))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,$($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_),x_Symbol),
    Condition(Plus(Times(CN1,Sin(Plus(a,Times(b,x))),Power(Times(c,Sec(Plus(a,Times(b,x)))),Plus(n,C1)),Power(Times(b,c,n),CN1)),Times(Plus(n,C1),Power(Times(Sqr(c),n),CN1),Int(Power(Times(c,Sec(Plus(a,Times(b,x)))),Plus(n,C2)),x))),And(And(FreeQ(List(a,b,c),x),RationalQ(n)),Less(n,CN1)))),
ISetDelayed(Int(Power(Times(c_DEFAULT,$($s("§csc"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_),x_Symbol),
    Condition(Plus(Times(Cos(Plus(a,Times(b,x))),Power(Times(c,Csc(Plus(a,Times(b,x)))),Plus(n,C1)),Power(Times(b,c,n),CN1)),Times(Plus(n,C1),Power(Times(Sqr(c),n),CN1),Int(Power(Times(c,Csc(Plus(a,Times(b,x)))),Plus(n,C2)),x))),And(And(FreeQ(List(a,b,c),x),RationalQ(n)),Less(n,CN1)))),
ISetDelayed(Int(Power(Times(c_DEFAULT,$($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_),x_Symbol),
    Condition(Times(Power(Cos(Plus(a,Times(b,x))),n),Power(Times(c,Sec(Plus(a,Times(b,x)))),n),Int(Power(Power(Cos(Plus(a,Times(b,x))),n),CN1),x)),And(FreeQ(List(a,b,c),x),ZeroQ(Plus(Sqr(n),Times(CN1,C1D4)))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,$($s("§csc"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_),x_Symbol),
    Condition(Times(Power(Sin(Plus(a,Times(b,x))),n),Power(Times(c,Csc(Plus(a,Times(b,x)))),n),Int(Power(Power(Sin(Plus(a,Times(b,x))),n),CN1),x)),And(FreeQ(List(a,b,c),x),ZeroQ(Plus(Sqr(n),Times(CN1,C1D4)))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,$($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_),x_Symbol),
    Condition(Times(Power(c,CN1),Power(Cos(Plus(a,Times(b,x))),Plus(n,C1)),Power(Times(c,Sec(Plus(a,Times(b,x)))),Plus(n,C1)),Int(Power(Power(Cos(Plus(a,Times(b,x))),n),CN1),x)),And(And(And(FreeQ(List(a,b,c),x),Not(IntegerQ(Times(C2,n)))),RationalQ(n)),Less(n,C0)))),
ISetDelayed(Int(Power(Times(c_DEFAULT,$($s("§csc"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_),x_Symbol),
    Condition(Times(Power(c,CN1),Power(Sin(Plus(a,Times(b,x))),Plus(n,C1)),Power(Times(c,Csc(Plus(a,Times(b,x)))),Plus(n,C1)),Int(Power(Power(Sin(Plus(a,Times(b,x))),n),CN1),x)),And(And(And(FreeQ(List(a,b,c),x),Not(IntegerQ(Times(C2,n)))),RationalQ(n)),Less(n,C0)))),
ISetDelayed(Int(Power(Times(c_DEFAULT,$($s("§sec"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_),x_Symbol),
    Condition(Times(c,Power(Cos(Plus(a,Times(b,x))),Plus(n,Times(CN1,C1))),Power(Times(c,Sec(Plus(a,Times(b,x)))),Plus(n,Times(CN1,C1))),Int(Power(Power(Cos(Plus(a,Times(b,x))),n),CN1),x)),And(And(FreeQ(List(a,b,c,n),x),Not(IntegerQ(Times(C2,n)))),Not(And(RationalQ(n),Less(n,C0)))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,$($s("§csc"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),n_),x_Symbol),
    Condition(Times(c,Power(Sin(Plus(a,Times(b,x))),Plus(n,Times(CN1,C1))),Power(Times(c,Csc(Plus(a,Times(b,x)))),Plus(n,Times(CN1,C1))),Int(Power(Power(Sin(Plus(a,Times(b,x))),n),CN1),x)),And(And(FreeQ(List(a,b,c,n),x),Not(IntegerQ(Times(C2,n)))),Not(And(RationalQ(n),Less(n,C0))))))
  );
}
