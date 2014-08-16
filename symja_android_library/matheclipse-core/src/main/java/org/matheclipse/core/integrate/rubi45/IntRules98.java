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
public class IntRules98 { 
  public static IAST RULES = List( 
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Sech(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_DEFAULT)))),p_),Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_DEFAULT))))),x_Symbol),
    Condition(Plus(Times(CN1,Power(x,Plus(m,Times(CN1,pn),C1)),Power(Sech(Plus(a,Times(b,Power(x,pn)))),Plus(p,Times(CN1,C1))),Power(Times(b,pn,Plus(p,Times(CN1,C1))),CN1)),Times(Plus(m,Times(CN1,pn),C1),Power(Times(b,pn,Plus(p,Times(CN1,C1))),CN1),Int(Times(Power(x,Plus(m,Times(CN1,pn))),Power(Sech(Plus(a,Times(b,Power(x,pn)))),Plus(p,Times(CN1,C1)))),x))),And(And(And(And(FreeQ(List(a,b,p),x),RationalQ(m)),IntegerQ(pn)),GreaterEqual(Plus(m,Times(CN1,pn)),C0)),NonzeroQ(Plus(p,Times(CN1,C1)))))),
ISetDelayed(Int(Times(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_DEFAULT)))),Power(x_,m_DEFAULT),Power(Csch(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_DEFAULT)))),p_)),x_Symbol),
    Condition(Plus(Times(CN1,Power(x,Plus(m,Times(CN1,pn),C1)),Power(Csch(Plus(a,Times(b,Power(x,pn)))),Plus(p,Times(CN1,C1))),Power(Times(b,pn,Plus(p,Times(CN1,C1))),CN1)),Times(Plus(m,Times(CN1,pn),C1),Power(Times(b,pn,Plus(p,Times(CN1,C1))),CN1),Int(Times(Power(x,Plus(m,Times(CN1,pn))),Power(Csch(Plus(a,Times(b,Power(x,pn)))),Plus(p,Times(CN1,C1)))),x))),And(And(And(And(FreeQ(List(a,b,p),x),RationalQ(m)),IntegerQ(pn)),GreaterEqual(Plus(m,Times(CN1,pn)),C0)),NonzeroQ(Plus(p,Times(CN1,C1)))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Sech(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_DEFAULT)))),p_DEFAULT),Power(Tanh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_DEFAULT)))),q_DEFAULT)),x_Symbol),
    Condition(Plus(Times(CN1,Power(x,Plus(m,Times(CN1,pn),C1)),Power(Sech(Plus(a,Times(b,Power(x,pn)))),p),Power(Times(b,pn,p),CN1)),Times(Plus(m,Times(CN1,pn),C1),Power(Times(b,pn,p),CN1),Int(Times(Power(x,Plus(m,Times(CN1,pn))),Power(Sech(Plus(a,Times(b,Power(x,pn)))),p)),x))),And(And(And(And(FreeQ(List(a,b,p),x),RationalQ(m)),IntegerQ(pn)),GreaterEqual(Plus(m,Times(CN1,pn)),C0)),SameQ(q,C1)))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Coth(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_DEFAULT)))),q_DEFAULT),Power(Csch(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_DEFAULT)))),p_DEFAULT)),x_Symbol),
    Condition(Plus(Times(CN1,Power(x,Plus(m,Times(CN1,pn),C1)),Power(Csch(Plus(a,Times(b,Power(x,pn)))),p),Power(Times(b,pn,p),CN1)),Times(Plus(m,Times(CN1,pn),C1),Power(Times(b,pn,p),CN1),Int(Times(Power(x,Plus(m,Times(CN1,pn))),Power(Csch(Plus(a,Times(b,Power(x,pn)))),p)),x))),And(And(And(And(FreeQ(List(a,b,p),x),RationalQ(m)),IntegerQ(pn)),GreaterEqual(Plus(m,Times(CN1,pn)),C0)),SameQ(q,C1)))),
ISetDelayed(Int(Power($(pf_,v_),p_DEFAULT),x_Symbol),
    Condition(Int(Power($(pf,ExpandToSum(v,x)),p),x),And(And(And(FreeQ(p,x),HyperbolicQ(pf)),BinomialQ(v,x)),Not(BinomialMatchQ(v,x))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power($(pf_,v_),p_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(x,m),Power($(pf,ExpandToSum(v,x)),p)),x),And(And(And(FreeQ(List(m,p),x),HyperbolicQ(pf)),BinomialQ(v,x)),Not(BinomialMatchQ(v,x))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Power(Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_)))),p_DEFAULT)),q_),x_Symbol),
    Condition(Times(Power(c,Plus(q,Times(CN1,C1D2))),Sqrt(Times(c,Power(Sinh(Plus(a,Times(b,Power(x,pn)))),p))),Power(Power(Sinh(Plus(a,Times(b,Power(x,pn)))),Times(C1D2,p)),CN1),Int(Power(Sinh(Plus(a,Times(b,Power(x,pn)))),Times(p,q)),x)),And(And(FreeQ(List(a,b,c,pn,p,q),x),PositiveIntegerQ(Plus(q,C1D2))),Not(OneQ(c,p))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Power(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_)))),p_DEFAULT)),q_),x_Symbol),
    Condition(Times(Power(c,Plus(q,Times(CN1,C1D2))),Sqrt(Times(c,Power(Cosh(Plus(a,Times(b,Power(x,pn)))),p))),Power(Power(Cosh(Plus(a,Times(b,Power(x,pn)))),Times(C1D2,p)),CN1),Int(Power(Cosh(Plus(a,Times(b,Power(x,pn)))),Times(p,q)),x)),And(And(FreeQ(List(a,b,c,pn,p,q),x),PositiveIntegerQ(Plus(q,C1D2))),Not(OneQ(c,p))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Power(Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_)))),p_DEFAULT)),q_),x_Symbol),
    Condition(Times(Power(c,Plus(q,C1D2)),Power(Sinh(Plus(a,Times(b,Power(x,pn)))),Times(C1D2,p)),Power(Times(c,Power(Sinh(Plus(a,Times(b,Power(x,pn)))),p)),CN1D2),Int(Power(Sinh(Plus(a,Times(b,Power(x,pn)))),Times(p,q)),x)),And(And(FreeQ(List(a,b,c,pn,p,q),x),NegativeIntegerQ(Plus(q,Times(CN1,C1D2)))),Not(OneQ(c,p))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Power(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_)))),p_DEFAULT)),q_),x_Symbol),
    Condition(Times(Power(c,Plus(q,C1D2)),Power(Cosh(Plus(a,Times(b,Power(x,pn)))),Times(C1D2,p)),Power(Times(c,Power(Cosh(Plus(a,Times(b,Power(x,pn)))),p)),CN1D2),Int(Power(Cosh(Plus(a,Times(b,Power(x,pn)))),Times(p,q)),x)),And(And(FreeQ(List(a,b,c,pn,p,q),x),NegativeIntegerQ(Plus(q,Times(CN1,C1D2)))),Not(OneQ(c,p))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Power(Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_)))),p_DEFAULT)),q_),x_Symbol),
    Condition(Times(Power(Times(c,Power(Sinh(Plus(a,Times(b,Power(x,pn)))),p)),q),Power(Power(Sinh(Plus(a,Times(b,Power(x,pn)))),Times(p,q)),CN1),Int(Power(Sinh(Plus(a,Times(b,Power(x,pn)))),Times(p,q)),x)),And(And(FreeQ(List(a,b,c,pn,p,q),x),Not(IntegerQ(Plus(q,C1D2)))),Not(OneQ(c,p))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Power(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_)))),p_DEFAULT)),q_),x_Symbol),
    Condition(Times(Power(Times(c,Power(Cosh(Plus(a,Times(b,Power(x,pn)))),p)),q),Power(Power(Cosh(Plus(a,Times(b,Power(x,pn)))),Times(p,q)),CN1),Int(Power(Cosh(Plus(a,Times(b,Power(x,pn)))),Times(p,q)),x)),And(And(FreeQ(List(a,b,c,pn,p,q),x),Not(IntegerQ(Plus(q,C1D2)))),Not(OneQ(c,p))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,Power(Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_DEFAULT)))),p_DEFAULT)),q_)),x_Symbol),
    Condition(Times(Power(c,Plus(q,Times(CN1,C1D2))),Sqrt(Times(c,Power(Sinh(Plus(a,Times(b,Power(x,pn)))),p))),Power(Power(Sinh(Plus(a,Times(b,Power(x,pn)))),Times(C1D2,p)),CN1),Int(Times(Power(x,m),Power(Sinh(Plus(a,Times(b,Power(x,pn)))),Times(p,q))),x)),And(And(FreeQ(List(a,b,c,m,pn,p,q),x),PositiveIntegerQ(Plus(q,C1D2))),Not(OneQ(c,p))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,Power(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_DEFAULT)))),p_DEFAULT)),q_)),x_Symbol),
    Condition(Times(Power(c,Plus(q,Times(CN1,C1D2))),Sqrt(Times(c,Power(Cosh(Plus(a,Times(b,Power(x,pn)))),p))),Power(Power(Cosh(Plus(a,Times(b,Power(x,pn)))),Times(C1D2,p)),CN1),Int(Times(Power(x,m),Power(Cosh(Plus(a,Times(b,Power(x,pn)))),Times(p,q))),x)),And(And(FreeQ(List(a,b,c,m,pn,p,q),x),PositiveIntegerQ(Plus(q,C1D2))),Not(OneQ(c,p))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,Power(Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_DEFAULT)))),p_DEFAULT)),q_)),x_Symbol),
    Condition(Times(Power(c,Plus(q,C1D2)),Power(Sinh(Plus(a,Times(b,Power(x,pn)))),Times(C1D2,p)),Power(Times(c,Power(Sinh(Plus(a,Times(b,Power(x,pn)))),p)),CN1D2),Int(Times(Power(x,m),Power(Sinh(Plus(a,Times(b,Power(x,pn)))),Times(p,q))),x)),And(And(FreeQ(List(a,b,c,m,pn,p,q),x),NegativeIntegerQ(Plus(q,Times(CN1,C1D2)))),Not(OneQ(c,p))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,Power(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_DEFAULT)))),p_DEFAULT)),q_)),x_Symbol),
    Condition(Times(Power(c,Plus(q,C1D2)),Power(Cosh(Plus(a,Times(b,Power(x,pn)))),Times(C1D2,p)),Power(Times(c,Power(Cosh(Plus(a,Times(b,Power(x,pn)))),p)),CN1D2),Int(Times(Power(x,m),Power(Cosh(Plus(a,Times(b,Power(x,pn)))),Times(p,q))),x)),And(And(FreeQ(List(a,b,c,m,pn,p,q),x),NegativeIntegerQ(Plus(q,Times(CN1,C1D2)))),Not(OneQ(c,p))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,Power(Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_DEFAULT)))),p_DEFAULT)),q_)),x_Symbol),
    Condition(Times(Power(Times(c,Power(Sinh(Plus(a,Times(b,Power(x,pn)))),p)),q),Power(Power(Sinh(Plus(a,Times(b,Power(x,pn)))),Times(p,q)),CN1),Int(Times(Power(x,m),Power(Sinh(Plus(a,Times(b,Power(x,pn)))),Times(p,q))),x)),And(And(FreeQ(List(a,b,c,m,pn,p,q),x),Not(IntegerQ(Plus(q,C1D2)))),Not(OneQ(c,p))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,Power(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_DEFAULT)))),p_DEFAULT)),q_)),x_Symbol),
    Condition(Times(Power(Times(c,Power(Cosh(Plus(a,Times(b,Power(x,pn)))),p)),q),Power(Power(Cosh(Plus(a,Times(b,Power(x,pn)))),Times(p,q)),CN1),Int(Times(Power(x,m),Power(Cosh(Plus(a,Times(b,Power(x,pn)))),Times(p,q))),x)),And(And(FreeQ(List(a,b,c,m,pn,p,q),x),Not(IntegerQ(Plus(q,C1D2)))),Not(OneQ(c,p))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Power(Sech(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_)))),p_DEFAULT)),q_),x_Symbol),
    Condition(Times(Power(c,Plus(q,Times(CN1,C1D2))),Sqrt(Times(c,Power(Sech(Plus(a,Times(b,Power(x,pn)))),p))),Power(Power(Sech(Plus(a,Times(b,Power(x,pn)))),Times(C1D2,p)),CN1),Int(Power(Sech(Plus(a,Times(b,Power(x,pn)))),Times(p,q)),x)),And(And(FreeQ(List(a,b,c,pn,p,q),x),PositiveIntegerQ(Plus(q,C1D2))),Not(OneQ(c,p))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Power(Csch(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_)))),p_DEFAULT)),q_),x_Symbol),
    Condition(Times(Power(c,Plus(q,Times(CN1,C1D2))),Sqrt(Times(c,Power(Csch(Plus(a,Times(b,Power(x,pn)))),p))),Power(Power(Csch(Plus(a,Times(b,Power(x,pn)))),Times(C1D2,p)),CN1),Int(Power(Csch(Plus(a,Times(b,Power(x,pn)))),Times(p,q)),x)),And(And(FreeQ(List(a,b,c,pn,p,q),x),PositiveIntegerQ(Plus(q,C1D2))),Not(OneQ(c,p))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Power(Sech(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_)))),p_DEFAULT)),q_),x_Symbol),
    Condition(Times(Power(c,Plus(q,C1D2)),Power(Sech(Plus(a,Times(b,Power(x,pn)))),Times(C1D2,p)),Power(Times(c,Power(Sech(Plus(a,Times(b,Power(x,pn)))),p)),CN1D2),Int(Power(Sech(Plus(a,Times(b,Power(x,pn)))),Times(p,q)),x)),And(And(FreeQ(List(a,b,c,pn,p,q),x),NegativeIntegerQ(Plus(q,Times(CN1,C1D2)))),Not(OneQ(c,p))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Power(Csch(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_)))),p_DEFAULT)),q_),x_Symbol),
    Condition(Times(Power(c,Plus(q,C1D2)),Power(Csch(Plus(a,Times(b,Power(x,pn)))),Times(C1D2,p)),Power(Times(c,Power(Csch(Plus(a,Times(b,Power(x,pn)))),p)),CN1D2),Int(Power(Csch(Plus(a,Times(b,Power(x,pn)))),Times(p,q)),x)),And(And(FreeQ(List(a,b,c,pn,p,q),x),NegativeIntegerQ(Plus(q,Times(CN1,C1D2)))),Not(OneQ(c,p))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Power(Sech(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_)))),p_DEFAULT)),q_),x_Symbol),
    Condition(Times(Power(Times(c,Power(Sech(Plus(a,Times(b,Power(x,pn)))),p)),q),Power(Power(Sech(Plus(a,Times(b,Power(x,pn)))),Times(p,q)),CN1),Int(Power(Sech(Plus(a,Times(b,Power(x,pn)))),Times(p,q)),x)),And(And(FreeQ(List(a,b,c,pn,p,q),x),Not(IntegerQ(Plus(q,C1D2)))),Not(OneQ(c,p))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Power(Csch(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_)))),p_DEFAULT)),q_),x_Symbol),
    Condition(Times(Power(Times(c,Power(Csch(Plus(a,Times(b,Power(x,pn)))),p)),q),Power(Power(Csch(Plus(a,Times(b,Power(x,pn)))),Times(p,q)),CN1),Int(Power(Csch(Plus(a,Times(b,Power(x,pn)))),Times(p,q)),x)),And(And(FreeQ(List(a,b,c,pn,p,q),x),Not(IntegerQ(Plus(q,C1D2)))),Not(OneQ(c,p))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,Power(Sech(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_DEFAULT)))),p_DEFAULT)),q_)),x_Symbol),
    Condition(Times(Power(c,Plus(q,Times(CN1,C1D2))),Sqrt(Times(c,Power(Sech(Plus(a,Times(b,Power(x,pn)))),p))),Power(Power(Sech(Plus(a,Times(b,Power(x,pn)))),Times(C1D2,p)),CN1),Int(Times(Power(x,m),Power(Sech(Plus(a,Times(b,Power(x,pn)))),Times(p,q))),x)),And(And(FreeQ(List(a,b,c,m,pn,p,q),x),PositiveIntegerQ(Plus(q,C1D2))),Not(OneQ(c,p))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,Power(Csch(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_DEFAULT)))),p_DEFAULT)),q_)),x_Symbol),
    Condition(Times(Power(c,Plus(q,Times(CN1,C1D2))),Sqrt(Times(c,Power(Csch(Plus(a,Times(b,Power(x,pn)))),p))),Power(Power(Csch(Plus(a,Times(b,Power(x,pn)))),Times(C1D2,p)),CN1),Int(Times(Power(x,m),Power(Csch(Plus(a,Times(b,Power(x,pn)))),Times(p,q))),x)),And(And(FreeQ(List(a,b,c,m,pn,p,q),x),PositiveIntegerQ(Plus(q,C1D2))),Not(OneQ(c,p))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,Power(Sech(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_DEFAULT)))),p_DEFAULT)),q_)),x_Symbol),
    Condition(Times(Power(c,Plus(q,C1D2)),Power(Sech(Plus(a,Times(b,Power(x,pn)))),Times(C1D2,p)),Power(Times(c,Power(Sech(Plus(a,Times(b,Power(x,pn)))),p)),CN1D2),Int(Times(Power(x,m),Power(Sech(Plus(a,Times(b,Power(x,pn)))),Times(p,q))),x)),And(And(FreeQ(List(a,b,c,m,pn,p,q),x),NegativeIntegerQ(Plus(q,C1D2))),Not(OneQ(c,p))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,Power(Csch(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_DEFAULT)))),p_DEFAULT)),q_)),x_Symbol),
    Condition(Times(Power(c,Plus(q,C1D2)),Power(Csch(Plus(a,Times(b,Power(x,pn)))),Times(C1D2,p)),Power(Times(c,Power(Csch(Plus(a,Times(b,Power(x,pn)))),p)),CN1D2),Int(Times(Power(x,m),Power(Csch(Plus(a,Times(b,Power(x,pn)))),Times(p,q))),x)),And(And(FreeQ(List(a,b,c,m,pn,p,q),x),NegativeIntegerQ(Plus(q,C1D2))),Not(OneQ(c,p))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,Power(Sech(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_DEFAULT)))),p_DEFAULT)),q_)),x_Symbol),
    Condition(Times(Power(Times(c,Power(Sech(Plus(a,Times(b,Power(x,pn)))),p)),q),Power(Power(Sech(Plus(a,Times(b,Power(x,pn)))),Times(p,q)),CN1),Int(Times(Power(x,m),Power(Sech(Plus(a,Times(b,Power(x,pn)))),Times(p,q))),x)),And(And(FreeQ(List(a,b,c,m,pn,p,q),x),Not(IntegerQ(Plus(q,C1D2)))),Not(OneQ(c,p))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,Power(Csch(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_DEFAULT)))),p_DEFAULT)),q_)),x_Symbol),
    Condition(Times(Power(Times(c,Power(Csch(Plus(a,Times(b,Power(x,pn)))),p)),q),Power(Power(Csch(Plus(a,Times(b,Power(x,pn)))),Times(p,q)),CN1),Int(Times(Power(x,m),Power(Csch(Plus(a,Times(b,Power(x,pn)))),Times(p,q))),x)),And(And(FreeQ(List(a,b,c,m,pn,p,q),x),Not(IntegerQ(Plus(q,C1D2)))),Not(OneQ(c,p))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Power($(pf_,v_),p_DEFAULT)),q_),x_Symbol),
    Condition(Int(Power(Times(c,Power($(pf,ExpandToSum(v,x)),p)),q),x),And(And(And(FreeQ(List(c,p,q),x),HyperbolicQ(pf)),BinomialQ(v,x)),Not(BinomialMatchQ(v,x))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,Power($(pf_,v_),p_DEFAULT)),q_)),x_Symbol),
    Condition(Int(Times(Power(x,m),Power(Times(c,Power($(pf,ExpandToSum(v,x)),p)),q)),x),And(And(And(FreeQ(List(c,m,p,q),x),HyperbolicQ(pf)),BinomialQ(v,x)),Not(BinomialMatchQ(v,x)))))
  );
}
