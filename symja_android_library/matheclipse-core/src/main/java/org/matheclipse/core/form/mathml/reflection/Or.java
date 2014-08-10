package org.matheclipse.core.form.mathml.reflection;

import org.matheclipse.core.form.mathml.AbstractOperator;
import org.matheclipse.parser.client.operator.ASTNodeFactory;

public class Or extends AbstractOperator {

	public Or() {
		// &or; &#x2228;
		super(ASTNodeFactory.MMA_STYLE_FACTORY.get("Or").getPrecedence(), "&#x2228;");
	}

}
