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
	int fStartOffset;

	/**
	 * row index where the error occurred2
	 */
	int fRowIndex;

	/**
	 * column index where the error occurred (offset relative to rowIndex)
	 */
	int fColumnIndex;

	/**
	 * length of the error
	 */
	int fLength;

	String fCurrentLine;

	String fError;

	/**
	 * SyntaxError exception
	 *
	 * @param startOffset
	 * @param length
	 *
	 * @see
	 */
	public SyntaxError(final int startOffset, final int rowIndx, final int columnIndx, final String currentLine, final String error, final int length) {
		fStartOffset = startOffset;
		fRowIndex = rowIndx;
		fColumnIndex = columnIndx;
		fCurrentLine = currentLine;
		fError = error;
		fLength = length;
	}

	public String getMessage() {
		final StringBuffer buf = new StringBuffer(256);
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
	 * offset where the error occurred
	 */
	public int getStartOffset() {
		return fStartOffset;
	}

	/**
	 * column index where the error occurred (offset relative to rowIndex)
	 */
	public int getColumnIndex() {
		return fColumnIndex;
	}

	/**
	 * source code line, where the error occurred
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

	/**
	 * row index where the error occurred
	 */
	public int getRowIndex() {
		return fRowIndex;
	}
}
