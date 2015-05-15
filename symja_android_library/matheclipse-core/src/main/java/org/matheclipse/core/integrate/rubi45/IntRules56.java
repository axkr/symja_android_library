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
public class IntRules56 { 
  public static IAST RULES = List( 
ISetDelayed(Int(Sqr(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),x_Symbol),
    Condition(Plus(Times(Plus(Sqr(a),Negate(Sqr(b))),x),Times(Sqr(b),Tan(Plus(c,Times(d,x))),Power(d,-1)),Times(C2,a,b,Int(Tan(Plus(c,Times(d,x))),x))),FreeQ(List(a,b,c,d),x))),
ISetDelayed(Int(Sqr(Plus(a_,Times(b_DEFAULT,$($s("§cot"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),x_Symbol),
    Condition(Plus(Times(Plus(Sqr(a),Negate(Sqr(b))),x),Times(CN1,Sqr(b),Cot(Plus(c,Times(d,x))),Power(d,-1)),Times(C2,a,b,Int(Cot(Plus(c,Times(d,x))),x))),FreeQ(List(a,b,c,d),x))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Plus(Times(b,Power(Plus(a,Times(b,Tan(Plus(c,Times(d,x))))),Plus(n,Negate(C1))),Power(Times(d,Plus(n,Negate(C1))),-1)),Times(C2,a,Int(Power(Plus(a,Times(b,Tan(Plus(c,Times(d,x))))),Plus(n,Negate(C1))),x))),And(And(And(FreeQ(List(a,b,c,d),x),ZeroQ(Plus(Sqr(a),Sqr(b)))),RationalQ(n)),Greater(n,C1)))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§cot"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Plus(Times(CN1,b,Power(Plus(a,Times(b,Cot(Plus(c,Times(d,x))))),Plus(n,Negate(C1))),Power(Times(d,Plus(n,Negate(C1))),-1)),Times(C2,a,Int(Power(Plus(a,Times(b,Cot(Plus(c,Times(d,x))))),Plus(n,Negate(C1))),x))),And(And(And(FreeQ(List(a,b,c,d),x),ZeroQ(Plus(Sqr(a),Sqr(b)))),RationalQ(n)),Greater(n,C1)))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Plus(Times(a,Power(Plus(a,Times(b,Tan(Plus(c,Times(d,x))))),n),Power(Times(C2,b,d,n),-1)),Times(Power(Times(C2,a),-1),Int(Power(Plus(a,Times(b,Tan(Plus(c,Times(d,x))))),Plus(n,C1)),x))),And(And(And(FreeQ(List(a,b,c,d),x),ZeroQ(Plus(Sqr(a),Sqr(b)))),RationalQ(n)),Less(n,C0)))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§cot"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Plus(Times(CN1,a,Power(Plus(a,Times(b,Cot(Plus(c,Times(d,x))))),n),Power(Times(C2,b,d,n),-1)),Times(Power(Times(C2,a),-1),Int(Power(Plus(a,Times(b,Cot(Plus(c,Times(d,x))))),Plus(n,C1)),x))),And(And(And(FreeQ(List(a,b,c,d),x),ZeroQ(Plus(Sqr(a),Sqr(b)))),RationalQ(n)),Less(n,C0)))),
ISetDelayed(Int(Sqrt(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),x_Symbol),
    Condition(Times(CN2,b,Power(d,-1),Subst(Int(Power(Plus(Times(C2,a),Negate(Sqr(x))),-1),x),x,Sqrt(Plus(a,Times(b,Tan(Plus(c,Times(d,x)))))))),And(FreeQ(List(a,b,c,d),x),ZeroQ(Plus(Sqr(a),Sqr(b)))))),
ISetDelayed(Int(Sqrt(Plus(a_,Times(b_DEFAULT,$($s("§cot"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),x_Symbol),
    Condition(Times(C2,b,Power(d,-1),Subst(Int(Power(Plus(Times(C2,a),Negate(Sqr(x))),-1),x),x,Sqrt(Plus(a,Times(b,Cot(Plus(c,Times(d,x)))))))),And(FreeQ(List(a,b,c,d),x),ZeroQ(Plus(Sqr(a),Sqr(b)))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Times(CN1,b,Power(d,-1),Subst(Int(Times(Power(Plus(a,x),Plus(n,Negate(C1))),Power(Plus(a,Negate(x)),-1)),x),x,Times(b,Tan(Plus(c,Times(d,x)))))),And(And(And(FreeQ(List(a,b,c,d,n),x),ZeroQ(Plus(Sqr(a),Sqr(b)))),RationalQ(n)),Less(Less(C0,n),C1)))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§cot"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Times(b,Power(d,-1),Subst(Int(Times(Power(Plus(a,x),Plus(n,Negate(C1))),Power(Plus(a,Negate(x)),-1)),x),x,Times(b,Cot(Plus(c,Times(d,x)))))),And(And(And(FreeQ(List(a,b,c,d,n),x),ZeroQ(Plus(Sqr(a),Sqr(b)))),RationalQ(n)),Less(Less(C0,n),C1)))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Times(CN1,b,Power(Plus(a,Times(b,Tan(Plus(c,Times(d,x))))),n),Power(Times(C2,a,d,n),-1),Hypergeometric2F1(C1,n,Plus(n,C1),Times(Plus(a,Times(b,Tan(Plus(c,Times(d,x))))),Power(Times(C2,a),-1)))),And(And(FreeQ(List(a,b,c,d,n),x),ZeroQ(Plus(Sqr(a),Sqr(b)))),Not(IntegerQ(Times(C2,n)))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§cot"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Times(b,Power(Plus(a,Times(b,Cot(Plus(c,Times(d,x))))),n),Power(Times(C2,a,d,n),-1),Hypergeometric2F1(C1,n,Plus(n,C1),Times(Plus(a,Times(b,Cot(Plus(c,Times(d,x))))),Power(Times(C2,a),-1)))),And(And(FreeQ(List(a,b,c,d,n),x),ZeroQ(Plus(Sqr(a),Sqr(b)))),Not(IntegerQ(Times(C2,n)))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Plus(Times(b,Power(Plus(a,Times(b,Tan(Plus(c,Times(d,x))))),Plus(n,Negate(C1))),Power(Times(d,Plus(n,Negate(C1))),-1)),Int(Times(Plus(Sqr(a),Negate(Sqr(b)),Times(C2,a,b,Tan(Plus(c,Times(d,x))))),Power(Plus(a,Times(b,Tan(Plus(c,Times(d,x))))),Plus(n,Negate(C2)))),x)),And(And(And(FreeQ(List(a,b,c,d),x),NonzeroQ(Plus(Sqr(a),Sqr(b)))),RationalQ(n)),Greater(n,C1)))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§cot"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Plus(Times(CN1,b,Power(Plus(a,Times(b,Cot(Plus(c,Times(d,x))))),Plus(n,Negate(C1))),Power(Times(d,Plus(n,Negate(C1))),-1)),Int(Times(Plus(Sqr(a),Negate(Sqr(b)),Times(C2,a,b,Cot(Plus(c,Times(d,x))))),Power(Plus(a,Times(b,Cot(Plus(c,Times(d,x))))),Plus(n,Negate(C2)))),x)),And(And(And(FreeQ(List(a,b,c,d),x),NonzeroQ(Plus(Sqr(a),Sqr(b)))),RationalQ(n)),Greater(n,C1)))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),-1),x_Symbol),
    Condition(Plus(Times(a,x,Power(Plus(Sqr(a),Sqr(b)),-1)),Times(b,Power(Plus(Sqr(a),Sqr(b)),-1),Int(Times(Plus(b,Times(CN1,a,Tan(Plus(c,Times(d,x))))),Power(Plus(a,Times(b,Tan(Plus(c,Times(d,x))))),-1)),x))),And(FreeQ(List(a,b,c,d),x),NonzeroQ(Plus(Sqr(a),Sqr(b)))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§cot"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),-1),x_Symbol),
    Condition(Plus(Times(a,x,Power(Plus(Sqr(a),Sqr(b)),-1)),Times(b,Power(Plus(Sqr(a),Sqr(b)),-1),Int(Times(Plus(b,Times(CN1,a,Cot(Plus(c,Times(d,x))))),Power(Plus(a,Times(b,Cot(Plus(c,Times(d,x))))),-1)),x))),And(FreeQ(List(a,b,c,d),x),NonzeroQ(Plus(Sqr(a),Sqr(b)))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Plus(Times(b,Power(Plus(a,Times(b,Tan(Plus(c,Times(d,x))))),Plus(n,C1)),Power(Times(d,Plus(n,C1),Plus(Sqr(a),Sqr(b))),-1)),Times(Power(Plus(Sqr(a),Sqr(b)),-1),Int(Times(Plus(a,Times(CN1,b,Tan(Plus(c,Times(d,x))))),Power(Plus(a,Times(b,Tan(Plus(c,Times(d,x))))),Plus(n,C1))),x))),And(And(And(FreeQ(List(a,b,c,d),x),NonzeroQ(Plus(Sqr(a),Sqr(b)))),RationalQ(n)),Less(n,CN1)))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§cot"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Plus(Times(CN1,b,Power(Plus(a,Times(b,Cot(Plus(c,Times(d,x))))),Plus(n,C1)),Power(Times(d,Plus(Sqr(a),Sqr(b)),Plus(n,C1)),-1)),Times(Power(Plus(Sqr(a),Sqr(b)),-1),Int(Times(Plus(a,Times(CN1,b,Cot(Plus(c,Times(d,x))))),Power(Plus(a,Times(b,Cot(Plus(c,Times(d,x))))),Plus(n,C1))),x))),And(And(And(FreeQ(List(a,b,c,d),x),NonzeroQ(Plus(Sqr(a),Sqr(b)))),RationalQ(n)),Less(n,CN1)))),
ISetDelayed(Int(Sqrt(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),x_Symbol),
    Condition(Plus(Times(C1D2,Plus(a,Times(CN1,b,CI)),Int(Times(Plus(C1,Times(CI,Tan(Plus(c,Times(d,x))))),Power(Plus(a,Times(b,Tan(Plus(c,Times(d,x))))),CN1D2)),x)),Times(C1D2,Plus(a,Times(b,CI)),Int(Times(Plus(C1,Times(CN1,CI,Tan(Plus(c,Times(d,x))))),Power(Plus(a,Times(b,Tan(Plus(c,Times(d,x))))),CN1D2)),x))),And(FreeQ(List(a,b,c,d),x),NonzeroQ(Plus(Sqr(a),Sqr(b)))))),
ISetDelayed(Int(Sqrt(Plus(a_,Times(b_DEFAULT,$($s("§cot"),Plus(c_DEFAULT,Times(d_DEFAULT,x_)))))),x_Symbol),
    Condition(Plus(Times(C1D2,Plus(a,Times(CN1,b,CI)),Int(Times(Plus(C1,Times(CI,Cot(Plus(c,Times(d,x))))),Power(Plus(a,Times(b,Cot(Plus(c,Times(d,x))))),CN1D2)),x)),Times(C1D2,Plus(a,Times(b,CI)),Int(Times(Plus(C1,Times(CN1,CI,Cot(Plus(c,Times(d,x))))),Power(Plus(a,Times(b,Cot(Plus(c,Times(d,x))))),CN1D2)),x))),And(FreeQ(List(a,b,c,d),x),NonzeroQ(Plus(Sqr(a),Sqr(b)))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§tan"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Plus(Times(C1D2,Int(Times(Plus(C1,Times(CI,Tan(Plus(c,Times(d,x))))),Power(Plus(a,Times(b,Tan(Plus(c,Times(d,x))))),n)),x)),Times(C1D2,Int(Times(Plus(C1,Times(CN1,CI,Tan(Plus(c,Times(d,x))))),Power(Plus(a,Times(b,Tan(Plus(c,Times(d,x))))),n)),x))),And(And(FreeQ(List(a,b,c,d,n),x),NonzeroQ(Plus(Sqr(a),Sqr(b)))),Not(IntegerQ(n))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,$($s("§cot"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))))),n_),x_Symbol),
    Condition(Plus(Times(C1D2,Int(Times(Plus(C1,Times(CI,Cot(Plus(c,Times(d,x))))),Power(Plus(a,Times(b,Cot(Plus(c,Times(d,x))))),n)),x)),Times(C1D2,Int(Times(Plus(C1,Times(CN1,CI,Cot(Plus(c,Times(d,x))))),Power(Plus(a,Times(b,Cot(Plus(c,Times(d,x))))),n)),x))),And(And(FreeQ(List(a,b,c,d,n),x),NonzeroQ(Plus(Sqr(a),Sqr(b)))),Not(IntegerQ(n)))))
  );
}
