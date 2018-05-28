package org.matheclipse.core.builtin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Predicate;

import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ReturnException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongNumberOfArguments;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.Lambda;
import org.matheclipse.core.eval.util.OpenFixedSizeMap;
import org.matheclipse.core.eval.util.Options;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.ISymbol2IntMap;
import org.matheclipse.core.visit.AbstractVisitorLong;
import org.matheclipse.core.visit.IndexedLevel;
import org.matheclipse.core.visit.VisitorLevelSpecification;
import org.matheclipse.parser.client.math.MathException;

public class Structure {

	static {
		F.Apply.setEvaluator(new Apply());
		F.Depth.setEvaluator(new Depth());
		F.Flatten.setEvaluator(new Flatten());
		F.FlattenAt.setEvaluator(new FlattenAt());
		F.Function.setEvaluator(new Function());
		F.Head.setEvaluator(new Head());
		F.LeafCount.setEvaluator(new LeafCount());
		F.Map.setEvaluator(new Map());
		F.MapAll.setEvaluator(new MapAll());
		F.MapAt.setEvaluator(new MapAt());
		F.MapIndexed.setEvaluator(new MapIndexed());
		F.MapThread.setEvaluator(new MapThread());
		F.Order.setEvaluator(new Order());
		F.OrderedQ.setEvaluator(new OrderedQ());
		F.Operate.setEvaluator(new Operate());
		F.Quit.setEvaluator(new Quit());
		F.Scan.setEvaluator(new Scan());
		F.Sort.setEvaluator(new Sort());
		F.Symbol.setEvaluator(new Symbol());
		F.SymbolName.setEvaluator(new SymbolName());
		F.Thread.setEvaluator(new Thread());
		F.Through.setEvaluator(new Through());
	}

	/**
	 * <pre>
	 * Apply(f, expr)
	 * 
	 * f @@ expr
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * replaces the head of <code>expr</code> with <code>f</code>.
	 * </p>
	 * 
	 * <pre>
	 * Apply(f, expr, levelspec)
	 * </pre>
	 * <p>
	 * applies <code>f</code> on the parts specified by <code>levelspec</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; f @@ {1, 2, 3}
	 * f(1, 2, 3)
	 * &gt;&gt; Plus @@ {1, 2, 3}
	 * 6
	 * </pre>
	 * <p>
	 * The head of $expr$ need not be 'List':
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; f @@ (a + b + c)
	 * f(a, b, c)
	 * </pre>
	 * <p>
	 * Apply on level 1:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Apply(f, {a + b, g(c, d, e * f), 3}, {1})
	 * {f(a, b), f(c, d, e*f), 3}
	 * </pre>
	 * <p>
	 * The default level is 0:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Apply(f, {a, b, c}, {0})
	 * f(a, b, c)
	 * </pre>
	 * <p>
	 * Range of levels, including negative level (counting from bottom):
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Apply(f, {{{{{a}}}}}, {2, -3})
	 * {{f(f({a}))}}
	 * </pre>
	 * <p>
	 * Convert all operations to lists:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Apply(List, a + b * c ^ e * f(g), {0, Infinity})
	 * {a,{b,{c,e},{g}}}
	 * </pre>
	 * <p>
	 * Level specification x + y is not of the form n, {n}, or {m, n}.
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Apply(f, {a, b, c}, x+y) 
	 * Apply(f, {a, b, c}, x + y)
	 * </pre>
	 */
	private final static class Apply extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 3, 5);

			IASTAppendable evaledAST = ast.copyAppendable();
			evaledAST.setArgs(evaledAST.size(), i -> engine.evaluate(evaledAST.get(i)));
			// for (int i = 1; i < evaledAST.size(); i++) {
			// evaledAST.set(i, engine.evaluate(evaledAST.get(i)));
			// }
			int lastIndex = evaledAST.argSize();
			boolean heads = false;
			final Options options = new Options(evaledAST.topHead(), evaledAST, lastIndex, engine);
			IExpr option = options.getOption("Heads");
			if (option.isPresent()) {
				lastIndex--;
				if (option.isTrue()) {
					heads = true;
				}
			} else {
				Validate.checkRange(evaledAST, 3, 4);
			}

			IExpr arg1 = evaledAST.arg1();
			IExpr arg2 = evaledAST.arg2();
			return evalApply(arg1, arg2, evaledAST, lastIndex, heads, engine);
		}

		public static IExpr evalApply(IExpr arg1, IExpr arg2, IAST evaledAST, int lastIndex, boolean heads,
				EvalEngine engine) {
			VisitorLevelSpecification level = null;
			java.util.function.Function<IExpr, IExpr> af = x -> x.isAST() ? ((IAST) x).setAtCopy(0, arg1) : F.NIL;
			try {
				if (lastIndex == 3) {
					level = new VisitorLevelSpecification(af, evaledAST.get(lastIndex), heads, engine);
				} else {
					level = new VisitorLevelSpecification(af, 0);
				}

				if (!arg2.isAtom()) {
					return arg2.accept(level).orElse(arg2);
				} else if (evaledAST.isAST2()) {
					if (arg1.isFunction()) {
						return F.unaryAST1(arg1, arg2);
					}
					return arg2;
				}
			} catch (final MathException e) {
				engine.printMessage(e.getMessage());
			} catch (final ArithmeticException e) {

			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	/**
	 * <pre>
	 * Depth(expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * gives the depth of <code>expr</code>.
	 * </p>
	 * </blockquote>
	 * <p>
	 * The depth of an expression is defined as one plus the maximum number of <code>Part</code> indices required to
	 * reach any part of <code>expr</code>, except for heads.
	 * </p>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Depth(x)
	 * 1
	 * 
	 * &gt;&gt; Depth(x + y)
	 * 2
	 * 
	 * &gt;&gt; Depth({{{{x}}}})
	 * 5
	 * </pre>
	 * <p>
	 * Complex numbers are atomic, and hence have depth 1:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Depth(1 + 2*I)
	 * 1
	 * </pre>
	 * <p>
	 * <code>Depth</code> ignores heads:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Depth(f(a, b)[c])
	 * 2
	 * </pre>
	 */
	private final static class Depth extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			final IExpr arg1 = engine.evaluate(ast.arg1());
			if (!(arg1.isAST())) {
				return F.C1;
			}
			return F.integer(depth((IAST) ast.arg1(), 1));
		}

		/**
		 * Calculates the depth of an expression. Atomic expressions (no sublists) have depth <code>1</code> Example:
		 * the nested list <code>[x,[y]]</code> has depth <code>3</code>
		 * 
		 * @param headOffset
		 * 
		 */
		public static int depth(final IAST list, int headOffset) {
			int maxDepth = 1;
			int d;
			for (int i = headOffset; i < list.size(); i++) {
				if (list.get(i).isAST()) {
					d = depth((IAST) list.get(i), headOffset);
					if (d > maxDepth) {
						maxDepth = d;
					}
				}
			}
			return ++maxDepth;
		}

	}

	/**
	 * <pre>
	 * Flatten(expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * flattens out nested lists in <code>expr</code>.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * Flatten(expr, n)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * stops flattening at level <code>n</code>.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * Flatten(expr, n, h)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * flattens expressions with head <code>h</code> instead of 'List'.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Flatten({{a, b}, {c, {d}, e}, {f, {g, h}}})
	 * {a, b, c, d, e, f, g, h}
	 * &gt;&gt; Flatten({{a, b}, {c, {e}, e}, {f, {g, h}}}, 1)
	 * {a, b, c, {e}, e, f, {g, h}}
	 * &gt;&gt; Flatten(f(a, f(b, f(c, d)), e), Infinity, f)
	 * f(a, b, c, d, e)
	 * &gt;&gt; Flatten({{a, b}, {c, d}}, {{2}, {1}})
	 * {{a, c}, {b, d}}
	 * &gt;&gt; Flatten({{a, b}, {c, d}}, {{1, 2}})
	 * {a, b, c, d}
	 * </pre>
	 * <p>
	 * Flatten also works in irregularly shaped arrays
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Flatten({{1, 2, 3}, {4}, {6, 7}, {8, 9, 10}}, {{2}, {1}})
	 * {{1, 4, 6, 8}, {2, 7, 9}, {3, 10}}
	 * 
	 * &gt;&gt; Flatten({{{111, 112, 113}, {121, 122}}, {{211, 212}, {221, 222, 223}}}, {{3}, {1}, {2}})
	 * {{{111, 121}, {211, 221}}, {{112, 122}, {212, 222}}, {{113}, {223}}}
	 * 
	 * &gt;&gt; Flatten({{{1, 2, 3}, {4, 5}}, {{6, 7}, {8, 9,  10}}}, {{3}, {1}, {2}})
	 * {{{1, 4}, {6, 8}}, {{2, 5}, {7, 9}}, {{3}, {10}}}
	 * 
	 * &gt;&gt; Flatten({{{1, 2, 3}, {4, 5}}, {{6, 7}, {8, 9, 10}}}, {{2}, {1, 3}})
	 * {{1, 2, 3, 6, 7}, {4, 5, 8, 9, 10}}
	 * 
	 * &gt;&gt; Flatten({{1, 2}, {3,4}}, {1, 2})
	 * {1, 2, 3, 4}
	 * </pre>
	 * <p>
	 * Levels to be flattened together in {{-1, 2}} should be lists of positive integers.
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Flatten({{1, 2}, {3, 4}}, {{-1, 2}})
	 * Flatten({{1, 2}, {3, 4}}, {{-1, 2}}, List)
	 * </pre>
	 * <p>
	 * Level 2 specified in {{1}, {2}} exceeds the levels, 1, which can be flattened together in {a, b}.
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Flatten({a, b}, {{1}, {2}})
	 * Flatten({a, b}, {{1}, {2}}, List)
	 * </pre>
	 * <p>
	 * Check <code>n</code> completion
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; m = {{{1, 2}, {3}}, {{4}, {5, 6}}}
	 * &gt;&gt; Flatten(m, {2})
	 * {{{1, 2}, {4}}, {{3}, {5, 6}}}
	 * &gt;&gt; Flatten(m, {{2}})
	 * {{{1, 2}, {4}}, {{3}, {5, 6}}}
	 * &gt;&gt; Flatten(m, {{2}, {1}})
	 * {{{1, 2}, {4}}, {{3}, {5, 6}}}
	 * &gt;&gt; Flatten(m, {{2}, {1}, {3}})
	 * {{{1, 2}, {4}}, {{3}, {5, 6}}}
	 * </pre>
	 * <p>
	 * Level 4 specified in {{2}, {1}, {3}, {4}} exceeds the levels, 3, which can be flattened together in {{{1, 2},
	 * {3}}, {{4}, {5, 6}}}.
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Flatten(m, {{2}, {1}, {3}, {4}})
	 * Flatten({{{1, 2}, {3}}, {{4}, {5, 6}}}, {{2}, {1}, {3}, {4}}, List)
	 * </pre>
	 * 
	 * <pre>
	 * &gt;&gt; m{{1, 2, 3}, {4, 5, 6}, {7, 8, 9}};
	 * &gt;&gt; Flatten(m, {1})
	 * {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}
	 * &gt;&gt; Flatten(m, {2})
	 * {{1, 4, 7}, {2, 5, 8}, {3, 6, 9}}
	 * &gt;&gt; Flatten(m, {3})
	 *  : Level 3 specified in {3} exceeds the levels, 2, which can be flattened together in {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}.
	 * Flatten({{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}, {3}, List)
	 * &gt;&gt; Flatten(m, {2, 1})
	 * {1, 4, 7, 2, 5, 8, 3, 6, 9}
	 * Reproduce strange head behaviour
	 * &gt;&gt; Flatten({{1}, 2}, {1, 2})
	 *  : Level 2 specified in {1, 2} exceeds the levels, 1, which can be flattened together in {{1}, 2}.
	 * Flatten({{1}, 2}, {1, 2}, List)
	 * &gt;&gt; Flatten(a(b(1, 2), b(3)), {1, 2}, b)     (* MMA BUG: {{1, 2}} not {1, 2}  *)
	 *  : Level 1 specified in {1, 2} exceeds the levels, 0, which can be flattened together in a(b(1, 2), b(3)).
	 * Flatten(a(b(1, 2), b(3)), {1, 2}, b)
	 * &gt;&gt; Flatten({{1, 2}, {3, {4}}}, {{1, 2}})
	 * {1, 2, 3, {4}}
	 * &gt;&gt; Flatten({{1, 2}, {3, {4}}}, {{1, 2, 3}})
	 *  : Level 3 specified in {{1, 2, 3}} exceeds the levels, 2, which can be flattened together in {{1, 2}, {3, {4}}}.
	 * Flatten({{1, 2}, {3, {4}}}, {{1, 2, 3}}, List)
	 * &gt;&gt; Flatten(p(1, p(2), p(3)))
	 * p(1, 2, 3)
	 * &gt;&gt; Flatten(p(1, p(2), p(3)), 2)
	 * p(1, 2, 3)
	 * </pre>
	 */
	private final static class Flatten extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2);

			IExpr arg1 = engine.evaluate(ast.arg1());
			if (arg1.isAST()) {
				IAST arg1AST = (IAST) arg1;
				if (ast.isAST1()) {
					IAST resultList = EvalAttributes.flatten(arg1AST.topHead(), (IAST) arg1);
					if (resultList.isPresent()) {
						return resultList;
					}
					return arg1AST;
				} else if (ast.isAST2()) {
					IExpr arg2 = engine.evaluate(ast.arg2());

					int level = Validate.checkIntLevelType(arg2);
					if (level > 0) {
						IASTAppendable resultList = F.ast(arg1AST.topHead());
						if (EvalAttributes.flatten(arg1AST.topHead(), (IAST) arg1, resultList, 0, level)) {
							return resultList;
						}
					}
					return arg1;
				} else if (ast.isAST3() && ast.arg3().isSymbol()) {
					IExpr arg2 = engine.evaluate(ast.arg2());

					int level = Validate.checkIntLevelType(arg2);
					if (level > 0) {
						IASTAppendable resultList = F.ast(arg1AST.topHead());
						if (EvalAttributes.flatten((ISymbol) ast.arg3(), (IAST) arg1, resultList, 0, level)) {
							return resultList;
						}
					}
					return arg1;
				}
			}
			return F.NIL;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	/**
	 * <pre>
	 * FlattenAt(expr, position)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * flattens out nested lists at the given <code>position</code> in <code>expr</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; FlattenAt(f(a, g(b,c), {d, e}, {f}), -2)
	 * f(a,g(b,c),d,e,{f})
	 * 
	 * &gt;&gt; FlattenAt(f(a, g(b,c), {d, e}, {f}), 4)
	 * f(a,g(b,c),{d,e},f)
	 * </pre>
	 */
	private final static class FlattenAt extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 3);

			IExpr arg1 = engine.evaluate(ast.arg1());
			IExpr arg2 = engine.evaluate(ast.arg2());
			if (arg1.isAST()) {
				IAST arg1AST = (IAST) arg1;
				int[] positions = null;
				if (arg2.isInteger()) {
					positions = new int[1];
					positions[0] = ((IInteger) arg2).toIntDefault(Integer.MIN_VALUE);
					if (positions[0] == Integer.MIN_VALUE) {
						return F.NIL;
					}
				}
				if (positions != null) {
					int size = arg1AST.size();
					for (int i = 0; i < positions.length; i++) {
						if (positions[i] < 0) {
							positions[i] = size + positions[i];
						}
					}
					IAST resultList = EvalAttributes.flattenAt(arg1AST.topHead(), arg1AST, positions);
					if (resultList.isPresent()) {
						return resultList;
					}
					return arg1AST;
				}
			}
			return F.NIL;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	private final static class Function extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.head().equals(F.Function)) {
				IExpr temp = engine.evalHoldPattern(ast, true);
				if (temp.isPresent() && !temp.equals(ast)) {
					return temp;
				}
				return F.NIL;
			}
			if (ast.head().isAST()) {

				final IAST function = (IAST) ast.head();
				if (function.size() > 1) {
					IExpr arg1 = function.arg1();
					if (function.isAST1()) {
						return Lambda.replaceSlotsOrElse(arg1, ast, arg1);
					} else if (function.isAST2()) {
						IExpr arg2 = function.arg2();
						IAST symbolSlots;
						if (arg1.isList()) {
							symbolSlots = (IAST) arg1;
						} else {
							symbolSlots = F.List(arg1);
						}
						if (symbolSlots.size() > ast.size()) {
							throw new WrongNumberOfArguments(ast, symbolSlots.argSize(), ast.argSize());
						}
						return arg2.replaceAll(x -> {
							IExpr temp = getRulesMap(symbolSlots, ast).get(x);
							return temp != null ? temp : F.NIL;
						}).orElse(arg2);
					}
				}
			}
			return F.NIL;
		}

		private static java.util.Map<IExpr, IExpr> getRulesMap(final IAST symbolSlots, final IAST ast) {
			int size = symbolSlots.argSize();
			final java.util.Map<IExpr, IExpr> rulesMap;
			if (size <= 5) {
				rulesMap = new OpenFixedSizeMap<IExpr, IExpr>(size * 3 - 1);
			} else {
				rulesMap = new HashMap<IExpr, IExpr>();
			}
			for (int i = 1; i <= size; i++) {
				rulesMap.put(symbolSlots.get(i), ast.get(i));
			}
			return rulesMap;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			// don't set HOLDALL - the arguments are evaluated before applying the 'function
			// head'
		}
	}

	/**
	 * <pre>
	 * Head(expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the head of the expression or atom <code>expr</code>.
	 * </p>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt; Head(a * b)
	 * Times
	 * &gt; Head(6)
	 * Integer
	 * &gt; Head(x)
	 * Symbol
	 * </pre>
	 * 
	 * </blockquote>
	 */
	private static class Head extends AbstractCoreFunctionEvaluator {
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST1()) {
				return engine.evaluate(ast.arg1()).head();
			}
			return F.Symbol;
		}

	}

	/**
	 * Count the number of leaves of an expression.
	 * 
	 */
	public static class LeafCount extends AbstractCoreFunctionEvaluator {

		/**
		 * Calculate the number of leaves in an AST
		 */
		public static class LeafCountVisitor extends AbstractVisitorLong {
			int fHeadOffset;

			public LeafCountVisitor() {
				this(1);
			}

			public LeafCountVisitor(int hOffset) {
				fHeadOffset = hOffset;
			}

			@Override
			public long visit(IAST list) {
				long sum = 0;
				for (int i = fHeadOffset; i < list.size(); i++) {
					sum += list.get(i).accept(this);
				}
				return sum;
			}

			@Override
			public long visit(IComplex element) {
				return element.leafCount();
			}

			@Override
			public long visit(IComplexNum element) {
				return element.leafCount();
			}

			@Override
			public long visit(IFraction element) {
				return element.leafCount();
			}
		}

		/**
		 * Calculate the number of leaves in an AST
		 */
		public static class SimplifyLeafCountVisitor extends AbstractVisitorLong {
			int fHeadOffset;

			public SimplifyLeafCountVisitor() {
				this(1);
			}

			public SimplifyLeafCountVisitor(int hOffset) {
				fHeadOffset = hOffset;
			}

			@Override
			public long visit(IAST list) {
				long sum = 0;
				for (int i = fHeadOffset; i < list.size(); i++) {
					sum += list.get(i).accept(this);
				}
				return sum;
			}

			@Override
			public long visit(IComplex element) {
				return element.leafCountSimplify();
			}

			@Override
			public long visit(IComplexNum element) {
				return 3;
			}

			@Override
			public long visit(IFraction element) {
				return element.leafCountSimplify();
			}

			@Override
			public long visit(IInteger element) {
				return element.leafCountSimplify();
			}
		}

		public static class SimplifyLeafCountPatternMapVisitor extends AbstractVisitorLong {

			int fHeadOffset;

			ISymbol2IntMap fPatternMap;

			public SimplifyLeafCountPatternMapVisitor(ISymbol2IntMap patternMap, int hOffset) {
				fHeadOffset = hOffset;
				fPatternMap = patternMap;
			}

			@Override
			public long visit(IAST list) {
				long sum = 0L;
				// if (list.isAnd()) {
				// sum = 1L;
				// }
				for (int i = fHeadOffset; i < list.size(); i++) {
					sum += list.get(i).accept(this);
				}
				return sum;
			}

			@Override
			public long visit(IComplex element) {
				return element.leafCountSimplify();
			}

			@Override
			public long visit(IComplexNum element) {
				return 3;
			}

			@Override
			public long visit(IFraction element) {
				return element.leafCountSimplify();
			}

			@Override
			public long visit(IInteger element) {
				return element.leafCountSimplify();
			}

			@Override
			public long visit(ISymbol element) {
				return element.leafCountSimplify();
			}

		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			return F.integer(engine.evaluate(ast.arg1()).leafCount());
		}

	}

	/**
	 * <pre>
	 * Map(f, expr)  or  f /@ expr
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * applies <code>f</code> to each part on the first level of <code>expr</code>.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * Map(f, expr, levelspec)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * applies f to each level specified by <code>levelspec</code> of <code>expr</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; f /@ {1, 2, 3}
	 * {f(1),f(2),f(3)}
	 * &gt;&gt; #^2&amp; /@ {1, 2, 3, 4}
	 * {1,4,9,16}
	 * </pre>
	 * <p>
	 * Map <code>f</code> on the second level:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Map(f, {{a, b}, {c, d, e}}, {2})
	 * {{f(a),f(b)},{f(c),f(d),f(e)}}
	 * </pre>
	 * <p>
	 * Include heads:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Map(f, a + b + c, Heads-&gt;True) 
	 * f(Plus)[f(a),f(b),f(c)]
	 * </pre>
	 * <p>
	 * Level specification a + b is not of the form n, {n}, or {m, n}.
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Map(f, expr, a+b, Heads-&gt;True) 
	 * Map(f, expr, a + b, Heads -&gt; True)
	 * </pre>
	 */
	private static class Map extends AbstractFunctionEvaluator {
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 3, 5);

			int lastIndex = ast.argSize();
			boolean heads = false;
			final Options options = new Options(ast.topHead(), ast, lastIndex, engine);
			IExpr option = options.getOption("Heads");
			if (option.isPresent()) {
				lastIndex--;
				if (option.isTrue()) {
					heads = true;
				}
			} else {
				Validate.checkRange(ast, 3, 4);
			}

			try {
				IExpr arg1 = ast.arg1();
				IExpr arg2 = ast.arg2();
				VisitorLevelSpecification level;
				if (lastIndex == 3) {
					level = new VisitorLevelSpecification(x -> F.unaryAST1(arg1, x), ast.get(lastIndex), heads, engine);
				} else {
					level = new VisitorLevelSpecification(x -> F.unaryAST1(arg1, x), 1, heads);
				}
				return arg2.accept(level).orElse(arg2);
			} catch (final MathException e) {
				engine.printMessage(e.getMessage());
			}
			return F.NIL;
		}

	}

	private static class MapAll extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 3);

			final IExpr arg1 = ast.arg1();
			final VisitorLevelSpecification level = new VisitorLevelSpecification(x -> F.unaryAST1(arg1, x), 0,
					Integer.MAX_VALUE, false);

			final IExpr result = ast.arg2().accept(level);
			return result.isPresent() ? result : ast.arg2();
		}

	}

	private static class MapAt extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 4);

			final IExpr arg2 = ast.arg2();
			if (arg2.isAST()) {
				try {
					final IExpr arg3 = ast.arg3();
					if (arg3.isInteger()) {
						final IExpr arg1 = ast.arg1();
						IInteger i3 = (IInteger) arg3;
						int n = i3.toInt();
						return ((IAST) arg2).setAtCopy(n, F.unaryAST1(arg1, ((IAST) arg2).get(n)));
					}
				} catch (RuntimeException ae) {
				}
			}
			return F.NIL;
		}

	}

	private final static class MapIndexed extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 3, 4);

			int lastIndex = ast.argSize();
			boolean heads = false;
			final Options options = new Options(ast.topHead(), ast, lastIndex, engine);
			IExpr option = options.getOption("Heads");
			if (option.isPresent()) {
				lastIndex--;
				if (option.isTrue()) {
					heads = true;
				}
			}  

			try {
				IExpr arg1 = ast.arg1();
				IndexedLevel level;
				if (lastIndex == 3) {
					level = new IndexedLevel((x, y) -> F.binaryAST2(arg1, x, y), ast.get(lastIndex), heads,
							engine);
				} else {
					level = new IndexedLevel((x, y) -> F.binaryAST2(arg1, x, y), 1, heads);
				}
				
				IExpr arg2 = ast.arg2();
				if (arg2.isAST()) {
					return level.visitAST(((IAST) arg2), new int[0]).orElse(arg2);
				}
			} catch (final MathException e) {
				engine.printMessage(e.getMessage());
			}
			return F.NIL;
		}

	}

	/**
	 * <pre>
	 * MapThread(`f`, {{`a1`, `a2`, ...}, {`b1`, `b2`, ...}, ...})
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns '{<code>f</code>(<code>a1</code>, <code>b1</code>, &hellip;), <code>f</code>(<code>a2</code>,
	 * <code>b2</code>, &hellip;), &hellip;}'.<br />
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * MapThread(`f`, {`expr1`, `expr2`, ...}, `n`)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * applies <code>f</code> at level <code>n</code>.<br />
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; MapThread(f, {{a, b, c}, {1, 2, 3}})       
	 * {f(a,1),f(b,2),f(c,3)}
	 * 
	 * &gt;&gt; MapThread(f, {{{a, b}, {c, d}}, {{e, f}, {g, h}}}, 2)    
	 * {{f(a, e), f(b, f)}, {f(c, g), f(d, h)}}
	 * </pre>
	 * <p>
	 * Non-negative machine-sized integer expected at position 3 in MapThread(f, {{a, b}, {c, d}}, {1}).<br />
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; MapThread(f, {{a, b}, {c, d}}, {1})    
	 * MapThread(f, {{a, b}, {c, d}}, {1})
	 * </pre>
	 * <p>
	 * Object {a, b} at position {2, 1} in MapThread(f, {{a, b}, {c, d}}, 2) has only 1 of required 2 dimensions.<br />
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; MapThread(f, {{a, b}, {c, d}}, 2)   
	 * MapThread(f, {{a, b}, {c, d}}, 2)
	 * </pre>
	 * <p>
	 * Incompatible dimensions of objects at positions {2, 1} and {2, 2} of MapThread(f, {{a}, {b, c}}); dimensions are
	 * 1 and 2.<br />
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; MapThread(f, {{a}, {b, c}})    
	 * MapThread(f, {{a}, {b, c}})    
	 * 
	 * &gt;&gt; MapThread(f, {})    
	 * {}    
	 * 
	 * &gt;&gt; MapThread(f, {a, b}, 0)    
	 * f(a, b)
	 * </pre>
	 * <p>
	 * Object a at position {2, 1} in MapThread(f, {a, b}, 1) has only 0 of required 1 dimensions.
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; MapThread(f, {a, b}, 1)    
	 * MapThread(f, {a, b}, 1)
	 * </pre>
	 */
	private final static class MapThread extends AbstractFunctionEvaluator {

		private static class MapThreadLevel {
			/**
			 * The permutation of the result tensor
			 */
			final int level;

			final IExpr constant;

			private MapThreadLevel(IExpr constant, int level) {
				this.constant = constant;
				this.level = level;
			}

			private IAST recursiveMapThread(int recursionLevel, IAST lst, IASTAppendable resultList) {
				if (recursionLevel >= level) {
					return lst;
				}
				int size = lst.first().size() - 1;
				IASTAppendable list;
				if (level == recursionLevel + 1) {
					list = EvalAttributes.threadList(lst, F.List, constant, size);
					if (resultList != null) {
						resultList.append(list);
					}
				} else {
					list = EvalAttributes.threadList(lst, F.List, F.List, size);
					IASTAppendable result = F.ListAlloc(size);
					for (int i = 1; i < list.size(); i++) {
						recursiveMapThread(recursionLevel + 1, (IAST) list.get(i), result);
					}
					if (resultList != null) {
						resultList.append(result);
					}
					return result;
				}
				return list;
			}
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 3, 4);

			if (ast.arg2().isAST()) {
				int level = 1;
				if (ast.isAST3()) {
					level = ast.arg3().toIntDefault(-1);
					if (level < 0) {
						return F.NIL;
					}
				}

				IAST tensor = (IAST) ast.arg2();
				ArrayList<Integer> dims = LinearAlgebra.dimensions(tensor, tensor.head(), Integer.MAX_VALUE);
				if (dims.size() > level) {
					if (level == 0) {
						return tensor.apply(ast.arg1());
					}
					// if (level == 1) {
					// return EvalAttributes.threadList(tensor, F.List, ast.arg1(), dims.get(level));
					// }
					return new MapThreadLevel(ast.arg1(), level).recursiveMapThread(0, tensor, null);
				}
				if (tensor.isAST(F.List, 1)) {
					return tensor;
				}
				engine.printMessage("MapThread: argument 2 dimensions less than level.");
			}
			return F.NIL;
		}

	}

	/**
	 * <pre>
	 * Order(a, b)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * is <code>0</code> if <code>a</code> equals <code>b</code>. Is <code>-1</code> or <code>1</code> according to
	 * canonical order of <code>a</code> and <code>b</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Order(3,4)
	 * 1
	 * 
	 * &gt;&gt; Order(4,3)
	 * -1
	 * </pre>
	 */
	private final static class Order extends AbstractFunctionEvaluator {

		/**
		 * Compares the first expression with the second expression for order. Returns 1, 0, -1 as this expression is
		 * canonical less than, equal to, or greater than the specified expression. <br>
		 * <br>
		 * (<b>Implementation note</b>: see the different results in the <code>IExpr#compareTo(IExpr)</code> method)
		 * 
		 * @see org.matheclipse.core.interfaces.IExpr#compareTo(IExpr)
		 */
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);

			final int cp = ast.arg1().compareTo(ast.arg2());
			if (cp < 0) {
				return F.C1;
			} else if (cp > 0) {
				return F.CN1;
			}
			return F.C0;
		}

	}

	/**
	 * <pre>
	 * OrderedQ({a, b})
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * is <code>True</code> if <code>a</code> sorts before <code>b</code> according to canonical ordering.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; OrderedQ({a, b})
	 * True
	 * 
	 * &gt;&gt; OrderedQ({b, a})
	 * False
	 * </pre>
	 */
	private final static class OrderedQ extends AbstractFunctionEvaluator implements Predicate<IAST> {
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);
			return F.bool(test(((IAST) ast.arg1())));
		}

		@Override
		public boolean test(IAST ast) {
			return ast.compareAdjacent((x, y) -> x.isLEOrdered(y));
		}

	}

	/**
	 * <pre>
	 * Operate(p, expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * applies <code>p</code> to the head of <code>expr</code>.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * Operate(p, expr, n)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * applies <code>p</code> to the <code>n</code>th head of <code>expr</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Operate(p, f(a, b))
	 * p(f)[a,b]
	 * </pre>
	 * <p>
	 * The default value of <code>n</code> is <code>1</code>:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Operate(p, f(a, b), 1)
	 * p(f)[a,b]
	 * </pre>
	 * <p>
	 * With <code>n = 0</code>, <code>Operate</code> acts like <code>Apply</code>:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Operate(p, f(a)[b][c], 0)
	 * p(f(a)[b][c])
	 * 
	 * &gt;&gt; Operate(p, f(a)[b][c])
	 * p(f(a)[b])[c] 
	 * 
	 * &gt;&gt; Operate(p, f(a)[b][c], 1)
	 * p(f(a)[b])[c]
	 * 
	 * &gt;&gt; Operate(p, f(a)[b][c], 2)
	 * p(f(a))[b][c] 
	 * 
	 * &gt;&gt; Operate(p, f(a)[b][c], 3)
	 * p(f)[a][b][c]
	 * 
	 * &gt;&gt; Operate(p, f(a)[b][c], 4)
	 * f(a)[b][c]
	 * 
	 * &gt;&gt; Operate(p, f)
	 * f
	 * 
	 * &gt;&gt; Operate(p, f, 0)
	 * p(f)
	 * </pre>
	 * <p>
	 * Non-negative integer expected at position <code>3</code> in <code>Operate(p, f, -1)</code>.
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Operate(p, f, -1)
	 * Operate(p, f, -1)
	 * </pre>
	 */
	private final static class Operate extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 3, 4);

			int headDepth = 1;
			if (ast.isAST3()) {
				if (!ast.arg3().isInteger()) {
					return F.NIL;
				}
				IInteger depth = (IInteger) ast.arg3();
				if (depth.isNegative()) {
					engine.printMessage("Non-negative integer expected at position 3 in Operate()");
					return F.NIL;
				}

				headDepth = depth.toIntDefault(Integer.MIN_VALUE);
				if (headDepth == Integer.MIN_VALUE) {
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

			IASTAppendable result = ((IAST) arg2).copyAppendable();
			IASTAppendable last = result;
			IASTAppendable head = result;

			for (int i = 1; i < headDepth; i++) {
				head = ((IAST) head.head()).copyAppendable();
				last.set(0, head);
				last = head;
			}

			head.set(0, F.unaryAST1(p, head.head()));
			return result;
		}
	}

	private final static class Quit extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 1);
			return F.Null;
		}
	}

	/**
	 * <pre>
	 * Scan(f, expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * applies <code>f</code> to each element of <code>expr</code> and returns 'Null'.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * Scan(f, expr, levelspec)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * applies <code>f</code> to each level specified by <code>levelspec</code> of <code>expr</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Scan(Print, {1, 2, 3})
	 *  1
	 *  2
	 *  3
	 * &gt;&gt; Scan(Print, f(g(h(x))), 2)
	 *  h(x)
	 *  g(h(x))
	 * &gt;&gt; Scan(Print)({1, 2})
	 *  1
	 *  2
	 * &gt;&gt; Scan(Return, {1, 2})
	 * 1
	 * </pre>
	 */
	private final static class Scan extends Map {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 3, 5);

			int lastIndex = ast.argSize();
			boolean heads = false;
			final Options options = new Options(ast.topHead(), ast, lastIndex, engine);
			IExpr option = options.getOption("Heads");
			if (option.isPresent()) {
				lastIndex--;
				if (option.isTrue()) {
					heads = true;
				}
			} else {
				Validate.checkRange(ast, 3, 4);
			}

			try {
				IExpr arg1 = ast.arg1();
				IExpr arg2 = ast.arg2();
				if (lastIndex == 3) {
					IASTAppendable result = F.ListAlloc(10);
					java.util.function.Function<IExpr, IExpr> sf = x -> {
						IAST a = F.unaryAST1(arg1, x);
						result.append(a);
						return F.NIL;
					};

					VisitorLevelSpecification level = new VisitorLevelSpecification(sf, ast.get(lastIndex), heads,
							engine);

					arg2.accept(level);
					result.forEach(result.size(), x -> engine.evaluate(x));
					// for (int i = 1; i < result.size(); i++) {
					// engine.evaluate(result.get(i));
					// }

				} else {
					if (arg2.isAST()) {
						engine.evaluate(((IAST) arg2).map(x -> F.unaryAST1(arg1, x), 1));
					} else {
						engine.evaluate(arg2);
					}
				}
				return F.Null;
			} catch (final ReturnException e) {
				return e.getValue();
				// don't catch Throw[] here !
			}
		}

	}

	/**
	 * <pre>
	 * Sort(list)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * sorts $list$ (or the leaves of any other expression) according to canonical ordering.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * Sort(list, p)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * sorts using <code>p</code> to determine the order of two elements.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Sort({4, 1.0, a, 3+I})
	 * {1.0,4,3+I,a}
	 * </pre>
	 */
	private static class Sort extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			if (ast.arg1().isAST()) {
				IAST arg1 = (IAST) ast.arg1();
				if (ast.isAST1() && (arg1.getEvalFlags() & IAST.IS_SORTED) == IAST.IS_SORTED) {
					return arg1;
				}
				final IASTMutable shallowCopy = ((IAST) ast.arg1()).copy();
				if (shallowCopy.size() <= 2) {
					return shallowCopy;
				}
				try {
					if (ast.isAST1()) {
						EvalAttributes.sort(shallowCopy);
					} else {
						// use the 2nd argument as a head for the comparator operation:
						EvalAttributes.sort(shallowCopy, new Predicates.IsBinaryFalse(ast.arg2()));
					}
					return shallowCopy;
				} catch (Exception ex) {

				}
			}

			return F.NIL;
		}
	}

	/**
	 * <pre>
	 * Symbol
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * is the head of symbols.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Head(x)
	 * Symbol
	 * </pre>
	 * <p>
	 * You can use <code>Symbol</code> to create symbols from strings:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Symbol("x") + Symbol("x")
	 * 2*x
	 * &gt;&gt; {\[Eta], \[CapitalGamma]\[Beta], Z\[Infinity], \[Angle]XYZ, \[FilledSquare]r, i\[Ellipsis]j}
	 * {\u03b7, \u0393\u03b2, Z\u221e, \u2220XYZ, \u25a0r, i\u2026j}
	 * </pre>
	 */
	private static class Symbol extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			if (ast.arg1().isString()) {
				return F.symbol(ast.arg1().toString(), engine);
			}
			return F.NIL;
		}
	}

	/**
	 * <pre>
	 * SymbolName(s)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns the name of the symbol <code>s</code> (without any leading context name).
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; SymbolName(x)  // InputForm
	 * "x"
	 * &gt;&gt; SymbolName(a`b`x)  // InputForm
	 * "x"
	 * </pre>
	 */
	private static class SymbolName extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			if (ast.arg1().isSymbol()) {
				return F.stringx(ast.arg1().toString());
			}
			return F.NIL;
		}
	}

	/**
	 * <pre>
	 * Thread(f(args)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * threads <code>f</code> over any lists that appear in <code>args</code>.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * Thread(f(args), h)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * threads over any parts with head <code>h</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Thread(f({a, b, c}))
	 * {f(a),f(b),f(c)}
	 * 
	 * &gt;&gt; Thread(f({a, b, c}, t))
	 * {f(a,t),f(b,t),f(c,t)}
	 * 
	 * &gt;&gt; Thread(f(a + b + c), Plus)
	 * f(a)+f(b)+f(c)
	 * </pre>
	 * <p>
	 * Functions with attribute <code>Listable</code> are automatically threaded over lists:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; {a, b, c} + {d, e, f} + g
	 * {a+d+g,b+e+g,c+f+g}
	 * </pre>
	 */
	private final static class Thread extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			if (!(ast.arg1().isAST())) {
				return F.NIL;
			}
			// LevelSpec level = null;
			// if (functionList.isAST3()) {
			// level = new LevelSpecification(functionList.arg3());
			// } else {
			// level = new LevelSpec(1);
			// }
			IExpr head = F.List;
			if (ast.isAST2()) {
				head = ast.arg2();
			}
			final IAST list = (IAST) ast.arg1();
			if (list.size() > 1) {
				return threadList(list, head, list.head());
			}
			return F.NIL;
		}

		/**
		 * Thread through all lists in the arguments of the IAST [i.e. the list header has the attribute
		 * ISymbol.LISTABLE] example: Sin[{2,x,Pi}] ==> {Sin[2],Sin[x],Sin[Pi]}
		 * 
		 * @param list
		 * @param head
		 *            the head over which
		 * @param mapHead
		 *            the arguments head (typically <code>ast.head()</code>)
		 * @return
		 */
		public static IAST threadList(final IAST list, IExpr head, IExpr mapHead) {

			int listLength = 0;

			for (int i = 1; i < list.size(); i++) {
				if ((list.get(i).isAST()) && (((IAST) list.get(i)).head().equals(head))) {
					if (listLength == 0) {
						listLength = ((IAST) list.get(i)).argSize();
					} else {
						if (listLength != ((IAST) list.get(i)).argSize()) {
							listLength = 0;
							return F.NIL;
							// for loop
						}
					}
				}
			}

			return EvalAttributes.threadList(list, head, mapHead, listLength);

		}
	}

	/**
	 * <pre>
	 * Through(p(f)[x])
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * gives <code>p(f(x))</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Through(f(g)[x])
	 * f(g(x))
	 * 
	 * &gt;&gt; Through(p(f, g)[x])
	 * p(f(x), g(x))
	 * 
	 * &gt;&gt; Through(p(f, g)[x, y])
	 * p(f(x, y), g(x, y))
	 * 
	 * &gt;&gt; Through(p(f, g)[])
	 * p(f(), g())
	 * 
	 * &gt;&gt; Through(p(f, g))
	 * Through(p(f, g))
	 * 
	 * &gt;&gt; Through(f()[x])
	 * f()
	 * </pre>
	 */
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
					IASTAppendable result = F.ast(arg1HeadAST.head());
					return result.appendArgs(arg1HeadAST.size(), i -> arg1AST.apply(arg1HeadAST.get(i)));
					// for (int i = 1; i < arg1HeadAST.size(); i++) {
					// clonedList = arg1AST.apply(arg1HeadAST.get(i));
					// result.append(clonedList);
					// }
					// return result;
				}
				return arg1AST;
			}
			return ast.arg1();
		}
	}

	/**
	 * Maps the elements of the <code>expr</code> with the cloned <code>replacement</code>. <code>replacement</code> is
	 * an IAST where the argument at the given position will be replaced by the currently mapped element.
	 * 
	 * 
	 * @param expr
	 * @param replacement
	 *            an IAST there the argument at the given position is replaced by the currently mapped argument of this
	 *            IAST.
	 * @param position
	 * @return
	 */
	public static IAST threadLogicEquationOperators(IExpr expr, IAST replacement, int position) {
		if (expr.isAST()) {
			IAST ast = (IAST) expr;
			if (ast.size() > 1) {
				ISymbol[] logicEquationHeads = { F.And, F.Or, F.Xor, F.Nand, F.Nor, F.Not, F.Implies, F.Equivalent,
						F.Equal, F.Unequal, F.Less, F.Greater, F.LessEqual, F.GreaterEqual };
				for (int i = 0; i < logicEquationHeads.length; i++) {
					if (ast.isAST(logicEquationHeads[i])) {
						IASTMutable copy = replacement.setAtCopy(position, null);
						return ast.mapThread(copy, position);
					}
				}

			}
		}
		return F.NIL;
	}

	/**
	 * Maps the elements of the <code>expr</code> with the cloned <code>replacement</code>. <code>replacement</code> is
	 * an IAST where the argument at the given position will be replaced by the currently mapped element.
	 * 
	 * 
	 * @param expr
	 * @param replacement
	 *            an IAST there the argument at the given position is replaced by the currently mapped argument of this
	 *            IAST.
	 * @param position
	 * @return
	 */
	public static IAST threadPlusLogicEquationOperators(IExpr expr, IAST replacement, int position) {
		if (expr.isAST()) {
			IAST ast = (IAST) expr;
			if (ast.size() > 1) {
				ISymbol[] plusLogicEquationHeads = { F.Plus, F.And, F.Or, F.Xor, F.Nand, F.Nor, F.Not, F.Implies,
						F.Equivalent, F.Equal, F.Unequal, F.Less, F.Greater, F.LessEqual, F.GreaterEqual };
				for (int i = 0; i < plusLogicEquationHeads.length; i++) {
					if (ast.isAST(plusLogicEquationHeads[i])) {
						IASTMutable copy = replacement.setAtCopy(position, null);
						return ast.mapThread(copy, position);
					}
				}

			}
		}
		return F.NIL;
	}

	private final static Structure CONST = new Structure();

	public static Structure initialize() {
		return CONST;
	}

	private Structure() {

	}
}
