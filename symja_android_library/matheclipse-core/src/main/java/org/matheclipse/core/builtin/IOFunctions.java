package org.matheclipse.core.builtin;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractCoreFunctionEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.AST2;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMatcher;
import org.matheclipse.parser.client.FEConfig;
import org.matheclipse.parser.trie.SuggestTree;
import org.matheclipse.parser.trie.SuggestTree.Node;

public class IOFunctions {

	/**
	 * 
	 * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation in static
	 * initializer</a>
	 */
	private static class Initializer {

		private static void init() {
			// S.General.setEvaluator(new General());
			S.Echo.setEvaluator(new Echo());
			S.EchoFunction.setEvaluator(new EchoFunction());
			S.Message.setEvaluator(new Message());
			S.Names.setEvaluator(new Names());
			S.Print.setEvaluator(new Print());
			S.Short.setEvaluator(new Short());
			S.StyleForm.setEvaluator(new StyleForm());
			for (int i = 0; i < MESSAGES.length; i += 2) {
				S.General.putMessage(IPatternMatcher.SET, MESSAGES[i], F.stringx(MESSAGES[i + 1]));
			}
		}
	}

	private static class Echo extends Print {

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
			OutputFormFactory out = OutputFormFactory.get(engine.isRelaxedSyntax());
			boolean[] convert = new boolean[] { true };
			IExpr arg1 = ast.arg1();
			IExpr result = engine.evaluate(arg1);
			if (ast.argSize() >= 2) {
				IExpr arg2 = engine.evaluate(ast.arg2());
				printExpression(arg2, out, buf, convert, engine);
				if (ast.isAST3()) {
					IExpr arg3 = engine.evaluate(F.unaryAST1(ast.arg3(), arg1));
					printExpression(arg3, out, buf, convert, engine);
				} else {
					printExpression(result, out, buf, convert, engine);
				}
			} else {
				printExpression(result, out, buf, convert, engine);
			}
			stream.println(buf.toString());
			return result;
		}

		public int[] expectedArgSize(IAST ast) {
			return IOFunctions.ARGS_1_3;
		}
	}

	private final static class EchoFunction extends Print {
		@Override
		public IExpr evaluate(IAST ast, EvalEngine engine) {

			if (ast.isAST1() && ast.head().isAST()) {
				final int size = ast.head().size();
				switch (size) {
				case 1:
					return F.unaryAST1(S.Echo, ast.arg1());
				case 2:
					return echo(ast.arg1(), ast.head().first(), engine);
				case 3:
					return F.ternaryAST3(S.Echo, ast.arg1(), ast.head().first(), ast.head().second());
				default:
				}
			}
			return F.NIL;
		}

		private static IExpr echo(final IExpr arg1, IExpr headFirst, EvalEngine engine) {
			final PrintStream s = engine.getOutPrintStream();
			final PrintStream stream;
			if (s == null) {
				stream = System.out;
			} else {
				stream = s;
			}
			final StringBuilder buf = new StringBuilder();
			OutputFormFactory out = OutputFormFactory.get(engine.isRelaxedSyntax());
			boolean[] convert = new boolean[] { true };
			IExpr result = engine.evaluate(arg1);
			IExpr arg3 = engine.evaluate(F.unaryAST1(headFirst, arg1));
			printExpression(arg3, out, buf, convert, engine);
			stream.println(buf.toString());
			return result;
		}

		public int[] expectedArgSize(IAST ast) {
			return IOFunctions.ARGS_0_2;
		}

	}

	private static class Message extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.size() > 1) {
				if (ast.arg1().isString()) {
					String message = ast.arg1().toString();
					for (int i = 2; i < ast.size(); i++) {
						message = message.replaceAll("`" + (i - 1) + "`", ast.get(i).toString());
					}
					return F.stringx(": " + message);
				}
				if (ast.arg1().isAST(S.MessageName, 3)) {
					IAST messageName = (IAST) ast.arg1();
					String messageShortcut = messageName.arg2().toString();
					if (messageName.arg1().isSymbol()) {
						IExpr temp = message((ISymbol) messageName.arg1(), messageShortcut, ast);
						if (temp.isPresent()) {
							return temp;
						}
					}
					return message(S.General, messageShortcut, ast);
				}
			}
			return F.NIL;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDFIRST);
		}

	}

	private static class Short extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			return F.stringx(shorten(ast.arg1()));
		}

		public int[] expectedArgSize(IAST ast) {
			return IOFunctions.ARGS_1_1;
		}

	}

	private static class StyleForm extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.head() == S.StyleForm) {
				return ast.apply(S.Style);
			}
			return F.NIL;
		}

	}

	private final static class Names extends AbstractFunctionEvaluator {
		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.isAST0()) {
				return getAllNames();
			}

			if (ast.arg1() instanceof IStringX) {
				return getNamesByPrefix(ast.arg1().toString());
			}
			return F.NIL;
		}

		public int[] expectedArgSize(IAST ast) {
			return IOFunctions.ARGS_0_1;
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
			OutputFormFactory out = OutputFormFactory.get(engine.isRelaxedSyntax());
			boolean[] convert = new boolean[] { true };
			ast.forEach(x -> {
				IExpr temp = engine.evaluate(x);
				printExpression(temp, out, buf, convert, engine);
			});
			if (!convert[0]) {
				stream.println("ERROR-IN-OUTPUTFORM");
				return F.Null;
			}
			stream.println(buf.toString());
			return F.Null;
		}

		protected static void printExpression(IExpr x, OutputFormFactory out, final StringBuilder buf,
				boolean[] convert, EvalEngine engine) {
			if (x instanceof IStringX) {
				buf.append(x.toString());
			} else {
				if (x.isASTSizeGE(S.Style, 2)) {
					printExpression(x.first(), out, buf, convert, engine);
				} else if (convert[0] && !out.convert(buf, x)) {
					convert[0] = false;
				}
			}
		}

	}

	public static final int[] ARGS_0_0 = new int[] { 0, 0 };

	public static final int[] ARGS_0_1 = new int[] { 0, 1 };

	public static final int[] ARGS_0_2 = new int[] { 0, 2 };

	public static final int[] ARGS_1_1 = new int[] { 1, 1 };

	public static final int[] ARGS_1_2 = new int[] { 1, 2 };

	public static final int[] ARGS_1_5 = new int[] { 1, 5 };

	public static final int[] ARGS_2_2 = new int[] { 2, 2 };

	public static final int[] ARGS_1_3 = new int[] { 1, 3 };

	public static final int[] ARGS_1_4 = new int[] { 1, 4 };

	public static final int[] ARGS_2_3 = new int[] { 2, 3 };

	public static final int[] ARGS_2_4 = new int[] { 2, 4 };

	public static final int[] ARGS_3_3 = new int[] { 3, 3 };

	public static final int[] ARGS_3_4 = new int[] { 3, 4 };

	public static final int[] ARGS_4_4 = new int[] { 4, 4 };

	public static final int[] ARGS_5_5 = new int[] { 5, 5 };

	public static final int[] ARGS_1_INFINITY = new int[] { 1, Integer.MAX_VALUE };

	public static final int[] ARGS_2_INFINITY = new int[] { 2, Integer.MAX_VALUE };

	public static final int[] ARGS_3_INFINITY = new int[] { 3, Integer.MAX_VALUE };

	private final static String[] MESSAGES = { //
			"argillegal", "illegal arguments: \"`1`\" in `2`", //
			"argb", "`1` called with `2` arguments; between `3` and `4` arguments are expected.", //
			"argct", "`1` called with `2` arguments.", //
			"argctu", "`1` called with 1 argument.", //
			"argr", "`1` called with 1 argument; `2` arguments are expected.", //
			"argrx", "`1` called with `2` arguments; `3` arguments are expected.", //
			"argx", "`1` called with `2` arguments; 1 argument is expected.", //
			"argt", "`1` called with `2` arguments; `3` or `4` arguments are expected.", //
			"argtu", "`1` called with 1 argument; `2` or `3` arguments are expected.", //
			"base", "Requested base `1` in `2` should be between 2 and `3`.", //
			"boxfmt", "`1` is not a box formatting type.", //
			"coef", "The first argument `1` of `2` should be a non-empty list of positive integers.", //
			"color", "`1` is not a valid color or gray-level specification.", //
			"compat", "`1` and `2` are incompatible units", //
			"cxt", "`1` is not a valid context name.", //
			"divz", "The argument `1` should be nonzero.", //
			"digit", "Digit at position `1` in `2` is too large to be used in base `3`.", //
			"dmval",
			"Input value `1` lies outside the range of data in the interpolating function. Extrapolation will be used.",
			"drop", "Cannot drop positions `1` through `2` in `3`.", //
			"eqf", "`1` is not a well-formed equation.", //
			"exact", "Argument `1` is not an exact number.", //
			"exdims", "The dimensions cannot be determined from the position `1`.", //
			"fftl", "Argument `1` is not a non-empty list or rectangular array of numeric quantities.", //
			"fpct", "To many parameters in `1` to be filled from `2`.", //
			"fnsym", "First argument in `1` is not a symbol or a string naming a symbol.", //
			"heads", "Heads `1` and `2` are expected to be the same.", //
			"ilsnn", "Single or list of non-negative integers expected at position `1`.", //
			"incpt", "incompatible elements in `1` cannot be joined.", //
			"indet", "Indeterminate expression `1` encountered.", //
			"infy", "Infinite expression `1` encountered.", //
			"innf", "Non-negative integer or Infinity expected at position `1` in `2`.", //
			"int", "Integer expected at position `2` in `1`.", //
			"intjava", "Java int value greater equal `1` expected instead of `2`.", //
			"intlevel", "Level specification value greater equal `1` expected instead of `2`.", //
			"intp", "Positive integer expected.", //
			"intnn", "Non-negative integer expected.", //
			"intnm", "Non-negative machine-sized integer expected at position `2` in `1`.", //
			"intm", "Machine-sized integer expected at position `2` in `1`.", //
			"intpm", "Positive machine-sized integer expected at position `2` in `1`.", //
			"intrange", "Integer expected in range `1` to `2`.", //
			"invdt", "The argument is not a rule or a list of rules.", //
			"invrl", "The argument `1` is not a valid Association or list of rules.", //
			"iterb", "Iterator does not have appropriate bounds.", //
			"itform", "Argument `1` at position `2` does not have the correct form for an iterator.", //
			"itlim", "Iteration limit of `1` exceeded for `2`.", //
			"itlimpartial", "Iteration limit of `1` exceeded. Returning partial results.", //
			"itendless", "Endless iteration detected in `1` in evaluation loop.", //
			"ivar", "`1` is not a valid variable.", //
			"lend",
			"The argument at position `1` in `2` should be a vector of unsigned byte values or a Base64 encoded string.", //
			"level", "Level specification `1` is not of the form n, {n}, or {m, n}.", //
			"list", "List expected at position `1` in `2`.", //
			"listofbigints", "List of Java BigInteger numbers expected in `1`.", //
			"listofints", "List of Java int numbers expected in `1`.", //
			"listoflongs", "List of Java long numbers expected in `1`.", //
			"locked", "Symbol `1` is locked.", //
			"lvlist", "Local variable specification `1` is not a List.", //
			"lvws", "Variable `1` in local variable specification `2` requires assigning a value", //
			"lvset",
			"Local variable specification `1` contains `2`, which is an assignment to `3`; only assignments to symbols are allowed.", //
			"matrix", "Argument `1` at position `2` is not a non-empty rectangular matrix.", //
			"matsq", "Argument `1` at position `2` is not a non-empty square matrix.", //
			"nil", "unexpected NIL expression encountered.", //
			"noneg", "Surd is not defined for even roots of negative values.", //
			"noopen", "Cannot open `1`.", //
			"nonopt",
			"Options expected (instead of `1`) beyond position `2` in `3`. An option must be a rule or a list of rules.", //
			"nord", "Invalid comparison with `1` attempted.", //
			"normal", "Nonatomic expression expected at position `1` in `2`.", //
			"notent", "`2` is not a known entity, class, or tag for `1`.", //
			"nquan", "The Quantile specification `1` should be a number between `2` and `3`.", "nvld",
			"The expression `1` is not a valid interval.", //
			"notunicode",
			"A character unicode, which should be a non-negative integer less than 1114112, is expected at position `2` in `1`.", //
			"noval", "Symbol `1` in part assignment does not have an immediate value.", //
			"nsmet", "This system cannot be solved with the methods available to `1`", //
			"openx", "`1` is not open.", //
			"optb", "Optional object `1` in `2` is not a single blank.", //
			"optnf", "Option name `2` not found in defaults for `1`", //
			"optx", "Unknown option `1` in `2`.", //
			"ovfl", "Overflow occurred in computation.", //
			"padlevel", "The padding specification `1` involves `2` levels; the list `3` has only `4` level.", //
			"partd", "Part specification `1` is longer than depth of object.", //
			"partw", "Part `1` of `2` does not exist.", //
			"pilist",
			"The arguments to `1` must be two lists of integers of identical length, with the second list only containing positive integers.", //
			"plld", "Endpoints in `1` must be distinct machine-size real numbers.", //
			"plln", "Limiting value `1` in `2` is not a machine-size real number.", //
			"pspec", "Part specification `1` is neither an integer nor a list of integer.", //
			"poly", "`1` is not a polynomial.", //
			"polynomial", "Polynomial expected at position `1` in `2`.", //
			"posr", "The left hand side of `2` in `1` doesn't match an int-array of depth `3`.", //
			"pkspec1", "The expression `1` cannot be used as a part specification.", //
			"precsm", "Requested precision `1` is smaller than `2`.", //
			"precgt", "Requested precision `1` is greater than `2`.", //
			"range", "Range specification in `1` does not have appropriate bounds.", //
			"reclim2", "Recursion depth of `1` exceeded during evaluation of `2`.", //
			"rectt", "Rectangular array expected at position `1` in `2`.", //
			"rvalue", "`1` is not a variable with a value, so its value cannot be changed.", //
			"rubiendless", "Endless iteration detected in `1` for Rubi pattern-matching rules.", //
			"seqs", "Sequence specification expected, but got `1`.", //
			"setp", "Part assignment to `1` could not be made", //
			"setraw", "Cannot assign to raw object `1`.", //
			"setps", "`1` in the part assignment is not a symbol.", //
			"sing", "Matrix `1` is singular.", //
			"span", "`1` is not a valid Span specification.", //
			"stream", "`1` is not string, InputStream[], or OutputStream[]", //
			"string", "String expected at position `1` in `2`.", //
			"sym", "Argument `1` at position `2` is expected to be a symbol.", //
			"tdlen", "Objects of unequal length in `1` cannot be combined.", //
			"tag", "Rule for `1` can only be attached to `2`.", //
			"take", "Cannot take positions `1` through `2` in `3`.", //
			"toggle", "ToggleFeature `1` is disabled.", //
			"unsupported", "`1` currently not supported in `2`.", //
			"usraw", "Cannot unset object `1`.", //
			"vloc", "The variable `1` cannot be localized so that it can be assigned to numerical values.", //
			"vpow2", "Argument `1` is restricted to vectors with a length of power of 2.", //
			"vrule", "Cannot set `1` to `2`, which is not a valid list of replacement rules.", //
			"write", "Tag `1` in `2` is Protected.", //
			"wrsym", "Symbol `1` is Protected.", //
			"ucdec", "An invalid unicode sequence was encountered and ignored." //
	};

	public static void initialize() {
		Initializer.init();
	}

	public static IExpr message(ISymbol symbol, String messageShortcut, final IAST list) {
		IExpr temp = symbol.evalMessage(messageShortcut);
		String message = null;
		if (temp.isPresent()) {
			message = temp.toString();
		} else {
			temp = F.General.evalMessage(messageShortcut);
			if (temp.isPresent()) {
				message = temp.toString();
			}
		}
		if (message != null) {
			message = rawMessage(list, message);
			return F.stringx(symbol.toString() + ": " + message);
		}
		return F.NIL;
	}

	public static IExpr printArgMessage(IAST ast, int[] expected, EvalEngine engine) {
		final ISymbol topHead = ast.topHead();
		int argSize = ast.argSize();
		if (expected[0] == expected[1]) {
			if (expected[0] == 1) {
				return printMessage(topHead, "argx", F.List(topHead, F.ZZ(argSize), F.ZZ(expected[0])), engine);
			}
			if (argSize == 1) {
				return printMessage(topHead, "argr", F.List(topHead, F.ZZ(expected[0])), engine);
			}
			return printMessage(topHead, "argrx", F.List(topHead, F.ZZ(argSize), F.ZZ(expected[0])), engine);
		}
		return printMessage(topHead, "argt", F.List(topHead, F.ZZ(argSize), F.ZZ(expected[0]), F.ZZ(expected[1])),
				engine);
	}

	/**
	 * 
	 * @param symbol
	 * @param messageShortcut
	 *            the message shortcut defined in <code>MESSAGES</code> array
	 * @param listOfArgs
	 *            a list of arguments which should be inserted into the message shortcuts placeholder
	 * @param engine
	 * @return always <code>F.NIL</code>
	 */
	public static IAST printMessage(ISymbol symbol, String messageShortcut, final IAST listOfArgs, EvalEngine engine) {
		IExpr temp = symbol.evalMessage(messageShortcut);
		String message = null;
		if (temp.isPresent()) {
			message = temp.toString();
		} else {
			temp = F.General.evalMessage(messageShortcut);
			if (temp.isPresent()) {
				message = temp.toString();
			}
		}
		if (message == null) {
			message = "Undefined message shortcut: " + messageShortcut;
			engine.setMessageShortcut(messageShortcut);
			engine.printMessage(symbol.toString() + ": " + message);
		} else {
			for (int i = 1; i < listOfArgs.size(); i++) {
				message = StringUtils.replace(message, "`" + (i) + "`", shorten(listOfArgs.get(i)));
			}
			engine.setMessageShortcut(messageShortcut);
			engine.printMessage(symbol.toString() + ": " + message);
		}
		return F.NIL;
	}

	public static String getMessage(String messageShortcut, final IAST listOfArgs) {
		return getMessage(messageShortcut, listOfArgs, EvalEngine.get());
	}

	public static String getMessage(String messageShortcut, final IAST listOfArgs, EvalEngine engine) {
		IExpr temp = F.General.evalMessage(messageShortcut);
		String message = null;
		if (temp.isPresent()) {
			message = temp.toString();
		}
		if (message == null) {
			message = "Undefined message shortcut: " + messageShortcut;
			engine.setMessageShortcut(messageShortcut);
			return message;
		}
		for (int i = 1; i < listOfArgs.size(); i++) {
			message = StringUtils.replace(message, "`" + (i) + "`", shorten(listOfArgs.get(i)));
		}
		engine.setMessageShortcut(messageShortcut);
		return message;
	}

	public static IAST printMessage(ISymbol symbol, Exception ex, EvalEngine engine) {
		String message = ex.getMessage();
		if (message != null) {
			engine.printMessage(symbol.toString() + ": " + message);
		} else {
			engine.printMessage(symbol.toString() + ": " + ex.getClass().toString());
		}

		return F.NIL;
	}

	private static String rawMessage(final IAST list, String message) {
		for (int i = 2; i < list.size(); i++) {
			message = StringUtils.replace(message, "`" + (i - 1) + "`", shorten(list.get(i)));
		}
		return message;
	}

	/**
	 * Shorten the output string generated from <code>expr</code> to a maximum length of <code>80</code> characters.
	 * Print <<SHORT>> as substitute of the middle of the expression if necessary.
	 * 
	 * @param expr
	 * @return
	 */
	public static String shorten(IExpr expr) {
		return shorten(expr, 80);
	}

	/**
	 * Shorten the output string generated from <code>expr</code> to a maximum length of <code>maximuLength</code>
	 * characters. Print <<SHORT>> as substitute of the middle of the expression if necessary.
	 * 
	 * @param expr
	 * @param maximuLength
	 *            the maximum length of the result string.
	 * @return
	 */
	public static String shorten(IExpr expr, int maximuLength) {
		String str = expr.toString();
		if (str.length() > maximuLength) {
			StringBuilder buf = new StringBuilder(maximuLength);
			int halfLength = (maximuLength / 2) - 14;
			buf.append(str.substring(0, halfLength));
			buf.append("<<SHORT>>");
			buf.append(str.substring(str.length() - halfLength));
			return buf.toString();
		}
		return str;
	}

	public static IAST getNamesByPrefix(String name) {

		if (name.length() == 0) {
			return F.List();
		}
		boolean exact = true;
		if (name.charAt(name.length() - 1) == '*') {
			name = name.substring(0, name.length() - 1);
			if (name.length() == 0) {
				return getAllNames();
			}
			exact = false;
		}
		SuggestTree suggestTree = AST2Expr.getSuggestTree();
		name = FEConfig.PARSER_USE_LOWERCASE_SYMBOLS ? name.toLowerCase() : name;
		Node n = suggestTree.getAutocompleteSuggestions(name);
		if (n != null) {
			IASTAppendable list = F.ListAlloc(n.listLength());
			for (int i = 0; i < n.listLength(); i++) {
				if (exact) {
					if (name.equals(n.getSuggestion(i).getTerm())) {
						list.append(F.$s(n.getSuggestion(i).getTerm()));
					}
				} else {
					list.append(F.$s(n.getSuggestion(i).getTerm()));
				}
			}
			return list;
		}
		return F.List();
	}

	public static List<String> getAutoCompletionList(String namePrefix) {
		List<String> list = new ArrayList<String>();
		if (namePrefix.length() == 0) {
			return list;
		}
		SuggestTree suggestTree = AST2Expr.getSuggestTree();
		namePrefix = FEConfig.PARSER_USE_LOWERCASE_SYMBOLS ? namePrefix.toLowerCase() : namePrefix;
		Node n = suggestTree.getAutocompleteSuggestions(namePrefix);
		if (n != null) {
			for (int i = 0; i < n.listLength(); i++) {
				list.add(n.getSuggestion(i).getTerm());
			}
		}
		return list;
	}

	public static IAST getAllNames() {
		int size = AST2Expr.FUNCTION_STRINGS.length;
		IASTAppendable list = F.ListAlloc(size);
		return list.appendArgs(0, size, i -> F.$s(AST2Expr.FUNCTION_STRINGS[i]));
	}

	private IOFunctions() {

	}

}
