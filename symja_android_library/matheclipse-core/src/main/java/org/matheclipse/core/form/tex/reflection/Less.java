package org.matheclipse.core.form.tex.reflection;

import org.matheclipse.core.form.tex.AbstractOperator;
import org.matheclipse.parser.client.operator.ASTNodeFactory;

public class Less extends AbstractOperator {

	public Less() {
		super(ASTNodeFactory.MMA_STYLE_FACTORY.get("Less").getPrecedence(), "<");
	}

}
