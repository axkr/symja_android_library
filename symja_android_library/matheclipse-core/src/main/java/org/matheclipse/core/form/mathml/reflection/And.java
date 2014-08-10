package org.matheclipse.core.form.mathml.reflection;

import org.matheclipse.core.form.mathml.AbstractOperator;
import org.matheclipse.parser.client.operator.ASTNodeFactory;

public class And extends AbstractOperator {

	public And() {
		// and 2227
		super(ASTNodeFactory.MMA_STYLE_FACTORY.get("And").getPrecedence(), "&#x2227;");
	}

}
