package com.alonsoruibal.chess.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class SquareJPanel extends JPanel {
	private static final long serialVersionUID = -4865276927847037885L;

	private int index;
	private boolean highlighted;
	private boolean color;

	public SquareJPanel(int index) {
		super(new BorderLayout());
		this.index = index;
		this.highlighted = false;

		Dimension size = new Dimension(75, 75);
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		setSize(size);

		int row = (index / 8) % 2;
		if (row == 0) {
			color = index % 2 == 0;
		} else {
			color = index % 2 != 0;
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawSquare(g);
		if (highlighted) {
			drawHighlight(g);
		}
	}

	private void drawSquare(Graphics g) {
		BufferedImage image = color ? ImageLoader.imgWhite : ImageLoader.imgBlack;
		g.drawImage(image, 0, 0, 75, 75, null);
	}

	private void drawHighlight(Graphics g) {
		g.setColor(Color.yellow);
		for (int i = 1; i <= 4; i++) {
			g.drawRect(i, i, 74 - 2 * i, 74 - 2 * i);
		}
	}

	public void setHighlighted(boolean highlighted) {
		this.highlighted = highlighted;
		repaint();
	}

	public int getIndex() {
		return index;
	}
}
