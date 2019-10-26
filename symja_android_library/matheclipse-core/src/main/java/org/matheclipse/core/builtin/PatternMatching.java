package org.matheclipse.core.builtin;

import static org.matheclipse.core.expression.F.Rule;
import static org.matheclipse.core.expression.F.RuleDelayed;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.util.List;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.ConditionException;
import org.matheclipse.core.eval.exception.FailedException;
import org.matheclipse.core.eval.exception.ReturnException;
import org.matheclipse.core.eval.exception.RuleCreationError;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.exception.WrongArgumentType;
import org.matheclipse.core.eval.exception.WrongNumberOfArguments;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.ISetEvaluator;
import org.matheclipse.core.eval.util.Lambda;
import org.matheclipse.core.eval.util.OptionArgs;
import org.matheclipse.core.expression.Context;
import org.matheclipse.core.expression.ContextPath;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.form.Documentation;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTMutable;
import org.matheclipse.core.interfaces.IBuiltInSymbol;
import org.matheclipse.core.interfaces.IEvaluator;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.IPatternObject;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.parser.client.Parser;
import org.matheclipse.parser.client.ast.ASTNode;

import com.google.common.io.Files;

public final class PatternMatching {

	/**
	 * 
	 * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation in static
	 * initializer</a>
	 */
	private static class Initializer {

		private static void init() {
			F.BeginPackage.setEvaluator(new BeginPackage());
			F.EndPackage.setEvaluator(new EndPackage());
			F.Begin.setEvaluator(new Begin());
			F.End.setEvaluator(new End());
			F.Blank.setEvaluator(Blank.CONST);
			F.BlankSequence.setEvaluator(BlankSequence.CONST);
			F.BlankNullSequence.setEvaluator(BlankNullSequence.CONST);
			F.Clear.setEvaluator(new Clear());
			F.ClearAll.setEvaluator(new ClearAll());
			F.Context.setEvaluator(new ContextFunction());
			F.Default.setEvaluator(new Default());
			F.Definition.setEvaluator(new Definition());
			F.Evaluate.setEvaluator(new Evaluate());
			F.Get.setEvaluator(new Get());
			F.Hold.setEvaluator(new Hold());
			F.HoldPattern.setEvaluator(new HoldPattern());
			F.Identity.setEvaluator(new Identity());
			F.Information.setEvaluator(new Information());
			F.Literal.setEvaluator(new Literal());
			F.MessageName.setEvaluator(new MessageName());
			F.Optional.setEvaluator(Optional.CONST);
			F.Options.setEvaluator(new Options());
			F.Pattern.setEvaluator(Pattern.CONST);
			F.Put.setEvaluator(new Put());
			F.Rule.setEvaluator(new Rule());
			F.RuleDelayed.setEvaluator(new RuleDelayed());
			F.Set.setEvaluator(new Set());
			F.SetDelayed.setEvaluator(new SetDelayed());
			F.TagSet.setEvaluator(new TagSet());
			F.TagSetDelayed.setEvaluator(new TagSetDelayed());
			F.Unique.setEvaluator(new Unique());
			F.Unset.setEvaluator(new Unset());
			F.UpSet.setEvaluator(new UpSet());
			F.UpSetDelayed.setEvaluator(new UpSetDelayed());
		}
	}

	private final static class Begin extends AbstractCoreFunctionEvaluator {
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			String contextName = Validate.checkContextName(ast, 1);
			Context pack = EvalEngine.get().getContextPath().currentContext();
			// String packageName = pack.getContextName();
			// if (packageName.equals(Context.GLOBAL_CONTEXT_NAME)) {
			// completeContextName = contextName;
			// } else {
			// completeContextName = packageName.substring(0, packageName.length() - 1) + contextName;
			// }
			Context context = engine.begin(contextName, pack);
			return F.stringx(context.completeContextName());
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_1;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	private final static class BeginPackage extends AbstractFunctionEvaluator {
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.size() > 1) {
				String contextName = Validate.checkContextName(ast, 1);
				engine.beginPackage(contextName);
				if (Config.isFileSystemEnabled(engine)) {
					try {
						for (int j = 2; j < ast.size(); j++) {
							// FileReader reader = new FileReader(ast.get(j).toString());
							FileInputStream fis = new FileInputStream(ast.get(j).toString());
							BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
							Get.loadPackage(engine, reader);
							reader.close();
							fis.close();
						}
					} catch (IOException e) {
						if (Config.SHOW_STACKTRACE) {
							e.printStackTrace();
						}
					}
				}
				return F.Null;
			}
			return F.NIL;
		}

	}

	private final static class End extends AbstractCoreFunctionEvaluator {
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Context context = engine.end();
			if (context == null) {
				return F.NIL;
			}
			return F.stringx(context.completeContextName());
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_0_0;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	private final static class EndPackage extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			engine.endPackage();
			return F.Null;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
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
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	private final static class BlankSequence extends AbstractCoreFunctionEvaluator {
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
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	private final static class BlankNullSequence extends AbstractCoreFunctionEvaluator {
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
			Lambda.forEach(ast, x -> x.isSymbol(), x -> ((ISymbol) x).clearAll(engine));
			return F.Null;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	private final static class ContextFunction extends AbstractCoreFunctionEvaluator {

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
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	/**
	 * Get the default value for a symbol (i.e. <code>1</code> is the default value for <code>Times</code>,
	 * <code>0</code> is the default value for <code>Plus</code>).
	 */
	private final static class Default extends AbstractFunctionEvaluator implements ISetEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			ISymbol symbol = Validate.checkSymbolType(ast, 1);

			if (ast.size() > 2) {
				int pos = ast.arg2().toIntDefault();
				if (pos > 0) {
					return symbol.getDefaultValue(pos);
				}
			} else {
				return symbol.getDefaultValue();
			}
			return F.NIL;
		}

		@Override
		public IExpr evaluateSet(final IExpr leftHandSide, IExpr rightHandSide, EvalEngine engine) {
			if (leftHandSide.isAST(F.Default) && leftHandSide.size() > 1) {
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
		public int[] expectedArgSize() {
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
			ISymbol symbol = Validate.checkSymbolType(ast, 1);

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

			return F.Null;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_1;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}

	}

	private final static class Evaluate extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.size() == 2) {
				return engine.evaluate(ast.arg1());
			}
			IASTMutable sequence = ast.copy();
			sequence.set(0, F.Sequence);
			return engine.evaluate(sequence);
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
		}

	}

	/**
	 * Get[{&lt;file name&gt;}}
	 * 
	 */
	private final static class Get extends AbstractFunctionEvaluator {

		private static int addContextToPath(ContextPath contextPath, final List<ASTNode> node, int i,
				final EvalEngine engine, ISymbol endSymbol) {
			ContextPath path = engine.getContextPath();
			try {
				engine.setContextPath(contextPath);
				AST2Expr ast2Expr = new AST2Expr(engine.isRelaxedSyntax(), engine);
				while (i < node.size()) {
					IExpr temp = ast2Expr.convert(node.get(i++));
					if (temp.isAST()) {
						IExpr head = temp.head();
						IAST ast = (IAST) temp;
						if (head.equals(endSymbol) && ast.isAST0()) {
							continue;
						} else if (head.equals(F.Begin) && ast.size() >= 2) {
							try {
								contextPath.add(engine.getContextPath().getContext(ast.arg1().toString()));
								i = addContextToPath(contextPath, node, i, engine, F.End);
							} finally {
								contextPath.remove(contextPath.size() - 1);
							}
							continue;
						}
					}
					engine.evaluate(temp);
				}
				// TODO add error message
			} finally {
				engine.setContextPath(path);
			}
			return i;
		}

		/**
		 * Load a package from the given reader
		 * 
		 * @param engine
		 * @param is
		 * @return the last evaluated expression result
		 */
		protected static IExpr loadPackage(final EvalEngine engine, final String is) {
			Context packageContext = null;
			try {
				final List<ASTNode> node = parseReader(is, engine);
				return evaluatePackage(node, engine);
			} catch (final Exception e) {
				e.printStackTrace();
			} finally {
				if (packageContext != null) {
					engine.getContextPath().add(packageContext);
				}
			}
			return F.Null;
		}

		/**
		 * Load a package from the given reader
		 * 
		 * @param engine
		 * @param is
		 * @return the last evaluated expression result
		 */
		protected static IExpr loadPackage(final EvalEngine engine, final BufferedReader is) {
			final BufferedReader r = is;
			Context packageContext = null;
			try {
				final List<ASTNode> node = parseReader(r, engine);

				return evaluatePackage(node, engine);
			} catch (final Exception e) {
				e.printStackTrace();
			} finally {
				if (packageContext != null) {
					engine.getContextPath().add(packageContext);
				}
				try {
					r.close();
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return F.Null;
		}

		/**
		 * <p>
		 * Parse the <code>reader</code> input.
		 * </p>
		 * <p>
		 * This method ignores the first line of the script if it starts with the <code>#!</code> characters (i.e. Unix
		 * Script Executables)
		 * </p>
		 * <p>
		 * <b>Note</b>: uses the <code>ASTNode</code> parser and not the <code>ExprParser</code>, because otherwise the
		 * symbols couldn't be assigned to the contexts.
		 * </p>
		 * 
		 * @param reader
		 * @param engine
		 * @return
		 * @throws IOException
		 */
		public static List<ASTNode> parseReader(final BufferedReader reader, final EvalEngine engine)
				throws IOException {
			String record;
			StringBuilder builder = new StringBuilder(2048);
			if ((record = reader.readLine()) != null) {
				// ignore the first line of the script if it starts with the #!
				// characters (i.e. Unix Script Executables)
				if (!record.startsWith("!#")) {
					builder.append(record);
					builder.append('\n');
				}
			}
			while ((record = reader.readLine()) != null) {
				builder.append(record);
				builder.append('\n');
			}
			final Parser parser = new Parser(engine.isRelaxedSyntax(), true);
			final List<ASTNode> node = parser.parsePackage(builder.toString());
			return node;
		}

		/**
		 * <p>
		 * Parse the <code>reader</code> input.
		 * </p>
		 * <p>
		 * This method ignores the first line of the script if it starts with the <code>#!</code> characters (i.e. Unix
		 * Script Executables)
		 * </p>
		 * <p>
		 * <b>Note</b>: uses the <code>ASTNode</code> parser and not the <code>ExprParser</code>, because otherwise the
		 * symbols couldn't be assigned to the contexts.
		 * </p>
		 * 
		 * @param reader
		 * @param engine
		 * @return
		 * @throws IOException
		 */
		public static List<ASTNode> parseReader(final String reader, final EvalEngine engine) throws IOException {
			final Parser parser = new Parser(engine.isRelaxedSyntax(), true);
			final List<ASTNode> node = parser.parsePackage(reader);
			return node;
		}

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (Config.isFileSystemEnabled(engine)) {

				if (!(ast.arg1() instanceof IStringX)) {
					throw new WrongNumberOfArguments(ast, 1, ast.argSize());
				}
				IStringX arg1 = (IStringX) ast.arg1();
				File file = new File(arg1.toString());

				if (file.exists()) {
					// System.out.println(file.toString());
					return getFile(file, engine);
				} else {
					file = FileSystems.getDefault().getPath(arg1.toString()).toAbsolutePath().toFile();
					if (file.exists()) {
						return getFile(file, engine);
					}
				}

			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_1;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
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

	private final static class Identity extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			return ast.arg1();
		}

		@Override
		public int[] expectedArgSize() {
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
					if (!arg1.isSymbol()) {
						throw new WrongArgumentType(ast, ast.arg1(), 1, "");
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
						if (Config.SHOW_STACKTRACE) {
							ioe.printStackTrace();
						}
					}
				}
				return F.Null;
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
			// Assignment of the message is handled in the Set() function
			if (!ast.arg1().isSymbol()) {
				throw new WrongArgumentType(ast, ast.arg1(), 1, "");
			}
			// if (!ast.arg2().isAST(F.Set, 3)) {
			// throw new WrongArgumentType(ast, ast.arg2(), 2, "");
			// }
			IExpr arg2 = engine.evaluate(ast.arg2());
			if (arg2 instanceof IStringX || arg2.isSymbol()) {
				return F.NIL;
			}

			return F.Null;
		}

		@Override
		public int[] expectedArgSize() {
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
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
		}
	}

	private final static class Options extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST1() && ast.arg1().isBuiltInSymbol()) {
				IBuiltInSymbol sym = (IBuiltInSymbol) ast.arg1();
				IEvaluator evaluator = sym.getEvaluator();
				IAST list = evaluator.options();
				if (list.isPresent()) {
					return list;
				}
				return F.CEmptyList;
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
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
						if (ast.arg2().isBlank()) {
							IPatternObject blank = (IPatternObject) ast.arg2();
							return F.$p((ISymbol) ast.arg1(), blank.getHeadTest());
						}
						if (ast.arg2().isAST(F.Blank, 1)) {
							return F.$p((ISymbol) ast.arg1());
						}
						if (ast.arg2().isAST(F.BlankSequence, 1)) {
							return F.$ps((ISymbol) ast.arg1(), null, false, false);
						}
						if (ast.arg2().isAST(F.BlankNullSequence, 1)) {
							return F.$ps((ISymbol) ast.arg1(), null, false, true);
						}
						if (ast.arg2().isAST(F.BlankSequence, 2)) {
							return F.$ps((ISymbol) ast.arg1(), ast.arg2().first(), false, false);
						}
						if (ast.arg2().isAST(F.BlankNullSequence, 2)) {
							return F.$ps((ISymbol) ast.arg1(), ast.arg2().first(), false, true);
						}
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
	 * Put[{&lt;file name&gt;}}
	 * 
	 */
	private final static class Put extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (Config.isFileSystemEnabled(engine)) {

				final int argSize = ast.argSize();
				IStringX fileName = Validate.checkStringType(ast, argSize);
				FileWriter writer;
				try {
					writer = new FileWriter(fileName.toString());
					final StringBuilder buf = new StringBuilder();
					for (int i = 1; i < argSize; i++) {
						IExpr temp = engine.evaluate(ast.get(i));
						if (!OutputFormFactory.get().convert(buf, temp)) {
							return engine.printMessage("Put: file " + fileName.toString() + "ERROR-IN_OUTPUTFORM");
						}
						buf.append('\n');
						if (i < argSize - 1) {
							buf.append('\n');
						}
					}
					writer.write(buf.toString());
					writer.close();
				} catch (IOException e) {
					return engine.printMessage("Put: file " + fileName.toString() + " I/O exception !");
				}
				return F.Null;
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_2_INFINITY;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL);
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
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_2_2;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL | ISymbol.SEQUENCEHOLD);
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
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_2_2;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDALL | ISymbol.SEQUENCEHOLD);
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
			final IExpr head = leftHandSide.head();

			IExpr rightHandSide = ast.arg2();
			try {
				rightHandSide = engine.evaluate(rightHandSide);
			} catch (final ConditionException e) {
				// System.out.println("Condition[] in right-hand-side of Set[]");
			} catch (final ReturnException e) {
				rightHandSide = e.getValue();
			}

			if (head.isBuiltInSymbol()) {
				if (leftHandSide.isAST()) {
					IBuiltInSymbol symbol = (IBuiltInSymbol) head;
					IEvaluator eval = symbol.getEvaluator();
					if (eval instanceof ISetEvaluator) {
						IExpr temp = ((ISetEvaluator) eval).evaluateSet(leftHandSide, rightHandSide, engine);
						if (temp.isPresent()) {
							return temp;
						}
					}
				}
			}
			return createPatternMatcher(leftHandSide, rightHandSide, engine.isPackageMode(), engine);
			// return (IExpr) result[1];
		}

		@Override
		public int[] expectedArgSize() {
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
			newSymbol.setAttributes(ISymbol.HOLDALL | ISymbol.SEQUENCEHOLD);
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
			final IExpr rightHandSide = ast.arg2();

			createPatternMatcher(leftHandSide, rightHandSide, engine.isPackageMode(), engine);

			return F.Null;
		}

		@Override
		public int[] expectedArgSize() {
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
				IOFunctions.printMessage(F.SetDelayed, "write", F.List(lhsSymbol, leftHandSide), EvalEngine.get());
				throw new FailedException();
			}
			lhsSymbol.putDownRule(IPatternMatcher.SET, false, leftHandSide, rightHandSide, packageMode);
			return rightHandSide;
		}
		if (leftHandSide.isSymbol()) {
			final ISymbol lhsSymbol = (ISymbol) leftHandSide;
			if (lhsSymbol.isProtected()) {
				IOFunctions.printMessage(F.SetDelayed, "write", F.List(lhsSymbol, leftHandSide), EvalEngine.get());
				throw new FailedException();
			}
			lhsSymbol.assign(rightHandSide);
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
			((ISymbol) leftHandSide).assign(rightHandSide);
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
				IOFunctions.printMessage(F.SetDelayed, "write", F.List(lhsSymbol, leftHandSide), EvalEngine.get());
				throw new FailedException();
			}
			lhsSymbol.putDownRule(flags | IPatternMatcher.SET_DELAYED, false, leftHandSide, rightHandSide, packageMode);
			return;
		}
		if (leftHandSide.isSymbol()) {
			final ISymbol lhsSymbol = (ISymbol) leftHandSide;
			if (lhsSymbol.isProtected()) {
				IOFunctions.printMessage(F.SetDelayed, "write", F.List(lhsSymbol, leftHandSide), EvalEngine.get());
				throw new FailedException();
			}
			((ISymbol) leftHandSide).assign(rightHandSide);
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
			((ISymbol) leftHandSide).assign(rightHandSide);
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
				Object[] result = createPatternMatcher(symbol, leftHandSide, rightHandSide, false, engine);
				return (IExpr) result[1];
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize() {
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

			try {
				rightHandSide = engine.evaluate(rightHandSide);
			} catch (final ConditionException e) {
				System.out.println("Condition[] in right-hand-side of UpSet[]");
			} catch (final ReturnException e) {
				rightHandSide = e.getValue();
			}

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
					IOFunctions.printMessage(F.SetDelayed, "write", F.List(symbol, leftHandSide), EvalEngine.get());
					throw new FailedException();
				}

				createPatternMatcher(symbol, leftHandSide, rightHandSide, false, engine);

				return F.Null;
			}
			return F.NIL;
		}

		@Override
		public int[] expectedArgSize() {
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
	 * Unique(expr)
	 * </pre>
	 * 
	 * <blockquote>
	 * <p>
	 * create a unique symbol of the form <code>expr$...</code>.
	 * </p>
	 * </blockquote>
	 * 
	 * <pre>
	 * Unique("expr")
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
	 * &gt;&gt; Unique(xy)
	 * xy$1
	 * 
	 * &gt;&gt; Unique("a")
	 * a1
	 * </pre>
	 */
	private final static class Unique extends AbstractCoreFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			final int moduleCounter = engine.incModuleCounter();
			if (ast.isAST1()) {
				if (ast.arg1().isSymbol()) {
					final String varAppend = ast.arg1().toString() + "$" + moduleCounter;
					return F.symbol(varAppend, engine);
				} else if (ast.arg1() instanceof IStringX) {
					// TODO start counter by 1....
					final String varAppend = ast.arg1().toString() + moduleCounter;
					return F.symbol(varAppend, engine);
				}
			}
			final String varAppend = "$" + moduleCounter;
			return F.symbol(varAppend, engine);
		}

		@Override
		public int[] expectedArgSize() {
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
			if (leftHandSide.isList()) {
				// thread over lists
				IExpr temp = engine.threadASTListArgs((IASTMutable) F.Unset(leftHandSide));
				if (temp.isPresent()) {
					return engine.evaluate(temp);
				}
			}
			removePatternMatcher(leftHandSide, engine.isPackageMode(), engine);
			return F.Null;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_1_1;
		}

		public void removePatternMatcher(IExpr leftHandSide, boolean packageMode, EvalEngine engine)
				throws RuleCreationError {

			if (leftHandSide.isAST()) {
				leftHandSide = engine.evalHoldPattern((IAST) leftHandSide);
			}
			removeRule(leftHandSide, packageMode);
		}

		public void removeRule(IExpr leftHandSide, boolean packageMode) {
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

		private void printAssignmentNotFound(final IExpr leftHandSide) {
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
		}

		@Override
		public int[] expectedArgSize() {
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
				System.out.println("Condition[] in right-hand-side of UpSet[]");
			} catch (final ReturnException e) {
				rightHandSide = e.getValue();
			}

			result[0] = null; // IPatternMatcher
			result[1] = rightHandSide;

			IAST lhsAST = Validate.checkASTUpRuleType(leftHandSide);
			for (int i = 1; i < lhsAST.size(); i++) {
				IExpr temp = lhsAST.get(i);
				if (temp instanceof IPatternObject) {
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

			createPatternMatcher(leftHandSide, rightHandSide, false, engine);

			return F.Null;
		}

		@Override
		public int[] expectedArgSize() {
			return IOFunctions.ARGS_2_2;
		}

		private static Object[] createPatternMatcher(IExpr leftHandSide, IExpr rightHandSide, boolean packageMode,
				EvalEngine engine) throws RuleCreationError {
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

			for (int i = 1; i < lhsAST.size(); i++) {
				IExpr temp = lhsAST.get(i);
				if (temp instanceof IPatternObject) {
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

	public static IExpr evaluatePackage(final List<ASTNode> node, final EvalEngine engine) {
		IExpr temp;
		int i = 0;
		AST2Expr ast2Expr = new AST2Expr(engine.isRelaxedSyntax(), engine);
		IExpr result = F.Null;
		while (i < node.size()) {
			temp = ast2Expr.convert(node.get(i++));
			// if (temp.isAST()) {
			// IAST ast = (IAST) temp;
			// IExpr head = ast.head();
			// if (ast.isASTSizeGE(F.BeginPackage, 2)) {
			// String contextName = Validate.checkContextName(ast, 1);
			// packageContext = engine.getContextPath().getContext(contextName);
			// ISymbol endSymbol = F.EndPackage;
			// for (int j = 2; j < ast.size(); j++) {
			// // FileReader reader = new FileReader(ast.get(j).toString());
			// BufferedReader reader = new BufferedReader(
			// new InputStreamReader(new FileInputStream(ast.get(j).toString()), "UTF-8"));
			// Get.loadPackage(engine, reader);
			// reader.close();
			// }
			// i = addContextToPath(new ContextPath(packageContext), node, i, engine, endSymbol);
			// continue;
			// } else if (head.equals(F.Begin) && ast.size() >= 2) {
			// String contextName = Validate.checkContextName(ast, 1);
			// ISymbol endSymbol = F.End;
			// i = addContextToPath(new ContextPath(contextName), node, i, engine, endSymbol);
			// continue;
			// }
			// }
			result = engine.evaluate(temp);
		}
		return result;
	}

	public static IExpr getFile(File file, EvalEngine engine) {
		boolean packageMode = engine.isPackageMode();
		try {
			engine.setPackageMode(true);
			String str = Files.asCharSource(file, Charset.defaultCharset()).read();
			return Get.loadPackage(engine, str);
		} catch (IOException e) {
			if (Config.SHOW_STACKTRACE) {
				e.printStackTrace();
			}
			engine.printMessage("Get exception: " + e.getMessage());
		} finally {
			engine.setPackageMode(packageMode);
		}
		return F.Null;
	}

	public static void initialize() {
		Initializer.init();
	}

	private PatternMatching() {

	}
}
