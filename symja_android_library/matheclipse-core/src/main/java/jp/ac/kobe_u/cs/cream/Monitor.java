/*
 * @(#)Monitor.java
 */
package jp.ac.kobe_u.cs.cream;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.IdentityHashMap;

/**
 * Monitor class.
 *
 * @see Solver
 * @see ParallelSolver
 * @since 1.0
 * @version 1.4
 * @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 */
public class Monitor extends Frame {
	private static final long serialVersionUID = 2455481861250231140L;

	private long startTime;

	private ArrayList<Solver> solvers;

	private IdentityHashMap<Solver,Integer[]> solverData;

	private int current_x;

	private int xmin;

	private int xmax;

	private int ymin;

	private int ymax;

	private Image image = null;

	private long prevPaintTime;

	public int topMargin = 100;

	public int botMargin = 50;

	public int leftMargin = 50;

	public int rightMargin = 50;

	private double xscale;

	private double yscale;

	private Color[] colors = {
			Color.RED, Color.BLUE, new Color(0, 128, 0),
			new Color(0, 128, 128), Color.MAGENTA, Color.GREEN,
			new Color(128, 128, 0), Color.PINK };

	public Monitor() {
		init();
		setSize(800, 600);
		setResizable(true);
		MenuItem close = new MenuItem("Close");
		close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		MenuItem quit = new MenuItem("Quit");
		quit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		Menu menu = new Menu("Window");
		menu.add(close);
		menu.add(quit);
		MenuBar mb = new MenuBar();
		mb.add(menu);
		setMenuBar(mb);
		setBackground(Color.WHITE);
		validate();
		setVisible(true);
	}

	public synchronized void init() {
		startTime = System.currentTimeMillis();
		solvers = new ArrayList<Solver>();
		solverData = new IdentityHashMap<Solver,Integer[]>();
		current_x = 0;
		setX(0, 10);
		ymin = Integer.MAX_VALUE;
		ymax = Integer.MIN_VALUE;
	}

	/**
	 * Returns the plot colors.
	 * @return the plot colors
	 */
	public Color[] getColors() {
		return colors;
	}

	/**
	 * Sets the plot colors.
	 * @param colors the plot colors to set
	 */
	public void setColors(Color[] colors) {
		this.colors = colors;
	}

	public synchronized void setX(int xmin, int xmax) {
		this.xmin = Math.max(0, xmin);
		this.xmax = Math.max(this.xmin + 60, xmax);
	}

	public synchronized void add(Solver solver) {
		solvers.add(solver);
	}

	public synchronized void addData(Solver solver, int y) {
		long t0 = System.currentTimeMillis();
		int x = (int) ((t0 - startTime) / 1000);
		int j = x - xmin;
		if (x < xmin)
			return;
		if (x > xmax) {
			xmax = xmin + 4 * j / 3;
			if (xmax % 60 != 0)
				xmax += 60 - xmax % 60;
		}
		current_x = Math.max(current_x, x);
		ymin = Math.min(ymin, y);
		ymax = Math.max(ymax, y);
		Integer[] data = solverData.get(solver);
		if (data == null) {
			data = new Integer[xmax - xmin];
			solverData.put(solver, data);
		}
		if (j >= data.length) {
			Integer[] newData = new Integer[4 * j / 3];
			System.arraycopy(data, 0, newData, 0, data.length);
			data = newData;
			solverData.put(solver, data);
		}
		data[j] = new Integer(y);
		if (image == null || t0 - prevPaintTime >= 1000) {
			repaint();
			prevPaintTime = t0;
		}
	}

	private int wpos(int x) {
		return leftMargin + (int) (xscale * (x - xmin));
	}

	private int hpos(int y) {
		return topMargin + (int) (yscale * (ymax - y));
	}

	private void drawLine(Graphics g, int x0, int y0, int x1, int y1) {
		g.drawLine(wpos(x0), hpos(y0), wpos(x1), hpos(y1));
	}

	private synchronized void updateImage(int width, int height) {
		image = createImage(width, height);
		int w = width - (leftMargin + rightMargin);
		int h = height - (topMargin + botMargin);
		if (w <= 0 || h <= 0)
			return;
		if (xmin >= xmax || ymin >= ymax)
			return;
		xscale = (double) w / (double) (xmax - xmin);
		yscale = (double) h / (double) (ymax - ymin);
		Graphics g = image.getGraphics();
		// x-axis
		g.setColor(Color.LIGHT_GRAY);
		drawLine(g, xmin, ymin, xmax, ymin);
		drawLine(g, xmin, ymax, xmax, ymax);
		g.setColor(Color.BLACK);
		g.drawString(Integer.toString(xmax), wpos(xmax), hpos(ymin) + botMargin
				/ 4);
		// y-axis
		drawLine(g, xmin, ymin, xmin, ymax);
		g.drawString(Integer.toString(ymin), leftMargin / 3, hpos(ymin) + 5);
		g.drawString(Integer.toString(ymax), leftMargin / 3, hpos(ymax) + 5);
		g.drawString("time=" + current_x, wpos(xmin), hpos(ymin) + botMargin
				/ 2);

		for (int i = 0; i < solvers.size(); i++) {
			Solver solver = solvers.get(i);
			Integer[] data = solverData.get(solver);
			if (data == null)
				continue;
			g.setColor(colors[i % colors.length]);
			String msg = solver.toString() + "=" + solver.getBestValue();
			g.drawString(msg, wpos(xmin) + 100 * (i + 1), hpos(ymin)
					+ botMargin / 2);
			int x0 = -1;
			int y0 = -1;
			for (int j = 0; j < data.length; j++) {
				if (data[j] == null)
					continue;
				int x = xmin + j;
				int y = data[j].intValue();
				if (x0 >= 0) {
					drawLine(g, x0, y0, x, y);
				}
				x0 = x;
				y0 = y;
			}
		}
	}

	public void update(Graphics g) {
		Dimension size = getSize();
		updateImage(size.width, size.height);
		g.drawImage(image, 0, 0, null);
	}

	public void paint(Graphics g) {
		if (image != null) {
			g.drawImage(image, 0, 0, null);
		}
	}
}
