package org.matheclipse.core.sympy.utilities;

import java.util.function.Function;
import java.util.function.Predicate;
import org.matheclipse.core.expression.DefaultDict;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.Pair;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IASTAppendable;
import org.matheclipse.core.interfaces.IExpr;

public class Iterables {

  /**
   * 
   * @param seq
   * @param keyfunc
   * @return a pair of {@link IASTAppendable}
   */
  public static Pair siftBinary(IAST seq, Predicate<IExpr> keyfunc) {
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
    return F.pair(t, f);
  }

  public static DefaultDict<IASTAppendable> sift(IAST seq, Function<IExpr, IExpr> keyfunc) {
    DefaultDict<IASTAppendable> m = new DefaultDict<IASTAppendable>(() -> F.ListAlloc());
    for (int j = 1; j < seq.size(); j++) {
      IExpr i = seq.get(j);
      m.getValue(keyfunc.apply(i)).append(i);
    }
    return m;
  }
}
