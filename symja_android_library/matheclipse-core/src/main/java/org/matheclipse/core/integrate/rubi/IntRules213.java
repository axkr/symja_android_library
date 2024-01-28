package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctions.*;
import org.matheclipse.core.interfaces.IAST;

/** 
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class IntRules213 { 
  public static IAST RULES = List( 
IIntegrate(4261,Integrate(Sqrt(Plus(Times($($s("§csc"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT),a_)),x_Symbol),
    Condition(Simp(Star(Times(CN2,b,Power(d,CN1)),Subst(Integrate(Power(Plus(a,Sqr(x)),CN1),x),x,Times(b,Cot(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Csc(Plus(c,Times(d,x))))),CN1D2)))),x),And(FreeQ(List(a,b,c,d),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0)))),
IIntegrate(4262,Integrate(Power(Plus(Times($($s("§csc"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT),a_),n_),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Sqr(b),Cot(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Csc(Plus(c,Times(d,x))))),Subtract(n,C2)),Power(Times(d,Subtract(n,C1)),CN1)),x),Simp(Star(Times(a,Power(Subtract(n,C1),CN1)),Integrate(Times(Power(Plus(a,Times(b,Csc(Plus(c,Times(d,x))))),Subtract(n,C2)),Plus(Times(a,Subtract(n,C1)),Times(b,Subtract(Times(C3,n),C4),Csc(Plus(c,Times(d,x)))))),x)),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0),GtQ(n,C1),IntegerQ(Times(C2,n))))),
IIntegrate(4263,Integrate(Power(Plus(Times($($s("§csc"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT),a_),CN1D2),x_Symbol),
    Condition(Subtract(Simp(Star(Power(a,CN1),Integrate(Sqrt(Plus(a,Times(b,Csc(Plus(c,Times(d,x)))))),x)),x),Simp(Star(Times(b,Power(a,CN1)),Integrate(Times(Csc(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Csc(Plus(c,Times(d,x))))),CN1D2)),x)),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0)))),
IIntegrate(4264,Integrate(Power(Plus(Times($($s("§csc"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT),a_),n_),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Cot(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Csc(Plus(c,Times(d,x))))),n),Power(Times(d,Plus(Times(C2,n),C1)),CN1)),x),Simp(Star(Power(Times(Sqr(a),Plus(Times(C2,n),C1)),CN1),Integrate(Times(Power(Plus(a,Times(b,Csc(Plus(c,Times(d,x))))),Plus(n,C1)),Subtract(Times(a,Plus(Times(C2,n),C1)),Times(b,Plus(n,C1),Csc(Plus(c,Times(d,x)))))),x)),x)),And(FreeQ(List(a,b,c,d),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0),LeQ(n,CN1),IntegerQ(Times(C2,n))))),
IIntegrate(4265,Integrate(Power(Plus(Times($($s("§csc"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT),a_),n_),x_Symbol),
    Condition(Simp(Star(Times(Power(a,n),Cot(Plus(c,Times(d,x))),Power(Times(d,Sqrt(Plus(C1,Csc(Plus(c,Times(d,x))))),Sqrt(Subtract(C1,Csc(Plus(c,Times(d,x)))))),CN1)),Subst(Integrate(Times(Power(Plus(C1,Times(b,x,Power(a,CN1))),Subtract(n,C1D2)),Power(Times(x,Sqrt(Subtract(C1,Times(b,x,Power(a,CN1))))),CN1)),x),x,Csc(Plus(c,Times(d,x))))),x),And(FreeQ(List(a,b,c,d,n),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0),Not(IntegerQ(Times(C2,n))),GtQ(a,C0)))),
IIntegrate(4266,Integrate(Power(Plus(Times($($s("§csc"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT),a_),n_),x_Symbol),
    Condition(Simp(Star(Times(Power(a,IntPart(n)),Power(Plus(a,Times(b,Csc(Plus(c,Times(d,x))))),FracPart(n)),Power(Power(Plus(C1,Times(b,Power(a,CN1),Csc(Plus(c,Times(d,x))))),FracPart(n)),CN1)),Integrate(Power(Plus(C1,Times(b,Power(a,CN1),Csc(Plus(c,Times(d,x))))),n),x)),x),And(FreeQ(List(a,b,c,d,n),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0),Not(IntegerQ(Times(C2,n))),Not(GtQ(a,C0))))),
IIntegrate(4267,Integrate(Sqrt(Plus(Times($($s("§csc"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT),a_)),x_Symbol),
    Condition(Simp(Times(C2,Plus(a,Times(b,Csc(Plus(c,Times(d,x))))),Power(Times(d,Rt(Plus(a,b),C2),Cot(Plus(c,Times(d,x)))),CN1),Sqrt(Times(b,Plus(C1,Csc(Plus(c,Times(d,x)))),Power(Plus(a,Times(b,Csc(Plus(c,Times(d,x))))),CN1))),Sqrt(Times(CN1,b,Subtract(C1,Csc(Plus(c,Times(d,x)))),Power(Plus(a,Times(b,Csc(Plus(c,Times(d,x))))),CN1))),EllipticPi(Times(a,Power(Plus(a,b),CN1)),ArcSin(Times(Rt(Plus(a,b),C2),Power(Plus(a,Times(b,Csc(Plus(c,Times(d,x))))),CN1D2))),Times(Subtract(a,b),Power(Plus(a,b),CN1)))),x),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(a),Sqr(b)),C0)))),
IIntegrate(4268,Integrate(Power(Plus(Times($($s("§csc"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT),a_),QQ(3L,2L)),x_Symbol),
    Condition(Plus(Integrate(Times(Plus(Sqr(a),Times(b,Subtract(Times(C2,a),b),Csc(Plus(c,Times(d,x))))),Power(Plus(a,Times(b,Csc(Plus(c,Times(d,x))))),CN1D2)),x),Simp(Star(Sqr(b),Integrate(Times(Csc(Plus(c,Times(d,x))),Plus(C1,Csc(Plus(c,Times(d,x)))),Power(Plus(a,Times(b,Csc(Plus(c,Times(d,x))))),CN1D2)),x)),x)),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(a),Sqr(b)),C0)))),
IIntegrate(4269,Integrate(Power(Plus(Times($($s("§csc"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT),a_),n_),x_Symbol),
    Condition(Plus(Simp(Times(CN1,Sqr(b),Cot(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Csc(Plus(c,Times(d,x))))),Subtract(n,C2)),Power(Times(d,Subtract(n,C1)),CN1)),x),Simp(Star(Power(Subtract(n,C1),CN1),Integrate(Times(Power(Plus(a,Times(b,Csc(Plus(c,Times(d,x))))),Subtract(n,C3)),Simp(Plus(Times(Power(a,C3),Subtract(n,C1)),Times(b,Plus(Times(Sqr(b),Subtract(n,C2)),Times(C3,Sqr(a),Subtract(n,C1))),Csc(Plus(c,Times(d,x)))),Times(a,Sqr(b),Subtract(Times(C3,n),C4),Sqr(Csc(Plus(c,Times(d,x)))))),x)),x)),x)),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(a),Sqr(b)),C0),GtQ(n,C2),IntegerQ(Times(C2,n))))),
IIntegrate(4270,Integrate(Power(Plus(Times($($s("§csc"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT),a_),CN1),x_Symbol),
    Condition(Subtract(Simp(Times(x,Power(a,CN1)),x),Simp(Star(Power(a,CN1),Integrate(Power(Plus(C1,Times(a,Power(b,CN1),Sin(Plus(c,Times(d,x))))),CN1),x)),x)),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(a),Sqr(b)),C0)))),
IIntegrate(4271,Integrate(Power(Plus(Times($($s("§csc"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT),a_),CN1D2),x_Symbol),
    Condition(Simp(Times(C2,Rt(Plus(a,b),C2),Power(Times(a,d,Cot(Plus(c,Times(d,x)))),CN1),Sqrt(Times(b,Subtract(C1,Csc(Plus(c,Times(d,x)))),Power(Plus(a,b),CN1))),Sqrt(Times(CN1,b,Plus(C1,Csc(Plus(c,Times(d,x)))),Power(Subtract(a,b),CN1))),EllipticPi(Times(Plus(a,b),Power(a,CN1)),ArcSin(Times(Sqrt(Plus(a,Times(b,Csc(Plus(c,Times(d,x)))))),Power(Rt(Plus(a,b),C2),CN1))),Times(Plus(a,b),Power(Subtract(a,b),CN1)))),x),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(a),Sqr(b)),C0)))),
IIntegrate(4272,Integrate(Power(Plus(Times($($s("§csc"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT),a_),n_),x_Symbol),
    Condition(Plus(Simp(Times(Sqr(b),Cot(Plus(c,Times(d,x))),Power(Plus(a,Times(b,Csc(Plus(c,Times(d,x))))),Plus(n,C1)),Power(Times(a,d,Plus(n,C1),Subtract(Sqr(a),Sqr(b))),CN1)),x),Simp(Star(Power(Times(a,Plus(n,C1),Subtract(Sqr(a),Sqr(b))),CN1),Integrate(Times(Power(Plus(a,Times(b,Csc(Plus(c,Times(d,x))))),Plus(n,C1)),Simp(Plus(Times(Subtract(Sqr(a),Sqr(b)),Plus(n,C1)),Times(CN1,a,b,Plus(n,C1),Csc(Plus(c,Times(d,x)))),Times(Sqr(b),Plus(n,C2),Sqr(Csc(Plus(c,Times(d,x)))))),x)),x)),x)),And(FreeQ(List(a,b,c,d),x),NeQ(Subtract(Sqr(a),Sqr(b)),C0),LtQ(n,CN1),IntegerQ(Times(C2,n))))),
IIntegrate(4273,Integrate(Power(Plus(Times($($s("§csc"),Plus(c_DEFAULT,Times(d_DEFAULT,x_))),b_DEFAULT),a_),n_),x_Symbol),
    Condition(Unintegrable(Power(Plus(a,Times(b,Csc(Plus(c,Times(d,x))))),n),x),And(FreeQ(List(a,b,c,d,n),x),NeQ(Subtract(Sqr(a),Sqr(b)),C0),Not(IntegerQ(Times(C2,n)))))),
IIntegrate(4274,Integrate(Times(Power(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),d_DEFAULT),n_DEFAULT),Plus(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT),a_)),x_Symbol),
    Condition(Plus(Simp(Star(a,Integrate(Power(Times(d,Csc(Plus(e,Times(f,x)))),n),x)),x),Simp(Star(Times(b,Power(d,CN1)),Integrate(Power(Times(d,Csc(Plus(e,Times(f,x)))),Plus(n,C1)),x)),x)),FreeQ(List(a,b,d,e,f,n),x))),
IIntegrate(4275,Integrate(Times(Power(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),d_DEFAULT),n_DEFAULT),Sqr(Plus(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT),a_))),x_Symbol),
    Condition(Plus(Simp(Star(Times(C2,a,b,Power(d,CN1)),Integrate(Power(Times(d,Csc(Plus(e,Times(f,x)))),Plus(n,C1)),x)),x),Integrate(Times(Power(Times(d,Csc(Plus(e,Times(f,x)))),n),Plus(Sqr(a),Times(Sqr(b),Sqr(Csc(Plus(e,Times(f,x))))))),x)),FreeQ(List(a,b,d,e,f,n),x))),
IIntegrate(4276,Integrate(Times(Sqr($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_)))),Power(Plus(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT),a_),CN1)),x_Symbol),
    Condition(Subtract(Simp(Star(Power(b,CN1),Integrate(Csc(Plus(e,Times(f,x))),x)),x),Simp(Star(Times(a,Power(b,CN1)),Integrate(Times(Csc(Plus(e,Times(f,x))),Power(Plus(a,Times(b,Csc(Plus(e,Times(f,x))))),CN1)),x)),x)),FreeQ(List(a,b,e,f),x))),
IIntegrate(4277,Integrate(Times(Power($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),C3),Power(Plus(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT),a_),CN1)),x_Symbol),
    Condition(Subtract(Simp(Times(CN1,Cot(Plus(e,Times(f,x))),Power(Times(b,f),CN1)),x),Simp(Star(Times(a,Power(b,CN1)),Integrate(Times(Sqr(Csc(Plus(e,Times(f,x)))),Power(Plus(a,Times(b,Csc(Plus(e,Times(f,x))))),CN1)),x)),x)),FreeQ(List(a,b,e,f),x))),
IIntegrate(4278,Integrate(Times(Power(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),d_DEFAULT),n_DEFAULT),Power(Plus(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT),a_),m_)),x_Symbol),
    Condition(Integrate(ExpandTrig(Times(Power(Plus(a,Times(b,$($s("§csc"),Plus(e,Times(f,x))))),m),Power(Times(d,$($s("§csc"),Plus(e,Times(f,x)))),n)),x),x),And(FreeQ(List(a,b,d,e,f,m,n),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0),IGtQ(m,C0),RationalQ(n)))),
IIntegrate(4279,Integrate(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),Sqrt(Plus(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT),a_))),x_Symbol),
    Condition(Simp(Times(CN2,b,Cot(Plus(e,Times(f,x))),Power(Times(f,Sqrt(Plus(a,Times(b,Csc(Plus(e,Times(f,x))))))),CN1)),x),And(FreeQ(List(a,b,e,f),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0)))),
IIntegrate(4280,Integrate(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),Power(Plus(Times($($s("§csc"),Plus(e_DEFAULT,Times(f_DEFAULT,x_))),b_DEFAULT),a_),m_)),x_Symbol),
    Condition(Plus(Simp(Times(CN1,b,Cot(Plus(e,Times(f,x))),Power(Plus(a,Times(b,Csc(Plus(e,Times(f,x))))),Subtract(m,C1)),Power(Times(f,m),CN1)),x),Simp(Star(Times(a,Subtract(Times(C2,m),C1),Power(m,CN1)),Integrate(Times(Csc(Plus(e,Times(f,x))),Power(Plus(a,Times(b,Csc(Plus(e,Times(f,x))))),Subtract(m,C1))),x)),x)),And(FreeQ(List(a,b,e,f),x),EqQ(Subtract(Sqr(a),Sqr(b)),C0),GtQ(m,C1D2),IntegerQ(Times(C2,m)))))
  );
}
