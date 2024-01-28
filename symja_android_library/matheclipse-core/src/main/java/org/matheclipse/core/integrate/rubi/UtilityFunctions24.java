package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;

import org.matheclipse.core.interfaces.IAST;
/** 
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class UtilityFunctions24 { 
  public static IAST RULES = List( 
ISetDelayed(303,PolynomialDivide(u_,v_,w_,x_Symbol),
    ReplaceAll(PolynomialDivide(PolynomialInSubst(u,w,x),PolynomialInSubst(v,w,x),x),list(Rule(x,w)))),
ISetDelayed(304,ExpandToSum(u_,v_,x_Symbol),
    Module(list(Set(w,ExpandToSum(v,x)),r),CompoundExpression(Set(r,NonfreeTerms(w,x)),If(SumQ(r),Plus(Times(u,FreeTerms(w,x)),Map(Function(MergeMonomials(Times(u,Slot1),x)),r)),Plus(Times(u,FreeTerms(w,x)),MergeMonomials(Times(u,r),x)))))),
ISetDelayed(305,ExpandToSum(u_,x_Symbol),
    If(PolyQ(u,x),Simp(Apply(Plus,Map(Function(Times(Coeff(u,x,Slot1),Power(x,Slot1))),Expon(u,x,List))),x),If(BinomialQ(u,x),$(Function(Plus(Part(Slot1,C1),Times(Part(Slot1,C2),Power(x,Part(Slot1,C3))))),BinomialParts(u,x)),If(TrinomialQ(u,x),$(Function(Plus(Part(Slot1,C1),Times(Part(Slot1,C2),Power(x,Part(Slot1,C4))),Times(Part(Slot1,C3),Power(x,Times(C2,Part(Slot1,C4)))))),TrinomialParts(u,x)),If(GeneralizedBinomialQ(u,x),$(Function(Plus(Times(Part(Slot1,C1),Power(x,Part(Slot1,C4))),Times(Part(Slot1,C2),Power(x,Part(Slot1,C3))))),GeneralizedBinomialParts(u,x)),If(GeneralizedTrinomialQ(u,x),$(Function(Plus(Times(Part(Slot1,C1),Power(x,Part(Slot1,C5))),Times(Part(Slot1,C2),Power(x,Part(Slot1,C4))),Times(Part(Slot1,C3),Power(x,Subtract(Times(C2,Part(Slot1,C4)),Part(Slot1,C5)))))),GeneralizedTrinomialParts(u,x)),CompoundExpression(Print($str("Warning: Unrecognized expression for expansion "),u),Expand(u,x)))))))),
ISetDelayed(306,ExpandTrig(u_,x_Symbol),
    ActivateTrig(ExpandIntegrand(u,x))),
ISetDelayed(307,ExpandTrig(u_,v_,x_Symbol),
    With(list(Set(w,ExpandTrig(v,x)),Set(z,ActivateTrig(u))),If(SumQ(w),Map(Function(Times(z,Slot1)),w),Times(z,w)))),
ISetDelayed(308,ExpandIntegrand(Power(Plus(a_DEFAULT,Times(Log(Times(c_DEFAULT,Power(x_,n_DEFAULT))),b_DEFAULT)),p_DEFAULT),Times(Power(x_,m_DEFAULT),Power(Plus(d_,Times(e_DEFAULT,Power(x_,r_DEFAULT))),q_DEFAULT)),x_Symbol),
    Condition($($s("§distributeoverterms"),Power(Plus(a,Times(b,Log(Times(c,Power(x,n))))),p),$($s("§collectrecipterms"),ExpandIntegrand(Times(Power(x,m),Power(Plus(d,Times(e,Power(x,r))),q)),x),x),x),And(FreeQ(List(a,b,c,d,e,n),x),IGtQ(p,C0),IGtQ(r,C0),ILtQ(m,C0),ILtQ(q,C0),EqQ(r,C1)))),
ISetDelayed(309,ExpandIntegrand(u_,v_,x_Symbol),
    $($s("§distributeoverterms"),u,ExpandIntegrand(v,x),x)),
ISetDelayed(310,$($s("§distributeoverterms"),u_,v_,x_Symbol),
    With(list(Set(w,NonfreeTerms(v,x))),Plus(Times(u,FreeTerms(v,x)),If(SumQ(w),Map(Function(MergeMonomials(Times(u,Slot1),x)),w),MergeMonomials(Times(u,w),x))))),
ISetDelayed(311,$($s("§collectrecipterms"),Plus(u_DEFAULT,Times(A_DEFAULT,Power(x_,CN1)),Times(B_DEFAULT,Power(Plus(d_,Times(e_DEFAULT,x_)),CN1))),x_Symbol),
    Condition(Plus(u,Times(ASymbol,d,Power(Times(x,Plus(d,Times(e,x))),CN1))),And(FreeQ(List(ASymbol,BSymbol,d,e),x),EqQ(Plus(BSymbol,Times(ASymbol,e)),C0)))),
ISetDelayed(312,$($s("§collectrecipterms"),u_,x_Symbol),
    u),
ISetDelayed(313,ExpandIntegrand(Power(u_,p_DEFAULT),x_Symbol),
    Condition(If(EqQ(p,C1),ExpandCleanup(u,x),ExpandCleanup(Expand(Power(u,p),x),x)),And(SumQ(u),IGtQ(p,C0)))),
ISetDelayed(314,ExpandIntegrand(Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_DEFAULT),x_Symbol),
    Condition(ExpandIntegrand(Times(Power(x,Times(n,p)),Power(Plus(b,Times(a,Power(x,Negate(n)))),p)),x),And(IntegerQ(p),ILtQ(n,C0)))),
ISetDelayed(315,ExpandIntegrand(Times(Power(x_,m_DEFAULT),Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),p_DEFAULT)),x_Symbol),
    Condition(ExpandIntegrand(Times(Power(x,Plus(m,Times(n,p))),Power(Plus(b,Times(a,Power(x,Negate(n)))),p)),x),And(IntegerQ(p),ILtQ(n,C0)))),
ISetDelayed(316,ExpandIntegrand(Times($p("§px",true),Power(x_,m_),Power(Plus(Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(c_DEFAULT,Power(x_,$p("n1")))),p_DEFAULT)),x_Symbol),
    Condition(ExpandIntegrand(Times($s("§px"),Power(x,Plus(m,Times(n,p))),Power(Plus(b,Times(c,x)),p)),x),And(FreeQ(list(b,c,m),x),PolyQ($s("§px"),x),IGtQ(n,C0),EqQ($s("n1"),Plus(n,C1)),IntegerQ(p)))),
ISetDelayed(317,ExpandIntegrand(Times($p("§px",true),Power(Plus(Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(c_DEFAULT,Power(x_,$p("n1")))),p_DEFAULT)),x_Symbol),
    Condition(ExpandIntegrand(Times($s("§px"),Power(x,Times(n,p)),Power(Plus(b,Times(c,x)),p)),x),And(FreeQ(list(b,c),x),PolyQ($s("§px"),x),IGtQ(n,C0),EqQ($s("n1"),Plus(n,C1)),IntegerQ(p)))),
ISetDelayed(318,ExpandIntegrand(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(F_,u_))),p_DEFAULT),Power(Plus(c_DEFAULT,Times(d_DEFAULT,Power(F_,v_))),q_DEFAULT)),x_Symbol),
    Condition(With(list(Set(k,Simplify(Times(u,Power(v,CN1))))),Condition(ReplaceAll(ExpandIntegrand(Times(Power(Plus(a,Times(b,Power(x,Numerator(k)))),p),Power(Plus(c,Times(d,Power(x,Denominator(k)))),q)),x),Rule(x,Power(FSymbol,Times(v,Power(Denominator(k),CN1))))),RationalQ(k))),And(FreeQ(List(FSymbol,a,b,c,d),x),IntegersQ(p,q)))),
ISetDelayed(319,ExpandIntegrand(Times(Power(F_,Times(e_DEFAULT,Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),n_DEFAULT))),Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_)),m_DEFAULT),Power(Plus(g_DEFAULT,Times(h_DEFAULT,x_)),CN1)),x_Symbol),
    Condition(With(list(Set($s("tmp"),Subtract(Times(a,h),Times(b,g)))),Module(list(k),Plus(Times(SimplifyTerm(Times(Power($s("tmp"),m),Power(Power(h,m),CN1)),x),Power(FSymbol,Times(e,Power(Plus(c,Times(d,x)),n))),Power(Plus(g,Times(h,x)),CN1)),Sum(Times(SimplifyTerm(Times(b,Power($s("tmp"),Subtract(k,C1)),Power(Power(h,k),CN1)),x),Power(FSymbol,Times(e,Power(Plus(c,Times(d,x)),n))),Power(Plus(a,Times(b,x)),Subtract(m,k))),list(k,C1,m))))),And(FreeQ(List(FSymbol,a,b,c,d,e,g,h),x),IGtQ(m,C0),EqQ(Subtract(Times(b,c),Times(a,d)),C0)))),
ISetDelayed(320,ExpandIntegrand(Times(Power(F_,Times(b_DEFAULT,Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),n_DEFAULT))),Power(x_,m_DEFAULT),Power(Plus(e_,Times(f_DEFAULT,x_)),p_DEFAULT)),x_Symbol),
    Condition(If(And(IGtQ(m,C0),IGtQ(p,C0),LessEqual(m,p),Or(EqQ(n,C1),EqQ(Subtract(Times(d,e),Times(c,f)),C0))),ExpandLinearProduct(Times(Power(Plus(e,Times(f,x)),p),Power(FSymbol,Times(b,Power(Plus(c,Times(d,x)),n)))),Power(x,m),e,f,x),If(IGtQ(p,C0),Distribute(Times(Power(x,m),Power(FSymbol,Times(b,Power(Plus(c,Times(d,x)),n))),Expand(Power(Plus(e,Times(f,x)),p),x)),Plus,Times),ExpandIntegrand(Power(FSymbol,Times(b,Power(Plus(c,Times(d,x)),n))),Times(Power(x,m),Power(Plus(e,Times(f,x)),p)),x))),FreeQ(List(FSymbol,b,c,d,e,f,m,n,p),x))),
ISetDelayed(321,ExpandIntegrand(Times(Power(F_,Plus(a_DEFAULT,Times(b_DEFAULT,Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),n_DEFAULT)))),Power(x_,m_DEFAULT),Power(Plus(e_,Times(f_DEFAULT,x_)),p_DEFAULT)),x_Symbol),
    Condition(If(And(IGtQ(m,C0),IGtQ(p,C0),LessEqual(m,p),Or(EqQ(n,C1),EqQ(Subtract(Times(d,e),Times(c,f)),C0))),ExpandLinearProduct(Times(Power(Plus(e,Times(f,x)),p),Power(FSymbol,Plus(a,Times(b,Power(Plus(c,Times(d,x)),n))))),Power(x,m),e,f,x),If(IGtQ(p,C0),Distribute(Times(Power(x,m),Power(FSymbol,Plus(a,Times(b,Power(Plus(c,Times(d,x)),n)))),Expand(Power(Plus(e,Times(f,x)),p),x)),Plus,Times),ExpandIntegrand(Power(FSymbol,Plus(a,Times(b,Power(Plus(c,Times(d,x)),n)))),Times(Power(x,m),Power(Plus(e,Times(f,x)),p)),x))),FreeQ(List(FSymbol,a,b,c,d,e,f,m,n,p),x)))
  );
}
