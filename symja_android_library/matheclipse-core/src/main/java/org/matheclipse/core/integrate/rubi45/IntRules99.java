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
public class IntRules99 { 
  public static IAST RULES = List( 
ISetDelayed(Int(Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),x_Symbol),
    Condition(Plus(Times(C1D2,Int(Power(E,Plus(a,Times(b,x),Times(c,Sqr(x)))),x)),Times(CN1,C1D2,Int(Power(E,Plus(Negate(a),Times(CN1,b,x),Times(CN1,c,Sqr(x)))),x))),FreeQ(List(a,b,c),x))),
ISetDelayed(Int(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),x_Symbol),
    Condition(Plus(Times(C1D2,Int(Power(E,Plus(a,Times(b,x),Times(c,Sqr(x)))),x)),Times(C1D2,Int(Power(E,Plus(Negate(a),Times(CN1,b,x),Times(CN1,c,Sqr(x)))),x))),FreeQ(List(a,b,c),x))),
ISetDelayed(Int(Power(Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_),x_Symbol),
    Condition(Int(ExpandTrigReduce(Power(Sinh(Plus(a,Times(b,x),Times(c,Sqr(x)))),n),x),x),And(And(FreeQ(List(a,b,c),x),IntegerQ(n)),Greater(n,C1)))),
ISetDelayed(Int(Power(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_),x_Symbol),
    Condition(Int(ExpandTrigReduce(Power(Cosh(Plus(a,Times(b,x),Times(c,Sqr(x)))),n),x),x),And(And(FreeQ(List(a,b,c),x),IntegerQ(n)),Greater(n,C1)))),
ISetDelayed(Int(Power(Sinh(v_),n_DEFAULT),x_Symbol),
    Condition(Int(Power(Sinh(ExpandToSum(v,x)),n),x),And(And(PositiveIntegerQ(n),QuadraticQ(v,x)),Not(QuadraticMatchQ(v,x))))),
ISetDelayed(Int(Power(Cosh(v_),n_DEFAULT),x_Symbol),
    Condition(Int(Power(Cosh(ExpandToSum(v,x)),n),x),And(And(PositiveIntegerQ(n),QuadraticQ(v,x)),Not(QuadraticMatchQ(v,x))))),
ISetDelayed(Int(Times(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Times(e,Cosh(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),-1)),And(FreeQ(List(a,b,c,d,e),x),ZeroQ(Plus(Times(b,e),Times(CN1,C2,c,d)))))),
ISetDelayed(Int(Times(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Times(e,Sinh(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),-1)),And(FreeQ(List(a,b,c,d,e),x),ZeroQ(Plus(Times(b,e),Times(CN1,C2,c,d)))))),
ISetDelayed(Int(Times(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Times(e,Cosh(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),-1)),Times(CN1,Plus(Times(b,e),Times(CN1,C2,c,d)),Power(Times(C2,c),-1),Int(Sinh(Plus(a,Times(b,x),Times(c,Sqr(x)))),x))),And(FreeQ(List(a,b,c,d,e),x),NonzeroQ(Plus(Times(b,e),Times(CN1,C2,c,d)))))),
ISetDelayed(Int(Times(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Times(e,Sinh(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),-1)),Times(CN1,Plus(Times(b,e),Times(CN1,C2,c,d)),Power(Times(C2,c),-1),Int(Cosh(Plus(a,Times(b,x),Times(c,Sqr(x)))),x))),And(FreeQ(List(a,b,c,d,e),x),NonzeroQ(Plus(Times(b,e),Times(CN1,C2,c,d)))))),
ISetDelayed(Int(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_),Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Times(e,Power(Plus(d,Times(e,x)),Plus(m,Negate(C1))),Cosh(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),-1)),Times(CN1,Sqr(e),Plus(m,Negate(C1)),Power(Times(C2,c),-1),Int(Times(Power(Plus(d,Times(e,x)),Plus(m,Negate(C2))),Cosh(Plus(a,Times(b,x),Times(c,Sqr(x))))),x))),And(And(And(FreeQ(List(a,b,c,d,e),x),RationalQ(m)),Greater(m,C1)),ZeroQ(Plus(Times(b,e),Times(CN1,C2,c,d)))))),
ISetDelayed(Int(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_),Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Times(e,Power(Plus(d,Times(e,x)),Plus(m,Negate(C1))),Sinh(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),-1)),Times(CN1,Sqr(e),Plus(m,Negate(C1)),Power(Times(C2,c),-1),Int(Times(Power(Plus(d,Times(e,x)),Plus(m,Negate(C2))),Sinh(Plus(a,Times(b,x),Times(c,Sqr(x))))),x))),And(And(And(FreeQ(List(a,b,c,d,e),x),RationalQ(m)),Greater(m,C1)),ZeroQ(Plus(Times(b,e),Times(CN1,C2,c,d)))))),
ISetDelayed(Int(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_),Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Times(e,Power(Plus(d,Times(e,x)),Plus(m,Negate(C1))),Cosh(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),-1)),Times(CN1,Plus(Times(b,e),Times(CN1,C2,c,d)),Power(Times(C2,c),-1),Int(Times(Power(Plus(d,Times(e,x)),Plus(m,Negate(C1))),Sinh(Plus(a,Times(b,x),Times(c,Sqr(x))))),x)),Times(CN1,Sqr(e),Plus(m,Negate(C1)),Power(Times(C2,c),-1),Int(Times(Power(Plus(d,Times(e,x)),Plus(m,Negate(C2))),Cosh(Plus(a,Times(b,x),Times(c,Sqr(x))))),x))),And(And(And(FreeQ(List(a,b,c,d,e),x),RationalQ(m)),Greater(m,C1)),NonzeroQ(Plus(Times(b,e),Times(CN1,C2,c,d)))))),
ISetDelayed(Int(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_),Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Times(e,Power(Plus(d,Times(e,x)),Plus(m,Negate(C1))),Sinh(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),-1)),Times(CN1,Plus(Times(b,e),Times(CN1,C2,c,d)),Power(Times(C2,c),-1),Int(Times(Power(Plus(d,Times(e,x)),Plus(m,Negate(C1))),Cosh(Plus(a,Times(b,x),Times(c,Sqr(x))))),x)),Times(CN1,Sqr(e),Plus(m,Negate(C1)),Power(Times(C2,c),-1),Int(Times(Power(Plus(d,Times(e,x)),Plus(m,Negate(C2))),Sinh(Plus(a,Times(b,x),Times(c,Sqr(x))))),x))),And(And(And(FreeQ(List(a,b,c,d,e),x),RationalQ(m)),Greater(m,C1)),NonzeroQ(Plus(Times(b,e),Times(CN1,C2,c,d)))))),
ISetDelayed(Int(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_),Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Sinh(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(e,Plus(m,C1)),-1)),Times(CN1,C2,c,Power(Times(Sqr(e),Plus(m,C1)),-1),Int(Times(Power(Plus(d,Times(e,x)),Plus(m,C2)),Cosh(Plus(a,Times(b,x),Times(c,Sqr(x))))),x))),And(And(And(FreeQ(List(a,b,c,d,e),x),RationalQ(m)),Less(m,CN1)),ZeroQ(Plus(Times(b,e),Times(CN1,C2,c,d)))))),
ISetDelayed(Int(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_),Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Cosh(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(e,Plus(m,C1)),-1)),Times(CN1,C2,c,Power(Times(Sqr(e),Plus(m,C1)),-1),Int(Times(Power(Plus(d,Times(e,x)),Plus(m,C2)),Sinh(Plus(a,Times(b,x),Times(c,Sqr(x))))),x))),And(And(And(FreeQ(List(a,b,c,d,e),x),RationalQ(m)),Less(m,CN1)),ZeroQ(Plus(Times(b,e),Times(CN1,C2,c,d)))))),
ISetDelayed(Int(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_),Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Sinh(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(e,Plus(m,C1)),-1)),Times(CN1,Plus(Times(b,e),Times(CN1,C2,c,d)),Power(Times(Sqr(e),Plus(m,C1)),-1),Int(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Cosh(Plus(a,Times(b,x),Times(c,Sqr(x))))),x)),Times(CN1,C2,c,Power(Times(Sqr(e),Plus(m,C1)),-1),Int(Times(Power(Plus(d,Times(e,x)),Plus(m,C2)),Cosh(Plus(a,Times(b,x),Times(c,Sqr(x))))),x))),And(And(And(FreeQ(List(a,b,c,d,e),x),RationalQ(m)),Less(m,CN1)),NonzeroQ(Plus(Times(b,e),Times(CN1,C2,c,d)))))),
ISetDelayed(Int(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_),Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Cosh(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(e,Plus(m,C1)),-1)),Times(CN1,Plus(Times(b,e),Times(CN1,C2,c,d)),Power(Times(Sqr(e),Plus(m,C1)),-1),Int(Times(Power(Plus(d,Times(e,x)),Plus(m,C1)),Sinh(Plus(a,Times(b,x),Times(c,Sqr(x))))),x)),Times(CN1,C2,c,Power(Times(Sqr(e),Plus(m,C1)),-1),Int(Times(Power(Plus(d,Times(e,x)),Plus(m,C2)),Sinh(Plus(a,Times(b,x),Times(c,Sqr(x))))),x))),And(And(And(FreeQ(List(a,b,c,d,e),x),RationalQ(m)),Less(m,CN1)),NonzeroQ(Plus(Times(b,e),Times(CN1,C2,c,d)))))),
ISetDelayed(Int(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT),Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition($(Defer($s("Int")),Times(Power(Plus(d,Times(e,x)),m),Sinh(Plus(a,Times(b,x),Times(c,Sqr(x))))),x),FreeQ(List(a,b,c,d,e,m),x))),
ISetDelayed(Int(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT),Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition($(Defer($s("Int")),Times(Power(Plus(d,Times(e,x)),m),Cosh(Plus(a,Times(b,x),Times(c,Sqr(x))))),x),FreeQ(List(a,b,c,d,e,m),x))),
ISetDelayed(Int(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT),Power(Sinh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_)),x_Symbol),
    Condition(Int(ExpandTrigReduce(Power(Plus(d,Times(e,x)),m),Power(Sinh(Plus(a,Times(b,x),Times(c,Sqr(x)))),n),x),x),And(And(FreeQ(List(a,b,c,d,e,m),x),IntegerQ(n)),Greater(n,C1)))),
ISetDelayed(Int(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT),Power(Cosh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_)),x_Symbol),
    Condition(Int(ExpandTrigReduce(Power(Plus(d,Times(e,x)),m),Power(Cosh(Plus(a,Times(b,x),Times(c,Sqr(x)))),n),x),x),And(And(FreeQ(List(a,b,c,d,e,m),x),IntegerQ(n)),Greater(n,C1)))),
ISetDelayed(Int(Times(Power(u_,m_DEFAULT),Power(Sinh(v_),n_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(ExpandToSum(u,x),m),Power(Sinh(ExpandToSum(v,x)),n)),x),And(And(And(And(FreeQ(m,x),PositiveIntegerQ(n)),LinearQ(u,x)),QuadraticQ(v,x)),Not(And(LinearMatchQ(u,x),QuadraticMatchQ(v,x)))))),
ISetDelayed(Int(Times(Power(u_,m_DEFAULT),Power(Cosh(v_),n_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(ExpandToSum(u,x),m),Power(Cosh(ExpandToSum(v,x)),n)),x),And(And(And(And(FreeQ(m,x),PositiveIntegerQ(n)),LinearQ(u,x)),QuadraticQ(v,x)),Not(And(LinearMatchQ(u,x),QuadraticMatchQ(v,x)))))),
ISetDelayed(Int(Power(Tanh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_DEFAULT),x_Symbol),
    Condition($(Defer($s("Int")),Power(Tanh(Plus(a,Times(b,x),Times(c,Sqr(x)))),n),x),FreeQ(List(a,b,c,n),x))),
ISetDelayed(Int(Power(Coth(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_DEFAULT),x_Symbol),
    Condition($(Defer($s("Int")),Power(Coth(Plus(a,Times(b,x),Times(c,Sqr(x)))),n),x),FreeQ(List(a,b,c,n),x))),
ISetDelayed(Int(Times(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),Tanh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Times(e,Log(Cosh(Plus(a,Times(b,x),Times(c,Sqr(x))))),Power(Times(C2,c),-1)),Times(Plus(Times(C2,c,d),Times(CN1,b,e)),Power(Times(C2,c),-1),Int(Tanh(Plus(a,Times(b,x),Times(c,Sqr(x)))),x))),FreeQ(List(a,b,c,d,e),x))),
ISetDelayed(Int(Times(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),Coth(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Times(e,Log(Sinh(Plus(a,Times(b,x),Times(c,Sqr(x))))),Power(Times(C2,c),-1)),Times(Plus(Times(C2,c,d),Times(CN1,b,e)),Power(Times(C2,c),-1),Int(Coth(Plus(a,Times(b,x),Times(c,Sqr(x)))),x))),FreeQ(List(a,b,c,d,e),x))),
ISetDelayed(Int(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT),Power(Tanh(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_DEFAULT)),x_Symbol),
    Condition($(Defer($s("Int")),Times(Power(Plus(d,Times(e,x)),m),Power(Tanh(Plus(a,Times(b,x),Times(c,Sqr(x)))),n)),x),FreeQ(List(a,b,c,d,e,m,n),x))),
ISetDelayed(Int(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT),Power(Coth(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_DEFAULT)),x_Symbol),
    Condition($(Defer($s("Int")),Times(Power(Plus(d,Times(e,x)),m),Power(Coth(Plus(a,Times(b,x),Times(c,Sqr(x)))),n)),x),FreeQ(List(a,b,c,d,e,m,n),x)))
  );
}
