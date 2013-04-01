package org.matheclipse.core.form.tex.reflection;

import org.matheclipse.core.form.tex.AbstractOperator;
import org.matheclipse.parser.client.operator.ASTNodeFactory;

public class LessEqual extends AbstractOperator {

	public LessEqual() {
		// &leq; &#02264;
		super(ASTNodeFactory.MMA_STYLE_FACTORY.get("LessEqual").getPrecedence(),
				"\\leq ");
	}

}
