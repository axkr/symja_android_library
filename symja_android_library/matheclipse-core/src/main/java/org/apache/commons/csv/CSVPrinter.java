/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.csv;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.apache.commons.csv.Constants.CR;
import static org.apache.commons.csv.Constants.LF;
import static org.apache.commons.csv.Constants.SP;

/**
 * Prints values in a CSV format.
 */
public final class CSVPrinter implements Flushable, Closeable {

    /** The place that the values get written. */
    private final Appendable out;
    private final CSVFormat format;

    /** True if we just began a new record. */
    private boolean newRecord = true;

    /**
     * Creates a printer that will print values to the given stream following the CSVFormat.
     * <p>
     * Currently, only a pure encapsulation format or a pure escaping format is supported. Hybrid formats (encapsulation
     * and escaping with a different character) are not supported.
     * </p>
     *
     * @param out
     *            stream to which to print. Must not be null.
     * @param format
     *            the CSV format. Must not be null.
     * @throws IOException
     *             thrown if the optional header cannot be printed.
     * @throws IllegalArgumentException
     *             thrown if the parameters of the format are inconsistent or if either out or format are null.
     */
    public CSVPrinter(final Appendable out, final CSVFormat format) throws IOException {
        Assertions.notNull(out, "out");
        Assertions.notNull(format, "format");

        this.out = out;
        this.format = format;
        // TODO: Is it a good idea to do this here instead of on the first call to a print method?
        // It seems a pain to have to track whether the header has already been printed or not.
        if (format.getHeaderComments() != null) {
            for (final String line : format.getHeaderComments()) {
                if (line != null) {
                    this.printComment(line);
                }
            }
        }
        if (format.getHeader() != null && !format.getSkipHeaderRecord()) {
            this.printRecord((Object[]) format.getHeader());
        }
    }

    // ======================================================
    // printing implementation
    // ======================================================

    @Override
    public void close() throws IOException {
        if (out instanceof Closeable) {
            ((Closeable) out).close();
        }
    }

    /**
     * Flushes the underlying stream.
     *
     * @throws IOException
     *             If an I/O error occurs
     */
    @Override
    public void flush() throws IOException {
        if (out instanceof Flushable) {
            ((Flushable) out).flush();
        }
    }

    /**
     * Gets the target Appendable.
     *
     * @return the target Appendable.
     */
    public Appendable getOut() {
        return this.out;
    }

    /**
     * Prints the string as the next value on the line. The value will be escaped or encapsulated as needed.
     *
     * @param value
     *            value to be output.
     * @throws IOException
     *             If an I/O error occurs
     */
    public void print(final Object value) throws IOException {
        format.print(value, out, newRecord);
        newRecord = false;
    }

    /**
     * Prints a comment on a new line among the delimiter separated values.
     *
     * <p>
     * Comments will always begin on a new line and occupy a least one full line. The character specified to start
     * comments and a space will be inserted at the beginning of each new line in the comment.
     * </p>
     *
     * If comments are disabled in the current CSV format this method does nothing.
     *
     * @param comment
     *            the comment to output
     * @throws IOException
     *             If an I/O error occurs
     */
    public void printComment(final String comment) throws IOException {
        if (!format.isCommentMarkerSet()) {
            return;
        }
        if (!newRecord) {
            println();
        }
        out.append(format.getCommentMarker().charValue());
        out.append(SP);
        for (int i = 0; i < comment.length(); i++) {
            final char c = comment.charAt(i);
            switch (c) {
            case CR:
                if (i + 1 < comment.length() && comment.charAt(i + 1) == LF) {
                    i++;
                }
                //$FALL-THROUGH$ break intentionally excluded.
            case LF:
                println();
                out.append(format.getCommentMarker().charValue());
                out.append(SP);
                break;
            default:
                out.append(c);
                break;
            }
        }
        println();
    }

    /**
     * Outputs the record separator.
     *
     * @throws IOException
     *             If an I/O error occurs
     */
    public void println() throws IOException {
        format.println(out);
        newRecord = true;
    }

    /**
     * Prints the given values a single record of delimiter separated values followed by the record separator.
     *
     * <p>
     * The values will be quoted if needed. Quotes and newLine characters will be escaped. This method adds the record
     * separator to the output after printing the record, so there is no need to call {@link #println()}.
     * </p>
     *
     * @param values
     *            values to output.
     * @throws IOException
     *             If an I/O error occurs
     */
    public void printRecord(final Iterable<?> values) throws IOException {
        for (final Object value : values) {
            print(value);
        }
        println();
    }

    /**
     * Prints the given values a single record of delimiter separated values followed by the record separator.
     *
     * <p>
     * The values will be quoted if needed. Quotes and newLine characters will be escaped. This method adds the record
     * separator to the output after printing the record, so there is no need to call {@link #println()}.
     * </p>
     *
     * @param values
     *            values to output.
     * @throws IOException
     *             If an I/O error occurs
     */
    public void printRecord(final Object... values) throws IOException {
        format.printRecord(out, values);
        newRecord = true;
    }

    /**
     * Prints all the objects in the given collection handling nested collections/arrays as records.
     *
     * <p>
     * If the given collection only contains simple objects, this method will print a single record like
     * {@link #printRecord(Iterable)}. If the given collections contains nested collections/arrays those nested elements
     * will each be printed as records using {@link #printRecord(Object...)}.
     * </p>
     *
     * <p>
     * Given the following data structure:
     * </p>
     *
     * <pre>
     * <code>
     * List&lt;String[]&gt; data = ...
     * data.add(new String[]{ "A", "B", "C" });
     * data.add(new String[]{ "1", "2", "3" });
     * data.add(new String[]{ "A1", "B2", "C3" });
     * </code>
     * </pre>
     *
     * <p>
     * Calling this method will print:
     * </p>
     *
     * <pre>
     * <code>
     * A, B, C
     * 1, 2, 3
     * A1, B2, C3
     * </code>
     * </pre>
     *
     * @param values
     *            the values to print.
     * @throws IOException
     *             If an I/O error occurs
     */
    public void printRecords(final Iterable<?> values) throws IOException {
        for (final Object value : values) {
            if (value instanceof Object[]) {
                this.printRecord((Object[]) value);
            } else if (value instanceof Iterable) {
                this.printRecord((Iterable<?>) value);
            } else {
                this.printRecord(value);
            }
        }
    }

    /**
     * Prints all the objects in the given array handling nested collections/arrays as records.
     *
     * <p>
     * If the given array only contains simple objects, this method will print a single record like
     * {@link #printRecord(Object...)}. If the given collections contains nested collections/arrays those nested
     * elements will each be printed as records using {@link #printRecord(Object...)}.
     * </p>
     *
     * <p>
     * Given the following data structure:
     * </p>
     *
     * <pre>
     * <code>
     * String[][] data = new String[3][]
     * data[0] = String[]{ "A", "B", "C" };
     * data[1] = new String[]{ "1", "2", "3" };
     * data[2] = new String[]{ "A1", "B2", "C3" };
     * </code>
     * </pre>
     *
     * <p>
     * Calling this method will print:
     * </p>
     *
     * <pre>
     * <code>
     * A, B, C
     * 1, 2, 3
     * A1, B2, C3
     * </code>
     * </pre>
     *
     * @param values
     *            the values to print.
     * @throws IOException
     *             If an I/O error occurs
     */
    public void printRecords(final Object... values) throws IOException {
        for (final Object value : values) {
            if (value instanceof Object[]) {
                this.printRecord((Object[]) value);
            } else if (value instanceof Iterable) {
                this.printRecord((Iterable<?>) value);
            } else {
                this.printRecord(value);
            }
        }
    }

    /**
     * Prints all the objects in the given JDBC result set.
     *
     * @param resultSet
     *            result set the values to print.
     * @throws IOException
     *             If an I/O error occurs
     * @throws SQLException
     *             if a database access error occurs
     */
    public void printRecords(final ResultSet resultSet) throws SQLException, IOException {
        final int columnCount = resultSet.getMetaData().getColumnCount();
        while (resultSet.next()) {
            for (int i = 1; i <= columnCount; i++) {
                print(resultSet.getObject(i));
            }
            println();
        }
    }
}
