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
public class IntRules139 { 
  public static IAST RULES = List( 
ISetDelayed(Int(Times(u_DEFAULT,Power(Plus(a_,Times(b_DEFAULT,Power(x_,pn_DEFAULT))),p_DEFAULT)),x_Symbol),
    Condition(Int(Times(u,Power(Times(b,Power(x,pn)),p)),x),And(FreeQ(List(a,b,pn,p),x),ZeroQ(a)))),
ISetDelayed(Int(Times(u_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,pn_DEFAULT))),p_DEFAULT)),x_Symbol),
    Condition(Int(Times(u,Power(a,p)),x),And(FreeQ(List(a,b,pn,p),x),ZeroQ(b)))),
ISetDelayed(Int(Times(u_DEFAULT,Power(Plus(a_,Times(c_DEFAULT,Power(x_,j_DEFAULT)),Times(b_DEFAULT,Power(x_,pn_DEFAULT))),p_DEFAULT)),x_Symbol),
    Condition(Int(Times(u,Power(Plus(Times(b,Power(x,pn)),Times(c,Power(x,Times(C2,pn)))),p)),x),And(And(FreeQ(List(a,b,c,pn,p),x),ZeroQ(Plus(j,Times(CN1,C2,pn)))),ZeroQ(a)))),
ISetDelayed(Int(Times(u_DEFAULT,Power(Plus(a_DEFAULT,Times(c_DEFAULT,Power(x_,j_DEFAULT)),Times(b_DEFAULT,Power(x_,pn_DEFAULT))),p_DEFAULT)),x_Symbol),
    Condition(Int(Times(u,Power(Plus(a,Times(c,Power(x,Times(C2,pn)))),p)),x),And(And(FreeQ(List(a,b,c,pn,p),x),ZeroQ(Plus(j,Times(CN1,C2,pn)))),ZeroQ(b)))),
ISetDelayed(Int(Times(u_DEFAULT,Power(Plus(a_DEFAULT,Times(c_DEFAULT,Power(x_,j_DEFAULT)),Times(b_DEFAULT,Power(x_,pn_DEFAULT))),p_DEFAULT)),x_Symbol),
    Condition(Int(Times(u,Power(Plus(a,Times(b,Power(x,pn))),p)),x),And(And(FreeQ(List(a,b,c,pn,p),x),ZeroQ(Plus(j,Times(CN1,C2,pn)))),ZeroQ(c)))),
ISetDelayed(Int(Times(u_DEFAULT,Power(x_,m_)),x_Symbol),
    Condition(Int(Times(u,Power(x,CN1)),x),And(ZeroQ(Plus(m,C1)),UnsameQ(m,CN1)))),
ISetDelayed(Int(a_,x_Symbol),
    Condition(Times(a,x),FreeQ(a,x))),
ISetDelayed(Int(Times(a_,Plus(b_,Times(c_DEFAULT,x_))),x_Symbol),
    Condition(Times(a,Sqr(Plus(b,Times(c,x))),Power(Times(C2,c),CN1)),FreeQ(List(a,b,c),x))),
ISetDelayed(Int(Times(CN1,u_),x_Symbol),
    Times(Identity(CN1),Int(u,x))),
ISetDelayed(Int(Times(u_,Complex(C0,a_)),x_Symbol),
    Condition(Times(Complex(Identity(C0),a),Int(u,x)),And(FreeQ(a,x),OneQ(Sqr(a))))),
ISetDelayed(Int(Times(a_,u_),x_Symbol),
    Condition(Times(a,Int(u,x)),And(FreeQ(a,x),Not(MatchQ(u,Condition(Times(b_,v_),FreeQ(b,x))))))),
ISetDelayed(Int(u_,x_Symbol),
    Condition(IntSum(u,x),SumQ(u))),
ISetDelayed(Int(Times(u_,Power(Times(c_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Int(ExpandIntegrand(Times(Power(Times(c,x),m),u),x),x),And(And(FreeQ(List(c,m),x),SumQ(u)),Not(MatchQ(u,Condition(Plus(a_,Times(b_DEFAULT,v_)),And(FreeQ(List(a,b),x),InverseFunctionQ(v)))))))),
ISetDelayed(Int(Times(u_DEFAULT,Power(Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),pn_)),p_)),x_Symbol),
    Condition(Times(Power(c,Plus(p,Times(CN1,C1D2))),Sqrt(Times(c,Power(Plus(a,Times(b,x)),pn))),Power(Power(Plus(a,Times(b,x)),Times(C1D2,pn)),CN1),Int(Times(u,Power(Plus(a,Times(b,x)),Times(pn,p))),x)),And(FreeQ(List(a,b,c,pn,p),x),PositiveIntegerQ(Plus(p,C1D2))))),
ISetDelayed(Int(Times(u_DEFAULT,Power(Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),pn_)),p_)),x_Symbol),
    Condition(Times(Power(c,Plus(p,C1D2)),Power(Plus(a,Times(b,x)),Times(C1D2,pn)),Power(Times(c,Power(Plus(a,Times(b,x)),pn)),CN1D2),Int(Times(u,Power(Plus(a,Times(b,x)),Times(pn,p))),x)),And(FreeQ(List(a,b,c,pn,p),x),NegativeIntegerQ(Plus(p,Times(CN1,C1D2)))))),
ISetDelayed(Int(Times(u_DEFAULT,Power(Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),pn_)),p_)),x_Symbol),
    Condition(Times(Power(Times(c,Power(Plus(a,Times(b,x)),pn)),p),Power(Power(Plus(a,Times(b,x)),Times(pn,p)),CN1),Int(Times(u,Power(Plus(a,Times(b,x)),Times(pn,p))),x)),And(FreeQ(List(a,b,c,pn,p),x),Not(IntegerQ(Times(C2,p)))))),
ISetDelayed(Int(Times(u_DEFAULT,Power(Times(c_DEFAULT,Power(Times(pd_,Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_)),q_)),x_Symbol),
    Condition(Times(Power(Times(c,Power(Times(pd,Plus(a,Times(b,x))),p)),q),Power(Power(Plus(a,Times(b,x)),Times(p,q)),CN1),Int(Times(u,Power(Plus(a,Times(b,x)),Times(p,q))),x)),And(And(FreeQ(List(a,b,c,pd,p,q),x),Not(IntegerQ(p))),Not(IntegerQ(q))))),
ISetDelayed(Int(Times(u_DEFAULT,Power(Times(c_DEFAULT,Power(Times(pd_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),pn_)),p_)),q_)),x_Symbol),
    Condition(Times(Power(Times(c,Power(Times(pd,Power(Plus(a,Times(b,x)),pn)),p)),q),Power(Power(Plus(a,Times(b,x)),Times(pn,p,q)),CN1),Int(Times(u,Power(Plus(a,Times(b,x)),Times(pn,p,q))),x)),And(And(FreeQ(List(a,b,c,pd,pn,p,q),x),Not(IntegerQ(p))),Not(IntegerQ(q))))),
ISetDelayed(Int(Times(u_DEFAULT,Power(v_,m_DEFAULT),Power(Times(b_,v_),pn_)),x_Symbol),
    Condition(Times(Power(Power(b,m),CN1),Int(Times(u,Power(Times(b,v),Plus(m,pn))),x)),And(FreeQ(List(b,pn),x),IntegerQ(m)))),
ISetDelayed(Int(Times(u_DEFAULT,Power(Times(a_DEFAULT,v_),m_),Power(Times(b_DEFAULT,v_),pn_)),x_Symbol),
    Condition(Times(Power(a,Plus(m,C1D2)),Power(b,Plus(pn,Times(CN1,C1D2))),Sqrt(Times(b,v)),Power(Times(a,v),CN1D2),Int(Times(u,Power(v,Plus(m,pn))),x)),And(And(And(FreeQ(List(a,b,m),x),Not(IntegerQ(m))),PositiveIntegerQ(Plus(pn,C1D2))),IntegerQ(Plus(m,pn))))),
ISetDelayed(Int(Times(u_DEFAULT,Power(Times(a_DEFAULT,v_),m_),Power(Times(b_DEFAULT,v_),pn_)),x_Symbol),
    Condition(Times(Power(a,Plus(m,Times(CN1,C1D2))),Power(b,Plus(pn,C1D2)),Sqrt(Times(a,v)),Power(Times(b,v),CN1D2),Int(Times(u,Power(v,Plus(m,pn))),x)),And(And(And(FreeQ(List(a,b,m),x),Not(IntegerQ(m))),NegativeIntegerQ(Plus(pn,Times(CN1,C1D2)))),IntegerQ(Plus(m,pn))))),
ISetDelayed(Int(Times(u_DEFAULT,Power(Times(a_DEFAULT,v_),m_),Power(Times(b_DEFAULT,v_),pn_)),x_Symbol),
    Condition(Times(Power(a,Plus(m,pn)),Power(Times(b,v),pn),Power(Power(Times(a,v),pn),CN1),Int(Times(u,Power(v,Plus(m,pn))),x)),And(And(And(FreeQ(List(a,b,m,pn),x),Not(IntegerQ(m))),Not(IntegerQ(pn))),IntegerQ(Plus(m,pn))))),
ISetDelayed(Int(Times(u_DEFAULT,Power(Times(a_DEFAULT,v_),m_),Power(Times(b_DEFAULT,v_),pn_)),x_Symbol),
    Condition(Times(Power(Times(b,v),pn),Power(Power(Times(a,v),pn),CN1),Int(Times(u,Power(Times(a,v),Plus(m,pn))),x)),And(And(And(FreeQ(List(a,b,m,pn),x),Not(IntegerQ(m))),Not(IntegerQ(pn))),Not(IntegerQ(Plus(m,pn)))))),
ISetDelayed(Int(Times(u_DEFAULT,Power(Plus(a_,Times(b_DEFAULT,v_)),m_DEFAULT),Power(Plus(c_,Times(v_,pd_DEFAULT)),pn_DEFAULT)),x_Symbol),
    Condition(Times(Power(Times(b,Power(pd,CN1)),m),Int(Times(u,Power(Plus(c,Times(pd,v)),Plus(m,pn))),x)),And(And(And(FreeQ(List(a,b,c,pd,m,pn),x),ZeroQ(Plus(Times(b,c),Times(CN1,a,pd)))),Or(IntegerQ(m),PositiveQ(Times(b,Power(pd,CN1))))),Or(Not(IntegerQ(pn)),LessEqual(LeafCount(Plus(c,Times(pd,x))),LeafCount(Plus(a,Times(b,x)))))))),
ISetDelayed(Int(Times(u_DEFAULT,Power(Plus(a_,Times(b_DEFAULT,v_)),m_),Power(Plus(c_,Times(v_,pd_DEFAULT)),pn_)),x_Symbol),
    Condition(Times(Power(Plus(a,Times(b,v)),m),Power(Power(Plus(c,Times(pd,v)),m),CN1),Int(Times(u,Power(Plus(c,Times(pd,v)),Plus(m,pn))),x)),And(And(FreeQ(List(a,b,c,pd,m,pn),x),ZeroQ(Plus(Times(b,c),Times(CN1,a,pd)))),Not(Or(Or(IntegerQ(m),IntegerQ(pn)),PositiveQ(Times(b,Power(pd,CN1)))))))),
ISetDelayed(Int(Times(u_DEFAULT,Power(Plus(a_,Times(b_DEFAULT,v_)),m_DEFAULT),Power(Plus(c_,Times(v_,pd_DEFAULT)),pn_DEFAULT)),x_Symbol),
    Condition(Times(Power(Times(b,Power(pd,CN1)),m),Int(Times(u,Power(Plus(c,Times(pd,v)),Plus(m,pn))),x)),And(And(And(FreeQ(List(a,b,c,pd,m,pn),x),ZeroQ(Plus(Times(b,c),Times(CN1,a,pd)))),Or(IntegerQ(m),PositiveQ(Times(b,Power(pd,CN1))))),Or(Not(IntegerQ(pn)),LessEqual(LeafCount(Plus(c,Times(pd,x))),LeafCount(Plus(a,Times(b,x)))))))),
ISetDelayed(Int(Times(u_DEFAULT,Power(Plus(a_,Times(b_DEFAULT,v_)),m_),Power(Plus(c_,Times(v_,pd_DEFAULT)),pn_)),x_Symbol),
    Condition(Times(Power(Plus(a,Times(b,v)),m),Power(Power(Plus(c,Times(pd,v)),m),CN1),Int(Times(u,Power(Plus(c,Times(pd,v)),Plus(m,pn))),x)),And(And(FreeQ(List(a,b,c,pd,m,pn),x),ZeroQ(Plus(Times(b,c),Times(CN1,a,pd)))),Not(Or(Or(IntegerQ(m),IntegerQ(pn)),PositiveQ(Times(b,Power(pd,CN1)))))))),
ISetDelayed(Int(Times(u_DEFAULT,Plus(Times(b_DEFAULT,v_),Times(c_DEFAULT,Sqr(v_))),Power(Times(a_DEFAULT,v_),m_)),x_Symbol),
    Condition(Times(Power(a,CN1),Int(Times(u,Power(Times(a,v),Plus(m,C1)),Simp(Plus(b,Times(c,v)),x)),x)),And(And(FreeQ(List(a,b,c),x),RationalQ(m)),LessEqual(m,CN1)))),
ISetDelayed(Int(Times(u_DEFAULT,Plus(pa_DEFAULT,Times(v_,pb_DEFAULT),Times(pc_DEFAULT,Sqr(v_))),Power(Plus(a_,Times(b_DEFAULT,v_)),m_)),x_Symbol),
    Condition(Times(Power(b,CN2),Int(Times(u,Power(Plus(a,Times(b,v)),Plus(m,C1)),Simp(Plus(Times(b,pb),Times(CN1,a,pc),Times(b,pc,v)),x)),x)),And(And(And(FreeQ(List(a,b,pa,pb,pc),x),ZeroQ(Plus(Times(pa,Sqr(b)),Times(CN1,a,b,pb),Times(Sqr(a),pc)))),RationalQ(m)),LessEqual(m,CN1)))),
ISetDelayed(Int(Times(u_DEFAULT,Power(Plus(a_,Times(b_DEFAULT,Power(x_,pn_DEFAULT))),m_DEFAULT),Power(Plus(c_,Times(pd_DEFAULT,Power(x_,q_DEFAULT))),p_DEFAULT)),x_Symbol),
    Condition(Times(Power(Times(pd,Power(a,CN1)),p),Int(Times(u,Power(Plus(a,Times(b,Power(x,pn))),Plus(m,p)),Power(Power(x,Times(pn,p)),CN1)),x)),And(And(And(And(FreeQ(List(a,b,c,pd,m,pn),x),ZeroQ(Plus(pn,q))),IntegerQ(p)),ZeroQ(Plus(Times(a,c),Times(CN1,b,pd)))),Not(And(IntegerQ(m),NegQ(pn)))))),
ISetDelayed(Int(Times(u_DEFAULT,Power(Plus(a_,Times(b_DEFAULT,Power(x_,pn_DEFAULT))),m_DEFAULT),Power(Plus(c_,Times(pd_DEFAULT,Power(x_,j_))),p_DEFAULT)),x_Symbol),
    Condition(Times(Power(Times(CN1,Sqr(b),Power(pd,CN1)),m),Int(Times(u,Power(Plus(a,Times(CN1,b,Power(x,pn))),Times(CN1,m))),x)),And(And(And(And(And(FreeQ(List(a,b,c,pd,m,pn,p),x),ZeroQ(Plus(j,Times(CN1,C2,pn)))),ZeroQ(Plus(m,p))),ZeroQ(Plus(Times(Sqr(b),c),Times(Sqr(a),pd)))),PositiveQ(a)),NegativeQ(pd)))),
ISetDelayed(Int(Times(u_DEFAULT,Power(Plus(Times(a_DEFAULT,Power(x_,r_DEFAULT)),Times(b_DEFAULT,Power(x_,s_DEFAULT))),m_DEFAULT)),x_Symbol),
    Condition(Int(Times(u,Power(x,Times(m,r)),Power(Plus(a,Times(b,Power(x,Plus(s,Times(CN1,r))))),m)),x),And(And(FreeQ(List(a,b,m,r,s),x),IntegerQ(m)),PosQ(Plus(s,Times(CN1,r))))))
  );
}
