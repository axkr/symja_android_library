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
        return sw.toString().trim();
    }


    /**
     * Parse paired String with given delimiters from Reader.
     * @param b opposite delimiter.
     * @param c delimiter, != b.
     * @param r Reader.
     * @return next nested matching String from b up to c from r.
     */
    public static String nextPairedString(Reader r, char b, char c) {
        if (b == c) {
            throw new IllegalArgumentException("b == c, not allowed " + b);
        }
        StringWriter sw = new StringWriter();
        try {
            int level = 0;
            boolean first = true;
            char buffer;
            int i;
            // read chars != c, ignore new lines ?
            while ((i = r.read()) > -1) {
                buffer = (char) i;
                if (buffer == b) {
                    level++;
                    if (first) { // skip first opening 'brace'
                        first = false;
                        continue;
                    }
                }
                if (buffer == c) {
                    level--;
                    if (level <= 0) {
                        break; // skip last closing 'brace'
                    }
                }
                if (level > 0) {
                    sw.write(buffer);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sw.toString().trim();
    }


    /**
     * Select stack trace parts.
     * @param expr regular matching expression.
     * @return stack trace with elements matching expr.
     */
    public static String selectStackTrace(String expr) {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < stack.length; i++) {
            String s = stack[i].toString();
            if (s.indexOf("selectStackTrace") >= 0) {
                continue;
            }
            if (s.matches(expr)) {
                sb.append("\nstack[" + i + "] = ");
                sb.append(s);
            }
            //System.out.println("stack["+i+"] = " + s);
        }
        return sb.toString();
    }

}
