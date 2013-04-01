package org.matheclipse.core.form.tex.reflection;

import org.matheclipse.core.form.tex.AbstractOperator;
import org.matheclipse.parser.client.operator.ASTNodeFactory;

public class Rule extends AbstractOperator {

  public Rule() {
    super(ASTNodeFactory.MMA_STYLE_FACTORY.get("Rule").getPrecedence(), "-&gt;");
  }

}
