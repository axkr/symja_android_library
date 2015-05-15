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
public class IntRules4 { 
  public static IAST RULES = List( 
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),x_Symbol),
    Condition(Times(x,Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Power(a,-1)),And(FreeQ(List(a,b,n,p),x),ZeroQ(Plus(Times(n,Plus(p,C1)),C1))))),
ISetDelayed(Int(Sqr(Plus(a_,Times(b_DEFAULT,Power(x_,n_)))),x_Symbol),
    Condition(Int(Plus(Sqr(a),Times(C2,a,b,Power(x,n)),Times(Sqr(b),Power(x,Times(C2,n)))),x),And(FreeQ(List(a,b,n),x),NonzeroQ(Plus(Times(C3,n),C1))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),x_Symbol),
    Condition(Int(Times(Power(x,Times(n,p)),Power(Plus(b,Times(a,Power(x,Negate(n)))),p)),x),And(And(And(FreeQ(List(a,b),x),RationalQ(n)),Less(n,C0)),IntegerQ(p)))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),x_Symbol),
    Condition(Int(ExpandIntegrand(Power(Plus(a,Times(b,Power(x,n))),p),x),x),And(And(FreeQ(List(a,b),x),NonzeroQ(Plus(Times(n,Plus(p,C1)),C1))),PositiveIntegerQ(n,p)))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),x_Symbol),
    Condition(Plus(Times(x,Power(Plus(a,Times(b,Power(x,n))),p),Power(Plus(Times(n,p),C1),-1)),Times(a,n,p,Power(Plus(Times(n,p),C1),-1),Int(Power(Plus(a,Times(b,Power(x,n))),Plus(p,Negate(C1))),x))),And(And(And(And(And(And(FreeQ(List(a,b),x),NonzeroQ(Plus(Times(n,Plus(p,C1)),C1))),PositiveIntegerQ(n)),RationalQ(p)),Greater(p,C0)),NonzeroQ(Plus(Times(n,p),C1))),Or(IntegerQ(Times(C2,p)),IntegerQ(Plus(p,Power(n,-1))))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),x_Symbol),
    Condition(Plus(Times(CN1,x,Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Power(Times(a,n,Plus(p,C1)),-1)),Times(Plus(Times(n,Plus(p,C1)),C1),Power(Times(a,n,Plus(p,C1)),-1),Int(Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),x))),And(And(And(And(And(FreeQ(List(a,b),x),NonzeroQ(Plus(Times(n,Plus(p,C1)),C1))),PositiveIntegerQ(n)),RationalQ(p)),Less(p,CN1)),Or(IntegerQ(Times(C2,p)),IntegerQ(Plus(p,Power(n,-1))))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Power(x_,3))),-1),x_Symbol),
    Condition(Module(List(Set(r,Numerator(Rt(Times(a,Power(b,-1)),C3))),Set(s,Denominator(Rt(Times(a,Power(b,-1)),C3)))),Plus(Times(r,Power(Times(C3,a),-1),Int(Power(Plus(r,Times(s,x)),-1),x)),Times(r,Power(Times(C3,a),-1),Int(Times(Plus(Times(C2,r),Times(CN1,s,x)),Power(Plus(Sqr(r),Times(CN1,r,s,x),Times(Sqr(s),Sqr(x))),-1)),x)))),And(FreeQ(List(a,b),x),PosQ(Times(a,Power(b,-1)))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Power(x_,3))),-1),x_Symbol),
    Condition(Module(List(Set(r,Numerator(Rt(Times(CN1,a,Power(b,-1)),C3))),Set(s,Denominator(Rt(Times(CN1,a,Power(b,-1)),C3)))),Plus(Times(r,Power(Times(C3,a),-1),Int(Power(Plus(r,Times(CN1,s,x)),-1),x)),Times(r,Power(Times(C3,a),-1),Int(Times(Plus(Times(C2,r),Times(s,x)),Power(Plus(Sqr(r),Times(r,s,x),Times(Sqr(s),Sqr(x))),-1)),x)))),And(FreeQ(List(a,b),x),NegQ(Times(a,Power(b,-1)))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),-1),x_Symbol),
    Condition(Module(List(Set(r,Numerator(Rt(Times(a,Power(b,-1)),n))),Set(s,Denominator(Rt(Times(a,Power(b,-1)),n)))),Int(Plus(Times(r,Power(Times(a,n,Plus(r,Times(s,x))),-1)),Sum(Times(C2,r,Plus(r,Times(CN1,s,Cos(Times(Plus(Times(C2,k),Negate(C1)),Pi,Power(n,-1))),x)),Power(Times(a,n,Plus(Sqr(r),Times(CN1,C2,r,s,Cos(Times(Plus(Times(C2,k),Negate(C1)),Pi,Power(n,-1))),x),Times(Sqr(s),Sqr(x)))),-1)),List(k,C1,Times(C1D2,Plus(n,Negate(C1)))))),x)),And(And(FreeQ(List(a,b),x),PositiveIntegerQ(Times(C1D2,Plus(n,Negate(C3))))),PosQ(Times(a,Power(b,-1)))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),-1),x_Symbol),
    Condition(Module(List(Set(r,Numerator(Rt(Times(CN1,a,Power(b,-1)),n))),Set(s,Denominator(Rt(Times(CN1,a,Power(b,-1)),n)))),Int(Plus(Times(r,Power(Times(a,n,Plus(r,Times(CN1,s,x))),-1)),Sum(Times(C2,r,Plus(r,Times(s,Cos(Times(Plus(Times(C2,k),Negate(C1)),Pi,Power(n,-1))),x)),Power(Times(a,n,Plus(Sqr(r),Times(C2,r,s,Cos(Times(Plus(Times(C2,k),Negate(C1)),Pi,Power(n,-1))),x),Times(Sqr(s),Sqr(x)))),-1)),List(k,C1,Times(C1D2,Plus(n,Negate(C1)))))),x)),And(And(FreeQ(List(a,b),x),PositiveIntegerQ(Times(C1D2,Plus(n,Negate(C3))))),NegQ(Times(a,Power(b,-1)))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),-1),x_Symbol),
    Condition(Times(Rt(Times(a,Power(b,-1)),C2),Power(a,-1),ArcTan(Times(x,Power(Rt(Times(a,Power(b,-1)),C2),-1)))),And(FreeQ(List(a,b),x),PosQ(Times(a,Power(b,-1)))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),-1),x_Symbol),
    Condition(Times(Rt(Times(CN1,a,Power(b,-1)),C2),Power(a,-1),ArcTanh(Times(x,Power(Rt(Times(CN1,a,Power(b,-1)),C2),-1)))),And(FreeQ(List(a,b),x),NegQ(Times(a,Power(b,-1)))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),-1),x_Symbol),
    Condition(Module(List(Set(r,Numerator(Rt(Times(a,Power(b,-1)),Times(C1D2,n)))),Set(s,Denominator(Rt(Times(a,Power(b,-1)),Times(C1D2,n))))),Plus(Times(C2,r,Power(Times(a,n),-1),Int(Power(Plus(r,Times(s,Sqr(x))),-1),x)),Times(C4,r,Power(Times(a,n),-1),Int(Sum(Times(Plus(r,Times(CN1,s,Cos(Times(C2,Plus(Times(C2,k),Negate(C1)),Pi,Power(n,-1))),Sqr(x))),Power(Plus(Sqr(r),Times(CN1,C2,r,s,Cos(Times(C2,Plus(Times(C2,k),Negate(C1)),Pi,Power(n,-1))),Sqr(x)),Times(Sqr(s),Power(x,4))),-1)),List(k,C1,Times(C1D4,Plus(n,Negate(C2))))),x)))),And(And(FreeQ(List(a,b),x),PositiveIntegerQ(Times(C1D4,Plus(n,Negate(C2))))),PosQ(Times(a,Power(b,-1)))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),-1),x_Symbol),
    Condition(Module(List(Set(r,Numerator(Rt(Times(CN1,a,Power(b,-1)),Times(C1D2,n)))),Set(s,Denominator(Rt(Times(CN1,a,Power(b,-1)),Times(C1D2,n))))),Plus(Times(C2,r,Power(Times(a,n),-1),Int(Power(Plus(r,Times(CN1,s,Sqr(x))),-1),x)),Times(C4,r,Power(Times(a,n),-1),Int(Sum(Times(Plus(r,Times(CN1,s,Cos(Times(C4,k,Pi,Power(n,-1))),Sqr(x))),Power(Plus(Sqr(r),Times(CN1,C2,r,s,Cos(Times(C4,k,Pi,Power(n,-1))),Sqr(x)),Times(Sqr(s),Power(x,4))),-1)),List(k,C1,Times(C1D4,Plus(n,Negate(C2))))),x)))),And(And(FreeQ(List(a,b),x),PositiveIntegerQ(Times(C1D4,Plus(n,Negate(C2))))),NegQ(Times(a,Power(b,-1)))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Power(x_,4))),-1),x_Symbol),
    Condition(Module(List(Set(r,Numerator(Rt(Times(a,Power(b,-1)),C2))),Set(s,Denominator(Rt(Times(a,Power(b,-1)),C2)))),Plus(Times(Power(Times(C2,r),-1),Int(Times(Plus(r,Times(CN1,s,Sqr(x))),Power(Plus(a,Times(b,Power(x,4))),-1)),x)),Times(Power(Times(C2,r),-1),Int(Times(Plus(r,Times(s,Sqr(x))),Power(Plus(a,Times(b,Power(x,4))),-1)),x)))),And(FreeQ(List(a,b),x),Or(PositiveQ(Times(a,Power(b,-1))),And(And(PosQ(Times(a,Power(b,-1))),NonsumQ(a)),NonsumQ(b)))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),-1),x_Symbol),
    Condition(Module(List(Set(r,Numerator(Rt(Times(a,Power(b,-1)),C4))),Set(s,Denominator(Rt(Times(a,Power(b,-1)),C4)))),Plus(Times(r,Power(Times(C2,CSqrt2,a),-1),Int(Times(Plus(Times(CSqrt2,r),Times(CN1,s,Power(x,Times(C1D4,n)))),Power(Plus(Sqr(r),Times(CN1,CSqrt2,r,s,Power(x,Times(C1D4,n))),Times(Sqr(s),Power(x,Times(C1D2,n)))),-1)),x)),Times(r,Power(Times(C2,CSqrt2,a),-1),Int(Times(Plus(Times(CSqrt2,r),Times(s,Power(x,Times(C1D4,n)))),Power(Plus(Sqr(r),Times(CSqrt2,r,s,Power(x,Times(C1D4,n))),Times(Sqr(s),Power(x,Times(C1D2,n)))),-1)),x)))),And(And(And(FreeQ(List(a,b),x),PositiveIntegerQ(Times(C1D4,n))),Greater(n,C4)),PositiveQ(Times(a,Power(b,-1)))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),-1),x_Symbol),
    Condition(Module(List(Set(r,Numerator(Rt(Times(CN1,a,Power(b,-1)),C2))),Set(s,Denominator(Rt(Times(CN1,a,Power(b,-1)),C2)))),Plus(Times(r,Power(Times(C2,a),-1),Int(Power(Plus(r,Times(CN1,s,Power(x,Times(C1D2,n)))),-1),x)),Times(r,Power(Times(C2,a),-1),Int(Power(Plus(r,Times(s,Power(x,Times(C1D2,n)))),-1),x)))),And(And(FreeQ(List(a,b),x),PositiveIntegerQ(Times(C1D4,n))),Not(PositiveQ(Times(a,Power(b,-1))))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1D2),x_Symbol),
    Condition(Times(ArcSinh(Times(Rt(b,C2),x,Power(a,CN1D2))),Power(Rt(b,C2),-1)),And(And(FreeQ(List(a,b),x),PositiveQ(a)),PosQ(b)))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1D2),x_Symbol),
    Condition(Times(ArcSin(Times(Rt(Negate(b),C2),x,Power(a,CN1D2))),Power(Rt(Negate(b),C2),-1)),And(And(FreeQ(List(a,b),x),PositiveQ(a)),NegQ(b)))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Sqr(x_))),CN1D2),x_Symbol),
    Condition(Subst(Int(Power(Plus(C1,Times(CN1,b,Sqr(x))),-1),x),x,Times(x,Power(Plus(a,Times(b,Sqr(x))),CN1D2))),And(FreeQ(List(a,b),x),Not(PositiveQ(a))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Power(x_,3))),CN1D2),x_Symbol),
    Condition(Times(Sqrt(Plus(Power(a,C1D3),Times(Power(b,C1D3),x))),Sqrt(Plus(Times(Power(a,C1D3),Sqrt(Times(CN3,Power(b,QQ(2L,3L))))),Times(Power(a,C1D3),Power(b,C1D3)),Times(CN1,C2,Power(b,QQ(2L,3L)),x))),Sqrt(Plus(Times(Power(a,C1D3),Sqrt(Times(CN3,Power(b,QQ(2L,3L))))),Times(CN1,Power(a,C1D3),Power(b,C1D3)),Times(C2,Power(b,QQ(2L,3L)),x))),Power(Plus(a,Times(b,Power(x,3))),CN1D2),Int(Power(Times(Sqrt(Plus(Power(a,C1D3),Times(Power(b,C1D3),x))),Sqrt(Plus(Times(Power(a,C1D3),Sqrt(Times(CN3,Power(b,QQ(2L,3L))))),Times(Power(a,C1D3),Power(b,C1D3)),Times(CN1,C2,Power(b,QQ(2L,3L)),x))),Sqrt(Plus(Times(Power(a,C1D3),Sqrt(Times(CN3,Power(b,QQ(2L,3L))))),Times(CN1,Power(a,C1D3),Power(b,C1D3)),Times(C2,Power(b,QQ(2L,3L)),x)))),-1),x)),And(FreeQ(List(a,b),x),PosQ(b)))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Power(x_,3))),CN1D2),x_Symbol),
    Condition(Times(Sqrt(Plus(Power(a,C1D3),Times(CN1,Power(Negate(b),C1D3),x))),Sqrt(Plus(Times(Power(a,C1D3),Sqrt(Times(CN3,Power(Negate(b),QQ(2L,3L))))),Times(CN1,Power(a,C1D3),Power(Negate(b),C1D3)),Times(CN1,C2,Power(Negate(b),QQ(2L,3L)),x))),Sqrt(Plus(Times(Power(a,C1D3),Sqrt(Times(CN3,Power(Negate(b),QQ(2L,3L))))),Times(Power(a,C1D3),Power(Negate(b),C1D3)),Times(C2,Power(Negate(b),QQ(2L,3L)),x))),Power(Plus(a,Times(b,Power(x,3))),CN1D2),Int(Power(Times(Sqrt(Plus(Power(a,C1D3),Times(CN1,Power(Negate(b),C1D3),x))),Sqrt(Plus(Times(Power(a,C1D3),Sqrt(Times(CN3,Power(Negate(b),QQ(2L,3L))))),Times(CN1,Power(a,C1D3),Power(Negate(b),C1D3)),Times(CN1,C2,Power(Negate(b),QQ(2L,3L)),x))),Sqrt(Plus(Times(Power(a,C1D3),Sqrt(Times(CN3,Power(Negate(b),QQ(2L,3L))))),Times(Power(a,C1D3),Power(Negate(b),C1D3)),Times(C2,Power(Negate(b),QQ(2L,3L)),x)))),-1),x)),And(FreeQ(List(a,b),x),NegQ(b)))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Power(x_,4))),CN1D2),x_Symbol),
    Condition(Times(EllipticF(ArcSin(Times(Rt(Times(CN1,b,Power(a,-1)),C4),x)),CN1),Power(Times(Sqrt(a),Rt(Times(CN1,b,Power(a,-1)),C4)),-1)),And(FreeQ(List(a,b),x),PositiveQ(a)))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Power(x_,4))),CN1D2),x_Symbol),
    Condition(Times(Sqrt(Times(Plus(a,Times(b,Power(x,4))),Power(a,-1))),Power(Plus(a,Times(b,Power(x,4))),CN1D2),Int(Power(Plus(C1,Times(b,Power(x,4),Power(a,-1))),CN1D2),x)),And(FreeQ(List(a,b),x),Not(PositiveQ(a))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Power(x_,6))),CN1D2),x_Symbol),
    Condition(Times(x,Plus(Power(a,C1D3),Times(Power(b,C1D3),Sqr(x))),Sqrt(Times(Plus(Power(a,QQ(2L,3L)),Times(CN1,Power(a,C1D3),Power(b,C1D3),Sqr(x)),Times(Power(b,QQ(2L,3L)),Power(x,4))),Power(Plus(Power(a,C1D3),Times(Plus(C1,CSqrt3),Power(b,C1D3),Sqr(x))),-2))),Power(Times(C2,Power(C3,C1D4),Power(a,C1D3),Sqrt(Times(Power(b,C1D3),Sqr(x),Plus(Power(a,C1D3),Times(Power(b,C1D3),Sqr(x))),Power(Plus(Power(a,C1D3),Times(Plus(C1,CSqrt3),Power(b,C1D3),Sqr(x))),-2))),Sqrt(Plus(a,Times(b,Power(x,6))))),-1),EllipticF(ArcCos(Times(Plus(Power(a,C1D3),Times(CN1,Plus(CN1,CSqrt3),Power(b,C1D3),Sqr(x))),Power(Plus(Power(a,C1D3),Times(Plus(C1,CSqrt3),Power(b,C1D3),Sqr(x))),-1))),Times(C1D4,Plus(C2,CSqrt3)))),FreeQ(List(a,b),x))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),x_Symbol),
    Condition(Negate(Subst(Int(Times(Power(Plus(a,Times(b,Power(x,Negate(n)))),p),Power(x,-2)),x),x,Power(x,-1))),And(And(FreeQ(List(a,b,p),x),NonzeroQ(Plus(Times(n,Plus(p,C1)),C1))),NegativeIntegerQ(n)))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),x_Symbol),
    Condition(Module(List(Set(d,Denominator(n))),Times(d,Subst(Int(Times(Power(x,Plus(d,Negate(C1))),Power(Plus(a,Times(b,Power(x,Times(d,n)))),p)),x),x,Power(x,Power(d,-1))))),And(And(And(FreeQ(List(a,b,p),x),NonzeroQ(Plus(Times(n,Plus(p,C1)),C1))),Not(IntegerQ(n))),FractionQ(n)))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),x_Symbol),
    Condition(Module(List(Set(q,Denominator(p))),Times(q,Power(a,Plus(p,Power(n,-1))),Power(n,-1),Subst(Int(Times(Power(x,Plus(Times(q,Power(n,-1)),Negate(C1))),Power(Power(Plus(C1,Times(CN1,b,Power(x,q))),Plus(p,Power(n,-1),C1)),-1)),x),x,Times(Power(x,Times(n,Power(q,-1))),Power(Power(Plus(a,Times(b,Power(x,n))),Power(q,-1)),-1))))),And(And(And(And(And(FreeQ(List(a,b),x),NonzeroQ(Plus(Times(n,Plus(p,C1)),C1))),RationalQ(n,p)),Less(Less(CN1,p),C0)),Unequal(p,CN1D2)),IntegerQ(Plus(p,Power(n,-1)))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),x_Symbol),
    Condition(Int(ExpandIntegrand(Power(Plus(a,Times(b,Power(x,n))),p),x),x),And(And(FreeQ(List(a,b,n),x),NonzeroQ(Plus(Times(n,Plus(p,C1)),C1))),PositiveIntegerQ(p)))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),x_Symbol),
    Condition(Times(Power(a,p),x,Hypergeometric2F1(Negate(p),Power(n,-1),Plus(Power(n,-1),C1),Times(CN1,b,Power(x,n),Power(a,-1)))),And(And(And(And(And(FreeQ(List(a,b,n,p),x),NonzeroQ(Plus(Times(n,Plus(p,C1)),C1))),Not(PositiveIntegerQ(p))),Not(IntegerQ(Power(n,-1)))),Not(NegativeIntegerQ(Simplify(Plus(Power(n,-1),p))))),Or(IntegerQ(p),PositiveQ(a))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_),x_Symbol),
    Condition(Times(x,Power(Plus(a,Times(b,Power(x,n))),Plus(p,C1)),Power(a,-1),Hypergeometric2F1(C1,Plus(Power(n,-1),p,C1),Plus(Power(n,-1),C1),Times(CN1,b,Power(x,n),Power(a,-1)))),And(And(And(And(FreeQ(List(a,b,n,p),x),NonzeroQ(Plus(Times(n,Plus(p,C1)),C1))),Not(PositiveIntegerQ(p))),Not(IntegerQ(Power(n,-1)))),Not(NegativeIntegerQ(Simplify(Plus(Power(n,-1),p))))))),
ISetDelayed(Int(Power(Plus(a_,Times(b_DEFAULT,Power(u_,n_))),p_),x_Symbol),
    Condition(Times(Power(Coefficient(u,x,C1),-1),Subst(Int(Power(Plus(a,Times(b,Power(x,n))),p),x),x,u)),And(And(FreeQ(List(a,b,n,p),x),LinearQ(u,x)),NonzeroQ(Plus(u,Negate(x)))))),
ISetDelayed(Int(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Times(c_DEFAULT,Power(x_,-1)),n_))),p_),x_Symbol),
    Condition(Times(CN1,c,Subst(Int(Times(Power(Plus(a,Times(b,Power(x,n))),p),Power(x,-2)),x),x,Times(c,Power(x,-1)))),FreeQ(List(a,b,c,n,p),x))),
ISetDelayed(Int(Power(u_,p_),x_Symbol),
    Condition(Int(Power(ExpandToSum(u,x),p),x),And(And(FreeQ(p,x),BinomialQ(u,x)),Not(BinomialMatchQ(u,x))))),
ISetDelayed(Int(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(Times(c_DEFAULT,Power(x_,n_)),q_))),p_DEFAULT),x_Symbol),
    Condition(Times(x,Power(Power(Times(c,Power(x,n)),Power(n,-1)),-1),Subst(Int(Power(Plus(a,Times(b,Power(x,Times(n,q)))),p),x),x,Power(Times(c,Power(x,n)),Power(n,-1)))),And(FreeQ(List(a,b,c,q,n,p),x),IntegerQ(Times(n,q)))))
  );
}
