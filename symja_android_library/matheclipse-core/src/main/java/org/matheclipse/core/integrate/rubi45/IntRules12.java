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
public class IntRules12 { 
  public static IAST RULES = List( 
ISetDelayed(Int(Power(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_),p_),x_Symbol),
    Condition(Int(Cancel(Times(Power(Plus(Times(C1D2,b),Times(c,x)),Times(C2,p)),Power(Power(c,p),CN1))),x),And(And(FreeQ(List(a,b,c),x),ZeroQ(Plus(Sqr(b),Times(CN1,C4,a,c)))),IntegerQ(p)))),
ISetDelayed(Int(Power(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_),CN1D2),x_Symbol),
    Condition(Times(Plus(Times(C1D2,b),Times(c,x)),Power(Plus(a,Times(b,x),Times(c,Sqr(x))),CN1D2),Int(Power(Plus(Times(C1D2,b),Times(c,x)),CN1),x)),And(FreeQ(List(a,b,c),x),ZeroQ(Plus(Sqr(b),Times(CN1,C4,a,c)))))),
ISetDelayed(Int(Power(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_),p_),x_Symbol),
    Condition(Times(Plus(b,Times(C2,c,x)),Power(Plus(a,Times(b,x),Times(c,Sqr(x))),p),Power(Times(C2,c,Plus(Times(C2,p),C1)),CN1)),And(And(And(FreeQ(List(a,b,c,p),x),ZeroQ(Plus(Sqr(b),Times(CN1,C4,a,c)))),Not(IntegerQ(p))),NonzeroQ(Plus(Times(C2,p),C1))))),
ISetDelayed(Int(Power(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_),CN1),x_Symbol),
    Condition(Module(List(Set(q,Rt(Plus(Sqr(b),Times(CN1,C4,a,c)),C2))),Plus(Times(c,Power(q,CN1),Int(Power(Simp(Plus(Times(C1D2,b),Times(CN1,C1D2,q),Times(c,x)),x),CN1),x)),Times(CN1,c,Power(q,CN1),Int(Power(Simp(Plus(Times(C1D2,b),Times(C1D2,q),Times(c,x)),x),CN1),x)))),And(And(And(FreeQ(List(a,b,c),x),NonzeroQ(Plus(Sqr(b),Times(CN1,C4,a,c)))),PosQ(Plus(Sqr(b),Times(CN1,C4,a,c)))),PerfectSquareQ(Plus(Sqr(b),Times(CN1,C4,a,c)))))),
ISetDelayed(Int(Power(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_),CN1),x_Symbol),
    Condition(Times(CN2,ArcTanh(Times(Plus(b,Times(C2,c,x)),Power(Rt(Plus(Sqr(b),Times(CN1,C4,a,c)),C2),CN1))),Power(Rt(Plus(Sqr(b),Times(CN1,C4,a,c)),C2),CN1)),And(And(And(FreeQ(List(a,b,c),x),NonzeroQ(Plus(Sqr(b),Times(CN1,C4,a,c)))),PosQ(Plus(Sqr(b),Times(CN1,C4,a,c)))),Not(PerfectSquareQ(Plus(Sqr(b),Times(CN1,C4,a,c))))))),
ISetDelayed(Int(Power(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_),CN1),x_Symbol),
    Condition(Times(C2,ArcTan(Plus(Times(b,Power(Rt(Plus(Times(C4,a,c),Times(CN1,Sqr(b))),C2),CN1)),Times(C2,c,x,Power(Rt(Plus(Times(C4,a,c),Times(CN1,Sqr(b))),C2),CN1)))),Power(Rt(Plus(Times(C4,a,c),Times(CN1,Sqr(b))),C2),CN1)),And(And(And(FreeQ(List(a,b,c),x),NonzeroQ(Plus(Sqr(b),Times(CN1,C4,a,c)))),NegQ(Plus(Sqr(b),Times(CN1,C4,a,c)))),RationalQ(Times(b,Power(Rt(Plus(Times(C4,a,c),Times(CN1,Sqr(b))),C2),CN1)))))),
ISetDelayed(Int(Power(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_),CN1),x_Symbol),
    Condition(Times(C2,ArcTan(Times(Plus(b,Times(C2,c,x)),Power(Rt(Plus(Times(C4,a,c),Times(CN1,Sqr(b))),C2),CN1))),Power(Rt(Plus(Times(C4,a,c),Times(CN1,Sqr(b))),C2),CN1)),And(And(FreeQ(List(a,b,c),x),NonzeroQ(Plus(Sqr(b),Times(CN1,C4,a,c)))),NegQ(Plus(Sqr(b),Times(CN1,C4,a,c)))))),
ISetDelayed(Int(Power(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_),CN1D2),x_Symbol),
    Condition(Times(ArcSinh(Times(Plus(b,Times(C2,c,x)),Power(Times(Rt(c,C2),Sqrt(Plus(Times(C4,a),Times(CN1,Sqr(b),Power(c,CN1))))),CN1))),Power(Rt(c,C2),CN1)),And(And(And(FreeQ(List(a,b,c),x),NonzeroQ(Plus(Sqr(b),Times(CN1,C4,a,c)))),PositiveQ(Plus(Times(C4,a),Times(CN1,Sqr(b),Power(c,CN1))))),PosQ(c)))),
ISetDelayed(Int(Power(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_),CN1D2),x_Symbol),
    Condition(Times(CN1,ArcSin(Times(Plus(b,Times(C2,c,x)),Power(Times(Rt(Times(CN1,c),C2),Sqrt(Plus(Times(C4,a),Times(CN1,Sqr(b),Power(c,CN1))))),CN1))),Power(Rt(Times(CN1,c),C2),CN1)),And(And(And(FreeQ(List(a,b,c),x),NonzeroQ(Plus(Sqr(b),Times(CN1,C4,a,c)))),PositiveQ(Plus(Times(C4,a),Times(CN1,Sqr(b),Power(c,CN1))))),NegQ(c)))),
ISetDelayed(Int(Power(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_),CN1D2),x_Symbol),
    Condition(Times(C2,Subst(Int(Power(Plus(Times(C4,c),Times(CN1,Sqr(x))),CN1),x),x,Times(Plus(b,Times(C2,c,x)),Power(Plus(a,Times(b,x),Times(c,Sqr(x))),CN1D2)))),And(And(FreeQ(List(a,b,c),x),NonzeroQ(Plus(Sqr(b),Times(CN1,C4,a,c)))),Not(PositiveQ(Plus(Times(C4,a),Times(CN1,Sqr(b),Power(c,CN1)))))))),
ISetDelayed(Int(Power(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_),QQ(-3L,2L)),x_Symbol),
    Condition(Times(CN2,Plus(b,Times(C2,c,x)),Power(Times(Plus(Sqr(b),Times(CN1,C4,a,c)),Sqrt(Plus(a,Times(b,x),Times(c,Sqr(x))))),CN1)),And(FreeQ(List(a,b,c),x),NonzeroQ(Plus(Sqr(b),Times(CN1,C4,a,c)))))),
ISetDelayed(Int(Power(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_),p_),x_Symbol),
    Condition(Module(List(Set(q,Rt(Plus(Sqr(b),Times(CN1,C4,a,c)),C2))),Times(Power(Power(c,p),CN1),Int(Times(Power(Simp(Plus(Times(C1D2,b),Times(CN1,C1D2,q),Times(c,x)),x),p),Power(Simp(Plus(Times(C1D2,b),Times(C1D2,q),Times(c,x)),x),p)),x))),And(And(And(FreeQ(List(a,b,c),x),NonzeroQ(Plus(Sqr(b),Times(CN1,C4,a,c)))),PositiveIntegerQ(p)),PerfectSquareQ(Plus(Sqr(b),Times(CN1,C4,a,c)))))),
ISetDelayed(Int(Power(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_),p_),x_Symbol),
    Condition(Int(ExpandIntegrand(Power(Plus(a,Times(b,x),Times(c,Sqr(x))),p),x),x),And(And(And(FreeQ(List(a,b,c),x),NonzeroQ(Plus(Sqr(b),Times(CN1,C4,a,c)))),PositiveIntegerQ(p)),Not(PerfectSquareQ(Plus(Sqr(b),Times(CN1,C4,a,c))))))),
ISetDelayed(Int(Power(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_),p_),x_Symbol),
    Condition(Plus(Times(Plus(b,Times(C2,c,x)),Power(Plus(a,Times(b,x),Times(c,Sqr(x))),p),Power(Times(C2,c,Plus(Times(C2,p),C1)),CN1)),Times(CN1,p,Plus(Sqr(b),Times(CN1,C4,a,c)),Power(Times(C2,c,Plus(Times(C2,p),C1)),CN1),Int(Power(Plus(a,Times(b,x),Times(c,Sqr(x))),Plus(p,Times(CN1,C1))),x))),And(And(And(And(FreeQ(List(a,b,c),x),NonzeroQ(Plus(Sqr(b),Times(CN1,C4,a,c)))),RationalQ(p)),Greater(p,C0)),Not(IntegerQ(p))))),
ISetDelayed(Int(Power(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_),p_),x_Symbol),
    Condition(Plus(Times(Plus(b,Times(C2,c,x)),Power(Plus(a,Times(b,x),Times(c,Sqr(x))),Plus(p,C1)),Power(Times(Plus(p,C1),Plus(Sqr(b),Times(CN1,C4,a,c))),CN1)),Times(CN1,C2,c,Plus(Times(C2,p),C3),Power(Times(Plus(p,C1),Plus(Sqr(b),Times(CN1,C4,a,c))),CN1),Int(Power(Plus(a,Times(b,x),Times(c,Sqr(x))),Plus(p,C1)),x))),And(And(And(And(FreeQ(List(a,b,c),x),NonzeroQ(Plus(Sqr(b),Times(CN1,C4,a,c)))),RationalQ(p)),Less(p,CN1)),Unequal(p,QQ(-3L,2L))))),
ISetDelayed(Int(Power(Plus(Times(Sqr(x_),c_DEFAULT),Times(b_DEFAULT,x_),a_),p_),x_Symbol),
    Condition(Times(Power(C2,Plus(p,Times(CN1,C1))),Plus(b,Times(CN1,Rt(Plus(Sqr(b),Times(CN1,C4,a,c)),C2)),Times(C2,c,x)),Power(Plus(a,Times(b,x),Times(c,Sqr(x))),p),Power(Times(c,Plus(p,C1),Power(Times(Plus(b,Rt(Plus(Sqr(b),Times(CN1,C4,a,c)),C2),Times(C2,c,x)),Power(Rt(Plus(Sqr(b),Times(CN1,C4,a,c)),C2),CN1)),p)),CN1),Hypergeometric2F1(Times(CN1,p),Plus(p,C1),Plus(p,C2),Times(Plus(Times(CN1,b),Rt(Plus(Sqr(b),Times(CN1,C4,a,c)),C2),Times(CN1,C2,c,x)),Power(Times(C2,Rt(Plus(Sqr(b),Times(CN1,C4,a,c)),C2)),CN1)))),And(And(FreeQ(List(a,b,c,p),x),NonzeroQ(Plus(Sqr(b),Times(CN1,C4,a,c)))),NonzeroQ(Plus(p,C1))))),
ISetDelayed(Int(Power(Plus(Times(Sqr(v_),c_DEFAULT),Times(b_DEFAULT,u_),a_),p_),x_Symbol),
    Condition(Times(Power(Coefficient(u,x,C1),CN1),Subst(Int(Power(Plus(a,Times(b,x),Times(c,Sqr(x))),p),x),x,u)),And(And(And(FreeQ(List(a,b,c,p),x),ZeroQ(Plus(u,Times(CN1,v)))),LinearQ(u,x)),NonzeroQ(Plus(u,Times(CN1,x)))))),
ISetDelayed(Int(Power(u_,p_),x_Symbol),
    Condition(Int(Power(ExpandToSum(u,x),p),x),And(And(FreeQ(p,x),QuadraticQ(u,x)),Not(QuadraticMatchQ(u,x)))))
  );
}
