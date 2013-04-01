package org.matheclipse.core.form.tex.reflection;

import org.matheclipse.core.form.tex.AbstractOperator;
import org.matheclipse.parser.client.operator.ASTNodeFactory;

public class RuleDelayed extends AbstractOperator {

	public RuleDelayed() {
		// &RuleDelayed; &#x29F4;
		super(ASTNodeFactory.MMA_STYLE_FACTORY.get("RuleDelayed").getPrecedence(),
				" := ");
	}

}
