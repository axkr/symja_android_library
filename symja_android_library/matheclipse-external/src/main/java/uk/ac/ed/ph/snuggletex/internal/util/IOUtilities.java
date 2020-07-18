/* $Id: IOUtilities.java 525 2010-01-05 14:07:36Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package uk.ac.ed.ph.snuggletex.internal.util;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.channels.FileChannel;

/**
 * A collection of vaguely useful utilities for doing common I/O and File-related tasks.
 * <p>
 * (This is a cut-down version of the Class of the same name in <tt>ph-commons-util</tt>.)
 * 
 * @author  David McKain
 * @version $Revision: 525 $
 */
public final class IOUtilities {
    
    /** Buffer size when transferring streams */
    public static int BUFFER_SIZE = 32 * 1024;

    /** Maximum size of characters we'll read from a text stream before complaining */
    public static int MAX_TEXT_STREAM_SIZE = 1024 * 1024;

    //----------------------------------------------------------------------------

    /**
     * Simple method to ensure that a given directory exists. If the directory
     * does not exist then it is created, along with all required parents.
     *
     * @throws IOException if creation could not succeed for some reason.
     */
    public static File ensureDirectoryCreated(File directory) throws IOException {
        if (!directory.isDirectory()) {
            if (!directory.mkdirs()) {
                throw new IOException("Could not create directory " + directory);
            }
        }
        return directory;
    }

    /**
     * Simple method to ensure that a given File exists. If the File
     * does not exist then it is created, along with all required parent
     * directories.
     *
     * @throws IOException if creation could not succeed for some reason.
     */
    public static File ensureFileCreated(File file) throws IOException {
        /* Make sure parent exists */
        File parentDirectory = file.getParentFile();
        if (parentDirectory!=null) {
            ensureDirectoryCreated(parentDirectory);
        }
        /* Now create file */
        if (!file.isFile()) {
            if (!file.createNewFile()) {
                throw new IOException("Could not create file " + file);
            }
        }
        return file;
    }

    //----------------------------------------------------------------------------
    
    /**
     * Ensures that as many of the given {@link Closeable}s are closed. If any fail to close, the
     * first Exception is thrown after an attempt has been made to close the rest.
     * <p>
     * For convenience, any number of the stream may be null, in which case they will be ignored.
     * 
     * @param streams "streams" to close
     * @throws IOException 
     */
    public static void ensureClose(Closeable... streams) throws IOException {
        IOException firstException = null;
        for (Closeable stream : streams) {
            if (stream!=null) {
                try {
                    stream.close();
                }
                catch (IOException e) {
                    firstException = e;
                }
            }
        }
        if (firstException!=null) {
            throw firstException;
        }
    }
    
    //----------------------------------------------------------------------------
    // Convenience methods for data transfers
    
    /**
     * "Transfers" data from the given InputStream to the given OutputStream,
     * closing both streams once the InputStream has been exhausted.
     * <p>
     * This will check to see if both streams are File streams and, if so, use the
     * {@link #transfer(FileInputStream, FileOutputStream)} version of this method instead.
     *
     * @param inStream
     * @param outStream
     * @throws IOException
     */
    public static void transfer(InputStream inStream, OutputStream outStream) throws IOException {
        if (inStream instanceof FileInputStream && outStream instanceof FileOutputStream) {
            transfer((FileInputStream) inStream, (FileOutputStream) outStream);
        }
        else {
            transfer(inStream, outStream, true);
        }
    }
    
    /**
     * Version of {@link #transfer(InputStream, OutputStream)} for File streams that uses
     * NIO to do a hopefully more efficient transfer.
     */
    public static void transfer(FileInputStream fileInStream, FileOutputStream fileOutStream)
            throws IOException {
        FileChannel fileInChannel = fileInStream.getChannel();
        FileChannel fileOutChannel = fileOutStream.getChannel();
        long fileInSize = fileInChannel.size();
        try {
            long transferred = fileInChannel.transferTo(0, fileInSize, fileOutChannel);
            if (transferred!=fileInSize) {
                /* Hmmm... need to rethink this algorithm if something goes wrong */
                throw new IOException("transfer() did not complete");
            }
        }
        finally {
            ensureClose(fileInChannel, fileOutChannel);
        }
    }
    
    /**
     * "Transfers" data from the given InputStream to the given OutputStream,
     * closing the InputStream afterwards. If the parameter closeOutputStream is true
     * then the OutputStream is closed too. If not, it will be flushed.
     *
     * @param inStream
     * @param outStream
     * @throws IOException
     */
    public static void transfer(InputStream inStream, OutputStream outStream, boolean closeOutputStream)
            throws IOException {
        transfer(inStream, outStream, true, closeOutputStream);
    }


    /**
     * "Transfers" data from the given InputStream to the given OutputStream,
     * optionally closing the given input and output streams afterwards.
     * <p>
     * Even if not closing the output stream, it will still be flushed.
     *
     * @param inStream
     * @param outStream
     * @throws IOException
     */
    public static void transfer(InputStream inStream, OutputStream outStream,
            boolean closeInputStream, boolean closeOutputStream) throws IOException {
        byte [] buffer = new byte[BUFFER_SIZE];
        int count;
        try {
            while ((count = inStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, count);
            }
        }
        finally {
            if (closeInputStream) {
                inStream.close();
            }
            if (closeOutputStream) {
                outStream.close();
            }
            else {
                outStream.flush();
            }
        }
    }

    //----------------------------------------------------------------------------
    // Reading methods
    
    /**
     * Reads all character data from the given Reader, returning a String
     * containing all of the data. The Reader will be buffered for efficiency and
     * will be closed once finished with.
     * Be careful reading in very large files - we will barf if MAX_FILE_SIZE is
     * passed as a safety precaution.
     *
     * @param reader source of string data
     * @return String representing the data read
     * @throws IOException
     */
    public static String readCharacterStream(Reader reader) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(reader);
        String line;
        int size = 0;
        StringBuilder result = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null) {
            size += line.length() + 1;
            if (size > MAX_TEXT_STREAM_SIZE) {
                throw new IOException("String data exceeds current maximum safe size ("
                        + MAX_TEXT_STREAM_SIZE + ")");
            }
            result.append(line).append("\n");
        }
        bufferedReader.close();
        return result.toString();
    }

    /**
     * Same as {@link #readCharacterStream(Reader)} but assumes the
     * stream is encoded as UTF-8.
     *
     * @param in InputStream supplying character data
     * @return String representing the data read in
     * @throws IOException
     */
    public static String readUnicodeStream(InputStream in) throws IOException {
        return readCharacterStream(new InputStreamReader(in, "UTF-8"));
    }

    /**
     * Same as {@link #readUnicodeStream(InputStream)} but accepts a plain
     * File object for convenience
     *
     * @param file File to read from
     * @return String representing the data we read in
     * @throws IOException
     */
    public static String readUnicodeFile(File file) throws IOException {
        InputStream inStream = new FileInputStream(file);
        try {
            return readUnicodeStream(inStream);   
        }
        finally {
            inStream.close();
        }
    }
}
