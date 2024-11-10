package org.matheclipse.core.system;

import org.junit.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;

public class FileFunctionsTest extends ExprEvaluatorTestCase {

  @Override
  public void setUp() {
    super.setUp();
    try {
      F.initSymbols();
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
