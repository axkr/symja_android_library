package org.matheclipse.core.expression;

public class ImplementationStatus {
  public final static int FULL_SUPPORT = 0;
  public final static int PARTIAL_SUPPORT = 1;
  public final static int NO_SUPPORT = 2;
  public final static int DEPRECATED = 3;
  public final static int EXPERIMENTAL = 4;
  public final static int JVM_SUPPORT = 5;
  public final static int WINDOWS_SUPPORT = 6;
  public final static int ALIAS = 7;

  public final static String[] STATUS_STRINGS = {//
      "* &#x2705; - full supported", //
      "* &#x2611; - partially implemented", //
      "* &#x274C; - currently not supported", //
      "* &#x26A0; - deprecated", //
      "* &#x1F9EA; - experimental", //
      "* &#x2615; - supported on Java virtual machine ", //
      "* &#x229E; - supported on Windows", //
      "* &#x1F504; - alias for another function"
  };

  public final static String[] STATUS_EMOJIS = {//
      " &#x2705; ", //
      " &#x2611; ", //
      " &#x274C; ", //
      " &#x26A0; ", //
      " &#x1F9EA; ", //
      " &#x2615; ", //
      " &#x229E; ", //
      " &#x1F504; "
  };
}
