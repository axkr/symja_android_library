package org.matheclipse.core.builtin;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.RuleCreationError;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.expression.F;
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

				if (attribute == F.Constant) {
					sym.clearAttributes(ISymbol.CONSTANT);
					return F.Null;
				}

				if (attribute == F.Flat) {
					sym.clearAttributes(ISymbol.FLAT);
					return F.Null;
				}

				if (attribute == F.Listable) {
					sym.clearAttributes(ISymbol.LISTABLE);
					return F.Null;
				}

				if (attribute == F.OneIdentity) {
					sym.clearAttributes(ISymbol.ONEIDENTITY);
					return F.Null;
				}

				if (attribute == F.Orderless) {
					sym.clearAttributes(ISymbol.ORDERLESS);
					return F.Null;
				}

				if (attribute == F.HoldAll) {
					sym.clearAttributes(ISymbol.HOLDALL);
					return F.Null;
				}

				if (attribute == F.HoldFirst) {
					sym.clearAttributes(ISymbol.HOLDFIRST);
					return F.Null;
				}

				if (attribute == F.HoldRest) {
					sym.clearAttributes(ISymbol.HOLDREST);
					return F.Null;
				}

				if (attribute == F.NHoldAll) {
					sym.clearAttributes(ISymbol.NHOLDALL);
					return F.Null;
				}

				if (attribute == F.NHoldFirst) {
					sym.clearAttributes(ISymbol.NHOLDFIRST);
					return F.Null;
				}

				if (attribute == F.NHoldRest) {
					sym.clearAttributes(ISymbol.NHOLDREST);
					return F.Null;
				}

				if (attribute == F.NumericFunction) {
					sym.clearAttributes(ISymbol.NUMERICFUNCTION);
					return F.Null;
				}

			} else {
				if (ast.arg2().isList()) {
					final IAST lst = (IAST) ast.arg2();
					for (int i = 1; i < lst.size(); i++) {
						ISymbol attribute = (ISymbol) lst.get(i);

						if (attribute == F.Constant) {
							sym.clearAttributes(ISymbol.CONSTANT);
							return F.Null;
						}

						if (attribute == F.Flat) {
							sym.clearAttributes(ISymbol.FLAT);
						}

						if (attribute == F.Listable) {
							sym.clearAttributes(ISymbol.LISTABLE);
						}

						if (attribute == F.OneIdentity) {
							sym.clearAttributes(ISymbol.ONEIDENTITY);
						}

						if (attribute == F.Orderless) {
							sym.clearAttributes(ISymbol.ORDERLESS);
						}

						if (attribute == F.HoldAll) {
							sym.clearAttributes(ISymbol.HOLDALL);
						}

						if (attribute == F.HoldFirst) {
							sym.clearAttributes(ISymbol.HOLDFIRST);
						}

						if (attribute == F.HoldRest) {
							sym.clearAttributes(ISymbol.HOLDREST);
						}

						if (attribute == F.NHoldAll) {
							sym.clearAttributes(ISymbol.NHOLDALL);
						}

						if (attribute == F.NHoldFirst) {
							sym.clearAttributes(ISymbol.NHOLDFIRST);
						}

						if (attribute == F.NHoldRest) {
							sym.clearAttributes(ISymbol.NHOLDREST);
						}

						if (attribute == F.NumericFunction) {
							sym.clearAttributes(ISymbol.NUMERICFUNCTION);
						}
					}

					return F.Null;
				}
			}
			return F.Null;
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

				if (attribute == F.Constant) {
					sym.addAttributes(ISymbol.CONSTANT);
					return F.Null;
				}

				if (attribute == F.Flat) {
					sym.addAttributes(ISymbol.FLAT);
					return F.Null;
				}

				if (attribute == F.Listable) {
					sym.addAttributes(ISymbol.LISTABLE);
					return F.Null;
				}

				if (attribute == F.OneIdentity) {
					sym.addAttributes(ISymbol.ONEIDENTITY);
					return F.Null;
				}

				if (attribute == F.Orderless) {
					sym.addAttributes(ISymbol.ORDERLESS);
					return F.Null;
				}

				if (attribute == F.HoldAll) {
					sym.addAttributes(ISymbol.HOLDALL);
					return F.Null;
				}

				if (attribute == F.HoldFirst) {
					sym.addAttributes(ISymbol.HOLDFIRST);
					return F.Null;
				}

				if (attribute == F.HoldRest) {
					sym.addAttributes(ISymbol.HOLDREST);
					return F.Null;
				}

				if (attribute == F.NHoldAll) {
					sym.addAttributes(ISymbol.NHOLDALL);
					return F.Null;
				}

				if (attribute == F.NHoldFirst) {
					sym.addAttributes(ISymbol.NHOLDFIRST);
					return F.Null;
				}

				if (attribute == F.NHoldRest) {
					sym.addAttributes(ISymbol.NHOLDREST);
					return F.Null;
				}

				if (attribute == F.NumericFunction) {
					sym.addAttributes(ISymbol.NUMERICFUNCTION);
					return F.Null;
				}

			} else {
				if (ast.arg2().isList()) {
					final IAST lst = (IAST) ast.arg2();
					for (int i = 1; i < lst.size(); i++) {
						ISymbol attribute = (ISymbol) lst.get(i);
						if (attribute == F.Flat) {
							sym.addAttributes(ISymbol.FLAT);
						}

						if (attribute == F.Listable) {
							sym.addAttributes(ISymbol.LISTABLE);
						}

						if (attribute == F.OneIdentity) {
							sym.addAttributes(ISymbol.ONEIDENTITY);
						}

						if (attribute == F.Orderless) {
							sym.addAttributes(ISymbol.ORDERLESS);
						}

						if (attribute == F.HoldAll) {
							sym.addAttributes(ISymbol.HOLDALL);
						}

						if (attribute == F.HoldFirst) {
							sym.addAttributes(ISymbol.HOLDFIRST);
						}

						if (attribute == F.HoldRest) {
							sym.addAttributes(ISymbol.HOLDREST);
						}

						if (attribute == F.NHoldAll) {
							sym.addAttributes(ISymbol.NHOLDALL);
						}

						if (attribute == F.NHoldFirst) {
							sym.addAttributes(ISymbol.NHOLDFIRST);
						}

						if (attribute == F.NHoldRest) {
							sym.addAttributes(ISymbol.NHOLDREST);
						}

						if (attribute == F.NumericFunction) {
							sym.addAttributes(ISymbol.NUMERICFUNCTION);
						}

					}
					// end for

					return F.Null;
				}
			}
			return F.Null;
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
