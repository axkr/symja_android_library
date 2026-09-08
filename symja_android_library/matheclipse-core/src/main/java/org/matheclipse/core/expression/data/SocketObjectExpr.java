package org.matheclipse.core.expression.data;

import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.io.net.SocketEntry;
import org.matheclipse.core.io.net.SocketRegistry;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

/**
 * <code>SocketObject["uuid"]</code>: one open socket.
 *
 * <p>
 * The expression carries only the name; the socket itself lives in {@link SocketRegistry}, so a
 * <code>SocketObject</code> stays a value that can be copied, compared and stored in an association
 * while the connection behind it is opened and closed elsewhere.
 *
 * <p>
 * Asked for a property - <code>socket["DestinationPort"]</code> - it answers from the registry.
 */
public class SocketObjectExpr extends DataExpr<String> {

  private static final long serialVersionUID = 1L;

  public static SocketObjectExpr newInstance(SocketEntry entry) {
    return new SocketObjectExpr(entry.uuid());
  }

  public static SocketObjectExpr newInstance(String uuid) {
    return new SocketObjectExpr(uuid);
  }

  private SocketObjectExpr(String uuid) {
    super(S.SocketObject, uuid);
  }

  /** The socket this names, or <code>null</code> once it has been closed. */
  public SocketEntry entry() {
    return SocketRegistry.get(fData);
  }

  @Override
  public IExpr evaluateHead(IAST ast, EvalEngine engine) {
    if (!ast.isAST1() || !ast.arg1().isString()) {
      return F.NIL;
    }
    String property = ast.arg1().toString();
    SocketEntry entry = entry();
    switch (property) {
      case "UUID":
        return F.stringx(fData);
      case "Properties":
        return F.List(F.stringx("UUID"), F.stringx("DestinationHostname"),
            F.stringx("DestinationPort"), F.stringx("ConnectedClients"));
      case "DestinationHostname":
        return entry == null ? S.$Failed : F.stringx(entry.host());
      case "DestinationPort":
        return entry == null ? S.$Failed : F.ZZ(entry.port());
      case "ConnectedClients":
        if (entry == null) {
          return S.$Failed;
        }
        IASTAppendable clients = F.ListAlloc(entry.acceptedUuids().size());
        for (String uuid : entry.acceptedUuids()) {
          if (SocketRegistry.get(uuid) != null) {
            clients.append(new SocketObjectExpr(uuid));
          }
        }
        return clients;
      default:
        return F.NIL;
    }
  }

  @Override
  public IAST fullForm() {
    return F.unaryAST1(S.SocketObject, F.stringx(fData));
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    return obj instanceof SocketObjectExpr && fData.equals(((SocketObjectExpr) obj).fData);
  }

  @Override
  public int hashCode() {
    return 197 + fData.hashCode();
  }

  @Override
  public IExpr copy() {
    return new SocketObjectExpr(fData);
  }

  @Override
  public String toString() {
    return "SocketObject[" + fData + "]";
  }
}
