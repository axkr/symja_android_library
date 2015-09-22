package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.List;

import java.io.UnsupportedEncodingException;
import java.util.List;

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
			return null;
		}
		
		IAST resultList = List();
		resultList = (IAST) toCharacterCode(ast.arg1().toString(), "UTF-8", resultList);
		return resultList;
	}

	@Override
	public void setUp(final ISymbol symbol) {
		symbol.setAttributes(ISymbol.LISTABLE);
	}

	public static List<IExpr> toCharacterCode(final String unicodeInput, final String inputEncoding, final List<IExpr> list) {
		try {
			final String utf8String = new String(unicodeInput.getBytes(inputEncoding), "UTF-8");
			int characterCode;
			for (int i = 0; i < utf8String.length(); i++) {
				characterCode = utf8String.charAt(i);
				list.add(F.integer(characterCode));
			}
			return list;
		} catch (final UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
}
