package org.matheclipse.core.io;

import java.util.Locale;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.interfaces.IExpr;

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
  /** Windows icon image format */
  ICO, //
  /** table */
  CSV, //
  /** Data */
  DAT, //
  /** graph (theory) format */
  DOT, //
  /** ExpressionJSON */
  EXPRESSIONJSON, //
  /** FASTA biomolecular sequence format */
  FASTA, //
  /** GenBank biomolecular sequence format */
  GENBANK, //
  /** animation format */
  GIF, //
  /** graph (theory) format */
  GRAPHML, //
  /** undirected graphs as printable ASCII adjacency bit vectors, one graph per line */
  GRAPH6, //
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
  /** Portable Any/Bit/Gray/Pixmap image formats */
  PNM, //
  /** compressed image format with alpha channel */
  PNG, //
  /** Adobe Photoshop image format */
  PSD, //
  /** RawJSON */
  RAWJSON, //
  /** text format */
  STRING,
  /** structured vector graphics format */
  SVG,
  /** table format */
  TABLE,
  /** Truevision TGA image format */
  TGA, //
  /** loss-less image format with several compression schemes */
  TIFF, //
  /** Tab-Separated Values */
  TSV, //
  /** plain text format */
  TXT,
  /** compressed image format from Google */
  WEBP, //
  /** WXF format */
  WXF,
  /**
   * Excel workbook. Read only - the vendored Tablesaw fork in <code>matheclipse-dataset</code> has
   * an <code>XlsxReader</code> but no writer.
   */
  XLSX, //
  /** ZIP archive: several files in one, which is how a paclet is shipped */
  ZIP;

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
        if (ucExtension.equals("TIF")) {
          return TIFF;
        }
        if (ucExtension.equals("PBM") || ucExtension.equals("PGM") || ucExtension.equals("PPM")) {
          return PNM;
        }
        if (ucExtension.equals("XLS")) {
          return XLSX;
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
      if (ucExtension.equals("TEXT")) {
        // "Text" is what the script language calls this format; TXT is the file name it uses
        return TXT;
      }
      if (ucExtension.equals("JPG")) {
        return JPEG;
      }
      if (ucExtension.equals("TIF")) {
        return TIFF;
      }
      if (ucExtension.equals("PBM") || ucExtension.equals("PGM") || ucExtension.equals("PPM")) {
        return PNM;
      }
      if (ucExtension.equals("XLS")) {
        return XLSX;
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
        if (extensionString.equals("G6")) {
          return GRAPH6;
        }
        if (extensionString.equals("TIF")) {
          return TIFF;
        }
        if (extensionString.equals("PBM") || extensionString.equals("PGM")
            || extensionString.equals("PPM")) {
          return PNM;
        }
        if (extensionString.equals("XLS")) {
          return XLSX;
        }
        return valueOf(extensionString);
      }
    } catch (RuntimeException rex) {
      Errors.rethrowsInterruptException(rex);
    }
    return STRING;
  }

  /** A format argument of <code>Import</code> or <code>ImportString</code>. */
  public static final class ImportSpec {
    public final Extension format;

    /** The element of the format to import, <code>""</code> for its default element. */
    public final String element;

    private ImportSpec(Extension format, String element) {
      this.format = format;
      this.element = element;
    }
  }

  /**
   * The format and the element of a format argument <code>"format"</code> or
   * <code>{"format", "element"}</code> of <code>Import</code> or <code>ImportString</code>.
   *
   * @return <code>null</code> if the argument has another form or the format has no elements
   */
  public static ImportSpec importSpec(IExpr formatArg) {
    String element = "";
    if (formatArg.isList2() && formatArg.first().isString() && formatArg.second().isString()) {
      element = formatArg.second().toString();
      formatArg = formatArg.first();
    }
    if (!formatArg.isString()) {
      return null;
    }
    Extension format = importExtension(formatArg.toString());
    if (!element.isEmpty() && format != GRAPH6) {
      // only Graph6 has elements
      return null;
    }
    return new ImportSpec(format, element);
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
      String ucExtension = extensionString.toUpperCase(Locale.US);
      if (ucExtension.equals("G6")) {
        return GRAPH6;
      }
      if (ucExtension.equals("JPG")) {
        return JPEG;
      }
      if (ucExtension.equals("TIF")) {
        return TIFF;
      }
      if (ucExtension.equals("PBM") || ucExtension.equals("PGM") || ucExtension.equals("PPM")) {
        return PNM;
      }
      if (ucExtension.equals("XLS")) {
        return XLSX;
      }
      return valueOf(ucExtension);
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
