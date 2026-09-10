package org.matheclipse.core.expression.data;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.io.link.LinkEntry;
import org.matheclipse.core.io.link.LinkRegistry;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <code>LinkObject["name", uuid]</code>: one open link to another kernel.
 *
 * <p>
 * As with a socket, the expression carries only the name; the streams live in
 * {@link LinkRegistry}, so a link can be stored in an association and passed around while the
 * kernel on the other end comes and goes.
 */
public class LinkObjectExpr extends DataExpr<String> {

  private static final long serialVersionUID = 1L;

  public static LinkObjectExpr newInstance(LinkEntry entry) {
    return new LinkObjectExpr(entry.uuid());
  }

  private LinkObjectExpr(String uuid) {
    super(S.LinkObject, uuid);
  }

  /** The link this names, or <code>null</code> once it has been closed. */
  public LinkEntry entry() {
    return LinkRegistry.get(fData);
  }

  @Override
  public IExpr evaluateHead(IAST ast, EvalEngine engine) {
    if (!ast.isAST1() || !ast.arg1().isString()) {
      return F.NIL;
    }
    LinkEntry entry = entry();
    switch (ast.arg1().toString()) {
      case "Name":
        return entry == null ? S.$Failed : F.stringx(entry.name());
      case "UUID":
        return F.stringx(fData);
      case "ProcessID":
        return entry == null || entry.process() == null ? S.$Failed
            : F.ZZ(entry.process().pid());
      default:
        return F.NIL;
    }
  }

  @Override
  public IAST fullForm() {
    LinkEntry entry = entry();
    return F.binaryAST2(S.LinkObject, F.stringx(entry == null ? "" : entry.name()),
        F.stringx(fData));
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    return obj instanceof LinkObjectExpr && fData.equals(((LinkObjectExpr) obj).fData);
  }

  @Override
  public int hashCode() {
    return 211 + fData.hashCode();
  }

  @Override
  public IExpr copy() {
    return new LinkObjectExpr(fData);
  }

  @Override
  public String toString() {
    LinkEntry entry = entry();
    return "LinkObject[" + (entry == null ? "closed" : entry.name()) + ", " + fData + "]";
  }
}
