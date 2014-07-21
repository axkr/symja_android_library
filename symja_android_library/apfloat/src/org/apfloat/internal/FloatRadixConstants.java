package org.apfloat.internal;

/**
 * Constants related to different radixes for the <code>float</code> data type.
 *
 * @version 1.0
 * @author Mikko Tommila
 */

public interface FloatRadixConstants
{
    /**
     * Bases for radixes 2, ..., 36. The base is the radix to the maximum power
     * so that the base is less than all moduli used.
     */

    public static final float BASE[] = { (float) -1L, (float) -1L, (float) 8388608L, (float) 4782969L, (float) 4194304L, (float) 9765625L, (float) 10077696L, (float) 5764801L, (float) 2097152L, (float) 4782969L, (float) 10000000L, (float) 1771561L, (float) 2985984L, (float) 4826809L, (float) 7529536L, (float) 11390625L, (float) 1048576L, (float) 1419857L, (float) 1889568L, (float) 2476099L, (float) 3200000L, (float) 4084101L, (float) 5153632L, (float) 6436343L, (float) 7962624L, (float) 9765625L, (float) 11881376L, (float) 531441L, (float) 614656L, (float) 707281L, (float) 810000L, (float) 923521L, (float) 1048576L, (float) 1185921L, (float) 1336336L, (float) 1500625L, (float) 1679616L };

    /**
     * The power of the radix in each base.
     */

    public static final int BASE_DIGITS[] = { -1, -1, 23, 14, 11, 10, 9, 8, 7, 7, 7, 6, 6, 6, 6, 6, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4 };

    /**
     * The minimum number in each radix to have the specified amount of digits.
     */

    public static final float MINIMUM_FOR_DIGITS[][] = { null, null, { (float) 1L, (float) 2L, (float) 4L, (float) 8L, (float) 16L, (float) 32L, (float) 64L, (float) 128L, (float) 256L, (float) 512L, (float) 1024L, (float) 2048L, (float) 4096L, (float) 8192L, (float) 16384L, (float) 32768L, (float) 65536L, (float) 131072L, (float) 262144L, (float) 524288L, (float) 1048576L, (float) 2097152L, (float) 4194304L }, { (float) 1L, (float) 3L, (float) 9L, (float) 27L, (float) 81L, (float) 243L, (float) 729L, (float) 2187L, (float) 6561L, (float) 19683L, (float) 59049L, (float) 177147L, (float) 531441L, (float) 1594323L }, { (float) 1L, (float) 4L, (float) 16L, (float) 64L, (float) 256L, (float) 1024L, (float) 4096L, (float) 16384L, (float) 65536L, (float) 262144L, (float) 1048576L }, { (float) 1L, (float) 5L, (float) 25L, (float) 125L, (float) 625L, (float) 3125L, (float) 15625L, (float) 78125L, (float) 390625L, (float) 1953125L }, { (float) 1L, (float) 6L, (float) 36L, (float) 216L, (float) 1296L, (float) 7776L, (float) 46656L, (float) 279936L, (float) 1679616L }, { (float) 1L, (float) 7L, (float) 49L, (float) 343L, (float) 2401L, (float) 16807L, (float) 117649L, (float) 823543L }, { (float) 1L, (float) 8L, (float) 64L, (float) 512L, (float) 4096L, (float) 32768L, (float) 262144L }, { (float) 1L, (float) 9L, (float) 81L, (float) 729L, (float) 6561L, (float) 59049L, (float) 531441L }, { (float) 1L, (float) 10L, (float) 100L, (float) 1000L, (float) 10000L, (float) 100000L, (float) 1000000L }, { (float) 1L, (float) 11L, (float) 121L, (float) 1331L, (float) 14641L, (float) 161051L }, { (float) 1L, (float) 12L, (float) 144L, (float) 1728L, (float) 20736L, (float) 248832L }, { (float) 1L, (float) 13L, (float) 169L, (float) 2197L, (float) 28561L, (float) 371293L }, { (float) 1L, (float) 14L, (float) 196L, (float) 2744L, (float) 38416L, (float) 537824L }, { (float) 1L, (float) 15L, (float) 225L, (float) 3375L, (float) 50625L, (float) 759375L }, { (float) 1L, (float) 16L, (float) 256L, (float) 4096L, (float) 65536L }, { (float) 1L, (float) 17L, (float) 289L, (float) 4913L, (float) 83521L }, { (float) 1L, (float) 18L, (float) 324L, (float) 5832L, (float) 104976L }, { (float) 1L, (float) 19L, (float) 361L, (float) 6859L, (float) 130321L }, { (float) 1L, (float) 20L, (float) 400L, (float) 8000L, (float) 160000L }, { (float) 1L, (float) 21L, (float) 441L, (float) 9261L, (float) 194481L }, { (float) 1L, (float) 22L, (float) 484L, (float) 10648L, (float) 234256L }, { (float) 1L, (float) 23L, (float) 529L, (float) 12167L, (float) 279841L }, { (float) 1L, (float) 24L, (float) 576L, (float) 13824L, (float) 331776L }, { (float) 1L, (float) 25L, (float) 625L, (float) 15625L, (float) 390625L }, { (float) 1L, (float) 26L, (float) 676L, (float) 17576L, (float) 456976L }, { (float) 1L, (float) 27L, (float) 729L, (float) 19683L }, { (float) 1L, (float) 28L, (float) 784L, (float) 21952L }, { (float) 1L, (float) 29L, (float) 841L, (float) 24389L }, { (float) 1L, (float) 30L, (float) 900L, (float) 27000L }, { (float) 1L, (float) 31L, (float) 961L, (float) 29791L }, { (float) 1L, (float) 32L, (float) 1024L, (float) 32768L }, { (float) 1L, (float) 33L, (float) 1089L, (float) 35937L }, { (float) 1L, (float) 34L, (float) 1156L, (float) 39304L }, { (float) 1L, (float) 35L, (float) 1225L, (float) 42875L }, { (float) 1L, (float) 36L, (float) 1296L, (float) 46656L } };

    /**
     * Maximum allowed exponent for each radix.
     */

    public static final long MAX_EXPONENT[] = { -1L, -1L, 401016175515425029L, 658812288346769694L, 838488366986797794L, 922337203685477574L, 1024819115206086194L, 1152921504606846969L, 1317624576693539395L, 1317624576693539395L, 1317624576693539395L, 1537228672809129295L, 1537228672809129295L, 1537228672809129295L, 1537228672809129295L, 1537228672809129295L, 1844674407370955155L, 1844674407370955155L, 1844674407370955155L, 1844674407370955155L, 1844674407370955155L, 1844674407370955155L, 1844674407370955155L, 1844674407370955155L, 1844674407370955155L, 1844674407370955155L, 1844674407370955155L, 2305843009213693945L, 2305843009213693945L, 2305843009213693945L, 2305843009213693945L, 2305843009213693945L, 2305843009213693945L, 2305843009213693945L, 2305843009213693945L, 2305843009213693945L, 2305843009213693945L };
}
