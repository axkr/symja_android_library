package org.matheclipse.core.form.mathml.reflection;

import org.matheclipse.core.form.mathml.AbstractOperator;
import org.matheclipse.parser.client.operator.ASTNodeFactory;

public class Statement extends AbstractOperator {

	public Statement() {
		super(ASTNodeFactory.MMA_STYLE_FACTORY.get("Statement").getPrecedence(), ";");
	}

}
