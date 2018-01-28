package org.matheclipse.core.form.tex;

import org.matheclipse.core.interfaces.IAST;

public class TeXFunction extends AbstractConverter {

  String fFunctionName;

  public TeXFunction(final TeXFormFactory factory, final String functionName) {
    super(factory);
    fFunctionName = functionName;
  }

  /** {@inheritDoc} */
  @Override
public boolean convert(final StringBuilder buf, final IAST f, final int precedence) {
  	buf.append('\\');
  	buf.append(fFunctionName);
  	buf.append('(');
    for (int i = 1; i < f.size(); i++) {
			fFactory.convert(buf, f.get(i), 0);
      if (i < f.argSize()) {
        buf.append(',');
      }
    }
    buf.append(')');
    return true;
  }
}