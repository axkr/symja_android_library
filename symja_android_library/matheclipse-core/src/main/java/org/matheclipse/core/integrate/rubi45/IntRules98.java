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
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Sech(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),p_),Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))))),x_Symbol),
    Condition(Plus(Times(CN1,Power(x,Plus(m,Negate(n),C1)),Power(Sech(Plus(a,Times(b,Power(x,n)))),Plus(p,Negate(C1))),Power(Times(b,n,Plus(p,Negate(C1))),-1)),Times(Plus(m,Negate(n),C1),Power(Times(b,n,Plus(p,Negate(C1))),-1),Int(Times(Power(x,Plus(m,Negate(n))),Power(Sech(Plus(a,Times(b,Power(x,n)))),Plus(p,Negate(C1)))),x))),And(And(And(And(FreeQ(List(a,b,p),x),RationalQ(m)),IntegerQ(n)),GreaterEqual(Plus(m,Negate(n)),C0)),NonzeroQ(Plus(p,Negate(C1)))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Csch(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),p_),Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))))),x_Symbol),
    Condition(Plus(Times(CN1,Power(x,Plus(m,Negate(n),C1)),Power(Csch(Plus(a,Times(b,Power(x,n)))),Plus(p,Negate(C1))),Power(Times(b,n,Plus(p,Negate(C1))),-1)),Times(Plus(m,Negate(n),C1),Power(Times(b,n,Plus(p,Negate(C1))),-1),Int(Times(Power(x,Plus(m,Negate(n))),Power(Csch(Plus(a,Times(b,Power(x,n)))),Plus(p,Negate(C1)))),x))),And(And(And(And(FreeQ(List(a,b,p),x),RationalQ(m)),IntegerQ(n)),GreaterEqual(Plus(m,Negate(n)),C0)),NonzeroQ(Plus(p,Negate(C1)))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Sech(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT),Power(Tanh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),q_DEFAULT)),x_Symbol),
    Condition(Plus(Times(CN1,Power(x,Plus(m,Negate(n),C1)),Power(Sech(Plus(a,Times(b,Power(x,n)))),p),Power(Times(b,n,p),-1)),Times(Plus(m,Negate(n),C1),Power(Times(b,n,p),-1),Int(Times(Power(x,Plus(m,Negate(n))),Power(Sech(Plus(a,Times(b,Power(x,n)))),p)),x))),And(And(And(And(FreeQ(List(a,b,p),x),RationalQ(m)),IntegerQ(n)),GreaterEqual(Plus(m,Negate(n)),C0)),SameQ(q,C1)))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Csch(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT),Power(Coth(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),q_DEFAULT)),x_Symbol),
    Condition(Plus(Times(CN1,Power(x,Plus(m,Negate(n),C1)),Power(Csch(Plus(a,Times(b,Power(x,n)))),p),Power(Times(b,n,p),-1)),Times(Plus(m,Negate(n),C1),Power(Times(b,n,p),-1),Int(Times(Power(x,Plus(m,Negate(n))),Power(Csch(Plus(a,Times(b,Power(x,n)))),p)),x))),And(And(And(And(FreeQ(List(a,b,p),x),RationalQ(m)),IntegerQ(n)),GreaterEqual(Plus(m,Negate(n)),C0)),SameQ(q,C1)))),
ISetDelayed(Int(Power($($p("F"),v_),p_DEFAULT),x_Symbol),
    Condition(Int(Power(F(ExpandToSum(v,x)),p),x),And(And(And(FreeQ(p,x),HyperbolicQ($s("F"))),BinomialQ(v,x)),Not(BinomialMatchQ(v,x))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power($($p("F"),v_),p_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(x,m),Power(F(ExpandToSum(v,x)),p)),x),And(And(And(FreeQ(List(m,p),x),HyperbolicQ($s("F"))),BinomialQ(v,x)),Not(BinomialMatchQ(v,x))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Power(Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_DEFAULT)),q_),x_Symbol),
    Condition(Times(Power(c,Plus(q,Negate(C1D2))),Sqrt(Times(c,Power(Sinh(Plus(a,Times(b,Power(x,n)))),p))),Power(Power(Sinh(Plus(a,Times(b,Power(x,n)))),Times(C1D2,p)),-1),Int(Power(Sinh(Plus(a,Times(b,Power(x,n)))),Times(p,q)),x)),And(And(FreeQ(List(a,b,c,n,p,q),x),PositiveIntegerQ(Plus(q,C1D2))),Not(OneQ(c,p))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Power(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_DEFAULT)),q_),x_Symbol),
    Condition(Times(Power(c,Plus(q,Negate(C1D2))),Sqrt(Times(c,Power(Cosh(Plus(a,Times(b,Power(x,n)))),p))),Power(Power(Cosh(Plus(a,Times(b,Power(x,n)))),Times(C1D2,p)),-1),Int(Power(Cosh(Plus(a,Times(b,Power(x,n)))),Times(p,q)),x)),And(And(FreeQ(List(a,b,c,n,p,q),x),PositiveIntegerQ(Plus(q,C1D2))),Not(OneQ(c,p))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Power(Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_DEFAULT)),q_),x_Symbol),
    Condition(Times(Power(c,Plus(q,C1D2)),Power(Sinh(Plus(a,Times(b,Power(x,n)))),Times(C1D2,p)),Power(Times(c,Power(Sinh(Plus(a,Times(b,Power(x,n)))),p)),CN1D2),Int(Power(Sinh(Plus(a,Times(b,Power(x,n)))),Times(p,q)),x)),And(And(FreeQ(List(a,b,c,n,p,q),x),NegativeIntegerQ(Plus(q,Negate(C1D2)))),Not(OneQ(c,p))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Power(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_DEFAULT)),q_),x_Symbol),
    Condition(Times(Power(c,Plus(q,C1D2)),Power(Cosh(Plus(a,Times(b,Power(x,n)))),Times(C1D2,p)),Power(Times(c,Power(Cosh(Plus(a,Times(b,Power(x,n)))),p)),CN1D2),Int(Power(Cosh(Plus(a,Times(b,Power(x,n)))),Times(p,q)),x)),And(And(FreeQ(List(a,b,c,n,p,q),x),NegativeIntegerQ(Plus(q,Negate(C1D2)))),Not(OneQ(c,p))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Power(Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_DEFAULT)),q_),x_Symbol),
    Condition(Times(Power(Times(c,Power(Sinh(Plus(a,Times(b,Power(x,n)))),p)),q),Power(Power(Sinh(Plus(a,Times(b,Power(x,n)))),Times(p,q)),-1),Int(Power(Sinh(Plus(a,Times(b,Power(x,n)))),Times(p,q)),x)),And(And(FreeQ(List(a,b,c,n,p,q),x),Not(IntegerQ(Plus(q,C1D2)))),Not(OneQ(c,p))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Power(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_DEFAULT)),q_),x_Symbol),
    Condition(Times(Power(Times(c,Power(Cosh(Plus(a,Times(b,Power(x,n)))),p)),q),Power(Power(Cosh(Plus(a,Times(b,Power(x,n)))),Times(p,q)),-1),Int(Power(Cosh(Plus(a,Times(b,Power(x,n)))),Times(p,q)),x)),And(And(FreeQ(List(a,b,c,n,p,q),x),Not(IntegerQ(Plus(q,C1D2)))),Not(OneQ(c,p))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,Power(Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT)),q_)),x_Symbol),
    Condition(Times(Power(c,Plus(q,Negate(C1D2))),Sqrt(Times(c,Power(Sinh(Plus(a,Times(b,Power(x,n)))),p))),Power(Power(Sinh(Plus(a,Times(b,Power(x,n)))),Times(C1D2,p)),-1),Int(Times(Power(x,m),Power(Sinh(Plus(a,Times(b,Power(x,n)))),Times(p,q))),x)),And(And(FreeQ(List(a,b,c,m,n,p,q),x),PositiveIntegerQ(Plus(q,C1D2))),Not(OneQ(c,p))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,Power(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT)),q_)),x_Symbol),
    Condition(Times(Power(c,Plus(q,Negate(C1D2))),Sqrt(Times(c,Power(Cosh(Plus(a,Times(b,Power(x,n)))),p))),Power(Power(Cosh(Plus(a,Times(b,Power(x,n)))),Times(C1D2,p)),-1),Int(Times(Power(x,m),Power(Cosh(Plus(a,Times(b,Power(x,n)))),Times(p,q))),x)),And(And(FreeQ(List(a,b,c,m,n,p,q),x),PositiveIntegerQ(Plus(q,C1D2))),Not(OneQ(c,p))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,Power(Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT)),q_)),x_Symbol),
    Condition(Times(Power(c,Plus(q,C1D2)),Power(Sinh(Plus(a,Times(b,Power(x,n)))),Times(C1D2,p)),Power(Times(c,Power(Sinh(Plus(a,Times(b,Power(x,n)))),p)),CN1D2),Int(Times(Power(x,m),Power(Sinh(Plus(a,Times(b,Power(x,n)))),Times(p,q))),x)),And(And(FreeQ(List(a,b,c,m,n,p,q),x),NegativeIntegerQ(Plus(q,Negate(C1D2)))),Not(OneQ(c,p))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,Power(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT)),q_)),x_Symbol),
    Condition(Times(Power(c,Plus(q,C1D2)),Power(Cosh(Plus(a,Times(b,Power(x,n)))),Times(C1D2,p)),Power(Times(c,Power(Cosh(Plus(a,Times(b,Power(x,n)))),p)),CN1D2),Int(Times(Power(x,m),Power(Cosh(Plus(a,Times(b,Power(x,n)))),Times(p,q))),x)),And(And(FreeQ(List(a,b,c,m,n,p,q),x),NegativeIntegerQ(Plus(q,Negate(C1D2)))),Not(OneQ(c,p))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,Power(Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT)),q_)),x_Symbol),
    Condition(Times(Power(Times(c,Power(Sinh(Plus(a,Times(b,Power(x,n)))),p)),q),Power(Power(Sinh(Plus(a,Times(b,Power(x,n)))),Times(p,q)),-1),Int(Times(Power(x,m),Power(Sinh(Plus(a,Times(b,Power(x,n)))),Times(p,q))),x)),And(And(FreeQ(List(a,b,c,m,n,p,q),x),Not(IntegerQ(Plus(q,C1D2)))),Not(OneQ(c,p))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,Power(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT)),q_)),x_Symbol),
    Condition(Times(Power(Times(c,Power(Cosh(Plus(a,Times(b,Power(x,n)))),p)),q),Power(Power(Cosh(Plus(a,Times(b,Power(x,n)))),Times(p,q)),-1),Int(Times(Power(x,m),Power(Cosh(Plus(a,Times(b,Power(x,n)))),Times(p,q))),x)),And(And(FreeQ(List(a,b,c,m,n,p,q),x),Not(IntegerQ(Plus(q,C1D2)))),Not(OneQ(c,p))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Power(Sech(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_DEFAULT)),q_),x_Symbol),
    Condition(Times(Power(c,Plus(q,Negate(C1D2))),Sqrt(Times(c,Power(Sech(Plus(a,Times(b,Power(x,n)))),p))),Power(Power(Sech(Plus(a,Times(b,Power(x,n)))),Times(C1D2,p)),-1),Int(Power(Sech(Plus(a,Times(b,Power(x,n)))),Times(p,q)),x)),And(And(FreeQ(List(a,b,c,n,p,q),x),PositiveIntegerQ(Plus(q,C1D2))),Not(OneQ(c,p))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Power(Csch(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_DEFAULT)),q_),x_Symbol),
    Condition(Times(Power(c,Plus(q,Negate(C1D2))),Sqrt(Times(c,Power(Csch(Plus(a,Times(b,Power(x,n)))),p))),Power(Power(Csch(Plus(a,Times(b,Power(x,n)))),Times(C1D2,p)),-1),Int(Power(Csch(Plus(a,Times(b,Power(x,n)))),Times(p,q)),x)),And(And(FreeQ(List(a,b,c,n,p,q),x),PositiveIntegerQ(Plus(q,C1D2))),Not(OneQ(c,p))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Power(Sech(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_DEFAULT)),q_),x_Symbol),
    Condition(Times(Power(c,Plus(q,C1D2)),Power(Sech(Plus(a,Times(b,Power(x,n)))),Times(C1D2,p)),Power(Times(c,Power(Sech(Plus(a,Times(b,Power(x,n)))),p)),CN1D2),Int(Power(Sech(Plus(a,Times(b,Power(x,n)))),Times(p,q)),x)),And(And(FreeQ(List(a,b,c,n,p,q),x),NegativeIntegerQ(Plus(q,Negate(C1D2)))),Not(OneQ(c,p))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Power(Csch(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_DEFAULT)),q_),x_Symbol),
    Condition(Times(Power(c,Plus(q,C1D2)),Power(Csch(Plus(a,Times(b,Power(x,n)))),Times(C1D2,p)),Power(Times(c,Power(Csch(Plus(a,Times(b,Power(x,n)))),p)),CN1D2),Int(Power(Csch(Plus(a,Times(b,Power(x,n)))),Times(p,q)),x)),And(And(FreeQ(List(a,b,c,n,p,q),x),NegativeIntegerQ(Plus(q,Negate(C1D2)))),Not(OneQ(c,p))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Power(Sech(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_DEFAULT)),q_),x_Symbol),
    Condition(Times(Power(Times(c,Power(Sech(Plus(a,Times(b,Power(x,n)))),p)),q),Power(Power(Sech(Plus(a,Times(b,Power(x,n)))),Times(p,q)),-1),Int(Power(Sech(Plus(a,Times(b,Power(x,n)))),Times(p,q)),x)),And(And(FreeQ(List(a,b,c,n,p,q),x),Not(IntegerQ(Plus(q,C1D2)))),Not(OneQ(c,p))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Power(Csch(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_)))),p_DEFAULT)),q_),x_Symbol),
    Condition(Times(Power(Times(c,Power(Csch(Plus(a,Times(b,Power(x,n)))),p)),q),Power(Power(Csch(Plus(a,Times(b,Power(x,n)))),Times(p,q)),-1),Int(Power(Csch(Plus(a,Times(b,Power(x,n)))),Times(p,q)),x)),And(And(FreeQ(List(a,b,c,n,p,q),x),Not(IntegerQ(Plus(q,C1D2)))),Not(OneQ(c,p))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,Power(Sech(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT)),q_)),x_Symbol),
    Condition(Times(Power(c,Plus(q,Negate(C1D2))),Sqrt(Times(c,Power(Sech(Plus(a,Times(b,Power(x,n)))),p))),Power(Power(Sech(Plus(a,Times(b,Power(x,n)))),Times(C1D2,p)),-1),Int(Times(Power(x,m),Power(Sech(Plus(a,Times(b,Power(x,n)))),Times(p,q))),x)),And(And(FreeQ(List(a,b,c,m,n,p,q),x),PositiveIntegerQ(Plus(q,C1D2))),Not(OneQ(c,p))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,Power(Csch(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT)),q_)),x_Symbol),
    Condition(Times(Power(c,Plus(q,Negate(C1D2))),Sqrt(Times(c,Power(Csch(Plus(a,Times(b,Power(x,n)))),p))),Power(Power(Csch(Plus(a,Times(b,Power(x,n)))),Times(C1D2,p)),-1),Int(Times(Power(x,m),Power(Csch(Plus(a,Times(b,Power(x,n)))),Times(p,q))),x)),And(And(FreeQ(List(a,b,c,m,n,p,q),x),PositiveIntegerQ(Plus(q,C1D2))),Not(OneQ(c,p))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,Power(Sech(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT)),q_)),x_Symbol),
    Condition(Times(Power(c,Plus(q,C1D2)),Power(Sech(Plus(a,Times(b,Power(x,n)))),Times(C1D2,p)),Power(Times(c,Power(Sech(Plus(a,Times(b,Power(x,n)))),p)),CN1D2),Int(Times(Power(x,m),Power(Sech(Plus(a,Times(b,Power(x,n)))),Times(p,q))),x)),And(And(FreeQ(List(a,b,c,m,n,p,q),x),NegativeIntegerQ(Plus(q,C1D2))),Not(OneQ(c,p))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,Power(Csch(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT)),q_)),x_Symbol),
    Condition(Times(Power(c,Plus(q,C1D2)),Power(Csch(Plus(a,Times(b,Power(x,n)))),Times(C1D2,p)),Power(Times(c,Power(Csch(Plus(a,Times(b,Power(x,n)))),p)),CN1D2),Int(Times(Power(x,m),Power(Csch(Plus(a,Times(b,Power(x,n)))),Times(p,q))),x)),And(And(FreeQ(List(a,b,c,m,n,p,q),x),NegativeIntegerQ(Plus(q,C1D2))),Not(OneQ(c,p))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,Power(Sech(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT)),q_)),x_Symbol),
    Condition(Times(Power(Times(c,Power(Sech(Plus(a,Times(b,Power(x,n)))),p)),q),Power(Power(Sech(Plus(a,Times(b,Power(x,n)))),Times(p,q)),-1),Int(Times(Power(x,m),Power(Sech(Plus(a,Times(b,Power(x,n)))),Times(p,q))),x)),And(And(FreeQ(List(a,b,c,m,n,p,q),x),Not(IntegerQ(Plus(q,C1D2)))),Not(OneQ(c,p))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,Power(Csch(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)))),p_DEFAULT)),q_)),x_Symbol),
    Condition(Times(Power(Times(c,Power(Csch(Plus(a,Times(b,Power(x,n)))),p)),q),Power(Power(Csch(Plus(a,Times(b,Power(x,n)))),Times(p,q)),-1),Int(Times(Power(x,m),Power(Csch(Plus(a,Times(b,Power(x,n)))),Times(p,q))),x)),And(And(FreeQ(List(a,b,c,m,n,p,q),x),Not(IntegerQ(Plus(q,C1D2)))),Not(OneQ(c,p))))),
ISetDelayed(Int(Power(Times(c_DEFAULT,Power($($p("F"),v_),p_DEFAULT)),q_),x_Symbol),
    Condition(Int(Power(Times(c,Power(F(ExpandToSum(v,x)),p)),q),x),And(And(And(FreeQ(List(c,p,q),x),HyperbolicQ($s("F"))),BinomialQ(v,x)),Not(BinomialMatchQ(v,x))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Times(c_DEFAULT,Power($($p("F"),v_),p_DEFAULT)),q_)),x_Symbol),
    Condition(Int(Times(Power(x,m),Power(Times(c,Power(F(ExpandToSum(v,x)),p)),q)),x),And(And(And(FreeQ(List(c,m,p,q),x),HyperbolicQ($s("F"))),BinomialQ(v,x)),Not(BinomialMatchQ(v,x)))))
  );
}
