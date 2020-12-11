package com.twosigma.beakerx.symjamma.output;

import com.twosigma.beakerx.mimetype.MIMEContainer;

public abstract class NotebookOutput extends MIMEContainer {

  public NotebookOutput(String mimeTypeObj, String content) {
    super(mimeTypeObj, content);
  }
}
