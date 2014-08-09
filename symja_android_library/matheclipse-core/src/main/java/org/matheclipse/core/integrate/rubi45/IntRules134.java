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
public class IntRules134 { 
  public static IAST RULES = List( 
ISetDelayed(Int(Zeta(C2,Plus(Times(b_DEFAULT,x_),a_DEFAULT)),x_Symbol),
    Condition(Int(PolyGamma(C1,Plus(a,Times(b,x))),x),FreeQ(List(a,b),x))),
ISetDelayed(Int(Zeta(s_,Plus(Times(b_DEFAULT,x_),a_DEFAULT)),x_Symbol),
    Condition(Times(CN1,Zeta(Plus(s,Times(CN1,C1)),Plus(a,Times(b,x))),Power(Times(b,Plus(s,Times(CN1,C1))),CN1)),And(And(FreeQ(List(a,b,s),x),NonzeroQ(Plus(s,Times(CN1,C1)))),NonzeroQ(Plus(s,Times(CN1,C2)))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Zeta(C2,Plus(Times(b_DEFAULT,x_),a_DEFAULT))),x_Symbol),
    Condition(Int(Times(Power(x,m),PolyGamma(C1,Plus(a,Times(b,x)))),x),And(FreeQ(List(a,b),x),RationalQ(m)))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Zeta(s_,Plus(Times(b_DEFAULT,x_),a_DEFAULT))),x_Symbol),
    Condition(Plus(Times(CN1,Power(x,m),Zeta(Plus(s,Times(CN1,C1)),Plus(a,Times(b,x))),Power(Times(b,Plus(s,Times(CN1,C1))),CN1)),Times(m,Power(Times(b,Plus(s,Times(CN1,C1))),CN1),Int(Times(Power(x,Plus(m,Times(CN1,C1))),Zeta(Plus(s,Times(CN1,C1)),Plus(a,Times(b,x)))),x))),And(And(And(And(FreeQ(List(a,b,s),x),NonzeroQ(Plus(s,Times(CN1,C1)))),NonzeroQ(Plus(s,Times(CN1,C2)))),RationalQ(m)),Greater(m,C0)))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Zeta(s_,Plus(Times(b_DEFAULT,x_),a_DEFAULT))),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),Zeta(s,Plus(a,Times(b,x))),Power(Plus(m,C1),CN1)),Times(b,s,Power(Plus(m,C1),CN1),Int(Times(Power(x,Plus(m,C1)),Zeta(Plus(s,C1),Plus(a,Times(b,x)))),x))),And(And(And(And(FreeQ(List(a,b,s),x),NonzeroQ(Plus(s,Times(CN1,C1)))),NonzeroQ(Plus(s,Times(CN1,C2)))),RationalQ(m)),Less(m,CN1))))
  );
}
