package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.List;

import java.io.UnsupportedEncodingException;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.exception.Validate;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;

public class ToCharacterCode extends AbstractFunctionEvaluator {

	public ToCharacterCode() {
	}

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
