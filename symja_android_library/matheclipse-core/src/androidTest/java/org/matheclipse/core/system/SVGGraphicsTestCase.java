package org.matheclipse.core.system;

import org.matheclipse.core.system.AbstractTestCase;

/**
 * View with: http://www.rapidtables.com/web/tools/svg-viewer-editor.htm
 * 
 */
public class SVGGraphicsTestCase extends AbstractTestCase {
	public SVGGraphicsTestCase(String name) {
		super(name);
	}

	public void testPoint001() {

		checkSVGGraphics("Show(Graphics(Point({0, 0})))",
				"<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" viewBox=\"-1.666667 -1.666667 353.333333 353.333333\" width=\"350px\" height=\"350px\">\n"
						+ "<circle cx=\"175.0\" cy=\"175.0\" r=\"1.0\" \n"
						+ "      style=\"stroke: none; stroke-width: 0.000000px; fill: rgb(0.0%, 0.0%, 0.0%); fill-opacity: 1\" />\n"
						+ "\n" + "</svg>");

	}

	public void testPoint002() {

		checkSVGGraphics("Show(Graphics(Point(Table({t, Sin(t)}, {t, 0, 2 Pi, 2 Pi/10}))))",
				"<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" viewBox=\"-1.666667 -1.666667 353.333333 353.333333\" width=\"350px\" height=\"350px\">\n" + 
				"<circle cx=\"0.0\" cy=\"175.0\" r=\"1.0\" \n" + 
				"      style=\"stroke: none; stroke-width: 0.000000px; fill: rgb(0.0%, 0.0%, 0.0%); fill-opacity: 1\" />\n" + 
				"<circle cx=\"35.0\" cy=\"66.84405\" r=\"1.0\" \n" + 
				"      style=\"stroke: none; stroke-width: 0.000000px; fill: rgb(0.0%, 0.0%, 0.0%); fill-opacity: 1\" />\n" + 
				"<circle cx=\"70.0\" cy=\"0.0\" r=\"1.0\" \n" + 
				"      style=\"stroke: none; stroke-width: 0.000000px; fill: rgb(0.0%, 0.0%, 0.0%); fill-opacity: 1\" />\n" + 
				"<circle cx=\"105.0\" cy=\"0.0\" r=\"1.0\" \n" + 
				"      style=\"stroke: none; stroke-width: 0.000000px; fill: rgb(0.0%, 0.0%, 0.0%); fill-opacity: 1\" />\n" + 
				"<circle cx=\"140.0\" cy=\"66.84405\" r=\"1.0\" \n" + 
				"      style=\"stroke: none; stroke-width: 0.000000px; fill: rgb(0.0%, 0.0%, 0.0%); fill-opacity: 1\" />\n" + 
				"<circle cx=\"175.0\" cy=\"175.0\" r=\"1.0\" \n" + 
				"      style=\"stroke: none; stroke-width: 0.000000px; fill: rgb(0.0%, 0.0%, 0.0%); fill-opacity: 1\" />\n" + 
				"<circle cx=\"210.0\" cy=\"283.15595\" r=\"1.0\" \n" + 
				"      style=\"stroke: none; stroke-width: 0.000000px; fill: rgb(0.0%, 0.0%, 0.0%); fill-opacity: 1\" />\n" + 
				"<circle cx=\"245.0\" cy=\"350.0\" r=\"1.0\" \n" + 
				"      style=\"stroke: none; stroke-width: 0.000000px; fill: rgb(0.0%, 0.0%, 0.0%); fill-opacity: 1\" />\n" + 
				"<circle cx=\"280.0\" cy=\"350.0\" r=\"1.0\" \n" + 
				"      style=\"stroke: none; stroke-width: 0.000000px; fill: rgb(0.0%, 0.0%, 0.0%); fill-opacity: 1\" />\n" + 
				"<circle cx=\"315.0\" cy=\"283.15595\" r=\"1.0\" \n" + 
				"      style=\"stroke: none; stroke-width: 0.000000px; fill: rgb(0.0%, 0.0%, 0.0%); fill-opacity: 1\" />\n" + 
				"<circle cx=\"350.0\" cy=\"175.0\" r=\"1.0\" \n" + 
				"      style=\"stroke: none; stroke-width: 0.000000px; fill: rgb(0.0%, 0.0%, 0.0%); fill-opacity: 1\" />\n" + 
				"\n" + 
				"</svg>");

	}

	public void testLine001() {

		checkSVGGraphics("Show(Graphics(Line({{0,1},{0,0},{1,0},{1,1}})))",
				"<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" viewBox=\"-1.666667 -1.666667 353.333333 353.333333\" width=\"350px\" height=\"350px\">\n"
						+ "<polyline points=\"0.0,0.0 0.0,350.0 350.0,350.0 350.0,0.0\" \n"
						+ "          style=\"stroke: rgb(0.000000%, 0.000000%, 0.000000%); stroke-opacity: 1; stroke-width: 0.666667px; fill: none\" />\n"
						+ "</svg>");

	}

	public void testLine002() {

		checkSVGGraphics("Show(Graphics(Line({{1, 0}, {2, 1}, {3, 0}, {4, 1}})))",
				"<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" viewBox=\"-1.666667 -1.666667 353.333333 353.333333\" width=\"350px\" height=\"350px\">\n"
						+ "<polyline points=\"0.0,350.0 116.66667,0.0 233.33333,350.0 350.0,0.0\" \n"
						+ "          style=\"stroke: rgb(0.000000%, 0.000000%, 0.000000%); stroke-opacity: 1; stroke-width: 0.666667px; fill: none\" />\n"
						+ "</svg>");

	}

	public void testPlot() {

		checkSVGGraphics("Plot(Sin(x),{x,-10,10})",
				"<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" viewBox=\"-1.666667 -1.666667 353.333333 353.333333\" width=\"350px\" height=\"350px\">\n" + 
				"<polyline points=\"0.0,88.41427 3.46535,116.67163 6.93069,147.25436 10.39604,178.94323 13.86139,210.47489 17.32673,240.59227 20.79208,268.0947 24.25743,291.88574 27.72277,311.01691 31.18812,324.72552 34.65347,332.46505 38.11881,333.92693 41.58416,329.05291 45.0495,318.03728 48.51485,301.3192 51.9802,279.56518 55.44554,253.64248 58.91089,224.58454 62.37624,193.54983 65.84158,161.77559 69.30693,130.52857 72.77228,101.05449 76.23762,74.52838 79.70297,52.00775 83.16832,34.39044 86.63366,22.37879 90.09901,16.45166 93.56436,16.84536 97.0297,23.54418 100.49505,36.28107 103.9604,54.54824 107.42574,77.61745 110.89109,104.56899 114.35644,134.32839 117.82178,165.70924 121.28713,197.46049 124.75248,228.3163 128.21782,257.04657 131.68317,282.50589 135.14851,303.6793 138.61386,319.72266 142.07921,329.99639 145.54455,334.09091 149.0099,331.84297 152.47525,323.3422 155.94059,308.92749 159.40594,289.17352 162.87129,264.8678 166.33663,236.97935 169.80198,206.61997 173.26733,175.0 176.73267,143.38003 180.19802,113.02065 183.66337,85.1322 187.12871,60.82648 190.59406,41.07251 194.05941,26.6578 197.52475,18.15703 200.9901,15.90909 204.45545,20.00361 207.92079,30.27734 211.38614,46.3207 214.85149,67.49411 218.31683,92.95343 221.78218,121.6837 225.24752,152.53951 228.71287,184.29076 232.17822,215.67161 235.64356,245.43101 239.10891,272.38255 242.57426,295.45176 246.0396,313.71893 249.50495,326.45582 252.9703,333.15464 256.43564,333.54834 259.90099,327.62121 263.36634,315.60956 266.83168,297.99225 270.29703,275.47162 273.76238,248.94551 277.22772,219.47143 280.69307,188.22441 284.15842,156.45017 287.62376,125.41546 291.08911,96.35752 294.55446,70.43482 298.0198,48.6808 301.48515,31.96272 304.9505,20.94709 308.41584,16.07307 311.88119,17.53495 315.34653,25.27448 318.81188,38.98309 322.27723,58.11426 325.74257,81.9053 329.20792,109.40773 332.67327,139.52511 336.13861,171.05677 339.60396,202.74564 343.06931,233.32837 346.53465,261.58573\" \n" + 
				"          style=\"stroke: rgb(0.000000%, 0.000000%, 0.000000%); stroke-opacity: 1; stroke-width: 0.666667px; fill: none\" /><polyline points=\"173.26733,0.0 173.26733,350.0\" style=\"stroke: rgb(0.000000%, 0.000000%, 0.000000%); stroke-opacity: 1; stroke-width: 0.666667px; fill: none\"/>\n" + 
				"<polyline points=\"0.0,175.0 350.0,175.0\" style=\"stroke: rgb(0.000000%, 0.000000%, 0.000000%); stroke-opacity: 1; stroke-width: 0.666667px; fill: none\"/>\n" + 
				"\n" + 
				"</svg>");

	}

	public void testRectangle001() {

		checkSVGGraphics("Show(Graphics({Rectangle({1, 1})}))",
				"<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" viewBox=\"-1.666667 -1.666667 353.333333 353.333333\" width=\"350px\" height=\"350px\">\n"
						+ "<rect x=\"0.0\" y=\"0.0\" width=\"350.0\" height=\"350.0\" \n"
						+ "      style=\"stroke: none; stroke-width: 0.000000px; fill: rgb(0.0%, 0.0%, 0.0%); stroke-opacity: 1; stroke-width: 0.666667px; fill-opacity: 1\" />\n"
						+ "\n" + "</svg>");

	}

	public void testRectangle002() {

		checkSVGGraphics("Show(Graphics({Red, Rectangle({0, 0}), Blue, Rectangle({0.5, 0.5})}))",
				"<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" viewBox=\"-1.666667 -1.666667 353.333333 353.333333\" width=\"350px\" height=\"350px\">\n"
						+ "<rect x=\"0.0\" y=\"116.66667\" width=\"233.33333\" height=\"233.33333\" \n"
						+ "      style=\"stroke: none; stroke-width: 0.000000px; fill: rgb(100.0%, 0.0%, 0.0%); stroke-opacity: 1; stroke-width: 0.666667px; fill-opacity: 1\" />\n"
						+ "<rect x=\"116.66667\" y=\"0.0\" width=\"233.33333\" height=\"233.33333\" \n"
						+ "      style=\"stroke: none; stroke-width: 0.000000px; fill: rgb(0.0%, 0.0%, 100.0%); stroke-opacity: 1; stroke-width: 0.666667px; fill-opacity: 1\" />\n"
						+ "\n" + "</svg>");

	}

	public void testRectangle003() {

		checkSVGGraphics("Show(Graphics({Red, Rectangle({0, 0},{1, 3}), Blue, Rectangle({2, 1},{4, 2})}))",
				"<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" viewBox=\"-1.666667 -1.666667 353.333333 353.333333\" width=\"350px\" height=\"350px\">\n"
						+ "<rect x=\"0.0\" y=\"0.0\" width=\"87.5\" height=\"350.0\" \n"
						+ "      style=\"stroke: none; stroke-width: 0.000000px; fill: rgb(100.0%, 0.0%, 0.0%); stroke-opacity: 1; stroke-width: 0.666667px; fill-opacity: 1\" />\n"
						+ "<rect x=\"175.0\" y=\"116.66667\" width=\"175.0\" height=\"116.66667\" \n"
						+ "      style=\"stroke: none; stroke-width: 0.000000px; fill: rgb(0.0%, 0.0%, 100.0%); stroke-opacity: 1; stroke-width: 0.666667px; fill-opacity: 1\" />\n"
						+ "\n" + "</svg>");

	}

	public void testRectangle004() {

		checkSVGGraphics("Show(Graphics({Rectangle({1, 1})}, Axes -> True, PlotRange -> {{-2, 1.5}, {-1, 1.5}}))",
				"<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" viewBox=\"-1.666667 -1.666667 353.333333 353.333333\" width=\"350px\" height=\"350px\">\n"
						+ "<rect x=\"300.0\" y=\"-70.0\" width=\"100.0\" height=\"140.0\" \n"
						+ "      style=\"stroke: none; stroke-width: 0.000000px; fill: rgb(0.0%, 0.0%, 0.0%); stroke-opacity: 1; stroke-width: 0.666667px; fill-opacity: 1\" />\n"
						+ "<polyline points=\"200.0,0.0 200.0,350.0\" style=\"stroke: rgb(0.000000%, 0.000000%, 0.000000%); stroke-opacity: 1; stroke-width: 0.666667px; fill: none\"/>\n"
						+ "<polyline points=\"0.0,140.0 350.0,140.0\" style=\"stroke: rgb(0.000000%, 0.000000%, 0.000000%); stroke-opacity: 1; stroke-width: 0.666667px; fill: none\"/>\n"
						+ "\n" + "</svg>");
	}

}
