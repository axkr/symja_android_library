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
public class IntRules91 { 
  public static IAST RULES = List( 
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Tan(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_)))),p_)),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,Times(CN1,pn),C1)),Power(Tan(Plus(a,Times(b,Power(x,pn)))),Plus(p,C1)),Power(Times(b,pn,Plus(p,C1)),CN1)),Times(CN1,Plus(m,Times(CN1,pn),C1),Power(Times(b,pn,Plus(p,C1)),CN1),Int(Times(Power(x,Plus(m,Times(CN1,pn))),Power(Tan(Plus(a,Times(b,Power(x,pn)))),Plus(p,C1))),x)),Times(CN1,Int(Times(Power(x,m),Power(Tan(Plus(a,Times(b,Power(x,pn)))),Plus(p,C2))),x))),And(And(And(FreeQ(List(a,b),x),RationalQ(m,pn,p)),Less(p,CN1)),Less(Less(C0,pn),Plus(m,C1))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Cot(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_)))),p_)),x_Symbol),
    Condition(Plus(Times(CN1,Power(x,Plus(m,Times(CN1,pn),C1)),Power(Cot(Plus(a,Times(b,Power(x,pn)))),Plus(p,C1)),Power(Times(b,pn,Plus(p,C1)),CN1)),Times(Plus(m,Times(CN1,pn),C1),Power(Times(b,pn,Plus(p,C1)),CN1),Int(Times(Power(x,Plus(m,Times(CN1,pn))),Power(Cot(Plus(a,Times(b,Power(x,pn)))),Plus(p,C1))),x)),Times(CN1,Int(Times(Power(x,m),Power(Cot(Plus(a,Times(b,Power(x,pn)))),Plus(p,C2))),x))),And(And(And(FreeQ(List(a,b),x),RationalQ(m,pn,p)),Less(p,CN1)),Less(Less(C0,pn),Plus(m,C1))))),
ISetDelayed(Int(Sec(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_)))),x_Symbol),
    Condition(Times(Power(pn,CN1),Subst(Int(Times(Power(x,Plus(Power(pn,CN1),Times(CN1,C1))),Sec(Plus(a,Times(b,x)))),x),x,Power(x,pn))),And(FreeQ(List(a,b),x),PositiveIntegerQ(Power(pn,CN1))))),
ISetDelayed(Int(Csc(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_)))),x_Symbol),
    Condition(Times(Power(pn,CN1),Subst(Int(Times(Power(x,Plus(Power(pn,CN1),Times(CN1,C1))),Csc(Plus(a,Times(b,x)))),x),x,Power(x,pn))),And(FreeQ(List(a,b),x),PositiveIntegerQ(Power(pn,CN1))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Sec(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_)))),p_DEFAULT)),x_Symbol),
    Condition(Times(Power(pn,CN1),Subst(Int(Times(Power(x,Plus(Simplify(Times(Plus(m,C1),Power(pn,CN1))),Times(CN1,C1))),Power(Sec(Plus(a,Times(b,x))),p)),x),x,Power(x,pn))),And(FreeQ(List(a,b,m,pn,p),x),PositiveIntegerQ(Simplify(Times(Plus(m,C1),Power(pn,CN1))))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Csc(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_)))),p_DEFAULT)),x_Symbol),
    Condition(Times(Power(pn,CN1),Subst(Int(Times(Power(x,Plus(Simplify(Times(Plus(m,C1),Power(pn,CN1))),Times(CN1,C1))),Power(Csc(Plus(a,Times(b,x))),p)),x),x,Power(x,pn))),And(FreeQ(List(a,b,m,pn,p),x),PositiveIntegerQ(Simplify(Times(Plus(m,C1),Power(pn,CN1))))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Sec(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_)))),p_DEFAULT)),x_Symbol),
    Condition($(Defer($s("Int")),Times(Power(x,m),Power(Sec(Plus(a,Times(b,Power(x,pn)))),p)),x),FreeQ(List(a,b,m,pn,p),x))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Csc(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_)))),p_DEFAULT)),x_Symbol),
    Condition($(Defer($s("Int")),Times(Power(x,m),Power(Csc(Plus(a,Times(b,Power(x,pn)))),p)),x),FreeQ(List(a,b,m,pn,p),x))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Sec(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_DEFAULT)))),p_),Sin(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_DEFAULT))))),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,Times(CN1,pn),C1)),Power(Sec(Plus(a,Times(b,Power(x,pn)))),Plus(p,Times(CN1,C1))),Power(Times(b,pn,Plus(p,Times(CN1,C1))),CN1)),Times(CN1,Plus(m,Times(CN1,pn),C1),Power(Times(b,pn,Plus(p,Times(CN1,C1))),CN1),Int(Times(Power(x,Plus(m,Times(CN1,pn))),Power(Sec(Plus(a,Times(b,Power(x,pn)))),Plus(p,Times(CN1,C1)))),x))),And(And(And(And(FreeQ(List(a,b,p),x),RationalQ(m)),IntegerQ(pn)),GreaterEqual(Plus(m,Times(CN1,pn)),C0)),NonzeroQ(Plus(p,Times(CN1,C1)))))),
ISetDelayed(Int(Times(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_DEFAULT)))),Power(x_,m_DEFAULT),Power(Csc(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_DEFAULT)))),p_)),x_Symbol),
    Condition(Plus(Times(CN1,Power(x,Plus(m,Times(CN1,pn),C1)),Power(Csc(Plus(a,Times(b,Power(x,pn)))),Plus(p,Times(CN1,C1))),Power(Times(b,pn,Plus(p,Times(CN1,C1))),CN1)),Times(Plus(m,Times(CN1,pn),C1),Power(Times(b,pn,Plus(p,Times(CN1,C1))),CN1),Int(Times(Power(x,Plus(m,Times(CN1,pn))),Power(Csc(Plus(a,Times(b,Power(x,pn)))),Plus(p,Times(CN1,C1)))),x))),And(And(And(And(FreeQ(List(a,b,p),x),RationalQ(m)),IntegerQ(pn)),GreaterEqual(Plus(m,Times(CN1,pn)),C0)),NonzeroQ(Plus(p,Times(CN1,C1)))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Sec(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_DEFAULT)))),p_DEFAULT),Power(Tan(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_DEFAULT)))),q_DEFAULT)),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,Times(CN1,pn),C1)),Power(Sec(Plus(a,Times(b,Power(x,pn)))),p),Power(Times(b,pn,p),CN1)),Times(CN1,Plus(m,Times(CN1,pn),C1),Power(Times(b,pn,p),CN1),Int(Times(Power(x,Plus(m,Times(CN1,pn))),Power(Sec(Plus(a,Times(b,Power(x,pn)))),p)),x))),And(And(And(And(FreeQ(List(a,b,p),x),RationalQ(m)),IntegerQ(pn)),GreaterEqual(Plus(m,Times(CN1,pn)),C0)),SameQ(q,C1)))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Cot(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_DEFAULT)))),q_DEFAULT),Power(Csc(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_DEFAULT)))),p_DEFAULT)),x_Symbol),
    Condition(Plus(Times(CN1,Power(x,Plus(m,Times(CN1,pn),C1)),Power(Csc(Plus(a,Times(b,Power(x,pn)))),p),Power(Times(b,pn,p),CN1)),Times(Plus(m,Times(CN1,pn),C1),Power(Times(b,pn,p),CN1),Int(Times(Power(x,Plus(m,Times(CN1,pn))),Power(Csc(Plus(a,Times(b,Power(x,pn)))),p)),x))),And(And(And(And(FreeQ(List(a,b,p),x),RationalQ(m)),IntegerQ(pn)),GreaterEqual(Plus(m,Times(CN1,pn)),C0)),SameQ(q,C1)))),
ISetDelayed(Int(Power($(pf_,v_),p_DEFAULT),x_Symbol),
    Condition(Int(Power($(pf,ExpandToSum(v,x)),p),x),And(And(And(FreeQ(p,x),TrigQ(pf)),BinomialQ(v,x)),Not(BinomialMatchQ(v,x))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power($(pf_,v_),p_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(x,m),Power($(pf,ExpandToSum(v,x)),p)),x),And(And(And(FreeQ(List(m,p),x),TrigQ(pf)),BinomialQ(v,x)),Not(BinomialMatchQ(v,x))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Power(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_)))),p_DEFAULT)),q_),x_Symbol),
    Condition(Times(Power(c,Plus(q,Times(CN1,C1D2))),Sqrt(Times(c,Power(Sin(Plus(a,Times(b,Power(x,pn)))),p))),Power(Power(Sin(Plus(a,Times(b,Power(x,pn)))),Times(C1D2,p)),CN1),Int(Power(Sin(Plus(a,Times(b,Power(x,pn)))),Times(p,q)),x)),And(And(FreeQ(List(a,b,c,pn,p,q),x),PositiveIntegerQ(Plus(q,C1D2))),Not(OneQ(c,p))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Power(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_)))),p_DEFAULT)),q_),x_Symbol),
    Condition(Times(Power(c,Plus(q,Times(CN1,C1D2))),Sqrt(Times(c,Power(Cos(Plus(a,Times(b,Power(x,pn)))),p))),Power(Power(Cos(Plus(a,Times(b,Power(x,pn)))),Times(C1D2,p)),CN1),Int(Power(Cos(Plus(a,Times(b,Power(x,pn)))),Times(p,q)),x)),And(And(FreeQ(List(a,b,c,pn,p,q),x),PositiveIntegerQ(Plus(q,C1D2))),Not(OneQ(c,p))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Power(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_)))),p_DEFAULT)),q_),x_Symbol),
    Condition(Times(Power(c,Plus(q,C1D2)),Power(Sin(Plus(a,Times(b,Power(x,pn)))),Times(C1D2,p)),Power(Times(c,Power(Sin(Plus(a,Times(b,Power(x,pn)))),p)),CN1D2),Int(Power(Sin(Plus(a,Times(b,Power(x,pn)))),Times(p,q)),x)),And(And(FreeQ(List(a,b,c,pn,p,q),x),NegativeIntegerQ(Plus(q,Times(CN1,C1D2)))),Not(OneQ(c,p))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Power(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_)))),p_DEFAULT)),q_),x_Symbol),
    Condition(Times(Power(c,Plus(q,C1D2)),Power(Cos(Plus(a,Times(b,Power(x,pn)))),Times(C1D2,p)),Power(Times(c,Power(Cos(Plus(a,Times(b,Power(x,pn)))),p)),CN1D2),Int(Power(Cos(Plus(a,Times(b,Power(x,pn)))),Times(p,q)),x)),And(And(FreeQ(List(a,b,c,pn,p,q),x),NegativeIntegerQ(Plus(q,Times(CN1,C1D2)))),Not(OneQ(c,p))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Power(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_)))),p_DEFAULT)),q_),x_Symbol),
    Condition(Times(Power(Times(c,Power(Sin(Plus(a,Times(b,Power(x,pn)))),p)),q),Power(Power(Sin(Plus(a,Times(b,Power(x,pn)))),Times(p,q)),CN1),Int(Power(Sin(Plus(a,Times(b,Power(x,pn)))),Times(p,q)),x)),And(And(FreeQ(List(a,b,c,pn,p,q),x),Not(IntegerQ(Plus(q,C1D2)))),Not(OneQ(c,p))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Power(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_)))),p_DEFAULT)),q_),x_Symbol),
    Condition(Times(Power(Times(c,Power(Cos(Plus(a,Times(b,Power(x,pn)))),p)),q),Power(Power(Cos(Plus(a,Times(b,Power(x,pn)))),Times(p,q)),CN1),Int(Power(Cos(Plus(a,Times(b,Power(x,pn)))),Times(p,q)),x)),And(And(FreeQ(List(a,b,c,pn,p,q),x),Not(IntegerQ(Plus(q,C1D2)))),Not(OneQ(c,p))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,Power(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_DEFAULT)))),p_DEFAULT)),q_)),x_Symbol),
    Condition(Times(Power(c,Plus(q,Times(CN1,C1D2))),Sqrt(Times(c,Power(Sin(Plus(a,Times(b,Power(x,pn)))),p))),Power(Power(Sin(Plus(a,Times(b,Power(x,pn)))),Times(C1D2,p)),CN1),Int(Times(Power(x,m),Power(Sin(Plus(a,Times(b,Power(x,pn)))),Times(p,q))),x)),And(And(FreeQ(List(a,b,c,m,pn,p,q),x),PositiveIntegerQ(Plus(q,C1D2))),Not(OneQ(c,p))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,Power(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_DEFAULT)))),p_DEFAULT)),q_)),x_Symbol),
    Condition(Times(Power(c,Plus(q,Times(CN1,C1D2))),Sqrt(Times(c,Power(Cos(Plus(a,Times(b,Power(x,pn)))),p))),Power(Power(Cos(Plus(a,Times(b,Power(x,pn)))),Times(C1D2,p)),CN1),Int(Times(Power(x,m),Power(Cos(Plus(a,Times(b,Power(x,pn)))),Times(p,q))),x)),And(And(FreeQ(List(a,b,c,m,pn,p,q),x),PositiveIntegerQ(Plus(q,C1D2))),Not(OneQ(c,p))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,Power(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_DEFAULT)))),p_DEFAULT)),q_)),x_Symbol),
    Condition(Times(Power(c,Plus(q,C1D2)),Power(Sin(Plus(a,Times(b,Power(x,pn)))),Times(C1D2,p)),Power(Times(c,Power(Sin(Plus(a,Times(b,Power(x,pn)))),p)),CN1D2),Int(Times(Power(x,m),Power(Sin(Plus(a,Times(b,Power(x,pn)))),Times(p,q))),x)),And(And(FreeQ(List(a,b,c,m,pn,p,q),x),NegativeIntegerQ(Plus(q,Times(CN1,C1D2)))),Not(OneQ(c,p))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,Power(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_DEFAULT)))),p_DEFAULT)),q_)),x_Symbol),
    Condition(Times(Power(c,Plus(q,C1D2)),Power(Cos(Plus(a,Times(b,Power(x,pn)))),Times(C1D2,p)),Power(Times(c,Power(Cos(Plus(a,Times(b,Power(x,pn)))),p)),CN1D2),Int(Times(Power(x,m),Power(Cos(Plus(a,Times(b,Power(x,pn)))),Times(p,q))),x)),And(And(FreeQ(List(a,b,c,m,pn,p,q),x),NegativeIntegerQ(Plus(q,Times(CN1,C1D2)))),Not(OneQ(c,p))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,Power(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_DEFAULT)))),p_DEFAULT)),q_)),x_Symbol),
    Condition(Times(Power(Times(c,Power(Sin(Plus(a,Times(b,Power(x,pn)))),p)),q),Power(Power(Sin(Plus(a,Times(b,Power(x,pn)))),Times(p,q)),CN1),Int(Times(Power(x,m),Power(Sin(Plus(a,Times(b,Power(x,pn)))),Times(p,q))),x)),And(And(FreeQ(List(a,b,c,m,pn,p,q),x),Not(IntegerQ(Plus(q,C1D2)))),Not(OneQ(c,p))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,Power(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_DEFAULT)))),p_DEFAULT)),q_)),x_Symbol),
    Condition(Times(Power(Times(c,Power(Cos(Plus(a,Times(b,Power(x,pn)))),p)),q),Power(Power(Cos(Plus(a,Times(b,Power(x,pn)))),Times(p,q)),CN1),Int(Times(Power(x,m),Power(Cos(Plus(a,Times(b,Power(x,pn)))),Times(p,q))),x)),And(And(FreeQ(List(a,b,c,m,pn,p,q),x),Not(IntegerQ(Plus(q,C1D2)))),Not(OneQ(c,p))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Power(Sec(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_)))),p_DEFAULT)),q_),x_Symbol),
    Condition(Times(Power(c,Plus(q,Times(CN1,C1D2))),Sqrt(Times(c,Power(Sec(Plus(a,Times(b,Power(x,pn)))),p))),Power(Power(Sec(Plus(a,Times(b,Power(x,pn)))),Times(C1D2,p)),CN1),Int(Power(Sec(Plus(a,Times(b,Power(x,pn)))),Times(p,q)),x)),And(And(FreeQ(List(a,b,c,pn,p,q),x),PositiveIntegerQ(Plus(q,C1D2))),Not(OneQ(c,p))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Power(Csc(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_)))),p_DEFAULT)),q_),x_Symbol),
    Condition(Times(Power(c,Plus(q,Times(CN1,C1D2))),Sqrt(Times(c,Power(Csc(Plus(a,Times(b,Power(x,pn)))),p))),Power(Power(Csc(Plus(a,Times(b,Power(x,pn)))),Times(C1D2,p)),CN1),Int(Power(Csc(Plus(a,Times(b,Power(x,pn)))),Times(p,q)),x)),And(And(FreeQ(List(a,b,c,pn,p,q),x),PositiveIntegerQ(Plus(q,C1D2))),Not(OneQ(c,p))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Power(Sec(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_)))),p_DEFAULT)),q_),x_Symbol),
    Condition(Times(Power(c,Plus(q,C1D2)),Power(Sec(Plus(a,Times(b,Power(x,pn)))),Times(C1D2,p)),Power(Times(c,Power(Sec(Plus(a,Times(b,Power(x,pn)))),p)),CN1D2),Int(Power(Sec(Plus(a,Times(b,Power(x,pn)))),Times(p,q)),x)),And(And(FreeQ(List(a,b,c,pn,p,q),x),NegativeIntegerQ(Plus(q,Times(CN1,C1D2)))),Not(OneQ(c,p))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Power(Csc(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_)))),p_DEFAULT)),q_),x_Symbol),
    Condition(Times(Power(c,Plus(q,C1D2)),Power(Csc(Plus(a,Times(b,Power(x,pn)))),Times(C1D2,p)),Power(Times(c,Power(Csc(Plus(a,Times(b,Power(x,pn)))),p)),CN1D2),Int(Power(Csc(Plus(a,Times(b,Power(x,pn)))),Times(p,q)),x)),And(And(FreeQ(List(a,b,c,pn,p,q),x),NegativeIntegerQ(Plus(q,Times(CN1,C1D2)))),Not(OneQ(c,p))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Power(Sec(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_)))),p_DEFAULT)),q_),x_Symbol),
    Condition(Times(Power(Times(c,Power(Sec(Plus(a,Times(b,Power(x,pn)))),p)),q),Power(Power(Sec(Plus(a,Times(b,Power(x,pn)))),Times(p,q)),CN1),Int(Power(Sec(Plus(a,Times(b,Power(x,pn)))),Times(p,q)),x)),And(And(FreeQ(List(a,b,c,pn,p,q),x),Not(IntegerQ(Plus(q,C1D2)))),Not(OneQ(c,p))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Power(Csc(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_)))),p_DEFAULT)),q_),x_Symbol),
    Condition(Times(Power(Times(c,Power(Csc(Plus(a,Times(b,Power(x,pn)))),p)),q),Power(Power(Csc(Plus(a,Times(b,Power(x,pn)))),Times(p,q)),CN1),Int(Power(Csc(Plus(a,Times(b,Power(x,pn)))),Times(p,q)),x)),And(And(FreeQ(List(a,b,c,pn,p,q),x),Not(IntegerQ(Plus(q,C1D2)))),Not(OneQ(c,p))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,Power(Sec(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_DEFAULT)))),p_DEFAULT)),q_)),x_Symbol),
    Condition(Times(Power(c,Plus(q,Times(CN1,C1D2))),Sqrt(Times(c,Power(Sec(Plus(a,Times(b,Power(x,pn)))),p))),Power(Power(Sec(Plus(a,Times(b,Power(x,pn)))),Times(C1D2,p)),CN1),Int(Times(Power(x,m),Power(Sec(Plus(a,Times(b,Power(x,pn)))),Times(p,q))),x)),And(And(FreeQ(List(a,b,c,m,pn,p,q),x),PositiveIntegerQ(Plus(q,C1D2))),Not(OneQ(c,p))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,Power(Csc(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_DEFAULT)))),p_DEFAULT)),q_)),x_Symbol),
    Condition(Times(Power(c,Plus(q,Times(CN1,C1D2))),Sqrt(Times(c,Power(Csc(Plus(a,Times(b,Power(x,pn)))),p))),Power(Power(Csc(Plus(a,Times(b,Power(x,pn)))),Times(C1D2,p)),CN1),Int(Times(Power(x,m),Power(Csc(Plus(a,Times(b,Power(x,pn)))),Times(p,q))),x)),And(And(FreeQ(List(a,b,c,m,pn,p,q),x),PositiveIntegerQ(Plus(q,C1D2))),Not(OneQ(c,p))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,Power(Sec(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_DEFAULT)))),p_DEFAULT)),q_)),x_Symbol),
    Condition(Times(Power(c,Plus(q,C1D2)),Power(Sec(Plus(a,Times(b,Power(x,pn)))),Times(C1D2,p)),Power(Times(c,Power(Sec(Plus(a,Times(b,Power(x,pn)))),p)),CN1D2),Int(Times(Power(x,m),Power(Sec(Plus(a,Times(b,Power(x,pn)))),Times(p,q))),x)),And(And(FreeQ(List(a,b,c,m,pn,p,q),x),NegativeIntegerQ(Plus(q,C1D2))),Not(OneQ(c,p))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,Power(Csc(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_DEFAULT)))),p_DEFAULT)),q_)),x_Symbol),
    Condition(Times(Power(c,Plus(q,C1D2)),Power(Csc(Plus(a,Times(b,Power(x,pn)))),Times(C1D2,p)),Power(Times(c,Power(Csc(Plus(a,Times(b,Power(x,pn)))),p)),CN1D2),Int(Times(Power(x,m),Power(Csc(Plus(a,Times(b,Power(x,pn)))),Times(p,q))),x)),And(And(FreeQ(List(a,b,c,m,pn,p,q),x),NegativeIntegerQ(Plus(q,C1D2))),Not(OneQ(c,p))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,Power(Sec(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_DEFAULT)))),p_DEFAULT)),q_)),x_Symbol),
    Condition(Times(Power(Times(c,Power(Sec(Plus(a,Times(b,Power(x,pn)))),p)),q),Power(Power(Sec(Plus(a,Times(b,Power(x,pn)))),Times(p,q)),CN1),Int(Times(Power(x,m),Power(Sec(Plus(a,Times(b,Power(x,pn)))),Times(p,q))),x)),And(And(FreeQ(List(a,b,c,m,pn,p,q),x),Not(IntegerQ(Plus(q,C1D2)))),Not(OneQ(c,p))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,Power(Csc(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_DEFAULT)))),p_DEFAULT)),q_)),x_Symbol),
    Condition(Times(Power(Times(c,Power(Csc(Plus(a,Times(b,Power(x,pn)))),p)),q),Power(Power(Csc(Plus(a,Times(b,Power(x,pn)))),Times(p,q)),CN1),Int(Times(Power(x,m),Power(Csc(Plus(a,Times(b,Power(x,pn)))),Times(p,q))),x)),And(And(FreeQ(List(a,b,c,m,pn,p,q),x),Not(IntegerQ(Plus(q,C1D2)))),Not(OneQ(c,p))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Power($(pf_,v_),p_DEFAULT)),q_),x_Symbol),
    Condition(Int(Power(Times(c,Power($(pf,ExpandToSum(v,x)),p)),q),x),And(And(And(FreeQ(List(c,p,q),x),TrigQ(pf)),BinomialQ(v,x)),Not(BinomialMatchQ(v,x))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,Power($(pf_,v_),p_DEFAULT)),q_)),x_Symbol),
    Condition(Int(Times(Power(x,m),Power(Times(c,Power($(pf,ExpandToSum(v,x)),p)),q)),x),And(And(And(FreeQ(List(c,m,p,q),x),TrigQ(pf)),BinomialQ(v,x)),Not(BinomialMatchQ(v,x)))))
  );
}
