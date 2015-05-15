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
public class IntRules139 { 
  public static IAST RULES = List( 
ISetDelayed(Int(Times(u_DEFAULT,Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_DEFAULT))),p_DEFAULT)),x_Symbol),
    Condition(Int(Times(u,Power(Times(b,Power(x,n)),p)),x),And(FreeQ(List(a,b,n,p),x),ZeroQ(a)))),
ISetDelayed(Int(Times(u_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT))),p_DEFAULT)),x_Symbol),
    Condition(Int(Times(u,Power(a,p)),x),And(FreeQ(List(a,b,n,p),x),ZeroQ(b)))),
ISetDelayed(Int(Times(u_DEFAULT,Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(c_DEFAULT,Power(x_,j_DEFAULT))),p_DEFAULT)),x_Symbol),
    Condition(Int(Times(u,Power(Plus(Times(b,Power(x,n)),Times(c,Power(x,Times(C2,n)))),p)),x),And(And(FreeQ(List(a,b,c,n,p),x),ZeroQ(Plus(j,Times(CN1,C2,n)))),ZeroQ(a)))),
ISetDelayed(Int(Times(u_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(c_DEFAULT,Power(x_,j_DEFAULT))),p_DEFAULT)),x_Symbol),
    Condition(Int(Times(u,Power(Plus(a,Times(c,Power(x,Times(C2,n)))),p)),x),And(And(FreeQ(List(a,b,c,n,p),x),ZeroQ(Plus(j,Times(CN1,C2,n)))),ZeroQ(b)))),
ISetDelayed(Int(Times(u_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(c_DEFAULT,Power(x_,j_DEFAULT))),p_DEFAULT)),x_Symbol),
    Condition(Int(Times(u,Power(Plus(a,Times(b,Power(x,n))),p)),x),And(And(FreeQ(List(a,b,c,n,p),x),ZeroQ(Plus(j,Times(CN1,C2,n)))),ZeroQ(c)))),
ISetDelayed(Int(Times(u_DEFAULT,Power(x_,m_)),x_Symbol),
    Condition(Int(Times(u,Power(x,-1)),x),And(ZeroQ(Plus(m,C1)),UnsameQ(m,CN1)))),
ISetDelayed(Int(a_,x_Symbol),
    Condition(Times(a,x),FreeQ(a,x))),
ISetDelayed(Int(Times(a_,Plus(b_,Times(c_DEFAULT,x_))),x_Symbol),
    Condition(Times(a,Sqr(Plus(b,Times(c,x))),Power(Times(C2,c),-1)),FreeQ(List(a,b,c),x))),
ISetDelayed(Int(Negate(u_),x_Symbol),
    Times(Identity(CN1),Int(u,x))),
ISetDelayed(Int(Times(u_,Complex(C0,a_)),x_Symbol),
    Condition(Times(Complex(Identity(C0),a),Int(u,x)),And(FreeQ(a,x),OneQ(Sqr(a))))),
ISetDelayed(Int(Times(a_,u_),x_Symbol),
    Condition(Times(a,Int(u,x)),And(FreeQ(a,x),Not(MatchQ(u,Condition(Times(b_,v_),FreeQ(b,x))))))),
ISetDelayed(Int(u_,x_Symbol),
    Condition(IntSum(u,x),SumQ(u))),
ISetDelayed(Int(Times(u_,Power(Times(c_DEFAULT,x_),m_DEFAULT)),x_Symbol),
    Condition(Int(ExpandIntegrand(Times(Power(Times(c,x),m),u),x),x),And(And(FreeQ(List(c,m),x),SumQ(u)),Not(MatchQ(u,Condition(Plus(a_,Times(b_DEFAULT,v_)),And(FreeQ(List(a,b),x),InverseFunctionQ(v)))))))),
ISetDelayed(Int(Times(u_DEFAULT,Power(Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),n_)),p_)),x_Symbol),
    Condition(Times(Power(c,Plus(p,Negate(C1D2))),Sqrt(Times(c,Power(Plus(a,Times(b,x)),n))),Power(Power(Plus(a,Times(b,x)),Times(C1D2,n)),-1),Int(Times(u,Power(Plus(a,Times(b,x)),Times(n,p))),x)),And(FreeQ(List(a,b,c,n,p),x),PositiveIntegerQ(Plus(p,C1D2))))),
ISetDelayed(Int(Times(u_DEFAULT,Power(Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),n_)),p_)),x_Symbol),
    Condition(Times(Power(c,Plus(p,C1D2)),Power(Plus(a,Times(b,x)),Times(C1D2,n)),Power(Times(c,Power(Plus(a,Times(b,x)),n)),CN1D2),Int(Times(u,Power(Plus(a,Times(b,x)),Times(n,p))),x)),And(FreeQ(List(a,b,c,n,p),x),NegativeIntegerQ(Plus(p,Negate(C1D2)))))),
ISetDelayed(Int(Times(u_DEFAULT,Power(Times(c_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),n_)),p_)),x_Symbol),
    Condition(Times(Power(Times(c,Power(Plus(a,Times(b,x)),n)),p),Power(Power(Plus(a,Times(b,x)),Times(n,p)),-1),Int(Times(u,Power(Plus(a,Times(b,x)),Times(n,p))),x)),And(FreeQ(List(a,b,c,n,p),x),Not(IntegerQ(Times(C2,p)))))),
ISetDelayed(Int(Times(u_DEFAULT,Power(Times(c_DEFAULT,Power(Times(d_,Plus(a_DEFAULT,Times(b_DEFAULT,x_))),p_)),q_)),x_Symbol),
    Condition(Times(Power(Times(c,Power(Times(d,Plus(a,Times(b,x))),p)),q),Power(Power(Plus(a,Times(b,x)),Times(p,q)),-1),Int(Times(u,Power(Plus(a,Times(b,x)),Times(p,q))),x)),And(And(FreeQ(List(a,b,c,d,p,q),x),Not(IntegerQ(p))),Not(IntegerQ(q))))),
ISetDelayed(Int(Times(u_DEFAULT,Power(Times(c_DEFAULT,Power(Times(d_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),n_)),p_)),q_)),x_Symbol),
    Condition(Times(Power(Times(c,Power(Times(d,Power(Plus(a,Times(b,x)),n)),p)),q),Power(Power(Plus(a,Times(b,x)),Times(n,p,q)),-1),Int(Times(u,Power(Plus(a,Times(b,x)),Times(n,p,q))),x)),And(And(FreeQ(List(a,b,c,d,n,p,q),x),Not(IntegerQ(p))),Not(IntegerQ(q))))),
ISetDelayed(Int(Times(u_DEFAULT,Power(v_,m_DEFAULT),Power(Times(b_,v_),n_)),x_Symbol),
    Condition(Times(Power(Power(b,m),-1),Int(Times(u,Power(Times(b,v),Plus(m,n))),x)),And(FreeQ(List(b,n),x),IntegerQ(m)))),
ISetDelayed(Int(Times(u_DEFAULT,Power(Times(a_DEFAULT,v_),m_),Power(Times(b_DEFAULT,v_),n_)),x_Symbol),
    Condition(Times(Power(a,Plus(m,C1D2)),Power(b,Plus(n,Negate(C1D2))),Sqrt(Times(b,v)),Power(Times(a,v),CN1D2),Int(Times(u,Power(v,Plus(m,n))),x)),And(And(And(FreeQ(List(a,b,m),x),Not(IntegerQ(m))),PositiveIntegerQ(Plus(n,C1D2))),IntegerQ(Plus(m,n))))),
ISetDelayed(Int(Times(u_DEFAULT,Power(Times(a_DEFAULT,v_),m_),Power(Times(b_DEFAULT,v_),n_)),x_Symbol),
    Condition(Times(Power(a,Plus(m,Negate(C1D2))),Power(b,Plus(n,C1D2)),Sqrt(Times(a,v)),Power(Times(b,v),CN1D2),Int(Times(u,Power(v,Plus(m,n))),x)),And(And(And(FreeQ(List(a,b,m),x),Not(IntegerQ(m))),NegativeIntegerQ(Plus(n,Negate(C1D2)))),IntegerQ(Plus(m,n))))),
ISetDelayed(Int(Times(u_DEFAULT,Power(Times(a_DEFAULT,v_),m_),Power(Times(b_DEFAULT,v_),n_)),x_Symbol),
    Condition(Times(Power(a,Plus(m,n)),Power(Times(b,v),n),Power(Power(Times(a,v),n),-1),Int(Times(u,Power(v,Plus(m,n))),x)),And(And(And(FreeQ(List(a,b,m,n),x),Not(IntegerQ(m))),Not(IntegerQ(n))),IntegerQ(Plus(m,n))))),
ISetDelayed(Int(Times(u_DEFAULT,Power(Times(a_DEFAULT,v_),m_),Power(Times(b_DEFAULT,v_),n_)),x_Symbol),
    Condition(Times(Power(Times(b,v),n),Power(Power(Times(a,v),n),-1),Int(Times(u,Power(Times(a,v),Plus(m,n))),x)),And(And(And(FreeQ(List(a,b,m,n),x),Not(IntegerQ(m))),Not(IntegerQ(n))),Not(IntegerQ(Plus(m,n)))))),
ISetDelayed(Int(Times(u_DEFAULT,Power(Plus(a_,Times(b_DEFAULT,v_)),m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,v_)),n_DEFAULT)),x_Symbol),
    Condition(Times(Power(Times(b,Power(d,-1)),m),Int(Times(u,Power(Plus(c,Times(d,v)),Plus(m,n))),x)),And(And(And(FreeQ(List(a,b,c,d,m,n),x),ZeroQ(Plus(Times(b,c),Times(CN1,a,d)))),Or(IntegerQ(m),PositiveQ(Times(b,Power(d,-1))))),Or(Not(IntegerQ(n)),LessEqual(LeafCount(Plus(c,Times(d,x))),LeafCount(Plus(a,Times(b,x)))))))),
ISetDelayed(Int(Times(u_DEFAULT,Power(Plus(a_,Times(b_DEFAULT,v_)),m_),Power(Plus(c_,Times(d_DEFAULT,v_)),n_)),x_Symbol),
    Condition(Times(Power(Plus(a,Times(b,v)),m),Power(Power(Plus(c,Times(d,v)),m),-1),Int(Times(u,Power(Plus(c,Times(d,v)),Plus(m,n))),x)),And(And(FreeQ(List(a,b,c,d,m,n),x),ZeroQ(Plus(Times(b,c),Times(CN1,a,d)))),Not(Or(Or(IntegerQ(m),IntegerQ(n)),PositiveQ(Times(b,Power(d,-1)))))))),
ISetDelayed(Int(Times(u_DEFAULT,Power(Plus(a_,Times(b_DEFAULT,v_)),m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,v_)),n_DEFAULT)),x_Symbol),
    Condition(Times(Power(Times(b,Power(d,-1)),m),Int(Times(u,Power(Plus(c,Times(d,v)),Plus(m,n))),x)),And(And(And(FreeQ(List(a,b,c,d,m,n),x),ZeroQ(Plus(Times(b,c),Times(CN1,a,d)))),Or(IntegerQ(m),PositiveQ(Times(b,Power(d,-1))))),Or(Not(IntegerQ(n)),LessEqual(LeafCount(Plus(c,Times(d,x))),LeafCount(Plus(a,Times(b,x)))))))),
ISetDelayed(Int(Times(u_DEFAULT,Power(Plus(a_,Times(b_DEFAULT,v_)),m_),Power(Plus(c_,Times(d_DEFAULT,v_)),n_)),x_Symbol),
    Condition(Times(Power(Plus(a,Times(b,v)),m),Power(Power(Plus(c,Times(d,v)),m),-1),Int(Times(u,Power(Plus(c,Times(d,v)),Plus(m,n))),x)),And(And(FreeQ(List(a,b,c,d,m,n),x),ZeroQ(Plus(Times(b,c),Times(CN1,a,d)))),Not(Or(Or(IntegerQ(m),IntegerQ(n)),PositiveQ(Times(b,Power(d,-1)))))))),
ISetDelayed(Int(Times(u_DEFAULT,Power(Times(a_DEFAULT,v_),m_),Plus(Times(b_DEFAULT,v_),Times(c_DEFAULT,Sqr(v_)))),x_Symbol),
    Condition(Times(Power(a,-1),Int(Times(u,Power(Times(a,v),Plus(m,C1)),Simp(Plus(b,Times(c,v)),x)),x)),And(And(FreeQ(List(a,b,c),x),RationalQ(m)),LessEqual(m,CN1)))),
ISetDelayed(Int(Times(u_DEFAULT,Plus(A_DEFAULT,Times(B_DEFAULT,v_),Times(C_DEFAULT,Sqr(v_))),Power(Plus(a_,Times(b_DEFAULT,v_)),m_)),x_Symbol),
    Condition(Times(Power(b,-2),Int(Times(u,Power(Plus(a,Times(b,v)),Plus(m,C1)),Simp(Plus(Times(b,BSymbol),Times(CN1,a,CSymbol),Times(b,CSymbol,v)),x)),x)),And(And(And(FreeQ(List(a,b,ASymbol,BSymbol,CSymbol),x),ZeroQ(Plus(Times(ASymbol,Sqr(b)),Times(CN1,a,b,BSymbol),Times(Sqr(a),CSymbol)))),RationalQ(m)),LessEqual(m,CN1)))),
ISetDelayed(Int(Times(u_DEFAULT,Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_DEFAULT))),m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Power(x_,q_DEFAULT))),p_DEFAULT)),x_Symbol),
    Condition(Times(Power(Times(d,Power(a,-1)),p),Int(Times(u,Power(Plus(a,Times(b,Power(x,n))),Plus(m,p)),Power(Power(x,Times(n,p)),-1)),x)),And(And(And(And(FreeQ(List(a,b,c,d,m,n),x),ZeroQ(Plus(n,q))),IntegerQ(p)),ZeroQ(Plus(Times(a,c),Times(CN1,b,d)))),Not(And(IntegerQ(m),NegQ(n)))))),
ISetDelayed(Int(Times(u_DEFAULT,Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_DEFAULT))),m_DEFAULT),Power(Plus(c_,Times(d_DEFAULT,Power(x_,j_))),p_DEFAULT)),x_Symbol),
    Condition(Times(Power(Times(CN1,Sqr(b),Power(d,-1)),m),Int(Times(u,Power(Plus(a,Times(CN1,b,Power(x,n))),Negate(m))),x)),And(And(And(And(And(FreeQ(List(a,b,c,d,m,n,p),x),ZeroQ(Plus(j,Times(CN1,C2,n)))),ZeroQ(Plus(m,p))),ZeroQ(Plus(Times(Sqr(b),c),Times(Sqr(a),d)))),PositiveQ(a)),NegativeQ(d)))),
ISetDelayed(Int(Times(u_DEFAULT,Power(Plus(Times(a_DEFAULT,Power(x_,r_DEFAULT)),Times(b_DEFAULT,Power(x_,s_DEFAULT))),m_DEFAULT)),x_Symbol),
    Condition(Int(Times(u,Power(x,Times(m,r)),Power(Plus(a,Times(b,Power(x,Plus(s,Negate(r))))),m)),x),And(And(FreeQ(List(a,b,m,r,s),x),IntegerQ(m)),PosQ(Plus(s,Negate(r))))))
  );
}
