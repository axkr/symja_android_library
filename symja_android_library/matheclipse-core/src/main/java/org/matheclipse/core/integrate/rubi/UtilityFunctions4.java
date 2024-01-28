package org.matheclipse.core.integrate.rubi;


import static org.matheclipse.core.expression.F.$rubi;
import static org.matheclipse.core.expression.F.And;
import static org.matheclipse.core.expression.F.AtomQ;
import static org.matheclipse.core.expression.F.C2;
import static org.matheclipse.core.expression.F.Catch;
import static org.matheclipse.core.expression.F.CompoundExpression;
import static org.matheclipse.core.expression.F.FreeQ;
import static org.matheclipse.core.expression.F.Function;
import static org.matheclipse.core.expression.F.Greater;
import static org.matheclipse.core.expression.F.GreaterEqual;
import static org.matheclipse.core.expression.F.Head;
import static org.matheclipse.core.expression.F.ISetDelayed;
import static org.matheclipse.core.expression.F.If;
import static org.matheclipse.core.expression.F.IntegerQ;
import static org.matheclipse.core.expression.F.Less;
import static org.matheclipse.core.expression.F.LessEqual;
import static org.matheclipse.core.expression.F.List;
import static org.matheclipse.core.expression.F.Or;
import static org.matheclipse.core.expression.F.Part;
import static org.matheclipse.core.expression.F.SameQ;
import static org.matheclipse.core.expression.F.Scan;
import static org.matheclipse.core.expression.F.Slot1;
import static org.matheclipse.core.expression.F.Throw;
import static org.matheclipse.core.expression.F.n_;
import static org.matheclipse.core.expression.F.u_;
import static org.matheclipse.core.expression.F.x_;
import static org.matheclipse.core.expression.F.x_Symbol;
import static org.matheclipse.core.expression.S.AppellF1;
import static org.matheclipse.core.expression.S.False;
import static org.matheclipse.core.expression.S.Hypergeometric2F1;
import static org.matheclipse.core.expression.S.Integrate;
import static org.matheclipse.core.expression.S.Null;
import static org.matheclipse.core.expression.S.True;
import static org.matheclipse.core.expression.S.n;
import static org.matheclipse.core.expression.S.u;
import static org.matheclipse.core.expression.S.x;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.CalculusFreeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.CalculusQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.HeldFormQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.HyperbolicQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IGtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ILeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.ILtQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.IntegralFreeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.InverseFunctionFreeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.InverseFunctionQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.TrigHyperbolicFreeQ;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.TrigQ;
import org.matheclipse.core.interfaces.IAST;
/** 
 * UtilityFunctions rules from the <a href="https://rulebasedintegration.org/">Rubi -
 * rule-based integrator</a>.
 *  
 */
class UtilityFunctions4 { 
  public static IAST RULES = List( 
ISetDelayed(24,TrigHyperbolicFreeQ(u_,x_Symbol),
    If(AtomQ(u),True,If(Or(TrigQ(u),HyperbolicQ(u),CalculusQ(u)),FreeQ(u,x),Catch(CompoundExpression(Scan(Function(If(TrigHyperbolicFreeQ(Slot1,x),Null,Throw(False))),u),True))))),
ISetDelayed(25,InverseFunctionFreeQ(u_,x_Symbol),
    If(AtomQ(u),True,If(Or(InverseFunctionQ(u),CalculusQ(u),SameQ(Head(u),Hypergeometric2F1),SameQ(Head(u),AppellF1)),FreeQ(u,x),Catch(CompoundExpression(Scan(Function(If(InverseFunctionFreeQ(Slot1,x),Null,Throw(False))),u),True))))),
ISetDelayed(26,CalculusFreeQ(u_,x_),
    If(AtomQ(u),True,If(Or(And(CalculusQ(u),SameQ(Part(u,C2),x)),HeldFormQ(u)),False,Catch(CompoundExpression(Scan(Function(If(CalculusFreeQ(Slot1,x),Null,Throw(False))),u),True))))),
ISetDelayed(27,IntegralFreeQ(u_),
    And(FreeQ(u,Integrate),FreeQ(u,$rubi("Integral")),FreeQ(u,$rubi("Unintegrable")),FreeQ(u,$rubi("CannotIntegrate")))),
      // ISetDelayed(28,EqQ(u_,v_),
      // Or(Quiet(PossibleZeroQ(Subtract(u,v))),SameQ(Refine(Equal(u,v)),True))),
      // ISetDelayed(29,NeQ(u_,v_),
      // Not(Or(Quiet(PossibleZeroQ(Subtract(u,v))),SameQ(Refine(Equal(u,v)),True)))),
ISetDelayed(30,IGtQ(u_,n_),
    And(IntegerQ(u),Greater(u,n))),
ISetDelayed(31,ILtQ(u_,n_),
    And(IntegerQ(u),Less(u,n))),
ISetDelayed(32,IGeQ(u_,n_),
    And(IntegerQ(u),GreaterEqual(u,n))),
ISetDelayed(33,ILeQ(u_,n_),
    And(IntegerQ(u),LessEqual(u,n)))
  );
}
