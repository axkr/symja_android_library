package org.matheclipse.core.builtin;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.patternmatching.IPatternMatcher;

public class IOFunctions {

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

	static {
		// F.General.setEvaluator(new General());
		F.Message.setEvaluator(new Message());
		for (int i = 0; i < MESSAGES.length; i += 2) {
			F.General.putMessage(IPatternMatcher.SET, MESSAGES[i], F.stringx(MESSAGES[i + 1]));
		}
	}

	private static class Message extends AbstractEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			if (ast.size() > 1 && ast.arg1().isAST(F.MessageName, 3)) {
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
			return F.NIL;
		}

	}

	public static IExpr message(ISymbol symbol, String messageShortcut, final IAST ast) {
		IExpr temp = symbol.evalMessage(messageShortcut);
		if (temp.isPresent()) {
			String message = temp.toString();

			if (message != null) {
				for (int i = 2; i < ast.size(); i++) {
					message = message.replaceAll("`" + (i - 1) + "`", ast.get(i).toString());
				}
				return F.stringx(symbol.toString()+": " + message);
			}
		}
		return F.NIL;
	}

	private final static IOFunctions CONST = new IOFunctions();

	public static IOFunctions initialize() {
		return CONST;
	}

	private IOFunctions() {

	}

}
