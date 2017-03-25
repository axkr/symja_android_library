package org.matheclipse.core.builtin;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiPredicate;

import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Comparators;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public final class ListFunctions {

	static {
		F.Complement.setEvaluator(new Complement());
		F.Composition.setEvaluator(new Composition());
		F.DeleteDuplicates.setEvaluator(new DeleteDuplicates());
		F.Intersection.setEvaluator(new Intersection());
		F.Join.setEvaluator(new Join());
		F.Union.setEvaluator(new Union());
	}

	private final static class Complement extends AbstractFunctionEvaluator {

		public Complement() {
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);

			if (!ast.arg1().isAtom() && !ast.arg2().isAtom()) {

				final IAST arg1 = ((IAST) ast.arg1());
				final IAST arg2 = ((IAST) ast.arg2());
				return complement(arg1, arg2);
			}
			return F.NIL;
		}

		public static IExpr complement(final IAST arg1, final IAST arg2) {
			IAST result = F.List();
			Set<IExpr> set2 = arg2.asSet();
			Set<IExpr> set3 = new HashSet<IExpr>();
			for (int i = 1; i < arg1.size(); i++) {
				IExpr temp = arg1.get(i);
				if (!set2.contains(temp)) {
					set3.add(temp);
				}
			}
			for (IExpr expr : set3) {
				result.append(expr);
			}
			EvalAttributes.sort(result);
			return result;
		}
	}

	private final static class Composition extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.head().equals(F.Composition)) {
				return F.NIL;
			}
			if (ast.head().isAST()) {

				IAST headList = (IAST) ast.head();
				if (headList.size() > 1) {
					IAST inner = F.ast(headList.get(1));
					IAST result = inner;
					IAST temp;
					for (int i = 2; i < headList.size(); i++) {
						temp = F.ast(headList.get(i));
						inner.append(temp);
						inner = temp;
					}
					for (int i = 1; i < ast.size(); i++) {
						inner.append(ast.get(i));
					}
					return result;
				}
				
			}
			return F.NIL;
		}

	}

	/**
	 * Delete duplicate values from a list.
	 */
	private final static class DeleteDuplicates extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			IExpr test = F.Equal;
			if (ast.isAST2()) {
				test = ast.arg2();
			}
			if (ast.arg1().isList()) {
				IAST list = (IAST) ast.arg1();
				final IAST result = F.List();
				IExpr temp;
				boolean evaledTrue;
				BiPredicate<IExpr, IExpr> biPredicate = Predicates.isBinaryTrue(test);
				for (int i = 1; i < list.size(); i++) {
					temp = list.get(i);
					evaledTrue = false;
					for (int j = 1; j < result.size(); j++) {
						if (biPredicate.test(result.get(j), temp)) {
							evaledTrue = true;
							break;
						}
					}
					if (evaledTrue) {
						continue;
					}
					result.append(temp);
				}
				return result;
			}
			return F.NIL;
		}

	}

	/**
	 * Intersection of 2 sets
	 * 
	 * See: <a href=
	 * "http://en.wikipedia.org/wiki/Intersection_(set_theory)">Intersection
	 * (set theory)</a>
	 */
	private final static class Intersection extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			if (ast.isAST1() && ast.arg1().isAST()) {
				final IAST result = F.List();
				IAST arg1 = (IAST) ast.arg1();
				Set<IExpr> set = arg1.asSet();
				for (IExpr IExpr : set) {
					result.append(IExpr);
				}
				EvalAttributes.sort(result, Comparators.ExprComparator.CONS);
				return result;
			}

			if (ast.arg1().isAST() && ast.arg2().isAST()) {
				IAST arg1AST = ((IAST) ast.arg1());
				IAST arg2AST = ((IAST) ast.arg2());
				final IAST result = F.List();
				return intersection(arg1AST, arg2AST, result);
			}

			return F.NIL;
		}

		/**
		 * Create the (ordered) intersection set from both ASTs.
		 * 
		 * @param ast1
		 *            first AST set
		 * @param ast2
		 *            second AST set
		 * @param result
		 *            the AST where the elements of the union should be appended
		 * @return
		 */
		public static IExpr intersection(IAST ast1, IAST ast2, final IAST result) {
			Set<IExpr> set1 = new HashSet<IExpr>(ast1.size() + ast1.size() / 10);
			Set<IExpr> set2 = new HashSet<IExpr>(ast2.size() + ast1.size() / 10);
			Set<IExpr> resultSet = new TreeSet<IExpr>();
			int size = ast1.size();
			for (int i = 1; i < size; i++) {
				set1.add(ast1.get(i));
			}
			size = ast2.size();
			for (int i = 1; i < size; i++) {
				set2.add(ast2.get(i));
			}
			for (IExpr expr : set1) {
				if (set2.contains(expr)) {
					resultSet.add(expr);
				}
			}
			for (IExpr expr : resultSet) {
				result.append(expr);
			}
			return result;
		}
	}

	private final static class Join extends AbstractFunctionEvaluator {

		public Join() {
			super();
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 3);
			if (ast.args().any(PredicateQ.ATOMQ)) {
				return F.NIL;
			}

			int astSize = ast.size();
			int size = 0;
			for (int i = 1; i < astSize; i++) {
				size += ((IAST) ast.get(i)).size() - 1;
			}
			final IAST result = F.ListAlloc(size);
			for (int i = 1; i < ast.size(); i++) {
				result.appendArgs((IAST) ast.get(i));
			}
			return result;
		}
	}

	/**
	 * Union of two sets. See
	 * <a href="http://en.wikipedia.org/wiki/Union_(set_theory)">Union (set
	 * theory)</a>
	 */
	private final static class Union extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			if (ast.isAST1() && ast.arg1().isAST()) {
				final IAST result = F.List();
				IAST arg1 = (IAST) ast.arg1();
				Set<IExpr> set = arg1.asSet();
				for (IExpr IExpr : set) {
					result.append(IExpr);
				}
				EvalAttributes.sort(result, Comparators.ExprComparator.CONS);
				return result;
			}

			if (ast.arg1().isAST() && ast.arg2().isAST()) {
				IAST arg1AST = ((IAST) ast.arg1());
				IAST arg2AST = ((IAST) ast.arg2());
				final IAST result = F.List();
				return union(arg1AST, arg2AST, result);
			}
			return F.NIL;
		}

		/**
		 * Create the (ordered) union from both ASTs.
		 * 
		 * @param ast1
		 *            first AST set
		 * @param ast2
		 *            second AST set
		 * @param result
		 *            the AST where the elements of the union should be appended
		 * @return
		 */
		public static IExpr union(IAST ast1, IAST ast2, final IAST result) {
			Set<IExpr> resultSet = new TreeSet<IExpr>();
			int size = ast1.size();
			for (int i = 1; i < size; i++) {
				resultSet.add(ast1.get(i));
			}
			size = ast2.size();
			for (int i = 1; i < size; i++) {
				resultSet.add(ast2.get(i));
			}
			for (IExpr expr : resultSet) {
				result.append(expr);
			}
			return result;
		}

	}

	final static ListFunctions CONST = new ListFunctions();

	public static ListFunctions initialize() {
		return CONST;
	}

	private ListFunctions() {

	}
}
