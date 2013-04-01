package org.matheclipse.core.expression;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.FieldElement;

public class ExprField implements Field<ExprFieldElement> {
  public final static ExprField CONST = new ExprField();

  public final static ExprFieldElement ONE = new ExprFieldElement(F.C1);
  public final static ExprFieldElement ZERO = new ExprFieldElement(F.C0);

  @Override
  public ExprFieldElement getOne() {
    return ONE;
  }

//  @Override
//  public Class<? extends FieldElement<ExprFieldElement>> getRuntimeClass() {
//    return ExprFieldElement.class;
//  }

  @Override
  public ExprFieldElement getZero() {
    return ZERO;
  }

	@Override
	public Class<? extends FieldElement<ExprFieldElement>> getRuntimeClass() {
		return ExprFieldElement.class;
	}

}
