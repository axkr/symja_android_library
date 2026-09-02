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
class IntRules189 { 
  public static IAST RULES = List( 
IIntegrate(3781,Integrate(Times(Plus(A_,Times(B_DEFAULT,$($s("§sin"),Plus(d_DEFAULT,Times(e_DEFAULT,x_))))),Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(d_DEFAULT,Times(e_DEFAULT,x_)))),Times(c_DEFAULT,Sqr($($s("§sin"),Plus(d_DEFAULT,Times(e_DEFAULT,x_)))))),n_)),x_Symbol),
    Condition(Simp(Dist(Power(Times(Power(C4,n),Power(c,n)),CN1),Integrate(Times(Plus(ASymbol,Times(BSymbol,Sin(Plus(d,Times(e,x))))),Power(Plus(b,Times(C2,c,Sin(Plus(d,Times(e,x))))),Times(C2,n))),x),x),x),And(FreeQ(List(a,b,c,d,e,ASymbol,BSymbol),x),EqQ(Subtract(Sqr(b),Times(C4,a,c)),C0),IntegerQ(n)))),
IIntegrate(3782,Integrate(Times(Power(Plus(Times($($s("§cos"),Plus(d_DEFAULT,Times(e_DEFAULT,x_))),b_DEFAULT),Times(Sqr($($s("§cos"),Plus(d_DEFAULT,Times(e_DEFAULT,x_)))),c_DEFAULT),a_),n_),Plus(Times($($s("§cos"),Plus(d_DEFAULT,Times(e_DEFAULT,x_))),B_DEFAULT),A_)),x_Symbol),
    Condition(Simp(Dist(Power(Times(Power(C4,n),Power(c,n)),CN1),Integrate(Times(Plus(ASymbol,Times(BSymbol,Cos(Plus(d,Times(e,x))))),Power(Plus(b,Times(C2,c,Cos(Plus(d,Times(e,x))))),Times(C2,n))),x),x),x),And(FreeQ(List(a,b,c,d,e,ASymbol,BSymbol),x),EqQ(Subtract(Sqr(b),Times(C4,a,c)),C0),IntegerQ(n)))),
IIntegrate(3783,Integrate(Times(Plus(A_,Times(B_DEFAULT,$($s("§sin"),Plus(d_DEFAULT,Times(e_DEFAULT,x_))))),Power(Plus(a_,Times(b_DEFAULT,$($s("§sin"),Plus(d_DEFAULT,Times(e_DEFAULT,x_)))),Times(c_DEFAULT,Sqr($($s("§sin"),Plus(d_DEFAULT,Times(e_DEFAULT,x_)))))),n_)),x_Symbol),
    Condition(Simp(Dist(Times(Power(Plus(a,Times(b,Sin(Plus(d,Times(e,x)))),Times(c,Sqr(Sin(Plus(d,Times(e,x)))))),n),Power(Power(Plus(b,Times(C2,c,Sin(Plus(d,Times(e,x))))),Times(C2,n)),CN1)),Integrate(Times(Plus(ASymbol,Times(BSymbol,Sin(Plus(d,Times(e,x))))),Power(Plus(b,Times(C2,c,Sin(Plus(d,Times(e,x))))),Times(C2,n))),x),x),x),And(FreeQ(List(a,b,c,d,e,ASymbol,BSymbol),x),EqQ(Subtract(Sqr(b),Times(C4,a,c)),C0),Not(IntegerQ(n))))),
IIntegrate(3784,Integrate(Times(Power(Plus(Times($($s("§cos"),Plus(d_DEFAULT,Times(e_DEFAULT,x_))),b_DEFAULT),Times(Sqr($($s("§cos"),Plus(d_DEFAULT,Times(e_DEFAULT,x_)))),c_DEFAULT),a_),n_),Plus(Times($($s("§cos"),Plus(d_DEFAULT,Times(e_DEFAULT,x_))),B_DEFAULT),A_)),x_Symbol),
    Condition(Simp(Dist(Times(Power(Plus(a,Times(b,Cos(Plus(d,Times(e,x)))),Times(c,Sqr(Cos(Plus(d,Times(e,x)))))),n),Power(Power(Plus(b,Times(C2,c,Cos(Plus(d,Times(e,x))))),Times(C2,n)),CN1)),Integrate(Times(Plus(ASymbol,Times(BSymbol,Cos(Plus(d,Times(e,x))))),Power(Plus(b,Times(C2,c,Cos(Plus(d,Times(e,x))))),Times(C2,n))),x),x),x),And(FreeQ(List(a,b,c,d,e,ASymbol,BSymbol),x),EqQ(Subtract(Sqr(b),Times(C4,a,c)),C0),Not(IntegerQ(n))))),
IIntegrate(3785,Integrate(Times(Plus(A_,Times(B_DEFAULT,$($s("§sin"),Plus(d_DEFAULT,Times(e_DEFAULT,x_))))),Power(Plus(a_DEFAULT,Times(b_DEFAULT,$($s("§sin"),Plus(d_DEFAULT,Times(e_DEFAULT,x_)))),Times(c_DEFAULT,Sqr($($s("§sin"),Plus(d_DEFAULT,Times(e_DEFAULT,x_)))))),CN1)),x_Symbol),
    Condition(Module(list(Set(q,Rt(Subtract(Sqr(b),Times(C4,a,c)),C2))),Plus(Simp(Dist(Plus(BSymbol,Times(Subtract(Times(b,BSymbol),Times(C2,ASymbol,c)),Power(q,CN1))),Integrate(Power(Plus(b,q,Times(C2,c,Sin(Plus(d,Times(e,x))))),CN1),x),x),x),Simp(Dist(Subtract(BSymbol,Times(Subtract(Times(b,BSymbol),Times(C2,ASymbol,c)),Power(q,CN1))),Integrate(Power(Plus(b,Negate(q),Times(C2,c,Sin(Plus(d,Times(e,x))))),CN1),x),x),x))),And(FreeQ(List(a,b,c,d,e,ASymbol,BSymbol),x),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0)))),
IIntegrate(3786,Integrate(Times(Power(Plus(a_DEFAULT,Times($($s("§cos"),Plus(d_DEFAULT,Times(e_DEFAULT,x_))),b_DEFAULT),Times(Sqr($($s("§cos"),Plus(d_DEFAULT,Times(e_DEFAULT,x_)))),c_DEFAULT)),CN1),Plus(Times($($s("§cos"),Plus(d_DEFAULT,Times(e_DEFAULT,x_))),B_DEFAULT),A_)),x_Symbol),
    Condition(Module(list(Set(q,Rt(Subtract(Sqr(b),Times(C4,a,c)),C2))),Plus(Simp(Dist(Plus(BSymbol,Times(Subtract(Times(b,BSymbol),Times(C2,ASymbol,c)),Power(q,CN1))),Integrate(Power(Plus(b,q,Times(C2,c,Cos(Plus(d,Times(e,x))))),CN1),x),x),x),Simp(Dist(Subtract(BSymbol,Times(Subtract(Times(b,BSymbol),Times(C2,ASymbol,c)),Power(q,CN1))),Integrate(Power(Plus(b,Negate(q),Times(C2,c,Cos(Plus(d,Times(e,x))))),CN1),x),x),x))),And(FreeQ(List(a,b,c,d,e,ASymbol,BSymbol),x),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0)))),
IIntegrate(3787,Integrate(Times(Plus(A_,Times(B_DEFAULT,$($s("§sin"),Plus(d_DEFAULT,Times(e_DEFAULT,x_))))),Power(Plus(a_DEFAULT,Times(b_DEFAULT,$($s("§sin"),Plus(d_DEFAULT,Times(e_DEFAULT,x_)))),Times(c_DEFAULT,Sqr($($s("§sin"),Plus(d_DEFAULT,Times(e_DEFAULT,x_)))))),n_)),x_Symbol),
    Condition(Integrate(ExpandTrig(Times(Plus(ASymbol,Times(BSymbol,$($s("§sin"),Plus(d,Times(e,x))))),Power(Plus(a,Times(b,$($s("§sin"),Plus(d,Times(e,x)))),Times(c,Sqr($($s("§sin"),Plus(d,Times(e,x)))))),n)),x),x),And(FreeQ(List(a,b,c,d,e,ASymbol,BSymbol),x),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),IntegerQ(n)))),
IIntegrate(3788,Integrate(Times(Power(Plus(a_DEFAULT,Times($($s("§cos"),Plus(d_DEFAULT,Times(e_DEFAULT,x_))),b_DEFAULT),Times(Sqr($($s("§cos"),Plus(d_DEFAULT,Times(e_DEFAULT,x_)))),c_DEFAULT)),n_),Plus(Times($($s("§cos"),Plus(d_DEFAULT,Times(e_DEFAULT,x_))),B_DEFAULT),A_)),x_Symbol),
    Condition(Integrate(ExpandTrig(Times(Plus(ASymbol,Times(BSymbol,$($s("§cos"),Plus(d,Times(e,x))))),Power(Plus(a,Times(b,$($s("§cos"),Plus(d,Times(e,x)))),Times(c,Sqr($($s("§cos"),Plus(d,Times(e,x)))))),n)),x),x),And(FreeQ(List(a,b,c,d,e,ASymbol,BSymbol),x),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),IntegerQ(n)))),
IIntegrate(3789,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Power(Plus(c,Times(d,x)),m),Cos(Plus(e,Times(f,x))),Power(f,CN1)),x),Simp(Dist(Times(d,m,Power(f,CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(m,CN1)),Cos(Plus(e,Times(f,x)))),x),x),x)),And(FreeQ(List(c,d,e,f),x),GtQ(m,C0)))),
IIntegrate(3790,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_),$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Sin(Plus(e,Times(f,x))),Power(Times(d,Plus(m,C1)),CN1)),x),Simp(Dist(Times(f,Power(Times(d,Plus(m,C1)),CN1)),Integrate(Times(Power(Plus(c,Times(d,x)),Plus(m,C1)),Cos(Plus(e,Times(f,x)))),x),x),x)),And(FreeQ(List(c,d,e,f),x),LtQ(m,CN1)))),
IIntegrate(3791,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),CN1),$($s("§sin"),Plus(e_DEFAULT,Times(Complex(C0,$p("fz")),f_DEFAULT,x_)))),x_Symbol),
    Condition(Simp(Times(CI,SinhIntegral(Plus(Times(c,f,$s("fz"),Power(d,CN1)),Times(f,$s("fz"),x))),Power(d,CN1)),x),And(FreeQ(List(c,d,e,f,$s("fz")),x),EqQ(Subtract(Times(d,e),Times(c,f,$s("fz"),CI)),C0)))),
IIntegrate(3792,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),CN1),$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),x_Symbol),
    Condition(Simp(Times(SinIntegral(Plus(e,Times(f,x))),Power(d,CN1)),x),And(FreeQ(List(c,d,e,f),x),EqQ(Subtract(Times(d,e),Times(c,f)),C0)))),
IIntegrate(3793,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),CN1),$($s("§sin"),Plus(e_DEFAULT,Times(Complex(C0,$p("fz")),f_DEFAULT,x_)))),x_Symbol),
    Condition(Simp(Times(CoshIntegral(Subtract(Times(CN1,c,f,$s("fz"),Power(d,CN1)),Times(f,$s("fz"),x))),Power(d,CN1)),x),And(FreeQ(List(c,d,e,f,$s("fz")),x),EqQ(Subtract(Times(d,Subtract(e,CPiHalf)),Times(c,f,$s("fz"),CI)),C0),NegQ(Times(c,f,$s("fz"),Power(d,CN1)),C0)))),
IIntegrate(3794,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),CN1),$($s("§sin"),Plus(e_DEFAULT,Times(Complex(C0,$p("fz")),f_DEFAULT,x_)))),x_Symbol),
    Condition(Simp(Times(CoshIntegral(Plus(Times(c,f,$s("fz"),Power(d,CN1)),Times(f,$s("fz"),x))),Power(d,CN1)),x),And(FreeQ(List(c,d,e,f,$s("fz")),x),EqQ(Subtract(Times(d,Subtract(e,CPiHalf)),Times(c,f,$s("fz"),CI)),C0)))),
IIntegrate(3795,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),CN1),$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),x_Symbol),
    Condition(Simp(Times(CosIntegral(Plus(e,Times(CN1,C1D2,Pi),Times(f,x))),Power(d,CN1)),x),And(FreeQ(List(c,d,e,f),x),EqQ(Subtract(Times(d,Subtract(e,CPiHalf)),Times(c,f)),C0)))),
IIntegrate(3796,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),CN1),$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Simp(Dist(Cos(Times(Subtract(Times(d,e),Times(c,f)),Power(d,CN1))),Integrate(Times(Sin(Plus(Times(c,f,Power(d,CN1)),Times(f,x))),Power(Plus(c,Times(d,x)),CN1)),x),x),x),Simp(Dist(Sin(Times(Subtract(Times(d,e),Times(c,f)),Power(d,CN1))),Integrate(Times(Cos(Plus(Times(c,f,Power(d,CN1)),Times(f,x))),Power(Plus(c,Times(d,x)),CN1)),x),x),x)),And(FreeQ(List(c,d,e,f),x),NeQ(Subtract(Times(d,e),Times(c,f)),C0)))),
IIntegrate(3797,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),CN1D2),$($s("§sin"),Plus(CPiHalf,e_DEFAULT,Times(f_DEFAULT,x_)))),x_Symbol),
    Condition(Simp(Dist(Times(C2,Power(d,CN1)),Subst(Integrate(Cos(Times(f,Sqr(x),Power(d,CN1))),x),x,Sqrt(Plus(c,Times(d,x)))),x),x),And(FreeQ(List(c,d,e,f),x),ComplexFreeQ(f),EqQ(Subtract(Times(d,e),Times(c,f)),C0)))),
IIntegrate(3798,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),CN1D2),$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),x_Symbol),
    Condition(Simp(Dist(Times(C2,Power(d,CN1)),Subst(Integrate(Sin(Times(f,Sqr(x),Power(d,CN1))),x),x,Sqrt(Plus(c,Times(d,x)))),x),x),And(FreeQ(List(c,d,e,f),x),ComplexFreeQ(f),EqQ(Subtract(Times(d,e),Times(c,f)),C0)))),
IIntegrate(3799,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),CN1D2),$($s("§sin"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),x_Symbol),
    Condition(Plus(Simp(Dist(Cos(Times(Subtract(Times(d,e),Times(c,f)),Power(d,CN1))),Integrate(Times(Sin(Plus(Times(c,f,Power(d,CN1)),Times(f,x))),Power(Plus(c,Times(d,x)),CN1D2)),x),x),x),Simp(Dist(Sin(Times(Subtract(Times(d,e),Times(c,f)),Power(d,CN1))),Integrate(Times(Cos(Plus(Times(c,f,Power(d,CN1)),Times(f,x))),Power(Plus(c,Times(d,x)),CN1D2)),x),x),x)),And(FreeQ(List(c,d,e,f),x),ComplexFreeQ(f),NeQ(Subtract(Times(d,e),Times(c,f)),C0)))),
IIntegrate(3800,Integrate(Times(Power(Plus(c_DEFAULT,Times(d_DEFAULT,x_)),m_DEFAULT),$($s("§sin"),Plus(e_DEFAULT,Times(Pi,k_DEFAULT),Times(f_DEFAULT,x_)))),x_Symbol),
    Condition(Subtract(Simp(Dist(CC(0L,1L,1L,2L),Integrate(Times(Power(Plus(c,Times(d,x)),m),Power(Times(Exp(Times(CI,k,Pi)),Exp(Times(CI,Plus(e,Times(f,x))))),CN1)),x),x),x),Simp(Dist(CC(0L,1L,1L,2L),Integrate(Times(Power(Plus(c,Times(d,x)),m),Exp(Times(CI,k,Pi)),Exp(Times(CI,Plus(e,Times(f,x))))),x),x),x)),And(FreeQ(List(c,d,e,f,m),x),IntegerQ(Times(C2,k)))))
  );
}
