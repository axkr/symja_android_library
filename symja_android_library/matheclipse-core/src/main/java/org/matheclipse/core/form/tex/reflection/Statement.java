package org.matheclipse.core.form.tex.reflection;

import org.matheclipse.core.form.tex.AbstractOperator;
import org.matheclipse.parser.client.operator.ASTNodeFactory;

public class Statement extends AbstractOperator {

	public Statement() {
		super(ASTNodeFactory.MMA_STYLE_FACTORY.get("Statement").getPrecedence(), ", ");
	}

}
