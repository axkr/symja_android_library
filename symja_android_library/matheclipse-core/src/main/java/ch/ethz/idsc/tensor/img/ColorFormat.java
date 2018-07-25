package ch.ethz.idsc.tensor.img;

import java.awt.Color;
import java.util.stream.IntStream;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.parser.client.math.MathException;

import ch.ethz.idsc.tensor.io.ImageFormat;

/**
 * mappings between {@link IAST}, {@link Color}, and 0xAA:RR:GG:BB integer
 * 
 * <p>
 * functions are used in {@link ImageFormat}
 */
public enum ColorFormat {
	;
	/**
	 * there are only [0, 1, ..., 255] possible values for red, green, blue, and alpha. We preallocate instances of
	 * these scalars in a lookup table to save memory and possibly enhance execution time.
	 */
	private static final IExpr[] LOOKUP = new IExpr[256];
	static {
		IntStream.range(0, 256).forEach(index -> LOOKUP[index] = F.QQ(index, 1));
	}
	// ---

	/**
	 * @param color
	 * @return vector with {@link IExpr} entries as {R, G, B, A}
	 */
	public static IAST toVector(Color color) {
		return F.List( //
				LOOKUP[color.getRed()], //
				LOOKUP[color.getGreen()], //
				LOOKUP[color.getBlue()], //
				LOOKUP[color.getAlpha()]);
	}

	/**
	 * @param argb
	 *            encoding color as 0xAA:RR:GG:BB
	 * @return vector with {@link IExpr} entries as {R, G, B, A}
	 */
	public static IAST toVector(int argb) {
		return toVector(new Color(argb, true));
	}

	/**
	 * @param vector
	 *            with {@link IExpr} entries as {R, G, B, A}
	 * @return encoding color as 0xAA:RR:GG:BB
	 * @throws Exception
	 *             if either color value is outside the allowed range [0, ..., 255]
	 */
	public static Color toColor(IExpr vector) {
		if (vector.argSize() != 4) {
			throw MathException.of(vector);
		}
		IAST v=(IAST)vector;
		return new Color( //
				v.get(1).toIntDefault(Integer.MIN_VALUE), //
				v.get(2).toIntDefault(Integer.MIN_VALUE), //
				v.get(3).toIntDefault(Integer.MIN_VALUE), //
				v.get(4).toIntDefault(Integer.MIN_VALUE));
	}

	/**
	 * @param vector
	 *            with {@link IExpr} entries as {R, G, B, A}
	 * @return int in hex 0xAA:RR:GG:BB
	 */
	public static int toInt(IAST vector) {
		return toColor(vector).getRGB();
	}
}
