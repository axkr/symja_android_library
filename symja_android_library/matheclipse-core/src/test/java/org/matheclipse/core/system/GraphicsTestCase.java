package org.matheclipse.core.system;

import org.matheclipse.core.system.AbstractTestCase;

/**
 * View with: http://www.rapidtables.com/web/tools/svg-viewer-editor.htm
 * 
 */
public class GraphicsTestCase extends AbstractTestCase {
	public GraphicsTestCase(String name) {
		super(name);
	}

	public void testLine() {

		checkSVGGraphics("Show(Graphics(Line({{0,1},{0,0},{1,0},{1,1}})))",
				"<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" viewBox=\"-1.666667 -1.666667 353.333333 353.333333\" width=\"350px\" height=\"350px\">\n"
						+ "<polyline points=\"0.0,0.0 0.0,350.0 350.0,350.0 350.0,0.0\" \n"
						+ "          style=\"stroke: rgb(0.000000%, 0.000000%, 0.000000%); stroke-opacity: 1; stroke-width: 0.666667px; fill: none\" />\n"
						+ "</svg>");

	}
}
