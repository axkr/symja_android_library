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
      return BigInteger.valueOf(crc32.getValue());
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
          buffer = DigestUtils.sha1(is);
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
        case "SHA3-5124":
          buffer = DigestUtils.sha3_512(is);
          break;
        default:
          throw new NoSuchAlgorithmException("Unsupported algorithm: " + algorithmInput);
      }
      if (buffer == null) {
        return null;
      }
      return new BigInteger(1, buffer);
    }
  }

  public static BigInteger hash(InputStreamExpr stream, String algorithmInput)
      throws IOException, NoSuchAlgorithmException {
    InputStream newInputStream = stream.toData();
    return hash(newInputStream, algorithmInput);
  }

  public Hash() {}

  @Override
  public IExpr evaluate(IAST ast, EvalEngine engine) {
    if (!Config.FILESYSTEM_ENABLED) {
      // The operation `1` is not allowed in sandbox mode.
      return Errors.printMessage(S.Hash, "sandbox", F.List(S.Hash));
    }
    IExpr arg1 = ast.arg1();
    String algorithm = "Expression";
    if (ast.isAST2()) {
      IExpr arg2 = ast.arg2();
      if (arg2 instanceof IStringX) {
        algorithm = arg2.toString();
      } else {
        return F.NIL;
      }
    }
    if (algorithm.equals("Expression")) {
      int hashCode = arg1.hashCode();
      return F.ZZ(hashCode);
    }
    if (arg1 instanceof IStringX) {
      try {
        InputStream newInputStream = IOUtils.toInputStream(arg1.toString(), StandardCharsets.UTF_8);
        BigInteger hashValue = hash(newInputStream, algorithm);
        if (hashValue != null) {
          return F.ZZ(hashValue);
        }
      } catch (Exception e) {
        e.printStackTrace();
        return F.NIL;
      }
    }
    if (arg1 instanceof ByteArrayExpr) {
      try {
        BigInteger hashValue = hash((ByteArrayExpr) arg1, algorithm);
        if (hashValue != null) {
          return F.ZZ(hashValue);
        }
      } catch (Exception e) {
        e.printStackTrace();
        return F.NIL;
      }
    }
    if (arg1 instanceof FileExpr) {
      try {
        BigInteger hashValue = hash((FileExpr) arg1, algorithm);
        if (hashValue != null) {
          return F.ZZ(hashValue);
        }
      } catch (Exception e) {
        e.printStackTrace();
        return F.NIL;
      }
    }
    if (arg1 instanceof InputStreamExpr) {
      try {
        BigInteger hashValue = hash((InputStreamExpr) arg1, algorithm);
        if (hashValue != null) {
          return F.ZZ(hashValue);
        }
      } catch (Exception e) {
        e.printStackTrace();
        return F.NIL;
      }
    }
    return F.NIL;
  }

  @Override
  public int[] expectedArgSize(IAST ast) {
    return ARGS_1_2;
  }

}
