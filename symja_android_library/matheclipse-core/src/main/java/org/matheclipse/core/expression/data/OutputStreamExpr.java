package org.matheclipse.core.expression.data;

import java.io.Externalizable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.Writer;
import java.nio.channels.Channels;

import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;

public class OutputStreamExpr extends DataExpr<Writer> implements Externalizable {
  private final int uniqueID;

  private final String streamName;

  public OutputStreamExpr() {
    super(S.OutputStream, null);
    uniqueID = InputStreamExpr.STREAM_COUNTER.getAndIncrement();
    streamName = "String";
  }

  /**
   * @param fileName
   * @param append if <code>true</code>, then bytes will be written to the end of the file rather
   *     than the beginning
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

  public static OutputStreamExpr newInstance(final RandomAccessFile file, String streamName)
      throws FileNotFoundException {

    OutputStream outputStream = Channels.newOutputStream(file.getChannel());
    OutputStreamWriter osw = new OutputStreamWriter(outputStream);
    return new OutputStreamExpr(osw, streamName);
  }

  /**
   * @param file
   * @param append if <code>true</code>, then bytes will be written to the end of the file rather
   *     than the beginning
   * @return
   * @throws IOException
   */
  public static OutputStreamExpr newInstance(final File file, boolean append) throws IOException {

    FileOutputStream fos = new FileOutputStream(file, append);
    if (append) {
      try {
        fos.write('\n');
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    OutputStreamWriter osw = new OutputStreamWriter(fos);
    return new OutputStreamExpr(osw, file.getCanonicalPath());
  }

  protected OutputStreamExpr(final Writer value, String streamName) {
    super(S.OutputStream, value);
    this.uniqueID = InputStreamExpr.STREAM_COUNTER.getAndIncrement();
    this.streamName = streamName;
  }

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
    fData = (OutputStreamWriter) in.readObject();
  }

  @Override
  public void writeExternal(ObjectOutput output) throws IOException {
    output.writeObject(fData);
  }
}
