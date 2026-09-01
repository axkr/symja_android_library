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
class IntRules213 { 
  public static IAST RULES = List( 
IIntegrate(4261,Integrate(Times(Cot(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),Plus(d_,Times(e_DEFAULT,x_))),x_Symbol),
    Condition(Simp(Times(e,Log(Sin(Plus(a,Times(b,x),Times(c,Sqr(x))))),Power(Times(C2,c),CN1)),x),And(FreeQ(List(a,b,c,d,e),x),EqQ(Subtract(Times(C2,c,d),Times(b,e)),C0)))),
IIntegrate(4262,Integrate(Times(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),Tan(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))))),x_Symbol),
    Condition(Plus(Simp(Times(CN1,e,Log(Cos(Plus(a,Times(b,x),Times(c,Sqr(x))))),Power(Times(C2,c),CN1)),x),Simp(Dist(Times(Subtract(Times(C2,c,d),Times(b,e)),Power(Times(C2,c),CN1)),Integrate(Tan(Plus(a,Times(b,x),Times(c,Sqr(x)))),x),x),x)),And(FreeQ(List(a,b,c,d,e),x),NeQ(Subtract(Times(C2,c,d),Times(b,e)),C0)))),
IIntegrate(4263,Integrate(Times(Cot(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),Plus(d_DEFAULT,Times(e_DEFAULT,x_))),x_Symbol),
    Condition(Plus(Simp(Times(e,Log(Sin(Plus(a,Times(b,x),Times(c,Sqr(x))))),Power(Times(C2,c),CN1)),x),Simp(Dist(Times(Subtract(Times(C2,c,d),Times(b,e)),Power(Times(C2,c),CN1)),Integrate(Cot(Plus(a,Times(b,x),Times(c,Sqr(x)))),x),x),x)),And(FreeQ(List(a,b,c,d,e),x),NeQ(Subtract(Times(C2,c,d),Times(b,e)),C0)))),
IIntegrate(4264,Integrate(Times(Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT),Power(Tan(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(d,Times(e,x)),m),Power(Tan(Plus(a,Times(b,x),Times(c,Sqr(x)))),n)),x),FreeQ(List(a,b,c,d,e,m,n),x))),
IIntegrate(4265,Integrate(Times(Power(Cot(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),n_DEFAULT),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_)),m_DEFAULT)),x_Symbol),
    Condition(Unintegrable(Times(Power(Plus(d,Times(e,x)),m),Power(Cot(Plus(a,Times(b,x),Times(c,Sqr(x)))),n)),x),FreeQ(List(a,b,c,d,e,m,n),x))),
IIntegrate(4266,Integrate(Power($($s("§csc"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),n_),x_Symbol),
    Condition(Simp(Dist(Negate(Power(d,CN1)),Subst(Integrate(ExpandIntegrand(Power(Plus(C1,Sqr(x)),Plus(Times(C1D2,n),CN1)),x),x),x,Cot(Plus(c,Times(d,x)))),x),x),And(FreeQ(list(c,d),x),IGtQ(Times(C1D2,n),C0)))),
IIntegrate(4267,Integrate(Power(Times($($s("§csc"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT),n_),x_Symbol),
    Condition(Plus(Simp(Times(CN1,b,Cos(Plus(c,Times(d,x))),Power(Times(b,Csc(Plus(c,Times(d,x)))),Plus(n,CN1)),Power(Times(d,Plus(n,CN1)),CN1)),x),Simp(Dist(Times(Sqr(b),Plus(n,CN2),Power(Plus(n,CN1),CN1)),Integrate(Power(Times(b,Csc(Plus(c,Times(d,x)))),Plus(n,CN2)),x),x),x)),And(FreeQ(list(b,c,d),x),GtQ(n,C1),IntegerQ(Times(C2,n))))),
IIntegrate(4268,Integrate(Power(Times($($s("§csc"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT),n_),x_Symbol),
    Condition(Plus(Simp(Times(Cos(Plus(c,Times(d,x))),Power(Times(b,Csc(Plus(c,Times(d,x)))),Plus(n,C1)),Power(Times(b,d,n),CN1)),x),Simp(Dist(Times(Plus(n,C1),Power(Times(Sqr(b),n),CN1)),Integrate(Power(Times(b,Csc(Plus(c,Times(d,x)))),Plus(n,C2)),x),x),x)),And(FreeQ(list(b,c,d),x),LtQ(n,CN1),IntegerQ(Times(C2,n))))),
IIntegrate(4269,Integrate($($s("§csc"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),x_Symbol),
    Condition(Simp(Times(CN1,ArcTanh(Cos(Plus(c,Times(d,x)))),Power(d,CN1)),x),FreeQ(list(c,d),x))),
IIntegrate(4270,Integrate(Power(Times($($s("§csc"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT),n_),x_Symbol),
    Condition(Simp(Dist(Times(Power(Times(b,Csc(Plus(c,Times(d,x)))),n),Power(Sin(Plus(c,Times(d,x))),n)),Integrate(Power(Power(Sin(Plus(c,Times(d,x))),n),CN1),x),x),x),And(FreeQ(list(b,c,d),x),EqQ(Sqr(n),C1D4)))),
IIntegrate(4271,Integrate(Power(Times($($s("§csc"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT),n_),x_Symbol),
    Condition(Simp(Times(Power(Times(b,Csc(Plus(c,Times(d,x)))),Plus(n,CN1)),Dist(Power(Times(Sin(Plus(c,Times(d,x))),Power(b,CN1)),Plus(n,CN1)),Integrate(Power(Power(Times(Sin(Plus(c,Times(d,x))),Power(b,CN1)),n),CN1),x),x)),x),And(FreeQ(List(b,c,d,n),x),Not(IntegerQ(n))))),
IIntegrate(4272,Integrate(Sqr(Plus(Times($($s("§csc"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT),a_)),x_Symbol),
    Condition(Plus(Simp(Times(Sqr(a),x),x),Simp(Dist(Times(C2,a,b),Integrate(Csc(Plus(c,Times(d,x))),x),x),x),Simp(Dist(Sqr(b),Integrate(Sqr(Csc(Plus(c,Times(d,x)))),x),x),x)),FreeQ(List(a,b,c,d),x))),
IIntegrate(4273,Integrate(Sqrt(Plus(Times($($s("§csc"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT),a_)),x_Symbol),
    Condition(Simp(Dist(Times(CN2,b,Power(d,CN1)),Subst(Integrate(Power(Plus(a,Sqr(x)),CN1),x),x,Times(b,Cot(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Csc(Plus(c,Times(d,x))))),CN1D2))),x),x),And(FreeQ(List(a,b,c,d),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0)))),
IIntegrate(4274,Integrate(Power(Plus(Times($($s("§csc"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT),a_),n_),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Sqr(b),Cot(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Csc(Plus(c,Times(d,x))))),Plus(n,CN2)),Power(Times(d,Plus(n,CN1)),CN1)),x),Simp(Dist(Times(a,Power(Plus(n,CN1),CN1)),Integrate(Times(Power(Plus(a,Times(b,Csc(Plus(c,Times(d,x))))),Plus(n,CN2)),Plus(Times(a,Plus(n,CN1)),Times(b,Plus(Times(C3,n),CN4),Csc(Plus(c,Times(d,x)))))),x),x),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0),GtQ(n,C1),IntegerQ(Times(C2,n))))),
IIntegrate(4275,Integrate(Power(Plus(Times($($s("§csc"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT),a_),CN1D2),x_Symbol),
    Condition(Subtract(Simp(Dist(Power(a,CN1),Integrate(Sqrt(Plus(a,Times(b,Csc(Plus(c,Times(d,x)))))),x),x),x),Simp(Dist(Times(b,Power(a,CN1)),Integrate(Times(Csc(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Csc(Plus(c,Times(d,x))))),CN1D2)),x),x),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0)))),
IIntegrate(4276,Integrate(Power(Plus(Times($($s("§csc"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT),a_),n_),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Cot(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Csc(Plus(c,Times(d,x))))),n),Power(Times(d,Plus(Times(C2,n),C1)),CN1)),x),Simp(Dist(Power(Times(Sqr(a),Plus(Times(C2,n),C1)),CN1),Integrate(Times(Power(Plus(a,Times(b,Csc(Plus(c,Times(d,x))))),Plus(n,C1)),Subtract(Times(a,Plus(Times(C2,n),C1)),Times(b,Plus(n,C1),Csc(Plus(c,Times(d,x)))))),x),x),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0),LeQ(n,CN1),IntegerQ(Times(C2,n))))),
IIntegrate(4277,Integrate(Power(Plus(Times($($s("§csc"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT),a_),n_),x_Symbol),
    Condition(Simp(Dist(Times(Power(a,n),Cot(Plus(c,Times(d,x))),Power(Times(d,Sqrt(Plus(C1,Csc(Plus(c,Times(d,x))))),Sqrt(Subtract(C1,Csc(Plus(c,Times(d,x)))))),CN1)),Subst(Integrate(Times(Power(Plus(C1,Times(b,x,Power(a,CN1))),Plus(n,CN1D2)),Power(Times(x,Sqrt(Subtract(C1,Times(b,x,Power(a,CN1))))),CN1)),x),x,Csc(Plus(c,Times(d,x)))),x),x),And(FreeQ(List(a,b,c,d,n),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0),Not(IntegerQ(Times(C2,n))),GtQ(a,C0)))),
IIntegrate(4278,Integrate(Power(Plus(Times($($s("§csc"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT),a_),n_),x_Symbol),
    Condition(Simp(Dist(Times(Power(a,IntPart(n)),Power(Plus(a,Times(b,Csc(Plus(c,Times(d,x))))),FracPart(n)),Power(Power(Plus(C1,Times(b,Power(a,CN1),Csc(Plus(c,Times(d,x))))),FracPart(n)),CN1)),Integrate(Power(Plus(C1,Times(b,Power(a,CN1),Csc(Plus(c,Times(d,x))))),n),x),x),x),And(FreeQ(List(a,b,c,d,n),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0),Not(IntegerQ(Times(C2,n))),Not(GtQ(a,C0))))),
IIntegrate(4279,Integrate(Sqrt(Plus(Times($($s("§csc"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT),a_)),x_Symbol),
    Condition(Simp(Times(C2,Plus(a,Times(b,Csc(Plus(c,Times(d,x))))),Power(Times(d,Rt(Plus(a,b),C2),Cot(Plus(c,Times(d,x)))),CN1),Sqrt(Times(b,Plus(C1,Csc(Plus(c,Times(d,x)))),Power(Plus(a,Times(b,Csc(Plus(c,Times(d,x))))),CN1))),Sqrt(Times(CN1,b,Subtract(C1,Csc(Plus(c,Times(d,x)))),Power(Plus(a,Times(b,Csc(Plus(c,Times(d,x))))),CN1))),EllipticPi(Times(a,Power(Plus(a,b),CN1)),ArcSin(Times(Rt(Plus(a,b),C2),Power(Plus(a,Times(b,Csc(Plus(c,Times(d,x))))),CN1D2))),Times(Subtract(a,b),Power(Plus(a,b),CN1)))),x),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(a),Sqr(b)),C0)))),
IIntegrate(4280,Integrate(Power(Plus(Times($($s("§csc"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT),a_),QQ(3L,2L)),x_Symbol),
    Condition(Plus(Integrate(Times(Plus(Sqr(a),Times(b,Subtract(Times(C2,a),b),Csc(Plus(c,Times(d,x))))),Power(Plus(a,Times(b,Csc(Plus(c,Times(d,x))))),CN1D2)),x),Simp(Dist(Sqr(b),Integrate(Times(Csc(Plus(c,Times(d,x))),Plus(C1,Csc(Plus(c,Times(d,x)))),Power(Plus(a,Times(b,Csc(Plus(c,Times(d,x))))),CN1D2)),x),x),x)),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(a),Sqr(b)),C0))))
  );
}
