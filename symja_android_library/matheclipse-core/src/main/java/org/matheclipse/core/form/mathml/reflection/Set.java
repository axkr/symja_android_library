package org.matheclipse.core.form.mathml.reflection;

import org.matheclipse.core.form.mathml.AbstractOperator;
import org.matheclipse.parser.client.operator.ASTNodeFactory;

public class Set extends AbstractOperator {

	public Set() {
		super(ASTNodeFactory.MMA_STYLE_FACTORY.get("Set").getPrecedence(), "=");
	}

}
