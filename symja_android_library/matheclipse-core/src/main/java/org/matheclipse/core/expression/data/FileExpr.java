package org.matheclipse.core.expression.data;

import java.io.Externalizable;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.matheclipse.core.expression.DataExpr;
import org.matheclipse.core.expression.S;
import org.matheclipse.core.interfaces.IExpr;

public class FileExpr extends DataExpr<File> implements Externalizable {

  public FileExpr() {
    super(S.File, null);
  }

  /**
   * @param fileName
   * @return
   */
  public static FileExpr newInstance(final String fileName) {
    File file = new File(fileName);
    return new FileExpr(file);
  }

  protected FileExpr(final File value) {
    super(S.File, value);
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof FileExpr) {
      return fData.equals(((FileExpr) obj).fData);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return (fData == null) ? 353 : 353 + fData.hashCode();
  }

  @Override
  public int hierarchy() {
    return FILEEXPRID;
  }

  @Override
  public IExpr copy() {
    return new FileExpr(fData);
  }

  @Override
  public String toString() {
    try {
      return fHead + "[" + fData.getCanonicalPath() + "]";
    } catch (IOException e) {
      return fHead + "[IOException in File#getCanonicalPath()]";
    }
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    fData = (File) in.readObject();
  }

  @Override
  public void writeExternal(ObjectOutput output) throws IOException {
    output.writeObject(fData);
  }
}
