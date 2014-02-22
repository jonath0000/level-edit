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
import levelfileformats.Blocko2LevelFile;
import levelfileformats.CommaSeparatedTileMapLevelFile;
import levelfileformats.InternalLevelFile;
import levelfileformats.MappyLevelFile;
import levelfileformats.XmlObjectListLevelFile;
import levelmodel.Level;

/**
 * The GUI class of the LevelEdit application. Sets up all the buttons, menus
 * and stuff in the main window.
 * 
 */
public class LevelEdit extends JFrame {

	private static final String HELP_TEXT = 
			  "Quick help:                              \n"
			+ "                                         \n"
			+ "Before using you should set up a config  \n"
			+ "file specifying your project. See the    \n"
			+ "stand-alone documentation for help on    \n"
			+ "this.                                    \n"
			+ "                                         \n"
			+ "A,W,S,D - move camera                    \n"
			+ "                                         \n"
			+ "Dummy objects are game objects such as   \n"
			+ "characters, pickups, trigger areas etc.  \n"
			+ "Select from the list on screen and use   \n"
			+ "the tools to create and move these.      \n"
			+ "                                         \n"
			+ "Use the tile tools to edit the tile map. \n"
			+ "The tile map can include several layers. \n" +

			"";

	private JPanel panel;
	private ToolSelector toolSelector;
	private TileSelector tileSelector;
	private DummyTypeSelector dummyTypeSelector;
	private Menu menu;
	private Config config;
	private MapView mapView;
	private Level level = new Level();
	private File currentFile = null;

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

			JFileChooser fc = new JFileChooser(currentFile);
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

		JFileChooser fc = new JFileChooser(currentFile);
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
				level.getDummyObjects().moveSelectedDummy(0, -Config.tileSize);
			} else if (event.getSource() == menu.moveDownItem) {
				level.isAboutToAlterState();
				level.getDummyObjects().moveSelectedDummy(0, Config.tileSize);
			} else if (event.getSource() == menu.moveLeftItem) {
				level.isAboutToAlterState();
				level.getDummyObjects().moveSelectedDummy(-Config.tileSize, 0);
			} else if (event.getSource() == menu.moveRightItem) {
				level.isAboutToAlterState();
				level.getDummyObjects().moveSelectedDummy(Config.tileSize, 0);
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
				mapView.scrollY(-Config.tileSize);
			}
			if (event.getSource() == menu.scrollDownItem) {
				mapView.scrollY(Config.tileSize);
			}
			if (event.getSource() == menu.scrollLeftItem) {
				mapView.scrollX(-Config.tileSize);
			}
			if (event.getSource() == menu.scrollRightItem) {
				mapView.scrollX(Config.tileSize);
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

			mapView.repaint();
		}
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

		panel = new JPanel();
		panel.setLayout(new GridLayout(1, 3));

		toolSelector = new ToolSelector();
		panel.add(toolSelector);

		dummyTypeSelector = new DummyTypeSelector();
		panel.add(dummyTypeSelector);

		tileSelector = new TileSelector();
		panel.add(tileSelector);

		container.add(panel, BorderLayout.SOUTH);

		// init main window
		setSize(Toolkit.getDefaultToolkit().getScreenSize().width - 40, Toolkit
				.getDefaultToolkit().getScreenSize().height - 60);
		setLocation(20, 20);
		setVisible(true);
		setResizable(true);

	}

	/**
	 * Setup app.
	 * 
	 * @param configFile Config file path.
	 */
	public LevelEdit(String configFile) {
		super("LevelEdit");

		config = new Config(configFile);
		
		currentFile = new File(config.projectPath);

		Container container = getContentPane();
		createGui(container);

		tileSelector.setTiles(config.tiles, Config.numTiles, Config.tileSize,
				Config.tileSize);

		dummyTypeSelector.setDummyList(config.typeNames, config.typeData);

		// mapview
		mapView = new MapView(config, toolSelector, tileSelector,
				dummyTypeSelector, level);
		mapView.addMouseListener(mapView);
		mapView.addMouseMotionListener(mapView);
		container.add(mapView, BorderLayout.CENTER);
		mapView.revalidate();
		mapView.repaint();
	}

	/**
	 * App main entry.
	 * 
	 * @param args arg 0 path to a config file.
	 */
	public static void main(String[] args) {
		String configFile;
		if (args.length < 1) {
			System.out.println("No config specified, using default.");
			configFile = "Example_res/example.config";
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