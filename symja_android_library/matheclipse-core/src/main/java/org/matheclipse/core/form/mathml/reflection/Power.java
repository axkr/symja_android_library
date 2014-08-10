package org.matheclipse.core.form.mathml.reflection;

import org.matheclipse.core.form.mathml.AbstractOperator;
import org.matheclipse.parser.client.operator.ASTNodeFactory;

public class Power extends AbstractOperator {

	public Power() {
		super(ASTNodeFactory.MMA_STYLE_FACTORY.get("Power").getPrecedence(), "msup",
				"");
	}

}
