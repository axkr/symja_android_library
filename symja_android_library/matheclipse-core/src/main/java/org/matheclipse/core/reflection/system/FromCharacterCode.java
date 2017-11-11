package org.matheclipse.core.reflection.system;

import java.io.UnsupportedEncodingException;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.StringX;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.ISymbol;

public class FromCharacterCode extends AbstractFunctionEvaluator {

	public FromCharacterCode() {
	}

	@Override
	public IExpr evaluate(final IAST ast, EvalEngine engine) {
		if (ast.size() != 2) {
			return F.NIL;
		}

		if (ast.arg1().isList()) {
			final IAST list = (IAST) ast.arg1();
			final StringBuilder buffer = new StringBuilder();
			char ch;
			for (int i = 1; i < list.size(); i++) {
				if (list.get(i).isInteger()) {
					ch = (char) Validate.checkIntType(list, i);
					buffer.append(ch);
				} else {
					return F.NIL;
				}
			}
			return StringX.valueOf(buffer);
		}
		if (ast.arg1().isInteger()) {
			final char ch = (char) Validate.checkIntType(ast, 1);
			return StringX.valueOf(ch);
		}

		return F.NIL;
	}

	@Override
	public void setUp(final ISymbol newSymbol) {
	}

	public static IAST fromCharcterCode(final String unicodeInput, final String inputEncoding, final IASTAppendable list) {
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
