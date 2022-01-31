package org.matheclipse.core.expression;

public class BuiltInRubi extends BuiltInDummy {
  private static final long serialVersionUID = -7659044186079472277L;

  public BuiltInRubi(final String symbolName) {
    super(symbolName);
  }

  @Override
  public final Context getContext() {
    return Context.RUBI;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isProtected() {
    return true;
  }
}
