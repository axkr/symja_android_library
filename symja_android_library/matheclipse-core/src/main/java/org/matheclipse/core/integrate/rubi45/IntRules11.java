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
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Plus(Times(a_DEFAULT,Power(x_,q_DEFAULT)),Times(b_DEFAULT,Power(x_,pn_DEFAULT))),p_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(x,m),Power(Times(Plus(a,b),Power(x,pn)),p)),x),And(FreeQ(List(a,b,m,pn,p),x),ZeroQ(Plus(pn,Times(CN1,q)))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Plus(Times(a_DEFAULT,Power(x_,q_DEFAULT)),Times(b_DEFAULT,Power(x_,pn_DEFAULT))),p_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(x,Plus(m,Times(p,q))),Power(Plus(a,Times(b,Power(x,Plus(pn,Times(CN1,q))))),p)),x),And(And(FreeQ(List(a,b,m,pn,q),x),IntegerQ(p)),PosQ(Plus(pn,Times(CN1,q)))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Plus(Times(a_DEFAULT,Power(x_,q_DEFAULT)),Times(b_DEFAULT,Power(x_,pn_DEFAULT))),p_)),x_Symbol),
    Condition(Times(Power(x,Plus(m,Times(CN1,pn),C1)),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,pn))),Plus(p,C1)),Power(Times(b,Plus(pn,Times(CN1,q)),Plus(p,C1)),CN1)),And(And(And(FreeQ(List(a,b,m,pn,p,q),x),Not(IntegerQ(p))),NonzeroQ(Plus(pn,Times(CN1,q)))),ZeroQ(Plus(m,Times(p,q),Times(CN1,pn),q,C1))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Plus(Times(a_DEFAULT,Power(x_,q_DEFAULT)),Times(b_DEFAULT,Power(x_,pn_DEFAULT))),p_)),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,Times(CN1,pn),C1)),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,pn))),Plus(p,C1)),Power(Times(b,Plus(m,Times(pn,p),C1)),CN1)),Times(CN1,a,Plus(m,Times(p,q),Times(CN1,pn),q,C1),Power(Times(b,Plus(m,Times(pn,p),C1)),CN1),Int(Times(Power(x,Plus(m,Times(CN1,pn),q)),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,pn))),p)),x))),And(And(And(And(And(FreeQ(List(a,b,m,pn,q),x),Not(IntegerQ(p))),PosQ(Plus(pn,Times(CN1,q)))),RationalQ(p)),Greater(p,C0)),PositiveIntegerQ(Times(Plus(m,Times(p,q),Times(CN1,pn),q,C1),Power(Plus(pn,Times(CN1,q)),CN1)))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Plus(Times(a_DEFAULT,Power(x_,q_DEFAULT)),Times(b_DEFAULT,Power(x_,pn_DEFAULT))),p_)),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,Times(CN1,q),C1)),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,pn))),Plus(p,C1)),Power(Times(a,Plus(m,Times(p,q),C1)),CN1)),Times(CN1,b,Plus(m,Times(pn,p),pn,Times(CN1,q),C1),Power(Times(a,Plus(m,Times(p,q),C1)),CN1),Int(Times(Power(x,Plus(m,pn,Times(CN1,q))),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,pn))),p)),x))),And(And(And(And(And(And(FreeQ(List(a,b,m,pn,q),x),Not(IntegerQ(p))),PosQ(Plus(pn,Times(CN1,q)))),RationalQ(p)),Greater(p,C0)),NonzeroQ(Plus(m,Times(p,q),C1))),NegativeIntegerQ(Plus(m,Times(pn,p),pn,Times(CN1,q),C1))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Plus(Times(a_DEFAULT,Power(x_,q_DEFAULT)),Times(b_DEFAULT,Power(x_,pn_DEFAULT))),p_)),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,pn))),p),Power(Plus(m,Times(p,q),C1),CN1)),Times(CN1,b,Plus(pn,Times(CN1,q)),p,Power(Plus(m,Times(p,q),C1),CN1),Int(Times(Power(x,Plus(m,pn)),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,pn))),Plus(p,Times(CN1,C1)))),x))),And(And(And(And(And(FreeQ(List(a,b),x),Not(IntegerQ(p))),RationalQ(m,pn,p,q)),Less(q,pn)),Greater(p,C0)),Less(Plus(m,Times(p,q),C1),C0)))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Plus(Times(a_DEFAULT,Power(x_,q_DEFAULT)),Times(b_DEFAULT,Power(x_,pn_DEFAULT))),p_)),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,C1)),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,pn))),p),Power(Plus(m,Times(pn,p),C1),CN1)),Times(a,Plus(pn,Times(CN1,q)),p,Power(Plus(m,Times(pn,p),C1),CN1),Int(Times(Power(x,Plus(m,q)),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,pn))),Plus(p,Times(CN1,C1)))),x))),And(And(And(And(And(FreeQ(List(a,b,m,pn,q),x),Not(IntegerQ(p))),PosQ(Plus(pn,Times(CN1,q)))),RationalQ(p)),Greater(p,C0)),NonzeroQ(Plus(m,Times(pn,p),C1))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Plus(Times(a_DEFAULT,Power(x_,q_DEFAULT)),Times(b_DEFAULT,Power(x_,pn_DEFAULT))),p_)),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,Times(CN1,pn),C1)),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,pn))),Plus(p,C1)),Power(Times(b,Plus(pn,Times(CN1,q)),Plus(p,C1)),CN1)),Times(CN1,Plus(m,Times(p,q),Times(CN1,pn),q,C1),Power(Times(b,Plus(pn,Times(CN1,q)),Plus(p,C1)),CN1),Int(Times(Power(x,Plus(m,Times(CN1,pn))),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,pn))),Plus(p,C1))),x))),And(And(And(And(And(And(FreeQ(List(a,b),x),Not(IntegerQ(p))),RationalQ(m,pn,p,q)),Less(q,pn)),Less(p,CN1)),Greater(Plus(m,Times(p,q),C1),Plus(pn,Times(CN1,q)))),Not(NegativeIntegerQ(Times(Plus(m,Times(pn,p),pn,Times(CN1,q),C1),Power(Plus(pn,Times(CN1,q)),CN1))))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Plus(Times(a_DEFAULT,Power(x_,q_DEFAULT)),Times(b_DEFAULT,Power(x_,pn_DEFAULT))),p_)),x_Symbol),
    Condition(Plus(Times(CN1,Power(x,Plus(m,Times(CN1,q),C1)),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,pn))),Plus(p,C1)),Power(Times(a,Plus(pn,Times(CN1,q)),Plus(p,C1)),CN1)),Times(Plus(m,Times(pn,p),pn,Times(CN1,q),C1),Power(Times(a,Plus(pn,Times(CN1,q)),Plus(p,C1)),CN1),Int(Times(Power(x,Plus(m,Times(CN1,q))),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,pn))),Plus(p,C1))),x))),And(And(And(And(FreeQ(List(a,b,m,pn,q),x),Not(IntegerQ(p))),PosQ(Plus(pn,Times(CN1,q)))),RationalQ(p)),Less(p,CN1)))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Plus(Times(b_DEFAULT,Power(x_,j_DEFAULT)),Times(a_DEFAULT,Power(x_,pn_))),CN1D2)),x_Symbol),
    Condition(Times(Power(pn,CN1),Subst(Int(Power(Plus(Times(a,x),Times(b,Sqr(x))),CN1D2),x),x,Power(x,pn))),And(And(FreeQ(List(a,b,pn),x),ZeroQ(Plus(j,Times(CN1,C2,pn)))),ZeroQ(Plus(m,Times(CN1,pn),C1))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Plus(Times(a_DEFAULT,Power(x_,q_DEFAULT)),Times(b_DEFAULT,Power(x_,pn_DEFAULT))),CN1D2)),x_Symbol),
    Condition(Times(CN2,Power(Plus(pn,Times(CN1,q)),CN1),Subst(Int(Power(Plus(C1,Times(CN1,a,Sqr(x))),CN1),x),x,Times(Power(x,Times(C1D2,q)),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,pn))),CN1D2)))),And(And(FreeQ(List(a,b,pn,q),x),ZeroQ(Plus(m,Times(CN1,C1D2,q),C1))),NonzeroQ(Plus(pn,Times(CN1,q)))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Plus(Times(a_DEFAULT,Power(x_,q_DEFAULT)),Times(b_DEFAULT,Power(x_,pn_DEFAULT))),CN1D2)),x_Symbol),
    Condition(Times(Power(x,Times(C1D2,q)),Sqrt(Plus(a,Times(b,Power(x,Plus(pn,Times(CN1,q)))))),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,pn))),CN1D2),Int(Times(Power(x,Plus(m,Times(CN1,C1D2,q))),Power(Plus(a,Times(b,Power(x,Plus(pn,Times(CN1,q))))),CN1D2)),x)),And(And(And(FreeQ(List(a,b,m,pn,q),x),PosQ(Plus(pn,Times(CN1,q)))),RationalQ(m,pn,q)),Or(Or(Or(And(And(Equal(m,C1),Equal(q,C1)),Equal(pn,C3)),And(And(Equal(m,C2),Equal(q,C1)),Equal(pn,C4))),And(And(Or(Equal(m,C1D2),Equal(m,QQ(3L,2L))),Equal(q,C2)),Equal(pn,C4))),And(And(Or(Or(Or(Equal(m,C1),Equal(m,C2)),Equal(m,C1D2)),Equal(m,QQ(5L,2L))),Equal(q,C2)),Equal(pn,C5)))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Plus(Times(a_DEFAULT,Power(x_,q_DEFAULT)),Times(b_DEFAULT,Power(x_,pn_DEFAULT))),p_)),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,Times(CN1,pn),C1)),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,pn))),Plus(p,C1)),Power(Times(b,Plus(m,Times(pn,p),C1)),CN1)),Times(CN1,a,Plus(m,Times(p,q),Times(CN1,pn),q,C1),Power(Times(b,Plus(m,Times(pn,p),C1)),CN1),Int(Times(Power(x,Plus(m,Times(CN1,pn),q)),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,pn))),p)),x))),And(And(And(And(FreeQ(List(a,b),x),RationalQ(m,pn,p,q)),Less(q,pn)),Less(Less(CN1,p),C0)),Greater(Plus(m,Times(p,q),C1),Plus(pn,Times(CN1,q)))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Plus(Times(a_DEFAULT,Power(x_,q_DEFAULT)),Times(b_DEFAULT,Power(x_,pn_DEFAULT))),p_)),x_Symbol),
    Condition(Plus(Times(Power(x,Plus(m,Times(CN1,q),C1)),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,pn))),Plus(p,C1)),Power(Times(a,Plus(m,Times(p,q),C1)),CN1)),Times(CN1,b,Plus(m,Times(pn,p),pn,Times(CN1,q),C1),Power(Times(a,Plus(m,Times(p,q),C1)),CN1),Int(Times(Power(x,Plus(m,pn,Times(CN1,q))),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,pn))),p)),x))),And(And(And(And(FreeQ(List(a,b),x),RationalQ(m,pn,p,q)),Less(q,pn)),Less(Less(CN1,p),C0)),Less(Plus(m,Times(p,q),C1),C0)))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Plus(Times(a_DEFAULT,Power(x_,q_DEFAULT)),Times(b_DEFAULT,Power(x_,pn_DEFAULT))),p_)),x_Symbol),
    Condition(Module(List(Set($s("mn"),Simplify(Plus(m,Times(CN1,pn),q)))),Plus(Times(Power(x,Plus($s("mn"),Times(CN1,q),C1)),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,pn))),Plus(p,C1)),Power(Times(b,Plus(m,Times(pn,p),C1)),CN1)),Times(CN1,a,Plus($s("mn"),Times(p,q),C1),Power(Times(b,Plus(m,Times(pn,p),C1)),CN1),Int(Times(Power(x,$s("mn")),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,pn))),p)),x)))),And(And(And(And(FreeQ(List(a,b,m,pn,p,q),x),Not(RationalQ(p))),NonzeroQ(Plus(pn,Times(CN1,q)))),NonzeroQ(Plus(m,Times(pn,p),C1))),SumSimplerQ(m,Times(CN1,Plus(pn,Times(CN1,q))))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Plus(Times(a_DEFAULT,Power(x_,q_DEFAULT)),Times(b_DEFAULT,Power(x_,pn_DEFAULT))),p_)),x_Symbol),
    Condition(Module(List(Set($s("mn"),Simplify(Plus(m,pn,Times(CN1,q))))),Plus(Times(Power(x,Plus(m,Times(CN1,q),C1)),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,pn))),Plus(p,C1)),Power(Times(a,Plus(m,Times(p,q),C1)),CN1)),Times(CN1,b,Plus($s("mn"),Times(pn,p),C1),Power(Times(a,Plus(m,Times(p,q),C1)),CN1),Int(Times(Power(x,$s("mn")),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,pn))),p)),x)))),And(And(And(And(FreeQ(List(a,b,m,pn,p,q),x),Not(RationalQ(p))),NonzeroQ(Plus(pn,Times(CN1,q)))),NonzeroQ(Plus(m,Times(p,q),C1))),SumSimplerQ(m,Plus(pn,Times(CN1,q)))))),
ISetDelayed(Int(Times(Power(x_,m_DEFAULT),Power(Plus(Times(a_DEFAULT,Power(x_,q_DEFAULT)),Times(b_DEFAULT,Power(x_,pn_DEFAULT))),p_)),x_Symbol),
    Condition(Times(Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,pn))),p),Power(Times(Power(x,Times(p,q)),Power(Plus(a,Times(b,Power(x,Plus(pn,Times(CN1,q))))),p)),CN1),Int(Times(Power(x,Plus(m,Times(p,q))),Power(Plus(a,Times(b,Power(x,Plus(pn,Times(CN1,q))))),p)),x)),And(And(And(FreeQ(List(a,b,m,pn,p,q),x),Not(IntegerQ(p))),NonzeroQ(Plus(pn,Times(CN1,q)))),PosQ(Plus(pn,Times(CN1,q)))))),
ISetDelayed(Int(Times(Power(u_,m_DEFAULT),Power(Plus(Times(a_DEFAULT,Power(v_,q_DEFAULT)),Times(b_DEFAULT,Power(w_,pn_DEFAULT))),p_)),x_Symbol),
    Condition(Times(Power(Coefficient(u,x,C1),CN1),Subst(Int(Times(Power(x,m),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,pn))),p)),x),x,u)),And(And(And(And(FreeQ(List(a,b,m,pn,p,q),x),ZeroQ(Plus(u,Times(CN1,v)))),ZeroQ(Plus(u,Times(CN1,w)))),LinearQ(u,x)),NonzeroQ(Plus(u,Times(CN1,x)))))),
ISetDelayed(Int(Times(Power(u_,p_DEFAULT),Power(x_,m_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(x,m),Power(ExpandToSum(u,x),p)),x),And(And(FreeQ(List(m,p),x),GeneralizedBinomialQ(u,x)),Not(GeneralizedBinomialMatchQ(u,x)))))
  );
}
