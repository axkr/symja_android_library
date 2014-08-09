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
public class IntRules89 { 
  public static IAST RULES = List( 
ISetDelayed(Int(Times(Csc(Plus(Times(b_DEFAULT,x_),a_DEFAULT)),Power(Cot(Plus(Times(b_DEFAULT,x_),a_DEFAULT)),p_),Power(Plus(Times(x_,pd_DEFAULT),c_DEFAULT),m_DEFAULT)),x_Symbol),
    Condition(Plus(Times(CN1,Int(Times(Power(Plus(c,Times(pd,x)),m),Csc(Plus(a,Times(b,x))),Power(Cot(Plus(a,Times(b,x))),Plus(p,Times(CN1,C2)))),x)),Int(Times(Power(Plus(c,Times(pd,x)),m),Power(Csc(Plus(a,Times(b,x))),C3),Power(Cot(Plus(a,Times(b,x))),Plus(p,Times(CN1,C2)))),x)),And(FreeQ(List(a,b,c,pd,m),x),PositiveIntegerQ(Times(C1D2,p))))),
ISetDelayed(Int(Times(Power(Cot(Plus(Times(b_DEFAULT,x_),a_DEFAULT)),p_),Power(Csc(Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pn_DEFAULT),Power(Plus(Times(x_,pd_DEFAULT),c_DEFAULT),m_DEFAULT)),x_Symbol),
    Condition(Plus(Times(CN1,Int(Times(Power(Plus(c,Times(pd,x)),m),Power(Csc(Plus(a,Times(b,x))),pn),Power(Cot(Plus(a,Times(b,x))),Plus(p,Times(CN1,C2)))),x)),Int(Times(Power(Plus(c,Times(pd,x)),m),Power(Csc(Plus(a,Times(b,x))),Plus(pn,C2)),Power(Cot(Plus(a,Times(b,x))),Plus(p,Times(CN1,C2)))),x)),And(FreeQ(List(a,b,c,pd,m,pn),x),PositiveIntegerQ(Times(C1D2,p))))),
ISetDelayed(Int(Times(Power(Plus(Times(x_,pd_DEFAULT),c_DEFAULT),m_DEFAULT),Power(Sec(Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pn_DEFAULT),Power(Tan(Plus(Times(b_DEFAULT,x_),a_DEFAULT)),p_DEFAULT)),x_Symbol),
    Condition(Module(List(Set(u,Block(List(Set($s("§showsteps"),False),Set($s("§stepcounter"),Null)),Int(Times(Power(Sec(Plus(a,Times(b,x))),pn),Power(Tan(Plus(a,Times(b,x))),p)),x)))),Plus(Dist(Power(Plus(c,Times(pd,x)),m),u,x),Times(CN1,pd,m,Int(Times(Power(Plus(c,Times(pd,x)),Plus(m,Times(CN1,C1))),u),x)))),And(And(FreeQ(List(a,b,c,pd,pn,p),x),PositiveIntegerQ(m)),Or(EvenQ(pn),OddQ(p))))),
ISetDelayed(Int(Times(Power(Cot(Plus(Times(b_DEFAULT,x_),a_DEFAULT)),p_DEFAULT),Power(Csc(Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pn_DEFAULT),Power(Plus(Times(x_,pd_DEFAULT),c_DEFAULT),m_DEFAULT)),x_Symbol),
    Condition(Module(List(Set(u,Block(List(Set($s("§showsteps"),False),Set($s("§stepcounter"),Null)),Int(Times(Power(Csc(Plus(a,Times(b,x))),pn),Power(Cot(Plus(a,Times(b,x))),p)),x)))),Plus(Dist(Power(Plus(c,Times(pd,x)),m),u,x),Times(CN1,pd,m,Int(Times(Power(Plus(c,Times(pd,x)),Plus(m,Times(CN1,C1))),u),x)))),And(And(FreeQ(List(a,b,c,pd,pn,p),x),PositiveIntegerQ(m)),Or(EvenQ(pn),OddQ(p))))),
ISetDelayed(Int(Times(Power(Csc(Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pn_DEFAULT),Power(Plus(Times(x_,pd_DEFAULT),c_DEFAULT),m_DEFAULT),Power(Sec(Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pn_DEFAULT)),x_Symbol),
    Condition(Times(Power(C2,pn),Int(Times(Power(Plus(c,Times(pd,x)),m),Power(Csc(Plus(Times(C2,a),Times(C2,b,x))),pn)),x)),And(And(FreeQ(List(a,b,c,pd),x),RationalQ(m)),IntegerQ(pn)))),
ISetDelayed(Int(Times(Power(Csc(Plus(Times(b_DEFAULT,x_),a_DEFAULT)),pn_DEFAULT),Power(Plus(Times(x_,pd_DEFAULT),c_DEFAULT),m_DEFAULT),Power(Sec(Plus(Times(b_DEFAULT,x_),a_DEFAULT)),p_DEFAULT)),x_Symbol),
    Condition(Module(List(Set(u,Block(List(Set($s("§showsteps"),False),Set($s("§stepcounter"),Null)),Int(Times(Power(Csc(Plus(a,Times(b,x))),pn),Power(Sec(Plus(a,Times(b,x))),p)),x)))),Plus(Dist(Power(Plus(c,Times(pd,x)),m),u,x),Times(CN1,pd,m,Int(Times(Power(Plus(c,Times(pd,x)),Plus(m,Times(CN1,C1))),u),x)))),And(And(And(And(FreeQ(List(a,b,c,pd),x),IntegersQ(pn,p)),RationalQ(m)),Greater(m,C0)),Unequal(pn,p)))),
ISetDelayed(Int(Times(Power($(pf_,v_),pn_DEFAULT),Power($(pg_,w_),p_DEFAULT),Power(u_,m_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(ExpandToSum(u,x),m),Power($(pf,ExpandToSum(v,x)),pn),Power($(pg,ExpandToSum(v,x)),p)),x),And(And(And(And(And(FreeQ(List(m,pn,p),x),TrigQ(pf)),TrigQ(pg)),ZeroQ(Plus(v,Times(CN1,w)))),LinearQ(List(u,v,w),x)),Not(LinearMatchQ(List(u,v,w),x))))),
ISetDelayed(Int(Times(Cos(Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),Power(Plus(Times(Sin(Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_),pn_DEFAULT),Power(Plus(Times(f_DEFAULT,x_),pe_DEFAULT),m_DEFAULT)),x_Symbol),
    Condition(Plus(Times(Power(Plus(pe,Times(f,x)),m),Power(Plus(a,Times(b,Sin(Plus(c,Times(pd,x))))),Plus(pn,C1)),Power(Times(b,pd,Plus(pn,C1)),CN1)),Times(CN1,f,m,Power(Times(b,pd,Plus(pn,C1)),CN1),Int(Times(Power(Plus(pe,Times(f,x)),Plus(m,Times(CN1,C1))),Power(Plus(a,Times(b,Sin(Plus(c,Times(pd,x))))),Plus(pn,C1))),x))),And(And(FreeQ(List(a,b,c,pd,pe,f,pn),x),PositiveIntegerQ(m)),NonzeroQ(Plus(pn,C1))))),
ISetDelayed(Int(Times(Power(Plus(Times(Cos(Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_),pn_DEFAULT),Power(Plus(Times(f_DEFAULT,x_),pe_DEFAULT),m_DEFAULT),Sin(Plus(Times(x_,pd_DEFAULT),c_DEFAULT))),x_Symbol),
    Condition(Plus(Times(CN1,Power(Plus(pe,Times(f,x)),m),Power(Plus(a,Times(b,Cos(Plus(c,Times(pd,x))))),Plus(pn,C1)),Power(Times(b,pd,Plus(pn,C1)),CN1)),Times(f,m,Power(Times(b,pd,Plus(pn,C1)),CN1),Int(Times(Power(Plus(pe,Times(f,x)),Plus(m,Times(CN1,C1))),Power(Plus(a,Times(b,Cos(Plus(c,Times(pd,x))))),Plus(pn,C1))),x))),And(And(FreeQ(List(a,b,c,pd,pe,f,pn),x),PositiveIntegerQ(m)),NonzeroQ(Plus(pn,C1))))),
ISetDelayed(Int(Times(Power(Plus(Times(Tan(Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_),pn_DEFAULT),Power(Plus(Times(f_DEFAULT,x_),pe_DEFAULT),m_DEFAULT),Sqr(Sec(Plus(Times(x_,pd_DEFAULT),c_DEFAULT)))),x_Symbol),
    Condition(Plus(Times(Power(Plus(pe,Times(f,x)),m),Power(Plus(a,Times(b,Tan(Plus(c,Times(pd,x))))),Plus(pn,C1)),Power(Times(b,pd,Plus(pn,C1)),CN1)),Times(CN1,f,m,Power(Times(b,pd,Plus(pn,C1)),CN1),Int(Times(Power(Plus(pe,Times(f,x)),Plus(m,Times(CN1,C1))),Power(Plus(a,Times(b,Tan(Plus(c,Times(pd,x))))),Plus(pn,C1))),x))),And(And(FreeQ(List(a,b,c,pd,pe,f,pn),x),PositiveIntegerQ(m)),NonzeroQ(Plus(pn,C1))))),
ISetDelayed(Int(Times(Sqr(Csc(Plus(Times(x_,pd_DEFAULT),c_DEFAULT))),Power(Plus(Times(Cot(Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_),pn_DEFAULT),Power(Plus(Times(f_DEFAULT,x_),pe_DEFAULT),m_DEFAULT)),x_Symbol),
    Condition(Plus(Times(CN1,Power(Plus(pe,Times(f,x)),m),Power(Plus(a,Times(b,Cot(Plus(c,Times(pd,x))))),Plus(pn,C1)),Power(Times(b,pd,Plus(pn,C1)),CN1)),Times(f,m,Power(Times(b,pd,Plus(pn,C1)),CN1),Int(Times(Power(Plus(pe,Times(f,x)),Plus(m,Times(CN1,C1))),Power(Plus(a,Times(b,Cot(Plus(c,Times(pd,x))))),Plus(pn,C1))),x))),And(And(FreeQ(List(a,b,c,pd,pe,f,pn),x),PositiveIntegerQ(m)),NonzeroQ(Plus(pn,C1))))),
ISetDelayed(Int(Times(Power(Plus(Times(Sec(Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_),pn_DEFAULT),Power(Plus(Times(f_DEFAULT,x_),pe_DEFAULT),m_DEFAULT),Sec(Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),Tan(Plus(Times(x_,pd_DEFAULT),c_DEFAULT))),x_Symbol),
    Condition(Plus(Times(Power(Plus(pe,Times(f,x)),m),Power(Plus(a,Times(b,Sec(Plus(c,Times(pd,x))))),Plus(pn,C1)),Power(Times(b,pd,Plus(pn,C1)),CN1)),Times(CN1,f,m,Power(Times(b,pd,Plus(pn,C1)),CN1),Int(Times(Power(Plus(pe,Times(f,x)),Plus(m,Times(CN1,C1))),Power(Plus(a,Times(b,Sec(Plus(c,Times(pd,x))))),Plus(pn,C1))),x))),And(And(FreeQ(List(a,b,c,pd,pe,f,pn),x),PositiveIntegerQ(m)),NonzeroQ(Plus(pn,C1))))),
ISetDelayed(Int(Times(Cot(Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),Csc(Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),Power(Plus(Times(Csc(Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_),pn_DEFAULT),Power(Plus(Times(f_DEFAULT,x_),pe_DEFAULT),m_DEFAULT)),x_Symbol),
    Condition(Plus(Times(CN1,Power(Plus(pe,Times(f,x)),m),Power(Plus(a,Times(b,Csc(Plus(c,Times(pd,x))))),Plus(pn,C1)),Power(Times(b,pd,Plus(pn,C1)),CN1)),Times(f,m,Power(Times(b,pd,Plus(pn,C1)),CN1),Int(Times(Power(Plus(pe,Times(f,x)),Plus(m,Times(CN1,C1))),Power(Plus(a,Times(b,Csc(Plus(c,Times(pd,x))))),Plus(pn,C1))),x))),And(And(FreeQ(List(a,b,c,pd,pe,f,pn),x),PositiveIntegerQ(m)),NonzeroQ(Plus(pn,C1))))),
ISetDelayed(Int(Times(Power(Plus(Times(f_DEFAULT,x_),pe_DEFAULT),m_DEFAULT),Power(Sin(Plus(Times(b_DEFAULT,x_),a_DEFAULT)),p_DEFAULT),Power(Sin(Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),q_DEFAULT)),x_Symbol),
    Condition(Int(ExpandTrigReduce(Power(Plus(pe,Times(f,x)),m),Times(Power(Sin(Plus(a,Times(b,x))),p),Power(Sin(Plus(c,Times(pd,x))),q)),x),x),And(And(FreeQ(List(a,b,c,pd,pe,f),x),PositiveIntegerQ(p,q)),IntegerQ(m)))),
ISetDelayed(Int(Times(Power(Cos(Plus(Times(b_DEFAULT,x_),a_DEFAULT)),p_DEFAULT),Power(Cos(Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),q_DEFAULT),Power(Plus(Times(f_DEFAULT,x_),pe_DEFAULT),m_DEFAULT)),x_Symbol),
    Condition(Int(ExpandTrigReduce(Power(Plus(pe,Times(f,x)),m),Times(Power(Cos(Plus(a,Times(b,x))),p),Power(Cos(Plus(c,Times(pd,x))),q)),x),x),And(And(FreeQ(List(a,b,c,pd,pe,f),x),PositiveIntegerQ(p,q)),IntegerQ(m)))),
ISetDelayed(Int(Times(Power(Cos(Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),q_DEFAULT),Power(Plus(Times(f_DEFAULT,x_),pe_DEFAULT),m_DEFAULT),Power(Sin(Plus(Times(b_DEFAULT,x_),a_DEFAULT)),p_DEFAULT)),x_Symbol),
    Condition(Int(ExpandTrigReduce(Power(Plus(pe,Times(f,x)),m),Times(Power(Sin(Plus(a,Times(b,x))),p),Power(Cos(Plus(c,Times(pd,x))),q)),x),x),And(FreeQ(List(a,b,c,pd,pe,f,m),x),PositiveIntegerQ(p,q)))),
ISetDelayed(Int(Times(Power(Plus(Times(f_DEFAULT,x_),pe_DEFAULT),m_DEFAULT),Power($(pf_,Plus(Times(b_DEFAULT,x_),a_DEFAULT)),p_DEFAULT),Power($(pg_,Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),q_DEFAULT)),x_Symbol),
    Condition(Int(ExpandTrigExpand(Times(Power(Plus(pe,Times(f,x)),m),Power($(pg,Plus(c,Times(pd,x))),q)),pf,Plus(c,Times(pd,x)),p,Times(b,Power(pd,CN1)),x),x),And(And(And(And(And(FreeQ(List(a,b,c,pd,pe,f,m),x),MemberQ(List($s("Sin"),$s("Cos")),pf)),MemberQ(List($s("Sec"),$s("Csc")),pg)),PositiveIntegerQ(p,q)),ZeroQ(Plus(Times(b,c),Times(CN1,a,pd)))),PositiveIntegerQ(Plus(Times(b,Power(pd,CN1)),Times(CN1,C1))))))
  );
}
