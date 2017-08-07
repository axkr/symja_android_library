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
				"<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" viewBox=\"-1.666667 -1.666667 353.333333 353.333333\" width=\"350px\" height=\"350px\">\n"
						+ "<polyline points=\"0.0,79.75569 3.5,110.83879 7.0,144.4798 10.5,179.33755 14.0,214.02237 17.5,247.1515 21.0,277.40417 24.5,303.57432 28.0,324.61861 31.5,339.69807 35.0,348.21155 38.5,349.81963 42.0,344.4582 45.5,332.34101 49.0,313.95112 52.5,290.0217 56.0,261.50672 59.5,229.543 63.0,195.40481 66.5,160.45315 70.0,126.08143 73.5,93.65994 77.0,64.48121 80.5,39.70853 84.0,20.32948 87.5,7.11667 91.0,0.59683 94.5,1.02989 98.0,8.3986 101.5,22.40918 105.0,42.50307 108.5,67.87919 112.0,97.52589 115.5,130.26123 119.0,164.78017 122.5,199.70654 126.0,233.64793 129.5,265.25122 133.0,293.25648 136.5,316.54723 140.0,334.19493 143.5,345.49603 147.0,350.0 150.5,347.52727 154.0,338.17642 157.5,322.32024 161.0,300.59087 164.5,273.85458 168.0,243.17728 171.5,209.78196 175.0,175.0 178.5,140.21804 182.0,106.82272 185.5,76.14542 189.0,49.40913 192.5,27.67976 196.0,11.82358 199.5,2.47273 203.0,0.0 206.5,4.50397 210.0,15.80507 213.5,33.45277 217.0,56.74352 220.5,84.74878 224.0,116.35207 227.5,150.29346 231.0,185.21983 234.5,219.73877 238.0,252.47411 241.5,282.12081 245.0,307.49693 248.5,327.59082 252.0,341.6014 255.5,348.97011 259.0,349.40317 262.5,342.88333 266.0,329.67052 269.5,310.29147 273.0,285.51879 276.5,256.34006 280.0,223.91857 283.5,189.54685 287.0,154.59519 290.5,120.457 294.0,88.49328 297.5,59.9783 301.0,36.04888 304.5,17.65899 308.0,5.5418 311.5,0.18037 315.0,1.78845 318.5,10.30193 322.0,25.38139 325.5,46.42568 329.0,72.59583 332.5,102.8485 336.0,135.97763 339.5,170.66245 343.0,205.5202 346.5,239.16121 350.0,270.24431\" \n"
						+ "          style=\"stroke: rgb(0.000000%, 0.000000%, 0.000000%); stroke-opacity: 1; stroke-width: 0.666667px; fill: none\" /><polyline points=\"175.0,0.0 175.0,350.0\" style=\"stroke: rgb(0.000000%, 0.000000%, 0.000000%); stroke-opacity: 1; stroke-width: 0.666667px; fill: none\"/>\n"
						+ "<polyline points=\"0.0,175.0 350.0,175.0\" style=\"stroke: rgb(0.000000%, 0.000000%, 0.000000%); stroke-opacity: 1; stroke-width: 0.666667px; fill: none\"/>\n"
						+ "\n" + "</svg>");

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

	// <polyline xmlns="http://www.w3.org/2000/svg" points="0.000000,233.333333 6.666667,233.333333" style="stroke:
	// rgb(0.000000%, 0.000000%, 0.000000%); stroke-opacity: 1; stroke-width: 0.666667px; fill: none"/>
}
