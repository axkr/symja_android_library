package org.matheclipse.core.expression.data;

import java.io.BufferedReader;
import java.io.Externalizable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Reader;
import java.util.concurrent.atomic.AtomicInteger;

import org.jgrapht.graph.AbstractBaseGraph;
import org.matheclipse.core.builtin.GraphFunctions;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;

public class InputStreamExpr extends DataExpr<Reader> implements Externalizable {
  public static AtomicInteger STREAM_COUNTER = new AtomicInteger(1);

  private final int uniqueID;
  private final String streamName;

  public InputStreamExpr() {
    super(S.InputStream, null);
    uniqueID = STREAM_COUNTER.getAndIncrement();
    streamName = "String";
  }

  /**
   * @param fileName
   * @return
   * @throws FileNotFoundException
   */
  public static InputStreamExpr newInstance(final String fileName) throws FileNotFoundException {
    File file = new File(fileName);
    Reader inputStreamReader = new BufferedReader(new FileReader(file));
    return new InputStreamExpr(inputStreamReader);
  }

  public static InputStreamExpr newInstance(final File file) throws FileNotFoundException {
    Reader inputStreamReader = new BufferedReader(new FileReader(file));
    return new InputStreamExpr(inputStreamReader);
  }

  public static InputStreamExpr newInstance(final String fileName, Reader inputStreamReader) {
    //	    File file = new File(fileName);
    //	    InputStream inputStream       = new FileInputStream(file);
    // 	    Reader      inputStreamReader = new InputStreamReader(inputStream);

    return new InputStreamExpr(inputStreamReader);
  }

  protected InputStreamExpr(final Reader value) {
    super(S.InputStream, value);
    uniqueID = STREAM_COUNTER.getAndIncrement();
    streamName = "String";
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
    fData = (InputStreamReader) in.readObject();
  }

  @Override
  public void writeExternal(ObjectOutput output) throws IOException {
    output.writeObject(fData);
  }
}
