package leveledit;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import tools.SelectDummyTool;
import tools.SelectTileRectTool;
import tools.TileSelector;
import tools.Tool;
import leveledit.LayerListButtonBar.LayerListButtonBarListener;
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
public class LevelEdit extends JFrame 
	implements LevelEditComponentsAccessor, 
		CurrentLevelProvider,
		LayerListButtonBarListener,
		LayerSelector.SelectedLayerChangeListener {

	private static final String HELP_TEXT = 
			  "Quick help:                              \n"
			+ "                                         \n"
			+ "Before using you should set up a config  \n"
			+ "file specifying your project. See the    \n"
			+ "stand-alone documentation for help on    \n"
			+ "this.                                    \n"
			+ "                                         \n"
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
	private TileSelectorMenu tileSelector;
	private DummyTypeSelector dummyTypeSelector;
	private Menu menu;
	private Config config;
	private String configFilePath;
	private String tileMapImagePath;
	private MapView mapView;
	private Level level;
	private File currentFile = null;
	private ImageStore imageStore;
	private ApplicationClipBoard clipBoard;
	private LayerSelector layerSelector;

	
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
		
		Image image = Toolkit.getDefaultToolkit().getImage(getClass().getResource(
				"res/leveledit-icon.png")).getScaledInstance(24, 24, Image.SCALE_SMOOTH);
		ImageIcon icon = new ImageIcon(image);
		setIconImage(icon.getImage());
		
		level = new Level();
		
		config = new Config(configFilePath, tileMapImagePath);	
		
		imageStore = new ImageStore(config.dummyImagePath);
		
		layerSelector = new LayerSelector(this);
		
		toolSelector = new ToolSelector();
		tileSelector = new TileSelectorMenu();
		dummyTypeSelector = new DummyTypeSelector();
		mapView = new MapView(this);
		
		Container container = getContentPane();
		createGui(container);

		loadDummyAndTileDefinitions();

		clipBoard = new ApplicationClipBoard(this);
		
		mapView.onLevelLoaded();
		layerSelector.onLayersChanged(level.getTileMap());
		mapView.revalidate();
		mapView.repaint();
	}
	
	/**
	 * Will reload the definitions for dummys and tile image.
	 */
	private void loadDummyAndTileDefinitions() {
		tileSelector.setTiles(config.tiles, config.numTiles, config.tilesPerRow, config.sourceImageTileSize,
				config.sourceImageTileSize, getHeight()/30, getHeight()/30);
		dummyTypeSelector.setDummyList(config.dummyDefinitions);
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
		leftPanel.setLayout(new GridLayout(3, 1));
		JPanel bottomPanel = new JPanel();
		
		leftPanel.add(toolSelector);

		JPanel layerListPanel = new JPanel(new BorderLayout());
		JToolBar layerSelectorToolBar = new JToolBar();
		layerListPanel.add(layerSelector, BorderLayout.CENTER);
		layerListPanel.add(new LayerListButtonBar(this), BorderLayout.NORTH);
		layerSelectorToolBar.add(layerListPanel);
		leftPanel.add(layerSelectorToolBar, BorderLayout.WEST);
		
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
		JScrollPane scrollPane = new JScrollPane(mapView);
		container.add(scrollPane, BorderLayout.CENTER);
		
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
		mapView.onLevelLoaded();
		layerSelector.onLayersChanged(level.getTileMap());
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
			
			if (event.getSource() == menu.copyItem) {
				clipBoard.copySelectedImage();
			}
			
			if (event.getSource() == menu.cutItem) {
				level.isAboutToAlterState();
				clipBoard.cutSelectedImage();
			}
			
			if (event.getSource() == menu.pasteItem) {
				level.isAboutToAlterState();
				clipBoard.pasteImage();
			}
			
			if (event.getSource() == menu.deleteItem) {
				if (toolSelector.getTool() instanceof SelectTileRectTool) {
					level.isAboutToAlterState();
					clipBoard.deleteSelectedImage();
				}
				if (toolSelector.getTool() instanceof SelectDummyTool) {
					level.isAboutToAlterState();
					level.getDummyObjects().deleteSelectedDummy();
				}
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


			if (event.getSource() == menu.editDummyCustomDataItem) {
				String newCustomData = JOptionPane.showInputDialog("Custom data for object " 
						+ level.getDummyObjects().getSelected().name, 
						level.getDummyObjects().getSelected().additionalData);
				if (newCustomData != null) {
					level.isAboutToAlterState();
					level.getDummyObjects().getSelected().additionalData = newCustomData;
				}
			}
			
			if (event.getSource() == menu.zoomInItem) {
				mapView.zoom(2.0f);
			}
			if (event.getSource() == menu.zoomOutItem) {
				mapView.zoom(0.5f);
			}

			// tile layer

			if (event.getSource() == menu.addLayerItem) {
				addLayer();
			}
			if (event.getSource() == menu.deleteLayerItem) {
				deleteLayer();
			}
			
			if (event.getSource() == menu.resizeTilemapItem) {
				resizeTileMap();
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
							config.dummyDefinitions.toArray(new DummyObject[0]));
					mapView.onLevelLoaded();
					layerSelector.onLayersChanged(level.getTileMap());
				}
			}

			// MENU -> import mappy
			if (event.getSource() == menu.importMappyItem) {
				String path = getOpenLevelPath();
				if (path != null) {
					level.initFromFile(new MappyLevelFile(path),
							(DummyObject[])config.dummyDefinitions.toArray(new DummyObject[0]));
					mapView.onLevelLoaded();
					layerSelector.onLayersChanged(level.getTileMap());
				}
			}
			
			if (event.getSource() == menu.reloadConfigItem) {
				config = new Config(configFilePath, tileMapImagePath);	
				loadDummyAndTileDefinitions();
			}

			mapView.repaint();
		}
	}
	
	private void resizeTileMap() {
		ArrayList<PropertiesInputDialog.PropertyItem> propertyList = new ArrayList<PropertiesInputDialog.PropertyItem>();
		propertyList.add(new PropertiesInputDialog.PropertyItem("Right addition", "0"));
		propertyList.add(new PropertiesInputDialog.PropertyItem("Left addition", "0"));
		propertyList.add(new PropertiesInputDialog.PropertyItem("Top addition", "0"));
		propertyList.add(new PropertiesInputDialog.PropertyItem("Bottom addition", "0"));
		
		new PropertiesInputDialog(this, "Resize", propertyList);
		
		int right = 0;
		int left = 0;
		int top = 0;
		int bottom = 0;
		
		for (PropertiesInputDialog.PropertyItem property : propertyList) {
			int intValue = 0;
			try {
				intValue = Integer.parseInt(property.value);
			} catch (Exception e) {}
			
			if (property.name.equals("Right addition")) right = intValue;
			if (property.name.equals("Left addition")) left = intValue;
			if (property.name.equals("Top addition")) top = intValue;
			if (property.name.equals("Bottom addition")) bottom = intValue;
		}

		level.isAboutToAlterState();
		level.getTileMap().resize(left, right, top, bottom);
		mapView.zoom(1); // to repaint
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
			configFile = "Example_res/exampleConfig.xml";
		} else {
			configFile = args[0];
		}
		
		if (args.length >= 2) {
			System.out.println("Overriding config file tilemap image with " + args[1]);
			tileMapImage = args[1];
		}

		LevelEdit app = new LevelEdit(configFile, tileMapImage);
		app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	@Override
	public TileSelector getTileSelector() {
		return tileSelector;
	}

	@Override
	public DummyObjectFactory getDummyObjectFactory() {
		return dummyTypeSelector;
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

	@Override
	public Level getLevel() {
		return level;
	}

	@Override
	public int getSelectedLayer() {
		return mapView.getSelectedLayer();
	}

	@Override
	public ApplicationClipBoard getClipBoard() {
		return clipBoard;
	}

	public void addLayer() {
		level.isAboutToAlterState();
		level.getTileMap().addMap();
		layerSelector.onLayersChanged(level.getTileMap());
		mapView.zoom(1);
	}
	
	public void deleteLayer() {
		level.isAboutToAlterState();
		level.getTileMap().deleteMap(mapView.getSelectedLayer());
		layerSelector.onLayersChanged(level.getTileMap());
		mapView.zoom(1);
	}

	@Override
	public void onMoveLayerUpButton() {
		level.getTileMap().moveLayerUp(layerSelector.getSelectedIndex());	
		layerSelector.onLayersChanged(level.getTileMap());
		mapView.zoom(1);
	}

	@Override
	public void onMoveLayerDownButton() {
		level.getTileMap().moveLayerDown(layerSelector.getSelectedIndex());
		layerSelector.onLayersChanged(level.getTileMap());
		mapView.zoom(1);
	}

	@Override
	public void onToggleLayerVisibleButton() {
		level.getTileMap().toggleHidden(layerSelector.getSelectedIndex());
		layerSelector.onLayersChanged(level.getTileMap());
		mapView.zoom(1);
	}

	@Override
	public void onSelectedLayerChanged() {
		mapView.selectLayer(layerSelector.getSelectedIndex());
		mapView.zoom(1);
	}
}