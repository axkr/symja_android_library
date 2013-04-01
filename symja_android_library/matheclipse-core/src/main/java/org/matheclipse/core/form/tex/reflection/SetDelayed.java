package org.matheclipse.core.form.tex.reflection;

import org.matheclipse.core.form.tex.AbstractOperator;
import org.matheclipse.parser.client.operator.ASTNodeFactory;

/**
 * Operator function conversions
 */
public class SetDelayed extends AbstractOperator {

	public SetDelayed() {
		super(ASTNodeFactory.MMA_STYLE_FACTORY.get("SetDelayed").getPrecedence(),
				":=");
	}

}
