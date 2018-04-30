package org.matheclipse.core.numbertheory;

import java.math.BigInteger;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import org.matheclipse.combinatoric.KSubsets;
import org.matheclipse.combinatoric.KSubsets.KSubsetsList;
import org.matheclipse.core.eval.exception.ReturnException;
import org.matheclipse.core.eval.util.OpenIntToIExprHashMap;
import org.matheclipse.core.expression.AbstractIntegerSym;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.expression.IntegerSym;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.IRational;

import com.google.common.math.BigIntegerMath;
import com.google.common.math.LongMath;

/**
 * Provides primality probabilistic methods.
 * 
 * Copied from Apache Harmony projects java.math package.
 * 
 */
public class Primality {

	private static class PrimePowerTreedMap extends TreeMap<BigInteger, Integer> {
		private static final long serialVersionUID = 7802239809732541730L;

		@Override
		public Integer put(BigInteger key, Integer value) {
			Integer result = super.put(key, value);
			if (size() > 1) {
				// not a prime power:
				throw ReturnException.RETURN_FALSE;
			}
			return result;
		}
	}

	private static class SquareFreeTreedMap extends TreeMap<BigInteger, Integer> {
		private static final long serialVersionUID = -7769218967264615452L;

		@Override
		public Integer put(BigInteger key, Integer value) {
			Integer result = super.put(key, value);
			if (value > 1) {
				// not a square free number:
				throw ReturnException.RETURN_FALSE;
			}
			if (result != null && result > 1) {
				// not a square free number:
				throw ReturnException.RETURN_FALSE;
			}
			return result;
		}
	}

	private final static SecureRandom random = new SecureRandom();
	private final static BigInteger TWO = new BigInteger("2");

	/** Just to denote that this class can't be instantied. */
	private Primality() {
	}

	/* Private Fields */

	/** All prime numbers with bit length lesser than 10 bits. */
	private static final int primes[] = { 2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71,
			73, 79, 83, 89, 97, 101, 103, 107, 109, 113, 127, 131, 137, 139, 149, 151, 157, 163, 167, 173, 179, 181,
			191, 193, 197, 199, 211, 223, 227, 229, 233, 239, 241, 251, 257, 263, 269, 271, 277, 281, 283, 293, 307,
			311, 313, 317, 331, 337, 347, 349, 353, 359, 367, 373, 379, 383, 389, 397, 401, 409, 419, 421, 431, 433,
			439, 443, 449, 457, 461, 463, 467, 479, 487, 491, 499, 503, 509, 521, 523, 541, 547, 557, 563, 569, 571,
			577, 587, 593, 599, 601, 607, 613, 617, 619, 631, 641, 643, 647, 653, 659, 661, 673, 677, 683, 691, 701,
			709, 719, 727, 733, 739, 743, 751, 757, 761, 769, 773, 787, 797, 809, 811, 821, 823, 827, 829, 839, 853,
			857, 859, 863, 877, 881, 883, 887, 907, 911, 919, 929, 937, 941, 947, 953, 967, 971, 977, 983, 991, 997,
			1009, 1013, 1019, 1021 };

	/**
	 * All prime numbers shorter than Short.MAX_VALUE which are not in <code>primes[]</code> array.
	 */
	private static final short[] SHORT_PRIMES = { 1031, 1033, 1039, 1049, 1051, 1061, 1063, 1069, 1087, 1091, 1093,
			1097, 1103, 1109, 1117, 1123, 1129, 1151, 1153, 1163, 1171, 1181, 1187, 1193, 1201, 1213, 1217, 1223, 1229,
			1231, 1237, 1249, 1259, 1277, 1279, 1283, 1289, 1291, 1297, 1301, 1303, 1307, 1319, 1321, 1327, 1361, 1367,
			1373, 1381, 1399, 1409, 1423, 1427, 1429, 1433, 1439, 1447, 1451, 1453, 1459, 1471, 1481, 1483, 1487, 1489,
			1493, 1499, 1511, 1523, 1531, 1543, 1549, 1553, 1559, 1567, 1571, 1579, 1583, 1597, 1601, 1607, 1609, 1613,
			1619, 1621, 1627, 1637, 1657, 1663, 1667, 1669, 1693, 1697, 1699, 1709, 1721, 1723, 1733, 1741, 1747, 1753,
			1759, 1777, 1783, 1787, 1789, 1801, 1811, 1823, 1831, 1847, 1861, 1867, 1871, 1873, 1877, 1879, 1889, 1901,
			1907, 1913, 1931, 1933, 1949, 1951, 1973, 1979, 1987, 1993, 1997, 1999, 2003, 2011, 2017, 2027, 2029, 2039,
			2053, 2063, 2069, 2081, 2083, 2087, 2089, 2099, 2111, 2113, 2129, 2131, 2137, 2141, 2143, 2153, 2161, 2179,
			2203, 2207, 2213, 2221, 2237, 2239, 2243, 2251, 2267, 2269, 2273, 2281, 2287, 2293, 2297, 2309, 2311, 2333,
			2339, 2341, 2347, 2351, 2357, 2371, 2377, 2381, 2383, 2389, 2393, 2399, 2411, 2417, 2423, 2437, 2441, 2447,
			2459, 2467, 2473, 2477, 2503, 2521, 2531, 2539, 2543, 2549, 2551, 2557, 2579, 2591, 2593, 2609, 2617, 2621,
			2633, 2647, 2657, 2659, 2663, 2671, 2677, 2683, 2687, 2689, 2693, 2699, 2707, 2711, 2713, 2719, 2729, 2731,
			2741, 2749, 2753, 2767, 2777, 2789, 2791, 2797, 2801, 2803, 2819, 2833, 2837, 2843, 2851, 2857, 2861, 2879,
			2887, 2897, 2903, 2909, 2917, 2927, 2939, 2953, 2957, 2963, 2969, 2971, 2999, 3001, 3011, 3019, 3023, 3037,
			3041, 3049, 3061, 3067, 3079, 3083, 3089, 3109, 3119, 3121, 3137, 3163, 3167, 3169, 3181, 3187, 3191, 3203,
			3209, 3217, 3221, 3229, 3251, 3253, 3257, 3259, 3271, 3299, 3301, 3307, 3313, 3319, 3323, 3329, 3331, 3343,
			3347, 3359, 3361, 3371, 3373, 3389, 3391, 3407, 3413, 3433, 3449, 3457, 3461, 3463, 3467, 3469, 3491, 3499,
			3511, 3517, 3527, 3529, 3533, 3539, 3541, 3547, 3557, 3559, 3571, 3581, 3583, 3593, 3607, 3613, 3617, 3623,
			3631, 3637, 3643, 3659, 3671, 3673, 3677, 3691, 3697, 3701, 3709, 3719, 3727, 3733, 3739, 3761, 3767, 3769,
			3779, 3793, 3797, 3803, 3821, 3823, 3833, 3847, 3851, 3853, 3863, 3877, 3881, 3889, 3907, 3911, 3917, 3919,
			3923, 3929, 3931, 3943, 3947, 3967, 3989, 4001, 4003, 4007, 4013, 4019, 4021, 4027, 4049, 4051, 4057, 4073,
			4079, 4091, 4093, 4099, 4111, 4127, 4129, 4133, 4139, 4153, 4157, 4159, 4177, 4201, 4211, 4217, 4219, 4229,
			4231, 4241, 4243, 4253, 4259, 4261, 4271, 4273, 4283, 4289, 4297, 4327, 4337, 4339, 4349, 4357, 4363, 4373,
			4391, 4397, 4409, 4421, 4423, 4441, 4447, 4451, 4457, 4463, 4481, 4483, 4493, 4507, 4513, 4517, 4519, 4523,
			4547, 4549, 4561, 4567, 4583, 4591, 4597, 4603, 4621, 4637, 4639, 4643, 4649, 4651, 4657, 4663, 4673, 4679,
			4691, 4703, 4721, 4723, 4729, 4733, 4751, 4759, 4783, 4787, 4789, 4793, 4799, 4801, 4813, 4817, 4831, 4861,
			4871, 4877, 4889, 4903, 4909, 4919, 4931, 4933, 4937, 4943, 4951, 4957, 4967, 4969, 4973, 4987, 4993, 4999,
			5003, 5009, 5011, 5021, 5023, 5039, 5051, 5059, 5077, 5081, 5087, 5099, 5101, 5107, 5113, 5119, 5147, 5153,
			5167, 5171, 5179, 5189, 5197, 5209, 5227, 5231, 5233, 5237, 5261, 5273, 5279, 5281, 5297, 5303, 5309, 5323,
			5333, 5347, 5351, 5381, 5387, 5393, 5399, 5407, 5413, 5417, 5419, 5431, 5437, 5441, 5443, 5449, 5471, 5477,
			5479, 5483, 5501, 5503, 5507, 5519, 5521, 5527, 5531, 5557, 5563, 5569, 5573, 5581, 5591, 5623, 5639, 5641,
			5647, 5651, 5653, 5657, 5659, 5669, 5683, 5689, 5693, 5701, 5711, 5717, 5737, 5741, 5743, 5749, 5779, 5783,
			5791, 5801, 5807, 5813, 5821, 5827, 5839, 5843, 5849, 5851, 5857, 5861, 5867, 5869, 5879, 5881, 5897, 5903,
			5923, 5927, 5939, 5953, 5981, 5987, 6007, 6011, 6029, 6037, 6043, 6047, 6053, 6067, 6073, 6079, 6089, 6091,
			6101, 6113, 6121, 6131, 6133, 6143, 6151, 6163, 6173, 6197, 6199, 6203, 6211, 6217, 6221, 6229, 6247, 6257,
			6263, 6269, 6271, 6277, 6287, 6299, 6301, 6311, 6317, 6323, 6329, 6337, 6343, 6353, 6359, 6361, 6367, 6373,
			6379, 6389, 6397, 6421, 6427, 6449, 6451, 6469, 6473, 6481, 6491, 6521, 6529, 6547, 6551, 6553, 6563, 6569,
			6571, 6577, 6581, 6599, 6607, 6619, 6637, 6653, 6659, 6661, 6673, 6679, 6689, 6691, 6701, 6703, 6709, 6719,
			6733, 6737, 6761, 6763, 6779, 6781, 6791, 6793, 6803, 6823, 6827, 6829, 6833, 6841, 6857, 6863, 6869, 6871,
			6883, 6899, 6907, 6911, 6917, 6947, 6949, 6959, 6961, 6967, 6971, 6977, 6983, 6991, 6997, 7001, 7013, 7019,
			7027, 7039, 7043, 7057, 7069, 7079, 7103, 7109, 7121, 7127, 7129, 7151, 7159, 7177, 7187, 7193, 7207, 7211,
			7213, 7219, 7229, 7237, 7243, 7247, 7253, 7283, 7297, 7307, 7309, 7321, 7331, 7333, 7349, 7351, 7369, 7393,
			7411, 7417, 7433, 7451, 7457, 7459, 7477, 7481, 7487, 7489, 7499, 7507, 7517, 7523, 7529, 7537, 7541, 7547,
			7549, 7559, 7561, 7573, 7577, 7583, 7589, 7591, 7603, 7607, 7621, 7639, 7643, 7649, 7669, 7673, 7681, 7687,
			7691, 7699, 7703, 7717, 7723, 7727, 7741, 7753, 7757, 7759, 7789, 7793, 7817, 7823, 7829, 7841, 7853, 7867,
			7873, 7877, 7879, 7883, 7901, 7907, 7919, 7927, 7933, 7937, 7949, 7951, 7963, 7993, 8009, 8011, 8017, 8039,
			8053, 8059, 8069, 8081, 8087, 8089, 8093, 8101, 8111, 8117, 8123, 8147, 8161, 8167, 8171, 8179, 8191, 8209,
			8219, 8221, 8231, 8233, 8237, 8243, 8263, 8269, 8273, 8287, 8291, 8293, 8297, 8311, 8317, 8329, 8353, 8363,
			8369, 8377, 8387, 8389, 8419, 8423, 8429, 8431, 8443, 8447, 8461, 8467, 8501, 8513, 8521, 8527, 8537, 8539,
			8543, 8563, 8573, 8581, 8597, 8599, 8609, 8623, 8627, 8629, 8641, 8647, 8663, 8669, 8677, 8681, 8689, 8693,
			8699, 8707, 8713, 8719, 8731, 8737, 8741, 8747, 8753, 8761, 8779, 8783, 8803, 8807, 8819, 8821, 8831, 8837,
			8839, 8849, 8861, 8863, 8867, 8887, 8893, 8923, 8929, 8933, 8941, 8951, 8963, 8969, 8971, 8999, 9001, 9007,
			9011, 9013, 9029, 9041, 9043, 9049, 9059, 9067, 9091, 9103, 9109, 9127, 9133, 9137, 9151, 9157, 9161, 9173,
			9181, 9187, 9199, 9203, 9209, 9221, 9227, 9239, 9241, 9257, 9277, 9281, 9283, 9293, 9311, 9319, 9323, 9337,
			9341, 9343, 9349, 9371, 9377, 9391, 9397, 9403, 9413, 9419, 9421, 9431, 9433, 9437, 9439, 9461, 9463, 9467,
			9473, 9479, 9491, 9497, 9511, 9521, 9533, 9539, 9547, 9551, 9587, 9601, 9613, 9619, 9623, 9629, 9631, 9643,
			9649, 9661, 9677, 9679, 9689, 9697, 9719, 9721, 9733, 9739, 9743, 9749, 9767, 9769, 9781, 9787, 9791, 9803,
			9811, 9817, 9829, 9833, 9839, 9851, 9857, 9859, 9871, 9883, 9887, 9901, 9907, 9923, 9929, 9931, 9941, 9949,
			9967, 9973, 10007, 10009, 10037, 10039, 10061, 10067, 10069, 10079, 10091, 10093, 10099, 10103, 10111,
			10133, 10139, 10141, 10151, 10159, 10163, 10169, 10177, 10181, 10193, 10211, 10223, 10243, 10247, 10253,
			10259, 10267, 10271, 10273, 10289, 10301, 10303, 10313, 10321, 10331, 10333, 10337, 10343, 10357, 10369,
			10391, 10399, 10427, 10429, 10433, 10453, 10457, 10459, 10463, 10477, 10487, 10499, 10501, 10513, 10529,
			10531, 10559, 10567, 10589, 10597, 10601, 10607, 10613, 10627, 10631, 10639, 10651, 10657, 10663, 10667,
			10687, 10691, 10709, 10711, 10723, 10729, 10733, 10739, 10753, 10771, 10781, 10789, 10799, 10831, 10837,
			10847, 10853, 10859, 10861, 10867, 10883, 10889, 10891, 10903, 10909, 10937, 10939, 10949, 10957, 10973,
			10979, 10987, 10993, 11003, 11027, 11047, 11057, 11059, 11069, 11071, 11083, 11087, 11093, 11113, 11117,
			11119, 11131, 11149, 11159, 11161, 11171, 11173, 11177, 11197, 11213, 11239, 11243, 11251, 11257, 11261,
			11273, 11279, 11287, 11299, 11311, 11317, 11321, 11329, 11351, 11353, 11369, 11383, 11393, 11399, 11411,
			11423, 11437, 11443, 11447, 11467, 11471, 11483, 11489, 11491, 11497, 11503, 11519, 11527, 11549, 11551,
			11579, 11587, 11593, 11597, 11617, 11621, 11633, 11657, 11677, 11681, 11689, 11699, 11701, 11717, 11719,
			11731, 11743, 11777, 11779, 11783, 11789, 11801, 11807, 11813, 11821, 11827, 11831, 11833, 11839, 11863,
			11867, 11887, 11897, 11903, 11909, 11923, 11927, 11933, 11939, 11941, 11953, 11959, 11969, 11971, 11981,
			11987, 12007, 12011, 12037, 12041, 12043, 12049, 12071, 12073, 12097, 12101, 12107, 12109, 12113, 12119,
			12143, 12149, 12157, 12161, 12163, 12197, 12203, 12211, 12227, 12239, 12241, 12251, 12253, 12263, 12269,
			12277, 12281, 12289, 12301, 12323, 12329, 12343, 12347, 12373, 12377, 12379, 12391, 12401, 12409, 12413,
			12421, 12433, 12437, 12451, 12457, 12473, 12479, 12487, 12491, 12497, 12503, 12511, 12517, 12527, 12539,
			12541, 12547, 12553, 12569, 12577, 12583, 12589, 12601, 12611, 12613, 12619, 12637, 12641, 12647, 12653,
			12659, 12671, 12689, 12697, 12703, 12713, 12721, 12739, 12743, 12757, 12763, 12781, 12791, 12799, 12809,
			12821, 12823, 12829, 12841, 12853, 12889, 12893, 12899, 12907, 12911, 12917, 12919, 12923, 12941, 12953,
			12959, 12967, 12973, 12979, 12983, 13001, 13003, 13007, 13009, 13033, 13037, 13043, 13049, 13063, 13093,
			13099, 13103, 13109, 13121, 13127, 13147, 13151, 13159, 13163, 13171, 13177, 13183, 13187, 13217, 13219,
			13229, 13241, 13249, 13259, 13267, 13291, 13297, 13309, 13313, 13327, 13331, 13337, 13339, 13367, 13381,
			13397, 13399, 13411, 13417, 13421, 13441, 13451, 13457, 13463, 13469, 13477, 13487, 13499, 13513, 13523,
			13537, 13553, 13567, 13577, 13591, 13597, 13613, 13619, 13627, 13633, 13649, 13669, 13679, 13681, 13687,
			13691, 13693, 13697, 13709, 13711, 13721, 13723, 13729, 13751, 13757, 13759, 13763, 13781, 13789, 13799,
			13807, 13829, 13831, 13841, 13859, 13873, 13877, 13879, 13883, 13901, 13903, 13907, 13913, 13921, 13931,
			13933, 13963, 13967, 13997, 13999, 14009, 14011, 14029, 14033, 14051, 14057, 14071, 14081, 14083, 14087,
			14107, 14143, 14149, 14153, 14159, 14173, 14177, 14197, 14207, 14221, 14243, 14249, 14251, 14281, 14293,
			14303, 14321, 14323, 14327, 14341, 14347, 14369, 14387, 14389, 14401, 14407, 14411, 14419, 14423, 14431,
			14437, 14447, 14449, 14461, 14479, 14489, 14503, 14519, 14533, 14537, 14543, 14549, 14551, 14557, 14561,
			14563, 14591, 14593, 14621, 14627, 14629, 14633, 14639, 14653, 14657, 14669, 14683, 14699, 14713, 14717,
			14723, 14731, 14737, 14741, 14747, 14753, 14759, 14767, 14771, 14779, 14783, 14797, 14813, 14821, 14827,
			14831, 14843, 14851, 14867, 14869, 14879, 14887, 14891, 14897, 14923, 14929, 14939, 14947, 14951, 14957,
			14969, 14983, 15013, 15017, 15031, 15053, 15061, 15073, 15077, 15083, 15091, 15101, 15107, 15121, 15131,
			15137, 15139, 15149, 15161, 15173, 15187, 15193, 15199, 15217, 15227, 15233, 15241, 15259, 15263, 15269,
			15271, 15277, 15287, 15289, 15299, 15307, 15313, 15319, 15329, 15331, 15349, 15359, 15361, 15373, 15377,
			15383, 15391, 15401, 15413, 15427, 15439, 15443, 15451, 15461, 15467, 15473, 15493, 15497, 15511, 15527,
			15541, 15551, 15559, 15569, 15581, 15583, 15601, 15607, 15619, 15629, 15641, 15643, 15647, 15649, 15661,
			15667, 15671, 15679, 15683, 15727, 15731, 15733, 15737, 15739, 15749, 15761, 15767, 15773, 15787, 15791,
			15797, 15803, 15809, 15817, 15823, 15859, 15877, 15881, 15887, 15889, 15901, 15907, 15913, 15919, 15923,
			15937, 15959, 15971, 15973, 15991, 16001, 16007, 16033, 16057, 16061, 16063, 16067, 16069, 16073, 16087,
			16091, 16097, 16103, 16111, 16127, 16139, 16141, 16183, 16187, 16189, 16193, 16217, 16223, 16229, 16231,
			16249, 16253, 16267, 16273, 16301, 16319, 16333, 16339, 16349, 16361, 16363, 16369, 16381, 16411, 16417,
			16421, 16427, 16433, 16447, 16451, 16453, 16477, 16481, 16487, 16493, 16519, 16529, 16547, 16553, 16561,
			16567, 16573, 16603, 16607, 16619, 16631, 16633, 16649, 16651, 16657, 16661, 16673, 16691, 16693, 16699,
			16703, 16729, 16741, 16747, 16759, 16763, 16787, 16811, 16823, 16829, 16831, 16843, 16871, 16879, 16883,
			16889, 16901, 16903, 16921, 16927, 16931, 16937, 16943, 16963, 16979, 16981, 16987, 16993, 17011, 17021,
			17027, 17029, 17033, 17041, 17047, 17053, 17077, 17093, 17099, 17107, 17117, 17123, 17137, 17159, 17167,
			17183, 17189, 17191, 17203, 17207, 17209, 17231, 17239, 17257, 17291, 17293, 17299, 17317, 17321, 17327,
			17333, 17341, 17351, 17359, 17377, 17383, 17387, 17389, 17393, 17401, 17417, 17419, 17431, 17443, 17449,
			17467, 17471, 17477, 17483, 17489, 17491, 17497, 17509, 17519, 17539, 17551, 17569, 17573, 17579, 17581,
			17597, 17599, 17609, 17623, 17627, 17657, 17659, 17669, 17681, 17683, 17707, 17713, 17729, 17737, 17747,
			17749, 17761, 17783, 17789, 17791, 17807, 17827, 17837, 17839, 17851, 17863, 17881, 17891, 17903, 17909,
			17911, 17921, 17923, 17929, 17939, 17957, 17959, 17971, 17977, 17981, 17987, 17989, 18013, 18041, 18043,
			18047, 18049, 18059, 18061, 18077, 18089, 18097, 18119, 18121, 18127, 18131, 18133, 18143, 18149, 18169,
			18181, 18191, 18199, 18211, 18217, 18223, 18229, 18233, 18251, 18253, 18257, 18269, 18287, 18289, 18301,
			18307, 18311, 18313, 18329, 18341, 18353, 18367, 18371, 18379, 18397, 18401, 18413, 18427, 18433, 18439,
			18443, 18451, 18457, 18461, 18481, 18493, 18503, 18517, 18521, 18523, 18539, 18541, 18553, 18583, 18587,
			18593, 18617, 18637, 18661, 18671, 18679, 18691, 18701, 18713, 18719, 18731, 18743, 18749, 18757, 18773,
			18787, 18793, 18797, 18803, 18839, 18859, 18869, 18899, 18911, 18913, 18917, 18919, 18947, 18959, 18973,
			18979, 19001, 19009, 19013, 19031, 19037, 19051, 19069, 19073, 19079, 19081, 19087, 19121, 19139, 19141,
			19157, 19163, 19181, 19183, 19207, 19211, 19213, 19219, 19231, 19237, 19249, 19259, 19267, 19273, 19289,
			19301, 19309, 19319, 19333, 19373, 19379, 19381, 19387, 19391, 19403, 19417, 19421, 19423, 19427, 19429,
			19433, 19441, 19447, 19457, 19463, 19469, 19471, 19477, 19483, 19489, 19501, 19507, 19531, 19541, 19543,
			19553, 19559, 19571, 19577, 19583, 19597, 19603, 19609, 19661, 19681, 19687, 19697, 19699, 19709, 19717,
			19727, 19739, 19751, 19753, 19759, 19763, 19777, 19793, 19801, 19813, 19819, 19841, 19843, 19853, 19861,
			19867, 19889, 19891, 19913, 19919, 19927, 19937, 19949, 19961, 19963, 19973, 19979, 19991, 19993, 19997,
			20011, 20021, 20023, 20029, 20047, 20051, 20063, 20071, 20089, 20101, 20107, 20113, 20117, 20123, 20129,
			20143, 20147, 20149, 20161, 20173, 20177, 20183, 20201, 20219, 20231, 20233, 20249, 20261, 20269, 20287,
			20297, 20323, 20327, 20333, 20341, 20347, 20353, 20357, 20359, 20369, 20389, 20393, 20399, 20407, 20411,
			20431, 20441, 20443, 20477, 20479, 20483, 20507, 20509, 20521, 20533, 20543, 20549, 20551, 20563, 20593,
			20599, 20611, 20627, 20639, 20641, 20663, 20681, 20693, 20707, 20717, 20719, 20731, 20743, 20747, 20749,
			20753, 20759, 20771, 20773, 20789, 20807, 20809, 20849, 20857, 20873, 20879, 20887, 20897, 20899, 20903,
			20921, 20929, 20939, 20947, 20959, 20963, 20981, 20983, 21001, 21011, 21013, 21017, 21019, 21023, 21031,
			21059, 21061, 21067, 21089, 21101, 21107, 21121, 21139, 21143, 21149, 21157, 21163, 21169, 21179, 21187,
			21191, 21193, 21211, 21221, 21227, 21247, 21269, 21277, 21283, 21313, 21317, 21319, 21323, 21341, 21347,
			21377, 21379, 21383, 21391, 21397, 21401, 21407, 21419, 21433, 21467, 21481, 21487, 21491, 21493, 21499,
			21503, 21517, 21521, 21523, 21529, 21557, 21559, 21563, 21569, 21577, 21587, 21589, 21599, 21601, 21611,
			21613, 21617, 21647, 21649, 21661, 21673, 21683, 21701, 21713, 21727, 21737, 21739, 21751, 21757, 21767,
			21773, 21787, 21799, 21803, 21817, 21821, 21839, 21841, 21851, 21859, 21863, 21871, 21881, 21893, 21911,
			21929, 21937, 21943, 21961, 21977, 21991, 21997, 22003, 22013, 22027, 22031, 22037, 22039, 22051, 22063,
			22067, 22073, 22079, 22091, 22093, 22109, 22111, 22123, 22129, 22133, 22147, 22153, 22157, 22159, 22171,
			22189, 22193, 22229, 22247, 22259, 22271, 22273, 22277, 22279, 22283, 22291, 22303, 22307, 22343, 22349,
			22367, 22369, 22381, 22391, 22397, 22409, 22433, 22441, 22447, 22453, 22469, 22481, 22483, 22501, 22511,
			22531, 22541, 22543, 22549, 22567, 22571, 22573, 22613, 22619, 22621, 22637, 22639, 22643, 22651, 22669,
			22679, 22691, 22697, 22699, 22709, 22717, 22721, 22727, 22739, 22741, 22751, 22769, 22777, 22783, 22787,
			22807, 22811, 22817, 22853, 22859, 22861, 22871, 22877, 22901, 22907, 22921, 22937, 22943, 22961, 22963,
			22973, 22993, 23003, 23011, 23017, 23021, 23027, 23029, 23039, 23041, 23053, 23057, 23059, 23063, 23071,
			23081, 23087, 23099, 23117, 23131, 23143, 23159, 23167, 23173, 23189, 23197, 23201, 23203, 23209, 23227,
			23251, 23269, 23279, 23291, 23293, 23297, 23311, 23321, 23327, 23333, 23339, 23357, 23369, 23371, 23399,
			23417, 23431, 23447, 23459, 23473, 23497, 23509, 23531, 23537, 23539, 23549, 23557, 23561, 23563, 23567,
			23581, 23593, 23599, 23603, 23609, 23623, 23627, 23629, 23633, 23663, 23669, 23671, 23677, 23687, 23689,
			23719, 23741, 23743, 23747, 23753, 23761, 23767, 23773, 23789, 23801, 23813, 23819, 23827, 23831, 23833,
			23857, 23869, 23873, 23879, 23887, 23893, 23899, 23909, 23911, 23917, 23929, 23957, 23971, 23977, 23981,
			23993, 24001, 24007, 24019, 24023, 24029, 24043, 24049, 24061, 24071, 24077, 24083, 24091, 24097, 24103,
			24107, 24109, 24113, 24121, 24133, 24137, 24151, 24169, 24179, 24181, 24197, 24203, 24223, 24229, 24239,
			24247, 24251, 24281, 24317, 24329, 24337, 24359, 24371, 24373, 24379, 24391, 24407, 24413, 24419, 24421,
			24439, 24443, 24469, 24473, 24481, 24499, 24509, 24517, 24527, 24533, 24547, 24551, 24571, 24593, 24611,
			24623, 24631, 24659, 24671, 24677, 24683, 24691, 24697, 24709, 24733, 24749, 24763, 24767, 24781, 24793,
			24799, 24809, 24821, 24841, 24847, 24851, 24859, 24877, 24889, 24907, 24917, 24919, 24923, 24943, 24953,
			24967, 24971, 24977, 24979, 24989, 25013, 25031, 25033, 25037, 25057, 25073, 25087, 25097, 25111, 25117,
			25121, 25127, 25147, 25153, 25163, 25169, 25171, 25183, 25189, 25219, 25229, 25237, 25243, 25247, 25253,
			25261, 25301, 25303, 25307, 25309, 25321, 25339, 25343, 25349, 25357, 25367, 25373, 25391, 25409, 25411,
			25423, 25439, 25447, 25453, 25457, 25463, 25469, 25471, 25523, 25537, 25541, 25561, 25577, 25579, 25583,
			25589, 25601, 25603, 25609, 25621, 25633, 25639, 25643, 25657, 25667, 25673, 25679, 25693, 25703, 25717,
			25733, 25741, 25747, 25759, 25763, 25771, 25793, 25799, 25801, 25819, 25841, 25847, 25849, 25867, 25873,
			25889, 25903, 25913, 25919, 25931, 25933, 25939, 25943, 25951, 25969, 25981, 25997, 25999, 26003, 26017,
			26021, 26029, 26041, 26053, 26083, 26099, 26107, 26111, 26113, 26119, 26141, 26153, 26161, 26171, 26177,
			26183, 26189, 26203, 26209, 26227, 26237, 26249, 26251, 26261, 26263, 26267, 26293, 26297, 26309, 26317,
			26321, 26339, 26347, 26357, 26371, 26387, 26393, 26399, 26407, 26417, 26423, 26431, 26437, 26449, 26459,
			26479, 26489, 26497, 26501, 26513, 26539, 26557, 26561, 26573, 26591, 26597, 26627, 26633, 26641, 26647,
			26669, 26681, 26683, 26687, 26693, 26699, 26701, 26711, 26713, 26717, 26723, 26729, 26731, 26737, 26759,
			26777, 26783, 26801, 26813, 26821, 26833, 26839, 26849, 26861, 26863, 26879, 26881, 26891, 26893, 26903,
			26921, 26927, 26947, 26951, 26953, 26959, 26981, 26987, 26993, 27011, 27017, 27031, 27043, 27059, 27061,
			27067, 27073, 27077, 27091, 27103, 27107, 27109, 27127, 27143, 27179, 27191, 27197, 27211, 27239, 27241,
			27253, 27259, 27271, 27277, 27281, 27283, 27299, 27329, 27337, 27361, 27367, 27397, 27407, 27409, 27427,
			27431, 27437, 27449, 27457, 27479, 27481, 27487, 27509, 27527, 27529, 27539, 27541, 27551, 27581, 27583,
			27611, 27617, 27631, 27647, 27653, 27673, 27689, 27691, 27697, 27701, 27733, 27737, 27739, 27743, 27749,
			27751, 27763, 27767, 27773, 27779, 27791, 27793, 27799, 27803, 27809, 27817, 27823, 27827, 27847, 27851,
			27883, 27893, 27901, 27917, 27919, 27941, 27943, 27947, 27953, 27961, 27967, 27983, 27997, 28001, 28019,
			28027, 28031, 28051, 28057, 28069, 28081, 28087, 28097, 28099, 28109, 28111, 28123, 28151, 28163, 28181,
			28183, 28201, 28211, 28219, 28229, 28277, 28279, 28283, 28289, 28297, 28307, 28309, 28319, 28349, 28351,
			28387, 28393, 28403, 28409, 28411, 28429, 28433, 28439, 28447, 28463, 28477, 28493, 28499, 28513, 28517,
			28537, 28541, 28547, 28549, 28559, 28571, 28573, 28579, 28591, 28597, 28603, 28607, 28619, 28621, 28627,
			28631, 28643, 28649, 28657, 28661, 28663, 28669, 28687, 28697, 28703, 28711, 28723, 28729, 28751, 28753,
			28759, 28771, 28789, 28793, 28807, 28813, 28817, 28837, 28843, 28859, 28867, 28871, 28879, 28901, 28909,
			28921, 28927, 28933, 28949, 28961, 28979, 29009, 29017, 29021, 29023, 29027, 29033, 29059, 29063, 29077,
			29101, 29123, 29129, 29131, 29137, 29147, 29153, 29167, 29173, 29179, 29191, 29201, 29207, 29209, 29221,
			29231, 29243, 29251, 29269, 29287, 29297, 29303, 29311, 29327, 29333, 29339, 29347, 29363, 29383, 29387,
			29389, 29399, 29401, 29411, 29423, 29429, 29437, 29443, 29453, 29473, 29483, 29501, 29527, 29531, 29537,
			29567, 29569, 29573, 29581, 29587, 29599, 29611, 29629, 29633, 29641, 29663, 29669, 29671, 29683, 29717,
			29723, 29741, 29753, 29759, 29761, 29789, 29803, 29819, 29833, 29837, 29851, 29863, 29867, 29873, 29879,
			29881, 29917, 29921, 29927, 29947, 29959, 29983, 29989, 30011, 30013, 30029, 30047, 30059, 30071, 30089,
			30091, 30097, 30103, 30109, 30113, 30119, 30133, 30137, 30139, 30161, 30169, 30181, 30187, 30197, 30203,
			30211, 30223, 30241, 30253, 30259, 30269, 30271, 30293, 30307, 30313, 30319, 30323, 30341, 30347, 30367,
			30389, 30391, 30403, 30427, 30431, 30449, 30467, 30469, 30491, 30493, 30497, 30509, 30517, 30529, 30539,
			30553, 30557, 30559, 30577, 30593, 30631, 30637, 30643, 30649, 30661, 30671, 30677, 30689, 30697, 30703,
			30707, 30713, 30727, 30757, 30763, 30773, 30781, 30803, 30809, 30817, 30829, 30839, 30841, 30851, 30853,
			30859, 30869, 30871, 30881, 30893, 30911, 30931, 30937, 30941, 30949, 30971, 30977, 30983, 31013, 31019,
			31033, 31039, 31051, 31063, 31069, 31079, 31081, 31091, 31121, 31123, 31139, 31147, 31151, 31153, 31159,
			31177, 31181, 31183, 31189, 31193, 31219, 31223, 31231, 31237, 31247, 31249, 31253, 31259, 31267, 31271,
			31277, 31307, 31319, 31321, 31327, 31333, 31337, 31357, 31379, 31387, 31391, 31393, 31397, 31469, 31477,
			31481, 31489, 31511, 31513, 31517, 31531, 31541, 31543, 31547, 31567, 31573, 31583, 31601, 31607, 31627,
			31643, 31649, 31657, 31663, 31667, 31687, 31699, 31721, 31723, 31727, 31729, 31741, 31751, 31769, 31771,
			31793, 31799, 31817, 31847, 31849, 31859, 31873, 31883, 31891, 31907, 31957, 31963, 31973, 31981, 31991,
			32003, 32009, 32027, 32029, 32051, 32057, 32059, 32063, 32069, 32077, 32083, 32089, 32099, 32117, 32119,
			32141, 32143, 32159, 32173, 32183, 32189, 32191, 32203, 32213, 32233, 32237, 32251, 32257, 32261, 32297,
			32299, 32303, 32309, 32321, 32323, 32327, 32341, 32353, 32359, 32363, 32369, 32371, 32377, 32381, 32401,
			32411, 32413, 32423, 32429, 32441, 32443, 32467, 32479, 32491, 32497, 32503, 32507, 32531, 32533, 32537,
			32561, 32563, 32569, 32573, 32579, 32587, 32603, 32609, 32611, 32621, 32633, 32647, 32653, 32687, 32693,
			32707, 32713, 32717, 32719, 32749 };

	/**
	 * All {@code BigInteger} prime numbers with bit length lesser than 8 bits.
	 */
	private static final BigInteger BIprimes[] = new BigInteger[primes.length];

	/**
	 * It encodes how many iterations of Miller-Rabin test are need to get an error bound not greater than
	 * {@code 2<sup>(-100)</sup>}. For example: for a {@code 1000}-bit number we need {@code 4} iterations, since
	 * {@code BITS[3]
	 * < 1000 <= BITS[4]}.
	 */
	private static final int[] BITS = { 0, 0, 1854, 1233, 927, 747, 627, 543, 480, 431, 393, 361, 335, 314, 295, 279,
			265, 253, 242, 232, 223, 216, 181, 169, 158, 150, 145, 140, 136, 132, 127, 123, 119, 114, 110, 105, 101, 96,
			92, 87, 83, 78, 73, 69, 64, 59, 54, 49, 44, 38, 32, 26, 1 };

	/**
	 * It encodes how many i-bit primes there are in the table for {@code i=2,...,10}. For example
	 * {@code offsetPrimes[6]} says that from index {@code 11} exists {@code 7} consecutive {@code 6}-bit prime numbers
	 * in the array.
	 */
	private static final int[][] offsetPrimes = { null, null, { 0, 2 }, { 2, 2 }, { 4, 2 }, { 6, 5 }, { 11, 7 },
			{ 18, 13 }, { 31, 23 }, { 54, 43 }, { 97, 75 } };

	static {// To initialize the dual table of BigInteger primes
		for (int i = 0; i < primes.length; i++) {
			BIprimes[i] = BigInteger.valueOf(primes[i]);
		}
	}

	/**
	 * Factor the given value into primes less equal than 1021.
	 * 
	 * @param val
	 *            a BigInteger value which should be factored by all primes less equal than 1021
	 * @param map
	 *            a map which counts the prime integer factors less equal than 1021
	 * @return the rest factor or zero, if the number could be factored completely into primes less equal then 1021
	 */
	public static BigInteger countPrimes1021(final BigInteger val, Map<Integer, Integer> map) {
		BigInteger[] divRem;
		BigInteger result = val;
		int count;
		for (int i = 0; i < primes.length; i++) {
			if (result.compareTo(BIprimes[i]) < 0) {
				break;
			}
			divRem = result.divideAndRemainder(BIprimes[i]);
			if (divRem[1].equals(BigInteger.ZERO)) {
				count = 0;
				Integer iCount = map.get(primes[i]);
				if (iCount != null) {
					count = iCount;
				}
				do {
					count++;
					result = divRem[0];// quotient
					if (result.compareTo(BIprimes[i]) < 0) {
						break;
					}
					divRem = result.divideAndRemainder(BIprimes[i]);
				} while (divRem[1].equals(BigInteger.ZERO));
				map.put(primes[i], count);
			}
		}
		return result;
	}

	/**
	 * Factor the given base into primes less equal than 1021.
	 * 
	 * @param base
	 *            a BigInteger value which should be factored by all primes less equal than 1021
	 * @param exponent
	 *            the exponent which should be used for the collected primes
	 * @param map
	 *            a map which collects the prime integer factors less equal than 1021 in the form
	 *            <code>prime-factor -> prime-count * exponent</code>
	 * @param evaled
	 *            if <code>evaled[0]</code> is <code>true</code> a transformations was done
	 * @return the rest factor or one, if the number could be factored completely into primes less equal then 1021
	 */
	public static IInteger countPrimes1021(IInteger base, IRational exponent, OpenIntToIExprHashMap<IRational> map,
			boolean setEvaled, boolean[] evaled) {
		if (base.isOne() || base.isMinusOne()) {
			return base;
		}
		if (base instanceof IntegerSym) {
			return countPrimes1021(base.intValue(), exponent, map, setEvaled, evaled);
		}
		return countPrimes1021(base.toBigNumerator(), exponent, map, evaled);
	}

	/**
	 * Factor the given base into primes less equal than 1021.
	 * 
	 * @param base
	 *            a BigInteger value which should be factored by all primes less equal than 1021
	 * @param exponent
	 *            the exponent which should be used for the collected primes
	 * @param map
	 *            a map which collects the prime integer factors less equal than 1021 in the form
	 *            <code>prime-factor -> prime-count * exponent</code>
	 * @param evaled
	 *            if <code>evaled[0]</code> is <code>true</code> a transformations was done
	 * @return the rest factor or one, if the number could be factored completely into primes less equal then 1021
	 */
	private static IInteger countPrimes1021(final BigInteger base, IRational exponent,
			OpenIntToIExprHashMap<IRational> map, boolean[] evaled) {
		BigInteger[] divRem;
		BigInteger result = base;
		int count;
		if (base.compareTo(BigInteger.ZERO) < 0) {
			// base < 0
			IRational exp = map.get(-1);
			if (exp == null) {
				map.put(-1, exponent);
			} else {
				evaled[0] = true;
				map.put(-1, exp.add(exponent));
			}
			result = base.negate();
		}
		for (int i = 0; i < primes.length; i++) {
			if (result.compareTo(BIprimes[i]) < 0) {
				break;
			}
			divRem = result.divideAndRemainder(BIprimes[i]);
			if (divRem[1].equals(BigInteger.ZERO)) {
				count = 0;
				do {
					count++;
					result = divRem[0];// quotient
					if (result.compareTo(BIprimes[i]) < 0) {
						break;
					}
					divRem = result.divideAndRemainder(BIprimes[i]);
				} while (divRem[1].equals(BigInteger.ZERO));
				IRational exp = map.get(primes[i]);
				if (exp == null) {
					map.put(primes[i], F.ZZ(count).multiply(exponent));
				} else {
					evaled[0] = true;
					map.put(primes[i], exp.add(F.ZZ(count).multiply(exponent)));
				}

			}
		}
		return AbstractIntegerSym.valueOf(result);
	}

	/**
	 * Factor the given base into primes less equal than 1021.
	 * 
	 * @param base
	 *            an int value <code>!= 0</code> which should be factored by all primes less equal than 1021
	 * @param exponent
	 *            the exponent which should be used for the collected primes
	 * @param map
	 *            a map which collects the prime integer factors less equal than 1021 in the form
	 *            <code>prime-factor -> prime-count * exponent</code>
	 * @param evaled
	 *            if <code>evaled[0]</code> is <code>true</code> a transformations was done
	 * @return the rest factor or one, if the number could be factored completely into primes less equal then 1021
	 */
	private static IInteger countPrimes1021(final int base, IRational exponent, OpenIntToIExprHashMap<IRational> map,
			boolean setEvaled, boolean[] evaled) {
		int result = base;
		int count = 0;
		if (base < 0) {
			IRational exp = map.get(-1);
			if (exp == null) {
				map.put(-1, exponent);
			} else {
				evaled[0] = true;
				map.put(-1, exp.add(exponent));
			}
			result = -base;
		}
		// special case primes[0]==2
		while ((result & 0x00000001) == 0x00000000) {
			// even integer
			result >>= 1;
			count++;
		}
		if (count > 0) {
			IRational exp = map.get(2);
			if (exp == null) {
				if (setEvaled && count > 1) {
					evaled[0] = true;
				}
				map.put(2, F.ZZ(count).multiply(exponent));
			} else {
				evaled[0] = true;
				map.put(2, exp.add(F.ZZ(count).multiply(exponent)));
			}
		}

		for (int i = 1; i < primes.length; i++) {
			if (result < primes[i]) {
				break;
			}
			int div = result / primes[i];
			int rem = result % primes[i];
			if (rem == 0) {
				count = 0;
				do {
					count++;
					result = div;// quotient
					if (result < primes[i]) {
						break;
					}
					div = result / primes[i];
					rem = result % primes[i];
				} while (rem == 0);
				IRational exp = map.get(primes[i]);
				if (exp == null) {
					if (setEvaled && count > 1) {
						evaled[0] = true;
					}
					map.put(primes[i], F.ZZ(count).multiply(exponent));
				} else {
					evaled[0] = true;
					map.put(primes[i], exp.add(F.ZZ(count).multiply(exponent)));
				}

			}
		}
		return AbstractIntegerSym.valueOf(result);
	}

	/**
	 * Factor the given value into primes less than <code>Short.MAX_VALUE</code> .
	 * 
	 * @param val
	 *            a BigInteger value which should be factored by all primes less equal than 1021
	 * @param map
	 *            a map which counts the prime integer factors less equal than 32749
	 * @return the rest factor or zero, if the number could be factored completely into primes less equal then 1021
	 */
	public static BigInteger countPrimes32749(final BigInteger val, Map<Integer, Integer> map) {
		BigInteger[] divRem;
		BigInteger result = val;
		BigInteger sqrt = BigIntegerMath.sqrt(result, RoundingMode.DOWN);
		int count = 0;
		for (int i = 0; i < primes.length; i++) {
			if (sqrt.compareTo(BIprimes[i]) < 0) {
				break;
			}
			divRem = result.divideAndRemainder(BIprimes[i]);
			if (divRem[1].equals(BigInteger.ZERO)) {
				count = 0;
				Integer iCount = map.get(primes[i]);
				if (iCount != null) {
					count = iCount;
				}
				do {
					count++;
					result = divRem[0];// quotient
					sqrt = BigIntegerMath.sqrt(result, RoundingMode.DOWN);
					if (sqrt.compareTo(BIprimes[i]) < 0) {
						break;
					}
					divRem = result.divideAndRemainder(BIprimes[i]);
				} while (divRem[1].equals(BigInteger.ZERO));
				map.put(primes[i], count);
			}
		}
		BigInteger b;
		int prime;
		sqrt = BigIntegerMath.sqrt(result, RoundingMode.DOWN);
		for (int i = 0; i < SHORT_PRIMES.length; i++) {
			prime = SHORT_PRIMES[i];
			b = BigInteger.valueOf(prime);
			if (sqrt.compareTo(b) < 0) {
				break;
			}
			divRem = result.divideAndRemainder(b);
			if (divRem[1].equals(BigInteger.ZERO)) {
				count = 0;
				Integer iCount = map.get(prime);
				if (iCount != null) {
					count = iCount;
				}
				do {
					count++;
					result = divRem[0];// quotient
					sqrt = BigIntegerMath.sqrt(result, RoundingMode.DOWN);
					if (sqrt.compareTo(b) < 0) {
						break;
					}
					divRem = result.divideAndRemainder(b);
				} while (divRem[1].equals(BigInteger.ZERO));
				map.put(prime, count);
			}
		}
		return result;
	}

	/**
	 * Get the highest exponent of base (greater than 1) that divides val.
	 * 
	 * @param val
	 *            a BigInteger value
	 * @param base
	 *            a base greater than 1
	 * @return the exponent, which is the highest exponent of base that divides val.
	 */
	public static BigInteger countExponent(final BigInteger val, final BigInteger base) {
		BigInteger[] divRem;
		BigInteger result = val;
		BigInteger count = BigInteger.ZERO;
		divRem = result.divideAndRemainder(base);
		while (divRem[1].equals(BigInteger.ZERO)) {
			count = count.add(BigInteger.ONE);
			result = divRem[0];// quotient
			if (result.equals(BigInteger.ZERO)) {
				break;
			}
			divRem = result.divideAndRemainder(base);
		}
		return count;
	}

	/**
	 * See <a href="https://en.wikipedia.org/wiki/Pollard%27s_rho_algorithm"> Wikipedia: Pollards rho algorithm</a>
	 * 
	 * @param val
	 * @param map
	 */
	public static void pollardRhoFactors(final BigInteger val, Map<BigInteger, Integer> map) {
		BigInteger factor;
		BigInteger temp = val;
		int iterationCounter = 0;
		Integer count;
		while (!temp.isProbablePrime(32)) {
			factor = rho(temp);
			if (factor.equals(temp)) {
				if (iterationCounter++ > 4) {
					break;
				}
			} else {
				iterationCounter = 1;
			}
			count = map.get(factor);
			if (count == null) {
				map.put(factor, 1);
			} else {
				map.put(factor, count + 1);
			}
			temp = temp.divide(factor);
		}
		count = map.get(temp);
		if (count == null) {
			map.put(temp, 1);
		} else {
			map.put(temp, count + 1);
		}
	}

	public static List<BigInteger> factorize(final BigInteger val, List<BigInteger> result) {
		SortedMap<BigInteger, Integer> bigMap = new TreeMap<BigInteger, Integer>();
		Primality.factorInteger(val, bigMap);
		for (Map.Entry<BigInteger, Integer> entry : bigMap.entrySet()) {
			BigInteger key = entry.getKey();
			for (int i = 0; i < entry.getValue(); i++) {
				result.add(key);
			}
		}
		return result;
	}

	public static void factorInteger(final BigInteger val, Map<BigInteger, Integer> map) {
		EllipticCurveMethod ecm = new EllipticCurveMethod(val);
		ecm.factorize(map);
	}

	public static BigInteger rho(final BigInteger val) {
		BigInteger divisor;
		BigInteger c = new BigInteger(val.bitLength(), random);
		BigInteger x = new BigInteger(val.bitLength(), random);
		BigInteger xx = x;

		do {
			x = x.multiply(x).mod(val).add(c).mod(val);
			xx = xx.multiply(xx).mod(val).add(c).mod(val);
			xx = xx.multiply(xx).mod(val).add(c).mod(val);
			divisor = x.subtract(xx).gcd(val);
		} while (divisor.equals(BigInteger.ONE));

		return divisor;
	}

	/**
	 * Determine the n-th root from the prime decomposition of the primes[] array.
	 * 
	 * @param val
	 *            a BigInteger value which should be factored by all primes less equal than 1021
	 * @param root
	 *            th n-th root which should be determined
	 * @return <code>(result[0] ^ root ) * result[1]</code>
	 */
	public static BigInteger[] countRoot1021(final BigInteger val, int root) {
		BigInteger[] result = new BigInteger[2];
		result[1] = val;
		result[0] = BigInteger.ONE;
		BigInteger[] divRem;
		BigInteger sqrt = BigIntegerMath.sqrt(val, RoundingMode.DOWN);
		int count = 0;
		BigInteger temp = val;
		// handle even values
		while (temp.and(BigInteger.ONE).equals(BigInteger.ZERO) && !temp.equals(BigInteger.ZERO)) {
			temp = temp.shiftRight(1);
			count++;
			if (count == root) {
				count = 0;
				result[1] = result[1].shiftRight(root);
				result[0] = result[0].shiftLeft(1);
			}
		}

		for (int i = 1; i < primes.length; i++) {
			if (sqrt.compareTo(BIprimes[i]) < 0) {
				break;
			}
			count = 0;
			divRem = temp.divideAndRemainder(BIprimes[i]);
			while (divRem[1].equals(BigInteger.ZERO)) {
				count++;
				if (count == root) {
					count = 0;
					result[1] = result[1].divide(BIprimes[i].pow(root));
					result[0] = result[0].multiply(BIprimes[i]);
				}
				temp = divRem[0];// quotient
				if (temp.compareTo(BIprimes[i]) < 0) {
					break;
				}
				divRem = temp.divideAndRemainder(BIprimes[i]);
			}
		}
		return result;
	}

	/**
	 * Determine the n-th root from the prime decomposition of the primes[] array.
	 * 
	 * @param val
	 *            a BigInteger value which should be factored by all primes less equal than 1021
	 * @param root
	 *            the n-th root which should be determined
	 * @return <code>(result[0] ^ root ) * result[1]</code>
	 */
	public static long[] countRoot1021(final long val, int root) {
		long[] result = new long[2];
		result[1] = val;
		result[0] = 1L;

		long sqrt = LongMath.sqrt(val, RoundingMode.DOWN);
		long count = 0;
		long temp = val;
		// handle even values
		while ((temp & 0x00000001) == 0x00000000 && temp != 0L) {
			temp = temp >> 1L;
			count++;
			if (count == root) {
				count = 0;
				result[1] = result[1] >> root;
				result[0] = result[0] << 1L;
			}
		}

		for (int i = 1; i < primes.length; i++) {
			if (sqrt < primes[i]) {
				break;
			}
			count = 0;
			// divRem = temp.divideAndRemainder(BIprimes[i]);
			long[] divRem = new long[2];
			divRem[0] = temp / primes[i];
			divRem[1] = temp % primes[i];
			while (divRem[1] == 0L) {
				count++;
				if (count == root) {
					count = 0;
					// result[1] = result[1] / (primes[i].pow(root));
					result[1] = result[1] / pow(primes[i], root);
					result[0] = result[0] * primes[i];
				}
				temp = divRem[0];// quotient
				if (temp < primes[i]) {
					break;
				}
				// divRem = temp.divideAndRemainder(BIprimes[i]);
				divRem[0] = temp / primes[i];
				divRem[1] = temp % primes[i];
			}
		}
		return result;
	}

	private static long pow(long prime, long count) {
		long result = prime;
		for (int i = 1; i < count; i++) {
			result *= prime;
		}
		return result;
	}

	/**
	 * Return all divisors of this integer number.
	 * 
	 * <pre>
	 * divisors(24) ==> [1,2,3,4,6,8,12,24]
	 * </pre>
	 */
	public static List<BigInteger> divisors(BigInteger val) {
		if (val.equals(BigInteger.ONE) || val.equals(BigInteger.valueOf(-1))) {
			ArrayList<BigInteger> result = new ArrayList<BigInteger>();
			result.add(BigInteger.ONE);
			return result;
		}

		Set<BigInteger> set = new TreeSet<BigInteger>();

		final List<BigInteger> primeFactorsList = factorize(val, new ArrayList<BigInteger>());
		int len = primeFactorsList.size();

		// build the k-subsets from the primeFactorsList
		for (int k = 1; k < len; k++) {
			final KSubsetsList<BigInteger, List<BigInteger>> iter = KSubsets.createKSubsets(primeFactorsList, k, 0);
			for (List<BigInteger> subset : iter) {
				if (subset == null) {
					break;
				}
				// create the product of all integers in the k-subset
				BigInteger factor = BigInteger.ONE;
				for (int j = 0; j < subset.size(); j++) {
					factor = factor.multiply((BigInteger) subset.get(j));
				}
				// add this divisor to the set collection
				set.add(factor);
			}
		}

		// build the final divisors list from the tree set
		final ArrayList<BigInteger> resultList = new ArrayList<BigInteger>();
		resultList.add(BigInteger.ONE);
		for (BigInteger entry : set) {
			resultList.add(entry);
		}
		resultList.add(val);
		return resultList;
	}

	/**
	 * Least common multiple
	 * 
	 * @param arg1
	 * @param arg2
	 * @return
	 */
	public static BigInteger lcm(BigInteger arg1, BigInteger arg2) {
		if (arg1.equals(BigInteger.ZERO) || arg2.equals(BigInteger.ZERO)) {
			return BigInteger.ZERO;
		}
		BigInteger a = arg1.abs();
		BigInteger b = arg2.abs();
		BigInteger gcd = arg1.gcd(arg2);
		return a.multiply(b).divide(gcd);
	}

	public static BigInteger charmichaelLambda(BigInteger value) {
		if (value.equals(BigInteger.ZERO)) {
			return BigInteger.ZERO;
		}
		if (value.compareTo(BigInteger.ZERO) < 0) {
			value = value.negate();
		}
		if (value.equals(BigInteger.ONE)) {
			return BigInteger.ONE;
		}

		SortedMap<BigInteger, Integer> map = new TreeMap<BigInteger, Integer>();
		factorInteger(value, map);
		BigInteger l = BigInteger.ONE;
		for (Map.Entry<BigInteger, Integer> entry : map.entrySet()) {
			BigInteger base = entry.getKey();
			int exponent = entry.getValue();
			if (exponent >= 3 && base.equals(TWO)) {
				l = lcm(l, base.pow(exponent - 2));
			} else {
				l = lcm(l, (base.pow(exponent - 1)).multiply(base.subtract(BigInteger.ONE)));
			}
		}
		return l;
	}

	/**
	 * Euler phi function.
	 * 
	 * See: <a href="http://en.wikipedia.org/wiki/Euler%27s_totient_function">Euler's totient function</a>
	 * 
	 * @return Euler's totient function
	 * @throws ArithmeticException
	 */
	public static BigInteger eulerPhi(BigInteger value) {
		if (value.equals(BigInteger.ZERO)) {
			return BigInteger.ZERO;
		}
		if (value.compareTo(BigInteger.ZERO) < 0) {
			value = value.negate();
		}
		if (value.equals(BigInteger.ONE)) {
			return BigInteger.ONE;
		}
		SortedMap<BigInteger, Integer> map = new TreeMap<BigInteger, Integer>();
		factorInteger(value, map);
		BigInteger phi = BigInteger.ONE;
		for (Map.Entry<BigInteger, Integer> entry : map.entrySet()) {
			BigInteger q = entry.getKey();
			int c = entry.getValue();
			if (c == 1) {
				phi = phi.multiply(q.subtract(BigInteger.ONE));
			} else {
				phi = phi.multiply(q.subtract(BigInteger.ONE).multiply(q.pow(c - 1)));
			}
		}
		return phi;
	}

	public static int moebiusMu(BigInteger value) {
		if (value.compareTo(BigInteger.ZERO) < 0) {
			value = value.negate();
		}
		if (value.equals(BigInteger.ZERO)) {
			return 0;
		}
		if (value.equals(BigInteger.ONE)) {
			return 1;
		}
		SortedMap<BigInteger, Integer> map = new SquareFreeTreedMap();
		try {
			factorInteger(value, map);
			// value is square-free
			Integer max = 1;
			for (Map.Entry<BigInteger, Integer> entry : map.entrySet()) {
				Integer c = entry.getValue();
				if (c.compareTo(max) > 0) {
					return 0;
				}
			}
			if ((map.size() & 0x00000001) == 0x00000001) {
				// odd number
				return -1;
			}
			return 1;
		} catch (ReturnException re) {
			// not square-free
		}
		return 0;
	}

	/**
	 * See <a href="https://en.wikipedia.org/wiki/Multiplicative_order">Wikipedia: Multiplicative order</a> and
	 * <a href="https://rosettacode.org/wiki/Multiplicative_order">Rosettacode. org: Multiplicative order</a>.
	 *
	 * @return <code>null</code> if GCD(k,N) != 1 or is negative
	 */
	public static BigInteger multiplicativeOrder(BigInteger k, BigInteger n) {
		if (n.compareTo(BigInteger.ZERO) < 0) {
			return null;
		}
		if (!k.gcd(n).equals(BigInteger.ONE)) {
			return null;
		}
		SortedMap<BigInteger, Integer> map = new TreeMap<BigInteger, Integer>();
		Primality.factorInteger(n, map);
		BigInteger res = BigInteger.ONE;
		for (Map.Entry<BigInteger, Integer> entry : map.entrySet()) {
			res = lcm(res, multiplicativeOrder(k, entry.getKey(), entry.getValue()));
		}
		return res;
	}

	private static BigInteger multiplicativeOrder(BigInteger a, BigInteger prime, int exponent) {
		BigInteger m = prime.pow(exponent);
		BigInteger t = m.divide(prime).multiply(prime.subtract(BigInteger.ONE));
		List<BigInteger> divisors = divisors(t);
		int len = divisors.size();
		for (int i = 0; i < len; i++) {
			BigInteger factor = divisors.get(i);
			if (a.modPow(factor, m).equals(BigInteger.ONE)) {
				return factor;
			}
		}
		return BigInteger.ZERO;
	}

	public static BigInteger primeOmega(BigInteger value) {
		SortedMap<BigInteger, Integer> map = new TreeMap<BigInteger, Integer>();
		factorInteger(value, map);
		BigInteger sum = BigInteger.ZERO;
		for (Map.Entry<BigInteger, Integer> entry : map.entrySet()) {
			sum = sum.add(BigInteger.valueOf(entry.getValue()));
		}
		return sum;
	}

	/**
	 * Return <code>true</code> if <code>val</code> is a power of a prime number.
	 * 
	 * @param val
	 * @return
	 */
	public static boolean isPrimePower(BigInteger val) {
		if (val.compareTo(BigInteger.ZERO) < 0) {
			val = val.negate();
		}
		try {
			SortedMap<BigInteger, Integer> map = new PrimePowerTreedMap();
			factorInteger(val, map);
			if (map.size() == 1) {
				for (Map.Entry<BigInteger, Integer> entry : map.entrySet()) {
					if (entry.getValue() > 1) {
						return true;
					}
				}
			}
		} catch (ReturnException re) {
		}
		return false;
	}

	/**
	 * Return <code>true</code> if <code>val</code> is square free.
	 * 
	 * @param val
	 * @return
	 */
	public static boolean isSquareFree(BigInteger val) {
		if (val.compareTo(BigInteger.ZERO) < 0) {
			val = val.negate();
		}
		SortedMap<BigInteger, Integer> map = new SquareFreeTreedMap();
		try {
			factorInteger(val, map);
			return true;
		} catch (ReturnException re) {
		}
		return false;
	}

	/**
	 * <code>Prime(i)</code> gives the i-th prime number for <code>i</code> less equal 103000000.
	 * 
	 * See: <a href="https://bitbucket.org/dafis/javaprimes">https://bitbucket.org/dafis/javaprimes</a><br />
	 * <a href=
	 * "http://stackoverflow.com/questions/9625663/calculating-and-printing-the-nth-prime-number/9704912#9704912">
	 * stackoverflow. com - Calculating and printing the nth prime number</a>
	 */
	public static long prime(long n) {
		// Speed up counting by counting the primes per
		// array slot and not individually. This yields
		// another factor of about 1.24 or so.
		if (n < 2L) {
			return 2L;
		}
		if (n == 2L) {
			return 3L;
		}
		if (n == 3L) {
			return 5L;
		}
		long limit, root, count = 2;
		limit = (long) (n * (Math.log(n) + Math.log(Math.log(n)))) + 3;
		root = (long) Math.sqrt(limit);
		switch ((int) (limit % 6)) {
		case 0:
			limit = 2 * (limit / 6) - 1;
			break;
		case 5:
			limit = 2 * (limit / 6) + 1;
			break;
		default:
			limit = 2 * (limit / 6);
		}
		switch ((int) (root % 6)) {
		case 0:
			root = 2 * (root / 6) - 1;
			break;
		case 5:
			root = 2 * (root / 6) + 1;
			break;
		default:
			root = 2 * (root / 6);
		}
		int dim = (int) ((limit + 31) >> 5);
		int[] sieve = new int[dim];
		int start, s1, s2, tempi;
		for (int i = 0; i < root; ++i) {
			if ((sieve[i >> 5] & (1 << (i & 31))) == 0) {
				if ((i & 1) == 0) {
					tempi = i + i;
					start = i * (tempi + i + 10) + 7;
					s1 = tempi + 3;
					s2 = tempi + tempi + 7;
				} else {
					tempi = i + i;
					start = i * (tempi + i + 8) + 4;
					s1 = tempi + tempi + 5;
					s2 = tempi + 3;
				}
				for (long j = start; j < limit; j += s2) {
					sieve[(int) (j >> 5)] |= 1 << (j & 31);
					j += s1;
					if (j >= limit)
						break;
					sieve[(int) (j >> 5)] |= 1 << (j & 31);
				}
			}
		}
		int i;
		for (i = 0; count < n; ++i) {
			count += popCount(~sieve[i]);
		}
		--i;
		int mask = ~sieve[i];
		int p;
		for (p = 31; count >= n; --p) {
			count -= (mask >> p) & 1;
		}
		return 3 * (p + (i << 5)) + 7 + (p & 1);
	}

	// Count number of set bits in an int
	private static int popCount(int n) {
		n -= (n >>> 1) & 0x55555555;
		n = ((n >>> 2) & 0x33333333) + (n & 0x33333333);
		n = ((n >> 4) & 0x0F0F0F0F) + (n & 0x0F0F0F0F);
		return (n * 0x01010101) >> 24;
	}

}
