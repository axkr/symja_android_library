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
public class IntRules92 { 
  public static IAST RULES = List( 
ISetDelayed(Int(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),x_Symbol),
    Condition(Int(Sin(Times(Sqr(Plus(b,Times(C2,c,x))),Power(Times(C4,c),-1))),x),And(FreeQ(List(a,b,c),x),ZeroQ(Plus(Sqr(b),Times(CN1,C4,a,c)))))),
ISetDelayed(Int(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),x_Symbol),
    Condition(Int(Cos(Times(Sqr(Plus(b,Times(C2,c,x))),Power(Times(C4,c),-1))),x),And(FreeQ(List(a,b,c),x),ZeroQ(Plus(Sqr(b),Times(CN1,C4,a,c)))))),
ISetDelayed(Int(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),x_Symbol),
    Condition(Plus(Times(Cos(Times(Plus(Sqr(b),Times(CN1,C4,a,c)),Power(Times(C4,c),-1))),Int(Sin(Times(Sqr(Plus(b,Times(C2,c,x))),Power(Times(C4,c),-1))),x)),Times(CN1,Sin(Times(Plus(Sqr(b),Times(CN1,C4,a,c)),Power(Times(C4,c),-1))),Int(Cos(Times(Sqr(Plus(b,Times(C2,c,x))),Power(Times(C4,c),-1))),x))),And(FreeQ(List(a,b,c),x),NonzeroQ(Plus(Sqr(b),Times(CN1,C4,a,c)))))),
ISetDelayed(Int(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),x_Symbol),
    Condition(Plus(Times(Cos(Times(Plus(Sqr(b),Times(CN1,C4,a,c)),Power(Times(C4,c),-1))),Int(Cos(Times(Sqr(Plus(b,Times(C2,c,x))),Power(Times(C4,c),-1))),x)),Times(Sin(Times(Plus(Sqr(b),Times(CN1,C4,a,c)),Power(Times(C4,c),-1))),Int(Sin(Times(Sqr(Plus(b,Times(C2,c,x))),Power(Times(C4,c),-1))),x))),And(FreeQ(List(a,b,c),x),NonzeroQ(Plus(Sqr(b),Times(CN1,C4,a,c)))))),
ISetDelayed(Int(Power(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_),x_Symbol),
    Condition(Int(ExpandTrigReduce(Power(Sin(Plus(a,Times(b,x),Times(c,Sqr(x)))),n),x),x),And(And(FreeQ(List(a,b,c),x),IntegerQ(n)),Greater(n,C1)))),
ISetDelayed(Int(Power(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_),x_Symbol),
    Condition(Int(ExpandTrigReduce(Power(Cos(Plus(a,Times(b,x),Times(c,Sqr(x)))),n),x),x),And(And(FreeQ(List(a,b,c),x),IntegerQ(n)),Greater(n,C1)))),
ISetDelayed(Int(Power(Sin(v_),n_DEFAULT),x_Symbol),
    Condition(Int(Power(Sin(ExpandToSum(v,x)),n),x),And(And(PositiveIntegerQ(n),QuadraticQ(v,x)),Not(QuadraticMatchQ(v,x))))),
ISetDelayed(Int(Power(Cos(v_),n_DEFAULT),x_Symbol),
    Condition(Int(Power(Cos(ExpandToSum(v,x)),n),x),And(And(PositiveIntegerQ(n),QuadraticQ(v,x)),Not(QuadraticMatchQ(v,x))))),
ISetDelayed(Int(Times(Plus(d_,Times(e_DEFAULT,x_)),Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Times(CN1,e,Cos(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),-1)),And(FreeQ(List(a,b,c,d,e),x),ZeroQ(Plus(Times(C2,c,d),Times(CN1,b,e)))))),
ISetDelayed(Int(Times(Plus(d_,Times(e_DEFAULT,x_)),Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Times(e,Sin(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),-1)),And(FreeQ(List(a,b,c,d,e),x),ZeroQ(Plus(Times(C2,c,d),Times(CN1,b,e)))))),
ISetDelayed(Int(Times(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Times(CN1,e,Cos(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),-1)),Times(Plus(Times(C2,c,d),Times(CN1,b,e)),Power(Times(C2,c),-1),Int(Sin(Plus(a,Times(b,x),Times(c,Sqr(x)))),x))),And(FreeQ(List(a,b,c,d,e),x),NonzeroQ(Plus(Times(C2,c,d),Times(CN1,b,e)))))),
ISetDelayed(Int(Times(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Times(e,Sin(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),-1)),Times(Plus(Times(C2,c,d),Times(CN1,b,e)),Power(Times(C2,c),-1),Int(Cos(Plus(a,Times(b,x),Times(c,Sqr(x)))),x))),And(FreeQ(List(a,b,c,d,e),x),NonzeroQ(Plus(Times(C2,c,d),Times(CN1,b,e)))))),
ISetDelayed(Int(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_),Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Times(CN1,e,Power(Plus(d,Times(e,x)),Plus(m,Negate(C1))),Cos(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),-1)),Times(Sqr(e),Plus(m,Negate(C1)),Power(Times(C2,c),-1),Int(Times(Power(Plus(d,Times(e,x)),Plus(m,Negate(C2))),Cos(Plus(a,Times(b,x),Times(c,Sqr(x))))),x))),And(And(And(FreeQ(List(a,b,c,d,e),x),RationalQ(m)),Greater(m,C1)),ZeroQ(Plus(Times(b,e),Times(CN1,C2,c,d)))))),
ISetDelayed(Int(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_),Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Times(e,Power(Plus(d,Times(e,x)),Plus(m,Negate(C1))),Sin(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),-1)),Times(CN1,Sqr(e),Plus(m,Negate(C1)),Power(Times(C2,c),-1),Int(Times(Power(Plus(d,Times(e,x)),Plus(m,Negate(C2))),Sin(Plus(a,Times(b,x),Times(c,Sqr(x))))),x))),And(And(And(FreeQ(List(a,b,c,d,e),x),RationalQ(m)),Greater(m,C1)),ZeroQ(Plus(Times(b,e),Times(CN1,C2,c,d)))))),
ISetDelayed(Int(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_),Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Times(CN1,e,Power(Plus(d,Times(e,x)),Plus(m,Negate(C1))),Cos(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),-1)),Times(CN1,Plus(Times(b,e),Times(CN1,C2,c,d)),Power(Times(C2,c),-1),Int(Times(Power(Plus(d,Times(e,x)),Plus(m,Negate(C1))),Sin(Plus(a,Times(b,x),Times(c,Sqr(x))))),x)),Times(Sqr(e),Plus(m,Negate(C1)),Power(Times(C2,c),-1),Int(Times(Power(Plus(d,Times(e,x)),Plus(m,Negate(C2))),Cos(Plus(a,Times(b,x),Times(c,Sqr(x))))),x))),And(And(And(FreeQ(List(a,b,c,d,e),x),RationalQ(m)),Greater(m,C1)),NonzeroQ(Plus(Times(b,e),Times(CN1,C2,c,d)))))),
ISetDelayed(Int(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_),Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Times(e,Power(Plus(d,Times(e,x)),Plus(m,Negate(C1))),Sin(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),-1)),Times(CN1,Plus(Times(b,e),Times(CN1,C2,c,d)),Power(Times(C2,c),-1),Int(Times(Power(Plus(d,Times(e,x)),Plus(m,Negate(C1))),Cos(Plus(a,Times(b,x),Times(c,Sqr(x))))),x)),Times(CN1,Sqr(e),Plus(m,Negate(C1)),Power(Times(C2,c),-1),Int(Times(Power(Plus(d,Times(e,x)),Plus(m,Negate(C2))),Sin(Plus(a,Times(b,x),Times(c,Sqr(x))))),x))),And(And(And(FreeQ(List(a,b,c,d,e),x),RationalQ(m)),Greater(m,C1)),NonzeroQ(Plus(Times(b,e),Times(CN1,C2,c,d)))))),
ISetDelayed(Int(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_),Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Sin(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(e,Plus(m,C1)),-1)),Times(CN1,C2,c,Power(Times(Sqr(e),Plus(m,C1)),-1),Int(Times(Power(Plus(d,Times(e,x)),Plus(m,C2)),Cos(Plus(a,Times(b,x),Times(c,Sqr(x))))),x))),And(And(And(FreeQ(List(a,b,c,d,e),x),RationalQ(m)),Less(m,CN1)),ZeroQ(Plus(Times(b,e),Times(CN1,C2,c,d)))))),
ISetDelayed(Int(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_),Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Cos(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(e,Plus(m,C1)),-1)),Times(C2,c,Power(Times(Sqr(e),Plus(m,C1)),-1),Int(Times(Power(Plus(d,Times(e,x)),Plus(m,C2)),Sin(Plus(a,Times(b,x),Times(c,Sqr(x))))),x))),And(And(And(FreeQ(List(a,b,c,d,e),x),RationalQ(m)),Less(m,CN1)),ZeroQ(Plus(Times(b,e),Times(CN1,C2,c,d)))))),
ISetDelayed(Int(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_),Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Sin(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(e,Plus(m,C1)),-1)),Times(CN1,Plus(Times(b,e),Times(CN1,C2,c,d)),Power(Times(Sqr(e),Plus(m,C1)),-1),Int(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Cos(Plus(a,Times(b,x),Times(c,Sqr(x))))),x)),Times(CN1,C2,c,Power(Times(Sqr(e),Plus(m,C1)),-1),Int(Times(Power(Plus(d,Times(e,x)),Plus(m,C2)),Cos(Plus(a,Times(b,x),Times(c,Sqr(x))))),x))),And(And(And(FreeQ(List(a,b,c,d,e),x),RationalQ(m)),Less(m,CN1)),NonzeroQ(Plus(Times(b,e),Times(CN1,C2,c,d)))))),
ISetDelayed(Int(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_),Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Cos(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(e,Plus(m,C1)),-1)),Times(Plus(Times(b,e),Times(CN1,C2,c,d)),Power(Times(Sqr(e),Plus(m,C1)),-1),Int(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Sin(Plus(a,Times(b,x),Times(c,Sqr(x))))),x)),Times(C2,c,Power(Times(Sqr(e),Plus(m,C1)),-1),Int(Times(Power(Plus(d,Times(e,x)),Plus(m,C2)),Sin(Plus(a,Times(b,x),Times(c,Sqr(x))))),x))),And(And(And(FreeQ(List(a,b,c,d,e),x),RationalQ(m)),Less(m,CN1)),NonzeroQ(Plus(Times(b,e),Times(CN1,C2,c,d)))))),
ISetDelayed(Int(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT),Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Integrate(Times(Power(Plus(d,Times(e,x)),m),Sin(Plus(a,Times(b,x),Times(c,Sqr(x))))),x),FreeQ(List(a,b,c,d,e,m),x))),
ISetDelayed(Int(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT),Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Integrate(Times(Power(Plus(d,Times(e,x)),m),Cos(Plus(a,Times(b,x),Times(c,Sqr(x))))),x),FreeQ(List(a,b,c,d,e,m),x))),
ISetDelayed(Int(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT),Power(Sin(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_)),x_Symbol),
    Condition(Int(ExpandTrigReduce(Power(Plus(d,Times(e,x)),m),Power(Sin(Plus(a,Times(b,x),Times(c,Sqr(x)))),n),x),x),And(And(FreeQ(List(a,b,c,d,e,m),x),IntegerQ(n)),Greater(n,C1)))),
ISetDelayed(Int(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT),Power(Cos(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_)),x_Symbol),
    Condition(Int(ExpandTrigReduce(Power(Plus(d,Times(e,x)),m),Power(Cos(Plus(a,Times(b,x),Times(c,Sqr(x)))),n),x),x),And(And(FreeQ(List(a,b,c,d,e,m),x),IntegerQ(n)),Greater(n,C1)))),
ISetDelayed(Int(Times(Power(u_,m_DEFAULT),Power(Sin(v_),n_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(ExpandToSum(u,x),m),Power(Sin(ExpandToSum(v,x)),n)),x),And(And(And(And(FreeQ(m,x),PositiveIntegerQ(n)),LinearQ(u,x)),QuadraticQ(v,x)),Not(And(LinearMatchQ(u,x),QuadraticMatchQ(v,x)))))),
ISetDelayed(Int(Times(Power(u_,m_DEFAULT),Power(Cos(v_),n_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(ExpandToSum(u,x),m),Power(Cos(ExpandToSum(v,x)),n)),x),And(And(And(And(FreeQ(m,x),PositiveIntegerQ(n)),LinearQ(u,x)),QuadraticQ(v,x)),Not(And(LinearMatchQ(u,x),QuadraticMatchQ(v,x)))))),
ISetDelayed(Int(Power(Tan(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_DEFAULT),x_Symbol),
    Condition(Integrate(Power(Tan(Plus(a,Times(b,x),Times(c,Sqr(x)))),n),x),FreeQ(List(a,b,c,n),x))),
ISetDelayed(Int(Power(Cot(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_DEFAULT),x_Symbol),
    Condition(Integrate(Power(Cot(Plus(a,Times(b,x),Times(c,Sqr(x)))),n),x),FreeQ(List(a,b,c,n),x))),
ISetDelayed(Int(Times(Plus(d_,Times(e_DEFAULT,x_)),Tan(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Times(CN1,e,Log(Cos(Plus(a,Times(b,x),Times(c,Sqr(x))))),Power(Times(C2,c),-1)),And(FreeQ(List(a,b,c,d,e),x),ZeroQ(Plus(Times(C2,c,d),Times(CN1,b,e)))))),
ISetDelayed(Int(Times(Plus(d_,Times(e_DEFAULT,x_)),Cot(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Times(e,Log(Sin(Plus(a,Times(b,x),Times(c,Sqr(x))))),Power(Times(C2,c),-1)),And(FreeQ(List(a,b,c,d,e),x),ZeroQ(Plus(Times(C2,c,d),Times(CN1,b,e)))))),
ISetDelayed(Int(Times(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),Tan(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Times(CN1,e,Log(Cos(Plus(a,Times(b,x),Times(c,Sqr(x))))),Power(Times(C2,c),-1)),Times(Plus(Times(C2,c,d),Times(CN1,b,e)),Power(Times(C2,c),-1),Int(Tan(Plus(a,Times(b,x),Times(c,Sqr(x)))),x))),And(FreeQ(List(a,b,c,d,e),x),NonzeroQ(Plus(Times(C2,c,d),Times(CN1,b,e)))))),
ISetDelayed(Int(Times(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),Cot(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Times(e,Log(Sin(Plus(a,Times(b,x),Times(c,Sqr(x))))),Power(Times(C2,c),-1)),Times(Plus(Times(C2,c,d),Times(CN1,b,e)),Power(Times(C2,c),-1),Int(Cot(Plus(a,Times(b,x),Times(c,Sqr(x)))),x))),And(FreeQ(List(a,b,c,d,e),x),NonzeroQ(Plus(Times(C2,c,d),Times(CN1,b,e)))))),
ISetDelayed(Int(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT),Power(Tan(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Plus(d,Times(e,x)),m),Power(Tan(Plus(a,Times(b,x),Times(c,Sqr(x)))),n)),x),FreeQ(List(a,b,c,d,e,m,n),x))),
ISetDelayed(Int(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT),Power(Cot(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_DEFAULT)),x_Symbol),
    Condition(Integrate(Times(Power(Plus(d,Times(e,x)),m),Power(Cot(Plus(a,Times(b,x),Times(c,Sqr(x)))),n)),x),FreeQ(List(a,b,c,d,e,m,n),x)))
  );
}
