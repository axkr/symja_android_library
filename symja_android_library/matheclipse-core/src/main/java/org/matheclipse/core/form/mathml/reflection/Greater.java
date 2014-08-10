package org.matheclipse.core.form.mathml.reflection;

import org.matheclipse.core.form.mathml.AbstractOperator;
import org.matheclipse.parser.client.operator.ASTNodeFactory;

/**
 * Operator function conversions
 */
public class Greater extends AbstractOperator {

  public Greater() {
    super(ASTNodeFactory.MMA_STYLE_FACTORY.get("Greater").getPrecedence(), "&gt;");
  }

}
