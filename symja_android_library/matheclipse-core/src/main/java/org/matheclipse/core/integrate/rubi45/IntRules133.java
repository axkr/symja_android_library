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
public class IntRules133 { 
  public static IAST RULES = List( 
ISetDelayed(Int(Gamma(pn_,Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Times(Plus(a,Times(b,x)),Gamma(pn,Plus(a,Times(b,x))),Power(b,CN1)),Times(CN1,Gamma(Plus(pn,C1),Plus(a,Times(b,x))),Power(b,CN1))),FreeQ(List(a,b),x))),
ISetDelayed(Int(Times(Gamma(pn_,Times(b_,x_)),Power(x_,CN1)),x_Symbol),
    Condition(Plus(Times(Gamma(pn),Log(x)),Times(CN1,Power(Times(b,x),pn),Power(pn,CN2),HypergeometricPFQ(List(pn,pn),List(Plus(C1,pn),Plus(C1,pn)),Times(CN1,b,x)))),And(FreeQ(List(b,pn),x),Not(And(IntegerQ(pn),LessEqual(pn,C0)))))),
ISetDelayed(Int(Times(Gamma(pn_,Times(b_,x_)),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),Gamma(pn,Times(b,x)),Power(Plus(m,C1),CN1)),Times(CN1,Power(x,m),Gamma(Plus(m,pn,C1),Times(b,x)),Power(Times(b,Plus(m,C1),Power(Times(b,x),m)),CN1))),And(FreeQ(List(b,m,pn),x),NonzeroQ(Plus(m,C1))))),
ISetDelayed(Int(Times(Gamma(pn_,Plus(a_,Times(b_DEFAULT,x_))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Block(List(Set($s("§$usegamma"),True)),Plus(Times(Power(x,Plus(m,C1)),Gamma(pn,Plus(a,Times(b,x))),Power(Plus(m,C1),CN1)),Times(b,Power(Plus(m,C1),CN1),Int(Times(Power(x,Plus(m,C1)),Power(Plus(a,Times(b,x)),Plus(pn,Times(CN1,C1))),Power(Power(E,Plus(a,Times(b,x))),CN1)),x)))),And(And(FreeQ(List(a,b,m,pn),x),Or(Or(PositiveIntegerQ(m),PositiveIntegerQ(pn)),IntegersQ(m,pn))),NonzeroQ(Plus(m,C1))))),
ISetDelayed(Int(LogGamma(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Times(PolyGamma(CN2,Plus(a,Times(b,x))),Power(b,CN1)),FreeQ(List(a,b),x))),
ISetDelayed(Int(Times(LogGamma(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Times(Power(x,m),PolyGamma(CN2,Plus(a,Times(b,x))),Power(b,CN1)),Times(CN1,m,Power(b,CN1),Int(Times(Power(x,Plus(m,Times(CN1,C1))),PolyGamma(CN2,Plus(a,Times(b,x)))),x))),And(And(FreeQ(List(a,b),x),RationalQ(m)),Greater(m,C0)))),
ISetDelayed(Int(PolyGamma(pn_,Plus(a_DEFAULT,Times(b_DEFAULT,x_))),x_Symbol),
    Condition(Times(PolyGamma(Plus(pn,Times(CN1,C1)),Plus(a,Times(b,x))),Power(b,CN1)),FreeQ(List(a,b,pn),x))),
ISetDelayed(Int(Times(PolyGamma(pn_,Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Times(Power(x,m),PolyGamma(Plus(pn,Times(CN1,C1)),Plus(a,Times(b,x))),Power(b,CN1)),Times(CN1,m,Power(b,CN1),Int(Times(Power(x,Plus(m,Times(CN1,C1))),PolyGamma(Plus(pn,Times(CN1,C1)),Plus(a,Times(b,x)))),x))),And(And(FreeQ(List(a,b,pn),x),RationalQ(m)),Greater(m,C0)))),
ISetDelayed(Int(Times(PolyGamma(pn_,Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),PolyGamma(pn,Plus(a,Times(b,x))),Power(Plus(m,C1),CN1)),Times(CN1,b,Power(Plus(m,C1),CN1),Int(Times(Power(x,Plus(m,C1)),PolyGamma(Plus(pn,C1),Plus(a,Times(b,x)))),x))),And(And(FreeQ(List(a,b,pn),x),RationalQ(m)),Less(m,CN1)))),
ISetDelayed(Int(Times(PolyGamma(C0,Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Gamma(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),pn_DEFAULT)),x_Symbol),
    Condition(Times(Power(Gamma(Plus(a,Times(b,x))),pn),Power(Times(b,pn),CN1)),FreeQ(List(a,b,pn),x))),
ISetDelayed(Int(Times(PolyGamma(C0,Plus(c_DEFAULT,Times(b_DEFAULT,x_))),Power(Factorial(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),pn_DEFAULT)),x_Symbol),
    Condition(Times(Power(Factorial(Plus(a,Times(b,x))),pn),Power(Times(b,pn),CN1)),And(FreeQ(List(a,b,c,pn),x),ZeroQ(Plus(a,Times(CN1,c),C1)))))
  );
}
