package org.matheclipse.core.system;

import org.junit.Test;
import org.matheclipse.core.basic.Config;

public class FileFunctionsTest extends ExprEvaluatorTestCase {

  @Test
  public void testSave() {
    Config.FILESYSTEM_ENABLED = true;
    check("temp = FileNameJoin({$TemporaryDirectory, \"saved.txt\"});Print(temp);", //
        "");
    check("g(x_) := x^3;"//
        + "SetAttributes(f, Listable);"//
        + "f(x_) := g(x^2);", //
        "");
    check("Save(temp, f)", //
        "");
    check("Clear(f,g)", //
        "");
    check("{f(2),g(7)}", //
        "{f(2),g(7)}");
    check("Get(temp)", //
        "");
    check("{f(2),g(7)}", //
        "{64,343}");
  }


}
