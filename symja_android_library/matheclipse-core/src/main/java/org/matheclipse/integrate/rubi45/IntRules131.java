package org.matheclipse.integrate.rubi45;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.integrate.rubi45.UtilityFunctionCtors.*;
import static org.matheclipse.integrate.rubi45.UtilityFunctions.*;
import org.matheclipse.core.interfaces.IAST;

/** 
 * IndefiniteIntegrationRules from the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi -
 * rule-based integrator</a>.
 *  
 */
public class IntRules131 { 
  public static IAST RULES = List( 
ISetDelayed(Int(SinIntegral(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Times(Plus(a,Times(b,x)),SinIntegral(Plus(a,Times(b,x))),Power(b,-1)),Times(Cos(Plus(a,Times(b,x))),Power(b,-1))),FreeQ(List(a,b),x))),
ISetDelayed(Int(CosIntegral(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Times(Plus(a,Times(b,x)),CosIntegral(Plus(a,Times(b,x))),Power(b,-1)),Times(CN1,Sin(Plus(a,Times(b,x))),Power(b,-1))),FreeQ(List(a,b),x))),
ISetDelayed(Int(Times(Power(x_,-1),SinIntegral(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Times(C1D2,b,x,HypergeometricPFQ(List(C1,C1,C1),List(C2,C2,C2),Times(CN1,CI,b,x))),Times(C1D2,b,x,HypergeometricPFQ(List(C1,C1,C1),List(C2,C2,C2),Times(CI,b,x)))),FreeQ(b,x))),
ISetDelayed(Int(Times(CosIntegral(Times(b_DEFAULT,x_)),Power(x_,-1)),x_Symbol),
    Condition(Plus(Times(CN1D2,CI,b,x,HypergeometricPFQ(List(C1,C1,C1),List(C2,C2,C2),Times(CN1,CI,b,x))),Times(C1D2,CI,b,x,HypergeometricPFQ(List(C1,C1,C1),List(C2,C2,C2),Times(CI,b,x))),Times($s("EulerGamma"),Log(x)),Times(C1D2,Sqr(Log(Times(b,x))))),FreeQ(b,x))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),SinIntegral(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),SinIntegral(Plus(a,Times(b,x))),Power(Plus(m,C1),-1)),Times(CN1,b,Power(Plus(m,C1),-1),Int(Times(Power(x,Plus(m,C1)),Sin(Plus(a,Times(b,x))),Power(Plus(a,Times(b,x)),-1)),x))),And(FreeQ(List(a,b,m),x),NonzeroQ(Plus(m,C1))))),
ISetDelayed(Int(Times(CosIntegral(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),CosIntegral(Plus(a,Times(b,x))),Power(Plus(m,C1),-1)),Times(CN1,b,Power(Plus(m,C1),-1),Int(Times(Power(x,Plus(m,C1)),Cos(Plus(a,Times(b,x))),Power(Plus(a,Times(b,x)),-1)),x))),And(FreeQ(List(a,b,m),x),NonzeroQ(Plus(m,C1))))),
ISetDelayed(Int(Sqr(SinIntegral(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Times(Plus(a,Times(b,x)),Sqr(SinIntegral(Plus(a,Times(b,x)))),Power(b,-1)),Times(CN1,C2,Int(Times(Sin(Plus(a,Times(b,x))),SinIntegral(Plus(a,Times(b,x)))),x))),FreeQ(List(a,b),x))),
ISetDelayed(Int(Sqr(CosIntegral(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Times(Plus(a,Times(b,x)),Sqr(CosIntegral(Plus(a,Times(b,x)))),Power(b,-1)),Times(CN1,C2,Int(Times(Cos(Plus(a,Times(b,x))),CosIntegral(Plus(a,Times(b,x)))),x))),FreeQ(List(a,b),x))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Sqr(SinIntegral(Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),Sqr(SinIntegral(Times(b,x))),Power(Plus(m,C1),-1)),Times(CN1,C2,Power(Plus(m,C1),-1),Int(Times(Power(x,m),Sin(Times(b,x)),SinIntegral(Times(b,x))),x))),And(FreeQ(b,x),PositiveIntegerQ(m)))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Sqr(CosIntegral(Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),Sqr(CosIntegral(Times(b,x))),Power(Plus(m,C1),-1)),Times(CN1,C2,Power(Plus(m,C1),-1),Int(Times(Power(x,m),Cos(Times(b,x)),CosIntegral(Times(b,x))),x))),And(FreeQ(b,x),PositiveIntegerQ(m)))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Sqr(SinIntegral(Plus(a_,Times(b_DEFAULT,x_))))),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),Sqr(SinIntegral(Plus(a,Times(b,x)))),Power(Plus(m,C1),-1)),Times(a,Power(x,m),Sqr(SinIntegral(Plus(a,Times(b,x)))),Power(Times(b,Plus(m,C1)),-1)),Times(CN1,C2,Power(Plus(m,C1),-1),Int(Times(Power(x,m),Sin(Plus(a,Times(b,x))),SinIntegral(Plus(a,Times(b,x)))),x)),Times(CN1,a,m,Power(Times(b,Plus(m,C1)),-1),Int(Times(Power(x,Plus(m,Negate(C1))),Sqr(SinIntegral(Plus(a,Times(b,x))))),x))),And(FreeQ(List(a,b),x),PositiveIntegerQ(m)))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Sqr(CosIntegral(Plus(a_,Times(b_DEFAULT,x_))))),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),Sqr(CosIntegral(Plus(a,Times(b,x)))),Power(Plus(m,C1),-1)),Times(a,Power(x,m),Sqr(CosIntegral(Plus(a,Times(b,x)))),Power(Times(b,Plus(m,C1)),-1)),Times(CN1,C2,Power(Plus(m,C1),-1),Int(Times(Power(x,m),Cos(Plus(a,Times(b,x))),CosIntegral(Plus(a,Times(b,x)))),x)),Times(CN1,a,m,Power(Times(b,Plus(m,C1)),-1),Int(Times(Power(x,Plus(m,Negate(C1))),Sqr(CosIntegral(Plus(a,Times(b,x))))),x))),And(FreeQ(List(a,b),x),PositiveIntegerQ(m)))),
ISetDelayed(Int(Times(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),SinIntegral(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Times(CN1,Cos(Plus(a,Times(b,x))),SinIntegral(Plus(c,Times(d,x))),Power(b,-1)),Times(d,Power(b,-1),Int(Times(Cos(Plus(a,Times(b,x))),Sin(Plus(c,Times(d,x))),Power(Plus(c,Times(d,x)),-1)),x))),FreeQ(List(a,b,c,d),x))),
ISetDelayed(Int(Times(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),CosIntegral(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Times(Sin(Plus(a,Times(b,x))),CosIntegral(Plus(c,Times(d,x))),Power(b,-1)),Times(CN1,d,Power(b,-1),Int(Times(Sin(Plus(a,Times(b,x))),Cos(Plus(c,Times(d,x))),Power(Plus(c,Times(d,x)),-1)),x))),FreeQ(List(a,b,c,d),x))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),SinIntegral(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Times(CN1,Power(x,m),Cos(Plus(a,Times(b,x))),SinIntegral(Plus(c,Times(d,x))),Power(b,-1)),Times(d,Power(b,-1),Int(Times(Power(x,m),Cos(Plus(a,Times(b,x))),Sin(Plus(c,Times(d,x))),Power(Plus(c,Times(d,x)),-1)),x)),Times(m,Power(b,-1),Int(Times(Power(x,Plus(m,Negate(C1))),Cos(Plus(a,Times(b,x))),SinIntegral(Plus(c,Times(d,x)))),x))),And(FreeQ(List(a,b,c,d),x),PositiveIntegerQ(m)))),
ISetDelayed(Int(Times(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(x_,m_DEFAULT),CosIntegral(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Times(Power(x,m),Sin(Plus(a,Times(b,x))),CosIntegral(Plus(c,Times(d,x))),Power(b,-1)),Times(CN1,d,Power(b,-1),Int(Times(Power(x,m),Sin(Plus(a,Times(b,x))),Cos(Plus(c,Times(d,x))),Power(Plus(c,Times(d,x)),-1)),x)),Times(CN1,m,Power(b,-1),Int(Times(Power(x,Plus(m,Negate(C1))),Sin(Plus(a,Times(b,x))),CosIntegral(Plus(c,Times(d,x)))),x))),And(FreeQ(List(a,b,c,d),x),PositiveIntegerQ(m)))),
ISetDelayed(Int(Times(Power(x_,m_),Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),SinIntegral(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),Sin(Plus(a,Times(b,x))),SinIntegral(Plus(c,Times(d,x))),Power(Plus(m,C1),-1)),Times(CN1,d,Power(Plus(m,C1),-1),Int(Times(Power(x,Plus(m,C1)),Sin(Plus(a,Times(b,x))),Sin(Plus(c,Times(d,x))),Power(Plus(c,Times(d,x)),-1)),x)),Times(CN1,b,Power(Plus(m,C1),-1),Int(Times(Power(x,Plus(m,C1)),Cos(Plus(a,Times(b,x))),SinIntegral(Plus(c,Times(d,x)))),x))),And(And(FreeQ(List(a,b,c,d),x),IntegerQ(m)),Less(m,CN1)))),
ISetDelayed(Int(Times(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(x_,m_DEFAULT),CosIntegral(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),Cos(Plus(a,Times(b,x))),CosIntegral(Plus(c,Times(d,x))),Power(Plus(m,C1),-1)),Times(CN1,d,Power(Plus(m,C1),-1),Int(Times(Power(x,Plus(m,C1)),Cos(Plus(a,Times(b,x))),Cos(Plus(c,Times(d,x))),Power(Plus(c,Times(d,x)),-1)),x)),Times(b,Power(Plus(m,C1),-1),Int(Times(Power(x,Plus(m,C1)),Sin(Plus(a,Times(b,x))),CosIntegral(Plus(c,Times(d,x)))),x))),And(And(FreeQ(List(a,b,c,d),x),IntegerQ(m)),Less(m,CN1)))),
ISetDelayed(Int(Times(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),SinIntegral(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Times(Sin(Plus(a,Times(b,x))),SinIntegral(Plus(c,Times(d,x))),Power(b,-1)),Times(CN1,d,Power(b,-1),Int(Times(Sin(Plus(a,Times(b,x))),Sin(Plus(c,Times(d,x))),Power(Plus(c,Times(d,x)),-1)),x))),FreeQ(List(a,b,c,d),x))),
ISetDelayed(Int(Times(CosIntegral(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Times(CN1,Cos(Plus(a,Times(b,x))),CosIntegral(Plus(c,Times(d,x))),Power(b,-1)),Times(d,Power(b,-1),Int(Times(Cos(Plus(a,Times(b,x))),Cos(Plus(c,Times(d,x))),Power(Plus(c,Times(d,x)),-1)),x))),FreeQ(List(a,b,c,d),x))),
ISetDelayed(Int(Times(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(x_,m_DEFAULT),SinIntegral(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Times(Power(x,m),Sin(Plus(a,Times(b,x))),SinIntegral(Plus(c,Times(d,x))),Power(b,-1)),Times(CN1,d,Power(b,-1),Int(Times(Power(x,m),Sin(Plus(a,Times(b,x))),Sin(Plus(c,Times(d,x))),Power(Plus(c,Times(d,x)),-1)),x)),Times(CN1,m,Power(b,-1),Int(Times(Power(x,Plus(m,Negate(C1))),Sin(Plus(a,Times(b,x))),SinIntegral(Plus(c,Times(d,x)))),x))),And(FreeQ(List(a,b,c,d),x),PositiveIntegerQ(m)))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),CosIntegral(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Times(CN1,Power(x,m),Cos(Plus(a,Times(b,x))),CosIntegral(Plus(c,Times(d,x))),Power(b,-1)),Times(d,Power(b,-1),Int(Times(Power(x,m),Cos(Plus(a,Times(b,x))),Cos(Plus(c,Times(d,x))),Power(Plus(c,Times(d,x)),-1)),x)),Times(m,Power(b,-1),Int(Times(Power(x,Plus(m,Negate(C1))),Cos(Plus(a,Times(b,x))),CosIntegral(Plus(c,Times(d,x)))),x))),And(FreeQ(List(a,b,c,d),x),PositiveIntegerQ(m)))),
ISetDelayed(Int(Times(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(x_,m_DEFAULT),SinIntegral(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),Cos(Plus(a,Times(b,x))),SinIntegral(Plus(c,Times(d,x))),Power(Plus(m,C1),-1)),Times(CN1,d,Power(Plus(m,C1),-1),Int(Times(Power(x,Plus(m,C1)),Cos(Plus(a,Times(b,x))),Sin(Plus(c,Times(d,x))),Power(Plus(c,Times(d,x)),-1)),x)),Times(b,Power(Plus(m,C1),-1),Int(Times(Power(x,Plus(m,C1)),Sin(Plus(a,Times(b,x))),SinIntegral(Plus(c,Times(d,x)))),x))),And(And(FreeQ(List(a,b,c,d),x),IntegerQ(m)),Less(m,CN1)))),
ISetDelayed(Int(Times(Power(x_,m_),Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),CosIntegral(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),Sin(Plus(a,Times(b,x))),CosIntegral(Plus(c,Times(d,x))),Power(Plus(m,C1),-1)),Times(CN1,d,Power(Plus(m,C1),-1),Int(Times(Power(x,Plus(m,C1)),Sin(Plus(a,Times(b,x))),Cos(Plus(c,Times(d,x))),Power(Plus(c,Times(d,x)),-1)),x)),Times(CN1,b,Power(Plus(m,C1),-1),Int(Times(Power(x,Plus(m,C1)),Cos(Plus(a,Times(b,x))),CosIntegral(Plus(c,Times(d,x)))),x))),And(And(FreeQ(List(a,b,c,d),x),IntegerQ(m)),Less(m,CN1))))
  );
}
