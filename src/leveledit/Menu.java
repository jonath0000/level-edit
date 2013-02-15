package leveledit;

import java.awt.event.ActionListener;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

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
    protected JMenuItem helpItem;
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
    
    private void addMenuItem(JMenuItem item,
            JMenu menu, ActionListener handler, String text) {
        item = new JMenuItem (text);
	item.addActionListener(handler);
	menu.add(item);
    }
    
    public Menu(ActionListener handler) {
        super();
        
        JMenu fileMenu = new JMenu("File");
        JMenu dummyMenu = new JMenu("Dummy");
	JMenu helpMenu = new JMenu("Help");       

        addMenuItem(newMapItem, fileMenu, handler, "New map");
        addMenuItem(openItem, fileMenu, handler, "Open");
        addMenuItem(importMappyItem, fileMenu, handler, "Import Mappy file");
        addMenuItem(saveItem, fileMenu, handler, "Save");
        addMenuItem(saveAsItem, fileMenu, handler, "Save as...");
        addMenuItem(exportAsBlockoFormatItem, fileMenu, handler, "Export to Blocko format");
        addMenuItem(exportAsBlockoFormat2Item, fileMenu, handler, "Export to Blocko format 2");

        addMenuItem(helpItem, helpMenu, handler, "Help");

        addMenuItem(moveLeftItem, dummyMenu, handler, "Move left");
        addMenuItem(moveRightItem, dummyMenu, handler, "Move right");
        addMenuItem(moveUpItem, dummyMenu, handler, "Move up");
        addMenuItem(moveDownItem, dummyMenu, handler, "Move down");
        
        addMenuItem(nudgeLeftItem, dummyMenu, handler, "Nudge left");
        addMenuItem(nudgeRightItem, dummyMenu, handler, "Nudge right");
        addMenuItem(nudgeUpItem, dummyMenu, handler, "Nudge up");
        addMenuItem(nudgeDownItem, dummyMenu, handler, "Nudge down");
        
        addMenuItem(selectNextItem, dummyMenu, handler, "Select next");
        addMenuItem(selectPrevItem, dummyMenu, handler, "Select prev");
        
	add(fileMenu);
        add(dummyMenu);
	add(helpMenu);
    }
    
}
