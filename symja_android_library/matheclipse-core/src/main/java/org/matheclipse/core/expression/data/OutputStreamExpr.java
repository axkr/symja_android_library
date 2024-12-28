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

public class OutputStreamExpr extends DataExpr<OutputStream> implements Externalizable {
  private final int uniqueID;

  private final String streamName;

  /** Character based writer */
  private Writer writer;

  private DataOutput dataOut;

  public DataOutput getDataOutput() {
    if (dataOut == null) {
      dataOut = new DataOutputStream(fData);
    }
    return dataOut;
  }

  public Writer getWriter() {
    if (writer == null) {
      writer = new OutputStreamWriter(fData);
    }
    return writer;
  }

  public OutputStreamExpr() {
    super(S.OutputStream, null);
    uniqueID = InputStreamExpr.STREAM_COUNTER.getAndIncrement();
    streamName = "String";
  }

  /**
   * @param fileName
   * @param append if <code>true</code>, then bytes will be written to the end of the file rather
   *        than the beginning
   * @return
   * @throws IOException
   */
  public static OutputStreamExpr newInstance(final String fileName, boolean append)
      throws IOException {
    File file = new File(fileName);
    return newInstance(file, append);
  }

  /**
   * Create a temporary file with prefix <code>symja</code>
   *
   * @return
   * @throws IOException
   */
  public static OutputStreamExpr newInstance() throws IOException {
    File file = File.createTempFile("symja", "");
    return newInstance(file, false);
  }

  public static OutputStreamExpr newInstance(final RandomAccessFile file, String streamName) {

    OutputStream outputStream = Channels.newOutputStream(file.getChannel());
    // OutputStreamWriter osw = new OutputStreamWriter(outputStream);
    return new OutputStreamExpr(outputStream, streamName);
  }

  /**
   * @param file
   * @param append if <code>true</code>, then bytes will be written to the end of the file rather
   *        than the beginning
   * @return
   * @throws IOException
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

  protected OutputStreamExpr(final OutputStream stream, String streamName) {
    super(S.OutputStream, stream);
    this.uniqueID = InputStreamExpr.STREAM_COUNTER.getAndIncrement();
    this.streamName = streamName;
  }

  // protected OutputStreamExpr(final Writer value, String streamName) {
  // super(S.OutputStream, value);
  // this.uniqueID = InputStreamExpr.STREAM_COUNTER.getAndIncrement();
  // this.streamName = streamName;
  // }

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

  public String getStreamName() {
    return streamName;
  }

  @Override
  public int hashCode() {
    return (fData == null) ? 353 : 353 + fData.hashCode();
  }

  @Override
  public int hierarchy() {
    return OUTPUTSTREAMEXPRID;
  }

  public void close() throws IOException {
    if (writer != null) {
      writer.close();
      writer = null;
    }
    fData.close();
    dataOut = null;
  }

  @Override
  public IExpr copy() {
    return new OutputStreamExpr(fData, streamName);
  }

  @Override
  public String toString() {
    return fHead + "[" + "Name: " + streamName + " Unique-ID: " + uniqueID + "]";
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    fData = (OutputStream) in.readObject();
  }

  @Override
  public void writeExternal(ObjectOutput output) throws IOException {
    output.writeObject(fData);
  }
}
