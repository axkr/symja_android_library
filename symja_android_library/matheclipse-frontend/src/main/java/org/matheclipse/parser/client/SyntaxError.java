/*
 * Copyright 2005-2008 Axel Kramer (axelclk@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.matheclipse.parser.client;

import org.matheclipse.parser.client.math.MathException;

/**
 * Exception for a syntax error detected by the MathEclipse parser
 *
 */
public class SyntaxError extends MathException {

	/** 
	 *
	 */
	private static final long serialVersionUID = 1849387697719679119L;

	/**
	 * offset where the error occurred
	 */
	final int fStartOffset;

	/**
	 * row index where the error occurred2
	 */
	final int fRowIndex;

	/**
	 * column index where the error occurred (offset relative to rowIndex)
	 */
	final int fColumnIndex;

	/**
	 * length of the error
	 */
	final int fLength;

	final String fCurrentLine;

	final String fError;

	/**
	 * SyntaxError exception
	 *
	 * @param startOffset the start offset inside the row
	 * @param rowIndx the row index
	 * @param columnIndx 
	 * @param currentLine
	 * @param error
	 * @param length
	 *
	 */
	public SyntaxError(final int startOffset, final int rowIndx, final int columnIndx, final String currentLine,
			final String error, final int length) {
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

	/**
	 * the error string
	 */
	public String getError() {
		return fError;
	}

	/**
	 * length of the error
	 */
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

	/**
	 * row index where the error occurred
	 */
	public int getRowIndex() {
		return fRowIndex;
	}

	/**
	 * offset where the error occurred
	 */
	public int getStartOffset() {
		return fStartOffset;
	}
}
