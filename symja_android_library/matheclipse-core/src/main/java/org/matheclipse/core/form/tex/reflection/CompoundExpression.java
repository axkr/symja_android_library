package org.matheclipse.core.form.tex.reflection;

import org.matheclipse.core.form.tex.AbstractOperator;
import org.matheclipse.parser.client.operator.ASTNodeFactory;

public class CompoundExpression extends AbstractOperator {

	public CompoundExpression() {
		super(ASTNodeFactory.MMA_STYLE_FACTORY.get("CompoundExpression").getPrecedence(), ", ");
	}

}
