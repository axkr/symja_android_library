package ch.ethz.idsc.tensor.io;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.stream.IntStream;

import org.matheclipse.core.builtin.LinearAlgebra;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.img.ColorFormat;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IInteger;

/**
 * ImageFormat uses the data alignment of {@link BufferedImage}.
 * 
 * <p>
 * The {@link Dimensions} of tensors that represent native images are For grayscale: <code>height x width</code>
 * 
 * For color (not supported yet): <code>height x width x 4</code> The 4 entries in the last dimension are RGBA.
 * 
 * <p>
 * <code>tensor.get(y, x)</code> refers to the same pixel as <code>BufferedImage::getRGB(x, y)</code>
 * 
 */
public enum ImageFormat {
	;
	/**
	 * there are only [0, 1, ..., 255] possible values for red, green, blue, and alpha. We preallocate instances of
	 * these scalars in a lookup table to save memory and possibly enhance execution time.
	 */
	private static final IExpr[] LOOKUP = new IExpr[256];
	static {
		IntStream.range(0, 256).forEach(index -> LOOKUP[index] = F.fraction(index, 1));
	}

	/**
	 * encode image as AST. {@link Dimensions} of output are [height x width] for grayscale images of type
	 * BufferedImage.TYPE_BYTE_GRAY [height x width x 4] for color images
	 * 
	 * @param bufferedImage
	 * @return AST encoding the color values of given bufferedImage
	 */
	public static IAST from(BufferedImage bufferedImage) {
		switch (bufferedImage.getType()) {
		case BufferedImage.TYPE_BYTE_GRAY:
			return fromGrayscale(bufferedImage);
		default:
			return F.matrix((y, x) -> ColorFormat.toVector(bufferedImage.getRGB(x, y)), //
					bufferedImage.getHeight(), bufferedImage.getWidth());
		}
	}

	/**
	 * @param ast
	 * @return image of type BufferedImage.TYPE_BYTE_GRAY or BufferedImage.TYPE_INT_ARGB
	 */
	public static BufferedImage of(IAST ast) {
		List<Integer> dims = LinearAlgebra.dimensions(ast);
		if (dims.size() == 2)
			return toTYPE_BYTE_GRAY(ast, dims.get(1), dims.get(0));
		return toTYPE_INT(ast, dims.get(1), dims.get(0), BufferedImage.TYPE_INT_ARGB);
	}

	/**
	 * @param bufferedImage
	 *            grayscale image with dimensions [width x height]
	 * @return tensor with dimensions [height x width]
	 */
	private static IAST fromGrayscale(BufferedImage bufferedImage) {
		WritableRaster writableRaster = bufferedImage.getRaster();
		DataBufferByte dataBufferByte = (DataBufferByte) writableRaster.getDataBuffer();
		ByteBuffer byteBuffer = ByteBuffer.wrap(dataBufferByte.getData());
		return F.matrix((i, j) -> LOOKUP[byteBuffer.get() & 0xff], //
				bufferedImage.getHeight(), bufferedImage.getWidth());
	}

	// helper function
	static BufferedImage toTYPE_BYTE_GRAY(IAST tensor, int width, int height) {
		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		WritableRaster writableRaster = bufferedImage.getRaster();
		DataBufferByte dataBufferByte = (DataBufferByte) writableRaster.getDataBuffer();
		byte[] bytes = dataBufferByte.getData();
		ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
		tensor.forEach(row -> {
			((IAST) row).forEach(number -> {
				byteBuffer.put(((IInteger) number).byteValue());
			});
		});
		return bufferedImage;
	}

	// fast extraction of color information to buffered image
	private static BufferedImage toTYPE_INT(IAST ast, int width, int height, int imageType) {
		BufferedImage bufferedImage = new BufferedImage(width, height, imageType);
		int[] array = new int[width * height];
		int[] i = new int[1];
		ast.forEach(row -> {
			((IAST) row).forEach(number -> {
				array[i[0]++] = ((IInteger) number).intValue();
			});
		});
		bufferedImage.setRGB(0, 0, width, height, array, 0, width);
		return bufferedImage;
	}

	/**
	 * Functionality for export to jpg image format
	 * 
	 * @param ast
	 * @return image of type BufferedImage.TYPE_BYTE_GRAY or BufferedImage.TYPE_INT_BGR
	 */
	public static BufferedImage jpg(IAST ast) {
		List<Integer> dims = LinearAlgebra.dimensions(ast);
		if (dims.size() == 2) {
			return toTYPE_BYTE_GRAY(ast, dims.get(1), dims.get(0));
		}
		return toTYPE_INT(ast, dims.get(1), dims.get(0), BufferedImage.TYPE_INT_BGR);
	}
}
