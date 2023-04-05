package org.matheclipse.core.form.tex;

import org.matheclipse.parser.client.math.MathException;

/** Exception for a syntax error detected by the Symja parsers */
public class TeXSyntaxError extends MathException {

  private static final long serialVersionUID = 1849387697719679119L;

  /** offset where the error occurred */
  private final int fStartOffset;

  /** row index where the error occurred2 */
  private final int fRowIndex;

  /** column index where the error occurred (offset relative to rowIndex) */
  private final int fColumnIndex;

  /** length of the error */
  private final int fLength;

  private final String fCurrentLine;

  private final String fError;

  /**
   * SyntaxError exception
   *
   * @param startOffset the start offset inside the row
   * @param rowIndx the row index
   * @param columnIndx
   * @param currentLine
   * @param error
   * @param length
   */
  public TeXSyntaxError(final int startOffset, final int rowIndx, final int columnIndx,
      final String currentLine, final String error, final int length) {
    fStartOffset = startOffset;
    fRowIndex = rowIndx;
    fColumnIndex = columnIndx;
    fCurrentLine = currentLine;
    fError = error;
    fLength = length;
  }

  /**
   * Column index where the error occurred (offset relative to rowIndex)
   *
   * @return the index where the error occurred.
   */
  public int getColumnIndex() {
    return fColumnIndex;
  }

  /**
   * Source code line, where the error occurred
   *
   * @return line, where the error occurred
   */
  public String getCurrentLine() {
    return fCurrentLine;
  }

  /** the error string */
  public String getError() {
    return fError;
  }

  /** length of the error */
  public int getLength() {
    return fLength;
  }

  @Override
  public String getMessage() {
    final StringBuilder buf = new StringBuilder(256);
    buf.append("Syntax error in line: ");
    buf.append(fRowIndex + 1);
    buf.append(" - " + fError + "\n");
    buf.append(fCurrentLine + "\n");
    for (int i = 0; i < (fColumnIndex - 1); i++) {
      buf.append(' ');
    }
    buf.append('^');
    return buf.toString();
  }

  /** row index where the error occurred */
  public int getRowIndex() {
    return fRowIndex;
  }

  /** offset where the error occurred */
  public int getStartOffset() {
    return fStartOffset;
  }
}
