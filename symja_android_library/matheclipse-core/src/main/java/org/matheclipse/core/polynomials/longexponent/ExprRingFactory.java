package org.matheclipse.core.polynomials.longexponent;

import java.io.IOException;
import java.io.Reader;
import java.math.BigInteger;
import java.util.List;
import java.util.Random;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;
import com.google.common.io.CharStreams;
import edu.jas.structure.RingFactory;

/** Ring factory class for IExpr with configurable field properties. */
public class ExprRingFactory implements RingFactory<IExpr> {

  private static final long serialVersionUID = -6146597389011632638L;

  /** Default ring factory (not a field). */
  public static final ExprRingFactory CONST = new ExprRingFactory(false);

  /** Ring factory configured to act as a field. */
  public static final ExprRingFactory CONST_FIELD = new ExprRingFactory(true);

  private final boolean isField;

  /**
   * Default constructor, creates a non-field ring factory.
   */
  public ExprRingFactory() {
    this(false);
  }

  /**
   * Constructor with explicit field configuration.
   * 
   * @param isField true if this factory should represent a field
   */
  public ExprRingFactory(boolean isField) {
    this.isField = isField;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isField() {
    return this.isField;
  }

  /** {@inheritDoc} */
  @Override
  public java.math.BigInteger characteristic() {
    return java.math.BigInteger.ZERO;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr fromInteger(long a) {
    return F.ZZ(a);
  }

  /** {@inheritDoc} */
  public static IExpr valueOf(long a) {
    return F.ZZ(a);
  }

  /** {@inheritDoc} */
  @Override
  public IExpr getZERO() {
    return F.C0;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr copy(IExpr c) {
    return c.copy();
  }

  /** {@inheritDoc} */
  @Override
  public IExpr fromInteger(BigInteger a) {
    return F.ZZ(a);
  }

  /** {@inheritDoc} */
  @Override
  public List<IExpr> generators() {
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isFinite() {
    return false;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr parse(String s) {
    return EvalEngine.get().parse(s);
  }

  /** {@inheritDoc} */
  @Override
  public IExpr parse(Reader r) {
    String s;
    try {
      s = CharStreams.toString(r);
      return EvalEngine.get().parse(s);
    } catch (IOException e) {
      Errors.printMessage(S.List, "error", F.List("IOException in ExprRingFactory#parse()."));
    }
    return S.Undefined;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr random(int n) {
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr random(int n, Random random) {
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public String toScript() {
    return "ExprRingFactory(" + isField + ")";
  }

  /** {@inheritDoc} */
  @Override
  public IExpr getONE() {
    return F.C1;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isAssociative() {
    return true;
  }

  /** {@inheritDoc} */
  @Override
  public boolean isCommutative() {
    return true;
  }
}
