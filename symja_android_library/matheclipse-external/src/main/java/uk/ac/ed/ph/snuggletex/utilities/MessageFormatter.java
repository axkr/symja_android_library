/* $Id: MessageFormatter.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.utilities;

import uk.ac.ed.ph.snuggletex.ErrorCode;
import uk.ac.ed.ph.snuggletex.ErrorGroup;
import uk.ac.ed.ph.snuggletex.InputError;
import uk.ac.ed.ph.snuggletex.SnuggleConstants;
import uk.ac.ed.ph.snuggletex.SnuggleLogicException;
import uk.ac.ed.ph.snuggletex.SnuggleRuntimeException;
import uk.ac.ed.ph.snuggletex.definitions.CorePackageDefinitions;
import uk.ac.ed.ph.snuggletex.definitions.Globals;
import uk.ac.ed.ph.snuggletex.definitions.W3CConstants;
import uk.ac.ed.ph.snuggletex.internal.FrozenSlice;
import uk.ac.ed.ph.snuggletex.internal.SnuggleInputReader;
import uk.ac.ed.ph.snuggletex.internal.WorkingDocument;
import uk.ac.ed.ph.snuggletex.internal.WorkingDocument.CharacterSource;
import uk.ac.ed.ph.snuggletex.internal.WorkingDocument.IndexResolution;
import uk.ac.ed.ph.snuggletex.internal.WorkingDocument.Slice;
import uk.ac.ed.ph.snuggletex.internal.WorkingDocument.SourceContext;
import uk.ac.ed.ph.snuggletex.internal.util.ObjectUtilities;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Utility class that formats an {@link InputError} in various ways.
 * 
 * NOTE: We're using {@link MessageFormat} extensively here, which hasn't been updated to use
 * {@link StringBuilder} so we'll sadly have to make do with {@link StringBuffer}
 *
 * @author  David McKain
 * @version $Revision: 525 $
 */
public final class MessageFormatter {
    
    /** {@link ResourceBundle} providing templates for message bodies */
    public static final PropertyResourceBundle GENERAL_MESSAGE_BUNDLE;
    
    static {
        try {
            GENERAL_MESSAGE_BUNDLE = (PropertyResourceBundle) ResourceBundle.getBundle(Globals.GENERAL_MESSAGES_PROPERTIES_BASENAME);
        }
        catch (MissingResourceException e) {
            throw new SnuggleRuntimeException(e);
        }
    }
    
    /** Constructs an error message for the given {@link InputError}. */
    public static String getErrorMessage(InputError error) {
        return getErrorMessage(error.getErrorCode(), error.getArguments());
    }
    
    /** Constructs an error message for the given {@link ErrorCode} and arguments. */
    public static String getErrorMessage(ErrorCode errorCode, Object... arguments) {
        ResourceBundle errorMessageBundle = errorCode.getErrorGroup().getPackage().getErrorMessageBundle();
        String result;
        if (errorMessageBundle!=null) {
            /* Use ResourceBundle specified to format error */
            result = MessageFormat.format(errorMessageBundle.getString(errorCode.toString()), arguments);
        }
        else {
            /* No ResourceBundle specified, so do as best as we can */
            result = "Error " + formatErrorCodeName(errorCode) + ": " + Arrays.toString(arguments);
        }
        return result;
    }
    
    /** Creates a full diagnosis of the given error */
    public static String formatErrorAsString(InputError error) {
        StringBuffer resultBuilder = new StringBuffer();
        appendErrorAsString(resultBuilder, error);
        return resultBuilder.toString();
    }
    
    public static String formatErrorGroup(ErrorGroup errorGroup) {
        ResourceBundle errorMessageBundle = errorGroup.getPackage().getErrorMessageBundle();
        String result;
        if (errorMessageBundle!=null) {
            /* Use ResourceBundle specified to format error */
            result = MessageFormat.format(errorMessageBundle.getString(errorGroup.toString()), ObjectUtilities.EMPTY_OBJECT_ARRAY);
        }
        else {
            /* No ResourceBundle specified, so do as best as we can */
            result = "Error " + errorGroup.getName();
        }
        return result;
    }
    
    public static String formatErrorCodeName(ErrorCode errorCode) {
        String packageName = errorCode.getErrorGroup().getPackage().getName();
        String errorCodeName = errorCode.getName();
        return CorePackageDefinitions.CORE_PACKAGE_NAME.equals(packageName)
            ? errorCodeName : packageName + "/" + errorCodeName;
    }
    
    /**
     * Creates a DOM {@link Element} containing information about the given error, including
     * either just the {@link ErrorCode} or full details.
     * 
     * @param ownerDocument {@link Document} that will contain the resulting element.
     * @param error {@link InputError} to format
     * @param fullDetails false if you just want the error code, true for full details.
     */
    public static Element formatErrorAsXML(Document ownerDocument, InputError error, boolean fullDetails) {
        Element result = ownerDocument.createElementNS(SnuggleConstants.SNUGGLETEX_NAMESPACE, "error");
        result.setAttribute("code", error.getErrorCode().getName());
        result.setAttribute("package", error.getErrorCode().getErrorGroup().getPackage().getName());
        
        if (fullDetails) {
            /* Nicely format XML error content */
            StringBuffer messageBuilder = new StringBuffer(getErrorMessage(error));
            FrozenSlice errorSlice = error.getSlice();
            if (errorSlice!=null) {
                appendSliceContext(messageBuilder, errorSlice);
            }
            
            /* Add message as child node */
            result.appendChild(ownerDocument.createTextNode(messageBuilder.toString()));
        }
        /* That's it! */
        return result;
    }
    
    public static Element formatErrorAsXHTML(Document ownerDocument, InputError error) {
        Element result = ownerDocument.createElementNS(W3CConstants.XHTML_NAMESPACE, "div");
        result.setAttribute("class", "error");
        
        Element heading = ownerDocument.createElementNS(W3CConstants.XHTML_NAMESPACE, "h2");
        heading.appendChild(ownerDocument.createTextNode("SnuggleTeX Error (" + formatErrorCodeName(error.getErrorCode()) + ")"));
        
        Element pre = ownerDocument.createElementNS(W3CConstants.XHTML_NAMESPACE, "pre");
        
        /* Nicely format XML error content */
        StringBuffer messageBuilder = new StringBuffer(getErrorMessage(error));
        FrozenSlice errorSlice = error.getSlice();
        if (errorSlice!=null) {
            appendSliceContext(messageBuilder, errorSlice);
        }
        
        /* Add message as child node */
        pre.appendChild(ownerDocument.createTextNode(messageBuilder.toString()));
        
        /* That's it! */
        result.appendChild(heading);
        result.appendChild(pre);
        return result;
    }
    
    public static void appendErrorAsString(StringBuffer messageBuilder, InputError error) {
        new MessageFormat(GENERAL_MESSAGE_BUNDLE.getString("error_as_string")).format(new Object[] {
                formatErrorCodeName(error.getErrorCode()), /* Error code/package */
                getErrorMessage(error) /* Error Message */
        }, messageBuilder, null);
        FrozenSlice errorSlice = error.getSlice();
        if (errorSlice!=null) {
            appendSliceContext(messageBuilder, errorSlice);
        }
    }
    
    private static void appendNewlineIfRequired(StringBuffer messageBuilder) {
        if (messageBuilder.length()>0) {
            messageBuilder.append('\n');
        }
    }

    public static void appendSliceContext(StringBuffer messageBuilder, FrozenSlice slice) {
        WorkingDocument document = slice.getDocument();
        
        /* Work out where the error occurred */
        IndexResolution errorResolution = document.resolveIndex(slice.startIndex, false);
        if (errorResolution==null) {
            /* (If this happens, then most likely the error occurred at the end of the document) */
            errorResolution = document.resolveIndex(slice.startIndex, true);
        }
        if (errorResolution==null) {
            throw new SnuggleLogicException("Could not resolve component containing error slice starting at "
                    + slice.startIndex);
        }
        Slice errorSlice = errorResolution.slice;
        CharacterSource errorComponent = errorSlice.resolvedComponent;
        int errorIndex = errorResolution.indexInComponent;
        appendFrame(messageBuilder, errorComponent, errorIndex);
    }
    
    private static void appendFrame(StringBuffer messageBuilder, CharacterSource source, int offsetInSource) {
        SourceContext context = source.context;
        if (context instanceof SnuggleInputReader) {
            SnuggleInputReader inputContext = (SnuggleInputReader) context;
            int[] location = inputContext.getLineAndColumn(offsetInSource);
            appendNewlineIfRequired(messageBuilder);
            new MessageFormat(GENERAL_MESSAGE_BUNDLE.getString("input_context")).format(new Object[] {
                  location[0], /* Line */
                  location[1], /* Column */
                  inputContext.getInput().getIdentifier() /* Input description */
            }, messageBuilder, null);
        }
        else if (context instanceof WorkingDocument.SubstitutionContext) {
            WorkingDocument.SubstitutionContext substitutionContext = (WorkingDocument.SubstitutionContext) context;
            appendNewlineIfRequired(messageBuilder);
            new MessageFormat(GENERAL_MESSAGE_BUNDLE.getString("subs_context")).format(new Object[] {
                    offsetInSource, /* Character index */
                    formatText(source.substitutedText), /* Before subs */
                    formatText(substitutionContext.replacement) /* After subs */
            }, messageBuilder, null);
        }
        else {
            throw new SnuggleLogicException("Unexpected SourceContext " + context.getClass().getName());
        }
        if (source.substitutedSource!=null) {
            appendFrame(messageBuilder, source.substitutedSource, source.substitutionOffset);
        }
    }
    
    private static final int TRUNCATE_LENGTH = 20;
    
    /**
     * Trivial helper to truncate a portion of text if it goes longer than {@link #TRUNCATE_LENGTH}.
     * This also replaces all whitespace with a spaces so as to avoid the results spanning
     * multiple lines.
     */
    private static String formatText(CharSequence content) {
        String result = content.toString().replaceAll("\\s", " ");
        if (result.length()>TRUNCATE_LENGTH) {
            result = result.substring(0, TRUNCATE_LENGTH) + "...";
        }
        return result;
    }
}
