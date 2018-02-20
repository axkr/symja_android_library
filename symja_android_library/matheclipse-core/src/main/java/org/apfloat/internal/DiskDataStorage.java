package org.apfloat.internal;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.HashSet;
import java.util.Set;

import org.apfloat.ApfloatContext;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.spi.ArrayAccess;
import org.apfloat.spi.DataStorage;
import org.apfloat.spi.FilenameGenerator;
import org.apfloat.spi.MatrixStrategy;

/**
 * Abstract base class for disk-based data storage, containing the common
 * functionality independent of the element type.
 *
 * @version 1.7.0
 * @author Mikko Tommila
 */

public abstract class DiskDataStorage
    extends DataStorage
{
    private static class FileStorage
        implements Serializable
    {
        public FileStorage()
            throws ApfloatRuntimeException
        {
            init();
        }

        private void init()
            throws ApfloatRuntimeException
        {
            ApfloatContext ctx = ApfloatContext.getContext();
            FilenameGenerator generator = ctx.getFilenameGenerator();

            this.filename = generator.generateFilename();

            this.file = new File(this.filename);

            try
            {
                if (!this.file.createNewFile())
                {
                    throw new BackingStorageException("Failed to create new file \"" + this.filename + '\"');
                }

                // Ensure file is deleted always
                this.file.deleteOnExit();

                this.randomAccessFile = new RandomAccessFile(this.file, "rw");
            }
            catch (IOException ioe)
            {
                throw new BackingStorageException("Unable to access file \"" + this.filename + '\"', ioe);
            }

            this.fileChannel = this.randomAccessFile.getChannel();

            referenceFileStorage(this);     // To put to reference queue after garbage collection
        }

        public void setSize(long size)
            throws IOException, ApfloatRuntimeException
        {
            try
            {
                getRandomAccessFile().setLength(size);
            }
            catch (IOException ioe)
            {
                // Probably out of disk space - run garbage collection and process reference queue to delete unused files, then retry
                System.gc();
                forceFreeFileStorage();
                getRandomAccessFile().setLength(size);
            }
        }

        public void transferFrom(ReadableByteChannel in, long position, long size)
            throws ApfloatRuntimeException
        {
            try
            {
                if (in instanceof FileChannel)
                {
                    // Optimized transferFrom() between two FileChannels
                    while (size > 0)
                    {
                        long count = getFileChannel().transferFrom(in, position, size);
                        position += count;
                        size -= count;
                        assert (size >= 0);
                    }
                }
                else
                {
                    // The FileChannel transferFrom() uses an 8kB buffer, which is too small and inefficient
                    // So we use a similar mechanism but with a custom buffer size
                    ByteBuffer buffer = getDirectByteBuffer();
                    while (size > 0)
                    {
                        buffer.clear();
                        int readCount = (int) Math.min(size, buffer.capacity());
                        buffer.limit(readCount);
                        readCount = in.read(buffer);
                        buffer.flip();
                        while (readCount > 0)
                        {
                            int writeCount = getFileChannel().write(buffer, position);
                            position += writeCount;
                            size -= writeCount;
                            readCount -= writeCount;
                        }
                        assert (readCount == 0);
                        assert (size >= 0);
                    }
                }
            }
            catch (IOException ioe)
            {
                throw new BackingStorageException("Unable to write to file \"" + getFilename() + '\"', ioe);
            }
        }

        public void transferTo(WritableByteChannel out, long position, long size)
            throws ApfloatRuntimeException
        {
            try
            {
                if (out instanceof FileChannel)
                {
                    // Optimized transferTo() between two FileChannels
                    while (size > 0)
                    {
                        long count = getFileChannel().transferTo(position, size, out);
                        position += count;
                        size -= count;
                        assert (size >= 0);
                    }
                }
                else
                {
                    // The DiskChannel transferTo() uses an 8kB buffer, which is too small and inefficient
                    // So we use a similar mechanism but with a custom buffer size
                    ByteBuffer buffer = getDirectByteBuffer();
                    while (size > 0)
                    {
                        buffer.clear();
                        int readCount = (int) Math.min(size, buffer.capacity());
                        buffer.limit(readCount);
                        readCount = getFileChannel().read(buffer, position);
                        buffer.flip();
                        while (readCount > 0)
                        {
                            int writeCount = out.write(buffer);
                            position += writeCount;
                            size -= writeCount;
                            readCount -= writeCount;
                        }
                        assert (readCount == 0);
                        assert (size >= 0);
                    }
                }
            }
            catch (IOException ioe)
            {
                throw new BackingStorageException("Unable to read from file \"" + getFilename() + '\"', ioe);
            }
        }

        public String getFilename()
        {
            return this.filename;
        }

        public File getFile()
        {
            return this.file;
        }

        public RandomAccessFile getRandomAccessFile()
        {
            return this.randomAccessFile;
        }

        public FileChannel getFileChannel()
        {
            return this.fileChannel;
        }

        // Writes the file contents to the serialization stream
        private void writeObject(ObjectOutputStream out)
            throws IOException
        {
            long size = getFileChannel().size();
            out.writeLong(size);

            transferTo(Channels.newChannel(out), 0, size);

            out.defaultWriteObject();
        }

        // Reads file contents from the serialization stream
        private void readObject(ObjectInputStream in)
            throws IOException, ClassNotFoundException
        {
            init();

            long size = in.readLong();

            setSize(size);

            transferFrom(Channels.newChannel(in), 0, size);

            in.defaultReadObject();
        }

        private static final long serialVersionUID = 2062430603153403341L;

        // These fields are not serialized automatically
        private transient String filename;
        private transient File file;
        private transient RandomAccessFile randomAccessFile;
        private transient FileChannel fileChannel;
    }

    // A PhantomReference is used so it's only queued when the Apfloat can't become accessible in any way (e.g. if it's weakly referenced)
    private static class FileStorageReference
        extends PhantomReference<FileStorage>
    {
        public FileStorageReference(FileStorage fileStorage, ReferenceQueue<FileStorage> queue)
        {
            super(fileStorage, queue);

            this.file = fileStorage.getFile();
            this.randomAccessFile = fileStorage.getRandomAccessFile();
            this.fileChannel = fileStorage.getFileChannel();
        }

        public void dispose()
        {
            try
            {
                this.fileChannel.close();
            }
            catch (IOException ioe)
            {
                // Ignore
            }

            try
            {
                this.randomAccessFile.close();
            }
            catch (IOException ioe)
            {
                // Ignore
            }

            // If deletion fails now, at least deleteOnExit() has been called
            this.file.delete();
        }

        private File file;
        private RandomAccessFile randomAccessFile;
        private FileChannel fileChannel;
    }

    /**
     * Default constructor.
     */

    protected DiskDataStorage()
        throws ApfloatRuntimeException
    {
        this.fileStorage = createFileStorage();
    }

    /**
     * Subsequence constructor.
     *
     * @param diskDataStorage The originating data storage.
     * @param offset The subsequence starting position.
     * @param length The subsequence length.
     */

    protected DiskDataStorage(DiskDataStorage diskDataStorage, long offset, long length)
    {
        super(diskDataStorage, offset, length);
        this.fileStorage = diskDataStorage.fileStorage;
    }

    public boolean isCached()
    {
        return false;
    }

    protected void implCopyFrom(DataStorage dataStorage, long size)
        throws ApfloatRuntimeException
    {
        if (dataStorage == this)
        {
            setSize(size);
            return;
        }

        assert (size > 0);
        assert (!isReadOnly());
        assert (!isSubsequenced());

        int unitSize = getUnitSize();
        long byteSize = size * unitSize;

        assert (byteSize > 0);

        try
        {
            this.fileStorage.setSize(byteSize);

            long readSize = Math.min(size, dataStorage.getSize()),
                 oldSize = readSize * unitSize,
                 padSize = byteSize - oldSize;

            if (dataStorage instanceof DiskDataStorage)
            {
                // Optimized disk-to-disk copy

                DiskDataStorage that = (DiskDataStorage) dataStorage;
                that.transferTo(getFileChannel().position(0),
                                that.getOffset() * unitSize,
                                oldSize);
            }
            else
            {
                // Un-optimized copy from arbitrary data storage

                long position = 0;
                int bufferSize = getBlockSize() / unitSize;
                while (readSize > 0)
                {
                    int length = (int) Math.min(bufferSize, readSize);

                    ArrayAccess readArrayAccess = dataStorage.getArray(READ, position, length);
                    ArrayAccess writeArrayAccess = getArray(WRITE, position, length);
                    System.arraycopy(readArrayAccess.getData(), readArrayAccess.getOffset(), writeArrayAccess.getData(), writeArrayAccess.getOffset(), length);
                    writeArrayAccess.close();
                    readArrayAccess.close();

                    readSize -= length;
                    position += length;
                }
            }
            pad(oldSize, padSize);
        }
        catch (IOException ioe)
        {
            throw new BackingStorageException("Unable to copy to file \"" + getFilename() + '\"', ioe);
        }
    }

    protected long implGetSize()
        throws ApfloatRuntimeException
    {
        try
        {
            return getFileChannel().size() / getUnitSize();
        }
        catch (IOException ioe)
        {
            throw new BackingStorageException("Unable to access file \"" + getFilename() + '\"', ioe);
        }
    }

    protected void implSetSize(long size)
        throws ApfloatRuntimeException
    {
        assert (size > 0);
        assert (!isReadOnly());
        assert (!isSubsequenced());

        size *= getUnitSize();

        assert (size > 0);

        try
        {
            long oldSize = getFileChannel().size(),
                 padSize = size - oldSize;
            this.fileStorage.setSize(size);
            pad(oldSize, padSize);
        }
        catch (IOException ioe)
        {
            throw new BackingStorageException("Unable to access file \"" + getFilename() + '\"', ioe);
        }
    }

    protected synchronized ArrayAccess implGetArray(int mode, int startColumn, int columns, int rows)
        throws ApfloatRuntimeException
    {
        int width = (int) (getSize() / rows);

        if (columns != (columns & -columns) || rows != (rows & -rows) || startColumn + columns > width)
        {
            throw new ApfloatInternalException("Invalid size");
        }

        ArrayAccess arrayAccess = createArrayAccess(mode, startColumn, columns, rows);

        if ((mode & READ) != 0)
        {
            long readPosition = startColumn;
            int writePosition = 0;
            for (int i = 0; i < rows; i++)
            {
                readToArray(readPosition, arrayAccess, writePosition, columns);

                readPosition += width;
                writePosition += columns;
            }
        }

        return arrayAccess;
    }

    protected synchronized ArrayAccess implGetTransposedArray(int mode, int startColumn, int columns, int rows)
        throws ApfloatRuntimeException
    {
        int width = (int) (getSize() / rows);

        if (columns != (columns & -columns) || rows != (rows & -rows) || startColumn + columns > width)
        {
            throw new ApfloatInternalException("Invalid size");
        }

        int blockSize = columns * rows,
            b = Math.min(columns, rows);
        ArrayAccess arrayAccess = createTransposedArrayAccess(mode, startColumn, columns, rows);

        if ((mode & READ) != 0)
        {
            // Read the data from the input file in b x b blocks
            ApfloatContext ctx = ApfloatContext.getContext();
            MatrixStrategy matrixStrategy = ctx.getBuilderFactory().getMatrixBuilder().createMatrix();

            if (columns < rows)
            {
                // Taller than wide section
                long readPosition = startColumn;
                for (int i = 0; i < rows; i += b)
                {
                    int writePosition = i;

                    for (int j = 0; j < b; j++)
                    {
                        readToArray(readPosition, arrayAccess, writePosition, b);

                        readPosition += width;
                        writePosition += rows;
                    }

                    // Transpose the b x b block

                    ArrayAccess subArrayAccess = arrayAccess.subsequence(i, blockSize - i);
                    matrixStrategy.transposeSquare(subArrayAccess, b, rows);
                }
            }
            else
            {
                // Wider than tall section
                for (int i = 0; i < b; i++)
                {
                    long readPosition = startColumn + i * width;
                    int writePosition = i * b;

                    for (int j = 0; j < columns; j += b)
                    {
                        readToArray(readPosition, arrayAccess, writePosition, b);

                        readPosition += b;
                        writePosition += b * b;
                    }
                }

                for (int i = 0; i < blockSize; i += b * b)
                {
                    // Transpose the b x b block

                    ArrayAccess subArrayAccess = arrayAccess.subsequence(i, blockSize - i);
                    matrixStrategy.transposeSquare(subArrayAccess, b, b);
                }
            }
        }

        return arrayAccess;
    }

    /**
     * Write the data back to the same location in the file that was retrieved with
     * {@link #implGetArray(int,int,int,int)}.
     *
     * @param arrayAccess The transposed array access.
     * @param startColumn The starting column where data is stored.
     * @param columns The number of columns of data.
     * @param rows The number of rows of data.
     *
     * @since 1.7.0
     */

    protected synchronized void setArray(ArrayAccess arrayAccess, int startColumn, int columns, int rows)
        throws ApfloatRuntimeException
    {
        int width = (int) (getSize() / rows);

        int readPosition = 0;
        long writePosition = startColumn;
        for (int i = 0; i < rows; i++)
        {
            writeFromArray(arrayAccess, readPosition, writePosition, columns);

            readPosition += columns;
            writePosition += width;
        }
    }

    /**
     * Write the data back to the same location in the file that was retrieved with
     * {@link #implGetTransposedArray(int,int,int,int)}.
     *
     * @param arrayAccess The transposed array access.
     * @param startColumn The starting column where data is stored.
     * @param columns The number of columns of data.
     * @param rows The number of rows of data.
     *
     * @since 1.7.0
     */

    protected synchronized void setTransposedArray(ArrayAccess arrayAccess, int startColumn, int columns, int rows)
        throws ApfloatRuntimeException
    {
        int width = (int) (getSize() / rows);

        int blockSize = arrayAccess.getLength(),
            b = Math.min(columns, rows);

        ApfloatContext ctx = ApfloatContext.getContext();
        MatrixStrategy matrixStrategy = ctx.getBuilderFactory().getMatrixBuilder().createMatrix();

        if (columns < rows)
        {
            // Taller than wide section
            long writePosition = startColumn;
            for (int i = 0; i < rows; i += b)
            {
                int readPosition = i;

                // Transpose the b x b block

                ArrayAccess subArrayAccess = arrayAccess.subsequence(i, blockSize - i);
                matrixStrategy.transposeSquare(subArrayAccess, b, rows);

                for (int j = 0; j < b; j++)
                {
                    writeFromArray(arrayAccess, readPosition, writePosition, b);

                    readPosition += rows;
                    writePosition += width;
                }
            }
        }
        else
        {
            // Wider than tall section
            for (int i = 0; i < blockSize; i += b * b)
            {
                // Transpose the b x b block

                ArrayAccess subArrayAccess = arrayAccess.subsequence(i, blockSize - i);
                matrixStrategy.transposeSquare(subArrayAccess, b, b);
            }

            for (int i = 0; i < b; i++)
            {
                long writePosition = startColumn + i * width;
                int readPosition = i * b;

                for (int j = 0; j < columns; j += b)
                {
                    writeFromArray(arrayAccess, readPosition, writePosition, b);

                    readPosition += b * b;
                    writePosition += b;
                }
            }
        }
    }

    private void readToArray(long readPosition, ArrayAccess arrayAccess, int writePosition, int length)
        throws ApfloatRuntimeException
    {
        ArrayAccess readArrayAccess = getArray(READ, readPosition, length);
        System.arraycopy(readArrayAccess.getData(), readArrayAccess.getOffset(), arrayAccess.getData(), arrayAccess.getOffset() + writePosition, length);
        readArrayAccess.close();
    }

    private void writeFromArray(ArrayAccess arrayAccess, int readPosition, long writePosition, int length)
        throws ApfloatRuntimeException
    {
        ArrayAccess writeArrayAccess = getArray(WRITE, writePosition, length);
        System.arraycopy(arrayAccess.getData(), arrayAccess.getOffset() + readPosition, writeArrayAccess.getData(), writeArrayAccess.getOffset(), length);
        writeArrayAccess.close();
    }

    /**
     * Create an empty ArrayAccess.
     *
     * @param mode Whether the array is prepared for reading, writing or both. The value should be {@link #READ}, {@link #WRITE} or a combination of these.
     * @param startColumn The starting column where data is stored.
     * @param columns The number of columns of data.
     * @param rows The number of rows of data.
     *
     * @return Access to an empty array of the specified size and position.
     *
     * @since 1.7.0
     */

    protected abstract ArrayAccess createArrayAccess(int mode, int startColumn, int columns, int rows);

    /**
     * Create an empty transposed ArrayAccess.
     *
     * @param mode Whether the array is prepared for reading, writing or both. The value should be {@link #READ}, {@link #WRITE} or a combination of these.
     * @param startColumn The starting column where data is stored.
     * @param columns The number of columns of data.
     * @param rows The number of rows of data.
     *
     * @return Access to an empty array of the specified size and position.
     *
     * @since 1.7.0
     */

    protected abstract ArrayAccess createTransposedArrayAccess(int mode, int startColumn, int columns, int rows);

    /**
     * Transfer from a readable channel, possibly in multiple chunks.
     *
     * @param in Input channel.
     * @param position Start position of transfer.
     * @param size Total number of bytes to transfer.
     */

    protected void transferFrom(ReadableByteChannel in, long position, long size)
        throws ApfloatRuntimeException
    {
        this.fileStorage.transferFrom(in, position, size);
    }

    /**
     * Transfer to a writable channel, possibly in multiple chunks.
     *
     * @param out Output channel.
     * @param position Start position of transfer.
     * @param size Total number of bytes to transfer.
     */

    protected void transferTo(WritableByteChannel out, long position, long size)
        throws ApfloatRuntimeException
    {
        this.fileStorage.transferTo(out, position, size);
    }

    /**
     * Convenience method for getting the block size (in bytes) for the
     * current {@link ApfloatContext}.
     *
     * @return I/O block size, in bytes.
     */

    protected static int getBlockSize()
    {
        ApfloatContext ctx = ApfloatContext.getContext();
        return ctx.getBlockSize();
    }

    /**
     * Size of the element type, in bytes.
     *
     * @return Size of the element type, in bytes.
     */

    protected abstract int getUnitSize();

    /**
     * Filename of the underlying disk data storage.
     *
     * @return Filename of the underlying disk data storage.
     */

    protected final String getFilename()
    {
        return this.fileStorage.getFilename();
    }

    /**
     * The <code>FileChannel</code> of the underlying disk file.
     *
     * @return The <code>FileChannel</code> of the underlying disk file.
     */

    protected final FileChannel getFileChannel()
    {
        return this.fileStorage.getFileChannel();
    }

    static synchronized void cleanUp()
        throws ApfloatRuntimeException
    {
        for (FileStorageReference reference : DiskDataStorage.references)
        {
            // Just remove everything that has been created
            reference.dispose();
            reference.clear();
        }
        DiskDataStorage.references.clear();
        DiskDataStorage.cleanUp = true;
    }

    static synchronized void gc()
        throws ApfloatRuntimeException
    {
        forceFreeFileStorage();
    }

    private void pad(long position, long size)
        throws IOException, ApfloatRuntimeException
    {
        transferFrom(ZERO_CHANNEL, position, size);
    }

    private static final ReadableByteChannel ZERO_CHANNEL = new ReadableByteChannel()
    {
        public int read(ByteBuffer buffer)
        {
            int writeLength = buffer.remaining();

            for (int i = 0; i < writeLength; i++)
            {
                buffer.put((byte) 0);
            }

            return writeLength;
        }

        public void close() {}
        public boolean isOpen() { return true; }
    };

    private static synchronized FileStorage createFileStorage()
        throws ApfloatInternalException
    {
        if (DiskDataStorage.cleanUp)
        {
            throw new ApfloatInternalException("Shutdown has been initiated, clean-up is in progress");
        }

        freeFileStorage();                  // Before creating new files, delete the ones that have been garbage collected
        FileStorage fileStorage = new FileStorage();

        return fileStorage;
    };

    private static synchronized void referenceFileStorage(FileStorage fileStorage)
        throws ApfloatInternalException
    {
        if (DiskDataStorage.cleanUp)
        {
            new FileStorageReference(fileStorage, null).dispose();      // Delete the file immediately; it skipped the clean-up procedure
            throw new ApfloatInternalException("Shutdown has been initiated, clean-up is in progress");
        }

        // The reference might not really be needed for anything else than queuing in the reference queue,
        // but we have to keep a hard reference to it to have it queued
        FileStorageReference reference = new FileStorageReference(fileStorage, DiskDataStorage.referenceQueue);
        DiskDataStorage.references.add(reference);
    }

    private static synchronized void freeFileStorage()
    {
        FileStorageReference reference;
        // Just check if there's anything that can be cleaned up immediately
        while ((reference = (FileStorageReference) DiskDataStorage.referenceQueue.poll()) != null)
        {
            reference.dispose();
            reference.clear();
            DiskDataStorage.references.remove(reference);
        }
    }

    private static synchronized void forceFreeFileStorage()
        throws ApfloatInternalException
    {
        try
        {
            FileStorageReference reference;
            // Instead of poll(), wait for some time for GC to finish; we want to free as much disk as possible e.g. if we are out of disk space so waiting some time is not that bad
            while ((reference = (FileStorageReference) DiskDataStorage.referenceQueue.remove(TIMEOUT)) != null)
            {
                reference.dispose();
                reference.clear();
                DiskDataStorage.references.remove(reference);
            }
        }
        catch (InterruptedException ie)
        {
            throw new ApfloatInternalException("Reference queue polling was interrupted", ie);
        }
    }

    private static ByteBuffer getDirectByteBuffer()
    {
        // Since direct buffers are allocated outside of the heap they can behave strangely in relation to GC
        // So we try to make them as long-lived as possible and cache them in a ThreadLocal
        ByteBuffer buffer = null;
        int blockSize = getBlockSize();
        SoftReference<ByteBuffer> reference = DiskDataStorage.threadLocal.get();
        if (reference != null)
        {
            buffer = reference.get();
            if (buffer != null && buffer.capacity() != blockSize)
            {
                // Clear references to the direct buffer so it may be GC'd
                reference.clear();
                buffer = null;
            }
        }
        if (buffer == null)
        {
            buffer = ByteBuffer.allocateDirect(blockSize);
            reference = new SoftReference<ByteBuffer>(buffer);
            DiskDataStorage.threadLocal.set(reference);
        }

        return buffer;
    }

    private static final long serialVersionUID = 741984828408146034L;

    private static final long TIMEOUT = 1000;   // Reference queue waiting timeout when forcing deleting garbage collected files

    private static ReferenceQueue<FileStorage> referenceQueue = new ReferenceQueue<FileStorage>();
    private static Set<FileStorageReference> references = new HashSet<FileStorageReference>();
    private static ThreadLocal<SoftReference<ByteBuffer>> threadLocal = new ThreadLocal<SoftReference<ByteBuffer>>();
    private static boolean cleanUp = false;

    private FileStorage fileStorage;
}
