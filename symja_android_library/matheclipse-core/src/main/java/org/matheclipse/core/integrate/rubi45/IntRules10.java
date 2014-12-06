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
ISetDelayed(Int(Power(Plus(Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(a_DEFAULT,Power(x_,q_DEFAULT))),p_),x_Symbol),
    Condition(Int(Power(Times(Plus(a,b),Power(x,n)),p),x),And(FreeQ(List(a,b,n,p),x),ZeroQ(Plus(n,Times(CN1,q)))))),
ISetDelayed(Int(Power(Plus(Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(a_DEFAULT,Power(x_,q_DEFAULT))),p_),x_Symbol),
    Condition(Int(Times(Power(x,Times(p,q)),Power(Plus(a,Times(b,Power(x,Plus(n,Times(CN1,q))))),p)),x),And(And(FreeQ(List(a,b,n,q),x),IntegerQ(p)),PosQ(Plus(n,Times(CN1,q)))))),
ISetDelayed(Int(Power(Plus(Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(a_DEFAULT,Power(x_,q_DEFAULT))),p_),x_Symbol),
    Condition(Times(Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n))),Plus(p,C1)),Power(Times(b,Plus(n,Times(CN1,q)),Plus(p,C1),Power(x,Plus(n,Times(CN1,C1)))),CN1)),And(And(And(FreeQ(List(a,b,n,p,q),x),Not(IntegerQ(p))),NonzeroQ(Plus(n,Times(CN1,q)))),ZeroQ(Plus(Times(p,q),Times(CN1,n),q,C1))))),
ISetDelayed(Int(Power(Plus(Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(a_DEFAULT,Power(x_,q_DEFAULT))),p_),x_Symbol),
    Condition(Plus(Times(Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n))),Plus(p,C1)),Power(Times(b,Plus(Times(n,p),C1),Power(x,Plus(n,Times(CN1,C1)))),CN1)),Times(CN1,a,Plus(Times(p,q),Times(CN1,n),q,C1),Power(Times(b,Plus(Times(n,p),C1)),CN1),Int(Times(Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n))),p),Power(Power(x,Plus(n,Times(CN1,q))),CN1)),x))),And(And(And(And(And(FreeQ(List(a,b,n,q),x),Not(IntegerQ(p))),PosQ(Plus(n,Times(CN1,q)))),RationalQ(p)),Greater(p,C0)),PositiveIntegerQ(Times(Plus(Times(p,q),Times(CN1,n),q,C1),Power(Plus(n,Times(CN1,q)),CN1)))))),
ISetDelayed(Int(Power(Plus(Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(a_DEFAULT,Power(x_,q_DEFAULT))),p_),x_Symbol),
    Condition(Plus(Times(Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n))),Plus(p,C1)),Power(Times(a,Plus(Times(p,q),C1),Power(x,Plus(q,Times(CN1,C1)))),CN1)),Times(CN1,b,Plus(Times(n,p),n,Times(CN1,q),C1),Power(Times(a,Plus(Times(p,q),C1)),CN1),Int(Times(Power(x,Plus(n,Times(CN1,q))),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n))),p)),x))),And(And(And(And(And(And(FreeQ(List(a,b,n,q),x),Not(IntegerQ(p))),PosQ(Plus(n,Times(CN1,q)))),RationalQ(p)),Greater(p,C0)),NonzeroQ(Plus(Times(p,q),C1))),NegativeIntegerQ(Plus(Times(n,p),n,Times(CN1,q),C1))))),
ISetDelayed(Int(Power(Plus(Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(a_DEFAULT,Power(x_,q_DEFAULT))),p_),x_Symbol),
    Condition(Plus(Times(x,Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n))),p),Power(Plus(Times(p,q),C1),CN1)),Times(CN1,b,Plus(n,Times(CN1,q)),p,Power(Plus(Times(p,q),C1),CN1),Int(Times(Power(x,n),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n))),Plus(p,Times(CN1,C1)))),x))),And(And(And(And(And(FreeQ(List(a,b),x),Not(IntegerQ(p))),RationalQ(n,p,q)),Less(q,n)),Greater(p,C0)),Less(Plus(Times(p,q),C1),C0)))),
ISetDelayed(Int(Power(Plus(Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(a_DEFAULT,Power(x_,q_DEFAULT))),p_),x_Symbol),
    Condition(Plus(Times(x,Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n))),p),Power(Plus(Times(n,p),C1),CN1)),Times(a,Plus(n,Times(CN1,q)),p,Power(Plus(Times(n,p),C1),CN1),Int(Times(Power(x,q),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n))),Plus(p,Times(CN1,C1)))),x))),And(And(And(And(And(FreeQ(List(a,b,n,q),x),Not(IntegerQ(p))),PosQ(Plus(n,Times(CN1,q)))),RationalQ(p)),Greater(p,C0)),NonzeroQ(Plus(Times(n,p),C1))))),
ISetDelayed(Int(Power(Plus(Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(a_DEFAULT,Power(x_,q_DEFAULT))),p_),x_Symbol),
    Condition(Plus(Times(Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n))),Plus(p,C1)),Power(Times(b,Plus(n,Times(CN1,q)),Plus(p,C1),Power(x,Plus(n,Times(CN1,C1)))),CN1)),Times(CN1,Plus(Times(p,q),Times(CN1,n),q,C1),Power(Times(b,Plus(n,Times(CN1,q)),Plus(p,C1)),CN1),Int(Times(Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n))),Plus(p,C1)),Power(Power(x,n),CN1)),x))),And(And(And(And(And(FreeQ(List(a,b),x),Not(IntegerQ(p))),RationalQ(n,p,q)),Less(q,n)),Less(p,CN1)),Greater(Plus(Times(p,q),C1),Plus(n,Times(CN1,q)))))),
ISetDelayed(Int(Power(Plus(Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(a_DEFAULT,Power(x_,q_DEFAULT))),p_),x_Symbol),
    Condition(Plus(Times(CN1,Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n))),Plus(p,C1)),Power(Times(a,Plus(n,Times(CN1,q)),Plus(p,C1),Power(x,Plus(q,Times(CN1,C1)))),CN1)),Times(Plus(Times(n,p),n,Times(CN1,q),C1),Power(Times(a,Plus(n,Times(CN1,q)),Plus(p,C1)),CN1),Int(Times(Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n))),Plus(p,C1)),Power(Power(x,q),CN1)),x))),And(And(And(And(FreeQ(List(a,b,n,q),x),Not(IntegerQ(p))),PosQ(Plus(n,Times(CN1,q)))),RationalQ(p)),Less(p,CN1)))),
ISetDelayed(Int(Power(Plus(Times(b_DEFAULT,x_),Times(a_DEFAULT,Sqr(x_))),CN1D2),x_Symbol),
    Condition(Times(CN1,Power(Times(b,Sqrt(Times(CN1,a,Power(b,CN2)))),CN1),ArcSin(Plus(C1,Times(C2,a,x,Power(b,CN1))))),And(FreeQ(List(a,b),x),PositiveQ(Times(CN1,a,Power(b,CN2)))))),
ISetDelayed(Int(Power(Plus(Times(a_DEFAULT,Sqr(x_)),Times(b_DEFAULT,Power(x_,n_DEFAULT))),CN1D2),x_Symbol),
    Condition(Times(C2,Power(Plus(C2,Times(CN1,n)),CN1),Subst(Int(Power(Plus(C1,Times(CN1,a,Sqr(x))),CN1),x),x,Times(x,Power(Plus(Times(a,Sqr(x)),Times(b,Power(x,n))),CN1D2)))),And(FreeQ(List(a,b,n),x),NonzeroQ(Plus(n,Times(CN1,C2)))))),
ISetDelayed(Int(Power(Plus(Times(a_DEFAULT,x_),Times(b_DEFAULT,Power(x_,n_))),CN1D2),x_Symbol),
    Condition(Times(Sqrt(x),Sqrt(Plus(a,Times(b,Power(x,Plus(n,Times(CN1,C1)))))),Power(Plus(Times(a,x),Times(b,Power(x,n))),CN1D2),Int(Power(Times(Sqrt(x),Sqrt(Plus(a,Times(b,Power(x,Plus(n,Times(CN1,C1))))))),CN1),x)),And(FreeQ(List(a,b,n),x),Or(ZeroQ(Plus(n,Times(CN1,C3))),ZeroQ(Plus(n,Times(CN1,C4))))))),
ISetDelayed(Int(Power(Plus(Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(a_DEFAULT,Power(x_,q_DEFAULT))),p_),x_Symbol),
    Condition(Plus(Times(Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n))),Plus(p,C1)),Power(Times(b,Plus(Times(n,p),C1),Power(x,Plus(n,Times(CN1,C1)))),CN1)),Times(CN1,a,Plus(Times(p,q),Times(CN1,n),q,C1),Power(Times(b,Plus(Times(n,p),C1)),CN1),Int(Times(Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n))),p),Power(Power(x,Plus(n,Times(CN1,q))),CN1)),x))),And(And(And(And(FreeQ(List(a,b),x),RationalQ(n,p,q)),Less(q,n)),Less(Less(CN1,p),C0)),Greater(Plus(Times(p,q),C1),Plus(n,Times(CN1,q)))))),
ISetDelayed(Int(Power(Plus(Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(a_DEFAULT,Power(x_,q_DEFAULT))),p_),x_Symbol),
    Condition(Plus(Times(Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n))),Plus(p,C1)),Power(Times(a,Plus(Times(p,q),C1),Power(x,Plus(q,Times(CN1,C1)))),CN1)),Times(CN1,b,Plus(Times(n,p),n,Times(CN1,q),C1),Power(Times(a,Plus(Times(p,q),C1)),CN1),Int(Times(Power(x,Plus(n,Times(CN1,q))),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n))),p)),x))),And(And(And(And(FreeQ(List(a,b),x),RationalQ(n,p,q)),Less(q,n)),Less(Less(CN1,p),C0)),Less(Plus(Times(p,q),C1),C0)))),
ISetDelayed(Int(Power(Plus(Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(a_DEFAULT,Power(x_,q_DEFAULT))),p_),x_Symbol),
    Condition(Times(Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n))),p),Power(Times(Power(x,Times(p,q)),Power(Plus(a,Times(b,Power(x,Plus(n,Times(CN1,q))))),p)),CN1),Int(Times(Power(x,Times(p,q)),Power(Plus(a,Times(b,Power(x,Plus(n,Times(CN1,q))))),p)),x)),And(And(And(FreeQ(List(a,b,n,p,q),x),Not(IntegerQ(p))),NonzeroQ(Plus(n,Times(CN1,q)))),PosQ(Plus(n,Times(CN1,q)))))),
ISetDelayed(Int(Power(Plus(Times(a_DEFAULT,Power(u_,q_DEFAULT)),Times(b_DEFAULT,Power(v_,n_DEFAULT))),p_),x_Symbol),
    Condition(Times(Power(Coefficient(u,x,C1),CN1),Subst(Int(Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n))),p),x),x,u)),And(And(And(FreeQ(List(a,b,n,p,q),x),ZeroQ(Plus(u,Times(CN1,v)))),LinearQ(u,x)),NonzeroQ(Plus(u,Times(CN1,x)))))),
ISetDelayed(Int(Power(u_,p_),x_Symbol),
    Condition(Int(Power(ExpandToSum(u,x),p),x),And(And(FreeQ(p,x),GeneralizedBinomialQ(u,x)),Not(GeneralizedBinomialMatchQ(u,x)))))
  );
}
