package org.matheclipse.core.builtin;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.RuleCreationError;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.ID;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class AttributeFunctions {
	static {
		F.Attributes.setEvaluator(new Attributes());
		F.ClearAttributes.setEvaluator(new ClearAttributes());
		F.SetAttributes.setEvaluator(new SetAttributes());
	}

	/**
	 * <pre>
	 * Attributes(symbol)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the list of attributes which are assigned to <code>symbol</code>
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Attributes(Plus)
	 * {Flat,Listable,OneIdentity,Orderless,NumericFunction}
	 * </pre>
	 */
	private final static class Attributes extends AbstractCoreFunctionEvaluator {

		/**
		*
		*/
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			if (ast.arg1().isSymbol()) {
				final ISymbol sym = ((ISymbol) ast.arg1());
				return attributesList(sym);
			}
			return F.NIL;
		}

	}

	/**
	 * Set the attributes for a symbol
	 * 
	 */
	private final static class ClearAttributes extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);

			if (ast.arg1().isSymbol()) {
				IExpr arg2 = engine.evaluate(ast.arg2());
				final ISymbol sym = ((ISymbol) ast.arg1());
				return clearAttributes(sym, ast, arg2, engine);
			}
			if (ast.arg1().isList()) {
				IAST list = (IAST) ast.arg1();
				IExpr arg2 = engine.evaluate(ast.arg2());
				for (int i = 1; i < list.size(); i++) {
					if (list.get(i).isSymbol()) {
						final ISymbol sym = ((ISymbol) list.get(i));
						clearAttributes(sym, ast, arg2, engine);
					}
				}
				return F.Null;

			}
			return F.NIL;
		}

		private IExpr clearAttributes(final ISymbol sym, final IAST ast, IExpr arg2, EvalEngine engine) {
			if (!engine.isPackageMode()) {
				if (Config.SERVER_MODE && (sym.toString().charAt(0) != '$')) {
					throw new RuleCreationError(sym);
				}
			}
			if (arg2.isSymbol()) {
				ISymbol attribute = (ISymbol) arg2;
				clearAttributes(sym, attribute);
				return F.Null;
			} else {
				if (ast.arg2().isList()) {
					final IAST lst = (IAST) ast.arg2();
					for (int i = 1; i < lst.size(); i++) {
						ISymbol attribute = (ISymbol) lst.get(i);
						clearAttributes(sym, attribute);
					}
					return F.Null;
				}
			}
			return F.Null;
		}

		private void clearAttributes(final ISymbol sym, ISymbol attribute) {
			int functionID = attribute.ordinal();
			if (functionID > ID.UNKNOWN) {
				switch (functionID) {
				case ID.Constant:
					sym.clearAttributes(ISymbol.CONSTANT);
					break;
				case ID.Flat:
					sym.clearAttributes(ISymbol.FLAT);
					break;
				case ID.Listable:
					sym.clearAttributes(ISymbol.LISTABLE);
					break;
				case ID.OneIdentity:
					sym.clearAttributes(ISymbol.ONEIDENTITY);
					break;
				case ID.Orderless:
					sym.clearAttributes(ISymbol.ORDERLESS);
					break;
				case ID.HoldAll:
					sym.clearAttributes(ISymbol.HOLDALL);
					break;
				case ID.HoldFirst:
					sym.clearAttributes(ISymbol.HOLDFIRST);
					break;
				case ID.HoldRest:
					sym.clearAttributes(ISymbol.HOLDREST);
					break;
				case ID.NHoldAll:
					sym.clearAttributes(ISymbol.NHOLDALL);
					break;
				case ID.NHoldFirst:
					sym.clearAttributes(ISymbol.NHOLDFIRST);
					break;
				case ID.NHoldRest:
					sym.clearAttributes(ISymbol.NHOLDREST);
					break;
				case ID.NumericFunction:
					sym.clearAttributes(ISymbol.NUMERICFUNCTION);
					break;
				}
			}
		}

	}

	/**
	 * Set the attributes for a symbol
	 * 
	 */
	private final static class SetAttributes extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);

			if (ast.arg1().isSymbol()) {
				IExpr arg2 = engine.evaluate(ast.arg2());
				final ISymbol sym = ((ISymbol) ast.arg1());
				return addAttributes(sym, ast, arg2, engine);
			}
			if (ast.arg1().isList()) {
				IAST list = (IAST) ast.arg1();
				return setSymbolsAttributes(ast, engine, list);
			}
			return F.NIL;
		}

		private static IExpr addAttributes(final ISymbol sym, final IAST ast, IExpr arg2, EvalEngine engine) {
			if (!engine.isPackageMode()) {
				if (Config.SERVER_MODE && (sym.toString().charAt(0) != '$')) {
					throw new RuleCreationError(sym);
				}
			}
			if (arg2.isSymbol()) {
				ISymbol attribute = (ISymbol) arg2;
				addAttributes(sym, attribute);
				return F.Null;
			} else {
				if (ast.arg2().isList()) {
					final IAST lst = (IAST) ast.arg2();
					for (int i = 1; i < lst.size(); i++) {
						ISymbol attribute = (ISymbol) lst.get(i);
						addAttributes(sym, attribute);
					}
					// end for

					return F.Null;
				}
			}
			return F.Null;
		}

		private static void addAttributes(final ISymbol sym, ISymbol attribute) {
			int functionID = attribute.ordinal();
			if (functionID > ID.UNKNOWN) {
				switch (functionID) {
				case ID.Constant:
					sym.addAttributes(ISymbol.CONSTANT);
					break;
				case ID.Flat:
					sym.addAttributes(ISymbol.FLAT);
					break;
				case ID.Listable:
					sym.addAttributes(ISymbol.LISTABLE);
					break;
				case ID.OneIdentity:
					sym.addAttributes(ISymbol.ONEIDENTITY);
					break;
				case ID.Orderless:
					sym.addAttributes(ISymbol.ORDERLESS);
					break;
				case ID.HoldAll:
					sym.addAttributes(ISymbol.HOLDALL);
					break;
				case ID.HoldFirst:
					sym.addAttributes(ISymbol.HOLDFIRST);
					break;
				case ID.HoldRest:
					sym.addAttributes(ISymbol.HOLDREST);
					break;
				case ID.NHoldAll:
					sym.addAttributes(ISymbol.NHOLDALL);
					break;
				case ID.NHoldFirst:
					sym.addAttributes(ISymbol.NHOLDFIRST);
					break;
				case ID.NHoldRest:
					sym.addAttributes(ISymbol.NHOLDREST);
					break;
				case ID.NumericFunction:
					sym.addAttributes(ISymbol.NUMERICFUNCTION);
					break;
				}
			}
		}

	}

	/**
	 * Get the attrbutes of this <code>symbol</code> as symbolic constants in a list.
	 * 
	 * 
	 * @param symbol
	 * @return
	 */
	public static IAST attributesList(final ISymbol symbol) {
		IASTAppendable result = F.ListAlloc(4);
		int attributea = symbol.getAttributes();

		if ((attributea & ISymbol.CONSTANT) != ISymbol.NOATTRIBUTE) {
			result.append(F.Constant);
		}

		if ((attributea & ISymbol.FLAT) != ISymbol.NOATTRIBUTE) {
			result.append(F.Flat);
		}

		if ((attributea & ISymbol.LISTABLE) != ISymbol.NOATTRIBUTE) {
			result.append(F.Listable);
		}

		if ((attributea & ISymbol.ONEIDENTITY) != ISymbol.NOATTRIBUTE) {
			result.append(F.OneIdentity);
		}

		if ((attributea & ISymbol.ORDERLESS) != ISymbol.NOATTRIBUTE) {
			result.append(F.Orderless);
		}

		if ((attributea & ISymbol.HOLDALL) != ISymbol.NOATTRIBUTE) {
			result.append(F.HoldAll);
		} else {
			if ((attributea & ISymbol.HOLDFIRST) != ISymbol.NOATTRIBUTE) {
				result.append(F.HoldFirst);
			}

			if ((attributea & ISymbol.HOLDREST) != ISymbol.NOATTRIBUTE) {
				result.append(F.HoldRest);
			}
		}

		if ((attributea & ISymbol.NHOLDALL) != ISymbol.NOATTRIBUTE) {
			result.append(F.NHoldAll);
		} else {
			if ((attributea & ISymbol.NHOLDFIRST) != ISymbol.NOATTRIBUTE) {
				result.append(F.NHoldFirst);
			}

			if ((attributea & ISymbol.NHOLDREST) != ISymbol.NOATTRIBUTE) {
				result.append(F.NHoldRest);
			}
		}

		if ((attributea & ISymbol.NUMERICFUNCTION) != ISymbol.NOATTRIBUTE) {
			result.append(F.NumericFunction);
		}
		return result;
	}

	static IExpr setSymbolsAttributes(final IAST ast, EvalEngine engine, IAST list) {
		IExpr arg2 = engine.evaluate(ast.arg2());
		for (int i = 1; i < list.size(); i++) {
			if (list.get(i).isSymbol()) {
				final ISymbol sym = ((ISymbol) list.get(i));
				SetAttributes.addAttributes(sym, ast, arg2, engine);
			}
		}
		return F.Null;
	}

	private final static AttributeFunctions CONST = new AttributeFunctions();

	public static AttributeFunctions initialize() {
		return CONST;
	}

	private AttributeFunctions() {

	}

}
