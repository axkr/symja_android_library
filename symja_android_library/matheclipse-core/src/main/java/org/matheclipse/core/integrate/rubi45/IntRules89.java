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
ISetDelayed(Int(Times(Csc(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),Power(Plus(c_DEFAULT,Times($p(d,true),x_)),m_DEFAULT),Power(Cot(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_)),x_Symbol),
    Condition(Plus(Times(CN1,Int(Times(Power(Plus(c,Times(d,x)),m),Csc(Plus(a,Times(b,x))),Power(Cot(Plus(a,Times(b,x))),Plus(p,Times(CN1,C2)))),x)),Int(Times(Power(Plus(c,Times(d,x)),m),Power(Csc(Plus(a,Times(b,x))),C3),Power(Cot(Plus(a,Times(b,x))),Plus(p,Times(CN1,C2)))),x)),And(FreeQ(List(a,b,c,d,m),x),PositiveIntegerQ(Times(C1D2,p))))),
ISetDelayed(Int(Times(Power(Csc(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Plus(c_DEFAULT,Times($p(d,true),x_)),m_DEFAULT),Power(Cot(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_)),x_Symbol),
    Condition(Plus(Times(CN1,Int(Times(Power(Plus(c,Times(d,x)),m),Power(Csc(Plus(a,Times(b,x))),n),Power(Cot(Plus(a,Times(b,x))),Plus(p,Times(CN1,C2)))),x)),Int(Times(Power(Plus(c,Times(d,x)),m),Power(Csc(Plus(a,Times(b,x))),Plus(n,C2)),Power(Cot(Plus(a,Times(b,x))),Plus(p,Times(CN1,C2)))),x)),And(FreeQ(List(a,b,c,d,m,n),x),PositiveIntegerQ(Times(C1D2,p))))),
ISetDelayed(Int(Times(Power(Plus(c_DEFAULT,Times($p(d,true),x_)),m_DEFAULT),Power(Sec(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Tan(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT)),x_Symbol),
    Condition(Module(List(Set(u,Block(List(Set($s("§showsteps"),False),Set($s("§stepcounter"),Null)),Int(Times(Power(Sec(Plus(a,Times(b,x))),n),Power(Tan(Plus(a,Times(b,x))),p)),x)))),Plus(Dist(Power(Plus(c,Times(d,x)),m),u,x),Times(CN1,d,m,Int(Times(Power(Plus(c,Times(d,x)),Plus(m,Times(CN1,C1))),u),x)))),And(And(FreeQ(List(a,b,c,d,n,p),x),PositiveIntegerQ(m)),Or(EvenQ(n),OddQ(p))))),
ISetDelayed(Int(Times(Power(Csc(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Plus(c_DEFAULT,Times($p(d,true),x_)),m_DEFAULT),Power(Cot(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT)),x_Symbol),
    Condition(Module(List(Set(u,Block(List(Set($s("§showsteps"),False),Set($s("§stepcounter"),Null)),Int(Times(Power(Csc(Plus(a,Times(b,x))),n),Power(Cot(Plus(a,Times(b,x))),p)),x)))),Plus(Dist(Power(Plus(c,Times(d,x)),m),u,x),Times(CN1,d,m,Int(Times(Power(Plus(c,Times(d,x)),Plus(m,Times(CN1,C1))),u),x)))),And(And(FreeQ(List(a,b,c,d,n,p),x),PositiveIntegerQ(m)),Or(EvenQ(n),OddQ(p))))),
ISetDelayed(Int(Times(Power(Csc(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Plus(c_DEFAULT,Times($p(d,true),x_)),m_DEFAULT),Power(Sec(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Times(Power(C2,n),Int(Times(Power(Plus(c,Times(d,x)),m),Power(Csc(Plus(Times(C2,a),Times(C2,b,x))),n)),x)),And(And(FreeQ(List(a,b,c,d),x),RationalQ(m)),IntegerQ(n)))),
ISetDelayed(Int(Times(Power(Csc(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Plus(c_DEFAULT,Times($p(d,true),x_)),m_DEFAULT),Power(Sec(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT)),x_Symbol),
    Condition(Module(List(Set(u,Block(List(Set($s("§showsteps"),False),Set($s("§stepcounter"),Null)),Int(Times(Power(Csc(Plus(a,Times(b,x))),n),Power(Sec(Plus(a,Times(b,x))),p)),x)))),Plus(Dist(Power(Plus(c,Times(d,x)),m),u,x),Times(CN1,d,m,Int(Times(Power(Plus(c,Times(d,x)),Plus(m,Times(CN1,C1))),u),x)))),And(And(And(And(FreeQ(List(a,b,c,d),x),IntegersQ(n,p)),RationalQ(m)),Greater(m,C0)),Unequal(n,p)))),
ISetDelayed(Int(Times(Power(u_,m_DEFAULT),Power($($p("F"),v_),n_DEFAULT),Power($($p("G"),w_),p_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(ExpandToSum(u,x),m),Power(F(ExpandToSum(v,x)),n),Power(G(ExpandToSum(v,x)),p)),x),And(And(And(And(And(FreeQ(List(m,n,p),x),TrigQ($s("F"))),TrigQ($s("G"))),ZeroQ(Plus(v,Times(CN1,w)))),LinearQ(List(u,v,w),x)),Not(LinearMatchQ(List(u,v,w),x))))),
ISetDelayed(Int(Times(Cos(Plus(c_DEFAULT,Times($p(d,true),x_))),Power(Plus($p(e,true),Times(f_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Sin(Plus(c_DEFAULT,Times($p(d,true),x_))))),n_DEFAULT)),x_Symbol),
    Condition(Plus(Times(Power(Plus(e,Times(f,x)),m),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),Plus(n,C1)),Power(Times(b,d,Plus(n,C1)),CN1)),Times(CN1,f,m,Power(Times(b,d,Plus(n,C1)),CN1),Int(Times(Power(Plus(e,Times(f,x)),Plus(m,Times(CN1,C1))),Power(Plus(a,Times(b,Sin(Plus(c,Times(d,x))))),Plus(n,C1))),x))),And(And(FreeQ(List(a,b,c,d,e,f,n),x),PositiveIntegerQ(m)),NonzeroQ(Plus(n,C1))))),
ISetDelayed(Int(Times(Power(Plus($p(e,true),Times(f_DEFAULT,x_)),m_DEFAULT),Sin(Plus(c_DEFAULT,Times($p(d,true),x_))),Power(Plus(a_,Times(b_DEFAULT,Cos(Plus(c_DEFAULT,Times($p(d,true),x_))))),n_DEFAULT)),x_Symbol),
    Condition(Plus(Times(CN1,Power(Plus(e,Times(f,x)),m),Power(Plus(a,Times(b,Cos(Plus(c,Times(d,x))))),Plus(n,C1)),Power(Times(b,d,Plus(n,C1)),CN1)),Times(f,m,Power(Times(b,d,Plus(n,C1)),CN1),Int(Times(Power(Plus(e,Times(f,x)),Plus(m,Times(CN1,C1))),Power(Plus(a,Times(b,Cos(Plus(c,Times(d,x))))),Plus(n,C1))),x))),And(And(FreeQ(List(a,b,c,d,e,f,n),x),PositiveIntegerQ(m)),NonzeroQ(Plus(n,C1))))),
ISetDelayed(Int(Times(Power(Plus($p(e,true),Times(f_DEFAULT,x_)),m_DEFAULT),Sqr(Sec(Plus(c_DEFAULT,Times($p(d,true),x_)))),Power(Plus(a_,Times(b_DEFAULT,Tan(Plus(c_DEFAULT,Times($p(d,true),x_))))),n_DEFAULT)),x_Symbol),
    Condition(Plus(Times(Power(Plus(e,Times(f,x)),m),Power(Plus(a,Times(b,Tan(Plus(c,Times(d,x))))),Plus(n,C1)),Power(Times(b,d,Plus(n,C1)),CN1)),Times(CN1,f,m,Power(Times(b,d,Plus(n,C1)),CN1),Int(Times(Power(Plus(e,Times(f,x)),Plus(m,Times(CN1,C1))),Power(Plus(a,Times(b,Tan(Plus(c,Times(d,x))))),Plus(n,C1))),x))),And(And(FreeQ(List(a,b,c,d,e,f,n),x),PositiveIntegerQ(m)),NonzeroQ(Plus(n,C1))))),
ISetDelayed(Int(Times(Sqr(Csc(Plus(c_DEFAULT,Times($p(d,true),x_)))),Power(Plus($p(e,true),Times(f_DEFAULT,x_)),m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Cot(Plus(c_DEFAULT,Times($p(d,true),x_))))),n_DEFAULT)),x_Symbol),
    Condition(Plus(Times(CN1,Power(Plus(e,Times(f,x)),m),Power(Plus(a,Times(b,Cot(Plus(c,Times(d,x))))),Plus(n,C1)),Power(Times(b,d,Plus(n,C1)),CN1)),Times(f,m,Power(Times(b,d,Plus(n,C1)),CN1),Int(Times(Power(Plus(e,Times(f,x)),Plus(m,Times(CN1,C1))),Power(Plus(a,Times(b,Cot(Plus(c,Times(d,x))))),Plus(n,C1))),x))),And(And(FreeQ(List(a,b,c,d,e,f,n),x),PositiveIntegerQ(m)),NonzeroQ(Plus(n,C1))))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Sec(Plus(c_DEFAULT,Times($p(d,true),x_))))),n_DEFAULT),Power(Plus($p(e,true),Times(f_DEFAULT,x_)),m_DEFAULT),Sec(Plus(c_DEFAULT,Times($p(d,true),x_))),Tan(Plus(c_DEFAULT,Times($p(d,true),x_)))),x_Symbol),
    Condition(Plus(Times(Power(Plus(e,Times(f,x)),m),Power(Plus(a,Times(b,Sec(Plus(c,Times(d,x))))),Plus(n,C1)),Power(Times(b,d,Plus(n,C1)),CN1)),Times(CN1,f,m,Power(Times(b,d,Plus(n,C1)),CN1),Int(Times(Power(Plus(e,Times(f,x)),Plus(m,Times(CN1,C1))),Power(Plus(a,Times(b,Sec(Plus(c,Times(d,x))))),Plus(n,C1))),x))),And(And(FreeQ(List(a,b,c,d,e,f,n),x),PositiveIntegerQ(m)),NonzeroQ(Plus(n,C1))))),
ISetDelayed(Int(Times(Cot(Plus(c_DEFAULT,Times($p(d,true),x_))),Csc(Plus(c_DEFAULT,Times($p(d,true),x_))),Power(Plus(a_,Times(b_DEFAULT,Csc(Plus(c_DEFAULT,Times($p(d,true),x_))))),n_DEFAULT),Power(Plus($p(e,true),Times(f_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Plus(Times(CN1,Power(Plus(e,Times(f,x)),m),Power(Plus(a,Times(b,Csc(Plus(c,Times(d,x))))),Plus(n,C1)),Power(Times(b,d,Plus(n,C1)),CN1)),Times(f,m,Power(Times(b,d,Plus(n,C1)),CN1),Int(Times(Power(Plus(e,Times(f,x)),Plus(m,Times(CN1,C1))),Power(Plus(a,Times(b,Csc(Plus(c,Times(d,x))))),Plus(n,C1))),x))),And(And(FreeQ(List(a,b,c,d,e,f,n),x),PositiveIntegerQ(m)),NonzeroQ(Plus(n,C1))))),
ISetDelayed(Int(Times(Power(Plus($p(e,true),Times(f_DEFAULT,x_)),m_DEFAULT),Power(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT),Power(Sin(Plus(c_DEFAULT,Times($p(d,true),x_))),q_DEFAULT)),x_Symbol),
    Condition(Int(ExpandTrigReduce(Power(Plus(e,Times(f,x)),m),Times(Power(Sin(Plus(a,Times(b,x))),p),Power(Sin(Plus(c,Times(d,x))),q)),x),x),And(And(FreeQ(List(a,b,c,d,e,f),x),PositiveIntegerQ(p,q)),IntegerQ(m)))),
ISetDelayed(Int(Times(Power(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT),Power(Plus($p(e,true),Times(f_DEFAULT,x_)),m_DEFAULT),Power(Cos(Plus(c_DEFAULT,Times($p(d,true),x_))),q_DEFAULT)),x_Symbol),
    Condition(Int(ExpandTrigReduce(Power(Plus(e,Times(f,x)),m),Times(Power(Cos(Plus(a,Times(b,x))),p),Power(Cos(Plus(c,Times(d,x))),q)),x),x),And(And(FreeQ(List(a,b,c,d,e,f),x),PositiveIntegerQ(p,q)),IntegerQ(m)))),
ISetDelayed(Int(Times(Power(Plus($p(e,true),Times(f_DEFAULT,x_)),m_DEFAULT),Power(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT),Power(Cos(Plus(c_DEFAULT,Times($p(d,true),x_))),q_DEFAULT)),x_Symbol),
    Condition(Int(ExpandTrigReduce(Power(Plus(e,Times(f,x)),m),Times(Power(Sin(Plus(a,Times(b,x))),p),Power(Cos(Plus(c,Times(d,x))),q)),x),x),And(FreeQ(List(a,b,c,d,e,f,m),x),PositiveIntegerQ(p,q)))),
ISetDelayed(Int(Times(Power(Plus($p(e,true),Times(f_DEFAULT,x_)),m_DEFAULT),Power($($p("F"),Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT),Power($($p("G"),Plus(c_DEFAULT,Times($p(d,true),x_))),q_DEFAULT)),x_Symbol),
    Condition(Int(ExpandTrigExpand(Times(Power(Plus(e,Times(f,x)),m),Power(G(Plus(c,Times(d,x))),q)),$s("F"),Plus(c,Times(d,x)),p,Times(b,Power(d,CN1)),x),x),And(And(And(And(And(FreeQ(List(a,b,c,d,e,f,m),x),MemberQ(List($s("Sin"),$s("Cos")),$s("F"))),MemberQ(List($s("Sec"),$s("Csc")),$s("G"))),PositiveIntegerQ(p,q)),ZeroQ(Plus(Times(b,c),Times(CN1,a,d)))),PositiveIntegerQ(Plus(Times(b,Power(d,CN1)),Times(CN1,C1))))))
  );
}
