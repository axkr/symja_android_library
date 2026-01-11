package org.matheclipse.tools;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.matheclipse.core.eval.util.SourceCodeProperties;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.tensor.QuantityParser;
import org.matheclipse.core.tensor.io.ResourceData;

public class PhysicalConstantsGenerator {
  private static final Map<String, IExpr> MAP = new HashMap<>();
  static {
    Properties properties =
        ResourceData.properties("/org/matheclipse/core/tools/physical_constants.properties");
    for (String key : properties.stringPropertyNames())
      MAP.put(key, QuantityParser.fromString(properties.getProperty(key)));
  }

  public static void main(String[] args) {
    for (Map.Entry<String, IExpr> entry : MAP.entrySet()) {
      String key = entry.getKey();
      IExpr val = entry.getValue();
      System.out.println("F.ISet(S." + key + ", "
          + val.internalJavaString(SourceCodeProperties.JAVA_FORM_PROPERTIES, -1, null) + ");");
    }
  }

  /**
   * @param key for instance "BohrRadius"
   * @return
   */
  public static IExpr of(String key) {
    return MAP.get(key);
  }


}
