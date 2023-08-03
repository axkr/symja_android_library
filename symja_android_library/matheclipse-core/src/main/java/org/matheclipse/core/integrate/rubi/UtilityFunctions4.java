package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;

import org.matheclipse.core.interfaces.IAST;
/** 
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class UtilityFunctions4 { 
  public static IAST RULES = List( 
ISetDelayed(30,IGtQ(u_,n_),
    And(IntegerQ(u),Greater(u,n))),
ISetDelayed(31,ILtQ(u_,n_),
    And(IntegerQ(u),Less(u,n))),
ISetDelayed(32,IGeQ(u_,n_),
    And(IntegerQ(u),GreaterEqual(u,n))),
ISetDelayed(33,ILeQ(u_,n_),
    And(IntegerQ(u),LessEqual(u,n))),
ISetDelayed(34,GtQ(u_,v_),
    If(RealValuedNumberQ(u),If(RealValuedNumberQ(v),Greater(u,v),With(list(Set($s("vn"),N(Together(v)))),And(SameQ(Head($s("vn")),Real),Greater(u,$s("vn"))))),With(list(Set($s("un"),N(Together(u)))),If(SameQ(Head($s("un")),Real),If(RealValuedNumberQ(v),Greater($s("un"),v),With(list(Set($s("vn"),N(Together(v)))),And(SameQ(Head($s("vn")),Real),Greater($s("un"),$s("vn"))))),False)))),
ISetDelayed(35,GtQ(u_,v_,w_),
    And(GtQ(u,v),GtQ(v,w))),
ISetDelayed(36,LtQ(u_,v_),
    If(RealValuedNumberQ(u),If(RealValuedNumberQ(v),Less(u,v),With(list(Set($s("vn"),N(Together(v)))),And(SameQ(Head($s("vn")),Real),Less(u,$s("vn"))))),With(list(Set($s("un"),N(Together(u)))),If(SameQ(Head($s("un")),Real),If(RealValuedNumberQ(v),Less($s("un"),v),With(list(Set($s("vn"),N(Together(v)))),And(SameQ(Head($s("vn")),Real),Less($s("un"),$s("vn"))))),False)))),
ISetDelayed(37,LtQ(u_,v_,w_),
    And(LtQ(u,v),LtQ(v,w))),
ISetDelayed(38,GeQ(u_,v_),
    If(RealValuedNumberQ(u),If(RealValuedNumberQ(v),GreaterEqual(u,v),With(list(Set($s("vn"),N(Together(v)))),And(SameQ(Head($s("vn")),Real),GreaterEqual(u,$s("vn"))))),With(list(Set($s("un"),N(Together(u)))),If(SameQ(Head($s("un")),Real),If(RealValuedNumberQ(v),GreaterEqual($s("un"),v),With(list(Set($s("vn"),N(Together(v)))),And(SameQ(Head($s("vn")),Real),GreaterEqual($s("un"),$s("vn"))))),False)))),
ISetDelayed(39,GeQ(u_,v_,w_),
    And(GeQ(u,v),GeQ(v,w))),
ISetDelayed(40,LeQ(u_,v_),
    If(RealValuedNumberQ(u),If(RealValuedNumberQ(v),LessEqual(u,v),With(list(Set($s("vn"),N(Together(v)))),And(SameQ(Head($s("vn")),Real),LessEqual(u,$s("vn"))))),With(list(Set($s("un"),N(Together(u)))),If(SameQ(Head($s("un")),Real),If(RealValuedNumberQ(v),LessEqual($s("un"),v),With(list(Set($s("vn"),N(Together(v)))),And(SameQ(Head($s("vn")),Real),LessEqual($s("un"),$s("vn"))))),False)))),
ISetDelayed(41,LeQ(u_,v_,w_),
    And(LeQ(u,v),LeQ(v,w)))
  );
}
