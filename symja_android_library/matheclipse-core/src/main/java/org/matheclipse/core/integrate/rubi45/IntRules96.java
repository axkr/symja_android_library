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
public class IntRules96 { 
  public static IAST RULES = List( 
ISetDelayed(Int(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Csch(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Sech(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Times(Power(C2,n),Int(Times(Power(Plus(c,Times(d,x)),m),Power(Csch(Plus(Times(C2,a),Times(C2,b,x))),n)),x)),And(And(FreeQ(List(a,b,c,d),x),RationalQ(m)),IntegerQ(n)))),
ISetDelayed(Int(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),Power(Csch(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),n_DEFAULT),Power(Sech(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT)),x_Symbol),
    Condition(Module(List(Set(u,Block(List(Set($s("§showsteps"),False),Set($s("§stepcounter"),Null)),Int(Times(Power(Csch(Plus(a,Times(b,x))),n),Power(Sech(Plus(a,Times(b,x))),p)),x)))),Plus(Dist(Power(Plus(c,Times(d,x)),m),u,x),Times(CN1,d,m,Int(Times(Power(Plus(c,Times(d,x)),Plus(m,Negate(C1))),u),x)))),And(And(And(And(FreeQ(List(a,b,c,d),x),IntegersQ(n,p)),RationalQ(m)),Greater(m,C0)),Unequal(n,p)))),
ISetDelayed(Int(Times(Power(u_,m_DEFAULT),Power($(F_,v_),n_DEFAULT),Power($(G_,w_),p_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(ExpandToSum(u,x),m),Power(F(ExpandToSum(v,x)),n),Power(G(ExpandToSum(v,x)),p)),x),And(And(And(And(And(FreeQ(List(m,n,p),x),HyperbolicQ(FSymbol)),HyperbolicQ(GSymbol)),ZeroQ(Plus(v,Negate(w)))),LinearQ(List(u,v,w),x)),Not(LinearMatchQ(List(u,v,w),x))))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Times(Power(Plus(e,Times(f,x)),m),Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,x))))),Plus(n,C1)),Power(Times(b,d,Plus(n,C1)),-1)),Times(CN1,f,m,Power(Times(b,d,Plus(n,C1)),-1),Int(Times(Power(Plus(e,Times(f,x)),Plus(m,Negate(C1))),Power(Plus(a,Times(b,Sinh(Plus(c,Times(d,x))))),Plus(n,C1))),x))),And(And(FreeQ(List(a,b,c,d,e,f,n),x),PositiveIntegerQ(m)),NonzeroQ(Plus(n,C1))))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Times(Power(Plus(e,Times(f,x)),m),Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,x))))),Plus(n,C1)),Power(Times(b,d,Plus(n,C1)),-1)),Times(CN1,f,m,Power(Times(b,d,Plus(n,C1)),-1),Int(Times(Power(Plus(e,Times(f,x)),Plus(m,Negate(C1))),Power(Plus(a,Times(b,Cosh(Plus(c,Times(d,x))))),Plus(n,C1))),x))),And(And(FreeQ(List(a,b,c,d,e,f,n),x),PositiveIntegerQ(m)),NonzeroQ(Plus(n,C1))))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Tanh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Sqr(Sech(Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),x_Symbol),
    Condition(Plus(Times(Power(Plus(e,Times(f,x)),m),Power(Plus(a,Times(b,Tanh(Plus(c,Times(d,x))))),Plus(n,C1)),Power(Times(b,d,Plus(n,C1)),-1)),Times(CN1,f,m,Power(Times(b,d,Plus(n,C1)),-1),Int(Times(Power(Plus(e,Times(f,x)),Plus(m,Negate(C1))),Power(Plus(a,Times(b,Tanh(Plus(c,Times(d,x))))),Plus(n,C1))),x))),And(And(FreeQ(List(a,b,c,d,e,f,n),x),PositiveIntegerQ(m)),NonzeroQ(Plus(n,C1))))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Coth(Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Sqr(Csch(Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),x_Symbol),
    Condition(Plus(Times(CN1,Power(Plus(e,Times(f,x)),m),Power(Plus(a,Times(b,Coth(Plus(c,Times(d,x))))),Plus(n,C1)),Power(Times(b,d,Plus(n,C1)),-1)),Times(f,m,Power(Times(b,d,Plus(n,C1)),-1),Int(Times(Power(Plus(e,Times(f,x)),Plus(m,Negate(C1))),Power(Plus(a,Times(b,Coth(Plus(c,Times(d,x))))),Plus(n,C1))),x))),And(And(FreeQ(List(a,b,c,d,e,f,n),x),PositiveIntegerQ(m)),NonzeroQ(Plus(n,C1))))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Sech(Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Sech(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Tanh(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Times(CN1,Power(Plus(e,Times(f,x)),m),Power(Plus(a,Times(b,Sech(Plus(c,Times(d,x))))),Plus(n,C1)),Power(Times(b,d,Plus(n,C1)),-1)),Times(f,m,Power(Times(b,d,Plus(n,C1)),-1),Int(Times(Power(Plus(e,Times(f,x)),Plus(m,Negate(C1))),Power(Plus(a,Times(b,Sech(Plus(c,Times(d,x))))),Plus(n,C1))),x))),And(And(FreeQ(List(a,b,c,d,e,f,n),x),PositiveIntegerQ(m)),NonzeroQ(Plus(n,C1))))),
ISetDelayed(Int(Times(Power(Plus(a_,Times(b_DEFAULT,Csch(Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_DEFAULT),Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Coth(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),Csch(Plus(c_DEFAULT,Times(d_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Times(CN1,Power(Plus(e,Times(f,x)),m),Power(Plus(a,Times(b,Csch(Plus(c,Times(d,x))))),Plus(n,C1)),Power(Times(b,d,Plus(n,C1)),-1)),Times(f,m,Power(Times(b,d,Plus(n,C1)),-1),Int(Times(Power(Plus(e,Times(f,x)),Plus(m,Negate(C1))),Power(Plus(a,Times(b,Csch(Plus(c,Times(d,x))))),Plus(n,C1))),x))),And(And(FreeQ(List(a,b,c,d,e,f,n),x),PositiveIntegerQ(m)),NonzeroQ(Plus(n,C1))))),
ISetDelayed(Int(Times(Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Power(Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT),Power(Sinh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),q_DEFAULT)),x_Symbol),
    Condition(Int(ExpandTrigReduce(Power(Plus(e,Times(f,x)),m),Times(Power(Sinh(Plus(a,Times(b,x))),p),Power(Sinh(Plus(c,Times(d,x))),q)),x),x),And(And(FreeQ(List(a,b,c,d,e,f),x),PositiveIntegerQ(p,q)),IntegerQ(m)))),
ISetDelayed(Int(Times(Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Power(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT),Power(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),q_DEFAULT)),x_Symbol),
    Condition(Int(ExpandTrigReduce(Power(Plus(e,Times(f,x)),m),Times(Power(Cosh(Plus(a,Times(b,x))),p),Power(Cosh(Plus(c,Times(d,x))),q)),x),x),And(And(FreeQ(List(a,b,c,d,e,f),x),PositiveIntegerQ(p,q)),IntegerQ(m)))),
ISetDelayed(Int(Times(Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Power(Cosh(Plus(c_DEFAULT,Times(d_DEFAULT,x_))),q_DEFAULT),Power(Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT)),x_Symbol),
    Condition(Int(ExpandTrigReduce(Power(Plus(e,Times(f,x)),m),Times(Power(Sinh(Plus(a,Times(b,x))),p),Power(Cosh(Plus(c,Times(d,x))),q)),x),x),And(FreeQ(List(a,b,c,d,e,f,m),x),PositiveIntegerQ(p,q)))),
ISetDelayed(Int(Times(Power(Plus(e_DEFAULT,Times(f_DEFAULT,x_)),m_DEFAULT),Power($(F_,Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_DEFAULT),Power($(G_,Plus(c_DEFAULT,Times(d_DEFAULT,x_))),q_DEFAULT)),x_Symbol),
    Condition(Int(ExpandTrigExpand(Times(Power(Plus(e,Times(f,x)),m),Power(G(Plus(c,Times(d,x))),q)),FSymbol,Plus(c,Times(d,x)),p,Times(b,Power(d,-1)),x),x),And(And(And(And(And(FreeQ(List(a,b,c,d,e,f,m),x),MemberQ(List($s("Sinh"),$s("Cosh")),FSymbol)),MemberQ(List($s("Sech"),$s("Csch")),GSymbol)),PositiveIntegerQ(p,q)),ZeroQ(Plus(Times(b,c),Times(CN1,a,d)))),PositiveIntegerQ(Plus(Times(b,Power(d,-1)),Negate(C1))))))
  );
}
