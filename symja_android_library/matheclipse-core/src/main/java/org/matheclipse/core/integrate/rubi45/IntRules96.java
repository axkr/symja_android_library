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
public class IntRules96 { 
  public static IAST RULES = List( 
ISetDelayed(Int(Times(Power(Csch(Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pn_DEFAULT),Power(Plus(Times(x_,pd_DEFAULT),c_DEFAULT),m_DEFAULT),Power(Sech(Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pn_DEFAULT)),x_Symbol),
    Condition(Times(Power(C2,pn),Int(Times(Power(Plus(c,Times(pd,x)),m),Power(Csch(Plus(Times(C2,a),Times(C2,b,x))),pn)),x)),And(And(FreeQ(List(a,b,c,pd),x),RationalQ(m)),IntegerQ(pn)))),
ISetDelayed(Int(Times(Power(Csch(Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pn_DEFAULT),Power(Plus(Times(x_,pd_DEFAULT),c_DEFAULT),m_DEFAULT),Power(Sech(Plus(Times(b_DEFAULT,x_),a_DEFAULT)),p_DEFAULT)),x_Symbol),
    Condition(Module(List(Set(u,Block(List(Set($s("§showsteps"),False),Set($s("§stepcounter"),Null)),Int(Times(Power(Csch(Plus(a,Times(b,x))),pn),Power(Sech(Plus(a,Times(b,x))),p)),x)))),Plus(Dist(Power(Plus(c,Times(pd,x)),m),u,x),Times(CN1,pd,m,Int(Times(Power(Plus(c,Times(pd,x)),Plus(m,Times(CN1,C1))),u),x)))),And(And(And(And(FreeQ(List(a,b,c,pd),x),IntegersQ(pn,p)),RationalQ(m)),Greater(m,C0)),Unequal(pn,p)))),
ISetDelayed(Int(Times(Power($(pf_,v_),pn_DEFAULT),Power($(pg_,w_),p_DEFAULT),Power(u_,m_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(ExpandToSum(u,x),m),Power($(pf,ExpandToSum(v,x)),pn),Power($(pg,ExpandToSum(v,x)),p)),x),And(And(And(And(And(FreeQ(List(m,pn,p),x),HyperbolicQ(pf)),HyperbolicQ(pg)),ZeroQ(Plus(v,Times(CN1,w)))),LinearQ(List(u,v,w),x)),Not(LinearMatchQ(List(u,v,w),x))))),
ISetDelayed(Int(Times(Cosh(Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),Power(Plus(Times(Sinh(Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_),pn_DEFAULT),Power(Plus(Times(f_DEFAULT,x_),pe_DEFAULT),m_DEFAULT)),x_Symbol),
    Condition(Plus(Times(Power(Plus(pe,Times(f,x)),m),Power(Plus(a,Times(b,Sinh(Plus(c,Times(pd,x))))),Plus(pn,C1)),Power(Times(b,pd,Plus(pn,C1)),CN1)),Times(CN1,f,m,Power(Times(b,pd,Plus(pn,C1)),CN1),Int(Times(Power(Plus(pe,Times(f,x)),Plus(m,Times(CN1,C1))),Power(Plus(a,Times(b,Sinh(Plus(c,Times(pd,x))))),Plus(pn,C1))),x))),And(And(FreeQ(List(a,b,c,pd,pe,f,pn),x),PositiveIntegerQ(m)),NonzeroQ(Plus(pn,C1))))),
ISetDelayed(Int(Times(Power(Plus(Times(Cosh(Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_),pn_DEFAULT),Power(Plus(Times(f_DEFAULT,x_),pe_DEFAULT),m_DEFAULT),Sinh(Plus(Times(x_,pd_DEFAULT),c_DEFAULT))),x_Symbol),
    Condition(Plus(Times(Power(Plus(pe,Times(f,x)),m),Power(Plus(a,Times(b,Cosh(Plus(c,Times(pd,x))))),Plus(pn,C1)),Power(Times(b,pd,Plus(pn,C1)),CN1)),Times(CN1,f,m,Power(Times(b,pd,Plus(pn,C1)),CN1),Int(Times(Power(Plus(pe,Times(f,x)),Plus(m,Times(CN1,C1))),Power(Plus(a,Times(b,Cosh(Plus(c,Times(pd,x))))),Plus(pn,C1))),x))),And(And(FreeQ(List(a,b,c,pd,pe,f,pn),x),PositiveIntegerQ(m)),NonzeroQ(Plus(pn,C1))))),
ISetDelayed(Int(Times(Power(Plus(Times(Tanh(Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_),pn_DEFAULT),Power(Plus(Times(f_DEFAULT,x_),pe_DEFAULT),m_DEFAULT),Sqr(Sech(Plus(Times(x_,pd_DEFAULT),c_DEFAULT)))),x_Symbol),
    Condition(Plus(Times(Power(Plus(pe,Times(f,x)),m),Power(Plus(a,Times(b,Tanh(Plus(c,Times(pd,x))))),Plus(pn,C1)),Power(Times(b,pd,Plus(pn,C1)),CN1)),Times(CN1,f,m,Power(Times(b,pd,Plus(pn,C1)),CN1),Int(Times(Power(Plus(pe,Times(f,x)),Plus(m,Times(CN1,C1))),Power(Plus(a,Times(b,Tanh(Plus(c,Times(pd,x))))),Plus(pn,C1))),x))),And(And(FreeQ(List(a,b,c,pd,pe,f,pn),x),PositiveIntegerQ(m)),NonzeroQ(Plus(pn,C1))))),
ISetDelayed(Int(Times(Sqr(Csch(Plus(Times(x_,pd_DEFAULT),c_DEFAULT))),Power(Plus(Times(Coth(Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_),pn_DEFAULT),Power(Plus(Times(f_DEFAULT,x_),pe_DEFAULT),m_DEFAULT)),x_Symbol),
    Condition(Plus(Times(CN1,Power(Plus(pe,Times(f,x)),m),Power(Plus(a,Times(b,Coth(Plus(c,Times(pd,x))))),Plus(pn,C1)),Power(Times(b,pd,Plus(pn,C1)),CN1)),Times(f,m,Power(Times(b,pd,Plus(pn,C1)),CN1),Int(Times(Power(Plus(pe,Times(f,x)),Plus(m,Times(CN1,C1))),Power(Plus(a,Times(b,Coth(Plus(c,Times(pd,x))))),Plus(pn,C1))),x))),And(And(FreeQ(List(a,b,c,pd,pe,f,pn),x),PositiveIntegerQ(m)),NonzeroQ(Plus(pn,C1))))),
ISetDelayed(Int(Times(Power(Plus(Times(Sech(Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_),pn_DEFAULT),Power(Plus(Times(f_DEFAULT,x_),pe_DEFAULT),m_DEFAULT),Sech(Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),Tanh(Plus(Times(x_,pd_DEFAULT),c_DEFAULT))),x_Symbol),
    Condition(Plus(Times(CN1,Power(Plus(pe,Times(f,x)),m),Power(Plus(a,Times(b,Sech(Plus(c,Times(pd,x))))),Plus(pn,C1)),Power(Times(b,pd,Plus(pn,C1)),CN1)),Times(f,m,Power(Times(b,pd,Plus(pn,C1)),CN1),Int(Times(Power(Plus(pe,Times(f,x)),Plus(m,Times(CN1,C1))),Power(Plus(a,Times(b,Sech(Plus(c,Times(pd,x))))),Plus(pn,C1))),x))),And(And(FreeQ(List(a,b,c,pd,pe,f,pn),x),PositiveIntegerQ(m)),NonzeroQ(Plus(pn,C1))))),
ISetDelayed(Int(Times(Coth(Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),Csch(Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),Power(Plus(Times(Csch(Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_),pn_DEFAULT),Power(Plus(Times(f_DEFAULT,x_),pe_DEFAULT),m_DEFAULT)),x_Symbol),
    Condition(Plus(Times(CN1,Power(Plus(pe,Times(f,x)),m),Power(Plus(a,Times(b,Csch(Plus(c,Times(pd,x))))),Plus(pn,C1)),Power(Times(b,pd,Plus(pn,C1)),CN1)),Times(f,m,Power(Times(b,pd,Plus(pn,C1)),CN1),Int(Times(Power(Plus(pe,Times(f,x)),Plus(m,Times(CN1,C1))),Power(Plus(a,Times(b,Csch(Plus(c,Times(pd,x))))),Plus(pn,C1))),x))),And(And(FreeQ(List(a,b,c,pd,pe,f,pn),x),PositiveIntegerQ(m)),NonzeroQ(Plus(pn,C1))))),
ISetDelayed(Int(Times(Power(Plus(Times(f_DEFAULT,x_),pe_DEFAULT),m_DEFAULT),Power(Sinh(Plus(Times(b_DEFAULT,x_),a_DEFAULT)),p_DEFAULT),Power(Sinh(Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),q_DEFAULT)),x_Symbol),
    Condition(Int(ExpandTrigReduce(Power(Plus(pe,Times(f,x)),m),Times(Power(Sinh(Plus(a,Times(b,x))),p),Power(Sinh(Plus(c,Times(pd,x))),q)),x),x),And(And(FreeQ(List(a,b,c,pd,pe,f),x),PositiveIntegerQ(p,q)),IntegerQ(m)))),
ISetDelayed(Int(Times(Power(Cosh(Plus(Times(b_DEFAULT,x_),a_DEFAULT)),p_DEFAULT),Power(Cosh(Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),q_DEFAULT),Power(Plus(Times(f_DEFAULT,x_),pe_DEFAULT),m_DEFAULT)),x_Symbol),
    Condition(Int(ExpandTrigReduce(Power(Plus(pe,Times(f,x)),m),Times(Power(Cosh(Plus(a,Times(b,x))),p),Power(Cosh(Plus(c,Times(pd,x))),q)),x),x),And(And(FreeQ(List(a,b,c,pd,pe,f),x),PositiveIntegerQ(p,q)),IntegerQ(m)))),
ISetDelayed(Int(Times(Power(Cosh(Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),q_DEFAULT),Power(Plus(Times(f_DEFAULT,x_),pe_DEFAULT),m_DEFAULT),Power(Sinh(Plus(Times(b_DEFAULT,x_),a_DEFAULT)),p_DEFAULT)),x_Symbol),
    Condition(Int(ExpandTrigReduce(Power(Plus(pe,Times(f,x)),m),Times(Power(Sinh(Plus(a,Times(b,x))),p),Power(Cosh(Plus(c,Times(pd,x))),q)),x),x),And(FreeQ(List(a,b,c,pd,pe,f,m),x),PositiveIntegerQ(p,q)))),
ISetDelayed(Int(Times(Power(Plus(Times(f_DEFAULT,x_),pe_DEFAULT),m_DEFAULT),Power($(pf_,Plus(Times(b_DEFAULT,x_),a_DEFAULT)),p_DEFAULT),Power($(pg_,Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),q_DEFAULT)),x_Symbol),
    Condition(Int(ExpandTrigExpand(Times(Power(Plus(pe,Times(f,x)),m),Power($(pg,Plus(c,Times(pd,x))),q)),pf,Plus(c,Times(pd,x)),p,Times(b,Power(pd,CN1)),x),x),And(And(And(And(And(FreeQ(List(a,b,c,pd,pe,f,m),x),MemberQ(List($s("Sinh"),$s("Cosh")),pf)),MemberQ(List($s("Sech"),$s("Csch")),pg)),PositiveIntegerQ(p,q)),ZeroQ(Plus(Times(b,c),Times(CN1,a,pd)))),PositiveIntegerQ(Plus(Times(b,Power(pd,CN1)),Times(CN1,C1))))))
  );
}
