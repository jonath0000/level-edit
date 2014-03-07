package leveledit;

import levelmodel.DummyObject;
import graphicsutils.GridDrawingUtil;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import levelmodel.Level;

/**
 * Panel showing the tile map and dummy objects.
 * 
 * Listens to mouse clicks and key events.
 *
 */
public class MapView
        extends JPanel
        implements MouseListener, MouseMotionListener {
    
    /** Image for getSelected dummy. */
    private ImageIcon markerSelectedDummy;
    
    /** Path to getSelected dummy image. */
    private static final String MARKERSELECTEDDUMMY_PATH 
            = "res/markerSelectedDummy.png";
    
    /** Reference to config. */
    private Config config;    
    
    /** Reference to level. */
    private Level level;
    
    /** Reference to tool selector. */
    private ToolSelector toolSelector;   
    
    /** Reference to tile selector. */
    private TileSelector tileSelector;
    
    /** Reference to dummy type selector. */
    private DummyTypeSelector dummyTypeSelector;
    
    /** Scroll of map. */
    private int scrollX = 0;
    
    /** Scroll of map. */
    private int scrollY = 0; 

    /** Layer currently edited. */
    private int editlayer = 0;

    /** Last edited tile, used to draw lines. */
    private int lastEditedTileX = 0;

    /** Last edited tile, used to draw lines. */
    private int lastEditedTileY = 0;
        
    /** Want second click to draw the line. */
    private boolean lineToolFirstClick = true;
    
    /** For saving undo state while press. */
    private long lastMousePressStateSaveTime = 0;
    
    /** Scale of view */
    private float scale = 1.0f;
    
    /**
     * Constructor.
     * @param config
     * @param toolSelector
     * @param tileSelector
     * @param dummyTypeSelector
     * @param level 
     */
    public MapView(
            Config config, 
            ToolSelector toolSelector, 
            TileSelector tileSelector,
            DummyTypeSelector dummyTypeSelector,
            Level level) {
        this.toolSelector = toolSelector;
        this.config = config;
        this.tileSelector = tileSelector;
        this.dummyTypeSelector = dummyTypeSelector;
        this.level = level;
        setBackground(config.bgCol);
        markerSelectedDummy = new ImageIcon(MARKERSELECTEDDUMMY_PATH);
        if (markerSelectedDummy.getImageLoadStatus() != MediaTracker.COMPLETE) {
            System.out.println("Error! Couldn't get image!");
        }
    }

    // <editor-fold desc="Listeners">

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
        if (toolSelector.getTool() == ToolSelector.Tool.SET_TILE
                || toolSelector.getTool() == ToolSelector.Tool.DELETE_TILE) {
            click(screenToModelCoord(e.getX()), screenToModelCoord(e.getY()), true);
            repaint();
            e.consume();
        }
    }

    // </editor-fold>
    
    
    
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

		switch (toolSelector.getTool()) {

		case NEW_DUMMY:
			if (!repeated) {
				level.isAboutToAlterState();
				DummyObject d = dummyTypeSelector.createNewDummyObject();
				level.getDummyObjects().newDummy(x + scrollX, y + scrollY, d);
			}
			break;

		case SELECT_DUMMY:
			level.getDummyObjects().selectDummy(x + scrollX, y + scrollY);
			break;

		case DELETE_DUMMY:
			level.isAboutToAlterState();
			level.getDummyObjects().deleteDummy(x + scrollX, y + scrollY);
			break;

		case SET_TILE:
		case DELETE_TILE:
			if (!repeated || System.currentTimeMillis() - lastMousePressStateSaveTime > 500) {
				lastMousePressStateSaveTime = System.currentTimeMillis();
				level.isAboutToAlterState();
			}
			if (tileSelector.getSelectedIndex() != -1) {
				setTileVal(x, y, tileSelector.getSelectedIndex());
			}
			break;
			
		case FILL_TILE:
		case LINE_TILE:
			if (!repeated || System.currentTimeMillis() - lastMousePressStateSaveTime > 500) {
				lastMousePressStateSaveTime = System.currentTimeMillis();
				level.isAboutToAlterState();
			}
			if (tileSelector.getSelectedIndex() != -1) {
				setTileVal(x, y, tileSelector.getSelectedIndex());
			}
			break;
			
		case PICKUP_TILE:
			if (tileSelector.getSelectedIndex() != -1) {
				setTileVal(x, y, tileSelector.getSelectedIndex());
			}
			break;
		}
		requestFocusInWindow();
	}
    
    
    /**
     * Sets tile at mouse click, decides how to behave dependent on selected 
     * tool.
     * @param x Mouse x (model coords)
     * @param y Mouse y (model coords)
     * @param tileIndex Tile index number
     */
    public void setTileVal(int x, int y, int tileIndex) {

        // to tile index
        int tX = (x + getScrollX()) / Config.representationTileSize;
        int tY = (y + getScrollY()) / Config.representationTileSize;

        // tiles are 1 indexed.
        tileIndex++;
        
        // set tile
        switch (toolSelector.getTool()) {
            case SET_TILE:
                level.getTileMap().setTileVal(tX, tY, editlayer, tileIndex);
                break;
            case DELETE_TILE:
                level.getTileMap().setTileVal(tX, tY, editlayer, 0);
                break;
            case PICKUP_TILE:
                tileSelector.setSelectedIndex(tileIndex);
                break;
            case FILL_TILE:
                level.getTileMap().fill(editlayer, tX, tY,
                    level.getTileMap().getTileVal(tX, tY, editlayer),
                    tileIndex);
                break;
            case LINE_TILE:
                    
                
                if (lineToolFirstClick) {
                    lineToolFirstClick = false;
                    level.getTileMap().setTileVal(tX, tY, editlayer, tileIndex);
                }
                else {
                	level.isAboutToAlterState();
                    level.getTileMap().drawLine(editlayer, lastEditedTileX,
                        lastEditedTileY, tX, tY, tileIndex);
                }
                break;
		case DELETE_DUMMY:
			break;
		case NEW_DUMMY:
			break;
		case SELECT_DUMMY:
			break;
		default:
			break;
        }

        lastEditedTileX = tX;
        lastEditedTileY = tY;
        if (toolSelector.getTool() != ToolSelector.Tool.LINE_TILE) {
            lineToolFirstClick = true;
        }
    }

    /**
     * Select next layer in tilemap for editing.
     */
    public void selectNextLayer() {
        if (editlayer + 1 >= level.getTileMap().getNumLayers()) return;
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
     * Scroll position x
     * @return scrollX
     */
    public int getScrollX() {
        return scrollX;
    }
 
    /**
     * Scroll position y
     * @return scrollY
     */
    public int getScrollY() {
        return scrollY;
    }
    
    /**
     * Scroll in x direction.
     * @param length Pixels to scroll.
     */
    public void scrollX(int length) {
        scrollX += length/scale;
    }
    
    /**
     * Scroll in y direction.
     * @param length Pixels to scroll.
     */
    public void scrollY(int length) {
        scrollY += length/scale;
    }

    
    /**
     * Zoom view.
     * @param scaleFactor Positive will zoom in, negative out.
     */
    public void zoom(float scaleFactor) {
    	scale *= scaleFactor;
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
    	
        int scrollXModel = getScrollX();
        int scrollYModel = getScrollY();
        
        int tileMapWidth = map[0].length;
        int tileMapHeight = map.length;

        int tileScrollX = scrollXModel / Config.representationTileSize;
        int tileScrollY = scrollYModel / Config.representationTileSize;
        int tileViewWidth = screenToModelCoord(this.getWidth()) / Config.representationTileSize;
        int tileViewHeight = screenToModelCoord(this.getHeight()) / Config.representationTileSize;
        
        Image tileImage = config.tiles;
        
        // draw tiles
        for (int tx = tileScrollX; tx < tileScrollX + tileViewWidth; tx++) {
            for (int ty = tileScrollY; ty < tileScrollY + tileViewHeight; ty++) {
                if (tx >= 0 && ty >= 0 && tx < tileMapWidth && ty < tileMapHeight && map[ty][tx] != 0) {
                    int tileValue = map[ty][tx];

                    // get row
                    tileValue--; // translate tile index to image index
                    int row = tileValue / Config.tilesPerRow;
                    tileValue = tileValue % Config.tilesPerRow;
                  
                    // non-mirrored
                    if (map[ty][tx] <= Config.mirrorTileVal) {
                        g.drawImage(tileImage,
                                //dest
                                modelToScreenCoord(tx * Config.representationTileSize - scrollXModel),
                                modelToScreenCoord(ty * Config.representationTileSize - scrollYModel),
                                modelToScreenCoord((tx + 1) * Config.representationTileSize - scrollXModel),
                                modelToScreenCoord((ty + 1) * Config.representationTileSize - scrollYModel),
                                // src
                                (tileValue) * Config.sourceImageTileSize,
                                row * Config.sourceImageTileSize,
                                (tileValue + 1) * Config.sourceImageTileSize,
                                (0 + row + 1) * Config.sourceImageTileSize,
                                this);
                    } // mirrored tiles
                    else {
                        g.drawImage(tileImage,
                                //dest
                        		modelToScreenCoord(tx * Config.representationTileSize - scrollXModel), 
                                modelToScreenCoord(ty * Config.representationTileSize - scrollYModel),
                                modelToScreenCoord((tx + 1) * Config.representationTileSize - scrollXModel), 
                                modelToScreenCoord((ty + 1) * Config.representationTileSize - scrollYModel),
                                // src
                                (map[ty][tx] - Config.mirrorTileVal + 1) * Config.sourceImageTileSize, 
                                0,
                                (map[ty][tx] - Config.mirrorTileVal) * Config.sourceImageTileSize, 
                                Config.sourceImageTileSize,
                                this);
                    }
                }
            }
        }
    }

    
    private void drawSelectedDummyIndicator(Graphics g) {
        DummyObject d = level.getDummyObjects().getSelected();
        if (d != null) {
            if (markerSelectedDummy != null) {
                g.drawImage(markerSelectedDummy.getImage(),
                        (modelToScreenCoord(d.x + 
                        d.w / 2  
                        - getScrollX())
                        - (markerSelectedDummy.getImage()).getWidth(this) / 2),
                        modelToScreenCoord(d.y - getScrollY()) - (markerSelectedDummy.getImage()).getHeight(this),
                        this);
            }
        }
    }
    
    private void drawDummyObjects(Graphics g) {
    	int scrollXModel = getScrollX();
        int scrollYModel = getScrollY();
        DummyObject dummy;
        for (int i = 0; i < level.getDummyObjects().size(); i++) {
            dummy = level.getDummyObjects().elementAt(i);
            if (dummy.pic) {
                g.drawImage(config.dummyPics.getImage(),
                        //dest
                		modelToScreenCoord(dummy.x - scrollXModel), 
                        modelToScreenCoord(dummy.y - scrollYModel), 
                        modelToScreenCoord(dummy.x + dummy.w - scrollXModel), 
                        modelToScreenCoord(dummy.y + dummy.h - scrollYModel),
                        // src
                        dummy.picX, dummy.picY, dummy.picW, dummy.picH,
                        this);
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
        for (int i = 0; i <level.getTileMap().getNumLayers(); i++) {
            paintMap(g, level.getTileMap().getMap(i));
        }

        drawDummyObjects(g);

        // show tile map borders
        GridDrawingUtil.drawBoundingBox(Color.CYAN, g, 
        		modelToScreenCoord(-getScrollX()),
        		modelToScreenCoord(-getScrollY()), 
        		modelToScreenCoord(level.getTileMap().getWidth() * Config.representationTileSize - getScrollX()),
        		modelToScreenCoord(level.getTileMap().getHeight() * Config.representationTileSize - getScrollY()));

        drawSelectedDummyIndicator(g);
        
        // show tile map layer
        g.setColor(Color.BLACK);
        g.drawString("Editing tilemap layer " + (editlayer+1) + "/" + 
                level.getTileMap().getNumLayers(), 5, getHeight() 
                - g.getFontMetrics().getHeight());
        
    }
   
}