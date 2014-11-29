package leveledit;

import levelmodel.DummyObject;
import graphicsutils.GridDrawingUtil;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Scrollable;

/**
 * Panel showing the tile map and dummy objects.
 * 
 * Listens to mouse clicks and key events.
 *
 */
public class MapView
        extends JPanel
        implements MouseListener, MouseMotionListener, Scrollable {
    
    /** Image for getSelected dummy. */
    private ImageIcon markerSelectedDummy;
    
    /** Path to getSelected dummy image. */
    private static final String MARKERSELECTEDDUMMY_PATH 
            = "res/markerSelectedDummy.png"; 

    /** Layer currently edited. */
    private int editlayer = 0;
    
    /** Scale of view */
    private float scale = 1.0f;
    
    /** for accessing the gui components state */
    LevelEditComponentsAccessor componentsAccessor;
    
    /**
     * Constructor.
     * @param componentsAccessor.getConfig()
     * @param toolSelector
     * @param componentsAccessor
     */
    public MapView(LevelEditComponentsAccessor componentsAccessor) {
        this.componentsAccessor = componentsAccessor;
        setBackground(componentsAccessor.getConfig().bgCol);
        markerSelectedDummy = new ImageIcon(MARKERSELECTEDDUMMY_PATH);
        if (markerSelectedDummy.getImageLoadStatus() != MediaTracker.COMPLETE) {
            System.out.println("Error! Couldn't get image!");
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {    	
        click(screenToModelCoord(e.getX()), screenToModelCoord(e.getY()), true);
        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        click(screenToModelCoord(e.getX()), screenToModelCoord(e.getY()), false);
        repaint();
    }

    // implement mousemovelistener

    @Override
    public void mouseMoved(MouseEvent e) {
        // called during motion when no buttons are down
        e.consume();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // called during motion with buttons down
        click(screenToModelCoord(e.getX()), screenToModelCoord(e.getY()), true);
        repaint();
        e.consume();
    }
    
    
    private Dimension getContentSize() {
    	return new Dimension((int)(componentsAccessor.getLevelModel().getTileMap().getWidth() 
    			* componentsAccessor.getConfig().representationTileSize * scale),
    			(int)(componentsAccessor.getLevelModel().getTileMap().getHeight() 
    			* componentsAccessor.getConfig().representationTileSize * scale));
    }
    
    /**
     * Called when a new level was loaded to update size etc.
     */
    public void onLevelLoaded() {
    	setPreferredSize(getContentSize());
    	revalidate();
    }
    
    
	/**
	 * Map click to tool action.
	 * 
	 * @param x
	 *            Mouse pos x (model coords)
	 * @param y
	 *            Mouse pos y (model coords)
	 * @param repeated
	 *            If the click event is from a "press down".
	 */
	public void click(int x, int y, boolean repeated) {

		int tX = x / componentsAccessor.getConfig().representationTileSize;
		int tY = y / componentsAccessor.getConfig().representationTileSize;
		componentsAccessor.getSelectedTool().click(x, y, tX, tY, repeated, 
				componentsAccessor.getLevelModel(), 
				componentsAccessor.getDummyObjectFactory(),
				componentsAccessor.getTileSelector(),
				editlayer);

		requestFocusInWindow();
	}
    


    /**
     * Select next layer in tilemap for editing.
     */
    public void selectNextLayer() {
        if (editlayer + 1 >= componentsAccessor.getLevelModel().getTileMap().getNumLayers()) return;
        editlayer ++ ;
    }
    
    /**
     * Select previous layer in tilemap for editing.
     */
    public void selectPrevLayer() {
        if (editlayer <= 0) return;
        editlayer -- ;
    }
    
    /**
     * Get currently edited layer. 
     */
    public int getSelectedLayer() {
        return editlayer;
    }
    
    /**
     * Zoom view.
     * @param scaleFactor Positive will zoom in, negative out.
     */
    public void zoom(float scaleFactor) {
    	scale *= scaleFactor;
    	setPreferredSize(getContentSize());
    	revalidate();
    	System.out.println("Change zoom to " + scale);
    }
    
    
    private int screenToModelCoord(int screenCoord) {
    	return (int)(screenCoord / scale);
    }

    
    private int modelToScreenCoord(int modelCoord) {
    	return (int)(modelCoord * scale);
    }

    
    /**
     * Draw a tile map to screen
     * @param g Graphics object
     * @param map array of tile values.
     */
    private void paintMap(Graphics g, int[][] map) {
        
        int tileMapWidth = map[0].length;
        int tileMapHeight = map.length;

		int tileScrollX = screenToModelCoord(g.getClipBounds().x) / componentsAccessor.getConfig().representationTileSize;
		int tileScrollY = screenToModelCoord(g.getClipBounds().y) / componentsAccessor.getConfig().representationTileSize;
		int tileViewWidth = 1 + screenToModelCoord(g.getClipBounds().width) / componentsAccessor.getConfig().representationTileSize;
		int tileViewHeight = 1 + screenToModelCoord(g.getClipBounds().height) / componentsAccessor.getConfig().representationTileSize;
        
        Image tileImage = componentsAccessor.getConfig().tiles;
        
        // draw tiles
        for (int tx = tileScrollX; tx <= tileScrollX + tileViewWidth; tx++) {
            for (int ty = tileScrollY; ty <= tileScrollY + tileViewHeight; ty++) {
                if (tx >= 0 && ty >= 0 && tx < tileMapWidth && ty < tileMapHeight && map[ty][tx] != 0) {
                    int tileValue = map[ty][tx];

                    // get row
                    tileValue--; // translate tile index to image index
					int row = tileValue / componentsAccessor.getConfig().tilesPerRow;
					tileValue = tileValue % componentsAccessor.getConfig().tilesPerRow;
                  
					// non-mirrored
                    if (map[ty][tx] <= componentsAccessor.getConfig().mirrorTileVal) {
                        g.drawImage(tileImage,
                                //dest
                                modelToScreenCoord(tx * componentsAccessor.getConfig().representationTileSize),
                                modelToScreenCoord(ty * componentsAccessor.getConfig().representationTileSize),
                                modelToScreenCoord((tx + 1) * componentsAccessor.getConfig().representationTileSize),
                                modelToScreenCoord((ty + 1) * componentsAccessor.getConfig().representationTileSize),
                                // src
                                (tileValue) * componentsAccessor.getConfig().sourceImageTileSize,
                                row * componentsAccessor.getConfig().sourceImageTileSize,
                                (tileValue + 1) * componentsAccessor.getConfig().sourceImageTileSize,
                                (0 + row + 1) * componentsAccessor.getConfig().sourceImageTileSize,
                                this);
                    } // mirrored tiles
                    else {
                        g.drawImage(tileImage,
                                //dest
                        		modelToScreenCoord(tx * componentsAccessor.getConfig().representationTileSize), 
                                modelToScreenCoord(ty * componentsAccessor.getConfig().representationTileSize ),
                                modelToScreenCoord((tx + 1) * componentsAccessor.getConfig().representationTileSize), 
                                modelToScreenCoord((ty + 1) * componentsAccessor.getConfig().representationTileSize),
                                // src
                                (map[ty][tx] - componentsAccessor.getConfig().mirrorTileVal + 1) * componentsAccessor.getConfig().sourceImageTileSize, 
                                0,
                                (map[ty][tx] - componentsAccessor.getConfig().mirrorTileVal) * componentsAccessor.getConfig().sourceImageTileSize, 
                                componentsAccessor.getConfig().sourceImageTileSize,
                                this);
                    }
                }
            }
        }
    }

    
    private void drawSelectedDummyIndicator(Graphics g) {
        DummyObject d = componentsAccessor.getLevelModel().getDummyObjects().getSelected();
        if (d != null) {
            if (markerSelectedDummy != null) {
                g.drawImage(markerSelectedDummy.getImage(),
                        (modelToScreenCoord(d.x + 
                        d.w / 2  - (markerSelectedDummy.getImage()).getWidth(this) / 2)),
                        modelToScreenCoord(d.y) - (markerSelectedDummy.getImage()).getHeight(this),
                        this);
            }
        }
    }
    
    private void drawDummyObjects(Graphics g) {
        DummyObject dummy;
        for (int i = 0; i < componentsAccessor.getLevelModel().getDummyObjects().size(); i++) {
            dummy = componentsAccessor.getLevelModel().getDummyObjects().elementAt(i);
        	Image dummyImage = componentsAccessor.getImageStore().getImage(dummy.name);
        	if (dummyImage != null) {
                g.drawImage(dummyImage,
                        //dest
                		modelToScreenCoord(dummy.x), 
                        modelToScreenCoord(dummy.y), 
                        modelToScreenCoord(dummy.x + dummy.w), 
                        modelToScreenCoord(dummy.y + dummy.h),
                        // src
                        0, 0, dummyImage.getWidth(this), dummyImage.getHeight(this),
                        this);
        	} else {
            	GridDrawingUtil.drawBoundingBox(Color.RED, g, modelToScreenCoord(dummy.x), 
                        modelToScreenCoord(dummy.y), 
                        modelToScreenCoord(dummy.x + dummy.w), 
                        modelToScreenCoord(dummy.y + dummy.h));
            }
            
        }
    }
    
    
    /**
     * Paint.
     * @param g
     */
    @Override
    public void paint(Graphics g) {

        super.paint(g);

        // draw tiles
        for (int i = 0; i < componentsAccessor.getLevelModel().getTileMap().getNumLayers(); i++) {
            paintMap(g, componentsAccessor.getLevelModel().getTileMap().getMap(i));
        }

        drawDummyObjects(g);

        // show tile map borders
        GridDrawingUtil.drawBoundingBox(Color.CYAN, g, 
        		0,
        		0, 
        		modelToScreenCoord(componentsAccessor.getLevelModel().getTileMap().getWidth() * componentsAccessor.getConfig().representationTileSize),
        		modelToScreenCoord(componentsAccessor.getLevelModel().getTileMap().getHeight() * componentsAccessor.getConfig().representationTileSize));

        drawSelectedDummyIndicator(g);
        
        // show tile map layer
        g.setColor(Color.BLACK);
        g.drawString("Editing tilemap layer " + (editlayer+1) + "/" + 
        		componentsAccessor.getLevelModel().getTileMap().getNumLayers(), 5, getHeight() 
                - g.getFontMetrics().getHeight());
        
    }

	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return getContentSize();
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle arg0, int arg1, int arg2) {
		return componentsAccessor.getConfig().representationTileSize;
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		return false;
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle arg0, int arg1, int arg2) {
		return componentsAccessor.getConfig().representationTileSize;
	}
   
}