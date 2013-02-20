package leveledit;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import levelfileformats.Blocko2LevelFile;
import levelfileformats.InternalLevelFile;
import levelfileformats.MappyLevelFile;
import levelmodel.Level;

/**
 * The GUI class of the LevelEdit application.
 * Sets up all the buttons, menus and stuff in the main window.
 * 
 */
public class LevelEdit extends JFrame {

    private static final String HELP_TEXT = 
	"Quick help:                              \n"+
        "                                         \n"+
        "Before using you should set up a config  \n"+    
        "file specifying your project. See the    \n"+  
        "stand-alone documentation for help on    \n"+    
        "this.                                    \n"+      
	"                                         \n"+
	"A,W,S,D - move camera                    \n"+
        "                                         \n"+
	"Dummy objects are game objects such as   \n"+
        "characters, pickups, trigger areas etc.  \n"+ 
        "Select from the list on screen and use   \n"+
        "the tools to create and move these.      \n"+
        "                                         \n"+
        "Use the tile tools to edit the tile map. \n"+    
        "The tile map can include several layers. \n"+    

	""
    ;

    private JPanel panel;    
    private ToolSelector toolSelector;
    private TileSelector tileSelector;
    private DummyTypeSelector dummyTypeSelector;    
    private Menu menu;
    private Config config;
    private MapView mapView; 
    private Level level = new Level();

    /** For showing open file. */
    private File currentFile = null;

    /** Remember pos in file system. */
    private File currentDir;

    
    /**
     * Create new blank level. Ask for size.
     */
    public void newLevel() {
        int x = Integer.parseInt(
                JOptionPane.showInputDialog(this, "Map size x?"));
        int y = Integer.parseInt(
                JOptionPane.showInputDialog(this, "Map size y?"));
        level.initBlankMap(x, y);
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
        @Override
	public void actionPerformed (ActionEvent event)
	{		    	    	       
            if (event.getSource() == menu.selectNextItem) {
                level.getDummyObjects().selectNextDummy();
            }

            if (event.getSource() == menu.selectPrevItem) {
                level.getDummyObjects().selectPrevDummy();
            }

            // move
            if (event.getSource() == menu.moveUpItem) {
                level.getDummyObjects().moveSelectedDummy(0, -Config.TILESIZE);
            } else if (event.getSource() == menu.moveDownItem) {
                level.getDummyObjects().moveSelectedDummy(0, Config.TILESIZE);
            } else if (event.getSource() == menu.moveLeftItem) {
                level.getDummyObjects().moveSelectedDummy(-Config.TILESIZE, 0);
            } else if (event.getSource() == menu.moveRightItem) {
                level.getDummyObjects().moveSelectedDummy(Config.TILESIZE, 0);
            } // nudge
            else if (event.getSource() == menu.nudgeUpItem) {
                level.getDummyObjects().moveSelectedDummy(0, -1);
            } else if (event.getSource() == menu.nudgeDownItem) {
                level.getDummyObjects().moveSelectedDummy(0, 1);
            } else if (event.getSource() == menu.nudgeLeftItem) {
                level.getDummyObjects().moveSelectedDummy(-1, 0);
            } else if (event.getSource() == menu.nudgeRightItem) {
                level.getDummyObjects().moveSelectedDummy(1, 0);
            }       
            
            // scroll
            if (event.getSource() == menu.scrollUpItem) {
                mapView.scrollY(-Config.TILESIZE);
            } else if (event.getSource() == menu.scrollDownItem) {
                mapView.scrollY(Config.TILESIZE);
            } else if (event.getSource() == menu.scrollLeftItem) {
                mapView.scrollX(-Config.TILESIZE);
            } else if (event.getSource() == menu.scrollRightItem) {
                mapView.scrollX(Config.TILESIZE);
            } 
            
            // tile layer
            if (event.getSource() == menu.nextLayerItem) {
                mapView.selectNextLayer();
            }
            if (event.getSource() == menu.prevLayerItem) {
                mapView.selectPrevLayer();
            }

            // MENU -> "Save" (LevelEdit format)
            if (event.getSource() == menu.saveItem) {
                level.writeToFile(new InternalLevelFile(
                        getSaveLevelPath(true)));
            }

            // MENU -> "Save as" LevelEdit format
            if (event.getSource() == menu.saveAsItem) {
                level.writeToFile(new InternalLevelFile(
                        getSaveLevelPath(false)));
            }

            // MENU -> Export as blocko format 2
            if (event.getSource() == menu.exportAsBlockoFormat2Item) {
                level.writeToFile(new Blocko2LevelFile(
                        getSaveLevelPath(false)));
            }
            
            if (event.getSource() == menu.quitItem) {
                System.exit(0);
            }

            // HELP
            if (event.getSource() == menu.helpItem) {
                showHelp();
            }

	    
	    // MENU -> New map
	    if (event.getSource() == menu.newMapItem){
                newLevel();		
	    }

            // MENU -> open
            if (event.getSource() == menu.openItem) {
                level.initFromFile(new InternalLevelFile(getOpenLevelPath()), 
                        config.typeData);
            }
	    
	    // MENU -> import mappy           
            if (event.getSource() == menu.importMappyItem) {
                level.initFromFile(new MappyLevelFile(getOpenLevelPath()),
                        config.typeData);
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
        
        menu = new Menu(handler);
        setJMenuBar(menu);

	panel = new JPanel();
	panel.setLayout(new GridLayout(1,3));	
  
        toolSelector = new ToolSelector();
	panel.add(toolSelector);
        
        dummyTypeSelector = new DummyTypeSelector();
        panel.add(dummyTypeSelector);

        tileSelector = new TileSelector();	
        panel.add(tileSelector);
        
	container.add(panel, BorderLayout.SOUTH);

	// init main window
       	setSize(1200,500);
	setLocation(10,10);
	setVisible(true);
	setResizable(true);
        
    }
        
    /**
     * Setup app.
     * @param configFile Config file path.
     */
    public LevelEdit(String configFile)
    {
      	super("LevelEdit");
        
        config = new Config(configFile);
        
	Container container = getContentPane();
        createGui(container);
  
        tileSelector.setTiles(config.tiles, Config.NUM_TILES, 
                Config.TILESIZE, Config.TILESIZE);

        dummyTypeSelector.setDummyList(config.typeNames, config.typeData);
        
	// mapview
	mapView = new MapView(config, toolSelector, tileSelector, 
                dummyTypeSelector, level);
        mapView.addMouseListener(mapView);
        mapView.addMouseMotionListener(mapView);
	container.add(mapView, BorderLayout.CENTER);
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