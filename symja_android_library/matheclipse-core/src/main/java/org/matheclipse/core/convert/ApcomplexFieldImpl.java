package org.matheclipse.core.convert;

import org.hipparchus.Field;

public class ApcomplexFieldImpl implements Field<ApcomplexField> {

  public static ApcomplexFieldImpl getInstance() {
    return new ApcomplexFieldImpl();
  }


  private ApcomplexFieldImpl() {}

  @Override
  public ApcomplexField getOne() {
    return ApcomplexField.ONE;
  }

  @Override
  public Class<ApcomplexField> getRuntimeClass() {
    return ApcomplexField.class;
  }

  @Override
  public ApcomplexField getZero() {
    return ApcomplexField.ZERO;
  }
}

