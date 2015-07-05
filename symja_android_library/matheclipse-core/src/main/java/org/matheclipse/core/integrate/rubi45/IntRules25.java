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
public class IntRules25 { 
  public static IAST RULES = List( 
ISetDelayed(Int(Power(Plus(Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(a_DEFAULT,Power(x_,q_DEFAULT)),Times(c_DEFAULT,Power(x_,r_DEFAULT))),p_),x_Symbol),
    Condition(Int(Power(Times(Plus(a,b,c),Power(x,n)),p),x),And(And(FreeQ(List(a,b,c,n,p),x),ZeroQ(Plus(n,Negate(q)))),ZeroQ(Plus(r,Negate(n)))))),
ISetDelayed(Int(Power(Plus(Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(a_DEFAULT,Power(x_,q_DEFAULT)),Times(c_DEFAULT,Power(x_,r_DEFAULT))),p_),x_Symbol),
    Condition(Int(Times(Power(x,Times(p,q)),Power(Plus(a,Times(b,Power(x,Plus(n,Negate(q)))),Times(c,Power(x,Times(C2,Plus(n,Negate(q)))))),p)),x),And(And(And(FreeQ(List(a,b,c,n,q),x),ZeroQ(Plus(r,Negate(Plus(Times(C2,n),Negate(q)))))),PosQ(Plus(n,Negate(q)))),IntegerQ(p)))),
ISetDelayed(Int(Sqrt(Plus(Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(a_DEFAULT,Power(x_,q_DEFAULT)),Times(c_DEFAULT,Power(x_,r_DEFAULT)))),x_Symbol),
    Condition(Times(Sqrt(Plus(Times(a,Power(x,q)),Times(b,Power(x,n)),Times(c,Power(x,Plus(Times(C2,n),Negate(q)))))),Power(Times(Power(x,Times(C1D2,q)),Sqrt(Plus(a,Times(b,Power(x,Plus(n,Negate(q)))),Times(c,Power(x,Times(C2,Plus(n,Negate(q)))))))),-1),Int(Times(Power(x,Times(C1D2,q)),Sqrt(Plus(a,Times(b,Power(x,Plus(n,Negate(q)))),Times(c,Power(x,Times(C2,Plus(n,Negate(q)))))))),x)),And(And(FreeQ(List(a,b,c,n,q),x),ZeroQ(Plus(r,Negate(Plus(Times(C2,n),Negate(q)))))),PosQ(Plus(n,Negate(q)))))),
ISetDelayed(Int(Power(Plus(Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(a_DEFAULT,Power(x_,q_DEFAULT)),Times(c_DEFAULT,Power(x_,r_DEFAULT))),CN1D2),x_Symbol),
    Condition(Times(Power(x,Times(C1D2,q)),Sqrt(Plus(a,Times(b,Power(x,Plus(n,Negate(q)))),Times(c,Power(x,Times(C2,Plus(n,Negate(q))))))),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n)),Times(c,Power(x,Plus(Times(C2,n),Negate(q))))),CN1D2),Int(Power(Times(Power(x,Times(C1D2,q)),Sqrt(Plus(a,Times(b,Power(x,Plus(n,Negate(q)))),Times(c,Power(x,Times(C2,Plus(n,Negate(q)))))))),-1),x)),And(And(FreeQ(List(a,b,c,n,q),x),ZeroQ(Plus(r,Negate(Plus(Times(C2,n),Negate(q)))))),PosQ(Plus(n,Negate(q)))))),
ISetDelayed(Int(Power(Plus(Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(a_DEFAULT,Power(x_,q_DEFAULT)),Times(c_DEFAULT,Power(x_,r_DEFAULT))),p_),x_Symbol),
    Condition(Plus(Times(x,Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n)),Times(c,Power(x,Plus(Times(C2,n),Negate(q))))),p),Power(Plus(Times(p,Plus(Times(C2,n),Negate(q))),C1),-1)),Times(Plus(n,Negate(q)),p,Power(Plus(Times(p,Plus(Times(C2,n),Negate(q))),C1),-1),Int(Times(Power(x,q),Plus(Times(C2,a),Times(b,Power(x,Plus(n,Negate(q))))),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n)),Times(c,Power(x,Plus(Times(C2,n),Negate(q))))),Plus(p,Negate(C1)))),x))),And(And(And(And(And(And(And(FreeQ(List(a,b,c,n,q),x),ZeroQ(Plus(r,Negate(Plus(Times(C2,n),Negate(q)))))),PosQ(Plus(n,Negate(q)))),Not(IntegerQ(p))),NonzeroQ(Plus(Sqr(b),Times(CN1,C4,a,c)))),RationalQ(p)),Greater(p,C0)),NonzeroQ(Plus(Times(p,Plus(Times(C2,n),Negate(q))),C1))))),
ISetDelayed(Int(Power(Plus(Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(a_DEFAULT,Power(x_,q_DEFAULT)),Times(c_DEFAULT,Power(x_,r_DEFAULT))),p_),x_Symbol),
    Condition(Plus(Times(CN1,Power(x,Plus(Negate(q),C1)),Plus(Sqr(b),Times(CN1,C2,a,c),Times(b,c,Power(x,Plus(n,Negate(q))))),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n)),Times(c,Power(x,Plus(Times(C2,n),Negate(q))))),Plus(p,C1)),Power(Times(a,Plus(n,Negate(q)),Plus(p,C1),Plus(Sqr(b),Times(CN1,C4,a,c))),-1)),Times(Power(Times(a,Plus(n,Negate(q)),Plus(p,C1),Plus(Sqr(b),Times(CN1,C4,a,c))),-1),Int(Times(Power(x,Negate(q)),Plus(Times(Plus(Times(p,q),C1),Plus(Sqr(b),Times(CN1,C2,a,c))),Times(Plus(n,Negate(q)),Plus(p,C1),Plus(Sqr(b),Times(CN1,C4,a,c))),Times(b,c,Plus(Times(p,q),Times(Plus(n,Negate(q)),Plus(Times(C2,p),C3)),C1),Power(x,Plus(n,Negate(q))))),Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n)),Times(c,Power(x,Plus(Times(C2,n),Negate(q))))),Plus(p,C1))),x))),And(And(And(And(And(And(FreeQ(List(a,b,c,n,q),x),ZeroQ(Plus(r,Negate(Plus(Times(C2,n),Negate(q)))))),PosQ(Plus(n,Negate(q)))),Not(IntegerQ(p))),NonzeroQ(Plus(Sqr(b),Times(CN1,C4,a,c)))),RationalQ(p)),Less(p,CN1)))),
ISetDelayed(Int(Power(Plus(Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(a_DEFAULT,Power(x_,q_DEFAULT)),Times(c_DEFAULT,Power(x_,r_DEFAULT))),p_),x_Symbol),
    Condition(Times(Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n)),Times(c,Power(x,Plus(Times(C2,n),Negate(q))))),p),Power(Times(Power(x,Times(p,q)),Power(Plus(a,Times(b,Power(x,Plus(n,Negate(q)))),Times(c,Power(x,Times(C2,Plus(n,Negate(q)))))),p)),-1),Int(Times(Power(x,Times(p,q)),Power(Plus(a,Times(b,Power(x,Plus(n,Negate(q)))),Times(c,Power(x,Times(C2,Plus(n,Negate(q)))))),p)),x)),And(And(And(FreeQ(List(a,b,c,n,p,q),x),ZeroQ(Plus(r,Negate(Plus(Times(C2,n),Negate(q)))))),PosQ(Plus(n,Negate(q)))),Not(IntegerQ(p))))),
ISetDelayed(Int(Power(Plus(Times(a_DEFAULT,Power(u_,q_DEFAULT)),Times(b_DEFAULT,Power(v_,n_DEFAULT)),Times(c_DEFAULT,Power(w_,r_DEFAULT))),p_),x_Symbol),
    Condition(Times(Power(Coefficient(u,x,C1),-1),Subst(Int(Power(Plus(Times(a,Power(x,q)),Times(b,Power(x,n)),Times(c,Power(x,Plus(Times(C2,n),Negate(q))))),p),x),x,u)),And(And(And(And(And(FreeQ(List(a,b,c,n,p,q),x),ZeroQ(Plus(r,Negate(Plus(Times(C2,n),Negate(q)))))),ZeroQ(Plus(u,Negate(v)))),ZeroQ(Plus(u,Negate(w)))),LinearQ(u,x)),NonzeroQ(Plus(u,Negate(x)))))),
ISetDelayed(Int(Power(u_,p_),x_Symbol),
    Condition(Int(Power(ExpandToSum(u,x),p),x),And(And(FreeQ(p,x),GeneralizedTrinomialQ(u,x)),Not(GeneralizedTrinomialMatchQ(u,x)))))
  );
}
