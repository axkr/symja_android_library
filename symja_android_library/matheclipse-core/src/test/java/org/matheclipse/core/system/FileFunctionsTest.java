package org.matheclipse.core.system;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;

public class FileFunctionsTest extends ExprEvaluatorTestCase {

  @Override
  public void setUp() {
    super.setUp();
    try {
      F.initSymja();
      F.await();
      // wait especially for Rubi` context to be initialized otherwise
      // java.util.ConcurrentModificationException can occur in saving Global` context in
      // testSaveGlobalContext()
      S.Integrate.getEvaluator().await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }


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
  public void testGetAfterSyntaxError() throws IOException {
    Config.FILESYSTEM_ENABLED = true;
    // Get reports a syntax error and goes on reading the rest of the file
    Path file = Files.createTempFile("get-syntax-error", ".m");
    try {
      Files.writeString(file, "sxa = 1;\n" //
          + "sxb = 2 +* ;\n" //
          + "sxc = 3;\n" //
          + "{sxa, sxb, sxc}\n");
      check("Get(\"" + file.toString().replace("\\", "\\\\") + "\")", //
          "{1,sxb,3}");

      // the error is in the second line of a definition; reading resumes after that line
      Files.writeString(file, "sxd = 4;\n" //
          + "sxf(x_) := x +\n" //
          + "   * 2;\n" //
          + "sxe = 5;\n" //
          + "{sxd, sxe, sxf(1)}\n");
      check("Get(\"" + file.toString().replace("\\", "\\\\") + "\")", //
          "{4,5,sxf(1)}");

      // an error in the last line
      Files.writeString(file, "sxg = 6;\n" //
          + "sxh = 2 +* ;\n");
      check("Get(\"" + file.toString().replace("\\", "\\\\") + "\")", //
          "");
      check("sxg", //
          "6");
    } finally {
      Files.deleteIfExists(file);
    }
  }

  @Test
  public void testSaveIn() {
    Config.FILESYSTEM_ENABLED = true;
    check("g(x_) := x^3", //
        "");

    check("g(x_,y_) := f(x,y)", //
        "");

    check("SetAttributes(f, Listable)", //
        "");

    check("f(x_) := g(x^2) ", //
        "");

    check("temp = FileNameJoin({$TemporaryDirectory, \"savedin.txt\"});Print(temp);", //
        "");
    check("FullDefinition(In)", //
        "Attributes(In)={Listable,NHoldFirst,Protected}\n" //
            + "\n" //
            + "In(1):=g(x_):=x^3\n" //
            + "\n" //
            + "In(2):=g(x_,y_):=f(x,y)\n" //
            + "\n" //
            + "In(3):=SetAttributes(f,Listable)\n" //
            + "\n" //
            + "In(4):=f(x_):=g(x^2)\n" //
            + "\n" //
            + "In(5):=(temp=FileNameJoin({$TemporaryDirectory,\"savedin.txt\"});Print(temp);Null)");

    check("Save(temp, In)", //
        "");
    //
    // check("ClearAll(f,g)", //
    // "");
    // check("Attributes(f)", //
    // "{}");
    // check("{f(2),g(7)}", //
    // "{f(2),g(7)}");
    //
    // check("Get(temp)", //
    // "");
    // check("{f(2),g(7)}", //
    // "{64,343}");
    // check("Attributes(f)", //
    // "{Listable}");
  }

  @Test
  public void testSaveOut() {
    Config.FILESYSTEM_ENABLED = true;
    check("g(x_) := x^3", //
        "");

    check("g(x_,y_) := f(x,y)", //
        "");

    check("SetAttributes(f, Listable)", //
        "");

    check("f(x_) := g(x^2) ", //
        "");
    check("hello = \"hello world\" ", //
        "hello world");
    check("temp = FileNameJoin({$TemporaryDirectory, \"savedout.txt\"});Print(temp);", //
        "");
    check("FullDefinition(Out)", //
        "Attributes(Out)={Listable,NHoldFirst,Protected}\n" //
            + "\n" //
            + "Out(1)=Null\n" //
            + "\n" //
            + "Out(2)=Null\n" //
            + "\n" //
            + "Out(3)=Null\n" //
            + "\n" //
            + "Out(4)=Null\n" //
            + "\n" //
            + "Out(5)=\"hello world\"\n" //
            + "\n" //
            + "Out(6)=Null");

    check("Save(temp, Out)", //
        "");
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
