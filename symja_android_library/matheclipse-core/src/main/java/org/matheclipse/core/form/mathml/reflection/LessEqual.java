package org.matheclipse.core.form.mathml.reflection;

import org.matheclipse.core.form.mathml.AbstractOperator;
import org.matheclipse.parser.client.operator.ASTNodeFactory;

public class LessEqual extends AbstractOperator {

	public LessEqual() {
		// &leq; &#02264;
		super(ASTNodeFactory.MMA_STYLE_FACTORY.get("LessEqual").getPrecedence(),
				"&#x2264;");
	}

}
