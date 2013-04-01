package org.matheclipse.core.form.tex.reflection;

import org.matheclipse.core.form.tex.AbstractOperator;
import org.matheclipse.parser.client.operator.ASTNodeFactory;

/**
 * Operator function conversions
 */
public class GreaterEqual extends AbstractOperator {

  public GreaterEqual() {
  	// &geq; &#02265;
  	super(ASTNodeFactory.MMA_STYLE_FACTORY.get("GreaterEqual").getPrecedence(), "\\geq ");
  }

}
