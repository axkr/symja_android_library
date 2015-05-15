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
public class IntRules132 { 
  public static IAST RULES = List( 
ISetDelayed(Int(SinhIntegral(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Times(Plus(a,Times(b,x)),SinhIntegral(Plus(a,Times(b,x))),Power(b,-1)),Times(CN1,Cosh(Plus(a,Times(b,x))),Power(b,-1))),FreeQ(List(a,b),x))),
ISetDelayed(Int(CoshIntegral(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Times(Plus(a,Times(b,x)),CoshIntegral(Plus(a,Times(b,x))),Power(b,-1)),Times(CN1,Sinh(Plus(a,Times(b,x))),Power(b,-1))),FreeQ(List(a,b),x))),
ISetDelayed(Int(Times(Power(x_,-1),SinhIntegral(Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Times(C1D2,b,x,HypergeometricPFQ(List(C1,C1,C1),List(C2,C2,C2),Times(CN1,b,x))),Times(C1D2,b,x,HypergeometricPFQ(List(C1,C1,C1),List(C2,C2,C2),Times(b,x)))),FreeQ(b,x))),
ISetDelayed(Int(Times(CoshIntegral(Times(b_DEFAULT,x_)),Power(x_,-1)),x_Symbol),
    Condition(Plus(Times(CN1D2,b,x,HypergeometricPFQ(List(C1,C1,C1),List(C2,C2,C2),Times(CN1,b,x))),Times(C1D2,b,x,HypergeometricPFQ(List(C1,C1,C1),List(C2,C2,C2),Times(b,x))),Times($s("EulerGamma"),Log(x)),Times(C1D2,Sqr(Log(Times(b,x))))),FreeQ(b,x))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),SinhIntegral(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),SinhIntegral(Plus(a,Times(b,x))),Power(Plus(m,C1),-1)),Times(CN1,b,Power(Plus(m,C1),-1),Int(Times(Power(x,Plus(m,C1)),Sinh(Plus(a,Times(b,x))),Power(Plus(a,Times(b,x)),-1)),x))),And(FreeQ(List(a,b,m),x),NonzeroQ(Plus(m,C1))))),
ISetDelayed(Int(Times(CoshIntegral(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),CoshIntegral(Plus(a,Times(b,x))),Power(Plus(m,C1),-1)),Times(CN1,b,Power(Plus(m,C1),-1),Int(Times(Power(x,Plus(m,C1)),Cosh(Plus(a,Times(b,x))),Power(Plus(a,Times(b,x)),-1)),x))),And(FreeQ(List(a,b,m),x),NonzeroQ(Plus(m,C1))))),
ISetDelayed(Int(Sqr(SinhIntegral(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Times(Plus(a,Times(b,x)),Sqr(SinhIntegral(Plus(a,Times(b,x)))),Power(b,-1)),Times(CN1,C2,Int(Times(Sinh(Plus(a,Times(b,x))),SinhIntegral(Plus(a,Times(b,x)))),x))),FreeQ(List(a,b),x))),
ISetDelayed(Int(Sqr(CoshIntegral(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Times(Plus(a,Times(b,x)),Sqr(CoshIntegral(Plus(a,Times(b,x)))),Power(b,-1)),Times(CN1,C2,Int(Times(Cosh(Plus(a,Times(b,x))),CoshIntegral(Plus(a,Times(b,x)))),x))),FreeQ(List(a,b),x))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Sqr(SinhIntegral(Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),Sqr(SinhIntegral(Times(b,x))),Power(Plus(m,C1),-1)),Times(CN1,C2,Power(Plus(m,C1),-1),Int(Times(Power(x,m),Sinh(Times(b,x)),SinhIntegral(Times(b,x))),x))),And(FreeQ(b,x),PositiveIntegerQ(m)))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Sqr(CoshIntegral(Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),Sqr(CoshIntegral(Times(b,x))),Power(Plus(m,C1),-1)),Times(CN1,C2,Power(Plus(m,C1),-1),Int(Times(Power(x,m),Cosh(Times(b,x)),CoshIntegral(Times(b,x))),x))),And(FreeQ(b,x),PositiveIntegerQ(m)))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Sqr(SinhIntegral(Plus(a_,Times(b_DEFAULT,x_))))),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),Sqr(SinhIntegral(Plus(a,Times(b,x)))),Power(Plus(m,C1),-1)),Times(a,Power(x,m),Sqr(SinhIntegral(Plus(a,Times(b,x)))),Power(Times(b,Plus(m,C1)),-1)),Times(CN1,C2,Power(Plus(m,C1),-1),Int(Times(Power(x,m),Sinh(Plus(a,Times(b,x))),SinhIntegral(Plus(a,Times(b,x)))),x)),Times(CN1,a,m,Power(Times(b,Plus(m,C1)),-1),Int(Times(Power(x,Plus(m,Negate(C1))),Sqr(SinhIntegral(Plus(a,Times(b,x))))),x))),And(FreeQ(List(a,b),x),PositiveIntegerQ(m)))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Sqr(CoshIntegral(Plus(a_,Times(b_DEFAULT,x_))))),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),Sqr(CoshIntegral(Plus(a,Times(b,x)))),Power(Plus(m,C1),-1)),Times(a,Power(x,m),Sqr(CoshIntegral(Plus(a,Times(b,x)))),Power(Times(b,Plus(m,C1)),-1)),Times(CN1,C2,Power(Plus(m,C1),-1),Int(Times(Power(x,m),Cosh(Plus(a,Times(b,x))),CoshIntegral(Plus(a,Times(b,x)))),x)),Times(CN1,a,m,Power(Times(b,Plus(m,C1)),-1),Int(Times(Power(x,Plus(m,Negate(C1))),Sqr(CoshIntegral(Plus(a,Times(b,x))))),x))),And(FreeQ(List(a,b),x),PositiveIntegerQ(m)))),
ISetDelayed(Int(Times(Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),SinhIntegral(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Times(Cosh(Plus(a,Times(b,x))),SinhIntegral(Plus(c,Times(d,x))),Power(b,-1)),Times(CN1,d,Power(b,-1),Int(Times(Cosh(Plus(a,Times(b,x))),Sinh(Plus(c,Times(d,x))),Power(Plus(c,Times(d,x)),-1)),x))),FreeQ(List(a,b,c,d),x))),
ISetDelayed(Int(Times(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),CoshIntegral(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Times(Sinh(Plus(a,Times(b,x))),CoshIntegral(Plus(c,Times(d,x))),Power(b,-1)),Times(CN1,d,Power(b,-1),Int(Times(Sinh(Plus(a,Times(b,x))),Cosh(Plus(c,Times(d,x))),Power(Plus(c,Times(d,x)),-1)),x))),FreeQ(List(a,b,c,d),x))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),SinhIntegral(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Times(Power(x,m),Cosh(Plus(a,Times(b,x))),SinhIntegral(Plus(c,Times(d,x))),Power(b,-1)),Times(CN1,d,Power(b,-1),Int(Times(Power(x,m),Cosh(Plus(a,Times(b,x))),Sinh(Plus(c,Times(d,x))),Power(Plus(c,Times(d,x)),-1)),x)),Times(CN1,m,Power(b,-1),Int(Times(Power(x,Plus(m,Negate(C1))),Cosh(Plus(a,Times(b,x))),SinhIntegral(Plus(c,Times(d,x)))),x))),And(And(FreeQ(List(a,b,c,d),x),IntegerQ(m)),Greater(m,C0)))),
ISetDelayed(Int(Times(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(x_,m_DEFAULT),CoshIntegral(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Times(Power(x,m),Sinh(Plus(a,Times(b,x))),CoshIntegral(Plus(c,Times(d,x))),Power(b,-1)),Times(CN1,d,Power(b,-1),Int(Times(Power(x,m),Sinh(Plus(a,Times(b,x))),Cosh(Plus(c,Times(d,x))),Power(Plus(c,Times(d,x)),-1)),x)),Times(CN1,m,Power(b,-1),Int(Times(Power(x,Plus(m,Negate(C1))),Sinh(Plus(a,Times(b,x))),CoshIntegral(Plus(c,Times(d,x)))),x))),And(And(FreeQ(List(a,b,c,d),x),IntegerQ(m)),Greater(m,C0)))),
ISetDelayed(Int(Times(Power(x_,m_),Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),SinhIntegral(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),Sinh(Plus(a,Times(b,x))),SinhIntegral(Plus(c,Times(d,x))),Power(Plus(m,C1),-1)),Times(CN1,d,Power(Plus(m,C1),-1),Int(Times(Power(x,Plus(m,C1)),Sinh(Plus(a,Times(b,x))),Sinh(Plus(c,Times(d,x))),Power(Plus(c,Times(d,x)),-1)),x)),Times(CN1,b,Power(Plus(m,C1),-1),Int(Times(Power(x,Plus(m,C1)),Cosh(Plus(a,Times(b,x))),SinhIntegral(Plus(c,Times(d,x)))),x))),And(And(FreeQ(List(a,b,c,d),x),IntegerQ(m)),Less(m,CN1)))),
ISetDelayed(Int(Times(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(x_,m_DEFAULT),CoshIntegral(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),Cosh(Plus(a,Times(b,x))),CoshIntegral(Plus(c,Times(d,x))),Power(Plus(m,C1),-1)),Times(CN1,d,Power(Plus(m,C1),-1),Int(Times(Power(x,Plus(m,C1)),Cosh(Plus(a,Times(b,x))),Cosh(Plus(c,Times(d,x))),Power(Plus(c,Times(d,x)),-1)),x)),Times(CN1,b,Power(Plus(m,C1),-1),Int(Times(Power(x,Plus(m,C1)),Sinh(Plus(a,Times(b,x))),CoshIntegral(Plus(c,Times(d,x)))),x))),And(And(FreeQ(List(a,b,c,d),x),IntegerQ(m)),Less(m,CN1)))),
ISetDelayed(Int(Times(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),SinhIntegral(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Times(Sinh(Plus(a,Times(b,x))),SinhIntegral(Plus(c,Times(d,x))),Power(b,-1)),Times(CN1,d,Power(b,-1),Int(Times(Sinh(Plus(a,Times(b,x))),Sinh(Plus(c,Times(d,x))),Power(Plus(c,Times(d,x)),-1)),x))),FreeQ(List(a,b,c,d),x))),
ISetDelayed(Int(Times(CoshIntegral(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Times(Cosh(Plus(a,Times(b,x))),CoshIntegral(Plus(c,Times(d,x))),Power(b,-1)),Times(CN1,d,Power(b,-1),Int(Times(Cosh(Plus(a,Times(b,x))),Cosh(Plus(c,Times(d,x))),Power(Plus(c,Times(d,x)),-1)),x))),FreeQ(List(a,b,c,d),x))),
ISetDelayed(Int(Times(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(x_,m_DEFAULT),SinhIntegral(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Times(Power(x,m),Sinh(Plus(a,Times(b,x))),SinhIntegral(Plus(c,Times(d,x))),Power(b,-1)),Times(CN1,d,Power(b,-1),Int(Times(Power(x,m),Sinh(Plus(a,Times(b,x))),Sinh(Plus(c,Times(d,x))),Power(Plus(c,Times(d,x)),-1)),x)),Times(CN1,m,Power(b,-1),Int(Times(Power(x,Plus(m,Negate(C1))),Sinh(Plus(a,Times(b,x))),SinhIntegral(Plus(c,Times(d,x)))),x))),And(FreeQ(List(a,b,c,d),x),PositiveIntegerQ(m)))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),CoshIntegral(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Times(Power(x,m),Cosh(Plus(a,Times(b,x))),CoshIntegral(Plus(c,Times(d,x))),Power(b,-1)),Times(CN1,d,Power(b,-1),Int(Times(Power(x,m),Cosh(Plus(a,Times(b,x))),Cosh(Plus(c,Times(d,x))),Power(Plus(c,Times(d,x)),-1)),x)),Times(CN1,m,Power(b,-1),Int(Times(Power(x,Plus(m,Negate(C1))),Cosh(Plus(a,Times(b,x))),CoshIntegral(Plus(c,Times(d,x)))),x))),And(FreeQ(List(a,b,c,d),x),PositiveIntegerQ(m)))),
ISetDelayed(Int(Times(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(x_,m_DEFAULT),SinhIntegral(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),Cosh(Plus(a,Times(b,x))),SinhIntegral(Plus(c,Times(d,x))),Power(Plus(m,C1),-1)),Times(CN1,d,Power(Plus(m,C1),-1),Int(Times(Power(x,Plus(m,C1)),Cosh(Plus(a,Times(b,x))),Sinh(Plus(c,Times(d,x))),Power(Plus(c,Times(d,x)),-1)),x)),Times(CN1,b,Power(Plus(m,C1),-1),Int(Times(Power(x,Plus(m,C1)),Sinh(Plus(a,Times(b,x))),SinhIntegral(Plus(c,Times(d,x)))),x))),And(And(FreeQ(List(a,b,c,d),x),IntegerQ(m)),Less(m,CN1)))),
ISetDelayed(Int(Times(Power(x_,m_),Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),CoshIntegral(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),Sinh(Plus(a,Times(b,x))),CoshIntegral(Plus(c,Times(d,x))),Power(Plus(m,C1),-1)),Times(CN1,d,Power(Plus(m,C1),-1),Int(Times(Power(x,Plus(m,C1)),Sinh(Plus(a,Times(b,x))),Cosh(Plus(c,Times(d,x))),Power(Plus(c,Times(d,x)),-1)),x)),Times(CN1,b,Power(Plus(m,C1),-1),Int(Times(Power(x,Plus(m,C1)),Cosh(Plus(a,Times(b,x))),CoshIntegral(Plus(c,Times(d,x)))),x))),And(And(FreeQ(List(a,b,c,d),x),IntegerQ(m)),Less(m,CN1))))
  );
}
