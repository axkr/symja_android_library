package org.matheclipse.core.convert;

import org.hipparchus.Field;

public class ApfloatFieldImpl implements Field<ApfloatField> {

  public static ApfloatFieldImpl getInstance() {
    return new ApfloatFieldImpl();
  }


  private ApfloatFieldImpl() {}

  @Override
  public ApfloatField getOne() {
    return ApfloatField.ONE;
  }

  @Override
  public Class<ApfloatField> getRuntimeClass() {
    return ApfloatField.class;
  }

  @Override
  public ApfloatField getZero() {
    return ApfloatField.ZERO;
  }
}
