package org.matheclipse.core.form.tex.reflection;

import org.matheclipse.core.form.tex.AbstractConverter;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class Integrate extends AbstractConverter {

  public Integrate() {
  }

  /** {@inheritDoc} */
  public boolean convert(final StringBuffer buf, final IAST f, final int precedence) {
    if (f.size() == 3) {
      final IExpr arg2 = f.arg2();
      IAST list = null;
      if (arg2.isAST()) {
        list = (IAST) arg2;
      }
      if ((list != null) && (list.size() == 4) && list.isList()) {
      	buf.append("\\int_{");
        fFactory.convert(buf, list.arg2(), 0);
        buf.append("}^{");
        fFactory.convert(buf, list.arg3(), 0);
        buf.append('}');
      } else {
        list = null;
        buf.append("\\int ");
      }

      fFactory.convert(buf, f.arg1(), 0);
      buf.append("\\,d");
      if (list != null) {
        fFactory.convert(buf, list.arg1(), 0);
      } else {
        fFactory.convert(buf, f.arg2(), 0);
      }
      return true;
    }
    return false;
  }
}