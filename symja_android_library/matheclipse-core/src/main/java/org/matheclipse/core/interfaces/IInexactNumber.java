package org.matheclipse.core.interfaces;

import org.matheclipse.core.eval.EvalEngine;

/**
 * The IInexactNumber interface represents an inexact number in the system. It extends the INumber
 * interface, inheriting all of its methods. In addition to the methods inherited from INumber,
 * IInexactNumber also defines methods for arithmetic operations and trigonometric functions that
 * are specific to inexact numbers.
 */
public interface IInexactNumber extends INumber {

  @Override
  default IInexactNumber eval(EvalEngine engine) {
    return this;
  }

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
  public IInexactNumber exp();

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
  public IInexactNumber times(INumber that);

  @Override
  public IInexactNumber log();

  @Override
  public IInexactNumber barnesG();

  @Override
  public IInexactNumber logBarnesG();
}
