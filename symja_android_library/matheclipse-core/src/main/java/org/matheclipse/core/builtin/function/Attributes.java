package org.matheclipse.core.builtin.function;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Get the list of attributes of a given symbol.
 * <p>
 * See the online Symja function reference: <a href="https://bitbucket.org/axelclk/symja_android_library/wiki/Symbols/Attributes">Attributes</a>
 * </p>
 *
 */
public class Attributes extends AbstractCoreFunctionEvaluator {

	public Attributes() {
	}

	/**
   *
   */
	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 2);

		if (ast.arg1().isSymbol()) {
			IAST result = F.List();
			final ISymbol sym = ((ISymbol) ast.arg1());
			int attributea = sym.getAttributes();

			if ((attributea & ISymbol.FLAT) != ISymbol.NOATTRIBUTE) {
				result.add(F.Flat);
			}

			if ((attributea & ISymbol.LISTABLE) != ISymbol.NOATTRIBUTE) {
				result.add(F.Listable);
			}

			if ((attributea & ISymbol.ONEIDENTITY) != ISymbol.NOATTRIBUTE) {
				result.add(F.OneIdentity);
			}

			if ((attributea & ISymbol.ORDERLESS) != ISymbol.NOATTRIBUTE) {
				result.add(F.Orderless);
			}

			if ((attributea & ISymbol.HOLDALL) != ISymbol.NOATTRIBUTE) {
				result.add(F.HoldAll);
			}

			if ((attributea & ISymbol.HOLDFIRST) != ISymbol.NOATTRIBUTE) {
				result.add(F.HoldFirst);
			}

			if ((attributea & ISymbol.HOLDREST) != ISymbol.NOATTRIBUTE) {
				result.add(F.HoldRest);
			}

			if ((attributea & ISymbol.NHOLDALL) != ISymbol.NOATTRIBUTE) {
				result.add(F.NHoldAll);
			}

			if ((attributea & ISymbol.NHOLDFIRST) != ISymbol.NOATTRIBUTE) {
				result.add(F.NHoldFirst);
			}

			if ((attributea & ISymbol.NHOLDREST) != ISymbol.NOATTRIBUTE) {
				result.add(F.NHoldRest);
			}

			if ((attributea & ISymbol.NUMERICFUNCTION) != ISymbol.NOATTRIBUTE) {
				result.add(F.NumericFunction);
			}
			return result;
		}
		return F.NIL;
	}
}
