package leveledit;

import levelmodel.DummyObject;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
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
        implements MouseListener, MouseMotionListener, KeyListener {
    
    /** window top to map start */
    private static final int Y_OFFSET = 70;
    
    /** Default placement of new dummy. */
    private static final int STDNEWXPOS = 500;
    
    /** Position for next created dummy. */
    private int newXPos = STDNEWXPOS;
    
    /** Image for selected dummy. */
    private ImageIcon markerSelectedDummy;
    
    /** Image for next dummy placement. */
    private ImageIcon markerNextDummyPos;
    
    /** Path to selected dummy image. */
    private static final String MARKERSELECTEDDUMMY_PATH 
            = "res/markerSelectedDummy.png";
    
    /** Path to next dummy image. */
    private static final String MARKERNEXTDUMMYPOS_PATH 
            = "res/markerNextDummyPos.png";
    
    /** Storage of config. */
    private Config config;    
    
    /** Reference to level. */
    private Level level;
    
    /** Reference to tool selector. */
    private ToolSelector toolSelector;   
    
    private LevelEdit leveledit;
    
    /** Scroll of map. */
    private int scrollX = 0;
    
    /** Scroll of map. */
    private int scrollY = 0; 

    /** Layer currently edited. */
    private int editlayer = 0;
    /** key to delete tiles */
    private boolean deleteTileKeyDown = false;
    /** key to fill */
    private boolean fillKeyDown = false;
    /** Key to draw lines */
    private boolean lineKeyDown = false;
    /** Last edited tile, used to draw lines. */
    private int lastEditedTileX = 0;
    /** Last edited tile, used to draw lines. */
    private int lastEditedTileY = 0;

    /**
     * Constructor.
     * @param config 
     * @param owner Handle to the GUI class
     * @param level  
     */
    public MapView(Config config, ToolSelector toolSelector, Level level, LevelEdit leveledit) {
        this.toolSelector = toolSelector;
        this.config = config;
        this.level = level;
        this.leveledit = leveledit;
        setBackground(config.bgCol);
        markerSelectedDummy = new ImageIcon(MARKERSELECTEDDUMMY_PATH);
        markerNextDummyPos = new ImageIcon(MARKERNEXTDUMMYPOS_PATH);
    }

    // <editor-fold desc="Listeners">
    /**
     * 
     * @param e
     */
    @Override
    public void mousePressed(MouseEvent e) {
        clickMapView(e.getX(), e.getY(), true);
        repaint();
    }

    /**
     * 
     * @param e
     */
    @Override
    public void mouseReleased(MouseEvent e) {
    }

    /**
     * 
     * @param e
     */
    @Override
    public void mouseEntered(MouseEvent e) {
    }

    /**
     * 
     * @param e
     */
    @Override
    public void mouseExited(MouseEvent e) {
    }

    /**
     * 
     * @param e
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        clickMapView(e.getX(), e.getY(), false);
        repaint();
    }

    // implement mousemovelistener
    /**
     * 
     * @param e
     */
    @Override
    public void mouseMoved(MouseEvent e) {
        // called during motion when no buttons are down
        e.consume();
    }

    /**
     * 
     * @param e
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        // called during motion with buttons down
        if (toolSelector.getTool() == ToolSelector.Tool.SET_TILE) {
            clickMapView(e.getX(), e.getY(), true);
            repaint();
            e.consume();
        }
    }

    // implement keylistener
    /**
     * 
     * @param e
     */
    @Override
    public void keyTyped(KeyEvent e) {
    }

    /**
     * 
     * @param e
     */
    @Override
    public void keyPressed(KeyEvent e) {

        if (e.getKeyCode() == java.awt.event.KeyEvent.VK_UP) {
            level.getDummyObjects().moveSelectedDummy(0, -Config.TILESIZE);
        }
        if (e.getKeyCode() == java.awt.event.KeyEvent.VK_DOWN) {
            level.getDummyObjects().moveSelectedDummy(0, Config.TILESIZE);
        }
        if (e.getKeyCode() == java.awt.event.KeyEvent.VK_LEFT) {
            level.getDummyObjects().moveSelectedDummy(-Config.TILESIZE, 0);
        }
        if (e.getKeyCode() == java.awt.event.KeyEvent.VK_RIGHT) {
            level.getDummyObjects().moveSelectedDummy(Config.TILESIZE, 0);
        }
        if (e.getKeyCode() == java.awt.event.KeyEvent.VK_Q) {
            level.getDummyObjects().selectPrevDummy();
        }
        if (e.getKeyCode() == java.awt.event.KeyEvent.VK_E) {
            level.getDummyObjects().selectNextDummy();
        }
        if (e.getKeyCode() == java.awt.event.KeyEvent.VK_DELETE) {
            level.getDummyObjects().deleteSelectedDummy();
        }

        if (e.getKeyCode() == java.awt.event.KeyEvent.VK_A) {
            scrollX -= Config.TILESIZE;
        }
        if (e.getKeyCode() == java.awt.event.KeyEvent.VK_D) {
            scrollX += Config.TILESIZE;
        }
        if (e.getKeyCode() == java.awt.event.KeyEvent.VK_W) {
            scrollY -= Config.TILESIZE;
        }
        if (e.getKeyCode() == java.awt.event.KeyEvent.VK_S) {
            scrollY += Config.TILESIZE;
        }

        if (e.getKeyCode() == java.awt.event.KeyEvent.VK_CONTROL) {
            this.deleteTileKeyDown = true;
        }

        if (e.getKeyCode() == java.awt.event.KeyEvent.VK_F) {
            this.fillKeyDown = true;
        }

        if (e.getKeyCode() == java.awt.event.KeyEvent.VK_1) {
            this.editlayer = 0;
            System.out.println("Editing fg tile layer");
        }

        if (e.getKeyCode() == java.awt.event.KeyEvent.VK_2) {
            this.editlayer = 1;
            System.out.println("Editing bg tile layer");
        }

        if (e.getKeyCode() == java.awt.event.KeyEvent.VK_L) {
            this.lineKeyDown = true;
            System.out.println("Line tool modifier down.");
        }

        repaint();
    }
    

    /**
     * 
     * @param e
     */
    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == java.awt.event.KeyEvent.VK_CONTROL) {
            this.deleteTileKeyDown = false;
        }

        if (e.getKeyCode() == java.awt.event.KeyEvent.VK_F) {
            this.fillKeyDown = false;
        }

        if (e.getKeyCode() == java.awt.event.KeyEvent.VK_L) {
            this.lineKeyDown = false;
            System.out.println("Line tool modifier released.");
        }
    }
    // </editor-fold>
    
    
    
    /**
     * User clicks the "mapView" frame, ie. the tilemap frame.
     * @param x  Mouse pos x
     * @param y  Mouse pos y
     * @param repeated If the click event is from a "press down".
     */
    public void clickMapView(int x, int y, boolean repeated) {

        switch (toolSelector.getTool()) {
            case NEW_DUMMY:
                DummyObject d = leveledit.dummyTypeSelector.createNewDummyObject();
                level.getDummyObjects().newDummy(x, y, d);
                break;
            case SELECT_DUMMY:
                level.getDummyObjects().selectDummy(x, y);
                break;
            case SET_TILE:
                if (leveledit.tileSelector.getSelectedIndex() != -1) {
                    setTileVal(x, y, leveledit.tileSelector.getSelectedIndex());
                }
                break;
        }
        requestFocusInWindow();
    }
    
    
    /**
     * Sets tile at mouse click, decides how to behave dependent on selected 
     * tile editing mode.
     * @param x Mouse x
     * @param y Mouse y
     * @param tileIndex Tile index number
     */
    public void setTileVal(int x, int y, int tileIndex) {
        
        // to map coords
        x = x + getScrollX();
        y = y + getScrollY();

        // to tile index
        int tX = x / Config.TILESIZE;
        int tY = y / Config.TILESIZE;

        // set tile
        if (tX >= 0 && tX < level.getTileMap().getWidth() &&
                tY >= 0 && tY < level.getTileMap().getHeight()) {
            int mapToEdit;
            if (this.editlayer == 1) {
                mapToEdit = 1;
            } else {
                mapToEdit = 0;
            }

            int val = tileIndex + 1;

            if (this.fillKeyDown) {
                level.getTileMap().fillRecursive(mapToEdit, tX, tY,
                        level.getTileMap().getTileVal(tX, tY, mapToEdit),
                        val);
            } else if (this.lineKeyDown) {
                level.getTileMap().drawLine(mapToEdit, lastEditedTileX,
                        lastEditedTileY, tX, tY, val);
            } else {
                if (this.deleteTileKeyDown) {
                    val = 0;
                }
                level.getTileMap().setTileVal(tX, tY, mapToEdit, val);
            }

            lastEditedTileX = tX;
            lastEditedTileY = tY;
        }
    }

    /**
     * Scroll position x
     * @return scrollX
     */
    int getScrollX() {
        return scrollX;
    }

    /**
     * Scroll position y
     * @return scrollY
     */
    int getScrollY() {
        return scrollY;
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
                    int row = tile / Config.TILESPERROW;
                    tile = tile % Config.TILESPERROW;

                    // non-mirrored
                    if (map[j][i] <= Config.MIRROR_CONST) {
                        g.drawImage(config.tiles.getImage(),
                                //dest
                                i * Config.TILESIZE - x,
                                j * Config.TILESIZE - y,
                                (i + 1) * Config.TILESIZE - x,
                                (j + 1) * Config.TILESIZE - y,
                                // src
                                (tile) * Config.TILESIZE,
                                row * Config.TILESIZE,
                                (tile + 1) * Config.TILESIZE,
                                (0 + row + 1) * Config.TILESIZE,
                                this);
                    } // mirrored tiles
                    else {
                        g.drawImage(config.tiles.getImage(),
                                //dest
                                i * Config.TILESIZE - x, 
                                j * Config.TILESIZE - y,
                                (i + 1) * Config.TILESIZE - x, 
                                (j + 1) * Config.TILESIZE - y,
                                // src
                                (map[j][i] - Config.MIRROR_CONST + 1) * Config.TILESIZE, 
                                0,
                                (map[j][i] - Config.MIRROR_CONST) * Config.TILESIZE, 
                                Config.TILESIZE,
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
        paintMap(g, level.getTileMap().getMap(1));
        paintMap(g, level.getTileMap().getMap(0));

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
                level.getTileMap().getHeight() * Config.TILESIZE - y,
                this.getWidth(),
                level.getTileMap().getHeight() * Config.TILESIZE - y);
        g.drawLine(level.getTileMap().getWidth() * Config.TILESIZE - x,
                0 - y,
                level.getTileMap().getWidth() * Config.TILESIZE - x,
                this.getHeight() - y);

        // show where next dummy will be created
        g.setColor(new Color(0x0000FF));
        if (markerNextDummyPos != null) {
            g.drawImage(markerNextDummyPos.getImage(),
                    newXPos - 10,
                    this.getHeight() - 40,
                    this);
        } else {
            g.drawLine(newXPos - markerNextDummyPos.getImage().getWidth(this) / 2,
                    level.getTileMap().getHeight() * Config.TILESIZE - y,
                    newXPos,
                    this.getHeight());
        }

        // show selected dummy
        g.setColor(new Color(0x00FF00));
        if (level.getDummyObjects().selectedDummy != null) {
            if (markerSelectedDummy != null) {
                g.drawImage(markerSelectedDummy.getImage(),
                        (level.getDummyObjects().selectedDummy.x + 
                        level.getDummyObjects().selectedDummy.w / 2 
                        - (markerSelectedDummy.getImage()).getWidth(this) / 2) 
                        - x,
                        level.getDummyObjects().selectedDummy.y - 30 - y,
                        this);
            } else {
                g.drawRect(level.getDummyObjects().selectedDummy.x - x, 
                        level.getDummyObjects().selectedDummy.y - 4 - y, 
                        level.getDummyObjects().selectedDummy.w, 
                        4);
            }
        }
    }
    //</editor-fold>
   
}