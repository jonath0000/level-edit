package leveledit;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Panel to select editing tool.
 * 
 */
public class ToolSelector extends JPanel {
    
    public enum Tool {
        SET_TILE,
        DELETE_TILE,
        FILL_TILE,
        LINE_TILE,
        NEW_DUMMY,
        SELECT_DUMMY,
        DELETE_DUMMY,
    }
    
    private Tool tool;
    
    // tool choose btns
    private JButton toolNewDummy;
    private JButton toolSelectDummy;
    private JButton toolSetTile;
    
    public ToolSelector() {
        
        super();
        
        tool = Tool.SET_TILE;

	toolNewDummy = new JButton(new ImageIcon("res/toolNewDummy.png"));
	toolSelectDummy = new JButton(new ImageIcon("res/toolSelectDummy.png"));
	toolSetTile = new JButton(new ImageIcon("res/toolSetTile.png"));
        
        toolNewDummy.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                tool = Tool.NEW_DUMMY;
                selectToolButton(toolNewDummy);
            }
        });
        toolSelectDummy.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                tool = Tool.SELECT_DUMMY;
                selectToolButton(toolSelectDummy);
            }
        });
        toolSetTile.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                tool = Tool.SET_TILE;
                selectToolButton(toolSetTile);
            }
        });

	setLayout(new GridLayout(3,1));
	add(toolNewDummy);
	add(toolSelectDummy);
	add(toolSetTile);
	ButtonGroup toolGroup = new ButtonGroup();
	toolGroup.add(toolNewDummy);
	toolGroup.add(toolSelectDummy);
	toolGroup.add(toolSetTile);
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

    
    public Tool getTool() {
        return tool;
    }
}
