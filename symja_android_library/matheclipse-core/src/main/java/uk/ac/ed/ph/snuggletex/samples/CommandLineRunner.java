/* $Id: CommandLineRunner.java 583 2010-05-24 10:28:13Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.samples;

import uk.ac.ed.ph.snuggletex.InputError;
import uk.ac.ed.ph.snuggletex.SnuggleEngine;
import uk.ac.ed.ph.snuggletex.SnuggleInput;
import uk.ac.ed.ph.snuggletex.SnuggleRuntimeException;
import uk.ac.ed.ph.snuggletex.SnuggleSession;
import uk.ac.ed.ph.snuggletex.WebPageOutputOptions;
import uk.ac.ed.ph.snuggletex.WebPageOutputOptionsTemplates;
import uk.ac.ed.ph.snuggletex.SnuggleSession.EndOutputAction;
import uk.ac.ed.ph.snuggletex.WebPageOutputOptions.WebPageType;
import uk.ac.ed.ph.snuggletex.internal.util.IOUtilities;
import uk.ac.ed.ph.snuggletex.utilities.MessageFormatter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Trivial command line interface for running SnuggleTeX on a number of inputs files (or STDIN),
 * supporting most of the XML- and web-based output options.
 * 
 * @since 1.2.2
 *
 * @author  David McKain
 * @version $Revision: 583 $
 */
public class CommandLineRunner {
    
    private final String[] args;
    private final List<String> inputFiles;
    private WebPageOutputOptions snuggleOptions;
    private boolean requestedWebOutput;
    
    public CommandLineRunner(String[] args) {
        this.args = args;
        this.inputFiles = new ArrayList<String>();
        this.snuggleOptions = null;
        this.requestedWebOutput = false;
    }
    
    public void execute() {
        /* Show usage if nothing was provided on command line */
        if (args.length==0) {
            showHelp();
            return;
        }
        
        /* Parse arguments, show usage and exit if anything was invalid */
        try {
            if (!parseCommandLineArguments()) {
                showUsage();
                return;
            }
        }
        catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            showHelp();
            return;
        }
        
        /* Make sure we have at least one input file */
        if (inputFiles.isEmpty()) {
            System.err.println("No input files specified");
            showHelp();
            return;
        }
        
        /* Now do actual SnuggleTeX work */
        SnuggleEngine engine = new SnuggleEngine();
        SnuggleSession session = engine.createSession();
        try {
            /* Process each input file in turn */
            for (String inputFile : inputFiles) {
                SnuggleInput input;
                if (inputFile.equals("-")) {
                    /* Read from STDIN */
                    input = new SnuggleInput(System.in);
                }
                else {
                    /* Read from file */
                    input = new SnuggleInput(new File(inputFile));
                }
                session.parseInput(input);
            }
            
            /* Build output */
            if (requestedWebOutput) {
                session.writeWebPage(snuggleOptions, System.out, EndOutputAction.FLUSH);
            }
            else {
                System.out.println(session.buildXMLString(snuggleOptions));
            }
         
            /* Add a final newline after non-indented output as the default serializer tends not to include one */
            if (!snuggleOptions.isIndenting()) {
                System.out.println();
            }
        }
        catch (IOException e) {
            System.err.println("Got IOException running SnuggleTeX: " + e.getMessage());
        }

        
        /* Show any errors the were generated */
        for (InputError error : session.getErrors()) {
            System.err.println(MessageFormatter.formatErrorAsString(error));
        }
    }
    
    private void showHelp() {
        System.out.println("For help and usage, use the -? option");
    }
    
    private void showUsage() {
        InputStream usageStream = getClass().getClassLoader().getResourceAsStream("uk/ac/ed/ph/snuggletex/command-line-usage.txt");
        try {
            IOUtilities.transfer(usageStream, System.out);
        }
        catch (IOException e) {
            throw new SnuggleRuntimeException("Unexpected Exception printing out usage info", e);
        }
    }
    
    /**
     * (Returns true to continue executing, false to show usage and exit.
     * Throw an {@link IllegalArgumentException} for an error message.)
     */
    private boolean parseCommandLineArguments() {
        /* First sweep of the arguments is to determine whether we're doing plain old XML output
         * or generating a web page
         */
        WebPageType webPageType = null;
        String arg, nextArg;
        for (int i=0; i<args.length; i++) {
            arg = args[i];
            nextArg = i<args.length-1 ? args[i+1] : null;
            if ("-web".equals(arg)) {
                String webPageTypeName = nextArg;
                if (webPageTypeName==null) {
                    throw new IllegalArgumentException("No value provided for -web option");
                }
                try {
                    webPageType = WebPageType.valueOf(webPageTypeName);
                    requestedWebOutput = true;
                }
                catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Unknown web page type " + webPageTypeName);
                }
                i++; /* Skip over argument value */
            }
        }
        /* Now create appropriate Snuggle Options object. We're be slightly cheaty here
         * and use a WebPageOutputOptions even if doing XML output, as the required class is
         * just a proper subclass of this. 
         */
        snuggleOptions = requestedWebOutput ? WebPageOutputOptionsTemplates.createWebPageOptions(webPageType) : new WebPageOutputOptions();
        
        /* Now do second sweep over the arguments to read in everything else */ 
        for (int i=0; i<args.length; i++) {
            arg = args[i];
            nextArg = i<args.length-1 ? args[i+1] : null;
            if ("-".equals(arg)) {
                /* Represents STDIN here */
                inputFiles.add(arg);
            }
            else if ("-?".equals(arg) || "-h".equals(arg) || "-help".equals(arg)) {
                /* Show usage and exit */
                return false;
            }
            else if ("-web".equals(arg)) {
                /* Did this earlier */
                i++; /* (Skip over value) */
                continue;
            }
            else if (arg.startsWith("-")) {
                /* It's an '-option value' pair */
                if (nextArg==null) {
                    throw new IllegalArgumentException("No value provided for " + arg + " option");
                }
                String name = arg.substring(1);
                String value = nextArg;
                if ("enc".equals(name)) {
                    snuggleOptions.setEncoding(value);
                }
                else if ("indent".equals(name)) {
                    snuggleOptions.setIndenting(parseBoolean(name, value));
                }
                else if ("xmldecl".equals(name)) {
                    snuggleOptions.setIncludingXMLDeclaration(parseBoolean(name, value));
                }
                else if ("dtpublic".equals(name)) {
                    snuggleOptions.setDoctypePublic(value);
                }
                else if ("dtsystem".equals(name)) {
                    snuggleOptions.setDoctypeSystem(value);
                }
                else if ("ctype".equals(name)) {
                    snuggleOptions.setContentType(value);
                }
                else if ("lang".equals(name)) {
                    snuggleOptions.setLang(value);
                }
                else if ("title".equals(name)) {
                    snuggleOptions.setTitle(value);
                }
                else if ("head".equals(name)) {
                    snuggleOptions.setAddingTitleHeading(parseBoolean(name, value));
                }
                else if ("style".equals(name)) {
                    snuggleOptions.setIncludingStyleElement(parseBoolean(name, value));
                }
                else if ("css".equals(name)) {
                    snuggleOptions.addCSSStylesheetURLs(value);
                }
                else if ("clientxsl".equals(name)) {
                    snuggleOptions.addClientSideXSLTStylesheetURLs(value);
                }
                else {
                    throw new IllegalArgumentException("Unknown option " + arg);
                }
                i++; /* (Skip over value) */
            }
            else {
                /* Must be an input file */
                inputFiles.add(arg);
            }
        }
        return true;
    }
    
    private boolean parseBoolean(final String name, final String value) {
        if ("true".equals(value) || "on".equals(value) || "1".equals(value)) {
            return true;
        }
        else if ("false".equals(value) || "off".equals(value) || "0".equals(value)) {
            return false;
        }
        else {
            throw new IllegalArgumentException("Expected option " + name + " to have value true, false, on, off, 1 or 0, but got " + value);
        }
    }
    
    public static void main(String[] args) {
        new CommandLineRunner(args).execute();
    }
}
