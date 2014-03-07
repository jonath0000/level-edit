package leveledit;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import leveledit.ToolSelector.Tool;
import levelfileformats.Blocko2LevelFile;
import levelfileformats.CommaSeparatedTileMapLevelFile;
import levelfileformats.InternalLevelFile;
import levelfileformats.MappyLevelFile;
import levelfileformats.XmlObjectListLevelFile;
import levelmodel.DummyObject;
import levelmodel.Level;

/**
 * The GUI class of the LevelEdit application. Sets up all the buttons, menus
 * and stuff in the main window.
 * 
 */
public class LevelEdit extends JFrame implements LevelEditComponentsAccessor {

	private static final String HELP_TEXT = 
			  "Quick help:                              \n"
			+ "                                         \n"
			+ "Before using you should set up a config  \n"
			+ "file specifying your project. See the    \n"
			+ "stand-alone documentation for help on    \n"
			+ "this.                                    \n"
			+ "                                         \n"
			+ "A,W,S,D - move camera                    \n"
			+ ". ,     - zoom camera                    \n"
			+ "                                         \n"
			+ "Dummy objects are game objects such as   \n"
			+ "characters, pickups, trigger areas etc.  \n"
			+ "Select from the list on screen and use   \n"
			+ "the tools to create and move these.      \n"
			+ "                                         \n"
			+ "Use the tile tools to edit the tile map. \n"
			+ "The tile map can include several layers. \n" +

			"";

	private ToolSelector toolSelector;
	private TileSelector tileSelector;
	private DummyTypeSelector dummyTypeSelector;
	private Menu menu;
	private Config config;
	private String configFilePath;
	private String tileMapImagePath;
	private MapView mapView;
	private Level level;
	private File currentFile = null;
	private ImageStore imageStore;

	
	/**
	 * Setup app.
	 * 
	 * @param configFilePath Config file path.
	 * @param tileMapImagePath Override of image in config.
	 */
	public LevelEdit(String configFilePath, String tileMapImagePath) {
		super("LevelEdit");
		this.configFilePath = configFilePath;
		this.tileMapImagePath = tileMapImagePath;
		
		level = new Level();
		
		config = new Config(configFilePath, tileMapImagePath);	
		
		imageStore = new ImageStore(config.dummyImagePath);
		
		toolSelector = new ToolSelector();
		tileSelector = new TileSelector();
		dummyTypeSelector = new DummyTypeSelector();
		mapView = new MapView(this);
		
		Container container = getContentPane();
		createGui(container);

		loadDummyAndTileDefinitions();

		mapView.revalidate();
		mapView.repaint();
	}
	
	/**
	 * Will reload the definitions for dummys and tile image.
	 */
	private void loadDummyAndTileDefinitions() {
		tileSelector.setTiles(config.tiles, config.numTiles, config.tilesPerRow, config.sourceImageTileSize,
				config.sourceImageTileSize, getHeight()/40, getHeight()/40);
		dummyTypeSelector.setDummyList(config.typeNames, config.typeData);
	}
	
	/**
	 * Create the main window GUI.
	 * 
	 * @param container Container object.
	 */
	private void createGui(Container container) {
		LevelEditHandler handler = new LevelEditHandler();

		menu = new Menu(handler);
		setJMenuBar(menu);

		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new GridLayout(2, 1));
		JPanel bottomPanel = new JPanel();
		
		leftPanel.add(toolSelector);

		JToolBar dummyToolBar = new JToolBar();
		dummyToolBar.add(dummyTypeSelector);
		leftPanel.add(dummyToolBar);
		
		JToolBar tileToolBar = new JToolBar();
		tileToolBar.add(tileSelector);
		bottomPanel.add(tileToolBar);

		container.add(leftPanel, BorderLayout.WEST);
		container.add(bottomPanel, BorderLayout.SOUTH);

		mapView.addMouseListener(mapView);
		mapView.addMouseMotionListener(mapView);
		container.add(mapView, BorderLayout.CENTER);
		
		// init main window
		setSize(Toolkit.getDefaultToolkit().getScreenSize().width - 40, Toolkit
				.getDefaultToolkit().getScreenSize().height - 60);
		setLocation(20, 20);
		setVisible(true);
		setResizable(true);
	}
	
	
	/**
	 * Create new blank level. Ask for size.
	 */
	public void newLevel() {
		int x = Integer.parseInt(JOptionPane.showInputDialog(this,
				"Map size x?"));
		int y = Integer.parseInt(JOptionPane.showInputDialog(this,
				"Map size y?"));
		level.initBlankMap(x, y);
		currentFile = null;
		setTitle("LevelEdit - untitled");
	}

	/**
	 * Show "export" dialog.
	 * @return Path to export file to.
	 */
	private String getExportLevelPath() {
		JFileChooser fc = new JFileChooser(new File(config.exportPath));
		fc.showSaveDialog(this);
		return fc.getSelectedFile().getAbsolutePath();
	}
	
	/**
	 * Show a "save" dialog to get path.
	 * 
	 * @param useCurrent If to use already opened level if any.
	 * @param updateCurrent If to update the current file (We don't want that for export formats).
	 * @return Path to save to.
	 */
	public String getSaveLevelPath(boolean useCurrent, boolean updateCurrent) {
		
		File selFile = null;
		if (useCurrent) {
			selFile = currentFile;
		}
		
		if (!useCurrent || currentFile == null) {
			
			JFileChooser fc;
			if (currentFile != null) {
				fc = new JFileChooser(currentFile);
			} else {
				fc = new JFileChooser(new File(config.projectPath));
			}
			fc.showSaveDialog(this);
			selFile = fc.getSelectedFile();			

			if (selFile != null && updateCurrent) {
				currentFile = selFile;				
			}

		} else {
			System.out.println("Save to file " + selFile.getAbsolutePath());
		}

		return selFile.getAbsolutePath();
	}

	/**
	 * Show an "open" dialog to to get path.
	 * 
	 * @return Path to open.
	 */
	public String getOpenLevelPath() {

		JFileChooser fc;
		if (currentFile != null) {
			fc = new JFileChooser(currentFile);
		} else {
			fc = new JFileChooser(new File(config.projectPath));
		}
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
		
	public void showTileValue() {
		JOptionPane.showMessageDialog(this, "Tile value: " + tileSelector.getSelectedIndex());
	}

	/**
	 * Handle all actions in main window.
	 */
	private class LevelEditHandler implements ActionListener {			
		@Override
		public void actionPerformed(ActionEvent event) {
			
			if (event.getSource() == menu.undoItem) {
				level.undo();
			}
			
			if (event.getSource() == menu.selectNextItem) {
				level.getDummyObjects().selectNextDummy();
			}

			if (event.getSource() == menu.selectPrevItem) {
				level.getDummyObjects().selectPrevDummy();
			}

			// move
			if (event.getSource() == menu.moveUpItem) {
				level.isAboutToAlterState();
				level.getDummyObjects().moveSelectedDummy(0, -config.representationTileSize);
			} else if (event.getSource() == menu.moveDownItem) {
				level.isAboutToAlterState();
				level.getDummyObjects().moveSelectedDummy(0, config.representationTileSize);
			} else if (event.getSource() == menu.moveLeftItem) {
				level.isAboutToAlterState();
				level.getDummyObjects().moveSelectedDummy(-config.representationTileSize, 0);
			} else if (event.getSource() == menu.moveRightItem) {
				level.isAboutToAlterState();
				level.getDummyObjects().moveSelectedDummy(config.representationTileSize, 0);
			} // nudge
			else if (event.getSource() == menu.nudgeUpItem) {
				level.isAboutToAlterState();
				level.getDummyObjects().moveSelectedDummy(0, -1);
			} else if (event.getSource() == menu.nudgeDownItem) {
				level.isAboutToAlterState();
				level.getDummyObjects().moveSelectedDummy(0, 1);
			} else if (event.getSource() == menu.nudgeLeftItem) {
				level.isAboutToAlterState();
				level.getDummyObjects().moveSelectedDummy(-1, 0);
			} else if (event.getSource() == menu.nudgeRightItem) {
				level.isAboutToAlterState();
				level.getDummyObjects().moveSelectedDummy(1, 0);
			}

			if (event.getSource() == menu.deleteDummyItem) {
				level.isAboutToAlterState();
				level.getDummyObjects().deleteSelectedDummy();
			}

			if (event.getSource() == menu.editDummyCustomDataItem) {
				String newCustomData = JOptionPane.showInputDialog("Custom data for object " 
						+ level.getDummyObjects().getSelected().name, 
						level.getDummyObjects().getSelected().additionalData);
				if (newCustomData != null) {
					level.isAboutToAlterState();
					level.getDummyObjects().getSelected().additionalData = newCustomData;
				}
			}

			// scroll
			if (event.getSource() == menu.scrollUpItem) {
				mapView.scrollY(-config.representationTileSize);
			}
			if (event.getSource() == menu.scrollDownItem) {
				mapView.scrollY(config.representationTileSize);
			}
			if (event.getSource() == menu.scrollLeftItem) {
				mapView.scrollX(-config.representationTileSize);
			}
			if (event.getSource() == menu.scrollRightItem) {
				mapView.scrollX(config.representationTileSize);
			}
			
			if (event.getSource() == menu.zoomInItem) {
				mapView.zoom(2.0f);
			}
			if (event.getSource() == menu.zoomOutItem) {
				mapView.zoom(0.5f);
			}

			// tile layer
			if (event.getSource() == menu.nextLayerItem) {
				mapView.selectNextLayer();
			}
			if (event.getSource() == menu.prevLayerItem) {
				mapView.selectPrevLayer();
			}
			if (event.getSource() == menu.addLayerItem) {
				level.isAboutToAlterState();
				level.getTileMap().addMap();
			}
			if (event.getSource() == menu.deleteLayerItem) {
				level.isAboutToAlterState();
				level.getTileMap().deleteMap(mapView.getSelectedLayer());
			}
			
			if (event.getSource() == menu.showTileValueItem) {
				showTileValue();
			}		

			// MENU -> "Save" (LevelEdit format)
			if (event.getSource() == menu.saveItem) {
				String path = getSaveLevelPath(true, true);
				level.writeToFile(new InternalLevelFile(path));
				if (path != null) {
					setTitle("LevelEdit - " + currentFile.getAbsolutePath() 
							+ " | Last save: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
				}
			}

			// MENU -> "Save as" LevelEdit format
			if (event.getSource() == menu.saveAsItem) {
				String path = getSaveLevelPath(false, true);
				level.writeToFile(new InternalLevelFile(path));
				if (path != null) {
					setTitle("LevelEdit - " + currentFile.getAbsolutePath() 
							+ " | Last save: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
				}
			}

			// MENU -> Export as blocko format 2
			if (event.getSource() == menu.exportAsBlockoFormat2Item) {
				level.writeToFile(new Blocko2LevelFile(getExportLevelPath()));
			}

			// MENU -> Export as comma separated
			if (event.getSource() == menu.exportAsCommaSeparatedItem) {
				level.writeToFile(new CommaSeparatedTileMapLevelFile(
						getExportLevelPath()));
			}

			// MENU -> Export as XML object list
			if (event.getSource() == menu.exportAsXmlObjectListItem) {
				level.writeToFile(new XmlObjectListLevelFile(
						getExportLevelPath()));
			}
			
			// MENU -> Export as XML object list and comma separated
			if (event.getSource() == menu.exportAsXmlObjectListAndCommaSeparatedItem) {
				String filename = getExportLevelPath();
				level.writeToFile(new XmlObjectListLevelFile(filename + "_objects.xml"));
				level.writeToFile(new CommaSeparatedTileMapLevelFile(filename + ".tilemap"));
			}

			if (event.getSource() == menu.quitItem) {
				System.exit(0);
			}

			// HELP
			if (event.getSource() == menu.helpItem) {
				showHelp();
			}

			// MENU -> New map
			if (event.getSource() == menu.newMapItem) {
				newLevel();
			}

			// MENU -> open
			if (event.getSource() == menu.openItem) {
				String path = getOpenLevelPath();
				if (path != null) {
					level.initFromFile(new InternalLevelFile(path),
							config.typeData);
				}
			}

			// MENU -> import mappy
			if (event.getSource() == menu.importMappyItem) {
				String path = getOpenLevelPath();
				if (path != null) {
					level.initFromFile(new MappyLevelFile(path),
						config.typeData);
				}
			}
			
			if (event.getSource() == menu.reloadConfigItem) {
				config = new Config(configFilePath, tileMapImagePath);	
				loadDummyAndTileDefinitions();
			}

			mapView.repaint();
		}
	}

	/**
	 * App main entry.
	 * 
	 * Args: 
	 *   arg0: path to a config file.
	 *   arg1: override of config file tile image.
	 * 
	 * @param args arg 0 
	 */
	public static void main(String[] args) {
		String configFile;
		String tileMapImage = null;
		
		if (args.length < 1) {
			System.out.println("No config specified, using default.");
			configFile = "Example_res/example.config";
		} else {
			configFile = args[0];
		}
		
		if (args.length >= 2) {
			System.out.println("Overriding config file tilemap image with " + args[1]);
			tileMapImage = args[1];
		}

		try {
			LevelEdit app = new LevelEdit(configFile, tileMapImage);
			app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		} catch (Exception e) {
			System.out.println("Error while initializing app.");
		}
	}

	@Override
	public int getSelectedTileIndex() {
		return tileSelector.getSelectedIndex();
	}

	@Override
	public void setSelectedTileIndex(int index) {
		tileSelector.setSelectedIndex(index);
	}

	@Override
	public DummyObject createNewDummyFromSelectedType() {
		return dummyTypeSelector.createNewDummyObject();
	}

	@Override
	public Level getLevelModel() {
		return level;
	}

	@Override
	public Tool getSelectedTool() {
		return toolSelector.getTool();
	}
	
	@Override 
	public Config getConfig() {
		return config;
	}

	@Override
	public ImageStore getImageStore() {
		return imageStore;
	}
}