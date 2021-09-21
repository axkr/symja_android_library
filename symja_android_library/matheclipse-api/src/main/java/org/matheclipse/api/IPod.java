package org.matheclipse.api;

import org.matheclipse.core.eval.EvalEngine;
import com.fasterxml.jackson.databind.node.ArrayNode;

public interface IPod {
  public static final short DOCUMENTATION = 1;
  public static final short ELEMENT_DATA = 2;
  public static final short CONSTANT_DATA = 3;
  public static final short LIST_DATA = 4;
  /**
   * Get the type of this pod
   *
   * @return
   */
  public short podType();

  /**
   * Get the key word of this pod
   *
   * @return
   */
  public String keyWord();

  /**
   * Create and add JSON output of this object.
   *
   * @param podsArray
   * @return the number of pods; <code>0</code> if no pod was appended
   */
  public int addJSON(ArrayNode podsArray, int formats, EvalEngine engine);
}
