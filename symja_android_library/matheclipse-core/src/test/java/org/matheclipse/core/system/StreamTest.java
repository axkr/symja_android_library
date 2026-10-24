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
        "1:eJxTTMoPSmNhYGAoBhEFOaXFaUwgHiuIl1+eWlTMCGQlZjIDyTRmmExJZm5qMUQMq2qwIIiZhE0PRBE2nUlgndhlQDoBeBkdqw==");
    check("Uncompress(str)", //
        "a^3+3*a^2*b+3*a*b^2+b^3");
    check("str = Compress(N(Pi, 100))", //
        "1:eJwNy7ERA0EIA8BrxQ14AAmQ2vgKPJ85cv+ZP9poX/fvur7nHLyT2a5ptLxGQZwaAkKtO0pienMMG9sZrQovTT4RscphTGkqZD8oQFWDlbkx+mTEH1hdF7c=");
    check("Uncompress(str)", //
        "3.141592653589793238462643383279502884197169399375105820974944592307816406286208998628034825342117068");

  }


  /**
   * <code>ReadString(stream, term)</code>: a string terminator that never comes reads the rest of the
   * stream, silently; a pattern that never matches is <code>ReadString::notfound</code>, and the
   * rest of the stream is still the answer. Mathematica (2026-09-11):
   * <code>ReadString[StringToStream["&lt;|...|&gt;\n"], "%" ~~ ...]</code> prints the message and
   * answers the text, and <code>FailureQ</code> of it is <code>False</code>. The WLJS notebook
   * reader rejects its older file format at the header check that follows.
   */
  /**
   * A literal terminator stays in the stream; a read which starts at one skips it first.
   */
  @Test
  public void testReadStringLiteralTerminator() {
    check("s=StringToStream(\"pq;rs;tu\"); {ReadString(s, \";\"), ReadString(s, \";\"), ReadString(s, \";\"), ReadString(s, \";\")} // InputForm", //
        "{\"pq\",\"rs\",\"tu\",EndOfFile}");
    check("s=StringToStream(\"uv::wx::yz\"); {ReadString(s, \":\" ~~ \":\"), Read(s, Character), ReadString(s, \"::\"), ReadString(s)} // InputForm", //
        "{\"uv\",\":\",\":wx\",\"::yz\"}");
    check("s=StringToStream(\"first second\"); {ReadString(s, \" \"), Read(s, Character), ReadString(s)} // InputForm", //
        "{\"first\",\" \",\"second\"}");
  }

  @Test
  public void testReadStringTerminatorNotFound() {
    check("s=StringToStream(\"abc\\ndef\"); {StringLength(Check(ReadString(s, \"xyz\"), \"msg\")), ReadString(s, \"xyz\")}", //
        "{7,EndOfFile}");
    check("s=StringToStream(\"<|a->1|>\\nmore\"); "
        + "Check(ReadString(s, ___ ~~ \"%Notebook%\" ~~ EndOfLine, TimeConstraint -> 10), \"msg\")", //
        "msg");
    check("s=StringToStream(\"<|a->1|>\\nmore\"); "
        + "{StringLength(Quiet(ReadString(s, \"%\" ~~ Repeated(\"-\", {16, 100}) ~~ \"%\"))), "
        + "ReadString(s)}", //
        "{13,EndOfFile}");
    check("FailureQ(Quiet(ReadString(StringToStream(\"<|a->1|>\"), \"%\" ~~ \"x\")))", //
        "False");
    // strings joined by ~~ are a literal terminator, which stays in the stream
    check("s=StringToStream(\"head\\n%%\\ntail\"); {ReadString(s, \"\\n\" ~~ \"%%\"), StringLength(ReadString(s))}", //
        "{head,8}");

    // a terminator starting with ___ is tried once, from the start: tried again from every position
    // a WLJS notebook of the older format (4 MB, no %Notebook% header) never finished loading
    check("s=StringToStream(StringRepeat(\"0123456789\", 2000)); "
        + "{StringLength(Quiet(ReadString(s, ___ ~~ \"%Notebook%\" ~~ EndOfLine))), ReadString(s)}", //
        "{20000,EndOfFile}");
    check("s=StringToStream(\"ab%Notebook%\\nrest\"); {ReadString(s, ___ ~~ \"%Notebook%\" ~~ EndOfLine), ReadString(s)}", //
        "{,\n"
            + "rest}");
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
  public void testStringStreamTypes() {
    // a stream over a string needs no file system access
    check("st = StringToStream(\"41 -2.5 word1 word2\\nlast\"); {Read(st, Number), Read(st, Number), Read(st, Word), ReadList(st, Word)}", //
        "{41,-2.5,word1,{word2,last}}");
    // an expression may continue on the next line
    check("st = StringToStream(\"h(u,\\n v) + 1\\nk(3)\"); {Read(st), Read(st), Read(st)}", //
        "{1+h(u,v),k(3),EndOfFile}");
    check("{ReadList(StringToStream(\"7 8 9 10\"), Number, 3), ReadList(StringToStream(\"3 p 4 q\"), {Number, Word})}", //
        "{{7,8,9},{{3,p},{4,q}}}");
    check("st = StringToStream(\"one\\ntwo\"); {Read(st, String), Read(st, String), Read(st, String)}", //
        "{one,two,EndOfFile}");
    // a real may end with its decimal point; a read which finds no number takes nothing
    check("st = StringToStream(\"6. .25 7*^-1 - 9\"); {Read(st, Number), Read(st, Number), Read(st, Number), Read(st, Number), Read(st, Word), Read(st, Number)}", //
        "{6.0,0.25,0.7,$Failed,-,9}");
  }

  @Test
  public void testRead001() {
    call("str = StringToStream(\"4711 dummy 0815\")");
    check("Read(str, Number)", //
        "4711");
    check("Read(str, {Word, Number})", //
        "{dummy,815}");
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
        "815");
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
    // a comment standing alone is the statement Null; empty lines are skipped
    check("ReadList(StringToStream(\"(**)\\n\\n\\n{0, x, 1, 0}\\n{1, x, 1, x}\"))", //
        "{Null,{0,x,1,0},{1,x,1,x}}");
    check("ReadList(StringToStream(\"(**)\\n\\n\\n{0, x, 1, 0}\\n{1, x, 1, x}\"), Expression, 2)", //
        "{Null,{0,x,1,0}}");
    check("ReadList(StringToStream(\"(* one\\n two *)\\nu+\\nv\\n\\n(* three *) w\\n\"))", //
        "{Null,u+v,w}");
    // also at the end of the input
    check("{ReadList(StringToStream(\"(* end *)\")), ReadList(StringToStream(\"v\\n(* end *)\"))}", //
        "{{Null},{v,Null}}");

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
    // a non-negative 64-bit value
    check("Hash(\"abcdef\")", //
        "13602181451802230653");
    check("Hash(7)>=0", //
        "True");
    check("StringLength(Hash(7,\"Expression\",\"HexString\"))", //
        "16");
    check("StringLength(Hash(7,\"Expression\",\"DecimalString\"))", //
        "20");
    check("FromDigits(Hash(7,\"Expression\",\"HexString\"),16)==Hash(7)", //
        "True");
    check("Hash(3+4)==Hash(7)", //
        "True");
    check("Hash({3,4})==Hash({4,3})", //
        "False");
    // every element counts, also the late ones of a long list, and the keys of an association
    check("Hash(Append(Range(25), \"u\"))==Hash(Append(Range(25), \"v\"))", //
        "False");
    check("Hash(Append(Range(25), {x}))==Hash(Append(Range(25), {x}))", //
        "True");
    check("Hash(<|a->1, b->2|>)==Hash(<|a->1, c->2|>)", //
        "False");
    // messages invhash and uform
    check("Hash(7,11)", //
        "Hash(7,11)");
    check("Hash(7,\"Expression\",\"NoFormat\")", //
        "Hash(7,Expression,NoFormat)");
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

  /** Write puts its expressions one after the other on a line of their own. */
  @Test
  public void testWriteLines() {
    call("wf = FileNameJoin({$TemporaryDirectory, \"symja_write_lines.txt\"})");
    check("s = OpenWrite(wf); Write(s, p + q); Write(s, r, t); Close(s); InputForm(ReadString(wf))", //
        "\"p + q\\nrt\\n\"");
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
