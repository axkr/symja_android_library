package org.matheclipse.core.reflection.system;

import java.util.ArrayList;
import java.util.List;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class Inner extends AbstractFunctionEvaluator {

	private static class InnerAlgorithm {
		final IExpr f;
		final IExpr g;
		final IExpr head;
		final IAST list1;
		final IAST list2;
		int list2Dim0;

		private InnerAlgorithm(final IExpr f, final IAST list1, final IAST list2, final IExpr g) {
			this.f = f;
			this.list1 = list1;
			this.list2 = list2;
			this.g = g;
			this.head = list2.head();
		}

		private IAST inner() {
			ArrayList<Integer> list1Dimensions = Dimensions.getDimensions(list1, list1.head(), Integer.MAX_VALUE);
			ArrayList<Integer> list2Dimensions = Dimensions.getDimensions(list2, list2.head(), Integer.MAX_VALUE);
			list2Dim0 = list2Dimensions.get(0);
			return recursion(new ArrayList<Integer>(), new ArrayList<Integer>(),
					list1Dimensions.subList(0, list1Dimensions.size() - 1), list2Dimensions.subList(1, list2Dimensions.size()));
		}

		private IAST recursion(ArrayList<Integer> list1Cur, ArrayList<Integer> list2Cur, List<Integer> list1RestDimensions,
				List<Integer> list2RestDimensions) {
			if (list1RestDimensions.size() > 0) {
				IAST newResult = F.ast(head);
				for (int i = 1; i < list1RestDimensions.get(0) + 1; i++) {
					ArrayList<Integer> list1CurClone = (ArrayList<Integer>) list1Cur.clone();
					list1CurClone.add(i);
					newResult.add(recursion(list1CurClone, list2Cur, list1RestDimensions.subList(1, list1RestDimensions.size()),
							list2RestDimensions));
				}
				return newResult;
			} else if (list2RestDimensions.size() > 0) {
				IAST newResult = F.ast(head);
				for (int i = 1; i < list2RestDimensions.get(0) + 1; i++) {
					ArrayList<Integer> list2CurClone = (ArrayList<Integer>) list2Cur.clone();
					list2CurClone.add(i);
					newResult.add(recursion(list1Cur, list2CurClone, list1RestDimensions,
							list2RestDimensions.subList(1, list2RestDimensions.size())));
				}
				return newResult;
			} else {
				IAST part = F.ast(g);
				for (int i = 1; i < list2Dim0 + 1; i++) {
					part.add(summand(list1Cur, list2Cur, i));
				}
				return part;
			}
		}

		private IAST summand(ArrayList<Integer> list1Cur, ArrayList<Integer> list2Cur, final int i) {
			IAST result = F.ast(f);
			ArrayList<Integer> list1CurClone = (ArrayList<Integer>) list1Cur.clone();
			list1CurClone.add(i);
			result.add(list1.getPart(list1CurClone));
			ArrayList<Integer> list2CurClone = (ArrayList<Integer>) list2Cur.clone();
			list2CurClone.add(0, i);
			result.add(list2.getPart(list2CurClone));
			return result;
		}
	}

	public Inner() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkRange(ast, 4, 5);

		if (ast.arg2().isAST() && ast.arg3().isAST()) {
			IExpr f = ast.arg1();
			IAST list1 = (IAST) ast.arg2();
			IAST list2 = (IAST) ast.arg3();
			IExpr g;
			if (ast.size() == 4) {
				g = F.Plus;
			} else {
				g = ast.arg4();
			}
			IExpr head2 = list2.head();
			if (!list1.head().equals(head2)) {
				return null;
			}
			InnerAlgorithm ic = new InnerAlgorithm(f, list1, list2, g);
			return ic.inner();
		}
		return null;
	}
}
