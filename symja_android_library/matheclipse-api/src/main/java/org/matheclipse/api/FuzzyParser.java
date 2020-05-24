package org.matheclipse.api;

import java.util.Locale;

import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.AST2Expr;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.parser.ExprParser;
import org.matheclipse.core.parser.ExprParserFactory;
import org.matheclipse.parser.client.SyntaxError;
import org.matheclipse.parser.client.ast.IParserFactory;

public class FuzzyParser extends ExprParser {
	public FuzzyParser(final EvalEngine engine ) throws SyntaxError {
		super(engine, ExprParserFactory.RELAXED_STYLE_FACTORY, true, false, false);
	}
	
	protected IExpr convertSymbolOnInput(final String nodeStr, final String context) {
		String lowercaseStr = nodeStr.toLowerCase(Locale.ENGLISH);
		if (lowercaseStr.equals("set")) {
			// special - convert on input
			return F.Equal;
		} 
		return super.convertSymbolOnInput(nodeStr, context);
	}
}
