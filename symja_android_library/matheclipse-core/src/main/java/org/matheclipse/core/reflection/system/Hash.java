package org.matheclipse.core.reflection.system;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;
import java.util.zip.Checksum;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.expression.data.ByteArrayExpr;
import org.matheclipse.core.expression.data.FileExpr;
import org.matheclipse.core.expression.data.InputStreamExpr;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IAssociation;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IStringX;

public class Hash extends AbstractFunctionEvaluator {

  public static BigInteger hash(ByteArrayExpr data, String algorithmInput)
      throws IOException, NoSuchAlgorithmException {
    InputStream newInputStream = new ByteArrayInputStream(data.toData());
    return hash(newInputStream, algorithmInput);
  }

  public static BigInteger hash(FileExpr path, String algorithmInput)
      throws IOException, NoSuchAlgorithmException {
    InputStream newInputStream = Files.newInputStream(path.toData().toPath());
    return hash(newInputStream, algorithmInput);
  }

  public static BigInteger hash(InputStream inputStream, String algorithmInput)
      throws IOException, NoSuchAlgorithmException {
    byte[] digest = hashBytes(inputStream, algorithmInput);
    return digest == null ? null : new BigInteger(1, digest);
  }

  /**
   * The digest itself, rather than the number it spells.
   *
   * <p>
   * A digest is a fixed number of bytes and may begin with a zero one, which reading it as a number
   * throws away. Anything which has to hand the digest on as it stands - a WebSocket handshake
   * answers with its Base64 - needs the bytes.
   */
  public static byte[] hashBytes(InputStream inputStream, String algorithmInput)
      throws IOException, NoSuchAlgorithmException {
    String algoUpper = algorithmInput.toUpperCase();
    // CRC32 (uses java.util.zip)
    if ("CRC32".equals(algoUpper)) {
      Checksum crc32 = new CRC32();
      try (InputStream is = inputStream) {
        byte[] buffer = new byte[8192]; // 8KB Buffer
        int bytesRead;
        while ((bytesRead = is.read(buffer)) != -1) {
          crc32.update(buffer, 0, bytesRead);
        }
      }
      long checksum = crc32.getValue();
      return new byte[] {(byte) (checksum >>> 24), (byte) (checksum >>> 16),
          (byte) (checksum >>> 8), (byte) checksum};
    }

    try (InputStream is = inputStream) {
      byte[] buffer;
      switch (algoUpper) {
        case "MD2":
          buffer = DigestUtils.md2(is);
          break;
        case "MD5":
          buffer = DigestUtils.md5(is);
          break;
        case "SHA":
        case "SHA1":
          buffer = DigestUtils.sha1(is);
          break;
        case "SHA384":
          buffer = DigestUtils.sha384(is);
          break;
        case "SHA256":
          buffer = DigestUtils.sha256(is);
          break;
        case "SHA512":
          buffer = DigestUtils.sha512(is);
          break;
        case "SHA3-224":
          buffer = DigestUtils.sha3_224(is);
          break;
        case "SHA3-256":
          buffer = DigestUtils.sha3_256(is);
          break;
        case "SHA3-384":
          buffer = DigestUtils.sha3_384(is);
          break;
        case "SHA3-512":
          buffer = DigestUtils.sha3_512(is);
          break;
        default:
          throw new NoSuchAlgorithmException("Unsupported algorithm: " + algorithmInput);
      }
      return buffer;
    }
  }

  public static BigInteger hash(InputStreamExpr stream, String algorithmInput)
      throws IOException, NoSuchAlgorithmException {
    InputStream newInputStream = stream.toData();
    return hash(newInputStream, algorithmInput);
  }

  public Hash() {}

  /**
   * The digest, written the way the second-or-third argument asks for it.
   *
   * <p>
   * Wolfram's formats: an integer by default, a decimal or hexadecimal string zero-padded to the
   * width of the digest, a base-36 string, or the raw bytes.
   */
  private static IExpr formatHash(byte[] digest, String format) {
    BigInteger value = new BigInteger(1, digest);
    switch (format) {
      case "Integer":
        return F.ZZ(value);
      case "DecimalString":
        // the widest decimal a digest of this many bytes can reach
        int decimalDigits = new BigInteger(1, fullBytes(digest.length)).toString().length();
        return F.$str(padLeft(value.toString(), decimalDigits));
      case "HexString":
        return F.$str(padLeft(value.toString(16), digest.length * 2));
      case "Base36String":
        int base36Digits = new BigInteger(1, fullBytes(digest.length)).toString(36).length();
        return F.$str(padLeft(value.toString(36), base36Digits));
      case "ByteArray":
        return ByteArrayExpr.newInstance(digest);
      default:
        return F.NIL;
    }
  }

  private static byte[] fullBytes(int length) {
    byte[] max = new byte[length];
    java.util.Arrays.fill(max, (byte) 0xFF);
    return max;
  }

  private static String padLeft(String digits, int width) {
    if (digits.length() >= width) {
      return digits;
    }
    StringBuilder buf = new StringBuilder(width);
    for (int i = digits.length(); i < width; i++) {
      buf.append('0');
    }
    return buf.append(digits).toString();
  }

  /**
   * An input stream over whatever the first argument holds, or <code>null</code> if it holds
   * nothing which can be hashed byte by byte.
   */
  private static InputStream openStream(IExpr arg1) throws IOException {
    if (arg1 instanceof IStringX) {
      return IOUtils.toInputStream(arg1.toString(), StandardCharsets.UTF_8);
    }
    if (arg1 instanceof ByteArrayExpr) {
      return new ByteArrayInputStream(((ByteArrayExpr) arg1).toData());
    }
    if (arg1 instanceof FileExpr) {
      return Files.newInputStream(((FileExpr) arg1).toData().toPath());
    }
    if (arg1 instanceof InputStreamExpr) {
      return ((InputStreamExpr) arg1).toData();
    }
    return null;
  }

  /** Distribute the 32 bits of a hash code over 64 bits (the finalizer of SplitMix64). */
  private static long spread(int hashCode) {
    return mix64((hashCode & 0xFFFFFFFFL) + 0x9E3779B97F4A7C15L);
  }

  /** The finalizer of SplitMix64. */
  private static long mix64(long z) {
    z = (z ^ (z >>> 30)) * 0xBF58476D1CE4E5B9L;
    z = (z ^ (z >>> 27)) * 0x94D049BB133111EBL;
    return z ^ (z >>> 31);
  }

  /**
   * A 64-bit hash of the whole structure of <code>expr</code>: the head and every argument, and for
   * an association every key and value. {@link Object#hashCode()} of a large expression looks at
   * part of it only, which is right for hash tables but makes two lists which differ in a late
   * element hash alike - and a program which keys a cache on the hash of a list then mistakes one
   * for the other.
   */
  public static long expressionHash(IExpr expr) {
    if (expr.isAssociation()) {
      IAssociation association = (IAssociation) expr;
      long h = 0x243F6A8885A308D3L;
      for (int i = 1; i < association.size(); i++) {
        h = mix64(h * 0x100000001B3L ^ expressionHash(association.getRule(i)));
      }
      return h;
    }
    if (expr instanceof IAST) {
      IAST ast = (IAST) expr;
      long h = mix64(0x13198A2E03707344L ^ expressionHash(ast.head()));
      for (int i = 1; i < ast.size(); i++) {
        h = mix64(h * 0x100000001B3L ^ expressionHash(ast.get(i)));
      }
      return mix64(h ^ ast.argSize());
    }
    return spread(expr.hashCode());
  }

  @Override
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    IExpr arg1 = ast.arg1();
    if (!Config.FILESYSTEM_ENABLED && (arg1 instanceof FileExpr || arg1 instanceof InputStreamExpr)) {
      // Only what is read from the system is refused; an expression is hashed in memory.
      // The operation `1` is not allowed in sandbox mode.
      return Errors.printMessage(S.Hash, "sandbox", F.List(S.Hash));
    }
    String algorithm = "Expression";
    if (ast.size() >= 3) {
      IExpr arg2 = ast.arg2();
      if (arg2 instanceof IStringX) {
        algorithm = arg2.toString();
      } else {
        // `1` is not a valid Hash specification.
        return Errors.printMessage(S.Hash, "invhash", F.List(arg2), engine);
      }
    }
    String format = "Integer";
    if (ast.size() >= 4) {
      IExpr arg3 = ast.arg3();
      if (arg3 instanceof IStringX) {
        format = arg3.toString();
      } else {
        // `1` is not a valid Hash specification.
        return Errors.printMessage(S.Hash, "invhash", F.List(arg3), engine);
      }
    }
    if (algorithm.equals("Expression")) {
      // a non-negative 64-bit value, so that the string formats have a fixed width
      long h = expressionHash(arg1);
      byte[] bytes = new byte[8];
      for (int i = 7; i >= 0; i--) {
        bytes[i] = (byte) h;
        h >>>= 8;
      }
      IExpr formatted = formatHash(bytes, format);
      if (formatted.isNIL()) {
        // Invalid hash format `1`.
        return Errors.printMessage(S.Hash, "uform", F.List(F.$str(format)), engine);
      }
      return formatted;
    }
    try {
      InputStream inputStream = openStream(arg1);
      if (inputStream == null) {
        return F.NIL;
      }
      byte[] digest = hashBytes(inputStream, algorithm);
      if (digest == null) {
        return F.NIL;
      }
      IExpr formatted = formatHash(digest, format);
      if (formatted.isNIL()) {
        // Invalid hash format `1`.
        return Errors.printMessage(S.Hash, "uform", F.List(F.$str(format)), engine);
      }
      return formatted;
    } catch (NoSuchAlgorithmException nsae) {
      // `1` is not a known hash code.
      return Errors.printMessage(S.Hash, "hshtype", F.List(F.$str(algorithm)), engine);
    } catch (IOException ioe) {
      return Errors.printMessage(S.Hash, ioe, engine);
    }
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_3;
  }

}
