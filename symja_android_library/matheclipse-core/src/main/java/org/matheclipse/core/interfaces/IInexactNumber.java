package org.matheclipse.core.interfaces;

public interface IInexactNumber extends INumber {

  public IInexactNumber plus(final IInexactNumber that);

  public IInexactNumber times(final IInexactNumber that);

  @Override
  public IInexactNumber acos();

  @Override
  public IInexactNumber asin();

  @Override
  public IInexactNumber atan();

  @Override
  public IInexactNumber cos();

  @Override
  public IInexactNumber sin();

  @Override
  public IInexactNumber tan();

  @Override
  public IInexactNumber cosh();

  @Override
  public IInexactNumber reciprocal();

  @Override
  public IInexactNumber sinh();

  @Override
  public IInexactNumber tanh();

  @Override
  public IInexactNumber log();
}
