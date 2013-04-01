package org.matheclipse.core.integrate.rubi;

import static org.matheclipse.core.expression.F.*;
import static org.matheclipse.core.integrate.rubi.UtilityFunctionCtors.*;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * UtilityFunctions from the <a href="http://www.apmaths.uwo.ca/~arich/">Rubi -
 * rule-based integrator</a>.
 * 
 * TODO a lot of functions are only placeholders at the moment.
 * 
 */
public class UtilityFunctions {
	public final static String PACKAGE_NAME = "org.matheclipse.core.integrate.rubi";

	public final static String CLASS_NAME = "UtilityFunctions";

	public final static String INTEGRATE_PREFIX = "Integrate::";

	/**
	 * Convert to Integrate[]
	 * 
	 * @param a0
	 * @param a1
	 * @return
	 */
	public static IAST Int(final IExpr a0, final IExpr a1) {
		// Integrate.setAttributes(ISymbol.CONSOLE_OUTPUT);
		return binary(Integrate, a0, a1);
	}

	public static ISymbol $sDBG(final String symbolName) {
		ISymbol sym = $s(symbolName);
		// sym.setAttributes(ISymbol.CONSOLE_OUTPUT);
		return sym;
	}

	public static IAST FunctionOfQ(final IExpr a0, final IExpr a1, final IExpr a2) {
		// call the constructor with 4 arguments
		return UtilityFunctionCtors.FunctionOfQ(a0, a1, a2, F.False);
	}

	public static IAST IntegrateMonomialSum(final IExpr a0, final IExpr a1) {
		// TODO
		return binary($sDBG(INTEGRATE_PREFIX + "IntegrateMonomialSum"), a0, a1);
	}

	public static IAST SplitFreeIntegrate(final IExpr a0, final IExpr a1) {
		// TODO
		return binary($sDBG(INTEGRATE_PREFIX + "SplitFreeIntegrate"), a0, a1);
	}

	public static IAST TryTanSubst(final IExpr a0, final IExpr a1) {
		// TODO
		return binary($sDBG(INTEGRATE_PREFIX + "TryTanSubst"), a0, a1);
	}

	public static IAST TryTanhSubst(final IExpr a0, final IExpr a1) {
		// TODO
		return binary($sDBG(INTEGRATE_PREFIX + "TryTanhSubst"), a0, a1);
	}

	public static IAST TryPureTanSubst(final IExpr a0, final IExpr a1) {
		// TODO
		return binary($sDBG(INTEGRATE_PREFIX + "TryPureTanSubst"), a0, a1);
	}

	public static IAST TryPureTanhSubst(final IExpr a0, final IExpr a1) {
		// TODO
		return binary($sDBG(INTEGRATE_PREFIX + "TryPureTanhSubst"), a0, a1);
	}

	public static IAST LinearSinCosQ(final IExpr a0, final IExpr a1) {
		// TODO
		return binary($sDBG(INTEGRATE_PREFIX + "LinearSinCosQ"), a0, a1);
	}

	public static IAST LinearSinhCoshQ(final IExpr a0, final IExpr a1) {
		// TODO
		return binary($sDBG(INTEGRATE_PREFIX + "LinearSinhCoshQ"), a0, a1);
	}

	public static IExpr isList(IAST ast) {
		if (ast.size() > 1) {
			return F.bool(ast.get(1).isList());
		}
		return F.False;
	}

	static ISymbol ListQ = F.method(INTEGRATE_PREFIX + "ListQ", PACKAGE_NAME, CLASS_NAME, "isList");

	public static IAST ListQ(final IExpr a) {
		return unary(ListQ, a);
	}

	//
	// should be buit-in ?
	//
	public static IAST Break() {
		return ast($s("Break"));
	}

	public static IAST Return(final IExpr a) {
		// TODO fix this
		return unary($s("Return"), a);
	}

	public static IAST OrderedQ(final IExpr a) {
		// TODO fix this
		return unary($s("OrderedQ"), a);
	}

	public static IAST Sign(final IExpr a) {
		// TODO fix this
		return unary($s("Sign"), a);
	}

	public static IAST FullSimplify(final IExpr a) {
		// TODO fix this
		return unary($s("FullSimplify"), a);
	}

	public static IAST Increment(final IExpr a) {
		// TODO fix this
		return unary($s("Increment"), a);
	}

	public static IAST Decrement(final IExpr a) {
		// TODO fix this
		return unary($s("Decrement"), a);
	}

	public static IAST Distribute(final IExpr a) {
		// TODO fix this
		return unary($sDBG("Distribute"), a);
	}

	public static IAST FactorSquareFreeList(final IExpr a) {
		// TODO fix this
		return unary($sDBG("FactorSquareFreeList"), a);
	}

	public static IAST Reverse(final IExpr a) {
		// TODO fix this
		return unary($s("Reverse"), a);
	}

	public static IAST Protect(final IExpr a) {
		// TODO fix this
		return unary($s("Protect"), a);
	}

	public static IAST InverseFunction(final IExpr a) {
		// TODO fix this
		return unary($s("InverseFunction"), a);
	}

	public static IAST Count(final IExpr a0, final IExpr a1, final IExpr a2) {
		// TODO
		return ternary($s("Count"), a0, a1, a2);
	}

	public static IAST Exponent(final IExpr a0, final IExpr a1, final IExpr a2) {
		// TODO
		return ternary($sDBG("Exponent"), a0, a1, a2);
	}

	public static IAST LCM(final IExpr a0, final IExpr a1) {
		// TODO
		return binary($sDBG("LCM"), a0, a1);
	}

	public static IAST Order(final IExpr a0, final IExpr a1) {
		// TODO
		return binary($s("Order"), a0, a1);
	}

	public static IAST Delete(final IExpr a0, final IExpr a1) {
		// TODO
		return binary($s("Delete"), a0, a1);
	}

	public static IAST Join(final IExpr a0, final IExpr a1) {
		// TODO
		return binary($s("Join"), a0, a1);
	}

	public static IAST TrigExpand(final IExpr a0) {
		// TODO
		return unary($sDBG("TrigExpand"), a0);
	}

	public static IAST While(final IExpr a0, final IExpr a1) {
		// TODO
		return binary($s("While"), a0, a1);
	}

	public static IAST StringJoin(final IExpr a) {
		// TODO fix this
		return unary($s("StringJoin"), a);
	}

	public static IAST Drop(final IExpr a0, final IExpr a1) {
		// TODO
		return binary($s("Drop"), a0, a1);
	}

	public static IAST Take(final IExpr a0, final IExpr a1) {
		// TODO
		return binary($s("Take"), a0, a1);
	}

	public static IAST Print(final IExpr a0) {
		// TODO
		return unary($s("Print"), a0);
	}

	public static IAST Print(final IExpr a0, final IExpr a1) {
		// TODO
		return binary($s("Print"), a0, a1);
	}

	public static IAST PolynomialRemainder(final IExpr a0, final IExpr a1, final IExpr a2) {
		// TODO
		return ternary($s("PolynomialRemainder"), a0, a1, a2);
	}

	public static IAST PolynomialQuotient(final IExpr a0, final IExpr a1, final IExpr a2) {
		// TODO
		return ternary($s("PolynomialQuotient"), a0, a1, a2);
	}

	public static IAST ReplacePart(final IExpr a0, final IExpr a1, final IExpr a2) {
		// TODO
		return ternary($sDBG("ReplacePart"), a0, a1, a2);
	}

	public static IAST Do(final IExpr a0, final IExpr a1) {
		// TODO
		return binary($s("Do"), a0, a1);
	}

	public static IAST Reap(final IExpr a) {
		// TODO fix this
		return unary($s("Reap"), a);
	}

	public static IAST Sow(final IExpr a) {
		// TODO fix this
		return unary($s("Sow"), a);
	}

	// public static IAST Unprotect(final IExpr a) {
	// // TODO fix this
	// return unary($s("Unprotect"), a);
	// }

	// public static IAST DownValues(final IExpr a) {
	// // TODO fix this
	// return unary($s("DownValues"), a);
	// }

	public static IAST Length(final IExpr a) {
		// TODO fix this
		return unary($s("Length"), a);
	}

	public static IAST Scan(final IExpr a0, final IExpr a1) {
		// TODO
		return binary($sDBG("Scan"), a0, a1);
	}

	public static IAST ArcCoth(final IExpr a) {
		// TODO fix this
		return unary($s("ArcCoth"), a);
	}

	public static IAST Coefficient(final IExpr a0, final IExpr a1, final IExpr a2) {
		return ternary($sDBG("Coefficient"), a0, a1, a2);
	}

	public static IAST CosIntegral(final IExpr a) {
		// TODO fix this
		return unary($s("CosIntegral"), a);
	}

	public static IAST EllipticE(final IExpr a0, final IExpr a1) {
		return binary($s("EllipticE"), a0, a1);
	}

	public static IAST EllipticF(final IExpr a0, final IExpr a1) {
		return binary($s("EllipticF"), a0, a1);
	}

	public static IAST Exponent(final IExpr a0, final IExpr a1) {
		return binary($sDBG("Exponent"), a0, a1);
	}

	public static IAST ArcCsc(final IExpr a) {
		// TODO fix this
		return unary($s("ArcCsc"), a);
	}

	public static IAST ArcCsch(final IExpr a) {
		// TODO fix this
		return unary($s("ArcCsch"), a);
	}

	public static IAST ArcSec(final IExpr a) {
		// TODO fix this
		return unary($s("ArcSec"), a);
	}

	public static IAST ArcSech(final IExpr a) {
		// TODO fix this
		return unary($s("ArcSech"), a);
	}

	public static IAST FresnelC(final IExpr a) {
		// TODO fix this
		return unary($s("FresnelC"), a);
	}

	public static IAST FresnelS(final IExpr a) {
		// TODO fix this
		return unary($s("FresnelS"), a);
	}

	public static IAST PolynomialQ(final IExpr a0, final IExpr a1) {
		return binary($sDBG("PolynomialQ"), a0, a1);
	}

	public static IAST SameQ(final IExpr a0, final IExpr a1) {
		return binary($sDBG("SameQ"), a0, a1);
	}

	public static IAST UnsameQ(final IExpr a0, final IExpr a1) {
		return binary($sDBG("UnsameQ"), a0, a1);
	}

	public static IAST SinIntegral(final IExpr a) {
		// TODO fix this
		return unary($s("SinIntegral"), a);
	}

	public static IAST CoshIntegral(final IExpr a) {
		// TODO fix this
		return unary($s("CoshIntegral"), a);
	}

	public static IAST SinhIntegral(final IExpr a) {
		// TODO fix this
		return unary($s("SinhIntegral"), a);
	}

	public static IAST AtomQ(final IExpr a) {
		// TODO fix this
		return unary($s("AtomQ"), a);
	}

	public static IAST Head(final IExpr a) {
		// TODO fix this
		return unary($s("Head"), a);
	}

	public static IAST Map(final IExpr a0, final IExpr a1) {
		return binary($s("Map"), a0, a1);
	}

	public static IAST Unequal(final IExpr a0, final IExpr a1) {
		return binary($s("Unequal"), a0, a1);
	}

	public static IAST Discriminant(final IExpr a0, final IExpr a1) {
		return binary($s("Discriminant"), a0, a1);
	}

	public static IAST Erf(final IExpr a) {
		return unary($s("Erf"), a);
	}

	public static IAST Erfi(final IExpr a) {
		return unary($s("Erfi"), a);
	}

	public static IAST ExpIntegralEi(final IExpr a) {
		return unary($s("ExpIntegralEi"), a);
	}

	public static IAST Gamma(final IExpr a0, final IExpr a1) {
		return binary($s("Gamma"), a0, a1);
	}

	public static IAST LogIntegral(final IExpr a) {
		return unary($s("LogIntegral"), a);
	}

	public static IAST PolyLog(final IExpr a0, final IExpr a1) {
		return binary($s("PolyLog"), a0, a1);
	}

	public static IAST ProductLog(final IExpr a) {
		return unary($s("ProductLog"), a);
	}

	public static IAST TrigReduce(final IExpr v) {
		return unary($s("TrigReduce"), v);
	}

	public static IAST Csch(final IExpr a) {
		return unary($s("Csch"), a);
	}

	public static IAST Sech(final IExpr a) {
		return unary($s("Sech"), a);
	}

	public static IAST FractionalPart(final IExpr a) {
		return unary($s("FractionalPart"), a);
	}

	public static IAST Throw(final IExpr a) {
		return unary($s("Throw"), a);
	}

	public static IAST Catch(final IExpr a) {
		return unary($sDBG("Catch"), a);
	}

	public static IAST Erfc(final IExpr a) {
		return unary($s("Erfc"), a);
	}

	public static IAST Gamma(final IExpr a0) {
		return unary($s("Gamma"), a0);
	}

	public static IAST LogGamma(final IExpr a0) {
		return unary($s("LogGamma"), a0);
	}

	public static IAST Zeta(final IExpr a0, final IExpr a1) {
		return binary($s("Zeta"), a0, a1);
	}

	public static IAST PolyGamma(final IExpr a0, final IExpr a1) {
		return binary($s("PolyGamma"), a0, a1);
	}

	public static IAST ExpIntegralE(final IExpr a0, final IExpr a1) {
		return binary($s("ExpIntegralE"), a0, a1);
	}

	public static void init() {
		// Dist[u_,v_]+Dist[w_,v_] := If[ZeroQ[u+w], 0, Dist[u+w,v]],
		org.matheclipse.core.reflection.system.Plus.CONST.setUpHashRule(Dist($p("u"), $p("v")), Dist($p("w"), $p("v")), If(ZeroQ(Plus(
				$s("u"), $s("w"))), C0, Dist(Plus($s("u"), $s("w")), $s("v"))), null);
		// Dist[u_,v_]-Dist[w_,v_] := If[ZeroQ[u-w], 0, Dist[u-w,v]],
		org.matheclipse.core.reflection.system.Plus.CONST.setUpHashRule(Dist($p("u"), $p("v")), Times(CN1, Dist($p("w"), $p("v"))), If(
				ZeroQ(Plus($s("u"), Times(CN1, $s("w")))), C0, Dist(Plus($s("u"), Times(CN1, $s("w"))), $s("v"))), null);
		// Dist[u_,v_]*w_ := Dist[w*u,v] /; w=!=-1
		org.matheclipse.core.reflection.system.Times.CONST.setUpHashRule(Dist($p("u"), $p("v")), $p("w"), Dist(Times($s("w"), $s("u")),
				$s("v")), UnsameQ($s("w"), CN1));
	}
}