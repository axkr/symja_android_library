package org.matheclipse.core.system;

import org.junit.Test;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.EvalEngine;

public class StreamTest extends ExprEvaluatorTestCase {

  @Test
  public void testBinaryWrite001() {
    call("f = File(\"test.bin\")");
    check("BinaryWrite(f, {8, 97, 255, 255, 255});", //
        "");
    check("BinaryRead(f)", //
        "8");
    check("BinaryRead(f, \"Character8\")", //
        "a");
    check("BinaryRead(f, \"Integer8\")", //
        "-1");
    check("BinaryRead(f, \"Byte\")", //
        "255");
    check("BinaryRead(f, \"UnsignedInteger8\")", //
        "255");
    // BinaryRead: EOFException
    check("BinaryRead(f, \"UnsignedInteger8\")", //
        "EndOfFile");
  }

  @Test
  public void testFindList() {
    check("sstream=StringToStream(\"12345\\n45\\nx\\ny\");", //
        "");
    check("FindList(sstream, \"4\") // InputForm", //
        "{\"12345\",\"45\"}");
    check("FindList(sstream,\"23\")", //
        "{}");
    check("FindList(StringToStream(\"12345\\n45\\nx\\ny\"),\"23\")", //
        "{12345}");
  }

  @Test
  public void testReadLine() {
    check("sstream=StringToStream(\"123\\n45\\nx\\ny\");", //
        "");
    check("ReadLine(sstream) // InputForm", //
        "\"123\"");
    check("ReadLine(sstream)", //
        "45");
  }

  @Test
  public void testRead001() {
    call("str = StringToStream(\"4711 dummy 0815\")");
    check("Read(str, Number)", //
        "4711");
    check("Read(str, {Word, Number})", //
        "{dummy,0815}");
    check("Read(str, Number)", //
        "EndOfFile");
    check("Close(str)", //
        "String");
  }

  @Test
  public void testRead002() {
    call("str = StringToStream(\"4711 dummy 0815\")");
    check("Read(str, Number)", //
        "4711");
    check("Read(str, Word)", //
        "dummy");
    check("Read(str, Number)", //
        "0815");
    check("Close(str)", //
        "String");
  }

  @Test
  public void testRead003() {
    check("str = \"foo::usage = \\\"foo is ...\\\";\n" + "bar::usage = \\\"bar is ...\\\";\"", //
        "foo::usage = \"foo is ...\";\n" + "bar::usage = \"bar is ...\";");
    check("strm = StringToStream(str);", //
        "");
    check("Read(strm, Hold(Expression)) // InputForm", //
        "Hold(foo::usage=\"foo is ...\";Null)");
    check("Read(strm, Hold(Expression)) // InputForm", //
        "Hold(bar::usage=\"bar is ...\";Null)");
    check("Close(strm)", //
        "String");
  }

  @Test
  public void testRead004() {
    // newline after ::usage
    check("str = \"foo::usage = \n \\\"foo is ...\\\";\n" + "bar::usage = \\\"bar is ...\\\";\"", //
        "foo::usage = \n" + " \"foo is ...\";\n" + "bar::usage = \"bar is ...\";");
    check("strm = StringToStream(str);", //
        "");
    check("Read(strm, Hold(Expression)) // InputForm", //
        "Hold(foo::usage=\"foo is ...\";Null)");
    check("Read(strm, Hold(Expression)) // InputForm", //
        "Hold(bar::usage=\"bar is ...\";Null)");
    check("Close(strm)", //
        "String");
  }

  @Test
  public void testReadList001() {
    check("ReadList(StringToStream(\"123\\n45\\nx\\ny\"), String) // InputForm", //
        "{\"123\",\"45\",\"x\",\"y\"}");
    check("ReadList(StringToStream(\"123\\n45\\nx\\ny\"))", //
        "{123,45,x,y}");
  }

  @Test
  public void testReadList002() {
    check("ReadList(StringToStream(\"(**)\\n\\n\\n{0, x, 1, 0}\\n{1, x, 1, x}\"))", //
        "{Null,{0,x,1,0},{1,x,1,x}}");
    check("ReadList(StringToStream(\"(**)\\n\\n\\n{0, x, 1, 0}\\n{1, x, 1, x}\"), Expression)", //
        "{Null,{0,x,1,0},{1,x,1,x}}");
    check("ReadList(StringToStream(\"(**)\\n\\n\\n{0, x, 1, 0}\\n{1, x, 1, x}\"), Expression, 2)", //
        "{Null,{0,x,1,0}}");

    check(
        "ReadList(StringToStream(\"(**)\\n\\n\\n{0, x, 1, 0}\\n{1, x, 1, x}\"), String) // InputForm", //
        "{\"(**)\",\"{0, x, 1, 0}\",\"{1, x, 1, x}\"}");
  }

  @Test
  public void testFile001() {
    call("f = File(\"test.txt\")");
    check("Write(f, a + b)", //
        "");
    check("Read(f, {Word,Word,Word})", //
        "{a,+,b}");
  }

  @Test
  public void testStringToStream() {
    call("str = StringToStream(\"234,32412,4234\")");
    check("Read(str, Number)", //
        "234");
    check("Read(str, Character)", //
        ",");
    check("Close(str)", //
        "String");
  }

  @Test
  public void testOpenAppend001() {
    call("f = FileNameJoin({$TemporaryDirectory, \"test_open.txt\"});Print(f)");
    check("str = OpenWrite(f);", //
        "");
    check("Write(str, x^2 - y^2)", //
        "");
    check("Close(str);", //
        "");
    check("FilePrint(f)", //
        "");

    check("str = OpenAppend(f);", //
        "");
    check("Write(str, x^2 + y^2)", //
        "");
    check("Close(str);", //
        "");
    check("FilePrint(f)", //
        "");
  }

  @Test
  public void testOpenAppend002() {
    // create temporary file and open output stream
    check("str = OpenWrite();Print(str);", //
        "");
    check("Write(str, x^2 - y^2)", //
        "");
    check("Close(str);", //
        "");
  }

  /** The JUnit setup method */
  @Override
  public void setUp() {
    super.setUp();
    Config.FILESYSTEM_ENABLED = true;
    Config.MAX_AST_SIZE = 10000;
    EvalEngine.get().setIterationLimit(500);
  }

  @Override
  public void tearDown() throws Exception {
    super.tearDown();
  }
}
