package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;
import org.matheclipse.core.basic.Config;

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
  public void testCompressUncompress() {
    check("str = Compress(Expand((a+b)^3))", //
        "H4sIAAAAAAAA/0uMM1bQVjDWSowz0kqCsLSS4oyArKQ4YwDZmlsNHQAAAA==");
    check("Uncompress(str)", //
        "a^3+3*a^2*b+3*a*b^2+b^3");
    check("str = Compress(N(Pi, 100))", //
        "H4sIAAAAAAAA/w3LuRHAAAwCsI1yGPzANtl/iqRSJT3VNeGOxrmIci+3JYuXAe2uXG2U6KYwJnKd7j8K59rG0ks4+THU5qhZddh7C/gA8p+qHmkAAAA=");
    check("Uncompress(str)", //
        "3.141592653589793238462643383279502884197169399375105820974944592307816406286208998628034825342117067");

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
  public void testFileHash() {
    check("FileHash(StringToStream(\"Hello World\"),\"CRC32\")", //
        "1243066710");
    check("FileHash(StringToStream(\"Hello World\"),\"MD5\")", //
        "235328152096874191772633713977838157797");
    check("FileHash(StringToStream(\"Hello World\"),\"SHA\")", //
        "58814527086678835124646218365765974827431397072");
    check("FileHash(StringToStream(\"Hello World\"),\"SHA256\")", //
        "74888964247292943290829644364954473609342749251111522634754282069725240300654");
    check("FileHash(StringToStream(\"Hello World\"),\"SHA512\")", //
        "2328401333974886873383385362411738102359762677572253218035172085337438831147144\\\n" //
            + "388051624789144933618951067533458801064187563795115771720046463851513726363");
  }

  @Test
  public void testHash() {
    // Symja expression hash value
    check("Hash(\"abcdef\")", //
        "-1424385912");
    check("Hash(ByteArray({123, 45, 67, 89, 98, 76, 54, 32, 1}), \"SHA\")", //
        "205163851794085253373860936160163003382114085276");

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

  @Test
  public void testURLDecode() {
    check("URLDecode(\"Hello%2C+World%21\")", //
        "Hello, World!");
  }

  @Test
  public void testURLEncode() {
    check("URLEncode(\"Hello, World!\")", //
        "Hello%2C%20World%21");
    check("URLEncode(Null) // InputForm", //
        "\"\"");
    check("URLEncode(None) // InputForm", //
        "\"\"");
    check("URLEncode(Missing) // InputForm", //
        "\"\"");
    check("URLEncode(True) // InputForm", //
        "\"true\"");
    check("URLEncode(False) // InputForm", //
        "\"false\"");
  }

  /** The JUnit setup method */
  @Override
  public void setUp() {
    super.setUp();
    Config.FILESYSTEM_ENABLED = true;
  }
}
