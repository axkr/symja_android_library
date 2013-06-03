/*
 * $Id$
 */

package edu.jas.kern;


import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;


/**
 * Static String and Reader methods.
 * @author Heinz Kredel
 */

public class StringUtil {


    /**
     * Parse white space delimited String from Reader.
     * @param r Reader.
     * @return next non white space String from r.
     */
    public static String nextString(Reader r) {
        StringWriter sw = new StringWriter();
        try {
            char buffer;
            int i;
            // skip white space
            while ((i = r.read()) > -1) {
                buffer = (char) i;
                if (!Character.isWhitespace(buffer)) {
                    sw.write(buffer);
                    break;
                }
            }
            // read non white space, ignore new lines ?
            while ((i = r.read()) > -1) {
                buffer = (char) i;
                if (Character.isWhitespace(buffer)) {
                    break;
                }
                sw.write(buffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sw.toString();
    }


    /**
     * Parse String with given delimiter from Reader.
     * @param c delimiter.
     * @param r Reader.
     * @return next String up to c from r.
     */
    public static String nextString(Reader r, char c) {
        StringWriter sw = new StringWriter();
        try {
            char buffer;
            int i;
            // read chars != c, ignore new lines ?
            while ((i = r.read()) > -1) {
                buffer = (char) i;
                if (buffer == c) {
                    break;
                }
                sw.write(buffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sw.toString();
    }


    /**
     * Parse paired String with given delimiters from Reader.
     * @param b opposite delimiter.
     * @param c delimiter.
     * @param r Reader.
     * @return next nested matching String up to c from r.
     */
    public static String nextPairedString(Reader r, char b, char c) {
        StringWriter sw = new StringWriter();
        try {
            int level = 0;
            char buffer;
            int i;
            // read chars != c, ignore new lines ?
            while ((i = r.read()) > -1) {
                buffer = (char) i;
                if (buffer == b) {
                    level++;
                }
                if (buffer == c) {
                    level--;
                    if (level < 0) {
                        break; // skip last closing 'brace' 
                    }
                }
                sw.write(buffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sw.toString();
    }

}
