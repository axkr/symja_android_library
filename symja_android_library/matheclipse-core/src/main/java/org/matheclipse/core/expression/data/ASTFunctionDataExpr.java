package org.matheclipse.core.expression.data;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IBuiltInSymbol;

/**
 * Base class of the function objects <code>head(data)</code> which wrap an {@link IAST}. The data
 * is serialized as the {@link IAST} itself; the head is fixed by the subclass constructor.
 */
public abstract class ASTFunctionDataExpr extends DataExpr<IAST> implements Externalizable {

  private static final long serialVersionUID = 1L;

  protected ASTFunctionDataExpr(final IBuiltInSymbol head, final IAST data) {
    super(head, data);
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    this.fData = (IAST) in.readObject();
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeObject(this.fData);
  }

  @Override
  public String toString() {
    return fHead.toString() + "(" + fData + ")";
  }
}
