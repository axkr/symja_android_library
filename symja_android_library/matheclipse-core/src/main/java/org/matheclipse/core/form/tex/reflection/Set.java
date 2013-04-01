package org.matheclipse.core.form.tex.reflection;

import org.matheclipse.core.form.tex.AbstractOperator;
import org.matheclipse.parser.client.operator.ASTNodeFactory;

public class Set extends AbstractOperator {

	public Set() {
		super(ASTNodeFactory.MMA_STYLE_FACTORY.get("Set").getPrecedence(), " = ");
	}

}
