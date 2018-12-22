/* $Id: ArgumentContainerToken.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.tokens;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import uk.ac.ed.ph.snuggletex.internal.util.DumpMode;
import uk.ac.ed.ph.snuggletex.internal.util.ObjectDumperOptions;
import uk.ac.ed.ph.snuggletex.definitions.Command;
import uk.ac.ed.ph.snuggletex.definitions.Environment;
import uk.ac.ed.ph.snuggletex.definitions.LaTeXMode;
import uk.ac.ed.ph.snuggletex.internal.FrozenSlice;

/**
 * This token is used as a container for the arguments specified for a particular
 * {@link Command} or {@link Environment}, along with regions of input that have
 * been enclosed in braces.
 * 
 * @author  David McKain
 * @version $Revision: 525 $
 */
@ObjectDumperOptions(DumpMode.DEEP)
public final class ArgumentContainerToken extends Token implements Iterable<FlowToken> {
    
    public static final ArgumentContainerToken[] EMPTY_ARRAY = new ArgumentContainerToken[0];
    
    private final List<FlowToken> contents;
    
    public ArgumentContainerToken(final FrozenSlice slice, final LaTeXMode latexMode,
            final List<FlowToken> contents) {
        super(slice, TokenType.ARGUMENT_CONTAINER, latexMode);
        this.contents = contents;
    }
    
    public static ArgumentContainerToken createFromSingleToken(final LaTeXMode latexMode,
            final FlowToken content) {
        List<FlowToken> contentList = new ArrayList<FlowToken>();
        contentList.add(content);
        return new ArgumentContainerToken(content.getSlice(), latexMode, contentList);
    }
    
    public static ArgumentContainerToken createFromContiguousTokens(final Token parentToken,
            final LaTeXMode latexMode, final List<? extends FlowToken> contents) {
        return createFromContiguousTokens(parentToken, latexMode, contents, 0, contents.size());
    }
    
    public static ArgumentContainerToken createFromContiguousTokens(final Token parentToken,
            final LaTeXMode latexMode, final List<? extends FlowToken> contents,
            final int startIndex, final int endIndex) {
        if (startIndex>endIndex) {
            throw new IllegalArgumentException("startIndex must be <= endIndex");
        }
        ArgumentContainerToken result;
        if (contents.size()>0) {
            FrozenSlice startSlice = contents.get(startIndex).getSlice();
            FrozenSlice endSlice = contents.get(endIndex-1).getSlice();
            FrozenSlice resultSlice = startSlice.rightOuterSpan(endSlice);
            
            result = new ArgumentContainerToken(resultSlice, latexMode,
                    new ArrayList<FlowToken>(contents.subList(startIndex, endIndex)));
        }
        else {
            result = createEmptyContainer(parentToken, latexMode);
        }
        return result;
    }
    
    public static ArgumentContainerToken createEmptyContainer(final Token parentToken, final LaTeXMode latexMode) {
        List<FlowToken> emptyTokens = Collections.emptyList();
        return new ArgumentContainerToken(parentToken.getSlice(), latexMode, emptyTokens);
    }
    
    @ObjectDumperOptions(DumpMode.DEEP)
    public List<FlowToken> getContents() {
        return contents;
    }
    
    public Iterator<FlowToken> iterator() {
        return contents.iterator();
    }
}
