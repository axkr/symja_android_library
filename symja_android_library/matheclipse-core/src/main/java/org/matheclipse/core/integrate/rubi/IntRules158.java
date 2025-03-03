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
class IntRules158 { 
  public static IAST RULES = List( 
IIntegrate(3161,Integrate(Times(Power($($s("§cos"),Plus(d_DEFAULT,Times(e_DEFAULT,x_))),n_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,$($s("§sec"),Plus(d_DEFAULT,Times(e_DEFAULT,x_)))),Times(c_DEFAULT,$($s("§tan"),Plus(d_DEFAULT,Times(e_DEFAULT,x_))))),n_DEFAULT)),x_Symbol),
    Condition(Integrate(Power(Plus(b,Times(a,Cos(Plus(d,Times(e,x)))),Times(c,Sin(Plus(d,Times(e,x))))),n),x),And(FreeQ(List(a,b,c,d,e),x),IntegerQ(n)))),
IIntegrate(3162,Integrate(Times(Power(Plus(a_DEFAULT,Times($($s("§csc"),Plus(d_DEFAULT,Times(e_DEFAULT,x_))),b_DEFAULT),Times($($s("§cot"),Plus(d_DEFAULT,Times(e_DEFAULT,x_))),c_DEFAULT)),n_DEFAULT),Power($($s("§sin"),Plus(d_DEFAULT,Times(e_DEFAULT,x_))),n_DEFAULT)),x_Symbol),
    Condition(Integrate(Power(Plus(b,Times(a,Sin(Plus(d,Times(e,x)))),Times(c,Cos(Plus(d,Times(e,x))))),n),x),And(FreeQ(List(a,b,c,d,e),x),IntegerQ(n)))),
IIntegrate(3163,Integrate(Times(Power($($s("§cos"),Plus(d_DEFAULT,Times(e_DEFAULT,x_))),n_),Power(Plus(a_DEFAULT,Times(b_DEFAULT,$($s("§sec"),Plus(d_DEFAULT,Times(e_DEFAULT,x_)))),Times(c_DEFAULT,$($s("§tan"),Plus(d_DEFAULT,Times(e_DEFAULT,x_))))),n_)),x_Symbol),
    Condition(Dist(Times(Power(Cos(Plus(d,Times(e,x))),n),Power(Plus(a,Times(b,Sec(Plus(d,Times(e,x)))),Times(c,Tan(Plus(d,Times(e,x))))),n),Power(Power(Plus(b,Times(a,Cos(Plus(d,Times(e,x)))),Times(c,Sin(Plus(d,Times(e,x))))),n),CN1)),Integrate(Power(Plus(b,Times(a,Cos(Plus(d,Times(e,x)))),Times(c,Sin(Plus(d,Times(e,x))))),n),x),x),And(FreeQ(List(a,b,c,d,e),x),Not(IntegerQ(n))))),
IIntegrate(3164,Integrate(Times(Power(Plus(a_DEFAULT,Times($($s("§csc"),Plus(d_DEFAULT,Times(e_DEFAULT,x_))),b_DEFAULT),Times($($s("§cot"),Plus(d_DEFAULT,Times(e_DEFAULT,x_))),c_DEFAULT)),n_),Power($($s("§sin"),Plus(d_DEFAULT,Times(e_DEFAULT,x_))),n_)),x_Symbol),
    Condition(Dist(Times(Power(Sin(Plus(d,Times(e,x))),n),Power(Plus(a,Times(b,Csc(Plus(d,Times(e,x)))),Times(c,Cot(Plus(d,Times(e,x))))),n),Power(Power(Plus(b,Times(a,Sin(Plus(d,Times(e,x)))),Times(c,Cos(Plus(d,Times(e,x))))),n),CN1)),Integrate(Power(Plus(b,Times(a,Sin(Plus(d,Times(e,x)))),Times(c,Cos(Plus(d,Times(e,x))))),n),x),x),And(FreeQ(List(a,b,c,d,e),x),Not(IntegerQ(n))))),
IIntegrate(3165,Integrate(Times(Power($($s("§sec"),Plus(d_DEFAULT,Times(e_DEFAULT,x_))),n_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,$($s("§sec"),Plus(d_DEFAULT,Times(e_DEFAULT,x_)))),Times(c_DEFAULT,$($s("§tan"),Plus(d_DEFAULT,Times(e_DEFAULT,x_))))),m_)),x_Symbol),
    Condition(Integrate(Power(Power(Plus(b,Times(a,Cos(Plus(d,Times(e,x)))),Times(c,Sin(Plus(d,Times(e,x))))),n),CN1),x),And(FreeQ(List(a,b,c,d,e),x),EqQ(Plus(m,n),C0),IntegerQ(n)))),
IIntegrate(3166,Integrate(Times(Power($($s("§csc"),Plus(d_DEFAULT,Times(e_DEFAULT,x_))),n_DEFAULT),Power(Plus(a_DEFAULT,Times($($s("§csc"),Plus(d_DEFAULT,Times(e_DEFAULT,x_))),b_DEFAULT),Times($($s("§cot"),Plus(d_DEFAULT,Times(e_DEFAULT,x_))),c_DEFAULT)),m_)),x_Symbol),
    Condition(Integrate(Power(Power(Plus(b,Times(a,Sin(Plus(d,Times(e,x)))),Times(c,Cos(Plus(d,Times(e,x))))),n),CN1),x),And(FreeQ(List(a,b,c,d,e),x),EqQ(Plus(m,n),C0),IntegerQ(n)))),
IIntegrate(3167,Integrate(Times(Power($($s("§sec"),Plus(d_DEFAULT,Times(e_DEFAULT,x_))),n_DEFAULT),Power(Plus(a_DEFAULT,Times(b_DEFAULT,$($s("§sec"),Plus(d_DEFAULT,Times(e_DEFAULT,x_)))),Times(c_DEFAULT,$($s("§tan"),Plus(d_DEFAULT,Times(e_DEFAULT,x_))))),m_)),x_Symbol),
    Condition(Dist(Times(Power(Sec(Plus(d,Times(e,x))),n),Power(Plus(b,Times(a,Cos(Plus(d,Times(e,x)))),Times(c,Sin(Plus(d,Times(e,x))))),n),Power(Power(Plus(a,Times(b,Sec(Plus(d,Times(e,x)))),Times(c,Tan(Plus(d,Times(e,x))))),n),CN1)),Integrate(Power(Power(Plus(b,Times(a,Cos(Plus(d,Times(e,x)))),Times(c,Sin(Plus(d,Times(e,x))))),n),CN1),x),x),And(FreeQ(List(a,b,c,d,e),x),EqQ(Plus(m,n),C0),Not(IntegerQ(n))))),
IIntegrate(3168,Integrate(Times(Power($($s("§csc"),Plus(d_DEFAULT,Times(e_DEFAULT,x_))),n_DEFAULT),Power(Plus(a_DEFAULT,Times($($s("§csc"),Plus(d_DEFAULT,Times(e_DEFAULT,x_))),b_DEFAULT),Times($($s("§cot"),Plus(d_DEFAULT,Times(e_DEFAULT,x_))),c_DEFAULT)),m_)),x_Symbol),
    Condition(Dist(Times(Power(Csc(Plus(d,Times(e,x))),n),Power(Plus(b,Times(a,Sin(Plus(d,Times(e,x)))),Times(c,Cos(Plus(d,Times(e,x))))),n),Power(Power(Plus(a,Times(b,Csc(Plus(d,Times(e,x)))),Times(c,Cot(Plus(d,Times(e,x))))),n),CN1)),Integrate(Power(Power(Plus(b,Times(a,Sin(Plus(d,Times(e,x)))),Times(c,Cos(Plus(d,Times(e,x))))),n),CN1),x),x),And(FreeQ(List(a,b,c,d,e),x),EqQ(Plus(m,n),C0),Not(IntegerQ(n))))),
IIntegrate(3169,Integrate(Times(Plus(a_,Times(b_DEFAULT,Sqr($($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))))),Plus(A_DEFAULT,Times(B_DEFAULT,Sqr($($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))))),x_Symbol),
    Condition(Plus(Simp(Times(QQ(1L,8L),Plus(Times(C4,ASymbol,Plus(Times(C2,a),b)),Times(BSymbol,Plus(Times(C4,a),Times(C3,b)))),x),x),Negate(Simp(Times(b,BSymbol,Cos(Plus(e,Times(f,x))),Power(Sin(Plus(e,Times(f,x))),C3),Power(Times(C4,f),CN1)),x)),Negate(Simp(Times(Plus(Times(C4,ASymbol,b),Times(BSymbol,Plus(Times(C4,a),Times(C3,b)))),Cos(Plus(e,Times(f,x))),Sin(Plus(e,Times(f,x))),Power(Times(C8,f),CN1)),x))),FreeQ(List(a,b,e,f,ASymbol,BSymbol),x))),
IIntegrate(3170,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Sqr($($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))))),p_),Plus(A_DEFAULT,Times(B_DEFAULT,Sqr($($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))))),x_Symbol),
    Condition(Plus(Negate(Simp(Times(BSymbol,Cos(Plus(e,Times(f,x))),Sin(Plus(e,Times(f,x))),Power(Plus(a,Times(b,Sqr(Sin(Plus(e,Times(f,x)))))),p),Power(Times(C2,f,Plus(p,C1)),CN1)),x)),Dist(Power(Times(C2,Plus(p,C1)),CN1),Integrate(Times(Power(Plus(a,Times(b,Sqr(Sin(Plus(e,Times(f,x)))))),Subtract(p,C1)),Simp(Plus(Times(a,BSymbol),Times(C2,a,ASymbol,Plus(p,C1)),Times(Plus(Times(C2,ASymbol,b,Plus(p,C1)),Times(BSymbol,Plus(b,Times(C2,a,p),Times(C2,b,p)))),Sqr(Sin(Plus(e,Times(f,x)))))),x)),x),x)),And(FreeQ(List(a,b,e,f,ASymbol,BSymbol),x),GtQ(p,C0)))),
IIntegrate(3171,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Sqr($($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))))),CN1),Plus(A_DEFAULT,Times(B_DEFAULT,Sqr($($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))))),x_Symbol),
    Condition(Plus(Simp(Times(BSymbol,x,Power(b,CN1)),x),Dist(Times(Subtract(Times(ASymbol,b),Times(a,BSymbol)),Power(b,CN1)),Integrate(Power(Plus(a,Times(b,Sqr(Sin(Plus(e,Times(f,x)))))),CN1),x),x)),FreeQ(List(a,b,e,f,ASymbol,BSymbol),x))),
IIntegrate(3172,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Sqr($($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))))),CN1D2),Plus(A_DEFAULT,Times(B_DEFAULT,Sqr($($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))))),x_Symbol),
    Condition(Plus(Dist(Times(BSymbol,Power(b,CN1)),Integrate(Sqrt(Plus(a,Times(b,Sqr(Sin(Plus(e,Times(f,x))))))),x),x),Dist(Times(Subtract(Times(ASymbol,b),Times(a,BSymbol)),Power(b,CN1)),Integrate(Power(Plus(a,Times(b,Sqr(Sin(Plus(e,Times(f,x)))))),CN1D2),x),x)),FreeQ(List(a,b,e,f,ASymbol,BSymbol),x))),
IIntegrate(3173,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,Sqr($($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))))),p_),Plus(A_DEFAULT,Times(B_DEFAULT,Sqr($($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))))),x_Symbol),
    Condition(Subtract(Negate(Simp(Times(Subtract(Times(ASymbol,b),Times(a,BSymbol)),Cos(Plus(e,Times(f,x))),Sin(Plus(e,Times(f,x))),Power(Plus(a,Times(b,Sqr(Sin(Plus(e,Times(f,x)))))),Plus(p,C1)),Power(Times(C2,a,f,Plus(a,b),Plus(p,C1)),CN1)),x)),Dist(Power(Times(C2,a,Plus(a,b),Plus(p,C1)),CN1),Integrate(Times(Power(Plus(a,Times(b,Sqr(Sin(Plus(e,Times(f,x)))))),Plus(p,C1)),Simp(Plus(Times(a,BSymbol),Times(CN1,ASymbol,Plus(Times(C2,a,Plus(p,C1)),Times(b,Plus(Times(C2,p),C3)))),Times(C2,Subtract(Times(ASymbol,b),Times(a,BSymbol)),Plus(p,C2),Sqr(Sin(Plus(e,Times(f,x)))))),x)),x),x)),And(FreeQ(List(a,b,e,f,ASymbol,BSymbol),x),LtQ(p,CN1),NeQ(Plus(a,b),C0)))),
IIntegrate(3174,Integrate(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,Sqr($($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))))),p_),Plus(A_DEFAULT,Times(B_DEFAULT,Sqr($($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))))),x_Symbol),
    Condition(With(list(Set($s("ff"),FreeFactors(Tan(Plus(e,Times(f,x))),x))),Dist(Times($s("ff"),Power(Plus(a,Times(b,Sqr(Sin(Plus(e,Times(f,x)))))),p),Power(Sqr(Sec(Plus(e,Times(f,x)))),p),Power(Times(f,Power(Plus(a,Times(Plus(a,b),Sqr(Tan(Plus(e,Times(f,x)))))),p)),CN1)),Subst(Integrate(Times(Power(Plus(a,Times(Plus(a,b),Sqr($s("ff")),Sqr(x))),p),Plus(ASymbol,Times(Plus(ASymbol,BSymbol),Sqr($s("ff")),Sqr(x))),Power(Power(Plus(C1,Times(Sqr($s("ff")),Sqr(x))),Plus(p,C2)),CN1)),x),x,Times(Tan(Plus(e,Times(f,x))),Power($s("ff"),CN1))),x)),And(FreeQ(List(a,b,e,f,ASymbol,BSymbol),x),Not(IntegerQ(p))))),
IIntegrate(3175,Integrate(Times(u_DEFAULT,Power(Plus(a_,Times(b_DEFAULT,Sqr($($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))))),p_)),x_Symbol),
    Condition(Dist(Power(a,p),Integrate(ActivateTrig(Times(u,Power($($s("§cos"),Plus(e,Times(f,x))),Times(C2,p)))),x),x),And(FreeQ(List(a,b,e,f,p),x),EqQ(Plus(a,b),C0),IntegerQ(p)))),
IIntegrate(3176,Integrate(Times(u_DEFAULT,Power(Plus(a_,Times(b_DEFAULT,Sqr($($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))))),p_)),x_Symbol),
    Condition(Integrate(ActivateTrig(Times(u,Power(Times(a,Sqr($($s("§cos"),Plus(e,Times(f,x))))),p))),x),And(FreeQ(List(a,b,e,f,p),x),EqQ(Plus(a,b),C0)))),
IIntegrate(3177,Integrate(Sqrt(Plus(a_,Times(b_DEFAULT,Sqr($($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))))),x_Symbol),
    Condition(Simp(Times(Sqrt(a),EllipticE(Plus(e,Times(f,x)),Times(CN1,b,Power(a,CN1))),Power(f,CN1)),x),And(FreeQ(List(a,b,e,f),x),GtQ(a,C0)))),
IIntegrate(3178,Integrate(Sqrt(Plus(a_,Times(b_DEFAULT,Sqr($($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))))),x_Symbol),
    Condition(Dist(Times(Sqrt(Plus(a,Times(b,Sqr(Sin(Plus(e,Times(f,x))))))),Power(Plus(C1,Times(b,Sqr(Sin(Plus(e,Times(f,x)))),Power(a,CN1))),CN1D2)),Integrate(Sqrt(Plus(C1,Times(b,Sqr(Sin(Plus(e,Times(f,x)))),Power(a,CN1)))),x),x),And(FreeQ(List(a,b,e,f),x),Not(GtQ(a,C0))))),
IIntegrate(3179,Integrate(Sqr(Plus(a_,Times(b_DEFAULT,Sqr($($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))))))),x_Symbol),
    Condition(Plus(Simp(Times(QQ(1L,8L),Plus(Times(C8,Sqr(a)),Times(C8,a,b),Times(C3,Sqr(b))),x),x),Negate(Simp(Times(Sqr(b),Cos(Plus(e,Times(f,x))),Power(Sin(Plus(e,Times(f,x))),C3),Power(Times(C4,f),CN1)),x)),Negate(Simp(Times(b,Plus(Times(C8,a),Times(C3,b)),Cos(Plus(e,Times(f,x))),Sin(Plus(e,Times(f,x))),Power(Times(C8,f),CN1)),x))),FreeQ(List(a,b,e,f),x))),
IIntegrate(3180,Integrate(Power(Plus(a_,Times(b_DEFAULT,Sqr($($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))))),p_),x_Symbol),
    Condition(Plus(Negate(Simp(Times(b,Cos(Plus(e,Times(f,x))),Sin(Plus(e,Times(f,x))),Power(Plus(a,Times(b,Sqr(Sin(Plus(e,Times(f,x)))))),Subtract(p,C1)),Power(Times(C2,f,p),CN1)),x)),Dist(Power(Times(C2,p),CN1),Integrate(Times(Power(Plus(a,Times(b,Sqr(Sin(Plus(e,Times(f,x)))))),Subtract(p,C2)),Simp(Plus(Times(a,Plus(b,Times(C2,a,p))),Times(b,Plus(Times(C2,a),b),Subtract(Times(C2,p),C1),Sqr(Sin(Plus(e,Times(f,x)))))),x)),x),x)),And(FreeQ(List(a,b,e,f),x),NeQ(Plus(a,b),C0),GtQ(p,C1))))
  );
}
