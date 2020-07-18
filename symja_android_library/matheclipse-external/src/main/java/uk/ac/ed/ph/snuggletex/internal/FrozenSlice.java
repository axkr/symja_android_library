/* $Id: FrozenSlice.java 541 2010-03-24 18:17:15Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.internal;

import uk.ac.ed.ph.snuggletex.tokens.Token;

/**
 * Represents a finished "segment" of text in the {@link WorkingDocument}. This
 * represents the document after ALL text substitutions have been made so ought
 * to be considered as representing a fixed block of text.
 * <p>
 * This is used to represent the block of text each parsed {@link Token} refers
 * to.
 * 
 * <h2>Developer Note</h2>
 * 
 * At one point in {@link LaTeXTokeniser}, it is technically possible to have a
 * {@link FrozenSlice} which may refer to text which could change, but this
 * phenomenon is temporary.
 *
 * @author  David McKain
 * @version $Revision: 541 $
 */
public final class FrozenSlice {
    
    /* No, Mum hasn't gone to Iceland... */
    
    public final WorkingDocument document;
    public final int startIndex;
    public final int endIndex;
    
    private transient String stringRepresentation;
    
    public FrozenSlice(WorkingDocument document, int startIndex, int endIndex) {
        this.document = document;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }
    
    // ----------------------------------------------
    
    public WorkingDocument getDocument() {
        return document;
    }
    
    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }
    
    // ----------------------------------------------

    public CharSequence extract() {
        return document.extract(startIndex, endIndex);
    }
    
    public boolean isWhitespace() {
        return document.isRegionWhitespace(startIndex, endIndex);
    }
    
    // ----------------------------------------------
    // Spanning Slice construction
    
    public FrozenSlice rightOuterSpan(final FrozenSlice rightSlice) {
        ensureSharesDocumentWith(rightSlice);
        return document.freezeSlice(startIndex, rightSlice.endIndex);
    }

    public final boolean sharesDocumentWith(final FrozenSlice otherSlice) {
        return document.equals(otherSlice.document);
    }

    protected final void ensureSharesDocumentWith(final FrozenSlice otherSlice) {
        if (!sharesDocumentWith(otherSlice)) {
            throw new IllegalArgumentException("Document mismatch for FrozenSlice " + otherSlice);
        }
    }
    
    // ----------------------------------------------
    
    @Override
    public String toString() {
        if (stringRepresentation==null) {
            StringBuilder builder = new StringBuilder(getClass().getSimpleName());
            builder.append("(range=[")
                .append(startIndex)
                .append(",")
                .append(endIndex)
                .append("), value='")
                .append(extract().toString().replace("\n", "%n").replace("\r", "%r")) /* (Hmmm... crap way of representing newline/CR!) */
                .append("')");
            stringRepresentation = builder.toString();
        }
        return stringRepresentation;
    }


}
