package org.matheclipse.core.visit;

import java.util.IdentityHashMap;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

/**
 * Replace all occurrences of expressions where the given
 * <code>function.apply()</code> method returns a non <code>F.NIL</code> value.
 * The visitors <code>visit()</code> methods return <code>F.NIL</code> if no
 * substitution occurred.
 */
public class ModuleReplaceAll extends VisitorExpr {
	final IdentityHashMap<ISymbol, ISymbol> fModuleVariables;
	final int fOffset;

	public ModuleReplaceAll(IdentityHashMap<ISymbol, ISymbol> moduleVariables) {
		this(moduleVariables, 0);
	}

	public ModuleReplaceAll(IdentityHashMap<ISymbol, ISymbol> moduleVariables, int offset) {
		this.fModuleVariables = moduleVariables;
		this.fOffset = offset;
	}

	private IExpr apply(final IExpr arg) {
		IExpr temp = fModuleVariables.get(arg);
		return temp != null ? temp : F.NIL;
	}

	/**
	 * 
	 * @return <code>F.NIL</code>, if no evaluation is possible
	 */
	public IExpr visit(ISymbol element) {
		return apply(element);
	}

	@Override
	public IExpr visit(IAST ast) {
		IdentityHashMap<ISymbol, ISymbol> variables = null;
		if (ast.isASTSizeGE(F.Block, 2) && ast.arg1().isList()) {

			IAST localVariablesList = (IAST) ast.arg1();
			for (int i = 1; i < localVariablesList.size(); i++) {
				if (localVariablesList.get(i).isSymbol()) {
					if (fModuleVariables.get((ISymbol) localVariablesList.get(i)) != null) {
						if (variables == null) {
							variables = (IdentityHashMap<ISymbol, ISymbol>) fModuleVariables.clone();
						}
						variables.remove((ISymbol) localVariablesList.get(i));
					}
				} else {
					if (localVariablesList.get(i).isAST(F.Set, 3)) {
						// lhs = rhs
						final IAST setFun = (IAST) localVariablesList.get(i);
						if (setFun.arg1().isSymbol()) {
							if (fModuleVariables.get((ISymbol) setFun.arg1()) != null) {
								if (variables == null) {
									variables = (IdentityHashMap<ISymbol, ISymbol>) fModuleVariables.clone();
								}
								variables.remove((ISymbol) setFun.arg1());
							}
						}
					}
				}
			}

		}

		ModuleReplaceAll visitor = null;
		if (variables == null) {
			visitor = this;
		} else {
			visitor = new ModuleReplaceAll(variables);
		}
		IExpr temp;
		IAST result = F.NIL;
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
}
