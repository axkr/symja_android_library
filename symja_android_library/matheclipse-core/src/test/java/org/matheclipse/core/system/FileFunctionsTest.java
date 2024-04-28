package org.matheclipse.core.system;

import org.junit.Test;
import org.matheclipse.core.basic.Config;

public class FileFunctionsTest extends ExprEvaluatorTestCase {

  @Test
  public void testSave() {
    Config.FILESYSTEM_ENABLED = true;
    check("temp = FileNameJoin({$TemporaryDirectory, \"saved.txt\"});Print(temp);", //
        "");
    check("a = 1000", //
        "1000");
    check("Save(temp, a)", //
        "");
    check("Clear(a)", //
        "");
    check("Get(temp)", //
        "{1000}");
    check("a", //
        "1000");
  }


}
