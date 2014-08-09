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
public class IntRules99 { 
  public static IAST RULES = List( 
ISetDelayed(Int(Sinh(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_DEFAULT)),x_Symbol),
    Condition(Plus(Times(C1D2,Int(Power(E,Plus(a,Times(b,x),Times(c,Sqr(x)))),x)),Times(CN1,C1D2,Int(Power(E,Plus(Times(CN1,a),Times(CN1,b,x),Times(CN1,c,Sqr(x)))),x))),FreeQ(List(a,b,c),x))),
ISetDelayed(Int(Cosh(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_DEFAULT)),x_Symbol),
    Condition(Plus(Times(C1D2,Int(Power(E,Plus(a,Times(b,x),Times(c,Sqr(x)))),x)),Times(C1D2,Int(Power(E,Plus(Times(CN1,a),Times(CN1,b,x),Times(CN1,c,Sqr(x)))),x))),FreeQ(List(a,b,c),x))),
ISetDelayed(Int(Power(Sinh(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_DEFAULT)),pn_),x_Symbol),
    Condition(Int(ExpandTrigReduce(Power(Sinh(Plus(a,Times(b,x),Times(c,Sqr(x)))),pn),x),x),And(And(FreeQ(List(a,b,c),x),IntegerQ(pn)),Greater(pn,C1)))),
ISetDelayed(Int(Power(Cosh(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_DEFAULT)),pn_),x_Symbol),
    Condition(Int(ExpandTrigReduce(Power(Cosh(Plus(a,Times(b,x),Times(c,Sqr(x)))),pn),x),x),And(And(FreeQ(List(a,b,c),x),IntegerQ(pn)),Greater(pn,C1)))),
ISetDelayed(Int(Power(Sinh(v_),pn_DEFAULT),x_Symbol),
    Condition(Int(Power(Sinh(ExpandToSum(v,x)),pn),x),And(And(PositiveIntegerQ(pn),QuadraticQ(v,x)),Not(QuadraticMatchQ(v,x))))),
ISetDelayed(Int(Power(Cosh(v_),pn_DEFAULT),x_Symbol),
    Condition(Int(Power(Cosh(ExpandToSum(v,x)),pn),x),And(And(PositiveIntegerQ(pn),QuadraticQ(v,x)),Not(QuadraticMatchQ(v,x))))),
ISetDelayed(Int(Times(Plus(Times(x_,pe_DEFAULT),pd_DEFAULT),Sinh(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_DEFAULT))),x_Symbol),
    Condition(Times(pe,Cosh(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),CN1)),And(FreeQ(List(a,b,c,pd,pe),x),ZeroQ(Plus(Times(b,pe),Times(CN1,C2,c,pd)))))),
ISetDelayed(Int(Times(Cosh(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_DEFAULT)),Plus(Times(x_,pe_DEFAULT),pd_DEFAULT)),x_Symbol),
    Condition(Times(pe,Sinh(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),CN1)),And(FreeQ(List(a,b,c,pd,pe),x),ZeroQ(Plus(Times(b,pe),Times(CN1,C2,c,pd)))))),
ISetDelayed(Int(Times(Plus(Times(x_,pe_DEFAULT),pd_DEFAULT),Sinh(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_DEFAULT))),x_Symbol),
    Condition(Plus(Times(pe,Cosh(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),CN1)),Times(CN1,Plus(Times(b,pe),Times(CN1,C2,c,pd)),Power(Times(C2,c),CN1),Int(Sinh(Plus(a,Times(b,x),Times(c,Sqr(x)))),x))),And(FreeQ(List(a,b,c,pd,pe),x),NonzeroQ(Plus(Times(b,pe),Times(CN1,C2,c,pd)))))),
ISetDelayed(Int(Times(Cosh(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_DEFAULT)),Plus(Times(x_,pe_DEFAULT),pd_DEFAULT)),x_Symbol),
    Condition(Plus(Times(pe,Sinh(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),CN1)),Times(CN1,Plus(Times(b,pe),Times(CN1,C2,c,pd)),Power(Times(C2,c),CN1),Int(Cosh(Plus(a,Times(b,x),Times(c,Sqr(x)))),x))),And(FreeQ(List(a,b,c,pd,pe),x),NonzeroQ(Plus(Times(b,pe),Times(CN1,C2,c,pd)))))),
ISetDelayed(Int(Times(Power(Plus(Times(x_,pe_DEFAULT),pd_DEFAULT),m_),Sinh(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_DEFAULT))),x_Symbol),
    Condition(Plus(Times(pe,Power(Plus(pd,Times(pe,x)),Plus(m,Times(CN1,C1))),Cosh(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),CN1)),Times(CN1,Sqr(pe),Plus(m,Times(CN1,C1)),Power(Times(C2,c),CN1),Int(Times(Power(Plus(pd,Times(pe,x)),Plus(m,Times(CN1,C2))),Cosh(Plus(a,Times(b,x),Times(c,Sqr(x))))),x))),And(And(And(FreeQ(List(a,b,c,pd,pe),x),RationalQ(m)),Greater(m,C1)),ZeroQ(Plus(Times(b,pe),Times(CN1,C2,c,pd)))))),
ISetDelayed(Int(Times(Cosh(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_DEFAULT)),Power(Plus(Times(x_,pe_DEFAULT),pd_DEFAULT),m_)),x_Symbol),
    Condition(Plus(Times(pe,Power(Plus(pd,Times(pe,x)),Plus(m,Times(CN1,C1))),Sinh(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),CN1)),Times(CN1,Sqr(pe),Plus(m,Times(CN1,C1)),Power(Times(C2,c),CN1),Int(Times(Power(Plus(pd,Times(pe,x)),Plus(m,Times(CN1,C2))),Sinh(Plus(a,Times(b,x),Times(c,Sqr(x))))),x))),And(And(And(FreeQ(List(a,b,c,pd,pe),x),RationalQ(m)),Greater(m,C1)),ZeroQ(Plus(Times(b,pe),Times(CN1,C2,c,pd)))))),
ISetDelayed(Int(Times(Power(Plus(Times(x_,pe_DEFAULT),pd_DEFAULT),m_),Sinh(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_DEFAULT))),x_Symbol),
    Condition(Plus(Times(pe,Power(Plus(pd,Times(pe,x)),Plus(m,Times(CN1,C1))),Cosh(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),CN1)),Times(CN1,Plus(Times(b,pe),Times(CN1,C2,c,pd)),Power(Times(C2,c),CN1),Int(Times(Power(Plus(pd,Times(pe,x)),Plus(m,Times(CN1,C1))),Sinh(Plus(a,Times(b,x),Times(c,Sqr(x))))),x)),Times(CN1,Sqr(pe),Plus(m,Times(CN1,C1)),Power(Times(C2,c),CN1),Int(Times(Power(Plus(pd,Times(pe,x)),Plus(m,Times(CN1,C2))),Cosh(Plus(a,Times(b,x),Times(c,Sqr(x))))),x))),And(And(And(FreeQ(List(a,b,c,pd,pe),x),RationalQ(m)),Greater(m,C1)),NonzeroQ(Plus(Times(b,pe),Times(CN1,C2,c,pd)))))),
ISetDelayed(Int(Times(Cosh(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_DEFAULT)),Power(Plus(Times(x_,pe_DEFAULT),pd_DEFAULT),m_)),x_Symbol),
    Condition(Plus(Times(pe,Power(Plus(pd,Times(pe,x)),Plus(m,Times(CN1,C1))),Sinh(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(C2,c),CN1)),Times(CN1,Plus(Times(b,pe),Times(CN1,C2,c,pd)),Power(Times(C2,c),CN1),Int(Times(Power(Plus(pd,Times(pe,x)),Plus(m,Times(CN1,C1))),Cosh(Plus(a,Times(b,x),Times(c,Sqr(x))))),x)),Times(CN1,Sqr(pe),Plus(m,Times(CN1,C1)),Power(Times(C2,c),CN1),Int(Times(Power(Plus(pd,Times(pe,x)),Plus(m,Times(CN1,C2))),Sinh(Plus(a,Times(b,x),Times(c,Sqr(x))))),x))),And(And(And(FreeQ(List(a,b,c,pd,pe),x),RationalQ(m)),Greater(m,C1)),NonzeroQ(Plus(Times(b,pe),Times(CN1,C2,c,pd)))))),
ISetDelayed(Int(Times(Power(Plus(Times(x_,pe_DEFAULT),pd_DEFAULT),m_),Sinh(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_DEFAULT))),x_Symbol),
    Condition(Plus(Times(Power(Plus(pd,Times(pe,x)),Plus(m,C1)),Sinh(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(pe,Plus(m,C1)),CN1)),Times(CN1,C2,c,Power(Times(Sqr(pe),Plus(m,C1)),CN1),Int(Times(Power(Plus(pd,Times(pe,x)),Plus(m,C2)),Cosh(Plus(a,Times(b,x),Times(c,Sqr(x))))),x))),And(And(And(FreeQ(List(a,b,c,pd,pe),x),RationalQ(m)),Less(m,CN1)),ZeroQ(Plus(Times(b,pe),Times(CN1,C2,c,pd)))))),
ISetDelayed(Int(Times(Cosh(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_DEFAULT)),Power(Plus(Times(x_,pe_DEFAULT),pd_DEFAULT),m_)),x_Symbol),
    Condition(Plus(Times(Power(Plus(pd,Times(pe,x)),Plus(m,C1)),Cosh(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(pe,Plus(m,C1)),CN1)),Times(CN1,C2,c,Power(Times(Sqr(pe),Plus(m,C1)),CN1),Int(Times(Power(Plus(pd,Times(pe,x)),Plus(m,C2)),Sinh(Plus(a,Times(b,x),Times(c,Sqr(x))))),x))),And(And(And(FreeQ(List(a,b,c,pd,pe),x),RationalQ(m)),Less(m,CN1)),ZeroQ(Plus(Times(b,pe),Times(CN1,C2,c,pd)))))),
ISetDelayed(Int(Times(Power(Plus(Times(x_,pe_DEFAULT),pd_DEFAULT),m_),Sinh(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_DEFAULT))),x_Symbol),
    Condition(Plus(Times(Power(Plus(pd,Times(pe,x)),Plus(m,C1)),Sinh(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(pe,Plus(m,C1)),CN1)),Times(CN1,Plus(Times(b,pe),Times(CN1,C2,c,pd)),Power(Times(Sqr(pe),Plus(m,C1)),CN1),Int(Times(Power(Plus(pd,Times(pe,x)),Plus(m,C1)),Cosh(Plus(a,Times(b,x),Times(c,Sqr(x))))),x)),Times(CN1,C2,c,Power(Times(Sqr(pe),Plus(m,C1)),CN1),Int(Times(Power(Plus(pd,Times(pe,x)),Plus(m,C2)),Cosh(Plus(a,Times(b,x),Times(c,Sqr(x))))),x))),And(And(And(FreeQ(List(a,b,c,pd,pe),x),RationalQ(m)),Less(m,CN1)),NonzeroQ(Plus(Times(b,pe),Times(CN1,C2,c,pd)))))),
ISetDelayed(Int(Times(Cosh(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_DEFAULT)),Power(Plus(Times(x_,pe_DEFAULT),pd_DEFAULT),m_)),x_Symbol),
    Condition(Plus(Times(Power(Plus(pd,Times(pe,x)),Plus(m,C1)),Cosh(Plus(a,Times(b,x),Times(c,Sqr(x)))),Power(Times(pe,Plus(m,C1)),CN1)),Times(CN1,Plus(Times(b,pe),Times(CN1,C2,c,pd)),Power(Times(Sqr(pe),Plus(m,C1)),CN1),Int(Times(Power(Plus(pd,Times(pe,x)),Plus(m,C1)),Sinh(Plus(a,Times(b,x),Times(c,Sqr(x))))),x)),Times(CN1,C2,c,Power(Times(Sqr(pe),Plus(m,C1)),CN1),Int(Times(Power(Plus(pd,Times(pe,x)),Plus(m,C2)),Sinh(Plus(a,Times(b,x),Times(c,Sqr(x))))),x))),And(And(And(FreeQ(List(a,b,c,pd,pe),x),RationalQ(m)),Less(m,CN1)),NonzeroQ(Plus(Times(b,pe),Times(CN1,C2,c,pd)))))),
ISetDelayed(Int(Times(Power(Plus(Times(x_,pe_DEFAULT),pd_DEFAULT),m_DEFAULT),Sinh(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_DEFAULT))),x_Symbol),
    Condition($(Defer($s("Int")),Times(Power(Plus(pd,Times(pe,x)),m),Sinh(Plus(a,Times(b,x),Times(c,Sqr(x))))),x),FreeQ(List(a,b,c,pd,pe,m),x))),
ISetDelayed(Int(Times(Cosh(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_DEFAULT)),Power(Plus(Times(x_,pe_DEFAULT),pd_DEFAULT),m_DEFAULT)),x_Symbol),
    Condition($(Defer($s("Int")),Times(Power(Plus(pd,Times(pe,x)),m),Cosh(Plus(a,Times(b,x),Times(c,Sqr(x))))),x),FreeQ(List(a,b,c,pd,pe,m),x))),
ISetDelayed(Int(Times(Power(Plus(Times(x_,pe_DEFAULT),pd_DEFAULT),m_DEFAULT),Power(Sinh(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_DEFAULT)),pn_)),x_Symbol),
    Condition(Int(ExpandTrigReduce(Power(Plus(pd,Times(pe,x)),m),Power(Sinh(Plus(a,Times(b,x),Times(c,Sqr(x)))),pn),x),x),And(And(FreeQ(List(a,b,c,pd,pe,m),x),IntegerQ(pn)),Greater(pn,C1)))),
ISetDelayed(Int(Times(Power(Cosh(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_DEFAULT)),pn_),Power(Plus(Times(x_,pe_DEFAULT),pd_DEFAULT),m_DEFAULT)),x_Symbol),
    Condition(Int(ExpandTrigReduce(Power(Plus(pd,Times(pe,x)),m),Power(Cosh(Plus(a,Times(b,x),Times(c,Sqr(x)))),pn),x),x),And(And(FreeQ(List(a,b,c,pd,pe,m),x),IntegerQ(pn)),Greater(pn,C1)))),
ISetDelayed(Int(Times(Power(Sinh(v_),pn_DEFAULT),Power(u_,m_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(ExpandToSum(u,x),m),Power(Sinh(ExpandToSum(v,x)),pn)),x),And(And(And(And(FreeQ(m,x),PositiveIntegerQ(pn)),LinearQ(u,x)),QuadraticQ(v,x)),Not(And(LinearMatchQ(u,x),QuadraticMatchQ(v,x)))))),
ISetDelayed(Int(Times(Power(Cosh(v_),pn_DEFAULT),Power(u_,m_DEFAULT)),x_Symbol),
    Condition(Int(Times(Power(ExpandToSum(u,x),m),Power(Cosh(ExpandToSum(v,x)),pn)),x),And(And(And(And(FreeQ(m,x),PositiveIntegerQ(pn)),LinearQ(u,x)),QuadraticQ(v,x)),Not(And(LinearMatchQ(u,x),QuadraticMatchQ(v,x)))))),
ISetDelayed(Int(Power(Tanh(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_DEFAULT)),pn_DEFAULT),x_Symbol),
    Condition($(Defer($s("Int")),Power(Tanh(Plus(a,Times(b,x),Times(c,Sqr(x)))),pn),x),FreeQ(List(a,b,c,pn),x))),
ISetDelayed(Int(Power(Coth(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_DEFAULT)),pn_DEFAULT),x_Symbol),
    Condition($(Defer($s("Int")),Power(Coth(Plus(a,Times(b,x),Times(c,Sqr(x)))),pn),x),FreeQ(List(a,b,c,pn),x))),
ISetDelayed(Int(Times(Plus(Times(x_,pe_DEFAULT),pd_DEFAULT),Tanh(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_DEFAULT))),x_Symbol),
    Condition(Plus(Times(pe,Log(Cosh(Plus(a,Times(b,x),Times(c,Sqr(x))))),Power(Times(C2,c),CN1)),Times(Plus(Times(C2,c,pd),Times(CN1,b,pe)),Power(Times(C2,c),CN1),Int(Tanh(Plus(a,Times(b,x),Times(c,Sqr(x)))),x))),FreeQ(List(a,b,c,pd,pe),x))),
ISetDelayed(Int(Times(Coth(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_DEFAULT)),Plus(Times(x_,pe_DEFAULT),pd_DEFAULT)),x_Symbol),
    Condition(Plus(Times(pe,Log(Sinh(Plus(a,Times(b,x),Times(c,Sqr(x))))),Power(Times(C2,c),CN1)),Times(Plus(Times(C2,c,pd),Times(CN1,b,pe)),Power(Times(C2,c),CN1),Int(Coth(Plus(a,Times(b,x),Times(c,Sqr(x)))),x))),FreeQ(List(a,b,c,pd,pe),x))),
ISetDelayed(Int(Times(Power(Plus(Times(x_,pe_DEFAULT),pd_DEFAULT),m_DEFAULT),Power(Tanh(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_DEFAULT)),pn_DEFAULT)),x_Symbol),
    Condition($(Defer($s("Int")),Times(Power(Plus(pd,Times(pe,x)),m),Power(Tanh(Plus(a,Times(b,x),Times(c,Sqr(x)))),pn)),x),FreeQ(List(a,b,c,pd,pe,m,pn),x))),
ISetDelayed(Int(Times(Power(Coth(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_DEFAULT)),pn_DEFAULT),Power(Plus(Times(x_,pe_DEFAULT),pd_DEFAULT),m_DEFAULT)),x_Symbol),
    Condition($(Defer($s("Int")),Times(Power(Plus(pd,Times(pe,x)),m),Power(Coth(Plus(a,Times(b,x),Times(c,Sqr(x)))),pn)),x),FreeQ(List(a,b,c,pd,pe,m,pn),x)))
  );
}
