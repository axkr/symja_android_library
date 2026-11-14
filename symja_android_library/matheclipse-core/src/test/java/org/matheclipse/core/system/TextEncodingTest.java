package org.matheclipse.core.system;

import org.junit.jupiter.api.Test;

/** Character encodings of strings, byte arrays and the text formats. */
public class TextEncodingTest extends ExprEvaluatorTestCase {

  @Test
  public void testByteArrayToString() {
    check("ToCharacterCode(ByteArrayToString(ByteArray({107, 195, 188, 226, 130, 172})))", //
        "{107,252,8364}");
    check(
        "{ToCharacterCode(ByteArrayToString(ByteArray({107, 195, 188}), \"ISOLatin1\")), "
            + "ToCharacterCode(ByteArrayToString(ByteArray({107, 195, 188}), \"UTF8\")), "
            + "ToCharacterCode(ByteArrayToString(ByteArray({252, 133, 150}), \"WindowsANSI\")), "
            + "ToCharacterCode(ByteArrayToString(ByteArray({138, 159, 165}), \"MacintoshRoman\"))}", //
        "{{107,195,188},{107,252},{252,8230,8211},{228,252,8226}}");
    // a byte without a character in the encoding is the character 0xF200 + byte
    check(
        "{ToCharacterCode(ByteArrayToString(ByteArray({90, 128, 255}), \"ASCII\")), "
            + "ToCharacterCode(ByteArrayToString(ByteArray({9, 13, 32, 126, 127, 200}), \"PrintableASCII\")), "
            + "ToCharacterCode(ByteArrayToString(ByteArray({141, 143, 144, 157, 158}), \"WindowsANSI\"))}", //
        "{{90,62080,62207},{61961,13,32,126,62079,62152},{62093,62095,62096,62109,382}}");
    check("ByteArrayToString(ByteArray({}), \"ISOLatin1\") // InputForm", //
        "\"\"");
    // an encoding which is not known
    check("Head(ByteArrayToString(ByteArray({90}), \"NoSuchEncoding\"))", //
        "ByteArrayToString");
  }

  @Test
  public void testByteArrayToStringInvalidUTF8() {
    // $CharacterEncoding: The byte sequence {226,130,90} could not be interpreted as a character
    // in the UTF-8 character encoding.
    // the first byte of the sequence stays, the bytes after it are read again
    check("ToCharacterCode(ByteArrayToString(ByteArray({226, 130, 90, 195, 188})))", //
        "{226,130,90,252}");
    check("Check(ByteArrayToString(ByteArray({90, 240, 159})), \"msg\")", //
        "msg");
    check("ToCharacterCode(Quiet(ByteArrayToString(ByteArray({90, 240, 159}))))", //
        "{90,240,159}");
    // a sequence which is longer than it has to be is read as its character
    check("ToCharacterCode(ByteArrayToString(ByteArray({193, 129, 224, 129, 130})))", //
        "{65,66}");
  }

  @Test
  public void testStringToByteArray() {
    check(
        "{Normal(StringToByteArray(\"k\u00FC\")), Normal(StringToByteArray(\"k\u00FC\", \"UTF8\")), "
            + "Normal(StringToByteArray(\"k\u00FC\", \"ISOLatin1\")), "
            + "Normal(StringToByteArray(\"k\u00FC\u2026\", \"WindowsANSI\")), "
            + "Normal(StringToByteArray(\"k\u00FC\u2022\", \"MacintoshRoman\"))}", //
        "{{107,195,188},{107,195,188},{107,252},{107,252,133},{107,159,165}}");
    // a character the encoding does not have
    check(
        "{Head(StringToByteArray(\"k\u2026\", \"ISOLatin1\")), Head(StringToByteArray(\"k\u00FC\", \"ASCII\")), "
            + "Head(StringToByteArray(\"k\tm\", \"PrintableASCII\"))}", //
        "{StringToByteArray,StringToByteArray,StringToByteArray}");
    // StringToByteArray: The character '...' (code point 8230) cannot be represented in character
    // encoding ISOLatin1.
    check("Check(StringToByteArray(\"k\u2026\", \"ISOLatin1\"), \"msg\")", //
        "msg");
    // an encoding which is not known: no message
    check("Check(Head(StringToByteArray(\"k\", \"NoSuchEncoding\")), \"msg\")", //
        "StringToByteArray");
    check("ByteArrayToString(StringToByteArray(\"k\u00FC\u2022\", \"MacintoshRoman\"), \"MacintoshRoman\")", //
        "k\u00FC\u2022");
  }

  @Test
  public void testToCharacterCodeEncoding() {
    check(
        "{ToCharacterCode(\"k\u00FC\u2026\", \"WindowsANSI\"), ToCharacterCode(\"k\u00FC\u2026\", \"MacintoshRoman\"), "
            + "ToCharacterCode(\"k\u00FC\u2026\", \"ISOLatin1\"), ToCharacterCode(\"k\u0394\u00B5\u03BC\", \"MacintoshRoman\")}", //
        "{{107,252,133},{107,159,201},{107,252,None},{107,198,None,181}}");
    // the character which a byte without a character is read as is not written back
    check(
        "{ToCharacterCode(FromCharacterCode(62093), \"WindowsANSI\"), ToCharacterCode(FromCharacterCode({62080, 90}), \"ASCII\")}", //
        "{{None},{None,90}}");
  }

  @Test
  public void testFromCharacterCodeEncoding() {
    // codes below 256 are bytes of the encoding, the larger ones are characters
    check(
        "{ToCharacterCode(FromCharacterCode({252, 133, 8230, 141}, \"WindowsANSI\")), "
            + "ToCharacterCode(FromCharacterCode({252, 1000}, \"ISOLatin1\")), "
            + "ToCharacterCode(FromCharacterCode({159, 219, 181}, \"MacintoshRoman\")), "
            + "ToCharacterCode(FromCharacterCode({226, 130, 172, 1000, 195, 188}, \"UTF8\")), "
            + "ToCharacterCode(FromCharacterCode({90, 200}, \"ASCII\"))}", //
        "{{252,8230,8230,62093},{252,1000},{252,164,956},{8364,1000,252},{90,62152}}");
    check("ToCharacterCode(FromCharacterCode({{107, 252}, {133}}, \"WindowsANSI\"))", //
        "{{107,252},{8230}}");
    check("ToCharacterCode(FromCharacterCode(133, \"WindowsANSI\"))", //
        "{8230}");
    // a character interrupts a byte sequence
    check("ToCharacterCode(Quiet(FromCharacterCode({195, 1000, 188}, \"UTF8\")))", //
        "{195,1000,188}");
  }

  @Test
  public void testExportStringBytes() {
    // a text format is handed out as the bytes of its UTF-8 encoding
    check(
        "{ToCharacterCode(ExportString(\"k\u00FC\u2022\", \"Text\")), ToCharacterCode(ExportString(\"k\u00FC\", \"Table\"))}", //
        "{{107,195,188,226,128,162},{107,195,188}}");
    // every CSV cell is quoted, a number too
    check("ToCharacterCode(ExportString({{\"\u00FC\", 5}, {\"m\"}}, \"CSV\"))", //
        "{34,195,188,34,44,34,53,34,10,34,109,34,10}");
    check("ToCharacterCode(ExportString({\"\u00FC\"}, \"JSON\"))", //
        "{91,34,195,188,34,93}");
    check("ExportString({{\"p\\\"q\", 2.5}, {x, 7}}, \"CSV\") // InputForm", //
        "\"\\\"p\\\"\\\"q\\\",\\\"2.5\\\"\\n\\\"x\\\",\\\"7\\\"\\n\"");
    check("ExportString({3, \"uv\", 4.5}, \"Table\") // InputForm", //
        "\"3\\nuv\\n4.5\"");
    check("ExportString(\"k\u00FC\", \"String\") == \"k\u00FC\"", //
        "True");
  }

  @Test
  public void testExportStringJSONEscapes() {
    check("ExportString(FromCharacterCode({90, 8, 12, 9, 31, 127, 1}), \"JSON\") // InputForm", //
        "\"\\\"Z\\\\b\\\\f\\\\t\\\\u001f\\\\u007f\\\\u0001\\\"\"");
  }

  @Test
  public void testImportStringBytes() {
    check(
        "t = \"k\u00FC\u2022\"; {ImportString(ExportString(t, \"Text\"), \"Text\") == t, "
            + "ImportString(ExportString({t, 3}, \"JSON\"), \"JSON\") == {t, 3}, "
            + "ImportString(ExportString(<|t -> t|>, \"RawJSON\"), \"RawJSON\") == <|t -> t|>}", //
        "{True,True,True}");
    // a text which is not UTF-8 as a whole stays as it is; "String" is the characters
    check(
        "{ToCharacterCode(ImportString(FromCharacterCode({195, 188, 252, 107}), \"Text\")), "
            + "ToCharacterCode(ImportString(FromCharacterCode({195, 188, 252, 107}), \"String\")), "
            + "ToCharacterCode(ImportString(FromCharacterCode({252, 8226, 195, 188}), \"Text\"))}", //
        "{{195,188,252,107},{195,188,252,107},{252,8226,195,188}}");
    check("ToCharacterCode(ImportString(FromCharacterCode({107, 195, 188, 226, 128, 162}), \"Text\"))", //
        "{107,252,8226}");
    check("ImportString(\"p+p\", \"String\") // InputForm", //
        "\"p+p\"");
  }
}
