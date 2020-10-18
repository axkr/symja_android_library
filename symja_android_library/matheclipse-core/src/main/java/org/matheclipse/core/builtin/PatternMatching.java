package org.matheclipse.core.builtin;

import static org.matheclipse.core.expression.F.Rule;
import static org.matheclipse.core.expression.F.RuleDelayed;

import java.io.IOException;
import java.io.PrintStream;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ConditionException;
import org.matheclipse.core.eval.exception.FailedException;
import org.matheclipse.core.eval.exception.ReturnException;
import org.matheclipse.core.eval.exception.RuleCreationError;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.ValidateException;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.ISetEvaluator;
import org.matheclipse.core.eval.util.Lambda;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.PatternNested;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.form.Documentation;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.IPatternObject;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.core.patternmatching.PatternMatcherEquals;
import org.matheclipse.core.patternmatching.RulesData;
import org.matheclipse.parser.client.FEConfig;

public final class PatternMatching {

	/**
	 * 
	 * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation in static
	 * initializer</a>
	 */
	private static class Initializer {

		private static void init() {
			S.FilterRules.setEvaluator(new FilterRules());
			S.Hold.setEvaluator(new Hold());
			S.HoldPattern.setEvaluator(new HoldPattern());
			S.Identity.setEvaluator(new Identity());
			S.Information.setEvaluator(new Information());
			S.Literal.setEvaluator(new Literal());
			S.MessageName.setEvaluator(new MessageName());
			S.Optional.setEvaluator(Optional.CONST);
			S.Options.setEvaluator(new Options());
			S.OptionValue.setEvaluator(new OptionValue());
			S.Rule.setEvaluator(new Rule());
			S.RuleDelayed.setEvaluator(new RuleDelayed());
			// if (!Config.FUZZY_PARSER) {
			S.Set.setEvaluator(new Set());
			S.SetDelayed.setEvaluator(new SetDelayed());
			// }
			S.Unique.setEvaluator(new Unique());
			if (!Config.FUZZY_PARSER) {
				S.Blank.setEvaluator(Blank.CONST);
				S.BlankSequence.setEvaluator(BlankSequence.CONST);
				S.BlankNullSequence.setEvaluator(BlankNullSequence.CONST);
				S.DownValues.setEvaluator(new DownValues());
				S.Pattern.setEvaluator(Pattern.CONST);
				S.Clear.setEvaluator(new Clear());
				S.ClearAll.setEvaluator(new ClearAll());
				S.Context.setEvaluator(new Context());
				S.Default.setEvaluator(new Default());
				S.Definition.setEvaluator(new Definition());
				S.Evaluate.setEvaluator(new Evaluate());
				S.OptionsPattern.setEvaluator(OptionsPattern.CONST);
				S.OwnValues.setEvaluator(new OwnValues());
				S.Repeated.setEvaluator(Repeated.CONST);
				S.RepeatedNull.setEvaluator(RepeatedNull.CONST);
				S.TagSet.setEvaluator(new TagSet());
				S.TagSetDelayed.setEvaluator(new TagSetDelayed());
				S.Unset.setEvaluator(new Unset());
				S.UpSet.setEvaluator(new UpSet());
				S.UpSetDelayed.setEvaluator(new UpSetDelayed());
				S.UpValues.setEvaluator(new UpValues());
			}
		}
	}

	public final static class Blank extends AbstractCoreFunctionEvaluator {
		public final static Blank CONST = new Blank();

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.head().equals(F.Blank)) {
				if (ast.isAST0()) {
					return F.$b();
				}
				if (ast.isAST1()) {
					return F.$b(ast.arg1());
				}
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize(IAST ast) {
			return IOFunctions.ARGS_0_1;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	public final static class BlankSequence extends AbstractCoreFunctionEvaluator {
		public final static BlankSequence CONST = new BlankSequence();

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.head().equals(F.BlankSequence)) {
				if (ast.isAST0()) {
					return F.$ps((ISymbol) null);
				}
				if (ast.isAST1() && ast.arg1().isSymbol()) {
					return F.$ps((ISymbol) ast.arg1());
				}
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize(IAST ast) {
			return IOFunctions.ARGS_0_1;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	public final static class BlankNullSequence extends AbstractCoreFunctionEvaluator {
		public final static BlankNullSequence CONST = new BlankNullSequence();

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.head().equals(F.BlankNullSequence)) {
				if (ast.isAST0()) {
					return F.$ps((ISymbol) null, true);
				}
				if (ast.isAST1() && ast.arg1().isSymbol()) {
					return F.$ps((ISymbol) ast.arg1(), true);
				}
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize(IAST ast) {
			return IOFunctions.ARGS_0_1;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	/**
	 * <pre>
	 * Clear(symbol1, symbol2,...)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * clears all values of the given symbols.
	 * </p>
	 * </blockquote>
	 * <p>
	 * <code>Clear</code> does not remove attributes, options, and default values associated with the symbols. Use
	 * <code>ClearAll</code> to do so.
	 * </p>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; a=2
	 * 2
	 * 
	 * &gt;&gt; Definition(a)
	 * {a=2}
	 * 
	 * &gt;&gt; Clear(a)
	 * &gt;&gt; a
	 * a
	 * </pre>
	 */
	private final static class Clear extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			for (int i = 1; i < ast.size(); i++) {
				IExpr expr = ast.get(i);
				if (!expr.isSymbol() || ((ISymbol) expr).isProtected()) {
					// Symbol `1` is Protected.
					IOFunctions.printMessage(ast.topHead(), "wrsym", F.List(expr), engine);
					return F.Null;
				}
			}
			Lambda.forEach(ast, x -> x.isSymbol(), x -> ((ISymbol) x).clear(engine));
			return F.Null;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	/**
	 * <pre>
	 * ClearAll(symbol1, symbol2,...)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * clears all values and attributes associated with the given symbols.
	 * </p>
	 * </blockquote>
	 */
	private final static class ClearAll extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			for (int i = 1; i < ast.size(); i++) {
				IExpr expr = ast.get(i);
				if (!expr.isSymbol() || ((ISymbol) expr).isProtected()) {
					// Symbol `1` is Protected.
					IOFunctions.printMessage(ast.topHead(), "wrsym", F.List(expr), engine);
					return F.Null;
				}
			}

			Lambda.forEach(ast, x -> x.isSymbol(), x -> ((ISymbol) x).clearAll(engine));
			return F.Null;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	/**
	 * <pre>
	 * <code>Context(symbol)
	 * </code>
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * return the context of the given symbol.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * <code>Context()
	 * </code>
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * return the current context.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * <code>&gt;&gt; $ContextPath
	 * {System`,Global`}
	 * 
	 * &gt;&gt; Context(a)
	 * Global`
	 * 
	 * &gt;&gt; Context(Sin)
	 * System`
	 * </code>
	 * </pre>
	 */
	private final static class Context extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST1() && ast.first().isSymbol()) {
				return F.stringx(((ISymbol) ast.first()).getContext().getContextName());
			}
			if (ast.isAST0()) {
				return F.stringx(EvalEngine.get().getContext().completeContextName());
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize(IAST ast) {
			return IOFunctions.ARGS_0_1;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	/**
	 * Get the default value for a symbol (i.e. <code>1</code> is the default value for <code>Times</code>,
	 * <code>0</code> is the default value for <code>Plus</code>).
	 */
	/**
	 * <pre>
	 * <code>Default(symbol)
	 * </code>
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * <code>Default</code> returns the default value associated with the <code>symbol</code> for a pattern default
	 * <code>_.</code> expression.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * <code class="language-ition)"></code>
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * <code>Default</code> returns the default value associated with the <code>symbol</code> for a pattern default
	 * <code>_.</code> expression at position <code>pos</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * <code>&gt;&gt; Default(test) = 1 
	 * 1 
	 * 
	 * &gt;&gt; test(x_., y_.) = {x, y} 
	 * {x,y} 
	 * 				
	 * &gt;&gt; test(a) 
	 * {a,1} 
	 * 				
	 * &gt;&gt; test( ) 
	 * {1,1}
	 * </code>
	 * </pre>
	 */
	private final static class Default extends AbstractFunctionEvaluator implements ISetEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			IExpr arg1 = Validate.checkSymbolType(ast, 1, engine);
			if (arg1.isPresent()) {
				ISymbol symbol = (ISymbol) arg1;
				if (ast.size() > 2) {
					int pos = ast.arg2().toIntDefault();
					if (pos > 0) {
						return symbol.getDefaultValue(pos);
					}
				} else {
					return symbol.getDefaultValue();
				}
			}
			return F.NIL;
		}

		@Override
		public IExpr evaluateSet(final IExpr leftHandSide, IExpr rightHandSide, IBuiltInSymbol builtinSymbol,
				EvalEngine engine) {
			if (leftHandSide.isAST(S.Default) && leftHandSide.size() > 1) {
				if (!leftHandSide.first().isSymbol()) {
					IOFunctions.printMessage(builtinSymbol, "setps", F.List(leftHandSide.first()), engine);
					return rightHandSide;
				}
				ISymbol symbol = (ISymbol) leftHandSide.first();
				if (symbol.isProtected()) {
					IOFunctions.printMessage(F.Default, "write", F.List(symbol, leftHandSide), EvalEngine.get());
					throw new FailedException();
				}
				if (leftHandSide.size() == 2 && leftHandSide.first().isSymbol()) {
					symbol.setDefaultValue(rightHandSide);
					return rightHandSide;
				} else if (leftHandSide.size() == 3 && leftHandSide.first().isSymbol()) {
					int pos = leftHandSide.second().toIntDefault();
					if (pos > 1) {
						symbol.setDefaultValue(rightHandSide);
						return rightHandSide;
					}
				}
			}
			return F.NIL;

		}

		@Override
		public int[] expectedArgSize(IAST ast) {
			return IOFunctions.ARGS_1_2;
		}
	}

	/**
	 * <pre>
	 * Definition(symbol)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * prints user-defined values and rules associated with <code>symbol</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Definition(ArcSinh)
	 * {ArcSinh(0)=0,
	 *  ArcSinh(I*1/2)=I*1/6*Pi,
	 *  ArcSinh(I)=I*1/2*Pi,
	 *  ArcSinh(1)=Log(1+Sqrt(2)),
	 *  ArcSinh(I*1/2*Sqrt(2))=I*1/4*Pi,
	 *  ArcSinh(I*1/2*Sqrt(3))=I*1/3*Pi,
	 *  ArcSinh(Infinity)=Infinity,
	 *  ArcSinh(I*Infinity)=Infinity,
	 *  ArcSinh(ComplexInfinity)=ComplexInfinity}
	 * 
	 * &gt;&gt; a=2
	 * 2
	 * 
	 * &gt;&gt; Definition(a)
	 * {a=2}
	 * </pre>
	 */
	private final static class Definition extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			IExpr arg1 = Validate.checkSymbolType(ast, 1, engine);
			if (arg1.isPresent()) {
				ISymbol symbol = (ISymbol) arg1;
				PrintStream stream;
				stream = engine.getOutPrintStream();
				if (stream == null) {
					stream = System.out;
				}
				try {
					return F.stringx(symbol.definitionToString());
				} catch (IOException e) {
					stream.println(e.getMessage());
					if (Config.DEBUG) {
						e.printStackTrace();
					}
				}
			}
			return F.Null;
		}

		@Override
		public int[] expectedArgSize(IAST ast) {
			return IOFunctions.ARGS_1_1;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	private final static class DownValues extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			IExpr arg1 = Validate.checkSymbolType(ast, 1, engine);
			if (arg1.isPresent()) {
				ISymbol symbol = (ISymbol) arg1;
				RulesData rulesData = symbol.getRulesData();
				if (rulesData == null) {
					return F.CEmptyList;
				}
				return rulesData.downValues();
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize(IAST ast) {
			return IOFunctions.ARGS_1_1;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	/**
	 * <pre>
	 * <code>Evaluate(expr)
	 * </code>
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * the <code>Evaluate</code> function will be executed even if the function attributes
	 * <code>HoldFirst, HoldRest, HoldAll</code> are set for the function head.
	 * </p>
	 * </blockquote>
	 */
	private final static class Evaluate extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.size() == 2) {
				return engine.evaluate(ast.arg1());
			}
			IASTMutable sequence = ast.copy();
			sequence.set(0, F.Identity);
			return engine.evaluate(sequence);
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

	}

	private final static class FilterRules extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.arg1().isListOfRules()) {
				IAST listOfRules = (IAST) ast.arg1();
				if (ast.arg2().isList()) {
					IAST list2 = (IAST) ast.arg2();
					IASTAppendable result = F.ListAlloc(listOfRules.size());
					for (int i = 1; i < listOfRules.size(); i++) {
						IExpr listOfRulesArg = listOfRules.get(i);
						if (listOfRulesArg.isRuleAST()) {
							IAST rule = (IAST) listOfRulesArg;
							for (int j = 1; j < list2.size(); j++) {
								IExpr list2Arg = list2.get(j);
								if (list2Arg.isRuleAST()) {
									if (rule.first().equals(list2Arg.first())) {
										result.append(rule);
										break;
									}
								} else {
									if (rule.first().equals(list2Arg)) {
										result.append(rule);
										break;
									}
								}
							}
						}
					}
					return result;
				}
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize(IAST ast) {
			return IOFunctions.ARGS_2_2;
		}

	}

	/**
	 * <pre>
	 * Hold(expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * <code>Hold</code> doesn't evaluate <code>expr</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; Hold(3*2)
	 * Hold(3*2)
	 * </pre>
	 */
	private final static class Hold extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

		@Override
		public int[] expectedArgSize(IAST ast) {
			return IOFunctions.ARGS_1_INFINITY;
		}
	}

	/**
	 * <pre>
	 * HoldPattern(expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * <code>HoldPattern</code> doesn't evaluate <code>expr</code> for pattern-matching.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * <p>
	 * One might be very surprised that the following line evaluates to <code>True</code>!
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; MatchQ(And(x, y, z), Times(p__))
	 * True
	 * </pre>
	 * <p>
	 * When the line above is evaluated <code>Times(p__)</code> evaluates to <code>(p__)</code> before the kernel checks
	 * to see if the pattern matches. <code>MatchQ</code> then determines if <code>And(x,y,z)</code> matches the pattern
	 * <code>(p__)</code> and it does because <code>And(x,y,z)</code> is itself a sequence of one.
	 * </p>
	 * <p>
	 * Now the next line also evaluates to <code>True</code> because both <code>( And(p__) )</code> and
	 * <code>( Times(p__) )</code> evaluate to <code>( p__ )</code>.
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Times(p__)===And(p__)
	 * True
	 * </pre>
	 * <p>
	 * In the examples above prevent the patterns from evaluating, by wrapping them with <code>HoldPattern</code> as in
	 * the following lines.
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; MatchQ(And(x, y, z), HoldPattern(Times(p__))) 
	 * False
	 * 
	 * &gt;&gt; HoldPattern(Times(p__))===HoldPattern(And(p__)) 
	 * False
	 * </pre>
	 * <p>
	 * In the next lines <code>HoldPattern</code> is used to ensure the head <code>(And)</code> is changed to
	 * <code>(List)</code>.<br />
	 * The two examples that follow have the same effect, but the use of <code>HoldPattern</code> isn't needed.
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; And(x, y, z)/.HoldPattern(And(a__)) -&gt;List(a)
	 * {x,y,z}
	 * 
	 * &gt;&gt; And(x, y, z)/.And-&gt;List 
	 * {x,y,z}
	 * 
	 * &gt;&gt; And(x, y, z)/.And(a_,b___)-&gt;List(a,b) 
	 * {x,y,z}
	 * </pre>
	 */
	private static class HoldPattern extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.size() == 2) {
				IExpr arg1 = ast.arg1();
				if (arg1.isAST()) {
					IExpr temp = engine.evalHoldPattern((IAST) arg1);
					if (temp == arg1) {
						return F.NIL;
					}
					return F.HoldPattern(temp);
				}
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize(IAST ast) {
			return IOFunctions.ARGS_1_1;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	/**
	 * 
	 * @deprecated use {@link HoldPattern}
	 */
	@Deprecated
	private final static class Literal extends HoldPattern {
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.size() == 2) {
				IExpr arg1 = ast.arg1();
				if (arg1.isAST()) {
					IExpr temp = engine.evalHoldPattern((IAST) arg1);
					if (temp == arg1) {
						return F.NIL;
					}
					return F.Literal(temp);
				}
			}
			return F.NIL;
		}
	}

	/**
	 * <pre>
	 * <code>Identity(expr)
	 * </code>
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * returns <code>expr</code>.
	 * </p>
	 * </blockquote>
	 * <p>
	 * See
	 * </p>
	 * <ul>
	 * <li><a href="https://en.wikipedia.org/wiki/Identity_function">Wikipedia - Identity function</a></li>
	 * </ul>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * <code>&gt;&gt; Identity(5)
	 * 5
	 * </code>
	 * </pre>
	 */
	private final static class Identity extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			return ast.arg1();
		}

		@Override
		public int[] expectedArgSize(IAST ast) {
			return IOFunctions.ARGS_1_1;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	private final static class Information extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.size() == 2 || ast.size() == 3) {
				try {
					boolean longForm = true;
					if (ast.size() == 3) {
						final OptionArgs options = new OptionArgs(ast.topHead(), ast, 2, engine);
						if (options.isFalse(F.LongForm)) {
							longForm = false;
						}
					}

					ISymbol symbol = null;
					if (!ast.arg1().isSymbol()) {
						IExpr arg1 = engine.evaluate(ast.arg1());
						if (arg1.isEmptyList()) {
							return arg1;
						}
						if (!arg1.isSymbol()) {
							return engine.printMessage(
									ast.topHead() + ": symbol expected at position 1 instead of " + arg1.toString());
						}
						symbol = (ISymbol) arg1;
					} else {
						symbol = (ISymbol) ast.arg1();
					}
					final PrintStream s = engine.getOutPrintStream();
					final PrintStream stream;
					if (s == null) {
						stream = System.out;
					} else {
						stream = s;
					}

					// Set[MessageName(f,"usage"),"text")
					IExpr temp = symbol.evalMessage("usage");
					if (temp.isPresent()) {
						stream.println(temp.toString());
					}
					if (longForm) {
						try {
							Documentation.printDocumentation(stream, symbol.getSymbolName());

							IAST function = F.Attributes(symbol);
							temp = engine.evaluate(F.Attributes(symbol));
							if (temp.isPresent()) {
								stream.println(function.toString() + " = " + temp.toString());
							}

							stream.println(symbol.definitionToString());
						} catch (IOException ioe) {
							if (FEConfig.SHOW_STACKTRACE) {
								ioe.printStackTrace();
							}
						}
					}
					return F.Null;
				} catch (RuntimeException rex) {
					//
					if (FEConfig.SHOW_STACKTRACE) {
						rex.printStackTrace();
					}
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
	 * MessageName[{&lt;file name&gt;}}
	 * 
	 */
	private final static class MessageName extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			// Here we only validate the arguments
			// The assignment of the message is handled in the Set() function
			if (!ast.arg1().isSymbol()) {
				return engine.printMessage(
						ast.topHead() + ": symbol expected at position 1 instead of " + ast.arg1().toString());
			}
			IExpr arg2 = engine.evaluate(ast.arg2());
			if (arg2 instanceof IStringX || arg2.isSymbol()) {
				return F.NIL;
			}

			return F.Null;
		}

		@Override
		public int[] expectedArgSize(IAST ast) {
			return IOFunctions.ARGS_2_2;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	/**
	 * <pre>
	 * Optional(patt, default)
	 * </pre>
	 * <p>
	 * or
	 * </p>
	 * 
	 * <pre>
	 * patt : default
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * is a pattern which matches <code>patt</code>, which if omitted should be replaced by <code>default</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; f(x_, y_:1) := {x, y}
	 * &gt;&gt; f(1, 2)
	 * {1,2}
	 * 
	 * &gt;&gt; f(a)
	 * {a,1}
	 * </pre>
	 */
	public static class Optional extends AbstractCoreFunctionEvaluator {
		public final static Optional CONST = new Optional();

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.head().equals(F.Optional)) {
				// convert only special forms of _. or x_.
				if (ast.size() == 2) {
					IExpr arg1 = engine.evaluate(ast.arg1());
					if (arg1.isBlank()) {
						IPattern patt = (IPattern) arg1;
						if (patt.getHeadTest() == null) {
							return F.$b(patt.getHeadTest(), true);
						}
					}
					if (arg1.isPattern()) {
						IPattern patt = (IPattern) arg1;
						if (patt.getHeadTest() == null) {
							return F.$p(patt.getSymbol(), patt.getHeadTest(), true);
						}
					}
				}
				// if (ast.size() == 3) {
				// // convert only special forms of _:v or x_:v.
				// IExpr arg1 = engine.evaluate(ast.arg1());
				// IExpr arg2 = engine.evaluate(ast.arg2());
				// if (arg1.isBlank()) {
				// IPattern patt = (IPattern) arg1;
				// if (patt.getCondition() == null) {
				// return F.$b(patt.getCondition(), arg2);
				// }
				// }
				// if (arg1.isPattern()) {
				// IPattern patt = (IPattern) arg1;
				// if (patt.getCondition() == null) {
				// return F.$p(patt.getSymbol(), patt.getCondition(), arg2);
				// }
				// }
				// }
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize(IAST ast) {
			return IOFunctions.ARGS_1_2;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	private final static class Options extends AbstractFunctionEvaluator implements ISetEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST1() && ast.arg1().isSymbol()) {
				ISymbol arg1 = (ISymbol) ast.arg1();
				return optionsList(arg1, false);
			}
			return F.NIL;
		}

		public IExpr evaluateSet(final IExpr leftHandSide, IExpr rightHandSide, IBuiltInSymbol builtinSymbol,
				EvalEngine engine) {
			if (leftHandSide.isAST(S.Options, 2) && leftHandSide.first().isSymbol()) {
				ISymbol symbol = (ISymbol) leftHandSide.first();
				if (!symbol.isProtected()) {
					try {
						if (!builtinSymbol.equals(S.SetDelayed)) {
							rightHandSide = engine.evaluate(rightHandSide);
						}
					} catch (final ReturnException e) {
						rightHandSide = e.getValue();
					}
					symbol.putDownRule(IPatternMatcher.SET, true, leftHandSide, rightHandSide, engine.isPackageMode());
					if (builtinSymbol.equals(S.Set)) {
						return rightHandSide;
					}
					return F.Null;
				}
			}
			return F.NIL;

		}

		@Override
		public int[] expectedArgSize(IAST ast) {
			return IOFunctions.ARGS_1_2;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.addAttributes(ISymbol.HOLDALL);
		}

	}

	public static class OptionValue extends AbstractCoreFunctionEvaluator {
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.head().equals(F.OptionValue)) {
				IASTAppendable optionsPattern = null;
				IExpr arg1 = engine.evaluate(ast.arg1());
				IExpr rhsRuleValue = F.NIL;
				IAST optionsList = null;
				if (ast.size() > 2 && arg1.isSymbol()) {
					optionsList = optionsList((ISymbol) arg1, true);
				}
				IExpr optionValue;

				if (ast.isAST3()) {
					IExpr arg2 = ast.arg2();
					IExpr arg3 = ast.arg3();
					if (arg3.isList()) {
						return ((IAST) arg3).mapThread(ast, 3);
					}
					optionsPattern = F.ListAlloc(10);
					extractRules(arg2, optionsPattern);
					extractRules(optionsList, optionsPattern);
					optionValue = arg3;
					if (arg3.isSymbol()) {
						optionValue = F.$str(((ISymbol) arg3).getSymbolName());
					}
					if (optionsPattern != null) {
						rhsRuleValue = rhsRuleValue(optionValue, optionsPattern);
						if (rhsRuleValue.isPresent()) {
							return rhsRuleValue;
						}
						IOFunctions.printMessage(ast.topHead(), "optnf", F.List(optionsPattern, optionValue), engine);
						return optionValue;
					}
					return F.NIL;
				} else if (ast.isAST2()) {
					IExpr arg2 = ast.arg2();
					if (arg2.isList()) {
						return ((IAST) arg2).mapThread(ast, 2);
					}
					optionValue = arg2;
					if (arg2.isSymbol()) {
						optionValue = F.$str(((ISymbol) arg2).getSymbolName());
					}
					if (arg1.isSymbol()) {
						Iterator<IdentityHashMap<ISymbol, IASTAppendable>> iter = engine.optionsStackIterator();
						while (iter.hasNext()) {
							IdentityHashMap<ISymbol, IASTAppendable> map = iter.next();
							if (map != null) {
								optionsPattern = map.get(arg1);
								if (optionsPattern != null) {
									rhsRuleValue = rhsRuleValue(optionValue, optionsPattern);
									if (rhsRuleValue.isPresent()) {
										return rhsRuleValue;
									}
								}
							}
						}
					} else {
						if (arg1.isAST()) {
							optionsList = (IAST) arg1;
						}
					}
					if (optionsPattern == null) {
						optionsPattern = F.ListAlloc(10);
					}
					extractRules(optionsList, optionsPattern);
					if (optionsPattern != null) {
						rhsRuleValue = rhsRuleValue(optionValue, optionsPattern);
						if (rhsRuleValue.isPresent()) {
							return rhsRuleValue;
						}
						IOFunctions.printMessage(ast.topHead(), "optnf", F.List(optionsPattern, optionValue), engine);
						return optionValue;
					}
					return F.NIL;
				} else {// ast.isAST1()
					optionValue = arg1;
					if (arg1.isSymbol()) {
						optionValue = F.$str(((ISymbol) arg1).getSymbolName());
					}

					Iterator<IdentityHashMap<ISymbol, IASTAppendable>> iter = engine.optionsStackIterator();
					while (iter.hasNext()) {
						IdentityHashMap<ISymbol, IASTAppendable> map = iter.next();
						if (map != null) {
							optionsPattern = map.get(S.LHS_HEAD);
							if (optionsPattern != null) {
								ISymbol lhsHead = optionsPattern.topHead();
								optionsPattern = map.get(lhsHead);
								rhsRuleValue = rhsRuleValue(optionValue, optionsPattern);
								if (rhsRuleValue.isPresent()) {
									return rhsRuleValue;
								}
							}
						}
					}

				}

				if (optionsPattern != null) {
					// for (int i = 1; i < optionsPattern.size(); i++) {
					// IAST rule = (IAST) optionsPattern.get(i);
					// if (rule.arg1().equals(optionValue)) {
					// return rule.arg2();
					// }
					// }
					// String optionString = optionValue.toString();
					// for (int i = 1; i < optionsPattern.size(); i++) {
					// IAST rule = (IAST) optionsPattern.get(i);
					// if ((rule.arg1().isString() || rule.arg1().isSymbol()) && //
					// rule.arg1().toString().equals(optionString)) {
					// return rule.arg2();
					// }
					// }
					IOFunctions.printMessage(ast.topHead(), "optnf", F.List(optionsPattern, optionValue), engine);
					return optionValue;
				}

			}
			return F.NIL;
		}

		private IExpr rhsRuleValue(IExpr optionValue, IASTAppendable optionsPattern) {
			if (optionValue.isSymbol()) {
				optionValue = F.$str(((ISymbol) optionValue).getSymbolName());
			}
			if (optionsPattern != null) {
				for (int i = 1; i < optionsPattern.size(); i++) {
					IAST rule = (IAST) optionsPattern.get(i);
					if (rule.arg1().equals(optionValue)) {
						return rule.arg2();
					}
				}
				// String optionString = optionValue.toString();
				// for (int i = 1; i < optionsPattern.size(); i++) {
				// IAST rule = (IAST) optionsPattern.get(i);
				// if ((rule.arg1().isString() || rule.arg1().isSymbol()) && //
				// rule.arg1().toString().equals(optionString)) {
				// return rule.arg2();
				// }
				// }
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize(IAST ast) {
			return IOFunctions.ARGS_1_3;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	private final static class OwnValues extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			IExpr arg1 = Validate.checkSymbolType(ast, 1, engine);
			if (arg1.isPresent()) {
				ISymbol symbol = (ISymbol) arg1;
				IExpr value = symbol.assignedValue();
				if (value == null) {
					return F.CEmptyList;
				}
				return F.List(F.RuleDelayed(F.HoldPattern(symbol), value));
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize(IAST ast) {
			return IOFunctions.ARGS_1_1;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	public final static class OptionsPattern extends AbstractCoreFunctionEvaluator {
		public final static OptionsPattern CONST = new OptionsPattern();

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.head().equals(S.OptionsPattern)) {
				if (ast.isAST0()) {
					return F.$OptionsPattern(null);
				}
				if (ast.isAST1()) {
					return F.$OptionsPattern(null, ast.arg1());
				}
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize(IAST ast) {
			return IOFunctions.ARGS_0_1;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	public final static class Pattern extends AbstractCoreFunctionEvaluator {
		public final static Pattern CONST = new Pattern();

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.head().equals(F.Pattern)) {
				if (ast.size() == 3) {
					if (ast.arg1().isSymbol()) {
						final ISymbol symbol = (ISymbol) ast.arg1();

						IExpr arg2 = ast.arg2();
						if (arg2.isBlank()) {
							return F.$p(symbol, ((IPattern) arg2).getHeadTest());
						}
						if (arg2.isAST()) {

							if (arg2.size() == 1) {
								if (arg2.isAST(F.Blank)) {
									return F.$p(symbol);
								}
								if (arg2.isAST(F.BlankSequence)) {
									return F.$ps(symbol, null, false, false);
								}
								if (arg2.isAST(F.BlankNullSequence)) {
									return F.$ps(symbol, null, false, true);
								}
								if (arg2.isAST(F.OptionsPattern)) {
									return F.$OptionsPattern(symbol);
								}
							} else if (arg2.size() == 2) {
								IExpr first = arg2.first();
								if (first.isAST()) {
									first = engine.evalHoldPattern((IAST) first);
								}
								if (arg2.isAST(F.Blank)) {
									return F.$p(symbol, first);
								}
								if (arg2.isAST(F.BlankSequence)) {
									return F.$ps(symbol, first, false, false);
								}
								if (arg2.isAST(F.BlankNullSequence)) {
									return F.$ps(symbol, first, false, true);
								}
								if (arg2.isAST(F.OptionsPattern)) {
									return F.$OptionsPattern(symbol, first);
								}
							}

							arg2 = engine.evalHoldPattern((IAST) arg2);
						}
						return PatternNested.valueOf(symbol, arg2);
					}
				}
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize(IAST ast) {
			return IOFunctions.ARGS_2_2;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	public static class Repeated extends AbstractCoreFunctionEvaluator {
		public final static Repeated CONST = new Repeated();

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.head().equals(S.Repeated)) {
				if (ast.isAST1()) {
					return F.$Repeated(ast.arg1(), engine);
				}
				if (ast.isAST2()) {
					// TODO
				}
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize(IAST ast) {
			return IOFunctions.ARGS_1_2;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	public final static class RepeatedNull extends Repeated {
		public final static RepeatedNull CONST = new RepeatedNull();

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.head().equals(S.RepeatedNull)) {
				if (ast.isAST1()) {
					return F.$RepeatedNull(ast.arg1(), engine);
				}
				if (ast.isAST2()) {
					// TODO
				}
			}
			return F.NIL;
		}

	}

	/**
	 * <pre>
	 * Rule(x, y)
	 * 
	 * x -&gt; y
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * represents a rule replacing <code>x</code> with <code>y</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; a+b+c /. c-&gt;d
	 * a+b+d
	 * 
	 * &gt;&gt; {x,x^2,y} /. x-&gt;3
	 * {3,9,y}
	 * </pre>
	 * <p>
	 * Rule called with 3 arguments; 2 arguments are expected.
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; a /. Rule(1, 2, 3) -&gt; t 
	 * a
	 * </pre>
	 */
	private final static class Rule extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			IExpr leftHandSide = ast.arg1();
			// if (leftHandSide.isAST()) {
			// leftHandSide = engine.evalHoldPattern((IAST) leftHandSide);
			// } else {
			leftHandSide = engine.evaluate(leftHandSide);
			// }
			IExpr arg2 = engine.evaluateNull(ast.arg2());
			if (!arg2.isPresent()) {
				if (leftHandSide.equals(ast.arg1())) {
					return F.NIL;
				}
				return Rule(leftHandSide, ast.arg2());
			}
			return Rule(leftHandSide, arg2);
		}

		@Override
		public int[] expectedArgSize(IAST ast) {
			return IOFunctions.ARGS_2_2;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.SEQUENCEHOLD);
		}
	}

	/**
	 * <pre>
	 * RuleDelayed(x, y)
	 * 
	 * x :&gt; y
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * represents a rule replacing <code>x</code> with <code>y</code>, with <code>y</code> held unevaluated.
	 * </p>
	 * </blockquote>
	 */
	private final static class RuleDelayed extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {

			IExpr leftHandSide = ast.arg1();
			// if (leftHandSide.isAST()) {
			// leftHandSide = engine.evalHoldPattern((IAST) leftHandSide);
			// } else {
			leftHandSide = engine.evaluate(leftHandSide);
			// }
			if (!leftHandSide.equals(ast.arg1())) {
				return RuleDelayed(leftHandSide, ast.arg2());
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize(IAST ast) {
			return IOFunctions.ARGS_2_2;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDREST | ISymbol.SEQUENCEHOLD);
		}
	}

	/**
	 * <pre>
	 * Set(expr, value)
	 * 
	 * expr = value
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * evaluates <code>value</code> and assigns it to <code>expr</code>.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * {s1, s2, s3} = {v1, v2, v3}
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * sets multiple symbols <code>(s1, s2, ...)</code> to the corresponding values <code>(v1, v2, ...)</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * <p>
	 * <code>Set</code> can be used to give a symbol a value:<br />
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; a = 3    
	 * 3  
	 * 
	 * &gt;&gt; a      
	 * 3
	 * </pre>
	 * <p>
	 * You can set multiple values at once using lists:<br />
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; {a, b, c} = {10, 2, 3}    
	 * {10,2,3}    
	 * 
	 * &gt;&gt; {a, b, {c, {d}}} = {1, 2, {{c1, c2}, {a}}} 
	 * {1,2,{{c1,c2},{10}}}
	 * 
	 * &gt;&gt; d    
	 * 10
	 * </pre>
	 * <p>
	 * <code>Set</code> evaluates its right-hand side immediately and assigns it to the left-hand side:<br />
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; a    
	 * 1    
	 * 
	 * &gt;&gt; x = a    
	 * 1    
	 * 
	 * &gt;&gt; a = 2    
	 * 2    
	 * 
	 * &gt;&gt; x    
	 * 1
	 * </pre>
	 * <p>
	 * 'Set' always returns the right-hand side, which you can again use in an assignment:<br />
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; a = b = c = 2    
	 * &gt;&gt; a == b == c == 2    
	 * True
	 * </pre>
	 * <p>
	 * 'Set' supports assignments to parts:<br />
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; A = {{1, 2}, {3, 4}}    
	 * &gt;&gt; A[[1, 2]] = 5    
	 * 5    
	 * 
	 * &gt;&gt; A    
	 * {{1,5}, {3,4}}    
	 * 
	 * &gt;&gt; A[[;;, 2]] = {6, 7}    
	 * {6,7}    
	 * 
	 * &gt;&gt; A    
	 * {{1,6},{3,7}}
	 * </pre>
	 * <p>
	 * Set a submatrix:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; B = {{1, 2, 3}, {4, 5, 6}, {7, 8, 9}}    
	 * &gt;&gt; B[[1;;2, 2;;-1]] = {{t, u}, {y, z}}   
	 * &gt;&gt; B    
	 * {{1, t, u}, {4, y, z}, {7, 8, 9}}
	 * </pre>
	 */
	private final static class Set extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			final IExpr leftHandSide = ast.arg1();
			IExpr head = engine.evaluate(leftHandSide.head());
			if (head.topHead().equals(S.Association)) {
				head = S.Association;
			}
			IExpr rightHandSide = ast.arg2();
			try {
				rightHandSide = engine.evaluate(rightHandSide);
			} catch (final ConditionException e) {
				// System.out.println("Condition[] in right-hand-side of Set[]");
			} catch (final ReturnException e) {
				rightHandSide = e.getValue();
			}

			try {
				if (head.isBuiltInSymbol()) {
					if (leftHandSide.isAST()) {
						IBuiltInSymbol symbol = (IBuiltInSymbol) head;
						IEvaluator eval = symbol.getEvaluator();
						if (eval instanceof ISetEvaluator) {
							IExpr temp = ((ISetEvaluator) eval).evaluateSet(leftHandSide, rightHandSide, S.Set, engine);
							if (temp.isPresent()) {
								return temp;
							}
						}
					}
				}
				return createPatternMatcher(leftHandSide, rightHandSide, engine.isPackageMode(), engine);
			} catch (RuleCreationError rce) {
				// Cannot unset object `1`.
				IOFunctions.printMessage(ast.topHead(), "usraw", F.List(leftHandSide), engine);
				return rightHandSide;
			}
		}

		@Override
		public int[] expectedArgSize(IAST ast) {
			return IOFunctions.ARGS_2_2;
		}

		private static IExpr createPatternMatcher(IExpr leftHandSide, IExpr rightHandSide, boolean packageMode,
				final EvalEngine engine) throws RuleCreationError {
			int[] flags = new int[] { IPatternMatcher.NOFLAG };
			leftHandSide = evalLHS(leftHandSide, flags, engine);
			// try {
			// rightHandSide = engine.evaluate(rightHandSide);
			// } catch (final ConditionException e) {
			// // System.out.println("Condition[] in right-hand-side of Set[]");
			// } catch (final ReturnException e) {
			// rightHandSide = e.getValue();
			// }
			if (leftHandSide.isAST()) {
				if (leftHandSide.isAST(F.MessageName, 3) && leftHandSide.first().isSymbol()) {
					// Set[MessageName(f,"usage"),"text")
					ISymbol symbol = (ISymbol) leftHandSide.first();
					String messageName = leftHandSide.second().toString();
					IStringX message;
					if (rightHandSide instanceof IStringX) {
						message = (IStringX) rightHandSide;
					} else {
						message = F.stringx(rightHandSide.toString());
					}
					symbol.putMessage(IPatternMatcher.SET, messageName, message);
					return message;
				}
			}

			return setDownRule(leftHandSide, flags[0], rightHandSide, packageMode);
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDFIRST | ISymbol.SEQUENCEHOLD);
		}

	}

	/**
	 * <pre>
	 * SetDelayed(expr, value)
	 * 
	 * expr := value
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * assigns <code>value</code> to <code>expr</code>, without evaluating <code>value</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * <p>
	 * <code>SetDelayed</code> is like <code>Set</code>, except it has attribute <code>HoldAll</code>, thus it does not
	 * evaluate the right-hand side immediately, but evaluates it when needed.<br />
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; Attributes(SetDelayed)    
	 * {HoldAll}    
	 * 
	 * &gt;&gt; a = 1    
	 * 1    
	 * 
	 * &gt;&gt; x := a
	 * &gt;&gt; x    
	 * 1
	 * </pre>
	 * <p>
	 * Changing the value of <code>a</code> affects <code>x</code>:<br />
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; a = 2    
	 * 2    
	 * 
	 * &gt;&gt; x    
	 * 2
	 * </pre>
	 * <p>
	 * <code>Condition</code> (<code>/;</code>) can be used with <code>SetDelayed</code> to make an assignment that only
	 * holds if a condition is satisfied:<br />
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
	private final static class SetDelayed extends AbstractCoreFunctionEvaluator {

		// public final static SetDelayed CONST = new SetDelayed();

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			final IExpr leftHandSide = ast.arg1();
			IExpr head = engine.evaluate(leftHandSide.head());
			if (head.isAssociation()) {
				head = F.Association;
			}
			try {
				final IExpr rightHandSide = ast.arg2();

				if (head.isBuiltInSymbol()) {
					if (leftHandSide.isAST()) {
						IBuiltInSymbol symbol = (IBuiltInSymbol) head;
						IEvaluator eval = symbol.getEvaluator();
						if (eval instanceof ISetEvaluator) {
							IExpr temp = ((ISetEvaluator) eval).evaluateSet(leftHandSide, rightHandSide, S.SetDelayed,
									engine);
							if (temp.isPresent()) {
								return temp;
							}
						}
					}
				}
				createPatternMatcher(leftHandSide, rightHandSide, engine.isPackageMode(), engine);

				return F.Null;
			} catch (RuleCreationError rce) {
				// Cannot unset object `1`.
				IOFunctions.printMessage(ast.topHead(), "usraw", F.List(leftHandSide), engine);
				return F.$Failed;
			}
		}

		@Override
		public int[] expectedArgSize(IAST ast) {
			return IOFunctions.ARGS_2_2;
		}

		private static void createPatternMatcher(IExpr leftHandSide, IExpr rightHandSide, boolean packageMode,
				final EvalEngine engine) throws RuleCreationError {
			int[] flags = new int[] { IPatternMatcher.NOFLAG };
			leftHandSide = evalLHS(leftHandSide, flags, engine);
			setDelayedDownRule(leftHandSide, flags[0], rightHandSide, packageMode);
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL | ISymbol.SEQUENCEHOLD);
		}

	}

	private static IExpr setDownRule(IExpr leftHandSide, int flags, IExpr rightHandSide, boolean packageMode) {
		// final Object[] result = new Object[] { null, rightHandSide };
		if (leftHandSide.isAST()) {
			final ISymbol lhsSymbol = determineRuleTag(leftHandSide);
			if (lhsSymbol.isProtected()) {
				// Symbol `1` is Protected.
				IOFunctions.printMessage(F.SetDelayed, "wrsym", F.List(lhsSymbol), EvalEngine.get());
				throw new FailedException();
			}
			lhsSymbol.putDownRule(IPatternMatcher.SET, false, leftHandSide, rightHandSide, packageMode);
			return rightHandSide;
		}
		if (leftHandSide.isSymbol()) {
			final ISymbol lhsSymbol = (ISymbol) leftHandSide;
			if (lhsSymbol.isProtected()) {
				// Symbol `1` is Protected.
				IOFunctions.printMessage(F.SetDelayed, "wrsym", F.List(lhsSymbol), EvalEngine.get());
				throw new FailedException();
			}
			lhsSymbol.assignValue(rightHandSide);
			return rightHandSide;
		}

		throw new RuleCreationError(leftHandSide);
	}

	private static ISymbol determineRuleTag(IExpr leftHandSide) {
		while (leftHandSide.isCondition()) {
			if (leftHandSide.first().isAST()) {
				leftHandSide = leftHandSide.first();
				continue;
			}
			break;
		}
		if (leftHandSide.isSymbol()) {
			return (ISymbol) leftHandSide;
		}
		return leftHandSide.topHead();
	}

	public static IExpr setDownRule(int flags, IExpr leftHandSide, IExpr rightHandSide, boolean packageMode) {
		// final Object[] result = new Object[] { null, rightHandSide };
		if (leftHandSide.isAST()) {
			final ISymbol lhsSymbol = ((IAST) leftHandSide).topHead();
			lhsSymbol.putDownRule(IPatternMatcher.SET, false, leftHandSide, rightHandSide, packageMode);
			return rightHandSide;
		}
		if (leftHandSide.isSymbol()) {
			((ISymbol) leftHandSide).assignValue(rightHandSide);
			return rightHandSide;
		}

		throw new RuleCreationError(leftHandSide);
	}

	private static void setDelayedDownRule(IExpr leftHandSide, int flags, IExpr rightHandSide, boolean packageMode) {
		if (leftHandSide.isAST()) {
			if (leftHandSide.isAST(F.MessageName, 3) && leftHandSide.first().isSymbol()) {
				// Set[MessageName(f,"usage"),"text")
				ISymbol symbol = (ISymbol) leftHandSide.first();
				String messageName = leftHandSide.second().toString();
				IStringX message;
				if (rightHandSide instanceof IStringX) {
					message = (IStringX) rightHandSide;
				} else {
					message = F.stringx(rightHandSide.toString());
				}
				symbol.putMessage(IPatternMatcher.SET_DELAYED, messageName, message);
				return;
			}
			final ISymbol lhsSymbol = determineRuleTag(leftHandSide);
			// final ISymbol lhsSymbol = ((IAST) leftHandSide).topHead();
			if (lhsSymbol.isProtected()) {
				// Symbol `1` is Protected.
				IOFunctions.printMessage(F.SetDelayed, "wrsym", F.List(lhsSymbol), EvalEngine.get());
				throw new FailedException();
			}
			lhsSymbol.putDownRule(flags | IPatternMatcher.SET_DELAYED, false, leftHandSide, rightHandSide, packageMode);
			return;
		}
		if (leftHandSide.isSymbol()) {
			final ISymbol lhsSymbol = (ISymbol) leftHandSide;
			if (lhsSymbol.isProtected()) {
				// Symbol `1` is Protected.
				IOFunctions.printMessage(F.SetDelayed, "wrsym", F.List(lhsSymbol), EvalEngine.get());
				throw new FailedException();
			}
			((ISymbol) leftHandSide).assignValue(rightHandSide);
			return;
		}
		throw new RuleCreationError(leftHandSide);
	}

	public static void setDelayedDownRule(int priority, IExpr leftHandSide, IExpr rightHandSide, boolean packageMode) {
		if (leftHandSide.isAST()) {
			final ISymbol lhsSymbol = ((IAST) leftHandSide).topHead();

			lhsSymbol.putDownRule(IPatternMatcher.SET_DELAYED, false, leftHandSide, rightHandSide, priority,
					packageMode);
			return;
		}
		if (leftHandSide.isSymbol()) {
			((ISymbol) leftHandSide).assignValue(rightHandSide);
			return;
		}
		throw new RuleCreationError(leftHandSide);
	}

	private final static class TagSet extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			IExpr arg1 = ast.arg1();
			if (arg1.isSymbol()) {
				ISymbol symbol = (ISymbol) arg1;
				final IExpr leftHandSide = ast.arg2();
				IExpr rightHandSide = ast.arg3();
				try {
					rightHandSide = engine.evaluate(rightHandSide);
				} catch (final ConditionException e) {
					// System.out.println("Condition[] in right-hand-side of Set[]");
				} catch (final ReturnException e) {
					rightHandSide = e.getValue();
				}
				if (symbol.isProtected()) {
					IOFunctions.printMessage(F.SetDelayed, "write", F.List(symbol, leftHandSide), EvalEngine.get());
					throw new FailedException();
				}

				if (leftHandSide.isList()) {
					// thread over lists
					try {
						rightHandSide = engine.evaluate(rightHandSide);
					} catch (final ReturnException e) {
						rightHandSide = e.getValue();
					}
					IExpr temp = engine.threadASTListArgs(F.TagSet(symbol, leftHandSide, rightHandSide));
					if (temp.isPresent()) {
						return engine.evaluate(temp);
					}
				}
				try {
					Object[] result = createPatternMatcher(symbol, leftHandSide, rightHandSide, false, engine);
					return (IExpr) result[1];
				} catch (final ValidateException ve) {
					return engine.printMessage(ve.getMessage(ast.topHead()));
				}
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize(IAST ast) {
			return IOFunctions.ARGS_3_3;
		}

		private static Object[] createPatternMatcher(ISymbol tagSetSymbol, IExpr leftHandSide, IExpr rightHandSide,
				boolean packageMode, EvalEngine engine) throws RuleCreationError {
			final Object[] result = new Object[2];

			// if (leftHandSide.isAST()) {
			// leftHandSide = engine.evalHoldPattern((IAST) leftHandSide);
			// }
			int[] flags = new int[] { IPatternMatcher.NOFLAG };
			leftHandSide = evalLHS(leftHandSide, flags, engine);

			result[0] = null; // IPatternMatcher
			result[1] = rightHandSide;

			IAST lhsAST = Validate.checkASTUpRuleType(leftHandSide);
			result[0] = tagSetSymbol.putUpRule(flags[0] | IPatternMatcher.TAGSET, false, lhsAST, rightHandSide);
			return result;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL | ISymbol.SEQUENCEHOLD);
		}

	}

	private final static class TagSetDelayed extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			IExpr arg1 = ast.arg1();
			if (arg1.isSymbol()) {
				ISymbol symbol = (ISymbol) arg1;
				final IExpr leftHandSide = ast.arg2();
				final IExpr rightHandSide = ast.arg3();
				if (symbol.isProtected()) {
					IOFunctions.printMessage(ast.topHead(), "write", F.List(symbol, leftHandSide), EvalEngine.get());
					throw new FailedException();
				}
				try {
					createPatternMatcher(symbol, leftHandSide, rightHandSide, false, engine);

					return F.Null;
				} catch (final ValidateException ve) {
					return engine.printMessage(ve.getMessage(ast.topHead()));
				}
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize(IAST ast) {
			return IOFunctions.ARGS_3_3;
		}

		private static Object[] createPatternMatcher(ISymbol lhsSymbol, IExpr leftHandSide, IExpr rightHandSide,
				boolean packageMode, EvalEngine engine) throws RuleCreationError {
			final Object[] result = new Object[2];

			// if (leftHandSide.isAST()
			// && (((IAST) leftHandSide).getEvalFlags() & IAST.IS_FLATTENED_OR_SORTED_MASK) == IAST.NO_FLAG) {
			// leftHandSide = engine.evalHoldPattern((IAST) leftHandSide);
			// }
			int[] flags = new int[] { IPatternMatcher.NOFLAG };
			leftHandSide = evalLHS(leftHandSide, flags, engine);

			result[0] = null;
			result[1] = rightHandSide;

			IAST lhsAST = Validate.checkASTUpRuleType(leftHandSide);
			result[0] = lhsSymbol.putUpRule(flags[0] | IPatternMatcher.TAGSET_DELAYED, false, lhsAST, rightHandSide);
			return result;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL | ISymbol.SEQUENCEHOLD);
		}

	}

	/**
	 * <pre>
	 * <code>Unique(expr)
	 * </code>
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * create a unique symbol of the form <code>expr$...</code>.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * <code>Unique(&quot;expr&quot;)
	 * </code>
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * create a unique symbol of the form <code>expr...</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * <code>&gt;&gt; Unique(xy)
	 * xy$1
	 * 
	 * &gt;&gt; Unique(&quot;a&quot;)
	 * a1
	 * </code>
	 * </pre>
	 */
	private final static class Unique extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST1()) {
				if (ast.arg1().isSymbol()) {
					final String varAppend = ast.arg1().toString() + engine.uniqueName("$");
					return F.symbol(varAppend, engine);
				} else if (ast.arg1() instanceof IStringX) {
					// TODO start counter by 1....
					final String varAppend = engine.uniqueName(ast.arg1().toString());
					return F.symbol(varAppend, engine);
				}
			}
			return F.symbol(engine.uniqueName("$"), engine);
		}

		@Override
		public int[] expectedArgSize(IAST ast) {
			return IOFunctions.ARGS_0_1;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	/**
	 * <pre>
	 * Unset(expr)
	 * </pre>
	 * <p>
	 * or
	 * </p>
	 * 
	 * <pre>
	 * expr =.
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * removes any definitions belonging to the left-hand-side <code>expr</code>.
	 * </p>
	 * </blockquote>
	 * <h3>Examples</h3>
	 * 
	 * <pre>
	 * &gt;&gt; a = 2
	 * 2
	 * 
	 * &gt;&gt; a =.
	 * 
	 * &gt;&gt; a
	 * a
	 * </pre>
	 * <p>
	 * Unsetting an already unset or never defined variable will not change anything:
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; a =.
	 * 
	 * &gt;&gt; b =.
	 * </pre>
	 * <p>
	 * <code>Unset</code> can unset particular function values. It will print a message if no corresponding rule is
	 * found.
	 * </p>
	 * 
	 * <pre>
	 * &gt;&gt; f[x_) =.
	 * Assignment not found for: f(x_)
	 * 
	 * &gt;&gt; f(x_) := x ^ 2
	 * 
	 * &gt;&gt; f(3)
	 * 9
	 * 
	 * &gt;&gt; f(x_) =.
	 * 
	 * &gt;&gt; f(3)
	 * f(3)
	 * </pre>
	 */
	private final static class Unset extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			final IExpr leftHandSide = ast.arg1();
			try {
				if (leftHandSide.isList()) {
					// thread over lists
					IExpr temp = engine.threadASTListArgs((IASTMutable) F.Unset(leftHandSide));
					if (temp.isPresent()) {
						return engine.evaluate(temp);
					}
					return F.NIL;
				}

				if (leftHandSide.isAST()) {
					final ISymbol lhsSymbol = determineRuleTag(leftHandSide);
					if (lhsSymbol.isProtected()) {
						// Symbol `1` is Protected.
						IOFunctions.printMessage(ast.topHead(), "wrsym", F.List(lhsSymbol), EvalEngine.get());
						throw new FailedException();
					}
				}
				if (leftHandSide.isSymbol()) {
					final ISymbol lhsSymbol = (ISymbol) leftHandSide;
					if (lhsSymbol.isProtected()) {
						// Symbol `1` is Protected.
						IOFunctions.printMessage(ast.topHead(), "wrsym", F.List(lhsSymbol), EvalEngine.get());
						throw new FailedException();
					}
				}

				removePatternMatcher(leftHandSide, engine.isPackageMode(), engine);
				return F.Null;
			} catch (RuleCreationError rce) {
				// Cannot unset object `1`.
				IOFunctions.printMessage(ast.topHead(), "usraw", F.List(leftHandSide), engine);
				return F.$Failed;
			}
		}

		@Override
		public int[] expectedArgSize(IAST ast) {
			return IOFunctions.ARGS_1_1;
		}

		private static void removePatternMatcher(IExpr leftHandSide, boolean packageMode, EvalEngine engine)
				throws RuleCreationError {

			if (leftHandSide.isAST()) {
				leftHandSide = engine.evalHoldPattern((IAST) leftHandSide);
			}
			removeRule(leftHandSide, packageMode);
		}

		private static void removeRule(IExpr leftHandSide, boolean packageMode) {
			if (leftHandSide.isAST()) {
				final ISymbol lhsSymbol = ((IAST) leftHandSide).topHead();
				if (!lhsSymbol.removeRule(IPatternMatcher.SET, false, leftHandSide, packageMode)) {
					printAssignmentNotFound(leftHandSide);
				}
				return;
			}
			if (leftHandSide.isSymbol()) {
				final ISymbol lhsSymbol = (ISymbol) leftHandSide;

				if (!lhsSymbol.removeRule(IPatternMatcher.SET, true, leftHandSide, packageMode)) {
					printAssignmentNotFound(leftHandSide);
				}
				return;
			}

			throw new RuleCreationError(leftHandSide);
		}

		private static void printAssignmentNotFound(final IExpr leftHandSide) {
			EvalEngine.get().printMessage("Assignment not found for: " + leftHandSide.toString());
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	private final static class UpSet extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			final IExpr leftHandSide = ast.arg1();
			IExpr rightHandSide = ast.arg2();
			try {
				if (leftHandSide.isList()) {
					// thread over lists
					try {
						rightHandSide = engine.evaluate(rightHandSide);
					} catch (final ReturnException e) {
						rightHandSide = e.getValue();
					}
					IExpr temp = engine.threadASTListArgs((IASTMutable) F.UpSet(leftHandSide, rightHandSide));
					if (temp.isPresent()) {
						return engine.evaluate(temp);
					}
				}
				Object[] result = createPatternMatcher(leftHandSide, rightHandSide, false, engine);
				return (IExpr) result[1];
			} catch (final ValidateException ve) {
				return engine.printMessage(ve.getMessage(ast.topHead()));
			}
		}

		@Override
		public int[] expectedArgSize(IAST ast) {
			return IOFunctions.ARGS_2_2;
		}

		private static Object[] createPatternMatcher(IExpr leftHandSide, IExpr rightHandSide, boolean packageMode,
				EvalEngine engine) throws RuleCreationError {
			final Object[] result = new Object[2];

			int[] flags = new int[] { IPatternMatcher.NOFLAG };
			leftHandSide = evalLHS(leftHandSide, flags, engine);

			try {
				rightHandSide = engine.evaluate(rightHandSide);
			} catch (final ConditionException e) {
				// System.out.println("Condition[] in right-hand-side of UpSet[]");
			} catch (final ReturnException e) {
				rightHandSide = e.getValue();
			}

			result[0] = null; // IPatternMatcher
			result[1] = rightHandSide;

			IAST lhsAST = Validate.checkASTUpRuleType(leftHandSide);
			for (int i = 1; i < lhsAST.size(); i++) {
				IExpr temp = lhsAST.get(i);
				if (temp instanceof IPatternObject) {
					IExpr headTest = ((IPatternObject) temp).getHeadTest();
					if (headTest != null && headTest.isSymbol()) {
						ISymbol lhsSymbol = (ISymbol) headTest;
						result[0] = lhsSymbol.putUpRule(flags[0] | IPatternMatcher.UPSET, false, lhsAST, rightHandSide);
					}
					continue;
				}
				ISymbol lhsSymbol = null;
				if (temp.isSymbol()) {
					lhsSymbol = (ISymbol) temp;
				} else {
					lhsSymbol = lhsAST.get(i).topHead();
				}
				result[0] = lhsSymbol.putUpRule(flags[0] | IPatternMatcher.UPSET, false, lhsAST, rightHandSide);
			}
			return result;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	private final static class UpSetDelayed extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			final IExpr leftHandSide = ast.arg1();
			final IExpr rightHandSide = ast.arg2();
			try {
				createPatternMatcher(leftHandSide, rightHandSide, false, engine);

				return F.Null;
			} catch (final ValidateException ve) {
				return engine.printMessage(ve.getMessage(ast.topHead()));
			}
		}

		@Override
		public int[] expectedArgSize(IAST ast) {
			return IOFunctions.ARGS_2_2;
		}

		private static Object[] createPatternMatcher(IExpr leftHandSide, IExpr rightHandSide, boolean packageMode,
				EvalEngine engine) throws RuleCreationError {
			final Object[] result = new Object[2];

			int[] flags = new int[] { IPatternMatcher.NOFLAG };
			leftHandSide = evalLHS(leftHandSide, flags, engine);

			result[0] = null;
			result[1] = rightHandSide;

			IAST lhsAST = Validate.checkASTUpRuleType(leftHandSide);
			for (int i = 1; i < lhsAST.size(); i++) {
				IExpr temp = lhsAST.get(i);
				if (temp instanceof IPatternObject) {
					IExpr headTest = ((IPatternObject) temp).getHeadTest();
					if (headTest != null && headTest.isSymbol()) {
						ISymbol lhsSymbol = (ISymbol) headTest;
						result[0] = lhsSymbol.putUpRule(flags[0] | IPatternMatcher.UPSET_DELAYED, false, lhsAST,
								rightHandSide);
					}
					continue;
				}
				ISymbol lhsSymbol = null;
				if (temp.isSymbol()) {
					lhsSymbol = (ISymbol) temp;
				} else {
					lhsSymbol = lhsAST.get(i).topHead();
				}
				result[0] = lhsSymbol.putUpRule(flags[0] | IPatternMatcher.UPSET_DELAYED, false, lhsAST, rightHandSide);
			}
			return result;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	private final static class UpValues extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			IExpr arg1 = Validate.checkSymbolType(ast, 1, engine);
			if (arg1.isPresent()) {
				ISymbol symbol = (ISymbol) arg1;
				RulesData rulesData = symbol.getRulesData();
				if (rulesData == null) {
					return F.CEmptyList;
				}
				return rulesData.upValues();
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize(IAST ast) {
			return IOFunctions.ARGS_1_1;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	private static IExpr evalLHS(IExpr leftHandSide, int[] flags, EvalEngine engine) {
		if (leftHandSide.isAST()
				&& (((IAST) leftHandSide).getEvalFlags() & IAST.IS_FLATTENED_OR_SORTED_MASK) == IAST.NO_FLAG) {
			if (leftHandSide.isHoldPatternOrLiteral()) {
				flags[0] = leftHandSide.isAST(F.HoldPattern, 2) ? IPatternMatcher.HOLDPATTERN : IPatternMatcher.LITERAL;
				return leftHandSide.first();
			}
			return engine.evalHoldPattern((IAST) leftHandSide);
		}
		return leftHandSide;
	}

	public static void extractRules(IExpr x, IASTAppendable optionsPattern) {
		if (x != null) {
			if (x.isSequence() || x.isList()) {
				IAST list = (IAST) x;
				for (int i = 1; i < list.size(); i++) {
					// also for nested lists
					extractRules(list.get(i), optionsPattern);
				}
			} else if (x.isRuleAST()) {
				if (x.first().isSymbol()) {
					String name = ((ISymbol) x.first()).getSymbolName();
					optionsPattern.append(F.binaryAST2(x.topHead(), name, x.second()));
				} else {
					optionsPattern.append((IAST) x);
				}
			}
		}
	}

	/**
	 * Returns a list of the default options of a symbol defined by <code>Option(f)={a->b,...}</code>.
	 * 
	 * @param symbol
	 * @param optionValueRules
	 *            convert to &quot;string&quot;" rules, suitable for <code>OptionValue</code>
	 * @return
	 */
	public static IAST optionsList(ISymbol symbol, boolean optionValueRules) {
		if (symbol.isBuiltInSymbol()) {
			IBuiltInSymbol builinSymbol = (IBuiltInSymbol) symbol;
			IEvaluator evaluator = builinSymbol.getEvaluator();
			if (evaluator != null) {
				IAST list = evaluator.options();
				if (list.isPresent()) {
					if (optionValueRules) {
						IASTAppendable result = F.ListAlloc(list.size() + 5);
						extractRules(list, result);
						return result;
					}
					return list;
				}
			}
			return F.CEmptyList;
		}
		RulesData rules = symbol.getRulesData();
		if (rules != null) {
			Map<IExpr, PatternMatcherEquals> map = rules.getEqualDownRules();
			PatternMatcherEquals matcher = map.get(F.Options(symbol));
			if (matcher != null) {
				IExpr temp = matcher.getRHS();
				if (optionValueRules) {
					IASTAppendable result = F.ListAlloc(10);
					extractRules(temp, result);
					return result;
				}
				if (temp.isList()) {
					return (IAST) temp;
				}
				return F.List(temp);
			}
		}
		return F.CEmptyList;
	}

	public static void initialize() {
		Initializer.init();
	}

	private PatternMatching() {

	}
}
