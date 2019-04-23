package org.matheclipse.core.visit;

import java.util.function.Function;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Replace all occurrences of expressions where the given <code>function.apply()</code> method returns a non
 * <code>F.NIL</code> value. The visitors <code>visit()</code> methods return <code>F.NIL</code> if no substitution
 * occurred.
 */
public class VisitorPlusTimesPowerReplaceAll extends VisitorReplaceAll {

	public VisitorPlusTimesPowerReplaceAll(Function<IExpr, IExpr> function) {
		super(function, 1);
	}

	public VisitorPlusTimesPowerReplaceAll(IAST ast) {
		super(ast, 1);
	}

	@Override
	public IExpr visit(IASTMutable ast) {
		if (ast.isPlusTimesPower()) {
			return visitAST(ast);
		}
		return fFunction.apply(ast);
	}

	@Override
	protected IExpr visitAST(IAST ast) {
		IASTMutable result = F.NIL;
		if (ast.isPower()) {
			IExpr base = ast.base().accept(this);
			return base.isPresent() ? ast.setAtCopy(1, base) : F.NIL;
		}
		int i = fOffset;
		int size = ast.size();
		IExpr temp;
		while (i < size) {
			temp = ast.get(i).accept(this);
			if (temp.isPresent()) {
				// something was evaluated - return a new IAST:
				result = ast.setAtCopy(i++, temp);
				break;
			}
			i++;
		}
		if (result.isPresent()) {
			while (i < size) {
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
