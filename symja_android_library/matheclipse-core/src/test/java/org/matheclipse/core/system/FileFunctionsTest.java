package org.matheclipse.core.system;

import org.junit.Test;
import org.matheclipse.core.basic.Config;

public class FileFunctionsTest extends ExprEvaluatorTestCase {

  @Test
  public void testSave() {
    Config.FILESYSTEM_ENABLED = true;
    check("g(x_) := x^3;"//
        + "g(x_,y_) := f(x,y);"//
        + "SetAttributes(f, Listable);"//
        + "f(x_) := g(x^2);", //
        "");

    check("temp = FileNameJoin({$TemporaryDirectory, \"saved.txt\"});Print(temp);", //
        "");
    check("Save(temp, f)", //
        "");

    check("ClearAll(f,g)", //
        "");
    check("Attributes(f)", //
        "{}");
    check("{f(2),g(7)}", //
        "{f(2),g(7)}");

    check("Get(temp)", //
        "");
    check("{f(2),g(7)}", //
        "{64,343}");
    check("Attributes(f)", //
        "{Listable}");
  }

  @Test
  public void testSaveList() {
    Config.FILESYSTEM_ENABLED = true;

    check("g(x_) := x^3;"//
        + "g(x_,y_) := f(x,y);"//
        + "SetAttributes(f, Listable);"//
        + "f(x_) := g(x^2);", //
        "");

    check("temp = FileNameJoin({$TemporaryDirectory, \"savedlist.txt\"});Print(temp);", //
        "");
    check("Save(temp, {f,g})", //
        "");

    check("ClearAll(f,g)", //
        "");
    check("Attributes(f)", //
        "{}");
    check("{f(2),g(7)}", //
        "{f(2),g(7)}");

    check("Get(temp)", //
        "");
    check("{f(2),g(7)}", //
        "{64,343}");
    check("Attributes(f)", //
        "{Listable}");
  }

  @Test
  public void testSaveGlobalContext() {
    Config.FILESYSTEM_ENABLED = true;

    check("g(x_) := x^3;"//
        + "g(x_,y_) := f(x,y);"//
        + "SetAttributes(f, Listable);"//
        + "f(x_) := g(x^2);", //
        "");

    check("temp = FileNameJoin({$TemporaryDirectory, \"savedcntxt.txt\"});Print(temp);", //
        "");
    check("Save(temp, \"Global`*\")", //
        "");

    check("ClearAll(f,g)", //
        "");
    check("Attributes(f)", //
        "{}");
    check("{f(2),g(7)}", //
        "{f(2),g(7)}");
    check("Get(temp)", //
        "");
    check("{f(2),g(7)}", //
        "{64,343}");
    check("Attributes(f)", //
        "{Listable}");
  }


}
