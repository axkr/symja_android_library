package org.matheclipse.core.builtin.function;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.RuleCreationError;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Set the attributes for a symbol
 * 
 */
public class SetAttributes extends AbstractCoreFunctionEvaluator {

	public SetAttributes() {
	}

	/**
   *
   */
	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		Validate.checkSize(ast, 3);
		
		if (ast.arg1().isSymbol()) {
			IExpr arg2 = F.eval(ast.arg2());
			final ISymbol sym = ((ISymbol) ast.arg1());
			if (!engine.isPackageMode()) {
				if (Config.SERVER_MODE && (sym.toString().charAt(0) != '$')) {
					throw new RuleCreationError(sym);
				}
			}
			if (arg2.isSymbol()) {
				ISymbol attribute = (ISymbol) arg2;
				if (attribute == F.Flat) {
					sym.setAttributes(ISymbol.FLAT);
					return F.Null;
				}

				if (attribute == F.Listable) {
					sym.setAttributes(ISymbol.LISTABLE);
					return F.Null;
				}

				if (attribute == F.OneIdentity) {
					sym.setAttributes(ISymbol.ONEIDENTITY);
					return F.Null;
				}

				if (attribute == F.Orderless) {
					sym.setAttributes(ISymbol.ORDERLESS);
					return F.Null;
				}

				if (attribute == F.HoldAll) {
					sym.setAttributes(ISymbol.HOLDALL);
					return F.Null;
				}

				if (attribute == F.HoldFirst) {
					sym.setAttributes(ISymbol.HOLDFIRST);
					return F.Null;
				}

				if (attribute == F.HoldRest) {
					sym.setAttributes(ISymbol.HOLDREST);
					return F.Null;
				}

				if (attribute == F.NHoldAll) {
					sym.setAttributes(ISymbol.NHOLDALL);
					return F.Null;
				}

				if (attribute == F.NHoldFirst) {
					sym.setAttributes(ISymbol.NHOLDFIRST);
					return F.Null;
				}

				if (attribute == F.NHoldRest) {
					sym.setAttributes(ISymbol.NHOLDREST);
					return F.Null;
				}

				if (attribute == F.NumericFunction) {
					sym.setAttributes(ISymbol.NUMERICFUNCTION);
					return F.Null;
				}

			} else {
				if (ast.arg2().isList()) {
					final IAST lst = (IAST) ast.arg2();
					int symbolAttributes = ISymbol.NOATTRIBUTE;
					for (int i = 1; i < lst.size(); i++) {
						ISymbol attribute = (ISymbol) lst.get(i);
						if (attribute == F.Flat) {
							sym.setAttributes(symbolAttributes | ISymbol.FLAT);
						}

						if (attribute == F.Listable) {
							sym.setAttributes(symbolAttributes | ISymbol.LISTABLE);
						}

						if (attribute == F.OneIdentity) {
							sym.setAttributes(symbolAttributes | ISymbol.ONEIDENTITY);
						}

						if (attribute == F.Orderless) {
							sym.setAttributes(symbolAttributes | ISymbol.ORDERLESS);
						}

						if (attribute == F.HoldAll) {
							sym.setAttributes(symbolAttributes | ISymbol.HOLDALL);
						}

						if (attribute == F.HoldFirst) {
							sym.setAttributes(symbolAttributes | ISymbol.HOLDFIRST);
						}

						if (attribute == F.HoldRest) {
							sym.setAttributes(symbolAttributes | ISymbol.HOLDREST);
						}

						if (attribute == F.NHoldAll) {
							sym.setAttributes(symbolAttributes | ISymbol.NHOLDALL);
						}

						if (attribute == F.NHoldFirst) {
							sym.setAttributes(symbolAttributes | ISymbol.NHOLDFIRST);
						}

						if (attribute == F.NHoldRest) {
							sym.setAttributes(symbolAttributes | ISymbol.NHOLDREST);
						}

						if (attribute == F.NumericFunction) {
							sym.setAttributes(symbolAttributes | ISymbol.NUMERICFUNCTION);
						}

						symbolAttributes = sym.getAttributes();
					}
					// end for

					return F.Null;
				}
			}
		}
		return null;
	}

}
