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
public class IntRules91 { 
  public static IAST RULES = List( 
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Tan(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_)),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,Negate(n),C1)),Power(Tan(Plus(a,Times(b,Power(x,n)))),Plus(p,C1)),Power(Times(b,n,Plus(p,C1)),-1)),Times(CN1,Plus(m,Negate(n),C1),Power(Times(b,n,Plus(p,C1)),-1),Int(Times(Power(x,Plus(m,Negate(n))),Power(Tan(Plus(a,Times(b,Power(x,n)))),Plus(p,C1))),x)),Negate(Int(Times(Power(x,m),Power(Tan(Plus(a,Times(b,Power(x,n)))),Plus(p,C2))),x))),And(And(And(FreeQ(List(a,b),x),RationalQ(m,n,p)),Less(p,CN1)),Less(Less(C0,n),Plus(m,C1))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Cot(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_)),x_Symbol),
    Condition(Plus(Times(CN1,Power(x,Plus(m,Negate(n),C1)),Power(Cot(Plus(a,Times(b,Power(x,n)))),Plus(p,C1)),Power(Times(b,n,Plus(p,C1)),-1)),Times(Plus(m,Negate(n),C1),Power(Times(b,n,Plus(p,C1)),-1),Int(Times(Power(x,Plus(m,Negate(n))),Power(Cot(Plus(a,Times(b,Power(x,n)))),Plus(p,C1))),x)),Negate(Int(Times(Power(x,m),Power(Cot(Plus(a,Times(b,Power(x,n)))),Plus(p,C2))),x))),And(And(And(FreeQ(List(a,b),x),RationalQ(m,n,p)),Less(p,CN1)),Less(Less(C0,n),Plus(m,C1))))),
ISetDelayed(Int(Sec(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),x_Symbol),
    Condition(Times(Power(n,-1),Subst(Int(Times(Power(x,Plus(Power(n,-1),Negate(C1))),Sec(Plus(a,Times(b,x)))),x),x,Power(x,n))),And(FreeQ(List(a,b),x),PositiveIntegerQ(Power(n,-1))))),
ISetDelayed(Int(Csc(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),x_Symbol),
    Condition(Times(Power(n,-1),Subst(Int(Times(Power(x,Plus(Power(n,-1),Negate(C1))),Csc(Plus(a,Times(b,x)))),x),x,Power(x,n))),And(FreeQ(List(a,b),x),PositiveIntegerQ(Power(n,-1))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Sec(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_DEFAULT)),x_Symbol),
    Condition(Times(Power(n,-1),Subst(Int(Times(Power(x,Plus(Simplify(Times(Plus(m,C1),Power(n,-1))),Negate(C1))),Power(Sec(Plus(a,Times(b,x))),p)),x),x,Power(x,n))),And(FreeQ(List(a,b,m,n,p),x),PositiveIntegerQ(Simplify(Times(Plus(m,C1),Power(n,-1))))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Csc(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_DEFAULT)),x_Symbol),
    Condition(Times(Power(n,-1),Subst(Int(Times(Power(x,Plus(Simplify(Times(Plus(m,C1),Power(n,-1))),Negate(C1))),Power(Csc(Plus(a,Times(b,x))),p)),x),x,Power(x,n))),And(FreeQ(List(a,b,m,n,p),x),PositiveIntegerQ(Simplify(Times(Plus(m,C1),Power(n,-1))))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Sec(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),p_),Sin(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))))),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,Negate(n),C1)),Power(Sec(Plus(a,Times(b,Power(x,n)))),Plus(p,Negate(C1))),Power(Times(b,n,Plus(p,Negate(C1))),-1)),Times(CN1,Plus(m,Negate(n),C1),Power(Times(b,n,Plus(p,Negate(C1))),-1),Int(Times(Power(x,Plus(m,Negate(n))),Power(Sec(Plus(a,Times(b,Power(x,n)))),Plus(p,Negate(C1)))),x))),And(And(And(And(FreeQ(List(a,b,p),x),RationalQ(m)),IntegerQ(n)),GreaterEqual(Plus(m,Negate(n)),C0)),NonzeroQ(Plus(p,Negate(C1)))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Csc(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),p_),Cos(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))))),x_Symbol),
    Condition(Plus(Times(CN1,Power(x,Plus(m,Negate(n),C1)),Power(Csc(Plus(a,Times(b,Power(x,n)))),Plus(p,Negate(C1))),Power(Times(b,n,Plus(p,Negate(C1))),-1)),Times(Plus(m,Negate(n),C1),Power(Times(b,n,Plus(p,Negate(C1))),-1),Int(Times(Power(x,Plus(m,Negate(n))),Power(Csc(Plus(a,Times(b,Power(x,n)))),Plus(p,Negate(C1)))),x))),And(And(And(And(FreeQ(List(a,b,p),x),RationalQ(m)),IntegerQ(n)),GreaterEqual(Plus(m,Negate(n)),C0)),NonzeroQ(Plus(p,Negate(C1)))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Sec(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT),Power(Tan(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),q_DEFAULT)),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,Negate(n),C1)),Power(Sec(Plus(a,Times(b,Power(x,n)))),p),Power(Times(b,n,p),-1)),Times(CN1,Plus(m,Negate(n),C1),Power(Times(b,n,p),-1),Int(Times(Power(x,Plus(m,Negate(n))),Power(Sec(Plus(a,Times(b,Power(x,n)))),p)),x))),And(And(And(And(FreeQ(List(a,b,p),x),RationalQ(m)),IntegerQ(n)),GreaterEqual(Plus(m,Negate(n)),C0)),SameQ(q,C1)))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Csc(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT),Power(Cot(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),q_DEFAULT)),x_Symbol),
    Condition(Plus(Times(CN1,Power(x,Plus(m,Negate(n),C1)),Power(Csc(Plus(a,Times(b,Power(x,n)))),p),Power(Times(b,n,p),-1)),Times(Plus(m,Negate(n),C1),Power(Times(b,n,p),-1),Int(Times(Power(x,Plus(m,Negate(n))),Power(Csc(Plus(a,Times(b,Power(x,n)))),p)),x))),And(And(And(And(FreeQ(List(a,b,p),x),RationalQ(m)),IntegerQ(n)),GreaterEqual(Plus(m,Negate(n)),C0)),SameQ(q,C1)))),
ISetDelayed(Int(Power($(F_,v_),p_DEFAULT),x_Symbol),
    Condition(Int(Power(F(ExpandToSum(v,x)),p),x),And(And(And(FreeQ(p,x),TrigQ(FSymbol)),BinomialQ(v,x)),Not(BinomialMatchQ(v,x))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power($(F_,v_),p_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(x,m),Power(F(ExpandToSum(v,x)),p)),x),And(And(And(FreeQ(List(m,p),x),TrigQ(FSymbol)),BinomialQ(v,x)),Not(BinomialMatchQ(v,x))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Power(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_DEFAULT)),q_),x_Symbol),
    Condition(Times(Power(c,Plus(q,Negate(C1D2))),Sqrt(Times(c,Power(Sin(Plus(a,Times(b,Power(x,n)))),p))),Power(Power(Sin(Plus(a,Times(b,Power(x,n)))),Times(C1D2,p)),-1),Int(Power(Sin(Plus(a,Times(b,Power(x,n)))),Times(p,q)),x)),And(And(FreeQ(List(a,b,c,n,p,q),x),PositiveIntegerQ(Plus(q,C1D2))),Not(OneQ(c,p))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Power(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_DEFAULT)),q_),x_Symbol),
    Condition(Times(Power(c,Plus(q,Negate(C1D2))),Sqrt(Times(c,Power(Cos(Plus(a,Times(b,Power(x,n)))),p))),Power(Power(Cos(Plus(a,Times(b,Power(x,n)))),Times(C1D2,p)),-1),Int(Power(Cos(Plus(a,Times(b,Power(x,n)))),Times(p,q)),x)),And(And(FreeQ(List(a,b,c,n,p,q),x),PositiveIntegerQ(Plus(q,C1D2))),Not(OneQ(c,p))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Power(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_DEFAULT)),q_),x_Symbol),
    Condition(Times(Power(c,Plus(q,C1D2)),Power(Sin(Plus(a,Times(b,Power(x,n)))),Times(C1D2,p)),Power(Times(c,Power(Sin(Plus(a,Times(b,Power(x,n)))),p)),CN1D2),Int(Power(Sin(Plus(a,Times(b,Power(x,n)))),Times(p,q)),x)),And(And(FreeQ(List(a,b,c,n,p,q),x),NegativeIntegerQ(Plus(q,Negate(C1D2)))),Not(OneQ(c,p))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Power(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_DEFAULT)),q_),x_Symbol),
    Condition(Times(Power(c,Plus(q,C1D2)),Power(Cos(Plus(a,Times(b,Power(x,n)))),Times(C1D2,p)),Power(Times(c,Power(Cos(Plus(a,Times(b,Power(x,n)))),p)),CN1D2),Int(Power(Cos(Plus(a,Times(b,Power(x,n)))),Times(p,q)),x)),And(And(FreeQ(List(a,b,c,n,p,q),x),NegativeIntegerQ(Plus(q,Negate(C1D2)))),Not(OneQ(c,p))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Power(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_DEFAULT)),q_),x_Symbol),
    Condition(Times(Power(Times(c,Power(Sin(Plus(a,Times(b,Power(x,n)))),p)),q),Power(Power(Sin(Plus(a,Times(b,Power(x,n)))),Times(p,q)),-1),Int(Power(Sin(Plus(a,Times(b,Power(x,n)))),Times(p,q)),x)),And(And(FreeQ(List(a,b,c,n,p,q),x),Not(IntegerQ(Plus(q,C1D2)))),Not(OneQ(c,p))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Power(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_DEFAULT)),q_),x_Symbol),
    Condition(Times(Power(Times(c,Power(Cos(Plus(a,Times(b,Power(x,n)))),p)),q),Power(Power(Cos(Plus(a,Times(b,Power(x,n)))),Times(p,q)),-1),Int(Power(Cos(Plus(a,Times(b,Power(x,n)))),Times(p,q)),x)),And(And(FreeQ(List(a,b,c,n,p,q),x),Not(IntegerQ(Plus(q,C1D2)))),Not(OneQ(c,p))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,Power(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT)),q_)),x_Symbol),
    Condition(Times(Power(c,Plus(q,Negate(C1D2))),Sqrt(Times(c,Power(Sin(Plus(a,Times(b,Power(x,n)))),p))),Power(Power(Sin(Plus(a,Times(b,Power(x,n)))),Times(C1D2,p)),-1),Int(Times(Power(x,m),Power(Sin(Plus(a,Times(b,Power(x,n)))),Times(p,q))),x)),And(And(FreeQ(List(a,b,c,m,n,p,q),x),PositiveIntegerQ(Plus(q,C1D2))),Not(OneQ(c,p))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,Power(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT)),q_)),x_Symbol),
    Condition(Times(Power(c,Plus(q,Negate(C1D2))),Sqrt(Times(c,Power(Cos(Plus(a,Times(b,Power(x,n)))),p))),Power(Power(Cos(Plus(a,Times(b,Power(x,n)))),Times(C1D2,p)),-1),Int(Times(Power(x,m),Power(Cos(Plus(a,Times(b,Power(x,n)))),Times(p,q))),x)),And(And(FreeQ(List(a,b,c,m,n,p,q),x),PositiveIntegerQ(Plus(q,C1D2))),Not(OneQ(c,p))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,Power(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT)),q_)),x_Symbol),
    Condition(Times(Power(c,Plus(q,C1D2)),Power(Sin(Plus(a,Times(b,Power(x,n)))),Times(C1D2,p)),Power(Times(c,Power(Sin(Plus(a,Times(b,Power(x,n)))),p)),CN1D2),Int(Times(Power(x,m),Power(Sin(Plus(a,Times(b,Power(x,n)))),Times(p,q))),x)),And(And(FreeQ(List(a,b,c,m,n,p,q),x),NegativeIntegerQ(Plus(q,Negate(C1D2)))),Not(OneQ(c,p))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,Power(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT)),q_)),x_Symbol),
    Condition(Times(Power(c,Plus(q,C1D2)),Power(Cos(Plus(a,Times(b,Power(x,n)))),Times(C1D2,p)),Power(Times(c,Power(Cos(Plus(a,Times(b,Power(x,n)))),p)),CN1D2),Int(Times(Power(x,m),Power(Cos(Plus(a,Times(b,Power(x,n)))),Times(p,q))),x)),And(And(FreeQ(List(a,b,c,m,n,p,q),x),NegativeIntegerQ(Plus(q,Negate(C1D2)))),Not(OneQ(c,p))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,Power(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT)),q_)),x_Symbol),
    Condition(Times(Power(Times(c,Power(Sin(Plus(a,Times(b,Power(x,n)))),p)),q),Power(Power(Sin(Plus(a,Times(b,Power(x,n)))),Times(p,q)),-1),Int(Times(Power(x,m),Power(Sin(Plus(a,Times(b,Power(x,n)))),Times(p,q))),x)),And(And(FreeQ(List(a,b,c,m,n,p,q),x),Not(IntegerQ(Plus(q,C1D2)))),Not(OneQ(c,p))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,Power(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT)),q_)),x_Symbol),
    Condition(Times(Power(Times(c,Power(Cos(Plus(a,Times(b,Power(x,n)))),p)),q),Power(Power(Cos(Plus(a,Times(b,Power(x,n)))),Times(p,q)),-1),Int(Times(Power(x,m),Power(Cos(Plus(a,Times(b,Power(x,n)))),Times(p,q))),x)),And(And(FreeQ(List(a,b,c,m,n,p,q),x),Not(IntegerQ(Plus(q,C1D2)))),Not(OneQ(c,p))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Power(Sec(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_DEFAULT)),q_),x_Symbol),
    Condition(Times(Power(c,Plus(q,Negate(C1D2))),Sqrt(Times(c,Power(Sec(Plus(a,Times(b,Power(x,n)))),p))),Power(Power(Sec(Plus(a,Times(b,Power(x,n)))),Times(C1D2,p)),-1),Int(Power(Sec(Plus(a,Times(b,Power(x,n)))),Times(p,q)),x)),And(And(FreeQ(List(a,b,c,n,p,q),x),PositiveIntegerQ(Plus(q,C1D2))),Not(OneQ(c,p))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Power(Csc(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_DEFAULT)),q_),x_Symbol),
    Condition(Times(Power(c,Plus(q,Negate(C1D2))),Sqrt(Times(c,Power(Csc(Plus(a,Times(b,Power(x,n)))),p))),Power(Power(Csc(Plus(a,Times(b,Power(x,n)))),Times(C1D2,p)),-1),Int(Power(Csc(Plus(a,Times(b,Power(x,n)))),Times(p,q)),x)),And(And(FreeQ(List(a,b,c,n,p,q),x),PositiveIntegerQ(Plus(q,C1D2))),Not(OneQ(c,p))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Power(Sec(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_DEFAULT)),q_),x_Symbol),
    Condition(Times(Power(c,Plus(q,C1D2)),Power(Sec(Plus(a,Times(b,Power(x,n)))),Times(C1D2,p)),Power(Times(c,Power(Sec(Plus(a,Times(b,Power(x,n)))),p)),CN1D2),Int(Power(Sec(Plus(a,Times(b,Power(x,n)))),Times(p,q)),x)),And(And(FreeQ(List(a,b,c,n,p,q),x),NegativeIntegerQ(Plus(q,Negate(C1D2)))),Not(OneQ(c,p))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Power(Csc(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_DEFAULT)),q_),x_Symbol),
    Condition(Times(Power(c,Plus(q,C1D2)),Power(Csc(Plus(a,Times(b,Power(x,n)))),Times(C1D2,p)),Power(Times(c,Power(Csc(Plus(a,Times(b,Power(x,n)))),p)),CN1D2),Int(Power(Csc(Plus(a,Times(b,Power(x,n)))),Times(p,q)),x)),And(And(FreeQ(List(a,b,c,n,p,q),x),NegativeIntegerQ(Plus(q,Negate(C1D2)))),Not(OneQ(c,p))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Power(Sec(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_DEFAULT)),q_),x_Symbol),
    Condition(Times(Power(Times(c,Power(Sec(Plus(a,Times(b,Power(x,n)))),p)),q),Power(Power(Sec(Plus(a,Times(b,Power(x,n)))),Times(p,q)),-1),Int(Power(Sec(Plus(a,Times(b,Power(x,n)))),Times(p,q)),x)),And(And(FreeQ(List(a,b,c,n,p,q),x),Not(IntegerQ(Plus(q,C1D2)))),Not(OneQ(c,p))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Power(Csc(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_DEFAULT)),q_),x_Symbol),
    Condition(Times(Power(Times(c,Power(Csc(Plus(a,Times(b,Power(x,n)))),p)),q),Power(Power(Csc(Plus(a,Times(b,Power(x,n)))),Times(p,q)),-1),Int(Power(Csc(Plus(a,Times(b,Power(x,n)))),Times(p,q)),x)),And(And(FreeQ(List(a,b,c,n,p,q),x),Not(IntegerQ(Plus(q,C1D2)))),Not(OneQ(c,p))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,Power(Sec(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT)),q_)),x_Symbol),
    Condition(Times(Power(c,Plus(q,Negate(C1D2))),Sqrt(Times(c,Power(Sec(Plus(a,Times(b,Power(x,n)))),p))),Power(Power(Sec(Plus(a,Times(b,Power(x,n)))),Times(C1D2,p)),-1),Int(Times(Power(x,m),Power(Sec(Plus(a,Times(b,Power(x,n)))),Times(p,q))),x)),And(And(FreeQ(List(a,b,c,m,n,p,q),x),PositiveIntegerQ(Plus(q,C1D2))),Not(OneQ(c,p))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,Power(Csc(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT)),q_)),x_Symbol),
    Condition(Times(Power(c,Plus(q,Negate(C1D2))),Sqrt(Times(c,Power(Csc(Plus(a,Times(b,Power(x,n)))),p))),Power(Power(Csc(Plus(a,Times(b,Power(x,n)))),Times(C1D2,p)),-1),Int(Times(Power(x,m),Power(Csc(Plus(a,Times(b,Power(x,n)))),Times(p,q))),x)),And(And(FreeQ(List(a,b,c,m,n,p,q),x),PositiveIntegerQ(Plus(q,C1D2))),Not(OneQ(c,p))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,Power(Sec(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT)),q_)),x_Symbol),
    Condition(Times(Power(c,Plus(q,C1D2)),Power(Sec(Plus(a,Times(b,Power(x,n)))),Times(C1D2,p)),Power(Times(c,Power(Sec(Plus(a,Times(b,Power(x,n)))),p)),CN1D2),Int(Times(Power(x,m),Power(Sec(Plus(a,Times(b,Power(x,n)))),Times(p,q))),x)),And(And(FreeQ(List(a,b,c,m,n,p,q),x),NegativeIntegerQ(Plus(q,C1D2))),Not(OneQ(c,p))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,Power(Csc(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT)),q_)),x_Symbol),
    Condition(Times(Power(c,Plus(q,C1D2)),Power(Csc(Plus(a,Times(b,Power(x,n)))),Times(C1D2,p)),Power(Times(c,Power(Csc(Plus(a,Times(b,Power(x,n)))),p)),CN1D2),Int(Times(Power(x,m),Power(Csc(Plus(a,Times(b,Power(x,n)))),Times(p,q))),x)),And(And(FreeQ(List(a,b,c,m,n,p,q),x),NegativeIntegerQ(Plus(q,C1D2))),Not(OneQ(c,p))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,Power(Sec(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT)),q_)),x_Symbol),
    Condition(Times(Power(Times(c,Power(Sec(Plus(a,Times(b,Power(x,n)))),p)),q),Power(Power(Sec(Plus(a,Times(b,Power(x,n)))),Times(p,q)),-1),Int(Times(Power(x,m),Power(Sec(Plus(a,Times(b,Power(x,n)))),Times(p,q))),x)),And(And(FreeQ(List(a,b,c,m,n,p,q),x),Not(IntegerQ(Plus(q,C1D2)))),Not(OneQ(c,p))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,Power(Csc(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT)),q_)),x_Symbol),
    Condition(Times(Power(Times(c,Power(Csc(Plus(a,Times(b,Power(x,n)))),p)),q),Power(Power(Csc(Plus(a,Times(b,Power(x,n)))),Times(p,q)),-1),Int(Times(Power(x,m),Power(Csc(Plus(a,Times(b,Power(x,n)))),Times(p,q))),x)),And(And(FreeQ(List(a,b,c,m,n,p,q),x),Not(IntegerQ(Plus(q,C1D2)))),Not(OneQ(c,p))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Power($(F_,v_),p_DEFAULT)),q_),x_Symbol),
    Condition(Int(Power(Times(c,Power(F(ExpandToSum(v,x)),p)),q),x),And(And(And(FreeQ(List(c,p,q),x),TrigQ(FSymbol)),BinomialQ(v,x)),Not(BinomialMatchQ(v,x))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,Power($(F_,v_),p_DEFAULT)),q_)),x_Symbol),
    Condition(Int(Times(Power(x,m),Power(Times(c,Power(F(ExpandToSum(v,x)),p)),q)),x),And(And(And(FreeQ(List(c,m,p,q),x),TrigQ(FSymbol)),BinomialQ(v,x)),Not(BinomialMatchQ(v,x)))))
  );
}
