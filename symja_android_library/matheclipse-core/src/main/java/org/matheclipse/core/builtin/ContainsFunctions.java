package org.matheclipse.core.builtin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.hipparchus.exception.MathIllegalArgumentException;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractNonOrderlessArgMultiple;
import org.matheclipse.core.eval.util.Options;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.ISymbol;

public class ContainsFunctions {

	private static class Initializer {

		private static void init() {
			F.ContainsAny.setEvaluator(new ContainsAny());
			F.ContainsAll.setEvaluator(ContainsAll.CONST);
			F.ContainsExactly.setEvaluator(new ContainsExactly());
			F.ContainsNone.setEvaluator(new ContainsNone());
			F.ContainsOnly.setEvaluator(ContainsOnly.CONST);
		}

	}

	private static class ContainsAny extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST1()) {
				return F.operatorFormAppend(ast);
			}
			IExpr sameTest = F.SameQ;
			if (ast.arg1().isList() && ast.arg2().isList()) {
				if (ast.isAST3()) {
					// determine option SameTest
					final Options options = new Options(ast.topHead(), ast, 2, engine);
					IExpr option = options.getOption(F.SameTest);
					if (option.isPresent()) {
						sameTest = option;
					}
				}
				IAST list1 = (IAST) ast.arg1();
				IAST list2 = (IAST) ast.arg2();
				return containsFunction(list1, list2, sameTest, engine);
			}
			return F.NIL;
		}

		public IExpr containsFunction(IAST list1, IAST list2, IExpr sameTest, EvalEngine engine) {
			for (int i = 1; i < list1.size(); i++) {
				IExpr list1Arg = list1.get(i);
				for (int j = 1; j < list2.size(); j++) {
					IExpr list2Arg = list2.get(j);
					if (engine.evalTrue(F.binaryAST2(sameTest, list1Arg, list2Arg))) {
						return F.True;
					}
				}
			}
			return F.False;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_3;
		}
	}

	private final static class ContainsExactly extends ContainsAny {

		public IExpr containsFunction(IAST list1, IAST list2, IExpr sameTest, EvalEngine engine) {
			if (ContainsAll.CONST.containsFunction(list1, list2, sameTest, engine).isTrue()) {
				if (ContainsOnly.CONST.containsFunction(list1, list2, sameTest, engine).isTrue()) {
					return F.True;
				}
			}
			return F.False;
		}

	}

	private final static class ContainsAll extends ContainsAny {
		final static ContainsAll CONST = new ContainsAll();

		public IExpr containsFunction(IAST list1, IAST list2, IExpr sameTest, EvalEngine engine) {
			boolean evaledTrue;
			for (int i = 1; i < list2.size(); i++) {
				IExpr list2Arg = list2.get(i);
				evaledTrue = false;
				for (int j = 1; j < list1.size(); j++) {
					IExpr list1Arg = list1.get(j);
					if (engine.evalTrue(F.binaryAST2(sameTest, list1Arg, list2Arg))) {
						evaledTrue = true;
						break;
					}
				}
				if (!evaledTrue) {
					return F.False;
				}
			}
			return F.True;
		}

	}

	private final static class ContainsOnly extends ContainsAny {
		final static ContainsOnly CONST = new ContainsOnly();

		public IExpr containsFunction(IAST list1, IAST list2, IExpr sameTest, EvalEngine engine) {
			boolean evaledTrue;
			for (int i = 1; i < list1.size(); i++) {
				IExpr list1Arg = list1.get(i);
				evaledTrue = false;
				for (int j = 1; j < list2.size(); j++) {
					IExpr list2Arg = list2.get(j);
					if (engine.evalTrue(F.binaryAST2(sameTest, list1Arg, list2Arg))) {
						evaledTrue = true;
						break;
					}
				}
				if (!evaledTrue) {
					return F.False;
				}
			}
			return F.True;
		}

	}

	private final static class ContainsNone extends ContainsAny {

		public IExpr containsFunction(IAST list1, IAST list2, IExpr sameTest, EvalEngine engine) {
			for (int i = 1; i < list1.size(); i++) {
				IExpr list1Arg = list1.get(i);
				for (int j = 1; j < list2.size(); j++) {
					IExpr list2Arg = list2.get(j);
					if (engine.evalTrue(F.binaryAST2(sameTest, list1Arg, list2Arg))) {
						return F.False;
					}
				}
			}
			return F.True;
		}

	}

	public static void initialize() {
		Initializer.init();
	}

	private ContainsFunctions() {

	}

}
