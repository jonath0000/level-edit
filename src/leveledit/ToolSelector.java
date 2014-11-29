package leveledit;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import tools.DeleteDummyTool;
import tools.DeleteTileTool;
import tools.FillTileTool;
import tools.LineTileTool;
import tools.NewDummyTool;
import tools.PickupTileTool;
import tools.SelectDummyTool;
import tools.SetTileTool;
import tools.Tool;

/**
 * Panel to select editing tool.
 * 
 */
public class ToolSelector extends JToolBar {
    
	/*
    public enum Tool {
        SET_TILE,
        DELETE_TILE,
        FILL_TILE,
        PICKUP_TILE,
        LINE_TILE,
        NEW_DUMMY,
        SELECT_DUMMY,
        DELETE_DUMMY,
    }*/
    
    private Tool tool;

    private class ToolButton extends JButton {
        public Tool tool;
        public ToolButton(ImageIcon image, Tool tool) {
            super(image);
            this.tool = tool;
        }
    }
        
    private class ButtonHandler implements ActionListener {
        
        @Override
	public void actionPerformed (ActionEvent event) {
            
            tool = ((ToolButton)event.getSource()).tool;

        }
    }
    
    private ButtonHandler buttonHandler = new ButtonHandler();
    
    private void createButton(Tool tool, String image) {
        ToolButton button = new ToolButton(new ImageIcon(image), tool);
        button.addActionListener(buttonHandler);
        add(button);
    }
    
    public ToolSelector() {
        
        super();
        
        tool = new NewDummyTool();

        createButton(new NewDummyTool(),    "res/toolNewDummy.png");
        createButton(new SelectDummyTool(), "res/toolSelectDummy.png");
        createButton(new DeleteDummyTool(), "res/toolDeleteDummy.png");
        createButton(new SetTileTool(),     "res/toolSetTile.png");
        createButton(new DeleteTileTool(),  "res/toolDeleteTile.png");
        createButton(new FillTileTool(),    "res/toolFillTile.png");
        createButton(new PickupTileTool(),  "res/toolPickupTile.png");
        createButton(new LineTileTool(),    "res/toolLineTile.png");
        
        setLayout(new GridLayout(3,3));
    }

    
    public Tool getTool() {
        return tool;
    }
}
