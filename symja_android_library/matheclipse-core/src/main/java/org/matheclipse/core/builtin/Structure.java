package org.matheclipse.core.builtin;

import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;

public class Structure {
	static {
		F.Operate.setEvaluator(new Operate());
		F.Sort.setEvaluator(new Sort());
		F.Through.setEvaluator(new Through());
	}

	private static class Operate extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 3, 4);

			int headDepth = 1;
			if (ast.isAST3()) {
				if (!ast.arg3().isInteger()) {
					return F.NIL;
				}
				try {
					headDepth = ((IInteger) ast.arg3()).toInt();
					if (headDepth < 0) {
						return F.NIL;
					}
				} catch (ArithmeticException ae) {
					return F.NIL;
				}
			}

			IExpr p = ast.arg1();
			IExpr arg2 = ast.arg2();
			if (headDepth == 0) {
				// act like Apply()
				return F.unaryAST1(p, arg2);
			}

			if (!arg2.isAST()) {
				return arg2;
			}

			IExpr expr = arg2;
			for (int i = 1; i < headDepth; i++) {
				expr = expr.head();
				if (!expr.isAST()) {
					// headDepth is higher than the depth of heads in arg2
					// return arg2 unmodified.
					return arg2;
				}
			}

			IAST result = ((IAST) arg2).clone();
			IAST last = result;
			IAST head = result;

			for (int i = 1; i < headDepth; i++) {
				head = ((IAST) head.head()).clone();
				last.set(0, head);
				last = head;
			}

			head.set(0, F.unaryAST1(p, head.head()));
			return result;
		}
	}

	private static class Sort extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			if (ast.arg1().isAST()) {
				final IAST shallowCopy = ((IAST) ast.arg1()).copy();
				if (shallowCopy.size() <= 2) {
					return shallowCopy;
				}
				try {
					if (ast.isAST1()) {
						EvalAttributes.sort(shallowCopy);
					} else {
						// use the 2nd argument as a head for the comparator
						// operation:
						EvalAttributes.sort(shallowCopy, new Predicates.IsBinaryFalse(ast.arg2()));
					}
					return shallowCopy;
				} catch (Exception ex) {

				}
			}

			return F.NIL;
		}
	}

	private static class Through extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			if (ast.arg1().isAST()) {
				IAST arg1AST = (IAST) ast.arg1();
				IExpr arg1Head = arg1AST.head();
				if (arg1Head.isAST()) {

					IAST clonedList;
					IAST arg1HeadAST = (IAST) arg1Head;
					if (ast.isAST2() && !arg1HeadAST.head().equals(ast.arg2())) {
						return arg1AST;
					}
					IAST result = F.ast(arg1HeadAST.head());
					for (int i = 1; i < arg1HeadAST.size(); i++) {
						clonedList = arg1AST.apply(arg1HeadAST.get(i));
						result.append(clonedList);
					}
					return result;
				}
				return arg1AST;
			}
			return ast.arg1();
		}
	}

	final static Structure CONST = new Structure();

	public static Structure initialize() {
		return CONST;
	}

	private Structure() {

	}
}
