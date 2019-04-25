package org.matheclipse.core.builtin;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMatcher;

public class IOFunctions {

	/**
	 * 
	 * See <a href="https://pangin.pro/posts/computation-in-static-initializer">Beware of computation in static
	 * initializer</a>
	 */
	private static class Initializer {

		private static void init() {
			// F.General.setEvaluator(new General());
			F.Message.setEvaluator(new Message());
			for (int i = 0; i < MESSAGES.length; i += 2) {
				F.General.putMessage(IPatternMatcher.SET, MESSAGES[i], F.stringx(MESSAGES[i + 1]));
			}
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
				if (ast.arg1().isAST(F.MessageName, 3)) {
					IAST messageName = (IAST) ast.arg1();
					String messageShortcut = messageName.arg2().toString();
					if (messageName.arg1().isSymbol()) {
						IExpr temp = message((ISymbol) messageName.arg1(), messageShortcut, ast);
						if (temp.isPresent()) {
							return temp;
						}
					}
					return message(F.General, messageShortcut, ast);
				}
			}
			return F.NIL;
		}

		@Override
		public void setUp(ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.HOLDFIRST);
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
			"color", "`1` is not a valid color or gray-level specification.", //
			"cxt", "`1` is not a valid context name.", //
			"divz", "The argument `1` should be nonzero.", //
			"digit", "Digit at position `1` in `2` is too large to be used in base `3`.", //
			"exact", "Argument `1` is not an exact number.", //
			"fnsym", "First argument in `1` is not a symbol or a string naming a symbol.", //
			"heads", "Heads `1` and `2` are expected to be the same.", //
			"ilsnn", "Single or list of non-negative integers expected at position `1`.", //
			"indet", "Indeterminate expression `1` encountered.", //
			"innf", "Non-negative integer or Infinity expected at position `1`.", //
			"int", "Integer expected.", //
			"intp", "Positive integer expected.", //
			"intnn", "Non-negative integer expected.", //
			"iterb", "Iterator does not have appropriate bounds.", //
			"ivar", "`1` is not a valid variable.", //
			"level", "Level specification `1` is not of the form n, {n}, or {m, n}.", //
			"locked", "Symbol `1` is locked.", //
			"matsq", "Argument `1` is not a non-empty square matrix.", //
			"noopen", "Cannot open `1`.", //
			"nord", "Invalid comparison with `1` attempted.", //
			"normal", "Nonatomic expression expected.", //
			"noval", "Symbol `1` in part assignment does not have an immediate value.", //
			"openx", "`1` is not open.", //
			"optb", "Optional object `1` in `2` is not a single blank.", //
			"ovfl", "Overflow occurred in computation.", //
			"partd", "Part specification is longer than depth of object.", //
			"partw", "Part `1` of `2` does not exist.", //
			"plld", "Endpoints in `1` must be distinct machine-size real numbers.", //
			"plln", "Limiting value `1` in `2` is not a machine-size real number.", //
			"pspec", "Part specification `1` is neither an integer nor a list of integer.", //
			"seqs", "Sequence specification expected, but got `1`.", //
			"setp", "Part assignment to `1` could not be made", //
			"setps", "`1` in the part assignment is not a symbol.", //
			"span", "`1` is not a valid Span specification.", //
			"stream", "`1` is not string, InputStream[], or OutputStream[]", //
			"string", "String expected.", //
			"sym", "Argument `1` at position `2` is expected to be a symbol.", //
			"tag", "Rule for `1` can only be attached to `2`.", //
			"take", "Cannot take positions `1` through `2` in `3`.", //
			"vrule", "Cannot set `1` to `2`, which is not a valid list of replacement rules.", //
			"write", "Tag `1` in `2` is Protected.", //
			"wrsym", "Symbol `1` is Protected.", //
			"ucdec", "An invalid unicode sequence was encountered and ignored." //
	};

	public static void initialize() {
		Initializer.init();
	}

	public static IExpr message(ISymbol symbol, String messageShortcut, final IAST ast) {
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
			message = rawMessage(ast, message);
			return F.stringx(symbol.toString() + ": " + message);
		}
		return F.NIL;
	}

	public static IExpr printArgMessage(IAST ast, int[] expected, EvalEngine engine) {
		final ISymbol topHead = ast.topHead();
		int argSize = ast.argSize();
		if (expected[0] == expected[1]) {
			if (expected[0] == 1) {
				printMessage(topHead, "argx", F.List(topHead, F.ZZ(argSize), F.ZZ(expected[0])), engine);
				return F.NIL;
			}
			if (argSize == 1) {
				printMessage(topHead, "argr", F.List(topHead, F.ZZ(expected[0])), engine);
				return F.NIL;
			}
			printMessage(topHead, "argrx", F.List(topHead, F.ZZ(argSize), F.ZZ(expected[0])), engine);
			return F.NIL;
		}
		printMessage(topHead, "argt", F.List(topHead, F.ZZ(argSize), F.ZZ(expected[0]), F.ZZ(expected[1])), engine);
		return F.NIL;
	}

	public static boolean printMessage(ISymbol symbol, String messageShortcut, final IAST ast, EvalEngine engine) {
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
			if (message != null) {
				for (int i = 1; i < ast.size(); i++) {
					message = message.replaceAll("`" + (i) + "`", ast.get(i).toString());
				}
				engine.printMessage(symbol.toString() + ": " + message);
				return true;
			}
		}
		return false;
	}

	private static String rawMessage(final IAST ast, String message) {
		for (int i = 2; i < ast.size(); i++) {
			message = message.replaceAll("`" + (i - 1) + "`", ast.get(i).toString());
		}
		return message;
	}

	private IOFunctions() {

	}

}
