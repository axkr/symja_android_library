/* $Id: SnuggleInputReader.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.internal;

import uk.ac.ed.ph.snuggletex.InputError;
import uk.ac.ed.ph.snuggletex.SnuggleInput;
import uk.ac.ed.ph.snuggletex.SnuggleLogicException;
import uk.ac.ed.ph.snuggletex.definitions.CoreErrorCode;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper class that does the job of taking a {@link SnuggleInput}, checking its contents for
 * ASCII characters, working out how to map absolute offsets into <tt>(line,column)</tt> pairs
 * and producing a {@link WorkingDocument} for later use.
 * 
 * @author  David McKain
 * @version $Revision: 525 $
 */
public final class SnuggleInputReader implements WorkingDocument.SourceContext {
    
    private final SessionContext sessionContext;
    private final SnuggleInput input;
    
    private int[] newlineIndices = null;
    private int inputLength;
    private WorkingDocument resultingDocument = null;
    
    public SnuggleInputReader(SessionContext sessionContext, SnuggleInput input) {
        this.sessionContext = sessionContext;
        this.input = input;
    }
    
    public SnuggleInput getInput() {
        return input;
    }
    
    public SessionContext getSessionContext() {
        return sessionContext;
    }
    
    //---------------------------------------------
    // Public interface

    public WorkingDocument createWorkingDocument() throws IOException, SnuggleParseException {
        /* First of all, we read in the input document, returning a StringBuilder */
        StringBuilder inputData = readInputData();
        this.inputLength = inputData.length();
        
        /* Go through data, checking it is ASCII and calculating indices of newlines */
        this.newlineIndices = calculateNewlineIndicesAndCheckASCII(inputData);
        
        /* Then create a WorkingDocument that can be passed to the LaTeX tokeniser for messing with */
        this.resultingDocument = new WorkingDocument(inputData, this);
        return resultingDocument;
    }
    
    public int[] getLineAndColumn(int index) {
        if (newlineIndices==null) {
            throw new IllegalStateException("Input has not yet been read");
        }
        if (index<0 || index>inputLength) {
            throw new IndexOutOfBoundsException();
        }
        int line, column;
        for (line=0; line<newlineIndices.length && newlineIndices[line]<index; line++)
            ;
        column = index - newlineIndices[line-1];
        return new int[] { line, column };
    }

    //---------------------------------------------
    
    private StringBuilder readInputData() throws IOException {
        switch (input.getType()) {
            case STRING:
                return new StringBuilder(input.getString());

            case FILE:
                /* (Assumes platform default encoding) */
                return readCharacterStream(new InputStreamReader(new FileInputStream(input.getFile())));

            case INPUT_STREAM:
                /* (Assumes platform default encoding) */
                return readCharacterStream(new InputStreamReader(input.getInputStream()));

            case READER:
                return readCharacterStream(input.getReader());

            default:
                throw new SnuggleLogicException("Unexpected switch case: " + input.getType());
        }
    }
    
    private StringBuilder readCharacterStream(Reader reader) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(reader);
        String line;
        int size = 0;
        StringBuilder result = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null) {
            size += line.length() + 1;
            result.append(line).append("\n");
        }
        bufferedReader.close();
        return result;
    }

    private int[] calculateNewlineIndicesAndCheckASCII(StringBuilder inputData) throws SnuggleParseException {
        List<Integer> newlineIndicesBuilder = new ArrayList<Integer>();
        newlineIndicesBuilder.add(Integer.valueOf(-1));
        int c;
        for (int i=0, length=inputData.length(); i<length; i++) {
            c = inputData.charAt(i);
            if (c=='\n') {
                newlineIndicesBuilder.add(Integer.valueOf(i));
            }
            if ((c<32 && !Character.isWhitespace(c) || c > 126)) {
                InputError error = new InputError(CoreErrorCode.TTEG02, null,
                        Character.toString((char) c),
                        Integer.toHexString(c),
                        Integer.valueOf(i));
                sessionContext.registerError(error);
                inputData.setCharAt(i, 'x');
            }
            
        }
        int[] calculatedNewlineIndices = new int[newlineIndicesBuilder.size()];
        for (int i = 0; i < calculatedNewlineIndices.length; i++) {
            calculatedNewlineIndices[i] = newlineIndicesBuilder.get(i);
        }
        return calculatedNewlineIndices;
    }
}
