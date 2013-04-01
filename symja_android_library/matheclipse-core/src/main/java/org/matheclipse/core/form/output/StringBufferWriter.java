package org.matheclipse.core.form.output;

import java.io.IOException;
import java.io.Writer;

/**
 * The same as StringWriter, but takes an existing StringBuffer in its
 * constructor.
 * 
 */
public class StringBufferWriter extends Writer {
	private boolean fIgnoreNewLine = false;

	private StringBuilder fBuffer;

	private int fColumnCounter;

	public StringBufferWriter() {
		this(16);
	}

	public StringBufferWriter(final int initialSize) {
		if (initialSize < 0) {
			throw new IllegalArgumentException("Negative buffer size");
		}
		fBuffer = new StringBuilder(initialSize);
		lock = fBuffer;
	}

	public StringBufferWriter(final StringBuilder buffer) {
		fBuffer = buffer;
		lock = buffer;
	}

	@Override
	public void close() throws IOException {
	}

	@Override
	public void flush() {
	}

	public StringBuilder getBuffer() {
		return fBuffer;
	}

	/**
	 * @return Returns the columnCounter.
	 */
	public int getColumnCounter() {
		return fColumnCounter;
	}

	/**
	 * this resets the columnCounter to offset 0
	 * 
	 */
	public void newLine() {
		if (!fIgnoreNewLine) {
			fBuffer.append('\n');
		}
		fColumnCounter = 0;
	}

	/**
	 * @param columnCounter
	 *          The columnCounter to set.
	 */
	public void setColumnCounter(final int columnCounter) {
		fColumnCounter = columnCounter;
	}

	@Override
	public String toString() {
		return fBuffer.toString();
	}

	@Override
	public void write(final char cbuf[], final int off, final int len) {
		if ((off < 0) || (off > cbuf.length) || (len < 0) || ((off + len) > cbuf.length) || ((off + len) < 0)) {
			throw new IndexOutOfBoundsException();
		} else if (len == 0) {
			return;
		}
		fBuffer.append(cbuf, off, len);
		fColumnCounter += len;
	}

	@Override
	public void write(final int c) {
		fBuffer.append((char) c);
		fColumnCounter++;
	}

	@Override
	public void write(final String str) {
		fBuffer.append(str);
		fColumnCounter += str.length();
	}

	@Override
	public void write(final String str, final int off, final int len) {
		fBuffer.append(str.substring(off, off + len));
		fColumnCounter += len;
	}

	/**
	 * @return Returns the ignoreNewLine.
	 */
	public boolean isIgnoreNewLine() {
		return fIgnoreNewLine;
	}

	/**
	 * Check if the buffer is empty.
	 * 
	 * @return
	 */
	public boolean isEmpty() {
		return fBuffer.length() == 0;
	}

	/**
	 * @param ignoreNewLine
	 *          The ignoreNewLine to set.
	 */
	public void setIgnoreNewLine(final boolean ignoreNewLine) {
		fIgnoreNewLine = ignoreNewLine;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		StringBufferWriter writer = (StringBufferWriter) super.clone();
		writer.fBuffer = new StringBuilder();
		writer.lock = fBuffer;
		writer.fIgnoreNewLine = fIgnoreNewLine;
		return writer;
	}

}
