package leveledit;

import levelmodel.DummyObject;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.File;
import java.awt.image.BufferedImage;
import levelfileformats.Blocko2LevelFile;
import levelfileformats.InternalLevelFile;
import levelfileformats.MappyLevelFile;

/**
 * The GUI class of the LevelEdit application.
 * Sets up all the buttons, menus and stuff in the main window.
 * 
 * <p>TODO: Not the best way to set up a GUI in java.
 * This should be revised.
 * 
 */
public class LevelEdit extends JFrame
{
    // Help text
    private static final String HELP_TEXT = 
	"Editor help                              \n"+
	"-----------                              \n"+
	"                                         \n"+
	"Keys:                                    \n"+
	"A,W,S,D - move camera                    \n"+
	"Tiletool + CTRL + click - erase          \n"+
	"Tiletool + F + click - flood fill        \n"+
	"Q,E - select next/prev dummy             \n"+
	"1,2 - edit FG / BG tile layer            \n"+
	"UP/DOWN/LEFT/RIGHT - move selected dummy.\n"+
	"                                         \n"+
	"                                         \n"+
	""
    ;
   
    
    // <editor-fold desc="GUI elements">

    // menu bar
    private JMenuItem newMapItem;    
    private JMenuItem saveItem;
    private JMenuItem saveAsItem;
    private JMenuItem openItem;
    private JMenuItem importMappyItem;  
    private JMenuItem exportAsBlockoFormatItem;
    private JMenuItem exportAsBlockoFormat2Item;
    private JMenuItem helpItem;

    // tool choose btns
    private JButton toolNewDummy;
    private JButton toolSelectDummy;
    private JButton toolSetTile;

    // object edit btns    
    private JButton moveUp; 
    private JButton moveDown; 
    private JButton moveRight; 
    private JButton moveLeft;
    private JButton nudgeUp;
    private JButton nudgeDown;
    private JButton nudgeRight; 
    private JButton nudgeLeft;
    private JButton newDummyButton; 
    private JButton nextObjBtn; 
    private JButton prevObjBtn;
    private JPanel  dummyTab;
    private JLabel  message;
    public  JSlider scrollbar;
    public  JList   types; // captions of dummy types

    // edit tiles btns
    public JList tileList;   
    
    // </editor-fold>
    
    // constants
    public static final String
	LABEL_UP           = "^",
	LABEL_DOWN         = "v",
	LABEL_RIGHT        = ">",
	LABEL_LEFT         = "<";
    
    // initial window props.
    private static final int
	SIZE_X             = 1200,
	SIZE_Y             = 500,
	STARTLOC_X         = 10,
	STARTLOC_Y         = 100;

    // for showing open file
    public static File currentFile = null;

    // remember pos in file system - very convinient!
    public static File currentDir;
    
    // dummy type data
    public String typeNames[] = new String [1];
    public DummyObject typeData [] = new DummyObject [1];

    // "tool": what to do when mapView is clicked
    public static final int TOOL_NEW_DUMMY = 1;
    public static final int TOOL_SELECT_DUMMY = 2;
    public static final int TOOL_SET_TILE = 3;
    protected int selectedTool = TOOL_SELECT_DUMMY;
    
    // game object
    private MapView mapView; 
    
    /** 
     * Replaces the dummy list with new values
     * 
     * @param   captions    list of text for each item
     * @param   dummys      list of dummyobjects
     */
    public void updateDummyList(String captions [], DummyObject dummys [])
    {
	typeNames = captions;
	typeData = dummys;	
	types.setListData(typeNames);
        this.repaint();
    }

    /**
     * Replaces the tile select menu with new tiles
     * @param tilePic   image with the tiles in a long row horizontally
     * @param nTiles    number of tiles in the image
     * @param w         width of a tile
     * @param h         height of a tile
     */
    public void updateTileList(ImageIcon tilePic, int nTiles, int w, int h)
    {
	Image tiles = tilePic.getImage();
	ImageIcon listitems [] = new ImageIcon [nTiles];
	for (int i = 0; i < nTiles; i++)
	{
	    int row = i / MapView.TILESPERROW;
	    int tile = i % MapView.TILESPERROW;

	    BufferedImage newIm = 
                    new BufferedImage(w,h,BufferedImage.TYPE_3BYTE_BGR);
	    Graphics g = newIm.getGraphics();
	    g.drawImage(tiles, 0, 0, w, h, 
			tile*w,	row*h, (tile+1)*w, (row+1)*h, this );  
	    ImageIcon icon = new ImageIcon(newIm);
	    listitems[i] = icon;
	}
	tileList.setListData(listitems);
	tileList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
	tileList.setVisibleRowCount(8);
    }

    /**
     * User clicks the "mapView" frame, ie. the tilemap frame.
     * @param x  Mouse pos x
     * @param y  Mouse pos y
     * @param repeated If the click event is from a "press down".
     */
    public void clickMapView(int x, int y, boolean repeated) {
        switch (selectedTool) {
            case TOOL_NEW_DUMMY:
                if (types.getSelectedIndex() != -1 && !repeated) {
                    DummyObject d = new DummyObject(typeData[types.getSelectedIndex()]);
                    mapView.dummyObjects.newDummy(x, y, d);
                }
                break;
            case TOOL_SELECT_DUMMY:
                mapView.dummyObjects.selectDummy(x, y);
                break;
            case TOOL_SET_TILE:
                if (tileList.getSelectedIndex() != -1) {
                    mapView.setTileVal(x, y, tileList.getSelectedIndex());
                }
                break;
        }
        mapView.requestFocusInWindow();
    }

    /**
     * Select one editing tool and deselect all others.
     */
    public void selectToolButton(JButton toolBtn)
    {
	toolNewDummy.setSelected(false);
	toolSetTile.setSelected(false);
	toolSelectDummy.setSelected(false);
	toolBtn.setSelected(false);
    }

    /**
     * Add dummy button clicked.
     */
    public void addDummy() {
        if (types.getSelectedIndex() != -1) {
            
            message.setText("Selected dummy: " + 
                    typeData[types.getSelectedIndex()].name);
            DummyObject d = new DummyObject(typeData[types.getSelectedIndex()]);
            mapView.dummyObjects.newDummyCommand(d);
        }
    }
    
    /**
     * Create new blank level. Ask for size.
     */
    public void newLevel() {
        int x = Integer.parseInt(
                JOptionPane.showInputDialog(this, "Map size x?"));
        int y = Integer.parseInt(
                JOptionPane.showInputDialog(this, "Map size y?"));
        if (mapView.initBlankMap(x, y) == false) {
            JOptionPane.showMessageDialog(this,
                    "Couldn't create new level!");
            System.exit(0);
        }
        currentFile = null;
        setTitle("LevelEdit - untitled");
    }

    /**
     * Show a "save" dialog to get path.
     * @param useCurrent If to use already opened level if any.
     * @return Path to save to.
     */
    public String getSaveLevelPath(boolean useCurrent) {
        if (!useCurrent || currentFile == null) {
            
            JFileChooser fc = new JFileChooser("");
            fc.showSaveDialog(this);
            fc.setCurrentDirectory(currentDir);
            File selFile = fc.getSelectedFile();
            
            if (selFile != null) {                                
                currentFile = selFile;
                setTitle("LevelEdit - " + currentFile.getAbsolutePath());
            }                        

        } else {            
            System.out.println("Wrote file " + currentFile.getAbsolutePath());            
        }
        
        return currentFile.getAbsolutePath();
    }
    
    /**
     * Show an "open" dialog to to get path.
     * @return Path to open. 
     */
    public String getOpenLevelPath() {
            
        JFileChooser fc = new JFileChooser("");
        int returnVal = fc.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
                        
            currentFile = fc.getSelectedFile();
            setTitle("LevelEdit - " + currentFile.getAbsolutePath());
            return currentFile.getAbsolutePath();
        }
        return null;
    }
        
    /**
     * Shows help window.
     */
    public void showHelp() {
        JOptionPane.showMessageDialog(this, HELP_TEXT);
        System.out.println(HELP_TEXT);
    }
    
    /**
     * Handle all actions in main window.
     */
    private class LevelEditHandler implements ActionListener
    {
	LevelEdit parentframe;
	
        @Override
	public void actionPerformed (ActionEvent event)
	{		    	    

	    if (mapView.inited)
	    {	       
		if (event.getSource() == newDummyButton) {
                    addDummy();		    
		}
		
		if (event.getSource() == nextObjBtn) {
		    mapView.dummyObjects.selectNextDummy();
		}
		
		if (event.getSource() == prevObjBtn) {
                    mapView.dummyObjects.selectPrevDummy();
		}
		
                // buttons move & nudge
                if (mapView.dummyObjects.selected() != null) {
                    // move
                    if (event.getSource() == moveUp) {
                        mapView.dummyObjects.moveSelectedDummy(0, -MapView.TILESIZE);
                    } else if (event.getSource() == moveDown) {
                        mapView.dummyObjects.moveSelectedDummy(0, MapView.TILESIZE);
                    } else if (event.getSource() == moveLeft) {
                        mapView.dummyObjects.moveSelectedDummy(-MapView.TILESIZE, 0);
                    } else if (event.getSource() == moveRight) {
                        mapView.dummyObjects.moveSelectedDummy(MapView.TILESIZE, 0);
                    } // nudge
                    else if (event.getSource() == nudgeUp) {
                        mapView.dummyObjects.moveSelectedDummy(0, -1);
                    } else if (event.getSource() == nudgeDown) {
                        mapView.dummyObjects.moveSelectedDummy(0, 1);
                    } else if (event.getSource() == nudgeLeft) {
                        mapView.dummyObjects.moveSelectedDummy(-1, 0);
                    } else if (event.getSource() == nudgeRight) {
                        mapView.dummyObjects.moveSelectedDummy(1, 0);
                    }
                }

		// MENU -> "Save" (LevelEdit format)
		if (event.getSource() == saveItem){
                    mapView.saveLevel(new InternalLevelFile(
                            getSaveLevelPath(true)));                     
		}	    

		// MENU -> "Save as" LevelEdit format
		if (event.getSource() == saveAsItem){
                    mapView.saveLevel(new InternalLevelFile(
                            getSaveLevelPath(false)));                    
		}

		// MENU -> Export as blocko format 2
		if (event.getSource() == exportAsBlockoFormat2Item){                    
                    mapView.saveLevel(new Blocko2LevelFile(
                            getSaveLevelPath(false)));  
		}

		// HELP
		if (event.getSource() == helpItem){
		    showHelp();
		}
	    }

	    
	    // MENU -> New map
	    if (event.getSource() == newMapItem){
                newLevel();		
	    }

            // MENU -> open
            if (event.getSource() == openItem) {

                if (!mapView.initFromFile(
                        new InternalLevelFile(getOpenLevelPath()))) {
                    JOptionPane.showMessageDialog(parentframe,
                            "Error while opening file!");
                    System.exit(0);
                }
            }
	    
	    // MENU -> import mappy           
            if (event.getSource() == importMappyItem) {

                if (!mapView.initFromFile(
                        new MappyLevelFile(getOpenLevelPath()))) {
                    JOptionPane.showMessageDialog(parentframe,
                            "Error while opening file!");
                    System.exit(0);
                }
            }           

	    mapView.requestFocusInWindow();
	    repaint();
	    mapView.repaint();
	}	
    }
    
    
    /**
     * Create the main window GUI.
     * @param container Container object.
     */
    private void createGui(Container container)
    {
	LevelEditHandler handler = new LevelEditHandler(); 	
	handler.parentframe = this;
        
	//------------------------------------------------------
	// menu bar init
	JMenuBar bar = new JMenuBar();
	setJMenuBar(bar);

	JMenu fileMenu = new JMenu ("File");

	JMenu helpMenu = new JMenu ("Help");
	helpItem = new JMenuItem ("Help");
	helpItem.addActionListener(handler);
	helpMenu.add(helpItem);

	newMapItem = new JMenuItem ("New map");
	newMapItem.addActionListener(handler);
	fileMenu.add(newMapItem);

	openItem = new JMenuItem ("Open");
	openItem.addActionListener(handler);
	fileMenu.add(openItem);

	importMappyItem = new JMenuItem ("Import Mappy file");
	importMappyItem.addActionListener(handler);
	fileMenu.add(importMappyItem);

	saveItem = new JMenuItem ("Save");
	saveItem.addActionListener(handler);
	fileMenu.add(saveItem);

	saveAsItem = new JMenuItem ("Save As...");
	saveAsItem.addActionListener(handler);
	fileMenu.add(saveAsItem);

	exportAsBlockoFormatItem = new JMenuItem ("Export to Blocko format");
	exportAsBlockoFormatItem.addActionListener(handler);
	fileMenu.add(exportAsBlockoFormatItem);

	exportAsBlockoFormat2Item = new JMenuItem ("Export to Blocko format 2");
	exportAsBlockoFormat2Item.addActionListener(handler);
	fileMenu.add(exportAsBlockoFormat2Item);

	bar.add(fileMenu);
	bar.add(helpMenu);

	//-----------------------------------------------------
	// DUMMYTAB
	//
	dummyTab  = new JPanel();
	dummyTab.setLayout(new GridLayout(1,9));	
	JPanel moveButtons = new JPanel();
	JPanel nudgeButtons = new JPanel();

	// tool buttons
	toolNewDummy = new JButton(new ImageIcon("res/toolNewDummy.png"));
	toolSelectDummy = new JButton(new ImageIcon("res/toolSelectDummy.png"));
	toolSetTile = new JButton(new ImageIcon("res/toolSetTile.png"));
        toolNewDummy.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                selectedTool = TOOL_NEW_DUMMY;
                selectToolButton(toolNewDummy);
            }
        });
        toolSelectDummy.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                selectedTool = TOOL_SELECT_DUMMY;
                selectToolButton(toolSelectDummy);
            }
        });
        toolSetTile.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                selectedTool = TOOL_SET_TILE;
                selectToolButton(toolSetTile);
            }
        });
	JPanel pan = new JPanel();
	pan.setLayout(new GridLayout(3,1));
	pan.add(toolNewDummy);
	pan.add(toolSelectDummy);
	pan.add(toolSetTile);
	ButtonGroup toolGroup = new ButtonGroup();
	toolGroup.add(toolNewDummy);
	toolGroup.add(toolSelectDummy);
	toolGroup.add(toolSetTile);
	dummyTab.add(pan);

	// dummylist
	types = new JList (typeNames);
	types.setVisibleRowCount (3);
	types.setSelectionMode( ListSelectionModel.SINGLE_SELECTION) ;
	JScrollPane dummyListScroller = new JScrollPane(types);
	dummyTab.add(dummyListScroller);


	//------------------------------------------------------
	// add, prev, next buttons
	JPanel middle = new JPanel();
	middle.setLayout(new GridLayout(2,2));
	message = new JLabel("-", JLabel.CENTER);
	middle.add(message);
	newDummyButton = new JButton("Add dummy");
	newDummyButton.addActionListener(handler);
	middle.add(newDummyButton);

	nextObjBtn = new JButton("Select next");
	nextObjBtn.addActionListener(handler);
	middle.add(nextObjBtn);

	prevObjBtn = new JButton("Select prev");
	prevObjBtn.addActionListener(handler);
	middle.add(prevObjBtn);
	
	dummyTab.add(middle);

	//-----------------------------------------------------------
	// move, nudge direction btns.
	moveButtons.setLayout(new GridLayout(3,3)); 

	// 1 row
	moveButtons.add(new JPanel());
	moveUp = new JButton(LABEL_UP);
	moveButtons.add(moveUp);
	moveUp.addActionListener(handler);
	moveButtons.add(new JPanel());
	
	// 2 row
	moveLeft = new JButton(LABEL_LEFT); 
	moveButtons.add(moveLeft);
	moveLeft.addActionListener(handler);
	moveButtons.add(new JLabel("MOVE", JLabel.CENTER));
	moveRight = new JButton(LABEL_RIGHT); 
	moveButtons.add(moveRight);
	moveRight.addActionListener(handler);

	// 3 row
	moveButtons.add(new JPanel());
	moveDown = new JButton(LABEL_DOWN); 
	moveButtons.add(moveDown);
	moveDown.addActionListener(handler);
	moveButtons.add(new JPanel());
      
	//--------------------------------------------------------

	nudgeButtons.setLayout(new GridLayout(3,3));

	// 1 row
	nudgeButtons.add(new JPanel());
	nudgeUp = new JButton(LABEL_UP);
	nudgeButtons.add(nudgeUp);
	nudgeUp.addActionListener(handler);
	nudgeButtons.add(new JPanel());
	
	// 2 row
	nudgeLeft = new JButton(LABEL_LEFT);
	nudgeButtons.add(nudgeLeft);
	nudgeLeft.addActionListener(handler);
	nudgeButtons.add(new JLabel("NUDGE",JLabel.CENTER));
	nudgeRight = new JButton(LABEL_RIGHT);
	nudgeButtons.add(nudgeRight);
	nudgeRight.addActionListener(handler);
	
	// 3 row
	nudgeButtons.add(new JLabel());
	nudgeDown = new JButton(LABEL_DOWN);
	nudgeButtons.add(nudgeDown);
	nudgeDown.addActionListener(handler);
	nudgeButtons.add(new JLabel());

	dummyTab.add(moveButtons);
	dummyTab.add(nudgeButtons);

	//-----------------------------------------------------
	//
	// TILETAB
	//
	tileList = new JList();
	tileList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
	//tileList.setVisibleRowCount(8);
	JScrollPane tileListScroller = new JScrollPane(tileList);
	dummyTab.add(tileListScroller);
	tileListScroller.setPreferredSize(new Dimension(200, 300));
	container.add(tileListScroller,BorderLayout.EAST);
	container.add(dummyTab,BorderLayout.SOUTH);
	
	//------------------------------------------------------
	
	// init main window
       	setSize(SIZE_X,SIZE_Y);
	setLocation(STARTLOC_X,STARTLOC_Y);
	setVisible(true);
	setResizable(true);
        
    }
        
    /**
     * Setup app.
     * @param configfile a config file in the LevelEdit format.
     */
    public LevelEdit(String configFile)
    {
      	super("LevelEdit");
	Container container = getContentPane();
        createGui(container);

        Config config = new Config(configFile);
        
        updateTileList(config.tiles, MapView.NUM_TILES, 
                MapView.TILESIZE, MapView.TILESIZE);        
        updateDummyList(config.typeNames, config.typeData);
        
	// mapview
	mapView = new MapView(config, this);
        mapView.addKeyListener(mapView);
        mapView.addMouseListener(mapView);
        mapView.addMouseMotionListener(mapView);
	container.add(mapView, BorderLayout.CENTER);
	mapView.setVisible(true);
	repaint();
	mapView.repaint();
	mapView.requestFocusInWindow();
    }

    /**
     * App main entry.
     * 
     * @param args  arg 0 path to a config file.
     */
    public static void main(String[] args) {
        String configFile;
        if (args.length < 1) {
            System.out.println("No config specified, using default.");
            configFile = "src/leveledit/default.config";
        } else {
            configFile = args[0];
        }

        try {
            LevelEdit app = new LevelEdit(configFile);
            app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        } catch (Exception e) {
            System.out.println("Error while initializing app.");
        }
    }
}