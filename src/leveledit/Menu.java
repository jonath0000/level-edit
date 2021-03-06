package leveledit;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

/**
 * The application menu bar.
 * 
 */
public class Menu extends JMenuBar {

	protected JMenuItem newMapItem;
	protected JMenuItem saveItem;
	protected JMenuItem saveAsItem;
	protected JMenuItem openItem;
	protected JMenuItem importMappyItem;
	protected JMenuItem exportAsBlockoFormatItem;
	protected JMenuItem exportAsBlockoFormat2Item;
	protected JMenuItem exportAsCommaSeparatedItem;
	protected JMenuItem exportAsXmlObjectListItem;
	protected JMenuItem exportAsXmlObjectListAndCommaSeparatedItem;
	protected JMenuItem reloadConfigItem;
	protected JMenuItem quitItem;
	protected JMenuItem helpItem;
	protected JMenuItem undoItem;
	protected JMenuItem copyItem;
	protected JMenuItem cutItem;
	protected JMenuItem pasteItem;
	protected JMenuItem deleteItem;
	protected JMenuItem moveLeftItem;
	protected JMenuItem moveRightItem;
	protected JMenuItem moveUpItem;
	protected JMenuItem moveDownItem;
	protected JMenuItem nudgeLeftItem;
	protected JMenuItem nudgeRightItem;
	protected JMenuItem nudgeUpItem;
	protected JMenuItem nudgeDownItem;
	protected JMenuItem selectNextItem;
	protected JMenuItem selectPrevItem;
	protected JMenuItem editDummyCustomDataItem;
	protected JMenuItem zoomInItem;
	protected JMenuItem zoomOutItem;
	protected JMenuItem addLayerItem;
	protected JMenuItem deleteLayerItem;
	protected JMenuItem resizeTilemapItem;
	protected JMenuItem showTileValueItem;

	private JMenuItem addMenuItem(JMenu menu, ActionListener handler, String text) {
		JMenuItem item = new JMenuItem(text);
		item.addActionListener(handler);
		menu.add(item);
		return item;
	}

	private JMenuItem addMenuItem(JMenu menu, ActionListener handler, String text, int keyCode, int keyMask) {
		JMenuItem item = new JMenuItem(text);
		item.addActionListener(handler);
		item.setAccelerator(KeyStroke.getKeyStroke(keyCode, keyMask));
		menu.add(item);
		return item;
	}

	public Menu(ActionListener handler) {
		super();

		JMenu fileMenu = new JMenu("File");
		JMenu editMenu = new JMenu("Edit");
		JMenu levelMenu = new JMenu("Level");
		JMenu dummyMenu = new JMenu("Dummy");
		JMenu tileMenu = new JMenu("Tilemap");
		JMenu helpMenu = new JMenu("Help");

		newMapItem = addMenuItem(fileMenu, handler, "New", KeyEvent.VK_N, ActionEvent.CTRL_MASK);
		openItem = addMenuItem(fileMenu, handler, "Open", KeyEvent.VK_O, ActionEvent.CTRL_MASK);
		importMappyItem = addMenuItem(fileMenu, handler, "Import Mappy file");
		saveItem = addMenuItem(fileMenu, handler, "Save", KeyEvent.VK_S, ActionEvent.CTRL_MASK);
		saveAsItem = addMenuItem(fileMenu, handler, "Save as...");
		exportAsBlockoFormatItem = addMenuItem(fileMenu, handler, "Export to Blocko format");
		exportAsBlockoFormat2Item = addMenuItem(fileMenu, handler, "Export to Blocko format 2");
		exportAsCommaSeparatedItem = addMenuItem(fileMenu, handler, "Export as comma separated");
		exportAsXmlObjectListItem = addMenuItem(fileMenu, handler, "Export as XML object list");
		exportAsXmlObjectListAndCommaSeparatedItem = addMenuItem(fileMenu, handler, "Export as XML object list and comma separated");
		reloadConfigItem = addMenuItem(fileMenu, handler, "Reload config");
		quitItem = addMenuItem(fileMenu, handler, "Quit");

		helpItem = addMenuItem(helpMenu, handler, "Help");

		undoItem = addMenuItem(editMenu, handler, "Undo", KeyEvent.VK_Z, ActionEvent.CTRL_MASK);
		copyItem = addMenuItem(editMenu, handler, "Copy", KeyEvent.VK_C, ActionEvent.CTRL_MASK);
		cutItem = addMenuItem(editMenu, handler, "Cut", KeyEvent.VK_X, ActionEvent.CTRL_MASK);
		pasteItem = addMenuItem(editMenu, handler, "Paste", KeyEvent.VK_V, ActionEvent.CTRL_MASK);
		deleteItem = addMenuItem(editMenu, handler, "Delete", KeyEvent.VK_DELETE, 0);
		
		moveLeftItem = addMenuItem(dummyMenu, handler, "Move left", KeyEvent.VK_J, 0);
		moveRightItem = addMenuItem(dummyMenu, handler, "Move right", KeyEvent.VK_L, 0);
		moveUpItem = addMenuItem(dummyMenu, handler, "Move up", KeyEvent.VK_I, 0);
		moveDownItem = addMenuItem(dummyMenu, handler, "Move down", KeyEvent.VK_K, 0);

		nudgeLeftItem = addMenuItem(dummyMenu, handler, "Nudge left", KeyEvent.VK_J, ActionEvent.ALT_MASK);
		nudgeRightItem = addMenuItem(dummyMenu, handler, "Nudge right", KeyEvent.VK_L, ActionEvent.ALT_MASK);
		nudgeUpItem = addMenuItem(dummyMenu, handler, "Nudge up", KeyEvent.VK_I, ActionEvent.ALT_MASK);
		nudgeDownItem = addMenuItem(dummyMenu, handler, "Nudge down", KeyEvent.VK_K, ActionEvent.ALT_MASK);

		selectNextItem = addMenuItem(dummyMenu, handler, "Select next", KeyEvent.VK_Q, 0);
		selectPrevItem = addMenuItem(dummyMenu, handler, "Select prev", KeyEvent.VK_E, 0);

		editDummyCustomDataItem = addMenuItem(dummyMenu, handler, "Edit custom data", KeyEvent.VK_E, ActionEvent.CTRL_MASK);

		zoomInItem = addMenuItem(levelMenu, handler, "Zoom in", KeyEvent.VK_PERIOD, 0);
		zoomOutItem = addMenuItem(levelMenu, handler, "Zoom out", KeyEvent.VK_COMMA, 0);
		
		addLayerItem = addMenuItem(tileMenu, handler, "Add layer");
		deleteLayerItem = addMenuItem(tileMenu, handler, "Delete layer");

		resizeTilemapItem = addMenuItem(tileMenu, handler, "Resize...");

		
		showTileValueItem = addMenuItem(tileMenu, handler, "Show selected tiles index", KeyEvent.VK_T, ActionEvent.CTRL_MASK);
		
		add(fileMenu);
		add(editMenu);
		add(levelMenu);
		add(dummyMenu);
		add(tileMenu);
		add(helpMenu);
	}

}
