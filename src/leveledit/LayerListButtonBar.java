package leveledit;

import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

public class LayerListButtonBar extends JPanel {

	private ButtonHandler buttonHandler = new ButtonHandler();

	public interface LayerListButtonBarListener {
		public void onMoveLayerUpButton();
		public void onMoveLayerDownButton();
		public void onToggleLayerVisibleButton();
	}

	private class ButtonHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent event) {
			((ActionButton) event.getSource()).action.run();
		}
	}

	private class ActionButton extends JButton {
		public Runnable action;

		public ActionButton(ImageIcon image, Runnable action) {
			super(image);
			this.action = action;
		}
	}

	private void createButton(String imagePath, Runnable action) {
		Image image = Toolkit.getDefaultToolkit().getImage(getClass().getResource(
				imagePath)).getScaledInstance(24, 24, Image.SCALE_SMOOTH);
		ActionButton button = new ActionButton(new ImageIcon(image), action);
		button.addActionListener(buttonHandler);
		add(button);
	}

	public LayerListButtonBar(final LayerListButtonBarListener listener) {
		super();
		createButton("res/buttonToggleLayerVisible.png", new Runnable() {
			@Override
			public void run() {
				listener.onToggleLayerVisibleButton();
			}
		});
		createButton("res/buttonMoveLayerDown.png", new Runnable() {
			@Override
			public void run() {
				listener.onMoveLayerDownButton();
			}
		});
		createButton("res/buttonMoveLayerUp.png", new Runnable() {
			@Override
			public void run() {
				listener.onMoveLayerUpButton();
			}
		});
		setLayout(new GridLayout(1, 3));
	}
}
