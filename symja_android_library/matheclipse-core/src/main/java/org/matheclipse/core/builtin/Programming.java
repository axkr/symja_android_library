package org.matheclipse.core.builtin;

import static org.matheclipse.core.expression.F.Divide;
import static org.matheclipse.core.expression.F.List;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.convert.VariablesSet;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.AbortException;
import org.matheclipse.core.eval.exception.BreakException;
import org.matheclipse.core.eval.exception.ConditionException;
import org.matheclipse.core.eval.exception.ContinueException;
import org.matheclipse.core.eval.exception.NoEvalException;
import org.matheclipse.core.eval.exception.ReturnException;
import org.matheclipse.core.eval.exception.ThrowException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrappedException;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.util.Iterator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IIterator;
import org.matheclipse.core.interfaces.ISignedNumber;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.core.visit.ModuleReplaceAll;
import org.matheclipse.parser.client.math.MathException;

import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.google.common.util.concurrent.TimeLimiter;

public final class Programming {
	private final static Programming CONST = new Programming();

	static {
		F.Abort.setEvaluator(new Abort());
		F.Break.setEvaluator(new Break());
		F.Block.setEvaluator(new Block());
		F.Catch.setEvaluator(new Catch());
		F.Compile.setEvaluator(new Compile());
		F.CompoundExpression.setEvaluator(new CompoundExpression());
		F.Condition.setEvaluator(new Condition());
		F.Continue.setEvaluator(new Continue());
		F.Defer.setEvaluator(new Defer());
		F.Do.setEvaluator(new Do());
		F.FixedPoint.setEvaluator(new FixedPoint());
		F.FixedPointList.setEvaluator(new FixedPointList());
		F.For.setEvaluator(new For());
		F.If.setEvaluator(new If());
		F.Module.setEvaluator(new Module());
		F.Nest.setEvaluator(new Nest());
		F.NestList.setEvaluator(new NestList());
		F.NestWhile.setEvaluator(new NestWhile());
		F.NestWhileList.setEvaluator(new NestWhileList());
		F.Part.setEvaluator(new Part());
		F.Print.setEvaluator(new Print());
		F.Quiet.setEvaluator(new Quiet());
		F.Reap.setEvaluator(new Reap());
		F.Return.setEvaluator(new Return());
		F.Sow.setEvaluator(new Sow());
		F.Switch.setEvaluator(new Switch());
		F.TimeConstrained.setEvaluator(new TimeConstrained());
		F.Timing.setEvaluator(new Timing());
		F.Throw.setEvaluator(new Throw());
		F.Trace.setEvaluator(new Trace());
		F.Unevaluated.setEvaluator(new Unevaluated());
		F.Which.setEvaluator(new Which());
		F.While.setEvaluator(new While());
		F.With.setEvaluator(new With());
	}

	private final static class Abort extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST0()) {
				throw AbortException.ABORTED;
			}
			Validate.checkSize(ast, 1);

			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

	}

	/**
	 * <p>
	 * <code>Break()</code> leaves a <code>Do</code>, <code>For</code> or <code>While</code> loop.
	 * </p>
	 * <p>
	 * See the online Symja function reference:
	 * <a href= "https://bitbucket.org/axelclk/symja_android_library/wiki/Symbols/Break"> Break</a>
	 * </p>
	 *
	 */
	private final static class Break extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST0()) {
				throw BreakException.CONST;
			}
			Validate.checkSize(ast, 1);

			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	/**
	 * 
	 * <p>
	 * See the online Symja function reference:
	 * <a href= "https://bitbucket.org/axelclk/symja_android_library/wiki/Symbols/Block"> Block</a>
	 * </p>
	 *
	 */
	private final static class Block extends AbstractCoreFunctionEvaluator {
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);

			if (ast.arg1().isList()) {
				final IAST blockVariablesList = (IAST) ast.arg1();
				return engine.evalBlock(ast.arg2(), blockVariablesList);
			}

			return F.NIL;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	/**
	 * <p>
	 * See the online Symja function reference:
	 * <a href= "https://bitbucket.org/axelclk/symja_android_library/wiki/Symbols/Catch"> Catch</a>
	 * </p>
	 */
	private final static class Catch extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);
			try {
				return engine.evaluate(ast.arg1());
			} catch (final ThrowException e) {
				return e.getValue();
			}
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	private static class Compile extends AbstractCoreFunctionEvaluator {
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (!ToggleFeature.COMPILE) {
				return F.NIL;
			}
			engine.printMessage("Compile: Compile() function not implemented! ");
			return F.Null;
		}

	}

	private final static class CompoundExpression extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.size() > 1) {
				IExpr[] result = { F.Null };
				ast.forEach(x -> result[0] = engine.evaluate(x));
				return result[0];
			}
			return F.Null;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	private final static class Condition extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST2()) {
				if (engine.evalTrue(ast.arg2())) {
					return engine.evaluate(ast.arg1());
				}
				if (engine.isEvalLHSMode()) {
					return F.NIL;
				}
				throw new ConditionException(ast);
			}
			Validate.checkSize(ast, 3);
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	private final static class Continue extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST0()) {
				throw ContinueException.CONST;
			}
			Validate.checkSize(ast, 1);

			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

	}

	/**
	 * TODO implement &quot;Defer&quot; mode
	 * 
	 */
	private static class Defer extends AbstractCoreFunctionEvaluator {
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (!ToggleFeature.DEFER) {
				return F.NIL;
			}
			// Validate.checkSize(ast, 2);
			// IExpr arg1 = engine.evaluate(ast.arg1());

			return F.NIL;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			if (!ToggleFeature.DEFER) {
				return;
			}
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	/**
	 * <pre>
	 * Do(expr, {max})
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * evaluates <code>expr</code> <code>max</code> times.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * Do(expr, {i, max})
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * evaluates <code>expr</code> <code>max</code> times, substituting <code>i</code> in <code>expr</code> with values
	 * from <code>1</code> to <code>max</code>.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * Do(expr, {i, min, max})
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * starts with <code>i = max</code>.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * Do(expr, {i, min, max, step})
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * uses a step size of <code>step</code>.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * Do(expr, {i, {i1, i2, ...}})
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * uses values <code>i1, i2, ... for i</code>.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * Do(expr, {i, imin, imax}, {j, jmin, jmax}, ...)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * evaluates expr for each j from jmin to jmax, for each i from imin to imax, etc.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Do(Print(i), {i, 2, 4})
	 *  | 2
	 *  | 3
	 *  | 4
	 * 
	 * &gt;&gt; Do(Print({i, j}), {i,1,2}, {j,3,5})
	 *  | {1, 3}
	 *  | {1, 4}
	 *  | {1, 5}
	 *  | {2, 3}
	 *  | {2, 4}
	 *  | {2, 5}
	 * </pre>
	 * <p>
	 * You can use 'Break()' and 'Continue()' inside 'Do':
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Do(If(i &gt; 10, Break(), If(Mod(i, 2) == 0, Continue()); Print(i)), {i, 5, 20})
	 *  | 5
	 *  | 7
	 *  | 9
	 * 
	 * &gt;&gt; Do(Print("hi"),{1+1})
	 *  | hi
	 *  | hi
	 * </pre>
	 */
	private final static class Do extends AbstractCoreFunctionEvaluator {

		private static class DoIterator {

			final List<? extends IIterator<IExpr>> fIterList;
			final EvalEngine fEngine;
			int fIndex;

			public DoIterator(final List<? extends IIterator<IExpr>> iterList, EvalEngine engine) {
				fIterList = iterList;
				fEngine = engine;
				fIndex = 0;
			}

			public IExpr doIt(IExpr input) {
				if (fIndex < fIterList.size()) {
					final IIterator<IExpr> iter = fIterList.get(fIndex);
					if (iter.setUp()) {
						try {
							fIndex++;
							while (iter.hasNext()) {
								try {
									iter.next();
									doIt(input);
								} catch (final ReturnException e) {
									return e.getValue();
								} catch (final BreakException e) {
									return F.Null;
								} catch (final ContinueException e) {
									continue;
								}

							}
						} finally {
							--fIndex;
							iter.tearDown();
						}
					}
					return F.Null;
				}
				fEngine.evaluate(input);

				return F.NIL;
			}
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 3);
			try {
				final List<IIterator<IExpr>> iterList = new ArrayList<IIterator<IExpr>>();
				ast.forEach(2, ast.size(), x -> iterList.add(Iterator.create((IAST) x, engine)));
				final DoIterator generator = new DoIterator(iterList, engine);
				return generator.doIt(ast.arg1());
			} catch (final ClassCastException e) {
				// the iterators are generated only from IASTs
			} catch (final NoEvalException e) {
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	private final static class FixedPoint extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 3, 4);

			try {
				// use EvalEngine's iterationLimit only for evaluation control
				// final int iterationLimit = engine.getIterationLimit();
				// int iterationCounter = 1;

				IExpr f = ast.arg1();
				IExpr current = ast.arg2();
				int iterations = Integer.MAX_VALUE;
				if (ast.isAST3()) {
					IExpr arg3 = ast.arg3();
					if (arg3.isInfinity()) {
						iterations = Integer.MAX_VALUE;
					} else if (arg3.isNegativeInfinity()) {
						iterations = Integer.MIN_VALUE;
					} else {
						iterations = Validate.checkIntType(arg3, Integer.MIN_VALUE);
					}
				}
				if (iterations < 0) {
					engine.printMessage("FixedPoint: Non-negative integer expected.");
					return F.NIL;
				}
				if (iterations > 0) {
					IExpr last;
					do {
						last = current;
						current = engine.evaluate(F.Apply(f, F.List(current)));
						// if (iterationLimit >= 0 && iterationLimit <= ++iterationCounter) {
						// IterationLimitExceeded.throwIt(iterationCounter, ast);
						// }
					} while ((!current.isSame(last)) && (--iterations > 0));
				}
				return current;

			} finally {
				engine.setNumericMode(false);
			}

		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	private final static class FixedPointList extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 3, 4);

			try {
				// use EvalEngine's iterationLimit only for evaluation control
				// final int iterationLimit = engine.getIterationLimit();
				// int iterationCounter = 1;

				IExpr f = ast.arg1();
				IASTAppendable list = F.ListAlloc(32);
				IExpr current = ast.arg2();
				list.append(current);
				int iterations = Integer.MAX_VALUE;
				if (ast.isAST3()) {
					if (ast.arg3().isInfinity()) {
						iterations = Integer.MAX_VALUE;
					} else if (ast.arg3().isNegativeInfinity()) {
						iterations = Integer.MIN_VALUE;
					} else {
						iterations = Validate.checkIntType(ast, 3, Integer.MIN_VALUE);
					}
				}
				if (iterations < 0) {
					engine.printMessage("FixedPoint: Non-negative integer expected.");
					return F.NIL;
				}
				if (iterations > 0) {
					IExpr last;
					do {
						last = current;
						current = engine.evaluate(F.Apply(f, F.List(current)));
						list.append(current);
						// if (iterationLimit >= 0 && iterationLimit <= ++iterationCounter) {
						// IterationLimitExceeded.throwIt(iterationCounter, ast);
						// }
					} while ((!current.isSame(last)) && (--iterations > 0));
				}
				return list;

			} finally {
				engine.setNumericMode(false);
			}

		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	/**
	 * For[] loop
	 * 
	 * Example: For[$j = 1, $j <= 10, $j++, Print[$j]]
	 * 
	 */
	private final static class For extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 4, 5);
			// use EvalEngine's iterationLimit only for evaluation control
			// final int iterationLimit = engine.getIterationLimit();
			// int iterationCounter = 1;

			// For(start, test, incr, body)
			engine.evaluate(ast.arg1()); // start
			IExpr test = ast.arg2();
			IExpr incr = ast.arg3();
			IExpr body = F.Null;
			if (ast.size() == 5) {
				body = ast.arg4();
			}
			boolean exit = false;
			while (true) {
				try {
					if (!engine.evaluate(test).isTrue()) {
						exit = true;
						return F.Null;
					}
					if (ast.size() == 5) {
						engine.evaluate(body);
					}
					// if (iterationLimit >= 0 && iterationLimit <= ++iterationCounter) {
					// IterationLimitExceeded.throwIt(iterationCounter, ast);
					// }
				} catch (final BreakException e) {
					exit = true;
					return F.Null;
				} catch (final ContinueException e) {
					// if (iterationLimit >= 0 && iterationLimit <= ++iterationCounter) {
					// IterationLimitExceeded.throwIt(iterationCounter, ast);
					// }
					continue;
				} catch (final ReturnException e) {
					return e.getValue();
				} finally {
					if (!exit) {
						engine.evaluate(incr);
					}
				}
			}
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	private final static class If extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 3, 5);

			final IExpr temp = engine.evaluate(ast.arg1());

			if (temp.isFalse()) {
				if (ast.size() >= 4) {
					return ast.arg3();
				}

				return F.Null;
			}

			if (temp.equals(F.True)) {
				return ast.arg2();
			}

			if (ast.size() == 5) {
				return ast.arg4();
			}

			return F.Null;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	private final static class Module extends AbstractCoreFunctionEvaluator {
		/**
		 *
		 */
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);

			if (ast.arg1().isList()) {
				IAST lst = (IAST) ast.arg1();
				IExpr arg2 = ast.arg2();
				return evalModule(lst, arg2, engine);
			}

			return F.NIL;
		}

		/**
		 * <code>Module[{variablesList}, rhs ]</code>
		 * 
		 * @param intializerList
		 * @param arg2
		 * @param engine
		 * @return
		 */
		private static IExpr evalModule(IAST intializerList, IExpr arg2, final EvalEngine engine) {
			final int moduleCounter = engine.incModuleCounter();
			final String varAppend = "$" + moduleCounter;
			final java.util.IdentityHashMap<ISymbol, IExpr> moduleVariables = new IdentityHashMap<ISymbol, IExpr>();

			try {
				rememberModuleVariables(intializerList, varAppend, moduleVariables, engine);
				IExpr subst = arg2.accept(new ModuleReplaceAll(moduleVariables, engine));
				if (subst.isPresent()) {
					return engine.evaluate(subst);
				}
				return arg2;
			} finally {
				// removeUserVariables(moduleVariables);
			}
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	private final static class Nest extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 4);

			return evaluateNest(ast, engine);
		}

		public static IExpr evaluateNest(final IAST ast, EvalEngine engine) {
			IExpr arg3 = engine.evaluate(ast.arg3());
			if (arg3.isInteger()) {
				final int n = Validate.checkIntType(arg3);
				return nest(ast.arg2(), n, x -> F.unaryAST1(ast.arg1(), x), engine);
			}
			return F.NIL;
		}

		public static IExpr nest(final IExpr expr, final int n, final Function<IExpr, IExpr> fn, EvalEngine engine) {
			IExpr temp = expr;
			for (int i = 0; i < n; i++) {
				temp = engine.evaluate(fn.apply(temp));
			}
			return temp;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	private final static class NestList extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 4);

			return evaluateNestList(ast, List(), engine);
		}

		public static IExpr evaluateNestList(final IAST ast, final IASTAppendable resultList, EvalEngine engine) {
			IExpr arg3 = engine.evaluate(ast.arg3());
			if (arg3.isInteger()) {
				final int n = Validate.checkIntType(arg3);
				IExpr arg1 = ast.arg1();
				nestList(ast.arg2(), n, x -> F.unaryAST1(arg1, x), resultList, engine);
				return resultList;
			}
			return F.NIL;
		}

		public static void nestList(final IExpr expr, final int n, final Function<IExpr, IExpr> fn,
				final IASTAppendable resultList, EvalEngine engine) {
			IExpr temp = expr;
			resultList.append(temp);
			for (int i = 0; i < n; i++) {
				temp = engine.evaluate(fn.apply(temp));
				resultList.append(temp);
			}
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	private final static class NestWhile extends NestWhileList {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 4);

			return nestWhile(ast.arg2(), engine.evaluate(ast.arg3()), x -> F.unaryAST1(ast.arg1(), x), engine);
		}

		public static IExpr nestWhile(final IExpr expr, final IExpr test, final Function<IExpr, IExpr> fn,
				EvalEngine engine) {
			IExpr temp = expr;
			while (engine.evalTrue(F.unaryAST1(test, temp))) {
				temp = engine.evaluate(fn.apply(temp));
			}
			return temp;

		}

	}

	private static class NestWhileList extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 4);

			IExpr arg1 = ast.arg1();
			return nestList(ast.arg2(), engine.evaluate(ast.arg3()), x -> F.unaryAST1(arg1, x), List(), engine);
			// Functors.append(F.ast(ast.arg1())), List(), engine);
		}

		public static IAST nestList(final IExpr expr, final IExpr test, final Function<IExpr, IExpr> fn,
				final IASTAppendable resultList, EvalEngine engine) {
			IExpr temp = expr;
			while (engine.evalTrue(F.unaryAST1(test, temp))) {
				resultList.append(temp);
				temp = engine.evaluate(fn.apply(temp));
			}
			resultList.append(temp);
			return resultList;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	private final static class Part extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.size() >= 3) {
				try {
					IExpr arg1 = engine.evalLoop(ast.arg1());
					if (arg1.isPresent()) {
						if (!arg1.isAST()) {
							IExpr result = ast.setAtClone(1, arg1);
							engine.printMessage("Part: " + result + " could not extract a part");
							return result;
						}
					} else {
						arg1 = ast.arg1();
						if (!arg1.isAST()) {
							engine.printMessage("Part: " + ast + " could not extract a part");
							return F.NIL;
						}
					}
					IASTMutable evaledAST = F.NIL;

					boolean numericMode = engine.isNumericMode();
					IExpr temp;
					try {
						int astSize = ast.size();
						for (int i = 2; i < astSize; i++) {
							temp = engine.evalLoop(ast.get(i));
							if (temp.isPresent()) {
								if (evaledAST.isPresent()) {
									evaledAST.set(i, temp);
								} else {
									evaledAST = ast.copy();
									evaledAST.addEvalFlags(ast.getEvalFlags() & IAST.IS_MATRIX_OR_VECTOR);
									evaledAST.set(i, temp);
								}
							}
						}
					} finally {
						engine.setNumericMode(numericMode);
					}
					if (evaledAST.isPresent()) {
						return part(arg1, evaledAST, 2, engine);
					}
					return part(arg1, ast, 2, engine);

				} catch (WrongArgumentType wat) {
					engine.printMessage(wat.getMessage());
				}
			}
			return F.NIL;
		}

	}

	private static class Print extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			final PrintStream s = engine.getOutPrintStream();
			final PrintStream stream;
			if (s == null) {
				stream = System.out;
			} else {
				stream = s;
			}
			final StringBuilder buf = new StringBuilder();
			OutputFormFactory out = OutputFormFactory.get();
			ast.forEach(x -> {
				IExpr temp = engine.evaluate(x);
				if (temp instanceof IStringX) {
					buf.append(temp.toString());
				} else {
					try {
						out.convert(buf, temp);
					} catch (IOException e) {
						stream.println(e.getMessage());
						if (Config.DEBUG) {
							e.printStackTrace();
						}
					}
				}
			});
			stream.println(buf.toString());
			return F.Null;
		}

	}

	/**
	 * The call <code>Quiet( expr )</code> evaluates <code>expr</code> in &quot;quiet&quot; mode (i.e. no warning
	 * messages are shown during evaluation).
	 * 
	 */
	private static class Quiet extends AbstractCoreFunctionEvaluator {
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);
			boolean quietMode = engine.isQuietMode();
			try {
				engine.setQuietMode(true);
				return engine.evaluate(ast.arg1());
			} finally {
				engine.setQuietMode(quietMode);
			}
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	private final static class Reap extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			IASTAppendable oldList = engine.getReapList();
			try {
				IASTAppendable reapList = F.ListAlloc(10);
				engine.setReapList(reapList);
				IExpr expr = engine.evaluate(ast.arg1());
				if (reapList.isAST0()) {
					return F.List(expr, F.CEmptyList);
				}
				return F.List(expr, F.List(reapList));
			} finally {
				engine.setReapList(oldList);
			}

		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	private final static class Return extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST0()) {
				throw new ReturnException();
			}
			if (ast.isAST1()) {
				if (ast.arg1().isTrue()) {
					throw ReturnException.RETURN_TRUE;
				}
				if (ast.arg1().isTrue()) {
					throw ReturnException.RETURN_FALSE;
				}
				throw new ReturnException(engine.evaluate(ast.arg1()));
			}
			Validate.checkRange(ast, 1, 2);

			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

	}

	private final static class Sow extends AbstractCoreFunctionEvaluator {

		// public final static double DEFAULT_CHOP_DELTA = 10E-10;

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			IASTAppendable reapList = engine.getReapList();
			IExpr expr = engine.evaluate(ast.arg1());
			if (reapList != null) {
				reapList.append(expr);
			}
			return expr;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	private final static class Switch extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			// Validate.checkRange(ast, 4);
			if ((ast.size() & 0x0001) != 0x0000) {
				engine.printMessage("Switch: number of arguments must be odd");
			}
			if (ast.size() > 3) {
				final IExpr arg1 = engine.evaluate(ast.arg1());
				IPatternMatcher matcher;
				for (int i = 2; i < ast.size(); i += 2) {
					matcher = engine.evalPatternMatcher(ast.get(i));
					if (matcher.test(arg1, engine) && (i + 1 < ast.size())) {
						return engine.evaluate(ast.get(i + 1));
					}
				}
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	/**
	 * TODO implement &quot;TimeConstrained&quot; mode
	 * 
	 */
	private static class TimeConstrained extends AbstractCoreFunctionEvaluator {

		private static class EvalCallable implements Callable<IExpr> {
			private final EvalEngine fEngine;
			private final IExpr fExpr;

			public EvalCallable(IExpr expr, EvalEngine engine) {
				fExpr = expr;
				fEngine = engine;
			}

			@Override
			public IExpr call() throws Exception {
				// TODO Auto-generated method stub
				return fEngine.evaluate(fExpr);
			}

		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 3, 4);

			if (Config.JAS_NO_THREADS) {
				// no thread can be spawned
				try {
					return engine.evaluate(ast.arg1());
				} catch (final MathException e) {
					throw e;
				} catch (final Throwable th) {
					if (ast.isAST3()) {
						return ast.arg3();
					}
				}
				return F.Aborted;
			}

			IExpr arg2 = engine.evaluate(ast.arg2());
			long seconds = 0L;
			try {
				// if (ast.arg2().toString().equals("§timelimit")){
				// arg2=F.num(5.0);
				// }
				if (arg2.isSignedNumber()) {
					arg2 = ((ISignedNumber) arg2).ceilFraction();
					seconds = ((ISignedNumber) arg2).toLong();
				} else {
					engine.printMessage("TimeConstrained: " + ast.arg2().toString() + " is not a Java long value.");
					return F.NIL;
				}

			} catch (ArithmeticException ae) {
				engine.printMessage("TimeConstrained: " + ast.arg2().toString() + " is not a Java long value.");
				return F.NIL;
			}

			TimeLimiter timeLimiter = new SimpleTimeLimiter();
			Callable<IExpr> work = new EvalCallable(ast.arg1(), engine);

			try {
				return timeLimiter.callWithTimeout(work, seconds, TimeUnit.SECONDS, true);
			} catch (java.util.concurrent.TimeoutException e) {
				if (ast.isAST3()) {
					return ast.arg3();
				}
				return F.Aborted;
			} catch (com.google.common.util.concurrent.UncheckedTimeoutException e) {
				if (ast.isAST3()) {
					return ast.arg3();
				}
				return F.Aborted;
			} catch (Exception e) {
				if (Config.DEBUG) {
					e.printStackTrace();
				}
				return F.Null;
			}

		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	/**
	 * Calculate the time needed for evaluating an expression
	 * 
	 */
	private static class Timing extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			final long begin = System.currentTimeMillis();
			final IExpr result = engine.evaluate(ast.arg1());
			return List(Divide(F.num(System.currentTimeMillis() - begin), F.integer(1000L)), F.Hold(result));
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	private final static class Throw extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST1()) {
				throw new ThrowException(engine.evaluate(ast.arg1()));
			}
			Validate.checkSize(ast, 2);

			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	/**
	 * <pre>
	 * Trace(expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * return the evaluation steps which are used to get the result.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Trace(D(Sin(x),x))
	 * {{Cos(#1)&amp;[x],Cos(x)},D(x,x)*Cos(x),{D(x,x),1},1*Cos(x),Cos(x)}
	 * </pre>
	 */
	private static class Trace extends AbstractCoreFunctionEvaluator {
		/**
		 * Trace the evaluation steps for a given expression. The resulting trace expression list is wrapped by Hold
		 * (i.e. <code>Hold[{...}]</code>.
		 * 
		 */
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			final IExpr temp = ast.arg1();
			IPatternMatcher matcher = null;
			if (ast.isAST2()) {
				matcher = engine.evalPatternMatcher(ast.arg2());
			}

			return engine.evalTrace(temp, matcher, F.List());
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	private static class Unevaluated extends AbstractCoreFunctionEvaluator {
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (!ToggleFeature.UNEVALUATED) {
				return F.NIL;
			}

			Validate.checkSize(ast, 2);
			IExpr arg1 = engine.evaluate(ast.arg1());

			return arg1;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			if (!ToggleFeature.UNEVALUATED) {
				return;
			}
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	private final static class Which extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			// Validate.checkEven(ast);
			if (((ast.size() - 1) & 0x0001) == 0x0001) {
				engine.printMessage("Which: number of arguments must be evaen");
			}
			for (int i = 1; i < ast.size(); i += 2) {
				IExpr temp = engine.evaluate(ast.get(i));
				if (temp.isFalse()) {
					continue;
				}
				if (temp.isTrue()) {
					if ((i + 1 < ast.size())) {
						return engine.evaluate(ast.get(i + 1));
					}
					continue;
				}
				if (i == 1) {
					return F.NIL;
				}
				return F.ast(ast, ast.head(), true, i, ast.size());
			}
			return F.Null;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	private final static class While extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 2, 3);

			// use EvalEngine's iterationLimit only for evaluation control
			// final int iterationLimit = engine.getIterationLimit();
			// int iterationCounter = 1;

			// While(test, body)
			IExpr test = ast.arg1();
			IExpr body = F.Null;
			if (ast.isAST2()) {
				body = ast.arg2();
			}

			while (engine.evaluate(test).isTrue()) {
				try {
					if (ast.isAST2()) {
						engine.evaluate(body);
					}
					// if (iterationLimit >= 0 && iterationLimit <= ++iterationCounter) {
					// IterationLimitExceeded.throwIt(iterationCounter, ast);
					// }
				} catch (final BreakException e) {
					return F.Null;
				} catch (final ContinueException e) {
					continue;
				} catch (final ReturnException e) {
					return e.getValue();
				}
			}

			return F.Null;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	private final static class With extends AbstractCoreFunctionEvaluator {
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);

			if (ast.arg1().isList()) {
				IAST lst = (IAST) ast.arg1();
				IExpr arg2 = ast.arg2();
				return evalWith(lst, arg2, engine);
			}

			return F.NIL;
		}

		/**
		 * <code>Module[{variablesList}, rhs ]</code>
		 * 
		 * @param intializerList
		 * @param arg2
		 * @param engine
		 * @return
		 */
		private static IExpr evalWith(IAST intializerList, IExpr arg2, final EvalEngine engine) {
			final int moduleCounter = engine.incModuleCounter();
			final String varAppend = "$" + moduleCounter;
			final java.util.IdentityHashMap<ISymbol, IExpr> moduleVariables = new IdentityHashMap<ISymbol, IExpr>();
			final java.util.Set<IExpr> renamedVarsSet = new HashSet<IExpr>();
			final java.util.IdentityHashMap<ISymbol, ISymbol> renamedVars = new IdentityHashMap<ISymbol, ISymbol>();

			try {
				rememberWithVariables(intializerList, moduleVariables, renamedVarsSet, engine);
				for (IExpr expr : renamedVarsSet) {
					if (expr.isSymbol()) {
						renamedVars.put((ISymbol) expr, F.$s(expr.toString() + varAppend));
					}
				}
				IExpr subst = arg2.accept(new ModuleReplaceAll(moduleVariables, engine));
				if (subst.isPresent()) {
					return engine.evaluate(subst);
				}
				return arg2;
			} finally {
				// removeUserVariables(moduleVariables);
			}
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	/**
	 * Remember which local variable names (appended with the module counter) we use in the given
	 * <code>variablesMap</code>.
	 * 
	 * @param variablesList
	 *            initializer variables list from the <code>Module</code> function
	 * @param variablesMap
	 *            the resulting module variables map
	 */
	private static void rememberWithVariables(IAST variablesList, final java.util.Map<ISymbol, IExpr> variablesMap,
			final java.util.Set<IExpr> renamedVars, EvalEngine engine) {
		ISymbol oldSymbol;
		for (int i = 1; i < variablesList.size(); i++) {
			// if (variablesList.get(i).isSymbol()) {
			// oldSymbol = (ISymbol) variablesList.get(i);
			// newSymbol = F.userSymbol(oldSymbol.toString() + varAppend, engine);
			// variablesMap.put(oldSymbol, newSymbol);
			// } else {
			if (variablesList.get(i).isAST(F.Set, 3)) {
				final IAST setFun = (IAST) variablesList.get(i);
				if (setFun.arg1().isSymbol()) {
					oldSymbol = (ISymbol) setFun.arg1();
					IExpr rightHandSide = setFun.arg2();
					try {
						IExpr temp = engine.evaluate(rightHandSide);
						VariablesSet.addVariables(renamedVars, temp);
						variablesMap.put(oldSymbol, temp);
					} catch (MathException me) {
						if (Config.DEBUG) {
							me.printStackTrace();
						}
						variablesMap.put(oldSymbol, rightHandSide);
					}

				}
			}
			// }
		}
	}

	/**
	 * Remember which local variable names (appended with the module counter) we use in the given
	 * <code>variablesMap</code>.
	 * 
	 * @param variablesList
	 *            initializer variables list from the <code>Module</code> function
	 * @param varAppend
	 *            the module counter string which aer appended to the variable names.
	 * @param variablesMap
	 *            the resulting module variables map
	 * @param engine
	 *            the evaluation engine
	 */
	private static void rememberModuleVariables(IAST variablesList, final String varAppend,
			final java.util.Map<ISymbol, IExpr> variablesMap, final EvalEngine engine) {
		ISymbol oldSymbol;
		ISymbol newSymbol;
		for (int i = 1; i < variablesList.size(); i++) {
			if (variablesList.get(i).isSymbol()) {
				oldSymbol = (ISymbol) variablesList.get(i);
				// if (oldSymbol.toString().equals("num")){
				// System.out.println(variablesList.toString());
				// }
				newSymbol = F.userSymbol(oldSymbol.toString() + varAppend, engine);
				variablesMap.put(oldSymbol, newSymbol);
				// newSymbol.pushLocalVariable();
				engine.localStackCreate(newSymbol).push(F.NIL);
			} else {
				if (variablesList.get(i).isAST(F.Set, 3)) {
					final IAST setFun = (IAST) variablesList.get(i);
					if (setFun.arg1().isSymbol()) {
						oldSymbol = (ISymbol) setFun.arg1();
						newSymbol = F.userSymbol(oldSymbol.toString() + varAppend, engine);
						variablesMap.put(oldSymbol, newSymbol);
						IExpr rightHandSide = setFun.arg2();
						try {
							rightHandSide = engine.evaluate(rightHandSide);
						} catch (MathException me) {
							if (Config.DEBUG) {
								me.printStackTrace();
							}
						}
						engine.localStackCreate(newSymbol).push(rightHandSide);
						// newSymbol.pushLocalVariable(rightHandSide);
					}
				}
			}
		}
	}

	/**
	 * Remove all <code>moduleVariables</code> from this evaluation engine.
	 * 
	 * @param moduleVariables
	 */
	public static void removeUserVariables(final Map<ISymbol, ISymbol> moduleVariables) {
		// remove all module variables from eval engine
		ISymbol temp;
		for (ISymbol symbol : moduleVariables.values()) {
			temp = F.removeUserSymbol(symbol.toString());
			// if (Config.DEBUG && temp == null) {
			// throw new NullPointerException("Remove user-defined variabe: " +
			// symbol.toString());
			// }
		}
	}

	/**
	 * Check the (possible nested) module condition in pattern matcher without evaluating a result.
	 * 
	 * @param arg1
	 * @param arg2
	 * @param engine
	 * @return
	 */
	public static boolean checkModuleOrWithCondition(IExpr arg1, IExpr arg2, final EvalEngine engine) {
		if (arg1.isList()) {
			IAST intializerList = (IAST) arg1;
			final int moduleCounter = engine.incModuleCounter();
			final String varAppend = "$" + moduleCounter;
			final java.util.Map<ISymbol, IExpr> moduleVariables = new IdentityHashMap<ISymbol, IExpr>();

			try {
				rememberModuleVariables(intializerList, varAppend, moduleVariables, engine);
				IExpr result = F.subst(arg2, x -> {
					IExpr temp = moduleVariables.get(x);
					return temp != null ? temp : F.NIL;
				});
				if (result.isCondition()) {
					return checkCondition(result.getAt(1), result.getAt(2), engine);
				} else if (result.isModuleOrWith()) {
					return checkModuleOrWithCondition(result.getAt(1), result.getAt(2), engine);
				}
			} finally {
				// removeUserVariables(moduleVariables);
			}
		}
		return true;
	}

	/**
	 * Check the (possible nested) condition in pattern matcher without evaluating a result.
	 * 
	 * @param arg1
	 * @param arg2
	 * @param engine
	 * @return
	 */
	public static boolean checkCondition(IExpr arg1, IExpr arg2, final EvalEngine engine) {
		if (engine.evalTrue(arg2)) {
			if (arg1.isCondition()) {
				return checkCondition(arg1.getAt(1), arg1.getAt(2), engine);
			} else if (arg2.isModuleOrWith()) {
				return checkModuleOrWithCondition(arg2.getAt(1), arg2.getAt(2), engine);
			}
			return true;
		}
		return false;
	}

	/**
	 * Get the element stored at the given <code>position</code>.
	 * 
	 * @param ast
	 * @param position
	 * @return
	 */
	private static IExpr getIndex(IAST ast, int position) {
		if (position < 0) {
			position = ast.size() + position;
		}
		if ((position < 0) || (position >= ast.size())) {
			throw new WrappedException(new IndexOutOfBoundsException(
					"Part[] index " + position + " of " + ast.toString() + " is out of bounds."));
		}
		return ast.get(position);
	}

	/**
	 * Get the <code>Part[...]</code> of an expression. If the expression is no <code>IAST</code> return the expression.
	 * 
	 * @param expr
	 *            the expression from which parts should be extracted
	 * @param ast
	 *            the <code>Part[...]</code> expression
	 * @param pos
	 *            the index position from which the sub-expressions should be extracted
	 * @param engine
	 *            the evaluation engine
	 * @return
	 */
	public static IExpr part(final IExpr expr, final IAST ast, int pos, EvalEngine engine) {
		if (!expr.isAST() || pos >= ast.size()) {
			return expr;
		}
		IAST arg1 = (IAST) expr;
		final IExpr arg2 = ast.get(pos);
		int p1 = pos + 1;
		int[] span = arg2.isSpan(arg1.size());
		if (span != null) {
			int start = span[0];
			int last = span[1];
			int step = span[2];
			IASTAppendable result = arg1.copyHead();

			if (step < 0 && start >= last) {
				for (int i = start; i >= last; i += step) {
					result.append(part(arg1.get(i), ast, p1, engine));
				}
			} else if (step > 0 && (last != 1 || start <= last)) {
				for (int i = start; i <= last; i += step) {
					result.append(part(arg1.get(i), ast, p1, engine));
				}
			} else {
				throw new WrongArgumentType(ast, arg2, pos,
						"Wrong argument for Part[] function: " + arg2.toString() + " selects no part expression.");
			}
			return result;
		} else if (arg2.isSignedNumber()) {
			final int indx = Validate.checkIntType(ast, pos, Integer.MIN_VALUE);
			IExpr result = null;
			result = getIndex(arg1, indx);
			if (p1 < ast.size()) {
				if (result.isAST()) {
					return part(result, ast, p1, engine);
				} else {
					throw new WrongArgumentType(ast, arg1, pos,
							"Wrong argument for Part[] function. Function or list expected.");
				}
			}
			return result;
		} else if (arg2.isList()) {
			IExpr temp = null;
			final IAST list = (IAST) arg2;
			final IASTAppendable result = F.ListAlloc(list.size());

			for (int i = 1; i < list.size(); i++) {
				final IExpr listArg = list.get(i);
				if (listArg.isInteger()) {
					IExpr ires = null;

					final int indx = Validate.checkIntType(listArg, Integer.MIN_VALUE);
					ires = getIndex(arg1, indx);
					if (ires == null) {
						return F.NIL;
					}
					if (p1 < ast.size()) {
						if (ires.isAST()) {
							temp = part(ires, ast, p1, engine);
							result.append(temp);
						} else {
							throw new WrongArgumentType(ast, arg1, pos,
									"Wrong argument for Part[] function. Function or list expected.");
						}
					} else {
						result.append(ires);
					}
				}
			}
			return result;
		}
		throw new WrongArgumentType(ast, arg1, pos,
				"Wrong argument for Part[] function: " + arg2.toString() + " selects no part expression.");
	}

	public static IExpr assignPart(final IExpr assignedExpr, final IAST part, int partPosition, IExpr value,
			EvalEngine engine) {
		if (!assignedExpr.isAST() || partPosition >= part.size()) {
			return value;
		}
		IAST assignedAST = (IAST) assignedExpr;
		final IExpr arg2 = engine.evaluate(part.get(partPosition));
		int partPositionPlus1 = partPosition + 1;
		int[] span = arg2.isSpan(assignedAST.size());
		if (span != null) {
			int start = span[0];
			int last = span[1];
			int step = span[2];
			IASTAppendable result = F.NIL;
			IExpr element;

			if (step < 0 && start >= last) {
				for (int i = start; i >= last; i += step) {
					element = assignedAST.get(i);
					result = assignPartSpanValue(assignedAST, element, part, partPositionPlus1, result, i, value,
							engine);
				}
			} else if (step > 0 && (last != 1 || start <= last)) {
				for (int i = start; i <= last; i += step) {
					element = assignedAST.get(i);
					result = assignPartSpanValue(assignedAST, element, part, partPositionPlus1, result, i, value,
							engine);
				}
			} else {
				throw new WrongArgumentType(part, arg2, partPosition,
						"Wrong argument for Part[] function: " + arg2.toString() + " selects no part expression.");
			}
			return result;
		} else if (arg2.isSignedNumber()) {
			int indx = Validate.checkIntType(arg2, Integer.MIN_VALUE);
			if (indx < 0) {
				indx = part.size() + indx;
			}
			if ((indx < 0) || (indx >= part.size())) {
				throw new WrappedException(new IndexOutOfBoundsException(
						"Part[] index " + indx + " of " + part.toString() + " is out of bounds."));
			}
			IASTAppendable result = F.NIL;
			IExpr temp = assignPart(assignedAST.get(indx), part, partPositionPlus1, value, engine);
			if (temp.isPresent()) {
				if (!result.isPresent()) {
					result = assignedAST.copyAppendable();
				}
				result.set(indx, temp);
			}
			return result;
		} else if (arg2.isList()) {
			IExpr temp = null;
			final IAST list = (IAST) arg2;
			final IASTAppendable result = F.ListAlloc(list.size());

			for (int i = 1; i < list.size(); i++) {
				final IExpr listArg = list.get(i);
				if (listArg.isInteger()) {
					IExpr ires = null;

					final int indx = Validate.checkIntType(listArg, Integer.MIN_VALUE);
					ires = assignPartValue(assignedAST, indx, value);
					if (ires == null) {
						return F.NIL;
					}
					if (partPositionPlus1 < part.size()) {
						if (ires.isAST()) {
							temp = assignPart(ires, part, partPositionPlus1, value, engine);
							result.append(temp);
						} else {
							throw new WrongArgumentType(part, assignedAST, partPosition,
									"Wrong argument for Part[] function. Function or list expected.");
						}
					} else {
						result.append(ires);
					}
				}
			}
			return result;
		}
		throw new WrongArgumentType(part, assignedAST, partPosition,
				"Wrong argument for Part[] function: " + arg2.toString() + " selects no part expression.");
	}

	public static IExpr assignPart(final IExpr assignedExpr, final IAST part, int partPosition, IAST rhs, int rhsPos,
			EvalEngine engine) {
		if (!assignedExpr.isAST() || partPosition >= part.size()) {
			return assignedExpr;
		}
		IAST assignedAST = (IAST) assignedExpr;
		final IExpr arg2 = part.get(partPosition);
		int partPositionPlus1 = partPosition + 1;
		int[] span = arg2.isSpan(assignedAST.size());
		if (span != null) {
			int start = span[0];
			int last = span[1];
			int step = span[2];
			IASTAppendable result = F.NIL;

			if (step < 0 && start >= last) {
				int rhsIndx = 1;
				for (int i = start; i >= last; i += step) {
					IExpr temp = rhs.get(rhsIndx++);
					if (!temp.isList()) {
						temp = assignPart(assignedAST.get(i), part, partPositionPlus1, temp, engine);
					} else {
						temp = assignPart(assignedAST.get(i), part, partPositionPlus1, (IAST) temp, 1, engine);
					}

					if (temp.isPresent()) {
						if (!result.isPresent()) {
							result = assignedAST.copyAppendable();
						}
						result.set(i, temp);
					}
				}
			} else if (step > 0 && (last != 1 || start <= last)) {
				int rhsIndx = 1;
				for (int i = start; i <= last; i += step) {
					IExpr temp = rhs.get(rhsIndx++);
					if (!temp.isList()) {
						temp = assignPart(assignedAST.get(i), part, partPositionPlus1, temp, engine);
					} else {
						temp = assignPart(assignedAST.get(i), part, partPositionPlus1, (IAST) temp, 1, engine);
					}

					if (temp.isPresent()) {
						if (!result.isPresent()) {
							result = assignedAST.copyAppendable();
						}
						result.set(i, temp);
					}
				}
			} else {
				throw new WrongArgumentType(part, arg2, partPosition,
						"Wrong argument for Part[] function: " + arg2.toString() + " selects no part expression.");
			}
			return result;
		} else if (arg2.isSignedNumber()) {
			final int indx = Validate.checkIntType(part, partPosition, Integer.MIN_VALUE);
			IExpr ires = null;
			ires = assignPartValue(assignedAST, indx, rhs.getAST(rhsPos++));
			if (partPositionPlus1 < part.size()) {
				if (ires.isAST()) {
					return assignPart(ires, part, partPositionPlus1, rhs, rhsPos++, engine);
				} else {
					throw new WrongArgumentType(part, assignedAST, partPosition,
							"Wrong argument for Part[] function. Function or list expected.");
				}
			}
			return ires;
		} else if (arg2.isList()) {
			IExpr temp = null;
			final IAST list = (IAST) arg2;
			final IASTAppendable result = F.ListAlloc(list.size());

			for (int i = 1; i < list.size(); i++) {
				final IExpr listArg = list.get(i);
				if (listArg.isInteger()) {
					IExpr ires = null;

					final int indx = Validate.checkIntType(listArg, Integer.MIN_VALUE);
					ires = assignPartValue(assignedAST, indx, list);
					if (ires == null) {
						return F.NIL;
					}
					if (partPositionPlus1 < part.size()) {
						if (ires.isAST()) {
							temp = assignPart(ires, part, partPositionPlus1, rhs, rhsPos++, engine);
							result.append(temp);
						} else {
							throw new WrongArgumentType(part, assignedAST, partPosition,
									"Wrong argument for Part[] function. Function or list expected.");
						}
					} else {
						result.append(ires);
					}
				}
			}
			return result;
		}
		throw new WrongArgumentType(part, assignedAST, partPosition,
				"Wrong argument for Part[] function: " + arg2.toString() + " selects no part expression.");
	}

	/**
	 * Assign the <code>value</code> to the given position in the left-hand-side. <code>lhs[[position]] = value</code>
	 * 
	 * @param lhs
	 *            left-hand-side
	 * @param partPosition
	 * @param value
	 * @return
	 */
	private static IExpr assignPartValue(IAST lhs, int partPosition, IExpr value) {
		if (partPosition < 0) {
			partPosition = lhs.size() + partPosition;
		}
		if ((partPosition < 0) || (partPosition >= lhs.size())) {
			throw new WrappedException(new IndexOutOfBoundsException(
					"Part[] index " + partPosition + " of " + lhs.toString() + " is out of bounds."));
		}
		return lhs.setAtClone(partPosition, value);
	}

	/**
	 * Call <code>assignPart(element, ast, pos, value, engine)</code> recursively and assign the result to the given
	 * position in the result. <code>result[[position]] = resultValue</code>
	 * 
	 * @param expr
	 * @param element
	 * @param partPosition
	 * @param pos
	 * @param result
	 *            will be cloned if an assignment occurs and returned by this method
	 * @param position
	 * @param value
	 * @param engine
	 *            the evaluation engineF
	 * @return the (cloned and value assigned) result AST from input
	 */
	private static IASTAppendable assignPartSpanValue(IAST expr, IExpr element, final IAST part, int partPosition,
			IASTAppendable result, int position, IExpr value, EvalEngine engine) {
		IExpr resultValue = assignPart(element, part, partPosition, value, engine);
		if (resultValue.isPresent()) {
			if (!result.isPresent()) {
				result = expr.copyAppendable();
			}
			result.set(position, resultValue);
		}
		return result;
	}

	public static Programming initialize() {
		return CONST;
	}

	private Programming() {

	}
}
