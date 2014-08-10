package org.matheclipse.core.form.mathml.reflection;

import org.matheclipse.core.form.mathml.AbstractOperator;
import org.matheclipse.parser.client.operator.ASTNodeFactory;

public class Equal extends AbstractOperator {

	public Equal() {
		super(ASTNodeFactory.MMA_STYLE_FACTORY.get("Equal").getPrecedence(), "=");
	}

}
