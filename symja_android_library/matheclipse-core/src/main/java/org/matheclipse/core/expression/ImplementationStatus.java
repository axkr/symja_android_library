package org.matheclipse.core.expression;

import org.matheclipse.core.interfaces.IBuiltInSymbol;

/**
 * Status of the implementation of a built-in symbol function ({@link IBuiltInSymbol}). This class
 * defines constants for different implementation statuses, such as full support, partial support,
 * no support, deprecated, experimental, supported on JVM, supported on Windows, and alias for
 * another function. It also provides arrays of status strings and emojis for visual representation
 * of the implementation status.
 */
public class ImplementationStatus {
  /** Full supported status */
  public final static int FULL_SUPPORT = 0;
  /** Partially implemented status */
  public final static int PARTIAL_SUPPORT = 1;
  /** Currently not supported status */
  public final static int NO_SUPPORT = 2;
  /** Deprecated status */
  public final static int DEPRECATED = 3;
  /** Experimental status */
  public final static int EXPERIMENTAL = 4;
  /** Supported on JVM status */
  public final static int JVM_SUPPORT = 5;
  /** Supported on Windows status */
  public final static int WINDOWS_SUPPORT = 6;
  /** Alias for another function status */
  public final static int ALIAS = 7;

  /**
   * Status strings for the implementation status.
   */
  public final static String[] STATUS_STRINGS = {//
      "* &#x2705; - full supported", //
      "* &#x2611; - partially implemented", //
      "* &#x274C; - currently not supported", //
      "* &#x26A0; - deprecated", //
      "* &#x1F9EA; - experimental", //
      "* &#x2615; - supported on Java virtual machine ", //
      "* &#x229E; - supported on Windows", //
      "* &#x1F504; - alias for another function"};

  /**
   * Emojis for the implementation status.
   */
  public final static String[] STATUS_EMOJIS = {//
      " &#x2705; ", //
      " &#x2611; ", //
      " &#x274C; ", //
      " &#x26A0; ", //
      " &#x1F9EA; ", //
      " &#x2615; ", //
      " &#x229E; ", //
      " &#x1F504; "};
}
