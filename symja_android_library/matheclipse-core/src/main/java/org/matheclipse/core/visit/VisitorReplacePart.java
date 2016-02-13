package org.matheclipse.core.visit;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 *  
 */
public class VisitorReplacePart extends AbstractVisitor {
	final IExpr fReplaceExpr;
	int[] fPositions;

	public VisitorReplacePart(IAST rule) {
		super();
		IExpr fromPositions = rule.arg1();
		this.fReplaceExpr = rule.arg2();
		if (fromPositions.isList()) {
			IAST list = (IAST) fromPositions;
			this.fPositions = new int[list.size() - 1];
			for (int j = 1; j < list.size(); j++) {
				this.fPositions[j - 1] = Validate.checkIntType(list, j);
			}
		} else {
			this.fPositions = new int[1];
			this.fPositions[0] = Validate.checkIntType(rule, 1);
		}
	}

	private IExpr visitIndex(IAST ast, final int index) {
		int position = fPositions[index];
		if (position >= ast.size()) {
			return ast;
		}
		IAST result = ast.clone();
		if (index == fPositions.length - 1) {
			result.set(position, fReplaceExpr);
		} else {
			IExpr arg = result.get(position);
			if (arg.isAST()) {
				result.set(position, visitIndex((IAST) arg, index + 1));
			}
		}
		return result;
	}

	@Override
	public IExpr visit(IAST ast) {
		return visitIndex(ast, 0);
	}

}
