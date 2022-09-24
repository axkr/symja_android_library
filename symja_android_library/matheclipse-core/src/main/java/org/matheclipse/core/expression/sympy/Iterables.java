package org.matheclipse.core.expression.sympy;

import java.util.function.Function;
import java.util.function.Predicate;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

public class Iterables {
  public static IAST siftBinary(IAST seq, Predicate<IExpr> keyfunc) {
    IASTAppendable f = F.ListAlloc();
    IASTAppendable t = F.ListAlloc();
    for (int j = 1; j < seq.size(); j++) {
      IExpr i = seq.get(j);
      if (keyfunc.test(i)) {
        t.append(i);
      } else {
        f.append(i);
      }
    }
    return F.List(t, f);
  }

  public static DefaultDict<IASTAppendable> sift(IAST seq, Function<IExpr, IExpr> keyfunc) {
    DefaultDict<IASTAppendable> m = new DefaultDict<IASTAppendable>(() -> F.ListAlloc());
    for (int j = 1; j < seq.size(); j++) {
      IExpr i = seq.get(j);
      m.get(keyfunc.apply(i)).append(i);
    }
    return m;
  }
}
