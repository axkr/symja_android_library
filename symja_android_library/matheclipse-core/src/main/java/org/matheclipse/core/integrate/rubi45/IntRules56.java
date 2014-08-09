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
public class IntRules56 { 
  public static IAST RULES = List( 
ISetDelayed(Int(Sqr(Plus(Times($($s("§tan"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_)),x_Symbol),
    Condition(Plus(Times(Plus(Sqr(a),Times(CN1,Sqr(b))),x),Times(Sqr(b),Tan(Plus(c,Times(pd,x))),Power(pd,CN1)),Times(C2,a,b,Int(Tan(Plus(c,Times(pd,x))),x))),FreeQ(List(a,b,c,pd),x))),
ISetDelayed(Int(Sqr(Plus(Times($($s("§cot"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_)),x_Symbol),
    Condition(Plus(Times(Plus(Sqr(a),Times(CN1,Sqr(b))),x),Times(CN1,Sqr(b),Cot(Plus(c,Times(pd,x))),Power(pd,CN1)),Times(C2,a,b,Int(Cot(Plus(c,Times(pd,x))),x))),FreeQ(List(a,b,c,pd),x))),
ISetDelayed(Int(Power(Plus(Times($($s("§tan"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_),pn_),x_Symbol),
    Condition(Plus(Times(b,Power(Plus(a,Times(b,Tan(Plus(c,Times(pd,x))))),Plus(pn,Times(CN1,C1))),Power(Times(pd,Plus(pn,Times(CN1,C1))),CN1)),Times(C2,a,Int(Power(Plus(a,Times(b,Tan(Plus(c,Times(pd,x))))),Plus(pn,Times(CN1,C1))),x))),And(And(And(FreeQ(List(a,b,c,pd),x),ZeroQ(Plus(Sqr(a),Sqr(b)))),RationalQ(pn)),Greater(pn,C1)))),
ISetDelayed(Int(Power(Plus(Times($($s("§cot"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_),pn_),x_Symbol),
    Condition(Plus(Times(CN1,b,Power(Plus(a,Times(b,Cot(Plus(c,Times(pd,x))))),Plus(pn,Times(CN1,C1))),Power(Times(pd,Plus(pn,Times(CN1,C1))),CN1)),Times(C2,a,Int(Power(Plus(a,Times(b,Cot(Plus(c,Times(pd,x))))),Plus(pn,Times(CN1,C1))),x))),And(And(And(FreeQ(List(a,b,c,pd),x),ZeroQ(Plus(Sqr(a),Sqr(b)))),RationalQ(pn)),Greater(pn,C1)))),
ISetDelayed(Int(Power(Plus(Times($($s("§tan"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_),pn_),x_Symbol),
    Condition(Plus(Times(a,Power(Plus(a,Times(b,Tan(Plus(c,Times(pd,x))))),pn),Power(Times(C2,b,pd,pn),CN1)),Times(Power(Times(C2,a),CN1),Int(Power(Plus(a,Times(b,Tan(Plus(c,Times(pd,x))))),Plus(pn,C1)),x))),And(And(And(FreeQ(List(a,b,c,pd),x),ZeroQ(Plus(Sqr(a),Sqr(b)))),RationalQ(pn)),Less(pn,C0)))),
ISetDelayed(Int(Power(Plus(Times($($s("§cot"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_),pn_),x_Symbol),
    Condition(Plus(Times(CN1,a,Power(Plus(a,Times(b,Cot(Plus(c,Times(pd,x))))),pn),Power(Times(C2,b,pd,pn),CN1)),Times(Power(Times(C2,a),CN1),Int(Power(Plus(a,Times(b,Cot(Plus(c,Times(pd,x))))),Plus(pn,C1)),x))),And(And(And(FreeQ(List(a,b,c,pd),x),ZeroQ(Plus(Sqr(a),Sqr(b)))),RationalQ(pn)),Less(pn,C0)))),
ISetDelayed(Int(Sqrt(Plus(Times($($s("§tan"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_)),x_Symbol),
    Condition(Times(CN2,b,Power(pd,CN1),Subst(Int(Power(Plus(Times(C2,a),Times(CN1,Sqr(x))),CN1),x),x,Sqrt(Plus(a,Times(b,Tan(Plus(c,Times(pd,x)))))))),And(FreeQ(List(a,b,c,pd),x),ZeroQ(Plus(Sqr(a),Sqr(b)))))),
ISetDelayed(Int(Sqrt(Plus(Times($($s("§cot"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_)),x_Symbol),
    Condition(Times(C2,b,Power(pd,CN1),Subst(Int(Power(Plus(Times(C2,a),Times(CN1,Sqr(x))),CN1),x),x,Sqrt(Plus(a,Times(b,Cot(Plus(c,Times(pd,x)))))))),And(FreeQ(List(a,b,c,pd),x),ZeroQ(Plus(Sqr(a),Sqr(b)))))),
ISetDelayed(Int(Power(Plus(Times($($s("§tan"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_),pn_),x_Symbol),
    Condition(Times(CN1,b,Power(pd,CN1),Subst(Int(Times(Power(Plus(a,x),Plus(pn,Times(CN1,C1))),Power(Plus(a,Times(CN1,x)),CN1)),x),x,Times(b,Tan(Plus(c,Times(pd,x)))))),And(And(And(FreeQ(List(a,b,c,pd,pn),x),ZeroQ(Plus(Sqr(a),Sqr(b)))),RationalQ(pn)),Less(Less(C0,pn),C1)))),
ISetDelayed(Int(Power(Plus(Times($($s("§cot"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_),pn_),x_Symbol),
    Condition(Times(b,Power(pd,CN1),Subst(Int(Times(Power(Plus(a,x),Plus(pn,Times(CN1,C1))),Power(Plus(a,Times(CN1,x)),CN1)),x),x,Times(b,Cot(Plus(c,Times(pd,x)))))),And(And(And(FreeQ(List(a,b,c,pd,pn),x),ZeroQ(Plus(Sqr(a),Sqr(b)))),RationalQ(pn)),Less(Less(C0,pn),C1)))),
ISetDelayed(Int(Power(Plus(Times($($s("§tan"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_),pn_),x_Symbol),
    Condition(Times(CN1,b,Power(Plus(a,Times(b,Tan(Plus(c,Times(pd,x))))),pn),Power(Times(C2,a,pd,pn),CN1),Hypergeometric2F1(C1,pn,Plus(pn,C1),Times(Plus(a,Times(b,Tan(Plus(c,Times(pd,x))))),Power(Times(C2,a),CN1)))),And(And(FreeQ(List(a,b,c,pd,pn),x),ZeroQ(Plus(Sqr(a),Sqr(b)))),Not(IntegerQ(Times(C2,pn)))))),
ISetDelayed(Int(Power(Plus(Times($($s("§cot"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_),pn_),x_Symbol),
    Condition(Times(b,Power(Plus(a,Times(b,Cot(Plus(c,Times(pd,x))))),pn),Power(Times(C2,a,pd,pn),CN1),Hypergeometric2F1(C1,pn,Plus(pn,C1),Times(Plus(a,Times(b,Cot(Plus(c,Times(pd,x))))),Power(Times(C2,a),CN1)))),And(And(FreeQ(List(a,b,c,pd,pn),x),ZeroQ(Plus(Sqr(a),Sqr(b)))),Not(IntegerQ(Times(C2,pn)))))),
ISetDelayed(Int(Power(Plus(Times($($s("§tan"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_),pn_),x_Symbol),
    Condition(Plus(Times(b,Power(Plus(a,Times(b,Tan(Plus(c,Times(pd,x))))),Plus(pn,Times(CN1,C1))),Power(Times(pd,Plus(pn,Times(CN1,C1))),CN1)),Int(Times(Plus(Sqr(a),Times(CN1,Sqr(b)),Times(C2,a,b,Tan(Plus(c,Times(pd,x))))),Power(Plus(a,Times(b,Tan(Plus(c,Times(pd,x))))),Plus(pn,Times(CN1,C2)))),x)),And(And(And(FreeQ(List(a,b,c,pd),x),NonzeroQ(Plus(Sqr(a),Sqr(b)))),RationalQ(pn)),Greater(pn,C1)))),
ISetDelayed(Int(Power(Plus(Times($($s("§cot"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_),pn_),x_Symbol),
    Condition(Plus(Times(CN1,b,Power(Plus(a,Times(b,Cot(Plus(c,Times(pd,x))))),Plus(pn,Times(CN1,C1))),Power(Times(pd,Plus(pn,Times(CN1,C1))),CN1)),Int(Times(Plus(Sqr(a),Times(CN1,Sqr(b)),Times(C2,a,b,Cot(Plus(c,Times(pd,x))))),Power(Plus(a,Times(b,Cot(Plus(c,Times(pd,x))))),Plus(pn,Times(CN1,C2)))),x)),And(And(And(FreeQ(List(a,b,c,pd),x),NonzeroQ(Plus(Sqr(a),Sqr(b)))),RationalQ(pn)),Greater(pn,C1)))),
ISetDelayed(Int(Power(Plus(Times($($s("§tan"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_),CN1),x_Symbol),
    Condition(Plus(Times(a,x,Power(Plus(Sqr(a),Sqr(b)),CN1)),Times(b,Power(Plus(Sqr(a),Sqr(b)),CN1),Int(Times(Plus(b,Times(CN1,a,Tan(Plus(c,Times(pd,x))))),Power(Plus(a,Times(b,Tan(Plus(c,Times(pd,x))))),CN1)),x))),And(FreeQ(List(a,b,c,pd),x),NonzeroQ(Plus(Sqr(a),Sqr(b)))))),
ISetDelayed(Int(Power(Plus(Times($($s("§cot"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_),CN1),x_Symbol),
    Condition(Plus(Times(a,x,Power(Plus(Sqr(a),Sqr(b)),CN1)),Times(b,Power(Plus(Sqr(a),Sqr(b)),CN1),Int(Times(Plus(b,Times(CN1,a,Cot(Plus(c,Times(pd,x))))),Power(Plus(a,Times(b,Cot(Plus(c,Times(pd,x))))),CN1)),x))),And(FreeQ(List(a,b,c,pd),x),NonzeroQ(Plus(Sqr(a),Sqr(b)))))),
ISetDelayed(Int(Power(Plus(Times($($s("§tan"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_),pn_),x_Symbol),
    Condition(Plus(Times(b,Power(Plus(a,Times(b,Tan(Plus(c,Times(pd,x))))),Plus(pn,C1)),Power(Times(pd,Plus(pn,C1),Plus(Sqr(a),Sqr(b))),CN1)),Times(Power(Plus(Sqr(a),Sqr(b)),CN1),Int(Times(Plus(a,Times(CN1,b,Tan(Plus(c,Times(pd,x))))),Power(Plus(a,Times(b,Tan(Plus(c,Times(pd,x))))),Plus(pn,C1))),x))),And(And(And(FreeQ(List(a,b,c,pd),x),NonzeroQ(Plus(Sqr(a),Sqr(b)))),RationalQ(pn)),Less(pn,CN1)))),
ISetDelayed(Int(Power(Plus(Times($($s("§cot"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_),pn_),x_Symbol),
    Condition(Plus(Times(CN1,b,Power(Plus(a,Times(b,Cot(Plus(c,Times(pd,x))))),Plus(pn,C1)),Power(Times(pd,Plus(Sqr(a),Sqr(b)),Plus(pn,C1)),CN1)),Times(Power(Plus(Sqr(a),Sqr(b)),CN1),Int(Times(Plus(a,Times(CN1,b,Cot(Plus(c,Times(pd,x))))),Power(Plus(a,Times(b,Cot(Plus(c,Times(pd,x))))),Plus(pn,C1))),x))),And(And(And(FreeQ(List(a,b,c,pd),x),NonzeroQ(Plus(Sqr(a),Sqr(b)))),RationalQ(pn)),Less(pn,CN1)))),
ISetDelayed(Int(Sqrt(Plus(Times($($s("§tan"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_)),x_Symbol),
    Condition(Plus(Times(C1D2,Plus(a,Times(CN1,b,CI)),Int(Times(Plus(C1,Times(CI,Tan(Plus(c,Times(pd,x))))),Power(Plus(a,Times(b,Tan(Plus(c,Times(pd,x))))),CN1D2)),x)),Times(C1D2,Plus(a,Times(b,CI)),Int(Times(Plus(C1,Times(CN1,CI,Tan(Plus(c,Times(pd,x))))),Power(Plus(a,Times(b,Tan(Plus(c,Times(pd,x))))),CN1D2)),x))),And(FreeQ(List(a,b,c,pd),x),NonzeroQ(Plus(Sqr(a),Sqr(b)))))),
ISetDelayed(Int(Sqrt(Plus(Times($($s("§cot"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_)),x_Symbol),
    Condition(Plus(Times(C1D2,Plus(a,Times(CN1,b,CI)),Int(Times(Plus(C1,Times(CI,Cot(Plus(c,Times(pd,x))))),Power(Plus(a,Times(b,Cot(Plus(c,Times(pd,x))))),CN1D2)),x)),Times(C1D2,Plus(a,Times(b,CI)),Int(Times(Plus(C1,Times(CN1,CI,Cot(Plus(c,Times(pd,x))))),Power(Plus(a,Times(b,Cot(Plus(c,Times(pd,x))))),CN1D2)),x))),And(FreeQ(List(a,b,c,pd),x),NonzeroQ(Plus(Sqr(a),Sqr(b)))))),
ISetDelayed(Int(Power(Plus(Times($($s("§tan"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_),pn_),x_Symbol),
    Condition(Plus(Times(C1D2,Int(Times(Plus(C1,Times(CI,Tan(Plus(c,Times(pd,x))))),Power(Plus(a,Times(b,Tan(Plus(c,Times(pd,x))))),pn)),x)),Times(C1D2,Int(Times(Plus(C1,Times(CN1,CI,Tan(Plus(c,Times(pd,x))))),Power(Plus(a,Times(b,Tan(Plus(c,Times(pd,x))))),pn)),x))),And(And(FreeQ(List(a,b,c,pd,pn),x),NonzeroQ(Plus(Sqr(a),Sqr(b)))),Not(IntegerQ(pn))))),
ISetDelayed(Int(Power(Plus(Times($($s("§cot"),Plus(Times(x_,pd_DEFAULT),c_DEFAULT)),b_DEFAULT),a_),pn_),x_Symbol),
    Condition(Plus(Times(C1D2,Int(Times(Plus(C1,Times(CI,Cot(Plus(c,Times(pd,x))))),Power(Plus(a,Times(b,Cot(Plus(c,Times(pd,x))))),pn)),x)),Times(C1D2,Int(Times(Plus(C1,Times(CN1,CI,Cot(Plus(c,Times(pd,x))))),Power(Plus(a,Times(b,Cot(Plus(c,Times(pd,x))))),pn)),x))),And(And(FreeQ(List(a,b,c,pd,pn),x),NonzeroQ(Plus(Sqr(a),Sqr(b)))),Not(IntegerQ(pn)))))
  );
}
