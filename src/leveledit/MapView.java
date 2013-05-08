package leveledit;

import levelmodel.DummyObject;
import java.awt.Color;
import java.awt.Graphics;
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
        click(e.getX(), e.getY(), true);
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
        click(e.getX(), e.getY(), false);
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
            click(e.getX(), e.getY(), true);
            repaint();
            e.consume();
        }
    }

    // </editor-fold>
    
    
    
    /**
     * Map click to tool action.
     * @param x  Mouse pos x
     * @param y  Mouse pos y
     * @param repeated If the click event is from a "press down".
     */
    public void click(int x, int y, boolean repeated) {

        switch (toolSelector.getTool()) {
            
            case NEW_DUMMY:
                DummyObject d = dummyTypeSelector.createNewDummyObject();
                level.getDummyObjects().newDummy(x + scrollX, y + scrollY, d);
                break;
                
            case SELECT_DUMMY:
                level.getDummyObjects().selectDummy(x + scrollX, y + scrollY);
                break;
                
            case DELETE_DUMMY:
                level.getDummyObjects().deleteDummy(x + scrollX, y + scrollY);
                break;    
                
            case SET_TILE:
            case DELETE_TILE:
            case FILL_TILE:
            case LINE_TILE:
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
     * @param x Mouse x
     * @param y Mouse y
     * @param tileIndex Tile index number
     */
    public void setTileVal(int x, int y, int tileIndex) {

        // to tile index
        int tX = (x + getScrollX()) / Config.tileSize;
        int tY = (y + getScrollY()) / Config.tileSize;

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
                level.getTileMap().drawLine(editlayer, lastEditedTileX,
                    lastEditedTileY, tX, tY, tileIndex);
                break;
        }

        lastEditedTileX = tX;
        lastEditedTileY = tY;
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
        scrollX += length;
    }
    
    /**
     * Scroll in y direction.
     * @param length Pixels to scroll.
     */
    public void scrollY(int length) {
        scrollY += length;
    }


    //<editor-fold desc="Paint methods.">
    
    /**
     * Draw a tile map to screen
     * @param g Graphics object
     * @param map A int[][] array of tile values.
     */
    private void paintMap(Graphics g, int[][] map) {
        
        int x = getScrollX();
        int y = getScrollY();

        // get tiles in screen
        int w = map[0].length;
        int h = map.length;

        // draw tiles
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                if (map[j][i] != 0) {
                    int tile = map[j][i];

                    // get row
                    tile--; // translate tile index to image index
                    int row = tile / Config.tilesPerRow;
                    tile = tile % Config.tilesPerRow;

                    // non-mirrored
                    if (map[j][i] <= Config.mirrorTileVal) {
                        g.drawImage(config.tiles.getImage(),
                                //dest
                                i * Config.tileSize - x,
                                j * Config.tileSize - y,
                                (i + 1) * Config.tileSize - x,
                                (j + 1) * Config.tileSize - y,
                                // src
                                (tile) * Config.tileSize,
                                row * Config.tileSize,
                                (tile + 1) * Config.tileSize,
                                (0 + row + 1) * Config.tileSize,
                                this);
                    } // mirrored tiles
                    else {
                        g.drawImage(config.tiles.getImage(),
                                //dest
                                i * Config.tileSize - x, 
                                j * Config.tileSize - y,
                                (i + 1) * Config.tileSize - x, 
                                (j + 1) * Config.tileSize - y,
                                // src
                                (map[j][i] - Config.mirrorTileVal + 1) * Config.tileSize, 
                                0,
                                (map[j][i] - Config.mirrorTileVal) * Config.tileSize, 
                                Config.tileSize,
                                this);
                    }
                }
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

        int x = getScrollX();
        int y = getScrollY();

        // draw tiles
        for (int i = 0; i <level.getTileMap().getNumLayers(); i++) {
            paintMap(g, level.getTileMap().getMap(i));
        }

        // draw the dummy objects
        DummyObject p;
        for (int i = 0; i < level.getDummyObjects().size(); i++) {
            p = (DummyObject) level.getDummyObjects().elementAt(i);

            // if has picture, draw it
            if (p.pic) {
                g.drawImage(config.dummyPics.getImage(),
                        //dest
                        p.x - x, p.y - y, p.x + p.w - x, p.y + p.h - y,
                        // src
                        p.picX, p.picY, p.picW, p.picH,
                        this);

            // no pic, draw a square
            } else {
                g.setColor(new Color(0xFF0000));
                g.drawRect(p.x - x, p.y - y, p.w, p.h);
            }
        }

        // some border lines
        g.setColor(new Color(0x555555));
        g.drawLine(0,
                -y,
                this.getWidth(),
                -y);
        g.drawLine(0 - x,
                0 - y,
                0 - x,
                this.getHeight() - y);
        g.drawLine(0,
                level.getTileMap().getHeight() * Config.tileSize - y,
                this.getWidth(),
                level.getTileMap().getHeight() * Config.tileSize - y);
        g.drawLine(level.getTileMap().getWidth() * Config.tileSize - x,
                0 - y,
                level.getTileMap().getWidth() * Config.tileSize - x,
                this.getHeight() - y);

        // show getSelected dummy
        DummyObject d = level.getDummyObjects().getSelected();
        g.setColor(new Color(0x00FF00));
        if (d != null) {
            if (markerSelectedDummy != null) {
                g.drawImage(markerSelectedDummy.getImage(),
                        (d.x + 
                        d.w / 2 
                        - (markerSelectedDummy.getImage()).getWidth(this) / 2) 
                        - x,
                        d.y - 30 - y,
                        this);
            } else {
                g.drawRect(d.x - x, 
                        d.y - 4 - y, 
                        d.w, 
                        4);
            }
        }
        
        // show tilemap layer
        g.setColor(new Color(0x000000));
        g.drawString("Editing tilemap layer " + (editlayer+1) + "/" + 
                level.getTileMap().getNumLayers(), 5, getHeight() 
                - g.getFontMetrics().getHeight());
        
    }
    //</editor-fold>
   
}