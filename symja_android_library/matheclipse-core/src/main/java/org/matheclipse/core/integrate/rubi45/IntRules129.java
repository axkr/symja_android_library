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
public class IntRules129 { 
  public static IAST RULES = List( 
ISetDelayed(Int(FresnelS(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Times(Plus(a,Times(b,x)),FresnelS(Plus(a,Times(b,x))),Power(b,-1)),Times(Cos(Times(C1D2,Pi,Sqr(Plus(a,Times(b,x))))),Power(Times(b,Pi),-1))),FreeQ(List(a,b),x))),
ISetDelayed(Int(FresnelC(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Times(Plus(a,Times(b,x)),FresnelC(Plus(a,Times(b,x))),Power(b,-1)),Times(CN1,Sin(Times(C1D2,Pi,Sqr(Plus(a,Times(b,x))))),Power(Times(b,Pi),-1))),FreeQ(List(a,b),x))),
ISetDelayed(Int(Times(FresnelS(Times(b_DEFAULT,x_)),Power(x_,-1)),x_Symbol),
    Condition(Plus(Times(C1D2,CI,b,x,HypergeometricPFQ(List(C1D2,C1D2),List(QQ(3L,2L),QQ(3L,2L)),Times(CN1D2,CI,Sqr(b),Pi,Sqr(x)))),Times(CN1,C1D2,CI,b,x,HypergeometricPFQ(List(C1D2,C1D2),List(QQ(3L,2L),QQ(3L,2L)),Times(C1D2,CI,Sqr(b),Pi,Sqr(x))))),FreeQ(b,x))),
ISetDelayed(Int(Times(FresnelC(Times(b_DEFAULT,x_)),Power(x_,-1)),x_Symbol),
    Condition(Plus(Times(C1D2,b,x,HypergeometricPFQ(List(C1D2,C1D2),List(QQ(3L,2L),QQ(3L,2L)),Times(CN1D2,CI,Sqr(b),Pi,Sqr(x)))),Times(C1D2,b,x,HypergeometricPFQ(List(C1D2,C1D2),List(QQ(3L,2L),QQ(3L,2L)),Times(C1D2,CI,Sqr(b),Pi,Sqr(x))))),FreeQ(b,x))),
ISetDelayed(Int(Times(FresnelS(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),FresnelS(Plus(a,Times(b,x))),Power(Plus(m,C1),-1)),Times(CN1,b,Power(Plus(m,C1),-1),Int(Times(Power(x,Plus(m,C1)),Sin(Times(C1D2,Pi,Sqr(Plus(a,Times(b,x)))))),x))),And(FreeQ(List(a,b,m),x),NonzeroQ(Plus(m,C1))))),
ISetDelayed(Int(Times(FresnelC(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),FresnelC(Plus(a,Times(b,x))),Power(Plus(m,C1),-1)),Times(CN1,b,Power(Plus(m,C1),-1),Int(Times(Power(x,Plus(m,C1)),Cos(Times(C1D2,Pi,Sqr(Plus(a,Times(b,x)))))),x))),And(FreeQ(List(a,b,m),x),NonzeroQ(Plus(m,C1))))),
ISetDelayed(Int(Sqr(FresnelS(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Times(Plus(a,Times(b,x)),Sqr(FresnelS(Plus(a,Times(b,x)))),Power(b,-1)),Times(CN1,C2,Int(Times(Plus(a,Times(b,x)),Sin(Times(C1D2,Pi,Sqr(Plus(a,Times(b,x))))),FresnelS(Plus(a,Times(b,x)))),x))),FreeQ(List(a,b),x))),
ISetDelayed(Int(Sqr(FresnelC(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Times(Plus(a,Times(b,x)),Sqr(FresnelC(Plus(a,Times(b,x)))),Power(b,-1)),Times(CN1,C2,Int(Times(Plus(a,Times(b,x)),Cos(Times(C1D2,Pi,Sqr(Plus(a,Times(b,x))))),FresnelC(Plus(a,Times(b,x)))),x))),FreeQ(List(a,b),x))),
ISetDelayed(Int(Times(Power(x_,m_),Sqr(FresnelS(Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),Sqr(FresnelS(Times(b,x))),Power(Plus(m,C1),-1)),Times(CN1,C2,b,Power(Plus(m,C1),-1),Int(Times(Power(x,Plus(m,C1)),Sin(Times(C1D2,Pi,Sqr(b),Sqr(x))),FresnelS(Times(b,x))),x))),And(And(And(FreeQ(b,x),IntegerQ(m)),Unequal(Plus(m,C1),C0)),Or(And(Greater(m,C0),EvenQ(m)),Equal(Mod(m,C4),C3))))),
ISetDelayed(Int(Times(Power(x_,m_),Sqr(FresnelC(Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),Sqr(FresnelC(Times(b,x))),Power(Plus(m,C1),-1)),Times(CN1,C2,b,Power(Plus(m,C1),-1),Int(Times(Power(x,Plus(m,C1)),Cos(Times(C1D2,Pi,Sqr(b),Sqr(x))),FresnelC(Times(b,x))),x))),And(And(And(FreeQ(b,x),IntegerQ(m)),Unequal(Plus(m,C1),C0)),Or(And(Greater(m,C0),EvenQ(m)),Equal(Mod(m,C4),C3))))),
ISetDelayed(Int(Times(x_,Sin(Times(c_DEFAULT,Sqr(x_))),FresnelS(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Times(CN1,Cos(Times(C1D2,Pi,Sqr(b),Sqr(x))),FresnelS(Times(b,x)),Power(Times(Pi,Sqr(b)),-1)),Times(Power(Times(C2,Pi,b),-1),Int(Sin(Times(Pi,Sqr(b),Sqr(x))),x))),And(FreeQ(List(b,c),x),ZeroQ(Plus(c,Times(CN1,C1D2,Pi,Sqr(b))))))),
ISetDelayed(Int(Times(x_,Cos(Times(c_DEFAULT,Sqr(x_))),FresnelC(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Times(Sin(Times(C1D2,Pi,Sqr(b),Sqr(x))),FresnelC(Times(b,x)),Power(Times(Pi,Sqr(b)),-1)),Times(CN1,Power(Times(C2,Pi,b),-1),Int(Sin(Times(Pi,Sqr(b),Sqr(x))),x))),And(FreeQ(List(b,c),x),ZeroQ(Plus(c,Times(CN1,C1D2,Pi,Sqr(b))))))),
ISetDelayed(Int(Times(Power(x_,m_),Sin(Times(c_DEFAULT,Sqr(x_))),FresnelS(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Times(CN1,Power(x,Plus(m,Negate(C1))),Cos(Times(C1D2,Pi,Sqr(b),Sqr(x))),FresnelS(Times(b,x)),Power(Times(Pi,Sqr(b)),-1)),Times(Power(Times(C2,Pi,b),-1),Int(Times(Power(x,Plus(m,Negate(C1))),Sin(Times(Pi,Sqr(b),Sqr(x)))),x)),Times(Plus(m,Negate(C1)),Power(Times(Pi,Sqr(b)),-1),Int(Times(Power(x,Plus(m,Negate(C2))),Cos(Times(C1D2,Pi,Sqr(b),Sqr(x))),FresnelS(Times(b,x))),x))),And(And(And(And(FreeQ(List(b,c),x),ZeroQ(Plus(c,Times(CN1,C1D2,Pi,Sqr(b))))),IntegerQ(m)),Greater(m,C1)),Not(Equal(Mod(m,C4),C2))))),
ISetDelayed(Int(Times(Cos(Times(c_DEFAULT,Sqr(x_))),Power(x_,m_),FresnelC(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,Negate(C1))),Sin(Times(C1D2,Pi,Sqr(b),Sqr(x))),FresnelC(Times(b,x)),Power(Times(Pi,Sqr(b)),-1)),Times(CN1,Power(Times(C2,Pi,b),-1),Int(Times(Power(x,Plus(m,Negate(C1))),Sin(Times(Pi,Sqr(b),Sqr(x)))),x)),Times(CN1,Plus(m,Negate(C1)),Power(Times(Pi,Sqr(b)),-1),Int(Times(Power(x,Plus(m,Negate(C2))),Sin(Times(C1D2,Pi,Sqr(b),Sqr(x))),FresnelC(Times(b,x))),x))),And(And(And(And(FreeQ(List(b,c),x),ZeroQ(Plus(c,Times(CN1,C1D2,Pi,Sqr(b))))),IntegerQ(m)),Greater(m,C1)),Not(Equal(Mod(m,C4),C2))))),
ISetDelayed(Int(Times(Power(x_,m_),Sin(Times(c_DEFAULT,Sqr(x_))),FresnelS(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),Sin(Times(C1D2,Pi,Sqr(b),Sqr(x))),FresnelS(Times(b,x)),Power(Plus(m,C1),-1)),Times(CN1,b,Power(x,Plus(m,C2)),Power(Times(C2,Plus(m,C1),Plus(m,C2)),-1)),Times(b,Power(Times(C2,Plus(m,C1)),-1),Int(Times(Power(x,Plus(m,C1)),Cos(Times(Pi,Sqr(b),Sqr(x)))),x)),Times(CN1,Pi,Sqr(b),Power(Plus(m,C1),-1),Int(Times(Power(x,Plus(m,C2)),Cos(Times(C1D2,Pi,Sqr(b),Sqr(x))),FresnelS(Times(b,x))),x))),And(And(And(And(FreeQ(List(b,c),x),ZeroQ(Plus(c,Times(CN1,C1D2,Pi,Sqr(b))))),IntegerQ(m)),Less(m,CN2)),Equal(Mod(m,C4),C0)))),
ISetDelayed(Int(Times(Cos(Times(c_DEFAULT,Sqr(x_))),Power(x_,m_),FresnelC(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),Cos(Times(C1D2,Pi,Sqr(b),Sqr(x))),FresnelC(Times(b,x)),Power(Plus(m,C1),-1)),Times(CN1,b,Power(x,Plus(m,C2)),Power(Times(C2,Plus(m,C1),Plus(m,C2)),-1)),Times(CN1,b,Power(Times(C2,Plus(m,C1)),-1),Int(Times(Power(x,Plus(m,C1)),Cos(Times(Pi,Sqr(b),Sqr(x)))),x)),Times(Pi,Sqr(b),Power(Plus(m,C1),-1),Int(Times(Power(x,Plus(m,C2)),Sin(Times(C1D2,Pi,Sqr(b),Sqr(x))),FresnelC(Times(b,x))),x))),And(And(And(And(FreeQ(List(b,c),x),ZeroQ(Plus(c,Times(CN1,C1D2,Pi,Sqr(b))))),IntegerQ(m)),Less(m,CN2)),Equal(Mod(m,C4),C0)))),
ISetDelayed(Int(Times(x_,Cos(Times(c_DEFAULT,Sqr(x_))),FresnelS(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Times(Sin(Times(C1D2,Pi,Sqr(b),Sqr(x))),FresnelS(Times(b,x)),Power(Times(Pi,Sqr(b)),-1)),Times(CN1,x,Power(Times(C2,Pi,b),-1)),Times(Power(Times(C2,Pi,b),-1),Int(Cos(Times(Pi,Sqr(b),Sqr(x))),x))),And(FreeQ(List(b,c),x),ZeroQ(Plus(c,Times(CN1,C1D2,Pi,Sqr(b))))))),
ISetDelayed(Int(Times(x_,Sin(Times(c_DEFAULT,Sqr(x_))),FresnelC(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Times(CN1,Cos(Times(C1D2,Pi,Sqr(b),Sqr(x))),FresnelC(Times(b,x)),Power(Times(Pi,Sqr(b)),-1)),Times(x,Power(Times(C2,Pi,b),-1)),Times(Power(Times(C2,Pi,b),-1),Int(Cos(Times(Pi,Sqr(b),Sqr(x))),x))),And(FreeQ(List(b,c),x),ZeroQ(Plus(c,Times(CN1,C1D2,Pi,Sqr(b))))))),
ISetDelayed(Int(Times(Cos(Times(c_DEFAULT,Sqr(x_))),Power(x_,m_),FresnelS(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,Negate(C1))),Sin(Times(C1D2,Pi,Sqr(b),Sqr(x))),FresnelS(Times(b,x)),Power(Times(Pi,Sqr(b)),-1)),Times(CN1,Power(x,m),Power(Times(C2,b,m,Pi),-1)),Times(Power(Times(C2,Pi,b),-1),Int(Times(Power(x,Plus(m,Negate(C1))),Cos(Times(Pi,Sqr(b),Sqr(x)))),x)),Times(CN1,Plus(m,Negate(C1)),Power(Times(Pi,Sqr(b)),-1),Int(Times(Power(x,Plus(m,Negate(C2))),Sin(Times(C1D2,Pi,Sqr(b),Sqr(x))),FresnelS(Times(b,x))),x))),And(And(And(And(FreeQ(List(b,c),x),ZeroQ(Plus(c,Times(CN1,C1D2,Pi,Sqr(b))))),IntegerQ(m)),Greater(m,C1)),Not(Equal(Mod(m,C4),C0))))),
ISetDelayed(Int(Times(Power(x_,m_),Sin(Times(c_DEFAULT,Sqr(x_))),FresnelC(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Times(CN1,Power(x,Plus(m,Negate(C1))),Cos(Times(C1D2,Pi,Sqr(b),Sqr(x))),FresnelC(Times(b,x)),Power(Times(Pi,Sqr(b)),-1)),Times(Power(x,m),Power(Times(C2,b,m,Pi),-1)),Times(Power(Times(C2,Pi,b),-1),Int(Times(Power(x,Plus(m,Negate(C1))),Cos(Times(Pi,Sqr(b),Sqr(x)))),x)),Times(Plus(m,Negate(C1)),Power(Times(Pi,Sqr(b)),-1),Int(Times(Power(x,Plus(m,Negate(C2))),Cos(Times(C1D2,Pi,Sqr(b),Sqr(x))),FresnelC(Times(b,x))),x))),And(And(And(And(FreeQ(List(b,c),x),ZeroQ(Plus(c,Times(CN1,C1D2,Pi,Sqr(b))))),IntegerQ(m)),Greater(m,C1)),Not(Equal(Mod(m,C4),C0))))),
ISetDelayed(Int(Times(Cos(Times(c_DEFAULT,Sqr(x_))),Power(x_,m_),FresnelS(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),Cos(Times(C1D2,Pi,Sqr(b),Sqr(x))),FresnelS(Times(b,x)),Power(Plus(m,C1),-1)),Times(CN1,b,Power(Times(C2,Plus(m,C1)),-1),Int(Times(Power(x,Plus(m,C1)),Sin(Times(Pi,Sqr(b),Sqr(x)))),x)),Times(Pi,Sqr(b),Power(Plus(m,C1),-1),Int(Times(Power(x,Plus(m,C2)),Sin(Times(C1D2,Pi,Sqr(b),Sqr(x))),FresnelS(Times(b,x))),x))),And(And(And(And(FreeQ(List(b,c),x),ZeroQ(Plus(c,Times(CN1,C1D2,Pi,Sqr(b))))),IntegerQ(m)),Less(m,CN1)),Equal(Mod(m,C4),C2)))),
ISetDelayed(Int(Times(Power(x_,m_),Sin(Times(c_DEFAULT,Sqr(x_))),FresnelC(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),Sin(Times(C1D2,Pi,Sqr(b),Sqr(x))),FresnelC(Times(b,x)),Power(Plus(m,C1),-1)),Times(CN1,b,Power(Times(C2,Plus(m,C1)),-1),Int(Times(Power(x,Plus(m,C1)),Sin(Times(Pi,Sqr(b),Sqr(x)))),x)),Times(CN1,Pi,Sqr(b),Power(Plus(m,C1),-1),Int(Times(Power(x,Plus(m,C2)),Cos(Times(C1D2,Pi,Sqr(b),Sqr(x))),FresnelC(Times(b,x))),x))),And(And(And(And(FreeQ(List(b,c),x),ZeroQ(Plus(c,Times(CN1,C1D2,Pi,Sqr(b))))),IntegerQ(m)),Less(m,CN1)),Equal(Mod(m,C4),C2))))
  );
}
