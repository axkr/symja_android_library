package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$;
import static org.matheclipse.core.expression.F.$s;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.C0;
import static org.matheclipse.core.expression.F.C1;
import static org.matheclipse.core.expression.F.C1D2;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.C3;
import static org.matheclipse.core.expression.F.C4;
import static org.matheclipse.core.expression.F.C5;
import static org.matheclipse.core.expression.F.CN1;
import static org.matheclipse.core.expression.F.CN1D2;
import static org.matheclipse.core.expression.F.CN2;
import static org.matheclipse.core.expression.F.CN4;
import static org.matheclipse.core.expression.F.Condition;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.IIntegrate;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Integrate;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Negate;
import static org.matheclipse.core.expression.F.Not;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Plus;
import static org.matheclipse.core.expression.F.Power;
import static org.matheclipse.core.expression.F.Set;
import static org.matheclipse.core.expression.F.Sqr;
import static org.matheclipse.core.expression.F.Sqrt;
import static org.matheclipse.core.expression.F.Subtract;
import static org.matheclipse.core.expression.F.Times;
import static org.matheclipse.core.expression.F.With;
import static org.matheclipse.core.expression.F.a_;
import static org.matheclipse.core.expression.F.a_DEFAULT;
import static org.matheclipse.core.expression.F.b_DEFAULT;
import static org.matheclipse.core.expression.F.c_DEFAULT;
import static org.matheclipse.core.expression.F.d_;
import static org.matheclipse.core.expression.F.d_DEFAULT;
import static org.matheclipse.core.expression.F.e_DEFAULT;
import static org.matheclipse.core.expression.F.f_DEFAULT;
import static org.matheclipse.core.expression.F.list;
import static org.matheclipse.core.expression.F.p_;
import static org.matheclipse.core.expression.F.q_;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.a;
import static org.matheclipse.core.expression.S.b;
import static org.matheclipse.core.expression.S.c;
import static org.matheclipse.core.expression.S.d;
import static org.matheclipse.core.expression.S.e;
import static org.matheclipse.core.expression.S.f;
import static org.matheclipse.core.expression.S.p;
import static org.matheclipse.core.expression.S.q;
import static org.matheclipse.core.expression.S.r;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.EqQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ExpandIntegrand;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.GtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ILtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.LtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NegQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.NiceSqrtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.PosQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Rt;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Simp;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Star;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.Subst;
import org.matheclipse.core.interfaces.IAST;

/** 
 * IndefiniteIntegrationRules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class IntRules65 { 
  public static IAST RULES = List( 
IIntegrate(1301,Integrate(Times(Power(Plus(a_DEFAULT,Times(c_DEFAULT,Sqr(x_))),p_),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_),Times(f_DEFAULT,Sqr(x_))),q_)),x_Symbol),
    Condition(With(list(Set(r,Rt(Times(CN1,a,c),C2))),Condition(Simp(Star(Power(Power(c,p),CN1),Integrate(ExpandIntegrand(Times(Power(Plus(Negate(r),Times(c,x)),p),Power(Plus(r,Times(c,x)),p),Power(Plus(d,Times(e,x),Times(f,Sqr(x))),q)),x),x)),x),Or(EqQ(p,CN1),Not($($s("§fractionalpowerfactorq"),r))))),And(FreeQ(List(a,c,d,e,f),x),ILtQ(p,C0),IntegerQ(q),NiceSqrtQ(Times(CN1,a,c))))),
IIntegrate(1302,Integrate(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_),Times(f_DEFAULT,Sqr(x_))),q_)),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(b,Times(C2,c,x)),Power(Plus(a,Times(b,x),Times(c,Sqr(x))),Plus(p,C1)),Power(Plus(d,Times(e,x),Times(f,Sqr(x))),q),Power(Times(Subtract(Sqr(b),Times(C4,a,c)),Plus(p,C1)),CN1)),x),Simp(Star(Power(Times(Subtract(Sqr(b),Times(C4,a,c)),Plus(p,C1)),CN1),Integrate(Times(Power(Plus(a,Times(b,x),Times(c,Sqr(x))),Plus(p,C1)),Power(Plus(d,Times(e,x),Times(f,Sqr(x))),Subtract(q,C1)),Simp(Plus(Times(C2,c,d,Plus(Times(C2,p),C3)),Times(b,e,q),Times(Plus(Times(C2,b,f,q),Times(C2,c,e,Plus(Times(C2,p),q,C3))),x),Times(C2,c,f,Plus(Times(C2,p),Times(C2,q),C3),Sqr(x))),x)),x)),x)),And(FreeQ(List(a,b,c,d,e,f),x),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),NeQ(Subtract(Sqr(e),Times(C4,d,f)),C0),LtQ(p,CN1),GtQ(q,C0),Not(IGtQ(q,C0))))),
IIntegrate(1303,Integrate(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_),Power(Plus(d_DEFAULT,Times(f_DEFAULT,Sqr(x_))),q_)),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(b,Times(C2,c,x)),Power(Plus(a,Times(b,x),Times(c,Sqr(x))),Plus(p,C1)),Power(Plus(d,Times(f,Sqr(x))),q),Power(Times(Subtract(Sqr(b),Times(C4,a,c)),Plus(p,C1)),CN1)),x),Simp(Star(Power(Times(Subtract(Sqr(b),Times(C4,a,c)),Plus(p,C1)),CN1),Integrate(Times(Power(Plus(a,Times(b,x),Times(c,Sqr(x))),Plus(p,C1)),Power(Plus(d,Times(f,Sqr(x))),Subtract(q,C1)),Simp(Plus(Times(C2,c,d,Plus(Times(C2,p),C3)),Times(C2,b,f,q,x),Times(C2,c,f,Plus(Times(C2,p),Times(C2,q),C3),Sqr(x))),x)),x)),x)),And(FreeQ(List(a,b,c,d,f),x),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),LtQ(p,CN1),GtQ(q,C0),Not(IGtQ(q,C0))))),
IIntegrate(1304,Integrate(Times(Power(Plus(a_DEFAULT,Times(c_DEFAULT,Sqr(x_))),p_),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_),Times(f_DEFAULT,Sqr(x_))),q_)),x_Symbol),
          Condition(
              Subtract(
                  Simp(Times(C2, c, x, Power(Plus(a, Times(c, Sqr(x))), Plus(p, C1)),
                      Power(Plus(d, Times(e, x), Times(f, Sqr(x))), q),
                      Power(Times(CN4, a, c, Plus(p, C1)), CN1)), x),
                  Simp(Star(Power(Times(CN4, a, c, Plus(p, C1)), CN1), Integrate(Times(
                      Power(Plus(a, Times(c, Sqr(x))), Plus(p, C1)),
                      Power(Plus(d, Times(e, x), Times(f, Sqr(x))), Subtract(q, C1)),
                      Simp(Plus(Times(C2, c, d, Plus(Times(C2, p), C3)),
                          Times(C2, c, e, Plus(Times(C2, p), q, C3), x),
                          Times(C2, c, f, Plus(Times(C2, p), Times(C2, q), C3), Sqr(x))), x)),
                      x)), x)),
              And(FreeQ(List(a, c, d, e, f), x), NeQ(Subtract(Sqr(e), Times(C4, d, f)), C0),
                  LtQ(p, CN1), GtQ(q, C0), Not(IGtQ(q, C0))))),
IIntegrate(1305,Integrate(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_),Times(f_DEFAULT,Sqr(x_))),q_)),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(Times(C2,a,Sqr(c),e),Times(CN1,Sqr(b),c,e),Times(Power(b,C3),f),Times(b,c,Subtract(Times(c,d),Times(C3,a,f))),Times(c,Subtract(Plus(Times(C2,Sqr(c),d),Times(Sqr(b),f)),Times(c,Plus(Times(b,e),Times(C2,a,f)))),x)),Power(Plus(a,Times(b,x),Times(c,Sqr(x))),Plus(p,C1)),Power(Plus(d,Times(e,x),Times(f,Sqr(x))),Plus(q,C1)),Power(Times(Subtract(Sqr(b),Times(C4,a,c)),Subtract(Sqr(Subtract(Times(c,d),Times(a,f))),Times(Subtract(Times(b,d),Times(a,e)),Subtract(Times(c,e),Times(b,f)))),Plus(p,C1)),CN1)),x),Simp(Star(Power(Times(Subtract(Sqr(b),Times(C4,a,c)),Subtract(Sqr(Subtract(Times(c,d),Times(a,f))),Times(Subtract(Times(b,d),Times(a,e)),Subtract(Times(c,e),Times(b,f)))),Plus(p,C1)),CN1),Integrate(Times(Power(Plus(a,Times(b,x),Times(c,Sqr(x))),Plus(p,C1)),Power(Plus(d,Times(e,x),Times(f,Sqr(x))),q),Simp(Plus(Times(C2,c,Subtract(Sqr(Subtract(Times(c,d),Times(a,f))),Times(Subtract(Times(b,d),Times(a,e)),Subtract(Times(c,e),Times(b,f)))),Plus(p,C1)),Times(CN1,Subtract(Plus(Times(C2,Sqr(c),d),Times(Sqr(b),f)),Times(c,Plus(Times(b,e),Times(C2,a,f)))),Subtract(Times(a,f,Plus(p,C1)),Times(c,d,Plus(p,C2)))),Times(CN1,e,Subtract(Subtract(Subtract(Times(Sqr(b),c,e),Times(C2,a,Sqr(c),e)),Times(Power(b,C3),f)),Times(b,c,Subtract(Times(c,d),Times(C3,a,f)))),Plus(p,q,C2)),Times(Subtract(Times(C2,f,Plus(Times(C2,a,Sqr(c),e),Times(CN1,Sqr(b),c,e),Times(Power(b,C3),f),Times(b,c,Subtract(Times(c,d),Times(C3,a,f)))),Plus(p,q,C2)),Times(Subtract(Plus(Times(C2,Sqr(c),d),Times(Sqr(b),f)),Times(c,Plus(Times(b,e),Times(C2,a,f)))),Subtract(Times(b,f,Plus(p,C1)),Times(c,e,Plus(Times(C2,p),q,C4))))),x),Times(c,f,Subtract(Plus(Times(C2,Sqr(c),d),Times(Sqr(b),f)),Times(c,Plus(Times(b,e),Times(C2,a,f)))),Plus(Times(C2,p),Times(C2,q),C5),Sqr(x))),x)),x)),x)),And(FreeQ(List(a,b,c,d,e,f,q),x),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),NeQ(Subtract(Sqr(e),Times(C4,d,f)),C0),LtQ(p,CN1),NeQ(Subtract(Sqr(Subtract(Times(c,d),Times(a,f))),Times(Subtract(Times(b,d),Times(a,e)),Subtract(Times(c,e),Times(b,f)))),C0),Not(And(Not(IntegerQ(p)),ILtQ(q,CN1))),Not(IGtQ(q,C0))))),
IIntegrate(1306,Integrate(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_),Power(Plus(d_DEFAULT,Times(f_DEFAULT,Sqr(x_))),q_)),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(Times(Power(b,C3),f),Times(b,c,Subtract(Times(c,d),Times(C3,a,f))),Times(c,Subtract(Plus(Times(C2,Sqr(c),d),Times(Sqr(b),f)),Times(c,C2,a,f)),x)),Power(Plus(a,Times(b,x),Times(c,Sqr(x))),Plus(p,C1)),Power(Plus(d,Times(f,Sqr(x))),Plus(q,C1)),Power(Times(Subtract(Sqr(b),Times(C4,a,c)),Plus(Times(Sqr(b),d,f),Sqr(Subtract(Times(c,d),Times(a,f)))),Plus(p,C1)),CN1)),x),Simp(Star(Power(Times(Subtract(Sqr(b),Times(C4,a,c)),Plus(Times(Sqr(b),d,f),Sqr(Subtract(Times(c,d),Times(a,f)))),Plus(p,C1)),CN1),Integrate(Times(Power(Plus(a,Times(b,x),Times(c,Sqr(x))),Plus(p,C1)),Power(Plus(d,Times(f,Sqr(x))),q),Simp(Plus(Times(C2,c,Plus(Times(Sqr(b),d,f),Sqr(Subtract(Times(c,d),Times(a,f)))),Plus(p,C1)),Times(CN1,Subtract(Plus(Times(C2,Sqr(c),d),Times(Sqr(b),f)),Times(c,C2,a,f)),Subtract(Times(a,f,Plus(p,C1)),Times(c,d,Plus(p,C2)))),Times(Subtract(Times(C2,f,Plus(Times(Power(b,C3),f),Times(b,c,Subtract(Times(c,d),Times(C3,a,f)))),Plus(p,q,C2)),Times(Subtract(Plus(Times(C2,Sqr(c),d),Times(Sqr(b),f)),Times(c,C2,a,f)),b,f,Plus(p,C1))),x),Times(c,f,Subtract(Plus(Times(C2,Sqr(c),d),Times(Sqr(b),f)),Times(c,C2,a,f)),Plus(Times(C2,p),Times(C2,q),C5),Sqr(x))),x)),x)),x)),And(FreeQ(List(a,b,c,d,f,q),x),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),LtQ(p,CN1),NeQ(Plus(Times(Sqr(b),d,f),Sqr(Subtract(Times(c,d),Times(a,f)))),C0),Not(And(Not(IntegerQ(p)),ILtQ(q,CN1))),Not(IGtQ(q,C0))))),
IIntegrate(1307,Integrate(Times(Power(Plus(a_DEFAULT,Times(c_DEFAULT,Sqr(x_))),p_),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_),Times(f_DEFAULT,Sqr(x_))),q_)),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(Times(C2,a,Sqr(c),e),Times(c,Subtract(Times(C2,Sqr(c),d),Times(c,C2,a,f)),x)),Power(Plus(a,Times(c,Sqr(x))),Plus(p,C1)),Power(Plus(d,Times(e,x),Times(f,Sqr(x))),Plus(q,C1)),Power(Times(CN4,a,c,Plus(Times(a,c,Sqr(e)),Sqr(Subtract(Times(c,d),Times(a,f)))),Plus(p,C1)),CN1)),x),Simp(Star(Power(Times(CN4,a,c,Plus(Times(a,c,Sqr(e)),Sqr(Subtract(Times(c,d),Times(a,f)))),Plus(p,C1)),CN1),Integrate(Times(Power(Plus(a,Times(c,Sqr(x))),Plus(p,C1)),Power(Plus(d,Times(e,x),Times(f,Sqr(x))),q),Simp(Plus(Times(C2,c,Subtract(Sqr(Subtract(Times(c,d),Times(a,f))),Times(CN1,a,e,c,e)),Plus(p,C1)),Times(CN1,Subtract(Times(C2,Sqr(c),d),Times(c,C2,a,f)),Subtract(Times(a,f,Plus(p,C1)),Times(c,d,Plus(p,C2)))),Times(CN1,e,CN2,a,Sqr(c),e,Plus(p,q,C2)),Times(Subtract(Times(C2,f,C2,a,Sqr(c),e,Plus(p,q,C2)),Times(Subtract(Times(C2,Sqr(c),d),Times(c,C2,a,f)),CN1,c,e,Plus(Times(C2,p),q,C4))),x),Times(c,f,Subtract(Times(C2,Sqr(c),d),Times(c,C2,a,f)),Plus(Times(C2,p),Times(C2,q),C5),Sqr(x))),x)),x)),x)),And(FreeQ(List(a,c,d,e,f,q),x),NeQ(Subtract(Sqr(e),Times(C4,d,f)),C0),LtQ(p,CN1),NeQ(Plus(Times(a,c,Sqr(e)),Sqr(Subtract(Times(c,d),Times(a,f)))),C0),Not(And(Not(IntegerQ(p)),ILtQ(q,CN1))),Not(IGtQ(q,C0))))),
IIntegrate(1308,Integrate(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_),Times(f_DEFAULT,Sqr(x_))),q_)),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(Times(b,f,Plus(Times(C3,p),Times(C2,q))),Times(CN1,c,e,Plus(Times(C2,p),q)),Times(C2,c,f,Plus(p,q),x)),Power(Plus(a,Times(b,x),Times(c,Sqr(x))),Subtract(p,C1)),Power(Plus(d,Times(e,x),Times(f,Sqr(x))),Plus(q,C1)),Power(Times(C2,Sqr(f),Plus(p,q),Plus(Times(C2,p),Times(C2,q),C1)),CN1)),x),Simp(Star(Power(Times(C2,Sqr(f),Plus(p,q),Plus(Times(C2,p),Times(C2,q),C1)),CN1),Integrate(Times(Power(Plus(a,Times(b,x),Times(c,Sqr(x))),Subtract(p,C2)),Power(Plus(d,Times(e,x),Times(f,Sqr(x))),q),Simp(Plus(Times(Subtract(Times(b,d),Times(a,e)),Subtract(Times(c,e),Times(b,f)),Subtract(C1,p),Plus(Times(C2,p),q)),Times(CN1,Plus(p,q),Subtract(Times(Sqr(b),d,f,Subtract(C1,p)),Times(a,Plus(Times(f,Subtract(Times(b,e),Times(C2,a,f)),Plus(Times(C2,p),Times(C2,q),C1)),Times(c,Subtract(Times(C2,d,f),Times(Sqr(e),Plus(Times(C2,p),q)))))))),Times(Subtract(Times(C2,Subtract(Times(c,d),Times(a,f)),Subtract(Times(c,e),Times(b,f)),Subtract(C1,p),Plus(Times(C2,p),q)),Times(Plus(p,q),Plus(Times(Subtract(Sqr(b),Times(C4,a,c)),e,f,Subtract(C1,p)),Times(b,Plus(Times(c,Subtract(Sqr(e),Times(C4,d,f)),Plus(Times(C2,p),q)),Times(f,Plus(Times(C2,c,d),Times(CN1,b,e),Times(C2,a,f)),Plus(Times(C2,p),Times(C2,q),C1))))))),x),Times(Plus(Times(Sqr(Subtract(Times(c,e),Times(b,f))),Subtract(C1,p),p),Times(c,Plus(p,q),Subtract(Times(f,Subtract(Times(b,e),Times(C2,a,f)),Subtract(Plus(Times(C4,p),Times(C2,q)),C1)),Times(c,Plus(Times(C2,d,f,Subtract(C1,Times(C2,p))),Times(Sqr(e),Subtract(Plus(Times(C3,p),q),C1))))))),Sqr(x))),x)),x)),x)),And(FreeQ(List(a,b,c,d,e,f,q),x),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),NeQ(Subtract(Sqr(e),Times(C4,d,f)),C0),GtQ(p,C1),NeQ(Plus(p,q),C0),NeQ(Plus(Times(C2,p),Times(C2,q),C1),C0),Not(IGtQ(p,C0)),Not(IGtQ(q,C0))))),
IIntegrate(1309,Integrate(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),p_),Power(Plus(d_DEFAULT,Times(f_DEFAULT,Sqr(x_))),q_)),x_Symbol),
    Condition(Subtract(Simp(Times(Plus(Times(b,Plus(Times(C3,p),Times(C2,q))),Times(C2,c,Plus(p,q),x)),Power(Plus(a,Times(b,x),Times(c,Sqr(x))),Subtract(p,C1)),Power(Plus(d,Times(f,Sqr(x))),Plus(q,C1)),Power(Times(C2,f,Plus(p,q),Plus(Times(C2,p),Times(C2,q),C1)),CN1)),x),Simp(Star(Power(Times(C2,f,Plus(p,q),Plus(Times(C2,p),Times(C2,q),C1)),CN1),Integrate(Times(Power(Plus(a,Times(b,x),Times(c,Sqr(x))),Subtract(p,C2)),Power(Plus(d,Times(f,Sqr(x))),q),Simp(Plus(Times(Sqr(b),d,Subtract(p,C1),Plus(Times(C2,p),q)),Times(CN1,Plus(p,q),Subtract(Times(Sqr(b),d,Subtract(C1,p)),Times(C2,a,Subtract(Times(c,d),Times(a,f,Plus(Times(C2,p),Times(C2,q),C1)))))),Times(CN1,Subtract(Times(C2,b,Subtract(Times(c,d),Times(a,f)),Subtract(C1,p),Plus(Times(C2,p),q)),Times(C2,Plus(p,q),b,Subtract(Times(C2,c,d,Plus(Times(C2,p),q)),Times(Plus(Times(c,d),Times(a,f)),Plus(Times(C2,p),Times(C2,q),C1))))),x),Times(Plus(Times(Sqr(b),f,p,Subtract(C1,p)),Times(C2,c,Plus(p,q),Subtract(Times(c,d,Subtract(Times(C2,p),C1)),Times(a,f,Subtract(Plus(Times(C4,p),Times(C2,q)),C1))))),Sqr(x))),x)),x)),x)),And(FreeQ(List(a,b,c,d,f,q),x),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),GtQ(p,C1),NeQ(Plus(p,q),C0),NeQ(Plus(Times(C2,p),Times(C2,q),C1),C0),Not(IGtQ(p,C0)),Not(IGtQ(q,C0))))),
IIntegrate(1310,Integrate(Times(Power(Plus(a_DEFAULT,Times(c_DEFAULT,Sqr(x_))),p_),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_),Times(f_DEFAULT,Sqr(x_))),q_)),x_Symbol),
    Condition(Subtract(Simp(Times(CN1,c,Subtract(Times(e,Plus(Times(C2,p),q)),Times(C2,f,Plus(p,q),x)),Power(Plus(a,Times(c,Sqr(x))),Subtract(p,C1)),Power(Plus(d,Times(e,x),Times(f,Sqr(x))),Plus(q,C1)),Power(Times(C2,Sqr(f),Plus(p,q),Plus(Times(C2,p),Times(C2,q),C1)),CN1)),x),Simp(Star(Power(Times(C2,Sqr(f),Plus(p,q),Plus(Times(C2,p),Times(C2,q),C1)),CN1),Integrate(Times(Power(Plus(a,Times(c,Sqr(x))),Subtract(p,C2)),Power(Plus(d,Times(e,x),Times(f,Sqr(x))),q),Simp(Plus(Times(CN1,a,c,Sqr(e),Subtract(C1,p),Plus(Times(C2,p),q)),Times(a,Plus(p,q),Plus(Times(CN2,a,Sqr(f),Plus(Times(C2,p),Times(C2,q),C1)),Times(c,Subtract(Times(C2,d,f),Times(Sqr(e),Plus(Times(C2,p),q)))))),Times(Plus(Times(C2,Subtract(Times(c,d),Times(a,f)),c,e,Subtract(C1,p),Plus(Times(C2,p),q)),Times(C4,a,c,e,f,Subtract(C1,p),Plus(p,q))),x),Times(Subtract(Times(p,Sqr(c),Sqr(e),Subtract(C1,p)),Times(c,Plus(p,q),Plus(Times(C2,a,Sqr(f),Subtract(Plus(Times(C4,p),Times(C2,q)),C1)),Times(c,Plus(Times(C2,d,f,Subtract(C1,Times(C2,p))),Times(Sqr(e),Subtract(Plus(Times(C3,p),q),C1))))))),Sqr(x))),x)),x)),x)),And(FreeQ(List(a,c,d,e,f,q),x),NeQ(Subtract(Sqr(e),Times(C4,d,f)),C0),GtQ(p,C1),NeQ(Plus(p,q),C0),NeQ(Plus(Times(C2,p),Times(C2,q),C1),C0),Not(IGtQ(p,C0)),Not(IGtQ(q,C0))))),
IIntegrate(1311,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),CN1),Power(Plus(d_,Times(e_DEFAULT,x_),Times(f_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(With(list(Set(q,Plus(Times(Sqr(c),Sqr(d)),Times(CN1,b,c,d,e),Times(a,c,Sqr(e)),Times(Sqr(b),d,f),Times(CN1,C2,a,c,d,f),Times(CN1,a,b,e,f),Times(Sqr(a),Sqr(f))))),Condition(Plus(Simp(Star(Power(q,CN1),Integrate(Times(Subtract(Subtract(Plus(Times(Sqr(c),d),Times(CN1,b,c,e),Times(Sqr(b),f)),Times(a,c,f)),Times(Subtract(Times(Sqr(c),e),Times(b,c,f)),x)),Power(Plus(a,Times(b,x),Times(c,Sqr(x))),CN1)),x)),x),Simp(Star(Power(q,CN1),Integrate(Times(Plus(Times(c,Sqr(e)),Times(CN1,c,d,f),Times(CN1,b,e,f),Times(a,Sqr(f)),Times(Subtract(Times(c,e,f),Times(b,Sqr(f))),x)),Power(Plus(d,Times(e,x),Times(f,Sqr(x))),CN1)),x)),x)),NeQ(q,C0))),And(FreeQ(List(a,b,c,d,e,f),x),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),NeQ(Subtract(Sqr(e),Times(C4,d,f)),C0)))),
IIntegrate(1312,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),CN1),Power(Plus(d_,Times(f_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(With(list(Set(q,Plus(Times(Sqr(c),Sqr(d)),Times(Sqr(b),d,f),Times(CN1,C2,a,c,d,f),Times(Sqr(a),Sqr(f))))),Condition(Subtract(Simp(Star(Power(q,CN1),Integrate(Times(Plus(Times(Sqr(c),d),Times(Sqr(b),f),Times(CN1,a,c,f),Times(b,c,f,x)),Power(Plus(a,Times(b,x),Times(c,Sqr(x))),CN1)),x)),x),Simp(Star(Power(q,CN1),Integrate(Times(Plus(Times(c,d,f),Times(CN1,a,Sqr(f)),Times(b,Sqr(f),x)),Power(Plus(d,Times(f,Sqr(x))),CN1)),x)),x)),NeQ(q,C0))),And(FreeQ(List(a,b,c,d,f),x),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0)))),
IIntegrate(1313,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),CN1),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_),Times(f_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Simp(Star(Times(CN2,e),Subst(Integrate(Power(Subtract(Times(e,Subtract(Times(b,e),Times(C4,a,f))),Times(Subtract(Times(b,d),Times(a,e)),Sqr(x))),CN1),x),x,Times(Plus(e,Times(C2,f,x)),Power(Plus(d,Times(e,x),Times(f,Sqr(x))),CN1D2)))),x),And(FreeQ(List(a,b,c,d,e,f),x),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),NeQ(Subtract(Sqr(e),Times(C4,d,f)),C0),EqQ(Subtract(Times(c,e),Times(b,f)),C0)))),
IIntegrate(1314,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),CN1),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_),Times(f_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(With(list(Set(q,Rt(Subtract(Sqr(b),Times(C4,a,c)),C2))),Subtract(Simp(Star(Times(C2,c,Power(q,CN1)),Integrate(Power(Times(Plus(b,Negate(q),Times(C2,c,x)),Sqrt(Plus(d,Times(e,x),Times(f,Sqr(x))))),CN1),x)),x),Simp(Star(Times(C2,c,Power(q,CN1)),Integrate(Power(Times(Plus(b,q,Times(C2,c,x)),Sqrt(Plus(d,Times(e,x),Times(f,Sqr(x))))),CN1),x)),x))),And(FreeQ(List(a,b,c,d,e,f),x),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),NeQ(Subtract(Sqr(e),Times(C4,d,f)),C0),NeQ(Subtract(Times(c,e),Times(b,f)),C0),PosQ(Subtract(Sqr(b),Times(C4,a,c)))))),
IIntegrate(1315,Integrate(Times(Power(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),CN1),Power(Plus(d_,Times(f_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(With(list(Set(q,Rt(Subtract(Sqr(b),Times(C4,a,c)),C2))),Subtract(Simp(Star(Times(C2,c,Power(q,CN1)),Integrate(Power(Times(Plus(b,Negate(q),Times(C2,c,x)),Sqrt(Plus(d,Times(f,Sqr(x))))),CN1),x)),x),Simp(Star(Times(C2,c,Power(q,CN1)),Integrate(Power(Times(Plus(b,q,Times(C2,c,x)),Sqrt(Plus(d,Times(f,Sqr(x))))),CN1),x)),x))),And(FreeQ(List(a,b,c,d,f),x),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),PosQ(Subtract(Sqr(b),Times(C4,a,c)))))),
IIntegrate(1316,Integrate(Times(Power(Plus(a_,Times(c_DEFAULT,Sqr(x_))),CN1),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_),Times(f_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(Plus(Simp(Star(C1D2,Integrate(Power(Times(Subtract(a,Times(Rt(Times(CN1,a,c),C2),x)),Sqrt(Plus(d,Times(e,x),Times(f,Sqr(x))))),CN1),x)),x),Simp(Star(C1D2,Integrate(Power(Times(Plus(a,Times(Rt(Times(CN1,a,c),C2),x)),Sqrt(Plus(d,Times(e,x),Times(f,Sqr(x))))),CN1),x)),x)),And(FreeQ(List(a,c,d,e,f),x),NeQ(Subtract(Sqr(e),Times(C4,d,f)),C0),PosQ(Times(CN1,a,c))))),
IIntegrate(1317,Integrate(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),CN1),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_),Times(f_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(With(list(Set(q,Rt(Subtract(Sqr(Subtract(Times(c,d),Times(a,f))),Times(Subtract(Times(b,d),Times(a,e)),Subtract(Times(c,e),Times(b,f)))),C2))),Subtract(Simp(Star(Power(Times(C2,q),CN1),Integrate(Times(Plus(Times(c,d),Times(CN1,a,f),q,Times(Subtract(Times(c,e),Times(b,f)),x)),Power(Times(Plus(a,Times(b,x),Times(c,Sqr(x))),Sqrt(Plus(d,Times(e,x),Times(f,Sqr(x))))),CN1)),x)),x),Simp(Star(Power(Times(C2,q),CN1),Integrate(Times(Plus(Times(c,d),Times(CN1,a,f),Negate(q),Times(Subtract(Times(c,e),Times(b,f)),x)),Power(Times(Plus(a,Times(b,x),Times(c,Sqr(x))),Sqrt(Plus(d,Times(e,x),Times(f,Sqr(x))))),CN1)),x)),x))),And(FreeQ(List(a,b,c,d,e,f),x),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),NeQ(Subtract(Sqr(e),Times(C4,d,f)),C0),NeQ(Subtract(Times(c,e),Times(b,f)),C0),NegQ(Subtract(Sqr(b),Times(C4,a,c)))))),
IIntegrate(1318,Integrate(Times(Power(Plus(a_DEFAULT,Times(c_DEFAULT,Sqr(x_))),CN1),Power(Plus(d_DEFAULT,Times(e_DEFAULT,x_),Times(f_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(With(list(Set(q,Rt(Plus(Sqr(Subtract(Times(c,d),Times(a,f))),Times(a,c,Sqr(e))),C2))),Subtract(Simp(Star(Power(Times(C2,q),CN1),Integrate(Times(Plus(Times(c,d),Times(CN1,a,f),q,Times(c,e,x)),Power(Times(Plus(a,Times(c,Sqr(x))),Sqrt(Plus(d,Times(e,x),Times(f,Sqr(x))))),CN1)),x)),x),Simp(Star(Power(Times(C2,q),CN1),Integrate(Times(Plus(Times(c,d),Times(CN1,a,f),Negate(q),Times(c,e,x)),Power(Times(Plus(a,Times(c,Sqr(x))),Sqrt(Plus(d,Times(e,x),Times(f,Sqr(x))))),CN1)),x)),x))),And(FreeQ(List(a,c,d,e,f),x),NeQ(Subtract(Sqr(e),Times(C4,d,f)),C0),NegQ(Times(CN1,a,c))))),
IIntegrate(1319,Integrate(Times(Power(Plus(a_DEFAULT,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_))),CN1),Power(Plus(d_DEFAULT,Times(f_DEFAULT,Sqr(x_))),CN1D2)),x_Symbol),
    Condition(With(list(Set(q,Rt(Plus(Sqr(Subtract(Times(c,d),Times(a,f))),Times(Sqr(b),d,f)),C2))),Subtract(Simp(Star(Power(Times(C2,q),CN1),Integrate(Times(Plus(Times(c,d),Times(CN1,a,f),q,Times(CN1,b,f,x)),Power(Times(Plus(a,Times(b,x),Times(c,Sqr(x))),Sqrt(Plus(d,Times(f,Sqr(x))))),CN1)),x)),x),Simp(Star(Power(Times(C2,q),CN1),Integrate(Times(Plus(Times(c,d),Times(CN1,a,f),Negate(q),Times(CN1,b,f,x)),Power(Times(Plus(a,Times(b,x),Times(c,Sqr(x))),Sqrt(Plus(d,Times(f,Sqr(x))))),CN1)),x)),x))),And(FreeQ(List(a,b,c,d,f),x),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),NegQ(Subtract(Sqr(b),Times(C4,a,c)))))),
IIntegrate(1320,Integrate(Times(Sqrt(Plus(a_,Times(b_DEFAULT,x_),Times(c_DEFAULT,Sqr(x_)))),Power(Plus(d_,Times(e_DEFAULT,x_),Times(f_DEFAULT,Sqr(x_))),CN1)),x_Symbol),
    Condition(Subtract(Simp(Star(Times(c,Power(f,CN1)),Integrate(Power(Plus(a,Times(b,x),Times(c,Sqr(x))),CN1D2),x)),x),Simp(Star(Power(f,CN1),Integrate(Times(Plus(Times(c,d),Times(CN1,a,f),Times(Subtract(Times(c,e),Times(b,f)),x)),Power(Times(Sqrt(Plus(a,Times(b,x),Times(c,Sqr(x)))),Plus(d,Times(e,x),Times(f,Sqr(x)))),CN1)),x)),x)),And(FreeQ(List(a,b,c,d,e,f),x),NeQ(Subtract(Sqr(b),Times(C4,a,c)),C0),NeQ(Subtract(Sqr(e),Times(C4,d,f)),C0))))
  );
}
