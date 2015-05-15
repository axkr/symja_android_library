package org.matheclipse.integrate.rubi45;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.integrate.rubi45.UtilityFunctionCtors.*;
import static org.matheclipse.integrate.rubi45.UtilityFunctions.*;
import org.matheclipse.core.interfaces.IAST;

/** 
 * IndefiniteIntegrationRules from the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi -
 * rule-based integrator</a>.
 *  
 */
public class IntRules12 { 
  public static IAST RULES = List( 
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_),x_Symbol),
    Condition(Int(Cancel(Times(Power(Plus(Times(C1D2,b),Times(c,x)),Times(C2,p)),Power(Power(c,p),-1))),x),And(And(FreeQ(List(a,b,c),x),ZeroQ(Plus(Sqr(b),Times(CN1,C4,a,c)))),IntegerQ(p)))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),CN1D2),x_Symbol),
    Condition(Times(Plus(Times(C1D2,b),Times(c,x)),Power(Plus(a,Times(b,x),Times(c,Sqr(x))),CN1D2),Int(Power(Plus(Times(C1D2,b),Times(c,x)),-1),x)),And(FreeQ(List(a,b,c),x),ZeroQ(Plus(Sqr(b),Times(CN1,C4,a,c)))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_),x_Symbol),
    Condition(Times(Plus(b,Times(C2,c,x)),Power(Plus(a,Times(b,x),Times(c,Sqr(x))),p),Power(Times(C2,c,Plus(Times(C2,p),C1)),-1)),And(And(And(FreeQ(List(a,b,c,p),x),ZeroQ(Plus(Sqr(b),Times(CN1,C4,a,c)))),Not(IntegerQ(p))),NonzeroQ(Plus(Times(C2,p),C1))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),-1),x_Symbol),
    Condition(Module(List(Set(q,Rt(Plus(Sqr(b),Times(CN1,C4,a,c)),C2))),Plus(Times(c,Power(q,-1),Int(Power(Simp(Plus(Times(C1D2,b),Times(CN1,C1D2,q),Times(c,x)),x),-1),x)),Times(CN1,c,Power(q,-1),Int(Power(Simp(Plus(Times(C1D2,b),Times(C1D2,q),Times(c,x)),x),-1),x)))),And(And(And(FreeQ(List(a,b,c),x),NonzeroQ(Plus(Sqr(b),Times(CN1,C4,a,c)))),PosQ(Plus(Sqr(b),Times(CN1,C4,a,c)))),PerfectSquareQ(Plus(Sqr(b),Times(CN1,C4,a,c)))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),-1),x_Symbol),
    Condition(Times(CN2,ArcTanh(Times(Plus(b,Times(C2,c,x)),Power(Rt(Plus(Sqr(b),Times(CN1,C4,a,c)),C2),-1))),Power(Rt(Plus(Sqr(b),Times(CN1,C4,a,c)),C2),-1)),And(And(And(FreeQ(List(a,b,c),x),NonzeroQ(Plus(Sqr(b),Times(CN1,C4,a,c)))),PosQ(Plus(Sqr(b),Times(CN1,C4,a,c)))),Not(PerfectSquareQ(Plus(Sqr(b),Times(CN1,C4,a,c))))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),-1),x_Symbol),
    Condition(Times(C2,ArcTan(Plus(Times(b,Power(Rt(Plus(Times(C4,a,c),Negate(Sqr(b))),C2),-1)),Times(C2,c,x,Power(Rt(Plus(Times(C4,a,c),Negate(Sqr(b))),C2),-1)))),Power(Rt(Plus(Times(C4,a,c),Negate(Sqr(b))),C2),-1)),And(And(And(FreeQ(List(a,b,c),x),NonzeroQ(Plus(Sqr(b),Times(CN1,C4,a,c)))),NegQ(Plus(Sqr(b),Times(CN1,C4,a,c)))),RationalQ(Times(b,Power(Rt(Plus(Times(C4,a,c),Negate(Sqr(b))),C2),-1)))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),-1),x_Symbol),
    Condition(Times(C2,ArcTan(Times(Plus(b,Times(C2,c,x)),Power(Rt(Plus(Times(C4,a,c),Negate(Sqr(b))),C2),-1))),Power(Rt(Plus(Times(C4,a,c),Negate(Sqr(b))),C2),-1)),And(And(FreeQ(List(a,b,c),x),NonzeroQ(Plus(Sqr(b),Times(CN1,C4,a,c)))),NegQ(Plus(Sqr(b),Times(CN1,C4,a,c)))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),CN1D2),x_Symbol),
    Condition(Times(ArcSinh(Times(Plus(b,Times(C2,c,x)),Power(Times(Rt(c,C2),Sqrt(Plus(Times(C4,a),Times(CN1,Sqr(b),Power(c,-1))))),-1))),Power(Rt(c,C2),-1)),And(And(And(FreeQ(List(a,b,c),x),NonzeroQ(Plus(Sqr(b),Times(CN1,C4,a,c)))),PositiveQ(Plus(Times(C4,a),Times(CN1,Sqr(b),Power(c,-1))))),PosQ(c)))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),CN1D2),x_Symbol),
    Condition(Times(CN1,ArcSin(Times(Plus(b,Times(C2,c,x)),Power(Times(Rt(Negate(c),C2),Sqrt(Plus(Times(C4,a),Times(CN1,Sqr(b),Power(c,-1))))),-1))),Power(Rt(Negate(c),C2),-1)),And(And(And(FreeQ(List(a,b,c),x),NonzeroQ(Plus(Sqr(b),Times(CN1,C4,a,c)))),PositiveQ(Plus(Times(C4,a),Times(CN1,Sqr(b),Power(c,-1))))),NegQ(c)))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),CN1D2),x_Symbol),
    Condition(Times(C2,Subst(Int(Power(Plus(Times(C4,c),Negate(Sqr(x))),-1),x),x,Times(Plus(b,Times(C2,c,x)),Power(Plus(a,Times(b,x),Times(c,Sqr(x))),CN1D2)))),And(And(FreeQ(List(a,b,c),x),NonzeroQ(Plus(Sqr(b),Times(CN1,C4,a,c)))),Not(PositiveQ(Plus(Times(C4,a),Times(CN1,Sqr(b),Power(c,-1)))))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),QQ(-3L,2L)),x_Symbol),
    Condition(Times(CN2,Plus(b,Times(C2,c,x)),Power(Times(Plus(Sqr(b),Times(CN1,C4,a,c)),Sqrt(Plus(a,Times(b,x),Times(c,Sqr(x))))),-1)),And(FreeQ(List(a,b,c),x),NonzeroQ(Plus(Sqr(b),Times(CN1,C4,a,c)))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_),x_Symbol),
    Condition(Module(List(Set(q,Rt(Plus(Sqr(b),Times(CN1,C4,a,c)),C2))),Times(Power(Power(c,p),-1),Int(Times(Power(Simp(Plus(Times(C1D2,b),Times(CN1,C1D2,q),Times(c,x)),x),p),Power(Simp(Plus(Times(C1D2,b),Times(C1D2,q),Times(c,x)),x),p)),x))),And(And(And(FreeQ(List(a,b,c),x),NonzeroQ(Plus(Sqr(b),Times(CN1,C4,a,c)))),PositiveIntegerQ(p)),PerfectSquareQ(Plus(Sqr(b),Times(CN1,C4,a,c)))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_),x_Symbol),
    Condition(Int(ExpandIntegrand(Power(Plus(a,Times(b,x),Times(c,Sqr(x))),p),x),x),And(And(And(FreeQ(List(a,b,c),x),NonzeroQ(Plus(Sqr(b),Times(CN1,C4,a,c)))),PositiveIntegerQ(p)),Not(PerfectSquareQ(Plus(Sqr(b),Times(CN1,C4,a,c))))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_),x_Symbol),
    Condition(Plus(Times(Plus(b,Times(C2,c,x)),Power(Plus(a,Times(b,x),Times(c,Sqr(x))),p),Power(Times(C2,c,Plus(Times(C2,p),C1)),-1)),Times(CN1,p,Plus(Sqr(b),Times(CN1,C4,a,c)),Power(Times(C2,c,Plus(Times(C2,p),C1)),-1),Int(Power(Plus(a,Times(b,x),Times(c,Sqr(x))),Plus(p,Negate(C1))),x))),And(And(And(And(FreeQ(List(a,b,c),x),NonzeroQ(Plus(Sqr(b),Times(CN1,C4,a,c)))),RationalQ(p)),Greater(p,C0)),Not(IntegerQ(p))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_),x_Symbol),
    Condition(Plus(Times(Plus(b,Times(C2,c,x)),Power(Plus(a,Times(b,x),Times(c,Sqr(x))),Plus(p,C1)),Power(Times(Plus(p,C1),Plus(Sqr(b),Times(CN1,C4,a,c))),-1)),Times(CN1,C2,c,Plus(Times(C2,p),C3),Power(Times(Plus(p,C1),Plus(Sqr(b),Times(CN1,C4,a,c))),-1),Int(Power(Plus(a,Times(b,x),Times(c,Sqr(x))),Plus(p,C1)),x))),And(And(And(And(FreeQ(List(a,b,c),x),NonzeroQ(Plus(Sqr(b),Times(CN1,C4,a,c)))),RationalQ(p)),Less(p,CN1)),Unequal(p,QQ(-3L,2L))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_),x_Symbol),
    Condition(Times(Power(C2,Plus(p,Negate(C1))),Plus(b,Negate(Rt(Plus(Sqr(b),Times(CN1,C4,a,c)),C2)),Times(C2,c,x)),Power(Plus(a,Times(b,x),Times(c,Sqr(x))),p),Power(Times(c,Plus(p,C1),Power(Times(Plus(b,Rt(Plus(Sqr(b),Times(CN1,C4,a,c)),C2),Times(C2,c,x)),Power(Rt(Plus(Sqr(b),Times(CN1,C4,a,c)),C2),-1)),p)),-1),Hypergeometric2F1(Negate(p),Plus(p,C1),Plus(p,C2),Times(Plus(Negate(b),Rt(Plus(Sqr(b),Times(CN1,C4,a,c)),C2),Times(CN1,C2,c,x)),Power(Times(C2,Rt(Plus(Sqr(b),Times(CN1,C4,a,c)),C2)),-1)))),And(And(FreeQ(List(a,b,c,p),x),NonzeroQ(Plus(Sqr(b),Times(CN1,C4,a,c)))),NonzeroQ(Plus(p,C1))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,u_),Times(c_DEFAULT,Sqr(v_))),p_),x_Symbol),
    Condition(Times(Power(Coefficient(u,x,C1),-1),Subst(Int(Power(Plus(a,Times(b,x),Times(c,Sqr(x))),p),x),x,u)),And(And(And(FreeQ(List(a,b,c,p),x),ZeroQ(Plus(u,Negate(v)))),LinearQ(u,x)),NonzeroQ(Plus(u,Negate(x)))))),
ISetDelayed(Int(Power(u_,p_),x_Symbol),
    Condition(Int(Power(ExpandToSum(u,x),p),x),And(And(FreeQ(p,x),QuadraticQ(u,x)),Not(QuadraticMatchQ(u,x)))))
  );
}
