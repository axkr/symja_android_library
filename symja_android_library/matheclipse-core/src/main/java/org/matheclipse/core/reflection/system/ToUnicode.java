package org.matheclipse.core.reflection.system;

import java.io.UnsupportedEncodingException;

import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.StringX;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;

public class ToUnicode extends AbstractFunctionEvaluator {
	private final static String UNICODE_PREFIX = "\\u";

	public ToUnicode() {
	}

	@Override
	public IExpr evaluate(final IAST ast) {
		Validate.checkSize(ast, 2);
		if (!(ast.arg1() instanceof IStringX)) {
			return null;
		}
		
		return StringX.valueOf(toUnicodeString(ast.arg1().toString(), "UTF-8"));
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.LISTABLE);
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
