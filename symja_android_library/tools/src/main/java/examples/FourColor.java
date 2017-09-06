/*
 * @(#)FourColor.java
 */
package examples;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import jp.ac.kobe_u.cs.cream.*;

/**
 * Four color problem.
 * See Martin Gardner: Mathematical Games, Scientific American, April, 1975.
 * 
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public class FourColor extends Frame {
	private static final long serialVersionUID = -8807342308085600468L;

	static int[][] map = {
			{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
					1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
			{ 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 5, 5, 5, 5, 6, 6, 6, 6, 7, 7, 7, 7,
					8, 8, 8, 8, 9, 9, 9, 9, 10, 10, 10, 10, 11, 11, 1 },
			{ 2, 12, 12, 12, 13, 13, 13, 13, 14, 14, 14, 14, 15, 15, 15, 15,
					16, 16, 16, 16, 17, 17, 17, 17, 18, 18, 18, 18, 19, 19, 19,
					19, 20, 20, 20, 11, 1 },
			{ 2, 12, 12, 21, 21, 21, 22, 22, 22, 22, 23, 23, 23, 23, 24, 24,
					24, 24, 25, 25, 25, 25, 26, 26, 26, 26, 27, 27, 27, 27, 28,
					28, 28, 20, 20, 11, 1 },
			{ 2, 12, 12, 21, 21, 29, 29, 29, 30, 30, 30, 30, 31, 31, 31, 31,
					32, 32, 32, 32, 33, 33, 33, 33, 34, 34, 34, 34, 35, 35, 35,
					28, 28, 20, 20, 11, 1 },
			{ 2, 12, 12, 21, 21, 29, 29, 36, 36, 36, 37, 37, 37, 37, 38, 38,
					38, 38, 39, 39, 39, 39, 40, 40, 40, 40, 41, 41, 41, 35, 35,
					28, 28, 20, 20, 11, 1 },
			{ 2, 12, 12, 21, 21, 29, 29, 36, 36, 42, 42, 42, 43, 43, 43, 43,
					44, 44, 44, 44, 45, 45, 45, 45, 46, 46, 46, 41, 41, 35, 35,
					28, 28, 20, 20, 11, 1 },
			{ 2, 12, 12, 21, 21, 29, 29, 36, 36, 42, 42, 47, 47, 47, 48, 48,
					48, 48, 49, 49, 49, 49, 50, 50, 50, 46, 46, 41, 41, 35, 35,
					28, 28, 20, 20, 11, 1 },
			{ 2, 12, 12, 21, 21, 29, 29, 36, 36, 42, 42, 47, 47, 51, 51, 51,
					52, 52, 52, 52, 53, 53, 53, 50, 50, 46, 46, 41, 41, 35, 35,
					28, 28, 20, 20, 11, 1 },
			{ 2, 12, 12, 21, 21, 29, 29, 36, 36, 42, 42, 47, 47, 51, 51, 54,
					54, 54, 55, 55, 55, 53, 53, 50, 50, 46, 46, 41, 41, 35, 35,
					28, 28, 20, 20, 11, 1 },
			{ 2, 12, 12, 21, 21, 29, 29, 36, 36, 42, 42, 47, 47, 51, 51, 54,
					54, 56, 56, 55, 55, 53, 53, 50, 50, 46, 46, 41, 41, 35, 35,
					28, 28, 20, 20, 11, 1 },
			{ 2, 57, 58, 58, 59, 59, 60, 60, 61, 61, 62, 62, 63, 63, 64, 64,
					65, 56, 56, 66, 67, 67, 68, 68, 69, 69, 70, 70, 71, 71, 72,
					72, 73, 73, 1, 1, 1 },
			{ 2, 57, 58, 58, 59, 59, 60, 60, 61, 61, 62, 62, 63, 63, 64, 64,
					65, 65, 66, 66, 67, 67, 68, 68, 69, 69, 70, 70, 71, 71, 72,
					72, 73, 73, 1, 1, 1 },
			{ 2, 57, 58, 58, 59, 59, 60, 60, 61, 61, 62, 62, 63, 63, 64, 64,
					64, 74, 74, 67, 67, 67, 68, 68, 69, 69, 70, 70, 71, 71, 72,
					72, 73, 73, 1, 1, 1 },
			{ 2, 57, 58, 58, 59, 59, 60, 60, 61, 61, 62, 62, 63, 63, 63, 75,
					75, 75, 76, 76, 76, 68, 68, 68, 69, 69, 70, 70, 71, 71, 72,
					72, 73, 73, 1, 1, 1 },
			{ 2, 57, 58, 58, 59, 59, 60, 60, 61, 61, 62, 62, 62, 77, 77, 77,
					77, 78, 78, 79, 79, 79, 79, 69, 69, 69, 70, 70, 71, 71, 72,
					72, 73, 73, 1, 1, 1 },
			{ 2, 57, 58, 58, 59, 59, 60, 60, 61, 61, 61, 80, 80, 80, 80, 81,
					81, 81, 82, 82, 82, 83, 83, 83, 83, 70, 70, 70, 71, 71, 72,
					72, 73, 73, 1, 1, 1 },
			{ 2, 57, 58, 58, 59, 59, 60, 60, 60, 84, 84, 84, 84, 85, 85, 85,
					85, 86, 86, 87, 87, 87, 87, 88, 88, 88, 88, 71, 71, 71, 72,
					72, 73, 73, 1, 1, 1 },
			{ 2, 57, 58, 58, 59, 59, 59, 89, 89, 89, 89, 90, 90, 90, 90, 91,
					91, 91, 92, 92, 92, 93, 93, 93, 93, 94, 94, 94, 94, 72, 72,
					72, 73, 73, 1, 1, 1 },
			{ 2, 57, 58, 58, 58, 95, 95, 95, 95, 96, 96, 96, 96, 97, 97, 97,
					97, 98, 98, 99, 99, 99, 99, 100, 100, 100, 100, 101, 101,
					101, 101, 73, 73, 73, 1, 1, 1 },
			{ 2, 57, 57, 102, 102, 102, 102, 103, 103, 103, 103, 104, 104, 104,
					104, 105, 105, 105, 106, 106, 106, 107, 107, 107, 107, 108,
					108, 108, 108, 109, 109, 109, 109, 1, 1, 1, 1 },
			{ 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
					2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 } };

	static int[][] neighbors = {
			{ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 20, 73, 109, 110 },
			{ 2, 1, 3, 12, 57, 102, 103, 104, 105, 106, 110 },
			{ 3, 1, 2, 4, 12, 13 }, { 4, 1, 3, 5, 13, 14 },
			{ 5, 1, 4, 6, 14, 15 }, { 6, 1, 5, 7, 15, 16 },
			{ 7, 1, 6, 8, 16, 17 }, { 8, 1, 7, 9, 17, 18 },
			{ 9, 1, 8, 10, 18, 19 }, { 10, 1, 9, 11, 19, 20 },
			{ 11, 1, 10, 20 }, { 12, 2, 3, 13, 21, 57, 58 },
			{ 13, 3, 4, 12, 14, 21, 22 }, { 14, 4, 5, 13, 15, 22, 23 },
			{ 15, 5, 6, 14, 16, 23, 24 }, { 16, 6, 7, 15, 17, 24, 25 },
			{ 17, 7, 8, 16, 18, 25, 26 }, { 18, 8, 9, 17, 19, 26, 27 },
			{ 19, 9, 10, 18, 20, 27, 28 }, { 20, 1, 10, 11, 19, 28, 73 },
			{ 21, 12, 13, 22, 29, 58, 59 }, { 22, 13, 14, 21, 23, 29, 30 },
			{ 23, 14, 15, 22, 24, 30, 31 }, { 24, 15, 16, 23, 25, 31, 32 },
			{ 25, 16, 17, 24, 26, 32, 33 }, { 26, 17, 18, 25, 27, 33, 34 },
			{ 27, 18, 19, 26, 28, 34, 35 }, { 28, 19, 20, 27, 35, 72, 73 },
			{ 29, 21, 22, 30, 36, 59, 60 }, { 30, 22, 23, 29, 31, 36, 37 },
			{ 31, 23, 24, 30, 32, 37, 38 }, { 32, 24, 25, 31, 33, 38, 39 },
			{ 33, 25, 26, 32, 34, 39, 40 }, { 34, 26, 27, 33, 35, 40, 41 },
			{ 35, 27, 28, 34, 41, 71, 72 }, { 36, 29, 30, 37, 42, 60, 61 },
			{ 37, 30, 31, 36, 38, 42, 43 }, { 38, 31, 32, 37, 39, 43, 44 },
			{ 39, 32, 33, 38, 40, 44, 45 }, { 40, 33, 34, 39, 41, 45, 46 },
			{ 41, 34, 35, 40, 46, 70, 71 }, { 42, 36, 37, 43, 47, 61, 62 },
			{ 43, 37, 38, 42, 44, 47, 48 }, { 44, 38, 39, 43, 45, 48, 49 },
			{ 45, 39, 40, 44, 46, 49, 50 }, { 46, 40, 41, 45, 50, 69, 70 },
			{ 47, 42, 43, 48, 51, 62, 63 }, { 48, 43, 44, 47, 49, 51, 52 },
			{ 49, 44, 45, 48, 50, 52, 53 }, { 50, 45, 46, 49, 53, 68, 69 },
			{ 51, 47, 48, 52, 54, 63, 64 }, { 52, 48, 49, 51, 53, 54, 55 },
			{ 53, 49, 50, 52, 55, 67, 68 }, { 54, 51, 52, 55, 56, 64, 65 },
			{ 55, 52, 53, 54, 56, 66, 67 }, { 56, 54, 55, 65, 66 },
			{ 57, 2, 12, 58, 102 }, { 58, 12, 21, 57, 59, 95, 102 },
			{ 59, 21, 29, 58, 60, 89, 95 }, { 60, 29, 36, 59, 61, 84, 89 },
			{ 61, 36, 42, 60, 62, 80, 84 }, { 62, 42, 47, 61, 63, 77, 80 },
			{ 63, 47, 51, 62, 64, 75, 77 }, { 64, 51, 54, 63, 65, 74, 75 },
			{ 65, 54, 56, 64, 66, 74 }, { 66, 55, 56, 65, 67, 74 },
			{ 67, 53, 55, 66, 68, 74, 76 }, { 68, 50, 53, 67, 69, 76, 79 },
			{ 69, 46, 50, 68, 70, 79, 83 }, { 70, 41, 46, 69, 71, 83, 88 },
			{ 71, 35, 41, 70, 72, 88, 94 }, { 72, 28, 35, 71, 73, 94, 101 },
			{ 73, 1, 20, 28, 72, 101, 109 }, { 74, 64, 65, 66, 67, 75, 76 },
			{ 75, 63, 64, 74, 76, 77, 78 }, { 76, 67, 68, 74, 75, 78, 79 },
			{ 77, 62, 63, 75, 78, 80, 81 }, { 78, 75, 76, 77, 79, 81, 82 },
			{ 79, 68, 69, 76, 78, 82, 83 }, { 80, 61, 62, 77, 81, 84, 85 },
			{ 81, 77, 78, 80, 82, 85, 86 }, { 82, 78, 79, 81, 83, 86, 87 },
			{ 83, 69, 70, 79, 82, 87, 88 }, { 84, 60, 61, 80, 85, 89, 90 },
			{ 85, 80, 81, 84, 86, 90, 91 }, { 86, 81, 82, 85, 87, 91, 92 },
			{ 87, 82, 83, 86, 88, 92, 93 }, { 88, 70, 71, 83, 87, 93, 94 },
			{ 89, 59, 60, 84, 90, 95, 96 }, { 90, 84, 85, 89, 91, 96, 97 },
			{ 91, 85, 86, 90, 92, 97, 98 }, { 92, 86, 87, 91, 93, 98, 99 },
			{ 93, 87, 88, 92, 94, 99, 100 }, { 94, 71, 72, 88, 93, 100, 101 },
			{ 95, 58, 59, 89, 96, 102, 103 }, { 96, 89, 90, 95, 97, 103, 104 },
			{ 97, 90, 91, 96, 98, 104, 105 }, { 98, 91, 92, 97, 99, 105, 106 },
			{ 99, 92, 93, 98, 100, 106, 107 },
			{ 100, 93, 94, 99, 101, 107, 108 },
			{ 101, 72, 73, 94, 100, 108, 109 }, { 102, 2, 57, 58, 95, 103 },
			{ 103, 2, 95, 96, 102, 104 }, { 104, 2, 96, 97, 103, 105 },
			{ 105, 2, 97, 98, 104, 106 }, { 106, 2, 98, 99, 105, 107, 110 },
			{ 107, 99, 100, 106, 108, 110 }, { 108, 100, 101, 107, 109, 110 },
			{ 109, 1, 73, 101, 108, 110 }, { 110, 1, 2, 106, 107, 108, 109 } };

	class FourColorPanel extends Panel {
		private static final long serialVersionUID = -871639673567464376L;

		int[][] map = null;

		int[] color = null;

		Color[] ct = { Color.lightGray, Color.white, Color.orange, Color.cyan,
				Color.green };

		FourColorPanel(int[][] map) {
			this.map = map;
		}

		public synchronized void setColor(int[] color) {
			this.color = color;
		}

		@Override
		public void paint(Graphics g) {
			if (map == null)
				return;
			int h = getSize().height - 10;
			int w = getSize().width - 10;
			int M = map.length;
			int N = map[0].length;
			int cell_h = h / M;
			int cell_w = w / N;
			int y0 = (getSize().height - M * cell_h) / 2;
			int x0 = (getSize().width - N * cell_w) / 2;
			int x, y, c;
			if (color != null) {
				for (int i = 0; i < M; ++i) {
					for (int j = 0; j < N; ++j) {
						x = x0 + j * cell_w;
						y = y0 + i * cell_h;
						c = color[map[i][j]];
						g.setColor(ct[c]);
						g.fillRect(x, y, cell_w, cell_h);
					}
				}
			}
			g.setColor(Color.black);
			g.drawRect(x0, y0, N * cell_w, M * cell_h);
			for (int i = 0; i < M; ++i) {
				for (int j = 0; j < N; ++j) {
					x = x0 + j * cell_w;
					y = y0 + i * cell_h;
					if (i < M - 1 && map[i][j] != map[i + 1][j]) {
						g.drawLine(x, y + cell_h, x + cell_w, y + cell_h);
					}
					if (j < N - 1 && map[i][j] != map[i][j + 1]) {
						g.drawLine(x + cell_w, y, x + cell_w, y + cell_h);
					}
				}
			}
		}

	}

	FourColorPanel fourColorPanel;

	Button nextButton;

	Button quitButton;

	static int WAIT = 0;

	static int NEXT = 1;

	static int QUIT = 2;

	int state = WAIT;

	public FourColor(int[][] map) {
		setSize(400, 400);
		setLayout(new BorderLayout());
		fourColorPanel = new FourColorPanel(map);
		add("Center", fourColorPanel);
		Panel p = new Panel();
		nextButton = new Button("Next");
		nextButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionNext();
			}
		});
		quitButton = new Button("Quit");
		quitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionQuit();
			}
		});
		p.add(nextButton);
		p.add(quitButton);
		add("South", p);
		setVisible(true);
	}

	private void setSolution(Solution solution, IntVariable[] region) {
		int n = region.length;
		int[] color = new int[n + 1];
		for (int i = 0; i < n; i++) {
			IntDomain d = (IntDomain) solution.getDomain(region[i]);
			if (d.size() == 1) {
				color[i + 1] = d.value();
			} else {
				color[i + 1] = 0;
			}
		}
		fourColorPanel.setColor(color);
		fourColorPanel.repaint();
	}

	private synchronized void setStateWait() {
		state = WAIT;
	}

	private synchronized void actionNext() {
		state = NEXT;
		notifyAll();
	}

	private synchronized void actionQuit() {
		state = QUIT;
		notifyAll();
	}

	private synchronized void waitAction() {
		while (state == WAIT) {
			try {
				wait();
			} catch (InterruptedException e) {
			}
		}
	}

	@Override
	public void paint(Graphics g) {
		paintComponents(g);
	}

	public static void main(String[] args) {
		FourColor fc = new FourColor(map);

		Network net = new Network();
		int n = neighbors.length;
		IntVariable[] region = new IntVariable[n];

		for (int i = 0; i < n; i++)
			region[i] = new IntVariable(net, 1, 4);
		for (int i = 0; i < n; i++) {
			IntVariable v = region[neighbors[i][0] - 1];
			for (int j = 1; j < neighbors[i].length; ++j)
				if (neighbors[i][0] < neighbors[i][j])
					v.notEquals(region[neighbors[i][j] - 1]);
		}
		Solver solver = new DefaultSolver(net);
		solver.start();
		while (true) {
			fc.setStateWait();
			fc.waitAction();
			if (fc.state == QUIT)
				break;
			if (!solver.waitNext())
				break;
			fc.setSolution(solver.getSolution(), region);
			solver.resume();
		}
		solver.stop();
		fc.dispose();
	}

}
