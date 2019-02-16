package com.twosigma.beakerx.symjamma.output;

public class HTMLNotebookOutput extends HTMLFriendlyNotebookOutput {

    public HTMLNotebookOutput(String content) {
        super("text/html", content);
    }

    @Override
    public String toHTML() {
        return (String) getData();
    }

}