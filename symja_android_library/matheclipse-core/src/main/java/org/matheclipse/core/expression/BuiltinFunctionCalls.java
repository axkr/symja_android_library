package org.matheclipse.core.expression;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.interfaces.ISymbol;

public class BuiltinFunctionCalls {

  /**
   * Compare number of measured nano seconds.
   */
  public static Comparator<BuiltinFunctionCalls> NANO_COMPARATOR =
      new Comparator<BuiltinFunctionCalls>() {

        @Override
        public int compare(BuiltinFunctionCalls o1, BuiltinFunctionCalls o2) {
          return o1.nanoseconds < o2.nanoseconds ? -1 : (o1.nanoseconds > o2.nanoseconds ? 1 : 0);
        }
      };

  public static void printStatistics() {
    Set<BuiltinFunctionCalls> entrySet = Config.PRINT_PROFILE.keySet();
    BuiltinFunctionCalls[] array = new BuiltinFunctionCalls[entrySet.size()];
    entrySet.toArray(array);
    Arrays.sort(array, BuiltinFunctionCalls.NANO_COMPARATOR);
    for (int i = 0; i < array.length; i++) {
      BuiltinFunctionCalls key = array[i];
      System.out.println(key.symbol.toString() //
          + " - nano seconds: " + key.nanoseconds //
          + " - number of calls: " + key.numberOfCalls);
    }
  }

  final ISymbol symbol;

  long nanoseconds;

  long numberOfCalls;


  public BuiltinFunctionCalls(ISymbol symbol) {
    this.symbol = symbol;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (obj instanceof BuiltinFunctionCalls) {
      BuiltinFunctionCalls other = (BuiltinFunctionCalls) obj;
      return symbol.equals(other.symbol);
    }
    return false;
  }


  @Override
  public int hashCode() {
    return symbol.hashCode();
  }

  public long incCalls(long nanoseconds) {
    this.nanoseconds += nanoseconds;
    return ++this.numberOfCalls;
  }
}
