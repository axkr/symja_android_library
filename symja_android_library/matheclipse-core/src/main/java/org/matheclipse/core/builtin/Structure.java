package org.matheclipse.core.builtin;

import java.util.HashMap;
import java.util.function.Predicate;

import org.matheclipse.core.eval.EvalAttributes;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ReturnException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongNumberOfArguments;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.util.OpenFixedSizeMap;
import org.matheclipse.core.eval.util.Options;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.generic.Functors;
import org.matheclipse.core.generic.Predicates;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.visit.VisitorLevelSpecification;
import org.matheclipse.parser.client.math.MathException;

public class Structure {

	static {
		F.Apply.setEvaluator(new Apply());
		F.Depth.setEvaluator(new Depth());
		F.Flatten.setEvaluator(new Flatten());
		F.Function.setEvaluator(new Function());
		F.Map.setEvaluator(new Map());
		F.MapThread.setEvaluator(new MapThread());
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
	 * 
	 * <p>
	 * See the online Symja function reference:
	 * <a href= "https://bitbucket.org/axelclk/symja_android_library/wiki/Symbols/Apply"> Apply</a>
	 * </p>
	 *
	 */
	private final static class Apply extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 3, 5);

			IAST evaledAST = ast.clone();
			for (int i = 1; i < evaledAST.size(); i++) {
				evaledAST.set(i, engine.evaluate(evaledAST.get(i)));
			}
			int lastIndex = evaledAST.size() - 1;
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
			return evalApply(arg1, arg2, evaledAST, lastIndex, heads);
		}

		public static IExpr evalApply(IExpr arg1, IExpr arg2, IAST evaledAST, int lastIndex, boolean heads) {
			VisitorLevelSpecification level = null;
			java.util.function.Function<IExpr, IExpr> af = Functors.apply(arg1);
			try {
				if (lastIndex == 3) {
					level = new VisitorLevelSpecification(af, evaledAST.get(lastIndex), heads);
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
				EvalEngine.get().printMessage(e.getMessage());
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
	 * Calculates the depth of an expression (i.e. <code>{x,{y}} --> 3</code>
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
	 * TODO implement &quot;Flatten&quot; function (especially Flatten(list, 1) )
	 * 
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
						IAST resultList = F.ast(arg1AST.topHead());
						if (EvalAttributes.flatten(arg1AST.topHead(), (IAST) arg1, resultList, 0, level)) {
							return resultList;
						}
					}
					return arg1;
				} else if (ast.isAST3() && ast.arg3().isSymbol()) {
					IExpr arg2 = engine.evaluate(ast.arg2());

					int level = Validate.checkIntLevelType(arg2);
					if (level > 0) {
						IAST resultList = F.ast(arg1AST.topHead());
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

	private final static class Function extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.head().isAST()) {

				final IAST function = (IAST) ast.head();
				if (function.isAST1()) {
					return replaceSlots(function.arg1(), ast);
				} else if (function.isAST2()) {
					IAST symbolSlots;
					if (function.arg1().isList()) {
						symbolSlots = (IAST) function.arg1();
					} else {
						symbolSlots = F.List(function.arg1());
					}
					if (symbolSlots.size() > ast.size()) {
						throw new WrongNumberOfArguments(ast, symbolSlots.size() - 1, ast.size() - 1);
					}
					return function.arg2().replaceAll(Functors.rules(getRulesMap(symbolSlots, ast)))
							.orElse(function.arg2());
				}

			}
			return F.NIL;
		}

		private static java.util.Map<IExpr, IExpr> getRulesMap(final IAST symbolSlots, final IAST ast) {
			int size = symbolSlots.size() - 1;
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

		public static IExpr replaceSlots(final IExpr expr, final IAST list) {
			return expr.replaceSlots(list).orElse(expr);
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			// don't set HOLDALL - the arguments are evaluated before aopplying the 'function head'
		}
	}

	/**
	 * 
	 * @see Scan
	 */
	private static class Map extends AbstractFunctionEvaluator {
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 3, 5);

			int lastIndex = ast.size() - 1;
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
				final IAST arg1 = F.ast(ast.arg1());
				if (lastIndex == 3) {
					VisitorLevelSpecification level = new VisitorLevelSpecification(Functors.append(arg1),
							ast.get(lastIndex), heads);
					final IExpr result = ast.arg2().accept(level);
					return result.isPresent() ? result : ast.arg2();
				} else {
					VisitorLevelSpecification level = new VisitorLevelSpecification(Functors.append(arg1), 1, heads);
					final IExpr result = ast.arg2().accept(level);
					return result.isPresent() ? result : ast.arg2();
				}
			} catch (final MathException e) {
				EvalEngine.get().printMessage(e.getMessage());
			}
			return F.NIL;
		}

	}

	private final static class MapThread extends AbstractFunctionEvaluator {

		private static class UnaryMapThread implements java.util.function.Function<IExpr, IExpr> {
			final IExpr fConstant;

			public UnaryMapThread(final IExpr constant) {
				fConstant = constant;
			}

			@Override
			public IExpr apply(final IExpr firstArg) {
				if (firstArg.isAST()) {
					return Thread.threadList((IAST) firstArg, F.List, fConstant).orElse(firstArg);
				}
				return firstArg;
			}

		}

		public MapThread() {
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 3, 4);

			VisitorLevelSpecification level = null;
			java.util.function.Function<IExpr, IExpr> umt = new UnaryMapThread(ast.arg1());
			if (ast.isAST3()) {
				level = new VisitorLevelSpecification(umt, ast.arg3(), false);
			} else {
				level = new VisitorLevelSpecification(umt, 0);
			}
			final IExpr result = ast.arg2().accept(level);
			return result.isPresent() ? result : ast.arg2();
		}

	}

	private final static class OrderedQ extends AbstractFunctionEvaluator implements Predicate<IAST> {
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);
			return F.bool(test(((IAST) ast.arg1())));
		}

		@Override
		public boolean test(IAST ast) {
			return ast.args().compareAdjacent((x, y) -> x.isLEOrdered(y));
		}

	}

	private final static class Operate extends AbstractFunctionEvaluator {

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
						EvalEngine.get().printMessage("Non-negative integer expected at position 3 in Operate()");
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

	private final static class Quit extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 1);
			return F.Null;
		}
	}

	/**
	 * @see Map
	 */
	private final static class Scan extends Map {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 3, 5);

			int lastIndex = ast.size() - 1;
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
				final IAST arg1 = F.ast(ast.arg1());
				if (lastIndex == 3) {
					IAST result = F.List();
					java.util.function.Function<IExpr, IExpr> sf = Functors.scan(arg1, result);
					VisitorLevelSpecification level = new VisitorLevelSpecification(sf, ast.get(lastIndex), heads);

					ast.arg2().accept(level);
					for (int i = 1; i < result.size(); i++) {
						engine.evaluate(result.get(i));
					}

				} else {
					if (ast.arg2().isAST()) {
						engine.evaluate(((IAST) ast.arg2()).map(Functors.append(arg1)));
					} else {
						engine.evaluate(ast.arg2());
					}
				}
				return F.Null;
			} catch (final ReturnException e) {
				return e.getValue();
				// don't catch Throw[] here !
			}
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

	private static class Symbol extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			if (ast.arg1().isString()) {
				return F.userSymbol(ast.arg1().toString());
			}
			return F.NIL;
		}
	}

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
						listLength = ((IAST) list.get(i)).size() - 1;
					} else {
						if (listLength != ((IAST) list.get(i)).size() - 1) {
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
				IAST cloned = replacement.clone();
				cloned.set(position, null);
				ISymbol[] logicEquationHeads = { F.And, F.Or, F.Xor, F.Nand, F.Nor, F.Not, F.Implies, F.Equivalent,
						F.Equal, F.Unequal, F.Less, F.Greater, F.LessEqual, F.GreaterEqual };
				for (int i = 0; i < logicEquationHeads.length; i++) {
					if (ast.isAST(logicEquationHeads[i])) {
						return ((IAST) ast).mapThread(cloned, position);
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
				IAST cloned = replacement.clone();
				cloned.set(position, null);
				ISymbol[] plusLogicEquationHeads = { F.Plus, F.And, F.Or, F.Xor, F.Nand, F.Nor, F.Not, F.Implies,
						F.Equivalent, F.Equal, F.Unequal, F.Less, F.Greater, F.LessEqual, F.GreaterEqual };
				for (int i = 0; i < plusLogicEquationHeads.length; i++) {
					if (ast.isAST(plusLogicEquationHeads[i])) {
						return ((IAST) ast).mapThread(cloned, position);
					}
				}

			}
		}
		return F.NIL;
	}

	final static Structure CONST = new Structure();

	public static Structure initialize() {
		return CONST;
	}

	private Structure() {

	}
}
