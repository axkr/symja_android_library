package org.matheclipse.core.expression.data;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.Externalizable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;
import com.google.common.io.CharStreams;

/**
 * Represents an input stream wrapper.
 *
 * <p>
 * This class extends {@link DataExpr} parametrized with {@link InputStream} and implements
 * {@link Externalizable} so streams can be serialized/deserialized when required.
 *
 * <p>
 * The class provides support for both binary reads (via {@link DataInput}) and text reads (via
 * {@link Reader}). It also maintains a unique ID and an optional stream name for debugging or
 * identification purposes.
 */
public class InputStreamExpr extends DataExpr<InputStream> implements Externalizable {
  /**
   * Global counter used to assign a unique id to each stream instance.
   */
  public static AtomicInteger STREAM_COUNTER = new AtomicInteger(1);

  /**
   * Unique identifier for this stream instance.
   */
  private final int uniqueID;

  /**
   * Human readable name for the stream (for example, file name or "String").
   */
  private final String streamName;

  /**
   * Returns a {@link DataInput} for binary reads. If a {@link Reader} was previously created it is
   * closed and discarded because binary access and character access share the same underlying
   * stream resource.
   *
   * @return a {@link DataInput} view of the underlying stream
   * @throws IOException if creating or closing resources fails
   */
  private DataInput dataIn;

  /**
   * Reader for text based operation {@link S#Read}
   */
  private Reader reader;

  /**
   * Returns a {@link DataInput} for binary reads. If a {@link Reader} was previously created it is
   * closed and discarded because binary access and character access share the same underlying
   * stream resource.
   *
   * @return a {@link DataInput} view of the underlying stream
   * @throws IOException if creating or closing resources fails
   */
  public DataInput getDataInput() throws IOException {
    if (dataIn == null) {
      dataIn = new DataInputStream(fData);
      if (reader != null) {
        reader.close();
        reader = null;
      }
    }
    return dataIn;
  }

  /**
   * Returns a {@link Reader} for text reads. The method reads all remaining bytes from the
   * underlying stream using UTF-8 encoding into a string and wraps it in a {@link StringReader}.
   * Any previously created {@link DataInput} is discarded because character and binary access share
   * the same underlying {@link InputStream}.
   *
   * @return a {@link Reader} for reading text content
   * @throws IOException if reading from the underlying stream fails
   */
  public Reader getReader() throws IOException {
    if (reader == null) {
      String str = new String(fData.readAllBytes(), StandardCharsets.UTF_8);
      if (dataIn != null) {
        dataIn = null;
      }
      reader = new StringReader(str);
    }
    return reader;
  }

  public InputStreamExpr() {
    super(S.InputStream, null);
    uniqueID = STREAM_COUNTER.getAndIncrement();
    streamName = "String";
  }

  /**
   * Creates or returns a remembered {@link InputStreamExpr} for the given {@link FileExpr}.
   *
   * <p>
   * If a remembered stream exists for the file it is returned; otherwise a new
   * {@link InputStreamExpr} wrapping a {@link FileInputStream} is created and stored via the
   * provided {@link EvalEngine}.
   *
   * @param fileExpr the file expression to open
   * @param streamName the desired name of the stream (for identification)
   * @param engine the evaluation engine used to remember streams
   * @return an {@link InputStreamExpr} associated with the file
   * @throws FileNotFoundException if the file cannot be opened
   */
  public static InputStreamExpr getFromFile(final FileExpr fileExpr, String streamName,
      EvalEngine engine) throws FileNotFoundException {
    File file = fileExpr.toData();
    IExpr temp = engine.getRemember(file);
    if (temp == null || !(temp instanceof InputStreamExpr)) {
      // don't close FileInputStream here
      InputStreamExpr stream = new InputStreamExpr(new FileInputStream(file));
      engine.putRememberMap(file, stream);
      return stream;
    }
    return (InputStreamExpr) temp;
  }

  /**
   * Creates a new {@link InputStreamExpr} from a file name.
   *
   * @param fileName the file path to open
   * @param streamName the name assigned to the created stream
   * @return a new {@link InputStreamExpr} wrapping the file input stream
   * @throws FileNotFoundException if the file does not exist
   */
  public static InputStreamExpr newInstance(final String fileName, String streamName)
      throws FileNotFoundException {
    File file = new File(fileName);
    return new InputStreamExpr(new FileInputStream(file), streamName);
  }

  /**
   * Creates a new {@link InputStreamExpr} from a {@link File} and an optional {@link Reader}.
   *
   * <p>
   * If a {@link Reader} is provided it will be stored; otherwise only the underlying
   * {@link InputStream} is kept.
   *
   * @param file the file to open
   * @param streamName the name assigned to the created stream
   * @param reader an optional reader representing textual access to the file
   * @return a new {@link InputStreamExpr} wrapping the file input stream
   * @throws FileNotFoundException if the file cannot be opened
   */
  public static InputStreamExpr newInstance(final File file, String streamName, final Reader reader)
      throws FileNotFoundException {
    return new InputStreamExpr(new FileInputStream(file), streamName, reader);
  }

  /**
   * Creates a new {@link InputStreamExpr} from a {@link Reader}. The reader content is copied into
   * an in-memory {@link ByteArrayInputStream} using UTF-8 encoding. The supplied reader is then
   * reset (caller must ensure the reader supports reset).
   *
   * @param reader the reader whose entire content will be captured
   * @return a new {@link InputStreamExpr} backed by an in-memory byte array
   * @throws IOException if reading from or resetting the reader fails
   */
  public static InputStreamExpr newInstance(final Reader reader) throws IOException {
    InputStream targetStream =
        new ByteArrayInputStream(CharStreams.toString(reader).getBytes(StandardCharsets.UTF_8));
    reader.reset();
    return new InputStreamExpr(targetStream, "String", reader);
  }


  /**
   * Protected constructor used to create a stream wrapper with default name {@code "String"}.
   *
   * @param value the underlying input stream
   */
  protected InputStreamExpr(final InputStream value) {
    this(value, "String");
  }

  /**
   * Protected constructor allowing to specify the stream name and an associated {@link Reader}.
   *
   * @param value the underlying input stream
   * @param streamName the name assigned to this stream
   * @param reader an associated reader (may be {@code null})
   */
  protected InputStreamExpr(final InputStream value, String streamName, final Reader reader) {
    super(S.InputStream, value);
    this.uniqueID = STREAM_COUNTER.getAndIncrement();
    this.streamName = streamName;
    this.reader = reader;
  }

  /**
   * Protected constructor allowing to specify the stream name.
   *
   * @param value the underlying input stream
   * @param streamName the name assigned to this stream
   */
  protected InputStreamExpr(final InputStream value, String streamName) {
    super(S.InputStream, value);
    this.uniqueID = STREAM_COUNTER.getAndIncrement();
    this.streamName = streamName;
  }

  /**
   * Two streams are considered equal if their underlying {@link InputStream} objects are equal.
   *
   * @param obj the object to compare with
   * @return {@code true} if the other object is the same instance or wraps an equal input stream
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof InputStreamExpr) {
      return fData.equals(((InputStreamExpr) obj).fData);
    }
    return false;
  }

  /**
   * Returns the human readable stream name.
   *
   * @return the stream name
   */
  public String getStreamName() {
    return streamName;
  }

  @Override
  public int hashCode() {
    return (fData == null) ? 353 : 353 + fData.hashCode();
  }

  @Override
  public int hierarchy() {
    return INPUTSTREAMEXPRID;
  }

  /**
   * Closes any associated {@link Reader} and the underlying {@link InputStream}.
   *
   * @throws IOException if closing the resources fails
   */
  public void close() throws IOException {
    if (reader != null) {
      reader.close();
      reader = null;
    }
    fData.close();
  }

  /**
   * Creates a shallow copy of this expression. The returned instance wraps the same underlying
   * {@link InputStream} reference.
   *
   * @return a new {@link InputStreamExpr} that wraps the same input stream
   */
  @Override
  public IExpr copy() {
    return new InputStreamExpr(fData);
  }

  @Override
  public String toString() {
    return fHead + "[" + "Name: " + streamName + " Unique-ID: " + uniqueID + "]";
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    fData = (InputStream) in.readObject();
  }

  @Override
  public void writeExternal(ObjectOutput output) throws IOException {
    output.writeObject(fData);
  }
}
