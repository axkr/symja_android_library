package org.matheclipse.core.visit;

import java.util.IdentityHashMap;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.IPatternSequence;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Replace all occurrences of expressions where the given <code>function.apply()</code> method returns a non
 * <code>F.NIL</code> value. The visitors <code>visit()</code> methods return <code>F.NIL</code> if no substitution
 * occurred.
 */
public class ModuleReplaceAll extends VisitorExpr {
	final IdentityHashMap<ISymbol, ? extends IExpr> fModuleVariables;
	final int fOffset;
	final EvalEngine fEngine;
	final String moduleCounter;

	public ModuleReplaceAll(IdentityHashMap<ISymbol, ? extends IExpr> moduleVariables, EvalEngine engine,
			String moduleCounter) {
		this(moduleVariables, engine, moduleCounter, 0);
	}

	public ModuleReplaceAll(IdentityHashMap<ISymbol, ? extends IExpr> moduleVariables, EvalEngine engine,
			String moduleCounter, int offset) {
		this.fModuleVariables = moduleVariables;
		this.fOffset = offset;
		this.fEngine = engine;
		this.moduleCounter = moduleCounter;
	}

	private IExpr apply(final ISymbol arg) {
		IExpr temp = fModuleVariables.get(arg);
		return temp != null ? temp : F.NIL;
	}

	@Override
	public IExpr visit(ISymbol element) {
		return apply(element);
	}

	@Override
	public IExpr visit(IPattern element) {
		ISymbol symbol = element.getSymbol();
		if (symbol != null) {
			IExpr expr = apply(symbol);
			if (expr.isSymbol()) {
				return F.$p((ISymbol) expr, element.getHeadTest(), element.isPatternDefault());
			}
		}
		return F.NIL;
	}

	@Override
	public IExpr visit(IPatternSequence element) {
		ISymbol symbol = element.getSymbol();
		if (symbol != null) {
			IExpr expr = apply(symbol);
			if (expr.isPresent() && expr.isSymbol()) {
				return F.$ps((ISymbol) expr, element.getHeadTest(), element.isDefault(), element.isNullSequence());
			}
		}
		return F.NIL;
	}

	@Override
	public IExpr visit(IASTMutable ast) {
		if (ast.isSameHeadSizeGE(F.Function, 2)) {
			return visitNestedScope(ast, true);
		} else if (ast.isWith()) {
			return visitNestedScope(ast, false).orElse(ast);
		} else if (ast.isModule()) {
			return visitNestedScope(ast, false).orElse(ast);
		}
		return visitASTModule(ast);
	}

	/**
	 * Handle nested Module(), With() or Function()
	 * 
	 * @param ast
	 * @param isFunction
	 *            <code>ast</code> has the form <code>Function(a1, a2)</code>
	 * @return
	 */
	private IAST visitNestedScope(IAST ast, boolean isFunction) {
		IASTMutable result = F.NIL;
		IAST localVariablesList =  F.NIL;
		if (ast.arg1().isSymbol()) {
			localVariablesList = F.List(ast.arg1());
		} else if (ast.arg1().isList()) {
			localVariablesList = (IAST) ast.arg1();
		}
		ModuleReplaceAll visitor = this;
		if (localVariablesList.isPresent()) {
			IdentityHashMap<ISymbol, IExpr> variables = renamedVariables(localVariablesList, isFunction);
			if (variables != null) {
				visitor = new ModuleReplaceAll(variables, fEngine, moduleCounter);
			}
		}

		IExpr temp;

		int i = fOffset;
		while (i < ast.size()) {
			temp = ast.get(i).accept(visitor);
			if (temp.isPresent()) {
				// something was evaluated - return a new IAST:
				result = ast.copy();
				result.set(i++, temp);
				break;
			}
			i++;
		}
		if (result.isPresent()) {
			while (i < ast.size()) {
				temp = ast.get(i).accept(visitor);
				if (temp.isPresent()) {
					result.set(i, temp);
				}
				i++;
			}
		}

		return result;

	}

	private IdentityHashMap<ISymbol, IExpr> renamedVariables(IAST localVariablesList, boolean isFunction) {
		IdentityHashMap<ISymbol, IExpr> variables = null;
		final String varAppend = moduleCounter;
		int size = localVariablesList.size();
		for (int i = 1; i < size; i++) {
			IExpr temp = localVariablesList.get(i);
			if (temp.isSymbol()) {
				ISymbol symbol = (ISymbol) temp;
				if (isFunction || fModuleVariables.get(symbol) != null) {

					if (variables == null) {
						variables = (IdentityHashMap<ISymbol, IExpr>) fModuleVariables.clone();
					}
					variables.remove(symbol);
					if (isFunction) {
						variables.put(symbol, F.Dummy(symbol.toString() + varAppend));
					}
				}
			} else {
				if (temp.isAST(F.Set, 3)) {
					// lhs = rhs
					final IAST setFun = (IAST) temp;
					if (setFun.arg1().isSymbol()) {
						ISymbol symbol = (ISymbol) setFun.arg1();
						if (isFunction || fModuleVariables.get(symbol) != null) {
							if (variables == null) {
								variables = (IdentityHashMap<ISymbol, IExpr>) fModuleVariables.clone();
							}
							variables.remove(symbol);
							if (isFunction) {
								variables.put(symbol, F.Dummy(symbol.toString() + varAppend));
							}
						}
					}
				}
			}
		}

		return variables;
	}

	private IExpr visitASTModule(IAST ast) {
		IExpr temp;
		IASTMutable result = F.NIL;
		int i = fOffset;
		while (i < ast.size()) {
			temp = ast.get(i).accept(this);
			if (temp.isPresent()) {
				// something was evaluated - return a new IAST:
				result = ast.copy();
				result.set(i++, temp);
				break;
			}
			i++;
		}
		if (result.isPresent()) {
			while (i < ast.size()) {
				temp = ast.get(i).accept(this);
				if (temp.isPresent()) {
					result.set(i, temp);
				}
				i++;
			}
		}
		return result;
	}
}
