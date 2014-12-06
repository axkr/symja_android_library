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
public class IntRules11 { 
  public static IAST RULES = List( 
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Plus(Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(a_DEFAULT,Power(x_,q_DEFAULT))),p_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(x,m),Power(Times(Plus(a,b),Power(x,n)),p)),x),And(FreeQ(List(a,b,m,n,p),x),ZeroQ(Plus(n,Times(CN1,q)))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Plus(Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(a_DEFAULT,Power(x_,q_DEFAULT))),p_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(x,Plus(m,Times(p,q))),Power(Plus(a,Times(b,Power(x,Plus(n,Times(CN1,q))))),p)),x),And(And(FreeQ(List(a,b,m,n,q),x),IntegerQ(p)),PosQ(Plus(n,Times(CN1,q)))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Plus(Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(a_DEFAULT,Power(x_,q_DEFAULT))),p_)),x_Symbol),
    Condition(Times(Power(x,Plus(m,Times(CN1,n),C1)),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n))),Plus(p,C1)),Power(Times(b,Plus(n,Times(CN1,q)),Plus(p,C1)),CN1)),And(And(And(FreeQ(List(a,b,m,n,p,q),x),Not(IntegerQ(p))),NonzeroQ(Plus(n,Times(CN1,q)))),ZeroQ(Plus(m,Times(p,q),Times(CN1,n),q,C1))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Plus(Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(a_DEFAULT,Power(x_,q_DEFAULT))),p_)),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,Times(CN1,n),C1)),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n))),Plus(p,C1)),Power(Times(b,Plus(m,Times(n,p),C1)),CN1)),Times(CN1,a,Plus(m,Times(p,q),Times(CN1,n),q,C1),Power(Times(b,Plus(m,Times(n,p),C1)),CN1),Int(Times(Power(x,Plus(m,Times(CN1,n),q)),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n))),p)),x))),And(And(And(And(And(FreeQ(List(a,b,m,n,q),x),Not(IntegerQ(p))),PosQ(Plus(n,Times(CN1,q)))),RationalQ(p)),Greater(p,C0)),PositiveIntegerQ(Times(Plus(m,Times(p,q),Times(CN1,n),q,C1),Power(Plus(n,Times(CN1,q)),CN1)))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Plus(Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(a_DEFAULT,Power(x_,q_DEFAULT))),p_)),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,Times(CN1,q),C1)),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n))),Plus(p,C1)),Power(Times(a,Plus(m,Times(p,q),C1)),CN1)),Times(CN1,b,Plus(m,Times(n,p),n,Times(CN1,q),C1),Power(Times(a,Plus(m,Times(p,q),C1)),CN1),Int(Times(Power(x,Plus(m,n,Times(CN1,q))),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n))),p)),x))),And(And(And(And(And(And(FreeQ(List(a,b,m,n,q),x),Not(IntegerQ(p))),PosQ(Plus(n,Times(CN1,q)))),RationalQ(p)),Greater(p,C0)),NonzeroQ(Plus(m,Times(p,q),C1))),NegativeIntegerQ(Plus(m,Times(n,p),n,Times(CN1,q),C1))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Plus(Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(a_DEFAULT,Power(x_,q_DEFAULT))),p_)),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n))),p),Power(Plus(m,Times(p,q),C1),CN1)),Times(CN1,b,Plus(n,Times(CN1,q)),p,Power(Plus(m,Times(p,q),C1),CN1),Int(Times(Power(x,Plus(m,n)),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n))),Plus(p,Times(CN1,C1)))),x))),And(And(And(And(And(FreeQ(List(a,b),x),Not(IntegerQ(p))),RationalQ(m,n,p,q)),Less(q,n)),Greater(p,C0)),Less(Plus(m,Times(p,q),C1),C0)))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Plus(Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(a_DEFAULT,Power(x_,q_DEFAULT))),p_)),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n))),p),Power(Plus(m,Times(n,p),C1),CN1)),Times(a,Plus(n,Times(CN1,q)),p,Power(Plus(m,Times(n,p),C1),CN1),Int(Times(Power(x,Plus(m,q)),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n))),Plus(p,Times(CN1,C1)))),x))),And(And(And(And(And(FreeQ(List(a,b,m,n,q),x),Not(IntegerQ(p))),PosQ(Plus(n,Times(CN1,q)))),RationalQ(p)),Greater(p,C0)),NonzeroQ(Plus(m,Times(n,p),C1))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Plus(Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(a_DEFAULT,Power(x_,q_DEFAULT))),p_)),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,Times(CN1,n),C1)),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n))),Plus(p,C1)),Power(Times(b,Plus(n,Times(CN1,q)),Plus(p,C1)),CN1)),Times(CN1,Plus(m,Times(p,q),Times(CN1,n),q,C1),Power(Times(b,Plus(n,Times(CN1,q)),Plus(p,C1)),CN1),Int(Times(Power(x,Plus(m,Times(CN1,n))),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n))),Plus(p,C1))),x))),And(And(And(And(And(And(FreeQ(List(a,b),x),Not(IntegerQ(p))),RationalQ(m,n,p,q)),Less(q,n)),Less(p,CN1)),Greater(Plus(m,Times(p,q),C1),Plus(n,Times(CN1,q)))),Not(NegativeIntegerQ(Times(Plus(m,Times(n,p),n,Times(CN1,q),C1),Power(Plus(n,Times(CN1,q)),CN1))))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Plus(Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(a_DEFAULT,Power(x_,q_DEFAULT))),p_)),x_Symbol),
    Condition(Plus(Times(CN1,Power(x,Plus(m,Times(CN1,q),C1)),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n))),Plus(p,C1)),Power(Times(a,Plus(n,Times(CN1,q)),Plus(p,C1)),CN1)),Times(Plus(m,Times(n,p),n,Times(CN1,q),C1),Power(Times(a,Plus(n,Times(CN1,q)),Plus(p,C1)),CN1),Int(Times(Power(x,Plus(m,Times(CN1,q))),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n))),Plus(p,C1))),x))),And(And(And(And(FreeQ(List(a,b,m,n,q),x),Not(IntegerQ(p))),PosQ(Plus(n,Times(CN1,q)))),RationalQ(p)),Less(p,CN1)))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Plus(Times(b_DEFAULT,Power(x_,j_DEFAULT)),Times(a_DEFAULT,Power(x_,n_))),CN1D2)),x_Symbol),
    Condition(Times(Power(n,CN1),Subst(Int(Power(Plus(Times(a,x),Times(b,Sqr(x))),CN1D2),x),x,Power(x,n))),And(And(FreeQ(List(a,b,n),x),ZeroQ(Plus(j,Times(CN1,C2,n)))),ZeroQ(Plus(m,Times(CN1,n),C1))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Plus(Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(a_DEFAULT,Power(x_,q_DEFAULT))),CN1D2)),x_Symbol),
    Condition(Times(CN2,Power(Plus(n,Times(CN1,q)),CN1),Subst(Int(Power(Plus(C1,Times(CN1,a,Sqr(x))),CN1),x),x,Times(Power(x,Times(C1D2,q)),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n))),CN1D2)))),And(And(FreeQ(List(a,b,n,q),x),ZeroQ(Plus(m,Times(CN1,C1D2,q),C1))),NonzeroQ(Plus(n,Times(CN1,q)))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Plus(Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(a_DEFAULT,Power(x_,q_DEFAULT))),CN1D2)),x_Symbol),
    Condition(Times(Power(x,Times(C1D2,q)),Sqrt(Plus(a,Times(b,Power(x,Plus(n,Times(CN1,q)))))),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n))),CN1D2),Int(Times(Power(x,Plus(m,Times(CN1,C1D2,q))),Power(Plus(a,Times(b,Power(x,Plus(n,Times(CN1,q))))),CN1D2)),x)),And(And(And(FreeQ(List(a,b,m,n,q),x),PosQ(Plus(n,Times(CN1,q)))),RationalQ(m,n,q)),Or(Or(Or(And(And(Equal(m,C1),Equal(q,C1)),Equal(n,C3)),And(And(Equal(m,C2),Equal(q,C1)),Equal(n,C4))),And(And(Or(Equal(m,C1D2),Equal(m,QQ(3L,2L))),Equal(q,C2)),Equal(n,C4))),And(And(Or(Or(Or(Equal(m,C1),Equal(m,C2)),Equal(m,C1D2)),Equal(m,QQ(5L,2L))),Equal(q,C2)),Equal(n,C5)))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Plus(Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(a_DEFAULT,Power(x_,q_DEFAULT))),p_)),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,Times(CN1,n),C1)),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n))),Plus(p,C1)),Power(Times(b,Plus(m,Times(n,p),C1)),CN1)),Times(CN1,a,Plus(m,Times(p,q),Times(CN1,n),q,C1),Power(Times(b,Plus(m,Times(n,p),C1)),CN1),Int(Times(Power(x,Plus(m,Times(CN1,n),q)),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n))),p)),x))),And(And(And(And(FreeQ(List(a,b),x),RationalQ(m,n,p,q)),Less(q,n)),Less(Less(CN1,p),C0)),Greater(Plus(m,Times(p,q),C1),Plus(n,Times(CN1,q)))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Plus(Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(a_DEFAULT,Power(x_,q_DEFAULT))),p_)),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,Times(CN1,q),C1)),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n))),Plus(p,C1)),Power(Times(a,Plus(m,Times(p,q),C1)),CN1)),Times(CN1,b,Plus(m,Times(n,p),n,Times(CN1,q),C1),Power(Times(a,Plus(m,Times(p,q),C1)),CN1),Int(Times(Power(x,Plus(m,n,Times(CN1,q))),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n))),p)),x))),And(And(And(And(FreeQ(List(a,b),x),RationalQ(m,n,p,q)),Less(q,n)),Less(Less(CN1,p),C0)),Less(Plus(m,Times(p,q),C1),C0)))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Plus(Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(a_DEFAULT,Power(x_,q_DEFAULT))),p_)),x_Symbol),
    Condition(Module(List(Set($s("mn"),Simplify(Plus(m,Times(CN1,n),q)))),Plus(Times(Power(x,Plus($s("mn"),Times(CN1,q),C1)),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n))),Plus(p,C1)),Power(Times(b,Plus(m,Times(n,p),C1)),CN1)),Times(CN1,a,Plus($s("mn"),Times(p,q),C1),Power(Times(b,Plus(m,Times(n,p),C1)),CN1),Int(Times(Power(x,$s("mn")),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n))),p)),x)))),And(And(And(And(FreeQ(List(a,b,m,n,p,q),x),Not(RationalQ(p))),NonzeroQ(Plus(n,Times(CN1,q)))),NonzeroQ(Plus(m,Times(n,p),C1))),SumSimplerQ(m,Times(CN1,Plus(n,Times(CN1,q))))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Plus(Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(a_DEFAULT,Power(x_,q_DEFAULT))),p_)),x_Symbol),
    Condition(Module(List(Set($s("mn"),Simplify(Plus(m,n,Times(CN1,q))))),Plus(Times(Power(x,Plus(m,Times(CN1,q),C1)),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n))),Plus(p,C1)),Power(Times(a,Plus(m,Times(p,q),C1)),CN1)),Times(CN1,b,Plus($s("mn"),Times(n,p),C1),Power(Times(a,Plus(m,Times(p,q),C1)),CN1),Int(Times(Power(x,$s("mn")),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n))),p)),x)))),And(And(And(And(FreeQ(List(a,b,m,n,p,q),x),Not(RationalQ(p))),NonzeroQ(Plus(n,Times(CN1,q)))),NonzeroQ(Plus(m,Times(p,q),C1))),SumSimplerQ(m,Plus(n,Times(CN1,q)))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Plus(Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(a_DEFAULT,Power(x_,q_DEFAULT))),p_)),x_Symbol),
    Condition(Times(Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n))),p),Power(Times(Power(x,Times(p,q)),Power(Plus(a,Times(b,Power(x,Plus(n,Times(CN1,q))))),p)),CN1),Int(Times(Power(x,Plus(m,Times(p,q))),Power(Plus(a,Times(b,Power(x,Plus(n,Times(CN1,q))))),p)),x)),And(And(And(FreeQ(List(a,b,m,n,p,q),x),Not(IntegerQ(p))),NonzeroQ(Plus(n,Times(CN1,q)))),PosQ(Plus(n,Times(CN1,q)))))),
ISetDelayed(Int(Times(Power(u_,m_DEFAULT),Power(Plus(Times(a_DEFAULT,Power(v_,q_DEFAULT)),Times(b_DEFAULT,Power(w_,n_DEFAULT))),p_)),x_Symbol),
    Condition(Times(Power(Coefficient(u,x,C1),CN1),Subst(Int(Times(Power(x,m),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n))),p)),x),x,u)),And(And(And(And(FreeQ(List(a,b,m,n,p,q),x),ZeroQ(Plus(u,Times(CN1,v)))),ZeroQ(Plus(u,Times(CN1,w)))),LinearQ(u,x)),NonzeroQ(Plus(u,Times(CN1,x)))))),
ISetDelayed(Int(Times(Power(u_,p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(x,m),Power(ExpandToSum(u,x),p)),x),And(And(FreeQ(List(m,p),x),GeneralizedBinomialQ(u,x)),Not(GeneralizedBinomialMatchQ(u,x)))))
  );
}
