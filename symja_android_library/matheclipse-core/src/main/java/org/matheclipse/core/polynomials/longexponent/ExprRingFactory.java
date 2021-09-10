package org.matheclipse.core.polynomials.longexponent;

import java.io.IOException;
import java.io.Reader;
import java.math.BigInteger;
import java.util.List;
import java.util.Random;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;
import com.google.common.io.CharStreams;
import edu.jas.structure.RingFactory;

/** Singleton ring factory class. */
public class ExprRingFactory implements RingFactory<IExpr> {
  private static final Logger LOGGER = LogManager.getLogger();

  /** */
  private static final long serialVersionUID = -6146597389011632638L;

  public static final ExprRingFactory CONST = new ExprRingFactory();

  private ExprRingFactory() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  public boolean isField() {
    return false;
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
    // TODO Auto-generated method stub
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
      LOGGER.error("ExprRingFactory.parse() failed", e);
    }
    return S.Undefined;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr random(int n) {
    // TODO Auto-generated method stub
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public IExpr random(int n, Random random) {
    // TODO Auto-generated method stub
    return null;
  }

  /** {@inheritDoc} */
  @Override
  public String toScript() {
    return "ExprRingFactory";
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
