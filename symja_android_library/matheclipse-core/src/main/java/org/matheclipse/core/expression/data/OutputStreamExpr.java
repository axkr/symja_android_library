package org.matheclipse.core.expression.data;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.Externalizable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.nio.channels.Channels;
import org.matheclipse.core.eval.Errors;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;

/**
 * Wrapper class for an {@link OutputStream}.
 * <p>
 * This class extends {@link DataExpr} parameterized with {@link OutputStream} and implements
 * {@link Externalizable} for custom serialization. It provides convenience methods to obtain a
 * {@link Writer} or {@link DataOutput} view of the underlying stream and factory methods to create
 * instances from files and random access files.
 * </p>
 */
public class OutputStreamExpr extends DataExpr<OutputStream> implements Externalizable {

  /**
   * Create a new {@link OutputStreamExpr} backed by a temporary file with the prefix {@code symja}.
   *
   * @return a new {@link OutputStreamExpr} pointing to a temporary file
   * @throws IOException if an I/O error occurs creating the temporary file
   */
  public static OutputStreamExpr newInstance() throws IOException {
    File file = File.createTempFile("symja", "");
    return newInstance(file, false);
  }

  /**
   * Create a new {@link OutputStreamExpr} that writes to the given {@link File}.
   *
   * @param file destination file
   * @param append if {@code true}, bytes will be written to the end of the file rather than the
   *        beginning
   * @return a new {@link OutputStreamExpr} writing to the given file
   * @throws IOException if an I/O error occurs opening the file
   */
  public static OutputStreamExpr newInstance(final File file, boolean append) throws IOException {

    FileOutputStream fos = new FileOutputStream(file, append);
    if (append) {
      try {
        fos.write('\n');
      } catch (IOException e) {
        // `1`.
        Errors.printMessage(S.List, "error",
            F.List("IOException in OutputStreamExpr.newInstance#newInstance()."));
      }
    }
    // OutputStreamWriter osw = new OutputStreamWriter(fos);
    return new OutputStreamExpr(fos, file.getCanonicalPath());
  }

  /**
   * Create a new {@link OutputStreamExpr} backed by the channel of a {@link RandomAccessFile}.
   *
   * @param file the {@link RandomAccessFile} providing the channel
   * @param streamName descriptive name for the stream
   * @return a new {@link OutputStreamExpr} that writes to the file channel
   */
  public static OutputStreamExpr newInstance(final RandomAccessFile file, String streamName) {

    OutputStream outputStream = Channels.newOutputStream(file.getChannel());
    // OutputStreamWriter osw = new OutputStreamWriter(outputStream);
    return new OutputStreamExpr(outputStream, streamName);
  }

  /**
   * Create a new {@link OutputStreamExpr} that writes to a file specified by {@code fileName}.
   *
   * @param fileName destination file path
   * @param append if {@code true}, bytes will be written to the end of the file rather than the
   *        beginning
   * @return a new {@link OutputStreamExpr} writing to the given file
   * @throws IOException if an I/O error occurs opening the file
   */
  public static OutputStreamExpr newInstance(final String fileName, boolean append)
      throws IOException {
    File file = new File(fileName);
    return newInstance(file, append);
  }

  /**
   * Unique identifier assigned to each stream instance. Generated using the shared stream counter
   * from {@link InputStreamExpr}.
   */
  private final int uniqueID;

  /**
   * Human readable name of the stream (e.g. file path or descriptor).
   */
  private final String streamName;

  /** Character based writer that wraps the underlying {@link OutputStream}. */
  private Writer writer;

  /** Binary data output view that wraps the underlying {@link OutputStream}. */
  private DataOutput dataOut;

  /**
   * Default constructor used for externalization.
   * <p>
   * Initializes the instance with a null stream and a default stream name.
   * </p>
   */
  public OutputStreamExpr() {
    super(S.OutputStream, null);
    uniqueID = InputStreamExpr.STREAM_COUNTER.getAndIncrement();
    streamName = "String";
  }

  /**
   * Protected constructor used by factory methods.
   *
   * @param stream the underlying {@link OutputStream}
   * @param streamName descriptive name for the stream (e.g. file path)
   */
  protected OutputStreamExpr(final OutputStream stream, String streamName) {
    super(S.OutputStream, stream);
    this.uniqueID = InputStreamExpr.STREAM_COUNTER.getAndIncrement();
    this.streamName = streamName;
  }

  /**
   * Close the underlying resources.
   * <p>
   * If a character {@link Writer} was created it is closed first. The underlying
   * {@link OutputStream} is then closed and the cached {@link DataOutput} reference is cleared.
   * </p>
   *
   * @throws IOException if an I/O error occurs while closing the stream
   */
  public void close() throws IOException {
    if (writer != null) {
      writer.close();
      writer = null;
    }
    fData.close();
    dataOut = null;
  }

  /**
   * Create a shallow copy of this expression that shares the same underlying {@link OutputStream}.
   * The returned copy will have the same stream name.
   *
   * @return a new {@link OutputStreamExpr} referencing the same stream
   */
  @Override
  public IExpr copy() {
    return new OutputStreamExpr(fData, streamName);
  }

  // protected OutputStreamExpr(final Writer value, String streamName) {
  // super(S.OutputStream, value);
  // this.uniqueID = InputStreamExpr.STREAM_COUNTER.getAndIncrement();
  // this.streamName = streamName;
  // }

  /**
   * Two {@link OutputStreamExpr} objects are considered equal if they wrap the same underlying
   * stream object.
   *
   * @param obj the other object to compare
   * @return {@code true} if equal, {@code false} otherwise
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof OutputStreamExpr) {
      return fData.equals(((OutputStreamExpr) obj).fData);
    }
    return false;
  }

  /**
   * Lazily create and return a {@link DataOutput} wrapper for the underlying stream. If already
   * created, the cached instance is returned.
   *
   * @return a {@link DataOutput} for writing primitive data types
   */
  public DataOutput getDataOutput() {
    if (dataOut == null) {
      dataOut = new DataOutputStream(fData);
    }
    return dataOut;
  }

  /**
   * Get the descriptive name of the stream.
   *
   * @return the stream name
   */
  public String getStreamName() {
    return streamName;
  }

  /**
   * Lazily create and return a {@link Writer} that encodes characters to bytes and writes them to
   * the underlying stream. If already created, the cached instance is returned.
   *
   * @return a {@link Writer} for character based output
   */
  public Writer getWriter() {
    if (writer == null) {
      writer = new OutputStreamWriter(fData);
    }
    return writer;
  }

  @Override
  public int hashCode() {
    return (fData == null) ? 353 : 353 + fData.hashCode();
  }

  @Override
  public int hierarchy() {
    return OUTPUTSTREAMEXPRID;
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    fData = (OutputStream) in.readObject();
  }

  @Override
  public String toString() {
    return fHead + "[" + "Name: " + streamName + " Unique-ID: " + uniqueID + "]";
  }

  @Override
  public void writeExternal(ObjectOutput output) throws IOException {
    output.writeObject(fData);
  }
}
