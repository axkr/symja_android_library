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
public class IntRules130 { 
  public static IAST RULES = List( 
ISetDelayed(Int(ExpIntegralE(pn_,Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Times(CN1,ExpIntegralE(Plus(pn,C1),Plus(a,Times(b,x))),Power(b,CN1)),FreeQ(List(a,b,pn),x))),
ISetDelayed(Int(Times(ExpIntegralE(pn_,Times(b_DEFAULT,x_)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Times(CN1,Power(x,m),ExpIntegralE(Plus(pn,C1),Times(b,x)),Power(b,CN1)),Times(m,Power(b,CN1),Int(Times(Power(x,Plus(m,Times(CN1,C1))),ExpIntegralE(Plus(pn,C1),Times(b,x))),x))),And(And(FreeQ(b,x),ZeroQ(Plus(m,pn))),PositiveIntegerQ(m)))),
ISetDelayed(Int(Times(ExpIntegralE(C1,Times(b_DEFAULT,x_)),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Times(b,x,HypergeometricPFQ(List(C1,C1,C1),List(C2,C2,C2),Times(CN1,b,x))),Times(CN1,$s("EulerGamma"),Log(x)),Times(CN1,C1D2,Sqr(Log(Times(b,x))))),FreeQ(b,x))),
ISetDelayed(Int(Times(ExpIntegralE(pn_,Times(b_DEFAULT,x_)),Power(x_,m_)),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),ExpIntegralE(pn,Times(b,x)),Power(Plus(m,C1),CN1)),Times(b,Power(Plus(m,C1),CN1),Int(Times(Power(x,Plus(m,C1)),ExpIntegralE(Plus(pn,Times(CN1,C1)),Times(b,x))),x))),And(And(And(FreeQ(b,x),ZeroQ(Plus(m,pn))),IntegerQ(m)),Less(m,CN1)))),
ISetDelayed(Int(Times(ExpIntegralE(pn_,Times(b_DEFAULT,x_)),Power(x_,m_)),x_Symbol),
    Condition(Plus(Times(Power(x,m),Gamma(Plus(m,C1)),Log(x),Power(Times(b,Power(Times(b,x),m)),CN1)),Times(CN1,Power(x,Plus(m,C1)),HypergeometricPFQ(List(Plus(m,C1),Plus(m,C1)),List(Plus(m,C2),Plus(m,C2)),Times(CN1,b,x)),Power(Plus(m,C1),CN2))),And(And(FreeQ(List(b,m,pn),x),ZeroQ(Plus(m,pn))),Not(IntegerQ(m))))),
ISetDelayed(Int(Times(ExpIntegralE(pn_,Times(b_DEFAULT,x_)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),ExpIntegralE(pn,Times(b,x)),Power(Plus(m,pn),CN1)),Times(CN1,Power(x,Plus(m,C1)),ExpIntegralE(Times(CN1,m),Times(b,x)),Power(Plus(m,pn),CN1))),And(FreeQ(List(b,m,pn),x),NonzeroQ(Plus(m,pn))))),
ISetDelayed(Int(Times(ExpIntegralE(pn_,Plus(a_,Times(b_DEFAULT,x_))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Times(CN1,Power(x,m),ExpIntegralE(Plus(pn,C1),Plus(a,Times(b,x))),Power(b,CN1)),Times(m,Power(b,CN1),Int(Times(Power(x,Plus(m,Times(CN1,C1))),ExpIntegralE(Plus(pn,C1),Plus(a,Times(b,x)))),x))),And(FreeQ(List(a,b,m,pn),x),Or(Or(PositiveIntegerQ(m),NegativeIntegerQ(pn)),And(And(RationalQ(m,pn),Greater(m,C0)),Less(pn,CN1)))))),
ISetDelayed(Int(Times(ExpIntegralE(pn_,Plus(a_,Times(b_DEFAULT,x_))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),ExpIntegralE(pn,Plus(a,Times(b,x))),Power(Plus(m,C1),CN1)),Times(b,Power(Plus(m,C1),CN1),Int(Times(Power(x,Plus(m,C1)),ExpIntegralE(Plus(pn,Times(CN1,C1)),Plus(a,Times(b,x)))),x))),And(And(FreeQ(List(a,b,m),x),Or(PositiveIntegerQ(pn),And(And(RationalQ(m,pn),Less(m,CN1)),Greater(pn,C0)))),NonzeroQ(Plus(m,C1))))),
ISetDelayed(Int(ExpIntegralEi(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Times(Plus(a,Times(b,x)),ExpIntegralEi(Plus(a,Times(b,x))),Power(b,CN1)),Times(CN1,Power(E,Plus(a,Times(b,x))),Power(b,CN1))),FreeQ(List(a,b),x))),
ISetDelayed(Int(Times(ExpIntegralEi(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),ExpIntegralEi(Plus(a,Times(b,x))),Power(Plus(m,C1),CN1)),Times(CN1,b,Power(Plus(m,C1),CN1),Int(Times(Power(x,Plus(m,C1)),Power(E,Plus(a,Times(b,x))),Power(Plus(a,Times(b,x)),CN1)),x))),And(FreeQ(List(a,b,m),x),NonzeroQ(Plus(m,C1))))),
ISetDelayed(Int(Sqr(ExpIntegralEi(Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Times(Plus(a,Times(b,x)),Sqr(ExpIntegralEi(Plus(a,Times(b,x)))),Power(b,CN1)),Times(CN1,C2,Int(Times(Power(E,Plus(a,Times(b,x))),ExpIntegralEi(Plus(a,Times(b,x)))),x))),FreeQ(List(a,b),x))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Sqr(ExpIntegralEi(Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),Sqr(ExpIntegralEi(Times(b,x))),Power(Plus(m,C1),CN1)),Times(CN1,C2,Power(Plus(m,C1),CN1),Int(Times(Power(x,m),Power(E,Times(b,x)),ExpIntegralEi(Times(b,x))),x))),And(FreeQ(b,x),PositiveIntegerQ(m)))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Sqr(ExpIntegralEi(Plus(a_,Times(b_DEFAULT,x_))))),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),Sqr(ExpIntegralEi(Plus(a,Times(b,x)))),Power(Plus(m,C1),CN1)),Times(a,Power(x,m),Sqr(ExpIntegralEi(Plus(a,Times(b,x)))),Power(Times(b,Plus(m,C1)),CN1)),Times(CN1,C2,Power(Plus(m,C1),CN1),Int(Times(Power(x,m),Power(E,Plus(a,Times(b,x))),ExpIntegralEi(Plus(a,Times(b,x)))),x)),Times(CN1,a,m,Power(Times(b,Plus(m,C1)),CN1),Int(Times(Power(x,Plus(m,Times(CN1,C1))),Sqr(ExpIntegralEi(Plus(a,Times(b,x))))),x))),And(FreeQ(List(a,b),x),PositiveIntegerQ(m)))),
ISetDelayed(Int(Times(ExpIntegralEi(Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),Power(E,Plus(a_DEFAULT,Times(b_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Times(Power(E,Plus(a,Times(b,x))),ExpIntegralEi(Plus(c,Times(pd,x))),Power(b,CN1)),Times(CN1,pd,Power(b,CN1),Int(Times(Power(E,Plus(a,c,Times(Plus(b,pd),x))),Power(Plus(c,Times(pd,x)),CN1)),x))),FreeQ(List(a,b,c,pd),x))),
ISetDelayed(Int(Times(ExpIntegralEi(Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),Power(E,Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Times(Power(x,m),Power(E,Plus(a,Times(b,x))),ExpIntegralEi(Plus(c,Times(pd,x))),Power(b,CN1)),Times(CN1,pd,Power(b,CN1),Int(Times(Power(x,m),Power(E,Plus(a,c,Times(Plus(b,pd),x))),Power(Plus(c,Times(pd,x)),CN1)),x)),Times(CN1,m,Power(b,CN1),Int(Times(Power(x,Plus(m,Times(CN1,C1))),Power(E,Plus(a,Times(b,x))),ExpIntegralEi(Plus(c,Times(pd,x)))),x))),And(FreeQ(List(a,b,c,pd),x),PositiveIntegerQ(m)))),
ISetDelayed(Int(Times(ExpIntegralEi(Plus(c_DEFAULT,Times(x_,pd_DEFAULT))),Power(E,Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(x_,m_)),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),Power(E,Plus(a,Times(b,x))),ExpIntegralEi(Plus(c,Times(pd,x))),Power(Plus(m,C1),CN1)),Times(CN1,pd,Power(Plus(m,C1),CN1),Int(Times(Power(x,Plus(m,C1)),Power(E,Plus(a,c,Times(Plus(b,pd),x))),Power(Plus(c,Times(pd,x)),CN1)),x)),Times(CN1,b,Power(Plus(m,C1),CN1),Int(Times(Power(x,Plus(m,C1)),Power(E,Plus(a,Times(b,x))),ExpIntegralEi(Plus(c,Times(pd,x)))),x))),And(And(FreeQ(List(a,b,c,pd),x),IntegerQ(m)),Less(m,CN1)))),
ISetDelayed(Int(LogIntegral(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Times(Plus(a,Times(b,x)),LogIntegral(Plus(a,Times(b,x))),Power(b,CN1)),Times(CN1,ExpIntegralEi(Times(C2,Log(Plus(a,Times(b,x))))),Power(b,CN1))),FreeQ(List(a,b),x))),
ISetDelayed(Int(Times(LogIntegral(Times(b_DEFAULT,x_)),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Times(CN1,b,x),Times(Log(Times(b,x)),LogIntegral(Times(b,x)))),FreeQ(b,x))),
ISetDelayed(Int(Times(LogIntegral(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),LogIntegral(Plus(a,Times(b,x))),Power(Plus(m,C1),CN1)),Times(CN1,b,Power(Plus(m,C1),CN1),Int(Times(Power(x,Plus(m,C1)),Power(Log(Plus(a,Times(b,x))),CN1)),x))),And(FreeQ(List(a,b,m),x),NonzeroQ(Plus(m,C1)))))
  );
}
