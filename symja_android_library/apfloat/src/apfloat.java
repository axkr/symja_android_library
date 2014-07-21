import java.util.ListResourceBundle;

//import java.lang.management.ManagementFactory;
//import java.lang.management.MemoryMXBean;
//import java.lang.management.MemoryUsage;

import org.apfloat.ApfloatContext;
import org.apfloat.spi.Util;

/**
 * Default initial settings for the global {@link ApfloatContext}.
 *
 * @version 1.8.0
 * @author Mikko Tommila
 */

public class apfloat
    extends ListResourceBundle
{
    public Object[][] getContents()
    {
        return CONTENTS;
    }

    private static final Object[][] CONTENTS;

    static
    {
        // Try to use up to 80% of total memory and all processors
        long totalMemory;
//        try
//        {
//            MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
//            MemoryUsage memoryUsage = memoryBean.getHeapMemoryUsage();
//            totalMemory = Math.max(memoryUsage.getCommitted(), memoryUsage.getMax());
//        }
//        catch (NoClassDefFoundError ncdfe)
//        {
            // The ManagementFactory class might be unavailable
            totalMemory = Runtime.getRuntime().maxMemory();
//        }

        long maxMemoryBlockSize = Util.round23down(totalMemory / 5 * 4);
        int numberOfProcessors = Runtime.getRuntime().availableProcessors();
        long memoryThreshold = Math.max(maxMemoryBlockSize >> 10, 65536);
        int blockSize = Util.round2down((int) Math.min(memoryThreshold, Integer.MAX_VALUE));

        // Guess if we are using a 32-bit or 64-bit platform
        String elementType = (totalMemory >= 4L << 30 ? "Long" : "Int");

        Object[][] contents =
        {
            { ApfloatContext.BUILDER_FACTORY, "org.apfloat.internal." + elementType + "BuilderFactory" },
            { ApfloatContext.DEFAULT_RADIX, "10" },
            { ApfloatContext.MAX_MEMORY_BLOCK_SIZE, String.valueOf(maxMemoryBlockSize) },
            { ApfloatContext.CACHE_L1_SIZE, "8192" },
            { ApfloatContext.CACHE_L2_SIZE, "262144" },
            { ApfloatContext.CACHE_BURST, "32" },
            { ApfloatContext.MEMORY_THRESHOLD, String.valueOf(memoryThreshold) },
            { ApfloatContext.SHARED_MEMORY_TRESHOLD, String.valueOf(maxMemoryBlockSize / numberOfProcessors / 32) },
            { ApfloatContext.BLOCK_SIZE, String.valueOf(blockSize) },
            { ApfloatContext.NUMBER_OF_PROCESSORS, String.valueOf(numberOfProcessors) },
            { ApfloatContext.FILE_PATH, "" },
            { ApfloatContext.FILE_INITIAL_VALUE, "0" },
            { ApfloatContext.FILE_SUFFIX, ".ap" },
            { ApfloatContext.CLEANUP_AT_EXIT, "true" }
        };

        CONTENTS = contents;
    }
}
