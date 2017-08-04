
package org.matheclipse.core.builtin;

import static org.matheclipse.core.expression.F.List;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.StringX;
import org.matheclipse.core.form.output.OutputFormFactory;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;
import org.matheclipse.core.parser.ExprParser;

public final class StringFunctions {

	static {
		F.StringDrop.setEvaluator(new StringDrop());
		F.StringJoin.setEvaluator(new StringJoin());
		F.StringLength.setEvaluator(new StringLength());
		F.StringTake.setEvaluator(new StringTake());
		F.SyntaxLength.setEvaluator(new SyntaxLength());
		F.ToCharacterCode.setEvaluator(new ToCharacterCode());
		F.ToString.setEvaluator(new ToString());
		F.ToUnicode.setEvaluator(new ToUnicode());
	}

	private static class StringDrop extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);

			if (ast.arg1().isString()) {
				String s = ast.arg1().toString();
				final int n = Validate.checkIntType(ast, 2, Integer.MIN_VALUE);
				if (n >= 0) {
					return F.$str(s.substring(n, s.length()));
				} else {
					return F.$str(s.substring(0, s.length() + n));
				}
			}

			return F.NIL;
		}
	}

	private static class StringJoin extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkRange(ast, 3);

			StringBuffer buf = new StringBuffer();
			for (int i = 1; i < ast.size(); i++) {
				if (ast.get(i).isString()) {
					buf.append(ast.get(i).toString());
				} else {
					return F.NIL;
				}
			}
			return F.$str(buf.toString());

		}
	}

	private static class StringLength extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			if (ast.arg1().isString()) {
				return F.integer(ast.arg1().toString().length());
			}
			return F.NIL;
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}
	}

	private static class StringTake extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 3);

			if (ast.arg1().isString()) {
				String s = ast.arg1().toString();
				final int n = Validate.checkIntType(ast, 2, Integer.MIN_VALUE);
				if (n >= 0) {
					return F.$str(s.substring(0, n));
				} else {
					return F.$str(s.substring(s.length() + n, s.length()));
				}
			}

			return F.NIL;
		}
	}

	private static class SyntaxLength extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);
			if (!(ast.arg1() instanceof IStringX)) {
				return F.NIL;
			}

			final String str = ast.arg1().toString();
			return F.integer(ExprParser.syntaxLength(str));
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}

	}

	private static class ToCharacterCode extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);
			if (!(ast.arg1() instanceof IStringX)) {
				return F.NIL;
			}

			return toCharacterCode(ast.arg1().toString(), "UTF-8", List());
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}

		public static IAST toCharacterCode(final String unicodeInput, final String inputEncoding, final IAST list) {
			try {
				final String utf8String = new String(unicodeInput.getBytes(inputEncoding), "UTF-8");
				int characterCode;
				for (int i = 0; i < utf8String.length(); i++) {
					characterCode = utf8String.charAt(i);
					list.append(F.integer(characterCode));
				}
				return list;
			} catch (final UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return F.NIL;
		}
	}

	private static class ToString extends AbstractFunctionEvaluator {

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);

			return F.$str(outputForm(ast.arg1()));
		}

	}

	private static class ToUnicode extends AbstractFunctionEvaluator {
		private final static String UNICODE_PREFIX = "\\u";

		@Override
		public IExpr evaluate(final IAST ast, EvalEngine engine) {
			Validate.checkSize(ast, 2);
			if (!(ast.arg1() instanceof IStringX)) {
				return F.NIL;
			}

			return StringX.valueOf(toUnicodeString(ast.arg1().toString(), "UTF-8"));
		}

		@Override
		public void setUp(final ISymbol newSymbol) {
			newSymbol.setAttributes(ISymbol.LISTABLE);
		}

		public static String toUnicodeString(final String unicodeInput, final String inputEncoding) {
			final StringBuffer unicodeStringBuffer = new StringBuffer();
			String unicodeString = null;

			try {
				final String utf8String = new String(unicodeInput.getBytes(inputEncoding), "UTF-8");
				String hexValueString = null;
				int hexValueLength = 0;
				for (int i = 0; i < utf8String.length(); i++) {
					hexValueString = Integer.toHexString(utf8String.charAt(i));
					hexValueLength = hexValueString.length();
					if (hexValueLength < 4) {
						for (int j = 0; j < (4 - hexValueLength); j++) {
							hexValueString = "0" + hexValueString;
						}
					}
					unicodeStringBuffer.append(UNICODE_PREFIX);
					unicodeStringBuffer.append(hexValueString);
				}
				unicodeString = unicodeStringBuffer.toString();
			} catch (final UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return unicodeString;
		}
	}

	public static String outputForm(final IExpr expression) {
		try {
			StringBuilder buf = new StringBuilder();
			OutputFormFactory off = OutputFormFactory.get();
			off.setIgnoreNewLine(true);
			off.convert(buf, expression);
			return buf.toString();
		} catch (IOException e) {
			if (Config.SHOW_STACKTRACE) {
				e.printStackTrace();
			}
		}
		return null;
	}

	final static StringFunctions CONST = new StringFunctions();

	public static StringFunctions initialize() {
		return CONST;
	}

	private StringFunctions() {

	}

}
