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
public class IntRules37 { 
  public static IAST RULES = List( 
ISetDelayed(Int(u_,x_Symbol),
    Condition(Int(DeactivateTrig(u,x),x),FunctionOfTrigOfLinearQ(u,x))),
ISetDelayed(Int($($s("§sin"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Times(CN1,Cos(Plus(a,Times(b,x))),Power(b,CN1)),FreeQ(List(a,b),x))),
ISetDelayed(Int($($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Times(Sin(Plus(a,Times(b,x))),Power(b,CN1)),FreeQ(List(a,b),x))),
ISetDelayed(Int(Power($($s("§sin"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),pn_),x_Symbol),
    Condition(Times(CN1,Power(b,CN1),Subst(Int(Expand(Power(Plus(C1,Times(CN1,Sqr(x))),Times(C1D2,Plus(pn,Times(CN1,C1)))),x),x),x,Cos(Plus(a,Times(b,x))))),And(And(And(FreeQ(List(a,b),x),RationalQ(pn)),Greater(pn,C1)),OddQ(pn)))),
ISetDelayed(Int(Power($($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),pn_),x_Symbol),
    Condition(Times(Power(b,CN1),Subst(Int(Expand(Power(Plus(C1,Times(CN1,Sqr(x))),Times(C1D2,Plus(pn,Times(CN1,C1)))),x),x),x,Sin(Plus(a,Times(b,x))))),And(And(And(FreeQ(List(a,b),x),RationalQ(pn)),Greater(pn,C1)),OddQ(pn)))),
ISetDelayed(Int(Sqr($($s("§sin"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Times(C1D2,x),Times(CN1,Cos(Plus(a,Times(b,x))),Sin(Plus(a,Times(b,x))),Power(Times(C2,b),CN1))),FreeQ(List(a,b),x))),
ISetDelayed(Int(Sqr($($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Times(C1D2,x),Times(Cos(Plus(a,Times(b,x))),Sin(Plus(a,Times(b,x))),Power(Times(C2,b),CN1))),FreeQ(List(a,b),x))),
ISetDelayed(Int(Power(Times(c_DEFAULT,$($s("§sin"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),pn_),x_Symbol),
    Condition(Plus(Times(CN1,c,Cos(Plus(a,Times(b,x))),Power(Times(c,Sin(Plus(a,Times(b,x)))),Plus(pn,Times(CN1,C1))),Power(Times(b,pn),CN1)),Times(Sqr(c),Plus(pn,Times(CN1,C1)),Power(pn,CN1),Int(Power(Times(c,Sin(Plus(a,Times(b,x)))),Plus(pn,Times(CN1,C2))),x))),And(And(And(FreeQ(List(a,b,c),x),RationalQ(pn)),Greater(pn,C1)),Not(OddQ(pn))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,$($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),pn_),x_Symbol),
    Condition(Plus(Times(c,Sin(Plus(a,Times(b,x))),Power(Times(c,Cos(Plus(a,Times(b,x)))),Plus(pn,Times(CN1,C1))),Power(Times(b,pn),CN1)),Times(Sqr(c),Plus(pn,Times(CN1,C1)),Power(pn,CN1),Int(Power(Times(c,Cos(Plus(a,Times(b,x)))),Plus(pn,Times(CN1,C2))),x))),And(And(And(FreeQ(List(a,b,c),x),RationalQ(pn)),Greater(pn,C1)),Not(OddQ(pn))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,$($s("§sin"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),pn_),x_Symbol),
    Condition(Plus(Times(Cos(Plus(a,Times(b,x))),Power(Times(c,Sin(Plus(a,Times(b,x)))),Plus(pn,C1)),Power(Times(b,c,Plus(pn,C1)),CN1)),Times(Plus(pn,C2),Power(Times(Sqr(c),Plus(pn,C1)),CN1),Int(Power(Times(c,Sin(Plus(a,Times(b,x)))),Plus(pn,C2)),x))),And(And(FreeQ(List(a,b,c),x),RationalQ(pn)),Less(pn,CN1)))),
ISetDelayed(Int(Power(Times(c_DEFAULT,$($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),pn_),x_Symbol),
    Condition(Plus(Times(CN1,Sin(Plus(a,Times(b,x))),Power(Times(c,Cos(Plus(a,Times(b,x)))),Plus(pn,C1)),Power(Times(b,c,Plus(pn,C1)),CN1)),Times(Plus(pn,C2),Power(Times(Sqr(c),Plus(pn,C1)),CN1),Int(Power(Times(c,Cos(Plus(a,Times(b,x)))),Plus(pn,C2)),x))),And(And(FreeQ(List(a,b,c),x),RationalQ(pn)),Less(pn,CN1)))),
ISetDelayed(Int(Sqrt($($s("§sin"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Times(CN2,EllipticE(Plus(Times(C1D4,Pi),Times(CN1,C1D2,Plus(a,Times(b,x)))),C2),Power(b,CN1)),FreeQ(List(a,b),x))),
ISetDelayed(Int(Sqrt($($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Times(C2,EllipticE(Times(C1D2,Plus(a,Times(b,x))),C2),Power(b,CN1)),FreeQ(List(a,b),x))),
ISetDelayed(Int(Power($($s("§sin"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),CN1D2),x_Symbol),
    Condition(Times(CN2,EllipticF(Plus(Times(C1D4,Pi),Times(CN1,C1D2,Plus(a,Times(b,x)))),C2),Power(b,CN1)),FreeQ(List(a,b),x))),
ISetDelayed(Int(Power($($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),CN1D2),x_Symbol),
    Condition(Times(C2,EllipticF(Times(C1D2,Plus(a,Times(b,x))),C2),Power(b,CN1)),FreeQ(List(a,b),x))),
ISetDelayed(Int(Power(Times(c_,$($s("§sin"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),pn_),x_Symbol),
    Condition(Times(Power(Times(c,Sin(Plus(a,Times(b,x)))),pn),Power(Power(Sin(Plus(a,Times(b,x))),pn),CN1),Int(Power(Sin(Plus(a,Times(b,x))),pn),x)),And(FreeQ(List(a,b,c),x),ZeroQ(Plus(Sqr(pn),Times(CN1,C1D4)))))),
ISetDelayed(Int(Power(Times(c_,$($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),pn_),x_Symbol),
    Condition(Times(Power(Times(c,Cos(Plus(a,Times(b,x)))),pn),Power(Power(Cos(Plus(a,Times(b,x))),pn),CN1),Int(Power(Cos(Plus(a,Times(b,x))),pn),x)),And(FreeQ(List(a,b,c),x),ZeroQ(Plus(Sqr(pn),Times(CN1,C1D4)))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,$($s("§sin"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),pn_),x_Symbol),
    Condition(Times(CN1,c,Cos(Plus(a,Times(b,x))),Power(Times(c,Sin(Plus(a,Times(b,x)))),Plus(pn,Times(CN1,C1))),Power(Times(b,Power(Sqr(Sin(Plus(a,Times(b,x)))),Times(C1D2,Plus(pn,Times(CN1,C1))))),CN1),Hypergeometric2F1(C1D2,Times(C1D2,Plus(C1,Times(CN1,pn))),QQ(3L,2L),Sqr(Cos(Plus(a,Times(b,x)))))),And(And(And(FreeQ(List(a,b,c),x),Not(IntegerQ(Times(C2,pn)))),RationalQ(pn)),Greater(pn,C0)))),
ISetDelayed(Int(Power(Times(c_DEFAULT,$($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),pn_),x_Symbol),
    Condition(Times(c,Sin(Plus(a,Times(b,x))),Power(Times(c,Cos(Plus(a,Times(b,x)))),Plus(pn,Times(CN1,C1))),Power(Times(b,Power(Sqr(Cos(Plus(a,Times(b,x)))),Times(C1D2,Plus(pn,Times(CN1,C1))))),CN1),Hypergeometric2F1(C1D2,Times(C1D2,Plus(C1,Times(CN1,pn))),QQ(3L,2L),Sqr(Sin(Plus(a,Times(b,x)))))),And(And(And(FreeQ(List(a,b,c),x),Not(IntegerQ(Times(C2,pn)))),RationalQ(pn)),Greater(pn,C0)))),
ISetDelayed(Int(Power(Times(c_DEFAULT,$($s("§sin"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),pn_),x_Symbol),
    Condition(Times(CN1,Cos(Plus(a,Times(b,x))),Power(Times(c,Sin(Plus(a,Times(b,x)))),Plus(pn,C1)),Power(Times(b,c,Power(Sqr(Sin(Plus(a,Times(b,x)))),Times(C1D2,Plus(pn,C1)))),CN1),Hypergeometric2F1(C1D2,Times(C1D2,Plus(C1,Times(CN1,pn))),QQ(3L,2L),Sqr(Cos(Plus(a,Times(b,x)))))),And(And(FreeQ(List(a,b,c,pn),x),Not(IntegerQ(Times(C2,pn)))),Not(And(RationalQ(pn),Greater(pn,C0)))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,$($s("§cos"),Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),pn_),x_Symbol),
    Condition(Times(Sin(Plus(a,Times(b,x))),Power(Times(c,Cos(Plus(a,Times(b,x)))),Plus(pn,C1)),Power(Times(b,c,Power(Sqr(Cos(Plus(a,Times(b,x)))),Times(C1D2,Plus(pn,C1)))),CN1),Hypergeometric2F1(C1D2,Times(C1D2,Plus(C1,Times(CN1,pn))),QQ(3L,2L),Sqr(Sin(Plus(a,Times(b,x)))))),And(And(FreeQ(List(a,b,c,pn),x),Not(IntegerQ(Times(C2,pn)))),Not(And(RationalQ(pn),Greater(pn,C0))))))
  );
}
