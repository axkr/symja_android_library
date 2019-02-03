package org.matheclipse.core.builtin;

import static org.matheclipse.core.expression.F.Divide;
import static org.matheclipse.core.expression.F.List;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.basic.ToggleFeature;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.AbortException;
import org.matheclipse.core.eval.exception.BreakException;
import org.matheclipse.core.eval.exception.ConditionException;
import org.matheclipse.core.eval.exception.ContinueException;
import org.matheclipse.core.eval.exception.NoEvalException;
import org.matheclipse.core.eval.exception.RecursionLimitExceeded;
import org.matheclipse.core.eval.exception.ReturnException;
import org.matheclipse.core.eval.exception.ThrowException;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrappedException;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.ISetEvaluator;
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
import org.matheclipse.core.patternmatching.RulesData;
import org.matheclipse.core.visit.ModuleReplaceAll;
import org.matheclipse.parser.client.SyntaxError;

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
		F.List.setEvaluator(new ListFunction());
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

	/**
	 * <pre>
	 * Abort()
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * aborts an evaluation completely and returns <code>$Aborted</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Print("a"); Abort(); Print("b")
	 * $Aborted
	 * </pre>
	 */
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
	 * <pre>
	 * Break()
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * exits a <code>For</code>, <code>While</code>, or <code>Do</code> loop.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; n = 0
	 * &gt;&gt; While(True, If(n&gt;10, Break()); n=n+1)
	 * &gt;&gt; n
	 * 11
	 * </pre>
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
	 * <pre>
	 * Block({list_of_local_variables}, expr )
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * evaluates <code>expr</code> for the <code>list_of_local_variables</code>
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; $blck=Block({$i=10}, $i=$i+1; Return($i))
	 * 11
	 * </pre>
	 */
	private final static class Block extends AbstractCoreFunctionEvaluator {
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.size() == 3) {
				if (ast.arg1().isList()) {
					final IAST blockVariablesList = (IAST) ast.arg1();
					return engine.evalBlock(ast.arg2(), blockVariablesList);
				}

				return F.NIL;
			}
			Validate.checkSize(ast, 3);
			return F.NIL;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	private final static class Catch extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.size() == 2) {
				try {
					return engine.evaluate(ast.arg1());
				} catch (final ThrowException e) {
					return e.getValue();
				}
			}
			Validate.checkSize(ast, 2);
			return F.NIL;
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

	/**
	 * <pre>
	 * CompoundExpression(expr1, expr2, ...)
	 * </pre>
	 * <p>
	 * or
	 * </p>
	 * 
	 * <pre>
	 * expr1; expr2; ...
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * evaluates its arguments in turn, returning the last result.
	 * </p>
	 * </blockquote>
	 */
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

	/**
	 * <pre>
	 * Condition(pattern, expr)
	 * </pre>
	 * <p>
	 * or
	 * </p>
	 * 
	 * <pre>
	 * pattern /; expr
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * places an additional constraint on <code>pattern</code> that only allows it to match if <code>expr</code>
	 * evaluates to <code>True</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * <p>
	 * The controlling expression of a <code>Condition</code> can use variables from the pattern:
	 * </p>
	 * 
	 * <pre>
	 * &gt; f(3) /. f(x_) /; x&gt;0 -&gt; t
	 * t
	 * 
	 * &gt;&gt; f(-3) /. f(x_) /; x&gt;0 -&gt; t
	 * f(-3)
	 * </pre>
	 * <p>
	 * <code>Condition</code> can be used in an assignment:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; f(x_) := p(x) /; x&gt;0
	 * &gt;&gt; f(3)
	 * p(3)
	 * 
	 * &gt;&gt; f(-3)
	 * f(-3)
	 * </pre>
	 */
	private final static class Condition extends AbstractCoreFunctionEvaluator {

		@Override
		public final IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST2()) {
				if (engine.evalTrue(ast.arg2())) {
					return ast.arg1();
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

	/**
	 * <pre>
	 * Continue()
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * continues with the next iteration in a <code>For</code>, <code>While</code>, or <code>Do</code> loop.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; For(i=1, i&lt;=8, i=i+1, If(Mod(i,2) == 0, Continue()); Print(i))
	 *  | 1
	 *  | 3
	 *  | 5
	 *  | 7
	 * </pre>
	 */
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
			// IExpr arg1=ast.arg1();
			// if (arg1.isAST()){
			// IAST copy=(IAST)arg1.copy();
			// copy.addEvalFlags(IAST.DEFER_AST);
			// return copy;
			// }
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
			if (ast.size() >= 3) {
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
			Validate.checkRange(ast, 3);
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	/**
	 * <pre>
	 * FixedPoint(f, expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * starting with <code>expr</code>, iteratively applies <code>f</code> until the result no longer changes.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * FixedPoint(f, expr, n)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * performs at most <code>n</code> iterations.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; FixedPoint(Cos, 1.0)
	 * 0.7390851332151607
	 * 
	 * &gt;&gt; FixedPoint(#+1 &amp;, 1, 20)
	 * 21
	 * 
	 * &gt;&gt; FixedPoint(f, x, 0)
	 * x
	 * </pre>
	 * <p>
	 * Non-negative integer expected.
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; FixedPoint(f, x, -1)
	 * FixedPoint(f, x, -1)
	 * 
	 * &gt;&gt; FixedPoint(Cos, 1.0, Infinity)
	 * 0.739085
	 * </pre>
	 */
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

	/**
	 * <pre>
	 * FixedPointList(f, expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * starting with <code>expr</code>, iteratively applies <code>f</code> until the result no longer changes, and
	 * returns a list of all intermediate results.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * FixedPointList(f, expr, n)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * performs at most <code>n</code> iterations.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; FixedPointList(Cos, 1.0, 4)   
	 * {1.0,0.5403023058681398,0.8575532158463934,0.6542897904977791,0.7934803587425656}
	 * </pre>
	 * <p>
	 * Observe the convergence of Newton's method for approximating square roots:<br />
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; newton(n_) := FixedPointList(.5(# + n/#) &amp;, 1.);   
	 * &gt;&gt; newton(9)   
	 * {1.0,5.0,3.4,3.023529411764706,3.00009155413138,3.000000001396984,3.0,3.0}
	 * </pre>
	 * <p>
	 * Get the &ldquo;hailstone&rdquo; sequence of a number:<br />
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; collatz(1) := 1;   
	 * &gt;&gt; collatz(x_ ? EvenQ) := x / 2;   
	 * &gt;&gt; collatz(x_) := 3*x + 1;   
	 * &gt;&gt; FixedPointList(collatz, 14)   
	 * {14,7,22,11,34,17,52,26,13,40,20,10,5,16,8,4,2,1,1}
	 * </pre>
	 * 
	 * <pre>
	 * ``` 
	 * &gt;&gt; FixedPointList(f, x, 0)   
	 * {x}
	 * </pre>
	 * <p>
	 * Non-negative integer expected.
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; FixedPointList(f, x, -1)      
	 * FixedPointList(f,x,-1)   
	 * 
	 * &gt;&gt; Last(FixedPointList(Cos, 1.0, Infinity))   
	 * 0.7390851332151607
	 * </pre>
	 */
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
	 * <pre>
	 * For(start, test, incr, body)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * evaluates <code>start</code>, and then iteratively <code>body</code> and <code>incr</code> as long as test
	 * evaluates to <code>True</code>.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * For(start, test, incr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * evaluates only <code>incr</code> and no <code>body</code>.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * For(start, test)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * runs the loop without any body.<br />
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * <p>
	 * Compute the factorial of 10 using 'For':
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; n := 1
	 * &gt;&gt; For(i=1, i&lt;=10, i=i+1, n = n * i)
	 * &gt;&gt; n
	 * 3628800
	 * 
	 * &gt;&gt; n == 10!
	 * True
	 * 
	 * &gt;&gt; n := 1
	 * &gt;&gt; For(i=1, i&lt;=10, i=i+1, If(i &gt; 5, Return(i)); n = n * i)
	 * 6
	 * 
	 * &gt;&gt; n
	 * 120
	 * </pre>
	 */
	private final static class For extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.size() >= 4 && ast.size() <= 5) {
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
			Validate.checkRange(ast, 4, 5);
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	/**
	 * <pre>
	 * If(cond, pos, neg)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns <code>pos</code> if <code>cond</code> evaluates to <code>True</code>, and <code>neg</code> if it
	 * evaluates to <code>False</code>.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * If(cond, pos, neg, other)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns <code>other</code> if <code>cond</code> evaluates to neither <code>True</code> nor <code>False</code>.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * If(cond, pos)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns <code>Null</code> if <code>cond</code> evaluates to <code>False</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; If(1&lt;2, a, b)
	 * a
	 * </pre>
	 * <p>
	 * If the second branch is not specified, <code>Null</code> is taken:
	 * </p>
	 * <blockquote><blockquote>
	 * <p>
	 * If(1&lt;2, a) a
	 * </p>
	 * <p>
	 * If(False, a) //FullForm Null
	 * </p>
	 * </blockquote> </blockquote>
	 * <p>
	 * You might use comments (inside <code>(*</code> and <code>*)</code>) to make the branches of <code>If</code> more
	 * readable:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; If(a, (*then*) b, (*else*) c);
	 * </pre>
	 */
	private final static class If extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.size() >= 3 && ast.size() <= 5) {
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

				return F.NIL;
			}
			Validate.checkRange(ast, 3, 5);
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	private final static class ListFunction extends AbstractFunctionEvaluator implements ISetEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			return F.NIL;
		}

		public IExpr evaluateSet(final IExpr leftHandSide, IExpr rightHandSide, EvalEngine engine) {
			if (leftHandSide.isList()) {
				// thread over lists
				try {
					rightHandSide = engine.evaluate(rightHandSide);
				} catch (final ReturnException e) {
					rightHandSide = e.getValue();
				}
				IExpr temp = engine.threadASTListArgs((IASTMutable) F.Set(leftHandSide, rightHandSide));
				if (temp.isPresent()) {
					return engine.evaluate(temp);
				}
			}
			return F.NIL;

		}
	}

	/**
	 * <pre>
	 * Module({list_of_local_variables}, expr )
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * evaluates <code>expr</code> for the <code>list_of_local_variables</code> by renaming local variables.
	 * </p>
	 * </blockquote>
	 */
	private final static class Module extends AbstractCoreFunctionEvaluator {
		/**
		 *
		 */
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST2()) {
				if (ast.arg1().isList()) {
					IExpr temp = moduleSubstVariables((IAST) ast.arg1(), ast.arg2(), engine);
					if (temp.isPresent()) {
						return engine.evaluate(temp);
					}
				}
				return F.NIL;
			}
			Validate.checkSize(ast, 3);
			return F.NIL;

		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	/**
	 * <pre>
	 * Nest(f, expr, n)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * starting with <code>expr</code>, iteratively applies <code>f</code> <code>n</code> times and returns the final
	 * result.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Nest(f, x, 3)
	 * f(f(f(x)))
	 * 
	 * &gt;&gt; Nest((1+#) ^ 2 &amp;, x, 2)
	 * (1+(1+x)^2)^2
	 * </pre>
	 */
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

	/**
	 * <pre>
	 * NestList(f, expr, n)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * starting with <code>expr</code>, iteratively applies <code>f</code> <code>n</code> times and returns a list of
	 * all intermediate results.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; NestList(f, x, 3)
	 * {x,f(x),f(f(x)),f(f(f(x)))}
	 * 
	 * &gt;&gt; NestList(2 # &amp;, 1, 8)
	 * {1,2,4,8,16,32,64,128,256}
	 * </pre>
	 */
	private final static class NestList extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 4);

			return evaluateNestList(ast, F.ListAlloc(), engine);
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

	/**
	 * <pre>
	 * NestWhile(f, expr, test)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * applies a function <code>f</code> repeatedly on an expression <code>expr</code>, until applying <code>test</code>
	 * on the result no longer yields <code>True</code>.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * NestWhile(f, expr, test, m)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * supplies the last <code>m</code> results to <code>test</code> (default value: <code>1</code>).
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * NestWhile(f, expr, test, All)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * supplies all results gained so far to <code>test</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * <p>
	 * Divide by 2 until the result is no longer an integer:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; NestWhile(#/2&amp;, 10000, IntegerQ)
	 * 625/2
	 * </pre>
	 */
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
			return nestList(ast.arg2(), engine.evaluate(ast.arg3()), x -> F.unaryAST1(arg1, x), F.ListAlloc(), engine);
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

	/**
	 * <pre>
	 * Part(expr, i)
	 * </pre>
	 * <p>
	 * or
	 * </p>
	 * 
	 * <pre>
	 * expr[[i]]
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns part <code>i</code> of <code>expr</code>.
	 * </p>
	 * </blockquote>
	 * <p>
	 * Extract an element from a list:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; A = {a, b, c, d}
	 * &gt;&gt; A[[3]]
	 * c
	 * </pre>
	 * <p>
	 * Negative indices count from the end:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; {a, b, c}[[-2]]
	 * b
	 * </pre>
	 * <p>
	 * <code>Part</code> can be applied on any expression, not necessarily lists.
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; (a + b + c)[[2]]
	 * b
	 * </pre>
	 * <p>
	 * <code>expr[[0]]</code> gives the head of <code>expr</code>:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; (a + b + c)[[0]]
	 * Plus
	 * </pre>
	 * <p>
	 * Parts of nested lists:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; M = {{a, b}, {c, d}}
	 * &gt;&gt; M[[1, 2]]
	 * b
	 * </pre>
	 * <p>
	 * You can use <code>Span</code> to specify a range of parts:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; {1, 2, 3, 4}[[2;;4]]
	 * {2,3,4}
	 * 
	 * &gt;&gt; {1, 2, 3, 4}[[2;;-1]]
	 * {2,3,4}
	 * </pre>
	 * <p>
	 * A list of parts extracts elements at certain indices:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; {a, b, c, d}[[{1, 3, 3}]]
	 * {a,c,c}
	 * </pre>
	 * <p>
	 * Get a certain column of a matrix:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; B = {{a, b, c}, {d, e, f}, {g, h, i}}
	 * &gt;&gt; B[[;;, 2]]
	 * {b, e, h}
	 * </pre>
	 * <p>
	 * Extract a submatrix of 1st and 3rd row and the two last columns:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; B = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}
	 * &gt;&gt; B[[{1, 3}, -2;;-1]]
	 * {{2,3},{8,9}}
	 * </pre>
	 * <p>
	 * Further examples:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; (a+b+c+d)[[-1;;-2]]
	 * 0
	 * </pre>
	 * <p>
	 * Part specification is longer than depth of object.
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; x[[2]] 
	 * x[[2]]
	 * </pre>
	 * <p>
	 * Assignments to parts are possible:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; B[[;;, 2]] = {10, 11, 12}
	 * {10, 11, 12}
	 * 
	 * &gt;&gt; B
	 * {{1, 10, 3}, {4, 11, 6}, {7, 12, 9}}
	 * 
	 * &gt;&gt; B[[;;, 3]] = 13
	 * 13
	 * 
	 * &gt;&gt; B
	 * {{1, 10, 13}, {4, 11, 13}, {7, 12, 13}}
	 * 
	 * &gt;&gt; B[[1;;-2]] = t
	 * &gt;&gt; B
	 * {t,t,{7,12,13}}
	 * 
	 * &gt;&gt; F = Table(i*j*k, {i, 1, 3}, {j, 1, 3}, {k, 1, 3})
	 * &gt;&gt; F[[;; All, 2 ;; 3, 2]] = t
	 * &gt;&gt; F
	 * {{{1,2,3},{2,t,6},{3,t,9}},{{2,4,6},{4,t,12},{6,t,18}},{{3,6,9},{6,t,18},{9,t,27}}} 
	 * 
	 * &gt;&gt; F[[;; All, 1 ;; 2, 3 ;; 3]] = k
	 * &gt;&gt; F
	 * {{{1,2,k},{2,t,k},{3,t,9}},{{2,4,k},{4,t,k},{6,t,18}},{{3,6,k},{6,t,k},{9,t,27}}}
	 * </pre>
	 * <p>
	 * Of course, part specifications have precedence over most arithmetic operations:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; A[[1]] + B[[2]] + C[[3]] // Hold // FullForm
	 * "Hold(Plus(Plus(Part(A, 1), Part(B, 2)), Part(C, 3)))"
	 * 
	 * &gt;&gt; a = {2,3,4}; i = 1; a[[i]] = 0; a
	 * {0, 3, 4}
	 * </pre>
	 * <p>
	 * Negative step
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; {1,2,3,4,5}[[3;;1;;-1]]
	 * {3,2,1}
	 * 
	 * &gt;&gt; {1, 2, 3, 4, 5}[[;; ;; -1]]       
	 * {5, 4, 3, 2, 1}
	 * 
	 * &gt;&gt; Range(11)[[-3 ;; 2 ;; -2]]
	 * {9,7,5,3}
	 * 
	 * &gt;&gt; Range(11)[[-3 ;; -7 ;; -3]]
	 * {9,6}
	 * 
	 * &gt;&gt; Range(11)[[7 ;; -7;; -2]]
	 * {7,5}
	 * </pre>
	 * <p>
	 * Cannot take positions <code>1</code> through <code>3</code> in <code>{1, 2, 3, 4}</code>.
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; {1, 2, 3, 4}[[1;;3;;-1]]
	 * {1,2,3,4}[[1;;3;;-1]]
	 * </pre>
	 * <p>
	 * Cannot take positions <code>3</code> through <code>1</code> in <code>{1, 2, 3, 4}</code>.
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; {1, 2, 3, 4}[[3;;1]]
	 * {1,2,3,4}[[3;;1]]
	 * </pre>
	 */
	private final static class Part extends AbstractCoreFunctionEvaluator implements ISetEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.size() >= 3) {
				try {
					IExpr arg1 = engine.evalLoop(ast.arg1());
					if (arg1.isPresent()) {
						if (!arg1.isAST()) {
							IExpr result = ast.setAtCopy(1, arg1);
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
				} catch (WrappedException we) {
					if (Config.SHOW_STACKTRACE) {
						we.printStackTrace();
					}
					engine.printMessage(we.getMessage());
				} catch (WrongArgumentType wat) {
					if (Config.SHOW_STACKTRACE) {
						wat.printStackTrace();
					}
					engine.printMessage(wat.getMessage());
				}
			}
			return F.NIL;
		}

		public IExpr evaluateSet(final IExpr leftHandSide, IExpr rightHandSide, EvalEngine engine) {
			if (leftHandSide.size() > 1) {
				IAST part = (IAST) leftHandSide;
				if (part.arg1().isSymbol()) {
					ISymbol symbol = (ISymbol) part.arg1();
					IExpr temp = symbol.assignedValue();
					// RulesData rd = symbol.getRulesData();
					if (temp == null) {
						engine.printMessage(
								"Set: no value defined for symbol '" + symbol.toString() + "' in Part() expression.");
					} else {
						try {
							if (rightHandSide.isList()) {
								IExpr res = Programming.assignPart(temp, part, 2, (IAST) rightHandSide, 1, engine);
								// symbol.putDownRule(IPatternMatcher.SET, true, symbol, res, false);
								symbol.assign(res);
								return rightHandSide;
							} else {
								IExpr res = Programming.assignPart(temp, part, 2, rightHandSide, engine);
								// symbol.putDownRule(IPatternMatcher.SET, true, symbol, res, false);
								symbol.assign(res);
								return rightHandSide;
							}
						} catch (RuntimeException npe) {
							engine.printMessage("Set: wrong argument for Part[] function: " + part.toString()
									+ " selects no part expression.");
						}
					}
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
			if (ast.isAST1()) {
				boolean quietMode = engine.isQuietMode();
				try {
					engine.setQuietMode(true);
					return engine.evaluate(ast.arg1());
				} finally {
					engine.setQuietMode(quietMode);
				}
			}
			Validate.checkSize(ast, 2);
			return F.NIL;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	/**
	 * <pre>
	 * Reap(expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * gives the result of evaluating <code>expr</code>, together with all values sown during this evaluation. Values
	 * sown with different tags are given in different lists.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Reap(Sow(3); Sow(1))
	 * {1,{{3,1}}}
	 * </pre>
	 */
	private final static class Reap extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST1()) {
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
	 * Return(expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * aborts a function call and returns <code>expr</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; f(x_) := (If(x &lt; 0, Return(0)); x)
	 * &gt;&gt; f(-1)
	 * 0
	 * 
	 * &gt;&gt; Do(If(i &gt; 3, Return()); Print(i), {i, 10})
	 *  | 1
	 *  | 2
	 *  | 3
	 * </pre>
	 * <p>
	 * <code>Return</code> only exits from the innermost control flow construct.
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; g(x_) := (Do(If(x &lt; 0, Return(0)), {i, {2, 1, 0, -1}}); x)
	 * &gt;&gt; g(-1)
	 * -1
	 * 
	 * &gt;&gt; h(x_) := (If(x &lt; 0, Return()); x)
	 * &gt;&gt; h(1)
	 * 1
	 * 
	 * &gt;&gt; h(-1) // FullForm
	 * "Null"
	 * 
	 * &gt;&gt; f(x_) := Return(x)
	 * &gt;&gt; g(y_) := Module({}, z = f(y); 2)
	 * &gt;&gt; g(1)  
	 * 2
	 * </pre>
	 */
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

	/**
	 * <pre>
	 * Sow(expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * sends the value <code>expr</code> to the innermost <code>Reap</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Reap(Sow(3); Sow(1))
	 * {1,{{3,1}}}
	 * </pre>
	 */
	private final static class Sow extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST1()) {
				IASTAppendable reapList = engine.getReapList();
				IExpr expr = engine.evaluate(ast.arg1());
				if (reapList != null) {
					reapList.append(expr);
				}
				return expr;
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
	 * Switch(expr, pattern1, value1, pattern2, value2, ...)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * yields the first <code>value</code> for which <code>expr</code> matches the corresponding pattern.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Switch(2, 1, x, 2, y, 3, z)
	 * y
	 * 
	 * &gt;&gt; Switch(5, 1, x, 2, y)
	 * Switch(5, 1, x, 2, y)
	 * 
	 * &gt;&gt; Switch(5, 1, x, 2, y, _, z)
	 * z
	 * </pre>
	 * <p>
	 * Switch called with 2 arguments. Switch must be called with an odd number of arguments.
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Switch(2, 1)
	 * Switch(2, 1)
	 * </pre>
	 * <p>
	 * Switch called with 2 arguments. Switch must be called with an odd number of arguments.
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; a; Switch(b, b)
	 * Switch(b, b)
	 * </pre>
	 * <p>
	 * Switch called with 2 arguments. Switch must be called with an odd number of arguments.
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; z = Switch(b, b);
	 * &gt;&gt; z
	 * 
	 * Switch(b, b)
	 * </pre>
	 */
	private final static class Switch extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
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

		static class EvalControlledCallable implements Callable<IExpr> {
			private final EvalEngine fEngine;
			private IExpr fExpr;

			public EvalControlledCallable(EvalEngine engine) {
				fEngine = engine.copy();
			}

			@Override
			public IExpr call() throws Exception {
				EvalEngine.set(fEngine);
				try {
					return fEngine.evaluate(fExpr);
				} catch (final SyntaxError se) {
					String msg = se.getMessage();
					fEngine.printMessage(msg);
				} catch (org.matheclipse.core.eval.exception.TimeoutException e) {
					return F.$Aborted;
				} catch (final RecursionLimitExceeded re) {
					throw re;
				} catch (final RuntimeException re) {
					if (Config.SHOW_STACKTRACE) {
						re.printStackTrace();
					}
					fEngine.printMessage(re.getMessage());
				} catch (final Exception e) {
					fEngine.printMessage(e.getMessage());
				} catch (final OutOfMemoryError e) {
					fEngine.printMessage(e.getMessage());
				} catch (final StackOverflowError e) {
					fEngine.printMessage(e.getMessage());
				} finally {
					EvalEngine.remove();
				}
				return F.$Aborted;
			}

			public void cancel() {
				fEngine.setStopRequested(true);
			}

			public void setExpr(IExpr fExpr) {
				this.fExpr = fExpr;
			}
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 3, 4);

			long s = engine.getSeconds();
			if (s > 0 || Config.TIMECONSTRAINED_NO_THREAD) {
				// no new thread should be spawned
				if (ast.isAST3()) {
					return ast.arg3();
				}
				return engine.evaluate(ast.arg1());
			}

			IExpr arg2 = engine.evaluate(ast.arg2());
			long seconds = 0L;
			try {
				if (arg2.isReal()) {
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
			final ExecutorService executor = Executors.newSingleThreadExecutor();
			TimeLimiter timeLimiter = SimpleTimeLimiter.create(executor);// Executors.newSingleThreadExecutor());
			EvalControlledCallable work = new EvalControlledCallable(engine);
			work.setExpr(ast.arg1());
			try {
				seconds = seconds > 1 ? seconds - 1 : seconds;
				return timeLimiter.callWithTimeout(work, seconds, TimeUnit.SECONDS);
			} catch (org.matheclipse.core.eval.exception.TimeoutException e) {
				if (ast.isAST3()) {
					return ast.arg3();
				}
				return F.$Aborted;
			} catch (java.util.concurrent.TimeoutException e) {
				if (ast.isAST3()) {
					return ast.arg3();
				}
				return F.$Aborted;
			} catch (com.google.common.util.concurrent.UncheckedTimeoutException e) {
				if (ast.isAST3()) {
					return ast.arg3();
				}
				return F.$Aborted;
			} catch (Exception e) {
				Throwable re = e.getCause();
				if (re instanceof RecursionLimitExceeded) {
					throw (RecursionLimitExceeded) re;
				}
				if (Config.DEBUG) {
					e.printStackTrace();
				}
				return F.Null;
			} finally {
				work.cancel();
				executor.shutdown(); // Disable new tasks from being submitted
				try {
					// Wait a while for existing tasks to terminate
					if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
						executor.shutdownNow(); // Cancel currently executing tasks
						// Wait a while for tasks to respond to being cancelled
						if (!executor.awaitTermination(2, TimeUnit.SECONDS)) {
							engine.printMessage("TimeConstrained: pool did not terminate");
						}
					}
				} catch (InterruptedException ie) {
					// (Re-)Cancel if current thread also interrupted
					executor.shutdownNow();
				}

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
			if (ast.size() == 2) {
				final long begin = System.currentTimeMillis();
				final IExpr result = engine.evaluate(ast.arg1());
				return List(Divide(F.num(System.currentTimeMillis() - begin), F.integer(1000L)), F.HoldForm(result));
			}
			return F.NIL;
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

			try {
				final IExpr temp = ast.arg1();
				IPatternMatcher matcher = null;
				if (ast.isAST2()) {
					matcher = engine.evalPatternMatcher(ast.arg2());
				}

				return engine.evalTrace(temp, matcher, F.List());
			} catch (RuntimeException rex) {
				if (Config.SHOW_STACKTRACE) {
					rex.printStackTrace();
				}
			}
			return F.NIL;
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
			if (ast.size() == 2) {
				return ast.arg1();
			}
			engine.printMessage("Unevaluated: expected only one argument.");
			return F.NIL;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			if (!ToggleFeature.UNEVALUATED) {
				return;
			}
			newSymbol.setAttributes(ISymbol.HOLDALLCOMPLETE);
		}
	}

	/**
	 * <pre>
	 * Which(cond1, expr1, cond2, expr2, ...)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * yields <code>expr1</code> if <code>cond1</code> evaluates to <code>True</code>, <code>expr2</code> if
	 * <code>cond2</code> evaluates to <code>True</code>, etc.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; n=5;
	 * &gt;&gt; Which(n == 3, x, n == 5, y)
	 * y
	 * 
	 * &gt;&gt; f(x_) := Which(x &lt; 0, -x, x == 0, 0, x &gt; 0, x)
	 * &gt;&gt; f(-3)
	 * 3
	 * </pre>
	 * <p>
	 * If no test yields <code>True</code>, <code>Which</code> returns <code>Null</code>:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Which(False, a)
	 * </pre>
	 * <p>
	 * If a test does not evaluate to <code>True</code> or <code>False</code>, evaluation stops and a <code>Which</code>
	 * expression containing the remaining cases is returned:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Which(False, a, x, b, True, c)
	 * Which(x,b,True,c)
	 * </pre>
	 * <p>
	 * <code>Which</code> must be called with an even number of arguments:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Which(a, b, c)
	 * Which(a, b, c)
	 * </pre>
	 */
	private final static class Which extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (((ast.argSize()) & 0x0001) == 0x0001) {
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

	/**
	 * <pre>
	 * While(test, body)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * evaluates <code>body</code> as long as test evaluates to <code>True</code>.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * While(test)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * runs the loop without any body.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * <p>
	 * Compute the GCD of two numbers:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; {a, b} = {27, 6};
	 * &gt;&gt; While(b != 0, {a, b} = {b, Mod(a, b)});
	 * &gt;&gt; a
	 * 3
	 * 
	 * &gt;&gt; i = 1; While(True, If(i^2 &gt; 100, Return(i + 1), i++))
	 * 12
	 * </pre>
	 */
	private final static class While extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.size() >= 2 && ast.size() <= 3) {
				// use EvalEngine's iterationLimit only for evaluation control

				// While(test, body)
				IExpr test = ast.arg1();
				IExpr body = ast.isAST2() ? ast.arg2() : F.Null;
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
			Validate.checkRange(ast, 2, 3);
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	/**
	 * <pre>
	 * With({list_of_local_variables}, expr )
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * evaluates <code>expr</code> for the <code>list_of_local_variables</code> by replacing the local variables in
	 * <code>expr</code>.
	 * </p>
	 * </blockquote>
	 */
	private final static class With extends AbstractCoreFunctionEvaluator {
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.size() == 3) {
				if (ast.arg1().isList()) {
					IExpr temp = withSubstVariables((IAST) ast.arg1(), ast.arg2(), engine);
					if (temp.isPresent()) {
						return engine.evaluate(temp);
					}

				}

				return F.NIL;
			}
			Validate.checkSize(ast, 3);
			return F.NIL;
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
	 * @return
	 */
	private static boolean rememberWithVariables(IAST variablesList, final java.util.Map<ISymbol, IExpr> variablesMap,
			EvalEngine engine) {
		ISymbol oldSymbol;
		for (int i = 1; i < variablesList.size(); i++) {
			if (variablesList.get(i).isAST(F.Set, 3)) {
				final IAST setFun = (IAST) variablesList.get(i);
				if (setFun.arg1().isSymbol()) {
					oldSymbol = (ISymbol) setFun.arg1();
					IExpr rightHandSide = setFun.arg2();
					IExpr temp = engine.evaluate(rightHandSide);
					variablesMap.put(oldSymbol, temp);
				} else {
					engine.printMessage(
							"With: expression requires variable with value assignment: " + setFun.toString());
					return false;
				}
			} else {
				engine.printMessage("With: assignment to variable required: " + variablesList.get(i).toString());
				return false;
			}
		}
		return true;
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
	public static boolean rememberModuleVariables(IAST variablesList, final String varAppend,
			final java.util.Map<ISymbol, IExpr> variablesMap, final EvalEngine engine) {
		ISymbol oldSymbol;
		ISymbol newSymbol;
		for (int i = 1; i < variablesList.size(); i++) {
			if (variablesList.get(i).isSymbol()) {
				oldSymbol = (ISymbol) variablesList.get(i);
				newSymbol = F.Dummy(oldSymbol.toString() + varAppend);
				variablesMap.put(oldSymbol, newSymbol);
			} else {
				if (variablesList.get(i).isAST(F.Set, 3)) {
					final IAST setFun = (IAST) variablesList.get(i);
					if (setFun.arg1().isSymbol()) {
						oldSymbol = (ISymbol) setFun.arg1();
						newSymbol = F.Dummy(oldSymbol.toString() + varAppend);
						variablesMap.put(oldSymbol, newSymbol);
						engine.evaluate(F.Set(newSymbol, setFun.arg2()));
					} else {
						engine.printMessage("Module: expression requires symbol variable: " + setFun.toString());
						return false;
					}
				} else {
					engine.printMessage(
							"Module: expression requires symbol variable: " + variablesList.get(i).toString());
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Remember which local variable names (appended with the module counter) we use in the given
	 * <code>variablesMap</code>.
	 * 
	 * @param variablesList
	 *            initializer variables list from the <code>Module</code> function
	 * @param varAppend
	 *            the module counter string which appended to the variable names.
	 * @param variablesMap
	 *            the resulting module variables map
	 * @param engine
	 *            the evaluation engine
	 */
	public static void rememberBlockVariables(IAST variablesList, final String varAppend,
			final java.util.Map<ISymbol, ISymbol> variablesMap, final EvalEngine engine) {
		ISymbol oldSymbol;
		ISymbol newSymbol;
		for (int i = 1; i < variablesList.size(); i++) {
			if (variablesList.get(i).isSymbol()) {
				oldSymbol = (ISymbol) variablesList.get(i);
				newSymbol = F.Dummy(oldSymbol.toString() + varAppend);
				variablesMap.put(oldSymbol, newSymbol);
			} else {
				if (variablesList.get(i).isAST(F.Set, 3)) {
					final IAST setFun = (IAST) variablesList.get(i);
					if (setFun.arg1().isSymbol()) {
						oldSymbol = (ISymbol) setFun.arg1();
						newSymbol = F.Dummy(oldSymbol.toString() + varAppend);
						variablesMap.put(oldSymbol, newSymbol);
					}
				}
			}
		}
		for (int i = 1; i < variablesList.size(); i++) {
			if (variablesList.get(i).isAST(F.Set, 3)) {
				final IAST setFun = (IAST) variablesList.get(i);
				if (setFun.arg1().isSymbol()) {
					oldSymbol = (ISymbol) setFun.arg1();
					newSymbol = (ISymbol) variablesMap.get(oldSymbol);
					IExpr temp = F.subst(engine.evaluate(setFun.arg2()), variablesMap);
					engine.evaluate(F.Set(newSymbol, temp));
				}
			}
		}
	}

	/**
	 * Substitute the variable names from the list with temporary dummy variable names in the &quot;module-block&quot;..
	 * 
	 * @param intializerList
	 *            list of variables which should be substituted by appending <code>$<number></code> to the variable
	 *            names
	 * @param moduleBlock
	 *            the module block where the variables should be replaced with temporary variables
	 * @param engine
	 * @return
	 */
	private static IExpr moduleSubstVariables(IAST intializerList, IExpr moduleBlock, final EvalEngine engine) {
		final int moduleCounter = engine.incModuleCounter();
		final String varAppend = "$" + moduleCounter;
		final java.util.IdentityHashMap<ISymbol, IExpr> moduleVariables = new IdentityHashMap<ISymbol, IExpr>();
		if (rememberModuleVariables(intializerList, varAppend, moduleVariables, engine)) {
			IExpr result = moduleBlock.accept(new ModuleReplaceAll(moduleVariables, engine, varAppend));
			return result.orElse(moduleBlock);
		}
		return F.NIL;
	}

	/**
	 * Substitute the variable names from the list with temporary dummy variable names in the &quot;with-block&quot;..
	 * 
	 * @param intializerList
	 *            list of variables which should be substituted by appending <code>$<number></code> to the variable
	 *            names
	 * @param withBlock
	 *            the with block where the variables should be replaced with temporary variables
	 * @param engine
	 * @return
	 */
	private static IExpr withSubstVariables(IAST intializerList, IExpr withBlock, final EvalEngine engine) {
		final int moduleCounter = engine.incModuleCounter();
		final String varAppend = "$" + moduleCounter;
		final java.util.IdentityHashMap<ISymbol, IExpr> moduleVariables = new IdentityHashMap<ISymbol, IExpr>();
		if (rememberWithVariables(intializerList, moduleVariables, engine)) {
			IExpr result = withBlock.accept(new ModuleReplaceAll(moduleVariables, engine, varAppend));
			return result.orElse(withBlock);
		}
		return F.NIL;
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
		final IAST arg1 = (IAST) expr;
		final IExpr arg2 = engine.evaluate(ast.get(pos));
		int p1 = pos + 1;
		int[] span = arg2.isSpan(arg1.size());
		if (span != null) {
			int start = span[0];
			int last = span[1];
			int step = span[2];
			return spanPart(ast, pos, arg1, arg2, start, last, step, p1, engine);
		} else if (arg2.equals(F.All)) {
			return spanPart(ast, pos, arg1, arg2, 1, arg1.size() - 1, 1, p1, engine);
		} else if (arg2.isReal()) {
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

	private static IExpr spanPart(final IAST ast, int pos, IAST arg1, final IExpr arg2, int start, int last, int step,
			int p1, EvalEngine engine) {
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
		} else if (arg2.isReal()) {
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
		} else if (arg2.isReal()) {
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
		return lhs.setAtCopy(partPosition, value);
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
