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
    protected JMenuItem quitItem;
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
    protected JMenuItem scrollLeftItem;
    protected JMenuItem scrollRightItem;
    protected JMenuItem scrollUpItem;
    protected JMenuItem scrollDownItem;
    protected JMenuItem addLayerItem;
    protected JMenuItem deleteLayerItem;
    protected JMenuItem nextLayerItem;
    protected JMenuItem prevLayerItem;
   
    private JMenuItem addMenuItem(
            JMenu menu, ActionListener handler, 
            String text) {
        JMenuItem item = new JMenuItem(text);
	item.addActionListener(handler);
        menu.add(item);      
        return item;
    }
    
    private JMenuItem addMenuItem(
            JMenu menu, ActionListener handler, 
            String text, int keyCode, int keyMask) {
        JMenuItem item = new JMenuItem(text);
	item.addActionListener(handler);
	item.setAccelerator(KeyStroke.getKeyStroke(
            keyCode, keyMask));    
        menu.add(item);
        return item;
    }
   
    
    public Menu(ActionListener handler) {
        super();
        
        JMenu fileMenu = new JMenu("File");
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
        quitItem = addMenuItem(fileMenu, handler, "Quit");

        helpItem = addMenuItem(helpMenu, handler, "Help");

        moveLeftItem = addMenuItem(dummyMenu, handler, "Move left", KeyEvent.VK_LEFT, 0);
        moveRightItem = addMenuItem(dummyMenu, handler, "Move right", KeyEvent.VK_RIGHT, 0);
        moveUpItem = addMenuItem(dummyMenu, handler, "Move up", KeyEvent.VK_UP, 0);
        moveDownItem = addMenuItem(dummyMenu, handler, "Move down", KeyEvent.VK_DOWN, 0);
        
        nudgeLeftItem = addMenuItem(dummyMenu, handler, "Nudge left", KeyEvent.VK_LEFT, ActionEvent.ALT_MASK);
        nudgeRightItem = addMenuItem(dummyMenu, handler, "Nudge right", KeyEvent.VK_RIGHT, ActionEvent.ALT_MASK);
        nudgeUpItem = addMenuItem(dummyMenu, handler, "Nudge up", KeyEvent.VK_UP, ActionEvent.ALT_MASK);
        nudgeDownItem = addMenuItem(dummyMenu, handler, "Nudge down", KeyEvent.VK_DOWN, ActionEvent.ALT_MASK);
        
        selectNextItem = addMenuItem(dummyMenu, handler, "Select next", KeyEvent.VK_Q, 0);
        selectPrevItem = addMenuItem(dummyMenu, handler, "Select prev", KeyEvent.VK_E, 0);
        
        scrollLeftItem = addMenuItem(levelMenu, handler, "Scroll left", KeyEvent.VK_A, 0);
        scrollRightItem = addMenuItem(levelMenu, handler, "Scroll right", KeyEvent.VK_D, 0);
        scrollUpItem = addMenuItem(levelMenu, handler, "Scroll up", KeyEvent.VK_W, 0);
        scrollDownItem = addMenuItem(levelMenu, handler, "Scroll down", KeyEvent.VK_S, 0);
        
        addLayerItem = addMenuItem(tileMenu, handler, "Add layer");
        deleteLayerItem = addMenuItem(tileMenu, handler, "Delete layer");
        nextLayerItem = addMenuItem(tileMenu, handler, "Select next layer", KeyEvent.VK_Q, ActionEvent.ALT_MASK);
        prevLayerItem = addMenuItem(tileMenu, handler, "Select previous layer", KeyEvent.VK_E, ActionEvent.ALT_MASK);
        
	add(fileMenu);
        add(levelMenu);
        add(dummyMenu);
        add(tileMenu);
	add(helpMenu);
    }
    
}
