package org.matheclipse.core.visit;

import java.util.ArrayList;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;

/**
 *  
 */
public class VisitorReplacePart extends AbstractVisitor {
	
	final IExpr fReplaceExpr;
	
	final ArrayList<int[]> fList;

	public VisitorReplacePart(IAST rule) {
		super();
		IExpr fromPositions = rule.arg1();
		this.fReplaceExpr = rule.arg2();
		if (fromPositions.isList()) {
			IAST list = (IAST) fromPositions;
			if (list.isListOfLists()) {
				fList = new ArrayList<int[]>(list.size());
				for (int j = 1; j < list.size(); j++) {
					IAST subList = list.getAST(j);
					int[] fPositions = new int[subList.argSize()];
					for (int k = 1; k < subList.size(); k++) {
						fPositions[k - 1] = Validate.checkIntType(subList, k, Integer.MIN_VALUE);
					}
					fList.add(fPositions);
				}
			} else {
				int[] fPositions = new int[list.argSize()];
				fList = new ArrayList<int[]>(1);
				for (int j = 1; j < list.size(); j++) {
					fPositions[j - 1] = Validate.checkIntType(list, j, Integer.MIN_VALUE);
				}
				fList.add(fPositions);
			}

		} else {
			fList = new ArrayList<int[]>(1);
			int[] fPositions = new int[1];
			fPositions[0] = Validate.checkIntType(rule, 1, Integer.MIN_VALUE);
			fList.add(fPositions);
		}
	}

	private IExpr visitIndex(IAST ast, final int index) {
		int[] fPositions;
		IASTAppendable result = ast.copyAppendable();
		for (int i = 0; i < fList.size(); i++) {
			fPositions = fList.get(i);
			if (index >= fPositions.length) {
				continue;
			}
			
			int position = fPositions[index];
			if (position < 0) {
				position = ast.size() + position;
			}
			if (position >= ast.size() || position < 0) {
				continue;
			}

			if (index == fPositions.length - 1) {
				result.set(position, fReplaceExpr);
			} else {
				IExpr arg = result.get(position);
				if (arg.isAST()) {
					result.set(position, visitIndex((IAST) arg, index + 1));
				}
			}
		}

		return result;
	}

	@Override
	public IExpr visit(IASTMutable ast) {
		return visitIndex(ast, 0);
	}

}
