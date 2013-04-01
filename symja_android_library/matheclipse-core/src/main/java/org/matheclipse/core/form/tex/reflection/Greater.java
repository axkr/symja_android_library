package org.matheclipse.core.form.tex.reflection;

import org.matheclipse.core.form.tex.AbstractOperator;
import org.matheclipse.parser.client.operator.ASTNodeFactory;

/**
 * Operator function conversions
 */
public class Greater extends AbstractOperator {

  public Greater() {
    super(ASTNodeFactory.MMA_STYLE_FACTORY.get("Greater").getPrecedence(), " > ");
  }

}
