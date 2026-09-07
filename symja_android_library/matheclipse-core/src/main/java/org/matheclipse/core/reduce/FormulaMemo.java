package org.matheclipse.core.reduce;

import java.util.HashMap;
import java.util.Map;

/**
 * Structural cache for one quantifier elimination run. Repeated subformulas are common after
 * Cooper's boundary instantiation, and eliminating them once is what keeps a nested elimination
 * from re-deciding the same problem exponentially often.
 */
public final class FormulaMemo {

  private final Map<Formula, Formula> entries = new HashMap<Formula, Formula>();

  private int hits;

  public Formula get(Formula key) {
    Formula value = entries.get(key);
    if (value != null) {
      hits++;
    }
    return value;
  }

  public void put(Formula key, Formula value) {
    entries.put(key, value);
  }

  public int hits() {
    return hits;
  }

  public int size() {
    return entries.size();
  }
}
