package leveledit;

import levelmodel.DummyObject;
import levelmodel.DummyObjectList;
import levelmodel.TileMap;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 * Panel showing the tilemap and dummy objects.
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
    
    //--------------------------------------
    // tile system
    private int editlayer = 0;
    public static final int TILESIZE = 16;
    public static final int TILESPERROW = 8;
    public static final int NUM_TILES = 200;
    public static final int MIRROR_CONST = 300;
    private TileMap tileMap;
    // dummy objects
    protected DummyObjectList dummyObjects = new DummyObjectList();
    // camera
    private int camX = 0;
    private int camY = 0;
    
    /** Ref to owner class */
    private LevelEdit owner;     
    
    public boolean inited = false;
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
     * @param owner Handle to the GUI class
     */
    public MapView(Config config, LevelEdit owner) {
        this.owner = owner;
        this.config = config;
        setBackground(config.bgCol);
        markerSelectedDummy = new ImageIcon(MARKERSELECTEDDUMMY_PATH);
        markerNextDummyPos = new ImageIcon(MARKERNEXTDUMMYPOS_PATH);
        tileMap = new TileMap(10, 10, 2, 10);
    }

    /**
     * Height of currently shown tilemap.
     * @return map y val
     */
    private int getMapHeight() {
        return tileMap.getHeight();
    }

    /**
     * Width of currently shown tilemap.
     * @return map x val
     */
    private int getMapWidth() {
        return tileMap.getWidth();
    }

    // <editor-fold desc="Listeners">
    @Override
    public void mousePressed(MouseEvent e) {
        owner.clickMapView(e.getX(), e.getY(), true);
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
        owner.clickMapView(e.getX(), e.getY(), false);
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
        if (owner.selectedTool == LevelEdit.TOOL_SET_TILE) {
            owner.clickMapView(e.getX(), e.getY(), true);
            repaint();
            e.consume();
        }
    }

    // implement keylistener
    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {

        if (e.getKeyCode() == java.awt.event.KeyEvent.VK_UP) {
            dummyObjects.moveSelectedDummy(0, -MapView.TILESIZE);
        }
        if (e.getKeyCode() == java.awt.event.KeyEvent.VK_DOWN) {
            dummyObjects.moveSelectedDummy(0, MapView.TILESIZE);
        }
        if (e.getKeyCode() == java.awt.event.KeyEvent.VK_LEFT) {
            dummyObjects.moveSelectedDummy(-MapView.TILESIZE, 0);
        }
        if (e.getKeyCode() == java.awt.event.KeyEvent.VK_RIGHT) {
            dummyObjects.moveSelectedDummy(MapView.TILESIZE, 0);
        }
        if (e.getKeyCode() == java.awt.event.KeyEvent.VK_Q) {
            dummyObjects.selectPrevDummy();
        }
        if (e.getKeyCode() == java.awt.event.KeyEvent.VK_E) {
            dummyObjects.selectNextDummy();
        }
        if (e.getKeyCode() == java.awt.event.KeyEvent.VK_DELETE) {
            dummyObjects.deleteSelectedDummy();
        }

        if (e.getKeyCode() == java.awt.event.KeyEvent.VK_A) {
            camX -= TILESIZE;
        }
        if (e.getKeyCode() == java.awt.event.KeyEvent.VK_D) {
            camX += TILESIZE;
        }
        if (e.getKeyCode() == java.awt.event.KeyEvent.VK_W) {
            camY -= TILESIZE;
        }
        if (e.getKeyCode() == java.awt.event.KeyEvent.VK_S) {
            camY += TILESIZE;
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
        int tX = x / TILESIZE;
        int tY = y / TILESIZE;

        // set tile
        if (tX >= 0 && tX < tileMap.getWidth() &&
                tY >= 0 && tY < tileMap.getHeight()) {
            int mapToEdit;
            if (this.editlayer == 1) {
                mapToEdit = 1;
            } else {
                mapToEdit = 0;
            }

            int val = tileIndex + 1;

            if (this.fillKeyDown) {
                tileMap.fillRecursive(mapToEdit, tX, tY,
                        tileMap.getTileVal(tX, tY, mapToEdit),
                        val);
            } else if (this.lineKeyDown) {
                tileMap.drawLine(mapToEdit, lastEditedTileX,
                        lastEditedTileY, tX, tY, val);
            } else {
                if (this.deleteTileKeyDown) {
                    val = 0;
                }
                tileMap.setTileVal(tX, tY, mapToEdit, val);
            }

            lastEditedTileX = tX;
            lastEditedTileY = tY;
        }
    }

    /**
     * Init level from a specified file.
     * @param lf Level file format.
     * @return True.
     */
    public boolean initFromFile(LevelFileInterface lf) {
        
        dummyObjects.flushData();
        lf.read(dummyObjects, owner.typeData, tileMap);

        inited = true;
        return true;
    }

    /**
     * Sets up the enviroment from the configfile and creates a blank map.
     * 
     * @param x width of new map.
     * @param y height of new map.
     * @return true if all's ok.
     */
    public boolean initBlankMap(int x, int y) {

        dummyObjects.flushData();
        tileMap = new TileMap(x, y, 2, TILESIZE);

        inited = true;

        x = -30;
        y = -30;

        return true;
    }    
    
    /**
     * Save level to file.
     * @param lf Specifies file format.
     */
    public void saveLevel(LevelFileInterface lf) {
        lf.write(dummyObjects, tileMap);
    }

    /**
     * Camera position x
     * @return camX
     */
    int getScrollX() {
        return camX;
    }

    /**
     * Camera position y
     * @return camY
     */
    int getScrollY() {
        return camY;
    }

    //<editor-fold desc="Paint methods.">
    
    /**
     * Draw a tile map to screen
     * @param g Graphics object
     * @param map A int[][] array of tile values.
     */
    private void paintMap(Graphics g, int[][] map) {
        
        // get camera pos
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
                    int row = tile / TILESPERROW;
                    tile = tile % TILESPERROW;

                    // non-mirrored
                    if (map[j][i] <= MIRROR_CONST) {
                        g.drawImage(config.tiles.getImage(),
                                //dest
                                i * TILESIZE - x,
                                j * TILESIZE - y,
                                (i + 1) * TILESIZE - x,
                                (j + 1) * TILESIZE - y,
                                // src
                                (tile) * TILESIZE,
                                row * TILESIZE,
                                (tile + 1) * TILESIZE,
                                (0 + row + 1) * TILESIZE,
                                this);
                    } // mirrored tiles
                    else {
                        g.drawImage(config.tiles.getImage(),
                                //dest
                                i * TILESIZE - x, 
                                j * TILESIZE - y,
                                (i + 1) * TILESIZE - x, 
                                (j + 1) * TILESIZE - y,
                                // src
                                (map[j][i] - MIRROR_CONST + 1) * TILESIZE, 
                                0,
                                (map[j][i] - MIRROR_CONST) * TILESIZE, 
                                TILESIZE,
                                this);
                    }
                }
            }
        }
    }

    /**
     * Paint. :)
     * @param g
     */
    @Override
    public void paint(Graphics g) {
        if (!inited) {
            return;
        }

        super.paint(g);

        int x = getScrollX();
        int y = getScrollY();

        // draw tiles
        paintMap(g, tileMap.getMap(1));
        paintMap(g, tileMap.getMap(0));

        // draw the dummy objetcs
        DummyObject p;
        for (int i = 0; i < dummyObjects.size(); i++) {
            p = (DummyObject) dummyObjects.elementAt(i);

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
                this.getMapHeight() * TILESIZE - y,
                this.getWidth(),
                this.getMapHeight() * TILESIZE - y);
        g.drawLine(this.getMapWidth() * TILESIZE - x,
                0 - y,
                this.getMapWidth() * TILESIZE - x,
                this.getHeight() - y);

        // show where next entity will be created
        g.setColor(new Color(0x0000FF));
        if (markerNextDummyPos != null) {
            g.drawImage(markerNextDummyPos.getImage(),
                    newXPos - 10,
                    this.getHeight() - 40,
                    this);
        } else {
            g.drawLine(newXPos - markerNextDummyPos.getImage().getWidth(this) / 2,
                    this.getMapHeight() * TILESIZE - y,
                    newXPos,
                    this.getHeight());
        }

        // show selected dummy
        g.setColor(new Color(0x00FF00));
        if (dummyObjects.selectedDummy != null) {
            if (markerSelectedDummy != null) {
                g.drawImage(markerSelectedDummy.getImage(),
                        (dummyObjects.selectedDummy.x + 
                        dummyObjects.selectedDummy.w / 2 
                        - (markerSelectedDummy.getImage()).getWidth(this) / 2) 
                        - x,
                        dummyObjects.selectedDummy.y - 30 - y,
                        this);
            } else {
                g.drawRect(dummyObjects.selectedDummy.x - x, 
                        dummyObjects.selectedDummy.y - 4 - y, 
                        dummyObjects.selectedDummy.w, 
                        4);
            }
        }
    }
    //</editor-fold>
   
}