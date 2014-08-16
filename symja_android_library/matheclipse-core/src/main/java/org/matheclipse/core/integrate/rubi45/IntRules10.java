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
public class IntRules10 { 
  public static IAST RULES = List( 
ISetDelayed(Int(Power(Plus(Times(a_DEFAULT,Power(x_,q_DEFAULT)),Times(b_DEFAULT,Power(x_,pn_DEFAULT))),p_),x_Symbol),
    Condition(Int(Power(Times(Plus(a,b),Power(x,pn)),p),x),And(FreeQ(List(a,b,pn,p),x),ZeroQ(Plus(pn,Times(CN1,q)))))),
ISetDelayed(Int(Power(Plus(Times(a_DEFAULT,Power(x_,q_DEFAULT)),Times(b_DEFAULT,Power(x_,pn_DEFAULT))),p_),x_Symbol),
    Condition(Int(Times(Power(x,Times(p,q)),Power(Plus(a,Times(b,Power(x,Plus(pn,Times(CN1,q))))),p)),x),And(And(FreeQ(List(a,b,pn,q),x),IntegerQ(p)),PosQ(Plus(pn,Times(CN1,q)))))),
ISetDelayed(Int(Power(Plus(Times(a_DEFAULT,Power(x_,q_DEFAULT)),Times(b_DEFAULT,Power(x_,pn_DEFAULT))),p_),x_Symbol),
    Condition(Times(Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,pn))),Plus(p,C1)),Power(Times(b,Plus(pn,Times(CN1,q)),Plus(p,C1),Power(x,Plus(pn,Times(CN1,C1)))),CN1)),And(And(And(FreeQ(List(a,b,pn,p,q),x),Not(IntegerQ(p))),NonzeroQ(Plus(pn,Times(CN1,q)))),ZeroQ(Plus(Times(p,q),Times(CN1,pn),q,C1))))),
ISetDelayed(Int(Power(Plus(Times(a_DEFAULT,Power(x_,q_DEFAULT)),Times(b_DEFAULT,Power(x_,pn_DEFAULT))),p_),x_Symbol),
    Condition(Plus(Times(Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,pn))),Plus(p,C1)),Power(Times(b,Plus(Times(pn,p),C1),Power(x,Plus(pn,Times(CN1,C1)))),CN1)),Times(CN1,a,Plus(Times(p,q),Times(CN1,pn),q,C1),Power(Times(b,Plus(Times(pn,p),C1)),CN1),Int(Times(Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,pn))),p),Power(Power(x,Plus(pn,Times(CN1,q))),CN1)),x))),And(And(And(And(And(FreeQ(List(a,b,pn,q),x),Not(IntegerQ(p))),PosQ(Plus(pn,Times(CN1,q)))),RationalQ(p)),Greater(p,C0)),PositiveIntegerQ(Times(Plus(Times(p,q),Times(CN1,pn),q,C1),Power(Plus(pn,Times(CN1,q)),CN1)))))),
ISetDelayed(Int(Power(Plus(Times(a_DEFAULT,Power(x_,q_DEFAULT)),Times(b_DEFAULT,Power(x_,pn_DEFAULT))),p_),x_Symbol),
    Condition(Plus(Times(Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,pn))),Plus(p,C1)),Power(Times(a,Plus(Times(p,q),C1),Power(x,Plus(q,Times(CN1,C1)))),CN1)),Times(CN1,b,Plus(Times(pn,p),pn,Times(CN1,q),C1),Power(Times(a,Plus(Times(p,q),C1)),CN1),Int(Times(Power(x,Plus(pn,Times(CN1,q))),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,pn))),p)),x))),And(And(And(And(And(And(FreeQ(List(a,b,pn,q),x),Not(IntegerQ(p))),PosQ(Plus(pn,Times(CN1,q)))),RationalQ(p)),Greater(p,C0)),NonzeroQ(Plus(Times(p,q),C1))),NegativeIntegerQ(Plus(Times(pn,p),pn,Times(CN1,q),C1))))),
ISetDelayed(Int(Power(Plus(Times(a_DEFAULT,Power(x_,q_DEFAULT)),Times(b_DEFAULT,Power(x_,pn_DEFAULT))),p_),x_Symbol),
    Condition(Plus(Times(x,Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,pn))),p),Power(Plus(Times(p,q),C1),CN1)),Times(CN1,b,Plus(pn,Times(CN1,q)),p,Power(Plus(Times(p,q),C1),CN1),Int(Times(Power(x,pn),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,pn))),Plus(p,Times(CN1,C1)))),x))),And(And(And(And(And(FreeQ(List(a,b),x),Not(IntegerQ(p))),RationalQ(pn,p,q)),Less(q,pn)),Greater(p,C0)),Less(Plus(Times(p,q),C1),C0)))),
ISetDelayed(Int(Power(Plus(Times(a_DEFAULT,Power(x_,q_DEFAULT)),Times(b_DEFAULT,Power(x_,pn_DEFAULT))),p_),x_Symbol),
    Condition(Plus(Times(x,Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,pn))),p),Power(Plus(Times(pn,p),C1),CN1)),Times(a,Plus(pn,Times(CN1,q)),p,Power(Plus(Times(pn,p),C1),CN1),Int(Times(Power(x,q),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,pn))),Plus(p,Times(CN1,C1)))),x))),And(And(And(And(And(FreeQ(List(a,b,pn,q),x),Not(IntegerQ(p))),PosQ(Plus(pn,Times(CN1,q)))),RationalQ(p)),Greater(p,C0)),NonzeroQ(Plus(Times(pn,p),C1))))),
ISetDelayed(Int(Power(Plus(Times(a_DEFAULT,Power(x_,q_DEFAULT)),Times(b_DEFAULT,Power(x_,pn_DEFAULT))),p_),x_Symbol),
    Condition(Plus(Times(Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,pn))),Plus(p,C1)),Power(Times(b,Plus(pn,Times(CN1,q)),Plus(p,C1),Power(x,Plus(pn,Times(CN1,C1)))),CN1)),Times(CN1,Plus(Times(p,q),Times(CN1,pn),q,C1),Power(Times(b,Plus(pn,Times(CN1,q)),Plus(p,C1)),CN1),Int(Times(Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,pn))),Plus(p,C1)),Power(Power(x,pn),CN1)),x))),And(And(And(And(And(FreeQ(List(a,b),x),Not(IntegerQ(p))),RationalQ(pn,p,q)),Less(q,pn)),Less(p,CN1)),Greater(Plus(Times(p,q),C1),Plus(pn,Times(CN1,q)))))),
ISetDelayed(Int(Power(Plus(Times(a_DEFAULT,Power(x_,q_DEFAULT)),Times(b_DEFAULT,Power(x_,pn_DEFAULT))),p_),x_Symbol),
    Condition(Plus(Times(CN1,Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,pn))),Plus(p,C1)),Power(Times(a,Plus(pn,Times(CN1,q)),Plus(p,C1),Power(x,Plus(q,Times(CN1,C1)))),CN1)),Times(Plus(Times(pn,p),pn,Times(CN1,q),C1),Power(Times(a,Plus(pn,Times(CN1,q)),Plus(p,C1)),CN1),Int(Times(Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,pn))),Plus(p,C1)),Power(Power(x,q),CN1)),x))),And(And(And(And(FreeQ(List(a,b,pn,q),x),Not(IntegerQ(p))),PosQ(Plus(pn,Times(CN1,q)))),RationalQ(p)),Less(p,CN1)))),
ISetDelayed(Int(Power(Plus(Times(b_DEFAULT,x_),Times(a_DEFAULT,Sqr(x_))),CN1D2),x_Symbol),
    Condition(Times(CN1,Power(Times(b,Sqrt(Times(CN1,a,Power(b,CN2)))),CN1),ArcSin(Plus(C1,Times(C2,a,x,Power(b,CN1))))),And(FreeQ(List(a,b),x),PositiveQ(Times(CN1,a,Power(b,CN2)))))),
ISetDelayed(Int(Power(Plus(Times(a_DEFAULT,Sqr(x_)),Times(b_DEFAULT,Power(x_,pn_DEFAULT))),CN1D2),x_Symbol),
    Condition(Times(C2,Power(Plus(C2,Times(CN1,pn)),CN1),Subst(Int(Power(Plus(C1,Times(CN1,a,Sqr(x))),CN1),x),x,Times(x,Power(Plus(Times(a,Sqr(x)),Times(b,Power(x,pn))),CN1D2)))),And(FreeQ(List(a,b,pn),x),NonzeroQ(Plus(pn,Times(CN1,C2)))))),
ISetDelayed(Int(Power(Plus(Times(a_DEFAULT,x_),Times(b_DEFAULT,Power(x_,pn_))),CN1D2),x_Symbol),
    Condition(Times(Sqrt(x),Sqrt(Plus(a,Times(b,Power(x,Plus(pn,Times(CN1,C1)))))),Power(Plus(Times(a,x),Times(b,Power(x,pn))),CN1D2),Int(Power(Times(Sqrt(x),Sqrt(Plus(a,Times(b,Power(x,Plus(pn,Times(CN1,C1))))))),CN1),x)),And(FreeQ(List(a,b,pn),x),Or(ZeroQ(Plus(pn,Times(CN1,C3))),ZeroQ(Plus(pn,Times(CN1,C4))))))),
ISetDelayed(Int(Power(Plus(Times(a_DEFAULT,Power(x_,q_DEFAULT)),Times(b_DEFAULT,Power(x_,pn_DEFAULT))),p_),x_Symbol),
    Condition(Plus(Times(Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,pn))),Plus(p,C1)),Power(Times(b,Plus(Times(pn,p),C1),Power(x,Plus(pn,Times(CN1,C1)))),CN1)),Times(CN1,a,Plus(Times(p,q),Times(CN1,pn),q,C1),Power(Times(b,Plus(Times(pn,p),C1)),CN1),Int(Times(Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,pn))),p),Power(Power(x,Plus(pn,Times(CN1,q))),CN1)),x))),And(And(And(And(FreeQ(List(a,b),x),RationalQ(pn,p,q)),Less(q,pn)),Less(Less(CN1,p),C0)),Greater(Plus(Times(p,q),C1),Plus(pn,Times(CN1,q)))))),
ISetDelayed(Int(Power(Plus(Times(a_DEFAULT,Power(x_,q_DEFAULT)),Times(b_DEFAULT,Power(x_,pn_DEFAULT))),p_),x_Symbol),
    Condition(Plus(Times(Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,pn))),Plus(p,C1)),Power(Times(a,Plus(Times(p,q),C1),Power(x,Plus(q,Times(CN1,C1)))),CN1)),Times(CN1,b,Plus(Times(pn,p),pn,Times(CN1,q),C1),Power(Times(a,Plus(Times(p,q),C1)),CN1),Int(Times(Power(x,Plus(pn,Times(CN1,q))),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,pn))),p)),x))),And(And(And(And(FreeQ(List(a,b),x),RationalQ(pn,p,q)),Less(q,pn)),Less(Less(CN1,p),C0)),Less(Plus(Times(p,q),C1),C0)))),
ISetDelayed(Int(Power(Plus(Times(a_DEFAULT,Power(x_,q_DEFAULT)),Times(b_DEFAULT,Power(x_,pn_DEFAULT))),p_),x_Symbol),
    Condition(Times(Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,pn))),p),Power(Times(Power(x,Times(p,q)),Power(Plus(a,Times(b,Power(x,Plus(pn,Times(CN1,q))))),p)),CN1),Int(Times(Power(x,Times(p,q)),Power(Plus(a,Times(b,Power(x,Plus(pn,Times(CN1,q))))),p)),x)),And(And(And(FreeQ(List(a,b,pn,p,q),x),Not(IntegerQ(p))),NonzeroQ(Plus(pn,Times(CN1,q)))),PosQ(Plus(pn,Times(CN1,q)))))),
ISetDelayed(Int(Power(Plus(Times(a_DEFAULT,Power(u_,q_DEFAULT)),Times(b_DEFAULT,Power(v_,pn_DEFAULT))),p_),x_Symbol),
    Condition(Times(Power(Coefficient(u,x,C1),CN1),Subst(Int(Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,pn))),p),x),x,u)),And(And(And(FreeQ(List(a,b,pn,p,q),x),ZeroQ(Plus(u,Times(CN1,v)))),LinearQ(u,x)),NonzeroQ(Plus(u,Times(CN1,x)))))),
ISetDelayed(Int(Power(u_,p_),x_Symbol),
    Condition(Int(Power(ExpandToSum(u,x),p),x),And(And(FreeQ(p,x),GeneralizedBinomialQ(u,x)),Not(GeneralizedBinomialMatchQ(u,x)))))
  );
}
