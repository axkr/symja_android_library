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
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Reader;
import java.io.StringReader;
import java.util.concurrent.atomic.AtomicInteger;
import org.matheclipse.core.eval.EvalEngine;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;

public class InputStreamExpr extends DataExpr<InputStream> implements Externalizable {
  public static AtomicInteger STREAM_COUNTER = new AtomicInteger(1);

  private final int uniqueID;
  private final String streamName;

  /**
   * Data input for binary based operation {@link S#BinaryRead}
   */
  private DataInput dataIn;

  /**
   * Reader for text based operation {@link S#Read}
   */
  private Reader reader;

  /**
   * Get data input for binary based operation {@link S#BinaryRead}
   *
   * @return
   * @throws IOException
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
   * Get reader for text based operation {@link S#Read}.
   *
   * @return
   * @throws IOException
   */
  public Reader getReader() throws IOException {
    if (reader == null) {
      String str = CharStreams.toString(new InputStreamReader(fData, Charsets.UTF_8));
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

  public static InputStreamExpr getFromFile(final FileExpr fileExpr, String streamName,
      EvalEngine engine) throws FileNotFoundException {
    File file = fileExpr.toData();
    IExpr temp = engine.rememberMap.get(file);
    if (temp == null || !(temp instanceof InputStreamExpr)) {
      InputStreamExpr stream = new InputStreamExpr(new FileInputStream(file));
      engine.rememberMap.put(file, stream);
      return stream;
    }
    return (InputStreamExpr) temp;
  }

  /**
   * @param fileName
   * @param streamName the name of the stream
   * @return
   * @throws FileNotFoundException
   */
  public static InputStreamExpr newInstance(final String fileName, String streamName)
      throws FileNotFoundException {
    File file = new File(fileName);
    return new InputStreamExpr(new FileInputStream(file), streamName);
  }

  public static InputStreamExpr newInstance(final File file, String streamName, final Reader reader)
      throws FileNotFoundException {
    return new InputStreamExpr(new FileInputStream(file), streamName, reader);
  }

  public static InputStreamExpr newInstance(final Reader reader)
      throws IOException, FileNotFoundException {
    InputStream targetStream =
        new ByteArrayInputStream(CharStreams.toString(reader).getBytes(Charsets.UTF_8));
    reader.reset();
    return new InputStreamExpr(targetStream, "String", reader);
  }

  protected InputStreamExpr(final InputStream value) {
    this(value, "String");
  }

  protected InputStreamExpr(final InputStream value, String streamName, final Reader reader) {
    super(S.InputStream, value);
    this.uniqueID = STREAM_COUNTER.getAndIncrement();
    this.streamName = streamName;
    this.reader = reader;
  }

  protected InputStreamExpr(final InputStream value, String streamName) {
    super(S.InputStream, value);
    this.uniqueID = STREAM_COUNTER.getAndIncrement();
    this.streamName = streamName;
  }

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

  public void close() throws IOException {
    if (reader != null) {
      reader.close();
      reader = null;
    }
    fData.close();
  }

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
