/*
 * CS 106A Critters
 * A drawing surface that draws the state of all critters in the simulation.
 *
 * DO NOT MODIFY THIS FILE!
 *
 * @author Marty Stepp
 * @version 2015/05/23
 * - initial version for 15sp
 */

package critters.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JPanel;
import critters.model.*;

public class CritterPanel extends JPanel implements Observer, MouseListener, MouseMotionListener {
	// class constants
	private static final long serialVersionUID = 0;

	private static final boolean ANTI_ALIAS = false;
	private static final Color BACKGROUND_COLOR = new Color(220, 255, 220);
	private static final int FONT_SIZE = 12;
	private static final Font FONT = new Font("Monospaced", Font.BOLD, FONT_SIZE + 4);
	private static final Font BABY_FONT = new Font("Monospaced", Font.BOLD, FONT_SIZE + 1);
	private static final int MIN_COLOR = 192;  // darkest bg color out of 255
	private static final List<Color> PREDEFINED_COLORS = new LinkedList<Color>();
	private static final Color DRAG_DROP_COLOR = Color.PINK.darker();

	static {
		PREDEFINED_COLORS.add(new Color(255, 200, 200));
		PREDEFINED_COLORS.add(new Color(200, 200, 255));
		PREDEFINED_COLORS.add(new Color(200, 255, 200));
		PREDEFINED_COLORS.add(new Color(255, 200, 100));
		PREDEFINED_COLORS.add(new Color(200, 255, 255));
		PREDEFINED_COLORS.add(new Color(255, 255, 100));
	}

	// fields
	private CritterModel model;
	private Map<String, Color> colorMap;
	private boolean backgroundColors;
	private Point dragStart = null;   // x/y of animal being dragged/dropped
	private Point dragEnd = null;     // (null if none)
	
	// Constucts a new panel to display the given model.
	public CritterPanel(CritterModel model, boolean backgroundColors) {
		this.model = model;
		model.addObserver(this);

		colorMap = new HashMap<String, Color>();
		this.backgroundColors = backgroundColors;

		setFont(FONT);
		setBackground(BACKGROUND_COLOR);
		setPreferredSize(new Dimension(FONT_SIZE * model.getWidth() + 1,
		        FONT_SIZE * (model.getHeight()) + FONT_SIZE / 2));

		// pre-decide colors so that east GUI labels look right
		ensureAllColors();
		
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	// Ensures that any currently visible Wolf class has been assigned a
	// background color.
	public void ensureAllColors() {
		if (backgroundColors) {
			for (int x = 0; x < model.getWidth(); x++) {
				for (int y = 0; y < model.getHeight(); y++) {
					Class<? extends Critter> clazz = model.getCritterClass(x, y);
					if (CritterModel.isWolfClass(clazz)) {
						ensureColorExists(clazz);
					}
				}
			}
		}
	}

	public Color getColor(String className) {
		return colorMap.get(className);
	}
	
	// MouseListener implementation
	public void mouseClicked(MouseEvent event) {
		// empty
	}
	
	public void mouseEntered(MouseEvent event) {
		// empty
	}
	
	public void mouseExited(MouseEvent event) {
		// empty
	}
	
	public void mouseMoved(MouseEvent event) {
		// empty
	}
	
	public void mousePressed(MouseEvent event) {
		if (!model.isDebug()) {
			return;
		}
		
		Point p = event.getPoint();
		Class<? extends Critter> clazz = model.getCritterClass(getColumn(p), getRow(p));
		if (clazz != null) {
			dragStart = p;
			dragEnd = p;
			model.printDebugInfo(getColumn(p), getRow(p));
		}
	}
	
	public void mouseReleased(MouseEvent event) {
		if (!model.isDebug() || dragStart == null || dragEnd == null || dragStart.equals(dragEnd)) {
			dragStart = dragEnd = null;
			return;
		}
		
		model.move(getColumn(dragStart), getRow(dragStart),
				getColumn(dragEnd), getRow(dragEnd));
		dragStart = dragEnd = null;
		repaint();
	}
	
	public void mouseDragged(MouseEvent event) {
		if (dragStart == null || !model.isDebug()) {
			return;
		}
		dragEnd = event.getPoint();
		repaint();
	}

	// Paints the critters on the panel.
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		// anti-aliasing
		if (ANTI_ALIAS) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}

		int dragX = -1;
		int dragY = -1;
		int dragEndX = -1;
		int dragEndY = -1;
		if (dragEnd != null) {
			dragX = getColumn(dragStart);
			dragY = getRow(dragStart);
			dragEndX = getColumn(dragEnd);
			dragEndY = getRow(dragEnd);
		}
		
		// draw all critters
		for (int x = 0; x < model.getWidth(); x++) {
			for (int y = 0; y < model.getHeight(); y++) {
                int drawX = getDrawX(x);
                int drawY = getDrawY(y);

                if (x == dragX && y == dragY) {
                	int dx = dragEnd.x - dragStart.x;
                	int dy = dragEnd.y - dragStart.y;
                	drawX += dx;
                	drawY += dy;
                }

                // if sleeping/mating, draw a "zzz" bubble or heart
                if (model.isAsleep(x, y)) {
                    drawBubble(g, "z", drawX, drawY);
                } else if (model.isMating(x, y)) {
                    drawHeart(g, drawX, drawY);
                }
			}
		}
		
        for (int x = 0; x < model.getWidth(); x++) {
            for (int y = 0; y < model.getHeight(); y++) {
                int drawX = getDrawX(x);
                int drawY = getDrawY(y);
                
                // draw an animal being dragged at a different offset
                if (x == dragX && y == dragY) {
                	int dx = dragEnd.x - dragStart.x;
                	int dy = dragEnd.y - dragStart.y;
                	drawX += dx;
                	drawY += dy;
                }
                
				Color color = model.getColor(x, y);

				// possibly draw a background color behind the critter
				if (backgroundColors) {
					Class<? extends Critter> clazz = model.getCritterClass(x, y);
					if (CritterModel.isWolfClass(clazz)) {
						Color bgColor = ensureColorExists(clazz);
						g.setColor(bgColor);
						g.fillRect(drawX - 1, drawY - FONT_SIZE + 1, FONT_SIZE, FONT_SIZE + 1);
					}
				}

                // highlight the "current" critter, if we are in partial mode
                if (model.isDebug() && x == dragEndX && y == dragEndY) {
                	g.setColor(DRAG_DROP_COLOR);
                	g.drawRect(drawX - 3, drawY - FONT_SIZE - 1, FONT_SIZE + 3, FONT_SIZE + 4);
                	g.drawRect(drawX - 2, drawY - FONT_SIZE,     FONT_SIZE + 1, FONT_SIZE + 2);
                }
				
				// draw the critter's toString representation
				String critterString = model.getString(x, y);
				if (model.isBaby(x, y)) {
				    critterString = critterString.toLowerCase();
				    g.setFont(BABY_FONT);
				}
                drawShadowedString(g, critterString, color, drawX, drawY);

				if (model.isBaby(x, y)) {
				    g.setFont(FONT);
				}
                
                // highlight the "current" critter, if we are in partial mode
                if (model.isDebug() && model.isCurrentCritter(x, y)) {
                	g.setColor(Color.ORANGE);
                	g.drawRect(drawX - 3, drawY - FONT_SIZE - 1, FONT_SIZE + 3, FONT_SIZE + 4);
                	g.drawRect(drawX - 2, drawY - FONT_SIZE,     FONT_SIZE + 1, FONT_SIZE + 2);
                }
			}
		}
	}

	public void setBackgroundColors(boolean backgroundColors) {
		this.backgroundColors = backgroundColors;
		repaint();
	}

	// Responds to Observable updates to the model.
	public void update(Observable o, Object arg) {
		repaint();
	}
	
	private void drawHeart(Graphics g, int x, int y) {
        // heart (mating)
        g.setColor(Color.PINK);
        int heartX = x + FONT_SIZE / 3;
        int heartY = y - 5 * FONT_SIZE / 4;
        Polygon heart = new Polygon();
        heart.addPoint(heartX, heartY + 2);
        heart.addPoint(heartX + 2, heartY);
        heart.addPoint(heartX + 5, heartY);
        heart.addPoint(heartX + 7, heartY + 2);
        heart.addPoint(heartX + 9, heartY);
        heart.addPoint(heartX + 12, heartY);
        heart.addPoint(heartX + 14, heartY + 2);
        heart.addPoint(heartX + 14, heartY + 5);
        heart.addPoint(heartX + 7, heartY + 11);
        heart.addPoint(heartX, heartY + 5);
        g.fillPolygon(heart);
	}
	
	private void drawBubble(Graphics g, String text, int x, int y) {
	    int bubbleX = x + FONT_SIZE / 2;
	    int bubbleY = y - 3 * FONT_SIZE / 2;
	    int bubbleSize = FONT_SIZE;
	    
        g.setColor(Color.WHITE);
	    g.fillOval(bubbleX, bubbleY, bubbleSize, bubbleSize);
	    g.setColor(Color.GRAY);
        g.drawOval(bubbleX, bubbleY, bubbleSize, bubbleSize);
	    
        // draw text in bubble
        Font oldFont = g.getFont();
	    Font newFont = oldFont.deriveFont(11f);
	    Rectangle2D bounds = g.getFontMetrics().getStringBounds(text, g);
        int textX = (int) (bubbleX + bubbleSize / 2 - bounds.getWidth() / 2 + 2);
        int textY = (int) (bubbleY + bubbleSize / 2 + 11f / 2 - 2);
	    
        g.setColor(Color.BLACK);
	    g.setFont(newFont);
	    g.drawString(text, textX, textY);
        g.setFont(oldFont);
	}

	// Draws the given text with a dark shadow beneath it.
	private void drawShadowedString(Graphics g, String s, Color c, int x, int y) {
		if (s == null) {
		    return;
		}
	    g.setColor(Color.BLACK);
		drawStringSpaced(g, s, x + 1, y + 1);
		if (c != null) {
			g.setColor(c);
		}
	    drawStringSpaced(g, s, x, y);
	}
	
	// draws each letter evenly spaced.
	private void drawStringSpaced(Graphics g, String s, int x, int y) {
	    for (int i = 0; i < s.length(); i++) {
	        g.drawString(s.substring(i, i + 1), x, y);
	        x += FONT_SIZE;
	    }
	}

	private Color ensureColorExists(Class<? extends Critter> clazz) {
		Color bgColor = getColor(clazz.getName());
		if (bgColor == null) {
			if (PREDEFINED_COLORS.isEmpty()) {
				bgColor = new Color(
				        (int) (Math.random() * (256 - MIN_COLOR)) + MIN_COLOR,
				        (int) (Math.random() * (256 - MIN_COLOR)) + MIN_COLOR,
				        (int) (Math.random() * (256 - MIN_COLOR)) + MIN_COLOR);
			} else {
				bgColor = PREDEFINED_COLORS.remove(0);
			}
			colorMap.put(clazz.getName(), bgColor);
		}
		return bgColor;
	}

	// Returns the RGB opposite of the given color.
	public Color getReverseColor(Color c) {
		return new Color(~c.getRGB());
	}
	
	private int getDrawX(int x) {
        return x * FONT_SIZE + 2;
	}
	
	private int getDrawY(int y) {
        return (y + 1) * FONT_SIZE;
	}
	
	// get the row/col for a given x/y position on this panel (for mouse listener)
	private int getRow(Point p) {
		return p.y / FONT_SIZE;
	}
	
	private int getColumn(Point p) {
		return (p.x - 2) / FONT_SIZE;
	}
}
