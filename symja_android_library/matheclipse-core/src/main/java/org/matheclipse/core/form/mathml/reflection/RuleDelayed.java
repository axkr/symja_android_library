package org.matheclipse.core.form.mathml.reflection;

import org.matheclipse.core.form.mathml.AbstractOperator;
import org.matheclipse.parser.client.operator.ASTNodeFactory;

public class RuleDelayed extends AbstractOperator {

	public RuleDelayed() {
		// &RuleDelayed; &#x29F4;
		super(ASTNodeFactory.MMA_STYLE_FACTORY.get("RuleDelayed").getPrecedence(),
				"&#x29F4;");
	}

}
