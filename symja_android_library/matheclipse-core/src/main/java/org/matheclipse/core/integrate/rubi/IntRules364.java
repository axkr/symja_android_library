package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctions.*;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IAST;
import com.google.common.base.Supplier;

/** 
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class IntRules364 { 
  public static IAST RULES = List( 
IIntegrate(7281,Integrate(u_,x_Symbol),
    With(list(Set($s("lst"),SubstForFractionalPowerOfQuotientOfLinears(u,x))),Condition(Simp(Dist(Times(Part($s("lst"),C2),Part($s("lst"),C4)),Subst(Integrate(Part($s("lst"),C1),x),x,Power(Part($s("lst"),C3),Power(Part($s("lst"),C2),CN1))),x),x),Not(FalseQ($s("lst")))))),
IIntegrate(7282,Integrate(Times(u_DEFAULT,Power(Times(a_DEFAULT,Power(v_,m_DEFAULT),Power(w_,n_DEFAULT),Power(z_,q_DEFAULT)),p_)),x_Symbol),
    Condition(Simp(Dist(Times(Power(a,IntPart(p)),Power(Times(a,Power(v,m),Power(w,n),Power(z,q)),FracPart(p)),Power(Times(Power(v,Times(m,FracPart(p))),Power(w,Times(n,FracPart(p))),Power(z,Times(q,FracPart(p)))),CN1)),Integrate(Times(u,Power(v,Times(m,p)),Power(w,Times(n,p)),Power(z,Times(p,q))),x),x),x),And(FreeQ(List(a,m,n,p,q),x),Not(IntegerQ(p)),Not(FreeQ(v,x)),Not(FreeQ(w,x)),Not(FreeQ(z,x))))),
IIntegrate(7283,Integrate(Times(u_DEFAULT,Power(Times(a_DEFAULT,Power(v_,m_DEFAULT),Power(w_,n_DEFAULT)),p_)),x_Symbol),
    Condition(Simp(Dist(Times(Power(a,IntPart(p)),Power(Times(a,Power(v,m),Power(w,n)),FracPart(p)),Power(Times(Power(v,Times(m,FracPart(p))),Power(w,Times(n,FracPart(p)))),CN1)),Integrate(Times(u,Power(v,Times(m,p)),Power(w,Times(n,p))),x),x),x),And(FreeQ(List(a,m,n,p),x),Not(IntegerQ(p)),Not(FreeQ(v,x)),Not(FreeQ(w,x))))),
IIntegrate(7284,Integrate(Times(u_DEFAULT,Power(Times(a_DEFAULT,Power(v_,m_DEFAULT)),p_)),x_Symbol),
    Condition(Simp(Dist(Times(Power(a,IntPart(p)),Power(Times(a,Power(v,m)),FracPart(p)),Power(Power(v,Times(m,FracPart(p))),CN1)),Integrate(Times(u,Power(v,Times(m,p))),x),x),x),And(FreeQ(list(a,m,p),x),Not(IntegerQ(p)),Not(FreeQ(v,x)),Not(And(EqQ(a,C1),EqQ(m,C1))),Not(And(EqQ(v,x),EqQ(m,C1)))))),
IIntegrate(7285,Integrate(Times(u_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_))),p_)),x_Symbol),
    Condition(Simp(Dist(Times(Power(b,IntPart(p)),Power(Plus(a,Times(b,Power(x,n))),FracPart(p)),Power(Times(Power(x,Times(n,FracPart(p))),Power(Plus(C1,Times(a,Power(Times(Power(x,n),b),CN1))),FracPart(p))),CN1)),Integrate(Times(u,Power(x,Times(n,p)),Power(Plus(C1,Times(a,Power(Times(Power(x,n),b),CN1))),p)),x),x),x),And(FreeQ(list(a,b,p),x),Not(IntegerQ(p)),ILtQ(n,C0),Not(RationalFunctionQ(u,x)),IntegerQ(Plus(p,C1D2))))),
IIntegrate(7286,Integrate(Times(u_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(v_,n_))),p_)),x_Symbol),
    Condition(Simp(Dist(Times(Power(Plus(a,Times(b,Power(v,n))),FracPart(p)),Power(Times(Power(v,Times(n,FracPart(p))),Power(Plus(b,Times(a,Power(Power(v,n),CN1))),FracPart(p))),CN1)),Integrate(Times(u,Power(v,Times(n,p)),Power(Plus(b,Times(a,Power(Power(v,n),CN1))),p)),x),x),x),And(FreeQ(list(a,b,p),x),Not(IntegerQ(p)),ILtQ(n,C0),BinomialQ(v,x),Not(LinearQ(v,x))))),
IIntegrate(7287,Integrate(Times(u_DEFAULT,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(v_,n_),Power(x_,m_DEFAULT))),p_)),x_Symbol),
    Condition(Simp(Dist(Times(Power(Plus(a,Times(b,Power(x,m),Power(v,n))),FracPart(p)),Power(Times(Power(v,Times(n,FracPart(p))),Power(Plus(Times(b,Power(x,m)),Times(a,Power(Power(v,n),CN1))),FracPart(p))),CN1)),Integrate(Times(u,Power(v,Times(n,p)),Power(Plus(Times(b,Power(x,m)),Times(a,Power(Power(v,n),CN1))),p)),x),x),x),And(FreeQ(List(a,b,m,p),x),Not(IntegerQ(p)),ILtQ(n,C0),BinomialQ(v,x)))),
IIntegrate(7288,Integrate(Times(u_DEFAULT,Power(Plus(Times(a_DEFAULT,Power(x_,r_DEFAULT)),Times(b_DEFAULT,Power(x_,s_DEFAULT))),m_)),x_Symbol),
    Condition(With(list(Set(v,Times(Power(Plus(Times(a,Power(x,r)),Times(b,Power(x,s))),FracPart(m)),Power(Times(Power(x,Times(r,FracPart(m))),Power(Plus(a,Times(b,Power(x,Subtract(s,r)))),FracPart(m))),CN1)))),Condition(Simp(Dist(v,Integrate(Times(u,Power(x,Times(m,r)),Power(Plus(a,Times(b,Power(x,Subtract(s,r)))),m)),x),x),x),NeQ(Simplify(v),C1))),And(FreeQ(List(a,b,m,r,s),x),Not(IntegerQ(m)),PosQ(Subtract(s,r))))),
IIntegrate(7289,Integrate(Times(u_,Power(Plus(a_,Times(b_DEFAULT,Power(x_,n_))),CN1)),x_Symbol),
    Condition(With(list(Set(v,RationalFunctionExpand(Times(u,Power(Plus(a,Times(b,Power(x,n))),CN1)),x))),Condition(Integrate(v,x),SumQ(v))),And(FreeQ(list(a,b),x),IGtQ(n,C0)))),
IIntegrate(7290,Integrate(Times(u_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(c_DEFAULT,Power(x_,$p("n2",true)))),p_DEFAULT)),x_Symbol),
    Condition(Simp(Dist(Power(Times(Power(C4,p),Power(c,p)),CN1),Integrate(Times(u,Power(Plus(b,Times(C2,c,Power(x,n))),Times(C2,p))),x),x),x),And(FreeQ(List(a,b,c,n),x),EqQ($s("n2"),Times(C2,n)),EqQ(Subtract(Sqr(b),Times(C4,a,c)),C0),IntegerQ(p),Not(AlgebraicFunctionQ(u,x))))),
IIntegrate(7291,Integrate(Times(u_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(c_DEFAULT,Power(x_,$p("n2",true)))),p_)),x_Symbol),
    Condition(Simp(Dist(Times(Power(Plus(a,Times(b,Power(x,n)),Times(c,Power(x,Times(C2,n)))),p),Power(Power(Plus(b,Times(C2,c,Power(x,n))),Times(C2,p)),CN1)),Integrate(Times(u,Power(Plus(b,Times(C2,c,Power(x,n))),Times(C2,p))),x),x),x),And(FreeQ(List(a,b,c,n,p),x),EqQ($s("n2"),Times(C2,n)),EqQ(Subtract(Sqr(b),Times(C4,a,c)),C0),Not(IntegerQ(p)),Not(AlgebraicFunctionQ(u,x))))),
IIntegrate(7292,Integrate(Times(u_,Power(Plus(a_DEFAULT,Times(b_DEFAULT,Power(x_,n_DEFAULT)),Times(c_DEFAULT,Power(x_,$p("n2",true)))),CN1)),x_Symbol),
    Condition(With(list(Set(v,RationalFunctionExpand(Times(u,Power(Plus(a,Times(b,Power(x,n)),Times(c,Power(x,Times(C2,n)))),CN1)),x))),Condition(Integrate(v,x),SumQ(v))),And(FreeQ(list(a,b,c),x),EqQ($s("n2"),Times(C2,n)),IGtQ(n,C0)))),
IIntegrate(7293,Integrate(Times(u_DEFAULT,Power(Plus(Times(a_DEFAULT,Power(x_,m_DEFAULT)),Times(b_DEFAULT,Sqrt(Times(c_DEFAULT,Power(x_,n_))))),CN1)),x_Symbol),
    Condition(Integrate(Times(u,Subtract(Times(a,Power(x,m)),Times(b,Sqrt(Times(c,Power(x,n))))),Power(Subtract(Times(Sqr(a),Power(x,Times(C2,m))),Times(Sqr(b),c,Power(x,n))),CN1)),x),FreeQ(List(a,b,c,m,n),x))),
IIntegrate(7294,Integrate(u_,x_Symbol),
    With(list(Set($s("lst"),FunctionOfLinear(u,x))),Condition(Simp(Dist(Power(Part($s("lst"),C3),CN1),Subst(Integrate(Part($s("lst"),C1),x),x,Plus(Part($s("lst"),C2),Times(Part($s("lst"),C3),x))),x),x),Not(FalseQ($s("lst")))))),
IIntegrate(7295,Integrate(Times(u_,Power(x_,CN1)),x_Symbol),
    Condition(With(list(Set($s("lst"),PowerVariableExpn(u,C0,x))),Condition(Simp(Dist(Power(Part($s("lst"),C2),CN1),Subst(Integrate(NormalizeIntegrand(Simplify(Times(Part($s("lst"),C1),Power(x,CN1))),x),x),x,Power(Times(Part($s("lst"),C3),x),Part($s("lst"),C2))),x),x),And(Not(FalseQ($s("lst"))),NeQ(Part($s("lst"),C2),C0)))),And(NonsumQ(u),Not(RationalFunctionQ(u,x))))),
IIntegrate(7296,Integrate(Times(u_,Power(x_,m_DEFAULT)),x_Symbol),
    Condition(With(list(Set($s("lst"),PowerVariableExpn(u,Plus(m,C1),x))),Condition(Simp(Dist(Power(Part($s("lst"),C2),CN1),Subst(Integrate(NormalizeIntegrand(Simplify(Times(Part($s("lst"),C1),Power(x,CN1))),x),x),x,Power(Times(Part($s("lst"),C3),x),Part($s("lst"),C2))),x),x),And(Not(FalseQ($s("lst"))),NeQ(Part($s("lst"),C2),Plus(m,C1))))),And(IntegerQ(m),NeQ(m,CN1),NonsumQ(u),Or(GtQ(m,C0),Not(AlgebraicFunctionQ(u,x)))))),
IIntegrate(7297,Integrate(Times($p("§fx"),Power(x_,m_)),x_Symbol),
    Condition(With(list(Set(k,Denominator(m))),Simp(Dist(k,Subst(Integrate(Times(Power(x,Plus(Times(k,Plus(m,C1)),CN1)),$($s("§substpower"),$s("§fx"),x,k)),x),x,Power(x,Power(k,CN1))),x),x)),FractionQ(m))),
IIntegrate(7298,Integrate(u_,x_Symbol),
    Condition(With(list(Set($s("lst"),FunctionOfSquareRootOfQuadratic(u,x))),Condition(Simp(Dist(C2,Subst(Integrate(Part($s("lst"),C1),x),x,Part($s("lst"),C2)),x),x),And(Not(FalseQ($s("lst"))),EqQ(Part($s("lst"),C3),C1)))),EulerIntegrandQ(u,x))),
IIntegrate(7299,Integrate(u_,x_Symbol),
    Condition(With(list(Set($s("lst"),FunctionOfSquareRootOfQuadratic(u,x))),Condition(Simp(Dist(C2,Subst(Integrate(Part($s("lst"),C1),x),x,Part($s("lst"),C2)),x),x),And(Not(FalseQ($s("lst"))),EqQ(Part($s("lst"),C3),C2)))),EulerIntegrandQ(u,x))),
IIntegrate(7300,Integrate(u_,x_Symbol),
    Condition(With(list(Set($s("lst"),FunctionOfSquareRootOfQuadratic(u,x))),Condition(Simp(Dist(C2,Subst(Integrate(Part($s("lst"),C1),x),x,Part($s("lst"),C2)),x),x),And(Not(FalseQ($s("lst"))),EqQ(Part($s("lst"),C3),C3)))),EulerIntegrandQ(u,x)))
  );
}
