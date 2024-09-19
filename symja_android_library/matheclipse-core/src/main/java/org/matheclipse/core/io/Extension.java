package org.matheclipse.core.io;

import org.matheclipse.core.basic.OperationSystem;
import org.matheclipse.core.eval.Errors;

import java.util.Locale;

/**
 * File extensions format.
 *
 * <p>
 * See Symja's <code>Import()</code> and <code>Export()</code> functions.
 */
public enum Extension {
  /** Base64 encoding, decoding */
  BASE64,
  /** uncompressed loss-less image format, no alpha channel */
  BMP, //
  /** table */
  CSV, //
  /** Data */
  DAT, //
  /** graph (theory) format */
  DOT, //
  /** ExpressionJSON */
  EXPRESSIONJSON, //
  /** animation format */
  GIF, //
  /** graph (theory) format */
  GRAPHML, //
  /** compressed version of another format, for instance csv.gz */
  GZ, //
  /** compressed, lossy image format */
  JPEG, //
  /** JSON */
  JSON, //
  /** Mathematica *.m file */
  M, //
  /** MATLAB *.mat file */
  MAT, //
  /** compressed image format with alpha channel */
  PNG, //
  /** RawJSON */
  RAWJSON, //
  /** text format */
  STRING,
  /** table format */
  TABLE,
  /** Tab-Separated Values */
  TSV, //
  /** plain text format */
  TXT,
  /** WXF format */
  WXF;

  public static boolean isAllowedExtension(String extensionString) {
    try {
      Extension ext = valueOf(extensionString.toUpperCase(Locale.US));
      if (ext != null) {
        return true;
      }
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    }
    return false;
  }

  /**
   * Determine <code>Extension</code> format from the filename's extension. If no <code>Extension
   * </code> can be determined return <code>DAT</code>.
   *
   * @param filename
   * @return
   */
  public static Extension exportFilename(String filename) {
    try {
      int pos = filename.lastIndexOf('.');
      if (pos >= 1) {
        String ucExtension = filename.substring(pos + 1).toUpperCase(Locale.US);
        if (ucExtension.equals("DATA")) {
          return DAT;
        }
        if (ucExtension.equals("ExpressionJSON")) {
          return EXPRESSIONJSON;
        }
        if (ucExtension.equals("JPG")) {
          return JPEG;
        }
        return valueOf(ucExtension);
      }
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    }
    return DAT;
  }

  /**
   * Determine <code>Extension</code> format from the extension string. If no <code>Extension</code>
   * can be determined return <code>DAT</code>.
   *
   * @param extensionString
   * @return
   */
  public static Extension exportExtension(String extensionString) {
    try {
      if (extensionString.equals("ExpressionJSON")) {
        return EXPRESSIONJSON;
      }
      String ucExtension = extensionString.toUpperCase(Locale.US);
      if (ucExtension.equals("DATA")) {
        return DAT;
      }
      if (ucExtension.equals("JPG")) {
        return JPEG;
      }
      return valueOf(extensionString.toUpperCase(Locale.US));
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    }
    return DAT;
  }

  /**
   * Determine <code>Extension</code> format from the filename's extension. If no <code>Extension
   * </code> can be determined return <code>STRING</code>.
   *
   * @param filename
   * @return
   */
  public static Extension importFilename(String filename) {
    try {
      int pos = filename.lastIndexOf('.');
      if (pos >= 1) {
        String extensionString = filename.substring(pos + 1).toUpperCase(Locale.US);
        if (extensionString.equals("TEXT")) {
          return TXT;
        }
        if (extensionString.equals("JPG")) {
          return JPEG;
        }
        if (extensionString.equals("JSON")) {
          return JSON;
        }
        return valueOf(extensionString);
      }
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    }
    return STRING;
  }

  /**
   * Determine <code>Extension</code> format from the extension string. If no <code>Extension</code>
   * can be determined return <code>STRING</code>.
   *
   * @param extensionString
   * @return
   */
  public static Extension importExtension(String extensionString) {
    try {
      return valueOf(extensionString.toUpperCase(Locale.US));
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    }
    if (extensionString.equals("Text")) {
      return TXT;
    }
    if (extensionString.equals("JPG")) {
      return JPEG;
    }
    return STRING;
  }

  /**
   * @param string
   * @return
   * @throws IllegalArgumentException if given string does not match any known file types
   */
  public static Extension of(String string) {
    return valueOf(string.toUpperCase(Locale.US));
  }
}
