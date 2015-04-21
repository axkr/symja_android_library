package org.matheclipse.core.form.tex.reflection;

import org.matheclipse.core.form.tex.AbstractOperator;
import org.matheclipse.parser.client.operator.ASTNodeFactory;

/**
 * Operator function conversions
 */
public class ReplaceRepeated extends AbstractOperator {

	public ReplaceRepeated() {
		super(ASTNodeFactory.MMA_STYLE_FACTORY.get("ReplaceRepeated").getPrecedence(),
				"\\text{//.}\\,");
	}

}
